package library.model.simulation.objects;

import library.model.simulation.*;
import org.locationtech.jts.geom.Geometry;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;

/**
 * Base Object for all objects in the simulation that have the ability of active attribute manipulation
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class ActiveSimulationObject extends DynamicSimulationObject implements IActiveDynamic {

    @XmlElement
    private double timeStepSize;

    @XmlElement
    private Behaviour behaviour;

    @XmlElement
    private MethodCall linkedMethodCall;
FF
    public MethodCall getLinkedMethodCall() {
        return linkedMethodCall;
    }

    @XmlTransient
    public void setLinkedMethodCall(MethodCall linkedMethodCall) {
        this.linkedMethodCall = linkedMethodCall;
    }

    public void setTimeStepSize(double timeStepSize) {
        this.timeStepSize = timeStepSize;
    }

    // TODO QUESTION i think the components doesn't have to be simulation attributes, since we don't share those?
    // TODO use a standard array list type of list instead of a custom implementation arraylist wrapper thingy
    // TODO replaced with standard ArrayList (AC)
    @XmlElement
    private ArrayList<SimulationComponent> components;

    public ActiveSimulationObject(double timeStepSize,
                                  boolean physical,
                                  Position position,
                                  Geometry form,
                                  double rotation) {
        super(physical, position, form, rotation);
        this.timeStepSize = timeStepSize;
        this.components = new ArrayList<>();
    }

    public ActiveSimulationObject(double timeStepSize,
                                  SimulationProperty<Boolean> physical,
                                  SimulationProperty<Position> position,
                                  SimulationProperty<Geometry> form,
                                  SimulationProperty<Double> rotation) {
        super(physical, position, form, rotation);
        this.timeStepSize = timeStepSize;
        this.components = new ArrayList<>();
    }

    public ActiveSimulationObject() {
        super();
        this.components = new ArrayList<>();
    }

    @Override
    public double getTimeStepSize() {
        //TODO workaround to avoid exception in simulation execution
        if (timeStepSize == 0.0) {
            return 1.0;
        }
        return timeStepSize;
    }

    @Override
    public void setBehaviour(Behaviour behaviour) {
        this.behaviour = behaviour;
    }

    public Behaviour getBehaviour() {
        return behaviour;
    }

    @XmlTransient
    public ArrayList<SimulationComponent> getComponents() {
        if (components == null) {
            components = new ArrayList<>();
        }
        return components;
    }

    public SimulationObject addComponent(SimulationComponent component) {
        this.components.add(component);
        return this;
    }

    public void setComponents(ArrayList<SimulationComponent> components) {
        this.components = components;
    }


    /*public void setComponents(ArrayList components) {
        if (!(components.get(0) instanceof SimulationComponent)) {
            return;
        }
        ArrayList<SimulationComponent> list2 = new ArrayList<>();
        for (Object c : components) {
            if (c instanceof SimulationComponent) {
                list2.add((SimulationComponent) c);
            }
        }
        ComponentList componentList = new ComponentList();
        componentList.setComponents(list2);
        SimulationProperty<ComponentList> componentListSimulationProperty = new SimulationProperty<>();
        componentListSimulationProperty.setValue(componentList);
        componentListSimulationProperty.setValueKind(SimulationPropertyValueKind.SINGLE);
        componentListSimulationProperty.setSimulationVisibility(SimulationVisibilityKind.READ);
        setComponents(componentListSimulationProperty);
    }*/
}
