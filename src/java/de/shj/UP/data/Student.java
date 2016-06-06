
/*
 * shj-online, Mannheim, Germany: 2009
 *
 * Copyright (c) 2009 shj-online
 * 
 * http://www.shj-online.de/
 * mailto:heiko.jakubzik@shj-online.de
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
 * AUFBAU
 *
 * @version 7-00-00 (auto coded)
 * change date              change author           change description
 * ===================================================================
 * version 7-00	            
 *
 * Oct-2009		    h. jakubzik             autoclass
 * Jun-2016                 h. jakubzik             login für OS
 */

package de.shj.UP.data;

import de.shj.UP.util.PasswordHash;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import javax.naming.NamingException;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import org.w3c.dom.Node; 

/**
 *  Modules: SignUp, Exam, Mafo
Table/Class that holds information on students.
 *  
 *  ALLGEMEINE INFORMATION ZU DEN OBJEKTEN DIESES PACKAGES:
 *  Diese Klasse kapselt die Tabelle 'Student' in 
 *  der Datenbankversion 7-00-00 (Oktober 2009). 
 *  Jede Tabellenspalte entspricht einem Feld dieses Objekt (mit Gettern und Settern).
 * 
 *  Es gibt Konstruktoren für 
 *  - ein leeres Objekt (Bean)
 *  - ein Objekt mit Inhalt einer Tabellenzeile aus der Datenbank
 *    -- Konstruktion über Indexwerte
 *    -- Konstruktion per Übergabe eines Recordsets (protected initByRst())
 *  - ein Objekt mit Inhalten aus einer Xml-Datei füllen.
 * 
 *  Die Hauptmethoden sind {@link {@link #add()}, {@link {@link #update()} und {@link #delete()}.
 * 
 *  Es gibt eine kanonische Methode {@link #isDirty()} nach folgenden Regeln:
 * 
 *  1) Nach Konstruktion und im Zweifel ist {@link #isDirty()} == false;
 *  2) Nachdem per Setter eine Eigentschaft verändert (!) wurde, ist das Objekt 'dirty'
 *  3) Nach einem fehlgeschlagenen Aufruf von {@link #update()} ist das Objekt 'dirty'
 *  4) Nach einem fehlgeschlagenen Aufruf von {@link #add()} ist das Objekt 'dirty'
 *  5) Nach einem Aufruf von {@link #delete()} (erfolgreich oder nicht) ist das Objekt 'dirty'
 *  6) Nach einem erfolgreichen Aufruf von {@link #update()} ist {@link #isDirty()} == false;
 *  7) Nach einem erfolgreichen Aufruf von {@link #add()} ist {@link #isDirty()} == false;
 *  8) Alle anderen Methoden wirken sich nicht auf {@link #isDirty()} aus, solange sie keine Eigenschaften des Objekts ändern
 *  Beachten Sie bitte, dass {@link #update()} und {@link #add()} nur ausgeführt werden, wenn {@link #isDirty()} == true.
 *  Beachten Sie bitte auch, dass dieses Objekt sowohl unbenutzt als auch ungetestet sein mag;  
 *  es wird automatisch aus dem Datenmodell der Anwendung erzeugt.
 * 
 **/
public class Student extends shjCore{

	private static final long serialVersionUID = 7L;

////////////////////////////////////////////////////////////////
// 1.   P R I V A T E   D E K L A R A T I O N E N
////////////////////////////////////////////////////////////////
	
	private boolean m_bIsDirty = false;

	private long m_lStudentPID;
	private String m_sMatrikelnummer;
	private String m_sStudentVorname;
	private String m_sStudentNachname;
	private String m_sStudentGeburtsname;
	private int m_iStudentSemester;
	private String m_sStudentPasswort;
	private boolean m_bStudentLoggedIn;
	private Date m_dStudentGeburtstag;
	private String m_sStudentGeburtsort;
	private String m_sStudentStaat;
	private long m_lStudentZUVZiel;
	private String m_sStudentStrasse;
	private String m_sStudentPLZ;
	private String m_sStudentOrt;
	private String m_sStudentEmail;
	private boolean m_bStudentPublishEmail;
	private String m_sStudentTelefon;
	private String m_sStudentHandy;
	private boolean m_bStudentPublishHandy;
	private String m_sStudentHomepage;
	private boolean m_bStudentPublishHomepage;
	private String m_sStudentInterests;
	private boolean m_bStudentVisible;
	private String m_tStudentLastLogin;
	private String m_tStudentLastLogout;
	private Date m_dStudentZUVAdd;
	private Date m_dStudentZUVUpdate;
	private Date m_dStudentZUVImmatrikuliert;
	private String m_sStudentHf1;
	private String m_sStudentHf2;
	private boolean m_bStudentFemale;
	private long m_lStudentRandom;
	private String m_sStudentZUVFach;
	private int m_iStudentFach1;
	private int m_iStudentFach2;
	private int m_iStudentFach3;
	private int m_iStudentFach4;
	private int m_iStudentFach5;
	private int m_iStudentFach6;
	private int m_iStudentFachsemester1;
	private int m_iStudentFachsemester2;
	private int m_iStudentFachsemester3;
	private int m_iStudentFachsemester4;
	private int m_iStudentFachsemester5;
	private int m_iStudentFachsemester6;
	private String m_sStudentBemerkung;
	private boolean m_bStudentSelected;
	private String m_sStudentUrlaub;
	private String	m_strURZPassword	= "";
	public boolean m_bLoginStateInternalErr=false; // 1=INTERNAL ERROR

////////////////////////////////////////////////////////////////
// 2.   Ö F F E N T L I C H E  E I G E N S C H A F T E N
////////////////////////////////////////////////////////////////

 	/**<pre>
 	 * The URZ-Password is set on loginURZ (@see #loginURZ(String, String, String, long)).
 	 * </pre>
 	 * @param URZPassword The URZPassword to set.
 	 */
 	private void setURZPassword(String uRZPassword) {
 		m_strURZPassword = uRZPassword;
 	}
 
 
 	/**
 	 * <pre>
 	 * The URZ-Password is set on loginURZ (@see #loginURZ(String, String, String, long)).
 	 * It is *not*, however, stored persistently in the SignUp database.
 	 * This property is only kept for a session, so as to be able 
 	 * to confirm login without accessing the URZ-server every time.
 	 * </pre>
 	 * @return Returns the URZPassword.
 	 */
 	public String getURZPassword() {
 		return m_strURZPassword;
 	}

 
 	/**<pre>
 	 * method checks if a student is registered under this name and matriculation, with this password,
 	 * and in this institute/seminar. Returns boolean true if this is so, false if not or an error occurred.
 	 * In case the method works fine, the Student object is initialized. The student's sessionID is always
 	 * updated using this method, as it assumes to be a _new_ login.
 	 * </pre>
 	 * @param strMatrikel matriculation number of student to log in
 	 * @param strName last name of student to log in
 	 * @param strPwd this student's password (as stored in SignUp)
 	 * @param lngSeminarID id of institute or seminar.
 	 * @return true if login okay and complete. False otherwise, i.e. if password or name wrong, or if an error occurred.
 	 */
 	public boolean login(String strMatrikel, String strName, String strPwd, long lngSeminarID){
 
 		boolean blnReturn=false;
 		ResultSet rst=null;
 
 		String strSQL =
 			"SELECT " +
 			   "b.*, " +
 				"x.\"lngSeminarID\", " +
 				"x.\"intFachID\" " +
 
 			"FROM \"tblBdStudent\" b, \"tblSdSeminarXFach\" x " +
 			"WHERE " +
 			 "(" +
 			   "b.\"strMatrikelnummer\"='" + strMatrikel + "' AND " +
 			   "b.\"strStudentNachname\"='" + strName + "' AND " +
 			   "b.\"strStudentPasswort\"='" + strPwd + "' AND " +
 			   "x.\"lngSeminarID\"=" + lngSeminarID + " AND " +
 			   "(" +
 				 "b.\"intStudentFach1\"=x.\"intFachID\" OR " +
 				 "b.\"intStudentFach2\"=x.\"intFachID\" OR " +
 				 "b.\"intStudentFach3\"=x.\"intFachID\" OR " +
 				 "b.\"intStudentFach4\"=x.\"intFachID\" OR " +
 				 "b.\"intStudentFach5\"=x.\"intFachID\" OR " +
 				 "b.\"intStudentFach6\"=x.\"intFachID\" " +
 			   ")" +
 			 ");";
 
 		try{
 			rst=sqlQuery(strSQL);
 			if(rst.next()){
 
 				initByRst(rst);
 				//rst.close();
 
 				// update SessionID 'lngRandom' in 'tblBdStudent' in database:
 				// long lngSessionID=Math.round((Math.random()*2147483647));
 				strSQL = "UPDATE \"tblBdStudent\" ";
 				strSQL+= "SET \"dtmStudentLastLogin\" = CURRENT_TIMESTAMP ";
 				// strSQL+= "\"lngStudentRandom\" = " + lngSessionID +" ";
 				strSQL+= "WHERE (\"lngStudentPID\"=" + this.getStudentPID() + ");";
 				sqlExe(strSQL);
 				// this.setStudentRandom(lngSessionID);
 
 
 				blnReturn=true;
 				}
 			rst.close();
 			}catch(Exception eLogin){
 				m_bLoginStateInternalErr=true;
 				blnReturn=false;
 		}
 		// new: March 1, 2010
 		try{this.disconnect();}catch(Exception e){}
 		return blnReturn;
 	}
	
