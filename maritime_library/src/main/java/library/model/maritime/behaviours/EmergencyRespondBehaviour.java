package library.model.maritime.behaviours;

import library.model.maritime.Vessel;
import library.model.simulation.Behaviour;
import library.model.simulation.IBehaviour;
import library.model.simulation.Position;
import library.model.simulation.SimulationProperty;
import library.model.simulation.objects.SimulationObject;
import library.model.simulation.units.NoUnit;
import library.model.traffic.Obstacle;
import library.model.traffic.TrafficParticipant;
import library.services.geodata.MapDataProvider;
import org.geotools.feature.SchemaException;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static library.model.maritime.behaviours.FollowRouteBehaviour.coordinateDiffInMeters;
import static library.model.maritime.behaviours.FollowRouteBehaviour.route2Shapefiles;

/**
 * A Behaviour that moves a SimulationObject towards another SimulationObject that triggered an emergencycall
 */
public class EmergencyRespondBehaviour extends Behaviour implements IBehaviour {

    // TODO this definitely has to be configured somewhere globally!
    //Configures how as how many seconds one simulation step is interpreted
    protected final double SECONDS_PER_STEP = 5.0;

    private boolean emergencyCallReceived;
    private ArrayList<Position> route;
    private TrafficParticipant trafficParticipant;
    private MapDataProvider mapDataProvider;
    private boolean goalReached;
    private ArrayList<Position> visitedPositions;
    private int nextWaypointIndex;

    private SimulationProperty<HashMap<String, Double>> csLongitudes = new SimulationProperty<>(NoUnit.get(), new HashMap<>(), "longitudes");
    private SimulationProperty<HashMap<String, Double>> csLatitudes = new SimulationProperty<>(NoUnit.get(), new HashMap<>(), "latitudes");
    private SimulationProperty<HashMap<String, Double>> csAltitudes = new SimulationProperty<>(NoUnit.get(), new HashMap<>(), "altitudes");
    private SimulationProperty<HashMap<String, Double>> csLength = new SimulationProperty<>(NoUnit.get(), new HashMap<>(), "forms");
    private SimulationProperty<HashMap<String, Double>> csWidth = new SimulationProperty<>(NoUnit.get(), new HashMap<>(), "forms");
    private SimulationProperty<HashMap<String, Boolean>> csBroken = new SimulationProperty<>(NoUnit.get(), new HashMap<>(), "broken");

    private SimulationProperty<HashMap<String, Double>> psLongitudes = new SimulationProperty<>(NoUnit.get(), new HashMap<>(), "longitudes");
    private SimulationProperty<HashMap<String, Double>> psLatitudes = new SimulationProperty<>(NoUnit.get(), new HashMap<>(), "latitudes");
    private SimulationProperty<HashMap<String, Double>> psAltitudes = new SimulationProperty<>(NoUnit.get(), new HashMap<>(), "altitudes");
    private SimulationProperty<HashMap<String, Double>> psLength = new SimulationProperty<>(NoUnit.get(), new HashMap<>(), "forms");
    private SimulationProperty<HashMap<String, Double>> psWidth = new SimulationProperty<>(NoUnit.get(), new HashMap<>(), "forms");

    private SimulationProperty<HashMap<String, Double>> vLongitudes = new SimulationProperty<>(NoUnit.get(), new HashMap<>(), "longitudes");
    private SimulationProperty<HashMap<String, Double>> vLatitudes = new SimulationProperty<>(NoUnit.get(), new HashMap<>(), "latitudes");
    private SimulationProperty<HashMap<String, Double>> vAltitudes = new SimulationProperty<>(NoUnit.get(), new HashMap<>(), "altitudes");
    private SimulationProperty<HashMap<String, Double>> vLength = new SimulationProperty<>(NoUnit.get(), new HashMap<>(), "forms");
    private SimulationProperty<HashMap<String, Double>> vWidth = new SimulationProperty<>(NoUnit.get(), new HashMap<>(), "forms");

    private HashMap<String, Position> positionMap = new HashMap<>();
    private HashMap<String, PositionFormPair> positionDimensionMap = new HashMap<>();

    public SimulationProperty<HashMap<String, Double>> getCsLength() {
        return csLength;
    }

    public SimulationProperty<HashMap<String, Double>> getCsWidth() {
        return csWidth;
    }

    public SimulationProperty<HashMap<String, Boolean>> getCsBroken() {
        return csBroken;
    }

    public SimulationProperty<HashMap<String, Double>> getPsLength() {
        return psLength;
    }

    public SimulationProperty<HashMap<String, Double>> getPsWidth() {
        return psWidth;
    }

    public SimulationProperty<HashMap<String, Double>> getvLongitudes() {
        return vLongitudes;
    }

    public SimulationProperty<HashMap<String, Double>> getvLatitudes() {
        return vLatitudes;
    }

