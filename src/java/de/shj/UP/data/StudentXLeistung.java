
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
import java.sql.Date;
import java.sql.Time;
import javax.naming.NamingException;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import org.w3c.dom.Node; 

// import com.shj.signUp.medUtil.LeistungEmail;
// import com.shj.signUp.util.SendMail;

/**
 *  Module: Exam
1:n-mapping of student to credits. 
Credits can have three stati:\n
1. Commitment: no real credit, just an exam-application or a commitment.\n
2. Credit: real credit.
3. Gesiegelt: extra ensured credit.
Modifikation ab 5-24 
 *  
 *  ALLGEMEINE INFORMATION ZU DEN OBJEKTEN DIESES PACKAGES:
 *  Diese Klasse kapselt die Tabelle 'StudentXLeistung' in 
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
public class StudentXLeistung extends shjCore{

	private static final long serialVersionUID = 7L;

////////////////////////////////////////////////////////////////
// 1.   P R I V A T E   D E K L A R A T I O N E N
////////////////////////////////////////////////////////////////
	
	private boolean m_bIsDirty = false;

	private long m_lSdSeminarID;
	private String m_sMatrikelnummer;
	private long m_lLeistungsID;
	private long m_lStudentLeistungCount;
	private long m_lDozentID;
	private int m_iNoteID;
	private long m_lStudentLeistungHISPrId;
	private long m_lKlausuranmeldungKurstypID;
	private long m_lKlausuranmeldungKursID;
	private String m_sSLKursUnivISID;
	private String m_sSLKursTag;
	private Time m_tSLKursBeginn;
	private Time m_tSLKursEnde;
	private String m_sSLKursRaum;
	private String m_sSLKursTag2;
	private Time m_tSLKursBeginn2;
	private Time m_tSLKursEnde2;
	private String m_sSLKursRaum2;
	private String m_sSLKursTitel;
	private String m_sSLKursTitel_en;
	private String m_sSLKursBeschreibung;
	private String m_sSLKursBeschreibung_en;
	private String m_sSLKursLiteratur;
	private String m_sSLKursZusatz;
	private String m_sSLKursAnmeldung;
	private String m_sSLKursVoraussetzung;
	private boolean m_bSLKursSchein;
	private String m_sSLKursEinordnung;
	private int m_iSLKursStunden;
	private String m_tSLKursLastChange;
	private Date m_dSLKursScheinanmeldungBis;
	private Date m_dSLKursScheinanmeldungVon;
	private boolean m_bSLKursScheinanmeldungErlaubt;
	private String m_sSLKursTerminFreitext;
	private int m_iSLKursTeilnehmer;
	private String m_sSLKursRaumExtern1;
	private String m_sSLKursRaumExtern2;
	private String m_sSLKursDetails;
	private String m_sStudentLeistungDetails;
	private boolean m_bStudentLeistungBestanden;
	private String m_sStudentLeistungNote;
	private float m_sStudentLeistungCreditPts;
	private Date m_dStudentLeistungAusstellungsdatum;
	private String m_sStudentLeistungAussteller;
	private String m_sStudentLeistungAusstellerVor;
	private String m_sStudentLeistungAusstellerTit;
	private String m_sStudentLeistungBemerkung;
	private boolean m_bStudentLeistungValidiert;
	private boolean m_bStudentLeistungPruefung;
	private Date m_dStudentLeistungAntragdatum;
	private boolean m_bStudentLeistungKlausuranmeldung;
	private boolean m_bStudentLeistungEditierbar;
	private boolean m_bStudentLeistungGesiegelt;
	private String m_sStudentLeistungCustom1;
	private String m_sStudentLeistungCustom2;
	private String m_sStudentLeistungCustom3;
	private long m_lModulID;
	private Date m_dStudentLeistungHISExport;
	private Date m_dStudentLeistungHISVerified;
        private boolean m_bStudentLeistungAnerkannt=false; // default

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
	 * Seminar
	 * @return Seminar
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.lngSdSeminarID</Code>
	 **/
	public long getSdSeminarID(){
		return this.m_lSdSeminarID;
	}

	/**
	 * Seminar
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.lngSdSeminarID</Code>
	 **/	
	public void setSdSeminarID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lSdSeminarID));
		this.m_lSdSeminarID=value;
	}
	

	/**
	 * Student"s registration number.
	 * @return Student"s registration number.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.strMatrikelnummer</Code>
	 **/
	public String getMatrikelnummer(){
		return this.m_sMatrikelnummer;
	}

	/**
	 * Student"s registration number.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.strMatrikelnummer</Code>
	 **/	
	public void setMatrikelnummer(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sMatrikelnummer)));
		this.m_sMatrikelnummer=value;
	}
	

	/**
	 * Id of credit.
	 * @return Id of credit.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.lngLeistungsID</Code>
	 **/
	public long getLeistungsID(){
		return this.m_lLeistungsID;
	}

	/**
	 * Id of credit.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.lngLeistungsID</Code>
	 **/	
	public void setLeistungsID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lLeistungsID));
		this.m_lLeistungsID=value;
	}
	

	/**
	 * How many credits of this type does this student already have (how often did he fail?)
	 * @return How many credits of this type does this student already have (how often did he fail?)
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.lngStudentLeistungCount</Code>
	 **/
	public long getStudentLeistungCount(){
		return this.m_lStudentLeistungCount;
	}

	/**
	 * How many credits of this type does this student already have (how often did he fail?)
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.lngStudentLeistungCount</Code>
	 **/	
	public void setStudentLeistungCount(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lStudentLeistungCount));
		this.m_lStudentLeistungCount=value;
	}
	

	/**
	 * Id of teacher who issued the credit.
	 * @return Id of teacher who issued the credit.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.lngDozentID</Code>
	 **/
	public long getDozentID(){
		return this.m_lDozentID;
	}

	/**
	 * Id of teacher who issued the credit.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.lngDozentID</Code>
	 **/	
	public void setDozentID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lDozentID));
		this.m_lDozentID=value;
	}
	

	/**
	 * Id of grade for this credit.
	 * @return Id of grade for this credit.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.intNoteID</Code>
	 **/
	public int getNoteID(){
		return this.m_iNoteID;
	}

	/**
	 * Id of grade for this credit.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.intNoteID</Code>
	 **/	
	public void setNoteID(int value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_iNoteID));
		this.m_iNoteID=value;
	}
	

	/**
	 * Falls Kurstyp- und KursId vorliegen, erhält die Leistung eine PrüfungsId aus HIS. Neu in Version 6-23, Dez. 2008.
	 * @return Falls Kurstyp- und KursId vorliegen, erhält die Leistung eine PrüfungsId aus HIS. Neu in Version 6-23, Dez. 2008.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.lngStudentLeistungHISPrId</Code>
	 **/
	public long getStudentLeistungHISPrId(){
		return this.m_lStudentLeistungHISPrId;
	}

	/**
	 * Falls Kurstyp- und KursId vorliegen, erhält die Leistung eine PrüfungsId aus HIS. Neu in Version 6-23, Dez. 2008.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.lngStudentLeistungHISPrId</Code>
	 **/	
	public void setStudentLeistungHISPrId(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lStudentLeistungHISPrId));
		this.m_lStudentLeistungHISPrId=value;
	}
	

	/**
	 * If this is a commitment rather than a credit, then what course does it refer to?
	 * @return If this is a commitment rather than a credit, then what course does it refer to?
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.lngKlausuranmeldungKurstypID</Code>
	 **/
	public long getKlausuranmeldungKurstypID(){
		return this.m_lKlausuranmeldungKurstypID;
	}

	/**
	 * If this is a commitment rather than a credit, then what course does it refer to?
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.lngKlausuranmeldungKurstypID</Code>
	 **/	
	public void setKlausuranmeldungKurstypID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lKlausuranmeldungKurstypID));
		this.m_lKlausuranmeldungKurstypID=value;
	}
	

	/**
	 * If this is a commitment rather than a credit, then what course does it refer to?
	 * @return If this is a commitment rather than a credit, then what course does it refer to?
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.lngKlausuranmeldungKursID</Code>
	 **/
	public long getKlausuranmeldungKursID(){
		return this.m_lKlausuranmeldungKursID;
	}

	/**
	 * If this is a commitment rather than a credit, then what course does it refer to?
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.lngKlausuranmeldungKursID</Code>
	 **/	
	public void setKlausuranmeldungKursID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lKlausuranmeldungKursID));
		this.m_lKlausuranmeldungKursID=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.strSLKursUnivISID</Code>
	 **/
	public String getSLKursUnivISID(){
		return this.m_sSLKursUnivISID;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.strSLKursUnivISID</Code>
	 **/	
	public void setSLKursUnivISID(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sSLKursUnivISID)));
		this.m_sSLKursUnivISID=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.strSLKursTag</Code>
	 **/
	public String getSLKursTag(){
		return this.m_sSLKursTag;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.strSLKursTag</Code>
	 **/	
	public void setSLKursTag(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sSLKursTag)));
		this.m_sSLKursTag=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.dtmSLKursBeginn</Code>
	 **/
	public Time getSLKursBeginn(){
		return this.m_tSLKursBeginn;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.dtmSLKursBeginn</Code>
	 **/	
	public void setSLKursBeginn(Time value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_tSLKursBeginn)));
		this.m_tSLKursBeginn=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.dtmSLKursEnde</Code>
	 **/
	public Time getSLKursEnde(){
		return this.m_tSLKursEnde;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.dtmSLKursEnde</Code>
	 **/	
	public void setSLKursEnde(Time value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_tSLKursEnde)));
		this.m_tSLKursEnde=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.strSLKursRaum</Code>
	 **/
	public String getSLKursRaum(){
		return this.m_sSLKursRaum;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.strSLKursRaum</Code>
	 **/	
	public void setSLKursRaum(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sSLKursRaum)));
		this.m_sSLKursRaum=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.strSLKursTag2</Code>
	 **/
	public String getSLKursTag2(){
		return this.m_sSLKursTag2;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.strSLKursTag2</Code>
	 **/	
	public void setSLKursTag2(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sSLKursTag2)));
		this.m_sSLKursTag2=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.dtmSLKursBeginn2</Code>
	 **/
	public Time getSLKursBeginn2(){
		return this.m_tSLKursBeginn2;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.dtmSLKursBeginn2</Code>
	 **/	
	public void setSLKursBeginn2(Time value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_tSLKursBeginn2)));
		this.m_tSLKursBeginn2=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.dtmSLKursEnde2</Code>
	 **/
	public Time getSLKursEnde2(){
		return this.m_tSLKursEnde2;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.dtmSLKursEnde2</Code>
	 **/	
	public void setSLKursEnde2(Time value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_tSLKursEnde2)));
		this.m_tSLKursEnde2=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.strSLKursRaum2</Code>
	 **/
	public String getSLKursRaum2(){
		return this.m_sSLKursRaum2;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.strSLKursRaum2</Code>
	 **/	
	public void setSLKursRaum2(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sSLKursRaum2)));
		this.m_sSLKursRaum2=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.strSLKursTitel</Code>
	 **/
	public String getSLKursTitel(){
		return this.m_sSLKursTitel;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.strSLKursTitel</Code>
	 **/	
	public void setSLKursTitel(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sSLKursTitel)));
		this.m_sSLKursTitel=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.strSLKursTitel_en</Code>
	 **/
	public String getSLKursTitel_en(){
		return this.m_sSLKursTitel_en;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.strSLKursTitel_en</Code>
	 **/	
	public void setSLKursTitel_en(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sSLKursTitel_en)));
		this.m_sSLKursTitel_en=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.strSLKursBeschreibung</Code>
	 **/
	public String getSLKursBeschreibung(){
		return this.m_sSLKursBeschreibung;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.strSLKursBeschreibung</Code>
	 **/	
	public void setSLKursBeschreibung(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sSLKursBeschreibung)));
		this.m_sSLKursBeschreibung=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.strSLKursBeschreibung_en</Code>
	 **/
	public String getSLKursBeschreibung_en(){
		return this.m_sSLKursBeschreibung_en;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.strSLKursBeschreibung_en</Code>
	 **/	
	public void setSLKursBeschreibung_en(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sSLKursBeschreibung_en)));
		this.m_sSLKursBeschreibung_en=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.strSLKursLiteratur</Code>
	 **/
	public String getSLKursLiteratur(){
		return this.m_sSLKursLiteratur;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.strSLKursLiteratur</Code>
	 **/	
	public void setSLKursLiteratur(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sSLKursLiteratur)));
		this.m_sSLKursLiteratur=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.strSLKursZusatz</Code>
	 **/
	public String getSLKursZusatz(){
		return this.m_sSLKursZusatz;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.strSLKursZusatz</Code>
	 **/	
	public void setSLKursZusatz(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sSLKursZusatz)));
		this.m_sSLKursZusatz=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.strSLKursAnmeldung</Code>
	 **/
	public String getSLKursAnmeldung(){
		return this.m_sSLKursAnmeldung;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.strSLKursAnmeldung</Code>
	 **/	
	public void setSLKursAnmeldung(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sSLKursAnmeldung)));
		this.m_sSLKursAnmeldung=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.strSLKursVoraussetzung</Code>
	 **/
	public String getSLKursVoraussetzung(){
		return this.m_sSLKursVoraussetzung;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.strSLKursVoraussetzung</Code>
	 **/	
	public void setSLKursVoraussetzung(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sSLKursVoraussetzung)));
		this.m_sSLKursVoraussetzung=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.blnSLKursSchein</Code>
	 **/
	public boolean getSLKursSchein(){
		return this.m_bSLKursSchein;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.blnSLKursSchein</Code>
	 **/	
	public void setSLKursSchein(boolean value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_bSLKursSchein));
		this.m_bSLKursSchein=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.strSLKursEinordnung</Code>
	 **/
	public String getSLKursEinordnung(){
		return this.m_sSLKursEinordnung;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.strSLKursEinordnung</Code>
	 **/	
	public void setSLKursEinordnung(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sSLKursEinordnung)));
		this.m_sSLKursEinordnung=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.intSLKursStunden</Code>
	 **/
	public int getSLKursStunden(){
		return this.m_iSLKursStunden;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.intSLKursStunden</Code>
	 **/	
	public void setSLKursStunden(int value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_iSLKursStunden));
		this.m_iSLKursStunden=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.dtmSLKursLastChange</Code>
	 **/
	public String getSLKursLastChange(){
		return this.m_tSLKursLastChange;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.dtmSLKursLastChange</Code>
	 **/	
	public void setSLKursLastChange(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_tSLKursLastChange));
		this.m_tSLKursLastChange=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.dtmSLKursScheinanmeldungBis</Code>
	 **/
	public Date getSLKursScheinanmeldungBis(){
		return this.m_dSLKursScheinanmeldungBis;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.dtmSLKursScheinanmeldungBis</Code>
	 **/	
	public void setSLKursScheinanmeldungBis(Date value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_dSLKursScheinanmeldungBis)));
		this.m_dSLKursScheinanmeldungBis=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.dtmSLKursScheinanmeldungVon</Code>
	 **/
	public Date getSLKursScheinanmeldungVon(){
		return this.m_dSLKursScheinanmeldungVon;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.dtmSLKursScheinanmeldungVon</Code>
	 **/	
	public void setSLKursScheinanmeldungVon(Date value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_dSLKursScheinanmeldungVon)));
		this.m_dSLKursScheinanmeldungVon=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.blnSLKursScheinanmeldungErlaubt</Code>
	 **/
	public boolean getSLKursScheinanmeldungErlaubt(){
		return this.m_bSLKursScheinanmeldungErlaubt;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.blnSLKursScheinanmeldungErlaubt</Code>
	 **/	
	public void setSLKursScheinanmeldungErlaubt(boolean value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_bSLKursScheinanmeldungErlaubt));
		this.m_bSLKursScheinanmeldungErlaubt=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.strSLKursTerminFreitext</Code>
	 **/
	public String getSLKursTerminFreitext(){
		return this.m_sSLKursTerminFreitext;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.strSLKursTerminFreitext</Code>
	 **/	
	public void setSLKursTerminFreitext(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sSLKursTerminFreitext)));
		this.m_sSLKursTerminFreitext=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.intSLKursTeilnehmer</Code>
	 **/
	public int getSLKursTeilnehmer(){
		return this.m_iSLKursTeilnehmer;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.intSLKursTeilnehmer</Code>
	 **/	
	public void setSLKursTeilnehmer(int value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_iSLKursTeilnehmer));
		this.m_iSLKursTeilnehmer=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.strSLKursRaumExtern1</Code>
	 **/
	public String getSLKursRaumExtern1(){
		return this.m_sSLKursRaumExtern1;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.strSLKursRaumExtern1</Code>
	 **/	
	public void setSLKursRaumExtern1(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sSLKursRaumExtern1)));
		this.m_sSLKursRaumExtern1=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.strSLKursRaumExtern2</Code>
	 **/
	public String getSLKursRaumExtern2(){
		return this.m_sSLKursRaumExtern2;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.strSLKursRaumExtern2</Code>
	 **/	
	public void setSLKursRaumExtern2(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sSLKursRaumExtern2)));
		this.m_sSLKursRaumExtern2=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.strSLKursDetails</Code>
	 **/
	public String getSLKursDetails(){
		return this.m_sSLKursDetails;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.strSLKursDetails</Code>
	 **/	
	public void setSLKursDetails(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sSLKursDetails)));
		this.m_sSLKursDetails=value;
	}
	

	/**
	 * Details on credit.
	 * @return Details on credit.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.strStudentLeistungDetails</Code>
	 **/
	public String getStudentLeistungDetails(){
		return this.m_sStudentLeistungDetails;
	}

	/**
	 * Details on credit.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.strStudentLeistungDetails</Code>
	 **/	
	public void setStudentLeistungDetails(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sStudentLeistungDetails)));
		this.m_sStudentLeistungDetails=value;
	}
	

	/**
	 * Is this credit passed?
	 * @return Is this credit passed?
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.blnStudentLeistungBestanden</Code>
	 **/
	public boolean getStudentLeistungBestanden(){
		return this.m_bStudentLeistungBestanden;
	}

	/**
	 * Is this credit passed?
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.blnStudentLeistungBestanden</Code>
	 **/	
	public void setStudentLeistungBestanden(boolean value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_bStudentLeistungBestanden));
		this.m_bStudentLeistungBestanden=value;
	}
	

	/**
	 * Grade for this credit in plain text.
	 * @return Grade for this credit in plain text.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.strStudentLeistungNote</Code>
	 **/
	public String getStudentLeistungNote(){
		return this.m_sStudentLeistungNote;
	}

	/**
	 * Grade for this credit in plain text.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.strStudentLeistungNote</Code>
	 **/	
	public void setStudentLeistungNote(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sStudentLeistungNote)));
		this.m_sStudentLeistungNote=value;
	}
	

	/**
	 * Credit points for this credit.
	 * @return Credit points for this credit.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.sngStudentLeistungCreditPts</Code>
	 **/
	public float getStudentLeistungCreditPts(){
		return this.m_sStudentLeistungCreditPts;
	}

	/**
	 * Credit points for this credit.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.sngStudentLeistungCreditPts</Code>
	 **/	
	public void setStudentLeistungCreditPts(float value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_sStudentLeistungCreditPts));
		this.m_sStudentLeistungCreditPts=value;
	}
	

	/**
	 * Date when this credit was issued.
	 * @return Date when this credit was issued.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.dtmStudentLeistungAusstellungsdatum</Code>
	 **/
	public Date getStudentLeistungAusstellungsdatum(){
		return this.m_dStudentLeistungAusstellungsdatum;
	}

	/**
	 * Date when this credit was issued.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.dtmStudentLeistungAusstellungsdatum</Code>
	 **/	
	public void setStudentLeistungAusstellungsdatum(Date value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_dStudentLeistungAusstellungsdatum)));
		this.m_dStudentLeistungAusstellungsdatum=value;
	}
	

	/**
	 * Issuer of this credit (plain text).
	 * @return Issuer of this credit (plain text).
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.strStudentLeistungAussteller</Code>
	 **/
	public String getStudentLeistungAussteller(){
		return this.m_sStudentLeistungAussteller;
	}

	/**
	 * Issuer of this credit (plain text).
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.strStudentLeistungAussteller</Code>
	 **/	
	public void setStudentLeistungAussteller(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sStudentLeistungAussteller)));
		this.m_sStudentLeistungAussteller=value;
	}
	

	/**
	 * Issuer of this credit (first name).
	 * @return Issuer of this credit (first name).
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.strStudentLeistungAusstellerVor</Code>
	 **/
	public String getStudentLeistungAusstellerVorname(){
		return this.m_sStudentLeistungAusstellerVor;
	}

	/**
	 * Issuer of this credit (first name).
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.strStudentLeistungAusstellerVor</Code>
	 **/	
	public void setStudentLeistungAusstellerVorname(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sStudentLeistungAusstellerVor)));
		this.m_sStudentLeistungAusstellerVor=value;
	}
	

	/**
	 * Acedemic title of the issuer of this credit.
	 * @return Acedemic title of the issuer of this credit.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.strStudentLeistungAusstellerTit</Code>
	 **/
	public String getStudentLeistungAusstellerTitel(){
		return this.m_sStudentLeistungAusstellerTit;
	}

	/**
	 * Acedemic title of the issuer of this credit.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.strStudentLeistungAusstellerTit</Code>
	 **/	
	public void setStudentLeistungAusstellerTitel(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sStudentLeistungAusstellerTit)));
		this.m_sStudentLeistungAusstellerTit=value;
	}
	

	/**
	 * Remark on this credit.
	 * @return Remark on this credit.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.strStudentLeistungBemerkung</Code>
	 **/
	public String getStudentLeistungBemerkung(){
		return this.m_sStudentLeistungBemerkung;
	}

	/**
	 * Remark on this credit.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.strStudentLeistungBemerkung</Code>
	 **/	
	public void setStudentLeistungBemerkung(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sStudentLeistungBemerkung)));
		this.m_sStudentLeistungBemerkung=value;
	}
	

	/**
	 * Validated? (_Not_ a commitment?).
	 * @return Validated? (_Not_ a commitment?).
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.blnStudentLeistungValidiert</Code>
	 **/
	public boolean getStudentLeistungValidiert(){
		return this.m_bStudentLeistungValidiert;
	}

	/**
	 * Validated? (_Not_ a commitment?).
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.blnStudentLeistungValidiert</Code>
	 **/	
	public void setStudentLeistungValidiert(boolean value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_bStudentLeistungValidiert));
		this.m_bStudentLeistungValidiert=value;
	}
	

	/**
	 * Is this a Pruefungsleistung (in contrast to a Zulassungsvoraussetzung?).
	 * @return Is this a Pruefungsleistung (in contrast to a Zulassungsvoraussetzung?).
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.blnStudentLeistungPruefung</Code>
	 **/
	public boolean getStudentLeistungPruefung(){
		return this.m_bStudentLeistungPruefung;
	}

	/**
	 * Is this a Pruefungsleistung (in contrast to a Zulassungsvoraussetzung?).
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.blnStudentLeistungPruefung</Code>
	 **/	
	public void setStudentLeistungPruefung(boolean value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_bStudentLeistungPruefung));
		this.m_bStudentLeistungPruefung=value;
	}
	

	/**
	 * When did the student apply for this credit?
	 * @return When did the student apply for this credit?
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.dtmStudentLeistungAntragdatum</Code>
	 **/
	public Date getStudentLeistungAntragdatum(){
		return this.m_dStudentLeistungAntragdatum;
	}

	/**
	 * When did the student apply for this credit?
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.dtmStudentLeistungAntragdatum</Code>
	 **/	
	public void setStudentLeistungAntragdatum(Date value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_dStudentLeistungAntragdatum)));
		this.m_dStudentLeistungAntragdatum=value;
	}
	

	/**
	 * Is this a commitment (rather than a "real" credit)?
	 * @return Is this a commitment (rather than a "real" credit)?
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.blnStudentLeistungKlausuranmeldung</Code>
	 **/
	public boolean getStudentLeistungKlausuranmeldung(){
		return this.m_bStudentLeistungKlausuranmeldung;
	}

	/**
	 * Is this a commitment (rather than a "real" credit)?
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.blnStudentLeistungKlausuranmeldung</Code>
	 **/	
	public void setStudentLeistungKlausuranmeldung(boolean value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_bStudentLeistungKlausuranmeldung));
		this.m_bStudentLeistungKlausuranmeldung=value;
	}
        

	/**
	 * Is this a commitment (rather than a "real" credit)?
	 * @return Is this a commitment (rather than a "real" credit)?
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.blnStudentLeistungKlausuranmeldung</Code>
	 **/
	public boolean getStudentLeistungAnerkannt(){
		return this.m_bStudentLeistungAnerkannt;
	}

	/**
	 * Is this a commitment (rather than a "real" credit)?
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.blnStudentLeistungKlausuranmeldung</Code>
	 **/	
	public void setStudentLeistungAnerkannt(boolean value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_bStudentLeistungAnerkannt));
		this.m_bStudentLeistungAnerkannt=value;
	}
	

	/**
	 * Can this credit still be changed? (After calculating the grade of an exam, it might be desirable to block changing grades?).
	 * @return Can this credit still be changed? (After calculating the grade of an exam, it might be desirable to block changing grades?).
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.blnStudentLeistungEditierbar</Code>
	 **/
	public boolean getStudentLeistungEditierbar(){
		return this.m_bStudentLeistungEditierbar;
	}

	/**
	 * Can this credit still be changed? (After calculating the grade of an exam, it might be desirable to block changing grades?).
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.blnStudentLeistungEditierbar</Code>
	 **/	
	public void setStudentLeistungEditierbar(boolean value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_bStudentLeistungEditierbar));
		this.m_bStudentLeistungEditierbar=value;
	}
	

	/**
	 * Optional flag indicating that this credit has been cross-examined.
	 * @return Optional flag indicating that this credit has been cross-examined.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.blnStudentLeistungGesiegelt</Code>
	 **/
	public boolean getStudentLeistungGesiegelt(){
		return this.m_bStudentLeistungGesiegelt;
	}

	/**
	 * Optional flag indicating that this credit has been cross-examined.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.blnStudentLeistungGesiegelt</Code>
	 **/	
	public void setStudentLeistungGesiegelt(boolean value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_bStudentLeistungGesiegelt));
		this.m_bStudentLeistungGesiegelt=value;
	}
	

	/**
	 * Custom
	 * @return Custom
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.strStudentLeistungCustom1</Code>
	 **/
	public String getStudentLeistungCustom1(){
		return this.m_sStudentLeistungCustom1;
	}

	/**
	 * Custom
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.strStudentLeistungCustom1</Code>
	 **/	
	public void setStudentLeistungCustom1(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sStudentLeistungCustom1)));
		this.m_sStudentLeistungCustom1=value;
	}
	

	/**
	 * Custom
	 * @return Custom
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.strStudentLeistungCustom2</Code>
	 **/
	public String getStudentLeistungCustom2(){
		return this.m_sStudentLeistungCustom2;
	}

	/**
	 * Custom
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.strStudentLeistungCustom2</Code>
	 **/	
	public void setStudentLeistungCustom2(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sStudentLeistungCustom2)));
		this.m_sStudentLeistungCustom2=value;
	}
	

	/**
	 * Custom
	 * @return Custom
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.strStudentLeistungCustom3</Code>
	 **/
	public String getStudentLeistungCustom3(){
		return this.m_sStudentLeistungCustom3;
	}

	/**
	 * Custom
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.strStudentLeistungCustom3</Code>
	 **/	
	public void setStudentLeistungCustom3(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sStudentLeistungCustom3)));
		this.m_sStudentLeistungCustom3=value;
	}
	

	/**
	 * New in 6-19: tblModulXLeistung (Seminar, LeistungID, ModulID) 1 - - n tblStudentXLeistung (Seminar, Leistung, _ModulID_).
	 * @return New in 6-19: tblModulXLeistung (Seminar, LeistungID, ModulID) 1 - - n tblStudentXLeistung (Seminar, Leistung, _ModulID_).
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.lngModulID</Code>
	 **/
	public long getModulID(){
		return this.m_lModulID;
	}

	/**
	 * New in 6-19: tblModulXLeistung (Seminar, LeistungID, ModulID) 1 - - n tblStudentXLeistung (Seminar, Leistung, _ModulID_).
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.lngModulID</Code>
	 **/	
	public void setModulID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lModulID));
		this.m_lModulID=value;
	}
	

	/**
	 * When was this credit reported to HIS-Software?
	 * @return When was this credit reported to HIS-Software?
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.dtmStudentLeistungHISExport</Code>
	 **/
	public Date getStudentLeistungHISExport(){
		return this.m_dStudentLeistungHISExport;
	}

	/**
	 * When was this credit reported to HIS-Software?
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.dtmStudentLeistungHISExport</Code>
	 **/	
	public void setStudentLeistungHISExport(Date value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_dStudentLeistungHISExport)));
		this.m_dStudentLeistungHISExport=value;
	}
	

	/**
	 * When has HIS-Software verified delivery of this credit? Deprecated in Version 6.23. Use tblStudentLeistungHIS instead.
	 * @return When has HIS-Software verified delivery of this credit? Deprecated in Version 6.23. Use tblStudentLeistungHIS instead.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.dtmStudentLeistungHISVerified</Code>
	 **/
	public Date getStudentLeistungHISVerified(){
		return this.m_dStudentLeistungHISVerified;
	}

	/**
	 * When has HIS-Software verified delivery of this credit? Deprecated in Version 6.23. Use tblStudentLeistungHIS instead.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXLeistung.dtmStudentLeistungHISVerified</Code>
	 **/	
	public void setStudentLeistungHISVerified(Date value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_dStudentLeistungHISVerified)));
		this.m_dStudentLeistungHISVerified=value;
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
			"<Matrikelnummer>" + m_sMatrikelnummer + "</Matrikelnummer>"  + 
			"<LeistungsID>" + m_lLeistungsID + "</LeistungsID>"  + 
			"<StudentLeistungCount>" + m_lStudentLeistungCount + "</StudentLeistungCount>"  + 
			"<DozentID>" + m_lDozentID + "</DozentID>"  + 
			"<NoteID>" + m_iNoteID + "</NoteID>"  + 
			"<StudentLeistungHISPrId>" + m_lStudentLeistungHISPrId + "</StudentLeistungHISPrId>"  + 
			"<KlausuranmeldungKurstypID>" + m_lKlausuranmeldungKurstypID + "</KlausuranmeldungKurstypID>"  + 
			"<KlausuranmeldungKursID>" + m_lKlausuranmeldungKursID + "</KlausuranmeldungKursID>"  + 
			"<SLKursUnivISID>" + m_sSLKursUnivISID + "</SLKursUnivISID>"  + 
			"<SLKursTag>" + m_sSLKursTag + "</SLKursTag>"  + 
			"<SLKursBeginn>" + m_tSLKursBeginn + "</SLKursBeginn>"  + 
			"<SLKursEnde>" + m_tSLKursEnde + "</SLKursEnde>"  + 
			"<SLKursRaum>" + m_sSLKursRaum + "</SLKursRaum>"  + 
			"<SLKursTag2>" + m_sSLKursTag2 + "</SLKursTag2>"  + 
			"<SLKursBeginn2>" + m_tSLKursBeginn2 + "</SLKursBeginn2>"  + 
			"<SLKursEnde2>" + m_tSLKursEnde2 + "</SLKursEnde2>"  + 
			"<SLKursRaum2>" + m_sSLKursRaum2 + "</SLKursRaum2>"  + 
			"<SLKursTitel>" + m_sSLKursTitel + "</SLKursTitel>"  + 
			"<SLKursTitel_en>" + m_sSLKursTitel_en + "</SLKursTitel_en>"  + 
			"<SLKursBeschreibung>" + m_sSLKursBeschreibung + "</SLKursBeschreibung>"  + 
			"<SLKursBeschreibung_en>" + m_sSLKursBeschreibung_en + "</SLKursBeschreibung_en>"  + 
			"<SLKursLiteratur>" + m_sSLKursLiteratur + "</SLKursLiteratur>"  + 
			"<SLKursZusatz>" + m_sSLKursZusatz + "</SLKursZusatz>"  + 
			"<SLKursAnmeldung>" + m_sSLKursAnmeldung + "</SLKursAnmeldung>"  + 
			"<SLKursVoraussetzung>" + m_sSLKursVoraussetzung + "</SLKursVoraussetzung>"  + 
			"<SLKursSchein>" + m_bSLKursSchein + "</SLKursSchein>"  + 
			"<SLKursEinordnung>" + m_sSLKursEinordnung + "</SLKursEinordnung>"  + 
			"<SLKursStunden>" + m_iSLKursStunden + "</SLKursStunden>"  + 
			"<SLKursLastChange>" + m_tSLKursLastChange + "</SLKursLastChange>"  + 
			"<SLKursScheinanmeldungBis>" + m_dSLKursScheinanmeldungBis + "</SLKursScheinanmeldungBis>"  + 
			"<SLKursScheinanmeldungVon>" + m_dSLKursScheinanmeldungVon + "</SLKursScheinanmeldungVon>"  + 
			"<SLKursScheinanmeldungErlaubt>" + m_bSLKursScheinanmeldungErlaubt + "</SLKursScheinanmeldungErlaubt>"  + 
			"<SLKursTerminFreitext>" + m_sSLKursTerminFreitext + "</SLKursTerminFreitext>"  + 
			"<SLKursTeilnehmer>" + m_iSLKursTeilnehmer + "</SLKursTeilnehmer>"  + 
			"<SLKursRaumExtern1>" + m_sSLKursRaumExtern1 + "</SLKursRaumExtern1>"  + 
			"<SLKursRaumExtern2>" + m_sSLKursRaumExtern2 + "</SLKursRaumExtern2>"  + 
			"<SLKursDetails>" + m_sSLKursDetails + "</SLKursDetails>"  + 
			"<StudentLeistungDetails>" + m_sStudentLeistungDetails + "</StudentLeistungDetails>"  + 
			"<StudentLeistungBestanden>" + m_bStudentLeistungBestanden + "</StudentLeistungBestanden>"  + 
			"<StudentLeistungNote>" + m_sStudentLeistungNote + "</StudentLeistungNote>"  + 
			"<StudentLeistungCreditPts>" + m_sStudentLeistungCreditPts + "</StudentLeistungCreditPts>"  + 
			"<StudentLeistungAusstellungsdatum>" + m_dStudentLeistungAusstellungsdatum + "</StudentLeistungAusstellungsdatum>"  + 
			"<StudentLeistungAussteller>" + m_sStudentLeistungAussteller + "</StudentLeistungAussteller>"  + 
			"<StudentLeistungAusstellerVor>" + m_sStudentLeistungAusstellerVor + "</StudentLeistungAusstellerVor>"  + 
			"<StudentLeistungAusstellerTit>" + m_sStudentLeistungAusstellerTit + "</StudentLeistungAusstellerTit>"  + 
			"<StudentLeistungBemerkung>" + m_sStudentLeistungBemerkung + "</StudentLeistungBemerkung>"  + 
			"<StudentLeistungValidiert>" + m_bStudentLeistungValidiert + "</StudentLeistungValidiert>"  + 
			"<StudentLeistungPruefung>" + m_bStudentLeistungPruefung + "</StudentLeistungPruefung>"  + 
			"<StudentLeistungAntragdatum>" + m_dStudentLeistungAntragdatum + "</StudentLeistungAntragdatum>"  + 
			"<StudentLeistungKlausuranmeldung>" + m_bStudentLeistungKlausuranmeldung + "</StudentLeistungKlausuranmeldung>"  + 
			"<StudentLeistungEditierbar>" + m_bStudentLeistungEditierbar + "</StudentLeistungEditierbar>"  + 
			"<StudentLeistungGesiegelt>" + m_bStudentLeistungGesiegelt + "</StudentLeistungGesiegelt>"  + 
			"<StudentLeistungCustom1>" + m_sStudentLeistungCustom1 + "</StudentLeistungCustom1>"  + 
			"<StudentLeistungCustom2>" + m_sStudentLeistungCustom2 + "</StudentLeistungCustom2>"  + 
			"<StudentLeistungCustom3>" + m_sStudentLeistungCustom3 + "</StudentLeistungCustom3>"  + 
			"<ModulID>" + m_lModulID + "</ModulID>"  + 
			"<StudentLeistungHISExport>" + m_dStudentLeistungHISExport + "</StudentLeistungHISExport>"  + 
			"<StudentLeistungHISVerified>" + m_dStudentLeistungHISVerified + "</StudentLeistungHISVerified>" ;
	}

