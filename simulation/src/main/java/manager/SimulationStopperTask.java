package manager;

import java.util.TimerTask;

/**
 * A TimerTask for stopping a Simulation that has a time limited Scenario.
 */
public class SimulationStopperTask extends TimerTask {

    private final SimulationManager simulationManager;
    private final long simulationTime;

    public SimulationStopperTask(SimulationManager simulationManager, long simulationTime) {
        this.simulationManager = simulationManager;
        this.simulationTime = simulationTime;
    }

    @Override
    public void run() {
        System.out.println(String.format("Simulation time of %d ms has run out and the current Simulation will end.", simulationTime));
        simulationManager.stopSimulation(false);
    }
}
