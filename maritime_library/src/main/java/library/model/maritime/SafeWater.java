package library.model.maritime;

import library.model.simulation.Position;
import library.model.simulation.SimulationProperty;
import library.model.traffic.Infrastructure;
import library.model.traffic.PossibleDomains;
import org.locationtech.jts.geom.Geometry;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

@XmlRootElement
public class SafeWater extends Infrastructure {
    
    @XmlElementWrapper(name = "beacons")
    @XmlElement(name = "beacon")
    private ArrayList<Beacon> beacons = new ArrayList<>();
    
    @XmlElementWrapper(name = "lateralMarks")
    @XmlElement(name = "lateralMark")
    private ArrayList<LateralMark> lateralMarks = new ArrayList<>();
    
    @XmlElementWrapper(name = "lighthouses")
    @XmlElement(name = "lighthouse")
    private ArrayList<Lighthouse> lighthouses = new ArrayList<>();
    
    @XmlElementWrapper(name = "safeWaterMarks")
    @XmlElement(name = "safeWaterMark")
    private ArrayList<SafeWaterMark> safeWaterMarks = new ArrayList<>();
    
    public SafeWater() {
    }

    public SafeWater(boolean physical, Position position, Geometry form, double rotation) {
        super(physical, position, form, rotation);
        this.beacons = new ArrayList<>();
        this.lateralMarks = new ArrayList<>();
        this.lighthouses = new ArrayList<>();
        this.safeWaterMarks = new ArrayList<>();
    }

    public SafeWater(SimulationProperty<Boolean> physical, SimulationProperty<Position> position, SimulationProperty<Geometry> form, SimulationProperty<Double> rotation) {
        super(physical, position, form, rotation);
        this.beacons = new ArrayList<>();
        this.lateralMarks = new ArrayList<>();
        this.lighthouses = new ArrayList<>();
        this.safeWaterMarks = new ArrayList<>();
    }

    public SafeWater(boolean physical, Position position, Geometry form, double rotation, ArrayList<PossibleDomains> possibleDomains) {
        super(physical, position, form, rotation, possibleDomains);
        this.beacons = new ArrayList<>();
        this.lateralMarks = new ArrayList<>();
        this.lighthouses = new ArrayList<>();
        this.safeWaterMarks = new ArrayList<>();
    }

    public SafeWater(SimulationProperty<Boolean> physical, SimulationProperty<Position> position, SimulationProperty<Geometry> form, SimulationProperty<Double> rotation, ArrayList<PossibleDomains> possibleDomains) {
        super(physical, position, form, rotation, possibleDomains);
        this.beacons = new ArrayList<>();
        this.lateralMarks = new ArrayList<>();
        this.lighthouses = new ArrayList<>();
        this.safeWaterMarks = new ArrayList<>();
    }
    
    // Getters and Setters
    
    public ArrayList<Beacon> getBeacons() {
        return beacons;
    }
    
    public void setBeacons(ArrayList<Beacon> beacons) {
        this.beacons = beacons;
    }
    
    public SafeWater addBeacon(Beacon beacon) {
        this.beacons.add(beacon);
        return this;
    }
    
    public ArrayList<LateralMark> getLateralMarks() {
        return lateralMarks;
    }
    
    public void setLateralMarks(ArrayList<LateralMark> lateralMarks) {
        this.lateralMarks = lateralMarks;
    }
    
    public SafeWater addLateralMark(LateralMark lateralMark) {
        this.lateralMarks.add(lateralMark);
        return this;
    }
    
    public ArrayList<Lighthouse> getLighthouses() {
        return lighthouses;
    }
    
    public void setLighthouses(ArrayList<Lighthouse> lighthouses) {
        this.lighthouses = lighthouses;
    }
    
    public SafeWater addLighthouse(Lighthouse lighthouse) {
        this.lighthouses.add(lighthouse);
        return this;
    }
    
    public ArrayList<SafeWaterMark> getSafeWaterMarks() {
        return safeWaterMarks;
    }
    
    public void setSafeWaterMarks(ArrayList<SafeWaterMark> safeWaterMarks) {
        this.safeWaterMarks = safeWaterMarks;
    }
    
    public SafeWater addSafeWaterMark(SafeWaterMark safeWaterMark) {
        this.safeWaterMarks.add(safeWaterMark);
        return this;
    }
}
