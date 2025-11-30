package library.model.maritime.behaviours;

import library.model.maritime.Vessel;
import library.model.simulation.Behaviour;
import library.model.simulation.Goal;
import library.model.simulation.objects.SimulationObject;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Deceleration behaviour for vessels that takes inertia into account.
 * The vessel will gradually decelerate towards a target speed (or stop), with the
 * rate of deceleration influenced by the vessel's inertia (0-1).
 * 
 * Higher inertia (closer to 1) means the vessel responds more slowly to deceleration,
 * requiring more time and distance to slow down (like a heavy cargo ship).
 * Lower inertia (closer to 0) means the vessel responds more quickly (like a small boat).
 */
public class DecelerationBehaviour extends Behaviour {

    private final static Logger LOGGER = Logger.getLogger(DecelerationBehaviour.class.getName());

    private Vessel vessel;
    @Setter
    @Getter
    private double targetSpeed; // Target speed in m/s (can be 0 for full stop)

    final double r = 6371 * 1000; // Earth Radius in meters

    public DecelerationBehaviour() {
    }

    public DecelerationBehaviour(Vessel vessel, double targetSpeed) {
        super();
        this.vessel = vessel;
        this.targetSpeed = targetSpeed;
    }

    public DecelerationBehaviour(List<Goal> goals, Vessel vessel, double targetSpeed) {
        super(goals);
        this.vessel = vessel;
        this.targetSpeed = targetSpeed;
    }

    /**
     * Calculates the new position and speed after deceleration for one timestep.
     * 
     * @param timePassed passed time in seconds
     * @return HashMap containing updated values for position and speed
     */
    @Override
    public HashMap<String, Object> nextStep(double timePassed) {
        
        double currentSpeed = vessel.getSpeed().getValue();
        double maxDeceleration = vessel.getMaxDeceleration().getValue();
        double inertia = vessel.getInertia().getValue();

        double inertiaFactor = 1.0 - inertia;
        double effectiveDeceleration = maxDeceleration * inertiaFactor;

        double speedChange = effectiveDeceleration * timePassed;

        double newSpeed = Math.max(currentSpeed - speedChange, targetSpeed);
        newSpeed = Math.max(newSpeed, 0.0);

        double averageSpeed = (currentSpeed + newSpeed) / 2.0;
        double distance = averageSpeed * timePassed;

        double latOld = vessel.getPosition().getValue().getLatitude().getValue();
        double lonOld = vessel.getPosition().getValue().getLongitude().getValue();
        double bearing = vessel.getRotation().getValue();

        double latNew = latOld;
        double lonNew = lonOld;
        
        if (distance > 0.0001) {
            latNew = Math.asin(Math.sin(Math.toRadians(latOld))
                    * Math.cos(distance / r)
                    + Math.cos(Math.toRadians(latOld)) * Math.sin(distance / r) * Math.cos(Math.toRadians(bearing)));

            lonNew = Math.toRadians(lonOld)
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
        }

        double actualAcceleration = -(currentSpeed - newSpeed) / timePassed;

        HashMap<String, Object> valuesToUpdate = new HashMap<>();
        valuesToUpdate.put(vessel.getPosition().getValue().getLongitude().getId(), lonNew);
        valuesToUpdate.put(vessel.getPosition().getValue().getLatitude().getId(), latNew);
        valuesToUpdate.put(vessel.getSpeed().getId(), newSpeed);
        valuesToUpdate.put(vessel.getAcceleration().getId(), actualAcceleration);
        
        LOGGER.log(Level.INFO, String.format(
            "%s:%s decelerating - Speed: %.2f->%.2f m/s (target: %.2f m/s), Inertia: %.2f, Distance: %.2f m, Position: (%.6f, %.6f)",
            vessel.getClass().getSimpleName(),
            vessel.getId(),
            currentSpeed,
            newSpeed,
            targetSpeed,
            inertia,
            distance,
            lonNew,
            latNew
        ));

        return valuesToUpdate;
    }

    @Override
    public void setSimulationObject(SimulationObject simulationObject) {
        if (simulationObject instanceof Vessel) {
            this.vessel = (Vessel) simulationObject;
        }
    }
}
