# http://flask-socketio.readthedocs.io/en/latest/
# API 需要按照这个奇葩的标准写
import time
import json
from threading import Thread, Lock, RLock

from flask import Flask, render_template
from flask_bootstrap import Bootstrap
from flask_socketio import SocketIO
import eventlet

from LiftUtils.LiftController import LiftController


eventlet.monkey_patch()
async_mode = None
app = Flask(__name__)
bootstrap = Bootstrap(app)
app.config['SECRET_KEY'] = 'secret!'
socketio = SocketIO(app, async_mode=async_mode)
# lift controller in programming
program_lift_controller = LiftController(socketio)


def floor_valid(floor_n: int)->bool:
    """

    :param floor_n: 楼梯的序号
    :return: 是否合法
    """
    if isinstance(floor_n, str):
        floor_n = int(floor_n)
    elif not isinstance(floor_n, int):
        raise RuntimeError(f"{floor_n} is not str or int")
    return 0 < floor_n <= 20


@app.route("/")
def hello():
    return render_template('base.html',
                           lift_ids=(i for i in range(1, 6)),
                           key_ids=(i for i in range(1, 21)), async_mode=socketio.async_mode)


@socketio.on('connect', namespace='/lifts')
def test_connect():
    # 这里SOCKETIO ON是没有MESSAGE的？
    program_lift_controller.emit('lifts all', program_lift_controller.get_all_status())


@socketio.on('disconnect', namespace='/lifts')
def test_disconnect():
    print('socketIO: disconnect')


@socketio.on('add job', namespace='/lifts')
def handle_add_job(json_msg):
    # msg_json = json.loads(json_msg)
    print(json_msg["from"], json_msg["to"])
    from_floor = int(json_msg["from"])
    to_floor = int(json_msg["to"])
    if not floor_valid(from_floor) or not floor_valid(to_floor):
        return
    program_lift_controller.add_job(from_floor, to_floor)


@socketio.on('inner job', namespace='/lifts')
def handle_inner_job(json_msg):
    if not floor_valid(json_msg["to"]):
        return
    program_lift_controller.add_inner_job(
        lift_number=int(json_msg["lift_number"]),
        to=int(json_msg["to"])
    )


@socketio.on('my event', namespace='/lifts')
def handle_my_custom_event(json):
    print('handle my event')


@socketio.on('outer job', namespace='/lifts')
def handle_key_clicked(json_v):
    if not floor_valid(json_v["floor"]):
        return
    program_lift_controller.add_outer_job(from_floor=int(json_v["floor"]), drc=json_v["direc"])


if __name__ == '__main__':
    socketio.run(app=app, host='127.0.0.1', port=5000)


