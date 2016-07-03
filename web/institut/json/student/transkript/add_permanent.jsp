<%--
    Utility JSP: 
    ruft JSP-Seite (Transkript) ab und speichert sie in tblBdStudentDokument.jsp

    @TODO
    ===========================

    SYNOPSIS (German)
    ===========================
    2013, Dec 21, shj    erzeugt. 
    2014, Jun 16, shj    Glitch mit https-Verbindungen behoben
    
    Ruft die Seite über java.net.URL auf, erhält dabei 
    die Session mit Cookies, fängt die Ausgabe (RTF) ab 
    und speichert sie in der Datenbank.

    So können RTF-Versionen der Transkripte in der 
    Datenbank abgelegt werden, z.B. wenn das 
    Prüfungsamt einen BA-Abschluss ausstellt. 
    Auch wenn die Leistungen später archiviert 
    werden, bleibt so das Transkript in seiner 
    Form erhalten.
    
    Expected SESSION PROPERTIES
    ===========================
    user        muss initialisiert sein (AccessLevel 500 oder mehr)

    Expected PARAMETER(S):
    ===========================
    englisch    [null|not-null]     speichert das englische Transkript

    Returns
    =======
    ?


    
--%><%@page import="de.shj.UP.data.shjCore"%>
<%@page import="java.util.Random"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.util.List"%>
<%@page import="java.io.InputStreamReader"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="java.net.HttpURLConnection"%>
<%@page import="java.net.URLConnection"%>
<%@page import="java.net.URL"%>
<jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" />
<jsp:useBean id="student" scope="session" class="de.shj.UP.logic.StudentData" />
<%@ page language="java" session="true" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%><%@include file="../../../fragments/checkVersion.jsp" %>
<%@include file="../../../fragments/checkLogin.jsp" %>
<%@include file="../../../fragments/checkAccess500.jsp" %>
<%@include file="../../../fragments/checkInitStudent.jsp" %>
<%
    // Stelle den URL der Seite /print/transcript.rtf 
    // ODER bei gpa-Transkript ../gpa/signUpJSON.jsp
    // zusammen:
    String sLang = (request.getParameter("englisch")==null ? "" : "_e");
    String sServerPort=String.valueOf(request.getServerPort());
    if(sServerPort.equals("443")) sServerPort="80";
    String sURL = request.getServerName() + ":" + sServerPort + "/" + request.getRequestURI();
    
    sURL="http://" + sURL.substring(0,sURL.lastIndexOf("json/")) + "../../gpa/signUpJSON.jsp?matrikelnummer=" + student.getMatrikelnummer();

    URL url = new URL(sURL);
    
    URLConnection urlConnection = url.openConnection();

    // Übertrage die Cookies auf die Anfrage, insbesondere 
    // (bei Tomcat) JSESSIONID=session.getSessionId().
    for(int ii=0;ii<request.getCookies().length;ii++){
        urlConnection.addRequestProperty("Cookie", request.getCookies()[ii].getName() + "=" + request.getCookies()[ii].getValue());
    }
    HttpURLConnection connection = null;
    if(urlConnection instanceof HttpURLConnection){
       connection = (HttpURLConnection) urlConnection;
       System.out.println("Kodierung Transkirpt: " + urlConnection.getContentEncoding());
    }else{
       System.out.println("Please enter an HTTP URL.");
       return;
    }
    
    // Hole mir die Seite als String:
    BufferedReader in = new BufferedReader(
    new InputStreamReader(connection.getInputStream(), "iso-8859-1"));
    
    String urlString = "";
    String current;
    //while((current = in.readLine()) != null){
    while((current = in.readLine()) != null){
       urlString += current;
    }
    
    PreparedStatement pstm=user.prepareStatement("insert into \"tblBdStudentDokument\" ("
            + "\"lngSdSeminarID\", \"strMatrikelnummer\", \"dtmStudentDokumentDatum\", " +
            "\"strVerifikationscode\", \"strDokument\", \"strStudentDokumentName\", " +
            "\"blnStudentDokumentDauertranskript\") " +
            "VALUES (?, ?, ?, " +
            "?, ?, ?, " +
            "?);");
    int ii=1;
    String sVerify=new RandomString(4).nextString();
    pstm.setLong(ii++, user.getSdSeminarID());
    pstm.setString(ii++, student.getMatrikelnummer());
    pstm.setDate(ii++, new java.sql.Date(new java.util.Date().getTime()));
    pstm.setString(ii++, sVerify);
    pstm.setString(ii++, urlString.trim());
    
    if(b_gpa_json)
        pstm.setString(ii++, "JSON Archiv (GPA)");
    else
        pstm.setString(ii++, "Transkript" + (sLang.equals("") ? "" : " Englisch"));
    
    pstm.setBoolean(ii++, true);
    boolean bSuccess=(pstm.executeUpdate()==1);
%>{"transkript":{"success":"<%=bSuccess%>","dok_name":"Transkript <%=(sLang.equals("") ? "" : " Englisch")%>", "datum":"<%=shjCore.g_ISO_DATE_FORMAT.format(user.g_TODAY) %>","verifikationscode":"<%=sVerify%>"}}
<%!
final class RandomString{

  private final char[] symbols = new char[36];

  private void init() {
    for (int idx = 0; idx < 10; ++idx)
      symbols[idx] = (char) ('0' + idx);
    for (int idx = 10; idx < 36; ++idx)
      symbols[idx] = (char) ('a' + idx - 10);
  }

  private final Random random = new Random();

  private final char[] buf;

  public RandomString(int length)
  {
    if (length < 1)
      throw new IllegalArgumentException("length < 1: " + length);
    buf = new char[length];
    init();
  }

  public String nextString()
  {
    for (int idx = 0; idx < buf.length; ++idx) 
      buf[idx] = symbols[random.nextInt(symbols.length)];
    return new String(buf);
  }

}
%>