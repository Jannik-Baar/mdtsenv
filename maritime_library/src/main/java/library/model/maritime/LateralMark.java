package library.model.maritime;

import library.model.simulation.Position;
import library.model.simulation.SimulationProperty;
import library.model.simulation.units.NoUnit;
import library.model.traffic.Obstacle;
import org.locationtech.jts.geom.Geometry;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a Lateral Mark (Lateralzeichen) - Requirement F-3.3.
 * <p>
 * A Lateral Mark is an {@link Obstacle} used to indicate the port or starboard side
 * of the route to be followed.
 * </p>
 */
@XmlRootElement
public class LateralMark extends Obstacle {

    /**
     * The identifier/name of this mark (e.g. "Elbe 1").
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
     * The number/identifier displayed on the mark.
     */
    @XmlElement
    private SimulationProperty<String> number;

    /**
     * The color of the mark (derived from type and region).
     */
    @XmlElement
    private SimulationProperty<LateralMarkColor> color;

    /**
     * The shape of the mark.
     */
    @XmlElement
    private SimulationProperty<LateralMarkShape> shape;

    /**
     * The light signal characteristics (can be null if not lit).
     */
    @XmlElement
    private LightSignal lightSignal;

    /**
     * Default constructor for JAXB.
     */
    public LateralMark() {
        super();
    }

    /**
     * Creates a new Lateral Mark.
     *
     * @param nameStr     The identifier of the mark (e.g., "Elbe 1").
     * @param position    The geographic position.
     * @param geometry    The physical shape (collision body).
     * @param markType    The lateral type (e.g. STARBOARD_HAND).
     * @param region      The IALA region (A or B).
     * @param number      The displayed number/string on the mark (e.g. "1").
     * @param lightSignal The light signal characteristics (can be null if not lit).
     * @param shape       The explicit shape of the mark (e.g. PILLAR, SPAR). If null, the standard shape (CAN/CONE) is derived.
     */
    public LateralMark(String nameStr, Position position, Geometry geometry,
                       LateralMarkType markType, Region region, String number, LightSignal lightSignal, LateralMarkShape shape) {
        // A Lateral Mark is always physical (true)
        super(true, position, geometry, 0.0);

        // Initialize the name property
        this.name = new SimulationProperty<>(false, false, NoUnit.get(), nameStr, "name");

        // Set mark type
        this.markType = new SimulationProperty<>(false, false, NoUnit.get(), markType, "markType");

        // Set region
        this.region = new SimulationProperty<>(false, false, NoUnit.get(), region, "region");

        // Set number
        this.number = new SimulationProperty<>(false, false, NoUnit.get(), number, "number");

        // Derive and set color and shape based on IALA rules and user input
        applyStandardAppearance(markType, region, shape);

        // Set the light signal
        this.lightSignal = lightSignal;
    }

    /**
     * Applies standard IALA colors and shapes based on type and region.
     * If a specific shape is provided (e.g., PILLAR or SPAR), it takes precedence over the default CAN/CONE logic.
     */
    private void applyStandardAppearance(LateralMarkType type, Region region, LateralMarkShape requestedShape) {
        LateralMarkColor derivedColor = LateralMarkColor.RED; // Default fallback
        LateralMarkShape derivedShape = LateralMarkShape.CAN; // Default fallback

        // Determine Color and Default Shape based on Region
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

        // Use requested shape if provided (e.g. PILLAR or SPAR), otherwise use derived standard shape
        LateralMarkShape finalShape = (requestedShape != null) ? requestedShape : derivedShape;

        this.color = new SimulationProperty<>(false, false, NoUnit.get(), derivedColor, "color");
        this.shape = new SimulationProperty<>(false, false, NoUnit.get(), finalShape, "shape");
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

    public SimulationProperty<String> getNumber() {
        return number;
    }

    public void setNumber(SimulationProperty<String> number) {
        this.number = number;
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