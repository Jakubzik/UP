<%--
    JSP-Object "student/antrag/get"

    SYNOPSIS (German)
    ===========================
    2013, May 19, 2013      erzeugt. 
    2013, Dez 26, 2013      überarbeitet
    
    Üblicher Lifecycle: GET
    Liefert die Anträge der Studierenden 
    inklusive deren (alter und neuer) Stati 
    
    Expected SESSION PROPERTIES
    ===========================
    user mit AccessLevel>500, oder
    student angemeldet.

    Je nach user werden entweder 
    - alle offenen Anträge (aller Studierender) 
      ODER
    - alle (offenen und abgeschlossensn) Anträge
      des/r anemeldeten Studierenden

    ausgegeben.

    Expected PARAMETER(S):
    ===========================
    matrikelnummer  [long]  sofern es die Anfrage eines/r Studierenden ist

    Returns
    =======
    Array der Anträge und ihrer Stati, z.B. so:
    {
        "antraege": [
            {                                           // ----------------------- Erster Antrag ----------------------------
                "antrag": {
                    "matrikelnummer": "1234567",
                    "id": "0",                          // Lfd. Nr., nur für Update
                    "antrag": "Neues Transkript",       // Bezeichnung -- random
                    "typ_id": "1",                      // Typ des Antrags (obliegt dem Frontend)
                    "stati": [
                        {
                            "datum": "2013-05-19",      // chronologisch sortiert die Stati. Der erste ist immer die Beantragung.
                            "bearbeiter": "0",          // ist die DozentID (Verküpfung obliegt dem Fronten)
                            "typ_id": "1",              // Typ des Status (obliegt dem Frontend)
                            "abschluss": "false"        // Ist das ein 'finaler Status', ist der Antrag also 'bearbeitet'?
                        },
                        {
                            "datum": "2013-01-01",      // zweiter Status
                            "bearbeiter": "0",
                            "typ_id": "3",
                            "abschluss": "true"         // hiermit ist der Antrag fertig bearbeitet
                        }
                    ],
                    "abgeschlossen": "true"             // Zeigt die Existenz eines Status mit "abschluss":"true" an.
                }
            },
            {                                           // ----------------------- Zweiter Antrag ----------------------------
                "antrag": {
                    "matrikelnummer": "1234567",
                    "id": "1",
                    "antrag": "Neues Transkript",
                    "typ_id": "1",
                    "stati": [
                        {
                            "datum": "2013-05-19",
                            "bearbeiter": "0",
                            "typ_id": "1",
                            "abschluss": "false"
                        }
                    ],
                    "abgeschlossen": "true"
                }
            }
        ]
    }


    Sample Usage
    ============
    --
    
