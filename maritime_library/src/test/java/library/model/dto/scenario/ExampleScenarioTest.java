//package library.model.dto.scenario;
//
//import library.model.examples.behaviours.ConstantSpeedBehaviour;
//import library.model.examples.behaviours.ControlledBehaviour;
//import library.model.maritime.ContainerShip;
//import library.model.maritime.Engine;
//import library.model.maritime.PassengerShip;
//import library.model.maritime.Vessel;
//
//import library.model.maritime.behaviours.DefectEngineBehaviour;
//import library.model.maritime.behaviours.EmergencyRespondBehaviour;
//import library.model.maritime.behaviours.FollowGivenRouteBehaviour;
//import library.model.maritime.behaviours.MaximumAllowedSpeedBehaviour;
//import library.model.simulation.Behaviour;
//import library.model.simulation.FormDummy;
//import library.model.simulation.enums.NumericalRestrictionType;
//import library.model.simulation.Position;
//import library.model.limitations.Restriction;
//import library.model.simulation.SimulationAttribute;
//import library.model.simulation.Goal;
//import library.model.simulation.IGoal;
//import library.model.examples.goals.TimeGoal;
//import library.model.simulation.units.AccelerationUnit;
//import library.model.simulation.units.DistanceUnit;
//import library.model.simulation.units.NoUnit;
//import library.model.simulation.units.RotationUnit;
//import library.model.simulation.units.SimulationUnit;
//import library.model.simulation.units.SpeedUnit;
//import library.model.simulation.units.TimeUnit;
//import library.model.simulation.units.WeightUnit;
//
//import library.model.traffic.Infrastructure;
//import library.model.traffic.Obstacle;
//import library.model.traffic.PossibleDomains;
//import library.model.traffic.SpeedSensor;
//import library.model.traffic.SteeringSystem;
//import library.services.geodata.GeoPackageReader;
//import library.services.logging.LoggingType;
//import library.services.scenario.ScenarioConverter;
//import org.geotools.feature.SchemaException;
//import org.locationtech.jts.geom.Coordinate;
//import org.locationtech.jts.geom.Geometry;
//import org.locationtech.jts.io.ParseException;
//import org.locationtech.jts.io.WKTReader;
//import org.locationtech.jts.util.GeometricShapeFactory;
//
//import javax.xml.bind.JAXBException;
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
///**
// * Creates an example Scenario and saves it under C:/example (C: if this scenario is run within a directory in C:)
// */
//public class ExampleScenarioTest {
//
//    private double getAccel(double length, double maxSpeed) {
//        double breakingDistance = length * 15;
//        double speed = maxSpeed * SpeedUnit.KNOTS.getUnitToBase();
//        double breakingTime = (2 * breakingDistance) / speed;
//        return (2 * breakingDistance) / Math.pow(breakingTime, 2);
//    }
//
//    @org.junit.jupiter.api.Test
//    public void testScenario() throws ParseException, SchemaException, IOException {
//        //
//        //  Needed Utility Object(s)
//        //
//        GeometricShapeFactory geometricShapeFactory = new GeometricShapeFactory();
//
//
//        //
//        //  Vessel definition
//        //
//        //Create Controlled Vessel Containership
//        ContainerShip containerShip = new ContainerShip(true, new Position(8.535274, 53.855108, 0), new FormDummy(), 0.0, 1,
//                PossibleDomains.MARITIME, 112.9, 0.0, 0.0, new Position(), 0.0, 12.6, "Hamburch", "SS Hope", "Belarus",
//                "12432344654365", "ferwdsgdsv", "Black Hawk", 2.5, 5, 1.0, 1.0);
//
//        //Create SteeringSystem that can manipulate the speed and the rotation
//        SteeringSystem steeringSystem = new SteeringSystem(containerShip);
//        steeringSystem.addChangeableAttributeID(containerShip.getRotation().getId());
//        steeringSystem.addChangeableAttributeID(containerShip.getSpeed().getId());
//        System.out.println("Rotation ID for frontend usage: " + containerShip.getRotation().getId());
//        System.out.println("Speed ID for frontend usage: " + containerShip.getSpeed().getId());
//
//        //Add steering system to containership
//        containerShip.addComponent(steeringSystem);
//
//        //Set Behaviours
//        Behaviour controlledBehaviour = new ControlledBehaviour(containerShip);
//        controlledBehaviour.setSystemUnderTestConfig(new SystemUnderTestConfig(3002, "localhost", true));
//        containerShip.setBehaviour(new ConstantSpeedBehaviour());
//        steeringSystem.setBehaviour(controlledBehaviour);
//
//        /**
//         * BREMERHAVEN
//         * https://www.weserfaehre.de/schiffe/
//         * https://www.marinetraffic.com/en/ais/details/ships/shipid:142026/mmsi:211595610/imo:0/vessel:BREMERHAVEN
//         */
//        PassengerShip ship1 = new PassengerShip(
//                new SimulationAttribute<Boolean>(true, true, NoUnit.get(), true, "physical"),
//                new SimulationAttribute<Position>(true, true, RotationUnit.DEGREE, new Position(8.574818, 53.535395, 0), "position"),
//                null,
//                new SimulationAttribute<Double>(true, true, RotationUnit.DEGREE, 255.0, "rotation"),
//                new SimulationAttribute<Double>(true, true, TimeUnit.SECOND, 1.0, "timeStepSize"),
//                new SimulationAttribute<PossibleDomains>(true, true, NoUnit.get(), PossibleDomains.MARITIME, "assignedDomain"),
//                new SimulationAttribute<Double>(true, true, WeightUnit.TON, 25518.0, "weight"),
//                new SimulationAttribute<Double>(true, true, SpeedUnit.KNOTS, 8.5, "speed"),
//                null,
//                new SimulationAttribute<Position>(true, true, RotationUnit.DEGREE, new Position(8.574818, 53.535395, 0), "origin"),
//                new SimulationAttribute<Double>(true, true, RotationUnit.DEGREE, 255.0, "course"),
//                new SimulationAttribute<Double>(true, true, DistanceUnit.METER, 2.7, "draught"),
//                new SimulationAttribute<String>(true, true, NoUnit.get(), "Bremerhaven", "homeHarbour"),
//                new SimulationAttribute<String>(true, true, NoUnit.get(), "Bremerhaven", "vesselName"),
//                new SimulationAttribute<String>(true, true, NoUnit.get(), "Germany [DE]", "flag"),
//                null,
//                new SimulationAttribute<String>(true, true, NoUnit.get(), "211595610", "mmsi"),
//                new SimulationAttribute<String>(true, true, NoUnit.get(), "DC4782", "callsign"),
//                null,
//                new SimulationAttribute<Integer>(true, true, NoUnit.get(), 300, "passengerCapacity"),
//                new SimulationAttribute<Double>(true, true, DistanceUnit.METER, 59.0, "length"),
//                new SimulationAttribute<Double>(true, true, DistanceUnit.METER, 13.4, "width")
//        );
//        ship1.setMaxSpeed(new SimulationAttribute<Double>(true, true, SpeedUnit.KNOTS, 14.3, "maxSpeed"));
//        ship1.setAcceleration(new SimulationAttribute<Double>(true, true, AccelerationUnit.METERSPERSECONDSSQUARED, getAccel(ship1.getLength().getValue(), ship1.getMaxSpeed().getValue()), "acceleration"));
//        geometricShapeFactory.setNumPoints(4);
//        geometricShapeFactory.setWidth(ship1.getWidth().getValue());
//        geometricShapeFactory.setHeight(ship1.getLength().getValue());
//        Geometry ship1Shape = geometricShapeFactory.createRectangle();
//        ship1.setForm(new SimulationAttribute<Geometry>(true, true, DistanceUnit.METER, ship1Shape, "form"));
//
//        /**
//         * NIGHT STAR
//         * https://www.marinetraffic.com/en/ais/details/ships/shipid:5902591/mmsi:368092080/imo:0/vessel:NIGHT_STAR
//         */
//        Vessel ship2 = new Vessel(
//                new SimulationAttribute<Boolean>(true, false, NoUnit.get(), true, "physical"),
//                new SimulationAttribute<Position>(true, false, RotationUnit.DEGREE, new Position(8.486374, 53.328804, 0), "position"),
//                null,
//                new SimulationAttribute<Double>(true, false, RotationUnit.DEGREE, 135.0, "rotation"),
//                new SimulationAttribute<Double>(true, false, TimeUnit.SECOND, 1.0, "timeStepSize"),
//                new SimulationAttribute<PossibleDomains>(true, false, NoUnit.get(), PossibleDomains.MARITIME, "assignedDomain"),
//                null,
//                new SimulationAttribute<Double>(true, false, SpeedUnit.KNOTS, 14.9, "speed"),
//                null,
//                new SimulationAttribute<Position>(true, false, RotationUnit.DEGREE, new Position(8.486374, 53.328804, 0), "origin"),
//                new SimulationAttribute<Double>(true, false, RotationUnit.DEGREE, 135.0, "course"),
//                null,
//                new SimulationAttribute<String>(true, false, NoUnit.get(), "Fort Lauderdale", "homeHarbour"),
//                new SimulationAttribute<String>(true, false, NoUnit.get(), "Night Star", "vesselName"),
//                new SimulationAttribute<String>(true, false, NoUnit.get(), "USA [US]", "flag"),
//                null,
//                new SimulationAttribute<String>(true, false, NoUnit.get(), "368092080", "mmsi"),
//                new SimulationAttribute<String>(true, false, NoUnit.get(), "WDK8225", "callsign"),
//                null,
//                new SimulationAttribute<Double>(true, false, DistanceUnit.METER, 23.0, "length"),
//                new SimulationAttribute<Double>(true, false, DistanceUnit.METER, 6.0, "width")
//        );
//        ship2.setMaxSpeed(new SimulationAttribute<Double>(true, false, SpeedUnit.KNOTS, 25.2, "maxSpeed"));
//        ship2.setAcceleration(new SimulationAttribute<Double>(true, false,
//                AccelerationUnit.METERSPERSECONDSSQUARED, getAccel(ship2.getLength().getValue(),
//                ship2.getMaxSpeed().getValue()), "acceleration"));
//        geometricShapeFactory.setNumPoints(4);
//        geometricShapeFactory.setWidth(ship2.getWidth().getValue());
//        geometricShapeFactory.setHeight(ship2.getLength().getValue());
//        Geometry ship2Shape = geometricShapeFactory.createRectangle();
//        ship2.setForm(new SimulationAttribute<Geometry>(true, false, DistanceUnit.METER, ship2Shape, "form"));
//
//        ship2.setBehaviour(new MaximumAllowedSpeedBehaviour());
//
//        List<SimulationAttribute<Position>> route = Arrays.asList(
//                new SimulationAttribute<Position>(true, false, RotationUnit.DEGREE, new Position(8.486374, 53.328804, 0), "position"),
//                new SimulationAttribute<Position>(true, false, RotationUnit.DEGREE, new Position(8.489189, 53.328255, 0), "position"),
//                new SimulationAttribute<Position>(true, false, RotationUnit.DEGREE, new Position(8.496832, 53.337510, 0), "position"),
//                new SimulationAttribute<Position>(true, false, RotationUnit.DEGREE, new Position(8.502373, 53.354140, 0), "position"),
//                new SimulationAttribute<Position>(true, false, RotationUnit.DEGREE, new Position(8.497692, 53.383581, 0), "position"),
//                new SimulationAttribute<Position>(true, false, RotationUnit.DEGREE, new Position(8.491976, 53.441987, 0), "position"),
//                new SimulationAttribute<Position>(true, false, RotationUnit.DEGREE, new Position(8.498406, 53.486163, 0), "position"),
//                new SimulationAttribute<Position>(true, false, RotationUnit.DEGREE, new Position(8.521474, 53.504622, 0), "position"),
//                new SimulationAttribute<Position>(true, false, RotationUnit.DEGREE, new Position(8.564613, 53.523593, 0), "position"),
//                new SimulationAttribute<Position>(true, false, RotationUnit.DEGREE, new Position(8.559649, 53.542790, 0), "position"),
//                new SimulationAttribute<Position>(true, false, RotationUnit.DEGREE, new Position(8.525297, 53.565923, 0), "position"),
//                new SimulationAttribute<Position>(true, false, RotationUnit.DEGREE, new Position(8.466058, 53.581205, 0), "position"),
//                new SimulationAttribute<Position>(true, false, RotationUnit.DEGREE, new Position(8.337918, 53.701863, 0), "position"),
//                new SimulationAttribute<Position>(true, false, RotationUnit.DEGREE, new Position(8.188142, 53.763087, 0), "position"),
//                new SimulationAttribute<Position>(true, false, RotationUnit.DEGREE, new Position(8.004147, 53.791264, 0), "position"),
//                new SimulationAttribute<Position>(true, false, RotationUnit.DEGREE, new Position(7.885223, 53.799216, 0), "position"),
//                new SimulationAttribute<Position>(true, false, RotationUnit.DEGREE, new Position(7.843778, 53.791388, 0), "position"),
//                new SimulationAttribute<Position>(true, false, RotationUnit.DEGREE, new Position(7.838944, 53.788447, 0), "position"),
//                new SimulationAttribute<Position>(true, false, RotationUnit.DEGREE, new Position(7.864120, 53.771498, 0), "position"),
//                new SimulationAttribute<Position>(true, false, RotationUnit.DEGREE, new Position(7.867186, 53.772021, 0), "position"),
//                new SimulationAttribute<Position>(true, false, RotationUnit.DEGREE, new Position(7.867663, 53.774356, 0), "position"),
//                new SimulationAttribute<Position>(true, false, RotationUnit.DEGREE, new Position(7.869258, 53.776224, 0), "position")
//        );
//        ship2.setRoute(route);
//        ship2.addComponent(new SpeedSensor(null));
//
//        /**
//         * MSC ELODIE
//         * https://www.marinetraffic.com/en/ais/details/ships/shipid:3308713/mmsi:255806494/imo:9704972/vessel:MSC_ELODIE
//         */
//        ContainerShip ship3 = new ContainerShip(
//                new SimulationAttribute<Boolean>(true, false, NoUnit.get(), true, "physical"),
//                new SimulationAttribute<Position>(true, false, RotationUnit.DEGREE, new Position(8.155173, 53.589058, 0.0), "position"),
//                null,
//                new SimulationAttribute<Double>(true, false, RotationUnit.DEGREE, 0.0, "rotation"),
//                new SimulationAttribute<Double>(true, false, TimeUnit.SECOND, 1.0, "timeStepSize"),
//                new SimulationAttribute<PossibleDomains>(true, false, NoUnit.get(), PossibleDomains.MARITIME, "assignedDomain"),
//                null,
//                new SimulationAttribute<Double>(true, false, SpeedUnit.KNOTS, 18.3, "speed"),
//                null,
//                new SimulationAttribute<Position>(true, false, RotationUnit.DEGREE, new Position(8.155173, 53.589058, 0.0), "origin"),
//                new SimulationAttribute<Double>(true, false, RotationUnit.DEGREE, 0.0, "course"),
//                new SimulationAttribute<Double>(true, false, DistanceUnit.METER, 9.9, "draught"),
//                new SimulationAttribute<String>(true, false, NoUnit.get(), "Lissabon", "homeHarbour"),
//                new SimulationAttribute<String>(true, false, NoUnit.get(), "MSC Elodie", "vesselName"),
//                new SimulationAttribute<String>(true, false, NoUnit.get(), "Portugal [PT]", "flag"),
//                new SimulationAttribute<String>(true, false, NoUnit.get(), "9704972", "imo"),
//                new SimulationAttribute<String>(true, false, NoUnit.get(), "255806494", "mmsi"),
//                new SimulationAttribute<String>(true, false, NoUnit.get(), "CQEV3", "callsign"),
//                new SimulationAttribute<Double>(true, false, WeightUnit.TON, 109576.0, "loadCapacity"),
//                new SimulationAttribute<Integer>(true, false, NoUnit.get(), 8800, "containerCapacity"),
//                new SimulationAttribute<Double>(true, false, DistanceUnit.METER, 299.97, "length"),
//                new SimulationAttribute<Double>(true, false, DistanceUnit.METER, 48.32, "width")
//        );
//        ship3.setMaxSpeed(new SimulationAttribute<Double>(true, false, SpeedUnit.KNOTS, 23.2, "maxSpeed"));
//        ship3.setAcceleration(new SimulationAttribute<Double>(true, false,
//                AccelerationUnit.METERSPERSECONDSSQUARED, getAccel(ship3.getLength().getValue(),
//                ship3.getMaxSpeed().getValue()), "acceleration"));
//        geometricShapeFactory.setNumPoints(4);
//        geometricShapeFactory.setWidth(ship3.getWidth().getValue());
//        geometricShapeFactory.setHeight(ship3.getLength().getValue());
//        Geometry ship3Shape = geometricShapeFactory.createRectangle();
//        ship3.setForm(new SimulationAttribute<Geometry>(true, false, DistanceUnit.METER, ship3Shape, "form"));
//
//        ship3.setBehaviour(new FollowGivenRouteBehaviour());
//        List<SimulationAttribute<Position>> route3 = Arrays.asList(
//                new SimulationAttribute<Position>(true, false, RotationUnit.DEGREE, new Position(8.155173, 53.589058, 0), "position"),
//                new SimulationAttribute<Position>(true, false, RotationUnit.DEGREE, new Position(8.159788, 53.602260, 0), "position"),
//                new SimulationAttribute<Position>(true, false, RotationUnit.DEGREE, new Position(8.148588, 53.630226, 0), "position"),
//                new SimulationAttribute<Position>(true, false, RotationUnit.DEGREE, new Position(8.126432, 53.651158, 0), "position"),
//                new SimulationAttribute<Position>(true, false, RotationUnit.DEGREE, new Position(8.095825, 53.673563, 0), "position"),
//                new SimulationAttribute<Position>(true, false, RotationUnit.DEGREE, new Position(8.079512, 53.701474, 0), "position"),
//                new SimulationAttribute<Position>(true, false, RotationUnit.DEGREE, new Position(8.067947, 53.733083, 0), "position"),
//                new SimulationAttribute<Position>(true, false, RotationUnit.DEGREE, new Position(8.063566, 53.762844, 0), "position"),
//                new SimulationAttribute<Position>(true, false, RotationUnit.DEGREE, new Position(8.059956, 53.791966, 0), "position"),
//                new SimulationAttribute<Position>(true, false, RotationUnit.DEGREE, new Position(8.037943, 53.828029, 0), "position"),
//                new SimulationAttribute<Position>(true, false, RotationUnit.DEGREE, new Position(7.955380, 53.859326, 0), "position")
//
//        );
//        ship3.setRoute(route3);
//
//        Engine ship3Engine = new Engine();
//        ship3Engine.setBehaviour(new DefectEngineBehaviour());
//
//        TimeGoal timeGoal = new TimeGoal(new SimulationAttribute<>(true, false, TimeUnit.SECOND, 1200.0, "targetTime"));
//        ship3Engine.getBehaviour().addGoal(new Goal<IGoal>(timeGoal));
//
//        ship3.addComponent(ship3Engine);
//
//        /**
//         * DELPHIS RIGA
//         * https://www.marinetraffic.com/en/ais/details/ships/shipid:4702918/mmsi:477234800/imo:9780665/vessel:DELPHIS_RIGA
//         */
//        ContainerShip ship4 = new ContainerShip(
//                new SimulationAttribute<Boolean>(true, false, NoUnit.get(), true, "physical"),
//                new SimulationAttribute<Position>(true, false, RotationUnit.DEGREE, new Position(8.008945, 53.801381, 0.0), "position"),
//                null,
//                new SimulationAttribute<Double>(true, false, RotationUnit.DEGREE, 180.0, "rotation"),
//                new SimulationAttribute<Double>(true, false, TimeUnit.SECOND, 1.0, "timeStepSize"),
//                new SimulationAttribute<PossibleDomains>(true, false, NoUnit.get(), PossibleDomains.MARITIME, "assignedDomain"),
//                null,
//                new SimulationAttribute<Double>(true, false, SpeedUnit.KNOTS, 6.5, "speed"),
//                null,
//                new SimulationAttribute<Position>(true, false, RotationUnit.DEGREE, new Position(8.008945, 53.801381, 0.0), "origin"),
//                new SimulationAttribute<Double>(true, false, RotationUnit.DEGREE, 180.0, "course"),
//                new SimulationAttribute<Double>(true, false, DistanceUnit.METER, 7.0, "draught"),
//                new SimulationAttribute<String>(true, false, NoUnit.get(), "Hong Kong", "homeHarbour"),
//                new SimulationAttribute<String>(true, false, NoUnit.get(), "DELPHIS RIGA", "vesselName"),
//                new SimulationAttribute<String>(true, false, NoUnit.get(), "Hong Kong [HK]", "flag"),
//                new SimulationAttribute<String>(true, false, NoUnit.get(), "9780665", "imo"),
//                new SimulationAttribute<String>(true, false, NoUnit.get(), "477234800", "mmsi"),
//                new SimulationAttribute<String>(true, false, NoUnit.get(), "VERQN8", "callsign"),
//                new SimulationAttribute<Double>(true, false, WeightUnit.TON, 24700.0, "loadCapacity"),
//                new SimulationAttribute<Integer>(true, false, NoUnit.get(), 1926, "containerCapacity"),
//                new SimulationAttribute<Double>(true, false, DistanceUnit.METER, 177.56, "length"),
//                new SimulationAttribute<Double>(true, false, DistanceUnit.METER, 30.54, "width")
//        );
//        ship4.setMaxSpeed(new SimulationAttribute<Double>(true, false, SpeedUnit.KNOTS, 14.6, "maxSpeed"));
//        ship4.setAcceleration(new SimulationAttribute<Double>(true, false,
//                AccelerationUnit.METERSPERSECONDSSQUARED, getAccel(ship4.getLength().getValue(),
//                ship4.getMaxSpeed().getValue()), "acceleration"));
//        geometricShapeFactory.setNumPoints(4);
//        geometricShapeFactory.setWidth(ship4.getWidth().getValue());
//        geometricShapeFactory.setHeight(ship4.getLength().getValue());
//        Geometry ship4Shape = geometricShapeFactory.createRectangle();
//        ship4.setForm(new SimulationAttribute<Geometry>(true, false, DistanceUnit.METER, ship4Shape, "form"));
//
//        ship4.setBehaviour(new FollowGivenRouteBehaviour());
//        List<SimulationAttribute<Position>> route4 = Arrays.asList(
//                new SimulationAttribute<Position>(true, false, RotationUnit.DEGREE, new Position(8.008945, 53.801381, 0), "position"),
//                new SimulationAttribute<Position>(true, false, RotationUnit.DEGREE, new Position(8.034819, 53.793288, 0), "position"),
//                new SimulationAttribute<Position>(true, false, RotationUnit.DEGREE, new Position(8.040818, 53.773237, 0), "position"),
//                new SimulationAttribute<Position>(true, false, RotationUnit.DEGREE, new Position(8.047009, 53.754732, 0), "position"),
//                new SimulationAttribute<Position>(true, false, RotationUnit.DEGREE, new Position(8.062728, 53.723404, 0), "position"),
//                new SimulationAttribute<Position>(true, false, RotationUnit.DEGREE, new Position(8.0756378, 53.6932958, 0), "position"),
//                new SimulationAttribute<Position>(true, false, RotationUnit.DEGREE, new Position(8.123662, 53.656587, 0), "position"),
//                new SimulationAttribute<Position>(true, false, RotationUnit.DEGREE, new Position(8.149547, 53.630216, 0), "position"),
//                new SimulationAttribute<Position>(true, false, RotationUnit.DEGREE, new Position(8.153631, 53.609776, 0), "position"),
//                new SimulationAttribute<Position>(true, false, RotationUnit.DEGREE, new Position(8.152364, 53.598622, 0), "position")
//        );
//        ship4.setRoute(route4);
//
//        /**
//         * VB Jade
//         * https://www.marinetraffic.com/en/ais/details/ships/shipid:133283/mmsi:211334830/imo:9212278/vessel:VB_JADE
//         */
//        Vessel ship5 = new Vessel(
//                new SimulationAttribute<Boolean>(true, false, NoUnit.get(), true, "physical"),
//                new SimulationAttribute<Position>(true, false, RotationUnit.DEGREE, new Position(8.148555, 53.603040, 0), "position"),
//                null,
//                new SimulationAttribute<Double>(true, false, RotationUnit.DEGREE, 45.0, "rotation"),
//                new SimulationAttribute<Double>(true, false, TimeUnit.SECOND, 1.0, "timeStepSize"),
//                new SimulationAttribute<PossibleDomains>(true, false, NoUnit.get(), PossibleDomains.MARITIME, "assignedDomain"),
//                null,
//                new SimulationAttribute<Double>(true, false, SpeedUnit.KNOTS, 8.4, "speed"),
//                null,
//                new SimulationAttribute<Position>(true, false, RotationUnit.DEGREE, new Position(8.148555, 53.603040, 0), "origin"),
//                new SimulationAttribute<Double>(true, false, RotationUnit.DEGREE, 254.0, "course"),
//                null,
//                new SimulationAttribute<String>(true, false, NoUnit.get(), "BREMEN", "homeHarbour"),
//                new SimulationAttribute<String>(true, false, NoUnit.get(), "VB Jade", "vesselName"),
//                new SimulationAttribute<String>(true, false, NoUnit.get(), "Germany [DE]", "flag"),
//                null,
//                new SimulationAttribute<String>(true, false, NoUnit.get(), "211334830", "mmsi"),
//                new SimulationAttribute<String>(true, false, NoUnit.get(), "DDUT", "callsign"),
//                null,
//                new SimulationAttribute<Double>(true, false, DistanceUnit.METER, 33.46, "length"),
//                new SimulationAttribute<Double>(true, false, DistanceUnit.METER, 12.5, "width")
//        );
//        ship5.setMaxSpeed(new SimulationAttribute<Double>(true, false, SpeedUnit.KNOTS, 10.1, "maxSpeed"));
//        ship5.setAcceleration(new SimulationAttribute<Double>(true, false,
//                AccelerationUnit.METERSPERSECONDSSQUARED, getAccel(ship5.getLength().getValue(),
//                ship5.getMaxSpeed().getValue()), "acceleration"));
//        geometricShapeFactory.setNumPoints(4);
//        geometricShapeFactory.setWidth(ship5.getWidth().getValue());
//        geometricShapeFactory.setHeight(ship5.getLength().getValue());
//        Geometry ship5Shape = geometricShapeFactory.createRectangle();
//        ship5.setForm(new SimulationAttribute<Geometry>(true, false, DistanceUnit.METER, ship5Shape, "form"));
//
//        ship5.setBehaviour(new EmergencyRespondBehaviour());
//
//
//        //
//        //  Scenario Creation
//        //
//        Scenario scenario = new Scenario();
//        scenario.addSimulationObject(containerShip);
//        scenario.addSimulationObject(ship1);
//        scenario.addSimulationObject(ship2);
//        scenario.addSimulationObject(ship3);
//        scenario.addSimulationObject(ship4);
//        scenario.addSimulationObject(ship5);
//
//
//        //
//        //  Observer configuration
//        //
//        Observer observer = new Observer(5, new ObserverWebSocketConfig("localhost", 3001));
//        observer.addAttributeToObserve(containerShip.getId(), containerShip.getPosition().getId());
//        observer.addAttributeToObserve(containerShip.getId(), containerShip.getVesselName().getId());
//        observer.addAttributeToObserve(containerShip.getId(), containerShip.getRotation().getId());
//        observer.addAttributeToObserve(containerShip.getId(), containerShip.getSpeed().getId());
//
//        observer.addAttributeToObserve(ship1.getId(), ship1.getPosition().getId());
//        observer.addAttributeToObserve(ship1.getId(), ship1.getVesselName().getId());
//        observer.addAttributeToObserve(ship1.getId(), ship1.getRotation().getId());
//        observer.addAttributeToObserve(ship1.getId(), ship1.getSpeed().getId());
//
//        observer.addAttributeToObserve(ship2.getId(), ship2.getPosition().getId());
//        observer.addAttributeToObserve(ship2.getId(), ship2.getVesselName().getId());
//        observer.addAttributeToObserve(ship2.getId(), ship2.getRotation().getId());
//        observer.addAttributeToObserve(ship2.getId(), ship2.getSpeed().getId());
//
//        observer.addAttributeToObserve(ship3.getId(),ship3.getPosition().getId());
//        observer.addAttributeToObserve(ship3.getId(),ship3.getVesselName().getId());
//        observer.addAttributeToObserve(ship3.getId(),ship3.getRotation().getId());
//        observer.addAttributeToObserve(ship3.getId(),ship3.getSpeed().getId());
//        observer.addAttributeToObserve(ship3.getId(),ship3.getEmergencyDeclared().getId());
//
//        observer.addAttributeToObserve(ship4.getId(),ship4.getPosition().getId());
//        observer.addAttributeToObserve(ship4.getId(),ship4.getVesselName().getId());
//        observer.addAttributeToObserve(ship4.getId(), ship4.getRotation().getId());
//        observer.addAttributeToObserve(ship4.getId(), ship4.getSpeed().getId());
//
//        observer.addAttributeToObserve(ship5.getId(), ship5.getPosition().getId());
//        observer.addAttributeToObserve(ship5.getId(), ship5.getVesselName().getId());
//        observer.addAttributeToObserve(ship5.getId(), ship5.getRotation().getId());
//        observer.addAttributeToObserve(ship5.getId(), ship5.getSpeed().getId());
//        observer.addLoggingType(LoggingType.WEBSOCKET);
//        scenario.addObserver(observer);
//
//
//        //
//        //  Restriction Definiton
//        //
//        Restriction<Double> speedRestriction1 = Restriction.getNumericalRestriction("speedInKmH", 35.0, NumericalRestrictionType.MAX, SpeedUnit.KILOMETERSPERHOUR);
//        WKTReader wktReader = new WKTReader();
//        //Weser Speed Restriction Zone
//        Geometry restriction1Form = wktReader.read("POLYGON ((8.359737 53.582825, 8.876953 53.582825, 8.876953 53.064945, 8.359737 53.064945, 8.359737 53.582825))");
//        speedRestriction1.setForm(new SimulationAttribute<Geometry>(true, false, NoUnit.get(), restriction1Form, "form"));
//
//        Restriction<Double> speedRestriction2 = Restriction.getNumericalRestriction("speedInKmH", SimulationUnit.convert(SpeedUnit.KNOTS, 12.0, SpeedUnit.KILOMETERSPERHOUR), NumericalRestrictionType.MAX, SpeedUnit.KILOMETERSPERHOUR);
//        //Island Speed Restriction Zone
//        Geometry restrictionForm2 = wktReader.read("POLYGON ((7.834969 53.800043, 8.027401 53.800043, 8.027401 53.736325, 7.834969 53.736325, 7.834969 53.800043))");
//        speedRestriction2.setForm(new SimulationAttribute<Geometry>(true, false, NoUnit.get(), restrictionForm2, "form"));
//
//
//        //
//        //  Obstacle Defintion
//        //
//        ArrayList<Obstacle> obstacles = new ArrayList<>();
//        /**
//         * Langlütjen IIa
//         */
//        String wkt = "POLYGON ((8.497397 53.570704, 8.498816 53.570704, 8.498816 53.572262, 8.497397 53.572262, 8.497397 53.570704))";
//        Geometry geom = wktReader.read(wkt);
//        Coordinate[] coordinates = geom.getEnvelope().getCoordinates();
//        double middleX = (coordinates[2].x - coordinates[0].x) * 0.5 + coordinates[0].x;
//        double middleY = (coordinates[2].y - coordinates[0].y) * 0.5 + coordinates[0].y;
//        Position position = new Position(middleX, middleY, 0);
//        obstacles.add(new Obstacle(
//                new SimulationAttribute<Boolean>(true, false, NoUnit.get(), true, "physical"),
//                new SimulationAttribute<Position>(true, false, RotationUnit.DEGREE, position, "position"),
//                new SimulationAttribute<Geometry>(true, false, NoUnit.get(), geom, "form"),
//                null,
//                new SimulationAttribute<Double>(true, false, TimeUnit.HOUR, Double.MAX_VALUE, "timeStepSize")));
//
//        /**
//         * Langlütjen IIb
//         */
//        wkt = "POLYGON ((8.493619 53.574375, 8.495098 53.572844, 8.496813 53.573456, 8.495273 53.574853, 8.493619 53.574375))";
//        geom = wktReader.read(wkt);
//        coordinates = geom.getEnvelope().getCoordinates();
//        middleX = (coordinates[2].x - coordinates[0].x) * 0.5 + coordinates[0].x;
//        middleY = (coordinates[2].y - coordinates[0].y) * 0.5 + coordinates[0].y;
//        position = new Position(middleX, middleY, 0);
//        obstacles.add(new Obstacle(
//                new SimulationAttribute<Boolean>(true, false, NoUnit.get(), true, "physical"),
//                new SimulationAttribute<Position>(true, false, RotationUnit.DEGREE, position, "position"),
//                new SimulationAttribute<Geometry>(true, false, NoUnit.get(), geom, "form"),
//                null,
//                new SimulationAttribute<Double>(true, false, TimeUnit.HOUR, Double.MAX_VALUE, "timeStepSize")));
//        obstacles.forEach(scenario::addSimulationObject);
//
//
//        //
//        //  Restrictions to Infrastructure Mapping
//        //
//        try {
//            ArrayList<Infrastructure> infrastructures = (new GeoPackageReader()).read(GeoPackageReader.class.getResource("/library/services/geodata/waterbody.gpkg").getPath());
//            infrastructures.forEach(t -> t.addRestriction(speedRestriction1));
//            infrastructures.forEach(t -> t.addRestriction(speedRestriction2));
//            infrastructures.forEach(scenario::addSimulationObject);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//        //
//        //  Basic Scenario Config
//        //
//        scenario.setSimulationIterations(3000);
//
//
//        //
//        //  Writing Scenario To Disk
//        //
//        try {
//            File file = new File("\\example\\");
//            if (!file.exists()) {
//                file.mkdirs();
//            }
//            System.out.println(file.getAbsolutePath());
//            ScenarioConverter.convertToXML(scenario, "\\example\\", "ExampleScenario");
//        } catch (JAXBException | IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
