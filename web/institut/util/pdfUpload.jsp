<%@page import="de.shj.UP.data.shjCore"%>
<%@ page session="true" %>
<jsp:useBean id="sd" scope="session" class="de.shj.UP.util.SessionData"  />
<jsp:useBean id="user" class="de.shj.UP.data.Dozent" scope="session" />
<%@include file="../fragments/checkLogin.jsp" %>
<%@include file="../fragments/checkAccess1.jsp" %>
<%	
	String strErr="";
        String sFileName="";
        String sRedirect="../daten.jsp";
	if(request.getContentLength()>0){
		// ------------------------------------------------------------------------
		// Make Uploaded Data accessible
		// ------------------------------------------------------------------------
		de.shj.UP.data.shjUpload server=new de.shj.UP.data.shjUpload();
                sFileName="d" + user.getDozentID() + "_" + System.currentTimeMillis() + ".pdf";

		server.setDestinationPath(application.getRealPath("/studentPhoto/") + "/" + sFileName);		

		server.setContentTypeWhiteList("application/pdf", "PDF");
                server.setContentTypeWhiteList("", "PDF");
//		server.setContentTypeWhiteList("image/jpg", "JPeg Image");
//		server.setContentTypeWhiteList("image/pjpeg", "JPeg Image");		
		//set path for saving:
                System.out.println("Dateiname1:" + server.getParameter("pdf"));
		server.setDestinationPath(application.getRealPath("/studentPhoto") + "/" + sFileName);
		server.setSizeLimit(1000*1024);		
		server.setRebuffOnContentError(false);
		if(server.upload(request)){
                    // System.out.println("Dateiname2:" + server.getParameter("pdf"));
                    // OK, dirty: add JSON
 //                   String sTmp = user.getDozentHomepageOptions();
//                    String sInsert = "{\"src\":\"" + request.getParameter("src") + "\",\"name\":\"" + shjCore.string2JSON(request.getParameter("name")) + "\"}";
//                    if(sTmp.contains("downloads")){
//                        int iStart = sTmp.indexOf("downloads");
//                        int iStop = sTmp.indexOf("]", iStart);
//                        // (1) es gibt schon Downloads
//                        if((iStop-iStart) > "downloads\":[".length() ){
//                            sTmp = sTmp.substring(0, iStop-1) + "," + sInsert + sTmp.substring(iStop);
//                        }else{
//                            // (1) Es gibt noch keine Downloads
//                            sTmp = sTmp.substring(0, iStop-1) + sInsert + sTmp.substring(iStop);
//                        }
//                    }else{
//                        sTmp = "{\"downloads\":[" + sInsert + "]," + sTmp.substring(1);
//                    }
//                    
//                    user.setDozentHomepageOptions(sTmp);
////                    user.update();
//                    response.sendRedirect(sRedirect);
                    out.write("{\"src\":\"" + sFileName + "\"}");
		}else{
		  throw new Exception(server.getErrDescription());
		}
	}
%>