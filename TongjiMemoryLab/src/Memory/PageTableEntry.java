package Memory;

/**
 * 页表项
 * 页表中存储的实体，用于映射到真正的物理空间。
 * 同时根据保护位等信息表示对应的物理信息是否在物理内存中，或者这块内存是否被初始化／使用
 */
public class PageTableEntry {
    // PFN
    private int PFN;
    // 保护位，表示是否在内存中
    private boolean protectbits;
    // 是否合法
    private boolean valid;

    /**
     * 获得页表项对应的页表上的偏移量
     * @return
     */
    public int getBiosValue() {
        return biosValue;
    }

    // 偏移量
    private final int biosValue;

    /**
     * 获得页表项对应的物理内存的位置
     *
     * @return PFN，对应物理内存的位置
     */
    public int getPFN() {
        return PFN;
    }

    /**
     * 设置页表项对应物理内存的位置
     *
     * @param PFN 新设置的对应物理内存的位置
     */
    public void setPFN(int PFN) {
        this.PFN = PFN;
    }

    /**
     * 获得保护位
     * 保护位用于表示页表是否在物理内存中
     * @return 保护位
     */
    public boolean getProtectbits() {
        return protectbits;
    }

    /**
     * 设置保护位
     * 保护位用于表示物理内存
     *
     * @param protectbits 新的保护位
     */
    public void setProtectbits(boolean protectbits) {
        this.protectbits = protectbits;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    PageTableEntry(int biosValue) {
        this.biosValue = biosValue;
        // 被申请内存之后都是valid
        this.valid = true;
        this.protectbits = false;
    }


    @Override
    public String toString() {
        return "PageTableEntry{" +
                "biosValue=" + biosValue +
                '}';
    }
}
