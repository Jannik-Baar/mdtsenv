package library.services.utils;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

public class ReflectionUtils {

    /**
     * generates a list of all the classes fields, including all superclasses.
     *
     * @param classOfSimObject Class for which the list is going to be generated
     * @return list of all attributes in the class
     */
    public static ArrayList<Field> getFieldsOfClass(Class<?> classOfSimObject) {
        ArrayList<Field> fields = new ArrayList<>(Arrays.asList(classOfSimObject.getDeclaredFields()));
        Class<?> superClass = classOfSimObject.getSuperclass();
        while (true) {
            fields.addAll(Arrays.asList(superClass.getDeclaredFields()));
            if (superClass.getSuperclass() == null) {
                break;
            } else {
                superClass = superClass.getSuperclass();
            }
        }
        return fields;
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

    public static ArrayList<Field> getAllFieldsOfClass(Class clazz) {
        ArrayList<Field> fields = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));
        Class superClass = clazz.getSuperclass();
        while (true) {
            fields.addAll(Arrays.asList(superClass.getDeclaredFields()));
            if (superClass.getSuperclass() == null) {
                break;
            } else {
                superClass = superClass.getSuperclass();
            }
        }
        return fields;
    }

    public static ArrayList<Field> getAllFieldsOfClass(Object object) {
        return getAllFieldsOfClass(object.getClass());
    }

}
