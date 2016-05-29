<%@ page contentType="text/json" pageEncoding="UTF-8"
	session="false" isThreadSafe="false" import="de.shj.UP.util.SendMail" errorPage="error.jsp"%>
<%
//------------------------------------------------
//initialize
//------------------------------------------------
%>
<%!
String string2JSON(String s){
	return s.replace('\n', ' ').replace('\r', ' ').replace('\t', ' ').replace('\"', '\''); 
}
%>
<%
    if(request.getParameter("matrikelnummer")==null) throw new Exception("Prob");
    if(request.getParameter("subject")==null) throw new Exception("Prob");
    if(request.getParameter("name")==null) throw new Exception("Prob");
    if(request.getParameter("msg")==null) throw new Exception("Prob");
    if(request.getParameter("sender")==null) throw new Exception("Prob");
    
    if(!request.getParameter("sender").contains("@")) throw new Exception("Prob");
    if(!request.getParameter("sender").contains(".")) throw new Exception("Prob");
    if(request.getParameter("msg").length()<5) throw new Exception("Prop");
    
    SendMail mail = new SendMail("extmail.urz.uni-heidelberg.de");
    String sText=request.getParameter("msg") + "\n\n" + "Matrikelnr: " + request.getParameter("matrikelnummer") + "\nAbsender:" + request.getParameter("name") + "<" + request.getParameter("sender") + ">";
    if(!mail.sendMail("Info", "info@shj-online.de","", "info@shj-online.de", "SignUp-Kontakt: " + request.getParameter("subject"), sText, null))
            throw new Exception("Fehler beim Versuch, die E-Mail zu versenden.");
%>
{"success":"true"}
