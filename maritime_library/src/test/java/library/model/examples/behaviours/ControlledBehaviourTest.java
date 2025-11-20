//package library.model.examples.behaviours;
//
//import library.model.maritime.ContainerShip;
//import library.model.simulation.Behaviour;
//import library.model.simulation.FormDummy;
//import library.model.simulation.Position;
//import library.model.simulation.SimulationAttribute;
//import library.model.traffic.PossibleDomains;
//import library.model.traffic.SteeringSystem;
//import org.java_websocket.client.WebSocketClient;
//import org.java_websocket.handshake.ServerHandshake;
//import org.junit.jupiter.api.Assertions;
//
//import java.io.BufferedOutputStream;
//import java.io.BufferedWriter;
//import java.io.IOException;
//import java.io.OutputStreamWriter;
//import java.net.InetSocketAddress;
//import java.net.Socket;
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.TimeUnit;
//
//public class ControlledBehaviourTest {
//
//    private ControlledBehaviour controlledBehaviourTCP;
//    private ControlledBehaviour controlledBehaviourWebsocket;
//    private Socket clientSocket;
//
//    @org.junit.jupiter.api.Test
//    public void testControlledBehaviourTCPServer() throws IOException, InterruptedException {
//        CountDownLatch lock = new CountDownLatch(1);
//
//        //Create Containership
//        ContainerShip containerShip = new ContainerShip(true, new Position(8.535274, 53.555108, 0), new FormDummy(), 0.0, 1,
//                PossibleDomains.MARITIME, 112.9, 0.0, 0.0, new Position(), 180.4, 12.6, "Hamburch", "SS Hope", "Belarus",
//                "12432344654365", "ferwdsgdsv", "Black Hawk", 2.5, 5, 1.0, 1.0);
//
//        //Create SteeringSystem that can manipulate the speed and the rotation
//        SteeringSystem steeringSystem = new SteeringSystem(containerShip);
//        steeringSystem.addChangeableAttributeID(containerShip.getRotation().getId());
//        steeringSystem.addChangeableAttributeID(containerShip.getSpeed().getId());
//
//        //Add steering system to containership
//        containerShip.addComponent(steeringSystem);
//
//        //Set Behaviours
//        containerShip.setBehaviour((Behaviour) new ConstantSpeedBehaviour(containerShip));
//        try {
//            this.controlledBehaviourTCP = new ControlledBehaviour(containerShip,"localhost",3002,false);
//            steeringSystem.setBehaviour(controlledBehaviourTCP);
//            this.controlledBehaviourTCP.startServer();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        clientSocket = new Socket();
//        InetSocketAddress endPoint = new InetSocketAddress("localhost", 3002);
//        clientSocket.connect(endPoint);
//
//        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new BufferedOutputStream(clientSocket.getOutputStream())));
//        out.write(containerShip.getSpeed().getId() + ":5.0\n");
//        out.flush();
//        lock.await(1000, TimeUnit.MILLISECONDS);
//
//        this.controlledBehaviourTCP.nextStep(new SimulationAttribute<>(true, true, library.model.simulation.units.TimeUnit.MINUTE, 1.0, "~"));
//        containerShip.getBehaviour().nextStep(new SimulationAttribute<>(true, true, library.model.simulation.units.TimeUnit.MINUTE, 1.0, "~"));
//
//        out.close();
//        controlledBehaviourTCP.destroyBehaviour();
//        Assertions.assertEquals(containerShip.getSpeed().getValue(),5.0);
//    }
//
//    @org.junit.jupiter.api.Test
//    public void testControlledBehaviourWebsocketServer() throws URISyntaxException, InterruptedException {
//        CountDownLatch lock = new CountDownLatch(1);
//        ControlledBehaviour controlledBehaviourWebsocket;
//
//        //Create Containership
//        ContainerShip containerShip = new ContainerShip(true, new Position(8.535274, 53.555108, 0), new FormDummy(), 0.0, 1,
//                PossibleDomains.MARITIME, 112.9, 0.0, 0.0, new Position(), 180.4, 12.6, "Hamburch", "SS Hope", "Belarus",
//                "12432344654365", "ferwdsgdsv", "Black Hawk", 2.5, 5, 1.0, 1.0);
//
//        //Create SteeringSystem that can manipulate the speed and the rotation
//        SteeringSystem steeringSystem = new SteeringSystem(containerShip);
//        steeringSystem.addChangeableAttributeID(containerShip.getRotation().getId());
//        steeringSystem.addChangeableAttributeID(containerShip.getSpeed().getId());
//
//        //Add steering system to containership
//        containerShip.addComponent(steeringSystem);
//
//        //Set Behaviours
//        containerShip.setBehaviour((Behaviour) new ConstantSpeedBehaviour(containerShip));
//        try {
//            this.controlledBehaviourWebsocket = new ControlledBehaviour(containerShip,"localhost",1337,true);
//            steeringSystem.setBehaviour(this.controlledBehaviourWebsocket);
//            this.controlledBehaviourWebsocket.startServer();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        WebSocketClient webSocketClient = new WebSocketClient(new URI("ws://localhost:1337"))
//        {
//            @Override
//            public void onMessage( String message ) {
//
//            }
//
//            @Override
//            public void onOpen( ServerHandshake handshake ) {
//                System.out.println( "opened connection" );
//            }
//
//            @Override
//            public void onClose( int code, String reason, boolean remote ) {
//                System.out.println( "closed connection" );
//            }
//
//            @Override
//            public void onError( Exception ex ) {
//                ex.printStackTrace();
//            }
//
//        };
//        webSocketClient.connect();
//
//        lock.await(1000, TimeUnit.MILLISECONDS);
//
//        webSocketClient.send(containerShip.getSpeed().getId() + ":" + "5.0");
//        lock.await(1000, TimeUnit.MILLISECONDS);
//        this.controlledBehaviourWebsocket.nextStep(new SimulationAttribute<>(true, true, library.model.simulation.units.TimeUnit.MINUTE, 1.0, "~"));
//        containerShip.getBehaviour().nextStep(new SimulationAttribute<>(true, true, library.model.simulation.units.TimeUnit.MINUTE, 1.0, "~"));
//        lock.await(1000, TimeUnit.MILLISECONDS);
//
//        Assertions.assertEquals(containerShip.getSpeed().getValue(),5.0);
//
//        try {
//            this.controlledBehaviourWebsocket.destroyBehaviour();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
