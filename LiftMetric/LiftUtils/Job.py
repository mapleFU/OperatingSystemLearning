"""
分派给LiftController的电梯基本状态
"""
from enum import Enum

from LiftState import LiftState


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
        self.beg = beg
        self.to = to
        if not direction:
            # if direc is None
            direction = '⬆️' if beg < to else '⬇️'
        self.dct = direction # 定下自身的方向

    def to_floor(self):
        return self.to

    def beg_floor(self):
        return self.beg

    @property
    def direc(self)->LiftState:
        """
        :return: direction of a job
        """
        return LiftState.UP if self.dct == '⬆️' else LiftState.DOWN


if __name__ == '__main__':
    print('⬆️⬇️')
