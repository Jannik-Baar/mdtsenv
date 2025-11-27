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
public class GeneralCargo extends Vessel {

    @XmlElement
    private SimulationProperty<Integer> maxLoadingWeight;

    public GeneralCargo() {
        super();
    }

    public GeneralCargo(double timeStepSize,
                        Position position,
                        Geometry form,
                        double rotation,
                        boolean physical,
                        PossibleDomains assignedDomain,
                        Double weight,
                        Double inertia,
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
                        int maxLoadingWeight,
                        double length,
                        double width) {
        super(timeStepSize, position, form, rotation, physical, assignedDomain, weight, inertia, speed, acceleration, origin,
              course, draught, homeHarbour, vesselName, flag, imo, mmsi, callsign, loadCapacity, length, width, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
        this.maxLoadingWeight = new SimulationProperty<>(NoUnit.get(), maxLoadingWeight, "maxLoadingWeight");
    }

    public GeneralCargo(double timeStepSize,
                        SimulationProperty<Position> position,
                        SimulationProperty<Geometry> form,
                        SimulationProperty<Double> rotation,
                        SimulationProperty<Boolean> physical,
                        SimulationProperty<PossibleDomains> assignedDomain,
                        SimulationProperty<Double> weight,
                        SimulationProperty<Double> inertia,
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
                        SimulationProperty<Integer> maxLoadingWeight,
                        SimulationProperty<Double> length,
                        SimulationProperty<Double> width) {
        super(timeStepSize, position, form, rotation, physical, assignedDomain, weight, inertia, speed, acceleration, origin, course, draught, homeHarbour, vesselName, flag, imo, mmsi, callsign, loadCapacity, length, width, 
              new SimulationProperty<>(library.model.simulation.units.DistanceUnit.METER, 0.0, "freeboard"),
              new SimulationProperty<>(library.model.simulation.units.DistanceUnit.METER, 0.0, "turningCircle"),
              new SimulationProperty<>(library.model.simulation.units.DistanceUnit.METER, 0.0, "stoppingDistance"),
              new SimulationProperty<>(library.model.simulation.units.DistanceUnit.METER, 0.0, "accelerationDistance"),
              new SimulationProperty<>(library.model.simulation.units.AccelerationUnit.METERSPERSECONDSSQUARED, 0.0, "maxAcceleration"),
              new SimulationProperty<>(library.model.simulation.units.AccelerationUnit.METERSPERSECONDSSQUARED, 0.0, "maxDeceleration"));
        this.maxLoadingWeight = maxLoadingWeight;
    }

    public SimulationProperty<Integer> getMaxLoadingWeight() {
        return maxLoadingWeight;
    }

}
