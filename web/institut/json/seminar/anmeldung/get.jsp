<%--
    JSP-Object "seminar/anmeldung/get"
    @Revision: Nov 19, 2013 -- hj

    @TODO
    ===========================
    Entscheiden, ob auch ohne Filter nach LeistungsID machbar,
    entscheiden, ob weitere Filter (Datum/Semester/Kurs) gewünscht.

    SYNOPSIS (German)
    ===========================
    2013, Aug 19, shj
    Erzeugt. 
    
    Üblicher Lifecycle: GET

    Liefert die Anmeldungen eines Lehrenden.

    Wird benötigt, um Notenlisten anzuzeigen, in die die 
    Noten schnell eingetragen werden können.

    Ähnlich wie student/anmeldung/get -- hier werden allerdings
    nur die Anmeldungen eines/r Studierenden geliefert.

    Optional kann eine "dozent_id" übergeben werden, die 
    bei einer Berechtigung von >=500 von der ID des eingeloggten 
    Benutzers abweichen kann.
    
    Expected SESSION PROPERTIES
    ===========================
    user        muss initialisiert sein (AccessLevel egal),

    Expected PARAMETER(S):
    ===========================
    leistung_id  [long]
    [dozent_id]  [long | *] -- nur falls dozent.getAccessLevel >= 500, sonst Fehler
    
    Returns
    =======
    Array of objects with remarks:
    {
    "anmeldungen": [
        {
            "anmeldung": {
                "name": "#Server liefert keinen Namen",
                "name_en": "#Server liefert keinen englischen Namen",
                "matrikelnummer": "1234567",
                "student_vorname": "Meike",
                "student_nachname": "Musterfrau",
                "id": "10",
                "count": "1",
                "ects": "5.5",
                "kurstitel": "Amerikanische Lyrik im 19. Jahrhundert",
                "kurs_id": "14632",
                "kurstitel_en": "19th Century American Poetry",
                "kurs_semester": "ss2013",
                "leistungstyp": "",
                "details": "",
                "bemerkung": "",
                "is_anmeldung": "t",
                "datum": "19.11.2013",
                "aussteller_nachname": "Jakubzik",
                "aussteller_vorname": "Heiko",
                "aussteller_titel": "Dr.",
                "modul": "100235"
            }
        }, ...
        }]
        
    
--%><%@page import="java.sql.PreparedStatement"%>
<%@page contentType="text/json" pageEncoding="UTF-8" import="java.sql.ResultSet,de.shj.UP.data.Dozent,de.shj.UP.data.shjCore" session="true" isThreadSafe="false"  errorPage="../../error.jsp" %>
<jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" />
<jsp:useBean id="sd" scope="session" class="de.shj.UP.util.SessionData" />
<%@include file="../../../fragments/checkVersion.jsp" %>
<%@include file="../../../fragments/checkLogin.jsp" %>    
<%@include file="../../../fragments/checkAccess1.jsp" %>
<%! long lERR_BASE; %>
<%lERR_BASE=207000 + 100;    // Anmeldung + Get%>

