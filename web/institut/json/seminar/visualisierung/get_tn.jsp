<%--
    JSP-Object "seminar/visualisierung/get"

    SYNOPSIS (German)
    ===========================
    2015, Dec 26
    
    Üblicher Lifecycle: GET

  
    Expected SESSION PROPERTIES
    ===========================
    user        muss initialisiert sein (AccessLevel egal) ODER

    Expected PARAMETER(S):
    ===========================
    
    Returns
    =======

    Sample Usage
    ============

    
--%><%@page import="java.util.Hashtable"%>
<%@page contentType="text/json" pageEncoding="UTF-8" import="java.sql.ResultSet,de.shj.UP.data.Dozent,de.shj.UP.data.shjCore" session="true" isThreadSafe="false"  errorPage="../../error.jsp" %>
<jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean id="seminar" scope="session" class="de.shj.UP.HTML.HtmlSeminar" />
<jsp:useBean id="sd" scope="session" class="de.shj.UP.util.SessionData" /><jsp:useBean id="student" scope="session" class="de.shj.UP.beans.config.student.StudentBean" />
<%@include file="../../../fragments/checkVersion.jsp" %>
<%long lERR_BASE=300000 + 100;    // Visualisierung + Get %>
<%@include file="../../../fragments/checkLogin.jsp" %>
<%
/**
 * Idee vgl.: http://bl.ocks.org/mbostock/4063269
 * 
 * 
 * Filter:
 * jahr=<int> Jahr, in dem die Leistung ausgestellt wurde.
 * note=<var> Note:
 *      entweder Notenwert (1,2,3,4,5) oder "anerkannt" -- letzteres schließt die Anerkennung von Noten mit ein.
 * */

String sFilter="";

 // Brauche
 // (1) Array mit Semestern: ["Sommer 2015", "Winter 2015/2016" ... ]
 // 
 // (2) TN lt. Seminarstatistik als Array [15,17,21]
 // 
 // (3) # Kurse als Array [5, 7, 2]
 //
 // (4) # Tatsächlich erzeugte Scheine [1,3,10]


// Alle Semester, in denen Einträge vorliegen.
// Will wissen: (1) wie viele sind das? und
// (2) wie heisst das erste Semester mit Einträgen?
int iNoOfSemesters=0;
int iNoOfSemestersToIgnore=3;	// In den ersten drei Semestern stehen nur Nullen
String sSemStartName="";
ResultSet rSemester = teacher.sqlQuery("SELECT \"strKvvArchivSemesterName\", " + 
		"MIN( \"lngID\" ) " + 
		"FROM \"tblBdKvvArchiv\" WHERE \"lngSdSeminarID\" = " + seminar.getSeminarID() + " " + 
		"GROUP BY \"strKvvArchivSemesterName\" ORDER BY MIN( \"lngID\" ) ASC");

