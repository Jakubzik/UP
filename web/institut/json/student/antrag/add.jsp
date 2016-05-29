<%--
    JSP-Object "student/antrag/add"

    SYNOPSIS (German)
    ===========================
    2013, May 20, shj       erzeugt. 
    2013, Dez 26, shj       überarbeitet (SQL Injection)

    Üblicher Lifecycle: ADD

    Speichert einen neuen Antrag und liefert 
    dessen ID zurück. Es wird auch ein passender 
    Status erzeugt.
    
    Expected SESSION PROPERTIES
    ===========================
    student     muss angemeldet sein

    Expected PARAMETER(S):
    ===========================
    matrikelnummer  [long], passend zur Sitzung
    typ_id          [long], siehe signup-faculty-common.js:             
                                "iTYP_ANTRAG_TRANSKRIPT_DE":1,
                                "iTYP_ANTRAG_TRANSKRIPT_EN":2,
                                "iTYP_ANTRAG_TRANSKRIPT_UNMODULAR":3
    antrag          [text], Klartext Beschreibung des Antrags, z.B. "Deutsches Transkript"

    signup-expected-backend-version [text], vgl. fragments/checkVersion.jsp    
    Returns
    =======
    Object:
    {"success":"true", "antrag_id":'id'}
--%> <%@page import="java.sql.PreparedStatement"%>
<%@ page contentType="text/json" pageEncoding="UTF-8" import="java.sql.ResultSet" session="true" isThreadSafe="false" errorPage="../../error.jsp"%><jsp:useBean id="seminar" scope="session" class="de.shj.UP.HTML.HtmlSeminar" /><jsp:useBean id="student" scope="session" class="de.shj.UP.beans.config.student.StudentBean" /><% long lERR_BASE=106000 + 400;    // Antrag + Add
if(request.getParameter("antrag")==null || request.getParameter("antrag").trim().equals("")) throw new Exception("{\"error\":\"Die Antrag ist leer und konnte nicht gespeichert werden.\",\"errorDebug\":\"Der erforderliche Parameter >antrag< ist null oder leer.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":20}");%>
<%if(request.getParameter("typ_id")==null || request.getParameter("typ_id").trim().equals("")) throw new Exception("{\"error\":\"Die Antrag ist nicht typisiert und konnte nicht gespeichert werden.\",\"errorDebug\":\"Der erforderliche Parameter >typ_id< ist null oder leer.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":20}");%>
<%@include file="../../../fragments/checkVersion.jsp" %>
<%@include file="../../../fragments/checkLoginStudent.jsp" %>
<%@include file="../../../fragments/conf_general.jsp" %>
<%   // ================================================== ===========================================
    // Schnittstelle: wenn ein offener Antrag gleichen Typs noch vorliegt: Fehler auslösen
    // ================================================== ===========================================
    ResultSet rAntraege=seminar.sqlQuery("SELECT " +
            "a.\"lngSdSeminarID\", " +
            "a.\"strMatrikelnummer\", " +
            "a.\"lngStudentAntragID\", " + 
            "a.\"lngStudentAntragTypID\" " +
          "FROM " +
            "\"tblBdStudentAntrag\" a " +
          "WHERE " +
            "a.\"lngSdSeminarID\"=" + student.getSeminarID() + " and " +
            "a.\"lngStudentAntragTypID\"=" + Long.parseLong(request.getParameter("typ_id")) + " and " +
            "a.\"strMatrikelnummer\"='" + Long.parseLong(request.getParameter("matrikelnummer")) + "' and "  +
            "not exists(select * from \"tblBdStudentAntragStatus\" s where (" +
                "a.\"lngStudentAntragID\" = s.\"lngStudentAntragID\" AND " + 
                "a.\"strMatrikelnummer\" = s.\"strMatrikelnummer\" AND " + 
                "a.\"lngSdSeminarID\"=s.\"lngSdSeminarID\" and " + 
                "s.\"blnStudentAntragStatusAbschluss\"=true));");
    if(rAntraege.next()){
        throw new Exception("{\"error\":\"Sorry, ein entsprechender Antrag liegt bereits vor.\",\"errorDebug\":\"Das Frontend hat den Antrag zwar durchgereicht, es liegt aber noch ein offener Antrag vor.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":20}");
    }
    long lAntragID=student.getNextID("lngStudentAntragID", "tblBdStudentAntrag", "\"lngSdSeminarID\"=" + 
            student.getSeminarID() + " and \"strMatrikelnummer\"='" + student.getMatrikelnummer() + "'");
    
    // Teste, ob es eine Leistung gibt, die neuer ist als der letzte 
    // gestellte Antrag diesen Typs:
    // (BETA: das macht vermutlich für Transkripte Sinn, um Urkunden 
    //        nicht doppelt auszustellen. Bei anderen Dokumenten -- 
    //        Bafög-Bescheinigungen o.ä. -- macht es möglicherweise 
    //        keinen Sinn.
    ResultSet rNeu=seminar.sqlQuery("select ((max(s.\"dtmStudentLeistungAusstellungsd\")> max(a.\"dtmStudentAntragDatum\")) or max(a.\"dtmStudentAntragDatum\") is null) as neu_ok " +
        "from \"tblBdStudentXLeistung\" s, \"tblBdStudentAntrag\" a, \"tblSdNote\" n " +
        "where " +
          "s.\"strMatrikelnummer\"='" + student.getMatrikelnummer() + "' and " +
          "s.\"lngSdSeminarID\"=" + student.getSeminarID() + " and " +
          "s.\"intNoteID\"=n.\"intNoteID\" and " +
          "n.\"lngSdSeminarID\"=s.\"lngSdSeminarID\" and " +
          "n.\"blnNoteBestanden\"=true and " +
          "a.\"lngStudentAntragTypID\"=" + Long.parseLong(request.getParameter("typ_id")) + " and " +
          "a.\"strMatrikelnummer\"=s.\"strMatrikelnummer\" and " +
          "a.\"lngSdSeminarID\"=s.\"lngSdSeminarID\";");
    if(rNeu.next()){
        boolean bOK=rNeu.getBoolean("neu_ok");
        if(!bOK) throw new Exception("{\"error\":\"Sorry, seit Ihrem letzten Antrag " + 
                "wurden keine neuen Leistungen eingetragen; es ist aber nicht möglich, "
                + "die gleiche Urkunde ein zweites Mal auszustellen.\""
                + ",\"errorDebug\":\"Das Frontend hat den Antrag zwar "
                + "durchgereicht, es liegen aber keine neuren Leistungen vor.\",\"errorcode\":" + 
                lERR_BASE + 3 + ",\"severity\":20}");

    }else{
        
    }
    
    String sSQL = "INSERT INTO \"tblBdStudentAntrag\" (\"lngStudentAntragID\"," + 
        "\"lngSdSeminarID\", \"strMatrikelnummer\", " +
        "\"lngStudentAntragTypID\", \"strStudentAntragBezeichnung\", \"strStudentAntragBeschreibung\", " +
        "\"dtmStudentAntragDatum\") " +
        "VALUES (?,?,?,?,?,?,CURRENT_DATE);";

    sSQL += "INSERT INTO \"tblBdStudentAntragStatus\" (" + 
        "\"lngSdSeminarID\", \"strMatrikelnummer\", \"lngDozentID\", \"lngStudentAntragID\", " + 
        "\"lngStudentAntragStatusTypID\", \"strStudentAntragStatusBezeichnung\", " + 
        "\"strStudentAntragStatusBeschreibung\", \"dtmStudentAntragStatusDatum\", " + 
        "\"blnStudentAntragStatusAbschluss\")" + 
    "VALUES (?,?,null,?,?,'Antrag gestellt', 'Antrag gestellt', CURRENT_DATE,false);";

    PreparedStatement pstm=student.prepareStatement(sSQL);int ii=1;
    pstm.setLong(ii++, lAntragID);
    pstm.setLong(ii++, student.getSeminarID());
    pstm.setString(ii++, student.getMatrikelnummer());
    pstm.setLong(ii++, Long.parseLong(request.getParameter("typ_id")));
    pstm.setString(ii++, request.getParameter("antrag"));
    pstm.setString(ii++, "");

    pstm.setLong(ii++, student.getSeminarID());
    pstm.setString(ii++, student.getMatrikelnummer());
    pstm.setLong(ii++, lAntragID);
    pstm.setLong(ii++, lANTRAG_STATUS_TYP_NEU);

    if(pstm.executeUpdate()!=1){
        throw new Exception("{\"error\":\"Der Antrag konnte nicht gespeichert werden, die Datenbank hat sich verweigert.\",\"errorDebug\":\"Weitere Info nur über die Logdateien der Datenbank.\",\"errorcode\":" + lERR_BASE + 1 + ",\"severity\":100}");            
    };
    %>{"success":"true","antrag_id":"<%=lAntragID %>"}
