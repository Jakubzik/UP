
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
 *  In contrast to SdGruppen, this table groups students (not teachers). This has been introduced in version 6-06 because the Faculty of Medicine requested a feature to put cohorts into groups. This is also why a group is uniquely identified by a group-id with a seminar-id, but the mapping of students (in StudGruppeXStudent) takes additional information on the semester and the Studiensemester.
 *  
 *  ALLGEMEINE INFORMATION ZU DEN OBJEKTEN DIESES PACKAGES:
 *  Diese Klasse kapselt die Tabelle 'StudGruppe' in 
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
public class StudGruppe extends shjCore{

	private static final long serialVersionUID = 7L;

////////////////////////////////////////////////////////////////
// 1.   P R I V A T E   D E K L A R A T I O N E N
////////////////////////////////////////////////////////////////
	
	private boolean m_bIsDirty = false;

	private long m_lSeminarID;
	private long m_lStudGruppeID;
	private long m_lStudGruppeNummer;
	private String m_sStudGruppeBezeichnung;
	private String m_sStudGruppeBezeichnung_en;
	private String m_sStudGruppeBeschreibung;
	private String m_sStudGruppeBeschreibung_en;
	private String m_sStudGruppeCustom1;
	private String m_sStudGruppeCustom2;
	private String m_sStudGruppeCustom3;
	private boolean m_bStudGruppeOpen;

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
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdStudGruppe.lngSeminarID</Code>
	 **/
	public long getSeminarID(){
		return this.m_lSeminarID;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdStudGruppe.lngSeminarID</Code>
	 **/	
	public void setSeminarID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lSeminarID));
		this.m_lSeminarID=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdStudGruppe.lngStudGruppeID</Code>
	 **/
	public long getStudGruppeID(){
		return this.m_lStudGruppeID;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdStudGruppe.lngStudGruppeID</Code>
	 **/	
	public void setStudGruppeID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lStudGruppeID));
		this.m_lStudGruppeID=value;
	}
	

	/**
	 * Number of this group (descriptive, not a unique number)
	 * @return Number of this group (descriptive, not a unique number)
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdStudGruppe.lngStudGruppeNummer</Code>
	 **/
	public long getStudGruppeNummer(){
		return this.m_lStudGruppeNummer;
	}

	/**
	 * Number of this group (descriptive, not a unique number)
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdStudGruppe.lngStudGruppeNummer</Code>
	 **/	
	public void setStudGruppeNummer(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lStudGruppeNummer));
		this.m_lStudGruppeNummer=value;
	}
	

	/**
	 * Group name (local language).
	 * @return Group name (local language).
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdStudGruppe.strStudGruppeBezeichnung</Code>
	 **/
	public String getStudGruppeBezeichnung(){
		return this.m_sStudGruppeBezeichnung;
	}

	/**
	 * Group name (local language).
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdStudGruppe.strStudGruppeBezeichnung</Code>
	 **/	
	public void setStudGruppeBezeichnung(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sStudGruppeBezeichnung)));
		this.m_sStudGruppeBezeichnung=value;
	}
	

	/**
	 * Group name in English.
	 * @return Group name in English.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdStudGruppe.strStudGruppeBezeichnung_en</Code>
	 **/
	public String getStudGruppeBezeichnung_en(){
		return this.m_sStudGruppeBezeichnung_en;
	}

	/**
	 * Group name in English.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdStudGruppe.strStudGruppeBezeichnung_en</Code>
	 **/	
	public void setStudGruppeBezeichnung_en(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sStudGruppeBezeichnung_en)));
		this.m_sStudGruppeBezeichnung_en=value;
	}
	

	/**
	 * Description of this group (local language).
	 * @return Description of this group (local language).
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdStudGruppe.strStudGruppeBeschreibung</Code>
	 **/
	public String getStudGruppeBeschreibung(){
		return this.m_sStudGruppeBeschreibung;
	}

	/**
	 * Description of this group (local language).
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdStudGruppe.strStudGruppeBeschreibung</Code>
	 **/	
	public void setStudGruppeBeschreibung(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sStudGruppeBeschreibung)));
		this.m_sStudGruppeBeschreibung=value;
	}
	

	/**
	 * Description of this group in English.
	 * @return Description of this group in English.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdStudGruppe.strStudGruppeBeschreibung_en</Code>
	 **/
	public String getStudGruppeBeschreibung_en(){
		return this.m_sStudGruppeBeschreibung_en;
	}

	/**
	 * Description of this group in English.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdStudGruppe.strStudGruppeBeschreibung_en</Code>
	 **/	
	public void setStudGruppeBeschreibung_en(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sStudGruppeBeschreibung_en)));
		this.m_sStudGruppeBeschreibung_en=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdStudGruppe.strStudGruppeCustom1</Code>
	 **/
	public String getStudGruppeCustom1(){
		return this.m_sStudGruppeCustom1;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdStudGruppe.strStudGruppeCustom1</Code>
	 **/	
	public void setStudGruppeCustom1(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sStudGruppeCustom1)));
		this.m_sStudGruppeCustom1=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdStudGruppe.strStudGruppeCustom2</Code>
	 **/
	public String getStudGruppeCustom2(){
		return this.m_sStudGruppeCustom2;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdStudGruppe.strStudGruppeCustom2</Code>
	 **/	
	public void setStudGruppeCustom2(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sStudGruppeCustom2)));
		this.m_sStudGruppeCustom2=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdStudGruppe.strStudGruppeCustom3</Code>
	 **/
	public String getStudGruppeCustom3(){
		return this.m_sStudGruppeCustom3;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdStudGruppe.strStudGruppeCustom3</Code>
	 **/	
	public void setStudGruppeCustom3(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sStudGruppeCustom3)));
		this.m_sStudGruppeCustom3=value;
	}
	

	/**
	 * Flag indicating if this group is open for auto-distribution of students. New in @version 6-17 which is Sept 4 2006.
	 * @return Flag indicating if this group is open for auto-distribution of students. New in @version 6-17 which is Sept 4 2006.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdStudGruppe.blnStudGruppeOpen</Code>
	 **/
	public boolean getStudGruppeOpen(){
		return this.m_bStudGruppeOpen;
	}

	/**
	 * Flag indicating if this group is open for auto-distribution of students. New in @version 6-17 which is Sept 4 2006.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdStudGruppe.blnStudGruppeOpen</Code>
	 **/	
	public void setStudGruppeOpen(boolean value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_bStudGruppeOpen));
		this.m_bStudGruppeOpen=value;
	}
	


