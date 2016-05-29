<%@ page language="java" pageEncoding="UTF8"%><%if(request.getParameter("matrikelnummer")==null) throw new Exception("{\"error\":\"Es gibt Verständigungsschwierigkeiten zwischen Anwendung und Server; bitte melden Sie sich beim Anbieter Ihrer App.\",\"errorDebug\":\"Der Pflichtparameter >matrikelnummer< wurde nicht übergeben.\",\"errorcode\":6,\"severity\":100}");
try{
    Long.parseLong(request.getParameter("matrikelnummer"));
}catch(Exception eNotNumeric){throw new Exception("{\"error\":\"Es gibt Verständigungsschwierigkeiten zwischen Anwendung und Server; bitte melden Sie sich beim Anbieter Ihrer App.\",\"errorDebug\":\"Es sollen Bemerkungen geliefert werden, aber die Matrikelnummer >" + request.getParameter("matrikelnummer") + "< ist nicht numerisch!\",\"errorcode\":7,\"severity\":100}");}
if(student.getMatrikelnummer()==null){    student.init(user.getSdSeminarID(), request.getParameter("matrikelnummer"));}
if(!student.getMatrikelnummer().equals(request.getParameter("matrikelnummer"))){
    // Hm, wir machen's mal so:
    student.init(user.getSdSeminarID(),request.getParameter("matrikelnummer"));
    // throw new Exception("{\"error\":\"Es ist ein unvorhergesehender Zustand eingetreten. Bitte Versuchen Sie es erneut. Sollte das Problem fotbestehen, melden Sie es beim Anbieter Ihrer App.\",\"errorDebug\":\"Unklar, ob Studierendem/r >" +student.getMatrikelnummer() + "< oder >" + request.getParameter("matrikelnummer") + "< gemeint ist\",\"errorcode\":8,\"severity\":90}");
}%>
