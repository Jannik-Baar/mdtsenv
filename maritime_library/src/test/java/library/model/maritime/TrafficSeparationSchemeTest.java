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
 * Test class for verifying TrafficSeparationScheme functionality.
 * Tests initialization and XML persistence.
 */
class TrafficSeparationSchemeTest {

    @TempDir
    Path tempDir;

    /**
     * Tests that a TrafficSeparationScheme object is correctly initialized with values.
     */
    @Test
    @DisplayName("Test initialization of TrafficSeparationScheme")
    void testTrafficSeparationSchemeInitialization() {
        // 1. ARRANGE
        Position pos = new Position(53.5, 8.5, 0.0);
        FormDummy geometry = new FormDummy();
        String tssName = "North Sea TSS";
        TrafficSeparationSchemeZoneType zoneType = TrafficSeparationSchemeZoneType.TRAFFIC_LANE;
        TrafficSeparationSchemeTrafficDirection direction = TrafficSeparationSchemeTrafficDirection.NORTH_TO_SOUTH;

        // 2. ACT
        TrafficSeparationScheme tss = new TrafficSeparationScheme(
                false,
                pos,
                geometry,
                0.0,
                zoneType,
                direction,
                tssName
        );

        // 3. ASSERT
        assertNotNull(tss, "TrafficSeparationScheme object should be created");
        assertEquals(zoneType, tss.getZoneType().getValue(),
                "ZoneType should be initialized correctly");
        assertEquals(direction, tss.getTrafficDirection().getValue(),
                "TrafficDirection should be initialized correctly");
        assertEquals(tssName, tss.getTssName().getValue(),
                "TSS name should be initialized correctly");
        assertFalse(tss.isEntryProhibited(),
                "Traffic lane should not prohibit entry");
        
        // Test heading compliance
        assertTrue(tss.isHeadingCompliant(180.0, 10.0),
                "Heading 180° should be compliant for NORTH_TO_SOUTH");
        assertFalse(tss.isHeadingCompliant(0.0, 10.0),
                "Heading 0° should not be compliant for NORTH_TO_SOUTH");
    }

    /**
     * Tests that a separation zone prohibits entry.
     */
    @Test
    @DisplayName("Test separation zone entry prohibition")
    void testSeparationZoneEntryProhibition() {
        // 1. ARRANGE
        Position pos = new Position(53.0, 8.0, 0.0);
        FormDummy geometry = new FormDummy();

        // 2. ACT
        TrafficSeparationScheme separationZone = new TrafficSeparationScheme(
                false,
                pos,
                geometry,
                0.0,
                TrafficSeparationSchemeZoneType.SEPARATION_ZONE,
                TrafficSeparationSchemeTrafficDirection.NO_RESTRICTION,
                "Test Separation Zone"
        );

        // 3. ASSERT
        assertTrue(separationZone.isEntryProhibited(),
                "Separation zone should prohibit entry");
    }

    /**
     * Tests XML persistence (marshalling and unmarshalling) for TrafficSeparationScheme.
     */
    @Test
    @DisplayName("Test XML persistence (Marshalling) for TrafficSeparationScheme")
    void testTrafficSeparationSchemeMarshalling() throws JAXBException {
        // 1. ARRANGE
        Position pos = new Position(54.0, 8.0, 0.0);
        FormDummy geometry = new FormDummy();
        String expectedName = "Baltic Sea TSS";

        TrafficSeparationScheme originalTSS = new TrafficSeparationScheme(
                false,
                pos,
                geometry,
                0.0,
                TrafficSeparationSchemeZoneType.INSHORE_TRAFFIC_ZONE,
                TrafficSeparationSchemeTrafficDirection.BIDIRECTIONAL,
                expectedName
        );

        ScenarioDTO scenario = new ScenarioDTO();
        scenario.addSimulationObject(originalTSS);

        File xmlFile = tempDir.resolve("TrafficSeparationSchemeTest.xml").toFile();

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
        assertTrue(loadedObj instanceof TrafficSeparationScheme, "Loaded object must be of type TrafficSeparationScheme");

        TrafficSeparationScheme loadedTSS = (TrafficSeparationScheme) loadedObj;
        assertEquals(expectedName, loadedTSS.getTssName().getValue(),
                "Error: TSS name was not correctly restored.");
        assertEquals(TrafficSeparationSchemeZoneType.INSHORE_TRAFFIC_ZONE, loadedTSS.getZoneType().getValue(),
                "Error: ZoneType was not correctly restored.");
        assertEquals(TrafficSeparationSchemeTrafficDirection.BIDIRECTIONAL, loadedTSS.getTrafficDirection().getValue(),
                "Error: TrafficDirection was not correctly restored.");
    }
}

