<%--
    JSP-Object "seminar/lehrauftrag/update"
    @Erzeugt: Mai, 2016 -- hj

    @TODO
    ===========================
    Lfd.-Nr. veränderlich machen? (Braucht dann eine Logik zum 
    Umsortieren).

    Dozent austauschen?

    SYNOPSIS (German)
    ===========================  
    Ändert Details an einem Lehrauftrag
    
    Expected SESSION PROPERTIES
    ===========================
    seminar     muss initialisiert sein
    user        muss initialisiert sein & Access Level >=500 haben

    Expected PARAMETER(S):
    ===========================
    semester    'Wintersemester 2015/2016', 'Sommersemester 2015' etc.      }
    dozent_id   <long>                                                      }   Identifizieren den zu ändernden
    set_nr      <long>      Set ist ein Satz bzw. eine Version der LA des   }   Datensatz
                            Semesters                                       }
    lfd_nr      <long>      Laufende Nr. in diesem Set                      }

    set_datum   <ISO>       Datum dieses Sets. (Es kann pro Datum n Sets geben)
    dozent_besch<bool>      Dozent an der Uni HD beschäftigt?
    kurstyp     <text>      Kurstyp/Titel
    sws         <int>       Semesterwochenstunden des Lehrauftrags
    honorar     <float>     Vergütung für den Lehrauftrag
    kuenstler   <bool>      Muss Künstler-Sozialabgabe abgeführt werden?
    weiterbildung<bool>     Weiterbildungsangebot?
    ergaenzung  <bool>      Ergänzung des Lehrangebots?
    kostenstelle_bez <text> Bezeichnung der Kostenstelle     
    kostenstelle_nr <text>  Nummer der Kostenstelle [#HACK; change DB]
    bereich     <text>      Literaturwiss./Kulturwiss/etc., je nach Kultur

    ACHTUNG!
    <bool> heißt: true gilt als TRUE, false als FALSE, und Fehlen des Parameters etc. als "nicht ändern".
                    die Angaben sind damit NICHT OPTIONAL.

    Returns
    =======
    {"success":"true","lfd_nr":"<%=iLfdNr%>"}

    Sample Usage
    ============
 
    
--%><%@page import="java.sql.ResultSet"%>
<%@page import="de.shj.UP.data.shjCore"%><%@page import="java.util.Date"%><%@page import="java.sql.PreparedStatement"%><%@page contentType="text/json" pageEncoding="UTF-8" session="true" isThreadSafe="false"  errorPage="../../error.jsp" %>
<jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean id="seminar" scope="session" class="de.shj.UP.HTML.HtmlSeminar" />
<%@include file="../../../fragments/checkVersion.jsp" %>
<%@include file="../../../fragments/checkAccess500.jsp" %>
<%long lERR_BASE=218000 + 200;    // Lehrauftrag + Update
    // Init
    long lDozentID  = -1;
    long lLfdNr     = -1;
    int iLA_SetNr   = -1;
    Date dSetDate   = null;
    String sKurstyp = "";
    int iSWS        = -1;
    float fHonorar  = -1;
    String sKostenB = "";
    String sKostenN = "";
    String sBereich = "";
    
    // =========================================================================
    // Schnittstelle
    // =========================================================================
    if(request.getParameter("semester")==null || request.getParameter("semester").length()<19){
        throw new Exception("{\"error\":\"Der Lehrauftrag kann nicht geändert werden, "
                + "ohne dass ein Semester angegeben wird.\"" + 
                ",\"errorDebug\":\"Der Parameter >semester< muss z.B. lauten "
                + ">Wintersemester 2018/2019<, lautet aber >" + 
                request.getParameter("semester") + "<\",\"errorcode\":" + 
                lERR_BASE + 1 + ",\"severity\":10}"); 
    }

    if(request.getParameter("bereich")==null || request.getParameter("bereich").length()<3){
    }else{
        sBereich = request.getParameter("bereich").trim();
    }
    
    try{
        lDozentID = Long.parseLong(request.getParameter("dozent_id"));
    }catch(Exception eNoParam){
        throw new Exception("{\"error\":\"Der Lehrauftrag kann nicht geändert "
                + "werden, ohne dass ein(e) Lehrende(r) angegeben ist.\"" + 
                    ",\"errorDebug\":\"Der Parameter >dozent_id< muss numerisch sein, "
                + "lautet aber >" + request.getParameter("semester") + 
                "<\",\"errorcode\":" + lERR_BASE + 1 + ",\"severity\":10}"); 
    }
    
    try{
        lLfdNr = Integer.parseInt(request.getParameter("lfd_nr"));
    }catch(Exception eNoParam){
        throw new Exception("{\"error\":\"Der Lehrauftrag kann nicht geändert "
                + "werden, ohne dass seine lfd. Nr. angegeben wird.\"" + 
                    ",\"errorDebug\":\"Der Parameter >lfd_nr< muss numerisch sein, "
                + "lautet aber >" + request.getParameter("lfd_nr") + 
                "<\",\"errorcode\":" + lERR_BASE + 1 + ",\"severity\":10}"); 
    }
    
    try{
        dSetDate = shjCore.g_ISO_DATE_FORMAT.parse(request.getParameter("set_datum"));
    }catch(Exception eFormat){
        dSetDate = null;
    }
    
    if(request.getParameter("kurstyp")==null || request.getParameter("kurstyp").length()<4){
    }else{
        sKurstyp = request.getParameter("kurstyp").trim();
    }
    
    try{
        iSWS = Integer.parseInt(request.getParameter("sws"));
    }catch(Exception eNoParam){
        iSWS = -1;
    }
    
    try{
        fHonorar = Float.parseFloat(request.getParameter("honorar"));
    }catch(Exception eNoParam){
        fHonorar = -1;
    }
    
    if(request.getParameter("kostenstelle_bez")==null || request.getParameter("kostenstelle_bez").length()<4){
    }else{
        sKostenB = request.getParameter("kostenstelle_bez").trim();
    }

    if(request.getParameter("kostenstelle_nr")==null || request.getParameter("kostenstelle_nr").length()<4){
    }else{
        sKostenN = request.getParameter("kostenstelle_nr").trim();
    }
    
    /**
     * -1: nicht ändern,
     *  1: true
     *  0: false
     */
    int iBesch_in_HD    = (request.getParameter("dozent_besch")==null ? -1 : (request.getParameter("dozent_besch").equals("true") ? 1 : 0));
    int iKuenstler      = (request.getParameter("kuenstler")==null ? -1 : (request.getParameter("kuenstler").equals("true") ? 1 : 0));
    int iWeiterbildung  = (request.getParameter("weiterbildung")==null ? -1 : (request.getParameter("weiterbildung").equals("true") ? 1 : 0));
    int iErgaenzung     = (request.getParameter("ergaenzung")==null ? -1 : (request.getParameter("ergaenzung").equals("true") ? 1 : 0));
    
    PreparedStatement pstm= seminar.prepareStatement("UPDATE \"tblBdLehrauftrag\" " +
        "SET  " +
            (dSetDate==null ? "" : "\"dtmLehrauftragSetVersion\"=?, ") + 
            (iBesch_in_HD<0 ? "" : "\"blnLehrauftragBeschaeftigtUniHD\"=?, ") + 
            (sKurstyp.length()<4 ? "" : "\"strLehrauftragKurstyp\"=?, ") +
            (iSWS<0 ? "" : "\"intLehrauftragSWS\"=?, ") + 
            (fHonorar<0 ? "" : "\"dblLehrauftragVerguetung\"=?, ") + 
            (iErgaenzung<0 ? "" : "\"blnLehrauftragErgaenzungLehrangebot\"=?, ") +
            (iWeiterbildung<0 ? "" : "\"blnLehrauftragWeiterbildung\"=?, ") + 
            (iKuenstler<0 ? "" : "\"blnLehrauftragKuensterSozialabgabe\"=?, ") +
            (sKostenB.length()<3 ? "" : "\"strLehrauftragKostenstelleBez\"=?, ") + 
            (sKostenN.length()<3 ? "" : "\"strLehrauftragKostenstelleNr\"=?, ") +
            (sBereich.length()<3 ? "" : "\"strLehrauftragBereich\"=?, ") + 
                    "\"lngSdSeminarID\"=" + seminar.getSeminarID() + " " +          // Setze default, falls sonst keine Änderungen
            "WHERE \"lngSdSeminarID\"=? and \"strLehrauftragSemester\"=? and " + 
            "\"lngDozentID\"=? \"intLehrauftragSetNummer\"=? and \"intLehrauftragLfdNr\"=?;");
    
    int ii=1;
    if(dSetDate != null) pstm.setDate(ii++, new java.sql.Date(dSetDate.getTime()));
    if(iBesch_in_HD>=0) pstm.setBoolean(ii++, (iBesch_in_HD==1));
    if(sKurstyp.length()>3) pstm.setString(ii++, sKurstyp);
    if(iSWS>=0) pstm.setInt(ii++, iSWS);
    if(fHonorar>=0) pstm.setFloat(ii++, fHonorar);
    if(iErgaenzung>=0) pstm.setBoolean(ii++, (iErgaenzung==1));
    if(iWeiterbildung>=0) pstm.setBoolean(ii++, (iWeiterbildung==1));
    if(iKuenstler>=0) pstm.setBoolean(ii++, (iKuenstler==1));
    if(sKostenB.length()>=2) pstm.setString(ii++,sKostenB);
    if(sKostenN.length()>=2) pstm.setString(ii++, sKostenN);
    if(sBereich.length()>=2) pstm.setString(ii++, sBereich);
    
    pstm.setLong(ii++, seminar.getSeminarID());
    pstm.setString(ii++, request.getParameter("semester").trim());
    pstm.setLong(ii++, lDozentID);
    pstm.setInt(ii++, iLA_SetNr);
    pstm.setInt(ii++, (int)lLfdNr);
    
    int iRowsAffected=pstm.executeUpdate();

    if(iRowsAffected==0)
        throw new Exception("{\"error\":\"Der Lehrauftrag konnte nicht geändert werden, sorry..\"" + 
                    ",\"errorDebug\":\"Update hat keine Zeile in der Datenbank verändert\",\"errorcode\":" + lERR_BASE + 1 + ",\"severity\":10}"); 

    if(iRowsAffected>1)
        throw new Exception("{\"error\":\"Ein schwerwiegendes Problem ist aufgetreten -- bitte informieren Sie den Systemadministrator.\"" + 
                    ",\"errorDebug\":\"Das Ändern des Lehrauftrags hat mehr als eine Zeile verändert\",\"errorcode\":" + lERR_BASE + 1 + ",\"severity\":10}"); 

%>{"success":"true"}