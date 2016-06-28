<%--
    JSP-Object "student/get"

    @TODO
    ===========================
    de.shj.UP.beans.config.student.StudentSearchBean wird verwendet, 
    ist aber ein ziemlicher Schrotthaufen.

    SYNOPSIS (German)
    ===========================
    2013, Feb 27, shj
    Erzeugt. 

    2016, März 13, shj
    q&d: anmeldung_gympo zugeordnet tblBdStudent.strStudentZUVFach (sonst leer).
         wahr, falls strStudentZUVFach != null. In dem Fall wird das Datum 
         (ISO) ausgegeben, an dem die Anmeldemarkierung gelöscht werden kann.
         
         Umsetzung in der Oberfläche per Checkbox und Cbo fürs Löschdatum
         
         Löschen nach Auslaufen GymPO (ca. 2022).

         Ein eigenes Tabellenfeld lohnt den Aufwand nicht -- es müsste in 
         den Beans ausprogrammiert werden.
    
    Üblicher Lifecycle: GET

    Zwei Varianten:
    (a) Suchfunktion: Suche nach Studierenden
    (b) Detailinformation zu einem/r Studierenden
    
    Expected SESSION PROPERTIES
    ===========================
    user        muss initialisiert sein (AccessLevel egal),

    Expected PARAMETER(S):
    ===========================
    (a) Suchfunktion:
        txtSuchen       [String]                Teil des Nachnamens (M?ll, Ulz, Meier, Meyer, M*r) oder die Matrikelnr.
        [chkEhemalige]  ["true", optional]      Sollen auch nicht aktuell immatrikulierte Studierende gelistet werden?
    
    Returns
    =======
    

    Sample Usage
    ============

    
--%>

