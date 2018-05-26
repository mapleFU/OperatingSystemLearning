package Memory;

import Memory.Code;
import Memory.EvictAlgorithm.EvictBase;
import Memory.PageTable;
import Memory.VirtualMemory;
import javafx.util.Pair;

public class Worker {

    public static Pair<Worker, PhysicsMemory> generateWorker(EvictBase evictAlgo) {
        // 存储的硬件
        HardDiskMemory hardDiskMemory = new HardDiskMemory(32);
        // 物理内存
        PhysicsMemory physicsMemory = new PhysicsMemory(4, hardDiskMemory, evictAlgo);
        // 虚拟内存需要处理32指令
        VirtualMemory virtualMemory = new VirtualMemory(32, physicsMemory);
        // 执行的进程WORKER
        return new Pair<>(new Worker(virtualMemory), physicsMemory);
    }

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
