package library.model.maritime;

import library.model.simulation.Position;
import library.model.simulation.SimulationProperty;
import library.model.simulation.units.NoUnit;
import library.model.traffic.Infrastructure;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Geometry;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a Traffic Separation Scheme (TSS) according to IMO standards.
 */
@Getter
@Setter
@XmlRootElement
public class TrafficSeparationScheme extends Infrastructure {

    /** The type of TSS zone */
    @XmlElement
    private SimulationProperty<TrafficSeparationSchemeZoneType> zoneType;

    /** The direction of traffic flow within this TSS zone */
    @XmlElement
    private SimulationProperty<TrafficSeparationSchemeTrafficDirection> trafficDirection;
    
    /** Unique identifier or name of the TSS */
    @XmlElement
    private SimulationProperty<String> tssName;
    
    /** Custom bearing in degrees for traffic direction */
    @XmlElement
    private SimulationProperty<Double> customBearing;
    
    public TrafficSeparationScheme() {
        super();
    }
    
    public TrafficSeparationScheme(boolean physical,
                                   Position position,
                                   Geometry form,
                                   double rotation,
                                   TrafficSeparationSchemeZoneType zoneType,
                                   TrafficSeparationSchemeTrafficDirection trafficDirection,
                                   String tssName) {
        super(physical, position, form, rotation);
        
        this.zoneType = new SimulationProperty<>(
                false, false, NoUnit.get(), zoneType, "tssZoneType"
        );
        
        this.trafficDirection = new SimulationProperty<>(
                false, false, NoUnit.get(), trafficDirection, "tssTrafficDirection"
        );
        
        this.tssName = new SimulationProperty<>(
                false, false, NoUnit.get(), tssName, "tssName"
        );
        
        // Don't initialize customBearing with null value
        this.customBearing = null;
    }
    
    public TrafficSeparationScheme(boolean physical,
                                   Position position,
                                   Geometry form,
                                   double rotation,
                                   TrafficSeparationSchemeZoneType zoneType,
                                   Double customBearing,
                                   String tssName) {
        this(physical, position, form, rotation, zoneType, 
             TrafficSeparationSchemeTrafficDirection.CUSTOM_BEARING, tssName);
        
        // Only initialize customBearing if it's not null
        if (customBearing != null) {
            this.customBearing = new SimulationProperty<>(
                    false, false, NoUnit.get(), customBearing, "customBearing"
            );
        }
    }
    
    /**
     * Checks if a vessel's heading is compliant with the TSS direction requirements.
     */
    public boolean isHeadingCompliant(double vesselHeading, double tolerance) {
        if (trafficDirection == null || trafficDirection.getValue() == null) {
            return true;
        }
        
        TrafficSeparationSchemeTrafficDirection direction = trafficDirection.getValue();
        
        switch (direction) {
            case NORTH_TO_SOUTH:
                return isWithinTolerance(vesselHeading, 180.0, tolerance);
            case SOUTH_TO_NORTH:
                return isWithinTolerance(vesselHeading, 0.0, tolerance);
            case EAST_TO_WEST:
                return isWithinTolerance(vesselHeading, 270.0, tolerance);
            case WEST_TO_EAST:
                return isWithinTolerance(vesselHeading, 90.0, tolerance);
            case CUSTOM_BEARING:
                if (customBearing != null && customBearing.getValue() != null) {
                    return isWithinTolerance(vesselHeading, customBearing.getValue(), tolerance);
                }
                return true;
            case BIDIRECTIONAL:
            case NO_RESTRICTION:
                return true;
            case CLOCKWISE:
            case COUNTER_CLOCKWISE:
                return true;
            default:
                return true;
        }
    }
    
    /**
     * Helper method to check if a heading is within tolerance of a target bearing.
     */
    private boolean isWithinTolerance(double heading, double targetBearing, double tolerance) {
        double difference = Math.abs(heading - targetBearing);
        if (difference > 180) {
            difference = 360 - difference;
        }
        return difference <= tolerance;
    }
    
    /**
     * Checks if this TSS zone prohibits entry.
     */
    public boolean isEntryProhibited() {
        if (this.zoneType == null || this.zoneType.getValue() == null) {
            return false;
        }
        
        TrafficSeparationSchemeZoneType type = this.zoneType.getValue();
        return type == TrafficSeparationSchemeZoneType.SEPARATION_ZONE ||
               type == TrafficSeparationSchemeZoneType.AREA_TO_BE_AVOIDED;
    }
}
