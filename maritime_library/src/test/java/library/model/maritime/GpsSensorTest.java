package library.model.maritime;

import library.model.simulation.Position;
import library.model.simulation.SimulationProperty;
import library.model.simulation.units.NoUnit;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GpsSensorTest {

    @Test
    void vesselProvidesGpsPositionWhenSensorIsEnabled() {
        Vessel vessel = new Vessel();
        Position referencePosition = new Position(8.5, 53.5, 12.0);
        vessel.setPosition(new SimulationProperty<>(NoUnit.get(), referencePosition, "position"));

        GpsSensor sensor = new GpsSensor(vessel.getTimeStepSize(), vessel);
        vessel.setGpsSensor(sensor);

        assertTrue(vessel.getGpsSensor() != null);

        vessel.getGpsSensor().captureParentPosition();
        Position gpsPosition = vessel.getGpsSensor().getGpsPosition();

        assertEquals(referencePosition.getLatitude().getValue(), gpsPosition.getLatitude().getValue());
        assertEquals(referencePosition.getLongitude().getValue(), gpsPosition.getLongitude().getValue());
        assertEquals(referencePosition.getAltitude().getValue(), gpsPosition.getAltitude().getValue());
    }
}


