package library.model.examples.behaviours.controlledBehaviour;

import library.model.examples.behaviours.ControlledBehaviour;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ControlledBehaviourTCPServer implements IControlledBehaviourServer, Runnable {
    private ServerSocket serverSocket;
    private ControlledBehaviour behaviour;
    private HashMap<Socket, BufferedReader> socketMap;

    public ControlledBehaviourTCPServer(ControlledBehaviour behaviour,String address, int port) throws IOException {
        this.behaviour = behaviour;
        serverSocket = new ServerSocket(port);
        this.socketMap = new HashMap<>();
    }

    public ArrayList<String> readDataAsArrayList() throws IOException {
        ArrayList<String> messages = new ArrayList<>();
        for (Map.Entry<Socket, BufferedReader> entry : socketMap.entrySet()) {
            try {
                entry.getKey().getOutputStream().write(1337);
            }catch (Exception e){
                System.out.println("Connection to " + entry.getKey().getInetAddress() + " was interrupted.");
                entry.getKey().close();
                socketMap.remove(entry.getKey());
            }
            while (entry.getValue().ready()) {
                String msg = entry.getValue().readLine();
                System.out.println("New message received from " + entry.getKey().getInetAddress() + ": " + msg);
                messages.add(msg);
            }
        }
        return messages;
    }

    @Override
    public void send(String message) {

    }

    @Override
    public void closeServer() {
        try{
            this.serverSocket.close();
        }catch (IOException e){
            System.out.println("Failed to close the TCP Server");
        }

    }

    @Override
    public void run() {
        try {
            System.out.println("ControlledBehaviourServer started on port: " + serverSocket.getLocalPort() + ". Waiting for a Connection");
            while(!Thread.interrupted()) {
                Socket newSocket = serverSocket.accept();
                BufferedReader socketInputStream = new BufferedReader(new InputStreamReader(new BufferedInputStream(newSocket.getInputStream())));
                this.socketMap.put(newSocket, socketInputStream);
                System.out.println("New connection established with IP: " + newSocket.getInetAddress());
            }
        } catch (SocketException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
