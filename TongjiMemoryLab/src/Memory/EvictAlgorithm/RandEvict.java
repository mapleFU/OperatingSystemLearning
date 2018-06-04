package Memory.EvictAlgorithm;

import java.util.Random;

public class RandEvict extends EvictBase {

    private Random random;
    public RandEvict(int lruSize) {
        super(lruSize);
        evictAlgoName = "Rand";
        random = new Random();
    }

    // 什么都不做
    @Override
    public void codeUse(int frameID) { }

    @Override
    public int evictID() {
        // 返回一个对应大小的数
        return random.nextInt(lruSize);
    }
}
