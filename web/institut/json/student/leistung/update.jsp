<%--
    JSP-Object "student/leistung/update"

    SYNOPSIS (German)
    ===========================
    2012, Dec 31, shj    erzeugt. 
    2013, Dez 27, shj    überarbeitet
    2016. Jun 21, shj    an OS angepasst

    Üblicher Lifecycle: UPDATE

    Ändert Note, ID, Kurstitel (D und E), LP, Datum
    Hausarbeitstitel/Leistungstyp oder Aussteller 
    einer Leistung, oder ändert eine Anmeldung in 
    eine entsprechende Leistung um.

    Kurstitel (D und E) werden (je nach Konfiguration) ggf bei 
    allen Kursen (in allen Leistungen) abgeändert, d.h. 
    die Änderung wird propagiert. (Inbesondere bei 
    Korrekturen im Transkript macht das Sinn, wenn z.B. 
    "Ficion in the USA" geändert wird zu "Fiction in the USA").

    Es ist explizit auch möglich, die Leistung zu ändern, 
    d.h. zum Beispiel aus einem Proseminar Literaturwiss. 
    ein Proseminar Kulturwissenschaft zu machen.
        
    Expected SESSION PROPERTIES
    ===========================
    user        muss initialisiert sein; bei Berechtigung < 500 dürfen nur eigene Leistungen geändert werden.
    student     muss identische Matrikelnummer haben, die auch
                als Parameter übergeben wird.

    Expected PARAMETER(S):
    ===========================
    matrikelnummer  [long]
    id_orig         [long] leistungsID              } Schlüsselwerte der Leistung in der DB; i.d.R. ist das gleich id und count,
    count_orig      [long] studentLeistungCount     } bei Änderung der Leistung (von Litwiss. auf Kulturwiss. o.ä.) ist es die 
                                                      Bezugsgröße des derzeit gespeicherten Datensatzes

    aussteller_id   [long] (ggf. neuer) Aussteller d. Leistung

    kurstitel       [text] Neuer Kurstitel. Je nach Konfiguration werden alle Titel des Kurses entsprechend umbenannt.
    kurstitel_en    [text] Neuer engl. Kurstitel. Je nach Konfiguration werden alle Titel des Kurses entsprechend umbenannt.

    noten_id        [int]  Id der Note der Leistung (vgl. tblSdNoten.intNoteID)
    datum           [text] interpretierbar als deutsches Datum (dd.MM.yyyy)
    ects            [text] interpretierbar als float
    leistungstyp    [text] (Hausarbeit, Klausur, nix)
    details         [text] ggf. Titel der Hausarbeit
    bemerkung       [text] Weitere Bemerkung (z.B. "anerkannt von ursprünglich Leistung xyz").
    modul_id        [text] numerisch oder 'null' (optional)
    
    [anerkannt       [t|f] optional]

    signup-expected-backend-version [text]
    
    Returns
    =======
    Object:
    {"success":"true"}

    Sample Usage
    ============
    @TODO
    
--%><%@page import="de.shj.UP.data.Dozent"%>
<%@page import="de.shj.UP.data.StudentXLeistung"%>
<%@ page contentType="text/json" pageEncoding="UTF-8" 
         import="de.shj.UP.data.shjCore" session="true" isThreadSafe="false" errorPage="../../error.jsp"%>
<jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" />
<jsp:useBean id="student" scope="session" class="de.shj.UP.beans.config.student.StudentBean" />
<%@include file="../../../fragments/checkVersion.jsp" %>
<%@include file="../../../fragments/checkLogin.jsp" %>
<%@include file="../../../fragments/checkAccess1.jsp" %>
<%@include file="../../../fragments/checkInitStudent.jsp" %>
<%      long lERR_BASE=101000 + 200;    // Leistung + Update

        // Wenn die Kurstitel von Leistungen geändert werden,
        // sollen dann _alle_ solche Kurstitel geändert werden?
        //
        // Oft handelt es sich um Korrekturen, d.h. die 
        // grundsätzliche Änderung ist erwünscht.
        boolean g_bSpreadKurstitelChanges=true;

        // Initialisiere Leistung
        // über Parameter 
        // id_orig und count_orig
        StudentXLeistung sl=null;
        try{sl = new StudentXLeistung(
            user.getSdSeminarID(), 
            student.getMatrikelnummer(),
            Long.parseLong(request.getParameter("id_orig")),
            Long.parseLong(request.getParameter("count_orig")));
        }catch(Exception eParametersIncorrect){
            throw new Exception("{\"error\":\"Die Leistung konnte nicht geändert werden.\",\"errorDebug\":\"Die zu ändernde Leistung konnte anhand der Parameter id_orig und count_orig (" + request.getParameter("id_orig") + ", " + request.getParameter("count_orig") + ") nicht identifiziert werden.\",\"errorcode\":" + lERR_BASE + 9 + ",\"severity\":100}");            
        }

        // Besteht die Berechtigung
        // zum Ändern der Leistungsdetails?
        if(sl.getDozentID()!=user.getDozentID() && user.getDozentAccessLevel()<500){
            throw new Exception("{\"error\":\"Die Leistung konnte nicht geändert werden -- Ihre Berechtigung reicht nicht aus.\",\"errorDebug\":\"Sie haben nicht die notwendige Berechtigung, um eine Leistung eines anderen Lehrenden zu ändern, sondern nur " + user.getDozentAccessLevel() + ".\",\"errorcode\":" + lERR_BASE + 2 + ",\"severity\":80}");            
        }
        
        // Falls der Aussteller geändert wurde:
        // setze auch Namen und Titel
	try{
            if(sl.getDozentID() != Long.parseLong(request.getParameter("aussteller_id")) && (Long.parseLong(request.getParameter("aussteller_id"))!=-1)){
                Dozent dozent = new Dozent(user.getSdSeminarID(), Long.parseLong(request.getParameter("aussteller_id")));
                sl.setStudentLeistungAussteller(dozent.getDozentNachname());
                sl.setStudentLeistungAusstellerVorname(dozent.getDozentVorname());
                sl.setStudentLeistungAusstellerTitel(dozent.getDozentTitel());
                sl.setDozentID(Long.parseLong(request.getParameter("aussteller_id")));
            }
	}catch(Exception eNoDozentIDIgnore){
		// Kommt in folgender Konstellation vor:
		// Wird der Schein eines früheren Lehrenden auf-
		// gerufen, der nicht aktuell in der Kombo-Box 
		// aufgelistet ist, wird beim Update des Scheins 
		// keine ID übergeben (weil sie nämlich schlicht
		// nicht vorliegt). Das kann unschädlich
		// ignoriert werden.
	}
        
        boolean bSpreadKurstitelDE=false;
        boolean bSpreadKurstitelEN=false;
        String sKurstitelAlt="";
        String sKurstitelAlt_en="";
        
        if(g_bSpreadKurstitelChanges){
            try{
                bSpreadKurstitelDE= (!sl.getSLKursTitel().trim().equals(request.getParameter("kurstitel").trim()));
                sKurstitelAlt=sl.getSLKursTitel();
            }catch(Exception eNullSomethingIgnore){System.out.println("Oops, kann Kurstitel nicht vergleichen, egal.");}
            try{
                bSpreadKurstitelEN= (!sl.getSLKursTitel_en().trim().equals(request.getParameter("kurstitel_en").trim()));
                sKurstitelAlt_en=sl.getSLKursTitel_en();
            }catch(Exception eNullSomethingIgnoreToo){System.out.println("Oops, kann Kurstitel nicht vergleichen, egal.");}            
        }
        
        // Setze die ansonsten über 
        // Parameter übergebenen Werte
	try{
            sl.setSLKursTitel(request.getParameter("kurstitel"));
            sl.setSLKursTitel_en(request.getParameter("kurstitel_en"));
            sl.setNoteID(Integer.parseInt(request.getParameter("noten_id")));
            sl.setStudentLeistungAusstellungsdatum(new java.sql.Date(shjCore.g_GERMAN_DATE_FORMAT.parse(request.getParameter("datum")).getTime()));
            sl.setStudentLeistungCreditPts(Float.parseFloat(request.getParameter("ects")));
            sl.setStudentLeistungCustom3(request.getParameter("leistungstyp"));
            sl.setStudentLeistungDetails(request.getParameter("details"));
            sl.setStudentLeistungBemerkung(request.getParameter("bemerkung"));
        }catch(Exception eParametersAskew){
            throw new Exception("{\"error\":\"Die Leistung konnte nicht geändert werden -- es fehlen Angaben.\"" + 
                    ",\"errorDebug\":\"Note, Datum oder LP [noten_id, datum, ects] = " + 
                    "(" + request.getParameter("noten_id") + ", " + request.getParameter("datum") + ", " +
                    request.getParameter("ects") + ") entsprechen nicht dem erwarteten Format.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":100}");            
        }
        
        // Verschiebung ins Zusatzmodul
	if(request.getParameter("modul_id")==null || request.getParameter("modul_id").equals("null")) sl.setModulToNull();
        else sl.setModulID(Long.parseLong(request.getParameter("modul_id")));
        
        // Flag "anerkannt"
        sl.setStudentLeistungAnerkannt(shjCore.normalize(request.getParameter("anerkannt")).equals("t"));

	// Ggf. die LeistungsID ändern
	if(Long.parseLong(request.getParameter("id")) != sl.getLeistungsID()){
            
		if(request.getParameter("modul_id")==null || request.getParameter("modul_id").equals("null")){
                    if(!sl.updateLeistungIdTo(Long.parseLong(request.getParameter("id")))){
                        throw new Exception("{\"error\":\"Die Leistung konnte nicht geändert werden -- sorry. Ist sie vielleicht Teil einer Prüfung? Dann muss zuerst die Prüfung gelöscht werden.\"" + 
                        ",\"errorDebug\":\"Die Datenbank hat sich verweigert und will die LeistungsID nicht ändern (ModulID ist übrigens null).\",\"errorcode\":" + lERR_BASE + 1 + ",\"severity\":50}");            
                    }
                }else{
                    if(!sl.updateLeistungIdTo(Long.parseLong(request.getParameter("id")), Long.parseLong(request.getParameter("modul_id")))){
                        if(!sl.updateLeistungIdTo(Long.parseLong(request.getParameter("id")))){
                            throw new Exception("{\"error\":\"Die Leistung konnte nicht geändert werden -- sorry. Ist sie vielleicht Teil einer Prüfung? Dann muss zuerst die Prüfung gelöscht werden.\"" + 
                            ",\"errorDebug\":\"Die Datenbank hat sich verweigert und will die LeistungsID nicht ändern.\",\"errorcode\":" + lERR_BASE + 1 + ",\"severity\":50}");            
                        }
                    }
                }
	}
	
	// Fehler abfangen:
	String sErrorReport="";
	if(sl.getNoteID()<=0){
            throw new Exception("{\"error\":\"Die Leistung konnte nicht geändert werden -- es muss eine Note angegeben werden.\"" + 
                    ",\"errorDebug\":\"Die übergebene Note [noten_id] = " + 
                    "(" + request.getParameter("noten_id") + " hat eine reservierte ID (<= 0).\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":100}");            

	}else{
		// Ist das eine Umwandlung einer Anmeldung?
		if(sl.getStudentLeistungKlausuranmeldung()){
			sl.setStudentLeistungKlausuranmeldung(false);
			sl.setStudentLeistungGesiegelt(true);
			sl.setStudentLeistungValidiert(true);
		}
		if(!sl.update()) throw new Exception("{\"error\":\"Die Leistung konnte nicht geändert werden -- sorry.\"" + 
                    ",\"errorDebug\":\"Die Datenbank hat sich verweigert.\",\"errorcode\":" + lERR_BASE + 1 + ",\"severity\":50}");            

	}	
        if(!sErrorReport.equals("")) throw new Exception("HELLO, CORRECT ME");

        if(bSpreadKurstitelDE){
            try{
                if(sKurstitelAlt.length()<5) throw new Exception("Der Kurstitel ist zu kurz");
                user.sqlExe("update \"tblBdStudentXLeistung\" set \"strSLKursTitel\"='" + sl.getSLKursTitel() + "' where " + 
                        "\"lngSdSeminarID\"=" + user.getSdSeminarID() + " and \"strSLKursTitel\"='" + 
                        sKurstitelAlt + "' and \"lngLeistungsID\"=" + sl.getLeistungsID() + ";");
            }catch(Exception eFailsOnDatabase){
                 throw new Exception("{\"error\":\"Die Leistung geändert werden, aber der Kurstitel wurde nicht generell korrigiert.\"" + 
                    ",\"errorDebug\":\"Die Datenbank hat sich verweigert.\",\"errorcode\":" + lERR_BASE + 1 + ",\"severity\":10}");            
            }
        }
        
        if(bSpreadKurstitelEN){
            try{
                if(sKurstitelAlt_en.length()<5) throw new Exception("Der engl. Kurstitel ist zu kurz");
                user.sqlExe("update \"tblBdStudentXLeistung\" set \"strSLKursTitel_en\"='" + sl.getSLKursTitel() + "' where " + 
                        "\"lngSdSeminarID\"=" + user.getSdSeminarID() + " and \"strSLKursTitel_en\"='" + 
                        sKurstitelAlt_en + "' and \"lngLeistungsID\"=" + sl.getLeistungsID() + ";");
            }catch(Exception eFailsOnDatabaseToo){
                 throw new Exception("{\"error\":\"Die Leistung geändert werden, aber der Kurstitel wurde nicht generell korrigiert.\"" + 
                    ",\"errorDebug\":\"Die Datenbank hat sich verweigert.\",\"errorcode\":" + lERR_BASE + 1 + ",\"severity\":10}");            
            }
        }
        
%>{"success":"<%=(sErrorReport.equals("") ? "true" : "false")%>", "details":"<%=sErrorReport %>", "leistung":"<%=sl.getLeistungsID() %>", "leistung_count":"<%=sl.getStudentLeistungCount() %>", "aussteller_vorname":"<%=shjCore.string2JSON(sl.getStudentLeistungAusstellerVorname()) %>", "aussteller_nachname":"<%=shjCore.string2JSON(sl.getStudentLeistungAussteller()) %>"}
