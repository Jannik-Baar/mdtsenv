package library.model.dto.observer;

import library.model.simulation.objects.SimulationObject;
import org.jetbrains.annotations.NotNull;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object (DTO) for a SimulationObject to subscribe to
 */
@XmlRootElement(name = "observedClass")
@XmlAccessorType(XmlAccessType.NONE)
public class ObservedClassDTO {

    @XmlElement
    @NotNull
    private String type;

    @XmlElementWrapper(name = "attributes")
    @XmlElement(name = "attribute")
    private List<String> attributes = new ArrayList<>();

    private String fomPath;

    private Class<SimulationObject> objectClass;

    public ObservedClassDTO() {
        // empty constructor
    }

    public ObservedClassDTO(String id, ArrayList<String> attributes) {
        this.attributes = attributes;
    }

    @NotNull
    public String getType() {
        return type;
    }

    public void setType(@NotNull String type) {
        this.type = type;
    }

    public List<String> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<String> attributes) {
        this.attributes = attributes;
    }

    public Class<SimulationObject> getObjectClass() {
        return objectClass;
    }

    public void setObjectClass(Class<SimulationObject> objectClass) {
        this.objectClass = objectClass;
    }

    public String getFomPath() {
        return fomPath;
    }

    public void setFomPath(String fomPath) {
        this.fomPath = fomPath;
    }

}
