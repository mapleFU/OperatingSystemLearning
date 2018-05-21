package Memory;

public class VirtualMemory {
    // 物理内存
    private PhysicsMemory physicsMemory;
    private MMUTranslator mmuTranslator;
    // 虚拟空间的大小和匹配
    private int virtualSize;

    public VirtualMemory(int code_nums, PhysicsMemory physicsMemory) {
        this.physicsMemory = physicsMemory;
        virtualSize = code_nums;
        mmuTranslator = new MMUTranslator(this, physicsMemory);
    }

    public Code getCode(int codeNum) {
        // 这一步相对来说应该是直接get pte?
        Frame frame = null;
        frame = mmuTranslator.getFrame(getVPN(codeNum));
        // 获得偏移量对应的code
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
