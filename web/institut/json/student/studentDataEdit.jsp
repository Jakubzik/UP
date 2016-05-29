<%@ page errorPage="../error.jsp" contentType="text/json" pageEncoding="UTF-8" import="java.sql.ResultSet,de.shj.UP.logic.AktuellesIterator,java.text.SimpleDateFormat, de.shj.UP.data.Dozent,de.shj.UP.data.shjCore" session="true" isThreadSafe="false"  %><%@page import="java.util.Locale"%><jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean id="seminar" scope="session" class="de.shj.UP.HTML.HtmlSeminar" /><jsp:useBean id="student" scope="session" class="de.shj.UP.beans.config.student.StudentBean" /><%if(student.getStudentNachname()==null && user.getDozentNachname()==null){%><jsp:include page="../checkAccess.jsp" flush="true"></jsp:include><%}%><%
if(request.getParameter("txtMatrikelnummer").length()!=7) return;
	if(request.getParameter("txtEmail")==null) throw new Exception("{\"error\":\"Eine E-Mail soll verändert werden, der neue Wert ist aber null.\",\"errorcode\":2}");
	if(request.getParameter("txtEmail").length()<4) throw new Exception("{\"error\":\"Eine E-Mail soll verändert werden, der neue Wert ist aber zu kurz.\",\"errorcode\":2}");;
	student.setStudentEmail(request.getParameter("txtEmail"));
        student.setStudentHandy(request.getParameter("txtHandy"));
        student.setStudentPublishEmail(request.getParameter("txtInfoEmail").equals("true"));
	student.update();
%>{"success":"true"}
