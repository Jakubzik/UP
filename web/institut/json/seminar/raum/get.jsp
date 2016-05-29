<%--
    @Revision Oct 2014, hj
    
    JSP-Object "seminar/raum/get"

    Anzeige (freier) Räume. "Frei" heißt, dass 
    es in tblBdKursTermin zu den angefragten 
    Terminen für diesen Raum noch keinen 
    Eintrag mit "blnKursTerminZuschlag"=true 
    gibt.

    @TODO
    ===========================
    Variabel machen: sollte 
    - HACK: Regeltest (für skph) funktioniert nicht, 
      ist bloß halb anprogrammiert. Braucht review!
    - alle Räume des Seminars liefern können,
    - alle belegten Räume/freien Räume etc. etc.

    SYNOPSIS (German)
    ===========================
    2014, Oct 2, shj
    
    Üblicher Lifecycle: GET

    
    Expected SESSION PROPERTIES
    ===========================
    seminar        muss initialisiert sein (AccessLevel egal)

    Expected PARAMETER(S):
    ===========================
    datum_<i>:      Datum des zu speichernden Termins, ISO-Format
    start_<i>:      (H)H:MM oder (H)H.MM der Start- und
    stop_<i>:       Stopptermine
                    (z.B.: start_0=9.00, stop_0=9.10, start_1=9:10 ...)

    kurstyp_id:     OPTIONAL -- u.U. für Regelwerk der Raumvergabe 
                    relevant.

    Returns
    =======
    Array der Raumbezeichnungen
    ["R1","R2", ... ,"Rn"]
    der Räume, die zu keinem der übergebenen 
    Termine belegt sind

    Sample Usage
    ============

    
--%><%@page import="java.util.Calendar"%>
<%@page import="java.util.Date"%>
<%@page import="de.shj.UP.HTML.HtmlSeminar"%>
<%@page contentType="text/json" pageEncoding="UTF-8" import="java.sql.ResultSet,java.text.SimpleDateFormat, de.shj.UP.data.Dozent,de.shj.UP.data.shjCore" session="true" isThreadSafe="false"  errorPage="../../error.jsp" %>
<jsp:useBean id="seminar" scope="session" class="de.shj.UP.HTML.HtmlSeminar" />
<jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" />
<%@include file="../../../fragments/checkVersion.jsp" %>
<%@include file="../../../fragments/checkLogin.jsp" %> 
{"raeume":[
<%long lERR_BASE=216000 + 100;    // Raum + Get

    // HACK, @TODO: Das funktioniert noch nicht:
//    String sNoRoomReason=getRuleCheckResult(request,seminar);

    String sNoRoomReason=g_sRaumcheckDefault;
    String sSubquery="";
    if(sNoRoomReason.equals(g_sRaumcheckDefault)){
        for(int ii=0;true==true;ii++){
         if(request.getParameter("datum_" + ii)==null) break;
         sSubquery += "or ( t.\"dtmKursTerminDatum\"='" + request.getParameter("datum_" + ii) + 
                 "' and (t.\"dtmKursTerminStart\",t.\"dtmKursTerminStop\") "
                 + "overlaps ('" + java.sql.Time.valueOf(request.getParameter("start_" + ii).replace('.', ':') + ":00") + "'::time,'" + java.sql.Time.valueOf(request.getParameter("stop_" + ii).replace('.', ':')+ ":00") + "'::time)) ";
        }

        boolean bFirst=true;
        ResultSet rFreieRaeume=user.sqlQuery("SELECT " +
             "r.\"strRaumBezeichnung\" " +
           "FROM " +
             "\"tblSdRaum\" r " +
           "WHERE " +
              "r.\"lngSdSeminarID\"=" + user.getSdSeminarID() + " and " +
              "not exists (select \"strRaumBezeichnung\" from \"tblBdKursTermin\" t where " +
              "t.\"lngSdSeminarID\"=r.\"lngSdSeminarID\" and " +
              "t.\"blnKursTerminZuschlag\"=true and " +
              "t.\"strRaumBezeichnung\"=r.\"strRaumBezeichnung\" and " +
              "( " + sSubquery.substring(2)+
              "));");

        while(rFreieRaeume.next()){
            if(!bFirst) out.write(",");
            out.write("\"" + rFreieRaeume.getString("strRaumBezeichnung") + "\"");
            bFirst=false;
        }
    }
%>], "err":{"msg":"none","details":"<%=sNoRoomReason%>"}}
<%!
long lERR_BASE=216100;       //Raum get
final String g_sRaumcheckDefault="Alle Räume sind zu diesem Termin leider schon mit anderen Veranstaltungen belegt -- sorry :-(";

