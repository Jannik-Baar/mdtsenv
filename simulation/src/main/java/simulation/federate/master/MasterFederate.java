package simulation.federate.master;

import hla.rti1516e.*;
import hla.rti1516e.exceptions.FederationExecutionAlreadyExists;
import hla.rti1516e.exceptions.RTIexception;
import hla.rti1516e.time.HLAfloat64Interval;
import hla.rti1516e.time.HLAfloat64Time;
import hla.rti1516e.time.HLAfloat64TimeFactory;
import simulation.coordination.SynchronisationPoint;
import library.services.logging.LoggingService;
import library.services.logging.LoggingType;
import org.portico.impl.hla1516e.Rti1516eFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Simple Federate that initiates a Federation, and then blocks at a syncPoint.
 * After the other Federates are set up, using the same Sync Point, the FederationFullySynced boolean should be
 * set to true, which finally lets this federate achieve the sync point.
 */
public class MasterFederate implements Runnable {

    // TODO store information like this in a configuration file or service?
    private static final String SIMULATION_FUNCTION_FOM_PATH = "/foms/SimulationFunctions.xml";
    private static final String EMPTY_FOM_PATH = "/foms/EmptyFOM.xml";

    public Object LOCK = new Object();
    public InteractionClassHandle simulationEndHandle;
    public ObjectClassHandle mainFederateClassHandle;

    private final String federateId;
    private final String federateName;
    private final String federateType;
    private final String federationName;
    private final int iterations;
    private final boolean iterationsUnlimited;

    private int currentIteration = 0;
    private boolean syncPointAnnounced = false;
    private boolean federationFullySynced = false;
    private boolean mainFederateDetected = false;
    private boolean simulationEndIsDetected = false;
    private RTIambassador rtiAmb;
    private MasterFederateAmbassador fedAmb;
    private ArrayList<ObjectInstanceHandle> mainFederates = new ArrayList<>();
    private HLAfloat64TimeFactory timeFactory; // set when we join

    public MasterFederate(int iterations) {
        LoggingService.registerLogger(LoggingType.CONSOLE);
        this.iterations = iterations;
        this.iterationsUnlimited = iterations <= 0;
        this.federateId = UUID.randomUUID().toString();
        this.federateName = "MasterFederate";
        this.federateType = "MasterFederate";
        this.federationName = "ExampleFederation";
    }

