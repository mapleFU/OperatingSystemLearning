package Memory;

public class PageTable {
    private int pageTableSize;
    private Memory.PageTableEntry[] PTEArray;

    PageTable(int size) {
        pageTableSize = size;
        PTEArray = new PageTableEntry[size];
        for (int i = 0; i < PTEArray.length; i++) {
            // 自动初始化的PTE
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

    void setPTEProtectbitsFalse(int virtualAdd) {
        PageTableEntry pte = PTEArray[virtualAdd];
        if (!pte.getProtectbits()) {
            pte.setProtectbits(false);
        }
    }
}

class PageFaultException extends Exception
{
    // 缺页的PTE
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