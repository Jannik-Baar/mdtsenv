package library.model.simulation;

import library.model.simulation.objects.SimulationObject;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;

@XmlRootElement
public class ExternalSimulationComponent extends SimulationObject {

    @XmlElement
    private Configuration config;

    @XmlElementWrapper
    @XmlElement(name = "component")
    private ArrayList<SimulationComponent> subComponents = new ArrayList<>();

    @XmlTransient
    public Configuration getConfiguration() {
        return config;
    }

    public void setConfiguration(Configuration configuration) {
        this.config = configuration;
    }

    @XmlTransient
    public ArrayList<SimulationComponent> getSubComponents() {
        return subComponents;
    }

    public void setSubComponents(ArrayList<SimulationComponent> subComponents) {
        this.subComponents = subComponents;
    }
}
