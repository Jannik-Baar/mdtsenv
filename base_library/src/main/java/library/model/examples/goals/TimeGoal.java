package library.model.examples.goals;

import library.model.simulation.Goal;
import library.model.simulation.SimulationProperty;
import library.model.simulation.units.TimeUnit;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Provides target time that can be used in combination with other goals to establish a time limit for goal completion
 */
@XmlRootElement
public class TimeGoal extends Goal {

    private SimulationProperty<Double> targetTime;
    @XmlTransient
    private SimulationProperty<Double> currentTime = new SimulationProperty(TimeUnit.SECOND, 0.0, "currentSimulationTime");

    public TimeGoal(SimulationProperty<Double> targetTime) {
        this.targetTime = targetTime;
    }

    public TimeGoal() {

    }

    /**
     * Checks if the time limit has elapsed
     *
     * @return true if the currentTime is greater than the targetTime
     */
    @Override
    public Boolean check() {
        return currentTime.getValue() > targetTime.getValue();
    }

    public Boolean check(Double currentTime) {
        this.setCurrentTime(currentTime);
        return check();
    }

    public void setCurrentTime(double currentTime) {
        this.currentTime.setSingleValue(currentTime);
    }

    public SimulationProperty<Double> getTargetTime() {
        return targetTime;
    }

    public void setTargetTime(SimulationProperty<Double> targetTime) {
        this.targetTime = targetTime;
    }
}