	/**
 	 * Wahr, wenn sich das Objekt von der Datenbanktabellenzeile unterscheidet.
	 * @return: Urteil, ob das Objekt noch mit der Datenbanktabellenzeile identisch ist.
	 **/
	public boolean isDirty(){
	  return this.m_bIsDirty;
	}


	/**
	 * Identifier used internally.
	 * @return Identifier used internally.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.lngStudentPID</Code>
	 **/
	public long getStudentPID(){
		return this.m_lStudentPID;
	}

	/**
	 * Identifier used internally.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.lngStudentPID</Code>
	 **/	
	public void setStudentPID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lStudentPID));
		this.m_lStudentPID=value;
	}
	

	/**
	 * Registration number of this student. Must have seven digits, currently.
	 * @return Registration number of this student. Must have seven digits, currently.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.strMatrikelnummer</Code>
	 **/
	public String getMatrikelnummer(){
		return this.m_sMatrikelnummer;
	}

	/**
	 * Registration number of this student. Must have seven digits, currently.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.strMatrikelnummer</Code>
	 **/	
	public void setMatrikelnummer(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sMatrikelnummer)));
		this.m_sMatrikelnummer=value;
	}
	

	/**
	 * Student"s first name.
	 * @return Student"s first name.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.strStudentVorname</Code>
	 **/
	public String getStudentVorname(){
		return this.m_sStudentVorname;
	}

	/**
	 * Student"s first name.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.strStudentVorname</Code>
	 **/	
	public void setStudentVorname(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sStudentVorname)));
		this.m_sStudentVorname=value;
	}
	

	/**
	 * Student"s last name.
	 * @return Student"s last name.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.strStudentNachname</Code>
	 **/
	public String getStudentNachname(){
		return this.m_sStudentNachname;
	}

	/**
	 * Student"s last name.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.strStudentNachname</Code>
	 **/	
	public void setStudentNachname(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sStudentNachname)));
		this.m_sStudentNachname=value;
	}
	

	/**
	 * Maiden name of this student, if any. (Otherwise empty or null; new in version 6-06).
	 * @return Maiden name of this student, if any. (Otherwise empty or null; new in version 6-06).
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.strStudentGeburtsname</Code>
	 **/
	public String getStudentGeburtsname(){
		return this.m_sStudentGeburtsname;
	}

	/**
	 * Maiden name of this student, if any. (Otherwise empty or null; new in version 6-06).
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.strStudentGeburtsname</Code>
	 **/	
	public void setStudentGeburtsname(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sStudentGeburtsname)));
		this.m_sStudentGeburtsname=value;
	}
	

	/**
	 * @Deprecated. Semester of student. (But in what subject? Use the intFachsemester-properties below instead).
	 * @return @Deprecated. Semester of student. (But in what subject? Use the intFachsemester-properties below instead).
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.intStudentSemester</Code>
	 **/
	public int getStudentSemester(){
		return this.m_iStudentSemester;
	}

	/**
	 * @Deprecated. Semester of student. (But in what subject? Use the intFachsemester-properties below instead).
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.intStudentSemester</Code>
	 **/	
	public void setStudentSemester(int value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_iStudentSemester));
		this.m_iStudentSemester=value;
	}
	

	/**
	 * Student"s password to access SignUp.
	 * @return Student"s password to access SignUp.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.strStudentPasswort</Code>
	 **/
	public String getStudentPasswort(){
		return this.m_sStudentPasswort;
	}

	/**
	 * Student"s password to access SignUp.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.strStudentPasswort</Code>
	 **/	
	public void setStudentPasswort(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sStudentPasswort)));
		this.m_sStudentPasswort=value;
	}
	

	/**
	 * Is this student currently logged in? @Deprecated. Information is of no use.
	 * @return Is this student currently logged in? @Deprecated. Information is of no use.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.blnStudentLoggedIn</Code>
	 **/
	public boolean getStudentLoggedIn(){
		return this.m_bStudentLoggedIn;
	}

	/**
	 * Is this student currently logged in? @Deprecated. Information is of no use.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.blnStudentLoggedIn</Code>
	 **/	
	public void setStudentLoggedIn(boolean value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_bStudentLoggedIn));
		this.m_bStudentLoggedIn=value;
	}
	

	/**
	 * Date of birth.
	 * @return Date of birth.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.dtmStudentGeburtstag</Code>
	 **/
	public Date getStudentGeburtstag(){
		return this.m_dStudentGeburtstag;
	}

	/**
	 * Date of birth.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.dtmStudentGeburtstag</Code>
	 **/	
	public void setStudentGeburtstag(Date value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_dStudentGeburtstag));
		this.m_dStudentGeburtstag=value;
	}
	

	/**
	 * Place of birth.
	 * @return Place of birth.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.strStudentGeburtsort</Code>
	 **/
	public String getStudentGeburtsort(){
		return this.m_sStudentGeburtsort;
	}

	/**
	 * Place of birth.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.strStudentGeburtsort</Code>
	 **/	
	public void setStudentGeburtsort(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sStudentGeburtsort)));
		this.m_sStudentGeburtsort=value;
	}
	

	/**
	 * Nationality. Refers to column SAGH (Staatsangehgkt) in ZUV database It is clear, German text.
	 * @return Nationality. Refers to column SAGH (Staatsangehgkt) in ZUV database It is clear, German text.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.strStudentStaat</Code>
	 **/
	public String getStudentStaat(){
		return this.m_sStudentStaat;
	}

	/**
	 * Nationality. Refers to column SAGH (Staatsangehgkt) in ZUV database It is clear, German text.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.strStudentStaat</Code>
	 **/	
	public void setStudentStaat(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sStudentStaat)));
		this.m_sStudentStaat=value;
	}
	

	/**
	 * What graduation (Bachelor, Master, Magister, PhD etc.) is this student heading for? This is currently a ZUV-encoded number, see Heidelberg ZUV for a documentation. Maybe future versions of SignUp should have their own Stammdatentabelle for this?
	 * @return What graduation (Bachelor, Master, Magister, PhD etc.) is this student heading for? This is currently a ZUV-encoded number, see Heidelberg ZUV for a documentation. Maybe future versions of SignUp should have their own Stammdatentabelle for this?
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.lngStudentZUVZiel</Code>
	 **/
	public long getStudentZUVZiel(){
		return this.m_lStudentZUVZiel;
	}

	/**
	 * What graduation (Bachelor, Master, Magister, PhD etc.) is this student heading for? This is currently a ZUV-encoded number, see Heidelberg ZUV for a documentation. Maybe future versions of SignUp should have their own Stammdatentabelle for this?
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.lngStudentZUVZiel</Code>
	 **/	
	public void setStudentZUVZiel(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lStudentZUVZiel));
		this.m_lStudentZUVZiel=value;
	}
	

	/**
	 * Address: student"s street.
	 * @return Address: student"s street.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.strStudentStrasse</Code>
	 **/
	public String getStudentStrasse(){
		return this.m_sStudentStrasse;
	}

	/**
	 * Address: student"s street.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.strStudentStrasse</Code>
	 **/	
	public void setStudentStrasse(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sStudentStrasse)));
		this.m_sStudentStrasse=value;
	}
	

	/**
	 * Address: student"s zip-code.
	 * @return Address: student"s zip-code.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.strStudentPLZ</Code>
	 **/
	public String getStudentPLZ(){
		return this.m_sStudentPLZ;
	}

	/**
	 * Address: student"s zip-code.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.strStudentPLZ</Code>
	 **/	
	public void setStudentPLZ(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sStudentPLZ)));
		this.m_sStudentPLZ=value;
	}
	

	/**
	 * Address: student"s place of residence.
	 * @return Address: student"s place of residence.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.strStudentOrt</Code>
	 **/
	public String getStudentOrt(){
		return this.m_sStudentOrt;
	}

	/**
	 * Address: student"s place of residence.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.strStudentOrt</Code>
	 **/	
	public void setStudentOrt(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sStudentOrt)));
		this.m_sStudentOrt=value;
	}
	

	/**
	 * student"s email address.
	 * @return student"s email address.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.strStudentEmail</Code>
	 **/
	public String getStudentEmail(){
		return this.m_sStudentEmail;
	}

	/**
	 * student"s email address.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.strStudentEmail</Code>
	 **/	
	public void setStudentEmail(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sStudentEmail)));
		this.m_sStudentEmail=value;
	}
	

	/**
	 * Is the student"s email address visible for other students? )This feature can only be edited by the student).
	 * @return Is the student"s email address visible for other students? )This feature can only be edited by the student).
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.blnStudentPublishEmail</Code>
	 **/
	public boolean getStudentPublishEmail(){
		return this.m_bStudentPublishEmail;
	}

	/**
	 * Is the student"s email address visible for other students? )This feature can only be edited by the student).
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.blnStudentPublishEmail</Code>
	 **/	
	public void setStudentPublishEmail(boolean value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_bStudentPublishEmail));
		this.m_bStudentPublishEmail=value;
	}
	

	/**
	 * Student"s phone number.
	 * @return Student"s phone number.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.strStudentTelefon</Code>
	 **/
	public String getStudentTelefon(){
		return this.m_sStudentTelefon;
	}

	/**
	 * Student"s phone number.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.strStudentTelefon</Code>
	 **/	
	public void setStudentTelefon(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sStudentTelefon)));
		this.m_sStudentTelefon=value;
	}
	

	/**
	 * Student"s cell-phone number.
	 * @return Student"s cell-phone number.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.strStudentHandy</Code>
	 **/
	public String getStudentHandy(){
		return this.m_sStudentHandy;
	}

	/**
	 * Student"s cell-phone number.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.strStudentHandy</Code>
	 **/	
	public void setStudentHandy(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sStudentHandy)));
		this.m_sStudentHandy=value;
	}
	

	/**
	 * Is this student"s cell-phone number visible to other students?
	 * @return Is this student"s cell-phone number visible to other students?
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.blnStudentPublishHandy</Code>
	 **/
	public boolean getStudentPublishHandy(){
		return this.m_bStudentPublishHandy;
	}

	/**
	 * Is this student"s cell-phone number visible to other students?
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.blnStudentPublishHandy</Code>
	 **/	
	public void setStudentPublishHandy(boolean value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_bStudentPublishHandy));
		this.m_bStudentPublishHandy=value;
	}
	

	/**
	 * Student"s homepage.
	 * @return Student"s homepage.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.strStudentHomepage</Code>
	 **/
	public String getStudentHomepage(){
		return this.m_sStudentHomepage;
	}

	/**
	 * Student"s homepage.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.strStudentHomepage</Code>
	 **/	
	public void setStudentHomepage(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sStudentHomepage)));
		this.m_sStudentHomepage=value;
	}
	

	/**
	 * Is this student"s homepage visible to other students?
	 * @return Is this student"s homepage visible to other students?
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.blnStudentPublishHomepage</Code>
	 **/
	public boolean getStudentPublishHomepage(){
		return this.m_bStudentPublishHomepage;
	}

	/**
	 * Is this student"s homepage visible to other students?
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.blnStudentPublishHomepage</Code>
	 **/	
	public void setStudentPublishHomepage(boolean value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_bStudentPublishHomepage));
		this.m_bStudentPublishHomepage=value;
	}
	

	/**
	 * Comma-separated list of interest of this student.
	 * @return Comma-separated list of interest of this student.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.strStudentInterests</Code>
	 **/
	public String getStudentInterests(){
		return this.m_sStudentInterests;
	}

	/**
	 * Comma-separated list of interest of this student.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.strStudentInterests</Code>
	 **/	
	public void setStudentInterests(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sStudentInterests)));
		this.m_sStudentInterests=value;
	}
	

	/**
	 * Are these interests searchable for other students?
	 * @return Are these interests searchable for other students?
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.blnStudentVisible</Code>
	 **/
	public boolean getStudentVisible(){
		return this.m_bStudentVisible;
	}

	/**
	 * Are these interests searchable for other students?
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.blnStudentVisible</Code>
	 **/	
	public void setStudentVisible(boolean value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_bStudentVisible));
		this.m_bStudentVisible=value;
	}
	

	/**
	 * When did this student last log in?
	 * @return When did this student last log in?
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.dtmStudentLastLogin</Code>
	 **/
	public String getStudentLastLogin(){
		return this.m_tStudentLastLogin;
	}

	/**
	 * When did this student last log in?
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.dtmStudentLastLogin</Code>
	 **/	
	public void setStudentLastLogin(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_tStudentLastLogin));
		this.m_tStudentLastLogin=value;
	}
	

	/**
	 * When did this student last log out?
	 * @return When did this student last log out?
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.dtmStudentLastLogout</Code>
	 **/
	public String getStudentLastLogout(){
		return this.m_tStudentLastLogout;
	}

	/**
	 * When did this student last log out?
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.dtmStudentLastLogout</Code>
	 **/	
	public void setStudentLastLogout(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_tStudentLastLogout));
		this.m_tStudentLastLogout=value;
	}
	

	/**
	 * When was this student added to SignUp? (Used for separation of these students after an update: who is new?). This is new in version 6.10, November 2005.
	 * @return When was this student added to SignUp? (Used for separation of these students after an update: who is new?). This is new in version 6.10, November 2005.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.dtmStudentZUVAdd</Code>
	 **/
	public Date getStudentZUVAdd(){
		return this.m_dStudentZUVAdd;
	}

	/**
	 * When was this student added to SignUp? (Used for separation of these students after an update: who is new?). This is new in version 6.10, November 2005.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.dtmStudentZUVAdd</Code>
	 **/	
	public void setStudentZUVAdd(Date value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_dStudentZUVAdd));
		this.m_dStudentZUVAdd=value;
	}
	

	/**
	 * When was this student last updated with data from the central administration? (Used for archiving student-data of students that have not been reported as immatriculated for more than two years or so). This is new in version 6.01, April 2005.
	 * @return When was this student last updated with data from the central administration? (Used for archiving student-data of students that have not been reported as immatriculated for more than two years or so). This is new in version 6.01, April 2005.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.dtmStudentZUVUpdate</Code>
	 **/
	public Date getStudentZUVUpdate(){
		return this.m_dStudentZUVUpdate;
	}

	/**
	 * When was this student last updated with data from the central administration? (Used for archiving student-data of students that have not been reported as immatriculated for more than two years or so). This is new in version 6.01, April 2005.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.dtmStudentZUVUpdate</Code>
	 **/	
	public void setStudentZUVUpdate(Date value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_dStudentZUVUpdate));
		this.m_dStudentZUVUpdate=value;
	}
	

	/**
	 * When was this student first immatriculated (according to ZUV-data)? This is new in version 6.09, August 2005.
	 * @return When was this student first immatriculated (according to ZUV-data)? This is new in version 6.09, August 2005.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.dtmStudentZUVImmatrikuliert</Code>
	 **/
	public Date getStudentZUVImmatrikuliert(){
		return this.m_dStudentZUVImmatrikuliert;
	}

	/**
	 * When was this student first immatriculated (according to ZUV-data)? This is new in version 6.09, August 2005.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.dtmStudentZUVImmatrikuliert</Code>
	 **/	
	public void setStudentZUVImmatrikuliert(Date value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_dStudentZUVImmatrikuliert));
		this.m_dStudentZUVImmatrikuliert=value;
	}
	

	/**
	 * @Deprecated. Use intFach instead.
	 * @return @Deprecated. Use intFach instead.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.strStudentHf1</Code>
	 **/
	public String getStudentHf1(){
		return this.m_sStudentHf1;
	}

	/**
	 * @Deprecated. Use intFach instead.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.strStudentHf1</Code>
	 **/	
	public void setStudentHf1(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sStudentHf1)));
		this.m_sStudentHf1=value;
	}
	

	/**
	 * @Deprecated. Use intFach instead.
	 * @return @Deprecated. Use intFach instead.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.strStudentHf2</Code>
	 **/
	public String getStudentHf2(){
		return this.m_sStudentHf2;
	}

	/**
	 * @Deprecated. Use intFach instead.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.strStudentHf2</Code>
	 **/	
	public void setStudentHf2(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sStudentHf2)));
		this.m_sStudentHf2=value;
	}
	

	/**
	 * Is this student female? (Concession to feminism. Please note that in this boolean construction, femaleness is the 1, the "true," the "thereness," while maleness is the absence.)
	 * @return Is this student female? (Concession to feminism. Please note that in this boolean construction, femaleness is the 1, the "true," the "thereness," while maleness is the absence.)
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.blnStudentFemale</Code>
	 **/
	public boolean getStudentFemale(){
		return this.m_bStudentFemale;
	}

	/**
	 * Is this student female? (Concession to feminism. Please note that in this boolean construction, femaleness is the 1, the "true," the "thereness," while maleness is the absence.)
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.blnStudentFemale</Code>
	 **/	
	public void setStudentFemale(boolean value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_bStudentFemale));
		this.m_bStudentFemale=value;
	}
	

	/**
	 * Random number and SessionID of this student. Highly deprecated, no unique index anymore
	 * @return Random number and SessionID of this student. Highly deprecated, no unique index anymore
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.lngStudentRandom</Code>
	 **/
	public long getStudentRandom(){
		return this.m_lStudentRandom;
	}

	/**
	 * Random number and SessionID of this student. Highly deprecated, no unique index anymore
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.lngStudentRandom</Code>
	 **/	
	public void setStudentRandom(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lStudentRandom));
		this.m_lStudentRandom=value;
	}
	

	/**
	 * @Deprecated. Please use intFach instead.
	 * @return @Deprecated. Please use intFach instead.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.strStudentZUVFach</Code>
	 **/
	public String getStudentZUVFach(){
		return this.m_sStudentZUVFach;
	}

	/**
	 * @Deprecated. Please use intFach instead.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.strStudentZUVFach</Code>
	 **/	
	public void setStudentZUVFach(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sStudentZUVFach)));
		this.m_sStudentZUVFach=value;
	}
	

	/**
	 * Subject 1 that the student studies.
	 * @return Subject 1 that the student studies.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.intStudentFach1</Code>
	 **/
	public int getStudentFach1(){
		return this.m_iStudentFach1;
	}

	/**
	 * Subject 1 that the student studies.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.intStudentFach1</Code>
	 **/	
	public void setStudentFach1(int value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_iStudentFach1));
		this.m_iStudentFach1=value;
	}
	

	/**
	 * Subject 2 that the student studies.
	 * @return Subject 2 that the student studies.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.intStudentFach2</Code>
	 **/
	public int getStudentFach2(){
		return this.m_iStudentFach2;
	}

	/**
	 * Subject 2 that the student studies.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.intStudentFach2</Code>
	 **/	
	public void setStudentFach2(int value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_iStudentFach2));
		this.m_iStudentFach2=value;
	}
	

	/**
	 * Subject 3 that the student studies.
	 * @return Subject 3 that the student studies.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.intStudentFach3</Code>
	 **/
	public int getStudentFach3(){
		return this.m_iStudentFach3;
	}

	/**
	 * Subject 3 that the student studies.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.intStudentFach3</Code>
	 **/	
	public void setStudentFach3(int value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_iStudentFach3));
		this.m_iStudentFach3=value;
	}
	

	/**
	 * Subject 4 that the student studies.
	 * @return Subject 4 that the student studies.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.intStudentFach4</Code>
	 **/
	public int getStudentFach4(){
		return this.m_iStudentFach4;
	}

	/**
	 * Subject 4 that the student studies.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.intStudentFach4</Code>
	 **/	
	public void setStudentFach4(int value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_iStudentFach4));
		this.m_iStudentFach4=value;
	}
	

	/**
	 * Subject 5 that the student studies.
	 * @return Subject 5 that the student studies.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.intStudentFach5</Code>
	 **/
	public int getStudentFach5(){
		return this.m_iStudentFach5;
	}

	/**
	 * Subject 5 that the student studies.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.intStudentFach5</Code>
	 **/	
	public void setStudentFach5(int value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_iStudentFach5));
		this.m_iStudentFach5=value;
	}
	

	/**
	 * Subject 6 that the student studies.
	 * @return Subject 6 that the student studies.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.intStudentFach6</Code>
	 **/
	public int getStudentFach6(){
		return this.m_iStudentFach6;
	}

	/**
	 * Subject 6 that the student studies.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.intStudentFach6</Code>
	 **/	
	public void setStudentFach6(int value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_iStudentFach6));
		this.m_iStudentFach6=value;
	}
	

	/**
	 * Semester (ordinal number) of this student concerning his subject 1.
	 * @return Semester (ordinal number) of this student concerning his subject 1.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.intStudentFachsemester1</Code>
	 **/
	public int getStudentFachsemester1(){
		return this.m_iStudentFachsemester1;
	}

	/**
	 * Semester (ordinal number) of this student concerning his subject 1.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.intStudentFachsemester1</Code>
	 **/	
	public void setStudentFachsemester1(int value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_iStudentFachsemester1));
		this.m_iStudentFachsemester1=value;
	}
	

	/**
	 * Semester (ordinal number) of this student concerning his subject 2.
	 * @return Semester (ordinal number) of this student concerning his subject 2.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.intStudentFachsemester2</Code>
	 **/
	public int getStudentFachsemester2(){
		return this.m_iStudentFachsemester2;
	}

	/**
	 * Semester (ordinal number) of this student concerning his subject 2.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.intStudentFachsemester2</Code>
	 **/	
	public void setStudentFachsemester2(int value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_iStudentFachsemester2));
		this.m_iStudentFachsemester2=value;
	}
	

	/**
	 * Semester (ordinal number) of this student concerning his subject 3.
	 * @return Semester (ordinal number) of this student concerning his subject 3.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.intStudentFachsemester3</Code>
	 **/
	public int getStudentFachsemester3(){
		return this.m_iStudentFachsemester3;
	}

	/**
	 * Semester (ordinal number) of this student concerning his subject 3.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.intStudentFachsemester3</Code>
	 **/	
	public void setStudentFachsemester3(int value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_iStudentFachsemester3));
		this.m_iStudentFachsemester3=value;
	}
	

	/**
	 * Semester (ordinal number) of this student concerning his subject 4.
	 * @return Semester (ordinal number) of this student concerning his subject 4.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.intStudentFachsemester4</Code>
	 **/
	public int getStudentFachsemester4(){
		return this.m_iStudentFachsemester4;
	}

	/**
	 * Semester (ordinal number) of this student concerning his subject 4.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.intStudentFachsemester4</Code>
	 **/	
	public void setStudentFachsemester4(int value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_iStudentFachsemester4));
		this.m_iStudentFachsemester4=value;
	}
	

	/**
	 * Semester (ordinal number) of this student concerning his subject 5.
	 * @return Semester (ordinal number) of this student concerning his subject 5.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.intStudentFachsemester5</Code>
	 **/
	public int getStudentFachsemester5(){
		return this.m_iStudentFachsemester5;
	}

	/**
	 * Semester (ordinal number) of this student concerning his subject 5.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.intStudentFachsemester5</Code>
	 **/	
	public void setStudentFachsemester5(int value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_iStudentFachsemester5));
		this.m_iStudentFachsemester5=value;
	}
	

	/**
	 * Semester (ordinal number) of this student concerning his subject 6.
	 * @return Semester (ordinal number) of this student concerning his subject 6.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.intStudentFachsemester6</Code>
	 **/
	public int getStudentFachsemester6(){
		return this.m_iStudentFachsemester6;
	}

	/**
	 * Semester (ordinal number) of this student concerning his subject 6.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.intStudentFachsemester6</Code>
	 **/	
	public void setStudentFachsemester6(int value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_iStudentFachsemester6));
		this.m_iStudentFachsemester6=value;
	}
	

	/**
	 * Remark about this student.
	 * @return Remark about this student.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.strStudentBemerkung</Code>
	 **/
	public String getStudentBemerkung(){
		return this.m_sStudentBemerkung;
	}

	/**
	 * Remark about this student.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.strStudentBemerkung</Code>
	 **/	
	public void setStudentBemerkung(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sStudentBemerkung)));
		this.m_sStudentBemerkung=value;
	}
	

	/**
	 * Multi-purpose flag. (From version 6-11 onwards).@deprecated ? (Since it makes no sense, really, to have 1 flag for a student, for all seminars, for all teachers.
	 * @return Multi-purpose flag. (From version 6-11 onwards).@deprecated ? (Since it makes no sense, really, to have 1 flag for a student, for all seminars, for all teachers.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.blnStudentSelected</Code>
	 **/
	public boolean getStudentSelected(){
		return this.m_bStudentSelected;
	}

	/**
	 * Multi-purpose flag. (From version 6-11 onwards).@deprecated ? (Since it makes no sense, really, to have 1 flag for a student, for all seminars, for all teachers.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.blnStudentSelected</Code>
	 **/	
	public void setStudentSelected(boolean value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_bStudentSelected));
		this.m_bStudentSelected=value;
	}
	

	/**
	 * Urlaubssemester, texual information with count of Urlaubssemester, half-structured.
	 * @return Urlaubssemester, texual information with count of Urlaubssemester, half-structured.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.strStudentUrlaub</Code>
	 **/
	public String getStudentUrlaub(){
		return this.m_sStudentUrlaub;
	}

	/**
	 * Urlaubssemester, texual information with count of Urlaubssemester, half-structured.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudent.strStudentUrlaub</Code>
	 **/	
	public void setStudentUrlaub(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sStudentUrlaub)));
		this.m_sStudentUrlaub=value;
	}
	
