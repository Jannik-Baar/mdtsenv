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
import org.locationtech.jts.geom.Geometry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Optional;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Vessel extends TrafficParticipant {

    @XmlElement
    private SimulationProperty<Double> course;

    @XmlElement
    private SimulationProperty<Double> draught;

    @XmlElement
    private SimulationProperty<String> homeHarbour;

    @XmlElement
    private SimulationProperty<String> vesselName;

    @XmlElement
    private SimulationProperty<String> flag;

    @XmlElement
    private SimulationProperty<String> imo;

    @XmlElement
    private SimulationProperty<String> mmsi;

    @XmlElement
    private SimulationProperty<String> callsign;

    @XmlElement
    private SimulationProperty<Double> loadCapacity;

    @XmlElement
    private SimulationProperty<Double> freeboard;

    @XmlElement
    private SimulationProperty<Double> turningCircle;

    @XmlElement
    private SimulationProperty<Double> stoppingDistance;

    @XmlElement
    private SimulationProperty<Double> accelerationDistance;

    @XmlElement
    private SimulationProperty<Double> maxAcceleration;

    @XmlElement
    private SimulationProperty<Double> maxDeceleration;

    @XmlTransient
    private GpsSensor gpsSensor;

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

    public SimulationProperty<Double> getFreeboard() {
        return freeboard;
    }

    public void setFreeboard(SimulationProperty<Double> freeboard) {
        this.freeboard = freeboard;
    }

    public SimulationProperty<Double> getCourse() {
        return course;
    }

    public SimulationProperty<Double> getDraught() {
        return draught;
    }

    public SimulationProperty<String> getHomeHarbour() {
        return homeHarbour;
    }

    public SimulationProperty<String> getVesselName() {
        return vesselName;
    }

    public SimulationProperty<String> getFlag() {
        return flag;
    }

    public SimulationProperty<String> getImo() {
        return imo;
    }

    public SimulationProperty<String> getMmsi() {
        return mmsi;
    }

    public SimulationProperty<String> getCallsign() {
        return callsign;
    }

    public SimulationProperty<Double> getLoadCapacity() {
        return loadCapacity;
    }

    public void setCourse(SimulationProperty<Double> course) {
        this.course = course;
    }

    public void setDraught(SimulationProperty<Double> draught) {
        this.draught = draught;
    }

    public void setHomeHarbour(SimulationProperty<String> homeHarbour) {
        this.homeHarbour = homeHarbour;
    }

    public void setVesselName(SimulationProperty<String> vesselName) {
        this.vesselName = vesselName;
    }

    public void setFlag(SimulationProperty<String> flag) {
        this.flag = flag;
    }

    public void setImo(SimulationProperty<String> imo) {
        this.imo = imo;
    }

    public void setMmsi(SimulationProperty<String> mmsi) {
        this.mmsi = mmsi;
    }

    public void setCallsign(SimulationProperty<String> callsign) {
        this.callsign = callsign;
    }

    public void setLoadCapacity(SimulationProperty<Double> loadCapacity) {
        this.loadCapacity = loadCapacity;
    }

    public SimulationProperty<Double> getTurningCircle() {
        return turningCircle;
    }

    public void setTurningCircle(SimulationProperty<Double> turningCircle) {
        this.turningCircle = turningCircle;
    }

    public SimulationProperty<Double> getStoppingDistance() {
        return stoppingDistance;
    }

    public void setStoppingDistance(SimulationProperty<Double> stoppingDistance) {
        this.stoppingDistance = stoppingDistance;
    }

    public SimulationProperty<Double> getAccelerationDistance() {
        return accelerationDistance;
    }

    public void setAccelerationDistance(SimulationProperty<Double> accelerationDistance) {
        this.accelerationDistance = accelerationDistance;
    }

    public SimulationProperty<Double> getMaxAcceleration() {
        return maxAcceleration;
    }

    public void setMaxAcceleration(SimulationProperty<Double> maxAcceleration) {
        this.maxAcceleration = maxAcceleration;
    }

    public SimulationProperty<Double> getMaxDeceleration() {
        return maxDeceleration;
    }

    public void setMaxDeceleration(SimulationProperty<Double> maxDeceleration) {
        this.maxDeceleration = maxDeceleration;
    }

    public GpsSensor getGpsSensor() {
        if (gpsSensor == null) {
            gpsSensor = findComponent(GpsSensor.class).orElse(null);
        }
        return gpsSensor;
    }

    public void setGpsSensor(GpsSensor gpsSensor) {
        this.getComponents().removeIf(c -> c instanceof GpsSensor);
        this.gpsSensor = gpsSensor;
        if (gpsSensor != null) {
            attachSensor(gpsSensor, "gpsSensor");
        }
    }

    public AisSensor getAisSensor() {
        if (aisSensor == null) {
            aisSensor = findComponent(AisSensor.class).orElse(null);
        }
        return aisSensor;
    }

    public void setAisSensor(AisSensor aisSensor) {
        this.getComponents().removeIf(c -> c instanceof AisSensor);
        this.aisSensor = aisSensor;
        if (aisSensor != null) {
            attachSensor(aisSensor, "aisSensor");
        }
    }
    
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

    private <T> Optional<T> findComponent(Class<T> componentType) {
        return this.getComponents()
                .stream()
                .filter(componentType::isInstance)
                .map(componentType::cast)
                .findFirst();
    }
}
