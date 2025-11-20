package library.model.maritime.behaviours;

import library.model.simulation.IBehaviour;
import library.model.simulation.Position;
import library.model.simulation.objects.SimulationObject;
import library.model.traffic.TrafficParticipant;

import java.util.ArrayList;

public class FollowGivenRouteBehaviour extends FollowRouteBehaviour implements IBehaviour {

    public FollowGivenRouteBehaviour() {
        super();
    }

    @Override
    public void setSimulationObject(SimulationObject simulationObject) {
        if (simulationObject instanceof TrafficParticipant) {
            TrafficParticipant trafficParticipant = (TrafficParticipant) simulationObject;
            ArrayList<Position> route = new ArrayList<>();
            trafficParticipant.getRoute().forEach(t -> route.add(t.getValue()));
            this.trafficParticipant = trafficParticipant;
            this.route = route;
        }
    }
}
