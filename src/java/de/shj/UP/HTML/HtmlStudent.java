/**
 * Software-Entwicklung H. Jakubzik, Lizenz Version 1.0 (Uni Heidelberg)
 *
 *
 * Copyright (c) 2004 Heiko Jakubzik.  All rights reserved.
 *
 * http://www.heiko-jakubzik.de/
 * http://www.heiko-jakubzik.de/SignUp/
 * http://www.heiko-jakubzik.de/SignUp/hd/
 * mailto:heiko.jakubzik@t-online.de
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is not permitted.
 *
 * Lizenznehmer der Universitaet Heidelberg erwerben mit der Lizenz
 * das Recht, den Quellcode der Internetanwendung "SignUp" einzusehen,
 * zu veraendern und fuer den Eigenbedarf neu zu kompilieren.
 *
 * Die Weitergabe von Softwarekomponenten des Pakets durch die
 * Lizenznehmerin ist weder als Quelltext noch in kompilierter Form
 * ganz oder teilweise gestattet.
 *
 * Diese Lizenz erstreckt sich auf die Internetanwendung "SignUp",
 * also
 *
 * 1. das Datenschema,
 * 2. die Bibliothek "SignUp.jar"
 * 3. das Webarchiv  "SignUpXP.war"
 * 4. die Resource Bundles und Interfaces,
 * 5. das Access Frontend "SignUpXP",
 * 6. Konvertierungs-Skripte, XML und XSL Dateien,
 *    sowie alle vbs und hta Anwendungen zu Konvertierung
 *    von Excel und XML Dateien in SignUp-kompatibles Format,
 * 7. C# .NET Frontend Komponenten,
 * 8. die Webservice Spezifikation,
 * 9. die Dokumentation inkl. Bilder
 *
 * Das Rechtemanagement zur Neukompilierung unterliegt der
 * Kontrolle der Universtitaet Heidelberg. Fuer Kompilationen,
 * die von der Lizenznehmerin durchgefuehrt wurden, besteht
 * kein Anspruch auf Nachbesserung.
 *
 * Der Lizenzgeber behaelt es sich vor, die Lizenz auf
 * ein "Open Source"-Modell umzustellen.
 *
 * Bei Aenderungen am Datenmodell oder an Objekten _dieses_ Pakets
 * (com.shj.signUp.data) wird _dringend_ Ruecksprache mit dem
 * Autor der Software empfohlen.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL H. JAKUBZIK SOFTWARE-DEVELOPEMENT
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * 1. declarations
 *
 * 2. public Strings (Html-tables)
 *
 * 3. constructor
 *
 * @version 5-00-02 (auto-coded)
 *
 * change date              change author           change description
 * Dec 2003		            h. jakubzik             creation
 * Jan 04, 2004				h. jakubzik				StudentCreditsHtml: due to changed database (ECTS-grade), the Html table was expanded.
 * Jan 28, 2004				h. jakubzik				StudentMissingCreditsHtml: due to change in logic.StudentData, changed.
 * Feb 15, 2004				h. jakubzik				StudentCreditsHtml: now shows creditpoints (ECTS). There is, however,
 *   												the need to #hack and create a transcript instead of a mere table.
 *
 * May 04, 2004				h. jakubzik				StudentCreditsHtml: normalized a null value.
 * Oct 03, 2004				h. jakubzik				StudentCreditsHtml: mit blnStudentLeistungPruefung (Pr�fungsleistung vs. Studienleistung)
 * Nov  7, 2004				h. jakubzik				Added strSLKursTitel to .StudentExamAppsHtml()
 */

package de.shj.UP.HTML;

import java.sql.ResultSet;
import java.util.Locale;

/**
 *  Small helper-class around Html-display of student-related items in jsp frontend.
 *  Mainly, this class is supposed to help with tables about exams, credits and applications for tests ('Klausuranmeldungen').<br />
 *  As with all classes in package 'signUp.HTML,' it is required that you hand
 *  the Locale over, so that language-specific settings can be made. Currently, however,
 *  only the dates are localized. <br />
 *  As in all classes of this package (signUp.HTML), table-headers are formatted with
 *  css 'class="theaderstyle," and regulare fields with 'class="tcellstyle".'
 **/
public class HtmlStudent extends de.shj.UP.logic.StudentData{

	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 1.   D E C L A R A T I O N S
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------

	private long m_lngSeminarID=-1;
	private String m_strMatrikelnummer="";
	private Locale m_Locale;


	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 1.   P U B L I C  S T R I N G S   ( H T M L )
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------

