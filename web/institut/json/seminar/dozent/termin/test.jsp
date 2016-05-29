<%--
    JSP-Object "seminar/dozent/termin/test"
    @Revision: Jan 22, 2014 -- hj

    @TODO
    ===========================
    

    SYNOPSIS (German)
    ===========================
    Testet das Versenden von Erinnerungs E-Mails    

    Expected SESSION PROPERTIES
    ===========================
    nutzer        muss initialisiert sein 

    Expected PARAMETER(S):
    ===========================
    --


    Returns
    =======
    Information in System.err

    Sample Usage
    ============
 
    
--%><%@page import="de.shj.UP.util.SignUpLocalConnector"%>
<%@page import="de.shj.UP.logic.Sprechstunde"%>
<%@page import="java.sql.Date"%>
<%@page import="de.shj.UP.data.shjCore"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page contentType="text/json" pageEncoding="UTF-8" import="java.sql.ResultSet,de.shj.UP.data.Dozent,de.shj.UP.util.PasswordHash" session="true" isThreadSafe="false"  errorPage="../../../error.jsp" %>
<jsp:useBean id="nutzer" scope="session" class="de.shj.UP.data.Nutzer" />
<%
    Sprechstunde mailer=new Sprechstunde();
    mailer.sendEmailReminders(new SignUpLocalConnector());
    
%>