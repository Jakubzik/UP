<%--
    JSP-Object "seminar/fach/get"
    @Revision Nov 23, 2013

    @TODO
    ===========================
    Man könnte iFachID übergeben und 
    so -- insbes. bei Studierenden-Logins
    -- die Info auf _einen_ Studiengang 
    beschränken.                                                ... OK, 26.2.2015

!   Bei Studierenden-Login nur das eine 
!   Fach abrufen

    SYNOPSIS (German)
    ===========================
    2013, Mar 5, shj
    Erzeugt. 
    
    Üblicher Lifecycle: GET
    
    Liefert (z.B. beim ersten Login) Informationen zu
    den Studiengängen des Seminars (Module, Leistungen -- the works).

    ACHTUNG: abhängig von ../fragments/conf_general. g_bFachForEveryone
    wird der Zugang zu den Daten auch ohne Login gestattet. Ohne Login 
    muss allerdings der Parameter >seminar_id< übergeben werden

    Aggregiert Informationen zum Seminar: 
    - Faecher (Studiengaenge),
        -- Module
            --- Leistungen
    
    Expected SESSION PROPERTIES
    ===========================
    user        muss initialisiert sein (AccessLevel egal),
    oder studierende/r

    Expected PARAMETER(S):
    ===========================
    fach_id     (optional)

    Returns
    =======
    Array of objects with remarks:
    {
      "faecher": [
         {
            "fach": {
                "name": "BA 25% Kulturwissenschaft Englische Philologie",
                "id": "92202",
                "module": [
                    {
                        "modul": {
                            "name": "Aufbaustudien Kulturwissenschaft",
                            "name_en": "Intermediate Studies Culture",
                            "min_ects": "10",
                            "waehlbar": true,
                            "id": "122",
                            "leistungen": [
                                {
                                    "leistung": {
                                        "name": "Proseminar II Kulturwissenschaft/Landeskunde",
                                        "name_en": "Intermediate Seminar in Cultural Studies",
                                        "cp": "6",
                                        "id": "10090"
                                    }
                                },
                                {
                                    "leistung": {
                                        "name": "Vorlesung Kulturwissenschaft",
                                        "name_en": "Lecture Course in Cultural Studies",
                                        "cp": "4",
                                        "id": "10150"
                                    }
                                }
                            ]
                        }
                    },
                    {
                        "modul": {
                            "name": "Basismodul Kulturwissenschaft (25KW)",
                            "name_en": "Cultural Core Studies (25KW)",
                            "min_ects": "9.5",
                              ...
                              ...
                              ...
                            }
                        }
                    ]
                }
            },
            "fach": {
                ...
                ...

--%>
<%@page contentType="text/json" pageEncoding="UTF-8" import="java.sql.ResultSet,de.shj.UP.data.Dozent,de.shj.UP.data.shjCore" session="true" isThreadSafe="false"  errorPage="../../error.jsp" %>
<jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean id="seminar" scope="session" class="de.shj.UP.logic.SeminarData" /><jsp:useBean id="student" scope="session" class="de.shj.UP.beans.config.student.StudentBean" />
<%@include file="../../../fragments/checkVersion.jsp" %>
<%@include file="../../../fragments/conf_general.jsp" %>
<%if(user.getDozentNachname()!=null){%>
<%@include file="../../../fragments/checkLogin.jsp" %>
<%@include file="../../../fragments/checkAccess1.jsp" %>
<%}else{
    if(g_bFachForEveryone){
        seminar.init(0,0,Long.parseLong(request.getParameter("seminar_id")));
    }else{
%>
<%@include file="../../../fragments/checkLoginStudent.jsp" %>
<%}}%>
{"faecher":[
<%long lERR_BASE=202000 + 100;    // Fach + Get
        long lFachID=-1;
        if(request.getParameter("fach_id")!=null){
            try{lFachID=Long.parseLong(request.getParameter("fach_id"));
            }catch(Exception eIgnore){}
        }
	boolean bFirstLoop=true;
	ResultSet rNoten=seminar.sqlQuery(
        "SELECT distinct f.\"strFachBezeichnung\", f.\"strFachBeschreibung\", m.\"strModulBeschreibung\", m.\"lngModulID\", pxf.\"intFachID\", " +
                "m.\"strModulBezeichnung\",m.\"strModulBezeichnung_en\", m.\"blnModulWaehlbar\", mxl.\"lngLeistungID\", mxl.\"sngMinCreditPts\"," +
                "pxm.\"sngMinCreditPtsPLeistung\",pxm.\"sngPruefungLeistungGewichtung\"," +
                "l.\"strLeistungBezeichnung\", l.\"strLeistungBezeichnung_en\"  " +
                "FROM \"tblSdModulXLeistung\" mxl, " +
                "\"tblSdModul\" m, " +
                "\"tblSdPruefungXModul\" pxm, " +
                "\"tblSdPruefungXFach\" pxf," +
                "\"tblSdFach\" f, " +
                "\"tblSdLeistung\" l " +
                "WHERE " +
                        "l.\"lngLeistungID\"=mxl.\"lngLeistungID\" and " +
                        "l.\"lngSdSeminarID\"=mxl.\"lngSdSeminarID\" and " +
                        "f.\"intFachID\"=pxf.\"intFachID\" and " +
                        "pxf.\"blnPruefungFachAbschluss\"='t' and " + 
                        (lFachID >0 ? "pxf.\"intFachID\" = " + lFachID + " AND " : "" ) +
                        "mxl.\"lngSdSeminarID\" = m.\"lngSdSeminarID\" AND " + 
                             "mxl.\"lngModulID\" = m.\"lngModulID\" AND " +
                             "pxm.\"lngSdSeminarID\" = m.\"lngSdSeminarID\" AND " +
                             "pxm.\"lngModulID\" = m.\"lngModulID\" AND " +
                             "pxf.\"lngSdSeminarID\" = pxm.\"lngSdSeminarID\" AND " +
                             "pxf.\"lngPruefungID\" = pxm.\"lngPruefungID\" AND " +
                             "mxl.\"lngSdSeminarID\" = " + seminar.getSeminarID() + " AND " +
                             "m.\"lngSdSeminarID\" =  " + seminar.getSeminarID() + " AND " +
                             "pxm.\"lngSdSeminarID\" =  " + seminar.getSeminarID() + " AND " +
                             "pxf.\"lngSdSeminarID\" =  " + seminar.getSeminarID() + " " +
                "ORDER BY pxf.\"intFachID\", m.\"strModulBezeichnung\" ASC;"                
        );

        long lFachIDCurr=-1; long lFachIDOld=-2;
        long lModulIDCurr=-1; long lModulIDOld=-2;
        boolean bFirstFach=true;
        boolean bFirstModul=true;
        boolean bIsNewModul=false;
        boolean bIsNewFach=false;
	while(rNoten.next()){
                lModulIDCurr=rNoten.getLong("lngModulID");lFachIDCurr=rNoten.getLong("intFachID");
                bIsNewModul = (lModulIDCurr!=lModulIDOld);bIsNewFach=(lFachIDCurr!=lFachIDOld);
		if(bIsNewFach){
                    bFirstModul=true;
                    if(!bFirstFach) out.write("]}}]}},"); // ]} für Leistungen, ]} für Module, }} fürs Fach
                    bFirstFach=false;
                    out.write( "{\"fach\":{" + 
                                    "\"name\":\"" + shjCore.string2JSON(rNoten.getString("strFachBeschreibung")) + "\"," +
                                    "\"id\":\"" + lFachIDCurr + "\"," +
                                    "\"module\":[" + 
                                        "{\"modul\":{" +
//                                            "\"qziele\":\"" + shjCore.string2JSON(rNoten.getString("strModulBeschreibung")) + "\"," + 
                                            "\"name\":\"" + rNoten.getString("strModulBezeichnung") + "\"," +
                                            "\"name_en\":\"" + shjCore.string2JSON(rNoten.getString("strModulBezeichnung_en")) + "\"," +
                                            "\"min_ects\":\"" + rNoten.getString("sngMinCreditPtsPLeistung") + "\"," +
                                            "\"gewichtung\":\"" + rNoten.getString("sngPruefungLeistungGewichtung") + "\"," +
                                            "\"waehlbar\": "+ rNoten.getBoolean("blnModulWaehlbar") + "," +
                                            "\"id\":\"" + rNoten.getString("lngModulID") + "\"," +
                                            "\"leistungen\":[{\"leistung\":{" + 
                                                "\"name\":\"" + rNoten.getString("strLeistungBezeichnung") + "\"," +
                                                "\"name_en\":\"" + rNoten.getString("strLeistungBezeichnung_en") + "\"," +
                                                "\"cp\":\"" + rNoten.getString("sngMinCreditPts") + "\"," +
                                                "\"id\":\"" + rNoten.getString("lngLeistungID") + "\"}}");
                }else if (bIsNewModul){
                    out.write("]}},");
                    out.write("{\"modul\":{" +
//                            "\"qziele\":\"" + shjCore.string2JSON(rNoten.getString("strModulBeschreibung")) + "\"," +
                                    "\"name\":\"" + rNoten.getString("strModulBezeichnung") + "\"," +
                                    "\"name_en\":\"" + shjCore.string2JSON(rNoten.getString("strModulBezeichnung_en")) + "\"," +
                                    "\"min_ects\":\"" + rNoten.getString("sngMinCreditPtsPLeistung") + "\"," +
                                    "\"gewichtung\":\"" + rNoten.getString("sngPruefungLeistungGewichtung") + "\"," +
                                    "\"id\":\"" + rNoten.getString("lngModulID") + "\"," +
                                    "\"waehlbar\": "+ rNoten.getBoolean("blnModulWaehlbar") + "," +
                                    "\"leistungen\":[{\"leistung\":{" + 
                                        "\"name\":\"" + rNoten.getString("strLeistungBezeichnung") + "\"," +
                                        "\"name_en\":\"" + rNoten.getString("strLeistungBezeichnung_en") + "\"," +
                                        "\"cp\":\"" + rNoten.getString("sngMinCreditPts") + "\"," +
                                        "\"id\":\"" + rNoten.getString("lngLeistungID") + "\"}}");
                }else{
                    out.write(",{\"leistung\":{" + 
                                    "\"name\":\"" + rNoten.getString("strLeistungBezeichnung") + "\"," +
                                    "\"name_en\":\"" + rNoten.getString("strLeistungBezeichnung_en") + "\"," +
                                    "\"cp\":\"" + rNoten.getString("sngMinCreditPts") + "\"," +
                                    "\"id\":\"" + rNoten.getString("lngLeistungID") + "\"}}");
                }
                lModulIDOld=lModulIDCurr;
                lFachIDOld=lFachIDCurr;
	}
	//rNoten.close();
%>]}}]}}]}