package library.model.maritime;

import library.model.simulation.Position;
import library.model.simulation.SimulationProperty;
import library.model.simulation.units.DistanceUnit;
import library.model.simulation.units.NoUnit;
import library.model.traffic.Obstacle;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Geometry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a Lighthouse used as navigational aid.
 */
@Getter
@Setter
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Lighthouse extends Obstacle {

    /** The name of the lighthouse */
    @XmlElement
    private SimulationProperty<String> name;

    /** The height of the lighthouse tower in meters */
    @XmlElement
    private SimulationProperty<Double> height;

    /** The light signal characteristics */
    @XmlElement
    private LightSignal lightSignal;

    /** Whether the lighthouse is currently operational */
    @XmlElement
    private SimulationProperty<Boolean> isActive;

    public Lighthouse() {
        super();
    }

    public Lighthouse(String name, Position position, Geometry geometry,
                      double height, LightColor lightColor, LighthousePattern lightPattern,
                      double lightPeriod, double nominalRange,
                      boolean isActive) {
        super(true, position, geometry, 0.0);

        this.name = new SimulationProperty<>(false, false, NoUnit.get(), name, "name");
        this.height = new SimulationProperty<>(false, false, DistanceUnit.METER, height, "height");

        this.lightSignal = new LightSignal(
                new SimulationProperty<>(false, false, NoUnit.get(), lightPattern, "lightPattern"),
                new SimulationProperty<>(false, false, NoUnit.get(), lightPeriod, "lightPeriod"),
                new SimulationProperty<>(false, false, DistanceUnit.NAUTICALMILE, nominalRange, "nominalRange"),
                new SimulationProperty<>(false, false, NoUnit.get(), lightColor, "lightColor")
        );

        this.isActive = new SimulationProperty<>(false, false, NoUnit.get(), isActive, "isActive");
    }

    public Lighthouse(String name, Position position, Geometry geometry,
                      double height, LightColor lightColor, double nominalRange) {
        this(name, position, geometry, height, lightColor,
                LighthousePattern.FLASHING, 10.0, nominalRange, true);
    }
}