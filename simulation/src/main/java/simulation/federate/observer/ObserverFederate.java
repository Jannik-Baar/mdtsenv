package simulation.federate.observer;

import hla.rti1516e.CallbackModel;
import hla.rti1516e.ResignAction;
import hla.rti1516e.exceptions.FederatesCurrentlyJoined;
import hla.rti1516e.exceptions.FederationExecutionDoesNotExist;
import hla.rti1516e.exceptions.RTIexception;
import hla.rti1516e.time.HLAfloat64TimeFactory;
import interpreter.utils.ReflectionUtils;
import library.model.dto.observer.Observer;
import library.model.simulation.objects.SimulationObject;
import library.model.simulation.SimulationProperty;
import library.services.logging.ConsoleLogger;
import library.services.logging.ExampleLogger;
import library.services.logging.FileLogger;
import library.services.logging.ILogger;
import library.services.logging.LoggingService;
import library.services.logging.LoggingType;
import library.services.logging.UDPLogger;
import library.services.logging.WebSocketLogger;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.portico.impl.hla1516e.Rti1516eFactory;
import simulation.federate.AbstractFederate;
import simulation.federate.SimulationFederateAmbassador;
import simulation.federate.handler.IncomingDataHandler;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static library.services.logging.LoggingService.log;

/**
 * A Federate that is responsible for logging Oberserver Data of observed SimulationAttributes
 */
public class ObserverFederate extends AbstractFederate {

    //----------------------------------------------------------
    //                    STATIC VARIABLES
    //----------------------------------------------------------

    // TODO remove - i think this isn't needed anymore after the HLA usage refactoring was done
    ///**
    // * The number of times we will update our attributes and send an interaction
    // */
    //private int ITERATIONS;

    public final double timeStepSize;

    //----------------------------------------------------------
    //                   INSTANCE VARIABLES
    //----------------------------------------------------------

    // SIMULATION CONTENTS
    private final Observer observer;

    // STATUS
    private boolean atSyncPoint = false;
    private boolean simulationEndDetected = false;

    // CONFIGURATION
    private final String address;
    private final int port;
    private static HashMap<LoggingType, ILogger> loggers = new HashMap<>();

    //----------------------------------------------------------
    //                      CONSTRUCTORS
    //----------------------------------------------------------
    public ObserverFederate(Observer observer,
                            String identifier) {

        super(observer.getTimeStepSize());

        LoggingService.registerLogger(LoggingType.CONSOLE);

        this.observer = observer;
        this.address = observer.getObserverWebSocketConfig().getAddress();
        this.port = observer.getObserverWebSocketConfig().getPort();
        this.timeStepSize = observer.getTimeStepSize();
        this.federateName = identifier;
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
        this.encoderFactory = new Rti1516eFactory().getEncoderFactory();

        // connect to the RTI with our ambassador who will receive the RTI callbacks
        log("Connecting...");

        //create the IncomingDataHandler which will handle our incoming data traffic
        //tell the federateAmbassador about the handler, so it routes incoming messages into it
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
        URL[] joinModules = new URL[0];
        rtiAmb.joinFederationExecution("Oberserver(" + this.federateName + ")",
                                       "ObserverFederate",
                                       "ExampleFederation", // TODO use a proper name
                                       joinModules);

        log("Joined Federation as " + federateName);

        // cache the time factory for easy access
        this.timeFactory = (HLAfloat64TimeFactory) rtiAmb.getTimeFactory();

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
        // in this section we enable/disable all time policies
        // note that this step is optional!
        enableTimePolicy();
        log("Time Policy Enabled");

        //////////////////////////////
        // 8. publish and subscribe //
        //////////////////////////////
        // in this section we tell the RTI of all the data we are going to
        // produce, and all the data we want to know about
        subscribe();
        log("Subscribed");

        /////////////////////////////////////
        // 9. register an object to update //
        /////////////////////////////////////
        // nothing gets updated by the observer federate since it just... observes

        /////////////////////////////////////
        // 10. do the main simulation loop //
        /////////////////////////////////////
        // here is where we do the meat of our work. in each iteration, we will
        // update the attribute values of the object we registered, and will
        // send an interaction.
        while (!this.simulationEndDetected) {
            //log all the observed values in JSN Format
            logAttributesAsJSON();

            // request a time advance and wait until we get it
            advanceTime(this.timeStepSize);
            log("Time Advanced to " + fedAmb.getFederateTime());
        }

        //////////////////////////////////////
        // 11. delete the object we created //
        //////////////////////////////////////
        // nothing to delete here since we did not create objects in the first place

        ////////////////////////////////////
        // 12. resign from the federation //
        ////////////////////////////////////
        rtiAmb.resignFederationExecution(ResignAction.DELETE_OBJECTS);
        log("Resigned from Federation");

        ////////////////////////////////////////
        // 13. try and destroy the federation //
        ////////////////////////////////////////
        // NOTE: we won't die if we can't do this because other federates remain.
        //       in that case we'll leave it for them to clean up
        try {
            rtiAmb.destroyFederationExecution("ExampleFederation");
            log("Destroyed Federation");
        } catch (FederationExecutionDoesNotExist dne) {
            log("No need to destroy federation, it doesn't exist");
        } catch (FederatesCurrentlyJoined fcj) {
            log("Didn't destroy federation, federates still joined");
        }

        // disconnect
        try {
            rtiAmb.disconnect();
            log("Disconnected");
        } catch (Exception e) {
            log("Exception while disconnecting");
            e.printStackTrace();
        }
    }

