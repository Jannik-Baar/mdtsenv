package library.model.maritime;

import library.model.simulation.SimulationComponent;
import library.model.simulation.objects.SimulationObject;

/**
 * A basic Engine Component
 */
public class Engine extends SimulationComponent {

    protected Engine() {
        super();
    }

    public Engine(double timeStepSize,
                  SimulationObject parent) {
        super(timeStepSize, parent);
    }

}
