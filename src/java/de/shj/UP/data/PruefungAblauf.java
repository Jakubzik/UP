
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
 *  Module: Exam. Utility table save workflow bits in connection with an exam. In its twin table, tblBdPruefungAblauf, the specific case can be checked for its workflow. (Mind the difference in the Bd-table and the Sd-table. Sd stores the configuration, Bd stores the specific case of a certain student).
 *  
 *  ALLGEMEINE INFORMATION ZU DEN OBJEKTEN DIESES PACKAGES:
 *  Diese Klasse kapselt die Tabelle 'PruefungAblauf' in 
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
public class PruefungAblauf extends shjCore{

	private static final long serialVersionUID = 7L;

////////////////////////////////////////////////////////////////
// 1.   P R I V A T E   D E K L A R A T I O N E N
////////////////////////////////////////////////////////////////
	
	private boolean m_bIsDirty = false;

	private long m_lSdSeminarID;
	private long m_lPruefungID;
	private long m_lAblaufID;
	private String m_sPruefungAblaufBezeichnung;
	private String m_sPruefungAblaufBeschreibung;
	private boolean m_bPruefungAblaufPrereq;
	private boolean m_bPruefungAblaufStudentinfo;
	private String m_sCustom1;
	private String m_sCustom2;
	private String m_sCustom3;

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
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefungAblauf.lngSdSeminarID</Code>
	 **/
	public long getSdSeminarID(){
		return this.m_lSdSeminarID;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefungAblauf.lngSdSeminarID</Code>
	 **/	
	public void setSdSeminarID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lSdSeminarID));
		this.m_lSdSeminarID=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefungAblauf.lngPruefungID</Code>
	 **/
	public long getPruefungID(){
		return this.m_lPruefungID;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefungAblauf.lngPruefungID</Code>
	 **/	
	public void setPruefungID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lPruefungID));
		this.m_lPruefungID=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefungAblauf.lngAblaufID</Code>
	 **/
	public long getAblaufID(){
		return this.m_lAblaufID;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefungAblauf.lngAblaufID</Code>
	 **/	
	public void setAblaufID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lAblaufID));
		this.m_lAblaufID=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefungAblauf.strPruefungAblaufBezeichnung</Code>
	 **/
	public String getPruefungAblaufBezeichnung(){
		return this.m_sPruefungAblaufBezeichnung;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefungAblauf.strPruefungAblaufBezeichnung</Code>
	 **/	
	public void setPruefungAblaufBezeichnung(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sPruefungAblaufBezeichnung)));
		this.m_sPruefungAblaufBezeichnung=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefungAblauf.strPruefungAblaufBeschreibung</Code>
	 **/
	public String getPruefungAblaufBeschreibung(){
		return this.m_sPruefungAblaufBeschreibung;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefungAblauf.strPruefungAblaufBeschreibung</Code>
	 **/	
	public void setPruefungAblaufBeschreibung(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sPruefungAblaufBeschreibung)));
		this.m_sPruefungAblaufBeschreibung=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefungAblauf.blnPruefungAblaufPrereq</Code>
	 **/
	public boolean getPruefungAblaufPrereq(){
		return this.m_bPruefungAblaufPrereq;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefungAblauf.blnPruefungAblaufPrereq</Code>
	 **/	
	public void setPruefungAblaufPrereq(boolean value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_bPruefungAblaufPrereq));
		this.m_bPruefungAblaufPrereq=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefungAblauf.blnPruefungAblaufStudentinfo</Code>
	 **/
	public boolean getPruefungAblaufStudentinfo(){
		return this.m_bPruefungAblaufStudentinfo;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefungAblauf.blnPruefungAblaufStudentinfo</Code>
	 **/	
	public void setPruefungAblaufStudentinfo(boolean value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_bPruefungAblaufStudentinfo));
		this.m_bPruefungAblaufStudentinfo=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefungAblauf.strCustom1</Code>
	 **/
	public String getCustom1(){
		return this.m_sCustom1;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefungAblauf.strCustom1</Code>
	 **/	
	public void setCustom1(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sCustom1)));
		this.m_sCustom1=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefungAblauf.strCustom2</Code>
	 **/
	public String getCustom2(){
		return this.m_sCustom2;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefungAblauf.strCustom2</Code>
	 **/	
	public void setCustom2(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sCustom2)));
		this.m_sCustom2=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefungAblauf.strCustom3</Code>
	 **/
	public String getCustom3(){
		return this.m_sCustom3;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdPruefungAblauf.strCustom3</Code>
	 **/	
	public void setCustom3(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sCustom3)));
		this.m_sCustom3=value;
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
			"<AblaufID>" + m_lAblaufID + "</AblaufID>"  + 
			"<PruefungAblaufBezeichnung>" + m_sPruefungAblaufBezeichnung + "</PruefungAblaufBezeichnung>"  + 
			"<PruefungAblaufBeschreibung>" + m_sPruefungAblaufBeschreibung + "</PruefungAblaufBeschreibung>"  + 
			"<PruefungAblaufPrereq>" + m_bPruefungAblaufPrereq + "</PruefungAblaufPrereq>"  + 
			"<PruefungAblaufStudentinfo>" + m_bPruefungAblaufStudentinfo + "</PruefungAblaufStudentinfo>"  + 
			"<Custom1>" + m_sCustom1 + "</Custom1>"  + 
			"<Custom2>" + m_sCustom2 + "</Custom2>"  + 
			"<Custom3>" + m_sCustom3 + "</Custom3>" ;
	}

