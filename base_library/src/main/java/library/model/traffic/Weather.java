package library.model.traffic;

import library.model.simulation.objects.ActiveSimulationObject;
import library.model.simulation.Position;
import library.model.simulation.SimulationProperty;
import library.model.simulation.units.TemperatureUnit;
import org.locationtech.jts.geom.Geometry;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A SimulationObject that is supposed to simulate the Weather
 */
@XmlRootElement
public class Weather extends ActiveSimulationObject {

    @XmlElement
    private SimulationProperty<Double> temperature;

    public Weather() {
        super();
    }

    public Weather(double timeStepSize,
                   boolean physical,
                   Position position,
                   Geometry form,
                   double rotation,
                   double temperature) {
        super(timeStepSize, physical, position, form, rotation);
        this.temperature = new SimulationProperty<>(false, false, TemperatureUnit.CELSIUS, temperature, "temperature");
    }

    public Weather(double timeStepSize,
                   SimulationProperty<Boolean> physical,
                   SimulationProperty<Position> position,
                   SimulationProperty<Geometry> form,
                   SimulationProperty<Double> rotation,
                   SimulationProperty<Double> temperature) {
        super(timeStepSize, physical, position, form, rotation);
        this.temperature = temperature;
    }

    public SimulationProperty<Double> getTemperature() {
        return temperature;
    }
}
