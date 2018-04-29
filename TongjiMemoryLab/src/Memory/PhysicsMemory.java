package Memory;

class PhysicsMemory {
    private int physicsMemorySize;
    private Frame[] frames;
    PhysicsMemory(int physicsMemorySize) {
        this.physicsMemorySize = physicsMemorySize;
        frames = new Frame[physicsMemorySize];
    }

    private void LRUEvict() {

    }

    private Frame getPhysicFrame(int frameID) {
        return frames[frameID];
    }
}
