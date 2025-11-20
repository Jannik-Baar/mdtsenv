package simulation.federate;

import hla.rti1516e.*;
import hla.rti1516e.exceptions.FederateInternalError;
import hla.rti1516e.time.HLAfloat64Time;
import simulation.federate.handler.IncomingDataHandler;
import library.services.logging.LoggingService;
import library.services.logging.LoggingType;

import static library.services.logging.LoggingService.log;

/**
 * This class is the Ambassador for the Interpreted Federates. It will receive RTI Callbacks and handle them
 */
public class SimulationFederateAmbassador extends NullFederateAmbassador {

    //----------------------------------------------------------
    //                   INSTANCE VARIABLES
    //----------------------------------------------------------
    private final AbstractFederate federate;
    private final IncomingDataHandler incomingDataHandler;

    // these variables are accessible in the package
    private double federateTime = 0.0;
    private double federateLookahead = 1.0;

    private boolean regulating = false;
    private boolean constrained = false;
    private boolean advancing = false;

    private boolean announced = false;
    private boolean readyToRun = false;

    //----------------------------------------------------------
    //                      CONSTRUCTORS
    //----------------------------------------------------------

    public SimulationFederateAmbassador(AbstractFederate federate, IncomingDataHandler incomingDataHandler) {
        this.federate = federate;
        this.incomingDataHandler = incomingDataHandler;
        LoggingService.registerLogger(LoggingType.CONSOLE);
    }
    
    //////////////////////////////////////////////////////////////////////////
    ////////////////////////// RTI Callback Methods //////////////////////////
    //////////////////////////////////////////////////////////////////////////
    @Override
    public void synchronizationPointRegistrationFailed(String label,
                                                       SynchronizationPointFailureReason reason) {
        log("Failed to register sync point: " + label + ", reason=" + reason);
    }

    @Override
    public void synchronizationPointRegistrationSucceeded(String label) {
        log("Successfully registered sync point: " + label);
    }

    @Override
    public void announceSynchronizationPoint(String label, byte[] tag) {
        log("Synchronization point announced: " + label);
        if (label.equals(federate.READY_TO_RUN)) {
            this.announced = true;
        }
    }

    @Override
    public void federationSynchronized(String label, FederateHandleSet failed) {
        log("Federation Synchronized: " + label);
        if (label.equals(federate.READY_TO_RUN)) {
            this.readyToRun = true;
        }
    }

    /**
     * The RTI has informed us that time regulation is now enabled.
     */
    @Override
    public void timeRegulationEnabled(LogicalTime time) {
        this.federateTime = ((HLAfloat64Time) time).getValue();
        this.regulating = true;
    }

    @Override
    public void timeConstrainedEnabled(LogicalTime time) {
        this.federateTime = ((HLAfloat64Time) time).getValue();
        this.constrained = true;
    }

    @Override
    public void timeAdvanceGrant(LogicalTime time) {
        this.federateTime = ((HLAfloat64Time) time).getValue();
        this.advancing = false;
    }

    @Override
    public void discoverObjectInstance(ObjectInstanceHandle theObject,
                                       ObjectClassHandle theObjectClass,
                                       String objectName) throws FederateInternalError {

        log("Discovered Object: handle=" + theObject + ", classHandle=" + theObjectClass + ", name=" + objectName);

        incomingDataHandler.processDiscoveredObjectInstanceData(theObject, theObjectClass, objectName);
    }

    @Override
    public void reflectAttributeValues(ObjectInstanceHandle theObject,
                                       AttributeHandleValueMap theAttributes,
                                       byte[] tag,
                                       OrderType sentOrder,
                                       TransportationTypeHandle transport,
                                       SupplementalReflectInfo reflectInfo)
        throws FederateInternalError {
        // just pass it on to the other method for printing purposes
        // passing null as the time will let the other method know
        // this call is from us, not from the RTI
        reflectAttributeValues(theObject,
                               theAttributes,
                               tag,
                               sentOrder,
                               transport,
                               null,
                               sentOrder,
                               reflectInfo);
    }

