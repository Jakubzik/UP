<%--
    JSP-Object "seminar/dozent/get"
    @Revesion: Feb 5, 2015  -- hj (fit für mobile App, also auch Studierende)
    @Revision: Nov 20, 2013 -- hj

    @TODO
    ===========================
    (Ggf?) mehr Daten liefern -- 
    E-Mail, Sprechstunde etc.

    SYNOPSIS (German)
    ===========================
    2013, Mar 5, shj   erzeugt. 
    2013, Dez 29, shj   überarbeitet
    2015, Feb 5, shj    für mobil, auch für Studierende zugänglich.
    
    Üblicher Lifecycle: GET
    (Achtung, es werden nur Lehrende mit blnLehrend==true gewählt)
    
    Expected SESSION PROPERTIES
    ===========================
    seminar             muss initialisiert sein,
    student oder user   angemeldet
    
    gruppen_id          <optional> ID der Gruppe 
                                    (z.B. Lehrstühle, Sprechstundenlisten...)
    dozent_details      <optional> liefere auch Zimmer, Telefon und Sprechstd.
    
    Studierende können diese Daten 
    erst seit Feb 2015 abrufen.

    Expected PARAMETER(S):
    ===========================
    keine möglich

    Returns
    =======
    JSON-Array, sortiert nach Nachnamen
        [
            {
                "dozent": {
                    "name": "Müller",
                    "vorname": "Moritz",
                    "titel": "Prof. Dr.",
                    "email": "moritz.mueller@signup-shj.de",
                    "id": "373"<                            // OPTIONAL
                    "telefon": "54 28 38",                  // OPTIONAL NUR FALS PARAM dozent_details!=null
                    "sprechstunde": "do 14-15",             // OPTIONAL
                    "zimmer": "210"
                    >
                }
            },
            {
                "dozent": {
                    "name": "Schulz",
                    "vorname": "Isabel",
                    "titel":"",
                    "email": "isabel_schulz@onlinehome.de",
                    "id": "380"
                }
            }, ...
        ]    
--%><%@page contentType="text/json" pageEncoding="UTF-8" import="java.sql.ResultSet,de.shj.UP.data.Dozent,de.shj.UP.data.shjCore" session="true" isThreadSafe="false"  errorPage="../../error.jsp" %>
<jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean id="seminar" scope="session" class="de.shj.UP.HTML.HtmlSeminar" />
<jsp:useBean id="sd" scope="session" class="de.shj.UP.util.SessionData" /><jsp:useBean id="student" scope="session" class="de.shj.UP.beans.config.student.StudentBean" />
<%@include file="../../../fragments/checkVersion.jsp" %>
<%  long lERR_BASE=201000 + 100;    // Dozent + Get
    if(sd.getSessionType().equals("student")){%>
<%@include file="../../../fragments/checkLoginStudent.jsp" %>    
<%}else{%>
<%@include file="../../../fragments/checkLogin.jsp" %>
<%}%>
{"dozenten":[
<%      
	boolean bFirstLoop=true;
        long lGruppeID=-1;
        String sSQLFrom         = "\"tblSdDozent\" d";
        String sSQLWhereGruppe  = "";
        if(request.getParameter("gruppen_id")!=null){
            try{
                lGruppeID=Long.parseLong(request.getParameter("gruppen_id"));
                sSQLFrom += ", \"tblBdGruppenHistory\" h ";
                sSQLWhereGruppe += " and h.\"lngSdSeminarID\"=" + seminar.getSeminarID() + 
                        " and h.\"lngSdDozentID\"=d.\"lngDozentID\" and h.\"lngSdGruppenID\"=" + lGruppeID;
            }catch(Exception eParamTypeWrong){
                throw new Exception("{\"error\":\"Die Personen der Gruppe >" + request.getParameter("gruppen_id") + "< können nicht abgerufen werden."
                        + " Die ID der Gruppe muss numerischen Wert haben. Es liegt offenbar ein Programmierfehler im Frontend vor.\"" + 
                            ",\"errorDebug\":\"Keine weitere Info.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":100}");               
            }
        }
        
        boolean bDetails=(request.getParameter("dozent_details")!=null);
	ResultSet rDozent=seminar.sqlQuery("select * from " + sSQLFrom + " where d.\"lngSdSeminarID\"=" + seminar.getSeminarID() + 
			" and d.\"blnDozentLehrend\"=true " + sSQLWhereGruppe + " order by \"strDozentNachname\";");
	
	while(rDozent.next()){
		if(!bFirstLoop) out.write(",");
		else bFirstLoop=false;
		
		out.write("{\"dozent\":{" + 
                    "\"name\":\"" + rDozent.getString("strDozentNachname") + "\"," +
                    "\"vorname\":\"" + rDozent.getString("strDozentVorname") + "\"," +
                    "\"titel\":\"" + rDozent.getString("strDozentTitel") + "\"," +
                    "\"email\":\"" + rDozent.getString("strDozentEmail") + "\"," +
                        (bDetails ? "\"telefon\":\"" + rDozent.getString("strDozentTelefon") + "\", " + 
                                "\"zimmer\":\"" + rDozent.getString("strDozentZimmer") + "\"," + 
                                "\"sprechstunde\":\"" + shjCore.string2JSON(rDozent.getString("strDozentSprechstunde")) + "\"," : "") +
                    "\"id\":\"" + rDozent.getString("lngDozentID") + "\"" +
                "}}");
	}
        rDozent.close();
%>]}