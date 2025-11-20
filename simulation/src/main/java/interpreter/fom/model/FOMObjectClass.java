package interpreter.fom.model;

import java.util.ArrayList;
import java.util.List;

/**
 * This Class reflects the data Structure of a HLAObjectClass that is part of a FOM
 * Therefore it has subClasses and attributes as well as the FOMPath which identifies it within the FOM
 */
public class FOMObjectClass {

    private FOMObjectClass superClass;
    private List<FOMObjectClass> subClasses = new ArrayList<>();
    private List<FOMAttribute> attributes = new ArrayList<>();

    private String name;

    public FOMObjectClass(String name) {
        this.name = name;
    }

    /**
     * this method builds the String for the "sharing" property in the FOM
     *
     * @return the proper string for the "sharing" property
     */
    public String getSharingString() {
        boolean publish = false;

        for (FOMAttribute attribute : attributes) {
            if (attribute.isPublish()) {
                publish = true;
            }
            if (publish) {
                break;
            }
        }

        StringBuilder sharing = new StringBuilder();
        if (publish) {
            sharing.append("Publish");
        }

        if (!publish) {
            sharing.append("Neither");
        }

        return sharing.toString();
    }

    public void setSuperClass(FOMObjectClass superClass) {
        this.superClass = superClass;
    }

    public void addSubClass(FOMObjectClass subClass) {
        this.subClasses.add(subClass);
    }

    public void addSubClasses(List<FOMObjectClass> subClasses) {
        this.subClasses.addAll(subClasses);
    }

    public FOMObjectClass getSuperClass() {
        return superClass;
    }

    public void addAttribute(FOMAttribute attribute) {
        this.attributes.add(attribute);
    }

    public void addAttributes(List<FOMAttribute> attributes) {
        this.attributes.addAll(attributes);
    }

    public List<FOMObjectClass> getSubClasses() {
        return subClasses;
    }

    public void setSubClasses(ArrayList<FOMObjectClass> subClasses) {
        this.subClasses = subClasses;
    }

    public List<FOMAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<FOMAttribute> attributes) {
        this.attributes = attributes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
