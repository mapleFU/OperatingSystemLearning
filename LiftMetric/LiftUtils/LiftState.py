"""
储存电梯的状态信息
"""
from enum import Enum


class LiftState(Enum):
    REST = 'rest'
    UP = 'up'
    DOWN = 'down'


if __name__ == '__main__':
    print(LiftState.UP.value)