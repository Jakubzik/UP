<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.Date"%>
<%@ page contentType="text/json" pageEncoding="UTF-8" import="java.util.Locale,java.util.Enumeration,java.text.SimpleDateFormat, de.shj.UP.data.Dozent,de.shj.UP.data.StudentXLeistung,de.shj.UP.data.shjCore" session="true" isThreadSafe="false" %>
<jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" />
<jsp:useBean id="student" scope="session" class="de.shj.UP.beans.config.student.StudentBean" />
<%      
        long lERR_BASE=105000 + 200;    // Student + Update
        int iFachNeu=-1;

        // Soll "nur" das Flag Anmeldung zum Staatsexamen gesetzt werden?
        // Soll "nur" das Flag Anmeldung zum Staatsexamen gesetzt werden?
        // 
        if(user.getDozentAccessLevel()>500){
            if(request.getParameter("setStxGymPO")!=null){
                
                if(user.getSdSeminarID()==1 && user.getDozentID()!=363){
                    out.write("{\"success\":\"false\", \"problem\":\"falscher Benutzer\"");
                    return;
                }

                // Germanistik? Nicht Frau Engelhardt? --> Problem
                if(user.getSdSeminarID()==5 && user.getDozentID()!=160){
                    out.write("{\"success\":\"false\", \"problem\":\"falscher Benutzer\"");
                    return;
                }   
                
                boolean bAngemeldet = request.getParameter("chkStudentStx")!=null;
                if(bAngemeldet){
                    Date dtm = new Date(shjCore.g_ISO_DATE_FORMAT.parse(request.getParameter("txtStxDelete")).getTime());
                    PreparedStatement pstm = user.prepareStatement("update \"tblBdStudent\" set \"strStudentZUVFach\"=? where \"strMatrikelnummer\"=?");
                    pstm.setString(1, shjCore.g_ISO_DATE_FORMAT.format(dtm));
                    pstm.setString(2, student.getMatrikelnummer());
                    if(1!=pstm.executeUpdate()) throw new Exception("Fucked up");
                    out.write("{\"success\":\"true\", \"state\":\"checked\", \"delete_date\":\"" + request.getParameter("txtStxDelete") + "\"}");
                }else{
                    if(1 != student.sqlExeCount("update \"tblBdStudent\" set \"strStudentZUVFach\"=null " + 
                        " where \"strMatrikelnummer\"='" + student.getMatrikelnummer() + "';"))
                        throw new Exception("Ooops, sorry. Das ging schief!");
                    out.write("{\"success\":\"true\", \"state\":\"\", \"delete_date\":\"\"}");
                }
                return;
            }
        }
        
        try{
            iFachNeu = Integer.parseInt(request.getParameter("fach_id"));
        }catch(Exception eNoParam){
            throw new Exception("{\"error\":\"Das Fach konnte nicht geändert werden.\",\"errorDebug\":\"Das neue Fach konnte nicht am Parameter fach_id (" + request.getParameter("fach_id") + ") ausgelesen werden.\",\"errorcode\":" + lERR_BASE + 9 + ",\"severity\":100}");            
        }
	
                // Ist das ein Fachwechsel (von Config, Berechtigung erforderlich)? ...
if(iFachNeu != student.Fach().getFachID()){
%>
<%@include file="../../fragments/checkVersion.jsp" %>
<%@include file="../../fragments/checkLogin.jsp" %>
<%@include file="../../fragments/checkAccess1.jsp" %>
<%@include file="../../fragments/checkInitStudent.jsp" %>
<%
        
	// ist das Fach1, Fach2, ..., Fach5, Fach6?
	String sFachCount="";
	if(student.Fach().getFachID()==student.getStudentFach1()) sFachCount="1";
	else if(student.Fach().getFachID()==student.getStudentFach2()) sFachCount="2";
	else if(student.Fach().getFachID()==student.getStudentFach3()) sFachCount="3";
	else if(student.Fach().getFachID()==student.getStudentFach4()) sFachCount="4";
	else if(student.Fach().getFachID()==student.getStudentFach5()) sFachCount="5";
	else if(student.Fach().getFachID()==student.getStudentFach6()) sFachCount="6";
	
	// (1) Update String erzeugen,
	// (2) Update durchführen,
	// (3) Update in Postprocessing notieren
	// (4) Bemerkung anlegen
	
	// (1)
	String sSQL = "update \"tblBdStudent\" set \"intStudentFach" + sFachCount + "\"=" + iFachNeu + " " + 
					"where \"strMatrikelnummer\"='" + student.getMatrikelnummer() + "';";
	
	// (2)
	if(1!=user.sqlExeCount(sSQL)){
            throw new Exception("{\"error\":\"Die Fachänderung konnte nicht gespeichert werden.\",\"errorDebug\":\"Die Datenbank wollte nicht, vielleicht gibt's Hinweise in den Logs?\",\"errorcode\":" + lERR_BASE + 1 + ",\"severity\":100}");            
        }
	
	// (3)
	user.sqlExe("delete from \"tblPostprocessing\" where \"strMatrikelnummer\"='" + student.getMatrikelnummer() + "';");
	user.sqlExe("insert into \"tblPostprocessing\" (\"strMatrikelnummer\", \"strSQL\", \"strUser\", \"dtmDatum\", \"strKommentar\") values (" +
					"'" + student.getMatrikelnummer() + 
					"', '" + user.getDBCleanString(sSQL) +
					"', '" + (user.getDozentVorname() + " " + user.getDozentNachname()).replaceAll("'", "\'") + 
					"', CURRENT_DATE, 'Fachwechsel von " + student.Fach().getFachBeschreibung()  + "');");
	
        // (4) Versuche, die Module neu zu Mappen und
        //      auch die Leistungspunkte entsprechend des
        //      neuen Fachs zu setzen. Dabei ist insbesondere
        //      das Flag tblSdModul.blnModulVarLP von Bedeutung:
        //      nur in Modulen, in denen blnModulVarLP==true
        //      gilt, werden die Leistungspunkte nicht um-
        //      gestellt.
        //      Außerdem nicht betroffen von Veränderungen der 
        //      Leistungspunktezahl sind Leistungen, die mit 0 LP
        //      gespeichert sind (in StudentXLeistung), oder die
        //      mit 0 LP diesem Modul zugeordnet sind (in ModulXLeistung).
        try{
            user.execProc("mapmodules", "long", String.valueOf(user.getSdSeminarID()), "String", student.getMatrikelnummer(), "int", String.valueOf(iFachNeu));
        }catch(Exception eIgnoreExperimental){}

    }else{
        // Oder der Änderungswunsche eines/r Studierendden?
%><%@include file="../../fragments/checkVersion.jsp" %>
<%@include file="../../fragments/checkLoginStudent.jsp" %><%
    student.setStudentEmail(request.getParameter("email"));
    student.setStudentHandy(request.getParameter("mobil"));
    System.out.println(request.getParameter("info_email")  + " ... " + (request.getParameter("info_email")!=null && request.getParameter("info_email").equals("checked")));
    
    boolean bPublishEmail=(request.getParameter("info_email")!=null && request.getParameter("info_email").equals("checked"));
    student.setStudentPublishEmail(bPublishEmail);
    
    student.update();

}




	
%>{"success":"yes"}