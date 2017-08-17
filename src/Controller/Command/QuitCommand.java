package Controller.Command;

class QuitRun extends RuntimeException {

}

public class QuitCommand extends Command {
    @Override
    public void excute() {
        throw new QuitRun();
    }
}

