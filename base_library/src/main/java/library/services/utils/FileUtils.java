package library.services.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;

public class FileUtils {

    public static File createFile(String path, String filename) throws IOException {
        return createFile(getFullPath(path, filename));
    }

    public static File createXmlFile(String path, String filename) throws IOException {
        return createFile(getFullXmlPath(path, filename));
    }

    public static File createFile(String fullPath) throws IOException {
        File file = new File(fullPath);
        try {
            if (file.createNewFile()) {
                return file;
            }
            throw new FileAlreadyExistsException(fullPath);
        } catch (IOException e) {
            e.printStackTrace(); // TODO proper logging
            throw e;
        }
    }

    public static String getFullPath(String path, String filename) {
        path = path.endsWith("/") ? path : path + "/";
        String fullPath = path + filename;
        return fullPath;
    }

    public static String getFullXmlPath(String path, String filename) {
        String fullPath = getFullPath(path, filename);
        return getXmlPath(fullPath);
    }

    public static String getXmlPath(String path) {
        return path.endsWith(".xml") ? path : path + ".xml";
    }

}
