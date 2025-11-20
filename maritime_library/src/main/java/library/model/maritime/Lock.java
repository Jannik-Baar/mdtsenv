package library.model.maritime;

import library.model.simulation.Behaviour;
import library.model.simulation.Position;
import library.model.simulation.SimulationProperty;
import library.model.simulation.units.TimeUnit;
import library.model.traffic.Infrastructure;
import library.model.traffic.PossibleDomains;
import org.locationtech.jts.geom.Geometry;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

/**
 * A lock
 */
@XmlRootElement
public class Lock extends Infrastructure {

    @XmlElement
    private Behaviour behaviour;

    @XmlElement
    private SimulationProperty<Double> timeToFill;

    @XmlElement
    private SimulationProperty<Double> timeToDrain;

    public Lock() {

    }

    public Lock(boolean physical,
                Position position,
                Geometry form,
                double rotation,
                double timeToFill,
                double timeToDrain) {
        super(physical, position, form, rotation);
        this.timeToFill = new SimulationProperty<>(false, false, TimeUnit.SECOND, timeToFill, "timeToFill");
        this.timeToDrain = new SimulationProperty<>(false, false, TimeUnit.SECOND, timeToDrain, "timeToDrain");
    }

    public Lock(SimulationProperty<Boolean> physical,
                SimulationProperty<Position> position,
                SimulationProperty<Geometry> form,
                SimulationProperty<Double> rotation,
                SimulationProperty<Double> timeToFill,
                SimulationProperty<Double> timeToDrain) {
        super(physical, position, form, rotation);
        this.timeToFill = timeToFill;
        this.timeToDrain = timeToDrain;
    }

    public Lock(boolean physical,
                Position position,
                Geometry form,
                double rotation,
                ArrayList<PossibleDomains> possibleDomains,
                double timeToFill,
                double timeToDrain) {
        super(physical, position, form, rotation, possibleDomains);
        this.timeToFill = new SimulationProperty<>(false, false, TimeUnit.SECOND, timeToFill, "timeToFill");
        this.timeToDrain = new SimulationProperty<>(false, false, TimeUnit.SECOND, timeToDrain, "timeToDrain");
    }

    public Lock(SimulationProperty<Boolean> physical,
                SimulationProperty<Position> position,
                SimulationProperty<Geometry> form,
                SimulationProperty<Double> rotation,
                ArrayList<PossibleDomains> possibleDomains,
                SimulationProperty<Double> timeToFill,
                SimulationProperty<Double> timeToDrain) {
        super(physical, position, form, rotation, possibleDomains);
        this.timeToFill = timeToFill;
        this.timeToDrain = timeToDrain;
    }

    public SimulationProperty<Double> getTimeToFill() {
        return timeToFill;
    }

    public SimulationProperty<Double> getTimeToDrain() {
        return timeToDrain;
    }

}
