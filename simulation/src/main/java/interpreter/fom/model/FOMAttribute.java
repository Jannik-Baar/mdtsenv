package interpreter.fom.model;

import library.model.simulation.SimulationProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * This Class reflects the data Structure of a HLAAttribute that is part of a FOM
 * Therefore it has subClasses and attributes as well as the FOMPath which identifies it within the FOM
 */
public class FOMAttribute {

    private boolean publish = false;
    private String updateType = "Unconditional";
    private String ownerShip = "NoTransfer";
    private String name;
    private String dataType;

    List<SimulationProperty> instances = new ArrayList<>();
    // DO IT LIKE THAT

    public FOMAttribute() {
        // empty constructor
    }

    public FOMAttribute(boolean publish, String updateType, String ownerShip, String name, String dataType) {
        this.publish = publish;
        this.updateType = updateType;
        this.ownerShip = ownerShip;
        this.name = name;
        this.dataType = dataType;
    }

    public String getSharingString() {
        String sharing = "";
        if (this.publish) {
            sharing = sharing + "Publish";
        }
        if (sharing.equals("")) {
            sharing = "Neither";
        }
        return sharing;
    }

    public boolean isPublish() {
        return publish;
    }

    public String getUpdateType() {
        return updateType;
    }

    public String getOwnerShip() {
        return ownerShip;
    }

    public String getName() {
        return name;
    }

    public String getDataType() {
        return dataType;
    }

    public void setPublish(boolean publish) {
        this.publish = publish;
    }

    public void setUpdateType(String updateType) {
        this.updateType = updateType;
    }

    public void setOwnerShip(String ownerShip) {
        this.ownerShip = ownerShip;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
}
