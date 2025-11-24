package library.model.maritime;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * Defines the operational types/states of a lighthouse.
 */
@XmlType
@XmlEnum
public enum LighthouseType {
    /**
     * The lighthouse is active and operational.
     */
    ACTIVE,

    /**
     * The lighthouse is inactive or decommissioned.
     */
    INACTIVE,

    /**
     * The lighthouse is under maintenance.
     */
    MAINTENANCE
}

