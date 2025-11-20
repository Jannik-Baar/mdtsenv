package library.model.simulation;

import library.model.simulation.objects.SimulationObject;

import java.util.ArrayList;

public class InterObjectCommunication {

    private SimulationObject end;
    private SimulationObject start;
    private ArrayList<SimulationProperty> communicatedProperties;

    public InterObjectCommunication(SimulationObject end, SimulationObject start, ArrayList<SimulationProperty> communicatedProperties) {
        this.end = end;
        this.start = start;
        this.communicatedProperties = communicatedProperties;
    }

    public InterObjectCommunication() {
        this.communicatedProperties = new ArrayList<>();
    }

    public SimulationObject getEnd() {
        return end;
    }

    public void setEnd(SimulationObject end) {
        this.end = end;
    }

    public SimulationObject getStart() {
        return start;
    }

    public void setStart(SimulationObject start) {
        this.start = start;
    }

    public ArrayList<SimulationProperty> getCommunicatedProperties() {
        return communicatedProperties;
    }

    public void setCommunicatedProperties(ArrayList<SimulationProperty> communicatedProperties) {
        this.communicatedProperties = communicatedProperties;
    }
}
