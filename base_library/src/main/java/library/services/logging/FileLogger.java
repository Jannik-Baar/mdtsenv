package library.services.logging;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * FileLogger with minimal overhead
 */
public class FileLogger implements ILogger{

    private File outputFile;
    private BufferedWriter bufferedWriter;

    public FileLogger(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm");
        LocalDateTime now = LocalDateTime.now();
        this.outputFile = new File(dtf.format(now) + "_sim.log");
        try {
            this.bufferedWriter = new BufferedWriter(new FileWriter(outputFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void logData(String data) throws IOException {
        bufferedWriter.write(data + "\n");
    }

    @Override
    public void close() {
        try {
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteLog(){
        this.outputFile.deleteOnExit();
    }
}
