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
 * @version 0-00-01 (auto-coded)
 * change date              change author           change description
 * November 10, 2003        h. jakubzik             creation
 * March	2, 	2004		h. jakubzik				Make this course understandable for SignUp, even if it isn't really understood.
 * March	7,  2004		h. jakubzik				Bugfix in .initByNode(): "Schein=[ja|nein]" is small, no caps.
 * October 23,  2004		h. jakubzik				Added the m_lngKurstypID-feature, so that now
 *													the import has control over the coursetype that
 *													is mapped (up to now: always zero).
 * June 	21, 2005		h. jakubzik				Coursetype-mapping improved, KurstypUnivISID.
 * Oktober	11, 2005		h. Jakubzik				m_blnCompareWithoutDozentID
 */

package de.shj.UP.logic;

import java.sql.Time;

import org.w3c.dom.Node;

/**
 * This class handles the import of UnivIS lecture-data. It expects the data
 * to come in an XML-format specified in the document 'Lecture.xsd,' see constructor
 * for the versioning. <br />
 * This class is experimental. UnivIS-Version used: 1.3.
 **/
public class KursUnivIS extends de.shj.UP.logic.Kurs{

	public 	String 	m_strDebug;
	private long	m_lngKurstypID;
	private boolean m_blnNewKursChecked=false;
	private boolean m_blnIsNewKurs=false;
	private boolean m_blnAllowKurstypeGuessing = true;
	private boolean m_blnCPWithoutDozent;

	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 1.   C O N S T A N T S
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------

	// stati
	public final int UPDATE_OKAY	= 1;
	public final int ADDED_OKAY		= 2;
	public final int UPDATE_FAILED	= 3;
	public final int ADDED_FAILED	= 4;
	public final int INTERNAL_ERROR	= 5;


	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 1.   P U B L I C   M E T H O D S
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	
	/**
	 * <pre>
	 * Changes @link #isNewKurs(), so that it compares only UnivIS-Id and 
	 * SeminarID, and disregards possible differences in DozentID.
	 * 
	 * Method new in 9-03-02. If UnivIS changes the teacher-Ids, no   
	 * course will be recognized. In order to resolve the problem, 
	 * teacher recognition can be switched off.
	 * </pre>
	 */
	public void setCompareWithoutDozentID(){
		m_blnCPWithoutDozent=true;
		m_blnNewKursChecked=false;
	}
	
	
	/**
	 * <pre>
	 * This method 
	 * (i) checks if the current Kurs is a 'new Kurs,' 
	 * 	   that is to say if UnivisID/TeacherID combination
	 *     already exists (see @link #setCompareWithoutDozentID()). 
	 * 
	 *     If this combination already exists, the method 
	 * 
	 *   (ii a) updates the Kurs according
	 *          to current bean data (details: tba.). 
	 * 
	 *     If the Kurs does not yet exist, the method 
	 * 
	 * 	 (ii b) adds the Kurs according
	 *       to current bean-data (details KursID (new), tba.).<br />
	 * 
	 *     <b>Note:</b> This is experimental, UnivIS-XML-export is not documented. Especially with courses that
	 * take place more than two times a week, or as a block, there are problems. The method tries to import
	 * as much as possible. If things are not understood, 'KursTerminFreitext" is set to this:<br />
	 * UnivIS-Import: Termin nicht verstanden<br />
	 * <b>Note:</b> Since Oct 23, 2004, the Coursetype can be set using '.setKurstyp(long)'. This works
	 * only when a course is added, it does not work on an update.
	 * </pre>
	 * @return Integer naming one of the stati UPDATE_OKAY, ADDED_OKAY, UPDATE_FAILED, ADDED_FAILED, INTERNAL_ERROR.
	 **/
	public int updateSignUp(){

		try{

		  // Make this course understandable for SignUp, even if it isn't really understood.
		  if(!(this.terminSeemsOk())){
			  this.setKursTerminFreitext("UnivIS-Import: Termin nicht verstanden");
			  this.setKursTag			("");
			  this.setKursBeginn		(new Time(g_TIME_FORMAT.parse(null).getTime()));
			  this.setKursEnde			(new Time(g_TIME_FORMAT.parse(null).getTime()));

			  this.setKursTag2			("");
			  this.setKursBeginn2		(new Time(g_TIME_FORMAT.parse(null).getTime()));
			  this.setKursEnde2			(new Time(g_TIME_FORMAT.parse(null).getTime()));

			  this.setKursRaumExtern2	("");
		  }

		  if(this.isNewKurs())  return ( (this.cAdd( this.getKurstypID() )) ? this.ADDED_OKAY : this.ADDED_FAILED);
	    }catch(Exception eUpdate1){return this.INTERNAL_ERROR;}

		try{
			int intReturn = 0;
		    intReturn =  ( (cUpdate() ) ? UPDATE_OKAY : UPDATE_FAILED );
		    if(getKurstypID()!=m_KURSTYP_NULL) this.mapCoursetype(getKurstypID());
		    return intReturn;		    
		}catch(Exception updateFailed){m_strDebug+="\n\n" + updateFailed.toString(); return this.INTERNAL_ERROR;}
	}

