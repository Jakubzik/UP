<%--
    JSP-Object "student/leistung/delete"

    SYNOPSIS (German)
    ===========================
    2012, Nov 20, shj   erzeugt. 
    2013, Dez 26, shj   überarbeitet.

    Üblicher Lifecycle: DELETE

    
    Expected SESSION PROPERTIES
    ===========================
    user        muss initialisiert sein (AccessLevel 500 oder höher),
    student     muss identische Matrikelnummer haben, die auch
                als Parameter übergeben wird.

    Expected PARAMETER(S):
    ===========================
    matrikelnummer  [long]
    id              [long] } Identifizieren die zu löschende Prüfung
    count           [long] }
    signup-expected-backend-version [text], vgl. fragments/checkVersion.jsp
    
    Returns
    =======
    Object:
    {"success":"true"}

--%>   
<%@ page contentType="text/json" pageEncoding="UTF-8" import="de.shj.UP.data.shjCore" session="true" isThreadSafe="false" errorPage="../../error.jsp"%>
<jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean id="seminar" scope="session" class="de.shj.UP.logic.SeminarData" />
<jsp:useBean id="student" scope="session" class="de.shj.UP.logic.StudentData" />
<%@include file="../../../fragments/checkVersion.jsp" %>
<%@include file="../../../fragments/checkLogin.jsp" %>
<%@include file="../../../fragments/checkAccess500.jsp" %>
<%@include file="../../../fragments/checkInitStudent.jsp" %>
<%
    long lERR_BASE=104000 + 300;    // Prüfung + Delete

    // ==============================================================
    // Schnittstelle
    // ==============================================================
    long lPruefungID = -1;
    long lPruefungCount=-1;
    try{
        lPruefungID=Long.parseLong(request.getParameter("id"));
        lPruefungCount=Long.parseLong(request.getParameter("count"));
    }catch(Exception eNotHandedOver){throw new Exception("{\"error\":\"Die Prüfung kann nicht gelöscht werden.\",\"errorDebug\":\"Der Parameter >id< (" + request.getParameter("id") + ") oder >count< (" + request.getParameter("count") + ") wurde nicht korrekt übergeben.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":100}");}

    if(!user.sqlExe("DELETE FROM \"tblBdStudentPruefungDetail\" where " +
 			"\"lngSdSeminarID\"=" + user.getSdSeminarID() + " and " +
			"\"strMatrikelnummer\"='" + student.getMatrikelnummer() + "' and " +  
			"\"lngSdPruefungsID\"=" + lPruefungID + " and " + 
                        "\"intStudentPruefungCount\"=" + lPruefungCount))
        	throw new Exception("{\"error\":\"Die Prüfungsdetails konnten nicht gelöscht werden. Rätselhaft.\",\"errorDebug\":\"Die Datenbank hat sich verweigert -- Näheres vielleicht in deren Logs?\",\"errorcode\":" + lERR_BASE + 1 + ",\"severity\":50}");
     
    if(!user.sqlExe("DELETE FROM \"tblBdStudentXPruefung\" where " +
 			"\"lngSdSeminarID\"=" + user.getSdSeminarID() + " and " +
			"\"strMatrikelnummer\"='" + student.getMatrikelnummer() + "' and " +  
			"\"lngSdPruefungsID\"=" + lPruefungID + " and " + 
                        "\"intStudentPruefungCount\"=" + lPruefungCount)){
		throw new Exception("{\"error\":\"Die Prüfung konnte nicht gelöscht werden. Rätselhaft.\",\"errorDebug\":\"Die Datenbank hat sich verweigert -- Näheres vielleicht in deren Logs? Achtung: die Prüfungsdetails wurden bereits gelöscht.\",\"errorcode\":" + lERR_BASE + 1 + ",\"severity\":50}");
	}else{
	%>{"success":"true"}<%}%>