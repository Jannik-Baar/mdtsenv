package library.model.maritime;

import library.model.simulation.SimulationProperty;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents light signal characteristics.
 */
@Getter
@Setter
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class LightSignal {
    
    /** The pattern of the light signal */
    @XmlElement
    private SimulationProperty<LighthousePattern> pattern;

    /** The period of the light cycle in seconds */
    @XmlElement
    private SimulationProperty<Double> period;

    /** The nominal range of the light in nautical miles */
    @XmlElement
    private SimulationProperty<Double> nominalRange;

    /** The color of the light */
    @XmlElement
    private SimulationProperty<LightColor> color;

    public LightSignal() {
    }

    public LightSignal(
        SimulationProperty<LighthousePattern> pattern,
        SimulationProperty<Double> period,
        SimulationProperty<Double> nominalRange,
        SimulationProperty<LightColor> color
    ) {
        this.pattern = pattern;
        this.period = period;
        this.nominalRange = nominalRange;
        this.color = color;
    }
}