	/**
	 * Is the current one a new Kurs? It might seem cleaner, here, to check for duplicity in 'UnivISID'-values only,
	 * than to compare SeminarID and TeacherID in addition. But what do we know about UnivIS Ids? Nothing.<br />
	 * This method sets the KursID of this object, if it is not a 'newKurs'.
	 * @return true if Kurs with current UnivISID/TeacherID/SeminarID is nonexistant in database,
	 * false otherwise.
	 * @throws Exception if connection to db is erroneous
	 */
	public boolean isNewKurs() throws Exception{
		if(m_blnNewKursChecked) return m_blnIsNewKurs;
		String strPID="";
		boolean blnReturn=false;
		
//		if(isCompareWithoutDozent())
//			strPID=lookUp("lngKursID", "tblBdKurs", "\"lngSdSeminarID\"=" + this.getSdSeminarID() + " AND \"strKursUnivISID\"='" + this.getKursUnivISID() + "'");
//		else
			strPID=lookUp("lngKursID", "tblBdKurs", 
					"\"lngSdSeminarID\"=" + this.getSdSeminarID() + 
					" AND \"strKursTitel\"='" + this.getKursTitel() + 
					"' AND \"lngDozentID\"='" + this.getDozentID() + "' AND " + 
					"\"strKursTag\"='" + this.getKursTag() + "'");
		

		if(strPID.equals("#NO_RESULT")){
			blnReturn=true;
		}else{
			blnReturn=false;
			this.setKursID(Long.parseLong(strPID));
		}
		
		m_blnNewKursChecked=true;
		m_blnIsNewKurs=blnReturn;
		return blnReturn;
	}

	public long getKurstypID(){
		return this.m_lngKurstypID;
	}

	public void setKurstypID(long lngKurstypID){
		this.m_lngKurstypID = lngKurstypID;
	}

	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 3.   P R I V A T E    M E T H O D S
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------

	private boolean isCompareWithoutDozent(){
		return m_blnCPWithoutDozent;
	}
	
	/**
	 * @return DozentID of the teacher specified through name and UnivIS-ID
	 * (and limited to all teachers with the given SeminarID).
	 * If there is no match, a NumberFormatException is thrown (since
	 * 'lookUp' tries to parse '#NO_RESULT' as a long value).
	 * @param strDozentName: last name of the teacher.
	 * @param strDozentUnivISID: UnivIS-ID of the teacher. Name AND this ID are used, because I mistrust the Univis-id.
	 * @throws several Exceptions: no teacher found throws a NumberFormat, and then there's Exceptions thrown on connection to db problems.
	 **/
	private void setDozentID(String strDozentName, String strDozentUnivISID) throws Exception{

		if(this.getDozentID()<=0){
			this.setDozentID(Long.parseLong(lookUp("lngDozentID", "tblSdDozent", "\"lngSdSeminarID\"=" + this.getSdSeminarID() + " AND \"strDozentUnivISID\"='" + strDozentUnivISID + "' AND \"strDozentNachname\"='" + strDozentName + "'")));
		}		
	}

