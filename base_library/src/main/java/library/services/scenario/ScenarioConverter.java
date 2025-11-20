package library.services.scenario;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.mapper.CannotResolveClassException;
import com.thoughtworks.xstream.security.AnyTypePermission;
import library.model.dto.scenario.ScenarioDTO;
import library.model.simulation.Behaviour;
import library.model.simulation.objects.IActiveDynamic;
import library.model.simulation.objects.SimulationObject;
import library.model.simulation.SimulationProperty;
import library.services.utils.FileUtils;
import library.services.utils.JAXBUtils;
import library.services.utils.ReflectionUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * The ScenarioConverter is used to convert Scenario Instances to XML Files and backwards.
 */
public class ScenarioConverter {

    /**
     * Converts a Scenario instance into an XML-File by using JAXB and saves it at the place pointed at by the given path.
     *
     * @param scenario     an instance of the Scenario
     * @param path         the path where the scenario should be serialized and written to
     * @param scenarioName the name of the scenario
     * @throws JAXBException thrown if the 'jaxb.index' can't be found in the resources path
     * @throws IOException   thrown by the FileReader
     */
    public static void convertToXML(ScenarioDTO scenario, String path, String scenarioName) throws JAXBException, IOException {
        JAXBContext modelContext = JAXBUtils.getModelContext();
        Marshaller marshaller = modelContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        File file = FileUtils.createXmlFile(path, scenarioName);
        marshaller.marshal(scenario, file);
    }

    /**
     * Converts a scenario.xml into a Scenario object instance by using JAXB.
     *
     * @param fullPath is the fullPath where the scenario.xml is located
     * @return object instance of Scenario
     * @throws JAXBException         thrown if the 'jaxb.index' can not be found in the resources fullPath
     * @throws FileNotFoundException thrown by the FileReader
     */
    public static ScenarioDTO convertToScenarioModel(String fullPath) throws JAXBException, IOException {
        ScenarioDTO scenario;
        try {
            JAXBContext modelContext = JAXBUtils.getModelContext();
            scenario = (ScenarioDTO) modelContext.createUnmarshaller().unmarshal(new FileReader(FileUtils.getXmlPath(fullPath)));
        } catch (JAXBException jaxbException) {
            jaxbException.printStackTrace();
            return loadFromXmlXStream(Path.of(fullPath));
        }

        // QUESTION: Why is this here? Does it have a purpose?
        // ANSWER: Yes it does. This methods instantiates and starts stuff... a renaming is needed (see fix-me comment on the method itself)
        for (SimulationObject simulationObject : scenario.getSimulationObjects()) {
            HashSet<IActiveDynamic> activeDynamics = findAllActiveDynamics(simulationObject, new HashSet<>(), new HashSet<>());
        }

        return scenario;
    }

    private static ScenarioDTO loadFromXmlXStream(Path file) throws IOException {
        String input = Files.readString(file);

        XStream xStream = new XStream();
        xStream.addPermission(AnyTypePermission.ANY);
        Object output;

        URLClassLoader child = new URLClassLoader(
                new URL[]{Path.of(file.toString().replace(".xml", ".jar")).toFile().toURI().toURL()},
                ScenarioConverter.class.getClassLoader()
        );

        xStream.setClassLoader(child);

        try {
            output = xStream.fromXML(input);
        } catch (CannotResolveClassException exception) {
            throw new IOException("Couldn't load classes from scenario file.");
        } catch (ConversionException exception) {
            exception.printStackTrace();
            throw exception;
        }

        if (output.getClass() == ScenarioDTO.class) {
            return (ScenarioDTO) output;
        } else {
            throw new IOException("Loaded class is not of type " + ScenarioDTO.class.getName()
                    + ", found " + output.getClass().getName() + " instead.");
        }
    }

    public static ScenarioDTO convertToScenarioModel(String path, String scenarioName) throws JAXBException, IOException {
        return convertToScenarioModel(FileUtils.getFullXmlPath(path, scenarioName));
    }

    // FIXME: rename / split up this method, since it does not only find the activeDynamics but does also start the websocket servers for controlled behaviours etc.
    public static HashSet<IActiveDynamic> findAllActiveDynamics(Object mainObject, HashSet<IActiveDynamic> iActiveDynamics,
                                                                HashSet<Object> visitedNodes) {
        return findAllActiveDynamics(mainObject, iActiveDynamics, visitedNodes, (SimulationObject) mainObject);
    }

