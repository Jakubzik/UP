<%--
    @Revision Nov 2013, hj
    
    JSP-Object "seminar/kurstyp/get"

    Kurstypen für die Kurswahl mit Anzahl der zu 
    wählenden Parallelkurse sowie der Anzahl der 
    vorhandenen Kurse.

    @TODO
    ===========================

    SYNOPSIS (German)
    ===========================
    2013, Jun 7, shj
    2014, Mar 21, hj: "tausch":'true'|'false' ergänzt (da beim Tausch sonst u.U. 
                      die falschen Kurstypen angezeigt wurden, wenn nämlich die 
                      Kurswahl zu anderen Typen als Proseminaren schon begonnen 
                      hatte).
    
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
    "kurstyp":{
        "name":<Bezeichnung des Kurstyps,               [Text]>,
        "kurswahl":<Nimmt an der Kurswahl teil?         [Bool]>,
        "leistung_id":<Zugeordnete Leistung             [long]>,
        "kurswahl_anzahl":<Wie viele Parallelkurse 
                           müssen gewählt werden?       [int]>,
        "kurse":<Id's der zugeordneten Kurse im 
                aktuellen Semester>                     [Array (int)]>,
        "tauschoption":<"true", falls dieser Kurstyp 
                einen Tausch erlaubt, vgl. 
                fragmente/conf_kurswahl_tausch.jsp      [Bool]>,
        "id":<Zus. mit SeminarID Primärschlüssel        [long]>
    }

    Sample Usage
    ============

    