////////////////////////////////////////////////////////////////
// 4.   S Q L  U T I L I T I E S
////////////////////////////////////////////////////////////////


	/**
	 * Where-SQL für eindeutigen Index dieses Objekte in der Datenbank (in Tabelle 'tblSdPruefungAblauf.').
	 * @return SQL-String wie z.B. "Id1=? and Id2=?"
	 **/
	private String getSQLWhereClause(){
		return 
			"\"lngSdSeminarID\"=? AND " + 
			"\"lngPruefungID\"=? AND " + 
			"\"lngAblaufID\"=?";
	}

	/**
	 * SQL-Befehl zum Löschen des Objekts in der Datenbank (aus Tabelle 'tblSdPruefungAblauf.')
	 * @return SQL-String zum Löschen des Objekts, z.B. "delete from T1 where (Id1=?);"
	 **/
	private String toDBDeleteString(){
		return "delete from \"tblSdPruefungAblauf\" where ( " + this.getSQLWhereClause() + ");";
	}

	/**
 	 * Setzt die Werte für alle Platzhalter in {@link #getSQLWhereClause()}.
	 * @param ii Offset für die Parameter (inklusive).
	 * @throws SQLException 
	 **/
	private void pokeWhere(int ii, PreparedStatement ps) throws SQLException{
		
		ps.setLong(ii++, m_lSdSeminarID);
		ps.setLong(ii++, m_lPruefungID);
		ps.setLong(ii++, m_lAblaufID);
	}

	/**
  	 * Füllt das SQL-Statement mit den aktuellen Werten des Objekts
	 * @throws SQLException
	 **/
	private void pokeStatement(PreparedStatement ps) throws SQLException{
		
		ps.setLong(1, m_lSdSeminarID);
		ps.setLong(2, m_lPruefungID);
		ps.setLong(3, m_lAblaufID);
		ps.setString(4, m_sPruefungAblaufBezeichnung);
		ps.setString(5, m_sPruefungAblaufBeschreibung);
		ps.setBoolean(6, m_bPruefungAblaufPrereq);
		ps.setBoolean(7, m_bPruefungAblaufStudentinfo);
		ps.setString(8, m_sCustom1);
		ps.setString(9, m_sCustom2);
		ps.setString(10, m_sCustom3);

	}

	/**
	 * SQL-Befehl zum Speichern der Objekteigenschaften in die Datenbank (Tabelle 'tblSdPruefungAblauf.')
	 * @return PreparedStatemtent, das nach Aufruf von {@link #pokeStatement(PreparedStatement)} und {@link #execute()} das Update ausführt.
	 **/
	private String toDBUpdateString(){
		return "update \"tblSdPruefungAblauf\" set " +
			"\"lngSdSeminarID\"=?, " +
			"\"lngPruefungID\"=?, " +
			"\"lngAblaufID\"=?, " +
			"\"strPruefungAblaufBezeichnung\"=?, " +
			"\"strPruefungAblaufBeschreibung\"=?, " +
			"\"blnPruefungAblaufPrereq\"=?, " +
			"\"blnPruefungAblaufStudentinfo\"=?, " +
			"\"strCustom1\"=?, " +
			"\"strCustom2\"=?, " +
			"\"strCustom3\"=?" +
			" where (" + this.getSQLWhereClause() + ");";
	}

	/**
	 * SQL-Befehl zum Hinzufügen des Objekts in die Datenbank (Tabelle 'tblSdPruefungAblauf.')
	 * @return SQL-String "insert into...".
	 **/
	private String toDBAddString(){
		return "insert into \"tblSdPruefungAblauf\" ( " +
			"\"lngSdSeminarID\", \"lngPruefungID\", \"lngAblaufID\", \"strPruefungAblaufBezeichnung\", \"strPruefungAblaufBeschreibung\", \"blnPruefungAblaufPrereq\", \"blnPruefungAblaufStudentinfo\", \"strCustom1\", \"strCustom2\", \"strCustom3\" ) VALUES ( ?,?,?,?,?,?,?,?,?,?);";
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
	private void init(long lngSdSeminarID, long lngPruefungID, long lngAblaufID) throws SQLException, NamingException{

		this.m_lSdSeminarID=lngSdSeminarID;

		this.m_lPruefungID=lngPruefungID;

		this.m_lAblaufID=lngAblaufID;

		
		PreparedStatement pstm = prepareStatement("select * from \"tblSdPruefungAblauf\" where (" + this.getSQLWhereClause() + ");");
		pokeWhere(1,pstm);

		ResultSet rst=pstm.executeQuery();
		if(rst.next()) this.initByRst(rst);
		rst.close();
		this.disconnect();
		rst=null;
	}	
	
	/**
	 * Lade die Objekteigenschaften aus einem ResultSet.
	 * param rst ResultSet mit allen Spalten der Tabelle 'tblSdPruefungAblauf'
	 * @throws SQLException 
	 **/
	protected void initByRst(ResultSet rst) throws SQLException{
		this.m_lSdSeminarID=rst.getLong("lngSdSeminarID");
		this.m_lPruefungID=rst.getLong("lngPruefungID");
		this.m_lAblaufID=rst.getLong("lngAblaufID");
		this.m_sPruefungAblaufBezeichnung=rst.getString("strPruefungAblaufBezeichnung");
		this.m_sPruefungAblaufBeschreibung=rst.getString("strPruefungAblaufBeschreibung");
		this.m_bPruefungAblaufPrereq=rst.getBoolean("blnPruefungAblaufPrereq");
		this.m_bPruefungAblaufStudentinfo=rst.getBoolean("blnPruefungAblaufStudentinfo");
		this.m_sCustom1=rst.getString("strCustom1");
		this.m_sCustom2=rst.getString("strCustom2");
		this.m_sCustom3=rst.getString("strCustom3");	
	}	
	
	/**
	 * Lade die Objekteigenschaften aus einer XML-Node.
	 * param node XML-Node mit allen Eigenschaften als Tags.
	 * @throws ParseException (Datum muss im ISO-Format yyyy-MM-dd übergeben werden).
	 **/
	private void initByNode(Node node) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		this.m_lSdSeminarID=Long.parseLong(shjNodeValue(node, "SdSeminarID"));
		this.m_lPruefungID=Long.parseLong(shjNodeValue(node, "PruefungID"));
		this.m_lAblaufID=Long.parseLong(shjNodeValue(node, "AblaufID"));
		this.m_sPruefungAblaufBezeichnung=(shjNodeValue(node, "PruefungAblaufBezeichnung"));
		this.m_sPruefungAblaufBeschreibung=(shjNodeValue(node, "PruefungAblaufBeschreibung"));
		this.m_bPruefungAblaufPrereq=Boolean.valueOf(shjNodeValue(node, "PruefungAblaufPrereq")).booleanValue();
		this.m_bPruefungAblaufStudentinfo=Boolean.valueOf(shjNodeValue(node, "PruefungAblaufStudentinfo")).booleanValue();
		this.m_sCustom1=(shjNodeValue(node, "Custom1"));
		this.m_sCustom2=(shjNodeValue(node, "Custom2"));
		this.m_sCustom3=(shjNodeValue(node, "Custom3"));
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
		pokeWhere(11,pstm);
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
	public PruefungAblauf(){}	
	
	/**
	 * Konstruktor mit Indexwerten (eindeutig) der assoziierten Datenbanktabelle
	 * @throws NamingException 
	 * @throws SQLException 
	 **/
	public PruefungAblauf(long lngSdSeminarID, long lngPruefungID, long lngAblaufID) throws SQLException, NamingException{
		this.init(lngSdSeminarID, lngPruefungID, lngAblaufID);
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
	public PruefungAblauf(ResultSet rst) throws SQLException{
		this.initByRst(rst);
		this.m_bIsDirty = false;
	}

	/**
	 * Konstruktor per XML-Darstellung des Objekts.
	 * @throws ParseException, if a date can't be read.
	 **/
	public PruefungAblauf(Node node) throws ParseException{
		this.initByNode(node);
		this.m_bIsDirty = false;
	}

  }//Klassenende
