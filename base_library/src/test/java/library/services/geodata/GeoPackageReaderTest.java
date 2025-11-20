package library.services.geodata;

import library.model.traffic.Infrastructure;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;


class GeoPackageReaderTest {

    @Test
    void read() throws IOException {
        ArrayList<Infrastructure> infrastructures = (new GeoPackageReader()).read(GeoPackageReader.class.getResource("/library/services/geodata/EPSG4326_WGS84.gpkg").getPath());
        assertEquals(123, infrastructures.size());
    }
}