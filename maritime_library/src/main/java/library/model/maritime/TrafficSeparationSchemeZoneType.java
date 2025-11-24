package library.model.maritime;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

/**
 * Enumeration of Traffic Separation Scheme (TSS) zone types according to IMO standards.
 * A TSS is a routing measure that separates opposing streams of maritime traffic
 * by establishing traffic lanes.
 */
@XmlEnum
public enum TrafficSeparationSchemeZoneType {
    
    /**
     * A defined area where traffic moves in a specified direction.
     * Ships must follow the general direction of traffic flow.
     */
    @XmlEnumValue("TRAFFIC_LANE")
    TRAFFIC_LANE,
    
    /**
     * A zone separating traffic lanes in which ships must not normally enter.
     * This provides a buffer between opposing traffic flows.
     */
    @XmlEnumValue("SEPARATION_ZONE")
    SEPARATION_ZONE,
    
    /**
     * A separation line separating traffic lanes where the separation zone is very narrow.
     */
    @XmlEnumValue("SEPARATION_LINE")
    SEPARATION_LINE,
    
    /**
     * A routing measure comprising a separation zone or line and adjacent traffic lanes.
     */
    @XmlEnumValue("ROUNDABOUT")
    ROUNDABOUT,
    
    /**
     * A zone designated for ships not using the TSS.
     * Often used by smaller vessels or those making local passages.
     */
    @XmlEnumValue("INSHORE_TRAFFIC_ZONE")
    INSHORE_TRAFFIC_ZONE,
    
    /**
     * An area within defined limits where caution must be exercised.
     */
    @XmlEnumValue("PRECAUTIONARY_AREA")
    PRECAUTIONARY_AREA,
    
    /**
     * An area to be avoided by certain classes of ships or carrying certain cargoes.
     */
    @XmlEnumValue("AREA_TO_BE_AVOIDED")
    AREA_TO_BE_AVOIDED,
    
    /**
     * A defined area where ships are prohibited from anchoring.
     */
    @XmlEnumValue("NO_ANCHORING_AREA")
    NO_ANCHORING_AREA
}

