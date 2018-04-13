# 电梯调度算法设计
## 用户接口

1. 添加一个 从beg楼到end楼的人
2. 在电梯内瞎几把按键

## 基本算法

### 电梯的状态
* 上行
* 下行
* 静止

上行和下行的状态中有等待的属性。电梯的状态会根据状态开启一个线程，并暴露有这个基本的状态。
### 电梯的调度算法
#### 电梯的选择
按下电梯后，会在LiftController添加一个信号，这个信号是未受理的。它的内容包含去几楼。

检查这些未受理的信息，找到最好的调度的电梯\(可调度的电梯中选择最近的\)。并删除这个信号。

### 电梯的运行算法
运行中的电梯每隔t1时间会改变一次楼层的属性，修改的时候是lock的。同一层的运行电梯不会被调度。

电梯保有自身的楼层属性和想要去的楼层的信息。这个信息是有序的。

### 电梯的status

```python
def status(self):
    """
    :return: json str of status.
    """
	with self._state_lock:
    return json.dumps({
        'lift_number': self.LNUM,
        'status': self._state.value,
        'floor': self._floor,
    })
```



### SocketIO 事件

发送方：服务端

| event              | 数据格式                                                     | 使用时刻                 |
| ------------------ | ------------------------------------------------------------ | ------------------------ |
| lifts all          | {"1": "{\"lift_number\": 1, \"status\": \"rest\"}", "2": "{\"lift_number\": 2, \"status\": \"rest\"}", "3": "{\"lift_number\": 3, \"status\": \"rest\"}", "4": "{\"lift_number\": 4, \"status\": \"rest\"}", "5": "{\"lift_number\": 5, \"status\": \"rest\"}"} | 广播现有的所有电梯的数据 |
| lift change        | {"floor": 12, "status": "up", "lift_number": 9}              | 单个电梯楼层等状态变化   |
| lift status change | {\"lift_number\": 1, \"status\": \"rest\"}                   | 到达某个约定的层         |
| lift innertask     | {"lift_number": 1, "aim floor": }                            |                          |

发送方：客户

| event     | 数据格式                    | 使用时刻                             |
| --------- | --------------------------- | ------------------------------------ |
| add job   | {"from": 1, "to": 2}        | 在电梯口按按钮上行-下行调度时        |
| inner job | {"lift_number": 1, "to": 9} | 在电梯内部按按钮上行-下行-几楼调度时 |

## 架构

### main

主程序，提供 flask 接口，给各个信息路由提供反应

### LiftUtils

#### Jobs

单个任务

#### Lift

单个电梯的信息

#### LiftController

电梯控制器

#### Task

任务的集合

## 请求和相应

add job —> LiftController：放入队列，试图增加请求 —> 全部失败则放到队列尾部

增加请求：查询每个jobs

## 互斥的信息
电梯的状态，

