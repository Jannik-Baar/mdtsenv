package library.model.simulation.units;

/**
 * Units of measurement for pressure
 */
public class PressureUnit extends SimulationUnit {

    public PressureUnit() {
    }

    public static final PressureUnit PASCAL = new PressureUnit("pascal", "pa", 1.0);
    public static final PressureUnit TORR = new PressureUnit("torr", "mmHg", 133.322);
    public static final PressureUnit PSI = new PressureUnit("psi", "psi", 6894.757293168);
    public static final PressureUnit bar = new PressureUnit("bar", "bar", 100000.0);

    public PressureUnit(String name, String abbreviation, Double unitToBase) {
        super(name, abbreviation, unitToBase);
    }
}
