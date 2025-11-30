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
 * Test class for verifying SafeWaterMark functionality.
 * Tests initialization and XML persistence.
 */
class SafeWaterMarkTest {

    @TempDir
    Path tempDir;

    /**
     * Tests that a SafeWaterMark object is correctly initialized with values.
     */
    @Test
    @DisplayName("Test initialization of SafeWaterMark")
    void testSafeWaterMarkInitialization() {
        // 1. ARRANGE
        Position pos = new Position(53.5, 8.5, 0.0);
        FormDummy geometry = new FormDummy();
        String markName = "Channel Entry Mark";
        String marking = "CE";
        SafeWaterMarkShape shape = SafeWaterMarkShape.SPHERICAL;

        // 2. ACT
        SafeWaterMark safeWaterMark = new SafeWaterMark(
                markName,
                pos,
                geometry,
                shape,
                marking,
                null // no light signal
        );

        // 3. ASSERT
        assertNotNull(safeWaterMark, "SafeWaterMark object should be created");
        assertEquals(markName, safeWaterMark.getName().getValue(),
                "Name should be initialized correctly");
        assertEquals(shape, safeWaterMark.getShape().getValue(),
                "Shape should be initialized correctly");
        assertEquals(marking, safeWaterMark.getMarking().getValue(),
                "Marking should be initialized correctly");
        assertEquals(SafeWaterMarkColor.RED_WHITE_VERTICAL_STRIPES, safeWaterMark.getColor().getValue(),
                "Safe water marks should have red/white vertical stripes");
        assertFalse(safeWaterMark.isLit(),
                "Mark without light signal should not be lit");
        assertTrue(safeWaterMark.getPhysical().getValue(),
                "SafeWaterMark should be physical");
    }

    /**
     * Tests XML persistence (marshalling and unmarshalling) for SafeWaterMark.
     */
    @Test
    @DisplayName("Test XML persistence (Marshalling) for SafeWaterMark")
    void testSafeWaterMarkMarshalling() throws JAXBException {
        // 1. ARRANGE
        Position pos = new Position(54.0, 8.0, 0.0);
        FormDummy geometry = new FormDummy();
        String expectedName = "Fairway Mark";
        String expectedMarking = "FW";

        SafeWaterMark originalMark = new SafeWaterMark(
                expectedName,
                pos,
                geometry,
                SafeWaterMarkShape.PILLAR,
                expectedMarking,
                null
        );

        ScenarioDTO scenario = new ScenarioDTO();
        scenario.addSimulationObject(originalMark);

        File xmlFile = tempDir.resolve("SafeWaterMarkTest.xml").toFile();

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
        assertTrue(loadedObj instanceof SafeWaterMark, "Loaded object must be of type SafeWaterMark");

        SafeWaterMark loadedMark = (SafeWaterMark) loadedObj;
        assertEquals(expectedName, loadedMark.getName().getValue(),
                "Error: Name was not correctly restored.");
        assertEquals(expectedMarking, loadedMark.getMarking().getValue(),
                "Error: Marking was not correctly restored.");
        assertEquals(SafeWaterMarkShape.PILLAR, loadedMark.getShape().getValue(),
                "Error: Shape was not correctly restored.");
    }
}

