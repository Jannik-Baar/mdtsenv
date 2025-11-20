package library.model.maritime.behaviours;

import library.model.examples.goals.TimeGoal;
import library.model.maritime.ContainerShip;
import library.model.simulation.Behaviour;
import library.model.simulation.Goal;
import library.model.simulation.objects.SimulationObject;
import library.model.simulation.SimulationProperty;
import library.model.simulation.units.TimeUnit;
import library.model.traffic.TrafficParticipant;
import org.geotools.feature.SchemaException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Behaviour for a vessels engine. The Behaviour sets the speed of a vessel to 0 after the TimeGoal is reached.
 * If no TimeGoal is setted, the default value is 1 Hour. The Behaviour unit of the time should be in seconds
 */
public class DefectEngineBehaviour extends Behaviour {

    // TODO this definitely has to be configured somewhere globally!
    //Configures how as how many seconds one simulation step is interpreted
    protected final double SECONDS_PER_STEP = 5.0;

    private TrafficParticipant vessel;
    private TimeGoal timeGoal;
    private ArrayList<Goal> goals = new ArrayList<>();
    private double timePassed = 0;
    private boolean first = true;
    private boolean saved = false;

    /**
     * On first call the Behaviour sets the default timeGoal of 3600 Seconds
     * Sets the vessels speed to 0.0 if the TimeGoal is reached
     *
     * @param timePassed
     * @return
     */
    @Override
    public Map<String, Object> nextStep(double timePassed) {

        Map<String, Object> attributeUpdates = new HashMap<>();

        // QUESTION: why was this overwritten?
        // ASSUMES USAGE OF TIME STEP SIZE = 1
        // timePassed = new SimulationAttribute<Double>(true, true, TimeUnit.SECOND, SECONDS_PER_STEP, "timePassed");

        if (first) {
            checkForTimeGoal();
            first = false;
        }
        this.timePassed += timePassed;
        timeGoal.setCurrentTime(this.timePassed);
        if (timeGoal.check(this.timePassed)) {
            attributeUpdates.put(vessel.getSpeed().getId(), 0.0);
            attributeUpdates.put(((ContainerShip) vessel).getEmergencyDeclared().getId(), true);
            if (!saved) {
                saved = true;
                System.out.println("Engine broke down at time " + this.timePassed + " position " + vessel.getPosition().getValue().getLongitude().getValue() + ", " + vessel.getPosition().getValue().getLatitude().getValue());
                if (vessel.getBehaviour() instanceof FollowRouteBehaviour) {
                    try {
                        FollowRouteBehaviour.route2Shapefiles(((FollowRouteBehaviour) vessel.getBehaviour()).visitedPositions, "visited_untilBreakDown");
                    } catch (SchemaException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return attributeUpdates;
    }

    /**
     * Checks if a TimeGoal is set. If the goals doesn't contain a TimeGoal, a default TimeGoal of 3600 seconds is set.
     *
     * @return
     */
    private boolean checkForTimeGoal() {
        boolean res = false;
        for (Goal g : goals) {
            if (g instanceof TimeGoal) {
                timeGoal = (TimeGoal) g;
                res = true;
            }
        }
        if (!res) {
            this.timeGoal = new TimeGoal(new SimulationProperty<>(TimeUnit.SECOND, 3600.0, "time when the engine should stop working"));
            goals.add(timeGoal);
        }
        return res;
    }

    @Override
    public void setGoals(ArrayList<Goal> goals) {
        this.goals = goals;
    }

    @Override
    public void setSimulationObject(SimulationObject vessel) {
        if (vessel instanceof TrafficParticipant) {
            this.vessel = (TrafficParticipant) vessel;
        }
    }

    @Override
    public void addGoal(Goal goal) {
        goals.add(goal);
    }

    @Override
    public ArrayList<Goal> getGoals() {
        return null;
    }
}
