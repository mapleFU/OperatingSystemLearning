package Memory;


import javafx.util.Pair;

public class MMUTranslator {
    // 去除
    private VirtualMemory virtualMemory;
    private PhysicsMemory physicsMemory;
    private PageTable pageTable;
    public MMUTranslator(VirtualMemory virtualMemory, PhysicsMemory physicsMemory) {
        this.virtualMemory = virtualMemory;
        this.physicsMemory = physicsMemory;
        // 创建虚拟内存大小的PTE
        pageTable = new PageTable(virtualMemory.getVirtualSize());
    }

    /*
    包内用于GET
     */
    Frame getFrame(int codeID) {
        PageTableEntry codePte;
        try {
            codePte = pageTable.getPTE(codeID);
        } catch (PageFaultException e) {
            // 处理PAGE FAULT
            PageTableEntry to_move_pte = e.pageTableEntry;
            // 加载到物理存储空间中, 并且返回frame
            Pair<Integer, Integer> valuePair = physicsMemory.loadToPhysicsMemory(to_move_pte);
            int newFrameID = valuePair.getKey();
            int oldFrameID = valuePair.getValue();
            // set false
            if (oldFrameID != -1) {
                // there is old value.
                pageTable.setPTEProtectbitsFalse(oldFrameID);
            }
            codePte = to_move_pte;
            to_move_pte.setProtectbits(true);
            to_move_pte.setPFN(newFrameID);
        }
        // 获得偏移量对应的code

        if (codePte == null) {
            throw new RuntimeException("codePTE is null!");
        }
        return physicsMemory.getPhysicFrame(codePte.getPFN());
    }

    private void handlePageFault() {

    }
}
