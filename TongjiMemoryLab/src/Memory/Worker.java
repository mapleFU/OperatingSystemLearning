package Memory;

import Memory.Code;
import Memory.EvictAlgorithm.EvictBase;
import Memory.PageTable;
import Memory.VirtualMemory;
import javafx.beans.property.StringProperty;
import javafx.util.Pair;

/**
 * 执行代码的类，能够执行代码，并且分析执行代码产生的缺页率等信息。
 */
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

    /**
     * 模拟执行代码，并产生相应的信息
     *
     * @param codeID 虚拟内存的VPN
     * @return 执行代码产生的信息。
     */
    public String executeCode(int codeID) {
        Code code = virtualMemory.getCode(codeID);
        // 具体处理指令
        String ret = "Execute: " + code.toString();
        System.out.println(ret);
        return ret;
    }
}
