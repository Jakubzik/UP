<%--
    JSP-Object "seminar/dozent/update"
    @Revision: Nov 20, 2013 -- hj
    @Revision: Dez 29, 2013 -- hj

    @TODO
    ===========================
    Update auch Daten derjenigen Nutzer ermöglichen, 
    die nicht gerade eingeloggt sind?

    SYNOPSIS (German)
    ===========================
    2013, Mar 5, shj
    Erzeugt. (Stub)
    
    Ändert die Daten des gerade angemeldeten Nutzers
    gemäß den übergebenen Parametern. 
    
    Expected SESSION PROPERTIES
    ===========================
    user        muss initialisiert sein 

    Expected PARAMETER(S):
    ===========================
    id:             [long]                      - muss die ID des eingeloggten Benutzers sein
    email:          [String]                    - E-Mail Adresse (keine Syntaxprüfung)
    passwort:       [String | ***** | null]     - neues Passwort 
    sprechstunde:   [String]
    titel:          [String]
    telefon:        [String]
    zimmer:         [String]
    homepage:       [String] (URL)
    hp_optionen:    [JSON String]
                    Bsp: hp_optionen: "show_kvv":"alle","downloads":[]
                    -  "show_kvv":"aktuell|alle" (nur aktuelle Veranstaltung(en) oder 
                                                            alle Veranstaltungen (auch alte) oder
                                                            leer für keine Anzeige)

                    - "downloads":[{"src":"d26_1385233785959.pdf","name":"Test1"},{"src":"d26_1385233805238.pdf","name":"Noch ein Test"}]
                                                                
    rubriken:       [JSON String | null], 
                    - rubriken: {"rubrik":"","inhalt":""} für leere Rubriken,
                    -  {"rubrik":"Lebenslauf","inhalt":"Dies<br>und das<br>und dann noch meht"},{"rubrik":"Publikationen","inhalt":"Eine<br>Zweite<br>Dritte"}

    Returns
    =======
    {
        "success": "true",
        "dozent": {
            "id": "26",
            "email": "heiko.jakubzik@shj-online.de",
            "telefon": "123456",
            "zimmer": "123",
            "sprechstunde": "Do 14-15 und Fr 10-11.30 Uhr",
            "titel": "Dr.",
            "homepage_rubriken": [
                {
                    "rubrik": "",
                    "inhalt": ""
                }
            ],
            "homepage_optionen": {
                "show_kvv": "",
                "downloads": []
            }
        }
    }

    Sample Usage
    ============
 
    
--%><%@page contentType="text/json" pageEncoding="UTF-8" import="java.sql.ResultSet,de.shj.UP.data.Dozent,de.shj.UP.util.PasswordHash" session="true" isThreadSafe="false"  errorPage="../../error.jsp" %>
<jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean id="seminar" scope="session" class="de.shj.UP.HTML.HtmlSeminar" />
<%@include file="../../../fragments/checkVersion.jsp" %>
<%@include file="../../../fragments/checkLogin.jsp" %>
<%@include file="../../../fragments/checkAccess1.jsp" %>
<%long lERR_BASE=201000 + 200;    // Dozent + Update

    // Wenn der User seine eigenen Daten verändert:
    long lDozentID=-1;
    //        lDozentID=Long.parseLong(request.getParameter("id"));
    try{
        lDozentID=Long.parseLong(request.getParameter("id").trim());
    }catch(Exception eNotNumeric){
        throw new Exception("{\"error\":\"Die Änderungen an den Details des/r Lehrenden können nicht gespeichert werden. Es wurde keine Id übergeben.\"" + 
                    ",\"errorDebug\":\"Der Parameter >id< (für die DozentID) hat den nicht-numerischen Wert " + request.getParameter("id") + ".\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":10}");               
    }
    if(user.getDozentID()==lDozentID){
        user.setDozentEmail(request.getParameter("email"));
        if(request.getParameter("passwort")!=null && !request.getParameter("passwort").equals("*****")) user.setDozentPasswort(PasswordHash.createHash(request.getParameter("passwort")));
        user.setDozentSprechstunde(request.getParameter("sprechstunde"));
        user.setDozentTitel(request.getParameter("titel"));
        user.setDozentTelefon(request.getParameter("telefon"));
        user.setDozentZimmer(request.getParameter("zimmer"));
        user.setDozentHomepage(request.getParameter("homepage"));
        user.setDozentHomepageOptions("{" + request.getParameter("hp_optionen") + "}");
        if(request.getParameter("rubriken")!=null) user.setDozentInteressen("[" + request.getParameter("rubriken") + "]");
        if(!user.update()){
        throw new Exception("{\"error\":\"Die Lehrendendetails können nicht geändert werden, sorry -- es ist ein Fehler aufgetreten.\"" + 
                    ",\"errorDebug\":\"Update der Lehrendendaten scheitert an der Datenbank.\",\"errorcode\":" + lERR_BASE + 1 + ",\"severity\":10}");            
        }
    }else{
        throw new Exception("{\"error\":\"Es sollen Details eines nicht-angemeldeten Lehrenden geändert werden -- sorry, das ist noch nicht implementiert.\"" + 
                    ",\"errorDebug\":\"Please program me: json/seminar/dozent/update.jsp.\",\"errorcode\":" + lERR_BASE + 1 + ",\"severity\":10}");            
    }    
%>{"success":"true","dozent":{"id":"<%=user.getDozentID()%>","email":"<%=user.getDozentEmail() %>", "telefon":"<%=user.getDozentTelefon() %>", "zimmer":"<%=user.getDozentZimmer() %>","sprechstunde":"<%=user.getDozentSprechstunde() %>", "titel":"<%=user.getDozentTitel() %>", "homepage_rubriken":<%=user.getDozentInteressen()%>, "homepage_optionen":<%=((user.getDozentHomepageOptions()==null || user.getDozentHomepageOptions().trim().equals("{null}")) ? "\"\"" : user.getDozentHomepageOptions())%>}}