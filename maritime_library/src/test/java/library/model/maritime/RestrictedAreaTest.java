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
 * Test class for verifying RestrictedArea functionality.
 * Tests initialization and XML persistence.
 */
class RestrictedAreaTest {

    @TempDir
    Path tempDir;

    /**
     * Tests that a RestrictedArea object is correctly initialized with values.
     */
    @Test
    @DisplayName("Test initialization of RestrictedArea")
    void testRestrictedAreaInitialization() {
        // 1. ARRANGE
        Position pos = new Position(53.5, 8.5, 0.0);
        FormDummy geometry = new FormDummy();
        String areaName = "Military Exercise Zone";
        String reason = "Naval exercises";
        RestrictedAreaType restrictionType = RestrictedAreaType.MILITARY_ZONE;

        // 2. ACT
        RestrictedArea restrictedArea = new RestrictedArea(
                false,
                pos,
                geometry,
                0.0,
                restrictionType,
                areaName,
                reason,
                false // temporary
        );

        // 3. ASSERT
        assertNotNull(restrictedArea, "RestrictedArea object should be created");
        assertEquals(restrictionType, restrictedArea.getRestrictionType().getValue(),
                "RestrictionType should be initialized correctly");
        assertEquals(areaName, restrictedArea.getAreaName().getValue(),
                "Area name should be initialized correctly");
        assertEquals(reason, restrictedArea.getReason().getValue(),
                "Reason should be initialized correctly");
        assertFalse(restrictedArea.getIsPermanent().getValue(),
                "IsPermanent should be false");
        assertTrue(restrictedArea.isEntryProhibited(),
                "Military zone should prohibit entry");
        assertTrue(restrictedArea.isAnchoringProhibited(),
                "Military zone should prohibit anchoring");
    }

    /**
     * Tests XML persistence (marshalling and unmarshalling) for RestrictedArea.
     */
    @Test
    @DisplayName("Test XML persistence (Marshalling) for RestrictedArea")
    void testRestrictedAreaMarshalling() throws JAXBException {
        // 1. ARRANGE
        Position pos = new Position(54.0, 8.0, 0.0);
        FormDummy geometry = new FormDummy();
        String expectedName = "Conservation Area";
        String expectedReason = "Marine wildlife protection";

        RestrictedArea originalArea = new RestrictedArea(
                false,
                pos,
                geometry,
                0.0,
                RestrictedAreaType.NATURE_CONSERVATION,
                expectedName,
                expectedReason,
                true
        );

        ScenarioDTO scenario = new ScenarioDTO();
        scenario.addSimulationObject(originalArea);

        File xmlFile = tempDir.resolve("RestrictedAreaTest.xml").toFile();

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
        assertTrue(loadedObj instanceof RestrictedArea, "Loaded object must be of type RestrictedArea");

        RestrictedArea loadedArea = (RestrictedArea) loadedObj;
        assertEquals(expectedName, loadedArea.getAreaName().getValue(),
                "Error: Area name was not correctly restored.");
        assertEquals(expectedReason, loadedArea.getReason().getValue(),
                "Error: Reason was not correctly restored.");
        assertEquals(RestrictedAreaType.NATURE_CONSERVATION, loadedArea.getRestrictionType().getValue(),
                "Error: RestrictionType was not correctly restored.");
        assertTrue(loadedArea.getIsPermanent().getValue(),
                "Error: IsPermanent was not correctly restored.");
    }
}

