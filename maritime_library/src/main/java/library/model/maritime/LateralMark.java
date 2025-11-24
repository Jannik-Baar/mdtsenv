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

    @XmlElement
    private LateralRestriction lateralCharacteristics;

    /**
     * Default constructor for JAXB.
     */
    public LateralMark() {
        super();
    }

    /**
     * Creates a new Lateral Mark.
     *
     * @param nameStr The identifier of the mark (e.g., "Elbe 1").
     * @param position The geographic position.
     * @param geometry The physical shape (collision body).
     * @param markType The lateral type (e.g. STARBOARD_HAND).
     * @param region The IALA region (A or B).
     * @param number The displayed number/string on the mark (e.g. "1").
     */
    public LateralMark(String nameStr, Position position, Geometry geometry,
                       LateralMarkType markType, LateralRegion region, String number) {
        // A Lateral Mark is always physical (true)
        super(true, position, geometry, 0.0);

        // Initialize the name property locally
        this.name = new SimulationProperty<>(false, false, NoUnit.get(), nameStr, "name");

        // Create and assign the restriction component
        this.lateralCharacteristics = new LateralRestriction(markType, region, number);
    }

    public LateralRestriction getLateralCharacteristics() {
        return lateralCharacteristics;
    }

    public void setLateralCharacteristics(LateralRestriction lateralCharacteristics) {
        this.lateralCharacteristics = lateralCharacteristics;
    }

    public SimulationProperty<String> getName() {
        return name;
    }

    public void setName(SimulationProperty<String> name) {
        this.name = name;
    }

    public LateralMarkColor getIalaColor() {
        if (lateralCharacteristics != null && lateralCharacteristics.getColor() != null) {
            return lateralCharacteristics.getColor().getValue();
        }
        return null;
    }
}