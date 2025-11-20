package library.model.simulation.objects;

import library.model.dto.observer.ObservedClassDTO;
import library.model.simulation.InterObjectCommunication;
import library.model.limitations.Restriction;
import library.model.simulation.Position;
import library.model.simulation.SimulationProperty;
import org.locationtech.jts.geom.Geometry;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base Object for all objects in the simulation that get manipulated
 */
@XmlRootElement
public abstract class DynamicSimulationObject extends SimulationObject implements IDynamic {

    @XmlElementWrapper(name = "limitations")
    @XmlElement(name = "limitations")
    private ArrayList<Restriction> limitations;

    @XmlElementWrapper(name = "communications")
    @XmlElement(name = "communication")
    private ArrayList<InterObjectCommunication> communications;

    ///////////////////////////
    // OBSERVED SURROUNDINGS //
    ///////////////////////////

    // the object classes that should be observed
    @XmlElementWrapper(name = "observedClasses")
    @XmlElement(name = "observedClass")
    private List<ObservedClassDTO> observedClasses;

    // the discovered object instances during runtime
    private Map<Class<?>, ArrayList<SimulationObject>> observedObjects = new HashMap<>();

    public DynamicSimulationObject(boolean physical,
                                   Position position,
                                   Geometry form,
                                   double rotation) {
        super(physical, position, form, rotation);
        this.limitations = new ArrayList<>();
        this.communications = new ArrayList<>();
    }

    public DynamicSimulationObject(SimulationProperty<Boolean> physical,
                                   SimulationProperty<Position> position,
                                   SimulationProperty<Geometry> form,
                                   SimulationProperty<Double> rotation) {
        super(physical, position, form, rotation);
        this.limitations = new ArrayList<>();
        this.communications = new ArrayList<>();
    }

    protected DynamicSimulationObject() {
        super();
        this.limitations = new ArrayList<>();
        this.observedClasses = new ArrayList<>();
        this.communications = new ArrayList<>();
    }

    @XmlTransient
    public ArrayList<Restriction> getLimitations() {
        return limitations;
    }

    public DynamicSimulationObject addLimitation(Restriction restrictions) {
        this.limitations.add(restrictions);
        return this;
    }

    @XmlTransient
    public List<ObservedClassDTO> getObservedClasses() {
        return observedClasses;
    }

    public Map<Class<?>, ArrayList<SimulationObject>> getObservedObjects() {
        return observedObjects;
    }

    public void addObservedObject(SimulationObject simulationObject) {
        Class<? extends SimulationObject> objectClass = simulationObject.getClass();
        ArrayList<SimulationObject> objectList = this.observedObjects.get(objectClass);
        if (objectList == null) {
            objectList = new ArrayList<>();
            this.observedObjects.put(objectClass, objectList);
        }
        objectList.add(simulationObject);
    }

    public void setRestrictions(ArrayList<Restriction> limitations) {
        this.limitations = limitations;
    }

    public void setObservedClasses(List<ObservedClassDTO> observedClasses) {
        this.observedClasses = observedClasses;
    }

    public void setObservedObjects(Map<Class<?>, ArrayList<SimulationObject>> observedObjects) {
        this.observedObjects = observedObjects;
    }

    @XmlTransient
    public ArrayList<InterObjectCommunication> getCommunications() {
        return communications;
    }

    public void setCommunications(ArrayList<InterObjectCommunication> communications) {
        this.communications = communications;
    }
}
