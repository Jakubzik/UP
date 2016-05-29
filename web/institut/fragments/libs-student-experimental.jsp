<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="iso-8859-1"%>
 
<%

// Quellen für JS-Bibliotheken
// 
boolean isProductiveEnvironment=false;
boolean isConnected=true;
//System.out.println(request.getAttribute("javax.servlet.forward.request_uri"));
String sCallerPath=request.getRequestURI();
String sRelativity=sCallerPath.substring("/SignUp/as/Faculty/".length());

int count = sRelativity.length() - sRelativity.replace("/","").length();
String sRel="";
if(count>0){
    for(int ii=0;ii<count;ii++) sRel+="../";
}
if(isProductiveEnvironment){%>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.8.2/jquery.min.js"></script>
<script src="js/2.1.1/bootstrap.min.js"></script>
<script src="js/signup-faculty-studentview.min.js"></script>
<%}else{
    if(isConnected){ %>
     <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.8.2/jquery.min.js"></script>
     <!-- <script src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.23/jquery-ui.min.js"></script> -->
     <script src="js/3.2.0/bootstrap.min.js"></script> 
     <!--<script src="js/2.2.0/bootstrap.min.js"></script> -->
    <script src="js/signup-faculty-common.js"></script>
    <script type="text/javascript" src="js/signup-faculty-student-dom-common.js"></script>
    <script type="text/javascript"  src="js/signup-faculty-studentview.js"></script>
    <script src="js/bootstrap-fileupload.min.js"></script>
    <script src="js/tsort/tsort.min.js"> </script>
    <%}else{ %>
     <script src="js/jquery.min.js"></script>
     <script src="js/3.2.0/bootstrap.min.js"></script>
    <script src="js/signup-faculty-common.js"></script>
    <script type="text/javascript" src="js/signup-faculty-student-dom-common.js"></script>
    <script type="text/javascript"  src="js/signup-faculty-studentview.js"></script>
    <script src="js/bootstrap-fileupload.min.js"></script>
    <script src="js/tsort/tsort.min.js"> </script>
<%}
}%>
 