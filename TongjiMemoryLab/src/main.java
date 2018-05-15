import Memory.HardDiskMemory;
import Memory.MMUTranslator;
import Memory.PhysicsMemory;
import Memory.VirtualMemory;

import java.util.Iterator;

public class main {
    public static void main(String[] args) {
        // 存储的硬件
        HardDiskMemory hardDiskMemory = new HardDiskMemory(32);
        // 物理内存
        PhysicsMemory physicsMemory = new PhysicsMemory(4, hardDiskMemory);
        // 虚拟内存需要处理32指令
        VirtualMemory virtualMemory = new VirtualMemory(32, physicsMemory);

        Worker worker = new Worker(virtualMemory);
        // execute code of rcg
        Iterator<Integer> rcg = new RandomCodeGenerator(320);
        // 准备对比与对比算法
        while (rcg.hasNext()) {
            int value = rcg.next();
            worker.executeCode(value);
        }
    }
}
