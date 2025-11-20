package library.model.traffic;

import library.model.simulation.objects.ActiveSimulationObject;
import library.model.simulation.Position;
import library.model.simulation.SimulationProperty;
import library.model.simulation.units.AccelerationUnit;
import library.model.simulation.units.DistanceUnit;
import library.model.simulation.units.NoUnit;
import library.model.simulation.units.SpeedUnit;
import library.model.simulation.units.WeightUnit;
import org.locationtech.jts.geom.Geometry;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * A SimulationObject that actively takes part in the Traffic of the Simulation
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrafficParticipant extends ActiveSimulationObject {

    @XmlElement
    private SimulationProperty<PossibleDomains> assignedDomain;

    private SimulationProperty<Double> weight;

    @XmlElement
    private SimulationProperty<Double> speed;

    private SimulationProperty<Double> acceleration;

    @XmlElement
    private SimulationProperty<Position> origin;

    private SimulationProperty<Double> maxSpeed;

    @XmlElementWrapper
    @XmlElement(name = "waypoint")
    private List<SimulationProperty<Position>> route;

    private SimulationProperty<Double> length;

    private SimulationProperty<Double> width;

    public void setWeight(SimulationProperty<Double> weight) {
        this.weight = weight;
    }

    public SimulationProperty<Double> getWidth() {
        return width;
    }

    public void setWidth(SimulationProperty<Double> width) {
        this.width = width;
    }

    public SimulationProperty<Double> getLength() {
        return length;
    }

    public void setLength(SimulationProperty<Double> length) {
        this.length = length;
    }

    public TrafficParticipant() {
        super();
    }

    public TrafficParticipant(double timeStepSize,
                              boolean physical,
                              Position position,
                              Geometry form,
                              double rotation,
                              PossibleDomains assignedDomain,
                              Double weight,
                              Double speed,
                              Double acceleration,
                              Position origin,
                              double length,
                              double width) {
        super(timeStepSize, physical, position, form, rotation);
        this.assignedDomain = new SimulationProperty<>(false, false, NoUnit.get(), assignedDomain, "assignedDomain");
        this.weight = new SimulationProperty<>(false, false, WeightUnit.TON, weight, "weight");
        this.speed = new SimulationProperty<>(false, false, SpeedUnit.KNOTS, speed, "speed");
        this.acceleration = new SimulationProperty<>(false, false, AccelerationUnit.METERSPERSECONDSSQUARED, acceleration, "acceleration");
        this.origin = new SimulationProperty<>(false, false, NoUnit.get(), origin, "origin");
        this.length = new SimulationProperty<>(true, false, DistanceUnit.METER, length, "length");
        this.width = new SimulationProperty<>(true, false, DistanceUnit.METER, width, "width");
    }

    public TrafficParticipant(double timeStepSize,
                              SimulationProperty<Boolean> physical,
                              SimulationProperty<Position> position,
                              SimulationProperty<Geometry> form,
                              SimulationProperty<Double> rotation,
                              SimulationProperty<PossibleDomains> assignedDomain,
                              SimulationProperty<Double> weight,
                              SimulationProperty<Double> speed,
                              SimulationProperty<Double> acceleration,
                              SimulationProperty<Position> origin,
                              SimulationProperty<Double> length,
                              SimulationProperty<Double> width) {
        super(timeStepSize, physical, position, form, rotation);
        this.assignedDomain = assignedDomain;
        this.weight = weight;
        this.speed = speed;
        this.acceleration = acceleration;
        this.origin = origin;
        this.length = length;
        this.width = width;
    }

    public SimulationProperty<PossibleDomains> getAssignedDomain() {
        return assignedDomain;
    }

    public SimulationProperty<Double> getWeight() {
        return weight;
    }

    public SimulationProperty<Double> getSpeed() {
        return speed;
    }

    public SimulationProperty<Double> getAcceleration() {
        return acceleration;
    }

    public SimulationProperty<Position> getOrigin() {
        return origin;
    }

    public List<SimulationProperty<Position>> getRoute() {
        return route;
    }

    @XmlTransient
    public void setRoute(List<SimulationProperty<Position>> route) {
        this.route = route;
    }

    public SimulationProperty<Double> getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(SimulationProperty<Double> maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    @XmlElement
    public void setMaxSpeed(Double maxSpeed) {
        this.maxSpeed = new SimulationProperty<>(true, true, SpeedUnit.METERSPERSECOND, maxSpeed, "maxSpeed");
    }

    public void setAcceleration(SimulationProperty<Double> acceleration) {
        this.acceleration = acceleration;
    }

    public void setAssignedDomain(SimulationProperty<PossibleDomains> assignedDomain) {
        this.assignedDomain = assignedDomain;
    }

    public void setSpeed(SimulationProperty<Double> speed) {
        this.speed = speed;
    }

    public void setOrigin(SimulationProperty<Position> origin) {
        this.origin = origin;
    }
}
