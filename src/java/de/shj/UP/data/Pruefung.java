
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
import javax.naming.NamingException;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import org.w3c.dom.Node; 

/**
 *  Module: Exam
Exam (German "Prüfung").
 *  
 *  ALLGEMEINE INFORMATION ZU DEN OBJEKTEN DIESES PACKAGES:
 *  Diese Klasse kapselt die Tabelle 'Pruefung' in 
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
public class Pruefung extends shjCore{

	private static final long serialVersionUID = 7L;

////////////////////////////////////////////////////////////////
// 1.   P R I V A T E   D E K L A R A T I O N E N
////////////////////////////////////////////////////////////////
	
	private boolean m_bIsDirty = false;

	private long m_lSdSeminarID;
	private long m_lPruefungID;
	private String m_sPruefungBezeichnung;
	private String m_sPruefungBeschreibung;
	private String m_sPruefungFach;
	private String m_sPruefungAbschluss;
	private String m_sPruefungsordnung;
	private boolean m_bPruefungHauptfach;
	private String m_sPruefungZUVAmt;
	private String m_sPruefungZUVFach;
	private String m_sPruefungZUVTyp;
	private float m_sPruefungMinCreditPts;
	private boolean m_bPruefungECTSGewicht;
	private String m_sPruefungBezeichnung_en;
	private String m_sPruefungBeschreibung_en;
	private String m_sPruefungFach_en;
	private String m_sPruefungAbschluss_en;
	private String m_sPruefungCustom1;
	private String m_sPruefungCustom2;
	private String m_sPruefungCustom3;
	private boolean m_bPruefungTriggered;

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
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefung.lngSdSeminarID</Code>
	 **/
	public long getSdSeminarID(){
		return this.m_lSdSeminarID;
	}

	/**
	 * Seminar
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefung.lngSdSeminarID</Code>
	 **/	
	public void setSdSeminarID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lSdSeminarID));
		this.m_lSdSeminarID=value;
	}
	

	/**
	 * Id of exam.
	 * @return Id of exam.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefung.lngPruefungID</Code>
	 **/
	public long getPruefungID(){
		return this.m_lPruefungID;
	}

	/**
	 * Id of exam.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefung.lngPruefungID</Code>
	 **/	
	public void setPruefungID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lPruefungID));
		this.m_lPruefungID=value;
	}
	

	/**
	 * Name of this exam.
	 * @return Name of this exam.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefung.strPruefungBezeichnung</Code>
	 **/
	public String getPruefungBezeichnung(){
		return this.m_sPruefungBezeichnung;
	}

	/**
	 * Name of this exam.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefung.strPruefungBezeichnung</Code>
	 **/	
	public void setPruefungBezeichnung(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sPruefungBezeichnung)));
		this.m_sPruefungBezeichnung=value;
	}
	

	/**
	 * Description of this exam.
	 * @return Description of this exam.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefung.strPruefungBeschreibung</Code>
	 **/
	public String getPruefungBeschreibung(){
		return this.m_sPruefungBeschreibung;
	}

	/**
	 * Description of this exam.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefung.strPruefungBeschreibung</Code>
	 **/	
	public void setPruefungBeschreibung(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sPruefungBeschreibung)));
		this.m_sPruefungBeschreibung=value;
	}
	

	/**
	 * Subject.
	 * @return Subject.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefung.strPruefungFach</Code>
	 **/
	public String getPruefungFach(){
		return this.m_sPruefungFach;
	}

	/**
	 * Subject.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefung.strPruefungFach</Code>
	 **/	
	public void setPruefungFach(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sPruefungFach)));
		this.m_sPruefungFach=value;
	}
	

	/**
	 * Goal.
	 * @return Goal.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefung.strPruefungAbschluss</Code>
	 **/
	public String getPruefungAbschluss(){
		return this.m_sPruefungAbschluss;
	}

	/**
	 * Goal.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefung.strPruefungAbschluss</Code>
	 **/	
	public void setPruefungAbschluss(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sPruefungAbschluss)));
		this.m_sPruefungAbschluss=value;
	}
	

	/**
	 * Legal document describing this exam.
	 * @return Legal document describing this exam.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefung.strPruefungsordnung</Code>
	 **/
	public String getPruefungsordnung(){
		return this.m_sPruefungsordnung;
	}

	/**
	 * Legal document describing this exam.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefung.strPruefungsordnung</Code>
	 **/	
	public void setPruefungsordnung(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sPruefungsordnung)));
		this.m_sPruefungsordnung=value;
	}
	

	/**
	 * Is this a major?
	 * @return Is this a major?
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefung.blnPruefungHauptfach</Code>
	 **/
	public boolean getPruefungHauptfach(){
		return this.m_bPruefungHauptfach;
	}

	/**
	 * Is this a major?
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefung.blnPruefungHauptfach</Code>
	 **/	
	public void setPruefungHauptfach(boolean value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_bPruefungHauptfach));
		this.m_bPruefungHauptfach=value;
	}
	

	/**
	 * Internal interface value (Heidelberg).
	 * @return Internal interface value (Heidelberg).
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefung.strPruefungZUVAmt</Code>
	 **/
	public String getPruefungZUVAmt(){
		return this.m_sPruefungZUVAmt;
	}

	/**
	 * Internal interface value (Heidelberg).
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefung.strPruefungZUVAmt</Code>
	 **/	
	public void setPruefungZUVAmt(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sPruefungZUVAmt)));
		this.m_sPruefungZUVAmt=value;
	}
	

	/**
	 * Internal interface value (Heidelberg).
	 * @return Internal interface value (Heidelberg).
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefung.strPruefungZUVFach</Code>
	 **/
	public String getPruefungZUVFach(){
		return this.m_sPruefungZUVFach;
	}

	/**
	 * Internal interface value (Heidelberg).
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefung.strPruefungZUVFach</Code>
	 **/	
	public void setPruefungZUVFach(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sPruefungZUVFach)));
		this.m_sPruefungZUVFach=value;
	}
	

	/**
	 * Internal interface value (Heidelberg).
	 * @return Internal interface value (Heidelberg).
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefung.strPruefungZUVTyp</Code>
	 **/
	public String getPruefungZUVTyp(){
		return this.m_sPruefungZUVTyp;
	}

	/**
	 * Internal interface value (Heidelberg).
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefung.strPruefungZUVTyp</Code>
	 **/	
	public void setPruefungZUVTyp(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sPruefungZUVTyp)));
		this.m_sPruefungZUVTyp=value;
	}
	

	/**
	 * Minimum number of credit points needed for this exam.
	 * @return Minimum number of credit points needed for this exam.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefung.sngPruefungMinCreditPts</Code>
	 **/
	public float getPruefungMinCreditPts(){
		return this.m_sPruefungMinCreditPts;
	}

	/**
	 * Minimum number of credit points needed for this exam.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefung.sngPruefungMinCreditPts</Code>
	 **/	
	public void setPruefungMinCreditPts(float value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_sPruefungMinCreditPts));
		this.m_sPruefungMinCreditPts=value;
	}
	

	/**
	 * Weight of this exam in ECTS-calculation
	 * @return Weight of this exam in ECTS-calculation
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefung.blnPruefungECTSGewicht</Code>
	 **/
	public boolean getPruefungECTSGewicht(){
		return this.m_bPruefungECTSGewicht;
	}

	/**
	 * Weight of this exam in ECTS-calculation
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefung.blnPruefungECTSGewicht</Code>
	 **/	
	public void setPruefungECTSGewicht(boolean value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_bPruefungECTSGewicht));
		this.m_bPruefungECTSGewicht=value;
	}
	

	/**
	 * English name of exam.
	 * @return English name of exam.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefung.strPruefungBezeichnung_en</Code>
	 **/
	public String getPruefungBezeichnung_en(){
		return this.m_sPruefungBezeichnung_en;
	}

	/**
	 * English name of exam.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefung.strPruefungBezeichnung_en</Code>
	 **/	
	public void setPruefungBezeichnung_en(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sPruefungBezeichnung_en)));
		this.m_sPruefungBezeichnung_en=value;
	}
	

	/**
	 * English description of exam.
	 * @return English description of exam.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefung.strPruefungBeschreibung_en</Code>
	 **/
	public String getPruefungBeschreibung_en(){
		return this.m_sPruefungBeschreibung_en;
	}

	/**
	 * English description of exam.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefung.strPruefungBeschreibung_en</Code>
	 **/	
	public void setPruefungBeschreibung_en(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sPruefungBeschreibung_en)));
		this.m_sPruefungBeschreibung_en=value;
	}
	

	/**
	 * English name of subject.
	 * @return English name of subject.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefung.strPruefungFach_en</Code>
	 **/
	public String getPruefungFach_en(){
		return this.m_sPruefungFach_en;
	}

	/**
	 * English name of subject.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefung.strPruefungFach_en</Code>
	 **/	
	public void setPruefungFach_en(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sPruefungFach_en)));
		this.m_sPruefungFach_en=value;
	}
	

	/**
	 * English name of goal.
	 * @return English name of goal.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefung.strPruefungAbschluss_en</Code>
	 **/
	public String getPruefungAbschluss_en(){
		return this.m_sPruefungAbschluss_en;
	}

	/**
	 * English name of goal.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefung.strPruefungAbschluss_en</Code>
	 **/	
	public void setPruefungAbschluss_en(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sPruefungAbschluss_en)));
		this.m_sPruefungAbschluss_en=value;
	}
	

	/**
	 * Custom
	 * @return Custom
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefung.strPruefungCustom1</Code>
	 **/
	public String getPruefungCustom1(){
		return this.m_sPruefungCustom1;
	}

	/**
	 * Custom
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefung.strPruefungCustom1</Code>
	 **/	
	public void setPruefungCustom1(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sPruefungCustom1)));
		this.m_sPruefungCustom1=value;
	}
	

	/**
	 * Custom
	 * @return Custom
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefung.strPruefungCustom2</Code>
	 **/
	public String getPruefungCustom2(){
		return this.m_sPruefungCustom2;
	}

	/**
	 * Custom
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefung.strPruefungCustom2</Code>
	 **/	
	public void setPruefungCustom2(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sPruefungCustom2)));
		this.m_sPruefungCustom2=value;
	}
	

	/**
	 * Custom
	 * @return Custom
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefung.strPruefungCustom3</Code>
	 **/
	public String getPruefungCustom3(){
		return this.m_sPruefungCustom3;
	}

	/**
	 * Custom
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefung.strPruefungCustom3</Code>
	 **/	
	public void setPruefungCustom3(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sPruefungCustom3)));
		this.m_sPruefungCustom3=value;
	}
	

	/**
	 * Wird diese Prüfung automatisch eingetragen, wenn alle Leistungen vorliegen?
	 * @return Wird diese Prüfung automatisch eingetragen, wenn alle Leistungen vorliegen?
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefung.blnPruefungTriggered</Code>
	 **/
	public boolean getPruefungTriggered(){
		return this.m_bPruefungTriggered;
	}

	/**
	 * Wird diese Prüfung automatisch eingetragen, wenn alle Leistungen vorliegen?
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefung.blnPruefungTriggered</Code>
	 **/	
	public void setPruefungTriggered(boolean value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_bPruefungTriggered));
		this.m_bPruefungTriggered=value;
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
			"<PruefungID>" + m_lPruefungID + "</PruefungID>"  + 
			"<PruefungBezeichnung>" + m_sPruefungBezeichnung + "</PruefungBezeichnung>"  + 
			"<PruefungBeschreibung>" + m_sPruefungBeschreibung + "</PruefungBeschreibung>"  + 
			"<PruefungFach>" + m_sPruefungFach + "</PruefungFach>"  + 
			"<PruefungAbschluss>" + m_sPruefungAbschluss + "</PruefungAbschluss>"  + 
			"<Pruefungsordnung>" + m_sPruefungsordnung + "</Pruefungsordnung>"  + 
			"<PruefungHauptfach>" + m_bPruefungHauptfach + "</PruefungHauptfach>"  + 
			"<PruefungZUVAmt>" + m_sPruefungZUVAmt + "</PruefungZUVAmt>"  + 
			"<PruefungZUVFach>" + m_sPruefungZUVFach + "</PruefungZUVFach>"  + 
			"<PruefungZUVTyp>" + m_sPruefungZUVTyp + "</PruefungZUVTyp>"  + 
			"<PruefungMinCreditPts>" + m_sPruefungMinCreditPts + "</PruefungMinCreditPts>"  + 
			"<PruefungECTSGewicht>" + m_bPruefungECTSGewicht + "</PruefungECTSGewicht>"  + 
			"<PruefungBezeichnung_en>" + m_sPruefungBezeichnung_en + "</PruefungBezeichnung_en>"  + 
			"<PruefungBeschreibung_en>" + m_sPruefungBeschreibung_en + "</PruefungBeschreibung_en>"  + 
			"<PruefungFach_en>" + m_sPruefungFach_en + "</PruefungFach_en>"  + 
			"<PruefungAbschluss_en>" + m_sPruefungAbschluss_en + "</PruefungAbschluss_en>"  + 
			"<PruefungCustom1>" + m_sPruefungCustom1 + "</PruefungCustom1>"  + 
			"<PruefungCustom2>" + m_sPruefungCustom2 + "</PruefungCustom2>"  + 
			"<PruefungCustom3>" + m_sPruefungCustom3 + "</PruefungCustom3>"  + 
			"<PruefungTriggered>" + m_bPruefungTriggered + "</PruefungTriggered>" ;
	}