    public SimulationProperty<HashMap<String, Double>> getvAltitudes() {
        return vAltitudes;
    }

    public SimulationProperty<HashMap<String, Double>> getvLength() {
        return vLength;
    }

    public SimulationProperty<HashMap<String, Double>> getvWidth() {
        return vWidth;
    }

    public SimulationProperty<HashMap<String, Double>> getVLength() {
        return vLength;
    }

    public SimulationProperty<HashMap<String, Double>> getVWidth() {
        return vWidth;
    }

    public SimulationProperty<HashMap<String, Double>> getCsLongitudes() {
        return csLongitudes;
    }

    public SimulationProperty<HashMap<String, Double>> getCsLatitudes() {
        return csLatitudes;
    }

    public SimulationProperty<HashMap<String, Double>> getCsAltitudes() {
        return csAltitudes;
    }

    public SimulationProperty<HashMap<String, Double>> getPsLongitudes() {
        return psLongitudes;
    }

    public SimulationProperty<HashMap<String, Double>> getPsLatitudes() {
        return psLatitudes;
    }

    public SimulationProperty<HashMap<String, Double>> getPsAltitudes() {
        return psAltitudes;
    }

    public SimulationProperty<HashMap<String, Double>> getVLongitudes() {
        return vLongitudes;
    }

    public SimulationProperty<HashMap<String, Double>> getVLatitudes() {
        return vLatitudes;
    }

    public SimulationProperty<HashMap<String, Double>> getVAltitudes() {
        return vAltitudes;
    }

    public EmergencyRespondBehaviour() {
        emergencyCallReceived = false;
        goalReached = false;
        this.route = null;
        visitedPositions = new ArrayList<>();
        nextWaypointIndex = 0;

        /** configure the SimulationsAttributes to receive the correct Data */
//        csLongitudes.setFomClassString("HLAobjectRoot.ContainerShip.Position.longitude");
        csLongitudes.setListType(Double.class);
        csLongitudes.setSubscribe(true);
//        csLatitudes.setFomClassString("HLAobjectRoot.ContainerShip.Position.latitude");
        csLatitudes.setListType(Double.class);
        csLatitudes.setSubscribe(true);
//        csAltitudes.setFomClassString("HLAobjectRoot.ContainerShip.Position.altitude");
        csAltitudes.setListType(Double.class);
        csAltitudes.setSubscribe(true);
//        csLength.setFomClassString("HLAobjectRoot.ContainerShip.length");
        csLength.setListType(Double.class);
        csLength.setSubscribe(true);
//        csWidth.setFomClassString("HLAobjectRoot.ContainerShip.width");
        csWidth.setListType(Double.class);
        csWidth.setSubscribe(true);
//        csBroken.setFomClassString("HLAobjectRoot.ContainerShip.emergencyDeclared");
        csBroken.setListType(Boolean.class);
        csBroken.setSubscribe(true);

        /** configure the SimulationsAttributes to receive the correct Data */
//        psLongitudes.setFomClassString("HLAobjectRoot.PassengerShip.Position.longitude");
        psLongitudes.setListType(Double.class);
        psLongitudes.setSubscribe(true);
//        psLatitudes.setFomClassString("HLAobjectRoot.PassengerShip.Position.latitude");
        psLatitudes.setListType(Double.class);
        psLatitudes.setSubscribe(true);
//        psAltitudes.setFomClassString("HLAobjectRoot.PassengerShip.Position.altitude");
        psAltitudes.setListType(Double.class);
        psAltitudes.setSubscribe(true);
//        psLength.setFomClassString("HLAobjectRoot.PassengerShip.length");
        psLength.setListType(Double.class);
        psLength.setSubscribe(true);
//        psWidth.setFomClassString("HLAobjectRoot.PassengerShip.width");
        psWidth.setListType(Double.class);
        psWidth.setSubscribe(true);

        /** configure the SimulationsAttributes to receive the correct Data */
//        vLongitudes.setFomClassString("HLAobjectRoot.Vessel.Position.longitude");
        vLongitudes.setListType(Double.class);
        vLongitudes.setSubscribe(true);
//        vLatitudes.setFomClassString("HLAobjectRoot.Vessel.Position.latitude");
        vLatitudes.setListType(Double.class);
        vLatitudes.setSubscribe(true);
//        vAltitudes.setFomClassString("HLAobjectRoot.Vessel.Position.altitude");
        vAltitudes.setListType(Double.class);
        vAltitudes.setSubscribe(true);
//        vLength.setFomClassString("HLAobjectRoot.Vessel.length");
        vLength.setListType(Double.class);
        vLength.setSubscribe(true);
//        vWidth.setFomClassString("HLAobjectRoot.Vessel.width");
        vWidth.setListType(Double.class);
        vWidth.setSubscribe(true);
    }

