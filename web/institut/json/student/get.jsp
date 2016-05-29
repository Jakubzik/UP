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

<%@page contentType="text/json" pageEncoding="UTF-8" import="java.sql.ResultSet,de.shj.UP.logic.AktuellesIterator,java.text.SimpleDateFormat, de.shj.UP.data.Dozent,de.shj.UP.data.shjCore" session="true" isThreadSafe="false"  errorPage="../error.jsp" %>
<jsp:useBean id="student" scope="session" class="de.shj.UP.beans.config.student.StudentBean" /><jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean id="seminar" scope="session" class="de.shj.UP.HTML.HtmlSeminar" /><jsp:useBean id="studenten" scope="page" class="de.shj.UP.beans.config.student.StudentSearchBean" />
<%@include file="../../fragments/checkVersion.jsp" %>
<%@include file="../../fragments/conf_general.jsp" %>
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
    studenten.setSeminarID(user.getSdSeminarID());
    studenten.setShowAllOnEmptySearch( (g_iMinSearchChars_Studierende<=0) );
    studenten.init(request);
    String sInfo="";boolean bStart=true;
    boolean bEhemalige=false;
    try{bEhemalige=request.getParameter("chkEhemalige").equals("true");}catch(Exception eIsNullIgnore){}
    int iFachsemester=0;
    out.write("[");
    while(studenten.nextStudent()){
       sInfo="";
       try{
            if(studenten.getStudentUrlaub()!=null && studenten.getStudentUrlaub().startsWith("!")){sInfo="Urlaub";}
            // Altbestand nur falls älter als 3 Monate.
            //System.out.println(studenten.getStudentNachname() + ": " + (seminar.getLastZUVUpdate().getTime()-studenten.getStudentZUVUpdate().getTime()));
            if((seminar.getLastZUVUpdate().getTime()-studenten.getStudentZUVUpdate().getTime()) > (long)1000*60*60*24*30*3){sInfo="Altbestand";}
            //if(studenten.getStudentZUVUpdate().before(seminar.getLastZUVUpdate())){sInfo="Altbestand";}
       }catch(Exception eWhat){System.out.println("Caught an error");}
    if(bEhemalige || !sInfo.equals("Altbestand")){if(!bStart) out.write(",");bStart=false;
    
    %>{"student":
        {"vorname":"<%=shjCore.string2JSON(studenten.getStudentVorname()) %>",
        "nachname":"<%=shjCore.string2JSON(studenten.getStudentNachname()) %>",
        "matrikelnummer":"<%=studenten.getMatrikelnummer() %>",
        "studiengang":"<%=studenten.getZUVZielExplain() %>",
        "info":"<%=sInfo %>",
        "strasse":"<%=studenten.getStudentStrasse() %>",
        "plz":"<%=studenten.getStudentPLZ()%>",
        "ort":"<%=studenten.getStudentOrt()%>",
        "semester":"<%=studenten.getStudentSemester() %>",
        "email":"<%=studenten.getStudentEmail() %>",
        "telefon":"<%=studenten.getStudentTelefon() %>",
        "fach":"<%=studenten.getFach()%>",
        "fach_id":"<%=studenten.getFachID()%>",
        "fachsemester":"<%=studenten.getFachsemester(studenten.getFachID())%> ",
        "pid":"<%=studenten.getStudentPID()%>",
        "geburtstag":"<%= (studenten.getStudentGeburtstag()==null ? "" : shjCore.shjGetGermanDate(studenten.getStudentGeburtstag())) %>",
        "geburtsort":"<%=studenten.getStudentGeburtsort()%>",
        "staat":"<%=studenten.getStudentStaat()%>",
        "mobil":"<%=studenten.getStudentHandy() %>",
        "homepage":"<%=studenten.getStudentHomepage() %>",
        "erstimmatrikulation":"<%=studenten.getStudentZUVImmatrikuliert() %>",
        "update":"<%=studenten.getStudentZUVUpdate() %>",
        "anmeldung_gympo":"<%= shjCore.normalize(studenten.getStudentZUVFach()) %>",
        "urlaub":"<%=shjCore.string2JSON(studenten.getStudentUrlaubSummary()) %>",
        "ziel":"<%=studenten.getZUVZielExplain() %>",
        "info_email":"<%=studenten.getStudentPublishEmail() %>",
        "anrede":"<%=(studenten.getStudentFemale() ? "Frau" : "Herr") %>"}
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
        "fach":"<%=student.Fach().getFachBezeichnung() %>",
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