<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" />
<jsp:useBean id="student" scope="session" class="de.shj.UP.logic.StudentData" />
<jsp:useBean	id="sd" scope="session" class="de.shj.UP.util.SessionData" />
<%if(student.getStudentNachname()==null && user.getDozentNachname()==null){%><jsp:include page="../checkAccess.jsp" flush="true"></jsp:include><%}%>
<%
	String sPage="ls-index.jsp";
	if(user.getDozentNachname()==null || user.getDozentAccessLevel()<500){ sPage="../index-studierende.jsp";
            user=null;student=null;
            session.invalidate();
        }else{
            if(!student.getMatrikelnummer().equals(request.getParameter("matrikelnummer"))){
                sPage="index-studierende.jsp";
                user=null;student=null;
                session.invalidate();
            }else{
                sd.setSessionType("student");
                sPage="../ls-index.jsp";
            }
        }
  
response.sendRedirect(sPage);             
%>