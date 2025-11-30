package library.model.maritime;

import library.model.simulation.Position;
import library.model.simulation.SimulationProperty;
import library.model.simulation.units.NoUnit;
import library.model.traffic.Infrastructure;
import library.model.traffic.PossibleDomains;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Geometry;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

/**
 * Represents a Restricted Area where navigation is prohibited or restricted.
 */
@Getter
@Setter
@XmlRootElement
public class RestrictedArea extends Infrastructure {

    /** The type of restricted area */
    @XmlElement
    private SimulationProperty<RestrictedAreaType> restrictionType;

    /** The name or identifier of the restricted area */
    @XmlElement
    private SimulationProperty<String> areaName;

    /** The reason for the restriction */
    @XmlElement
    private SimulationProperty<String> reason;

    /** Indicates whether this is a permanent or temporary restriction */
    @XmlElement
    private SimulationProperty<Boolean> isPermanent;

    public RestrictedArea() {
        super();
    }

    public RestrictedArea(boolean physical, Position position, Geometry form, double rotation,
                          RestrictedAreaType restrictionType, String areaName,
                          String reason, boolean isPermanent) {
        super(physical, position, form, rotation);

        ArrayList<PossibleDomains> domains = new ArrayList<>();
        domains.add(PossibleDomains.MARITIME);
        this.setCanBeUsedBy(domains);

        this.restrictionType = new SimulationProperty<>(
                false, false, NoUnit.get(), restrictionType, "restrictionType"
        );
        this.areaName = new SimulationProperty<>(
                false, false, NoUnit.get(), areaName, "areaName"
        );
        this.reason = new SimulationProperty<>(
                false, false, NoUnit.get(), reason, "reason"
        );
        this.isPermanent = new SimulationProperty<>(
                false, false, NoUnit.get(), isPermanent, "isPermanent"
        );
    }

    public RestrictedArea(boolean physical, Position position, Geometry form, double rotation,
                          RestrictedAreaType restrictionType, String areaName,
                          String reason, boolean isPermanent,
                          ArrayList<PossibleDomains> possibleDomains) {
        super(physical, position, form, rotation, possibleDomains);

        this.restrictionType = new SimulationProperty<>(
                false, false, NoUnit.get(), restrictionType, "restrictionType"
        );
        this.areaName = new SimulationProperty<>(
                false, false, NoUnit.get(), areaName, "areaName"
        );
        this.reason = new SimulationProperty<>(
                false, false, NoUnit.get(), reason, "reason"
        );
        this.isPermanent = new SimulationProperty<>(
                false, false, NoUnit.get(), isPermanent, "isPermanent"
        );
    }

    /**
     * Checks if entry into this restricted area is completely prohibited.
     */
    public boolean isEntryProhibited() {
        if (restrictionType == null || restrictionType.getValue() == null) {
            return false;
        }

        RestrictedAreaType type = restrictionType.getValue();

        switch (type) {
            case MILITARY_ZONE:
            case CONSTRUCTION_ZONE:
            case SECURITY_ZONE:
            case HAZARDOUS_CARGO_ZONE:
                return true;

            case ANCHORAGE_PROHIBITED:
            case FISHING_PROHIBITED:
            case NATURE_CONSERVATION:
            case ENVIRONMENTAL_PROTECTION:
            case ARCHAEOLOGICAL_SITE:
                return false;

            case GENERAL_RESTRICTION:
            default:
                return false;
        }
    }

    /**
     * Checks if anchoring is prohibited in this restricted area.
     */
    public boolean isAnchoringProhibited() {
        if (restrictionType == null || restrictionType.getValue() == null) {
            return false;
        }

        RestrictedAreaType type = restrictionType.getValue();

        switch (type) {
            case ANCHORAGE_PROHIBITED:
            case MILITARY_ZONE:
            case CONSTRUCTION_ZONE:
            case SECURITY_ZONE:
            case HAZARDOUS_CARGO_ZONE:
            case ARCHAEOLOGICAL_SITE:
            case ENVIRONMENTAL_PROTECTION:
            case NATURE_CONSERVATION:
                return true;

            case FISHING_PROHIBITED:
            case GENERAL_RESTRICTION:
            default:
                return false;
        }
    }

    /**
     * Checks if fishing is prohibited in this restricted area.
     */
    public boolean isFishingProhibited() {
        if (restrictionType == null || restrictionType.getValue() == null) {
            return false;
        }

        RestrictedAreaType type = restrictionType.getValue();

        switch (type) {
            case FISHING_PROHIBITED:
            case NATURE_CONSERVATION:
            case ENVIRONMENTAL_PROTECTION:
            case ARCHAEOLOGICAL_SITE:
            case HAZARDOUS_CARGO_ZONE:
            case MILITARY_ZONE:
                return true;

            case CONSTRUCTION_ZONE:
            case SECURITY_ZONE:
            case ANCHORAGE_PROHIBITED:
            case GENERAL_RESTRICTION:
            default:
                return false;
        }
    }

    /**
     * Gets the reason for the restriction.
     */
    public String getRestrictionReason() {
        if (reason != null) {
            return reason.getValue();
        }
        return null;
    }
}