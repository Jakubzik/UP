<%--
    JSP-Object "student/bemerkung/get"

    @TODO
    ===========================
    de.shj.UP.beans.config.student.StudentBean student auf einfachere Struktur herunterbringen,

    SYNOPSIS (German)
    ===========================
    2012, Nov 16, shj           erzeugt
    2013, Dec 26, shj           leicht überarbeitet
    2016, Jun 13, shj           Importe gesäubert
    
    Üblicher Lifecycle: GET

    Liefert die Bemerkungen zu Studierenden mit Datum (Deutsch), 
    Autor, Text und ID. Benötigt angemeldete Lehrende sowie initialisierte 
    Studierende.

    Optional kann eine "bemerkung_id" übergeben werden; in diesem 
    Fall wird nur diese eine Bemerkung ausgegeben.
    
    Expected SESSION PROPERTIES
    ===========================
    user        muss initialisiert sein (AccessLevel 500 oder mehr),

    Expected PARAMETER(S):
    ===========================
    matrikelnummer  [long]
    [bemerkung_id    [long]]        (optional)
    
    Returns
    =======
    Array of objects with remarks:
    "bemerkungen":[
        {"id":'bemerkung_id',
        "inhalt":'contents of the remark',
        "datum":'date (in DD.MM.YYYY)',
        "autor":'name of author'}
    ]

    Sample Usage
    ============
    <jsp:include page="student/bemerkung/get.jsp" />
    <jsp:include page="student/bemerkung/get.jsp?bemerkung_id=7731" />
    
--%>
<%@page contentType="text/json" pageEncoding="UTF-8" import="java.sql.ResultSet,java.text.SimpleDateFormat, de.shj.UP.data.Dozent,de.shj.UP.data.shjCore" session="true" isThreadSafe="false"  errorPage="../../error.jsp" %>
<jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean id="seminar" scope="session" class="de.shj.UP.logic.SeminarData" />
<jsp:useBean id="student" scope="session" class="de.shj.UP.beans.config.student.StudentBean" />
<%@include file="../../../fragments/checkVersion.jsp" %>
<%@include file="../../../fragments/checkLogin.jsp" %>
<%@include file="../../../fragments/checkAccess500.jsp" %>
<%@include file="../../../fragments/checkInitStudent.jsp" %>
<%!
ResultSet getBemerkungen(de.shj.UP.beans.config.student.StudentBean student, HttpServletRequest r, long lERR_BASE) throws Exception{
        String sItem="";
        if(r.getParameter("bemerkung_id")!=null){
            try{
                sItem=" \"lngStudentBemerkungID\"=" + Long.parseLong(r.getParameter("bemerkung_id")) + " and ";
            }catch(Exception eNotNumeric){
                throw new Exception("{\"error\":\"Es wurde eine Bemerkung mit einer nicht existierenden ID angefordert.\",\"errorDebug\":\"Die übergebene Id (" + r.getParameter("bemerkung_id") + ") entspricht nicht der Konvention.\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":50}");
            }
        }
	return student.sqlQuery("select b.*, d.\"strDozentNachname\" from \"tblBdStudentBemerkung\" b, \"tblSdDozent\" d where " + 
					"b.\"lngSdSeminarID\"=" + student.getSeminarID() + " and " +
					"d.\"lngSdSeminarID\"=b.\"lngSdSeminarID\" and " + sItem + 
					"d.\"lngDozentID\"=b.\"lngDozentID\" and " +
					"b.\"strMatrikelnummer\"='" + Long.parseLong(student.getMatrikelnummer()) + "' order by \"lngStudentBemerkungID\" desc;");
}%>{"bemerkungen":[<%long lERR_BASE=100000 + 100;    // Bemerkung + Get
    boolean bFirst=true;ResultSet rBem=getBemerkungen(student, request, lERR_BASE);while(rBem.next()){
	if(!bFirst) out.write(",");bFirst=false;
	out.write("{\"bemerkung\":{\"matrikelnummer\":\"" + student.getMatrikelnummer() + "\",\"id\":\"" + rBem.getLong("lngStudentBemerkungID") + "\",\"bemerkung\":\"" + shjCore.string2JSON(rBem.getString("strStudentBemerkungText")) + "\",\"datum\":" + 
			"\"" + shjCore.g_GERMAN_DATE_FORMAT.format(rBem.getDate("dtmStudentBemerkungDatum")) + "\", \"autor\":\"" + shjCore.string2JSON(rBem.getString("strDozentNachname")) + "\"}}");
}%>]}

