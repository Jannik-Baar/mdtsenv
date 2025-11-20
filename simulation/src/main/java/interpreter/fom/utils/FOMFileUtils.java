package interpreter.fom.utils;

import library.model.simulation.objects.SimulationObject;
import util.FileSystemUtils;

import javax.xml.XMLConstants;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public final class FOMFileUtils {

    //Path name of the FOM directory
    private final static String PATHNAME = "FOMS\\";

    private static List<Class<?>> classes = null;

    private FOMFileUtils() {}

    private static HashMap<Object, String> objectStringHashMap;

    /**
     * This Method saves a generated FOM as an XML File
     *
     * @param filename      identifier for the file name
     * @param content String that holds the FOM XML
     */
    public static Path saveFomAsXMLFile(String filename, String content) {

        try {
            File classes = new File(PATHNAME + filename);
            classes.createNewFile();

            content = content.replaceAll("\n", "");
            Source xmlInput = new StreamSource(new StringReader(content));
            StringWriter stringWriter = new StringWriter();
            StreamResult xmlOutput = new StreamResult(stringWriter);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", 4);
            transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(xmlInput, xmlOutput);
            content = xmlOutput.getWriter().toString();
            content = content.replaceAll("\r\n {2,}\r\n", "\n");

            Path path = Files.write(classes.toPath(), content.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);

            if (path.toFile().isFile()) {
                return path;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * checks if the FOM for the given identifier exists
     *
     * @param filename filename of the FOM to search for
     * @return true if FOM exists false if not
     * @throws IOException if error occurs while handling files
     */
    public static boolean checkIfFomFileExists(String filename) throws IOException {
        try {
            FileSystemUtils.createDirectory(PATHNAME);
            //Search for fom in the specified directory
            File f = new File(PATHNAME + filename);
            return f.exists() && !f.isDirectory();
        } catch (IOException exception) {
            exception.printStackTrace();
            throw exception;
        }
    }

    public static Path getPathFromFileName(String filename) {
        return new File(PATHNAME + filename).toPath();
    }

    /**
     * checks if the FOM for the given simulationObject exists
     *
     * @param simObject simulationObject to search for
     * @return true if FOM exists false if not
     * @throws IOException if error occurs while handling files
     */
    public static boolean checkIfFomFileExists(SimulationObject simObject) throws IOException {
        String filename = getFOMFileName(simObject);
        try {
            return FOMFileUtils.checkIfFomFileExists(filename);
        } catch (IOException exception) {
            exception.printStackTrace();
            throw exception;
        }
    }

    public static ArrayList<String> getIdentifiersOfExistingFOMs(Set<SimulationObject> simulationObjects) {
        ArrayList<String> result = new ArrayList<>();
        for (SimulationObject so : simulationObjects) {
            if (so != null) {
                String filename = getFOMFileName(so);
                boolean fomFound = false;
                try {
                    if (FOMFileUtils.checkIfFomFileExists(filename)) {
                        fomFound = true;
                    }
                } catch (IOException exception) {
                    exception.printStackTrace(); // TODO use proper logging service
                }
                if (fomFound) {
                    result.add(filename);
                }
            } else {
                System.out.println("SimulationObject not found"); // TODO use proper logging service
            }
        }
        return result;
    }

    /**
     * Coordinates the generation of the identifier for the fom file.
     *
     * @param object the object for which an identifier/filename is required
     * @return name of the file to be searched / created
     */
    public static String getFOMFileName(Object object) {
        String className = object.getClass().getSimpleName();
        String objectIdentifier = ModelUtils.getClassIdentifier(object);
        String hashedObjectIdentifier = String.valueOf(objectIdentifier.hashCode());
        return className + "-" + hashedObjectIdentifier + ".xml";
    }

}
