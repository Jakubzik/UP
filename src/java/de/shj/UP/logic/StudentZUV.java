/*
 * Software-Developement H. Jakubzik, Licence Version 1.0
 *
 *
 * Copyright (c) 2003 Heiko Jakubzik.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by
 *        H. Jakubzik (http:////www.heiko-jakubzik.de//)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "shj" and "SignUp" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact heiko.jakubzik@t-online.de.
 *
 * 5. Products derived from this software may not be called "e~vv,"
 *    nor may "e~vv" appear in their name, without prior written
 *    permission of H. Jakubzik.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
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
 * @todo
 * line		description
 *  168		Why is .toDBAddString() public instead of private?
 *
 * code structure
 *
 * 1. constants													line  75
 *
 * 2. public methods											line  90
 *
 * 3. private methods											line 183
 *
 * 4. constructor												line 276
 *
 * @version 5-23-01 (auto-coded)
 * change date              change author           change description
 * November 10, 2003        h. jakubzik             creation
 * July 24, 2004			h. jakubzik				added 'StudentStaat' and 'StudentZUVZiel'
 *													(which are new in schema 5-23) to the
 *													logic. Also added the mechanism that
 *													sets 'Deutschland' als default Staat.
 *
 * May 8, 2005				h. jakubzik				modified 'isNewStudent()' so that it sets StudentFach2();
 * 													modified .toDBUpdateString() so that it updates Fach2 only 
 * 													if the current value is below 100.000.
 * 
 * August 26, 2005			h. jakubzik				modified initByNode, added StudentGeburtsname and StudentZUVImmatrikuliert.
 * 
 * Nov 8, 2005				h. jakubzik				toDBAddString not sets dtmStudentZUVAdd to today.
 * 
 * Jul 8, 2006				h. jakubzik				added property "StudentUrlaub" (String), which is new in schema 6-16.
 */

package de.shj.UP.logic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.w3c.dom.Node;

/**
 * This class handles the import of the ZUV's student-data. It expects the data
 * to come in an XML-format specified in the document 'Student.xsd,' see constructor
 * for the versioning. <br />
 * This class is no direct frontend.
 * Class is used by signUp.data.DataInput, which is yet on a more abstract level.
 **/
public class StudentZUV extends de.shj.UP.data.Student{

	public String m_strDebug;
	private boolean m_blnUpdateEmail=false;
	private String m_strZUVUpdateNow="";

	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 1.   C O N S T A N T S
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------

	// stati
	public final static int UPDATE_OKAY	= 1;
	public final static int ADDED_OKAY		= 2;
	public final static int UPDATE_FAILED	= 3;
	public final static int ADDED_FAILED	= 4;
	public final static int INTERNAL_ERROR	= 5;
	
	private int             m_intBAMA_NEBENFACH=0;
	
	private 	Connection 	conDBLocal;
	private		boolean		isConnectedLocal=false;


  	
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 1.   P U B L I C   M E T H O D S
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------

