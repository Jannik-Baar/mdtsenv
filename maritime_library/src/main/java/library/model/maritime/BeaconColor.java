package library.model.maritime;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * Defines the colors and color patterns for Beacons.
 */
@XmlType
@XmlEnum
public enum BeaconColor {
    // Single Colors
    RED,
    GREEN,
    YELLOW,
    WHITE,
    BLACK,
    ORANGE, // Oft für Tagesmarken/Richtfeuer verwendet
    GREY,   // Unbemalter Stein/Beton

    // Patterns / Combinations (Bands & Stripes)
    RED_GREEN_BAND,      // Preferred Channel
    GREEN_RED_BAND,      // Preferred Channel
    BLACK_YELLOW_BANDS,  // Cardinal
    RED_WHITE_STRIPES,   // Safe Water
    YELLOW_BLACK_STRIPES, // Special / Cardinal variations
    BLUE_YELLOW_STRIPES,  // Emergency Wreck
    RED_WHITE_HORIZONTAL, // Oft bei Richtfeuern
    BLACK_WHITE_HORIZONTAL
}