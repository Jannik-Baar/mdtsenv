package library.model.examples.behaviours;

import library.model.simulation.Behaviour;
import library.model.simulation.Goal;
import library.model.simulation.IBehaviour;
import library.model.simulation.objects.SimulationObject;
import library.model.traffic.TrafficParticipant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Calculates new Position of an SimulationObject for a specific past time
 */
public class ConstantSpeedBehaviour extends Behaviour implements IBehaviour {

    private final static Logger LOGGER = Logger.getLogger(ConstantSpeedBehaviour.class.getName());

    private TrafficParticipant trafficParticipant;

    // TODO move stuff like this into a static service class
    final double r = 6371 * 1000; // Earth Radius in m

    public ConstantSpeedBehaviour() {
    }

    public ConstantSpeedBehaviour(ArrayList<Goal> goals, TrafficParticipant trafficParticipant) {
        super(goals);
        this.trafficParticipant = trafficParticipant;
    }

    public ConstantSpeedBehaviour(TrafficParticipant trafficParticipant) {
        super();
        this.trafficParticipant = trafficParticipant;
    }

    /**
     * Calculates the new Position of
     *
     * @param timePassed passed time in seconds
     * @return
     */
    @Override
    public HashMap<String, Object> nextStep(double timePassed) {

        double distance = this.trafficParticipant.getSpeed().getValue() * timePassed;
        double latOld = trafficParticipant.getPosition().getValue().getLatitude().getValue();
        double lonOld = trafficParticipant.getPosition().getValue().getLongitude().getValue();
        double bearing = trafficParticipant.getRotation().getValue();

        double latNew = Math.asin(Math.sin(Math.toRadians(latOld))
                * Math.cos(distance / r)
                + Math.cos(Math.toRadians(latOld)) * Math.sin(distance / r) * Math.cos(Math.toRadians(bearing)));

        double lonNew = Math.toRadians(lonOld)
                + Math.atan2(
                (
                        Math.sin(Math.toRadians(bearing))
                * Math.sin(distance / r)
                * Math.cos(Math.toRadians(latOld))
            ),
                Math.cos(distance / r)
                        - Math.sin(Math.toRadians(latOld))
                        * Math.sin(latNew)
        );

        latNew = Math.toDegrees(latNew);
        lonNew = Math.toDegrees(lonNew);

        // UPDATE VALUES
        HashMap<String, Object> valuesToUpdate = new HashMap<>();
        valuesToUpdate.put(trafficParticipant.getPosition().getValue().getLongitude().getId(), lonNew);
        valuesToUpdate.put(trafficParticipant.getPosition().getValue().getLatitude().getId(), latNew);
        LOGGER.log(Level.INFO, trafficParticipant.getClass().getName() + ":" + this.trafficParticipant.getId() + "    task pushed:    x:" + lonNew + "    y:" + latNew);

        return valuesToUpdate;
    }

    @Override
    public void setSimulationObject(SimulationObject trafficParticipant) {
        if (trafficParticipant instanceof TrafficParticipant) {
            this.trafficParticipant = (TrafficParticipant) trafficParticipant;
        }
    }

}
