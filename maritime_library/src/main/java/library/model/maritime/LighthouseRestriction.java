package library.model.maritime;

import library.model.simulation.SimulationProperty;
import library.model.traffic.TrafficRestriction;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a restriction/influence area of a Lighthouse.
 * Contains a single lighthouse and derives its properties from that lighthouse.
 * The restriction area represents the visible and effective range of the lighthouse.
 */
@Setter
@Getter
@XmlRootElement
public class LighthouseRestriction extends TrafficRestriction<LighthouseType> {

    /**
     * The lighthouse that defines this restriction.
     */
    @XmlElement
    private Lighthouse lighthouse;

    public LighthouseRestriction() {
        super();
    }

    /**
     * Creates a LighthouseRestriction with the given lighthouse.
     *
     * @param lighthouse The lighthouse that defines this restriction.
     */
    public LighthouseRestriction(Lighthouse lighthouse) {
        super();
        this.lighthouse = lighthouse;
        
        // Add the lighthouse's operational state as a limited property
        if (lighthouse != null && lighthouse.getIsActive() != null) {
            LighthouseType type = lighthouse.getIsActive().getValue() 
                ? LighthouseType.ACTIVE 
                : LighthouseType.INACTIVE;
            SimulationProperty<LighthouseType> typeProp = new SimulationProperty<>(
                false, false, 
                lighthouse.getIsActive().getUnit(), 
                type, 
                "lighthouseType"
            );
            this.addLimitedProperty(typeProp);
        }
    }

    /**
     * Gets the name from the contained lighthouse.
     *
     * @return The name, or null if no lighthouse is set.
     */
    public SimulationProperty<String> getName() {
        return lighthouse != null ? lighthouse.getName() : null;
    }

    /**
     * Gets the height from the contained lighthouse.
     *
     * @return The height, or null if no lighthouse is set.
     */
    public SimulationProperty<Double> getHeight() {
        return lighthouse != null ? lighthouse.getHeight() : null;
    }

    /**
     * Gets the light signal from the contained lighthouse.
     *
     * @return The light signal, or null if no lighthouse is set.
     */
    public LightSignal getLightSignal() {
        return lighthouse != null ? lighthouse.getLightSignal() : null;
    }

    /**
     * Gets the focal height from the contained lighthouse.
     *
     * @return The focal height, or null if no lighthouse is set.
     */
    public SimulationProperty<Double> getFocalHeight() {
        return lighthouse != null ? lighthouse.getFocalHeight() : null;
    }

    /**
     * Gets the active status from the contained lighthouse.
     *
     * @return The active status, or null if no lighthouse is set.
     */
    public SimulationProperty<Boolean> getIsActive() {
        return lighthouse != null ? lighthouse.getIsActive() : null;
    }

    /**
     * Gets the effective range from the contained lighthouse.
     *
     * @return The effective range in nautical miles, or 0.0 if no lighthouse is set.
     */
    public double getEffectiveRange() {
        return lighthouse != null ? lighthouse.getEffectiveRange() : 0.0;
    }

    /**
     * Gets the geographic range from the contained lighthouse.
     *
     * @return The geographic range in nautical miles, or 0.0 if no lighthouse is set.
     */
    public double getGeographicRange() {
        return lighthouse != null ? lighthouse.calculateGeographicRange() : 0.0;
    }
}

