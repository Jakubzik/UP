<%@ page contentType="text/json" pageEncoding="UTF-8" import="de.shj.UP.util.ResultSetSHJ,java.sql.ResultSet,de.shj.UP.logic.AktuellesIterator,java.text.SimpleDateFormat, de.shj.UP.data.Dozent,de.shj.UP.data.shjCore" session="true" isThreadSafe="false"%>
<%@page import="java.util.Locale"%><jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean id="seminar" scope="session" class="de.shj.UP.HTML.HtmlSeminar" />
<%-- jsp:include page="checkAccess.jsp" flush="true"></jsp:include> --%>
<%
//------------------------------------------------
// Liefert Vorlesungskategorien, Kurstypen, Kurse,
// Lehrende
//
//  "_o" für "open": keine Zugangsberechtigung notwendig!
//
//  ansonsten identisch zu "_1"
//
// Aufrufparameter:
// - planungssemester [optional]. Nur falls "true", wird das Planungssemester gewählt.
// - nodownloads [optional]. Falls "true", werden keine Downloads geliefert (verdoppelt die Performance)
//
// Änderungen (neueste bitte oben)
// Name, Datum, 	Änderung
// hj    12.8.12 	Fertigstellung
// hj    19.1.16        Planungssemester eingefügt.
//------------------------------------------------
%>
<%!
String string2JSON(String s){
	return s.replace('\n', ' ').replace('\r', ' ').replace('\t', ' ').replace('\"', '\''); 
}
String getName(String sTitel, String sVorname, String sName){
    String sReturn= "";
    
    // Sonderfkt: Profs. Schulz & Shultis.
    if(sTitel.trim().equals("Profs.")) return "Profs. " + sName;
    
    if(sTitel.length()>0){
        if(sTitel.startsWith("Dr")) sReturn = "Dr. ";
        if(sTitel.startsWith("PD Dr.")) sReturn = "Priv.-Doz. Dr. ";
        if(sTitel.startsWith("Priv")) sReturn = "Priv.-Doz. Dr. ";
        if(sTitel.startsWith("Prof")) sReturn = "Prof. Dr. ";
    }
    
    return sReturn + sVorname.substring(0,1) + ". " + sName;
    
}
%>
{"kategorie":[ 
<%
	//Besteht eine gültige Sitzung?
//	if(user.getDozentNachname()==null) return;
//	if(user.getDozentAccessLevel()<1) throw new Exception("You are not allowed");
        long g_lSeminarID = 1;
        boolean bPlanungssemester = false;
        boolean bDownloads = true;
        
        if(request.getParameter("planungssemester")!=null && request.getParameter("planungssemester").equals("true")){
            bPlanungssemester = true;
        }
        
        if(request.getParameter("nodownloads")!=null && request.getParameter("nodownloads").equals("true")){
            bDownloads = false;
        }
        shjCore server = new shjCore();
        
        // Bei Performance-Engpass: WithoutDownloads wählen.
        ResultSetSHJ rKvv = null;
        if(bDownloads) rKvv = server.sqlQuerySHJ(getSQLWithDownloads(g_lSeminarID, bPlanungssemester));
        else rKvv = server.sqlQuerySHJ(getSQLWithoutDownloads(g_lSeminarID, bPlanungssemester));
        
    int iInhaltNrOld=-1;
    int iInhaltNr=0;
    
    long iKurstypIDOld=-1;
    long iKurstypID=0;
                  
while(rKvv.next()){
        
    iInhaltNr=rKvv.getInt("intSdKvvInhaltNr");
    iKurstypID=rKvv.getLong("lngKurstypID");

    // alter Kurstyp und alter Inhalt,
    // d.h. es wird bloß ein neuer Kurs
    // aufgelistet: also brauchen 
    // wir noch ein Komma:
    if(iKurstypID==iKurstypIDOld && iInhaltNrOld==iInhaltNr) out.write(",");
       
    // neuer Kurstyp?
    if(iKurstypID!=iKurstypIDOld){
        // alten Kurstyp und "Kurse" Sammlung schließen
        if(iKurstypIDOld>=0){out.write("]}"); }
    }
    
    // Neue Kategorie
    if(iInhaltNr!=iInhaltNrOld){
        // Falls es nicht das erste Objekt ist, 
        // altes Objekt schließen:
        if(iInhaltNrOld>=0) {out.write("]},");}
        if(!rKvv.isLastRow()) out.write("{\"name\":\"" + rKvv.getString("strKvvInhaltHeading")  + "\", \"id\":\"" + iInhaltNr + "\",\"kurstypen\":[");
        iInhaltNrOld=iInhaltNr;
    }else{
        // kein neuer Inhalt,
        // da fehlt möglicherweise noch 
        // ein Komma zwischen den Kurstypen
        if(iKurstypID!=iKurstypIDOld) out.write(",");
    }
    
    if(iKurstypID!=iKurstypIDOld){
        if(!rKvv.isLastRow()){
            out.write("{\"name\":\"" + 
                    rKvv.getString("strKurstypBezeichnung") + "\", \"beschreibung\":\"" + string2JSON(rKvv.getString("strKurstypBeschreibung_en")) + "\", \"id\":\"" + rKvv.getString("lngKurstypID") + "\", \"kurse\":[");
            iKurstypIDOld=iKurstypID;
        }
    }
        String sText=string2JSON(rKvv.getString("strKursLiteratur"));
        if(sText.trim().length()<5) sText = "";
        
        String sDowns = "[]";
        
        if(bDownloads){
            if(rKvv.getString("downlds")!=null){
                // sDowns = rKvv.getString("downlds").replace('{', '[').replace('}', ']');
                sDowns = rKvv.getString("downlds");
                if(sDowns.equals("null")) sDowns="[]";
                else{
                    // Postgres markiert Arrays außen mit 
                    // geschweiften Klammern "{" bzw. "}",
                    // die werden durch JSON ersetzt:
                    sDowns = "[" + sDowns.substring(1, sDowns.length()-1) + "]";
                    sDowns = sDowns.replaceAll("\"<", "{");
                    sDowns = sDowns.replaceAll(">\"", "}");
                    sDowns = sDowns.replaceAll("_#_", "\"");
                }
            }
        }
        // System.out.println(sDowns);
                
%>  
        {"titel":"<%=string2JSON(rKvv.getString("strKursTitel"))%>",
	"titel_en":"<%=string2JSON(rKvv.getString("strKursTitel_en"))%>",
	"termin":"<%=string2JSON(rKvv.getString("sTermin"))%>",
        "id":"<%=rKvv.getString("lngKursID")%>",
	"lehrende":"<%=getName(shjCore.normalize(rKvv.getString("strDozentTitel")).trim(),
                string2JSON(rKvv.getString("strDozentVorname").substring(0,1)),
                string2JSON(rKvv.getString("strDozentNachname")))%>",
        "beschreibung":"<%=string2JSON(rKvv.getString("strKursBeschreibung"))%>",
        "literatur":"<%=sText%>",
        "downloads":<%=sDowns%>,
	"beschreibung_en":"<%=string2JSON(rKvv.getString("strKursBeschreibung_en"))%>"}
<%}//End next()%>
]
			}]
