package library.model.maritime;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * Defines the functional type of a Beacon
 */
@XmlType
@XmlEnum
public enum BeaconType {
    LATERAL_PORT,
    LATERAL_STARBOARD,
    PREFERRED_CHANNEL_TO_STARBOARD,
    PREFERRED_CHANNEL_TO_PORT,
    CARDINAL_NORTH,
    CARDINAL_EAST,
    CARDINAL_SOUTH,
    CARDINAL_WEST,
    ISOLATED_DANGER,
    SAFE_WATER,
    SPECIAL,
    EMERGENCY_WRECK,
    LEADING_LINE,
    SECTOR_LIGHT,
    GENERAL_WARNING,
    NOTICE
}