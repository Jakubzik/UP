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
 *
 * code structure:
 *
 * 1. declarations						line 113
 *
 * 2. constants							line 150
 *
 * 3. public methods of interest		line 180 (cUpdate, cAdd, cDelete etc.)
 *
 * 4. private methods of interes		line 483
 *
 * 5. html combo utils (uninterest.)	line 691
 *
 * 6. constructors						line 994
 *
 * @version 0-00-01
 * change date              change author           change description
 * November 2003            h. jakubzik             initial creation
 *
 * February 2004			h. jakubzik				changed access of method "mapCoursetype"
 *													from private to public (needed in 'Staff/KvvMulti.jsp').
 *
 * February 2004		    h. jakubzik				changed access of method "getKurstypenRst"
 *													from private to public (needed in 'Staff/KvvMulti.jsp').
 *
 * March 1, 2004			h. jakubzik				added 'UnivISID' to toDBAddString(); did NOT add UnivIS ID
 *													to dbUpdateString.
 *
 * Jan  16, 2005			h. jakubzik				a Kurs now also has CreditPts; cAdd and cUpdate now set this 
 * 													value.
 * 
 * Jan  17, 2005			h. jakubzik				a Kurs now has a Dozent.
 * 
 * May  08, 2005			h. jakubzik				smoothed coursetype-mapping: Coursetypes with the ID=0 (KURSTYP_NULL)
 * 													now officially count as unmapped courses.
 * 
 * May  14, 2005			h. jakubzik				.()hasOpenCommitments + effects in cDelete
 * 
 * Aug  16, 2005			h. jakubzik				.cUpdate and cAdd now include KursCustom1 
 * 
 * April 15, 2006			h. jakubzik				.dropCommitments() added.
 * 
 * April 18, 2006			h. jakubzik				.isDuringCommitmentChange
 * 
 * 2011-1-7 Codeschau
 * @TODO: Termineingabe generell umarbeiten auf JavaScript und Plausibilität;
 * Diese Klasse dabei komplett ersetzen
 */
package de.shj.UP.logic;

import java.sql.ResultSet;
import de.shj.UP.data.Dozent;

/** This class is intended as a temporary utility-wrapper around courses and coursetypes.
 *  There is no deeper plan as yet (Nov 2003).
 *  If nothing develops from this, the class should be put into the package 'HTML' and be
 *	renamed to 'HtmlKurs.'<br /><br />
 *
 *  Goals and methods so far:<br /><br />
 *
 *	Display coursetypes per course in web-interface				|	getKurstypen<br />
 *																|	 getKurstypenRst<br />
 *<br /><br />
 *  Get time-format (19:30) through substring(0,5) 				|	_getKursBeginn/Ende<br />
 *  (This needs #hacking, better use SimpleDateFormat).<br />
 *<br /><br />
 *  Get Termin as text (either 1, 2 or free-text) and set 		|	getTermin<br />
 *  the 'TerminOpt'-property accordingly						|<br />
 *<br /><br />
 *  Delete Course AND its reference in tblKursXKurstyp			|	cDelete<br />
 *<br /><br />
 *	Update only if termin & room seem fine						|	cUpdate<br />
 *<br /><br />
 *  Checks for room-configuration with the help of this package's<br />
 *  helper class 'RaumData.' Error-possibilities are:<ul>
 *	<li>public final byte ERR_ROOM_TERMIN_NOT_OKAY				=-1;	<=> termin setting not okay (cf. method terminSeemsOk())
 *	<li>public final byte ERR_ROOM_1_SET_WITH_VAR_TERMIN		=1;		<=> getKursRaum() is not empty although freetext date/time
 *	<li>public final byte ERR_ROOM_2_SET_WITH_VAR_TERMIN		=2;		<=> getKursRaum2()is not empty although freetext date/time
 *	<li>public final byte ERR_ROOM_EXT_1_EMPTY_ON_VAR_TERMIN	=5;		<=> KursRaumExtern1 is empty although freetext date/time
 *	<li>public final byte ERR_ROOM_EXT_1_LOCAL					=3;		<=> KursRaumExtern1 is a local room, not an external one.
 *	<li>public final byte ERR_ROOM_EXT_2_LOCAL					=4;		<=> KursRaumExtern2 is a local room, not an external one.
 *	<li>public final byte ERR_ROOM_2_SET_WITH_ONE_TERMIN		=6;		<=> KursRaum2 not empty although 'TERMIN_ONE_ONLY'
 *	<li>public final byte ERR_ROOM_EXT_2_NOT_EMPTY_ON_VAR_TERMIN=7;		<=> External room 2 not empty on 'TERMIN_VARIABLE'
 *	<li>public final byte ERR_ROOMS_EMPTY_ON_ONE_TERMIN			=8;		<=> Setting 'TERMIN_ONE_ONLY': rooms KursRaum1 and Extern1 both empty
 *	<li>public final byte ERR_ROOM_1_OCCUPIED					=9;		<=> Room 1 is occupied meanwhile
 *	<li>public final byte ERR_NO_ROOM_1_SET						=10;	<=> KursRaum and RaumExtern1 are both empty.
 *	<li>public final byte ERR_NO_ROOM_2_SET						=11;	<=> KursRaum2 and RaumExtern2 are both empty.
 *	<li>public final byte ERR_ROOM_2_OCCUPIED					=12;	<=> Room 2 is occupied meanwhile
 *  <li>public final byte ERR_ROOM_BOOKING_INTERNAL				=13;	<=> An internal error occurred during check.
 *  </ul><br /><br />
 *  There's also the utitlity method to retrieve the termin
 *  (date/time)-settings, getTerminOpt(). it can take these
 *  values:<ul>
 *	<li>public final byte TERMIN_NOT_INITIALIZED 	=-1;
 *	<li>public final byte TERMIN_ONE_ONLY		 	=1;
 *	<li>public final byte TERMIN_TWO_DATES		 	=2;
 *	<li>public final byte TERMIN_VARIABEL		 	=3; </ul>
 * <br /><br />
 * As of @version 6.01.06 (May 8, 2005), a coursetype with the Id 0 (=KURSTYP_NULL) is oficially 
 * regarded as 'no mapping.' If the frontend tries to delete the last mapping, it is actually 
 * internally set to 0. 0-coursetypes are not displayed as mapped by the frontend anymore.
 */
public class Kurs extends de.shj.UP.data.Kurs{

///////////////////////////////////////////////////
// Felder
///////////////////////////////////////////////////	
	private static final long serialVersionUID = 1314061641986614344L;

	public String m_strDebug;						

	private long   m_lngKurstypAddNew = -7;
	private String m_strKurstypen="#";
	private String m_strTermin;
	private final String   m_TITLE_DEFAULT = " ---- ";
	private final String[] m_strTage = { "Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag", "Sonntag" };
	private final String[] m_strMins = { "00", "15", "30", "45" };

	private final byte KURSID_NEW_COURSE = -1;
	private final byte HOURS_OFFSET=8;				// for combos: when can courses start, when can they stop?
	private final byte HOURS_LIMIT=22;				// for combos: when can courses start, when can they stop?

	public long	   m_KURSTYP_NULL=0;			// counts as 'not mapped'
	
	private String m_strCboTag1="#";
	private String m_strCboTag2="#";

	private boolean m_blnAllowDeletionWithOpenCommitments=false;
	
	private String m_strCboStartHour1="#";
	private String m_strCboStartHour2="#";
	private String m_strCboStartMinute1="#";
	private String m_strCboStartMinute2="#";

	private String m_strCboEndHour1="#";
	private String m_strCboEndHour2="#";
	private String m_strCboEndMinute1="#";
	private String m_strCboEndMinute2="#";
	
	private int m_intKurstypenCount=0;

	private byte m_bytTerminOpt=-1;

	private RaumData m_objRoom;
	private Dozent   m_objDozent;

///////////////////////////////////////////////////
//  Konstanten
///////////////////////////////////////////////////	
	public final byte TERMIN_NOT_INITIALIZED 	=-1;
	public final byte TERMIN_ONE_ONLY		 	=1;
	public final byte TERMIN_TWO_DATES		 	=2;
	public final byte TERMIN_VARIABEL		 	=3;

