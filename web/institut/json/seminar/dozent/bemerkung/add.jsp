<%--
    JSP-Object "seminar/dozent/bemerkung/add"
    @Revision: Feb 25, 2016 -- hj

    @TODO
    ===========================

    SYNOPSIS (German)
    ===========================
    2016, Feb 25, shj
    Erzeugt. (Stub)
    
    2017, Mar 20, shj: umgestellt auf RqInterface
    
    Fügt eine neue Bemerkung hinzu und liefert deren Id in der Datenbank.
    
    Expected SESSION PROPERTIES
    ===========================
    user        muss initialisiert sein, AccessLevel >= 500

    Expected PARAMETER(S):
    ===========================
    "dozent_id"
    "tag": "Verlängerung"       (Optional)
    "text": "Vertrag 50% Forschung WissZeitVG Laufzeit 1.1.2016-31.12.2017.",
    "wiedervorlagedatum": "30.6.2017" (Deutsch)

    Returns
    =======
    {
        "success": "true",
        "bemerkung_id": "8"
    }

    Sample Usage
    ============
    http://localhost:8084/SignUpXP/as/Faculty/json/seminar/dozent/bemerkung/add.jsp?signup_expected_backend_version=1-0-0-2&dozent_id=26&text=Testbemerkung%20hinzugef%C3%BCgt&tag=Test&wiedervorlagedatum=1.3.2016
    
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
<%long lERR_BASE=201100 + 400;    // Dozent.Bemerkung + Add

    // =========================================================================
    // Schnittstelle
    RqInterface oD = new RqInterface();
    oD.setErrBase ( lERR_BASE );
    oD.setActionFailedDescription( "Die Bemerkung zu Lehrenden konnte nicht hinzugefügt werden." );
    oD.setRequest(request);
    
    oD.add(new RqDescription("dozent_id", RqDescription.RQ_LONG, "Id der Lehrperson"));
    oD.add(new RqDescription("text", RqDescription.RQ_STRING, 3, "Inhalt der Bemerkung"));
    oD.add(new RqDescription("studentNachname", RqDescription.RQ_STRING, 2, "Nachname des oder der Studierenden"));
    oD.add(new RqDescription("wiedervorlagedatum", RqDescription.RQ_DATE_DE, "Wiedervorlagedatum"));
    oD.checkRequestAgainstDescriptions();

    // =========================================================================
    // Initialisierung
    long lDozentID=Long.parseLong(request.getParameter("dozent_id").trim());
    String sText = shjCore.normalize(request.getParameter("text"));
    Date dWV = shjCore.g_GERMAN_DATE_FORMAT.parse(request.getParameter("wiedervorlagedatum"));
    String sTag = shjCore.normalize(request.getParameter("tag"));       /* optional */
    
    // =========================================================================
    // F Ü G E  Datensatz hinzu (und gebe neue Id aus)
    PreparedStatement pstm = user.prepareStatement("INSERT INTO \"tblBdDozentBemerkung\"(" +
            "\"lngSdSeminarID\", \"lngDozentID\", \"strDozentBemerkungTag\", " +
            "\"strDozentBemerkungText\", \"dtmDozentBemerkungDatum\", \"dtmDozentBemerkungWv\") " +
            "VALUES (?, ?, ?, " +
            "?, CURRENT_DATE, ?) RETURNING \"lngDozentBemerkungID\";");
    
    int ii=1;
    pstm.setLong(ii++, seminar.getSeminarID());
    pstm.setLong(ii++, lDozentID);
    pstm.setString(ii++, sTag);
    pstm.setString(ii++, sText);
    pstm.setDate(ii++, new java.sql.Date(dWV.getTime()));
    
    ResultSet rBem = pstm.executeQuery();
    long lBemerkungID = -1;
    try{
        rBem.next();
        lBemerkungID = rBem.getLong("lngDozentBemerkungID");
    }catch(Exception eIgnore){}
    
    // Geklappt?
    if(lBemerkungID < 0)
        throw new Exception("{\"error\":\"Es kann keine Bemerkung gespeichert werden. Die Datenbank wollte nicht.\"" + 
                    ",\"errorDebug\":\"Die DB meldet >" + pstm.getConnection().getWarnings() + "<.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":10}");

%>{"success":"true","bemerkung_id":"<%=lBemerkungID%>"}