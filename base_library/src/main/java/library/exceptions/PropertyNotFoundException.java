package library.exceptions;

/**
 * Exception if the given SimulationProperty is not part of the target SimulationObject
 */
public class PropertyNotFoundException extends Throwable {

    public PropertyNotFoundException() {
        super("The given SimulationProperty is not part of the target SimulationObject.");
    }
}
