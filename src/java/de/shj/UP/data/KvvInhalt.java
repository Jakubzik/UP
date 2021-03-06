
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
 *  Module: Kvv
List of contents (structure) of course-catalog.
 *  
 *  ALLGEMEINE INFORMATION ZU DEN OBJEKTEN DIESES PACKAGES:
 *  Diese Klasse kapselt die Tabelle 'KvvInhalt' in 
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
public class KvvInhalt extends shjCore{

	private static final long serialVersionUID = 7L;

////////////////////////////////////////////////////////////////
// 1.   P R I V A T E   D E K L A R A T I O N E N
////////////////////////////////////////////////////////////////
	
	private boolean m_bIsDirty = false;

	private long m_lSdSeminarID;
	private long m_lSdKvvInhaltNr;
	private String m_sKvvInhaltHeading;
	private String m_sKvvInhaltHeadingDescription;
	private int m_iKvvInhaltHeadingSequence;
	private String m_sKvvInhaltTyp;

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
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKvvInhalt.lngSdSeminarID</Code>
	 **/
	public long getSdSeminarID(){
		return this.m_lSdSeminarID;
	}

	/**
	 * Seminar
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKvvInhalt.lngSdSeminarID</Code>
	 **/	
	public void setSdSeminarID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lSdSeminarID));
		this.m_lSdSeminarID=value;
	}
	

	/**
	 * Sequence.
	 * @return Sequence.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKvvInhalt.intSdKvvInhaltNr</Code>
	 **/
	public long getKvvInhaltNr(){
		return this.m_lSdKvvInhaltNr;
	}

	/**
	 * Sequence.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKvvInhalt.intSdKvvInhaltNr</Code>
	 **/	
	public void setKvvInhaltNr(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lSdKvvInhaltNr));
		this.m_lSdKvvInhaltNr=value;
	}
	

	/**
	 * Heading of content-item.
	 * @return Heading of content-item.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKvvInhalt.strKvvInhaltHeading</Code>
	 **/
	public String getKvvInhaltHeading(){
		return this.m_sKvvInhaltHeading;
	}

	/**
	 * Heading of content-item.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKvvInhalt.strKvvInhaltHeading</Code>
	 **/	
	public void setKvvInhaltHeading(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKvvInhaltHeading)));
		this.m_sKvvInhaltHeading=value;
	}
	

	/**
	 * Description of this item.
	 * @return Description of this item.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKvvInhalt.strKvvInhaltHeadingDescription</Code>
	 **/
	public String getKvvInhaltHeadingDescription(){
		return this.m_sKvvInhaltHeadingDescription;
	}

	/**
	 * Description of this item.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKvvInhalt.strKvvInhaltHeadingDescription</Code>
	 **/	
	public void setKvvInhaltHeadingDescription(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKvvInhaltHeadingDescription)));
		this.m_sKvvInhaltHeadingDescription=value;
	}
	

	/**
	 * Sequence.
	 * @return Sequence.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKvvInhalt.intKvvInhaltHeadingSequence</Code>
	 **/
	public int getKvvInhaltHeadingSequence(){
		return this.m_iKvvInhaltHeadingSequence;
	}

	/**
	 * Sequence.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKvvInhalt.intKvvInhaltHeadingSequence</Code>
	 **/	
	public void setKvvInhaltHeadingSequence(int value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_iKvvInhaltHeadingSequence));
		this.m_iKvvInhaltHeadingSequence=value;
	}
	

	/**
	 * Type of content (#hack this description).
	 * @return Type of content (#hack this description).
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKvvInhalt.strKvvInhaltTyp</Code>
	 **/
	public String getKvvInhaltTyp(){
		return this.m_sKvvInhaltTyp;
	}

	/**
	 * Type of content (#hack this description).
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdKvvInhalt.strKvvInhaltTyp</Code>
	 **/	
	public void setKvvInhaltTyp(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKvvInhaltTyp)));
		this.m_sKvvInhaltTyp=value;
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
			"<SdKvvInhaltNr>" + m_lSdKvvInhaltNr + "</SdKvvInhaltNr>"  + 
			"<KvvInhaltHeading>" + m_sKvvInhaltHeading + "</KvvInhaltHeading>"  + 
			"<KvvInhaltHeadingDescription>" + m_sKvvInhaltHeadingDescription + "</KvvInhaltHeadingDescription>"  + 
			"<KvvInhaltHeadingSequence>" + m_iKvvInhaltHeadingSequence + "</KvvInhaltHeadingSequence>"  + 
			"<KvvInhaltTyp>" + m_sKvvInhaltTyp + "</KvvInhaltTyp>" ;
	}

