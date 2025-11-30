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
 * Test class for verifying LateralMark functionality.
 * Tests initialization and XML persistence.
 */
class LateralMarkTest {

    @TempDir
    Path tempDir;

    /**
     * Tests that a LateralMark object is correctly initialized with values.
     */
    @Test
    @DisplayName("Test initialization of LateralMark")
    void testLateralMarkInitialization() {
        // 1. ARRANGE
        Position pos = new Position(53.5, 8.5, 0.0);
        FormDummy geometry = new FormDummy();
        String markName = "Port Mark 1";
        LateralMarkType markType = LateralMarkType.PORT_HAND;
        Region region = Region.REGION_A;
        String number = "P1";

        // 2. ACT
        LateralMark lateralMark = new LateralMark(
                markName,
                pos,
                geometry,
                markType,
                region,
                number,
                null, // no light signal
                null  // default shape
        );

        // 3. ASSERT
        assertNotNull(lateralMark, "LateralMark object should be created");
        assertEquals(markName, lateralMark.getName().getValue(),
                "Name should be initialized correctly");
        assertEquals(markType, lateralMark.getMarkType().getValue(),
                "MarkType should be initialized correctly");
        assertEquals(region, lateralMark.getRegion().getValue(),
                "Region should be initialized correctly");
        assertEquals(number, lateralMark.getNumber().getValue(),
                "Number should be initialized correctly");
        
        // Verify IALA Region A color conventions
        assertEquals(LateralMarkColor.RED, lateralMark.getColor().getValue(),
                "Port hand mark in Region A should be red");
        assertEquals(LateralMarkShape.CAN, lateralMark.getShape().getValue(),
                "Port hand mark should have CAN shape");
        assertTrue(lateralMark.getPhysical().getValue(),
                "LateralMark should be physical");
    }

    /**
     * Tests XML persistence (marshalling and unmarshalling) for LateralMark.
     */
    @Test
    @DisplayName("Test XML persistence (Marshalling) for LateralMark")
    void testLateralMarkMarshalling() throws JAXBException {
        // 1. ARRANGE
        Position pos = new Position(54.0, 8.0, 0.0);
        FormDummy geometry = new FormDummy();
        String expectedName = "Starboard Mark";
        String expectedNumber = "S2";

        LateralMark originalMark = new LateralMark(
                expectedName,
                pos,
                geometry,
                LateralMarkType.STARBOARD_HAND,
                Region.REGION_A,
                expectedNumber,
                null,
                null
        );

        ScenarioDTO scenario = new ScenarioDTO();
        scenario.addSimulationObject(originalMark);

        File xmlFile = tempDir.resolve("LateralMarkTest.xml").toFile();

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
        assertTrue(loadedObj instanceof LateralMark, "Loaded object must be of type LateralMark");

        LateralMark loadedMark = (LateralMark) loadedObj;
        assertEquals(expectedName, loadedMark.getName().getValue(),
                "Error: Name was not correctly restored.");
        assertEquals(expectedNumber, loadedMark.getNumber().getValue(),
                "Error: Number was not correctly restored.");
        assertEquals(LateralMarkType.STARBOARD_HAND, loadedMark.getMarkType().getValue(),
                "Error: MarkType was not correctly restored.");
    }
}

