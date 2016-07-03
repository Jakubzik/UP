<%--
    JSP-Object "transkript/get"

    SYNOPSIS (German)
    ===========================
    2013, Okt 29, shj
    Erzeugt. 
    
    Liefert eine Liste der Transkripte -- nicht aber deren 
    Inhalt.

    Die permanent gespeicherten Transkripte des Prüfungsamts 
    (durch "blnStudentDokumentDauertranskript" markiert) werden 
    nur an Nutzer mit einer Berechtigung von 500 in der 
    Liste aufgeführt.

    Das einzelne Transkript (außer den permanent gespeicherten) 
    bekommt man über "SignUp/Faculty/dokument.jsp".

    
    Expected SESSION PROPERTIES
    ===========================
    user        muss initialisiert sein (AccessLevel 500 oder mehr),
    ODER Sitzungstyp student (d.h. es handelt sich um eine(n) angmeldete(n) Studierende(n)).

    Expected PARAMETER(S):
    ===========================
    matrikelnummer          [long]

    
    Returns
    =======
    {transkripte:[
        {transkript:{
               seminar_id:
               matrikelnummer:
               dok_name:
               datum:
               verifikationscode:
               <permanent:[true|false]> (handelt es sich um ein permanentes Transkript des Prüfungsamts?)
            }
         }
    ]}

    Sample Usage
    ============
    
--%>
<%@ page contentType="text/json" pageEncoding="UTF-8" import="java.sql.ResultSet,java.text.SimpleDateFormat, de.shj.UP.data.Dozent,de.shj.UP.data.shjCore" session="true" isThreadSafe="false" errorPage="../../error.jsp"%>
<%@page import="java.util.Locale"%><jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean id="seminar" scope="session" class="de.shj.UP.logic.SeminarData" />
<jsp:useBean id="student" scope="session" class="de.shj.UP.logic.StudentData" />
<jsp:useBean id="sd" scope="session" class="de.shj.UP.util.SessionData" />
<%@include file="../../../fragments/checkVersion.jsp" %>
<%long lERR_BASE=109000 + 100;    // Transkriptdruck + Get
if(sd.getSessionType().equals("student")){%>
<%@include file="../../../fragments/checkLoginStudent.jsp" %>    
<%
if(!student.getMatrikelnummer().equals(request.getParameter("matrikelnummer")))
    throw new Exception("{\"error\":\"Die Sitzung ist abgelaufen, bitte melden Sie sich neu an.\",\"errorDebug\":\"Abruf der gedruckten Transkripte aus Studierendensicht, aber die Sitzungs-Matrikelnummer entspricht nicht der übergebenen Matrikelnummer. Abbruch sicherheitshalber.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":50}");
}else{%>
<%@include file="../../../fragments/checkLogin.jsp" %>
<%@include file="../../../fragments/checkAccess500.jsp" %>
<%@include file="../../../fragments/checkInitStudent.jsp" %>
<%}%>
{
 "transkripte":[
<%  
    // \"strDokument\", der HTML-Text, ausgelassen.
    ResultSet rTranskripte = student.sqlQuery(
            "SELECT \"lngSdSeminarID\", \"strMatrikelnummer\", \"dtmStudentDokumentDatum\", " +
            "\"strVerifikationscode\", \"strStudentDokumentName\",\"blnStudentDokumentDauertranskript\" " +
            "FROM \"tblBdStudentDokument\" where " + 
            "\"strMatrikelnummer\"='" + student.getMatrikelnummer() + "' "
                    + "and \"lngSdSeminarID\"=" + seminar.getSeminarID() + 
                    ((!sd.getSessionType().equals("student") && user.getDozentAccessLevel()>=500) ? "" : " and \"blnStudentDokumentDauertranskript\" = false ") 
                    + "order by \"dtmStudentDokumentDatum\" desc;"
    );
    boolean bStart=true;
    while(rTranskripte.next()){
            if(!bStart) out.write(",");bStart=false;%>

            {"transkript":{"seminar_id":"<%=seminar.getSeminarID() %>",
                    "matrikelnummer":"<%=student.getMatrikelnummer()%>",
                    "dok_name":"<%=rTranskripte.getString("strStudentDokumentName") %>",
                    "datum":"<%=rTranskripte.getString("dtmStudentDokumentDatum") %>",
                    "verifikationscode":"<%=rTranskripte.getString("strVerifikationscode") %>",
                    "permanent":"<%=rTranskripte.getBoolean("blnStudentDokumentDauertranskript") %>"
                    }}<%
   }
%>]}