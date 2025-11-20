package library.model.simulation.objects;

import library.model.simulation.Behaviour;

/**
 * Interface for everything in the simulation that has the ability of active attribute manipulation
 */
public interface IActiveDynamic extends IDynamic {

    void setBehaviour(Behaviour behaviour);

    Behaviour getBehaviour();

    double getTimeStepSize();

}
