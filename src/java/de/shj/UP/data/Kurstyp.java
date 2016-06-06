
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
 * 
 */

package de.shj.UP.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.naming.NamingException;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import org.w3c.dom.Node; 

/**
 *  Modules: SignUp, Kvv, Exam
Coursetype. Next abstraction level over course. Coursetypes also have a "LeistungsID," viz. a mapped credit.
 *  
 *  ALLGEMEINE INFORMATION ZU DEN OBJEKTEN DIESES PACKAGES:
 *  Diese Klasse kapselt die Tabelle 'Kurstyp' in 
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
public class Kurstyp extends shjCore{

	private static final long serialVersionUID = 7L;

////////////////////////////////////////////////////////////////
// 1.   P R I V A T E   D E K L A R A T I O N E N
////////////////////////////////////////////////////////////////
	
	private boolean m_bIsDirty = false;

	private long m_lSdSeminarID;
	private long m_lKurstypID;
	private String m_sKurstypUnivISID;
	private String m_sKurstypBezeichnung;
	private String m_sKurstypBeschreibung;
	private int m_iKurstypSemesterMinimum;
	private int m_iKurstypKurswahlMin;
	private String m_sKurstypEinordnung;
	private boolean m_bKurstypFormularanmeldung;
	private boolean m_bKurstypKvvOnlyGrouped;
	private int m_iKurstypSequence;
	private long m_lKurstypLeistungsID;
	private float m_sKurstypCreditPts;
	private int m_iKurstypSemesterMin;
	private int m_iKurstypSemesterMax;
	private int m_iKurstypCommitmentMode;
	private boolean m_bKurstypTeilmodul;
	private boolean m_bKurstypVv;
	private String m_sKurstypCustom1;
	private String m_sKurstypCustom2;
	private String m_sKurstypCustom3;
	private String m_sKurstypBezeichnung_en;
	private String m_sKurstypBeschreibung_en;
	private String m_sKurstypCustom1_en;
	private String m_sKurstypCustom2_en;
	private String m_sKurstypCustom3_en;

////////////////////////////////////////////////////////////////
// 2.   Ö F F E N T L I C H E  E I G E N S C H A F T E N
////////////////////////////////////////////////////////////////

	/**
 	 * Wahr, wenn sich das Objekt von der Datenbanktabellenzeile unterscheidet.
	 * @return: Urteil, ob das Objekt noch mit der Datenbanktabellenzeile identisch ist.
	 **/
	public boolean isDirty(){
	  return this.m_bIsDirty;
	}


	/**
	 * Seminar.
	 * @return Seminar.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKurstyp.lngSdSeminarID</Code>
	 **/
	public long getSdSeminarID(){
		return this.m_lSdSeminarID;
	}

	/**
	 * Seminar.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKurstyp.lngSdSeminarID</Code>
	 **/	
	public void setSdSeminarID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lSdSeminarID));
		this.m_lSdSeminarID=value;
	}
	

	/**
	 * Id of coursetype.
	 * @return Id of coursetype.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKurstyp.lngKurstypID</Code>
	 **/
	public long getKurstypID(){
		return this.m_lKurstypID;
	}

	/**
	 * Id of coursetype.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKurstyp.lngKurstypID</Code>
	 **/	
	public void setKurstypID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lKurstypID));
		this.m_lKurstypID=value;
	}
	

	/**
	 * UnivIS-Id (on import). Unused.
	 * @return UnivIS-Id (on import). Unused.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKurstyp.strKurstypUnivISID</Code>
	 **/
	public String getKurstypUnivISID(){
		return this.m_sKurstypUnivISID;
	}

	/**
	 * UnivIS-Id (on import). Unused.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKurstyp.strKurstypUnivISID</Code>
	 **/	
	public void setKurstypUnivISID(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKurstypUnivISID)));
		this.m_sKurstypUnivISID=value;
	}
	

	/**
	 * Name of coursetype.
	 * @return Name of coursetype.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKurstyp.strKurstypBezeichnung</Code>
	 **/
	public String getKurstypBezeichnung(){
		return this.m_sKurstypBezeichnung;
	}

	/**
	 * Name of coursetype.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKurstyp.strKurstypBezeichnung</Code>
	 **/	
	public void setKurstypBezeichnung(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKurstypBezeichnung)));
		this.m_sKurstypBezeichnung=value;
	}
	

	/**
	 * Description of coursetype.
	 * @return Description of coursetype.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKurstyp.strKurstypBeschreibung</Code>
	 **/
	public String getKurstypBeschreibung(){
		return this.m_sKurstypBeschreibung;
	}

	/**
	 * Description of coursetype.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKurstyp.strKurstypBeschreibung</Code>
	 **/	
	public void setKurstypBeschreibung(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKurstypBeschreibung)));
		this.m_sKurstypBeschreibung=value;
	}
	

	/**
	 * Minimum semester to attend courses of this type.
	 * @return Minimum semester to attend courses of this type.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKurstyp.intKurstypSemesterMinimum</Code>
	 **/
	public int getKurstypSemesterMinimum(){
		return this.m_iKurstypSemesterMinimum;
	}

	/**
	 * Minimum semester to attend courses of this type.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKurstyp.intKurstypSemesterMinimum</Code>
	 **/	
	public void setKurstypSemesterMinimum(int value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_iKurstypSemesterMinimum));
		this.m_iKurstypSemesterMinimum=value;
	}
	

	/**
	 * When signing up for classes, how many classes of this type must the students 
			choose at least? (Typically two or three, German dept. Heidelberg has 8 for medieval German).
	 * @return When signing up for classes, how many classes of this type must the students 
			choose at least? (Typically two or three, German dept. Heidelberg has 8 for medieval German).
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKurstyp.intKurstypKurswahlMin</Code>
	 **/
	public int getKurstypKurswahlMin(){
		return this.m_iKurstypKurswahlMin;
	}

	/**
	 * When signing up for classes, how many classes of this type must the students 
			choose at least? (Typically two or three, German dept. Heidelberg has 8 for medieval German).
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKurstyp.intKurstypKurswahlMin</Code>
	 **/	
	public void setKurstypKurswahlMin(int value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_iKurstypKurswahlMin));
		this.m_iKurstypKurswahlMin=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKurstyp.strKurstypEinordnung</Code>
	 **/
	public String getKurstypEinordnung(){
		return this.m_sKurstypEinordnung;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKurstyp.strKurstypEinordnung</Code>
	 **/	
	public void setKurstypEinordnung(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKurstypEinordnung)));
		this.m_sKurstypEinordnung=value;
	}
	

	/**
	 * Does this coursetype use SignUp for its applications?
	 * @return Does this coursetype use SignUp for its applications?
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKurstyp.blnKurstypFormularanmeldung</Code>
	 **/
	public boolean getKurstypFormularanmeldung(){
		return this.m_bKurstypFormularanmeldung;
	}

	/**
	 * Does this coursetype use SignUp for its applications?
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKurstyp.blnKurstypFormularanmeldung</Code>
	 **/	
	public void setKurstypFormularanmeldung(boolean value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_bKurstypFormularanmeldung));
		this.m_bKurstypFormularanmeldung=value;
	}
	

	/**
	 * Do courses of this type have descriptions (in this case, onlyGrouped is false). This is used in module Kvv to display groups of courses without descriptions in a comprehensive manner.
	 * @return Do courses of this type have descriptions (in this case, onlyGrouped is false). This is used in module Kvv to display groups of courses without descriptions in a comprehensive manner.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKurstyp.blnKurstypKvvOnlyGrouped</Code>
	 **/
	public boolean getKurstypKvvOnlyGrouped(){
		return this.m_bKurstypKvvOnlyGrouped;
	}

	/**
	 * Do courses of this type have descriptions (in this case, onlyGrouped is false). This is used in module Kvv to display groups of courses without descriptions in a comprehensive manner.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKurstyp.blnKurstypKvvOnlyGrouped</Code>
	 **/	
	public void setKurstypKvvOnlyGrouped(boolean value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_bKurstypKvvOnlyGrouped));
		this.m_bKurstypKvvOnlyGrouped=value;
	}
	

	/**
	 * Sequence (in Module Kvv).
	 * @return Sequence (in Module Kvv).
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKurstyp.intKurstypSequence</Code>
	 **/
	public int getKurstypSequence(){
		return this.m_iKurstypSequence;
	}

	/**
	 * Sequence (in Module Kvv).
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKurstyp.intKurstypSequence</Code>
	 **/	
	public void setKurstypSequence(int value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_iKurstypSequence));
		this.m_iKurstypSequence=value;
	}
	

	/**
	 * What credit can you get when you take part in courses of this type?
	 * @return What credit can you get when you take part in courses of this type?
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKurstyp.lngKurstypLeistungsID</Code>
	 **/
	public long getKurstypLeistungsID(){
		return this.m_lKurstypLeistungsID;
	}

	/**
	 * What credit can you get when you take part in courses of this type?
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKurstyp.lngKurstypLeistungsID</Code>
	 **/	
	public void setKurstypLeistungsID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lKurstypLeistungsID));
		this.m_lKurstypLeistungsID=value;
	}
	

	/**
	 * How many credit pts. do you get (usually) when you take place in a course of this type?
	 * @return How many credit pts. do you get (usually) when you take place in a course of this type?
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKurstyp.sngKurstypCreditPts</Code>
	 **/
	public float getKurstypCreditPts(){
		return this.m_sKurstypCreditPts;
	}

	/**
	 * How many credit pts. do you get (usually) when you take place in a course of this type?
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKurstyp.sngKurstypCreditPts</Code>
	 **/	
	public void setKurstypCreditPts(float value){
		this.m_bIsDirty = (this.m_bIsDirty || value != m_sKurstypCreditPts);
		this.m_sKurstypCreditPts=value;
	}
	

	/**
	 * Minimum semester to attend courses of this type.
	 * @return Minimum semester to attend courses of this type.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKurstyp.intKurstypSemesterMin</Code>
	 **/
	public int getKurstypSemesterMin(){
		return this.m_iKurstypSemesterMin;
	}

	/**
	 * Minimum semester to attend courses of this type.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKurstyp.intKurstypSemesterMin</Code>
	 **/	
	public void setKurstypSemesterMin(int value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_iKurstypSemesterMin));
		this.m_iKurstypSemesterMin=value;
	}
	

	/**
	 * Maximum semester to attend courses of this type.
	 * @return Maximum semester to attend courses of this type.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKurstyp.intKurstypSemesterMax</Code>
	 **/
	public int getKurstypSemesterMax(){
		return this.m_iKurstypSemesterMax;
	}

	/**
	 * Maximum semester to attend courses of this type.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKurstyp.intKurstypSemesterMax</Code>
	 **/	
	public void setKurstypSemesterMax(int value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_iKurstypSemesterMax));
		this.m_iKurstypSemesterMax=value;
	}
	

	/**
	 * The default CommitmentMode to suggest for this coursetype. Commitment modes are: 0=student can commit for both Pruefungsleistung or Studienleistung. 1=student can commit for STUDIENLEISTUNG ONLY. 2=student can commit for PRUEFUNGSLEISTUNG ONLY.
	 * @return The default CommitmentMode to suggest for this coursetype. Commitment modes are: 0=student can commit for both Pruefungsleistung or Studienleistung. 1=student can commit for STUDIENLEISTUNG ONLY. 2=student can commit for PRUEFUNGSLEISTUNG ONLY.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKurstyp.intKurstypCommitmentMode</Code>
	 **/
	public int getKurstypCommitmentMode(){
		return this.m_iKurstypCommitmentMode;
	}

	/**
	 * The default CommitmentMode to suggest for this coursetype. Commitment modes are: 0=student can commit for both Pruefungsleistung or Studienleistung. 1=student can commit for STUDIENLEISTUNG ONLY. 2=student can commit for PRUEFUNGSLEISTUNG ONLY.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKurstyp.intKurstypCommitmentMode</Code>
	 **/	
	public void setKurstypCommitmentMode(int value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_iKurstypCommitmentMode));
		this.m_iKurstypCommitmentMode=value;
	}
	

	/**
	 * Is this coursetype a sub-module of itself? Is it for use in Module Exam rather than Kvv? (#hack this description).
	 * @return Is this coursetype a sub-module of itself? Is it for use in Module Exam rather than Kvv? (#hack this description).
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKurstyp.blnKurstypTeilmodul</Code>
	 **/
	public boolean getKurstypTeilmodul(){
		return this.m_bKurstypTeilmodul;
	}

	/**
	 * Is this coursetype a sub-module of itself? Is it for use in Module Exam rather than Kvv? (#hack this description).
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKurstyp.blnKurstypTeilmodul</Code>
	 **/	
	public void setKurstypTeilmodul(boolean value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_bKurstypTeilmodul));
		this.m_bKurstypTeilmodul=value;
	}
	

	/**
	 * Is this coursetype for Module Kvv?
	 * @return Is this coursetype for Module Kvv?
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKurstyp.blnKurstypVv</Code>
	 **/
	public boolean getKurstypVv(){
		return this.m_bKurstypVv;
	}

	/**
	 * Is this coursetype for Module Kvv?
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKurstyp.blnKurstypVv</Code>
	 **/	
	public void setKurstypVv(boolean value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_bKurstypVv));
		this.m_bKurstypVv=value;
	}
	

	/**
	 * Custom
	 * @return Custom
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKurstyp.strKurstypCustom1</Code>
	 **/
	public String getKurstypCustom1(){
		return this.m_sKurstypCustom1;
	}

	/**
	 * Custom
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKurstyp.strKurstypCustom1</Code>
	 **/	
	public void setKurstypCustom1(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKurstypCustom1)));
		this.m_sKurstypCustom1=value;
	}
	

	/**
	 * Custom
	 * @return Custom
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKurstyp.strKurstypCustom2</Code>
	 **/
	public String getKurstypCustom2(){
		return this.m_sKurstypCustom2;
	}

	/**
	 * Custom
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKurstyp.strKurstypCustom2</Code>
	 **/	
	public void setKurstypCustom2(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKurstypCustom2)));
		this.m_sKurstypCustom2=value;
	}
	

	/**
	 * Custom
	 * @return Custom
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKurstyp.strKurstypCustom3</Code>
	 **/
	public String getKurstypCustom3(){
		return this.m_sKurstypCustom3;
	}

	/**
	 * Custom
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKurstyp.strKurstypCustom3</Code>
	 **/	
	public void setKurstypCustom3(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKurstypCustom3)));
		this.m_sKurstypCustom3=value;
	}
	

	/**
	 * English version of this coursetype"s name
	 * @return English version of this coursetype"s name
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKurstyp.strKurstypBezeichnung_en</Code>
	 **/
	public String getKurstypBezeichnung_en(){
		return this.m_sKurstypBezeichnung_en;
	}

	/**
	 * English version of this coursetype"s name
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKurstyp.strKurstypBezeichnung_en</Code>
	 **/	
	public void setKurstypBezeichnung_en(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKurstypBezeichnung_en)));
		this.m_sKurstypBezeichnung_en=value;
	}
	

	/**
	 * English version of this coursetype"s description.
	 * @return English version of this coursetype"s description.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKurstyp.strKurstypBeschreibung_en</Code>
	 **/
	public String getKurstypBeschreibung_en(){
		return this.m_sKurstypBeschreibung_en;
	}

	/**
	 * English version of this coursetype"s description.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKurstyp.strKurstypBeschreibung_en</Code>
	 **/	
	public void setKurstypBeschreibung_en(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKurstypBeschreibung_en)));
		this.m_sKurstypBeschreibung_en=value;
	}
	

	/**
	 * English version of custom
	 * @return English version of custom
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKurstyp.strKurstypCustom1_en</Code>
	 **/
	public String getKurstypCustom1_en(){
		return this.m_sKurstypCustom1_en;
	}

	/**
	 * English version of custom
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKurstyp.strKurstypCustom1_en</Code>
	 **/	
	public void setKurstypCustom1_en(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKurstypCustom1_en)));
		this.m_sKurstypCustom1_en=value;
	}
	

	/**
	 * English version of custom
	 * @return English version of custom
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKurstyp.strKurstypCustom2_en</Code>
	 **/
	public String getKurstypCustom2_en(){
		return this.m_sKurstypCustom2_en;
	}

	/**
	 * English version of custom
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKurstyp.strKurstypCustom2_en</Code>
	 **/	
	public void setKurstypCustom2_en(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKurstypCustom2_en)));
		this.m_sKurstypCustom2_en=value;
	}
	

	/**
	 * English version of custom
	 * @return English version of custom
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKurstyp.strKurstypCustom3_en</Code>
	 **/
	public String getKurstypCustom3_en(){
		return this.m_sKurstypCustom3_en;
	}

	/**
	 * English version of custom
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKurstyp.strKurstypCustom3_en</Code>
	 **/	
	public void setKurstypCustom3_en(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKurstypCustom3_en)));
		this.m_sKurstypCustom3_en=value;
	}
	


