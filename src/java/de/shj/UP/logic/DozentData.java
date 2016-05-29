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
 * code structure
 *
 * 1. declarations,				line  92
 *
 * 2. properties,				line 102
 *
 * 3. methods general,			line 123
 *
 * 4. methods certificate		line 239
 *
 * 5. constructors				line 339
 *
 * @version 0-00-01 (auto-coded)
 * change date              change author           change description
 * November 2003            h. jakubzik             initial creation
 * February 2004			h. jakubzik				getDozentLectures now selects lngKurstypID as well (needed in HTMLDozent)
 * February 2004			h. jakubzik				getDozentLectures now selects ECTSCreditPts as well.
 * March 11,2004			h. jakubzik				methods for clientauth with digital id:
 * March 12,2004			h. jakubzik				Fehlerkorrektur
 * 													Handing class a constructor
 *													Re-structure code segments
 *													add cert-utilities (hasCert, hasValidCert).
 * April 10, 2004			h. jakubzik				method.update() now also updates Access-Level and IP (this is
 *													needed for /Config/ web-frontend. To be #hack ed here is that
 *													update does not yet set dirty to false.
 * May   15, 2005			h. jakubzik				.update() now also update blnLehrend.
 * 
 * August 21, 2005			h. jakubzik				.vCard()
 * 2011-1-5					h. jakubzik				CodeSchau:
 * 													- setSeminarID auskommentiert, lokales
 * 													  Feld überall ausgetauscht gegen Eigenschaft
 * 													  der Superklasse.
 */
package de.shj.UP.logic;

import java.security.cert.X509Certificate;
import java.sql.ResultSet;
//import com.shj.signUp.beans.algorithm.KursteilnehmerIterator;

/**
 * <pre>
 * Legacy class.
 * 
 * Ideen: vCard einbinden
 * TODO:  Auskommentiertes löschen, Zertifikat-Management löschen?
 * 
 * Usful are: @link #hasTime(String, String, String, String, boolean), @link #vCard(), and 
 * the methods concerning certificates.
 * </pre>
 **/
public class DozentData extends de.shj.UP.data.Dozent{

/////////////////////////////////////
// 1.   D E C L A R A T I O N S
/////////////////////////////////////
	
	private static final long serialVersionUID = -7102294401572715866L;
	public 	String m_strDebug;
//	private long m_lngSeminarID;	// W H Y ?	(Auskommentiert am 5.1.2011)
//	private KursteilnehmerIterator m_KI = null;


/////////////////////////////////////
// 2. P R O P E R T I E S
/////////////////////////////////////
	
//	/**
//	 * @return
//         * @deprecated Sorry, not in OS Variante (yet)
//	 */
//	public KursteilnehmerIterator getKursteilnehmerIterator() throws Exception{
//		if(m_KI==null) m_KI = new KursteilnehmerIterator(getSdSeminarID(), getDozentID());
//		return m_KI;
//	}
//	
//	/**
//	 * @return
//	 * @throws Exception
//         * @deprecated Sorry, not in OS Variante (yet)
//	 */
//	public KursteilnehmerIterator getNewKursteilnehmerIterator() throws Exception{
//		m_KI=null;
//		return getKursteilnehmerIterator();
//	}
	
	/**<pre>
	 * vCard in version 2.1, like this:
	 * BEFIN:VCARD
	 * VERSION:2.1
	 * N:lastname;firstname
	 * FN:firstname;lastname
	 * EMAIL:email
	 * TEL;TYPE=WORK:work-telephone
	 * TEL;TYPE=HOME:home-telephone
	 * BDAY:birthday
	 * END:VCARD
	 * </pre>
	 * @return vCard String
	 */
	public String vCard(){
		return "BEGIN:VCARD\n" +
		 		"VERSION:2.1\n" +
				"N:" + getDozentNachname() + ";" + getDozentVorname() + "\n" +
				"FN:" + getDozentVorname() + " " + getDozentNachname() +";\n" +
				"EMAIL:" + getDozentEmail() + "\n" +
				"TEL;TYPE=WORK:" + getDozentTelefon() + "\n" + 
				"TEL;TYPE=HOME:" + getDozentTelefonPrivat() + "\n" +
				"BDAY:" + getDozentGebdatum() + "\n" +
				"END:VCARD";
	}
	
	public String LDIF(){
		return "dn: cn=" + getDozentVorname() + " " + getDozentNachname() + ",mail=" + getDozentEmail() + "\n" +
			"objectclass: top\n" +
			"objectclass: person\n" +
			"objectclass: organizationalPerson\n" +
			"objectclass: inetOrgPerson\n" +
			"objectclass: mozillaAbPersonAlpha\n" +
			"givenName: " + getDozentVorname() + "\n" +
			"sn: " + getDozentNachname() + "\n" +
			"cn: " + getDozentVorname() + " " + getDozentNachname() + "\n" +
			"mail: " + normalize(getDozentEmail()) + "\n" +
			"modifytimestamp: 0Z\n" + 
			"telephoneNumber: " + normalize(getDozentTelefon()) + "\n" +
			"homePhone: " + normalize(getDozentTelefonPrivat()) + "\n" +
			"mobile: \n" +
			"homeStreet: " + normalize(getDozentStrasse()) + "\n" +
			"mozillaHomeLocalityName: " + normalize(getDozentOrt()) + "\n" +
			"mozillaHomePostalCode: " + normalize(getDozentPLZ()) + "\n" +
			"mozillaHomeCountryName: Deutschland\n" + 
			"street: Kettengasse 12\n" +
			"l: Heidelberg\n" +
			"postalCode: 69117\n" +
			"c: Deutschland\n" +
			"title: " + normalize(getDozentTitel()) + "\n" +
			"mozillaWorkUrl: http://www.as.uni-heidelberg.de\n" +
			"mozillaHomeUrl: " + normalize(getDozentHomepage()) + "\n";
	}
	
    /**
     * @param lngSeminarID: id of seminar.
     * @deprecated
     **/
//	public void setSeminarID(long lngSeminarID){
//		this.m_lngSeminarID=lngSeminarID;
//	}
//
	/**
	 * @return id of seminar.
	 **/
//	public long getSeminarID(){
//		return super.getSdSeminarID();
//	}

/////////////////////////////////////
//	 3. D B - U T I L I T I E S
/////////////////////////////////////
	
	/**<pre>
	 * Executes update of fields that can be changed through web-interface 
	 * by admin herself/himself. These are:
	 * 
	 * - last- and first name, 
	 * - email, 
	 * - homepage, 
	 * - phone, 
	 * - title, 
	 * - password, 
	 * - office-hours, 
	 * - postfach (pigeon hole),
	 * - interests, 
	 * - Access-Level and IP. 
	 * - address
	 * - private telephone
	 * - Bereich
	 * 
	 * Does not touch any of the certificate-properties!
	 * 
	 * @return 'true' for a successful update, 'false' for an sql-error.
	 * @throws Exception 
	 * @throws Exception 
	 * @throws Exception when connection to database is erroneous.
	 **/
	public boolean update() throws Exception{
		return sqlExe("update \"tblSdDozent\" set " +
			"\"strDozentNachname\"='" + getDBCleanString(this.getDozentNachname()) +  "', " +
			"\"strDozentVorname\"='" + getDBCleanString(this.getDozentVorname()) +  "', " +
			"\"strDozentTitel\"='" + getDBCleanString(this.getDozentTitel()) +  "', " +
			"\"strDozentPasswort\"='" + getDBCleanString(this.getDozentPasswort()) +  "', " +
			"\"strDozentSprechstunde\"='" + getDBCleanString(this.getDozentSprechstunde()) +  "', " +
			"\"strDozentZimmer\"='" + getDBCleanString(this.getDozentZimmer()) +  "', " +
			"\"strDozentEmail\"='" + getDBCleanString(this.getDozentEmail()) +  "', " +
			"\"strDozentTelefon\"='" + getDBCleanString(this.getDozentTelefon()) +  "', " +
			"\"strDozentHomepage\"='" + getDBCleanString(this.getDozentHomepage()) +  "', " +
			"\"strDozentStrasse\"='" + getDBCleanString(this.getDozentStrasse()) +  "', " +
			"\"strDozentPLZ\"='" + getDBCleanString(this.getDozentPLZ()) +  "', " +
			"\"strDozentOrt\"='" + getDBCleanString(this.getDozentOrt()) +  "', " +
			"\"strDozentTelefonPrivat\"='" + getDBCleanString(this.getDozentTelefonPrivat()) +  "', " +
			"\"strDozentInteressen\"='" + getDBCleanString(this.getDozentInteressen()) +  "', " +
			"\"intDozentAccessLevel\"=" + this.getDozentAccessLevel() +  ", " +
			"\"strDozentIP\"=" + dbNormal(this.getDozentIP()) +  ", " +
			"\"blnDozentLehrend\"=" + getDBBoolRepresentation(this.getDozentLehrend()) +  ", " +
			"\"strDozentBereich\"='" + getDBCleanString(this.getDozentBereich()) +  "', " +
			"\"strDozentPostfach\"='" + getDBCleanString(this.getDozentPostfach()) +  "'" +
			" where (" + "\"lngSdSeminarID\"=" + this.getSdSeminarID() + " AND "  + 
			"\"lngDozentID\"=" + this.getDozentID() + ");");
	}

    /**<pre>
     * Answer to the question if this teacher has time 
     * at specified day/start/stop in specified semester 
     * (blnPlanung) excepting course KursID from check.
     * </pre>
     * @return boolean value indicating if current Dozent has time at the specified moment. This is a method to avoid kvv-error type 1 during registration
     * (type 1 means: teacher registers two courses at the same or overlapping time).<br />
     * The parameters are an inheritance as is the entire method; they are hardly parsed. The input is expected
     * to make sense (needs #hack ing). <br />
     * Also, it should be discussed (and then #hack ed), what overlapping means. Currently, 10.15 overlaps with itself ('<=' instead of '<').
     * This has lead to problems sporadically, and although I still think that it is justified (hj, Nov 2003), I also think that more is gained than
     * lost when this is loosened up.
     * @param strKursID_IN: id of course or 'ADD.' If it's NOT 'ADD,' then the course specified through KursID is disregarded (necessary for course-change).
     * @param strTag_IN: what day in the week are we talking about? (Not parsed; language specific; #hack)
     * @param strStart: what's the start-time? (Not parsed, format 'hh:mm' expected)
     * @param strStop: what's the end-time? (Not parsed, format 'hh:mm' expected)
     * @param blnPlanung: check for course-times of current semester (blnPlanung=false) or a future semester (blnPlanung=true)?
     * @return 'true,' if there seems no overlap with another course by the same teacher,
     * 'false' otherwise. Currently, there are no specifications as to the overlapping course.
     * @exception throws if no connection to db or sqlQuery fails (not caught).
     * @version >=2.2i
     **/
	public boolean hasTime(String strKursID_IN, String strTag_IN, String strStart, String strStop, boolean blnPlanung) throws Exception{
		String strSQL = "SELECT " +
			"a.\"strKursTag\" AS Tag " +
			"FROM " +
			"\"tblBdKurs\" a " +
			"WHERE " +
			"a.\"lngSdSeminarID\"=" + this.getSdSeminarID() + " AND " +
			"a.\"lngDozentID\"=" + this.getDozentID() + " AND " +
			"a.\"blnKursPlanungssemester\"=" + getDBBoolRepresentation(blnPlanung) + " AND " + ((strKursID_IN.equals("ADD")) ? "" : ("a.\"lngKursID\"<>" + strKursID_IN + " AND ")) +

			"((" +												//1. Tag
			  "a.\"strKursTag\"='" + strTag_IN + "' AND " +
			  "((a.\"dtmKursBeginn\">='" + strStart + "' AND " +
			  "a.\"dtmKursBeginn\"<='" + strStop + "') OR " +			//Beginn zw. Start & Stop
			  "(a.\"dtmKursEnde\">='" + strStart + "' AND " +
			  "a.\"dtmKursEnde\"<='" + strStop + "') OR " +				//Ende zw. Start & Stop
			  "(a.\"dtmKursBeginn\"<'" + strStart + "' AND " +
			  "a.\"dtmKursEnde\">'" + strStop + "'))" +					//Beginn<Start & Ende>Stop
			 ") OR (" + 										//2. Tag
			  "a.\"strKursTag2\"='" + strTag_IN + "' AND " +
			  "((a.\"dtmKursBeginn2\">='" + strStart + "' AND " +
			  "a.\"dtmKursBeginn2\"<='" + strStop + "') OR " +			//Beginn zw. Start & Stop
			  "(a.\"dtmKursEnde2\">='" + strStart + "' AND " +
			  "a.\"dtmKursEnde2\"<='" + strStop + "') OR " +			//Ende zw. Start & Stop
			  "(a.\"dtmKursBeginn2\"<'" + strStart + "' AND " +
			  "a.\"dtmKursEnde2\">'" + strStop + "'))" +				//Beginn<Start & Ende>Stop
			 "));";

		ResultSet rst=sqlQuery(strSQL);

		boolean blnReturn = (!rst.next());

		rst.close();
		return blnReturn;
	}

//    /**
//     * @return ResultSet with all lectures of current teacher ordered by 'intKurstypSequence.'
//     * @param blnPlanung: future semester (blnPlanung=true), or current semester (blnPlanung=false)?
//     * @deprecated as of version 6. Please use getDozentLectureList instead
//     **/
//    protected ResultSet getDozentLectures(boolean blnPlanung) throws Exception{
//		return sqlQuery("SELECT " +
//		  "t.\"lngKurstypID\", " +
//		  "t.\"intKurstypSequence\", " +
//		  "t.\"strKurstypBezeichnung\", " +
//		  "k.\"strKursTag\", " +
//		  "k.\"strKursTerminFreitext\", " +
//		  "k.\"dtmKursBeginn\", " +
//		  "k.\"strKursTitel\", " +
//		  "k.\"lngDozentID\", " +
//		  "k.\"lngKursID\", " +
//		  "x.\"sngKursCreditPts\", " +
//		  "k.\"blnKursPlanungssemester\" " +
//		"FROM \"tblSdKurstyp\" t, \"tblBdKurs\" k, \"tblBdKursXKurstyp\" x  " +
//		"WHERE " +
//		  "(" +
//			"(t.\"lngKurstypID\" = x.\"lngKurstypID\") AND " +
//			"(t.\"lngSdSeminarID\" = x.\"lngSeminarID\") AND " +
//			"(k.\"lngKursID\" = x.\"lngKursID\") AND " +
//			"(k.\"lngSdSeminarID\" = x.\"lngSeminarID\") AND " +
//			"(k.\"lngDozentID\"=" + this.getDozentID() + ") AND " +
//			"(k.\"blnKursPlanungssemester\"=" + getDBBoolRepresentation(blnPlanung) + ") AND " +
//			"(t.\"lngSdSeminarID\" = " + this.getSdSeminarID() + ") AND " +
//			"(k.\"lngSdSeminarID\" = " + this.getSdSeminarID() + ")" +
//		  ")" +
//		"ORDER BY t.\"intKurstypSequence\" ASC;");
//	}

    /**
     * <pre>
     * A ResultSet with 
     * - <Code>KurstypBezeichnung</Code> and 
     * - <Code>tblBdKursXKurstyp.sngKursCreditPts</Code> and 
     * - <Code>tblBdKurs.*</Code>.
     * </pre>
     * @return ResultSet with all lectures of current teacher'
     * @param blnPlanung: future semester (blnPlanung=true), or current semester (blnPlanung=false)?
     * @param strOrderSQL: sql String, beginning with "order by ..".
     * @see #getDozentLectureList(boolean)
     **/
    public ResultSet getDozentLectureListOrdered(boolean blnPlanung, String strOrderSQL) throws Exception{
		return sqlQuery("SELECT " +
		  "t.\"lngKurstypID\", " +
		  "t.\"intKurstypSequence\", " +
		  "t.\"strKurstypBezeichnung\", " +
		  "k.*, " +
		  "x.\"sngKursCreditPts\" as cpts " +
		"FROM \"tblSdKurstyp\" t, \"tblBdKurs\" k, \"tblBdKursXKurstyp\" x  " +
		"WHERE " +
		  "(" +
			"(t.\"lngKurstypID\" = x.\"lngKurstypID\") AND " +
			"(t.\"lngSdSeminarID\" = x.\"lngSeminarID\") AND " +
			"(k.\"lngKursID\" = x.\"lngKursID\") AND " +
			"(k.\"lngSdSeminarID\" = x.\"lngSeminarID\") AND " +
			"(k.\"lngDozentID\"=" + this.getDozentID() + ") AND " +
			"(k.\"blnKursPlanungssemester\"=" + getDBBoolRepresentation(blnPlanung) + ") AND " +
			"(t.\"lngSdSeminarID\" = " + this.getSdSeminarID() + ")" +
		  ") " + strOrderSQL + ";");
	}    
    
    /**
     * @return ResultSet with all lectures of current teacher ordered by 'intKurstypSequence.'
     * @param blnPlanung: future semester (blnPlanung=true), or current semester (blnPlanung=false)?
     * @see #getDozentLectureListOrdered(boolean, String)
     **/
    public ResultSet getDozentLectureList(boolean blnPlanung) throws Exception{
		return this.getDozentLectureListOrdered(blnPlanung, "ORDER BY t.\"intKurstypSequence\" ASC"); 
	}
    
//////////////////////////////////////////////////////////////////////////
//  4. C E R T I F I C A T E   M  A N A G E M E N T
//////////////////////////////////// /////////////////////////////////////
	/**
	 * <pre>
	 * The following properties of the certificate are currently stored:
	 * 
	 * - IssuerDN (Name/s),
	 * - SerialID,
	 * - SubjectDN (Name/s)
	 * 
	 * All of these properties are saved as String values.
	 * 
	 * In addition, the two flags 'revoked' and 'validated' are
	 * set to false and true respectively.
	 * 
	 * Please note that there are no security restrictions within the 'logic' package.
	 * These must be taken care of in jsp (or .NET or whatever) wrapping applications.
	 * </pre> 
	 * @return true if certificate has successfully been saved in database,
	 * false in case of error (either the update didn't work, or the connection
	 * to db was altogether erroneous.
	 * @param cert: certificate whose values are mapped to this teacher.
	 **/
	public boolean setCert(X509Certificate cert){
		this.setDozentCertRevoked	( false );
		this.setDozentCertValidated	( true	);
		this.setDozentCertSerialID	( cert.getSerialNumber().toString() );
		this.setDozentCertIssuerDN  ( cert.getIssuerDN().getName()		);
		this.setDozentCertSubjectDN ( cert.getSubjectDN().getName()		);

		try{
		  return this.updateCert();
	    }catch(Exception dbErr){return false;}
	}

	/**
	 * @return true if deletion of current teacher's certificate in SignUp was successful.<br />
	 * <b>Please note that there are no security restrictions within the 'logic' package.
	 * These must be taken care of in jsp (or .NET or whatever) wrapping applications.</b>
	 **/
	public boolean deleteCert(){
		this.setDozentCertRevoked	( false );
		this.setDozentCertValidated	( false	);
		this.setDozentCertSerialID	( "" );
		this.setDozentCertIssuerDN  ( "" );
		this.setDozentCertSubjectDN ( "" );
		try{
		  return this.updateCert();
	    }catch(Exception dbErr){return false;}

	}

	/**
	 * @return true if revocation of current teacher's certificate in SignUp was successful.
	 * Revokation simply means setting 'revoked' to 'true.'<br />
	 * <b>Please note that there are no security restrictions within the 'logic' package.
	 * These must be taken care of in jsp (or .NET or whatever) wrapping applications.</b>
	 **/
	public boolean revokeCert(){
		this.setDozentCertRevoked	( true  );
		try{
		  return this.updateCert();
	    }catch(Exception dbErr){return false;}

	}

	/**
	 * <pre>
	 * 	The reason why this method is separate from public 'update' above is, obviously,
	 *  that otherwise a change of, say, the room number from a login that uses IP-identification
	 *  instead of digital id would erase the certificate from SignUp (its values empty).
	 * </pre>
	 *	@return true if current certificate-values are stored successfully in database,
	 *  false if they are not.<br />
	 *  @throws Exceptions when connection to database is broken.
	 **/
	private boolean updateCert() throws Exception{
		return this.sqlExe("update \"tblSdDozent\" set " +
			"\"blnDozentCertRevoked\"=" + getDBBoolRepresentation(this.getDozentCertRevoked()) +  ", " +
			"\"blnDozentCertValidated\"=" + getDBBoolRepresentation(this.getDozentCertValidated()) +  ", " +
			"\"strDozentCertSerialID\"='" + getDBCleanString(this.getDozentCertSerialID()) +  "', " +
			"\"strDozentCertIssuerDN\"='" + getDBCleanString(this.getDozentCertIssuerDN()) +  "', " +
			"\"strDozentCertSubjectDN\"='" + getDBCleanString(this.getDozentCertSubjectDN()) +  "'" +
			" where (" + "\"lngSdSeminarID\"=" + this.getSdSeminarID() + " AND "  + 
			"\"lngDozentID\"=" + this.getDozentID() + ");");
	}

	/**
	 * @return true if there's a non-null, non-empty SerialID of a certificate stored for the
	 * current teacher. This does not say the certificate is 'validated' or not 'revoked.'
	 **/
	public boolean hasCert(){
		try{
		  return ( !(this.getDozentCertSerialID().equals("")) );
		}catch(Exception eNull){return false;}
	}

	/**
	 * @return true if hasCert is true, the certificate is validated and not revoked.
	 **/
	public boolean hasValidCert(){
		return ( this.getDozentCertValidated() && !(this.getDozentCertRevoked()) && this.hasCert() );
	}


//////////////////////////////////////////////////////////////////////////
//  5. Construction
/////////////////////////////////////////////////////////////////////////


	/**
	 * Empty constructor, for backwards compatibility in jsp
	 **/
	public DozentData(){}

	/**
	 * @see com.shj.data.Dozent
	 **/
	public DozentData(long lngSdSeminarID, long lngDozentID) throws Exception{
		this.init(lngSdSeminarID, lngDozentID);
	}

}