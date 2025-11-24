package library.model.maritime;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * Defines the types of marks in the IALA Lateral System.
 * Represents requirement F-3.3.
 */
@XmlType
@XmlEnum
public enum LateralMarkType {
    /**
     * Port hand mark (Backbord-Seite).
     * Region A: Red, Can shape. Region B: Green, Can shape.
     */
    PORT_HAND,

    /**
     * Starboard hand mark (Steuerbord-Seite).
     * Region A: Green, Cone shape. Region B: Red, Cone shape.
     */
    STARBOARD_HAND,

    /**
     * Preferred channel to starboard (Hauptfahrwasser an Steuerbord).
     * Modified Port hand mark.
     */
    PREFERRED_CHANNEL_TO_STARBOARD,

    /**
     * Preferred channel to port (Hauptfahrwasser an Backbord).
     * Modified Starboard hand mark.
     */
    PREFERRED_CHANNEL_TO_PORT
}