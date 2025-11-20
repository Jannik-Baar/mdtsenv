package library.model.simulation;

/**
 * Task is used for internal communication between a simulationObject and a behaviour
 */
public class Task {

    private String attributeID;
    private Object value;

    public Task(String attributeID, Object value) {
        this.attributeID = attributeID;
        this.value = value;
    }

    public String getAttributeID() {
        return attributeID;
    }

    public void setAttributeID(String attributeID) {
        this.attributeID = attributeID;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
