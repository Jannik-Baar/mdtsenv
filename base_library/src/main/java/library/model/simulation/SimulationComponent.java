package library.model.simulation;

import library.model.simulation.objects.ActiveSimulationObject;
import library.model.simulation.objects.IActiveDynamic;
import library.model.simulation.objects.SimulationObject;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;

/**
 * Class to describe components which can be used to describe the inner workings of simulation objects
 */
@XmlRootElement
public abstract class SimulationComponent extends ActiveSimulationObject implements IActiveDynamic {

    @XmlIDREF
    private SimulationObject parent;

    @XmlElement
    private MethodCall linkedMethodCall;

    @XmlElementWrapper
    @XmlElement(name = "component")
    private ArrayList<SimulationComponent> subComponents = new ArrayList<>();

    private Behaviour behaviour;

    private double timeStepSize;

    protected SimulationComponent() {
        super();
    }

    public SimulationComponent(double timeStepSize,
                               SimulationObject parent) {
        this.timeStepSize = timeStepSize;
        if (parent == null) {
            throw new NullPointerException("param 'simulationObject' should not be null");
        }
        this.parent = parent;
        this.subComponents = new ArrayList<>();
    }

    public SimulationComponent addComponent(SimulationComponent subComponent) {
        this.subComponents.add(subComponent);
        return this;
    }

    @XmlTransient
    public SimulationObject getParent() {
        return parent;
    }

    @XmlTransient
    public ArrayList<SimulationComponent> getSubComponents() {
        return subComponents;
    }

    @Override
    public void setBehaviour(Behaviour behaviour) {
        this.behaviour = behaviour;
    }

    @Override
    public Behaviour getBehaviour() {
        return behaviour;
    }

    @Override
    public double getTimeStepSize() {
        return super.getTimeStepSize();
    }

    @XmlTransient
    public MethodCall getLinkedMethodCall() {
        return linkedMethodCall;
    }

    public void setParent(SimulationObject parent) {
        this.parent = parent;
    }

    public void setLinkedMethodCall(MethodCall linkedMethodCall) {
        this.linkedMethodCall = linkedMethodCall;
    }

    public void setSubComponents(ArrayList<SimulationComponent> subComponents) {
        this.subComponents = subComponents;
    }

    public void setTimeStepSize(double timeStepSize) {
        this.timeStepSize = timeStepSize;
    }
}
