<%--
    JSP-Object "seminar/dozent/termin/update"
    @Revision: Dec 16, 2013 -- hj

    SYNOPSIS (German)
    ===========================
    Nutzer können hier einen Termin buchen.

    (Nutzer heißt: nicht Dozent mit Rechten in SignUp,
    sondern 'externer' Nutzer, in HD mit UniID).

    D.h. der Termin muss 
    - vorher (per ./add.jsp) angelegt worden sein, 
    - darf noch nicht von einem Nutzer gebucht sein,
    - darf nicht vom Anbieter abgesagt sein.

    Der Nutzer muss für die Buchung angemeldet sein

    Achtung: zum Absagen des Termins bitte
    ./delete.jsp verwenden.

    Achtung: je nach Conf (fragments/conf_general.jsp)
    von bENFORCE_ONE_APPOINTMENT_PER_DAY wird 
    ein Fehler ausgelöst, wenn der eingeloggte 
    Nutzer einen zweiten Termin am gleichen Tag 
    zu buchen versucht.
    
    Expected SESSION PROPERTIES
    ===========================
    nutzer        muss initialisiert sein 
    (user kann null sein -- diese Fkt. ist auch 'von
     außen' benutzbar)

    Expected PARAMETER(S):
    ===========================
    datum:                  Datum des zu buchenden Termins, Deutsches Format [dd.MM.JJJJ]
    start:                  Startzeit des Termins, z.B. '14:30'
    bemerkung:              Textbeschreibung, die (.trim()) der Buchung beigefügt wird.
    remind_me: [true|else]  Will der Nutzer tags zuvor per E-Mail an den Termin erinnert werden?
    seminar_id [long]       Wer hat diesen Termin an-   
    dozent_id  [long]       geboten?

    Returns
    =======
    {"success": "true"}

    oder Fehler, wenn
    - nutzer nicht initialisiert oder
    - updateCount nicht 1 ist
 
    
--%><%@page import="java.sql.Array"%>
<%@page import="java.sql.Date"%>
<%@page import="de.shj.UP.data.shjCore"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page contentType="text/json" pageEncoding="UTF-8" import="java.sql.ResultSet,de.shj.UP.data.Dozent,de.shj.UP.util.PasswordHash" session="true" isThreadSafe="false"  errorPage="../../../error.jsp" %>
<jsp:useBean id="nutzer" scope="session" class="de.shj.UP.data.Nutzer" />
<%@include file="../../../../fragments/conf_general.jsp" %>
<%  final long lERR_BASE=208200; // Termin Update
    if(nutzer==null || nutzer.getNutzerUniID()==null) throw new Exception("Bitte erst anmelden");
    Long lSeminarID=Long.parseLong(request.getParameter("seminar_id"));
    Long lDozentID=Long.parseLong(request.getParameter("dozent_id"));
    Date dtmTerminDatum=new Date(shjCore.g_GERMAN_DATE_FORMAT.parse(request.getParameter("datum")).getTime());
    if(bENFORCE_ONE_APPOINTMENT_PER_DAY){
        PreparedStatement pst = nutzer.prepareStatement("select * from \"tblBdSprechstunde\" "+
            "where \"strNutzerUniID\"=? and "+
            "\"lngSdSeminarID\"=? and \"lngDozentID\" =? and \"dtmSprechstundeDatum\"=?");
        int jj=1;
        pst.setString(jj++, nutzer.getNutzerUniID());
        pst.setLong(jj++, lSeminarID);
        pst.setLong(jj++, lDozentID);
        pst.setDate(jj++, dtmTerminDatum);
        
        if(pst.executeQuery().next()) throw new Exception("{\"error\":\"Es kann pro Tag nur ein Termin gebucht werden -- bitte löschen Sie zuerst den gebuchten Termin.\"" + 
                    ",\"errorDebug\":\"Am " + request.getParameter("datum") + " wurde von diesem Nutzer bereits ein Termin gebucht.\",\"errorcode\":" + lERR_BASE + 5 + ",\"severity\":90}");               
    }
    PreparedStatement pstm = nutzer.prepareStatement("Update \"tblBdSprechstunde\" set "+
            "\"strNutzerUniID\"=?, \"strSprechstundeBemerkung\"=?, \"blnSprechstundeEmailReminder\"=?  "+
            "where \"lngSdSeminarID\"=? and \"lngDozentID\"=? and \"dtmSprechstundeDatum\"=? and "+
            "\"tmeSprechstundeStart\"=? and \"strNutzerUniID\" is null and \"blnSprechstundeAbgesagt\"=false");
    int ii=1;
    pstm.setString(ii++, nutzer.getNutzerUniID());
    pstm.setString(ii++, request.getParameter("bemerkung").trim());
    pstm.setBoolean(ii++, (request.getParameter("remind_me")!=null && request.getParameter("remind_me").equals("true")));
    pstm.setLong(ii++, lSeminarID);
    pstm.setLong(ii++, lDozentID);
    pstm.setDate(ii++, dtmTerminDatum);
    pstm.setTime(ii++, java.sql.Time.valueOf(request.getParameter("start") + ":00"));
    pstm.execute();

    if(pstm.getUpdateCount()!=1) throw new Exception("Sorry, die Buchung konnte nicht ausgeführt werden -- der Termin wurde nicht angeboten oder ist abgesagt.");
%>{"success":"true"}