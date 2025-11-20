//package simulation.federate.termination;
//
//import hla.rti1516e.*;
//import hla.rti1516e.encoding.DataElement;
//import hla.rti1516e.encoding.DecoderException;
//import hla.rti1516e.encoding.EncoderFactory;
//import hla.rti1516e.exceptions.*;
//import hla.rti1516e.time.HLAfloat64Interval;
//import hla.rti1516e.time.HLAfloat64Time;
//import hla.rti1516e.time.HLAfloat64TimeFactory;
//import simulation.federate.AbstractFederate;
//import interpreter.Interpreter;
//import interpreter.utils.ReflectionUtils;
//import library.model.simulation.IBehaviour;
//import library.model.dto.scenario.terminationconditions.TerminationCondition;
//import library.model.simulation.*;
//import library.services.logging.LoggingService;
//import org.portico.impl.hla1516e.Rti1516eFactory;
//import manager.SimulationWatchDog;
//
//import java.io.File;
//import java.lang.reflect.Field;
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.net.URL;
//import java.util.*;
//
///**
// * Federate that checks if termination conditions are met and the simulation is supposed to end.
// * Is that the case, it notifies the SimulationWatchdog.
// */
//public class TerminationFederate extends AbstractFederate {
//    //----------------------------------------------------------
//    //                    STATIC VARIABLES
//    //----------------------------------------------------------
//    /**
//     * The number of times we will update our attributes and send an interaction
//     */
//    private final int ITERATIONS;
//    private final boolean unlimitedIterations;
//
//    private int currentIteration = 0;
//
//    /**
//     * SimulationWatchDog that contains the Signal for the end of the simulation
//     */
//    private SimulationWatchDog watchDog;
//
//    //----------------------------------------------------------
//    //                   INSTANCE VARIABLES
//    //----------------------------------------------------------
//    private RTIambassador rtiAmb;
//    private TerminationFederateAmbassador fedAmb;  // created when we connect
//    private HLAfloat64TimeFactory timeFactory; // set when we join
//    protected EncoderFactory encoderFactory;
//    private final String federateName = "TerminationFederate";
//    private final ArrayList<String> federateFoms;
//    private final ArrayList<TerminationCondition<?>> terminationConditions;
//    private final String id;
//    private boolean atSyncPoint = false;
//    private boolean simulationEndIsDetected = false;
//
//    // caches of handle types - set once we join a federation
//    protected ArrayList<AttributeHandleSet> attributeHandleSets;
//    protected ArrayList<ObjectClassHandle> objectClassHandles;
//    protected ArrayList<ObjectInstanceHandle> objectInstanceHandles;
//    private HashMap<String, HashMap<AttributeHandle, SimulationAttribute>> objectIdAttributeMap = new HashMap<>();
//    private HashMap<ObjectInstanceHandle, HashMap<AttributeHandle, SimulationAttribute>> instanceHandleAttributeMap = new HashMap<>();
//
//    protected InteractionClassHandle simulationEndHandle;
//
//    protected HashMap<ObjectClassHandle, AttributeHandleSet> subscribeMap = new HashMap<>();
//    ;
//    protected HashMap<String, ObjectClassHandle> attributeToClassHandle;
//    protected HashMap<String, AttributeHandle> attributeToAttributeHandle;
//    protected HashMap<ObjectClassHandle, ObjectInstanceHandle> classToInstanceHandle;
//    protected HashSet<ObjectClassHandle> publishedObjectClassHandles;
//
//    protected HashMap<AttributeHandle, SimulationAttribute> atrHandleSimAtrMap;
//    protected HashMap<SimulationAttribute, ObjectClassHandle> simAtrObjClassHandleMap;
//
//    protected HashMap<SimulationObject, ArrayList<String>> simObjToAttrIdMap;
//
//    //----------------------------------------------------------
//    //                      CONSTRUCTORS
//    //----------------------------------------------------------
//    public TerminationFederate(ArrayList<TerminationCondition<?>> terminationConditions, HashMap<SimulationObject, ArrayList<String>> simObjToAttrIdMap, ArrayList<String> federateFoms, int iterations) throws NotConnected, FederateNotExecutionMember {
//        this.LOCK = new Object();
//        this.terminationConditions = terminationConditions;
//        this.federateFoms = federateFoms;
//        this.ITERATIONS = iterations;
//        this.attributeHandleSets = new ArrayList<>();
//        this.simObjToAttrIdMap = simObjToAttrIdMap;
//        this.objectClassHandles = new ArrayList<>();
//        this.attributeToClassHandle = new HashMap<>();
//        this.classToInstanceHandle = new HashMap<>();
//        this.attributeToAttributeHandle = new HashMap<>();
//        this.publishedObjectClassHandles = new HashSet<>();
//        this.objectInstanceHandles = new ArrayList<>();
//        this.atrHandleSimAtrMap = new HashMap<>();
//        this.simAtrObjClassHandleMap = new HashMap<>();
//        this.id = UUID.randomUUID().toString();
//        this.unlimitedIterations = iterations < 0;
//    }
//
//    ///////////////////////////////////////////////////////////////////////////
//    ////////////////////////// Main Simulation Method /////////////////////////
//    ///////////////////////////////////////////////////////////////////////////
//
//    /**
//     * This is the main simulation loop. It can be thought of as the main method of
//     * the federate. For a description of the basic flow of this federate, see the
//     * class level comments
//     */
//    @Override
//    public void execute() throws Exception {
//        this.runFederate(federateName);
//    }
//
//    public void runFederate(String federateName) throws Exception {
//        this.watchDog = SimulationWatchDog.getWatchDogInstance(this);
//
//        /////////////////////////////////////////////////
//        // 1 & 2. create the RTIambassador and Connect //
//        /////////////////////////////////////////////////
//        log("Creating RTIambassador");
//        rtiAmb = new Rti1516eFactory().getRtiAmbassador();
//        encoderFactory = new Rti1516eFactory().getEncoderFactory();
//
//        // connect to the RTI with our ambassador who will receive the RTI callbacks
//        log("Connecting...");
//        fedAmb = new TerminationFederateAmbassador(this);
//        rtiAmb.connect(fedAmb, CallbackModel.HLA_EVOKED);
//
//        //////////////////////////////
//        // 3. create the federation //
//        //////////////////////////////
//        // Since we organise each Federation with a MasterFederate,
//        // the InterpretedFederate should not attempt to create a Federation
//
//        ////////////////////////////
//        // 4. join the federation //
//        ////////////////////////////
//        URL[] joinModules = new URL[this.federateFoms.size() + 1];
//        for (String st : this.federateFoms) {
//            joinModules[this.federateFoms.indexOf(st)] = new File("FOMS//" + st).toURI().toURL();
//        }
//        joinModules[this.federateFoms.size()] = (getClass().getResource("/foms/SimulationFunctions.xml"));
//
//        rtiAmb.joinFederationExecution(federateName,    // name for the federate
//                                       "ExampleFederateType",               // federate type // TODO use proper name
//                                       "ExampleFederation",                 // name of federation // TODO use proper name
//                                       joinModules);                           // modules we want to add
//
//        log("Joined Federation as " + federateName);
//
//        // cache the time factory for easy access
//        this.timeFactory = (HLAfloat64TimeFactory) rtiAmb.getTimeFactory();
//
//        /////////////////////////////
//        // 5. enable time policies //
//        /////////////////////////////
//        // in this section we enable all time policies
//        // so the federates are synced in (simulation)-time
//        enableTimePolicy();
//        log("Time Policy Enabled");
//
//        //////////////////////////////
//        // 6. publish and subscribe //
//        //////////////////////////////
//        // in this section we tell the RTI of all the data we are going to
//        // produce, and all the data we want to know about
//
//        publishAndSubscribe();
//        log("Published and Subscribed");
//
//        rtiAmb.registerFederationSynchronizationPoint(READY_TO_RUN, null);
//
//        rtiAmb.synchronizationPointAchieved(READY_TO_RUN);
//
//        atSyncPoint = true;
//        synchronized (LOCK) {
//            //Tell the SimulationWatchDog that the federate is initialized (in case he waited for us)
//            LOCK.notify();
//        }
//
//        while (!fedAmb.isReadyToRun) {
//            rtiAmb.evokeMultipleCallbacks(0.1, 0.2);
//        }
//
//        /////////////////////////////////////
//        // 7. do the main simulation loop //
//        /////////////////////////////////////
//        // here is where we do the meat of our work. in each iteration, we will
//        // update the attribute values of the object we registered, and will
//        // send an interaction.
//        while (!reachedSimulationEnd()) {
//            if (this.watchDog.getSimulationHasEnded() || simulationEndIsDetected) {
//                log(this.federateName + " detected end of simulation, stopping federate. ");
//                simulationEndIsDetected = true;
//                sendSimulationEndInteraction();
//                break;
//            }
//            // check terminationConditions
//            for (TerminationCondition t : this.terminationConditions) {
//                if (t.conditionIsMet() && !simulationEndIsDetected) {
//                    log(this.federateName + " Termination condition triggered ");
//                    sendSimulationEndInteraction();
//                    simulationEndIsDetected = true;
//                    break;
//                }
//            }
//
//            // request a time advance and wait until we get it
//            advanceTime(1.0);
//            log("Time Advanced to " + fedAmb.federateTime);
//
//        }
//
//        ////////////////////////////////////
//        // 12. resign from the federation //
//        ////////////////////////////////////
//        rtiAmb.resignFederationExecution(ResignAction.DELETE_OBJECTS);
//        log("Resigned from Federation");
//
//        ////////////////////////////////////////
//        // 13. try and destroy the federation //
//        ////////////////////////////////////////
//        // NOTE: we won't die if we can't do this because other federates
//        //       remain. in that case we'll leave it for them to clean up
//        try {
//            rtiAmb.destroyFederationExecution("ExampleFederation");
//            log("Destroyed Federation");
//        } catch (FederationExecutionDoesNotExist dne) {
//            log("No need to destroy federation, it doesn't exist");
//        } catch (FederatesCurrentlyJoined fcj) {
//            log("Didn't destroy federation, federates still joined");
//        }
//    }
//
//    //----------------------------------------------------------
//    //                    INSTANCE METHODS
//    //----------------------------------------------------------
//
//    /**
//     * This is just a helper method to make sure all logging it output in the same form
//     */
//    private void log(String message) {
//        LoggingService.logWithAll(this.federateName + "   : " + message);
//    }
//
//    ////////////////////////////////////////////////////////////////////////////
//    ////////////////////////////// Helper Methods //////////////////////////////
//    ////////////////////////////////////////////////////////////////////////////
//
//    /**
//     * This method will attempt to enable the various time related properties for
//     * the federate
//     */
//    private void enableTimePolicy() throws Exception {
//        // NOTE: Unfortunately, the LogicalTime/LogicalTimeInterval create code is
//        //       Portico specific. You will have to alter this if you move to a
//        //       different RTI implementation. As such, we've isolated it into a
//        //       method so that any change only needs to happen in a couple of spots
//        HLAfloat64Interval lookahead = timeFactory.makeInterval(fedAmb.federateLookahead);
//
//        ////////////////////////////
//        // enable time regulation //
//        ////////////////////////////
//        this.rtiAmb.enableTimeRegulation(lookahead);
//
//        // tick until we get the callback
//        while (!fedAmb.isRegulating) {
//            rtiAmb.evokeMultipleCallbacks(0.1, 0.2);
//        }
//
//        /////////////////////////////
//        // enable time constrained //
//        /////////////////////////////
//        if (!fedAmb.isConstrained) {
//            this.rtiAmb.enableTimeConstrained();
//        }
//
//        // tick until we get the callback
//        while (!fedAmb.isConstrained) {
//            rtiAmb.evokeMultipleCallbacks(0.1, 0.2);
//        }
//    }
//
//    /**
//     * custom implementation of the generateHandles method from the interpretedFederate, since things are being handled differently here
//     *
//     * @param object                  object whose handles should be generated
//     * @param objectChainString       fomPathString depending on the iteration
//     * @param parentObjectClassHandle classHandle of the parent
//     * @param attributeIds
//     * @param objectId
//     * @throws NameNotFound
//     * @throws NotConnected
//     * @throws RTIinternalError
//     * @throws FederateNotExecutionMember
//     * @throws InvalidObjectClassHandle
//     */
//    private void generateHandles(Object object, String objectChainString, ObjectClassHandle parentObjectClassHandle, ArrayList<String> attributeIds, String objectId) throws NameNotFound, NotConnected, RTIinternalError, FederateNotExecutionMember, InvalidObjectClassHandle {
//
//        ArrayList<Field> fieldList = ReflectionUtils.getFieldsOfClass(object.getClass());
//        AttributeHandleSet subscribeSet = rtiAmb.getAttributeHandleSetFactory().create();
//        HashMap<AttributeHandle, SimulationAttribute> attrHandleSimAttrMap = new HashMap<>();
//
//        for (Field aField : fieldList) {
//            Object anObject;
//            //Create the Object from the field
//            anObject = ReflectionUtils.getValueObjectFromField(aField, object);
//            if (anObject == null) {
//                continue;
//            }
//
//            if (object instanceof Component && (aField.getName().equals("superComponent") || anObject instanceof SimulationInteraction || anObject instanceof IBehaviour || (aField.getName().equals("publish")) || aField.getName().equals("subscribe"))) {
//                //CASE: Field is not relevant or would cause infinite loop --> skip it
//                continue;
//            }
//            if (aField.getName().equals("subComponents") || aField.getName().equals("components") && anObject instanceof ArrayList) {
//                //CASE: Object is the subComponents or components Attribute and is thus an ArrayList of type Component
//                for (Component component : (ArrayList<Component>) anObject) {
//                    String fomClassString = objectChainString + "." + component.getClass().getSimpleName();
//                    ObjectClassHandle objectClassHandle = rtiAmb.getObjectClassHandle(fomClassString);
//                    generateHandles(component, fomClassString, objectClassHandle, attributeIds, component.getId());
//                }
//            } else if (anObject instanceof SimulationAttribute && (Interpreter.DATATYPE_MAP.containsKey(((SimulationAttribute<?>) anObject).getValue().getClass().getSimpleName()) || ((SimulationAttribute<?>) anObject).getValue() instanceof Enum || ((SimulationAttribute<?>) anObject).getValue().getClass() == HashMap.class)) {
//                //CASE: The Object is a SimulationAttribute of a primitive datatype
//                SimulationAttribute attribute = (SimulationAttribute) anObject;
//
//                if (!attributeIds.contains(attribute.getId())) {
//                    continue;
//                } else {
//                    AttributeHandle attributeHandle;
//                    boolean isCustomFomPath = (attribute.getFomClassString() != null);
//                    //Check if custom FOM path is set
//                    if (isCustomFomPath) {
//                        //Ignore Attributes with CustomFomPaths for now
//                        continue;
//                    } else {
//                        //If not, take default path but make sure parentObjectClassHandle is available
//                        if (parentObjectClassHandle != null) {
//                            attributeHandle = rtiAmb.getAttributeHandle(parentObjectClassHandle, aField.getName());
//                        } else {
//                            log("ERROR: Did not publish/subscribe " + aField.getName() + " because it is not part of a standard" +
//                                " structure. Define Custom FOM path to properly publish/subscribe this Attribute.");
//                            continue;
//                        }
//                    }
//                    subscribeSet.add(attributeHandle);
//                    attrHandleSimAttrMap.put(attributeHandle, attribute);
//                }
//            } else if (anObject instanceof SimulationObject || anObject instanceof Component) {
//                //CASE: Object is SimulationObject or Component
//                SimulationSuperClass theObject = (SimulationSuperClass) anObject;
//                ObjectClassHandle objectClassHandle = rtiAmb.getObjectClassHandle(objectChainString + "." + aField.getName());
//                generateHandles(anObject, objectChainString + "." + aField.getName(), objectClassHandle, attributeIds, theObject.getId());
//            } else if (anObject instanceof SimulationAttribute && !Interpreter.DATATYPE_MAP.containsKey(((SimulationAttribute<?>) anObject).getValue().getClass().getSimpleName())) {
//                //CASE: Object is a SimulationAttribute, but not a primitive Datatype
//                SimulationAttribute theAttribute = (SimulationAttribute) anObject;
//                Object anObject2 = theAttribute.getValue();
//                try {
//                    ObjectClassHandle objectClassHandle;
//                    String theObjectID = null;
//                    if (theAttribute.getFomClassString() != null) {
//                        objectClassHandle = rtiAmb.getObjectClassHandle(theAttribute.getFomClassString());
//                    } else {
//                        objectClassHandle = rtiAmb.getObjectClassHandle(objectChainString + "." + aField.getName());
//                        theObjectID = theAttribute.getId();
//                    }
//                    generateHandles(anObject2, theAttribute.getFomClassString(), objectClassHandle, attributeIds, theObjectID);
//                } catch (NameNotFound e) {
//                    log("ERROR during handle generation. Ignoring SimulationAttribute of type: " + theAttribute.getValue().getClass().getName() + " as it can not be handled as an ObjectClass, since it is not part of the FOM.");
//                    continue;
//                }
//            }
//        }
//
//        if (subscribeSet.size() > 0) {
//            subscribeMap.put(parentObjectClassHandle, subscribeSet);
//            objectIdAttributeMap.put(objectId, attrHandleSimAttrMap);
//        }
//    }
//
//    /**
//     * This method will inform the RTI about the types of data that the federate will
//     * be creating, and the types of data we are interested in hearing about as other
//     * federates produce it.
//     */
//    private void publishAndSubscribe() throws RTIexception {
//
//        // get all the handle information for the attributes of the simulated objects
//        for (Map.Entry<SimulationObject, ArrayList<String>> anEntry : this.simObjToAttrIdMap.entrySet()) {
//            SimulationObject simObject = anEntry.getKey();
//            String rootObject = "HLAobjectRoot." + simObject.getClass().getSimpleName();
//            ObjectClassHandle objectClassHandle = rtiAmb.getObjectClassHandle(rootObject);
//            generateHandles(simObject, rootObject, objectClassHandle, anEntry.getValue(), simObject.getId());
//        }
//
//        // do the actual subscriptions
//        for (Map.Entry<ObjectClassHandle, AttributeHandleSet> anEntry : subscribeMap.entrySet()) {
//            rtiAmb.subscribeObjectClassAttributes(anEntry.getKey(), anEntry.getValue());
//        }
//
//        //////////////////////////////////////////////////////////////////////
//        // publish to interaction class SimulationEnd //
//        //////////////////////////////////////////////////////////////////////
//        String interactionName = "HLAinteractionRoot.SimulationEnd";
//        simulationEndHandle = rtiAmb.getInteractionClassHandle(interactionName);
//        rtiAmb.publishInteractionClass(simulationEndHandle);
//        rtiAmb.subscribeInteractionClass(simulationEndHandle);
//
//    }
//
//    /**
//     * sends the SimulationEndInteraction which tells the other Federates that the simulation should end
//     *
//     * @throws RTIexception
//     */
//    private void sendSimulationEndInteraction() throws RTIexception {
//        //////////////////////////
//        // send the interaction //
//        //////////////////////////
//        ParameterHandleValueMap parameters = rtiAmb.getParameterHandleValueMapFactory().create(0);
//        HLAfloat64Time time = timeFactory.makeTime(fedAmb.federateTime + fedAmb.federateLookahead);
//        rtiAmb.sendInteraction(simulationEndHandle, parameters, generateTag(), time);
//        log(" SimulationEnd Interaction was send ");
//    }
//
//    public void addObjectInstanceHandle(ObjectInstanceHandle instanceHandle, String objectId) {
//        if (objectIdAttributeMap.containsKey(objectId)) {
//            instanceHandleAttributeMap.put(instanceHandle, objectIdAttributeMap.get(objectId));
//            objectIdAttributeMap.remove(objectId);
//        }
//    }
//
//    /**
//     * This method handles the incoming attribute reflections from the RTI
//     *
//     * @param theObject
//     * @param theAttributes
//     */
//    public void handleAttributeUpdates(ObjectInstanceHandle theObject, AttributeHandleValueMap theAttributes) {
//        if (this.instanceHandleAttributeMap.containsKey(theObject)) {
//            HashMap<AttributeHandle, SimulationAttribute> attrHandleSimAttrHashMap = this.instanceHandleAttributeMap.get(theObject);
//            for (AttributeHandle attributeHandle : theAttributes.keySet()) {
//                if (attrHandleSimAttrHashMap.containsKey(attributeHandle)) {
//                    SimulationAttribute simAttr = attrHandleSimAttrHashMap.get(attributeHandle);
//                    this.updateAttribute(simAttr, theAttributes.get(attributeHandle));
//                }
//            }
//        }
//    }
//
//    /**
//     * this method updates a single attribute depending on a byte value (encoded through the RTI)
//     *
//     * @param simulationAttribute
//     * @param value
//     */
//    public void updateAttribute(SimulationAttribute simulationAttribute, byte[] value) {
//        if (simulationAttribute != null) {
//            DataElement decoder = null;
//            try {
//                String hlaDataType;
//                Class type = simulationAttribute.getValue().getClass();
//                boolean isEnum = type.isEnum();
//                if (isEnum) {
//                    hlaDataType = Interpreter.DATATYPE_MAP.get("String");
//                } else {
//                    hlaDataType = Interpreter.DATATYPE_MAP.get(type.getSimpleName());
//                }
//
//                Method getDecoder = encoderFactory.getClass().getMethod("create" + hlaDataType);
//
//                decoder = (DataElement) getDecoder.invoke(encoderFactory);
//
//                decoder.decode(value);
//                Method getValue = decoder.getClass().getMethod("getValue");
//                Object decodedValue = getValue.invoke(decoder);
//                if (isEnum) {
//                    Method valueOf = type.getMethod("valueOf", String.class);
//                    decodedValue = valueOf.invoke(type, decodedValue);
//                }
//                simulationAttribute.setSingleValue(decodedValue);
//            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | DecoderException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public void handleSimulationEnd() {
//        this.simulationEndIsDetected = true;
//    }
//
//    private boolean reachedSimulationEnd() {
//        if (simulationEndIsDetected) {
//            return true;
//        } else if (!unlimitedIterations) {
//            if (currentIteration >= ITERATIONS) {
//                log("Ending Simulation after max Iterations of: " + String.valueOf(ITERATIONS));
//                return true;
//            } else {
//                currentIteration++;
//            }
//        }
//        return false;
//    }
//
//    /**
//     * This method will request a time advance to the current time, plus the given
//     * timestep. It will then wait until a notification of the time advance grant
//     * has been received.
//     */
//    private void advanceTime(double timestep) throws RTIexception {
//        // request the advance
//        fedAmb.isAdvancing = true;
//        HLAfloat64Time time = timeFactory.makeTime(fedAmb.federateTime + timestep);
//        rtiAmb.timeAdvanceRequest(time);
//
//        // wait for the time advance to be granted. ticking will tell the
//        // LRC to start delivering callbacks to the federate
//        while (fedAmb.isAdvancing) {
//            rtiAmb.evokeMultipleCallbacks(0.1, 0.2);
//        }
//    }
//
//    /**
//     * generates a tag as byte[] based on the simulatedObject ID
//     *
//     * @return
//     */
//    private byte[] generateTag() {
//        return this.id.getBytes();
//    }
//
//    @Override
//    public boolean isAtSyncPoint() {
//        return atSyncPoint;
//    }
//
//    @Override
//    public RTIambassador getRtiAmb() {
//        return rtiAmb;
//    }
//
//    public SimulationWatchDog getWatchDog() {
//        return watchDog;
//    }
//
//    //----------------------------------------------------------
//    //                     STATIC METHODS
//    //----------------------------------------------------------
//    // QUESTION this not even a static method... why this comment?
//    @Override
//    public void run() {
//        try {
//            execute();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
