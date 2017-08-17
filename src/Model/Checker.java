package Model;

/**
 * 乘客安检的时间长短由随机数产生，范围在MinTimeLen到MaxTimeLen之间，
 * 安检口暂停休息时间长短由随机数产生，范围在MinRestTimeLen到MaxRestTimeLen之间。
 * MinTimeLen，MaxTimeLen，MinRestTimeLen，MaxRestTimeLen这四个值是系统可以配置的参数，
 * 保存在配置文件中，系统初始化时需要读取此参数
 */
public class Checker {
    // STATIC DATA

    public final static int MaxCheck = 6;   // 最多检查6人
    private static int MinTimeLen;
    private static int MaxTimeLen;
    private static int MinRestTimeLen;
    private static int MaxRestTimeLen;

    public static void setMinTimeLen(int minTimeLen) {
        MinTimeLen = minTimeLen;
    }

    public static void setMaxTimeLen(int maxTimeLen) {
        MaxTimeLen = maxTimeLen;
    }

    public static void setMinRestTimeLen(int minRestTimeLen) {
        MinRestTimeLen = minRestTimeLen;
    }

    public static void setMaxRestTimeLen(int maxRestTimeLen) {
        MaxRestTimeLen = maxRestTimeLen;
    }

    // private data
    private int person = 0;         // 正在排队的数目
    public synchronized void addPerson() {
        ++person;
    }

    /**
     * @return person waiting in line
     */
    public synchronized int getPerson() {
        return person;
    }

}
