<%--
    JSP-Object "student/signup/delete"

    Löscht die Anmeldungen zur übergebenen KurstypID.

!   Achtung: schert sich nicht um "Fixiert" oder "Zuschlag" 
             oder "swap"

    @TODO:
    ===========================
    Müsste das Löschen zumindest bei 'fixierten' 
    Kursen oder Kursen mit "Zuschlag" durch 
    Übergabe eines bestätigenden Parameters 
    abgesichert werden?

    SYNOPSIS (German)
    ===========================
    2013, June 11, shj
    2014, Feb 3, shj      Routine Durchsicht
    
    Üblicher Lifecycle: DELETE

    Expected SESSION PROPERTIES
    ===========================
    seminar initialisiert,
    student initialisiert

    Expected PARAMETER(S):
    ===========================
    kurstyp_id [long]
    Expected Backend Version
    
    Returns
    =======
    {"success":"true"} oder Fehler.

--%>

<%@page contentType="text/json" pageEncoding="UTF-8" import="java.sql.ResultSet,de.shj.UP.logic.AktuellesIterator,java.text.SimpleDateFormat, de.shj.UP.data.Dozent,de.shj.UP.data.shjCore" session="true" isThreadSafe="false"  errorPage="../../../error.jsp" %>
<jsp:useBean id="seminar" scope="session" class="de.shj.UP.logic.SeminarData" />
<jsp:useBean id="student" scope="session" class="de.shj.UP.beans.config.student.StudentBean" />
<%@include file="../../../fragments/checkVersion.jsp" %>
<%@include file="../../../fragments/checkLoginStudent.jsp" %>
<%
        long lERR_BASE=108000 + 300;    // Anmeldung + Delete
        long lKurstypID=-1;
        try{
            lKurstypID=Long.parseLong(request.getParameter("kurstyp_id"));
        }catch(Exception eNoKurstypID){
            throw new Exception("{\"error\":\"Die Kurswahl konnte nicht gelöscht werden.\",\"errorDebug\":\"Der erforderliche Parameter >kurstyp_id< hatte den Wert '" + request.getParameter ("kurstyp_id") + "', muss aber numerisch sein.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":100}");
        }
        if(!seminar.Data().isSignUpPeriod()) throw new Exception("{\"error\":\"Zur Zeit findet keine Kurswahl statt.\",\"errorDebug\":\"Der Zeitraum der Kurswahl schließt das heutige Datum nicht ein (see tblData).\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":100}");
        if(!student.sqlExe("delete from \"tblBdAnmeldung\" where " +
            "\"lngSdSeminarID\"=" + seminar.getSeminarID() + " and " +
            "\"strMatrikelnummer\"='" + student.getMatrikelnummer() + "' and " +
            "\"lngKurstypID\"=" + lKurstypID + ";"))
            throw new Exception("{\"error\":\"Die Kurswahl konnte nicht gelöscht werden.\",\"errorDebug\":\"Die Datenbank hat sich verweigert -- mehr Informationen in den Logfiles dort.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":100}");
        
%>{"success":"true"}
