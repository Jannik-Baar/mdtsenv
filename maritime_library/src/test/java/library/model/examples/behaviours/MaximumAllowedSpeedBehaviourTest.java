//package library.model.examples.behaviours;
//
//import library.model.maritime.Vessel;
//import library.model.maritime.behaviours.MaximumAllowedSpeedBehaviour;
//import library.model.dto.scenario.Scenario;
//import library.model.simulation.FormDummy;
//import library.model.simulation.enums.NumericalRestrictionType;
//import library.model.simulation.Position;
//import library.model.limitations.Restriction;
//import library.model.simulation.SimulationAttribute;
//import library.model.simulation.Goal;
//import library.model.simulation.IGoal;
//import library.model.examples.goals.PositionalGoal;
//import library.model.simulation.units.RotationUnit;
//import library.model.simulation.units.SpeedUnit;
//import library.model.simulation.units.TimeUnit;
//import library.model.traffic.Infrastructure;
//import library.model.traffic.PossibleDomains;
//import library.services.geodata.GeoPackageReader;
//import library.services.geodata.MapDataProvider;
//import org.junit.jupiter.api.Test;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//class MaximumAllowedSpeedBehaviourTest {
//
//    @Test
//    void nextStep() throws IOException {
//        MaximumAllowedSpeedBehaviour behaviour = new MaximumAllowedSpeedBehaviour();
//        Vessel trafficParticipant = new Vessel(true, new Position(8.482088, 53.328312, 0),
//                new FormDummy(), 0, 1, PossibleDomains.MARITIME, 12.0, 0.0001, 0.0,
//                new Position(8.487773, 53.223498, 0), 1.0, 1.0, "Miami",
//                "Example Vessel", "Indonesia", "34534", "34342", "1337", 43.4, 12.2, 34.3);
//        trafficParticipant.setMaxSpeed(new SimulationAttribute<>(true, true, SpeedUnit.KILOMETERSPERHOUR, 130.0, "maxspeed"));
//        List<SimulationAttribute<Position>> route = Arrays.asList(
//                new SimulationAttribute<Position>(true, true, RotationUnit.DEGREE, new Position(8.484859, 53.329997, 0), "position"),
//                new SimulationAttribute<Position>(true, true, RotationUnit.DEGREE, new Position(8.486962, 53.328524, 0), "position"),
//                new SimulationAttribute<Position>(true, true, RotationUnit.DEGREE, new Position(8.496832, 53.337510, 0), "position"),
//                new SimulationAttribute<Position>(true, true, RotationUnit.DEGREE, new Position(8.502373, 53.354140, 0), "position"),
//                new SimulationAttribute<Position>(true, true, RotationUnit.DEGREE, new Position(8.497692, 53.383581, 0), "position"),
//                new SimulationAttribute<Position>(true, true, RotationUnit.DEGREE, new Position(8.491976, 53.441987, 0), "position"),
//                new SimulationAttribute<Position>(true, true, RotationUnit.DEGREE, new Position(8.498406, 53.486163, 0), "position"),
//                new SimulationAttribute<Position>(true, true, RotationUnit.DEGREE, new Position(8.521474, 53.504622, 0), "position"),
//                new SimulationAttribute<Position>(true, true, RotationUnit.DEGREE, new Position(8.563119, 53.524286, 0), "position"),
//                new SimulationAttribute<Position>(true, true, RotationUnit.DEGREE, new Position(8.559649, 53.542790, 0), "position"),
//                new SimulationAttribute<Position>(true, true, RotationUnit.DEGREE, new Position(8.463293, 53.625748, 0), "position"),
//                new SimulationAttribute<Position>(true, true, RotationUnit.DEGREE, new Position(8.337918, 53.701863, 0), "position"),
//                new SimulationAttribute<Position>(true, true, RotationUnit.DEGREE, new Position(8.188142, 53.763087, 0), "position"),
//                new SimulationAttribute<Position>(true, true, RotationUnit.DEGREE, new Position(8.004147, 53.791264, 0), "position"),
//                new SimulationAttribute<Position>(true, true, RotationUnit.DEGREE, new Position(7.885223, 53.799216, 0), "position"),
//                new SimulationAttribute<Position>(true, true, RotationUnit.DEGREE, new Position(7.838944, 53.788447, 0), "position"),
//                new SimulationAttribute<Position>(true, true, RotationUnit.DEGREE, new Position(7.864120, 53.771498, 0), "position"),
//                new SimulationAttribute<Position>(true, true, RotationUnit.DEGREE, new Position(7.867186, 53.772021, 0), "position"),
//                new SimulationAttribute<Position>(true, true, RotationUnit.DEGREE, new Position(7.867663, 53.774356, 0), "position"),
//                new SimulationAttribute<Position>(true, true, RotationUnit.DEGREE, new Position(7.869258, 53.776224, 0), "position")
//        );
//        trafficParticipant.setRoute(route);
//        behaviour.setSimulationObject(trafficParticipant);
//
//        Scenario scenario = new Scenario();
//        ArrayList<Infrastructure> infrastructures = (new GeoPackageReader()).read(GeoPackageReader.class.getResource("/library/services/geodata/waterbody.gpkg").getPath());
//        infrastructures.get(0).addRestriction(Restriction.getNumericalRestriction("speedInKmH", 30.0, NumericalRestrictionType.MAX, SpeedUnit.KILOMETERSPERHOUR));
//        infrastructures.get(0).getFormString();
//        infrastructures.forEach(scenario::addSimulationObject);
//        MapDataProvider mapDataProvider = new MapDataProvider(scenario);
//        behaviour.setMapDataProvider(mapDataProvider);
//
//        PositionalGoal positionalGoal = new PositionalGoal(trafficParticipant, new Position(8.43981, 53.64925, 0));
//        behaviour.addGoal(new Goal<IGoal>(positionalGoal));
//
//        while(!behaviour.getGoalReached()) {
//            behaviour.nextStep(new SimulationAttribute<>(true, true, TimeUnit.MINUTE, 1.0, "~"));
//        }
//    }
//}