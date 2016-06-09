<%--
    JSP-Object "seminar/signup/fehler/get"
    @Revision: Mar 3, 2014 -- hj

    SYNOPSIS (German)
    ===========================
    2014, Mar 3, shj
    Erzeugt. 
    
    Wenn die Kursbelegung eingeteilt wurde, liefert fehler/get
    eine Übersicht über die dabei entstandenen Fehler.

    Es werden (1) "offene_anmeldungen" ausgewiesen, also 
    Studierende, die leer ausgegangen sind, bzw. die 
    in einem gewählten Kurstyp _gar keinen_ Platz 
    bekommen haben. (Das kommt insbes. wegen der 
    automatischen Bereinigung von Überlappungen vor).

    (2) werden Studierende genannt, denen Kurse zugewiesen 
    wurden, die sich überlappen. Die Kurse werden dabei 
    _beide_ ausgegeben, d.h. bei "ueberlappungen" treten 
    immer zwei Einträge nacheinander mit gleicher 
    Matrikelnummer auf.
    
    Expected SESSION PROPERTIES
    ===========================
    user        muss initialisiert sein (AccessLevel >= 500),

    Expected PARAMETER(S):
    ===========================

    
    Returns
    =======
    {
    "fehler": {
        "offene_anmeldungen": [
            {
                "matrikelnummer": "3003325",
                "kurstyp": "Proseminar II Literaturwissenschaft",
                "kurstyp_id": "15"
            },
            ...
            ...
        ],
        "ueberlappungen": [
            {
                "matrikelnummer": "3003325",
                "kurstyp": "Proseminar II Literaturwissenschaft",
                "kurstyp_id": "15"
            },
            {
                "matrikelnummer": "2922313",
                "kurstyp": "Proseminar II historische Sprachwissenschaft (Überblick)",
                "kurstyp_id": "10020"
            },
            ...
            ...
       ]
     }

--%>
<%@page import="de.shj.UP.logic.SeminarData"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page contentType="text/json" pageEncoding="UTF-8" import="java.sql.ResultSet,de.shj.UP.data.Dozent,de.shj.UP.data.shjCore" session="true" isThreadSafe="false"  errorPage="../../../error.jsp" %>
<jsp:useBean id="seminar" scope="session" class="de.shj.UP.logic.SeminarData" />
<jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" />
<%@include file="../../../../fragments/checkVersion.jsp" %>
<%@include file="../../../../fragments/checkLogin.jsp" %>    
<%@include file="../../../../fragments/checkAccess500.jsp" %>
{"fehler":{"offene_anmeldungen":[
<%
lERR_BASE=212000 + 100;sem=seminar;long lKurstypID=-1;    // Fehler + Get
ResultSet r=getStudentsWithoutPlace();
boolean bStart=true;
while(r.next()){
    if(!bStart){out.write(',');}else{bStart=false;}
    out.write("{\"matrikelnummer\":\"" + r.getString("strMatrikelnummer") + 
            "\",\"kurstyp\":\"" + r.getString("strKurstypBezeichnung") + 
            "\",\"kurstyp_id\":\"" + r.getString("lngKurstypID") + "\"}");
}
%>],"ueberlappungen":[
<%ResultSet r2=getStudentsWithOverlap();
bStart=true;
while(r2.next()){
    if(!bStart){out.write(',');}else{bStart=false;}
    out.write("{\"matrikelnummer\":\"" + r2.getString("strMatrikelnummer") + 
            "\",\"kurstyp\":\"" + r2.getString("strKurstypBezeichnung") + 
            "\",\"kurstyp_id\":\"" + r2.getString("lngKurstypID") + "\"}");
}
%>]}}
<%! long lERR_BASE; HtmlSeminar sem;int iScaleMax=-1;

ResultSet getStudentsWithoutPlace()throws Exception{
    return sem.sqlQuery(
                "select distinct " +
                "t.\"strKurstypBezeichnung\"," +
                "a.\"strMatrikelnummer\", " +
                "a.\"lngKurstypID\" from " +
        "\"tblBdAnmeldung\" a, \"tblSdKurstyp\" t " +
        "where " +
                "a.\"lngSdSeminarID\"=" + sem.getSeminarID() + " and " +
                "t.\"lngSdSeminarID\"=a.\"lngSdSeminarID\" and " +
                "t.\"lngKurstypID\"=a.\"lngKurstypID\" and " +
                "(select count(*) " +
                        "from \"tblBdAnmeldung\" a2 " +
                        "where a2.\"lngSdSeminarID\"=a.\"lngSdSeminarID\" and " +
                        "a2.\"lngKurstypID\"=a.\"lngKurstypID\" and " +
                        "a2.\"strMatrikelnummer\"=a.\"strMatrikelnummer\" and " +
                        "a2.\"blnAnmeldungZuschlag\"=true)!=1 " +
        "order by a.\"lngKurstypID\"");
}

ResultSet getStudentsWithOverlap()throws Exception{
    //return new de.shj.UP.algorithm.SignUp(sem.getSeminarID()).getOverlaps();
    return sem.sqlQuery("select a.\"strMatrikelnummer\", a.\"lngKurstypID\", a.\"lngKursID\", "
            + "t.\"strKurstypBezeichnung\" from \"tblBdAnmeldung\" a, \"tblSdKurstyp\" t where " +
                "a.\"lngSdSeminarID\"=" + sem.getSeminarID() + " and " +
                "a.\"blnAnmeldungZuschlag\"='t' and " +
                "t.\"lngSdSeminarID\"=a.\"lngSdSeminarID\" and " +
                "t.\"lngKurstypID\"=a.\"lngKurstypID\" and " +
                "exists(select " +
                "a2.\"strMatrikelnummer\" from \"tblBdAnmeldung\" a2, \"tblBdKurs\" k, \"tblBdKurs\" k2 where  " +
                "a2.\"lngSdSeminarID\"=" + sem.getSeminarID() + " and  " +
                "k2.\"lngSdSeminarID\"=" + sem.getSeminarID() + " and " +
                "a2.\"blnAnmeldungZuschlag\"='t' and " +
                "a2.\"strMatrikelnummer\"=a.\"strMatrikelnummer\" and  " +
                "a2.\"lngKursID\"=k2.\"lngKursID\" and " +
                "a.\"lngKursID\"=k.\"lngKursID\" and " +
                "k.\"lngSdSeminarID\"=" + sem.getSeminarID() + " and " +

                "k2.\"lngKursID\" != k.\"lngKursID\" and " + 
                "k2.\"strKursTag\"=k.\"strKursTag\" and " +
                "(" +
                  "(k.\"dtmKursBeginn\",k.\"dtmKursEnde\") OVERLAPS (k2.\"dtmKursBeginn\", k2.\"dtmKursEnde\") or " +  

                  "(k.\"dtmKursBeginn2\",k.\"dtmKursEnde2\") OVERLAPS (k2.\"dtmKursBeginn2\", k2.\"dtmKursEnde2\") or  " +

                  "(k.\"dtmKursBeginn\",k.\"dtmKursEnde\") OVERLAPS (k2.\"dtmKursBeginn2\", k2.\"dtmKursEnde2\") or " +  

                  "(k.\"dtmKursBeginn2\",k.\"dtmKursEnde2\") OVERLAPS (k2.\"dtmKursBeginn\", k2.\"dtmKursEnde\") " + 
                ")) order by \"strMatrikelnummer\";");
}
%>