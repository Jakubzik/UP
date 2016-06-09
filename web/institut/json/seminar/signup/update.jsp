<%--
    JSP-Object "seminar/signup/update"
    @Revision: Mar 3, 2014 -- hj

    @TODO
    ===========================
    Kompletten Legacy-Code 
    analysieren und unter 
    Kontrolle bringen ...

    SYNOPSIS (German)
    ===========================
    2014, Mar 3, shj
    Erzeugt. 
    
    Es gibt zwei Optionen, die durch detail_request 
    unterschieden werden.

    Ist (1) detail_request == null
    --------------------------------------------------
    Teilt die Studierenden Kursen zu (d.h. setzt 
    "tblBdAnmeldung.blnAnmeldungZuschlag").

    Durch den Parameter "wunsch" wird angegeben, 
    wie die Verteilung vorgenommen wird:
    
    Ist wunsch=100, wir jeder Studierende schlicht 
    in den am höchsten priorisierten Kurse ein-
    gewählt.

    Je kleiner wunsch ist, desto mehr drängt der 
    Algorithmus auf gleichmäßige Teilnehmerzahlen.

    Ist (2) detail_request != null
    --------------------------------------------------
    Wird dem Studierenden (matrikelnummer) ein Platz im
    Kurs kurs_id der Typs kurstyp_id zugewiesen (grant=true) 
    oder entzogen (grant=false).

    Wenn nicht explizit 'allow_multiple' gesetzt ist, 
    werden alle anderen Zuweisungen dieses Studierenden 
    in diesem Kurstyp gelöscht.

    Expected SESSION PROPERTIES
    ===========================
    user        muss initialisiert sein (AccessLevel >= 500),

    Expected PARAMETER(S):
    ===========================
    kurstyp_id  [long]
    wunsch      [int, 0<=wunsch<=100]

    detail_request [flag, null or not null]
    matrikelnummer [String]
    kurs_id        [long]
    grant          [true|false]
    allow_multiple [flag]
    
    Returns
    =======

--%>
<%@page import="org.apache.commons.beanutils.DynaBean"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Enumeration"%>
<%@page import="de.shj.UP.logic.SeminarData"%>
<%@page import="de.shj.UP.algorithm.SignUp"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page contentType="text/json" pageEncoding="UTF-8" import="java.sql.ResultSet,de.shj.UP.data.Dozent,de.shj.UP.data.shjCore" session="true" isThreadSafe="false"  errorPage="../../error.jsp" %>
<jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" />
<jsp:useBean id="sd" scope="session" class="de.shj.UP.util.SessionData" />
<jsp:useBean id="seminar" scope="session" class="de.shj.UP.logic.SeminarData" />
<%@include file="../../../fragments/checkVersion.jsp" %>
<%@include file="../../../fragments/checkLogin.jsp" %>    
<%@include file="../../../fragments/checkAccess500.jsp" %>
<%! long lERR_BASE; HtmlSeminar sem;String m_sDebug;
	long lKursToFill;
	long lKursFillBy;

	long lKursToEmpty;
	long lKursEmptyBy;
	
	long lKursToCut;%>
<%lERR_BASE=211000 + 100;sem=seminar;long lKurstypID=-1;    // Anmeldung + Get
try{
    lKurstypID=Long.parseLong(request.getParameter("kurstyp_id"));
}catch(Exception eProblem){
    throw new Exception("{\"error\":\"Die Kursbelegung kann nicht berechnet werden, ohne dass ein Kurstyp gewählt ist.\"" + 
                    ",\"errorDebug\":\"Der Parameter >kurstyp_id< (für den Kurstyp) hat den nicht-numerischen Wert " + request.getParameter("kurstyp_id") + ".\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":10}");
}

