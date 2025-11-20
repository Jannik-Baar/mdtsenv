package interpreter.fom.model;

public enum SharingType {

    PUBLISH("Publish"),
    SUBSCRIBE("Subscribe"),
    NEITHER("Neither");

    private final String value;

    private SharingType(String value) {
        this.value = value;
    }

    public boolean equalsName(String otherName) {
        return value.equals(otherName);
    }

    public String toString() {
        return this.value;
    }
}
