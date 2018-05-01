import Memory.Code;
import Memory.PageTable;
import Memory.VirtualMemory;

public class Worker {
    private VirtualMemory virtualMemory;

    public Worker(VirtualMemory virtualMemory) {

        this.virtualMemory = virtualMemory;
    }

    public void executeCode(int codeID) {
        Code code = virtualMemory.getCode(codeID);
        // 具体处理指令
        System.out.println("Execute: " + code.toString());
    }

}
