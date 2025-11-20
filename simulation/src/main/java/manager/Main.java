package manager;

import java.io.IOException;

/**
 * This opens a command line and runs some other class in the jar
 *
 * @author Brandon Barajas
 */
public class Main {
    public static void main(String[] args) throws IOException {
        SimulationManager.main(args);
        System.out.println("Program has ended, please type 'exit' to close the console");
    }
}