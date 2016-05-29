
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
 *  Module: Gvp
This is part of the _Geschaeftsverteilungsplan_ -- the mapping of tasks to people. This is what made German administration big :-)
This table (or class) is an n:m map of people to tasks with the additional option to qualify an entry with a timestamp (a semester). This makes it easier to keep track of changes. On the other hand, it creates additional update-necessities. Personally, I use the table with a null-semester.
 *  
 *  ALLGEMEINE INFORMATION ZU DEN OBJEKTEN DIESES PACKAGES:
 *  Diese Klasse kapselt die Tabelle 'AufgabenHistory' in 
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
public class AufgabenHistory extends shjCore{

	private static final long serialVersionUID = 7L;

////////////////////////////////////////////////////////////////
// 1.   P R I V A T E   D E K L A R A T I O N E N
////////////////////////////////////////////////////////////////
	
	private boolean m_bIsDirty = false;

	private long m_lSdSeminarID;
	private long m_lSdDozentID;
	private String m_sBdSemester;
	private long m_lSdAufgabenID;
	private String m_sBdBemerkung;

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
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdAufgabenHistory.lngSdSeminarID</Code>
	 **/
	public long getSdSeminarID(){
		return this.m_lSdSeminarID;
	}

	/**
	 * Seminar
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdAufgabenHistory.lngSdSeminarID</Code>
	 **/	
	public void setSdSeminarID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lSdSeminarID));
		this.m_lSdSeminarID=value;
	}
	

	/**
	 * Id of teacher who had the task (see AufgabenID) in the given semester (see semester).
	 * @return Id of teacher who had the task (see AufgabenID) in the given semester (see semester).
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdAufgabenHistory.lngSdDozentID</Code>
	 **/
	public long getSdDozentID(){
		return this.m_lSdDozentID;
	}

	/**
	 * Id of teacher who had the task (see AufgabenID) in the given semester (see semester).
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdAufgabenHistory.lngSdDozentID</Code>
	 **/	
	public void setSdDozentID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lSdDozentID));
		this.m_lSdDozentID=value;
	}
	

	/**
	 * What semester are we talking about?
This is optional if you want to keep track of changes.
	 * @return What semester are we talking about?
This is optional if you want to keep track of changes.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdAufgabenHistory.strBdSemester</Code>
	 **/
	public String getBdSemester(){
		return this.m_sBdSemester;
	}

	/**
	 * What semester are we talking about?
This is optional if you want to keep track of changes.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdAufgabenHistory.strBdSemester</Code>
	 **/	
	public void setBdSemester(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sBdSemester)));
		this.m_sBdSemester=value;
	}
	

	/**
	 * Id of the task (German _Aufgabe_) that the teacher (specified through DozentID) was assigned in the semester.
	 * @return Id of the task (German _Aufgabe_) that the teacher (specified through DozentID) was assigned in the semester.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdAufgabenHistory.lngSdAufgabenID</Code>
	 **/
	public long getSdAufgabenID(){
		return this.m_lSdAufgabenID;
	}

	/**
	 * Id of the task (German _Aufgabe_) that the teacher (specified through DozentID) was assigned in the semester.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdAufgabenHistory.lngSdAufgabenID</Code>
	 **/	
	public void setSdAufgabenID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lSdAufgabenID));
		this.m_lSdAufgabenID=value;
	}
	

	/**
	 * Remark. If you use the semester-option, sensible remarks are New, or Took over from xyz.
	 * @return Remark. If you use the semester-option, sensible remarks are New, or Took over from xyz.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdAufgabenHistory.strBdBemerkung</Code>
	 **/
	public String getBdBemerkung(){
		return this.m_sBdBemerkung;
	}

	/**
	 * Remark. If you use the semester-option, sensible remarks are New, or Took over from xyz.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdAufgabenHistory.strBdBemerkung</Code>
	 **/	
	public void setBdBemerkung(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sBdBemerkung)));
		this.m_sBdBemerkung=value;
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
			"<SdDozentID>" + m_lSdDozentID + "</SdDozentID>"  + 
			"<BdSemester>" + m_sBdSemester + "</BdSemester>"  + 
			"<SdAufgabenID>" + m_lSdAufgabenID + "</SdAufgabenID>"  + 
			"<BdBemerkung>" + m_sBdBemerkung + "</BdBemerkung>" ;
	}

