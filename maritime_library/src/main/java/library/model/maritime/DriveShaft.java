package library.model.maritime;

import library.model.simulation.SimulationComponent;
import library.model.simulation.SimulationProperty;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DriveShaft extends SimulationComponent {

    SimulationProperty<Double> shaftLength;

    private DriveShaft() {
        super();
    }

    public DriveShaft(double timeStepSize,
                      SimulationComponent superComponent) {
        super(timeStepSize,
              superComponent);
    }

    public DriveShaft(double timeStepSize,
                      SimulationComponent superComponent,
                      SimulationProperty<Double> shaftLength) {
        super(timeStepSize, superComponent);
        this.shaftLength = shaftLength;
    }

    public DriveShaft(SimulationProperty<Double> shaftLength) {
        super();
        this.shaftLength = shaftLength;
    }

    public SimulationProperty<Double> getShaftLength() {
        return shaftLength;
    }

    public void setShaftLength(SimulationProperty<Double> shaftLength) {
        this.shaftLength = shaftLength;
    }

}
