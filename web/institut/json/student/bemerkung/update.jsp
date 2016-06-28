<%--
    JSP-Object "student/bemerkung/update"

    SYNOPSIS (German)
    ===========================
    2012, Nov 22, shj
    2013, Dec 26, shj -- überarbeitet (SQL-Injection)
    Erzeugt. 
    
    Üblicher Lifecycle: UPDATE

    Ändert den Text der Bemerkung (lässt aber die Information zu Autor sowie 
    das ursprüngliche Datum bestehen).

    Man kann nur die eigenen Bemerkungen ändern, oder man braucht eine 
    Berechtigung von 500 oder mehr.
    
    Expected SESSION PROPERTIES
    ===========================
    user        muss initialisiert sein; bei Berechtigung < 500 dürfen nur eigene Bemerkungen geändert werden.
    student     muss identische Matrikelnummer haben, die auch
                als Parameter übergeben wird.

    Expected PARAMETER(S):
    ===========================
    matrikelnummer  [long]
    bemerkung_id    [long]
    bemerkung       [text]
    signup-expected-backend-version [text]
    
    Returns
    =======
    Object:
    {"success":"true"}
    
--%><%@ page contentType="text/json" pageEncoding="UTF-8" import="java.util.Enumeration,java.text.SimpleDateFormat, de.shj.UP.data.Dozent,de.shj.UP.data.shjCore" session="true" isThreadSafe="false" errorPage="../error.jsp"%>
<%@page import="de.shj.UP.data.StudentBemerkung"%><jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean id="student" scope="session" class="de.shj.UP.logic.StudentData" />
<%@include file="../../../fragments/checkVersion.jsp" %>
<%@include file="../../../fragments/checkLogin.jsp" %>
<%@include file="../../../fragments/checkAccess1.jsp" %>
<%@include file="../../../fragments/checkInitStudent.jsp" %>
<%      long lERR_BASE=100000 + 200;    // Bemerkung + Update
        StudentBemerkung sb=null;
        try{
            sb=new StudentBemerkung(Long.parseLong(request.getParameter("bemerkung_id")));        
        }catch(Exception e){
            throw new Exception("{\"error\":\"Die Bemerkung konnte nicht geändert werden.\",\"errorDebug\":\"Der Parameter >bemerkung_id< wurde nicht übergeben oder ist nicht numerisch.\",\"errorcode\":" + lERR_BASE + 2 + ",\"severity\":50}");
        }
        
        // Bei 'niederen' Berechtigungen dürfen nur 
        // die eigenen Bemerkungen geändert werden:
        if(user.getDozentAccessLevel()<500){
            if(sb.getDozentID() != user.getDozentID() || sb.getSdSeminarID() != user.getSdSeminarID()) 
                throw new Exception("{\"error\":\"Die Bemerkung konnte nicht geändert werden.\",\"errorDebug\":\"Die zu ändernde Bemerkung wurde von einem anderen Benutzer erstellt -- Ihnen fehlt die Berechtigung\",\"errorcode\":" + lERR_BASE + 2 + ",\"severity\":50}");
        }
        
        sb.setStudentBemerkungText(request.getParameter("bemerkung"));
        if(!sb.update()) throw new Exception("{\"error\":\"Die Bemerkung konnte nicht geändert werden.\",\"errorDebug\":\"Die Datenbank hat sich verweigert -- Näheres vielleicht in deren Logs?\",\"errorcode\":" + lERR_BASE + 1 + ",\"severity\":50}");
	%>{"success":"true"} 