	/**
	 * Html-Table listing all applications for tests of this student. ('Klausuranmeldungen').
	 * @return Html-table with all test-applications (! not really exams) of this student. Table looks like this: [ID Leistungbezeichnung (Dozent) Tag | ApplicationPeriod | LINK ("Go")]
	 * where 'LINK' is a Link to 'Confirm.jsp' with all kinds of params, notably among them 'cmdAct=DeleteCreditApplication,'
	 * which would probably better be placed elsewhere for the generality of this method.
	 * This method calls 'connect' before it accesses the database (this is relevant for the use in webservices on axis/.NET).
	 * @param lngSessionID_IN: SessionID of Student. Is needed in order to constuct the LINK (see above) with a valid SessionID.
	 * @param lngPID_IN: PID of requesting student.Like SessionID, this is needed for the LINK (s.a.).
	 * @throws Exception when connection to db is erroneous.
	 **/
	public String StudentExamAppsHtml(long lngSessionID_IN, long lngPID_IN) throws Exception{

		String strReturn="";
		String strKursID="";
		String strDozentNachname="";
		String strLeistungBezeichnung="";

		this.connect();

		ResultSet rst=StudentExamApplications();

		while(rst.next()){
			strDozentNachname=rst.getString("strDozentNachname");
			strLeistungBezeichnung=rst.getString("strLeistungBezeichnung");
			strKursID=rst.getString("lngKursID");
			strReturn+="<tr><td class=\"tcellstyle\"><strong>" + strKursID + " " +
				strLeistungBezeichnung + "<br />&quot;" + rst.getString("strSLKursTitel") + "&quot;</strong><br /> (" +
				strDozentNachname + " " + rst.getString("strKursTag") + "), <br />" + (rst.getBoolean("blnStudentLeistungPruefung") ? "als Pr&uuml;fungsleistung" : "als Studienleistung") + "</td>" +
				"<td class=\"tcellstyle\">" + this.shjGetLocalDate(rst.getDate("dtmKursScheinanmeldungVon"), m_Locale) + "  -  " + this.shjGetLocalDate(rst.getDate("dtmKursScheinanmeldungBis"), m_Locale) + "</td>" +
				"<td class=\"tcellstyle\">" + ((rst.getBoolean("blnAllowChanges")) ? ("<a href=\"Confirm.jsp?s=" + this.m_lngSeminarID + "&txtMatrikelnummer=" + this.m_strMatrikelnummer + "&txtKursID=" + strKursID + "&txtLeistungsID=" + rst.getString("lngLeistungsID") +
					"&txtLeistungBezeichnung=" + strLeistungBezeichnung + "&txtDozentNachname=" +
					strDozentNachname + "&SessionID=" + lngSessionID_IN + "&txtPID=" + lngPID_IN +
					"&txtStudentLeistungCount=" + rst.getString("lngStudentLeistungCount") +
					"&txtKursTag=" + rst.getString("strKursTag") + "&txtKursBeginn=" + rst.getString("dtmKursBeginn") + "&txtKursEnde=" + rst.getString("dtmKursEnde") +
					"&cmdAct=DeleteCreditApplication\">Go</a></td></tr>\n") : ("</td>"));

		}

		rst.close();
		rst=null;

		return strReturn;
	}

	/**
	 * Html-table with all credits of current student.
	 * @return Html-table with all credits (validated) of this student. Table looks like this: [LeistungBezeichnung (x Credits) | LeistungAussteller / Date | Grade]
	 * This method calls 'connect' before it accesses the database (this is relevant for the use in webservices on axis/.NET).
	 * @throws Exception when connection to db erroneous.
	 **/
	public String StudentCreditsHtml() throws Exception{

		String strReturn="";
		this.connect();

		ResultSet rst=StudentCredits();

		while(rst.next()){
			strReturn+="<tr><td class=\"tcellstyle\"><strong>" + rst.getString("strLeistungBezeichnung") + "</strong> (" + rst.getString("sngStudentLeistungCreditPts") + " Credits)</td>" +
				"<td class=\"tcellstyle\">" + rst.getString("strStudentLeistungAussteller") + "/" + this.shjGetLocalDate(rst.getDate("dtmStudentLeistungAusstellungsd"), m_Locale) + "</td>" +
				"<td class=\"tcellstyle\">" + "ECTS:" + rst.getString("strNoteECTSGrade") + ", " + rst.getString("strNoteECTSDefinition") + " = " + rst.getString("sngNoteECTSCalc") + " " + normalize(rst.getString("strStudentLeistungNote")) + " " + (rst.getBoolean("blnStudentLeistungPruefung") ? "als Pr&uuml;fungsleistung": "als Studienleistung") + "</td></tr>\n";
		}

		rst.close();
		rst=null;

		return strReturn;
	}

	/**
	 * Html-table with all exams of current student (Zwischenpruefung, BA, MA etc.)
	 * @return Html-table with all exams (validated, no applications) of this student. Table looks like this: [ExamDetails | Semester | Grade.]
	 * This method calls 'connect' before it accesses the database (this is relevant for the use in webservices on axis/.NET).
	 * @throws Exception when connection to db erroneous
	 **/
	public String StudentExamsHtml() throws Exception{

		String strReturn="";
		this.connect();

		ResultSet rst=StudentExams(false);

		while(rst.next()){
			strReturn+="<tr><td class=\"tcellstyle\"><strong>" + rst.getString("strPruefungBezeichnung") + "<br/>" + rst.getString("strPruefungBeschreibung") +  "</strong></td>" +
				"<td class=\"tcellstyle\">" + rst.getString("strStudentPruefungSemester") + "</td>" +
				"<td class=\"tcellstyle\">" + rst.getString("strStudentPruefungNote") + "</td></tr>\n";
		}

		rst.close();
		rst=null;

		return strReturn;
	}