////////////////////////////////////////////////////////////////
// 2b.   E X T R A  M E T H O D E N 
////////////////////////////////////////////////////////////////
	/**
         * Prüft Login
	 * @param strName Nachname
	 * @param strPwd Passwort
         * @param strMatrikelnummer Matrikelnummer
	 * @param lngSeminarID id of institute or seminar.
	 * @return true, falls erfolgreich.
	 */
	public boolean loginSec(String strName, String strPwd, String strMatrikelnummer, long lngSeminarID){

		boolean blnReturn=false;
		ResultSet rst=null;

		try{
                    PreparedStatement pstm=prepareStatement( "SELECT " +
                        "b.*, " +
                             "x.\"lngSeminarID\", " +
                             "x.\"intFachID\" " +

                     "FROM \"tblBdStudent\" b, \"tblSdSeminarXFach\" x " +
                     "WHERE " +
                      "(" +
                        "b.\"strMatrikelnummer\"=? AND " +
                        "b.\"strStudentNachname\"=? AND " +
                        "x.\"lngSeminarID\"=? AND " +
                        "(" +
                              "b.\"intStudentFach1\"=x.\"intFachID\" OR " +
                              "b.\"intStudentFach2\"=x.\"intFachID\" OR " +
                              "b.\"intStudentFach3\"=x.\"intFachID\" OR " +
                              "b.\"intStudentFach4\"=x.\"intFachID\" OR " +
                              "b.\"intStudentFach5\"=x.\"intFachID\" OR " +
                              "b.\"intStudentFach6\"=x.\"intFachID\" " +
                        ")" +
                    ");");
                    
                    pstm.setString(1, strMatrikelnummer);
                    pstm.setString(2, strName);
                    pstm.setLong(3, lngSeminarID);
                    
                    String sPwd;
		    rst=pstm.executeQuery();
			while(rst.next()){

                           sPwd=rst.getString("strStudentPasswort");
                           if(PasswordHash.validatePassword(strPwd, sPwd)){
				initByRst(rst);
				rst.close();
				blnReturn=true;
                                break;
                            }
                        }
                    }catch(NoSuchAlgorithmException eLogin){
				blnReturn=false;
                    } catch (InvalidKeySpecException eLogin) {
                            blnReturn=false;
                    } catch (SQLException eLogin) {
                        blnReturn=false;
                    } catch (NamingException eLogin) {
                        blnReturn=false;
                    }
		try{this.disconnect();}catch(Exception e){}
		return blnReturn;
	}
