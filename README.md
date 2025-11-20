# *M*odel-based *D*istributed *T*raffic *S*imulation *Env*ironment (MDTSEnv)

## ---

# <span style="color:red">TODO update readme</color>

## ---

# TODO List
- use proper logging libraries instead of system.out.print
- <span style="color:red">__federates do block each other, since they are waiting for everyone to complete the current time step__</color>
    - introduce two possible modes: BLOCKING and FREE FLOW (for the latter see following explanation)
    - let everyone do stuff in their own pace... after completing the step populate the updated values to the RTI, get the current time and start the next calculation step
    - RTI knows about all currently up-to-date values which should be the basis for each calculation
    - no need to wait for everybody at every timestep
    - another advantage: different stepsizes would be possible (maybe some assistant systems only perform a calculation every minute but others could update every 10 seconds)
- implement and test real distributed simulations (currently everything is being started and running on the same machine)
- a lot of classes and methods are using variables as fields (aka class variables) where local variables (method variables) are way more appropriate
  - analyse all classes and revise where necessary!
- use some kind of template engine for FOM generation instead of those endless StringBuilder call chains / string concatenations
- _see code comments (TODO, FIXME, QUESTION) for a lot of further (smaller) todos_

## ---

# PROBLEMS AND WORKAROUNDS

- Java NIO connection exception: https://bugs.java.com/bugdatabase/view_bug.do?bug_id=8272476

## ---

# PGMTSS-MAIN
### HLA Co-Simulationsframework

Das PGMTSS HLA Co-Simulationsframework stellt eine umfassende Bibliothek bereit, mit der Co-Simulations Szenarien mit der Hilfe von domänenspezifischen Libraries erstellt und anschließend durch einen Simulationsmanager nach dem HLA Standard (High Level Architecture) simuliert werden können.

Die Grundstruktur eines frisch aufgesetzten Projektes sollte wie folgt aussehen:

    pgmtss [pgmtss-main]
        |
        -> base_library
        -> maritime_library
        -> maven_plugins
        -> simulation
        .gitignore
        build.bat
        ExampleScenario.xml
        pom.xml
        README.md
        run.bat
        template_library_source.zip

**Java 15 wird als Project SDK benötigt, um eine lauffähige Version zu gewährleisten!**

**Für eine detaillierte Erläuterung der einzelnen Modules bitte die README.md Dateien innerhalb der Modules, oder die Dokumentation lesen.**

Um dieses Framework weiterzuentwickeln, muss das Build-Management-Tool Maven verwendet werden, damit die verschiedenen Abhängigkeiten der internen Komponenten erstellt werden können. Entwicklungsumgebungen wie z.B. IntelliJ unterstützen Maven meistens nativ.

- **Setup in IntelliJ**: Beim erstmaligen Öffnen des Projektes innerhalb von IntelliJ, sollte theoretisch automatisch erkannt werden, dass es sich um ein Maven Projekt handelt. Ist dies nicht der Fall, kann mit einem Rechtsklick auf die im Hauptverzeichnis befindliche pom.xml unter dem Eintrag "*Add as Maven Project*" das Projekt initialisiert werden.
- **Setup in Eclipse**: Auch Eclipse sollte das Maven Projekt automatisch erkannt werden, ist dies nicht der Fall kann das Projekt ähnlich wie beim IntelliJ Setup initialisiert werden. Zusätzlich empfiehlt es sich in Eclipse die Eclipse m2e Erweiterung zu installieren, die die Handhabung von Maven erleichtert.

### Portico einbinden

Damit das notwendige Framework Portico, welches eine RTI Implementation bereitstellt, verwendet werden kann, muss einmalig der folgende Befehl im Hauptverzeichnis ausgeführt werden: (Dies kann entweder über die Maven Tools in der Entwicklungsumgebung oder mit der Konsole geschehen.)

*mvn install:install-file -Dfile=simulation/lib/portico-2.1.1/lib/portico.jar -DgroupId=portico -DartifactId=portico -Dversion=2.1.1 -Dpackaging=jar -DgeneratePom=true*
### Modulabhängigkeiten

