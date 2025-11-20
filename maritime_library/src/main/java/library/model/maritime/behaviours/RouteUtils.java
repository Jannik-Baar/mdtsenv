package library.model.maritime.behaviours;

import library.model.simulation.Position;
import library.model.traffic.Infrastructure;
import library.model.traffic.Obstacle;
import library.model.traffic.PossibleDomains;
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
import java.util.Objects;
import java.util.TreeSet;

/**
 * TODO add more comments!!!
 */
public class RouteUtils {

    private static final Double stepSize = 0.0004; // TODO unit? why 0.0004? and why is this fixed?
    private static Position currentGoalPosition;
    private static Integer nextStep = 0;

    public static boolean hasObstacles(Position currentPosition, Position nextWaypoint,
                                       MapDataProvider mapDataProvider, ArrayList<Obstacle> detectedObstacles) throws ParseException {
        String wkt = "LINESTRING (" + currentPosition.getLongitude().getValue() + " "
                + currentPosition.getLatitude().getValue() + ", "
                + nextWaypoint.getLongitude().getValue() + " "
                + nextWaypoint.getLatitude().getValue() + ")";
        WKTReader wktReader = new WKTReader();
        try {
            Geometry geometry = wktReader.read(wkt);
            ArrayList<Obstacle> result = (ArrayList<Obstacle>) mapDataProvider.getObstacleIntersected(geometry, detectedObstacles);

            result.removeIf(obs -> coordinateDiffInMeters(obs.getPosition().getValue().getLatitude().getValue(), obs.getPosition().getValue().getLongitude().getValue(), currentPosition.getLatitude().getValue(), currentPosition.getLongitude().getValue()) > 3704);
            if (!result.isEmpty()) {
                System.out.println("Obstacle in the way!");
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            throw e;
        }
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

    public static Double coordinateDiffInMeters(Position pos1, Position pos2) {
        return coordinateDiffInMeters(pos1.getLatitude().getValue(),
                pos1.getLongitude().getValue(),
                pos2.getLatitude().getValue(),
                pos2.getLongitude().getValue());
    }


    /**
     * Implementation of an interpretation of the A* search algorithm for maritime vehicles (see https://en.wikipedia.org/wiki/A*_search_algorithm)
     *
     * @param mapDataProvider
     * @param start
     * @param end
     * @param currentGoalPosition
     * @param detectedObstacles
     * @param export
     * @return
     * @throws ParseException
     * @throws SchemaException
     * @throws IOException
     */
    public static ArrayList<Position> generateRoute(MapDataProvider mapDataProvider, Position start, Position end, Position currentGoalPosition, ArrayList<Obstacle> detectedObstacles, boolean export) throws ParseException, SchemaException, IOException {
//        if (this.mapDataProvider == null) {
//            setMapDataProvider();
//        }
        List<WayToPosition> minimumNodesToPosition = new ArrayList<>();

        // automatically sorted List
        TreeSet<IncompleteRoute> incompleteRoutes = new TreeSet<>((ir1, ir2) -> {
            Double distance1Left = ir1.getDistanceLeft();
            Double distance2Left = ir2.getDistanceLeft();
            Double totalDistance = distanceBetween(ir1.getRoute().get(0), currentGoalPosition);
            Double percentage1Left = distance1Left / totalDistance;
            Double percentage2Left = distance2Left / totalDistance;
            Double distanceTravelled1 = 10.0; // TODO why 10?
            Double distanceTravelled2 = 10.0; // TODO why 10?
            Double totalTravellableDistance = 10.0; // TODO why 10?
            if (ir1.getRoute().size() > 10 && ir2.getRoute().size() > 10) { // TODO don't exactly know why this is here
                distanceTravelled1 = distanceBetween(ir1.getRoute().get(ir1.getRoute().size() - 11), ir1.getRoute().get(ir1.getRoute().size() - 1));
                distanceTravelled2 = distanceBetween(ir2.getRoute().get(ir2.getRoute().size() - 11), ir2.getRoute().get(ir2.getRoute().size() - 1));
            }
            Double percentageTravelled1 = distanceTravelled1 / totalTravellableDistance;
            Double percentageTravelled2 = distanceTravelled2 / totalTravellableDistance;
            return Double.compare(percentage1Left + percentageTravelled1, percentage2Left + percentageTravelled2);
        });

        try {
            System.out.println("Trying to calculate route from " + start + " to " + end);

            boolean goalReached = false;

            // generate the first IncompleteRoute which only contains the start Position
            IncompleteRoute firstRoute = new IncompleteRoute();

            // start the route with the given starting point
            firstRoute.addRouteEntry(start);

            // distance between start and end
            firstRoute.setDistanceLeft(distanceBetween(firstRoute.getRoute().get(firstRoute.getRoute().size() - 1), end)); // TODO no need to do an index lookup
            incompleteRoutes.add(firstRoute);

            // start a loop until no more options are left or goal is reached
            // Probably needs another termination condition to prevent eternal calculations for extremely complex routes
            IncompleteRoute incompleteRoute = null; // TODO remove unused?
            int counter = 0;
            int debug_counter = 0;
            while (incompleteRoutes.size() > 0 && !goalReached) {
                //System.out.println("TEST " + debug_counter);
                debug_counter++;
                IncompleteRoute toExtend = incompleteRoutes.first();
                incompleteRoute = toExtend;

                // generate next steps and alter in 4 directions
                ArrayList<Position> newPositions = new ArrayList<>();
                for (int i = 0; i < 4; i++) {
                    Position new1 = new Position();
                    new1.setLatitude(toExtend.getRoute().get(toExtend.getRoute().size() - 1).getLatitude().getValue());
                    new1.setLongitude(toExtend.getRoute().get(toExtend.getRoute().size() - 1).getLongitude().getValue());
                    newPositions.add(new1);
                }
                newPositions.get(0).setLongitude(newPositions.get(0).getLongitude().getValue() - stepSize);
                newPositions.get(0).setLatitude(newPositions.get(0).getLatitude().getValue() - stepSize);
                newPositions.get(1).setLongitude(newPositions.get(1).getLongitude().getValue() + stepSize);
                newPositions.get(1).setLatitude(newPositions.get(1).getLatitude().getValue() - stepSize);
                newPositions.get(2).setLongitude(newPositions.get(2).getLongitude().getValue() - stepSize);
                newPositions.get(2).setLatitude(newPositions.get(2).getLatitude().getValue() + stepSize);
                newPositions.get(3).setLongitude(newPositions.get(3).getLongitude().getValue() + stepSize);
                newPositions.get(3).setLatitude(newPositions.get(3).getLatitude().getValue() + stepSize);

                // iterate over each of the 4 generated new positions
                for (Position aNewPosition : newPositions) {
                    // check if in range of end and if yes, set the final result and terminate the algorithm
                    if (distanceBetween(aNewPosition, end) < Math.sqrt(2 * stepSize * stepSize)) { // QUESTION why (2 * stepSize * stepSize) ?
                        ArrayList<Position> route = new ArrayList<>(toExtend.getRoute());
                        route.add(aNewPosition);
                        route.add(end);
                        nextStep = 0;
                        goalReached = true;
                        if (export) {
                            route2Shapefiles(route, "finishedRoute"); // QUESTION what are those shapefiles used for
                        }
                        route = smoothenRoute(route, mapDataProvider, detectedObstacles);
                        if (export) {
                            route2Shapefiles(route, "smoothenedRoute");
                        }
                        return route;
                    }

                    // check if there is usable infrastructure (water) at the new position
                    List<Infrastructure> infrastructures = mapDataProvider.getInfrastructureAtPosition(aNewPosition);
                    boolean foundWater = false;
                    for (Infrastructure anInfrastructure : infrastructures) {
                        if (anInfrastructure.isUsableBy(PossibleDomains.MARITIME)) {
                            foundWater = true;
                        }
                    }

                    // check if there is one or more obstacle at the new position
                    boolean foundObstacle = false;
                    List<Obstacle> obstaclesAtPosition = mapDataProvider.getObstacleAtPosition(aNewPosition, detectedObstacles);
                    if (!obstaclesAtPosition.isEmpty()) {
                        foundObstacle = true;
                    }

                    if (foundWater && !foundObstacle) {
                        // copy toExtend and append the new Position, then add this newly generated Route to the list
                        // only uses shallow copy to save performance. Possible because positions aren't updated during the algorithm
                        ArrayList<Position> copy = new ArrayList<>(toExtend.getRoute());

                        if (!copy.contains(aNewPosition)/* && (minus5ToCurrent <= minus5ToNext)*/) {

                            // check if there is already a shorter route to the reached point
                            boolean foundShorterPath = false;
                            for (WayToPosition aWay : minimumNodesToPosition) {
                                if (aWay.getLatitude().equals(aNewPosition.getLatitude().getValue()) && aWay.getLongitude().equals(aNewPosition.getLongitude().getValue())
                                        && aWay.getAmountOfNodes() <= copy.size() + 1) {
                                    foundShorterPath = true;

                                }
                            }
                            if (!foundShorterPath) {
                                minimumNodesToPosition.add(new WayToPosition(aNewPosition.getLatitude().getValue(), aNewPosition.getLongitude().getValue(), copy.size() + 1));
                                copy.add(aNewPosition);
                                IncompleteRoute newRoute = new IncompleteRoute();
                                newRoute.setRoute(copy);
                                newRoute.setDistanceLeft(distanceBetween(newRoute.getRoute().get(newRoute.getRoute().size() - 1), end));
                                incompleteRoutes.add(newRoute);
                                counter++;
                                if (counter % 500 == 0) {
                                    System.out.println("Distance of last found route after " + counter + " iterations: " + newRoute.distanceLeft);
                                }
                            }
                        }
                    }
                }

                // after adding the next step, remove the extended route
                incompleteRoutes.remove(toExtend);
            }
            return new ArrayList<>();
        } catch (Exception e) {
            throw e;
        }
    }

    private static class IncompleteRoute {

        private ArrayList<Position> route;
        private Double distanceLeft;

        public IncompleteRoute() {
            this.route = new ArrayList<>();
            this.distanceLeft = Double.MAX_VALUE;
        }

        public ArrayList<Position> getRoute() {
            return route;
        }

        public void addRouteEntry(Position newEntry) {
            this.route.add(newEntry);
        }

        public void setRoute(ArrayList<Position> route) {
            this.route = route;
        }

        public Double getDistanceLeft() {
            return distanceLeft;
        }

        public void setDistanceLeft(Double distanceLeft) {
            this.distanceLeft = distanceLeft;
        }
    }

    protected static Double distanceBetween(Position pos1, Position pos2) {
        Double latDiff = Math.abs(pos2.getLatitude().getValue() - pos1.getLatitude().getValue());
        Double lonDiff = Math.abs(pos2.getLongitude().getValue() - pos1.getLongitude().getValue());

        return Math.sqrt((latDiff * latDiff) + (lonDiff * lonDiff));
    }

    private static class WayToPosition {

        private Double latitude;
        private Double longitude;
        private Integer amountOfNodes;

        public Double getLatitude() {
            return latitude;
        }

        public void setLatitude(Double latitude) {
            this.latitude = latitude;
        }

        public Double getLongitude() {
            return longitude;
        }

        public void setLongitude(Double longitude) {
            this.longitude = longitude;
        }

        public Integer getAmountOfNodes() {
            return amountOfNodes;
        }

        public void setAmountOfNodes(Integer amountOfNodes) {
            this.amountOfNodes = amountOfNodes;
        }

        public WayToPosition(Double latitude, Double longitude, Integer amountOfNodes) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.amountOfNodes = amountOfNodes;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            WayToPosition that = (WayToPosition) o;
            return Objects.equals(latitude, that.latitude) && Objects.equals(longitude, that.longitude) && Objects.equals(amountOfNodes, that.amountOfNodes);
        }

        @Override
        public int hashCode() {
            return Objects.hash(latitude, longitude, amountOfNodes);
        }
    }

    public static double angleFromCoordinate(double angleOld, double lat1, double long1, double lat2, double long2) {
        if (lat1 == lat2 && long1 == long2) {
            return angleOld;
        }
        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon);

        double angle = Math.atan2(y, x);

        angle = angle * (180 / Math.PI);
        angle = (angle + 360) % 360;
        angle = 360 - angle; // count degrees counter-clockwise - remove to make clockwise
        return angle;
    }

