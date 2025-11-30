package library.model.maritime;

import library.model.simulation.Position;
import library.model.simulation.SimulationProperty;
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
 * Represents a Safe Water Mark used to indicate navigable water.
 */
@Getter
@Setter
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SafeWaterMark extends Obstacle {

    /** The identifier/name of this mark */
    @XmlElement
    private SimulationProperty<String> name;

    /** The color pattern of the safe water mark */
    @XmlElement
    private SimulationProperty<SafeWaterMarkColor> color;

    /** The shape of the safe water mark */
    @XmlElement
    private SimulationProperty<SafeWaterMarkShape> shape;

    /** The marking text or identifier displayed on the mark */
    @XmlElement
    private SimulationProperty<String> marking;

    /** The light signal characteristics */
    @XmlElement
    private LightSignal lightSignal;

    public SafeWaterMark() {
        super();
    }

    public SafeWaterMark(String nameStr, Position position, Geometry geometry,
                         SafeWaterMarkShape shape, String marking, LightSignal lightSignal) {
        super(true, position, geometry, 0.0);

        this.name = new SimulationProperty<>(false, false, NoUnit.get(), nameStr, "name");

        this.color = new SimulationProperty<>(false, false, NoUnit.get(),
                SafeWaterMarkColor.RED_WHITE_VERTICAL_STRIPES, "color");

        this.shape = new SimulationProperty<>(false, false, NoUnit.get(), shape, "shape");

        this.marking = new SimulationProperty<>(false, false, NoUnit.get(), marking, "marking");

        this.lightSignal = lightSignal;
    }

    /**
     * Checks if this mark is fitted with a light.
     */
    public boolean isLit() {
        return lightSignal != null;
    }
}