	public byte m_bytRoomErr=0;

	public final byte ERR_ROOM_TERMIN_NOT_OKAY				=-1;	// termin setting not okay (cf. method terminSeemsOk())
	public final byte ERR_ROOM_1_SET_WITH_VAR_TERMIN		=1;		// getKursRaum() is not empty although freetext date/time
	public final byte ERR_ROOM_2_SET_WITH_VAR_TERMIN		=2;		// getKursRaum2()is not empty although freetext date/time
	public final byte ERR_ROOM_EXT_1_EMPTY_ON_VAR_TERMIN	=5;		// KursRaumExtern1 is empty although freetext date/time
	public final byte ERR_ROOM_EXT_1_LOCAL					=3;		// KursRaumExtern1 is a local room, not an external one.
	public final byte ERR_ROOM_EXT_2_LOCAL					=4;		// KursRaumExtern2 is a local room, not an external one.
	public final byte ERR_ROOM_2_SET_WITH_ONE_TERMIN		=6;		// KursRaum2 not empty although 'TERMIN_ONE_ONLY'
	public final byte ERR_ROOM_EXT_2_NOT_EMPTY_ON_VAR_TERMIN=7;		// External room 2 not empty on 'TERMIN_VARIABLE'
	public final byte ERR_ROOMS_EMPTY_ON_ONE_TERMIN			=8;		// Setting 'TERMIN_ONE_ONLY': rooms KursRaum1 and Extern1 both empty
	public final byte ERR_ROOM_1_OCCUPIED					=9;		// Room 1 is occupied meanwhile
	public final byte ERR_NO_ROOM_1_SET						=10;	// KursRaum and RaumExtern1 are both empty.
	public final byte ERR_NO_ROOM_2_SET						=11;	// KursRaum2 and RaumExtern2 are both empty.
	public final byte ERR_ROOM_2_OCCUPIED					=12;	// Room 2 is occupied meanwhile
	public final byte ERR_ROOM_BOOKING_INTERNAL				=13;	// An internal error occurred during check.

///////////////////////////////////////////////////
// Wichtige Öffentliche Eigenschaften und Methoden 
///////////////////////////////////////////////////	
	
///////////////////////////////////////////////////
// Eigenschaften
	
	/**
	 *  Raum -- bei zwei Räumen durch Komma getrennt.
	 *  Wenn etwas schiefläuft "#error".
	 **/
	public String getRaumName(){

		if (this.getTerminOpt()==TERMIN_ONE_ONLY) return (isEmpty(this.getKursRaum()) ? this.getKursRaumExtern1() : this.getKursRaum());
		
		if (this.getTerminOpt()==TERMIN_TWO_DATES)
			return (isEmpty(this.getKursRaum()) ? this.getKursRaumExtern1() : this.getKursRaum()) + ", " +
					(isEmpty(this.getKursRaum2()) ? this.getKursRaumExtern2() : this.getKursRaum2());
		
		if (this.getTerminOpt()==TERMIN_VARIABEL) return this.getKursRaumExtern1();
		
		return "#error: not initialized?";
	}
	
	/**
	 *  Text-String (no Html) with date/time of current course.
	 *  @return A String with the current course's time(s). This time is either
	 *  one appointment per week (case 1), two appointments per week (case 2)
	 *  or a free text that cannot be interpreted by the program (case 3).
	 *  The 'Termintype' is also set when this method is called. The String that
	 *  is returns looks like this:<br/>
	 *  <ul><li>case 1: 'Monday 14:00-15:00'<li>case 2: 'Tuesday 14:00-15:00; Wednesday 15:00-16:00'<li>case 3: whatever text is stored in the field 'getKursTerminFreitext' in the database.</ul>
	 */
	public String getTermin(){
		
		if( this.m_bytTerminOpt==TERMIN_NOT_INITIALIZED ){
			
			if( isNoDay(this.getKursTag()) ){
				this.m_bytTerminOpt		= TERMIN_VARIABEL;
				this.m_strTermin		= this.getKursTerminFreitext();
				
			} else {
				
				if( isNoDay(this.getKursTag2()) ) {
					this.m_bytTerminOpt		= TERMIN_ONE_ONLY;
					this.m_strTermin		= this.getKursTag() + " " + this._getKursBeginn() + " - " + this._getKursEnde();
					
				} else {
					this.m_bytTerminOpt		= TERMIN_TWO_DATES;
					this.m_strTermin		= this.getKursTag() + " " + this._getKursBeginn() + " - " + this._getKursEnde() + "; " +
					this.getKursTag2() + " " + this._getKursBeginn2() + " - " + this._getKursEnde2();
				}
			}
		}
		return normalize(m_strTermin);
	}
	
	/**
	 *  Get text-String with date/time (i) of this course.
	 *  @return A String with _one of_ the current course's times. This time is either
	 *  the first or second time, according to parameter i handed over. When this 'termin' is
	 *  a free text, this text is handed back no matter what i. If there is no second appointment,
	 *  an empty String is returned for 'getTermin(2).'
	 *  is returns looks like this:<br/>
	 *  <ul><li>case 1: 'Monday 14:00-15:00'<li>case 2: 'Tuesday 14:00-15:00'<li>case 3: whatever text is stored in the field 'getKursTerminFreitext' in the database.</ul><br />
	 *  @param bytWhichTermin: Either one or two, depending on whether the first or the second date is to be handed back.
	 */
	public String getTermin(byte bytWhichTermin){
		
		String strReturn="";
		
		if(this.getTerminOpt()==TERMIN_VARIABEL){
			strReturn = getTermin();
		}else{
			
			switch(bytWhichTermin){
			case 1:
				strReturn = this.getKursTag() + " " + this._getKursBeginn() + " - " + this._getKursEnde();
				break;
				
			case 2:
				strReturn = this.getKursTag2() + " " + this._getKursBeginn2() + " - " + this._getKursEnde2();
				break;
			}
		}
		return normalize(strReturn);
	}
	
	/**
	 *  Text-String (no Html) with coursetypes of this course.
	 *  @return names of coursetypes of current course. If an internal error occurs,
	 *  the String '#Courstypes unknown' is handed back.
	 *  The String is just a comma-separated list of all coursetypes until I find a
	 *  better solution for this. (Obviously, some #hack ing is needed).
	 */
	public String getKurstypen(){
		
		if( m_strKurstypen.equals("#") ){
			
			m_strKurstypen="";
			m_intKurstypenCount=0;
			try{
				
				ResultSet rst=this.getKurstypenRst();
				
				// go through all records of coursetypes that this course is mapped to, and list their names.
				while(rst.next()){
					m_strKurstypen += rst.getString("strKurstypBezeichnung") + ", ";
					m_intKurstypenCount++;
				}
				
				// get rid of last comma,
				m_strKurstypen = m_strKurstypen.substring(0,m_strKurstypen.length()-2);
				
				// gc
				rst.close();
				rst=null;
				
			}catch(Exception eKurstypen){m_strKurstypen="#Coursetypes unknown" + eKurstypen.toString();}
		}
		return m_strKurstypen;
	}
	
	/**
	 * Befinden wir uns gerade in der Anmeldeperiode 
	 * dieses Kurstyps _und_ ist für diesen Kurs 
	 * die Scheinanmeldung erlaubt?
	 * @return wahr oder falsch
	 */
	public boolean isDuringCommitmentPeriod(){
		return (getKursScheinanmeldungErlaubt() && (getKursScheinanmeldungVon().getTime() <= g_TODAY.getTime()) && 
						(getKursScheinanmeldungBis()).getTime() >= g_TODAY.getTime());
	}
	
	/**
	 * Does this course have the KursID 'KURSID_NEW_COURSE' ?
	 * @return true, if this is a new course that must yet be added to the database.
	 */
	public boolean isNew(){
		return (getKursID()==KURSID_NEW_COURSE);
	}
	
