package interpreter;

import com.google.common.primitives.Primitives;
import hla.rti1516e.exceptions.FederateNotExecutionMember;
import hla.rti1516e.exceptions.NotConnected;
import interpreter.fom.model.FOM;
import interpreter.fom.model.FOMAttribute;
import interpreter.fom.model.FOMInteraction;
import interpreter.fom.model.FOMObjectClass;
import interpreter.fom.utils.FOMContentUtils;
import interpreter.fom.utils.FOMFileUtils;
import interpreter.fom.utils.ModelUtils;
import interpreter.utils.ReflectionUtils;
import library.model.dto.observer.ObservedClassDTO;
import library.model.dto.observer.ObservedObjectDTO;
import library.model.dto.observer.Observer;
import library.model.dto.scenario.ScenarioDTO;
import library.model.simulation.objects.ActiveSimulationObject;
import library.model.simulation.objects.IActiveDynamic;
import library.model.simulation.SimulationComponent;
import library.model.simulation.objects.SimulationObject;
import library.model.simulation.SimulationProperty;
import library.services.logging.LoggingType;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import simulation.federate.AbstractFederate;
import simulation.federate.interpreted.InterpretedFederate;
import simulation.federate.observer.ObserverFederate;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * Converts SimulationObjects into Federates.
 * Creates FOM for the unique attribute configuration if it does not exist.
 */
public class Interpreter {

    private static Interpreter instance = null;

    /** this maps the native data types to the according HLADataTypes */
    public static final HashMap<String, String> DATATYPE_MAP = new HashMap<>() {{
        put("Integer", "HLAinteger32BE");
        put("Boolean", "HLAboolean");
        put("Double", "HLAfloat64BE");
        put("String", "HLAASCIIstring");
        put("Long", "HLAinteger64BE");
        put("Float", "HLAfloat32BE");
        put("ArrayList", "HLAvariableArray");
        put("Character", "HLAunicodeChar");
        put("Byte", "HLAbyte");
        put("Short", "HLAinteger16BE");
    }};

    private final List<ReferenceStore> referenceStores = new ArrayList<>();

    private Interpreter() {

    }

    // Singleton Pattern
    public static Interpreter getInstance() {
        if (instance == null) {
            instance = new Interpreter();
        }
        return instance;
    }

    /**
     * Reads the list of simulationObjects provided by the scenario object.
     *
     * @param scenario provided scenario
     * @return a list of all generated federates
     */
    public ArrayList<AbstractFederate> createFederates(ScenarioDTO scenario) {
        if (scenario == null) {
            throw new NullPointerException("parameter 'scenario' should not be null");
        }

        ArrayList<AbstractFederate> createdFederates = new ArrayList<>();

        try {

            //////////////////////////////////////////////////////////////////////////////////
            // (1) CREATE AN (ACTIVE) INTERPRETED FEDERATE OF EACH TOP-LEVEL SIMULATION-OBJECT
            //////////////////////////////////////////////////////////////////////////////////
            // build FOMs, referenceStores and federates based on the given top level simulation objects
            for (SimulationObject simulationObject : scenario.getSimulationObjects()) {
                if (simulationObject instanceof IActiveDynamic) {
                    int iterations = -1;
                    if (scenario.isStepsLimited()) {
                        iterations = scenario.getSimulationIterations();
                    }
                    // create a interpreted federate (including generation of a FOM, etc.) for every top level simulation object
                    InterpretedFederate interpretedFederate = createInterpretedFederate((ActiveSimulationObject) simulationObject, iterations);
                    createdFederates.add(interpretedFederate);
                }
            }

            // prepare data needed for the subscription of other simulationObjects published by other federates
            // this has to be done after all interpreted federates are created by when all FOMs and reference stores are generated
            for (AbstractFederate createdFederate : createdFederates) {
                InterpretedFederate interpretedFederate = (InterpretedFederate) createdFederate;
                prepareObservedClasses(interpretedFederate.getSimulatedObject().getObservedClasses());
            }

            ////////////////////////////////////////////////////////////////////
            // (2) CREATE A (PASSIVE) OBSERVER FEDERATE FROM THE OBSERVER OBJECT
            ////////////////////////////////////////////////////////////////////
            // create and add passive observing federates
            for (Observer observer : scenario.getObservers()) {

                prepareObservedObjects(observer.getObservedObjects());
                prepareObservedClasses(observer.getObservedClasses());

                // create a observerFederate for every observer that is configured in the given scenario
                ObserverFederate observerFederate = new ObserverFederate(observer, ModelUtils.getHashedClassIdentifier(observer));

                // TODO check logging process
                // register Observer specific LoggingTypes for the Observed-Values
                for (LoggingType loggingType : observer.getLoggingTypes()) {
                    observerFederate.registerLogger(loggingType);
                }

                // collect the references to the created observerFederates in the corresponding list
                createdFederates.add(observerFederate);
            }

            //////////////////////////////////////////////////////////////////////////////////////////
            // (3) CREATE A (SEMI-PASSIVE) TERMINATION FEDERATE USING THE GIVEN TERMINATION CONDITIONS
            //////////////////////////////////////////////////////////////////////////////////////////
            //
            // TODO refactor and re-add
            // add termination federate
            //
            // int iterations = -1;
            // if (scenario.isStepsLimited()) {
            //     iterations = scenario.getSimulationIterations();
            // }
            // TerminationFederate terminationFederate = createTerminationFederate(scenario, iterations);
            // createdFederates.add(terminationFederate);

        } catch (FederateNotExecutionMember federateNotExecutionMember) {
            //Log that creation of one federate failed;
            federateNotExecutionMember.printStackTrace();
        }

        return createdFederates;
    }