    @Override
    public void reflectAttributeValues(ObjectInstanceHandle theObject,
                                       AttributeHandleValueMap theAttributes,
                                       byte[] tag,
                                       OrderType sentOrdering,
                                       TransportationTypeHandle theTransport,
                                       LogicalTime time,
                                       OrderType receivedOrdering,
                                       SupplementalReflectInfo reflectInfo) {

        // TODO move things like this to something like an HLA-Log-Helper
        StringBuilder builder = new StringBuilder("Reflection for object:");

        // print the handle
        builder.append(" handle=" + theObject);
        // print the tag
        builder.append(", tag=" + new String(tag));
        // print the time (if we have it) we'll get null if we are just receiving
        // a forwarded call from the other reflect callback above
        if (time != null) {
            builder.append(", time=" + ((HLAfloat64Time) time).getValue());
        }

        // print the attribute information
        builder.append(", attributeCount=" + theAttributes.size());
        builder.append(", attributeHandles=");
        for (AttributeHandle attributeHandle : theAttributes.keySet()) {
            // print the attribute handle
            builder.append(attributeHandle + ", ");
        }
        builder.delete(builder.length() - 2, builder.length());

        log(builder.toString());

        //pass it to the dataHandler who will take care of everything
        incomingDataHandler.processAttributeUpdateData(theObject, theAttributes);
    }

    @Override
    public void receiveInteraction(InteractionClassHandle interactionClass,
                                   ParameterHandleValueMap theParameters,
                                   byte[] tag,
                                   OrderType sentOrdering,
                                   TransportationTypeHandle theTransport,
                                   SupplementalReceiveInfo receiveInfo) {
        // just pass it on to the other method for printing purposes
        // passing null as the time will let the other method know
        // that this call is from us, not from the RTI
        this.receiveInteraction(interactionClass,
                                theParameters,
                                tag,
                                sentOrdering,
                                theTransport,
                                null,
                                sentOrdering,
                                receiveInfo);
    }

    @Override
    public void receiveInteraction(InteractionClassHandle interactionClass,
                                   ParameterHandleValueMap theParameters,
                                   byte[] tag,
                                   OrderType sentOrdering,
                                   TransportationTypeHandle theTransport,
                                   LogicalTime time,
                                   OrderType receivedOrdering,
                                   SupplementalReceiveInfo receiveInfo) {

        // TODO move things like this to something like an HLA-Log-Helper
        StringBuilder builder = new StringBuilder("Interaction Received:");

        // print the handle
        builder.append(" handle=").append(interactionClass);

        // print the tag
        builder.append(", tag=").append(new String(tag));
        // print the time (if we have it) we'll get null if we are just receiving
        // a forwarded call from the other reflect callback above
        if (time != null) {
            builder.append(", time=").append(((HLAfloat64Time) time).getValue());
        }

        // print the parameer information
        builder.append(", parameterCount=").append(theParameters.size());
        builder.append("\n");
        for (ParameterHandle parameter : theParameters.keySet()) {
            // print the parameter handle
            builder.append("\tparamHandle=");
            builder.append(parameter);
            // print the parameter value
            builder.append(", paramValue=");
            builder.append(theParameters.get(parameter).length);
            builder.append(" bytes");
            builder.append("\n");
        }

        log(builder.toString());

        // TODO re-implement incomingDataHandler
        // incomingDataHandler.processInteraction(interactionClass, tag, theParameters);
    }

    @Override
    public void removeObjectInstance(ObjectInstanceHandle theObject,
                                     byte[] tag,
                                     OrderType sentOrdering,
                                     SupplementalRemoveInfo removeInfo)
        throws FederateInternalError {
        log("Object Removed: handle=" + theObject);
    }

    public double getFederateTime() {
        return federateTime;
    }

    public double getFederateLookahead() {
        return federateLookahead;
    }

    public boolean isRegulating() {
        return regulating;
    }

    public boolean isConstrained() {
        return constrained;
    }

    public void setAdvancing(boolean advancing) {
        this.advancing = advancing;
    }

    public boolean isAdvancing() {
        return advancing;
    }

    public boolean isAnnounced() {
        return announced;
    }

    public boolean isReadyToRun() {
        return readyToRun;
    }
}
