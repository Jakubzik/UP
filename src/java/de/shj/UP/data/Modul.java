
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

import de.shj.UP.util.ResultSetSHJ;

/**
 *  Module: Exam
In Bachelor- and Master-Lingo, a module, or group of credits.
 *  
 *  ALLGEMEINE INFORMATION ZU DEN OBJEKTEN DIESES PACKAGES:
 *  Diese Klasse kapselt die Tabelle 'Modul' in 
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
public class Modul extends shjCore{

	private static final long serialVersionUID = 7L;

////////////////////////////////////////////////////////////////
// 1.   P R I V A T E   D E K L A R A T I O N E N
////////////////////////////////////////////////////////////////
	
	private boolean m_bIsDirty = false;

	private long m_lSdSeminarID;
	private long m_lModulID;
	private String m_sModulBezeichnung;
	private String m_sModulBeschreibung;
	private long m_lModulNummer;
	private String m_sModulEinordnung;
	private int m_iModulSemester;
	private int m_iModulSemesterMin;
	private int m_iModulSemesterMax;
	private String m_sModulCustom1;
	private String m_sModulCustom2;
	private String m_sModulCustom3;
	private String m_sModulBezeichnung_en;
	private String m_sModulBeschreibung_en;
	private String m_sModulCustom1_en;
	private String m_sModulCustom2_en;
	private String m_sModulCustom3_en;
	private boolean m_bModulWaehlbar;
        private boolean m_bModulVarLP;

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
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdModul.lngSdSeminarID</Code>
	 **/
	public long getSdSeminarID(){
		return this.m_lSdSeminarID;
	}

	/**
	 * Seminar
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdModul.lngSdSeminarID</Code>
	 **/	
	public void setSdSeminarID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lSdSeminarID));
		this.m_lSdSeminarID=value;
	}
	

	/**
	 * Id of module.
	 * @return Id of module.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdModul.lngModulID</Code>
	 **/
	public long getModulID(){
		return this.m_lModulID;
	}

	/**
	 * Id of module.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdModul.lngModulID</Code>
	 **/	
	public void setModulID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lModulID));
		this.m_lModulID=value;
	}
	

	/**
	 * Name of module.
	 * @return Name of module.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdModul.strModulBezeichnung</Code>
	 **/
	public String getModulBezeichnung(){
		return this.m_sModulBezeichnung;
	}

	/**
	 * Name of module.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdModul.strModulBezeichnung</Code>
	 **/	
	public void setModulBezeichnung(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sModulBezeichnung)));
		this.m_sModulBezeichnung=value;
	}
	

	/**
	 * Description of module.
	 * @return Description of module.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdModul.strModulBeschreibung</Code>
	 **/
	public String getModulBeschreibung(){
		return this.m_sModulBeschreibung;
	}

	/**
	 * Description of module.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdModul.strModulBeschreibung</Code>
	 **/	
	public void setModulBeschreibung(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sModulBeschreibung)));
		this.m_sModulBeschreibung=value;
	}
	

	/**
	 * Number of module.
	 * @return Number of module.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdModul.lngModulNummer</Code>
	 **/
	public long getModulNummer(){
		return this.m_lModulNummer;
	}

	/**
	 * Number of module.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdModul.lngModulNummer</Code>
	 **/	
	public void setModulNummer(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lModulNummer));
		this.m_lModulNummer=value;
	}
	

	/**
	 * Categorization of module.
	 * @return Categorization of module.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdModul.strModulEinordnung</Code>
	 **/
	public String getModulEinordnung(){
		return this.m_sModulEinordnung;
	}

	/**
	 * Categorization of module.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdModul.strModulEinordnung</Code>
	 **/	
	public void setModulEinordnung(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sModulEinordnung)));
		this.m_sModulEinordnung=value;
	}
	

	/**
	 * Semester when  module should be completed.
	 * @return Semester when  module should be completed.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdModul.intModulSemester</Code>
	 **/
	public int getModulSemester(){
		return this.m_iModulSemester;
	}

	/**
	 * Semester when  module should be completed.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdModul.intModulSemester</Code>
	 **/	
	public void setModulSemester(int value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_iModulSemester));
		this.m_iModulSemester=value;
	}
	

	/**
	 * Minumum semester of module.
	 * @return Minumum semester of module.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdModul.intModulSemesterMin</Code>
	 **/
	public int getModulSemesterMin(){
		return this.m_iModulSemesterMin;
	}

	/**
	 * Minumum semester of module.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdModul.intModulSemesterMin</Code>
	 **/	
	public void setModulSemesterMin(int value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_iModulSemesterMin));
		this.m_iModulSemesterMin=value;
	}
	

	/**
	 * Maximum semester of module.
	 * @return Maximum semester of module.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdModul.intModulSemesterMax</Code>
	 **/
	public int getModulSemesterMax(){
		return this.m_iModulSemesterMax;
	}

	/**
	 * Maximum semester of module.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdModul.intModulSemesterMax</Code>
	 **/	
	public void setModulSemesterMax(int value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_iModulSemesterMax));
		this.m_iModulSemesterMax=value;
	}
	

	/**
	 * Custom
	 * @return Custom
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdModul.strModulCustom1</Code>
	 **/
	public String getModulCustom1(){
		return this.m_sModulCustom1;
	}

	/**
	 * Custom
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdModul.strModulCustom1</Code>
	 **/	
	public void setModulCustom1(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sModulCustom1)));
		this.m_sModulCustom1=value;
	}
	

	/**
	 * Custom
	 * @return Custom
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdModul.strModulCustom2</Code>
	 **/
	public String getModulCustom2(){
		return this.m_sModulCustom2;
	}

	/**
	 * Custom
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdModul.strModulCustom2</Code>
	 **/	
	public void setModulCustom2(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sModulCustom2)));
		this.m_sModulCustom2=value;
	}
	

	/**
	 * Custom
	 * @return Custom
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdModul.strModulCustom3</Code>
	 **/
	public String getModulCustom3(){
		return this.m_sModulCustom3;
	}

	/**
	 * Custom
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdModul.strModulCustom3</Code>
	 **/	
	public void setModulCustom3(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sModulCustom3)));
		this.m_sModulCustom3=value;
	}
	

	/**
	 * English name of module.
	 * @return English name of module.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdModul.strModulBezeichnung_en</Code>
	 **/
	public String getModulBezeichnung_en(){
		return this.m_sModulBezeichnung_en;
	}

	/**
	 * English name of module.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdModul.strModulBezeichnung_en</Code>
	 **/	
	public void setModulBezeichnung_en(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sModulBezeichnung_en)));
		this.m_sModulBezeichnung_en=value;
	}
	

	/**
	 * English description of module.
	 * @return English description of module.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdModul.strModulBeschreibung_en</Code>
	 **/
	public String getModulBeschreibung_en(){
		return this.m_sModulBeschreibung_en;
	}

	/**
	 * English description of module.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdModul.strModulBeschreibung_en</Code>
	 **/	
	public void setModulBeschreibung_en(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sModulBeschreibung_en)));
		this.m_sModulBeschreibung_en=value;
	}
	

	/**
	 * Custom English
	 * @return Custom English
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdModul.strModulCustom1_en</Code>
	 **/
	public String getModulCustom1_en(){
		return this.m_sModulCustom1_en;
	}

	/**
	 * Custom English
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdModul.strModulCustom1_en</Code>
	 **/	
	public void setModulCustom1_en(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sModulCustom1_en)));
		this.m_sModulCustom1_en=value;
	}
	

	/**
	 * Custom English
	 * @return Custom English
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdModul.strModulCustom2_en</Code>
	 **/
	public String getModulCustom2_en(){
		return this.m_sModulCustom2_en;
	}

	/**
	 * Custom English
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdModul.strModulCustom2_en</Code>
	 **/	
	public void setModulCustom2_en(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sModulCustom2_en)));
		this.m_sModulCustom2_en=value;
	}
	

	/**
	 * Custom English
	 * @return Custom English
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdModul.strModulCustom3_en</Code>
	 **/
	public String getModulCustom3_en(){
		return this.m_sModulCustom3_en;
	}

	/**
	 * Custom English
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdModul.strModulCustom3_en</Code>
	 **/	
	public void setModulCustom3_en(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sModulCustom3_en)));
		this.m_sModulCustom3_en=value;
	}
	

	/**
	 * Is this module an option for mapping a credit? Or is it only a helper module?
	 * @return Is this module an option for mapping a credit? Or is it only a helper module?
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdModul.blnModulWaehlbar</Code>
	 **/
	public boolean getModulWaehlbar(){
		return this.m_bModulWaehlbar;
	}

	/**
	 * Is this module an option for mapping a credit? Or is it only a helper module?
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdModul.blnModulWaehlbar</Code>
	 **/	
	public void setModulWaehlbar(boolean value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_bModulWaehlbar));
		this.m_bModulWaehlbar=value;
	}

         /**
          * Falls 'true' werden die Leistungspunktezahlen 
          * der Leistungen in diesem Modul nicht automatisch
          * beim Wechsel des Studienfachs angepasst.
          * 
          * (Z.B. in Modulen ÜK werden jeweils individuelle
          * LP Zahlen vergeben; beim Umschreiben wird die 
          * Trigger Fkt map_modules in Pg aufgerufen, die 
          * versucht, die Modulzuordnung und LP Zahlen anzupassen. 
          * Die ÜK-Leistungen würden alle mit 1 LP bewertet, 
          * wenn die ÜK-Module nicht mit dem Flag "VarLP" 
          * gekennzeichnet wären.
	 **/
	public boolean getModulVarLP(){
		return this.m_bModulVarLP;
	}

	/**
* Falls 'true' werden die Leistungspunktezahlen 
          * der Leistungen in diesem Modul nicht automatisch
          * beim Wechsel des Studienfachs angepasst.
          * 
          * (Z.B. in Modulen ÜK werden jeweils individuelle
          * LP Zahlen vergeben; beim Umschreiben wird die 
          * Trigger Fkt map_modules in Pg aufgerufen, die 
          * versucht, die Modulzuordnung und LP Zahlen anzupassen. 
          * Die ÜK-Leistungen würden alle mit 1 LP bewertet, 
          * wenn die ÜK-Module nicht mit dem Flag "VarLP" 
          * gekennzeichnet wären.
	 **/	
	public void setModulVarLP(boolean value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_bModulVarLP));
		this.m_bModulVarLP=value;
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
			"<ModulID>" + m_lModulID + "</ModulID>"  + 
			"<ModulBezeichnung>" + m_sModulBezeichnung + "</ModulBezeichnung>"  + 
			"<ModulBeschreibung>" + m_sModulBeschreibung + "</ModulBeschreibung>"  + 
			"<ModulNummer>" + m_lModulNummer + "</ModulNummer>"  + 
			"<ModulEinordnung>" + m_sModulEinordnung + "</ModulEinordnung>"  + 
			"<ModulSemester>" + m_iModulSemester + "</ModulSemester>"  + 
			"<ModulSemesterMin>" + m_iModulSemesterMin + "</ModulSemesterMin>"  + 
			"<ModulSemesterMax>" + m_iModulSemesterMax + "</ModulSemesterMax>"  + 
			"<ModulCustom1>" + m_sModulCustom1 + "</ModulCustom1>"  + 
			"<ModulCustom2>" + m_sModulCustom2 + "</ModulCustom2>"  + 
			"<ModulCustom3>" + m_sModulCustom3 + "</ModulCustom3>"  + 
			"<ModulBezeichnung_en>" + m_sModulBezeichnung_en + "</ModulBezeichnung_en>"  + 
			"<ModulBeschreibung_en>" + m_sModulBeschreibung_en + "</ModulBeschreibung_en>"  + 
			"<ModulCustom1_en>" + m_sModulCustom1_en + "</ModulCustom1_en>"  + 
			"<ModulCustom2_en>" + m_sModulCustom2_en + "</ModulCustom2_en>"  + 
			"<ModulCustom3_en>" + m_sModulCustom3_en + "</ModulCustom3_en>"  + 
			"<ModulWaehlbar>" + m_bModulWaehlbar + "</ModulWaehlbar>" ;
	}

