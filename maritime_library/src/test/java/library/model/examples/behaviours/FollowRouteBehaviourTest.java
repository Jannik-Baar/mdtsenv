//package library.model.examples.behaviours;
//
//import library.model.maritime.Vessel;
//import library.model.maritime.behaviours.FollowRouteBehaviour;
//import library.model.dto.scenario.Scenario;
//import library.model.simulation.FormDummy;
//import library.model.simulation.Position;
//import library.model.simulation.SimulationAttribute;
//import library.model.simulation.units.TimeUnit;
//import library.model.traffic.Infrastructure;
//import library.model.traffic.PossibleDomains;
//import library.services.geodata.GeoPackageReader;
//import library.services.geodata.MapDataProvider;
//import org.junit.jupiter.api.Test;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//class FollowRouteBehaviourTest {
//
//    @Test
//    void nextStep() throws IOException {
//        //TrafficParticipant trafficParticipant = new TrafficParticipant(true, new Position(1.0, 1.0, 0), new FormDummy(), 0.0, 1, PossibleDomains.MARITIME, 500.0, 1.0, 0.0, new Position(1.0, 1.0, 0.0), 1.0, 1.0);
//        Vessel trafficParticipant = new Vessel(true, new Position(1.0, 1.0, 0),
//                new FormDummy(), 0, 1, PossibleDomains.MARITIME, 12.0, 15, 0.0,
//                new Position(1.0, 1.0, 0), 1.0, 1.0, "Miami",
//                "Example Vessel", "Indonesia", "34534", "34342", "1337", 43.4, 12.2, 34.3);
//        List<Position> route = new ArrayList<>();
//        route.add(new Position(0.02, 0.02, 0.0));
//        route.add(new Position(0.05, 0.02, 0.0));
//        route.add(new Position(0.05, 0.05, 0.0));
//        route.add(new Position(0.05, 0.1, 0.0));
//        route.add(new Position(0.0, 0.1, 0.0));
//        route.add(new Position(0.0, 0.0, 0.0));
//
//        FollowRouteBehaviour behaviour = new FollowRouteBehaviour();
//        behaviour.setRoute(route);
//        behaviour.setSimulationObject(trafficParticipant);
//
//        Scenario scenario = new Scenario();
//        ArrayList<Infrastructure> infrastructures = (new GeoPackageReader()).read(GeoPackageReader.class.getResource("/library/services/geodata/waterbody.gpkg").getPath());
//        infrastructures.get(0).getFormString();
//        infrastructures.forEach(scenario::addSimulationObject);
//        MapDataProvider mapDataProvider = new MapDataProvider(scenario);
//        MapDataProvider.addToMap(trafficParticipant, mapDataProvider);
//
//        while (!behaviour.getGoalReached()) {
//            behaviour.nextStep(new SimulationAttribute<>(true, true, TimeUnit.MINUTE, 1.0, "~"));
//        }
//    }
//}