<%@page contentType="text/html" pageEncoding="UTF-8"%><jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" />
<%String s=(String)request.getRequestURL().toString();%>
<div class="row-fluid"> 
  <div class="span2"> 
    <div class="well sidebar-nav"> 
      <ul class="nav nav-list"> 
        <li class="nav-header">Aktionen</li> 
        <li <%=(s.endsWith("daten.jsp") ? "class=\"active\"" : "")%>><a href="./daten.jsp"><i class="icon-edit"></i>Daten</a></li> 
<%--        <li <%=(s.endsWith("benotung.jsp") ? "class=\"active\"" : "")%>><a href="./benotung.jsp"><i class="icon-plus"></i>Benotung</a></li> --%>
        <li <%=(s.endsWith("l-index.jsp") ? "class=\"active\"" : "")%>><a href="./l-index.jsp"><i class="icon-user"></i>Studierende</a></li>
        <li <%=(s.endsWith("antraege.jsp") ? "class=\"active\"" : "")%>><a href="./antraege.jsp"><i class="icon-pencil"></i>Anträge</a></li>
      </ul> 
    </div><!--/.well --> 
  </div><!--/span--> 

        