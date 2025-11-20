package library.services.logging;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Enables data logging over UDP stream
 */
public class UDPLogger implements ILogger{

    private static DatagramSocket socket = null;
    private InetAddress address;

    public UDPLogger() throws UnknownHostException, SocketException {
        socket = new DatagramSocket();
        socket.setBroadcast(true);
        address = InetAddress.getByName("255.255.255.255");
    }

    /**
     * Broadcast the given data string with udp
     * @param data
     * @throws IOException
     */
    @Override
    public void logData(String data) throws IOException {
        byte[] buffer = data.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 4445);
        socket.send(packet);
    }

    /**
     * closes the socket connection
     */
    @Override
    public void close(){
        socket.close();
    }
}
