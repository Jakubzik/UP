
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
Plan for external people and their usage of rooms in this seminar/institute.
 *  
 *  ALLGEMEINE INFORMATION ZU DEN OBJEKTEN DIESES PACKAGES:
 *  Diese Klasse kapselt die Tabelle 'RaumplanExtern' in 
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
public class RaumplanExtern extends shjCore{

	private static final long serialVersionUID = 7L;

////////////////////////////////////////////////////////////////
// 1.   P R I V A T E   D E K L A R A T I O N E N
////////////////////////////////////////////////////////////////
	
	private boolean m_bIsDirty = false;

	private long m_lSdSeminarID;
	private long m_lLfdNr;
	private String m_sRaum;
	private String m_sTag;
	private String m_tBeginn;
	private String m_tEnde;
	private String m_sName;
	private String m_sKurstypBezeichnung;
	private boolean m_bPlanung;

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
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdRaumplanExtern.lngSdSeminarID</Code>
	 **/
	public long getSdSeminarID(){
		return this.m_lSdSeminarID;
	}

	/**
	 * Seminar
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdRaumplanExtern.lngSdSeminarID</Code>
	 **/	
	public void setSdSeminarID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lSdSeminarID));
		this.m_lSdSeminarID=value;
	}
	

	/**
	 * Sequence-number.
	 * @return Sequence-number.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdRaumplanExtern.lngLfdNr</Code>
	 **/
	public long getLfdNr(){
		return this.m_lLfdNr;
	}

	/**
	 * Sequence-number.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdRaumplanExtern.lngLfdNr</Code>
	 **/	
	public void setLfdNr(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lLfdNr));
		this.m_lLfdNr=value;
	}
	

	/**
	 * Name of room.
	 * @return Name of room.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdRaumplanExtern.strRaum</Code>
	 **/
	public String getRaum(){
		return this.m_sRaum;
	}

	/**
	 * Name of room.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdRaumplanExtern.strRaum</Code>
	 **/	
	public void setRaum(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sRaum)));
		this.m_sRaum=value;
	}
	

	/**
	 * Day where room is used (weekly).
	 * @return Day where room is used (weekly).
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdRaumplanExtern.strTag</Code>
	 **/
	public String getTag(){
		return this.m_sTag;
	}

	/**
	 * Day where room is used (weekly).
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdRaumplanExtern.strTag</Code>
	 **/	
	public void setTag(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sTag)));
		this.m_sTag=value;
	}
	

	/**
	 * Start-time where room is used (weekly).
	 * @return Start-time where room is used (weekly).
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdRaumplanExtern.dtmBeginn</Code>
	 **/
	public String getBeginn(){
		return this.m_tBeginn;
	}

	/**
	 * Start-time where room is used (weekly).
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdRaumplanExtern.dtmBeginn</Code>
	 **/	
	public void setBeginn(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_tBeginn));
		this.m_tBeginn=value;
	}
	

	/**
	 * Stop-time where room is used (weekly).
	 * @return Stop-time where room is used (weekly).
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdRaumplanExtern.dtmEnde</Code>
	 **/
	public String getEnde(){
		return this.m_tEnde;
	}

	/**
	 * Stop-time where room is used (weekly).
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdRaumplanExtern.dtmEnde</Code>
	 **/	
	public void setEnde(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_tEnde));
		this.m_tEnde=value;
	}
	

	/**
	 * Name of teacher/person who uses this room.
	 * @return Name of teacher/person who uses this room.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdRaumplanExtern.strName</Code>
	 **/
	public String getName(){
		return this.m_sName;
	}

	/**
	 * Name of teacher/person who uses this room.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdRaumplanExtern.strName</Code>
	 **/	
	public void setName(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sName)));
		this.m_sName=value;
	}
	

	/**
	 * Name of coursetype the room is used for.
	 * @return Name of coursetype the room is used for.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdRaumplanExtern.strKurstypBezeichnung</Code>
	 **/
	public String getKurstypBezeichnung(){
		return this.m_sKurstypBezeichnung;
	}

	/**
	 * Name of coursetype the room is used for.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdRaumplanExtern.strKurstypBezeichnung</Code>
	 **/	
	public void setKurstypBezeichnung(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKurstypBezeichnung)));
		this.m_sKurstypBezeichnung=value;
	}
	

	/**
	 * Is this usage in the current or the future semester?
	 * @return Is this usage in the current or the future semester?
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdRaumplanExtern.blnPlanung</Code>
	 **/
	public boolean getPlanung(){
		return this.m_bPlanung;
	}

	/**
	 * Is this usage in the current or the future semester?
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdRaumplanExtern.blnPlanung</Code>
	 **/	
	public void setPlanung(boolean value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_bPlanung));
		this.m_bPlanung=value;
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
			"<LfdNr>" + m_lLfdNr + "</LfdNr>"  + 
			"<Raum>" + m_sRaum + "</Raum>"  + 
			"<Tag>" + m_sTag + "</Tag>"  + 
			"<Beginn>" + m_tBeginn + "</Beginn>"  + 
			"<Ende>" + m_tEnde + "</Ende>"  + 
			"<Name>" + m_sName + "</Name>"  + 
			"<KurstypBezeichnung>" + m_sKurstypBezeichnung + "</KurstypBezeichnung>"  + 
			"<Planung>" + m_bPlanung + "</Planung>" ;
	}

