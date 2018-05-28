package Memory;

import Memory.EvictAlgorithm.EvictBase;
import Memory.EvictAlgorithm.FIFOEvict;
import Memory.EvictAlgorithm.LRUEvict;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.*;
import javafx.util.Pair;

import java.util.concurrent.Callable;

public class PhysicsMemory {

    public int getPhysicsMemorySize() {
        return physicsMemorySize;
    }

    private final int physicsMemorySize;
    private Frame[] frames;
    private HardDiskMemory hardDiskMemory;

    public float getPageFaultRate() {
        return pageFaultRate.get();
    }

    public FloatProperty pageFaultRateProperty() {
        return pageFaultRate;
    }

    private FloatProperty pageFaultRate;
    private IntegerProperty requestPages;
    // 这个试试看0。0
    private FloatProperty faultNumbers;
    /**
     * 便于添加属性
     * @return
     */
    public IntegerProperty[] getFrameProperties() {
        return frameProperties;
    }

    private IntegerProperty[] frameProperties;
    /**
     * page的信息是否变更过
     */
    private boolean changed;
    /**
     * 算法缺页的次数
     */
    private int pageFaultCount;
    public PhysicsMemory(int physicsMemorySize, HardDiskMemory hardDiskMemory, EvictBase evictAlgorithm) {
        changed = false;
        this.physicsMemorySize = physicsMemorySize;
        frames = new Frame[physicsMemorySize];
        frameProperties = new IntegerProperty[physicsMemorySize];
        for (int i = 0; i < physicsMemorySize; i++) {
            frameProperties[i] = new SimpleIntegerProperty(-1);
        }

        this.hardDiskMemory = hardDiskMemory;

        this.spareSpace = physicsMemorySize;

        // 开辟等于物理大小的evictor
        this.evictor = evictAlgorithm;
        // init page fault count
        this.pageFaultCount = 0;

        // set property for the page
        requestPages = new SimpleIntegerProperty(1);

        faultNumbers = new SimpleFloatProperty(0);
        pageFaultRate = new SimpleFloatProperty(0);

//        pageFaultRate.bind(Bindings.divide(faultNumbers, requestPages));

        pageFaultRate.bind(
                Bindings.divide(faultNumbers, requestPages)
        );
    }

    private int spareSpace; // 空闲的空间

    // 负责执行evict
    // evict 对应的都是在数组里的下标
    private EvictBase evictor;
    // 返回应当evict的code
    private int evict() {
        return evictor.evictID();
    }

    private void useCode(int frameID) {
        evictor.codeUse(frameID);
    }

    Frame getPhysicFrame(int frameID) {
        useCode(frameID);
        return frames[frameID];
    }

    Pair<Integer, Integer> loadToPhysicsMemory(PageTableEntry pte) {
        // 切换成改变了
        if (!changed) {
            changed = true;
        } else {
            requestPages.setValue(requestPages.getValue() + 1);
            System.out.println(requestPages.getValue());
            System.out.println(faultNumbers.getValue());
            System.out.println(pageFaultRate.get());
        }
        int bios = pte.getBiosValue();
        Frame retFrame = null;
        int newPosition = -1;
        int evictPos = -1;
        System.out.println("Load " + pte + " in physics memory.");
        if (spareSpace != 0) {
            // 直接载入
            for (int i = 0; i < this.physicsMemorySize; i++) {
                if (frames[i] == null) {
                    useCode(i);
                    frames[i] = this.hardDiskMemory.releaseFrame(bios);
                    frameProperties[i].set(frames[i].getFrameID());
                    retFrame = frames[i];
                    newPosition = i;
                    --spareSpace;
                    break;
                }
            }
        } else {
            // 需要替换
            evictPos = evict();
            System.out.println("evict frame begin with " + frames[evictPos].getBegin() + " in physics memory by " + evictor);
            this.hardDiskMemory.addFrame(frames[evictPos]);
            frames[evictPos] = hardDiskMemory.releaseFrame(bios);
            frameProperties[evictPos].set(frames[evictPos].getFrameID());
            retFrame = frames[evictPos];
            ++pageFaultCount;
            // TODO:这个没有定义原子自增么...
            faultNumbers.setValue(faultNumbers.intValue() + 1);
            newPosition = evictPos;
            useCode(evictPos);
        }
//        return retFrame;
        if (newPosition == -1) {
            throw new RuntimeException("newPosition in loadToPhysicsMemory is -1.");
        }
        return new Pair<>(newPosition, evictPos);
    }

    public int getPageFaultCount() {
        return pageFaultCount;
    }

    /**
     * @return array for page frame id
     *         if frame is null, then return -1.
     */
    public int[] getPageNumbersInPhysicMemory() {
        int[] pageNumbers = new int[frames.length];
        for (int i = 0; i < frames.length; i++) {
            if (frames[i] == null) {
                // -1 non exists
                pageNumbers[i] = -1;
            } else {
                //
                pageNumbers[i] = frames[i].getFrameID();
            }
        }
        return pageNumbers;
    }

    /**
     * @return
     *  if unchange -- null
     *  else -- return getPageNumbersInPhysicMemory()
     */
    @Deprecated
    public int[] getChangedPageNumbersInPhysicsMemory() {
        if (!changed) {
            return null;
        } else {
            return getPageNumbersInPhysicMemory();
        }
    }
}
