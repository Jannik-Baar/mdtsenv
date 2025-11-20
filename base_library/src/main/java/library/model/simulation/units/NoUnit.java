package library.model.simulation.units;

/**
 * Default Unit
 */
public class NoUnit extends SimulationUnit {

    private static NoUnit instance = null;

    public static NoUnit get() {
        if (instance == null) {
            instance = new NoUnit("no unit", "nau", 1.0);
        }
        return instance;
    }

    public NoUnit() {
    }

    public NoUnit(String name, String abbreviation, Double unitToBase) {
        super(name, abbreviation, unitToBase);
    }
}
