package library.model.maritime.behaviours;

import library.model.maritime.Vessel;
import library.model.simulation.Behaviour;
import library.model.simulation.Goal;
import library.model.simulation.objects.SimulationObject;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Acceleration behaviour for vessels that takes inertia into account.
 * The vessel will gradually accelerate towards a target speed, with the
 * rate of acceleration influenced by the vessel's inertia (0-1).
 * 
 * Higher inertia (closer to 1) means the vessel responds more slowly to acceleration.
 * Lower inertia (closer to 0) means the vessel responds more quickly.
 */
public class AccelerationBehaviour extends Behaviour {

    private final static Logger LOGGER = Logger.getLogger(AccelerationBehaviour.class.getName());

    private Vessel vessel;
    private double targetSpeed; // Target speed in m/s
    
    // Earth Radius in meters
    final double r = 6371 * 1000;

    public AccelerationBehaviour() {
    }

    public AccelerationBehaviour(Vessel vessel, double targetSpeed) {
        super();
        this.vessel = vessel;
        this.targetSpeed = targetSpeed;
    }

    public AccelerationBehaviour(List<Goal> goals, Vessel vessel, double targetSpeed) {
        super(goals);
        this.vessel = vessel;
        this.targetSpeed = targetSpeed;
    }

    /**
     * Calculates the new position and speed after acceleration for one timestep.
     * 
     * @param timePassed passed time in seconds
     * @return HashMap containing updated values for position and speed
     */
    @Override
    public HashMap<String, Object> nextStep(double timePassed) {
        
        double currentSpeed = vessel.getSpeed().getValue();
        double maxAcceleration = vessel.getMaxAcceleration().getValue();
        double inertia = vessel.getInertia().getValue();
        
        // Calculate effective acceleration considering inertia
        // Higher inertia (closer to 1) reduces the effective acceleration
        // Inertia acts as a damping factor: effectiveAccel = maxAccel * (1 - inertia)
        double inertiaFactor = 1.0 - inertia;
        double effectiveAcceleration = maxAcceleration * inertiaFactor;
        
        // Calculate speed change for this timestep
        double speedChange = effectiveAcceleration * timePassed;
        
        // Calculate new speed (cannot exceed target speed)
        double newSpeed = Math.min(currentSpeed + speedChange, targetSpeed);
        
        // Use average speed for distance calculation to be more accurate
        double averageSpeed = (currentSpeed + newSpeed) / 2.0;
        double distance = averageSpeed * timePassed;
        
        // Calculate new position using great circle navigation
        double latOld = vessel.getPosition().getValue().getLatitude().getValue();
        double lonOld = vessel.getPosition().getValue().getLongitude().getValue();
        double bearing = vessel.getRotation().getValue();
        
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
        
        // Calculate actual acceleration achieved
        double actualAcceleration = (newSpeed - currentSpeed) / timePassed;

        // Update values
        HashMap<String, Object> valuesToUpdate = new HashMap<>();
        valuesToUpdate.put(vessel.getPosition().getValue().getLongitude().getId(), lonNew);
        valuesToUpdate.put(vessel.getPosition().getValue().getLatitude().getId(), latNew);
        valuesToUpdate.put(vessel.getSpeed().getId(), newSpeed);
        valuesToUpdate.put(vessel.getAcceleration().getId(), actualAcceleration);
        
        LOGGER.log(Level.INFO, String.format(
            "%s:%s accelerating - Speed: %.2f->%.2f m/s (target: %.2f m/s), Inertia: %.2f, Position: (%.6f, %.6f)",
            vessel.getClass().getSimpleName(),
            vessel.getId(),
            currentSpeed,
            newSpeed,
            targetSpeed,
            inertia,
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

    public double getTargetSpeed() {
        return targetSpeed;
    }

    public void setTargetSpeed(double targetSpeed) {
        this.targetSpeed = targetSpeed;
    }
}

