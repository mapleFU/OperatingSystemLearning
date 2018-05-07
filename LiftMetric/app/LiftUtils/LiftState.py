"""
储存电梯的状态信息
"""
from enum import Enum


class LiftState(Enum):
    REST = 'rest'
    UP = 'up'
    DOWN = 'down'

    @staticmethod
    def makestate(beg: int, to: int)->'LiftState':
        """
        :param beg: 起始的位置
        :param to: 结束的位置
        :return: 对应的楼梯状态
        """
        delta = to - beg
        if delta == 0:
            state = LiftState.REST
        elif delta > 0:
            state = LiftState.UP
        else:
            state = LiftState.DOWN
        return state


if __name__ == '__main__':
    print(LiftState.UP.value)
