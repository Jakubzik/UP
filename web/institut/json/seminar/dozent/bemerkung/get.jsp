<%--
    JSP-Object "seminar/dozent/bemerkung/get"
    @Revesion: Feb 24, 2016  -- hj 

    @TODO
    ===========================

    SYNOPSIS (German)
    ===========================
    2016, Feb 24, shj   erzeugt. 
    
    Üblicher Lifecycle: GET
    
    Expected SESSION PROPERTIES
    ===========================
    seminar             muss initialisiert sein,
    
    user                angemeldet: 
                        Zugriff auf Bemerkungen zu anderen Dozenten
                        erst ab AccessLevel > 500    

    Expected PARAMETER(S):
    ===========================
    dozent_id           [Optional: Id des Dozenten, zu dem Bemerkungen geliefert werden]

    wv                  [Optional: falls true werden alle Bemerkungen
                                    mit Wiedervorlagedatum in der Zukunft 
                                    geliefert.]

    Returns
    =======
    JSON-Array, sortiert nach Nachnamen
        [
            {
                "bemerkung": {
                    "dozent_id": "26",
                    "bemerkung_id": "21145",
                    "tag": "Verlängerung,Vertrag",
                    "text": "Vertrag 50% Forschung WissZeitVG Laufzeit 1.1.2016-31.12.2017.",
                    "datum": "12.10.2016"
                    "wiedervorlagedatum": "30.6.2017",
                }
            },
             ...
        ]    
--%><%@page contentType="text/json" pageEncoding="UTF-8" import="java.sql.ResultSet,de.shj.UP.data.Dozent,de.shj.UP.data.shjCore" session="true" isThreadSafe="false"  errorPage="../../../error.jsp" %>
<jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean id="seminar" scope="session" class="de.shj.UP.logic.SeminarData" />
<jsp:useBean id="sd" scope="session" class="de.shj.UP.util.SessionData" />
<%@include file="../../../../fragments/checkVersion.jsp" %>
<%  long lERR_BASE=201100 + 100;    // Dozent.Bemerkungen + Get%>
<%@include file="../../../../fragments/checkLogin.jsp" %>
[
<%      

// shjCore.g_GERMAN_DATE_FORMAT.parse(request.getParameter("datum"));
//    PreparedStatement pstm=student.prepareStatement(sSQL);int ii=1;
//    pstm.setLong(ii++, lAntragID);
//    pstm.setLong(ii++, student.getSeminarID());

    long lDozentID = -1;
    boolean bWiedervorlage = false;
    
    try{
        lDozentID = Long.parseLong(request.getParameter("dozent_id"));
    }catch(Exception eNoneHandedIn){ /*Ignore*/ }
    
    // Falls die Berechtigung nicht ausreicht, nur die "eigenen"
    // Bemerkungen zeigen:
    if(user.getDozentAccessLevel() < 500) lDozentID = user.getDozentID();
    
    bWiedervorlage = (request.getParameter("wv") != null);
    
    boolean bFirstLoop=true;
    String sSQL = "select * from \"tblBdDozentBemerkung\" where \"lngSdSeminarID\" = " +
            seminar.getSeminarID() + ((lDozentID>=0) ? " and \"lngDozentID\"=" + lDozentID : "") +
            (bWiedervorlage ? " and \"dtmDozentBemerkungWv\">=CURRENT_DATE" : "") + 
            " order by \"lngDozentID\", \"dtmDozentBemerkungWv\" desc, \"dtmDozentBemerkungDatum\";";
    
    ResultSet rBemerkung = seminar.sqlQuery(sSQL);
	
    while(rBemerkung.next()){
	if(!bFirstLoop) out.write(",");
	else bFirstLoop=false;
		
    	out.write("{\"bemerkung\":{\"dozent_id\":\"" + rBemerkung.getString("lngDozentID") + "\"," +
                    "\"bemerkung_id\":\"" + rBemerkung.getString("lngDozentBemerkungID") + "\"," +
                    "\"datum\":\"" + shjCore.g_GERMAN_DATE_FORMAT.format(rBemerkung.getDate("dtmDozentBemerkungDatum")) + "\"," +
                    "\"wiedervorlagedatum\":\"" + shjCore.g_GERMAN_DATE_FORMAT.format(rBemerkung.getDate("dtmDozentBemerkungWv")) + "\"," +
                    "\"text\":\"" + shjCore.string2JSON(rBemerkung.getString("strDozentBemerkungText")) + "\"," +
                    "\"tag\":\"" + shjCore.string2JSON(rBemerkung.getString("strDozentBemerkungTag")) + "\"}}");
    }
    rBemerkung.close();
%>]