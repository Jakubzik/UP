<%@page import="de.shj.UP.data.shjCore,java.io.File"%>
<%@ page session="true" %>
<jsp:useBean id="sd" scope="session" class="de.shj.UP.util.SessionData"  />
<jsp:useBean id="user" class="de.shj.UP.data.Dozent" scope="session" />
<jsp:useBean id="student" scope="session" class="de.shj.UP.beans.config.student.StudentBean" />
<%@include file="../fragments/checkVersion.jsp" %>
<%	
    // Es soll (von daten.jsp Anglistik) ein 
    // Upload gelöscht werden:
    // src enthält den Dateinamen,
    if(request.getParameter("command").equals("Delete Upload")){
%>        <%@include file="../fragments/checkLogin.jsp" %>
          <%@include file="../fragments/checkAccess1.jsp" %><%
        if(!request.getParameter("src").endsWith(".pdf"))
            throw new Exception("Es handelt sich nicht um eine PDF-Datei");
        
        File tmp = new File(application.getRealPath("/studentPhoto/") + "/" + request.getParameter("src"));
        if(tmp.exists()){
            if (!(tmp.delete())){
                throw new Exception("Löschen der Datei fehlgeschlagen");	
        }
                
    }
        out.println("{\"success\":\"true\"}");
    }else 
        if(request.getParameter("command").equals("Delete Image")){
%>        <%@include file="../fragments/checkLogin.jsp" %>
          <%@include file="../fragments/checkAccess1.jsp" %><%
           if(!request.getParameter("user").equals(String.valueOf(user.getDozentID())))
                 throw new Exception("Es ist unklar, welches Bild gemeint ist");
        
        File tmp = new File(application.getRealPath("/studentPhoto/") + "/dozent" + user.getDozentID() + ".jpg");
        if(tmp.exists()){
            if (!(tmp.delete())){
                throw new Exception("Löschen der Datei fehlgeschlagen");	
        }else{
                out.println("{\"success\":\"true\"}");
            }

        }
    }else 
        if(request.getParameter("command").equals("Delete Student Image")){
%>        <%@include file="../fragments/checkLoginStudent.jsp" %><%
           if(!request.getParameter("matrikelnummer").equals(String.valueOf(student.getMatrikelnummer())))
                 throw new Exception("Es ist unklar, welches Bild gemeint ist");
        
        File tmp = new File(application.getRealPath("/studentPhoto/") + "/" + student.getStudentPID() + ".jpg");
        
        // Löschversuch macht nur Sinn, 
        // wenn die Datei existiert.
        if(tmp.exists()){
            if (!(tmp.delete())){
                throw new Exception("Löschen der Datei fehlgeschlagen");	
        }else{
                out.println("{\"success\":\"true\"}");
            }

        }
    }
%>
<%--

command nimmt folgende Werte an:
    (1) Delete Upload
    (2) Delete Image
    (3) Delete Student Image

    (1) und (2) können nur von Lehrenden 
    mit AccessLevel >=1 und gültigem Login
    aufgerufen werden.

    Bei (2) ist der Parameter 
    user [=DozentID] zu übergeben, um 
    Irrtümer bei mehreren geöffneten Tabs 
    auszuschließen.

    (3) ist nur von Studierenden aufzurufen, 
    der Parameter matrikelnummer muss übergeben werden.
--%>