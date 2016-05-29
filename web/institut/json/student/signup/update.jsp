<%@ page contentType="text/json" pageEncoding="UTF-8" import="java.sql.ResultSet,java.util.Enumeration,java.text.SimpleDateFormat,de.shj.UP.data.shjCore" session="true" isThreadSafe="false" errorPage="../../error.jsp"%>
<jsp:useBean id="seminar" scope="session" class="de.shj.UP.HTML.HtmlSeminar" />
<jsp:useBean id="student" scope="session" class="de.shj.UP.beans.config.student.StudentBean" />
<%@include file="../../../fragments/checkVersion.jsp" %>
<%@include file="../../../fragments/checkLoginStudent.jsp" %>
<%@include file="../../../fragments/conf_kurswahl.jsp" %>
<%  long lERR_BASE=107000 + 200;    // Anmeldung + Update

    // TODO
    // - Limit Anzahl gewählter Kurstypen               (OK)
    // - verhindern, dass gleicher Kurs in untersch. 
    //   Kurstypen gewählt                              (OK)
    // - Fixierte überall ausnehmen                     (OK)
    // - Logik Wahlmöglichkeit/Studiengang und
    //     Prerequisites einbauen                       (OK)
    
// Wie viele Kurstypen dürfen maximal ausgewählt sein?
if(!seminar.Data().isSignUpPeriod()) throw new Exception("{\"error\":\"Zur Zeit findet keine Kurswahl statt.\",\"errorDebug\":\"Der Zeitraum der Kurswahl schließt das heutige Datum nicht ein (see tblData).\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":100}");


        // Lese Kurstyp aus Parameter
        long lKurstypID=-1;
        try{
            lKurstypID=Long.parseLong(request.getParameter("kurstyp_id"));
        }catch(Exception eNoKurstypID){
            throw new Exception("{\"error\":\"Die Kurswahl konnte nicht gespeichert werden.\",\"errorDebug\":\"Der erforderliche Parameter >kurstyp_id< hatte den Wert '" + request.getParameter ("kurstyp_id") + "', muss aber numerisch sein.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":100}");
        }
        
        // Zu viele Anmeldungen?
        // Nehme bereits zugeteilte Kurse aus
        if(("," + sMAX_AUSWAHL_KURSTYPEN_AUSNAHMEN + ",").indexOf("," + lKurstypID + ",")<0){
            // Es soll sich also zu einem Kurs angemeldet werden,
            // der _keine_ Ausnahme ist.
            //
            // Zähle die bereits gewählten Kurse -- nehme
            // dabei ggf. die Kurse aus, die schon bezuschlagt 
            // sind.
            //
            /* 
             * Zähle also 
             * (1) alle Frischen Anmeldungen (Zuschlag==null),
             * (2) Ggf. OHNE alle Kurse mit Zuschlag
             *     (Zuschlag nicht null und es gibt 
             *      keinen Zuschlag in diesem Kurstyp) <- NICHT mitzählen
            */
            // 
            int iCount=0;
            ResultSet tmp = seminar.sqlQuery(
                "SELECT count(\"lngKurstypID\") AS COUNT " +
                "FROM \"tblBdAnmeldung\" a " +
                "WHERE (\"lngSdSeminarID\"= " + seminar.getSeminarID() + " " +
                       "AND \"lngKurstypID\" != " + lKurstypID + " " +
                       "AND \"lngKurstypID\" NOT IN (" + sMAX_AUSWAHL_KURSTYPEN_AUSNAHMEN + ") " + 
                       "AND (\"blnAnmeldungZuschlag\" is NULL " +
                            (bIGNORE_ALREADY_ASSIGNED ? "" : "OR \"blnAnmeldungZuschlag\" IS NOT NULL AND EXISTS " +
                              "(SELECT * " +
                               "FROM \"tblBdAnmeldung\" a3 " +
                               "WHERE a3.\"lngSdSeminarID\"=a.\"lngSdSeminarID\" " +
                                 "AND a3.\"strMatrikelnummer\"=a.\"strMatrikelnummer\" " +
                                 "AND a3.\"lngKurstypID\"=a.\"lngKurstypID\" " +
                                 "AND a3.\"blnAnmeldungZuschlag\"=TRUE)  ") +
                       ") AND \"strMatrikelnummer\"='" + student.getMatrikelnummer() + "' " +
                       "AND \"intAnmeldungPrioritaet\"=9);");
            
            tmp.next();
            iCount=tmp.getInt("count");
            if(iCount>=lMAX_AUSWAHL_KURSTYPEN){
                out.write("{\"success\":\"false\",\"message\":\"Sie können sich höchstens zu " + lMAX_AUSWAHL_KURSTYPEN + " Kurstypen gleichzeitig anmelden.\"}");
                return;
            }
        }
        
        // Gibt es hier schon fixierte Ergebnisse? 
        // Dann darf nichts mehr geändert werden.
        if(seminar.dbCount("lngKurstypID", "tblBdAnmeldung", 
                "\"lngSdSeminarID\"= " + seminar.getSeminarID() + " and " +
                "\"lngKurstypID\" = " + lKurstypID + " and " +
                "\"blnAnmeldungFixiert\" = 't' and " +
                "\"strMatrikelnummer\"='" + student.getMatrikelnummer() + "'")>0){
            out.write("{\"success\":\"false\",\"message\":\"Sie haben in diesem Kurstyp bereits Kurse zugesagt bekommen, die jetzt nicht mehr verändert werden können.\"}");
            return;
        }
        
        // Bestimme Mindestanzahl zu wählender Kurse
        long lMinKurswahl=-1;
        try{
            lMinKurswahl=Long.parseLong(
                seminar.lookUp("intKurstypKurswahlMin", "tblSdKurstyp", 
                    "\"lngSdSeminarID\"=" + seminar.getSeminarID() + " and \"lngKurstypID\"=" + lKurstypID)
                );
            if( lMinKurswahl <=0 || lMinKurswahl >9 ) throw new Exception("Mist");
        }catch(Exception eKurswahlMinFehler){
            throw new Exception("{\"error\":\"Die Kurswahl konnte nicht gespeichert werden.\",\"errorDebug\":\"Die Anzahl der mindestens zu wählenden Kurse konnte nicht bestimmt werden (oder war 0 oder größer als 9).\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":100}");
        }
        
