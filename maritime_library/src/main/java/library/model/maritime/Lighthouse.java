package library.model.maritime;

import library.model.simulation.Position;
import library.model.simulation.SimulationProperty;
import library.model.simulation.units.DistanceUnit;
import library.model.simulation.units.NoUnit;
import library.model.traffic.Obstacle;
import org.locationtech.jts.geom.Geometry;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a Lighthouse (Leuchtturm) - Requirement F-3.8.
 * <p>
 * A Lighthouse is a tower, building, or other type of structure designed to emit light
 * from a system of lamps and lenses to serve as a navigational aid for maritime pilots
 * at sea or on inland waterways.
 * </p>
 * <p>
 * Lighthouses mark dangerous coastlines, hazardous shoals, reefs, rocks, and safe entries
 * to harbors. They also assist in aerial navigation.
 * </p>
 * <p>
 * Key characteristics:
 * - Fixed position (not floating like a buoy)
 * - Emits light with specific characteristics (color, pattern, period)
 * - Typically has significant height for visibility
 * - May include additional features like fog horns
 * </p>
 */
@XmlRootElement
public class Lighthouse extends Obstacle {

    /**
     * The name of the lighthouse (e.g., "Roter Sand", "Westerheversand").
     * Defined explicitly here as superclasses do not provide a name property.
     */
    @XmlElement
    private SimulationProperty<String> name;

    /**
     * The height of the lighthouse tower in meters.
     * Important for visibility calculations and identification.
     */
    @XmlElement
    private SimulationProperty<Double> height;

    /**
     * The light signal characteristics (pattern, period, nominal range, color).
     */
    @XmlElement
    private LightSignal lightSignal;

    /**
     * The focal height of the light above mean sea level in meters.
     * Important for calculating the geographic range of the light.
     */
    @XmlElement
    private SimulationProperty<Double> focalHeight;

    /**
     * Whether the lighthouse is currently active/operational.
     */
    @XmlElement
    private SimulationProperty<Boolean> isActive;

    /**
     * Default constructor for JAXB.
     */
    public Lighthouse() {
        super();
    }

    /**
     * Creates a new Lighthouse (Leuchtturm).
     *
     * @param name The name of the lighthouse.
     * @param position The geographic position (base of the lighthouse).
     * @param geometry The physical shape of the lighthouse structure.
     * @param height The total height of the lighthouse tower in meters.
     * @param lightColor The color of the emitted light.
     * @param lightPattern The pattern/characteristic of the light.
     * @param lightPeriod The period of the light cycle in seconds.
     * @param nominalRange The nominal range of the light in nautical miles.
     * @param focalHeight The focal height of the light above mean sea level in meters.
     * @param isActive Whether the lighthouse is currently operational.
     */
    public Lighthouse(String name, Position position, Geometry geometry,
                      double height, LightColor lightColor, LighthousePattern lightPattern,
                      double lightPeriod, double nominalRange, double focalHeight,
                      boolean isActive) {
        // A Lighthouse is always physical (true)
        super(true, position, geometry, 0.0);

        // Initialize properties
        this.name = new SimulationProperty<>(false, false, NoUnit.get(), name, "name");
        this.height = new SimulationProperty<>(false, false, DistanceUnit.METER, height, "height");
        
        // Create LightSignal with all light characteristics
        this.lightSignal = new LightSignal(
            new SimulationProperty<>(false, false, NoUnit.get(), lightPattern, "lightPattern"),
            new SimulationProperty<>(false, false, NoUnit.get(), lightPeriod, "lightPeriod"),
            new SimulationProperty<>(false, false, DistanceUnit.NAUTICALMILE, nominalRange, "nominalRange"),
            new SimulationProperty<>(false, false, NoUnit.get(), lightColor, "lightColor")
        );
        
        this.focalHeight = new SimulationProperty<>(false, false, DistanceUnit.METER, focalHeight, "focalHeight");
        this.isActive = new SimulationProperty<>(false, false, NoUnit.get(), isActive, "isActive");
    }

    /**
     * Simplified constructor for basic lighthouse creation.
     *
     * @param name The name of the lighthouse.
     * @param position The geographic position.
     * @param geometry The physical shape.
     * @param height The tower height in meters.
     * @param lightColor The color of the light.
     * @param nominalRange The nominal range in nautical miles.
     */
    public Lighthouse(String name, Position position, Geometry geometry,
                      double height, LightColor lightColor, double nominalRange) {
        this(name, position, geometry, height, lightColor,
                LighthousePattern.FLASHING, 10.0, nominalRange, height * 0.8, true);
    }

    // Getters and Setters

    public SimulationProperty<String> getName() {
        return name;
    }

    public void setName(SimulationProperty<String> name) {
        this.name = name;
    }

    public SimulationProperty<Double> getHeight() {
        return height;
    }

    public void setHeight(SimulationProperty<Double> height) {
        this.height = height;
    }

    public LightSignal getLightSignal() {
        return lightSignal;
    }

    public void setLightSignal(LightSignal lightSignal) {
        this.lightSignal = lightSignal;
    }

    public SimulationProperty<Double> getFocalHeight() {
        return focalHeight;
    }

    public void setFocalHeight(SimulationProperty<Double> focalHeight) {
        this.focalHeight = focalHeight;
    }

    public SimulationProperty<Boolean> getIsActive() {
        return isActive;
    }

    public void setIsActive(SimulationProperty<Boolean> isActive) {
        this.isActive = isActive;
    }

    /**
     * Calculates the geographic range of the lighthouse based on its focal height.
     * This is the maximum distance at which the light can be seen due to the curvature of the Earth.
     * <p>
     * Formula (nautical miles): Range = 2.08 * sqrt(focal_height_in_meters)
     * This assumes standard atmospheric refraction.
     * </p>
     *
     * @return The geographic range in nautical miles.
     */
    public double calculateGeographicRange() {
        if (focalHeight != null && focalHeight.getValue() != null) {
            return 2.08 * Math.sqrt(focalHeight.getValue());
        }
        return 0.0;
    }

    /**
     * Gets the effective range of the lighthouse.
     * This is the lesser of the nominal range (luminous range) and geographic range.
     *
     * @return The effective range in nautical miles.
     */
    public double getEffectiveRange() {
        double geographicRange = calculateGeographicRange();
        double luminousRange = (lightSignal != null && lightSignal.getNominalRange() != null 
                && lightSignal.getNominalRange().getValue() != null)
                ? lightSignal.getNominalRange().getValue() : 0.0;

        return Math.min(geographicRange, luminousRange);
    }
}