	/**
	 * @return number of coursetypes that this course is mapped to.
	 */
	protected int getMappingCount(){
		if(m_strKurstypen.equals("#")) getKurstypen();
		return m_intKurstypenCount;
	}
	
	/**
	 * Does this course have ONE meeting per week, TWO meetings per week, or something entirely different?
	 * @return Indicator of what is the current option of this course (one of the following):
	 * <table><li>TERMIN_NOT_INITIALIZED = -1 (is not given back; termin <i>is</i> initialized in case it hasn't been
	 * <li>TERMIN_ONE_ONLY = 1 (one time per week),
	 * <li>TERMIN_TWO_DATES = 2 (two times per week),
	 * <li>TERMIN_VARIABEL = 3 (a String representation, uninterpreted by application</ul>
	 */
	public byte getTerminOpt(){
		if ( this.m_bytTerminOpt==TERMIN_NOT_INITIALIZED ) this.getTermin();
		return m_bytTerminOpt;
	}
	
	/**
	 * Der oder die Lehrende
	 * @return teacher of this course.
	 * @throws Exception (Teacher can't be constructed)
	 */
	public Dozent getDozent() throws Exception{
		if(m_objDozent==null) m_objDozent=new Dozent(this.getSdSeminarID(), this.getDozentID());
		return m_objDozent;
	}	

///////////////////////////////////////////////////
// Methoden
	

	
//#####################
// - SQL-Methoden

	/**
	 *  Überprüft Termin- und Raumbuchungen 
	 *  ({@link #terminSeemsOk()}, {@link #roomBookingSeemsOkay()}),
	 *  setzt ggf. einen Default-Kurstitel und 
	 *  überträgt die Leistungspunktezahl in KursXKurstyp
	 *  (ob das sinnvoll ist, weiß ich nicht). 
	 *  @see #terminSeemsOk()
	 *  @see #roomBookingSeemsOkay()
	 *	@returns wahr für Erfolg
	 *  @throws Exceptions on erroneous db-connection.
	 **/
	public boolean cUpdate() throws Exception{
		// if termin okay and room booking okay 
		// and title not null, this.toDBUpdateString() is performed.
		if(! ( terminSeemsOk() && roomBookingSeemsOkay() ) ) return false;
		
		if(isEmpty(getKursTitel())) setKursTitel( m_TITLE_DEFAULT );
		
		if(this.getKursCreditPts() > 0){
			sqlExe("update \"tblBdKursXKurstyp\" set \"sngKursCreditPts\"=" + this.getKursCreditPts() + 
					"where \"lngSeminarID\"=" + this.getSdSeminarID() + " and " +
					"\"lngKursID\"=" + this.getKursID() + " and \"sngKursCreditPts\"=0;");
		}
		
		return this.sqlExe("update \"tblBdKurs\" set " +
				"\"strKursTag\"='" + getDBCleanString(this.getKursTag()) +  "', " +
				"\"dtmKursBeginn\"="+ dbNormalTime(this.getKursBeginn()) +  ", " +
				"\"lngDozentID\"=" + this.getDozentID() +  ", " +
				"\"dtmKursEnde\"=" + dbNormalTime(this.getKursEnde()) +  ", " +
				"\"strKursRaum\"=" + dbNormal(this.getKursRaum()) +  ", " +
				"\"strKursTag2\"='" + getDBCleanString(this.getKursTag2()) +  "', " +
				"\"dtmKursBeginn2\"=" + dbNormalTime(this.getKursBeginn2()) +  ", " +
				"\"dtmKursEnde2\"=" + (this.getKursEnde2()==null ? "null," : ("'" + g_TIME_FORMAT.format(this.getKursEnde2()) +  "', ")) +
				"\"strKursRaum2\"=" + dbNormal(this.getKursRaum2()) +  ", " +
				"\"intKursTeilnehmerMaximal\"=" + this.getKursTeilnehmerMaximal() +  ", " +
				"\"strKursTitel\"='" + getDBCleanString(this.getKursTitel()) +  "', " +
				"\"strKursTitel_en\"='" + getDBCleanString(this.getKursTitel_en()) +  "', " +
				"\"strKursBeschreibung\"='" + getDBCleanString(this.getKursBeschreibung()) +  "', " +
				"\"strKursLiteratur\"='" + getDBCleanString(this.getKursLiteratur()) +  "', " +
				"\"strKursZusatz\"='" + getDBCleanString(this.getKursZusatz()) +  "', " +
				"\"strKursAnmeldung\"='" + getDBCleanString(this.getKursAnmeldung()) +  "', " +
				"\"strKursVoraussetzung\"='" + getDBCleanString(this.getKursVoraussetzung()) +  "', " +
				"\"blnKursSchein\"=" + getDBBoolRepresentation(this.getKursSchein()) +  ", " +
				"\"strKursEinordnung\"='" + getDBCleanString(this.getKursEinordnung()) +  "', " +
				"\"dtmKursLastChange\"=CURRENT_DATE, " +
				"\"dtmKursScheinanmeldungBis\"=" + dbNormalDate(this.getKursScheinanmeldungBis()) +  ", " +
				"\"dtmKursScheinanmeldungVon\"=" + dbNormalDate(this.getKursScheinanmeldungVon()) +  ", " +
				"\"blnKursScheinanmeldungErlaubt\"=" + getDBBoolRepresentation(this.getKursScheinanmeldungErlaubt()) +  ", " +
				"\"intKursSequence\"=" + this.getKursSequence() +  ", " +
				"\"blnKursPlanungssemester\"=" + getDBBoolRepresentation(this.getKursPlanungssemester()) +  ", " +
				"\"strKursTerminFreitext\"='" + getDBCleanString(this.getKursTerminFreitext()) +  "', " +
				"\"intKursTeilnehmer\"=" + this.getKursTeilnehmer() +  ", " +
				"\"sngKursCreditPts\"=" + this.getKursCreditPts() + ", " +
				"\"strKursRaumExtern1\"='" + getDBCleanString(this.getKursRaumExtern1()) +  "', " +
				"\"strKursCustom1\"='" + getDBCleanString(this.getKursCustom1()) +  "', " +
				"\"strKursRaumExtern2\"='" + getDBCleanString(this.getKursRaumExtern2()) +  "'" +
				" where (\"lngSdSeminarID\"=" + this.getSdSeminarID() + " AND "  +
				"\"lngKursID\"=" + this.getKursID() + ");");
	}

	/**
	 * Check <Code>cAdd(lngKurstypID)</Code> for details. In this utility method,  
	 * lngKurstypID is the value that has been set with <Code>.initNewKurs()</Code>
	 * @see #initNewKurs(long, long, String)
	 * @see #cAdd(long)
	 * @return true for success.
	 * @throws Exception
	 */
	public boolean cAdd() throws Exception{
		return cAdd(m_lngKurstypAddNew);
	}
	
