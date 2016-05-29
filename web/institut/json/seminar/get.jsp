<%@page contentType="text/json" pageEncoding="UTF-8" import="java.sql.ResultSet,de.shj.UP.logic.AktuellesIterator,java.text.SimpleDateFormat, de.shj.UP.data.Dozent,de.shj.UP.data.shjCore" session="true" isThreadSafe="false"  errorPage="../error.jsp" %>
<jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean id="seminar" scope="session" class="de.shj.UP.HTML.HtmlSeminar" />
<%--   
        ==================== ==================== ==================== ==================== ====================
        SCHNITTSTELLE:
        - user muss initialisiert sein -- sonst Meldung "Sitzung abgelaufen" via 'checkAccess.jsp'
        - Versionsnummer muss stimmen.
        - Access-Level 1 genügt
        ==================== ==================== ==================== ==================== ====================
--%>
<%@include file="../../fragments/checkVersion.jsp" %>
<%@include file="../../fragments/checkLogin.jsp" %>
<%@include file="../../fragments/checkAccess1.jsp" %>

<%long lERR_BASE=200000 + 100;    // Seminar + Get
%>
<%--
    JSP-Object "seminar/get"

    @TODO
    ===========================

    SYNOPSIS (German)
    ===========================
    2013, Mar 5, shj
    Erzeugt. 
    
    Üblicher Lifecycle: GET

    Aggregiert Informationen zum Seminar: 
    - Faecher (Studiengaenge),
        -- Module
    - Noten,
    - Lehrende
    
    Expected SESSION PROPERTIES
    ===========================
    user        muss initialisiert sein (AccessLevel egal),

    Expected PARAMETER(S):
    ===========================
    
    Returns
    =======
    Array of objects with remarks:
    "seminar":{
    }

    Sample Usage
    ============

    
--%>