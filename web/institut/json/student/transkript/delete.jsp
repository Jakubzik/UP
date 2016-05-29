<%--
    JSP-Object "student/transkript/delete"
    
    @TODO
    ===========================
    löscht einen Eintrag aus tblBdStudentDokument

    SYNOPSIS (German)
    ===========================
    2013, Okt 30, shj    erzeugt. 
    2014, Jan 3, shj     überarbeitet
    
    Üblicher Lifecycle: DELETE


    @SEE ALSO
    ===========
    
    Expected SESSION PROPERTIES
    ===========================
    user        muss initialisiert sein (AccessLevel 500 oder höher),
    ODER        sd.getSessionType == "student"

    student     muss identische Matrikelnummer haben, die auch
                als Parameter übergeben wird.

    Expected PARAMETER(S):
    ===========================
    matrikelnummer  [long]
    verifikationscode    [String] (Länge 6 oder 4, keine Anführungszeichen)
    signup-expected-backend-version [text], vgl. fragments/checkVersion.jsp
    
    Returns
    =======
    Object:
    {"success":"true"}
    
--%>
<%@ page contentType="text/json" pageEncoding="UTF-8" import="java.util.Date,java.sql.PreparedStatement,java.util.Enumeration,java.text.SimpleDateFormat, de.shj.UP.data.Dozent,de.shj.UP.data.shjCore" session="true" isThreadSafe="false" errorPage="../../error.jsp"%>
<%@page import="de.shj.UP.data.StudentBemerkung"%><jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean id="seminar" scope="session" class="de.shj.UP.HTML.HtmlSeminar" />
<jsp:useBean id="student" scope="session" class="de.shj.UP.beans.config.student.StudentBean" />
<jsp:useBean id="credits" scope="page" class="de.shj.UP.beans.config.student.StudentLeistungBean" />
<jsp:useBean id="sd" scope="session" class="de.shj.UP.util.SessionData" />
<%@include file="../../../fragments/checkVersion.jsp" %>
<%long lERR_BASE=109000 + 300;    // Transkript + Delete
    if(sd.getSessionType().equals("student")){%>
<%@include file="../../../fragments/checkLoginStudent.jsp" %>    
<%
if(!student.getMatrikelnummer().equals(request.getParameter("matrikelnummer")))
    throw new Exception("{\"error\":\"Die Sitzung ist abgelaufen, bitte melden Sie sich neu an.\",\"errorDebug\":\"Abruf der Leistung aus Studierendensicht, aber die Sitzungs-Matrikelnummer entspricht nicht der übergebenen Matrikelnummer. Abbruch sicherheitshalber.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":50}");
}else{%>
<%@include file="../../../fragments/checkLogin.jsp" %>
<%@include file="../../../fragments/checkAccess500.jsp" %>
<%@include file="../../../fragments/checkInitStudent.jsp" %>
<%}

    // ==============================================================
    // Schnittstelle
    // ==============================================================
    if(request.getParameter("verifikationscode").length()<4 || request.getParameter("verifikationscode").contains("\"")) return;
    PreparedStatement pstm=seminar.prepareStatement("delete from \"tblBdStudentDokument\" where " +
			"\"lngSdSeminarID\"=? and " +
			"\"strMatrikelnummer\"=? and " +  
			"\"strVerifikationscode\"=?;");
    int ii=1;
    pstm.setLong(ii++, seminar.getSeminarID());
    pstm.setString(ii++,student.getMatrikelnummer());
    pstm.setString(ii++, request.getParameter("verifikationscode"));
    if(pstm.executeUpdate()!=1){
		throw new Exception("{\"error\":\"Das Transkript konnte nicht gelöscht werden.\",\"errorDebug\":\"Die Datenbank hat sich verweigert -- Näheres vielleicht in deren Logs?\",\"errorcode\":" + lERR_BASE + 1 + ",\"severity\":50}");
    }else{%>{"success":"true"}<%}%>
