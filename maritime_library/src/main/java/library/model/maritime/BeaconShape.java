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
    POLE,           // Pfahl / Stange (oft im Watt oder flachen Wasser)
    PERCH,          // Pricke (Baumstamm/Ast, oft unbeleuchtet)
    PILE,           // Dalben (Gruppe von Pfählen)
    LATTICE,        // Gittermast (typisch für Molenfeuer oder Richtfeuer)
    TOWER,          // Massiver Turm (Stein, Beton, Metall)
    CAIRN,          // Steinhaufen / Steinbake
    PANEL,          // Tafel (oft bei Richtfeuern verwendet)
    STRUCTURE,      // Sonstiges massives Bauwerk
    STAKE           // Kleiner Pfosten
}