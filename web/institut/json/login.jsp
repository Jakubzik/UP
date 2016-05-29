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
<%!
String string2JSON(String s){
	return s.replace('\n', ' ').replace('\r', ' ').replace('\t', ' ').replace('\"', '\''); 
}
boolean loginWITHOUTURZ(de.shj.UP.logic.StudentData s, String sMatrikelnr, String sName, String sGebDat, long lSeminarID){
	boolean blnReturn=false;
	ResultSet rst=null;
	sGebDat = s.shjGetPgDate(sGebDat);
	String strSQL =
		"SELECT " +
		   "b.*, " +
			"x.\"lngSeminarID\", " +
			"x.\"intFachID\" " +

		"FROM \"tblBdStudent\" b, \"tblSdSeminarXFach\" x " +
		"WHERE " +
		 "(" +
		   "b.\"strMatrikelnummer\"='" + sMatrikelnr + "' AND " +
		   "b.\"strStudentNachname\"='" + sName + "' AND " +
		   "b.\"dtmStudentGeburtstag\"='" + sGebDat + "' AND " +
		   "x.\"lngSeminarID\"=" + lSeminarID + " AND " +
		   "(" +
			 "b.\"intStudentFach1\"=x.\"intFachID\" OR " +
			 "b.\"intStudentFach2\"=x.\"intFachID\" OR " +
			 "b.\"intStudentFach3\"=x.\"intFachID\" OR " +
			 "b.\"intStudentFach4\"=x.\"intFachID\" OR " +
			 "b.\"intStudentFach5\"=x.\"intFachID\" OR " +
			 "b.\"intStudentFach6\"=x.\"intFachID\" " +
		   ")" +
		 ");";

	try{
		rst=s.sqlQuery(strSQL);
		if(rst.next()){
			s.initByRst(rst);
			blnReturn=true;
			}
		rst.close();
		}catch(Exception eLogin){
			blnReturn=false;
	}
	return blnReturn;

}
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
        if(!user.login(request.getParameter("txtDozentName"), request.getParameter("txtPwd"), lSEMINAR_ID)) sErr= "Login failed";
        else sErr="";
    }
// Login Studienrende
}else{

                sErr="";
                sd.setSessionType("student");
                student.setSeminarID( lSEMINAR_ID );
                student.setURZPassword(request.getParameter("txtPwd"));


                //if(!(student.loginURZ(request.getParameter("txtMatrikelnummer"),student.getDBCleanString(request.getParameter("txtStudentName")),request.getParameter("txtPwd"),lSEMINAR_ID))){
                if(!(loginWITHOUTURZ(student,request.getParameter("txtMatrikelnummer"),student.getDBCleanString(request.getParameter("txtStudentName")),request.getParameter("txtPwd"),lSEMINAR_ID))){
                  student.close();
                  student=null;
                  session.removeAttribute("student");	  

                  String sPwd = request.getParameter("txtPwd");

                  String sSonderzeichen = "äÄöÖüÜßéè";
                  for(int ii=0;ii<sSonderzeichen.length();ii++){
                                if(sPwd.indexOf(sSonderzeichen.charAt(ii)) > 0) sErr="Login-Fehler: Umlaute im Passwort sind ein Problem";
                  }

                  sErr="Login fehlgeschlagen";
                }else{
                        sErr="";
                        sd.setSessionType("student");
                        student.setSeminarID( lSEMINAR_ID );
                        student.setURZPassword(request.getParameter("txtPwd"));
                }  

}

//	if(sd.getLanguage()==null) sd.setLanguage( java.util.ResourceBundle.getBundle("StudentsFrame", request.getLocale()) );
if(sd.getLocale()==null) sd.setLocale(request.getLocale());

seminar.init( lSEMINAR_ID, sd.getLocale() );

if(!sErr.equals("")){ Thread.sleep(1000);
}else{
        seminar.init(lSEMINAR_ID, Locale.GERMANY);
}
%>
{"success":"<%=sErr.equals("") ? "true" : "false"%>","details":"<%=sErr.equals("") ? user.getDozentAccessLevel() : sErr%>"}
