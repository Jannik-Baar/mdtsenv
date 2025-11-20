package library.model.simulation.units;

/**
 * Units of measurement for distance
 */
public class DistanceUnit extends SimulationUnit {

    public DistanceUnit() {
        super();
    }

    public static final DistanceUnit METER = new DistanceUnit("meter", "m", 1.0);
    public static final DistanceUnit CENTIMETER = new DistanceUnit("centimeter", "cm", 0.01);
    public static final DistanceUnit MILLIMETER = new DistanceUnit("millimeter", "mm", 0.001);
    public static final DistanceUnit KILOMETER = new DistanceUnit("kilometer", "km", 1000.0);
    public static final DistanceUnit NAUTICALMILE = new DistanceUnit("nautical mile", "nm", 1852.216);
    public static final DistanceUnit MILE = new DistanceUnit("mile", "mi", 1609.344);

    public DistanceUnit(String name, String abbreviation, Double unitToBase) {
        super(name, abbreviation, unitToBase);
    }
}
