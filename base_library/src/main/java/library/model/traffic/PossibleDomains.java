package library.model.traffic;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * All currently possible traffic domains.
 */
@XmlRootElement
public enum PossibleDomains {

    MARITIME,
    STREET,
    AIR,
    RAIL,
    SPACE
}
