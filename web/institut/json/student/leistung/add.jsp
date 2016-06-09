<%--
    JSP-Object "student/leistung/add"

    @TODO
    addGeneric.jsp

    - JSON Rückgabe-Objekt vervollständigen
    - was, wenn schon eine Anmeldung vorliegt? (Wird die automatisch umgewandelt?
    - was, wenn keine Doubletten erlaubt sind? (Warnung?)
    - Problem: doppelte Anmeldung und Leistung .add Antwort {}{}

    SYNOPSIS (German)
    ===========================
    2012, Nov 23, shj    erzeugt. 
    2013, Dez 28, shj    überarbeitet

    Üblicher Lifecycle: ADD

    Speichert die Leistung. Entweder speichert man eine 
    Leistung mit Kurs unter eigenem Namen (keine besondere 
    Berechtigung erforderlich), oder es ist eine Berechtigung 
    von mindestens 500 erforderlich.

    Abhängigkeiten
    ===============
    Delegiert u.U. an 
    - addGeneric.jsp (wenn keine kurs_id übergeben wird) oder an
    - ../anmeldung/add.jsp (wenn noch keine Anmeldung vorliegt).
    
    Expected SESSION PROPERTIES
    ===========================
    user        muss initialisiert sein
    student     muss identische Matrikelnummer haben, die auch
                als Parameter übergeben wird.

    Expected PARAMETER(S):
    ===========================
    matrikelnummer  [long]
    id              [long]      Id der Leistung (vgl. "tblSdLeistung.lngLeistungID")

    noten_id        [int]  Id der Note der Leistung (vgl. tblSdNoten.intNoteID)
    datum           [text] interpretierbar als deutsches Datum (dd.MM.yyyy)

    ects            [text] interpretierbar als float
    leistungstyp    [text] (Hausarbeit, Klausur, nix)
    details         [text] ggf. Titel der Hausarbeit
    bemerkung       [text] Weitere Bemerkung (z.B. "anerkannt von ursprünglich Leistung xyz").
    modul_id        [text] numerisch oder 'null' (optional)
    
    [anerkannt       [t|f] optional]

    kurs_id         [long],     optional        } übergeben, oder keiner davon; alles 
    archiv          [boolean],  optional        } andere erzeugt einen Fehler. Wird keine kurs_id
                                                  übergeben, erfolgt die weitere Verarbeitung 
                                                  in 'addGeneric.jsp'
    
    signup-expected-backend-version [text], vgl. fragments/checkVersion.jsp
    
    Returns
    =======
    {
    "success": "true",
    "leistung": {
        "id": "16",
        "count": "2",
        "kurstitel": "Spaces of Decivilization: Norbert Elias's Cultural Theory and American Literature",
        "kurstitel_en": "Spaces of Decivilization: Norbert Elias's Cultural Theory and American Literature",
        "noten_id": "7",
        "aussteller_nachname": "Schloss",
        "aussteller_vorname": "Dietmar"
    }
}

######################################################################################

    Beispiel-INPUT:
    ===============
    Hinzufügen einer Leistung aus einem Kurs, ohne 
    dass eine vorherige Anmeldung vorläge

    Schalter-Parameter:
    archiv: kurs_id/kurstyp_id wird aus einem _archivierten_ Semester geholt
    
    a) minimal:
        {"id":"15",
          "signup-expected-backend-version":"1-0-0-2",
          "modul_id":"100105",
          "kurs_id":"14331",
          "txtDozentID":"26",
          "kurstyp_id":"15",
          "matrikelnummer":"2862565",
          "ects":"6",
          "noten_id":"4",
          "datum":"3.12.2012",
          [["anerkannt":"t"]],
        }

    b) mit Hausarbeitsthema und Bemerkung:
        {
          "id":"15",
          "signup-expected-backend-version":"1-0-0-2",
          "txtStudentLeistungDetails":"Kein Thema",
          "modul_id":"100105",
          "kurstyp_id":"15",
          "kurs_id":"14331",
          "aussteller_id":"26",
          "matrikelnummer":"2862565",
          "ects":"6",
          "noten_id":"4",
          "datum":"3.12.2012",
          "bemerkung":"Test",
          "details":"HA",
        }
    
--%> <%@ page contentType="text/json" pageEncoding="UTF-8" import="de.shj.UP.data.StudentXLeistung,de.shj.UP.data.Dozent,de.shj.UP.data.shjCore" session="true" isThreadSafe="false" errorPage="../../error.jsp"%><jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean id="seminar" scope="session" class="de.shj.UP.logic.SeminarData" /><jsp:useBean id="student" scope="session" class="de.shj.UP.beans.config.student.StudentBean" /><jsp:useBean
	id="sd" scope="session" class="de.shj.UP.util.SessionData" />
