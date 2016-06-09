<%--
    JSP-Object "seminar/kurs/get"

    @Revsion
    ===========================
    Nov 24, 2013 -- hj

    @TODO
    ===========================

    SYNOPSIS (German)
    ===========================
    2013, Jun 7, shj    Erzeugt. 
    2014, Okt 23, shj:  bisher nur für Studierende zugänglich; für Seite kvv.jsp
                        Zugang auch für Lehrende ermöglicht.
    2014, Okt 25, shj:  auch englischer Titel wird geliefert.
    
    Üblicher Lifecycle: GET

    Expected SESSION PROPERTIES
    ===========================
    seminar        muss initialisiert sein (AccessLevel egal),

    Expected PARAMETER(S):
    ===========================
    --

    Returns
    =======
    Array of objects with remarks:
    "kurs":{
        "titel":<Bezeichnung des Kurstyps,               [Text]>,
        "titel_en":<engl. Bezeichnung des Kurstyps,      [Text]>,
        "termin":<String mit Termin und Raum             [Text]>,
        "dozent_id":<ID der Lehrenden                    [long]>,
        "dozent_name":<Nachname der Lehrenden            [Text]>,
        "beschreibung":<text                             [Text]>,
        "literatur":<text                                [Text]>,
        "erlaubt_kurswahl":<                             [Bool]>,
        "planungssemester":<aktuell|planung?             [Bool]>,
        "id":<Id des Kurses (zus. mit SeminarID primär   [int]>
    }

--%>
<%@page contentType="text/json" pageEncoding="UTF-8" import="java.sql.ResultSet,de.shj.UP.data.Dozent,de.shj.UP.data.shjCore" session="true" isThreadSafe="false"  errorPage="../../error.jsp" %>
<jsp:useBean id="seminar" scope="session" class="de.shj.UP.logic.SeminarData" />
<jsp:useBean id="student" scope="session" class="de.shj.UP.beans.config.student.StudentBean" />
<jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" />
<jsp:useBean id="sd" scope="session" class="de.shj.UP.util.SessionData" />
<%@include file="../../../fragments/checkVersion.jsp" %>
<%if(sd.getSessionType().equals("student")){%>
<%@include file="../../../fragments/checkLoginStudent.jsp" %>
<%}else{%>
<%@include file="../../../fragments/checkAccess1.jsp" %>
<%}%>
{"kurse":[
<%long lERR_BASE=206000 + 100;    // Kurs + Get
	String sReturn="";
	boolean bFirstLoop=true;
	ResultSet rKurse=student.sqlQuery("select k.*, d.\"strDozentNachname\", " +
                "CASE WHEN COALESCE(k.\"strKursTerminFreitext\", '')='' THEN " + 
                    "CASE WHEN COALESCE(k.\"dtmKursBeginn2\"::text, '')='' THEN " + 
                "k.\"strKursTag\" || ', ' || EXTRACT(HOUR FROM k.\"dtmKursBeginn\") || '.' || EXTRACT(MINUTE FROM k.\"dtmKursBeginn\") || '-' || EXTRACT(HOUR FROM k.\"dtmKursEnde\") || '.' || EXTRACT (MINUTE FROM k.\"dtmKursEnde\") || ', Raum: ' ||	COALESCE(k.\"strKursRaum\",'') || COALESCE(k.\"strKursRaumExtern1\",'') " +	
                "ElSE " + 
                "k.\"strKursTag\" || ', ' || EXTRACT(HOUR FROM k.\"dtmKursBeginn\") || '.' || EXTRACT(MINUTE FROM k.\"dtmKursBeginn\") ||	'-' || EXTRACT(HOUR FROM k.\"dtmKursEnde\") || '.' || EXTRACT (MINUTE	FROM k.\"dtmKursEnde\") || ', Raum: ' || COALESCE(k.\"strKursRaum\",'') ||	COALESCE(k.\"strKursRaumExtern1\",'') || ' sowie ' || " +
                "k.\"strKursTag2\" || ', ' || EXTRACT(HOUR FROM k.\"dtmKursBeginn2\") || '.' || EXTRACT(MINUTE FROM k.\"dtmKursBeginn2\") || '-' || EXTRACT(HOUR FROM k.\"dtmKursEnde2\") || '.' || EXTRACT (MINUTE FROM k.\"dtmKursEnde2\") || ', Raum: ' || COALESCE(k.\"strKursRaum2\",'') || COALESCE(k.\"strKursRaumExtern2\",'') " + 
                " END " + 
                " ELSE	k.\"strKursTerminFreitext\" || ', Raum: ' || k.\"strKursRaumExtern1\" " +
                "END AS \"sTermin\" " + 
                "from \"tblBdKurs\" k, \"tblSdDozent\" d where k.\"lngSdSeminarID\"=" + seminar.getSeminarID() + 
                " and d.\"lngSdSeminarID\"=k.\"lngSdSeminarID\" " +
                " and d.\"lngDozentID\"=k.\"lngDozentID\" " +
                " order by \"intKursSequence\";");
	
	while(rKurse.next()){
		if(!bFirstLoop) out.write(",");
		else bFirstLoop=false;
                
		out.write("{\"kurs\":{" + 
                        "\"titel\":\"" + rKurse.getString("strKursTitel") + "\"," + 
                        "\"titel_en\":\"" + rKurse.getString("strKursTitel_en") + "\"," + 
                        "\"termin\":\"" + shjCore.string2JSON(rKurse.getString("sTermin")) + "\"," +
                        "\"dozent_id\":\"" + rKurse.getString("lngDozentID") + "\"," +
                        "\"dozent_name\":\"" + rKurse.getString("strDozentNachname") + "\"," +
                        "\"beschreibung\":\"" + (1==1 ? shjCore.string2JSON(rKurse.getString("strKursBeschreibung")) : "") + "\"," +
                        "\"literatur\":\"" + (1==1 ? shjCore.string2JSON(rKurse.getString("strKursLiteratur")) : "") + "\"," +
                        "\"erlaubt_kurswahl\":\""  + (rKurse.getBoolean("blnKursScheinanmeldungErlaubt") ? "true" : "false") + "\"," +
                        "\"planungssemester\":\"" + (rKurse.getBoolean("blnKursPlanungssemester") ? "true" : "false") + "\"," +
                        "\"id\":\"" + rKurse.getString("lngKursID") + "\"" + "}}");
	}
	//rKurse.close();
%>]}