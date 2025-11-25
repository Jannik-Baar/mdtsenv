package library.model.maritime;

import library.model.simulation.SimulationProperty;
import library.model.simulation.enums.NumericalRestrictionType;
import library.model.traffic.TrafficConstraint;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a maximum draught (depth) constraint for vessels.
 * This constraint limits the maximum draught that a vessel can have to navigate through
 * shallow waters, harbor entrances, channels, or other depth-restricted areas.
 * Draught is the vertical distance between the waterline and the bottom of the hull (keel).
 * 
 * Implements requirement F-5.2: The simulation model must support the representation
 * of depth constraints for model elements of type ship.
 */
@XmlRootElement
public class MaxDraughtConstraint extends TrafficConstraint<Double> {

    /**
     * Creates a MaxDraughtConstraint with the specified maximum draught value.
     *
     * @param value The maximum draught value as a SimulationProperty.
     */
    public MaxDraughtConstraint(SimulationProperty<Double> value) {
        super();
        value.setName("draught");
        this.addLimitedProperty(value);
        this.setNumericalRestrictionType(NumericalRestrictionType.MAX);
    }

    /**
     * Default constructor for JAXB serialization.
     */
    public MaxDraughtConstraint() {
        super();
    }
}

