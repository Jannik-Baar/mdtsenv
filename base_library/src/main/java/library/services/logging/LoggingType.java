package library.services.logging;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public enum LoggingType {
    UDP,
    EXAMPLE,
    FILE,
    CONSOLE,
    WEBSOCKET
}
