package library.model.traffic;

import library.model.simulation.Position;
import library.model.simulation.objects.SimulationObject;
import library.model.simulation.SimulationProperty;
import org.locationtech.jts.geom.Geometry;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * An Obstacle Simulation Object
 */
@XmlRootElement
public class Obstacle extends SimulationObject {

    public Obstacle() {

    }

    public Obstacle(boolean physical,
                    Position position,
                    Geometry form,
                    double rotation) {
        super(physical, position, form, rotation);
    }

    public Obstacle(SimulationProperty<Boolean> physical,
                    SimulationProperty<Position> position,
                    SimulationProperty<Geometry> form,
                    SimulationProperty<Double> rotation) {
        super(physical, position, form, rotation);
    }
}
