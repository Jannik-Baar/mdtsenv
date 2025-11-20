package library.services.logging;

/**
 * A basic Logger for logging to the console
 */
public class ConsoleLogger implements ILogger{

    public ConsoleLogger() {

    }

    @Override
    public void logData(String data) {
        System.out.println(data);
    }

    @Override
    public void close() {

    }
}
