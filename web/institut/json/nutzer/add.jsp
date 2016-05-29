<%--
    JSP-Object "nutzer"
    @Revision: Dec 16, 2013 -- hj

    @TODO
    ===========================
    Fehlercodes lERR_BASE mit Readme abgleichen

    SYNOPSIS (German)
    ===========================
    2013, Nov 30, shj
    2016, Mar 31, shj   Überarbeitung
    
    Fügt den Nutzer mit Name, Vorname, Titel etc.
    in der Tabelle der Nutzer hinzu.
    
    Expected SESSION PROPERTIES
    ===========================
    nutzer          muss mit UniID bereits in dieser Sitzung
                    initialisiert sein.

    Expected PARAMETER(S):
    ===========================
    vorname:    [muss]
    nachname:   [muss]
    titel:
    institut:
    email:
    telefon:
    level:[1,2,3,4] 1=BA, 2=MA, 3=PhD, 4=Colleague  [muss]

    Returns
    =======
    {
        "success": "true|false" // je nach Erfolg
    }

    Sample Usage
    ============
 
    
--%>
<%@ page contentType="text/json" pageEncoding="UTF-8" session="true" isThreadSafe="false" errorPage="../error.jsp"%>
<jsp:useBean id="nutzer" scope="session" class="de.shj.UP.data.Nutzer" />
<%  
    long lERR_BASE=102000 + 400;    // User + Add
    
    // ========================================================================
    // Schnittstelle: Teste vornamen, namen, level
    if(request.getParameter("vorname")==null || request.getParameter("vorname").length()<3){
        throw new Exception("{\"error\":\"Der Nutzer konnte nicht gespeichert werden\"," + 
                "\"errorDebug\":\"Der Parameter >vorname< (" + request.getParameter("vorname") + 
                " muss mindestens 3 Zeichen enthalten.\",\"errorcode\":" + 
                lERR_BASE + 3 + ",\"severity\":100}");
    }
    if(request.getParameter("nachname")==null || request.getParameter("nachname").length()<3){
        throw new Exception("{\"error\":\"Der Nutzer konnte nicht gespeichert werden\"," + 
                "\"errorDebug\":\"Der Parameter >nachname< (" + request.getParameter("nachname") + 
                " muss mindestens 3 Zeichen enthalten.\",\"errorcode\":" + 
                lERR_BASE + 3 + ",\"severity\":100}");
    }

    try{
        Integer.parseInt(request.getParameter("level"));
    }catch(Exception eNoNumber){
        throw new Exception("{\"error\":\"Der Nutzer konnte nicht gespeichert werden\"," + 
                "\"errorDebug\":\"Der Parameter >level< (" + request.getParameter("level") + 
                " muss numerisch sein (1=BA, 2=MA, 3=PhD, 4=Colleague).\",\"errorcode\":" + 
                lERR_BASE + 3 + ",\"severity\":100}");
    }
    nutzer.setNutzerVorname(request.getParameter("vorname"));
    nutzer.setNutzerNachname(request.getParameter("nachname"));
    nutzer.setNutzerTitel(request.getParameter("titel"));
    nutzer.setNutzerInstitut(request.getParameter("institut"));
    nutzer.setNutzerEmail(request.getParameter("email"));
    nutzer.setNutzerTelefon(request.getParameter("telefon"));
    nutzer.setNutzerLevel(Integer.parseInt(request.getParameter("level")));
    boolean bReturn=nutzer.add();
%>{"success":"<%=bReturn%>"}

