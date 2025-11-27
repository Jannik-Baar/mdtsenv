package library.model.maritime;

import library.model.simulation.SimulationComponent;
import library.model.simulation.objects.SimulationObject;
import library.model.traffic.Sensor;
import library.model.traffic.TrafficParticipant;
import library.services.geodata.MapDataProvider;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * AIS (Automatic Identification System) Sensor that detects and collects data from nearby vessels.
 * Uses the MapDataProvider to retrieve information about other traffic participants in the simulation.
 */
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
     * Scans for nearby vessels using the MapDataProvider and updates the list of detected vessels.
     * This method should be called regularly to keep the AIS data up to date.
     * @return List of AIS data from detected vessels
     */
    public List<AisData> getAisData() {
        List<AisData> detectedVessels = new ArrayList<>();

        SimulationObject parent = getParent();
        if (parent == null) {
            return detectedVessels;
        }

        // Get the MapDataProvider for this simulation object
        MapDataProvider mapDataProvider = MapDataProvider.getMap(parent);
        if (mapDataProvider == null) {
            return detectedVessels;
        }

        // Get all traffic participants from the map
        List<TrafficParticipant> allParticipants = mapDataProvider.getAllTrafficParticipants();

        // Add all vessels to the detected list (no filtering)
        for (TrafficParticipant tp : allParticipants) {
            if (tp instanceof Vessel) {
                detectedVessels.add(new AisData((Vessel) tp));
            }
        }
        return detectedVessels;
    }
}

