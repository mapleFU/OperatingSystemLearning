package Memory;

/**
 * 表示抽象的代码
 */
class Code {
    private final int codeID;

    /*
    constructor
     */
    Code(int codeID) {
        this.codeID = codeID;
    }

    @Override
    public String toString() {
        return "Code{" +
                "codeID=" + codeID +
                '}';
    }
}
