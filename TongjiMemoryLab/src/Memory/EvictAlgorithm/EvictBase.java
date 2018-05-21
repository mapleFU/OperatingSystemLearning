package Memory.EvictAlgorithm;

/**
 * 基本的替换算法
 * 参考：http://flychao88.iteye.com/blog/1977653
 */
public abstract class EvictBase {
    final int lruSize;

    public String getEvictAlgoName() {
        return evictAlgoName;
    }

    protected String evictAlgoName;
    public EvictBase(int lruSize) {
        this.lruSize = lruSize;
    }

    public abstract void codeUse(int frameID);
    public abstract int evictID();

    @Override
    public String toString() {
        return "EvictAlgorithm " + evictAlgoName;
    }
}
