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
 * Test class for verifying Channel functionality.
 * Tests initialization and XML persistence.
 */
class ChannelTest {

    @TempDir
    Path tempDir;

    /**
     * Tests that a Channel object is correctly initialized with values.
     */
    @Test
    @DisplayName("Test initialization of Channel")
    void testChannelInitialization() {
        // 1. ARRANGE
        Position pos = new Position(53.0, 8.0, 0.0);
        FormDummy geometry = new FormDummy();

        // 2. ACT
        Channel channel = new Channel(false, pos, geometry, 0.0);

        // 3. ASSERT
        assertNotNull(channel, "Channel object should be created");
        assertNotNull(channel.getBeacons(), "Beacons list should be initialized");
        assertNotNull(channel.getLateralMarks(), "LateralMarks list should be initialized");
        assertNotNull(channel.getLighthouses(), "Lighthouses list should be initialized");
        assertNotNull(channel.getSafeWaterMarks(), "SafeWaterMarks list should be initialized");
        assertTrue(channel.getBeacons().isEmpty(), "Beacons list should be empty");
        assertFalse(channel.getPhysical().getValue(), "Channel should not be physical");
    }

    /**
     * Tests XML persistence (marshalling and unmarshalling) for Channel.
     */
    @Test
    @DisplayName("Test XML persistence (Marshalling) for Channel")
    void testChannelMarshalling() throws JAXBException {
        // 1. ARRANGE
        Position pos = new Position(54.0, 8.0, 0.0);
        FormDummy geometry = new FormDummy();

        Channel originalChannel = new Channel(false, pos, geometry, 0.0);

        ScenarioDTO scenario = new ScenarioDTO();
        scenario.addSimulationObject(originalChannel);

        File xmlFile = tempDir.resolve("ChannelTest.xml").toFile();

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
        assertTrue(loadedObj instanceof Channel, "Loaded object must be of type Channel");

        Channel loadedChannel = (Channel) loadedObj;
        assertNotNull(loadedChannel.getBeacons(), "Beacons list should be initialized");
        assertFalse(loadedChannel.getPhysical().getValue(),
                "Error: Physical property was not correctly restored.");
    }
}

