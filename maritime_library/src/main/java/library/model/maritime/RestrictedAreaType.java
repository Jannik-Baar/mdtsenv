package library.model.maritime;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * Defines the types of restricted areas in maritime navigation.
 */
@XmlType
@XmlEnum
public enum RestrictedAreaType {
    /**
     * Military exercise area - used for naval exercises, weapons testing, etc.
     * Entry is typically prohibited during active exercises.
     */
    MILITARY_ZONE,

    /**
     * Nature conservation area - protected habitat for wildlife.
     * Entry may be prohibited or restricted to protect sensitive ecosystems.
     */
    NATURE_CONSERVATION,

    /**
     * Construction zone - area where construction or maintenance work is ongoing.
     * Entry is restricted for safety reasons.
     */
    CONSTRUCTION_ZONE,

    /**
     * Hazardous cargo handling area - zone where dangerous goods are loaded/unloaded.
     * Entry is restricted for safety and security reasons.
     */
    HAZARDOUS_CARGO_ZONE,

    /**
     * Archaeological site - area containing underwater cultural heritage.
     * Entry is restricted to protect historical artifacts.
     */
    ARCHAEOLOGICAL_SITE,

    /**
     * Environmental protection zone - area protected for environmental reasons.
     * Entry may be restricted to prevent pollution or environmental damage.
     */
    ENVIRONMENTAL_PROTECTION,

    /**
     * Security zone - area restricted for security reasons.
     * Entry is controlled or prohibited for safety and security.
     */
    SECURITY_ZONE,

    /**
     * Anchorage prohibited - area where anchoring is not allowed.
     * Navigation may be permitted but anchoring is restricted.
     */
    ANCHORAGE_PROHIBITED,

    /**
     * Fishing prohibited - area where fishing is not allowed.
     * Navigation may be permitted but fishing activities are restricted.
     */
    FISHING_PROHIBITED,

    /**
     * General restricted area - any other type of restricted area.
     * Used for restrictions not covered by other specific types.
     */
    GENERAL_RESTRICTION
}