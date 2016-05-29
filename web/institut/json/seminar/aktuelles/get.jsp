<%--
    JSP-Object "seminar/aktuelles/get"
    @version Feb 8, 2015 -- hj

    @TODO
    ===========================

    SYNOPSIS (German)
    ===========================
    2015, Feb 8, shj
    Erzeugt. 
    2016, April 7: Durchsicht, Korrektur Bsp. Unused? Absolviert test.
    Üblicher Lifecycle: GET

    Liefert die "aktuellen Meldungen" des Instituts.

    
    Expected SESSION PROPERTIES
    ===========================
    keine

    Expected PARAMETER(S):
    ===========================
    version,
    seminar_id
    
    Returns
    =======
    [

        {"nachricht":
        {"id":"3081", "head":"Ausfall Sprechstunde Hänßgen (23.03. und 30.03.)", "body":"<p>Die morgige Sprechstunde muss leider wegen Krankheit ausfallen. (N&auml;chste Woche findet sie urlaubsbedingt auch nicht statt.)&nbsp;</p>", "signature":"Hänßgen, 22.03.2016"}
        },

        {"nachricht":
        {"id":"3080", "head":"Sprechstunde Prof. Busse", "body":"Die n&auml;chste Sprechstunde von Frau Prof. Busse findet am 24.3., 8.00-9.15 Uhr statt. Um vorherige Anmeldung wird gebeten.", "signature":"Hirsch, 21.03.2016"}
        },
        ...
        ...
    ]
    
--%><%@page import="de.shj.UP.util.ResultSetSHJ"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page contentType="text/json" pageEncoding="UTF-8" import="java.sql.ResultSet,de.shj.UP.data.Dozent,de.shj.UP.data.shjCore" session="true" isThreadSafe="false"  errorPage="../../error.jsp" %>
<%@include file="../../../fragments/checkVersion.jsp" %>
<%! long lERR_BASE; %>
<%lERR_BASE=217000 + 100;    // Aktuelles + Get%>

<%
        long lSeminarID=-1;
        if(request.getParameter("seminar_id")!=null){
            try{
                lSeminarID=Long.parseLong(request.getParameter("seminar_id"));
            }catch(Exception eNotNumeric){
                throw new Exception("{\"error\":\"Es wurde kein Seminar für den Abruf aktueller Informationen spezifiziert.\",\"errorDebug\":\"Die übergebene seminar_id (" + request.getParameter("seminar_id") + ") entspricht nicht der Konvention.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":50}");
            }
        }
        
	PreparedStatement pstm= new shjCore().prepareStatement("select * from \"tblBdAktuelles\" " +
                "where \"lngSdSeminarID\"=? and " + 
                "\"dtmAktuellesStart\"<=CURRENT_DATE and \"dtmAktuellesStop\">=CURRENT_DATE " +
                "order by \"lngAktuellesSequence\" desc;");
           int ii=1;
           pstm.setLong(ii++, lSeminarID);
           ResultSetSHJ rAktuelles= new ResultSetSHJ(pstm.executeQuery());
%>[<%
while(rAktuelles.next() ){%>
{"nachricht":
{"id":"<%=rAktuelles.getString("lngAktuellesSequence") %>", "head":"<%=shjCore.string2JSON(rAktuelles.getString("strAktuellesHeading")) %>", "body":"<%=shjCore.string2JSON(rAktuelles.getString("strAktuellesDetails")) %>", "signature":"<%=shjCore.string2JSON(rAktuelles.getString("strAktuellesAutorName")) %>"}
}<%=(rAktuelles.isLastRow() ? "" : ",") %>
<%}%>]
