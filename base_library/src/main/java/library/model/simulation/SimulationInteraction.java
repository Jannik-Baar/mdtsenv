package library.model.simulation;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;

/**
 * Provides an object for sending or receiving a HLA Interaction
 */
public class SimulationInteraction {

    private String fomInteractionPath;
    private boolean publish;
    private boolean subscribe;
    private ArrayList<Pair<SimulationInteraction, String>> receivedInteractions = new ArrayList<>();

    public SimulationInteraction() {

    }

    public SimulationInteraction(String fomPath) {
        fomInteractionPath = fomPath;
    }

    public void addReceivedInteraction(byte[] tag) {
        receivedInteractions.add(new MutablePair<>(new SimulationInteraction(this.fomInteractionPath), new String(tag)));
    }

    public String getParentInteractionClassPath() {
        return fomInteractionPath.substring(0, fomInteractionPath.lastIndexOf("."));
    }

    public String getInteractionClassName() {
        return fomInteractionPath.substring(fomInteractionPath.lastIndexOf(".") + 1);
    }

    public void setFomInteractionPath(String fomPath) {
        fomInteractionPath = fomPath;
    }

    public String getFomInteractionPath() {
        return fomInteractionPath;
    }

    public void setPublish(boolean bool) {
        publish = bool;
    }

    public boolean isPublish() {
        return publish;
    }

    public void setSubscribe(boolean bool) {
        subscribe = bool;
    }

    public boolean isSubscribe() {
        return subscribe;
    }

}
