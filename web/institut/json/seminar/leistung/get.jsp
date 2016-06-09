<%--
    @Neu Juni 2014, hj
    
    JSP-Object "seminar/leistung/get"

    Leistungen des Seminars (Stammdaten)

    @TODO
    ===========================

    SYNOPSIS (German)
    ===========================   
    Üblicher Lifecycle: GET

    
    Expected SESSION PROPERTIES
    ===========================
    seminar        muss initialisiert sein (AccessLevel egal),

    Expected PARAMETER(S):
    ===========================
    --

    Returns
    =======
    Array of objects with remarks:
    "leistung":{
        "name":<Bezeichnung der Leistung,                [Text]>,
        "name_en":<Englische Bezeichnung                 [Text]>,
        "id":<Primärschlüssel                            [long]>,
        "cp":<Credit Pts. default                        [float]>,
        "supra_code":<Für Datenaustausch>                [Text]>,
    }

    Sample Usage
    ============

    
--%><%@page contentType="text/json" pageEncoding="UTF-8" import="java.sql.ResultSet,java.text.SimpleDateFormat, de.shj.UP.data.Dozent,de.shj.UP.data.shjCore" session="true" isThreadSafe="false"  errorPage="../../error.jsp" %>
<jsp:useBean id="seminar" scope="session" class="de.shj.UP.logic.SeminarData" />
<%@include file="../../../fragments/checkVersion.jsp" %>
{"leistungen":[
<%long lERR_BASE=213000 + 100;    // Kurstyp + Get
	boolean bFirstLoop=true;
        ResultSet rLeistung=seminar.sqlQuery("Select * from \"tblSdLeistung\" " +
            "where \"lngSdSeminarID\"=" +  seminar.getSeminarID() + ";");
	while(rLeistung.next()){
		if(!bFirstLoop) out.write(",");
		else bFirstLoop=false;
		out.write("{\"leistung\":{" + 
                        "\"name\":\"" + rLeistung.getString("strLeistungBezeichnung") + "\"," + 
                        "\"name_en\":\"" + rLeistung.getString("strLeistungBezeichnung_en") + "\"," + 
                        "\"id\":\"" + rLeistung.getString("lngLeistungID") + "\"," +
                        "\"cp\":\"" + rLeistung.getFloat("sngLeistungCreditPts") + "\"," +
                        "\"code\":\"" + shjCore.normalize(rLeistung.getString("strLeistungCustom2")) + "\"" +
                "}}");
	}
	rLeistung.close();
%>]}
