package library.model.maritime;

import library.model.simulation.Position;
import library.model.simulation.SimulationProperty;
import library.model.simulation.units.RotationUnit;
import library.model.simulation.units.TimeUnit;
import library.model.traffic.Infrastructure;
import library.model.traffic.PossibleDomains;
import org.locationtech.jts.geom.Geometry;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

@XmlRootElement
public class Waterbody extends Infrastructure {

    @XmlElement
    private SimulationProperty<Double> waveHeight;
    @XmlElement
    private SimulationProperty<Double> waveDirection;

    public Waterbody() {

    }

    public Waterbody(Position position,
                     Geometry form,
                     double rotation,
                     double waveHeight,
                     double waveDirection,
                     boolean physical) {
        super(physical, position, form, rotation);
        this.waveHeight = new SimulationProperty<>(false, false, TimeUnit.SECOND, waveHeight, "waveHeight");
        this.waveDirection = new SimulationProperty<>(false, false, RotationUnit.DEGREE, waveDirection, "waveDirection");
    }

    public Waterbody(SimulationProperty<Boolean> physical,
                     SimulationProperty<Position> position,
                     SimulationProperty<Geometry> form,
                     SimulationProperty<Double> rotation,
                     SimulationProperty<Double> waveHeight,
                     SimulationProperty<Double> waveDirection) {
        super(physical, position, form, rotation);
        this.waveHeight = waveHeight;
        this.waveDirection = waveDirection;
    }

    public Waterbody(boolean physical,
                     Position position,
                     Geometry form,
                     double rotation,
                     ArrayList<PossibleDomains> possibleDomains,
                     double waveHeight,
                     double waveDirection) {
        super(physical, position, form, rotation, possibleDomains);
        this.waveHeight = new SimulationProperty<>(false, false, TimeUnit.SECOND, waveHeight, "waveHeight");
        this.waveDirection = new SimulationProperty<>(false, false, RotationUnit.DEGREE, waveDirection, "waveDirection");
    }

    public Waterbody(SimulationProperty<Boolean> physical,
                     SimulationProperty<Position> position,
                     SimulationProperty<Geometry> form,
                     SimulationProperty<Double> rotation,
                     ArrayList<PossibleDomains> possibleDomains,
                     SimulationProperty<Double> waveHeight,
                     SimulationProperty<Double> waveDirection) {
        super(physical, position, form, rotation, possibleDomains);
        this.waveHeight = waveHeight;
        this.waveDirection = waveDirection;
    }

    public SimulationProperty<Double> getWaveHeight() {
        return waveHeight;
    }

    public SimulationProperty<Double> getWaveDirection() {
        return waveDirection;
    }
}
