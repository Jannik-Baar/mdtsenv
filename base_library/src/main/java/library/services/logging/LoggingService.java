package library.services.logging;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashMap;

/**
 * Holds all loggers and is able to command every single one to log a specific data string
 */
public class LoggingService {
    private static HashMap<LoggingType, ILogger> loggers = new HashMap<>();

    public LoggingService() {
    }

    public LoggingService(HashMap<LoggingType, ILogger> loggers) {
        this.loggers = loggers;
    }

    public static void log(String data) {
        logWithAll(data);
    }

    /**
     * Logs the given data with every Logger available
     *
     * @param data
     * @throws IOException
     */
    public static void logWithAll(String data) {
        for (ILogger logger : loggers.values()) {
            try {
                logger.logData(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Logs a string in a specific logger
     *
     * @param loggingType
     * @param data
     */
    public static void log(LoggingType loggingType, String data) {
        try {
            if (loggers.containsKey(loggingType)) {
                loggers.get(loggingType).logData(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Registers a logger based on the given enum parameter
     *
     * @param loggingType
     */
    public static void registerLogger(LoggingType loggingType) {
        switch (loggingType) {
            case UDP:
                try {
                    loggers.put(loggingType, new UDPLogger());
                } catch (UnknownHostException | SocketException e) {
                    e.printStackTrace();
                }
                return;
            case EXAMPLE:
                loggers.put(loggingType, new ExampleLogger());
                return;
            case FILE:
                loggers.put(loggingType, new FileLogger());
                return;
            case CONSOLE:
                loggers.put(loggingType, new ConsoleLogger());
                return;
            default:
                return;

        }
    }

    /**
     * closes and removes all loggers
     */
    public static void removeAllLoggers() {
        for (ILogger logger : loggers.values()) {
            logger.close();
        }
        loggers = new HashMap<>();
    }

    /**
     * Removes a logger based on its loggingMethod
     *
     * @param loggingType
     */
    public static void removeLogger(LoggingType loggingType) {
        loggers.get(loggingType).close();
        loggers.remove(loggingType);
    }

    public static Collection<ILogger> getLoggers() {
        return loggers.values();
    }
}
