package library.model.maritime;

import library.model.simulation.SimulationComponent;
import library.model.traffic.Sensor;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * A Sensor for sensing humidity, not implemented yet!
 */
@XmlRootElement
public class HumiditySensor extends Sensor {

    protected HumiditySensor() {
        super();
    }

    public HumiditySensor(double timeStepSize, SimulationComponent superComponent) {
        super(timeStepSize, superComponent);
    }
}
