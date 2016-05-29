<%@ page language="java" pageEncoding="UTF8" contentType="text/json" errorPage="error.jsp"%><jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean id="studenten" scope="page" class="de.shj.UP.beans.config.student.StudentSearchBean" /><jsp:useBean id="seminar" scope="session" class="de.shj.UP.HTML.HtmlSeminar" /><%!
String s2j(String s){
	return s.replace('\n', ' ').replace('\r', ' ').replace('\t', ' ').replace('\"', '\''); 
}
%><%@include file="../fragments/checkVersion.jsp" %>
<%@include file="../fragments/checkLogin.jsp" %>
<%@include file="../fragments/checkAccess1.jsp" %>[<%
 if(request.getParameter("signUp_studentLists")==null){
    if(request.getParameter("txtSuchen").length()<3) return;
    studenten.setSeminarID(user.getSdSeminarID());
    studenten.setShowAllOnEmptySearch( false );
    studenten.init(request);
    String sInfo="";boolean bStart=true;boolean bEhemalige=request.getParameter("chkEhemalige").equals("true");
    while(studenten.nextStudent()){
       sInfo="";
       try{
            if(studenten.getStudentUrlaub()!=null && studenten.getStudentUrlaub().startsWith("!")){sInfo="Urlaub";}
            if(studenten.getStudentZUVUpdate().before(seminar.getLastZUVUpdate())){sInfo="Altbestand";}
       }catch(Exception eWhat){System.out.println("Caught an error");}
    if(bEhemalige || !sInfo.equals("Altbestand")){if(!bStart) out.write(",");bStart=false;
    %>{"student":{"vorname":"<%=s2j(studenten.getStudentVorname()) %>","nachname":"<%=s2j(studenten.getStudentNachname()) %>","matrikelnummer":"<%=studenten.getMatrikelnummer() %>","studiengang":"<%=studenten.getZUVZielExplain() %>","info":"<%=sInfo %>"}}
    <%}}%>
    ]
<%}else{
     
     if(request.getParameter("signUp_studentLists").equals("DropOutAnglistik")){
        // Spezielle Listen (das ist noch nicht durchprogrammiert sondern 
        // liefert im Moment nur das notwendige SQL)

        // (1) Drop-Out
        String sSQL="";

        // Welche Leistungen sollen NOCH NICHT erbracht sein
        // (z.B. BA Abschlussprüfung, oder im Lehramt: Hauptseminare)
        String sLeistungenExcluded="16,21";   // Hauptseminare (Anglistik)

        // Welche "Fächer" (bzw. Studiengänge) kommen 
        // in die Auswahl?
        String sFaecher="8351,200822";        // Anglistik Lehramt

        // Zeitraum der Exmatrikulierung
        String sExNach="2011-4-1";
        String sExVor ="2012-11-1";

        // In welchem Fachsemester?
        int iFS_Max=11;
        int iFS_Min=5;

        for(int ii=1; ii<7; ii++){
            sSQL += (ii>1) ? " union " : "";
            sSQL += "select 'Lehramt', \"strMatrikelnummer\", \"strStudentVorname\", " +
           "\"strStudentNachname\", \"intStudentFachsemester" + ii + "\", (select count(*) from " +
           "\"tblBdStudentXLeistung\" x, \"tblSdNote\" n where x.\"lngSdSeminarID\"=" + user.getSdSeminarID() + " and " +
            "x.\"strMatrikelnummer\"=s.\"strMatrikelnummer\" and " +
             "n.\"lngSdSeminarID\"=" + user.getSdSeminarID() + " and " +
             "n.\"intNoteID\"=x.\"intNoteID\" and " +
             "n.\"blnNoteBestanden\"=true) bestandene, (select count(*) from " +
           "\"tblBdStudentXLeistung\" x, \"tblSdNote\" n where x.\"lngSdSeminarID\"=" + user.getSdSeminarID() + " and " +
              "x.\"strMatrikelnummer\"=s.\"strMatrikelnummer\" and " +
              "n.\"lngSdSeminarID\"=" + user.getSdSeminarID() + " and " +
              "n.\"intNoteID\"=x.\"intNoteID\" and " +
              "n.\"blnNoteBestanden\"=false) durchgefallene from " +
           "\"tblBdStudent\" s where \"intStudentFach" + ii + "\" IN (" + sFaecher + ") and " +
           "\"intStudentFachsemester" + ii + "\">=" + iFS_Min + " and \"intStudentFachsemester" + ii + "\"<=" + iFS_Max + " and " +
           "\"dtmStudentZUVUpdate\">'" + sExNach + "' and \"dtmStudentZUVUpdate\"<'" + sExVor + "' " +
           "and \"lngStudentZUVZiel\" != 506 and " +
              "(select count(*) from \"tblBdStudentXLeistung\" x, \"tblSdNote\" " +
           "n where x.\"lngSdSeminarID\"=" + user.getSdSeminarID() + " and " +
              "x.\"strMatrikelnummer\"=s.\"strMatrikelnummer\" and " +
              "n.\"lngSdSeminarID\"=" + user.getSdSeminarID() + " and " +
              "n.\"intNoteID\"=x.\"intNoteID\" and " +
              "n.\"blnNoteBestanden\"=true and " +
              "x.\"lngLeistungsID\" IN (" + sLeistungenExcluded + "))<1";
        }

    }else if(request.getParameter("signUp_studentLists").equals("OPSuenderAnglistik")){
        String sSQL="";
        String sRueckmeldungNach="2013-4-1";
        
        int iMinFS=4;
        
        for(int ii=1;ii<7;ii++){
            sSQL += (ii>1) ? " union " : "";
            sSQL += "select 'Lehramt und BA HF', \"strMatrikelnummer\", \"strStudentVorname\", " +
            "\"strStudentNachname\", \"intStudentFachsemester" + ii + "\", \"strStudentStrasse\", " +
            "\"strStudentPLZ\", \"strStudentOrt\", \"strStudentUrlaub\" from " +
            "\"tblBdStudent\" s where \"intStudentFach" + ii + "\" IN (8351, 83504, 83505, " +
            "83907,200216, 200317, 200418,200822) and \"intStudentFachsemester" + ii + "\">=" + iMinFS + " " +
            "and \"dtmStudentZUVUpdate\">'" + sRueckmeldungNach + "' and " +
                    "\"lngStudentZUVZiel\" != 506 and " +
                     "  (select count(*) from \"tblBdStudentXLeistung\" x, \"tblSdNote\" " +
            "n where x.\"lngSdSeminarID\"=" + user.getSdSeminarID() + " and " +
                       "x.\"strMatrikelnummer\"=s.\"strMatrikelnummer\" and " +
                       "n.\"lngSdSeminarID\"=" + user.getSdSeminarID() + " and " +
                       "n.\"intNoteID\"=x.\"intNoteID\" and " +
                       "n.\"blnNoteBestanden\"=true and " +
                       "x.\"lngLeistungsID\" IN (0,1))<2 " +
              "union select 'BA 25 LW oder KW', \"strMatrikelnummer\", \"strStudentVorname\", " +
                "\"strStudentNachname\", \"intStudentFachsemester" + ii + "\", \"strStudentStrasse\"," +
                "\"strStudentPLZ\", \"strStudentOrt\", \"strStudentUrlaub\" from " +
                "\"tblBdStudent\" s where \"intStudentFach" + ii + "\" IN (92202, 92302, 200519, " +
                "200620) and \"intStudentFachsemester" + ii + "\">=" + iMinFS + " and " +
                        "\"dtmStudentZUVUpdate\">'" + sRueckmeldungNach + "' and " +
                        "\"lngStudentZUVZiel\" != 506 and " +
                           "(select count(*) from \"tblBdStudentXLeistung\" x, \"tblSdNote\" " +
                        "n where x.\"lngSdSeminarID\"=" + user.getSdSeminarID() + " and " +
                           "x.\"strMatrikelnummer\"=s.\"strMatrikelnummer\" and " +
                           "n.\"lngSdSeminarID\"=" + user.getSdSeminarID() + " and " +
                           "n.\"intNoteID\"=x.\"intNoteID\" and " +
                           "n.\"blnNoteBestanden\"=true and " +
                           "x.\"lngLeistungsID\" IN (4,1))<2" +
                "union select 'BA 25 SW', \"strMatrikelnummer\", \"strStudentVorname\"," + 
                "\"strStudentNachname\", \"intStudentFachsemester" + ii + "\", \"strStudentStrasse\"," + 
                "\"strStudentPLZ\", \"strStudentOrt\", \"strStudentUrlaub\" from " + 
                "\"tblBdStudent\" s where \"intStudentFach" + ii + "\" IN (92402, 200721) and " + 
                        "\"intStudentFachsemester" + ii + "\">=" + iMinFS + " and " + 
                        "\"dtmStudentZUVUpdate\">'" + sRueckmeldungNach + "' and " + 
                        "\"lngStudentZUVZiel\" != 506 and " + 
                        "(select count(*) from \"tblBdStudentXLeistung\" x, \"tblSdNote\" " + 
                        "n where x.\"lngSdSeminarID\"=" + user.getSdSeminarID() + " and " + 
                         "x.\"strMatrikelnummer\"=s.\"strMatrikelnummer\" and " + 
                         "n.\"lngSdSeminarID\"=" + user.getSdSeminarID() + " and " + 
                         "n.\"intNoteID\"=x.\"intNoteID\" and " + 
                         "n.\"blnNoteBestanden\"=true and " + 
                         "x.\"lngLeistungsID\" IN (4,0))<2";
        }
    }
 }%>