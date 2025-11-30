package library.model.maritime;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * Defines the physical shape/structure of a Beacon.
 * Beacons are fixed structures and differ significantly from buoy shapes.
 */
@XmlType
@XmlEnum
public enum BeaconShape {
    POLE,
    PERCH,
    PILE,
    LATTICE,
    TOWER,
    CAIRN,
    PANEL,
    STRUCTURE,
    STAKE
}