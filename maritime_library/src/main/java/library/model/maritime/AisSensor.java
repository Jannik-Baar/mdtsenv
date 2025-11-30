package library.model.maritime;

import library.model.simulation.SimulationComponent;
import library.model.simulation.objects.SimulationObject;
import library.model.traffic.Sensor;
import library.model.traffic.TrafficParticipant;
import library.services.geodata.MapDataProvider;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * AIS (Automatic Identification System) Sensor that detects and collects data from all vessels.
 */
@Getter
@Setter
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AisSensor extends Sensor {

    public AisSensor() {
        super();
    }

    public AisSensor(double timeStepSize, SimulationComponent parent) {
        super(timeStepSize, parent);
    }

    public AisSensor(double timeStepSize, SimulationObject parent) {
        super(timeStepSize, (SimulationComponent) parent);
    }

    /**
     * Scans for nearby vessels and returns a list of detected vessels.
     */
    public List<AisData> getAisData() {
        List<AisData> detectedVessels = new ArrayList<>();

        SimulationObject parent = getParent();
        if (parent == null) {
            return detectedVessels;
        }

        MapDataProvider mapDataProvider = MapDataProvider.getMap(parent);
        if (mapDataProvider == null) {
            return detectedVessels;
        }

        List<TrafficParticipant> allParticipants = mapDataProvider.getAllTrafficParticipants();

        for (TrafficParticipant tp : allParticipants) {
            if (tp instanceof Vessel && tp != parent) {
                detectedVessels.add(new AisData((Vessel) tp));
            }
        }
        return detectedVessels;
    }
}
