package interpreter.fom.model;

import java.nio.file.Path;
import java.util.List;

public class FOM {

    private final Path path;
    private final String filename;
    private final List<FOMObjectClass> objectClasses;
    private final List<FOMInteraction> interactions;

    public FOM(Path path, String filename, List<FOMObjectClass> objectClasses, List<FOMInteraction> interactions) {
        this.path = path;
        this.filename = filename;
        this.objectClasses = objectClasses;
        this.interactions = interactions;
    }

    public Path getPath() {
        return path;
    }

    public String getFilename() {
        return filename;
    }

    public List<FOMObjectClass> getObjectClasses() {
        return objectClasses;
    }

    public List<FOMInteraction> getInteractions() {
        return interactions;
    }
}
