/*
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
 * code structure:
 *
 * 1. declarations											line	80
 *
 * 2. protected properties									line	90
 *
 * 3. public methods										line 	100
 *
 * 4. private methods										line	325
 *
 * 6. protected queries										line	355
 *
 * 7. utility												line 	523
 * 
 * 8. deprecated methods
 *
 * @version 5-19-01 (hand-coded)
 * change date              change author           change description
 * Nov 10, 2003	            h. jakubzik             creation
 * Jan 04, 2004				h. jakubzik				StudentCredits: added ECTS-grade to database and subsequently to query
 * Jan 05, 2004				h. jakubzik				StudentCredits(teacher): added method to show all credits signed by a specific teacher.
 *													init(): added method to initialize object (instead of a constructor)
 * Jan 27, 2004				h. jakubzik				made 'StudentMissingCredits' throw an exception if the exam was
 * 													a modular exam (there's no missing credits there, really. It's missing
 *													credit points, which is on the to do list somewhere...)
 *
 * Feb 14, 2004				h. jakubzik				"addKlausuranmeldung": now also saves KurstypID in StudentXLeistung.
 *													There's more to #hack there, too: StudentXLeistung should take as
 *													much information from tblBdKurs as it can hold (currently only IDs).
 *
 * Feb 14, 2004				h. jakubzik				Bugfix on StudentExamApplications, added crits validated and blnKlausuranmeldung.
 *
 * Feb 15, 2004				h. jakubzik				addKlausuranmeldung now adds CreditPoints which it looks up in table 'tblSdLeistung' beforehand.
 *													StudenCredits now selects CreditPoints, too.
 *
 * Feb 29, 2004				h. jakubzik				add method getDBBoolRepresenation in shjCore now includes the single quotes. Adapted method 'add' here.
 *
 * Mar 1,  2004				h. jakubzik				StudentCredits() is now public (wishful for '/Config/' webinterface).
 *													StudentCredits() now also selects "lngLeistungID"
 *													StudentCredits() now also selects "lngStudentLeistungCount"
 *
 * Mar 2,  2004				h. jakubzik				implemented method StudentCredits(blnCommitment) (needed for /Config/ in webapp).
 *
 * Apr 11, 2004				h. jakubzik				granted public access to  ResultSet.StudentExams() (formerly: protected),
 *													added .getMissingPrereqExams(id).
 *
 * Apr 21, 2004				h. jakubzik				StudentExams: added new cols (5-19) to select: blnBestanden, dtm, etc.
 *
 * Apr 27, 2004				h. jakubzik				.addKlausuranmeldung() now copies detail-information from "tblBdKurs" into "tblBdStudentXLeistung".
 *													it might not be optimal, from an informationalist point of view. But otherwise there would have
 *													to be a stricter logic in the archive, which is hard to do, since a semester-switch is always
 *													a bit tricky.
 *
 * May 02, 2004				h. jakubzik				.StudentExamApplications() made public (from protected).
 *													Since this method is used to show the Commitments to students,
 *													it should also be used to show commitments to administrators.
 *
 * June 10, 2004			h. jakubzik				.init(SeminarID, StudentName, StudentGeburtstag) [for bulk credit import]
 *
 * June 20, 2004			h. jakubzik				.getMissingPrereqExams() corrected: an exam usually has no grade. So
 *													the flag must be used to decide whether the exam was passed or not
 *													(as opposed to the flag in 'tblSdNote,' which is used for credits).
 *
 * June 22, 2004			h. jakubzik				.StudentCredits() now also selects the course's title.
 *
 * July  6, 2004			h. jakubzik				.init(...Birthdate...) now uses .initByRst (instead of just init).
 *													.init(...PID...) now also uses .initByRst (instead of just init).
 *													The reason for both is that the /data/Student.init() method filters
 *													for 'lngStudentRandom,' the SessionID of the student. When accessed
 *													from elsewhere (e.g. the config-account), the sessionId is not checked.
 *
 * October 3, 2004			h. jakubzik				StudentExamApplications() addecd x.* to select-clause
 *													(Pr???fungsleistung vs. Zulassungsvoraussetzung etc.)
 *													addKlausuranmeldung(), accordingly
 *													StudentCredits() accordingly (all variants)
 *
 * December 7, 2004			h. jakubzik				public void init(long lngSeminarID, String strName, HtmlDate h) [bulk-import]
 * 													Bugfix: this method now also sets the SeminarID to the param. 
 * 													(the bug didn't have any effects; it just showed during code-inspection).
 * 
 * 													added .public ResultSet getMissingCredits(long lngModulID)
 * 													Context: refactoring StudentExamAddPickCredits.jsp
 * 
 * 													added .float getCreditpointSum(long lngModulID)
 * 													Context: refactoring StudentExamAddPickCredits.jsp
 * 
 * 													deprecated .protected ResultSet StudentMissingCredits(long lngSeminarID, long lngPruefungsID)
 * 
 * 													deprecated .public void addCredit(StudentXLeistung s)
 * 
 * December 13, 2004	   h. jakubzik				added .getStudentCredits (refactored from StudentExamAddPick)	
 * 
 * December 18, 2004	   h. jakubzik				.getStudentCredits extended: avoids multiple Pr???fungsleistungseinbringung :-)
 * 
 * January 6, 2005		   h. jakubzik				StudentCredits(boolean blnCommitment, byte bytOrderByName) selects l.*, now, so 
 * 													that Leistung-object can be constructed from it.	
 * 
 * 													Same for .StudentExamApplications()		
 * 
 * January 25, 2005		   h. jakubzik				.bachelorize, isBachelor, resetPassword added.
 * April 30, 2005		   h. jakubzik				.masterize, isMaster added.
 * 
 * May 7, 2005			   h. jakubzik				.getBAMANebenfach()		
 * 
 * May 14, 2005			   h. jakubzik				.addKlausuranmeldung() now addes DozentVorname an DozentTitle separately.
 * 
 * May 23, 2005			   h. jakubzik				.getStudentTranskriptModule() (refactored from jsp)	
 * 
 * May 30, 2005			   h. jakubzik				changed StudentExamAppliations and Credits so that a data.Leistung-object can be constructed from the resultSet.
 * 
 * Aug 08, 2005			   h. jakubzik				added group-lookup					
 * 
 * Aug 09, 2005			   h. jakubzik				added StudGruppe as property
 * 
 * Aug 11, 2005			   h. jakubzik				.update() now also updates Bemerkung and StudentSemester.
 * 
 * Aug 14, 2005			   h. jakubzik				.init(seminar, name, gebdatum) ruft jetzt auch "resetThis" auf (Bugfix).
 * 
 * Aug 15, 2005			   h. jakubzik				.addKlausuranmeldung+strInfo@Custom1
 * 
 * Aug 16, 2005			   h. jakubzik				.addKlausuranmeldung+strCustom2
 * 
 * Nov 12, 2005			   h. jakubzik				.addKlausuranmeldung+strStudentLeistungBemerkung
 * 
 * Jan 30, 2006			   h. jakubzik				.StudentExams selects x.* instead of few cols only.
 * 
 * Feb  3, 2006			   h. jakubzik				.checkCommitmentDoublette made public!
 * 
 * Feb  6, 2006			   h. jakubzik				.getFachID made public
 * 
 * Mar 16, 2006			   h. jakubzik				property URZPassword
 * 
 * Mar 30, 2006			   h. jakubzik				added .getAnmeldungIterator
 * 
 * Apr 04, 2006			   h. jakubzik				closed all ResultSets
 * 
 * Apr 12, 2006			   h. jakubzik				unsetGruppeID now sets StudentXGruppe to null.
 * 
 * Apr 19, 2006			   h. jakubzik				.StudentExamApplications() now also selects blnKursScheinanmeldungErlaubt.
 * 
 * June 6, 2006			   h. jakubzik				.addKlausuranmeldung now looks for CreditPoints in KursXKurstyp. If it finds LP > 0 there, 
 * 													it adds these, and NOT, as before, the more generic ones from Leistung.
 * 
 * Aug 21, 2006			   h. jakubzik				replaced "blnStudentLeistungKlausuranmeld" with "+ung" (update Postgres 7.2 to 7.4)
 * 
 * Oct 9, 2006			   h. jakubzik				added .getStudentUrlaubSummary()
 * 
 * Mar 17, 2008			   h. jakubzik				made getFachID() permanently store it val in m_iStudentFachID
 * 
 * April 7, 2008		   h. jakubzik			    .addKlausuranmeldung() with ModulID
 */
package de.shj.UP.logic;

import java.sql.ResultSet;
import java.util.Locale;//
import java.util.Vector;

import de.shj.UP.beans.student.BemerkungIterator;
import de.shj.UP.data.StudGruppe;
import de.shj.UP.HTML.HtmlDate;
import de.shj.UP.data.Fach;
import de.shj.UP.data.KursXKurstyp;
import de.shj.UP.data.ModulXLeistung;
import de.shj.UP.data.StudentXLeistung;

/**
 * This class is the first layer above the raw data.Student wrapper around that
 * database table. It links the student to a seminar, which is a tricky business.
 * Through the link to a seminar, the Fach (subject) is extracted, and so are
 * credits, credit-applications and exams, missing credits for certain exams,
 * and also exam applications. Finally, there is quite a large collection of
 * (protected) methods that are basically queries.
 **/
public class StudentData extends de.shj.UP.data.Student{

	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 1.   D E C L A R A T I O N S
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------

	private static final long serialVersionUID = -6944592890299102777L;
	public String m_strDebug;
	private long m_lngSeminarID;
	public static 	int 	m_ZUV_ZIEL_BACHELOR1=182;
	public static 	int 	m_ZUV_ZIEL_BACHELOR2=282;
	public static 	int 	m_ZUV_ZIEL_MASTER	=188;
	public static 	int 	m_ZUV_ZIEL_MAGISTER	=102;
	public static 	int 	m_ZUV_ZIEL_LEHRAMT	=125;
	public static 	int 	m_ZUV_ZIEL_PROMOTION=506;
	public static 	int 	m_ZUV_ZIEL_ERASMUS=2;
	
	private			int		m_iStudentFachID	= -1;	// uninitialized integer
	
	private 		boolean	m_blnOverrideCommitmentDoubletteCheck = false;
	private 		String  m_strBAMANebenfach	="#";
	private			StudGruppe m_objStudGruppe	= null;
	private			BemerkungIterator m_Bemerkungen=null;
	
	private 		String	m_strURZPassword			= "";

	private boolean			m_blnGroupIDSet = false;
	private long			m_lngGroupID	=-1;
	private String			m_strSemester	="#";
	
	/**
	 * @return
	 * @throws Exception
	 */
	public BemerkungIterator getBemerkungIterator() throws Exception{
		if(m_Bemerkungen==null){
			m_Bemerkungen = new BemerkungIterator(getSeminarID(), getMatrikelnummer());
		}
		return m_Bemerkungen;
	}
	
	/**
	 * Iterate from start.
	 */
	public void resetBemerkungenIterator(){
		m_Bemerkungen=null;
	}
	
	
	/**<pre>
	 * Method returns the group that this student is mapped to,
	 * * in the current semester (!),
	 * * in the StudienSemester that is ths student's .StudentSemester().
	 * </pre>
	 * @see #getStudentGruppeID()
	 * @return Group that this student is mapped to
	 * @throws Exception
	 */
	public StudGruppe getStudGruppe() throws Exception{
		if(m_objStudGruppe==null){
			long lngGruppeID=Long.parseLong(lookUp("lngStudGruppeID", "tblBdStudentXStudGruppe", 
					"\"lngSeminarID\"=" + m_lngSeminarID + " and " + 
					"\"strMatrikelnummer\"='" + getMatrikelnummer() + "' and " + 
					"\"lngStudentStudGruppeStudiensem\"=" + getStudentSemester() + " and " +
					"\"strStudentStudGruppeSemester\"='" + getSemester() + "'"));
			m_objStudGruppe = new StudGruppe(m_lngSeminarID, lngGruppeID);
		}
		return m_objStudGruppe;
	}
	
	
	/**
	 * Returns current term in this format: ws2006/2007, or ss2007
	 * @return the current semester name, if none other is set.
	 * @throws Exception
	 */
	private String getSemester() throws Exception{
		if(m_strSemester.equals("#")) m_strSemester = new HtmlDate(Locale.GERMAN).getSemesterName();
		return m_strSemester;
	}	

	/**
	 * <pre>
	 * The database stores:
	 * [ #0] for no Urlaubssemester so far,
	 * [ #2] for two Urlaubssemester so far,
	 * Mutterschutz [ #3] for currently in Mutterschutz, this is
	 * 					  the third Urlaubssemester.
	 * 
	 * This method returns "--" for no Urlaubssemester, and it 
	 * takes care of null-values.
	 * </pre> 
	 * @return Urlaubssemester
	 */
	public String getStudentUrlaubSummary(){
		String sReturn = normalize(getStudentUrlaub()).trim();
		
		// keine Urlaubssemester
		if(sReturn.equals("[# 0]")) return "--";
		return sReturn;
	}
	
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 2.   P R O T E C T E D  P R O P E R T I E S
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	
	/**<pre>
	 * If set to true, the addition of a commitment using .addKlausuranmeldung does 
	 * not ensure that there is no other commitment for the same credit type.
	 * 
	 * Usually, it is (and should be) forbidden to register for two courses of 
	 * the same credit type. (It is always forbidden to register for the same 
	 * course twice, by the way). For generic constructs (e.g. credits of type minor or 
	 * Nebenfach), you might want to switch the override off.
	 * </pre> 
	 * @param value
	 */
	public void setCommitmentOverrideDoubletteCheck(boolean value){
		m_blnOverrideCommitmentDoubletteCheck=value;
	}
	
	public void setSeminarID(long lngSeminarID){
		this.m_lngSeminarID=lngSeminarID;
	}

	public long getSeminarID(){
		return this.m_lngSeminarID;
	}

