package models;

import Memory.EvictAlgorithm.EvictBase;
import Memory.HardDiskMemory;
import Memory.PhysicsMemory;
import javafx.beans.property.IntegerProperty;

public class PhysicMemoryBean {
    private final PhysicsMemory physicsMemory;

    public IntegerProperty getFrameProperty(int index) {
        return frameProperties[index];
    }

    private IntegerProperty[] frameProperties;

    public PhysicMemoryBean(int memorySize, HardDiskMemory hardDiskMemory, EvictBase evictBase) {
        this(new PhysicsMemory(memorySize, hardDiskMemory, evictBase));

    }

    public PhysicMemoryBean(PhysicsMemory pm) {
        physicsMemory = pm;
        frameProperties = new IntegerProperty[pm.getPhysicsMemorySize()];
        for (int i = 0; i < 4; i++) {
            frameProperties[i] = physicsMemory.getFrameProperties()[i];
        }
    }

}
