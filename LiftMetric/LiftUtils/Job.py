"""
分派给LiftController的电梯基本状态
"""
from enum import Enum
from threading import Lock

from .LiftState import LiftState


class Job:
    """
    任务，表示电梯上升或者下降
    """
    def __init__(self, beg: int, to: int, direction: str=None):
        """
        :param beg:
        :param to:
        :param direc: 方向
        """
        self._gced: bool = False
        self._gclock: Lock = Lock()

        self.beg = beg
        self.to = to
        if not direction:
            # if direc is None
            direction = '⬆️' if beg < to else '⬇️'
        self.dct = direction # 定下自身的方向

    def __lt__(self, other):
        if isinstance(other, Job):
            return self.beg < other.beg
        raise TypeError(f"{other} must be a Job")

    def to_floor(self):
        return self.to

    def beg_floor(self):
        return self.beg

    def accept(self):
        with self._gclock:
            self._gced = True

    @property
    def accepted(self)->bool:
        """
        :return: 返回这个Job 是否被摧毁
        """
        with self._gclock:
            return self._gced

    @property
    def direc(self)->LiftState:
        """
        :return: direction of a job
        """
        return LiftState.UP if self.dct == '⬆️' else LiftState.DOWN


if __name__ == '__main__':
    print('⬆️⬇️')