////////////////////////////////////////////////////////////////
// 3.   X M L  U T I L I T I E S
////////////////////////////////////////////////////////////////
	
	/**
	 * @return Objekt als XML-String
	 **/
	public String toXMLString(){
		return 	

			"<SdSeminarID>" + m_lSdSeminarID + "</SdSeminarID>"  + 
			"<KurstypID>" + m_lKurstypID + "</KurstypID>"  + 
			"<KurstypUnivISID>" + m_sKurstypUnivISID + "</KurstypUnivISID>"  + 
			"<KurstypBezeichnung>" + m_sKurstypBezeichnung + "</KurstypBezeichnung>"  + 
			"<KurstypBeschreibung>" + m_sKurstypBeschreibung + "</KurstypBeschreibung>"  + 
			"<KurstypSemesterMinimum>" + m_iKurstypSemesterMinimum + "</KurstypSemesterMinimum>"  + 
			"<KurstypKurswahlMin>" + m_iKurstypKurswahlMin + "</KurstypKurswahlMin>"  + 
			"<KurstypEinordnung>" + m_sKurstypEinordnung + "</KurstypEinordnung>"  + 
			"<KurstypFormularanmeldung>" + m_bKurstypFormularanmeldung + "</KurstypFormularanmeldung>"  + 
			"<KurstypKvvOnlyGrouped>" + m_bKurstypKvvOnlyGrouped + "</KurstypKvvOnlyGrouped>"  + 
			"<KurstypSequence>" + m_iKurstypSequence + "</KurstypSequence>"  + 
			"<KurstypLeistungsID>" + m_lKurstypLeistungsID + "</KurstypLeistungsID>"  + 
			"<KurstypCreditPts>" + m_sKurstypCreditPts + "</KurstypCreditPts>"  + 
			"<KurstypSemesterMin>" + m_iKurstypSemesterMin + "</KurstypSemesterMin>"  + 
			"<KurstypSemesterMax>" + m_iKurstypSemesterMax + "</KurstypSemesterMax>"  + 
			"<KurstypCommitmentMode>" + m_iKurstypCommitmentMode + "</KurstypCommitmentMode>"  + 
			"<KurstypTeilmodul>" + m_bKurstypTeilmodul + "</KurstypTeilmodul>"  + 
			"<KurstypVv>" + m_bKurstypVv + "</KurstypVv>"  + 
			"<KurstypCustom1>" + m_sKurstypCustom1 + "</KurstypCustom1>"  + 
			"<KurstypCustom2>" + m_sKurstypCustom2 + "</KurstypCustom2>"  + 
			"<KurstypCustom3>" + m_sKurstypCustom3 + "</KurstypCustom3>"  + 
			"<KurstypBezeichnung_en>" + m_sKurstypBezeichnung_en + "</KurstypBezeichnung_en>"  + 
			"<KurstypBeschreibung_en>" + m_sKurstypBeschreibung_en + "</KurstypBeschreibung_en>"  + 
			"<KurstypCustom1_en>" + m_sKurstypCustom1_en + "</KurstypCustom1_en>"  + 
			"<KurstypCustom2_en>" + m_sKurstypCustom2_en + "</KurstypCustom2_en>"  + 
			"<KurstypCustom3_en>" + m_sKurstypCustom3_en + "</KurstypCustom3_en>" ;
	}

