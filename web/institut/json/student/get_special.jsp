<%--
    JSP-Object "student/getspecial"

    @TODO
    ===========================
    de.shj.UP.beans.config.student.StudentSearchBean wird verwendet, 
    ist aber ein ziemlicher Schrotthaufen.

    SYNOPSIS (German)
    ===========================
    2016, April
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
<jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" />
<%@include file="../../fragments/checkAccess1.jsp" %><%    // Initialisiere die Suche (txtSuchen enthält entweder
    // einen Teil des Nachnamens, oder die Matrikelnummer)
    shjCore server = new shjCore();
    ResultSet rPPS = user.sqlQuery("select * from \"tblBdStudent\" s " + 
            "where exists(select * from \"tblBdStudentXLeistung\" x, \"tblSdNote\" n where " +
            "x.\"lngSdSeminarID\"=" + user.getSdSeminarID() + " and " +
            "x.\"strMatrikelnummer\" = s.\"strMatrikelnummer\" and " +
            "x.\"lngLeistungsID\"=1 and " +
            "x.\"lngDozentID\"=79 and " +
            "n.\"lngSdSeminarID\"=x.\"lngSdSeminarID\" and " +
            "n.\"intNoteID\"=x.\"intNoteID\" and " + 
            "n.\"intNoteID\"=10) order by \"strMatrikelnummer\";");
    out.write("{\"studenten\":[");
    String sBackend = "&signup_expected_backend_version=1-0-0-2";
    boolean bFirstLoop = true;
    while(rPPS.next()){
        if(!bFirstLoop) out.write(",");
        bFirstLoop = false;
        String sM = rPPS.getString("strMatrikelnummer");
        out.write("{\"student\":{\"matrikelnummer\":\"" + 
                sM + 
                "\", \"nachname\":\"" + rPPS.getString("strStudentNachname") + 
                "\", \"vorname\":\"" + rPPS.getString("strStudentVorname") + 
                "\", \"Lehramt?\":\"" + (rPPS.getLong("lngStudentZUVZiel")==125) + 
                "\", \"ZUVUpdate\":\"" + shjCore.shjGetGermanDate(rPPS.getDate("dtmStudentZUVUpdate")) + "\"},");
        String sLeistungen = "leistung/get.jsp?matrikelnummer=" + sM + sBackend;
        String sBemerkungen = "bemerkung/get.jsp?matrikelnummer=" + sM + sBackend;
        long lCount = server.dbCount("strMatrikelnummer", "tblBdStudentBemerkung", "\"strMatrikelnummer\"='" + sM + "'");
        %>
        "leistungen_o":<jsp:include page="<%=sLeistungen%>" />
        <%if(lCount>0){%>,"bemerkungen_o":<jsp:include page="<%=sBemerkungen%>" /><%}%>}
    <%
    }%>]}
