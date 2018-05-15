import javafx.util.Pair;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

/**
 * 用于标注生成乱序代码的类
 */
public class RandomCodeGenerator implements Iterator<Integer> {
    /**
     * 执行的指令的总条数
     */
    private final int codeNum;
    private final Random random;
    private final int MIN_SPLIT = 15;
    private final int MIN_CHOOSE = 10;

    LinkedList<Pair<Integer, Integer>> freeList;

    public RandomCodeGenerator(int codeNumber) {
        codeNum = codeNumber;
        random = new Random();
        freeList = new LinkedList<>();
        // init first
        freeList.add(new Pair<>(0, codeNum));
        currentCursor = codeNum;
        currentEnd = codeNum;
        currentBeg = 0;
    }

    /**
     * 根据给出的Segment, 获得对应的current变量组
     * @return true if linkedlist has remains else false
     */
    private boolean getCursorBySegment() {
        if (freeList.size() == 0) {
            return false;
        }
        int nodeN = random.nextInt(freeList.size());
        Pair<Integer, Integer> beg_end_pair = freeList.get(nodeN);
        int delta = beg_end_pair.getValue() - beg_end_pair.getKey();

        // 定下新的边界
        if (delta <= MIN_SPLIT) {
            currentBeg = beg_end_pair.getKey();
            currentEnd = beg_end_pair.getValue();
        } else {
            currentBeg = random.nextInt(beg_end_pair.getValue() - beg_end_pair.getKey()) + beg_end_pair.getKey();
            if (currentBeg + MIN_CHOOSE >= beg_end_pair.getValue()) {
                // 越过边界，只需要插入一段数据
                currentEnd = beg_end_pair.getValue();
            } else {
                // 没有越过边界，需要插入两段数据
                currentEnd = currentBeg + MIN_CHOOSE;
            }
        }

        // split linked list
        freeList.remove(nodeN);
        if (currentBeg > beg_end_pair.getKey())
            freeList.add(nodeN++, new Pair<>(beg_end_pair.getKey(), currentBeg));
        if (currentEnd < beg_end_pair.getValue())
            freeList.add(nodeN, new Pair<>(currentEnd, beg_end_pair.getValue()));
        currentCursor = currentBeg;
        return true;
    }

    // CURRENT变量组
    private int currentCursor;
    private int currentBeg, currentEnd;

    @Override
    public boolean hasNext() {
        // [beg, end) 区间
        if (currentCursor == currentEnd) {
            if (!getCursorBySegment()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Integer next() {
        if (currentCursor == codeNum) {
            System.err.println("Beg: " + currentBeg + " , End:" + currentEnd);
        }
        return currentCursor++;
    }

}
