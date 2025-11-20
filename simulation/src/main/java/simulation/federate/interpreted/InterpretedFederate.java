package simulation.federate.interpreted;

import hla.rti1516e.AttributeHandle;
import hla.rti1516e.AttributeHandleSet;
import hla.rti1516e.CallbackModel;
import hla.rti1516e.ObjectClassHandle;
import hla.rti1516e.ObjectInstanceHandle;
import hla.rti1516e.ResignAction;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.exceptions.FederateNotExecutionMember;
import hla.rti1516e.exceptions.FederatesCurrentlyJoined;
import hla.rti1516e.exceptions.FederationExecutionDoesNotExist;
import hla.rti1516e.exceptions.InvalidObjectClassHandle;
import hla.rti1516e.exceptions.NameNotFound;
import hla.rti1516e.exceptions.NotConnected;
import hla.rti1516e.exceptions.RTIexception;
import hla.rti1516e.exceptions.RTIinternalError;
import hla.rti1516e.time.HLAfloat64TimeFactory;
import interpreter.ReferenceStore;
import interpreter.utils.ReflectionUtils;
import library.model.simulation.Behaviour;
import library.model.simulation.objects.ActiveSimulationObject;
import library.model.simulation.objects.DynamicSimulationObject;
import library.model.simulation.objects.IActiveDynamic;
import library.model.simulation.IBehaviour;
import library.model.simulation.Identifier;
import library.model.simulation.SimulationInteraction;
import library.model.simulation.objects.SimulationObject;
import library.model.simulation.SimulationProperty;
import library.model.simulation.Task;
import library.services.logging.LoggingService;
import library.services.logging.LoggingType;
import org.apache.commons.lang3.tuple.MutablePair;
import org.jetbrains.annotations.NotNull;
import org.portico.impl.hla1516e.Rti1516eFactory;
import simulation.federate.AbstractFederate;
import simulation.federate.SimulationFederateAmbassador;
import simulation.federate.handler.IncomingDataHandler;
import simulation.federate.handler.OutgoingDataHandler;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import static library.services.logging.LoggingService.log;

/**
 * A Federate that is instantiated by the Interpreter and represents (contains and calls) a SimulationObject.
 */
public class InterpretedFederate extends AbstractFederate {

    //----------------------------------------------------------
    //                   INSTANCE VARIABLES
    //----------------------------------------------------------

    // HLA STUFF
    protected EncoderFactory encoderFactory;

    // COMMUNICATION OBJECTS
    private OutgoingDataHandler outgoingDataHandler;

    // STATUS
    private boolean atSyncPoint = false;
    private boolean simulationEndIsDetected = false;
    private final int iterations;

    // SIMULATION CONTENTS
    private final ReferenceStore referenceStore;
    private final DynamicSimulationObject simulatedObject;

    // SIMULATION OBJECT STUFF
    private final ArrayList<IBehaviour> behaviours;
    private ArrayList<SimulationInteraction> interactionsToFire; // TODO implement usage
    private HashMap<String, LinkedBlockingQueue<Task>> taskQueues = new HashMap<>(); // TODO check if a ConcurrentLinkedQueue would be more suitable here.

    //----------------------------------------------------------
    //                      CONSTRUCTORS
    //----------------------------------------------------------
    public InterpretedFederate(ReferenceStore referenceStore, int iterations) throws FederateNotExecutionMember {

        super(referenceStore.getSimulationObject().getTimeStepSize());

        LoggingService.registerLogger(LoggingType.CONSOLE); // TODO make logger configurable
        Logger logger = Logger.getAnonymousLogger();
        LogManager manager = LogManager.getLogManager();
        try {
            manager.readConfiguration(new FileInputStream("simulation/properties/logging.properties"));
            logger.getHandlers();
        } catch (IOException e) {
            logger.warning(e.getMessage());
        }

        this.LOCK = new Object();
        this.referenceStore = referenceStore;
        this.simulatedObject = referenceStore.getSimulationObject();
        this.behaviours = new ArrayList<>();

        findAllActiveDynamics(this.simulatedObject, new HashSet<>(), new HashSet<>());

        String name = null;
        ArrayList<Field> fieldArrayList = ReflectionUtils.getFieldsOfClass(simulatedObject.getClass());

        // Search for a name attribute inside the simulated object
        for (Field aField : fieldArrayList) {
            if (aField.getName().toLowerCase().contains("name")) {
                try {
                    String property = aField.getName();
                    Method method = new PropertyDescriptor(property, simulatedObject.getClass(), "is" + Character.toUpperCase(property.charAt(0)) + property.substring(1), null).getReadMethod();
                    if (method == null) {
                        continue;
                    }

                    Object nameAttribute = method.invoke(simulatedObject);
                    if (nameAttribute instanceof String) {
                        name = (String) nameAttribute;

                    } else if (nameAttribute instanceof SimulationProperty) {
                        name = ((SimulationProperty<String>) nameAttribute).getValue();
                    }
                    name = name + " - " + simulatedObject.getId();
                } catch (IllegalAccessException | InvocationTargetException | IntrospectionException | ClassCastException e) {
                    e.printStackTrace();
                    // TODO proper error handling
                }
            }
        }
        if (name == null || name.isBlank()) {
            name = simulatedObject.getClass().getSimpleName() + "-" + simulatedObject.getId();
        }

        this.iterations = iterations;
        this.federateName = "Federate(" + name + ")";
    }

