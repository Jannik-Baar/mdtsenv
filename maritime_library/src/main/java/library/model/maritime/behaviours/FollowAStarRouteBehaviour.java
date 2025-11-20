package library.model.maritime.behaviours;

import library.model.examples.goals.PositionalGoal;
import library.model.maritime.Vessel;
import library.model.simulation.IBehaviour;
import library.model.simulation.IGoal;
import library.model.simulation.Position;
import library.model.simulation.objects.SimulationObject;
import library.model.traffic.TrafficParticipant;
import library.services.geodata.MapDataProvider;
import org.geotools.feature.SchemaException;
import org.locationtech.jts.io.ParseException;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class FollowAStarRouteBehaviour extends FollowRouteBehaviour implements IBehaviour {

    private boolean routeCalculationInProgress = false;

    private MapDataProvider mapDataProvider;

    private HashSet<IGoal> usedGoals;
    private Position currentGoalPosition;

    public FollowAStarRouteBehaviour() {
        this.usedGoals = new HashSet<>();
    }

    @Override
    public void setSimulationObject(SimulationObject simulationObject) {
        if (simulationObject instanceof TrafficParticipant) {
            this.trafficParticipant = (TrafficParticipant) simulationObject;
        }
    }

    @Override
    public Map<String, Object> nextStep(double timePassed) {
        //ASSUMES USAGE OF TIME STEP SIZE = 1
        //timePassed = new SimulationProperty<>(true, true, TimeUnit.SECOND, SECONDS_PER_STEP, "timePassed");

        if (this.route == null && !routeCalculationInProgress) {
            routeCalculationInProgress = true;
            new Thread(() -> {
                setMapDataProvider();
                try {
                    System.out.println("Request route to next point for " + (trafficParticipant instanceof Vessel ? ((Vessel) trafficParticipant).getVesselName() : "NOT-A-VESSEL"));
                    this.route = RouteUtils.generateRoute(mapDataProvider, trafficParticipant.getPosition().getValue(), getEndPositionFromGoals(), currentGoalPosition, null, true);
                    routeCalculationInProgress = false;
                } catch (ParseException | SchemaException | IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }

//        int secondsWaited = 0;
//        while (this.route == null && secondsWaited < SECONDS_PER_STEP) {
//            try {
//                sleep(1000);
//                secondsWaited++;
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }

        if (this.route != null) {
            super.nextStep(timePassed);
        }
        return new HashMap<>();
    }

    private void setMapDataProvider() {
        this.mapDataProvider = MapDataProvider.getMap(this.trafficParticipant);
    }

    private Position getEndPositionFromGoals() {
        for (IGoal goal : this.getGoals()) {
            if (goal instanceof PositionalGoal && !usedGoals.contains(goal)) {
                this.currentGoalPosition = ((PositionalGoal) goal).getFinalPosition();
                usedGoals.add(goal);
                return ((PositionalGoal) goal).getFinalPosition();
            }
        }
        return new Position();
    }

    public void setMapDataProvider(MapDataProvider mapDataProvider) {
        this.mapDataProvider = mapDataProvider;
    }


}
