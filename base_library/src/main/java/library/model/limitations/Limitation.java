package library.model.limitations;

import library.model.simulation.SimulationProperty;
import library.model.simulation.SimulationSuperClass;
import library.model.simulation.enums.NumericalRestrictionType;
import library.model.simulation.objects.SimulationObject;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
public class Limitation<T> extends SimulationSuperClass {

    @XmlElement
    private ArrayList<SimulationProperty<T>> limitedProperties = new ArrayList<>();

    @XmlElement
    private NumericalRestrictionType numericalRestrictionType;

    private List<SimulationObject> limitedSimulationObjects = new ArrayList<>();

    public Limitation() {
        super();
    }

    protected Limitation(SimulationProperty<T> limitedProperty, NumericalRestrictionType numericalRestrictionType) {
        super();
        this.limitedProperties.add(limitedProperty);
        this.numericalRestrictionType = numericalRestrictionType;
    }

    protected Limitation(SimulationProperty<T> limitedProperty) {
        super();
        this.limitedProperties.add(limitedProperty);
        this.numericalRestrictionType = NumericalRestrictionType.NOTNUMERICAL;
    }

    public Limitation<T> addLimitedProperty(SimulationProperty<T> property) {
        this.limitedProperties.add(property);
        return this;
    }
//
//    @NotNull
//    @Contract("_, _, _ -> new")
//    public static Restriction<Short> getNumericalRestriction(String name, Short value, NumericalRestrictionType numericalRestrictionType, SimulationUnit unit) {
//        return new Restriction<>(name, new SimulationProperty<Short>(true, true, unit, value, "restVal"), numericalRestrictionType);
//    }
//
//    @NotNull
//    @Contract("_, _, _ -> new")
//    public static Restriction<Integer> getNumericalRestriction(String name, Integer value, NumericalRestrictionType numericalRestrictionType, SimulationUnit unit) {
//        return new Restriction<>(name, new SimulationProperty<Integer>(true, true, unit, value, "restVal"), numericalRestrictionType);
//    }
//
//    @NotNull
//    @Contract("_, _, _ -> new")
//    public static Restriction<Long> getNumericalRestriction(String name, Long value, NumericalRestrictionType numericalRestrictionType, SimulationUnit unit) {
//        return new Restriction<>(name, new SimulationProperty<Long>(true, true, unit, value, "restVal"), numericalRestrictionType);
//    }
//
//    @NotNull
//    @Contract("_, _, _ -> new")
//    public static Restriction<Float> getNumericalRestriction(String name, Float value, NumericalRestrictionType numericalRestrictionType, SimulationUnit unit) {
//        return new Restriction<>(name, new SimulationProperty<Float>(true, true, unit, value, "restVal"), numericalRestrictionType);
//    }
//
//    @NotNull
//    @Contract("_, _, _ -> new")
//    public static Restriction<Double> getNumericalRestriction(String name, Double value, NumericalRestrictionType numericalRestrictionType, SimulationUnit unit) {
//        return new Restriction<>(name, new SimulationProperty<Double>(true, true, unit, value, "restVal"), numericalRestrictionType);
//    }
//
//    @NotNull
//    @Contract("_, _, _ -> new")
//    public static Restriction<Boolean> getBooleanRestriction(String name, Boolean value) {
//        return new Restriction<>(name, new SimulationProperty<Boolean>(true, true, NoUnit.get(), value, "restVal"));
//    }
//

}