    public void run() {
        try {
            runFederate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void runFederate() throws Exception {
        rtiAmb = new Rti1516eFactory().getRtiAmbassador();
        fedAmb = new MasterFederateAmbassador(this);

        rtiAmb.connect(fedAmb, CallbackModel.HLA_EVOKED);

        log("Creating Simulation Federation");
        try {
            URL[] modules = new URL[]{getClass().getResource(EMPTY_FOM_PATH)};
            rtiAmb.createFederationExecution(federationName, modules); // TODO get name from scenario file
            log("Created Federation");
        } catch (FederationExecutionAlreadyExists exists) {
            log("Did not create federation, it already existed");
        }

        URL[] joinModules = new URL[]{
                (getClass().getResource(SIMULATION_FUNCTION_FOM_PATH))
        };

        rtiAmb.joinFederationExecution(federateName, federateType, federationName, joinModules);

        log("Joined Federation as " + federateName);

        // cache the time factory for easy access
        this.timeFactory = (HLAfloat64TimeFactory) rtiAmb.getTimeFactory();

        //Subscribe to Main Federates
        mainFederateClassHandle = rtiAmb.getObjectClassHandle("HLAobjectRoot.MainFederate");
        AttributeHandleSet handleSet = rtiAmb.getAttributeHandleSetFactory().create();
        AttributeHandle attributeHandle = rtiAmb.getAttributeHandle(mainFederateClassHandle, "dummyAttribute");
        handleSet.add(attributeHandle);

        //Publish and Subscribe SimulationEnd Interaction
        rtiAmb.subscribeObjectClassAttributes(mainFederateClassHandle, handleSet);
        simulationEndHandle = rtiAmb.getInteractionClassHandle("HLAinteractionRoot.SimulationEnd");
        rtiAmb.publishInteractionClass(simulationEndHandle);
        rtiAmb.subscribeInteractionClass(simulationEndHandle);

        // Set Up SyncPoint for Blocking
        rtiAmb.registerFederationSynchronizationPoint(SynchronisationPoint.READY_TO_RUN.toString(), null);

        while (!fedAmb.syncPointAnnounced) {
            rtiAmb.evokeMultipleCallbacks(0.1, 0.2);
        }
        syncPointAnnounced = true;

        synchronized (LOCK) {
            log("SyncPoint achieved, trying to wake up Watchdog");
            LOCK.notify();
            while (!federationFullySynced) {
                log("Federation not fully synced yet... Waiting...");
                LOCK.wait();
            }
        }

        log("Federation fully synced, starting Simulation...");

        rtiAmb.synchronizationPointAchieved(SynchronisationPoint.READY_TO_RUN.toString());

        while (!fedAmb.isReadyToRun) {
            rtiAmb.evokeMultipleCallbacks(0.1, 0.2);
        }

        enableTimePolicy();
        log("Time Policy Enabled");

        while (!reachedSimulationEnd()) {
            advanceTime();
        }

        if (!simulationEndIsDetected) {
            sendSimulationEndInteraction();
        }

        /** Resign after End of Simulation **/
        rtiAmb.resignFederationExecution(ResignAction.NO_ACTION);
    }

    public String getFederateName() {
        return federateName;
    }

    public boolean isSyncPointAnnounced() {
        return syncPointAnnounced;
    }

    public void setFederationFullySynced(boolean value) {
        federationFullySynced = value;
    }

    protected void log(String message) {
        LoggingService.logWithAll(this.federateName + "   : " + message);
    }

    public void addMainFederate(ObjectInstanceHandle federateHandle) {
        this.mainFederateDetected = true;
        this.mainFederates.add(federateHandle);
        log("Federate detected");
    }

    public void removeMainFederate(ObjectInstanceHandle federateHandle) {
        this.mainFederates.remove(federateHandle);
        log("Federate removed");
    }

    private boolean reachedSimulationEnd() {
        if ((this.mainFederateDetected && this.mainFederates.isEmpty()) || simulationEndIsDetected) {
            return true;
        } else if (!iterationsUnlimited) {
            if (currentIteration >= iterations) {
                log("Ending Simulation after max Iterations of: " + iterations);
                return true;
            } else {
                currentIteration++;
            }
        }
        return false;
    }

    private void enableTimePolicy() throws Exception {
        // NOTE: Unfortunately, the LogicalTime/LogicalTimeInterval create code is
        //       Portico specific. You will have to alter this if you move to a
        //       different RTI implementation. As such, we've isolated it into a
        //       method so that any change only needs to happen in a couple of spots
        HLAfloat64Interval lookahead = timeFactory.makeInterval(fedAmb.federateLookahead);

        ////////////////////////////
        // enable time regulation //
        ////////////////////////////
        this.rtiAmb.enableTimeRegulation(lookahead);

        // tick until we get the callback
        while (!fedAmb.isRegulating) {
            rtiAmb.evokeMultipleCallbacks(0.1, 0.2);
        }

        /////////////////////////////
        // enable time constrained //
        /////////////////////////////
        if (!fedAmb.isConstrained) {
            this.rtiAmb.enableTimeConstrained();
        }

        // tick until we get the callback
        while (!fedAmb.isConstrained) {
            rtiAmb.evokeMultipleCallbacks(0.1, 0.2);
        }
    }

    /**
     * This method will request a time advance to the current time.
     * It will then wait until a notification of the time advance grant
     * has been received.
     */
    private void advanceTime() throws RTIexception {
        // request the advance
        fedAmb.isAdvancing = true;
        HLAfloat64Time time = timeFactory.makeTime(fedAmb.federateTime + fedAmb.federateLookahead);
        rtiAmb.timeAdvanceRequest(time);

        // wait for the time advance to be granted. ticking will tell the
        // LRC to start delivering callbacks to the federate
        while (fedAmb.isAdvancing) {
            rtiAmb.evokeMultipleCallbacks(0.1, 0.2);
        }
    }

    private void sendSimulationEndInteraction() throws RTIexception {
        //////////////////////////
        // send the interaction //
        //////////////////////////
        ParameterHandleValueMap parameters = rtiAmb.getParameterHandleValueMapFactory().create(0);
        HLAfloat64Time time = timeFactory.makeTime(fedAmb.federateTime + fedAmb.federateLookahead);
        rtiAmb.sendInteraction(simulationEndHandle, parameters, generateTag(), time);
        log(" SimulationEnd Interaction was send ");
    }

    public void handleSimulationEndInteraction() {
        this.simulationEndIsDetected = true;
    }

    private byte[] generateTag() {
        return this.federateId.getBytes();
    }

}