<%@page import="de.shj.UP.data.Fach"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="de.shj.UP.util.ResultSetSHJ"%>
<%@page contentType="text/json" pageEncoding="UTF-8" import="java.sql.ResultSet,java.text.SimpleDateFormat, de.shj.UP.data.Dozent,de.shj.UP.data.shjCore" session="true" isThreadSafe="false"  errorPage="../error.jsp" %>
<jsp:useBean id="student" scope="session" class="de.shj.UP.logic.StudentData" /><jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean id="seminar" scope="session" class="de.shj.UP.logic.SeminarData" />
<%@include file="../../fragments/checkVersion.jsp" %>
<%@include file="../../fragments/conf_general.jsp" %>
<%!String getZUVZielInfo(int lZUVZiel){
    switch(lZUVZiel){
        case 181: return "Bachelor";
        case 182: return "Bachelor";
        case 188: return "Master";
        case 102: return "Magister";
        case 125: return "Lehramt";
        case 506: return "Promotion";
        case 2: return "Erasmus";
        default: return "unbekannt (" + lZUVZiel + ")";
    }
}%>
<%  
// Handelt es sich um eine Suche nach Studenten?
if(request.getParameter("txtSuchen")!=null){
    // Das Suchfeld muss mindestens 3 Zeichen
    // enthalten, sonst gibt es zu viele Ergebnisse
    if(request.getParameter("txtSuchen").length()<g_iMinSearchChars_Studierende) return;

    // Die Suche ist nur vom Lehrenden-Login aus erlaubt, 
    // überprüfe hier also die Berechtigungen:
%>
    <%@include file="../../fragments/checkLogin.jsp" %>
    <%@include file="../../fragments/checkAccess1.jsp" %>
<%    // Initialisiere die Suche (txtSuchen enthält entweder
    // einen Teil des Nachnamens, oder die Matrikelnummer)

// @TODO:
// Nachname säubern (um Wildcards zumindest, Länge mit g_iMinSearchChars_Studierende abgleichen)
// Matrikelnummer anstatt Nachnamen zulassen

    PreparedStatement pstm = user.prepareStatement("select " +
            "x.\"lngSeminarID\",x.\"intFachID\",f.\"strFachBeschreibung\", s.*, " +
           "CASE WHEN s.\"intStudentFach1\"=x.\"intFachID\" THEN s.\"intStudentFachsemester1\" " +
            "WHEN s.\"intStudentFach2\"=x.\"intFachID\" THEN s.\"intStudentFachsemester2\" " +
            "WHEN s.\"intStudentFach3\"=x.\"intFachID\" THEN s.\"intStudentFachsemester3\" " +
            "WHEN s.\"intStudentFach4\"=x.\"intFachID\" THEN s.\"intStudentFachsemester4\" " +
            "WHEN s.\"intStudentFach5\"=x.\"intFachID\" THEN s.\"intStudentFachsemester5\" " +
            "WHEN s.\"intStudentFach6\"=x.\"intFachID\" THEN s.\"intStudentFachsemester6\" " +
            "END  as fachsemester " +
            "FROM \"tblSdSeminarXFach\" x, \"tblBdStudent\" s, \"tblSdFach\" f " +
          "WHERE " +
           "(" +
            "(x.\"lngSeminarID\"=" + user.getSdSeminarID() + ") AND " + 
            "lower(\"strStudentNachname\") LIKE ? AND " +
                  "x.\"intFachID\"=f.\"intFachID\" and " +
              "(" +
                "(s.\"intStudentFach1\"=x.\"intFachID\") OR " +
                "(s.\"intStudentFach2\"=x.\"intFachID\") OR " +
                "(s.\"intStudentFach3\"=x.\"intFachID\") OR " +
                "(s.\"intStudentFach4\"=x.\"intFachID\") OR " +
                "(s.\"intStudentFach5\"=x.\"intFachID\") OR " +
                "(s.\"intStudentFach6\"=x.\"intFachID\")" +
              ")" +
           ")");
    pstm.setString(1, "%" + request.getParameter("txtSuchen").toLowerCase() + "%");
    ResultSetSHJ studenten = new ResultSetSHJ(pstm.executeQuery());

//    studenten.setShowAllOnEmptySearch( (g_iMinSearchChars_Studierende<=0) );
    String sInfo="";boolean bStart=true;
    boolean bEhemalige=false;
    try{bEhemalige=request.getParameter("chkEhemalige").equals("true");}catch(Exception eIsNullIgnore){}
    int iFachsemester=0;
    out.write("[");
    while(studenten.next()){
       sInfo="";
       try{
            if(studenten.getString("strStudentUrlaub")!=null && studenten.getString("strStudentUrlaub").startsWith("!")){sInfo="Urlaub";}
            // Altbestand nur falls älter als 3 Monate.
            //System.out.println(studenten.getStudentNachname() + ": " + (seminar.getLastZUVUpdate().getTime()-studenten.getStudentZUVUpdate().getTime()));
            if((seminar.getLastZUVUpdate().getTime()-studenten.getDate("dtmStudentZUVUpdate").getTime()) > (long)1000*60*60*24*30*3){sInfo="Altbestand";}
            //if(studenten.getStudentZUVUpdate().before(sZielEZielEeminar.getLastZUVUpdate())){sInfo="Altbestand";}
       }catch(Exception eWhat){System.out.println("Caught an error");}
    if(bEhemalige || !sInfo.equals("Altbestand")){if(!bStart) out.write(",");bStart=false;
    String sUrlaub = shjCore.normalize(studenten.getString("strStudentUrlaub")).trim();
    if(sUrlaub.equals("[# 0]")) sUrlaub = "--";
    
    %>{"student":
        {"vorname":"<%=shjCore.string2JSON(studenten.getString("strStudentVorname")) %>",
        "nachname":"<%=shjCore.string2JSON(studenten.getString("strStudentNachname")) %>",
        "matrikelnummer":"<%=studenten.getString("strMatrikelnummer")%>",
        "studiengang":"<%=getZUVZielInfo(studenten.getInt("lngStudentZUVZiel")) %>",
        "info":"<%=sInfo %>",
        "strasse":"<%=studenten.getString("strStudentStrasse")%>",
        "plz":"<%=studenten.getString("strStudentPLZ")%>",
        "ort":"<%=studenten.getString("strStudentOrt")%>",
        "semester":"<%=studenten.getLong("fachsemester") %>",
        "email":"<%=studenten.getString("strStudentEmail") %>",
        "telefon":"<%=studenten.getString("strStudentTelefon") %>",
        "fach":"<%=studenten.getString("strFachBeschreibung")%>",
        "fach_id":"<%=studenten.getString("intFachID") %>",
        "fachsemester":"<%=studenten.getLong("fachsemester") %> ",
        "pid":"<%=studenten.getString("lngStudentPID") %>",
        "geburtstag":"<%= (studenten.getDate("dtmStudentGeburtstag")==null ? "" : shjCore.shjGetGermanDate(studenten.getDate("dtmStudentGeburtstag"))) %>",
        "geburtsort":"<%=studenten.getString("strStudentGeburtsort")%>",
        "staat":"<%=studenten.getString("strStudentStaat")%>",
        "mobil":"<%=studenten.getString("strStudentHandy")%>",
        "homepage":"<%=studenten.getString("strStudentHomepage")%>",
        "erstimmatrikulation":"<%=studenten.getDate("dtmStudentZUVImmatrikuliert")%>",
        "update":"<%=studenten.getDate("dtmStudentZUVUpdate") %>",
        "anmeldung_gympo":"<%= shjCore.normalize(studenten.getString("strStudentZUVFach")) %>",
        "urlaub":"<%=shjCore.string2JSON(sUrlaub) %>",
        "ziel":"<%=getZUVZielInfo(studenten.getInt("lngStudentZUVZiel")) %>",
        "info_email":"<%=studenten.getBoolean("blnStudentPublishEmail")%>",
        "anrede":"<%=(studenten.getBoolean("blnStudentFemale") ? "Frau" : "Herr") %>"}
        }
<%}}out.write("]");

}else{
    //This is a request from a student, we assume, so the student must 
    //be initialized and active
    %><%@include file="../../fragments/checkLoginStudent.jsp" %><%
    
    %>[
    {"student":
        {"vorname":"<%=shjCore.string2JSON(student.getStudentVorname()) %>",
        "nachname":"<%=shjCore.string2JSON(student.getStudentNachname()) %>",
        "matrikelnummer":"<%=student.getMatrikelnummer() %>",
        "studiengang":"<%=student.getZUVZielExplain() %>",
        "info":"--",
        "strasse":"<%=student.getStudentStrasse() %>",
        "plz":"<%=student.getStudentPLZ()%>",
        "ort":"<%=student.getStudentOrt()%>",
        "semester":"<%=student.getStudentSemester() %>",
        "email":"<%=student.getStudentEmail() %>",
        "telefon":"<%=student.getStudentTelefon() %>",
        "fach":"<%=new Fach(student.getFachID(seminar.getSeminarID())).getFachBezeichnung() %>",
        "fach_id":"<%=student.getFachID(seminar.getSeminarID()) %>",
        "fachsemester":"<%=student.getFachsemester(student.getFachID(seminar.getSeminarID())) %> ",
        "pid":"<%=student.getStudentPID() %>",
        "geburtstag":"<%= (student.getStudentGeburtstag()==null ? "" : shjCore.shjGetGermanDate(student.getStudentGeburtstag())) %>",
        "geburtsort":"<%=student.getStudentGeburtsort()%>",
        "staat":"<%=student.getStudentStaat()%>",
        "mobil":"<%=student.getStudentHandy() %>",
        "homepage":"<%=student.getStudentHomepage() %>",
        "erstimmatrikulation":"<%=student.getStudentZUVImmatrikuliert() %>",
        "update":"<%=student.getStudentZUVUpdate() %>",
        "urlaub":"<%=shjCore.string2JSON(student.getStudentUrlaubSummary()) %>",
        "ziel":"<%=student.getZUVZielExplain() %>",
        "info_email":"<%=student.getStudentPublishEmail() %>",
        "anrede":"<%=(student.getStudentFemale() ? "Frau" : "Herr") %>"}
        }]
<%}%>