package library.services.geodata;

import library.model.simulation.Position;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

public class GeometryUtils {

    protected static Geometry createGeometryFromPosition(Position position) throws ParseException {
        WKTReader wktReader = new WKTReader();
        Geometry geometry;
        try {
            geometry = wktReader.read("POINT (" + position.getLongitude().getValue() + " " + position.getLatitude().getValue() + ")");
        } catch (ParseException e) {
            e.printStackTrace();
            throw e;
        }
        return geometry;
    }

}
