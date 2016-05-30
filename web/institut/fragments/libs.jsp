<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="iso-8859-1"%>
 
<%

// Quellen für JS-Bibliotheken
boolean isConnected=true;
//System.out.println(request.getAttribute("javax.servlet.forward.request_uri"));
String sCallerPath=request.getRequestURI();
String sRelativity=sCallerPath.substring("/UP/institut/".length());

int count = sRelativity.length() - sRelativity.replace("/","").length();
String sRel="";
if(count>0){
    for(int ii=0;ii<count;ii++) sRel+="../";
}
if(isConnected){ %>
<!-- <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.2.2/jquery.min.js"></script> -->
 <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.8.2/jquery.min.js"></script> 
 <script src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.23/jquery-ui.min.js"></script>
 <script src="<%=sRel%>js/jquery.validate.min.js"></script>
 <script src="<%=sRel%>js/jquery.metadata.js"></script>
 <script src="<%=sRel%>js/jquery.form.js"></script>
 <script src="<%=sRel%>js/2.1.1/bootstrap.min.js"></script> 
 <!--<script src="js/2.2.0/bootstrap.min.js"></script> -->
 <script src="<%=sRel%>js/jquery.contextmenu.r2.packed.js"></script>
<%}else{ %>
 <script src="<%=sRel%>js/jquery.min.js"></script>
 <script src="<%=sRel%>js/jquery-ui-1.8.13.custom.min.js"></script>
 <script src="<%=sRel%>js/jquery.validate.min.js"></script>
 <script src="<%=sRel%>js/jquery.metadata.js"></script>
 <script src="<%=sRel%>js/jquery.form.js"></script>
 <script src="<%=sRel%>js/2.1.1/bootstrap.min.js"></script>
 <script src="<%=sRel%>js/jquery.contextmenu.r2.packed.js"></script>
<%} %>
 