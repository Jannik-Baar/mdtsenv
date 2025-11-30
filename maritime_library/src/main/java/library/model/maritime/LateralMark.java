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
 * Represents a Lateral Mark in maritime navigation.
 */
@Getter
@Setter
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class LateralMark extends Obstacle {

    /** The identifier/name of the mark */
    @XmlElement
    private SimulationProperty<String> name;

    /** The lateral type (e.g. STARBOARD_HAND) */
    @XmlElement
    private SimulationProperty<LateralMarkType> markType;

    /** The IALA region (A or B) */
    @XmlElement
    private SimulationProperty<Region> region;

    /** The number or identifier displayed on the mark */
    @XmlElement
    private SimulationProperty<String> number;

    /** The color of the mark */
    @XmlElement
    private SimulationProperty<LateralMarkColor> color;

    /** The shape of the mark */
    @XmlElement
    private SimulationProperty<LateralMarkShape> shape;

    /** The light signal characteristics */
    @XmlElement
    private LightSignal lightSignal;

    public LateralMark() {
        super();
    }

    public LateralMark(String nameStr, Position position, Geometry geometry,
                       LateralMarkType markType, Region region, String number, LightSignal lightSignal, LateralMarkShape shape) {
        super(true, position, geometry, 0.0);

        this.name = new SimulationProperty<>(false, false, NoUnit.get(), nameStr, "name");

        this.markType = new SimulationProperty<>(false, false, NoUnit.get(), markType, "markType");

        this.region = new SimulationProperty<>(false, false, NoUnit.get(), region, "region");

        this.number = new SimulationProperty<>(false, false, NoUnit.get(), number, "number");

        applyStandardAppearance(markType, region, shape);
        
        this.lightSignal = lightSignal;
    }

    /**
     * Applies standard IALA colors and shapes based on type and region.
     */
    private void applyStandardAppearance(LateralMarkType type, Region region, LateralMarkShape requestedShape) {
        LateralMarkColor derivedColor = LateralMarkColor.RED;
        LateralMarkShape derivedShape = LateralMarkShape.CAN;

        if (region == Region.REGION_A) {
            switch (type) {
                case PORT_HAND:
                    derivedColor = LateralMarkColor.RED;
                    derivedShape = LateralMarkShape.CAN;
                    break;
                case STARBOARD_HAND:
                    derivedColor = LateralMarkColor.GREEN;
                    derivedShape = LateralMarkShape.CONE;
                    break;
                case PREFERRED_CHANNEL_TO_STARBOARD:
                    derivedColor = LateralMarkColor.RED_GREEN_RED;
                    derivedShape = LateralMarkShape.CAN;
                    break;
                case PREFERRED_CHANNEL_TO_PORT:
                    derivedColor = LateralMarkColor.GREEN_RED_GREEN;
                    derivedShape = LateralMarkShape.CONE;
                    break;
            }
        } else {
            switch (type) {
                case PORT_HAND:
                    derivedColor = LateralMarkColor.GREEN;
                    derivedShape = LateralMarkShape.CAN;
                    break;
                case STARBOARD_HAND:
                    derivedColor = LateralMarkColor.RED;
                    derivedShape = LateralMarkShape.CONE;
                    break;
                case PREFERRED_CHANNEL_TO_STARBOARD:
                    derivedColor = LateralMarkColor.GREEN_RED_GREEN;
                    derivedShape = LateralMarkShape.CAN;
                    break;
                case PREFERRED_CHANNEL_TO_PORT:
                    derivedColor = LateralMarkColor.RED_GREEN_RED;
                    derivedShape = LateralMarkShape.CONE;
                    break;
            }
        }

        LateralMarkShape finalShape = (requestedShape != null) ? requestedShape : derivedShape;

        this.color = new SimulationProperty<>(false, false, NoUnit.get(), derivedColor, "color");
        this.shape = new SimulationProperty<>(false, false, NoUnit.get(), finalShape, "shape");
    }
}