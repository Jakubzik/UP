<%@page import="java.util.Map"%><%@page import="java.util.HashMap"%><%@ page contentType="application/vnd.ms-excel" session="true" import="de.shj.UP.data.shjCore,de.shj.UP.HTML.HtmlDate,java.sql.ResultSet" %><jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" />lfdnrveranst;lehrperson;einrichtung;semester;veranstnr;titel_dt;titel_en;kuerzel;art;sws;leistungspunkte;studienjahr;sprache;hyperlink;teilnehmer_erw;teilnehmer_max;evasys;belegpflicht;drucken;import;kompetenzen;turnus;ueberschrift;lfdnrtermin;termin_wochentag;termin_von;termin_bis;termin_rhythmus;termin_raum;termin_beginn;termin_ende;termin_parallelgruppe;termin_teilnehmer_max;termin_bemerkung;termin_sws;termin_lehrpersonen;termin_lehrpersonen_alphabetisch;blob_voraussetzung;blob_kurzkommentar;blob_kommentar;blob_inhalt;blob_leistungsnachweis;blob_literatur
<%
    // EDIERE MICH
    final String g_sLSF_SEMESTER="20142";
    final String g_sKUERZEL="";         // Veranstaltungskürzel: wassat mean?
    final String g_sSWS="2";
    final String g_sLSF_NAME_ANGLISTIK="90200";
    
    final String g_sLFDNRTERMIN="1";    // unklar; was Spalte X bedeutet? Bei Blockveranstaltungen relevant?
    
    // Funktioniert nur für Seminar 1
    ResultSet rVorlesung = user.sqlQuery("SELECT " +
                    "i.\"strKvvInhaltHeading\", " +
                    "i.\"strKvvInhaltTyp\", " +
                    "i.\"intSdKvvInhaltNr\", " +
                    "t.\"strKurstypBezeichnung\", " +
                    "t.\"lngKurstypID\", " +
                    "t.\"strKurstypBezeichnung_en\", " +
                    "t.\"sngKurstypCreditPts\"," +
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
                    "k.\"strKursTerminFreitext\"," +
                    "CASE WHEN COALESCE(k.\"strKursTerminFreitext\", '')='' THEN " + 
                    "CASE WHEN COALESCE(k.\"dtmKursBeginn2\"::text, '')='' THEN " + 
                    "k.\"strKursTag\" || '; ' || EXTRACT(HOUR FROM k.\"dtmKursBeginn\") || '.' || EXTRACT(MINUTE FROM k.\"dtmKursBeginn\") || '-' || EXTRACT(HOUR FROM k.\"dtmKursEnde\") || '.' || EXTRACT (MINUTE FROM k.\"dtmKursEnde\") || '; Raum: ' ||	COALESCE(k.\"strKursRaum\",'') || COALESCE(k.\"strKursRaumExtern1\",'') " +	
                    "ElSE " + 
                    "k.\"strKursTag\" || '; ' || EXTRACT(HOUR FROM k.\"dtmKursBeginn\") || '.' || EXTRACT(MINUTE FROM k.\"dtmKursBeginn\") ||	'-' || EXTRACT(HOUR FROM k.\"dtmKursEnde\") || '.' || EXTRACT (MINUTE	FROM k.\"dtmKursEnde\") || '; Raum: ' || COALESCE(k.\"strKursRaum\",'') ||	COALESCE(k.\"strKursRaumExtern1\",'') || ' sowie ' || " +
                    "k.\"strKursTag2\" || '; ' || EXTRACT(HOUR FROM k.\"dtmKursBeginn2\") || '.' || EXTRACT(MINUTE FROM k.\"dtmKursBeginn2\") || '-' || EXTRACT(HOUR FROM k.\"dtmKursEnde2\") || '.' || EXTRACT (MINUTE FROM k.\"dtmKursEnde2\") || '; Raum: ' || COALESCE(k.\"strKursRaum2\",'') || COALESCE(k.\"strKursRaumExtern2\",'') " + 
                    " END " + 
                    " ELSE	k.\"strKursTerminFreitext\" || '; Raum: ' || k.\"strKursRaumExtern1\" " +
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
                    "k.\"blnKursPlanungssemester\"=true AND " +
                    "d.\"lngSdSeminarID\" = k.\"lngSdSeminarID\" AND " +
                    "d.\"lngDozentID\" = k.\"lngDozentID\" AND " +
                    "i.\"lngSdSeminarID\" = " + user.getSdSeminarID() +
                  " ORDER BY " +
                    "i.\"intKvvInhaltHeadingSequence\" ASC, " +
                    "t.\"intKurstypSequence\" ASC, " +
                    "k.\"intKursSequence\" ASC;" +
                ";");

    int ii=1;String sTerminTmp="";
    while(rVorlesung.next()){
        sTerminTmp = rVorlesung.getString("sTermin");

        out.write(ii++ + ";" + 
                getName(rVorlesung) + ";" + g_sLSF_NAME_ANGLISTIK + ";" + 
                g_sLSF_SEMESTER + ";" + 
                "NP-AS-" + rVorlesung.getString("lngKurstypID") + "-" + rVorlesung.getString("lngKursID") + ";" +
                rVorlesung.getString("strKursTitel").replace(';', ',') + ";" + 
                rVorlesung.getString("strKursTitel_en").replace(';', ',') + ";" +
                g_sKUERZEL + ";" +
                getVeranstaltungArt(rVorlesung.getString("strKurstypBezeichnung")) + ";" +
                g_sSWS + ";" +
                rVorlesung.getString("sngKurstypCreditPts") + ";" +
                "" + ";" +
                "englisch;" + 
                ";;;;" + 
                "nein;ja;ja;nein;keine Übernahme;" +
                getZuordnungZuUeberschriften()rVorlesung.getString("strKurstypBezeichnung") + ";" +
                g_sLFDNRTERMIN + ";" +
                (!rVorlesung.getString("strKursTerminFreitext").trim().equals("") ? 
                rVorlesung.getString("strKursTerminFreitext").trim().replace(';', ',').replace('\n', ' ') + ";;;;;" :
                sTerminTmp.substring(0,2) + ";" +
                getStart(sTerminTmp) + ";" +
                getStop(sTerminTmp) + ";" +
                "wöch;" +
                getRaum(sTerminTmp) + ";") +
                "\n"
                );
}%>

