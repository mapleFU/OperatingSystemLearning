package Memory;

public class PageTable {
    private int pageTableSize;
    private Memory.PageTableEntry[] PTEArray;

    PageTable(int size) {
        pageTableSize = size;
        PTEArray = new PageTableEntry[size];
        for (int i = 0; i < PTEArray.length; i++) {
            // 自动初始化的PTE, 给予PFN -- i
            PTEArray[i] = new PageTableEntry(i);
        }
    }

    PageTableEntry getPTE(int virtualAdd) throws PageFaultException {
        PageTableEntry pte = PTEArray[virtualAdd];
        if (!pte.isValid()) {
            // is not valid
            // 这里不可能发生的，反正...
            return null;
        } else if (!pte.getProtectbits()) {
            // not pretect
            throw new PageFaultException(pte);
        } else {
            return pte;
        }
    }

    /**
     * 将对应方法的保护位设置为真
     * @param virtualAdd 虚拟的底子
     */
    void setPTEProtectbitsTrue(int virtualAdd) {
        PageTableEntry pte = PTEArray[virtualAdd];
        if (!pte.getProtectbits()) {
            pte.setProtectbits(true);
        }
    }

    /**
     * 将对应方法的保护位设置为假
     * @param virtualAdd
     */
    void setPTEProtectbitsFalse(int virtualAdd) {
        PageTableEntry pte = PTEArray[virtualAdd];
//        if (!pte.getProtectbits()) {
//            pte.setProtectbits(false);
//        }
        if (pte.getProtectbits()) {
            pte.setProtectbits(false);
        }
    }
}

class PageFaultException extends Exception
{
    // 不在物理内存中的缺页的PTE，需要被载入物理内存
    PageTableEntry pageTableEntry;
    PageFaultException(PageTableEntry pageTableEntry)
    {
        super();
        this.pageTableEntry = pageTableEntry;
    }

    public PageFaultException(String message)
    {
        super(message);
    }

    public PageFaultException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public PageFaultException(Throwable cause)
    {
        super(cause);
    }
}