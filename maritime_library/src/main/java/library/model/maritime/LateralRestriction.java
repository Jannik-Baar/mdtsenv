package library.model.maritime;

import library.model.simulation.SimulationProperty;
import library.model.simulation.units.NoUnit;
import library.model.traffic.TrafficRestriction;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a restriction imposed by the IALA Lateral System.
 * Encapsulates logic (Type, Region) and visual representation (Color, Shape, Number).
 */
@Setter
@Getter
@XmlRootElement
public class LateralRestriction extends TrafficRestriction<LateralMarkType> {

    @XmlElement
    private SimulationProperty<LateralRegion> region;

    @XmlElement
    private SimulationProperty<LateralMarkColor> color;

    @XmlElement
    private SimulationProperty<LateralMarkShape> shape;

    @XmlElement
    private SimulationProperty<String> number;

    public LateralRestriction() {
        super();
    }

    /**
     * Creates a LateralRestriction and automatically derives standard Color and Shape
     * based on the provided Type and Region (IALA rules).
     *
     * @param type The logical type (e.g., PORT_HAND).
     * @param region The region (A or B).
     * @param number The identifier/number written on the mark (e.g., "2", "4a").
     */
    public LateralRestriction(LateralMarkType type, LateralRegion region, String number) {
        super();

        // 1. Set Logical Type (Restriction Value)
        SimulationProperty<LateralMarkType> typeProp = new SimulationProperty<>(
                false, false, NoUnit.get(), type, "lateralMarkType"
        );
        this.addLimitedProperty(typeProp);

        // 2. Set Region
        this.region = new SimulationProperty<>(
                false, false, NoUnit.get(), region, "ialaRegion"
        );

        // 3. Set Numbering
        this.number = new SimulationProperty<>(
                false, false, NoUnit.get(), number, "lateralNumber"
        );

        // 4. Derive & Set Visual Properties (Color & Shape)
        applyStandardAppearance(type, region);
    }

    /**
     * Applies standard IALA colors and shapes based on type and region.
     */
    private void applyStandardAppearance(LateralMarkType type, LateralRegion region) {
        LateralMarkColor derivedColor = LateralMarkColor.RED; // Default fallback
        LateralMarkShape derivedShape = LateralMarkShape.CAN; // Default fallback

        if (region == LateralRegion.REGION_A) {
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
        } else { // REGION_B
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

        this.color = new SimulationProperty<>(false, false, NoUnit.get(), derivedColor, "lateralColor");
        this.shape = new SimulationProperty<>(false, false, NoUnit.get(), derivedShape, "lateralShape");
    }
}