<%@ page contentType="text/json" pageEncoding="UTF-8"
	import="de.shj.UP.data.Dozent,de.shj.UP.util.PasswordHash"
	session="true" isThreadSafe="false" errorPage="error.jsp"%>
<%@page import="java.util.Locale,java.sql.ResultSet"%><jsp:useBean id="user"
	scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean
	id="seminar" scope="session" class="de.shj.UP.HTML.HtmlSeminar" /><jsp:useBean
	id="student" scope="session"
	class="de.shj.UP.beans.config.student.StudentBean" /><jsp:useBean
	id="sd" scope="session" class="de.shj.UP.util.SessionData" />
<%@include file="../fragments/conf_login.jsp" %>

<%
//------------------------------------------------
//initialize
//------------------------------------------------
%>
<%
String sErr="Fehler";
long lSEMINAR_ID=1;

// Login von Lehrenden
if(request.getParameter("txtMatrikelnummer")==null){
    if(bIsUSE_SECURE_LOGIN_FOR_STAFF){
        if(!user.loginSec(request.getParameter("txtDozentName"), request.getParameter("txtPwd"), lSEMINAR_ID)) sErr= "Login failed";
        else sErr="";
    }else{
        // Das darf nur zu Debugging-Zwecken 
        // verwendet werden: Passworte werden 
        // im Klartext mit der Datenbank verglichen, 
        // d.h. die Passworte müssen im Klartext
        // in der Datenbank gespeichert sein:
        if(!user.login(request.getParameter("txtDozentName"), request.getParameter("txtPwd"), lSEMINAR_ID)) sErr= "Login failed";
        else sErr="";
    }
// Login Studienrende
}else{

        sErr="";
        sd.setSessionType("student");
        student.setSeminarID( lSEMINAR_ID );


        //if(!(student.loginURZ(request.getParameter("txtMatrikelnummer"),student.getDBCleanString(request.getParameter("txtStudentName")),request.getParameter("txtPwd"),lSEMINAR_ID))){
        if(!student.login(request.getParameter("txtMatrikelnummer"), request.getParameter("txtStudentName"), request.getParameter("txtPwd"), lSEMINAR_ID)){
          student.close();
          student=null;
          session.removeAttribute("student");	  
        }else{
                sErr="";
                sd.setSessionType("student");
                student.setSeminarID( lSEMINAR_ID );
        }  
}

if(sd.getLocale()==null) sd.setLocale(request.getLocale());
seminar.init( lSEMINAR_ID, sd.getLocale() );

if(!sErr.equals("")){ Thread.sleep(1000);
}else{
        seminar.init(lSEMINAR_ID, Locale.GERMANY);
}
%>
{"success":"<%=sErr.equals("") ? "true" : "false"%>","details":"<%=sErr.equals("") ? user.getDozentAccessLevel() : sErr%>"}