// ==============================================================================================================
// # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # 
// Voraussetzungen und Konfigurierbares (auslagern als @include?) s.a. ganz unten 
// GS I For Repeat students need a failed GS1 class
try{
    if(lKurstypID==34) if(!hasFailedG1(seminar, student)) throw new Exception("Sorry, Sie können den Wiederholungskurs nur belegen, wenn Sie zuvor durch Tense and Aspect durchgefallen sind.");

    // GS II, W II, T II need completed Grundstudium Sprachpraxis
    if(lKurstypID==10110 || lKurstypID==10120 || lKurstypID==18) if(!isSprachpraxisGrundstudiumOK(seminar, student))  throw new Exception("Für die Wahl dieser Kurse müssen Sie zuerst die Sprachpraxiskurse im Grundstudium absolvieren.");

    // Vorbereitungskurs für Examenskandidaten: 500
    if(lKurstypID==28) if(!isSprachpraxisComplete(seminar, student))  throw new Exception("Für die Wahl von Examensvorbereitungskursen brauchen Sie alle anderen sprachpraktischen Scheine.");

    // New in Fall 2010: check prerequisite for Proseminare
    if(lKurstypID==14 || lKurstypID==15 || lKurstypID==16 || 
                    lKurstypID==10010 || lKurstypID==10020 || lKurstypID==10030 ||
                    lKurstypID==10070 || lKurstypID==10080 || lKurstypID==10090){

            if(!isProseminarClean(lKurstypID, seminar, student)) throw new Exception("Sie haben bereits einen Schein in diesem Proseminar und können es nicht erneut belegen.");
    }
}catch(Exception eSendMsg){
    out.write("{\"success\":\"false\",\"message\":\"" + eSendMsg.getMessage() + "\"}");
    return;
}

