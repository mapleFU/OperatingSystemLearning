package models;

import Memory.EvictAlgorithm.EvictBase;
import Memory.HardDiskMemory;
import Memory.PhysicsMemory;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;

public class PhysicMemoryBean {
    private final PhysicsMemory physicsMemory;

    public IntegerProperty getFrameProperty(int index) {
        return frameProperties[index];
    }

    private IntegerProperty[] frameProperties;
    // 缺页率
    private FloatProperty pageFaultRate;

    public PhysicMemoryBean(PhysicsMemory pm) {
        physicsMemory = pm;
        frameProperties = new IntegerProperty[pm.getPhysicsMemorySize()];
        for (int i = 0; i < 4; i++) {
            frameProperties[i] = physicsMemory.getFrameProperties()[i];
        }
        pageFaultRate = physicsMemory.pageFaultRateProperty();
    }

    public float getPageFaultRate() {
        return pageFaultRate.get();
    }

    public FloatProperty pageFaultRateProperty() {
        return pageFaultRate;
    }
}
