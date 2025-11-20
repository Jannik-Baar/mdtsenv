package library.services.logging;

import java.io.IOException;

/**
 * Every Logger Class needs to provide a logData Method
 */
public interface ILogger {
    void logData(String data) throws IOException;
    void close();
}
