package library.model.dto.observer;

import javax.xml.bind.annotation.XmlElement;

public class ObserverWebSocketConfig {

    private int port = 1337;

    private String address = "localhost";

    public ObserverWebSocketConfig() {
    }

    public ObserverWebSocketConfig(String address,int port) {
        this.port = port;
        this.address = address;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @XmlElement
    public int getPort() {
        return port;
    }

    @XmlElement
    public String getAddress() {
        return address;
    }

}
