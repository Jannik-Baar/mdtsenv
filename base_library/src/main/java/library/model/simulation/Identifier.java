package library.model.simulation;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Identifier {

    private String clazz;
    private String method;

    public Identifier(String clazz, String method) {
        this.clazz = clazz;
        this.method = method;
    }

    public Identifier() {
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public String toString() {
        return clazz + "::" + method;
    }
}
