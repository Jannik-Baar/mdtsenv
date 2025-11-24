package library.model.maritime;

import library.model.simulation.SimulationProperty;
import library.model.traffic.TrafficRestriction;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a restriction/influence area of a Safe Water Mark.
 * Contains a single safe water mark and derives its properties from that mark.
 * The restriction area represents the safe water zone indicated by the mark.
 */
@Setter
@Getter
@XmlRootElement
public class SafeWaterMarkRestriction extends TrafficRestriction<SafeWaterMarkType> {

    /**
     * The safe water mark that defines this restriction.
     */
    @XmlElement
    private SafeWaterMark safeWaterMark;

    /**
     * The type of safe water mark (mid-channel, landfall, fairway).
     */
    @XmlElement
    private SimulationProperty<SafeWaterMarkType> markType;

    public SafeWaterMarkRestriction() {
        super();
    }

    /**
     * Creates a SafeWaterMarkRestriction with the given safe water mark.
     *
     * @param safeWaterMark The safe water mark that defines this restriction.
     * @param markType The type of safe water mark.
     */
    public SafeWaterMarkRestriction(SafeWaterMark safeWaterMark, SafeWaterMarkType markType) {
        super();
        this.safeWaterMark = safeWaterMark;
        
        // Set the mark type
        if (markType != null) {
            this.markType = new SimulationProperty<>(
                false, false, 
                library.model.simulation.units.NoUnit.get(), 
                markType, 
                "safeWaterMarkType"
            );
            this.addLimitedProperty(this.markType);
        }
    }

    /**
     * Gets the name from the contained safe water mark.
     *
     * @return The name, or null if no mark is set.
     */
    public SimulationProperty<String> getName() {
        return safeWaterMark != null ? safeWaterMark.getName() : null;
    }

    /**
     * Gets the color from the contained safe water mark.
     *
     * @return The color, or null if no mark is set.
     */
    public SimulationProperty<SafeWaterMarkColor> getColor() {
        return safeWaterMark != null ? safeWaterMark.getColor() : null;
    }

    /**
     * Gets the shape from the contained safe water mark.
     *
     * @return The shape, or null if no mark is set.
     */
    public SimulationProperty<SafeWaterMarkShape> getShape() {
        return safeWaterMark != null ? safeWaterMark.getShape() : null;
    }

    /**
     * Gets the marking text from the contained safe water mark.
     *
     * @return The marking, or null if no mark is set.
     */
    public SimulationProperty<String> getMarking() {
        return safeWaterMark != null ? safeWaterMark.getMarking() : null;
    }

    /**
     * Gets the light signal from the contained safe water mark.
     *
     * @return The light signal, or null if no mark is set.
     */
    public LightSignal getLightSignal() {
        return safeWaterMark != null ? safeWaterMark.getLightSignal() : null;
    }

    /**
     * Checks if the safe water mark is lit.
     *
     * @return true if the mark has a light signal, false otherwise.
     */
    public boolean isLit() {
        return safeWaterMark != null && safeWaterMark.isLit();
    }
}

