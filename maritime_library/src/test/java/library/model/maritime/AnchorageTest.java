package library.model.maritime;

// Import der DTO-Klasse für das Szenario (wichtig: ScenarioDTO statt Scenario)
import library.model.dto.scenario.ScenarioDTO;

// Importe für Simulations-Komponenten
import library.model.simulation.Position;
import library.model.simulation.FormDummy;

// JUnit 5 Importe
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

// JAXB für XML-Verarbeitung
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testklasse zur Verifizierung der Anforderung F-2.7 (Ankerplätze).
 * Prüft Initialisierung und XML-Persistenz.
 */
class AnchorageTest {

    // JUnit 5 erstellt hiermit automatisch einen temporären Ordner für den Testlauf
    @TempDir
    Path tempDir;

    /**
     * Testet, ob ein Anchorage-Objekt korrekt mit Werten initialisiert wird.
     */
    @Test
    @DisplayName("Test der Initialisierung von Anchorage")
    void testAnchorageInitialization() {
        // 1. ARRANGE (Vorbereitung)
        Position pos = new Position(53.0, 8.0, 0.0);
        int maxCap = 20;
        int usedCap = 5;

        // 2. ACT (Ausführung)
        // Konstruktor: Position, Form, Rotation, Physical, MaxCapacity, UsedCapacity
        Anchorage anchorage = new Anchorage(
                pos,
                new FormDummy(),
                0.0,
                false,
                maxCap,
                usedCap
        );

        // 3. ASSERT (Überprüfung)
        assertNotNull(anchorage, "Anchorage-Objekt sollte erstellt worden sein");

        // Prüfen der spezifischen Eigenschaften für Ankerplätze
        assertEquals(maxCap, anchorage.getMaxCapacity().getValue(),
                "MaxCapacity sollte mit dem Wert 20 initialisiert sein");
        assertEquals(usedCap, anchorage.getUsedCapacity().getValue(),
                "UsedCapacity sollte mit dem Wert 5 initialisiert sein");

        // Prüfen einer geerbten Eigenschaft
        assertFalse(anchorage.getPhysical().getValue(),
                "Physical-Eigenschaft sollte 'false' sein");
    }

    /**
     * Testet Anforderung F-2.7: "Das Simulationsmodell muss die Abbildung von Ankerplätzen ermöglichen."
     * Dieser Test prüft die technische Abbildung durch Speichern (Marshalling) und Laden (Unmarshalling).
     */
    @Test
    @DisplayName("F-2.7: Test der XML-Persistenz (Marshalling) für Ankerplätze")
    void testAnchorageMarshalling() throws JAXBException {
        // 1. ARRANGE
        Position pos = new Position(54.0, 7.5, 0.0);
        int expectedMax = 10;
        int expectedUsed = 2;

        Anchorage originalAnchorage = new Anchorage(
                pos,
                new FormDummy(),
                0.0,
                false,
                expectedMax,
                expectedUsed
        );

        // ScenarioDTO als Container verwenden
        ScenarioDTO scenario = new ScenarioDTO();
        scenario.addSimulationObject(originalAnchorage);

        // Temporäre Datei für den Test definieren
        File xmlFile = tempDir.resolve("AnchorageTest.xml").toFile();

        // 2. ACT
        JAXBContext context = JAXBContext.newInstance("library.model");

        // Speichern (Objekt -> XML)
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(scenario, xmlFile);

        // Laden (XML -> Objekt)
        Unmarshaller unmarshaller = context.createUnmarshaller();
        ScenarioDTO loadedScenario = (ScenarioDTO) unmarshaller.unmarshal(xmlFile);

        // 3. ASSERT
        assertNotNull(loadedScenario.getSimulationObjects(), "Liste der Objekte sollte nicht null sein");
        assertEquals(1, loadedScenario.getSimulationObjects().size(), "Es sollte genau ein Objekt geladen werden");

        // Prüfen, ob das generische SimulationObject wirklich ein Anchorage ist
        Object loadedObj = loadedScenario.getSimulationObjects().get(0);
        assertTrue(loadedObj instanceof Anchorage, "Geladenes Objekt muss vom Typ Anchorage sein");

        Anchorage loadedAnchorage = (Anchorage) loadedObj;

        // Validierung der fachlichen Werte (F-2.7)
        assertEquals(expectedMax, loadedAnchorage.getMaxCapacity().getValue(),
                "Fehler: MaxCapacity wurde nicht korrekt wiederhergestellt.");
        assertEquals(expectedUsed, loadedAnchorage.getUsedCapacity().getValue(),
                "Fehler: UsedCapacity wurde nicht korrekt wiederhergestellt.");
    }
}