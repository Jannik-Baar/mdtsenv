package library.model.dto.conditions;

import library.model.simulation.objects.SimulationObject;
import library.model.simulation.SimulationProperty;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.HashMap;

/**
 * Compares Attributes with values and each other
 *
 * @param <T>
 */
@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class CompareAttributeCondition<T> extends TerminationCondition<T> {

    private SimulationProperty<T> property;

    private T valueToCompare;

    private SimulationProperty<T> propertyToCompare;

    @XmlElement
    private CompareOperation comparator;

    @XmlElementWrapper(name = "propertyIdToSimulationObjectMap")
    private final HashMap<String, SimulationObject> propertyIdToSimulationObjectMap = new HashMap<>();

    public CompareAttributeCondition(SimulationObject propertyParentObject,
                                     SimulationProperty<T> property,
                                     T valueToCompare,
                                     CompareOperation comparator) {
        if (propertyParentObject != null) {
            this.propertyIdToSimulationObjectMap.put(property.getId(), propertyParentObject);
        }
        this.property = property;
        this.valueToCompare = valueToCompare;
        this.comparator = comparator;
        this.propertyToCompare = null;
        this.property.setPublish(true);
    }

    public CompareAttributeCondition(SimulationObject propertyParentObject,
                                     SimulationProperty<T> property,
                                     SimulationObject propertyToCompareParentObject,
                                     SimulationProperty<T> propertyToCompare,
                                     CompareOperation comparator) {
        if (propertyParentObject != null) {
            this.propertyIdToSimulationObjectMap.put(property.getId(), propertyParentObject);
        }
        if (propertyToCompareParentObject != null) {
            this.propertyIdToSimulationObjectMap.put(propertyToCompare.getId(), propertyToCompareParentObject);
        }
        this.property = property;
        this.propertyToCompare = propertyToCompare;
        this.comparator = comparator;
        this.valueToCompare = null;
        this.property.setPublish(true);
        this.propertyToCompare.setPublish(true);
    }

    @Override
    public boolean conditionIsMet() {
        Comparable<T> firstValue = (Comparable<T>) property.getValue();
        T secondValue;
        if (valueToCompare != null) {
            secondValue = valueToCompare;
        } else if (propertyToCompare != null) {
            secondValue = propertyToCompare.getValue();
        } else {
            return false;
        }

        int compareValue = firstValue.compareTo(secondValue);

        return switch (comparator) {
            case EQUAL -> compareValue == 0;
            case LESS -> compareValue > 0;
            case GREATER -> compareValue < 0;
            case LESS_OR_EQUAL -> compareValue >= 0;
            case GREATER_OR_EQUAL -> compareValue <= 0;
            case NOT_EQUAL -> compareValue != 0;
            case AND -> (Boolean) firstValue && (Boolean) secondValue;
            case OR -> (Boolean) firstValue || (Boolean) secondValue;
        };
    }

    public HashMap<String, SimulationObject> getPropertyIdToSimulationObjectMap() {
        return this.propertyIdToSimulationObjectMap;
    }

}
