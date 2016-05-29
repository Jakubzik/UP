<%@ page contentType="text/json" pageEncoding="UTF-8" import="de.shj.UP.util.ResultSetSHJ,java.sql.ResultSet,de.shj.UP.logic.AktuellesIterator,java.text.SimpleDateFormat, de.shj.UP.data.Dozent,de.shj.UP.data.shjCore" session="true" isThreadSafe="false"%>
<%@page import="java.util.Locale"%><jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean id="seminar" scope="session" class="de.shj.UP.HTML.HtmlSeminar" />
<%-- jsp:include page="checkAccess.jsp" flush="true"></jsp:include> --%>
<%
//------------------------------------------------
// Liefert Vorlesungskategorien, Kurstypen, Kurse,
// Lehrende
//
// Aufrufparameter:
// - keine (initialisiertes Bean "user")
//
// Änderungen (neueste bitte oben)
// Name, Datum, 	Änderung
// hj    12.8.12 	Fertigstellung
//------------------------------------------------
%>
<%!
String string2JSON(String s){
	return s.replace('\n', ' ').replace('\r', ' ').replace('\t', ' ').replace('\"', '\''); 
}%>
[{
<%
	//Besteht eine gültige Sitzung?
	if(user.getDozentNachname()==null) return;
	if(user.getDozentAccessLevel()<1) throw new Exception("You are not allowed");
        
        ResultSetSHJ rKvv=user.sqlQuerySHJ("SELECT " +
                    "i.\"strKvvInhaltHeading\", " +
                    "i.\"strKvvInhaltTyp\", " +
                    "i.\"intSdKvvInhaltNr\", " +
                    "t.\"strKurstypBezeichnung\", " +
                    "t.\"lngKurstypID\", " +
                    "t.\"strKurstypBezeichnung_en\", " +
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
                    "k.\"strKursTag\" || ', ' || EXTRACT(HOUR FROM k.\"dtmKursBeginn\") || '.' || EXTRACT(MINUTE FROM k.\"dtmKursBeginn\") || '-' || EXTRACT(HOUR FROM k.\"dtmKursEnde\") || '.' || EXTRACT (MINUTE FROM k.\"dtmKursEnde\") || ', Raum: ' ||	COALESCE(k.\"strKursRaum\",'') || COALESCE(k.\"strKursRaumExtern1\",'') " +	
                    "ElSE " + 
                    "k.\"strKursTag\" || ', ' || EXTRACT(HOUR FROM k.\"dtmKursBeginn\") || '.' || EXTRACT(MINUTE FROM k.\"dtmKursBeginn\") ||	'-' || EXTRACT(HOUR FROM k.\"dtmKursEnde\") || '.' || EXTRACT (MINUTE	FROM k.\"dtmKursEnde\") || ', Raum: ' || COALESCE(k.\"strKursRaum\",'') ||	COALESCE(k.\"strKursRaumExtern1\",'') || ' sowie ' || " +
                    "k.\"strKursTag2\" || ', ' || EXTRACT(HOUR FROM k.\"dtmKursBeginn2\") || '.' || EXTRACT(MINUTE FROM k.\"dtmKursBeginn2\") || '-' || EXTRACT(HOUR FROM k.\"dtmKursEnde2\") || '.' || EXTRACT (MINUTE FROM k.\"dtmKursEnde2\") || ', Raum: ' || COALESCE(k.\"strKursRaum2\",'') || COALESCE(k.\"strKursRaumExtern2\",'') " + 
                    " END " + 
                    " ELSE	k.\"strKursTerminFreitext\" || ', Raum: ' || k.\"strKursRaumExtern1\" " +
                    "END AS \"sTermin\", " + 
                    "CASE WHEN EXISTS(select * from \"tblBdKursXLink\" kxl where kxl.\"lngSdSeminarID\"=" + user.getSdSeminarID() + " and \"lngKursID\"=k.\"lngKursID\" and \"blnKursLinkVisible\"=true) THEN 1 ELSE 0 END AS \"bHasDownloads\" " +		  
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
                    "d.\"lngSdSeminarID\" = k.\"lngSdSeminarID\" AND " +
                    "d.\"lngDozentID\" = k.\"lngDozentID\" AND " +
                    "i.\"lngSdSeminarID\" = " + user.getSdSeminarID() +
                  " ORDER BY " +
                    "i.\"intKvvInhaltHeadingSequence\" ASC, " +
                    "t.\"intKurstypSequence\" ASC, " +
                    "k.\"intKursSequence\" ASC;" +
                ";");
    
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
        if(iKurstypIDOld>=0){out.write("}}"); }
    }
    
    // Neue Kategorie
    if(iInhaltNr!=iInhaltNrOld){
        // Falls es nicht das erste Objekt ist, 
        // altes Objekt schließen:
        if(iInhaltNrOld>=0) {out.write("},");}
        if(!rKvv.isLastRow()) out.write("\"kvv-heading\":{\"name\":\"" + rKvv.getString("strKvvInhaltHeading")  + "\",");
        iInhaltNrOld=iInhaltNr;
    }else{
        // kein neuer Inhalt,
        // da fehlt möglicherweise noch 
        // ein Komma zwischen den Kurstypen
        if(iKurstypID!=iKurstypIDOld) out.write(",");
    }
    
    if(iKurstypID!=iKurstypIDOld){
        if(!rKvv.isLastRow()){
            out.write("\"kurstypen\":{\"name\":\"" + 
                    rKvv.getString("strKurstypBezeichnung") + "\", \"id\":\"" + rKvv.getString("lngKurstypID") + "\", \"kurse\":{");
            iKurstypIDOld=iKurstypID;
        }
    }
    
        
%>
"kurs":
        {"titel":"<%=string2JSON(rKvv.getString("strKursTitel"))%>",
	"titel_en":"<%=string2JSON(rKvv.getString("strKursTitel_en"))%>",
	"termin":"<%=rKvv.getString("sTermin")%>",
	"lehrende":"<%=string2JSON(rKvv.getString("strDozentNachname"))%>",
        "beschreibung":"<%=string2JSON(rKvv.getString("strKursBeschreibung"))%>",
	"beschreibung_en":"<%=string2JSON(rKvv.getString("strKursBeschreibung_en"))%>"}
<%}//End next()%>
    }
  }
}
}
}
}
]