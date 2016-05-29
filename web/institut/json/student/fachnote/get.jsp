<%--
    JSP-Object "student/fachnote/get"

    @TODO
    ===========================

    SYNOPSIS (German)
    ===========================
    2013, May 19, 2013    erzeugt. 
    2013, Dez 30, 2013    überarbeitet
    2016, Mar 27          ModuleJSON hinzugefügt
                          (Achtung, hängt von Fachnote.java update ab).
    
    TODO
    
    Expected SESSION PROPERTIES
    ===========================
    (TODO, siehe oben)

    Expected PARAMETER(S):
    ===========================
    matrikelnummer  [long]
    (TODO (siehe oben))

    Returns
    =======



    Sample Usage
    ============
    --
    
--%>
<%@page contentType="text/json" pageEncoding="UTF-8" import="de.shj.UP.transcript.Fachnote" session="true" isThreadSafe="false"  errorPage="../../error.jsp" %>
<jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean id="seminar" scope="session" class="de.shj.UP.HTML.HtmlSeminar" />
<jsp:useBean id="student" scope="session" class="de.shj.UP.beans.config.student.StudentBean" />
<%@include file="../../../fragments/checkVersion.jsp" %>
<%@include file="../../../fragments/checkLogin.jsp" %>
<%@include file="../../../fragments/checkInitStudent.jsp" %>
<%@include file="../../../fragments/checkAccess500.jsp" %>
<%@include file="../../../fragments/conf_llpa.jsp" %>
{"fachnoten":[<%
    long lERR_BASE=209000 + 100;    // Fachnote + Get
    Fachnote note=new Fachnote(student.getMatrikelnummer(), user.getSdSeminarID(), student.Fach().getFachID());
    String sFachnote = "";
    
    if(note.isComplete()){
        if(g_sFaecherMitZweiNachstellenFachnote.indexOf(";" + student.Fach().getFachID() + ";")>=0){
            sFachnote=String.valueOf(note.getGradeUnrounded()).substring(0, String.valueOf(note.getGradeUnrounded()).indexOf('.')+3).replace(".", ",");
        }else{
            sFachnote=note.getGrade().replace('.',',');
        }
    }else{
        if(g_sFaecherMitZweiNachstellenFachnote.indexOf(";" + student.Fach().getFachID() + ";")>=0){
            try{
                sFachnote=String.valueOf(note.getPreliminaryGradeUnrounded()).substring(0, String.valueOf(note.getPreliminaryGradeUnrounded()).indexOf('.')+3).replace(".", ",");
            }catch(Exception e){sFachnote="--";}
        }else{
            sFachnote=note.getPreliminaryGrade().replace('.',',');
        }
    }

    // System.out.println(note.getModuleJSON());
%>{"fachnote":{"module":<%=note.getModuleJSON()%>,"komplett":"<%=note.isComplete() %>", "notenwert":"<%=sFachnote %>","bezeichnung":"<%=note.getGradeName() %>","info":"<ul><%=note.explain().trim().replaceAll("\n", "<li>") %></ul>","warnung":"<%=note.getWarning().trim()%>"}}
]}