<%--
    @Neu Mai 2017, hj
    
    JSP-Object "seminar/lehrauftrag/get"

    Liefert alle "Sets" (also Versionen) der Lehraufträge 
    zu einem Semester mir allen für den Datenaustausch 
    notwendigen Informationen (Vergütung, Thema etc.)

    Informationen des Seminars (Bewegungsdaten)

    SYNOPSIS (German)
    ===========================   
    Üblicher Lifecycle: GET
    http://localhost:8084/SignUpXP/as/Faculty/json/seminar/lehrauftrag/get.jsp?signup_expected_backend_version=1-0-0-2&semester=Sommersemester%202016
    
    Expected SESSION PROPERTIES
    ===========================
    seminar        muss initialisiert sein (AccessLevel >=1),
    user           mit AccessLevel >= 500

    Expected PARAMETER(S):
    ===========================
    semester    = 'Wintersemester 2016/2017' oder 'Sommersemester 2009" etc.

    Returns
    =======
    Array of objects with remarks:
  
    {"lehrauftraege":
            {"semester":"wintersemester 2015/2016",
            "sets":[{"set":4,
                    "versionsdatum":"2016-1-5",
                    "la":[
                        {"dozent_vorname":"Heinz",
                        "dozent_name":"Jakubzik",
                        "dozent_strasse":"Rheinvillenstr. 5",
                        "dozent_plz":"68163",
                        "dozent_ort":"Mannheim",
                        "dozent_email":"info@shj-online.de",
                        "la_kurs":"Proseminar II Literaturwissenschaft",
                        "la_bereich":"Literaturwissenschaft",
                        "la_verguetung":"1000",
                        "la_kostenstelle_bez":"Aversum Anglistik",
                        "la_kostenstelle_nr":"1234567",
                        "la_lfd_nr":"7",
                        "la_sws":"5",
                        "la_ergaenzung_angebot":true,
                        "la_weiterbildung":false,
                        "la_kuenstler_sozial":false}
                     ]
                   }]
            }
    }

    Sample Usage
    ============

    
--%><%@page import="java.sql.ResultSet"%><%@page import="java.sql.PreparedStatement"%><%@page contentType="text/json" pageEncoding="UTF-8" session="true" isThreadSafe="false"  errorPage="../../error.jsp" %><jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean id="seminar" scope="session" class="de.shj.UP.logic.SeminarData" /><%@include file="../../../fragments/checkVersion.jsp" %><%@include file="../../../fragments/checkAccess500.jsp" %><%  long lERR_BASE=218000 + 100;    // Lehrauftrag + Get

    // -------------------------------------------------------------------------
    // Schnittstelle
    if(request.getParameter("semester")==null || request.getParameter("semester").length()<19){
        throw new Exception("{\"error\":\"Die Lehraufträge können nicht abgerufen werden, ohne dass ein Semester gewählt wurde.\"" + 
                    ",\"errorDebug\":\"Der Parameter >semester< muss z.B. lauten >Wintersemester 2018/2019<, lautet aber >" + request.getParameter("semester") + "<\",\"errorcode\":" + lERR_BASE + 1 + ",\"severity\":10}"); 
    }
    
    String sSemester = request.getParameter("semester");

    PreparedStatement pstm = seminar.prepareStatement("select l.*, d.* from \"tblBdLehrauftrag\" l, \"tblSdDozent\" d where "
            + "l.\"lngSdSeminarID\"=? and "
            + "l.\"strLehrauftragSemester\"=? and "
            + "d.\"lngSdSeminarID\"=l.\"lngSdSeminarID\" and "
            + "d.\"lngDozentID\"=l.\"lngDozentID\" order by l.\"intLehrauftragSetNummer\", l.\"intLehrauftragLfdNr\";");
    
    int ii = 1;
    pstm.setLong(ii++, seminar.getSeminarID());
    pstm.setString(ii++, sSemester);
    
    ResultSet rLA = pstm.executeQuery();
    boolean bStart = true;
    boolean bNewSet= true;
    long lSetNo = -1;
    String sCloseOldSet="";
    while(rLA.next()){
        if(bStart){
            out.write("{\"lehrauftraege\":{\"semester\":\"" + rLA.getString("strLehrauftragSemester") + "\",\"sets\":[");
            bNewSet= true;
        }
        
        if(lSetNo != rLA.getLong("intLehrauftragSetNummer")) bNewSet = true;
        
        if(bNewSet){
            out.write(sCloseOldSet);    // Beim ersten Durchlauf leer
            lSetNo = rLA.getLong("intLehrauftragSetNummer");
            out.write("{\"set\":" + lSetNo + ", " + 
                    "\"versionsdatum\":\"" + rLA.getDate("dtmLehrauftragSetVersion") + "\","+
                    "\"la\":[");
            sCloseOldSet = "]},";
            bNewSet = false;
        }else{
            out.write(",");
        }
  
%>{"dozent_vorname":"<%=rLA.getString("strDozentVorname")%>",
"dozent_name":"<%=rLA.getString("strDozentNachname")%>",
"dozent_id":"<%=rLA.getString("lngDozentID")%>",
"dozent_strasse":"<%=rLA.getString("strDozentStrasse")%>",
"dozent_plz":"<%=rLA.getString("strDozentPLZ")%>",
"dozent_ort":"<%=rLA.getString("strDozentOrt")%>",
"dozent_email":"<%=rLA.getString("strDozentEmail")%>",
"dozent_besch_hd":"<%=rLA.getBoolean("blnLehrauftragBeschaeftigtUniHD") ? "true" : "false"%>",
"la_kurs":"<%=rLA.getString("strLehrauftragKurstyp")%>",
"la_bereich":"<%=rLA.getString("strLehrauftragBereich")%>",
"la_verguetung":"<%=rLA.getString("dblLehrauftragVerguetung")%>",
"la_kostenstelle_bez":"<%=rLA.getString("strLehrauftragKostenstelleBez")%>",
"la_kostenstelle_nr":"<%=rLA.getString("strLehrauftragKostenstelleNr")%>",
"lfd_nr":"<%=rLA.getString("intLehrauftragLfdNr")%>",
"la_sws":"<%=rLA.getString("intLehrauftragSWS")%>",
"la_ergaenzung_angebot":<%=rLA.getBoolean("blnLehrauftragErgaenzungLehrangebot") ? "true" : "false"%>,
"la_weiterbildung":<%=rLA.getBoolean("blnLehrauftragWeiterbildung") ? "true" : "false"%>,
"la_kuenstler_sozial":<%=rLA.getBoolean("blnLehrauftragKuensterSozialabgabe") ? "true" : "false"%>}<%bStart=false;
    }
    out.write("]}"); // la, set
    out.write("]}}"); // sets, Semester, LA
%>
