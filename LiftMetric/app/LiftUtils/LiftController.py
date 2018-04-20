"""
电梯的最终控制器
"""
import json
from queue import Queue
from threading import Thread, Lock
import time
import random
from typing import Tuple, List, TYPE_CHECKING

from .Job import Job
from .Lift import Lift
from .Floor import Floor

if TYPE_CHECKING:
    from threading import RLock
    from .LiftState import LiftState


class LiftController:
    def __init__(self, socketio=None):
        self._socket = socketio
        self._lifts: Tuple[Lift] = tuple(Lift(i, self) for i in range(1, 6))
        self._floors: Tuple[Floor] = tuple(Floor(i, self) for i in range(1, 21))
        # 剩余工作的队列，对于到来的工作用FIFO
        self._remained_jobs: Queue = Queue()
        # 开启任务
        self._start_deamon()
        self._emit_lock = Lock()

    def _start_deamon(self):
        """
        开启一个处理JOB的守护线程
        """
        def task_start():
            while True:
                cur_job = self._remained_jobs.get(block=True, timeout=100000)
                if not self._dispatch_job(cur_job):
                    # 没有成功添加
                    self._remained_jobs.put(cur_job)
        t = Thread(target=task_start)
        t.daemon = True
        t.start()

    def get_all_status(self):
        """
        :return: 获得所有电梯的状态
        """
        return json.dumps([self._lifts[i].status() for i in range(5)])

    def emit(self, *args, **kwargs):
        """
        发送消息
        """
        with self._emit_lock:
            print(*args, **kwargs)
            return self._socket.emit(*args, **kwargs, namespace='/lifts', broadcast=True)

    @staticmethod
    def _dispatch_job(job: Job):
        """
        选出最佳的电梯组合
        :yield: the one available elevator
        :return: whether the job was well dispatched.
        """
        return Lift.choose_best(job)

    def add_job(self, from_floor: int, to_floor: int):
        """
        :param from_floor:
        :param to_floor:
        :return: 向任务添加新的任务
        """
        added = Job(from_floor, to_floor)
        # 生成一个Job 放入阻塞队列
        self._remained_jobs.put(added)
        floor_i: Floor = self._floors[from_floor - 1]
        floor_i.add_task(added)

    def add_outer_job(self, from_floor: int, drc: 'LiftState'):
        """
        相当于人在from_floor 按电梯
        这个实际上也是有方向的
        :param from_floor:
        :param drc: outer job 对应的反向
        """
        added = Job(from_floor, direction=drc)
        self._remained_jobs.put(added)
        floor_i: Floor = self._floors[from_floor - 1]
        floor_i.add_task(added)

    def arrived(self, lift: Lift, floor_n: int):
        # TODO 研究是否能够修改这一套逻辑
        """
        已经在锁的保护下了
        电梯到达楼层，LIFT为指定楼层，floor_n 为楼层序号
        :param lift:
        :param floor_n:
        :return:
        """
        # 获得对应的 FLOOR
        floor: Floor = self._floors[floor_n - 1]
        task_l: List[Job] = floor.clear_and_out(lift.state)
        lift.add_inner_jobs_under_lock(task_l)

    def add_inner_job(self, lift_number: int, to: int):
        """
        :param lift_number: 电梯序号
        :param to: 要去的楼层
        :return:
        """
        # 映射到对应的电梯上
        print(f"add inner job: to {to}")
        cur_lift: Lift = self._lifts[lift_number - 1]
        cur_lift.add_inner_job(to)


if __name__ == '__main__':
    lc = LiftController()
    print(lc.get_all_status())
