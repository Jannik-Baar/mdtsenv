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
 * Unit tests for TrafficSeparationSchemeRestriction.
 * 
 * Tests the creation and validation of TSS restrictions according to IMO standards.
 */
class TrafficSeparationSchemeRestrictionTest {

    private final GeometryFactory geometryFactory = new GeometryFactory();

    /**
     * Tests creation of a basic traffic lane with north-to-south direction.
     */
    @Test
    void testCreateTrafficLaneWithCardinalDirection() {
        // Create a TSS restriction for a northbound traffic lane
        TrafficSeparationSchemeRestriction tssRestriction = new TrafficSeparationSchemeRestriction(
                TrafficSeparationSchemeZoneType.TRAFFIC_LANE,
                TrafficSeparationSchemeTrafficDirection.NORTH_TO_SOUTH,
                "Dover Strait Southbound Lane"
        );

        assertNotNull(tssRestriction);
        assertEquals("Dover Strait Southbound Lane", tssRestriction.getTssName().getValue());
        assertEquals(TrafficSeparationSchemeTrafficDirection.NORTH_TO_SOUTH, tssRestriction.getTrafficDirection().getValue());
        assertFalse(tssRestriction.getLimitedProperties().isEmpty());
        assertEquals(TrafficSeparationSchemeZoneType.TRAFFIC_LANE, tssRestriction.getLimitedProperties().get(0).getValue());
    }

    /**
     * Tests creation of a TSS with custom bearing direction.
     */
    @Test
    void testCreateTrafficLaneWithCustomBearing() {
        // Create a TSS with a specific bearing of 045° (northeast)
        TrafficSeparationSchemeRestriction tssRestriction = new TrafficSeparationSchemeRestriction(
                TrafficSeparationSchemeZoneType.TRAFFIC_LANE,
                45.0, // Custom bearing
                "Strait of Gibraltar TSS"
        );

        assertNotNull(tssRestriction);
        assertEquals(TrafficSeparationSchemeTrafficDirection.CUSTOM_BEARING, tssRestriction.getTrafficDirection().getValue());
        assertEquals(45.0, tssRestriction.getCustomBearing().getValue());
        assertEquals("Strait of Gibraltar TSS", tssRestriction.getTssName().getValue());
    }

    /**
     * Tests creation of a separation zone.
     */
    @Test
    void testCreateSeparationZone() {
        TrafficSeparationSchemeRestriction separationZone = new TrafficSeparationSchemeRestriction(
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
        TrafficSeparationSchemeRestriction avoidanceArea = new TrafficSeparationSchemeRestriction(
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
        TrafficSeparationSchemeRestriction northbound = new TrafficSeparationSchemeRestriction(
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
        TrafficSeparationSchemeRestriction customBearingLane = new TrafficSeparationSchemeRestriction(
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
        TrafficSeparationSchemeRestriction northbound = new TrafficSeparationSchemeRestriction(
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
        TrafficSeparationSchemeRestriction inshoreZone = new TrafficSeparationSchemeRestriction(
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
     * Integration test: TSS restriction applied to infrastructure.
     */
    @Test
    void testTSSAppliedToInfrastructure() {
        // Create a polygon representing the traffic lane geometry
        Coordinate[] coordinates = new Coordinate[]{
                new Coordinate(1.0, 51.0),
                new Coordinate(1.5, 51.0),
                new Coordinate(1.5, 51.5),
                new Coordinate(1.0, 51.5),
                new Coordinate(1.0, 51.0)
        };
        Polygon laneGeometry = geometryFactory.createPolygon(coordinates);

        // Create infrastructure with TSS restriction
        Infrastructure tssLane = new Infrastructure(
                true,
                new Position(1.25, 51.25, 0),
                laneGeometry,
                0.0
        );

        // Add maritime domain
        ArrayList<PossibleDomains> domains = new ArrayList<>();
        domains.add(PossibleDomains.MARITIME);
        tssLane.setCanBeUsedBy(domains);

        // Create and add TSS restriction
        TrafficSeparationSchemeRestriction tssRestriction = new TrafficSeparationSchemeRestriction(
                TrafficSeparationSchemeZoneType.TRAFFIC_LANE,
                TrafficSeparationSchemeTrafficDirection.WEST_TO_EAST,
                "English Channel Eastbound"
        );

        tssLane.getImposedRestrictions().add(tssRestriction);

        // Verify the restriction is properly attached
        assertFalse(tssLane.getImposedRestrictions().isEmpty());
        assertEquals(1, tssLane.getImposedRestrictions().size());
        assertTrue(tssLane.isUsableBy(PossibleDomains.MARITIME));
    }

    /**
     * Tests roundabout traffic pattern.
     */
    @Test
    void testRoundaboutTraffic() {
        TrafficSeparationSchemeRestriction roundabout = new TrafficSeparationSchemeRestriction(
                TrafficSeparationSchemeZoneType.ROUNDABOUT,
                TrafficSeparationSchemeTrafficDirection.CLOCKWISE,
                "Traffic Roundabout"
        );

        assertEquals(TrafficSeparationSchemeZoneType.ROUNDABOUT, roundabout.getLimitedProperties().get(0).getValue());
        assertEquals(TrafficSeparationSchemeTrafficDirection.CLOCKWISE, roundabout.getTrafficDirection().getValue());
        
        // Roundabouts currently always return true (simplified implementation)
        assertTrue(roundabout.isHeadingCompliant(45.0, 10.0));
    }

    /**
     * Tests precautionary area.
     */
    @Test
    void testPrecautionaryArea() {
        TrafficSeparationSchemeRestriction precautionaryArea = new TrafficSeparationSchemeRestriction(
                TrafficSeparationSchemeZoneType.PRECAUTIONARY_AREA,
                TrafficSeparationSchemeTrafficDirection.NO_RESTRICTION,
                "High Traffic Density Area"
        );

        assertEquals(TrafficSeparationSchemeZoneType.PRECAUTIONARY_AREA,
                precautionaryArea.getLimitedProperties().get(0).getValue());
        assertFalse(precautionaryArea.isEntryProhibited(), 
                "Precautionary areas do not prohibit entry");
    }

    /**
     * Tests default constructor for XML deserialization.
     */
    @Test
    void testDefaultConstructor() {
        TrafficSeparationSchemeRestriction tss = new TrafficSeparationSchemeRestriction();
        assertNotNull(tss);
    }

    /**
     * Tests no-anchoring area.
     */
    @Test
    void testNoAnchoringArea() {
        TrafficSeparationSchemeRestriction noAnchorZone = new TrafficSeparationSchemeRestriction(
                TrafficSeparationSchemeZoneType.NO_ANCHORING_AREA,
                TrafficSeparationSchemeTrafficDirection.NO_RESTRICTION,
                "Pipeline Protection Zone"
        );

        assertEquals(TrafficSeparationSchemeZoneType.NO_ANCHORING_AREA,
                noAnchorZone.getLimitedProperties().get(0).getValue());
        assertFalse(noAnchorZone.isEntryProhibited(), 
                "No-anchoring areas allow passage but prohibit anchoring");
    }
}

