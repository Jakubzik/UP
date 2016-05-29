<%--
    JSP-Object "seminar/kurs/add"

    @Revsion
    ===========================
    Nov 4, 2014 -- hj

    @TODO
    ===========================
    Prüfen: bei AccessLevel < 500 
    muss dozent_id===user_id

    Mehr Parameter: Beschreibung, Kurswahl, Anmeldetermine etc. etc.

    Plausibilitätsprüfung: wird da ein Kurs 
    zum zweiten Mal hinzugefügt? Hat der 
    Dozent da schon überlappende Termine?

    Hinzufügen KursXKurstyp in Transaktion?

    ACHTUNG: trigger_check_raumbuchung achtet nicht auf       !
    blnPlanungssemester. THIS IS A TERRIBLE BUG!              !

    ACHTUNG: tblBdKursTermin hat keine referentielle In-      !
    tegrität zu tblBdKurs. Ist das genial oder b l ö d?       !
    Ich fürchte: blöd.                                        !

    SYNOPSIS (German)
    ===========================
    2013, Jun 7, shj    Erzeugt. 
    
    Üblicher Lifecycle: ADD

    Expected SESSION PROPERTIES
    ===========================
    seminar        muss initialisiert sein (AccessLevel egal),

    Expected PARAMETER(S):
    ===========================
    kurstyp_id:
    dozent_id:
    titel:
    titel_en:
    planungssemester:       [true für true, alles andere gilt als false]
    termin:                 [Kurztext]
    raum:                   [Kurztext]

    Returns
    =======
    Array of objects with remarks:
    "kurs":{
        "titel":<Bezeichnung des Kurstyps,               [Text]>,
        "titel_en":<engl. Bezeichnung des Kurstyps,      [Text]>,
        "id":<Id des Kurses (zus. mit SeminarID primär   [int]>
    }

--%>
<%@page contentType="text/json" pageEncoding="UTF-8" import="java.sql.PreparedStatement,java.sql.ResultSet,de.shj.UP.data.Dozent,de.shj.UP.data.shjCore" session="true" isThreadSafe="false"  errorPage="../../error.jsp" %>
<jsp:useBean id="seminar" scope="session" class="de.shj.UP.HTML.HtmlSeminar" />
<jsp:useBean id="student" scope="session" class="de.shj.UP.beans.config.student.StudentBean" />
<jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" />
<jsp:useBean id="sd" scope="session" class="de.shj.UP.util.SessionData" />
<%@include file="../../../fragments/checkVersion.jsp" %>
<%@include file="../../../fragments/checkAccess1.jsp" %>
{"kurs":{
<%long lERR_BASE=206000 + 400;    // Kurs + Add
    long lKursIDNeu = user.getNextID("lngKursID", "tblBdKurs", "\"lngSdSeminarID\"=" + user.getSdSeminarID());
    long lDozentID=-1;
    long lKurstypID=-1;
    boolean bPlanungssemester=request.getParameter("planungssemester")!=null && request.getParameter("planungssemester").equals("true");
    
    try{
        lDozentID=Long.parseLong(request.getParameter("dozent_id"));
    }catch(Exception eParamWrong){
        throw new Exception("{\"error\":\"Der Kurs konnte nicht hinzugefügt werden, sorry -- es fehlt die Angabe einer oder eines Lehrenden.\"" + 
                    ",\"errorDebug\":\"Der Parameter >dozent_id< hatte den nicht-numerischen Wert >" + request.getParameter("dozent_id") + "<\",\"errorcode\":" + lERR_BASE + 1 + ",\"severity\":100}"); 
    }
    
    try{
        lKurstypID=Long.parseLong(request.getParameter("kurstyp_id"));
    }catch(Exception eParamWrong){
        throw new Exception("{\"error\":\"Der Kurs konnte nicht hinzugefügt werden, sorry -- es fehlt die Angabe einer oder eines Kurstyps.\"" + 
                    ",\"errorDebug\":\"Der Parameter >kurstyp_id< hatte den nicht-numerischen Wert >" + request.getParameter("kurstyp_id") + "<\",\"errorcode\":" + lERR_BASE + 1 + ",\"severity\":100}"); 
    }
    PreparedStatement pstm= user.prepareStatement("INSERT INTO \"tblBdKurs\"(" + 
            "\"lngSdSeminarID\", \"lngKursID\", \"lngDozentID\", " + 
            "\"strKursTitel\", \"strKursTitel_en\", \"strKursTerminFreitext\", " + 
            "\"strKursRaumExtern1\", \"blnKursPlanungssemester\", \"strKursRaum\", \"strKursRaum2\") " + 
    "VALUES (?, ?, ?, ?, " +
            "?, ?, ?, ?, null, null);");
    int ii=1;
    pstm.setLong(ii++, user.getSdSeminarID());
    pstm.setLong(ii++, lKursIDNeu);
    pstm.setLong(ii++, lDozentID);
    pstm.setString(ii++, request.getParameter("titel").trim());
    pstm.setString(ii++, request.getParameter("titel_en").trim());
    pstm.setString(ii++, request.getParameter("termin").trim());
    pstm.setString(ii++, request.getParameter("raum").trim());
    pstm.setBoolean(ii++, bPlanungssemester);
    
    int iRowsAffected=pstm.executeUpdate();

    if(iRowsAffected==0)
        throw new Exception("{\"error\":\"Der Kurs konnte nicht hinzugefügt werden, sorry..\"" + 
                    ",\"errorDebug\":\"Update hat keine Zeile in der Datenbank verändert\",\"errorcode\":" + lERR_BASE + 1 + ",\"severity\":10}"); 

    if(iRowsAffected>1)
        throw new Exception("{\"error\":\"Ein schwerwiegendes Problem ist aufgetreten -- bitte informieren Sie den Systemadministrator.\"" + 
                    ",\"errorDebug\":\"Das Hinzufügen hat mehr als eine Zeile verändert\",\"errorcode\":" + lERR_BASE + 1 + ",\"severity\":10}"); 
    
    user.sqlExe("insert into \"tblBdKursXKurstyp\" (\"lngSeminarID\", \"lngKurstypID\", \"lngKursID\") values ("
            + user.getSdSeminarID() + "," + lKurstypID + "," + lKursIDNeu + ");");
  
%>"titel":"<%=request.getParameter("titel").trim()%>","titel_en":"<%=request.getParameter("titel_en").trim()%>","id":"<%=lKursIDNeu%>","planungssemester":"<%=(bPlanungssemester ? "true":"false") %>"
}}