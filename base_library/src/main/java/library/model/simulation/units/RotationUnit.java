package library.model.simulation.units;

/**
 * Units of measurement for rotation
 */
public class RotationUnit extends SimulationUnit {

    public RotationUnit() {
    }

    public static final RotationUnit DEGREE = new RotationUnit("degree", "°", 1.0);
    public static final RotationUnit RADIANT = new RotationUnit("radiant", "rad", (180 / Math.PI));

    public RotationUnit(String name, String abbreviation, Double unitToBase) {
        super(name, abbreviation, unitToBase);
    }
}
