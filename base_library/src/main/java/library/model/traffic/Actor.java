package library.model.traffic;

import library.model.simulation.SimulationComponent;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Actor extends SimulationComponent {

    protected Actor() {
        super();
    }

    public Actor(double timeStepSize, SimulationComponent parent) {
        super(timeStepSize, parent);
    }

}
