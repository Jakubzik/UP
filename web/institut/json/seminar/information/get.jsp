<%--
    @Neu Juni 2014, hj
    
    JSP-Object "seminar/information/get"

    Informationen des Seminars (Bewegungsdaten)

    @TODO
    ===========================
    Filterfkt. einbauen?
    Limitiere auf dtm >= now - 6months?

    SYNOPSIS (German)
    ===========================   
    Üblicher Lifecycle: GET

    
    Expected SESSION PROPERTIES
    ===========================
    seminar        muss initialisiert sein (AccessLevel >=1),

    Expected PARAMETER(S):
    ===========================
    [info_after:    <letzter vorgehaltener Timestamp> [milliseconds], optional]

    Returns
    =======
    Array of objects with remarks:
  informationen:[
    {"information":{
        "inhalt":<Inhalt der Info,                [Text]>,
        "autor":<voller Name des Autors                 [Text]>,
        "id":<InhaltID (relevant für Kommentarfkt.    [long]>,
        "timestamp":<Datum der Info                        [timestamp]>,
        "ms_timestamp":                            [timestamp, milliseconds]
        "kommentare":[
            "kommentar":{
                "inhalt":
                "autor":
                "id"
                "timestamp":
                "ms_timestamp":                            [timestamp, milliseconds]
            }
        ]
    }

    Sample Usage
    ============

    
--%><%@page import="java.sql.Timestamp"%>
<%@page contentType="text/json" pageEncoding="UTF-8" import="java.sql.ResultSet,de.shj.UP.logic.AktuellesIterator,java.text.SimpleDateFormat, de.shj.UP.data.Dozent,de.shj.UP.data.shjCore" session="true" isThreadSafe="false"  errorPage="../../error.jsp" %>
<jsp:useBean id="seminar" scope="session" class="de.shj.UP.HTML.HtmlSeminar" />
<%@include file="../../../fragments/checkVersion.jsp" %>
{"informationen":[
<%long lERR_BASE=214000 + 100;    // Information + Get
        final long g_iNeueInfoLimit=15; // wie viele Infos sollen heruntergeladen und angezeigt werden?
	boolean bFirstLoop=true;boolean bFirstInfo=true;boolean bHadKommentare=false;boolean bIsFirstKommentar=true;
        Timestamp tme=null;
        if(request.getParameter("info_after")!=null){
            try{
                tme=new Timestamp(Long.parseLong(request.getParameter("info_after")));
            }catch(Exception eFormatWrong){
                throw new Exception("{\"error\":\"Aktuelle Informationen können nicht übergeben werden. Am besten klicken Sie auf 'Neu Laden'.\"" + 
                    ",\"errorDebug\":\"Der optionale Parameter >info_after< (für die letzte vorgehaltene Info) wurde zwar übergeben, hat den nicht-numerischen Wert " + request.getParameter("info_after") + ".\",\"errorcode\":" + lERR_BASE + 3 + ",\"severity\":10}");               
            }
        }
        ResultSet rInfo=seminar.sqlQuery("SELECT " +
                  "d.\"strDozentVorname\" || ' ' || d.\"strDozentNachname\" as autor, " +
                  "d.\"lngDozentID\" as autor_id, " +
                  "i.\"strInformationInhalt\", " +
                  "i.\"dtmInformationDatum\", " +
                  "i.\"lngInformationID\", " +
                  "ik.\"strInformationKommentarInhalt\", " +
                  "ik.\"dtmInformationKommentarDatum\", " +
                  "ik.\"lngSdSeminarID\"," +
                  "d2.\"lngDozentID\" as kommentator_id," +
                  "d2.\"strDozentVorname\" || ' ' || d2.\"strDozentNachname\" as kommentator " +
                "FROM " +
                  "\"tblBdInformation\" i " +
                  "left join \"tblBdInformationKommentar\" ik on (" +
                        "ik.\"lngSdSeminarID\"=i.\"lngSdSeminarID\" and " +
                        "ik.\"lngInformationID\" = i.\"lngInformationID\") " +
                  "left join \"tblSdDozent\" d2 on (" +
                        "ik.\"lngSdSeminarID\"=d2.\"lngSdSeminarID\" and " +
                        "ik.\"lngDozentID\"=d2.\"lngDozentID\")," +
                  "\"tblSdDozent\" d " +
                "WHERE " +
                  "i.\"lngSdSeminarID\"=" + seminar.getSeminarID() + " and " +
                  "i.\"lngSdSeminarID\" = d.\"lngSdSeminarID\" AND " +
//                  ((tme==null) ? "" : " (\"dtmInformationDatum\">'" + tme.toString() + "' or \"dtmInformationKommentarDatum\">'" + tme.toString() + "') and ") +
                  ((tme==null) ? "" : " (\"dtmInformationDatum\">'" + tme.toString() + "' or exists(select \"lngSdSeminarID\" from \"tblBdInformationKommentar\" k2 where k2.\"dtmInformationKommentarDatum\">'" + tme.toString() + "' and k2.\"lngSdSeminarID\"=i.\"lngSdSeminarID\" and k2.\"lngInformationID\"=i.\"lngInformationID\")) and ") +
                  "i.\"lngDozentID\" = d.\"lngDozentID\" " +
                "order by i.\"lngSdSeminarID\", i.\"lngInformationID\" desc, ik.\"lngInformationKommentarID\" asc limit " + g_iNeueInfoLimit + ";");
        
        long lInfoID=-1;long lInfoIDOld=-100;
        if(tme!=null) System.out.println(request.getRemoteHost() + " [" + request.getHeader("User-Agent") + "] polls news with " + tme.toString());
	while(rInfo.next()){
                if(tme!=null) System.out.println("... found");
                lInfoID=rInfo.getLong("lngInformationID");
		
                // Ist das eine _neue_ Information? (Oder nur ein Kommentar?)
                if(lInfoID!=lInfoIDOld){
                    lInfoIDOld=lInfoID;
                    if(!bFirstInfo) out.write("]}},");
                    else if(!bFirstLoop) out.write(",");
                    
                    bFirstInfo=false;
                    bHadKommentare=false;
                    out.write("{\"information\":{" + 
                        "\"inhalt\":\"" + shjCore.string2JSON(rInfo.getString("strInformationInhalt")) + "\"," + 
                        "\"autor\":\"" + rInfo.getString("autor") + "\"," + 
                        "\"autor_id\":\"" + rInfo.getString("autor_id") + "\"," + 
                        "\"id\":\"" + rInfo.getString("lngInformationID") + "\"," +
                        "\"timestamp\":\"" + rInfo.getTimestamp("dtmInformationDatum") + "\"," +
                        "\"ms_timestamp\":\"" + (rInfo.getTimestamp("dtmInformationDatum")).getTime() + "\"," +
                        "\"kommentare\":[");
                }
                
                if(!(rInfo.getString("strInformationKommentarInhalt")==null)){
                    if(bHadKommentare) out.write(",");
                    out.write("{\"kommentar\":{" + 
                        "\"inhalt\":\"" + shjCore.string2JSON(rInfo.getString("strInformationKommentarInhalt")) + "\"," + 
                        "\"timestamp\":\"" + rInfo.getTimestamp("dtmInformationKommentarDatum") + "\"," +
                        "\"ms_timestamp\":\"" + (rInfo.getTimestamp("dtmInformationKommentarDatum")).getTime() + "\"," +
                        "\"autor_id\":\"" + rInfo.getString("kommentator_id") + "\"," + 
                        "\"autor\":\"" + rInfo.getString("kommentator") + "\"" + 
                    "}}");
                    bHadKommentare=true;
                }else{
                    bHadKommentare=false;
                }
                bFirstLoop=false;
	}
	rInfo.close();
%><%if(lInfoID>=0){%>]}}<%}%>]}
