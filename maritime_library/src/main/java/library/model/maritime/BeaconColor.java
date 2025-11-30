package library.model.maritime;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * Defines the colors and color patterns for Beacons.
 */
@XmlType
@XmlEnum
public enum BeaconColor {
    RED,
    GREEN,
    YELLOW,
    WHITE,
    BLACK,
    ORANGE,
    GREY,
    RED_GREEN_BAND,
    GREEN_RED_BAND,
    BLACK_YELLOW_BANDS,
    RED_WHITE_STRIPES,
    YELLOW_BLACK_STRIPES,
    BLUE_YELLOW_STRIPES,
    RED_WHITE_HORIZONTAL,
    BLACK_WHITE_HORIZONTAL
}
