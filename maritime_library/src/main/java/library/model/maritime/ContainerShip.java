package library.model.maritime;

import library.model.simulation.Position;
import library.model.simulation.SimulationProperty;
import library.model.simulation.units.NoUnit;
import library.model.traffic.PossibleDomains;
import org.locationtech.jts.geom.Geometry;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A ContainerShip Vessel holding attributes like containerCapacity, etc.
 */
@XmlRootElement
public class ContainerShip extends Vessel {

    @XmlElement
    private SimulationProperty<Integer> containerCapacity;

    @XmlElement
    private SimulationProperty<Boolean> emergencyDeclared;

    public ContainerShip() {
        super();
    }

    public ContainerShip(double timeStepSize, Position position, Geometry form, double rotation, boolean physical,
                         PossibleDomains assignedDomain,
                         Double weight,
                         Double speed,
                         Double acceleration,
                         Position origin,
                         Double course,
                         Double draught,
                         String homeHarbour,
                         String vesselName,
                         String flag,
                         String imo,
                         String mmsi,
                         String callsign,
                         Double loadCapacity,
                         int containerCapacity,
                         double length,
                         double width) {
        super(timeStepSize, position, form, rotation, physical, assignedDomain, weight, speed, acceleration, origin,
              course, draught, homeHarbour, vesselName, flag, imo, mmsi, callsign, loadCapacity, length, width);
        this.containerCapacity = new SimulationProperty<>(NoUnit.get(), containerCapacity, "containerCapacity");
        this.emergencyDeclared = new SimulationProperty<>(true, false, NoUnit.get(), false, "emergencyDeclared");
    }

    public ContainerShip(double timeStepSize,
                         SimulationProperty<Position> position,
                         SimulationProperty<Geometry> form,
                         SimulationProperty<Double> rotation,
                         SimulationProperty<Boolean> physical,
                         SimulationProperty<PossibleDomains> assignedDomain,
                         SimulationProperty<Double> weight,
                         SimulationProperty<Double> speed,
                         SimulationProperty<Double> acceleration,
                         SimulationProperty<Position> origin,
                         SimulationProperty<Double> course,
                         SimulationProperty<Double> draught,
                         SimulationProperty<String> homeHarbour,
                         SimulationProperty<String> vesselName,
                         SimulationProperty<String> flag,
                         SimulationProperty<String> imo,
                         SimulationProperty<String> mmsi,
                         SimulationProperty<String> callsign,
                         SimulationProperty<Double> loadCapacity,
                         SimulationProperty<Integer> containerCapacity,
                         SimulationProperty<Double> length,
                         SimulationProperty<Double> width) {
        super(timeStepSize, position, form, rotation, physical, assignedDomain, weight, speed, acceleration, origin, course, draught, homeHarbour, vesselName, flag, imo, mmsi, callsign, loadCapacity, length, width);
        this.containerCapacity = containerCapacity;
        this.emergencyDeclared = new SimulationProperty<>(true, false, NoUnit.get(), false, "emergencyDeclared");

    }

    public SimulationProperty<Integer> getContainerCapacity() {
        return containerCapacity;
    }

    public SimulationProperty<Boolean> getEmergencyDeclared() {
        return emergencyDeclared;
    }

}
