package simulation.federate.handler;

import com.google.common.primitives.Primitives;
import hla.rti1516e.*;
import hla.rti1516e.encoding.DataElement;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.exceptions.FederateNotExecutionMember;
import hla.rti1516e.exceptions.NotConnected;
import hla.rti1516e.exceptions.RTIexception;
import hla.rti1516e.exceptions.RTIinternalError;
import hla.rti1516e.time.HLAfloat64Time;
import hla.rti1516e.time.HLAfloat64TimeFactory;
import interpreter.Interpreter;
import library.model.simulation.SimulationInteraction;
import library.model.simulation.SimulationProperty;
import library.services.logging.LoggingService;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;
import org.portico.impl.hla1516e.Rti1516eFactory;
import simulation.federate.AbstractFederate;
import simulation.federate.SimulationFederateAmbassador;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages and handles the outgoing data of a federate.
 * Thus it should be part of each federate so it can be used when updating attributes and sending interactions to the RTI.
 * This handler uses maps to properly reflect all the necessary attributes together with their correct
 * attributeHandles and their corresponding objectClassInstance.
 */
public class OutgoingDataHandler {

    //MAPS
    private final Map<ObjectInstanceHandle, ArrayList<SimulationProperty<?>>> instanceToAttributeMap = new HashMap<>();
    private final Map<SimulationProperty<?>, AttributeHandle> attributeToHandleMap = new HashMap<>();
    private final Map<SimulationInteraction, InteractionClassHandle> interactionToHandleMap = new HashMap<>();
    private final List<SimulationInteraction> interactionsToFire = new ArrayList<>();

    private final List<Triple<ObjectClassHandle, ArrayList<SimulationProperty<?>>, String>> registerList = new ArrayList<>();
    private final List<Pair<ObjectClassHandle, AttributeHandleSet>> publishList = new ArrayList<>();
    private final List<InteractionClassHandle> interactionHandlesToPublish = new ArrayList<>();

    //HLA Objects
    private final EncoderFactory encoderFactory = new Rti1516eFactory().getEncoderFactory();
    private final HLAfloat64TimeFactory timeFactory;

    //Federate Objects
    private final SimulationFederateAmbassador fedAmbassador;
    private final RTIambassador rtiAmbassador;
    private final byte[] tag;

    public OutgoingDataHandler(AbstractFederate federate, byte[] tag) throws RTIinternalError, FederateNotExecutionMember, NotConnected {
        this.fedAmbassador = federate.getFedAmb();
        this.rtiAmbassador = federate.getRtiAmb();
        timeFactory = (HLAfloat64TimeFactory) rtiAmbassador.getTimeFactory();
        this.tag = tag;
        // interactionsToFire = federate.getSimulatedObject().getInteractionsToFire();
    }

