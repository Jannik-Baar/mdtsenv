//package library.model.examples.behaviours;
//
//import library.model.maritime.ContainerShip;
//import library.model.maritime.Engine;
//import library.model.maritime.behaviours.DefectEngineBehaviour;
//import library.model.simulation.FormDummy;
//import library.model.simulation.Position;
//import library.model.simulation.SimulationAttribute;
//import library.model.simulation.Goal;
//import library.model.examples.goals.TimeGoal;
//import library.model.simulation.units.TimeUnit;
//import library.model.traffic.PossibleDomains;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotEquals;
//
//class DefectEngineBehaviourTest {
//
//    @org.junit.jupiter.api.Test
//    public void defectEngineBehaviourTest(){
//        //Create Containership
//        ContainerShip containerShip = new ContainerShip(true, new Position(0.0,0.0,0), new FormDummy(), 45.0, 60,
//                PossibleDomains.MARITIME, 112.9, 1.0, 10.0, new Position(), 45.0, 12.6, "Hamburch", "SS Hope", "Belarus",
//                "12432344654365", "ferwdsgdsv", "Black Hawk", 2.5, 5, 0.0, 0.0);
//
//        //Create engine that can manipulate the speed of the vessel
//        Engine engine = new Engine();
//
//        //Add engine to containership
//        containerShip.addComponent(engine);
//
//        //Set Behaviours
//        containerShip.setBehaviour(new ConstantSpeedBehaviour(containerShip));
//        DefectEngineBehaviour defectEngineBehaviour = new DefectEngineBehaviour();
//        defectEngineBehaviour.setSimulationObject(containerShip);
//        defectEngineBehaviour.addGoal(new TimeGoal(new SimulationAttribute(TimeUnit.SECOND,180.0,"Time when the motor should stop")));
//        engine.setBehaviour(defectEngineBehaviour);
//
//        //Simulation of the SimulationSteps Stepsize 60seconds until the engine is defect
//        for (int i = 0; i < 36; i++) {
//            double positionLonOld = containerShip.getPosition().getValue().getLongitude().getValue();
//            double positionLatOld = containerShip.getPosition().getValue().getLatitude().getValue();
//            defectEngineBehaviour.nextStep(containerShip.getTimeStepSize());
//            containerShip.getBehaviour().nextStep(containerShip.getTimeStepSize());
//
//            //Check if there is a change in the position of the ship
//            assertNotEquals(positionLonOld, containerShip.getPosition().getValue().getLongitude().getValue());
//            assertNotEquals(positionLatOld, containerShip.getPosition().getValue().getLatitude().getValue());
//        }
//
//        //Save current Position of the ship
//
//        double longOld = containerShip.getPosition().getValue().getLongitude().getValue();
//        double latOld = containerShip.getPosition().getValue().getLatitude().getValue();
//
//        //Call the Behaviours another time to test if the Position isn't changed when the engine is defect
//        defectEngineBehaviour.nextStep(new SimulationAttribute(true, true, TimeUnit.SECOND, 5.0, "timesteps"));
//        containerShip.getBehaviour().nextStep(new SimulationAttribute(true, true, TimeUnit.SECOND, 5.0, "timesteps"));
//
//        assertEquals(longOld,containerShip.getPosition().getValue().getLongitude().getValue());
//        assertEquals(latOld,containerShip.getPosition().getValue().getLatitude().getValue());
//
//    }
//
//}