if(request.getParameter("detail_request")!=null){
    long lKursID=-1; String sMatrikelnummer=null;
    try{
        lKursID=Long.parseLong(request.getParameter("kurs_id"));
    }catch(Exception eProblem){
        throw new Exception("{\"error\":\"Die Zuteilung zu einem Kurs ist nicht hinreichend festgelegt.\"" + 
                        ",\"errorDebug\":\"Der Parameter >kurs_id< hat den nicht-numerischen Wert " + request.getParameter("kurs_id") + ".\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":10}");
    }   
    try{
        sMatrikelnummer=String.valueOf(Long.parseLong(request.getParameter("matrikelnummer")));
    }catch(Exception eProblem){
        throw new Exception("{\"error\":\"Die Zuteilung zu einem Kurs ist nicht hinreichend festgelegt.\"" + 
                        ",\"errorDebug\":\"Der Parameter >matrikelnummer< hat den nicht-numerischen Wert " + request.getParameter("matrikelnummer") + ".\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":10}");
    }

    //@TODO: Fehlermeldungen sqlExe abfangen und weiterleiten.
    if(request.getParameter("allow_multiple")==null){
        sem.sqlExe("update \"tblBdAnmeldung\" set \"blnAnmeldungZuschlag\"=false where " + 
                "\"lngSdSeminarID\"=" + sem.getSeminarID() + 
                " and \"lngKurstypID\"=" + lKurstypID + 
                " and \"strMatrikelnummer\"='" + sMatrikelnummer + "' and \"blnAnmeldungFixiert\"=false;");
    }
    sem.sqlExe("update \"tblBdAnmeldung\" set \"blnAnmeldungZuschlag\"=" + (shjCore.normalize(request.getParameter("grant")).indexOf("t")>=0 ? "true" : "false") + " where " + 
        "\"lngSdSeminarID\"=" + sem.getSeminarID() + 
        " and \"lngKurstypID\"=" + lKurstypID + 
        " and \"lngKursID\"=" + lKursID + 
        " and \"strMatrikelnummer\"='" + sMatrikelnummer + "' and \"blnAnmeldungFixiert\"=false;");

}else{
    long lWunsch=-1;
    lKursToFill=-1;
    lKursFillBy=-1;
    lKursToEmpty=-1;
    lKursEmptyBy=-1;
    lKursToCut=-1;
    try{
        lWunsch=Long.parseLong(request.getParameter("wunsch"));
    }catch(Exception eProblem){
        throw new Exception("{\"error\":\"Die Kursbelegung kann nicht berechnet werden, ohne dass Parameter festgelegt sind.\"" + 
                        ",\"errorDebug\":\"Der Parameter >wunsch< hat den nicht-numerischen Wert " + request.getParameter("wunsch") + ".\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":10}");
    }

    // Standardberechnung durchführen, falls gewünscht
    // (Fixierte werden ignoriert)
    if(request.getParameter("wunsch").equals("100")){
            smartGrantAll(lKurstypID);
    }else{
        if(lWunsch>=0){
            SignUp algorithm=new SignUp(seminar.getSeminarID());
            int iWish = Integer.parseInt(request.getParameter("wunsch"));
            algorithm.setPhaseOneOverbookPercent((int)iWish*(-1));
            algorithm.setPhaseTwoOverbookPercent((int)iWish);
            algorithm.doDistribute(lKurstypID);
        }else{
            getKursToFill(request);
            if(lKursToFill>0){
                    fill(getFixSQL(request), Long.parseLong(request.getParameter("kurstyp_id")));
            }else{
                    getKursToCut(request);			

                    if(lKursToCut>0){
                            cut(Long.parseLong(request.getParameter("kurstyp_id")));
                    }else{
                            getKursToEmpty(request);
                            if(lKursToEmpty>0){
                                    empty(getFixSQL(request), Long.parseLong(request.getParameter("kurstyp_id")));
                            }
                    }
            }
        }
      }
}
%>
<%!
// Grant-all gibt einfach allen Studierenden des Kurstyps ihren
// bevorzugten Kurs; dabei kommte es aber zu Überlappungen.
// smartGrantAll kümmert sich auch um die Überlappungen, sofern
// möglich.
void smartGrantAll(long lKurstypID_IN) throws Exception{
	sem.sqlExe("update \"tblBdAnmeldung\" set \"blnAnmeldungZuschlag\"='f' where \"lngSdSeminarID\"=" + sem.getSeminarID() + " and \"lngKurstypID\"=" + lKurstypID_IN + " and \"blnAnmeldungFixiert\"='f';");
	sem.sqlExe("update \"tblBdAnmeldung\" set \"blnAnmeldungZuschlag\"='t' where \"lngSdSeminarID\"=" + sem.getSeminarID() + " and \"lngKurstypID\"=" + lKurstypID_IN + " and \"intAnmeldungPrioritaet\"=9 and \"blnAnmeldungFixiert\"='f';");
	
	int iPrio=9;
	String sMatrikelnummer="";
	long lOverlaps=deleteOverlaps(lKurstypID_IN);
	ResultSet r=null;
	String sSQLBulk="";
	m_sDebug += "smartGrantAll(" + lKurstypID_IN + ")\n<br />";
	while((lOverlaps>0 && --iPrio>0)){
		m_sDebug += "\n\n<br /><br />__Prio " + iPrio + ", " + lOverlaps + " Overlaps...";
		r=getSadStudent(lKurstypID_IN);
		while(r.next()){
			sMatrikelnummer=r.getString("strMatrikelnummer");
//			m_sDebug += "_____Sad: " + sMatrikelnummer + ", granting Prio " + iPrio + "\n<br />";
//			m_sDebug += "__________Del: " + sem.sqlExe("update \"tblBdAnmeldung\" set \"blnAnmeldungZuschlag\"='f' where \"lngSdSeminarID\"=" + 
//					sem.getSeminarID() + " and \"lngKurstypID\"=" + 
//					lKurstypID_IN + " and \"blnAnmeldungFixiert\"='f' and " +
//					"\"strMatrikelnummer\"='" + sMatrikelnummer + "';");
//			m_sDebug += "\n<br />_____Happy: " + sMatrikelnummer + ", granting Prio " + iPrio + "...";sem.sqlExe("update \"tblBdAnmeldung\" set \"blnAnmeldungZuschlag\"='t' where " +
//					"\"lngSdSeminarID\"=" + sem.getSeminarID() + " and \"lngKurstypID\"=" + 
//					lKurstypID_IN + " and \"intAnmeldungPrioritaet\"=" + iPrio + " and \"blnAnmeldungFixiert\"='f'" + 
//					" and \"strMatrikelnummer\"='" + sMatrikelnummer+ "';");

			sSQLBulk+= "update \"tblBdAnmeldung\" set \"blnAnmeldungZuschlag\"='f' where \"lngSdSeminarID\"=" + 
								sem.getSeminarID() + " and \"lngKurstypID\"=" + 
								lKurstypID_IN + " and \"blnAnmeldungFixiert\"='f' and " +
								"\"strMatrikelnummer\"='" + sMatrikelnummer + "';" +
								
								"update \"tblBdAnmeldung\" set \"blnAnmeldungZuschlag\"='t' where " +
								"\"lngSdSeminarID\"=" + sem.getSeminarID() + " and \"lngKurstypID\"=" + 
								lKurstypID_IN + " and \"intAnmeldungPrioritaet\"=" + iPrio + " and \"blnAnmeldungFixiert\"='f'" + 
								" and \"strMatrikelnummer\"='" + sMatrikelnummer+ "';";
		}
		r.close();
		sem.sqlExeSingle(sSQLBulk);
		lOverlaps=deleteOverlaps(lKurstypID_IN);
	}
}

