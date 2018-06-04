package Generator;

import java.util.Iterator;
import java.util.Random;
import java.util.Stack;

public class RCodeGenerator implements Iterator<Integer> {
    private Stack<Integer> stack = new Stack<>();
    /**
     * 最多生成的指令
     */
    private final int maxExec;
    private int[] addresses;
    void generateAddress() {
        Random random = new Random();
        // records the last address to be stored
        int last = random.nextInt(maxExec);
        int flag = 0;
        for (int i = 0; i < maxExec; ++i) {
            addresses[i] = last;
            if (flag % 2 == 0) {
                // flag == 0 || flag == 2
                last = (last + 1) % maxExec;
            }
            else if (flag == 1) {
                if (last == 0) {
                    last = 0;
                }
                else {
                    last = random.nextInt(last - 1);
                }
            }
            else {
                // flag == 3
                last = last + 1 + random.nextInt(maxExec - (last + 1));
            }
            flag = (flag + 1) % 4;
        }
    }

    private int curIndex;

    public RCodeGenerator(int maxExecute) {
        maxExec = maxExecute;
        addresses = new int[maxExecute];
        generateAddress();
        curIndex = 0;
    }

    @Override
    public boolean hasNext() {
        return curIndex < maxExec;
    }

    @Override
    public Integer next() {
        return addresses[curIndex++];
    }
}