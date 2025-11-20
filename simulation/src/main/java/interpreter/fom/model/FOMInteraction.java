package interpreter.fom.model;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO use!!!
 * This Class reflects the data Structure of a HLAInteraction that is part of a FOM
 * Therefore it has subClasses and attributes as well as the FOMPath which identifies it within the FOM
 */
public class FOMInteraction {

    private boolean publish = false;
    private boolean subscribe = false;
    private String transport = "HLAreliable";
    private String order = "TimeStamp";
    private List<FOMDimension> dimensions = new ArrayList<>();
    private FOMInteraction superInteraction;
    private List<FOMInteraction> subInteractions = new ArrayList<>();
    private String name;
    private String dataType;
    private String semantics;

    public FOMInteraction(String name) {
        this.name = name;
        this.publish = true;
        this.subscribe = true;
    }

    public FOMInteraction(boolean publish, boolean subscribe, String transport, String order, List<FOMDimension> dimensions, String name, String dataType, String semantics) {
        this.publish = publish;
        this.subscribe = subscribe;
        this.transport = transport;
        this.order = order;
        this.dimensions = dimensions;
        this.name = name;
        this.dataType = dataType;
        this.semantics = semantics;
    }

    public boolean isPublish() {
        return publish;
    }

    public boolean isSubscribe() {
        return subscribe;
    }

    public String getTransport() {
        return transport;
    }

    public String getOrder() {
        return order;
    }

    public List<FOMDimension> getDimensions() {
        return dimensions;
    }

    public String getName() {
        return name;
    }

    public String getDataType() {
        return dataType;
    }

    public String getSemantics() {
        return semantics;
    }

    public List<FOMInteraction> getSubInteractions() {
        return subInteractions;
    }

    public FOMInteraction getSuperInteraction() {
        return superInteraction;
    }

    public void setSuperInteraction(FOMInteraction superInteraction) {
        this.superInteraction = superInteraction;
    }

    public void setSubInteractions(List<FOMInteraction> subInteractions) {
        this.subInteractions = subInteractions;
    }

    public void addSubInteraction(FOMInteraction subInteraction) {
        this.subInteractions.add(subInteraction);
    }

    public void addSubInteractions(List<FOMInteraction> subInteractions) {
        this.subInteractions.addAll(subInteractions);
    }
}
