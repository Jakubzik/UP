<%-- 
    Document   : gpa-modulnoten
    Created on : 12.02.2015, 20:22:47
    Author     : heiko

    Exportiert bei übergebenen Matrikelnummern alle 
    Modulnoten:
    Matrikelnr | Modulbez | Modulbez_en | LP | Datum | Gewichtung | Note
    1488258      "Einf"     "Intro"       0     ""      1         | ""          // Ein nicht vollst. absolviertes Modul
    1488258    | "Fort"   | "Advanced"  | 12 | 1.3.15 | 1         | "2.67766"

    EINSTWEILEN EINGESTELLT:
    Plan ist, lieber Fachnote so zu erweitern, dass 
    sie das CSV liefern kann.
--%>
<%@page import="de.shj.UP.util.ResultSetSHJ"%>
<%@ page pageEncoding="iso-8859-1" contentType="application/vnd.ms-word" errorPage="../json/error.jsp" session="true" import="de.shj.UP.beans.student.ExamIterator,de.shj.UP.transcript.*,de.shj.UP.data.Pruefung,de.shj.UP.transcript.Fachnote,de.shj.UP.data.shjCore,de.shj.UP.HTML.HtmlDate,java.sql.ResultSet" %><jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean id="student" scope="session" class="de.shj.UP.beans.config.student.StudentBean" />{\rtf1\ansi<%@include file="../fragments/checkLogin.jsp" %>
<%@include file="../fragments/checkAccess500.jsp" %>

<%
    // todo: Anglistik und Germanistik mixen.
    //
    // Im Gegensatz zu Transript.rtf und Fachnote müssen 
    // immer alle Module ausgegeben werden, also auch die, 
    // zu denen noch keinerlei Leistungen erbracht wurden.
    
    // Struktur
    // Loop: alle Module des Studiengangs von Matrikelnr. X
    //      produziere CSV
    //

    String sMatrikelnummer="2927658"; // Bsp. Emmerich, BA 75% Anglistik
    ResultSetSHJ rModule = user.sqlQuerySHJ("")
    %>