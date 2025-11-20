package library.model.traffic;

import library.model.simulation.objects.ActiveSimulationObject;
import library.model.simulation.Position;
import library.model.simulation.SimulationProperty;
import org.locationtech.jts.geom.Geometry;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

/**
 * Represents the Infrastructure and can specify which SimulationObject can use it based on its domain
 */
@XmlRootElement
public class ActiveInfrastructure extends ActiveSimulationObject {

    @XmlElementWrapper
    @XmlElement(name = "domain")
    private ArrayList<PossibleDomains> canBeUsedBy;

    public ActiveInfrastructure() {
        super();
        this.canBeUsedBy = new ArrayList<>();
    }

    public ActiveInfrastructure(double timeStepSize,
                                boolean physical,
                                Position position,
                                Geometry form,
                                double rotation) {
        super(timeStepSize, physical, position, form, rotation);
        this.canBeUsedBy = new ArrayList<>();
    }

    public ActiveInfrastructure(double timeStepSize,
                                SimulationProperty<Boolean> physical,
                                SimulationProperty<Position> position,
                                SimulationProperty<Geometry> form,
                                SimulationProperty<Double> rotation) {
        super(timeStepSize, physical, position, form, rotation);
        this.canBeUsedBy = new ArrayList<>();
    }

    public ActiveInfrastructure(double timeStepSize,
                                boolean physical,
                                Position position,
                                Geometry form,
                                double rotation,
                                ArrayList<PossibleDomains> possibleDomains) {
        super(timeStepSize, physical, position, form, rotation);
        this.canBeUsedBy = new ArrayList<>();
        this.canBeUsedBy.addAll(possibleDomains);
    }

    public ActiveInfrastructure(double timeStepSize,
                                SimulationProperty<Boolean> physical,
                                SimulationProperty<Position> position,
                                SimulationProperty<Geometry> form,
                                SimulationProperty<Double> rotation,
                                ArrayList<PossibleDomains> possibleDomains) {
        super(timeStepSize, physical, position, form, rotation);
        this.canBeUsedBy = possibleDomains;
    }

    public ActiveInfrastructure addDomain(PossibleDomains assignedDomain) {
        this.canBeUsedBy.add(assignedDomain);
        return this;
    }

    public boolean canBeUsedBy(PossibleDomains domain) {
        return canBeUsedBy.contains(domain);
    }

    public ArrayList<PossibleDomains> getCanBeUsedBy() {
        return canBeUsedBy;
    }
}