////////////////////////////////////////////////////////////////
// 4.   S Q L  U T I L I T I E S
////////////////////////////////////////////////////////////////


	/**
	 * Where-SQL für eindeutigen Index dieses Objekte in der Datenbank (in Tabelle 'tblBdRaumplanExtern.').
	 * @return SQL-String wie z.B. "Id1=? and Id2=?"
	 **/
	private String getSQLWhereClause(){
		return 
			"\"lngSdSeminarID\"=? AND " + 
			"\"lngLfdNr\"=?";
	}

	/**
	 * SQL-Befehl zum Löschen des Objekts in der Datenbank (aus Tabelle 'tblBdRaumplanExtern.')
	 * @return SQL-String zum Löschen des Objekts, z.B. "delete from T1 where (Id1=?);"
	 **/
	private String toDBDeleteString(){
		return "delete from \"tblBdRaumplanExtern\" where ( " + this.getSQLWhereClause() + ");";
	}

	/**
 	 * Setzt die Werte für alle Platzhalter in {@link #getSQLWhereClause()}.
	 * @param ii Offset für die Parameter (inklusive).
	 * @throws SQLException 
	 **/
	private void pokeWhere(int ii, PreparedStatement ps) throws SQLException{
		
		ps.setLong(ii++, m_lSdSeminarID);
		ps.setLong(ii++, m_lLfdNr);
	}

	/**
  	 * Füllt das SQL-Statement mit den aktuellen Werten des Objekts
	 * @throws SQLException
	 **/
	private void pokeStatement(PreparedStatement ps) throws SQLException{
		
		ps.setLong(1, m_lSdSeminarID);
		ps.setLong(2, m_lLfdNr);
		ps.setString(3, m_sRaum);
		ps.setString(4, m_sTag);
		ps.setString(5, m_tBeginn);
		ps.setString(6, m_tEnde);
		ps.setString(7, m_sName);
		ps.setString(8, m_sKurstypBezeichnung);
		ps.setBoolean(9, m_bPlanung);

	}

	/**
	 * SQL-Befehl zum Speichern der Objekteigenschaften in die Datenbank (Tabelle 'tblBdRaumplanExtern.')
	 * @return PreparedStatemtent, das nach Aufruf von {@link #pokeStatement(PreparedStatement)} und {@link #execute()} das Update ausführt.
	 **/
	private String toDBUpdateString(){
		return "update \"tblBdRaumplanExtern\" set " +
			"\"lngSdSeminarID\"=?, " +
			"\"lngLfdNr\"=?, " +
			"\"strRaum\"=?, " +
			"\"strTag\"=?, " +
			"\"dtmBeginn\"=?, " +
			"\"dtmEnde\"=?, " +
			"\"strName\"=?, " +
			"\"strKurstypBezeichnung\"=?, " +
			"\"blnPlanung\"=?" +
			" where (" + this.getSQLWhereClause() + ");";
	}

	/**
	 * SQL-Befehl zum Hinzufügen des Objekts in die Datenbank (Tabelle 'tblBdRaumplanExtern.')
	 * @return SQL-String "insert into...".
	 **/
	private String toDBAddString(){
		return "insert into \"tblBdRaumplanExtern\" ( " +
			"\"lngSdSeminarID\", \"lngLfdNr\", \"strRaum\", \"strTag\", \"dtmBeginn\", \"dtmEnde\", \"strName\", \"strKurstypBezeichnung\", \"blnPlanung\" ) VALUES ( ?,?,?,?,?,?,?,?,?);";
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
	private void init(long lngSdSeminarID, long lngLfdNr) throws SQLException, NamingException{

		this.m_lSdSeminarID=lngSdSeminarID;

		this.m_lLfdNr=lngLfdNr;

		
		PreparedStatement pstm = prepareStatement("select * from \"tblBdRaumplanExtern\" where (" + this.getSQLWhereClause() + ");");
		pokeWhere(1,pstm);

		ResultSet rst=pstm.executeQuery();
		if(rst.next()) this.initByRst(rst);
		rst.close();
		this.disconnect();
		rst=null;
	}	
	
	/**
	 * Lade die Objekteigenschaften aus einem ResultSet.
	 * param rst ResultSet mit allen Spalten der Tabelle 'tblBdRaumplanExtern'
	 * @throws SQLException 
	 **/
	protected void initByRst(ResultSet rst) throws SQLException{
		this.m_lSdSeminarID=rst.getLong("lngSdSeminarID");
		this.m_lLfdNr=rst.getLong("lngLfdNr");
		this.m_sRaum=rst.getString("strRaum");
		this.m_sTag=rst.getString("strTag");
		this.m_tBeginn=rst.getString("dtmBeginn");
		this.m_tEnde=rst.getString("dtmEnde");
		this.m_sName=rst.getString("strName");
		this.m_sKurstypBezeichnung=rst.getString("strKurstypBezeichnung");
		this.m_bPlanung=rst.getBoolean("blnPlanung");	
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
	public RaumplanExtern(){}	
	
	/**
	 * Konstruktor mit Indexwerten (eindeutig) der assoziierten Datenbanktabelle
	 * @throws NamingException 
	 * @throws SQLException 
	 **/
	public RaumplanExtern(long lngSdSeminarID, long lngLfdNr) throws SQLException, NamingException{
		this.init(lngSdSeminarID, lngLfdNr);
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
	public RaumplanExtern(ResultSet rst) throws SQLException{
		this.initByRst(rst);
		this.m_bIsDirty = false;
	}
  }//Klassenende
