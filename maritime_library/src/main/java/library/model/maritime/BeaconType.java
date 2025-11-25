package library.model.maritime;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * Defines the functional type of a Beacon (Bake).
 * Unlike buoys, beacons can serve a wider range of functions including leading lines.
 */
@XmlType
@XmlEnum
public enum BeaconType {
    // Lateral Marks (Lateralzeichen)
    LATERAL_PORT,
    LATERAL_STARBOARD,
    PREFERRED_CHANNEL_TO_STARBOARD,
    PREFERRED_CHANNEL_TO_PORT,

    // Cardinal Marks (Kardinalzeichen)
    CARDINAL_NORTH,
    CARDINAL_EAST,
    CARDINAL_SOUTH,
    CARDINAL_WEST,

    // Other Marks
    ISOLATED_DANGER,
    SAFE_WATER,
    SPECIAL,
    EMERGENCY_WRECK,

    // Specific Beacon Functions
    LEADING_LINE,    // Richtfeuer (Ober-/Unterbake)
    SECTOR_LIGHT,    // Sektorenfeuer-Träger
    GENERAL_WARNING, // Allgemeine Warnbake
    NOTICE           // Hinweistafel
}