// ENDE Voraussetzungen und Konfigurierbares (auslagern als @include?)
// # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # 
// ==============================================================================================================

        // (2) Bilde SQL
        String sSQL ="";
        String sKurse="";
        
        // Die Parameter sind:
        // kurs_id_1 ID der ersten Wahl (Priorität 9)
        // kurs_id_2 ID der zweiten Wahl (Priorität 8)
        // ...
        // kurs_id-n ID der n-ten Wahl (Priorität 10-n)
        for(int ii=0;ii<lMinKurswahl;ii++){
            try{
                sSQL += "INSERT INTO \"tblBdAnmeldung\"(" +
                    "\"lngSdSeminarID\", \"strMatrikelnummer\", \"lngKursID\", \"lngKurstypID\", " +
                    "\"intAnmeldungPrioritaet\", \"lngAnmeldungRandom\", \"blnAnmeldungZuschlag\", " +
                    "\"dtmAnmeldungDatum\", \"blnAnmeldungFixiert\") " +
                    "VALUES (" + seminar.getSeminarID() + ",'" + student.getMatrikelnummer() + "'," + 
                        Long.parseLong(request.getParameter("kurs_id_" + (ii+1))) + "," + lKurstypID + "," + 
                        (9-ii) + ",null,null,current_date,false);";
                sKurse+="," + request.getParameter("kurs_id_" + (ii+1));
            }catch(Exception eNumberFormat){
                throw new Exception("{\"error\":\"Die Kurswahl konnte nicht gespeichert werden.\",\"errorDebug\":\"Der Parameter >kurs_id-" + (ii+1) + "< hatte den Wert " + request.getParameter("kurs_id-" + (ii+1)) + ".\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":100}");
            }
        }
        
        // Stelle sicher, dass für keinen der gewählten 
        // Kurse schon eine Anmeldung im anderen 
        // Kurstyp vorliegt.
        //
        // und, dass keiner der Kurse aus 
        // der Kurswahl ausgenommen ist
        if(sKurse.length()>1){
		if(seminar.dbCount("lngKurstypID", "tblBdAnmeldung", 
					"\"lngSdSeminarID\"=" + seminar.getSeminarID() + " and " +
					"\"strMatrikelnummer\"='" + student.getMatrikelnummer() + "' and " +
					"\"lngKurstypID\" != " + lKurstypID + " and " +
					"\"lngKursID\" IN (" + sKurse.substring(1) + ")")>0){
                    out.write("{\"success\":\"false\",\"message\":\"Einer der Kurse wurde bereits in einem anderen Kurstyp gewählt -- bitte wählen Sie Kurse nur in einem Kurstyp.\"}");
                    return;
                }
		if(seminar.dbCount("lngKursID", "tblBdKurs", 
					"\"lngSdSeminarID\"=" + seminar.getSeminarID() + " and " +
					"\"blnKursScheinanmeldungErlaubt\"!=true and " + 
					"\"lngKursID\" IN (" + sKurse.substring(1) + ")")>0){
                    out.write("{\"success\":\"false\",\"message\":\"Einer der Kurse ist von der Kurswahl ausgenommen, sorry. Bitte wählen Sie neu.\"}");
                    return;
                }

	}
        
        sSQL = "delete from \"tblBdAnmeldung\" where " +
            "\"lngSdSeminarID\"=" + seminar.getSeminarID() + " and " +
            "\"strMatrikelnummer\"='" + student.getMatrikelnummer() + "' and " +
            "\"lngKurstypID\"=" + lKurstypID + ";" + 
                sSQL;
        
        if(!student.sqlExe(sSQL)) throw new Exception("{\"error\":\"Die Kurswahl konnte nicht gespeichert werden.\",\"errorDebug\":\"Die Datenbank hat sich verweigert -- bitte sehen Sie nach den Logdateien.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":100}");
        
	%>{"success":"true"}
<%--
    JSP-Object "student/bemerkung/update"

    SYNOPSIS (German)
    ===========================
    2013, Jun 10, shj
    Erzeugt. 
    
    Lifecycle: UPDATE/ADD
    * Löscht die vorhandenen Anmeldungen zum gegebenen Kurstyp
    * Speichert die übermittelten Anmeldungen
    * Löst Fehler aus, falls
    -- die Anzahl der übermittelten Anmeldungen zu gering;
    -- sonst DB-Fehler

    
    Expected SESSION PROPERTIES
    ===========================
    student     muss identische Matrikelnummer haben, die auch
                als Parameter übergeben wird.

    Expected PARAMETER(S):
    ===========================
    matrikelnummer  [long]
    signup-expected-backend-version [text]
    
    Returns
    =======
    Object:
    {"success":"true"} ODER, bei einer Auswahl von 
    Kursen, die den konfigurierten Möglichkeiten nicht 
    entspricht,
    {"success":"false", "message":"<info>"]

    Sample Usage
    ============
    
--%>           


<%!boolean hasFailedG1(de.shj.UP.HTML.HtmlSeminar sem, de.shj.UP.logic.StudentData student) throws Exception{
		int iLEISTUNGSID_GS1=5;
		ResultSet rs = sem.sqlQuery("select n.\"intNoteID\" " + 
				"FROM \"tblBdStudentXLeistung\" x, \"tblSdNote\" n " + 
				"WHERE x.\"lngSdSeminarID\" = n.\"lngSdSeminarID\" AND " + 
				"x.\"intNoteID\" = n.\"intNoteID\" " + 
				"AND n.\"lngSdSeminarID\" = " + sem.getSeminarID() + " " + 
				"AND n.\"blnNoteBestanden\" = False AND " + 
				"x.\"lngSdSeminarID\" = " + sem.getSeminarID() + " " + 
				"AND x.\"strMatrikelnummer\" = '" + student.getMatrikelnummer() + "' AND " + 
				"x.\"lngLeistungsID\" = " + iLEISTUNGSID_GS1 + ";");
		boolean bReturn = rs.next();
		rs.close();
		return bReturn;
}

