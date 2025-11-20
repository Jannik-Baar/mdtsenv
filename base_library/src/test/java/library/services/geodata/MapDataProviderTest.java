package library.services.geodata;

import library.model.dto.scenario.ScenarioDTO;
import library.model.simulation.Position;
import library.model.traffic.Infrastructure;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.io.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MapDataProviderTest {

    @Test
    public void getInfrastructureAtPosition() throws IOException, ParseException {
        GeoPackageReader geoPackageReader = new GeoPackageReader();
        ScenarioDTO scenario = new ScenarioDTO();
        ArrayList<Infrastructure> infrastructures = (new GeoPackageReader()).read(GeoPackageReader.class.getResource("/library/services/geodata/EPSG4326_WGS84.gpkg").getPath());
        infrastructures.get(0).getFormString();
        infrastructures.forEach(scenario::addSimulationObject);
        MapDataProvider mapDataProvider = new MapDataProvider(scenario);
        List<Infrastructure> infrastructures1 = mapDataProvider.getInfrastructureAtPosition(new Position(8.48677, 53.22510, 0));
        assertEquals(1, infrastructures1.size());
    }

}