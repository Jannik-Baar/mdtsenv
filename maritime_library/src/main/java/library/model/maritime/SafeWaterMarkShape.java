package library.model.maritime;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * Defines the possible shapes of a Safe Water Mark.
 * According to IALA standards, safe water marks can have different shapes.
 */
@XmlType
@XmlEnum
public enum SafeWaterMarkShape {
    /**
     * Spherical shape - a ball or sphere shaped buoy.
     * Most common shape for safe water marks.
     */
    SPHERICAL,

    /**
     * Pillar shape - a tall, narrow structure.
     * Often used for larger, more permanent safe water marks.
     */
    PILLAR,

    /**
     * Spar shape - a long, thin floating structure.
     * Less common but still IALA compliant.
     */
    SPAR
}
