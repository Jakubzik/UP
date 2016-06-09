<%--
    JSP-Object "student/pruefung/add"

    SYNOPSIS (German)
    ===========================
    2013, Feb 8, shj    erzeugt. 
    2013, Dez 26, shj   überarbeitet
    
    Üblicher Lifecycle: Add
    Führt in der Datenbank die Proc issuePruefung aus.
    
    Expected SESSION PROPERTIES
    ===========================
    user        muss initialisiert sein (AccessLevel 500 oder mehr) und
                ein Studierende/r muss ausgewählt und initialisiert sein.


    Expected PARAMETER(S):
    ===========================
    matrikelnummer          [long]
    pruefung_id             [long]
    saveAsIs                ["true" | else] Falls gesetzt, wird die Prüfung trotz fehlender 
                            Leistungen (dann nicht per StoredProc) gespeichert.
                            In diesem Fall wird in strStudentPruefungCustom2 der
                            Text "Manuelle Ausstellung/Anerkennung trotz fehlender Leistungen." 
                            hinterlegt.
    
    Returns
    =======
    {
        "pruefung": {
            "id": "200000",
            "count": "1",
            "datum": "2013-02-08",
            "semester": "",
            "note": "0",
            "notenberechnung": "<table><tr><td><b>Leistung/Modul</b></td><td><b>Note</b></td><td><b>LP</b></td></tr><tr style=&quot;background-color:#bbb&quot;><td>Einführungsmodul Sprach- und Literaturwissenschaft</td><td>1.8</td><td>0<tr><td>Einführung Sprachwissenschaft</td><td>2</td><td>4</td></tr><tr><td>Einführung Literaturwissenschaft</td><td>1.7</td><td>4</td></tr></td></tr></table>"
        }
    }
    
    ---
    bei SaveAsIs:
    {
    "pruefung": {
        "id": "200000",
        "count": "1",
        "datum": "2013-12-26",
        "semester": "",
        "note": "anerkannt",
        "notenberechnung": "Manuelle Ausstellung/Anerkennung trotz fehlender Leistungen."
        }
    }

