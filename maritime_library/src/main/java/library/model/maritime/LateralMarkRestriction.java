package library.model.maritime;

import library.model.simulation.SimulationProperty;
import library.model.traffic.TrafficRestriction;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a restriction imposed by the IALA Lateral System.
 * Contains a single lateral mark and derives its properties from that mark.
 */
@Setter
@Getter
@XmlRootElement
public class LateralMarkRestriction extends TrafficRestriction<LateralMarkType> {

    /**
     * The lateral mark that defines this restriction.
     */
    @XmlElement
    private LateralMark lateralMark;

    public LateralMarkRestriction() {
        super();
    }

    /**
     * Creates a LateralMarkRestriction with the given lateral mark.
     *
     * @param lateralMark The lateral mark that defines this restriction.
     */
    public LateralMarkRestriction(LateralMark lateralMark) {
        super();
        this.lateralMark = lateralMark;
        
        // Add the mark's type as a limited property
        if (lateralMark != null && lateralMark.getMarkType() != null) {
            this.addLimitedProperty(lateralMark.getMarkType());
        }
    }

    /**
     * Gets the IALA region from the contained lateral mark.
     *
     * @return The region, or null if no mark is set.
     */
    public SimulationProperty<Region> getRegion() {
        return lateralMark != null ? lateralMark.getRegion() : null;
    }

    /**
     * Gets the color from the contained lateral mark.
     *
     * @return The color, or null if no mark is set.
     */
    public SimulationProperty<LateralMarkColor> getColor() {
        return lateralMark != null ? lateralMark.getColor() : null;
    }

    /**
     * Gets the shape from the contained lateral mark.
     *
     * @return The shape, or null if no mark is set.
     */
    public SimulationProperty<LateralMarkShape> getShape() {
        return lateralMark != null ? lateralMark.getShape() : null;
    }

    /**
     * Gets the number/identifier from the contained lateral mark.
     *
     * @return The number, or null if no mark is set.
     */
    public SimulationProperty<String> getNumber() {
        return lateralMark != null ? lateralMark.getNumber() : null;
    }

    /**
     * Gets the mark type from the contained lateral mark.
     *
     * @return The mark type, or null if no mark is set.
     */
    public SimulationProperty<LateralMarkType> getMarkType() {
        return lateralMark != null ? lateralMark.getMarkType() : null;
    }
}

