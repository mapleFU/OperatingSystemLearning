package Controller.Command;

public class RecoverCommand extends Command {
    @Override
    public void excute(int arg) {
        System.out.println("C excuted: arg" + arg);
    }
}
