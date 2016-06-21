<%--
    JSP-Object "student/leistung/addGeneric.jsp"

    SYNOPSIS (German)
    ===========================
    2012, Nov 23, shj    erzeugt. 
    2013, Dez 28, shj    überarbeitet
    2016, Jun 17, shj    
    Üblicher Lifecycle: ADD

    Speichert die Leistung, ohne Angabe eines Kurses. AddGeneric.jsp 
    wird von ./add.jsp aufgerufen, falls dort kein 
    Parameter "kurs_id" übergeben wird.
    
    Expected SESSION PROPERTIES
    ===========================
    user        muss initialisiert sein und braucht einen AccessLevel >= 500
                wenn er/sie Leistungen eines/r anderen Lehrenden speichern 
                möchte.
                (Eigene Leistungen kann man mit einem AccessLevel >= 1 
                speichern).

    Expected PARAMETER(S):
    ===========================
    matrikelnummer  [long]
    id              [long] Id der Leistung (vgl. "tblSdLeistung.lngLeistungID")

    noten_id        [int]  Id der Note der Leistung (vgl. tblSdNoten.intNoteID)
    datum           [text] interpretierbar als deutsches Datum (dd.MM.yyyy)

    ects            [text] interpretierbar als float
    leistungstyp    [text] (Hausarbeit, Klausur, nix)
    details         [text] ggf. Titel der Hausarbeit
    bemerkung       [text] Weitere Bemerkung (z.B. "anerkannt von ursprünglich Leistung xyz").
    modul_id        [text] numerisch oder 'null' (optional)
    
    [anerkannt       [t|f] optional]
    
    signup-expected-backend-version [text], vgl. fragments/checkVersion.jsp
    
    Returns
    =======
    {
        "success": "true",
        "leistung": {
            "id": "30080",
            "count": "0",
            "kurstitel": "Ping",
            "kurstitel_en": "Pingy",
            "noten_id": "7",
            "aussteller_nachname": "Jakubzik",
            "aussteller_vorname": "Heiko"
        }
    }

    Fehler/Errors/THROWS
    ====================

    Beispiel
    =========
  
        {"id":"15",
          "signup-expected-backend-version":"1-0-0-2",
          "modul_id":"100105",
          "aussteller_id":"26",
          "matrikelnummer":"2862565",
          "ects":"6",
          "noten_id":"4",
          "datum":"3.12.2012",
          ["anerkannt":"t"]
        }
    
--%> <%@page import="java.sql.ResultSet"%>
<%@ page contentType="text/json" pageEncoding="UTF-8" import="de.shj.UP.data.StudentXLeistung,de.shj.UP.data.Dozent,de.shj.UP.data.shjCore" session="true" isThreadSafe="false" errorPage="../../error.jsp"%><jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean id="seminar" scope="session" class="de.shj.UP.logic.SeminarData" /><jsp:useBean id="student" scope="session" class="de.shj.UP.beans.config.student.StudentBean" /><jsp:useBean
	id="sd" scope="session" class="de.shj.UP.util.SessionData" />
