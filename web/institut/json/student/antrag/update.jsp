<%--
    JSP-Object "student/antrag/update"

    SYNOPSIS (German)
    ===========================
    2013, Mai 29, shj       erzeugt. 
    2013, Dez 26, shj       überarbeitet.
    
    Ändert den Status eines Antrags, z.B. auf 'bearbeitet'.
    Achtung: @TODO -- derzeit ist das die einzige mögliche 
    Änderung eines Status. Der Status 'bearbeitet' ist zudem 
    FEST VERDRAHTET ALS antrag_status_abschluss=100 IM 
    QUELLTEXT (siehe fragments/conf_general).

    Expected SESSION PROPERTIES
    ===========================
    Angemeldeter Benutzer mit Berechtigung > 500

    Expected PARAMETER(S):
    ===========================
    antrag_status_abschluss [!=null]  ist Pflicht -- ansonsten tut update einfach gar nichts.
    matrikelnummer  [long]      Pflicht  
    antrag_id       [long]      id des Antrags
    antrag_status_abschluss != null -> Antrag wird als abgeschlossen markiert
    
    Returns
    =======
    Object:
    {"success":"true"}

    Sample Usage
    ============
    
    
--%><%@ page contentType="text/json" pageEncoding="UTF-8" session="true" isThreadSafe="false" errorPage="../../error.jsp"%>
<jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" />
<%@include file="../../../fragments/checkVersion.jsp" %>
<%@include file="../../../fragments/checkLogin.jsp" %>
<%@include file="../../../fragments/checkAccess500.jsp" %>
<%@include file="../../../fragments/conf_general.jsp" %>
<%  long lERR_BASE=106000 + 200;    // Antrag + Update
    if(request.getParameter("antrag_status_abschluss")!=null){
        
        long lAntragID = Long.parseLong(request.getParameter("antrag_id"));
        String sSQL = "INSERT INTO \"tblBdStudentAntragStatus\" (" + 
            "\"lngSdSeminarID\", \"strMatrikelnummer\", \"lngDozentID\", \"lngStudentAntragID\", " + 
            "\"lngStudentAntragStatusTypID\", \"strStudentAntragStatusBezeichnung\", " + 
            "\"strStudentAntragStatusBeschreibung\", \"dtmStudentAntragStatusDatum\", " + 
            "\"blnStudentAntragStatusAbschluss\")" + 
            "VALUES (" + user.getSdSeminarID() + ", '" + Long.parseLong(request.getParameter("matrikelnummer")) + "', " + user.getDozentID() + ", " + lAntragID + ", " + 
            lANTRAG_STATUS_TYP_ABGESCHLOSSEN + ", 'Antrag abgeschlossen', 'Antrag abgeschlossen', " +
            "CURRENT_DATE, true);";
        
        if(!user.sqlExe(sSQL))
            throw new Exception("{\"error\":\"Der Antrag konnte nicht abgeschlossen werden, die Datenbank hat sich verweigert.\",\"errorDebug\":\"Weitere Info nur über die Logdateien der Datenbank.\",\"errorcode\":" + lERR_BASE + 1 + ",\"severity\":100}");
    }

%>{"success":"true"}
        