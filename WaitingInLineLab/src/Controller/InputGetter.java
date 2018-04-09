/**
 * Get input from command line
 */
package Controller;

import java.io.StringReader;
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
        System.out.println("Run start.");
        while (true) {
            String[] codes = in.nextLine().split(" ");
            Character code = codes[0].charAt(0);

            Command cmd = commandMap.get(code);
            System.out.println("Got it.");
            if (codes.length == 2) {
                int value = Integer.parseInt(codes[1]);
                cmd.excute(value);
            } else {
                cmd.excute();
            }
        }
    }
}
