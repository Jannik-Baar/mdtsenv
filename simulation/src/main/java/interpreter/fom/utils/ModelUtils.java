package interpreter.fom.utils;

import com.google.common.reflect.ClassPath;
import interpreter.utils.ReflectionUtils;
import library.model.simulation.SimulationComponent;
import library.model.simulation.objects.SimulationObject;
import library.model.simulation.SimulationProperty;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class ModelUtils {

    private final static String MODEL_PACKAGE = "library.model";
    private static List<Class<?>> classes = null;

    private ModelUtils() {}

    /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
     */
    @SuppressWarnings("UnstableApiUsage")
    public static List<Class<?>> getClasses() throws IOException {
        if (classes == null) {
            ClassPath cp = ClassPath.from(Thread.currentThread().getContextClassLoader());
            classes = cp.getTopLevelClassesRecursive(MODEL_PACKAGE)
                        .stream()
                        .map(info -> info.load())
                        .collect(Collectors.toList());
        }
        return classes;
    }

    public static Class<?> getClass(String classname) throws IOException {
        List<Class<?>> classes = getClasses();
        return classes.parallelStream()
                      .filter(c -> c.getSimpleName().equals(classname))
                      .findFirst()
                      .orElse(null);
    }

    /**
     * Generate an identifier for the combination of class and publish/subscribe settings of attributes in the object
     *
     * @param object object the identifier will be based on
     */
    public static String getClassIdentifier(Object object) {

        StringBuilder identifier = new StringBuilder();
        ArrayList<Field> fieldList = ReflectionUtils.getFieldsOfClass(object.getClass());

        for (Field field : fieldList) {
            Object attributeObject = ReflectionUtils.getValueObjectFromField(field, object);
            if (attributeObject == null) {
                continue;
            }
            if (attributeObject instanceof SimulationProperty) {
                SimulationProperty<?> attribute = (SimulationProperty<?>) attributeObject;
                identifier.append(attribute.isPublish());
                identifier.append(attribute.isSubscribe());
                Object attributeValue = attribute.getValue();
                if (attributeValue instanceof ArrayList) {
                    for (Object innerObject : (ArrayList<?>) attribute.getValue()) {
                        identifier.append(getClassIdentifier(innerObject));
                    }
                }

            } else if (attributeObject instanceof SimulationComponent) {
                getClassIdentifier(attributeObject);
            } else if (attributeObject instanceof SimulationObject) {
                getClassIdentifier(attributeObject);
            } else if (attributeObject instanceof ArrayList) {
                for (Object listObject : (ArrayList<?>) attributeObject) {
                    getClassIdentifier(listObject);
                }
            }
        }

        // processedObjects.put(object, identifier.toString());
        return identifier.toString();
    }

    public static String getHashedClassIdentifier(Object object) {
        String classIdentifier = getClassIdentifier(object);
        return String.valueOf(classIdentifier.hashCode());
    }

}
