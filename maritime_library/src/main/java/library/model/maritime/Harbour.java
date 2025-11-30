package library.model.maritime;

import library.model.simulation.Position;
import library.model.simulation.SimulationProperty;
import library.model.simulation.units.NoUnit;
import library.model.traffic.Infrastructure;
import library.model.traffic.PossibleDomains;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Geometry;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

/**
 * A Harbour Infrastructure Object.
 */
@Getter
@Setter
@XmlRootElement
public class Harbour extends Infrastructure {

    /** Maximum number of vessels that can be in the harbour */
    @XmlElement
    private SimulationProperty<Integer> maxCapacity;

    /** Current number of vessels in the harbour */
    @XmlElement
    private SimulationProperty<Integer> usedCapacity;

    protected Harbour() {

    }

    public Harbour(Position position,
                   Geometry form,
                   double rotation,
                   boolean physical,
                   int maxCapacity,
                   int usedCapacity) {
        super(physical, position, form, rotation);
        this.maxCapacity = new SimulationProperty<>(false, false, NoUnit.get(), maxCapacity, "maxCapacity");
        this.usedCapacity = new SimulationProperty<>(false, false, NoUnit.get(), usedCapacity, "usedCapacity");
    }

    public Harbour(SimulationProperty<Boolean> physical,
                   SimulationProperty<Position> position,
                   SimulationProperty<Geometry> form,
                   SimulationProperty<Double> rotation,
                   SimulationProperty<Integer> maxCapacity,
                   SimulationProperty<Integer> usedCapacity) {
        super(physical, position, form, rotation);
        this.maxCapacity = maxCapacity;
        this.usedCapacity = usedCapacity;
    }

    public Harbour(boolean physical,
                   Position position,
                   Geometry form,
                   double rotation,
                   ArrayList<PossibleDomains> possibleDomains,
                   int maxCapacity,
                   int usedCapacity) {
        super(physical, position, form, rotation, possibleDomains);
        this.maxCapacity = new SimulationProperty<>(false, false, NoUnit.get(), maxCapacity, "maxCapacity");
        this.usedCapacity = new SimulationProperty<>(false, false, NoUnit.get(), usedCapacity, "usedCapacity");
    }

    public Harbour(SimulationProperty<Boolean> physical,
                   SimulationProperty<Position> position,
                   SimulationProperty<Geometry> form,
                   SimulationProperty<Double> rotation,
                   ArrayList<PossibleDomains> possibleDomains,
                   SimulationProperty<Integer> maxCapacity,
                   SimulationProperty<Integer> usedCapacity) {
        super(physical, position, form, rotation, possibleDomains);
        this.maxCapacity = maxCapacity;
        this.usedCapacity = usedCapacity;
    }
}