int deleteOverlaps(long lKurstypID) throws Exception{
		int iHowMany=0;
		ResultSet rOver= sem.sqlQuery(
				"select a.\"strMatrikelnummer\", x.\"lngKurstypID\", x.\"lngKursID\" from \"tblBdAnmeldung\" a, \"tblBdKurs\" k, \"tblBdKursXKurstyp\" x where " + 
				"a.\"lngSdSeminarID\"=" + sem.getSeminarID() + " and " +
				"k.\"lngSdSeminarID\"=" + sem.getSeminarID() + " and " +
				"x.\"lngSeminarID\"=" + sem.getSeminarID() + " and " +
				"k.\"lngKursID\"=x.\"lngKursID\" and " + 
				"a.\"blnAnmeldungZuschlag\"='t' and " +
				"a.\"lngKurstypID\"=x.\"lngKurstypID\" and " + 
				"a.\"lngKursID\"=x.\"lngKursID\" and " +
				"a.\"lngKurstypID\"=" + lKurstypID + " and " +
				"exists(select " +
				"((k.\"dtmKursBeginn\",k.\"dtmKursEnde\") OVERLAPS (k2.\"dtmKursBeginn\", k2.\"dtmKursEnde\"))," + 
				"((k.\"dtmKursBeginn2\",k.\"dtmKursEnde2\") OVERLAPS (k2.\"dtmKursBeginn2\", k2.\"dtmKursEnde2\"))," +
				"((k.\"dtmKursBeginn\",k.\"dtmKursEnde\") OVERLAPS (k2.\"dtmKursBeginn2\", k2.\"dtmKursEnde2\"))," +
				"((k.\"dtmKursBeginn2\",k.\"dtmKursEnde2\") OVERLAPS (k2.\"dtmKursBeginn\", k2.\"dtmKursEnde\")) from \"tblBdAnmeldung\" a2, \"tblBdKurs\" k2, \"tblBdKursXKurstyp\" x2 where " + 
				"a2.\"lngSdSeminarID\"=" + sem.getSeminarID() + " and " + 
				"k2.\"lngSdSeminarID\"=" + sem.getSeminarID() + " and " +
				"x2.\"lngSeminarID\"=" + sem.getSeminarID() + " and " +
				"k2.\"lngKursID\"=x2.\"lngKursID\" and " + 
				"a2.\"blnAnmeldungZuschlag\"='t' and " +
				"a2.\"lngKurstypID\"=x2.\"lngKurstypID\" and " + 
				"a2.\"lngKursID\"=x2.\"lngKursID\" and " +
				"a2.\"strMatrikelnummer\"=a.\"strMatrikelnummer\" and " + 
				"k2.\"lngKursID\" != k.\"lngKursID\" and " +
				"(" +
				  "(k2.\"strKursTag\"=k.\"strKursTag\" and " + 
				  "(((k.\"dtmKursBeginn\",k.\"dtmKursEnde\") OVERLAPS (k2.\"dtmKursBeginn\", k2.\"dtmKursEnde\"))='t')) or " + 

				  "(k2.\"strKursTag2\"=k.\"strKursTag2\" and " +
				  "(((k.\"dtmKursBeginn2\",k.\"dtmKursEnde2\") OVERLAPS (k2.\"dtmKursBeginn2\", k2.\"dtmKursEnde2\"))='t')) or " + 

				  "(k2.\"strKursTag2\"=k.\"strKursTag\" and " +
				  "(((k.\"dtmKursBeginn\",k.\"dtmKursEnde\") OVERLAPS (k2.\"dtmKursBeginn2\", k2.\"dtmKursEnde2\"))='t')) or " + 

				  "(k.\"strKursTag2\"=k2.\"strKursTag\" and " +
				  "(((k.\"dtmKursBeginn2\",k.\"dtmKursEnde2\") OVERLAPS (k2.\"dtmKursBeginn\", k2.\"dtmKursEnde\"))='t'))" +
				")) order by \"strMatrikelnummer\";");
		
		String sDelSQL="";
		while(rOver.next()){
			
			iHowMany++;
			sDelSQL += "update \"tblBdAnmeldung\" set \"blnAnmeldungFixiert\"='f', \"blnAnmeldungZuschlag\"='f' where " +
					"\"lngSdSeminarID\"=" + sem.getSeminarID() + " and \"lngKurstypID\"=" + lKurstypID + " and " + 
					"\"strMatrikelnummer\"='" + rOver.getString("strMatrikelnummer") + "';";
		}
		rOver.close();
		sem.sqlExeSingle(sDelSQL);
		return iHowMany;
}
ResultSet getSadStudent(long lKurstypID)throws Exception{
	return sem.sqlQuery("select distinct a.\"strMatrikelnummer\" from \"tblBdAnmeldung\" a " +
			"where a.\"lngSdSeminarID\"=" + sem.getSeminarID() + " and a.\"lngKurstypID\"=" + lKurstypID + " and " +
			"(not exists (" +
				"select * from \"tblBdAnmeldung\" b " +
				"where b.\"lngSdSeminarID\"=" + sem.getSeminarID() + " and " +
				"b.\"lngKurstypID\"=" + lKurstypID + " and " +
				"b.\"blnAnmeldungZuschlag\"='t' and " +
				"b.\"strMatrikelnummer\"=a.\"strMatrikelnummer\" " +
				")) " +
			"order by a.\"strMatrikelnummer\";");
}

