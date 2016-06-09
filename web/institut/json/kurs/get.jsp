<%--
    JSP-Object "kurs/get"
    @Revision: Nov 18, 2013 -- hj

    @Idee
    ===========================
    Liefert Kurse nach Volltextsuche (Kurstitel, 
    Leistung, Dozent etc.) für Anmeldungen -- sowohl
    aus Studierendensicht als auch aus Lehrendensicht.

    SYNOPSIS (German)
    ===========================
    2012, Nov 30, shj
    Erzeugt. 
    
    Üblicher Lifecycle: GET
    Allerdings diverse Szenarien der Benutzung.

    
    Expected SESSION PROPERTIES
    ===========================
    user/student
    txtKurssucheDozent:     [String] Dozentname (Teilstring, case insensitive)
    idxKurssucheLeistung:   [String] Leistungebezeichnung (Teilstring, case insensitive)
    txtSLKurssucheKursTitel:[String] Kurstitel (Teilstring, case insensitive)

    Bei Zugriff durch Lehrende auch:
    cboKurssucheSemester:   [String] "current" (für aktuelles Semester) 
                                     oder Schlüsselwert d. Tabelle, z.B. "ss2008"

    Expected PARAMETER(S):
    ===========================
    matrikelnummer  [long]
    [bemerkung_id    [long]]        (optional)
    
    Returns
    =======
    Array von Kursen
    {
    "kurse": [
        {
            "kurs": {
                "termin": "Mi 14:15",
                "fristende": "2000-1-1",
                "semester_indicator": "Archive",
                "id": "2601",
                "aussteller_id": "26",
                "kurstyp_id": "14",
                "semester": "ss2009",
                "leistung": "Proseminar I Literaturwissenschaft",
                "leistung_id": "10",
                "lehrende": "Heiko Jakubzik",
                "titel": "Einführung in die Lyrik",
                "titel_en": "Introduction to U.S. American Poetry",
                "ects": "5.5"
            }
        }
    ]
    }

    Sample Usage
    ============
    (1) Studierendenanfrage mit 
    txtKurssucheDozent:"jak"
    idxKurssucheLeistung:"pro"
    txtSLKurssucheKursTitel:"Poetry"

    liefert diejenigen Kurse aus dem aktuellen Semester, 
    * deren Anmeldefrist gerade läuft UND
    * deren Lehrende(r) "jak" im Nachnamen hat (z.B. "Jakubzik") UND
    * in denen man einen "pro" Schein erwerben kann, z.B. "Proseminar" UND
    * in deren Kurstitel "Poetry" vorkommt, z.B. "19th Century American Poetry.

    (2) Lehrendenanfrage mit 
    txtKurssucheDozent:"jak"
    idxKurssucheLeistung:"pro"
    txtSLKurssucheKursTitel:"Poetry"
    cboKurssucheSemester:"ss2014"

    liefert diejenigen Kurse aus dem Sommersemester 2014, 
    * deren Lehrende(r) "jak" im Nachnamen hat (z.B. "Jakubzik") UND
    * in denen man einen "pro" Schein erwerben kann, z.B. "Proseminar" UND
    * in deren Kurstitel "Poetry" vorkommt, z.B. "19th Century American Poetry.
--%><%@page import="java.sql.PreparedStatement"%>
<%@page import="de.shj.UP.data.shjCore"%>
<%@ page language="java" pageEncoding="UTF8" contentType="text/html" import="java.sql.ResultSet" errorPage="../error.jsp"%>
<jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" />
<jsp:useBean id="seminar" scope="session" class="de.shj.UP.logic.SeminarData" />
<jsp:useBean id="sd" scope="session" class="de.shj.UP.util.SessionData" />
<%!
de.shj.UP.logic.SeminarData sem;

