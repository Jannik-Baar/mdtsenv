package library.model.maritime;

import library.model.simulation.Position;
import library.model.simulation.SimulationProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Encapsulates AIS data received from a vessel.
 */
@Getter
@Setter
public class AisData implements Serializable {

    /** MMSI number */
    private String mmsi;

    /** IMO number */
    private String imo;

    /** Vessel name */
    private String vesselName;

    /** Callsign */
    private String callsign;

    /** Position of the vessel */
    private Position position;

    /** Latitude in degrees */
    private Double latitude;

    /** Longitude in degrees */
    private Double longitude;

    /** Speed over ground in knots */
    private Double speedOverGround;

    /** Course over ground in degrees */
    private Double courseOverGround;

    /** Heading in degrees */
    private Double heading;

    /** Length in meters */
    private Double length;

    /** Width in meters */
    private Double width;

    /** Draught in meters */
    private Double draught;

    /** Flag/nationality */
    private String flag;

    /** Timestamp of the AIS data */
    private long timestamp;

    /**
     * Default constructor.
     */
    public AisData() {
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Creates AIS data from a Vessel's current state.
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

        if (vessel.getPosition() != null && vessel.getPosition().getValue() != null) {
            this.position = vessel.getPosition().getValue();
            if (position.getLatitude() != null) {
                this.latitude = position.getLatitude().getValue();
            }
            if (position.getLongitude() != null) {
                this.longitude = position.getLongitude().getValue();
            }
        }

        this.speedOverGround = getPropertyValue(vessel.getSpeed());
        this.courseOverGround = getPropertyValue(vessel.getCourse());
        this.heading = getPropertyValue(vessel.getRotation());

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
