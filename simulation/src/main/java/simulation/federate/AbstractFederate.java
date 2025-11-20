package simulation.federate;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Table;
import hla.rti1516e.*;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.exceptions.*;
import hla.rti1516e.time.HLAfloat64Interval;
import hla.rti1516e.time.HLAfloat64Time;
import hla.rti1516e.time.HLAfloat64TimeFactory;
import interpreter.utils.ReflectionUtils;
import library.model.dto.observer.ObservedClassDTO;
import library.model.simulation.SimulationInteraction;
import library.model.simulation.objects.SimulationObject;
import org.jetbrains.annotations.NotNull;
import simulation.federate.handler.IncomingDataHandler;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class AbstractFederate implements Runnable {

    protected String federateName;

    protected int currentIteration = 0;
    protected final double timeStepSize;

    protected EncoderFactory encoderFactory;
    protected HLAfloat64TimeFactory timeFactory;
    protected RTIambassador rtiAmb;
    protected SimulationFederateAmbassador fedAmb;

    protected IncomingDataHandler incomingDataHandler;

    public AbstractFederate(double timeStepSize) {
        this.timeStepSize = timeStepSize;
    }

    public AbstractFederate(String name, double timeStepSize) {
        this.federateName = name;
        this.timeStepSize = timeStepSize;
    }

    // INCOMING DATA MAPS
    public HashMap<InteractionClassHandle, ArrayList<SimulationInteraction>> handleToInteractionMap = new HashMap<>();

    protected BiMap<Class<SimulationObject>, ObjectClassHandle> objectClassHandleBiMap = HashBiMap.create();
    protected BiMap<ObjectClassHandle, AttributeHandleSet> attributeHandleBiMap = HashBiMap.create();
    protected Table<AttributeHandle, String, Class<?>> attributeHandleNameClassMap = HashBasedTable.create();
    protected BiMap<SimulationObject, ObjectInstanceHandle> objectInstanceHandleBiMap = HashBiMap.create();

    public void cacheObjectClassHandle(@NotNull Class<SimulationObject> simulationObjectClass,
                                          @NotNull ObjectClassHandle objectClassHandle) {
        objectClassHandleBiMap.put(simulationObjectClass, objectClassHandle);
    }

    public void cacheAttributeHandleSet(@NotNull ObjectClassHandle objectClassHandle,
                                           @NotNull AttributeHandleSet attributeHandleSet) {
        attributeHandleBiMap.put(objectClassHandle, attributeHandleSet);
    }

    public void cacheAttributeHandleData(@NotNull AttributeHandle attributeHandle,
                                            @NotNull String attributeName,
                                            @NotNull Class<?> attributeClass) {
        attributeHandleNameClassMap.put(attributeHandle, attributeName, attributeClass);
    }

    public void cacheObjectInstance(@NotNull SimulationObject simulationObject,
                                       @NotNull ObjectInstanceHandle objectInstanceHandle) {
        objectInstanceHandleBiMap.put(simulationObject, objectInstanceHandle);
    }

    public SimulationObject getSimulationObjectInstance(ObjectInstanceHandle objectInstanceHandle) {
        return objectInstanceHandleBiMap.inverse().get(objectInstanceHandle);
    }

    public String getAttributeNameByHandle(AttributeHandle attributeHandle) {
        return attributeHandleNameClassMap.row(attributeHandle).keySet().stream().findFirst().orElseThrow();
    }

    public Class<?> getAttributeTypeByHandle(AttributeHandle attributeHandle) {
        return attributeHandleNameClassMap.row(attributeHandle).entrySet().stream().findFirst().get().getValue();
    }

    public Class<SimulationObject> getObjectClassByHandle(ObjectClassHandle objectClassHandle) {
        return objectClassHandleBiMap.inverse().get(objectClassHandle);
    }

    // HANDLES
    protected InteractionClassHandle simulationEndHandle;

    /**
     * The sync point all federates will sync up on before starting
     */
    public static final String READY_TO_RUN = "ReadyToRun";

    /**
     * Lock Object used for Thread synchronization
     */
    public Object LOCK = new Object();

    public abstract void execute() throws Exception;

    public abstract boolean isAtSyncPoint();

    @Override
    public void run() {
        try {
            execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method will attempt to enable the various time related properties for
     * the federate
     */
    protected void enableTimePolicy() throws Exception {
        // NOTE: Unfortunately, the LogicalTime/LogicalTimeInterval create code is
        //       Portico specific. You will have to alter this if you move to a
        //       different RTI implementation. As such, we've isolated it into a
        //       method so that any change only needs to happen in a couple of spots
        HLAfloat64Interval lookahead = timeFactory.makeInterval(fedAmb.getFederateLookahead());

        ////////////////////////////
        // enable time regulation //
        ////////////////////////////
        this.rtiAmb.enableTimeRegulation(lookahead);

        // tick until we get the callback
        while (!fedAmb.isRegulating()) {
            rtiAmb.evokeMultipleCallbacks(0.1, 0.2);
        }

        /////////////////////////////
        // enable time constrained //
        /////////////////////////////
        if (!fedAmb.isConstrained()) {
            this.rtiAmb.enableTimeConstrained();
        }

        // tick until we get the callback
        while (!fedAmb.isConstrained()) {
            rtiAmb.evokeMultipleCallbacks(0.1, 0.2);
        }
    }

    /**
     * This method will request a time advance to the current time, plus the given time step.
     * It will then wait until a notification of the time advance grant has been received.
     */
    protected void advanceTime(double timeStep) throws RTIexception {

        // request the advance
        fedAmb.setAdvancing(true);
        HLAfloat64Time time = timeFactory.makeTime(fedAmb.getFederateTime() + timeStep);
        rtiAmb.timeAdvanceRequest(time);

        // wait for the time advance to be granted. ticking will tell the
        // LRC to start delivering callbacks to the federate
        while (fedAmb.isAdvancing()) {
            rtiAmb.evokeMultipleCallbacks(0.1, 0.2);
        }
    }

    protected void generateDataSubscriptionHandles(List<ObservedClassDTO> observedClasses) throws FederateNotExecutionMember, NotConnected, NameNotFound, RTIinternalError, InvalidObjectClassHandle, AttributeNotDefined, ObjectClassNotDefined, RestoreInProgress, SaveInProgress {

        for (ObservedClassDTO observedClass : observedClasses) {

            String observedObjectFomPath = observedClass.getFomPath();

            // check if the fom path is present
            if (observedObjectFomPath != null && !observedObjectFomPath.isBlank()) {

                // create the attribute handle set
                AttributeHandleSet attributeHandleSet = rtiAmb.getAttributeHandleSetFactory().create();

                // create the object class handle
                ObjectClassHandle objectClassHandle = rtiAmb.getObjectClassHandle(observedObjectFomPath);

                // get all field from the object (from its class and its superclasses)
                Class<SimulationObject> objectClass = observedClass.getObjectClass();
                ArrayList<Field> allObjectFields = ReflectionUtils.getFieldsOfClass(objectClass);

                // look for the given attribute name in the previously collected list of all object attributes
                for (String attributeFullName : observedClass.getAttributes()) {

                    // create an attributeHandle for the currently looked at attribute and collect them
                    AttributeHandle attributeHandle = rtiAmb.getAttributeHandle(objectClassHandle, attributeFullName);
                    attributeHandleSet.add(attributeHandle);

                    String[] attributeNamesSplitByDot = attributeFullName.split("\\.");

                    // get the field representing the attribute inside the object
                    Field attributeField = ReflectionUtils.getFieldFromFieldListByName(allObjectFields, attributeNamesSplitByDot[0]);

                    // attributes are always of type SimulationAttribute<T>, we therefore have to get the actual generic type
                    Class<?> attributeType = ReflectionUtils.getGenericTypeOfField(attributeField);

                    // if the attribute is a compound attribute (indicated by attribute names chained with a dot in between)
                    if (attributeNamesSplitByDot.length > 1) {
                        // ... we have to go along the data structure according to the chained name until we arrive at the final field
                        for (int i = 1; i < attributeNamesSplitByDot.length; i++) {
                            String attributeName = attributeNamesSplitByDot[i];
                            attributeField = ReflectionUtils.getFieldOfClassByName(attributeType, attributeName);
                            attributeType = ReflectionUtils.getGenericTypeOfField(attributeField);
                        }
                    }

                    // cache the handle, the fields name and the fields actual type for later usage
                    attributeHandleNameClassMap.put(attributeHandle, attributeFullName, attributeType);
                }

                // fill the caching biMaps for later usage (needed for process incoming object discoveries and attribute updates)
                objectClassHandleBiMap.put(observedClass.getObjectClass(), objectClassHandle);
                attributeHandleBiMap.put(objectClassHandle, attributeHandleSet);

                // subscribe to all attributes of the given object //
                rtiAmb.subscribeObjectClassAttributes(objectClassHandle, attributeHandleSet);
            }
        }
    }

    protected void generateInteractionSubscriptionHandles() throws FederateNotExecutionMember, NameNotFound, NotConnected, RTIinternalError, RestoreInProgress, InteractionClassNotDefined, FederateServiceInvocationsAreBeingReportedViaMOM, SaveInProgress {
        //////////////////////////////////////////////////
        // subscribe to interaction class SimulationEnd //
        //////////////////////////////////////////////////
        String interactionName = "HLAinteractionRoot.SimulationEnd";
        simulationEndHandle = rtiAmb.getInteractionClassHandle(interactionName);
        rtiAmb.subscribeInteractionClass(simulationEndHandle);
    }

    public RTIambassador getRtiAmb() {
        return rtiAmb;
    }

    public String getFederateName() {
        return federateName;
    }

    public int getCurrentIteration() {
        return currentIteration;
    }

    public double getTimeStepSize() {
        return timeStepSize;
    }

    public EncoderFactory getEncoderFactory() {
        return encoderFactory;
    }

    public HLAfloat64TimeFactory getTimeFactory() {
        return timeFactory;
    }

    public SimulationFederateAmbassador getFedAmb() {
        return fedAmb;
    }

    public IncomingDataHandler getIncomingDataHandler() {
        return incomingDataHandler;
    }

    public HashMap<InteractionClassHandle, ArrayList<SimulationInteraction>> getHandleToInteractionMap() {
        return handleToInteractionMap;
    }

    public BiMap<Class<SimulationObject>, ObjectClassHandle> getObjectClassHandleBiMap() {
        return objectClassHandleBiMap;
    }

    public BiMap<ObjectClassHandle, AttributeHandleSet> getAttributeHandleBiMap() {
        return attributeHandleBiMap;
    }

    public Table<AttributeHandle, String, Class<?>> getAttributeHandleNameClassMap() {
        return attributeHandleNameClassMap;
    }

    public BiMap<SimulationObject, ObjectInstanceHandle> getObjectInstanceHandleBiMap() {
        return objectInstanceHandleBiMap;
    }

    public InteractionClassHandle getSimulationEndHandle() {
        return simulationEndHandle;
    }

    public Object getLOCK() {
        return LOCK;
    }
}