--%><%@page import="java.sql.PreparedStatement"%>
<%@ page contentType="text/json" pageEncoding="UTF-8" import="java.sql.ResultSet, de.shj.UP.data.StudentXPruefung ,de.shj.UP.data.Dozent,de.shj.UP.data.shjCore" session="true" isThreadSafe="false" errorPage="../../error.jsp"%>
<jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean id="seminar" scope="session" class="de.shj.UP.logic.SeminarData" />
<jsp:useBean id="student" scope="session" class="de.shj.UP.beans.config.student.StudentBean" />
<jsp:useBean id="credits" scope="page" class="de.shj.UP.beans.config.student.StudentLeistungBean" />
<%@include file="../../../fragments/checkVersion.jsp" %>
<%@include file="../../../fragments/checkLogin.jsp" %>
<%@include file="../../../fragments/checkAccess500.jsp" %>
<%@include file="../../../fragments/checkInitStudent.jsp" %>
<%      long lERR_BASE=104000 + 400;    // Prüfung + Add
        StudentXPruefung sxp=null;
        try{
            Long.parseLong(request.getParameter("pruefung_id"));
        }catch(Exception eNotHandedOver){
            throw new Exception("{\"error\":\"Die Prüfung kann nicht hinzugefügt werden.\",\"errorDebug\":\"Der Parameter >pruefung_id< (" + request.getParameter("pruefung_id") + ") wurde nicht korrekt übergeben.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":100}");
        }

        String sMissingCredits=getMissingCredits(student, Long.parseLong(request.getParameter("pruefung_id")));
	
	if(sMissingCredits.length()==0){
                //
                // Die SQL Prozedur stellt die Prüfung aus, wenn die Voraussetzungen
                // vorliegen; immer mit count=1, der Rückgabewert ist nicht 
                // aussagekräftig, und execProc liest ihn auch gar nicht richtig
                // aus. Hier wäre #HACK noch Verbesserungspotential
                //
		student.execProc("issuePruefung", "long", String.valueOf(student.getSeminarID()), "String", student.getMatrikelnummer(), "int", String.valueOf(student.Fach().getFachID()), "long",request.getParameter("pruefung_id"));
                sxp=new StudentXPruefung(user.getSdSeminarID(), Long.parseLong(request.getParameter("pruefung_id")), student.getMatrikelnummer(), 1 );
                if(sxp==null) throw new Exception("{\"error\":\"Die Prüfung konnte nicht hinzugefügt werden.\",\"errorDebug\":\"Es scheinen zwar keine Leistungen zu fehlen, aber der Aufruf der SQL-Fkt. hat keinen passenden Datensatz erzeugt.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":100}");
	}else{
		if(request.getParameter("saveAsIs").equals("true")){
			PreparedStatement pstm=student.prepareStatement(
                            "INSERT INTO \"tblBdStudentXPruefung\"(" +
                                "\"lngSdSeminarID\", \"lngSdPruefungsID\", \"strMatrikelnummer\", \"intStudentPruefungCount\"," + 
                                "\"intNoteID\", \"strStudentPruefungNote\", \"strStudentPruefungSemester\", " +
                                "\"blnStudentPruefungZUVInformiert\", \"blnStudentPruefungValidiert\", " +
                                "\"blnStudentPruefungAnmeldung\", \"blnStudentPruefungGesiegelt\", " +
                                "\"blnStudentPruefungBestanden\", \"dtmStudentPruefungAusstellungsd\"," + 
                                "\"strStudentPruefungAussteller\",  " +
                                "\"strStudentPruefungCustom2\") " +
                        "VALUES (?,?,?,1,0,?,'',false,true,false,true,true,CURRENT_DATE,?,'Manuelle Ausstellung/Anerkennung trotz fehlender Leistungen.');");

                        int ii=1;
                        pstm.setLong(ii++, student.getSeminarID() );
                        pstm.setLong(ii++, Long.parseLong(request.getParameter("pruefung_id")));
                        pstm.setString(ii++, student.getMatrikelnummer());
                        pstm.setString(ii++, request.getParameter("note_manuell"));
                        pstm.setString(ii++, user.getDozentVorname() + " " + user.getDozentNachname());
                        pstm.execute();
                        sMissingCredits="";
                        
                        sxp=new StudentXPruefung(user.getSdSeminarID(), Long.parseLong(request.getParameter("pruefung_id")), student.getMatrikelnummer(), 1 );
                        if(sxp==null) throw new Exception("{\"error\":\"Die Prüfung konnte nicht hinzugefügt werden.\",\"errorDebug\":\"Die Prüfung sollte trotz fehlender Leistungen gespeichert werden, aber der Aufruf der SQL-Fkt. hat keinen passenden Datensatz erzeugt.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":100}");
		}
	}
        
        if(!sMissingCredits.equals("")) throw new Exception("{\"error\":\"Es fehlen noch Leistungen, um diese Prüfung zu speichern.\",\"errorDebug\":\"Mehr Details in fehlende_leistungen.\",\"errorcode\":10, \"bereinigungs_info\":{\"fehlende_leistungen\":" + sMissingCredits + "},\"severity\":100}");
