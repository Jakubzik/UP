<%@ page language="java" pageEncoding="UTF8" contentType="text/html" import="java.sql.ResultSet,de.shj.UP.HTML.HtmlDate" errorPage="error.jsp"%><jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean id="student" scope="session" class="de.shj.UP.beans.config.student.StudentBean" /><jsp:useBean id="seminar" scope="session" class="de.shj.UP.HTML.HtmlSeminar" /><%!
String s2j(String s){
	return s.replace('\n', ' ').replace('\r', ' ').replace('\t', ' ').replace('\"', '\''); 
}

// BIG Todo: @todo
// Es werden die Kurse angezeigt, zu denen man sich derzeit
// anmelden kann. 
// Der derzeit auskommentierte Teil der Abfrage 
// sorgt dafür, dass nur diejenigen Kurstypen wählbar 
// sind, die auch für den Studiengang eine Rolle spielen.
//
// Leider funktioniert das nicht, da die alten, unmodularen
// Studiengänge (8351, z.B. Jakubzik) nicht ordentlich 
// genug modularisiert im System stehen.

// Auswahl der Kurse, zu denen man sich 
// Anmelden kann:
String getSQL(HttpServletRequest m_request, long lSeminarID, int iFachID) throws Exception{
	String sSQL="";
	sSQL = "SELECT " + 
		  "k.\"lngSdSeminarID\", " + 
		  "k.\"lngKursID\" as id, " + 
		  "t.\"lngKurstypID\", " +
		  "k.\"strKursTitel\" as titel, " +
		  "k.\"strKursTitel_en\" as titel_en, " +
		  "l.\"strLeistungBezeichnung\" as leistung, " +
		  "t.\"strKurstypBezeichnung\" as kurstyp, " + 
		  "l.\"lngLeistungID\" as leistung_id, " +
		  "k.\"strKursBeschreibung\", " + 
		  "k.\"strKursLiteratur\", " + 
		  "k.\"strKursTag\", " +
		   "k.\"strKursTerminFreitext\", " + 
		  "k.\"dtmKursBeginn\", " + 
		  "d.\"strDozentVorname\" || ' ' || d.\"strDozentNachname\" as lehrende, " + 
		  "x.\"sngKursCreditPts\" as ects " +
		"FROM \"tblBdKurs\" k, \"tblBdKursXKurstyp\" x, \"tblSdKurstyp\" t, \"tblSdLeistung\" l, \"tblSdDozent\" d " + 
		"WHERE (" + 
//  "(l.\"lngLeistungID\" IN (SELECT distinct " +
//				  "mxl.\"lngLeistungID\" FROM " + 
//				  "\"tblSdPruefungXFach\" pxf, " +
//				  "\"tblSdPruefungXModul\" pxm, " +
//				  "\"tblSdModulXLeistung\" mxl " +
//				"WHERE "+ 
//				  "pxf.\"lngSdSeminarID\" = pxm.\"lngSdSeminarID\" AND " +
//				  "pxf.\"lngPruefungID\" = pxm.\"lngPruefungID\" AND " +
//				  "pxm.\"lngSdSeminarID\" = mxl.\"lngSdSeminarID\" AND " +
//				  "pxm.\"lngModulID\" = mxl.\"lngModulID\" AND " +
//				  "pxf.\"lngSdSeminarID\" = " + lSeminarID + " AND " +
//				  "pxf.\"intFachID\" = " + iFachID + ")) AND " +
		  "(l.\"lngSdSeminarID\"=" + lSeminarID + ") AND " +
		  "(k.\"lngKursID\" = x.\"lngKursID\") AND " + 
		  "(k.\"blnKursScheinanmeldungErlaubt\" = true) AND " +
		  "(CURRENT_DATE Between \"dtmKursScheinanmeldungVon\" AND \"dtmKursScheinanmeldungBis\") AND " +
		  "(k.\"blnKursPlanungssemester\"=false) and " +
		  "(k.\"lngSdSeminarID\" = x.\"lngSeminarID\") AND " + 
		  "(x.\"lngKurstypID\" = t.\"lngKurstypID\") AND " + 
		  "(x.\"lngSeminarID\" = t.\"lngSdSeminarID\") AND " + 
		  "(t.\"lngKurstypLeistungsID\" = l.\"lngLeistungID\") AND " + 
		  "(t.\"lngSdSeminarID\" = l.\"lngSdSeminarID\") AND " + 
		  "(k.\"lngDozentID\" = d.\"lngDozentID\") AND " + 
		  "(k.\"lngSdSeminarID\" = d.\"lngSdSeminarID\") AND " + 
		  "(k.\"lngSdSeminarID\"= " + lSeminarID + ") AND " + 
		  "(k.\"strKursTitel\" ~* '.*" + m_request.getParameter("txtSLKurssucheKursTitel") + ".*') AND " +
		  "(l.\"strLeistungBezeichnung\" ~* '.*" + m_request.getParameter("idxKurssucheLeistung") + ".*') AND " +
		  "(d.\"strDozentNachname\" ~* '.*" + m_request.getParameter("txtKurssucheDozent") + ".*')" +
		")"; 	   

	return sSQL;
}
String getTermin(ResultSet r) throws Exception{
	if(r.getString("strKursTag") != null && r.getString("strKursTag").length()>3){
		try{
			return r.getString("strKursTag") + ", " + r.getString("dtmKursBeginn").substring(0, 5);
		}catch(Exception whatever){return "??";}
	}else{
		return s2j(r.getString("strKursTerminFreitext"));
	}
}
%>[<%
ResultSet rKurse = user.sqlQuery(getSQL(request, student.getSeminarID(), student.getFachID(student.getSeminarID())));
boolean bFirst=true;
while(rKurse.next()){
	if(!bFirst) out.write(",");
	else bFirst=false;
%>{"kurs":{"id":"<%=rKurse.getString("id")%>","termin":"<%=getTermin(rKurse)%>","leistung":"<%=rKurse.getString("kurstyp") %>", "leistung_id":"<%=rKurse.getString("leistung_id") %>", "kurstyp_id":"<%=rKurse.getString("lngKurstypID")%>", "lehrende":"<%=rKurse.getString("lehrende") %>","titel":"<%=rKurse.getString("titel") %>", "titel_en":"<%=rKurse.getString("titel_en") %>", "ects":"<%=rKurse.getString("ects") %>"}}
<%}%>
]