    private void prepareObservedClasses(@NotNull List<ObservedClassDTO> observedClasses) {
        for (ObservedClassDTO observedClass : observedClasses) {
            String type = observedClass.getType();

            for (ReferenceStore referenceStore : referenceStores) {
                Class<?> simulationObjectClass = referenceStore.getSimulationObject().getClass();

                if (ReflectionUtils.getInheritanceStackOfClass(simulationObjectClass)
                                   .stream()
                                   .anyMatch(c -> c.getSimpleName().equalsIgnoreCase(type))) {

                    // TODO test if this is needed
                    // simulationFOMFilePaths.add(referenceStore.getFom().getPath());

                    String fomPath = referenceStore.getFomPathForSimulationObject(referenceStore.getSimulationObject());
                    fomPath = fomPath.substring(0, fomPath.indexOf(StringUtils.capitalize(type)) + type.length());
                    observedClass.setFomPath(fomPath);
                    observedClass.setObjectClass(ReflectionUtils.getInheritanceStackOfClass(simulationObjectClass)
                                                                .stream()
                                                                .filter(c -> c.getSimpleName().equalsIgnoreCase(type))
                                                                .map(c -> (Class<SimulationObject>) c)
                                                                .findFirst()
                                                                .orElseThrow());

                    // we have to adjust the attributes because some of them may be represent nested attributes that are flattened for the FOM
                    // example: 'position' refers to an object containing further attributes 'latitude', 'longitude' and 'altitude'
                    // those are flattened to e.g. 'position.latitude' for the FOM but the scenario may contain only the string 'position'
                    // therefore for every given scenario attribute to observe all FOM attribute paths are examined and if one contains the given string
                    // we get the fom path from this index on for the later creating of observer attributeHandles
                    observedClass.setAttributes(
                        observedClass.getAttributes()
                                     .stream()
                                     .flatMap(attribute -> referenceStore.getFomPathToFomAttributeBiMap().keySet()
                                                                         .stream()
                                                                         .filter(attributeFomPath -> attributeFomPath.contains(attribute))
                                                                         .map(attributeFomPath -> attributeFomPath.substring(attributeFomPath.indexOf(attribute)))
                                                                         .distinct()
                                     ).collect(Collectors.toList())
                    );

                    break;
                }
            }
        }
    }

