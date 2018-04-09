import Controller.InputGetter;
import Model.*;

public class main {
    public static void main(String[] args) {
        // set args

        // create manager with args
        WaitInLineBuffer buffer = new WaitInLineBuffer(3, 3);
        Checker[] checkers = new Checker[WaitingSystemManager.checkerNum];
        for (int i = 0; i < WaitingSystemManager.checkerNum; i++) {
            checkers[i] = new Checker();
        }

        // start receiving command line.
        Thread t = new Thread(new InputGetter());
        t.start();

    }
}