    /**
     * Logs all the Attributes in JSON Format with all the registered LoggingTypes
     */
    private void logAttributesAsJSON() {
        for (SimulationObject simulationObject : objectInstanceHandleBiMap.keySet()) {
            StringBuilder jsonString = new StringBuilder();
            jsonString.append(
                "{\n" +
                "\"id\" : \"" + simulationObject.getId() + "\",\n" +
                "\"type\" : \"" + simulationObject.getClass().getSimpleName() + "\",\n"
            );

            List<SimulationProperty<?>> attributes = ReflectionUtils.getFieldsOfClass(simulationObject.getClass())
                                                                     .stream()
                                                                     .filter(f -> f.getType().equals(SimulationProperty.class))
                                                                     .map(f -> {
                                                                         try {
                                                                             f.setAccessible(true);
                                                                             return (SimulationProperty<?>) f.get(simulationObject);
                                                                         } catch (IllegalAccessException e) {
                                                                             e.printStackTrace();
                                                                             return null;
                                                                         }
                                                                     })
                                                                     .filter(Objects::nonNull)
                                                                     .collect(Collectors.toList());

            for (SimulationProperty<?> simulationProperty : attributes) {

                if (simulationProperty == null) {
                    continue;
                }
                String attributeJsonString = getSimulationAttributeAsJsonString(simulationProperty);
                if (attributeJsonString != null && !attributeJsonString.isBlank()) {
                    jsonString.append(attributeJsonString);
                    jsonString.append(",\n");
                }

            }
            jsonString.append("}");
            String outputJson = jsonString.toString();

            // clean up
            outputJson = outputJson.replaceAll(",}", "}");
            outputJson = outputJson.replaceAll(",\n}", "\n}");

            //Hier müsste noch geschaut werden wie Components verarbeitet werden
            // System.out.println(outputJson);
            logWithAll(outputJson);
        }
    }

    private String getSimulationAttributeAsJsonString(@NotNull SimulationProperty<?> simulationProperty) {

        String jsonString = "";

        if (attributeHandleNameClassMap.columnKeySet().contains(simulationProperty.getName())) {
            jsonString = simulationProperty.toString();

        } else if (attributeHandleNameClassMap.columnKeySet()
                                              .stream()
                                              .anyMatch(namePath -> namePath.startsWith(simulationProperty.getName()))
        ) {

            List<String> matchingNamePaths = attributeHandleNameClassMap.columnKeySet()
                                                                        .stream()
                                                                        .filter(namePath -> namePath.startsWith(simulationProperty.getName()))
                                                                        .collect(Collectors.toList());

            try {
                jsonString = getDeepSimulationAttributeAsJsonString(simulationProperty, matchingNamePaths);
            } catch (Exception e) {
                e.printStackTrace();
                // TODO properly handle exception
            }

        }

        return jsonString;
    }

