import Memory.EvictAlgorithm.EvictBase;
import Memory.EvictAlgorithm.FIFOEvict;
import Memory.EvictAlgorithm.LRUEvict;
import Memory.HardDiskMemory;
import Memory.MMUTranslator;
import Memory.PhysicsMemory;
import Memory.VirtualMemory;
import javafx.util.Pair;

import java.util.Iterator;

public class main {
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

    public static void main(String[] args) {

        Pair<Worker, PhysicsMemory> pair1 = generateWorker(new LRUEvict(4));
        Worker worker1 = pair1.getKey();
        PhysicsMemory physicsMemory1 = pair1.getValue();

        Pair<Worker, PhysicsMemory> pair2 = generateWorker(new FIFOEvict(4));
        Worker worker2 = pair2.getKey();
        PhysicsMemory physicsMemory2 = pair2.getValue();

        // execute code of rcg
        Iterator<Integer> rcg = new RandomCodeGenerator(320);
        // 准备对比与对比算法
        while (rcg.hasNext()) {
            int value = rcg.next();
            // worker1 执行
            worker1.executeCode(value);
//            worker2.executeCode(value);
        }

        // compare physics
        System.out.println(worker1 + " used " + physicsMemory1.getPageFaultCount());
//        System.out.println(worker2 + " used " + physicsMemory2.getPageFaultCount());
    }
}
