<%@ page language="java" pageEncoding="UTF8"%><%

// Erlaubt (für den experimentellen 
// Studiengang-Manager) Zugang zu 
// json/seminar/fach/get.jsp auch 
// ohne Login.
final boolean g_bFachForEveryone=true;
    
// Wie viele Buchstaben müssen mindestens 
// bei der Suche nach Studierenden 
// vorgegeben sein?
// (Bei einem negativen Wert werden bei 
//  leerer Suchanfrage alle Studierenden
//  gelistet).
final int g_iMinSearchChars_Studierende = 3;

// Siehe Datei ./navigationLeftMaster_student.jsp
// für Konfigurationsmöglichkeiten zur Antragstellung
// Transkripte.

// --------------------------------------------------
// Stati der Anträge von Studierenden (auf 
// Ausstellen eines Transkripts, Bafög 48, 
// Zeugnisses etc.). Muss der 
// Konfiguration in der Datenbank entsprechen:
final long lANTRAG_STATUS_TYP_NEU       = 1;
long lANTRAG_STATUS_TYP_ABGESCHLOSSEN   = 100;

// Wenn bei den Leistungen der Studierenden 
// Kurstitel korrigiert werden, kann sich das 
// entweder auf alle Kurse mit gleichem Titel 
// auswirken, oder nur auf die spezifisch zu 
// ändernde Leistung. Das kann in leistung/update 
// konfiguriert werden:
// g_bSpreadKurstitelChanges in student/leistung/update.jsp

// Für die Terminbuchung (siehe 
// json/seminar/dozent/termin/update|delete)
// kann verboten werden, dass mehr als 
// ein Termin pro Tag von einem Nutzer
// (vgl json/nutzer) gebucht wird.
// #hack: vielleicht sollte man das zu 
// einer Eigenschaft des Dozenten machen, der 
// die Sprechstunde anbietet?
final boolean bENFORCE_ONE_APPOINTMENT_PER_DAY=true;

%>