    private String getDeepSimulationAttributeAsJsonString(@NotNull SimulationProperty<?> simulationProperty, List<String> innerAttributePaths) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        innerAttributePaths = innerAttributePaths.stream()
                                                 .map(p -> p.substring(p.indexOf(".") + 1))
                                                 .collect(Collectors.toList());

        StringBuilder json = new StringBuilder("\"" + simulationProperty.getName() + "\" : {\n");

        for (String innerAttributePath : innerAttributePaths) {

            Object innerAttributeValue = simulationProperty.getValue();
            if (innerAttributePath.contains(".")) {
                Method m = innerAttributeValue.getClass().getMethod("get" + StringUtils.capitalize(innerAttributePath.substring(0, innerAttributePath.indexOf("."))));
                SimulationProperty<?> innerSimulationProperty = (SimulationProperty<?>) m.invoke(innerAttributeValue);
                json.append(getDeepSimulationAttributeAsJsonString(innerSimulationProperty, new ArrayList<>(innerAttributePaths)));
            } else {
                Method m = innerAttributeValue.getClass().getMethod("get" + StringUtils.capitalize(innerAttributePath));
                SimulationProperty<?> innerSimulationProperty = (SimulationProperty<?>) m.invoke(innerAttributeValue);
                json.append("\"").append(innerSimulationProperty.getName()).append("\" : \"").append(innerSimulationProperty.getValue()).append("\",\n");
            }
        }

        json.append("}");

        return json.toString();
    }

    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////// Helper Methods //////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    private void subscribe() throws RTIexception {
        if (observer == null) {
            throw new NullPointerException("subscribe can not be called if no observer is set");
        }
        generateDataSubscriptionHandles(observer.getObservedClasses());
    }

    @Override
    public boolean isAtSyncPoint() {
        return atSyncPoint;
    }

    @Override
    public void run() {
        try {
            execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Logs the given data with every Logger available
     *
     * @param data
     * @throws IOException
     */
    public static void logWithAll(String data) {
        for (ILogger logger : loggers.values()) {
            try {
                logger.logData(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Logs a string in a specific logger
     *
     * @param loggingType
     * @param data
     */
    public static void logAttributeToObserve(LoggingType loggingType, String data) {
        try {
            if (loggers.containsKey(loggingType)) {
                loggers.get(loggingType).logData(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Registers a logger based on the given enum parameter
     *
     * @param loggingType
     */
    public void registerLogger(LoggingType loggingType) {
        switch (loggingType) {
            case UDP:
                try {
                    loggers.put(loggingType, new UDPLogger());
                } catch (UnknownHostException | SocketException e) {
                    e.printStackTrace();
                }
                return;
            case EXAMPLE:
                loggers.put(loggingType, new ExampleLogger());
                return;
            case FILE:
                loggers.put(loggingType, new FileLogger());
                return;
            case CONSOLE:
                loggers.put(loggingType, new ConsoleLogger());
                return;
            case WEBSOCKET:
                loggers.put(loggingType, new WebSocketLogger(this.address, this.port));
            default:
                return;

        }
    }

    /**
     * closes and removes all loggers
     */
    public void removeAllLoggers() {
        for (ILogger logger : loggers.values()) {
            logger.close();
        }
        loggers = new HashMap<>();
    }

    /**
     * Removes a logger based on its loggingMethod
     *
     * @param loggingType
     */
    public void removeLogger(LoggingType loggingType) {
        loggers.get(loggingType).close();
        loggers.remove(loggingType);
    }

    public static Collection<ILogger> getLoggers() {
        return loggers.values();
    }

    public Observer getObserver() {
        return this.observer;
    }

}
