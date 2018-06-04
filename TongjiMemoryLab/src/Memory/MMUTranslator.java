package Memory;


import javafx.util.Pair;

/**
 * MMU 转换器，能够将虚拟地址转换成物理空间的地址
 */
public class MMUTranslator {
    // 去除
    private VirtualMemory virtualMemory;
    private PhysicsMemory physicsMemory;
    private PageTable pageTable;
    MMUTranslator(VirtualMemory virtualMemory, PhysicsMemory physicsMemory) {
        this.virtualMemory = virtualMemory;
        this.physicsMemory = physicsMemory;
        // 创建虚拟内存大小的PTE
        pageTable = new PageTable(virtualMemory.getVirtualSize());
    }

    /**
     * 没有采用TLB的一级MMUTranslator.
     * @param vpn 需要获取的虚拟页面地址
     * @return 对应的codeID所在的
     */
    Frame getFrame(int vpn) {
        PageTableEntry codePte;
        try {
            // 先获取页面的 PTE
            codePte = pageTable.getPTE(vpn);
        } catch (PageFaultException e) {
            // 处理page fault
            codePte = handlePageFault(e);
        }
        // 获得偏移量对应的code

        if (codePte == null) {
            throw new RuntimeException("codePTE is null!");
        }
        Frame f = physicsMemory.getPhysicFrame(codePte.getPFN());
        // TODO: delete this after debug
        if (f.getFrameID() != vpn) {
            System.out.println("你的程序是一个傻逼程序");
        }
        return f;
    }

    private PageTableEntry handlePageFault(PageFaultException e) {
        // 处理PAGE FAULT
        // to_move_pte 表示要写入物理内存的PTE
        PageTableEntry to_move_pte = e.pageTableEntry;
        // 加载到物理存储空间中, 并且返回frame
        Pair<Integer, Integer> valuePair = physicsMemory.loadToPhysicsMemory(to_move_pte);
        int newFrameID = valuePair.getKey();
        int oldFrameID = valuePair.getValue();
        // set false
        if (oldFrameID != -1) {
            // 设置旧值(被Evict的)的保护位为false
            pageTable.setPTEProtectbitsFalse(oldFrameID);
        }

        to_move_pte.setProtectbits(true);
        to_move_pte.setPFN(newFrameID);
        return to_move_pte;
    }
}