////////////////////////////////////////////////////////////////
// 4.   S Q L  U T I L I T I E S
////////////////////////////////////////////////////////////////


	/**
	 * Where-SQL für eindeutigen Index dieses Objekte in der Datenbank (in Tabelle 'tblSdKurstyp.').
	 * @return SQL-String wie z.B. "Id1=? and Id2=?"
	 **/
	private String getSQLWhereClause(){
		return 
			"\"lngSdSeminarID\"=? AND " + 
			"\"lngKurstypID\"=?";
	}

	/**
	 * SQL-Befehl zum Löschen des Objekts in der Datenbank (aus Tabelle 'tblSdKurstyp.')
	 * @return SQL-String zum Löschen des Objekts, z.B. "delete from T1 where (Id1=?);"
	 **/
	private String toDBDeleteString(){
		return "delete from \"tblSdKurstyp\" where ( " + this.getSQLWhereClause() + ");";
	}

	/**
 	 * Setzt die Werte für alle Platzhalter in {@link #getSQLWhereClause()}.
	 * @param ii Offset für die Parameter (inklusive).
	 * @throws SQLException 
	 **/
	private void pokeWhere(int ii, PreparedStatement ps) throws SQLException{
		
		ps.setLong(ii++, m_lSdSeminarID);
		ps.setLong(ii++, m_lKurstypID);
	}

	/**
  	 * Füllt das SQL-Statement mit den aktuellen Werten des Objekts
	 * @throws SQLException
	 **/
	private void pokeStatement(PreparedStatement ps) throws SQLException{
		
		ps.setLong(1, m_lSdSeminarID);
		ps.setLong(2, m_lKurstypID);
		ps.setString(3, m_sKurstypUnivISID);
		ps.setString(4, m_sKurstypBezeichnung);
		ps.setString(5, m_sKurstypBeschreibung);
		ps.setInt(6, m_iKurstypSemesterMinimum);
		ps.setInt(7, m_iKurstypKurswahlMin);
		ps.setString(8, m_sKurstypEinordnung);
		ps.setBoolean(9, m_bKurstypFormularanmeldung);
		ps.setBoolean(10, m_bKurstypKvvOnlyGrouped);
		ps.setInt(11, m_iKurstypSequence);
		ps.setLong(12, m_lKurstypLeistungsID);
		ps.setFloat(13, m_sKurstypCreditPts);
		ps.setInt(14, m_iKurstypSemesterMin);
		ps.setInt(15, m_iKurstypSemesterMax);
		ps.setInt(16, m_iKurstypCommitmentMode);
		ps.setBoolean(17, m_bKurstypTeilmodul);
		ps.setBoolean(18, m_bKurstypVv);
		ps.setString(19, m_sKurstypCustom1);
		ps.setString(20, m_sKurstypCustom2);
		ps.setString(21, m_sKurstypCustom3);
		ps.setString(22, m_sKurstypBezeichnung_en);
		ps.setString(23, m_sKurstypBeschreibung_en);
		ps.setString(24, m_sKurstypCustom1_en);
		ps.setString(25, m_sKurstypCustom2_en);
		ps.setString(26, m_sKurstypCustom3_en);

	}

	/**
	 * SQL-Befehl zum Speichern der Objekteigenschaften in die Datenbank (Tabelle 'tblSdKurstyp.')
	 * @return PreparedStatemtent, das nach Aufruf von {@link #pokeStatement(PreparedStatement)} und {@link #execute()} das Update ausführt.
	 **/
	private String toDBUpdateString(){
		return "update \"tblSdKurstyp\" set " +
			"\"lngSdSeminarID\"=?, " +
			"\"lngKurstypID\"=?, " +
			"\"strKurstypUnivISID\"=?, " +
			"\"strKurstypBezeichnung\"=?, " +
			"\"strKurstypBeschreibung\"=?, " +
			"\"intKurstypSemesterMinimum\"=?, " +
			"\"intKurstypKurswahlMin\"=?, " +
			"\"strKurstypEinordnung\"=?, " +
			"\"blnKurstypFormularanmeldung\"=?, " +
			"\"blnKurstypKvvOnlyGrouped\"=?, " +
			"\"intKurstypSequence\"=?, " +
			"\"lngKurstypLeistungsID\"=?, " +
			"\"sngKurstypCreditPts\"=?, " +
			"\"intKurstypSemesterMin\"=?, " +
			"\"intKurstypSemesterMax\"=?, " +
			"\"intKurstypCommitmentMode\"=?, " +
			"\"blnKurstypTeilmodul\"=?, " +
			"\"blnKurstypVv\"=?, " +
			"\"strKurstypCustom1\"=?, " +
			"\"strKurstypCustom2\"=?, " +
			"\"strKurstypCustom3\"=?, " +
			"\"strKurstypBezeichnung_en\"=?, " +
			"\"strKurstypBeschreibung_en\"=?, " +
			"\"strKurstypCustom1_en\"=?, " +
			"\"strKurstypCustom2_en\"=?, " +
			"\"strKurstypCustom3_en\"=?" +
			" where (" + this.getSQLWhereClause() + ");";
	}

	/**
	 * SQL-Befehl zum Hinzufügen des Objekts in die Datenbank (Tabelle 'tblSdKurstyp.')
	 * @return SQL-String "insert into...".
	 **/
	private String toDBAddString(){
		return "insert into \"tblSdKurstyp\" ( " +
			"\"lngSdSeminarID\", \"lngKurstypID\", \"strKurstypUnivISID\", \"strKurstypBezeichnung\", \"strKurstypBeschreibung\", \"intKurstypSemesterMinimum\", \"intKurstypKurswahlMin\", \"strKurstypEinordnung\", \"blnKurstypFormularanmeldung\", \"blnKurstypKvvOnlyGrouped\", \"intKurstypSequence\", \"lngKurstypLeistungsID\", \"sngKurstypCreditPts\", \"intKurstypSemesterMin\", \"intKurstypSemesterMax\", \"intKurstypCommitmentMode\", \"blnKurstypTeilmodul\", \"blnKurstypVv\", \"strKurstypCustom1\", \"strKurstypCustom2\", \"strKurstypCustom3\", \"strKurstypBezeichnung_en\", \"strKurstypBeschreibung_en\", \"strKurstypCustom1_en\", \"strKurstypCustom2_en\", \"strKurstypCustom3_en\" ) VALUES ( ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
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
	private void init(long lngSdSeminarID, long lngKurstypID) throws SQLException, NamingException{

		this.m_lSdSeminarID=lngSdSeminarID;

		this.m_lKurstypID=lngKurstypID;

		
		PreparedStatement pstm = prepareStatement("select * from \"tblSdKurstyp\" where (" + this.getSQLWhereClause() + ");");
		pokeWhere(1,pstm);

		ResultSet rst=pstm.executeQuery();
		if(rst.next()) this.initByRst(rst);
		rst.close();
		this.disconnect();
		rst=null;
	}	
	
	/**
	 * Lade die Objekteigenschaften aus einem ResultSet.
	 * param rst ResultSet mit allen Spalten der Tabelle 'tblSdKurstyp'
	 * @throws SQLException 
	 **/
	protected void initByRst(ResultSet rst) throws SQLException{
		this.m_lSdSeminarID=rst.getLong("lngSdSeminarID");
		this.m_lKurstypID=rst.getLong("lngKurstypID");
		this.m_sKurstypUnivISID=rst.getString("strKurstypUnivISID");
		this.m_sKurstypBezeichnung=rst.getString("strKurstypBezeichnung");
		this.m_sKurstypBeschreibung=rst.getString("strKurstypBeschreibung");
		this.m_iKurstypSemesterMinimum=rst.getInt("intKurstypSemesterMinimum");
		this.m_iKurstypKurswahlMin=rst.getInt("intKurstypKurswahlMin");
		this.m_sKurstypEinordnung=rst.getString("strKurstypEinordnung");
		this.m_bKurstypFormularanmeldung=rst.getBoolean("blnKurstypFormularanmeldung");
		this.m_bKurstypKvvOnlyGrouped=rst.getBoolean("blnKurstypKvvOnlyGrouped");
		this.m_iKurstypSequence=rst.getInt("intKurstypSequence");
		this.m_lKurstypLeistungsID=rst.getLong("lngKurstypLeistungsID");
		this.m_sKurstypCreditPts=rst.getFloat("sngKurstypCreditPts");
		this.m_iKurstypSemesterMin=rst.getInt("intKurstypSemesterMin");
		this.m_iKurstypSemesterMax=rst.getInt("intKurstypSemesterMax");
		this.m_iKurstypCommitmentMode=rst.getInt("intKurstypCommitmentMode");
		this.m_bKurstypTeilmodul=rst.getBoolean("blnKurstypTeilmodul");
		this.m_bKurstypVv=rst.getBoolean("blnKurstypVv");
		this.m_sKurstypCustom1=rst.getString("strKurstypCustom1");
		this.m_sKurstypCustom2=rst.getString("strKurstypCustom2");
		this.m_sKurstypCustom3=rst.getString("strKurstypCustom3");
		this.m_sKurstypBezeichnung_en=rst.getString("strKurstypBezeichnung_en");
		this.m_sKurstypBeschreibung_en=rst.getString("strKurstypBeschreibung_en");
		this.m_sKurstypCustom1_en=rst.getString("strKurstypCustom1_en");
		this.m_sKurstypCustom2_en=rst.getString("strKurstypCustom2_en");
		this.m_sKurstypCustom3_en=rst.getString("strKurstypCustom3_en");	
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
	 **/
	public boolean add() throws SQLException, NamingException{
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
         **/
	public boolean update() throws SQLException, NamingException{
		if( !(isDirty()) ) return true;
		boolean bReturn = false;

		// Lade Statement mit SQL
		PreparedStatement pstm = prepareStatement(toDBUpdateString());

		// Lade Objekteigenschaften in Statement
		pokeStatement(pstm);

		// Identifiziere das Objekt (bzw. die Tabellenzeile) per Where-Clause
		// und übermittle die neuen Werte an die Datenbank
		pokeWhere(27,pstm);
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
	public Kurstyp(){}	
	
	/**
	 * Konstruktor mit Indexwerten (eindeutig) der assoziierten Datenbanktabelle
	 * @throws NamingException 
	 * @throws SQLException 
	 **/
	public Kurstyp(long lngSdSeminarID, long lngKurstypID) throws SQLException, NamingException{
		this.init(lngSdSeminarID, lngKurstypID);
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
	public Kurstyp(ResultSet rst) throws SQLException{
		this.initByRst(rst);
		this.m_bIsDirty = false;
	}
  }//Klassenende
