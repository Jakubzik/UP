<%--
    @TODO
    ===========================
    SQL wg. Injection lieber als Statement          (Priority 5)

    SYNOPSIS (German)
    ===========================
    2012, Nov 23, shj
    Erzeugt. 
    
    Üblicher Lifecycle: ADD

    Speichert die Anmeldung zur übergebenen KursID.
    Das Anmelden im Archiv ist nur möglich, wenn 
    ein Dozent-Login vorliegt; dann muss außerdem 
    die dozent_id desjenigen übergeben werden, der 
    den Kurs unterrichtet hat (die dozentID wird nämlich 
    nicht archiviert).
    
    Expected SESSION PROPERTIES
    ===========================
    [user        muss initialisiert sein, falls Archiv-Anmeldung erzeugt werden soll]
    student     muss identische Matrikelnummer haben, die auch
                als Parameter übergeben wird.

    Expected PARAMETER(S):
    ===========================
    id              [long] Id der Leistung gemäß tblSdLeistung.lngLeistungID
    matrikelnummer  [long]
    kurs_id         [long],     
    kurstyp_id      [long]
    aussteller_id   [long]
    modul_id        [long, optional]

    archiv          [boolean],  optional
    semester_indicator          optional

    signup-expected-backend-version [text], vgl. fragments/checkVersion.jsp
    
    Returns
    =======
    Object:
    {"success":"true/false", "details":'Beschreibung des Fehlers, z.B. Note fehlt'}

    Fehler/Errors/THROWS
    ====================

    
--%><%@page import="de.shj.UP.data.Kurs"%>
<%@ page contentType="text/json" pageEncoding="UTF-8" import="de.shj.UP.data.Dozent,de.shj.UP.data.shjCore" session="true" isThreadSafe="false" errorPage="../../error.jsp"%><jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean id="seminar" scope="session" class="de.shj.UP.HTML.HtmlSeminar" /><jsp:useBean id="student" scope="session" class="de.shj.UP.beans.config.student.StudentBean" /><jsp:useBean
	id="sd" scope="session" class="de.shj.UP.util.SessionData" /><% long lERR_BASE=102000 + 400;    // Anmeldung + Add%>
