package library.model.maritime.behaviours;

import library.model.maritime.Vessel;
import library.model.simulation.Behaviour;
import library.model.simulation.Position;
import library.model.simulation.objects.SimulationObject;
import library.model.simulation.SimulationProperty;
import library.model.traffic.TrafficParticipant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class SimpleFollowRouteBehaviour extends Behaviour {

    protected final static Logger LOGGER = Logger.getLogger(SimpleFollowRouteBehaviour.class.getName());

    protected List<Position> route;
    protected Integer nextWaypointIndex;
    protected Boolean goalReached;
    protected TrafficParticipant trafficParticipant;
    protected List<Position> visitedPositions;

    public SimpleFollowRouteBehaviour() {
        this.nextWaypointIndex = 0;
        this.goalReached = false;
        this.visitedPositions = new ArrayList<>();
    }

    private HashMap<String, Position> positionMap = new HashMap<>();
    private HashMap<String, RouteUtils.PositionFormPair> positionDimensionMap = new HashMap<>();

    @Override
    public void setSimulationObject(SimulationObject simulationObject) {
        if (simulationObject instanceof TrafficParticipant) {
            this.trafficParticipant = (TrafficParticipant) simulationObject;
            this.route = ((TrafficParticipant) simulationObject).getRoute()
                                                                .stream()
                                                                .map(SimulationProperty::getValue)
                                                                .collect(Collectors.toList());
        }
    }

    @Override
    public Map<String, Object> nextStep(double timePassed) {

        ///////////////////////////////////////////////////////////////////////////////
        // LOGGING TO DEMONSTRATE THAT THIS TRAFFIC PARTICIPANT CAN 'SEE' THE OTHERS //
        ///////////////////////////////////////////////////////////////////////////////
        if (this.trafficParticipant instanceof Vessel) {
            Vessel vessel = (Vessel) this.trafficParticipant;
            if (vessel.getVesselName().getValue().equals("Hamburg Express")) {

                List<SimulationObject> observedObjects = this.trafficParticipant.getObservedObjects().values().stream().findFirst().orElseGet(ArrayList::new);
                SimulationObject observedObject = observedObjects.stream().findFirst().orElse(null);

                if (observedObject != null) {
                    System.out.println("////////////////");
                    System.out.println("// " + vessel.getVesselName().getValue() + " : I CAN SEE!");
                    System.out.println("// I HAVE KNOWLEDGE OF A PARTICIPANT OF CLASS'" + observedObject.getClass() + "'");
                    System.out.println("// THE PARTICIPANT IS NAMED: " + ((Vessel) observedObject).getVesselName().getValue());
                    System.out.println("// THE PARTICIPANT IS AT: " + (observedObject.getPosition() != null ? observedObject.getPosition().getValue().toString(): null));
                    System.out.println("////////////////");
                }
            }
        }

        Map<String, Object> attributeUpdates = new HashMap<>();

        double distancePerTimeStep = (this.trafficParticipant.getSpeed().getValue() * this.trafficParticipant.getSpeed().getUnit().getUnitToBase()) * timePassed;
        Position currentPosition = this.trafficParticipant.getPosition().getValue();

        if (distancePerTimeStep > 0 && !goalReached) {

            if (this.nextWaypointIndex == this.route.size()) {
                goalReached = true;
                // TODO use proper logger
                System.out.println(this.getClass().getName() + " " + ((Vessel) this.trafficParticipant).getVesselName().getValue() + " reached Goal");
            } else {
                double distanceToNextWaypoint = RouteUtils.coordinateDiffInMeters(currentPosition.getLatitude().getValue(), currentPosition.getLongitude().getValue(), this.route.get(this.nextWaypointIndex).getLatitude().getValue(), this.route.get(this.nextWaypointIndex).getLongitude().getValue());
                if (distanceToNextWaypoint > distancePerTimeStep) {
                    // move on a line from the current position to the next position to reach the next waypoint
                    double steps = distanceToNextWaypoint / distancePerTimeStep;
                    double step_lat = (route.get(nextWaypointIndex).getLatitude().getValue() - currentPosition.getLatitude().getValue()) / steps;
                    double step_lon = (route.get(nextWaypointIndex).getLongitude().getValue() - currentPosition.getLongitude().getValue()) / steps;

                    Position newPosition = new Position();
                    newPosition.setLatitude(currentPosition.getLatitude().getValue() + step_lat);
                    newPosition.setLongitude(currentPosition.getLongitude().getValue() + step_lon);
                    newPosition.setAltitude(currentPosition.getAltitude().getValue());
                    currentPosition = newPosition;
                } else {
                    currentPosition = this.route.get(this.nextWaypointIndex);
                    this.nextWaypointIndex += 1;
                }

                this.visitedPositions.add(currentPosition);

                attributeUpdates.put(trafficParticipant.getRotation().getId(), RouteUtils.angleFromCoordinate(trafficParticipant.getRotation().getValue(),
                        trafficParticipant.getPosition().getValue().getLatitude().getValue(),
                        trafficParticipant.getPosition().getValue().getLongitude().getValue(),
                        currentPosition.getLatitude().getValue(),
                        currentPosition.getLongitude().getValue()));
                attributeUpdates.put(trafficParticipant.getPosition().getValue().getLongitude().getId(), currentPosition.getLongitude().getValue());
                attributeUpdates.put(trafficParticipant.getPosition().getValue().getLatitude().getId(), currentPosition.getLatitude().getValue());
                attributeUpdates.put(trafficParticipant.getPosition().getValue().getAltitude().getId(), currentPosition.getAltitude().getValue());

                LOGGER.info(this.trafficParticipant.getId() + "    task pushed:    long=" + currentPosition.getLongitude().getValue() + "    lat=" + currentPosition.getLatitude().getValue());
            }
        }
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

}
