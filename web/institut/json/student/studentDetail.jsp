<%@ page contentType="text/json" pageEncoding="UTF-8" import="java.sql.ResultSet,de.shj.UP.logic.AktuellesIterator,java.text.SimpleDateFormat, de.shj.UP.data.Dozent,de.shj.UP.data.shjCore" session="true" isThreadSafe="false"  %>
<%@page import="java.util.Locale"%><jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean id="seminar" scope="session" class="de.shj.UP.HTML.HtmlSeminar" />
<jsp:useBean id="student" scope="session" class="de.shj.UP.beans.config.student.StudentBean" />
<%if(student.getStudentNachname()==null && user.getDozentNachname()==null){throw new Exception("{\"error\":\"Das Login ist abgelaufen.\",\"errorcode\":1}");}%>
<%!
String string2JSON(String s){
	if(s==null) return "";
	return s.replace('\n', ' ').replace('\r', ' ').replace('\t', ' ').replace('\"', '\''); 
}
String getModuleJSON(de.shj.UP.HTML.HtmlSeminar seminar, long lFachID)throws Exception{
	String sReturn="";
	ResultSet rNoten=seminar.sqlQuery("SELECT distinct m.\"lngModulID\", " +
		       "m.\"strModulBezeichnung\", mxl.\"lngLeistungID\" " +
		       "FROM \"tblSdModulXLeistung\" mxl, \"tblSdModul\" m, \"tblSdPruefungXModul\" pxm, \"tblSdPruefungXFach\" pxf " + 
		       "WHERE mxl.\"lngSdSeminarID\" = m.\"lngSdSeminarID\" AND " +
		             "mxl.\"lngModulID\" = m.\"lngModulID\" AND " +
		             "pxm.\"lngSdSeminarID\" = m.\"lngSdSeminarID\" AND " +
		             "pxm.\"lngModulID\" = m.\"lngModulID\" AND " +
		             "pxf.\"lngSdSeminarID\" = pxm.\"lngSdSeminarID\" AND " +
		             "pxf.\"lngPruefungID\" = pxm.\"lngPruefungID\" AND " +
		             "mxl.\"lngSdSeminarID\" = " + seminar.getSeminarID() + " AND " +
		             "m.\"lngSdSeminarID\" = " + seminar.getSeminarID() + " AND " +
		             (true==false ? "m.\"blnModulWaehlbar\" = 't' AND " : "") +
		             "pxm.\"lngSdSeminarID\" = " + seminar.getSeminarID() + " AND " +
		             "pxf.\"intFachID\" = " + lFachID + " AND " +
		             "pxf.\"lngSdSeminarID\" = " + seminar.getSeminarID() + " " +
		             "ORDER BY m.\"strModulBezeichnung\" ASC;");


	String sLeistungen="";
	String sModul="";
	long lModulIDCurr=-99; long lModulIDAlt=-1;
	while(rNoten.next()){
		if(lModulIDCurr==-99){
			lModulIDCurr = rNoten.getLong("lngModulID");
			lModulIDAlt = lModulIDCurr;
		}else{
			lModulIDCurr = rNoten.getLong("lngModulID");
		}
		
		if(lModulIDCurr==lModulIDAlt){
			sModul = rNoten.getString("strModulBezeichnung");
			sLeistungen += rNoten.getString("lngLeistungID") + ";";
		}else{
			sReturn += "{\"modul\":{" + 
						"\"name\":\"" + sModul + "\"," +
						"\"id\":\"" + lModulIDAlt + "\"," +
						"\"leistungen\":\";" + sLeistungen + "\"" +
					"}},";
			sModul = rNoten.getString("strModulBezeichnung");
			sLeistungen = rNoten.getString("lngLeistungID") + ";";
			lModulIDAlt = lModulIDCurr;
		}
	}
	
	return "[" + sReturn + "{\"modul\":{" + 
				"\"name\":\"" + sModul + "\"," +
				"\"id\":\"" + lModulIDCurr + "\"," +
				"\"leistungen\":\";" + sLeistungen + ";\"" +
			"}}]";
}
%>
<%
	if(request.getParameter("txtMatrikelnummer").length()!=7) return;
	student.init(seminar.getSeminarID(), request);
        //System.out.println(getModuleJSON(seminar, student.Fach().getFachID()));
%>
{"matrikelnummer":"<%=student.getMatrikelnummer() %>",
"pid":"<%=student.getStudentPID()%>",
"vorname":"<%=student.getStudentVorname() %>",
"nachname":"<%=student.getStudentNachname() %>",
"geburtstag":"<%= (student.getStudentGeburtstag()==null ? "" : shjCore.shjGetGermanDate(student.getStudentGeburtstag())) %>",
"geburtsort":"<%=student.getStudentGeburtsort()%>",
"staat":"<%=student.getStudentStaat()%>",
"email":"<%=student.getStudentEmail() %>",
"strasse":"<%=student.getStudentStrasse() %>",
"plz":"<%=student.getStudentPLZ() %>",
"ort":"<%=student.getStudentOrt() %>",
"telefon":"<%=student.getStudentTelefon() %>",
"mobil":"<%=student.getStudentHandy() %>",
"homepage":"<%=student.getStudentHomepage() %>",
"erstimmatrikulation":"<%=student.getStudentZUVImmatrikuliert() %>",
"update":"<%=student.getStudentZUVUpdate() %>",
"anrede":"<%=(student.getStudentFemale() ? "Frau" : "Herr") %>",
"fach":"<%=student.Fach().getFachBeschreibung() %>",
"fach_id":"<%=student.Fach().getFachID() %>",
"urlaub":"<%=string2JSON(student.getStudentUrlaubSummary()) %>",
"semester":"<%=student.getFachsemester() %>",
"ziel":"<%=student.getZUVZielExplain() %>",
"info_email":"<%=student.getStudentPublishEmail() %>",
"module":<%=getModuleJSON(seminar, student.Fach().getFachID())%>
}
