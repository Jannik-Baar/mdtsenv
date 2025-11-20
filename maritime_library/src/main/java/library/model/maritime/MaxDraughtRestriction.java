package library.model.maritime;

import library.model.simulation.SimulationProperty;
import library.model.simulation.enums.NumericalRestrictionType;
import library.model.traffic.TrafficRestriction;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class MaxDraughtRestriction extends TrafficRestriction<Double> {

    public MaxDraughtRestriction(SimulationProperty<Double> value) {
        super();
        value.setName("draught");
        this.addLimitedProperty(value);
        this.setNumericalRestrictionType(NumericalRestrictionType.MAX);
    }

    public MaxDraughtRestriction() {
        super();
    }
}
