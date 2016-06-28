<%--
    @TODO
    ===========================
    

    SYNOPSIS (German)
    ===========================
    2013, Feb 1, shj
    Erzeugt. 
    
    Lifecycle UPDATE -- unüblich

    Die Anmeldung wird zum Schein umgewandelt (d.h. 
    es wird eine Note und ein Ausstellungsdatum angegeben).

    Die Schnittstelle wird überprüft, dann wird an 
    "leistung/add" übergeben.
    
    Expected SESSION PROPERTIES
    ===========================
    [user        muss initialisiert sein, falls Archiv-Anmeldung erzeugt werden soll]
    student     muss identische Matrikelnummer haben, die auch
                als Parameter übergeben wird.

    Expected PARAMETER(S):
    ===========================
    siehe leistung/add

    signup-expected-backend-version [text], vgl. fragments/checkVersion.jsp
    
    Returns
    =======
    siehe leistung/add

    Fehler/Errors/THROWS
    ====================

    
--%>        
<%@page import="de.shj.UP.data.Kurs"%>
<%@ page contentType="text/json" pageEncoding="UTF-8" import="de.shj.UP.data.Dozent,de.shj.UP.data.shjCore" session="true" isThreadSafe="false" errorPage="../../error.jsp"%><jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean id="seminar" scope="session" class="de.shj.UP.logic.SeminarData" /><jsp:useBean id="student" scope="session" class="de.shj.UP.logic.StudentData" />
<jsp:useBean id="sd" scope="session" class="de.shj.UP.util.SessionData" /><% long lERR_BASE=102000 + 200;    // Anmeldung + Update%>
<%@include file="../../../fragments/checkVersion.jsp" %>
<%@include file="../../../fragments/checkInitStudent.jsp" %>
<%
    // ==============================================================
    // Schnittstelle: Parameter
    // ==============================================================
    long lKursID=-1;            // Parameter
    long lKurstypID=-1;         // Parameter
    long lLeistungID=-1;        // Parameter
    String sModulID="";         // Parameter
    boolean bArchiv=false;      // Parameter
    boolean bIsStudent = user.getDozentNachname()==null || sd.getSessionType().equals("student");
    


    // ==============================================================
    // Schnittstelle: Berechtigungen, Sitzung
    // ==============================================================
    // Für Anmeldungen aus dem Archiv (um z.B. eine 
    // Leistung auszustellen) wird
    // - das Login durch einen Dozenten,
    // - für Anmeldungen zu Kursen anderer 
    //   eine Berechtigung > 500
    // benötigt
    long lDozentID=-1;
    try{
        lDozentID=Long.parseLong(request.getParameter("aussteller_id"));
    }catch(Exception eNotHandedOverIgnore){}
    if(bArchiv){
        if(bIsStudent)
            throw new Exception("{\"error\":\"Die Anmeldung konnte nicht gespeichert werden.\",\"errorDebug\":\"Mit studentischem Login können keine Anmeldungen aus dem Archiv hinzugefügt werden.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":100}");
                       
        if(user.getDozentID()!=lDozentID && user.getDozentAccessLevel()<500)
            throw new Exception("{\"error\":\"Die Anmeldung konnte nicht gespeichert werden.\",\"errorDebug\":\"Um Anmeldungen zu Kursen anderer Lehrender zu speichern ist eine höhere Berechtigung notwendig.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":80}");
    }
    
    // ==============================================================
    // Ausführung
    // ==============================================================
%><jsp:forward page="../leistung/update.jsp" />
