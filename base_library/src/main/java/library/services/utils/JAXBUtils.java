package library.services.utils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

public class JAXBUtils {

    private final static String PACKAGE_PATH_MODEL = "library.model";

    private static JAXBContext modelContext = null;

    public static JAXBContext getModelContext() throws JAXBException {
        if (modelContext == null) {
            modelContext = JAXBContext.newInstance(PACKAGE_PATH_MODEL);
        }
        return modelContext;
    }

}