	/**
	 *  Überprüft Termin- und Raumbuchungen 
	 *  ({@link #terminSeemsOk()}, {@link #roomBookingSeemsOkay()}),
	 *  setzt ggf. einen Default-Kurstitel,  
	 *  fügt den Kursdatensatz hinzu und ordnet
	 *  den Kurstyp zu.
	 *  @param lngKurstypID zuzuordnender Kurstyp
	 *  @throws Exceptions when db-connection goes wrong and/or when the new KursID cannot be set.
	 **/
	public boolean cAdd(long lngKurstypID) throws Exception{
		// if termin okay and room booking okay and title not null, a new KursID is set and 'toDBAddString' executed.
		if(! ( terminSeemsOk() && roomBookingSeemsOkay() ) ) return false;
		if(isEmpty(this.getKursTitel())) this.setKursTitel( m_TITLE_DEFAULT );
		this.setKursID ( this.getNextKursID() );
		boolean blnReturn = sqlExe("insert into \"tblBdKurs\" ( " +
				"\"lngSdSeminarID\", \"lngDozentID\", \"lngKursID\", \"strKursUnivISID\", \"strKursTag\", \"dtmKursBeginn\", \"dtmKursEnde\", \"strKursRaum\", \"strKursTag2\", \"dtmKursBeginn2\", \"dtmKursEnde2\", \"strKursRaum2\", \"intKursTeilnehmerMaximal\", \"lngKursRequests\", \"strKursTitel\", \"strKursTitel_en\", \"strKursBeschreibung\", \"strKursLiteratur\", \"strKursZusatz\", \"strKursAnmeldung\", \"strKursVoraussetzung\", \"blnKursSchein\", \"strKursEinordnung\", \"intKursStunden\", \"dtmKursLastChange\", \"dtmKursScheinanmeldungBis\", \"dtmKursScheinanmeldungVon\", \"blnKursScheinanmeldungErlaubt\", \"intKursSequence\", \"blnKursPlanungssemester\", \"strKursTerminFreitext\", \"intKursTeilnehmer\", \"sngKursCreditPts\", \"strKursRaumExtern1\", \"strKursCustom1\", \"strKursRaumExtern2\" ) VALUES ( " +
				this.getSdSeminarID() + ", " +
				this.getDozentID() + ", " +
				this.getKursID() + ", " +
				"'" + this.getKursUnivISID() + "', " +
				"'" + getDBCleanString(this.getKursTag()) + "', " +
				dbNormalTime(this.getKursBeginn()) + ", " +
				dbNormalTime(this.getKursEnde()) + ", " +
				dbNormal(this.getKursRaum())  + ", " +
				"'" + getDBCleanString(this.getKursTag2()) + "', " +
				dbNormalTime(this.getKursBeginn2()) + ", " +
				dbNormalTime(this.getKursEnde2()) + ", " +
				dbNormal(this.getKursRaum2()) + ", " +
				this.getKursTeilnehmerMaximal() + ", " +
				this.getKursRequests() + ", " +
				"'" + getDBCleanString(this.getKursTitel()) + "', " +
				"'" + getDBCleanString(this.getKursTitel_en()) + "', " +
				"'" + getDBCleanString(this.getKursBeschreibung()) + "', " +
				"'" + getDBCleanString(this.getKursLiteratur()) + "', " +
				"'" + getDBCleanString(this.getKursZusatz()) + "', " +
				"'" + getDBCleanString(this.getKursAnmeldung()) + "', " +
				"'" + getDBCleanString(this.getKursVoraussetzung()) + "', " +
				"" + getDBBoolRepresentation(this.getKursSchein()) + ", " +
				"'" + getDBCleanString(this.getKursEinordnung()) + "', " +
				this.getKursStunden() + ", " +
				"CURRENT_DATE, " +
				dbNormalDate(this.getKursScheinanmeldungBis()) + ", " +
				dbNormalDate(this.getKursScheinanmeldungVon()) + ", " +
				getDBBoolRepresentation(this.getKursScheinanmeldungErlaubt()) + ", " +
				this.getKursSequence() + ", " +
				getDBBoolRepresentation(this.getKursPlanungssemester()) + ", " +
				"'"  + getDBCleanString(this.getKursTerminFreitext()) +  "', " +
				this.getKursTeilnehmer() + ", " +
				this.getKursCreditPts() + ", " +
				"'" + getDBCleanString(this.getKursRaumExtern1()) + "', " +
				"'" + getDBCleanString(this.getKursCustom1()) + "', " +
				"'" + getDBCleanString(this.getKursRaumExtern2()) + "' " +
				" );");
		return ( ( blnReturn ) && ( this.mapCoursetype( lngKurstypID ) )  );
	}

	/**
	 * Löscht alle Zuordnungen des Kurses
	 * zu Kurstypen und dann den Kurs,
	 * wenn keine Anmeldungen ausstehen.
	 * Falls doch Anmeldungen ausstehen (und
	 * mit {@link #setAllowDeletionWithOpenCommitments(boolean)} 
	 * eine Ausnahme erlaubt wurde) wird ein Fehler 
	 * ausgelöst.
	 * @see #setAllowDeletionWithOpenCommitments(boolean)
	 * @return 'true' in case of success, 'false' in case of error.
	 * @throws ERR_KURS_BLOCKED_BY_OPEN_COMMITMENT if AllowDeletionWithOpenCommitments is set to false (default) and 
	 * there are open commitments to this course.
	 */
	public boolean cDelete() throws Exception{
		if(!m_blnAllowDeletionWithOpenCommitments)
			if((dbCount("strMatrikelnummer", "tblBdStudentXLeistung", 
					"\"lngKlausuranmeldungKursID\" = " + getKursID() + " AND " +
					"\"lngSdSeminarID\"=" + getSdSeminarID() + " AND " + 
					"\"blnStudentLeistungValidiert\"=false AND " +
					"\"blnStudentLeistungKlausuranmeldung\"=true")>0)) throw new Exception(String.valueOf(ERR_KURS_BLOCKED_BY_OPEN_COMMITMENT));

		boolean blnReturn;
		try{
		  blnReturn = sqlExe("delete from \"tblBdKursXKurstyp\" where \"lngSeminarID\"=" + this.getSdSeminarID() + " and \"lngKursID\"=" + this.getKursID() + ";");
		  if(blnReturn) this.delete();
		}catch(Exception eCDelete){blnReturn=false;}
		return blnReturn;
	}

// +++++++++++++++++++++++++++++++
// Konfigurations-Checks

	/**
	 *  Is the current course's room booking okay the way it is?
	 *	@return boolean value indicating whether the room-booking of the current course seems okay or not.
	 *  There are numerous checks made, among them of course checks if the rooms are free at the given
	 *  times and days. It is also checked whether 'external' rooms don't accidentally carry the same names
	 *  that local rooms do. The nature of the checks is detailed through the possible error codes, viz:<ul>
	 *	<li>public final byte ERR_ROOM_TERMIN_NOT_OKAY				=-1;	<=> termin setting not okay (cf. method terminSeemsOk())
	 *	<li>public final byte ERR_ROOM_1_SET_WITH_VAR_TERMIN		=1;		<=> getKursRaum() is not empty although freetext date/time
	 *	<li>public final byte ERR_ROOM_2_SET_WITH_VAR_TERMIN		=2;		<=> getKursRaum2()is not empty although freetext date/time
	 *	<li>public final byte ERR_ROOM_EXT_1_EMPTY_ON_VAR_TERMIN	=5;		<=> KursRaumExtern1 is empty although freetext date/time
	 *	<li>public final byte ERR_ROOM_EXT_1_LOCAL					=3;		<=> KursRaumExtern1 is a local room, not an external one.
	 *	<li>public final byte ERR_ROOM_EXT_2_LOCAL					=4;		<=> KursRaumExtern2 is a local room, not an external one.
	 *	<li>public final byte ERR_ROOM_2_SET_WITH_ONE_TERMIN		=6;		<=> KursRaum2 not empty although 'TERMIN_ONE_ONLY'
	 *	<li>public final byte ERR_ROOM_EXT_2_NOT_EMPTY_ON_VAR_TERMIN=7;		<=> External room 2 not empty on 'TERMIN_VARIABLE'
	 *	<li>public final byte ERR_ROOMS_EMPTY_ON_ONE_TERMIN			=8;		<=> Setting 'TERMIN_ONE_ONLY': rooms KursRaum1 and Extern1 both empty
	 *	<li>public final byte ERR_ROOM_1_OCCUPIED					=9;		<=> Room 1 is occupied meanwhile
	 *	<li>public final byte ERR_NO_ROOM_1_SET						=10;	<=> KursRaum and RaumExtern1 are both empty.
	 *	<li>public final byte ERR_NO_ROOM_2_SET						=11;	<=> KursRaum2 and RaumExtern2 are both empty.
	 *	<li>public final byte ERR_ROOM_2_OCCUPIED					=12;	<=> Room 2 is occupied meanwhile
	 *  <li>public final byte ERR_ROOM_BOOKING_INTERNAL				=13;	<=> An internal error occurred during check.
	 *  </ul>
	 *  The resulting error is stored in 'm_bytRoomErr' and publickly accessible.
	 *  If no error occurs, 'true' is returned.
	 **/
	public boolean roomBookingSeemsOkay(){
		try{
		  m_bytRoomErr = this.getRoomBookingErr();
	    }catch(Exception eRoomBookingSO){m_bytRoomErr = this.ERR_ROOM_BOOKING_INTERNAL;}
	    return (m_bytRoomErr==0);
	}
	
