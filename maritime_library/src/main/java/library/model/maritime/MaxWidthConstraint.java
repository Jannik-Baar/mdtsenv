package library.model.maritime;

import library.model.simulation.SimulationProperty;
import library.model.simulation.enums.NumericalRestrictionType;
import library.model.traffic.TrafficConstraint;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a maximum width constraint for vessels.
 * This constraint limits the maximum width that a vessel can have to pass through a channel,
 * lock, canal, or other narrow passage.
 * 
 * Implements requirement F-5.1: The simulation model must support the representation
 * of width constraints for model elements of type ship.
 */
@XmlRootElement
public class MaxWidthConstraint extends TrafficConstraint<Double> {

    /**
     * Creates a MaxWidthConstraint with the specified maximum width value.
     *
     * @param value The maximum width value as a SimulationProperty.
     */
    public MaxWidthConstraint(SimulationProperty<Double> value) {
        super();
        value.setName("width");
        this.addLimitedProperty(value);
        this.setNumericalRestrictionType(NumericalRestrictionType.MAX);
    }

    /**
     * Default constructor for JAXB serialization.
     */
    public MaxWidthConstraint() {
        super();
    }
}

