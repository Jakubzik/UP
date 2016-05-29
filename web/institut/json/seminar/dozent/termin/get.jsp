<%--
    JSP-Object "seminar/dozent/termin/get"
    @Revision: Nov 20, 2013 -- hj

    SYNOPSIS (German)
    ===========================
    2013, Nov 30, shj
    2014, Jan 10, shj:  leere Terminliste lieferte kein
                        korrektes JSON.
    
    Auflistung der zukünftigen Termine und 
    Terminbuchungen des angemeldeten Dozenten 
    als Array.
    
    Expected SESSION PROPERTIES
    ===========================
    user        muss initialisiert sein 

    Expected PARAMETER(S):
    ===========================

    Returns
    =======
    Im Beispiel werden zwei Termine zurückgegeben, beide am 1.1.2014.
    Der erste Termin (von 10:00-10:30 Uhr) wurde von Dr. Jakubzik
    gebucht. Der zweite Termin (10:30-11:00 Uhr) wurde nicht 
    gebucht (es gibt also kein sprechstunde.nutzer Objekt).

    Termine können "abgesagt" sein, falls sie (1) von einem Nutzer
    gebucht und (2) später vom Anbieter abgesagt wurden.
    [
    {
        "datum": "01.01.2014",
        "intervalle": [
            {
                "sprechstunde": {
                    "start": "10:00",
                    "stop": "10:30",
                    "abgesagt": "false",
                    "ort": "Anglistisches Seminar, Kettengasse 12, 69117 Heidelberg"
                    "stornolink": "http://www.buchung-storno.de",
                    "nutzer": {
                        "uni_id": "mg123",
                        "titel": "Dr.",
                        "vorname": "Heiko",
                        "nachname": "Jakubzik",
                        "email": "heiko.jakubzik@as.uni-heidelberg.de",
                        "telefon": "542838",
                        "bemerkung": "Kurze Beschreibung",
                        "institut": "Germanistik"
                    }
                }
            },
            {
                "sprechstunde": {
                    "start": "10:30",
                    "stop": "11:00",
                    "abgesagt": "false"
                }
            }
        ]
    }
    ]
    Sample Usage
    ============
    var termine=[];
    termine = "get ./get.jsp"

    for(var ii=0;ii<termine.length;ii++){
      for(var jj=0; jj<termine[ii].intervalle.length;jj++){
        alert(termine[ii].intervalle[jj].sprechstunde.start);
        alert(termine[ii].datum);
        alert(termine[ii].intervalle[jj].sprechstunde.nutzer.email);
      }
    }
--%><%@page import="java.sql.Date"%>
<%@page import="de.shj.UP.data.shjCore"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page contentType="text/json" pageEncoding="UTF-8" import="java.sql.ResultSet,de.shj.UP.data.Dozent,de.shj.UP.util.PasswordHash" session="true" isThreadSafe="false"  errorPage="../../../error.jsp" %>
<jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean id="seminar" scope="session" class="de.shj.UP.HTML.HtmlSeminar" />
<%@include file="../../../../fragments/checkVersion.jsp" %>
<%@include file="../../../../fragments/checkLogin.jsp" %>
<%@include file="../../../../fragments/checkAccess1.jsp" %>
[<%long lERR_BASE=208000 + 400;    // Termin + Add

// Nur zukünftige Termine auswählen:
boolean bShowOnlyFutureDates=true;

ResultSet rTermine=seminar.sqlQuery("SELECT \"lngSdSeminarID\", \"lngDozentID\", \"tblBdSprechstunde\".\"strNutzerUniID\", \"blnSprechstundeAbgesagt\", \"strMatrikelnummer\", " +
       "\"strSprechstundeBemerkung\", \"blnSprechstundeNurStudent\", \"dtmSprechstundeDatum\", " +
       "\"tmeSprechstundeStart\", \"tmeSprechstundeStop\", \"strSprechstundeOrt\",\"strSprechstundeStornolink\"," +
       "\"strNutzerVorname\", \"strNutzerNachname\", \"strNutzerTitel\", " +
       "\"strNutzerInstitut\", \"strNutzerEmail\", \"strNutzerTelefon\" " +
  "FROM \"tblBdSprechstunde\" left outer join \"tblSdNutzer\" on \"tblBdSprechstunde\".\"strNutzerUniID\"=\"tblSdNutzer\".\"strNutzerUniID\" where " +
        "\"lngSdSeminarID\"=" + user.getSdSeminarID() + 
        " and \"lngDozentID\"=" + user.getDozentID() + " " +
        ((bShowOnlyFutureDates) ? " and \"dtmSprechstundeDatum\">=CURRENT_DATE " : "") + 
        "order by \"dtmSprechstundeDatum\", \"tmeSprechstundeStart\"");

Date dTmp=null;
Date dTmpAlt=null;
boolean bStart=true;
boolean bStartIntervall=true;
while(rTermine.next()){
    dTmp = rTermine.getDate("dtmSprechstundeDatum");
    if(!dTmp.equals(dTmpAlt)){
        if(!bStart) out.write("]},");
        //else out.write(",");
        bStart=false;
        bStartIntervall=true;
        out.write("{\"datum\":\"" + shjCore.g_GERMAN_DATE_FORMAT.format(dTmp) + "\",\"intervalle\":[");
        dTmpAlt=dTmp;
    }
    if(bStartIntervall) bStartIntervall=false;
    else out.write(",");
    out.write("{\"sprechstunde\":"
            + "{\"start\":\"" + rTermine.getTime("tmeSprechstundeStart").toString().substring(0,5) + "\","
            + "\"ort\":\"" + shjCore.normalize(rTermine.getString("strSprechstundeOrt")) + "\","
            + "\"stornolink\":\"" + shjCore.normalize(rTermine.getString("strSprechstundeStornolink")) + "\","
            +  "\"stop\":\"" + rTermine.getTime("tmeSprechstundeStop").toString().substring(0,5) + "\","
            +  "\"abgesagt\":\"" + (rTermine.getBoolean("blnSprechstundeAbgesagt") ? "true" : "false") + "\""
            +  ((rTermine.getString("strNutzerUniID")==null) ? "" : ",\"nutzer\":{"
                    + "\"uni_id\":\"" +  rTermine.getString("strNutzerUniID") + "\"," 
                    + "\"titel\":\"" +  rTermine.getString("strNutzerTitel") + "\"," 
                    + "\"vorname\":\"" +  rTermine.getString("strNutzerVorname") + "\"," 
                    + "\"nachname\":\"" +  rTermine.getString("strNutzerNachname") + "\"," 
                    + "\"email\":\"" +  rTermine.getString("strNutzerEmail") + "\"," 
                    + "\"telefon\":\"" +  rTermine.getString("strNutzerTelefon") + "\"," 
                    + "\"bemerkung\":\"" + string2JSON(rTermine.getString("strSprechstundeBemerkung")) + "\","
                    + "\"institut\":\"" +  string2JSON(rTermine.getString("strNutzerInstitut")) + "\"}") 
            + "}}");
    }

%>]<%if(!bStart) out.write("}]");%>
<%!
String string2JSON(String s){
    return (s==null) ? "" : s.replace('\n', ' ').replace('\r', ' ').replace('\t', ' ').replace('\"', '\''); 
}
%>