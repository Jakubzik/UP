<%--
    JSP-Object "seminar/information/kommentiere"
    @Erzeugt: Jul 6, 2014 -- hj

    @TODO
    ===========================


    SYNOPSIS (German)
    ===========================  
    Postet einen Kommentar unter der Kennung 
    des/r Angemeldeten und dem aktuellen Datum
    
    Expected SESSION PROPERTIES
    ===========================
    user        muss initialisiert sein & Access Level > 0 haben

    Expected PARAMETER(S):
    ===========================
    kommentar           [String]
    id:               [long]    information_id
    sonderfkt:        [String]  Kommentar

    Returns
    =======
    {
        "success": "true",
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
<%long lERR_BASE=214000 + 400;    // Information + Add

    // -------------------------------------------------------------
    // Initialisierung
    long lInfoID=-1;

    // -------------------------------------------------------------
    // Schnittstelle
    try{
        lInfoID=Long.parseLong(request.getParameter("id"));
    }catch(Exception eNotNumeric){
        throw new Exception("{\"error\":\"Der Kommentar kann nicht gespeichert werden, sorry -- die ID wurde nicht übergeben..\"" + 
                    ",\"errorDebug\":\"Keine weitere Info.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":10}");               
    }
    
    if(!user.normalize(request.getParameter("sonderfkt")).equals("Kommentar")){
        throw new Exception("{\"error\":\"Der Kommentar kann nicht gespeichert werden, sorry.\"" + 
                    ",\"errorDebug\":\"Keine weitere Info.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":10}");               

    }

    PreparedStatement pstm= user.prepareStatement("INSERT INTO \"tblBdInformationKommentar\"(" +
            "\"lngSdSeminarID\", \"lngDozentID\", \"lngInformationID\", \"lngInformationKommentarID\", " +
            "\"strInformationKommentarInhalt\", \"dtmInformationKommentarDatum\")" +
    "VALUES (?, ?, ?, (select coalesce(max(\"lngInformationKommentarID\")+1,0) from \"tblBdInformationKommentar\" where \"lngSdSeminarID\"=" + user.getSdSeminarID() + " and \"lngInformationID\"=" + lInfoID + " ), " +
            "?, now());");
    int ii=1;
    pstm.setLong(ii++, user.getSdSeminarID());
    pstm.setLong(ii++, user.getDozentID());
    pstm.setLong(ii++, lInfoID);
    pstm.setString(ii++, request.getParameter("kommentar").trim());
    
    int iRowsAffected=pstm.executeUpdate();

    if(iRowsAffected==0)
        throw new Exception("{\"error\":\"Der Kommentar kann nicht hinzugefügt werden, sorry..\"" + 
                    ",\"errorDebug\":\"Update hat keine Zeile in der Datenbank verändert -- gibt es die InfoID >" + lInfoID + "< ?\",\"errorcode\":" + lERR_BASE + 1 + ",\"severity\":10}"); 

    if(iRowsAffected>1)
        throw new Exception("{\"error\":\"Ein schwerwiegendes Problem ist aufgetreten -- bitte informieren Sie den Systemadministrator.\"" + 
                    ",\"errorDebug\":\"Das Hinzufügen hat mehr als eine Zeile verändert\",\"errorcode\":" + lERR_BASE + 1 + ",\"severity\":10}"); 


%>{"success":"true", "ms_timestamp":"<%=new java.util.Date().getTime() %>"}