////////////////////////////////////////////////////////////////
// 3.   X M L  U T I L I T I E S
////////////////////////////////////////////////////////////////
	
	/**
	 * @return Objekt als XML-String
	 **/
	public String toXMLString(){
		return 	

			"<StudentPID>" + m_lStudentPID + "</StudentPID>"  + 
			"<Matrikelnummer>" + m_sMatrikelnummer + "</Matrikelnummer>"  + 
			"<StudentVorname>" + m_sStudentVorname + "</StudentVorname>"  + 
			"<StudentNachname>" + m_sStudentNachname + "</StudentNachname>"  + 
			"<StudentGeburtsname>" + m_sStudentGeburtsname + "</StudentGeburtsname>"  + 
			"<StudentSemester>" + m_iStudentSemester + "</StudentSemester>"  + 
			"<StudentPasswort>" + m_sStudentPasswort + "</StudentPasswort>"  + 
			"<StudentLoggedIn>" + m_bStudentLoggedIn + "</StudentLoggedIn>"  + 
			"<StudentGeburtstag>" + m_dStudentGeburtstag + "</StudentGeburtstag>"  + 
			"<StudentGeburtsort>" + m_sStudentGeburtsort + "</StudentGeburtsort>"  + 
			"<StudentStaat>" + m_sStudentStaat + "</StudentStaat>"  + 
			"<StudentZUVZiel>" + m_lStudentZUVZiel + "</StudentZUVZiel>"  + 
			"<StudentStrasse>" + m_sStudentStrasse + "</StudentStrasse>"  + 
			"<StudentPLZ>" + m_sStudentPLZ + "</StudentPLZ>"  + 
			"<StudentOrt>" + m_sStudentOrt + "</StudentOrt>"  + 
			"<StudentEmail>" + m_sStudentEmail + "</StudentEmail>"  + 
			"<StudentPublishEmail>" + m_bStudentPublishEmail + "</StudentPublishEmail>"  + 
			"<StudentTelefon>" + m_sStudentTelefon + "</StudentTelefon>"  + 
			"<StudentHandy>" + m_sStudentHandy + "</StudentHandy>"  + 
			"<StudentPublishHandy>" + m_bStudentPublishHandy + "</StudentPublishHandy>"  + 
			"<StudentHomepage>" + m_sStudentHomepage + "</StudentHomepage>"  + 
			"<StudentPublishHomepage>" + m_bStudentPublishHomepage + "</StudentPublishHomepage>"  + 
			"<StudentInterests>" + m_sStudentInterests + "</StudentInterests>"  + 
			"<StudentVisible>" + m_bStudentVisible + "</StudentVisible>"  + 
			"<StudentLastLogin>" + m_tStudentLastLogin + "</StudentLastLogin>"  + 
			"<StudentLastLogout>" + m_tStudentLastLogout + "</StudentLastLogout>"  + 
			"<StudentZUVAdd>" + m_dStudentZUVAdd + "</StudentZUVAdd>"  + 
			"<StudentZUVUpdate>" + m_dStudentZUVUpdate + "</StudentZUVUpdate>"  + 
			"<StudentZUVImmatrikuliert>" + m_dStudentZUVImmatrikuliert + "</StudentZUVImmatrikuliert>"  + 
			"<StudentHf1>" + m_sStudentHf1 + "</StudentHf1>"  + 
			"<StudentHf2>" + m_sStudentHf2 + "</StudentHf2>"  + 
			"<StudentFemale>" + m_bStudentFemale + "</StudentFemale>"  + 
			"<StudentRandom>" + m_lStudentRandom + "</StudentRandom>"  + 
			"<StudentZUVFach>" + m_sStudentZUVFach + "</StudentZUVFach>"  + 
			"<StudentFach1>" + m_iStudentFach1 + "</StudentFach1>"  + 
			"<StudentFach2>" + m_iStudentFach2 + "</StudentFach2>"  + 
			"<StudentFach3>" + m_iStudentFach3 + "</StudentFach3>"  + 
			"<StudentFach4>" + m_iStudentFach4 + "</StudentFach4>"  + 
			"<StudentFach5>" + m_iStudentFach5 + "</StudentFach5>"  + 
			"<StudentFach6>" + m_iStudentFach6 + "</StudentFach6>"  + 
			"<StudentFachsemester1>" + m_iStudentFachsemester1 + "</StudentFachsemester1>"  + 
			"<StudentFachsemester2>" + m_iStudentFachsemester2 + "</StudentFachsemester2>"  + 
			"<StudentFachsemester3>" + m_iStudentFachsemester3 + "</StudentFachsemester3>"  + 
			"<StudentFachsemester4>" + m_iStudentFachsemester4 + "</StudentFachsemester4>"  + 
			"<StudentFachsemester5>" + m_iStudentFachsemester5 + "</StudentFachsemester5>"  + 
			"<StudentFachsemester6>" + m_iStudentFachsemester6 + "</StudentFachsemester6>"  + 
			"<StudentBemerkung>" + m_sStudentBemerkung + "</StudentBemerkung>"  + 
			"<StudentSelected>" + m_bStudentSelected + "</StudentSelected>"  + 
			"<StudentUrlaub>" + m_sStudentUrlaub + "</StudentUrlaub>" ;
	}