<%@include file="../../../fragments/checkVersion.jsp" %>
<%@include file="../../../fragments/checkLogin.jsp" %>
<%@include file="../../../fragments/checkAccess1.jsp" %>
<%@include file="../../../fragments/checkInitStudent.jsp" %>
<%  long lERR_BASE=101000 + 400;    // Leistung + Add

    // ==============================================================
    // Schnittstelle
    // ==============================================================
    long lLeistungID = -1;
    try{
        lLeistungID=Long.parseLong(request.getParameter("id"));
    }catch(Exception eNotHandedOver){throw new Exception("{\"error\":\"Die Leistung bzw. Anmeldung kann nicht hinzugefügt werden.\",\"errorDebug\":\"Der Parameter >id< (" + request.getParameter("id") + ") wurde nicht korrekt übergeben.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":100}");}

    try{        
        int i=(int)Integer.parseInt(request.getParameter("noten_id"));
        if(i<=0) throw new Exception("{\"error\":\"Es muss eine Note angegeben werden!\",\"errorDebug\":\"Es wurde keine Note für diese Leistung übergeben\",\"errorcode\":" + lERR_BASE + 5 + ",\"severity\":40}");            
    }catch(Exception eNoteMissing){
         throw new Exception("{\"error\":\"Es muss eine Note angegeben werden!\",\"errorDebug\":\"Der relevante Parameter >noten_id< (" + request.getParameter("noten_id") + ") ist nicht numerisch.\",\"errorcode\":" + lERR_BASE + 5 + ",\"severity\":40}");    
    }
    
    try{new java.sql.Date(shjCore.g_GERMAN_DATE_FORMAT.parse(request.getParameter("datum")).getTime());}catch(Exception eNoteMissing){
         throw new Exception("{\"error\":\"Es muss ein Ausstellungsdatum angegeben werden!\",\"errorDebug\":\"Der relevante Parameter >datum< (" + request.getParameter("datum") + ") scheint kein deutsches Datum zu sein.\",\"errorcode\":" + lERR_BASE + 5 + ",\"severity\":40}");    
    }
    long lLeistungCount=0;
    try{
        ResultSet rst = user.sqlQuery("select max(\"lngStudentLeistungCount\")+1 "
                + "from \"tblBdStudentXLeistung\" "
                + "where "
                + "\"lngSdSeminarID\"=" +  user.getSdSeminarID() + 
                    " and \"strMatrikelnummer\"='" + Long.parseLong(student.getMatrikelnummer()) + 
                    "' and \"lngLeistungsID\"=" + lLeistungID + ";");
        rst.next();
        lLeistungCount=rst.getLong(1);
    }catch(Exception eNotNumeric2){
        // Ignore, das ist dann die erste Leistung diesen Typs
    }
   
    // Ab hier ist klar: es handelt sich um die 
    // Umwandlung einer Anmeldung zu einer Leistung
    // Es werden zumindest Parameter zu
    // Note, Datum, Leistungspunkten gebraucht, 
    // möglichst auch zum Modul
    StudentXLeistung sl = new StudentXLeistung(student.getSeminarID(), student.getMatrikelnummer(), lLeistungID, lLeistungCount);
    if(!shjCore.normalize(request.getParameter("leistungstyp")).equals("")) sl.setStudentLeistungCustom3(request.getParameter("leistungstyp"));
    if(!shjCore.normalize(request.getParameter("details")).equals("")) sl.setStudentLeistungDetails(request.getParameter("details"));
    if(!shjCore.normalize(request.getParameter("bemerkung")).equals("")) sl.setStudentLeistungBemerkung(request.getParameter("bemerkung"));
    if(!shjCore.normalize(request.getParameter("kurstitel")).equals("")) sl.setSLKursTitel(request.getParameter("kurstitel"));
    if(!shjCore.normalize(request.getParameter("kurstitel_en")).equals("")) sl.setSLKursTitel_en(request.getParameter("kurstitel_en"));
    
    if(!shjCore.normalize(request.getParameter("anerkannt")).equals("")) if(request.getParameter("anerkannt").equals("t")) sl.setStudentLeistungAnerkannt(true);
    
    // Hausarbeit/Klausur:
    // ENDE Hausarbeit/Klausur
    long lDozentID=-1;
    try{
        lDozentID=Long.parseLong(request.getParameter("aussteller_id"));
    }catch(Exception eAusstellerMissing){
        throw new Exception("{\"error\":\"Die Leistung kann ohne Angabe eines Ausstellers (oder Kurses) nicht gespeichert werden.\",\"errorDebug\":\"Es soll eine generische Leistung gespeichert werden, es wurde aber kein Aussteller angegeben (zumindest ist der Parameter >aussteller_id< (" + request.getParameter("aussteller_id") + ") nicht numerisch).\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":100}");
    }
    try{
        sl.setStudentLeistungCreditPts(Float.parseFloat(request.getParameter("ects")));
    }catch(Exception eNoCreditPts){
        throw new Exception("{\"error\":\"Die Leistung kann ohne Angabe von Leistungspunkten nicht gespeichert werden.\",\"errorDebug\":\"Es soll eine generische Leistung gespeichert werden, es wurden aber keine Leistungspunkte angegeben (zumindest ist der Parameter >ects< (" + request.getParameter("ects") + ") nicht numerisch).\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":100}");
    }
    // Entweder muss der Aussteller selbst eingeloggt sein,
    // oder er muss einen AccessLevel von mindestens 500 
    // haben. Sonst kann die Leistung nicht ausgestellt werden.
    if(user.getDozentID()!=lDozentID && user.getDozentAccessLevel()<500){
            throw new Exception("{\"error\":\"Die Leistung konnte nicht gespeichert werden.\",\"errorDebug\":\"Um Leistungen anderer Lehrender zu speichern ist eine höhere Berechtigung notwendig.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":80}");
    }
    
    Dozent dozent = new Dozent(user.getSdSeminarID(), Long.parseLong(request.getParameter("aussteller_id")));
    sl.setStudentLeistungAussteller(dozent.getDozentNachname());
    sl.setStudentLeistungAusstellerVorname(dozent.getDozentVorname());
    sl.setStudentLeistungAusstellerTitel(dozent.getDozentTitel());
    sl.setDozentID(dozent.getDozentID());
    // try/catch und Schnittstellentests
    // sind hier eigentlich überflüssig,
    // da oben schon durchgeführt (um 
    // zu verhindern, dass schon eine Anmeldung
    // erzeugt wird, wenn kein Schein ausgestellt
    // werden kann).
    try{
        sl.setNoteID(Integer.parseInt(request.getParameter("noten_id")));
    }catch(Exception eNoteMissing){
         throw new Exception("{\"error\":\"Es muss eine Note angegeben werden!\",\"errorDebug\":\"Der relevante Parameter >noten_id< (" + request.getParameter("noten_id") + ") ist nicht numerisch.\",\"errorcode\":" + lERR_BASE + 5 + ",\"severity\":40}");    
    }
    // @TODO: Leistungspunkte, Datum, Modul überprüfen
    if(sl.getNoteID()<=0) throw new Exception("{\"error\":\"Es muss eine Note angegeben werden!\",\"errorDebug\":\"Es wurde keine Note für diese Leistung übergeben\",\"errorcode\":" + lERR_BASE + 5 + ",\"severity\":40}");    
    
    try{
        sl.setStudentLeistungAusstellungsdatum(new java.sql.Date(shjCore.g_GERMAN_DATE_FORMAT.parse(request.getParameter("datum")).getTime()));
    }catch(Exception eNoteMissing){
         throw new Exception("{\"error\":\"Es muss ein Ausstellungsdatum angegeben werden!\",\"errorDebug\":\"Der relevante Parameter >datum< (" + request.getParameter("datum") + ") scheint kein deutsches Datum zu sein.\",\"errorcode\":" + lERR_BASE + 5 + ",\"severity\":40}");    
    }
    
    boolean bZusatzmodul=false;
    try{
        sl.setModulID(Long.parseLong(request.getParameter("modul_id")));
    }catch(Exception nullModul){
        bZusatzmodul=true;
    }

    sl.setStudentLeistungKlausuranmeldung(false);
    sl.setStudentLeistungGesiegelt(true);
    sl.setStudentLeistungValidiert(true);
    
    if(!sl.add()) throw new Exception("{\"error\":\"Die Leistung kann nicht hinzugefügt werden.\",\"errorDebug\":\"Die Datenbank verweigert das Speichern, es hilft wohl nur der Blick in deren Logfiles\",\"errorcode\":" + lERR_BASE + 1 + ",\"severity\":50}");
    if(bZusatzmodul) if(!sl.setModulToNull()) throw new Exception("{\"error\":\"Die Leistung wurde gespeichert, aber die Modulzuordnung kann nicht justiert werden.\",\"errorDebug\":\"Die Datenbank verweigert das Speichern dieser Leistung mit einem Nullwert im Modul (allerdings war der in >modul_id< übergebene Wert (" + request.getParameter("modul_id") + ") nicht numerisch).\",\"errorcode\":" + lERR_BASE + 1 + ",\"severity\":50}");
	%><%if(request.getParameter("shjSignUp_suppressResponse")==null){%>{"success":"true",
        "leistung":{"id":"<%=sl.getLeistungsID() %>","count":"<%=sl.getStudentLeistungCount() %>","kurstitel":"<%=shjCore.string2JSON(sl.getSLKursTitel()) %>","kurstitel_en":"<%=shjCore.string2JSON(sl.getSLKursTitel_en()) %>",
        "noten_id":"<%=sl.getNoteID() %>","aussteller_nachname":"<%=sl.getStudentLeistungAussteller() %>","aussteller_vorname":"<%=sl.getStudentLeistungAusstellerVorname() %>"}}<%}%>
        