	/**
	 * Constructor-helper with suitable xml-node. The XML-node is described in 'Lecture.xsd,' see
	 * constructor of this class for more details.<br />
	 * As of version 6.01.06 (May 3rd, 2005) this method merges the node data with what is 
	 * already in the database. In other words, before this version, the courses commitment 
	 * period was deleted if an update was called after initializing this object by its node. 
	 * Now, the method checks to see if a course with that UnivIS-Id and DozentID already exists, 
	 * loads its data, and then updates this data with information from the node.
	 * @param node Xml-Node containing all relevant tags to initialize object.
	 * @throws several Exceptions if X-Path isn't found as expected.
	 **/
	private void initByNode(Node node) throws Exception{

		// #hack: must take care of KurstypID. This is currently always set as '0' (just to take care of
		//		  other problems first. There is already the variables. Just needs a method to find ID from
		//		  UnivIS ID and name of type.

		// #hack: alle R�ume werden als 'externe R�ume' gebucht -> Raumplan ist f�rn A...
		m_lngKurstypID				=  0;	//default
		String 	strKurstypName		= "";
		String	strKurstypUnivISID	= "";
		String 	strDozentName		= "";
		String 	strDozentUnivISID	= "";

		String strTitel				= "";
		String strVoraussetzung		= "";

		this.setSdSeminarID(	( Long.parseLong(shjNodeValue(node, "SeminarID"))	));
		this.setKursUnivISID(	( shjNodeValue(node, "KursUnivISID"))	);

		try{
	 		  strKurstypName				= shjNodeValue(node, "Kurstyp");
			  strKurstypUnivISID			= shjNodeValue(node, "KurstypUnivISID");

			  strDozentName				= shjNodeValue(node, "Dozentname");
			  strDozentUnivISID			= shjNodeValue(node, "DozentUnivisID");
		}catch(Exception eNotFound){strDozentName="#";}

		// #hack: make text-field out of varchar
		strTitel				= shjNodeValue(node, "Titel");
		if(strTitel.length()>=254) strTitel = strTitel.substring(0,254);

		this.setKursTitel		( strTitel		);		
		
		// 	try to set the Kurstyp: strategy I: KurstypUnivISID; strategy II: Title==KurstypName		
		//guessKurstypID(strKurstypUnivISID, strTitel);
		
		// new Xml-Import: Name of Kurstyp handed over
		guessKurstypID(strKurstypName);//, strTitel);
//		if(this.getKurstypID()==this.m_KURSTYP_NULL){
//			guessKurstypID(strKurstypUnivISID, strKurstypName);	
//		}
		// #hack: this sets a default teacher id of 0, if teacher is not recognized...
		try{
		  this.setDozentID ( strDozentName, strDozentUnivISID );
		}catch(Exception eTeacherPlural){	
			this.setDozentID (0);
		}
		
		if(this.getDozentID()==0){
			try{
				this.setDozentID(Long.parseLong(lookUp("lngDozentID", "tblSdDozent", "\"lngSdSeminarID\"=" + this.getSdSeminarID() + " AND \"strDozentNachname\"='" + strDozentName + "'")));
			}catch(Exception eNested){}
		}
		if(this.getDozentID()==0) System.out.println("Kann Dozent '" + strDozentName + "' nicht finden :-(\n");
		// #hack: this sets a default teacher id of 0, if teacher is not recognized...

		// load information about this course from the database, if this is not a new course.
		if(!this.isNewKurs()){
			this.init(this.getSdSeminarID(), this.getKursID());
		
			// try to set creditpoints
			try {
				this.setKursCreditPts(Float.parseFloat(shjNodeValue(node, "Leistungspunkte")));
			} catch (NumberFormatException e) {}
		}
		
		this.setKursTag			( shjNodeValue(node, "Tag1")		);
                try{
                    this.setKursBeginn		( new Time(g_TIME_FORMAT.parse(shjNodeValue(node, "Beginn1")).getTime()		));
                    this.setKursEnde		( new Time(g_TIME_FORMAT.parse(shjNodeValue(node, "Ende1")).getTime()));
                    this.setKursRaumExtern1 ( shjNodeValue(node, "Bau1")		);
                }catch(Exception eTimeErrorIgnore){}
		if(this.getKursRaumExtern1().length()>50) this.setKursRaumExtern1(this.getKursRaumExtern1().substring(0, 49));

//		try{
//			this.setKursTag2		( shjNodeValue(node, "Tag2")		);
//			this.setKursBeginn2		( new Time(g_TIME_FORMAT.parse(shjNodeValue(node, "Beginn2")).getTime()		));
//			this.setKursEnde2		( new Time(g_TIME_FORMAT.parse(shjNodeValue(node, "Ende2")).getTime()		));
//			this.setKursRaumExtern2 ( shjNodeValue(node, "Raum2")		);
//		}catch(Exception e){}

		// # time such as '8:15' instead of '08:15' makes problems:
		// OFF on Nov 2, 2009: die Umstellung auf java.sql.Time
		// sollte das Problem bereits gelöst haben.
//		if(this._getKursBeginn().length()>0 && this._getKursBeginn().length()<5) this.setKursBeginn("0" + this._getKursBeginn());
//		if(this._getKursBeginn2().length()>0 && this._getKursBeginn2().length()<5) this.setKursBeginn2("0" + this.getKursBeginn2());
//		if(this._getKursEnde().length()>0 && this._getKursEnde().length()<5) this.setKursEnde("0" + this.getKursEnde());
//		if(this._getKursEnde2().length()>0 && this._getKursEnde2().length()<5) this.setKursEnde2("0" + this.getKursEnde2());


		this.setKursBeschreibung( shjNodeValue(node, "Beschreibung"));
		this.setKursLiteratur	( shjNodeValue(node, "Literatur")	);

		// #hack: make text-field out of varchar
		strVoraussetzung		= shjNodeValue(node, "Voraussetzung");
		if(strVoraussetzung.length()>=254) strVoraussetzung = strVoraussetzung.substring(0,254);
		this.setKursVoraussetzung( strVoraussetzung );

		this.setKursSchein		( (shjNodeValue(node, "Schein").equals("ja"))  		);

		// #hack: this should be taken care of in xsl-conversion
		try{
		  this.setKursStunden		( Integer.parseInt( shjNodeValue(node, "Stunden") ) );
		}catch(Exception eNaN){this.setKursStunden(2);}



		// #hack: since this uses external rooms only at the time being, make sure one is set:
		if(this.getKursRaumExtern1().equals("")) this.setKursRaumExtern1("UnivIS nicht verstanden");
		// if(this.getKursRaumExtern2().equals("") && this.getKursTag2().length()>0) this.setKursRaumExtern2("UnivIS nicht verstanden");

	}


	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 4.   C O N S T R U C T O R
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------

