package library.model.maritime;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * Defines the possible colors of a lateral mark.
 * Includes combined colors for preferred channels (bands).
 */
@XmlType
@XmlEnum
public enum LateralMarkColor {
    RED,
    GREEN,
    /** Red with Green band (Preferred channel to starboard in Reg A) */
    RED_GREEN_RED,
    /** Green with Red band (Preferred channel to port in Reg A) */
    GREEN_RED_GREEN
}