	/**
	 * @param intFachID, ZUV-Encoded FachID to lookup.
	 * @return Fachsemester of this Fach.
	 */
	public long getFachsemester( int intFachID ){
		long lngReturn = 4711;
		if(this.getStudentFach1()==intFachID) lngReturn=this.getStudentFachsemester1();
		else if(this.getStudentFach1()==intFachID) lngReturn=this.getStudentFachsemester1();
		else if(this.getStudentFach2()==intFachID) lngReturn=this.getStudentFachsemester2();
		else if(this.getStudentFach3()==intFachID) lngReturn=this.getStudentFachsemester3();
		else if(this.getStudentFach4()==intFachID) lngReturn=this.getStudentFachsemester4();
		else if(this.getStudentFach5()==intFachID) lngReturn=this.getStudentFachsemester5();
		else if(this.getStudentFach6()==intFachID) lngReturn=this.getStudentFachsemester6();
		return lngReturn;
	}
	
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 	3.   P U B L I C  M E T H O D S
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	/**<pre>
	 * Liefert ein ResultSet mit allen Modulen, zu denen der Studierende  
	 * im betreffenden Zeitraum eine Leistung erbracht hat. (Dabei muss 
	 * die Leistung in diesem Modul als "Im Transkript sichtbar" markiert 
	 * sein, m.a.W. die Eigenschaft 
	 * <Code>tblSdModulXLeistung.blnModulLeistungTranskript=true</Code> 
	 * sein).</pre>
	 * @version 6-04-00
	 * @param sem: betreffender Zeitraum
	 * @param blnNurEinSemester: Spezifizierung betreffender Zeitraum. 
	 *		  Bei 'Ja' wird nur das Semester <Code>sem</Code> gewertet, bei 
	 *		  'Nein' beginnt der "betreffende Zeitraum" bei 1970 und endet bei <Code>sem.Ende</Code>.
	 **/
	public ResultSet getStudentTranskriptModule(SemesterUtil sem, boolean blnNurEinSemester) throws Exception{
		return sqlQuery( "SELECT " +
			  "m.* " +
		"FROM \"tblSdModul\" m  " +
		"WHERE " +
		"m.\"lngSdSeminarID\"=" + getSeminarID() + " AND Exists " +
		"(" +
		 "Select * from \"tblBdStudentXLeistung\" x, \"tblSdModulXLeistung\" mxl Where " +
		 "(" +
		  "(x.\"lngSdSeminarID\"			= " + getSeminarID() + ")		AND " +
		   ( (blnNurEinSemester)  ? ("(x.\"dtmStudentLeistungAusstellungsd\" 	>= '" + sem.getSemesterStart() + "') AND ") : "") + 
		  "(x.\"dtmStudentLeistungAusstellungsd\" 	<= '" + sem.getSemesterEnd() + "') AND " +
		  "(x.\"strMatrikelnummer\"					= '" + getMatrikelnummer() + "')	AND " +
		  "(x.\"blnStudentLeistungValidiert\"		= 't'::bool)				AND " +
		  "(x.\"blnStudentLeistungKlausuranmeldung\"	= 'f'::bool) 			AND " +
		  "(x.\"lngLeistungsID\"					= mxl.\"lngLeistungID\") 	AND " +
		  "(x.\"lngSdSeminarID\"					= mxl.\"lngSdSeminarID\") 	AND " +	  
		  "(m.\"lngModulID\" 						= mxl.\"lngModulID\") 		AND " +
		  "(m.\"lngSdSeminarID\" 					= mxl.\"lngSdSeminarID\") 	AND " +
		  "(mxl.\"blnModulLeistungTranskript\"		= 't') " +
		 ")" +
		") order by \"lngModulNummer\";");
	}
	
	/**<pre>
	 * Liefert ein ResultSet mit allen Modulen, zu denen der Studierende  
	 * im betreffenden Zeitraum eine Leistung erbracht hat. (Dabei muss 
	 * die Leistung in diesem Modul als "Im Transkript sichtbar" markiert 
	 * sein, m.a.W. die Eigenschaft 
	 * <Code>tblSdModulXLeistung.blnModulLeistungTranskript=true</Code> 
	 * sein).</pre>
	 * Neu gegn�ber getStudentTranskriptModule: die Leistung muss explizit 
	 * dem Modul zugeordnet sein (d.h. tblStudentXLeistung.lngModulID = tblModulXLeistung.lngModulID).
	 * @version 6-19-05
	 * @param sem: betreffender Zeitraum
	 * @param blnNurEinSemester: Spezifizierung betreffender Zeitraum. 
	 *		  Bei 'Ja' wird nur das Semester <Code>sem</Code> gewertet, bei 
	 *		  'Nein' beginnt der "betreffende Zeitraum" bei 1970 und endet bei <Code>sem.Ende</Code>.
	 **/
	public ResultSet getStudentTranskriptModuleBA(SemesterUtil sem, boolean blnNurEinSemester) throws Exception{
		return sqlQuery( "SELECT " +
			  "m.* " +
		"FROM \"tblSdModul\" m  " +
		"WHERE " +
		"m.\"lngSdSeminarID\"=" + getSeminarID() + " AND Exists " +
		"(" +
		 "Select * from \"tblBdStudentXLeistung\" x, \"tblSdModulXLeistung\" mxl Where " +
		 "(" +
		  "(x.\"lngSdSeminarID\"			= " + getSeminarID() + ")		AND " +
		   ( (blnNurEinSemester)  ? ("(x.\"dtmStudentLeistungAusstellungsd\" 	>= '" + sem.getSemesterStart() + "') AND ") : "") + 
		  "(x.\"dtmStudentLeistungAusstellungsd\" 	<= '" + sem.getSemesterEnd() + "') AND " +
		  "(x.\"strMatrikelnummer\"					= '" + getMatrikelnummer() + "')	AND " +
		  "(x.\"blnStudentLeistungValidiert\"		= 't'::bool)				AND " +
		  "(x.\"blnStudentLeistungKlausuranmeldung\"	= 'f'::bool) 			AND " +
		  "(x.\"lngLeistungsID\"					= mxl.\"lngLeistungID\") 	AND " +
		  "(x.\"lngSdSeminarID\"					= mxl.\"lngSdSeminarID\") 	AND " +
		  "(x.\"lngModulID\"						= mxl.\"lngModulID\")		AND " +
		  "(m.\"lngModulID\" 						= mxl.\"lngModulID\") 		AND " +
		  "(m.\"lngSdSeminarID\" 					= mxl.\"lngSdSeminarID\") 	AND " +
		  "(mxl.\"blnModulLeistungTranskript\"		= 't') " +
		 ")" +
		") order by \"lngModulNummer\";");
	}
	
	/**
	 * Liefert alle Leistungen, die der Studierende (im richtigen Seminar) im betreffenden 
	 * Zeitraum zu einem bestimmten Modul erbracht hat.<br />
	 * (Dabei muss 
	 * die Leistung in diesem Modul als "Im Transkript sichtbar" markiert 
	 * sein, m.a.W. die Eigenschaft <Code>tblSdModulXLeistung.blnModulLeistungTranskript=true</Code> sein).
	 * @version 6-04-00	 * 
	 * @param sem: betreffender Zeitraum
	 * @param blnNurEinSemester: Spezifizierung betreffender Zeitraum. 
	 *		  Bei 'Ja' wird nur das Semester <Code>sem</Code> gewertet, bei 
	 *		  'Nein' beginnt der "betreffende Zeitraum" bei 1970 und endet bei <Code>sem.Ende</Code>.
	 * @param lngModulID: bestimmtes Modul des richtigen Seminars
	 **/
	public ResultSet getStudentTranskriptModulLeistungen(SemesterUtil sem, long lngModulID, boolean blnNurEinSemester) throws Exception{
		return sqlQuery("SELECT " +
		  "x.*, " +
		  "l.\"lngLeistungID\", " +
		  "m.\"lngModulID\", " +
		  "m.\"strModulBezeichnung\", " +
		  "m.\"strModulBeschreibung\", " +
		  "m.\"strModulBezeichnung_en\", " +
		  "m.\"strModulBeschreibung_en\", " +  
		  "m.\"lngModulNummer\", " +
		  "n.*, " +
		  "l.* " +
		"FROM \"tblBdStudentXLeistung\" x, \"tblSdNote\" n, \"tblSdLeistung\" l, \"tblSdModul\" m, \"tblSdModulXLeistung\" mxl  " +
			"WHERE" +
		 "(" +
		  "(x.\"lngSdSeminarID\"			= " + getSeminarID() + ")		AND " +
	     ( (blnNurEinSemester)  ? ("(x.\"dtmStudentLeistungAusstellungsd\" 	>= '" + sem.getSemesterStart() + "') AND ") : "") + 
		  "(x.\"dtmStudentLeistungAusstellungsd\" 	<= '" + sem.getSemesterEnd() + "') AND " +
		  "(x.\"blnStudentLeistungValidiert\"		= 't'::bool)				AND " +
		  "(x.\"blnStudentLeistungKlausuranmeldung\"	= 'f'::bool) 			AND " +
		  "(x.\"strMatrikelnummer\"='" + getMatrikelnummer() + "') AND " +
		  "(mxl.\"blnModulLeistungTranskript\" = 't') AND " +
		  "(l.\"lngLeistungID\" 			= mxl.\"lngLeistungID\") 	AND " +
		  "(l.\"lngSdSeminarID\" 			= mxl.\"lngSdSeminarID\") 	AND " +
		  "(m.\"lngModulID\" 				= mxl.\"lngModulID\") 		AND " +
		  "(m.\"lngSdSeminarID\" 			= mxl.\"lngSdSeminarID\") 	AND " +
		  "(m.\"lngModulID\" 				= " + lngModulID + ") AND " +
		  "(n.\"intNoteID\"					= x.\"intNoteID\") 			AND " +
		  "(n.\"lngSdSeminarID\" 			= x.\"lngSdSeminarID\") 	AND " +
		  "(l.\"lngLeistungID\" 			= x.\"lngLeistungsID\") 	AND " +
		  "(l.\"lngSdSeminarID\" 			= x.\"lngSdSeminarID\")		    " +
		 ") order by \"lngLeistungID\";");
	}
	
	/**
	 * Liefert alle Leistungen, die der Studierende (im richtigen Seminar) im betreffenden 
	 * Zeitraum zu einem bestimmten Modul erbracht hat.<br />
	 * (Dabei muss 
	 * die Leistung in diesem Modul als "Im Transkript sichtbar" markiert 
	 * sein, m.a.W. die Eigenschaft <Code>tblSdModulXLeistung.blnModulLeistungTranskript=true</Code> sein).
	 * �nderung gegen�ber getStudentTranskriptModulLeistungen: die Leistung muss explizit dem 
	 * Modul zugeordnet sein, d.h. es wird nach "tblBdStudentXLeistung.lngModulID" gefiltert.
	 * @version 6-19-00	 * 
	 * @param sem: betreffender Zeitraum
	 * @param blnNurEinSemester: Spezifizierung betreffender Zeitraum. 
	 *		  Bei 'Ja' wird nur das Semester <Code>sem</Code> gewertet, bei 
	 *		  'Nein' beginnt der "betreffende Zeitraum" bei 1970 und endet bei <Code>sem.Ende</Code>.
	 * @param lngModulID: bestimmtes Modul des richtigen Seminars
	 **/
	public ResultSet getStudentTranskriptModulLeistungenBA(SemesterUtil sem, long lngModulID, boolean blnNurEinSemester) throws Exception{
		return sqlQuery("SELECT " +
		  "x.*, " +
		  "l.\"lngLeistungID\", " +
		  "m.\"lngModulID\", " +
		  "m.\"strModulBezeichnung\", " +
		  "m.\"strModulBeschreibung\", " +
		  "m.\"strModulBezeichnung_en\", " +
		  "m.\"strModulBeschreibung_en\", " +  
		  "m.\"lngModulNummer\", " +
		  "n.*, " +
		  "l.* " +
		"FROM \"tblBdStudentXLeistung\" x, \"tblSdNote\" n, \"tblSdLeistung\" l, \"tblSdModul\" m, \"tblSdModulXLeistung\" mxl  " +
			"WHERE" +
		 "(" +
		  "(x.\"lngSdSeminarID\"			= " + getSeminarID() + ")		AND " +
	     ( (blnNurEinSemester)  ? ("(x.\"dtmStudentLeistungAusstellungsd\" 	>= '" + sem.getSemesterStart() + "') AND ") : "") + 
		  "(x.\"dtmStudentLeistungAusstellungsd\" 	<= '" + sem.getSemesterEnd() + "') AND " +
		  "(x.\"blnStudentLeistungValidiert\"		= 't'::bool)				AND " +
		  "(x.\"blnStudentLeistungKlausuranmeldung\"	= 'f'::bool) 			AND " +
		  "(x.\"strMatrikelnummer\"='" + getMatrikelnummer() + "') AND " +
		  "(x.\"lngModulID\"=" + lngModulID + ") AND " +
		  "(mxl.\"blnModulLeistungTranskript\" = 't') AND " +
		  "(l.\"lngLeistungID\" 			= mxl.\"lngLeistungID\") 	AND " +
		  "(l.\"lngSdSeminarID\" 			= mxl.\"lngSdSeminarID\") 	AND " +
		  "(m.\"lngModulID\" 				= mxl.\"lngModulID\") 		AND " +
		  "(m.\"lngSdSeminarID\" 			= mxl.\"lngSdSeminarID\") 	AND " +
		  "(m.\"lngModulID\" 				= " + lngModulID + ") AND " +
		  "(n.\"intNoteID\"					= x.\"intNoteID\") 			AND " +
		  "(n.\"lngSdSeminarID\" 			= x.\"lngSdSeminarID\") 	AND " +
		  "(l.\"lngLeistungID\" 			= x.\"lngLeistungsID\") 	AND " +
		  "(l.\"lngSdSeminarID\" 			= x.\"lngSdSeminarID\")		    " +
		 ") order by \"lngLeistungID\";");
	}
	
	public void unsetGruppeID(){
		m_blnGroupIDSet = false;
		m_objStudGruppe=null;
	}
	
	/**
	 * @see #getStudGruppe()
	 * @param lngSeminarID
	 * @return Id of the group that this student is mapped 
	 * to in the current semester, relative to his/her current 
	 * "StudentSemester".
	 */
	public long getStudentGruppeID() {
		try {
			if(!m_blnGroupIDSet) m_lngGroupID = getStudGruppe().getStudGruppeID();
			m_blnGroupIDSet=true;
		} catch (Exception e) {
			m_lngGroupID = -1;
			m_blnGroupIDSet=false;
		}
		return m_lngGroupID;
	}
	
	
	/**
	 * If this is a bachelor or master student, ZUV should have intFach2=0.<br />
	 * Currently (May 2005), minor subjects can be freely invented carrying Ids of 
	 * the major's seminar-Id * 100.000 upwards.<br />
	 * The idea of this method here is to report the name of this freely invented 
	 * Nebenfach. Or deliver a message that something seems to be wrong in case that 
	 * something seems to be wrong.<br />
	 * TODO: deprecated this as soon as there is official management of Nebenfaecher (minor subjects).
	 * @return tblSdFach.FachBeschreibung of tblBdStudent.intFach2, if this is a bachelor or master;<br />
	 * "Nur f???r Bachelor und Master erkl???rt", if this is _not_ a bachelor or master student.<br />
	 * "Interner Fehler", if an internal error has occured.
	 */
	public String getBAMANebenfach(){
		if(!m_strBAMANebenfach.equals("#")) return m_strBAMANebenfach;
		if(!(isBachelor() || isMaster())) return "Nur f???r Bachelor und Master erkl???rt.";
		
		try{
		 m_strBAMANebenfach = (new Fach(getStudentFach2())).getFachBeschreibung();
		}catch(Exception eNoFach){m_strBAMANebenfach="Interner Fehler :-(";}
		
		return m_strBAMANebenfach;
	}
	
