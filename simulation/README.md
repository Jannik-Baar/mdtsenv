# Simulation

Das *simulation* Package ist für die Simulation von Szenarien zuständig, die mit der Hilfe von der *base_library* und anderen domänenspezifischen Libraries erstellt worden sind.
Durch den SimulationManager können aus einer geladenen Szenario-Datei (.xml) HLA konforme Federates erstellt werden und als Federation über eine RTI (Runtime-Infrastructure) kommunizieren. Das HLA Framework Portico wird hierbei verwendet, um grundlegende HLA Komponenten wie die RTI zu realisieren.

### Funktionsweise Simulation Manager

Im README.md des Hauptverzeichnisses wird erläutert, wie der Simulation Manager gestartet werden kann. In der folgenden Liste werden die einzelnen bereitgestellten Befehle erläutert:

 - *help* : Gibt eine Liste aller hier stehenden Befehle aus
 - *scenario list* : Gibt eine List mit allen aktuellen geladenen Szenarien aus
 - *scenario add <path\>* : Fügt ein neues Szenario basierend auf dem übergebenen Pfad hinzu
 - *scenario add* : Öffnet ein Explorer-Fenster, in dem der Nutzer ein neues Szenario auswählen und laden kann
 - *scenario remove <index>* : Entfernt das an dem übergebenen Index befindliche Szenario aus der Liste geladener Szenarien
 - *scenario clear* : Entfernt alle Szenarien aus der Liste.
 - *start* : Führt alle Szenarien in der Reihenfolge, in der Sie geladen worden sind, durch
 - *exit* : Beendet den SimulationManager

Für eine detaillierte Erläuterung des *simulation* Packages, siehe Dokumentation.