	/**
	 * Tries to guess, first looking up the UnivIS-ID, then 
	 * checking if the course title is equal to a Kurstyp-Bezeichnung
	 * @param strKurstypUnivISID
	 * @param strTitel
	 */
	private void guessKurstypID(String strKurstypName) {
		try {
			this.setKurstypID(Long.parseLong(lookUp("lngKurstypID", "tblSdKurstyp", "\"strKurstypBezeichnung\"='" + 
					strKurstypName + "' and \"lngSdSeminarID\"=" + this.getSdSeminarID())));
		} catch (NumberFormatException e1) {
			this.setKurstypID( this.m_KURSTYP_NULL );
		}
		
//		if(this.isAllowKurstypeGuessing()){
//			if(this.getKurstypID()==this.m_KURSTYP_NULL){
//				try {
//					this.setKurstypID(Long.parseLong(lookUp("lngKurstypID", "tblSdKurstyp", "\"strKurstypBezeichnung\"='" + 
//							strTitel + "' and \"lngSdSeminarID\"=" + this.getSdSeminarID())));
//				} catch (NumberFormatException e1) {
//					this.setKurstypID( this.m_KURSTYP_NULL );
//				}			
//			}
//		}
	}

	/**
	 * Constructor uses a node according to SignUp-Specifictions.
	 * The expected version of a node is specified in document 'Lecture.xsd,' version
	 * XP-5-14-37 from March 2004, that is _not_ public, however, since it is experimental.
	 * The nodes are, however, the following: SeminarID, KursUnivISID, Kurstyp, KurstypUnivISID (unused), Dozentname, DozentUnivisID, Tag1, Beginn1, Ende1, Raum1, Tag2, Beginn2, Ende2, Raum2, Titel,Beschreibung,Literatur,Voraussetzung,Schein,Stunden
	 **/
	public KursUnivIS(Node n) throws Exception{
		this.initByNode(n);
	}

	/**
	 * Kurstype-Guessing means that on import, courses whose title 
	 * equals a Kurstype are automatically mapped to that coursetype.	 * 
	 * @param allowKurstypeGuessing .
	 */
	public void setAllowKurstypeGuessing(boolean allowKurstypeGuessing) {
		this.m_blnAllowKurstypeGuessing = allowKurstypeGuessing;
	}

	/**
	 * Kurstype-Guessing means that on import, courses whose title 
	 * equals a Kurstype are automatically mapped to that coursetype.
	 * @return Returns 
	 */
	public boolean isAllowKurstypeGuessing() {
		return m_blnAllowKurstypeGuessing;
	}
}