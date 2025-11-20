//package interpreter;
//
//import hla.rti1516e.exceptions.FederateNotExecutionMember;
//import hla.rti1516e.exceptions.NotConnected;
//import simulation.federate.interpreted.InterpretedFederate;
//import library.model.examples.behaviours.ConstantSpeedBehaviour;
//import library.model.maritime.ContainerShip;
//import library.model.maritime.DriveShaft;
//import library.model.maritime.Gear;
//import library.model.maritime.HumiditySensor;
//import library.model.dto.observer.Observer;
//import library.model.dto.observer.ObserverWebSocketConfig;
//import library.model.dto.scenario.Scenario;
//import library.model.simulation.Component;
//import library.model.simulation.FormDummy;
//import library.model.simulation.enums.NumericalRestrictionType;
//import library.model.simulation.Position;
//import library.model.limitations.Restriction;
//import library.model.simulation.SimulationAttribute;
//import library.model.simulation.units.NoUnit;
//import library.model.simulation.units.RotationUnit;
//import library.model.traffic.PossibleDomains;
//import library.model.traffic.SteeringSystem;
//import library.services.geodata.MapDataProvider;
//import library.services.scenario.ScenarioConverter;
//import org.locationtech.jts.io.ParseException;
//import manager.SimulationManager;
//import manager.SimulationWatchDog;
//
//import javax.xml.bind.JAXBException;
//import java.io.File;
//import java.io.IOException;
//import java.net.URISyntaxException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//class InterpreterTest {
//
//    private ContainerShip containerShip;
//    private Scenario scenario;
//    private MapDataProvider mapDataProvider;
//
//    @org.junit.jupiter.api.BeforeEach
//    void setUp() throws IOException {
//        containerShip = new ContainerShip(true, new Position(), new FormDummy(), 180.0, 1,
//                                          PossibleDomains.MARITIME, 112.9, 300.0, 25.0, new Position(), 233.4, 12.6, "Hamburch", "SS Hope", "Belarus",
//                                          "12432344654365", "ferwdsgdsv", "Black Hawk", 2.5, 5, 1.0, 1.0);
//        containerShip.getContainerCapacity().setPublish(true);
//        containerShip.getPosition().getValue().getLongitude().setPublish(true);
//        containerShip.getPosition().getValue().getLatitude().setPublish(true);
//        containerShip.getPosition().getValue().getAltitude().setPublish(true);
//        Restriction<Boolean> boolRest = Restriction.getBooleanRestriction("rammedOtherShip", true);
//        Restriction<Integer> xGoal = Restriction.getNumericalRestriction("xCoord", 32, NumericalRestrictionType.EXACT, RotationUnit.DEGREE);
//        Restriction<Integer> yGoal = Restriction.getNumericalRestriction("yCoord", 48, NumericalRestrictionType.EXACT, RotationUnit.DEGREE);
//        containerShip.addRestriction(boolRest).addRestriction(xGoal).addRestriction(yGoal);
//        Component humiditySensor = new HumiditySensor(null);
//        containerShip.addComponent(new Gear())
//                     .addComponent(new DriveShaft(new SimulationAttribute<>(true, false, NoUnit.get(), 32.58, "shaftLength")))
//                     .addComponent(humiditySensor);
//        SteeringSystem steeringSystem = new SteeringSystem(containerShip);
//        steeringSystem.addChangeableAttributeID(containerShip.getPosition().getId());
//        containerShip.addComponent(steeringSystem);
//        containerShip.setBehaviour(new ConstantSpeedBehaviour());
//        scenario = new Scenario();
//        scenario.addSimulationObject(containerShip);
//        scenario.setSimulationIterations(100);
//        ContainerShip containerShip2 = new ContainerShip(true, new Position(0, 5, 0), new FormDummy(), 180.0, 2,
//                                                         PossibleDomains.MARITIME, 112.9, 1.0, 25.0, new Position(), 233.3, 22.6, "Hamburch", "SS No Hope", "Galicia",
//                                                         "12432542344654365", "ferwddsasgdsv", "Black Hawk Down", 5.5, 4, 1.0, 1.0);
//        containerShip2.setBehaviour(new ConstantSpeedBehaviour());
//        containerShip2.getContainerCapacity().setSubscribe(true);
//        containerShip2.getPosition().getValue().getLongitude().setSubscribe(true);
//        steeringSystem = new SteeringSystem(containerShip2);
//        steeringSystem.addChangeableAttributeID(containerShip2.getPosition().getId());
//        containerShip2.addComponent(steeringSystem);
//        scenario.addSimulationObject(containerShip2);
//        Observer observer = new Observer(2, new ObserverWebSocketConfig("duemmer.Informatik.Uni-Oldenburg.DE", 3193));
//        observer.addAttributeToObserve(containerShip.getId(), containerShip.getPosition().getId());
//        observer.addAttributeToObserve(containerShip.getId(), containerShip.getVesselName().getId());
//        observer.addAttributeToObserve(containerShip.getId(), containerShip.getRotation().getId());
//        observer.addAttributeToObserve(containerShip2.getId(), containerShip2.getPosition().getId());
//        observer.addAttributeToObserve(containerShip2.getId(), containerShip2.getVesselName().getId());
//        observer.addAttributeToObserve(containerShip2.getId(), containerShip2.getRotation().getId());
//        //observer.addLoggingType(LoggingType.WEBSOCKET);
//        scenario.addObserver(observer);
//
//        new SimulationWatchDog(new SimulationManager(), new ArrayList<Thread>(), new ArrayList<AbstractFederate>(), "", mapDataProvider, -1);
//    }
//
//    @org.junit.jupiter.api.Test
//    void testCreateFederates() throws InterruptedException, IOException, ParseException {
//        try {
//            ScenarioConverter.convertToXML(scenario, "", "test");
//            scenario = ScenarioConverter.convertToScenarioModel("test");
//        } catch (JAXBException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        Interpreter interpreter = new Interpreter();
//        interpreter.generateFOMs(scenario);
//        try {
//            ScenarioConverter.convertToXML(scenario, "", "test");
//            scenario = ScenarioConverter.convertToScenarioModel("test");
//        } catch (JAXBException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        ArrayList<AbstractFederate> allFederates = interpreter.createFederates(scenario);
//
//        ArrayList<Thread> threads = new ArrayList<>();
//        for (AbstractFederate interpretedFederate : allFederates) {
//            Thread thread = new Thread(interpretedFederate);
//            threads.add(thread);
//        }
//
//        SimulationWatchDog watchDog = new SimulationWatchDog(new SimulationManager(), threads, allFederates, "", mapDataProvider, -1);
//
//        Thread watchDogThread = new Thread(watchDog);
//        watchDogThread.start();
//        watchDogThread.join();
//        assertEquals(4, allFederates.size());
//    }
//
//    @org.junit.jupiter.api.Test
//    void testInterpreter() throws NotConnected, FederateNotExecutionMember, InterruptedException, IOException, URISyntaxException {
//        Interpreter interpreter = new Interpreter();
//        Scenario tmpScenario = new Scenario();
//        tmpScenario.addSimulationObject(containerShip);
//        tmpScenario.setSimulationIterations(100);
//        interpreter.generateFOMs(tmpScenario);
//        InterpretedFederate interpretedFederate = (InterpretedFederate) interpreter.createFederates(tmpScenario).get(0);
////        ArrayList<AbstractFederate> interpretedFederates = new ArrayList<>();
////        ArrayList<Thread> threads = new ArrayList<>();
////        interpretedFederates.add(interpretedFederate);
////        Thread thread = new Thread(interpretedFederate);
////        threads.add(thread);
////        SimulationWatchDog watchDog = new SimulationWatchDog(new SimulationManager(), threads, interpretedFederates, "", mapDataProvider, -1);
////        Thread watchDogThread = new Thread(watchDog);
////        watchDogThread.start();
////        watchDogThread.join();
//        String FOMFileName = interpretedFederate.getFOMFileName();
//        System.out.println(FOMFileName);
//        File file = new File("src\\test\\resources\\interpreter\\" + FOMFileName);
//        String path = file.getAbsolutePath();
//        String referenceFile = new String(Files.readAllBytes(Paths.get(path)));
//        String generatedFile = new String(Files.readAllBytes(Paths.get("FOMS\\" + FOMFileName)));
//        File fileToDelete = new File("FOMS\\" + FOMFileName);
//        fileToDelete.delete();
//
//        assertEquals(stripDate(referenceFile), stripDate(generatedFile));
//    }
//
//    private String stripDate(String input) {
//        input = input.replaceAll(" {4}", "");
//        input = input.replaceAll("\n", "");
//        input = input.replaceAll("\r", "");
//        String[] splitted = input.split("<modificationDate");
//        String[] splitted2 = splitted[1].split("modificationDate>");
//        return splitted[0] + splitted2[1];
//    }
//}