	/**<pre>
	 * Executes update in database of fields that can be 
	 * changed through web-interface by student or Config-administration. 
	 * These are:
	 * password, email (and publish or no), cell-phone number (and publish or no), homepage (and publish or no),
	 * interests (and publish or no), ZUVZiel, remarks, "StudienSemester" (==StudentSemester).
	 * </pre>
	 * @return 'true' on success, 'false' on failure.
	 * @throws Exception when connection to database erroneous.
	 **/
	public boolean update() throws Exception{
		return sqlExe("update \"tblBdStudent\" set " +
			"\"strStudentPasswort\"='" + getDBCleanString(this.getStudentPasswort()) +  "', " +
			"\"strStudentEmail\"='" + getDBCleanString(this.getStudentEmail()) +  "', " +
			"\"blnStudentPublishEmail\"=" + getDBBoolRepresentation(this.getStudentPublishEmail()) +  ", " +
			"\"strStudentHandy\"='" + getDBCleanString(this.getStudentHandy()) +  "', " +
			"\"strStudentBemerkung\"=" + dbNormal(this.getStudentBemerkung()) + ", " +
			"\"blnStudentPublishHandy\"=" + getDBBoolRepresentation(this.getStudentPublishHandy()) +  ", " +
			"\"strStudentHomepage\"='" + getDBCleanString(this.getStudentHomepage()) +  "', " +
			"\"blnStudentPublishHomepage\"=" + getDBBoolRepresentation(this.getStudentPublishHomepage()) +  ", " +
			"\"strStudentInterests\"='" + getDBCleanString(this.getStudentInterests()) +  "', " +
			"\"lngStudentZUVZiel\"=" + getStudentZUVZiel() + "," +
			"\"intStudentFach2\"=" + getStudentFach2() + "," +
			"\"intStudentSemester\"=" + getStudentSemester() + ", " +
			"\"blnStudentVisible\"=" + getDBBoolRepresentation(getStudentVisible()) +
			" where (" + this.getSQLWhereClauseOld() + ");");
	}


	/**
	 * Adds a student to database. Data is taken from underlying object com.shj.data.Student.
	 * @return 'true' on success, 'false' on failure.
	 * @throws Exception when connection to database erroneous.
	 **/
	public boolean add() throws Exception{
		return sqlExe("insert into \"tblBdStudent\" ( " +
			"\"lngStudentPID\", \"strMatrikelnummer\", \"strStudentVorname\", \"strStudentNachname\", \"intStudentSemester\", \"strStudentPasswort\", \"dtmStudentGeburtstag\", \"strStudentGeburtsort\", \"strStudentStrasse\", \"strStudentPLZ\", \"strStudentOrt\", \"strStudentEmail\", \"blnStudentPublishEmail\", \"strStudentHandy\", \"blnStudentPublishHandy\", \"blnStudentFemale\", \"intStudentFach1\", \"intStudentFach2\", \"intStudentFach3\", \"intStudentFach4\", \"intStudentFach5\", \"intStudentFach6\", \"intStudentFachsemester1\", \"intStudentFachsemester2\", \"intStudentFachsemester3\", \"intStudentFachsemester4\", \"intStudentFachsemester5\", \"intStudentFachsemester6\" ) VALUES ( " +
			"" + this.getNextPID() + ", " +
			"'" + getDBCleanString(this.getMatrikelnummer()) + "', " +
			"'" + getDBCleanString(this.getStudentVorname()) + "', " +
			"'" + getDBCleanString(this.getStudentNachname()) + "', " +
			"" + this.getStudentSemester() + ", " +
			"'" + getDBCleanString(this.getMatrikelnummer()) + "', " +
			"'" + g_ISO_DATE_FORMAT.format(this.getStudentGeburtstag()) + "', " +
			"'" + getDBCleanString(this.getStudentGeburtsort()) + "', " +
			"'" + getDBCleanString(this.getStudentStrasse()) + "', " +
			"'" + getDBCleanString(this.getStudentPLZ()) + "', " +
			"'" + getDBCleanString(this.getStudentOrt()) + "', " +
			"'" + getDBCleanString(this.getStudentEmail()) + "', " +
			getDBBoolRepresentation( this.getStudentPublishEmail() ) + ", " +
			"'" + getDBCleanString( this.getStudentHandy()) + "', " +
			getDBBoolRepresentation(this.getStudentPublishHandy()) + ", " +
			getDBBoolRepresentation(this.getStudentFemale()) + ", " +
			"" + this.getStudentFach1() + ", " +
			"" + this.getStudentFach2() + ", " +
			"" + this.getStudentFach3() + ", " +
			"" + this.getStudentFach4() + ", " +
			"" + this.getStudentFach5() + ", " +
			"" + this.getStudentFach6() + ", " +
			"" + this.getStudentFachsemester1() + ", " +
			"" + this.getStudentFachsemester2() + ", " +
			"" + this.getStudentFachsemester3() + ", " +
			"" + this.getStudentFachsemester4() + ", " +
			"" + this.getStudentFachsemester5() + ", " +
			"" + this.getStudentFachsemester6() + ");");
	}

	/**<pre>
	 * Is this a new matriculation number?
	 * </pre>
	 * @return true if current matriculation number is nonexistent in database,
	 * false otherwise.
	 * @throws Exception if connection to db is erroneous.
	 */
	public boolean isNewStudent() throws Exception{
		return (lookUp("lngStudentPID", "tblBdStudent", "\"strMatrikelnummer\"='" + this.getMatrikelnummer() + "'").equals("#NO_RESULT"));
	}

	/**
	 * Sucht in allen konfigurierten Pr�fungen des Fachs, f�r
	 * das der aktuelle Student eingeschrieben ist, nach Modulen, 
	 * die die angegebene Leistung enthalten.
	 * @version 6-22: filter blnModulWaehlbar=true
	 * @param lLeistungID Leistung
	 * @return ResultSet containing "lngModulID" and "strModulBezeichnung"
	 */
	public ResultSet getModuleOptions(long lLeistungID) throws Exception{
		// Abfrage sucht in allen Pr�fungen zum Fach d. eingeschriebenen Studierenden
		// diejenigen Module heraus, denen die Leistung lLeistungID zugeordnet ist:
		// #hack; Oct 13,2008: verstehe nicht, warum 'distinct' notwendig ist, 
		// aber z.B. Seminar 1, Fach 83504, Leistung 1 kommt sonst doppelt 
		// (Einf�hrg. Litwiss. in Anglistik bei BA 75% oder 50%)

		return sqlQuery("SELECT distinct m.\"lngModulID\", " +
			       "m.\"strModulBezeichnung\" " +
			       "FROM \"tblSdModulXLeistung\" mxl, \"tblSdModul\" m, \"tblSdPruefungXModul\" pxm, \"tblSdPruefungXFach\" pxf " + 
			       "WHERE mxl.\"lngSdSeminarID\" = m.\"lngSdSeminarID\" AND " +
			             "mxl.\"lngModulID\" = m.\"lngModulID\" AND " +
			             "pxm.\"lngSdSeminarID\" = mxl.\"lngSdSeminarID\" AND " +
			             "pxm.\"lngModulID\" = mxl.\"lngModulID\" AND " +
			             "pxf.\"lngSdSeminarID\" = pxm.\"lngSdSeminarID\" AND " +
			             "pxf.\"lngPruefungID\" = pxm.\"lngPruefungID\" AND " +
			             "mxl.\"lngLeistungID\" = " + lLeistungID + " AND " +
			             "mxl.\"lngSdSeminarID\" = " + this.getSeminarID() + " AND " +
			             "m.\"lngSdSeminarID\" = " + this.getSeminarID() + " AND " +
			             "m.\"blnModulWaehlbar\" = 't' AND " +
			             "pxm.\"lngSdSeminarID\" = " + this.getSeminarID() + " AND " +
			             "pxf.\"intFachID\" = " + this.getFachID(this.getSeminarID()) + " AND " +
			             "pxf.\"lngSdSeminarID\" = " + this.getSeminarID() + " " +
			             "ORDER BY m.\"lngModulID\" ASC;");
	}
		
	/**
	 * Liefert die m�glichen Module im Vektor-Format. 
	 * Beispielverwendung:
	 * <Code>
	 * vModulliste = student.getModuleOptionsVector(lLeistungGrammatik);
	 * for(int ii=0; ii<vModulliste.size(); ii++){
	 * 		debug.print("Modul-Id:  " + ((String[])(vctModulliste.get(ii)))[0]);
	 * 		debug.print("Modul-Bez: " + ((String[])(vctModulliste.get(ii)))[1]);
	 * }
	 * </Code>
	 * @see #getModuleOptions(long)
	 * @param lLeistungID
	 * @return Vector containing String Array objects with s[0] = ModulID, and s[1] = ModulBezeichnung
	 * @throws Exception
	 */
	public Vector getModuleOptionsVector(long lLeistungID)throws Exception{
		Vector vctModule = new Vector();
		int ii=0;
		ResultSet rst = getModuleOptions(lLeistungID);
		while(rst.next()){
			vctModule.add(ii++, new String[]{rst.getString("lngModulID"), rst.getString("strModulBezeichnung")});
		}
		return vctModule;
	}
	
	
	/**
	 * @version 5-27-10
	 * A ResultSet containing all credits that are
	 * (1) part of the module 'modulId',
	 * (2) not yet passed by the current student.
	 * This is a utility to gather 'Zulassungsvoraussetzungen'. In 
	 * a really modular exam-administration, this makes not much sense, since 
	 * there sums of credit points count, while this method just
	 * retrieves single credits without regard to the credit points.
	 * @param lngModulID Id of the module against which the student's credits are checked.
	 * @return ResultSet with information on credits (tblSdLeistung.*).
	 * @throws Exception (ResultSet-related: no connection to db, or sql-error).
	 */
	public ResultSet getMissingCredits(long lngModulID) throws Exception{
	    return sqlQuery("SELECT " + 
		  "m.\"lngSdSeminarID\", " + 
		  "m.\"lngModulID\", " + 
		  "x.\"lngModulID\", " + 
		  "l.* " + 
		"FROM \"tblSdModul\" m, \"tblSdModulXLeistung\" x, \"tblSdLeistung\" l " + 
		"WHERE (" + 
		  "(m.\"lngModulID\" =     x.\"lngModulID\") AND " + 
		  "(m.\"lngSdSeminarID\" = x.\"lngSdSeminarID\") AND " + 
		  "(x.\"lngLeistungID\"  = l.\"lngLeistungID\") AND " + 
		  "(x.\"lngSdSeminarID\" = l.\"lngSdSeminarID\") AND " + 
		  "(m.\"lngSdSeminarID\" = " + this.getSeminarID() + ") AND " + 
		  "(m.\"lngModulID\"     = " + lngModulID + ")" + 
		  "AND NOT EXISTS (" + 
			"SELECT " + 
			  "sx.\"lngSdSeminarID\", " + 
			  "sx.\"strMatrikelnummer\", " + 
			  "sx.\"lngLeistungsID\", " + 
			  "n.\"blnNoteBestanden\" " + 
			"FROM \"tblBdStudentXLeistung\" sx, \"tblSdNote\" n " + 
			"WHERE (" + 
			  "(sx.\"intNoteID\" 	= n.\"intNoteID\") AND " + 
			  "(sx.\"lngSdSeminarID\"  = n.\"lngSdSeminarID\") AND " + 
			  "(n.\"blnNoteBestanden\" ='t'::bool) AND " + 
			  "(sx.\"lngSdSeminarID\"	= m.\"lngSdSeminarID\") AND " + 
			  "(sx.\"strMatrikelnummer\"= '" + this.getMatrikelnummer() + "') AND " + 
			  "(sx.\"lngLeistungsID\"	= l.\"lngLeistungID\")" + 
			")" + 
		  ")" + 
		") ORDER BY \"strLeistungBezeichnung\" ASC;");
	}

	/**
	 * @version 5-27-10
	 * Calculates the sum of all credits that this student has passed and 
	 * that are mapped to the module specified with 'lngModulID'.
	 * No distiction is made if these credits are also mapped to other 
	 * modules or not.
	 * @param lngModulID: Module to look up.
	 * @return CreditPoint-Sum of all credits this student has collected that 
	 * are in the module specified through 'lngModulID'. (Grade must be passed)
	 */
	public float getCreditpointSum(long lngModulID){
		float fltReturn	= 0;
		try{
			ResultSet rst = sqlQuery("SELECT " + 
			  "mx.\"lngSdSeminarID\", " +
			  "mx.\"lngModulID\", " +
			  "sx.\"strMatrikelnummer\", " +
			  "n.\"blnNoteBestanden\", " +
			  "Sum(sx.\"sngStudentLeistungCreditPts\") AS summe " +
			"FROM \"tblSdModulXLeistung\" mx, \"tblBdStudentXLeistung\" sx, \"tblSdNote\" n  " +
			"WHERE (" +
			  "(mx.\"lngLeistungID\" = sx.\"lngLeistungsID\") And " +
			  "(sx.\"intNoteID\" = n.\"intNoteID\")" +
			") " +
			"GROUP BY mx.\"lngSdSeminarID\", mx.\"lngModulID\", n.\"blnNoteBestanden\", sx.\"strMatrikelnummer\" " +
			"HAVING (" +
			  "(mx.\"lngSdSeminarID\"=" + this.getSeminarID() + ") AND " +
			  "(mx.\"lngModulID\"=" + lngModulID + ") AND " +
			  "(sx.\"strMatrikelnummer\"='" + this.getMatrikelnummer() + "') AND " +
			  "(n.\"blnNoteBestanden\"='t'::bool) " +
			");");
			
		    if(rst.next()) fltReturn = rst.getFloat("summe");
		    rst.close();
		}catch(Exception eNoCreditPts){}
	    
		return fltReturn;
	}
	
	/**
	 * Convenience and compatibility method, same as addKlausuranmeldung(long, long, long, boolean, String) (without an info String):
	 * @see #addKlausuranmeldung(long, long, long, boolean, String, String)
	 * @param lngSeminarID the seminar where the course is taught,
	 * @param lngKurstypID coursetype to which to apply. This has become necessary since course and type are n:m.
	 * @param lngKursID unique identifier of this course.
	 * @param blnPruefung is this counted as a Pruefungsleistung?
	 * @throws SignUp specific Exception No. ERR_STUDENT_TEST_REGISTRATION_FAILED_DOUBLETTE (==12). Also throws
	 * if a coursetype has no lngKurstypLeistungsID configured. See FAQ.
	 **/
	public void addKlausuranmeldung(long lngSeminarID, long lngKurstypID, long lngKursID, boolean blnPruefung) throws Exception{
		addKlausuranmeldung(lngSeminarID, lngKurstypID, lngKursID, blnPruefung, "", "");
	}
	
