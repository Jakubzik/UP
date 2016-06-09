<%@page contentType="text/json" pageEncoding="UTF-8" import="de.shj.UP.beans.algorithm.AnmeldungIterator, java.util.Date, de.shj.UP.logic.Kurs,java.sql.ResultSet,de.shj.UP.logic.AktuellesIterator,java.text.SimpleDateFormat, de.shj.UP.data.Dozent,de.shj.UP.data.shjCore" session="true" isThreadSafe="false"  errorPage="../../../error.jsp" %>
<jsp:useBean id="seminar" scope="session" class="de.shj.UP.logic.SeminarData" />
<jsp:useBean id="student" scope="session" class="de.shj.UP.beans.config.student.StudentBean" /><%--   
        ==================== ==================== ==================== ==================== ====================
        SCHNITTSTELLE:
        - Parameter matrikelnummer muss übergeben sein, und 
        -       numerisch, und
        -       ggf. identisch mit initialisiertem Student
          (sonst Fehlermeldung)
        ==================== ==================== ==================== ==================== ====================
--%>
<%@include file="../../../../fragments/checkVersion.jsp" %>
<%@include file="../../../../fragments/checkLoginStudent.jsp" %>
<%@include file="../../../../fragments/conf_kurswahl_tausch.jsp" %>
<%
        long lERR_BASE=108000 + 400;    // Tausch + Add
        
        // Bei diesen beiden Fehlermeldungen 
        // (see SQL Function anmeldung_swap) macht 
        // es Sinn, eine Vormerkung zu setzen:
        final String g_sERR1            = "Für einen Kursplatztausch sind im neuen Kurs nicht genug Plätze vorhanden, sorry.";
        final String g_sErr1Indicator   = "Kursplatztausch sind im neuen Kurs nicht genug";
        final String g_sERR2 = "Es gibt im aktuellen Kurs nur % Teilnehmer. Ein Tausch ist erst ab sechs Teilnehmern möglich";
        final String g_sErr2Indicator   = "Tausch ist erst ab";
        
	// =========================================================
	// Interface
	// Falls Zeitfenster falsch, Fehler auslösen
	if(!bTausch) throw new Exception("Proseminartausch nicht offen");
	
	long lKurstypID=-1;		// 
	long lKursID=-1;	        // Alter Kurs
        long lKurstypID_Neu=-1;
        long lKursID_Neu=-1;	        // Neuer Kurs
        try{
            lKurstypID=Long.parseLong(request.getParameter("kurstyp_id"));
        }catch(Exception eNoKurstypID){
            throw new Exception("{\"error\":\"Der Kurs konnte nicht getauscht werden.\",\"errorDebug\":\"Der erforderliche Parameter >kurstyp_id< hatte den Wert '" + request.getParameter ("kurstyp_id") + "', muss aber numerisch sein.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":100}");
        }
        try{
            lKurstypID_Neu=Long.parseLong(request.getParameter("kurstyp_id_neu"));
        }catch(Exception eNoKurstypID_neu){
            throw new Exception("{\"error\":\"Der Kurs konnte nicht getauscht werden.\",\"errorDebug\":\"Der erforderliche Parameter >kurstyp_id_neu< hatte den Wert '" + request.getParameter ("kurstyp_id_neu") + "', muss aber numerisch sein.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":100}");
        }
        try{
            lKursID=Long.parseLong(request.getParameter("kurs_id"));
        }catch(Exception eNoKursID){
            throw new Exception("{\"error\":\"Der Kurs konnte nicht getauscht werden.\",\"errorDebug\":\"Der erforderliche Parameter >kurs_id< hatte den Wert '" + request.getParameter ("kurs_id") + "', muss aber numerisch sein.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":100}");
        }
        try{
            lKursID_Neu=Long.parseLong(request.getParameter("kurs_id_neu"));
        }catch(Exception eNoKursID_neu){
            throw new Exception("{\"error\":\"Der Kurs konnte nicht getauscht werden.\",\"errorDebug\":\"Der erforderliche Parameter >kurs_id_neu< hatte den Wert '" + request.getParameter ("kurs_id_neu") + "', muss aber numerisch sein.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":100}");
        }
        
%>

