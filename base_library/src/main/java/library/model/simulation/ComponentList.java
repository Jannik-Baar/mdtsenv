package library.model.simulation;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;

/**
 * a list of components
 *
 * TODO: improve javadoc
 */
@XmlRootElement
public class ComponentList {
    @XmlTransient
    private ArrayList<SimulationComponent> components;

    public ComponentList() {
        this.components = new ArrayList<>();
    }

    public ComponentList(ArrayList componentList) {
        this.components = componentList;
    }

    public void addComponent(SimulationComponent component) {
        this.components.add(component);
    }

    @XmlElement(name = "component")
    public ArrayList<SimulationComponent> getComponents() {
        return components;
    }

    public void setComponents(ArrayList<SimulationComponent> componentsList) {
        this.components = componentsList;
    }
}