////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////

/////////////////////////////////
//getFixSQL: welche Kurse sollen
//in Ruhe gelassen werden?
// 
// returns SQL: KursID != 2 and 
//  KursID != 3 etc. etc.
/////////////////////////////////

String getFixSQL(HttpServletRequest r){
	String sReturn="";
	Enumeration pn=r.getParameterNames();
	for(;pn.hasMoreElements();){
		  String 	parName		= (String) 	pn.nextElement();
		  if(parName.startsWith("fix-")) sReturn += " and \"lngKursID\" != " + parName.substring("fix-".length());
	}
	if(!sReturn.equals("")) sReturn = sReturn.substring(" and ".length());
    return sReturn;
}

void getKursToFill(HttpServletRequest r){
	Enumeration pn=r.getParameterNames();
	long lTmp = 0;
	for(;pn.hasMoreElements();){
		  String 	parName		= (String) 	pn.nextElement();
		  if(parName.startsWith("fill-")){
			  lTmp = Long.parseLong(r.getParameter(parName));			  
			  if(lTmp>0){
			  	lKursToFill = Long.parseLong(parName.substring("fill-".length()));
			  	lKursFillBy = lTmp;
			  }
		  }
	}
}

void getKursToEmpty(HttpServletRequest r){
	Enumeration pn=r.getParameterNames();
	long lTmp = 0;
	for(;pn.hasMoreElements();){
		  String 	parName		= (String) 	pn.nextElement();
		  if(parName.startsWith("empty-")){
			  lTmp = Long.parseLong(r.getParameter(parName));			  
			  if(lTmp>0){
			  	lKursToEmpty = Long.parseLong(parName.substring("empty-".length()));
			  	lKursEmptyBy = lTmp;
			  }
		  }
	}
}

