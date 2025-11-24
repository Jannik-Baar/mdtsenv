package library.model.maritime;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * Defines the types of Safe Water Marks based on their function.
 */
@XmlType
@XmlEnum
public enum SafeWaterMarkType {
    /**
     * Mid-channel mark indicating safe water on all sides.
     */
    MID_CHANNEL,

    /**
     * Landfall mark indicating the approach to a channel or harbor.
     */
    LANDFALL,

    /**
     * Fairway mark indicating the center of a navigable channel.
     */
    FAIRWAY
}

