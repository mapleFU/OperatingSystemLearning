import json
from threading import Thread, Lock, RLock
from time import sleep
from typing import Tuple, List, TYPE_CHECKING

from LiftUtils.LiftState import LiftState

if TYPE_CHECKING:
    from LiftUtils.LiftController import LiftController
    from LiftUtils.Job import Job


class Lift:
    FLOOR_STEP_TIME = 1
    _class_lock: Lock = Lock()
    lift_objects: 'List[Job]' = []

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
            available = [avl for l in Lift.lift_objects and l._state == init_job.direc or l._state == LiftState.REST]
            if len(available) == 0:
                # 没有找到对应的对象
                return False
            min_lift, min_dist = None, 20
            for c_lift in Lift.lift_objects:
                if abs(c_lift - init_job.beg) < min_dist and abs(c_lift - init_job.beg) != 0:
                    # 最小，但是不存在与统一楼成的时候
                    min_dist = abs(c_lift - init_job.beg)
                    min_lift = c_lift
            min_lift.add_job(init_job)

            for l in Lift.lift_objects:
                l._state_lock.release()
            return True

    @property
    def state(self)->LiftState:
        with self._state_lock:
            return self._state

    def __init__(self, lnum: int, controller: 'LiftController'=None):
        with self._class_lock:
            self.lift_objects.append(self)
        # 是一个常量！
        self.LNUM = lnum

        self._state_lock = RLock()
        # 初始化为静止的状态
        self._state = LiftState.REST
        # 父控制者
        self._controller = controller
        # 对应的楼层
        self._floor = 1

        # 需要执行的任务
        self._farest: Job = None
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

    def add_job(self, new_job: 'Job'):
        """
        :param new_job: 给电梯添加一个新的 从XX到XX的工作
        :return:
        """
        with self._state_lock:
            if self._state == LiftState.REST:
                # 静止则开始启动
                if new_job.direc == LiftState.UP:
                    self._begin_to_go_up(new_job)
                else:
                    self._begin_to_go_down(new_job)
            else:
                if abs(self._farest.beg - self._floor) < abs(new_job - self._floor):
                    self._farest = new_job

    def status(self):
        """
        :return: str of status.
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
        self._controller.emit('lift change', self.status())

    def allow_job(self, job: 'Job')->bool:
        raise NotImplemented()

    def _running_task(self, step: int):
        if step not in {1, -1}:
            raise ValueError(f"step in Lift._running_task must be -1 or 1, but got {step}")
        # 注意BEG TO
        while self.get_states()['lift_number'] != self._farest.to:
            # 上楼时间
            sleep(Lift.FLOOR_STEP_TIME)
            with self._state_lock:
                self._floor += step
        # 停止工作，FINALLY
        with self._state_lock:
            self._state = LiftState.REST
            self._farest = None

    def _start_task(self, step: int, job: 'Job', state: 'LiftState'):
        with self._task_lock:
            if self._state != LiftState.REST:
                raise RuntimeError(f"state in {str(self)} is not {LiftState.REST} when calling go_up")
            else:
                self._state = state
                self._farest = job
                self._running_task(step)

    def _begin_to_go_up(self, farest: 'Job'):
        """
        从静止开始向上运行

        :param farest: 初始化的Job
        :return:
        """
        self._start_task(1, farest, LiftState.UP)

    def _begin_to_go_down(self, farest: 'Job'):
        self._start_task(-1, farest, LiftState.DOWN)


if __name__ == '__main__':
    lift = Lift(3)
    print(lift.status())
