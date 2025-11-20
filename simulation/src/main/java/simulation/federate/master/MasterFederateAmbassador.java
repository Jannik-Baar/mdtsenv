package simulation.federate.master;

import hla.rti1516e.*;
import hla.rti1516e.time.HLAfloat64Time;
import simulation.coordination.SynchronisationPoint;

/**
 * Ambassador for the MasterFederate
 * holds the Methods and Attributes needed to perform the syncPoint block
 */
public class MasterFederateAmbassador extends NullFederateAmbassador {

    private MasterFederate masterFederate;

    protected boolean syncPointAnnounced = false;
    protected boolean isReadyToRun = false;

    protected double federateTime = 0.0;
    protected double federateLookahead = 1.0;

    protected boolean isRegulating = false;
    protected boolean isConstrained = false;
    protected boolean isAdvancing = false;

    public MasterFederateAmbassador(MasterFederate masterFederate) {
        this.masterFederate = masterFederate;
    }

    @Override
    public void announceSynchronizationPoint(String label, byte[] tag) {
        log("Synchronization point announced: " + label);
        if (label.equals(SynchronisationPoint.READY_TO_RUN.toString())) {
            this.syncPointAnnounced = true;
        }
    }

    @Override
    public void federationSynchronized(String label, FederateHandleSet failed) {
        log("Federation Synchronized: " + label);
        if (label.equals(SynchronisationPoint.READY_TO_RUN.toString())) {
            this.isReadyToRun = true;
        }
    }

    @Override
    public void discoverObjectInstance(ObjectInstanceHandle theObject,
                                       ObjectClassHandle theObjectClass,
                                       String objectName) {
        if (theObjectClass.equals(this.masterFederate.mainFederateClassHandle)) {
            this.masterFederate.addMainFederate(theObject);
        }
    }

    @Override
    public void removeObjectInstance(ObjectInstanceHandle theObject,
                                     byte[] tag,
                                     OrderType sentOrdering,
                                     FederateAmbassador.SupplementalRemoveInfo removeInfo) {
        this.masterFederate.removeMainFederate(theObject);
    }

    @Override
    public void receiveInteraction(InteractionClassHandle interactionClass,
                                   ParameterHandleValueMap theParameters,
                                   byte[] tag,
                                   OrderType sentOrdering,
                                   TransportationTypeHandle theTransport,
                                   FederateAmbassador.SupplementalReceiveInfo receiveInfo) {
        // just pass it on to the other method for printing purposes
        // passing null as the time will let the other method know it
        // it from us, not from the RTI
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
                                   FederateAmbassador.SupplementalReceiveInfo receiveInfo) {
        StringBuilder builder = new StringBuilder("Interaction Received:");

        // print the handle
        builder.append(" handle=" + interactionClass);
        if (interactionClass.equals(masterFederate.simulationEndHandle)) {
            builder.append(" (SimulationEnd)");
            masterFederate.handleSimulationEndInteraction();
        }

        // print the tag
        builder.append(", tag=" + new String(tag));
        // print the time (if we have it) we'll get null if we are just receiving
        // a forwarded call from the other reflect callback above
        if (time != null) {
            builder.append(", time=" + ((HLAfloat64Time) time).getValue());
        }

        // print the parameer information
        builder.append(", parameterCount=" + theParameters.size());
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
    }

    private void log(String msg) {
        masterFederate.log(msg);
    }

    /**
     * The RTI has informed us that time regulation is now enabled.
     */
    @Override
    public void timeRegulationEnabled(LogicalTime time) {
        this.federateTime = ((HLAfloat64Time) time).getValue();
        this.isRegulating = true;
    }

    @Override
    public void timeConstrainedEnabled(LogicalTime time) {
        this.federateTime = ((HLAfloat64Time) time).getValue();
        this.isConstrained = true;
    }

    @Override
    public void timeAdvanceGrant(LogicalTime time) {
        this.federateTime = ((HLAfloat64Time) time).getValue();
        this.isAdvancing = false;
    }

    public double getFederateTime() {
        return federateTime;
    }

    public double getFederateLookahead() {
        return federateLookahead;
    }

}
