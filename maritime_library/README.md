<h2>Maritime Library</h2>

Die maritime_library ist eine domänenspezifische Umsetzung der maritimen M2 Ebene des Metamodells. Durch das Einbinden der base_library in der pom.xml wird mit den so bereitgestellten Klassen und Funktionen eine Library mit Fokus auf den maritimen Sektor erstellt.
Wichtig ist, dass die Package Struktur innerhalb der maritimen Library der Struktur der base_library entspricht.

Das Model-Package beinhaltet alle Klassen die in der M2 Ebene abgebildet sind. Dies sind z.B. Bojen, Schiffe, Häfen, etc.

Das Service-Package stellt neue Behaviours bereit, die z.B. die Routenfindung von Federates ermöglichen, den Ausfall einer Motorkomponente und so abgesetzte Notrufsignale simulieren, oder die Geschwindigkeit basierend auf dem befahrenen Gewässer regulieren.

In dem *maritime_library/src/test/model/scenario* Ordner sind Beispielimplementationen von Szenarien enthalten, die dort zusätzlich in .xml Dateien konvertiert werden, damit sie vom SimulationManager eingelesen werden können.

<strong>Für eine genauere Beschreibung der Library Struktur entweder die base_library/README.md oder auch die Dokumentation zur Hilfe ziehen.</strong>