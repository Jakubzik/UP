<%@page import="java.util.Calendar"%>
<%@ page language="java" import="java.util.Date,de.shj.UP.data.shjCore" contentType="text/html; charset=UTF-8" session="false"
    pageEncoding="UTF-8"%><!DOCTYPE html> 
<%	
	// Schnittstelle Parameter
        if(request.getParameter("matrikelnummer")==null || 
                request.getParameter("datum")==null || 
                request.getParameter("verifikation")==null) return;
        try{Long.parseLong(request.getParameter("matrikelnummer"));}catch(Exception eWrongMatrikelnummer){return;}
        String sISO="";
        String sIn6Months="";
        if((request.getParameter("matrikelnummer")+request.getParameter("datum")+request.getParameter("verifikation")).indexOf('\"')>=0) return;
        Date dEndOfVerification=null;
        try{
            sISO=shjCore.g_ISO_DATE_FORMAT.format(shjCore.g_GERMAN_DATE_FORMAT.parse(request.getParameter("datum")));
            dEndOfVerification = shjCore.g_GERMAN_DATE_FORMAT.parse(request.getParameter("datum"));
            Calendar cal = Calendar.getInstance();  
            cal.setTime(dEndOfVerification);  
            cal.add(Calendar.MONTH, 6);
            dEndOfVerification=cal.getTime();
        }catch(Exception eDateFmt){return;}
        if(request.getParameter("verifikation").length()!=6){return;}
        String sTmp=(new shjCore()).lookUp("strDokument", "tblBdStudentDokument", 
            "\"strMatrikelnummer\"='" + request.getParameter("matrikelnummer") + "' and " + 
            "\"strVerifikationscode\"='" + request.getParameter("verifikation") + "' and \"dtmStudentDokumentDatum\"='" + sISO + "'");
        if(sTmp.startsWith("#")){
            out.write("<h1>Dokument nicht gefunden, sorry :-(</h1>"); return;
        }
%>
<html>
<head>
<style type="text/css">
    .body{
        
    }
    div.content{
        width: 900px;
    }
    div.verify{
        width: 750px;
        margin: 2em;
        margin-left: 0;
        padding:1em;
        border: 1px solid;
        border-color:#990000;
    }
    .institutslogo{
        text-align: right;
        float:right;
        margin:20px;
        margin-right: 20px;
    }
.grey{
  color:#777;
}
table{
  border:none;
  width: 800px;
}
td.leistung{
  width: 660px;
}
td.note{
  padding-left:5px;
  vertical-align:bottom;
  text-align:right;
}
td.modulsumme{
  font-weight:bold;
  border-top: 1px solid #990000;
}
td.modulnote{
  font-weight:bold;
  font-color:#990000;
  padding-left:5px;
  vertical-align:bottom;
  text-align:right;
  border-top: 1px solid #990000;
}
td.verifikation{
    text-align:left;
}
</style>
</head>
<body>
    <div class="content">
    	<a href="https://github.com/Jakubzik/UP/" target="_blank">
			<img class="institutslogo" src="img/UP_logo.png" alt="Hier eigenes Logo einsetzen" width="204">
    	</a>

    <%=sTmp%>
    <br /><hr>

        <div class="verify">
            Dokument automatisch erzeugt/Document was generated automatically.
            <br /><br />Die Übereinstimmung der Angaben auf dem Dokument mit den  
            Daten des Instituts kann bis zu sechs Monate nach der
            Ausstellung online hier überprüft werden:/The validity of this document
            can be verfied online up to six months after its generation here:<br />
            <a href="http://verifikation.uni-hd.de" target="_blank">http://verifikation.uni-hd.de</a><br /><hr/>
        <table>
            <tr>
                <td width="50%">Matrikelnummer/matriculation no.</td><td class="verifikation"><%=request.getParameter("matrikelnummer")%></td>
            </tr>
            <tr>
                <td>Ausstellungsdatum/issue date</td><td class="verifikation"><%=request.getParameter("datum")%></td>
            </tr>
            <tr>
                <td>Verifikationsnr./verification no.</td><td class="verifikation"><%=request.getParameter("verifikation")%></td>
            </tr>
            <tr>
                <td>Verifkation möglich bis/online verification feasible until (dd.mm.yyyy)</td><td class="verifikation"><%=shjCore.g_GERMAN_DATE_FORMAT.format(dEndOfVerification) %></td>
            </tr>
        </table>
    </div>
            
        </div>
</body>