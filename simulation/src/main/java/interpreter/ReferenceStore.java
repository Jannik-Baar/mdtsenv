package interpreter;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Multimap;
import com.google.common.primitives.Primitives;
import interpreter.fom.model.FOM;
import interpreter.fom.model.FOMAttribute;
import interpreter.fom.model.FOMInteraction;
import interpreter.fom.model.FOMObjectClass;
import interpreter.utils.ReflectionUtils;
import library.model.simulation.objects.ActiveSimulationObject;
import library.model.simulation.SimulationComponent;
import library.model.simulation.SimulationInteraction;
import library.model.simulation.objects.SimulationObject;
import library.model.simulation.SimulationProperty;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;

import static interpreter.utils.ReflectionUtils.getFieldsOfClass;
import static interpreter.utils.ReflectionUtils.getValueObjectFromField;

public class ReferenceStore {

    private final static String FOM_HLA_OBJECT_ROOT = "HLAobjectRoot";
    private final static String FOM_HLA_INTERACTION_ROOT = "HLAinteractionRoot";

    private ActiveSimulationObject simulationObject;
    private String simulationObjectType;
    private FOM fom;

    private BiMap<String, FOMObjectClass> fomPathToFomObjectClassBiMap;
    private BiMap<String, FOMAttribute> fomPathToFomAttributeBiMap;
    private BiMap<String, FOMInteraction> fomPathToFomInteractionBiMap;

    private Multimap<String, SimulationObject> fomPathToSimulationObjectMap;
    private Multimap<String, SimulationProperty<?>> fomPathToSimulationAttributeMap;
    private Multimap<String, SimulationInteraction> fomPathToSimulationInteractionMap;

    private BiMap<String, SimulationObject> uuidToSimulationObjectBiMap;
    private BiMap<String, SimulationProperty<?>> uuidToSimulationAttributeBiMap;
    private BiMap<String, SimulationInteraction> uuidToSimulationInteractionBiMap;

    public ReferenceStore() {

    }

    public void initialize() {
        if (simulationObject == null) {
            throw new NullPointerException("simulationObject has to be set before initialize is called");
        }
        if (fom == null) {
            throw new NullPointerException("fom has to be set before initialize is called");
        }

        buildFomPathMaps();
        buildSimulationMaps();
        buildUUIDMaps();
    }

    private void buildFomPathMaps() {

        // objectClasses and attributes
        for (FOMObjectClass fomObjectClass : fom.getObjectClasses()) {
            buildFomPathToFomObjectClassMap(fomObjectClass);
        }

        // interactions
        for (FOMInteraction fomInteraction : fom.getInteractions()) {
            buildFomPathToFomInteractionClassMap(fomInteraction);
        }
    }

    private void buildFomPathToFomObjectClassMap(FOMObjectClass fomObjectClass) {
        if (fomObjectClass == null) {
            throw new NullPointerException("parameter 'fomObjectClass' should not be null");
        }

        StringBuilder fomPath = new StringBuilder(fomObjectClass.getName());

        // build the path part of the full path
        FOMObjectClass superClass = fomObjectClass.getSuperClass();
        while (superClass != null) {
            fomPath.insert(0, superClass.getName() + ".");
            superClass = superClass.getSuperClass();
        }

        // append the currently looked at objectClasses name as the identifying part of the full path
        this.fomPathToFomObjectClassBiMap.put(fomPath.toString(), fomObjectClass);

        // add every fomAttribute referenced by the currently looked at fomObjectClass prefixed with the previously build path
        for (FOMAttribute fomAttribute : fomObjectClass.getAttributes()) {
            this.fomPathToFomAttributeBiMap.put(fomPath + "." + fomAttribute.getName(), fomAttribute);
        }

        if (fomObjectClass.getSubClasses() != null && !fomObjectClass.getSubClasses().isEmpty()) {
            for (FOMObjectClass subClass : fomObjectClass.getSubClasses()) {
                buildFomPathToFomObjectClassMap(subClass);
            }
        } else {
            simulationObjectType = fomObjectClass.getName();
        }
    }

