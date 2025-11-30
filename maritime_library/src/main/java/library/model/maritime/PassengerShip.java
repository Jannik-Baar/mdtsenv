package library.model.maritime;

import library.model.simulation.Position;
import library.model.simulation.SimulationProperty;
import library.model.simulation.units.NoUnit;
import library.model.traffic.PossibleDomains;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Geometry;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A Passenger Ship Vessel.
 */
@Getter
@Setter
@XmlRootElement
public class PassengerShip extends Vessel {

    /** Maximum passenger capacity */
    @XmlElement
    private SimulationProperty<Integer> passengerCapacity;

    public PassengerShip() {

    }

    public PassengerShip(boolean physical, Position position, Geometry form, double rotation, double timeStepSize,
                         PossibleDomains assignedDomain, Double weight, Double inertia, Double speed, Double acceleration,
                         Position origin, Double course, Double draught, String homeHarbour,
                         String vesselName, String flag, String imo, String mmsi, String callsign, Double loadCapacity,
                         int passengerCapacity, double length, double width) {
        super(timeStepSize, position, form, rotation, physical, assignedDomain, weight, inertia, speed, acceleration, origin,
              course, draught, homeHarbour, vesselName, flag, imo, mmsi, callsign, loadCapacity, length, width, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
        this.passengerCapacity = new SimulationProperty<>(NoUnit.get(), passengerCapacity, "passengerCapacity");
    }

    public PassengerShip(double timeStepSize,
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
                         SimulationProperty<String> callSign,
                         SimulationProperty<Double> loadCapacity,
                         SimulationProperty<Integer> passengerCapacity,
                         SimulationProperty<Double> length,
                         SimulationProperty<Double> width) {
        super(timeStepSize, position, form, rotation, physical, assignedDomain, weight, inertia, speed, acceleration, origin,
              course, draught, homeHarbour, vesselName, flag, imo, mmsi, callSign, loadCapacity, length, width, 
              new SimulationProperty<>(library.model.simulation.units.DistanceUnit.METER, 0.0, "freeboard"),
              new SimulationProperty<>(library.model.simulation.units.DistanceUnit.METER, 0.0, "turningCircle"),
              new SimulationProperty<>(library.model.simulation.units.DistanceUnit.METER, 0.0, "stoppingDistance"),
              new SimulationProperty<>(library.model.simulation.units.DistanceUnit.METER, 0.0, "accelerationDistance"),
              new SimulationProperty<>(library.model.simulation.units.AccelerationUnit.METERSPERSECONDSSQUARED, 0.0, "maxAcceleration"),
              new SimulationProperty<>(library.model.simulation.units.AccelerationUnit.METERSPERSECONDSSQUARED, 0.0, "maxDeceleration"));
        this.passengerCapacity = passengerCapacity;
    }
}
