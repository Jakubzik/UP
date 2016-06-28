<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" />
<jsp:useBean id="student" scope="session" class="de.shj.UP.logic.StudentData" />
<%
	String sPage="index.jsp";
	if(user.getDozentNachname()==null) sPage="index-studierende.jsp";
	user=null;student=null;
	session.invalidate();
%>
<jsp:forward page="<%=sPage %>" />