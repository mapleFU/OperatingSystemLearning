package Memory.EvictAlgorithm;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class FIFOEvict extends EvictBase {
    /**
     * 是FIFO的使用队列
     */
    private LinkedList<Integer> fifoUsedQueue;

    public FIFOEvict(int lruSize) {
        super(lruSize);
        fifoUsedQueue = new LinkedList<>();
    }

    @Override
    public void codeUse(int frameID) {
        Iterator<Integer> iter = fifoUsedQueue.iterator();
        boolean existed = false;
        while (iter.hasNext()) {
            if (iter.next() == frameID) {
                existed = true;
                break;
            }
        }
        if (!existed) {
            if (fifoUsedQueue.size() == lruSize) {
                throw new RuntimeException("codeuse size out of range.");
            } else {
                fifoUsedQueue.addLast(frameID);
            }
        }
    }

    @Override
    public int evictID() {
        Integer remove = fifoUsedQueue.removeFirst();
        return remove;
    }
}