void getKursToCut(HttpServletRequest r){
	Enumeration pn=r.getParameterNames();
	long lTmp = 0;
	for(;pn.hasMoreElements();){
		  String 	parName		= (String) 	pn.nextElement();
		  if(parName.startsWith("cut-")){
			  lTmp = Long.parseLong(r.getParameter("empty-" + parName.substring("cut-".length())));			  
			  if(lTmp>0){
			  	lKursToCut = Long.parseLong(parName.substring("cut-".length()));
			  	lKursEmptyBy = lTmp;
			  }
		  }
	}
}

/////////////////////////////
// füllt den Kurs aus 
// lKursToFill um
// lKursFillBy Bewerber auf,
// ohne die Anmeldungen der
// Kurstypen sFix zu verändern.
/////////////////////////////
void fill(String sFix, long lKurstypID) throws Exception{
	
	String sMatrikelnummer="";
	if(sFix==null) sFix="";
	
	// Auswählen aller Anmeldungen für Kurs lKursToFill,
	// ohne Zuschlag, höchste Priorität zuvorderst.
	Iterator rSwap = sem.sqlQuerySafe("select \"strMatrikelnummer\", \"intAnmeldungPrioritaet\" from \"tblBdAnmeldung\" a where " +
			"\"lngSdSeminarID\"=" + sem.getSeminarID() + " and " + 
			"\"lngKurstypID\"=" + lKurstypID + " and " + 
			"\"lngKursID\"=" + lKursToFill + " and " + 
			"\"blnAnmeldungZuschlag\"='f' and " + 
			"\"blnAnmeldungFixiert\"='f' and " +
			"exists (select \"strMatrikelnummer\", \"lngKursID\" from \"tblBdAnmeldung\" a2 where " + 
					"a2.\"lngSdSeminarID\"=" + sem.getSeminarID() + " and " + 
					"a2.\"lngKurstypID\"=" + lKurstypID + " and " + 
					"a2.\"strMatrikelnummer\"=a.\"strMatrikelnummer\" and " + 
					"a2.\"blnAnmeldungZuschlag\"='t' and " + sFix + (sFix.equals("") ? "" : " and ") + 
					"a2.\"blnAnmeldungFixiert\"='f') " +
			"order by \"intAnmeldungPrioritaet\" desc;");
	
	long ii=0;
	while(rSwap.hasNext() && (ii++<lKursFillBy)){
		DynaBean row = (DynaBean) rSwap.next();
		sMatrikelnummer=(String)row.get("strmatrikelnummer");
		
		// Löschen des Zuschlags im anderen Kurs
		sem.sqlExe("update \"tblBdAnmeldung\" set \"blnAnmeldungZuschlag\"='f' where " + 
				"\"lngSdSeminarID\"="+sem.getSeminarID() + " and " + 
				"\"lngKurstypID\"=" + lKurstypID + " and " + 
				"\"blnAnmeldungFixiert\"='f' and " + 
				"\"strMatrikelnummer\"='" + sMatrikelnummer + "';");
		
		// Gewähren des Zuschlage in lKursToFill
		sem.sqlExe("update \"tblBdAnmeldung\" set \"blnAnmeldungZuschlag\"='t' where " + 
				"\"lngSdSeminarID\"="+sem.getSeminarID() + " and " + 
				"\"lngKurstypID\"=" + lKurstypID + " and " + 
				"\"blnAnmeldungFixiert\"='f' and " + 
				"\"lngKursID\"=" + lKursToFill + " and " + 
				"\"strMatrikelnummer\"='" + sMatrikelnummer + "';");
	}
			
	// Schließlich: Aufruf der Überlappungslöschers
	// (der möglicherweise die schöne Arbeit zunichte 
	//  macht...)
	deleteOverlaps(lKurstypID);
}

