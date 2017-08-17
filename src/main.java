import Controller.InputGetter;

public class main {
    public static void main(String[] args) {
        Thread t = new Thread(new InputGetter());
        t.start();
    }
}
