package library.model.maritime;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * Defines the light patterns (characteristics) of a lighthouse.
 * Based on IALA standards for light characteristics.
 */
@XmlType
@XmlEnum
public enum LighthousePattern {
    /**
     * Fixed light (F) - continuous steady light.
     * The light does not change in intensity.
     */
    FIXED,

    /**
     * Flashing (Fl) - light that flashes at regular intervals.
     * Duration of light is shorter than duration of darkness.
     * Most common pattern for major lighthouses.
     */
    FLASHING,

    /**
     * Quick flashing (Q) - flashes at a rate of 50-79 flashes per minute.
     * Used to mark special features or hazards.
     */
    QUICK_FLASHING,

    /**
     * Very quick flashing (VQ) - flashes at a rate of 80-159 flashes per minute.
     * Used for special marks or to indicate urgency.
     */
    VERY_QUICK_FLASHING,

    /**
     * Long flashing (LFl) - a single flash of not less than 2 seconds duration.
     * Repeated at regular intervals.
     */
    LONG_FLASHING,

    /**
     * Occulting (Oc) - light that is eclipsed at regular intervals.
     * Duration of light is longer than duration of darkness.
     */
    OCCULTING,

    /**
     * Isophase (Iso) - light and dark periods are of equal duration.
     * Creates a rhythmic on-off pattern.
     */
    ISOPHASE,

    /**
     * Morse code - light that flashes a specific morse code letter.
     * Commonly used: Mo(A) for "A" (dit-dah), Mo(U) for "U" (dit-dit-dah).
     */
    MORSE_CODE,

    /**
     * Group flashing (Fl(2), Fl(3), etc.) - a group of flashes repeated at regular intervals.
     * Number indicates how many flashes in each group.
     */
    GROUP_FLASHING,

    /**
     * Alternating - light that alternates between two colors.
     * Pattern can be flashing, occulting, etc., but with color change.
     */
    ALTERNATING,

    /**
     * Sector light - different colors or characteristics in different sectors.
     * Used to guide vessels in specific channels or warn of dangers.
     */
    SECTORED
}