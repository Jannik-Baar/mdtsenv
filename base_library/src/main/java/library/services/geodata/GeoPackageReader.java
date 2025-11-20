package library.services.geodata;

import library.model.simulation.Position;
import library.model.traffic.Infrastructure;
import library.model.traffic.PossibleDomains;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides the ability to read geoPackages.
 */
public class GeoPackageReader {

    public ArrayList<Infrastructure> read(String path) throws IOException {

        int elements = 0;

        File file = new File(path);
        Map<String, Object> map = new HashMap<>();
        map.put("dbtype", "geopkg");
        map.put("database", file.getAbsolutePath());

        DataStore dataStore = DataStoreFinder.getDataStore(map);
        String typeName = dataStore.getTypeNames()[0];

        FeatureSource<SimpleFeatureType, SimpleFeature> source =  dataStore.getFeatureSource(typeName);

        FeatureCollection<SimpleFeatureType, SimpleFeature> collection = source.getFeatures();
        ArrayList<Infrastructure> output = new ArrayList<>();
        try (FeatureIterator<SimpleFeature> features = collection.features()) {
            while (features.hasNext()) {
                SimpleFeature feature = features.next();
                Geometry geom = (Geometry) feature.getAttribute("geom");
                Coordinate[] coordinates = geom.getEnvelope().getCoordinates();
                double middleX = (coordinates[2].x - coordinates[0].x) * 0.5 + coordinates[0].x;
                double middleY = (coordinates[2].y - coordinates[0].y) * 0.5 + coordinates[0].y;
                Position position = new Position(middleX, middleY, 0);
                output.add(new Infrastructure(true, position, geom, 0));
                output.get(output.size() - 1).addDomain(PossibleDomains.MARITIME);
                elements++;

            }
        }
        dataStore.dispose();

        System.out.println(elements + " elements added");
        return output;
    }
}

