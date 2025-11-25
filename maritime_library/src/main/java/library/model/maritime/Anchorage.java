package library.model.maritime;

import library.model.simulation.Position;
import library.model.simulation.SimulationProperty;
import library.model.simulation.units.NoUnit;
import library.model.traffic.Infrastructure;
import library.model.traffic.PossibleDomains;
import org.locationtech.jts.geom.Geometry;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

/**
 * Represents an Anchorage (Ankerplatz) in maritime navigation.
 * Requirement F-2.7: Das Simulationsmodell muss die Abbildung von Ankerplätzen ermöglichen.
 * <p>
 * An Anchorage is a designated area where vessels can anchor safely. It is typically selected
 * based on several factors:
 * - Adequate water depth
 * - Protection from wind and waves
 * - Good holding ground (seabed type)
 * - Safe distance from navigational channels
 * - Proximity to port facilities
 * - Compliance with local regulations
 * </p>
 */
@XmlRootElement
public class Anchorage extends Infrastructure {

    @XmlElement
    private SimulationProperty<Integer> maxCapacity;

    @XmlElement
    private SimulationProperty<Integer> usedCapacity;

    /**
     * Default constructor for JAXB.
     */
    protected Anchorage() {

    }

    /**
     * Creates a new Anchorage (Ankerplatz) with basic parameters.
     *
     * @param position The reference position (typically the center of the anchorage).
     * @param form The geometric boundary of the anchorage (typically a Polygon).
     * @param rotation The rotation of the area (if applicable).
     * @param physical Whether the area has physical boundaries (e.g., buoys marking the area).
     * @param maxCapacity Maximum number of vessels that can anchor simultaneously.
     * @param usedCapacity Current number of vessels anchored.
     */
    public Anchorage(Position position,
                     Geometry form,
                     double rotation,
                     boolean physical,
                     int maxCapacity,
                     int usedCapacity) {
        super(physical, position, form, rotation);
        this.maxCapacity = new SimulationProperty<>(false, false, NoUnit.get(), maxCapacity, "maxCapacity");
        this.usedCapacity = new SimulationProperty<>(false, false, NoUnit.get(), usedCapacity, "usedCapacity");
    }

    /**
     * Creates a new Anchorage with SimulationProperty parameters.
     *
     * @param physical Whether the area has physical boundaries.
     * @param position The reference position.
     * @param form The geometric boundary of the anchorage.
     * @param rotation The rotation of the area.
     * @param maxCapacity Maximum vessel capacity.
     * @param usedCapacity Current vessel count.
     */
    public Anchorage(SimulationProperty<Boolean> physical,
                     SimulationProperty<Position> position,
                     SimulationProperty<Geometry> form,
                     SimulationProperty<Double> rotation,
                     SimulationProperty<Integer> maxCapacity,
                     SimulationProperty<Integer> usedCapacity) {
        super(physical, position, form, rotation);
        this.maxCapacity = maxCapacity;
        this.usedCapacity = usedCapacity;
    }

    /**
     * Creates a new Anchorage with specified domains.
     *
     * @param physical Whether the area has physical boundaries.
     * @param position The reference position.
     * @param form The geometric boundary of the anchorage.
     * @param rotation The rotation of the area.
     * @param possibleDomains The domains to which this anchorage applies.
     * @param maxCapacity Maximum vessel capacity.
     * @param usedCapacity Current vessel count.
     */
    public Anchorage(boolean physical,
                     Position position,
                     Geometry form,
                     double rotation,
                     ArrayList<PossibleDomains> possibleDomains,
                     int maxCapacity,
                     int usedCapacity) {
        super(physical, position, form, rotation, possibleDomains);
        this.maxCapacity = new SimulationProperty<>(false, false, NoUnit.get(), maxCapacity, "maxCapacity");
        this.usedCapacity = new SimulationProperty<>(false, false, NoUnit.get(), usedCapacity, "usedCapacity");
    }

    /**
     * Creates a new Anchorage with SimulationProperty parameters and specified domains.
     *
     * @param physical Whether the area has physical boundaries.
     * @param position The reference position.
     * @param form The geometric boundary.
     * @param rotation The rotation of the area.
     * @param possibleDomains The domains to which this anchorage applies.
     * @param maxCapacity Maximum vessel capacity.
     * @param usedCapacity Current vessel count.
     */
    public Anchorage(SimulationProperty<Boolean> physical,
                     SimulationProperty<Position> position,
                     SimulationProperty<Geometry> form,
                     SimulationProperty<Double> rotation,
                     ArrayList<PossibleDomains> possibleDomains,
                     SimulationProperty<Integer> maxCapacity,
                     SimulationProperty<Integer> usedCapacity) {
        super(physical, position, form, rotation, possibleDomains);
        this.maxCapacity = maxCapacity;
        this.usedCapacity = usedCapacity;
    }

    public SimulationProperty<Integer> getMaxCapacity() {
        return maxCapacity;
    }

    public SimulationProperty<Integer> getUsedCapacity() {
        return usedCapacity;
    }
}