--%><%@page contentType="text/json" pageEncoding="UTF-8" import="java.sql.ResultSet,de.shj.UP.logic.AktuellesIterator,java.text.SimpleDateFormat, de.shj.UP.data.Dozent,de.shj.UP.data.shjCore" session="true" isThreadSafe="false"  errorPage="../../error.jsp" %>
<jsp:useBean id="seminar" scope="session" class="de.shj.UP.logic.SeminarData" />
<jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" />
<jsp:useBean id="student" scope="session" class="de.shj.UP.beans.config.student.StudentBean" />
<jsp:useBean id="sd" scope="session" class="de.shj.UP.util.SessionData" />
<%@include file="../../../fragments/checkVersion.jsp" %>
<%@include file="../../../fragments/conf_kurswahl_tausch.jsp" %>
<%if(sd.getSessionType().equals("student")){%>
<%@include file="../../../fragments/checkLoginStudent.jsp" %> 
<%}else{%>
<%@include file="../../../fragments/checkLogin.jsp" %> 
<%}%>{"kurstypen":[
<%long lERR_BASE=205000 + 100;    // Kurstyp + Get
	String sReturn="";
	boolean bFirstLoop=true;
	String sKurstypID="";
        ResultSet rKurstyp=seminar.sqlQuery("select * from (Select t.\"lngKurstypLeistungsID\", t.\"lngKurstypID\",t.\"strKurstypBezeichnung\", t.\"intKurstypSequence\", t.\"blnKurstypFormularanmeldung\", t.\"intKurstypKurswahlMin\", " + 
            "array_to_string(array_agg(x.\"lngKursID\"), ',') as kurse from \"tblSdKurstyp\" t, \"tblBdKursXKurstyp\" x, \"tblBdKurs\" k " +
            "where t.\"lngSdSeminarID\"=" +  seminar.getSeminarID() + 
            " and x.\"lngSeminarID\"=t.\"lngSdSeminarID\" " +
            "and x.\"lngKurstypID\"=t.\"lngKurstypID\" " +
            "and k.\"lngSdSeminarID\"=x.\"lngSeminarID\" " + 
            "and k.\"lngKursID\"=x.\"lngKursID\" " + 
            "and k.\"blnKursPlanungssemester\"=false " + 
            "group by t.\"strKurstypBezeichnung\",t.\"intKurstypSequence\", t.\"blnKurstypFormularanmeldung\", t.\"intKurstypKurswahlMin\",t.\"lngSdSeminarID\",t.\"lngKurstypID\",t.\"lngKurstypLeistungsID\"  "+
            "union Select t2.\"lngKurstypLeistungsID\", t2.\"lngKurstypID\",t2.\"strKurstypBezeichnung\", t2.\"intKurstypSequence\", t2.\"blnKurstypFormularanmeldung\", t2.\"intKurstypKurswahlMin\", " + 
            "'' as kurse from \"tblSdKurstyp\" t2 " + 
            "where t2.\"lngSdSeminarID\"=" +  seminar.getSeminarID() + 
            " and not exists(select x2.\"lngKurstypID\" from \"tblBdKursXKurstyp\" x2 where x2.\"lngSeminarID\"=t2.\"lngSdSeminarID\" " +
            "and x2.\"lngKurstypID\"=t2.\"lngKurstypID\")) ss " +
            "order by \"strKurstypBezeichnung\"");
        
System.out.println("Select t.\"lngKurstypLeistungsID\", t.\"lngKurstypID\",t.\"strKurstypBezeichnung\", t.\"intKurstypSequence\", t.\"blnKurstypFormularanmeldung\", t.\"intKurstypKurswahlMin\", " + 
            "array_to_string(array_agg(x.\"lngKursID\"), ',') as kurse from \"tblSdKurstyp\" t, \"tblBdKursXKurstyp\" x, \"tblBdKurs\" k " +
            "where t.\"lngSdSeminarID\"=" +  seminar.getSeminarID() + 
            " and x.\"lngSeminarID\"=t.\"lngSdSeminarID\" " +
            "and x.\"lngKurstypID\"=t.\"lngKurstypID\" " +
            "and k.\"lngSdSeminarID\"=x.\"lngSeminarID\" " + 
            "and k.\"lngKursID\"=x.\"lngKursID\" " + 
            "and k.\"blnKursPlanungssemester\"=false " + 
            "group by t.\"strKurstypBezeichnung\",t.\"intKurstypSequence\", t.\"blnKurstypFormularanmeldung\", t.\"intKurstypKurswahlMin\",t.\"lngSdSeminarID\",t.\"lngKurstypID\",t.\"lngKurstypLeistungsID\" order by t.\"intKurstypSequence\" "+
            "union Select t2.\"lngKurstypLeistungsID\", t2.\"lngKurstypID\",t2.\"strKurstypBezeichnung\", t2.\"intKurstypSequence\", t2.\"blnKurstypFormularanmeldung\", t2.\"intKurstypKurswahlMin\", " + 
            "'' as kurse from \"tblSdKurstyp\" t2 " + 
            "where t2.\"lngSdSeminarID\"=" +  seminar.getSeminarID() + 
            " and not exists(select x2.\"lngKurstypID\" from \"tblBdKursXKurstyp\" x2 where x2.\"lngSeminarID\"=t2.\"lngSdSeminarID\" " +
            "and x2.\"lngKurstypID\"=t2.\"lngKurstypID\") " +
            "");
	while(rKurstyp.next()){
		if(!bFirstLoop) out.write(",");
		else bFirstLoop=false;
		sKurstypID=rKurstyp.getString("lngKurstypID");
		out.write("{\"kurstyp\":{" + 
                        "\"name\":\"" + rKurstyp.getString("strKurstypBezeichnung") + "\"," + 
                        "\"kurswahl\":\"" + (rKurstyp.getBoolean("blnKurstypFormularanmeldung") ? "true" : "false") + "\"," + 
                        "\"leistung_id\":\"" + rKurstyp.getString("lngKurstypLeistungsID") + "\"," +
                        "\"kurswahl_anzahl\":\"" + rKurstyp.getFloat("intKurstypKurswahlMin") + "\"," +
                        "\"kurse\":[" + rKurstyp.getString("kurse") + "]," +
                        "\"tauschoption\":\"" + (sKurstypenMitTauschoption.indexOf("," + sKurstypID + ",")>=0 ? "true" : "false") + "\"," +
                        "\"id\":\"" + sKurstypID + "\"" +
                "}}");
	}
	rKurstyp.close();
%>]}
