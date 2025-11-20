package library.services.logging;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Example Implementation of a Logger via java.util.logging
 */
public class ExampleLogger implements ILogger {

    private File file;
    private FileHandler fileHandler;
    private Logger logger = Logger.getLogger("logger");

    /**
     * Creates a log file and logs an initial Message
     */
    public ExampleLogger() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm");
        LocalDateTime now = LocalDateTime.now();
        this.file = new File(dtf.format(now) + "_test.log");
        try {
            this.file.createNewFile();
            fileHandler = new FileHandler(file.getPath());
            logger.addHandler(fileHandler);
            logger.setUseParentHandlers(false);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
            logger.info("initial log");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Logs the provided data String in the Logfile
     * @param data
     * @throws IOException
     */
    @Override
    public void logData(String data) throws IOException {
        logger.info(data);
    }

    /**
     * Deletes the Logfile
     */
    public void deleteLog(){
        this.file.deleteOnExit();
    }

    /**
     * Closes the handlers responsible for the logger
     */
    @Override
    public void close(){
        for(Handler h : logger.getHandlers()){
            h.close();
        }
    }

}