    @Override
    public void setSimulationObject(SimulationObject simulationObject) {
        if (simulationObject instanceof TrafficParticipant) {
            this.trafficParticipant = (TrafficParticipant) simulationObject;
        }
    }

    private Position emergencyPosition = null;

    @Override
    public Map<String, Object> nextStep(double timePassed) {
        //ASSUMES USAGE OF TIME STEP SIZE = 1
        //timePassed = new SimulationProperty<>(true, true, TimeUnit.SECOND, timePassed, "timePassed");

        calculatePositions();
        ArrayList<Obstacle> detectedObstacles = new ArrayList<>();
        for (PositionFormPair positionFormPair : positionDimensionMap.values()) {
            if (!positionFormPair.broken) {
                detectedObstacles.add(positionFormPair.obstacle);
            } else if (emergencyPosition == null) {
                emergencyPosition = positionFormPair.position;
                emergencyCallReceived = true;
            }
        }

        this.mapDataProvider = MapDataProvider.getMap(this.trafficParticipant);
        if (emergencyCallReceived && route == null) {
            System.out.println("GENERATING EMERGENCY ROUTE");
            try {
                this.route = RouteUtils.generateRoute(mapDataProvider, trafficParticipant.getPosition().getValue(),
                        emergencyPosition, emergencyPosition, detectedObstacles, true);
            } catch (ParseException | SchemaException | IOException e) {
                e.printStackTrace();
            }
        }

        if (emergencyCallReceived) {
            double distance = (this.trafficParticipant.getSpeed().getValue() * this.trafficParticipant.getSpeed().getUnit().getUnitToBase()) * timePassed; // TODO probably wrong calculation without the conversion, only needed to get it compiling (timePassed.getValue() * timePassed.getUnit().getUnitToBase());
            Position currentPosition = this.trafficParticipant.getPosition().getValue();

            boolean generatedObstacleRoute = false;

            while (distance > 0 && !goalReached) {
                if (this.nextWaypointIndex == this.route.size()) {
                    goalReached = true;
                    System.out.println(this.getClass().getName() + " " + ((Vessel) this.trafficParticipant).getVesselName().getValue() + " reached Goal");
                    try {
                        route2Shapefiles(visitedPositions, "visited");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }

                try {
                    if (RouteUtils.hasObstacles(currentPosition, this.route.get(this.nextWaypointIndex), mapDataProvider, detectedObstacles) && !generatedObstacleRoute) {
                        generatedObstacleRoute = true;
                        boolean pointOnObstacle = true;
                        int counter = 0;
                        while (pointOnObstacle) {
                            if (mapDataProvider.getObstacleAtPosition(this.route.get(this.nextWaypointIndex + counter), detectedObstacles).isEmpty()) {
                                pointOnObstacle = false;
                                System.out.println("Next possible point is next + " + counter);
                            } else {
                                counter++;
                            }
                        }
                        ArrayList<Position> newRouteToNextPoint = RouteUtils.generateRoute(mapDataProvider, currentPosition,
                                this.route.get(this.nextWaypointIndex + counter), this.route.get(this.nextWaypointIndex + counter), detectedObstacles, false);
                        for (int i = 0; i < counter; i++) {
                            this.route.remove(this.nextWaypointIndex + 1);
                        }
                        this.route.addAll(this.nextWaypointIndex, newRouteToNextPoint);
                        System.out.println("Inserted new route to prevent collision");
                    }
                } catch (ParseException | SchemaException | IOException e) {
                    e.printStackTrace();
                }

                double distanceToNextWaypoint = coordinateDiffInMeters(currentPosition.getLatitude().getValue(), currentPosition.getLongitude().getValue(), this.route.get(this.nextWaypointIndex).getLatitude().getValue(), this.route.get(this.nextWaypointIndex).getLongitude().getValue());
                if (distanceToNextWaypoint > distance) {
                    // move on line to nextWaypoint
                    double steps = distanceToNextWaypoint / distance;
                    double step_lat = (route.get(nextWaypointIndex).getLatitude().getValue() - currentPosition.getLatitude().getValue()) / steps;
                    double step_lon = (route.get(nextWaypointIndex).getLongitude().getValue() - currentPosition.getLongitude().getValue()) / steps;

                    Position newPosition = new Position();
                    newPosition.setLatitude(currentPosition.getLatitude().getValue() + step_lat);
                    newPosition.setLongitude(currentPosition.getLongitude().getValue() + step_lon);
                    newPosition.setAltitude(currentPosition.getAltitude().getValue());
                    currentPosition = newPosition;
                    distance = 0;
                } else {
                    currentPosition = this.route.get(this.nextWaypointIndex);
                    this.nextWaypointIndex += 1;
                    distance -= distanceToNextWaypoint;
                }
                this.visitedPositions.add(currentPosition);
            }

            //TODO new positions aren't pushed to the TrafficParticipant
//            try {
//                trafficParticipant.pushTask(new Task(trafficParticipant.getRotation().getId(), RouteUtils.angleFromCoordinate(trafficParticipant.getRotation().getValue(), trafficParticipant.getPosition().getValue().getLatitude().getValue(), trafficParticipant.getPosition().getValue().getLongitude().getValue(), currentPosition.getLatitude().getValue(), currentPosition.getLongitude().getValue())));
//                trafficParticipant.pushTask(new Task(trafficParticipant.getPosition().getValue().getLongitude().getId(), currentPosition.getLongitude().getValue()));
//                trafficParticipant.pushTask(new Task(trafficParticipant.getPosition().getValue().getLatitude().getId(), currentPosition.getLatitude().getValue()));
//                trafficParticipant.pushTask(new Task(trafficParticipant.getPosition().getValue().getAltitude().getId(), currentPosition.getAltitude().getValue()));
//            } catch (AttributeNotFoundException e) {
//                e.printStackTrace();
//            }
        }
        return new HashMap<>();
    }

    private void calculatePositions() {
        for (String id : csLongitudes.getValue().keySet()) {
            double longitude = csLongitudes.getValue().get(id);
            double latitude = csLatitudes.getValue().get(id);
            double altitude = csAltitudes.getValue().get(id);
            double length = csLength.getValue().get(id);
            double width = csWidth.getValue().get(id);
            boolean broken = csBroken.getValue().get(id);
            Position positionObject = new Position(longitude, latitude, altitude);
            positionMap.put(id, positionObject);
            positionDimensionMap.put(id, new PositionFormPair(id, positionObject, length, width, broken));
        }
        for (String id : psLongitudes.getValue().keySet()) {
            double longitude = psLongitudes.getValue().get(id);
            double latitude = psLatitudes.getValue().get(id);
            double altitude = psAltitudes.getValue().get(id);
            double length = psLength.getValue().get(id);
            double width = psWidth.getValue().get(id);
            Position positionObject = new Position(longitude, latitude, altitude);
            positionMap.put(id, positionObject);
            positionDimensionMap.put(id, new PositionFormPair(id, positionObject, length, width, false));
        }
        for (String id : vLongitudes.getValue().keySet()) {
            double longitude = vLongitudes.getValue().get(id);
            double latitude = vLatitudes.getValue().get(id);
            double altitude = vAltitudes.getValue().get(id);
            double length = vLength.getValue().get(id);
            double width = vWidth.getValue().get(id);
            Position positionObject = new Position(longitude, latitude, altitude);
            positionMap.put(id, positionObject);
            positionDimensionMap.put(id, new PositionFormPair(id, positionObject, length, width, false));
        }
    }

    class PositionFormPair {

        public PositionFormPair(String id, Position position, Double length, Double width, Boolean broken) {
            this.id = id;
            this.position = position;
            this.length = length;
            this.width = width;

            WKTReader wktReader = new WKTReader();

            Position position2 = new Position(position.getLongitude().getValue(),
                    position.getLatitude().getValue(),
                    position.getAltitude().getValue());

            while (FollowRouteBehaviour.coordinateDiffInMeters(position2.getLatitude().getValue(),
                    position2.getLongitude().getValue(),
                    position.getLatitude().getValue(),
                    position.getLongitude().getValue()) < width) {
                position2.setLongitude(position2.getLongitude().getValue() + 0.000005);
            }

            Position position3 = new Position(position.getLongitude().getValue(),
                    position.getLatitude().getValue(),
                    position.getAltitude().getValue());

            while (FollowRouteBehaviour.coordinateDiffInMeters(position3.getLatitude().getValue(),
                    position3.getLongitude().getValue(),
                    position.getLatitude().getValue(),
                    position.getLongitude().getValue()) < length) {
                position3.setLatitude(position3.getLatitude().getValue() - 0.00001);
            }

            Position position4 = new Position(position2.getLongitude().getValue(),
                    position3.getLatitude().getValue(),
                    0);

            String wkt = "POLYGON (("
                    + position.getLongitude().getValue() + " " + position.getLatitude().getValue() + ", "
                    + position2.getLongitude().getValue() + " " + position2.getLatitude().getValue() + ", "
                    + position4.getLongitude().getValue() + " " + position4.getLatitude().getValue() + ", "
                    + position3.getLongitude().getValue() + " " + position3.getLatitude().getValue() + ", "
                    + position.getLongitude().getValue() + " " + position.getLatitude().getValue()
                    + "))";

            try {
                form = wktReader.read(wkt);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            this.broken = broken;
            obstacle = new Obstacle(true, position, form, 0);
        }

        String id;
        Position position;
        Double length;
        Double width;
        Geometry form;
        Obstacle obstacle;
        Boolean broken;
    }
}
