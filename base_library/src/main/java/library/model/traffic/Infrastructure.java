package library.model.traffic;

import library.model.limitations.Restriction;
import library.model.simulation.Position;
import library.model.simulation.SimulationProperty;
import library.model.simulation.objects.SimulationObject;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Geometry;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;

/**
 * Represents the Infrastructure and can specify which SimulationObject can use it based on its domain
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
public class Infrastructure extends SimulationObject {

    @XmlElementWrapper
    @XmlElement(name = "domain")
    private ArrayList<PossibleDomains> canBeUsedBy = new ArrayList<>();

    @XmlElement
    private ArrayList<Restriction<?>> imposedRestrictions = new ArrayList<>();

    public Infrastructure() {

    }

    public Infrastructure(boolean physical, Position position, Geometry form, double rotation) {
        super(physical, position, form, rotation);
        this.canBeUsedBy = new ArrayList<>();
    }

    public Infrastructure(SimulationProperty<Boolean> physical,
                          SimulationProperty<Position> position,
                          SimulationProperty<Geometry> form,
                          SimulationProperty<Double> rotation) {
        super(physical, position, form, rotation);
        this.canBeUsedBy = new ArrayList<>();
    }

    public Infrastructure(boolean physical,
                          Position position,
                          Geometry form,
                          double rotation,
                          ArrayList<PossibleDomains> possibleDomains) {
        super(physical, position, form, rotation);
        this.canBeUsedBy = new ArrayList<>();
        this.canBeUsedBy.addAll(possibleDomains);
    }

    public Infrastructure(SimulationProperty<Boolean> physical,
                          SimulationProperty<Position> position,
                          SimulationProperty<Geometry> form,
                          SimulationProperty<Double> rotation,
                          ArrayList<PossibleDomains> possibleDomains) {
        super(physical, position, form, rotation);
        this.canBeUsedBy = possibleDomains;
    }

    public Infrastructure addDomain(PossibleDomains assignedDomain) {
        this.canBeUsedBy.add(assignedDomain);
        return this;
    }

    public boolean isUsableBy(PossibleDomains domain) {
        return canBeUsedBy.contains(domain);
    }

    public Infrastructure addRestriction(Restriction<?> restriction) {
        imposedRestrictions.add(restriction);
        return this;
    }

}
