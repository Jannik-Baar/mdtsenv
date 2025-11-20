package library.model.dto.observer;

import library.model.simulation.SimulationSuperClass;
import library.services.logging.LoggingType;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;

/**
 * Observer is used to specify Attributes from SimulationObjects that should be observed while the simulation is running.
 * <p>
 * TODO: implement generic observer interface class and different observers (not only websocket based observing)
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class Observer extends SimulationSuperClass {

    @XmlElement
    private double timeStepSize;

    @XmlElement(name = "observedObject")
    @XmlElementWrapper(name = "observedObjects")
    private ArrayList<ObservedObjectDTO> observedObjects = new ArrayList<>();

    @XmlElement(name = "observedClass")
    @XmlElementWrapper(name = "observedClasses")
    private ArrayList<ObservedClassDTO> observedClasses = new ArrayList<>();

    @XmlElement(name = "loggingType")
    @XmlElementWrapper(name = "loggingTypes")
    private ArrayList<LoggingType> loggingTypes = new ArrayList<>();

    @XmlElement
    private ObserverWebSocketConfig observerWebSocketConfig;

    public Observer() {
        super();
    }

    public Observer(double timeStepSize) {
        this.timeStepSize = timeStepSize;
    }

    public Observer(double timeStepSize,
                    ObserverWebSocketConfig observerWebSocketConfig) {
        this.timeStepSize = timeStepSize;
        this.observerWebSocketConfig = observerWebSocketConfig;
    }

    public double getTimeStepSize() {
        return timeStepSize;
    }

    public void addLoggingType(LoggingType loggingType) {
        this.loggingTypes.add(loggingType);
    }

    public ArrayList<LoggingType> getLoggingTypes() {
        return loggingTypes;
    }

    public ObserverWebSocketConfig getObserverWebSocketConfig() {
        return observerWebSocketConfig;
    }

    public ArrayList<ObservedObjectDTO> getObservedObjects() {
        return observedObjects;
    }

    public ArrayList<ObservedClassDTO> getObservedClasses() {
        return observedClasses;
    }

    public void setTimeStepSize(double timeStepSize) {
        this.timeStepSize = timeStepSize;
    }

    public void setObservedObjects(ArrayList<ObservedObjectDTO> observedObjects) {
        this.observedObjects = observedObjects;
    }

    public void setObservedClasses(ArrayList<ObservedClassDTO> observedClasses) {
        this.observedClasses = observedClasses;
    }

    public void setLoggingTypes(ArrayList<LoggingType> loggingTypes) {
        this.loggingTypes = loggingTypes;
    }

    public void setObserverWebSocketConfig(ObserverWebSocketConfig observerWebSocketConfig) {
        this.observerWebSocketConfig = observerWebSocketConfig;
    }
}
