<%--
    JSP-Object "seminar/kurs/add"

    @Revsion
    ===========================
    Nov 4, 2014 -- hj

    @TODO
    ===========================
    Prüfen: bei AccessLevel < 500 
    muss dozent_id===user_id

    Löschen KursXKurstyp in Transaktion?

    SYNOPSIS (German)
    ===========================
    2014, Nov 6, shj    Erzeugt. 
    
    Üblicher Lifecycle: DELETE

    Expected SESSION PROPERTIES
    ===========================
    seminar        muss initialisiert sein (AccessLevel egal),

    Expected PARAMETER(S):
    ===========================
    kurs_id OR id

    Returns
    =======
    {"success":"true"}

--%>
<%@page contentType="text/json" pageEncoding="UTF-8" import="java.sql.PreparedStatement,java.sql.ResultSet,de.shj.UP.data.Dozent,de.shj.UP.data.shjCore" session="true" isThreadSafe="false"  errorPage="../../error.jsp" %>
<jsp:useBean id="seminar" scope="session" class="de.shj.UP.logic.SeminarData" />
<jsp:useBean id="student" scope="session" class="de.shj.UP.beans.config.student.StudentBean" />
<jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" />
<jsp:useBean id="sd" scope="session" class="de.shj.UP.util.SessionData" />
<%@include file="../../../fragments/checkVersion.jsp" %>
<%@include file="../../../fragments/checkAccess1.jsp" %>
<%long lERR_BASE=206000 + 300;    // Kurs + Delete
    
    String sIdentifier="kurs_id";
    if(request.getParameter("kurs_id")==null) sIdentifier="id";
    long lKursID=-1;
    try{
        lKursID=Long.parseLong(request.getParameter(sIdentifier));
    }catch(Exception eParamWrong){
        throw new Exception("{\"error\":\"Der Kurs konnte nicht gelöscht werden, sorry -- es fehlt die Angabe einer Id.\"" + 
                    ",\"errorDebug\":\"Der Parameter >kurs_id< hatte den nicht-numerischen Wert >" + request.getParameter("kurs_id") + "<\",\"errorcode\":" + lERR_BASE + 1 + ",\"severity\":100}"); 
    }
    
    PreparedStatement pstm3= user.prepareStatement("DELETE FROM \"tblBdKursTermin\" " + 
            "where \"lngSdSeminarID\"=? and \"lngKursID\"=?;");

    int ii=1;
    pstm3.setLong(ii++, user.getSdSeminarID());
    pstm3.setLong(ii++, lKursID);
    
    pstm3.executeUpdate();

    PreparedStatement pstm= user.prepareStatement("DELETE FROM \"tblBdKursXKurstyp\" " + 
            "where \"lngSeminarID\"=? and \"lngKursID\"=?;");

    ii=1;
    pstm.setLong(ii++, user.getSdSeminarID());
    pstm.setLong(ii++, lKursID);
    
    int iRowsAffected=pstm.executeUpdate();

    if(iRowsAffected==0)
        throw new Exception("{\"error\":\"Der Kurs konnte nicht gelöscht werden, sorry..\"" + 
                    ",\"errorDebug\":\"Delete hat keine Zeile in der Datenbank verändert\",\"errorcode\":" + lERR_BASE + 1 + ",\"severity\":10}"); 
    
    PreparedStatement pstm2= user.prepareStatement("DELETE FROM \"tblBdKurs\" " + 
            "where \"lngSdSeminarID\"=? and \"lngKursID\"=?;");

    ii=1;
    pstm2.setLong(ii++, user.getSdSeminarID());
    pstm2.setLong(ii++, lKursID);
    
    iRowsAffected=pstm2.executeUpdate();

    if(iRowsAffected==0)
        throw new Exception("{\"error\":\"Der Kurs konnte nicht gelöscht werden, sorry..\"" + 
                    ",\"errorDebug\":\"Delete hat keine Zeile in der Datenbank verändert\",\"errorcode\":" + lERR_BASE + 1 + ",\"severity\":10}");
%>
{"success":"true"}