    private void prepareObservedObjects(@NotNull List<ObservedObjectDTO> observedObjects) {
        // look for the FOM-Paths of all objects that should be observed
        // those are needed later by the HLA ObjectHandles
        for (ObservedObjectDTO observedObject : observedObjects) {

            String id = observedObject.getId();

            // TODO this should probably be some kind of serializable data representing the fom files content
            //      if we want to execute the simulation in a physically distributed way at some point in the future

            if (id != null && !id.isBlank()) {

                for (ReferenceStore referenceStore : referenceStores) {
                    if (referenceStore.getSimulationObject().getId().equals(id)) {
                        Class clazz = referenceStore.getSimulationObject().getClass();
                        observedObject.setFomPath(referenceStore.getFomPathForSimulationObject(referenceStore.getSimulationObject()));
                        observedObject.setObjectClass(clazz);

                        // we have to adjust the attributes because some of them may be represent nested attributes that are flattened for the FOM
                        // example: 'position' refers to an object containing further attributes 'latitude', 'longitude' and 'altitude'
                        // those are flattened to e.g. 'position.latitude' for the FOM but the scenario may contain only the string 'position'
                        // therefore for every given scenario attribute to observe all FOM attribute paths are examined and if one contains the given string
                        // we get the fom path from this index on for the later creating of observer attributeHandles
                        List<String> correctedAttributes = observedObject.getAttributes()
                                                                         .stream()
                                                                         .flatMap(attribute -> referenceStore.getFomPathToFomAttributeBiMap().keySet()
                                                                                                             .stream()
                                                                                                             .filter(fomPath -> fomPath.contains(attribute))
                                                                                                             .map(fomPath -> fomPath.substring(fomPath.indexOf(attribute)))
                                                                                                             .distinct()
                                                                         ).collect(Collectors.toList());

                        observedObject.setAttributes(correctedAttributes);
                        break;
                    }
                }
            } else {
                // TODO throw and/or print error message (id is missing)
            }
        }
    }

    /**
     * TODO javadoc
     *
     * @param simulationObject
     * @return
     */
    private FOM getFOMForSimulationObject(SimulationObject simulationObject) {

        if (simulationObject == null) {
            throw new IllegalArgumentException("parameter 'simulationObject' should not be null");
        }
        if (!(simulationObject instanceof IActiveDynamic)) {
            return null; // TODO throw an appropriate exception
        }

        Path path;
        String filename = FOMFileUtils.getFOMFileName(simulationObject);

        boolean fomAlreadyExists = false;
        try {
            fomAlreadyExists = FOMFileUtils.checkIfFomFileExists(filename);
        } catch (IOException exception) {
            exception.printStackTrace(); // TODO implement proper error handling / logging
        }

        FOMObjectClass objectClass = getFOMObjectClass(simulationObject);
        List<FOMObjectClass> objectClasses = List.of(objectClass);

        // TODO implement proper use of interactions
        // List<FOMInteraction> interactions = getFOMInteractions(simulationObject);
        List<FOMInteraction> interactions = new ArrayList<>();

        if (!fomAlreadyExists) {
            String fomString = FOMContentUtils.generateFOMString(objectClasses, interactions, filename);
            path = FOMFileUtils.saveFomAsXMLFile(filename, fomString);
        } else {
            path = FOMFileUtils.getPathFromFileName(filename);
        }

        return new FOM(path, filename, objectClasses, interactions);
    }

    /**
     * TODO javadoc
     *
     * @param simulationObject
     * @return
     */
    private FOMObjectClass getFOMObjectClass(SimulationObject simulationObject) {

        if (simulationObject == null) {
            throw new NullPointerException("parameter simulationObject should not be null");
        }

        FOMObjectClass rootObjectClass = null;

        // create a stack of classes representing the inheritance structure
        Stack<Class<?>> classes = ReflectionUtils.getInheritanceStackOfObject(simulationObject, SimulationObject.class);

        // go through the constructed inheritance class structure stack
        FOMObjectClass lastVisitedObjectClass = null;

        while (!classes.isEmpty()) {
            Class<?> clazz = classes.pop();
            FOMObjectClass currentObjectClass = new FOMObjectClass(clazz.getSimpleName());

            if (lastVisitedObjectClass == null) { // if root object doesn't exist, create it. should be the 'simulationObject' class
                rootObjectClass = new FOMObjectClass(clazz.getSimpleName());
                currentObjectClass = rootObjectClass;
            } else { // if root exists, get the last visited class object and add a new subclass
                currentObjectClass.setSuperClass(lastVisitedObjectClass);
                lastVisitedObjectClass.addSubClass(currentObjectClass);
                // TODO
            }

            currentObjectClass.addAttributes(getFOMAttributesForObjectAndClass(simulationObject, clazz));

            lastVisitedObjectClass = currentObjectClass;
        }

        // add component attributes to the FOMObject that describes the actual class of the given simulation object
        ArrayList<SimulationComponent> components = ((ActiveSimulationObject) simulationObject).getComponents();
        for (SimulationComponent component : components) {
            assert lastVisitedObjectClass != null;
            lastVisitedObjectClass.addAttributes(getFOMAttributesForComponent(component));
        }

        return rootObjectClass;
    }

