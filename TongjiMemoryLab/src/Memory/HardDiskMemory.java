package Memory;

/**
 * 次级存储器，这里模拟磁盘进行存储，能够进行载入物理Frame、导出物理Frame的功能.
 */
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

    /**
     * 释放对应的物理帧, 并将页面对应的信息返回
     *
     * @param frameBios 对应的物理页面的偏移量
     * @return 寻找到的物理页面
     */
    Frame releaseFrame(int frameBios) {
        Frame frame = diskInMemory[frameBios];
        diskInMemory[frameBios] = null;
        return frame;
    }

    /**
     * 将指定的页面放入刺激存储
     *
     * @param frame 需要添加入物理内存的页面
     * @return 添加的本页面
     */
    Frame addFrame(Frame frame) {
        int bios = frame.getBegin();
        if (diskInMemory[bios] != null) {
            throw new RuntimeException("Bios in Frame.addFrame is not zero!");
        }
        diskInMemory[bios] = frame;
        return frame;
    }
}
