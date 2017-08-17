package Controller.Command;

/**
 * 异常: 未完成的类
 */
class UnfillException extends RuntimeException {

}

public class Command {
    public void excute() { throw new UnfillException();}
    public void excute(int arg) { throw new UnfillException(); }
}
