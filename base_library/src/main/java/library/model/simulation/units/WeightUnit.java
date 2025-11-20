package library.model.simulation.units;

/**
 * Units of measurement for weight
 */
public class WeightUnit extends SimulationUnit {

    public static final WeightUnit GRAM = new WeightUnit("gram", "g", 1.0);
    public static final WeightUnit KILOGRAM = new WeightUnit("kilo gram", "kg", 1000.0);
    public static final WeightUnit TON = new WeightUnit("ton", "T", 1000000.0);
    public static final WeightUnit OUNCE = new WeightUnit("ounce", "oz", 0.035274);

    public WeightUnit() {
    }

    public WeightUnit(String name, String abbreviation, Double unitToBase) {
        super(name, abbreviation, unitToBase);
    }
}
