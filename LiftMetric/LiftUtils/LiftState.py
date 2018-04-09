"""
储存电梯的状态信息
"""
from enum import Enum


class LiftState(Enum):
    REST = '静止'
    UP = '上楼'
    DOWN = '下楼'
