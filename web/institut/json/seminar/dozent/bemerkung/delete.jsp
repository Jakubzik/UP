<%--
    JSP-Object "seminar/dozent/bemerkung/delete"
    @Revision: Feb 25, 2016 -- hj

    @TODO
    ===========================

    SYNOPSIS (German)
    ===========================
    2016, Feb 25, shj
    Erzeugt. (Stub)
    
    Löscht eine Bemerkung.
    
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
<%@page import="java.sql.PreparedStatement"%>
<%@page contentType="text/json" pageEncoding="UTF-8" import="de.shj.UP.data.Dozent,de.shj.UP.data.shjCore" session="true" isThreadSafe="false"  errorPage="../../../error.jsp" %>
<jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean id="seminar" scope="session" class="de.shj.UP.HTML.HtmlSeminar" />
<%@include file="../../../../fragments/checkVersion.jsp" %>
<%@include file="../../../../fragments/checkLogin.jsp" %>
<%@include file="../../../../fragments/checkAccess500.jsp" %>
<%long lERR_BASE=201100 + 300;    // Dozent.Bemerkung + Delete

    // =========================================================================
    // Schnittstelle
    long lDozentID=-1;
    try{
        lDozentID=Long.parseLong(request.getParameter("dozent_id").trim());
    }catch(Exception eNotNumeric){
        throw new Exception("{\"error\":\"Bemerkung kann nicht gelöscht werden. Es wurde keine Id für die Lehrperson übergeben.\"" + 
                    ",\"errorDebug\":\"Der Parameter >id< (für die DozentID) hat den nicht-numerischen Wert >" + request.getParameter("dozent_id") + "<.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":10}");               
    }
      
    // Ist die Bemerkungs-ID angegeben?
    long lBemerkungID = -1;
    try{
        lBemerkungID = Long.parseLong(request.getParameter("bemerkung_id"));
    }catch(Exception e){
        throw new Exception("{\"error\":\"Die Bemerkung kann nicht gelöscht werden. Es wurde keine Id der Bemerkung übergeben.\"" + 
                    ",\"errorDebug\":\"Der Parameter >bemerkung_id< (dt. Format) hat den nicht-interpretierbaren Wert >" + request.getParameter("bemerkung_id") + "<.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":10}");               
    }
    
    // =========================================================================
    // L Ö S C H E N  aus der Datenbank
    boolean bSuccess = user.sqlExe("delete from \"tblBdDozentBemerkung\" " +
            "where \"lngSdSeminarID\"=" + seminar.getSeminarID() + 
            "  and \"lngDozentID\"=" + lDozentID + " and \"lngDozentBemerkungID\"=" + lBemerkungID);
    
    // Geklappt?
    if(!bSuccess)
        throw new Exception("{\"error\":\"Bemerkung konnte nicht gelöscht werden. Die Datenbank wollte nicht.\"" + 
                    ",\"errorDebug\":\"DELETE hat Fehler ausgelöst -- Logfiles.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":10}");
    
%>{"success":"true","bemerkung_id":"<%=lBemerkungID%>"}