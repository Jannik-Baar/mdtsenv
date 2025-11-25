package library.model.maritime;

import library.model.simulation.SimulationProperty;
import library.model.simulation.enums.NumericalRestrictionType;
import library.model.traffic.TrafficRestriction;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class MinRotationRestriction extends TrafficRestriction<Double> {

    public MinRotationRestriction(SimulationProperty<Double> value) {
        super();
        value.setName("rotation");
        this.addLimitedProperty(value);
        this.setNumericalRestrictionType(NumericalRestrictionType.MIN);
    }

    public MinRotationRestriction() {
        super();
    }
}
