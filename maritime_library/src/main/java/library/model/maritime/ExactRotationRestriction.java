package library.model.maritime;

import library.model.simulation.SimulationProperty;
import library.model.simulation.enums.NumericalRestrictionType;
import library.model.traffic.TrafficRestriction;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * A traffic restriction that requires vessels to maintain an exact rotation angle.
 */
@XmlRootElement
public class ExactRotationRestriction extends TrafficRestriction<Double> {

    public ExactRotationRestriction(SimulationProperty<Double> value) {
        super();
        value.setName("rotation");
        this.addLimitedProperty(value);
        this.setNumericalRestrictionType(NumericalRestrictionType.EXACT);
    }

    public ExactRotationRestriction() {
        super();
    }
}
