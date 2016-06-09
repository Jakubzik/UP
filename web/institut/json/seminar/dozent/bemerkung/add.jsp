<%--
    JSP-Object "seminar/dozent/bemerkung/add"
    @Revision: Feb 25, 2016 -- hj

    @TODO
    ===========================

    SYNOPSIS (German)
    ===========================
    2016, Feb 25, shj
    Erzeugt. (Stub)
    
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
    long lDozentID=-1;
    try{
        lDozentID=Long.parseLong(request.getParameter("dozent_id").trim());
    }catch(Exception eNotNumeric){
        throw new Exception("{\"error\":\"Es kann keine Bemerkung gespeichert werden. Es wurde keine Id für die Lehrperson übergeben.\"" + 
                    ",\"errorDebug\":\"Der Parameter >dozent_id< (für die DozentID) hat den nicht-numerischen Wert >" + request.getParameter("dozent_id") + "<.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":10}");               
    }
    
    // Wurde eine Bemerkung übergeben?
    String sText = shjCore.normalize(request.getParameter("text"));
    if(sText.length()<=2) throw new Exception("{\"error\":\"Es kann keine Bemerkung gespeichert werden. Es wurde kein Inhalt für eine Bemerkung übergeben.\"" + 
                    ",\"errorDebug\":\"Der Parameter >text< (für den inhalt der Bemerkung) hat den Wert >" + request.getParameter("text") + "<.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":10}");               
    
    // Ist das Wiedervorlagedatum angegeben?
    Date dWV = null;
    try{
        dWV = shjCore.g_GERMAN_DATE_FORMAT.parse(request.getParameter("wiedervorlagedatum"));
    }catch(Exception e){
        throw new Exception("{\"error\":\"Es kann keine Bemerkung gespeichert werden. Es wurde kein Datum für eine Wiedervorlage übergeben.\"" + 
                    ",\"errorDebug\":\"Der Parameter >wiedervorlagedatum< (dt. Format) hat den nicht-interpretierbaren Wert >" + request.getParameter("wiedervorlagedatum") + "<.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":10}");               
    }
    
    // Tag ist optional
    String sTag = shjCore.normalize(request.getParameter("tag"));
    
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
    
    // boolean bSuccess = pstm.execute();
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