	/**
	 * Erlaubt das Löschen von Kursen auch wenn
	 * es zu diesen Kursen noch offene Anmeldungen
	 * gibt. Nicht empfehlenswert.
	 * @param boolean value
	 */
	public void setAllowDeletionWithOpenCommitments(boolean value){
		m_blnAllowDeletionWithOpenCommitments=value;
	}
	
	/**
	 * Überprüft das Objekt daraufhin, ob
	 * - ein Termin angegeben wurde,
	 * - die Terminangaben (Freitext, einmal wöchentlich,
	 *      zweimal wöchentlich) je konsistent sind.
	 *  @return wahr für gute Konfiguration
	 **/
	public boolean terminSeemsOk(){
		
		boolean blnReturn=false;
		
		if(this.getTerminOpt()==TERMIN_NOT_INITIALIZED){
			return false;
		}
		
		// termin is initialized
		if(this.getTerminOpt()==TERMIN_ONE_ONLY){					// only one termin set and wanted:
			
			// check that others empty:
			if( !( isEmpty( this.getKursTag2()           ) )) return false;
			if( !( this.getKursEnde2() == null           )) return false;
			if( !( this.getKursBeginn2() == null         )) return false;
			if( !( isEmpty( this.getKursTerminFreitext() ) )) return false;
			if(isNoDay(this.getKursTag())) return false;
			
			// check that sequence is okay:
			try{
				return this.getKursBeginn().before(this.getKursEnde());
			}catch(Exception e1IsNoTime){return false;}
			
		}
		
		if(this.getTerminOpt()==TERMIN_TWO_DATES){			// two termine set and wanted:
			
			// check that freetext empty, both days set:
			if(isNoDay(this.getKursTag())) return false;
			if(isNoDay(this.getKursTag2())) return false;
			if( !( isEmpty( this.getKursTerminFreitext() ) )) return false;
			
			// check that both sequences are okay:
			try{
				if(this.getKursTag().equals(this.getKursTag2())){
					if(!(this.getKursEnde().before(this.getKursBeginn2()))) return false;
				}
				return ( (this.getKursBeginn().before(this.getKursEnde() )) && (this.getKursBeginn2().before(getKursEnde2() )) );
			}catch(Exception e2IsNoTime){return false;}
			
		}
		
		// termin as free text
		if( !( isEmpty( this.getKursTag()            ) )) return false;
		if( !(  this.getKursEnde() == null            )) return false;
		if( !(  this.getKursBeginn()==null         	 )) return false;
		if( !( isEmpty( this.getKursTag2()           ) )) return false;
		if( !(   this.getKursEnde2() ==null           )) return false;
		if( !(   this.getKursBeginn2()==null         )) return false;
		blnReturn =  !(isEmpty( this.getKursTerminFreitext() ));
		
		return blnReturn;
	}
	
	/**
	 * Method maps the current course to the coursetype specified through 'lngKurstypID.'
	 * In more prosaic terms: a record is added to table 'tblBdKursXKurstyp,' that maps
	 * the KursID to the new KurstypID.<br />
	 * @version 5-30 now also sets CreditPoints in KursXKurstyp
	 * Sets the column 'Bemerkung' to 'auto-add,' which is a remnant and should be #hack ed
	 * if it does not provide useful for something.
	 * @return 'true' if successful, 'false' if sql causes an exception.
	 * @param lngKurstypID: id of coursetype to add. The course runs from then on (also) under the heading of this course.
	 **/
	public boolean mapCoursetype(long lngKurstypID){
		return mapCoursetype(lngKurstypID, this.getKursCreditPts());
	}

	/**
	 * @param lngKurstypID
	 * @return true for success, false for error.
	 * @throws Exception ERR_TRYING_TO_ERASE_LAST_COURSEMAPPING
	 */
	public boolean dropCoursetypeMapping(long lngKurstypID) throws Exception{
		
		// Wenn die letzte Zuordnung gel?scht 
		// werden soll: stattdessen zu Kurstyp 0 
		// zuordnen. Fehler wird ausgelöst, 
		// wenn das nicht funktioniert hat.
		if(getMappingCount() <= 1){
			mapCoursetype(m_KURSTYP_NULL);
			m_strKurstypen="#";
			if(getMappingCount() <= 1) throw new Exception(String.valueOf(ERR_TRYING_TO_ERASE_LAST_COURSEMAPPING));
		}
		
		m_strKurstypen="#";
		try {
			return sqlExe ("delete from \"tblBdKursXKurstyp\" where " +
					"\"lngSeminarID\"=" + this.getSdSeminarID() + " and " +
					"\"lngKursID\"=" + this.getKursID() + " and " +
					"\"lngKurstypID\"=" + lngKurstypID + ";");
		} catch (Exception e) {return false;}
	}
	
	/**
	 * Set credit points of this mapping to <Code>fltCreditPts</Code>
	 * @param lngKurstypID
	 * @param fltCreditPts
	 * @return true for success, false for failure
	 */
	public boolean updateCoursetypeMapping(long lngKurstypID, float fltCreditPts){
		  try {
			return sqlExe ("update \"tblBdKursXKurstyp\" set \"sngKursCreditPts\"=" + fltCreditPts + 
					" where \"lngSeminarID\"=" + this.getSdSeminarID() + " and " +
					"\"lngKursID\"=" + this.getKursID() + " and \"lngKurstypID\"=" + lngKurstypID + ";");
		} catch (Exception e) {return false;}
	}
	
	/**
	 * Add mapping and override CreditPoints.<br />
	 * @version 6.01.06: If the coursetype-Id is not KURSTYP_NULL (the 'not-mapped' coursetype), 
	 * all references to KURSTYP_NULL are deleted.
	 * @param lngKurstypID
	 * @param fltCreditPts
	 * @return
	 */
	public boolean mapCoursetype(long lngKurstypID, float fltCreditPts) {
		m_strKurstypen="#";
		boolean blnSuccess = false;
		try{
			
			blnSuccess=sqlExe("insert into \"tblBdKursXKurstyp\" ( " +
					"\"lngSeminarID\", \"lngKurstypID\", \"lngKursID\", \"sngKursCreditPts\", \"strBemerkung\" ) VALUES ( " +
					this.getSdSeminarID() + ", " +
					lngKurstypID + ", " +
					this.getKursID() + ", " +
					fltCreditPts + ", " +
					"'auto-add'" +
			" );");
			
			if((blnSuccess) && (lngKurstypID!=m_KURSTYP_NULL)) blnSuccess = dropCoursetypeMapping(m_KURSTYP_NULL); 
			
		}catch(Exception eMapCoursetype){return false;}
		
		return blnSuccess;
		
	}
	