////////////////////////////////////////////////////////////////
// 4.   S Q L  U T I L I T I E S
////////////////////////////////////////////////////////////////


	/**
	 * Where-SQL für eindeutigen Index dieses Objekte in der Datenbank (in Tabelle 'tblBdStudentXLeistung.').
	 * @return SQL-String wie z.B. "Id1=? and Id2=?"
	 **/
	private String getSQLWhereClause(){
		return 
			"\"lngSdSeminarID\"=? AND " + 
			"\"strMatrikelnummer\"=? AND " + 
			"\"lngLeistungsID\"=? AND " + 
			"\"lngStudentLeistungCount\"=?";
	}
	
	/**
	 * Unique where-clause for table 'tblBdStudentXLeistung.'
	 * @return String (SQL-code, postgres-specific)
	 *
	 * #hack: you might want to add quotes in where-clause here or there!
	 **/
	protected String getSQLWhereClauseOld(){
		return 
			"\"lngSdSeminarID\"=" + this.m_lSdSeminarID + " AND "  + 
			"\"strMatrikelnummer\"='" + this.m_sMatrikelnummer + "' AND "  + 
			"\"lngLeistungsID\"=" + this.m_lLeistungsID + " AND "  + 
			"\"lngStudentLeistungCount\"=" + this.m_lStudentLeistungCount;
	}

	/**
	 * SQL-Befehl zum Löschen des Objekts in der Datenbank (aus Tabelle 'tblBdStudentXLeistung.')
	 * @return SQL-String zum Löschen des Objekts, z.B. "delete from T1 where (Id1=?);"
	 **/
	private String toDBDeleteString(){
		return "delete from \"tblBdStudentXLeistung\" where ( " + this.getSQLWhereClause() + ");";
	}

	/**
 	 * Setzt die Werte für alle Platzhalter in {@link #getSQLWhereClause()}.
	 * @param ii Offset für die Parameter (inklusive).
	 * @throws SQLException 
	 **/
	private void pokeWhere(int ii, PreparedStatement ps) throws SQLException{
		
		ps.setLong(ii++, m_lSdSeminarID);
		ps.setString(ii++, m_sMatrikelnummer);
		ps.setLong(ii++, m_lLeistungsID);
		ps.setLong(ii++, m_lStudentLeistungCount);
	}

	/**
  	 * Füllt das SQL-Statement mit den aktuellen Werten des Objekts
	 * @throws SQLException
	 **/
	private void pokeStatement(PreparedStatement ps) throws SQLException{
		
		ps.setLong(1, m_lSdSeminarID);
		ps.setString(2, m_sMatrikelnummer);
		ps.setLong(3, m_lLeistungsID);
		ps.setLong(4, m_lStudentLeistungCount);
		ps.setLong(5, m_lDozentID);
		ps.setInt(6, m_iNoteID);
		ps.setLong(7, m_lStudentLeistungHISPrId);
		ps.setLong(8, m_lKlausuranmeldungKurstypID);
		ps.setLong(9, m_lKlausuranmeldungKursID);
		ps.setString(10, m_sSLKursUnivISID);
		ps.setString(11, m_sSLKursTag);
		ps.setTime(12, m_tSLKursBeginn);
		ps.setTime(13, m_tSLKursEnde);
		ps.setString(14, m_sSLKursRaum);
		ps.setString(15, m_sSLKursTag2);
		ps.setTime(16, m_tSLKursBeginn2);
		ps.setTime(17, m_tSLKursEnde2);
		ps.setString(18, m_sSLKursRaum2);
		ps.setString(19, m_sSLKursTitel);
		ps.setString(20, m_sSLKursTitel_en);
		ps.setString(21, m_sSLKursBeschreibung);
		ps.setString(22, m_sSLKursBeschreibung_en);
		ps.setString(23, m_sSLKursLiteratur);
		ps.setString(24, m_sSLKursZusatz);
		ps.setString(25, m_sSLKursAnmeldung);
		ps.setString(26, m_sSLKursVoraussetzung);
		ps.setBoolean(27, m_bSLKursSchein);
		ps.setString(28, m_sSLKursEinordnung);
		ps.setInt(29, m_iSLKursStunden);
		try {
			ps.setTimestamp(30, (new java.sql.Timestamp(g_ISO_DATE_FORMAT.parse(m_tSLKursLastChange).getTime())));
		} catch (Exception e) {
			System.err.println("KursLastChange: kann Timestamp nicht in StudentXLeistung übernehmen: " + e.getMessage() + "; setze Timestamp auf 6.11.1969, 04:16 Uhr");
			try {
				ps.setTimestamp(30, (new java.sql.Timestamp(g_ISO_DATE_FORMAT.parse("1969-11-6 04:16").getTime())));
			} catch (ParseException e1) {
				System.err.println("KursLastChange: kann Timestamp nicht in StudentXLeistung übernehmen und auch nicht auf 6.11.1969 setzen: " + e.getMessage() + ";");
			}
		}
		ps.setDate(31, m_dSLKursScheinanmeldungBis);
		ps.setDate(32, m_dSLKursScheinanmeldungVon);
		ps.setBoolean(33, m_bSLKursScheinanmeldungErlaubt);
		ps.setString(34, m_sSLKursTerminFreitext);
		ps.setInt(35, m_iSLKursTeilnehmer);
		ps.setString(36, m_sSLKursRaumExtern1);
		ps.setString(37, m_sSLKursRaumExtern2);
		ps.setString(38, m_sSLKursDetails);
		ps.setString(39, m_sStudentLeistungDetails);
		ps.setBoolean(40, m_bStudentLeistungBestanden);
		ps.setString(41, m_sStudentLeistungNote);
		ps.setFloat(42, m_sStudentLeistungCreditPts);
		ps.setDate(43, m_dStudentLeistungAusstellungsdatum);
		ps.setString(44, m_sStudentLeistungAussteller);
		ps.setString(45, m_sStudentLeistungAusstellerVor);
		ps.setString(46, m_sStudentLeistungAusstellerTit);
		ps.setString(47, m_sStudentLeistungBemerkung);
		ps.setBoolean(48, m_bStudentLeistungValidiert);
		ps.setBoolean(49, m_bStudentLeistungPruefung);
		ps.setDate(50, m_dStudentLeistungAntragdatum);
		ps.setBoolean(51, m_bStudentLeistungKlausuranmeldung);
		ps.setBoolean(52, m_bStudentLeistungEditierbar);
		ps.setBoolean(53, m_bStudentLeistungGesiegelt);
		ps.setString(54, m_sStudentLeistungCustom1);
		ps.setString(55, m_sStudentLeistungCustom2);
		ps.setString(56, m_sStudentLeistungCustom3);
		
		// ModulID kann null sein:
		if(m_lModulID!=0){ ps.setLong(57, m_lModulID);
		}else{
			ps.setNull(57, java.sql.Types.BIGINT); 
		}
		ps.setDate(58, m_dStudentLeistungHISExport);
		ps.setDate(59, m_dStudentLeistungHISVerified);
                ps.setBoolean(60, m_bStudentLeistungAnerkannt);

	}

	/**
	 * SQL-Befehl zum Speichern der Objekteigenschaften in die Datenbank (Tabelle 'tblBdStudentXLeistung.')
	 * @return PreparedStatemtent, das nach Aufruf von {@link #pokeStatement(PreparedStatement)} und {@link #execute()} das Update ausführt.
	 **/
	private String toDBUpdateString(){
		return "update \"tblBdStudentXLeistung\" set " +
			"\"lngSdSeminarID\"=?, " +
			"\"strMatrikelnummer\"=?, " +
			"\"lngLeistungsID\"=?, " +
			"\"lngStudentLeistungCount\"=?, " +
			"\"lngDozentID\"=?, " +
			"\"intNoteID\"=?, " +
			"\"lngStudentLeistungHISPrId\"=?, " +
			"\"lngKlausuranmeldungKurstypID\"=?, " +
			"\"lngKlausuranmeldungKursID\"=?, " +
			"\"strSLKursUnivISID\"=?, " +
			"\"strSLKursTag\"=?, " +
			"\"dtmSLKursBeginn\"=?, " +
			"\"dtmSLKursEnde\"=?, " +
			"\"strSLKursRaum\"=?, " +
			"\"strSLKursTag2\"=?, " +
			"\"dtmSLKursBeginn2\"=?, " +
			"\"dtmSLKursEnde2\"=?, " +
			"\"strSLKursRaum2\"=?, " +
			"\"strSLKursTitel\"=?, " +
			"\"strSLKursTitel_en\"=?, " +
			"\"strSLKursBeschreibung\"=?, " +
			"\"strSLKursBeschreibung_en\"=?, " +
			"\"strSLKursLiteratur\"=?, " +
			"\"strSLKursZusatz\"=?, " +
			"\"strSLKursAnmeldung\"=?, " +
			"\"strSLKursVoraussetzung\"=?, " +
			"\"blnSLKursSchein\"=?, " +
			"\"strSLKursEinordnung\"=?, " +
			"\"intSLKursStunden\"=?, " +
			"\"dtmSLKursLastChange\"=?, " +
			"\"dtmSLKursScheinanmeldungBis\"=?, " +
			"\"dtmSLKursScheinanmeldungVon\"=?, " +
			"\"blnSLKursScheinanmeldungErlaubt\"=?, " +
			"\"strSLKursTerminFreitext\"=?, " +
			"\"intSLKursTeilnehmer\"=?, " +
			"\"strSLKursRaumExtern1\"=?, " +
			"\"strSLKursRaumExtern2\"=?, " +
			"\"strSLKursDetails\"=?, " +
			"\"strStudentLeistungDetails\"=?, " +
			"\"blnStudentLeistungBestanden\"=?, " +
			"\"strStudentLeistungNote\"=?, " +
			"\"sngStudentLeistungCreditPts\"=?, " +
			"\"dtmStudentLeistungAusstellungsd\"=?, " +
			"\"strStudentLeistungAussteller\"=?, " +
			"\"strStudentLeistungAusstellerVor\"=?, " +
			"\"strStudentLeistungAusstellerTit\"=?, " +
			"\"strStudentLeistungBemerkung\"=?, " +
			"\"blnStudentLeistungValidiert\"=?, " +
			"\"blnStudentLeistungPruefung\"=?, " +
			"\"dtmStudentLeistungAntragdatum\"=?, " +
			"\"blnStudentLeistungKlausuranmeldung\"=?, " +
			"\"blnStudentLeistungEditierbar\"=?, " +
			"\"blnStudentLeistungGesiegelt\"=?, " +
			"\"strStudentLeistungCustom1\"=?, " +
			"\"strStudentLeistungCustom2\"=?, " +
			"\"strStudentLeistungCustom3\"=?, " +
			"\"lngModulID\"=?, " +
			"\"dtmStudentLeistungHISExport\"=?, " +
			"\"dtmStudentLeistungHISVerified\"=?," +
                        "\"blnStudentLeistungAnerkannt\"=?" +
			" where (" + this.getSQLWhereClause() + ");";
	}

	/**
	 * SQL-Befehl zum Hinzufügen des Objekts in die Datenbank (Tabelle 'tblBdStudentXLeistung.')
	 * @return SQL-String "insert into...".
	 **/
	private String toDBAddString(){
		return "insert into \"tblBdStudentXLeistung\" ( " +
			"\"lngSdSeminarID\", \"strMatrikelnummer\", \"lngLeistungsID\", \"lngStudentLeistungCount\", \"lngDozentID\", \"intNoteID\", \"lngStudentLeistungHISPrId\", \"lngKlausuranmeldungKurstypID\", \"lngKlausuranmeldungKursID\", \"strSLKursUnivISID\", \"strSLKursTag\", \"dtmSLKursBeginn\", \"dtmSLKursEnde\", \"strSLKursRaum\", \"strSLKursTag2\", \"dtmSLKursBeginn2\", \"dtmSLKursEnde2\", \"strSLKursRaum2\", \"strSLKursTitel\", \"strSLKursTitel_en\", \"strSLKursBeschreibung\", \"strSLKursBeschreibung_en\", \"strSLKursLiteratur\", \"strSLKursZusatz\", \"strSLKursAnmeldung\", \"strSLKursVoraussetzung\", \"blnSLKursSchein\", \"strSLKursEinordnung\", \"intSLKursStunden\", \"dtmSLKursLastChange\", \"dtmSLKursScheinanmeldungBis\", \"dtmSLKursScheinanmeldungVon\", \"blnSLKursScheinanmeldungErlaubt\", \"strSLKursTerminFreitext\", \"intSLKursTeilnehmer\", \"strSLKursRaumExtern1\", \"strSLKursRaumExtern2\", \"strSLKursDetails\", \"strStudentLeistungDetails\", \"blnStudentLeistungBestanden\", \"strStudentLeistungNote\", \"sngStudentLeistungCreditPts\", \"dtmStudentLeistungAusstellungsd\", \"strStudentLeistungAussteller\", \"strStudentLeistungAusstellerVor\", \"strStudentLeistungAusstellerTit\", \"strStudentLeistungBemerkung\", \"blnStudentLeistungValidiert\", \"blnStudentLeistungPruefung\", \"dtmStudentLeistungAntragdatum\", \"blnStudentLeistungKlausuranmeldung\", \"blnStudentLeistungEditierbar\", \"blnStudentLeistungGesiegelt\", \"strStudentLeistungCustom1\", \"strStudentLeistungCustom2\", \"strStudentLeistungCustom3\", \"lngModulID\", \"dtmStudentLeistungHISExport\", \"dtmStudentLeistungHISVerified\", \"blnStudentLeistungAnerkannt\" ) VALUES ( ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
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
	public void init(long lngSdSeminarID, String strMatrikelnummer, long lngLeistungsID, long lngStudentLeistungCount) throws SQLException, NamingException{

		this.m_lSdSeminarID=lngSdSeminarID;

		this.m_sMatrikelnummer=strMatrikelnummer;

		this.m_lLeistungsID=lngLeistungsID;

		this.m_lStudentLeistungCount=lngStudentLeistungCount;

		
		PreparedStatement pstm = prepareStatement("select * from \"tblBdStudentXLeistung\" where (" + this.getSQLWhereClause() + ");");
		pokeWhere(1,pstm);

		ResultSet rst=pstm.executeQuery();
		if(rst.next()) this.initByRst(rst);
		rst.close();
		this.disconnect();
		rst=null;
	}	
	
	/**
	 * Lade die Objekteigenschaften aus einem ResultSet.
	 * param rst ResultSet mit allen Spalten der Tabelle 'tblBdStudentXLeistung'
	 * (public wg. HTACommitmentSaveBulk).
	 * @throws SQLException 
	 **/
	public void initByRst(ResultSet rst) throws SQLException{
		this.m_lSdSeminarID=rst.getLong("lngSdSeminarID");
		this.m_sMatrikelnummer=rst.getString("strMatrikelnummer");
		this.m_lLeistungsID=rst.getLong("lngLeistungsID");
		this.m_lStudentLeistungCount=rst.getLong("lngStudentLeistungCount");
		this.m_lDozentID=rst.getLong("lngDozentID");
		this.m_iNoteID=rst.getInt("intNoteID");
		this.m_lStudentLeistungHISPrId=rst.getLong("lngStudentLeistungHISPrId");
		this.m_lKlausuranmeldungKurstypID=rst.getLong("lngKlausuranmeldungKurstypID");
		this.m_lKlausuranmeldungKursID=rst.getLong("lngKlausuranmeldungKursID");
		this.m_sSLKursUnivISID=rst.getString("strSLKursUnivISID");
		this.m_sSLKursTag=rst.getString("strSLKursTag");
		this.m_tSLKursBeginn=rst.getTime("dtmSLKursBeginn");
		this.m_tSLKursEnde=rst.getTime("dtmSLKursEnde");
		this.m_sSLKursRaum=rst.getString("strSLKursRaum");
		this.m_sSLKursTag2=rst.getString("strSLKursTag2");
		this.m_tSLKursBeginn2=rst.getTime("dtmSLKursBeginn2");
		this.m_tSLKursEnde2=rst.getTime("dtmSLKursEnde2");
		this.m_sSLKursRaum2=rst.getString("strSLKursRaum2");
		this.m_sSLKursTitel=rst.getString("strSLKursTitel");
		this.m_sSLKursTitel_en=rst.getString("strSLKursTitel_en");
		this.m_sSLKursBeschreibung=rst.getString("strSLKursBeschreibung");
		this.m_sSLKursBeschreibung_en=rst.getString("strSLKursBeschreibung_en");
		this.m_sSLKursLiteratur=rst.getString("strSLKursLiteratur");
		this.m_sSLKursZusatz=rst.getString("strSLKursZusatz");
		this.m_sSLKursAnmeldung=rst.getString("strSLKursAnmeldung");
		this.m_sSLKursVoraussetzung=rst.getString("strSLKursVoraussetzung");
		this.m_bSLKursSchein=rst.getBoolean("blnSLKursSchein");
		this.m_sSLKursEinordnung=rst.getString("strSLKursEinordnung");
		this.m_iSLKursStunden=rst.getInt("intSLKursStunden");
		this.m_tSLKursLastChange=rst.getString("dtmSLKursLastChange");
		this.m_dSLKursScheinanmeldungBis=rst.getDate("dtmSLKursScheinanmeldungBis");
		this.m_dSLKursScheinanmeldungVon=rst.getDate("dtmSLKursScheinanmeldungVon");
		this.m_bSLKursScheinanmeldungErlaubt=rst.getBoolean("blnSLKursScheinanmeldungErlaubt");
		this.m_sSLKursTerminFreitext=rst.getString("strSLKursTerminFreitext");
		this.m_iSLKursTeilnehmer=rst.getInt("intSLKursTeilnehmer");
		this.m_sSLKursRaumExtern1=rst.getString("strSLKursRaumExtern1");
		this.m_sSLKursRaumExtern2=rst.getString("strSLKursRaumExtern2");
		this.m_sSLKursDetails=rst.getString("strSLKursDetails");
		this.m_sStudentLeistungDetails=rst.getString("strStudentLeistungDetails");
		this.m_bStudentLeistungBestanden=rst.getBoolean("blnStudentLeistungBestanden");
		this.m_sStudentLeistungNote=rst.getString("strStudentLeistungNote");
		this.m_sStudentLeistungCreditPts=rst.getFloat("sngStudentLeistungCreditPts");
		this.m_dStudentLeistungAusstellungsdatum=rst.getDate("dtmStudentLeistungAusstellungsd");
		this.m_sStudentLeistungAussteller=rst.getString("strStudentLeistungAussteller");
		this.m_sStudentLeistungAusstellerVor=rst.getString("strStudentLeistungAusstellerVor");
		this.m_sStudentLeistungAusstellerTit=rst.getString("strStudentLeistungAusstellerTit");
		this.m_sStudentLeistungBemerkung=rst.getString("strStudentLeistungBemerkung");
		this.m_bStudentLeistungValidiert=rst.getBoolean("blnStudentLeistungValidiert");
		this.m_bStudentLeistungPruefung=rst.getBoolean("blnStudentLeistungPruefung");
		this.m_dStudentLeistungAntragdatum=rst.getDate("dtmStudentLeistungAntragdatum");
		this.m_bStudentLeistungKlausuranmeldung=rst.getBoolean("blnStudentLeistungKlausuranmeldung");
		this.m_bStudentLeistungEditierbar=rst.getBoolean("blnStudentLeistungEditierbar");
		this.m_bStudentLeistungGesiegelt=rst.getBoolean("blnStudentLeistungGesiegelt");
		this.m_sStudentLeistungCustom1=rst.getString("strStudentLeistungCustom1");
		this.m_sStudentLeistungCustom2=rst.getString("strStudentLeistungCustom2");
		this.m_sStudentLeistungCustom3=rst.getString("strStudentLeistungCustom3");
		this.m_lModulID=rst.getLong("lngModulID");
		this.m_dStudentLeistungHISExport=rst.getDate("dtmStudentLeistungHISExport");
		this.m_dStudentLeistungHISVerified=rst.getDate("dtmStudentLeistungHISVerified");	
                this.m_bStudentLeistungAnerkannt=rst.getBoolean("blnStudentLeistungAnerkannt");
	}		
	
////////////////////////////////////////////////////////////////
// 6.   S Q L  U T I L I T I E S
////////////////////////////////////////////////////////////////

	public boolean setModulToNull() throws Exception{
		// Since this is not SQL-Exception-safe for the Matrikelnummer:
		Long.parseLong(this.m_sMatrikelnummer);

		if( sqlExe("update \"tblBdStudentXLeistung\" set \"lngModulID\"=null where (" + 
				"\"lngSdSeminarID\"=" + m_lSdSeminarID + " AND " + 
				"\"strMatrikelnummer\"='" + m_sMatrikelnummer + "' AND " + 
				"\"lngLeistungsID\"=" + m_lLeistungsID + " AND " + 
				"\"lngStudentLeistungCount\"=" + m_lStudentLeistungCount +
				");") ){
			m_lModulID=0;
			return true;
		}
		return false;
			
	}
	
	/**
	 * This method arises from the wish to be able to change one credit, say "Grammar", to 
	 * another type, say "Grammar II". It looks up the next LeistungCount of the 
	 * new Id for this student. (It is possible that there already is a "Grammar II" credit 
	 * stored). The default counter is set to 0.
	 * @version 6.02.01 (April 27, 2005); hj.
	 * @param lngLeistungIdNew LeistungsId to which to change this StudentXLeistung-item
	 * @return true for success (or nothing to do), false for error (then, the LeistungId cannot be easily changed).
	 * @throws Exception when the basic db connection is broken.
	 */
	public boolean updateLeistungIdTo(long lngLeistungIdNew) throws Exception{
		if(lngLeistungIdNew==this.getLeistungsID()) return true; // (nothing to do)
		
		// Since this is not SQL-Exception-safe for the Matrikelnummer:
		Long.parseLong(this.m_sMatrikelnummer);
		long lngNewLeistungNextCount = 0;
		try{
                    PreparedStatement pstm = this.prepareStatement("select \"lngStudentLeistungCount\" "
                            + "from \"tblBdStudentXLeistung\" where "
                            + "\"lngSdSeminarID\"=? AND "  +
                            "\"strMatrikelnummer\"=? AND "  +
                            "\"lngLeistungsID\"=?");
                    
                    pstm.setLong(1, m_lSdSeminarID);
                    pstm.setString(2, this.m_sMatrikelnummer);
                    pstm.setLong(3, lngLeistungIdNew);
                    ResultSet rN = pstm.executeQuery();
                    rN.next();
                    lngNewLeistungNextCount = rN.getLong("lngStudentLeistungCount")+1;
		}catch(Exception eNoLeistungOfThatIdFound){}		
		
		boolean bReturn= sqlExe("update \"tblBdStudentXLeistung\" set \"lngLeistungsID\"=" + lngLeistungIdNew + ", \"lngStudentLeistungCount\"=" + lngNewLeistungNextCount + " where (" + 
				"\"lngSdSeminarID\"=" + m_lSdSeminarID + " AND " + 
				"\"strMatrikelnummer\"='" + m_sMatrikelnummer + "' AND " + 
				"\"lngLeistungsID\"=" + m_lLeistungsID + " AND " + 
				"\"lngStudentLeistungCount\"=" + m_lStudentLeistungCount +
				");");
		
		if(bReturn){
			m_lLeistungsID = lngLeistungIdNew;
			m_lStudentLeistungCount = lngNewLeistungNextCount;
		}
		return bReturn;
	}
	
	/**
	 * Neu am 28.2.2011: sonst schlägt das Ändern von Leistungen 
	 * fehl.
	 * @return true for success (or nothing to do), false for error (then, the LeistungId cannot be easily changed).
	 * @throws Exception when the basic db connection is broken.
	 */
	public boolean updateLeistungIdTo(long lngLeistungIdNew, long lModulID) throws Exception{
		if(lngLeistungIdNew==this.getLeistungsID()) return true; // (nothing to do)
		// Since this is not SQL-Exception-safe for the Matrikelnummer:
		Long.parseLong(this.m_sMatrikelnummer);
		
		long lngNewLeistungNextCount = 0;
		try{
                    PreparedStatement pstm = this.prepareStatement("select \"lngStudentLeistungCount\" "
                            + "from \"tblBdStudentXLeistung\" where "
                            + "\"lngSdSeminarID\"=? AND "  +
                            "\"strMatrikelnummer\"=? AND "  +
                            "\"lngLeistungsID\"=?");
                    
                    pstm.setLong(1, m_lSdSeminarID);
                    pstm.setString(2, this.m_sMatrikelnummer);
                    pstm.setLong(3, lngLeistungIdNew);
                    ResultSet rN = pstm.executeQuery();
                    rN.next();
                    lngNewLeistungNextCount = rN.getLong("lngStudentLeistungCount")+1;
		}catch(Exception eNoLeistungOfThatIdFound){}		
		
		boolean bReturn = sqlExe("update \"tblBdStudentXLeistung\" set " +
				"\"lngLeistungsID\"=" + lngLeistungIdNew + ", " +
				"\"lngStudentLeistungCount\"=" + lngNewLeistungNextCount + ", " +
				"\"lngModulID\"=" + lModulID + " where (" + 
				"\"lngSdSeminarID\"=" + m_lSdSeminarID + " AND " + 
				"\"strMatrikelnummer\"='" + m_sMatrikelnummer + "' AND " + 
				"\"lngLeistungsID\"=" + m_lLeistungsID + " AND " + 
				"\"lngStudentLeistungCount\"=" + m_lStudentLeistungCount +
				");");
		
		if(bReturn){
			m_lLeistungsID = lngLeistungIdNew;
			m_lStudentLeistungCount = lngNewLeistungNextCount;
			m_lModulID=lModulID;
		}
		return bReturn;
	}
	
	/**
	 * 
	 * Öffentliche Methode, um das aktuelle Objekt als 
	 * neue Zeile in die Datenbank zu schreiben (insert into...).
	 * Diese Methode enthält keinen ID-Broker, m.a.W.
	 * muss anderweitig (durch SERIAL Ids oder per Programm) 
         * Sorge dafür getragen werden, dass das Objekt keine 
	 * bestehenden Indices verletzt.
         * 
         * ACHTUNG HACK @todo:
         * Mitteilung per E-Mail für OpenSource Variante von UP
         * abgeschaltet.
	 * 
	 * @return true für Erfolg (oder falls das Objekt nicht 'dirty' wahr, {@link #isDirty()}).
	 * @throws Exception 
	 **/
	public boolean add() throws Exception{
		if( !(isDirty()) ) return true;
		boolean bReturn = false;
		
		// Lade Statement mit SQL
		PreparedStatement pstm = prepareStatement(toDBAddString());
		
		// Lade Objekteigenschaften in Statement
		// und übermittle das an die Datenbank
		pokeStatement(pstm);
//		try {
			pstm.execute();
			bReturn=true;
//			tellStudentAboutNewCredit();
			
			// Medizinische Fakultät Heidelberg: Mitteilung 
			// Über mehrfach nicht bestandene Leistung
			// muss an verschiedene Personen gemailt werden:
//			if((getStudentLeistungCount()>=3)&&(getSdSeminarID()==4)){
//				if(!(new Note(this.getSdSeminarID(), this.getNoteID())).getNoteBestanden()) new LeistungEmail(this).sendInfoMail();
//			}
//		} catch (SQLException e) {}
		this.m_bIsDirty = !(bReturn);
		return bReturn;
	}

	/**
	 * experimental, in version 6-20 (February 2008).
	 * 
	 * Send an email to this student, telling him about
	 * the credit.
	 * 
	 * Checks if blnStudentPublishEmail is set to true; 
	 *
         * @deprecated Sorry, im Moment nicht in der UP OS Variante.
	 * @param sMatrikelnummer
	 */
//	public void tellStudentAboutNewCredit(){
//		if(getStudentLeistungKlausuranmeldung()) return;
//		try {
//			if(dbCount("lngStudentPID", 
//					"tblBdStudent", 
//					"\"strMatrikelnummer\"='" + getMatrikelnummer() + "' and \"blnStudentPublishEmail\"='t'") <= 0) return;
//			Student student = new Student(getMatrikelnummer());
//			
//			// does this student _want_ to be told?
//			if(!student.getStudentPublishEmail()) return;
//			
//			// is there a plausible email address?
//			if(student.getStudentEmail()==null) return;
//			if(student.getStudentEmail().indexOf('@') <=0) return;
//			
//			// or in more modern java environments:
//			//if(!student.getStudentEmail().contains("@")) return;
//			String sInfoMed = "";
//			if(getSdSeminarID()==4) sInfoMed="Jeder Prüfling kann gegen das Ergebnis seiner " +
//					"Prüfung innerhalb eines Monats schriftlich Widerspruch beim " +
//					"Modulverantwortlichen oder beim Studiendekan einlegen.\n\n";
//			
//			// well, then: tell student:
//			SendMail mail = new SendMail("extmail.urz.uni-heidelberg.de");
//			String sText = "Sehr geehrte" +
//								(student.getStudentFemale() ? " Frau " : "r Herr ") + 
//								student.getStudentNachname() + ",\n\n" + 
//							"in SignUp [1] wurde ein neuer Leistungsnachweis für Sie " + 
//							"gespeichert.\n\n" + 
//							"Leistung: " + lookUp("strLeistungBezeichnung", "tblSdLeistung", 
//									"\"lngSdSeminarID\"=" + this.getSdSeminarID() + 
//									" and \"lngLeistungID\"=" + this.getLeistungsID()) + "\n" + 
//							"Note: " + new Note(this.getSdSeminarID(), this.getNoteID()).getNoteNameDE() + 
//							"\n\n" + sInfoMed + 
//							"(Dies ist eine automatisch erzeugte Email; bitte antworten Sie nicht " + 
//							"darauf. Wenn Sie keine weiteren Emails über Leistungen erhalten möchten, " +
//							"wählen Sie diese Option in SignUp unter \"Daten\" ab. Weitere " + 
//							"Informationen finden Sie in der Online-Hilfe unter \"Infomail senden\").\n\n" + 
//							"[1] http://signup.uni-hd.de"; 
//			System.out.println(sText);
//			mail.sendMail(student.getStudentVorname() + " " + student.getStudentNachname(), 
//					student.getStudentEmail(),
//					"", "info@shj-online.de", "SignUp Uni Heidelberg, automatische Benachrichtigung", sText, null);
//			
//		} catch (NumberFormatException e) {
//			e.printStackTrace(System.out);
//		} catch (Exception e) {
//			e.printStackTrace(System.out);
//		}
//		
//	}

	
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
		pokeWhere(61,pstm);
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
	public StudentXLeistung(){}	
	
	/**
	 * Konstruktor mit Indexwerten (eindeutig) der assoziierten Datenbanktabelle
	 * @throws NamingException 
	 * @throws SQLException 
	 **/
	public StudentXLeistung(long lngSdSeminarID, String strMatrikelnummer, long lngLeistungsID, long lngStudentLeistungCount) throws SQLException, NamingException{
		this.init(lngSdSeminarID, strMatrikelnummer, lngLeistungsID, lngStudentLeistungCount);
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
	public StudentXLeistung(ResultSet rst) throws SQLException{
		this.initByRst(rst);
		this.m_bIsDirty = false;
	}

  }//Klassenende
