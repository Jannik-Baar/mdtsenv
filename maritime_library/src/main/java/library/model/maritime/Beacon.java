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
 * A Beacon is a fixed aid to navigation (AtoN). Unlike buoys, beacons rely on a fixed foundation.
 * They can function as Lateral, Cardinal, Leading Lines, or other marks.
 * </p>
 */
@XmlRootElement
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

    /**
     * Creates a new Beacon.
     *
     * @param nameStr     The identifier of the beacon.
     * @param position    The fixed geographic position.
     * @param geometry    The physical geometry/collision body.
     * @param beaconType  The functional type (e.g. LATERAL_PORT).
     * @param shape       The physical structure (e.g. TOWER).
     * @param color       The color/pattern of the structure.
     * @param region      The IALA region.
     * @param lightSignal The light signal (optional).
     */
    public Beacon(String nameStr, Position position, Geometry geometry,
                  BeaconType beaconType, BeaconShape shape, BeaconColor color,
                  Region region, LightSignal lightSignal) {
        // Beacons are fixed obstacles (isStatic = true)
        super(true, position, geometry, 0.0);

        this.name = new SimulationProperty<>(false, false, NoUnit.get(), nameStr, "name");
        this.beaconType = new SimulationProperty<>(false, false, NoUnit.get(), beaconType, "beaconType");
        this.shape = new SimulationProperty<>(false, false, NoUnit.get(), shape, "shape");
        this.color = new SimulationProperty<>(false, false, NoUnit.get(), color, "color");
        this.region = new SimulationProperty<>(false, false, NoUnit.get(), region, "region");
        this.lightSignal = lightSignal;
    }

    // Getters and Setters

    public SimulationProperty<String> getName() {
        return name;
    }

    public void setName(SimulationProperty<String> name) {
        this.name = name;
    }

    public SimulationProperty<BeaconType> getBeaconType() {
        return beaconType;
    }

    public void setBeaconType(SimulationProperty<BeaconType> beaconType) {
        this.beaconType = beaconType;
    }

    public SimulationProperty<BeaconShape> getShape() {
        return shape;
    }

    public void setShape(SimulationProperty<BeaconShape> shape) {
        this.shape = shape;
    }

    public SimulationProperty<BeaconColor> getColor() {
        return color;
    }

    public void setColor(SimulationProperty<BeaconColor> color) {
        this.color = color;
    }

    public SimulationProperty<Region> getRegion() {
        return region;
    }

    public void setRegion(SimulationProperty<Region> region) {
        this.region = region;
    }

    public LightSignal getLightSignal() {
        return lightSignal;
    }

    public void setLightSignal(LightSignal lightSignal) {
        this.lightSignal = lightSignal;
    }
}