	/**
	 *  ResultSet with coursetypes of current course.<br />
	 *  Selects everything in tblBdKurs and KursID and CreditPoints alias 'Leistungspunkte' from 
	 *  tblBdKursXKurstyp. 
	 *  @return a ResultSet with coursetypes (*-select) of current course, currently ordered by KurstypID asc.
	 *  @throws Exception if connection to db is erroneous.
	 */
	public ResultSet getKurstypenRst() throws Exception{
		return sqlQuery("SELECT " +
				"x.\"lngKursID\", " +
				"x.\"sngKursCreditPts\" as Leistungspunkte, " +
				"k.* " +
				"FROM \"tblSdKurstyp\" k, \"tblBdKursXKurstyp\" x  " +
				"WHERE " +
				"(" +
				"(k.\"lngSdSeminarID\" = x.\"lngSeminarID\") AND " +
				"(k.\"lngSdSeminarID\" = " + this.getSdSeminarID() + ") AND " +
				"(x.\"lngSeminarID\" = " + this.getSdSeminarID() + ") AND " +
				"(k.\"lngKurstypID\" = x.\"lngKurstypID\") AND " +
				"(x.\"lngKursID\"=" + this.getKursID() + ")" +
		") ORDER BY \"lngKurstypID\" ASC;");
	}
	
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 4.   P R I V A T E    M E T H O D S (of interest)
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	
	/**
	 * @returns object of type logic.RaumData -- a utility around rooms.
	 * @param strRaumBezeichnung: name of the room we're talking about.
	 **/
	private RaumData getRoom(String strRaumBezeichnung){
		if(m_objRoom==null)	m_objRoom = new RaumData(strRaumBezeichnung);
		if(!( (m_objRoom.getRaumBezeichnung().equals(strRaumBezeichnung)))) m_objRoom=new RaumData(strRaumBezeichnung);
		return m_objRoom;
	}
	
	/**
	 * Gibt 0 aus, wenn Zeit und Raum zusammenpassen,
	 * ansonsten einen Fehlercode.
	 * @see #ERR_NO_ROOM_1_SET etc.
	 * @throws Exceptions that are inherited from 'RoomIsFree' of class RoomData. (Database errors).
	 **/
	private byte getRoomBookingErr() throws Exception{
		
		byte bytReturn=0;
		
		// Zunächst Termin überprüfen.
		if(!(this.terminSeemsOk())) return ERR_ROOM_TERMIN_NOT_OKAY;
		
		// Falls der Kurs an einem nicht
		// interpretierbaren Termin stattfindet,
		// darf auch kein 'normaler' Raum
		// gebucht sein (sondern ein 'externer' 
		// Raum), da sonst der Raumplan nicht 
		// funktioniert
		if(this.getTerminOpt()==TERMIN_VARIABEL){				// termin freetext:
			
			if( !isEmpty( this.getKursRaum()  ) ) 	return ERR_ROOM_1_SET_WITH_VAR_TERMIN;
			if( !isEmpty( this.getKursRaum2() ) )	return ERR_ROOM_2_SET_WITH_VAR_TERMIN;
			
			if(  isEmpty( this.getKursRaumExtern1() ) ) return ERR_ROOM_EXT_1_EMPTY_ON_VAR_TERMIN;
			if(!(isEmpty(this.getKursRaumExtern2()))) return ERR_ROOM_EXT_2_NOT_EMPTY_ON_VAR_TERMIN;
			if( (getRoom("")).isLocalRoom(this.getSdSeminarID(), normalize(this.getKursRaumExtern1())) ) return ERR_ROOM_EXT_1_LOCAL;
			
		}else{
			
			// Wenn der Termin einmal wöchentlich statt-
			// findet, muss genau ein Raum gebucht sein.
			if(this.getTerminOpt()==TERMIN_ONE_ONLY){

				if( !isEmpty( this.getKursRaum2() ) )	return ERR_ROOM_2_SET_WITH_ONE_TERMIN;
				
				// Ist kein 'normaler' Raum gebucht, 
				// muss ein externer Raum gebucht sein.
				if( isEmpty( this.getKursRaum() ) ){
					if( isEmpty(this.getKursRaumExtern1()) ) return ERR_ROOMS_EMPTY_ON_ONE_TERMIN;
					if( this.getRoom("").isLocalRoom(this.getSdSeminarID(), normalize(this.getKursRaumExtern1())) ) return ERR_ROOM_EXT_1_LOCAL;
				}else{
					// Der normale Raum muss zu dieser Zeit frei sein
					if (!(this.getRoom(this.getKursRaum()).isFree(this, false)) ) return ERR_ROOM_1_OCCUPIED;
				}
			}else{
				// Wenn der Termin zweimal wöchentlich 
				// stattfindet, müssen zwei Räume 
				// gebucht sein
				if (isEmpty(this.getKursRaum())){
					if(isEmpty(this.getKursRaumExtern1())) return ERR_NO_ROOM_1_SET;
					if( this.getRoom("").isLocalRoom(this.getSdSeminarID(), normalize(this.getKursRaumExtern1())) ) return ERR_ROOM_EXT_1_LOCAL;
				}else{
					if (!(this.getRoom(this.getKursRaum()).isFree(this, false)) ) return ERR_ROOM_1_OCCUPIED;
				}
				if (isEmpty(this.getKursRaum2())){
					if(isEmpty(this.getKursRaumExtern2())) return ERR_NO_ROOM_2_SET;
					if( this.getRoom("").isLocalRoom(this.getSdSeminarID(), normalize(this.getKursRaumExtern2())) ) return ERR_ROOM_EXT_2_LOCAL;
				}else{
					if (!(this.getRoom(this.getKursRaum2()).isFree(this, true)) ) return ERR_ROOM_2_OCCUPIED;
				}
			}
		}
		return bytReturn;
	}
	
	/**
	 * <pre>
	 * Standard, but probably stupid: get next ID for this seminar. 
	 * Needed to add new records (since there is no 'autowert' or 'select nextval'
	 * used in SignUpXP database, for several reasons.
	 * 
	 * Note that since version 6-14 (March 2006), if there IS no 
	 * course, the method returns the maximum of "tblBdKvvArchiv" plus 
	 * one.
	 * 
	 * The goal of this is to try and make the KursID unique 
	 * in the future.
	 * </pre>
	 *  @return maximum id of course (of this seminar) plus one.
	 *  @throws Exception if connection to db is erroneous.
	 **/
	private long getNextKursID() throws Exception{
		ResultSet rst=sqlQuery("select max(\"lngKursID\")+1 as next from \"tblBdKurs\" where \"lngSdSeminarID\"=" + this.getSdSeminarID() + ";");
		long lngNextID = 0;
		if(rst.next()){
		  lngNextID=rst.getLong("next");
		  if(lngNextID==0){
		    lngNextID = getNextID("lngKvvArchivKursID", "tblBdKvvArchiv", "\"lngSdSeminarID\"=" + this.getSdSeminarID() + "");
		  }
		  return lngNextID;
		}
		rst.close();
		rst=null;
		return -1;

	}
	
	
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 5.   H T M L - C o m b o   U t i l s   (uninteresting)
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	
	/**
	 * Html-combo with weekdays (German -> #hack).
	 * @return a HTML-combo-option String with the weekdays -- only German, currently (#hack). The String
	 * looks like this: <option XY value="WEEKDAY">WEEKDAY</option> where <br/>
	 * XY is 'selected' if WEEKDAY equals Tag1 of this course,<br/>
	 * and is empty if WEEKDAY does not equal Tag1 of this course.<br/>
	 * WEEKDAY iterates through all weekdays. Unfortunately currently only in German (see String m_strTage).
	 * In case there is no day (but a freetext instead), this method returns '#.'
	 */
	public String getTag1Cbo(){
		if( m_strCboTag1.equals("#") ){
			m_strCboTag1="";
			for(int ii=0; ii<7; ii++){
				m_strCboTag1 += "<option " + ( ( m_strTage[ii].equals(this.getKursTag()) )  ? "selected " : "" ) + "value=\"" + m_strTage[ii] + "\">" + m_strTage[ii] + "</option>\n";
			}
			// add default (if none there yet):
			m_strCboTag1 = "<option " + ( (m_strCboTag1.indexOf("selected")<=0) ? "selected " : "") + "value=\"\">------</option>" + m_strCboTag1;
		}
		return m_strCboTag1;
	}
	
