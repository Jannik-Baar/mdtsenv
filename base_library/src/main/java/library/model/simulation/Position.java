package library.model.simulation;

import library.model.simulation.units.DistanceUnit;
import library.model.simulation.units.NoUnit;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class to declare the position of a SimulationObject
 */
@XmlRootElement
public class Position extends SimulationSuperClass {

    @XmlElement
    SimulationProperty<Double> latitude;
    @XmlElement
    SimulationProperty<Double> longitude;
    @XmlElement
    SimulationProperty<Double> altitude;

    public Position(double longitude, double latitude, double altitude) {
        this.longitude = new SimulationProperty<>(NoUnit.get(), longitude, "longitude");
        this.longitude.setPublish(true);
        this.latitude = new SimulationProperty<>(NoUnit.get(), latitude, "latitude");
        this.latitude.setPublish(true);
        this.altitude = new SimulationProperty<>(DistanceUnit.METER, altitude, "altitude");
        this.altitude.setPublish(true);
    }

    public Position() {
        this.longitude = new SimulationProperty<>(NoUnit.get(), 0.0, "longitude");
        this.latitude = new SimulationProperty<>(NoUnit.get(), 0.0, "latitude");
        this.altitude = new SimulationProperty<>(DistanceUnit.METER, 0.0, "altitude");
        this.longitude.setPublish(true);
        this.latitude.setPublish(true);
        this.altitude.setPublish(true);
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

    public void setLatitude(double latitude) {
        this.latitude.setSingleValue(latitude);
    }

    public void setLongitude(double longitude) {
        this.longitude.setSingleValue(longitude);
    }

    public void setAltitude(double altitude) {
        this.altitude.setSingleValue(altitude);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return latitude.getValue().equals(position.latitude.getValue()) && longitude.getValue().equals(position.getLongitude().getValue()) && altitude.getValue().equals(position.getAltitude().getValue());
    }

    @Override
    public String toString() {
        return String.format("\"Position\":{ \"Lat\":\"" + this.latitude.getValue().toString() + "\",\"Lon\":\"" +this.longitude.getValue().toString() + "\",\"Alt\":\"" +this.altitude.getValue().toString() + "\"}");
    }
}
