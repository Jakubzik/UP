<%--
    JSP-Object "student/leistung/get"

    SYNOPSIS (German)
    ===========================
    2012, Nov 16, shj    erzeugt. 
    2013, Dez 26, shj    überarbeitet.
    
    Üblicher Lifecycle: GET

    Liefert die Leistungen eines/r Studierenden
    
    Expected SESSION PROPERTIES
    ===========================
    Sitzungsoption I: Student
    - muss angemeldet sein und als Parameter 
      seine (ihre) Matrikelnummer übergeben ("matrikelnummer").

    Sitzungsoption II: Lehrende(r), selbst ausgestellte Leistungen
    - muss angemeldet sein und einen 
      AccessLevel von 1 oder mehr haben,
      aber weniger als 500. Bekommt eine Liste 
      derjenigen Leistungen zum/r Studierenden, 
      die sie/er selbst ausgestellt hat.

    Sitzungsoption III Lehrende(r), alle Leistungen
    - angemeldete/r Lehrende/r mit 
      AccessLevel von 500 oder mehr. Bekommt 
      eine Liste aller Leistungen zum/r 
      Studierenden

    Expected PARAMETER(S):
    ===========================
    matrikelnummer  [long]              Matrikelnummer des/r angfragenden Studierenden
    [leistung_id]   [long, optional]    } Spezifika der Leistung, die 
    [leistung_count][long, optional]    } angefragt wird (unused)
    
    Returns
    =======
    {
        "leistungen": [
            {
                "leistung": {
                    "name": "Einführung Literaturwissenschaft",
                    "name_en": "Introduction to the Study of English Literatures",
                    "matrikelnummer": "2893449",
                    "id": "1",
                    "count": "1",
                    "ects": "4",
                    "kurstitel": "",
                    "kurstitel_en": "",
                    "note": "2.0",
                    "notenbezeichnung": "Gut",
                    "noten_id": "4",
                    "bestanden": "t",
                    "leistungstyp": "",
                    "details": "",
                    "bemerkung": ";",
                    "is_anmeldung": "f",
                    "datum": "17.02.2011",
                    "aussteller_nachname": "Schloss",
                    "aussteller_vorname": "Dietmar",
                    "aussteller_titel": "Prof. Dr.",
                    "anerkannt": "f",
                    "modul": "100015"
                }
            },
            ... 
            ...
        ]
    }    