	/**
	 * Html-combo with weekdays (German -> #hack).
	 * @return a HTML-combo-option String with the weekdays -- only German, currently (#hack). The String
	 * looks like this: <option XY value="WEEKDAY">WEEKDAY</option> where <br/>
	 * XY is 'selected' if WEEKDAY equals Tag2 of this course,<br/>
	 * and is empty if WEEKDAY does not equal Tag2 of this course.<br/>
	 * WEEKDAY iterates through all weekdays. Unfortunately currently only in German (see String m_strTage).
	 * In case there _is_ no 2nd day, this method returns '#' as value.
	 */
	public String getTag2Cbo(){
		if( m_strCboTag2.equals("#") && (this.getKursTag2()!=null) ){
			m_strCboTag2="";
			for(int ii=0; ii<7; ii++){
				m_strCboTag2 += "<option " + ( ( m_strTage[ii].equals(this.getKursTag2()) ) ? "selected" : "" ) + " value=\"" + m_strTage[ii] + "\">" + m_strTage[ii] + "</option>\n";
			}
			// add default (if none there yet):
			m_strCboTag2 = "<option " + ( (m_strCboTag2.indexOf("selected")<=0) ? "selected " : "") + "value=\"\">------</option>" + m_strCboTag2;
		}
		return m_strCboTag2;
	}
	
	/**
	 * Utility for time-format that'd better be #hack ed.
	 * @return substring of time (0-5).
	 */
	private String _getKursBeginn(){
		return g_TIME_FORMAT.format(this.getKursBeginn());
	}
	
	/**
	 * Utility for time-format that'd better be #hack ed.
	 * @return substring of time (0-5).
	 */
	private String _getKursBeginn2(){
		return g_TIME_FORMAT.format(this.getKursBeginn2());
	}
	
	/**
	 * Utility for time-format that'd better be #hack ed.
	 * @return substring of time (0-5).
	 */
	private String _getKursEnde(){
		return g_TIME_FORMAT.format(this.getKursEnde());
	}
	
	/**
	 * Utility for time-format that'd better be #hack ed.
	 *  @return substring of time (0-5).
	 */
	private String _getKursEnde2(){
		return g_TIME_FORMAT.format(this.getKursEnde2());
	}
	
	/**
	 * Utility that should be #hack ed.
	 * @returns true if what isEmpty, false otherwise
	 **/
	private boolean isNoDay(String what){
		return isEmpty(what);
	}
	
	/**
	 * Html-Hour-combo.
	 * @return a HTML-combo-option String with hours (from 8 o clock to 22 o clock by default). The String looks like this:
	 * <option XY value="08">08</option> (for eight o'clock). XY is 'selected' in case 08 is the time of this course.
	 **/
	public String getStartHour1Cbo(){
		if( m_strCboStartHour1.equals("#") ){
			
			m_strCboStartHour1="";
			String str2Digit="";
			int intBeginn=0;
			
			try{
				intBeginn=Integer.parseInt(this._getKursBeginn().substring(0,2));
			}catch(Exception eGetHour1Cbo){intBeginn=-1;}
			
			for(int ii=HOURS_OFFSET; ii<HOURS_LIMIT; ii++){
				str2Digit = ( (ii<10) ? "0" + String.valueOf(ii) : String.valueOf(ii) );
				m_strCboStartHour1 += "<option " + ( (ii==intBeginn) ? "selected" : "" ) + " value=\"" + str2Digit + "\">" + str2Digit + "</option>\n";
			}
			// add default (if none there yet).
			m_strCboStartHour1 = "<option " + ( (m_strCboStartHour1.indexOf("selected")<=0) ? "selected " : "" ) + "value=\"\">--</option>" + m_strCboStartHour1;
		}
		
		return m_strCboStartHour1;
	}
	
	/**
	 * Html-Hour-combo.
	 * @return a HTML-combo-option String with hours (from 8 o clock to 22 o clock). The String looks like this:
	 * <option XY value="08">08</option> (for eight o'clock). XY is 'selected' in case 08 is the time of this course.
	 **/
	public String getStartHour2Cbo(){
		if( m_strCboStartHour2.equals("#") ){
			
			m_strCboStartHour2="";
			String str2Digit="";
			int intBeginn=0;
			
			try{
				intBeginn=Integer.parseInt(this._getKursBeginn2().substring(0,2));
			}catch(Exception eGetHour1Cbo){intBeginn=-1;}
			
			for(int ii=8; ii<22; ii++){
				str2Digit = ( (ii<10) ? "0" + String.valueOf(ii) : String.valueOf(ii) );
				m_strCboStartHour2 += "<option " + ( (ii==intBeginn) ? "selected" : "" ) + " value=\"" + str2Digit + "\">" + str2Digit + "</option>\n";
			}
			// add default (if none there yet).
			m_strCboStartHour2 = "<option " + ( (m_strCboStartHour2.indexOf("selected")<=0) ? "selected " : "" ) + "value=\"\">--</option>" + m_strCboStartHour2;
			
		}
		
		return m_strCboStartHour2;
	}
	
	/**
	 * Html-minute-combo.
	 * @return HTML-combo-String with minute-section of date of this course. The String looks like this:
	 * <option XY value="15">15</option> (meaning, together with the hour "08," the time "08:15". <br/>
	 * where XY is 'selected' in case this is the time of the current course.
	 * options are: "00", "15", "30", "45".
	 */
	public String getStartMinute1Cbo(){
		if( m_strCboStartMinute1.equals("#") ){
			m_strCboStartMinute1="";
			String strMins="";
			
			try{
				strMins = this._getKursBeginn().substring(3,5);
			}catch(Exception eBeginnIsNull){strMins="##";}
			
			for(int ii=0; ii<4; ii++){
				m_strCboStartMinute1 += "<option " + ( (m_strMins[ii].equals(strMins)) ? "selected" : "" ) + " value=\"" + m_strMins[ii] + "\">" + m_strMins[ii] + "</option>";
			}
			// add default (if none there yet)
			m_strCboStartMinute1="<option " + ( (m_strCboStartMinute1.indexOf("selected")<=0) ? "selected " : "" ) + "value=\"\">--</option>" + m_strCboStartMinute1;
		}
		return m_strCboStartMinute1;
	}
	
	/**
	 * Html-minute-combo.
	 * @return HTML-combo-String with minute-section of date of this course. The String looks like this:
	 * <option XY value="15">15</option> (meaning, together with the hour "08," the time "08:15". <br/>
	 * where XY is 'selected' in case this is the time of the current course.
	 * options are: "00", "15", "30", "45".
	 */
	public String getStartMinute2Cbo(){
		if( m_strCboStartMinute2.equals("#") ){
			m_strCboStartMinute2="";
			String strMins="";
			
			try{
				strMins = this._getKursBeginn2().substring(3,5);
			}catch(Exception eStartIsNull){strMins="##";}
			
			for(int ii=0; ii<4; ii++){
				m_strCboStartMinute2 += "<option " + ( (m_strMins[ii].equals(strMins)) ? "selected" : "" ) + " value=\"" + m_strMins[ii] + "\">" + m_strMins[ii] + "</option>";
			}
			// add default (if none there yet)
			m_strCboStartMinute2="<option " + ( (m_strCboStartMinute2.indexOf("selected")<=0) ? "selected " : "" ) + "value=\"\">--</option>" + m_strCboStartMinute2;
			
		}
		return m_strCboStartMinute2;
	}
	
	
	/**
	 * Html-hour combo.
	 * @returns a HTML-combo-option String with hours (from 8 o clock to 22 o clock by default). The String looks like this:
	 * <option XY value="08">08</option> (for eight o'clock). XY is 'selected' in case 08 is the time of this course.
	 */
	public String getEndHour1Cbo(){
		if( m_strCboEndHour1.equals("#") ){
			
			m_strCboEndHour1="";
			String str2Digit="";
			int intEnde=0;
			
			try{
				intEnde=Integer.parseInt(this._getKursEnde().substring(0,2));
			}catch(Exception eGetHour1Cbo){intEnde=-1;}
			
			for(int ii=HOURS_OFFSET; ii<HOURS_LIMIT; ii++){
				str2Digit = ( (ii<10) ? "0" + String.valueOf(ii) : String.valueOf(ii) );
				m_strCboEndHour1 += "<option " + ( (ii==intEnde) ? "selected" : "" ) + " value=\"" + str2Digit + "\">" + str2Digit + "</option>\n";
			}
			// add default (if none there yet).
			m_strCboEndHour1 = "<option " + ( (m_strCboEndHour1.indexOf("selected")<=0) ? "selected " : "") + "value=\"\">--</option>" + m_strCboEndHour1;
		}
		
		return m_strCboEndHour1;
	}
	
