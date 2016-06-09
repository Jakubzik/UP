<%@ page errorPage="../error.jsp" contentType="text/json" pageEncoding="UTF-8" import="java.sql.ResultSet,de.shj.UP.logic.AktuellesIterator,java.text.SimpleDateFormat, de.shj.UP.data.Dozent,de.shj.UP.data.shjCore" session="true" isThreadSafe="false"  %>
<%@page import="java.util.Locale"%><jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean id="seminar" scope="session" class="de.shj.UP.logic.SeminarData" />
<jsp:useBean id="student" scope="session" class="de.shj.UP.beans.config.student.StudentBean" />
<%if(student.getStudentNachname()==null && user.getDozentNachname()==null){%><jsp:include page="../checkAccess.jsp" flush="true"></jsp:include><%}%>
<%
	if(!request.getParameter("txtMatrikelnummer").equals(student.getMatrikelnummer())) throw new Exception("{\"error\":\"Unklare Sitzungsführung. Bitte loggen Sie sich nur in einem Register ein.\",\"errorcode\":3}");;
	student.setStudentPublishEmail(!student.getStudentPublishEmail());
	student.update();
%>{"success":"true"}
