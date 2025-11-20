package library.model.maritime.behaviours;

import library.model.maritime.Vessel;
import library.model.simulation.Behaviour;
import library.model.simulation.IBehaviour;
import library.model.simulation.Position;
import library.model.simulation.objects.SimulationObject;
import library.model.simulation.SimulationProperty;
import library.model.simulation.units.NoUnit;
import library.model.traffic.Obstacle;
import library.model.traffic.TrafficParticipant;
import library.services.geodata.MapDataProvider;
import org.geotools.data.DataUtilities;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.Transaction;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FollowRouteBehaviour extends Behaviour implements IBehaviour {

    // TODO this definitely has to be configured somewhere globally!
    // Configures how as how many seconds one simulation step is interpreted
    protected final double SECONDS_PER_STEP = 5.0;

    protected final static Logger LOGGER = Logger.getLogger(FollowRouteBehaviour.class.getName());

    protected List<Position> route;
    protected Integer nextWaypointIndex;
    protected Boolean goalReached;
    protected TrafficParticipant trafficParticipant;
    protected List<Position> visitedPositions;
    private MapDataProvider mapDataProvider;

    public FollowRouteBehaviour() {
        this.nextWaypointIndex = 0;
        this.goalReached = false;
        this.visitedPositions = new ArrayList<>();

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

    @Override
    public void setSimulationObject(SimulationObject simulationObject) {
        if (simulationObject instanceof TrafficParticipant) {
            this.trafficParticipant = (TrafficParticipant) simulationObject;
        }
    }

    @Override
    public Map<String, Object> nextStep(double timePassed) {
        // QUESTION: Why was this value overwritten each time instead of using the passed value?
        // ASSUMES USAGE OF TIME STEP SIZE = 1
        // timePassed = new SimulationProperty<>(true, true, TimeUnit.SECOND, SECONDS_PER_STEP, "timePassed");

        Map<String, Object> attributeUpdates = new HashMap<>();

        calculatePositions();
        ArrayList<Obstacle> detectedObstacles = new ArrayList<>();
        for (PositionFormPair positionFormPair : positionDimensionMap.values()) {
            detectedObstacles.add(positionFormPair.obstacle);
        }

        this.mapDataProvider = MapDataProvider.getMap(this.trafficParticipant);
        double distance = (this.trafficParticipant.getSpeed().getValue() * this.trafficParticipant.getSpeed().getUnit().getUnitToBase()) * timePassed; // TODO probably wrong calculation without the conversion, only needed to get it compiling (timePassed.getValue() * timePassed.getUnit().getUnitToBase());
        Position currentPosition = this.trafficParticipant.getPosition().getValue();

        boolean generatedObstacleRoute = false;

        // QUESTION why does this only gets executed if the speed of the ship is >0?(distance = speed * time)
        while (distance > 0 && !goalReached) { // OPTIMIZE this blocks for a long time

            if (this.nextWaypointIndex == this.route.size()) {

                goalReached = true;

                // TODO use proper logger
                System.out.println(this.getClass().getName() + " " + ((Vessel) this.trafficParticipant).getVesselName().getValue() + " reached Goal");
                try {
                    if (!visitedPositions.get(0).equals(((Vessel) this.trafficParticipant).getOrigin().getValue())) {
                        visitedPositions.remove(0);
                    }
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
                    System.out.println("Request route to next point for " + (trafficParticipant instanceof Vessel ? ((Vessel) trafficParticipant).getVesselName() : "NOT-A-VESSEL"));
                    ArrayList<Position> newRouteToNextPoint = RouteUtils.generateRoute(mapDataProvider,
                            currentPosition,
                            this.route.get(this.nextWaypointIndex + counter),
                            this.route.get(this.nextWaypointIndex + counter),
                            detectedObstacles, false);
                    for (int i = 0; i < counter; i++) {
                        this.route.remove(this.nextWaypointIndex + 1);
                    }
                    this.route.addAll(this.nextWaypointIndex, newRouteToNextPoint);
                    System.out.println("Inserted new route to prevent collision");
                }
            } catch (ParseException | SchemaException | IOException e) {
                e.printStackTrace();
            }

            double distanceToNextWaypoint = this.coordinateDiffInMeters(currentPosition.getLatitude().getValue(), currentPosition.getLongitude().getValue(), this.route.get(this.nextWaypointIndex).getLatitude().getValue(), this.route.get(this.nextWaypointIndex).getLongitude().getValue());
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

        attributeUpdates.put(trafficParticipant.getRotation().getId(), RouteUtils.angleFromCoordinate(trafficParticipant.getRotation().getValue(),
                trafficParticipant.getPosition().getValue().getLatitude().getValue(),
                trafficParticipant.getPosition().getValue().getLongitude().getValue(),
                currentPosition.getLatitude().getValue(),
                currentPosition.getLongitude().getValue()));
        attributeUpdates.put(trafficParticipant.getPosition().getValue().getLongitude().getId(), currentPosition.getLongitude().getValue());
        attributeUpdates.put(trafficParticipant.getPosition().getValue().getLatitude().getId(), currentPosition.getLatitude().getValue());
        attributeUpdates.put(trafficParticipant.getPosition().getValue().getLatitude().getId(), currentPosition.getLatitude().getValue());

        LOGGER.log(Level.INFO, this.trafficParticipant.getId() + "    task pushed:    x:" + currentPosition.getLongitude().getValue() + "    y: " + currentPosition.getLatitude().getValue());

        return attributeUpdates;
    }

    public List<Position> getRoute() {
        return route;
    }

    public void setRoute(List<Position> route) {
        this.route = route;
    }

    public Boolean getGoalReached() {
        return goalReached;
    }

    /**
     * TODO: add javadoc
     *
     * @param positions
     * @param fileName
     * @throws SchemaException
     * @throws IOException
     */
    public static void route2Shapefiles(List<Position> positions, String fileName) throws SchemaException, IOException {
        SimpleFeatureType TYPE =
                DataUtilities.createType(
                        "Location",
                        "the_geom:LineString:srid=4326," +
                                "name:String," +
                                "number:Integer"
                );
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(TYPE);
        List<SimpleFeature> features = new ArrayList<>();
        for (int i = 1; i < positions.size(); i++) {

            String name = Integer.toString(i);
            int number = i;

            LineString point = geometryFactory.createLineString(new Coordinate[]{new Coordinate(positions.get(i - 1).getLongitude().getValue(), positions.get(i - 1).getLatitude().getValue()), new Coordinate(positions.get(i).getLongitude().getValue(), positions.get(i).getLatitude().getValue())});

            featureBuilder.add(point);
            featureBuilder.add(name);
            featureBuilder.add(number);
            SimpleFeature feature = featureBuilder.buildFeature(null);
            features.add(feature);

        }

        File newFile = createNewShapeFile(fileName);

        ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();

        Map<String, Serializable> params = new HashMap<>();
        params.put("url", newFile.toURI().toURL());
        params.put("create spatial index", Boolean.TRUE);

        ShapefileDataStore newDataStore = (ShapefileDataStore) dataStoreFactory.createNewDataStore(params);

        newDataStore.createSchema(TYPE);
        Transaction transaction = new DefaultTransaction("create");

        String typeName = newDataStore.getTypeNames()[0];
        SimpleFeatureSource featureSource = newDataStore.getFeatureSource(typeName);
        SimpleFeatureType SHAPE_TYPE = featureSource.getSchema();

        System.out.println("SHAPE:" + SHAPE_TYPE);

        if (featureSource instanceof SimpleFeatureStore) {
            SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;
            SimpleFeatureCollection collection = new ListFeatureCollection(TYPE, features);
            featureStore.setTransaction(transaction);
            try {
                featureStore.addFeatures(collection);
                transaction.commit();
            } catch (Exception problem) {
                problem.printStackTrace();
                transaction.rollback();
            } finally {
                transaction.close();
            }
        } else {
            System.out.println(typeName + " does not support read/write access");
            System.exit(1);
        }
    }

    /**
     * Prompt the user for the name and path to used for the output shapefile
     * TODO: update javadoc, because the user wont get prompted anything here
     *
     * @return name and path for the shapefile as a new File object
     */
    private static int SHAPE_FILE_NAMING_COUNTER = 0; // TODO: give 'k' a more suitable and self explanatory name

    private static File createNewShapeFile(String fileName) {
        File direct = new File("shapes");
        direct.mkdirs();
        return new File(
                "shapes\\"
                        + fileName
                        + SHAPE_FILE_NAMING_COUNTER++
                        + ".shp"
        );
    }

    protected Double coordinateDiffInMeters(Position pos1, Position pos2) {
        return coordinateDiffInMeters(pos1.getLatitude().getValue(),
                pos1.getLongitude().getValue(),
                pos2.getLatitude().getValue(),
                pos2.getLongitude().getValue());
    }

    /**
     * calculates the difference between two geo coordinates in meters and returns the result
     *
     * @param lat1
     * @param lon1
     * @param lat2
     * @param lon2
     * @return
     */
    public static Double coordinateDiffInMeters(Double lat1, Double lon1, Double lat2, Double lon2) {  // generally used geo measurement function
        var R = 6378.137; // Radius of earth in KM
        var dLat = lat2 * Math.PI / 180 - lat1 * Math.PI / 180;
        var dLon = lon2 * Math.PI / 180 - lon1 * Math.PI / 180;
        var a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        var d = R * c;
        return d * 1000.0; // meters
    }

    /**
     * TODO: add javadoc
     */
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

    /**
     * TODO add javadoc
     */
    class PositionFormPair {

        public PositionFormPair(String id, Position position, Double length, Double width) {
            this.id = id;
            this.position = position;
            this.length = length;
            this.width = width;

            WKTReader wktReader = new WKTReader();
            Position position2 = new Position(position.getLongitude().getValue(), position.getLatitude().getValue(), position.getLatitude().getValue());

            // TODO i think this is _very_ inefficient
            while (FollowRouteBehaviour.coordinateDiffInMeters(position2.getLatitude().getValue(), position2.getLongitude().getValue(), position.getLatitude().getValue(), position.getLongitude().getValue()) < width) {
                position2.setLongitude(position2.getLongitude().getValue() + 0.000005);
            }

            Position position3 = new Position(position.getLongitude().getValue(), position.getLatitude().getValue(), position.getLatitude().getValue());

            // TODO i think this is _very_ inefficient
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