<%@include file="../../../fragments/checkVersion.jsp" %>
<%@include file="../../../fragments/checkLogin.jsp" %>
<%@include file="../../../fragments/checkAccess1.jsp" %>
<%@include file="../../../fragments/checkInitStudent.jsp" %>
<%@include file="../../../fragments/mapRequestToObject.jsp" %>
<%  long lERR_BASE=101000 + 400;    // Leistung + Add

    String g_sSuppressResponse="shjSignUp_suppressResponse=on";

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

    // ==============================================================
    // Ende der Schnittstelle
    // Prüfe, ob ein Schein zu einem Kurs
    // ausgestellt werden soll
    // ==============================================================
    long lKursID=-1;
    boolean bArchiv=false;
    
    // Es wird ein bestimmter Kurs angegeben. Dann 
    // müssen alle Parameter (kurs_id, commitment, archiv)
    // übergeben sein.
    if(request.getParameter("kurs_id")!=null || request.getParameter("archiv")!=null){
        try{
            lKursID = Long.parseLong(request.getParameter("kurs_id"));
            bArchiv = (request.getParameter("archiv")==null ? false : Boolean.parseBoolean(request.getParameter("archiv")));
        }catch(Exception eParameterJumble){
            throw new Exception("{\"error\":\"Die Leistung bzw. Anmeldung kann nicht hinzugefügt werden.\",\"errorDebug\":\"Die Parameter kurs_id und archiv (" + request.getParameter("kurs_id") + ", " + request.getParameter("archiv") + ") wurden nicht oder teilweise nicht korrekt übergeben.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":100}");
        }
    }else{
        %><jsp:forward page="addGeneric.jsp" /><%
    }
    
    // Schaue nach, ob eine Anmeldung zu diesem 
    // Kurs vorliegt. Erzeuge ggf. eine Anmeldung
    long lLeistungCount=-1;
    try{
        lLeistungCount=Long.parseLong(user.lookUp("lngStudentLeistungCount", "tblBdStudentXLeistung", 
            "\"strMatrikelnummer\"='" + student.getMatrikelnummer() + 
            "' and \"lngSdSeminarID\"=" + student.getSeminarID() + " and \"lngKlausuranmeldungKursID\"=" + lKursID + " and \"blnStudentLeistungKlausuranmeldung\"=true"));
    }catch(Exception eNotNumeric){
            // Es wird zuerst eine Anmeldung erzeugt
            // (ruhig über die dafür zuständige JSP
            // Seite).
            // Diese Seite darf keine eigene JSON-
            // Rückmeldung absenden, damit der Client
            // sich nicht über die JSON-Syntax beschwert.
            String sPage="../anmeldung/add.jsp?" + g_sSuppressResponse;
            %><jsp:include page="<%=sPage%>" /><%
                // Sehe nach, ob die Anmeldung erfolgreich 
                // hinzugefügt wurde:
                try{
                    lLeistungCount=Long.parseLong(user.lookUp("lngStudentLeistungCount", "tblBdStudentXLeistung", 
                        "\"strMatrikelnummer\"='" + student.getMatrikelnummer() + 
                        "' and \"lngSdSeminarID\"=" + student.getSeminarID() + " and \"lngKlausuranmeldungKursID\"=" + lKursID + " and \"blnStudentLeistungKlausuranmeldung\"=true"));
                }catch(Exception eNotNumeric2){
                    throw new Exception("{\"error\":\"Die Leistung kann leider so nicht gespeichert werden.\",\"errorDebug\":\"Es sollte eine Leistung hinzugefügt werden, zu der erst eine Anmeldung zu erzeugen war; das Erzeugen der Anmeldung ist allerdings leider schiefgegangen.\",\"errorcode\":" + lERR_BASE + 4 + ",\"severity\":50}");
            }
    }
   
    // Ab hier ist klar: es handelt sich um die 
    // Umwandlung einer Anmeldung zu einer Leistung
    // Es werden zumindest Parameter zu
    // Note, Datum, Leistungspunkten gebraucht, 
    // möglichst auch zum Modul
    StudentXLeistung sl = new StudentXLeistung(student.getSeminarID(), student.getMatrikelnummer(), lLeistungID, lLeistungCount);
    mapRequestToObject(sl, request);
    if(!shjCore.normalize(request.getParameter("leistungstyp")).equals("")) sl.setStudentLeistungCustom3(request.getParameter("leistungstyp"));
    if(!shjCore.normalize(request.getParameter("details")).equals("")) sl.setStudentLeistungDetails(request.getParameter("details"));
    if(!shjCore.normalize(request.getParameter("bemerkung")).equals("")) sl.setStudentLeistungBemerkung(request.getParameter("bemerkung"));
    if(!shjCore.normalize(request.getParameter("anerkannt")).equals("")) if(request.getParameter("anerkannt").equals("t")) sl.setStudentLeistungAnerkannt(true);
    // Hausarbeit/Klausur:
    // ENDE Hausarbeit/Klausur
    
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

    sl.setStudentLeistungKlausuranmeldung(false);
    sl.setStudentLeistungGesiegelt(true);
    sl.setStudentLeistungValidiert(true);
    
    if(!sl.update()) throw new Exception("{\"error\":\"Die Leistung kann nicht hinzugefügt werden.\",\"errorDebug\":\"Die Datenbank verweigert das Speichern, es hilft wohl nur der Blick in deren Logfiles\",\"errorcode\":" + lERR_BASE + 1 + ",\"severity\":50}");
	%><%if(request.getParameter("shjSignUp_suppressResponse")==null){%>{"success":"true",
        "leistung":{"id":"<%=sl.getLeistungsID() %>","count":"<%=sl.getStudentLeistungCount() %>","kurstitel":"<%=shjCore.string2JSON(sl.getSLKursTitel()) %>","kurstitel_en":"<%=shjCore.string2JSON(sl.getSLKursTitel_en()) %>",
        "noten_id":"<%=sl.getNoteID() %>","aussteller_nachname":"<%=sl.getStudentLeistungAussteller() %>","aussteller_vorname":"<%=sl.getStudentLeistungAusstellerVorname() %>"}}<%}%>