String sLegende="";
Hashtable semester = new Hashtable();
while(rSemester.next()){
	iNoOfSemesters++;
	if(iNoOfSemesters > iNoOfSemestersToIgnore){
		sLegende += (iNoOfSemesters-iNoOfSemestersToIgnore) + " = " + rSemester.getString("strKvvArchivSemesterName") + "<br />\n";
		semester.put((iNoOfSemesters-iNoOfSemestersToIgnore), rSemester.getString("strKvvArchivSemesterName"));
	}
}
ResultSet rStatistik = teacher.sqlQuery("SELECT t1.semester, " + 
		"t1.minid, t2.kurstyp, t2.extrapolated, t2.courses, t2.sum_real, t2.not_entered, t2.avg_extrapolated " + 
		"FROM " + 
			"(SELECT \"tblBdKvvArchiv\".\"strKvvArchivSemesterName\" AS semester, min(\"tblBdKvvArchiv\".\"lngID\") AS minid " + 
					"FROM \"tblBdKvvArchiv\" WHERE " + 
					"(\"tblBdKvvArchiv\".\"lngSdSeminarID\" = 1) " + 
					"GROUP BY \"tblBdKvvArchiv\".\"strKvvArchivSemesterName\" ORDER BY min(\"tblBdKvvArchiv\".\"lngID\")) t1  " + 
		"inner join " + 
			"(SELECT \"tblBdKvvArchiv\".\"strKvvArchivKurstypBezeichnung\" AS kurstyp, " + 
						"\"tblBdKvvArchiv\".\"strKvvArchivSemesterName\" AS semester, " + 
						"COUNT( \"lngKvvArchivKursID\" ) as courses, " +
						"SUM( \"intKvvArchivKursTeilnehmer\" ) AS sum_real ," +
						"SUM (CASE WHEN \"intKvvArchivKursTeilnehmer\"=0 THEN 1 ELSE 0 END) as not_entered," +
						"CASE WHEN (SUM (CASE WHEN \"intKvvArchivKursTeilnehmer\"=0 THEN 0 ELSE 1 END))=0 THEN 0 ELSE (SUM( \"intKvvArchivKursTeilnehmer\" )::float/(SUM (CASE WHEN \"intKvvArchivKursTeilnehmer\"=0 THEN 0 ELSE 1 END)::float))::float *COUNT( \"lngKvvArchivKursID\" ) END/COUNT( \"lngKvvArchivKursID\" ) as avg_extrapolated, " +
						"CASE WHEN (sum(CASE WHEN (\"tblBdKvvArchiv\".\"intKvvArchivKursTeilnehmer\" = 0) THEN 0 ELSE 1 END) = 0) THEN (0)::double precision ELSE (((sum(\"tblBdKvvArchiv\".\"intKvvArchivKursTeilnehmer\"))::double precision / (sum(CASE WHEN (\"tblBdKvvArchiv\".\"intKvvArchivKursTeilnehmer\" = 0) THEN 0 ELSE 1 END))::double precision) * (count(\"tblBdKvvArchiv\".\"lngKvvArchivKursID\"))::double precision) END AS extrapolated " + 
					"FROM \"tblBdKvvArchiv\" " + 
					"GROUP BY \"tblBdKvvArchiv\".\"lngSdSeminarID\", \"tblBdKvvArchiv\".\"strKvvArchivSemesterName\", \"tblBdKvvArchiv\".\"strKvvArchivKurstypBezeichnung\" HAVING (\"tblBdKvvArchiv\".\"lngSdSeminarID\" = 1)) t2 " + 
		"on (t1.semester=t2.semester) union " + 
		"select semester, minid, kurstyp, -1, 0, 0, 0,0 from (SELECT b1.\"strKvvArchivSemesterName\" AS semester, b1.minid, b2.\"strKvvArchivKurstypBezeichnung\" AS kurstyp FROM (SELECT \"tblBdKvvArchiv\".\"strKvvArchivSemesterName\", min(\"tblBdKvvArchiv\".\"lngID\") AS minid FROM \"tblBdKvvArchiv\" WHERE (\"tblBdKvvArchiv\".\"lngSdSeminarID\" = 1) GROUP BY \"tblBdKvvArchiv\".\"lngSdSeminarID\", \"tblBdKvvArchiv\".\"strKvvArchivSemesterName\") b1, (SELECT \"tblBdKvvArchiv\".\"strKvvArchivKurstypBezeichnung\" FROM \"tblBdKvvArchiv\" WHERE (\"tblBdKvvArchiv\".\"lngSdSeminarID\" = 1) GROUP BY \"tblBdKvvArchiv\".\"lngSdSeminarID\", \"tblBdKvvArchiv\".\"strKvvArchivKurstypBezeichnung\") b2 WHERE (NOT (EXISTS (SELECT \"tblBdKvvArchiv\".\"lngSdFakultaetID\" FROM \"tblBdKvvArchiv\" WHERE (((\"tblBdKvvArchiv\".\"lngSdSeminarID\" = 1) AND ((\"tblBdKvvArchiv\".\"strKvvArchivSemesterName\")::text = (b1.\"strKvvArchivSemesterName\")::text)) AND ((\"tblBdKvvArchiv\".\"strKvvArchivKurstypBezeichnung\")::text = (b2.\"strKvvArchivKurstypBezeichnung\")::text)))))) z77 " + 
		"order by kurstyp, minid;");



String sClass="";
int ii=0;
int iSem=0;
%>