////////////////////////////////////////////////////////////////
// 4.   S Q L  U T I L I T I E S
////////////////////////////////////////////////////////////////


	/**
	 * Where-SQL für eindeutigen Index dieses Objekte in der Datenbank (in Tabelle 'tblSdPruefung.').
	 * @return SQL-String wie z.B. "Id1=? and Id2=?"
	 **/
	private String getSQLWhereClause(){
		return 
			"\"lngSdSeminarID\"=? AND " + 
			"\"lngPruefungID\"=?";
	}

	/**
	 * SQL-Befehl zum Löschen des Objekts in der Datenbank (aus Tabelle 'tblSdPruefung.')
	 * @return SQL-String zum Löschen des Objekts, z.B. "delete from T1 where (Id1=?);"
	 **/
	private String toDBDeleteString(){
		return "delete from \"tblSdPruefung\" where ( " + this.getSQLWhereClause() + ");";
	}

	/**
 	 * Setzt die Werte für alle Platzhalter in {@link #getSQLWhereClause()}.
	 * @param ii Offset für die Parameter (inklusive).
	 * @throws SQLException 
	 **/
	private void pokeWhere(int ii, PreparedStatement ps) throws SQLException{
		
		ps.setLong(ii++, m_lSdSeminarID);
		ps.setLong(ii++, m_lPruefungID);
	}

	/**
  	 * Füllt das SQL-Statement mit den aktuellen Werten des Objekts
	 * @throws SQLException
	 **/
	private void pokeStatement(PreparedStatement ps) throws SQLException{
		
		ps.setLong(1, m_lSdSeminarID);
		ps.setLong(2, m_lPruefungID);
		ps.setString(3, m_sPruefungBezeichnung);
		ps.setString(4, m_sPruefungBeschreibung);
		ps.setString(5, m_sPruefungFach);
		ps.setString(6, m_sPruefungAbschluss);
		ps.setString(7, m_sPruefungsordnung);
		ps.setBoolean(8, m_bPruefungHauptfach);
		ps.setString(9, m_sPruefungZUVAmt);
		ps.setString(10, m_sPruefungZUVFach);
		ps.setString(11, m_sPruefungZUVTyp);
		ps.setFloat(12, m_sPruefungMinCreditPts);
		ps.setBoolean(13, m_bPruefungECTSGewicht);
		ps.setString(14, m_sPruefungBezeichnung_en);
		ps.setString(15, m_sPruefungBeschreibung_en);
		ps.setString(16, m_sPruefungFach_en);
		ps.setString(17, m_sPruefungAbschluss_en);
		ps.setString(18, m_sPruefungCustom1);
		ps.setString(19, m_sPruefungCustom2);
		ps.setString(20, m_sPruefungCustom3);
		ps.setBoolean(21, m_bPruefungTriggered);

	}

	/**
	 * SQL-Befehl zum Speichern der Objekteigenschaften in die Datenbank (Tabelle 'tblSdPruefung.')
	 * @return PreparedStatemtent, das nach Aufruf von {@link #pokeStatement(PreparedStatement)} und {@link #execute()} das Update ausführt.
	 **/
	private String toDBUpdateString(){
		return "update \"tblSdPruefung\" set " +
			"\"lngSdSeminarID\"=?, " +
			"\"lngPruefungID\"=?, " +
			"\"strPruefungBezeichnung\"=?, " +
			"\"strPruefungBeschreibung\"=?, " +
			"\"strPruefungFach\"=?, " +
			"\"strPruefungAbschluss\"=?, " +
			"\"strPruefungsordnung\"=?, " +
			"\"blnPruefungHauptfach\"=?, " +
			"\"strPruefungZUVAmt\"=?, " +
			"\"strPruefungZUVFach\"=?, " +
			"\"strPruefungZUVTyp\"=?, " +
			"\"sngPruefungMinCreditPts\"=?, " +
			"\"blnPruefungECTSGewicht\"=?, " +
			"\"strPruefungBezeichnung_en\"=?, " +
			"\"strPruefungBeschreibung_en\"=?, " +
			"\"strPruefungFach_en\"=?, " +
			"\"strPruefungAbschluss_en\"=?, " +
			"\"strPruefungCustom1\"=?, " +
			"\"strPruefungCustom2\"=?, " +
			"\"strPruefungCustom3\"=?, " +
			"\"blnPruefungTriggered\"=?" +
			" where (" + this.getSQLWhereClause() + ");";
	}

	/**
	 * SQL-Befehl zum Hinzufügen des Objekts in die Datenbank (Tabelle 'tblSdPruefung.')
	 * @return SQL-String "insert into...".
	 **/
	private String toDBAddString(){
		return "insert into \"tblSdPruefung\" ( " +
			"\"lngSdSeminarID\", \"lngPruefungID\", \"strPruefungBezeichnung\", \"strPruefungBeschreibung\", \"strPruefungFach\", \"strPruefungAbschluss\", \"strPruefungsordnung\", \"blnPruefungHauptfach\", \"strPruefungZUVAmt\", \"strPruefungZUVFach\", \"strPruefungZUVTyp\", \"sngPruefungMinCreditPts\", \"blnPruefungECTSGewicht\", \"strPruefungBezeichnung_en\", \"strPruefungBeschreibung_en\", \"strPruefungFach_en\", \"strPruefungAbschluss_en\", \"strPruefungCustom1\", \"strPruefungCustom2\", \"strPruefungCustom3\", \"blnPruefungTriggered\" ) VALUES ( ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
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
	private void init(long lngSdSeminarID, long lngPruefungID) throws SQLException, NamingException{

		this.m_lSdSeminarID=lngSdSeminarID;

		this.m_lPruefungID=lngPruefungID;

		
		PreparedStatement pstm = prepareStatement("select * from \"tblSdPruefung\" where (" + this.getSQLWhereClause() + ");");
		pokeWhere(1,pstm);

		ResultSet rst=pstm.executeQuery();
		if(rst.next()) this.initByRst(rst);
		rst.close();
		this.disconnect();
		rst=null;
	}	
	
	/**
	 * Lade die Objekteigenschaften aus einem ResultSet.
	 * param rst ResultSet mit allen Spalten der Tabelle 'tblSdPruefung'
	 * @throws SQLException 
	 **/
	protected void initByRst(ResultSet rst) throws SQLException{
		this.m_lSdSeminarID=rst.getLong("lngSdSeminarID");
		this.m_lPruefungID=rst.getLong("lngPruefungID");
		this.m_sPruefungBezeichnung=rst.getString("strPruefungBezeichnung");
		this.m_sPruefungBeschreibung=rst.getString("strPruefungBeschreibung");
		this.m_sPruefungFach=rst.getString("strPruefungFach");
		this.m_sPruefungAbschluss=rst.getString("strPruefungAbschluss");
		this.m_sPruefungsordnung=rst.getString("strPruefungsordnung");
		this.m_bPruefungHauptfach=rst.getBoolean("blnPruefungHauptfach");
		this.m_sPruefungZUVAmt=rst.getString("strPruefungZUVAmt");
		this.m_sPruefungZUVFach=rst.getString("strPruefungZUVFach");
		this.m_sPruefungZUVTyp=rst.getString("strPruefungZUVTyp");
		this.m_sPruefungMinCreditPts=rst.getFloat("sngPruefungMinCreditPts");
		this.m_bPruefungECTSGewicht=rst.getBoolean("blnPruefungECTSGewicht");
		this.m_sPruefungBezeichnung_en=rst.getString("strPruefungBezeichnung_en");
		this.m_sPruefungBeschreibung_en=rst.getString("strPruefungBeschreibung_en");
		this.m_sPruefungFach_en=rst.getString("strPruefungFach_en");
		this.m_sPruefungAbschluss_en=rst.getString("strPruefungAbschluss_en");
		this.m_sPruefungCustom1=rst.getString("strPruefungCustom1");
		this.m_sPruefungCustom2=rst.getString("strPruefungCustom2");
		this.m_sPruefungCustom3=rst.getString("strPruefungCustom3");
		this.m_bPruefungTriggered=rst.getBoolean("blnPruefungTriggered");	
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
		pokeWhere(22,pstm);
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
	public Pruefung(){}	
	
	/**
	 * Konstruktor mit Indexwerten (eindeutig) der assoziierten Datenbanktabelle
	 * @throws NamingException 
	 * @throws SQLException 
	 **/
	public Pruefung(long lngSdSeminarID, long lngPruefungID) throws SQLException, NamingException{
		this.init(lngSdSeminarID, lngPruefungID);
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
	public Pruefung(ResultSet rst) throws SQLException{
		this.initByRst(rst);
		this.m_bIsDirty = false;
	}
  }//Klassenende
