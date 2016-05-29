<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="iso-8859-1"%>
<%String sCallerPath=request.getRequestURI();
String sRelativity=sCallerPath.substring("/SignUpXP/as/Faculty/".length());

int count = sRelativity.length() - sRelativity.replace("/","").length();
String sRel="";
if(count>0){
    for(int ii=0;ii<count;ii++) sRel+="../";
}%>
<link href="<%=sRel%>css/3.2.0/bootstrap.min.css" rel="stylesheet"> 