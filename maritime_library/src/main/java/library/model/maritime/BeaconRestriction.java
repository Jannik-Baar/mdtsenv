package library.model.maritime;

import library.model.simulation.SimulationProperty;
import library.model.traffic.TrafficRestriction;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a restriction imposed by a Beacon.
 * Contains a single beacon and derives its properties from that beacon.
 * The restriction area represents the influence area of the beacon.
 */
@Setter
@Getter
@XmlRootElement
public class BeaconRestriction extends TrafficRestriction<BeaconType> {

    /**
     * The beacon that defines this restriction.
     */
    @XmlElement
    private Beacon beacon;

    public BeaconRestriction() {
        super();
    }

    /**
     * Creates a BeaconRestriction with the given beacon.
     *
     * @param beacon The beacon that defines this restriction.
     */
    public BeaconRestriction(Beacon beacon) {
        super();
        this.beacon = beacon;
        
        // Add the beacon's mark type as a limited property
        if (beacon != null && beacon.getBeaconType() != null) {
            this.addLimitedProperty(beacon.getBeaconType());
        }
    }

    /**
     * Gets the IALA region from the contained beacon.
     *
     * @return The region, or null if no beacon is set.
     */
    public SimulationProperty<Region> getRegion() {
        return beacon != null ? beacon.getRegion() : null;
    }

    /**
     * Gets the color from the contained beacon.
     *
     * @return The color, or null if no beacon is set.
     */
    public SimulationProperty<BeaconColor> getColor() {
        return beacon != null ? beacon.getColor() : null;
    }

    /**
     * Gets the shape from the contained beacon.
     *
     * @return The shape, or null if no beacon is set.
     */
    public SimulationProperty<BeaconShape> getShape() {
        return beacon != null ? beacon.getShape() : null;
    }

    /**
     * Gets the mark type from the contained beacon.
     *
     * @return The mark type, or null if no beacon is set.
     */
    public SimulationProperty<BeaconType> getMarkType() {
        return beacon != null ? beacon.getBeaconType() : null;
    }

    /**
     * Gets the light signal from the contained beacon.
     *
     * @return The light signal, or null if no beacon is set.
     */
    public LightSignal getLightSignal() {
        return beacon != null ? beacon.getLightSignal() : null;
    }

    /**
     * Gets the name from the contained beacon.
     *
     * @return The name, or null if no beacon is set.
     */
    public SimulationProperty<String> getName() {
        return beacon != null ? beacon.getName() : null;
    }
}

