<%--
    JSP-Object "student/signup/get"

    Liefert Arrays der IDs von Kursen, die 
    der Studierende gewählt hat oder zu denen 
    er vorgemerkt ist.

    Die Objekte in den Arrays enthalten Angaben 
    darüber, ob dieser Kurs zugeschlagen wurde etc. 
    Siehe unten unter "Returns"

    SYNOPSIS (German)
    ===========================
    2013, June 11, shj    drzeugt. 
    2014, Feb 3, shj      Routine Durchsicht
    
    Üblicher Lifecycle: GET

    Expected SESSION PROPERTIES
    ===========================
    seminar initialisiert,
    student initialisiert

    Expected PARAMETER(S):
    ===========================
    Expected Backend Version
    
    Returns
    =======
    Anmeldungen in tabellarischer Struktur
    {
        "anmeldungen": [
            {
                "kurstyp_id": "15",
                "kurs_id": "14963",
                "seminar_id": "1",
                "prioritaet": "9",
                "zuschlag": "false",
                "datum": "2014-02-04",
                "fixiert": "false"
            },
            ...
            ...
        ],

        "vormerkungen": [
            {
                "kurstyp_id": "15",
                "kurs_id": "14963",
                "seminar_id": "1",
                "kurstyp_wunsch_id": "15",
                "kurs_wunsch_id": "7"
            },

        ]
    }
    Sample Usage
    ============
    
--%>
<%@page contentType="text/json" pageEncoding="UTF-8" import="java.sql.ResultSet,de.shj.UP.logic.AktuellesIterator,java.text.SimpleDateFormat, de.shj.UP.data.Dozent,de.shj.UP.data.shjCore" session="true" isThreadSafe="false"  errorPage="../../error.jsp" %>
<jsp:useBean id="seminar" scope="session" class="de.shj.UP.HTML.HtmlSeminar" />
<jsp:useBean id="student" scope="session" class="de.shj.UP.beans.config.student.StudentBean" />
<%@include file="../../../fragments/checkVersion.jsp" %>
<%@include file="../../../fragments/checkLoginStudent.jsp" %>
{"anmeldungen":[<%
    boolean bFirst=true;
    ResultSet rAnm=seminar.sqlQuery("SELECT \"lngSdSeminarID\", \"strMatrikelnummer\", \"lngKursID\", \"lngKurstypID\", " + 
            "\"intAnmeldungPrioritaet\", \"lngAnmeldungRandom\", \"blnAnmeldungZuschlag\", " + 
            "\"dtmAnmeldungDatum\", \"blnAnmeldungFixiert\" " + 
        "FROM \"tblBdAnmeldung\" where \"lngSdSeminarID\"=" + seminar.getSeminarID() + 
            " and  \"strMatrikelnummer\"='" + student.getMatrikelnummer() + "' order by \"lngKurstypID\", \"intAnmeldungPrioritaet\" desc;");
            while(rAnm.next()){
                if(!bFirst) out.write(",");
                bFirst=false;
                out.write("{" + 
                      "\"kurstyp_id\":\"" + rAnm.getString("lngKurstypID") + "\"," + 
                      "\"kurs_id\":\"" + rAnm.getString("lngKursID") + "\"," + 
                      "\"seminar_id\":\"" + seminar.getSeminarID() + "\"," + 
                      "\"prioritaet\":\"" + rAnm.getString("intAnmeldungPrioritaet") + "\"," + 
                      "\"zuschlag\":\"" + (rAnm.getBoolean("blnAnmeldungZuschlag") ? "true" : "false") + "\"," + 
                      "\"datum\":\"" + rAnm.getString("dtmAnmeldungDatum") + "\"," + 
                      "\"fixiert\":\"" + (rAnm.getBoolean("blnAnmeldungFixiert") ? "true" : "false") + "\"" + 
                    "}");
            }
     bFirst=true;
     rAnm=seminar.sqlQuery("SELECT \"lngSdSeminarID\", \"strMatrikelnummer\", \"lngKursID\", \"lngKurstypID\", \"lngKursIDWunsch\", \"lngKurstypIDWunsch\" " + 
        "FROM \"tblBdAnmeldungSwap\" where \"lngSdSeminarID\"=" + seminar.getSeminarID() + 
            " and  \"strMatrikelnummer\"='" + student.getMatrikelnummer() + "' order by \"lngKurstypID\" desc;");
            while(rAnm.next()){
                if(!bFirst) out.write(",");
                else out.write("],\"vormerkungen\":[");
                bFirst=false;
                out.write("{" + 
                      "\"kurstyp_id\":\"" + rAnm.getString("lngKurstypID") + "\"," + 
                      "\"kurs_id\":\"" + rAnm.getString("lngKursID") + "\"," + 
                      "\"seminar_id\":\"" + seminar.getSeminarID() + "\"," + 
                      "\"kurstyp_wunsch_id\":\"" + rAnm.getString("lngKurstypIDWunsch") + "\"," + 
                      "\"kurs_wunsch_id\":\"" + rAnm.getString("lngKursIDWunsch") + "\"" + 
                    "}");
            }

%>]}

