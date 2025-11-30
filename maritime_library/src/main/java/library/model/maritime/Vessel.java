package library.model.maritime;

import library.model.simulation.Position;
import library.model.simulation.SimulationComponent;
import library.model.simulation.SimulationProperty;
import library.model.simulation.units.AccelerationUnit;
import library.model.simulation.units.DistanceUnit;
import library.model.simulation.units.NoUnit;
import library.model.simulation.units.RotationUnit;
import library.model.simulation.units.WeightUnit;
import library.model.traffic.PossibleDomains;
import library.model.traffic.TrafficParticipant;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Geometry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Optional;

/**
 * Represents a vessel in the maritime simulation.
 */
@Getter
@Setter
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Vessel extends TrafficParticipant {

    /** The vessel's course in degrees */
    @XmlElement
    private SimulationProperty<Double> course;

    /** The vessel's draught in meters */
    @XmlElement
    private SimulationProperty<Double> draught;

    /** The vessel's home harbour */
    @XmlElement
    private SimulationProperty<String> homeHarbour;

    /** The vessel's name */
    @XmlElement
    private SimulationProperty<String> vesselName;

    /** The vessel's flag/nationality */
    @XmlElement
    private SimulationProperty<String> flag;

    /** The vessel's IMO number */
    @XmlElement
    private SimulationProperty<String> imo;

    /** The vessel's MMSI number */
    @XmlElement
    private SimulationProperty<String> mmsi;

    /** The vessel's callsign */
    @XmlElement
    private SimulationProperty<String> callsign;

    /** The vessel's load capacity in tons */
    @XmlElement
    private SimulationProperty<Double> loadCapacity;

    /** The vessel's freeboard in meters */
    @XmlElement
    private SimulationProperty<Double> freeboard;

    /** The vessel's turning circle in meters */
    @XmlElement
    private SimulationProperty<Double> turningCircle;

    /** The vessel's stopping distance in meters */
    @XmlElement
    private SimulationProperty<Double> stoppingDistance;

    /** The vessel's acceleration distance in meters */
    @XmlElement
    private SimulationProperty<Double> accelerationDistance;

    /** The vessel's maximum acceleration in m/s² */
    @XmlElement
    private SimulationProperty<Double> maxAcceleration;

    /** The vessel's maximum deceleration in m/s² */
    @XmlElement
    private SimulationProperty<Double> maxDeceleration;

    /** The vessel's GPS sensor */
    @XmlTransient
    private GpsSensor gpsSensor;

    /** The vessel's AIS sensor */
    @XmlTransient
    private AisSensor aisSensor;

    public Vessel() {
        super();
        this.course = new SimulationProperty<>(RotationUnit.DEGREE, 0.0, "course");
        this.draught = new SimulationProperty<>(DistanceUnit.METER, 0.0, "draught");
        this.homeHarbour = new SimulationProperty<>(NoUnit.get(), "", "homeHarbour");
        this.vesselName = new SimulationProperty<>(NoUnit.get(), "", "vesselName");
        this.flag = new SimulationProperty<>(NoUnit.get(), "", "flag");
        this.imo = new SimulationProperty<>(NoUnit.get(), "", "imo");
        this.mmsi = new SimulationProperty<>(NoUnit.get(), "", "mmsi");
        this.callsign = new SimulationProperty<>(NoUnit.get(), "", "callsign");
        this.loadCapacity = new SimulationProperty<>(WeightUnit.TON, 0.0, "loadCapacity");
        this.freeboard = new SimulationProperty<>(DistanceUnit.METER, 0.0, "freeboard");
        this.turningCircle = new SimulationProperty<>(DistanceUnit.METER, 0.0, "turningCircle");
        this.stoppingDistance = new SimulationProperty<>(DistanceUnit.METER, 0.0, "stoppingDistance");
        this.accelerationDistance = new SimulationProperty<>(DistanceUnit.METER, 0.0, "accelerationDistance");
        this.maxAcceleration = new SimulationProperty<>(AccelerationUnit.METERSPERSECONDSSQUARED, 0.0, "maxAcceleration");
        this.maxDeceleration = new SimulationProperty<>(AccelerationUnit.METERSPERSECONDSSQUARED, 0.0, "maxDeceleration");
    }

    public Vessel(double timeStepSize,
                  Position position,
                  Geometry form,
                  double rotation,
                  boolean physical,
                  PossibleDomains assignedDomain,
                  double weight,
                  double inertia,
                  double speed,
                  double acceleration,
                  Position origin,
                  double course,
                  double draught,
                  String homeHarbour,
                  String vesselName,
                  String flag,
                  String imo,
                  String mmsi,
                  String callsign,
                  double loadCapacity,
                  double length,
                  double width,
                  double freeboard,
                  double turningCircle,
                  double stoppingDistance,
                  double accelerationDistance,
                  double maxAcceleration,
                  double maxDeceleration) {
        super(timeStepSize, physical, position, form, rotation, assignedDomain, weight, inertia, speed, acceleration, origin, length, width);
        this.course = new SimulationProperty<>(RotationUnit.DEGREE, course, "course");
        this.draught = new SimulationProperty<>(DistanceUnit.METER, draught, "draught");
        this.homeHarbour = new SimulationProperty<>(NoUnit.get(), homeHarbour, "homeHarbour");
        this.vesselName = new SimulationProperty<>(NoUnit.get(), vesselName, "vesselName");
        this.flag = new SimulationProperty<>(NoUnit.get(), flag, "flag");
        this.imo = new SimulationProperty<>(NoUnit.get(), imo, "imo");
        this.mmsi = new SimulationProperty<>(NoUnit.get(), mmsi, "mmsi");
        this.callsign = new SimulationProperty<>(NoUnit.get(), callsign, "callsign");
        this.loadCapacity = new SimulationProperty<>(WeightUnit.TON, loadCapacity, "loadCapacity");
        this.freeboard = new SimulationProperty<>(DistanceUnit.METER, freeboard, "freeboard");
        this.turningCircle = new SimulationProperty<>(DistanceUnit.METER, turningCircle, "turningCircle");
        this.stoppingDistance = new SimulationProperty<>(DistanceUnit.METER, stoppingDistance, "stoppingDistance");
        this.accelerationDistance = new SimulationProperty<>(DistanceUnit.METER, accelerationDistance, "accelerationDistance");
        this.maxAcceleration = new SimulationProperty<>(AccelerationUnit.METERSPERSECONDSSQUARED, maxAcceleration, "maxAcceleration");
        this.maxDeceleration = new SimulationProperty<>(AccelerationUnit.METERSPERSECONDSSQUARED, maxDeceleration, "maxDeceleration");
    }

    public Vessel(double timeStepSize,
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
                  SimulationProperty<Double> length,
                  SimulationProperty<Double> width,
                  SimulationProperty<Double> freeboard,
                  SimulationProperty<Double> turningCircle,
                  SimulationProperty<Double> stoppingDistance,
                  SimulationProperty<Double> accelerationDistance,
                  SimulationProperty<Double> maxAcceleration,
                  SimulationProperty<Double> maxDeceleration) {
        super(timeStepSize, physical, position, form, rotation, assignedDomain, weight, inertia, speed, acceleration, origin, length, width);
        this.course = course;
        this.draught = draught;
        this.homeHarbour = homeHarbour;
        this.vesselName = vesselName;
        this.flag = flag;
        this.imo = imo;
        this.mmsi = mmsi;
        this.callsign = callsign;
        this.loadCapacity = loadCapacity;
        this.freeboard = freeboard;
        this.turningCircle = turningCircle;
        this.stoppingDistance = stoppingDistance;
        this.accelerationDistance = accelerationDistance;
        this.maxAcceleration = maxAcceleration;
        this.maxDeceleration = maxDeceleration;
    }

    /**
     * Gets the GPS sensor attached to this vessel.
     */
    public GpsSensor getGpsSensor() {
        if (gpsSensor == null) {
            gpsSensor = findComponent(GpsSensor.class).orElse(null);
        }
        return gpsSensor;
    }

    /**
     * Sets the GPS sensor for this vessel.
     */
    public void setGpsSensor(GpsSensor gpsSensor) {
        this.getComponents().removeIf(c -> c instanceof GpsSensor);
        this.gpsSensor = gpsSensor;
        if (gpsSensor != null) {
            attachSensor(gpsSensor, "gpsSensor");
        }
    }

    /**
     * Gets the AIS sensor attached to this vessel.
     */
    public AisSensor getAisSensor() {
        if (aisSensor == null) {
            aisSensor = findComponent(AisSensor.class).orElse(null);
        }
        return aisSensor;
    }

    /**
     * Sets the AIS sensor for this vessel.
     */
    public void setAisSensor(AisSensor aisSensor) {
        this.getComponents().removeIf(c -> c instanceof AisSensor);
        this.aisSensor = aisSensor;
        if (aisSensor != null) {
            attachSensor(aisSensor, "aisSensor");
        }
    }
    
    /**
     * Attaches a sensor component to this vessel.
     */
    private void attachSensor(SimulationComponent component, String componentName) {
        if (component == null) {
            throw new IllegalArgumentException(componentName + " must not be null");
        }
        component.setParent(this);
        component.setTimeStepSize(this.getTimeStepSize());
        if (!this.getComponents().contains(component)) {
            this.addComponent(component);
        }
    }

    /**
     * Finds a component of the specified type.
     */
    private <T> Optional<T> findComponent(Class<T> componentType) {
        return this.getComponents()
                .stream()
                .filter(componentType::isInstance)
                .map(componentType::cast)
                .findFirst();
    }
}
