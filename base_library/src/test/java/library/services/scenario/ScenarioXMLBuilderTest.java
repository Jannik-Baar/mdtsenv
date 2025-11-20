package library.services.scenario;

import library.model.dto.scenario.ScenarioDTO;
import library.model.simulation.objects.ActiveSimulationObject;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import utils.TestUtils;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class ScenarioXMLBuilderTest {

    @org.junit.jupiter.api.Test
    void generateTestScenarioXML() throws IOException, JAXBException {

        File dir = new File("scenarios");
        if (!dir.exists()) {
            dir.mkdirs();
        } else if (!dir.isDirectory()) {
            fail("Testordner kann nicht angelegt werden, da bereits eine Datei mit dem gleichen Namen vorhanden ist");
        }

        try {
            ScenarioDTO testScenario = TestUtils.generateTestXML("scenarios", "test");

            File file2 = new File("scenarios/test.xml");

            if (!file2.isFile()) {
                fail("XML wurde nicht im richten Pfad abgelegt");
            }

            file2.deleteOnExit();

            ScenarioDTO scenario = ScenarioConverter.convertToScenarioModel("scenarios/test");
            assertEquals(testScenario.getSimulationObjects().size(), scenario.getSimulationObjects().size());
            for (int i = 0; i < testScenario.getSimulationObjects().size(); i++){
                assertEquals(testScenario.getSimulationObjects().get(i).getClass(), scenario.getSimulationObjects().get(i).getClass());
                if (testScenario.getSimulationObjects().get(i) instanceof ActiveSimulationObject && scenario.getSimulationObjects().get(i) instanceof ActiveSimulationObject) {
                    assertEquals(((ActiveSimulationObject) testScenario.getSimulationObjects().get(i)).getComponents(),
                            ((ActiveSimulationObject) scenario.getSimulationObjects().get(i)).getComponents().size());
                }
            }
            assertTrue(new ReflectionEquals(scenario.getSimulationObjects()).matches(testScenario.getSimulationObjects()));

        } catch (JAXBException|IOException e) {
            e.printStackTrace();
        }
    }
}