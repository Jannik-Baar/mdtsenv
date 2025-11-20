package library.model.simulation.enums;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Types for attributes to signify if in simulationAttribute has only a single value or provides a list or range
 */
@XmlRootElement
public enum SimulationPropertyType {

    RANGE,
    SINGLE,
    LIST
}
