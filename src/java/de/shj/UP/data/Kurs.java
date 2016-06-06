
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
import java.sql.Time;
import java.sql.Date;
import java.sql.Timestamp;

import javax.naming.NamingException;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import org.w3c.dom.Node; 

/**
 *  Modules: SignUp, Kvv, Exam
Courses of an institute. In this table/class, courses of the current semester and the next semester in the future ("blnKursPlanungssemester") are stored. Old courses go to KvvArchiv.
 *  
 *  ALLGEMEINE INFORMATION ZU DEN OBJEKTEN DIESES PACKAGES:
 *  Diese Klasse kapselt die Tabelle 'Kurs' in 
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
public class Kurs extends shjCore{

	private static final long serialVersionUID = 7L;

////////////////////////////////////////////////////////////////
// 1.   P R I V A T E   D E K L A R A T I O N E N
////////////////////////////////////////////////////////////////
	
	private boolean m_bIsDirty = false;

	private long m_lSdSeminarID;
	private long m_lKursID;
	private String m_sKursUnivISID;
	private long m_lDozentID;
	private String m_sKursTag;
	private Time m_tKursBeginn;
	private Time m_tKursEnde;
	private String m_sKursRaum;
	private String m_sKursTag2;
	private Time m_tKursBeginn2;
	private Time m_tKursEnde2;
	private String m_sKursRaum2;
	private long m_lKursTeilnehmerMaximal;
	private long m_lKursRequests;
	private String m_sKursTitel;
	private String m_sKursTitel_en;
	private String m_sKursBeschreibung;
	private String m_sKursBeschreibung_en;
	private String m_sKursLiteratur;
	private String m_sKursZusatz;
	private String m_sKursAnmeldung;
	private String m_sKursVoraussetzung;
	private boolean m_bKursSchein;
	private String m_sKursEinordnung;
	private int m_iKursStunden;
	private Timestamp m_tKursLastChange;
	private Date m_dKursScheinanmeldungBis;
	private Date m_dKursScheinanmeldungVon;
	private boolean m_bKursScheinanmeldungErlaubt;
	private int m_iKursSequence;
	private boolean m_bKursPlanungssemester;
	private String m_sKursTerminFreitext;
	private int m_iKursTeilnehmer;
	private String m_sKursRaumExtern1;
	private String m_sKursRaumExtern2;
	private String m_sKursCustom1;
	private String m_sKursCustom2;
	private String m_sKursCustom3;
	private float m_sKursCreditPts;

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
	 * Seminar or institute where this course is taught.
	 * @return Seminar or institute where this course is taught.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.lngSdSeminarID</Code>
	 **/
	public long getSdSeminarID(){
		return this.m_lSdSeminarID;
	}

	/**
	 * Seminar or institute where this course is taught.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.lngSdSeminarID</Code>
	 **/	
	public void setSdSeminarID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lSdSeminarID));
		this.m_lSdSeminarID=value;
	}
	

	/**
	 * Id of course (unique in combination with SeminarID).
	 * @return Id of course (unique in combination with SeminarID).
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.lngKursID</Code>
	 **/
	public long getKursID(){
		return this.m_lKursID;
	}

	/**
	 * Id of course (unique in combination with SeminarID).
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.lngKursID</Code>
	 **/	
	public void setKursID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lKursID));
		this.m_lKursID=value;
	}
	

	/**
	 * UnivIS-Id of this course (for imported courses).
	 * @return UnivIS-Id of this course (for imported courses).
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.strKursUnivISID</Code>
	 **/
	public String getKursUnivISID(){
		return this.m_sKursUnivISID;
	}

	/**
	 * UnivIS-Id of this course (for imported courses).
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.strKursUnivISID</Code>
	 **/	
	public void setKursUnivISID(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKursUnivISID)));
		this.m_sKursUnivISID=value;
	}
	

	/**
	 * Id of teacher who teaches this course. Currently, if there is more than one teacher, you will have to create a new pseudo-teacher with two or more names.
	 * @return Id of teacher who teaches this course. Currently, if there is more than one teacher, you will have to create a new pseudo-teacher with two or more names.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.lngDozentID</Code>
	 **/
	public long getDozentID(){
		return this.m_lDozentID;
	}

	/**
	 * Id of teacher who teaches this course. Currently, if there is more than one teacher, you will have to create a new pseudo-teacher with two or more names.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.lngDozentID</Code>
	 **/	
	public void setDozentID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lDozentID));
		this.m_lDozentID=value;
	}
	

	/**
	 * If course takes place on a weekly basis, this is day 1.
	 * @return If course takes place on a weekly basis, this is day 1.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.strKursTag</Code>
	 **/
	public String getKursTag(){
		return this.m_sKursTag;
	}

	/**
	 * If course takes place on a weekly basis, this is day 1.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.strKursTag</Code>
	 **/	
	public void setKursTag(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKursTag)));
		this.m_sKursTag=value;
	}
	

	/**
	 * If course takes place on a weekly basis, this the time when the course begins on day 1.
	 * @return If course takes place on a weekly basis, this the time when the course begins on day 1.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.dtmKursBeginn</Code>
	 **/
	public Time getKursBeginn(){
		return this.m_tKursBeginn;
	}

	/**
	 * If course takes place on a weekly basis, this the time when the course begins on day 1.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.dtmKursBeginn</Code>
	 **/	
	public void setKursBeginn(Time value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_tKursBeginn)));
		this.m_tKursBeginn=value;
	}
	

	/**
	 * If course takes place on a weekly basis, this time when the course ends on day 1.
	 * @return If course takes place on a weekly basis, this time when the course ends on day 1.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.dtmKursEnde</Code>
	 **/
	public Time getKursEnde(){
		return this.m_tKursEnde;
	}

	/**
	 * If course takes place on a weekly basis, this time when the course ends on day 1.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.dtmKursEnde</Code>
	 **/	
	public void setKursEnde(Time value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(m_tKursEnde)));
		this.m_tKursEnde=value;
	}
	

	/**
	 * If course takes place on a weekly basis, this is the room where the course takes place on day 1.
	 * @return If course takes place on a weekly basis, this is the room where the course takes place on day 1.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.strKursRaum</Code>
	 **/
	public String getKursRaum(){
		return this.m_sKursRaum;
	}

	/**
	 * If course takes place on a weekly basis, this is the room where the course takes place on day 1.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.strKursRaum</Code>
	 **/	
	public void setKursRaum(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKursRaum)));
		this.m_sKursRaum=value;
	}
	

	/**
	 * If course takes place on a weekly basis, this is day 2.
	 * @return If course takes place on a weekly basis, this is day 2.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.strKursTag2</Code>
	 **/
	public String getKursTag2(){
		return this.m_sKursTag2;
	}

	/**
	 * If course takes place on a weekly basis, this is day 2.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.strKursTag2</Code>
	 **/	
	public void setKursTag2(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKursTag2)));
		this.m_sKursTag2=value;
	}
	

	/**
	 * If course takes place on a weekly basis, this the time when the course begins on day 2.
	 * @return If course takes place on a weekly basis, this the time when the course begins on day 2.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.dtmKursBeginn2</Code>
	 **/
	public Time getKursBeginn2(){
		return this.m_tKursBeginn2;
	}

	/**
	 * If course takes place on a weekly basis, this the time when the course begins on day 2.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.dtmKursBeginn2</Code>
	 **/	
	public void setKursBeginn2(Time value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_tKursBeginn2)));
		this.m_tKursBeginn2=value;
	}
	

	/**
	 * If course takes place on a weekly basis, this the time when the course ends on day 2.
	 * @return If course takes place on a weekly basis, this the time when the course ends on day 2.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.dtmKursEnde2</Code>
	 **/
	public Time getKursEnde2(){
		return this.m_tKursEnde2;
	}

	/**
	 * If course takes place on a weekly basis, this the time when the course ends on day 2.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.dtmKursEnde2</Code>
	 **/	
	public void setKursEnde2(Time value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_tKursEnde2)));
		this.m_tKursEnde2=value;
	}
	

	/**
	 * If course takes place on a weekly basis, this is the room where the course takes place on day 2.
	 * @return If course takes place on a weekly basis, this is the room where the course takes place on day 2.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.strKursRaum2</Code>
	 **/
	public String getKursRaum2(){
		return this.m_sKursRaum2;
	}

	/**
	 * If course takes place on a weekly basis, this is the room where the course takes place on day 2.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.strKursRaum2</Code>
	 **/	
	public void setKursRaum2(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKursRaum2)));
		this.m_sKursRaum2=value;
	}
	

	/**
	 * Maximum number of participants in this course. Relevant for the distribution of students managed in module "SignUp."
	 * @return Maximum number of participants in this course. Relevant for the distribution of students managed in module "SignUp."
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.intKursTeilnehmerMaximal</Code>
	 **/
	public long getKursTeilnehmerMaximal(){
		return this.m_lKursTeilnehmerMaximal;
	}

	/**
	 * Maximum number of participants in this course. Relevant for the distribution of students managed in module "SignUp."
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.intKursTeilnehmerMaximal</Code>
	 **/	
	public void setKursTeilnehmerMaximal(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lKursTeilnehmerMaximal));
		this.m_lKursTeilnehmerMaximal=value;
	}
	

	/**
	 * Number of students who requested a place in this course.
	 * @return Number of students who requested a place in this course.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.lngKursRequests</Code>
	 **/
	public long getKursRequests(){
		return this.m_lKursRequests;
	}

	/**
	 * Number of students who requested a place in this course.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.lngKursRequests</Code>
	 **/	
	public void setKursRequests(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lKursRequests));
		this.m_lKursRequests=value;
	}
	

	/**
	 * Title of this course.
	 * @return Title of this course.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.strKursTitel</Code>
	 **/
	public String getKursTitel(){
		return this.m_sKursTitel;
	}

	/**
	 * Title of this course.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.strKursTitel</Code>
	 **/	
	public void setKursTitel(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKursTitel)));
		this.m_sKursTitel=value;
	}
	

	/**
	 * English version of the title of this course.
	 * @return English version of the title of this course.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.strKursTitel_en</Code>
	 **/
	public String getKursTitel_en(){
		return this.m_sKursTitel_en;
	}

	/**
	 * English version of the title of this course.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.strKursTitel_en</Code>
	 **/	
	public void setKursTitel_en(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKursTitel_en)));
		this.m_sKursTitel_en=value;
	}
	

	/**
	 * Description of this course"s contents.
	 * @return Description of this course"s contents.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.strKursBeschreibung</Code>
	 **/
	public String getKursBeschreibung(){
		return this.m_sKursBeschreibung;
	}

	/**
	 * Description of this course"s contents.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.strKursBeschreibung</Code>
	 **/	
	public void setKursBeschreibung(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKursBeschreibung)));
		this.m_sKursBeschreibung=value;
	}
	

	/**
	 * English version of the description of this course"s contents.
	 * @return English version of the description of this course"s contents.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.strKursBeschreibung_en</Code>
	 **/
	public String getKursBeschreibung_en(){
		return this.m_sKursBeschreibung_en;
	}

	/**
	 * English version of the description of this course"s contents.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.strKursBeschreibung_en</Code>
	 **/	
	public void setKursBeschreibung_en(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKursBeschreibung_en)));
		this.m_sKursBeschreibung_en=value;
	}
	

	/**
	 * What literature does the teacher recommend as introductory reading for this course?
	 * @return What literature does the teacher recommend as introductory reading for this course?
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.strKursLiteratur</Code>
	 **/
	public String getKursLiteratur(){
		return this.m_sKursLiteratur;
	}

	/**
	 * What literature does the teacher recommend as introductory reading for this course?
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.strKursLiteratur</Code>
	 **/	
	public void setKursLiteratur(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKursLiteratur)));
		this.m_sKursLiteratur=value;
	}
	

	/**
	 * Additional information.
	 * @return Additional information.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.strKursZusatz</Code>
	 **/
	public String getKursZusatz(){
		return this.m_sKursZusatz;
	}

	/**
	 * Additional information.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.strKursZusatz</Code>
	 **/	
	public void setKursZusatz(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKursZusatz)));
		this.m_sKursZusatz=value;
	}
	

	/**
	 * What does a student have to do to register for this course?
	 * @return What does a student have to do to register for this course?
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.strKursAnmeldung</Code>
	 **/
	public String getKursAnmeldung(){
		return this.m_sKursAnmeldung;
	}

	/**
	 * What does a student have to do to register for this course?
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.strKursAnmeldung</Code>
	 **/	
	public void setKursAnmeldung(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKursAnmeldung)));
		this.m_sKursAnmeldung=value;
	}
	

	/**
	 * What prerequisites does a student have to fulfil in order to take part in this course?
	 * @return What prerequisites does a student have to fulfil in order to take part in this course?
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.strKursVoraussetzung</Code>
	 **/
	public String getKursVoraussetzung(){
		return this.m_sKursVoraussetzung;
	}

	/**
	 * What prerequisites does a student have to fulfil in order to take part in this course?
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.strKursVoraussetzung</Code>
	 **/	
	public void setKursVoraussetzung(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKursVoraussetzung)));
		this.m_sKursVoraussetzung=value;
	}
	

	/**
	 * Is it possible to get a credit?
	 * @return Is it possible to get a credit?
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.blnKursSchein</Code>
	 **/
	public boolean getKursSchein(){
		return this.m_bKursSchein;
	}

	/**
	 * Is it possible to get a credit?
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.blnKursSchein</Code>
	 **/	
	public void setKursSchein(boolean value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_bKursSchein));
		this.m_bKursSchein=value;
	}
	

	/**
	 * Categorization (Grundstudium, Hauptstudium etc.).
	 * @return Categorization (Grundstudium, Hauptstudium etc.).
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.strKursEinordnung</Code>
	 **/
	public String getKursEinordnung(){
		return this.m_sKursEinordnung;
	}

	/**
	 * Categorization (Grundstudium, Hauptstudium etc.).
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.strKursEinordnung</Code>
	 **/	
	public void setKursEinordnung(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKursEinordnung)));
		this.m_sKursEinordnung=value;
	}
	

	/**
	 * Number of hours per week.
	 * @return Number of hours per week.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.intKursStunden</Code>
	 **/
	public int getKursStunden(){
		return this.m_iKursStunden;
	}

	/**
	 * Number of hours per week.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.intKursStunden</Code>
	 **/	
	public void setKursStunden(int value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_iKursStunden));
		this.m_iKursStunden=value;
	}
	

	/**
	 * Date when details of this course were last changed. @Deprecated. Use audit trail instead.
	 * @return Date when details of this course were last changed. @Deprecated. Use audit trail instead.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.dtmKursLastChange</Code>
	 **/
	public Timestamp getKursLastChange(){
		return this.m_tKursLastChange;
	}

	/**
	 * Date when details of this course were last changed. @Deprecated. Use audit trail instead.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.dtmKursLastChange</Code>
	 **/	
	public void setKursLastChange(Timestamp value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_tKursLastChange));
		this.m_tKursLastChange=value;
	}
	

	/**
	 * Until when (inclusive) is it possible to post a commitment ("Scheinanmeldung") for this course?
	 * @return Until when (inclusive) is it possible to post a commitment ("Scheinanmeldung") for this course?
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.dtmKursScheinanmeldungBis</Code>
	 **/
	public Date getKursScheinanmeldungBis(){
		return this.m_dKursScheinanmeldungBis;
	}

	/**
	 * Until when (inclusive) is it possible to post a commitment ("Scheinanmeldung") for this course?
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.dtmKursScheinanmeldungBis</Code>
	 **/	
	public void setKursScheinanmeldungBis(Date value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_dKursScheinanmeldungBis));
		this.m_dKursScheinanmeldungBis=value;
	}
	

	/**
	 * From what date on  (inclusive) is it possible to post a commitment ("Scheinanmeldung") for this course?
	 * @return From what date on  (inclusive) is it possible to post a commitment ("Scheinanmeldung") for this course?
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.dtmKursScheinanmeldungVon</Code>
	 **/
	public Date getKursScheinanmeldungVon(){
		return this.m_dKursScheinanmeldungVon;
	}

	/**
	 * From what date on  (inclusive) is it possible to post a commitment ("Scheinanmeldung") for this course?
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.dtmKursScheinanmeldungVon</Code>
	 **/	
	public void setKursScheinanmeldungVon(Date value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_dKursScheinanmeldungVon));
		this.m_dKursScheinanmeldungVon=value;
	}
	

	/**
	 * Is it possible to post a commitment ("Scheinanmeldung") for this course?
	 * @return Is it possible to post a commitment ("Scheinanmeldung") for this course?
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.blnKursScheinanmeldungErlaubt</Code>
	 **/
	public boolean getKursScheinanmeldungErlaubt(){
		return this.m_bKursScheinanmeldungErlaubt;
	}

	/**
	 * Is it possible to post a commitment ("Scheinanmeldung") for this course?
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.blnKursScheinanmeldungErlaubt</Code>
	 **/	
	public void setKursScheinanmeldungErlaubt(boolean value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_bKursScheinanmeldungErlaubt));
		this.m_bKursScheinanmeldungErlaubt=value;
	}
	

	/**
	 * What sequence in course catalog?
	 * @return What sequence in course catalog?
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.intKursSequence</Code>
	 **/
	public int getKursSequence(){
		return this.m_iKursSequence;
	}

	/**
	 * What sequence in course catalog?
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.intKursSequence</Code>
	 **/	
	public void setKursSequence(int value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_iKursSequence));
		this.m_iKursSequence=value;
	}
	

	/**
	 * Is this a course of the current semester or of the next, coming semester?
	 * @return Is this a course of the current semester or of the next, coming semester?
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.blnKursPlanungssemester</Code>
	 **/
	public boolean getKursPlanungssemester(){
		return this.m_bKursPlanungssemester;
	}

	/**
	 * Is this a course of the current semester or of the next, coming semester?
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.blnKursPlanungssemester</Code>
	 **/	
	public void setKursPlanungssemester(boolean value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_bKursPlanungssemester));
		this.m_bKursPlanungssemester=value;
	}
	

	/**
	 * If this course does not take place on a weekly basis, what are its appointments?
	 * @return If this course does not take place on a weekly basis, what are its appointments?
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.strKursTerminFreitext</Code>
	 **/
	public String getKursTerminFreitext(){
		return this.m_sKursTerminFreitext;
	}

	/**
	 * If this course does not take place on a weekly basis, what are its appointments?
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.strKursTerminFreitext</Code>
	 **/	
	public void setKursTerminFreitext(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKursTerminFreitext)));
		this.m_sKursTerminFreitext=value;
	}
	

	/**
	 * Statistical utility: how many "real" participants are in this course?
	 * @return Statistical utility: how many "real" participants are in this course?
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.intKursTeilnehmer</Code>
	 **/
	public int getKursTeilnehmer(){
		return this.m_iKursTeilnehmer;
	}

	/**
	 * Statistical utility: how many "real" participants are in this course?
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.intKursTeilnehmer</Code>
	 **/	
	public void setKursTeilnehmer(int value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_iKursTeilnehmer));
		this.m_iKursTeilnehmer=value;
	}
	

	/**
	 * If the course takes place in a building that does not belong to the seminar, or if this course does not take place on a weekly basis, where does it take place? (Day 1 reference).
	 * @return If the course takes place in a building that does not belong to the seminar, or if this course does not take place on a weekly basis, where does it take place? (Day 1 reference).
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.strKursRaumExtern1</Code>
	 **/
	public String getKursRaumExtern1(){
		return this.m_sKursRaumExtern1;
	}

	/**
	 * If the course takes place in a building that does not belong to the seminar, or if this course does not take place on a weekly basis, where does it take place? (Day 1 reference).
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.strKursRaumExtern1</Code>
	 **/	
	public void setKursRaumExtern1(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKursRaumExtern1)));
		this.m_sKursRaumExtern1=value;
	}
	

	/**
	 * If the course takes place in a building that does not belong to the seminar, or if this course does not take place on a weekly basis, where does it take place? (Day 2 reference).
	 * @return If the course takes place in a building that does not belong to the seminar, or if this course does not take place on a weekly basis, where does it take place? (Day 2 reference).
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.strKursRaumExtern2</Code>
	 **/
	public String getKursRaumExtern2(){
		return this.m_sKursRaumExtern2;
	}

	/**
	 * If the course takes place in a building that does not belong to the seminar, or if this course does not take place on a weekly basis, where does it take place? (Day 2 reference).
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.strKursRaumExtern2</Code>
	 **/	
	public void setKursRaumExtern2(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKursRaumExtern2)));
		this.m_sKursRaumExtern2=value;
	}
	

	/**
	 * Custom field
	 * @return Custom field
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.strKursCustom1</Code>
	 **/
	public String getKursCustom1(){
		return this.m_sKursCustom1;
	}

	/**
	 * Custom field
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.strKursCustom1</Code>
	 **/	
	public void setKursCustom1(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKursCustom1)));
		this.m_sKursCustom1=value;
	}
	

	/**
	 * Custom field
	 * @return Custom field
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.strKursCustom2</Code>
	 **/
	public String getKursCustom2(){
		return this.m_sKursCustom2;
	}

	/**
	 * Custom field
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.strKursCustom2</Code>
	 **/	
	public void setKursCustom2(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKursCustom2)));
		this.m_sKursCustom2=value;
	}
	

	/**
	 * Custom field
	 * @return Custom field
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.strKursCustom3</Code>
	 **/
	public String getKursCustom3(){
		return this.m_sKursCustom3;
	}

	/**
	 * Custom field
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKurs.strKursCustom3</Code>
	 **/	
	public void setKursCustom3(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKursCustom3)));
		this.m_sKursCustom3=value;
	}
	
 	/**
 	 * @return Credit Pts of this course (this is a DEFAULT, the specific 
 	 * credit-point value is always in a KursXKurstyp object.
 	 */
 	public float getKursCreditPts(){
 		return this.m_sKursCreditPts;
 	}
 	/**
 	 * @param value the CreditPts to set this course to (as a default value).
 	 */
 	public void setKursCreditPts(float value){
 		this.m_bIsDirty = ((this.m_bIsDirty) || (this.m_sKursCreditPts!=value) );
 		this.m_sKursCreditPts = value;
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
			"<KursID>" + m_lKursID + "</KursID>"  + 
			"<KursUnivISID>" + m_sKursUnivISID + "</KursUnivISID>"  + 
			"<DozentID>" + m_lDozentID + "</DozentID>"  + 
			"<KursTag>" + m_sKursTag + "</KursTag>"  + 
			"<KursBeginn>" + m_tKursBeginn + "</KursBeginn>"  + 
			"<KursEnde>" + m_tKursEnde + "</KursEnde>"  + 
			"<KursRaum>" + m_sKursRaum + "</KursRaum>"  + 
			"<KursTag2>" + m_sKursTag2 + "</KursTag2>"  + 
			"<KursBeginn2>" + m_tKursBeginn2 + "</KursBeginn2>"  + 
			"<KursEnde2>" + m_tKursEnde2 + "</KursEnde2>"  + 
			"<KursRaum2>" + m_sKursRaum2 + "</KursRaum2>"  + 
			"<KursTeilnehmerMaximal>" + m_lKursTeilnehmerMaximal + "</KursTeilnehmerMaximal>"  + 
			"<KursRequests>" + m_lKursRequests + "</KursRequests>"  + 
			"<KursTitel>" + m_sKursTitel + "</KursTitel>"  + 
			"<KursTitel_en>" + m_sKursTitel_en + "</KursTitel_en>"  + 
			"<KursBeschreibung>" + m_sKursBeschreibung + "</KursBeschreibung>"  + 
			"<KursBeschreibung_en>" + m_sKursBeschreibung_en + "</KursBeschreibung_en>"  + 
			"<KursLiteratur>" + m_sKursLiteratur + "</KursLiteratur>"  + 
			"<KursZusatz>" + m_sKursZusatz + "</KursZusatz>"  + 
			"<KursAnmeldung>" + m_sKursAnmeldung + "</KursAnmeldung>"  + 
			"<KursVoraussetzung>" + m_sKursVoraussetzung + "</KursVoraussetzung>"  + 
			"<KursSchein>" + m_bKursSchein + "</KursSchein>"  + 
			"<KursEinordnung>" + m_sKursEinordnung + "</KursEinordnung>"  + 
			"<KursStunden>" + m_iKursStunden + "</KursStunden>"  + 
			"<KursLastChange>" + m_tKursLastChange + "</KursLastChange>"  + 
			"<KursScheinanmeldungBis>" + m_dKursScheinanmeldungBis + "</KursScheinanmeldungBis>"  + 
			"<KursScheinanmeldungVon>" + m_dKursScheinanmeldungVon + "</KursScheinanmeldungVon>"  + 
			"<KursScheinanmeldungErlaubt>" + m_bKursScheinanmeldungErlaubt + "</KursScheinanmeldungErlaubt>"  + 
			"<KursSequence>" + m_iKursSequence + "</KursSequence>"  + 
			"<KursPlanungssemester>" + m_bKursPlanungssemester + "</KursPlanungssemester>"  + 
			"<KursTerminFreitext>" + m_sKursTerminFreitext + "</KursTerminFreitext>"  + 
			"<KursTeilnehmer>" + m_iKursTeilnehmer + "</KursTeilnehmer>"  + 
			"<KursRaumExtern1>" + m_sKursRaumExtern1 + "</KursRaumExtern1>"  + 
			"<KursRaumExtern2>" + m_sKursRaumExtern2 + "</KursRaumExtern2>"  + 
			"<KursCustom1>" + m_sKursCustom1 + "</KursCustom1>"  + 
			"<KursCustom2>" + m_sKursCustom2 + "</KursCustom2>"  + 
			"<KursCustom3>" + m_sKursCustom3 + "</KursCustom3>" +
			"<KursCreditPts>" + this.m_sKursCreditPts + "</KursCreditPts>";
	}

