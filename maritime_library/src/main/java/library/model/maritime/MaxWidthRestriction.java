package library.model.maritime;

import library.model.simulation.SimulationProperty;
import library.model.simulation.enums.NumericalRestrictionType;
import library.model.traffic.TrafficRestriction;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class MaxWidthRestriction extends TrafficRestriction<Double> {

    public MaxWidthRestriction(SimulationProperty<Double> value) {
        super();
        value.setName("width");
        this.addLimitedProperty(value);
        this.setNumericalRestrictionType(NumericalRestrictionType.MAX);
    }

    public MaxWidthRestriction() {
        super();
    }
}