    private void buildFomPathToFomInteractionClassMap(FOMInteraction fomInteraction) {
        if (fomInteraction == null) {
            throw new NullPointerException("parameter 'fomInteraction' should not be null");
        }

        // build the path part of the full path
        StringBuilder fomPath = new StringBuilder(fomInteraction.getName());
        FOMInteraction superInteraction = fomInteraction.getSuperInteraction();
        while (superInteraction != null) {
            fomPath.insert(0, superInteraction.getName() + ".");
            superInteraction = superInteraction.getSuperInteraction();
        }

        // append the currently looked at interaction name as the identifying part of the full path
        this.fomPathToFomInteractionBiMap.put(fomPath.toString(), fomInteraction);
    }

    private void buildSimulationMaps() {

        if (this.simulationObject == null) {
            throw new NullPointerException("'simulationObject' has to be set before building the simulation maps");
        }

        fomPathToSimulationObjectMap = ArrayListMultimap.create();
        fomPathToSimulationAttributeMap = ArrayListMultimap.create();
        fomPathToSimulationInteractionMap = ArrayListMultimap.create();

        buildSimulationObjectMap(simulationObject);
    }

    private void buildSimulationObjectMap(SimulationObject simulationObject) {
        // create a stack of classes representing the inheritance structure
        Stack<Class<?>> classes = ReflectionUtils.getInheritanceStackOfObject(simulationObject, SimulationObject.class);

        // go through the constructed inheritance class structure stack
        FOMObjectClass lastVisitedObjectClass = null;
        StringBuilder fomPath = new StringBuilder(FOM_HLA_OBJECT_ROOT);
        while (!classes.isEmpty()) {
            Class<?> clazz = classes.pop();
            fomPath.append("." + clazz.getSimpleName());
            buildSimulationAttributeMap(simulationObject, clazz, fomPath.toString());
        }

        fomPathToSimulationObjectMap.put(fomPath.toString(), simulationObject);

        // add component attributes to the FOMObject that describes the actual class of the given simulation object
        if (simulationObject instanceof ActiveSimulationObject) {
            ArrayList<SimulationComponent> components = ((ActiveSimulationObject) simulationObject).getComponents();
            for (SimulationComponent component : components) {
                buildSimulationComponentAttributeMap(component, new ArrayList<>(), fomPath.toString());
            }
        }
    }

