package library.model.maritime;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * Defines the IALA Maritime Buoyage System Regions.
 */
@XmlType
@XmlEnum
public enum Region {
    /**
     * Region A (Europe, Australia, New Zealand, Africa, etc.)
     * Red = Port, Green = Starboard.
     */
    REGION_A,

    /**
     * Region B (Americas, Japan, Korea, Philippines, etc.)
     * Green = Port, Red = Starboard.
     */
    REGION_B
}

