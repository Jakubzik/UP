
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
 *  Modules: Kvv
Faculty.
 *  
 *  ALLGEMEINE INFORMATION ZU DEN OBJEKTEN DIESES PACKAGES:
 *  Diese Klasse kapselt die Tabelle 'Fakultaet' in 
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
public class Fakultaet extends shjCore{

	private static final long serialVersionUID = 7L;

////////////////////////////////////////////////////////////////
// 1.   P R I V A T E   D E K L A R A T I O N E N
////////////////////////////////////////////////////////////////
	
	private boolean m_bIsDirty = false;

	private long m_lSdUniID;
	private long m_lSdFakultaetID;
	private String m_sFakultaetUnivISID;
	private long m_lFakultaetNr;
	private String m_sFakultaetName;
	private String m_sFakultaetLink;

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
	 * Id of university.
	 * @return Id of university.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdFakultaet.lngSdUniID</Code>
	 **/
	public long getSdUniID(){
		return this.m_lSdUniID;
	}

	/**
	 * Id of university.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdFakultaet.lngSdUniID</Code>
	 **/	
	public void setSdUniID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lSdUniID));
		this.m_lSdUniID=value;
	}
	

	/**
	 * Id of faculty.
	 * @return Id of faculty.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdFakultaet.lngSdFakultaetID</Code>
	 **/
	public long getFakultaetID(){
		return this.m_lSdFakultaetID;
	}

	/**
	 * Id of faculty.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdFakultaet.lngSdFakultaetID</Code>
	 **/	
	public void setFakultaetID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lSdFakultaetID));
		this.m_lSdFakultaetID=value;
	}
	

	/**
	 * UnivIS-ID of faculty (on import). Unused.
	 * @return UnivIS-ID of faculty (on import). Unused.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdFakultaet.strFakultaetUnivISID</Code>
	 **/
	public String getFakultaetUnivISID(){
		return this.m_sFakultaetUnivISID;
	}

	/**
	 * UnivIS-ID of faculty (on import). Unused.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdFakultaet.strFakultaetUnivISID</Code>
	 **/	
	public void setFakultaetUnivISID(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sFakultaetUnivISID)));
		this.m_sFakultaetUnivISID=value;
	}
	

	/**
	 * Number of faculty.
	 * @return Number of faculty.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdFakultaet.strFakultaetNr</Code>
	 **/
	public long getFakultaetNr(){
		return this.m_lFakultaetNr;
	}

	/**
	 * Number of faculty.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdFakultaet.strFakultaetNr</Code>
	 **/	
	public void setFakultaetNr(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lFakultaetNr));
		this.m_lFakultaetNr=value;
	}
	

	/**
	 * Faculty name.
	 * @return Faculty name.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdFakultaet.strFakultaetName</Code>
	 **/
	public String getFakultaetName(){
		return this.m_sFakultaetName;
	}

	/**
	 * Faculty name.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdFakultaet.strFakultaetName</Code>
	 **/	
	public void setFakultaetName(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sFakultaetName)));
		this.m_sFakultaetName=value;
	}
	

	/**
	 * Link to faculty homepage.
	 * @return Link to faculty homepage.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdFakultaet.strFakultaetLink</Code>
	 **/
	public String getFakultaetLink(){
		return this.m_sFakultaetLink;
	}

	/**
	 * Link to faculty homepage.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdFakultaet.strFakultaetLink</Code>
	 **/	
	public void setFakultaetLink(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sFakultaetLink)));
		this.m_sFakultaetLink=value;
	}
	


////////////////////////////////////////////////////////////////
// 3.   X M L  U T I L I T I E S
////////////////////////////////////////////////////////////////
	
	/**
	 * @return Objekt als XML-String
	 **/
	public String toXMLString(){
		return 	

			"<SdUniID>" + m_lSdUniID + "</SdUniID>"  + 
			"<SdFakultaetID>" + m_lSdFakultaetID + "</SdFakultaetID>"  + 
			"<FakultaetUnivISID>" + m_sFakultaetUnivISID + "</FakultaetUnivISID>"  + 
			"<FakultaetNr>" + m_lFakultaetNr + "</FakultaetNr>"  + 
			"<FakultaetName>" + m_sFakultaetName + "</FakultaetName>"  + 
			"<FakultaetLink>" + m_sFakultaetLink + "</FakultaetLink>" ;
	}