    // FIXME: rename / split up this method, since it does not only find the activeDynamics but does also start the websocket servers for controlled behaviours etc.
   /* private static HashSet<IActiveDynamic> findAllActiveDynamics(Object mainObject, HashSet<IActiveDynamic> iActiveDynamics,
                                                                 HashSet<Object> visitedNodes, SimulationObject rootSimulationObject) {

        // check if the given object is a subclass of the IActiveDynamic interface
        // which says that this object can actively manipulate/change its simulation relevant values
        if (IActiveDynamic.class.isAssignableFrom(mainObject.getClass())) {

            try {
                // check if the given object (which is an instance of IActiveDynamic) has a behaviour set
                if (((IActiveDynamic) mainObject).getBehaviour() != null) {

                    // get the behaviours
                    IBehaviour behaviour = BehaviourLoader.loadBehaviour(BehaviourLoader.class.getPackageName() + "." + ((IActiveDynamic) mainObject).getBehaviour().getBehaviourName().getValue());
                    behaviour.setGoals(((IActiveDynamic) mainObject).getBehaviour().getGoals());

                    //If the loaded behaviour is a system under test, then load the config in it and start the server
                    if (behaviour instanceof ControlledBehaviour) {
                        SystemUnderTestConfig systemUnderTestConfig = ((IActiveDynamic) mainObject).getBehaviour().getSystemUnderTestConfig();
                        ((ControlledBehaviour) behaviour).setAddress(systemUnderTestConfig.getAddress());
                        ((ControlledBehaviour) behaviour).setPort(systemUnderTestConfig.getPort());
                        ((ControlledBehaviour) behaviour).setWebSocket(systemUnderTestConfig.isWebsocket());
                        ((ControlledBehaviour) behaviour).startServer();
                    }
                    behaviour.setSimulationObject(rootSimulationObject);
                    ((IActiveDynamic) mainObject).setBehaviour(behaviour);
                }

            } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException | IOException e) {
                e.printStackTrace();
            }

            iActiveDynamics.add((IActiveDynamic) mainObject);

        }

        ArrayList<Field> fieldList = ReflectionUtils.getAllFieldsOfClass(mainObject);

        for (Field aField : fieldList) {
            if (aField.getName().equals("route")) {
                continue;
            }

            Object fieldValueObject = ReflectionUtils.getValueObjectFromField(aField, mainObject);
            if (fieldValueObject == null) {
                continue;
            }

            if (visitedNodes.contains(fieldValueObject)) {
                continue;
            } else {
                visitedNodes.add(fieldValueObject);
            }

            if (fieldValueObject instanceof SimulationAttribute) {

                iActiveDynamics = findAllActiveDynamics(((SimulationAttribute) fieldValueObject).getValue(), iActiveDynamics, visitedNodes, rootSimulationObject);

            } else if (fieldValueObject instanceof Collection) {

                for (Object collectionObject : (Collection) fieldValueObject) {
                    if (collectionObject instanceof SimulationAttribute) {
                        iActiveDynamics = findAllActiveDynamics(((SimulationAttribute) fieldValueObject).getValue(), iActiveDynamics, visitedNodes, rootSimulationObject);
                    } else {
                        iActiveDynamics = findAllActiveDynamics(collectionObject, iActiveDynamics, visitedNodes, rootSimulationObject);
                    }
                }

            } else {
                iActiveDynamics = findAllActiveDynamics(fieldValueObject, iActiveDynamics, visitedNodes, rootSimulationObject);
            }
        }

        return iActiveDynamics;
    }*/

    private static HashSet<IActiveDynamic> findAllActiveDynamics(Object mainObject, HashSet<IActiveDynamic> iActiveDynamics,
                                                                 HashSet<Object> visitedNodes, SimulationObject rootSimulationObject) {

        // check if the given object is a subclass of the IActiveDynamic interface
        // which says that this object can actively manipulate/change its simulation relevant values
        if (IActiveDynamic.class.isAssignableFrom(mainObject.getClass())) {

            Behaviour behaviour = ((IActiveDynamic) mainObject).getBehaviour();
            // check if the given object (which is an instance of IActiveDynamic) has a behaviour set
            if (behaviour != null) {
                behaviour.setSimulationObject(rootSimulationObject);
                ((IActiveDynamic) mainObject).setBehaviour(behaviour);
            }

            iActiveDynamics.add((IActiveDynamic) mainObject);

        }

        ArrayList<Field> fieldList = ReflectionUtils.getAllFieldsOfClass(mainObject);

        for (Field aField : fieldList) {
            if (aField.getName().equals("route")) {
                continue;
            }

            Object fieldValueObject = ReflectionUtils.getValueObjectFromField(aField, mainObject);
            if (fieldValueObject == null) {
                continue;
            }

            if (visitedNodes.contains(fieldValueObject)) {
                continue;
            } else {
                visitedNodes.add(fieldValueObject);
            }

            if (fieldValueObject instanceof SimulationProperty) {

                iActiveDynamics = findAllActiveDynamics(((SimulationProperty) fieldValueObject).getValue(), iActiveDynamics, visitedNodes, rootSimulationObject);

            } else if (fieldValueObject instanceof Collection) {

                for (Object collectionObject : (Collection) fieldValueObject) {
                    if (collectionObject instanceof SimulationProperty) {
                        iActiveDynamics = findAllActiveDynamics(((SimulationProperty) fieldValueObject).getValue(), iActiveDynamics, visitedNodes, rootSimulationObject);
                    } else {
                        iActiveDynamics = findAllActiveDynamics(collectionObject, iActiveDynamics, visitedNodes, rootSimulationObject);
                    }
                }

            } else {
                iActiveDynamics = findAllActiveDynamics(fieldValueObject, iActiveDynamics, visitedNodes, rootSimulationObject);
            }
        }

        return iActiveDynamics;
    }

}
