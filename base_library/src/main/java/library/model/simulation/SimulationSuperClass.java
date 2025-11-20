package library.model.simulation;

import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.UUID;

/**
 * Super class for all simulation objects
 * Contains all shared attributes
 */
@XmlRootElement
public abstract class SimulationSuperClass {

    private String id;

    protected SimulationSuperClass() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    @XmlID
    public void setId(String id) {
        this.id = id;
    }
}
