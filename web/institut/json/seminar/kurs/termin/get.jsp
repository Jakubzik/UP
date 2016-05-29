<%--
    JSP-Object "seminar/kurs/termin/get.jsp"
    @Revision: Sept 13, 2014 -- hj

    @TODO
    ===========================
    - 


    SYNOPSIS (German)
    ===========================
    2014, Sept 13, shj
    
    Terminserie abrufen (ggf. zu einem Kurs, 
    ggf. mit Alternativen).

    Bsp: "Terminserie" für Mo 11.15-12.45 werden 
    z.B. die Einzeltermine 
    1) 15.10.2014, 11.15-12.45,
    2) 21.10.2014, 11.15-12.45,
    3) 28.10.2014, 11.15-12.45,
    ...
    15) 1.2.2015, 11.15-12.45,
    16) 8.2.2015, 11.15-12.45
    
    ausgegeben. (Die Termine müssen nicht wöchentlich 
    sein, und es müssen auch nicht 16 sein, d.h. auch 
    Kurse mit zwei Sitzungen pro Woche und Blockseminare 
    werden auf diese Weise gespeichert).

    Es können außerdem pro Kurs mehere
    Terminserien gespeichert werden, so dass man 
    alternative Terminwünsche angeben kann (bzw. sogar 
    muss). Hierzu dient der Parameter termin_prio.
    
    
    Expected SESSION PROPERTIES
    ===========================
    user        muss initialisiert sein 

    Expected PARAMETER(S):
    ===========================

    kurs_id:        Id des Kurses


    Returns
    =======
    [
       {
           "kurs_id": 15013,
           "termine": [
               {
                   "prioritaet": 1,
                   "zuschlag": false,
                   "sitzungen": [
                       {
                           "nr": 1,
                           "datum": "2014-10-22",
                           "start": "12.15",
                           "stop": "13.45",
                           "raum": "333"
                       }
                   ]
               },
               {
                   "prioritaet": 2,
                   "zuschlag": true,
                   "sitzungen": [
                       {
                           "nr": 1,
                           "datum": "2014-10-22",
                           "start": "12.15",
                           "stop": "13.45",
                           "raum": "333"
                       }
                   ]
               }
           ]
       }
   ]
    Sample Usage
    ============
 
    
--%><%@page import="java.sql.Date"%>
<%@page import="de.shj.UP.data.shjCore"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page contentType="text/json" pageEncoding="UTF-8" import="java.sql.ResultSet,de.shj.UP.data.Dozent,de.shj.UP.util.PasswordHash" session="true" isThreadSafe="false"  errorPage="../../../error.jsp" %>
<jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean id="seminar" scope="session" class="de.shj.UP.HTML.HtmlSeminar" />
<%@include file="../../../../fragments/checkVersion.jsp" %>
<%@include file="../../../../fragments/checkLogin.jsp" %>
<%@include file="../../../../fragments/checkAccess1.jsp" %>
<%long lERR_BASE=215000 + 100;    // Kurstermin + Get
// =========== =========== =========== =========== =========== ===========
// Schnittstelle
// =========== =========== =========== =========== =========== ===========
int iKursID=-1;
try{
    iKursID=Integer.parseInt(request.getParameter("kurs_id").trim());
}catch(Exception eNotNumeric){
    throw new Exception("{\"error\":\"Die Termine zu diesem Kurs können nicht gespeichert werden. Es wurde keine Id des Kurses übergeben.\"" + 
                ",\"errorDebug\":\"Der Parameter >kurs_id< hat den nicht-numerischen Wert " + request.getParameter("kurs_id") + ".\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":100}");               
}

ResultSet rKursTermine=seminar.sqlQuery("SELECT \"lngSdSeminarID\", \"lngKursID\", \"intKursTerminPrioritaet\", \"intKursTerminNummer\", " +
       "\"strRaumBezeichnung\", \"dtmKursTerminDatum\", \"dtmKursTerminStart\", "+
       "\"dtmKursTerminStop\", \"blnKursTerminZuschlag\" " +
        "FROM \"tblBdKursTermin\" " + 
        "where \"lngSdSeminarID\"=" + seminar.getSeminarID() + " and \"lngKursID\"=" + iKursID + 
        " order by \"intKursTerminPrioritaet\", \"dtmKursTerminDatum\", \"dtmKursTerminStart\";");

%>[
    {
        "kurs_id": <%=iKursID%>,
        "termine": [<%

int iPrio=-1;
int iPrioAlt=-1;
boolean bEsExEinTermin=false;

// Durchlaufe die übergebenen Termine der Serie 
// und speichere sie einzeln in der Datenbank
while(rKursTermine.next()){
    bEsExEinTermin=true;
    iPrio=rKursTermine.getInt("intKursTerminPrioritaet");
    
    // Neue Priorität?
    if(iPrio!=iPrioAlt){
        
        // Ist nicht die erste?
        if(iPrioAlt>=0){
            out.write("]},");
        }
        
        iPrioAlt=iPrio;
        
        %>{"prioritaet": <%=iPrio %>,
                   "zuschlag": <%=rKursTermine.getBoolean("blnKursTerminZuschlag") %>,
                   "sitzungen": [
                       <%
    }else{out.write(",");}
    
    // Sitzung schreiben
    %>{
            "nr": <%=rKursTermine.getInt("intKursTerminNummer") %>,
            "datum": "<%=rKursTermine.getDate("dtmKursTerminDatum") %>",
            "start": "<%=rKursTermine.getTime("dtmKursTerminStart") %>",
            "stop": "<%=rKursTermine.getTime("dtmKursTerminStop") %>",
            "raum": "<%=rKursTermine.getString("strRaumBezeichnung") %>"
    }<%
}%>
<%-- Sitzungen etc. schließen --%>
                ]
<%if(bEsExEinTermin){%> 
            }
        ]
<%}%>
<%-- Kurs-Objekt und übergreifendes Array schließen --%>
  }
]