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
 * Test class for verifying Lighthouse functionality.
 * Tests initialization and XML persistence.
 */
class LighthouseTest {

    @TempDir
    Path tempDir;

    /**
     * Tests that a Lighthouse object is correctly initialized with values.
     */
    @Test
    @DisplayName("Test initialization of Lighthouse")
    void testLighthouseInitialization() {
        // 1. ARRANGE
        Position pos = new Position(53.5, 8.5, 0.0);
        FormDummy geometry = new FormDummy();
        String lighthouseName = "North Sea Lighthouse";
        double height = 45.0;
        LightColor lightColor = LightColor.WHITE;
        double nominalRange = 15.0;

        // 2. ACT
        Lighthouse lighthouse = new Lighthouse(
                lighthouseName,
                pos,
                geometry,
                height,
                lightColor,
                nominalRange
        );

        // 3. ASSERT
        assertNotNull(lighthouse, "Lighthouse object should be created");
        assertEquals(lighthouseName, lighthouse.getName().getValue(),
                "Name should be initialized correctly");
        assertEquals(height, lighthouse.getHeight().getValue(),
                "Height should be initialized correctly");
        assertTrue(lighthouse.getIsActive().getValue(),
                "Lighthouse should be active by default");
        assertNotNull(lighthouse.getLightSignal(),
                "LightSignal should be initialized");
        assertEquals(lightColor, lighthouse.getLightSignal().getColor().getValue(),
                "Light color should be initialized correctly");
        assertTrue(lighthouse.getPhysical().getValue(),
                "Lighthouse should be physical");
    }

    /**
     * Tests XML persistence (marshalling and unmarshalling) for Lighthouse.
     */
    @Test
    @DisplayName("Test XML persistence (Marshalling) for Lighthouse")
    void testLighthouseMarshalling() throws JAXBException {
        // 1. ARRANGE
        Position pos = new Position(54.0, 8.0, 0.0);
        FormDummy geometry = new FormDummy();
        String expectedName = "Test Lighthouse";
        double expectedHeight = 50.0;

        Lighthouse originalLighthouse = new Lighthouse(
                expectedName,
                pos,
                geometry,
                expectedHeight,
                LightColor.RED,
                LighthousePattern.OCCULTING,
                8.0,
                20.0,
                true
        );

        ScenarioDTO scenario = new ScenarioDTO();
        scenario.addSimulationObject(originalLighthouse);

        File xmlFile = tempDir.resolve("LighthouseTest.xml").toFile();

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
        assertTrue(loadedObj instanceof Lighthouse, "Loaded object must be of type Lighthouse");

        Lighthouse loadedLighthouse = (Lighthouse) loadedObj;
        assertEquals(expectedName, loadedLighthouse.getName().getValue(),
                "Error: Name was not correctly restored.");
        assertEquals(expectedHeight, loadedLighthouse.getHeight().getValue(),
                "Error: Height was not correctly restored.");
        assertTrue(loadedLighthouse.getIsActive().getValue(),
                "Error: IsActive was not correctly restored.");
    }
}

