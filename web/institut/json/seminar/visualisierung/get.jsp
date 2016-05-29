<%--
    JSP-Object "seminar/visualisierung/get"

    SYNOPSIS (German)
    ===========================
    2013, Mar 5, shj    erzeugt. 
    2013, Dez 28, shj   überarbeitet
    
    Üblicher Lifecycle: GET

    Die zur Benotung von Leistungen konfigurierten  
    Noten (tblSdNote).
    
    Expected SESSION PROPERTIES
    ===========================
    user        muss initialisiert sein (AccessLevel egal) ODER
    student     muss initialisiert sein.

    Expected PARAMETER(S):
    ===========================
    
    Returns
    =======

    Sample Usage
    ============

    
--%><%@page contentType="text/json" pageEncoding="UTF-8" import="java.sql.ResultSet,de.shj.UP.data.Dozent,de.shj.UP.data.shjCore" session="true" isThreadSafe="false"  errorPage="../../error.jsp" %>
<jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean id="seminar" scope="session" class="de.shj.UP.HTML.HtmlSeminar" />
<jsp:useBean id="sd" scope="session" class="de.shj.UP.util.SessionData" /><jsp:useBean id="student" scope="session" class="de.shj.UP.beans.config.student.StudentBean" />
<%@include file="../../../fragments/checkVersion.jsp" %>
<%long lERR_BASE=300000 + 100;    // Visualisierung + Get %>
<%@include file="../../../fragments/checkLogin.jsp" %>
<%
/**
 * Idee vgl.: http://bl.ocks.org/mbostock/4063269
 * 
 * 
 * Filter:
 * jahr=<int> Jahr, in dem die Leistung ausgestellt wurde.
 * note=<var> Note:
 *      entweder Notenwert (1,2,3,4,5) oder "anerkannt" -- letzteres schließt die Anerkennung von Noten mit ein.
 * */

String sFilter="";
int g_iNoteAnerkanntID=14;
if(request.getParameter("jahr")!=null){
    int iYear=Integer.parseInt(request.getParameter("jahr"));
    sFilter += "\"dtmStudentLeistungAusstellungsd\">='" + (iYear-1) + "-1-1' and " +
            "\"dtmStudentLeistungAusstellungsd\"<'" + (iYear) + "-1-1' and ";
}

if(request.getParameter("fach")!=null){
    int iFach=Integer.parseInt(request.getParameter("fach"));
    sFilter += "(s.\"intStudentFach1\"=" + iFach + 
            " or s.\"intStudentFach2\"=" + iFach + 
            " or s.\"intStudentFach3\"=" + iFach + 
            " or s.\"intStudentFach4\"=" + iFach + 
            " or s.\"intStudentFach5\"=" + iFach + 
            " or s.\"intStudentFach6\"=" + iFach + 
            ") and ";
}

if(request.getParameter("note")!=null){
    if(request.getParameter("note").equals("anerkannt")){
        sFilter += " (n.\"intNoteID\"=" + g_iNoteAnerkanntID + " or x.\"blnStudentLeistungAnerkannt\"=true) and ";
    }else{
        int iNote=Integer.parseInt(request.getParameter("note"));
        sFilter += " \"sngNoteECTSCalc\"=" + iNote + " and ";
    }
}
ResultSet rStat = seminar.sqlQuery("SELECT " +
  "l.\"strLeistungZuordnung\", " +
  "l.\"strLeistungBezeichnung\", " +
  "count(x.\"strMatrikelnummer\") as anzahl " +
"FROM " +
  "\"tblBdStudentXLeistung\" x, " +
  "\"tblSdLeistung\" l, " +
  "\"tblBdStudent\" s, " +
  "\"tblSdNote\" n " +
"WHERE " + sFilter +
  "x.\"lngSdSeminarID\" = n.\"lngSdSeminarID\" AND " +
  "x.\"strMatrikelnummer\"=s.\"strMatrikelnummer\" AND " + 
  "l.\"lngSdSeminarID\" = x.\"lngSdSeminarID\" AND " +
  "l.\"lngLeistungID\" = x.\"lngLeistungsID\" AND " +
  "n.\"intNoteID\" = x.\"intNoteID\" and " +
  "x.\"lngSdSeminarID\"=" + seminar.getSeminarID() + ""
+ "group by l.\"strLeistungZuordnung\",l.\"strLeistungBezeichnung\""
        + " order by \"strLeistungZuordnung\";");

String sZuordnungCurr="";
String sZuordnungAlt="";
boolean bStart=true;
boolean bNewZu=false;
out.write("{\"name\":\"signup\",\"children\":[");
while(rStat.next()){
    sZuordnungCurr=rStat.getString("strLeistungZuordnung");
    if(!sZuordnungCurr.equals(sZuordnungAlt)){
        if(bStart) bStart=false;
        else out.write("]},");
        
        bNewZu=true;
        sZuordnungAlt=sZuordnungCurr;
        out.write("{\"name\":\"" + sZuordnungCurr + "\",\"children\":[");
    }else{
        if(!bNewZu) out.write(",");
        if(bNewZu) bNewZu=false;
        out.write("{\"name\":\"" + rStat.getString("strLeistungBezeichnung") + "\", \"size\":" + rStat.getString("anzahl") + "}");
    }
}
%>]}]}

