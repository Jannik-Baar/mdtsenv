package library.model.simulation.objects;

import library.model.limitations.Restriction;
import library.model.simulation.Position;
import library.model.simulation.SimulationProperty;
import library.model.simulation.SimulationSuperClass;
import library.model.simulation.units.NoUnit;
import library.model.simulation.units.RotationUnit;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;

/**
 * Base Object for all passive objects in the simulation
 */
@XmlRootElement
public abstract class SimulationObject extends SimulationSuperClass {

    @XmlElement
    private SimulationProperty<Boolean> physical;

    @XmlElement
    private SimulationProperty<Position> position;

    @XmlElement
    private SimulationProperty form;

    @XmlElement
    private SimulationProperty<Double> rotation;

    @XmlElement
    private ArrayList<Restriction> imposedConstraints;

    public SimulationObject(boolean physical,
                            Position position,
                            Geometry form,
                            double rotation) {
        super();
        this.physical = new SimulationProperty<>(false, false, NoUnit.get(), physical, "physical");
        this.position = new SimulationProperty<>(false, false, NoUnit.get(), position, "position");
        this.form = new SimulationProperty<>(false, false, NoUnit.get(), form, "form");
        this.rotation = new SimulationProperty<>(false, false, RotationUnit.DEGREE, rotation, "rotation");
    }

    public SimulationObject(SimulationProperty<Boolean> physical,
                            SimulationProperty<Position> position,
                            SimulationProperty<Geometry> form,
                            SimulationProperty<Double> rotation) {
        super();
        this.physical = physical;
        this.position = position;
        this.form = form;
        this.rotation = rotation;
    }

    protected SimulationObject() {
        super();
    }

    @XmlTransient
    public SimulationProperty<Boolean> getPhysical() {
        return physical;
    }

    @XmlTransient
    public SimulationProperty<Position> getPosition() {
        return position;
    }

    public SimulationProperty<Geometry> getForm() {
        return form;
    }

    @XmlTransient
    public SimulationProperty<Double> getRotation() {
        return rotation;
    }

    public void setFormString(String form) throws ParseException {
        WKTReader reader = new WKTReader();
        Geometry multiPolygon = reader.read(form);
        this.form = new SimulationProperty<>(true, false, NoUnit.get(), multiPolygon, "form");
    }

    public String getFormString() {
        if (this.form != null)
            return this.form.getValue().toString();
        return null;
    }

    @XmlTransient
    public void setForm(SimulationProperty form) {
        this.form = form;
    }

    public void setPhysical(SimulationProperty<Boolean> physical) {
        this.physical = physical;
    }

    public void setPosition(SimulationProperty<Position> position) {
        this.position = position;
    }

    public void setRotation(SimulationProperty<Double> rotation) {
        this.rotation = rotation;
    }

    public ArrayList<Restriction> getImposedConstraints() {
        return imposedConstraints;
    }

    @XmlTransient
    public void setImposedConstraints(ArrayList<Restriction> imposedConstraint) {
        this.imposedConstraints = imposedConstraint;
    }

}
