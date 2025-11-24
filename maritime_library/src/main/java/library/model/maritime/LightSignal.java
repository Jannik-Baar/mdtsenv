package library.model.maritime;

import library.model.simulation.SimulationProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class LightSignal {
    @XmlElement
    private SimulationProperty<LighthousePattern> pattern;

    @XmlElement
    private SimulationProperty<Double> period;

    @XmlElement
    private SimulationProperty<Double> nominalRange;

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

    public SimulationProperty<LighthousePattern> getPattern() {
        return pattern;
    }

    public void setPattern(SimulationProperty<LighthousePattern> pattern) {
        this.pattern = pattern;
    }

    public SimulationProperty<Double> getPeriod() {
        return period;
    }

    public void setPeriod(SimulationProperty<Double> period) {
        this.period = period;
    }

    public SimulationProperty<Double> getNominalRange() {
        return nominalRange;
    }

    public void setNominalRange(SimulationProperty<Double> nominalRange) {
        this.nominalRange = nominalRange;
    }

    public SimulationProperty<LightColor> getColor() {
        return color;
    }

    public void setColor(SimulationProperty<LightColor> color) {
        this.color = color;
    }
}
