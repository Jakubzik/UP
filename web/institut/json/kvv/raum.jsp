<%@ page contentType="text/json" pageEncoding="UTF-8"
	import="de.shj.UP.logic.RaumData,java.text.SimpleDateFormat, java.sql.ResultSet, java.sql.Time, de.shj.UP.data.Dozent,de.shj.UP.data.shjCore"
	session="true" isThreadSafe="false"%>
<%@page import="java.util.Locale"%><jsp:useBean id="user"
	scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean
	id="seminar" scope="session" class="de.shj.UP.logic.SeminarData" /><jsp:useBean
	id="sd" scope="session" class="de.shj.UP.util.SessionData" />
<%!
String string2JSON(String s){
	return s.replace('\n', ' ').replace('\r', ' ').replace('\t', ' ').replace('\"', '\''); 
}
%>
<%
    RaumData roomplan=new RaumData("");
    java.text.DateFormat df = new SimpleDateFormat("hh.mm");
    
    // @TODO: Parameters
    boolean bPlanung=false;
    ResultSet resultSet=roomplan.getFreeRooms(seminar.getSeminarID(), 
            request.getParameter("tag"), 
            new java.sql.Time(df.parse(request.getParameter("start")).getTime()), 
            new java.sql.Time(df.parse(request.getParameter("stop")).getTime()), 
            bPlanung, "ADD", "");
    String sRaumArray="";
    while(resultSet.next()){
        sRaumArray+=",\"" + resultSet.getString("strRaumBezeichnung") + "\"";
    }
    if(sRaumArray.equals("")) sRaumArray=",";
   
%>[<%=sRaumArray.substring(1)%>]

