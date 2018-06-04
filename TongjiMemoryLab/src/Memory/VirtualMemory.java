package Memory;

/**
 * 虚拟内存，通过MMUTranslator在物理内存寻址。用户通过worker操作虚拟内存来查找对应的指令。
 */
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

    /**
     * 虚拟内存的指令位置(Virtual Page Number)来查找对应的指令。
     *
     * @param codeNum
     * @return 寻址到的CODE
     */
    public Code getCode(int codeNum) {
        // 这一步相对来说应该是直接get pte?
        Frame frame;
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

    /**
     * @return 虚拟内存的容量
     */
    public int getVirtualSize() {
        return virtualSize;
    }
}
