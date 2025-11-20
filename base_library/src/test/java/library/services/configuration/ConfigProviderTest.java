package library.services.configuration;

import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.util.Properties;

class ConfigProviderTest {

    @org.junit.jupiter.api.Test
    void loadTestConfiguration() throws IOException {
        String propValue = new ConfigProvider().loadConfig(ClassLoader.getSystemResource("library/services/configuration/test-sim.config").getPath(),"pg.name");
        Assertions.assertEquals(propValue,"MTSS");
    }
}