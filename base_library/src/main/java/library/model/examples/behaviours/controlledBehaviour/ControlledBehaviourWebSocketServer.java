package library.model.examples.behaviours.controlledBehaviour;

import library.model.simulation.Behaviour;
import library.model.examples.behaviours.ControlledBehaviour;
import library.services.logging.LoggingService;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControlledBehaviourWebSocketServer extends WebSocketServer implements IControlledBehaviourServer {

    private Behaviour behaviour;
    private String hostAdress;
    private int port;
    private static ArrayList<String> messages = new ArrayList<>();
    private ArrayList<WebSocket> sockets = new ArrayList<>();
    private final static Logger LOGGER = Logger.getLogger(ControlledBehaviour.class.getName());

    public ControlledBehaviourWebSocketServer(Behaviour behaviour, String hostAddress, int port){
        super(new InetSocketAddress(hostAddress, port));
        this.behaviour = behaviour;
        this.hostAdress = hostAddress;
        this.port = port;
    }

    @Override
    public ArrayList<String> readDataAsArrayList() throws IOException {
        ArrayList<String> messagez = (ArrayList<String>) messages.clone();
        messages = new ArrayList<>();
        return messagez;
    }

    @Override
    public void send(String message) {
        for(WebSocket socket : sockets){
            socket.send(message);
        }
    }

    @Override
    public void closeServer() {
        for(WebSocket socket: sockets){
            super.removeConnection(socket);
        }
        try {
            super.stop();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        this.sockets.add(webSocket);
        LoggingService.logWithAll("WebSocket Connection established with: " + webSocket.getRemoteSocketAddress().getAddress().toString());
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        sockets.remove(webSocket);
        LoggingService.logWithAll("Websocket Disconnected from:" + webSocket.getRemoteSocketAddress().getAddress().toString());
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        messages.add(s);
        LoggingService.logWithAll("WebSocket message received: " + s);
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {

    }

    @Override
    public void onStart() {
        try {
            System.out.println("Websocket Server started on address: " + Inet4Address.getLocalHost().getHostAddress()+ ":" +port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        LOGGER.log(Level.INFO,"Websocket Server started on address: " +hostAdress+":"+port);
    }
}
