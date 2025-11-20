package library.model.simulation.units;

/**
 * Units of measurement for speed
 */
public class SpeedUnit extends SimulationUnit {

    public SpeedUnit() {
    }

    public static final SpeedUnit METERSPERSECOND = new SpeedUnit("meters per second", "m/s", 1.0);
    public static final SpeedUnit KILOMETERSPERHOUR = new SpeedUnit("kilometers per hour", "km/h", (1 / 3.6));
    public static final SpeedUnit KNOTS = new SpeedUnit("knots", "kn", (463.0 / 900.0));
    public static final SpeedUnit MILESPERHOUR = new SpeedUnit("miles per hour", "mph", (1.0 / 2.237));


    public SpeedUnit(String name, String abbreviation, Double unitToBase) {
        super(name, abbreviation, unitToBase);
    }
}