////////////////////////////////////////////////////////////////
// 3.   X M L  U T I L I T I E S
////////////////////////////////////////////////////////////////
	
	/**
	 * @return Objekt als XML-String
	 **/
	public String toXMLString(){
		return 	

			"<SeminarID>" + m_lSeminarID + "</SeminarID>"  + 
			"<StudGruppeID>" + m_lStudGruppeID + "</StudGruppeID>"  + 
			"<StudGruppeNummer>" + m_lStudGruppeNummer + "</StudGruppeNummer>"  + 
			"<StudGruppeBezeichnung>" + m_sStudGruppeBezeichnung + "</StudGruppeBezeichnung>"  + 
			"<StudGruppeBezeichnung_en>" + m_sStudGruppeBezeichnung_en + "</StudGruppeBezeichnung_en>"  + 
			"<StudGruppeBeschreibung>" + m_sStudGruppeBeschreibung + "</StudGruppeBeschreibung>"  + 
			"<StudGruppeBeschreibung_en>" + m_sStudGruppeBeschreibung_en + "</StudGruppeBeschreibung_en>"  + 
			"<StudGruppeCustom1>" + m_sStudGruppeCustom1 + "</StudGruppeCustom1>"  + 
			"<StudGruppeCustom2>" + m_sStudGruppeCustom2 + "</StudGruppeCustom2>"  + 
			"<StudGruppeCustom3>" + m_sStudGruppeCustom3 + "</StudGruppeCustom3>"  + 
			"<StudGruppeOpen>" + m_bStudGruppeOpen + "</StudGruppeOpen>" ;
	}