    /**
     * reflects the values of all the Attributes in the attributeToHandleMap to the RTI
     *
     * @throws RTIexception
     */
    public void updateAttributeValues() throws RTIexception {
        //First update all the Attributes
        for (Map.Entry<ObjectInstanceHandle, ArrayList<SimulationProperty<?>>> entry : instanceToAttributeMap.entrySet()) {
            // Get the data that is relevant for the update
            ObjectInstanceHandle objectInstanceHandle = entry.getKey();
            AttributeHandleValueMap handleValueMap = rtiAmbassador.getAttributeHandleValueMapFactory().create(2);
            for (SimulationProperty instanceAttribute : entry.getValue()) {
                AttributeHandle attributeHandle = attributeToHandleMap.get(instanceAttribute);
                //DataElement which holds the data and the HLA Datatype for encoding
                DataElement dataElement = null;
                try {
                    String hlaDataType;
                    Class type;
                    boolean isEnum = instanceAttribute.getValue().getClass().isEnum();
                    Class attributeType = instanceAttribute.getValue().getClass();
                    if (isEnum) {
                        hlaDataType = Interpreter.DATATYPE_MAP.get("String");
                        type = String.class;
                    } else if (attributeType == ArrayList.class) {
                        hlaDataType = Interpreter.DATATYPE_MAP.get(instanceAttribute.getListType().getSimpleName());
                        type = Primitives.unwrap(instanceAttribute.getListType());
                    } else {
                        hlaDataType = Interpreter.DATATYPE_MAP.get(instanceAttribute.getValue().getClass().getSimpleName());
                        type = Primitives.unwrap(instanceAttribute.getValue().getClass());
                    }
                    // Invoke the correct "create" Method for the determined dataType
                    Method m = encoderFactory.getClass().getMethod("create" + hlaDataType, type);
                    if (isEnum) {
                        dataElement = (DataElement) m.invoke(encoderFactory, instanceAttribute.getValue().toString());
                    } else if (instanceAttribute.getValue().getClass() == ArrayList.class) {
                        ArrayList valueList = (ArrayList) instanceAttribute.getValue();
                        for (Object value : valueList) {
                            dataElement = (DataElement) m.invoke(encoderFactory, value);
                            handleValueMap.put(attributeHandle, dataElement.toByteArray());
                        }
                        continue;
                    } else {
                        dataElement = (DataElement) m.invoke(encoderFactory, instanceAttribute.getValue());
                    }
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                if (dataElement != null && attributeHandle != null && objectInstanceHandle != null) {
                    handleValueMap.put(attributeHandle, dataElement.toByteArray());
                }
            }
            // Reflect the updated Values to the RTI
            HLAfloat64Time time = timeFactory.makeTime(fedAmbassador.getFederateTime() + fedAmbassador.getFederateLookahead());
            rtiAmbassador.updateAttributeValues(objectInstanceHandle, handleValueMap, tag, time);
        }

        //Then fire Interactions (if there are any)
        for (SimulationInteraction interaction : interactionsToFire) {
            sendInteraction(interaction);
        }

        //clear Interaction list after all interactions have been sent
        interactionsToFire.clear();
    }

    /**
     * Sends an interaction depending on the interaction parameter using the interactionToHandleMap
     *
     * @param interaction
     * @throws RTIexception
     */
    public void sendInteraction(SimulationInteraction interaction) throws RTIexception {
        InteractionClassHandle interactionHandle = interactionToHandleMap.get(interaction);
        ParameterHandleValueMap parameters = rtiAmbassador.getParameterHandleValueMapFactory().create(0);
        HLAfloat64Time time = timeFactory.makeTime(fedAmbassador.getFederateTime() + fedAmbassador.getFederateLookahead());
        rtiAmbassador.sendInteraction(interactionHandle, parameters, tag, time);
    }

    /**
     * adds the interaction to the list, so it will be fired at the next update
     *
     * @param interaction
     */
    public void addInteractionToFire(SimulationInteraction interaction) {
        interactionsToFire.add(interaction);
    }

    /**
     * registers a SimulationInteraction Object with its handle, so it can be fired later on
     *
     * @param interaction
     * @param handle
     */
    public void registerInteraction(SimulationInteraction interaction, InteractionClassHandle handle) {
        if (!interactionHandlesToPublish.contains(handle)) {
            interactionHandlesToPublish.add(handle);
        }
        interactionToHandleMap.put(interaction, handle);
    }

    public void addInstanceToRegisterList(ObjectClassHandle classHandle, ArrayList<SimulationProperty<?>> propertyList, String ObjectId) {
        registerList.add(new MutableTriple<>(classHandle, propertyList, ObjectId));
    }

    /**
     * registers a SimulationAttribute, so it will be updated later on
     *
     * @param property
     * @param handle
     */
    public void registerPublishProperty(SimulationProperty property, AttributeHandle handle) {
        attributeToHandleMap.put(property, handle);
    }

    /**
     * adds a ObjectClassHandle, AttributeHandleSet Pair to the publish List, so those handles will be published
     *
     * @param pair
     */
    public void addToAttributePublishList(@NotNull Pair<ObjectClassHandle, AttributeHandleSet> pair) {
        if (pair == null) {
            throw new IllegalArgumentException("param 'pair' should not be null");
        }
        publishList.add(pair);
    }

    /**
     * Merges all the ObjectClassHandle, AttributeHandleSet pairs and performs the publish to the RTI
     */
    public void publishAttributes() {
        //First merge the AttributeHandleSets with the same ObjectClassHandle
        HashMap<ObjectClassHandle, AttributeHandleSet> mergedMap = new HashMap<>();

        for (Pair<ObjectClassHandle, AttributeHandleSet> pair : publishList) {
            ObjectClassHandle classHandle = pair.getKey();
            AttributeHandleSet attributeHandleSet = pair.getValue();
            if (mergedMap.containsKey(classHandle)) {
                AttributeHandleSet existingHandleSet = mergedMap.get(classHandle);
                for (AttributeHandle attributeHandle : attributeHandleSet) {
                    if (!existingHandleSet.contains(attributeHandle)) {
                        existingHandleSet.add(attributeHandle);
                    }
                }
            } else {
                mergedMap.put(classHandle, attributeHandleSet);
            }
        }

        //mergedMap now should contain the correct values
        //so now we try to publish them
        try {
            for (Map.Entry<ObjectClassHandle, AttributeHandleSet> entry : mergedMap.entrySet()) {
                rtiAmbassador.publishObjectClassAttributes(entry.getKey(), entry.getValue());
            }
        } catch (RTIexception e) {
            LoggingService.logWithAll("Error while publishing: ");
            e.printStackTrace();
        }
    }

    public void publishInteractions() {
        try {
            for (InteractionClassHandle handle : interactionHandlesToPublish) {
                rtiAmbassador.publishInteractionClass(handle);
            }
        } catch (RTIexception e) {
            e.printStackTrace();
        }

    }

    /**
     * Registers each Instance of a ObjectClass that exists, and maps the corresponding Attributes to the handle
     */
    public void registerInstances() {
        for (Triple<ObjectClassHandle, ArrayList<SimulationProperty<?>>, String> triple : registerList) {
            try {
                ObjectInstanceHandle instanceHandle;
                if (triple.getRight() != null) {
                    instanceHandle = rtiAmbassador.registerObjectInstance(triple.getLeft(), triple.getRight());
                } else {
                    instanceHandle = rtiAmbassador.registerObjectInstance(triple.getLeft());
                }
                instanceToAttributeMap.put(instanceHandle, triple.getMiddle());
            } catch (RTIexception e) {
                LoggingService.logWithAll("Error while registering an ObjectInstance: ");
                e.printStackTrace();
            }
        }
    }

    /**
     * destroys all the ObjectInstances
     */
    public void destroyAllObjects() {
        for (Map.Entry<ObjectInstanceHandle, ArrayList<SimulationProperty<?>>> entry : instanceToAttributeMap.entrySet()) {
            ObjectInstanceHandle instanceHandle = entry.getKey();
            try {
                rtiAmbassador.deleteObjectInstance(instanceHandle, tag);
            } catch (RTIexception e) {
                LoggingService.logWithAll("Error while deleting Object: " + instanceHandle);
                e.printStackTrace();
            }
        }
        //Since the Instances dont exist anymore, we should clear the Map
        instanceToAttributeMap.clear();
    }

}