	/**
	 * This method <br />(i) checks if the current Student is a 'new Student,' that is to say if the matriculation number
	 * already exists. If the matriculation number already exists, the method <br/>(ii a) updates the Student according
	 * to current bean data (details: Vorname, Nachname, Geburtstag, Geburtsort, Strasse, PLZ, Ort, Telefon, Female, Fach1-Fach6, Fachsemester1-Fachsemester5.). <br/>
	 * If, on the other hand, the matriculation number does not yet exist, the method <br/>(ii b) adds the Student according
	 * to current bean-data (details PID (new), Matrikelnummer, Vorname, Nachname, Geburtstag, Geburtsort, Strasse, PLZ, Ort, Telefon, Female, Fach1-Fach6, Fachsemester1-Fachsemester5.
	 * @return Integer naming one of the stati UPDATE_OKAY, ADDED_OKAY, UPDATE_FAILED, ADDED_FAILED, INTERNAL_ERROR.
	 **/
	public int updateZUV(){

		try{
		  if(this.isNewStudent()){
			  // new stutdent is added; SQL stored in m_strDebug for debugging.
			  
			  // check if there are new FachID's
			  checkFachIds();
			  
			  m_strDebug=this.toDBAddString();
			  if(sqlExe(m_strDebug))  return ADDED_OKAY;							// addition-success-exit
			  //#TODO
			  //System.err.println("Addition FAILURE: " + m_strDebug);
			  return ADDED_FAILED;
		  }
	    }catch(Exception eUpdate1){System.err.println("Ooops: Addition FAILURE: " + m_strDebug);eUpdate1.printStackTrace(System.err);return INTERNAL_ERROR;}


		try{
			// the student already exists. Update-SQL is executed (and stored in m_strDebug for debugging).
			
			// Wenn ein internes Bachelor/Master-Nebenfach gesetzt ist (>100000), 
			// von der ZUV aber auch ein Fach2 geliefert wird,
			// vorsichtshalber einen Fehler ausl�sen:
			if((m_intBAMA_NEBENFACH > 200000) && (m_intBAMA_NEBENFACH < 200050) && (getStudentFach2() != 0)){
				m_strDebug+="Internes Bachelor/Master Nebenfach steht im Konflikt mit dem Nebenfach der ZUV! Bitte das Fach Nr. 2 des Studierenden von Hand in der Import-Xml-Datei auf 0 setzen.";
				return UPDATE_FAILED;
			}
			
			// check if there are new FachID's
			checkFachIds();
			
			m_strDebug=this.toDBUpdateString();
			if(sqlExe(m_strDebug)) return UPDATE_OKAY;								// update-success-exit
		}catch(Exception updateFailed){m_strDebug+="\n\n" + updateFailed.toString(); return INTERNAL_ERROR;}

		return UPDATE_FAILED;
	}

	/**
	 * look if all 6 FachIDs already exist.
	 * If not, add new Fach.
	 */
	private void checkFachIds() {
		// TODO Auto-generated method stub
		checkFachID(this.getStudentFach1());
		checkFachID(this.getStudentFach2());
		checkFachID(this.getStudentFach3());
		checkFachID(this.getStudentFach4());
		checkFachID(this.getStudentFach5());
		checkFachID(this.getStudentFach6());
	}

	/**
	 * Check if Fach "Fach1" exists, and add it,
	 * if it does not.
	 * @param studentFach1
	 */
	private void checkFachID(int studentFach1) {
		// Fach 0 is OK
		if(studentFach1==0) return;
		
		// look up in DB
		if(lookUp("strFachBezeichnung", "tblSdFach", "\"intFachID\"=" + studentFach1).equals("#NO_RESULT")){
			try {
				sqlExe("insert into \"tblSdFach\" (\"intFachID\", \"strFachBezeichnung\") VALUES (" +
						studentFach1 + ", 'BA neu & unbekannt');");
			} catch (Exception e) {
				e.printStackTrace(System.err);
			}
		}
	}

	/**
	 * Is the current one a new matriculation number?
	 * This method is equivalent to other methods, 'isNewMatrikel' for example. Unification is needed -> #hack.
	 * @return true if current matriculation number is nonexistent in database,
	 * false otherwise.
	 * @version 6.00.02 (including Email).
	 * @version 6.01.06 (value for intStudentFach2 is set from database)
	 * @throws Exception if connection to db is erroneous
	 */
	public boolean isNewStudent() throws Exception{

		//this.connect();
		//String strPID="";
		String strEmail="";

		ResultSet rstLookup=sqlQuery("select * from " +
				"\"tblBdStudent\" where \"strMatrikelnummer\"='" + this.getMatrikelnummer() + "';");
		
		m_strDebug +="select * from " +
		"\"tblBdStudent\" where \"strMatrikelnummer\"='" + this.getMatrikelnummer() + "';";
//		System.err.println("select * from " +
//				"\"tblBdStudent\" where \"strMatrikelnummer\"='" + this.getMatrikelnummer() + "';\n\n");
				
		boolean blnReturn=false;

		if(rstLookup.next()==false){
			blnReturn=true;
		}else{
			blnReturn=false;
			this.setStudentPID(rstLookup.getLong("lngStudentPID"));
			m_intBAMA_NEBENFACH = rstLookup.getInt("intStudentFach2");
			strEmail = normalize(rstLookup.getString("strStudentEmail"));
			m_blnUpdateEmail= (strEmail.length()<5);
		}
		//System.err.println("isNewStudent? " + blnReturn + "\n" +
		//		"PID: " + getStudentPID() + "\n" + 
		//		"Email: " + strEmail + "\n\n\n");
		
		rstLookup.close();
		rstLookup=null;
		return blnReturn;
	}