	/**
	 * Convenience and compatibility method, same as addKlausuranmeldung(long, long, long, boolean, String) (without an info String):
	 * @see #addKlausuranmeldung(long, long, long, boolean, String, String)
	 * @param lngSeminarID the seminar where the course is taught,
	 * @param lngKurstypID coursetype to which to apply. This has become necessary since course and type are n:m.
	 * @param lngKursID unique identifier of this course.
	 * @param lModulID the module to map this credit to
	 * @param blnPruefung is this counted as a Pruefungsleistung?
	 * @throws SignUp specific Exception No. ERR_STUDENT_TEST_REGISTRATION_FAILED_DOUBLETTE (==12). Also throws
	 * if a coursetype has no lngKurstypLeistungsID configured. See FAQ.
	 **/
	public void addKlausuranmeldung(long lngSeminarID, long lngKurstypID, long lngKursID, long lModulID, boolean blnPruefung) throws Exception{
		addKlausuranmeldung(lngSeminarID, lngKurstypID, lngKursID, lModulID, blnPruefung, "", "", "");
	}
	
	/**
	 * <pre>
	 * Adds an exam-registration ('Klausuranmeldung') to 
	 * the specified course.
	 * 
	 * Checks if this is a unique registration and if "now" 
	 * is during registration period of this course.
	 * 
	 * <b>Note:</b> this method can be used for axis/.NET webservices.
	 * 
	 * Since April 2004 (5-20), this copies title and description &tc. 
	 * into StudentXLeistung (convenience).
	 * </pre>
	 * #hack (Apr 2004): needs at least a switch to turn the 'Doublette'-error off. (Right?)
	 * @param lngSeminarID the seminar where the course is taught,
	 * @param lngKurstypID coursetype to which to apply. This has become necessary since course and type are n:m.
	 * @param lngKursID unique identifier of this course.
	 * @param strCustom1 is stored under tblBdStudentXLeistung.strCustom1 (e.g. bulk upload).
	 * @param strCustom2 is stored under tblBdStudentXLeistung.strCustom2 (e.g. Modulzeitraum).
	 * @throws SignUp specific Exception No. ERR_STUDENT_TEST_REGISTRATION_FAILED_DOUBLETTE (==12). Also throws
	 * if a coursetype has no lngKurstypLeistungsID configured. See FAQ.
	 **/
	public void addKlausuranmeldung(long lngSeminarID, long lngKurstypID, long lngKursID, boolean blnPruefung, String strCustom1, String strCustom2) throws Exception{
		addKlausuranmeldung(lngSeminarID, lngKurstypID, lngKursID, g_ID_UNINITIALIZED, blnPruefung, strCustom1, strCustom2, "");
	}
	/**
	 * <pre>
	 * Adds an exam-registration ('Klausuranmeldung') to the specified course.
	 * Checks if this is a unique registration and if "now" is during 
	 * registration period of this course.
	 * 
	 * <b>Note:</b> this method can be used for axis/.NET webservices.
	 * 
	 * Since April 2004 (5-20), this copies title and description 
	 * &tc. into StudentXLeistung (convenience).
	 * 
	 * Die Leistungspunkte werden in der folgenden Reihenfolge nachgeschlagen:
	 * 
	 * (1) aus tblSdLeistung
	 * 
	 * (2) wenn vorhanden, wird stattdessen die LP Zahl aus KursXKurstyp genommen
	 * 
	 * (3) falls eine ModulID übergeben wurde, wird stattdessen die 
	 *     LP Zahl aus ModulXLeistung genommen
	 *     
	 * So können Veranstaltungen nach Studiengang (bzw. nach Modul)
	 * mit unterschiedlich vielen Leistungspunkten gewichtet werden.
	 * </pre>
	 * #hack (Apr 2004): needs at least a switch to turn the 'Doublette'-error off. (Right?)
	 * @param lngSeminarID the seminar where the course is taught,
	 * @param lngKurstypID coursetype to which to apply. This has become necessary since course and type are n:m.
	 * @param lngKursID unique identifier of this course.
	 * @param strCustom1 is stored under tblBdStudentXLeistung.strCustom1 (e.g. bulk upload).
	 * @param strCustom2 is stored under tblBdStudentXLeistung.strCustom2 (e.g. Modulzeitraum).
	 * @param strStudentXLeistungBemerkung is stored as tblBdStudentXLeistung.strStudentLeistungBemerkung
	 * @throws SignUp specific Exception No. ERR_STUDENT_TEST_REGISTRATION_FAILED_DOUBLETTE_COMMITMENT or ..._CREDIT. Also throws
	 * if a coursetype has no lngKurstypLeistungsID configured. See FAQ.
	 **/
	public void addKlausuranmeldung(long lngSeminarID, long lngKurstypID, long lngKursID, long lModulID, boolean blnPruefung, String strCustom1, String strCustom2, String strStudentXLeistungBemerkung) throws Exception{

		String 		strMatrikelnummer	= 	getDBCleanString( this.getMatrikelnummer() );
		long 		lngLeistungsID		=   -1;
		float		fltCreditPoints		=	0;

		// 0. gather data: what's this coursetype's LeistungsID? What's this LeistungID's next LeistungsCount?
		//	  what's this LeistungID's usual CreditPoint number?
		ResultSet rst=this.sqlQuery("SELECT " +
									  "t.\"lngSdSeminarID\", " +
									  "t.\"lngKurstypLeistungsID\", " +
									  "t.\"lngKurstypID\", " +
									  "l.\"sngLeistungCreditPts\"  " +
									"FROM \"tblSdKurstyp\" t, \"tblSdLeistung\" l " +
									"WHERE " +
									 "(" +
									  "(t.\"lngSdSeminarID\"=" + lngSeminarID + ") AND " +
									  "(t.\"lngKurstypID\"=" + lngKurstypID + ") AND " +
									  "(t.\"lngKurstypLeistungsID\" = l.\"lngLeistungID\") AND " +
									  "(t.\"lngSdSeminarID\" = l.\"lngSdSeminarID\")" +
									 ");");
		if(rst.next()){
					lngLeistungsID		=	rst.getLong("lngKurstypLeistungsID");
					fltCreditPoints		=	rst.getFloat("sngLeistungCreditPts");
		}

		long 		lngLeistungCount	=	this.getNextLeistungCount(lngSeminarID, lngLeistungsID);

		// 1. check that there is no registration or stored, passed credit for this Leistung, yet.
		
		// a) forbid app. to identical course
		if(!isUniqueKlausuranmeldung(lngSeminarID, lngKursID, strMatrikelnummer, lngLeistungsID)) throw new Exception(String.valueOf(ERR_STUDENT_TEST_REGISTRATION_FAILED_DOUBLETTE_COMMITMENT));
		
		// b) 
		if(!m_blnOverrideCommitmentDoubletteCheck) checkCommitmentDoublette(lngSeminarID, strMatrikelnummer, lngLeistungsID);

		// See if there are Credit Points in the course (they are more specific than those from 
		// the Leistung. (New in Jun 2006).
		try {
			float fTmp = new KursXKurstyp(lngSeminarID, lngKurstypID, lngKursID).getKursCreditPts();
			if(fTmp > 0) fltCreditPoints = fTmp;
		} catch (Exception e) {}
		
		// New on July 13, 2009: ModulID aus ModulXLeistung geht noch höher,
		// da so zwischen Studiengängen differenziert werden kann:
		// im BA gibt's 6, im MA 8 LP etc. pp.
		// DAS IST UNGETESTET
		if(lModulID!=g_ID_UNINITIALIZED){
			try{
				float fTmp = new ModulXLeistung(lngSeminarID, lngLeistungsID, lngKursID).getMinCreditPts();
				if(fTmp > 0) fltCreditPoints = fTmp;			
			}catch(Exception e2){}
		}
		
		rst.close();
		System.out.println("INSERT INTO " +
			  "\"tblBdStudentXLeistung\" " +
				"( \"lngSdSeminarID\", \"lngKlausuranmeldungKursID\", \"lngModulID\", \"strSLKursDetails\", \"lngKlausuranmeldungKurstypID\", \"strMatrikelnummer\", \"lngLeistungsID\", \"lngStudentLeistungCount\", \"sngStudentLeistungCreditPts\", \"strStudentLeistungAussteller\", \"strStudentLeistungAusstellerVor\", \"strStudentLeistungAusstellerTit\", \"lngDozentID\", \"blnStudentLeistungValidiert\", \"dtmStudentLeistungAntragdatum\", \"blnStudentLeistungKlausuranmeldung\", \"strSLKursUnivISID\", \"strSLKursTag\", \"dtmSLKursBeginn\", \"dtmSLKursEnde\", \"strSLKursRaum\", \"strSLKursTag2\", \"dtmSLKursBeginn2\", \"dtmSLKursEnde2\", \"strSLKursRaum2\", \"strSLKursTitel\", \"strSLKursTitel_en\", \"strSLKursBeschreibung\", \"strSLKursBeschreibung_en\", \"strSLKursLiteratur\", \"strSLKursZusatz\", \"strSLKursAnmeldung\", \"strSLKursVoraussetzung\", \"blnSLKursSchein\", \"strSLKursEinordnung\", \"intSLKursStunden\", \"dtmSLKursLastChange\", \"dtmSLKursScheinanmeldungBis\", \"dtmSLKursScheinanmeldungVon\", \"blnSLKursScheinanmeldungErlaubt\", \"strSLKursTerminFreitext\", \"intSLKursTeilnehmer\", \"strSLKursRaumExtern1\", \"strSLKursRaumExtern2\", \"blnStudentLeistungPruefung\", \"strStudentLeistungCustom1\", \"strStudentLeistungCustom2\", \"strStudentLeistungBemerkung\" ) " +
			"SELECT " +
			  "k.\"lngSdSeminarID\", " +
			  "k.\"lngKursID\", " +
			  ((lModulID==g_ID_UNINITIALIZED) ? "null, " : lModulID + ", ") +
			  "k.\"strKursTitel\", " +
			  lngKurstypID + " AS KurstypID, " +
			  "'" + strMatrikelnummer + "' AS strMatrikelnummaer, " +
			  lngLeistungsID + " AS lngLeistungsID, " +
			  lngLeistungCount + " AS lngStudentLeistungCount, " +
			  fltCreditPoints + " AS sngCreditPts, " +
			  "d.\"strDozentNachname\", " +
			  "d.\"strDozentVorname\", " +
			  "d.\"strDozentTitel\", " +
			  "d.\"lngDozentID\", " +
			  "'f'::bool AS LeistungValidiert, " +
			  "CURRENT_DATE AS Antragdatum, " +
			  "'t'::bool AS blnKlausuranmeldung, " +
			  "k.\"strKursUnivISID\", k.\"strKursTag\", k.\"dtmKursBeginn\", k.\"dtmKursEnde\", k.\"strKursRaum\", k.\"strKursTag2\", k.\"dtmKursBeginn2\", k.\"dtmKursEnde2\", k.\"strKursRaum2\", k.\"strKursTitel\", k.\"strKursTitel_en\", k.\"strKursBeschreibung\", k.\"strKursBeschreibung_en\", k.\"strKursLiteratur\", k.\"strKursZusatz\", k.\"strKursAnmeldung\", k.\"strKursVoraussetzung\", k.\"blnKursSchein\", k.\"strKursEinordnung\", k.\"intKursStunden\", k.\"dtmKursLastChange\", k.\"dtmKursScheinanmeldungBis\", k.\"dtmKursScheinanmeldungVon\", k.\"blnKursScheinanmeldungErlaubt\", k.\"strKursTerminFreitext\", k.\"intKursTeilnehmer\", k.\"strKursRaumExtern1\", k.\"strKursRaumExtern2\", " + (blnPruefung ? "'t'::bool" : "'f'::bool") + ", '" + getDBCleanString(strCustom1) + "', '" + getDBCleanString(strCustom2) + "', '" + getDBCleanString(strStudentXLeistungBemerkung) + "' " +
			"FROM \"tblSdDozent\" d, \"tblBdKurs\" k " +
			"WHERE " +
			  "(" +
			   "(d.\"lngDozentID\" = k.\"lngDozentID\") AND " +
			   "(d.\"lngSdSeminarID\" = k.\"lngSdSeminarID\") AND " +
			   "(k.\"lngSdSeminarID\"=" + lngSeminarID + ") AND " +
			   "(k.\"lngKursID\"=" + lngKursID + ") AND " +
			   "(k.\"blnKursPlanungssemester\"='f'::bool) AND " +
			   "(k.\"blnKursScheinanmeldungErlaubt\"='t'::bool) AND " +
			   "(k.\"dtmKursScheinanmeldungVon\"<=CURRENT_DATE) AND " +
			   "(k.\"dtmKursScheinanmeldungBis\">=CURRENT_DATE)" +
			  ");");
		// 2. add this one credit application
		// (new in January 2008 or 6-19: with ModulID
		if(sqlExeCount("INSERT INTO " +
			  "\"tblBdStudentXLeistung\" " +
				"( \"lngSdSeminarID\", \"lngKlausuranmeldungKursID\", \"lngModulID\", \"strSLKursDetails\", \"lngKlausuranmeldungKurstypID\", \"strMatrikelnummer\", \"lngLeistungsID\", \"lngStudentLeistungCount\", \"sngStudentLeistungCreditPts\", \"strStudentLeistungAussteller\", \"strStudentLeistungAusstellerVor\", \"strStudentLeistungAusstellerTit\", \"lngDozentID\", \"blnStudentLeistungValidiert\", \"dtmStudentLeistungAntragdatum\", \"blnStudentLeistungKlausuranmeldung\", \"strSLKursUnivISID\", \"strSLKursTag\", \"dtmSLKursBeginn\", \"dtmSLKursEnde\", \"strSLKursRaum\", \"strSLKursTag2\", \"dtmSLKursBeginn2\", \"dtmSLKursEnde2\", \"strSLKursRaum2\", \"strSLKursTitel\", \"strSLKursTitel_en\", \"strSLKursBeschreibung\", \"strSLKursBeschreibung_en\", \"strSLKursLiteratur\", \"strSLKursZusatz\", \"strSLKursAnmeldung\", \"strSLKursVoraussetzung\", \"blnSLKursSchein\", \"strSLKursEinordnung\", \"intSLKursStunden\", \"dtmSLKursLastChange\", \"dtmSLKursScheinanmeldungBis\", \"dtmSLKursScheinanmeldungVon\", \"blnSLKursScheinanmeldungErlaubt\", \"strSLKursTerminFreitext\", \"intSLKursTeilnehmer\", \"strSLKursRaumExtern1\", \"strSLKursRaumExtern2\", \"blnStudentLeistungPruefung\", \"strStudentLeistungCustom1\", \"strStudentLeistungCustom2\", \"strStudentLeistungBemerkung\" ) " +
			"SELECT " +
			  "k.\"lngSdSeminarID\", " +
			  "k.\"lngKursID\", " +
			  ((lModulID==g_ID_UNINITIALIZED) ? "null, " : lModulID + ", ") +
			  "k.\"strKursTitel\", " +
			  lngKurstypID + " AS KurstypID, " +
			  "'" + strMatrikelnummer + "' AS strMatrikelnummaer, " +
			  lngLeistungsID + " AS lngLeistungsID, " +
			  lngLeistungCount + " AS lngStudentLeistungCount, " +
			  fltCreditPoints + " AS sngCreditPts, " +
			  "d.\"strDozentNachname\", " +
			  "d.\"strDozentVorname\", " +
			  "d.\"strDozentTitel\", " +
			  "d.\"lngDozentID\", " +
			  "'f'::bool AS LeistungValidiert, " +
			  "CURRENT_DATE AS Antragdatum, " +
			  "'t'::bool AS blnKlausuranmeldung, " +
			  "k.\"strKursUnivISID\", k.\"strKursTag\", k.\"dtmKursBeginn\", k.\"dtmKursEnde\", k.\"strKursRaum\", k.\"strKursTag2\", k.\"dtmKursBeginn2\", k.\"dtmKursEnde2\", k.\"strKursRaum2\", k.\"strKursTitel\", k.\"strKursTitel_en\", k.\"strKursBeschreibung\", k.\"strKursBeschreibung_en\", k.\"strKursLiteratur\", k.\"strKursZusatz\", k.\"strKursAnmeldung\", k.\"strKursVoraussetzung\", k.\"blnKursSchein\", k.\"strKursEinordnung\", k.\"intKursStunden\", k.\"dtmKursLastChange\", k.\"dtmKursScheinanmeldungBis\", k.\"dtmKursScheinanmeldungVon\", k.\"blnKursScheinanmeldungErlaubt\", k.\"strKursTerminFreitext\", k.\"intKursTeilnehmer\", k.\"strKursRaumExtern1\", k.\"strKursRaumExtern2\", " + (blnPruefung ? "'t'::bool" : "'f'::bool") + ", '" + getDBCleanString(strCustom1) + "', '" + getDBCleanString(strCustom2) + "', '" + getDBCleanString(strStudentXLeistungBemerkung) + "' " +
			"FROM \"tblSdDozent\" d, \"tblBdKurs\" k " +
			"WHERE " +
			  "(" +
			   "(d.\"lngDozentID\" = k.\"lngDozentID\") AND " +
			   "(d.\"lngSdSeminarID\" = k.\"lngSdSeminarID\") AND " +
			   "(k.\"lngSdSeminarID\"=" + lngSeminarID + ") AND " +
			   "(k.\"lngKursID\"=" + lngKursID + ") AND " +
			   "(k.\"blnKursPlanungssemester\"='f'::bool) AND " +
			   "(k.\"blnKursScheinanmeldungErlaubt\"='t'::bool) AND " +
			   "(k.\"dtmKursScheinanmeldungVon\"<=CURRENT_DATE) AND " +
			   "(k.\"dtmKursScheinanmeldungBis\">=CURRENT_DATE)" +
			  ");")!=1) throw new Exception(String.valueOf(ERR_COMMITMENT_FAILED_ON_DB));
	}

