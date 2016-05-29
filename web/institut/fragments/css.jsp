<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="iso-8859-1"%>
<%String sCallerPath=request.getRequestURI();
String sRelativity=sCallerPath.substring("/UP/institut/".length());

int count = sRelativity.length() - sRelativity.replace("/","").length();
String sRel="";
if(count>0){
    for(int ii=0;ii<count;ii++) sRel+="../";
}%>
<link href="<%=sRel%>css/2.1.1/bootstrap.min.css" rel="stylesheet"> 