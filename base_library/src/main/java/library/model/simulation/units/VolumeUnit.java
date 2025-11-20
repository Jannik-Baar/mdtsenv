package library.model.simulation.units;

/**
 * Units of measurement for volume
 */
public class VolumeUnit extends SimulationUnit {

    public static final VolumeUnit CUBICMETER = new VolumeUnit("cubicmeter", "m^3", 1.0);
    public static final VolumeUnit LITER = new VolumeUnit("liter", "l", 0.001);
    public static final VolumeUnit MILLILITER = new VolumeUnit("milliliter", "ml", Math.pow(10, -6));
    public static final VolumeUnit CUBICFEET = new VolumeUnit("cubicfeet", "ft^3", 0.028316846592);
    public static final VolumeUnit FLUIDOUNCE = new VolumeUnit("fluid ounce", "fl. oz.", 0.00003);

    public VolumeUnit(String name, String abbreviation, Double unitToBase) {
        super(name, abbreviation, unitToBase);
    }

    public VolumeUnit() {
    }
}
