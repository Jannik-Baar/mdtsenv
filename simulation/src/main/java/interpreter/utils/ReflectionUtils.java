package interpreter.utils;

import com.google.common.primitives.Primitives;
import library.model.simulation.objects.SimulationObject;
import library.model.simulation.SimulationProperty;
import library.model.simulation.SimulationSuperClass;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class ReflectionUtils {

    /**
     * generates a list of all the classes fields, including all superclasses.
     *
     * @param classOfSimObject Class for which the list is going to be generated
     * @return list of all attributes in the class
     */
    public static ArrayList<Field> getFieldsOfClass(Class<?> classOfSimObject) {
        ArrayList<Field> allObjectFields = new ArrayList<>(
            Arrays.asList(classOfSimObject.getDeclaredFields())
        );
        Class<?> superClass = classOfSimObject.getSuperclass();
        while (superClass != null && !superClass.equals(SimulationSuperClass.class)) {
            allObjectFields.addAll(Arrays.asList(superClass.getDeclaredFields()));
            superClass = superClass.getSuperclass();
        }
        return allObjectFields;
    }

    /**
     * TODO write documentation
     *
     * @param clazz
     * @param fieldName
     * @return
     */
    public static Field getFieldOfClassByName(Class<?> clazz, String fieldName) {
        return getFieldFromFieldListByName(getFieldsOfClass(clazz), fieldName);
    }

    /**
     * TODO write documentation
     * @param fieldList
     * @param fieldName
     * @return
     */
    public static Field getFieldFromFieldListByName(List<Field> fieldList, String fieldName) {
        return fieldList.stream()
                        .filter(f -> f.getName().equalsIgnoreCase(fieldName))
                        .findFirst()
                        .orElseThrow();
    }

    /**
     * TODO write documentation
     *
     * @param clazz
     * @param fieldPath
     * @return
     */
    public static Field getDeepFieldOfClassByName(Class<? extends SimulationObject> clazz, String fieldPath) {
        String[] attributeNamesSplitByDot = fieldPath.split("\\.");

        // get the field representing the attribute inside the object
        Field attributeField = ReflectionUtils.getFieldOfClassByName(clazz, attributeNamesSplitByDot[0]);

        // attributes are always of type SimulationAttribute<T>, we therefore have to get the actual generic type
        Class<?> attributeType = ReflectionUtils.getGenericTypeOfField(attributeField);

        // if the attribute is a compound attribute (indicated by attribute names chained with a dot in between)
        if (attributeNamesSplitByDot.length > 1) {
            // ... we have to get we have to go along the data structure according to the chained name until we arrive at the final field
            for (int i = 1; i < attributeNamesSplitByDot.length; i++) {
                String attributeName = attributeNamesSplitByDot[i];
                attributeField = ReflectionUtils.getFieldOfClassByName(attributeType, attributeName);
            }
        }
        return attributeField;
    }

    /**
     * TODO write documentation
     *
     * @param simulationObject
     * @param fieldPath
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    @SuppressWarnings("unchecked")
    public static Map.Entry<Object, Field> getDeepObjectAndFieldByPath(SimulationObject simulationObject, String fieldPath) throws IllegalAccessException, InvocationTargetException, InstantiationException {

        String[] attributeNamesSplitByDot = fieldPath.split("\\.");

        Object object = simulationObject;
        Class<?> clazz = object.getClass();
        String attributeName = attributeNamesSplitByDot[0];
        Field attributeField = ReflectionUtils.getFieldOfClassByName(clazz, attributeNamesSplitByDot[0]);
        attributeField.setAccessible(true);

        Class<?> attributeType = ReflectionUtils.getGenericTypeOfField(attributeField);

        if (attributeField.get(object) == null) {
            attributeField.setAccessible(true);
            SimulationProperty newSimulationProperty = (SimulationProperty) getEmptyConstructorIfPresent(attributeField.getType()).newInstance();
            newSimulationProperty.setName(attributeName);
            if (Primitives.isWrapperType(attributeType)) {
                // newSimulationAttribute.setSingleValue(0);
            } else {
                newSimulationProperty.setSingleValue(getEmptyConstructorIfPresent(attributeType).newInstance());
            }
            attributeField.set(object, newSimulationProperty);
        }

        // if the attribute is a compound attribute (indicated by attribute names chained with a dot in between)
        if (attributeNamesSplitByDot.length > 1) {
            // ... we have to get we have to go along the data structure according to the chained name until we arrive at the final field
            for (int i = 1; i < attributeNamesSplitByDot.length; i++) {
                attributeName = attributeNamesSplitByDot[i];

                if (attributeField.get(object) == null) {
                    attributeField.setAccessible(true);
                    SimulationProperty newSimulationProperty = (SimulationProperty) getEmptyConstructorIfPresent(attributeField.getType()).newInstance();
                    newSimulationProperty.setName(attributeName);
                    if (Primitives.isWrapperType(attributeType)) {
                        // do we have to do something for primitive wrapper types?
                    } else {
                        newSimulationProperty.setSingleValue(getEmptyConstructorIfPresent(attributeType).newInstance());
                    }
                    attributeField.set(object, newSimulationProperty);
                }

                SimulationProperty simAttribute = (SimulationProperty) attributeField.get(object);
                object = simAttribute.getValue();
                attributeField = ReflectionUtils.getFieldOfClassByName(attributeType, attributeName);
            }
        }
        return Map.entry(object, attributeField);
    }

    /**
     * Tries to get the object that is the value of the field in the given object by using a getter method.
     *
     * @param field  field of which the value should be returned
     * @param object object from which the attribute value should be got
     * @return
     */
    public static Object getValueObjectFromField(Field field, Object object) {
        try {
            String property = field.getName();
            Method method = new PropertyDescriptor(property, object.getClass(), "is" + Character.toUpperCase(property.charAt(0)) + property.substring(1), null).getReadMethod();
            if (method == null) {
                return null;
            }
            return method.invoke(object);
        } catch (IllegalAccessException | InvocationTargetException | IntrospectionException e) {
            return null;
        }
    }

    /**
     * TODO write documentation
     *
     * @param genericField
     * @return
     */
    public static Class<?> getGenericTypeOfField(Field genericField) {
        ParameterizedType fieldParamType = (ParameterizedType) genericField.getGenericType();
        Type actualType = fieldParamType.getActualTypeArguments()[0];
        return (Class<?>) actualType;
    }

    /**
     * TODO write documentation
     *
     * @param object
     * @param stopAt
     * @return
     */
    public static Stack<Class<?>> getInheritanceStackOfObject(Object object, Class stopAt) {
        if (object == null) {
            throw new NullPointerException("param 'object' should not be null");
        }

        return getInheritanceStackOfClass(object.getClass(), stopAt);
    }

    /**
     * TODO write documentation
     *
     * @param object
     * @return
     */
    public static Stack<Class<?>> getInheritanceStackOfObject(Object object) {
        if (object == null) {
            throw new NullPointerException("param 'object' should not be null");
        }

        return getInheritanceStackOfObject(object, null);
    }

    public static Stack<Class<?>> getInheritanceStackOfClass(Class clazz, Class stopAt) {
        // create a stack of classes representing the inheritance structure
        Stack<Class<?>> classes = new Stack<>();
        classes.push(clazz);
        while (classes.peek().getSuperclass() != null) {
            Class<?> superClass = classes.peek().getSuperclass();
            classes.push(superClass);
            if (superClass != null && superClass == stopAt) {
                break;
            }
        }
        return classes;
    }

    public static Stack<Class<?>> getInheritanceStackOfClass(Class clazz) {
        return getInheritanceStackOfClass(clazz, null);
    }



    /**
     * TODO write documentation
     *
     * @param clazz
     * @return
     */
    private static Constructor<?> getEmptyConstructorIfPresent(Class<?> clazz) {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        Constructor<?> constructor = null;
        for (int i = 0; i < constructors.length; i++) {
            if (constructors[i].getGenericParameterTypes().length == 0) {
                constructor = constructors[i];
            }
        }
        return constructor;
    }
}