--%><%@page contentType="text/json" pageEncoding="UTF-8" import="java.sql.ResultSet,de.shj.UP.data.Dozent,de.shj.UP.data.shjCore" session="true" isThreadSafe="false"  errorPage="../../error.jsp" %>
<jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean id="seminar" scope="session" class="de.shj.UP.HTML.HtmlSeminar" />
<jsp:useBean id="student" scope="session" class="de.shj.UP.beans.config.student.StudentBean" />
<jsp:useBean id="sd" scope="session" class="de.shj.UP.util.SessionData" />
<%@include file="../../../fragments/checkVersion.jsp" %>
<%long lERR_BASE=101000 + 100;    // Leistung + Get
    if(sd.getSessionType().equals("student")){%>
<%@include file="../../../fragments/checkLoginStudent.jsp" %>    
<%if(!student.getMatrikelnummer().equals(request.getParameter("matrikelnummer")))
    throw new Exception("{\"error\":\"Die Sitzung ist abgelaufen, bitte melden Sie sich neu an.\",\"errorDebug\":\"Abruf der Leistung aus Studierendensicht, aber die Sitzungs-Matrikelnummer entspricht nicht der übergebenen Matrikelnummer. Abbruch sicherheitshalber.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":50}");
}else{%>
<%@include file="../../../fragments/checkLogin.jsp" %>
<%@include file="../../../fragments/checkAccess1.jsp" %>
<%@include file="../../../fragments/checkInitStudent.jsp" %>
<%}%>
<%!
ResultSet getLeistungen(de.shj.UP.beans.config.student.StudentBean student, de.shj.UP.data.Dozent d, HttpServletRequest r, long lERR_BASE) throws Exception{
        String sItem="";
        if(r.getParameter("leistung_id")!=null){
            try{
                sItem=" \"lngLeistungsID\"=" + Long.parseLong(r.getParameter("leistung_id")) + " and ";
            }catch(Exception eNotNumeric){
                throw new Exception("{\"error\":\"Es wurde eine Leistung mit einer nicht existierenden ID angefordert.\",\"errorDebug\":\"Die übergebene Id (" + r.getParameter("leistung_id") + ") entspricht nicht der Konvention.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":50}");
            }
        }
        if(r.getParameter("leistung_count")!=null){
            try{
                sItem=" \"lngStudentLeistungCount\"=" + Long.parseLong(r.getParameter("leistung_count")) + " and ";
            }catch(Exception eNotNumeric){
                throw new Exception("{\"error\":\"Es wurde eine Leistung mit einer nicht existierenden Lfd. Nr. angefordert.\",\"errorDebug\":\"Die übergebene Count (" + r.getParameter("leistung_count") + ") entspricht nicht der Konvention.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":50}");
            }
        }

	return student.sqlQuery("SELECT " +
                        "x.\"lngLeistungsID\", " +
                        "x.\"blnStudentLeistungAnerkannt\"," +
                        "x.\"lngStudentLeistungCount\", " +
                        "n.\"intNoteID\", " +
                        "n.\"sngNoteECTSCalc\", " +
                        "n.\"strNoteECTSGrade\", " +
                        "n.\"strNoteNameDE\", " +
                        "n.\"blnNoteBestanden\", " +
                        "x.\"strSLKursTitel\", " +
                        "x.\"strSLKursTitel_en\", " +
                        "x.\"strStudentLeistungDetails\", " +
                        "x.\"sngStudentLeistungCreditPts\", " +
                        "x.\"dtmStudentLeistungAusstellungsd\", " +
                        "x.\"strStudentLeistungCustom3\", " +
                        "x.\"strStudentLeistungBemerkung\", " +
                        "x.\"strStudentLeistungAussteller\", " +
                        "x.\"strStudentLeistungAusstellerVor\", x.\"strStudentLeistungAussteller\", " +
                        "x.\"strStudentLeistungAusstellerTit\", " +
                        "x.\"lngModulID\", " +
                        "Case when x.\"lngModulID\" is null THEN 0 ELSE x.\"lngModulID\" END as modul, " +
                        "x.\"blnStudentLeistungKlausuranmeldung\", " +
                        "l.\"strLeistungBezeichnung\", " +
                        "l.\"strLeistungBezeichnung_en\", " +
                        "x.\"blnStudentLeistungEditierbar\" " +
                      "FROM " +
                        "\"tblBdStudentXLeistung\" x, " +
                        "\"tblSdLeistung\" l, " +
                        "\"tblSdNote\" n " +
                      "WHERE " +
                        "n.\"lngSdSeminarID\" = x.\"lngSdSeminarID\" AND " +
                        "n.\"intNoteID\" = x.\"intNoteID\" AND " +
                        "l.\"lngSdSeminarID\" = x.\"lngSdSeminarID\" AND " +
                        "l.\"lngLeistungID\" = x.\"lngLeistungsID\" AND " +
                        "x.\"lngSdSeminarID\" = " + student.getSeminarID() + " AND " +
                        "x.\"blnStudentLeistungKlausuranmeldung\" != true and " +
                        ((d.getDozentNachname()==null || d.getDozentAccessLevel()>=500) ? "" : "x.\"lngDozentID\" = " + d.getDozentID() + " AND ") + // Nue wenn es eine studentische Anfrage ist (=null) oder hohe Berechtigung vorliegt
                        "x.\"strMatrikelnummer\" = '" + Long.parseLong(student.getMatrikelnummer()) + "' order by \"dtmStudentLeistungAusstellungsd\"");


}%>{"leistungen":[<%
    boolean bFirst=true;ResultSet rBem=getLeistungen(student, user, request, lERR_BASE);while(rBem.next()){
	if(!bFirst) out.write(",");bFirst=false;%>
        {"leistung":{"name":"<%=shjCore.string2JSON(rBem.getString("strLeistungBezeichnung")) %>",
        "name_en":"<%=shjCore.string2JSON(rBem.getString("strLeistungBezeichnung_en")) %>",
	"matrikelnummer":"<%=student.getMatrikelnummer()%>",
	"id":"<%=rBem.getString("lngLeistungsID") %>",
	"count":"<%=rBem.getString("lngStudentLeistungCount") %>",
	"ects":"<%=rBem.getString("sngStudentLeistungCreditPts") %>",
        "kurstitel":"<%=shjCore.string2JSON(shjCore.normalize(rBem.getString("strSLKursTitel"))) %>",
	"kurstitel_en":"<%=shjCore.string2JSON(shjCore.normalize(rBem.getString("strSLKursTitel_en"))) %>",
	"note":"<%=rBem.getFloat("sngNoteECTSCalc")==0 ? rBem.getString("strNoteNameDE") : rBem.getFloat("sngNoteECTSCalc") %>",
	"notenbezeichnung":"<%=rBem.getString("strNoteNameDE") %>",
	"noten_id":"<%=rBem.getString("intNoteID") %>",
        "bestanden":"<%=rBem.getString("blnNoteBestanden") %>",
	"leistungstyp":"<%=shjCore.string2JSON(shjCore.normalize(rBem.getString("strStudentLeistungCustom3"))) %>",
	"details":"<%=shjCore.string2JSON(shjCore.normalize(rBem.getString("strStudentLeistungDetails"))) %>",
	"bemerkung":"<%=shjCore.string2JSON(shjCore.normalize(rBem.getString("strStudentLeistungBemerkung"))) %>",
        "is_anmeldung":"<%=rBem.getString("blnStudentLeistungKlausuranmeldung")%>",
	"datum":"<%try{%><%=shjCore.shjGetGermanDate(rBem.getDate("dtmStudentLeistungAusstellungsd")) %><%}catch(Exception eNoDateGiven){%><%=shjCore.shjGetGermanDate(user.g_TODAY) %><%}%>",
        "aussteller_nachname":"<%=rBem.getString("strStudentLeistungAussteller") %>",
        "aussteller_vorname":"<%=rBem.getString("strStudentLeistungAusstellerVor") %>",
        "aussteller_titel":"<%=rBem.getString("strStudentLeistungAusstellerTit") %>",
        "anerkannt":"<%=rBem.getString("blnStudentLeistungAnerkannt")%>",
	"modul":"<%=rBem.getString("modul") %>"
	}}
<%}%>]}