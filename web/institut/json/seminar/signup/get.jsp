<%--
    JSP-Object "seminar/signup/get"
    @Revision: Mar 3, 2014 -- hj

    @TODO
    ===========================

    SYNOPSIS (German)
    ===========================
    2014, Mar 3, shj
    Erzeugt. 
    
    Üblicher Lifecycle: GET

    Liefert den aktuellen Stand der Kurswahl zu einem 
    Kurstyp, z.B. zur Darstellung in einer Tabelle like so:

    Proseminare
    ===========
    Hirsch, 20+5 (30)
    Reh, 30-8 (38)
    ...
    ...

    ----- ODER ---------
    falls "detail_request" und eine Matrikelnummer übergeben 
    wurden, wird eine Liste der vom Studierenden gewählten 
    Kurse mit Gesamt-TN-Zahl und Überlappungen (ja/nein) 
    geliefert.
    
    Expected SESSION PROPERTIES
    ===========================
    user        muss initialisiert sein (AccessLevel >= 500),

    Expected PARAMETER(S):
    ===========================
    kurstyp_id      [long]
    
    detail_request  [null/not null] als Flag für eine Detail-Anfrage
    matrikelnummer  [long]  muss für den detail_request geliefert werden.
    
    Returns
    =======
    Array wie z.B.:
        [
        {"kurs":
            {"id":"14992",
             "bezeichnung":"Mantlik, Di 14:15",
 *           "tn_wunsch":"29",                                  *  nur falls kein "detail_request"
 *           "tn_kein_wunsch":"20",                             ** nur falls ein "detail_request"
 *           "tn_fixiert":"0",
 *           "nachfrage":"53",
 **          "zuschlag":"false",
 **          "fixiert":"false",
 **          "ueberlappung":"true",
             "tn_alle_kurstypen":"49"}
             }
        ,
        {"kurs":
            {"id":"14996",
             "bezeichnung":"Polzenhagen, Di 11:15",
             "tn_wunsch":"2",
             "tn_kein_wunsch":"1",
             "tn_fixiert":"0",
             "nachfrage":"19",
             "tn_alle_kurstypen":"3"}
             }
        ,
        {"kurs":
            {"id":"14998",
             "bezeichnung":"Polzenhagen, Mo 14:15",
             "tn_wunsch":"14",
             "tn_kein_wunsch":"24",
             "tn_fixiert":"0",
             "nachfrage":"21",
             "tn_alle_kurstypen":"38"}
             }
        ]

        dabei bedeutet
        =============
        tn_wunsch:          # der TN, die diesen Kurs in diesem Kurstyp mit der Prio 9 gewählt und einen Platz bekommen haben;
        tn_kein_wunsch:     # der TN, die in diesen Kurs in diesem Kurstyp gewählt wurden, obwohl er nicht ihre TOP Prio war.
        tn_fixiert:         # der TN, deren Teilnahme in diesem Kurs fixiert wurde, die also von Änderungen ausgeschlossen sind.
        nachfrage:          # der TN, die diesen Kurs in diesem Kurstyp mit der Prio 9 gewählt haben (und ihn bekommen haben oder nicht)
        tn_alle_kurstypen:  # der TN in diesem Kurs (in allen Kurstypen)
