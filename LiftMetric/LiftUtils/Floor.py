from enum import Enum
from queue import Queue
from threading import RLock, Lock
from typing import List
from collections import namedtuple


from LiftState import LiftState
from Job import Job


class Floor:
    FloorChoice = namedtuple('FloorChoice', ['list', 'lock'])

    def __init__(self, floor_n: int):
        self.floor = floor_n

        self._uplist: List[Job] = []
        self.__uplock: Lock = Lock()

        self._downlist: List[Job] = []
        self.__downlock: Lock = Lock()

        # 关于上下的映射，让我们能够执行同样的逻辑
        self._mapper = {
            "up": Floor.FloorChoice(self._uplist, self.__uplock),
            "down": Floor.FloorChoice(self._downlist, self.__downlock)
        }

    def add_task(self, job: Job):
        if job.direc == '⬆️':
            self._add_up(job)
        else:
            self._add_down(job)

    def _add_up(self, job: Job):
        with self.__uplock:
            self._uplist.append(job)

    def _add_down(self, job: Job):
        with self.__downlock:
            self._downlist.append(job)

    def arrive(self, liftstate: LiftState):
        if liftstate == LiftState.UP:
            raise NotImplemented()
        elif liftstate == LiftState.DOWN:
            raise NotImplemented()
        else:
            raise ValueError(f"LiftState is {liftstate} but except up or down")

