<%--
    JSP-Object "seminar/kurs/termin/add.jsp"
    @Revision: Sept 12, 2014 -- hj

    @TODO
    ===========================
    - Vor der Buchung (per StoredProc?) Mehrfachbelegung 
      verhindern.

    - Der Kurs muss bereits existieren, das muss wohl 
      vom Frontend verhandelt werden?

    - Raum kann auch null sein. Ist das gut oder schlecht?

    - Bisher keine Restriktionen, dass man nur      !
      die Termine eigener Kurse eingeben könnte;    !
      für Berechtigte mit Level < 500 sollte man    !
      diese Restriktion wohl einführen.             !

    - Ist das Löschen der Terminserie dieser 
      Priorität OK? 

    - Das sollte als Transaktion ablaufen, sonst 
      sind die gespeicherten Termine möglicherweise 
      ein Mix aus alt und neu.

    - sollte das Backend die Semestertermine 
      überprüfen?


    SYNOPSIS (German)
    ===========================
    2014, Sept 12, shj
    
    Termine von Kursen speichern 
    (als Teil des Raumplans); vor dem Speichern 
    wird die Terminserie der übergebenen Priorität 
    komplett gelöscht! (Siehe auch "todo")

    Eigentlich werden Terminserien gespeichert, also 
    alle Termine im Semester. Das Zusammenstellen der 
    Termine obliegt dem Frontend (siehe aber 
    auch "todo").

    Da beim Buchen über "add" u.U. bereits gebuchte 
    Termine dieses Kurses gelöscht werden, müssen auch 
    Blocktermine erst im Frontend zusammengestellt und 
    dann in einem Rutsch gebucht werden.

    (u.U.: heißt: bereits fest gebuchte Termine werden 
    nur dann gelöscht, wenn der Parameter 'book_directly' 
    übergeben wird; ansonsten entsteht ein Fehler).

    Bsp: "Terminserie" für Mo 11.15-12.45 werden 
    z.B. die Einzeltermine 
    1) 15.10.2014, 11.15-12.45,
    2) 21.10.2014, 11.15-12.45,
    3) 28.10.2014, 11.15-12.45,
    ...
    15) 1.2.2015, 11.15-12.45,
    16) 8.2.2015, 11.15-12.45
    
    gespeichert. (Die Termine müssen nicht wöchentlich 
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

    ACHTUNG:        Die Datums/Start-Termine sollten in der richtigen 
                    Reihenfolge ans Backend übergeben werden.

    kurs_id:        Id des Kurses
    termin_prio:    Erstwunsch (1) oder zweit (2)?
!   book_directly:  (null/not null)                                             EXPERIMENTELL
                    Falls nicht null: der angefragte Termin wird hinzugefügt    EXPERIMENTELL
                    und sofort gebucht.                                         EXPERIMENTELL
    
    datum_<i>:      Datum des zu speichernden Termins, ISO-Format
    raum_<i>:       Raum
    start_<i>:      (H)H:MM der Start- und
    stop_<i>:       Stopptermine
                    (z.B.: start_0=9.00, stop_0=9.10, start_1=9:10 ...)

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
<jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean id="seminar" scope="session" class="de.shj.UP.logic.SeminarData" />
<%@include file="../../../../fragments/checkVersion.jsp" %>
<%@include file="../../../../fragments/checkLogin.jsp" %>
<%@include file="../../../../fragments/checkAccess1.jsp" %>
<%long lERR_BASE=215000 + 400;    // Kurstermin + Add

//System.out.println(seminar.getParameterDebug(request));

// =========== =========== =========== =========== =========== ===========
// Schnittstelle
// =========== =========== =========== =========== =========== ===========
int iTerminPrioritaet=100;
boolean bBookDirect=false;
try{
    iTerminPrioritaet=Integer.parseInt(request.getParameter("termin_prio").trim());
}catch(Exception eNotNumeric){
    throw new Exception("{\"error\":\"Die Termine zu diesem Kurs können nicht gespeichert werden. Es wurde keine Priorität übergeben.\"" + 
                ",\"errorDebug\":\"Der Parameter >termin_prio< (für die Priorisierung dieses Terminvorschlags) hat den nicht-numerischen Wert " + request.getParameter("termin_prio") + ".\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":100}");               
}

if(request.getParameter("book_directly")!=null) bBookDirect=true;

if(bBookDirect && (iTerminPrioritaet!=1)){
        throw new Exception("{\"error\":\"Die Termine zu diesem Kurs können nicht gespeichert werden. Der Termin gilt nicht als " + 
                "Erstwunsch, soll aber direkt gebucht werden. Der Fehler ist vermutlich nur von einem Administrator zu beheben, " +
                "da es sich offenbar um eine Fehlkonfiguration des Servers handelt. Bitte informieren Sie die Technik/EDV über " +
                "den genauen Wortlaut dieser Fehlermeldung\"" +
                ",\"errorDebug\":\"Der Parameter >termin_prio< hat den Wert " + request.getParameter("termin_prio") + ", gleichzeitig ist >book_directly< nicht null sondern " + request.getParameter("book_directly") + ".\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":100}");               

}

int iKursID=-1;
try{
    iKursID=Integer.parseInt(request.getParameter("kurs_id").trim());
}catch(Exception eNotNumeric){
    throw new Exception("{\"error\":\"Die Termine zu diesem Kurs können nicht gespeichert werden. Es wurde keine Id des Kurses übergeben.\"" + 
                ",\"errorDebug\":\"Der Parameter >kurs_id< hat den nicht-numerischen Wert " + request.getParameter("kurs_id") + ".\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":100}");               
}

// Wenn 'book_directly' angegeben ist, werden die bisherigen 
// Buchungen zu diesem Kurs gelöscht. 
// Ist 'book_directly' _nicht_ gewünscht, und es sind zur 
// gegebenen Priorität schon _feste_ Buchungen vorhanden, 
// wird ein Fehler ausgelöst.
if(!bBookDirect){
    if(seminar.dbCount("lngKursID", "tblBdKursTermin", "\"lngSdSeminarID\"=" + seminar.getSeminarID() + " and " 
        + "\"lngKursID\"=" + iKursID + " and "
        + "\"intKursTerminPrioritaet\"=" + iTerminPrioritaet + " and "
        + "\"blnKursTerminZuschlag\"=true")>0){
         throw new Exception("{\"error\":\"Die Termine zu diesem Kurs können nicht gespeichert werden, es liegen in dieser Priorisierung schon " +
                 "feste Buchungen vor.\"" + 
                ",\"errorDebug\":\"Um die Buchung direkt auszuführen, muss der Parameter book_directly übergeben werden; oder die Buchung muss zunächst gelöscht werden.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":100}");               
    }
}

// Lösche alle bisher gespeicherten Terminwünsche zu 
// diesem Kurs mit dieser Priorität, die nicht schon 
// gebucht sind.
seminar.sqlExe("delete from \"tblBdKursTermin\" where "
        + "\"lngSdSeminarID\"=" + seminar.getSeminarID() + " and "
        + "\"lngKursID\"=" + iKursID + " and "
        + "\"intKursTerminPrioritaet\"=" + 
        iTerminPrioritaet + ((bBookDirect) ? ";" : " and (\"blnKursTerminZuschlag\" is null or \"blnKursTerminZuschlag\"=false)"));

String sRaum="";

// Durchlaufe die übergebenen Termine der Serie 
// und speichere sie einzeln in der Datenbank,
// entweder priorisiert als Wunsch, oder (falls bBookDirect) 
// gleich als feste Raumbuchung.
for(int ii=0;true==true;ii++){
    if(request.getParameter("datum_" + ii)==null) break;
    PreparedStatement pstm = seminar.prepareStatement("INSERT INTO \"tblBdKursTermin\"("+
            "\"lngSdSeminarID\", \"lngKursID\", "+
            "\"intKursTerminPrioritaet\", \"intKursTerminNummer\", "+
            "\"strRaumBezeichnung\", \"dtmKursTerminDatum\", \"dtmKursTerminStart\", \"dtmKursTerminStop\" " + ((bBookDirect) ? ",\"blnKursTerminZuschlag\"" : "") + ") "+
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?" + ((bBookDirect) ? ",?" : "") + ");");
    sRaum=request.getParameter("raum_" + ii);
    if(sRaum.length()>5) sRaum=sRaum.substring(0,4);
    pstm.setLong(1, user.getSdSeminarID());
    pstm.setLong(2, iKursID);
    pstm.setLong(3, iTerminPrioritaet);
    pstm.setLong(4, (ii+1));
    pstm.setString(5, sRaum);
    pstm.setDate(6, new java.sql.Date( shjCore.g_ISO_DATE_FORMAT.parse(request.getParameter("datum_" + ii)).getTime()));
    pstm.setTime(7, java.sql.Time.valueOf(request.getParameter("start_" + ii).replace('.', ':') + ":00"));
    pstm.setTime(8, java.sql.Time.valueOf(request.getParameter("stop_" + ii).replace('.', ':') + ":00"));
    if(bBookDirect) pstm.setBoolean(9, true);
    long lUpdateCount=pstm.executeUpdate();
    if(lUpdateCount!=1){
        throw new Exception("{\"error\":\"Die Terminbuchung hat nicht genau einen Datensatz (sondern " + lUpdateCount + ") betroffen. Gibt es vielleicht eine gleichzeitige Buchung?\"" + 
                ",\"errorDebug\":\"Die Ursache ist unklar -- sagen die Logfiles der Datenbank etwas aus?.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":100}");               

    };
}

// if(true==true){out.write("{\"success\":\"true\"}"); return;}
%>{"success":"true"}