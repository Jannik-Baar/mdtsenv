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
 * 
 * A TSS is a routing measure aimed at the separation of opposing streams of traffic
 * by appropriate means and by the establishment of traffic lanes. TSSs are adopted
 * by the International Maritime Organization (IMO) and are used in areas of high
 * traffic density to improve safety of navigation.
 * 
 * This infrastructure object defines areas where maritime traffic must follow 
 * specific routing rules.
 * 
 * Key features:
 * - Defines the zone type (traffic lane, separation zone, etc.)
 * - Specifies traffic direction within lanes
 * - Can include a unique identifier/name for the TSS
 * - Optionally specifies custom bearing for non-cardinal directions
 * 
 * @see <a href="https://www.imo.org/en/OurWork/Safety/Pages/ShipsRouteing.aspx">IMO Ships' Routeing</a>
 */
@Setter
@Getter
@XmlRootElement
public class TrafficSeparationScheme extends Infrastructure {

    /**
     * The type of TSS zone (e.g., TRAFFIC_LANE, SEPARATION_ZONE, etc.).
     */
    @XmlElement
    private SimulationProperty<TrafficSeparationSchemeZoneType> zoneType;

    /**
     * The direction of traffic flow within this TSS zone.
     * For TRAFFIC_LANE zones, this indicates the required direction of travel.
     * For SEPARATION_ZONE, typically NO_RESTRICTION as entry is prohibited.
     */
    @XmlElement
    private SimulationProperty<TrafficSeparationSchemeTrafficDirection> trafficDirection;
    
    /**
     * Unique identifier or name of the TSS (e.g., "Dover Strait TSS", "Strait of Gibraltar TSS").
     */
    @XmlElement
    private SimulationProperty<String> tssName;
    
    /**
     * Custom bearing in degrees (0-360) for traffic direction.
     * Only used when trafficDirection is CUSTOM_BEARING.
     * 0° = North, 90° = East, 180° = South, 270° = West.
     */
    @XmlElement
    private SimulationProperty<Double> customBearing;
    
    /**
     * Default constructor for XML deserialization.
     */
    public TrafficSeparationScheme() {
        super();
    }
    
    /**
     * Creates a Traffic Separation Scheme with specified properties.
     * 
     * @param physical Whether this infrastructure is physical
     * @param position The position of the TSS
     * @param form The geometric form of the TSS area
     * @param rotation The rotation of the TSS
     * @param zoneType The type of TSS zone (e.g., TRAFFIC_LANE, SEPARATION_ZONE)
     * @param trafficDirection The permitted direction of traffic flow
     * @param tssName The name or identifier of the TSS
     */
    public TrafficSeparationScheme(boolean physical,
                                   Position position,
                                   Geometry form,
                                   double rotation,
                                   TrafficSeparationSchemeZoneType zoneType,
                                   TrafficSeparationSchemeTrafficDirection trafficDirection,
                                   String tssName) {
        super(physical, position, form, rotation);
        
        // 1. Set Zone Type
        this.zoneType = new SimulationProperty<>(
                false, false, NoUnit.get(), zoneType, "tssZoneType"
        );
        
        // 2. Set Traffic Direction
        this.trafficDirection = new SimulationProperty<>(
                false, false, NoUnit.get(), trafficDirection, "tssTrafficDirection"
        );
        
        // 3. Set TSS Name
        this.tssName = new SimulationProperty<>(
                false, false, NoUnit.get(), tssName, "tssName"
        );
        
        // 4. Initialize custom bearing as null (only used for CUSTOM_BEARING direction)
        this.customBearing = new SimulationProperty<>(
                false, false, NoUnit.get(), null, "customBearing"
        );
    }
    
    /**
     * Creates a Traffic Separation Scheme with a custom bearing direction.
     * 
     * @param physical Whether this infrastructure is physical
     * @param position The position of the TSS
     * @param form The geometric form of the TSS area
     * @param rotation The rotation of the TSS
     * @param zoneType The type of TSS zone
     * @param customBearing The bearing in degrees (0-360)
     * @param tssName The name or identifier of the TSS
     */
    public TrafficSeparationScheme(boolean physical,
                                   Position position,
                                   Geometry form,
                                   double rotation,
                                   TrafficSeparationSchemeZoneType zoneType,
                                   Double customBearing,
                                   String tssName) {
        this(physical, position, form, rotation, zoneType, 
             TrafficSeparationSchemeTrafficDirection.CUSTOM_BEARING, tssName);
        
        // Set the custom bearing value
        this.customBearing = new SimulationProperty<>(
                false, false, NoUnit.get(), customBearing, "customBearing"
        );
    }
    
    /**
     * Checks if a vessel's heading is compliant with the TSS direction requirements.
     * 
     * @param vesselHeading The vessel's current heading in degrees (0-360)
     * @param tolerance Acceptable deviation in degrees
     * @return true if the vessel is following the correct direction, false otherwise
     */
    public boolean isHeadingCompliant(double vesselHeading, double tolerance) {
        if (trafficDirection == null || trafficDirection.getValue() == null) {
            return true; // No restriction
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
                // For roundabouts, would need position-based calculation
                // This is simplified - real implementation would check tangent to circle
                return true;
            default:
                return true;
        }
    }
    
    /**
     * Helper method to check if a heading is within tolerance of a target bearing.
     * Handles wrapping around 0/360 degrees.
     */
    private boolean isWithinTolerance(double heading, double targetBearing, double tolerance) {
        double difference = Math.abs(heading - targetBearing);
        // Handle wrapping (e.g., 359° vs 1° should be 2° difference, not 358°)
        if (difference > 180) {
            difference = 360 - difference;
        }
        return difference <= tolerance;
    }
    
    /**
     * Checks if this TSS zone prohibits entry (e.g., separation zones).
     * 
     * @return true if vessels should not enter this zone, false otherwise
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