    ///////////////////////////////////////////////////////////////////////////
    ////////////////////////// Main Simulation Method /////////////////////////
    ///////////////////////////////////////////////////////////////////////////

    /**
     * This is the main simulation loop. It can be thought of as the main method of
     * the federate. For a description of the basic flow of this federate, see the
     * class level comments
     */
    @Override
    public void execute() throws Exception {

        /////////////////////////////////////////////////
        // 1 & 2. create the RTIambassador and Connect //
        /////////////////////////////////////////////////
        log("Creating RTIambassador");
        rtiAmb = new Rti1516eFactory().getRtiAmbassador();
        encoderFactory = new Rti1516eFactory().getEncoderFactory();

        // connect with our federate ambassador
        log("Connecting...");
        incomingDataHandler = new IncomingDataHandler(this);
        fedAmb = new SimulationFederateAmbassador(this, incomingDataHandler);
        rtiAmb.connect(fedAmb, CallbackModel.HLA_EVOKED);

        //////////////////////////////
        // 3. create the federation //
        //////////////////////////////
        // Since each Federation here is organized with a MasterFederate,
        // the ObserverFederate should not attempt to create a Federation

        ////////////////////////////
        // 4. join the federation //
        ////////////////////////////

        //Here the proper FOM-Modules for joining are selected
        URL[] joinModules = new URL[]{
                referenceStore.getFom().getPath().toFile().toURI().toURL(),
                (getClass().getResource("/foms/SimulationFunctions.xml"))
        };

        rtiAmb.joinFederationExecution(this.federateName,    // name for the federate
                "InterpretedFederate",               // federate type
                "ExampleFederation",                 // name of federation TODO use a proper name
                joinModules);                           // modules we want to add

        log("Joined Federation as " + this.federateName);

        // cache the time factory for easy access
        this.timeFactory = (HLAfloat64TimeFactory) rtiAmb.getTimeFactory();

        //create the OutgoingDataHandler which will handle our outgoing data traffic
        this.outgoingDataHandler = new OutgoingDataHandler(this, generateTag());

        ////////////////////////////////
        // 5. announce the sync point //
        ////////////////////////////////
        // announce a sync point to get everyone on the same page. if the point
        // has already been registered, we'll get a callback saying it failed,
        // but we don't care about that, as long as someone registered it
        rtiAmb.registerFederationSynchronizationPoint(READY_TO_RUN, null);

        // wait until the point is announced
        while (!fedAmb.isAnnounced()) {
            rtiAmb.evokeMultipleCallbacks(0.1, 0.2);
            System.out.println("Still waiting for announce");
        }

        ///////////////////////////////////////////////////////
        // 6. achieve the point and wait for synchronization //
        ///////////////////////////////////////////////////////
        // tell the RTI we are ready to move past the sync point and then wait
        // until the federation has synchronized on
        rtiAmb.synchronizationPointAchieved(READY_TO_RUN);

        // TODO this needs to be removed if the Federation is to run physically distributed in the future.
        atSyncPoint = true;
        synchronized (LOCK) {
            //Tell the SimulationWatchDog that the federate is initialized (in case he waited for us)
            LOCK.notify();
        }

        //Wait for the sync-point to be achieved by everyone (last one will be the masterFederate)
        while (!fedAmb.isReadyToRun()) {
            rtiAmb.evokeMultipleCallbacks(0.1, 0.2);
        }

        /////////////////////////////
        // 7. enable time policies //
        /////////////////////////////
        // in this section we enable all time policies
        // so the federates are synced in (simulation)-time
        enableTimePolicy();
        log("Time Policy Enabled");

        //////////////////////////////
        // 8. publish and subscribe //
        //////////////////////////////
        // in this section we tell the RTI about all the data we are going to
        // produce, and all the data we want to know about
        publish();
        subscribe();
        log("Published and Subscribed");

        ///////////////////////////////////////
        // 9. register our objects to update //
        ///////////////////////////////////////
        //register our actual instanced attributes at the RTI
        outgoingDataHandler.registerInstances();

        // register mainFederateObject for the communication with the MasterFederate
        // mainFederateInstanceHandle = rtiAmb.registerObjectInstance(mainFederateClassHandle);

        //initially share the values of the attributes
        outgoingDataHandler.updateAttributeValues();

        /////////////////////////////////////
        // 10. do the main simulation loop //
        /////////////////////////////////////
        // here is where we do the meat of our work. in each iteration, we will
        // update the attribute values of the object we registered, and will
        // fire all interactions that need to be fired.
        while (!reachedSimulationEnd()) {

            // 9.1 update the attribute values of the instance //
            executeBehaviours();
            executeAllTasks();

            // 9.2 reflect the updated values to the RTI and fire Interactions
            outgoingDataHandler.updateAttributeValues();

            // 9.3 request a time advance and wait until we get it
            advanceTime(timeStepSize);
            log("Time Advanced to " + fedAmb.getFederateTime());
        }

        //////////////////////////////////////
        // 9. delete the objects we created //
        //////////////////////////////////////
        outgoingDataHandler.destroyAllObjects();
        // rtiAmb.deleteObjectInstance(mainFederateInstanceHandle, generateTag());

        ////////////////////////////////////
        // 10. resign from the federation //
        ////////////////////////////////////
        rtiAmb.resignFederationExecution(ResignAction.DELETE_OBJECTS);
        log("Resigned from Federation");

        ////////////////////////////////////////
        // 11. try and destroy the federation //
        ////////////////////////////////////////
        // NOTE: we won't die if we can't do this because other federates
        //       remain. in that case we'll leave it for them to clean up
        try {
            rtiAmb.destroyFederationExecution("ExampleFederation");
            log("Destroyed Federation");
        } catch (FederationExecutionDoesNotExist dne) {
            log("No need to destroy federation, it doesn't exist");
        } catch (FederatesCurrentlyJoined fcj) {
            log("Didn't destroy federation, federates still joined");
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////// Helper Methods //////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     * This method will inform the RTI about the types of data that the federate will be creating
     *
     * @throws RTIexception
     */
    private void publish() throws RTIexception {
        // generate the handles
        generateDataPublicationHandles();
        generateInteractionPublicationHandles();

        // do the actual publication and subscriptions
        outgoingDataHandler.publishAttributes();
        outgoingDataHandler.publishInteractions();
    }

    /**
     * This method will inform the RTI about the types of data we are interested in hearing about as other federates produce it.
     *
     * @throws RTIexception
     */
    private void subscribe() throws RTIexception {
        // generate the handles
        generateDataSubscriptionHandles(simulatedObject.getObservedClasses());
        generateInteractionSubscriptionHandles();

        // move the actual subscription to the incoming data handler (or federate ambassador?)
        // incomingDataHandler.subscribeAttributes();
        // incomingDataHandler.subscribeInteractions();
    }

    /**
     * // TODO update javadoc description after method got refactored
     * This method generates all the RTI Attribute publication handles for the FOM data that we are going to publish.
     * Therefore it iterates over all given objectClass and gives their handles into the Incoming/
     * OutgoingDataHandler
     *
     * @throws NameNotFound
     * @throws NotConnected
     * @throws RTIinternalError
     * @throws FederateNotExecutionMember
     * @throws InvalidObjectClassHandle
     */
    private void generateDataPublicationHandles() throws NameNotFound, NotConnected, RTIinternalError, FederateNotExecutionMember, InvalidObjectClassHandle {

        SimulationObject simulationObject = referenceStore.getSimulationObject();
        String simulatedObjectFomPath = referenceStore.getFomPathForSimulationObject(simulationObject);
        ObjectClassHandle objectClassHandle = rtiAmb.getObjectClassHandle(simulatedObjectFomPath);

        // keeps the attribute handles that should be published
        AttributeHandleSet publishSet = rtiAmb.getAttributeHandleSetFactory().create();

        // contains all SimulationProperties of this SimulationObject
        final Set<SimulationProperty<?>> allSimulationProperties = referenceStore.getAllSimulationAttributes();

        // keeps the actual simulationAttribute instances that should be published
        Set<SimulationProperty<?>> simAttributesToPublish = new HashSet<>();

        for (SimulationProperty<?> simulationProperty : allSimulationProperties) {
            // added '&& simulationProperty.getName() != null' to avoid exception
            // TODO reconsider mapping of attribute handles by name string, maybe use id's instead?
            if (simulationProperty.isPublish() && simulationProperty.getName() != null) {
                AttributeHandle attributeHandle = rtiAmb.getAttributeHandle(objectClassHandle, simulationProperty.getName());
                // if attribute is publish, add it to the outgoingDataHandler
                outgoingDataHandler.registerPublishProperty(simulationProperty, attributeHandle);
                publishSet.add(attributeHandle);
                simAttributesToPublish.add(simulationProperty);
            }
        }

        // Tell the outgoingDataHandler about an ObjectInstance that should be registered
        if (simAttributesToPublish.size() > 0) {
            outgoingDataHandler.addInstanceToRegisterList(objectClassHandle, new ArrayList<>(simAttributesToPublish), referenceStore.getSimulationObject().getId());
        }

        // Tell the outgoingDataHandler about the new AttributeHandles that should be published
        if (publishSet.size() > 0) {
            outgoingDataHandler.addToAttributePublishList(new MutablePair<>(objectClassHandle, publishSet));
        }
    }

    private void generateInteractionPublicationHandles() {
        // TODO implement interaction publishing
    }

    /**
     * Recursively searches the datastructures of an object for possibly executable implementations of IActiveDynamic
     *
     * @param mainObject      the object in which to search for
     * @param iActiveDynamics HashSet for the found IActiveDynamic objects
     * @return HashSet of all found IActiveDynamic Objects
     */
    private HashSet<IActiveDynamic> findAllActiveDynamics(Object mainObject, HashSet<IActiveDynamic> iActiveDynamics, HashSet<Object> visistedNodes) {
        if (mainObject instanceof IActiveDynamic) {
            if (((IActiveDynamic) mainObject).getBehaviour() != null) {
                IBehaviour behaviour = ((IActiveDynamic) mainObject).getBehaviour();
                if (behaviour != null) {
                    //Behaviour is executable, save for later execution
                    behaviour.setSimulationObject(this.simulatedObject);
                    behaviours.add(behaviour);
                }
            } else if (mainObject instanceof ActiveSimulationObject
                    && ((ActiveSimulationObject) mainObject).getLinkedMethodCall() != null
                    && ((ActiveSimulationObject) mainObject).getLinkedMethodCall().getPath() != null) {
                Identifier mc = ((ActiveSimulationObject) mainObject).getLinkedMethodCall().getPath();
                try {
                    Class c = Class.forName(mc.getClazz());
                    System.out.println(c.getName());
                    Object o = c.getDeclaredConstructor().newInstance();
                    //TODO works only if the standard nextStep method is used
                    if (o instanceof Behaviour && mc.getMethod().equals("nextStep(double)")) {
                        Behaviour b = (Behaviour) o;
                        // TODO behaviour execution deactivated because it causes exception
                        // b.setSimulationObject(this.simulatedObject);
                        // behaviours.add(b);
                        // ((IActiveDynamic) this.simulatedObject).setBehaviour(b);
                    }
                } catch (ClassNotFoundException | InvocationTargetException | InstantiationException
                        | IllegalAccessException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
            iActiveDynamics.add((IActiveDynamic) mainObject);
        }
        ArrayList<Field> fieldList = ReflectionUtils.getFieldsOfClass(mainObject.getClass());

        for (Field aField : fieldList) {
            Object anObject = ReflectionUtils.getValueObjectFromField(aField, mainObject);
            if (anObject == null) {
                continue;
            }
            if (visistedNodes.contains(anObject)) {
                continue;
            } else {
                visistedNodes.add(anObject);
            }
            if (anObject instanceof SimulationProperty) {
                iActiveDynamics = findAllActiveDynamics(((SimulationProperty) anObject).getValue(), iActiveDynamics, visistedNodes);
            } else if (anObject instanceof Collection) {
                for (Object secondObject : (Collection) anObject) {
                    if (secondObject instanceof SimulationProperty) {
                        iActiveDynamics = findAllActiveDynamics(((SimulationProperty) secondObject).getValue(), iActiveDynamics, visistedNodes);
                    } else {
                        iActiveDynamics = findAllActiveDynamics(secondObject, iActiveDynamics, visistedNodes);
                    }
                }
            } else {
                iActiveDynamics = findAllActiveDynamics(anObject, iActiveDynamics, visistedNodes);
            }
        }

        return iActiveDynamics;
    }

    /**
     * execute all behaviours in the simulated object
     * usually called once in each time cycle
     */
    private void executeBehaviours() {
        for (IBehaviour behaviour : behaviours) {
            Map<String, Object> attributesToUpdate = behaviour.nextStep(this.timeStepSize);
            attributesToUpdate.entrySet()
                    .stream()
                    .forEach(entry -> pushTask(new Task(entry.getKey(), entry.getValue())));
        }
    }

    /**
     * generates a tag as byte[] based on the simulatedObject ID
     *
     * @return
     */
    private byte[] generateTag() {
        return this.simulatedObject.getId().getBytes();
    }

    /**
     * called from the InterpretedFederateAmbassador when the simulationEnd is received
     */
    protected void handleSimulationEndInteraction() {
        this.simulationEndIsDetected = true;
    }

    /**
     * loop condition
     *
     * @return
     */
    private boolean reachedSimulationEnd() {
        if (simulationEndIsDetected) {
            LoggingService.logWithAll(this.federateName + "detected end of simulation");
            return true;
        } else if (iterations > 0) {
            if (currentIteration >= iterations) {
                log("Ending Federate after max Iterations of: " + iterations);
                return true;
            } else {
                currentIteration++;
            }
        }
        return false;
    }

    @Override
    public boolean isAtSyncPoint() {
        return atSyncPoint;
    }

    public String getFederateName() {
        return federateName;
    }

    //----------------------------------------------------------
    //                     STATIC METHODS
    //----------------------------------------------------------

    public DynamicSimulationObject getSimulatedObject() {
        return this.simulatedObject;
    }

    public Set<SimulationProperty<?>> getAllSimulationAttributeInstances() {
        return referenceStore.getUuidToSimulationAttributeBiMap().values();
    }

    public Set<SimulationInteraction> getAllSimulationInteractions() {
        return referenceStore.getUuidToSimulationInteractionBiMap().values();
    }

    public SimulationProperty<?> getSimulationAttributeByID(String attributeID) {
        return referenceStore.getSimulationAttributeByUUID(attributeID);
    }

    private boolean objectHasAttribute(String attributeID) {
        return getSimulationAttributeByID(attributeID) != null;
    }

    /*
    the following methods got moved here from the simulationObject class
    it therefore have to be adapted to this move
    after doing so, find a better place in this class
     */
    public void pushTask(Task task) {
        if (!objectHasAttribute(task.getAttributeID())) {
            // throw new AttributeNotFoundException();
            log(task.getAttributeID() + "  Value to Change couldn't be found.");
        }
        if (this.taskQueues == null) {
            this.taskQueues = new HashMap<>();
        }
        if (!taskQueues.containsKey(task.getAttributeID())) {
            taskQueues.put(task.getAttributeID(), new LinkedBlockingQueue<>());
        }
        taskQueues.get(task.getAttributeID()).add(task);
    }

    public void executeAllTasks() {
        for (Map.Entry<String, LinkedBlockingQueue<Task>> propertyUpdateList : this.taskQueues.entrySet()) {
            for (Task task : propertyUpdateList.getValue()) {
                executeTask(task);
                propertyUpdateList.getValue().remove(task);
            }
        }
    }

    public void executeTask(@NotNull Task task) {
        //Check if the TrafficParticipant contains a SimulationAttribute with the SimulationAttributeID
        SimulationProperty propertyToUpdate = referenceStore.getSimulationAttributeByUUID(task.getAttributeID());
        if (propertyToUpdate == null) {
            // LOGGER.log(Level.INFO, this.simulationObject.getId() + "  Value to Change couldn't be found.");
            log(task.getAttributeID() + "  Value to update couldn't be found.");
            return;
        } else {
            propertyToUpdate.setSingleValue(task.getValue());
        }
    }

    public Task getNextTask(String attributeID) {
        if (taskQueues.containsKey(attributeID)) {
            return taskQueues.get(attributeID).poll();
        }
        return null;
    }

    public ReferenceStore getReferenceStore() {
        return referenceStore;
    }

    public HashMap<String, LinkedBlockingQueue<Task>> getTaskQueues() {
        return taskQueues;
    }

    @Override
    public void cacheObjectInstance(@NotNull SimulationObject simulationObject,
                                    @NotNull ObjectInstanceHandle objectInstanceHandle) {
        objectInstanceHandleBiMap.put(simulationObject, objectInstanceHandle);
        this.simulatedObject.addObservedObject(simulationObject);
    }

}
