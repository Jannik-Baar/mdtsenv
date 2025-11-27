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
 * Constant speed turning behaviour for vessels that respects the vessel's turning circle.
 * The vessel moves at a constant speed while turning towards a target bearing.
 * The rate of turn is calculated based on the vessel's turning circle radius and inertia.
 * 
 * A larger turning circle means slower, wider turns (like a large cargo ship).
 * A smaller turning circle means sharper, quicker turns (like a small boat).
 */
public class TurningBehaviour extends Behaviour {

    private final static Logger LOGGER = Logger.getLogger(TurningBehaviour.class.getName());

    private Vessel vessel;
    private double targetBearing; // Target bearing in degrees (0-360)
    
    // Earth Radius in meters
    final double r = 6371 * 1000;

    public TurningBehaviour() {
    }

    public TurningBehaviour(Vessel vessel, double targetBearing) {
        super();
        this.vessel = vessel;
        this.targetBearing = normalizeAngle(targetBearing);
    }

    public TurningBehaviour(List<Goal> goals, Vessel vessel, double targetBearing) {
        super(goals);
        this.vessel = vessel;
        this.targetBearing = normalizeAngle(targetBearing);
    }

    /**
     * Calculates the new position and rotation after turning for one timestep.
     * The vessel maintains constant speed while turning at a rate determined by
     * the turning circle radius.
     * 
     * @param timePassed passed time in seconds
     * @return HashMap containing updated values for position and rotation
     */
    @Override
    public HashMap<String, Object> nextStep(double timePassed) {
        
        double currentSpeed = vessel.getSpeed().getValue();
        double currentBearing = normalizeAngle(vessel.getRotation().getValue());
        double turningCircle = vessel.getTurningCircle().getValue();
        double inertia = vessel.getInertia().getValue();
        
        // Calculate the maximum rate of turn based on turning circle
        // Rate of turn (degrees per second) = (speed / turning_circle_radius) * (180/π)
        // The turning circle property is the diameter, so radius = turningCircle / 2
        double turningRadius = turningCircle / 2.0;
        
        // Maximum angular velocity in radians per second
        // ω = v / r, where v is speed and r is turning radius
        double maxAngularVelocity = currentSpeed / turningRadius;
        
        // Apply inertia factor to turning rate
        // Higher inertia (closer to 1) makes the vessel turn more slowly
        double inertiaFactor = 1.0 - (inertia * 0.5); // Reduce impact of inertia on turning
        double effectiveAngularVelocity = maxAngularVelocity * inertiaFactor;
        
        // Convert to degrees per second
        double maxTurnRatePerSecond = Math.toDegrees(effectiveAngularVelocity);
        
        // Calculate the angular difference between current and target bearing
        double bearingDifference = calculateShortestAngleDifference(currentBearing, targetBearing);
        
        // Calculate actual turn for this timestep
        double turnThisStep = Math.min(Math.abs(bearingDifference), maxTurnRatePerSecond * timePassed);
        
        // Apply turn in the correct direction
        double newBearing;
        if (Math.abs(bearingDifference) < 0.01) {
            // Already at target bearing
            newBearing = targetBearing;
        } else if (bearingDifference > 0) {
            // Turn clockwise (increase bearing)
            newBearing = normalizeAngle(currentBearing + turnThisStep);
        } else {
            // Turn counter-clockwise (decrease bearing)
            newBearing = normalizeAngle(currentBearing - turnThisStep);
        }
        
        // Calculate distance traveled during this timestep
        double distance = currentSpeed * timePassed;
        
        // When turning, the vessel follows a circular arc
        // We use the average bearing for position calculation
        double averageBearing = currentBearing;
        if (Math.abs(turnThisStep) > 0.001) {
            // Use the bearing at the middle of the turn for more accurate position
            averageBearing = normalizeAngle(currentBearing + (newBearing - currentBearing) / 2.0);
        }
        
        // Calculate new position using great circle navigation with average bearing
        double latOld = vessel.getPosition().getValue().getLatitude().getValue();
        double lonOld = vessel.getPosition().getValue().getLongitude().getValue();
        
        double latNew = Math.asin(Math.sin(Math.toRadians(latOld))
                * Math.cos(distance / r)
                + Math.cos(Math.toRadians(latOld)) * Math.sin(distance / r) * Math.cos(Math.toRadians(averageBearing)));

        double lonNew = Math.toRadians(lonOld)
                + Math.atan2(
                (
                        Math.sin(Math.toRadians(averageBearing))
                * Math.sin(distance / r)
                * Math.cos(Math.toRadians(latOld))
            ),
                Math.cos(distance / r)
                        - Math.sin(Math.toRadians(latOld))
                        * Math.sin(latNew)
        );

        latNew = Math.toDegrees(latNew);
        lonNew = Math.toDegrees(lonNew);
        
        // Calculate actual turn rate achieved (degrees per second)
        double actualTurnRate = turnThisStep / timePassed;

        // Update values
        HashMap<String, Object> valuesToUpdate = new HashMap<>();
        valuesToUpdate.put(vessel.getPosition().getValue().getLongitude().getId(), lonNew);
        valuesToUpdate.put(vessel.getPosition().getValue().getLatitude().getId(), latNew);
        valuesToUpdate.put(vessel.getRotation().getId(), newBearing);
        // Update course to match rotation
        valuesToUpdate.put(vessel.getCourse().getId(), newBearing);
        
        LOGGER.log(Level.INFO, String.format(
            "%s:%s turning - Speed: %.2f m/s, Bearing: %.1f°->%.1f° (target: %.1f°), Turn rate: %.2f°/s, Turning circle: %.1f m, Position: (%.6f, %.6f)",
            vessel.getClass().getSimpleName(),
            vessel.getId(),
            currentSpeed,
            currentBearing,
            newBearing,
            targetBearing,
            actualTurnRate,
            turningCircle,
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

    /**
     * Normalizes an angle to the range [0, 360)
     */
    private double normalizeAngle(double angle) {
        angle = angle % 360;
        if (angle < 0) {
            angle += 360;
        }
        return angle;
    }

    /**
     * Calculates the shortest angular difference between two bearings.
     * Returns positive value for clockwise turn, negative for counter-clockwise.
     * Result is in range [-180, 180]
     */
    private double calculateShortestAngleDifference(double from, double to) {
        double difference = to - from;
        
        // Normalize to [-180, 180]
        while (difference > 180) {
            difference -= 360;
        }
        while (difference < -180) {
            difference += 360;
        }
        
        return difference;
    }

    public double getTargetBearing() {
        return targetBearing;
    }

    public void setTargetBearing(double targetBearing) {
        this.targetBearing = normalizeAngle(targetBearing);
    }
}