Aufgrund der internen Modulabhängigkeiten, ist es wichtig die einzelnen Module initial in der richtigen Reihenfolge zu kompilieren und zu bauen. Die initiale Reihenfolge, in der dies stattfinden muss, ist wie folgt einzuhalten:

    BUILD AND INSTALL ORDER
    1.  Maven install: maven_plugins  
    2.  Maven install: base_library  
    3.  Maven install: Alle Libraries die auf der base_library aufbauen 
        (Initial nur die maritime_library).
    4.  Maven package: simulation 

Die Dependency-Struktur innerhalb der Modules ist also wie folgt: 
***maven_plugins -> base_library -> domänenspezifische Libraries -> simulation***

Je nachdem, in welchem Modul während der Entwicklung Änderungen vorgenommen werden, müssen beginnend ab dem betroffenen Package alle darauffolgenden Module erneut mit Maven gebaut/installiert werden. Die Änderungen werden sonst nicht übernommen.

**Beispiel:** Die *base_library* wurde verändert. Damit jeder die Änderungen seiner Abhängigkeiten erhält, müssen nun neben einem Maven Install der *base_library*, auch alle darauffolgenden Module in der Dependency-Struktur geupdated werden (Schritt 3-4 in der obigen Liste). In IntelliJ lässt sich dies leicht über den normalerweise am rechten Bildschirmrand befindlichen Maven-Reiter durchführen.

**Alternativ kann auch die *build.bat* im Hauptverzeichnis aufgerufen werden, diese führt die Schritte 1-4 automatisch durch. Voraussetzung ist jedoch, dass Maven auf dem System installiert ist.**

### Ausführen einer Simulation
Nachdem die im vorherigen Abschnitt erklärten Abhängigkeiten berücksichtigt worden sind, können nun Simulationsszenarien durch Hilfe des Simulationsmanagers durchgeführt werden. Dazu muss die SimulationManager-Klasse in *simulation/src/main/java/simulationManger/SimulationManager.java* über die Entwicklungsumgebung ausgeführt werden. 

**Alternativ besteht die Möglichkeit die *run.bat* im Hauptverzeichnis aufzurufen, um den SimulationManager zu starten. Allerdings hat auch diese die Voraussetzung, dass Maven auf dem System installiert ist.**

Ist das Ausführen des SimulationManagers erfolgreich, sollte sich zunächst ein Explorer-Fenster öffnen, mit dem ein initiales Szenario geladen werden kann. Ein Szenario ist eine .xml-Datei und der SimulationManager akzeptiert auch nur solche. 

Zu Demonstrationszwecken sollte das sich im Hauptverzeichnis befindliche *ExampleScenario.xml* geladen werden.

Ist ein Szenario geladen, listet der SimulationManager eine Reihe an Befehlen auf. Unter Eingabe des *"start"* Befehls wird die Simulation gestartet. **Ein detailliertere Liste der SimulationManager Befehle ist in der README.md des *simulation* Packages oder in der Dokumentation zu finden!**

Da der SimulationManager keine Visualisierung der Simulation beinhaltet, wird in dem ExampleScenario.xml ein Observer initialisiert, der versucht die Daten an einen im *PGMTSS_WEBSOCKET_CLIENT* enthaltenen Websocket Server zu senden, der auf "*localhost:3001*" laufen sollte. Ist dies der Fall, kann mit dem *PGMTSS_WEBSOCKET_CLIENT* eine rudimentäre Visualisierung des Szenarios betrachtet werden.

Auf dieselbe Art und Weise kann außerdem das *ExampleControlledScenario.xml* geladen werden. Dieses Szenario beinhaltet ein *ControlledFederate*, dass die Steuerung des Schiffes "SS HOPE" ermöglicht. Zu diesem Zweck hat die Webanwendung des *PGMTSS_WEBSOCKET_CLIENT* in der links unteren Ecke zwei Slider, mit denen die Ausrichtung und Geschwindigkeit der "SS Hope" kontrollierbar sind. Für das *ExampleScenario1.xml* haben diese beiden Slider keine Bedeutung.