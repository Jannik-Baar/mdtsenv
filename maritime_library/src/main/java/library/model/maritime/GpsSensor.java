package library.model.maritime;

import library.model.simulation.Position;
import library.model.simulation.SimulationComponent;
import library.model.simulation.SimulationProperty;
import library.model.simulation.objects.SimulationObject;
import library.model.simulation.units.DistanceUnit;
import library.model.simulation.units.RotationUnit;
import library.model.traffic.Sensor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Sensor that exposes the parent simulation object's current position as GPS coordinates.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class GpsSensor extends Sensor {

    @XmlElement
    private SimulationProperty<Double> latitude;

    @XmlElement
    private SimulationProperty<Double> longitude;

    @XmlElement
    private SimulationProperty<Double> altitude;

    public GpsSensor() {
        super();
        initializeReadings();
    }

    public GpsSensor(double timeStepSize, SimulationComponent parent) {
        super(timeStepSize, parent);
        initializeReadings();
    }

    public GpsSensor(double timeStepSize, SimulationObject parent) {
        this();
        setTimeStepSize(timeStepSize);
        setParent(parent);
    }

    private void initializeReadings() {
        this.latitude = new SimulationProperty<>(RotationUnit.DEGREE, 0.0, "gpsLatitude");
        this.longitude = new SimulationProperty<>(RotationUnit.DEGREE, 0.0, "gpsLongitude");
        this.altitude = new SimulationProperty<>(DistanceUnit.METER, 0.0, "gpsAltitude");
    }

    /**
     * Refreshes the reading by pulling the parent's current position.
     */
    public void captureParentPosition() {
        SimulationObject parent = getParent();
        if (parent == null) {
            return;
        }
        SimulationProperty<Position> parentPositionProperty = parent.getPosition();
        if (parentPositionProperty == null || parentPositionProperty.getValue() == null) {
            return;
        }
        updateFromPosition(parentPositionProperty.getValue());
    }

    /**
     * Allows injecting positions, e.g. for tests.
     *
     * @param position position in decimal degrees (longitude, latitude, altitude)
     */
    public void updateFromPosition(Position position) {
        if (position == null) {
            return;
        }
        if (position.getLatitude() != null) {
            this.latitude.setSingleValue(position.getLatitude().getValue());
        }
        if (position.getLongitude() != null) {
            this.longitude.setSingleValue(position.getLongitude().getValue());
        }
        if (position.getAltitude() != null) {
            this.altitude.setSingleValue(position.getAltitude().getValue());
        }
    }

    public SimulationProperty<Double> getLatitude() {
        return latitude;
    }

    public SimulationProperty<Double> getLongitude() {
        return longitude;
    }

    public SimulationProperty<Double> getAltitude() {
        return altitude;
    }

    /**
     * Returns the latest reading as a Position instance (longitude, latitude, altitude).
     */
    public Position getGpsPosition() {
        return new Position(this.longitude.getValue(), this.latitude.getValue(), this.altitude.getValue());
    }
}


