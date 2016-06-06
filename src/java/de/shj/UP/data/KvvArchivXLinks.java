
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
 *  Modules: Kvv
Archive of links and/or downloads that courses offered in past semesters.
 *  
 *  ALLGEMEINE INFORMATION ZU DEN OBJEKTEN DIESES PACKAGES:
 *  Diese Klasse kapselt die Tabelle 'KvvArchivXLinks' in 
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
public class KvvArchivXLinks extends shjCore{

	private static final long serialVersionUID = 7L;

////////////////////////////////////////////////////////////////
// 1.   P R I V A T E   D E K L A R A T I O N E N
////////////////////////////////////////////////////////////////
	
	private boolean m_bIsDirty = false;

	private long m_lID;
	private long m_lLinkID;
	private String m_sArchivLinkURL;
	private String m_sArchivLinkBezeichnung;
	private String m_sArchivLinkBeschreibung;
	private boolean m_bArchivLinkDownload;
	private String m_sArchivLinkCustom1;
	private String m_sArchivLinkCustom2;
	private String m_sArchivLinkCustom3;

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
	 * Id of this object.
	 * @return Id of this object.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchivXLinks.lngID</Code>
	 **/
	public long getID(){
		return this.m_lID;
	}

	/**
	 * Id of this object.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchivXLinks.lngID</Code>
	 **/	
	public void setID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lID));
		this.m_lID=value;
	}
	

	/**
	 * Id of link.
	 * @return Id of link.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchivXLinks.lngLinkID</Code>
	 **/
	public long getLinkID(){
		return this.m_lLinkID;
	}

	/**
	 * Id of link.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchivXLinks.lngLinkID</Code>
	 **/	
	public void setLinkID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lLinkID));
		this.m_lLinkID=value;
	}
	

	/**
	 * URL of this link (can be anything).
	 * @return URL of this link (can be anything).
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchivXLinks.strArchivLinkURL</Code>
	 **/
	public String getArchivLinkURL(){
		return this.m_sArchivLinkURL;
	}

	/**
	 * URL of this link (can be anything).
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchivXLinks.strArchivLinkURL</Code>
	 **/	
	public void setArchivLinkURL(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sArchivLinkURL)));
		this.m_sArchivLinkURL=value;
	}
	

	/**
	 * Name of this link (a-tag).
	 * @return Name of this link (a-tag).
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchivXLinks.strArchivLinkBezeichnung</Code>
	 **/
	public String getArchivLinkBezeichnung(){
		return this.m_sArchivLinkBezeichnung;
	}

	/**
	 * Name of this link (a-tag).
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchivXLinks.strArchivLinkBezeichnung</Code>
	 **/	
	public void setArchivLinkBezeichnung(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sArchivLinkBezeichnung)));
		this.m_sArchivLinkBezeichnung=value;
	}
	

	/**
	 * Description of this link.
	 * @return Description of this link.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchivXLinks.strArchivLinkBeschreibung</Code>
	 **/
	public String getArchivLinkBeschreibung(){
		return this.m_sArchivLinkBeschreibung;
	}

	/**
	 * Description of this link.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchivXLinks.strArchivLinkBeschreibung</Code>
	 **/	
	public void setArchivLinkBeschreibung(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sArchivLinkBeschreibung)));
		this.m_sArchivLinkBeschreibung=value;
	}
	

	/**
	 * Was this a download?
	 * @return Was this a download?
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchivXLinks.blnArchivLinkDownload</Code>
	 **/
	public boolean getArchivLinkDownload(){
		return this.m_bArchivLinkDownload;
	}

	/**
	 * Was this a download?
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchivXLinks.blnArchivLinkDownload</Code>
	 **/	
	public void setArchivLinkDownload(boolean value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_bArchivLinkDownload));
		this.m_bArchivLinkDownload=value;
	}
	

	/**
	 * Custom
	 * @return Custom
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchivXLinks.strArchivLinkCustom1</Code>
	 **/
	public String getArchivLinkCustom1(){
		return this.m_sArchivLinkCustom1;
	}

	/**
	 * Custom
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchivXLinks.strArchivLinkCustom1</Code>
	 **/	
	public void setArchivLinkCustom1(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sArchivLinkCustom1)));
		this.m_sArchivLinkCustom1=value;
	}
	

	/**
	 * Custom
	 * @return Custom
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchivXLinks.strArchivLinkCustom2</Code>
	 **/
	public String getArchivLinkCustom2(){
		return this.m_sArchivLinkCustom2;
	}

	/**
	 * Custom
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchivXLinks.strArchivLinkCustom2</Code>
	 **/	
	public void setArchivLinkCustom2(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sArchivLinkCustom2)));
		this.m_sArchivLinkCustom2=value;
	}
	

	/**
	 * Custom
	 * @return Custom
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchivXLinks.strArchivLinkCustom3</Code>
	 **/
	public String getArchivLinkCustom3(){
		return this.m_sArchivLinkCustom3;
	}

	/**
	 * Custom
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKvvArchivXLinks.strArchivLinkCustom3</Code>
	 **/	
	public void setArchivLinkCustom3(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sArchivLinkCustom3)));
		this.m_sArchivLinkCustom3=value;
	}
	


