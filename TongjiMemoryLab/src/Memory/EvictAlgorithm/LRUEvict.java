package Memory.EvictAlgorithm;

import java.util.Iterator;
import java.util.LinkedList;

public class LRUEvict extends EvictBase {
    private LinkedList<Integer> intList;
    
    public LRUEvict(int size) {
        // fill in lruSize
        super(size);
        intList = new LinkedList<>();
        evictAlgoName = "LRU";
    }

    @Override
    public void codeUse(int frameID) {
        Iterator<Integer> iter = intList.iterator();
        boolean existed = false;
        while (iter.hasNext()) {
            if (iter.next() == frameID) {
                iter.remove();
                existed = true;
                intList.addLast(frameID);
                break;
            }
        }
        if (!existed) {
            if (intList.size() == lruSize) {
                throw new RuntimeException("codeuse size out of range.");
            } else {
                intList.addLast(frameID);
            }
        }
    }

    @Override
    public int evictID() {
        int toRemove = intList.removeFirst();
        return toRemove;
    }
}
