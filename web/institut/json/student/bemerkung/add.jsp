<%--
    JSP-Object "student/bemerkung/add"

    SYNOPSIS (German)
    ===========================
    2012, Nov 16, shj           erzeugt. 
    2013, Dec 26, shj           leicht überarbeitet (SQL Injection)
    
    Üblicher Lifecycle: ADD

    Speichert die Bemerkung (aus Parameter 'bemerkung') 
    und liefert die bemerkung_id zurück. Es ist 
    AccessLevel500 erforderlich.
    
    Expected SESSION PROPERTIES
    ===========================
    user        muss initialisiert sein (AccessLevel 500 oder mehr),
    student     muss identische Matrikelnummer haben, die auch
                als Parameter übergeben wird.

    Expected PARAMETER(S):
    ===========================
    matrikelnummer  [long]
    bemerkung       [text]
    signup-expected-backend-version [text], vgl. fragments/checkVersion.jsp
    
    Returns
    =======
    Object:
    {"success":"true", "bemerkung_id":'id', "bemerkung":'bemerkung'}

    Fehler/Errors/THROWS
    ====================
    - if user is not logged in, a session-timeout is thrown;
    - if "bemerkung" is not handed over, a message is thrown
    - if matrikelummer is missing or differs from current session, error is thrown.

    Sample Usage
    ============
    <jsp:include page="student/bemerkung/add.jsp?bemerkung=Hallo&matrikelnummer=1488258&signup-expected-backend-version=1-01-2.7726-1" />
    
--%> <%@ page contentType="text/json" pageEncoding="UTF-8" import="de.shj.UP.data.StudentBemerkung,de.shj.UP.data.shjCore" session="true" isThreadSafe="false" errorPage="../../error.jsp"%><jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean id="student" scope="session" class="de.shj.UP.beans.config.student.StudentBean" /><jsp:useBean id="seminar" scope="session" class="de.shj.UP.logic.SeminarData" />
<% long lERR_BASE=100000 + 400;    // Bemerkung + Add
if(request.getParameter("bemerkung")==null || request.getParameter("bemerkung").trim().equals("")) throw new Exception("{\"error\":\"Die Bemerkung ist leer und konnte nicht gespeichert werden.\",\"errorDebug\":\"Der erforderliche Parameter >bemerkung< ist null oder leer.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":20}");%>
<%@include file="../../../fragments/checkVersion.jsp" %>
<%@include file="../../../fragments/checkLogin.jsp" %>
<%@include file="../../../fragments/checkAccess500.jsp" %>
<%@include file="../../../fragments/checkInitStudent.jsp" %>
<%  StudentBemerkung d = new StudentBemerkung();
    d.setSdSeminarID(user.getSdSeminarID());
    d.setDozentID(user.getDozentID());
    d.setMatrikelnummer(student.getMatrikelnummer());
    d.setStudentBemerkungDatum(seminar.getToday().getDate());
    d.setStudentBemerkungText(request.getParameter("bemerkung").trim());
    d.setStudentBemerkungTag("Config-New");
    d.setStudentBemerkungID(user.getNextID("lngStudentBemerkungID", "tblBdStudentBemerkung", ""));
    if(!d.add())  throw new Exception("{\"error\":\"Die Bemerkung konnte nicht gespeichert werden -- die Datenbank hat sich verweigert.\",\"errorDebug\":\"Mehr Informationen vielleicht in den Datenbank logs.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":20}");%>
    {"success":"true","bemerkung":{"datum":"<%=shjCore.g_GERMAN_DATE_FORMAT.format(d.getStudentBemerkungDatum())%>","autor":"<%=user.getDozentNachname() %>","bemerkung_id":"<%=d.getStudentBemerkungID()%>", "bemerkung":"<%=shjCore.string2JSON(d.getStudentBemerkungText()) %>"}}