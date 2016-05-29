
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
import java.text.ParseException;
import org.w3c.dom.Node; 

/**
 *  Modules: Kvv, (Exam, SignUp)
This table holds _all_ courses of _all_ seminars and institutes of former semesters. The information is copied from Kurs and KursXKurstyp.
 *  
 *  ALLGEMEINE INFORMATION ZU DEN OBJEKTEN DIESES PACKAGES:
 *  Diese Klasse kapselt die Tabelle 'KvvArchiv' in 
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
public class KvvArchiv extends shjCore{

	private static final long serialVersionUID = 7L;

////////////////////////////////////////////////////////////////
// 1.   P R I V A T E   D E K L A R A T I O N E N
////////////////////////////////////////////////////////////////
	
	private boolean m_bIsDirty = false;

	private long m_lSdFakultaetID;
	private String m_sSdFakultaetName;
	private long m_lSdSeminarID;
	private String m_sSdSeminarName;
	private long m_lID;
	private long m_lKvvArchivLeistungID;
	private String m_sKvvArchivLeistungBezeichnung;
	private long m_lKvvArchivKurstypID;
	private long m_lKvvArchivKursID;
	private String m_sKvvArchivSemesterName;
	private String m_sKvvArchivDozentname;
	private String m_sKvvArchivDozentvorname;
	private String m_sKvvArchivDozenttitel;
	private String m_sKvvArchivKurstypBezeichnung;
	private String m_sKvvArchivKursEinordnung;
	private String m_sKvvArchivKursKategorie;
	private String m_sKvvArchivKurstitel;
	private String m_sKvvArchivKurstitel_en;
	private String m_sKvvArchivKursTag;
	private String m_tKvvArchivKursBeginn;
	private String m_tKvvArchivKursEnde;
	private String m_sKvvArchivKursRaum;
	private String m_sKvvArchivKursTag2;
	private String m_tKvvArchivKursBeginn2;
	private String m_tKvvArchivKursEnde2;
	private String m_sKvvArchivKursKursRaum2;
	private String m_sKvvArchivKursBeschreibung;
	private String m_sKvvArchivKursBeschreibung_en;
	private String m_sKvvArchivKursVoraussetzung;
	private boolean m_bKvvArchivKursSchein;
	private String m_sKvvArchivKursLiteratur;
	private String m_sKvvArchivKursZusatz;
	private String m_sKvvArchivKursTerminFreitext;
	private int m_iKvvArchivKursTeilnehmer;
	private float m_sKvvArchivKursCreditPts;
	private String m_sKvvArchivCustom1;
	private String m_sKvvArchivCustom2;
	private String m_sKvvArchivCustom3;

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
	 * Id of faculty.
	 * @return Id of faculty.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.lngSdFakultaetID</Code>
	 **/
	public long getSdFakultaetID(){
		return this.m_lSdFakultaetID;
	}

	/**
	 * Id of faculty.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.lngSdFakultaetID</Code>
	 **/	
	public void setSdFakultaetID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lSdFakultaetID));
		this.m_lSdFakultaetID=value;
	}
	

	/**
	 * Name of faculty.
	 * @return Name of faculty.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.strSdFakultaetName</Code>
	 **/
	public String getSdFakultaetName(){
		return this.m_sSdFakultaetName;
	}

	/**
	 * Name of faculty.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.strSdFakultaetName</Code>
	 **/	
	public void setSdFakultaetName(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sSdFakultaetName)));
		this.m_sSdFakultaetName=value;
	}
	

	/**
	 * Seminar
	 * @return Seminar
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.lngSdSeminarID</Code>
	 **/
	public long getSdSeminarID(){
		return this.m_lSdSeminarID;
	}

	/**
	 * Seminar
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.lngSdSeminarID</Code>
	 **/	
	public void setSdSeminarID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lSdSeminarID));
		this.m_lSdSeminarID=value;
	}
	

	/**
	 * Name of seminar.
	 * @return Name of seminar.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.strSdSeminarName</Code>
	 **/
	public String getSdSeminarName(){
		return this.m_sSdSeminarName;
	}

	/**
	 * Name of seminar.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.strSdSeminarName</Code>
	 **/	
	public void setSdSeminarName(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sSdSeminarName)));
		this.m_sSdSeminarName=value;
	}
	

	/**
	 * Id of this object.
	 * @return Id of this object.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.lngID</Code>
	 **/
	public long getID(){
		return this.m_lID;
	}

	/**
	 * Id of this object.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.lngID</Code>
	 **/	
	public void setID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lID));
		this.m_lID=value;
	}
	

	/**
	 * What credit was this course mapped to?
	 * @return What credit was this course mapped to?
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.lngKvvArchivLeistungID</Code>
	 **/
	public long getKvvArchivLeistungID(){
		return this.m_lKvvArchivLeistungID;
	}

	/**
	 * What credit was this course mapped to?
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.lngKvvArchivLeistungID</Code>
	 **/	
	public void setKvvArchivLeistungID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lKvvArchivLeistungID));
		this.m_lKvvArchivLeistungID=value;
	}
	

	/**
	 * What was the name of the credit that this course was mapped to?
	 * @return What was the name of the credit that this course was mapped to?
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.strKvvArchivLeistungBezeichnung</Code>
	 **/
	public String getKvvArchivLeistungBezeichnung(){
		return this.m_sKvvArchivLeistungBezeichnung;
	}

	/**
	 * What was the name of the credit that this course was mapped to?
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.strKvvArchivLeistungBezeichnung</Code>
	 **/	
	public void setKvvArchivLeistungBezeichnung(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKvvArchivLeistungBezeichnung)));
		this.m_sKvvArchivLeistungBezeichnung=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.lngKvvArchivKurstypID</Code>
	 **/
	public long getKvvArchivKurstypID(){
		return this.m_lKvvArchivKurstypID;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.lngKvvArchivKurstypID</Code>
	 **/	
	public void setKvvArchivKurstypID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lKvvArchivKurstypID));
		this.m_lKvvArchivKurstypID=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.lngKvvArchivKursID</Code>
	 **/
	public long getKvvArchivKursID(){
		return this.m_lKvvArchivKursID;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.lngKvvArchivKursID</Code>
	 **/	
	public void setKvvArchivKursID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lKvvArchivKursID));
		this.m_lKvvArchivKursID=value;
	}
	

	/**
	 * What semester did this course take place in?
	 * @return What semester did this course take place in?
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.strKvvArchivSemesterName</Code>
	 **/
	public String getKvvArchivSemesterName(){
		return this.m_sKvvArchivSemesterName;
	}

	/**
	 * What semester did this course take place in?
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.strKvvArchivSemesterName</Code>
	 **/	
	public void setKvvArchivSemesterName(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKvvArchivSemesterName)));
		this.m_sKvvArchivSemesterName=value;
	}
	

	/**
	 * Name of teacher.
	 * @return Name of teacher.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.strKvvArchivDozentname</Code>
	 **/
	public String getKvvArchivDozentname(){
		return this.m_sKvvArchivDozentname;
	}

	/**
	 * Name of teacher.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.strKvvArchivDozentname</Code>
	 **/	
	public void setKvvArchivDozentname(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKvvArchivDozentname)));
		this.m_sKvvArchivDozentname=value;
	}
	

	/**
	 * First name of teacher.
	 * @return First name of teacher.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.strKvvArchivDozentvorname</Code>
	 **/
	public String getKvvArchivDozentvorname(){
		return this.m_sKvvArchivDozentvorname;
	}

	/**
	 * First name of teacher.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.strKvvArchivDozentvorname</Code>
	 **/	
	public void setKvvArchivDozentvorname(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKvvArchivDozentvorname)));
		this.m_sKvvArchivDozentvorname=value;
	}
	

	/**
	 * Academic title of teacher.
	 * @return Academic title of teacher.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.strKvvArchivDozenttitel</Code>
	 **/
	public String getKvvArchivDozenttitel(){
		return this.m_sKvvArchivDozenttitel;
	}

	/**
	 * Academic title of teacher.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.strKvvArchivDozenttitel</Code>
	 **/	
	public void setKvvArchivDozenttitel(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKvvArchivDozenttitel)));
		this.m_sKvvArchivDozenttitel=value;
	}
	

	/**
	 * Name of course-type.
	 * @return Name of course-type.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.strKvvArchivKurstypBezeichnung</Code>
	 **/
	public String getKvvArchivKurstypBezeichnung(){
		return this.m_sKvvArchivKurstypBezeichnung;
	}

	/**
	 * Name of course-type.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.strKvvArchivKurstypBezeichnung</Code>
	 **/	
	public void setKvvArchivKurstypBezeichnung(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKvvArchivKurstypBezeichnung)));
		this.m_sKvvArchivKurstypBezeichnung=value;
	}
	

	/**
	 * Classification of this course.
	 * @return Classification of this course.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.strKvvArchivKursEinordnung</Code>
	 **/
	public String getKvvArchivKursEinordnung(){
		return this.m_sKvvArchivKursEinordnung;
	}

	/**
	 * Classification of this course.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.strKvvArchivKursEinordnung</Code>
	 **/	
	public void setKvvArchivKursEinordnung(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKvvArchivKursEinordnung)));
		this.m_sKvvArchivKursEinordnung=value;
	}
	

	/**
	 * Category of this course.
	 * @return Category of this course.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.strKvvArchivKursKategorie</Code>
	 **/
	public String getKvvArchivKursKategorie(){
		return this.m_sKvvArchivKursKategorie;
	}

	/**
	 * Category of this course.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.strKvvArchivKursKategorie</Code>
	 **/	
	public void setKvvArchivKursKategorie(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKvvArchivKursKategorie)));
		this.m_sKvvArchivKursKategorie=value;
	}
	

	/**
	 * Title of this course.
	 * @return Title of this course.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.strKvvArchivKurstitel</Code>
	 **/
	public String getKvvArchivKurstitel(){
		return this.m_sKvvArchivKurstitel;
	}

	/**
	 * Title of this course.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.strKvvArchivKurstitel</Code>
	 **/	
	public void setKvvArchivKurstitel(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKvvArchivKurstitel)));
		this.m_sKvvArchivKurstitel=value;
	}
	

	/**
	 * English version of title of this course.
	 * @return English version of title of this course.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.strKvvArchivKurstitel_en</Code>
	 **/
	public String getKvvArchivKurstitel_en(){
		return this.m_sKvvArchivKurstitel_en;
	}

	/**
	 * English version of title of this course.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.strKvvArchivKurstitel_en</Code>
	 **/	
	public void setKvvArchivKurstitel_en(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKvvArchivKurstitel_en)));
		this.m_sKvvArchivKurstitel_en=value;
	}
	

	/**
	 * Day when this course took place.
	 * @return Day when this course took place.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.strKvvArchivKursTag</Code>
	 **/
	public String getKvvArchivKursTag(){
		return this.m_sKvvArchivKursTag;
	}

	/**
	 * Day when this course took place.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.strKvvArchivKursTag</Code>
	 **/	
	public void setKvvArchivKursTag(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKvvArchivKursTag)));
		this.m_sKvvArchivKursTag=value;
	}
	

	/**
	 * Start-time.
	 * @return Start-time.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.dtmKvvArchivKursBeginn</Code>
	 **/
	public String getKvvArchivKursBeginn(){
		return this.m_tKvvArchivKursBeginn;
	}

	/**
	 * Start-time.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.dtmKvvArchivKursBeginn</Code>
	 **/	
	public void setKvvArchivKursBeginn(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_tKvvArchivKursBeginn)));
		this.m_tKvvArchivKursBeginn=value;
	}
	

	/**
	 * Stop-time.
	 * @return Stop-time.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.dtmKvvArchivKursEnde</Code>
	 **/
	public String getKvvArchivKursEnde(){
		return this.m_tKvvArchivKursEnde;
	}

	/**
	 * Stop-time.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.dtmKvvArchivKursEnde</Code>
	 **/	
	public void setKvvArchivKursEnde(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_tKvvArchivKursEnde)));
		this.m_tKvvArchivKursEnde=value;
	}
	

	/**
	 * Room
	 * @return Room
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.strKvvArchivKursRaum</Code>
	 **/
	public String getKvvArchivKursRaum(){
		return this.m_sKvvArchivKursRaum;
	}

	/**
	 * Room
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.strKvvArchivKursRaum</Code>
	 **/	
	public void setKvvArchivKursRaum(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKvvArchivKursRaum)));
		this.m_sKvvArchivKursRaum=value;
	}
	

	/**
	 * Day 2.
	 * @return Day 2.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.strKvvArchivKursTag2</Code>
	 **/
	public String getKvvArchivKursTag2(){
		return this.m_sKvvArchivKursTag2;
	}

	/**
	 * Day 2.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.strKvvArchivKursTag2</Code>
	 **/	
	public void setKvvArchivKursTag2(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKvvArchivKursTag2)));
		this.m_sKvvArchivKursTag2=value;
	}
	

	/**
	 * Start-time of second weekly date.
	 * @return Start-time of second weekly date.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.dtmKvvArchivKursBeginn2</Code>
	 **/
	public String getKvvArchivKursBeginn2(){
		return this.m_tKvvArchivKursBeginn2;
	}

	/**
	 * Start-time of second weekly date.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.dtmKvvArchivKursBeginn2</Code>
	 **/	
	public void setKvvArchivKursBeginn2(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_tKvvArchivKursBeginn2)));
		this.m_tKvvArchivKursBeginn2=value;
	}
	

	/**
	 * Stop-time of second weekly date.
	 * @return Stop-time of second weekly date.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.dtmKvvArchivKursEnde2</Code>
	 **/
	public String getKvvArchivKursEnde2(){
		return this.m_tKvvArchivKursEnde2;
	}

	/**
	 * Stop-time of second weekly date.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.dtmKvvArchivKursEnde2</Code>
	 **/	
	public void setKvvArchivKursEnde2(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_tKvvArchivKursEnde2)));
		this.m_tKvvArchivKursEnde2=value;
	}
	

	/**
	 * Room of second weekly date.
	 * @return Room of second weekly date.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.strKvvArchivKursKursRaum2</Code>
	 **/
	public String getKvvArchivKursKursRaum2(){
		return this.m_sKvvArchivKursKursRaum2;
	}

	/**
	 * Room of second weekly date.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.strKvvArchivKursKursRaum2</Code>
	 **/	
	public void setKvvArchivKursKursRaum2(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKvvArchivKursKursRaum2)));
		this.m_sKvvArchivKursKursRaum2=value;
	}
	

	/**
	 * Description.
	 * @return Description.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.strKvvArchivKursBeschreibung</Code>
	 **/
	public String getKvvArchivKursBeschreibung(){
		return this.m_sKvvArchivKursBeschreibung;
	}

	/**
	 * Description.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.strKvvArchivKursBeschreibung</Code>
	 **/	
	public void setKvvArchivKursBeschreibung(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKvvArchivKursBeschreibung)));
		this.m_sKvvArchivKursBeschreibung=value;
	}
	

	/**
	 * English version of Description.
	 * @return English version of Description.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.strKvvArchivKursBeschreibung_en</Code>
	 **/
	public String getKvvArchivKursBeschreibung_en(){
		return this.m_sKvvArchivKursBeschreibung_en;
	}

	/**
	 * English version of Description.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.strKvvArchivKursBeschreibung_en</Code>
	 **/	
	public void setKvvArchivKursBeschreibung_en(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKvvArchivKursBeschreibung_en)));
		this.m_sKvvArchivKursBeschreibung_en=value;
	}
	

	/**
	 * Prerequisites for this course.
	 * @return Prerequisites for this course.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.strKvvArchivKursVoraussetzung</Code>
	 **/
	public String getKvvArchivKursVoraussetzung(){
		return this.m_sKvvArchivKursVoraussetzung;
	}

	/**
	 * Prerequisites for this course.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.strKvvArchivKursVoraussetzung</Code>
	 **/	
	public void setKvvArchivKursVoraussetzung(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKvvArchivKursVoraussetzung)));
		this.m_sKvvArchivKursVoraussetzung=value;
	}
	

	/**
	 * Was this a course where you could acquire a credit?
	 * @return Was this a course where you could acquire a credit?
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.blnKvvArchivKursSchein</Code>
	 **/
	public boolean getKvvArchivKursSchein(){
		return this.m_bKvvArchivKursSchein;
	}

	/**
	 * Was this a course where you could acquire a credit?
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.blnKvvArchivKursSchein</Code>
	 **/	
	public void setKvvArchivKursSchein(boolean value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_bKvvArchivKursSchein));
		this.m_bKvvArchivKursSchein=value;
	}
	

	/**
	 * Recommended literature.
	 * @return Recommended literature.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.strKvvArchivKursLiteratur</Code>
	 **/
	public String getKvvArchivKursLiteratur(){
		return this.m_sKvvArchivKursLiteratur;
	}

	/**
	 * Recommended literature.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.strKvvArchivKursLiteratur</Code>
	 **/	
	public void setKvvArchivKursLiteratur(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKvvArchivKursLiteratur)));
		this.m_sKvvArchivKursLiteratur=value;
	}
	

	/**
	 * Additional information.
	 * @return Additional information.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.strKvvArchivKursZusatz</Code>
	 **/
	public String getKvvArchivKursZusatz(){
		return this.m_sKvvArchivKursZusatz;
	}

	/**
	 * Additional information.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.strKvvArchivKursZusatz</Code>
	 **/	
	public void setKvvArchivKursZusatz(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKvvArchivKursZusatz)));
		this.m_sKvvArchivKursZusatz=value;
	}
	

	/**
	 * Date.
	 * @return Date.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.strKvvArchivKursTerminFreitext</Code>
	 **/
	public String getKvvArchivKursTerminFreitext(){
		return this.m_sKvvArchivKursTerminFreitext;
	}

	/**
	 * Date.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.strKvvArchivKursTerminFreitext</Code>
	 **/	
	public void setKvvArchivKursTerminFreitext(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKvvArchivKursTerminFreitext)));
		this.m_sKvvArchivKursTerminFreitext=value;
	}
	

	/**
	 * Number of participants.
	 * @return Number of participants.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.intKvvArchivKursTeilnehmer</Code>
	 **/
	public int getKvvArchivKursTeilnehmer(){
		return this.m_iKvvArchivKursTeilnehmer;
	}

	/**
	 * Number of participants.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.intKvvArchivKursTeilnehmer</Code>
	 **/	
	public void setKvvArchivKursTeilnehmer(int value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_iKvvArchivKursTeilnehmer));
		this.m_iKvvArchivKursTeilnehmer=value;
	}
	

	/**
	 * Credit points that were handed out for this course.
	 * @return Credit points that were handed out for this course.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.sngKvvArchivKursCreditPts</Code>
	 **/
	public float getKvvArchivKursCreditPts(){
		return this.m_sKvvArchivKursCreditPts;
	}

	/**
	 * Credit points that were handed out for this course.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.sngKvvArchivKursCreditPts</Code>
	 **/	
	public void setKvvArchivKursCreditPts(float value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_sKvvArchivKursCreditPts));
		this.m_sKvvArchivKursCreditPts=value;
	}
	

	/**
	 * Custom 1
	 * @return Custom 1
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.strKvvArchivCustom1</Code>
	 **/
	public String getKvvArchivCustom1(){
		return this.m_sKvvArchivCustom1;
	}

	/**
	 * Custom 1
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.strKvvArchivCustom1</Code>
	 **/	
	public void setKvvArchivCustom1(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKvvArchivCustom1)));
		this.m_sKvvArchivCustom1=value;
	}
	

	/**
	 * Custom 2
	 * @return Custom 2
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.strKvvArchivCustom2</Code>
	 **/
	public String getKvvArchivCustom2(){
		return this.m_sKvvArchivCustom2;
	}

	/**
	 * Custom 2
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.strKvvArchivCustom2</Code>
	 **/	
	public void setKvvArchivCustom2(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKvvArchivCustom2)));
		this.m_sKvvArchivCustom2=value;
	}
	

	/**
	 * Custom 3.
	 * @return Custom 3.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.strKvvArchivCustom3</Code>
	 **/
	public String getKvvArchivCustom3(){
		return this.m_sKvvArchivCustom3;
	}

	/**
	 * Custom 3.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchiv.strKvvArchivCustom3</Code>
	 **/	
	public void setKvvArchivCustom3(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKvvArchivCustom3)));
		this.m_sKvvArchivCustom3=value;
	}
	


