package Memory;



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

    /*
    包内用于GET
     */
    Frame getFrame(int codeID) throws PageFaultException {
        PageTableEntry codePte;

        codePte = pageTable.getPTE(codeID);
        if (codePte == null) {
            throw new RuntimeException("codePTE is null!");
        }

    }

    private void handlePageFault() {

    }
}