<%!
 String getZuordnungZuUeberschriften(long lKurstypID){
     switch(lKurstypID){
         case 1;2: return "Einführungsveranstaltungen";
         case 4;5;6;7;9;11: return "Sprachpraxis";
         case 12;20220;20230: return "Fachdidaktik";
         case 
     } 
 }

// Termin hat das Format "Montag; 13.00-14.15; Raum + [2. Termin]" oder Frei
// Start gibt 13:00 zurück
String getStart(String sTermin){
    int iPosKomma = sTermin.indexOf(";");
    int iPosHyphen= sTermin.indexOf("-");
    if(iPosKomma<0 || iPosHyphen<0) return "";
    
    String sReturn=sTermin.substring(iPosKomma+2, iPosHyphen).replace('.', ':').trim();
    
    // 9:00 -> 09:00
    if(sReturn.indexOf(":")==1) sReturn = "0" + sReturn;
    
    // 10:0 -> 10:00
    if(sReturn.length()==4) sReturn += "0";
    return sReturn;
}

String getStop(String sTermin){
    int iPosHyphen= sTermin.indexOf("-");
    if(iPosHyphen<0 || sTermin.length()<iPosHyphen+6) return "";
    
    String sReturn=sTermin.substring(iPosHyphen+1, iPosHyphen+6).replace('.', ':').trim();
    // 9:00 -> 09:00
    if(sReturn.indexOf(":")==1) sReturn = "0" + sReturn;
    
    // "10:0;" -> "10:00"
    if(sReturn.length()==5 && sReturn.endsWith(";")) sReturn = sReturn.replace(';', '0');
    return sReturn;
}

String getRaum(String sTermin){
    int iPosHyphen= sTermin.indexOf("Raum: ");
    if(iPosHyphen<0) return "";
    
    String sReturn=sTermin.substring(iPosHyphen+"Raum: ".length()).trim();
    iPosHyphen=sReturn.indexOf(' ');
    if(iPosHyphen>=0) sReturn = sReturn.substring(0, iPosHyphen);
    
    try{
        Long.parseLong(sReturn);return "Kettengasse 12 / AS SR " +sReturn;
    }catch(Exception eNotInAS){return sReturn;}
}

String getName(ResultSet r)throws Exception{
    String sReturn="";
    sReturn=r.getString("strDozentTitel").trim();
    if(sReturn.length()<3) sReturn="";
    else sReturn += " ";
    
    sReturn += r.getString("strDozentVorname") + " " + r.getString("strDozentNachname");
    return sReturn;
}

String getVeranstaltungArt(String sInput){
    String sReturn = "Kurs / Übung";
    if(sInput.startsWith("Vorlesung")) sReturn="Vorlesung";
    else if (sInput.startsWith("Proseminar")) sReturn="Proseminar";
    else if (sInput.startsWith("Hauptseminar")) sReturn="Hauptseminar";
    else if (sInput.startsWith("Einführung") || sInput.startsWith("Phonetik")) sReturn="Einführung";
    else if (sInput.startsWith("Oberseminar")) sReturn="Oberseminar";
    else if (sInput.startsWith("Kolloq")) sReturn="Kolloquium";
    else if (sInput.startsWith("Tutorium")) sReturn="Tutorium";
    return sReturn;
    // Pronunciation Practice;Grammar; Writing; Translation; Fachdidktik; Phonetik
}
%>