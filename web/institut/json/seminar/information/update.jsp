<%--
    JSP-Object "seminar/information/update"
    @Erzeugt: Jun 26, 2014 -- hj

    @TODO
    ===========================
    link, upload

    SYNOPSIS (German)
    ===========================  
    Ändert die Information
    
    Expected SESSION PROPERTIES
    ===========================
    user        muss initialisiert sein und 
                mit dem Autor der Info identisch

    Expected PARAMETER(S):
    ===========================
    id:             [long]                      - Id der Leistung
    inhalt:         [String]


    Returns
    =======
    {"success":"true",
    "information":{
        "inhalt":<Inhalt der Info,                [Text]>,
        "id":<InhaltID (relevant für Kommentarfkt.    [long]>
    }

    Sample Usage
    ============
 
    
--%><%@page import="java.sql.PreparedStatement"%>
<%@page import="de.shj.UP.data.Leistung"%>
<%@page contentType="text/json" pageEncoding="UTF-8" import="java.sql.ResultSet,de.shj.UP.data.Dozent,de.shj.UP.util.PasswordHash" session="true" isThreadSafe="false"  errorPage="../../error.jsp" %>
<jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean id="seminar" scope="session" class="de.shj.UP.logic.SeminarData" />
<%@include file="../../../fragments/checkVersion.jsp" %>
<%@include file="../../../fragments/checkLogin.jsp" %>
<%@include file="../../../fragments/checkAccess1.jsp" %>
<%long lERR_BASE=214000 + 200;    // Information + Update

    // -------------------------------------------------------------
    // Initialisierung
    long lInfoID=-1;

    // -------------------------------------------------------------
    // Schnittstelle
    try{
        lInfoID=Long.parseLong(request.getParameter("id").trim());
    }catch(Exception eNotNumeric){
        throw new Exception("{\"error\":\"Die Änderungen an der Information können nicht gespeichert werden. Es wurde keine Id übergeben.\"" + 
                    ",\"errorDebug\":\"Der Parameter >id< (für die InformationsID) hat den nicht-numerischen Wert " + request.getParameter("id") + ".\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":10}");               
    }
    
    PreparedStatement pstm= user.prepareStatement("update \"tblBdInformation\" " +
            "set \"strInformationInhalt\"=? " +
            " where " +
            "\"lngSdSeminarID\"=? and \"lngInformationID\"=? and \"lngDozentID\"=?");
    
    int ii=1;
    pstm.setString(ii++, request.getParameter("inhalt").trim());
    pstm.setLong(ii++, user.getSdSeminarID());
    pstm.setLong(ii++, lInfoID);
    pstm.setLong(ii++, user.getDozentID());
    
    int iRowsAffected=pstm.executeUpdate();
    
    if(iRowsAffected==0)
        throw new Exception("{\"error\":\"Die Information mit der id >" + lInfoID + "< wurde nicht gefunden bzw. stammt nicht von Ihnen.\"" + 
                    ",\"errorDebug\":\"Update hat keine Zeile in der Datenbank verändert\",\"errorcode\":" + lERR_BASE + 1 + ",\"severity\":10}"); 

    if(iRowsAffected>1)
        throw new Exception("{\"error\":\"Ein schwerwiegendes Problem ist aufgetreten -- bitte informieren Sie den Systemadministrator.\"" + 
                    ",\"errorDebug\":\"Das Update hat mehr als eine Zeile verändert\",\"errorcode\":" + lERR_BASE + 1 + ",\"severity\":10}"); 

%>{"success":"true","information":{"id":"<%=lInfoID %>","inhalt":"<%=request.getParameter("inhalt").trim() %>"}}