<%!
// @TODO
// 1. SQL-Abfrag
// 2. Wenn nur 1 Resultat, trotzdem als Array?
// 3. Dokumentation Parameter (unten)
// 4. Einschränkung blnStudentLeistungAnmeldung=true
// 4a. is_anmeldung aus JSON Antwort entfernen
// OK: 4. Einschränkung auf anfragenden Dozent bei geringer Berechtigung
ResultSet getAnmeldungen(de.shj.UP.data.Dozent d, HttpServletRequest r) throws Exception{
        long lLeistungID =-2;
        boolean bAlleDozenten=false;
        if(r.getParameter("leistung_id")!=null){
            try{
                lLeistungID=Long.parseLong(r.getParameter("leistung_id"));
            }catch(Exception eNotNumeric){
                throw new Exception("{\"error\":\"Es wurde eine Leistung mit einer nicht existierenden ID angefordert.\",\"errorDebug\":\"Die übergebene Id (" + r.getParameter("leistung_id") + ") entspricht nicht der Konvention.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":50}");
            }
        }
        long lDozentID=d.getDozentID();
        Dozent tmp_dozent=null;
        if(r.getParameter("dozent_id")!=null){
            if(d.getDozentAccessLevel()<500){
                if(!String.valueOf(d.getDozentID()).equals(r.getParameter("dozent_id"))){
                    throw new Exception("{\"error\":\"Es wurden Anmeldungen einer/s anderen Lehrenden angefordert; dazu fehlt leider die Berechtigung.\",\"errorDebug\":\"Es muss mindestens eine Berechtigung von 500 vorliegen.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":50}");
                }
            }
            try{
                if(r.getParameter("dozent_id").equals("*")) bAlleDozenten=true;
                else lDozentID= Long.parseLong(r.getParameter("dozent_id"));
            }catch(Exception eNotNumeric){
                throw new Exception("{\"error\":\"Es wurde eine Lehrenden-ID mit einer nicht existierenden Lfd. Nr. angefordert.\",\"errorDebug\":\"Die übergebene ID (" + r.getParameter("leistung_count") + ") entspricht nicht der Konvention.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":50}");
            }
        }
        
        if(lDozentID==d.getDozentID()){
            tmp_dozent=d;
        }else{
            tmp_dozent=new Dozent(d.getSdSeminarID(), lDozentID);
        }

	PreparedStatement pstm= d.prepareStatement("SELECT " +
                        "min(a.\"lngID\") + 100000 as sequence," +
                        "min(a.\"strKvvArchivSemesterName\") as strKvvArchivSemesterName," +
                        "s.\"strStudentVorname\"," +
                        "s.\"strStudentNachname\"," +
                        "x.\"strMatrikelnummer\"," +
                        "x.\"lngKlausuranmeldungKursID\"," +
                        "x.\"lngLeistungsID\", " +
                        "x.\"lngStudentLeistungCount\", " +
                        "x.\"strSLKursTitel\", " +
                        "x.\"strSLKursTitel_en\", " +
                        "x.\"strStudentLeistungDetails\", " +
                        "x.\"sngStudentLeistungCreditPts\", " +
                        "x.\"dtmStudentLeistungAusstellungsd\", " +
                        "x.\"strStudentLeistungCustom3\", " +
                        "x.\"strStudentLeistungBemerkung\", " +
                        "x.\"strStudentLeistungAussteller\", " +
                        "x.\"strStudentLeistungAusstellerVor\", x.\"strStudentLeistungAussteller\", " +
                        "x.\"strStudentLeistungAusstellerTit\", " +
                        "x.\"lngModulID\", " +
                        "Case when x.\"lngModulID\" is null THEN 0 ELSE x.\"lngModulID\" END as modul, " +
                        "x.\"blnStudentLeistungKlausuranmeldung\", " +
                        "x.\"blnStudentLeistungEditierbar\" " +
                      "FROM " +
                        "\"tblBdStudentXLeistung\" x, " +
                        "\"tblBdKvvArchiv\" a," +
                        "\"tblBdStudent\" s " +
                      "WHERE " +
                        "a.\"lngSdSeminarID\" =? AND " +
                        "(a.\"lngKvvArchivKursID\"=x.\"lngKlausuranmeldungKursID\" or " +   // Der Bezug in SxL ist entweder die KursID, oder die lngID aus dem Archiv (ja nach dem, ob die Anmeldung nachträglich nach dem Archivieren erzeugt wurde oder nicht).
                         "a.\"lngID\"=x.\"lngKlausuranmeldungKursID\") and " +
                        "a.\"lngKvvArchivLeistungID\"=x.\"lngLeistungsID\" and " + 
                        "s.\"strMatrikelnummer\"=x.\"strMatrikelnummer\" AND " + 
                        "x.\"blnStudentLeistungKlausuranmeldung\" = true and " +
                        "x.\"lngSdSeminarID\" = ? AND " +
                            (bAlleDozenten ? "" : "(x.\"lngDozentID\" = ? or (" +
                            "x.\"lngDozentID\" is null and x.\"strStudentLeistungAussteller\"=?"
                + " and x.\"strStudentLeistungAusstellerVor\"=?)) AND ") +
                        "x.\"lngLeistungsID\"=? group by \"strStudentVorname\", \"strStudentNachname\", x.\"strMatrikelnummer\", \"lngKlausuranmeldungKursID\", \"lngLeistungsID\", \"lngStudentLeistungCount\"," +
                        "\"strSLKursTitel\", \"strSLKursTitel_en\", \"strStudentLeistungDetails\", \"sngStudentLeistungCreditPts\", \"dtmStudentLeistungAusstellungsd\","+
                        "\"strStudentLeistungCustom3\",\"strStudentLeistungBemerkung\",\"strStudentLeistungAussteller\",\"strStudentLeistungAusstellerVor\", \"strStudentLeistungAussteller\","+
                        "\"strStudentLeistungAusstellerTit\",\"lngModulID\", \"blnStudentLeistungKlausuranmeldung\", \"blnStudentLeistungEditierbar\" " +
                        "union " + 
                    "SELECT " +
                        "x.\"lngKlausuranmeldungKursID\" as sequence," +
                        "'Current'," +
                        "s.\"strStudentVorname\"," +
                        "s.\"strStudentNachname\"," +
                        "x.\"strMatrikelnummer\"," +
                        "x.\"lngKlausuranmeldungKursID\"," +
                        "x.\"lngLeistungsID\", " +
                        "x.\"lngStudentLeistungCount\", " +
                        "x.\"strSLKursTitel\", " +
                        "x.\"strSLKursTitel_en\", " +
                        "x.\"strStudentLeistungDetails\", " +
                        "x.\"sngStudentLeistungCreditPts\", " +
                        "x.\"dtmStudentLeistungAusstellungsd\", " +
                        "x.\"strStudentLeistungCustom3\", " +
                        "x.\"strStudentLeistungBemerkung\", " +
                        "x.\"strStudentLeistungAussteller\", " +
                        "x.\"strStudentLeistungAusstellerVor\", x.\"strStudentLeistungAussteller\", " +
                        "x.\"strStudentLeistungAusstellerTit\", " +
                        "x.\"lngModulID\", " +
                        "Case when x.\"lngModulID\" is null THEN 0 ELSE x.\"lngModulID\" END as modul, " +
                        "x.\"blnStudentLeistungKlausuranmeldung\", " +
                        "x.\"blnStudentLeistungEditierbar\" " +
                      "FROM " +
                        "\"tblBdStudentXLeistung\" x, " +
                        "\"tblBdKurs\" k, " + 
                        "\"tblBdStudent\" s " +
                      "WHERE " +
                        "s.\"strMatrikelnummer\"=x.\"strMatrikelnummer\" AND " + 
                        "x.\"blnStudentLeistungKlausuranmeldung\" = true and " +
                        "x.\"lngSdSeminarID\" = ? AND " +
                            (bAlleDozenten ? "" : "x.\"lngDozentID\" = ? AND ") +
                        "x.\"lngKlausuranmeldungKursID\"=k.\"lngKursID\" and k.\"lngSdSeminarID\"=? and " +
                        "x.\"lngLeistungsID\"=? " + 
                        "order by sequence, \"strStudentNachname\", \"strStudentVorname\";");
           int ii=1;
           pstm.setLong(ii++, d.getSdSeminarID());
           pstm.setLong(ii++, d.getSdSeminarID());
           if(!bAlleDozenten) pstm.setLong(ii++, lDozentID);
           if(!bAlleDozenten) pstm.setString(ii++, tmp_dozent.getDozentNachname());
           if(!bAlleDozenten) pstm.setString(ii++, tmp_dozent.getDozentVorname());
           pstm.setLong(ii++, lLeistungID);
           pstm.setLong(ii++, d.getSdSeminarID());
           if(!bAlleDozenten) pstm.setLong(ii++, lDozentID);
           pstm.setLong(ii++, d.getSdSeminarID());
           pstm.setLong(ii++, lLeistungID);
           return pstm.executeQuery();
}%>{"anmeldungen":[<%
    boolean bFirst=true;ResultSet rBem=getAnmeldungen(user, request);while(rBem.next()){
	if(!bFirst) out.write(",");bFirst=false;%>
        {"anmeldung":{"name":"#Server liefert keinen Namen",
        "name_en":"#Server liefert keinen englischen Namen",
	"matrikelnummer":"<%=rBem.getString("strMatrikelnummer") %>",
        "student_vorname":"<%=rBem.getString("strStudentVorname")%>",
        "student_nachname":"<%=rBem.getString("strStudentNachname")%>",
	"id":"<%=rBem.getString("lngLeistungsID") %>",
	"count":"<%=rBem.getString("lngStudentLeistungCount") %>",
	"ects":"<%=rBem.getString("sngStudentLeistungCreditPts") %>",
        "kurstitel":"<%=shjCore.string2JSON(shjCore.normalize(rBem.getString("strSLKursTitel"))) %>",
        "kurs_id":"<%=shjCore.string2JSON(shjCore.normalize(rBem.getString("lngKlausuranmeldungKursID"))) %>",
	"kurstitel_en":"<%=shjCore.string2JSON(shjCore.normalize(rBem.getString("strSLKursTitel_en"))) %>",
        "kurs_semester":"<%=rBem.getString("strKvvArchivSemesterName")%>",
	"leistungstyp":"<%=shjCore.string2JSON(shjCore.normalize(rBem.getString("strStudentLeistungCustom3"))) %>",
	"details":"<%=shjCore.string2JSON(shjCore.normalize(rBem.getString("strStudentLeistungDetails"))) %>",
	"bemerkung":"<%=shjCore.string2JSON(shjCore.normalize(rBem.getString("strStudentLeistungBemerkung"))) %>",
        "is_anmeldung":"<%=rBem.getString("blnStudentLeistungKlausuranmeldung")%>",
	"datum":"<%try{%><%=shjCore.shjGetGermanDate(rBem.getDate("dtmStudentLeistungAntragdatum")) %><%}catch(Exception eNoDateGiven){%><%=shjCore.shjGetGermanDate(user.g_TODAY) %><%}%>",
        "aussteller_nachname":"<%=rBem.getString("strStudentLeistungAussteller") %>",
        "aussteller_vorname":"<%=rBem.getString("strStudentLeistungAusstellerVor") %>",
        "aussteller_titel":"<%=rBem.getString("strStudentLeistungAusstellerTit") %>",
	"modul":"<%=rBem.getString("modul") %>"
	}}
<%}%>]}
