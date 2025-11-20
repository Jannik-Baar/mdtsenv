package library.model.simulation;

import library.model.dto.scenario.SystemUnderTestConfig;
import library.model.simulation.objects.SimulationObject;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Behaviour Container that holds the goals and the name of the Behaviours.
 * This class is used to serialize a Behaviour. Every Behaviour should inherit this class.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public abstract class Behaviour extends SimulationSuperClass implements IBehaviour {

    /**
     * List of Goals for a specific Behaviour
     */
    @XmlElementWrapper
    @XmlElement(name = "goal")
    private List<Goal> goals = new ArrayList<>();

    @XmlElement
    private boolean isSystemUnderTest = false;

    @XmlElement
    private SystemUnderTestConfig systemUnderTestConfig = new SystemUnderTestConfig();

    public Behaviour(List<Goal> goals) {
        super();
        this.goals.addAll(goals);
    }

    public Behaviour() {
        // empty constructor
    }

    @Override
    public abstract Map<String, Object> nextStep(double timePassed);

    @Override
    public abstract void setSimulationObject(SimulationObject simulationObject);

    /**
     * Specifies whether the behaviour shall be used for a system under test
     *
     * @param isSystemUnderTest
     */
    public void setSystemUnderTest(boolean isSystemUnderTest) {
        this.isSystemUnderTest = isSystemUnderTest;
    }

    public void setGoals(ArrayList<Goal> goals) {
        this.goals = goals;
    }

    public void addGoal(Goal goal) {
        this.goals.add(goal);
    }

    @Override
    public List<Goal> getGoals() {
        return goals;
    }

    public SystemUnderTestConfig getSystemUnderTestConfig() {
        return systemUnderTestConfig;
    }

    public void setSystemUnderTestConfig(SystemUnderTestConfig systemUnderTestConfig) {
        this.systemUnderTestConfig = systemUnderTestConfig;
    }

    public void setSystemUnderTestConfigValues(String address, int port, boolean isWebsocket) {
        this.systemUnderTestConfig.setAddress(address);
        this.systemUnderTestConfig.setPort(port);
        this.systemUnderTestConfig.setWebsocket(isWebsocket);
    }
}