    public List<FOMAttribute> getFOMAttributesForObjectAndClass(@NotNull SimulationObject object, @NotNull Class<?> clazz) {

        if (!clazz.isAssignableFrom(object.getClass())) {
            throw new InvalidParameterException("the given class has to be the class or a superclass of the class of the given object");
        }

        List<FOMAttribute> fomAttributes = new ArrayList<>();

        // get all fields of the given class
        Field[] cassFields = clazz.getDeclaredFields();

        try {

            // vor every field of the given object
            for (Field field : cassFields) {

                field.setAccessible(true);

                // get the value (as object) of the field currently looked at
                Object fieldValueObject = field.get(object);

                // get the type (as class) of the value that the field is holding
                if (fieldValueObject != null && fieldValueObject.getClass() == SimulationProperty.class) {
                    fomAttributes.addAll(getFOMAttributesForProperty((SimulationProperty<?>) fieldValueObject, field));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fomAttributes;
    }

    private List<FOMAttribute> getFOMAttributesForProperty(SimulationProperty<?> property, Field field) throws IOException, IllegalAccessException {
        return getFOMAttributesForProperty(property, field, "");
    }

    private List<FOMAttribute> getFOMAttributesForProperty(SimulationProperty<?> property, Field field, String compositePath) throws IOException, IllegalAccessException {
        Class<?> propertyClass = property.getValue().getClass();
        List<FOMAttribute> fomAttributes = new ArrayList<>();

        if (propertyClass == ArrayList.class || propertyClass == HashMap.class) {
            // TODO refactor (see comment @SimulationAttribute.class)
            // propertyClass = simulationAttribute.getListType();
            // if (propertyClass == null) {
            //      // skip since the ListType was not given
            //      continue;
            // }

            // 15.02.2022: skip lists completely for now
        }

        if (Primitives.isWrapperType(propertyClass) || propertyClass == String.class || propertyClass.isEnum()) {

            FOMAttribute fomAttribute = createFOMAttribute(field, property, compositePath);
            fomAttributes.add(fomAttribute);

        } else {

            Object compositePropertyValueObject = property.getValue();
            for (Field innerField : compositePropertyValueObject.getClass().getDeclaredFields()) {
                innerField.setAccessible(true);
                if (innerField.get(compositePropertyValueObject) != null
                    && innerField.get(compositePropertyValueObject).getClass() == SimulationProperty.class) {
                    fomAttributes.addAll(
                        getFOMAttributesForProperty(
                            (SimulationProperty<?>) innerField.get(compositePropertyValueObject),
                            innerField,
                            compositePath + (!compositePath.isEmpty() ? "." : "") + property.getName()
                        )
                    );
                }
            }
        }
        return fomAttributes;
    }

    private List<FOMAttribute> getFOMAttributesForComponent(SimulationComponent component) {
        if (component == null) {
            throw new NullPointerException("parameter 'component' should not be null");
        }
        return getFOMAttributesForComponent(component, Collections.emptyList());
    }

    private List<FOMAttribute> getFOMAttributesForComponent(SimulationComponent component, List<SimulationComponent> parentComponents) {
        if (component == null) {
            throw new NullPointerException("parameter 'component' should not be null");
        }

        List<FOMAttribute> fomAttributes = new ArrayList<>();

        Class<?> componentClass = component.getClass();
        Field[] classFields = componentClass.getDeclaredFields();
        String componentAttributeNamePrefix = parentComponents.stream()
                                                              .map(c -> c.getClass().getSimpleName())
                                                              .collect(Collectors.joining("."));

        try {

            // vor every field of the given object
            for (Field field : classFields) {

                field.setAccessible(true);

                // get the value (as object) of the field currently looked at
                Object fieldValueObject = field.get(component);

                // get the type (as class) of the value that the field is holding
                if (fieldValueObject != null && fieldValueObject.getClass() == SimulationProperty.class) {
                    SimulationProperty<?> simulationProperty = (SimulationProperty<?>) fieldValueObject;
                    Class<?> propertyClass = simulationProperty.getValue().getClass();

                    if (propertyClass == ArrayList.class || propertyClass == HashMap.class) {
                        // skip lists
                        continue;
                    }

                    if (Primitives.isWrapperType(propertyClass) || propertyClass == String.class || propertyClass.isEnum()) {
                        FOMAttribute fomAttribute = createFOMAttribute(field,
                                                                       simulationProperty,
                                                                       componentAttributeNamePrefix);
                        fomAttributes.add(fomAttribute);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // call this method recursively for every sub-component this component links to
        if (!component.getSubComponents().isEmpty()) {
            parentComponents.add(component);
            fomAttributes.addAll(
                component.getSubComponents()
                         .stream()
                         .flatMap(subComponent -> getFOMAttributesForComponent(subComponent, parentComponents).stream())
                         .collect(Collectors.toList())
            );
        }

        return fomAttributes;
    }

    private FOMAttribute createFOMAttribute(Field field, SimulationProperty<?> property) throws IOException {
        if (field == null) {
            throw new NullPointerException("parameter 'field' should not be null");
        }
        if (property == null) {
            throw new NullPointerException("parameter 'property' should not be null");
        }

        return createFOMAttribute(field, property, "");
    }

    private FOMAttribute createFOMAttribute(Field field, SimulationProperty<?> property, String pathPrefix) throws IOException {
        if (field == null) {
            throw new NullPointerException("parameter 'field' should not be null");
        }
        if (property == null) {
            throw new NullPointerException("parameter 'property' should not be null");
        }

        if (pathPrefix == null || pathPrefix.isBlank()) {
            pathPrefix = "";
        }

        // String fomClassPath;
        // boolean publish = false;
        // boolean subscribe = false;
        // String updateType = "Unconditional";
        // String ownerShip = "NoTransfer";
        // String name;
        // String dataType;

        Type genericType = field.getGenericType();
        FOMAttribute fomAttribute = new FOMAttribute();

        fomAttribute.setPublish(property.isPublish());
        fomAttribute.setName(
            pathPrefix
            + (!pathPrefix.isBlank() && !pathPrefix.endsWith(".") ? "." : "")
            + field.getName()
        );

        String typeName;
        if (genericType instanceof ParameterizedType) {
            typeName = ((ParameterizedType) genericType).getActualTypeArguments()[0].getTypeName();
            typeName = typeName.substring(typeName.lastIndexOf(".") + 1);
        } else {
            typeName = genericType.getTypeName();
        }
        if (DATATYPE_MAP.containsKey(typeName)) {
            fomAttribute.setDataType(typeName);
        } else {
            Class<?> typeClass = null;
            for (Class<?> cls : ModelUtils.getClasses()) {
                if (cls.getSimpleName().equals(typeName)) {
                    typeClass = cls;
                    break;
                }
            }
            if (typeClass != null && typeClass.isEnum()) {
                fomAttribute.setDataType("String");
            } else {
                // TODO throw appropriate exception
            }
        }

        return fomAttribute;
    }

    ///////////////////////////////////
    // STEP 3 "prepare simulation"
    ///////////////////////////////////

    /**
     * Creates an interpreted federate
     *
     * @param simulationObject simulated object
     * @return Federate object of the object to be simulated
     * @throws FederateNotExecutionMember Federate not able to be executed
     * @throws NotConnected               Federate not able to connect to federation
     */
    private InterpretedFederate createInterpretedFederate(@NotNull ActiveSimulationObject simulationObject, int iterations) throws FederateNotExecutionMember {

        ReferenceStore referenceStore = new ReferenceStore();
        FOM fom = getFOMForSimulationObject(simulationObject);
        // TODO maybe move the following two values and the initialization to the constructor?
        referenceStore.setFom(fom);
        referenceStore.setSimulationObject(simulationObject);
        referenceStore.initialize();
        referenceStores.add(referenceStore);

        try {
            return new InterpretedFederate(referenceStore, iterations);
        } catch (FederateNotExecutionMember notConnected) {
            notConnected.printStackTrace(); // TODO use proper logging service
            throw notConnected;
        }
    }

    // TODO re-add
    // /**
    //  * creates the termination federate for the simulation
    //  *
    //  * @param scenario
    //  * @param iterations
    //  * @return the created TerminationFederate
    //  * @throws FederateNotExecutionMember
    //  * @throws NotConnected
    //  */
    // public TerminationFederate createTerminationFederate(Scenario scenario, int iterations) throws FederateNotExecutionMember, NotConnected {
    //     HashMap<SimulationObject, ArrayList<String>> simObjToAttrIdMap = getSimObjToAttrIdMap(scenario.getTerminationConditions());
    //     ArrayList<String> federateFoms = FOMFileUtils.getIdentifiersOfExistingFOMs(simObjToAttrIdMap.keySet());
    //     try {
    //         return new TerminationFederate(scenario.getTerminationConditions(), simObjToAttrIdMap, federateFoms, iterations);
    //     } catch (NotConnected | FederateNotExecutionMember notConnected) {
    //         notConnected.printStackTrace();
    //         throw notConnected;
    //     }
    // }

}
