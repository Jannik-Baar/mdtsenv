package library.model.dto.observer;

import javax.xml.bind.annotation.XmlElement;

/**
 * Wrapper Class for values that should be observed. Replaces a Hashmap.
 */
public class ObservedValue {

    @XmlElement
    private String simulationObjectUUID;
    @XmlElement
    private String simulationAttributeUUID;

    public ObservedValue(String simulationObjectUUID, String simulationAttributeUUID) {
        this.simulationObjectUUID = simulationObjectUUID;
        this.simulationAttributeUUID = simulationAttributeUUID;
    }

    public ObservedValue() {
    }

    public String getSimulationObjectUUID() {
        return simulationObjectUUID;
    }

    public String getSimulationAttributeUUID() {
        return simulationAttributeUUID;
    }
}
