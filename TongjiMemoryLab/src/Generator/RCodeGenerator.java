package Generator;

import java.util.Iterator;
import java.util.Stack;

public class RCodeGenerator implements Iterator<Integer> {
    private Stack<Integer> stack = new Stack<>();
    /**
     * 最多生成的指令
     */
    private final int maxExec;
    /**
     * 已经生成的指令
     */
    private int generatedCode;
    void generateFor() {

    }

    void generateIf() {

    }

    void generateContinus() {

    }



    public RCodeGenerator(int maxExecute) {
        maxExec = maxExecute;
        generatedCode = 0;

    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public Integer next() {
        return null;
    }
}