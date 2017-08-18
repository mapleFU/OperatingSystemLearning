package Model;

import Model.Checker;
import Model.WaitInLineBuffer;

/**
 * 与外界交互，利用组合管理Buffer和Checkers
 */
public class WaitingSystemManager {
    public static final int checkerNum = 8;     // 八个检票口

    // Data
    private Checker[] checkers;
    private Thread[] threads;
    private WaitInLineBuffer buffer;


    public WaitingSystemManager(WaitInLineBuffer buffer, Checker[] checkers) {
        this.buffer = buffer;
        this.checkers = checkers;
        for (int i = 0; i < checkerNum; i++) {
            threads[i] = new Thread(checkers[i]);
        }
        for (int i = 0; i < checkerNum; i++) {
            threads[i].start();
        }
    }

    /**
     * 添加角色是同步的操作, 只有一个人可以在线程中被加入
     * <em>强制</em> 加入一个人
     *
     * 实在没办法可以先全上写锁...?
     */
    private synchronized void addPersonToCheckers() {
        int[] curPersons = new int[checkerNum];

        while (true) {
            int minPersonIndex = -1;                    // the index of minimum checker
            int minPerson = 6;

            for (int i = 0; i < checkerNum; i++) {
                curPersons[i] = checkers[i].getPerson();
                if (curPersons[i] < minPerson) {
                    minPerson = curPersons[i];
                    minPersonIndex = i;         // ???
                }
            }

            if (minPersonIndex != -1) {
                // 只有能够加入 已经加入才会结束
                boolean added = checkers[minPersonIndex].checkAndAddPerson();
                if (added) {
                    break;
                }
            }
        }

    }

    /**
     * ADD PERSON TO BUFFER
     */
    public synchronized void addPersonToBuffer() {
        buffer.addInBuffer();
    }
}