	/**
	 * Adds a student to database. Data is taken from underlying object com.shj.data.Student.
	 * These properties are added: * Vorname, Nachname, Urlaub, Geburtstag, Geburtsort, Strasse, PLZ, Ort, Email, Telefon, Femail, Fach1-Fach6, Fachsemester1-Fachsemester5.
	 * Why is this method public? -> #hack (or check, rather).<br />
	 * (Added in Version 6.00.02: Email; also, dtmStudentZUVUpdate is set to current date).
	 * (Added in Version 6.09: dtmStudentZUVImmatrikuliert.
	 * @return SQL-string (insert) to add this student. SQL-injection is prohibited.
	 **/
	public String toDBAddString(){
		return "insert into \"tblBdStudent\" ( " +
			"\"lngStudentPID\", \"strMatrikelnummer\", \"strStudentVorname\", \"strStudentNachname\", \"strStudentUrlaub\", \"strStudentPasswort\", \"dtmStudentGeburtstag\", \"strStudentGeburtsort\", \"strStudentGeburtsname\", \"strStudentStaat\", \"lngStudentZUVZiel\", \"dtmStudentZUVUpdate\", \"dtmStudentZUVAdd\", \"dtmStudentZUVImmatrikuliert\", \"strStudentStrasse\", \"strStudentPLZ\", \"strStudentOrt\", \"strStudentEmail\", \"blnStudentFemale\", \"intStudentFach1\", \"intStudentFach2\", \"intStudentFach3\", \"intStudentFach4\", \"intStudentFach5\", \"intStudentFach6\", \"intStudentFachsemester1\", \"intStudentFachsemester2\", \"intStudentFachsemester3\", \"intStudentFachsemester4\", \"intStudentFachsemester5\", \"intStudentFachsemester6\" ) VALUES ( " +
			"" + this.getNextPID() + ", " +
			"'" + getDBCleanString( this.getMatrikelnummer() ) + "', " +
			"'" + getDBCleanString( this.getStudentVorname() ) + "', " +
			"'" + getDBCleanString( this.getStudentNachname() ) + "', " +
			"'" + getDBCleanString( this.getStudentUrlaub() ) + "', " +
			"'" + getDBCleanString( this.getMatrikelnummer() ) + "', " +
			"'" + g_ISO_DATE_FORMAT.format( this.getStudentGeburtstag() ) + "', " +
			"'" + getDBCleanString( this.getStudentGeburtsort() ) + "', " +
			"'" + getDBCleanString( this.getStudentGeburtsname() ) + "', " +
			"'" + getDBCleanString( this.getStudentStaat() ) + "', " +
			"" + this.getStudentZUVZiel() + ", " +
			"'" + this.getZUVUpdateNow() + "', " +
			"'" + this.getZUVUpdateNow() + "', " +
			"'" + this.getStudentZUVImmatrikuliert() + "', " +
			"'" + getDBCleanString( this.getStudentStrasse() ) + "', " +
			"'" + getDBCleanString( this.getStudentPLZ() ) + "', " +
			"'" + getDBCleanString( this.getStudentOrt() ) + "', " +
			"'" + getDBCleanString( this.getStudentEmail() ) + "', " +
			"" + getDBBoolRepresentation(this.getStudentFemale()) + ", " +
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
			"" + this.getStudentFachsemester6() + ");";
	}


	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 3.   P R I V A T E    M E T H O D S
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------

	/**
	 * In order to update the value of tblBdStudent.dtmStudentZUVUpdate (which 
	 * is the last date when the administration reported this student 
	 * as immatriculated), this internal method is called.<br />
	 * Whenever new students are added, or existing students updated, 
	 * this value is used.
	 * @return ISO-Date format representation of today
	 */
	private String getZUVUpdateNow(){
		if(m_strZUVUpdateNow.equals("")){
			m_strZUVUpdateNow=new SimpleDateFormat ("yyyy-MM-dd").format( new GregorianCalendar().getTime() );
		}
		return m_strZUVUpdateNow;
	}
	