////////////////////////////////////////////////////////////////
// 3.   X M L  U T I L I T I E S
////////////////////////////////////////////////////////////////
	
	/**
	 * @return Objekt als XML-String
	 **/
	public String toXMLString(){
		return 	

			"<SdFakultaetID>" + m_lSdFakultaetID + "</SdFakultaetID>"  + 
			"<SdFakultaetName>" + m_sSdFakultaetName + "</SdFakultaetName>"  + 
			"<SdSeminarID>" + m_lSdSeminarID + "</SdSeminarID>"  + 
			"<SdSeminarName>" + m_sSdSeminarName + "</SdSeminarName>"  + 
			"<ID>" + m_lID + "</ID>"  + 
			"<KvvArchivLeistungID>" + m_lKvvArchivLeistungID + "</KvvArchivLeistungID>"  + 
			"<KvvArchivLeistungBezeichnung>" + m_sKvvArchivLeistungBezeichnung + "</KvvArchivLeistungBezeichnung>"  + 
			"<KvvArchivKurstypID>" + m_lKvvArchivKurstypID + "</KvvArchivKurstypID>"  + 
			"<KvvArchivKursID>" + m_lKvvArchivKursID + "</KvvArchivKursID>"  + 
			"<KvvArchivSemesterName>" + m_sKvvArchivSemesterName + "</KvvArchivSemesterName>"  + 
			"<KvvArchivDozentname>" + m_sKvvArchivDozentname + "</KvvArchivDozentname>"  + 
			"<KvvArchivDozentvorname>" + m_sKvvArchivDozentvorname + "</KvvArchivDozentvorname>"  + 
			"<KvvArchivDozenttitel>" + m_sKvvArchivDozenttitel + "</KvvArchivDozenttitel>"  + 
			"<KvvArchivKurstypBezeichnung>" + m_sKvvArchivKurstypBezeichnung + "</KvvArchivKurstypBezeichnung>"  + 
			"<KvvArchivKursEinordnung>" + m_sKvvArchivKursEinordnung + "</KvvArchivKursEinordnung>"  + 
			"<KvvArchivKursKategorie>" + m_sKvvArchivKursKategorie + "</KvvArchivKursKategorie>"  + 
			"<KvvArchivKurstitel>" + m_sKvvArchivKurstitel + "</KvvArchivKurstitel>"  + 
			"<KvvArchivKurstitel_en>" + m_sKvvArchivKurstitel_en + "</KvvArchivKurstitel_en>"  + 
			"<KvvArchivKursTag>" + m_sKvvArchivKursTag + "</KvvArchivKursTag>"  + 
			"<KvvArchivKursBeginn>" + m_tKvvArchivKursBeginn + "</KvvArchivKursBeginn>"  + 
			"<KvvArchivKursEnde>" + m_tKvvArchivKursEnde + "</KvvArchivKursEnde>"  + 
			"<KvvArchivKursRaum>" + m_sKvvArchivKursRaum + "</KvvArchivKursRaum>"  + 
			"<KvvArchivKursTag2>" + m_sKvvArchivKursTag2 + "</KvvArchivKursTag2>"  + 
			"<KvvArchivKursBeginn2>" + m_tKvvArchivKursBeginn2 + "</KvvArchivKursBeginn2>"  + 
			"<KvvArchivKursEnde2>" + m_tKvvArchivKursEnde2 + "</KvvArchivKursEnde2>"  + 
			"<KvvArchivKursKursRaum2>" + m_sKvvArchivKursKursRaum2 + "</KvvArchivKursKursRaum2>"  + 
			"<KvvArchivKursBeschreibung>" + m_sKvvArchivKursBeschreibung + "</KvvArchivKursBeschreibung>"  + 
			"<KvvArchivKursBeschreibung_en>" + m_sKvvArchivKursBeschreibung_en + "</KvvArchivKursBeschreibung_en>"  + 
			"<KvvArchivKursVoraussetzung>" + m_sKvvArchivKursVoraussetzung + "</KvvArchivKursVoraussetzung>"  + 
			"<KvvArchivKursSchein>" + m_bKvvArchivKursSchein + "</KvvArchivKursSchein>"  + 
			"<KvvArchivKursLiteratur>" + m_sKvvArchivKursLiteratur + "</KvvArchivKursLiteratur>"  + 
			"<KvvArchivKursZusatz>" + m_sKvvArchivKursZusatz + "</KvvArchivKursZusatz>"  + 
			"<KvvArchivKursTerminFreitext>" + m_sKvvArchivKursTerminFreitext + "</KvvArchivKursTerminFreitext>"  + 
			"<KvvArchivKursTeilnehmer>" + m_iKvvArchivKursTeilnehmer + "</KvvArchivKursTeilnehmer>"  + 
			"<KvvArchivKursCreditPts>" + m_sKvvArchivKursCreditPts + "</KvvArchivKursCreditPts>"  + 
			"<KvvArchivCustom1>" + m_sKvvArchivCustom1 + "</KvvArchivCustom1>"  + 
			"<KvvArchivCustom2>" + m_sKvvArchivCustom2 + "</KvvArchivCustom2>"  + 
			"<KvvArchivCustom3>" + m_sKvvArchivCustom3 + "</KvvArchivCustom3>" ;
	}

