package Memory;

public class PageTableEntry {
    // PFN
    private int PFN;
    // 保护位，表示是否在内存中
    private boolean protectbits;
    // 是否合法
    private boolean valid;

    // 获得对应的偏移量
    public int getBiosValue() {
        return biosValue;
    }

    // 偏移量
    private final int biosValue;

    public int getPFN() {
        return PFN;
    }

    public void setPFN(int PFN) {
        this.PFN = PFN;
    }

    public boolean getProtectbits() {
        return protectbits;
    }

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
