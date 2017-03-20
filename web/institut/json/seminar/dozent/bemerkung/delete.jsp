<%--
    JSP-Object "seminar/dozent/bemerkung/delete"
    @Revision: Feb 25, 2016 -- hj

    @TODO
    ===========================

    SYNOPSIS (German)
    ===========================
    Löscht eine Bemerkung.
    
    2016, Feb 25, shj    Erzeugt. (Stub)
    2017, Mar 20, shj   Schnittstelle modernisiert
    
    
    Expected SESSION PROPERTIES
    ===========================
    user        muss initialisiert sein, AccessLevel >= 500

    Expected PARAMETER(S):
    ===========================
    "dozent_id"
    "bemerkung_id"

    Returns
    =======
    {
        "success": "true"
    }
    
    oder Fehler.

    (True wird auch geliefert, wenn es die zu löschende Bemerkung nicht gab).

    Sample Usage
    ============
    http://localhost:8084/SignUpXP/as/Faculty/json/seminar/dozent/bemerkung/delete.jsp?signup_expected_backend_version=1-0-0-2&dozent_id=26&text=Testbemerkung%20hinzugef%C3%BCgt%20und%20korrigiert&tag=Test&wiedervorlagedatum=1.5.2016&bemerkung_id=9
    
--%>
<%@page import="com.shj.signUp.util.RqDescription"%>
<%@page import="com.shj.signUp.util.RqInterface"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page contentType="text/json" pageEncoding="UTF-8" import="de.shj.UP.data.Dozent,de.shj.UP.data.shjCore" session="true" isThreadSafe="false"  errorPage="../../../error.jsp" %>
<jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean id="seminar" scope="session" class="de.shj.UP.logic.SeminarData" />
<%@include file="../../../../fragments/checkVersion.jsp" %>
<%@include file="../../../../fragments/checkLogin.jsp" %>
<%@include file="../../../../fragments/checkAccess500.jsp" %>
<%long lERR_BASE=201100 + 300;    // Dozent.Bemerkung + Delete

    // =========================================================================
    // Schnittstelle
    RqInterface oD = new RqInterface();
    oD.setErrBase ( lERR_BASE );
    oD.setActionFailedDescription( "Die Bemerkung zu Lehrenden konnte nicht gelöscht werden." );
    oD.setRequest(request);
      
    oD.add(new RqDescription("dozent_id", RqDescription.RQ_LONG, "Id der Lehrperson"));
    oD.add(new RqDescription("bemerkung_id", RqDescription.RQ_LONG, "Id der Bemerkung"));
    oD.checkRequestAgainstDescriptions();
    
    // =========================================================================
    // L Ö S C H E N  aus der Datenbank
    boolean bSuccess = user.sqlExe("delete from \"tblBdDozentBemerkung\" " +
            "where \"lngSdSeminarID\"=" + seminar.getSeminarID() + 
            "  and \"lngDozentID\"=" + Long.parseLong(request.getParameter("dozent_id").trim()) + 
            " and \"lngDozentBemerkungID\"=" + Long.parseLong(request.getParameter("bemerkung_id")));
    
    // Geklappt?
    if(!bSuccess)
        throw new Exception("{\"error\":\"Bemerkung konnte nicht gelöscht werden. Die Datenbank wollte nicht.\"" + 
                    ",\"errorDebug\":\"DELETE hat Fehler ausgelöst -- Logfiles.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":10}");
    
%>{"success":"true","bemerkung_id":"<%=Long.parseLong(request.getParameter("bemerkung_id"))%>"}
