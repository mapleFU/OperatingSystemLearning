package Memory;

/*
页面的FRAME
 */
public class Frame {
    private Memory.Code[] memories;
    private static final int MEMORY_SIZE = 10;
    private int begin;

    Memory.Code getCode(int bios) throws ArrayIndexOutOfBoundsException {
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

    public int getFrameID() {
        return begin;
    }

    public int getBegin() {
        return begin;
    }
}
