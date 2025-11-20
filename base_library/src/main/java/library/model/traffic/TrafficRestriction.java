package library.model.traffic;

import library.model.limitations.Constraint;
import library.model.limitations.Restriction;
import library.model.simulation.SimulationProperty;
import library.model.simulation.enums.NumericalRestrictionType;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A restriction that should be imposed TrafficParticipants
 *
 * @param <T>
 */
@XmlRootElement
public class TrafficRestriction<T> extends Restriction<T> {

    public TrafficRestriction(SimulationProperty<T> value, NumericalRestrictionType numericalRestrictionType) {
        super(value, numericalRestrictionType);
    }

    public TrafficRestriction(SimulationProperty<T> value) {
        super(value);
    }

    public TrafficRestriction() {
        super();
    }
}
