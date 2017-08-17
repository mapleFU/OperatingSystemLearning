package Controller.Command;

public class RestCommand extends Command {
    @Override
    public void excute(int arg) {
        System.out.println("R excuted" + arg);
    }
}
