package library.model.examples.behaviours.controlledBehaviour;

import java.io.IOException;
import java.util.ArrayList;

public interface IControlledBehaviourServer {

    ArrayList<String> readDataAsArrayList() throws IOException;

    void send(String message);
    void closeServer();


}