%>
{"pruefung":{"id":"<%=sxp.getSdPruefungsID() %>","count":"<%=sxp.getStudentPruefungCount()%>", "datum":"<%=sxp.getStudentPruefungAusstellungsd() %>", "semester":"<%=sxp.getStudentPruefungSemester() %>", "note":"<%=sxp.getStudentPruefungNote() %>", "notenberechnung":"<%=shjCore.string2JSON(sxp.getStudentPruefungCustom2())%>"}}
<%!// PROBLEM: bei GymPO Staatsexamen wird 
//          nix als "Missing" gefunden, 
//          und zwar wg. blnZulassungsvoraussetzung=true.
//          Das muss mal durchgesehen werden, 
//          inbses. da es bei GymPO ZP funktioniert 
//          (und die steht ja sogar auf 'auto-issue')
//
//          WEITERES PROBLEM, OP BA 25% SW:
//          die OP ist in Modul 81 gespeichert, 
//          aber die Leistungen (Phonetik & EV SPrachwiss)
//          sind in den Fachmodulen hinterlegt. D.h. 
//          MissingCredits findet hier immer ZU VIELE
//          fehlende Leistungen
//          (Plan: korrigiere zuerst issuePruefungen).
String getMissingCredits(de.shj.UP.beans.config.student.StudentBean stud, long lPruefungID) throws Exception{
	String sReturn="";
	ResultSet rMissing=stud.sqlQuery(
    "select " +
      "pxm.\"lngSdSeminarID\"," + 
      "pxm.\"lngPruefungID\", " +
      "pxm.\"lngModulID\", " +
      "mxl.\"lngLeistungID\"," +
      "l.\"strLeistungBezeichnung\" " + 
    "FROM " +
      "\"tblSdPruefungXModul\" pxm, " + 
      "\"tblSdModulXLeistung\" mxl," +
      "\"tblSdLeistung\" l " +
    "WHERE " +
      "pxm.\"lngSdSeminarID\"=" + stud.getSeminarID() + " and " +  
      "pxm.\"lngPruefungID\"=" + lPruefungID + " and " + 
      "pxm.\"lngSdSeminarID\" = mxl.\"lngSdSeminarID\" AND " +
      "pxm.\"lngModulID\" = mxl.\"lngModulID\" and " +
      "l.\"lngSdSeminarID\"=mxl.\"lngSdSeminarID\" and " +
      "l.\"lngLeistungID\"=mxl.\"lngLeistungID\" and " +
      "pxm.\"blnZulassungsvoraussetzung\"=true and " +
    "NOT EXISTS(" +
            "SELECT " +
              "sxl.\"lngSdSeminarID\"," + 
              "sxl.\"lngLeistungsID\", " +
              "sxl.\"lngModulID\" " +
            "FROM " +
              "\"tblBdStudentXLeistung\" sxl," + 
              "\"tblSdNote\" n " +
            "WHERE " +
              "sxl.\"strMatrikelnummer\"='" + Long.parseLong(stud.getMatrikelnummer()) + "' and " + 
              "sxl.\"lngSdSeminarID\"=pxm.\"lngSdSeminarID\" AND " + 
              "(" +
                    "sxl.\"lngLeistungsID\"=mxl.\"lngLeistungID\" or " +  
                    "sxl.\"lngLeistungsID\" IN " +
                    "(" +
                     "Select \"lngLeistungsID\" from \"tblSdPruefungRegelDetail\" regel " +
                            "WHERE " +
                            "regel.\"lngSdSeminarID\"=pxm.\"lngSdSeminarID\" and " + 
                            "regel.\"lngPruefungID\"=pxm.\"lngPruefungID\" and " +
                            "regel.\"lngModulID\"=pxm.\"lngModulID\" and " +
                            "regel.\"lngRegelID\" in (select \"lngRegelID\" from \"tblSdPruefungRegelDetail\" r2 where " + 
                             "r2.\"lngSdSeminarID\"=pxm.\"lngSdSeminarID\" and " + 
                             "r2.\"lngPruefungID\"=pxm.\"lngPruefungID\" and " +
                             "r2.\"lngModulID\"=pxm.\"lngModulID\" and " +
                             "r2.\"lngLeistungsID\"=mxl.\"lngLeistungID\" " +
                            ")" +  
                    ")" +
              ") and " +
              "sxl.\"lngModulID\"=pxm.\"lngModulID\" AND " + 
              "sxl.\"lngSdSeminarID\" = n.\"lngSdSeminarID\" AND " +
              "sxl.\"intNoteID\" = n.\"intNoteID\" AND " +
              "n.\"blnNoteBestanden\" = true " +
        ");");

	while(rMissing.next()){
		sReturn += ",{\"name\":\"" + rMissing.getString("strLeistungBezeichnung") + "\",\"id\":\"" + rMissing.getString("lngLeistungID") + 
				"\",\"modul_id\":\"" + rMissing.getString("lngModulID") + "\"}";
	}
	if(sReturn.length()>0) sReturn="[" + sReturn.substring(1) + "]";
	return sReturn;
}
%>