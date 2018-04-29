import json
from threading import Thread, Lock, RLock
from time import sleep
from typing import Tuple, List, TYPE_CHECKING, Dict, Set
from functools import wraps
import bisect

import eventlet
eventlet.monkey_patch()

from .LiftState import LiftState

if TYPE_CHECKING:
    from .LiftController import LiftController
    from .Job import Job


class Lift:
    FLOOR_STEP_TIME = 1
    _class_lock: Lock = Lock()
    lift_objects: 'List[Lift]' = []

    def _emit(self, *args, **kwargs)->None:
        """
        广播消息
        :param args:
        :param kwargs:
        """
        self._controller.emit(*args, **kwargs)

    @staticmethod
    def choose_best(init_job: 'Job')->bool:
        """
        :param init_job: 初始化的任务
        :return: 挑选是否成功
        """
        with Lift._class_lock:
            # 整个方法是类级别上锁的
            for l in Lift.lift_objects:
                l._state_lock.acquire()

            # 选出同向或者静止的
            available = [l for l in Lift.lift_objects if l._state == LiftState.makestate(l._floor, init_job.beg) or l._state == LiftState.REST]
            if len(available) == 0:
                # 没有找到对应的对象, 则释放所有的锁然后返回FALSE
                for l in Lift.lift_objects:
                    l._state_lock.release()
                return False
            min_lift, min_dist = None, 20
            # 选出对应的电梯
            for c_lift in Lift.lift_objects:
                # 此处已经上锁了, 不用进行额外的同步操作

                if abs(c_lift._floor - init_job.beg) < min_dist:
                    if abs(c_lift._floor - init_job.beg) != 0 and c_lift._state != LiftState.REST:
                        continue
                    # 最小，但是不存在与统一楼成的时候
                    min_dist = abs(c_lift._floor - init_job.beg)
                    min_lift = c_lift
            if min_dist == 20:
                # 没有找到符合要求的目标
                for l in Lift.lift_objects:
                    l._state_lock.release()
                return False
            elif min_dist == 0 and init_job.to is not None:
                # 在同一个楼层, 显然可以直接接走
                # 不过需要另一个存在
                min_lift.add_inner_job(init_job.to, True)
                min_lift._floor_arrived_under_lock()
            else:
                min_lift.add_job(init_job)

            # release all locks
            for l in Lift.lift_objects:
                l._state_lock.release()
            return True

    @property
    def state(self)->LiftState:
        with self._state_lock:
            return self._state

    def _check_reversed_jobs_under_locks(self)->bool:
        """
        检查
        :return: 是否存在reversed job
        """

        floor_r_tasks: List[Job] = self._reversed_jobs[self._floor]
        for floor_n in floor_r_tasks:
            if floor_n.to is not None:
                self._controller.add_job(from_floor=floor_n.beg, to_floor=floor_n.to)
            else:
                self._controller.add_outer_job(from_floor=floor_n.beg, drc=floor_n.dct)
        task_num = len(floor_r_tasks)
        floor_r_tasks.clear()
        return task_num != 0

    def __init__(self, lnum: int, controller: 'LiftController'=None):
        with self._class_lock:
            self.lift_objects.append(self)
        # 是一个常量！
        self.LNUM = lnum
        # 反向工作，可能被调度的对象反向行走. 形式为楼层->列表的映射
        self._reversed_jobs: Dict[int, List[Job]] = {i: list() for i in range(1, 21)}
        # 内部需要到达的工作
        self._inner_jobs: Set[int] = set()

        self._state_lock = RLock()
        # 初始化为静止的状态
        self._state = LiftState.REST
        # 父控制者
        self._controller = controller
        # 对应的楼层
        self._floor = 1

        # 需要执行的任务
        self._farest: int = None
        self._task = None
        self._task_lock: RLock = RLock()

    def get_states(self):
        """
        :return: 获得自身所在楼层数
        """
        with self._state_lock:
            return {
                'lift_number': self.LNUM,
                'status': self._state,
                'floor': self._floor,
            }

    def __str__(self):
        return 'Lift({})'.format(self.LNUM)

    def __job_direc(self, init_job: 'Job')->LiftState:
        """
        :param init_job: 初始化整个项目的工作
        :return: 电梯应该被切入的状态
        """
        with self._state_lock:
            return LiftState.UP if init_job.beg - self._floor > 0 else LiftState.DOWN

    def add_inner_jobs_under_lock(self, new_jobs: 'List[Job]'):
        """
        在目前已有的代码之下
        :param new_jobs:
        :return:
        """
        for job in new_jobs:
            self.add_inner_job(job.to)

    def add_job(self, new_job: 'Job'):
        """
        :param new_job: 给电梯添加一个新的 从XX到XX的工作
        :return:
        """
        print(f"!!!!add job: {new_job}!!!!!")
        with self._state_lock:

            if self._state == LiftState.REST:
                # 静止则开始启动
                to_direc = self.__job_direc(new_job)
                if to_direc == LiftState.UP:
                    self._begin_to_go_up(new_job)
                else:
                    self._begin_to_go_down(new_job)
                if to_direc != new_job.direc:
                    # self._reversed_jobs
                    print(f'task: {new_job.beg} -> {new_job.to}')
                    self._reversed_jobs[new_job.beg].append(new_job)

            else:
                if abs(self._farest - self._floor) < abs(new_job.beg - self._floor):
                    self._farest = new_job.beg

    def _boot_with_to(self, to: int):
        """
        将程序根据TO来更新
        :param to: 需要前往的楼层
        :return:
        """
        with self._state_lock:
            if self._state != LiftState.REST:
                raise ValueError(f"LiftState must be REST, but get {self._state.value}")
            to_direc = LiftState.UP if to > self._floor else LiftState.DOWN
            step = int((to - self._floor) / abs(to - self._floor))
            self._start_task(step, to_direc, to=to)

    def status(self):
        """
        :return: json str of status.
        """
        with self._state_lock:
            return json.dumps({
                'lift_number': self.LNUM,
                'status': self._state.value,
                'floor': self._floor,
            })

    def report_status_change(self):
        """
        :return: 报告电梯的状态变化
        """
        self._emit('lift change', self.status())

    def floor_change_method(self, change_func):
        @wraps(change_func)
        def _floor_change(*args, **kwargs):
            ret = change_func(*args, **kwargs)

            return ret
        return _floor_change

    def status_change_method(self, change_func):
        @wraps(change_func)
        def _status_change(*args, **kwargs):
            ret = change_func(*args, **kwargs)
            self.report_status_change()
            return ret
        return _status_change

    def allow_job(self, job: 'Job')->bool:
        raise NotImplemented()

    def _floor_arrived_under_lock(self):
        """
        guard by _state_lock
        :return:
        """
        self._controller.arrived(lift=self, floor_n=self._floor)

    def _running_task(self, step: int):
        # TODO: fill the task with stop and aim
        if step not in {1, -1}:
            raise ValueError(f"step in Lift._running_task must be -1 or 1, but got {step}")
        # 注意BEG TO
        self._controller.emit('lift status change', {
            'lift_number': self.LNUM,
            "status": "up" if step == 1 else "down"
        })

        while self.get_states()['floor'] != self._farest:

            # 上楼时间
            sleep(Lift.FLOOR_STEP_TIME)
            need_sleep = False
            with self._state_lock:

                self._floor += step
                if self._floor in self._inner_jobs:
                    need_sleep = True
                    self._inner_jobs.remove(self._floor)
                    self._report_inner_job_change()
                if self._floor != self._farest:

                    need_sleep = need_sleep or self._check_reversed_jobs_under_locks()
                self._floor_arrived_under_lock()
                self.report_status_change()
            if need_sleep and self._floor != self._farest:
                # 休眠的美妙停站时间
                print(f"Lift {self._floor} sleep for 0.5s")
                sleep(Lift.FLOOR_STEP_TIME)
        # 停止工作，FINALLY
        with self._state_lock:
            self._state = LiftState.REST

            if len(self._inner_jobs) != 0:
                self._inner_jobs.clear() # 对INNERJOBS 操作引发
                self._report_inner_job_change()

            self._farest = None
            # 传递信息
            self._emit('lift status change', {
                'lift_number': self.LNUM,
                "status": "rest"
            })
            end_floor_list = self._reversed_jobs[self._floor]
            for job in end_floor_list:
                if job.to is not None:
                    self.add_inner_job(to=job.to)
            self._floor_arrived_under_lock()
            end_floor_list.clear()

    def _start_task(self, step: int, state: 'LiftState', job: 'Job'=None, to: int=None):
        with self._task_lock:
            if self._state != LiftState.REST:
                raise RuntimeError(f"state in {str(self)} is not {LiftState.REST} when calling go_up")
            else:
                self._state = state
                if job is not None:
                    self._farest = job.beg
                else:
                    self._farest = to
                task_thread = Thread(target=self._running_task, args=(step,))
                task_thread.start()

    def _begin_to_go_up(self, farest: 'Job'):
        """
        从静止开始向上运行

        :param farest: 初始化的Job
        :return:
        """
        self._start_task(1, LiftState.UP, job=farest)

    def _begin_to_go_down(self, farest: 'Job'):
        self._start_task(-1, LiftState.DOWN, job=farest)

    def _report_inner_job_change(self):
        """
        报告电梯的内部工作增减
        :return: None
        """
        with self._state_lock:
            self._emit("lift innertask", {
                "lift number": self.LNUM,
                "tasks": list(self._inner_jobs)
            })

    def add_inner_job(self, to: int, must_added: bool=False)->bool:
        """
        添加电梯内部的指向型工作，通常在 1.接收有目的的客人 2. 内部按键 之后
        :param to:
        :param must_added:
        :return:
        """
        with self._state_lock:
            if to == self._floor:
                # 成功接收，但什么都没有发生
                return True
            direc = LiftState.UP if to > self._floor else LiftState.DOWN
            need_to_add = True
            if direc == LiftState.UP and to < self._floor:
                need_to_add = False
            elif direc == LiftState.DOWN and to > self._floor:
                need_to_add = False
            if self._state == LiftState.REST:
                # 需要重新被启动
                self._boot_with_to(to)
                self._inner_jobs.add(to)
                self._report_inner_job_change()
            elif need_to_add or must_added:
                if abs(self._farest - self._floor) < abs(to - self._floor):
                    self._farest = to
                # 插入内部工作
                self._inner_jobs.add(to)
                self._report_inner_job_change()


if __name__ == '__main__':
    lift = Lift(3)
    print(lift.status())