////////////////////////////////////////////////////////////////
// 4.   S Q L  U T I L I T I E S
////////////////////////////////////////////////////////////////


	/**
	 * Where-SQL für eindeutigen Index dieses Objekte in der Datenbank (in Tabelle 'tblBdKurs.').
	 * @return SQL-String wie z.B. "Id1=? and Id2=?"
	 **/
	private String getSQLWhereClause(){
		return 
			"\"lngSdSeminarID\"=? AND " + 
			"\"lngKursID\"=?";
	}

	/**
	 * SQL-Befehl zum Löschen des Objekts in der Datenbank (aus Tabelle 'tblBdKurs.')
	 * @return SQL-String zum Löschen des Objekts, z.B. "delete from T1 where (Id1=?);"
	 **/
	private String toDBDeleteString(){
		return "delete from \"tblBdKurs\" where ( " + this.getSQLWhereClause() + ");";
	}

	/**
 	 * Setzt die Werte für alle Platzhalter in {@link #getSQLWhereClause()}.
	 * @param ii Offset für die Parameter (inklusive).
	 * @throws SQLException 
	 **/
	private void pokeWhere(int ii, PreparedStatement ps) throws SQLException{
		
		ps.setLong(ii++, m_lSdSeminarID);
		ps.setLong(ii++, m_lKursID);
	}

	/**
  	 * Füllt das SQL-Statement mit den aktuellen Werten des Objekts
	 * @throws SQLException
	 **/
	private void pokeStatement(PreparedStatement ps) throws SQLException{
		
		ps.setLong(1, m_lSdSeminarID);
		ps.setLong(2, m_lKursID);
		ps.setString(3, m_sKursUnivISID);
		ps.setLong(4, m_lDozentID);
		ps.setString(5, m_sKursTag);
		ps.setTime(6, m_tKursBeginn);
		ps.setTime(7, m_tKursEnde);
		ps.setString(8, m_sKursRaum);
		ps.setString(9, m_sKursTag2);
		ps.setTime(10, m_tKursBeginn2);
		ps.setTime(11, m_tKursEnde2);
		ps.setString(12, m_sKursRaum2);
		ps.setLong(13, m_lKursTeilnehmerMaximal);
		ps.setLong(14, m_lKursRequests);
		ps.setString(15, m_sKursTitel);
		ps.setString(16, m_sKursTitel_en);
		ps.setString(17, m_sKursBeschreibung);
		ps.setString(18, m_sKursBeschreibung_en);
		ps.setString(19, m_sKursLiteratur);
		ps.setString(20, m_sKursZusatz);
		ps.setString(21, m_sKursAnmeldung);
		ps.setString(22, m_sKursVoraussetzung);
		ps.setBoolean(23, m_bKursSchein);
		ps.setString(24, m_sKursEinordnung);
		ps.setInt(25, m_iKursStunden);
		ps.setTimestamp(26, m_tKursLastChange);
//		try {
//			if(m_tKursLastChange)
//			ps.setTimestamp(26, new Timestamp(g_TIMESTAMT_FORMAT.parse(m_tKursLastChange).getTime()));
//		} catch (ParseException e) {
//			ps.setTimestamp(26,null);
//			System.err.println("ERROR (minor problem): data.Kurs, cannot set last change timestamp -- " + m_tKursLastChange + ", Err: " + e.getMessage() + " -> setting to null instead.");
//		}
		ps.setDate(27, m_dKursScheinanmeldungBis);
		ps.setDate(28, m_dKursScheinanmeldungVon);
		ps.setBoolean(29, m_bKursScheinanmeldungErlaubt);
		ps.setInt(30, m_iKursSequence);
		ps.setBoolean(31, m_bKursPlanungssemester);
		ps.setString(32, m_sKursTerminFreitext);
		ps.setInt(33, m_iKursTeilnehmer);
		ps.setString(34, m_sKursRaumExtern1);
		ps.setString(35, m_sKursRaumExtern2);
		ps.setString(36, m_sKursCustom1);
		ps.setString(37, m_sKursCustom2);
		ps.setString(38, m_sKursCustom3);

	}

	/**
	 * SQL-Befehl zum Speichern der Objekteigenschaften in die Datenbank (Tabelle 'tblBdKurs.')
	 * @return PreparedStatemtent, das nach Aufruf von {@link #pokeStatement(PreparedStatement)} und {@link #execute()} das Update ausführt.
	 **/
	private String toDBUpdateString(){
		return "update \"tblBdKurs\" set " +
			"\"lngSdSeminarID\"=?, " +
			"\"lngKursID\"=?, " +
			"\"strKursUnivISID\"=?, " +
			"\"lngDozentID\"=?, " +
			"\"strKursTag\"=?, " +
			"\"dtmKursBeginn\"=?, " +
			"\"dtmKursEnde\"=?, " +
			"\"strKursRaum\"=?, " +
			"\"strKursTag2\"=?, " +
			"\"dtmKursBeginn2\"=?, " +
			"\"dtmKursEnde2\"=?, " +
			"\"strKursRaum2\"=?, " +
			"\"intKursTeilnehmerMaximal\"=?, " +
			"\"lngKursRequests\"=?, " +
			"\"strKursTitel\"=?, " +
			"\"strKursTitel_en\"=?, " +
			"\"strKursBeschreibung\"=?, " +
			"\"strKursBeschreibung_en\"=?, " +
			"\"strKursLiteratur\"=?, " +
			"\"strKursZusatz\"=?, " +
			"\"strKursAnmeldung\"=?, " +
			"\"strKursVoraussetzung\"=?, " +
			"\"blnKursSchein\"=?, " +
			"\"strKursEinordnung\"=?, " +
			"\"intKursStunden\"=?, " +
			"\"dtmKursLastChange\"=?, " +
			"\"dtmKursScheinanmeldungBis\"=?, " +
			"\"dtmKursScheinanmeldungVon\"=?, " +
			"\"blnKursScheinanmeldungErlaubt\"=?, " +
			"\"intKursSequence\"=?, " +
			"\"blnKursPlanungssemester\"=?, " +
			"\"strKursTerminFreitext\"=?, " +
			"\"intKursTeilnehmer\"=?, " +
			"\"strKursRaumExtern1\"=?, " +
			"\"strKursRaumExtern2\"=?, " +
			"\"strKursCustom1\"=?, " +
			"\"strKursCustom2\"=?, " +
			"\"strKursCustom3\"=?" +
			" where (" + this.getSQLWhereClause() + ");";
	}

	/**
	 * SQL-Befehl zum Hinzufügen des Objekts in die Datenbank (Tabelle 'tblBdKurs.')
	 * @return SQL-String "insert into...".
	 **/
	private String toDBAddString(){
		return "insert into \"tblBdKurs\" ( " +
			"\"lngSdSeminarID\", \"strKursUnivISID\", \"lngDozentID\", \"strKursTag\", \"dtmKursBeginn\", \"dtmKursEnde\", \"strKursRaum\", \"strKursTag2\", \"dtmKursBeginn2\", \"dtmKursEnde2\", \"strKursRaum2\", \"intKursTeilnehmerMaximal\", \"lngKursRequests\", \"strKursTitel\", \"strKursTitel_en\", \"strKursBeschreibung\", \"strKursBeschreibung_en\", \"strKursLiteratur\", \"strKursZusatz\", \"strKursAnmeldung\", \"strKursVoraussetzung\", \"blnKursSchein\", \"strKursEinordnung\", \"intKursStunden\", \"dtmKursLastChange\", \"dtmKursScheinanmeldungBis\", \"dtmKursScheinanmeldungVon\", \"blnKursScheinanmeldungErlaubt\", \"intKursSequence\", \"blnKursPlanungssemester\", \"strKursTerminFreitext\", \"intKursTeilnehmer\", \"strKursRaumExtern1\", \"strKursRaumExtern2\", \"strKursCustom1\", \"strKursCustom2\", \"strKursCustom3\" ) VALUES ( ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
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
	public void init(long lngSdSeminarID, long lngKursID) throws SQLException, NamingException{

		this.m_lSdSeminarID=lngSdSeminarID;

		this.m_lKursID=lngKursID;

		
		PreparedStatement pstm = prepareStatement("select * from \"tblBdKurs\" where (" + this.getSQLWhereClause() + ");");
		pokeWhere(1,pstm);

		ResultSet rst=pstm.executeQuery();
		if(rst.next()) this.initByRst(rst);
		rst.close();
		this.disconnect();
		rst=null;
	}	
	
	/**
	 * Lade die Objekteigenschaften aus einem ResultSet.
	 * param rst ResultSet mit allen Spalten der Tabelle 'tblBdKurs'
	 * @throws SQLException 
	 **/
	public void initByRst(ResultSet rst) throws SQLException{
		this.m_lSdSeminarID=rst.getLong("lngSdSeminarID");
		this.m_lKursID=rst.getLong("lngKursID");
		this.m_sKursUnivISID=rst.getString("strKursUnivISID");
		this.m_lDozentID=rst.getLong("lngDozentID");
		this.m_sKursTag=rst.getString("strKursTag");
		this.m_tKursBeginn=rst.getTime("dtmKursBeginn");
		this.m_tKursEnde=rst.getTime("dtmKursEnde");
		this.m_sKursRaum=rst.getString("strKursRaum");
		this.m_sKursTag2=rst.getString("strKursTag2");
		this.m_tKursBeginn2=rst.getTime("dtmKursBeginn2");
		this.m_tKursEnde2=rst.getTime("dtmKursEnde2");
		this.m_sKursRaum2=rst.getString("strKursRaum2");
		this.m_lKursTeilnehmerMaximal=rst.getLong("intKursTeilnehmerMaximal");
		this.m_lKursRequests=rst.getLong("lngKursRequests");
		this.m_sKursTitel=rst.getString("strKursTitel");
		this.m_sKursTitel_en=rst.getString("strKursTitel_en");
		this.m_sKursBeschreibung=rst.getString("strKursBeschreibung");
		this.m_sKursBeschreibung_en=rst.getString("strKursBeschreibung_en");
		this.m_sKursLiteratur=rst.getString("strKursLiteratur");
		this.m_sKursZusatz=rst.getString("strKursZusatz");
		this.m_sKursAnmeldung=rst.getString("strKursAnmeldung");
		this.m_sKursVoraussetzung=rst.getString("strKursVoraussetzung");
		this.m_bKursSchein=rst.getBoolean("blnKursSchein");
		this.m_sKursEinordnung=rst.getString("strKursEinordnung");
		this.m_iKursStunden=rst.getInt("intKursStunden");
		this.m_tKursLastChange=rst.getTimestamp("dtmKursLastChange");
		this.m_dKursScheinanmeldungBis=rst.getDate("dtmKursScheinanmeldungBis");
		this.m_dKursScheinanmeldungVon=rst.getDate("dtmKursScheinanmeldungVon");
		this.m_bKursScheinanmeldungErlaubt=rst.getBoolean("blnKursScheinanmeldungErlaubt");
		this.m_iKursSequence=rst.getInt("intKursSequence");
		this.m_bKursPlanungssemester=rst.getBoolean("blnKursPlanungssemester");
		this.m_sKursTerminFreitext=rst.getString("strKursTerminFreitext");
		this.m_iKursTeilnehmer=rst.getInt("intKursTeilnehmer");
		this.m_sKursRaumExtern1=rst.getString("strKursRaumExtern1");
		this.m_sKursRaumExtern2=rst.getString("strKursRaumExtern2");
		this.m_sKursCustom1=rst.getString("strKursCustom1");
		this.m_sKursCustom2=rst.getString("strKursCustom2");
		this.m_sKursCustom3=rst.getString("strKursCustom3");	
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
		pokeWhere(39,pstm);
		bReturn	= pstm.execute();
		try {
			pstm.execute();
			bReturn=true;
		} catch (SQLException e) {System.err.println(e.getCause());}
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
	public Kurs(){}	
	
	/**
	 * Konstruktor mit Indexwerten (eindeutig) der assoziierten Datenbanktabelle
	 * @throws NamingException 
	 * @throws SQLException 
	 **/
	public Kurs(long lngSdSeminarID, long lngKursID) throws SQLException, NamingException{
		this.init(lngSdSeminarID, lngKursID);
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
	public Kurs(ResultSet rst) throws SQLException{
		this.initByRst(rst);
		this.m_bIsDirty = false;
	}

  }//Klassenende
