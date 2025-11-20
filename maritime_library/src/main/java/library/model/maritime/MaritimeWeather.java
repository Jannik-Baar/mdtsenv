package library.model.maritime;

import library.model.simulation.Position;
import library.model.simulation.SimulationProperty;
import library.model.simulation.units.NoUnit;
import library.model.simulation.units.PressureUnit;
import library.model.simulation.units.RotationUnit;
import library.model.simulation.units.SpeedUnit;
import library.model.simulation.units.VolumeUnit;
import library.model.traffic.Weather;
import org.locationtech.jts.geom.Geometry;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class MaritimeWeather extends Weather {

    @XmlElement
    private SimulationProperty<Double> windStrength;

    @XmlElement
    private SimulationProperty<Double> windDirection;

    @XmlElement
    private SimulationProperty<Double> precipitationAmount;

    @XmlElement
    private SimulationProperty<Double> airPressure;

    @XmlElement
    private SimulationProperty<Boolean> thunderStorm;

    protected MaritimeWeather() {
        super();
    }

    public MaritimeWeather(double timeStepSize,
                           Position position,
                           Geometry form,
                           double rotation,
                           boolean physical,
                           double temperature,
                           double windStrength,
                           double windDirection,
                           double precipitationAmount,
                           double airPressure,
                           boolean thunderStorm) {
        super(timeStepSize, physical, position, form, rotation, temperature);
        this.windStrength = new SimulationProperty<>(false, false, SpeedUnit.KNOTS, windStrength, "windStrength");
        this.windDirection = new SimulationProperty<>(false, false, RotationUnit.DEGREE, windDirection, "windDirection");
        this.precipitationAmount = new SimulationProperty<>(false, false, VolumeUnit.LITER, precipitationAmount, "precipitationAmount");
        this.airPressure = new SimulationProperty<>(false, false, PressureUnit.PASCAL, airPressure, "airPressure");
        this.thunderStorm = new SimulationProperty<>(false, false, NoUnit.get(), thunderStorm, "thunderStorm");
    }

    public MaritimeWeather(double timeStepSize,
                           SimulationProperty<Position> position,
                           SimulationProperty<Geometry> form,
                           SimulationProperty<Double> rotation,
                           SimulationProperty<Boolean> physical,
                           SimulationProperty<Double> temperature,
                           SimulationProperty<Double> windStrength,
                           SimulationProperty<Double> windDirection,
                           SimulationProperty<Double> precipitationAmount,
                           SimulationProperty<Double> airPressure,
                           SimulationProperty<Boolean> thunderStorm) {
        super(timeStepSize, physical, position, form, rotation, temperature);
        this.windStrength = windStrength;
        this.windDirection = windDirection;
        this.precipitationAmount = precipitationAmount;
        this.airPressure = airPressure;
        this.thunderStorm = thunderStorm;
    }

    public SimulationProperty<Double> getWindStrength() {
        return windStrength;
    }

    public SimulationProperty<Double> getWindDirection() {
        return windDirection;
    }

    public SimulationProperty<Double> getPrecipitationAmount() {
        return precipitationAmount;
    }

    public SimulationProperty<Double> getAirPressure() {
        return airPressure;
    }

    public SimulationProperty<Boolean> getThunderStorm() {
        return thunderStorm;
    }
}
