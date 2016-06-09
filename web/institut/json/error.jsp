<%@page import="java.io.PrintWriter"%>
<%@page import="java.io.StringWriter"%>
<%@ page language="java" pageEncoding="UTF8" import="java.util.Enumeration"  contentType="text/json" isErrorPage="true" %><jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean id="seminar" scope="session" class="de.shj.UP.logic.SeminarData" /><%!String gatherInfo(HttpServletRequest request){
  String strReturn = "Cookies:\n========\n";
  
  Cookie[] cookies = request.getCookies();
  int	   anzahl  = cookies.length;
  
  for(int ii=0; ii< anzahl; ii++){
    strReturn += "  .. " + cookies[ii].getName() + " = " + cookies[ii].getValue() + "\n";
  }
  
  strReturn += "\n\nParameters:\n========\n";
  java.util.Enumeration pn = request.getParameterNames();
  for(;pn.hasMoreElements();){
    String parName = (String) pn.nextElement();
    strReturn += "  .. " + parName + " = " + request.getParameter(parName) + "\n";
  }
  
  strReturn += "\n\nUser:\n========\n";
  strReturn += "IP: " + request.getRemoteHost() + "\n";
  strReturn += "Agent: " + request.getHeader("User-Agent") + "\n";
  
  return strReturn;
}%><%!
String getStacktrace(Throwable ex){
    String sReturn="Stacktrace nicht greifbar.";
    try{
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        sReturn = sw.toString();
    }catch(Exception eCannnot){} // ignore
    return sReturn;
}
String string2JSON(String s){
	if(s==null) return "";
	return s.replace('\n', ' ').replace('\r', ' ').replace('\t', ' ').replace('\"', '\''); 
}%><%
	String sOutput="";
	if(exception.getMessage()!=null && exception.getMessage().trim().startsWith("{")){
		//System.out.println(exception.getMessage());
		sOutput=exception.getMessage();
		//throw new Exception(exception.getMessage());
		//return;
	}else{
		sOutput="{\"error\":\"" + string2JSON(exception.getMessage()) + "\",\"errorcode\":0}";
		System.err.println("Error occurred: " + (String)request.getAttribute("javax.servlet.forward.request_uri") + "\n\n" + gatherInfo(request));
	}
        System.out.println("I am sending this error: " + sOutput);
        if(getStacktrace(exception).startsWith("java.lang.ClassCastException: de.shj.UP.logic.StudentData")){
            System.out.println("INVALIDATING SESSION... CASTING ERR");
            session.invalidate();
        }
        application.log("SignUp-Faculty:Error\n" + getStacktrace(exception) + "\n -> from (json/error.jsp)\n\n");
        out.write(sOutput);
        //response.sendError(500, sOutput);
        //response.flushBuffer();
%>
