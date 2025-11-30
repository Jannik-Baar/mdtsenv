package library.model.simulation;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateFilter;
import org.locationtech.jts.geom.CoordinateSequenceComparator;
import org.locationtech.jts.geom.CoordinateSequenceFilter;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryComponentFilter;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.GeometryFilter;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Random;

/**
 * Class to provide a default form dummy if no form is available
 */
@XmlRootElement
public class FormDummy extends Geometry {

    private final Integer hashCode;

    public FormDummy() {
        super(new GeometryFactory());
        StringBuilder hc = new StringBuilder();
        Random rdm = new Random();
        for (int i = 0; i < 5; i++) {
            hc.append(rdm.nextInt(10));
        }
        hashCode = Integer.parseInt(hc.toString());
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public String toString() {
        return "POINT (0 0)";
    }

    @Override
    public String getGeometryType() {
        return "Point";
    }

    @Override
    public Coordinate getCoordinate() {
        return new Coordinate(0.0, 0.0);
    }

    @Override
    public Coordinate[] getCoordinates() {
        return new Coordinate[]{new Coordinate(0.0, 0.0)};
    }

    @Override
    public int getNumPoints() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public int getDimension() {
        return 0;
    }

    @Override
    public Geometry getBoundary() {
        return null;
    }

    @Override
    public int getBoundaryDimension() {
        return 0;
    }

    @Override
    protected Geometry reverseInternal() {
        return null;
    }

    @Override
    public boolean equalsExact(Geometry geometry, double v) {
        return false;
    }

    @Override
    public void apply(CoordinateFilter coordinateFilter) {

    }

    @Override
    public void apply(CoordinateSequenceFilter coordinateSequenceFilter) {

    }

    @Override
    public void apply(GeometryFilter geometryFilter) {

    }

    @Override
    public void apply(GeometryComponentFilter geometryComponentFilter) {

    }

    @Override
    protected Geometry copyInternal() {
        return null;
    }

    @Override
    public void normalize() {

    }

    @Override
    protected Envelope computeEnvelopeInternal() {
        // Return an empty envelope at coordinate (0,0) to avoid NullPointerException
        return new Envelope(0.0, 0.0, 0.0, 0.0);
    }

    @Override
    protected int compareToSameClass(Object o) {
        return 0;
    }

    @Override
    protected int compareToSameClass(Object o, CoordinateSequenceComparator coordinateSequenceComparator) {
        return 0;
    }

    @Override
    protected int getTypeCode() {
        return 0;
    }
}