    private static ArrayList<Position> smoothenRoute(ArrayList<Position> originalRoute, MapDataProvider mapDataProvider, ArrayList<Obstacle> detectedObstacles) {
        int lastRunNodeCount;
        ArrayList<Position> routeCopy = new ArrayList<>(originalRoute);
        ArrayList<Position> checkPositions;
        while (true) {
            checkPositions = new ArrayList<>();
            lastRunNodeCount = routeCopy.size();
            ArrayList<Position> deleteList = new ArrayList<>();
            for (int i = 2; i < routeCopy.size(); i += 2) {
                Position first = routeCopy.get(i - 2);
                Position second = routeCopy.get(i);

                double c = distanceBetween(first, second);
                double steps = c / stepSize;
                double step_a = (second.getLatitude().getValue() - first.getLatitude().getValue()) / steps;
                double step_b = (second.getLongitude().getValue() - first.getLongitude().getValue()) / steps;
                boolean waterFound = true;
                for (int j = 1; j < steps; j++) {
                    Position checkPosition = new Position(first.getLongitude().getValue() + step_b * j, first.getLatitude().getValue() + step_a * j, 0);
                    checkPositions.add(checkPosition);
                    if (mapDataProvider.getInfrastructureAtPosition(checkPosition, PossibleDomains.MARITIME).isEmpty()) {
                        waterFound = false;
                        break;
                    }
                    List<Obstacle> temp = new ArrayList<>();
                    try {
                        temp = mapDataProvider.getObstacleAtPosition(checkPosition, detectedObstacles);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (!temp.isEmpty()) {
                        waterFound = false;
                        break;
                    }
                }

                if (waterFound) {
                    deleteList.add(routeCopy.get(i - 1));
                }
            }
            routeCopy.removeAll(deleteList);

            if (routeCopy.size() == lastRunNodeCount) {
                break;
            }
        }
        return routeCopy;
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
            Position position2 = new Position(position.getLongitude().getValue(), position.getLatitude().getValue(), position.getAltitude().getValue());

            // TODO i think this is _very_ inefficient
            while (RouteUtils.coordinateDiffInMeters(position2.getLatitude().getValue(), position2.getLongitude().getValue(), position.getLatitude().getValue(), position.getLongitude().getValue()) < width) {
                position2.setLongitude(position2.getLongitude().getValue() + 0.000005);
            }

            Position position3 = new Position(position.getLongitude().getValue(), position.getLatitude().getValue(), position.getAltitude().getValue());

            // TODO i think this is _very_ inefficient
            while (RouteUtils.coordinateDiffInMeters(position3.getLatitude().getValue(), position3.getLongitude().getValue(), position.getLatitude().getValue(), position.getLongitude().getValue()) < length) {
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
