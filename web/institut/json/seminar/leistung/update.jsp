<%--
    JSP-Object "seminar/leistung/update"
    @Erzeugt: Jun 26, 2014 -- hj

    @TODO
    ===========================
    Weitere Eigenschaften aus tblSdLeistung
    ergänzen (custom1, custom3, commitmentmode)

    SYNOPSIS (German)
    ===========================  
    Ändert die Stammdaten einer Leistung (Name, engl. Name etc.)
    
    Expected SESSION PROPERTIES
    ===========================
    user        muss initialisiert sein & Access Level > 500 haben

    Expected PARAMETER(S):
    ===========================
    id:             [long]                      - Id der Leistung
    name:           [String]
    name_en:        [String]
    cp:             [float]
    code:           [String]


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
<jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean id="seminar" scope="session" class="de.shj.UP.logic.SeminarData" />
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
        throw new Exception("{\"error\":\"Die Änderungen an den Details der Leistung können nicht gespeichert werden. Es wurde keine Id übergeben.\"" + 
                    ",\"errorDebug\":\"Der Parameter >id< (für die LeistungsID) hat den nicht-numerischen Wert " + request.getParameter("id") + ".\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":10}");               
    }
    PreparedStatement pstm= user.prepareStatement("update \"tblSdLeistung\" " +
            "set \"strLeistungBezeichnung\"=?," +
            "\"strLeistungBezeichnung_en\"=?," +
            "\"sngLeistungCreditPts\"=?," +
            "\"strLeistungCustom2\"=? where " +
            "\"lngLeistungID\"=? and \"lngSdSeminarID\"=?");
    
    int ii=1;
    pstm.setString(ii++, request.getParameter("name").trim());
    pstm.setString(ii++, request.getParameter("name_en").trim());
    pstm.setFloat(ii++, Float.parseFloat(request.getParameter("cp").trim()));
    pstm.setString(ii++, request.getParameter("code").trim());
    pstm.setLong(ii++, lLeistungID);
    pstm.setLong(ii++, user.getSdSeminarID());
    
    int iRowsAffected=pstm.executeUpdate();
    
    if(iRowsAffected==0)
        throw new Exception("{\"error\":\"Die Leistung mit der id >" + lLeistungID + "< wurde nicht gefunden.\"" + 
                    ",\"errorDebug\":\"Update hat keine Zeile in der Datenbank verändert\",\"errorcode\":" + lERR_BASE + 1 + ",\"severity\":10}"); 

    if(iRowsAffected>1)
        throw new Exception("{\"error\":\"Ein schwerwiegendes Problem ist aufgetreten -- bitte informieren Sie den Systemadministrator.\"" + 
                    ",\"errorDebug\":\"Das Update hat mehr als eine Zeile verändert\",\"errorcode\":" + lERR_BASE + 1 + ",\"severity\":10}"); 

%>{"success":"true","leistung":{"id":"<%=lLeistungID %>","name":"<%=request.getParameter("name").trim() %>", "name_en":"<%=request.getParameter("name_en").trim() %>", "cp":"<%=Float.parseFloat(request.getParameter("cp").trim()) %>","code":"<%=request.getParameter("code").trim() %>"}}