boolean isSprachpraxisGrundstudiumOK(de.shj.UP.HTML.HtmlSeminar sem, de.shj.UP.logic.StudentData student) throws Exception{
	boolean bReturn=false;
	
	// Master raus:
	if(student.isMaster()) return true;
	
	if(student.getMatrikelnummer().equals("2517916")) return true;
	// Jennifer Staab, Julia Schaller extra:
	//if(student.getMatrikelnummer().equals("2517916")||student.getMatrikelnummer().equals("2433439")||student.getMatrikelnummer().equals("2496579")) return true;
	
	// Vermeire, Shoesmith, Ergin, Schaub extra
	// if(student.getMatrikelnummer().equals("2876336")||student.getMatrikelnummer().equals("3011800")||student.getMatrikelnummer().equals("2945191")||student.getMatrikelnummer().equals("2911255")) return true;
	
	// Staatsexamen und Magister: GS1, T1, W1
	long lFachID=student.getFachID(sem.getSeminarID());
	if(lFachID<9999){
		ResultSet rs = sem.sqlQuery("select " + 
				"count(x.\"lngLeistungsID\") as grund " + 
				"FROM \"tblBdStudentXLeistung\" x, \"tblSdNote\" n " + 
				"WHERE x.\"lngSdSeminarID\" = n.\"lngSdSeminarID\" AND " + 
				"x.\"intNoteID\" = n.\"intNoteID\" AND " + 
				"n.\"lngSdSeminarID\" = " + sem.getSeminarID() + " AND " + 
				"n.\"blnNoteBestanden\" = TRUE AND " + 
				"x.\"lngSdSeminarID\" = " + sem.getSeminarID() + " AND " + 
				"x.\"strMatrikelnummer\" = '" + student.getMatrikelnummer() + "' AND " + 
				"(x.\"lngLeistungsID\" = 5 or x.\"lngLeistungsID\" = 12 or x.\"lngLeistungsID\" = 13);");
		try{
			rs.next();
			bReturn = rs.getInt("grund") >=3;
		}catch(Exception eWhat){}
		
	// BA alt sowie GymPO: GS1, W1 
	}else if(lFachID<100000 || lFachID==200822){
		ResultSet rs = sem.sqlQuery("select " + 
				"count(x.\"lngLeistungsID\") as grund " + 
				"FROM \"tblBdStudentXLeistung\" x, \"tblSdNote\" n " + 
				"WHERE x.\"lngSdSeminarID\" = n.\"lngSdSeminarID\" AND " + 
				"x.\"intNoteID\" = n.\"intNoteID\" AND " + 
				"n.\"lngSdSeminarID\" = " + sem.getSeminarID() + " AND " + 
				"n.\"blnNoteBestanden\" = TRUE AND " + 
				"x.\"lngSdSeminarID\" = " + sem.getSeminarID() + " AND " + 
				"x.\"strMatrikelnummer\" = '" + student.getMatrikelnummer() + "' AND " + 
				"(x.\"lngLeistungsID\" = 5 or x.\"lngLeistungsID\" = 12);");
		try{
			rs.next();
			bReturn = rs.getInt("grund") >=2;
		}catch(Exception eWhat){}
	// BA neu: nur W1
	}else{
		ResultSet rs = sem.sqlQuery("select " + 
				"count(x.\"lngLeistungsID\") as grund " + 
				"FROM \"tblBdStudentXLeistung\" x, \"tblSdNote\" n " + 
				"WHERE x.\"lngSdSeminarID\" = n.\"lngSdSeminarID\" AND " + 
				"x.\"intNoteID\" = n.\"intNoteID\" AND " + 
				"n.\"lngSdSeminarID\" = " + sem.getSeminarID() + " AND " + 
				"n.\"blnNoteBestanden\" = TRUE AND " + 
				"x.\"lngSdSeminarID\" = " + sem.getSeminarID() + " AND " + 
				"x.\"strMatrikelnummer\" = '" + student.getMatrikelnummer() + "' AND " + 
				"(x.\"lngLeistungsID\" = 12);");
		try{
			rs.next();
			bReturn = rs.getInt("grund") >=1;
		}catch(Exception eWhat){}
	}
	
	return bReturn;
}

