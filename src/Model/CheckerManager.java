package Model;

import Model.Checker;

public class CheckerManager {
    public static final int checkerNum = 8;     // 八个检票口

    // Data
    private Checker[] checkers;
    private Thread[] threads;

    public CheckerManager() {
        checkers = new Checker[checkerNum];
        for (int i = 0; i < checkerNum; i++) {
            threads[i] = new Thread(checkers[i]);
        }
        for (int i = 0; i < checkerNum; i++) {
            threads[i].start();
        }
    }

    public boolean addPerson() {

    }
}
