package library.model.maritime;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * Defines the shape (top mark or buoy body) of a lateral mark.
 */
@XmlType
@XmlEnum
public enum LateralMarkShape {
    /** Cylindrical / Can shape (Stumpf) - typically Port in Reg A */
    CAN,
    /** Conical / Cone shape (Spitz) - typically Starboard in Reg A */
    CONE,
    /** Pillar shape (Leuchttonne/Bake) - often used with top marks */
    PILLAR,
    /** Spar shape (Spier) */
    SPAR
}