	/**
	 * @param lngSeminarID
	 * @param lngKursID
	 * @param strMatrikelnummer
	 * @param lngLeistungsID
	 * @return
	 */
	public boolean isUniqueKlausuranmeldung(long lngSeminarID, long lngKursID, String strMatrikelnummer, long lngLeistungsID) {
		return (lookUp("lngSdSeminarID", "tblBdStudentXLeistung", "\"lngSdSeminarID\"=" + lngSeminarID + " AND \"strMatrikelnummer\"='" + strMatrikelnummer + "' AND \"lngLeistungsID\"=" + lngLeistungsID + " AND \"blnStudentLeistungKlausuranmeldung\"='t'::bool AND \"blnStudentLeistungValidiert\"='f'::bool AND \"lngKlausuranmeldungKursID\"=" + lngKursID)).equals("#NO_RESULT");
	}

	/**
	 * <pre>
	 * Checks if there already is a passed credit, or a commitment, to this credit.
	 * 
	 * A "passed credit" is a credit of the same Seminar and LeistungsID, graded with 
	 * a Note (grade) that is configured as 'passed' (blnNoteBestanden). The credit
	 * must also be validated (StudentXLeistung.blnStudentLeistungValidiert must be true).
	 * 
	 * A commitment is explicitly not validated (StudentXLeistung.blnStudentLeistungValidiert 
	 * must be false).
	 * 
	 * This method is public since 6-11-05
	 * </pre>
	 * @param lngSeminarID
	 * @param strMatrikelnummer
	 * @param lngLeistungsID
	 * @throws Exception ERR_STUDENT_TEST_REGISTRATION_FAILED_DOUBLETTE_COMMITMENT or ..._CREDIT
	 */
	public void checkCommitmentDoublette(long lngSeminarID, String strMatrikelnummer, long lngLeistungsID) throws Exception {
		String sError = "";
		boolean blnNext=false;
		ResultSet rst = sqlQuery("select * from \"tblBdStudentXLeistung\" x, \"tblSdNote\" n " + 
						"where " +
							"x.\"lngSdSeminarID\"=" + lngSeminarID + " AND " +
							"x.\"strMatrikelnummer\"='" + strMatrikelnummer + "' AND " +
							"x.\"lngLeistungsID\"=" + lngLeistungsID + " AND (" +
								"(n.\"lngSdSeminarID\"=x.\"lngSdSeminarID\" AND " +
								"n.\"intNoteID\"=x.\"intNoteID\" and " +
								"n.\"blnNoteBestanden\"='t' AND x.\"blnStudentLeistungValidiert\"='t'::bool) " +
							"OR " +	
							"(x.\"blnStudentLeistungKlausuranmeldung\"='t'::bool AND " +
							"x.\"blnStudentLeistungValidiert\"='f'::bool));");
		
		if(rst.next()){
			blnNext=true;
			sError = ((rst.getBoolean("blnStudentLeistungKlausuranmeldung")) ? String.valueOf(ERR_STUDENT_TEST_REGISTRATION_FAILED_DOUBLETTE_COMMITMENT) : String.valueOf(ERR_STUDENT_TEST_REGISTRATION_FAILED_DOUBLETTE_CREDIT));
		}
		rst.close();
	
		if(blnNext) throw new Exception(sError);
	}

	/**
	 * Checks if there is a record with the given SeminarID and LeistungsID of
	 * the current student that is _marked as passed_ (blnLeistungBestanden=true).
	 * <b>Note:</b> I don't see this method working here. Check where it is used.
	 * Use {@link #hasPassedCredit(long)} instead.
	 * @return true, if there is _no_ credit marked as passed of this student with this Seminar- and LeistungsID, false otherwise.
	 * @param lngSeminarID: id of seminar to check,
	 * @param lngLeistungsID: id of credit to look up.
	 * @deprecated
	 **/
	public boolean CreditExists(long lngSeminarID, long lngLeistungsID){
		return (!(lookUp("lngSdSeminarID", "tblBdStudentXLeistung", "\"lngSdSeminarID\"=" + lngSeminarID + " AND \"strMatrikelnummer\"='" + getDBCleanString(this.getMatrikelnummer()) + "' AND \"lngLeistungsID\"=" + lngLeistungsID + " AND \"blnStudentLeistungBestanden\"='t'::bool")).equals("#NO_RESULT"));
	}
	
	/**
	 * @param lLeistungID
	 * @return true, wenn der Studierende eine Leistung mit einer Note "bestanden" vorweisen kann.
	 * @throws Exception
	 */
	public boolean hasPassedCredit(long lLeistungID) throws Exception{
		return (sqlQuery("SELECT x.\"lngSdSeminarID\", " + 
		  "x.\"strMatrikelnummer\", " + 
		  "x.\"lngLeistungsID\", " + 
		  "n.\"blnNoteBestanden\" " +
		"FROM \"tblSdNote\" n, \"tblBdStudentXLeistung\" x " + 
		"WHERE " +
		  "n.\"lngSdSeminarID\" = x.\"lngSdSeminarID\" AND " + 
		  "n.\"intNoteID\" = x.\"intNoteID\" AND " + 
		  "x.\"lngSdSeminarID\" = " + getSeminarID() + " AND " + 
		  "x.\"strMatrikelnummer\" = '" + getMatrikelnummer() + "' AND " + 
		  "x.\"lngLeistungsID\" = " + lLeistungID + " AND " + 
		  "n.\"blnNoteBestanden\" = 't';")).next();
	}

	/**
	 * What is the next value of StudentLeistungCount?
	 * @return Next incremental value of 'StudentLeistungCount' of a given Leistung in a given Seminar.
	 * @param lngSeminarID: Seminar where this Leistung belongs.
	 * @param lngLeistungID: the Leistung.
	 **/
	public long getNextLeistungCount(long lngSeminarID, long lngLeistungsID){

	  long lngReturn=1;
	  try{
			ResultSet rst=sqlQuery("SELECT " +
			  "Max(x.\"lngStudentLeistungCount\")+1 FROM \"tblBdStudentXLeistung\" x " +
			  "GROUP BY x.\"lngSdSeminarID\", x.\"strMatrikelnummer\", x.\"lngLeistungsID\" " +
			  "HAVING " +
				"(" +
				 "(x.\"lngSdSeminarID\"=" + lngSeminarID + ") AND " +
				 "(x.\"strMatrikelnummer\"='" + this.getMatrikelnummer() + "') AND " +
				 "(x.\"lngLeistungsID\"=" + lngLeistungsID + ")" +
				");");

			rst.next();
			lngReturn=rst.getLong(1);
			rst.close();
			rst=null;
		}catch(Exception eGetNextLeistungCount){}
		return lngReturn;
	}

	/**
	 * Deletes a student's exam registration. checks first if there <i>is</i> an exam registration that can be changed (date-check). If
	 * there is such a registration, it is then deleted. Checks for passwords etc. must be made elsewhere.
	 * @param lngSeminarID: the exam registration is valid for which seminar?
	 * @param strMatrikelnummer: the student's registration number.
	 * @param lngLeistungsID: for what exam is this a registration?
	 * @param lngStudentLeistungCount: the missing bit of 'tblStudentXLeistung' index: how many registrations/credits are there?
	 **/
	public void deleteKlausuranmeldung(long lngSeminarID, String strMatrikelnummer, long lngLeistungsID, long lngStudentLeistungCount) throws Exception{

		boolean isInTime=false;

//		ResultSet rstInTime=sqlQuery("SELECT " +
//			  "k.\"dtmKursScheinanmeldungVon\", " +
//			  "k.\"dtmKursScheinanmeldungBis\", " +
//			  "x.\"lngSdSeminarID\", " +
//			  "x.\"strMatrikelnummer\", " +
//			  "x.\"lngLeistungsID\", " +
//			  "x.\"blnStudentLeistungValidiert\", " +
//			  "x.\"blnStudentLeistungKlausuranmeldung\", " +
//			  "x.\"lngStudentLeistungCount\" " +
//			"FROM \"tblBdKurs\" k, \"tblBdStudentXLeistung\" x " +
//			"WHERE " +
//			 "(" +
//			   "(x.\"lngKlausuranmeldungKursID\" = k.\"lngKursID\") AND " +
//			   "(x.\"lngSdSeminarID\" = k.\"lngSdSeminarID\") AND " +
//			   "(k.\"dtmKursScheinanmeldungVon\"<=CURRENT_DATE) AND " +
//			   "(k.\"dtmKursScheinanmeldungBis\">=CURRENT_DATE) AND " +
//			   "(x.\"strMatrikelnummer\"='" + strMatrikelnummer + "') AND " +
//			   "(x.\"lngSdSeminarID\"=" + lngSeminarID + ") AND " +
//			   "(k.\"lngSdSeminarID\"=" + lngSeminarID + ") AND " +
//			   "(x.\"lngLeistungsID\"=" + lngLeistungsID +") AND " +
//			   "(x.\"blnStudentLeistungValidiert\"='f'::bool) AND " +
//			   "(x.\"blnStudentLeistungKlausuranmeldung\"='t'::bool) AND " +
//			   "(x.\"lngStudentLeistungCount\"=" + lngStudentLeistungCount + ")" +
//			 ");");
                
                // Geändert im November 2012 für die Germanistik:
                // Für die Anmeldung zum Selbststudium wird nicht 
                // auf einen angelegten Kurs zugegriffen, daher 
                // könnte die nach der bisherigen Systematik (die 
                // die erlaubte Löschperiode aus dem zugehörigen Kurs
                // ausliest) nie gelöscht werden.
                //
                // Es scheint allgemein OK, die erlaubte Löschperiode
                // statt aus dem Kurs aus der Anmeldung auszulesen
                // (denn dorthin wird sie ja vom Kurs aus kopiert).
		ResultSet rstInTime=sqlQuery("SELECT " +
			  "x.\"dtmSLKursScheinanmeldungVon\", " +
			  "x.\"dtmSLKursScheinanmeldungBis\", " +
			  "x.\"lngSdSeminarID\", " +
			  "x.\"strMatrikelnummer\", " +
			  "x.\"lngLeistungsID\", " +
			  "x.\"blnStudentLeistungValidiert\", " +
			  "x.\"blnStudentLeistungKlausuranmeldung\", " +
			  "x.\"lngStudentLeistungCount\" " +
			"FROM \"tblBdStudentXLeistung\" x " +
			"WHERE " +
			 "(" +
			   "(x.\"dtmSLKursScheinanmeldungVon\"<=CURRENT_DATE) AND " +
			   "(x.\"dtmSLKursScheinanmeldungBis\">=CURRENT_DATE) AND " +
			   "(x.\"strMatrikelnummer\"='" + strMatrikelnummer + "') AND " +
			   "(x.\"lngSdSeminarID\"=" + lngSeminarID + ") AND " +
			   "(x.\"lngLeistungsID\"=" + lngLeistungsID +") AND " +
			   "(x.\"blnStudentLeistungValidiert\"='f'::bool) AND " +
			   "(x.\"blnStudentLeistungKlausuranmeldung\"='t'::bool) AND " +
			   "(x.\"lngStudentLeistungCount\"=" + lngStudentLeistungCount + ")" +
			 ");");
		isInTime=rstInTime.next();
		rstInTime.close();

		if(isInTime) sqlExe("delete from \"tblBdStudentXLeistung\" where (\"lngSdSeminarID\"=" + lngSeminarID +
							"AND \"strMatrikelnummer\"='" + this.getMatrikelnummer() + "' AND \"lngLeistungsID\"=" +
							lngLeistungsID + " AND \"lngStudentLeistungCount\"=" + lngStudentLeistungCount + ");");
	}

