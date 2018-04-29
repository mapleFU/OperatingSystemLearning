package Memory;

public class VirtualMemory {
    // 物理内存
    private PhysicsMemory physicsMemory;
    private MMUTranslator mmuTranslator;
    // 虚拟空间的大小和匹配
    private int virtualSize;

    public VirtualMemory(int code_nums) {
        physicsMemory = new PhysicsMemory();
        virtualSize = code_nums;
        mmuTranslator = new MMUTranslator(this, physicsMemory);
    }

    public Code getCode(int codeNum) {
        Frame frame = mmuTranslator.getFrame(getVPN(codeNum));
        // 获得偏移量
        return frame.getCode(getBios(codeNum));
    }

    private int getVPN(int codeNum) {
        return codeNum / 10;
    }

    private int getBios(int codeNum) {
        return codeNum % 10;
    }

    public int getVirtualSize() {
        return virtualSize;
    }
}
