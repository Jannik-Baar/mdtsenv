package library.model.maritime;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * Defines the possible colors of a lighthouse light.
 * According to IALA and international maritime standards.
 */
@XmlType
@XmlEnum
public enum LighthouseColor {
    /**
     * White light - most common color for major lighthouses.
     * Provides the best visibility and range.
     */
    WHITE,

    /**
     * Red light - used to indicate port side or hazards.
     * Also used for sectored lights to mark danger zones.
     */
    RED,

    /**
     * Green light - used to indicate starboard side.
     * Also used in sectored lights.
     */
    GREEN,

    /**
     * Yellow/Amber light - used for caution or to mark specific channels.
     * Less common than white, red, or green.
     */
    YELLOW,

    /**
     * Alternating white and red - changes between white and red.
     * Used for special marks or to increase conspicuity.
     */
    WHITE_RED_ALTERNATING,

    /**
     * Alternating white and green - changes between white and green.
     * Used for special marks.
     */
    WHITE_GREEN_ALTERNATING,

    /**
     * Alternating red and green - changes between red and green.
     * Rare, but used in some jurisdictions.
     */
    RED_GREEN_ALTERNATING
}