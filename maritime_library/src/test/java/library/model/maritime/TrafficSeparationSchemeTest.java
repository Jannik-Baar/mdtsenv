package library.model.maritime;

import library.model.simulation.Position;
import library.model.traffic.Infrastructure;
import library.model.traffic.PossibleDomains;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TrafficSeparationScheme.
 * 
 * Tests the creation and validation of TSS infrastructure according to IMO standards.
 */
class TrafficSeparationSchemeTest {

    private final GeometryFactory geometryFactory = new GeometryFactory();

    /**
     * Helper method to create a default polygon for testing.
     */
    private Polygon createDefaultPolygon() {
        Coordinate[] coordinates = new Coordinate[]{
                new Coordinate(1.0, 51.0),
                new Coordinate(1.5, 51.0),
                new Coordinate(1.5, 51.5),
                new Coordinate(1.0, 51.5),
                new Coordinate(1.0, 51.0)
        };
        return geometryFactory.createPolygon(coordinates);
    }

    /**
     * Tests creation of a basic traffic lane with north-to-south direction.
     */
    @Test
    void testCreateTrafficLaneWithCardinalDirection() {
        // Create a TSS for a northbound traffic lane
        TrafficSeparationScheme tss = new TrafficSeparationScheme(
                true,
                new Position(1.25, 51.25, 0),
                createDefaultPolygon(),
                0.0,
                TrafficSeparationSchemeZoneType.TRAFFIC_LANE,
                TrafficSeparationSchemeTrafficDirection.NORTH_TO_SOUTH,
                "Dover Strait Southbound Lane"
        );

        assertNotNull(tss);
        assertEquals("Dover Strait Southbound Lane", tss.getTssName().getValue());
        assertEquals(TrafficSeparationSchemeTrafficDirection.NORTH_TO_SOUTH, tss.getTrafficDirection().getValue());
        assertEquals(TrafficSeparationSchemeZoneType.TRAFFIC_LANE, tss.getZoneType().getValue());
    }

    /**
     * Tests creation of a TSS with custom bearing direction.
     */
    @Test
    void testCreateTrafficLaneWithCustomBearing() {
        // Create a TSS with a specific bearing of 045° (northeast)
        TrafficSeparationScheme tss = new TrafficSeparationScheme(
                true,
                new Position(5.0, 36.0, 0),
                createDefaultPolygon(),
                0.0,
                TrafficSeparationSchemeZoneType.TRAFFIC_LANE,
                45.0, // Custom bearing
                "Strait of Gibraltar TSS"
        );

        assertNotNull(tss);
        assertEquals(TrafficSeparationSchemeTrafficDirection.CUSTOM_BEARING, tss.getTrafficDirection().getValue());
        assertEquals(45.0, tss.getCustomBearing().getValue());
        assertEquals("Strait of Gibraltar TSS", tss.getTssName().getValue());
    }

    /**
     * Tests creation of a separation zone.
     */
    @Test
    void testCreateSeparationZone() {
        TrafficSeparationScheme separationZone = new TrafficSeparationScheme(
                true,
                new Position(1.25, 51.25, 0),
                createDefaultPolygon(),
                0.0,
                TrafficSeparationSchemeZoneType.SEPARATION_ZONE,
                TrafficSeparationSchemeTrafficDirection.NO_RESTRICTION,
                "Dover Strait Separation Zone"
        );

        assertTrue(separationZone.isEntryProhibited(), 
                "Separation zones should prohibit entry");
    }

    /**
     * Tests creation of an area to be avoided.
     */
    @Test
    void testCreateAreaToBeAvoided() {
        TrafficSeparationScheme avoidanceArea = new TrafficSeparationScheme(
                true,
                new Position(1.25, 51.25, 0),
                createDefaultPolygon(),
                0.0,
                TrafficSeparationSchemeZoneType.AREA_TO_BE_AVOIDED,
                TrafficSeparationSchemeTrafficDirection.NO_RESTRICTION,
                "Protected Marine Area"
        );

        assertTrue(avoidanceArea.isEntryProhibited(), 
                "Areas to be avoided should prohibit entry");
    }

    /**
     * Tests heading compliance for cardinal directions.
     */
    @Test
    void testHeadingComplianceCardinalDirection() {
        TrafficSeparationScheme northbound = new TrafficSeparationScheme(
                true,
                new Position(1.25, 51.25, 0),
                createDefaultPolygon(),
                0.0,
                TrafficSeparationSchemeZoneType.TRAFFIC_LANE,
                TrafficSeparationSchemeTrafficDirection.SOUTH_TO_NORTH,
                "Northbound Lane"
        );

        // Test vessel heading north (0°) - should be compliant
        assertTrue(northbound.isHeadingCompliant(0.0, 10.0));
        assertTrue(northbound.isHeadingCompliant(5.0, 10.0));
        assertTrue(northbound.isHeadingCompliant(355.0, 10.0));

        // Test vessel heading south (180°) - should not be compliant
        assertFalse(northbound.isHeadingCompliant(180.0, 10.0));
        
        // Test vessel heading east (90°) - should not be compliant
        assertFalse(northbound.isHeadingCompliant(90.0, 10.0));
    }

    /**
     * Tests heading compliance for custom bearing.
     */
    @Test
    void testHeadingComplianceCustomBearing() {
        // Create TSS with 045° bearing (northeast)
        TrafficSeparationScheme customBearingLane = new TrafficSeparationScheme(
                true,
                new Position(1.25, 51.25, 0),
                createDefaultPolygon(),
                0.0,
                TrafficSeparationSchemeZoneType.TRAFFIC_LANE,
                45.0,
                "Northeast Lane"
        );

        // Vessel heading 45° should be compliant
        assertTrue(customBearingLane.isHeadingCompliant(45.0, 10.0));
        assertTrue(customBearingLane.isHeadingCompliant(50.0, 10.0));
        assertTrue(customBearingLane.isHeadingCompliant(40.0, 10.0));

        // Vessel heading opposite direction should not be compliant
        assertFalse(customBearingLane.isHeadingCompliant(225.0, 10.0));
    }

