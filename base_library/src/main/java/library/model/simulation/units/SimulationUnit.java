package library.model.simulation.units;

import java.util.Objects;

/**
 * Provides necessary properties to represent and convert units in simulation
 */
public abstract class SimulationUnit {

    public SimulationUnit() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    private String name;
    private String symbol;
    private double factor;
    /**
     * Factor to convert this unit to the baseUnit of the current unit class
     */
    private Double unitToBase;

    /**
     * Two units are equal if name abbreviation and conversion factors are equal
     *
     * @param o Object to compare to this
     * @return true if equal false if not
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimulationUnit that = (SimulationUnit) o;
        return name.equals(that.name) && symbol.equals(that.symbol) && Objects.equals(unitToBase, that.unitToBase);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, symbol, unitToBase);
    }

    public SimulationUnit(String name, String symbol, Double unitToBase) {
        this.name = name;
        this.symbol = symbol;
        this.unitToBase = unitToBase;
    }

    public Double getUnitToBase() {
        return unitToBase;
    }

    public void setUnitToBase(Double unitToBase) {
        this.unitToBase = unitToBase;
    }

    /**
     * Inversion of unitToBase
     * @return Inverted factor of base unit to this unit
     */
    public Double getBaseToUnit() {
        return 1 / unitToBase;
    }

    public static Double convert(SimulationUnit originalUnit, double originalValue, SimulationUnit targetUnit) throws ClassCastException {
        if (originalUnit.getClass() != targetUnit.getClass()) {
            throw new ClassCastException("Units to convert do not belong to same category of Units.");
        } else if (originalUnit.getClass() == TemperatureUnit.class) {
            return TemperatureUnit.convert(originalUnit, originalValue, targetUnit);
        }
        return originalValue * originalUnit.unitToBase * (1 / targetUnit.unitToBase);
    }
}
