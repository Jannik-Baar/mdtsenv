package library.model.maritime;

import library.model.dto.scenario.ScenarioDTO;
import library.model.simulation.Position;
import library.model.simulation.FormDummy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for verifying Beacon functionality.
 * Tests initialization and XML persistence.
 */
class BeaconTest {

    @TempDir
    Path tempDir;

    /**
     * Tests that a Beacon object is correctly initialized with values.
     */
    @Test
    @DisplayName("Test initialization of Beacon")
    void testBeaconInitialization() {
        // 1. ARRANGE
        Position pos = new Position(53.5, 8.5, 0.0);
        FormDummy geometry = new FormDummy();
        String beaconName = "North Pier Beacon";
        BeaconType beaconType = BeaconType.LATERAL_PORT;
        BeaconShape shape = BeaconShape.TOWER;
        BeaconColor color = BeaconColor.RED;
        Region region = Region.REGION_A;

        // 2. ACT
        Beacon beacon = new Beacon(
                beaconName,
                pos,
                geometry,
                beaconType,
                shape,
                color,
                region,
                null // no light signal
        );

        // 3. ASSERT
        assertNotNull(beacon, "Beacon object should be created");
        assertEquals(beaconName, beacon.getName().getValue(),
                "Name should be initialized correctly");
        assertEquals(beaconType, beacon.getBeaconType().getValue(),
                "BeaconType should be initialized correctly");
        assertEquals(shape, beacon.getShape().getValue(),
                "Shape should be initialized correctly");
        assertEquals(color, beacon.getColor().getValue(),
                "Color should be initialized correctly");
        assertEquals(region, beacon.getRegion().getValue(),
                "Region should be initialized correctly");
        assertTrue(beacon.getPhysical().getValue(),
                "Beacon should be physical");
    }

    /**
     * Tests XML persistence (marshalling and unmarshalling) for Beacon.
     */
    @Test
    @DisplayName("Test XML persistence (Marshalling) for Beacon")
    void testBeaconMarshalling() throws JAXBException {
        // 1. ARRANGE
        Position pos = new Position(54.0, 8.0, 0.0);
        FormDummy geometry = new FormDummy();
        String expectedName = "Test Beacon";

        Beacon originalBeacon = new Beacon(
                expectedName,
                pos,
                geometry,
                BeaconType.CARDINAL_NORTH,
                BeaconShape.LATTICE,
                BeaconColor.BLACK_YELLOW_BANDS,
                Region.REGION_A,
                null
        );

        ScenarioDTO scenario = new ScenarioDTO();
        scenario.addSimulationObject(originalBeacon);

        File xmlFile = tempDir.resolve("BeaconTest.xml").toFile();

        // 2. ACT
        JAXBContext context = JAXBContext.newInstance("library.model");

        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(scenario, xmlFile);

        Unmarshaller unmarshaller = context.createUnmarshaller();
        ScenarioDTO loadedScenario = (ScenarioDTO) unmarshaller.unmarshal(xmlFile);

        // 3. ASSERT
        assertNotNull(loadedScenario.getSimulationObjects(), "List of objects should not be null");
        assertEquals(1, loadedScenario.getSimulationObjects().size(), "Exactly one object should be loaded");

        Object loadedObj = loadedScenario.getSimulationObjects().get(0);
        assertTrue(loadedObj instanceof Beacon, "Loaded object must be of type Beacon");

        Beacon loadedBeacon = (Beacon) loadedObj;
        assertEquals(expectedName, loadedBeacon.getName().getValue(),
                "Error: Name was not correctly restored.");
        assertEquals(BeaconType.CARDINAL_NORTH, loadedBeacon.getBeaconType().getValue(),
                "Error: BeaconType was not correctly restored.");
    }
}

