package library.model.maritime;

import library.model.simulation.Position;
import library.model.simulation.SimulationProperty;
import library.model.simulation.units.NoUnit;
import library.model.traffic.Obstacle;
import org.locationtech.jts.geom.Geometry;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a Mole Light (Molenfeuer) - Requirement F-3.9.
 * <p>
 * A Mole Light is a fixed aid to navigation placed at the head of a breakwater (Mole).
 * </p>
 */
@XmlRootElement
public class MoleLight extends Obstacle {

    /**
     * The identifier/name of this mark (e.g. "Mole North").
     * Defined explicitly here as superclasses do not provide a name property.
     */
    @XmlElement
    private SimulationProperty<String> name;

    @XmlElement
    private LateralRestriction lateralCharacteristics;

    /**
     * Default constructor for JAXB.
     */
    public MoleLight() {
        super();
    }

    /**
     * Creates a new Mole Light (Molenfeuer).
     *
     * @param nameStr The identifier of the light (e.g., "Mole North Head").
     * @param position The fixed geographic position.
     * @param geometry The physical shape (e.g., the tower geometry).
     * @param markType The lateral type (e.g., PORT_HAND for the left side of the entrance).
     * @param region The IALA region (A or B).
     */
    public MoleLight(String nameStr, Position position, Geometry geometry, LateralMarkType markType, LateralRegion region) {
        super(true, position, geometry, 0.0);

        // Initialize the name property locally
        this.name = new SimulationProperty<>(false, false, NoUnit.get(), nameStr, "name");

        // Apply the lateral rules (F-3.3)
        this.lateralCharacteristics = new LateralRestriction(markType, region, nameStr);
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

    public LateralMarkColor getLightColor() {
        if (lateralCharacteristics != null && lateralCharacteristics.getColor() != null) {
            return lateralCharacteristics.getColor().getValue();
        }
        return null;
    }
}