	/**
	 * Html-hour-combo.
	 * @return a HTML-combo-option String with hours (from 8 o clock to 22 o clock). The String looks like this:
	 * <option XY value="08">08</option> (for eight o'clock). XY is 'selected' in case 08 is the time of this course.
	 */
	public String getEndHour2Cbo(){
		if( m_strCboEndHour2.equals("#") ){
			
			m_strCboEndHour2="";
			String str2Digit="";
			int intEnde=0;
			
			try{
				intEnde=Integer.parseInt(this._getKursEnde2().substring(0,2));
			}catch(Exception eGetHour1Cbo){intEnde=-1;}
			
			for(int ii=8; ii<22; ii++){
				str2Digit = ( (ii<10) ? "0" + String.valueOf(ii) : String.valueOf(ii) );
				m_strCboEndHour2 += "<option " + ( (ii==intEnde) ? "selected" : "" ) + " value=\"" + str2Digit + "\">" + str2Digit + "</option>\n";
			}
			// add default (if none there yet).
			m_strCboEndHour2 = "<option " + ( (m_strCboEndHour2.indexOf("selected")<=0) ? "selected " : "") + "value=\"\">--</option>" + m_strCboEndHour2;
			
		}
		
		return m_strCboEndHour2;
	}
	
	/**
	 * Html-minute-combo.
	 * @return HTML-combo-String with minute-section of date of this course. The String looks like this:
	 * <option XY value="15">15</option> (meaning, together with the hour "08," the time "08:15". <br/>
	 * where XY is 'selected' in case this is the time of the current course.
	 * options are: "00", "15", "30", "45".
	 */
	public String getEndMinute1Cbo(){
		if( m_strCboEndMinute1.equals("#") ){
			m_strCboEndMinute1="";
			String strMins="";
			
			try{
				strMins = this._getKursEnde().substring(3,5);
			}catch(Exception eEndeIsNull){strMins="##";}
			
			for(int ii=0; ii<4; ii++){
				m_strCboEndMinute1 += "<option " + ( (m_strMins[ii].equals(strMins)) ? "selected" : "" ) + " value=\"" + m_strMins[ii] + "\">" + m_strMins[ii] + "</option>";
			}
			// add default (if none there yet)
			m_strCboEndMinute1="<option " + ( (m_strCboEndMinute1.indexOf("selected")<=0) ? "selected " : "") + "value=\"\">--</option>" + m_strCboEndMinute1;
		}
		return m_strCboEndMinute1;
	}
	
	/**
	 * Html-minute-combo.
	 * @returns HTML-combo-String with minute-section of date of this course. The String looks like this:
	 * <option XY value="15">15</option> (meaning, together with the hour "08," the time "08:15". <br/>
	 * where XY is 'selected' in case this is the time of the current course.
	 * options are: "00", "15", "30", "45".
	 */
	public String getEndMinute2Cbo(){
		if( m_strCboEndMinute2.equals("#") ){
			m_strCboEndMinute2="";
			String strMins="";
			
			try{
				strMins = this._getKursEnde2().substring(3,5);
			}catch(Exception eEndIsNull){strMins="##";}
			
			for(int ii=0; ii<4; ii++){
				m_strCboEndMinute2 += "<option " + ( (m_strMins[ii].equals(strMins)) ? "selected" : "" ) + " value=\"" + m_strMins[ii] + "\">" + m_strMins[ii] + "</option>";
			}
			// add default (if none there yet)
			m_strCboEndMinute2="<option " + ( (m_strCboEndMinute2.indexOf("selected")<=0) ? "selected " : "") + "value=\"\">--</option>" + m_strCboEndMinute2;
			
		}
		return m_strCboEndMinute2;
	}
	
///////////////////////////////////////////////////
// Initialisierer
///////////////////////////////////////////////////	
	/**
	 * Initialisiert das Objekt -- ruft
	 * zunächst {@link #reset()} auf
	 * @see #reset()
	 * @param lngSeminarID: id of seminar the course belongs to,
	 * @param lngKursID: id of course.
	 **/
	public void reinit(long lngSeminarID, long lngKursID) throws Exception{
		reset();
		this.init(lngSeminarID, lngKursID);
	}
	
	/**
	 * Initialisiert Kursobjekt als 
	 * neuen Kurs, der noch nicht in 
	 * der Datenbank steht.
	 * @param lngKurstypID
	 * @param lngDozentID
	 * @param strKurstitel
	 */
	public void initNewKurs(long lngKurstypID, long lngDozentID, String strKurstitel){
		this.setKursID(KURSID_NEW_COURSE);
		m_lngKurstypAddNew = lngKurstypID;
		this.setDozentID(lngDozentID);
		this.setKursTitel(strKurstitel);
	}
	
	/**
	 * Sets Termin to not initialized, so the configuration (1 data, 2 dates or free text) is 
	 * read again.
	 */
	public void unsetTermin(){
		m_bytTerminOpt=TERMIN_NOT_INITIALIZED;
	}

	/**
	 * reset object variables for flexible bean usage.
	 * @see #reinit(long, long)
	 */
	public void reset(){
		m_objDozent=null;
		m_objRoom=null;
		
		m_strKurstypen="#";
		m_strTermin=null;

		m_strCboTag1="#";
		m_strCboTag2="#";

		m_strCboStartHour1="#";
		m_strCboStartHour2="#";
		m_strCboStartMinute1="#";
		m_strCboStartMinute2="#";

		m_strCboEndHour1="#";
		m_strCboEndHour2="#";
		m_strCboEndMinute1="#";
		m_strCboEndMinute2="#";
		m_intKurstypenCount=0;
		m_bytTerminOpt=-1;
	}

///////////////////////////////////////////////////
// Schrotthaufen
///////////////////////////////////////////////////	
		/**
		 * <pre>
		 * 2011-1-5 Bei der Codeschau in med/Config/Vorlesungen/KursCommitementDelete.jsp verschoben.
		 * 
		 * Deletes all commitments to this course.
		 * 
		 * This is new and still experimental. For that 
		 * reason, it throws a number of undocumented Exceptions:
		 * 
		 * - if there is more than one coursetype mapped ("Cannot drop commitments")
		 * - db related exceptions
		 * 
		 * </pre>
		 * @version 6-14-05 (April 2006)
		 * @return number of records affected.
		 * @throws Exception
		 */
//		public long dropCommitments() throws Exception{
//			// check if this course is mapped to more than 
//			// one course, and throw an Exception if it is:
//			if(getMappingCount()>1) throw new Exception("Cannot drop commitments: course is mapped to more than one type.");
//			
//			// delete the commitments
//			long lReturn = sqlExeCount("delete from \"tblBdStudentXLeistung\" where " +
//					"\"lngSdSeminarID\"=" + getSdSeminarID() + " and " + 
//					"\"lngKlausuranmeldungKursID\"=" + getKursID() + " and " +
//					"\"blnStudentLeistungValidiert\"=" + getDBBoolRepresentation(false) + " AND " +
//					"\"blnStudentLeistungKlausuranmeldung\"=" + getDBBoolRepresentation(true));
//			
//			return lReturn;
//		}	
	
///////////////////////////////////////////////////
// Konstruktor
///////////////////////////////////////////////////	
	
	/**
	 * constructor, intializes extended 'data.Kurs'-object.
	 * @param lngSeminarID: id of seminar the course belongs to,
	 * @param lngKursID: id of course.
	 **/
	public Kurs(long lngSeminarID, long lngKursID) throws Exception{
		this.init(lngSeminarID, lngKursID);
	}
	
	/**
	 *  Empty constructor to allow for the addition of courses.
	 **/
	public Kurs(){}
}