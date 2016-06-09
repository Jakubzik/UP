<%--
    JSP-Object "seminar/information/delete"
    @Erzeugt: Jul 6, 2014 -- hj

    @TODO
    ===========================
    Löschen auch für Fremde mit 
    Access Level > 500 erlauben?

    SYNOPSIS (German)
    ===========================  
    Löscht die Information.

    Mit dem Parameter "force":"all" wird 
    erzwungen, dass auch solche Infos gelöscht 
    werden, zu denen es Kommentare gibt -- 
    diese werden dann zuerst gelöscht...

    Ansonsten fkt das Löschen nur, wenn noch keine Kommentare 
    vorliegen.

    Nur der Autor selbst 
    kann löschen.
    
    Expected SESSION PROPERTIES
    ===========================
    user        muss initialisiert sein 

    Expected PARAMETER(S):
    ===========================
    id:             [long]                      - Id der Leistung
    ["force"        ["all"                      - lösche auch Kommentare]

    Returns
    =======
    {
        "success": "true",
        "information": {
            "id": "26",
        }
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
<%long lERR_BASE=214000 + 300;    // Information + Delete

    // -------------------------------------------------------------
    // Initialisierung
    long lInfoID=-1;

    // -------------------------------------------------------------
    // Schnittstelle
    try{
        lInfoID=Long.parseLong(request.getParameter("id").trim());
    }catch(Exception eNotNumeric){
        throw new Exception("{\"error\":\"Das Löschen der Information ist nicht möglich. Es wurde keine Id übergeben.\"" + 
                    ",\"errorDebug\":\"Der Parameter >id< (für die InformationsID) hat den nicht-numerischen Wert " + request.getParameter("id") + ".\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":10}");               
    }
    
    if(user.normalize(request.getParameter("force")).equals("all")){
        PreparedStatement pstmK= user.prepareStatement("delete from \"tblBdInformationKommentar\" " +
                "where \"lngInformationID\"=? and \"lngSdSeminarID\"=?;");
        int ii=1;
        pstmK.setLong(ii++, lInfoID);
        pstmK.setLong(ii++, user.getSdSeminarID());
        pstmK.executeUpdate();
    }
    
    PreparedStatement pstm= user.prepareStatement("delete from \"tblBdInformation\" " +
            "where \"lngInformationID\"=? and \"lngSdSeminarID\"=? and \"lngDozentID\"=?;");
    int ii=1;
    pstm.setLong(ii++, lInfoID);
    pstm.setLong(ii++, user.getSdSeminarID());
    pstm.setLong(ii++, user.getDozentID());
    
    int iRowsAffected=pstm.executeUpdate();
    if(iRowsAffected==0)
        throw new Exception("{\"error\":\"Die Information konnte nicht gelöscht werden, sorry. Gab es vielleicht schon Kommentare?.\"" + 
                    ",\"errorDebug\":\"Delete hat keine Zeile in der Datenbank verändert\",\"errorcode\":" + lERR_BASE + 1 + ",\"severity\":10}"); 

    if(iRowsAffected>1)
        throw new Exception("{\"error\":\"Ein schwerwiegendes Problem ist aufgetreten -- bitte informieren Sie den Systemadministrator.\"" + 
                    ",\"errorDebug\":\"Das Löschen hat mehr als eine Zeile verändert\",\"errorcode\":" + lERR_BASE + 1 + ",\"severity\":10}"); 
%>{"success":"true","leistung":{"id":"<%=lInfoID %>"}}