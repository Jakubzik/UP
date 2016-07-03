<%@ page contentType="text/json" errorPage="../../error.jsp" session="true" import="java.sql.PreparedStatement,java.util.Date, java.util.List,java.io.File,java.util.Random,de.shj.UP.beans.student.ExamIterator,de.shj.UP.transcript.*,de.shj.UP.data.Pruefung,de.shj.UP.transcript.Fachnote,de.shj.UP.data.shjCore,java.sql.ResultSet" %>
<jsp:useBean id="student" scope="session" class="de.shj.UP.logic.StudentData" />
<jsp:useBean id="seminar" scope="session" class="de.shj.UP.logic.SeminarData" />
<%@include file="../../../fragments/checkLoginStudent.jsp" %>
<%if(request.getParameter("permanent")!=null){%><jsp:forward page="add_permanent.jsp" /><%}%>