	/**
	 * This student's credits in this seminar, signed by teacher with id 'lngTeacherPID.'
	 * @return ResultSet with registered, validated credits of this student, signed by the teacher indicated through 'lngTeacherPID,' ordered by date of document.
	 * Presupposes that SeminarID has been set prior to calling.
	 * @param lngTeacherPID: id of teacher.
	 * @throws Exception if connection to db is erroneous.
	 **/
	public ResultSet StudentCredits(long lngTeacherPID) throws Exception{
		return sqlQuery("SELECT x.\"lngSdSeminarID\", x.\"dtmStudentLeistungAusstellungsd\", x.\"sngStudentLeistungCreditPts\", l.\"lngLeistungID\", l.\"strLeistungBezeichnung\", x.\"lngStudentLeistungCount\", x.\"strStudentLeistungNote\", x.\"strStudentLeistungAussteller\", x.\"lngDozentID\", x.\"blnStudentLeistungPruefung\", n.* " +
					"FROM \"tblSdLeistung\" l, \"tblBdStudentXLeistung\" x, \"tblSdNote\" n " +
					"WHERE ((l.\"lngLeistungID\" = x.\"lngLeistungsID\") AND " +
					"(l.\"lngSdSeminarID\" = x.\"lngSdSeminarID\") AND " +
					"(n.\"lngSdSeminarID\" = x.\"lngSdSeminarID\") AND " +
					"(n.\"intNoteID\" = x.\"intNoteID\") AND " +
					"(x.\"lngSdSeminarID\"=" + this.getSeminarID() + ") AND " +
					"(l.\"lngSdSeminarID\"=" + this.getSeminarID()  + ") AND " +
					"(n.\"lngSdSeminarID\"=" + this.getSeminarID()  + ") AND " +
					"(x.\"lngDozentID\"=" + lngTeacherPID + ") AND " +
					"(x.\"strMatrikelnummer\"='" + getDBCleanString( this.getMatrikelnummer() ) + "') AND " +
					"(x.\"blnStudentLeistungValidiert\"='t') AND " +
					"(x.\"blnStudentLeistungKlausuranmeldung\"='f')" +
					") ORDER BY x.\"dtmStudentLeistungAusstellungsd\" ASC;");
	}

	/**
	 * This student's credits in this seminar.
	 * @return ResultSet with registered, validated credits of this student.ordered by date of document.
	 * Presupposes that SeminarID has been set prior to calling.
	 * @throws Exception if connection to db is erroneous.
	 **/
	public ResultSet StudentCredits() throws Exception{
		return sqlQuery("SELECT x.\"lngSdSeminarID\", x.\"dtmStudentLeistungAusstellungsd\", x.\"sngStudentLeistungCreditPts\", l.\"lngLeistungID\", l.\"strLeistungBezeichnung\", x.\"lngStudentLeistungCount\", x.\"strStudentLeistungNote\", x.\"strStudentLeistungAussteller\", x.\"lngDozentID\", x.\"blnStudentLeistungPruefung\", n.* " +
					"FROM \"tblSdLeistung\" l, \"tblBdStudentXLeistung\" x, \"tblSdNote\" n " +
					"WHERE ((l.\"lngLeistungID\" = x.\"lngLeistungsID\") AND " +
					"(l.\"lngSdSeminarID\" = x.\"lngSdSeminarID\") AND " +
					"(n.\"lngSdSeminarID\" = x.\"lngSdSeminarID\") AND " +
					"(n.\"intNoteID\" = x.\"intNoteID\") AND " +
					"(x.\"lngSdSeminarID\"=" + this.getSeminarID() + ") AND " +
					"(l.\"lngSdSeminarID\"=" + this.getSeminarID()  + ") AND " +
					"(n.\"lngSdSeminarID\"=" + this.getSeminarID()  + ") AND " +
					"(x.\"strMatrikelnummer\"='" + getDBCleanString( this.getMatrikelnummer() ) + "') AND " +
					"(x.\"blnStudentLeistungValidiert\"='t') AND " +
					"(x.\"blnStudentLeistungKlausuranmeldung\"='f')" +
					") ORDER BY x.\"dtmStudentLeistungAusstellungsd\" ASC;");
	}

	/**
	 * This student's credits in this seminar, or this students commitments.
	 * @return ResultSet with credits of this student.ordered by date of document. If 'blnCommitment' is 'true,'
	 * then only those Credits are selected, that are _not_ validated and whose flag 'commitment' (=Klausuranmeldung) is
	 * set to true. If 'blnCommitment' is false, the opposite are selected (which is then equal to method 'StudentCredits()').
	 * Presupposes that SeminarID has been set prior to calling.
	 * @param blnCommitment: flag indicating whether commitments are to be selected, or whether credits are to be selected.
	 * @throws Exception if connection to db is erroneous.
	 **/
	public ResultSet StudentCredits(boolean blnCommitment) throws Exception{
		return this.StudentCredits(blnCommitment,(byte)0);
	}

	/**
	 * @return ResultSet with credits of this student.ordered by date
	 * (default) or by LeistungBezeichnung (orderBy==1). If 'blnCommitment' is 'true,'
	 * then only those Credits are selected, that are _not_ validated and whose flag 'commitment' (=Klausuranmeldung) is
	 * set to true. If 'blnCommitment' is false, the opposite are selected (which is then equal to method 'StudentCredits()').
	 * Presupposes that SeminarID has been set prior to calling.
	 * @param blnCommitment: flag indicating whether commitments are to be selected, or whether credits are to be selected.
	 * @throws Exception if connection to db is erroneous.
	 **/	
	public ResultSet StudentCredits(boolean blnCommitment, byte bytOrderByName) throws Exception{
		
		String strOrderBy	= "";
		switch(bytOrderByName){
			case 1:
				strOrderBy = "l.\"strLeistungBezeichnung\"";
				break;
			default:
				strOrderBy = "x.\"dtmStudentLeistungAusstellungsd\"";
		}
		
		return sqlQuery("SELECT x.*, l.*, n.* " +
				"FROM \"tblSdLeistung\" l, \"tblBdStudentXLeistung\" x, \"tblSdNote\" n " +
				"WHERE ((l.\"lngLeistungID\" = x.\"lngLeistungsID\") AND " +
				"(l.\"lngSdSeminarID\" = x.\"lngSdSeminarID\") AND " +
				"(n.\"lngSdSeminarID\" = x.\"lngSdSeminarID\") AND " +
				"(n.\"intNoteID\" = x.\"intNoteID\") AND " +
				"(x.\"lngSdSeminarID\"=" + this.getSeminarID() + ") AND " +
				"(l.\"lngSdSeminarID\"=" + this.getSeminarID()  + ") AND " +
				"(n.\"lngSdSeminarID\"=" + this.getSeminarID()  + ") AND " +
				"(x.\"strMatrikelnummer\"='" + getDBCleanString( this.getMatrikelnummer() ) + "') AND " +
				"(x.\"blnStudentLeistungValidiert\"=" + (this.getDBBoolRepresentation(!(blnCommitment))) + ") AND " +
				"(x.\"blnStudentLeistungKlausuranmeldung\"=" + (this.getDBBoolRepresentation(blnCommitment)) + ")" +
				") ORDER BY " + strOrderBy + " ASC;");
	}

	/**
	 * This student's exams ('Pruefungen') at this seminar.
	 * @return ResultSet with student's exams (!, not credits) ordered by lngPruefungID, or with exam applications.
	 * Presupposes that a SeminarID has been set.
	 * @param blnApplication: if true, filter is 'not validated and flag-for-application true'; if false,
	 * filter is logical opposite.
	 * @throws Exception if connection to db is erroneous.
	 **/
	public ResultSet StudentExams(boolean blnApplication) throws Exception{
		return sqlQuery("SELECT " +
			  "x.*, " +
			  "p.\"lngPruefungID\", " +
			  "p.\"strPruefungBezeichnung\", " +
			  "p.\"strPruefungBeschreibung\" " +
			"FROM \"tblSdPruefung\" p, \"tblBdStudentXPruefung\" x " +
			  "WHERE " +
			  "(" +
				"(p.\"lngPruefungID\" = x.\"lngSdPruefungsID\") AND " +
				"(p.\"lngSdSeminarID\" = x.\"lngSdSeminarID\") AND " +
				"(p.\"lngSdSeminarID\" = " + this.getSeminarID() + ") AND " +
				"(x.\"strMatrikelnummer\"='" + getDBCleanString( this.getMatrikelnummer() ) + "') AND " +
				"(x.\"blnStudentPruefungValidiert\"=" + getDBBoolRepresentation(!blnApplication) + ") AND " +
				"(x.\"blnStudentPruefungAnmeldung\"=" + getDBBoolRepresentation(blnApplication) + ") " +
			  ")" +
			"ORDER BY p.\"lngPruefungID\" ASC;");
	}

	/**
	 * @return ResultSet with the exams that are prerequisite to the one specified
	 * through 'lngPruefungID,' and that the current student has not yet passed.
	 * The context in which this method is used is this: a new exam is to be issued
	 * to a student. The first thing to check is whether all prerequisite exams for
	 * this one are stored. (Student applies for a Master, is the Bachelor stored?).
	 * So a ResultSet with all missing exams is handed back. If (not rst.next()), all
	 * prerequisite exams are stored.<br>
	 * The query works like this:<br>
	 * select exams from pruefungxpruefung<br>
	 *   where there is no passed result in 'prereq'.
	 * The query hands back cols such as <br>
	 * lngPruefungPrereqID (which is the Id of an exam that must be marked as passed before
	 * proceeding further),<br>
	 * strPruefungBezeichnung, strPruefungBeschreibung etc. all of the _missing_ exam.
	 * @param lngPruefungID: id of exam that is to be checked for prerequisites.
	 * @throws Exception if connection to db is erroneous.
	 **/
	public ResultSet getMissingPrereqExams(long lngPruefungID) throws Exception{
		return sqlQuery("SELECT " +
				 "x.\"lngSdSeminarID\", " +
				 "x.\"lngPruefungID\", " +
				 "x.\"lngPruefungPrereqID\", " +
				 "p.* " +
				"FROM \"tblSdPruefungXPruefung\" x, \"tblSdPruefung\" p " +
				"WHERE (" +
				 "(x.\"lngSdSeminarID\" = " + this.getSeminarID() + ") AND " +
				 "(x.\"lngSdSeminarID\" = p.\"lngSdSeminarID\") AND " +
				 "(x.\"lngPruefungID\" = " + lngPruefungID + ") AND " +
				 "(x.\"lngPruefungPrereqID\" = p.\"lngPruefungID\") AND " +
				 "NOT EXISTS (" +
					"SELECT " +
					 "sx.\"lngSdSeminarID\", " +
					 "sx.\"lngSdPruefungsID\", " +
					 "sx.\"strMatrikelnummer\", " +
					 "sx.\"blnStudentPruefungBestanden\" " +
					"FROM \"tblBdStudentXPruefung\" sx " +
					"WHERE (" +
					 "(sx.\"strMatrikelnummer\" = '" + getDBCleanString( this.getMatrikelnummer() ) + "') AND " +
					 "(sx.\"lngSdSeminarID\" = x.\"lngSdSeminarID\") AND " +
					 "(sx.\"blnStudentPruefungBestanden\"='t'::bool) AND " +
					 "(x.\"lngPruefungPrereqID\" = sx.\"lngSdPruefungsID\")" +
					")" +
				 ")" +
				");");
	}

	/**
	 * Method to load all data from database into this Bean-extension.
	 * @param lngSeminarID: sets view of this student to a single seminar.
	 * @param lngStudentPID: the student's identifier no. 1,
	 * @param strMatrikelnummer: the student's identifier no. 2.
	 * @throws Exception if connection to db is erroneous.
	 **/
	public void init(long lngSeminarID, long lngStudentPID, String strMatrikelnummer) throws Exception{
		//this.init(lngStudentPID, strMatrikelnummer);

		//this.init(rst.getLong("lngStudentPID"), rst.getString("strMatrikelnummer"));
		// untested. the original 'init' uses 'getSQLWhereClause' which in turn checks
		// for lngStudentRandom ... so that this only works for students who have
		// not ever logged in ...
		ResultSet rst	= null;

		rst	= sqlQuery("select * from \"tblBdStudent\" where " +
					"\"lngStudentPID\"=" + lngStudentPID + " AND " +
					"\"strMatrikelnummer\"='" + strMatrikelnummer + "';");

		resetThis();
		try{rst.next(); this.initByRst ( rst ); rst.close();}catch(Exception eNoStudent){}
		this.setSeminarID ( lngSeminarID );
	}
	
	/**
	 * Method to load all data from database into this Bean-extension.
	 * @param lngSeminarID: sets view of this student to a single seminar.
	 * @param strMatrikelnummer: the student's identifier no. 2.
	 * @throws Exception if connection to db is erroneous.
	 **/
	public void init(long lngSeminarID, String strMatrikelnummer) throws Exception{
		//this.init(lngStudentPID, strMatrikelnummer);

		//this.init(rst.getLong("lngStudentPID"), rst.getString("strMatrikelnummer"));
		// untested. the original 'init' uses 'getSQLWhereClause' which in turn checks
		// for lngStudentRandom ... so that this only works for students who have
		// not ever logged in ...
		ResultSet rst	= null;

		rst	= sqlQuery("select * from \"tblBdStudent\" where " +
					"\"strMatrikelnummer\"='" + strMatrikelnummer + "';");
		
		resetThis();
		try{rst.next(); this.initByRst ( rst );}
		catch(Exception eNoStudent){}
		
		try{
			rst.close();
			disconnect();
		}catch(Exception eNotOpenIgnore){}
		
		resetThis();
		this.setSeminarID ( lngSeminarID );
	}	

	/**
	 * Resets object variables that are part of 
	 * "StudentData" but not of the super().
	 */
	private void resetThis() {
		m_blnGroupIDSet=false;
		m_objStudGruppe=null;
		m_strBAMANebenfach="#";
		m_strDebug="";
		m_iStudentFachID=-1;
	}

	/**
	 * Method to load all data from database into this Bean-extension.
	 * Here, a student is identified by name and birthdate.
	 * @param lngSeminarID: sets view of this student to a single seminar,
	 * @param strName: the student's last name,
	 * @param h: the student's birthdate as com.shj.signUp.HTML.HtmlDate.
	 * @throws Exception if connection to db is erroneous or, more notably,
	 *  if there is not exactly one student identified with this name and birthdate.
	 **/
	public void init(long lngSeminarID, String strName, de.shj.UP.HTML.HtmlDate h) throws Exception{
		ResultSet rst	= null;
		long 	  lngII	= 0;

		rst	= sqlQuery("select * from \"tblBdStudent\" where " +
					"\"strStudentNachname\"='" + strName + "' AND " +
					"\"dtmStudentGeburtstag\"='" + h.getIsoDate() + "';");

		while(rst.next()){
			lngII ++;
			//this.init(rst.getLong("lngStudentPID"), rst.getString("strMatrikelnummer"));
			// untested. the original 'init' uses 'getSQLWhereClause' which in turn checks
			// for lngStudentRandom ... so that this only works for students who have
			// not ever logged in ...
			this.initByRst( rst );
		}
		resetThis();
		this.setSeminarID(lngSeminarID);
		rst.close();
		if(lngII!=1) throw new Exception("More than one or less than one student with name '" + strName + "' and birthdate '" + h.getIsoDate() + "'");
	}

	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 	4.   P R I V A T E   M E T H O D S
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------

