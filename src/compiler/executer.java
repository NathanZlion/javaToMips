package compiler;

import java.io.FileWriter;
import java.io.IOException;

public class executer {

    public static void execute(String code) {
        try {
            String dir = System.getProperty("user.dir");

            // Create a file named assembly.asm and write the code to it
            FileWriter writer = new FileWriter(dir + "\\src\\compiler\\assembly.asm", false);

            writer.write(code);
            writer.close();
            // Build the command to run

            String[] command = {"cmd.exe", "/c", "start", "cmd.exe", "/K", "cd "+ dir +" \\src\\compiler && java -jar Mars4_5.jar sm assembly.asm"};            
            // Open command prompt and run commands
            try {
                Runtime.getRuntime().exec(command);
            } catch (IOException e) {
                System.out.println("Error running command: " + e.getMessage());
            }

        } catch (IOException e) {
            System.out.println("Failed to open cmd");
        }
    }

}
