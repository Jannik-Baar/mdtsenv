package library.model.limitations;

import library.model.simulation.SimulationProperty;
import library.model.simulation.enums.NumericalRestrictionType;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Constraint<T> extends Limitation<T> {

    public Constraint() {
        super();
    }

    protected Constraint(SimulationProperty<T> limitedProperty) {
        super(limitedProperty);
    }

    protected Constraint(SimulationProperty<T> limitedProperty, NumericalRestrictionType numericalRestrictionType) {
        super(limitedProperty, numericalRestrictionType);
    }

}
