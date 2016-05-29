<%--
    JSP-Object "seminar/information/add"
    @Erzeugt: Jul 6, 2014 -- hj

    @TODO
    ===========================


    SYNOPSIS (German)
    ===========================  
    Postet eine Information unter der Kennung 
    des/r Angemeldeten und dem aktuellen Datum
    
    Expected SESSION PROPERTIES
    ===========================
    user        muss initialisiert sein & Access Level >=1 haben

    Expected PARAMETER(S):
    ===========================
    inhalt:           [String]


    Returns
    =======
    {
        "success": "true",
        "information": {
            "id": "26",
            "autor":"Heiko Jakubzik",
            "autor_id":"26",
            "kommentare":[],                                // immer leer.
            "inhalt": "Einführung Sprachwissenschaft",
            "timestamp": "2014-7-3 19:45.123123"
        }
    }

    Sample Usage
    ============
 
    
--%><%@page import="de.shj.UP.data.shjCore"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="de.shj.UP.data.Leistung"%>
<%@page contentType="text/json" pageEncoding="UTF-8" import="java.sql.ResultSet,de.shj.UP.data.Dozent,de.shj.UP.util.PasswordHash" session="true" isThreadSafe="false"  errorPage="../../error.jsp" %>
<jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean id="seminar" scope="session" class="de.shj.UP.HTML.HtmlSeminar" />
<%@include file="../../../fragments/checkVersion.jsp" %>
<%@include file="../../../fragments/checkLogin.jsp" %>
<%@include file="../../../fragments/checkAccess1.jsp" %>
<%long lERR_BASE=214000 + 400;    // Information + Add

    // -------------------------------------------------------------
    // Initialisierung
    long lInfoID=-1;

    // -------------------------------------------------------------
    // Schnittstelle
    try{
        lInfoID=user.getNextID("lngInformationID", "tblBdInformation", "\"lngSdSeminarID\"=" + user.getSdSeminarID());
    }catch(Exception eNotNumeric){
        throw new Exception("{\"error\":\"Die Information kann nicht hinzugefügt werden. Es gibt keine neue ID.\"" + 
                    ",\"errorDebug\":\"Keine weitere Info.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":10}");               
    }
    
    if(user.normalize(request.getParameter("sonderfkt")).equals("Kommentar")){
        %><jsp:forward page="kommentiere.jsp" /><%
    }

    PreparedStatement pstm= user.prepareStatement("insert into \"tblBdInformation\" (" +
            "\"dtmInformationDatum\", \"lngSdSeminarID\", \"lngDozentID\", \"lngInformationID\", \"strInformationInhalt\" ) values (" +
            "now(),?,?,?,?);");
    int ii=1;
    pstm.setLong(ii++, user.getSdSeminarID());
    pstm.setLong(ii++, user.getDozentID());
    pstm.setLong(ii++, lInfoID);
    pstm.setString(ii++, request.getParameter("inhalt").trim());
    
    
    int iRowsAffected=pstm.executeUpdate();

    if(iRowsAffected==0)
        throw new Exception("{\"error\":\"Die Information konnte nicht hinzugefügt werden, sorry..\"" + 
                    ",\"errorDebug\":\"Update hat keine Zeile in der Datenbank verändert\",\"errorcode\":" + lERR_BASE + 1 + ",\"severity\":10}"); 

    if(iRowsAffected>1)
        throw new Exception("{\"error\":\"Ein schwerwiegendes Problem ist aufgetreten -- bitte informieren Sie den Systemadministrator.\"" + 
                    ",\"errorDebug\":\"Das Hinzufügen hat mehr als eine Zeile verändert\",\"errorcode\":" + lERR_BASE + 1 + ",\"severity\":10}"); 


        %>{"success":"true","information":{"id":"<%=lInfoID %>","autor":"<%=user.getDozentVorname() + " " + user.getDozentNachname() %>","autor_id":"<%=user.getDozentID() %>","inhalt":"<%=shjCore.string2JSON(request.getParameter("inhalt").trim()) %>", "ms_timestamp":"<%=new java.util.Date().getTime()%>", "timestamp":"<%=new java.sql.Timestamp(new java.util.Date().getTime()) %>","kommentare":[]}}