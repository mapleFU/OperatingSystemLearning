package Model;

/**
 * “排队缓冲区”初始状态下是直线型。一个直队最多能容纳MaxCustSingleLine位乘客。
 * 当等待安检的乘客人流量超过MaxCustSingleLine时，系统自动调整排队缓冲区为蛇形
 * 缓冲区，且蛇形缓冲区的形态会根据排队乘客数量进行动态调整。蛇形缓冲区最多由MaxLines
 * 个直队构成，如果排队乘客数超过了排队缓冲区最大容量，则剩下的乘客不允许进入排队缓冲区。
 */
public class WaitInLineBuffer {
    private int MaxCustSingleLine;
    private int MaxLines;

    /**
     * @param maxCustSingleLine: 一个直队最多能容纳MaxCustSingleLine位乘客
     * @param maxLines: 蛇形缓冲区最多由MaxLine个直队构成
     */
    public WaitInLineBuffer(int maxCustSingleLine, int maxLines) {
        MaxCustSingleLine = maxCustSingleLine;
        MaxLines = maxLines;
    }
}
