package library.model.maritime;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

/**
 * Enumeration of traffic flow directions within a Traffic Separation Scheme (TSS).
 * Specifies the permitted direction of vessel movement within a traffic lane.
 */
@XmlEnum
public enum TrafficSeparationSchemeTrafficDirection {
    
    /**
     * Traffic flows from north to south.
     */
    @XmlEnumValue("NORTH_TO_SOUTH")
    NORTH_TO_SOUTH,
    
    /**
     * Traffic flows from south to north.
     */
    @XmlEnumValue("SOUTH_TO_NORTH")
    SOUTH_TO_NORTH,
    
    /**
     * Traffic flows from east to west.
     */
    @XmlEnumValue("EAST_TO_WEST")
    EAST_TO_WEST,
    
    /**
     * Traffic flows from west to east.
     */
    @XmlEnumValue("WEST_TO_EAST")
    WEST_TO_EAST,
    
    /**
     * Traffic flows in a clockwise direction (for roundabouts).
     */
    @XmlEnumValue("CLOCKWISE")
    CLOCKWISE,
    
    /**
     * Traffic flows in a counter-clockwise direction (for roundabouts).
     */
    @XmlEnumValue("COUNTER_CLOCKWISE")
    COUNTER_CLOCKWISE,
    
    /**
     * Bidirectional traffic is allowed (for inshore traffic zones or special areas).
     */
    @XmlEnumValue("BIDIRECTIONAL")
    BIDIRECTIONAL,
    
    /**
     * No specified direction - area where normal navigation rules apply.
     */
    @XmlEnumValue("NO_RESTRICTION")
    NO_RESTRICTION,
    
    /**
     * Custom bearing-based direction (in degrees, 0-360).
     * The actual bearing value should be stored separately.
     */
    @XmlEnumValue("CUSTOM_BEARING")
    CUSTOM_BEARING
}

