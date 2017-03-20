<%--
    JSP-Object "seminar/dozent/bemerkung/update"
    @Revision: Feb 25, 2016 -- hj

    @TODO
    ===========================

    SYNOPSIS (German)
    ===========================
    2016, Feb 25, shj
    Erzeugt. (Stub)
    
    Fügt eine neue Bemerkung hinzu
    
    Expected SESSION PROPERTIES
    ===========================
    user        muss initialisiert sein, AccessLevel >= 500

    Expected PARAMETER(S):
    ===========================
    "dozent_id"
    "bemerkung_id"
    "tag": "Verlängerung"       (Optional)
    "text": "Vertrag 50% Forschung WissZeitVG Laufzeit 1.1.2016-31.12.2017.",   (Optional)
    "wiedervorlagedatum": "30.6.2017" (Deutsch)

    Returns
    =======
    {
        "success": "true"
    }

    Sample Usage
    ============
    http://localhost:8084/SignUpXP/as/Faculty/json/seminar/dozent/bemerkung/update.jsp?signup_expected_backend_version=1-0-0-2&dozent_id=26&text=Testbemerkung%20hinzugef%C3%BCgt%20und%20korrigiert&tag=Test&wiedervorlagedatum=1.5.2016&bemerkung_id=8
    
--%>
<%@page import="de.shj.UP.util.RqDescription"%>
<%@page import="de.shj.UP.util.RqInterface"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.util.Date"%>
<%@page contentType="text/json" pageEncoding="UTF-8" import="java.sql.ResultSet,de.shj.UP.data.Dozent,de.shj.UP.data.shjCore" session="true" isThreadSafe="false"  errorPage="../../../error.jsp" %>
<jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean id="seminar" scope="session" class="de.shj.UP.logic.SeminarData" />
<%@include file="../../../../fragments/checkVersion.jsp" %>
<%@include file="../../../../fragments/checkLogin.jsp" %>
<%@include file="../../../../fragments/checkAccess500.jsp" %>
<%long lERR_BASE=201100 + 200;    // Dozent.Bemerkung + Update

    // =========================================================================
    // Schnittstelle
    // =========================================================================
    RqInterface oD = new RqInterface();
    oD.setErrBase ( lERR_BASE );
    oD.setActionFailedDescription( "Die Bemerkung zur Lehrperson konnte nicht geändert werden." );
    oD.setRequest(request);
    
    oD.add(new RqDescription("dozent_id", RqDescription.RQ_LONG, "Id der Lehrperson"));
    oD.add(new RqDescription("bemerkung_id", RqDescription.RQ_LONG, "Id der Bemerkung"));
    oD.add(new RqDescription("text", RqDescription.RQ_LONG, 2, "Inhalt der Bemerkung"));
    oD.add(new RqDescription("text", RqDescription.RQ_DATE_DE, "Datum der Wiedervorlage", true));
    oD.checkRequestAgainstDescriptions();
    
    // =========================================================================
    // Initialisierung
    // =========================================================================
    Date dWV = shjCore.g_GERMAN_DATE_FORMAT.parse(request.getParameter("wiedervorlagedatum"));
    
    // =========================================================================
    // A K T U A L I S I E R E  den Datensatz in der Datenbank
    PreparedStatement pstm = user.prepareStatement("UPDATE \"tblBdDozentBemerkung\" " +
        "SET \"strDozentBemerkungTag\"=?, \"strDozentBemerkungText\"=?, \"dtmDozentBemerkungDatum\"=CURRENT_DATE " +
       ((dWV == null) ? "" : ",\"dtmDozentBemerkungWv\"=? ") +
        "WHERE \"lngSdSeminarID\"=? and \"lngDozentBemerkungID\"=? and \"lngDozentID\"=?;");
    
    int ii=1;
    pstm.setString(ii++, shjCore.normalize(request.getParameter("tag")));
    pstm.setString(ii++, request.getParameter("text").trim());
    if(dWV != null) pstm.setDate(ii++, new java.sql.Date(dWV.getTime()));
    pstm.setLong(ii++, seminar.getSeminarID());
    pstm.setLong(ii++, Long.parseLong(request.getParameter("bemerkung_id").trim()));
    pstm.setLong(ii++, Long.parseLong(request.getParameter("dozent_id").trim()));
    
    pstm.execute();
    boolean bSuccess = (pstm.getUpdateCount() == 1);
    
    if(!bSuccess)
        throw new Exception("{\"error\":\"Bemerkung kann nicht geändert werden. Die Datenbank streikt.\"" + 
                    ",\"errorDebug\":\"Siehe Datenbank Logs.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":10}");
    
%>{"success":"true","bemerkung_id":"<%=request.getParameter("bemerkung_id")%>"}
