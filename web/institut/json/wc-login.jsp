<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="de.shj.UP.util.URZ_RADIUSAuthenticationRequest"%>
<%@ page contentType="text/json" pageEncoding="UTF-8"
	import="de.shj.UP.data.Nutzer"
	session="true" isThreadSafe="false" errorPage="error.jsp"%>
<%@page import="java.util.Locale"%><jsp:useBean id="nutzer"
	scope="session" class="de.shj.UP.data.Nutzer" />
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
<%      boolean b_Bypass_Login=true; //<- ACHTUNG: PRODUKTIV MUSS HIER false STEHEN!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	String sErr="Fehler";
	long lSEMINAR_ID=1;
	// to do
	// Login
        sErr="";
        boolean bReturn = false;
        if(!b_Bypass_Login)
            bReturn = new URZ_RADIUSAuthenticationRequest(request.getParameter("UniID"), request.getParameter("Pwd")).authenticate();
        else
            bReturn=true;
        nutzer.init(request.getParameter("UniID"));
%>
{"success":"<%=bReturn ? "true" : "false"%>","isKnown":<%=nutzer.isKnown()%>,"details":"<%=sErr%>"<%if(nutzer.isKnown()){%>,
"nutzer":{"institut":"<%=nutzer.getNutzerInstitut() %>","titel":"<%=nutzer.getNutzerTitel() %>","vorname":"<%=nutzer.getNutzerVorname() %>","name":"<%=nutzer.getNutzerNachname() %>","email":"<%=nutzer.getNutzerEmail() %>","telefon":"<%=nutzer.getNutzerTelefon() %>","level":"<%=nutzer.getNutzerLevel()%>"}<%}%>}