// Editable:
// Muss für die Einhaltung der Regeln eine 
// KurstypID als Parameter im Request angegeben 
// sein?
boolean bRuleCheckRequiresKurstypID=false;
/**
 * Prüft seminarspezifisches Regelwerk für die Raumvergabe
 */
String getRuleCheckResult(HttpServletRequest r, HtmlSeminar seminar)throws Exception{
    String sReturn=g_sRaumcheckDefault;
    if(bRuleCheckRequiresKurstypID){
        try{
            Long.parseLong(r.getParameter("kurstyp_id"));
        }catch(Exception eNoKurstypID){
            throw new Exception("{\"error\":\"Freie Räume können nicht bestimmt werden, sorry. Bitte informieren Sie den Systemverantwortlichen mit dem Wortlaut dieser Meldung.\"" + 
            ",\"errorDebug\":\"Der Parameter >kurstyp_id< hat den nicht-numerischen Wert >" + r.getParameter("kurstyp_id") + ". Entweder muss in /seminar/raum/get die " + 
                    "Option >bRuleCheckRequiresKurstypID< auf >false< gesetzt werden. Oder der Client muss die >kurstyp_id< übergeben.<\",\"errorcode\":" + lERR_BASE + ",\"severity\":100}"); 

        }
    }
    
    // Seminarspezifische Regeln:
    // Außer den Kurstypen "Vorlesung" (13, 12) dürfen 
    // keine Veranstaltungen Mo, Mi, Do zwischen 11-13 Uhr liegen
    if(Long.parseLong(r.getParameter("kurstyp_id"))!=13 && Long.parseLong(r.getParameter("kurstyp_id"))!=12){
        if(isTerminOn(r, Calendar.MONDAY, "11:00", "13:00")){
            sReturn="Sorry -- die Termine Mo, Mi und Do von 11-13 Uhr sind für Vorlesungen reserviert.";
        }
    }
    
    return sReturn;
}

/**
 * Testet, ob einer der übergebenen Termine 
 * mit dem durch iDayOfWeek, sStart und sStop 
 * übergebenen Zeitraum überlapt.
 * 
 * Bsp:
 * isTerminOn(request, Calendar.TUESDAY, "13:00", "19:00")
 * 
 * --> true, falls im request einer der Termine mit Di 13-19 Uhr 
 *     überlappt, falsch sonst.
 */
boolean isTerminOn(HttpServletRequest r, int iDayOfWeek, String sStart, String sStop)throws Exception{
    boolean bReturn=false;
    SimpleDateFormat ft = new SimpleDateFormat ("hh:mm");
    SimpleDateFormat ft_day = new SimpleDateFormat ("yyyy-MM-dd");
    
//  DEBUG    System.out.println("isTerminOn ... teste auf: " + iDayOfWeek + " zw. " + sStart + " und " + sStop + " ...");
    
    for(int ii=0;true==true;ii++){
        if(r.getParameter("datum_" + ii)==null || bReturn) break;
        
        // Is the day the relevant day?
        Calendar c = Calendar.getInstance();
        c.setTime(ft_day.parse(r.getParameter("datum_" + ii)));
        
        //  DEBUG System.out.println("... der " + r.getParameter("datum_" + ii) + " ist Wochentag Nr. " + c.get(Calendar.DAY_OF_WEEK));
        
        if(c.get(Calendar.DAY_OF_WEEK)==iDayOfWeek){
            // if so, do the time periods overlap?
            
            //  DEBUG System.out.println("...... Tag stimmt überein, teste Überlappung (" + sStart + "-" + sStop + ") mit (" + r.getParameter("start_" + ii) + "-" + r.getParameter("stop_" + ii) + ")");
            if(ft.parse(sStart).before(ft.parse(r.getParameter("stop_" + ii).replace('.', ':'))) && 
                    ft.parse(r.getParameter("start_" + ii).replace('.', ':')).before(ft.parse(sStop))){
                //  DEBUG System.out.println("......... MATCH! Exit!");
                bReturn=true;
            }
        }
    }
    return bReturn;
}
%>
