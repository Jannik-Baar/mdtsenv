package library.model.traffic;

import library.model.simulation.SimulationComponent;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * A Sensor Component
 */
@XmlRootElement
public abstract class Sensor extends SimulationComponent {

    protected Sensor() {
        super();
    }

    public Sensor(double timeStepSize,
                  SimulationComponent parent) {
        super(timeStepSize, parent);
    }

}
