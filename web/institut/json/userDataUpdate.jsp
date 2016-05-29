<%@ page contentType="text/json" pageEncoding="UTF-8" import="de.shj.UP.logic.AktuellesIterator,java.text.SimpleDateFormat, de.shj.UP.data.Dozent,de.shj.UP.data.shjCore" session="true" isThreadSafe="false"%>
<%@page import="java.util.Locale"%><jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" />
<%
// mAktuellesPut.jsp 
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
	String sErr="";
        if(user.getDozentNachname()==null) throw new Exception("Sorry -- Login abgelaufen");
        
        if(!request.getParameter("passwort").equals("*****")){
            // Passwort soll gesetzt werden
            if(!request.getParameter("passwort").equals(request.getParameter("passwort_wdh")))
                sErr="Passwort und Wdh. sind nicht identisch.";
            else
                user.setDozentPasswort(request.getParameter("passwort"));
        }
        if(sErr.equals("")){
            //Passwort soll _nicht_ geändert werden
            user.setDozentTitel(request.getParameter("user_titel"));
            user.setDozentEmail(request.getParameter("user_email"));
            if(!user.update()) sErr += "Update fehlgeschlagen, keine Ursache bekannt.";
        }%>
{"success":"<%=sErr.equals("") ? "true" : "false"%>","details":"<%=sErr.equals("") ? user.getDozentAccessLevel() : sErr%>"}