////////////////////////////////////////////////////////////////
// 4.   S Q L  U T I L I T I E S
////////////////////////////////////////////////////////////////


	/**
	 * Where-SQL für eindeutigen Index dieses Objekte in der Datenbank (in Tabelle 'tblBdStudent.').
	 * @return SQL-String wie z.B. "Id1=? and Id2=?"
	 **/
	private String getSQLWhereClause(){
		return 
			"\"lngStudentPID\"=? AND " + 
			"\"strMatrikelnummer\"=?";
	}

	/**
	 * Unique where-clause for table 'tblBdStudent.'
	 * @return String (SQL-code, postgres-specific)
	 *
	 * @deprecated
	 **/
	protected String getSQLWhereClauseOld(){
			return
				"\"lngStudentPID\"=" + this.m_lStudentPID + " AND "  +
				"\"strMatrikelnummer\"='" + this.m_sMatrikelnummer + "'";
	}
	
	/**
	 * SQL-Befehl zum Löschen des Objekts in der Datenbank (aus Tabelle 'tblBdStudent.')
	 * @return SQL-String zum Löschen des Objekts, z.B. "delete from T1 where (Id1=?);"
	 **/
	private String toDBDeleteString(){
		return "delete from \"tblBdStudent\" where ( " + this.getSQLWhereClause() + ");";
	}

	/**
 	 * Setzt die Werte für alle Platzhalter in {@link #getSQLWhereClause()}.
	 * @param ii Offset für die Parameter (inklusive).
	 * @throws SQLException 
	 **/
	private void pokeWhere(int ii, PreparedStatement ps) throws SQLException{		
		ps.setLong(ii++, m_lStudentPID);
		ps.setString(ii++, m_sMatrikelnummer);
	}

	/**
  	 * Füllt das SQL-Statement mit den aktuellen Werten des Objekts
	 * @throws SQLException
	 **/
	private void pokeStatement(PreparedStatement ps) throws SQLException{
		
		ps.setLong(1, m_lStudentPID);
		ps.setString(2, m_sMatrikelnummer);
		ps.setString(3, m_sStudentVorname);
		ps.setString(4, m_sStudentNachname);
		ps.setString(5, m_sStudentGeburtsname);
		ps.setInt(6, m_iStudentSemester);
		ps.setString(7, m_sStudentPasswort);
		ps.setBoolean(8, m_bStudentLoggedIn);
		ps.setDate(9, m_dStudentGeburtstag);
		ps.setString(10, m_sStudentGeburtsort);
		ps.setString(11, m_sStudentStaat);
		ps.setLong(12, m_lStudentZUVZiel);
		ps.setString(13, m_sStudentStrasse);
		ps.setString(14, m_sStudentPLZ);
		ps.setString(15, m_sStudentOrt);
		ps.setString(16, m_sStudentEmail);
		ps.setBoolean(17, m_bStudentPublishEmail);
		ps.setString(18, m_sStudentTelefon);
		ps.setString(19, m_sStudentHandy);
		ps.setBoolean(20, m_bStudentPublishHandy);
		ps.setString(21, m_sStudentHomepage);
		ps.setBoolean(22, m_bStudentPublishHomepage);
		ps.setString(23, m_sStudentInterests);
		ps.setBoolean(24, m_bStudentVisible);
		ps.setString(25, m_tStudentLastLogin);
		ps.setString(26, m_tStudentLastLogout);
		ps.setDate(27, m_dStudentZUVAdd);
		ps.setDate(28, m_dStudentZUVUpdate);
		ps.setDate(29, m_dStudentZUVImmatrikuliert);
		ps.setString(30, m_sStudentHf1);
		ps.setString(31, m_sStudentHf2);
		ps.setBoolean(32, m_bStudentFemale);
		ps.setLong(33, m_lStudentRandom);
		ps.setString(34, m_sStudentZUVFach);
		ps.setInt(35, m_iStudentFach1);
		ps.setInt(36, m_iStudentFach2);
		ps.setInt(37, m_iStudentFach3);
		ps.setInt(38, m_iStudentFach4);
		ps.setInt(39, m_iStudentFach5);
		ps.setInt(40, m_iStudentFach6);
		ps.setInt(41, m_iStudentFachsemester1);
		ps.setInt(42, m_iStudentFachsemester2);
		ps.setInt(43, m_iStudentFachsemester3);
		ps.setInt(44, m_iStudentFachsemester4);
		ps.setInt(45, m_iStudentFachsemester5);
		ps.setInt(46, m_iStudentFachsemester6);
		ps.setString(47, m_sStudentBemerkung);
		ps.setBoolean(48, m_bStudentSelected);
		ps.setString(49, m_sStudentUrlaub);

	}

	/**
	 * SQL-Befehl zum Speichern der Objekteigenschaften in die Datenbank (Tabelle 'tblBdStudent.')
	 * @return PreparedStatemtent, das nach Aufruf von {@link #pokeStatement(PreparedStatement)} und {@link #execute()} das Update ausführt.
	 **/
	private String toDBUpdateString(){
		return "update \"tblBdStudent\" set " +
			"\"lngStudentPID\"=?, " +
			"\"strMatrikelnummer\"=?, " +
			"\"strStudentVorname\"=?, " +
			"\"strStudentNachname\"=?, " +
			"\"strStudentGeburtsname\"=?, " +
			"\"intStudentSemester\"=?, " +
			"\"strStudentPasswort\"=?, " +
			"\"blnStudentLoggedIn\"=?, " +
			"\"dtmStudentGeburtstag\"=?, " +
			"\"strStudentGeburtsort\"=?, " +
			"\"strStudentStaat\"=?, " +
			"\"lngStudentZUVZiel\"=?, " +
			"\"strStudentStrasse\"=?, " +
			"\"strStudentPLZ\"=?, " +
			"\"strStudentOrt\"=?, " +
			"\"strStudentEmail\"=?, " +
			"\"blnStudentPublishEmail\"=?, " +
			"\"strStudentTelefon\"=?, " +
			"\"strStudentHandy\"=?, " +
			"\"blnStudentPublishHandy\"=?, " +
			"\"strStudentHomepage\"=?, " +
			"\"blnStudentPublishHomepage\"=?, " +
			"\"strStudentInterests\"=?, " +
			"\"blnStudentVisible\"=?, " +
			"\"dtmStudentLastLogin\"=?, " +
			"\"dtmStudentLastLogout\"=?, " +
			"\"dtmStudentZUVAdd\"=?, " +
			"\"dtmStudentZUVUpdate\"=?, " +
			"\"dtmStudentZUVImmatrikuliert\"=?, " +
			"\"strStudentHf1\"=?, " +
			"\"strStudentHf2\"=?, " +
			"\"blnStudentFemale\"=?, " +
			"\"lngStudentRandom\"=?, " +
			"\"strStudentZUVFach\"=?, " +
			"\"intStudentFach1\"=?, " +
			"\"intStudentFach2\"=?, " +
			"\"intStudentFach3\"=?, " +
			"\"intStudentFach4\"=?, " +
			"\"intStudentFach5\"=?, " +
			"\"intStudentFach6\"=?, " +
			"\"intStudentFachsemester1\"=?, " +
			"\"intStudentFachsemester2\"=?, " +
			"\"intStudentFachsemester3\"=?, " +
			"\"intStudentFachsemester4\"=?, " +
			"\"intStudentFachsemester5\"=?, " +
			"\"intStudentFachsemester6\"=?, " +
			"\"strStudentBemerkung\"=?, " +
			"\"blnStudentSelected\"=?, " +
			"\"strStudentUrlaub\"=?" +
			" where (" + this.getSQLWhereClause() + ");";
	}

	/**
	 * SQL-Befehl zum Hinzufügen des Objekts in die Datenbank (Tabelle 'tblBdStudent.')
	 * @return SQL-String "insert into...".
	 **/
	private String toDBAddString(){
		return "insert into \"tblBdStudent\" ( " +
			"\"lngStudentPID\", \"strMatrikelnummer\", \"strStudentVorname\", \"strStudentNachname\", \"strStudentGeburtsname\", \"intStudentSemester\", \"strStudentPasswort\", \"blnStudentLoggedIn\", \"dtmStudentGeburtstag\", \"strStudentGeburtsort\", \"strStudentStaat\", \"lngStudentZUVZiel\", \"strStudentStrasse\", \"strStudentPLZ\", \"strStudentOrt\", \"strStudentEmail\", \"blnStudentPublishEmail\", \"strStudentTelefon\", \"strStudentHandy\", \"blnStudentPublishHandy\", \"strStudentHomepage\", \"blnStudentPublishHomepage\", \"strStudentInterests\", \"blnStudentVisible\", \"dtmStudentLastLogin\", \"dtmStudentLastLogout\", \"dtmStudentZUVAdd\", \"dtmStudentZUVUpdate\", \"dtmStudentZUVImmatrikuliert\", \"strStudentHf1\", \"strStudentHf2\", \"blnStudentFemale\", \"lngStudentRandom\", \"strStudentZUVFach\", \"intStudentFach1\", \"intStudentFach2\", \"intStudentFach3\", \"intStudentFach4\", \"intStudentFach5\", \"intStudentFach6\", \"intStudentFachsemester1\", \"intStudentFachsemester2\", \"intStudentFachsemester3\", \"intStudentFachsemester4\", \"intStudentFachsemester5\", \"intStudentFachsemester6\", \"strStudentBemerkung\", \"blnStudentSelected\", \"strStudentUrlaub\" ) VALUES ( ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
	}
	
