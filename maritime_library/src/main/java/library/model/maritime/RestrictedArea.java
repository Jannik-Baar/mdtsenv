package library.model.maritime;

import library.model.simulation.Position;
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
     * The type of restriction applied to this area.
     */
    @XmlElement
    private RestrictedAreaRestriction restriction;

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

        // Create and add the restriction
        this.restriction = new RestrictedAreaRestriction(restrictionType, areaName, reason, isPermanent);
        this.addRestriction(restriction);
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

        // Create and add the restriction
        this.restriction = new RestrictedAreaRestriction(restrictionType, areaName, reason, isPermanent);
        this.addRestriction(restriction);
    }

    // Getters and Setters

    public RestrictedAreaRestriction getRestriction() {
        return restriction;
    }

    public void setRestriction(RestrictedAreaRestriction restriction) {
        this.restriction = restriction;
    }

    /**
     * Checks if entry into this restricted area is completely prohibited.
     *
     * @return true if entry is prohibited, false otherwise.
     */
    public boolean isEntryProhibited() {
        if (restriction != null) {
            return restriction.isEntryProhibited();
        }
        return false;
    }

    /**
     * Gets the reason for the restriction.
     *
     * @return The reason string, or null if not set.
     */
    public String getRestrictionReason() {
        if (restriction != null && restriction.getReason() != null) {
            return restriction.getReason().getValue();
        }
        return null;
    }
}