////////////////////////////////////////////////////////////////
// 4.   S Q L  U T I L I T I E S
////////////////////////////////////////////////////////////////


	/**
	 * Where-SQL für eindeutigen Index dieses Objekte in der Datenbank (in Tabelle 'tblBdAufgabenHistory.').
	 * @return SQL-String wie z.B. "Id1=? and Id2=?"
	 **/
	private String getSQLWhereClause(){
		return 
			"\"lngSdSeminarID\"=? AND " + 
			"\"strBdSemester\"=? AND " + 
			"\"lngSdDozentID\"=? AND " + 
			"\"lngSdAufgabenID\"=?";
	}

	/**
	 * SQL-Befehl zum Löschen des Objekts in der Datenbank (aus Tabelle 'tblBdAufgabenHistory.')
	 * @return SQL-String zum Löschen des Objekts, z.B. "delete from T1 where (Id1=?);"
	 **/
	private String toDBDeleteString(){
		return "delete from \"tblBdAufgabenHistory\" where ( " + this.getSQLWhereClause() + ");";
	}

	/**
 	 * Setzt die Werte für alle Platzhalter in {@link #getSQLWhereClause()}.
	 * @param ii Offset für die Parameter (inklusive).
	 * @throws SQLException 
	 **/
	private void pokeWhere(int ii, PreparedStatement ps) throws SQLException{
		
		ps.setLong(ii++, m_lSdSeminarID);
		ps.setString(ii++, m_sBdSemester);
		ps.setLong(ii++, m_lSdDozentID);
		ps.setLong(ii++, m_lSdAufgabenID);
	}

	/**
  	 * Füllt das SQL-Statement mit den aktuellen Werten des Objekts
	 * @throws SQLException
	 **/
	private void pokeStatement(PreparedStatement ps) throws SQLException{
		
		ps.setLong(1, m_lSdSeminarID);
		ps.setLong(2, m_lSdDozentID);
		ps.setString(3, m_sBdSemester);
		ps.setLong(4, m_lSdAufgabenID);
		ps.setString(5, m_sBdBemerkung);

	}

	/**
	 * SQL-Befehl zum Speichern der Objekteigenschaften in die Datenbank (Tabelle 'tblBdAufgabenHistory.')
	 * @return PreparedStatemtent, das nach Aufruf von {@link #pokeStatement(PreparedStatement)} und {@link #execute()} das Update ausführt.
	 **/
	private String toDBUpdateString(){
		return "update \"tblBdAufgabenHistory\" set " +
			"\"lngSdSeminarID\"=?, " +
			"\"lngSdDozentID\"=?, " +
			"\"strBdSemester\"=?, " +
			"\"lngSdAufgabenID\"=?, " +
			"\"strBdBemerkung\"=?" +
			" where (" + this.getSQLWhereClause() + ");";
	}

	/**
	 * SQL-Befehl zum Hinzufügen des Objekts in die Datenbank (Tabelle 'tblBdAufgabenHistory.')
	 * @return SQL-String "insert into...".
	 **/
	private String toDBAddString(){
		return "insert into \"tblBdAufgabenHistory\" ( " +
			"\"lngSdSeminarID\", \"lngSdDozentID\", \"strBdSemester\", \"lngSdAufgabenID\", \"strBdBemerkung\" ) VALUES ( ?,?,?,?,?);";
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
	private void init(long lngSdSeminarID, String strBdSemester, long lngSdDozentID, long lngSdAufgabenID) throws SQLException, NamingException{

		this.m_lSdSeminarID=lngSdSeminarID;

		this.m_sBdSemester=strBdSemester;

		this.m_lSdDozentID=lngSdDozentID;

		this.m_lSdAufgabenID=lngSdAufgabenID;

		
		PreparedStatement pstm = prepareStatement("select * from \"tblBdAufgabenHistory\" where (" + this.getSQLWhereClause() + ");");
		pokeWhere(1,pstm);

		ResultSet rst=pstm.executeQuery();
		if(rst.next()) this.initByRst(rst);
		rst.close();
		this.disconnect();
		rst=null;
	}	
	
	/**
	 * Lade die Objekteigenschaften aus einem ResultSet.
	 * param rst ResultSet mit allen Spalten der Tabelle 'tblBdAufgabenHistory'
	 * @throws SQLException 
	 **/
	protected void initByRst(ResultSet rst) throws SQLException{
		this.m_lSdSeminarID=rst.getLong("lngSdSeminarID");
		this.m_lSdDozentID=rst.getLong("lngSdDozentID");
		this.m_sBdSemester=rst.getString("strBdSemester");
		this.m_lSdAufgabenID=rst.getLong("lngSdAufgabenID");
		this.m_sBdBemerkung=rst.getString("strBdBemerkung");	
	}	
	
	/**
	 * Lade die Objekteigenschaften aus einer XML-Node.
	 * param node XML-Node mit allen Eigenschaften als Tags.
	 * @throws ParseException (Datum muss im ISO-Format yyyy-MM-dd übergeben werden).
	 **/
	private void initByNode(Node node) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		this.m_lSdSeminarID=Long.parseLong(shjNodeValue(node, "SdSeminarID"));
		this.m_lSdDozentID=Long.parseLong(shjNodeValue(node, "SdDozentID"));
		this.m_sBdSemester=(shjNodeValue(node, "BdSemester"));
		this.m_lSdAufgabenID=Long.parseLong(shjNodeValue(node, "SdAufgabenID"));
		this.m_sBdBemerkung=(shjNodeValue(node, "BdBemerkung"));
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
		pokeWhere(6,pstm);
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
	public AufgabenHistory(){}	
	
	/**
	 * Konstruktor mit Indexwerten (eindeutig) der assoziierten Datenbanktabelle
	 * @throws NamingException 
	 * @throws SQLException 
	 **/
	public AufgabenHistory(long lngSdSeminarID, String strBdSemester, long lngSdDozentID, long lngSdAufgabenID) throws SQLException, NamingException{
		this.init(lngSdSeminarID, strBdSemester, lngSdDozentID, lngSdAufgabenID);
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
	public AufgabenHistory(ResultSet rst) throws SQLException{
		this.initByRst(rst);
		this.m_bIsDirty = false;
	}

	/**
	 * Konstruktor per XML-Darstellung des Objekts.
	 * @throws ParseException, if a date can't be read.
	 **/
	public AufgabenHistory(Node node) throws ParseException{
		this.initByNode(node);
		this.m_bIsDirty = false;
	}

  }//Klassenende