// Gibt "Mi 11:15" o.ä. aus.
// Fkt. nur beim Standard 1x wöchentlich, 
// feste Uhrzeit -- gibt ansonsten "?" aus.
// Müsste man vielleicht verbessern.
String getTermin(ResultSet rK, de.shj.UP.util.SessionData sd, HttpServletRequest req){
    String sReturn = "?";
    try{
        if(sd.getSessionType().equals("student") || ( req.getParameter("cboKurssucheSemester")!=null && req.getParameter("cboKurssucheSemester").equals("current"))){
            // Aktuelles Semester
            sReturn = rK.getString("strKursTag").substring(0,2) + " " + shjCore.g_TIME_FORMAT.format(rK.getTime("dtmKursBeginn"));
        }else{
            // Archiv
            sReturn = rK.getString("strKvvArchivKursTag").substring(0,2) + " " + shjCore.g_TIME_FORMAT.format(rK.getTime("dtmKvvArchivKursBeginn"));
        }
    }catch(Exception eNotPossIgnore){}
    return sReturn;
}
ResultSet getSQLStudent(HttpServletRequest m_request, int iFachID) throws Exception{
	PreparedStatement pstm = sem.prepareStatement( "SELECT 'Current' as \"Indicator\"," + 
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
                  "k.\"dtmKursScheinanmeldungBis\", " +
		  "k.\"dtmKursBeginn\", " + 
		  "d.\"strDozentVorname\" || ' ' || d.\"strDozentNachname\" as lehrende, " + 
                  "d.\"lngDozentID\" as dozent_id," +
		  "x.\"sngKursCreditPts\" as ects " +
		"FROM \"tblBdKurs\" k, \"tblBdKursXKurstyp\" x, \"tblSdKurstyp\" t, \"tblSdLeistung\" l, \"tblSdDozent\" d " + 
		"WHERE (" + 
		  "(l.\"lngSdSeminarID\"=?) AND " +
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
		  "(k.\"lngSdSeminarID\"= ?) AND " + 
		  "(k.\"strKursTitel\" ~* ?) AND " +
		  "(l.\"strLeistungBezeichnung\" ~* ?) AND " +
		  "(d.\"strDozentNachname\" ~* ?)" +
		")");

           int ii=1;
           pstm.setLong(ii++, sem.getSeminarID());
           pstm.setLong(ii++, sem.getSeminarID());
           pstm.setString(ii++, ".*" + m_request.getParameter("txtSLKurssucheKursTitel") + ".*");
           pstm.setString(ii++, ".*" + m_request.getParameter("idxKurssucheLeistung") + ".*");
           pstm.setString(ii++, ".*" + m_request.getParameter("txtKurssucheDozent") + ".*");
           return pstm.executeQuery();
}
ResultSet getSQL(HttpServletRequest m_request) throws Exception{
	// Da das Semester in SignUp _vor_ dem tatsächlichen
        // Wechsel des Verwaltungssemesters umgestellt wird,
        // braucht man tatsächlich die Bezeichnung "Aktuell"
        // [Oct 15, 2013: checked joins, ok]
	if(m_request.getParameter("cboKurssucheSemester")!=null && m_request.getParameter("cboKurssucheSemester").equals("current")){
 	   PreparedStatement pstm = sem.prepareStatement( "SELECT " + 
		  "k.\"lngSdSeminarID\", " + 
		  "k.\"lngKursID\" as id, " + 
		  "t.\"lngKurstypID\", " +
		  "'Current' AS Indicator, " +
//		  "'" + sem.getDBCleanString(m_request.getParameter("cboKurssucheSemester")) + "' AS Semester, " + 
                  "? as Semester," + //sem.getDBCleanString(m_request.getParameter("cboKurssucheSemester")) + "' AS Semester, " + 
		  "k.\"strKursTitel\" as titel, " +
		  "k.\"strKursTitel_en\" as titel_en, " +
		  "l.\"strLeistungBezeichnung\" as leistung, " + 
		  "l.\"lngLeistungID\" as leistung_id, " +
		  "k.\"strKursBeschreibung\", " + 
		  "k.\"strKursLiteratur\", " + 
                   "k.\"dtmKursScheinanmeldungBis\", " +
		  "k.\"strKursTag\", " + 
		  "k.\"dtmKursBeginn\", " + 
		  "d.\"strDozentVorname\" || ' ' || d.\"strDozentNachname\" as lehrende, " + 
                  "d.\"lngDozentID\" as dozent_id," +
		  "x.\"sngKursCreditPts\" as ects " +
		"FROM \"tblBdKurs\" k, \"tblBdKursXKurstyp\" x, \"tblSdKurstyp\" t, \"tblSdLeistung\" l, \"tblSdDozent\" d " + 
		"WHERE (" + 
		  "(k.\"lngKursID\" = x.\"lngKursID\") AND " + 
		  "(k.\"lngSdSeminarID\" = x.\"lngSeminarID\") AND " + 
		  "(x.\"lngKurstypID\" = t.\"lngKurstypID\") AND " + 
		  "(x.\"lngSeminarID\" = t.\"lngSdSeminarID\") AND " + 
		  "(t.\"lngKurstypLeistungsID\" = l.\"lngLeistungID\") AND " + 
		  "(t.\"lngSdSeminarID\" = l.\"lngSdSeminarID\") AND " + 
		  "(k.\"lngDozentID\" = d.\"lngDozentID\") AND " + 
                  ((m_request.getParameter("planungssemester")!=null) ? "(k.\"blnKursPlanungssemester\" = ?) AND " : "(k.\"blnKursPlanungssemester\" != true) AND ") + 
		  "(k.\"lngSdSeminarID\" = d.\"lngSdSeminarID\") AND " + 
		  "(k.\"lngSdSeminarID\"= ?) AND " + 
		  "(k.\"strKursTitel\" ~* ?) AND " +
		  "(l.\"strLeistungBezeichnung\" ~* ?) AND " +
		  "(d.\"strDozentNachname\" ~* ?)" +
		")"); 	  
           
           int ii=1;
           pstm.setString(ii++, m_request.getParameter("cboKurssucheSemester"));
           if(m_request.getParameter("planungssemester")!=null) pstm.setBoolean(ii++, m_request.getParameter("planungssemester").startsWith("t"));
           pstm.setLong(ii++, sem.getSeminarID());
           pstm.setString(ii++, ".*" + m_request.getParameter("txtSLKurssucheKursTitel") + ".*");
           pstm.setString(ii++, ".*" + m_request.getParameter("idxKurssucheLeistung") + ".*");
           pstm.setString(ii++, ".*" + m_request.getParameter("txtKurssucheDozent") + ".*");
           return pstm.executeQuery();
	}else{
		PreparedStatement pstm = sem.prepareStatement( "SELECT " +
		  "\"lngSdSeminarID\", " + 
		  "\"lngID\" as id, " + 
		  "\"lngKvvArchivKurstypID\", " +
		  "'Archive' AS Indicator, " +
		  "\"strKvvArchivSemesterName\", " +
		  "\"strKvvArchivKurstitel\" as titel, " +
		  "\"strKvvArchivKurstitel_en\" as titel_en, " +
		  "\"strKvvArchivLeistungBezeichnung\" as leistung, " +
		  "\"lngKvvArchivLeistungID\" as leistung_id, " +
		  "\"strKvvArchivKursBeschreibung\", " +
		  "\"strKvvArchivKursLiteratur\", " +
                   "'2000-1-1' as \"dtmKursScheinanmeldungBis\", " +
		  "\"strKvvArchivKursTag\", " +
		  "\"dtmKvvArchivKursBeginn\", " +
		  "\"strKvvArchivDozentvorname\" || ' ' || \"strKvvArchivDozentname\" as lehrende, " +
                  "\"lngKvvArchivDozentID\" as dozent_id," +
		  "\"sngKvvArchivKursCreditPts\" as ects " +
		"FROM \"tblBdKvvArchiv\" " +
		"WHERE (" +
		  "(\"lngSdSeminarID\" = ?) AND " +
		  "(\"strKvvArchivSemesterName\"= ?) AND " + 
		  "(\"strKvvArchivKurstitel\" ~* ?) AND " + 
		  "(\"strKvvArchivKurstypBezeichnung\" ~* ?) AND " + 
		  "(\"strKvvArchivDozentname\" ~* ?)" +
		");");
                int ii=1;
                pstm.setLong(ii++, sem.getSeminarID());
                pstm.setString(ii++, m_request.getParameter("cboKurssucheSemester"));
                pstm.setString(ii++, ".*" + m_request.getParameter("txtSLKurssucheKursTitel") + ".*");
                pstm.setString(ii++, ".*" + m_request.getParameter("idxKurssucheLeistung") + ".*");
                pstm.setString(ii++, ".*" + m_request.getParameter("txtKurssucheDozent") + ".*");
                return pstm.executeQuery();
	}
}
%>
{
 "kurse":[<%long lERR_BASE=103000 + 100;    // Kurs + Get
 sem=seminar;
 ResultSet rKurse=null;
 String sKurstypColumn="";
 if(sd.getSessionType().equals("student")){
    rKurse = getSQLStudent(request, -4711);
    sKurstypColumn="lngKurstypID";
 }else{
    rKurse = getSQL(request);
    sKurstypColumn = (request.getParameter("cboKurssucheSemester").equals("current") ? "lngKurstypID" : "lngKvvArchivKurstypID");
 }
boolean bFirst=true;
while(rKurse.next()){
	if(!bFirst) out.write(",");
	else bFirst=false;
%>
{"kurs":{"termin":"<%=getTermin(rKurse,sd,request)%>","fristende":"<%=rKurse.getString("dtmKursScheinanmeldungBis")%>","semester_indicator":"<%=rKurse.getString("Indicator")%>","id":"<%=rKurse.getString("id")%>","aussteller_id":"<%=rKurse.getString("dozent_id")%>","kurstyp_id":"<%=rKurse.getString(sKurstypColumn)%>","semester":"<%=request.getParameter("cboKurssucheSemester")%>","leistung":"<%=rKurse.getString("leistung") %>", "leistung_id":"<%=rKurse.getString("leistung_id") %>", "lehrende":"<%=rKurse.getString("lehrende").trim() %>","titel":"<%=rKurse.getString("titel") %>", "titel_en":"<%=rKurse.getString("titel_en") %>", "ects":"<%=rKurse.getString("ects") %>"}}
<%}%>
]}