////////////////////////////////////////////////////////////////
// 4.   S Q L  U T I L I T I E S
////////////////////////////////////////////////////////////////


	/**
	 * Where-SQL für eindeutigen Index dieses Objekte in der Datenbank (in Tabelle 'tblBdKvvArchiv.').
	 * @return SQL-String wie z.B. "Id1=? and Id2=?"
	 **/
	private String getSQLWhereClause(){
		return 
			"\"lngID\"=?";
	}

	/**
	 * SQL-Befehl zum Löschen des Objekts in der Datenbank (aus Tabelle 'tblBdKvvArchiv.')
	 * @return SQL-String zum Löschen des Objekts, z.B. "delete from T1 where (Id1=?);"
	 **/
	private String toDBDeleteString(){
		return "delete from \"tblBdKvvArchiv\" where ( " + this.getSQLWhereClause() + ");";
	}

	/**
 	 * Setzt die Werte für alle Platzhalter in {@link #getSQLWhereClause()}.
	 * @param ii Offset für die Parameter (inklusive).
	 * @throws SQLException 
	 **/
	private void pokeWhere(int ii, PreparedStatement ps) throws SQLException{
		
		ps.setLong(ii++, m_lID);
	}

	/**
  	 * Füllt das SQL-Statement mit den aktuellen Werten des Objekts
	 * @throws SQLException
	 **/
	private void pokeStatement(PreparedStatement ps) throws SQLException{
		
		ps.setLong(1, m_lSdFakultaetID);
		ps.setString(2, m_sSdFakultaetName);
		ps.setLong(3, m_lSdSeminarID);
		ps.setString(4, m_sSdSeminarName);
		ps.setLong(5, m_lID);
		ps.setLong(6, m_lKvvArchivLeistungID);
		ps.setString(7, m_sKvvArchivLeistungBezeichnung);
		ps.setLong(8, m_lKvvArchivKurstypID);
		ps.setLong(9, m_lKvvArchivKursID);
		ps.setString(10, m_sKvvArchivSemesterName);
		ps.setString(11, m_sKvvArchivDozentname);
		ps.setString(12, m_sKvvArchivDozentvorname);
		ps.setString(13, m_sKvvArchivDozenttitel);
		ps.setString(14, m_sKvvArchivKurstypBezeichnung);
		ps.setString(15, m_sKvvArchivKursEinordnung);
		ps.setString(16, m_sKvvArchivKursKategorie);
		ps.setString(17, m_sKvvArchivKurstitel);
		ps.setString(18, m_sKvvArchivKurstitel_en);
		ps.setString(19, m_sKvvArchivKursTag);
		ps.setString(20, m_tKvvArchivKursBeginn);
		ps.setString(21, m_tKvvArchivKursEnde);
		ps.setString(22, m_sKvvArchivKursRaum);
		ps.setString(23, m_sKvvArchivKursTag2);
		ps.setString(24, m_tKvvArchivKursBeginn2);
		ps.setString(25, m_tKvvArchivKursEnde2);
		ps.setString(26, m_sKvvArchivKursKursRaum2);
		ps.setString(27, m_sKvvArchivKursBeschreibung);
		ps.setString(28, m_sKvvArchivKursBeschreibung_en);
		ps.setString(29, m_sKvvArchivKursVoraussetzung);
		ps.setBoolean(30, m_bKvvArchivKursSchein);
		ps.setString(31, m_sKvvArchivKursLiteratur);
		ps.setString(32, m_sKvvArchivKursZusatz);
		ps.setString(33, m_sKvvArchivKursTerminFreitext);
		ps.setInt(34, m_iKvvArchivKursTeilnehmer);
		ps.setFloat(35, m_sKvvArchivKursCreditPts);
		ps.setString(36, m_sKvvArchivCustom1);
		ps.setString(37, m_sKvvArchivCustom2);
		ps.setString(38, m_sKvvArchivCustom3);

	}

	/**
	 * SQL-Befehl zum Speichern der Objekteigenschaften in die Datenbank (Tabelle 'tblBdKvvArchiv.')
	 * @return PreparedStatemtent, das nach Aufruf von {@link #pokeStatement(PreparedStatement)} und {@link #execute()} das Update ausführt.
	 **/
	private String toDBUpdateString(){
		return "update \"tblBdKvvArchiv\" set " +
			"\"lngSdFakultaetID\"=?, " +
			"\"strSdFakultaetName\"=?, " +
			"\"lngSdSeminarID\"=?, " +
			"\"strSdSeminarName\"=?, " +
			"\"lngID\"=?, " +
			"\"lngKvvArchivLeistungID\"=?, " +
			"\"strKvvArchivLeistungBezeichnung\"=?, " +
			"\"lngKvvArchivKurstypID\"=?, " +
			"\"lngKvvArchivKursID\"=?, " +
			"\"strKvvArchivSemesterName\"=?, " +
			"\"strKvvArchivDozentname\"=?, " +
			"\"strKvvArchivDozentvorname\"=?, " +
			"\"strKvvArchivDozenttitel\"=?, " +
			"\"strKvvArchivKurstypBezeichnung\"=?, " +
			"\"strKvvArchivKursEinordnung\"=?, " +
			"\"strKvvArchivKursKategorie\"=?, " +
			"\"strKvvArchivKurstitel\"=?, " +
			"\"strKvvArchivKurstitel_en\"=?, " +
			"\"strKvvArchivKursTag\"=?, " +
			"\"dtmKvvArchivKursBeginn\"=?, " +
			"\"dtmKvvArchivKursEnde\"=?, " +
			"\"strKvvArchivKursRaum\"=?, " +
			"\"strKvvArchivKursTag2\"=?, " +
			"\"dtmKvvArchivKursBeginn2\"=?, " +
			"\"dtmKvvArchivKursEnde2\"=?, " +
			"\"strKvvArchivKursKursRaum2\"=?, " +
			"\"strKvvArchivKursBeschreibung\"=?, " +
			"\"strKvvArchivKursBeschreibung_en\"=?, " +
			"\"strKvvArchivKursVoraussetzung\"=?, " +
			"\"blnKvvArchivKursSchein\"=?, " +
			"\"strKvvArchivKursLiteratur\"=?, " +
			"\"strKvvArchivKursZusatz\"=?, " +
			"\"strKvvArchivKursTerminFreitext\"=?, " +
			"\"intKvvArchivKursTeilnehmer\"=?, " +
			"\"sngKvvArchivKursCreditPts\"=?, " +
			"\"strKvvArchivCustom1\"=?, " +
			"\"strKvvArchivCustom2\"=?, " +
			"\"strKvvArchivCustom3\"=?" +
			" where (" + this.getSQLWhereClause() + ");";
	}

	/**
	 * SQL-Befehl zum Hinzufügen des Objekts in die Datenbank (Tabelle 'tblBdKvvArchiv.')
	 * @return SQL-String "insert into...".
	 **/
	private String toDBAddString(){
		return "insert into \"tblBdKvvArchiv\" ( " +
			"\"lngSdFakultaetID\", \"strSdFakultaetName\", \"lngSdSeminarID\", \"strSdSeminarName\", \"lngID\", \"lngKvvArchivLeistungID\", \"strKvvArchivLeistungBezeichnung\", \"lngKvvArchivKurstypID\", \"lngKvvArchivKursID\", \"strKvvArchivSemesterName\", \"strKvvArchivDozentname\", \"strKvvArchivDozentvorname\", \"strKvvArchivDozenttitel\", \"strKvvArchivKurstypBezeichnung\", \"strKvvArchivKursEinordnung\", \"strKvvArchivKursKategorie\", \"strKvvArchivKurstitel\", \"strKvvArchivKurstitel_en\", \"strKvvArchivKursTag\", \"dtmKvvArchivKursBeginn\", \"dtmKvvArchivKursEnde\", \"strKvvArchivKursRaum\", \"strKvvArchivKursTag2\", \"dtmKvvArchivKursBeginn2\", \"dtmKvvArchivKursEnde2\", \"strKvvArchivKursKursRaum2\", \"strKvvArchivKursBeschreibung\", \"strKvvArchivKursBeschreibung_en\", \"strKvvArchivKursVoraussetzung\", \"blnKvvArchivKursSchein\", \"strKvvArchivKursLiteratur\", \"strKvvArchivKursZusatz\", \"strKvvArchivKursTerminFreitext\", \"intKvvArchivKursTeilnehmer\", \"sngKvvArchivKursCreditPts\", \"strKvvArchivCustom1\", \"strKvvArchivCustom2\", \"strKvvArchivCustom3\" ) VALUES ( ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
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
	private void init(long lngID) throws SQLException, NamingException{

		this.m_lID=lngID;

		
		PreparedStatement pstm = prepareStatement("select * from \"tblBdKvvArchiv\" where (" + this.getSQLWhereClause() + ");");
		pokeWhere(1,pstm);

		ResultSet rst=pstm.executeQuery();
		if(rst.next()) this.initByRst(rst);
		rst.close();
		this.disconnect();
		rst=null;
	}	
	
	/**
	 * Lade die Objekteigenschaften aus einem ResultSet.
	 * param rst ResultSet mit allen Spalten der Tabelle 'tblBdKvvArchiv'
	 * @throws SQLException 
	 **/
	protected void initByRst(ResultSet rst) throws SQLException{
		this.m_lSdFakultaetID=rst.getLong("lngSdFakultaetID");
		this.m_sSdFakultaetName=rst.getString("strSdFakultaetName");
		this.m_lSdSeminarID=rst.getLong("lngSdSeminarID");
		this.m_sSdSeminarName=rst.getString("strSdSeminarName");
		this.m_lID=rst.getLong("lngID");
		this.m_lKvvArchivLeistungID=rst.getLong("lngKvvArchivLeistungID");
		this.m_sKvvArchivLeistungBezeichnung=rst.getString("strKvvArchivLeistungBezeichnung");
		this.m_lKvvArchivKurstypID=rst.getLong("lngKvvArchivKurstypID");
		this.m_lKvvArchivKursID=rst.getLong("lngKvvArchivKursID");
		this.m_sKvvArchivSemesterName=rst.getString("strKvvArchivSemesterName");
		this.m_sKvvArchivDozentname=rst.getString("strKvvArchivDozentname");
		this.m_sKvvArchivDozentvorname=rst.getString("strKvvArchivDozentvorname");
		this.m_sKvvArchivDozenttitel=rst.getString("strKvvArchivDozenttitel");
		this.m_sKvvArchivKurstypBezeichnung=rst.getString("strKvvArchivKurstypBezeichnung");
		this.m_sKvvArchivKursEinordnung=rst.getString("strKvvArchivKursEinordnung");
		this.m_sKvvArchivKursKategorie=rst.getString("strKvvArchivKursKategorie");
		this.m_sKvvArchivKurstitel=rst.getString("strKvvArchivKurstitel");
		this.m_sKvvArchivKurstitel_en=rst.getString("strKvvArchivKurstitel_en");
		this.m_sKvvArchivKursTag=rst.getString("strKvvArchivKursTag");
		this.m_tKvvArchivKursBeginn=rst.getString("dtmKvvArchivKursBeginn");
		this.m_tKvvArchivKursEnde=rst.getString("dtmKvvArchivKursEnde");
		this.m_sKvvArchivKursRaum=rst.getString("strKvvArchivKursRaum");
		this.m_sKvvArchivKursTag2=rst.getString("strKvvArchivKursTag2");
		this.m_tKvvArchivKursBeginn2=rst.getString("dtmKvvArchivKursBeginn2");
		this.m_tKvvArchivKursEnde2=rst.getString("dtmKvvArchivKursEnde2");
		this.m_sKvvArchivKursKursRaum2=rst.getString("strKvvArchivKursKursRaum2");
		this.m_sKvvArchivKursBeschreibung=rst.getString("strKvvArchivKursBeschreibung");
		this.m_sKvvArchivKursBeschreibung_en=rst.getString("strKvvArchivKursBeschreibung_en");
		this.m_sKvvArchivKursVoraussetzung=rst.getString("strKvvArchivKursVoraussetzung");
		this.m_bKvvArchivKursSchein=rst.getBoolean("blnKvvArchivKursSchein");
		this.m_sKvvArchivKursLiteratur=rst.getString("strKvvArchivKursLiteratur");
		this.m_sKvvArchivKursZusatz=rst.getString("strKvvArchivKursZusatz");
		this.m_sKvvArchivKursTerminFreitext=rst.getString("strKvvArchivKursTerminFreitext");
		this.m_iKvvArchivKursTeilnehmer=rst.getInt("intKvvArchivKursTeilnehmer");
		this.m_sKvvArchivKursCreditPts=rst.getFloat("sngKvvArchivKursCreditPts");
		this.m_sKvvArchivCustom1=rst.getString("strKvvArchivCustom1");
		this.m_sKvvArchivCustom2=rst.getString("strKvvArchivCustom2");
		this.m_sKvvArchivCustom3=rst.getString("strKvvArchivCustom3");	
	}	
	
	/**
	 * Lade die Objekteigenschaften aus einer XML-Node.
	 * param node XML-Node mit allen Eigenschaften als Tags.
	 * @throws ParseException (Datum muss im ISO-Format yyyy-MM-dd übergeben werden).
	 **/
	private void initByNode(Node node) throws ParseException{
		this.m_lSdFakultaetID=Long.parseLong(shjNodeValue(node, "SdFakultaetID"));
		this.m_sSdFakultaetName=(shjNodeValue(node, "SdFakultaetName"));
		this.m_lSdSeminarID=Long.parseLong(shjNodeValue(node, "SdSeminarID"));
		this.m_sSdSeminarName=(shjNodeValue(node, "SdSeminarName"));
		this.m_lID=Long.parseLong(shjNodeValue(node, "ID"));
		this.m_lKvvArchivLeistungID=Long.parseLong(shjNodeValue(node, "KvvArchivLeistungID"));
		this.m_sKvvArchivLeistungBezeichnung=(shjNodeValue(node, "KvvArchivLeistungBezeichnung"));
		this.m_lKvvArchivKurstypID=Long.parseLong(shjNodeValue(node, "KvvArchivKurstypID"));
		this.m_lKvvArchivKursID=Long.parseLong(shjNodeValue(node, "KvvArchivKursID"));
		this.m_sKvvArchivSemesterName=(shjNodeValue(node, "KvvArchivSemesterName"));
		this.m_sKvvArchivDozentname=(shjNodeValue(node, "KvvArchivDozentname"));
		this.m_sKvvArchivDozentvorname=(shjNodeValue(node, "KvvArchivDozentvorname"));
		this.m_sKvvArchivDozenttitel=(shjNodeValue(node, "KvvArchivDozenttitel"));
		this.m_sKvvArchivKurstypBezeichnung=(shjNodeValue(node, "KvvArchivKurstypBezeichnung"));
		this.m_sKvvArchivKursEinordnung=(shjNodeValue(node, "KvvArchivKursEinordnung"));
		this.m_sKvvArchivKursKategorie=(shjNodeValue(node, "KvvArchivKursKategorie"));
		this.m_sKvvArchivKurstitel=(shjNodeValue(node, "KvvArchivKurstitel"));
		this.m_sKvvArchivKurstitel_en=(shjNodeValue(node, "KvvArchivKurstitel_en"));
		this.m_sKvvArchivKursTag=(shjNodeValue(node, "KvvArchivKursTag"));
		this.m_tKvvArchivKursBeginn=(shjNodeValue(node, "KvvArchivKursBeginn"));
		this.m_tKvvArchivKursEnde=(shjNodeValue(node, "KvvArchivKursEnde"));
		this.m_sKvvArchivKursRaum=(shjNodeValue(node, "KvvArchivKursRaum"));
		this.m_sKvvArchivKursTag2=(shjNodeValue(node, "KvvArchivKursTag2"));
		this.m_tKvvArchivKursBeginn2=(shjNodeValue(node, "KvvArchivKursBeginn2"));
		this.m_tKvvArchivKursEnde2=(shjNodeValue(node, "KvvArchivKursEnde2"));
		this.m_sKvvArchivKursKursRaum2=(shjNodeValue(node, "KvvArchivKursKursRaum2"));
		this.m_sKvvArchivKursBeschreibung=(shjNodeValue(node, "KvvArchivKursBeschreibung"));
		this.m_sKvvArchivKursBeschreibung_en=(shjNodeValue(node, "KvvArchivKursBeschreibung_en"));
		this.m_sKvvArchivKursVoraussetzung=(shjNodeValue(node, "KvvArchivKursVoraussetzung"));
		this.m_bKvvArchivKursSchein=Boolean.valueOf(shjNodeValue(node, "KvvArchivKursSchein")).booleanValue();
		this.m_sKvvArchivKursLiteratur=(shjNodeValue(node, "KvvArchivKursLiteratur"));
		this.m_sKvvArchivKursZusatz=(shjNodeValue(node, "KvvArchivKursZusatz"));
		this.m_sKvvArchivKursTerminFreitext=(shjNodeValue(node, "KvvArchivKursTerminFreitext"));
		this.m_iKvvArchivKursTeilnehmer=Integer.parseInt(shjNodeValue(node, "KvvArchivKursTeilnehmer"));
		this.m_sKvvArchivKursCreditPts=Float.valueOf(shjNodeValue(node, "KvvArchivKursCreditPts")).floatValue();
		this.m_sKvvArchivCustom1=(shjNodeValue(node, "KvvArchivCustom1"));
		this.m_sKvvArchivCustom2=(shjNodeValue(node, "KvvArchivCustom2"));
		this.m_sKvvArchivCustom3=(shjNodeValue(node, "KvvArchivCustom3"));
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
	public KvvArchiv(){}	
	
	/**
	 * Konstruktor mit Indexwerten (eindeutig) der assoziierten Datenbanktabelle
	 * @throws NamingException 
	 * @throws SQLException 
	 **/
	public KvvArchiv(long lngID) throws SQLException, NamingException{
		this.init(lngID);
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
	public KvvArchiv(ResultSet rst) throws SQLException{
		this.initByRst(rst);
		this.m_bIsDirty = false;
	}

	/**
	 * Konstruktor per XML-Darstellung des Objekts.
	 * @throws ParseException, if a date can't be read.
	 **/
	public KvvArchiv(Node node) throws ParseException{
		this.initByNode(node);
		this.m_bIsDirty = false;
	}

  }//Klassenende
