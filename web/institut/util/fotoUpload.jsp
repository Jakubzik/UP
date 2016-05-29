<%@ page session="true" %>
<jsp:useBean id="sd" scope="session" class="de.shj.UP.util.SessionData"  />
<jsp:useBean id="student" class="de.shj.UP.logic.StudentData" scope="session" />
<jsp:useBean id="user" class="de.shj.UP.data.Dozent" scope="session" />
<%	
	String strErr="";
        String sFotoName="";
        String sRedirect="../ls-index.jsp";
	if(request.getContentLength()>0){
		// ------------------------------------------------------------------------
		// Make Uploaded Data accessible
		// ------------------------------------------------------------------------
		de.shj.UP.data.shjUpload server=new de.shj.UP.data.shjUpload();
                if(request.getHeader("referer").endsWith("daten.jsp")){
                    // Es handelt sich um ein Lehrendeninfo: Checke das 
                    // Login
                  %><%@include file="../fragments/checkLogin.jsp" %>
                    <%@include file="../fragments/checkAccess1.jsp" %><%
                    sFotoName = "dozent" + user.getDozentID();
                    sRedirect = "../daten.jsp";
                }else{
                    sFotoName=String.valueOf(student.getStudentPID());
                }
		server.setDestinationPath(application.getRealPath("/studentPhoto/") + "/" + student.getStudentPID() + ".jpg");		

		server.setContentTypeWhiteList("image/jpeg", "JPeg Image");
		server.setContentTypeWhiteList("image/jpg", "JPeg Image");
		server.setContentTypeWhiteList("image/pjpeg", "JPeg Image");		
		//set path for saving:
		server.setDestinationPath(application.getRealPath("/studentPhoto") + "/" + sFotoName + ".jpg");
		server.setSizeLimit(600*1024);		
		server.setRebuffOnContentError(true);
		if(server.upload(request)){
                    // response.sendRedirect(sRedirect);
                    out.write("{\"success\":\"true\"}");
		}else{
		  throw new Exception(server.getErrDescription());
		}
	}
%>