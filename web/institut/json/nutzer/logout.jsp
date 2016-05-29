<%--
    JSP-Object "nutzer"
    @Revision: Dec 16, 2013 -- hj

    @TODO
    ===========================
    Fehlercodes, Schnittstellentest.

    SYNOPSIS (German)
    ===========================
    2013, Nov 30, shj
    2016, Apr 6, shj Durchsicht
    
    Beendet die Sitzung.
    
    Expected SESSION PROPERTIES
    ===========================
    --

    Expected PARAMETER(S):
    ===========================
    --

    Returns
    =======
    {
        "success": "true"
    }

    Sample Usage
    ============
 
    
--%><%@ page contentType="text/json" pageEncoding="UTF-8" session="true" isThreadSafe="false" errorPage="../error.jsp"%>
<jsp:useBean id="nutzer" scope="session" class="de.shj.UP.data.Nutzer" />
<%session.invalidate();%>{"success":"true"}

