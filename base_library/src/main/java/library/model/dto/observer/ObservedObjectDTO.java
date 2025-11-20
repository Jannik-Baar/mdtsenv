package library.model.dto.observer;

import library.model.simulation.objects.SimulationObject;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object (DTO) for a SimulationObject to subscribe to
 */
@XmlRootElement(name = "observedObject")
@XmlAccessorType(XmlAccessType.NONE)
public class ObservedObjectDTO {

    @XmlElement
    @Nullable
    private String id;

    @XmlElementWrapper(name = "attributes")
    @XmlElement(name = "attribute")
    private List<String> attributes = new ArrayList<>();

    private String fomPath;

    private Class<SimulationObject> objectClass;

    public ObservedObjectDTO() {
        // empty constructor
    }

    public ObservedObjectDTO(String id, ArrayList<String> attributes) {
        this.id = id;
        this.attributes = attributes;
    }

    @Nullable
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<String> attributes) {
        this.attributes = attributes;
    }

    public String getFomPath() {
        return fomPath;
    }

    public void setFomPath(String fomPath) {
        this.fomPath = fomPath;
    }

    public Class<SimulationObject> getObjectClass() {
        return objectClass;
    }

    public void setObjectClass(Class<SimulationObject> objectClass) {
        this.objectClass = objectClass;
    }
    
}
