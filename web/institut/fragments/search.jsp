<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="iso-8859-1"%><!DOCTYPE html> 
<jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" />
<jsp:useBean id="studenten" scope="page" class="de.shj.UP.beans.config.student.StudentSearchBean" />
<jsp:useBean id="seminar" scope="session" class="de.shj.UP.logic.SeminarData" />
<%
// Besteht eine gültige Sitzung?
if(user.getDozentNachname()==null) return;
if(request.getParameter("txtSuchen").length()<3) return;
studenten.setSeminarID(user.getSdSeminarID());
studenten.setShowAllOnEmptySearch( false );
studenten.init(request);
String strClass="";String sInfo="";
while(studenten.nextStudent()){
   sInfo="";strClass="";
   try{
   	if(studenten.getStudentUrlaub()!=null && studenten.getStudentUrlaub().startsWith("!")){strClass = "tdredalert";sInfo=" (Urlaub)";}
   	if(studenten.getStudentZUVUpdate().before(seminar.getLastZUVUpdate())){strClass = "tdinactive";sInfo=" (Altbestand)";}
   }catch(Exception eWhat){}
%>
<tr class="linked-tablerow <%=strClass %>" id="student-<%=studenten.getStudentPID()%>">
	<td><%=studenten.getStudentNachname() %></td>
	<td><%=studenten.getStudentVorname() %></td>
	<td><%=studenten.getMatrikelnummer() %></td>
	<td><%=studenten.getZUVZielExplain() %></td>
	<td><%=sInfo %></td>
</tr>
<%}%>