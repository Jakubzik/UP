<%--
    JSP-Object "seminar/note/get"

    SYNOPSIS (German)
    ===========================
    2013, Mar 5, shj    erzeugt. 
    2013, Dez 28, shj   überarbeitet
    
    Üblicher Lifecycle: GET

    Die zur Benotung von Leistungen konfigurierten  
    Noten (tblSdNote).
    
    Expected SESSION PROPERTIES
    ===========================
    user        muss initialisiert sein (AccessLevel egal) ODER
    student     muss initialisiert sein.

    Expected PARAMETER(S):
    ===========================
    
    Returns
    =======
    {
        "noten": [
            {
                "note": {
                    "name": "Unbenotet (bestanden)",
                    "bestanden": "true",
                    "wert": "0.0",
                    "id": "13"
                }
            },
            {
                "note": {
                    "name": "Anerkannt (bestanden)",
                    "bestanden": "true",
                    "wert": "0.0",
                    "id": "14"
                }
            },
        {
            "note": {
                "name": "Erlassen (bestanden)",
                "bestanden": "true",
                "wert": "0.0",
                "id": "15"
            }
        },
        {
            "note": {
                "name": "Sehr gut",
                "bestanden": "true",
                "wert": "1.0",
                "id": "1"
            }
        },
        {
            "note": {
                "name": "Noch sehr gut",
                "bestanden": "true",
                "wert": "1.3",
                "id": "2"
            }
        }
        ...
        ...
        ]
    }

    Sample Usage
    ============

    
--%><%@page contentType="text/json" pageEncoding="UTF-8" import="java.sql.ResultSet,de.shj.UP.data.Dozent,de.shj.UP.data.shjCore" session="true" isThreadSafe="false"  errorPage="../../error.jsp" %>
<jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean id="seminar" scope="session" class="de.shj.UP.HTML.HtmlSeminar" />
<jsp:useBean id="sd" scope="session" class="de.shj.UP.util.SessionData" /><jsp:useBean id="student" scope="session" class="de.shj.UP.beans.config.student.StudentBean" />
<%@include file="../../../fragments/checkVersion.jsp" %>
<%long lERR_BASE=204000 + 100;    // Note + Get
    if(sd.getSessionType().equals("student")){%>
<%@include file="../../../fragments/checkLoginStudent.jsp" %>    
<%}else{%>
<%@include file="../../../fragments/checkLogin.jsp" %>
<%}%>
{"noten":[
<%
		boolean bFirstLoop=true;
	ResultSet rNoten=seminar.sqlQuery("select \"strNoteNameDE\", \"blnNoteBestanden\", \"sngNoteECTSCalc\", \"intNoteID\" from \"tblSdNote\" where \"lngSdSeminarID\"=" + user.getSdSeminarID() + 
			" order by \"sngNoteECTSCalc\";");
	
	while(rNoten.next()){
		if(!bFirstLoop) out.write(",");
		else bFirstLoop=false;
		
		out.write("{\"note\":{" + 
                        "\"name\":\"" + rNoten.getString("strNoteNameDE") + "\"," + 
                        "\"bestanden\":\"" + (rNoten.getBoolean("blnNoteBestanden") ? "true" : "false") + "\"," + 
                        "\"wert\":\"" + rNoten.getFloat("sngNoteECTSCalc") + "\"," +
                        "\"id\":\"" + rNoten.getString("intNoteID") + "\"" +
                "}}");
	}
	rNoten.close();
%>]}