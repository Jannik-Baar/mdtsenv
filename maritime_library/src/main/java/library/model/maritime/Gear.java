package library.model.maritime;

import library.model.simulation.SimulationComponent;
import library.model.traffic.Actor;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Gear extends Actor {

    protected Gear() {
        super();
    }

    public Gear(double timeStepSize,
                SimulationComponent superComponent) {
        super(timeStepSize, superComponent);
    }
}
