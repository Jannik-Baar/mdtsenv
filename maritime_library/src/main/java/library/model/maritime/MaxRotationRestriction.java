package library.model.maritime;

import library.model.simulation.SimulationProperty;
import library.model.simulation.enums.NumericalRestrictionType;
import library.model.traffic.TrafficRestriction;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class MaxRotationRestriction extends TrafficRestriction<Double> {

    public MaxRotationRestriction(SimulationProperty<Double> value) {
        super();
        value.setName("rotation");
        this.addLimitedProperty(value);
        this.setNumericalRestrictionType(NumericalRestrictionType.MAX);
    }

    public MaxRotationRestriction() {
        super();
    }
}