	/**
	 * This bean's SQL-code to update row in db.
	 * If the email is updated or not, depends on the local m_blnUpdateEmail (which 
	 * is set, in turn, in method 'isNewStudent,' if the student does not have 
	 * an email address set, yet.<br />
	 * The value of dtmStudentZUVUpdate is set to the current date.<br />
	 * The value of dtmStudentZUVImmatrikuliert is updated.<br />
	 * intStudentFach2 is only updated if the current value is below 100.000.<br />
	 * (The reason is that values above 100000 are used as minor subjects internally).
	 * @return SQL-Code for update in database of fields that can be changed through update with zuv-data. These are:
	 * Vorname, Nachname, Geburtstag, Geburtsort, Geburtsname, Strasse, PLZ, Ort, ZUVImmmatrikuliert, Urlaub, Telefon, Femail, Fach1-Fach6, Fachsemester1-Fachsemester5.
	 **/
	private String toDBUpdateString(){
		return "update \"tblBdStudent\" set " +
			"\"strStudentVorname\"='" + getDBCleanString( this.getStudentVorname() ) +  "', " +
			"\"strStudentNachname\"='" + getDBCleanString( this.getStudentNachname() ) +  "', " +
			"\"dtmStudentGeburtstag\"='" + g_ISO_DATE_FORMAT.format( this.getStudentGeburtstag() ) +  "', " +
			"\"strStudentGeburtsort\"='" + getDBCleanString( this.getStudentGeburtsort() ) +  "', " +
			"\"strStudentGeburtsname\"='" + getDBCleanString( this.getStudentGeburtsname() ) +  "', " +
			"\"strStudentStrasse\"='" + getDBCleanString( this.getStudentStrasse() ) +  "', " +
			"\"strStudentPLZ\"='" + getDBCleanString( this.getStudentPLZ() ) +  "', " +
			"\"strStudentOrt\"='" + getDBCleanString( this.getStudentOrt() ) +  "', " +
			(m_blnUpdateEmail ? "\"strStudentEmail\"='" + getDBCleanString( this.getStudentEmail() ) +  "', " : "") +
			"\"strStudentStaat\"='" + getDBCleanString( this.getStudentStaat() ) +  "', " +
			"\"lngStudentZUVZiel\"=" + this.getStudentZUVZiel() +  ", " +
			"\"dtmStudentZUVUpdate\"='" + this.getZUVUpdateNow() +  "', " +
			"\"dtmStudentZUVImmatrikuliert\"='" + g_ISO_DATE_FORMAT.format(this.getStudentZUVImmatrikuliert()) +  "', " +
			"\"strStudentTelefon\"='" + getDBCleanString( this.getStudentTelefon() ) +  "', " +
			"\"blnStudentFemale\"=" + getDBBoolRepresentation(this.getStudentFemale()) +  ", " +
			"\"intStudentFach1\"=" + this.getStudentFach1() +  ", " +
			"\"strStudentUrlaub\"='" + getDBCleanString( this.getStudentUrlaub() ) +  "', " +
			( (m_intBAMA_NEBENFACH<100000) ? "\"intStudentFach2\"=" + this.getStudentFach2() +  ", " : "") +
			"\"intStudentFach3\"=" + this.getStudentFach3() +  ", " +
			"\"intStudentFach4\"=" + this.getStudentFach4() +  ", " +
			"\"intStudentFach5\"=" + this.getStudentFach5() +  ", " +
			"\"intStudentFach6\"=" + this.getStudentFach6() +  ", " +
			"\"intStudentFachsemester1\"=" + this.getStudentFachsemester1() +  ", " +
			"\"intStudentFachsemester2\"=" + this.getStudentFachsemester2() +  ", " +
			"\"intStudentFachsemester3\"=" + this.getStudentFachsemester3() +  ", " +
			"\"intStudentFachsemester4\"=" + this.getStudentFachsemester4() +  ", " +
			"\"intStudentFachsemester5\"=" + this.getStudentFachsemester5() +  ", " +
			"\"intStudentFachsemester6\"=" + this.getStudentFachsemester6() +  "" +
			" where (" + this.getSQLWhereClause() + ");";
	}

	/**
	 * @returns the String 'lngStudentpID=this.StudentPID and Matrikelnr=this.matrikelnr'. It
	 * overrides the method of class data.Student, which also filters for the SessionID.
	 **/
	public String getSQLWhereClause(){
		return "(\"lngStudentPID\"=" + this.getStudentPID() + ") and (\"strMatrikelnummer\"='" + this.getMatrikelnummer() + "')";
	}

