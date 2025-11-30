package library.model.maritime;

import library.model.simulation.Position;
import library.model.simulation.SimulationProperty;
import library.model.traffic.Infrastructure;
import library.model.traffic.PossibleDomains;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Geometry;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

/**
 * Represents a maritime navigation channel that provides a defined waterway for vessel traffic.
 */
@Getter
@Setter
@XmlRootElement
public class Channel extends Infrastructure {
    
    /** List of beacons in this channel */
    @XmlElementWrapper(name = "beacons")
    @XmlElement(name = "beacon")
    private ArrayList<Beacon> beacons = new ArrayList<>();
    
    /** List of lateral marks in this channel */
    @XmlElementWrapper(name = "lateralMarks")
    @XmlElement(name = "lateralMark")
    private ArrayList<LateralMark> lateralMarks = new ArrayList<>();
    
    /** List of lighthouses in this channel */
    @XmlElementWrapper(name = "lighthouses")
    @XmlElement(name = "lighthouse")
    private ArrayList<Lighthouse> lighthouses = new ArrayList<>();
    
    /** List of safe water marks in this channel */
    @XmlElementWrapper(name = "safeWaterMarks")
    @XmlElement(name = "safeWaterMark")
    private ArrayList<SafeWaterMark> safeWaterMarks = new ArrayList<>();
    
    /** Whether this is a preferred channel */
    @XmlElement
    private SimulationProperty<Boolean> preferred;
    
    public Channel() {
    }

    public Channel(boolean physical, Position position, Geometry form, double rotation) {
        super(physical, position, form, rotation);
        this.beacons = new ArrayList<>();
        this.lateralMarks = new ArrayList<>();
        this.lighthouses = new ArrayList<>();
        this.safeWaterMarks = new ArrayList<>();
    }

    public Channel(SimulationProperty<Boolean> physical, SimulationProperty<Position> position, SimulationProperty<Geometry> form, SimulationProperty<Double> rotation) {
        super(physical, position, form, rotation);
        this.beacons = new ArrayList<>();
        this.lateralMarks = new ArrayList<>();
        this.lighthouses = new ArrayList<>();
        this.safeWaterMarks = new ArrayList<>();
    }

    public Channel(boolean physical, Position position, Geometry form, double rotation, ArrayList<PossibleDomains> possibleDomains) {
        super(physical, position, form, rotation, possibleDomains);
        this.beacons = new ArrayList<>();
        this.lateralMarks = new ArrayList<>();
        this.lighthouses = new ArrayList<>();
        this.safeWaterMarks = new ArrayList<>();
    }

    public Channel(SimulationProperty<Boolean> physical, SimulationProperty<Position> position, SimulationProperty<Geometry> form, SimulationProperty<Double> rotation, ArrayList<PossibleDomains> possibleDomains) {
        super(physical, position, form, rotation, possibleDomains);
        this.beacons = new ArrayList<>();
        this.lateralMarks = new ArrayList<>();
        this.lighthouses = new ArrayList<>();
        this.safeWaterMarks = new ArrayList<>();
    }
}
