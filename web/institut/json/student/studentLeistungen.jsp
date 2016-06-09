<%@ page contentType="text/json" pageEncoding="UTF-8" import="java.text.SimpleDateFormat, de.shj.UP.data.Dozent,de.shj.UP.data.shjCore" session="true" isThreadSafe="false" errorPage="../error.jsp"%>
<%@page import="java.util.Locale"%><jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean id="seminar" scope="session" class="de.shj.UP.logic.SeminarData" />
<jsp:useBean id="student" scope="session" class="de.shj.UP.beans.config.student.StudentBean" />
<jsp:useBean id="credits" scope="page" class="de.shj.UP.beans.config.student.StudentLeistungBean" /><%if(student.getStudentNachname()==null && user.getDozentNachname()==null){%><jsp:include page="../checkAccess.jsp" flush="true"></jsp:include><%}%>
<%!
String string2JSON(String s){
	return (s==null) ? "" : s.replace('\n', ' ').replace('\r', ' ').replace('\t', ' ').replaceAll("\"", "\\\\\""); 
}
String nn(Object s){
	return (s==null)?"":(((String)s).equals("null") ? "" : (String)s);
}
%>[
<%
	if(request.getParameter("txtMatrikelnummer").length()!=7) {System.out.println("Ungültige matrikelnummer");return;}
	if(!request.getParameter("txtMatrikelnummer").equals(student.getMatrikelnummer())) {System.out.println("Unklare Sitzungsführung");return;}
	credits.init(student, request);
	boolean bFirst=true;
	String sModul="";
	while(credits.nextLeistung()){if(!bFirst) out.write(","); bFirst=false;
	try{sModul="" + credits.getModulID();}catch(Exception eNullPointer){sModul="null";}
	%>
	{"leistung":{"name":"<%=credits.getLeistungBezeichnung() %>",
	"matrikelnummer":"<%=student.getMatrikelnummer()%>",
	"id":"<%=credits.getLeistungsID() %>",
	"count":"<%=credits.getStudentLeistungCount() %>",
	"ects":"<%=credits.getStudentLeistungCreditPts()%>",
	"kurstitel":"<%=nn(string2JSON(credits.getSLKursTitel())) %>",
	"kurstitel_en":"<%=nn(string2JSON(credits.getSLKursTitel_en())) %>",
	"note":"<%=credits.Note().getNoteECTSCalc()==0 ? credits.Note().getNoteECTSGrade() : credits.Note().getNoteECTSCalc() %>",
	"notenbezeichnung":"<%=credits.Note().getNoteNameDE() %>",
	"noten_id":"<%=credits.Note().getNoteID() %>",
	"leistungstyp":"<%=nn(credits.getStudentLeistungCustom3()).trim() %>",
	"details":"<%=nn(string2JSON(credits.getStudentLeistungDetails())) %>",
	"bemerkung":"<%=nn(credits.getStudentLeistungBemerkung()) %>",
        "is_anmeldung":"<%=credits.isCommitmentMode()%>",
	"datum":"<%try{%><%=credits.isCommitmentMode() ? shjCore.shjGetGermanDate(credits.getStudentLeistungAntragdatum()) : shjCore.shjGetGermanDate(credits.getStudentLeistungAusstellungsdatum()) %><%}catch(Exception eNoDateGiven){%><%=shjCore.shjGetGermanDate(user.g_TODAY) %><%}%>",
	"aussteller":"<%=credits.getStudentLeistungAusstellerVorname() + " " + credits.getStudentLeistungAussteller() %>",
	"modul":"<%=sModul %>"
	}}		
<%}%>
]