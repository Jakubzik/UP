<%--
    JSP-Object "student/bemerkung/delete"
    
    @TODO
    ===========================
    - variabler machen, wer löschen darf, z.B. erstellender 
      Benutzer noch am Tag der Erstellung oder so.

    SYNOPSIS (German)
    ===========================
    2012, Nov 20, shj    erzeugt. 
    2013, Dec 26, shj    überarbeitet

    Üblicher Lifecycle: DELETE

    Löscht die Bemerkung "bemerkung_id". Dazu ist eine 
    Berechtigung von 500 notwendig; sonst kann man auch 
    eigene Bemerkungen nicht löschen.
    
    Expected SESSION PROPERTIES
    ===========================
    user        muss initialisiert sein (AccessLevel 500 oder höher),
    student     muss identische Matrikelnummer haben, die auch
                als Parameter übergeben wird.

    Expected PARAMETER(S):
    ===========================
    matrikelnummer  [long]
    bemerkung_id      [text]
    signup-expected-backend-version [text], vgl. fragments/checkVersion.jsp
    
    Returns
    =======
    Object:
    {"success":"true"}

    Fehler/Errors/THROWS
    ====================
    - if user is not logged in, a session-timeout is thrown;
    - if "bemerkung" is not handed over, a message is thrown
    - if matrikelummer is missing or differs from current session, error is thrown.

    Sample Usage
    ============
    <jsp:include page="student/bemerkung/delete.jsp?bemerkung_id=7&matrikelnummer=1488258&signup-expected-backend-version=1-01-2.7726-1" />
    
--%>   
<%@ page contentType="text/json" pageEncoding="UTF-8" import="java.util.Enumeration,java.text.SimpleDateFormat, de.shj.UP.data.Dozent,de.shj.UP.data.shjCore" session="true" isThreadSafe="false" errorPage="../../error.jsp"%>
<%@page import="de.shj.UP.data.StudentBemerkung"%><jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" />
<jsp:useBean id="student" scope="session" class="de.shj.UP.logic.StudentData" />
<%@include file="../../../fragments/checkVersion.jsp" %>
<%@include file="../../../fragments/checkLogin.jsp" %>
<%@include file="../../../fragments/checkAccess500.jsp" %>
<%@include file="../../../fragments/checkInitStudent.jsp" %>
<%
long lERR_BASE=100000 + 300;    // Bemerkung + Delete
try{
    Long.parseLong(request.getParameter("bemerkung_id"));
}catch(Exception eNoNumericID){
    throw new Exception("{\"error\":\"Die Bemerkung konnte nicht gelöscht werden.\",\"errorDebug\":\"Der erforderliche Parameter >bemerkung_id< wurde nicht übergeben oder war nicht numerisch.\",\"errorcode\"" + lERR_BASE + 3 + ",\"severity\":100}");
}
if(!user.sqlExe("delete from \"tblBdStudentBemerkung\" where " +
    "\"lngSdSeminarID\"=" + user.getSdSeminarID() + " and " +
    "\"strMatrikelnummer\"='" + Long.parseLong(student.getMatrikelnummer()) + "' and " +  
    "\"lngStudentBemerkungID\"=" + Long.parseLong(request.getParameter("bemerkung_id")))){
    throw new Exception("{\"error\":\"Die Bemerkung konnte nicht gelöscht werden.\",\"errorDebug\":\"Die Datenbank hat sich verweigert -- Näheres vielleicht in deren Logs?\",\"errorcode\":" + lERR_BASE + 1 + ",\"severity\":50}");
}else{
	%>{"success":"true"}<%}%>