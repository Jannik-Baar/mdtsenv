<h2>Base Library</h2>

Die base_library stellt das Grundgerüst dar, das von jeder anderen domänenspezifischen Library für die Entwicklung als Dependency benötigt wird.
In der maritime_library ist die base_library in der pom.xml eingebunden und alle Simulationsobjekte bauen auf den in der base_library bereitgestellten Klassen auf.

Die base_library bietet zwei Packages in *src/main/java/library/* an:

<strong>Model:</strong>

- Stellt alle MOF Meta-Modell Klassen von der M5 Ebene bis einschließlich der verkehrspezifischen M3 Ebene bereit. Für Klassen der domänenspezifischen Modellebene M2
muss eine neue Library angelegt werden.

- Zusätzlich gibt das Model die Szenario und Observer Klassen vor, mit welchen die Szenarien letztendlich definiert und später serialisiert werden. 

<strong>Service:</strong>

Bietet verschiedene Servicefunktionen an: 

- Stellt alle zum Laden und Erstellen von Behaviours relevanten Klassen und Interfaces bereit, einschließlich einiger Beispiele und ein ControlledBehaviour, das für die externe Ansteuerung eines Federates benötigt wird.

- Einen ConfigProvider der Configurations-Attribute aus einer .properties-Datei auslesen kann.

- Einen Logging Service der es erlaubt verschiedene Logging Methoden zu registrieren und Daten zu loggen. Aktuell besteht die Option Console, File, UDP, und WebSocket Logger zum Loggen von Daten zu verwenden.

- Ein Geodata Package zum Einlesen und Bereitstellen von Kartendaten

- Einen ScenarioConverter der Scenario Klassen mittels JAXB in .xml Dateien konvertiert, damit diese in der Simulation eingelesen werden können.

Das Test Package stellt einige Tests bereit in denen die Verwendung der verschiedenen base_library Komponenten nachvollzogen werden kann. Testfälle für das Erstellen und Serialisieren eines Szenarios sind in der maritime_library zu finden.

<strong>Beim Erstellen neuer Libraries muss die interne Grundstruktur der neuen Library der base_library entsprechen. Im template_library_source.zip im Root-Verzeichnis ist diese Grundstruktur enthalten. Zusätzlich ist dort eine README.md die erklärt, wie mit der template_library eine neue Library erstellt werden kann.</strong>

<strong>Sollte die base_library erweitert werden, ist zu beachten, dass hinzugefügte Klassen und Funktionen so grundlegend in ihrer Anwendung sind, dass sie prinzipiell in allen weiteren domänenspezifischen Libraries Anwendung finden könnten. </strong>

Für eine detailliertere Erläuterung der *base_library*, siehe Dokumentation.