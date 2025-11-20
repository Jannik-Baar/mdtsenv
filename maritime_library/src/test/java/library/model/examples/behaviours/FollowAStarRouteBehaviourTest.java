//package library.model.examples.behaviours;
//
//import library.model.maritime.Vessel;
//import library.model.maritime.behaviours.FollowAStarRouteBehaviour;
//import library.model.dto.scenario.Scenario;
//import library.model.simulation.FormDummy;
//import library.model.simulation.Position;
//import library.model.simulation.SimulationAttribute;
//import library.model.simulation.Goal;
//import library.model.simulation.IGoal;
//import library.model.examples.goals.PositionalGoal;
//import library.model.simulation.units.TimeUnit;
//import library.model.traffic.Infrastructure;
//import library.model.traffic.PossibleDomains;
//import library.model.traffic.TrafficParticipant;
//import library.services.geodata.GeoPackageReader;
//import library.services.geodata.MapDataProvider;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.io.IOException;
//import java.util.ArrayList;
//
//class FollowAStarRouteBehaviourTest {
//
//    private FollowAStarRouteBehaviour followAStarRouteBehaviour;
//
//    @BeforeEach
//    void setUp() throws IOException {
//        this.followAStarRouteBehaviour = new FollowAStarRouteBehaviour();
//        TrafficParticipant trafficParticipant = new TrafficParticipant(true, new Position(8.580472, 53.536501, 0),
//                                                                       new FormDummy(), 0, 1, PossibleDomains.MARITIME, 12.0, 10.0, 3.0, new Position(8.580472, 53.536501, 0), 1.0, 1.0);
//        followAStarRouteBehaviour.setSimulationObject(trafficParticipant);
//
//        Scenario scenario = new Scenario();
//        ArrayList<Infrastructure> infrastructures = (new GeoPackageReader()).read(GeoPackageReader.class.getResource("/library/services/geodata/waterbody.gpkg").getPath());
//        infrastructures.get(0).getFormString();
//        infrastructures.forEach(scenario::addSimulationObject);
//        MapDataProvider mapDataProvider = new MapDataProvider(scenario);
//        followAStarRouteBehaviour.setMapDataProvider(mapDataProvider);
//
//        PositionalGoal positionalGoal = new PositionalGoal(trafficParticipant, new Position(8.804045, 53.074050, 0));
//        //PositionalGoal positionalGoal = new PositionalGoal(trafficParticipant, new Position(53.92220,8.52319, 0));
//        followAStarRouteBehaviour.addGoal(new Goal<IGoal>(positionalGoal));
//    }
//
//    @Test
//    void nextStep() throws IOException {
//        FollowAStarRouteBehaviour behaviour = new FollowAStarRouteBehaviour();
//        //TrafficParticipant trafficParticipant = new TrafficParticipant(true, new Position(8.574818, 53.535395, 0),
//        //new FormDummy(), 0, 1, PossibleDomains.MARITIME, 12.0, 0.0001, 0.0, new Position(8.574818, 53.535395, 0), 1.0, 1.0);
//        Vessel trafficParticipant = new Vessel(true, new Position(8.574818, 53.535395, 0),
//                new FormDummy(), 0, 1, PossibleDomains.MARITIME, 12.0, 15, 0.0,
//                new Position(8.574818, 53.535395, 0), 1.0, 1.0, "Miami",
//                "Example Vessel", "Indonesia", "34534", "34342", "1337", 43.4, 12.2, 34.3);
//        behaviour.setSimulationObject(trafficParticipant);
//
//        Scenario scenario = new Scenario();
//        ArrayList<Infrastructure> infrastructures = (new GeoPackageReader()).read(GeoPackageReader.class.getResource("/library/services/geodata/waterbody.gpkg").getPath());
//        infrastructures.get(0).getFormString();
//        infrastructures.forEach(scenario::addSimulationObject);
//        MapDataProvider mapDataProvider = new MapDataProvider(scenario);
//        MapDataProvider.addToMap(trafficParticipant, mapDataProvider);
//
//        PositionalGoal positionalGoal = new PositionalGoal(trafficParticipant, new Position(8.4906635, 53.3280752, 0));
//        behaviour.addGoal(new Goal<IGoal>(positionalGoal));
//
//        while(!behaviour.getGoalReached()) {
//            behaviour.nextStep(new SimulationAttribute<>(true, true, TimeUnit.MINUTE, 10.0, "~"));
//        }
//    }
//}