package library.model.maritime;

import library.model.simulation.SimulationProperty;
import library.model.simulation.units.NoUnit;
import library.model.traffic.TrafficRestriction;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a restriction for a Restricted Area (Sperrgebiet).
 * <p>
 * This restriction defines the type of limitation imposed on a specific maritime area,
 * including whether entry is prohibited, the reason for the restriction, and whether
 * it is permanent or temporary.
 * </p>
 */
@Setter
@Getter
@XmlRootElement
public class RestrictedAreaRestriction extends TrafficRestriction<RestrictedAreaType> {

    /**
     * The type of restricted area.
     */
    @XmlElement
    private SimulationProperty<RestrictedAreaType> restrictionType;

    /**
     * The name or identifier of the restricted area.
     */
    @XmlElement
    private SimulationProperty<String> areaName;

    /**
     * The reason for the restriction (e.g., "Military exercise area", "Nature conservation").
     */
    @XmlElement
    private SimulationProperty<String> reason;

    /**
     * Indicates whether this is a permanent or temporary restriction.
     * True = permanent, False = temporary.
     */
    @XmlElement
    private SimulationProperty<Boolean> isPermanent;

    /**
     * Default constructor for JAXB.
     */
    public RestrictedAreaRestriction() {
        super();
    }

    /**
     * Creates a Restricted Area Restriction.
     *
     * @param restrictionType The type of restriction.
     * @param areaName The name or identifier of the area.
     * @param reason The reason for the restriction.
     * @param isPermanent Whether this is a permanent restriction.
     */
    public RestrictedAreaRestriction(RestrictedAreaType restrictionType,
                                     String areaName,
                                     String reason,
                                     boolean isPermanent) {
        super();

        // 1. Set Restriction Type (the main restricted property)
        SimulationProperty<RestrictedAreaType> typeProp = new SimulationProperty<>(
                false, false, NoUnit.get(), restrictionType, "restrictionType"
        );
        this.addLimitedProperty(typeProp);
        this.restrictionType = typeProp;

        // 2. Set Area Name
        this.areaName = new SimulationProperty<>(
                false, false, NoUnit.get(), areaName, "areaName"
        );

        // 3. Set Reason
        this.reason = new SimulationProperty<>(
                false, false, NoUnit.get(), reason, "reason"
        );

        // 4. Set Permanent Flag
        this.isPermanent = new SimulationProperty<>(
                false, false, NoUnit.get(), isPermanent, "isPermanent"
        );
    }

    /**
     * Checks if entry into this restricted area is completely prohibited.
     * <p>
     * Certain restriction types (e.g., MILITARY_ZONE, CONSTRUCTION_ZONE, SECURITY_ZONE)
     * typically prohibit all entry, while others may only restrict specific activities.
     * </p>
     *
     * @return true if entry is prohibited, false otherwise.
     */
    public boolean isEntryProhibited() {
        if (restrictionType == null || restrictionType.getValue() == null) {
            return false;
        }

        RestrictedAreaType type = restrictionType.getValue();

        // These types typically prohibit all entry
        switch (type) {
            case MILITARY_ZONE:
            case CONSTRUCTION_ZONE:
            case SECURITY_ZONE:
            case HAZARDOUS_CARGO_ZONE:
                return true;

            // These types may allow navigation but restrict specific activities
            case ANCHORAGE_PROHIBITED:
            case FISHING_PROHIBITED:
            case NATURE_CONSERVATION:
            case ENVIRONMENTAL_PROTECTION:
            case ARCHAEOLOGICAL_SITE:
                return false; // Navigation may be allowed, but specific activities are restricted

            case GENERAL_RESTRICTION:
            default:
                // For general restrictions, we assume navigation is restricted but not necessarily prohibited
                return false;
        }
    }

    /**
     * Checks if anchoring is prohibited in this restricted area.
     *
     * @return true if anchoring is prohibited, false otherwise.
     */
    public boolean isAnchoringProhibited() {
        if (restrictionType == null || restrictionType.getValue() == null) {
            return false;
        }

        RestrictedAreaType type = restrictionType.getValue();

        // Most restricted areas prohibit anchoring
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
     *
     * @return true if fishing is prohibited, false otherwise.
     */
    public boolean isFishingProhibited() {
        if (restrictionType == null || restrictionType.getValue() == null) {
            return false;
        }

        RestrictedAreaType type = restrictionType.getValue();

        // Several types prohibit fishing
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
}