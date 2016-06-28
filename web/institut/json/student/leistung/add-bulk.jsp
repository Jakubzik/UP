<%--
    JSP-Object "seminar/leistung/add-bulk"
    @Revision: Jul 6, 2015 -- hj

    @TODO
    ===========================


    SYNOPSIS (German)
    ===========================
    2015, Jul 6, shj
    Erzeugt. 
    
    Üblicher Lifecycle: ADD

    Backend zum Hochladen von Excel-Dokumenten: verbucht 
    einzelne Leistungen, erzeugt ggf. (per StoredProc) 
    eine entsprechende Anmeldung.

    Nach ausführlicher Durchsicht aller anderen Möglichkeiten 
    zur Erzeugung dieser Leistungen mit vorhandenem Code 
    habe ich schweren Herzens entschieden, das doch einzeln 
    als Sonderfall auszuprogrammieren.

    (Problem sind letztlich die Kurse, die z.T. aus dem Archiv 
     kommen können, und deren Daten zuerst zum Frontend transportiert 
     werden müssten, um sie dann wiederum als Parameter an die 
     vorhandenen Funktionen zu reichen. Das kostet Bandbreite und 
     Performance, und vor allem müssten die Kurs/get.jsp Funktionen 
     erheblich erweitert werden, um mit der KursID als Parameter
     Kurse aus dem Archiv oder aktuelle Kurse zu liefern; 
     Dann wäre immer noch nicht überprüft, ob so eine Leistung 
     zu diesem Kurs vielleicht bereits ausgestellt ist etc. etc.)
    
    Expected SESSION PROPERTIES @TODO
    ===========================
    user        

    Expected PARAMETER(S):  @TODO
    ===========================
    matrikelnummer [String]
    kurs_id      [long]
    leistung_id  [long]
    noten_id     [int]
    datum        [dd.MM.jjjj] (Deutsches Ausstellungsdatum)
    note_bestanden OPTIONAL: wenn 'false', werden die LP auf 0 gesetzt!
    [dozent_id]  [long | *] -- nur falls dozent.getAccessLevel >= 500, sonst Fehler
    
    Returns  {\"success\":\"true\"}, oder {\"error\" über errorpge}
    =======

    
--%><%@page import="java.sql.Date"%>
<%@page import="de.shj.UP.data.StudentXLeistung"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page contentType="text/json" pageEncoding="UTF-8" import="java.sql.ResultSet,de.shj.UP.data.Dozent,de.shj.UP.data.shjCore" session="true" isThreadSafe="false"  errorPage="../../error.jsp" %>
<jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" />
<jsp:useBean id="sd" scope="session" class="de.shj.UP.util.SessionData" />
<%@include file="../../../fragments/checkVersion.jsp" %>
<%@include file="../../../fragments/checkLogin.jsp" %>    
<%@include file="../../../fragments/checkAccess1.jsp" %>
<%! long lERR_BASE; %>
<%lERR_BASE=207000 + 400;    // Anmeldung + Add%>

<%

// Schnittstelle
// =============================================================================
long lKursID=-1;
try{
    lKursID=Long.parseLong(request.getParameter("kurs_id"));
}catch(Exception eNoKursID){
    throw new Exception("{\"error\":\"Es muss ein Kurs angegeben werden!\"," 
            + "\"errorDebug\":\"Es wurde kein Kurs für diese Leistung "
            + "übergeben. Der Parameter >kurs_id< hat den nicht-numerischen" 
            + " Wert >" + request.getParameter("kurs_id") 
            + "<.\",\"errorcode\":" + lERR_BASE + 5 + ",\"severity\":40}");
}

long lLeistungID=-1;
try{
    lLeistungID=Long.parseLong(request.getParameter("leistung_id"));
}catch(Exception eNoKursID){
    throw new Exception("{\"error\":\"Es muss eine Leistung angegeben werden!\"," 
            + "\"errorDebug\":\"Es wurde kein Kurs für diese Leistung "
            + "übergeben. Der Parameter >leistung_id< hat den nicht-numerischen" 
            + " Wert >" + request.getParameter("leistung_id") 
            + "<.\",\"errorcode\":" + lERR_BASE + 5 + ",\"severity\":40}");
}

int iNotenID=-100;
try{
    iNotenID=Integer.parseInt(request.getParameter("noten_id"));
}catch(Exception eNoNotenID){
    throw new Exception("{\"error\":\"Es muss eine Note angegeben werden!\"," 
            + "\"errorDebug\":\"Es wurde keine Note für diese Leistung "
            + "übergeben. Der Parameter >noten_id< hat den nicht-numerischen" 
            + " Wert >" + request.getParameter("noten_id") 
            + "<.\",\"errorcode\":" + lERR_BASE + 5 + ",\"severity\":40}");
}


String sMatrikelnummer=request.getParameter("matrikelnummer");
try{
    Long.parseLong(sMatrikelnummer);
}catch(Exception eMatrikelnummer){
    throw new Exception("{\"error\":\"Es wurde keine Matrikelnummer übermittelt\"," 
            + "\"errorDebug\":\"Es wurde keine Matrikelnr für diese Leistung "
            + "übergeben. Der Parameter >matrikelnummer< hat den nicht-numerischen" 
            + " Wert >" + sMatrikelnummer
            + "<.\",\"errorcode\":" + lERR_BASE + 5 + ",\"severity\":40}");
}

Date dtm=null;
try{
    dtm=new java.sql.Date(shjCore.g_GERMAN_DATE_FORMAT.parse(request.getParameter("datum")).getTime());
}catch(Exception eNoteMissing){
     throw new Exception("{\"error\":\"Es muss ein Ausstellungsdatum angegeben werden!\",\"errorDebug\":\"Der relevante Parameter >datum< (" + request.getParameter("datum") + ") scheint kein deutsches Datum zu sein.\",\"errorcode\":" + lERR_BASE + 5 + ",\"severity\":40}");    
}

boolean bNoteBestanden=true;
if(request.getParameter("note_bestanden")!=null && request.getParameter("note_bestanden").equals("false")) bNoteBestanden=false;

// =============================================================================
// Hier wird die Anmeldung erzeugt, falls sie noch nicht vorliegt;
// das Erzeugen der Anmeldung weist das erstbeste Modul zu und 
// sucht in ModulXLeistung nach den Leistungspunkten.
// Falls kein Modul gefunden wird, ist es null, und die Leistungs-
// punkte werden aus "tblSdLeistung" übernommen.
try{
    user.execProc("forcecommitment", 
            "int", String.valueOf(user.getSdSeminarID()), 
            "int", String.valueOf(user.getDozentID()), 
            "int", String.valueOf(lLeistungID),
            "int", String.valueOf(lKursID),
            "String", sMatrikelnummer, 
            "boolean", String.valueOf(user.getDozentAccessLevel()>=500));
}catch(Exception eSPFail){
    throw new Exception("{\"error\":\"Die Anmeldung Matnr. " + sMatrikelnummer + " konnte nicht hinzugefügt werden."
            + " Der Server meldet:>>" + eSPFail.getMessage() + "<<.\",\"errorDebug\":\"StoredProcedure forceCommitment schlug fehl.\",\"errorcode\":" + lERR_BASE + 5 + ",\"severity\":40}");    
}

// Jetzt müsste die Anmeldung vorliegen:
ResultSet rst = user.sqlQuery("select * from \"tblBdStudentXLeistung\" Where " + 
                "\"strMatrikelnummer\"='" + sMatrikelnummer + "' and " + 
                "\"lngLeistungsID\"=" + lLeistungID + " and " +
                "\"lngSdSeminarID\"=" + user.getSdSeminarID() + " and " +
                "\"blnStudentLeistungKlausuranmeldung\"=true and " + 
                "\"lngKlausuranmeldungKursID\"=" +  lKursID  + ";");
if(! rst.next() ){
    rst.close();
    throw new Exception("{\"error\":\"Anmeldung konnte nicht hinzugefügt werden.\"," 
            + "\"errorDebug\":\"Die Anmeldung liegt trotz Aufruf StoredProc "
            + "nicht vor -- vielleicht mehr Info in den DB-Server Logs?" 
            + "\",\"errorcode\":" + lERR_BASE + 5 + ",\"severity\":40}");
}

// Hier liegt sicher die Anmeldung vor.
// Erzeuge data.StudentXLeistung, um mit 
// dessen Funktionalität die benachrichtigende 
// E-Mail an Studierende schicken zu können.
StudentXLeistung sl=new StudentXLeistung();
sl.initByRst(rst);
sl.setStudentLeistungAusstellungsdatum(dtm);
sl.setNoteID( iNotenID );
sl.setStudentLeistungKlausuranmeldung( false );
sl.setStudentLeistungGesiegelt( true );
sl.setStudentLeistungValidiert( true );
if(!bNoteBestanden) sl.setStudentLeistungCreditPts(0);

if(sl.update()){
//        sl.tellStudentAboutNewCredit();
        out.write("{\"success\":\"true\"}");
}else{
    throw new Exception("{\"error\":\"Datenbank konnte die Leistung nicht speichern\"," 
        + "\"errorDebug\":\"Siehe DB-Log. Der Datensatz konnte nicht gespeichert werden.\","
            + "\"errorcode\":" + lERR_BASE + 5 + ",\"severity\":40}");
}
try{
        sl.close();
        rst.close();
}catch(Exception eWasNeverOpen){}

%>
