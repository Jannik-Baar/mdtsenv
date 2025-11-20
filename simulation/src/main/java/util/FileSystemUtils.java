package util;

import java.io.File;
import java.io.IOException;

@SuppressWarnings("unused")
public class FileSystemUtils {

    public static boolean isPathFile(String path) {
        File file = new File(path);
        return file.exists() && file.isFile();
    }

    public static boolean isPathDirectory(String path) {
        File directory = new File(path);
        return directory.exists() && directory.isDirectory();
    }

    /**
     * creates a directory if it does not exist yet or else returns the existing one
     *
     * @param directoryPath
     * @return
     * @throws IOException
     */
    public static File createDirectory(String path) throws IOException {
        File dir = new File(path);
        if (dir.exists()) {
            return dir;
        }
        if (dir.mkdirs()) {
            return dir;
        }
        throw new IOException("Failed to create directory '" + dir.getAbsolutePath() + "' for an unknown reason.");
    }

}
