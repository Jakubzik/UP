<%--
    JSP-Object "student/leistung/delete"
    
    @TODO
    ===========================
    - unter welchen Bedingungen darf
    die Leistung gelöscht werden?
        + WER darf das?
        + WANN muss zuerst eine Prüfung gelöscht werden?

    SYNOPSIS (German)
    ===========================
    2012, Nov 20, shj
    Erzeugt. 
    
    Üblicher Lifecycle: DELETE

    Löscht die durch matrikelnummer, id und count 
    eindeutig gekennzeichnete Leistung endgültig 
    und ohne audit trail aus der Datenbank.

    (Je nach Frontend wird allerdings ggf. eine 
    Bemerkung gespeichert).
    
    Expected SESSION PROPERTIES
    ===========================
    user        muss initialisiert sein (AccessLevel 500 oder höher),
    student     muss identische Matrikelnummer haben, die auch
                als Parameter übergeben wird.

    Expected PARAMETER(S):
    ===========================
    matrikelnummer  [long]
    id              [long]  } Spezifiziert die Leistung, die gelöscht
    count           [long]  } werden soll
    signup-expected-backend-version [text], vgl. fragments/checkVersion.jsp
    
    Returns
    =======
    Object:
    {"success":"true"}

    
--%><%@ page contentType="text/json" pageEncoding="UTF-8" import="de.shj.UP.data.shjCore" session="true" isThreadSafe="false" errorPage="../../error.jsp"%>
<jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean id="seminar" scope="session" class="de.shj.UP.HTML.HtmlSeminar" />
<jsp:useBean id="student" scope="session" class="de.shj.UP.beans.config.student.StudentBean" />
<%@include file="../../../fragments/checkVersion.jsp" %>
<%@include file="../../../fragments/checkLogin.jsp" %>
<%@include file="../../../fragments/checkAccess500.jsp" %>
<%@include file="../../../fragments/checkInitStudent.jsp" %>
<%long lERR_BASE=101000 + 300;    // Leistung + Delete
// ==============================================================
// Schnittstelle
// ==============================================================
long lLeistungID = -1;
long lLeistungCount=-1;
try{
    lLeistungID=Long.parseLong(request.getParameter("id"));
    lLeistungCount=Long.parseLong(request.getParameter("count"));
}catch(Exception eNotHandedOver){throw new Exception("{\"error\":\"Die Leistung bzw. Anmeldung kann nicht gelöscht werden.\",\"errorDebug\":\"Der Parameter >id< (" + request.getParameter("id") + ") oder >count< (" + request.getParameter("count") + ") wurde nicht korrekt übergeben.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":100}");}

if(!user.sqlExe("delete from \"tblBdStudentXLeistung\" where " +
                    "\"lngSdSeminarID\"=" + user.getSdSeminarID() + " and " +
                    "\"strMatrikelnummer\"='" + Long.parseLong(student.getMatrikelnummer()) + "' and " +  
                    "\"lngLeistungsID\"=" + lLeistungID + " and " + 
                    "\"lngStudentLeistungCount\"=" + lLeistungCount)){
            throw new Exception("{\"error\":\"Die Leistung konnte nicht gelöscht werden. (Ist die Leistung möglicherweise Teil einer Prüfung? Dann muss zuerst die Prüfung gelöscht werden!)\",\"errorDebug\":\"Die Datenbank hat sich verweigert -- Näheres vielleicht in deren Logs?\",\"errorcode\":" + lERR_BASE + 1 + ",\"severity\":50}");
}else{
%>{"success":"true"}<%}%>
