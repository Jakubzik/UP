<%--
    JSP-Object "seminar/leistung/delete"
    @Erzeugt: Jun 26, 2014 -- hj

    @TODO
    ===========================


    SYNOPSIS (German)
    ===========================  
    Löscht die Stammdaten einer Leistung (Name, engl. Name etc.)
    (_no_ cascade)
    
    Expected SESSION PROPERTIES
    ===========================
    user        muss initialisiert sein & Access Level > 500 haben

    Expected PARAMETER(S):
    ===========================
    id:             [long]                      - Id der Leistung


    Returns
    =======
    {
        "success": "true",
        "leistung": {
            "id": "26",
            "name": "Einführung Sprachwissenschaft",
            "name_en": "Introduction to Linguistics",
            "code":"",
            "cp": "3.5",
        }
    }

    Sample Usage
    ============
--%><%@page import="java.sql.PreparedStatement"%>
<%@page import="de.shj.UP.data.Leistung"%>
<%@page contentType="text/json" pageEncoding="UTF-8" import="java.sql.ResultSet,de.shj.UP.data.Dozent,de.shj.UP.util.PasswordHash" session="true" isThreadSafe="false"  errorPage="../../error.jsp" %>
<jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean id="seminar" scope="session" class="de.shj.UP.HTML.HtmlSeminar" />
<%@include file="../../../fragments/checkVersion.jsp" %>
<%@include file="../../../fragments/checkLogin.jsp" %>
<%@include file="../../../fragments/checkAccess500.jsp" %>
<%long lERR_BASE=213000 + 200;    // Leistung + Update

    // -------------------------------------------------------------
    // Initialisierung
    long lLeistungID=-1;
    Leistung oLeistung=null;

    // -------------------------------------------------------------
    // Schnittstelle
    try{
        lLeistungID=Long.parseLong(request.getParameter("id").trim());
    }catch(Exception eNotNumeric){
        throw new Exception("{\"error\":\"Das Löschen der Leistung ist nicht möglich. Es wurde keine Id übergeben.\"" + 
                    ",\"errorDebug\":\"Der Parameter >id< (für die LeistungsID) hat den nicht-numerischen Wert " + request.getParameter("id") + ".\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":10}");               
    }
   PreparedStatement pstm= user.prepareStatement("delete from \"tblSdLeistung\" " +
            "where \"lngLeistungID\"=? and \"lngSdSeminarID\"=?;");
    int ii=1;
    pstm.setLong(ii++, lLeistungID);
    pstm.setLong(ii++, user.getSdSeminarID());
    
    int iRowsAffected=pstm.executeUpdate();
    if(iRowsAffected==0)
        throw new Exception("{\"error\":\"Die Leistung konnte nicht gelöscht werden, sorry..\"" + 
                    ",\"errorDebug\":\"Delete hat keine Zeile in der Datenbank verändert\",\"errorcode\":" + lERR_BASE + 1 + ",\"severity\":10}"); 

    if(iRowsAffected>1)
        throw new Exception("{\"error\":\"Ein schwerwiegendes Problem ist aufgetreten -- bitte informieren Sie den Systemadministrator.\"" + 
                    ",\"errorDebug\":\"Das Löschen hat mehr als eine Zeile verändert\",\"errorcode\":" + lERR_BASE + 1 + ",\"severity\":10}"); 
%>{"success":"true","leistung":{"id":"<%=lLeistungID %>","name":"<%=request.getParameter("name").trim() %>", "name_en":"<%=request.getParameter("name_en").trim() %>", "cp":"<%=Float.parseFloat(request.getParameter("cp").trim()) %>","code":"<%=request.getParameter("code").trim() %>"}}