<%--
    JSP-Object "seminar/kurs/termin/delete.jsp"
    @Revision: Oct 7, 2014 -- hj

    @TODO
    ===========================
    - 


    SYNOPSIS (German)
    ===========================
    2014, Oct 7, shj
    
    Terminserie bzw. Raumbuchungen löschen

    Bsp: "Terminserie" für Mo 11.15-12.45 werden 
    z.B. die Einzeltermine 
    1) 15.10.2014, 11.15-12.45,
    2) 21.10.2014, 11.15-12.45,
    3) 28.10.2014, 11.15-12.45,
    ...
    15) 1.2.2015, 11.15-12.45,
    16) 8.2.2015, 11.15-12.45
    
    
    Expected SESSION PROPERTIES
    ===========================
    user        muss initialisiert sein 

    Expected PARAMETER(S):
    ===========================

    kurs_id:        Id des Kurses
    termin_prio:    Erstwunsch (1) oder zweit (2)?
!   only_unbook:    null/nicht null
                    Falls der Parameter gesetzt ist, wird nur 
                    der Zuschlag zum Raum entfernt, der Wunsch 
                    bzw. die Vormerkung bleibt bestehen.

    datum_<i>:      Datum des zu speichernden Termins, ISO-Format
    raum_<i>:       Raum
    start_<i>:      (H)H:MM der Start- und
    stop_<i>:       Stopptermine
                    (z.B.: start_0=9.00, stop_0=9.10, start_1=9:10 ...)


    Returns
    =======

    Sample Usage
    ============
 
    
--%><%@page import="java.sql.Date"%>
<%@page import="de.shj.UP.data.shjCore"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page contentType="text/json" pageEncoding="UTF-8" import="java.sql.ResultSet,de.shj.UP.data.Dozent,de.shj.UP.util.PasswordHash" session="true" isThreadSafe="false"  errorPage="../../../error.jsp" %>
<jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean id="seminar" scope="session" class="de.shj.UP.HTML.HtmlSeminar" />
<%@include file="../../../../fragments/checkVersion.jsp" %>
<%@include file="../../../../fragments/checkLogin.jsp" %>
<%@include file="../../../../fragments/checkAccess1.jsp" %>
<%long lERR_BASE=215000 + 300;    // Kurstermin + Delete
// =========== =========== =========== =========== =========== ===========
// Schnittstelle
// =========== =========== =========== =========== =========== ===========
int iKursID=-1;
try{
    iKursID=Integer.parseInt(request.getParameter("kurs_id").trim());
}catch(Exception eNotNumeric){
    throw new Exception("{\"error\":\"Die Termine zu diesem Kurs können nicht gespeichert werden. Es wurde keine Id des Kurses übergeben.\"" + 
                ",\"errorDebug\":\"Der Parameter >kurs_id< hat den nicht-numerischen Wert " + request.getParameter("kurs_id") + ".\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":100}");               
}