	/**
	 * The next available StudentPID-number in database.
	 * Returns a -10 in case of error.
	 * @return maximum lngStudentPID plus one.
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

	/**
	 * Constructor-helper with suitable xml-node. The XML-node is described in 'Student.xsd,' see
	 * constructor of this class for more details.
	 * @param node Xml-Node containing all relevant tags to initialize object.
	 * @throws several Exceptions if X-Path isn't found as expected.
	 **/
	private void initByNode(Node node) throws Exception{
		this.setMatrikelnummer((shjNodeValue(node, "Matrikelnummer")));
		this.setStudentVorname((shjNodeValue(node, "StudentVorname")));
		this.setStudentNachname((shjNodeValue(node, "StudentNachname")));
		this.setStudentGeburtstag(new java.sql.Date(g_ISO_DATE_FORMAT.parse(shjNodeValue(node, "StudentGeburtstag")).getTime()));
		this.setStudentGeburtsort((shjNodeValue(node, "StudentGeburtsort")));
		this.setStudentGeburtsname((shjNodeValue(node, "StudentGeburtsname")));
		this.setStudentEmail((shjNodeValue(node, "StudentEmail")));
		this.setStudentStrasse((shjNodeValue(node, "StudentStrasse")));
		this.setStudentPLZ((shjNodeValue(node, "StudentPLZ")));
		this.setStudentOrt((shjNodeValue(node, "StudentOrt")));
		this.setStudentStaat((shjNodeValue(node, "StudentStaat")));
		this.setStudentZUVZiel(Long.parseLong(shjNodeValue(node, "StudentZUVZiel")));
		this.setStudentTelefon((shjNodeValue(node, "StudentTelefon")));
		this.setStudentFemale(Boolean.valueOf(shjNodeValue(node, "StudentFemale")).booleanValue());
		this.setStudentZUVImmatrikuliert(new java.sql.Date(g_ISO_DATE_FORMAT.parse(shjNodeValue(node, "StudentZUVImmatrikuliert")).getTime()));
		this.setStudentFach1(Integer.parseInt(shjNodeValue(node, "StudentFach1")));
		this.setStudentFach2(Integer.parseInt(shjNodeValue(node, "StudentFach2")));
		this.setStudentFach3(Integer.parseInt(shjNodeValue(node, "StudentFach3")));
		this.setStudentFach4(Integer.parseInt(shjNodeValue(node, "StudentFach4")));
		this.setStudentFach5(Integer.parseInt(shjNodeValue(node, "StudentFach5")));
		this.setStudentFach6(Integer.parseInt(shjNodeValue(node, "StudentFach6")));
		this.setStudentFachsemester1(Integer.parseInt(shjNodeValue(node, "StudentFachsemester1")));
		this.setStudentFachsemester2(Integer.parseInt(shjNodeValue(node, "StudentFachsemester2")));
		this.setStudentFachsemester3(Integer.parseInt(shjNodeValue(node, "StudentFachsemester3")));
		this.setStudentFachsemester4(Integer.parseInt(shjNodeValue(node, "StudentFachsemester4")));
		this.setStudentFachsemester5(Integer.parseInt(shjNodeValue(node, "StudentFachsemester5")));
		this.setStudentFachsemester6(Integer.parseInt(shjNodeValue(node, "StudentFachsemester6")));
		this.setStudentUrlaub((shjNodeValue(node, "StudentUrlaub")));
	}


	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 4.   C O N S T R U C T O R
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------

	/**
	 * Constructor uses a node according to SignUp-Specifictions.
	 * The expected version of a node is specified in document 'SignUp.xsd,' version
	 * XP-1-01-00 from July 24th, 2004, that ships with this application.
	 * Basically, its build is: no attributes, a Node "Student" with the following subnodes:
	 * Matrikelnummer, StudentVorname, StudentNachname, StudentGeburtstag, StudentGeburtsort, StudentStaat, StudentZUVZiel,
	 * StudentStrasse, StudentPLZ, StudentOrt, StudentTelefon, StudentFemail, StudentFach1, ..., StudentFach6,
	 * StudentFachsemester1, ..., StudentFachsemester 6.
	 **/
	public StudentZUV(Node n) throws Exception{
		this.initByNode(n);
		if(normalize(this.getStudentStaat()).equals("")) this.setStudentStaat( "Deutschland" );
	}
}