boolean isSprachpraxisComplete(de.shj.UP.HTML.HtmlSeminar sem, de.shj.UP.logic.StudentData student) throws Exception{
	boolean bReturn=false;
	
	if(";2607188;2687590;2557207;2796694;2517916;2774444;2715710;2911255;2459443;2945676;2452755;2607902;".contains(";" + student.getMatrikelnummer() + ";")) return true;

	// GS1, T1, W1, GS II, TII, W II
	ResultSet rs = sem.sqlQuery("select " + 
				"count(x.\"lngLeistungsID\") as grund " + 
				"FROM \"tblBdStudentXLeistung\" x, \"tblSdNote\" n " + 
				"WHERE x.\"lngSdSeminarID\" = n.\"lngSdSeminarID\" AND " + 
				"x.\"intNoteID\" = n.\"intNoteID\" AND " + 
				"n.\"lngSdSeminarID\" = " + sem.getSeminarID() + " AND " + 
				"n.\"blnNoteBestanden\" = TRUE AND " + 
				"x.\"lngSdSeminarID\" = " + sem.getSeminarID() + " AND " + 
				"x.\"strMatrikelnummer\" = '" + student.getMatrikelnummer() + "' AND " + 
				"(x.\"lngLeistungsID\" = 5 or x.\"lngLeistungsID\" = 12 or x.\"lngLeistungsID\" =13 or "+
				"x.\"lngLeistungsID\" =18 or x.\"lngLeistungsID\" =14 or x.\"lngLeistungsID\" =10110 or x.\"lngLeistungsID\" =19 or x.\"lngLeistungsID\" =10120);");
	try{
		rs.next();
		
		bReturn = rs.getInt("grund") >=6;
	}catch(Exception eWhat){}
	return bReturn;
}

boolean isProseminarClean(long lKurstypID, de.shj.UP.HTML.HtmlSeminar sem, de.shj.UP.logic.StudentData student) throws Exception{
	
	// BA 75% können PS II SW Periode, PS II LW/KW doppelt machen --
	// sparen wir uns da also den Test.
	
	// BA 75% Anglistik sind:
	// - 83907
	// - 200216
	if(lKurstypID==10010 || lKurstypID==15 ||lKurstypID==10090){
		if(student.getStudentFach1()==83907 || student.getStudentFach1()==200216) return true;
	}
	
	// LehramtskandidatInnen brauchen
	// zwei Landeskundescheine (10090, 10080)
	if(student.getStudentZUVZiel()==125 && (lKurstypID==10090 || lKurstypID==10080)) return true;
	
	// LehramtskandidatInnen können fürs Wahlmodul PS I mehrmals belegen:
	if(student.getFachID(sem.getSeminarID())==200822 && (lKurstypID==14 || lKurstypID==16|| lKurstypID==10070|| lKurstypID==10080|| lKurstypID==10090)) return true;
        
	long lLeistungID = Long.parseLong(sem.lookUpSHJ("lngKurstypLeistungsID", "tblSdKurstyp", "\"lngSdSeminarID\"=" + sem.getSeminarID() + " and \"lngKurstypID\"=" + lKurstypID));
	ResultSet rs = sem.sqlQuery("select n.\"intNoteID\", n.\"blnNoteBestanden\" " + 
			"FROM \"tblBdStudentXLeistung\" x, \"tblSdNote\" n " + 
			"WHERE x.\"lngSdSeminarID\" = n.\"lngSdSeminarID\" AND " + 
			"(x.\"intNoteID\" = n.\"intNoteID\" or x.\"intNoteID\" is null) " + 
			"AND n.\"lngSdSeminarID\" = " + sem.getSeminarID() + " AND " + 
			"x.\"lngSdSeminarID\" = " + sem.getSeminarID() + " " + 
			"AND x.\"strMatrikelnummer\" = '" + student.getMatrikelnummer() + "' AND " + 
			"x.\"lngLeistungsID\" = " + lLeistungID + ";");
	boolean bReturn = false;
	
	if(rs.next()){
		// es gibt einen Eintrag diesen Kurstyp
		// Das Proseminar darf nur belegt werden, 
		// wenn das alte PS nicht bestanden war.
		bReturn = (!rs.getBoolean("blnNoteBestanden"));
	}else{
		bReturn=true;
		//System.out.println("Hier gewesen, also kein 'next'...");
	}
	
	rs.close();
	return bReturn;
}


%>