////////////////////////////////////////////////////////////////
// 4.   S Q L  U T I L I T I E S
////////////////////////////////////////////////////////////////


	/**
	 * Where-SQL für eindeutigen Index dieses Objekte in der Datenbank (in Tabelle 'tblSdFakultaet.').
	 * @return SQL-String wie z.B. "Id1=? and Id2=?"
	 **/
	private String getSQLWhereClause(){
		return 
			"\"lngSdUniID\"=? AND " + 
			"\"lngSdFakultaetID\"=?";
	}

	/**
	 * SQL-Befehl zum Löschen des Objekts in der Datenbank (aus Tabelle 'tblSdFakultaet.')
	 * @return SQL-String zum Löschen des Objekts, z.B. "delete from T1 where (Id1=?);"
	 **/
	private String toDBDeleteString(){
		return "delete from \"tblSdFakultaet\" where ( " + this.getSQLWhereClause() + ");";
	}

	/**
 	 * Setzt die Werte für alle Platzhalter in {@link #getSQLWhereClause()}.
	 * @param ii Offset für die Parameter (inklusive).
	 * @throws SQLException 
	 **/
	private void pokeWhere(int ii, PreparedStatement ps) throws SQLException{
		
		ps.setLong(ii++, m_lSdUniID);
		ps.setLong(ii++, m_lSdFakultaetID);
	}

	/**
  	 * Füllt das SQL-Statement mit den aktuellen Werten des Objekts
	 * @throws SQLException
	 **/
	private void pokeStatement(PreparedStatement ps) throws SQLException{
		
		ps.setLong(1, m_lSdUniID);
		ps.setLong(2, m_lSdFakultaetID);
		ps.setString(3, m_sFakultaetUnivISID);
		ps.setLong(4, m_lFakultaetNr);
		ps.setString(5, m_sFakultaetName);
		ps.setString(6, m_sFakultaetLink);

	}

	/**
	 * SQL-Befehl zum Speichern der Objekteigenschaften in die Datenbank (Tabelle 'tblSdFakultaet.')
	 * @return PreparedStatemtent, das nach Aufruf von {@link #pokeStatement(PreparedStatement)} und {@link #execute()} das Update ausführt.
	 **/
	private String toDBUpdateString(){
		return "update \"tblSdFakultaet\" set " +
			"\"lngSdUniID\"=?, " +
			"\"lngSdFakultaetID\"=?, " +
			"\"strFakultaetUnivISID\"=?, " +
			"\"strFakultaetNr\"=?, " +
			"\"strFakultaetName\"=?, " +
			"\"strFakultaetLink\"=?" +
			" where (" + this.getSQLWhereClause() + ");";
	}

	/**
	 * SQL-Befehl zum Hinzufügen des Objekts in die Datenbank (Tabelle 'tblSdFakultaet.')
	 * @return SQL-String "insert into...".
	 **/
	private String toDBAddString(){
		return "insert into \"tblSdFakultaet\" ( " +
			"\"lngSdUniID\", \"strFakultaetUnivISID\", \"strFakultaetNr\", \"strFakultaetName\", \"strFakultaetLink\" ) VALUES ( ?,?,?,?,?,?);";
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
	private void init(long lngSdUniID, long lngSdFakultaetID) throws SQLException, NamingException{

		this.m_lSdUniID=lngSdUniID;

		this.m_lSdFakultaetID=lngSdFakultaetID;

		
		PreparedStatement pstm = prepareStatement("select * from \"tblSdFakultaet\" where (" + this.getSQLWhereClause() + ");");
		pokeWhere(1,pstm);

		ResultSet rst=pstm.executeQuery();
		if(rst.next()) this.initByRst(rst);
		rst.close();
		this.disconnect();
		rst=null;
	}	
	
	/**
	 * Lade die Objekteigenschaften aus einem ResultSet.
	 * param rst ResultSet mit allen Spalten der Tabelle 'tblSdFakultaet'
	 * @throws SQLException 
	 **/
	protected void initByRst(ResultSet rst) throws SQLException{
		this.m_lSdUniID=rst.getLong("lngSdUniID");
		this.m_lSdFakultaetID=rst.getLong("lngSdFakultaetID");
		this.m_sFakultaetUnivISID=rst.getString("strFakultaetUnivISID");
		this.m_lFakultaetNr=rst.getLong("strFakultaetNr");
		this.m_sFakultaetName=rst.getString("strFakultaetName");
		this.m_sFakultaetLink=rst.getString("strFakultaetLink");	
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
		pokeWhere(7,pstm);
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
	public Fakultaet(){}	
	
	/**
	 * Konstruktor mit Indexwerten (eindeutig) der assoziierten Datenbanktabelle
	 * @throws NamingException 
	 * @throws SQLException 
	 **/
	public Fakultaet(long lngSdUniID, long lngSdFakultaetID) throws SQLException, NamingException{
		this.init(lngSdUniID, lngSdFakultaetID);
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
	public Fakultaet(ResultSet rst) throws SQLException{
		this.initByRst(rst);
		this.m_bIsDirty = false;
	}

  }//Klassenende