	/**
	 * For adding a student: what is the next identifier?
	 * @return maximum PID plus one.
	 **/
	private long getNextPID(){

		long lngReturn=-10;

		try{

			ResultSet rst=sqlQuery("SELECT Max(\"lngStudentPID\")+1 FROM \"tblBdStudent\";");
			rst.next();
			lngReturn=rst.getLong(1);
			rst.close();
			rst=null;

		}catch(Exception eGetNextPID){}

		return lngReturn;
	}


	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 	5.   P R O T E C T E D|P U B L I C    Q U E R I E S
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	/**
	 * @param lngExamID Id of the exam that the module must be mapped to,
	 * @param lngModuleID Id of the module that the credits must be mapped to.
	 * @param blnCommitmentsOnly set this to true if you want to restrict this set to credits that have an application-date 
	 * (so they were generated by a commitment).
	 * @return This student's credit's with the following properties:
	 * (1) they are mapped to the specified Exam and Module,
	 * (2) they are validated and not a commitment,
	 * (3) they are graded (grade is passed),
	 * (4) they are not already in 'tblBdStudentPruefungDetail' as Pruefungsleistung.
	 * ordered by gradevalue (best first) and credit points (highest first).
	 * Note that the set contains Pruefungsleistungen as well as Zulassungsvoraussetzungen.
	 * @version 5-29-1 (Refactored from StudentExamAddPickCredits.jsp, Computerlinguistik hd, Dec 13, 2004).
	 * @throws Exception
	 */
	public ResultSet getStudentCredits(long lngExamID, long lngModuleID, boolean blnOnlyCommittedCredits) throws Exception{
		return getStudentCredits(lngExamID, lngModuleID, blnOnlyCommittedCredits, true);
	}
	
	/**
	 * @param lngExamID Id of the exam that the module must be mapped to,
	 * @param lngModuleID Id of the module that the credits must be mapped to.
	 * @param blnCommitmentsOnly set this to true if you want to restrict this set to credits that have an application-date
	 * @param blnNotAlreadyCounted exclude credits that have already been counted as Pruefungsleistung? (Default is 'true').
	 * (so they were generated by a commitment).
	 * @return This student's credit's with the following properties:
	 * (1) they are mapped to the specified Exam and Module,
	 * (2) they are validated and not a commitment,
	 * (3) they are graded (grade is passed),
	 * (4) if blnNotAlreadyCounted is true (which it should), credits are disregarded that are 
	 * already in 'tblBdStudentPruefungDetail' as Pruefungsleistung.
	 * ordered by gradevalue (best first) and credit points (highest first).
	 * Note that the set contains Pruefungsleistungen as well as Zulassungsvoraussetzungen.
	 * @version 5-29-1 (Refactored from StudentExamAddPickCredits.jsp, Computerlinguistik hd, Dec 13, 2004).
	 * @throws Exception
	 */
	public ResultSet getStudentCredits(long lngExamID, long lngModuleID, boolean blnOnlyCommittedCredits, boolean blnNotAlreadyCounted) throws Exception{ 
	  return sqlQuery("SELECT " +
			  "pxm.\"lngSdSeminarID\", " +
			  "pxm.\"lngPruefungID\", " +
			  "pxm.\"lngModulID\", " +
			  "sxl.\"strMatrikelnummer\", " +
			  "sxl.\"lngLeistungsID\", " +
			  "sxl.\"lngStudentLeistungCount\", " +
			  "sxl.\"blnStudentLeistungValidiert\", " +
			  "sxl.\"blnStudentLeistungKlausuranmeldung\", " +
			  "sxl.\"sngStudentLeistungCreditPts\", " +
			  "sxl.\"blnStudentLeistungPruefung\", " +
			  "sxl.\"strSLKursTitel\", " +
			  "sxl.\"dtmStudentLeistungAusstellungsd\", " +
			  "mxl.\"lngLeistungID\", " +
			  "n.*, " +
			  "l.\"strLeistungBezeichnung\" " +
			"FROM \"tblSdPruefungXModul\" pxm, \"tblBdStudentXLeistung\" sxl, \"tblSdNote\" n, \"tblSdLeistung\" l, \"tblSdModulXLeistung\" mxl " +
			"WHERE (" +
			  "(l.\"lngLeistungID\" = sxl.\"lngLeistungsID\") AND " +
			  (blnNotAlreadyCounted ? "not exists( select * from " +
			  		"\"tblBdStudentPruefungDetail\" where(" +
			  		"\"blnStudentLeistungPruefung\"=" + this.getDBBoolRepresentation(true) + " and " +
			  		"\"lngLeistungsID\"=sxl.\"lngLeistungsID\" and " +
			  		"\"lngStudentLeistungCount\"=sxl.\"lngStudentLeistungCount\" and " +
			  		"\"strMatrikelnummer\"=sxl.\"strMatrikelnummer\")" +
			  		") and " : "") +
			  "(l.\"lngSdSeminarID\" = sxl.\"lngSdSeminarID\") AND " +
			  "(n.\"intNoteID\" = sxl.\"intNoteID\") AND " +
			  "(n.\"lngSdSeminarID\" = sxl.\"lngSdSeminarID\") AND " +
			  "(mxl.\"lngLeistungID\" = sxl.\"lngLeistungsID\") AND " +
			  "(mxl.\"lngSdSeminarID\" = sxl.\"lngSdSeminarID\") AND " +
			  "(pxm.\"lngModulID\" = mxl.\"lngModulID\") AND " +
			  "(pxm.\"lngSdSeminarID\" = mxl.\"lngSdSeminarID\") AND " +
			  "(pxm.\"lngSdSeminarID\"=" + this.getSeminarID() + ") AND " +
			  "(pxm.\"lngPruefungID\"=" + lngExamID + ") AND " +
			  "(pxm.\"lngModulID\"=" + lngModuleID + ") AND " +
			  "(sxl.\"strMatrikelnummer\"='" + this.getMatrikelnummer() + "') AND " +
			  "(sxl.\"blnStudentLeistungValidiert\"='t') AND " +
			  "(sxl.\"blnStudentLeistungKlausuranmeldung\"='f') AND " +
			  ((blnOnlyCommittedCredits) ? "(sxl.\"dtmStudentLeistungAntragdatum\" NOTNULL) AND " : "") +
			  "(n.\"blnNoteBestanden\"='t')" +
			")" +
			"ORDER BY n.\"sngNoteWertDE\" ASC, sxl.\"sngStudentLeistungCreditPts\" DESC;");
	}

	/**
	 * This student's 'Klausuranmeldungen' (commitment, applications for tests) in this seminar, ordered by 'LeistungID ASC.'
	 * An exam application is, technically, a credit with the flags 'validated' false and 'Klausuranmeldung' true.
	 * @return ResultSet with test applications of this student (including the time-dependant,
     * calculated field 'blnAllowChanges'). Presupposes that a 'SeminarID' is set.
     * @throws Exception if connection to db is erroneous.
     **/
	public ResultSet StudentExamApplicationsDEPRECATED() throws Exception{
		return sqlQuery("SELECT " +
		  "l.\"strLeistungBezeichnung\", " +
		  "d.\"strDozentTitel\", " +
		  "d.\"strDozentVorname\", " +
		  "d.\"strDozentNachname\", " +
		  "k.\"lngKursID\", " +
		  "k.\"strKursTag\", " +
		  "k.\"dtmKursBeginn\", " +
		  "k.\"dtmKursEnde\", " +
		  "k.\"dtmKursScheinanmeldungVon\", " +
		  "k.\"dtmKursScheinanmeldungBis\", " +
		  "d.\"lngSdSeminarID\", " +
		  "k.\"lngSdSeminarID\", " +
		  "x.*, " +
		  "CASE WHEN ((k.\"dtmKursScheinanmeldungVon\"<=CURRENT_DATE AND k.\"dtmKursScheinanmeldungBis\">=CURRENT_DATE)) THEN 't'::bool ELSE 'f'::bool END AS \"blnAllowChanges\" " +
		"FROM \"tblSdDozent\" d, \"tblBdKurs\" k, \"tblSdLeistung\" l, \"tblBdStudentXLeistung\" x " +
		"WHERE " +
		  "(l.\"lngSdSeminarID\"=" + this.getSeminarID() + ") AND " +
		  "(x.\"lngSdSeminarID\"=" + this.getSeminarID() + ") AND " +
		  "(k.\"lngSdSeminarID\"=" + this.getSeminarID() + ") AND " +
		  "(d.\"lngSdSeminarID\"=" + this.getSeminarID() + ") AND " +
		  "(x.\"strMatrikelnummer\"='" + getDBCleanString( this.getMatrikelnummer() ) + "') AND " +
		  "(l.\"lngSdSeminarID\" = x.\"lngSdSeminarID\") AND " +
		  "(k.\"lngSdSeminarID\" = x.\"lngSdSeminarID\") AND " +
		  "(l.\"lngLeistungID\" = x.\"lngLeistungsID\") AND " +
		  "(d.\"lngSdSeminarID\" = k.\"lngSdSeminarID\") AND " +
		  "(k.\"lngKursID\" = x.\"lngKlausuranmeldungKursID\") AND " +
		  "(d.\"lngDozentID\" = k.\"lngDozentID\")  AND " +
		  "(x.\"blnStudentLeistungValidiert\"=" + getDBBoolRepresentation(false) + ") AND " +
		  "(x.\"blnStudentLeistungKlausuranmeldung\"=" + getDBBoolRepresentation(true) + ") " +
		"ORDER BY x.\"lngLeistungsID\" ASC;");
	}

	/**
	 * This student's 'Klausuranmeldungen' (commitment, applications for tests) in this seminar, ordered by 'LeistungID ASC.'
	 * An exam application is, technically, a credit with the flags 'validated' false and 'Klausuranmeldung' true.
	 * @return ResultSet with test applications of this student (including the time-dependant,
     * calculated field 'blnAllowChanges'). Presupposes that a 'SeminarID' is set.<br />
     * As of version 6.01.10 (May 14,2005) this method makes sure that also those commitments 
     * are displayed, that have no course linked to them anymore (because, for example, the 
     * course was deleted).<br />
     * These orphaned commitments are never marked as 'changeable'.
     * @throws Exception if connection to db is erroneous.
     **/
	public ResultSet StudentExamApplications() throws Exception{
		return sqlQuery("SELECT " +
		  "l.*, " +
		  "d.\"strDozentTitel\", " +
		  "d.\"strDozentVorname\", " +
		  "d.\"strDozentNachname\", " +
		  "k.\"lngKursID\", " +
		  "k.\"blnKursScheinanmeldungErlaubt\", " +
		  "k.\"strKursTag\", " +
		  "k.\"dtmKursBeginn\", " +
		  "k.\"dtmKursEnde\", " +
		  "k.\"dtmKursScheinanmeldungVon\", " +
		  "k.\"dtmKursScheinanmeldungBis\", " +
		  "d.\"lngSdSeminarID\", " +
		  "k.\"lngSdSeminarID\", " +
		  "x.*, " +
		  "CASE WHEN ((k.\"dtmKursScheinanmeldungVon\"<=CURRENT_DATE AND k.\"dtmKursScheinanmeldungBis\">=CURRENT_DATE)) THEN 't'::bool ELSE 'f'::bool END AS \"blnAllowChanges\" " +
		"FROM \"tblSdDozent\" d, \"tblBdKurs\" k, \"tblSdLeistung\" l, \"tblBdStudentXLeistung\" x " +
		"WHERE (" +
		  "(l.\"lngSdSeminarID\"=" + this.getSeminarID() + ") AND " +
		  "(x.\"lngSdSeminarID\"= l.\"lngSdSeminarID\") AND " +
		  "(k.\"lngSdSeminarID\"= x.\"lngSdSeminarID\") AND " +
		  "(d.\"lngSdSeminarID\"= k.\"lngSdSeminarID\") AND " +
		  "(x.\"strMatrikelnummer\"='" + getDBCleanString( this.getMatrikelnummer() ) + "') AND " +
		  "(l.\"lngLeistungID\" = x.\"lngLeistungsID\") AND " +
		  "(k.\"lngKursID\" = x.\"lngKlausuranmeldungKursID\") AND " +
		  "(d.\"lngDozentID\" = k.\"lngDozentID\")  AND " +
		  "(x.\"blnStudentLeistungValidiert\"=" + getDBBoolRepresentation(false) + ") AND " +
		  "(x.\"blnStudentLeistungKlausuranmeldung\"=" + getDBBoolRepresentation(true) + ") " +
		") " +
		"UNION SELECT " +
		  "l2.*, " +
		  "'', " +
		  "'', " +
		  "x2.\"strStudentLeistungAussteller\", " +
		  "x2.\"lngKlausuranmeldungKursID\", " +
		  "'f' as \"blnKursScheinanmeldungErlaubt\", " +
		  "x2.\"strSLKursTag\", " +
		  "x2.\"dtmSLKursBeginn\", " +
		  "x2.\"dtmSLKursEnde\", " +
		  "x2.\"dtmSLKursScheinanmeldungVon\", " +
		  "x2.\"dtmSLKursScheinanmeldungBis\", " +
		  "x2.\"lngSdSeminarID\", " +
		  "x2.\"lngSdSeminarID\", " +
		  "x2.*, " +
		  "'f'::bool AS \"blnAllowChanges\" " +
		"FROM \"tblSdLeistung\" l2, \"tblBdStudentXLeistung\" x2 WHERE (" +
			"(x2.\"lngSdSeminarID\"= " + getSeminarID() + ") AND " +
		 	"(x2.\"lngSdSeminarID\"= l2.\"lngSdSeminarID\") AND " +
			"(x2.\"lngLeistungsID\" = l2.\"lngLeistungID\") AND " +
			"(x2.\"strMatrikelnummer\"='" + getDBCleanString( this.getMatrikelnummer() ) + "') AND " +
			"(x2.\"blnStudentLeistungValidiert\"=" + getDBBoolRepresentation(false) + ") AND " +
			"(x2.\"blnStudentLeistungKlausuranmeldung\"=" + getDBBoolRepresentation(true) + ") AND " +
			"NOT EXISTS (" +
				"SELECT * FROM \"tblBdKurs\" kurs where (" +
						"kurs.\"lngSdSeminarID\"=x2.\"lngSdSeminarID\" AND " +
						"kurs.\"lngKursID\" = x2.\"lngKlausuranmeldungKursID\"" +
		        ")" + 
		    ")" +
		") " +
		"ORDER BY \"lngLeistungsID\" ASC;");
	}



	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 	6.   UTILITY
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------

