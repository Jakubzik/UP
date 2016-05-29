<%--
    JSP-Object "student/anmeldung/get"

    SYNOPSIS (German)
    ===========================
    2012, Nov 16, shj    erzeugt. 
    2013, Dez 28, shj    überarbeitet
    2014, Jan 8, shj     Korrektur: als anmeldung.datum wird jetzt das Datum 
                                der Anmeldung (ersatzweise heute) angegeben
                                (und nicht das Ausstellungsdatum).
    
    Üblicher Lifecycle: GET

    Eine Anmeldung wird ebenso wie die Leistung in 
    tblBdStudentXLeistung gespeichert, d.h. die 
    Mechanismen zum Aufspüren bzw. Verändern des 
    relevanten Datensatzes sind ähnlich wie 
    bei ../leistung/*.jsp. S.a. dort.

    
    Expected SESSION PROPERTIES
    ===========================
    (a)
    student ist angemeldet, ODER

    (b) 
    user ist angemeldet, mit 
    
    (b1) 1 <= AccessLevel < 500

    (b2) AccessLevel >= 500

    Expected PARAMETER(S):
    ===========================
    matrikelnummer  [long]  muss bei Sitzungstyp (a) mit der 
                            Matrikelnummer des angemeldeten Studierenden
                            übereinstimmen.
    leistung_id     [long]  } kennzeichnen (zus. mit SeminarID und Matrikelnummer)
    leistung_count  [long]  } die Anmeldung

    Returns
    =======
    {
        "anmeldungen": [
            {
                "anmeldung": {
                    "name": "Pronunciation Practice/Aussprachetest",
                    "name_en": "Pronunciation Practice",
                    "matrikelnummer": "3036960",
                    "id": "3",
                    "count": "1",
                    "ects": "1",
                    "kurstitel": "Begleitkurs",
                    "kurstitel_en": "Begleitkurs",
                    "leistungstyp": "",
                    "details": "",
                    "bemerkung": "",
                    "is_anmeldung": "t",
                    "fristende": "22.01.2012",
                    "datum": "28.12.2013",
                    "aussteller_nachname": "Jakubzik",
                    "aussteller_vorname": "Heiko",
                    "aussteller_titel": "Dr.",
                    "modul": "112"
                }
            },
            {
                "anmeldung": {
                    "name": "Proseminar I Kulturwissenschaft (anwendungsorientiert)",
                    "name_en": "Introductory Seminar in Cultural Studies (practical orientation)",
                    "matrikelnummer": "3036960",
                    "id": "10080",
                    "count": "1",
                    "ects": "5.5",
                    "kurstitel": "Amerikanischer Humor im 20. Jahrhundert",
                    "kurstitel_en": "American Humor in the 20th Century",
                    "leistungstyp": "",
                    "details": "",
                    "bemerkung": "",
                    "is_anmeldung": "t",
                    "fristende": "17.07.2012",
                    "datum": "28.12.2013",
                    "aussteller_nachname": "Bloom",
                    "aussteller_vorname": "Steven",
                    "aussteller_titel": "Dr.",
                    "modul": "119"
                }
            }    
--%>
<%@page contentType="text/json" pageEncoding="UTF-8" import="java.sql.ResultSet,de.shj.UP.data.Dozent,de.shj.UP.data.shjCore" session="true" isThreadSafe="false"  errorPage="../../error.jsp" %>
<jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean id="seminar" scope="session" class="de.shj.UP.HTML.HtmlSeminar" />
<jsp:useBean id="student" scope="session" class="de.shj.UP.beans.config.student.StudentBean" />
<jsp:useBean id="sd" scope="session" class="de.shj.UP.util.SessionData" />
<%@include file="../../../fragments/checkVersion.jsp" %>
<%long lERR_BASE=102000 + 100;    // Leistung + Get
    if(sd.getSessionType().equals("student")){%>
<%@include file="../../../fragments/checkLoginStudent.jsp" %>
<%
if(!student.getMatrikelnummer().equals(request.getParameter("matrikelnummer")))
    throw new Exception("{\"error\":\"Die Sitzung ist abgelaufen, bitte melden Sie sich neu an.\",\"errorDebug\":\"Abruf der Leistung aus Studierendensicht, aber die Sitzungs-Matrikelnummer entspricht nicht der übergebenen Matrikelnummer. Abbruch sicherheitshalber.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":50}");
}else{%>
<%@include file="../../../fragments/checkLogin.jsp" %>
<%@include file="../../../fragments/checkAccess1.jsp" %>
<%@include file="../../../fragments/checkInitStudent.jsp" %>
<%}%>
<%!
ResultSet getAnmeldungen(de.shj.UP.beans.config.student.StudentBean student, de.shj.UP.data.Dozent d, HttpServletRequest r, long lERR_BASE) throws Exception{
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
                        "x.\"lngStudentLeistungCount\", " +
                        "x.\"strSLKursTitel\", " +
                        "x.\"strSLKursTitel_en\", " +
                        "x.\"strStudentLeistungDetails\", " +
                        "x.\"sngStudentLeistungCreditPts\", " +
                        "x.\"dtmStudentLeistungAusstellungsd\", " +
                        "x.\"dtmStudentLeistungAntragdatum\"," +
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
                        "x.\"dtmSLKursScheinanmeldungBis\", "+
                        "x.\"blnStudentLeistungEditierbar\" " +
                      "FROM " +
                        "\"tblBdStudentXLeistung\" x, " +
                        "\"tblSdLeistung\" l " +
                      "WHERE " +
                        "l.\"lngSdSeminarID\" = x.\"lngSdSeminarID\" AND " +
                        "l.\"lngLeistungID\" = x.\"lngLeistungsID\" AND " +
                        "x.\"blnStudentLeistungKlausuranmeldung\" = true and " +
                        "x.\"lngSdSeminarID\" = " + student.getSeminarID() + " AND " +
                        ((d.getDozentNachname()==null || d.getDozentAccessLevel()>=500) ? "" : "x.\"lngDozentID\" = " + d.getDozentID() + " AND ") + // Nue wenn es eine studentische Anfrage ist (=null) oder hohe Berechtigung vorliegt
                        "x.\"strMatrikelnummer\" = '" + Long.parseLong(student.getMatrikelnummer()) + "'");

}%>{"anmeldungen":[<%
    boolean bFirst=true;ResultSet rBem=getAnmeldungen(student, user, request, lERR_BASE);while(rBem.next()){
	if(!bFirst) out.write(",");bFirst=false;%>
        {"anmeldung":{"name":"<%=shjCore.string2JSON(rBem.getString("strLeistungBezeichnung")) %>",
        "name_en":"<%=shjCore.string2JSON(rBem.getString("strLeistungBezeichnung_en")) %>",
	"matrikelnummer":"<%=student.getMatrikelnummer()%>",
	"id":"<%=rBem.getString("lngLeistungsID") %>",
	"count":"<%=rBem.getString("lngStudentLeistungCount") %>",
	"ects":"<%=rBem.getString("sngStudentLeistungCreditPts") %>",
        "kurstitel":"<%=shjCore.string2JSON(shjCore.normalize(rBem.getString("strSLKursTitel"))) %>",
	"kurstitel_en":"<%=shjCore.string2JSON(shjCore.normalize(rBem.getString("strSLKursTitel_en"))) %>",
	"leistungstyp":"<%=shjCore.string2JSON(shjCore.normalize(rBem.getString("strStudentLeistungCustom3"))) %>",
	"details":"<%=shjCore.string2JSON(shjCore.normalize(rBem.getString("strStudentLeistungDetails"))) %>",
	"bemerkung":"<%=shjCore.string2JSON(shjCore.normalize(rBem.getString("strStudentLeistungBemerkung"))) %>",
        "is_anmeldung":"<%=rBem.getString("blnStudentLeistungKlausuranmeldung")%>",
        "fristende":"<%try{%><%=shjCore.shjGetGermanDate(rBem.getDate("dtmSLKursScheinanmeldungBis")) %><%}catch(Exception eNoDateGiven){%>01.01.2000<%}%>",
	"datum":"<%try{%><%=shjCore.shjGetGermanDate(rBem.getDate("dtmStudentLeistungAntragdatum")) %><%}catch(Exception eNoDateGiven){%><%=shjCore.shjGetGermanDate(user.g_TODAY) %><%}%>",
        "aussteller_nachname":"<%=rBem.getString("strStudentLeistungAussteller") %>",
        "aussteller_vorname":"<%=rBem.getString("strStudentLeistungAusstellerVor") %>",
        "aussteller_titel":"<%=rBem.getString("strStudentLeistungAusstellerTit") %>",
	"modul":"<%=rBem.getString("modul") %>"
	}}
<%}%>]}