////////////////////////////////////////////////////////////////
// 4.   S Q L  U T I L I T I E S
////////////////////////////////////////////////////////////////


	/**
	 * Where-SQL für eindeutigen Index dieses Objekte in der Datenbank (in Tabelle 'tblSdKvvInhalt.').
	 * @return SQL-String wie z.B. "Id1=? and Id2=?"
	 **/
	private String getSQLWhereClause(){
		return 
			"\"lngSdSeminarID\"=? AND " + 
			"\"intSdKvvInhaltNr\"=?";
	}

	/**
	 * SQL-Befehl zum Löschen des Objekts in der Datenbank (aus Tabelle 'tblSdKvvInhalt.')
	 * @return SQL-String zum Löschen des Objekts, z.B. "delete from T1 where (Id1=?);"
	 **/
	private String toDBDeleteString(){
		return "delete from \"tblSdKvvInhalt\" where ( " + this.getSQLWhereClause() + ");";
	}

	/**
 	 * Setzt die Werte für alle Platzhalter in {@link #getSQLWhereClause()}.
	 * @param ii Offset für die Parameter (inklusive).
	 * @throws SQLException 
	 **/
	private void pokeWhere(int ii, PreparedStatement ps) throws SQLException{
		
		ps.setLong(ii++, m_lSdSeminarID);
		ps.setLong(ii++, m_lSdKvvInhaltNr);
	}

	/**
  	 * Füllt das SQL-Statement mit den aktuellen Werten des Objekts
	 * @throws SQLException
	 **/
	private void pokeStatement(PreparedStatement ps) throws SQLException{
		
		ps.setLong(1, m_lSdSeminarID);
		ps.setLong(2, m_lSdKvvInhaltNr);
		ps.setString(3, m_sKvvInhaltHeading);
		ps.setString(4, m_sKvvInhaltHeadingDescription);
		ps.setInt(5, m_iKvvInhaltHeadingSequence);
		ps.setString(6, m_sKvvInhaltTyp);

	}

	/**
	 * SQL-Befehl zum Speichern der Objekteigenschaften in die Datenbank (Tabelle 'tblSdKvvInhalt.')
	 * @return PreparedStatemtent, das nach Aufruf von {@link #pokeStatement(PreparedStatement)} und {@link #execute()} das Update ausführt.
	 **/
	private String toDBUpdateString(){
		return "update \"tblSdKvvInhalt\" set " +
			"\"lngSdSeminarID\"=?, " +
			"\"intSdKvvInhaltNr\"=?, " +
			"\"strKvvInhaltHeading\"=?, " +
			"\"strKvvInhaltHeadingDescription\"=?, " +
			"\"intKvvInhaltHeadingSequence\"=?, " +
			"\"strKvvInhaltTyp\"=?" +
			" where (" + this.getSQLWhereClause() + ");";
	}

	/**
	 * SQL-Befehl zum Hinzufügen des Objekts in die Datenbank (Tabelle 'tblSdKvvInhalt.')
	 * @return SQL-String "insert into...".
	 **/
	private String toDBAddString(){
		return "insert into \"tblSdKvvInhalt\" ( " +
			"\"lngSdSeminarID\", \"intSdKvvInhaltNr\", \"strKvvInhaltHeading\", \"strKvvInhaltHeadingDescription\", \"intKvvInhaltHeadingSequence\", \"strKvvInhaltTyp\" ) VALUES ( ?,?,?,?,?,?);";
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
	private void init(long lngSdSeminarID, long intSdKvvInhaltNr) throws SQLException, NamingException{

		this.m_lSdSeminarID=lngSdSeminarID;

		this.m_lSdKvvInhaltNr=intSdKvvInhaltNr;

		
		PreparedStatement pstm = prepareStatement("select * from \"tblSdKvvInhalt\" where (" + this.getSQLWhereClause() + ");");
		pokeWhere(1,pstm);

		ResultSet rst=pstm.executeQuery();
		if(rst.next()) this.initByRst(rst);
		rst.close();
		this.disconnect();
		rst=null;
	}	
	
	/**
	 * Lade die Objekteigenschaften aus einem ResultSet.
	 * param rst ResultSet mit allen Spalten der Tabelle 'tblSdKvvInhalt'
	 * @throws SQLException 
	 **/
	protected void initByRst(ResultSet rst) throws SQLException{
		this.m_lSdSeminarID=rst.getLong("lngSdSeminarID");
		this.m_lSdKvvInhaltNr=rst.getLong("intSdKvvInhaltNr");
		this.m_sKvvInhaltHeading=rst.getString("strKvvInhaltHeading");
		this.m_sKvvInhaltHeadingDescription=rst.getString("strKvvInhaltHeadingDescription");
		this.m_iKvvInhaltHeadingSequence=rst.getInt("intKvvInhaltHeadingSequence");
		this.m_sKvvInhaltTyp=rst.getString("strKvvInhaltTyp");	
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
	public KvvInhalt(){}	
	
	/**
	 * Konstruktor mit Indexwerten (eindeutig) der assoziierten Datenbanktabelle
	 * @throws NamingException 
	 * @throws SQLException 
	 **/
	public KvvInhalt(long lngSdSeminarID, long intSdKvvInhaltNr) throws SQLException, NamingException{
		this.init(lngSdSeminarID, intSdKvvInhaltNr);
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
	public KvvInhalt(ResultSet rst) throws SQLException{
		this.initByRst(rst);
		this.m_bIsDirty = false;
	}

  }//Klassenende
