<%@page import="java.sql.PreparedStatement"%>
<%@ page contentType="text/json" errorPage="../../error.jsp" session="true" import="java.util.Date, java.util.List,java.io.File,de.shj.UP.util.CommandRunner,java.util.Random,de.shj.UP.beans.student.ExamIterator,de.shj.UP.transcript.*,de.shj.UP.data.Pruefung,de.shj.UP.transcript.Fachnote,de.shj.UP.data.shjCore,de.shj.UP.HTML.HtmlDate,java.sql.ResultSet" %>
<jsp:useBean id="student" scope="session" class="de.shj.UP.logic.StudentData" />
<jsp:useBean id="seminar" scope="session" class="de.shj.UP.logic.SeminarData" />
<%@include file="../../../fragments/checkLoginStudent.jsp" %>
<%! String nf(float f){
	//String sReturn = String.valueOf(f).replaceAll("\\.0","");
	String sReturn = String.valueOf(f);
	if(sReturn.contains(".")){
		sReturn = sReturn.substring(0, sReturn.indexOf('.')+2);
		sReturn = sReturn.replace('.',',');
	}else{
		sReturn += ",0";
	}
	return sReturn;
}float minLP(String sFach){
	// Es werden bei 75% 20 LP ï¿½K mitgerechnet, bei 50% 10 ï¿½K
	if(sFach.contains("75")) return (float)133;
	if(sFach.contains("50")) return (float)84;
	if(sFach.contains("25")) return (float)35;
	return -1;
}String getLeistung(ResultSet rst, String sKurstitel, Object sLP)throws Exception{
    String sReturn="";
    String sLeistung =rst.getString("strLeistungBezeichnung"); 
    // Zweck der Fkt.: wenn Leistungebezeichnung und Kurstitel
    // in eine Zeile passen (also weniger als 67 Zeichen beinhalten),
    // dann sollen sie in eine Zeile geschrieben werden; sonst 
    // wird ein Umbruch (\\par) eingefügt.
    String sInsertLinebreak = (sLeistung + sKurstitel).length() <70 ? " " : "<br />";
     sReturn="<td class=\"leistung\">" + sLeistung + 
             " (" + shjCore.g_GERMAN_DATE_FORMAT.format(rst.getDate("dtmStudentLeistungAusstellungsd")) + ")";
    if(!sKurstitel.equals("")) {
        sReturn+=sInsertLinebreak +"<span class=\"grey\">&quot;" + sKurstitel + "&quot;</span>";
    }
        sReturn+="</td><td class=\"note\">" + (rst.getBoolean("blnStudentLeistungAnerkannt") ? "(*) " : "") + (rst.getFloat("sngNoteWertDE")==0 ? "ub" : nf(rst.getFloat("sngNoteWertDE"))) + 
            " (" + (sLP==null ? "--" : nf(rst.getFloat("sngStudentLeistungCreditPts"))) + " LP)</td>";

    return sReturn;
}String getPruefungen(long lSeminarID, String sMatrikelnummer)throws Exception{
    ExamIterator absolviert=new ExamIterator(lSeminarID, sMatrikelnummer);
	boolean bStart=true;
        String sReturn ="<h3>Abgeschlossene Prüfungen</h3><ul>";
        String sNote="";
        int ii=0;
	while(absolviert.next()){ii++;
            sNote=absolviert.StudentPruefung().getStudentPruefungNote().replace('.',',');
            if(sNote.equals("0")) sNote = "unbenotet";
            sReturn += "<li> ";
            try{
                sReturn += absolviert.getPruefungBezeichnung() + " (" + shjCore.g_GERMAN_DATE_FORMAT.format(absolviert.StudentPruefung().getStudentPruefungAusstellungsd()) + "), Note: " + sNote + "</li>";
            }catch(Exception eDateNotOK){
                sReturn += absolviert.getPruefungBezeichnung() + ", Note: " + sNote + "</li>";
            }

    }
    if(ii==0) sReturn = "<h3>Keine Orientierungs- oder Zwischenprüfung registriert.</h3>";
    else sReturn +="</ul>";
    return sReturn;
}%>
<%if(request.getParameter("permanent")!=null){%><jsp:forward page="add_permanent.jsp" /><%}if(request.getParameter("english").startsWith("t")) {%><jsp:forward page="add_en.jsp" /><%}
float fCreditPointSum = 0;	float fCreditPointSum2= 0;	float fWeighedGrade = 0;int	iCreditCount=0;float fSumNeeded=0;String sGradeTrend="";String sKurstitel="";String sModulBezeichnung="";float dLPSum=0;
        if(request.getParameter("matrikelnummer")!=null) student.init(seminar.getSeminarID(), request.getParameter("matrikelnummer"));
        long lPruefungID = Long.parseLong(seminar.lookUp("lngPruefungID", "tblSdPruefungXFach", "\"lngSdSeminarID\"=" + seminar.getSeminarID() + 
			" and \"intFachID\"=" + student.getFachID(seminar.getSeminarID()) + " and \"blnPruefungFachAbschluss\"=true"));
	String sPO = new Pruefung(student.getSeminarID(), lPruefungID).getPruefungsordnung();
	Fachnote note = new Fachnote(student.getMatrikelnummer(), seminar.getSeminarID(), student.getFachID(seminar.getSeminarID()));
	ResultSet rst = seminar.sqlQuery(
			"SELECT x2.\"blnStudentLeistungAnerkannt\",x2.\"lngKlausuranmeldungKurstypID\", x2.\"dtmStudentLeistungAusstellungsd\", x2.\"lngKlausuranmeldungKursID\", x2.\"strSLKursTitel\", x2.\"strSLKursTitel_en\", x2.\"lngStudentLeistungCount\", mxl2.\"strCustom2\", n2.\"sngNoteWertDE\", n2.\"strNoteNameDE\", x2.\"sngStudentLeistungCreditPts\", 't'::bool as bestanden, pxm2.\"lngPruefungModulSequence\" as sort0, m2.\"lngModulID\" as sort1, l2.\"lngLeistungID\" as sort2, m2.\"strModulBezeichnung\", l2.\"strLeistungBezeichnung\", pxm2.\"sngMinCreditPtsPLeistung\" " + 
			"FROM \"tblSdModulXLeistung\" mxl2, \"tblSdPruefungXModul\" pxm2, \"tblSdLeistung\" l2, \"tblSdModul\" m2,  \"tblBdStudentXLeistung\" x2, \"tblSdNote\" n2 " +
			"WHERE mxl2.\"lngSdSeminarID\" = pxm2.\"lngSdSeminarID\" AND " +
			  "n2.\"lngSdSeminarID\"=" + student.getSeminarID() + " and " +
			  "x2.\"intNoteID\"=n2.\"intNoteID\" and " + 
			  "n2.\"lngSdSeminarID\"=x2.\"lngSdSeminarID\" and " + 
			  "n2.\"blnNoteBestanden\"='t' AND " +
			  "mxl2.\"lngModulID\" = pxm2.\"lngModulID\" AND " +
			  "l2.\"lngSdSeminarID\" = mxl2.\"lngSdSeminarID\" AND " + 
			  "l2.\"lngLeistungID\" = mxl2.\"lngLeistungID\" AND  " +
			  "m2.\"lngSdSeminarID\" = mxl2.\"lngSdSeminarID\" AND  " +
			  "m2.\"lngModulID\" = mxl2.\"lngModulID\" AND " +
			  "pxm2.\"lngSdSeminarID\" = " + student.getSeminarID() + " AND " +
			  "mxl2.\"lngSdSeminarID\" = " + student.getSeminarID() + " AND " +
			  "l2.\"lngSdSeminarID\" = " + student.getSeminarID() + " AND " +
			  "m2.\"lngSdSeminarID\" = " + student.getSeminarID() + " AND " +
			  "pxm2.\"lngPruefungID\" = " + lPruefungID + " AND " +
			  "x2.\"lngSdSeminarID\"=" + student.getSeminarID() + " and " +
			  "x2.\"lngLeistungsID\"=l2.\"lngLeistungID\" and " +
			  "x2.\"lngModulID\"=m2.\"lngModulID\" and  " +
			  "x2.\"strMatrikelnummer\"='" + student.getMatrikelnummer() + "' " +
				" union ALL " +
				"SELECT x4.\"blnStudentLeistungAnerkannt\",x4.\"lngKlausuranmeldungKurstypID\", x4.\"dtmStudentLeistungAusstellungsd\", x4.\"lngKlausuranmeldungKursID\", x4.\"strSLKursTitel\", x4.\"strSLKursTitel_en\", x4.\"lngStudentLeistungCount\", '', n4.\"sngNoteWertDE\", n4.\"strNoteNameDE\", x4.\"sngStudentLeistungCreditPts\", 't'::bool as bestanden, 1500 as sort0, 999999 as sort1, l4.\"lngLeistungID\" as sort4, 'Zusatzleistungen', l4.\"strLeistungBezeichnung\", 0 " + 
				"FROM \"tblSdLeistung\" l4, \"tblBdStudentXLeistung\" x4, \"tblSdNote\" n4 " +
				"WHERE " +
				  "n4.\"lngSdSeminarID\"=" + student.getSeminarID() + " and " +
				  "x4.\"intNoteID\"=n4.\"intNoteID\" and " + 
				  "n4.\"lngSdSeminarID\"=x4.\"lngSdSeminarID\" and " + 
				  "n4.\"blnNoteBestanden\"='t' AND " +
				  "l4.\"lngSdSeminarID\" = " + student.getSeminarID() + " AND " +
				  "x4.\"lngSdSeminarID\"=" + student.getSeminarID() + " and " +
				  "x4.\"lngLeistungsID\"=l4.\"lngLeistungID\" and " +
				  "x4.\"lngModulID\" is null and  " +
                                  "x4.\"blnStudentLeistungKlausuranmeldung\"!='t' AND " +
				  "x4.\"strMatrikelnummer\"='" + student.getMatrikelnummer() + "' " +				  
		 "order by sort0, sort1, sort2;");
        String sTranskript="<h1>Leistungsübersicht für " + (student.getStudentFemale() ? "Frau " : "Herrn ") + 
    student.getStudentVorname() + " " + student.getStudentNachname() + "</h1>" +
    "<h2>" + student.Fach().getFachTranskriptName() +"</h2>" +
    "<p>" + (student.getStudentFemale() ? "Frau " : "Herr ") + student.getStudentVorname() + " " + student.getStudentNachname() + ", " +
    "geboren am " + shjCore.g_GERMAN_DATE_FORMAT.format(student.getStudentGeburtstag()) + " in " + student.getStudentGeburtsort()+ ", " +
    "Matrikelnummer " + student.getMatrikelnummer() + ", im " + student.getFachsemester() + ". Fachsemester, hat im o.g. Studiengang folgende Leistungen erbracht: </p>";
