package library.model.simulation.units;

/**
 * Units of measurement for temperature
 */
public class TemperatureUnit extends SimulationUnit {

    public static final TemperatureUnit KELVIN = new TemperatureUnit("kelvin", "K", null);
    public static final TemperatureUnit CELSIUS = new TemperatureUnit("celsius", "°C", null);

    public TemperatureUnit() {
    }

    public static final TemperatureUnit FAHRENHEIT = new TemperatureUnit("fahrenheit", "°F", null);

    public TemperatureUnit(String name, String abbreviation, Double unitToBase) {
        super(name, abbreviation, unitToBase);
    }

    public Double getUnitToBase() {
        throw new IllegalArgumentException("Factor not applicable for temperature units.");
    }

    public Double getBaseToUnit() {
        throw new IllegalArgumentException("Factor not applicable for temperature units.");
    }

    public static Double convert(SimulationUnit originalUnit, double originalValue, SimulationUnit targetUnit) {
        if (originalUnit.getClass() != TemperatureUnit.class || targetUnit.getClass() != TemperatureUnit.class) {
            return SimulationUnit.convert(originalUnit, originalValue, targetUnit);
        }
        if (originalUnit.equals(targetUnit)) {
            return originalValue;
        }
        if (originalUnit.equals(TemperatureUnit.KELVIN)) {
            if (targetUnit.equals(TemperatureUnit.CELSIUS)) {
                return originalValue - 273.15;
            } else if (targetUnit.equals(TemperatureUnit.FAHRENHEIT)) {
                return (originalValue - 273.15) * (9 / 5) + 32;
            }
        } else if (originalUnit.equals(TemperatureUnit.CELSIUS)) {
            if (targetUnit.equals(TemperatureUnit.FAHRENHEIT)) {
                return (originalValue * (9 / 5)) + 32;
            } else if (targetUnit.equals(TemperatureUnit.KELVIN)) {
                return originalValue + 273.15;
            }
        } else if (originalUnit.equals(TemperatureUnit.FAHRENHEIT)) {
            if (targetUnit.equals(TemperatureUnit.CELSIUS)) {
                return (originalValue - 32) * (5 / 9);
            } else if (targetUnit.equals(TemperatureUnit.KELVIN)) {
                return (originalValue - 32) * (5 / 9) + 273.15;
            }
        } else {
            throw new ClassCastException("Inconvertable Temperature types.");
        }
        throw new ClassCastException("Inconvertable Temperature types.");
    }
}
