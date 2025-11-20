package library.model.simulation.enums;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class to provide the ability to define if a restriction gives a min, max or exact value which is restricted
 */
@XmlRootElement
public enum NumericalRestrictionType {

    MIN,
    MAX,
    EXACT,
    NOTNUMERICAL
}
