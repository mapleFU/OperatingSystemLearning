package Memory.EvictAlgorithm;

public class EvictAlgoFactory {
    private final int memorySize;
    public EvictAlgoFactory() {
        this(4);
    }
    public EvictAlgoFactory(int size) {
        memorySize = size;
    }

    public EvictBase createEvict(String algo_name) {
        EvictBase evictor;
        switch (algo_name) {
            case "FIFO":
                evictor = new FIFOEvict(memorySize);
                break;
            case "LRU":
                evictor = new LRUEvict(memorySize);
                break;
            default:
                throw new RuntimeException(algo_name + " not in Evict algorithm.");
        }
        return evictor;
    }
}
