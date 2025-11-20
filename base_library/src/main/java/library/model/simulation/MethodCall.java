package library.model.simulation;

import java.util.ArrayList;
import java.util.Objects;

public class MethodCall extends ExternalBehavioralFeature {

    private ArrayList<Goal> goals;
    private ArrayList<SimulationProperty> methodParameters;
    private Identifier path;

    public MethodCall() {
    }

    public MethodCall(ArrayList<Goal> goals, ArrayList<SimulationProperty> methodParameters, Identifier path) {
        this.goals = goals;
        this.methodParameters = methodParameters;
        this.path = path;
    }

    public ArrayList<Goal> getGoals() {
        return goals;
    }

    public void setGoals(ArrayList<Goal> goals) {
        this.goals = goals;
    }

    public ArrayList<SimulationProperty> getMethodParameters() {
        return methodParameters;
    }

    public void setMethodParameters(ArrayList<SimulationProperty> methodParameters) {
        this.methodParameters = methodParameters;
    }

    public Identifier getPath() {
        return path;
    }

    public void setPath(Identifier path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodCall that = (MethodCall) o;
        return Objects.equals(goals, that.goals) && Objects.equals(methodParameters, that.methodParameters) && Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(goals, methodParameters, path);
    }
}