	/**
	 * A student may have up to 6 subjects, in table 'tblBdStudent' these are called intStudentFach1, intStudentFach2, ... , intStudentFach6.
	 * On the other hand, subjects are mapped to seminars (an n:m-mapping). This method finds out what's the current student's subject relevant
	 * to the given seminar (identified through 'lngSeminarID'). The result is handed over.
	 * @return FachID of current student at seminar specified through 'lngSeminarID.'
	 * @param lngSeminarID: ID of seminar whose subjects to look up.
	 **/
	public int getFachID(long lngSeminarID){
		// only look it up if it has not
		// already been calculated.
		if(m_iStudentFachID==-1){
		
			int iCount=0;
			String strUnionQuery="";
	
			// build query String (a 6-fold union query)
			for(iCount=1;iCount<=6;iCount++){
				if(iCount!=1) strUnionQuery+="  UNION  ";
	
				strUnionQuery += "SELECT " +
					  "x.\"lngSeminarID\", " +
					  "s.\"strMatrikelnummer\", " +
					  "s.\"intStudentFach" + iCount + "\" as \"Fach\" " +
					"FROM \"tblSdSeminarXFach\" x, \"tblBdStudent\" s " +
					"WHERE " +
					  "(" +
					   "(x.\"lngSeminarID\"=" + lngSeminarID + ") AND " +
					   "(s.\"strMatrikelnummer\"='" + this.getMatrikelnummer() + "') AND " +
					   "(s.\"intStudentFach" + iCount + "\" = x.\"intFachID\")" +
					  ")";
			}
			strUnionQuery+=";";
	
			// retrieve result of this query:
			ResultSet rst=null;
			try{
			  rst=sqlQuery(strUnionQuery);
			  rst.next();
			  m_iStudentFachID=rst.getInt("Fach");
			  rst.close();
	
			}catch(Exception eGetFachID){
				try{rst.close();}catch(Exception eNested){}
			}
		}		
		return m_iStudentFachID;
	}
	
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 	7.   D E P R E C A T E D   M E T H O D S
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	/**
	 * Adds given credit to the list of credits of this student, if this seems okay.
	 * "Seeming okay" means:
	 * @param s: Details of credit to be added.
	 * @deprecated: seems unused anyway.
	 * ### #hack ### this is unused (Oct 25, 2003)
	 **/
	public void addCredit(StudentXLeistung s) throws Exception{
		s.add();
	}	
	
	/**
	 * This student's sensible exam options for exams with a given name at a given seminar.
	 * @return ResultSet with exam-options of this student. All exams with name 'strPruefungBezeichnung' are displayed,
	 * if they are the student's 'Fach' (subject) and seminar, and if the student has not yet an exam of this sort (or applied to get one).
	 * Presupposes that a SeminarID has been set.
	 * #hack: not quite sure why a seminarID has to be handed over, here.
	 * @deprecated since version 5-27-10: not sure what this is at all. Please dont use.
	 * @param strPruefungBezeichnung: name of the exam.
	 * @throws Exception if connection to db is erroneous.
	 **/
	protected ResultSet StudentExamOptions(long lngSeminarID, String strPruefungBezeichnung) throws Exception{
		return sqlQuery("SELECT " +
			  "x.\"intFachID\", " +
			  "x.\"lngSdSeminarID\", " +
			  "p.\"lngSdSeminarID\", " +
			  "p.\"lngPruefungID\", " +
			  "p.\"strPruefungBezeichnung\", " +
			  "p.\"strPruefungsordnung\", " +
			  "p.\"strPruefungAbschluss\", " +
			  "p.\"strPruefungBeschreibung\" " +
			"FROM \"tblSdPruefung\" p, \"tblSdPruefungXFach\" x " +
			"WHERE " +
			  "(" +
			   "(p.\"lngPruefungID\" = x.\"lngPruefungID\") AND " +
			   "(p.\"lngSdSeminarID\" = x.\"lngSdSeminarID\") AND " +
			   "(x.\"intFachID\"=" + this.getFachID(lngSeminarID) + ") AND " +
			   "(x.\"lngSdSeminarID\"=" + this.getSeminarID() + ") AND " +
			   "(p.\"lngSdSeminarID\"=" + this.getSeminarID() + ") AND " +
			   "(p.\"strPruefungBezeichnung\"='" + getDBCleanString( strPruefungBezeichnung ) + "') AND " +
			   "NOT EXISTS (" +
				 "SELECT \"lngSdSeminarID\", \"lngSdPruefungsID\", \"strMatrikelnummer\" " +
				 "FROM \"tblBdStudentXPruefung\" WHERE ((\"strMatrikelnummer\"='" + getDBCleanString( this.getMatrikelnummer() ) + "') AND (\"lngSdSeminarID\"=" + lngSeminarID + ") AND (\"lngSdPruefungsID\"=p.\"lngPruefungID\"))" +
			   ")" +
			  ")" +
			  "ORDER BY p.\"lngPruefungID\" ASC;");
	}
	
	/**
	 * All credits (name, description, id) that this student lacks for the given exam (PruefungsID)
	 * in this seminar (SeminarID). In order to be counted, credits must be 'passed.' It makes no difference if they are
	 * 'validated' or not, however. Since applied-for credits are NOT validated, but do not count as 'missing' either.
	 * @return ResultSet with all credits (name, description, id) that this student lacks for the given exam (PruefungsID)
	 * in this seminar (SeminarID).
	 * #hack: better not use this anymore. I dont remember how well it works with
	 * modular exams, and in addition to that, the wrong flag 'bestanden' is checked here: the one in tblStudentXLeistung.
	 * @deprecated since version 5-27-10. This method picks missing credits using Pr???fungen (instead of modules).
	 * @param lngSeminarID: seminar whose exam to look up,
	 * @param lngPruefungsID: exam to look up.
	 * @throws Exception if connection to db is erroneous, or when this is a modular exam!
	 **/
	protected ResultSet StudentMissingCredits(long lngSeminarID, long lngPruefungsID) throws Exception{

		PruefungData pd = new PruefungData(lngSeminarID, lngPruefungsID);
		if(pd.isModular()) throw new Exception(String.valueOf(ERR_MISSING_CREDITS_ON_MOD_EXAM));

		return sqlQuery("SELECT " +
				  "l.* " +
				"FROM \"tblSdPruefung\" p, \"tblSdPruefungXModul\" pxm, " +
				"\"tblSdModulXLeistung\" mxl, \"tblSdLeistung\" l " +
				"WHERE " +
				 "(" +
				  "(p.\"lngPruefungID\"   	= pxm.\"lngPruefungID\"	) AND " +
				  "(p.\"lngSdSeminarID\" 	= pxm.\"lngSdSeminarID\"	) AND " +
				  "(pxm.\"lngModulID\" 		= mxl.\"lngModulID\"	) AND " +
				  "(pxm.\"lngSdSeminarID\"	= mxl.\"lngSdSeminarID\"	) AND " +
				  "(mxl.\"lngLeistungID\" 	= l.\"lngLeistungID\"	) AND " +
				  "(mxl.\"lngSdSeminarID\"	= l.\"lngSdSeminarID\"	) AND " +
				  "(p.\"lngSdSeminarID\"	= " + lngSeminarID + ") AND " +
						"NOT EXISTS (" +
						  "SELECT \"lngSdSeminarID\", \"strMatrikelnummer\", \"lngLeistungsID\", " +
						  	   "\"blnStudentLeistungBestanden\", \"blnStudentLeistungValidiert\" " +
						  	   "FROM \"tblBdStudentXLeistung\" " +
						  	   "WHERE (" +
								   "(\"lngSdSeminarID\"=" + lngSeminarID + ") AND " +
								   "(\"strMatrikelnummer\"='" + getDBCleanString( this.getMatrikelnummer() ) + "') AND " +
								   "(\"lngLeistungsID\"=l.\"lngLeistungID\") AND " +
								   "(\"blnStudentLeistungBestanden\"='t'::bool)) " +
						") AND " +
				  "(p.\"lngPruefungID\"	=" + lngPruefungsID + ")" +
				 ") " +
				"ORDER BY mxl.\"lngLeistungID\" ASC;");
	}

	/**
	 * Resets this student's password to the Matrikelnummer and 
	 * updates the record in the database.
	 * @throws Exception if update fails.
	 */
	public void resetPassword() throws Exception {
		setStudentPasswort( getMatrikelnummer() );
		update();
	}

	/**
	 * Sets this student's 'Ziel' to Bachelor (m_ZUV_ZIEL_BACHELOR1, to 
	 * be precise). Be careful to use this, because exam-reporting may 
	 * be more difficult if the current 'goals' of students differ 
	 * from the ones in the central administration.
	 * @throws Exception if update fails
	 */
	public void bachelorize() throws Exception {
		setStudentZUVZiel( m_ZUV_ZIEL_BACHELOR1 );
		update();
	}
	
	/**
	 * Sets this student's 'Ziel' to Master (m_ZUV_ZIEL_MASTER, to 
	 * be precise). Be careful to use this, because exam-reporting may 
	 * be more difficult if the current 'goals' of students differ 
	 * from the ones in the central administration.
	 * @throws Exception if update fails
	 */
	public void masterize() throws Exception {
		setStudentZUVZiel( m_ZUV_ZIEL_MASTER );
		update();
	}	

	/**
	 * @return "Bachelor", "Master", "Magister", "Staatsex.", "Promotion" oder "Erasmus"
	 */
	public String getZUVZielExplain(){
		if(isBachelor()) return "Bachelor";
		if(isMaster()) 	 return "Master";
		if(isMagister()) return "Magister";
		if(isLehramt())	 return "Staatsex.";
		if(isPromotion())	 return "Promotion";
		if(isErasmus())	 return "Erasmus";
		return "unbekannt: " + getStudentZUVZiel();
	}
	/**
	 * Being immatriculated as a bachelor-student is reflected by 
	 * <Code>tblBdStudent.lngStudentZUVZiel</Code>, which initially 
	 * contains the ZUV (Central University's Administration) 
	 * idiosynchratic values: 182 or 282 for Bachelor, or rather 
	 * <Code>m_ZUV_ZIEL_BACHELOR1</Code> and <Code>m_ZUV_ZIEL_BACHELOR2</Code>.
	 * Use .bachelorize to change that value. 
	 * @return true, if this student is immatriculated as bachelor student.
	 */
	public boolean isBachelor() {
		return ((this.getStudentZUVZiel()==m_ZUV_ZIEL_BACHELOR1) ||
				(this.getStudentZUVZiel()==m_ZUV_ZIEL_BACHELOR2));
	}
	
	/**
	 * Being immatriculated as a master-student is reflected by 
	 * <Code>tblBdStudent.lngStudentZUVZiel</Code>, which initially 
	 * contains the ZUV (Central University's Administration) 
	 * idiosynchratic values: 188 for Master, or rather 
	 * <Code>m_ZUV_ZIEL_MASTER</Code>.
	 * Use .masterize to change that value. 
	 * @return true, if this student is immatriculated as master student.
	 */
	public boolean isMaster() {
		return (this.getStudentZUVZiel()==m_ZUV_ZIEL_MASTER);
	}	

	/**
	 * Being immatriculated as a Erasmus-student is reflected by 
	 * <Code>tblBdStudent.lngStudentZUVZiel</Code>, which initially 
	 * contains the ZUV (Central University's Administration) 
	 * idiosynchratic values: 2 for "Abschluss im Ausland", or rather 
	 * <Code>m_ZUV_ZIEL_ERASMUS</Code>.
	 * @return true, if this student is immatriculated as master student.
	 */	
	public boolean isErasmus(){
		return (this.getStudentZUVZiel()==m_ZUV_ZIEL_ERASMUS );
	}

	/**
	 * Being immatriculated as a magister-student is reflected by 
	 * <Code>tblBdStudent.lngStudentZUVZiel</Code>, which initially 
	 * contains the ZUV (Central University's Administration) 
	 * idiosynchratic values:  
	 * <Code>m_ZUV_ZIEL_MAGISTER</Code>.
	 * @return true, if this student is immatriculated as master student.
	 */
	public boolean isMagister() {
		return (this.getStudentZUVZiel()==m_ZUV_ZIEL_MAGISTER);
	}

	/**
	 * Being immatriculated as a Staasexamens/Lehramt-student (German idiosyncasy) is reflected by 
	 * <Code>tblBdStudent.lngStudentZUVZiel</Code>, which initially 
	 * contains the ZUV (Central University's Administration) 
	 * idiosynchratic values:  
	 * <Code>m_ZUV_ZIEL_MAGISTER</Code>.
	 * @return true, if this student is immatriculated as master student.
	 */
	public boolean isLehramt() {
		return (this.getStudentZUVZiel()==m_ZUV_ZIEL_LEHRAMT);
	}

	/**
	 * Being immatriculated as a PhD-student is reflected by 
	 * <Code>tblBdStudent.lngStudentZUVZiel</Code>, which initially 
	 * contains the ZUV (Central University's Administration) 
	 * idiosynchratic values:  
	 * <Code>m_ZUV_ZIEL_PROMOTION</Code>.
	 * @return true, if this student is immatriculated as master student.
	 */
	public boolean isPromotion() {
		return (this.getStudentZUVZiel()==m_ZUV_ZIEL_PROMOTION);
	}
	
	/**
	 * Delete this student's exam in currently set Seminar.
	 * @param lngPruefungID
	 * @param intPruefungCount
	 * @throws Exception ERR_EXAM_DELETION_FAILED
	 */
	public void deleteExam(long lngPruefungID, int intPruefungCount) throws Exception {
		if(!sqlExe("Delete from \"tblBdStudentPruefungDetail\" where " +
		"(" +
		  "\"lngSdSeminarID\"= " + this.getSeminarID() + " AND " +
		  "\"lngSdPruefungsID\"= " + lngPruefungID + " AND " +
		  "\"strMatrikelnummer\"='" + this.getMatrikelnummer() + "' AND " +
		  "\"intStudentPruefungCount\"= " + intPruefungCount + 
		 ");" +
		 "Delete from \"tblBdStudentXPruefung\" where " + 
		"(" +
		  "\"lngSdSeminarID\"= " + this.getSeminarID() + " AND " +
		  "\"lngSdPruefungsID\"= " + lngPruefungID + " AND " +
		  "\"strMatrikelnummer\"='" + this.getMatrikelnummer() + "' AND " +
		  "\"intStudentPruefungCount\"= " + intPruefungCount + 
		 ");")) throw new Exception (String.valueOf(ERR_EXAM_DELETION_FAILED));
	}


	/**
	 * @param uRZPassword The uRZPassword to set.
	 */
	public void setURZPassword(String uRZPassword) {
		m_strURZPassword = uRZPassword;
	}


	/**
	 * @return Returns the uRZPassword.
	 */
	public String getURZPassword() {
		return m_strURZPassword;
	}


	
}