////////////////////////////////////////////////////////////////
// 5.   K O N S T R U K T O R   H I L F E 
////////////////////////////////////////////////////////////////
	
	/**
	 * Lade die Objekteigenschaften aus der Datenbank
	 * @param Indexwerte für eindeutige Identifikation der Datenbankdatbellenzeile
	 * @throws NamingException 
	 * @throws SQLException 
	 **/
	private void init(long lngStudentPID, String strMatrikelnummer) throws SQLException, NamingException{

		this.m_lStudentPID=lngStudentPID;

		this.m_sMatrikelnummer=strMatrikelnummer;

		this.m_sMatrikelnummer=strMatrikelnummer;

		
		PreparedStatement pstm = prepareStatement("select * from \"tblBdStudent\" where (" + this.getSQLWhereClause() + ");");
		pokeWhere(1,pstm);

		ResultSet rst=pstm.executeQuery();
		if(rst.next()) this.initByRst(rst);
		rst.close();
		this.disconnect();
		rst=null;
	}	
	
	/**
	 * Lade die Objekteigenschaften aus einem ResultSet.
	 * param rst ResultSet mit allen Spalten der Tabelle 'tblBdStudent'
	 * @throws SQLException 
	 **/
	public void initByRst(ResultSet rst) throws SQLException{
		this.m_lStudentPID=rst.getLong("lngStudentPID");
		this.m_sMatrikelnummer=rst.getString("strMatrikelnummer");
		this.m_sStudentVorname=rst.getString("strStudentVorname");
		this.m_sStudentNachname=rst.getString("strStudentNachname");
		this.m_sStudentGeburtsname=rst.getString("strStudentGeburtsname");
		this.m_iStudentSemester=rst.getInt("intStudentSemester");
		this.m_sStudentPasswort=rst.getString("strStudentPasswort");
		this.m_bStudentLoggedIn=rst.getBoolean("blnStudentLoggedIn");
		this.m_dStudentGeburtstag=rst.getDate("dtmStudentGeburtstag");
		this.m_sStudentGeburtsort=rst.getString("strStudentGeburtsort");
		this.m_sStudentStaat=rst.getString("strStudentStaat");
		this.m_lStudentZUVZiel=rst.getLong("lngStudentZUVZiel");
		this.m_sStudentStrasse=rst.getString("strStudentStrasse");
		this.m_sStudentPLZ=rst.getString("strStudentPLZ");
		this.m_sStudentOrt=rst.getString("strStudentOrt");
		this.m_sStudentEmail=rst.getString("strStudentEmail");
		this.m_bStudentPublishEmail=rst.getBoolean("blnStudentPublishEmail");
		this.m_sStudentTelefon=rst.getString("strStudentTelefon");
		this.m_sStudentHandy=rst.getString("strStudentHandy");
		this.m_bStudentPublishHandy=rst.getBoolean("blnStudentPublishHandy");
		this.m_sStudentHomepage=rst.getString("strStudentHomepage");
		this.m_bStudentPublishHomepage=rst.getBoolean("blnStudentPublishHomepage");
		this.m_sStudentInterests=rst.getString("strStudentInterests");
		this.m_bStudentVisible=rst.getBoolean("blnStudentVisible");
		this.m_tStudentLastLogin=rst.getString("dtmStudentLastLogin");
		this.m_tStudentLastLogout=rst.getString("dtmStudentLastLogout");
		this.m_dStudentZUVAdd=rst.getDate("dtmStudentZUVAdd");
		this.m_dStudentZUVUpdate=rst.getDate("dtmStudentZUVUpdate");
		this.m_dStudentZUVImmatrikuliert=rst.getDate("dtmStudentZUVImmatrikuliert");
		this.m_sStudentHf1=rst.getString("strStudentHf1");
		this.m_sStudentHf2=rst.getString("strStudentHf2");
		this.m_bStudentFemale=rst.getBoolean("blnStudentFemale");
		this.m_lStudentRandom=rst.getLong("lngStudentRandom");
		this.m_sStudentZUVFach=rst.getString("strStudentZUVFach");
		this.m_iStudentFach1=rst.getInt("intStudentFach1");
		this.m_iStudentFach2=rst.getInt("intStudentFach2");
		this.m_iStudentFach3=rst.getInt("intStudentFach3");
		this.m_iStudentFach4=rst.getInt("intStudentFach4");
		this.m_iStudentFach5=rst.getInt("intStudentFach5");
		this.m_iStudentFach6=rst.getInt("intStudentFach6");
		this.m_iStudentFachsemester1=rst.getInt("intStudentFachsemester1");
		this.m_iStudentFachsemester2=rst.getInt("intStudentFachsemester2");
		this.m_iStudentFachsemester3=rst.getInt("intStudentFachsemester3");
		this.m_iStudentFachsemester4=rst.getInt("intStudentFachsemester4");
		this.m_iStudentFachsemester5=rst.getInt("intStudentFachsemester5");
		this.m_iStudentFachsemester6=rst.getInt("intStudentFachsemester6");
		this.m_sStudentBemerkung=rst.getString("strStudentBemerkung");
		this.m_bStudentSelected=rst.getBoolean("blnStudentSelected");
		this.m_sStudentUrlaub=rst.getString("strStudentUrlaub");	
	}	

