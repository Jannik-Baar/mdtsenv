//package simulation.federate.termination;
//
//import hla.rti1516e.AttributeHandle;
//import hla.rti1516e.AttributeHandleValueMap;
//import hla.rti1516e.FederateAmbassador;
//import hla.rti1516e.FederateHandleSet;
//import hla.rti1516e.InteractionClassHandle;
//import hla.rti1516e.LogicalTime;
//import hla.rti1516e.NullFederateAmbassador;
//import hla.rti1516e.ObjectClassHandle;
//import hla.rti1516e.ObjectInstanceHandle;
//import hla.rti1516e.OrderType;
//import hla.rti1516e.ParameterHandle;
//import hla.rti1516e.ParameterHandleValueMap;
//import hla.rti1516e.SynchronizationPointFailureReason;
//import hla.rti1516e.TransportationTypeHandle;
//import hla.rti1516e.exceptions.FederateInternalError;
//import hla.rti1516e.time.HLAfloat64Time;
//import library.services.logging.LoggingService;
//
///**
// * This class is the Ambassador for a TerminationFederateFederate. It will receive RTI Callbacks, and handle them.
// */
//public class TerminationFederateAmbassador extends NullFederateAmbassador {
//    //----------------------------------------------------------
//    //                   INSTANCE VARIABLES
//    //----------------------------------------------------------
//    private TerminationFederate federate;
//
//    // these variables are accessible in the package
//    protected double federateTime = 0.0;
//    protected double federateLookahead = 1.0;
//
//    protected boolean isRegulating = false;
//    protected boolean isConstrained = false;
//    protected boolean isAdvancing = false;
//
//    protected boolean isAnnounced = false;
//    protected boolean isReadyToRun = false;
//
//    //----------------------------------------------------------
//    //                      CONSTRUCTORS
//    //----------------------------------------------------------
//
//    public TerminationFederateAmbassador(TerminationFederate federate) {this.federate = federate;}
//
//    //----------------------------------------------------------
//    //                    INSTANCE METHODS
//    //----------------------------------------------------------
//    private void log(String message) {
//        LoggingService.logWithAll("TerminationFederateAmbassador : " + message);
//    }
//
//    //////////////////////////////////////////////////////////////////////////
//    ////////////////////////// RTI Callback Methods //////////////////////////
//    //////////////////////////////////////////////////////////////////////////
//    @Override
//    public void synchronizationPointRegistrationFailed(String label,
//                                                       SynchronizationPointFailureReason reason) {
//        log("Failed to register sync point: " + label + ", reason=" + reason);
//    }
//
//    @Override
//    public void synchronizationPointRegistrationSucceeded(String label) {
//        log("Successfully registered sync point: " + label);
//    }
//
//    @Override
//    public void announceSynchronizationPoint(String label, byte[] tag) {
//        log("Synchronization point announced: " + label);
//        if (label.equals(federate.READY_TO_RUN))
//            this.isAnnounced = true;
//    }
//
//    @Override
//    public void federationSynchronized(String label, FederateHandleSet failed) {
//        log("Federation Synchronized: " + label);
//        if (label.equals(federate.READY_TO_RUN))
//            this.isReadyToRun = true;
//    }
//
//    /**
//     * The RTI has informed us that time regulation is now enabled.
//     */
//    @Override
//    public void timeRegulationEnabled(LogicalTime time) {
//        this.federateTime = ((HLAfloat64Time) time).getValue();
//        this.isRegulating = true;
//    }
//
//    @Override
//    public void timeConstrainedEnabled(LogicalTime time) {
//        this.federateTime = ((HLAfloat64Time) time).getValue();
//        this.isConstrained = true;
//    }
//
//    @Override
//    public void timeAdvanceGrant(LogicalTime time) {
//        this.federateTime = ((HLAfloat64Time) time).getValue();
//        this.isAdvancing = false;
//    }
//
//    @Override
//    public void discoverObjectInstance(ObjectInstanceHandle theObject,
//                                       ObjectClassHandle theObjectClass,
//                                       String objectName)
//            throws FederateInternalError {
//        log("Discoverd Object: handle=" + theObject + ", classHandle=" +
//                theObjectClass + ", name=" + objectName);
//        this.federate.addObjectInstanceHandle(theObject, objectName);
//    }
//
//    @Override
//    public void reflectAttributeValues(ObjectInstanceHandle theObject,
//                                       AttributeHandleValueMap theAttributes,
//                                       byte[] tag,
//                                       OrderType sentOrder,
//                                       TransportationTypeHandle transport,
//                                       FederateAmbassador.SupplementalReflectInfo reflectInfo)
//            throws FederateInternalError {
//        // just pass it on to the other method for printing purposes
//        // passing null as the time will let the other method know it
//        // it from us, not from the RTI
//        reflectAttributeValues(theObject,
//                theAttributes,
//                tag,
//                sentOrder,
//                transport,
//                null,
//                sentOrder,
//                reflectInfo);
//    }
//
//    @Override
//    public void reflectAttributeValues(ObjectInstanceHandle theObject,
//                                       AttributeHandleValueMap theAttributes,
//                                       byte[] tag,
//                                       OrderType sentOrdering,
//                                       TransportationTypeHandle theTransport,
//                                       LogicalTime time,
//                                       OrderType receivedOrdering,
//                                       FederateAmbassador.SupplementalReflectInfo reflectInfo)
//            throws FederateInternalError {
//        StringBuilder builder = new StringBuilder("Reflection for object:");
//
//        // print the handle
//        builder.append(" handle=" + theObject);
//        // print the tag
//        builder.append(", tag=" + new String(tag));
//        // print the time (if we have it) we'll get null if we are just receiving
//        // a forwarded call from the other reflect callback above
//        if (time != null) {
//            builder.append(", time=" + ((HLAfloat64Time) time).getValue());
//        }
//
//        // print the attribute information
//        builder.append(", attributeCount=" + theAttributes.size());
//        builder.append("\n");
//        builder.append("\tattributeHandles=");
//        for (AttributeHandle attributeHandle : theAttributes.keySet()) {
//            // print the attibute handle
//            builder.append(attributeHandle + ", ");
//        }
//        builder.append("\n");
//        log(builder.toString());
//        //pass it to the federate who will take care of the data
//        federate.handleAttributeUpdates(theObject,theAttributes);
//    }
//
//    @Override
//    public void receiveInteraction(InteractionClassHandle interactionClass,
//                                   ParameterHandleValueMap theParameters,
//                                   byte[] tag,
//                                   OrderType sentOrdering,
//                                   TransportationTypeHandle theTransport,
//                                   FederateAmbassador.SupplementalReceiveInfo receiveInfo)
//            throws FederateInternalError {
//        // just pass it on to the other method for printing purposes
//        // passing null as the time will let the other method know it
//        // it from us, not from the RTI
//        this.receiveInteraction(interactionClass,
//                theParameters,
//                tag,
//                sentOrdering,
//                theTransport,
//                null,
//                sentOrdering,
//                receiveInfo);
//    }
//
//    @Override
//    public void receiveInteraction(InteractionClassHandle interactionClass,
//                                   ParameterHandleValueMap theParameters,
//                                   byte[] tag,
//                                   OrderType sentOrdering,
//                                   TransportationTypeHandle theTransport,
//                                   LogicalTime time,
//                                   OrderType receivedOrdering,
//                                   FederateAmbassador.SupplementalReceiveInfo receiveInfo)
//            throws FederateInternalError {
//        if(interactionClass == federate.simulationEndHandle){
//            federate.handleSimulationEnd();
//        }
//        StringBuilder builder = new StringBuilder("Interaction Received:");
//
//        // print the handle
//        builder.append(" handle=" + interactionClass);
//
//        // print the tag
//        builder.append(", tag=" + new String(tag));
//        // print the time (if we have it) we'll get null if we are just receiving
//        // a forwarded call from the other reflect callback above
//        if (time != null) {
//            builder.append(", time=" + ((HLAfloat64Time) time).getValue());
//        }
//
//        // print the parameer information
//        builder.append(", parameterCount=" + theParameters.size());
//        builder.append("\n");
//        for (ParameterHandle parameter : theParameters.keySet()) {
//            // print the parameter handle
//            builder.append("\tparamHandle=");
//            builder.append(parameter);
//            // print the parameter value
//            builder.append(", paramValue=");
//            builder.append(theParameters.get(parameter).length);
//            builder.append(" bytes");
//            builder.append("\n");
//        }
//
//        log(builder.toString());
//    }
//
//    @Override
//    public void removeObjectInstance(ObjectInstanceHandle theObject,
//                                     byte[] tag,
//                                     OrderType sentOrdering,
//                                     FederateAmbassador.SupplementalRemoveInfo removeInfo)
//            throws FederateInternalError {
//        log("Object Removed: handle=" + theObject);
//    }
//}
