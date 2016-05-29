<%--
    JSP-Object "seminar/dozent/termin/delete"
    @Revision: Dec 14, 2013 -- hj
    @Revision: Feb 24, 2014 -- hj: in (A) und (B) unterteilt

    SYNOPSIS (German)
    ===========================
    Zwei Funktionalitäten:
    (A)
      Nutzer können die Buchungen von 
      Terminen stornieren
      (Der Termin wird damit für die erneute 
      Buchung freigegeben)

    (B) 
      User (Anbieter von Sprechstunden) können 
      Termine absagen, also löschen, bzw. 
      falls sie von einem Nutzer gebucht sind 
      als "abgesagt" markieren.
    
    Expected SESSION PROPERTIES
    ===========================
    (A)
    nutzer        muss initialisiert sein 

    --- oder ---
    (B)
    user          muss initialisiert sein

    Expected PARAMETER(S):
    ===========================
    datum:              Datum der zu stornierenden Buchung, Deutsches Format [dd.MM.JJJJ]
    start:              Startzeit der zu stornierenden Buchung, z.B. '14:30'
    seminar_id [long]   Wer hat diesen Termin an-   
    dozent_id  [long]   geboten?

    Beim Absagen von Terminen (B) ist die 
    Angabe von 'start', seminar_id und dozent_id optional: es können auch 
    alle Termine eines Datums in einem Rutsch
    abgesagt werden.

    Returns
    =======
    {
        "success": "true",
    }

    Sample Usage
    ============
 
    
--%><%@page import="java.sql.Date"%>
<%@page import="de.shj.UP.data.shjCore"%>
<%@page import="java.sql.PreparedStatement"%>
<%@include file="../../../../fragments/conf_writing-center.jsp" %>
<%@page contentType="text/json" pageEncoding="UTF-8" import="java.sql.ResultSet,de.shj.UP.data.Dozent,de.shj.UP.util.PasswordHash" session="true" isThreadSafe="false"  errorPage="../../../error.jsp" %>
<jsp:useBean id="nutzer" scope="session" class="de.shj.UP.data.Nutzer" />
<jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" />
<%
    boolean g_bVarianteB=false;
    long lERR_BASE=210000 + 300; //Termine + Delete
    if(nutzer==null || nutzer.getNutzerUniID()==null){
        //throw new Exception("Bitte erst anmelden");
%>
<%@include file="../../../../fragments/checkVersion.jsp" %>
<%@include file="../../../../fragments/checkLogin.jsp" %>
<%@include file="../../../../fragments/checkAccess1.jsp" %>
<%
        g_bVarianteB=true;
    }
    
    Date dtmTerminDatum=null;
    try{
        dtmTerminDatum=new Date(shjCore.g_GERMAN_DATE_FORMAT.parse(request.getParameter("datum")).getTime());
    }catch(Exception eParameter){
        throw new Exception("{\"error\":\"Es wurde das Absagen eines Termins angefordert, aber kein Datum übergeben.\",\"errorDebug\":\"Der übergebene Parameter >datum< (" + request.getParameter("datum") + ") entspricht nicht der Konvention.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":50}");
    }
    
    if(!g_bVarianteB){
        Long lSeminarID=Long.parseLong(request.getParameter("seminar_id"));
        Long lDozentID=Long.parseLong(request.getParameter("dozent_id"));
        PreparedStatement pstm = nutzer.prepareStatement("update \"tblBdSprechstunde\" set \"strNutzerUniID\"=null, \"strSprechstundeBemerkung\"='' where "+
                "\"strNutzerUniID\"=? and \"lngSdSeminarID\"=? and \"lngDozentID\"=? and \"dtmSprechstundeDatum\"=? and "+
                "\"tmeSprechstundeStart\"=? ");
        int ii=1;
        pstm.setString(ii++, nutzer.getNutzerUniID());
        pstm.setLong(ii++, lSeminarID);
        pstm.setLong(ii++, lDozentID);
        pstm.setDate(ii++, dtmTerminDatum);
        pstm.setTime(ii++, java.sql.Time.valueOf(request.getParameter("start") + ":00"));
        pstm.execute();
        if(pstm.getUpdateCount() != 1) throw new Exception("Fehler -- Terminbuchung konnte nicht abgesagt werden.");
        
        // Generelle Wartungsfunktion: verwaiste Sprechstunden löschen
        // (Wird eine Sprechstunde vom Anbieter abgesagt, während sie 
        // noch von einem Nutzer gebucht ist, der später dann aber 
        // absagt, bleibt eine leere, abgesagte Sprechstunde zurück...)
        nutzer.sqlExe("delete from \"tblBdSprechstunde\" where  \"blnSprechstundeAbgesagt\"=true and \"strNutzerUniID\" is null;");
    }else{
        boolean bSpecificSlot=(request.getParameter("start")!=null);
        
        // Lösche die spezifizierten Termine, sofern sie nicht 
        // schon gebucht sind:
        PreparedStatement pstm = user.prepareStatement("delete from \"tblBdSprechstunde\" where "+
                "\"lngSdSeminarID\"=? and \"lngDozentID\"=? and \"dtmSprechstundeDatum\"=? and \"strNutzerUniID\" is null "+
                (bSpecificSlot ? "and \"tmeSprechstundeStart\"=?" : ""));
        int ii=1;
        pstm.setLong(ii++, user.getSdSeminarID());
        pstm.setLong(ii++, user.getDozentID());
        pstm.setDate(ii++, dtmTerminDatum);
        if(bSpecificSlot) pstm.setTime(ii++, java.sql.Time.valueOf(request.getParameter("start") + ":00"));
        pstm.execute();
        
        // Markiere die übrigen Slots als abgesagt:
        pstm = user.prepareStatement("update \"tblBdSprechstunde\" set \"blnSprechstundeAbgesagt\"=true where "+
                "\"lngSdSeminarID\"=? and \"lngDozentID\"=? and \"dtmSprechstundeDatum\"=? "+
                (bSpecificSlot ? "and \"tmeSprechstundeStart\"=?" : ""));
        
        ii=1;
        pstm.setLong(ii++, user.getSdSeminarID());
        pstm.setLong(ii++, user.getDozentID());
        pstm.setDate(ii++, dtmTerminDatum);
        if(bSpecificSlot) pstm.setTime(ii++, java.sql.Time.valueOf(request.getParameter("start") + ":00"));
        pstm.execute();
        
        // Generelle Wartungsfunktion: verwaiste Sprechstunden löschen
        // (Wird eine Sprechstunde vom Anbieter abgesagt, während sie 
        // noch von einem Nutzer gebucht ist, der später dann aber 
        // absagt, bleibt eine leere, abgesagte Sprechstunde zurück...)
        user.sqlExe("delete from \"tblBdSprechstunde\" where  \"blnSprechstundeAbgesagt\"=true and \"strNutzerUniID\" is null;");
    }
    

%>{"success":"true"}