<%@include file="../../../fragments/checkVersion.jsp" %>
<%@include file="../../../fragments/checkInitStudent.jsp" %>
<%
    // ==============================================================
    // Schnittstelle: Parameter
    // ==============================================================
    long lKursID=-1;            // Parameter
    long lKurstypID=-1;         // Parameter
    long lLeistungID=-1;        // Parameter
    String sModulID="";         // Parameter
    boolean bArchiv=false;      // Parameter
    boolean bIsStudent = user.getDozentNachname()==null || sd.getSessionType().equals("student");
    
    try{
        lKursID=Long.parseLong(request.getParameter("kurs_id"));
        lKurstypID=Long.parseLong(request.getParameter("kurstyp_id"));
        lLeistungID=Long.parseLong(request.getParameter("id"));
        if(request.getParameter("modul_id")!=null) sModulID=request.getParameter("modul_id");
        bArchiv = (request.getParameter("archiv") != null) || (shjCore.normalize(request.getParameter("semester_indicator")).equalsIgnoreCase("archive"));
    }catch(Exception eParameterproblems){
        throw new Exception("{\"error\":\"Die Anmeldung konnte nicht gespeichert werden.\",\"errorDebug\":\"Die Parameter >kurs_id<, >kurstyp_id< oder >id< (" + request.getParameter("kurs_id") + ", " + request.getParameter("kurstyp_id") + ", " + request.getParameter("id") + ") sind null oder nicht numerisch.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":100}");
    }

    // ==============================================================
    // Schnittstelle: Berechtigungen, Sitzung
    // ==============================================================
    // Für Anmeldungen aus dem Archiv (um z.B. eine 
    // Leistung auszustellen) wird
    // - das Login durch einen Dozenten,
    // - für Anmeldungen zu Kursen anderer 
    //   eine Berechtigung > 500
    // benötigt
    long lDozentID=-1;
    try{
        lDozentID=Long.parseLong(request.getParameter("aussteller_id"));
    }catch(Exception eNotHandedOverIgnore){
        throw new Exception("{\"error\":\"Die Anmeldung konnte nicht gespeichert werden.\",\"errorDebug\":\"Der Parameter >aussteller_id< wurde nicht (numerisch) übergeben und hat den Wert: " + request.getParameter("aussteller_id") + ".\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":100}");
    }
    if(bArchiv){
        if(bIsStudent)
            throw new Exception("{\"error\":\"Die Anmeldung konnte nicht gespeichert werden.\",\"errorDebug\":\"Mit studentischem Login können keine Anmeldungen aus dem Archiv hinzugefügt werden.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":100}");
                       
        if(user.getDozentID()!=lDozentID && user.getDozentAccessLevel()<500)
            throw new Exception("{\"error\":\"Die Anmeldung konnte nicht gespeichert werden.\",\"errorDebug\":\"Um Anmeldungen zu Kursen anderer Lehrender zu speichern ist eine höhere Berechtigung notwendig.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":80}");
    }
    
    // ==============================================================
    // Ausführung
    // ==============================================================
    long lCount=-1;
    if(bArchiv){
       lCount=user.getNextID("lngStudentLeistungCount", 
                    "tblBdStudentXLeistung", 
                    "\"lngSdSeminarID\"=" +  user.getSdSeminarID() + 
                    " and \"strMatrikelnummer\"='" + Long.parseLong(student.getMatrikelnummer()) + 
                    "' and \"lngLeistungsID\"=" + lLeistungID);
       if(!user.sqlExe(getSaveAnmeldungSQLArchiv(student, 
                lLeistungID,
                lKursID, 
                lKurstypID, 
                lDozentID,
                lCount, 
                sModulID))) throw new Exception("{\"error\":\"Die Anmeldung konnte nicht gespeichert werden.\",\"errorDebug\":\"Die Datenbank bockt bei der Ausführung (Hinzufügen Anmeldung aus Archiv) -- Details in den Logfiles?.\",\"errorcode\":" + lERR_BASE + 1 + ",\"severity\":70}");
    }else{
        lCount=user.getNextID("lngStudentLeistungCount", 
                    "tblBdStudentXLeistung", 
                    "\"lngSdSeminarID\"=" +  student.getSeminarID() + 
                    " and \"strMatrikelnummer\"='" + Long.parseLong(student.getMatrikelnummer()) + 
                    "' and \"lngLeistungsID\"=" + lLeistungID);
        if(1!=(user.sqlExeCount(getSaveAnmeldungSQLAktuellesSemester(student, 
                lLeistungID,
                lKursID, 
                lKurstypID, 
                lCount, 
                sModulID, bIsStudent)))) throw new Exception("{\"error\":\"Die Anmeldung konnte nicht gespeichert werden.\",\"errorDebug\":\"Die Datenbank bockt bei der Ausführung -- Details in den Logfiles?.\",\"errorcode\":" + lERR_BASE + 1 + ",\"severity\":70}");
    }    
	%><%if(request.getParameter("shjSignUp_suppressResponse")==null){%>{"success":"true", "anmeldung":{"id":"<%=lLeistungID%>", "count":"<%=lCount%>"}}<%}%>
        
