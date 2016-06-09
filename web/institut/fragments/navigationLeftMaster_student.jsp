
<%-- 
    Document   : navigationLeftMaster
    Created on : 12.03.2013, 09:30:43
    Author     : heiko
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%><jsp:useBean id="student" scope="session" class="de.shj.UP.logic.StudentData" /><jsp:useBean
	id="seminar" scope="session" class="de.shj.UP.logic.SeminarData" />
<%String s=(String)request.getRequestURL().toString();
boolean bIsKurswahlLink=s.endsWith("kurswahl.jsp");

// EDIT
// Hier die ID des Seminars einfürgen, 
// wenn der Link "Antrag" für Studierende 
// zur Verfügung stehen soll. Mit dem 
// Antrag können Studierende das Ausstellen 
// von Transkripten und/oder Prüfungszeugnissen
// online beantragen und den Bearbeitungsstatus
// verfolgen.
String sSeminareMitAntrag = ",1,";

// Hier die ID des Seminars einfügen, wenn 
// der Link "Transkript" zur Verfügung stehen 
// soll.
String sSeminareMitTranskriptdruck = ",1,5,";
String sSeminar = "," + seminar.getSeminarID() + ",";
%>
<%--
    ######################
    Navigationsleiste links
    ######################
<%if(seminar.Data().isSignUpShowResults()){ %><a href="<%=strModRel%>SignUp/result.jsp" class="leftmenuitems" id="leftmenuitem_kurswahl" title="<%=sd.getLanguage().getString("LeftFrame.Kurswahl")%>"><%=sd.getLanguage().getString("LeftFrame.Kurswahl")%></a><%} %>
<%if(seminar.Data().isSignUpPleaseWait()){ %><a href="<%=strModRel%>SignUp/wait.jsp" class="leftmenuitems" id="leftmenuitem_kurswahl" title="<%=sd.getLanguage().getString("LeftFrame.Kurswahl")%>"><%=sd.getLanguage().getString("LeftFrame.Kurswahl")%></a><%} %>

--%>

<div class="well sidebar-nav"> 
    <ul class="nav nav-list"> 
        <li class="nav-header">Aktionen</li>
        <li class="signUp-student-nav<%=(!bIsKurswahlLink ? " active" : "")%>"><a href="<%=(bIsKurswahlLink ? "./ls-index.jsp" : "#")  %>" id="navDaten"><i class="icon-edit"></i>Daten</a></li> 
<%if(seminar.Data().isSignUpPeriod() || seminar.Data().isSignUpShowResults()){ %>
        <li class="signUp-student-nav<%=(bIsKurswahlLink ? " active" : "")%>"><a href="./kurswahl.jsp" id="navKurswahl"><i class="icon-calendar"></i>Kurswahl</a></li>
<%}%>
        <li class="signUp-student-nav<%=(!bIsKurswahlLink ? " active" : "")%>"><a href="<%=(bIsKurswahlLink ? "./ls-index.jsp" : "#")  %>" id="navLeistungen"><i class="icon-th"></i>Leistungen</a></li>
        <li class="signUp-student-nav<%=(!bIsKurswahlLink ? " active" : "")%>"><a href="<%=(bIsKurswahlLink ? "./ls-index.jsp" : "#")  %>" id="navAnmeldungen"><i class="icon-list"></i>Anmeldungen</a></li>
        <li class="signUp-student-nav<%=(!bIsKurswahlLink ? " active" : "")%>"><a href="<%=(bIsKurswahlLink ? "./ls-index.jsp" : "#")  %>" id="navPruefungen"><i class="icon-asterisk"></i>Prüfungen</a></li>
        <li class="signUp-student-nav<%=(!bIsKurswahlLink ? " active" : "")%>"><a href="<%=(bIsKurswahlLink ? "./ls-index.jsp" : "#")  %>" id="navStudienbilanz"><i class="icon-check"></i>Studienbilanz</a></li> 
        <%if(sSeminareMitAntrag.contains(sSeminar)){%>        <li class="signUp-student-nav<%=(!bIsKurswahlLink ? " active" : "")%>"><a href="<%=(bIsKurswahlLink ? "./ls-index.jsp" : "#")  %>" id="navAntrag"><i class="icon-envelope"></i>Antrag</a></li> <%}%>
        <%if(sSeminareMitTranskriptdruck.contains(sSeminar)){%>        <li class="signUp-student-nav<%=(!bIsKurswahlLink ? " active" : "")%>"><a href="<%=(bIsKurswahlLink ? "./ls-index.jsp" : "#")  %>" id="navTranskriptdruck"><i class="icon-print"></i>Transkriptdruck</a></li> <%}%>
    </ul> 
</div><!--/.well -->