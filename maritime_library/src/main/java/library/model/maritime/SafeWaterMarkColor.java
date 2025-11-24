package library.model.maritime;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * Defines the color pattern of a Safe Water Mark.
 * According to IALA standards, safe water marks have red and white vertical stripes.
 */
@XmlType
@XmlEnum
public enum SafeWaterMarkColor {
    /**
     * Red and white vertical stripes.
     * This is the standard IALA color pattern for safe water marks.
     */
    RED_WHITE_VERTICAL_STRIPES
}
