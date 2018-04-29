package Memory;

public class PageTableEntry {
    // PFN
    private int PFN;
    // 保护位，表示是否在内存中
    private boolean protectbits;
    // 是否合法
    private boolean valid;

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

    PageTableEntry() {
        this.valid = false;
        this.protectbits = true;
    }
}
