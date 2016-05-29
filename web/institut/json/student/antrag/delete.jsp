<%--
    JSP-Object "student/antrag/delete"

    SYNOPSIS (German)
    ===========================
    2012, Nov 20, shj   erzeugt. 
    2013, Dex 26, shj   überarbeitet
    
    DELETE: es gibt keinen Anlass, einen Antrag 
            zu löschen, d.h. der Aufruf löst 
            immer einen Fehler aus.
  
    Expected SESSION PROPERTIES
    ===========================
    user        muss initialisiert sein (AccessLevel 500 oder höher),

    Expected PARAMETER(S):
    ===========================
    
    Returns
    =======
    FEHLERMELDUNG

    
--%><%@ page contentType="text/json" pageEncoding="UTF-8" import="java.util.Enumeration,java.text.SimpleDateFormat, de.shj.UP.data.Dozent,de.shj.UP.data.shjCore" session="true" isThreadSafe="false" errorPage="../../error.jsp"%>
<%@page import="de.shj.UP.data.StudentBemerkung"%><jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean id="seminar" scope="session" class="de.shj.UP.HTML.HtmlSeminar" />
<jsp:useBean id="student" scope="session" class="de.shj.UP.beans.config.student.StudentBean" />
<jsp:useBean id="credits" scope="page" class="de.shj.UP.beans.config.student.StudentLeistungBean" />
<%@include file="../../../fragments/checkVersion.jsp" %>
<%@include file="../../../fragments/checkLogin.jsp" %>
<%@include file="../../../fragments/checkAccess500.jsp" %>
<%long lERR_BASE=106000 + 300;throw new Exception("{\"error\":\"Anträge können grundsätzlich nicht gelöscht werden.\",\"errorDebug\":\"Es gibt keine Möglichkeit, Anträge zu löschen.\",\"errorcode\":" + lERR_BASE + 7 + ",\"severity\":50}");%>