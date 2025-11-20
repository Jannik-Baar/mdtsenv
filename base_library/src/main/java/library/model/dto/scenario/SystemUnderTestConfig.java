package library.model.dto.scenario;

/**
 * Wrapper of the data for the system under test
 */
public class SystemUnderTestConfig {

    private int port = 3002;
    private String address = "localhost";
    private boolean isWebsocket = true;

    public SystemUnderTestConfig() {
    }

    public SystemUnderTestConfig(int port, String address, boolean isWebsocket) {
        this.port = port;
        this.address = address;
        this.isWebsocket = isWebsocket;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setWebsocket(boolean websocket) {
        isWebsocket = websocket;
    }

    public int getPort() {
        return port;
    }

    public String getAddress() {
        return address;
    }

    public boolean isWebsocket() {
        return isWebsocket;
    }
}
