package library.model.maritime;

import library.model.simulation.Position;
import library.model.simulation.SimulationComponent;
import library.model.simulation.objects.SimulationObject;
import library.model.traffic.Sensor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Sensor that exposes the parent simulation object's current position as GPS coordinates.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class GpsSensor extends Sensor {

    public GpsSensor() {
        super();
    }

    public GpsSensor(double timeStepSize, SimulationComponent parent) {
        super(timeStepSize, parent);
    }

    public GpsSensor(double timeStepSize, SimulationObject parent) {
        super(timeStepSize, (SimulationComponent) parent);
    }

    /**
     * Returns the latest reading as a Position instance (longitude, latitude, altitude).
     * Refreshes the reading by pulling the parent's current position.
     */
    public Position getGpsPosition() {
        SimulationObject parent = getParent();
        if (parent == null || parent.getPosition() == null || parent.getPosition().getValue() == null) {
            return null;
        }
        return parent.getPosition().getValue();
    }
}


