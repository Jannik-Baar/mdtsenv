package library.model.maritime;

import library.model.simulation.Position;
import library.model.simulation.SimulationProperty;
import library.model.simulation.units.NoUnit;
import library.model.traffic.Obstacle;
import org.locationtech.jts.geom.Geometry;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a Beacon (Bake/Molenfeuer) - Requirement F-3.9.
 * <p>
 * A Beacon is a fixed aid to navigation, typically placed at the head of a breakwater (Mole),
 * pier, or other prominent location to mark channels, harbor entrances, or dangers.
 * </p>
 */
@XmlRootElement
public class Beacon extends Obstacle {

    /**
     * The identifier/name of this beacon (e.g. "North Pier Beacon").
     * Defined explicitly here as superclasses do not provide a name property.
     */
    @XmlElement
    private SimulationProperty<String> name;

    /**
     * The lateral mark type (e.g., PORT_HAND, STARBOARD_HAND).
     */
    @XmlElement
    private SimulationProperty<LateralMarkType> markType;

    /**
     * The IALA region (A or B).
     */
    @XmlElement
    private SimulationProperty<Region> region;

    /**
     * The color of the beacon (derived from type and region).
     */
    @XmlElement
    private SimulationProperty<LateralMarkColor> color;

    /**
     * The shape of the beacon (derived from type and region).
     */
    @XmlElement
    private SimulationProperty<LateralMarkShape> shape;

    @XmlElement
    private LightSignal lightSignal;

    /**
     * Default constructor for JAXB.
     */
    public Beacon() {
        super();
    }

    /**
     * Creates a new Beacon.
     *
     * @param nameStr The identifier of the beacon (e.g., "North Pier Beacon").
     * @param position The fixed geographic position.
     * @param geometry The physical shape (e.g., the tower geometry).
     * @param markType The lateral type (e.g., PORT_HAND for the left side of the entrance).
     * @param region The IALA region (A or B).
     * @param lightSignal The light signal characteristics (can be null if not lit).
     */
    public Beacon(String nameStr, Position position, Geometry geometry, LateralMarkType markType, Region region, LightSignal lightSignal) {
        super(true, position, geometry, 0.0);

        // Initialize the name property locally
        this.name = new SimulationProperty<>(false, false, NoUnit.get(), nameStr, "name");

        // Set mark type
        this.markType = new SimulationProperty<>(false, false, NoUnit.get(), markType, "markType");

        // Set region
        this.region = new SimulationProperty<>(false, false, NoUnit.get(), region, "region");

        // Derive and set color and shape based on IALA rules
        applyStandardAppearance(markType, region);
        
        // Set the light signal
        this.lightSignal = lightSignal;
    }

    /**
     * Applies standard IALA colors and shapes based on type and region.
     */
    private void applyStandardAppearance(LateralMarkType type, Region region) {
        LateralMarkColor derivedColor = LateralMarkColor.RED; // Default fallback
        LateralMarkShape derivedShape = LateralMarkShape.CAN; // Default fallback

        if (region == Region.REGION_A) {
            switch (type) {
                case PORT_HAND:
                    derivedColor = LateralMarkColor.RED;
                    derivedShape = LateralMarkShape.CAN;
                    break;
                case STARBOARD_HAND:
                    derivedColor = LateralMarkColor.GREEN;
                    derivedShape = LateralMarkShape.CONE;
                    break;
                case PREFERRED_CHANNEL_TO_STARBOARD:
                    derivedColor = LateralMarkColor.RED_GREEN_RED;
                    derivedShape = LateralMarkShape.CAN;
                    break;
                case PREFERRED_CHANNEL_TO_PORT:
                    derivedColor = LateralMarkColor.GREEN_RED_GREEN;
                    derivedShape = LateralMarkShape.CONE;
                    break;
            }
        } else { // REGION_B
            switch (type) {
                case PORT_HAND:
                    derivedColor = LateralMarkColor.GREEN;
                    derivedShape = LateralMarkShape.CAN;
                    break;
                case STARBOARD_HAND:
                    derivedColor = LateralMarkColor.RED;
                    derivedShape = LateralMarkShape.CONE;
                    break;
                case PREFERRED_CHANNEL_TO_STARBOARD:
                    derivedColor = LateralMarkColor.GREEN_RED_GREEN;
                    derivedShape = LateralMarkShape.CAN;
                    break;
                case PREFERRED_CHANNEL_TO_PORT:
                    derivedColor = LateralMarkColor.RED_GREEN_RED;
                    derivedShape = LateralMarkShape.CONE;
                    break;
            }
        }

        this.color = new SimulationProperty<>(false, false, NoUnit.get(), derivedColor, "color");
        this.shape = new SimulationProperty<>(false, false, NoUnit.get(), derivedShape, "shape");
    }

    public SimulationProperty<String> getName() {
        return name;
    }

    public void setName(SimulationProperty<String> name) {
        this.name = name;
    }

    public SimulationProperty<LateralMarkType> getMarkType() {
        return markType;
    }

    public void setMarkType(SimulationProperty<LateralMarkType> markType) {
        this.markType = markType;
    }

    public SimulationProperty<Region> getRegion() {
        return region;
    }

    public void setRegion(SimulationProperty<Region> region) {
        this.region = region;
    }

    public SimulationProperty<LateralMarkColor> getColor() {
        return color;
    }

    public void setColor(SimulationProperty<LateralMarkColor> color) {
        this.color = color;
    }

    public SimulationProperty<LateralMarkShape> getShape() {
        return shape;
    }

    public void setShape(SimulationProperty<LateralMarkShape> shape) {
        this.shape = shape;
    }

    public LightSignal getLightSignal() {
        return lightSignal;
    }

    public void setLightSignal(LightSignal lightSignal) {
        this.lightSignal = lightSignal;
    }
}
