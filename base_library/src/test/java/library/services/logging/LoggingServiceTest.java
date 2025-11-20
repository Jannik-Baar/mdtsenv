package library.services.logging;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

class LoggingServiceTest {

    @org.junit.jupiter.api.Test
    void loggingServiceTest(){
        LoggingService loggingService = new LoggingService();
        loggingService.registerLogger(LoggingType.UDP);
        loggingService.registerLogger(LoggingType.EXAMPLE);
        loggingService.registerLogger(LoggingType.FILE);
        loggingService.registerLogger(LoggingType.CONSOLE);
        loggingService.logWithAll("Example Log Entry");
        loggingService.logWithAll("Second Example Log Entry");
        loggingService.log(LoggingType.CONSOLE,"Example Single Log Entry");
        //remove if you want to manually verify the log
        deleteLogFiles(loggingService);
        loggingService.removeAllLoggers();
    }

    //Attempts to call the deleteLog() methods on each Logger if it exists
    void deleteLogFiles(LoggingService ls) {
        ArrayList<ILogger> loggers = new ArrayList<>(ls.getLoggers());
        for (ILogger logger : loggers) {
            for (Method method : logger.getClass().getMethods()
            ) {
                if (method.getName().equals("deleteLog")) {
                    try {
                        method.invoke(logger);
                    } catch (IllegalAccessException|InvocationTargetException  e) {
                        e.printStackTrace();
                    }
                }
                if (method.getName().equals("close")) {
                    try {
                        method.invoke(logger);
                    } catch (IllegalAccessException|InvocationTargetException  e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }
}