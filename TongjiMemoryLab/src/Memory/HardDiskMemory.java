package Memory;

public class HardDiskMemory {
    private int hardMemorySize;
    private Frame[] diskInMemory;

    public HardDiskMemory(int hardMemorySize) {
        this.hardMemorySize = hardMemorySize;
        diskInMemory = new Frame[hardMemorySize];
        // init
        for (int i = 0; i < hardMemorySize; i++) {
            diskInMemory[i] = new Frame(i);
        }
    }

    Frame releaseFrame(int frameBios) {
        Frame frame = diskInMemory[frameBios];
        diskInMemory[frameBios] = null;
        return frame;
    }

    Frame addFrame(Frame frame) {
        int bios = frame.getBegin();
        if (diskInMemory[bios] != null) {
            throw new RuntimeException("Bios in Frame.addFrame is not zero!");
        }
        diskInMemory[bios] = frame;
        return frame;
    }
}