////////////////////////////////////////////////////////////////
// 4.   S Q L  U T I L I T I E S
////////////////////////////////////////////////////////////////


	/**
	 * Where-SQL für eindeutigen Index dieses Objekte in der Datenbank (in Tabelle 'tblSdStudGruppe.').
	 * @return SQL-String wie z.B. "Id1=? and Id2=?"
	 **/
	private String getSQLWhereClause(){
		return 
			"\"lngSeminarID\"=? AND " + 
			"\"lngStudGruppeID\"=?";
	}

	/**
	 * SQL-Befehl zum Löschen des Objekts in der Datenbank (aus Tabelle 'tblSdStudGruppe.')
	 * @return SQL-String zum Löschen des Objekts, z.B. "delete from T1 where (Id1=?);"
	 **/
	private String toDBDeleteString(){
		return "delete from \"tblSdStudGruppe\" where ( " + this.getSQLWhereClause() + ");";
	}

	/**
 	 * Setzt die Werte für alle Platzhalter in {@link #getSQLWhereClause()}.
	 * @param ii Offset für die Parameter (inklusive).
	 * @throws SQLException 
	 **/
	private void pokeWhere(int ii, PreparedStatement ps) throws SQLException{
		
		ps.setLong(ii++, m_lSeminarID);
		ps.setLong(ii++, m_lStudGruppeID);
	}

	/**
  	 * Füllt das SQL-Statement mit den aktuellen Werten des Objekts
	 * @throws SQLException
	 **/
	private void pokeStatement(PreparedStatement ps) throws SQLException{
		
		ps.setLong(1, m_lSeminarID);
		ps.setLong(2, m_lStudGruppeID);
		ps.setLong(3, m_lStudGruppeNummer);
		ps.setString(4, m_sStudGruppeBezeichnung);
		ps.setString(5, m_sStudGruppeBezeichnung_en);
		ps.setString(6, m_sStudGruppeBeschreibung);
		ps.setString(7, m_sStudGruppeBeschreibung_en);
		ps.setString(8, m_sStudGruppeCustom1);
		ps.setString(9, m_sStudGruppeCustom2);
		ps.setString(10, m_sStudGruppeCustom3);
		ps.setBoolean(11, m_bStudGruppeOpen);

	}

	/**
	 * SQL-Befehl zum Speichern der Objekteigenschaften in die Datenbank (Tabelle 'tblSdStudGruppe.')
	 * @return PreparedStatemtent, das nach Aufruf von {@link #pokeStatement(PreparedStatement)} und {@link #execute()} das Update ausführt.
	 **/
	private String toDBUpdateString(){
		return "update \"tblSdStudGruppe\" set " +
			"\"lngSeminarID\"=?, " +
			"\"lngStudGruppeID\"=?, " +
			"\"lngStudGruppeNummer\"=?, " +
			"\"strStudGruppeBezeichnung\"=?, " +
			"\"strStudGruppeBezeichnung_en\"=?, " +
			"\"strStudGruppeBeschreibung\"=?, " +
			"\"strStudGruppeBeschreibung_en\"=?, " +
			"\"strStudGruppeCustom1\"=?, " +
			"\"strStudGruppeCustom2\"=?, " +
			"\"strStudGruppeCustom3\"=?, " +
			"\"blnStudGruppeOpen\"=?" +
			" where (" + this.getSQLWhereClause() + ");";
	}

	/**
	 * SQL-Befehl zum Hinzufügen des Objekts in die Datenbank (Tabelle 'tblSdStudGruppe.')
	 * @return SQL-String "insert into...".
	 **/
	private String toDBAddString(){
		return "insert into \"tblSdStudGruppe\" ( " +
			"\"lngSeminarID\", \"lngStudGruppeID\", \"lngStudGruppeNummer\", \"strStudGruppeBezeichnung\", \"strStudGruppeBezeichnung_en\", \"strStudGruppeBeschreibung\", \"strStudGruppeBeschreibung_en\", \"strStudGruppeCustom1\", \"strStudGruppeCustom2\", \"strStudGruppeCustom3\", \"blnStudGruppeOpen\" ) VALUES ( ?,?,?,?,?,?,?,?,?,?,?);";
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
	private void init(long lngSeminarID, long lngStudGruppeID) throws SQLException, NamingException{

		this.m_lSeminarID=lngSeminarID;

		this.m_lStudGruppeID=lngStudGruppeID;

		
		PreparedStatement pstm = prepareStatement("select * from \"tblSdStudGruppe\" where (" + this.getSQLWhereClause() + ");");
		pokeWhere(1,pstm);

		ResultSet rst=pstm.executeQuery();
		if(rst.next()) this.initByRst(rst);
		rst.close();
		this.disconnect();
		rst=null;
	}	
	
	/**
	 * Lade die Objekteigenschaften aus einem ResultSet.
	 * param rst ResultSet mit allen Spalten der Tabelle 'tblSdStudGruppe'
	 * @throws SQLException 
	 **/
	protected void initByRst(ResultSet rst) throws SQLException{
		this.m_lSeminarID=rst.getLong("lngSeminarID");
		this.m_lStudGruppeID=rst.getLong("lngStudGruppeID");
		this.m_lStudGruppeNummer=rst.getLong("lngStudGruppeNummer");
		this.m_sStudGruppeBezeichnung=rst.getString("strStudGruppeBezeichnung");
		this.m_sStudGruppeBezeichnung_en=rst.getString("strStudGruppeBezeichnung_en");
		this.m_sStudGruppeBeschreibung=rst.getString("strStudGruppeBeschreibung");
		this.m_sStudGruppeBeschreibung_en=rst.getString("strStudGruppeBeschreibung_en");
		this.m_sStudGruppeCustom1=rst.getString("strStudGruppeCustom1");
		this.m_sStudGruppeCustom2=rst.getString("strStudGruppeCustom2");
		this.m_sStudGruppeCustom3=rst.getString("strStudGruppeCustom3");
		this.m_bStudGruppeOpen=rst.getBoolean("blnStudGruppeOpen");	
	}	
	
	/**
	 * Lade die Objekteigenschaften aus einer XML-Node.
	 * param node XML-Node mit allen Eigenschaften als Tags.
	 * @throws ParseException (Datum muss im ISO-Format yyyy-MM-dd übergeben werden).
	 **/
	private void initByNode(Node node) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		this.m_lSeminarID=Long.parseLong(shjNodeValue(node, "SeminarID"));
		this.m_lStudGruppeID=Long.parseLong(shjNodeValue(node, "StudGruppeID"));
		this.m_lStudGruppeNummer=Long.parseLong(shjNodeValue(node, "StudGruppeNummer"));
		this.m_sStudGruppeBezeichnung=(shjNodeValue(node, "StudGruppeBezeichnung"));
		this.m_sStudGruppeBezeichnung_en=(shjNodeValue(node, "StudGruppeBezeichnung_en"));
		this.m_sStudGruppeBeschreibung=(shjNodeValue(node, "StudGruppeBeschreibung"));
		this.m_sStudGruppeBeschreibung_en=(shjNodeValue(node, "StudGruppeBeschreibung_en"));
		this.m_sStudGruppeCustom1=(shjNodeValue(node, "StudGruppeCustom1"));
		this.m_sStudGruppeCustom2=(shjNodeValue(node, "StudGruppeCustom2"));
		this.m_sStudGruppeCustom3=(shjNodeValue(node, "StudGruppeCustom3"));
		this.m_bStudGruppeOpen=Boolean.valueOf(shjNodeValue(node, "StudGruppeOpen")).booleanValue();
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
		pokeWhere(12,pstm);
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
	public StudGruppe(){}	
	
	/**
	 * Konstruktor mit Indexwerten (eindeutig) der assoziierten Datenbanktabelle
	 * @throws NamingException 
	 * @throws SQLException 
	 **/
	public StudGruppe(long lngSeminarID, long lngStudGruppeID) throws SQLException, NamingException{
		this.init(lngSeminarID, lngStudGruppeID);
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
	public StudGruppe(ResultSet rst) throws SQLException{
		this.initByRst(rst);
		this.m_bIsDirty = false;
	}

	/**
	 * Konstruktor per XML-Darstellung des Objekts.
	 * @throws ParseException, if a date can't be read.
	 **/
	public StudGruppe(Node node) throws ParseException{
		this.initByNode(node);
		this.m_bIsDirty = false;
	}

  }//Klassenende