<%


	// TO DO
	// - Analysiere Procs ("Kursplatz_delete", "Anmeldung_Swap")
	// 1.) Tausch Kurstypübergreifend:
	//    a) Kombobox zum Wechsel des Kurstyps
	//
	// [ALT: 1.) Interferenzen bei gleichzeitiger Kurswahl testen]



		
	// =========================================================
	// USER-INPUT
	//
	// Es gibt folgende Aktionswünsche:
	// - {1} cmdDeleteVormerkung: eine Vormerkung soll zurückgenommen werden
	// - {3} cmdSwapchoice: der übergebene Kurs soll getauscht werden
	// - {4} cmdSwapNote:	es wird ein Tauschwunsch notiert.	
	// - {5} cmdSwap:		es soll ein konkreter Tausch durchgeführt werden



        if(sKurseMitTauschverbot.contains(request.getParameter("kurs_id"))) throw new Exception("Trying to swap blocked Kurs -- sorry, disallowed");
        if(sKurseMitTauschverbot.contains(request.getParameter("kurs_id_neu"))) throw new Exception("Trying to swap blocked Kurs -- sorry, disallowed");

         try{
		seminar.execProc("Anmeldung_Swap",
				"long", String.valueOf(seminar.getSeminarID()),
				"String", student.getMatrikelnummer(),
				"long", request.getParameter("kurstyp_id"), 
				"long", request.getParameter("kurs_id_neu"),
				"long", request.getParameter("kurs_id"),
				"long", request.getParameter("kurstyp_id_neu"));
         }catch(Exception eProblem){
             if(eProblem.getMessage().contains(g_sErr1Indicator) || eProblem.getMessage().contains(g_sErr2Indicator)){
                 System.out.println("W: Fehler beim Tauschen: " + eProblem.getMessage() + "\n...Speichere Vormerkung");
                 if(!seminar.sqlExe("insert into \"tblBdAnmeldungSwap\" (" +
                                            "\"lngSdSeminarID\", \"strMatrikelnummer\",\"lngKursID\", " +
                                            "\"lngKurstypID\",\"lngKursIDWunsch\",\"lngKurstypIDWunsch\",\"blnAnmeldungSwapZuschlag\", " +
                                            "\"dtmAnmeldungSwapDatum\") values (" + 
                                            seminar.getSeminarID() + ", '" + student.getMatrikelnummer() + "', " + 
                                            lKursID + "," + lKurstypID + "," + lKursID_Neu + "," + lKurstypID_Neu + ", false, now());")){
                          out.write("{\"success\":\"false\",\"message\":\"Sorry, der Tausch konnte nicht durchgeführt werden, und auch "
                                  + "eine Vormerkung konnte nicht gespeichert werden. Liegt möglicherweise bereits eine "
                                  + "entsprechende Vormerkung vor?\"}");
                          return;
                 }else{
                     System.out.println("...OK, Vormerkung gespeichert\n\n");
                     out.write("{\"success\":\"false\",\"message\":\"Die Teilnehmerzahlen lassen im Moment keinen Tausch zu. Aber Ihr Tauschwunsch " + 
                            "ist notiert und wird automatisch umgesetzt, sobald es die Teilnehmerzahlen erlauben.\"}");
                     return;}
             }else{
                 System.out.println("W: Fehler beim Tauschen: " + eProblem.getMessage() + "\n...Vormerkung sinnlos, melde Fehler an Nutzer");
                 out.write("{\"success\":\"false\",\"message\":\"Sorry, der Tausch konnte nicht durchgeführt werden. Die Datenbank " + 
                         "meldete: '" + eProblem.getMessage() + "'.\"}");
             }
         }
%>


{"success":"true"}
<%--
    JSP-Object "student/signup/delete"

    @TODO
    ===========================

    SYNOPSIS (German)
    ===========================
    2013, June 11, shj
    Erzeugt. 
    
    Üblicher Lifecycle: Delete

    Expected SESSION PROPERTIES
    ===========================

    Expected PARAMETER(S):
    ===========================
    
    Returns
    =======


    Sample Usage
    ============
    
