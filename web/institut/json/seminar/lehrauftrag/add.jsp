<%--
    JSP-Object "seminar/lehrauftrag/add"
    @Erzeugt: Mai, 2016 -- hj

    @TODO
    ===========================
    Klären, wie ein neues Set erzeugt werden soll/kann?
    CHECK set_datum und set_nr werden übergeben? Ist das gut?

    SYNOPSIS (German)
    ===========================  
    Fügt einen Lehrauftrag hinzu
    
    Expected SESSION PROPERTIES
    ===========================
    seminar     muss initialisiert sein
    user        muss initialisiert sein & Access Level >=500 haben

    Expected PARAMETER(S):
    ===========================
    semester    'Wintersemester 2015/2016', 'Sommersemester 2015' etc.
    dozent_id   <long>
    set_nr      <long>      Set ist ein Satz bzw. eine Version der LA des Semesters
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
    <bool> heißt: true gilt als TRUE, null, false, Fehlen des Parameters etc. als false.
                    die Angaben sind damit OPTIONAL.

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
<%long lERR_BASE=218000 + 400;    // Lehrauftrag + Add
    // Init
    long lDozentID  = -1;
    int iLA_SetNr   = -1;
    Date dSetDate   = null;
    String sKurstyp = "";
    int iSWS        = 0;
    float fHonorar  = 0;
    String sKostenB = "";
    String sKostenN = "";
            
    // =========================================================================
    // Schnittstelle
    // =========================================================================
    if(request.getParameter("semester")==null || request.getParameter("semester").length()<19){
        throw new Exception("{\"error\":\"Der Lehrauftrag kann nicht hinzugefügt werden, "
                + "ohne dass ein Semester angegeben wird.\"" + 
                ",\"errorDebug\":\"Der Parameter >semester< muss z.B. lauten "
                + ">Wintersemester 2018/2019<, lautet aber >" + 
                request.getParameter("semester") + "<\",\"errorcode\":" + 
                lERR_BASE + 1 + ",\"severity\":10}"); 
    }

    if(request.getParameter("bereich")==null || request.getParameter("bereich").length()<3){
        throw new Exception("{\"error\":\"Der Lehrauftrag kann nicht hinzugefügt werden, "
                + "ohne dass ein Bereich angegeben wird.\"" + 
                    ",\"errorDebug\":\"Der Parameter >bereich< muss mindestens zwei "
                + "Zeichen haben, lautet aber >" + request.getParameter("bereich") + 
                "<\",\"errorcode\":" + lERR_BASE + 1 + ",\"severity\":10}"); 
    }
    
    try{
        lDozentID = Long.parseLong(request.getParameter("dozent_id"));
    }catch(Exception eNoParam){
        throw new Exception("{\"error\":\"Der Lehrauftrag kann nicht hinzugefügt "
                + "werden, ohne dass ein(e) Lehrende(r) angegeben wird.\"" + 
                    ",\"errorDebug\":\"Der Parameter >dozent_id< muss numerisch sein, "
                + "lautet aber >" + request.getParameter("semester") + 
                "<\",\"errorcode\":" + lERR_BASE + 1 + ",\"severity\":10}"); 
    }
    
    try{
        iLA_SetNr = Integer.parseInt(request.getParameter("set_nr"));
    }catch(Exception eNoParam){
        throw new Exception("{\"error\":\"Der Lehrauftrag kann nicht hinzugefügt "
                + "werden, ohne dass eine Set-Nr. angegeben wird.\"" + 
                    ",\"errorDebug\":\"Der Parameter >set_nr< muss numerisch sein, "
                + "lautet aber >" + request.getParameter("set_nr") + 
                "<\",\"errorcode\":" + lERR_BASE + 1 + ",\"severity\":10}"); 
    }
    
    try{
        dSetDate = shjCore.g_ISO_DATE_FORMAT.parse(request.getParameter("set_datum"));
    }catch(Exception eFormat){
        throw new Exception("{\"error\":\"Der Lehrauftrag kann nicht hinzugefügt "
                + "werden, ohne dass ein Datum angegeben wird.\"" + 
                    ",\"errorDebug\":\"Der Parameter >set_datum< muss im ISO "
                + "Format übergeben werden, lautet aber >" + 
                request.getParameter("set_datum") + "<\",\"errorcode\":" + 
                lERR_BASE + 1 + ",\"severity\":10}"); 
    }
    
    if(request.getParameter("kurstyp")==null || request.getParameter("kurstyp").length()<4){
        throw new Exception("{\"error\":\"Der Lehrauftrag kann nicht hinzugefügt "
                + "werden, ohne dass ein Kurstyp oder Titel angegeben wird.\"" + 
                    ",\"errorDebug\":\"Der Parameter >kurstyp< muss z.B. lauten "
                + ">Proseminar Literaturwiss.<, lautet aber >" + 
                request.getParameter("kurstyp") + "<\",\"errorcode\":" + 
                lERR_BASE + 1 + ",\"severity\":10}"); 
    }
    
    sKurstyp = request.getParameter("kurstyp").trim();
    
    try{
        iSWS = Integer.parseInt(request.getParameter("sws"));
    }catch(Exception eNoParam){
        throw new Exception("{\"error\":\"Der Lehrauftrag kann nicht hinzugefügt "
                + "werden, ohne dass die Anzahl der Semesterwochenstunden (SWS) "
                + "angegeben wird.\"" + 
                    ",\"errorDebug\":\"Der Parameter >sws< muss numerisch sein, "
                + "lautet aber >" + request.getParameter("sws") + 
                "<\",\"errorcode\":" + lERR_BASE + 1 + ",\"severity\":10}"); 
    }
    
    try{
        fHonorar = Float.parseFloat(request.getParameter("honorar"));
    }catch(Exception eNoParam){
        throw new Exception("{\"error\":\"Der Lehrauftrag kann nicht hinzugefügt "
                + "werden, ohne dass die Vergütung (in Euro) angegeben wird.\"" + 
                    ",\"errorDebug\":\"Der Parameter >honorar< muss numerisch sein, "
                + "lautet aber >" + request.getParameter("honorar") + 
                "<\",\"errorcode\":" + lERR_BASE + 1 + ",\"severity\":10}"); 
    }
    
    if(request.getParameter("kostenstelle_bez")==null || request.getParameter("kostenstelle_bez").length()<4){
        throw new Exception("{\"error\":\"Der Lehrauftrag kann nicht hinzugefügt "
                + "werden, ohne dass eine Kostenstelle angegeben wird.\"" + 
                    ",\"errorDebug\":\"Der Parameter >kostenstelle_bez< muss z.B. "
                + "lauten >Aversum Anglistik<, lautet aber >" + 
                request.getParameter("kostenstelle_bez") + "<\",\"errorcode\":" + 
                lERR_BASE + 1 + ",\"severity\":10}"); 
    }

    if(request.getParameter("kostenstelle_nr")==null || request.getParameter("kostenstelle_nr").length()<4){
        throw new Exception("{\"error\":\"Der Lehrauftrag kann nicht hinzugefügt "
                + "werden, ohne dass eine Kostenstellennr. angegeben wird.\"" + 
                    ",\"errorDebug\":\"Der Parameter >kostenstelle_nr< muss z.B. "
                + "lauten >9240085<, lautet aber >" + 
                request.getParameter("kostenstelle_nr") + "<\",\"errorcode\":" + 
                lERR_BASE + 1 + ",\"severity\":10}"); 
    }
    
    sKostenB = request.getParameter("kostenstelle_bez").trim();
    sKostenN = request.getParameter("kostenstelle_nr").trim();
    
    boolean bBesch_in_HD    = request.getParameter("dozent_besch")==null || (!request.getParameter("dozent_besch").equals("true"));
    boolean bKuenstler      = request.getParameter("kuenstler")==null || (!request.getParameter("kuenstler").equals("true"));
    boolean bWeiterbildung  = request.getParameter("weiterbildung")==null || (!request.getParameter("weiterbildung").equals("true"));
    boolean bErgaenzung     = request.getParameter("ergaenzung")==null || (!request.getParameter("ergaenzung").equals("true"));
    
    // Hole Folgewert für laufende Nummer --------------------------------------
    PreparedStatement pssub = seminar.prepareStatement("select "
            + "max(\"intLehrauftragLfdNr\") maxilf from \"tblBdLehrauftrag\" "
            + "where " + 
            "\"lngSdSeminarID\"=? and \"strLehrauftragSemester\"=? and "
            + "\"intLehrauftragSetNummer\"=?;");
    int ij = 1;
    pssub.setLong(ij++, seminar.getSeminarID());
    pssub.setString(ij++, request.getParameter("semester").trim());
    pssub.setInt(ij++, iLA_SetNr);
    ResultSet rTmp = pssub.executeQuery();
    int iLfdNr = 1;
    if(rTmp.next()){
        iLfdNr = rTmp.getInt("maxilf")+1;
    }
    // Holte Folgewert für laufende Nummer -------------------------------------
    
    PreparedStatement pstm= seminar.prepareStatement("INSERT INTO \"tblBdLehrauftrag\"(" +
            "\"lngSdSeminarID\", \"strLehrauftragSemester\", \"lngDozentID\", \"intLehrauftragSetNummer\", " +
            "\"dtmLehrauftragSetVersion\", \"intLehrauftragLfdNr\", \"blnLehrauftragBeschaeftigtUniHD\", " +
            "\"strLehrauftragKurstyp\", \"intLehrauftragSWS\", \"dblLehrauftragVerguetung\", " +
            "\"blnLehrauftragErgaenzungLehrangebot\", \"blnLehrauftragWeiterbildung\", " +
            "\"blnLehrauftragKuensterSozialabgabe\", \"strLehrauftragKostenstelleBez\", " +
            "\"strLehrauftragKostenstelleNr\", \"strLehrauftragBereich\")" +
    "VALUES (?, ?, ?, ?, " +
            "?, ?, ?, " +
            "?, ?, ?, " +
            "?, ?, " +
            "?, ?, " +
            "?, ?);");
    
    int ii=1;
    pstm.setLong(ii++, seminar.getSeminarID());
    pstm.setString(ii++, request.getParameter("semester").trim());
    pstm.setLong(ii++, lDozentID);
    pstm.setInt(ii++, iLA_SetNr);
    pstm.setDate(ii++, new java.sql.Date(dSetDate.getTime()));
    pstm.setInt(ii++, iLfdNr);                                                   // Laufende Nummer: Datenbank?
    pstm.setBoolean(ii++, bBesch_in_HD);
    pstm.setString(ii++, sKurstyp);
    pstm.setInt(ii++, iSWS);
    pstm.setFloat(ii++, fHonorar);
    pstm.setBoolean(ii++, bErgaenzung);
    pstm.setBoolean(ii++, bWeiterbildung);
    pstm.setBoolean(ii++, bKuenstler);
    pstm.setString(ii++, sKostenB);
    pstm.setString(ii++, sKostenN);
    pstm.setString(ii++, request.getParameter("bereich").trim());
    
    int iRowsAffected=pstm.executeUpdate();

    if(iRowsAffected==0)
        throw new Exception("{\"error\":\"Der Lehrauftrag konnte nicht hinzugefügt werden, sorry..\"" + 
                    ",\"errorDebug\":\"Insert hat keine Zeile in der Datenbank verändert\",\"errorcode\":" + lERR_BASE + 1 + ",\"severity\":10}"); 

    if(iRowsAffected>1)
        throw new Exception("{\"error\":\"Ein schwerwiegendes Problem ist aufgetreten -- bitte informieren Sie den Systemadministrator.\"" + 
                    ",\"errorDebug\":\"Das Hinzufügen des Lehrauftrags hat mehr als eine Zeile verändert\",\"errorcode\":" + lERR_BASE + 1 + ",\"severity\":10}"); 

%>{"success":"true","lfd_nr":"<%=iLfdNr%>"}