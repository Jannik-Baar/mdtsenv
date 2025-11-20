package utils;

import library.model.dto.scenario.ScenarioDTO;
import library.services.scenario.ScenarioConverter;

import javax.xml.bind.JAXBException;
import java.io.IOException;

public class TestUtils {

    /**
     * Generates a minimalistic scenario.xml for testing.
     *
     * @param path         path where the scenario.xml should be instantiated
     * @param scenarioName the name of the scenario
     * @throws JAXBException is thrown if the jaxb.index can't ne found in the resources path
     * @throws IOException   is thrown by the FileReader
     */
    public static ScenarioDTO generateTestXML(String path, String scenarioName) throws JAXBException, IOException {
        ScenarioDTO testScenario = new ScenarioDTO();
        testScenario.setSimulationIterations(40000);
        testScenario.setMaxDuration(10000);
        ScenarioConverter.convertToXML(testScenario, path, scenarioName);

        return testScenario;
    }
}
