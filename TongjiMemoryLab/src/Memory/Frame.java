package Memory;

/*
页面的FRAME
 */
public class Frame {
    private Code[] memories;
    private static final int MEMORY_SIZE = 10;

    public Code getCode(int bios) throws ArrayIndexOutOfBoundsException {
        if (bios < 0 || bios >= MEMORY_SIZE) {
            throw new ArrayIndexOutOfBoundsException("bios should >= 0 and <=" + MEMORY_SIZE);
        }
        return memories[bios];
    }

    public Frame(int begin) {
        memories = new Code[MEMORY_SIZE];

        for (int i = 0; i < begin + MEMORY_SIZE; i++) {
            // generate code
            memories[i] = new Code(begin + i);
        }
    }
}
