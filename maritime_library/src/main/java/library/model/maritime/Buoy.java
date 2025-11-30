package library.model.maritime;

import library.model.simulation.Position;
import library.model.simulation.SimulationProperty;
import library.model.traffic.Obstacle;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Geometry;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * A basic Buoy obstacle.
 */
@Getter
@Setter
@XmlRootElement
public class Buoy extends Obstacle {

    public Buoy() {

    }

    public Buoy(boolean physical,
                Position position,
                Geometry form,
                double rotation) {
        super(physical, position, form, rotation);
    }

    public Buoy(SimulationProperty<Boolean> physical,
                SimulationProperty<Position> position,
                SimulationProperty<Geometry> form,
                SimulationProperty<Double> rotation) {
        super(physical, position, form, rotation);
    }
}
