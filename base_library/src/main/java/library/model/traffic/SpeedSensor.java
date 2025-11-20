package library.model.traffic;
//
//import library.model.simulation.Behaviour;
//import library.model.simulation.Component;
//import library.model.simulation.objects.IActiveDynamic;
//import library.model.simulation.SimulationAttribute;
//import library.model.simulation.Goal;
//import library.model.simulation.IBehaviour;
//
//import javax.xml.bind.annotation.XmlRootElement;
//import java.lang.reflect.Field;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
///**
// * A SpeedSensor Sensor Component that can display a SimulationObject speed in m/s.
// */
//@XmlRootElement
public class SpeedSensor extends Sensor {
//
//    private final Logger logger = Logger.getLogger(this.getClass().getName());
//
//    private Behaviour behaviour;
//    private ArrayList<Goal> goals;
//    private TrafficParticipant simulationObject;
//
//    public SpeedSensor() {
//
//    }
//
//    public SpeedSensor(Component superComponent) {
//        super(superComponent);
//    }
//
//    @Override
//    public void setBehaviour(Behaviour behaviour) {
//        this.behaviour = behaviour;
//    }
//
//    @Override
//    public Behaviour getBehaviour() {
//        return behaviour;
//    }
//
//    @Override
//    public HashMap<Object, Field> nextStep(SimulationAttribute<Double> timePassed) {
//        if (this.simulationObject != null) {
//            logger.log(Level.WARNING, this.simulationObject.getSpeed().getValue() + " " +
//                    this.simulationObject.getSpeed().getUnit().getAbbreviation() + " ("
//                    + (this.simulationObject.getSpeed().getValue()
//                    * this.simulationObject.getSpeed().getUnit().getUnitToBase()) + " m/s)");
//        }
//        return new HashMap<>();
//    }
//
//    @Override
//    public void setGoals(ArrayList<Goal> goals) {
//        this.goals = goals;
//    }
//
//    @Override
//    public void setSimulationObject(library.model.simulation.objects.SimulationObject simulationObject) {
//        if (simulationObject instanceof TrafficParticipant) {
//            this.simulationObject = (TrafficParticipant) simulationObject;
//        }
//    }
//
//    @Override
//    public void addGoal(Goal goal) {
//        this.goals.add(goal);
//    }
//
//    @Override
//    public ArrayList<Goal> getGoals() {
//        return goals;
//    }
}