////////////////////////////////////////////////////////////////
// 6.   S Q L  U T I L I T I E S
////////////////////////////////////////////////////////////////

	/**
	 * 
	 * Öffentliche Methode, um das aktuelle Objekt als 
	 * neue Zeile in die Datenbank zu schreiben (insert into...).
	 * Diese Methode enthält keinen ID-Broker, m.a.W.
	 * muss anderweitig (durch SERIAL Ids oder per Programm) 
         * Sorge dafür getragen werden, dass das Objekt keine 
	 * bestehenden Indices verletzt.
	 * 
	 * @return true für Erfolg (oder falls das Objekt nicht 'dirty' wahr, {@link #isDirty()}).
	 * @throws NamingException 
	 * @throws SQLException 
	 * @throws Exception 
	 **/
	public boolean add() throws SQLException, NamingException, Exception{
		if( !(isDirty()) ) return true;
		boolean bReturn = false;
		
		// Lade Statement mit SQL
		PreparedStatement pstm = prepareStatement(toDBAddString());
		
		// Lade Objekteigenschaften in Statement
		// und übermittle das an die Datenbank
		pokeStatement(pstm);
		try {
			pstm.execute();
			bReturn=true;
		} catch (SQLException e) {}
		this.m_bIsDirty = !(bReturn);
		return bReturn;
	}

	/**
	 * 
	 * Schreibe die Eigenschaften dieses Objekts in die 
	 * Datenbank.
	 * 
	 * @return true für Erfolg (oder falls das Objekt nicht 'dirty' wahr, {@link #isDirty()}).
	 * @throws NamingException 
	 * @throws SQLException 
	 * @throws Exception 
         **/
	public boolean update() throws SQLException, NamingException, Exception{
		if( !(isDirty()) ) return true;
		boolean bReturn = false;

		// Lade Statement mit SQL
		PreparedStatement pstm = prepareStatement(toDBUpdateString());

		// Lade Objekteigenschaften in Statement
		pokeStatement(pstm);

		// Identifiziere das Objekt (bzw. die Tabellenzeile) per Where-Clause
		// und übermittle die neuen Werte an die Datenbank
		pokeWhere(50,pstm);
		bReturn	= pstm.execute();
		try {
			pstm.execute();
			bReturn=true;
		} catch (SQLException e) {}
		return bReturn;
	}

	/**
	 * 
	 * Lösche dieses Objekt aus der Datenbank
	 * 
	 * @return true für Erfolg.
	 * @throws NamingException 
	 * @throws SQLException 
         **/
	public boolean delete() throws SQLException, NamingException{
		PreparedStatement pstm = prepareStatement(toDBDeleteString());
		pokeWhere(1,pstm);
		this.m_bIsDirty = true;
		try {
			pstm.execute();
			return true;
		} catch (SQLException e) {}
		return false;
	}	
	
