package library.model.maritime;

import library.model.simulation.Position;
import library.model.simulation.SimulationProperty;
import library.model.simulation.units.NoUnit;
import library.model.traffic.Obstacle;
import org.locationtech.jts.geom.Geometry;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a Safe Water Mark (Mitte-Fahrwasser-Zeichen) - Requirement F-3.2.
 * <p>
 * A Safe Water Mark indicates that there is navigable water all around the mark.
 * It is used to mark mid-channels, fairway centers, and landfall marks.
 * </p>
 * <p>
 * According to IALA standards:
 * - Color: Red and white vertical stripes
 * - Shape: Spherical, pillar or spar with a single red spherical top mark
 * - Light (if fitted): White, with a specific rhythm (e.g., Morse "A" or long flash every 10 seconds)
 * </p>
 */
@XmlRootElement
public class SafeWaterMark extends Obstacle {

    /**
     * The identifier/name of this mark (e.g. "Elbe Approach").
     * Defined explicitly here as superclasses do not provide a name property.
     */
    @XmlElement
    private SimulationProperty<String> name;

    /**
     * The color pattern of the safe water mark.
     * Always RED_WHITE_VERTICAL_STRIPES for IALA compliant marks.
     */
    @XmlElement
    private SimulationProperty<SafeWaterMarkColor> color;

    /**
     * The shape of the safe water mark.
     * Can be SPHERICAL, PILLAR, or SPAR.
     */
    @XmlElement
    private SimulationProperty<SafeWaterMarkShape> shape;

    /**
     * The marking text or identifier displayed on the mark (e.g., "E1").
     */
    @XmlElement
    private SimulationProperty<String> marking;

    /**
     * Indicates whether the mark is fitted with a light.
     */
    @XmlElement
    private SimulationProperty<Boolean> isLit;

    /**
     * Default constructor for JAXB.
     */
    public SafeWaterMark() {
        super();
    }

    /**
     * Creates a new Safe Water Mark (Mitte-Fahrwasser-Zeichen).
     *
     * @param nameStr The identifier of the mark (e.g., "Elbe Approach").
     * @param position The geographic position.
     * @param geometry The physical shape (collision body).
     * @param shape The shape of the mark (SPHERICAL, PILLAR, or SPAR).
     * @param marking The marking text or identifier on the mark.
     * @param isLit Whether the mark is fitted with a light.
     */
    public SafeWaterMark(String nameStr, Position position, Geometry geometry,
                         SafeWaterMarkShape shape, String marking, boolean isLit) {
        // A Safe Water Mark is always physical (true)
        super(true, position, geometry, 0.0);

        // Initialize the name property
        this.name = new SimulationProperty<>(false, false, NoUnit.get(), nameStr, "name");

        // Set standard IALA color (red and white vertical stripes)
        this.color = new SimulationProperty<>(false, false, NoUnit.get(),
                SafeWaterMarkColor.RED_WHITE_VERTICAL_STRIPES, "color");

        // Set the shape
        this.shape = new SimulationProperty<>(false, false, NoUnit.get(), shape, "shape");

        // Set the marking
        this.marking = new SimulationProperty<>(false, false, NoUnit.get(), marking, "marking");

        // Set whether it's lit
        this.isLit = new SimulationProperty<>(false, false, NoUnit.get(), isLit, "isLit");
    }

    // Getters and Setters

    public SimulationProperty<String> getName() {
        return name;
    }

    public void setName(SimulationProperty<String> name) {
        this.name = name;
    }

    public SimulationProperty<SafeWaterMarkColor> getColor() {
        return color;
    }

    public void setColor(SimulationProperty<SafeWaterMarkColor> color) {
        this.color = color;
    }

    public SimulationProperty<SafeWaterMarkShape> getShape() {
        return shape;
    }

    public void setShape(SimulationProperty<SafeWaterMarkShape> shape) {
        this.shape = shape;
    }

    public SimulationProperty<String> getMarking() {
        return marking;
    }

    public void setMarking(SimulationProperty<String> marking) {
        this.marking = marking;
    }

    public SimulationProperty<Boolean> getIsLit() {
        return isLit;
    }

    public void setIsLit(SimulationProperty<Boolean> isLit) {
        this.isLit = isLit;
    }
}