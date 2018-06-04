package Memory.EvictAlgorithm;

/**
 * 基本的替换算法的基类
 * 参考：http://flychao88.iteye.com/blog/1977653
 */
public abstract class EvictBase {
    final int lruSize;

    /**
     * 获得自用的缓存置换算法的算法名称
     * @return 算法名称
     */
    public String getEvictAlgoName() {
        return evictAlgoName;
    }

    protected String evictAlgoName;
    public EvictBase(int lruSize) {
        this.lruSize = lruSize;
    }

    /**
     * 表示对应位置的物理内存被使用
     * @param frameID
     */
    public abstract void codeUse(int frameID);

    /**
     * 返回应该evict的物理内存的ID
     * @return
     */
    public abstract int evictID();

    @Override
    public String toString() {
        return "EvictAlgorithm " + evictAlgoName;
    }
}
