package library.model.simulation;

import com.google.common.primitives.Primitives;
import library.model.simulation.enums.SimulationPropertyType;
import library.model.simulation.enums.SimulationVisibilityKind;
import library.model.simulation.units.SimulationUnit;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Attaches different attributes that go alongside a value
 *
 * @param <T>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class SimulationProperty<T> {

    @XmlID
    private String id;

    @XmlElement
    private SimulationPropertyType type;

    @XmlElement
    private SimulationUnit unit;

    @XmlElement
    private List<T> value = new ArrayList<T>();

    @XmlElement
    private String name;

    @XmlElement
    private String dataType;

    @XmlElement
    private boolean publish;

    // TODO REFACTOR: Remove subscribe from here and every usage, since it isn't actively used anymore. Subscriptions moved to the 'observedClasses' part of a scenario / object
    @XmlElement
    private boolean subscribe;

    // if the value of the SimulationAttribute is an ArrayList or a HashMap, the type of the data inside it should be saved here
    private Class listType;

    private int currentIndex;

    public SimulationProperty() {
        super();
        setID();
    }

    public SimulationProperty(SimulationUnit unit, T value, String name) {
        setID();
        this.currentIndex = 0;
        this.type = SimulationPropertyType.SINGLE;
        this.publish = false;
        this.subscribe = false;
        this.unit = unit;
        this.value = new ArrayList<>();
        this.value.add(value);
        this.dataType = value.getClass().getName();
        this.name = name;
    }

    public SimulationProperty(boolean publish, boolean subscribe, SimulationUnit unit, T value, String name) {
        setID();
        this.currentIndex = 0;
        this.type = SimulationPropertyType.SINGLE;
        this.subscribe = subscribe;
        this.publish = publish;
        this.unit = unit;
        this.value = new ArrayList<>();
        this.value.add(value);
        this.dataType = value.getClass().getName();
        this.name = name;
    }

    public SimulationProperty(boolean publish, boolean subscribe, SimulationUnit unit, T value, String name, SimulationPropertyType type) {
        setID();
        this.currentIndex = 0;
        this.type = type;
        this.publish = publish;
        this.subscribe = subscribe;
        this.unit = unit;
        this.value = new ArrayList<>();
        this.value.add(value);
        this.dataType = value.getClass().getName();
        this.name = name;
    }

    public SimulationProperty(boolean publish, boolean subscribe, SimulationUnit unit, ArrayList<T> value, String name) {
        setID();
        this.currentIndex = 0;
        this.type = SimulationPropertyType.LIST;
        this.publish = subscribe;
        this.subscribe = subscribe;
        this.unit = unit;
        this.value = value;
        this.dataType = value.getClass().getName();
        this.name = name;
    }

    public static SimulationProperty<Double> getRangedSimulationAttribute(boolean publish, boolean subscribe, SimulationUnit unit, double startValue, double endValue, double stepSize, String name) {
        return new SimulationProperty<>(publish, subscribe, unit, startValue, endValue, stepSize, name);
    }

    private SimulationProperty(boolean publish, boolean subscribe, SimulationUnit unit, double startValue, double endValue, double stepSize, String name) {
        super();
        this.currentIndex = 0;
        this.type = SimulationPropertyType.RANGE;
        this.publish = publish;
        this.subscribe = subscribe;
        this.unit = unit;
        setRangedValue(startValue, endValue, stepSize);
        this.name = name;
    }

    public SimulationPropertyType getAttributeType() {
        return type;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public boolean isPublish() {
        return publish;
    }

    public boolean isSubscribe() {
        return subscribe;
    }

    public SimulationUnit getUnit() {
        return unit;
    }

    public T getValue() {
        return value.get(this.currentIndex);
    }

    public T next() {
        if ((this.type == SimulationPropertyType.RANGE || this.type == SimulationPropertyType.LIST) && currentIndex < this.value.size() - 1) {
            currentIndex++;
        }
        return getValue();
    }

    public T previous() {
        if ((this.type == SimulationPropertyType.RANGE || this.type == SimulationPropertyType.LIST) && currentIndex > 0) {
            currentIndex--;
        }
        return getValue();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPublish(boolean publish) {
        this.publish = publish;
    }

    public void setSubscribe(boolean subscribe) {
        this.subscribe = subscribe;
    }

    private void setID() {
        this.id = UUID.randomUUID().toString();
    }

    public void setSingleValue(T value) {
        this.type = SimulationPropertyType.SINGLE;
        if (this.value.size() > 0) {
            this.value.set(0, value);
        } else {
            this.value.add(value);
        }
        this.dataType = value.getClass().getName();
    }

    public void setRangedValue(double startValue, double endValue, double stepSize) {
        this.type = SimulationPropertyType.RANGE;
        ArrayList<T> values = new ArrayList<>();
        Double istart = Math.min(startValue, endValue);
        Double iend = Math.max(startValue, endValue);
        values.add((T) istart);
        while ((Double) (values.get(values.size() - 1)) < iend) {
            Double next = (Double) (values.get(values.size() - 1)) + stepSize;
            if (next <= iend) {
                values.add((T) next);
            } else {
                break;
            }
        }
        if (startValue > endValue) {
            Collections.reverse(values);
        }
        this.value = values;
        this.dataType = Double.class.getName();
    }

    public void setListType(Class clazz) {
        listType = clazz;
    }

    public Class getListType() {
        return listType;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SimulationProperty<?> that = (SimulationProperty<?>) o;
        return publish == this.publish &&
               this.subscribe == this.subscribe &&
               currentIndex == that.currentIndex &&
               Objects.equals(id, that.id) &&
               type == that.type &&
               Objects.equals(dataType, that.dataType) &&
               unit == that.unit &&
               Objects.equals(value, that.value) &&
               Objects.equals(name, that.name);
    }

    @Override
    public String toString() {
        if (Primitives.isWrapperType(this.getValue().getClass()) || (this.getValue().getClass() == String.class)) {
            return String.format("\"" + this.getName() + "\":\"" + this.value.get(0).toString() + "\"");
        }
        return "";
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setType(SimulationPropertyType type) {
        this.type = type;
    }

    public void setUnit(SimulationUnit unit) {
        this.unit = unit;
    }

    public void setValue(List<T> value) {
        this.value = value;
    }

    public void setValue(T value) {
        ArrayList<T> t = new ArrayList<>();
        t.add(value);
        this.value = t;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

}
