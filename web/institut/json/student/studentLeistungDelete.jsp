<%@ page contentType="text/json" pageEncoding="UTF-8" import="java.util.Enumeration,java.text.SimpleDateFormat, de.shj.UP.data.Dozent,de.shj.UP.data.shjCore" session="true" isThreadSafe="false" errorPage="../error.jsp"%>
<%@page import="java.util.Locale"%>
<%@page import="de.shj.UP.data.StudentXLeistung"%><jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean id="seminar" scope="session" class="de.shj.UP.logic.SeminarData" />
<jsp:useBean id="student" scope="session" class="de.shj.UP.beans.config.student.StudentBean" />
<jsp:useBean id="credits" scope="page" class="de.shj.UP.beans.config.student.StudentLeistungBean" />
<%
        if(student.getMatrikelnummer()==null) {
                System.out.println("Last name is null, calling an Exception"); 
                throw new Exception("{\"error\":\"Das Login ist abgelaufen.\",\"errorcode\":1}");
        }
        
        // Aufruf von "Config"
	if(user.getDozentNachname()!=null && request.getParameter("idxLeistungIDOrig")!=null){
		StudentXLeistung sl = new StudentXLeistung(
			user.getSdSeminarID(), 
			student.getMatrikelnummer(),
			Long.parseLong(request.getParameter("idxLeistungIDOrig")),
			Long.parseLong(request.getParameter("idxLeistungCountOrig")));
			if(!sl.delete()){throw new Exception("{\"error\":\"Das Löschen der Anmeldung oder Leistung ist fehlgeschlagen.\",\"errorcode\":10}");}
	}else{
		// Studierende dürfen Ihre Anmeldungen 
		// selbst löschen
		StudentXLeistung sl = new StudentXLeistung(
				student.getSeminarID(), 
				student.getMatrikelnummer(),
				Long.parseLong(request.getParameter("Anmeldung_id")),
				Long.parseLong(request.getParameter("Anmeldung_count")));
		
		if(sl.getStudentLeistungKlausuranmeldung()){
			if(!sl.delete()){throw new Exception("{\"error\":\"Das Löschen der Anmeldung fehlgeschlagen.\",\"errorcode\":1000}");}
		}else{
			// das war keine Anmeldung sondern eine Schein:
			throw new Exception("{\"error\":\"Sorry -- Sie dürfen keine Leistungen löschen.\",\"errorcode\":1001}");
		}
	}
	%>{"json":"antwort"}