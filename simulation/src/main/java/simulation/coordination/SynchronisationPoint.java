package simulation.coordination;

public enum SynchronisationPoint {

    READY_TO_RUN("ReadyToRun");

    private final String value;

    SynchronisationPoint(String value) {
        this.value = value;
    }

    public boolean equals(String value) {
        return this.value.equals(value);
    }

    public String toString() {
        return this.value;
    }
}
