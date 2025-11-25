package library.model.maritime;

import library.model.simulation.SimulationProperty;
import library.model.simulation.enums.NumericalRestrictionType;
import library.model.traffic.TrafficConstraint;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a maximum freeboard (height) constraint for vessels.
 * This constraint limits the maximum freeboard that a vessel can have to pass under bridges,
 * overhead cables, and other low-clearance obstacles.
 * Freeboard is the vertical distance between the waterline and the main deck.
 * 
 * Implements requirement F-5.3: The simulation model must support the representation
 * of height constraints for model elements of type ship.
 */
@XmlRootElement
public class MaxFreeboardConstraint extends TrafficConstraint<Double> {

    /**
     * Creates a MaxFreeboardConstraint with the specified maximum freeboard value.
     *
     * @param value The maximum freeboard value as a SimulationProperty.
     */
    public MaxFreeboardConstraint(SimulationProperty<Double> value) {
        super();
        value.setName("freeboard");
        this.addLimitedProperty(value);
        this.setNumericalRestrictionType(NumericalRestrictionType.MAX);
    }

    /**
     * Default constructor for JAXB serialization.
     */
    public MaxFreeboardConstraint() {
        super();
    }
}

