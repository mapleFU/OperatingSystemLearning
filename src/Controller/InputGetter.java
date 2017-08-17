/**
 * Get input from command line
 */
package Controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import Controller.Command.*;

public class InputGetter extends Thread {
    // Data
    Scanner in;
    Map<Character, Command> commandMap;
    /**
     * Constructor
     */
    public InputGetter() {
         in = new Scanner(System.in);
         commandMap = new HashMap<>();

         // set command
         commandMap.put('G', new GetInCommand());
         commandMap.put('R', new RestCommand());
         commandMap.put('C', new RecoverCommand());
         commandMap.put('Q', new QuitCommand());
    }

    @Override
    public void run() {
        while (true) {
            Character code = in.next().charAt(0);
            Command cmd = commandMap.get(code);
            if (in.hasNextInt()) {
                int value = in.nextInt();
                cmd.excute();
            } else {
                cmd.excute();
            }
        }
    }
}
