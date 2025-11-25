package library.model.maritime;

import library.model.simulation.Position;
import library.model.simulation.SimulationProperty;
import library.model.simulation.units.NoUnit;
import library.model.traffic.Infrastructure;
import library.model.traffic.PossibleDomains;
import org.locationtech.jts.geom.Geometry;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

/**
 * Represents a Restricted Area (Sperrgebiet) - Requirement F-3.7.
 * <p>
 * A Restricted Area is a designated zone where navigation is prohibited or severely restricted
 * due to various reasons such as:
 * - Military exercises
 * - Nature conservation areas
 * - Construction zones
 * - Hazardous cargo handling areas
 * - Archaeological sites
 * - Environmental protection zones
 * </p>
 * <p>
 * Restricted areas can be permanent or temporary and may have different levels of restriction.
 * They are typically marked on nautical charts and may be indicated by buoys or other markers.
 * </p>
 */
@XmlRootElement
public class RestrictedArea extends Infrastructure {

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
    public RestrictedArea() {
        super();
    }

    /**
     * Creates a new Restricted Area (Sperrgebiet).
     *
     * @param physical Whether the area has physical boundaries (e.g., buoys, barriers).
     * @param position The reference position (e.g., center or corner of the area).
     * @param form The geometric boundary of the restricted area (typically a Polygon).
     * @param rotation The rotation of the area (if applicable).
     * @param restrictionType The type of restriction.
     * @param areaName The name or identifier of the restricted area.
     * @param reason The reason for the restriction.
     * @param isPermanent Whether this is a permanent or temporary restriction.
     */
    public RestrictedArea(boolean physical, Position position, Geometry form, double rotation,
                          RestrictedAreaType restrictionType, String areaName,
                          String reason, boolean isPermanent) {
        super(physical, position, form, rotation);

        // Restricted areas typically apply to maritime domain
        ArrayList<PossibleDomains> domains = new ArrayList<>();
        domains.add(PossibleDomains.MARITIME);
        this.setCanBeUsedBy(domains);

        // Initialize properties
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
     * Creates a new Restricted Area with specified domains.
     *
     * @param physical Whether the area has physical boundaries.
     * @param position The reference position.
     * @param form The geometric boundary of the restricted area.
     * @param rotation The rotation of the area.
     * @param restrictionType The type of restriction.
     * @param areaName The name or identifier of the restricted area.
     * @param reason The reason for the restriction.
     * @param isPermanent Whether this is a permanent or temporary restriction.
     * @param possibleDomains The domains to which this restriction applies.
     */
    public RestrictedArea(boolean physical, Position position, Geometry form, double rotation,
                          RestrictedAreaType restrictionType, String areaName,
                          String reason, boolean isPermanent,
                          ArrayList<PossibleDomains> possibleDomains) {
        super(physical, position, form, rotation, possibleDomains);

        // Initialize properties
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

    // Getters and Setters

    public SimulationProperty<RestrictedAreaType> getRestrictionType() {
        return restrictionType;
    }

    public void setRestrictionType(SimulationProperty<RestrictedAreaType> restrictionType) {
        this.restrictionType = restrictionType;
    }

    public SimulationProperty<String> getAreaName() {
        return areaName;
    }

    public void setAreaName(SimulationProperty<String> areaName) {
        this.areaName = areaName;
    }

    public SimulationProperty<String> getReason() {
        return reason;
    }

    public void setReason(SimulationProperty<String> reason) {
        this.reason = reason;
    }

    public SimulationProperty<Boolean> getIsPermanent() {
        return isPermanent;
    }

    public void setIsPermanent(SimulationProperty<Boolean> isPermanent) {
        this.isPermanent = isPermanent;
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

    /**
     * Gets the reason for the restriction.
     *
     * @return The reason string, or null if not set.
     */
    public String getRestrictionReason() {
        if (reason != null) {
            return reason.getValue();
        }
        return null;
    }
}