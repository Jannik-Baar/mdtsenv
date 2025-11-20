package library.model.limitations;

import library.model.simulation.SimulationProperty;
import library.model.simulation.enums.NumericalRestrictionType;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * NUMERIC: MIN, MAX, ExactValue
 * BOOLEAN: TRUE/FALSE
 *
 * @param <T>
 */
@XmlRootElement
public class Restriction<T> extends Limitation<T> {

    public Restriction() {
        super();
    }

    protected Restriction(SimulationProperty<T> limitedProperty, NumericalRestrictionType numericalRestrictionType) {
        super(limitedProperty, numericalRestrictionType);
    }

    protected Restriction(SimulationProperty<T> limitedProperty) {
        super(limitedProperty);
    }

}
