package Memory;

/*
页面的FRAME
 */
public class Frame {
    private Memory.Code[] memories;
    private static final int MEMORY_SIZE = 10;
    private int begin;

    boolean isInPhysicsMemory() {
        return inPhysicsMemory;
    }

    void getInPhysicsMemory() {
        inPhysicsMemory = true;
    }

    void getOutPhysicsMemory() { inPhysicsMemory = false; }

    private void setInPhysicsMemory(boolean inPhysicsMemory) {
        this.inPhysicsMemory = inPhysicsMemory;
    }

    // 是否在物理内存中
    private boolean inPhysicsMemory;

    Memory.Code getCode(int bios) throws ArrayIndexOutOfBoundsException {
        // 初始化为FALSE
        inPhysicsMemory = false;
        if (bios < 0 || bios >= MEMORY_SIZE) {
            throw new ArrayIndexOutOfBoundsException("bios should >= 0 and <=" + MEMORY_SIZE);
        }
        return memories[bios];
    }

    public Frame(int begin) {
        this.begin = begin;
        memories = new Memory.Code[MEMORY_SIZE];

        for (int i = 0; i < MEMORY_SIZE; i++) {
            // generate code
            memories[i] = new Memory.Code(begin * MEMORY_SIZE + i);
        }
    }

    public int getBegin() {
        return begin;
    }
}
