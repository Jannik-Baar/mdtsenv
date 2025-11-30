package library.model.maritime;

import library.model.simulation.Position;
import library.model.simulation.SimulationProperty;
import library.model.simulation.units.NoUnit;
import library.model.traffic.Obstacle;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Geometry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a Beacon
 */
@Getter
@Setter
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Beacon extends Obstacle {

    /**
     * The identifier/name of this beacon (e.g. "North Pier Beacon").
     */
    @XmlElement
    private SimulationProperty<String> name;

    /**
     * The functional type of the beacon (e.g., LATERAL_PORT, CARDINAL_NORTH, LEADING_LINE).
     */
    @XmlElement
    private SimulationProperty<BeaconType> beaconType;

    /**
     * The physical shape of the structure (e.g., TOWER, LATTICE, POLE).
     */
    @XmlElement
    private SimulationProperty<BeaconShape> shape;

    /**
     * The color or pattern of the beacon.
     */
    @XmlElement
    private SimulationProperty<BeaconColor> color;

    /**
     * The IALA region (A or B). Relevant for interpreting lateral colors.
     */
    @XmlElement
    private SimulationProperty<Region> region;

    /**
     * The light signal characteristics (can be null if unlit).
     */
    @XmlElement
    private LightSignal lightSignal;

    /**
     * Default constructor for JAXB.
     */
    public Beacon() {
        super();
    }

    public Beacon(String nameStr, Position position, Geometry geometry,
                  BeaconType beaconType, BeaconShape shape, BeaconColor color,
                  Region region, LightSignal lightSignal) {
        super(true, position, geometry, 0.0);

        this.name = new SimulationProperty<>(false, false, NoUnit.get(), nameStr, "name");
        this.beaconType = new SimulationProperty<>(false, false, NoUnit.get(), beaconType, "beaconType");
        this.shape = new SimulationProperty<>(false, false, NoUnit.get(), shape, "shape");
        this.color = new SimulationProperty<>(false, false, NoUnit.get(), color, "color");
        this.region = new SimulationProperty<>(false, false, NoUnit.get(), region, "region");
        this.lightSignal = lightSignal;
    }
}