boolean bStart=true;String sModulbezAlt=""; String sModulBezCurr="";long lModulIDOld=-1;
	while(rst.next()){
			sModulBezCurr=rst.getString("strModulBezeichnung");
			sKurstitel=shjCore.normalize(rst.getString("strSLKursTitel")).replaceAll("'", "\'"); 
			sModulBezeichnung=sModulBezCurr; 
			if (sKurstitel==null) sKurstitel=""; 
			sKurstitel= (sKurstitel.trim().equals("") ? "" : sKurstitel); 
			String sLP = rst.getString("sngStudentLeistungCreditPts"); 
			if(sLP!=null && !sModulBezeichnung.contains("Über") && !sModulBezeichnung.contains("Zusatz")) dLPSum += Float.parseFloat(sLP);  

			if(!sModulBezCurr.equals(sModulbezAlt)){ 
				if(!bStart && !sModulbezAlt.contains("Über") && !sModulbezAlt.contains("Zusatz")){
					if((fSumNeeded-fCreditPointSum2)>0){
						sTranskript+="<td class=\"modulsumme\">Vorläufige Modulnote (" + nf(fCreditPointSum2) + " von " + nf(fSumNeeded) +" LP erbracht)</td><td class=\"modulnote\">" +  (fCreditPointSum==0 ? "k.A." : note.getModul(lModulIDOld).getModulnote()) + "</td></tr>";
					}else{
                                                sTranskript+="<td class=\"modulsumme\">Modulnote </td><td class=\"modulnote\">" +  (fCreditPointSum==0 ? "k.A." : note.getModul(lModulIDOld).getModulnote() + " (" + nf(fCreditPointSum2) + " LP)") + "</td></tr>";
					}
                                        

				}
                                if(!bStart && sModulbezAlt.contains("Über")){
                                    if((fSumNeeded-fCreditPointSum2)>0){
                                            sTranskript+="<td class=\"modulsumme\">Unbenotet (" + nf(fCreditPointSum2) + " von " + nf(fSumNeeded) +" LP erbracht)</td><td class=\"modulnote\"></td></tr>";
                                    }else{
                                            sTranskript+="<td class=\"modulsumme\">Komplett</td><td class=\"modulnote\">(" + nf(fCreditPointSum2) + " LP)</td></tr>";
                                    }      
                                }
                                sModulbezAlt=sModulBezCurr;
sTranskript +="</table><table><tr><td colspan=\"2\"><h4 style=\"margin-bottom:1px\">" + sModulBezeichnung + "</h4></td></tr><tr>";
	fSumNeeded=rst.getFloat("sngMinCreditPtsPLeistung");
	fCreditPointSum = 0;
	fCreditPointSum2 = 0;
	fWeighedGrade = 0;
	iCreditCount=0;
	lModulIDOld=rst.getLong("sort1");
}// end new Modul
sTranskript+=getLeistung(rst, sKurstitel, sLP) + "</tr>";
bStart=false;
String sNote = rst.getString("strNoteNameDE");
float fLP = rst.getFloat("sngStudentLeistungCreditPts"); 
float fWert=rst.getFloat("sngNoteWertDE");
if(rst.getBoolean("bestanden")) fCreditPointSum2 += fLP;
if(fWert>0){
	fCreditPointSum += fLP;
	fWeighedGrade += fLP * fWert;
	iCreditCount++;
}
}
if(bStart) throw new Exception("Es wurden noch keine Scheine oder Module absolviert. Ein Transkript kann nicht ausgestellt werden.");
if(!sModulbezAlt.contains("bergreif") && !sModulbezAlt.contains("Zusatz")){
	if((fSumNeeded-fCreditPointSum2)>0){
		sTranskript+="<td class=\"modulsumme\">Vorläufige Modulnote (" + nf(fCreditPointSum2) + " von " + nf(fSumNeeded) + " LP erbracht) </td><td class=\"modulnote\">" +  (fCreditPointSum==0 ? "k.A." : note.getModul(lModulIDOld).getModulnote()) + "</td>";
	}else{
		sTranskript+="<td class=\"modulsumme\">Modulnote </td><td class=\"modulnote\">" + note.getModul(lModulIDOld).getModulnote() + " (" + nf(fSumNeeded) + " LP)" + "</td>";
	}
}
if(!bStart && sModulbezAlt.contains("Über")){
    if((fSumNeeded-fCreditPointSum2)>0){
            sTranskript+="<td class=\"modulsumme\">Unbenotet (" + nf(fCreditPointSum2) + " von " + nf(fSumNeeded) +" LP erbracht)</td><td class=\"modulnote\"></td></tr>";
    }else{
            sTranskript+="<td class=\"modulsumme\">Komplett</td><td class=\"modulnote\">(" + nf(fCreditPointSum2) + " LP)</td></tr>";
    }      
}
sTranskript+="</table>";
sTranskript+= "<br />" + getPruefungen(seminar.getSeminarID(), student.getMatrikelnummer());