int iTerminPrioritaet=100;
boolean bBookDirect=false;
try{
    iTerminPrioritaet=Integer.parseInt(request.getParameter("termin_prio").trim());
}catch(Exception eNotNumeric){
    throw new Exception("{\"error\":\"Die Termine zu diesem Kurs können nicht gelöscht werden. Es wurde keine Priorität übergeben.\"" + 
                ",\"errorDebug\":\"Der Parameter >termin_prio< (für die Priorisierung dieses Terminvorschlags) hat den nicht-numerischen Wert " + request.getParameter("termin_prio") + ".\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":100}");               
}
long lRecordsAffected=-1;
if(request.getParameter("only_unbook")==null){
    // LÖSCHE die übergebenen Termine aus der Terminbuchungs-
    // tabelle
    for(int ii=0;true==true;ii++){
        if(request.getParameter("datum_" + ii)==null) break;
        PreparedStatement pstm = seminar.prepareStatement("delete from \"tblBdKursTermin\" "+
                "where \"lngSdSeminarID\"=? and \"lngKursID\"=? and  "+
                "\"intKursTerminPrioritaet\"=? and \"strRaumBezeichnung\"=? and " +
                "\"dtmKursTerminDatum\"=? and \"dtmKursTerminStart\"=? and \"dtmKursTerminStop\"=?;");
        pstm.setLong(1, user.getSdSeminarID());
        pstm.setLong(2, iKursID);
        pstm.setLong(3, iTerminPrioritaet);
        pstm.setString(4, request.getParameter("raum_" + ii));
        pstm.setDate(5, new java.sql.Date( shjCore.g_ISO_DATE_FORMAT.parse(request.getParameter("datum_" + ii)).getTime()));
        pstm.setTime(6, java.sql.Time.valueOf(request.getParameter("start_" + ii).replace('.', ':') + ":00"));
        pstm.setTime(7, java.sql.Time.valueOf(request.getParameter("stop_" + ii).replace('.', ':') + ":00"));
        System.out.println("Lösche Termin am " + new java.sql.Date( shjCore.g_ISO_DATE_FORMAT.parse(request.getParameter("datum_" + ii)).getTime()));
        lRecordsAffected=pstm.executeUpdate();
        if(lRecordsAffected!=1){
            throw new Exception("{\"error\":\"Die Terminbuchung konnte nicht gelöscht werden: es war nicht 1 Datensatz (sondern " + lRecordsAffected + ") betroffen. Gibt es vielleicht eine gleichzeitige Buchung?\"" + 
                    ",\"errorDebug\":\"Die Ursache ist unklar -- sagen die Logfiles der Datenbank etwas aus?.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":100}");               
        };
    }
}else{
    // Lösche NUR DEN ZUSCHLAG zu den übergebenen Terminen
    for(int ii=0;true==true;ii++){
        if(request.getParameter("datum_" + ii)==null) break;
        if(request.getParameter("datum_" + ii)==null) break;
        PreparedStatement pstm = seminar.prepareStatement("update \"tblBdKursTermin\" "+
                "set \"blnKursTerminZuschlag\"=false where \"lngSdSeminarID\"=? and \"lngKursID\"=? and  "+
                "\"intKursTerminPrioritaet\"=? and \"strRaumBezeichnung\"=? and " +
                "\"dtmKursTerminDatum\"=? and \"dtmKursTerminStart\"=? and \"dtmKursTerminStop\"=?;");
        pstm.setLong(1, user.getSdSeminarID());
        pstm.setLong(2, iKursID);
        pstm.setLong(3, iTerminPrioritaet);
        pstm.setString(4, request.getParameter("raum_" + ii));
        pstm.setDate(5, new java.sql.Date( shjCore.g_ISO_DATE_FORMAT.parse(request.getParameter("datum_" + ii)).getTime()));
        pstm.setTime(6, java.sql.Time.valueOf(request.getParameter("start_" + ii).replace('.', ':') + ":00"));
        pstm.setTime(7, java.sql.Time.valueOf(request.getParameter("stop_" + ii).replace('.', ':') + ":00"));
        lRecordsAffected=pstm.executeUpdate();
        System.out.println("Lösche Termin ZUSCHLAG am " + new java.sql.Date( shjCore.g_ISO_DATE_FORMAT.parse(request.getParameter("datum_" + ii)).getTime()));
        if(lRecordsAffected!=1){
            throw new Exception("{\"error\":\"Die Terminbuchung konnte nicht gelöscht werden: es war nicht 1 Datensatz (sondern " + lRecordsAffected + ") betroffen. Gibt es vielleicht eine gleichzeitige Buchung?\"" + 
                    ",\"errorDebug\":\"Die Ursache ist unklar -- sagen die Logfiles der Datenbank etwas aus?.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":100}");               
        };
    }
}
%>{"success":"true"}