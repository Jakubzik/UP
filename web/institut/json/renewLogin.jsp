<%@ page contentType="text/json" pageEncoding="UTF-8" import="de.shj.UP.logic.AktuellesIterator,java.text.SimpleDateFormat, de.shj.UP.data.Dozent,de.shj.UP.data.shjCore" session="true" isThreadSafe="false"%>
<%@page import="java.util.Locale"%><jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean id="seminar" scope="session" class="de.shj.UP.logic.SeminarData" /><jsp:useBean id="student" scope="session" class="de.shj.UP.beans.config.student.StudentBean" />
<%
// mAktuellesPut.jsp 
//------------------------------------------------
//initialize
//------------------------------------------------
%>
<%!
String string2JSON(String s){
	return s.replace('\n', ' ').replace('\r', ' ').replace('\t', ' ').replace('\"', '\''); 
}
%>
<%
	String sErr="";
	long lSEMINAR_ID=Long.parseLong(request.getParameter("seminar_id"));
	System.out.println("Renewing login:" + request.getParameter("seminar_id") + ", " + request.getParameter("user_name") + ", " +request.getParameter("user_pwd"));
//	if(dozent.getDozentNachname()!=null) sErr = "Somebody under this session is already logged in.";
//	else{
	if(!user.login(request.getParameter("user_name"), request.getParameter("user_pwd"), lSEMINAR_ID)) sErr= "Login failed";
//	}
	if(!sErr.equals("")){ Thread.sleep(5000);
	}else{
		seminar.init(lSEMINAR_ID, Locale.GERMANY);
		if(request.getParameter("matrikelnummer").length()>5){
			student.init(lSEMINAR_ID, request.getParameter("matrikelnummer"));
		}
	}
%>
{"success":"<%=sErr.equals("") ? "true" : "false"%>","details":"<%=sErr.equals("") ? user.getDozentAccessLevel() : sErr%>"}
