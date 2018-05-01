package Memory.EvictAlgorithm;

/*
基本的替换算法
 */
public abstract class EvictBase {
    final int lruSize;

    public EvictBase(int lruSize) {
        this.lruSize = lruSize;
    }

    public abstract void codeUse(int frameID);
    public abstract int evictID();
}
