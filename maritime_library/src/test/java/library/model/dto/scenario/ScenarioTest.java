//package library.model.dto.scenario;
//
//import library.model.examples.behaviours.ConstantSpeedBehaviour;
//import library.model.maritime.Vessel;
//import library.model.dto.scenario.terminationconditions.CompareAttributeCondition;
//import library.model.dto.scenario.terminationconditions.TerminationCondition;
//import library.model.simulation.Behaviour;
//import library.model.simulation.FormDummy;
//import library.model.simulation.Position;
//import library.model.simulation.SimulationAttribute;
//import library.model.simulation.units.NoUnit;
//import library.model.traffic.PossibleDomains;
//import library.services.logging.LoggingType;
//
//import javax.xml.bind.JAXBContext;
//import javax.xml.bind.JAXBException;
//import javax.xml.bind.Marshaller;
//import java.io.File;
//import java.io.FileReader;
//import java.io.IOException;
//
//import static org.junit.jupiter.api.Assertions.*;
//
///**
// * Marshalls and unmarshalls a Scenario and checks if the size of the SimulationObjects are the same.
// * If an javax.xml.bind.JAXBException is thrown the mentioned Class in the Exception should be added
// * as an @XmlSeeAlso in the Scenario.class
// */
//class ScenarioTest {
//    @org.junit.jupiter.api.Test
//    void marshallXML() throws IOException, JAXBException {
//        Position start = new Position(0, 0, 0);
//        Vessel vessel1 = new Vessel(true, start, new FormDummy(), 90, 2,
//                PossibleDomains.MARITIME, 112.9, 1.0, 25.0, start, 180.4, 12.6, "Hamburch", "SS Hope", "Belarus",
//                "dsfdfsdfa", "fe34243232rwdsgdsr", "Generic", 2.5, 1.0, 1.0);
//        vessel1.setBehaviour(new ConstantSpeedBehaviour());
//        Vessel vessel2 = new Vessel(true, start, new FormDummy(), 90, 3,
//                PossibleDomains.MARITIME, 112.9, 1.0, 25.0, start, 180.4, 12.6, "Hamburch", "SS Nope", "Belarus",
//                "dsfdfsdfb", "fe34243232rwdsgdsm", "Generic", 2.5, 1.0, 1.0);
//        vessel2.setBehaviour(new ConstantSpeedBehaviour());
//        Vessel vessel3 = new Vessel(true, start, new FormDummy(), 90, 1,
//                PossibleDomains.MARITIME, 112.9, 1.0, 25.0, start, 180.4, 12.6, "Hamburch", "SS Pope", "Belarus",
//                "dsfdfsdfc", "fe34243232rwdsgdsv", "Generic", 2.5, 1.0, 1.0);
//        vessel3.setBehaviour(new ConstantSpeedBehaviour());
//        Scenario scenario = new Scenario();
//        scenario.addSimulationObject(vessel1);
//        scenario.addSimulationObject(vessel2);
//        scenario.addSimulationObject(vessel3);
//        Observer observer = new Observer(2,new ObserverWebSocketConfig());
//        observer.addAttributeToObserve(vessel1.getId(), vessel1.getPosition().getValue().getAltitude().getId());
//        scenario.addObserver(observer);
//        scenario.setMaxDuration(30000);
//        scenario.addLoggingType(LoggingType.CONSOLE);
//        scenario.setSimulationIterations(150);
//        scenario.addTerminationCondition(new TerminationCondition(new CompareAttributeCondition(null, new SimulationAttribute(NoUnit.get(), 200, "TestAttribute1"),199, CompareAttributeCondition.CompareOperation.LESS)));
//        scenario.addTerminationCondition(new TerminationCondition(new CompareAttributeCondition(null, new SimulationAttribute(NoUnit.get(), 201, "TestAttribute2"),null, new SimulationAttribute(NoUnit.get(), 200, "TestAttribute3"), CompareAttributeCondition.CompareOperation.GREATER)));
//        CompareAttributeCondition compareSpeed = new CompareAttributeCondition(vessel1, vessel1.getSpeed(),  (double) 100, CompareAttributeCondition.CompareOperation.LESS_OR_EQUAL);
//        scenario.addTerminationCondition(new TerminationCondition(compareSpeed));
//
//        JAXBContext context = JAXBContext.newInstance("library.model");
//
//        Marshaller mar = context.createMarshaller();
//        mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
//        File dir = new File("scenarios");
//        if (!dir.exists()) {
//            dir.mkdirs();
//        } else if (!dir.isDirectory()) {
//            fail("Testordner kann nicht angelegt werden, da bereits eine Datei mit dem gleichen Namen vorhanden ist");
//        }
//        File file = new File("scenarios/Testscenario.xml");
//        System.out.println(file.getAbsolutePath());
//        file.createNewFile();
//        mar.marshal(scenario, file);
//
//        Scenario deMarshalled = (Scenario) context.createUnmarshaller().unmarshal(new FileReader("scenarios/Testscenario.xml"));
//
//        assertEquals(3, deMarshalled.getTerminationConditions().size());
//        for(TerminationCondition t:  deMarshalled.getTerminationConditions()){
//            if(t.getCondition() instanceof CompareAttributeCondition){
//                CompareAttributeCondition comp = (CompareAttributeCondition)t.getCondition();
//                if (comp.getIsCompareWithValue() && comp.getAttrIdToSimObjMap().isEmpty()){
//                    assertTrue(comp.conditionIsMet());
//                }else {
//                    assertFalse(comp.conditionIsMet());
//                    if(!comp.getAttrIdToSimObjMap().isEmpty()){
//                        SimulationAttribute attribute = comp.getAttribute();
//                        assertTrue(comp.getAttrIdToSimObjMap().containsKey(attribute.getId()));
//                    }
//                }
//            }
//        }
//        assertEquals(scenario.getSimulationObjects().size(), deMarshalled.getSimulationObjects().size());
//        assertTrue(deMarshalled.isStepsLimited());
//        assertEquals(deMarshalled.getSimulationIterations(),150);
//        assertTrue(deMarshalled.isTimeLimited());
//        assertEquals(deMarshalled.getMaxDuration(), 30000);
//        for (int i = 0; i < deMarshalled.getSimulationObjects().size(); i++) {
//            assertEquals(scenario.getSimulationObjects().get(i).getClass(), deMarshalled.getSimulationObjects().get(i).getClass());
//        }
//
//        //Check if timeStepSize for VesselObjects is 1.0 and 3.0
//        assertEquals(deMarshalled.getSimulationObjects().get(0).getTimeStepSize().getValue(), 2.0);
//        assertEquals(deMarshalled.getSimulationObjects().get(1).getTimeStepSize().getValue(), 3.0);
//        assertEquals(deMarshalled.getSimulationObjects().get(2).getTimeStepSize().getValue(), 1.0);
//
//        ///file.deleteOnExit();
//
//    }
//}