	/**
	 * Html-table with exams the current student applied for.
	 * @return Html-table with all exam applications of this student. Table has only one column: ExamDetails (which exam).
	 * This method calls 'connect' before it accesses the database (this is relevant for the use in webservices on axis/.NET).
	 * @throws Exception when connection to db erroneous
	 **/
	public String StudentExamApplicationsHtml() throws Exception{

		String strReturn="";
		this.connect();

		ResultSet rst=StudentExams(true);

		while(rst.next()){
			strReturn+="<tr><td class=\"tcellstyle\"><strong>" + rst.getString("strPruefungBezeichnung") + "<br/>" + rst.getString("strPruefungBeschreibung") +  "</strong></td></tr>\n";
		}

		rst.close();
		rst=null;

		return strReturn;
	}

	/**
	 * Html-table with all 'sensible' exam options.
	 * @return Html-table with all sensible exam options (sensible means: correct fach and seminar for this student, student has not already applied or passed this exam).
	 * Table looks like this: [Pruefungbezeichnung |Abschluss [LA/MA/Promotion]| LINK],
	 * where LINK contains the Pruefunsbeschreibung (details) and href is '?txtPruefungID=x.'
	 * This method calls 'connect' before it accesses the database (this is relevant for the use in webservices on axis/.NET).
	 * @param lngSeminarID: relevant seminar,
	 * @param strPruefungBezeichnung: name of exam (e.g. Zwischenpr�fung, Orientierungspr�fung etc.)
	 * @param strQuery: query to add to 'LINK'
	 * @throws Exception when connection to db is erroneous
	 **/
	public String StudentExamOptionsHtml(long lngSeminarID, String strPruefungBezeichnung, String strQuery) throws Exception{

		String strReturn="";
		this.connect();
		ResultSet rst=StudentExamOptions(lngSeminarID, strPruefungBezeichnung);

		while(rst.next()){
			strReturn+="<tr><td class=\"tcellstyle\">" + rst.getString("strPruefungBezeichnung") + "</td>" +
						"<td class=\"tcellstyle\">" + rst.getString("strPruefungAbschluss") + "</td>" +
						"<td class=\"tcellstyle\"><a href=\"?txtPruefungID=" + rst.getString("lngPruefungID") + "&" + strQuery + "\">" + rst.getString("strPruefungBeschreibung") +  "</a></td></tr>\n";
		}

		rst.close();
		rst=null;

		return strReturn;
	}

	/**
	 * Html-table with inputs for missing credits.
	 * This method calls 'connect' before it accesses the database (this is relevant for the use in webservices on axis/.NET).
	 * @return Html-table to enter missing credits, looks like this: [Creditname | Input-Teacher | Input-Date | Input-Grade],
	 * where "Input" signifies that this is an &lt;input&gt; Html-element for the user to enter this information.
	 * @param lngSeminarID: Id of Seminar that has this exam,
	 * @param lngPruefungID, Id of this specific exam.
	 * @throws Exception when connection to db is erroneous
	 **/
	public String StudentMissingCreditsHtml(long lngSeminarID, long lngPruefungID) throws Exception{

		String strReturn="";
		String strLeistungID="";
		String strLeistungBezeichnung="";
		this.connect();

		ResultSet rst=StudentMissingCredits(lngSeminarID, lngPruefungID);

		while(rst.next()){
			strLeistungBezeichnung=rst.getString("strLeistungBezeichnung");
			strLeistungID=rst.getString("lngLeistungID");
			strReturn+="<tr><td class=\"tcellstyle\">" + strLeistungBezeichnung + "</td>" +
						"<td class=\"tcellstyle\"><input type=\"text\" name=\"txtLeistungAussteller-" + strLeistungID + "\" size=\"25\"/></td>" +
						"<td class=\"tcellstyle\"><input type=\"text\" name=\"txtLeistungAusstellungDatum-" + strLeistungID + "\" size=\"9\"/></td>" +
						"<td class=\"tcellstyle\"><input type=\"text\" name=\"txtLeistungNote-" + strLeistungID + "\" size=\"5\"/><input type=\"hidden\" name=\"txtLeistungBezeichnung-" + strLeistungID + "\" value=\"" + strLeistungBezeichnung + "\"</td></tr>";
		}

		rst.close();
		rst=null;

		return strReturn;
	}


	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 3.   C O N S T R U C T O R
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------

	/**
	 * Instantiate object.
	 * @param lngSeminarID_IN: id of seminar,
	 * @param strMatrikelnummer: matriculation number of student,
	 * @param locale: language-relevant information.
	 **/
	public HtmlStudent(long lngSeminarID_IN, String strMatrikelnummer, Locale locale){

		this.m_lngSeminarID=lngSeminarID_IN;
		this.m_strMatrikelnummer=strMatrikelnummer;
		this.m_Locale=locale;
		this.setMatrikelnummer(strMatrikelnummer);
		this.setSeminarID(lngSeminarID_IN);

	}

  }//end class

