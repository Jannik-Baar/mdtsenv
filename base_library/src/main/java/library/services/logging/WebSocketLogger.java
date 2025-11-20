package library.services.logging;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * A logger logging the data to a WebsocketServer
 */
public class WebSocketLogger implements ILogger {

    private final String address;
    private final int port;
    private WebSocketClient webSocketClient;

    public WebSocketLogger(String address, int port) {
        this.address = address;
        this.port = port;
        try {
            connect(address, port);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void connect(String address, int port) throws URISyntaxException {
        this.webSocketClient = new WebSocketClient(new URI("ws://" + address + ":" + port)) {
            @Override
            public void onMessage(String message) {

            }

            @Override
            public void onOpen(ServerHandshake handshake) {
                LoggingService.logWithAll("WebSocket-Connection with:" + address + ":" + port);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
            }

            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
            }

        };
        webSocketClient.connect();
    }

    @Override
    public void logData(String data) throws IOException {
        if (webSocketClient.isOpen()) {
            webSocketClient.send(data);
        } else {
            try {
                connect(address, port);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void close() {

    }
}