////////////////////////////////////////////////////////////////
// 3.   X M L  U T I L I T I E S
////////////////////////////////////////////////////////////////
	
	/**
	 * @return Objekt als XML-String
	 **/
	public String toXMLString(){
		return 	

			"<ID>" + m_lID + "</ID>"  + 
			"<LinkID>" + m_lLinkID + "</LinkID>"  + 
			"<ArchivLinkURL>" + m_sArchivLinkURL + "</ArchivLinkURL>"  + 
			"<ArchivLinkBezeichnung>" + m_sArchivLinkBezeichnung + "</ArchivLinkBezeichnung>"  + 
			"<ArchivLinkBeschreibung>" + m_sArchivLinkBeschreibung + "</ArchivLinkBeschreibung>"  + 
			"<ArchivLinkDownload>" + m_bArchivLinkDownload + "</ArchivLinkDownload>"  + 
			"<ArchivLinkCustom1>" + m_sArchivLinkCustom1 + "</ArchivLinkCustom1>"  + 
			"<ArchivLinkCustom2>" + m_sArchivLinkCustom2 + "</ArchivLinkCustom2>"  + 
			"<ArchivLinkCustom3>" + m_sArchivLinkCustom3 + "</ArchivLinkCustom3>" ;
	}

////////////////////////////////////////////////////////////////
// 4.   S Q L  U T I L I T I E S
////////////////////////////////////////////////////////////////


	/**
	 * Where-SQL für eindeutigen Index dieses Objekte in der Datenbank (in Tabelle 'tblBdKvvArchivXLinks.').
	 * @return SQL-String wie z.B. "Id1=? and Id2=?"
	 **/
	private String getSQLWhereClause(){
		return 
			"\"lngID\"=? AND " + 
			"\"lngLinkID\"=?";
	}

	/**
	 * SQL-Befehl zum Löschen des Objekts in der Datenbank (aus Tabelle 'tblBdKvvArchivXLinks.')
	 * @return SQL-String zum Löschen des Objekts, z.B. "delete from T1 where (Id1=?);"
	 **/
	private String toDBDeleteString(){
		return "delete from \"tblBdKvvArchivXLinks\" where ( " + this.getSQLWhereClause() + ");";
	}

	/**
 	 * Setzt die Werte für alle Platzhalter in {@link #getSQLWhereClause()}.
	 * @param ii Offset für die Parameter (inklusive).
	 * @throws SQLException 
	 **/
	private void pokeWhere(int ii, PreparedStatement ps) throws SQLException{
		
		ps.setLong(ii++, m_lID);
		ps.setLong(ii++, m_lLinkID);
	}

	/**
  	 * Füllt das SQL-Statement mit den aktuellen Werten des Objekts
	 * @throws SQLException
	 **/
	private void pokeStatement(PreparedStatement ps) throws SQLException{
		
		ps.setLong(1, m_lID);
		ps.setLong(2, m_lLinkID);
		ps.setString(3, m_sArchivLinkURL);
		ps.setString(4, m_sArchivLinkBezeichnung);
		ps.setString(5, m_sArchivLinkBeschreibung);
		ps.setBoolean(6, m_bArchivLinkDownload);
		ps.setString(7, m_sArchivLinkCustom1);
		ps.setString(8, m_sArchivLinkCustom2);
		ps.setString(9, m_sArchivLinkCustom3);

	}

	/**
	 * SQL-Befehl zum Speichern der Objekteigenschaften in die Datenbank (Tabelle 'tblBdKvvArchivXLinks.')
	 * @return PreparedStatemtent, das nach Aufruf von {@link #pokeStatement(PreparedStatement)} und {@link #execute()} das Update ausführt.
	 **/
	private String toDBUpdateString(){
		return "update \"tblBdKvvArchivXLinks\" set " +
			"\"lngID\"=?, " +
			"\"lngLinkID\"=?, " +
			"\"strArchivLinkURL\"=?, " +
			"\"strArchivLinkBezeichnung\"=?, " +
			"\"strArchivLinkBeschreibung\"=?, " +
			"\"blnArchivLinkDownload\"=?, " +
			"\"strArchivLinkCustom1\"=?, " +
			"\"strArchivLinkCustom2\"=?, " +
			"\"strArchivLinkCustom3\"=?" +
			" where (" + this.getSQLWhereClause() + ");";
	}

	/**
	 * SQL-Befehl zum Hinzufügen des Objekts in die Datenbank (Tabelle 'tblBdKvvArchivXLinks.')
	 * @return SQL-String "insert into...".
	 **/
	private String toDBAddString(){
		return "insert into \"tblBdKvvArchivXLinks\" ( " +
			"\"lngID\", \"lngLinkID\", \"strArchivLinkURL\", \"strArchivLinkBezeichnung\", \"strArchivLinkBeschreibung\", \"blnArchivLinkDownload\", \"strArchivLinkCustom1\", \"strArchivLinkCustom2\", \"strArchivLinkCustom3\" ) VALUES ( ?,?,?,?,?,?,?,?,?);";
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
	private void init(long lngID, long lngLinkID) throws SQLException, NamingException{

		this.m_lID=lngID;

		this.m_lLinkID=lngLinkID;

		
		PreparedStatement pstm = prepareStatement("select * from \"tblBdKvvArchivXLinks\" where (" + this.getSQLWhereClause() + ");");
		pokeWhere(1,pstm);

		ResultSet rst=pstm.executeQuery();
		if(rst.next()) this.initByRst(rst);
		rst.close();
		this.disconnect();
		rst=null;
	}	
	
	/**
	 * Lade die Objekteigenschaften aus einem ResultSet.
	 * param rst ResultSet mit allen Spalten der Tabelle 'tblBdKvvArchivXLinks'
	 * @throws SQLException 
	 **/
	protected void initByRst(ResultSet rst) throws SQLException{
		this.m_lID=rst.getLong("lngID");
		this.m_lLinkID=rst.getLong("lngLinkID");
		this.m_sArchivLinkURL=rst.getString("strArchivLinkURL");
		this.m_sArchivLinkBezeichnung=rst.getString("strArchivLinkBezeichnung");
		this.m_sArchivLinkBeschreibung=rst.getString("strArchivLinkBeschreibung");
		this.m_bArchivLinkDownload=rst.getBoolean("blnArchivLinkDownload");
		this.m_sArchivLinkCustom1=rst.getString("strArchivLinkCustom1");
		this.m_sArchivLinkCustom2=rst.getString("strArchivLinkCustom2");
		this.m_sArchivLinkCustom3=rst.getString("strArchivLinkCustom3");	
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
		pokeWhere(10,pstm);
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
	public KvvArchivXLinks(){}	
	
	/**
	 * Konstruktor mit Indexwerten (eindeutig) der assoziierten Datenbanktabelle
	 * @throws NamingException 
	 * @throws SQLException 
	 **/
	public KvvArchivXLinks(long lngID, long lngLinkID) throws SQLException, NamingException{
		this.init(lngID, lngLinkID);
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
	public KvvArchivXLinks(ResultSet rst) throws SQLException{
		this.initByRst(rst);
		this.m_bIsDirty = false;
	}

  }//Klassenende
