package library.model.traffic;

import library.model.limitations.Constraint;
import library.model.simulation.SimulationProperty;
import library.model.simulation.enums.NumericalRestrictionType;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * A physical constraint that should be imposed on TrafficParticipants.
 * Constraints represent physical limitations (e.g., a ship cannot pass through a narrow channel).
 * This is different from Restrictions which represent rules (e.g., speed limits, traffic regulations).
 *
 * @param <T> The type of the constraint value
 */
@XmlRootElement
public class TrafficConstraint<T> extends Constraint<T> {

    public TrafficConstraint(SimulationProperty<T> value, NumericalRestrictionType numericalRestrictionType) {
        super(value, numericalRestrictionType);
    }

    public TrafficConstraint(SimulationProperty<T> value) {
        super(value);
    }

    public TrafficConstraint() {
        super();
    }
}

