import Memory.HardDiskMemory;
import Memory.MMUTranslator;
import Memory.PhysicsMemory;
import Memory.VirtualMemory;

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
        RandomCodeGenerator rcg = new RandomCodeGenerator(320);
        while (rcg.hasNext()) {
            int value = rcg.next();
//            System.out.println(value);
            worker.executeCode(value);
        }
    }
}
