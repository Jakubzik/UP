<%--
    JSP-Object "nutzer/update"
    @Revision: Dec 16, 2013 -- hj

    @TODO
    ===========================
    Fehlercodes, Schnittstellentest.

    SYNOPSIS (German)
    ===========================
    2013, Nov 30, shj
    2016, April 7, shj
    
    Ändert die Stammdatenangaben des Nutzers
    
    Expected SESSION PROPERTIES
    ===========================
    nutzer          muss mit UniID bereits in dieser Sitzung
                    initialisiert sein.

    Expected PARAMETER(S):
    ===========================
    vorname:
    nachname:
    titel:
    institut:
    email:
    telefon:
    level:[1,2,3,4] mit 1=BA, 2=MA, 3=PhD, 4=KollegIn

    Returns
    =======
    {
        "success": "true|false" // je nach Erfolg
    }

    Sample Usage
    ============
 
    
--%><%@ page contentType="text/json" pageEncoding="UTF-8" session="true" isThreadSafe="false" errorPage="../error.jsp"%>
<jsp:useBean id="nutzer" scope="session" class="de.shj.UP.data.Nutzer" />

<%  nutzer.setNutzerVorname(request.getParameter("vorname"));
    nutzer.setNutzerNachname(request.getParameter("nachname"));
    nutzer.setNutzerTitel(request.getParameter("titel"));
    nutzer.setNutzerInstitut(request.getParameter("institut"));
    nutzer.setNutzerEmail(request.getParameter("email"));
    nutzer.setNutzerTelefon(request.getParameter("telefon"));
    nutzer.setNutzerLevel(Integer.parseInt(request.getParameter("level")));
    boolean bReturn=nutzer.update();
%>{"success":"<%=bReturn%>"}