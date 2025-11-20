package library.model.examples.behaviours;

import library.model.examples.behaviours.controlledBehaviour.ControlledBehaviourTCPServer;
import library.model.examples.behaviours.controlledBehaviour.ControlledBehaviourWebSocketServer;
import library.model.examples.behaviours.controlledBehaviour.IControlledBehaviourServer;
import library.model.simulation.Behaviour;
import library.model.simulation.IBehaviour;
import library.model.simulation.objects.SimulationObject;
import library.model.traffic.TrafficParticipant;
import library.services.logging.LoggingService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Behaviour that can be used to allow the communication between a system under test (SUT) and a traffic participant.
 */
public class ControlledBehaviour extends Behaviour implements IBehaviour {

    private SimulationObject simulationObject;
    private final static Logger LOGGER = Logger.getLogger(ControlledBehaviour.class.getName());
    boolean isWebSocket = false;
    private int port;
    private String address;
    private IControlledBehaviourServer server;
    private Thread serverThread;
    private boolean started = false;

    /**
     * Implementation of the system under test interface
     *
     * @param simulationObject entity of the simulation which data should be overwritten by a system under test
     * @param address          address of the communication interface
     * @param port             port of the communication interface
     * @param isWebSocket      defines if the communication is done via socket or websocket
     */
    public ControlledBehaviour(SimulationObject simulationObject, String address, int port, boolean isWebSocket) {
        super();
        this.isWebSocket = isWebSocket;
        this.simulationObject = simulationObject;
        this.address = address;
        this.port = port;
    }

    public ControlledBehaviour(SimulationObject simulationObject) {
        super();
        this.simulationObject = simulationObject;
    }

    /**
     * Starts the server
     *
     * @throws IOException
     */
    public void startServer() throws IOException {
        try {
            if (!isWebSocket) {
                this.server = new ControlledBehaviourTCPServer(this, address, port);
            } else {
                this.server = new ControlledBehaviourWebSocketServer(this, address, port);
            }
            serverThread = new Thread((Runnable) server);
            serverThread.start();
        } catch (IOException e) {
            LoggingService.logWithAll("ServerSocket couldn't be opened");
            e.printStackTrace();
        }
    }

    /**
     * Should be called to close the socket when the behaviour is no longer used
     *
     * @throws IOException
     */
    public void destroyBehaviour() throws IOException {
        serverThread.interrupt();
        server.closeServer();
    }

    /**
     * Reads the message from the client socket, transforms it into a primitive data type and pushes
     * a task on the traffic participant to which this behaviour belongs to.
     */
    public Map<String, Object> readData() throws IOException {

        Map<String, Object> attributeUpdates = new HashMap<>();
        List<String> messages = server.readDataAsArrayList();

        //Iterates through  the messages that should have the form: "AttributeID:Value"
        for (String message : messages) {
            message = message.trim();
            String[] splitMessage = message.split(":");
            String simulationAttributeID = splitMessage[0];
            String newValueAsString = splitMessage[1];

            //Check if the TrafficParticipant contains a SimulationAttribute with the SimulationAttributeID

            /* legacy code */
            // SimulationAttribute attributeToChange = simulationObject.getSimulationAttributeByID(simulationAttributeID);
            // if (attributeToChange == null) {
            //    LOGGER.log(Level.INFO, this.simulationObject.getId() + "  Value to Change couldn't be found.");
            //    return;
            // }

            //cast the value of the message from string to primitive
            Object newValue = Double.parseDouble(newValueAsString);

            //Push the Task
            attributeUpdates.put(simulationAttributeID, newValue);
            LOGGER.log(Level.INFO, this.simulationObject.getClass().getName() + " " + this.simulationObject.getId() + "    task pushed:    SimAtt:" + simulationAttributeID + "    newValue: " + newValue);
        }

        return attributeUpdates;
    }

    @Override
    public Map<String, Object> nextStep(double timePassed) {
        try {
            if (!started) {
                started = true;
                startServer();
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error while starting the server");
            e.printStackTrace();
        }

        Map<String, Object> attributeUpdates = null;
        try {
            attributeUpdates = readData();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error while reading incoming data");
            e.printStackTrace();
        }
        return attributeUpdates;
    }

    @Override
    public void setSimulationObject(SimulationObject trafficParticipant) {
        if (trafficParticipant instanceof TrafficParticipant) {
            this.simulationObject = trafficParticipant;
        }
    }

    public void setWebSocket(boolean webSocket) {
        isWebSocket = webSocket;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
