package library.model.examples.goals;

import library.model.simulation.IGoal;
import library.model.simulation.Position;
import library.model.traffic.TrafficParticipant;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Provides a position as goal for a given traffic participant and a function to check if the goal is reached by the traffic participant
 */
@XmlRootElement
public class PositionalGoal implements IGoal {

    @XmlIDREF
    private final TrafficParticipant trafficParticipant;
    @XmlElement
    private final Position finalPosition;

    public PositionalGoal(TrafficParticipant trafficParticipant, Position finalPosition) {
        this.trafficParticipant = trafficParticipant;
        this.finalPosition = finalPosition;
    }

    /**
     * Compare the goal position to the current position of the traffic participant
     * @return true if latitude and longitude match
     */
    @Override
    public Boolean check() {
        return (finalPosition.getLongitude() == trafficParticipant.getPosition().getValue().getLongitude()
                && finalPosition.getLatitude() == trafficParticipant.getPosition().getValue().getLatitude());
    }

    public Position getFinalPosition() {
        return finalPosition;
    }

    public PositionalGoal() {
        trafficParticipant = null;
        finalPosition = null;
    }
}
