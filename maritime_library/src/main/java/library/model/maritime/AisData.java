package library.model.maritime;

import library.model.simulation.Position;
import library.model.simulation.SimulationProperty;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Encapsulates AIS (Automatic Identification System) data received from a vessel.
 * Contains all relevant identification and navigational information transmitted by the vessel.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AisData implements Serializable {

    @XmlElement
    private String mmsi;

    @XmlElement
    private String imo;

    @XmlElement
    private String vesselName;

    @XmlElement
    private String callsign;

    @XmlElement
    private Position position;

    @XmlElement
    private Double latitude;

    @XmlElement
    private Double longitude;

    @XmlElement
    private Double speedOverGround;

    @XmlElement
    private Double courseOverGround;

    @XmlElement
    private Double heading;

    @XmlElement
    private Double length;

    @XmlElement
    private Double width;

    @XmlElement
    private Double draught;

    @XmlElement
    private String flag;

    @XmlElement
    private long timestamp;

    public AisData() {
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Creates AIS data from a Vessel's current state.
     * 
     * @param vessel The vessel to extract AIS data from
     */
    public AisData(Vessel vessel) {
        if (vessel == null) {
            throw new IllegalArgumentException("Vessel cannot be null");
        }

        this.mmsi = getPropertyValue(vessel.getMmsi());
        this.imo = getPropertyValue(vessel.getImo());
        this.vesselName = getPropertyValue(vessel.getVesselName());
        this.callsign = getPropertyValue(vessel.getCallsign());
        this.flag = getPropertyValue(vessel.getFlag());

        // Position data
        if (vessel.getPosition() != null && vessel.getPosition().getValue() != null) {
            this.position = vessel.getPosition().getValue();
            if (position.getLatitude() != null) {
                this.latitude = position.getLatitude().getValue();
            }
            if (position.getLongitude() != null) {
                this.longitude = position.getLongitude().getValue();
            }
        }

        // Speed and Course
        this.speedOverGround = getPropertyValue(vessel.getSpeed());
        this.courseOverGround = getPropertyValue(vessel.getCourse());
        this.heading = getPropertyValue(vessel.getRotation());

        // Dimensions
        this.length = getPropertyValue(vessel.getLength());
        this.width = getPropertyValue(vessel.getWidth());
        this.draught = getPropertyValue(vessel.getDraught());

        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Helper method to safely extract values from SimulationProperty objects.
     */
    private <T> T getPropertyValue(SimulationProperty<T> property) {
        return (property != null) ? property.getValue() : null;
    }

    // Getters and Setters

    public String getMmsi() {
        return mmsi;
    }

    public void setMmsi(String mmsi) {
        this.mmsi = mmsi;
    }

    public String getImo() {
        return imo;
    }

    public void setImo(String imo) {
        this.imo = imo;
    }

    public String getVesselName() {
        return vesselName;
    }

    public void setVesselName(String vesselName) {
        this.vesselName = vesselName;
    }

    public String getCallsign() {
        return callsign;
    }

    public void setCallsign(String callsign) {
        this.callsign = callsign;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getSpeedOverGround() {
        return speedOverGround;
    }

    public void setSpeedOverGround(Double speedOverGround) {
        this.speedOverGround = speedOverGround;
    }

    public Double getCourseOverGround() {
        return courseOverGround;
    }

    public void setCourseOverGround(Double courseOverGround) {
        this.courseOverGround = courseOverGround;
    }

    public Double getHeading() {
        return heading;
    }

    public void setHeading(Double heading) {
        this.heading = heading;
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getDraught() {
        return draught;
    }

    public void setDraught(Double draught) {
        this.draught = draught;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Calculates the distance to another AIS target in meters.
     * Uses Haversine formula for great-circle distance.
     * 
     * @param other The other AIS target
     * @return Distance in meters, or -1 if position data is missing
     */
    public double getDistanceTo(AisData other) {
        if (this.latitude == null || this.longitude == null ||
            other.latitude == null || other.longitude == null) {
            return -1;
        }

        final double R = 6371000; // Earth's radius in meters

        double lat1Rad = Math.toRadians(this.latitude);
        double lat2Rad = Math.toRadians(other.latitude);
        double deltaLat = Math.toRadians(other.latitude - this.latitude);
        double deltaLon = Math.toRadians(other.longitude - this.longitude);

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                   Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                   Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    /**
     * Calculates the bearing to another AIS target in degrees (0-360).
     * 
     * @param other The other AIS target
     * @return Bearing in degrees, or -1 if position data is missing
     */
    public double getBearingTo(AisData other) {
        if (this.latitude == null || this.longitude == null ||
            other.latitude == null || other.longitude == null) {
            return -1;
        }

        double lat1Rad = Math.toRadians(this.latitude);
        double lat2Rad = Math.toRadians(other.latitude);
        double deltaLon = Math.toRadians(other.longitude - this.longitude);

        double y = Math.sin(deltaLon) * Math.cos(lat2Rad);
        double x = Math.cos(lat1Rad) * Math.sin(lat2Rad) -
                   Math.sin(lat1Rad) * Math.cos(lat2Rad) * Math.cos(deltaLon);

        double bearing = Math.toDegrees(Math.atan2(y, x));
        return (bearing + 360) % 360;
    }

    @Override
    public String toString() {
        return "AisData{" +
                "mmsi='" + mmsi + '\'' +
                ", vesselName='" + vesselName + '\'' +
                ", position=" + (position != null ? position.toString() : "null") +
                ", speed=" + speedOverGround +
                ", course=" + courseOverGround +
                ", heading=" + heading +
                '}';
    }
}

