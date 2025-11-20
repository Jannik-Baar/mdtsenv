package library.services.geodata;

import library.model.dto.scenario.ScenarioDTO;
import library.model.simulation.Position;
import library.model.simulation.objects.SimulationObject;
import library.model.traffic.Infrastructure;
import library.model.traffic.Obstacle;
import library.model.traffic.PossibleDomains;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static library.services.geodata.GeometryUtils.createGeometryFromPosition;

/**
 * Registers Simulation Objects on a map and can retrieve SimulationObjects at specified positions.
 */
public class MapDataProvider {

    private static HashMap<SimulationObject, MapDataProvider> pmdpMap = new HashMap<>();

    public static void addToMap(SimulationObject simulationObject, MapDataProvider mapDataProvider) {
        pmdpMap.put(simulationObject, mapDataProvider);
    }

    public static MapDataProvider getMap(SimulationObject simulationObject) {
        return pmdpMap.get(simulationObject);
    }

    HashMap<Geometry, Infrastructure> envelopMap;
    HashMap<Geometry, Obstacle> obstacleMap;

    public MapDataProvider(ScenarioDTO scenario) {
        envelopMap = new HashMap<>();
        obstacleMap = new HashMap<>();
        for (SimulationObject simulationObject : scenario.getSimulationObjects()) {
            if (simulationObject instanceof Infrastructure) {
                envelopMap.put(simulationObject.getForm().getValue().getEnvelope(), (Infrastructure) simulationObject);
            }
            if (simulationObject instanceof Obstacle) {
                obstacleMap.put(simulationObject.getForm().getValue().getEnvelope(), (Obstacle) simulationObject);
            }
        }
    }

    public List<Infrastructure> getInfrastructureAtPosition(Position position) throws ParseException {
        Geometry positionGeometry = createGeometryFromPosition(position);
        return envelopMap
                .entrySet()
                .stream()
                .filter(t -> t.getKey().contains(positionGeometry))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                .values()
                .stream()
                .filter(t -> t.getForm().getValue().contains(positionGeometry))
                .collect(Collectors.toList());
    }

    public List<Obstacle> getObstacleAtPosition(Position position, ArrayList<Obstacle> detectedObstacles) throws ParseException {
        Geometry positionGeometry = createGeometryFromPosition(position);
        HashMap<Geometry, Obstacle> nMap = new HashMap<>(obstacleMap);
        if (detectedObstacles != null) {
            for (Obstacle obs : detectedObstacles) {
                nMap.put(obs.getForm().getValue(), obs);
            }
        }
        return nMap
                .entrySet()
                .stream()
                .filter(t -> t.getKey().contains(positionGeometry))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                .values()
                .stream()
                .filter(t -> t.getForm().getValue().contains(positionGeometry))
                .collect(Collectors.toList());
    }

    public List<Obstacle> getObstacleIntersected(Geometry geometry, ArrayList<Obstacle> detectedObstacles) {
        List<Obstacle> output = new ArrayList<>();
        ArrayList<Obstacle> coll = new ArrayList<>(obstacleMap.values());
        if (detectedObstacles != null) {
            coll.addAll(detectedObstacles);
        }
        coll.stream().filter(t -> t.getForm().getValue().intersects(geometry)).forEach(output::add);
        return output;
    }

    public List<Infrastructure> getInfrastructureAtPosition(Position position, PossibleDomains possibleDomains) {
        List<Infrastructure> output;
        try {
            output = getInfrastructureAtPosition(position);
        } catch (ParseException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        List<Infrastructure> finalOutput = output;
        output.forEach(t -> {
            if (!t.isUsableBy(possibleDomains)) {
                finalOutput.remove(t);
            }
        });
        return output;
    }

    public List<Infrastructure> getAllInfrastructures() {
        return new ArrayList<>(envelopMap.values());
    }

    public List<Obstacle> getAllObstacles() {
        return new ArrayList<>(obstacleMap.values());
    }
}
