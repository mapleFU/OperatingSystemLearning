package Model;

import java.sql.Time;
import java.util.Random;

/**
 * 乘客安检的时间长短由随机数产生，范围在MinTimeLen到MaxTimeLen之间，
 * 安检口暂停休息时间长短由随机数产生，范围在MinRestTimeLen到MaxRestTimeLen之间。
 * MinTimeLen，MaxTimeLen，MinRestTimeLen，MaxRestTimeLen这四个值是系统可以配置的参数，
 * 保存在配置文件中，系统初始化时需要读取此参数
 */
public class Checker extends Thread {
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
    private int person;         // 正在排队的数目
    public synchronized void addPerson() {
        ++person;
    }
    public synchronized void popPerson() {
        --person;
        // DEBUG
        if (person < 0) {
            throw new RuntimeException("Person < 0!!!");
        }
    }

    /**
     * @return person waiting in line
     * TODO: find out if you need lock here
     */
    public int getPerson() {
        return person;
    }

    Random random = new Random();

    private int getRandomCheck() {
        return random.nextInt(MaxTimeLen - MinTimeLen) + MinTimeLen;
    }
    private int getRandomRetire() {
        return random.nextInt(MaxRestTimeLen - MinRestTimeLen) + MinRestTimeLen;
    }


    @Override
    public void run() {
        while (true) {
            while (getPerson() == 0) {}      //if person == 0, block(Is it right?)
            int waitTime = getRandomCheck();
            try {
                Thread.sleep(waitTime * 1000);
            } catch (InterruptedException e) {
                System.err.println("Error in sleep");
            }
            popPerson();
        }
    }
}
