package library.model.simulation.units;

/**
 * Unit of measurement for acceleration
 */
public class AccelerationUnit extends SimulationUnit {

    public static final AccelerationUnit METERSPERSECONDSSQUARED = new AccelerationUnit("meters per second squared", "m/s^2", 1.0);

    public AccelerationUnit() {
        super();

    }

    public AccelerationUnit(String name, String abbreviation, Double unitToBase) {
        super(name, abbreviation, unitToBase);
    }
}