if(note.isComplete()){
    sTranskript +="<h3>Studienfach-Note: " + note.getGradeName() + "(" + note.getGrade().replace('.',',') + ")</h3>";
}else{
    if(note.getPreliminaryGrade().startsWith("0")){
        sTranskript +="<br /><br />";
    }else{
        sTranskript +="<h3>Vorläufige Studienfach-Note: " +  note.getPreliminaryGrade().replace('.',',') + "</h3>";
    }
}
String sVerify = new RandomString(6).nextString();
if(note.getSummeLPErreicht().floatValue() > student.Fach().getFachCreditPtsRequired()){
   sTranskript +="<p style=\"color:#990000; font-weight:bold\">Es wurden mehr Leistungspunkten erbracht als notwendig &mdash;"
           + "die Leistungsübersicht sollte von einem Studienberater korrigiert werden.</p><br />" +
        "<small>(*) = Leistungsnachweis von anderer Universität, in Heidelberg anerkannt.<br />" +
        "LP  = ECTS Leistungspunkte (1 Leistungspunkt = 30 Std. Arbeit)<br />" +
        "ub  = unbenotet bestanden<br />" +
        "1=sehr gut, 2=gut, 3=befriedigend, 4=ausreichend, 5=mangelhaft</small>";
}else{
    sTranskript +="<b>Es wurden " + nf(note.getSummeLPErreicht().floatValue()) + " von " + 
        nf(student.Fach().getFachCreditPtsRequired())+ " Leistungspunkten erbracht</b><br /><br />" +
        "<small>(*) = Leistungsnachweis von anderer Universität, in Heidelberg anerkannt.<br />" +
        "LP  = ECTS Leistungspunkte (1 Leistungspunkt = 30 Std. Arbeit)<br />" +
        "1=sehr gut, 2=gut, 3=befriedigend, 4=ausreichend, 5=mangelhaft</small>";
}
String sDokumentName="Deutsches Transkript";
PreparedStatement pstm = student.prepareStatement("INSERT INTO \"tblBdStudentDokument\"(" +
            "\"lngSdSeminarID\", \"strMatrikelnummer\", " + 
            "\"strVerifikationscode\", \"strDokument\", \"strStudentDokumentName\") " + 
        "VALUES (?,?,?,?,?);");
