package library.model.dto.scenario;

import library.model.dto.conditions.TerminationCondition;
import library.model.dto.observer.Observer;
import library.model.simulation.objects.SimulationObject;
import library.services.logging.LoggingType;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class holds a list of simulation objects and is used to generate the scenario via JAXB.
 * The generated scenario can then be used to generate the simulation model.
 * It also holds specific information about the configuration of the simulation.
 */
@XmlRootElement(name = "scenario")
@XmlAccessorType(XmlAccessType.NONE)
public class ScenarioDTO implements Serializable {

    private boolean timeLimited;
    private long maxDuration;

    private boolean iterationsLimited;
    private int maxIterations;

    @XmlElement(name = "library")
    private String library = "";

    @XmlElementWrapper
    @XmlElement(name = "simulationObject")
    private ArrayList<SimulationObject> simulationObjects = new ArrayList<>();

    @XmlElementWrapper
    @XmlElement(name = "observer")
    private ArrayList<Observer> observers = new ArrayList<>();

    @XmlElementWrapper(name = "loggingTypes")
    private ArrayList<LoggingType> loggingTypes = new ArrayList<>();

    @XmlElementWrapper(name = "terminationConditions")
    private ArrayList<TerminationCondition<?>> terminationCondition = new ArrayList<>();

    public ScenarioDTO() {
        this.timeLimited = false;
        this.iterationsLimited = false;
        this.maxDuration = -1;
        this.maxIterations = -1;
    }

    public void addSimulationObject(SimulationObject simulationObject) {
        this.simulationObjects.add(simulationObject);
    }

    public void removeSimulationObject(SimulationObject simulationObject) {
        this.simulationObjects.remove(simulationObject);
    }

    public ArrayList<SimulationObject> getSimulationObjects() {
        return simulationObjects;
    }

    public void addLoggingType(LoggingType type) {
        this.loggingTypes.add(type);
    }

    public void removeLoggingType(LoggingType type) {
        this.loggingTypes.remove(type);
    }

    public ArrayList<LoggingType> getLoggingTypes() {
        return loggingTypes;
    }

    public void addTerminationCondition(TerminationCondition terminationCondition) {
        this.terminationCondition.add(terminationCondition);
    }

    public void removeTerminationCondition(TerminationCondition terminationCondition) {
        this.terminationCondition.remove(terminationCondition);
    }

    public void addObserver(Observer observer) {
        this.observers.add(observer);
    }

    public void removerObserver(Observer observer) {
        this.observers.remove(observer);
    }

    public ArrayList<TerminationCondition<?>> getTerminationConditions() {
        return terminationCondition;
    }

    public boolean isTimeLimited() {
        return timeLimited;
    }

    public boolean isStepsLimited() {
        return iterationsLimited;
    }

    public int getSimulationIterations() {
        return maxIterations;
    }

    public void setSimulationIterations(int simulationIterations) {
        this.iterationsLimited = simulationIterations >= 0;
        this.maxIterations = simulationIterations;
    }

    public long getMaxDuration() {
        return maxDuration;
    }

    public void setMaxDuration(long maxDuration) {
        this.timeLimited = maxDuration >= 0;
        this.maxDuration = maxDuration;
    }

    public SimulationObject getSimulationObjectById(String id) {
        for (SimulationObject so : this.simulationObjects) {
            if (so.getId().equals(id)) {
                return so;
            }
        }
        return null;
    }

    public ArrayList<Observer> getObservers() {
        return observers;
    }

    public void setObservers(ArrayList<Observer> observers) {
        this.observers = observers;
    }

    /**
     * Sets the library information with which a scenario is instantiated
     *
     * @param libraryName
     * @param version
     */
    public void setLibraryInfo(String libraryName, String version) {
        this.library = libraryName + "-" + version;
    }

    public void setTimeLimited(boolean timeLimited) {
        this.timeLimited = timeLimited;
    }

    public void setIterationsLimited(boolean iterationsLimited) {
        this.iterationsLimited = iterationsLimited;
    }

    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
    }

    public void setLibrary(String library) {
        this.library = library;
    }

    public boolean isIterationsLimited() {
        return iterationsLimited;
    }

    public int getMaxIterations() {
        return maxIterations;
    }

    public String getLibrary() {
        return library;
    }

    public void setSimulationObjects(ArrayList<SimulationObject> simulationObjects) {
        this.simulationObjects = simulationObjects;
    }

    public void setLoggingTypes(ArrayList<LoggingType> loggingTypes) {
        this.loggingTypes = loggingTypes;
    }

    public ArrayList<TerminationCondition<?>> getTerminationCondition() {
        return terminationCondition;
    }

    public void setTerminationCondition(ArrayList<TerminationCondition<?>> terminationCondition) {
        this.terminationCondition = terminationCondition;
    }

}
