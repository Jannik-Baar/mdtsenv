package library.model.simulation;

import library.model.simulation.objects.SimulationObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface IBehaviour {

    Map<String, Object> nextStep(double timePassed);

    void setGoals(ArrayList<Goal> goals);

    void addGoal(Goal goal);

    List<Goal> getGoals();

    void setSimulationObject(SimulationObject simulationObject);

}
