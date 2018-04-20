from enum import Enum
from queue import Queue
from threading import RLock, Lock
from typing import List, TYPE_CHECKING
from collections import namedtuple
from copy import copy, deepcopy

import eventlet
eventlet.monkey_patch()

from .LiftState import LiftState
from .Job import Job

if TYPE_CHECKING:
    from .LiftController import LiftController
    from .Lift import Lift


class Floor:
    FloorChoice = namedtuple('FloorChoice', ['list', 'lock'])

    def _emit(self, *args, **kwargs)->None:
        """
        广播消息
        :param args:
        :param kwargs:
        """
        self._controler.emit(*args, **kwargs)

    def __init__(self, floor_n: int, controler: 'LiftController'):
        self.floor = floor_n
        self._controler = controler

        self._uplist: List[Job] = []
        self.__uplock: Lock = Lock()

        self._downlist: List[Job] = []
        self.__downlock: Lock = Lock()

        # 关于上下的映射，让我们能够执行同样的逻辑
        # wrong! is just a copy, we need proxy!
        self._mapper = {
            "up": Floor.FloorChoice(self._uplist, self.__uplock),
            "down": Floor.FloorChoice(self._downlist, self.__downlock)
        }

    @property
    def _up_task_num(self)->int:
        with self.__uplock:
            return len(self._uplist)

    @property
    def _down_task_num(self)->int:
        with self.__downlock:
            return len(self._downlist)

    def add_task(self, job: Job):
        if job.direc == LiftState.UP:
            self._add_up(job)
        else:
            self._add_down(job)

    def _add_up(self, job: Job):
        need_emit = False
        with self.__uplock:
            self._uplist.append(job)
            # 发送状态信息
            if len(self._uplist) == 1:
                need_emit = True
                print(f'uplist in {self.floor} -> {self._uplist}')
        if need_emit:
            self._emit("floor button", {
                "floor": self.floor,
                "key": "up",
                "light": True
            })

    def _add_down(self, job: Job):
        need_emit = False
        with self.__downlock:
            self._downlist.append(job)
            if len(self._downlist) == 1:
                need_emit = True
                print(f'downlist in {self.floor} -> {self._downlist}')
        if need_emit:
            self._emit("floor button", {
                "floor": self.floor,
                "key": "down",
                "light": True
            })

    def clear_and_out(self, liftstate: 'LiftState')->List[Job]:
        # TODO: make clear if locked
        """
        :param liftstate: 需要处理的电梯的状态
        :return: JOB的列表
        """
        print(f"lift state is {liftstate}")
        if liftstate == LiftState.UP:
            up_or_down = "up"
        elif liftstate == LiftState.DOWN:
            up_or_down = "down"
        else:
            if self._up_task_num == 0:
                up_or_down = "down"
            else:
                up_or_down = "up"
        if up_or_down == "up":
            cur_lock = self.__uplock
            cur_list = self._uplist
        else:
            cur_lock = self.__downlock
            cur_list = self._downlist
        # mapper = self._mapper[up_or_down]
        with cur_lock:
            # it should be a copy!!!!!!!!!!!!!!!
            m_list = copy(cur_list)
            cur_list.clear()
            length = len(m_list)
            if length != 0:
                self._emit("floor button", {
                    "floor": self.floor,
                    "key": up_or_down,
                    "light": False
                })
            # print(f'original m_list is {m_list}')
            for jobs in m_list:
                print(jobs)
            m_list = [job for job in m_list if job.to is not None]
            # print(f'ret m_list is {m_list}')
            return m_list




