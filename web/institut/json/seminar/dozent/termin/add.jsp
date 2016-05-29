<%--
    JSP-Object "seminar/dozent/termin/add.jsp"
    @Revision: Nov 20, 2013 -- hj

    @TODO
    ===========================
    Man könnte unterscheiden, FÜR WEN die 
    Termine angeboten werden: für Studierende,
    Externe, KollegInnen?

    SYNOPSIS (German)
    ===========================
    2013, Nov 30, shj
    
    Termine speichern, die zur online-Anmeldung
    (Sprechstunde) angeboten werden sollen.
    
    Expected SESSION PROPERTIES
    ===========================
    user        muss initialisiert sein 

    Expected PARAMETER(S):
    ===========================
    datum:          Datum des zu speichernden Termins, ISO-Format
    start_<i>:      (H)H:MM der Start- und
    stop_<i>:       Stopptermine
                    (z.B.: start_0=9:00, stop_0=9:10, start_1=9:10 ...)
    stornolink:     Ein Link zur Seite auf der der Termin 
                    storniert werden kann (für Erinnerungs E-Mails).
    [ort:            Ort für die Sprechstunde. Wird kein Ort per 
                    Parameter angegeben, wird der Name des Seminars 
                    und die Zimmernummer des angemeldeten "user" bzw.
                    Dozenten gesetzt.]

    Returns
    =======
    {
        "success": "true",
    }

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
<%long lERR_BASE=208000 + 400;    // Termin + Add
Date dtmTerminDatum=new Date(shjCore.g_ISO_DATE_FORMAT.parse(request.getParameter("datum")).getTime());
for(int ii=0;true==true;ii++){
    if(request.getParameter("stop_" + ii)==null) break;
    PreparedStatement pstm = seminar.prepareStatement("INSERT INTO \"tblBdSprechstunde\"("+
            "\"lngSdSeminarID\", \"lngDozentID\", "+
            "\"blnSprechstundeNurStudent\", \"dtmSprechstundeDatum\", "+
            "\"tmeSprechstundeStart\", \"tmeSprechstundeStop\", \"strSprechstundeOrt\", \"strSprechstundeStornolink\") "+
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?);");
    pstm.setLong(1, user.getSdSeminarID());
    pstm.setLong(2, user.getDozentID());
    pstm.setBoolean(3, false);
    pstm.setDate(4, dtmTerminDatum);
    pstm.setTime(5, java.sql.Time.valueOf(request.getParameter("start_" + ii) + ":00"));
    pstm.setTime(6, java.sql.Time.valueOf(request.getParameter("stop_" + ii) + ":00"));
    String sOrt ="";
    if(request.getParameter("ort")==null){
        sOrt = seminar.getSeminarName() + ", Raum " + user.getDozentZimmer();
    }else{
        sOrt = request.getParameter("ort");
    }
    pstm.setString(7, sOrt);
    pstm.setString(8, shjCore.normalize(request.getParameter("stornolink")));
    pstm.execute();
}
%>{"success":"true"}