////////////////////////////////////////////////////////////////
// 6.   K O N S T R U K T O R E N
////////////////////////////////////////////////////////////////

	/**
	 * Leerer Konstruktor (als Bean).
	 **/
	public Student(){}	
	
	/**
	 * Konstruktor mit Indexwerten (eindeutig) der assoziierten Datenbanktabelle
	 * @throws NamingException 
	 * @throws SQLException 
	 **/
	public Student(long lngStudentPID, String strMatrikelnummer) throws SQLException, NamingException{
		this.init(lngStudentPID, strMatrikelnummer);
		this.m_bIsDirty = false;
	}	
	
	/**
	 * 
	 * Konstruktor über ResultSet: das ResultSet muss alle
	 * Spalten der Tabelle enthalten, und rst.next() muss 
	 * vor Aufruf dieser Methode aufgerufen sein.
	 * 
	 * @throws SQLException 
	 **/
	public Student(ResultSet rst) throws SQLException{
		this.initByRst(rst);
		this.m_bIsDirty = false;
	}

	/**
	 * Überarbeitet 2016 (Statement statt Query)
	 * @param matrikelnummer
	 * @throws Exception 
	 * @throws NumberFormatException 
	 */
	public Student(String matrikelnummer) throws NumberFormatException, Exception {
            PreparedStatement pstm = this.prepareStatement("select \"lngStudentPID\" from \"tblBdStudent\" where \"strMatrikelnummer\"=?;");
            pstm.setString(1, matrikelnummer);
            ResultSet rs = pstm.executeQuery();
            if(rs.next()){
                this.init(rs.getLong("lngStudentPID"), matrikelnummer);
            }
            try{rs.close();}catch(Exception e){}
	}
  }//Klassenende
