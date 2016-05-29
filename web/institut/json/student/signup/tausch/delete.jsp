<%@page contentType="text/json" pageEncoding="UTF-8" import="java.sql.ResultSet,de.shj.UP.logic.AktuellesIterator,java.text.SimpleDateFormat, de.shj.UP.data.Dozent,de.shj.UP.data.shjCore" session="true" isThreadSafe="false"  errorPage="../../../error.jsp" %>
<jsp:useBean id="seminar" scope="session" class="de.shj.UP.HTML.HtmlSeminar" />
<jsp:useBean id="student" scope="session" class="de.shj.UP.beans.config.student.StudentBean" /><%--   
        ==================== ==================== ==================== ==================== ====================
        SCHNITTSTELLE:
        - Parameter matrikelnummer muss übergeben sein, und 
        -       numerisch, und
        -       ggf. identisch mit initialisiertem Student
          (sonst Fehlermeldung)
        ==================== ==================== ==================== ==================== ====================
--%>
<%@include file="../../../../fragments/checkVersion.jsp" %>
<%@include file="../../../../fragments/checkLoginStudent.jsp" %>
<%@include file="../../../../fragments/conf_kurswahl_tausch.jsp" %>
<%
        long lERR_BASE=108000 + 300;    // SignUp.Tausch + Delete
        
        try{
            Long.parseLong(request.getParameter("kurstyp_id"));
        }catch(Exception eNoKurstypID){
            throw new Exception("{\"error\":\"Der Platz in diesem Kurs konnte nicht gelöscht werden.\",\"errorDebug\":\"Der erforderliche Parameter >kurstyp_id< hatte den Wert '" + request.getParameter ("kurstyp_id") + "', muss aber numerisch sein.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":100}");
        }
        try{
            Long.parseLong(request.getParameter("kurs_id"));
        }catch(Exception eNoKurstypID){
            throw new Exception("{\"error\":\"Der Platz in diesem Kurs konnte nicht gelöscht werden.\",\"errorDebug\":\"Der erforderliche Parameter >kurs_id< hatte den Wert '" + request.getParameter ("kurs_id") + "', muss aber numerisch sein.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":100}");
        }
        
        if(!bTausch || sKurstypenMitTauschoption.indexOf(request.getParameter("kurstyp_id"))<0)
            throw new Exception("{\"error\":\"Der Platz in diesem Kurs konnte nicht gelöscht werden.\",\"errorDebug\":\"Der Tausch ist momentan für diesen Kurstyp nicht erlaubt.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":20}");
         
	// {1} Die Vormerkung soll gelöscht werden
	if(request.getParameter("cmdDeleteVormerkung")!=null){
		student.sqlExe("delete from \"tblBdAnmeldungSwap\" where " + 
				"\"lngSdSeminarID\"=" + seminar.getSeminarID() + " and " + 
				"\"strMatrikelnummer\"='" + student.getMatrikelnummer() + "' and " + 
				"\"lngKurstypIDWunsch\"=" + Long.parseLong(request.getParameter("kurstyp_id")) + " and " + 
				"\"lngKursIDWunsch\"=" + Long.parseLong(request.getParameter("kurs_id")));
	}else{
            seminar.execProc("Kursplatz_delete",
				"long", String.valueOf(seminar.getSeminarID()),
				"long", request.getParameter("kurstyp_id"), 
				"long", request.getParameter("kurs_id"),
				"String", student.getMatrikelnummer());
        }
        
%>{"success":"true"}
<%--
    JSP-Object "student/signup/delete"

    @TODO
    ===========================

    SYNOPSIS (German)
    ===========================
    2013, June 11, shj
    Erzeugt. 
    
    Üblicher Lifecycle: Delete

    Expected SESSION PROPERTIES
    ===========================

    Expected PARAMETER(S):
    ===========================
    
    Returns
    =======


    Sample Usage
    ============
    
--%>
