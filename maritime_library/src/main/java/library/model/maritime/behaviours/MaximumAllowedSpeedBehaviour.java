package library.model.maritime.behaviours;

import library.model.limitations.Restriction;
import library.model.maritime.Vessel;
import library.model.simulation.Position;
import library.model.simulation.SimulationProperty;
import library.model.simulation.units.NoUnit;
import library.model.simulation.units.SpeedUnit;
import library.model.traffic.Infrastructure;
import library.model.traffic.Obstacle;
import library.model.traffic.PossibleDomains;
import library.services.geodata.MapDataProvider;
import org.geotools.feature.SchemaException;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class MaximumAllowedSpeedBehaviour extends FollowGivenRouteBehaviour {

    private MapDataProvider mapDataProvider;

    private SimulationProperty<HashMap<String, Double>> csLongitudes = new SimulationProperty<>(NoUnit.get(), new HashMap<>(), "longitudes");
    private SimulationProperty<HashMap<String, Double>> csLatitudes = new SimulationProperty<>(NoUnit.get(), new HashMap<>(), "latitudes");
    private SimulationProperty<HashMap<String, Double>> csAltitudes = new SimulationProperty<>(NoUnit.get(), new HashMap<>(), "altitudes");
    private SimulationProperty<HashMap<String, Double>> csLength = new SimulationProperty<>(NoUnit.get(), new HashMap<>(), "forms");
    private SimulationProperty<HashMap<String, Double>> csWidth = new SimulationProperty<>(NoUnit.get(), new HashMap<>(), "forms");

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

    public MaximumAllowedSpeedBehaviour() {
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
    }

    @Override
    public Map<String, Object> nextStep(double timePassed) {
        // QUESTION: Why was this value overwritten each time instead of using the passed value?
        // ASSUMES USAGE OF TIME STEP SIZE = 1
        // timePassed = new SimulationProperty<Double>(true, true, TimeUnit.SECOND, SECONDS_PER_STEP, "timePassed");

        Map<String, Object> attributesToUpdate = new HashMap<>();

        calculatePositions();
        ArrayList<Obstacle> detectedObstacles = new ArrayList<>();
        for (PositionFormPair positionFormPair : positionDimensionMap.values()) {
            detectedObstacles.add(positionFormPair.obstacle);
        }

        if (this.mapDataProvider == null) {
            this.mapDataProvider = MapDataProvider.getMap(this.trafficParticipant);
        }
        SimulationProperty<Double> allowedSpeed = new SimulationProperty<Double>(true, true, SpeedUnit.KILOMETERSPERHOUR, Double.MAX_VALUE, "speed");
        try {
            List<Infrastructure> infrastructures = this.mapDataProvider.getInfrastructureAtPosition(this.trafficParticipant.getPosition().getValue());
            for (Infrastructure anInfrastructure : infrastructures) {
                if (anInfrastructure.isUsableBy(PossibleDomains.MARITIME)) {
                    for (Restriction<?> restriction : anInfrastructure.getImposedRestrictions()) {
                        for (SimulationProperty<?> limitProperty : restriction.getLimitedProperties()) {
                            if (limitProperty.getName().equals("speedInKmH") && (double) limitProperty.getValue() < allowedSpeed.getValue()) {
                                if (anInfrastructure.getForm() == null) {
                                    allowedSpeed = (SimulationProperty<Double>) limitProperty;
                                } else {
                                    WKTReader reader = new WKTReader();
                                    Geometry point = reader.read("POINT ("
                                                                 + this.trafficParticipant.getPosition().getValue().getLongitude().getValue() + " "
                                                                 + this.trafficParticipant.getPosition().getValue().getLatitude().getValue() + ")");
                                    if ((anInfrastructure.getForm().getValue()).contains(point)) {
                                        allowedSpeed = (SimulationProperty<Double>) limitProperty.getValue();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        double maxSpeedInMpS = trafficParticipant.getMaxSpeed().getUnit().getUnitToBase() * trafficParticipant.getMaxSpeed().getValue();
        double timePassedInS = timePassed;
        double allowedSpeedInMpS = allowedSpeed.getUnit().getUnitToBase() * allowedSpeed.getValue();

        double speedOfVessel = Math.min(maxSpeedInMpS, allowedSpeedInMpS);
        double distanceInM = speedOfVessel * timePassedInS;
        Position currentPosition = this.trafficParticipant.getPosition().getValue();
        double newLat = currentPosition.getLatitude().getValue();
        double newLon = currentPosition.getLongitude().getValue();
        double newAlt = currentPosition.getAltitude().getValue();

        //System.out.println("using speed: " + Math.min(maxSpeedInMpS, allowedSpeedInMpS));

        boolean generatedObstacleRoute = false;

        while (distanceInM > 0 && !goalReached) {
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

            double distanceToNextWaypoint = this.coordinateDiffInMeters(
                    currentPosition.getLatitude().getValue(),
                    currentPosition.getLongitude().getValue(),
                    this.route.get(this.nextWaypointIndex).getLatitude().getValue(),
                    this.route.get(this.nextWaypointIndex).getLongitude().getValue()
            );
            if (distanceToNextWaypoint > distanceInM) {
                // move on line to nextWaypoint
                double steps = distanceToNextWaypoint / distanceInM;
                double step_lat = (route.get(nextWaypointIndex).getLatitude().getValue() - currentPosition.getLatitude().getValue()) / steps;
                double step_lon = (route.get(nextWaypointIndex).getLongitude().getValue() - currentPosition.getLongitude().getValue()) / steps;

                Position newPosition = new Position();
                newPosition.setLatitude(currentPosition.getLatitude().getValue() + step_lat);
                newPosition.setLongitude(currentPosition.getLongitude().getValue() + step_lon);
                newPosition.setAltitude(currentPosition.getAltitude().getValue());
                currentPosition = newPosition;

                distanceInM = 0;
            } else {
                currentPosition = this.route.get(this.nextWaypointIndex);
                this.nextWaypointIndex += 1;
                distanceInM -= distanceToNextWaypoint;
            }
            this.visitedPositions.add(currentPosition);
        }

        attributesToUpdate.put(trafficParticipant.getRotation().getId(),
                RouteUtils.angleFromCoordinate(trafficParticipant.getRotation().getValue(),
                        trafficParticipant.getPosition().getValue().getLatitude().getValue(),
                        trafficParticipant.getPosition().getValue().getLongitude().getValue(),
                        currentPosition.getLatitude().getValue(),
                        currentPosition.getLongitude().getValue()));
        attributesToUpdate.put(trafficParticipant.getPosition().getValue().getLongitude().getId(), currentPosition.getLongitude().getValue());
        attributesToUpdate.put(trafficParticipant.getPosition().getValue().getLatitude().getId(), currentPosition.getLatitude().getValue());
        attributesToUpdate.put(trafficParticipant.getPosition().getValue().getAltitude().getId(), currentPosition.getAltitude().getValue());
        attributesToUpdate.put(trafficParticipant.getSpeed().getId(), speedOfVessel);
        LOGGER.log(Level.INFO, this.trafficParticipant.getId() + "    task pushed:    x:" + currentPosition.getLongitude().getValue() + "    y: " + currentPosition.getLatitude().getValue());

        return new HashMap<>();
    }

    public void setMapDataProvider(MapDataProvider mapDataProvider) {
        this.mapDataProvider = mapDataProvider;
    }

    private void calculatePositions() {
        for (String id : csLongitudes.getValue().keySet()) {
            double longitude = csLongitudes.getValue().get(id);
            double latitude = csLatitudes.getValue().get(id);
            double altitude = csAltitudes.getValue().get(id);
            double length = csLength.getValue().get(id);
            double width = csWidth.getValue().get(id);
            Position positionObject = new Position(longitude, latitude, altitude);
            positionMap.put(id, positionObject);
            positionDimensionMap.put(id, new PositionFormPair(id, positionObject, length, width));
        }
        for (String id : psLongitudes.getValue().keySet()) {
            double longitude = psLongitudes.getValue().get(id);
            double latitude = psLatitudes.getValue().get(id);
            double altitude = psAltitudes.getValue().get(id);
            double length = psLength.getValue().get(id);
            double width = psWidth.getValue().get(id);
            Position positionObject = new Position(longitude, latitude, altitude);
            positionMap.put(id, positionObject);
            positionDimensionMap.put(id, new PositionFormPair(id, positionObject, length, width));
        }
        for (String id : vLongitudes.getValue().keySet()) {
            double longitude = vLongitudes.getValue().get(id);
            double latitude = vLatitudes.getValue().get(id);
            double altitude = vAltitudes.getValue().get(id);
            double length = vLength.getValue().get(id);
            double width = vWidth.getValue().get(id);
            Position positionObject = new Position(longitude, latitude, altitude);
            positionMap.put(id, positionObject);
            positionDimensionMap.put(id, new PositionFormPair(id, positionObject, length, width));
        }
    }

    class PositionFormPair {

        public PositionFormPair(String id, Position position, Double length, Double width) {
            this.id = id;
            this.position = position;
            this.length = length;
            this.width = width;

            WKTReader wktReader = new WKTReader();
            Position position2 = new Position(position.getLongitude().getValue(), position.getLatitude().getValue(), position.getAltitude().getValue());
            while (FollowRouteBehaviour.coordinateDiffInMeters(position2.getLatitude().getValue(), position2.getLongitude().getValue(), position.getLatitude().getValue(), position.getLongitude().getValue()) < width) {
                position2.setLongitude(position2.getLongitude().getValue() + 0.000005);
            }
            Position position3 = new Position(position.getLongitude().getValue(), position.getLatitude().getValue(), position.getAltitude().getValue());
            while (FollowRouteBehaviour.coordinateDiffInMeters(position3.getLatitude().getValue(), position3.getLongitude().getValue(), position.getLatitude().getValue(), position.getLongitude().getValue()) < length) {
                position3.setLatitude(position3.getLatitude().getValue() - 0.00001);
            }
            Position position4 = new Position(position2.getLongitude().getValue(), position3.getLatitude().getValue(), 0);

            String wkt = "POLYGON ((" + position.getLongitude().getValue() + " " + position.getLatitude().getValue() + ", "
                    + position2.getLongitude().getValue() + " " + position2.getLatitude().getValue() + ", "
                    + position4.getLongitude().getValue() + " " + position4.getLatitude().getValue() + ", "
                    + position3.getLongitude().getValue() + " " + position3.getLatitude().getValue() + ", "
                    + position.getLongitude().getValue() + " " + position.getLatitude().getValue() + "))";

            try {
                form = wktReader.read(wkt);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            obstacle = new Obstacle(true, position, form, 0);
        }

        String id;
        Position position;
        Double length;
        Double width;
        Geometry form;
        Obstacle obstacle;
    }
}