    private void buildSimulationAttributeMap(SimulationObject simulationObject, Class<?> clazz, String fomPath) {
        if (simulationObject == null) {
            throw new NullPointerException("parameter 'simulationObject' should not be null");
        }
        if (clazz == null) {
            throw new NullPointerException("parameter 'clazz' should not be null");
        }

        if (!clazz.isAssignableFrom(simulationObject.getClass())) {
            throw new InvalidParameterException("the given class has to be the class or a superclass of the class of the given object");
        }

        // get all fields of the given class
        Field[] cassFields = clazz.getDeclaredFields();

        try {

            // vor every field of the given object
            for (Field field : cassFields) {

                field.setAccessible(true);

                // get the value (as object) of the field currently looked at
                Object fieldValueObject = field.get(simulationObject);

                // get the type (as class) of the value that the field is holding
                if (fieldValueObject != null && fieldValueObject.getClass() == SimulationProperty.class) {
                    SimulationProperty<?> simulationProperty = (SimulationProperty<?>) fieldValueObject;
                    Class<?> attributeClass = simulationProperty.getValue().getClass();

                    if (attributeClass == ArrayList.class || attributeClass == HashMap.class) {
                        // TODO refactor (see comment @SimulationAttribute.class)
                        // attributeClass = simulationAttribute.getListType();
                        // if (attributeClass == null) {
                        //      // skip since the ListType was not given
                        //      continue;
                        // }

                        // 15.02.2022: skip lists completely for now
                        continue;
                    }

                    if (Primitives.isWrapperType(attributeClass) || attributeClass == String.class || attributeClass.isEnum()) {
                        fomPathToSimulationAttributeMap.put(fomPath + "." + simulationProperty.getName(), simulationProperty);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // TODO maybe do this already directly with the creation of the FOM
    private void buildSimulationComponentAttributeMap(SimulationComponent component, ArrayList<SimulationComponent> parentComponents, String fomPath) {
        if (simulationObject == null) {
            throw new NullPointerException("parameter 'simulationObject' should not be null");
        }
/*
        if (!component.isPublish() && !component.isSubscribe()) {
            // if the component neither publishes or subscribes we do not have to look for its attributes for FOM inclusion
            return;
        }
*/
        Class<?> componentClass = component.getClass();
        Field[] classFields = componentClass.getDeclaredFields();
        // String componentAttributeNamePrefix = parentComponents.stream()
        //                                                      .map(c -> c.getClass().getSimpleName())
        //                                                      .collect(Collectors.joining("."));

        // Use the component class name as prefix
        String componentAttributeNamePrefix = "." + componentClass.getSimpleName();

        buildSimulationAttributeMap(component, componentClass, fomPath + componentAttributeNamePrefix);

        // call this method recursively for every sub-component this component links to
        if (!component.getComponents().isEmpty()) {
            parentComponents.add(component);
            component.getComponents()
                     .stream()
                     .forEach(subComponent -> buildSimulationComponentAttributeMap(subComponent, parentComponents, fomPath));
        }
    }

    private void buildUUIDMaps() {

        if (simulationObject == null) {
            throw new NullPointerException("'simulationObject' has to be set before building the UUIDMaps");
        }

        uuidToSimulationObjectBiMap = HashBiMap.create();
        uuidToSimulationAttributeBiMap = HashBiMap.create();
        buildUUIDToObjectBiMap(simulationObject);

        // TODO implement proper use of interactions
        uuidToSimulationInteractionBiMap = HashBiMap.create();
    }

    private void buildUUIDToObjectBiMap(SimulationObject simulationObject) {
        ArrayList<Field> fieldList = getFieldsOfClass(simulationObject.getClass());

        uuidToSimulationObjectBiMap.put(simulationObject.getId(), simulationObject);

        for (Field aField : fieldList) {

            Object anObject = getValueObjectFromField(aField, simulationObject);
            if (anObject == null) {
                continue;
            }

            if (aField.getName().equals("components")) {

                @SuppressWarnings("unchecked") // since we checked the fields name beforehand, we can safely say that this cast is valid
                ArrayList<SimulationComponent> componentListAttribute = (ArrayList<SimulationComponent>) anObject;

                for (SimulationComponent component : componentListAttribute) {
                    buildUUIDToObjectBiMap(component);
                }

            } else if (anObject instanceof SimulationObject) {
                buildUUIDToObjectBiMap((SimulationObject) anObject);
            } else if (anObject instanceof SimulationProperty) {
                buildUUIDToAttributeBiMap((SimulationProperty) anObject);
            }
        }
    }

    private void buildUUIDToAttributeBiMap(SimulationProperty<?> property) {
        if (Interpreter.DATATYPE_MAP.containsKey((property).getValue().getClass().getSimpleName())
            || property.getValue() instanceof Enum) {

            //attribute.
            uuidToSimulationAttributeBiMap.put(property.getId(), property);

        } else {
            Object compositeAttributeValueObject = property.getValue();
            for (Field field : compositeAttributeValueObject.getClass().getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    if (field.get(compositeAttributeValueObject) != null && field.get(compositeAttributeValueObject).getClass() == SimulationProperty.class) {
                        buildUUIDToAttributeBiMap((SimulationProperty<?>) field.get(compositeAttributeValueObject));
                    }
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    // TODO proper error logging
                    e.printStackTrace();
                }
            }
        }
    }

    /********************************************
     *  CUSTOM GETTER (USING THE REFERENCE MAPS)
     ********************************************/
    public FOMObjectClass getFOMObjectForFOMPath(@NotNull String fomPath) {
        if (fomPath == null) {
            throw new IllegalArgumentException("param 'fomPath' should not be null");
        }
        return fomPathToFomObjectClassBiMap.get(fomPath);
    }

    public FOMAttribute getFOMAttributeForFOMPath(@NotNull String fomPath) {
        if (fomPath == null) {
            throw new IllegalArgumentException("param 'fomPath' should not be null");
        }
        return fomPathToFomAttributeBiMap.get(fomPath);
    }

    public String getFOMPathForFOMObject(@NotNull FOMObjectClass fomObject) {
        if (fomObject == null) {
            throw new IllegalArgumentException("param 'fomObject' should not be null");
        }
        return fomPathToFomObjectClassBiMap.inverse().get(fomObject);
    }

    public String getFOMPathForFOMAttribute(@NotNull FOMAttribute fomAttribute) {
        if (fomAttribute == null) {
            throw new IllegalArgumentException("param 'fomObject' should not be null");
        }
        return fomPathToFomAttributeBiMap.inverse().get(fomAttribute);
    }

    public FOMObjectClass getRootFOMObject() {
        return fom.getObjectClasses().get(0);
    }

    public Set<SimulationProperty<?>> getAllSimulationAttributes() {
        return uuidToSimulationAttributeBiMap.values();
    }

    public Set<String> getAllSimulationAttributeUUIDs() {
        return uuidToSimulationAttributeBiMap.keySet();
    }

    public Set<SimulationObject> getAllSimulationObjects() {
        return uuidToSimulationObjectBiMap.values();
    }

    public Set<String> getAllSimulationObjectUUIDs() {
        return uuidToSimulationObjectBiMap.keySet();
    }

    public SimulationProperty<?> getSimulationAttributeByUUID(String uuid) {
        return uuidToSimulationAttributeBiMap.get(uuid);
    }

    public String getFomPathForSimulationObject(SimulationObject simulationObject) {
        Optional<Map.Entry<String, SimulationObject>> entry = fomPathToSimulationObjectMap.entries()
                                                                                          .stream()
                                                                                          .filter(e -> e.getValue() == simulationObject)
                                                                                          .findFirst();
        return entry.orElse(null).getKey();
    }

    /******************************************
     *  GETTER & SETTER
     ******************************************/
    public FOM getFom() {
        return fom;
    }

    public void setFom(FOM fom) {
        this.fom = fom;

        if (fomPathToFomObjectClassBiMap == null) {
            this.fomPathToFomObjectClassBiMap = HashBiMap.create();
            this.fomPathToFomObjectClassBiMap.put(FOM_HLA_OBJECT_ROOT, new FOMObjectClass(FOM_HLA_OBJECT_ROOT));
        }
        if (fomPathToFomAttributeBiMap == null) {
            this.fomPathToFomAttributeBiMap = HashBiMap.create();
        }
        this.fomPathToFomObjectClassBiMap.get(FOM_HLA_OBJECT_ROOT).addSubClasses(fom.getObjectClasses());
        for (FOMObjectClass objectClass : fom.getObjectClasses()) {
            objectClass.setSuperClass(this.fomPathToFomObjectClassBiMap.get(FOM_HLA_OBJECT_ROOT));
        }

        if (fomPathToFomInteractionBiMap == null) {
            this.fomPathToFomInteractionBiMap = HashBiMap.create();
            this.fomPathToFomInteractionBiMap.put(FOM_HLA_INTERACTION_ROOT, new FOMInteraction(FOM_HLA_INTERACTION_ROOT));
        }
        this.fomPathToFomInteractionBiMap.get(FOM_HLA_INTERACTION_ROOT).addSubInteractions(fom.getInteractions());
        for (FOMObjectClass objectClass : fom.getObjectClasses()) {
            objectClass.setSuperClass(this.fomPathToFomObjectClassBiMap.get(FOM_HLA_OBJECT_ROOT));
        }
    }

    public ActiveSimulationObject getSimulationObject() {
        return simulationObject;
    }

    public void setSimulationObject(ActiveSimulationObject simulationObject) {
        this.simulationObject = simulationObject;
    }

    public BiMap<String, FOMObjectClass> getFomPathToFomObjectClassBiMap() {
        return fomPathToFomObjectClassBiMap;
    }

    public void setFomPathToFomObjectClassBiMap(BiMap<String, FOMObjectClass> fomPathToFomObjectClassBiMap) {
        this.fomPathToFomObjectClassBiMap = fomPathToFomObjectClassBiMap;
    }

    public BiMap<String, FOMAttribute> getFomPathToFomAttributeBiMap() {
        return fomPathToFomAttributeBiMap;
    }

    public void setFomPathToFomAttributeBiMap(BiMap<String, FOMAttribute> fomPathToFomAttributeBiMap) {
        this.fomPathToFomAttributeBiMap = fomPathToFomAttributeBiMap;
    }

    public BiMap<String, FOMInteraction> getFomPathToFomInteractionBiMap() {
        return fomPathToFomInteractionBiMap;
    }

    public void setFomPathToFomInteractionBiMap(BiMap<String, FOMInteraction> fomPathToFomInteractionBiMap) {
        this.fomPathToFomInteractionBiMap = fomPathToFomInteractionBiMap;
    }

    public Multimap<String, SimulationObject> getFomPathToSimulationObjectMap() {
        return fomPathToSimulationObjectMap;
    }

    public void setFomPathToSimulationObjectMap(Multimap<String, SimulationObject> fomPathToSimulationObjectMap) {
        this.fomPathToSimulationObjectMap = fomPathToSimulationObjectMap;
    }

    public Multimap<String, SimulationProperty<?>> getFomPathToSimulationAttributeMap() {
        return fomPathToSimulationAttributeMap;
    }

    public void setFomPathToSimulationAttributeMap(Multimap<String, SimulationProperty<?>> fomPathToSimulationAttributeMap) {
        this.fomPathToSimulationAttributeMap = fomPathToSimulationAttributeMap;
    }

    public Multimap<String, SimulationInteraction> getFomPathToSimulationInteractionMap() {
        return fomPathToSimulationInteractionMap;
    }

    public void setFomPathToSimulationInteractionMap(Multimap<String, SimulationInteraction> fomPathToSimulationInteractionMap) {
        this.fomPathToSimulationInteractionMap = fomPathToSimulationInteractionMap;
    }

    public BiMap<String, SimulationObject> getUuidToSimulationObjectBiMap() {
        return uuidToSimulationObjectBiMap;
    }

    public void setUuidToSimulationObjectBiMap(BiMap<String, SimulationObject> uuidToSimulationObjectBiMap) {
        this.uuidToSimulationObjectBiMap = uuidToSimulationObjectBiMap;
    }

    public BiMap<String, SimulationProperty<?>> getUuidToSimulationAttributeBiMap() {
        return uuidToSimulationAttributeBiMap;
    }

    public void setUuidToSimulationAttributeBiMap(BiMap<String, SimulationProperty<?>> uuidToSimulationAttributeBiMap) {
        this.uuidToSimulationAttributeBiMap = uuidToSimulationAttributeBiMap;
    }

    public BiMap<String, SimulationInteraction> getUuidToSimulationInteractionBiMap() {
        return uuidToSimulationInteractionBiMap;
    }

    public void setUuidToSimulationInteractionBiMap(BiMap<String, SimulationInteraction> uuidToSimulationInteractionBiMap) {
        this.uuidToSimulationInteractionBiMap = uuidToSimulationInteractionBiMap;
    }
}
