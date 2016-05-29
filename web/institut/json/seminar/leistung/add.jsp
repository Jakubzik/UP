<%--
    JSP-Object "seminar/leistung/add"
    @Erzeugt: Jun 26, 2014 -- hj

    @TODO
    ===========================
    Weitere Eigenschaften aus tblSdLeistung
    ergänzen (custom1, custom3, commitmentmode)

    SYNOPSIS (German)
    ===========================  
    Fügt eine Leistung zu den Stammdaten hinzu (Name, engl. Name etc.).
    Wenn schon eine Leistung gleichen Namens vorliegt, wird 
    ein Fehler ausgelöst.
    
    Expected SESSION PROPERTIES
    ===========================
    user        muss initialisiert sein & Access Level > 500 haben

    Expected PARAMETER(S):
    ===========================
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
<jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean id="seminar" scope="session" class="de.shj.UP.HTML.HtmlSeminar" />
<%@include file="../../../fragments/checkVersion.jsp" %>
<%@include file="../../../fragments/checkLogin.jsp" %>
<%@include file="../../../fragments/checkAccess500.jsp" %>
<%long lERR_BASE=213000 + 400;    // Leistung + Add

    // -------------------------------------------------------------
    // Initialisierung
    long lLeistungID=-1;
    Leistung oLeistung=null;

    // -------------------------------------------------------------
    // Schnittstelle
    try{
        lLeistungID=user.getNextID("lngLeistungID", "tblSdLeistung", "\"lngSdSeminarID\"=" + user.getSdSeminarID());
    }catch(Exception eNotNumeric){
        throw new Exception("{\"error\":\"Die Leistung kann nicht hinzugefügt werden. Es gibt keine neue ID.\"" + 
                    ",\"errorDebug\":\"Keine weitere Info.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":10}");               
    }
    if(!(user.lookUp("lngLeistungID", "tblSdLeistung", "\"strLeistungBezeichnung\"='" + 
                request.getParameter("name").trim() + "' and \"lngSdSeminarID\"=" + user.getSdSeminarID()).startsWith("#")))
        throw new Exception("{\"error\":\"Eine Leistung mit der Bezeichnung '" + request.getParameter("name").trim() + "' gibt es bereits.\"" + 
                    ",\"errorDebug\":\"Keine weitere Info.\",\"errorcode\":" + lERR_BASE + 5 + ",\"severity\":10}");     
    PreparedStatement pstm= user.prepareStatement("insert into \"tblSdLeistung\" (" +
            "\"strLeistungBezeichnung\",\"strLeistungBezeichnung_en\",\"sngLeistungCreditPts\",\"strLeistungCustom2\", \"lngLeistungID\", \"lngSdSeminarID\") values (" +
            "?,?,?,?,?,?);");
    int ii=1;
    pstm.setString(ii++, request.getParameter("name").trim());
    pstm.setString(ii++, request.getParameter("name_en").trim());
    pstm.setFloat(ii++, Float.parseFloat(request.getParameter("cp").trim()));
    pstm.setString(ii++, request.getParameter("code").trim());
    pstm.setLong(ii++, lLeistungID);
    pstm.setLong(ii++, user.getSdSeminarID());
    
    int iRowsAffected=pstm.executeUpdate();

    if(iRowsAffected==0)
        throw new Exception("{\"error\":\"Die Leistung konnte nicht hinzugefügt werden, sorry..\"" + 
                    ",\"errorDebug\":\"Update hat keine Zeile in der Datenbank verändert\",\"errorcode\":" + lERR_BASE + 1 + ",\"severity\":10}"); 

    if(iRowsAffected>1)
        throw new Exception("{\"error\":\"Ein schwerwiegendes Problem ist aufgetreten -- bitte informieren Sie den Systemadministrator.\"" + 
                    ",\"errorDebug\":\"Das Hinzufügen hat mehr als eine Zeile verändert\",\"errorcode\":" + lERR_BASE + 1 + ",\"severity\":10}"); 


%>{"success":"true","leistung":{"id":"<%=lLeistungID %>","name":"<%=request.getParameter("name").trim() %>", "name_en":"<%=request.getParameter("name_en").trim() %>", "cp":"<%=Float.parseFloat(request.getParameter("cp").trim()) %>","code":"<%=request.getParameter("code").trim() %>"}}