--%>
<%@page contentType="text/json" pageEncoding="UTF-8" import="java.sql.ResultSet,de.shj.UP.data.shjCore" session="true" isThreadSafe="false"  errorPage="../../error.jsp" %>
<jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean id="seminar" scope="session" class="de.shj.UP.HTML.HtmlSeminar" />
<jsp:useBean id="student" scope="session" class="de.shj.UP.beans.config.student.StudentBean" /><jsp:useBean id="sd" scope="session" class="de.shj.UP.util.SessionData" />
<%@include file="../../../fragments/checkVersion.jsp" %>{"antraege":[
<%boolean bStudentRequest=false;if(sd.getSessionType().equals("student") && request.getParameter("matrikelnummer")!=null && request.getParameter("matrikelnummer").equals(student.getMatrikelnummer())){bStudentRequest=true; %>
    <%@include file="../../../fragments/checkInitStudent.jsp" %>
    <%@include file="../../../fragments/checkLoginStudent.jsp" %>
<%}else{%>
<%@include file="../../../fragments/checkLogin.jsp" %>
<%@include file="../../../fragments/checkAccess500.jsp" %>
<%}
    long lERR_BASE=106000 + 100;    // Antrag + Get
    // TODO: intelligenter filtern:
    // - unterscheiden, ob Anfrage von Studierenden 
    //   (Filter nach Matrikelnummer)
    // - oder Lehrenden 
    //   (Filter nach Antragstyp?)
    //
    ResultSet rAntraege=seminar.sqlQuery("SELECT " +
            "a.\"lngSdSeminarID\", " +
            "a.\"strMatrikelnummer\", " +
            "st.\"strStudentVorname\", " +
            "st.\"strStudentNachname\", " +
            "a.\"lngStudentAntragID\", " + 
            "a.\"lngStudentAntragTypID\", " +
            "a.\"strStudentAntragBezeichnung\", " +
            "a.\"strStudentAntragBeschreibung\", " +
            "a.\"dtmStudentAntragDatum\", " +
            "s.\"lngDozentID\", " +
            "s.\"lngStudentAntragStatusTypID\", " +
            "s.\"lngStudentAntragStatusID\", " +
            "s.\"strStudentAntragStatusBezeichnung\", " +
            "s.\"strStudentAntragStatusBeschreibung\", " +
            "s.\"dtmStudentAntragStatusDatum\", " +
            "d.\"strDozentVorname\", " +
            "d.\"strDozentNachname\", " +
            "s.\"blnStudentAntragStatusAbschluss\" " +
          "FROM " +
            "\"tblBdStudentAntrag\" a, " +
            "\"tblBdStudent\" st, " +
            "\"tblBdStudentAntragStatus\" s left outer join \"tblSdDozent\" d on (d.\"lngSdSeminarID\"=1 and d.\"lngDozentID\"=s.\"lngDozentID\") " +
          "WHERE " +
            "a.\"lngSdSeminarID\" = s.\"lngSdSeminarID\" AND " +
            "a.\"lngSdSeminarID\"=" + seminar.getSeminarID() + " AND " +
            "a.\"lngStudentAntragID\" = s.\"lngStudentAntragID\" AND " + 
            "a.\"strMatrikelnummer\" = s.\"strMatrikelnummer\" AND " + 
            "a.\"strMatrikelnummer\"=st.\"strMatrikelnummer\" AND " +
            (bStudentRequest ? "a.\"strMatrikelnummer\"='" + Long.parseLong(request.getParameter("matrikelnummer")) + "'" :                     // Entweder Filter nach Matrikelnr.
            "not exists (select \"blnStudentAntragStatusAbschluss\" from \"tblBdStudentAntragStatus\" s2 " +                    // Oder Filter nach "alle offenen Anträge"
            "where s2.\"lngSdSeminarID\"=a.\"lngSdSeminarID\" and s2.\"strMatrikelnummer\"=a.\"strMatrikelnummer\" and " + 
            "s2.\"lngStudentAntragID\"=a.\"lngStudentAntragID\" and s2.\"blnStudentAntragStatusAbschluss\"=true) ") 
            + " order by \"strMatrikelnummer\", \"lngStudentAntragID\", \"dtmStudentAntragDatum\" desc, \"lngStudentAntragStatusID\" ;");
    long lAntragID=-1;long lAntragIDOld=-2;String sStatusArray="";String sBezeichnung="";long lAntragTypID=-1;boolean bAbgeschlossen=false;
    String sMatrikelnummer="";String sMatrikelnummerOld="#";String sAntragsteller="";
    
    while(rAntraege.next()){
        sMatrikelnummer=rAntraege.getString("strMatrikelnummer");
        
        lAntragID=rAntraege.getLong("lngStudentAntragID");
        if(lAntragID!=lAntragIDOld || !sMatrikelnummer.equals(sMatrikelnummerOld)){        // ein Neuer Antrag
            if(!sStatusArray.equals("")){                                                  // es gilt noch, den alten Antrag auszugeben
                sStatusArray=sStatusArray.substring(0,sStatusArray.length()-1) + "]";
                out.write("{\"antrag\":{\"matrikelnummer\":\"" + sMatrikelnummerOld + 
                    "\",\"antragsteller\":\"" + sAntragsteller + "\",\"id\":\"" +lAntragIDOld  + 
                    "\",\"antrag\":\"" + sBezeichnung + 
                    "\", \"typ_id\":\"" + lAntragTypID + "\",\"stati\":" + sStatusArray + ",\"abgeschlossen\":\"" + (bAbgeschlossen ? "true" : "false") + "\"}},");
            }
            lAntragIDOld=lAntragID;
            sMatrikelnummerOld=sMatrikelnummer;
            bAbgeschlossen=false;
            sBezeichnung=rAntraege.getString("strStudentAntragBezeichnung");
            lAntragTypID=rAntraege.getLong("lngStudentAntragTypID");
            sStatusArray = "[{\"datum\":\"" + shjCore.g_GERMAN_DATE_FORMAT.format(rAntraege.getDate("dtmStudentAntragStatusDatum"))
                    + "\",\"bezeichnung\":\"" + rAntraege.getString("strStudentAntragStatusBezeichnung") 
                    + "\",\"bearbeiter_name\":\"" + rAntraege.getString("strDozentVorname") + " " + rAntraege.getString("strDozentNachname")  
                    + "\",\"typ_id\":\"" + rAntraege.getLong("lngStudentAntragStatusTypID")
                    + "\",\"abschluss\":\"" + rAntraege.getBoolean("blnStudentAntragStatusAbschluss")
                    + "\"},";
        }else{
            if(!bAbgeschlossen) bAbgeschlossen=rAntraege.getBoolean("blnStudentAntragStatusAbschluss");
            sStatusArray += "{\"datum\":\"" + shjCore.g_GERMAN_DATE_FORMAT.format(rAntraege.getDate("dtmStudentAntragStatusDatum")) 
                    + "\",\"bezeichnung\":\"" + rAntraege.getString("strStudentAntragStatusBezeichnung") 
                    + "\",\"bearbeiter_name\":\"" + rAntraege.getString("strDozentVorname") + " " + rAntraege.getString("strDozentNachname")  
                    + "\",\"typ_id\":\"" + rAntraege.getLong("lngStudentAntragStatusTypID")
                    + "\",\"abschluss\":\"" + rAntraege.getBoolean("blnStudentAntragStatusAbschluss")
                    + "\"},";
        }
        sAntragsteller=rAntraege.getString("strStudentVorname") + " " + rAntraege.getString("strStudentNachname");
    }
    if(!sStatusArray.equals("")) sStatusArray=sStatusArray.substring(0,sStatusArray.length()-1) + "]";
    
    if(lAntragID>=0)
        out.write("{\"antrag\":{\"matrikelnummer\":\"" + sMatrikelnummer + 
            "\",\"antragsteller\":\"" + sAntragsteller + "\",\"id\":\"" +lAntragIDOld  + 
            "\",\"antrag\":\"" + sBezeichnung + 
            "\", \"typ_id\":\"" + lAntragTypID + "\",\"stati\":" + sStatusArray + ",\"abgeschlossen\":\"" + (bAbgeschlossen ? "true" : "false") + "\"}}");
%>]}