--%>
<%! 
	long getCount(de.shj.UP.logic.SeminarData sem, long lKursID){
		return sem.dbCount("strMatrikelnummer", "tblBdAnmeldung", 
				"\"lngSdSeminarID\"=" + sem.getSeminarID() + 
				" and \"lngKursID\"=" + lKursID + " and \"blnAnmeldungZuschlag\"=true");
	}

	boolean overlaps(de.shj.UP.logic.StudentData student, long lKursID, Object oSwapCourse) throws Exception{
		if(oSwapCourse==null) return overlaps(student,lKursID);
		
		Kurs k = new Kurs(student.getSeminarID(),lKursID);
		long lOtherKursID=Long.parseLong((String)oSwapCourse);
		if(k.getTerminOpt()!=k.TERMIN_ONE_ONLY) return false;
		ResultSet rIndicator=student.sqlQuery("select * from \"tblBdAnmeldung\" a, \"tblBdKurs\" k where " + 
				"a.\"lngSdSeminarID\"=" + student.getSeminarID() + " and " + 
				"a.\"strMatrikelnummer\"='" + student.getMatrikelnummer() + "' and " + 
				"a.\"lngKursID\" != " + lKursID + " and a.\"lngKursID\" != " + lOtherKursID + " and " + 
				"a.\"blnAnmeldungZuschlag\"='t' and " +
				"k.\"lngKursID\"=a.\"lngKursID\" and " + 
				"k.\"lngSdSeminarID\"=a.\"lngSdSeminarID\" and " +
				"k.\"strKursTag\"='" + k.getKursTag() + "' and " + 
				"(TIME '" + k.getKursBeginn() + "', TIME '" + k.getKursEnde() + "') overlaps (\"dtmKursBeginn\", \"dtmKursEnde\");");
		boolean bReturn= rIndicator.next();
		rIndicator.close();
		return bReturn;		
	}
	
	boolean overlaps(de.shj.UP.logic.StudentData student, long lKursID) throws Exception{
		// Problem: soll ein Kurs getauscht werden,
		// weiß die Datenbank noch nix davon, damit 
		// ist dessen Zeit immer noch gesperrt und kein
		// Kurs kann gegen einen gleichzeitigen Kurs getauscht werden.
	Kurs k = new Kurs(student.getSeminarID(),lKursID);
	if(k.getTerminOpt()!=k.TERMIN_ONE_ONLY) return false;
	ResultSet rIndicator=student.sqlQuery("select * from \"tblBdAnmeldung\" a, \"tblBdKurs\" k where " + 
			"a.\"lngSdSeminarID\"=" + student.getSeminarID() + " and " + 
			"a.\"strMatrikelnummer\"='" + student.getMatrikelnummer() + "' and " + 
			"a.\"lngKursID\" != " + lKursID + " and " + 
			"a.\"blnAnmeldungZuschlag\"='t' and " +
			"k.\"lngKursID\"=a.\"lngKursID\" and " + 
			"k.\"lngSdSeminarID\"=a.\"lngSdSeminarID\" and " +
			"k.\"strKursTag\"='" + k.getKursTag() + "' and " + 
			"(TIME '" + k.getKursBeginn() + "', TIME '" + k.getKursEnde() + "') overlaps (\"dtmKursBeginn\", \"dtmKursEnde\");");
	boolean bReturn= rIndicator.next();
	rIndicator.close();
	return bReturn;
	}
	
	// HTML-Optionen-Liste für den Wechsel
	// des Kurstys; lKurstypID gibt die aktive
	// ID an, die nicht erneut zur Wahl gestellt wird.
	// lKurstypIDSwitched wird für die Vorauswahl benötigt:
	// es ist die gerade gewählte Kategorie.
	//
	// Komplex: es werden keine Kurstypen angezeigt, 
	// in denen es schon einen Zuschlag gibt (außer
	// dem ursprünglichen Kurstyp). Es soll vermieden werden,
	// dass man durch Tausch zwei Kurse eines Kurstyps
	// erhalten kann. [Das wird im not exists geregelt].
	String getKurstypOptions(de.shj.UP.logic.SeminarData sem, String sMatrikelnummer, long lKurstypID, long lKurstypIDSwitched) throws Exception{
		String sReturn="";
		long lExclude = (lKurstypIDSwitched<0) ? lKurstypID : lKurstypIDSwitched;
		long lTmp = -1;
		ResultSet rOptions = sem.sqlQuery("select \"strKurstypBezeichnung\", \"lngKurstypID\" from " + 
				"\"tblSdKurstyp\" AS kt where " + 
				"\"lngSdSeminarID\"=" + sem.getSeminarID() + " and " + 
				"\"lngKurstypID\" IN (14,15,16,10010,10020,10030,10070,10080,10090,31,20300,20010) and " +
				"(not exists (select * from \"tblBdAnmeldung\" " + 
						"where " +
							"\"lngSdSeminarID\"= " + sem.getSeminarID() + " and " + 
							"\"blnAnmeldungZuschlag\"=true and " + 
							"\"lngKurstypID\"=kt.\"lngKurstypID\" and " + 
							"\"strMatrikelnummer\"='" + sMatrikelnummer + "'" +
						") or \"lngKurstypID\"=" + lKurstypID + ") " +
				"order by \"strKurstypBezeichnung\";");
		
		while (rOptions.next()){
			lTmp = rOptions.getLong("lngKurstypID");
			sReturn += "<option " + ((lTmp==lExclude) ? "selected='selected'" : "") + " value='" + lTmp + "'>" + rOptions.getString("strKurstypBezeichnung") + "</option>\n";
		}
		return sReturn;
	}
%>