////////////////////////////////////////////////////////////////
// 4.   S Q L  U T I L I T I E S
////////////////////////////////////////////////////////////////


	/**
	 * Where-SQL für eindeutigen Index dieses Objekte in der Datenbank (in Tabelle 'tblSdModul.').
	 * @return SQL-String wie z.B. "Id1=? and Id2=?"
	 **/
	private String getSQLWhereClause(){
		return 
			"\"lngSdSeminarID\"=? AND " + 
			"\"lngModulID\"=?";
	}

	/**
	 * SQL-Befehl zum Löschen des Objekts in der Datenbank (aus Tabelle 'tblSdModul.')
	 * @return SQL-String zum Löschen des Objekts, z.B. "delete from T1 where (Id1=?);"
	 **/
	private String toDBDeleteString(){
		return "delete from \"tblSdModul\" where ( " + this.getSQLWhereClause() + ");";
	}

	/**
 	 * Setzt die Werte für alle Platzhalter in {@link #getSQLWhereClause()}.
	 * @param ii Offset für die Parameter (inklusive).
	 * @throws SQLException 
	 **/
	private void pokeWhere(int ii, PreparedStatement ps) throws SQLException{
		
		ps.setLong(ii++, m_lSdSeminarID);
		ps.setLong(ii++, m_lModulID);
	}

	/**
  	 * Füllt das SQL-Statement mit den aktuellen Werten des Objekts
	 * @throws SQLException
	 **/
	private void pokeStatement(PreparedStatement ps) throws SQLException{
		
		ps.setLong(1, m_lSdSeminarID);
		ps.setLong(2, m_lModulID);
		ps.setString(3, m_sModulBezeichnung);
		ps.setString(4, m_sModulBeschreibung);
		ps.setLong(5, m_lModulNummer);
		ps.setString(6, m_sModulEinordnung);
		ps.setInt(7, m_iModulSemester);
		ps.setInt(8, m_iModulSemesterMin);
		ps.setInt(9, m_iModulSemesterMax);
		ps.setString(10, m_sModulCustom1);
		ps.setString(11, m_sModulCustom2);
		ps.setString(12, m_sModulCustom3);
		ps.setString(13, m_sModulBezeichnung_en);
		ps.setString(14, m_sModulBeschreibung_en);
		ps.setString(15, m_sModulCustom1_en);
		ps.setString(16, m_sModulCustom2_en);
		ps.setString(17, m_sModulCustom3_en);
		ps.setBoolean(18, m_bModulWaehlbar);
                ps.setBoolean(19, m_bModulVarLP);

	}

	/**
	 * SQL-Befehl zum Speichern der Objekteigenschaften in die Datenbank (Tabelle 'tblSdModul.')
	 * @return PreparedStatemtent, das nach Aufruf von {@link #pokeStatement(PreparedStatement)} und {@link #execute()} das Update ausführt.
	 **/
	private String toDBUpdateString(){
		return "update \"tblSdModul\" set " +
			"\"lngSdSeminarID\"=?, " +
			"\"lngModulID\"=?, " +
			"\"strModulBezeichnung\"=?, " +
			"\"strModulBeschreibung\"=?, " +
			"\"lngModulNummer\"=?, " +
			"\"strModulEinordnung\"=?, " +
			"\"intModulSemester\"=?, " +
			"\"intModulSemesterMin\"=?, " +
			"\"intModulSemesterMax\"=?, " +
			"\"strModulCustom1\"=?, " +
			"\"strModulCustom2\"=?, " +
			"\"strModulCustom3\"=?, " +
			"\"strModulBezeichnung_en\"=?, " +
			"\"strModulBeschreibung_en\"=?, " +
			"\"strModulCustom1_en\"=?, " +
			"\"strModulCustom2_en\"=?, " +
			"\"strModulCustom3_en\"=?, " +
			"\"blnModulWaehlbar\"=?," +
                        "\"blnModulVarLP\"=?" +
			" where (" + this.getSQLWhereClause() + ");";
	}

	/**
	 * SQL-Befehl zum Hinzufügen des Objekts in die Datenbank (Tabelle 'tblSdModul.')
	 * @return SQL-String "insert into...".
	 **/
	private String toDBAddString(){
		return "insert into \"tblSdModul\" ( " +
			"\"lngSdSeminarID\", \"lngModulID\", \"strModulBezeichnung\", \"strModulBeschreibung\", \"lngModulNummer\", \"strModulEinordnung\", \"intModulSemester\", \"intModulSemesterMin\", \"intModulSemesterMax\", \"strModulCustom1\", \"strModulCustom2\", \"strModulCustom3\", \"strModulBezeichnung_en\", \"strModulBeschreibung_en\", \"strModulCustom1_en\", \"strModulCustom2_en\", \"strModulCustom3_en\", \"blnModulWaehlbar\", \"blnModulVarLP\" ) VALUES ( ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
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
	private void init(long lngSdSeminarID, long lngModulID) throws SQLException, NamingException{

		this.m_lSdSeminarID=lngSdSeminarID;

		this.m_lModulID=lngModulID;

		
		PreparedStatement pstm = prepareStatement("select * from \"tblSdModul\" where (" + this.getSQLWhereClause() + ");");
		pokeWhere(1,pstm);

		ResultSet rst=pstm.executeQuery();
		if(rst.next()) this.initByRst(rst);
		rst.close();
		this.disconnect();
		rst=null;
	}	
	
	/**
	 * Lade die Objekteigenschaften aus einem ResultSet.
	 * param rst ResultSet mit allen Spalten der Tabelle 'tblSdModul'
	 * @throws SQLException 
	 **/
	public void initByRst(ResultSet rst) throws SQLException{
		this.m_lSdSeminarID=rst.getLong("lngSdSeminarID");
		this.m_lModulID=rst.getLong("lngModulID");
		this.m_sModulBezeichnung=rst.getString("strModulBezeichnung");
		this.m_sModulBeschreibung=rst.getString("strModulBeschreibung");
		this.m_lModulNummer=rst.getLong("lngModulNummer");
		this.m_sModulEinordnung=rst.getString("strModulEinordnung");
		this.m_iModulSemester=rst.getInt("intModulSemester");
		this.m_iModulSemesterMin=rst.getInt("intModulSemesterMin");
		this.m_iModulSemesterMax=rst.getInt("intModulSemesterMax");
		this.m_sModulCustom1=rst.getString("strModulCustom1");
		this.m_sModulCustom2=rst.getString("strModulCustom2");
		this.m_sModulCustom3=rst.getString("strModulCustom3");
		this.m_sModulBezeichnung_en=rst.getString("strModulBezeichnung_en");
		this.m_sModulBeschreibung_en=rst.getString("strModulBeschreibung_en");
		this.m_sModulCustom1_en=rst.getString("strModulCustom1_en");
		this.m_sModulCustom2_en=rst.getString("strModulCustom2_en");
		this.m_sModulCustom3_en=rst.getString("strModulCustom3_en");
		this.m_bModulWaehlbar=rst.getBoolean("blnModulWaehlbar");	
                this.m_bModulVarLP=rst.getBoolean("blnModulVarLP");
	}	
	
	/**
	 * Lade die Objekteigenschaften aus einem ResultSet.
	 * param rst ResultSet mit allen Spalten der Tabelle 'tblSdModul'
	 * @throws SQLException 
	 **/
	public void initByRstSHJ(ResultSetSHJ rst) throws SQLException{
		this.m_lSdSeminarID=rst.getLong("lngSdSeminarID");
		this.m_lModulID=rst.getLong("lngModulID");
		this.m_sModulBezeichnung=rst.getString("strModulBezeichnung");
		this.m_sModulBeschreibung=rst.getString("strModulBeschreibung");
		
		this.m_sModulEinordnung=rst.getString("strModulEinordnung");
		try{
			this.m_lModulNummer=rst.getLong("lngModulNummer");
			this.m_iModulSemester=rst.getInt("intModulSemester");
			this.m_iModulSemesterMin=rst.getInt("intModulSemesterMin");
			this.m_iModulSemesterMax=rst.getInt("intModulSemesterMax");
		}catch(Exception eNull){//ignore
		}
		this.m_sModulCustom1=rst.getString("strModulCustom1");
		this.m_sModulCustom2=rst.getString("strModulCustom2");
		this.m_sModulCustom3=rst.getString("strModulCustom3");
		this.m_sModulBezeichnung_en=rst.getString("strModulBezeichnung_en");
		this.m_sModulBeschreibung_en=rst.getString("strModulBeschreibung_en");
		this.m_sModulCustom1_en=rst.getString("strModulCustom1_en");
		this.m_sModulCustom2_en=rst.getString("strModulCustom2_en");
		this.m_sModulCustom3_en=rst.getString("strModulCustom3_en");
		this.m_bModulWaehlbar=rst.getBoolean("blnModulWaehlbar");	
                this.m_bModulVarLP=rst.getBoolean("blnModulVarLP");
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
		} catch (SQLException e) {System.out.println(e.getMessage());}
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
		pokeWhere(20,pstm);
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
	public Modul(){}	
	
	/**
	 * Konstruktor mit Indexwerten (eindeutig) der assoziierten Datenbanktabelle
	 * @throws NamingException 
	 * @throws SQLException 
	 **/
	public Modul(long lngSdSeminarID, long lngModulID) throws SQLException, NamingException{
		this.init(lngSdSeminarID, lngModulID);
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
	public Modul(ResultSet rst) throws SQLException{
		this.initByRst(rst);
		this.m_bIsDirty = false;
	}
  }//Klassenende
