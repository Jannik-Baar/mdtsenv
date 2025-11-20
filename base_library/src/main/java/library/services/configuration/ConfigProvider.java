package library.services.configuration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * A class used for loading a config file property from a specified path.
 */
public class ConfigProvider {

    public ConfigProvider() {
    }

    /**
     * Loads a specified config file from resources with a given URL
     *
     * @param propertyKey the Path to the configuration file
     * @return Properties
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static String loadConfig(String pathToProperty, String propertyKey) throws FileNotFoundException, IOException {
        Properties prop = new Properties();
        InputStream inputStream = new FileInputStream(pathToProperty);
        prop.load(inputStream);
        return prop.getProperty(propertyKey);
    }
}