/////////////////////////////
//leert den Kurs aus 
//lKursToEmpty um
//lKursEmptyBy Bewerber auf,
//ohne die Anmeldungen der
//Kurstypen sFix zu verändern.
/////////////////////////////
void empty(String sFix, long lKurstypID) throws Exception{
	
	String sMatrikelnummer="";
	if(sFix==null) sFix="";
	int iPrio=-1;
	
	// Auswählen aller Anmeldungen für Kurs lKursToFill,
	// mit Zuschlag, niedrigste Priorität zuvorderst.
	String sSQL = "select \"strMatrikelnummer\", \"intAnmeldungPrioritaet\" from \"tblBdAnmeldung\" a where " +
				"\"lngSdSeminarID\"=" + sem.getSeminarID() + " and " + 
				"\"lngKurstypID\"=" + lKurstypID + " and " + 
				"\"lngKursID\"=" + lKursToEmpty + " and " + 
				"\"blnAnmeldungZuschlag\"='t' and " + 
				"\"blnAnmeldungFixiert\"='f' and " +
				"exists (select \"strMatrikelnummer\", \"lngKursID\" from \"tblBdAnmeldung\" a2 where " + 
						"a2.\"lngSdSeminarID\"=" + sem.getSeminarID() + " and " + 
						"a2.\"lngKurstypID\"=" + lKurstypID + " and " + 
						"a2.\"strMatrikelnummer\"=a.\"strMatrikelnummer\" and " + 
						"a2.\"blnAnmeldungZuschlag\"='f' and " + sFix + (sFix.equals("") ? "" : " and ") + 
						"a2.\"blnAnmeldungFixiert\"='f') " +
				"order by \"intAnmeldungPrioritaet\" desc;";
	m_sDebug += "Called 'empty'<br /><br />\n\n" + sSQL + "<br /><br />\n\n";
	long ii=0;
	int lKursID=0;
	//while(rSwap.next() && (ii++<=lKursEmptyBy)){
	Iterator rSwap_i = sem.sqlQuerySafe(sSQL);
	while(rSwap_i.hasNext() && (ii++<lKursEmptyBy)){
		
		DynaBean row = (DynaBean) rSwap_i.next();

		sMatrikelnummer=(String) row.get("strmatrikelnummer"); // ("strMatrikelnummer");
		m_sDebug += "Suche Alternativen für " + sMatrikelnummer + "<br />\n";
		// suchen nach der nächstbesten Alternative
		Iterator sTmp = sem.sqlQuerySafe("select \"lngKursID\" from \"tblBdAnmeldung\" where " +
				"\"lngSdSeminarID\"="+sem.getSeminarID() + " and " + 
				"\"lngKurstypID\"=" + lKurstypID + " and " + 
				"\"blnAnmeldungFixiert\"='f' and " + 
				"\"lngKursID\"!=" + lKursToEmpty + " and " + sFix + (sFix.equals("") ? "" : " and ") + 
				"\"strMatrikelnummer\"='" + sMatrikelnummer + "' " +
				"order by \"intAnmeldungPrioritaet\" desc;");
		
		
		if(sTmp.hasNext()){ 		
			// Löschen des Zuschlags im Kurs
			DynaBean row2 = (DynaBean) sTmp.next();
			sem.sqlExe("update \"tblBdAnmeldung\" set \"blnAnmeldungZuschlag\"='f' where " + 
					"\"lngSdSeminarID\"="+sem.getSeminarID() + " and " + 
					"\"lngKurstypID\"=" + lKurstypID + " and " + 
					"\"blnAnmeldungFixiert\"='f' and " + 
					"\"lngKursID\"=" + lKursToEmpty + " and " + 
					"\"strMatrikelnummer\"='" + sMatrikelnummer + "';");
		
			lKursID=((Integer)row2.get("lngkursid")).intValue();
			m_sDebug += "... buche Alternative " + lKursID + "<br />\n";
			
			//Zuschlag zu dieser Alternative:
			sem.sqlExe("update \"tblBdAnmeldung\" set \"blnAnmeldungZuschlag\"='t' where " + 
					"\"lngSdSeminarID\"="+sem.getSeminarID() + " and " + 
					"\"lngKurstypID\"=" + lKurstypID + " and " + 
					"\"blnAnmeldungFixiert\"='f' and " + 
					"\"lngKursID\"=" + lKursID + " and " + 
					"\"strMatrikelnummer\"='" + sMatrikelnummer + "';");				
		}else{ii--;m_sDebug += "... no luck.<br />\n";}
	}
	
	// Schließlich: Aufruf der Überlappungslöschers
	// (der möglicherweise die schöne Arbeit zunichte 
	//  macht...)
	deleteOverlaps(lKurstypID);
}

