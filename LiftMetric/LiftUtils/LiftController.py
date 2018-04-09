"""
电梯的最终控制器
"""
import json
from queue import Queue
from threading import Thread
import time
import random
from typing import Tuple

from LiftUtils.Job import Job
from LiftUtils.Lift import Lift


class LiftController:
    def __init__(self, socketio=None):

        self._socket = socketio
        self._lifts: Tuple[Lift] = tuple(Lift(i, self) for i in range(1, 6))
        # 剩余工作的队列，对于到来的工作用FIFO
        self._remained_jobs: Queue = Queue()
        # 开启任务
        self._start_deamon()

    def _start_deamon(self):
        """
        开启一个处理JOB的守护线程
        """
        def task_start():
            while True:
                cur_job = self._remained_jobs.get(block=True)
                if not self._dispatch_job(cur_job):
                    # 成功添加
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
        return self._socket.emit(*args, **kwargs, namespace='/lifts')

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


if __name__ == '__main__':
    lc = LiftController()
    print(lc.get_all_status())