<%--		}]
	}]--%>
}
<%! 

String getSQLWithoutDownloads(long lSeminarID, boolean bPlanungssemester){
    return "SELECT " +
                    "i.\"strKvvInhaltHeading\", " +
                    "i.\"strKvvInhaltTyp\", " +
                    "i.\"intSdKvvInhaltNr\", " +
                    "t.\"strKurstypBezeichnung\", " +
                    "t.\"lngKurstypID\", " +
                    "t.\"strKurstypBezeichnung_en\", " +
                    "t.\"strKurstypBeschreibung_en\", " +
                    "txk.\"sngKursCreditPts\", " +
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
                    "k.\"intKursSequence\", " +
                    "k.\"blnKursPlanungssemester\", " +                
                    "k.\"strKursTerminFreitext\", " +
                    "k.\"strKursRaumExtern1\", " +
                    "k.\"strKursRaumExtern2\", " +
                    "k.\"intKursTeilnehmer\", " +
                    "d.\"strDozentTitel\", " +
                    "d.\"strDozentVorname\", " +
                    "d.\"strDozentNachname\", " +
                    "CASE WHEN COALESCE(k.\"strKursTerminFreitext\", '')='' THEN " + 
                    "CASE WHEN COALESCE(k.\"dtmKursBeginn2\"::text, '')='' THEN " + 
                    "k.\"strKursTag\" || ', ' || EXTRACT(HOUR FROM k.\"dtmKursBeginn\") || '.' || EXTRACT(MINUTE FROM k.\"dtmKursBeginn\") || ' - ' || EXTRACT(HOUR FROM k.\"dtmKursEnde\") || '.' || EXTRACT (MINUTE FROM k.\"dtmKursEnde\") || ', ' ||	COALESCE(k.\"strKursRaum\",'') || COALESCE(k.\"strKursRaumExtern1\",'') " +	
                    "ElSE " + 
                    "k.\"strKursTag\" || ', ' || EXTRACT(HOUR FROM k.\"dtmKursBeginn\") || '.' || EXTRACT(MINUTE FROM k.\"dtmKursBeginn\") ||	' - ' || EXTRACT(HOUR FROM k.\"dtmKursEnde\") || '.' || EXTRACT (MINUTE	FROM k.\"dtmKursEnde\") || ', ' || COALESCE(k.\"strKursRaum\",'') ||	COALESCE(k.\"strKursRaumExtern1\",'') || ' and ' || " +
                    "k.\"strKursTag2\" || ', ' || EXTRACT(HOUR FROM k.\"dtmKursBeginn2\") || '.' || EXTRACT(MINUTE FROM k.\"dtmKursBeginn2\") || ' - ' || EXTRACT(HOUR FROM k.\"dtmKursEnde2\") || '.' || EXTRACT (MINUTE FROM k.\"dtmKursEnde2\") || ', ' || COALESCE(k.\"strKursRaum2\",'') || COALESCE(k.\"strKursRaumExtern2\",'') " + 
                    " END " + 
                    " ELSE	k.\"strKursTerminFreitext\" || ', Raum: ' || k.\"strKursRaumExtern1\" " +
                    "END AS \"sTermin\", " + 
                    "CASE WHEN EXISTS(select * from \"tblBdKursXLink\" kxl where kxl.\"lngSdSeminarID\"=" + lSeminarID + " and \"lngKursID\"=k.\"lngKursID\" and \"blnKursLinkVisible\"=true) THEN 1 ELSE 0 END AS \"bHasDownloads\" " +		  
                  "FROM " +
                    "\"tblSdKvvInhalt\" i, " +
                    "\"tblSdKvvInhaltXKurstyp\" ixt, " +
                    "\"tblSdKurstyp\" t, " +
                    "\"tblBdKursXKurstyp\" txk, " +
                    "\"tblBdKurs\" k, " +
                    "\"tblSdDozent\" d " +
                  "WHERE " +
                    "i.\"lngSdSeminarID\" = ixt.\"lngSdSeminarID\" AND " +
                    "i.\"intSdKvvInhaltNr\" = ixt.\"intKvvInhaltNr\" AND " +
                    "t.\"lngSdSeminarID\" = ixt.\"lngSdSeminarID\" AND " +
                    "t.\"lngKurstypID\" = ixt.\"lngKurstypID\" AND " +
                    "t.\"lngSdSeminarID\" = txk.\"lngSeminarID\" AND " +
                    "t.\"lngKurstypID\" = txk.\"lngKurstypID\" AND " +
                    "k.\"lngSdSeminarID\" = txk.\"lngSeminarID\" AND " +
                    "k.\"lngKursID\" = txk.\"lngKursID\" AND " +
                    "k.\"blnKursPlanungssemester\"=" + bPlanungssemester + " and " +
                    "d.\"lngSdSeminarID\" = k.\"lngSdSeminarID\" AND " +
                    "d.\"lngDozentID\" = k.\"lngDozentID\" AND " +
                    "i.\"lngSdSeminarID\" = " + lSeminarID +
                  " ORDER BY " +
                    "i.\"intKvvInhaltHeadingSequence\" ASC, " +
                    "t.\"intKurstypSequence\" ASC, " +
                    "k.\"intKursSequence\" ASC;" +
                ";";
}
// Problem liegt in der Verknüpfung der Downloads als Array.
// Das macht eine UNION notwendig -- ohne UNION liefert 
// array_agg entweder nur alle Kurse MIT Downloads, oder 
// es braucht 10 Sekunden.
//
// Diese Query braucht 3 Sekunden -- immer noch viel zu viel.
String getSQLWithDownloads(long lSeminarID, boolean bPlanungssemester){
    return "SELECT i.\"strKvvInhaltHeading\", i.\"intKvvInhaltHeadingSequence\", i.\"strKvvInhaltTyp\", i.\"intSdKvvInhaltNr\", t.\"strKurstypBezeichnung\", t.\"intKurstypSequence\", t.\"lngKurstypID\", t.\"strKurstypBezeichnung_en\", t.\"strKurstypBeschreibung_en\", txk.\"sngKursCreditPts\", k.\"lngKursID\", k.\"strKursTag\", k.\"dtmKursBeginn\", k.\"dtmKursEnde\", k.\"strKursRaum\", k.\"strKursTag2\", k.\"dtmKursBeginn2\", k.\"dtmKursEnde2\", k.\"strKursRaum2\", k.\"strKursTitel\", k.\"strKursTitel_en\", k.\"strKursBeschreibung\", k.\"strKursBeschreibung_en\", k.\"strKursLiteratur\", k.\"strKursZusatz\", k.\"strKursAnmeldung\", k.\"strKursVoraussetzung\", k.\"intKursSequence\", k.\"blnKursPlanungssemester\", k.\"strKursTerminFreitext\", k.\"strKursRaumExtern1\", k.\"strKursRaumExtern2\", k.\"intKursTeilnehmer\", d.\"strDozentTitel\", d.\"strDozentVorname\", d.\"strDozentNachname\", CASE " +
                    "WHEN COALESCE(k.\"strKursTerminFreitext\", '')='' THEN CASE " +
                    "                                                         WHEN COALESCE(k.\"dtmKursBeginn2\"::text, '')='' THEN k.\"strKursTag\" || ', ' || EXTRACT(HOUR " +
                    "                                                                                                                                               FROM k.\"dtmKursBeginn\") || '.' || EXTRACT(MINUTE " +
                    "                                                                                                                                                                                         FROM k.\"dtmKursBeginn\") || ' - ' || EXTRACT(HOUR " +
                    "FROM k.\"dtmKursEnde\") || '.' || EXTRACT (MINUTE " +
                    "                                         FROM k.\"dtmKursEnde\") || ', ' || COALESCE(k.\"strKursRaum\",'') || COALESCE(k.\"strKursRaumExtern1\",'') " +
                    "                                                         ELSE k.\"strKursTag\" || ', ' || EXTRACT(HOUR " +
                    "                                                                                                FROM k.\"dtmKursBeginn\") || '.' || EXTRACT(MINUTE " +
                    "                                                                                                                                          FROM k.\"dtmKursBeginn\") || ' - ' || EXTRACT(HOUR " +
                    "                                                                                                                                                                                      FROM k.\"dtmKursEnde\") || '.' || EXTRACT (MINUTE " +
                    "FROM k.\"dtmKursEnde\") || ', ' || COALESCE(k.\"strKursRaum\",'') || COALESCE(k.\"strKursRaumExtern1\",'') || ' and ' || k.\"strKursTag2\" || ', ' || EXTRACT(HOUR " +
                    "                                                                                                                                                      FROM k.\"dtmKursBeginn2\") || '.' || EXTRACT(MINUTE " +
                    "                                                                                                                                                                                                 FROM k.\"dtmKursBeginn2\") || ' - ' || EXTRACT(HOUR " +
                    "FROM k.\"dtmKursEnde2\") || '.' || EXTRACT (MINUTE " +
                    "                                          FROM k.\"dtmKursEnde2\") || ', ' || COALESCE(k.\"strKursRaum2\",'') || COALESCE(k.\"strKursRaumExtern2\",'') " +
                    "END " +
                    "ELSE k.\"strKursTerminFreitext\" || ', Raum: ' || k.\"strKursRaumExtern1\" " +
//                    "END AS \"sTermin\", array_agg(kxl.\"lngLinkID\") AS downlds " +
//                    "END AS \"sTermin\", array_agg('<''url'':''' || kxl.\"strKursLinkURL\" || ''',' || '''name'':''' || \"strKursLinkBezeichnung\" || '''>') AS downlds " +
"END AS \"sTermin\", array_agg('<_#_url_#_:_#_' || kxl.\"strKursLinkURL\" || '_#_,' || '_#_name_#_:_#_' || \"strKursLinkBezeichnung\" || '_#_>') AS downlds " +
                    "FROM \"tblSdKvvInhalt\" i,\"tblBdKursXLink\" kxl, " +
                    "     \"tblSdKvvInhaltXKurstyp\" ixt, " +
                    "     \"tblSdKurstyp\" t, " +
                    "     \"tblBdKursXKurstyp\" txk, " +
                    "     \"tblBdKurs\" k, " +
                    "     \"tblSdDozent\" d " +
                    "WHERE i.\"lngSdSeminarID\" = ixt.\"lngSdSeminarID\" " +
                    "AND kxl.\"lngSdSeminarID\"=" + lSeminarID + " " +
                    "AND kxl.\"lngKursID\"=k.\"lngKursID\" " +
                    "AND kxl.\"blnKursLinkVisible\"=TRUE  " +
                    "AND i.\"intSdKvvInhaltNr\" = ixt.\"intKvvInhaltNr\" " +
                    "AND t.\"lngSdSeminarID\" = ixt.\"lngSdSeminarID\" " +
                    "AND t.\"lngKurstypID\" = ixt.\"lngKurstypID\" " +
                    "AND t.\"lngSdSeminarID\" = txk.\"lngSeminarID\" " +
                    "AND t.\"lngKurstypID\" = txk.\"lngKurstypID\" " +
                    "AND k.\"lngSdSeminarID\" = txk.\"lngSeminarID\" " +
                    "AND k.\"lngKursID\" = txk.\"lngKursID\" " +
                    "AND k.\"blnKursPlanungssemester\"=" + bPlanungssemester + " " +
                    "AND d.\"lngSdSeminarID\" = k.\"lngSdSeminarID\" " +
                    "AND d.\"lngDozentID\" = k.\"lngDozentID\" " +
                    "AND i.\"lngSdSeminarID\" = " + lSeminarID + " " +
                    "group by i.\"strKvvInhaltHeading\",i.\"intKvvInhaltHeadingSequence\", i.\"strKvvInhaltTyp\", i.\"intSdKvvInhaltNr\", t.\"intKurstypSequence\",t.\"strKurstypBezeichnung\", t.\"intKurstypSequence\", t.\"lngKurstypID\", t.\"strKurstypBezeichnung_en\", t.\"strKurstypBeschreibung_en\", txk.\"sngKursCreditPts\", k.\"lngKursID\", k.\"strKursTag\", k.\"dtmKursBeginn\", k.\"dtmKursEnde\", k.\"strKursRaum\", k.\"strKursTag2\", k.\"dtmKursBeginn2\", k.\"dtmKursEnde2\", k.\"strKursRaum2\", k.\"strKursTitel\", k.\"strKursTitel_en\", k.\"strKursBeschreibung\", k.\"strKursBeschreibung_en\", k.\"strKursLiteratur\", k.\"strKursZusatz\", k.\"strKursAnmeldung\", k.\"strKursVoraussetzung\", k.\"intKursSequence\", k.\"blnKursPlanungssemester\", k.\"strKursTerminFreitext\", k.\"strKursRaumExtern1\", k.\"strKursRaumExtern2\", k.\"intKursTeilnehmer\", d.\"strDozentTitel\", d.\"strDozentVorname\", d.\"strDozentNachname\" " +
                    " " +
                    "UNION " +
                    " " +
                    "SELECT i.\"strKvvInhaltHeading\", i.\"intKvvInhaltHeadingSequence\", i.\"strKvvInhaltTyp\", i.\"intSdKvvInhaltNr\", t.\"strKurstypBezeichnung\", t.\"intKurstypSequence\", t.\"lngKurstypID\", t.\"strKurstypBezeichnung_en\", t.\"strKurstypBeschreibung_en\", txk.\"sngKursCreditPts\", k.\"lngKursID\", k.\"strKursTag\", k.\"dtmKursBeginn\", k.\"dtmKursEnde\", k.\"strKursRaum\", k.\"strKursTag2\", k.\"dtmKursBeginn2\", k.\"dtmKursEnde2\", k.\"strKursRaum2\", k.\"strKursTitel\", k.\"strKursTitel_en\", k.\"strKursBeschreibung\", k.\"strKursBeschreibung_en\", k.\"strKursLiteratur\", k.\"strKursZusatz\", k.\"strKursAnmeldung\", k.\"strKursVoraussetzung\", k.\"intKursSequence\", k.\"blnKursPlanungssemester\", k.\"strKursTerminFreitext\", k.\"strKursRaumExtern1\", k.\"strKursRaumExtern2\", k.\"intKursTeilnehmer\", d.\"strDozentTitel\", d.\"strDozentVorname\", d.\"strDozentNachname\", CASE " +
                    "WHEN COALESCE(k.\"strKursTerminFreitext\", '')='' THEN CASE " +
                    "                                                         WHEN COALESCE(k.\"dtmKursBeginn2\"::text, '')='' THEN k.\"strKursTag\" || ', ' || EXTRACT(HOUR " +
                    "                                                                                                                                               FROM k.\"dtmKursBeginn\") || '.' || EXTRACT(MINUTE " +
                    "                                                                                                                                                                                         FROM k.\"dtmKursBeginn\") || ' - ' || EXTRACT(HOUR " +
                    "FROM k.\"dtmKursEnde\") || '.' || EXTRACT (MINUTE " +
                    "                                         FROM k.\"dtmKursEnde\") || ', ' || COALESCE(k.\"strKursRaum\",'') || COALESCE(k.\"strKursRaumExtern1\",'') " +
                    "                                                         ELSE k.\"strKursTag\" || ', ' || EXTRACT(HOUR " +
                    "                                                                                                FROM k.\"dtmKursBeginn\") || '.' || EXTRACT(MINUTE " +
                    "                                                                                                                                          FROM k.\"dtmKursBeginn\") || ' - ' || EXTRACT(HOUR " +
                    "                                                                                                                                                                                      FROM k.\"dtmKursEnde\") || '.' || EXTRACT (MINUTE " +
                    "FROM k.\"dtmKursEnde\") || ', ' || COALESCE(k.\"strKursRaum\",'') || COALESCE(k.\"strKursRaumExtern1\",'') || ' and ' || k.\"strKursTag2\" || ', ' || EXTRACT(HOUR " +
                    "                                                                                                                                                      FROM k.\"dtmKursBeginn2\") || '.' || EXTRACT(MINUTE " +
                    "                                                                                                                                                                                                 FROM k.\"dtmKursBeginn2\") || ' - ' || EXTRACT(HOUR " +
                    "FROM k.\"dtmKursEnde2\") || '.' || EXTRACT (MINUTE " +
                    "                                          FROM k.\"dtmKursEnde2\") || ', ' || COALESCE(k.\"strKursRaum2\",'') || COALESCE(k.\"strKursRaumExtern2\",'') " +
                    "END " +
                    "ELSE k.\"strKursTerminFreitext\" || ', Raum: ' || k.\"strKursRaumExtern1\" " +
                    "END AS \"sTermin\", null AS downlds " +
                    "FROM \"tblSdKvvInhalt\" i, " +
                    "     \"tblSdKvvInhaltXKurstyp\" ixt, " +
                    "     \"tblSdKurstyp\" t, " +
                    "     \"tblBdKursXKurstyp\" txk, " +
                    "     \"tblBdKurs\" k, " +
                    "     \"tblSdDozent\" d " +
                    "WHERE i.\"lngSdSeminarID\" = ixt.\"lngSdSeminarID\" and  " +
                    "not exists (select * from \"tblBdKursXLink\" where \"lngSdSeminarID\"=" + lSeminarID + " and \"lngKursID\"=k.\"lngKursID\" and \"blnKursLinkVisible\"=true) " +
                    "AND i.\"intSdKvvInhaltNr\" = ixt.\"intKvvInhaltNr\" " +
                    "AND t.\"lngSdSeminarID\" = ixt.\"lngSdSeminarID\" " +
                    "AND t.\"lngKurstypID\" = ixt.\"lngKurstypID\" " +
                    "AND t.\"lngSdSeminarID\" = txk.\"lngSeminarID\" " +
                    "AND t.\"lngKurstypID\" = txk.\"lngKurstypID\" " +
                    "AND k.\"lngSdSeminarID\" = txk.\"lngSeminarID\" " +
                    "AND k.\"lngKursID\" = txk.\"lngKursID\" " +
                    "AND k.\"blnKursPlanungssemester\"=" + bPlanungssemester + " " +
                    "AND d.\"lngSdSeminarID\" = k.\"lngSdSeminarID\" " +
                    "AND d.\"lngDozentID\" = k.\"lngDozentID\" " +
                    "AND i.\"lngSdSeminarID\" = " + lSeminarID + " " +
                    " " +
                    "ORDER BY \"intKvvInhaltHeadingSequence\" ASC, " +
                    "         \"intKurstypSequence\" ASC, " +
                    "         \"intKursSequence\" ASC;";
}%>