void cut(long lKurstypID) throws Exception{
	String sSQL = "select \"strMatrikelnummer\", \"intAnmeldungPrioritaet\" from \"tblBdAnmeldung\" a where " +
	"\"lngSdSeminarID\"=" + sem.getSeminarID() + " and " + 
	"\"lngKurstypID\"=" + lKurstypID + " and " + 
	"\"lngKursID\"=" + lKursToCut + " and " + 
	"\"blnAnmeldungZuschlag\"='t' and " + 
	"\"blnAnmeldungFixiert\"='f' " +
	"order by \"intAnmeldungPrioritaet\" desc;";
	ResultSet rCut = sem.sqlQuery(sSQL);
	
	int ii=0;
	while(rCut.next() && (ii++<lKursEmptyBy)){
		sem.sqlExe("update \"tblBdAnmeldung\" set \"blnAnmeldungZuschlag\"='f' where " + 
				"\"lngSdSeminarID\"=" + sem.getSeminarID() + " and " + 
				"\"lngKurstypID\"=" + lKurstypID + " and " + 
				"\"lngKursID\"=" + lKursToCut + " and " + 
				"\"blnAnmeldungZuschlag\"='t' and " + 
				"\"blnAnmeldungFixiert\"='f' and " +
				"\"strMatrikelnummer\"='" + rCut.getString("strMatrikelnummer") + "';"	);
	}
	rCut.close();
}
%>{"success":"true"}