    /**
     * Tests heading compliance with wrap-around at 0°/360°.
     */
    @Test
    void testHeadingComplianceWrapAround() {
        TrafficSeparationScheme northbound = new TrafficSeparationScheme(
                true,
                new Position(1.25, 51.25, 0),
                createDefaultPolygon(),
                0.0,
                TrafficSeparationSchemeZoneType.TRAFFIC_LANE,
                TrafficSeparationSchemeTrafficDirection.SOUTH_TO_NORTH,
                "Northbound"
        );

        // 359° should be within 10° tolerance of 0°
        assertTrue(northbound.isHeadingCompliant(359.0, 10.0));
        
        // 1° should be within 10° tolerance of 0°
        assertTrue(northbound.isHeadingCompliant(1.0, 10.0));
    }

    /**
     * Tests bidirectional traffic zones.
     */
    @Test
    void testBidirectionalTraffic() {
        TrafficSeparationScheme inshoreZone = new TrafficSeparationScheme(
                true,
                new Position(1.25, 51.25, 0),
                createDefaultPolygon(),
                0.0,
                TrafficSeparationSchemeZoneType.INSHORE_TRAFFIC_ZONE,
                TrafficSeparationSchemeTrafficDirection.BIDIRECTIONAL,
                "Coastal Traffic Zone"
        );

        // Any heading should be compliant in bidirectional zones
        assertTrue(inshoreZone.isHeadingCompliant(0.0, 10.0));
        assertTrue(inshoreZone.isHeadingCompliant(90.0, 10.0));
        assertTrue(inshoreZone.isHeadingCompliant(180.0, 10.0));
        assertTrue(inshoreZone.isHeadingCompliant(270.0, 10.0));
    }

    /**
     * Integration test: TSS as Infrastructure with restrictions.
     */
    @Test
    void testTSSAsInfrastructure() {
        // Create a TSS
        TrafficSeparationScheme tss = new TrafficSeparationScheme(
                true,
                new Position(1.25, 51.25, 0),
                createDefaultPolygon(),
                0.0,
                TrafficSeparationSchemeZoneType.TRAFFIC_LANE,
                TrafficSeparationSchemeTrafficDirection.WEST_TO_EAST,
                "English Channel Eastbound"
        );

        // Add maritime domain
        tss.addDomain(PossibleDomains.MARITIME);

        // Verify infrastructure properties
        assertTrue(tss.isUsableBy(PossibleDomains.MARITIME));
        assertFalse(tss.isUsableBy(PossibleDomains.ROAD));
        assertEquals(1.25, tss.getPosition().getValue().getLongitude());
        assertEquals(51.25, tss.getPosition().getValue().getLatitude());
    }

    /**
     * Tests roundabout traffic pattern.
     */
    @Test
    void testRoundaboutTraffic() {
        TrafficSeparationScheme roundabout = new TrafficSeparationScheme(
                true,
                new Position(1.25, 51.25, 0),
                createDefaultPolygon(),
                0.0,
                TrafficSeparationSchemeZoneType.ROUNDABOUT,
                TrafficSeparationSchemeTrafficDirection.CLOCKWISE,
                "Traffic Roundabout"
        );

        assertEquals(TrafficSeparationSchemeZoneType.ROUNDABOUT, roundabout.getZoneType().getValue());
        assertEquals(TrafficSeparationSchemeTrafficDirection.CLOCKWISE, roundabout.getTrafficDirection().getValue());
        
        // Roundabouts currently always return true (simplified implementation)
        assertTrue(roundabout.isHeadingCompliant(45.0, 10.0));
    }

    /**
     * Tests precautionary area.
     */
    @Test
    void testPrecautionaryArea() {
        TrafficSeparationScheme precautionaryArea = new TrafficSeparationScheme(
                true,
                new Position(1.25, 51.25, 0),
                createDefaultPolygon(),
                0.0,
                TrafficSeparationSchemeZoneType.PRECAUTIONARY_AREA,
                TrafficSeparationSchemeTrafficDirection.NO_RESTRICTION,
                "High Traffic Density Area"
        );

        assertEquals(TrafficSeparationSchemeZoneType.PRECAUTIONARY_AREA,
                precautionaryArea.getZoneType().getValue());
        assertFalse(precautionaryArea.isEntryProhibited(), 
                "Precautionary areas do not prohibit entry");
    }

    /**
     * Tests default constructor for XML deserialization.
     */
    @Test
    void testDefaultConstructor() {
        TrafficSeparationScheme tss = new TrafficSeparationScheme();
        assertNotNull(tss);
    }

    /**
     * Tests no-anchoring area.
     */
    @Test
    void testNoAnchoringArea() {
        TrafficSeparationScheme noAnchorZone = new TrafficSeparationScheme(
                true,
                new Position(1.25, 51.25, 0),
                createDefaultPolygon(),
                0.0,
                TrafficSeparationSchemeZoneType.NO_ANCHORING_AREA,
                TrafficSeparationSchemeTrafficDirection.NO_RESTRICTION,
                "Pipeline Protection Zone"
        );

        assertEquals(TrafficSeparationSchemeZoneType.NO_ANCHORING_AREA,
                noAnchorZone.getZoneType().getValue());
        assertFalse(noAnchorZone.isEntryProhibited(), 
                "No-anchoring areas allow passage but prohibit anchoring");
    }
}

