//package library.model.examples.behaviours;
//
//import library.model.maritime.Vessel;
//import library.model.maritime.behaviours.FollowGivenRouteBehaviour;
//import library.model.dto.scenario.Scenario;
//import library.model.simulation.FormDummy;
//import library.model.simulation.Position;
//import library.model.simulation.SimulationAttribute;
//import library.model.simulation.units.NoUnit;
//import library.model.simulation.units.TimeUnit;
//import library.model.traffic.Infrastructure;
//import library.model.traffic.PossibleDomains;
//import library.services.geodata.GeoPackageReader;
//import library.services.geodata.MapDataProvider;
//import org.junit.jupiter.api.Test;
//
//import java.io.IOException;
//import java.util.ArrayList;
//
//class FollowGivenRouteBehaviourTest {
//
//    @Test
//    void nextStep() throws IOException {
//        //TrafficParticipant trafficParticipant = new TrafficParticipant(true, new Position(8.604875, 53.70372, 0), new FormDummy(), 0.0, 1, PossibleDomains.MARITIME, 500.0, 1.0, 0.0, new Position(1.0, 1.0, 0.0), 1.0, 1.0);
//        Vessel trafficParticipant = new Vessel(true, new Position(8.604875, 53.70372, 0),
//                new FormDummy(), 0, 1, PossibleDomains.MARITIME, 12.0, 5, 0.0,
//                new Position(8.604875, 53.70372, 0), 1.0, 1.0, "Miami",
//                "Example Vessel", "Indonesia", "34534", "34342", "1337", 43.4, 12.2, 34.3);
//        ArrayList<SimulationAttribute<Position>> routeList = new ArrayList<>();
//        String str = "8.504875,53.60372 8.502299,53.603361 8.506157,53.600215 8.520821,53.586204 8.539665,53.566975 8.554086,53.552254 8.566599,53.539475 8.568922,53.535166 8.566302,53.527458 8.540814,53.5165 8.525785,53.5101 8.516886,53.505543 8.511805,53.50074,0 8.504165,53.494152,0 8.497695,53.487385,0 8.496078,53.485227,0 8.495684,53.484163,0 8.494242,53.477775,0 8.494107,53.475939,0 8.493441,53.470921,0 8.491654,53.45951,0 8.491334,53.448871,0 8.492262,53.432548,0 8.492814,53.426496,0 8.493433,53.419616,0 8.494199,53.413532,0 8.495492,53.404484,0 8.495411,53.403242,0 8.497085,53.389188,0 8.497592,53.385136,0 8.498763,53.376553,0 8.500096,53.367055,0 8.50048,53.363628,0 8.500665,53.362242,0 8.500658,53.357325,0 8.50106,53.353061,0 8.50106,53.351945,0 8.501429,53.345434,0 8.499983,53.342873,0 8.494255,53.333477,0 8.49394,53.333039,0 8.492191,53.330425,0 8.489396,53.325926,0 8.489218,53.321647,0 8.490503,53.316006,0 8.49067,53.315124,0 8.491623,53.308249,0 8.49182,53.306208,0 8.49216,53.303974,0 8.491831,53.297816,0 8.490703,53.292866,0 8.487464,53.278644,0 8.485286,53.26915,0 8.483133,53.260154,0 8.482889,53.259289,0 8.482741,53.258734,0 8.482234,53.255195,0 8.482255,53.255148,0 8.48167,53.251699,1 8.481039,53.247679,1 8.481151,53.240255,1 8.481742,53.235544,1 8.482284,53.233831,1 8.483989,53.229432,1 8.488538,53.222809,1 8.489495,53.221686,1 8.493878,53.21736,1 8.494455,53.216801,1 8.499001,53.212453,1 8.50242,53.209373,1 8.507796,53.204187,1 8.510732,53.201354,1 8.51723,53.197402,1 8.519541,53.196019,1 8.521754,53.194879,1 8.523652,53.193991,1 8.53094,53.19054,1 8.538906,53.187817,1 8.548574,53.184545,1 8.558342,53.1812,1 8.560805,53.180396,1 8.566323,53.178565,1 8.573964,53.175958,1 8.579132,53.174131,1 8.580466,53.173776,1 8.583492,53.172759,1 8.592419,53.172083,1 8.599707,53.171519,1 8.602736,53.171264,1 8.604997,53.171097,1 8.610041,53.170569,1 8.612126,53.170306,1 8.61409,53.169917,1 8.616423,53.169147,1 8.619288,53.167892,1 8.620122,53.167398,1 8.623391,53.165066,1 8.624465,53.164101,1 8.625252,53.162733,1 8.625599,53.161858,1 8.626361,53.157775,1 8.626803,53.153465,1 8.627896,53.147791,1 8.629308,53.14379,1 8.631581,53.139862,1.75 8.634406,53.136444,2 8.636835,53.134333,2 8.639968,53.132093,2 8.643789,53.130068,1.75 8.647128,53.128526,2 8.648829,53.127918,2 8.652285,53.127034,2 8.661382,53.126093,2 8.667887,53.125483,2 8.670179,53.12503,2 8.675139,53.124051,2 8.679384,53.122924,2 8.68967,53.120474,2 8.698899,53.119243,2 8.70677,53.118589,1.75 8.71265,53.118486,2 8.717362,53.11829,2 8.720335,53.117993,2 8.722624,53.117736,2 8.727695,53.116606,2 8.731301,53.115628,2 8.735148,53.114184,2 8.737105,53.113149,2 8.738915,53.112191,2 8.741558,53.110239,2 8.743678,53.108369,2 8.744927,53.106971,2 8.747653,53.104401,2 8.749902,53.102612,2 8.753425,53.100453,2 8.7588,53.096914,2 8.763176,53.092385,2 8.764754,53.090567,2 8.767456,53.087753,2 8.770337,53.085974,2 8.773632,53.084214,1.75 8.777242,53.082958,2.25 8.780658,53.082027,2 8.783301,53.081645,2.75 8.787441,53.081067,1.5 8.789097,53.080664,2 8.790649,53.08031,1.75 8.792995,53.079705,2.25 8.796804,53.078478,2.5 8.798676,53.077459,2.75 8.801201,53.076023,3.25 8.802333,53.075046,2 8.803898,53.0736,2";
//        for (String str2 : str.split(" ")) {
//            String[] str3 = str2.split(",");
//            routeList.add(new SimulationAttribute<Position>(true, true, NoUnit.get(), new Position(Double.parseDouble(str3[0]), Double.parseDouble(str3[1]), 0), "waypoint"));
//        }
//        trafficParticipant.setRoute(routeList);
//
//
//        FollowGivenRouteBehaviour behaviour = new FollowGivenRouteBehaviour();
//        behaviour.setSimulationObject(trafficParticipant);
//
//        ArrayList<Infrastructure> infra = (new GeoPackageReader()).read(GeoPackageReader.class.getResource("/library/services/geodata/EPSG4326_WGS84.gpkg").getPath());
//        Scenario scenario = new Scenario();
//        infra.forEach(t -> scenario.addSimulationObject(t));
//        MapDataProvider mdp = new MapDataProvider(scenario);
//
//        MapDataProvider.addToMap(trafficParticipant, mdp);
//
//        while (!behaviour.getGoalReached()) {
//            behaviour.nextStep(new SimulationAttribute<>(true, true, TimeUnit.MINUTE, 1.0, "~"));
//        }
//    }
//}