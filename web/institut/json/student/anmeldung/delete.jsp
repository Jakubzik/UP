<%--
    JSP-Object "student/anmeldung/delete"


    SYNOPSIS (German)
    ===========================
    2012, Dec 30, shj    erzeugt. 
    2013, Dez 28, shj    überarbeitet.
    2016, Jun 20, shj    anpassen auf OS

    Üblicher Lifecycle: DELETE

    Löscht die Anmeldung aus StudentXLeistung. Dazu ist eine 
    Berechtigung von 500 notwendig; sonst kann man auch 
    "eigene" Anmeldungen nicht löschen.

    Ohne Berechtigung 500 geht das Löschen auch per Sitzungstyp
    "student": d.h. Studierende können die eigenen Anmeldungen 
    auch löschen, allerdings nur innerhalb der Anmeldefrist,
    wie sie in tblBdStudentXLeistung vermerkt ist.

    @SEE ALSO
    ===========
    /student/leistung/delete
    
    Expected SESSION PROPERTIES
    ===========================
    user        muss initialisiert sein (AccessLevel 500 oder höher),
    ODER        sd.getSessionType == "student"

    student     muss identische Matrikelnummer haben, die auch
                als Parameter übergeben wird.

    Expected PARAMETER(S):
    ===========================
    matrikelnummer  [long]
    id              [long] ID der Leistung aus tblBdStudentXLeistung
    count           [long] Count der Leistung aus tblBdStudentXLeistung
    signup-expected-backend-version [text], vgl. fragments/checkVersion.jsp
    
    Returns
    =======
    Object:
    {"success":"true"}

    Sample Usage
    ============
    <jsp:include page="student/anmeldung/delete.jsp?id=7&matrikelnummer=1488258&count=10&signup-expected-backend-version=1-01-2.7726-1" />
    
--%><%@page import="java.sql.ResultSet"%>
<%@page import="java.util.Date"%>
<%@ page contentType="text/json" pageEncoding="UTF-8" import="java.util.Enumeration,java.text.SimpleDateFormat, de.shj.UP.data.Dozent,de.shj.UP.data.shjCore" session="true" isThreadSafe="false" errorPage="../../error.jsp"%>
<%@page import="de.shj.UP.data.StudentBemerkung"%><jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean id="seminar" scope="session" class="de.shj.UP.logic.SeminarData" />
<jsp:useBean id="student" scope="session" class="de.shj.UP.logic.StudentData" />
<jsp:useBean id="credits" scope="page" class="de.shj.UP.beans.config.student.StudentLeistungBean" />
<jsp:useBean id="sd" scope="session" class="de.shj.UP.util.SessionData" />
<%@include file="../../../fragments/checkVersion.jsp" %>
<%long lERR_BASE=102000 + 300;    // Leistung + Delete
    if(sd.getSessionType().equals("student")){%>
<%@include file="../../../fragments/checkLoginStudent.jsp" %>    
<%
if(!student.getMatrikelnummer().equals(request.getParameter("matrikelnummer")))
    throw new Exception("{\"error\":\"Die Sitzung ist abgelaufen, bitte melden Sie sich neu an.\",\"errorDebug\":\"Abruf der Leistung aus Studierendensicht, aber die Sitzungs-Matrikelnummer entspricht nicht der übergebenen Matrikelnummer. Abbruch sicherheitshalber.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":50}");
}else{%>
<%@include file="../../../fragments/checkLogin.jsp" %>
<%@include file="../../../fragments/checkAccess500.jsp" %>
<%@include file="../../../fragments/checkInitStudent.jsp" %>
<%}

    // ==============================================================
    // Schnittstelle
    // ==============================================================
    long lLeistungID = -1;
    long lLeistungCount=-1;
    try{
        lLeistungID=Long.parseLong(request.getParameter("id"));
        lLeistungCount=Long.parseLong(request.getParameter("count"));
    }catch(Exception eNotHandedOver){throw new Exception("{\"error\":\"Die Anmeldung kann nicht gelöscht werden.\",\"errorDebug\":\"Der Parameter >id< (" + request.getParameter("leistung_id") + ") oder >count< (" + request.getParameter("leistung_count") + ") wurde nicht korrekt übergeben.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":100}");}

    if(sd.getSessionType().equals("student")){
        // Studierende können nur innerhalb der Anmelde
        // frist ihre Anmeldungen löschen
        Date fristende=null;
        try{
            ResultSet rTmp = student.sqlQuery("select \"dtmSLKursScheinanmeldungBis\" "
                    + "from \"tblBdStudentXLeistung\" " + 
                    "\"lngSdSeminarID\"=" + seminar.getSeminarID() + " and " +
                    "\"strMatrikelnummer\"='" + Long.parseLong(student.getMatrikelnummer()) + "' and " +  
                    "\"lngLeistungsID\"=" + lLeistungID + " and " + 
                    "\"blnStudentLeistungKlausuranmeldung\"=true and " + 
                    "\"lngStudentLeistungCount\"=" + lLeistungCount);
            if(!rTmp.next()) throw new Exception("Kein Fristende, Leistung nicht gefunden.");
            fristende = shjCore.g_ISO_DATE_FORMAT.parse(rTmp.getString(1));

        }catch(Exception eFristNichtLesbar){
            // Den Fehler, dass kein Fristende angegeben ist, ignorieren wir:
            // Falls die Anmeldung vom Config-Konto nachträglich 
            // erstellt wird, kann das durchaus vorkommen
            // In diesem Fall kann die Anemldung nicht 
            // von Studierenden gelöscht werden.
            
            fristende = shjCore.g_ISO_DATE_FORMAT.parse("2000-1-1");
        }
        if(fristende.before(new Date())){
            throw new Exception("{\"error\":\"Die Anmeldung kann nicht gelöscht werden, sorry. Die Frist zur Abmeldung ist am '" + shjCore.g_GERMAN_DATE_FORMAT.format(fristende) + "' abgelaufen.\",\"errorDebug\":\"Anmeldefrist schlicht abgelaufen -- bitte mit höherer Berechtigung löschen (und nicht als Studierende(r)).\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":100}");
        }
        
    }
    if(!seminar.sqlExe("delete from \"tblBdStudentXLeistung\" where " +
			"\"lngSdSeminarID\"=" + seminar.getSeminarID() + " and " +
			"\"strMatrikelnummer\"='" + Long.parseLong(student.getMatrikelnummer()) + "' and " +  
			"\"lngLeistungsID\"=" + lLeistungID + " and " + 
                        "\"blnStudentLeistungKlausuranmeldung\"=true and " + 
                        "\"lngStudentLeistungCount\"=" + lLeistungCount)){
		throw new Exception("{\"error\":\"Die Anmeldung konnte nicht gelöscht werden.\",\"errorDebug\":\"Die Datenbank hat sich verweigert -- Näheres vielleicht in deren Logs?\",\"errorcode\":" + lERR_BASE + 1 + ",\"severity\":50}");
	}else{
	%>{"success":"true"}<%}%>