--%>
<%@page import="java.util.Vector"%>
<%@page import="de.shj.UP.HTML.HtmlSeminar"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page contentType="text/json" pageEncoding="UTF-8" import="java.sql.ResultSet,de.shj.UP.data.Dozent,de.shj.UP.data.shjCore" session="true" isThreadSafe="false"  errorPage="../../error.jsp" %>
<jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" />
<jsp:useBean id="sd" scope="session" class="de.shj.UP.util.SessionData" />
<jsp:useBean id="seminar" scope="session" class="de.shj.UP.HTML.HtmlSeminar" />
<%@include file="../../../fragments/checkVersion.jsp" %>
<%@include file="../../../fragments/checkLogin.jsp" %>    
<%@include file="../../../fragments/checkAccess500.jsp" %>
<%! long lERR_BASE; HtmlSeminar sem;int iScaleMax=-1; %>
<%lERR_BASE=211000 + 100;sem=seminar;long lKurstypID=-1;long lKurstyp2_opt=-1;    // Anmeldung + Get
try{
    if(request.getParameter("kurstyp_id").indexOf(",")>0){
        String[] sKurstypen = request.getParameter("kurstyp_id").split(",");
        lKurstypID=Long.parseLong(sKurstypen[0]);
        lKurstyp2_opt=Long.parseLong(sKurstypen[1]);
    }else{
        lKurstypID=Long.parseLong(request.getParameter("kurstyp_id"));
    }
}catch(Exception eProblem){
    throw new Exception("{\"error\":\"Die Kursbelegung kann nicht angezeigt werden, ohne dass ein Kurstyp gewählt ist.\"" + 
                    ",\"errorDebug\":\"Der Parameter >kurstyp_id< (für den Kurstyp) hat den nicht-numerischen Wert " + request.getParameter("kurstyp_id") + ".\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":10}");
}
%>
<%!
    /**
     * Details zu den gewählten Kursen eines/r Studierenden in einem 
     * Kurstyp inkl. Gesamt-TN-Zahl und Überlappungsinfo.
     * 
     * Z.B. lässt sich für den Fall, dass ein Student in _keinen Kurs_ 
     * eingewählt wurde, so eine Liste der gewählten Kurse erstellen, 
     * mit Hinweisen, in welchem der Kurse der Studierende einen Platz 
     * bekommen sollte (welcher nämlich nicht mit schon gewählten 
     * Kursen überlappt, oder schon sehr voll ist, oder sehr gering 
     * priorisiert wurde).
     * 
     * KursID, KurstypID, Priorität, Zuschlag, Fixiert, Dozent, Termin sowie
     * tn_alle_kurstypen: # der Teilnehmer in diesem Kurs
     * has_overlaps: würde dieser Kurs mit einem anderen Kurs überlappen, in dem der Studierende einen Platz hat?
     */
    ResultSet getStudentDetails(String sMatrikelnummer, long lKurstypID, long lKurstypID2_opt)throws Exception{
    String sKurstypen = String.valueOf(((lKurstypID2_opt>0) ? (lKurstypID + "," + lKurstypID2_opt) : lKurstypID));
    return sem.sqlQuery(
            "SELECT a.\"lngKursID\", a.\"lngKurstypID\", " +
            "a.\"intAnmeldungPrioritaet\", a.\"blnAnmeldungZuschlag\", " +
            "a.\"blnAnmeldungFixiert\", d.\"strDozentNachname\", " +
            "k.\"strKursTag\", k.\"dtmKursBeginn\", " +
            "k.\"dtmKursEnde\", k.\"strKursTerminFreitext\"," +
            "(select count(\"strMatrikelnummer\") " +
                  "from \"tblBdAnmeldung\" a2 " +
                  "where a2.\"lngSdSeminarID\"=a.\"lngSdSeminarID\"  " +
                  "and a2.\"lngKursID\"=k.\"lngKursID\" " +
                  "and a2.\"blnAnmeldungZuschlag\"=true) as total_in_all_types, " +
            "(select count(*)>0 from \"tblBdAnmeldung\" a3, \"tblBdKurs\" k2 where  " +
                  "a3.\"lngSdSeminarID\"= a.\"lngSdSeminarID\" and  " +
                  "a3.\"strMatrikelnummer\"=a.\"strMatrikelnummer\" and  " +
                  "a3.\"lngKursID\" !=  k.\"lngKursID\" and  " +
                  "a3.\"blnAnmeldungZuschlag\"='t' and " +
                  "k2.\"lngKursID\"=a3.\"lngKursID\" and  " +
                  "k2.\"lngSdSeminarID\"=a3.\"lngSdSeminarID\" and " +
                  "k2.\"strKursTag\"=k.\"strKursTag\" and  " +
                  "(k.\"dtmKursBeginn\", k.\"dtmKursEnde\") overlaps (k2.\"dtmKursBeginn\", k2.\"dtmKursEnde\")) as has_overlaps " +
          "FROM \"tblBdKurs\" k, \"tblBdAnmeldung\" a, \"tblSdDozent\" d " +
          "WHERE " +
          "k.\"lngSdSeminarID\" = a.\"lngSdSeminarID\" AND " +
          "k.\"lngKursID\" = a.\"lngKursID\" AND " +
          "d.\"lngSdSeminarID\" = k.\"lngSdSeminarID\" AND " +
          "d.\"lngDozentID\" = k.\"lngDozentID\" AND " +
          "a.\"lngSdSeminarID\" = " + sem.getSeminarID() + " AND " +
          "a.\"strMatrikelnummer\" = '" + sMatrikelnummer + "' AND " +
          "a.\"lngKurstypID\" in (" + sKurstypen + ") " +
          "ORDER BY a.\"lngKurstypID\", a.\"intAnmeldungPrioritaet\" DESC;");
    }
    String getTermin(ResultSet r) throws Exception{
        String sReturn="";
        if(shjCore.normalize(r.getString("strKursTag")).length()>2){
            sReturn += r.getString("strKursTag").substring(0, 2) + " ";
        }
        if(shjCore.normalize(r.getString("dtmKursBeginn")).length()>5){
            sReturn += r.getString("dtmKursBeginn").substring(0, 5) + " ";
        }
        return sReturn;
    }
    String getBelegung(long lSeminarID, long lKurstypID){
    return "SELECT "
            + "d.\"strDozentNachname\", "
        + "(select count(a.\"strMatrikelnummer\") from \"tblBdAnmeldung\" a where "  
              + "a.\"lngSdSeminarID\" = t.\"lngSdSeminarID\" AND "
              + "a.\"blnAnmeldungZuschlag\"=true and "
              + "a.\"lngKursID\" = k.\"lngKursID\" AND "
              + "k.\"lngSdSeminarID\"=a.\"lngSdSeminarID\" and "
              + "a.\"lngSdSeminarID\" = " + lSeminarID + ") as total_in_all_types, "
        + "(select count(a.\"strMatrikelnummer\") from \"tblBdAnmeldung\" a where "
              + "a.\"lngSdSeminarID\" = t.\"lngSdSeminarID\" AND "
              + "a.\"blnAnmeldungZuschlag\"=true and "
              + "a.\"lngKurstypID\" = t.\"lngKurstypID\" AND "
              + "a.\"lngKursID\" = k.\"lngKursID\" AND "
              + "k.\"lngSdSeminarID\"=a.\"lngSdSeminarID\" and "
              + "a.\"lngSdSeminarID\" = " + lSeminarID + ") as total_in_type,"
       + "(select count(a.\"strMatrikelnummer\") from \"tblBdAnmeldung\" a where "  
              + "a.\"lngSdSeminarID\" = t.\"lngSdSeminarID\" AND "
              + "a.\"lngKurstypID\" = t.\"lngKurstypID\" AND "
              + "a.\"lngKursID\" = k.\"lngKursID\" AND "
              + "k.\"lngSdSeminarID\"=a.\"lngSdSeminarID\" and "
              + "a.\"blnAnmeldungFixiert\"=false and "
              + "a.\"intAnmeldungPrioritaet\"=9 and "
              + "a.\"lngSdSeminarID\" = " + lSeminarID + ") as wished_in_type,"
       + "(select count(a.\"strMatrikelnummer\") from \"tblBdAnmeldung\" a where "  
              + "a.\"lngSdSeminarID\" = t.\"lngSdSeminarID\" AND "
              + "a.\"blnAnmeldungZuschlag\"=true and "
              + "a.\"blnAnmeldungFixiert\"=false and "
              + "a.\"lngKurstypID\" = t.\"lngKurstypID\" AND "
              + "a.\"lngKursID\" = k.\"lngKursID\" AND "
              + "k.\"lngSdSeminarID\"=a.\"lngSdSeminarID\" and "
              + "a.\"intAnmeldungPrioritaet\"=9 and "
              + "a.\"lngSdSeminarID\" = " + lSeminarID + ") as wish_in_type,"
       + "(select count(a.\"strMatrikelnummer\") from \"tblBdAnmeldung\" a where "  
              + "a.\"lngSdSeminarID\" = t.\"lngSdSeminarID\" AND "
              + "a.\"blnAnmeldungZuschlag\"=true and "
              + "a.\"blnAnmeldungFixiert\"=true and "
              + "a.\"lngKurstypID\" = t.\"lngKurstypID\" AND "
              + "a.\"lngKursID\" = k.\"lngKursID\" AND "
              + "k.\"lngSdSeminarID\"=a.\"lngSdSeminarID\" and "
              + "a.\"intAnmeldungPrioritaet\"=9 and "
              + "a.\"lngSdSeminarID\" = " + lSeminarID + ") as fixiert_in_type,"
       + "(select count(a.\"strMatrikelnummer\") from \"tblBdAnmeldung\" a where "
              + "a.\"lngSdSeminarID\" = t.\"lngSdSeminarID\" AND "
              + "a.\"blnAnmeldungZuschlag\"=true and "
              + "a.\"blnAnmeldungFixiert\"=false and "
              + "a.\"lngKurstypID\" = t.\"lngKurstypID\" AND "
              + "a.\"lngKursID\" = k.\"lngKursID\" AND "
              + "k.\"lngSdSeminarID\"=a.\"lngSdSeminarID\" and "
              + "a.\"intAnmeldungPrioritaet\"<9 and "
              + "a.\"lngSdSeminarID\" = " + lSeminarID + ") as no_wish_in_type,"
        + "k.\"dtmKursBeginn\", "
        + "k.\"lngKursID\","
        + "k.\"strKursTag\", "
        + "k.\"strKursTitel\", " 
       + "t.\"lngKurstypID\", "
        + "t.\"strKurstypBezeichnung\" "
      + "FROM "
        + "\"tblBdKurs\" k, "
        + "\"tblSdKurstyp\" t, "
        + "\"tblSdDozent\" d,"
        + "\"tblBdKursXKurstyp\" x "
      + "WHERE "
        + "k.\"lngSdSeminarID\" = d.\"lngSdSeminarID\" AND "
        + "k.\"lngDozentID\" = d.\"lngDozentID\" AND "
        + "k.\"blnKursPlanungssemester\" = false AND "
        + "t.\"blnKurstypFormularanmeldung\"=true and "
        + "t.\"lngSdSeminarID\"=" + lSeminarID + " and "
            + "t.\"lngKurstypID\"=" + lKurstypID + " and "
        + "k.\"lngSdSeminarID\"=t.\"lngSdSeminarID\" and "
        + "d.\"lngSdSeminarID\"=t.\"lngSdSeminarID\" and "
        + "x.\"lngSeminarID\"=t.\"lngSdSeminarID\" and "
        + "x.\"lngKurstypID\"=t.\"lngKurstypID\" and "
        + "x.\"lngKursID\"=k.\"lngKursID\" "
        + "order by t.\"strKurstypBezeichnung\" asc, \"strDozentNachname\", k.\"lngKursID\";";
    }
