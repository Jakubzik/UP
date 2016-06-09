<%--
    JSP-Object "pruefung/get"

    SYNOPSIS (German)
    ===========================
    2013, Feb 8, shj    erzeugt. 
    2013, Dez 26, shj   überarbeitet
    
    Üblicher Lifecycle: GET, Abruf von Studierenden/Config, 
    Unterscheidung absolvierte vs. konfigurierte Prüfungen.
    
    Expected SESSION PROPERTIES
    ===========================
    user        muss initialisiert sein (AccessLevel 500 oder mehr) und
                ein Studierende/r muss ausgewählt und initialisiert sein.

    ODER 
    
    Sitzungstyp student (d.h. es handelt sich um einen eingeloggten Studierenden,
                         der seine eigenen Prüfungen abruft).

    Expected PARAMETER(S):
    ===========================
    matrikelnummer          [long]
    konfigurierte_only      [optional, null/not null] Sollen nur 
                             die (im Studiengang des Studierenden) konfigurierten
                             (anstatt absolvierte) Prüfungen ausgegeben werden?

    Beim Sitzungstyp "student" muss
    konfigurierte_only null sein, 
    sonst wird ein Fehler ausgelöst.
    
    Returns
    =======
    Bsp. Abruf der absolvierten Prüfungen:
    --------------------------------------------
    {
        "pruefungen": [
            {
                "pruefung": {
                    "id": "1401",
                    "count": "1",
                    "matrikelnummer": "3084378",
                    "bezeichnung": "Bachelor",
                    "datum": "2014-01-01",
                    "note": "1.0",
                    "semester": "",
                    "notenberechnung": "<table><tr><td><b>Leistung/Modul</b></td><td><b>Note</b></td><td><b>LP</b></td></tr></table>"
                }
            }
        ]
    }

    Bsp. Abruf der konfigurierten Prüfungen:
    --------------------------------------------
    {
        "pruefungen": [
            {
                "pruefung": {
                    "id": "1407",
                    "bezeichnung": "Staatsexamen",
                    "beschreibung": "Staatsexamen nach GymPO (neu im WS 2010/11)",
                    "abschluss": "t"
                }
            },
            {
                "pruefung": {
                    "id": "200000",
                    "bezeichnung": "Orientierungsprüfung Anglistik",
                    "beschreibung": "Orientierungsprüfung Anglistik nach GymPO",
                    "abschluss": "f"
                }
            },
            {
                "pruefung": {
                    "id": "200001",
                    "bezeichnung": "Zwischenprüfung Anglistik",
                    "beschreibung": "Zwischenprüfung Anglistik nach GymPO",
                    "abschluss": "f"
                }
            }
        ]
    }

--%>
<%@ page contentType="text/json" pageEncoding="UTF-8" import="java.sql.ResultSet,de.shj.UP.data.shjCore" session="true" isThreadSafe="false" errorPage="../../error.jsp"%><jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean id="seminar" scope="session" class="de.shj.UP.logic.SeminarData" />
<jsp:useBean id="student" scope="session" class="de.shj.UP.beans.config.student.StudentBean" />
<jsp:useBean id="sd" scope="session" class="de.shj.UP.util.SessionData" />
<%@include file="../../../fragments/checkVersion.jsp" %>
<%long lERR_BASE=104000 + 100;    // Prüfung + Get
if(sd.getSessionType().equals("student")){%>
<%@include file="../../../fragments/checkLoginStudent.jsp" %>    
<%
if(!student.getMatrikelnummer().equals(request.getParameter("matrikelnummer")))
    throw new Exception("{\"error\":\"Die Sitzung ist abgelaufen, bitte melden Sie sich neu an.\",\"errorDebug\":\"Abruf der Leistung aus Studierendensicht, aber die Sitzungs-Matrikelnummer entspricht nicht der übergebenen Matrikelnummer. Abbruch sicherheitshalber.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":50}");

if(request.getParameter("konfigurierte_only")!=null)
    throw new Exception("{\"error\":\"Die konfigurierten Prüfungen sollen abgerufen werden; das ist Studierenden aber nicht erlaubt.\",\"errorDebug\":\"Der Parameter >konfigurierte_only< muss beim Aufruf von student/pruefung/get null sein.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":50}");
}else{%>
<%@include file="../../../fragments/checkLogin.jsp" %>
<%@include file="../../../fragments/checkAccess500.jsp" %>
<%@include file="../../../fragments/checkInitStudent.jsp" %>
<%}%>
<%!

// Welche Prüfungen sind für 
// das Fach des Studierenden
// konfiguriert?
String getPruefungenFach(de.shj.UP.beans.config.student.StudentBean stud) throws Exception{
	String sReturn="";
	ResultSet rPruefungen = stud.sqlQuery( 
			"SELECT " +
			  "p.\"lngPruefungID\", \"strPruefungBezeichnung\"," + 
			  "\"strPruefungBeschreibung\", \"blnPruefungFachAbschluss\" " +
			"FROM " + 
			  "\"tblSdPruefung\" p, " +  
			  "\"tblSdPruefungXFach\" pxf " +
			"WHERE " +
			  "p.\"lngSdSeminarID\" = pxf.\"lngSdSeminarID\" AND " +
			  "p.\"lngSdSeminarID\"=" + stud.getSeminarID() + " and " + 
			  "p.\"lngPruefungID\" = pxf.\"lngPruefungID\" AND " +
			  "pxf.\"intFachID\"=" + stud.Fach().getFachID() + " order by p.\"lngPruefungID\";");
	
	while(rPruefungen.next()){
		sReturn += ",{\"pruefung\":{\"id\":\"" + rPruefungen.getString("lngPruefungID") +  
			"\",\"bezeichnung\":\"" + rPruefungen.getString("strPruefungBezeichnung") + 
			"\",\"beschreibung\":\"" + rPruefungen.getString("strPruefungBeschreibung") + 
			"\",\"abschluss\":\"" + rPruefungen.getString("blnPruefungFachAbschluss") + "\"}}";
	}
	
	return sReturn.substring(1);
}
%>
{
 "pruefungen":[
<%  

    if(request.getParameter("konfigurierte_only") != null){
        out.write(getPruefungenFach(student));
    }else{
	de.shj.UP.beans.student.ExamIterator absolviert=new de.shj.UP.beans.student.ExamIterator(student.getSeminarID(), student.getMatrikelnummer());
	boolean bStart=true;
	while(absolviert.next()){
		if(!bStart) out.write(",");bStart=false;%>
		
		{"pruefung":{"id":"<%=absolviert.StudentPruefung().getSdPruefungsID() %>",
			"count":"<%=absolviert.getStudentPruefungCount() %>",
                        "matrikelnummer":"<%=student.getMatrikelnummer()%>",
			"bezeichnung":"<%=absolviert.getPruefungBezeichnung() %>",
			"datum":"<%=absolviert.StudentPruefung().getStudentPruefungAusstellungsd() %>",
			"note":"<%=absolviert.StudentPruefung().getStudentPruefungNote() %>",
			"semester":"<%=absolviert.StudentPruefung().getStudentPruefungSemester() %>",
                        "notenberechnung":"<%=shjCore.string2JSON(absolviert.StudentPruefung().getStudentPruefungCustom2()) %>"}
			}<%
		}
    }
%>]}