<%!
    // ACHTUNG: setzt die "lngDozentID" in "tblBdStudentXLeistung" auf null, 
    //      wenn der Parameter lDozentID<1 ist.
    //      Das stört aber, wenn über das Objekt studentXLeistung ein 
    //      .update ausgeführt wird.
    String getSaveAnmeldungSQLArchiv(de.shj.UP.beans.config.student.StudentBean student, long leistung_id, long kurs_id, long kurstyp_id, long dozent_id, long leistung_count, String modul_id){
        return "INSERT INTO \"tblBdStudentXLeistung\"(" + 
            "\"lngSdSeminarID\", \"strMatrikelnummer\", \"lngLeistungsID\", \"lngStudentLeistungCount\", " + 
            "\"lngDozentID\", \"intNoteID\", \"lngKlausuranmeldungKurstypID\", \"lngKlausuranmeldungKursID\", " + 
            "\"strSLKursTag\", \"dtmSLKursBeginn\", \"dtmSLKursEnde\", " + 
            "\"strSLKursRaum\", \"strSLKursTag2\", \"dtmSLKursBeginn2\", \"dtmSLKursEnde2\", " + 
            "\"strSLKursRaum2\", \"strSLKursTitel\", \"strSLKursTitel_en\", \"strSLKursBeschreibung\", " + 
            "\"strSLKursBeschreibung_en\", \"strSLKursLiteratur\", \"strSLKursZusatz\", " + 
            "\"strSLKursVoraussetzung\", " + 
            "\"strSLKursEinordnung\", " + 
            "\"blnSLKursScheinanmeldungErlaubt\", \"strSLKursTerminFreitext\", " + 
            "\"intSLKursTeilnehmer\", " + 
            "\"sngStudentLeistungCreditPts\", \"strStudentLeistungAussteller\"," + 
            "\"blnStudentLeistungValidiert\", \"dtmStudentLeistungAntragdatum\", " + 
            "\"blnStudentLeistungKlausuranmeldung\", \"blnStudentLeistungEditierbar\", " + 
            "\"blnStudentLeistungGesiegelt\", \"blnStudentLeistungPruefung\", \"strStudentLeistungAusstellerVor\", " + 
            "\"strStudentLeistungAusstellerTit\", \"lngModulID\")" + 
        "SELECT " + 
            "\"lngSdSeminarID\"," + 
            "'" + Long.parseLong(student.getMatrikelnummer()) + "'," + leistung_id + ", " + leistung_count + "," + 
	    (dozent_id<0 ? " null" : dozent_id) + ", null, " + kurstyp_id + ", " + kurs_id + ", " + 
            "\"strKvvArchivKursTag\", \"dtmKvvArchivKursBeginn\", \"dtmKvvArchivKursEnde\", " + 
            "\"strKvvArchivKursRaum\", \"strKvvArchivKursTag2\", \"dtmKvvArchivKursBeginn2\", " + 
            "\"dtmKvvArchivKursEnde2\", \"strKvvArchivKursKursRaum2\"," + 
            "\"strKvvArchivKurstitel\", \"strKvvArchivKurstitel_en\", " + 
            "\"strKvvArchivKursBeschreibung\", " + 
            "\"strKvvArchivKursBeschreibung_en\",\"strKvvArchivKursLiteratur\", \"strKvvArchivKursZusatz\", " + 
            "\"strKvvArchivKursVoraussetzung\", \"strKvvArchivKursEinordnung\", true, \"strKvvArchivKursTerminFreitext\"," + 
            "\"intKvvArchivKursTeilnehmer\", \"sngKvvArchivKursCreditPts\",\"strKvvArchivDozentname\", false, CURRENT_DATE," + 
            "true, true, false, true, \"strKvvArchivDozentvorname\",\"strKvvArchivDozenttitel\", " + (modul_id.equals("") ? "null" : modul_id) + " " +
        " FROM \"tblBdKvvArchiv\"" + 
        "where \"lngSdSeminarID\"=" + student.getSeminarID() + " and " + 
            "\"lngKvvArchivKurstypID\"=" + kurstyp_id + " and " + 
            "\"lngID\"=" + kurs_id + " limit 1"; 
    }

    // ModulID: "" übergeben, falls "null" gesetzt werden soll!
    String getSaveAnmeldungSQLAktuellesSemester(de.shj.UP.beans.config.student.StudentBean student, 
            long leistung_id, long kurs_id, long kurstyp_id, 
            long leistung_count, String modul_id, boolean bCheckCommitmentPeriod){
        String sStrict = "k.\"dtmKursScheinanmeldungVon\"<=CURRENT_DATE and k.\"dtmKursScheinanmeldungBis\">=CURRENT_DATE and ";
        return "INSERT INTO \"tblBdStudentXLeistung\"(" +
            "\"lngSdSeminarID\", \"strMatrikelnummer\", \"lngLeistungsID\", \"lngStudentLeistungCount\", " +
            "\"lngDozentID\", \"intNoteID\", \"lngKlausuranmeldungKurstypID\", \"lngKlausuranmeldungKursID\", " +
            "\"strSLKursTag\", \"dtmSLKursBeginn\", \"dtmSLKursEnde\", " +
            "\"strSLKursRaum\", \"strSLKursTag2\", \"dtmSLKursBeginn2\", \"dtmSLKursEnde2\", " +
            "\"strSLKursRaum2\", \"strSLKursTitel\", \"strSLKursTitel_en\", \"strSLKursBeschreibung\", " +
            "\"strSLKursBeschreibung_en\", \"strSLKursLiteratur\", \"strSLKursZusatz\", " +
            "\"strSLKursAnmeldung\", \"strSLKursVoraussetzung\", \"blnSLKursSchein\", " +
            "\"strSLKursEinordnung\", " +
            "\"dtmSLKursScheinanmeldungBis\", \"dtmSLKursScheinanmeldungVon\", " +
            "\"blnSLKursScheinanmeldungErlaubt\", \"strSLKursTerminFreitext\", " +
            "\"intSLKursTeilnehmer\", \"strSLKursRaumExtern1\", \"strSLKursRaumExtern2\", " +
            "\"sngStudentLeistungCreditPts\", \"strStudentLeistungAussteller\"," +
            "\"blnStudentLeistungValidiert\", \"dtmStudentLeistungAntragdatum\", " +
            "\"blnStudentLeistungKlausuranmeldung\", \"blnStudentLeistungEditierbar\", " +
            "\"blnStudentLeistungGesiegelt\", \"blnStudentLeistungPruefung\", \"strStudentLeistungAusstellerVor\", " +
            "\"strStudentLeistungAusstellerTit\", \"lngModulID\") " +
    "SELECT " +
	student.getSeminarID() + "," +
	"'" + Long.parseLong(student.getMatrikelnummer()) + "'," +
        leistung_id + "," +
	leistung_count + "," +
        "d.\"lngDozentID\", " +
	"null," +
        "x.\"lngKurstypID\", " +
        "k.\"lngKursID\", " +
        "k.\"strKursTag\", " +
        "k.\"dtmKursBeginn\", " +
        "k.\"dtmKursEnde\", " +
        "k.\"strKursRaum\", " +
        "k.\"strKursTag2\", " +
        "k.\"dtmKursBeginn2\", " +
        "k.\"dtmKursEnde2\", " +
        "k.\"strKursRaum2\", " +
        "k.\"strKursTitel\", " +
        "k.\"strKursTitel_en\", " +
        "k.\"strKursBeschreibung\", " +
        "k.\"strKursBeschreibung_en\", " +
        "k.\"strKursLiteratur\", " +
        "k.\"strKursZusatz\", " +
        "k.\"strKursAnmeldung\", " +
        "k.\"strKursVoraussetzung\", " +
        "k.\"blnKursSchein\", " +
        "k.\"strKursEinordnung\", " +
        "k.\"dtmKursScheinanmeldungBis\", " +
        "k.\"dtmKursScheinanmeldungVon\", " +
        "k.\"blnKursScheinanmeldungErlaubt\", " +
        "k.\"strKursTerminFreitext\", " +
        "k.\"intKursTeilnehmer\", " +
        "k.\"strKursRaumExtern1\", " +
        "k.\"strKursRaumExtern2\", " +
        "x.\"sngKursCreditPts\"," +
        "d.\"strDozentNachname\", " +
        "false," +
        "CURRENT_DATE," +
	"true," +
	"true," +
	"false," +
	"true," +
        "d.\"strDozentVorname\"," + 
        "d.\"strDozentTitel\", " +
        (modul_id.equals("") ? "null" : modul_id) +
    " FROM " +
        "\"tblBdKurs\" k, " +
        "\"tblBdKursXKurstyp\" x, " +
        "\"tblSdDozent\" d, " +
        "\"tblSdKurstyp\" t " +
      "WHERE " +
        "x.\"lngSeminarID\" = k.\"lngSdSeminarID\" AND " +
        "x.\"lngKursID\" = k.\"lngKursID\" AND " +
        "d.\"lngSdSeminarID\" = k.\"lngSdSeminarID\" AND " +
        "d.\"lngDozentID\" = k.\"lngDozentID\" AND " +
        "t.\"lngSdSeminarID\" = x.\"lngSeminarID\" AND " +
        "t.\"lngKurstypID\" = x.\"lngKurstypID\" AND " + (bCheckCommitmentPeriod ? sStrict:"") +
        "x.\"lngSeminarID\" = " + student.getSeminarID() + " AND " +
        "x.\"lngKurstypID\" = " + kurstyp_id + " AND " +
        "x.\"lngKursID\" = " + kurs_id + " limit 1;";
    }
%>
