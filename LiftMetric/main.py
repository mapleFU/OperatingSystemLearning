# http://flask-socketio.readthedocs.io/en/latest/
# API 需要按照这个奇葩的标准写
import time
import json

from flask import Flask, render_template
from flask_bootstrap import Bootstrap
from flask_socketio import SocketIO, emit, send

from LiftUtils.LiftController import LiftController

app = Flask(__name__)
bootstrap = Bootstrap(app)
app.config['SECRET_KEY'] = 'secret!'
socketio = SocketIO(app, async_mode="threading")
# lift controller in programming
program_lift_controller = LiftController(socketio)


@app.route("/")
def hello():
    program_lift_controller.emit('lifts all', program_lift_controller.get_all_status())
    return render_template('base.html',
                           lift_ids=(i for i in range(1, 6)),
                           key_ids=(i for i in range(1, 21)))


@socketio.on('connect', namespace='/lifts')
def test_connect():
    # 这里SOCKETIO ON是没有MESSAGE的？
    emit('lift status', program_lift_controller.get_all_status())


@socketio.on('disconnect', namespace='/lifts')
def test_disconnect():
    print('socketIO: disconnect')


@socketio.on('add job', namespace='/lifts')
def handle_add_job(json_msg):
    print("Add Job!")
    print(json_msg)
    # msg_json = json.loads(json_msg)
    from_floor = int(json_msg["from"])
    to_floor = int(json_msg["to"])
    program_lift_controller.add_job(from_floor, to_floor)


@socketio.on('inner job', namespace='/lifts')
def handle_inner_job(json_msg):
    program_lift_controller.add_inner_job(
        lift_number=int(json_msg["lift_number"]),
        to=int(json_msg["to"])
    )


@socketio.on('my event', namespace='/lifts')
def handle_my_custom_event(json):
    print('handle my event')


@socketio.on('key clicked', namespace='/lifts')
def handle_key_clicked(json_v):
    pass


if __name__ == '__main__':
    socketio.run(debug=True, app=app)


