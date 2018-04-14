from enum import Enum
from queue import Queue
from threading import RLock, Lock
from typing import List, TYPE_CHECKING
from collections import namedtuple


from .LiftState import LiftState
from .Job import Job

if TYPE_CHECKING:
    from .LiftController import LiftController
    from .Lift import Lift


class Floor:
    FloorChoice = namedtuple('FloorChoice', ['list', 'lock'])

    def __init__(self, floor_n: int, controler: 'LiftController'):
        self.floor = floor_n
        self._controler = controler

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

    def _arrive_clear_choice_locked(self, chocie: FloorChoice):
        pass

    def clear_and_out(self, lift: 'Lift'):
        # TODO: make clear if locked
        liftstate = lift.state
        if liftstate == LiftState.UP:
            mapper = self._mapper["up"]
        elif liftstate == LiftState.DOWN:
            mapper = self._mapper["down"]
        else:
            mapper = None




