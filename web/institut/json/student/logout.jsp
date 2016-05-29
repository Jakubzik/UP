<%@ page contentType="text/json" pageEncoding="UTF-8"%>
<jsp:useBean id="student" scope="session" class="de.shj.UP.beans.config.student.StudentBean" />
<%
	session.invalidate();
%>
{"success":"true"}