pstm.setLong(1, student.getSeminarID());
pstm.setString(2, student.getMatrikelnummer());
pstm.setString(3, sVerify);
pstm.setString(4, sTranskript);
pstm.setString(5, sDokumentName);
try{
    pstm.execute();
}catch(Exception e){
    throw new Exception("Die Datenbank weigert sich, das Transkript zu speichern -- sorry.");
}

// Erstelle PDF-VERSION
String sURL = new de.shj.UP.data.shjCore().getHTTPBase(request, "8084") + "/../dokument.jsp?" + 
        "matrikelnummer=" + student.getMatrikelnummer() + "&datum=" + shjCore.g_GERMAN_DATE_FORMAT.format(new java.util.Date()) + 
        "&verifikation=" + sVerify;

%>{"transkript":{"seminar_id":"<%=seminar.getSeminarID() %>",
"matrikelnummer":"<%=student.getMatrikelnummer()%>",
"dok_name":"<%=sDokumentName %>",
"datum":"<%=shjCore.g_ISO_DATE_FORMAT.format(new java.util.Date()) %>",
"verifikationscode":"<%=sVerify %>"}}
<%-- <jsp:forward page="<%=sFwd%>" /> --%>
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
<%--
    @TODO
    ===========================

    SYNOPSIS (German)
    ===========================
    2013, Oct 21, 2013
    Erzeugt. 
    
    Erzeugt eine HTML-Version des Transkripts (analog zur
    RTF-Version in /print) und speichert diese HTML-
    version als "strDokument" in "tblBdStudent"."strDokument".

    Das Dokument wird mit dem Ausstellungsdatum und einer 
    zufälligen Verifikationsnummer markiert.

    Die Seite /[sem]/Faculty/dokument.jsp gibt das HTML-Dokument
    aus (wenn die Parameter matrikelnummer, verifikation und datum 
    korrekt übergeben werden).

    Diese self-print Seite lässt über wkhtml2pdf-i386 per
    dokument.jsp ein PDF dieser Ausgabe erzeugen und gibt es aus.
    
    
    Expected SESSION PROPERTIES
    ===========================
    (TODO, siehe oben)

    Expected PARAMETER(S):
    ===========================
    [matrikelnummer  [long]]

    Returns
    =======
--%>