%>[<%boolean bDetailRequest=false;boolean bFirst=true;ResultSet rKurse=null;
if(request.getParameter("detail_request")!=null){
    try{
        Long.parseLong(request.getParameter("matrikelnummer"));
    }catch(Exception eProblem){
        throw new Exception("{\"error\":\"Detailinformationen zur Wahl der Studierenden kann nur pro Matrikelnummer abgerufen werden.\"" + 
                    ",\"errorDebug\":\"Der Parameter >matrikelnummer< hat den nicht-numerischen Wert " + request.getParameter("matrikelnummer") + ".\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":10}");
    }
    rKurse=getStudentDetails(request.getParameter("matrikelnummer"), lKurstypID, lKurstyp2_opt);
    bDetailRequest=true;
}else{
    rKurse = user.sqlQuery(getBelegung(user.getSdSeminarID(), lKurstypID));
}
while(rKurse.next()){if(!bFirst) {out.write(",");}else{bFirst=false;}
%>
{"kurs":
    {"id":"<%=rKurse.getString("lngKursID")%>",
     "bezeichnung":"<%=rKurse.getString("strDozentNachname") + ", " + getTermin(rKurse) %>",
<%if(!bDetailRequest){%>     "tn_wunsch":"<%=rKurse.getString("wish_in_type") %>",
     "tn_kein_wunsch":"<%=rKurse.getString("no_wish_in_type") %>",
     "tn_fixiert":"<%=rKurse.getString("fixiert_in_type") %>",
     "nachfrage":"<%=rKurse.getString("wished_in_type") %>",<%}%>
<%if(bDetailRequest){%>
     "kurstyp_id":"<%=rKurse.getString("lngKurstypID")%>",
     "zuschlag":"<%=rKurse.getString("blnAnmeldungZuschlag") %>",
     "fixiert":"<%=rKurse.getString("blnAnmeldungFixiert") %>",
     "ueberlappung":"<%=rKurse.getString("has_overlaps") %>",
     <%}%>
     "tn_alle_kurstypen":"<%=rKurse.getString("total_in_all_types") %>"}
     }
<%}%>]
