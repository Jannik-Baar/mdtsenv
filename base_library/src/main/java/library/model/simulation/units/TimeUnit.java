package library.model.simulation.units;

/**
 * Units of measurement for time
 */
public class TimeUnit extends SimulationUnit {

    public static final TimeUnit SECOND = new TimeUnit("second", "s", 1.0);
    public static final TimeUnit MILLISECOND = new TimeUnit("millisecond", "ms", 0.001);
    public static final TimeUnit MINUTE = new TimeUnit("minute", "min", 60.0);
    public static final TimeUnit HOUR = new TimeUnit("hour", "h", 3600.0);

    public TimeUnit() {
    }

    public static final TimeUnit DAY = new TimeUnit("day", "d", 86400.0);

    public TimeUnit(String name, String abbreviation, Double unitToBase) {
        super(name, abbreviation, unitToBase);
    }
}
