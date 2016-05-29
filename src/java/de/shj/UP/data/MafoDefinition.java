
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
 *  Todo.
 *  
 *  ALLGEMEINE INFORMATION ZU DEN OBJEKTEN DIESES PACKAGES:
 *  Diese Klasse kapselt die Tabelle 'foDefinition' in 
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
public class MafoDefinition extends shjCore{

	private static final long serialVersionUID = 7L;

////////////////////////////////////////////////////////////////
// 1.   P R I V A T E   D E K L A R A T I O N E N
////////////////////////////////////////////////////////////////
	
	private boolean m_bIsDirty = false;

	private long m_lMafoID;
	private String m_sMafoName;
	private String m_sMafoKommentar;
	private Date m_dMafoStart;
	private Date m_dMafoStop;
	private int m_iMafoSemesterMin;
	private int m_iMafoSemesterMax;
	private String m_sMafoVeranstalter;
	private long m_lMafoRestrictUni;
	private long m_lMafoRestrictFaculty;
	private long m_lMafoRestrictSeminar;
	private int m_iMafoRestrictionIndex;

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
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblMafoDefinition.lngMafoID</Code>
	 **/
	public long getMafoID(){
		return this.m_lMafoID;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblMafoDefinition.lngMafoID</Code>
	 **/	
	public void setMafoID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lMafoID));
		this.m_lMafoID=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblMafoDefinition.strMafoName</Code>
	 **/
	public String getMafoName(){
		return this.m_sMafoName;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblMafoDefinition.strMafoName</Code>
	 **/	
	public void setMafoName(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sMafoName)));
		this.m_sMafoName=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblMafoDefinition.strMafoKommentar</Code>
	 **/
	public String getMafoKommentar(){
		return this.m_sMafoKommentar;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblMafoDefinition.strMafoKommentar</Code>
	 **/	
	public void setMafoKommentar(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sMafoKommentar)));
		this.m_sMafoKommentar=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblMafoDefinition.dtmMafoStart</Code>
	 **/
	public Date getMafoStart(){
		return this.m_dMafoStart;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblMafoDefinition.dtmMafoStart</Code>
	 **/	
	public void setMafoStart(Date value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_dMafoStart));
		this.m_dMafoStart=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblMafoDefinition.dtmMafoStop</Code>
	 **/
	public Date getMafoStop(){
		return this.m_dMafoStop;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblMafoDefinition.dtmMafoStop</Code>
	 **/	
	public void setMafoStop(Date value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_dMafoStop));
		this.m_dMafoStop=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblMafoDefinition.intMafoSemesterMin</Code>
	 **/
	public int getMafoSemesterMin(){
		return this.m_iMafoSemesterMin;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblMafoDefinition.intMafoSemesterMin</Code>
	 **/	
	public void setMafoSemesterMin(int value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_iMafoSemesterMin));
		this.m_iMafoSemesterMin=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblMafoDefinition.intMafoSemesterMax</Code>
	 **/
	public int getMafoSemesterMax(){
		return this.m_iMafoSemesterMax;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblMafoDefinition.intMafoSemesterMax</Code>
	 **/	
	public void setMafoSemesterMax(int value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_iMafoSemesterMax));
		this.m_iMafoSemesterMax=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblMafoDefinition.strMafoVeranstalter</Code>
	 **/
	public String getMafoVeranstalter(){
		return this.m_sMafoVeranstalter;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblMafoDefinition.strMafoVeranstalter</Code>
	 **/	
	public void setMafoVeranstalter(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sMafoVeranstalter)));
		this.m_sMafoVeranstalter=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblMafoDefinition.lngMafoRestrictUni</Code>
	 **/
	public long getMafoRestrictUni(){
		return this.m_lMafoRestrictUni;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblMafoDefinition.lngMafoRestrictUni</Code>
	 **/	
	public void setMafoRestrictUni(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lMafoRestrictUni));
		this.m_lMafoRestrictUni=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblMafoDefinition.lngMafoRestrictFaculty</Code>
	 **/
	public long getMafoRestrictFaculty(){
		return this.m_lMafoRestrictFaculty;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblMafoDefinition.lngMafoRestrictFaculty</Code>
	 **/	
	public void setMafoRestrictFaculty(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lMafoRestrictFaculty));
		this.m_lMafoRestrictFaculty=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblMafoDefinition.lngMafoRestrictSeminar</Code>
	 **/
	public long getMafoRestrictSeminar(){
		return this.m_lMafoRestrictSeminar;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblMafoDefinition.lngMafoRestrictSeminar</Code>
	 **/	
	public void setMafoRestrictSeminar(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lMafoRestrictSeminar));
		this.m_lMafoRestrictSeminar=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblMafoDefinition.intMafoRestrictionIndex</Code>
	 **/
	public int getMafoRestrictionIndex(){
		return this.m_iMafoRestrictionIndex;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblMafoDefinition.intMafoRestrictionIndex</Code>
	 **/	
	public void setMafoRestrictionIndex(int value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_iMafoRestrictionIndex));
		this.m_iMafoRestrictionIndex=value;
	}
	


////////////////////////////////////////////////////////////////
// 3.   X M L  U T I L I T I E S
////////////////////////////////////////////////////////////////
	
	/**
	 * @return Objekt als XML-String
	 **/
	public String toXMLString(){
		return 	

			"<MafoID>" + m_lMafoID + "</MafoID>"  + 
			"<MafoName>" + m_sMafoName + "</MafoName>"  + 
			"<MafoKommentar>" + m_sMafoKommentar + "</MafoKommentar>"  + 
			"<MafoStart>" + m_dMafoStart + "</MafoStart>"  + 
			"<MafoStop>" + m_dMafoStop + "</MafoStop>"  + 
			"<MafoSemesterMin>" + m_iMafoSemesterMin + "</MafoSemesterMin>"  + 
			"<MafoSemesterMax>" + m_iMafoSemesterMax + "</MafoSemesterMax>"  + 
			"<MafoVeranstalter>" + m_sMafoVeranstalter + "</MafoVeranstalter>"  + 
			"<MafoRestrictUni>" + m_lMafoRestrictUni + "</MafoRestrictUni>"  + 
			"<MafoRestrictFaculty>" + m_lMafoRestrictFaculty + "</MafoRestrictFaculty>"  + 
			"<MafoRestrictSeminar>" + m_lMafoRestrictSeminar + "</MafoRestrictSeminar>"  + 
			"<MafoRestrictionIndex>" + m_iMafoRestrictionIndex + "</MafoRestrictionIndex>" ;
	}

////////////////////////////////////////////////////////////////
// 4.   S Q L  U T I L I T I E S
////////////////////////////////////////////////////////////////


	/**
	 * Where-SQL für eindeutigen Index dieses Objekte in der Datenbank (in Tabelle 'tblMafoDefinition.').
	 * @return SQL-String wie z.B. "Id1=? and Id2=?"
	 **/
	private String getSQLWhereClause(){
		return 
			"\"lngMafoID\"=?";
	}

	/**
	 * SQL-Befehl zum Löschen des Objekts in der Datenbank (aus Tabelle 'tblMafoDefinition.')
	 * @return SQL-String zum Löschen des Objekts, z.B. "delete from T1 where (Id1=?);"
	 **/
	private String toDBDeleteString(){
		return "delete from \"tblMafoDefinition\" where ( " + this.getSQLWhereClause() + ");";
	}

	/**
 	 * Setzt die Werte für alle Platzhalter in {@link #getSQLWhereClause()}.
	 * @param ii Offset für die Parameter (inklusive).
	 * @throws SQLException 
	 **/
	private void pokeWhere(int ii, PreparedStatement ps) throws SQLException{
		
		ps.setLong(ii++, m_lMafoID);
	}

	/**
  	 * Füllt das SQL-Statement mit den aktuellen Werten des Objekts
	 * @throws SQLException
	 **/
	private void pokeStatement(PreparedStatement ps) throws SQLException{
		
		ps.setLong(1, m_lMafoID);
		ps.setString(2, m_sMafoName);
		ps.setString(3, m_sMafoKommentar);
		ps.setDate(4, m_dMafoStart);
		ps.setDate(5, m_dMafoStop);
		ps.setInt(6, m_iMafoSemesterMin);
		ps.setInt(7, m_iMafoSemesterMax);
		ps.setString(8, m_sMafoVeranstalter);
		ps.setLong(9, m_lMafoRestrictUni);
		ps.setLong(10, m_lMafoRestrictFaculty);
		ps.setLong(11, m_lMafoRestrictSeminar);
		ps.setInt(12, m_iMafoRestrictionIndex);

	}

	/**
	 * SQL-Befehl zum Speichern der Objekteigenschaften in die Datenbank (Tabelle 'tblMafoDefinition.')
	 * @return PreparedStatemtent, das nach Aufruf von {@link #pokeStatement(PreparedStatement)} und {@link #execute()} das Update ausführt.
	 **/
	private String toDBUpdateString(){
		return "update \"tblMafoDefinition\" set " +
			"\"lngMafoID\"=?, " +
			"\"strMafoName\"=?, " +
			"\"strMafoKommentar\"=?, " +
			"\"dtmMafoStart\"=?, " +
			"\"dtmMafoStop\"=?, " +
			"\"intMafoSemesterMin\"=?, " +
			"\"intMafoSemesterMax\"=?, " +
			"\"strMafoVeranstalter\"=?, " +
			"\"lngMafoRestrictUni\"=?, " +
			"\"lngMafoRestrictFaculty\"=?, " +
			"\"lngMafoRestrictSeminar\"=?, " +
			"\"intMafoRestrictionIndex\"=?" +
			" where (" + this.getSQLWhereClause() + ");";
	}

	/**
	 * SQL-Befehl zum Hinzufügen des Objekts in die Datenbank (Tabelle 'tblMafoDefinition.')
	 * @return SQL-String "insert into...".
	 **/
	private String toDBAddString(){
		return "insert into \"tblMafoDefinition\" ( " +
			"\"lngMafoID\", \"strMafoName\", \"strMafoKommentar\", \"dtmMafoStart\", \"dtmMafoStop\", \"intMafoSemesterMin\", \"intMafoSemesterMax\", \"strMafoVeranstalter\", \"lngMafoRestrictUni\", \"lngMafoRestrictFaculty\", \"lngMafoRestrictSeminar\", \"intMafoRestrictionIndex\" ) VALUES ( ?,?,?,?,?,?,?,?,?,?,?,?);";
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
	private void init(long lngMafoID) throws SQLException, NamingException{

		this.m_lMafoID=lngMafoID;

		
		PreparedStatement pstm = prepareStatement("select * from \"tblMafoDefinition\" where (" + this.getSQLWhereClause() + ");");
		pokeWhere(1,pstm);

		ResultSet rst=pstm.executeQuery();
		if(rst.next()) this.initByRst(rst);
		rst.close();
		this.disconnect();
		rst=null;
	}	
	
	/**
	 * Lade die Objekteigenschaften aus einem ResultSet.
	 * param rst ResultSet mit allen Spalten der Tabelle 'tblMafoDefinition'
	 * @throws SQLException 
	 **/
	protected void initByRst(ResultSet rst) throws SQLException{
		this.m_lMafoID=rst.getLong("lngMafoID");
		this.m_sMafoName=rst.getString("strMafoName");
		this.m_sMafoKommentar=rst.getString("strMafoKommentar");
		this.m_dMafoStart=rst.getDate("dtmMafoStart");
		this.m_dMafoStop=rst.getDate("dtmMafoStop");
		this.m_iMafoSemesterMin=rst.getInt("intMafoSemesterMin");
		this.m_iMafoSemesterMax=rst.getInt("intMafoSemesterMax");
		this.m_sMafoVeranstalter=rst.getString("strMafoVeranstalter");
		this.m_lMafoRestrictUni=rst.getLong("lngMafoRestrictUni");
		this.m_lMafoRestrictFaculty=rst.getLong("lngMafoRestrictFaculty");
		this.m_lMafoRestrictSeminar=rst.getLong("lngMafoRestrictSeminar");
		this.m_iMafoRestrictionIndex=rst.getInt("intMafoRestrictionIndex");	
	}	
	
	/**
	 * Lade die Objekteigenschaften aus einer XML-Node.
	 * param node XML-Node mit allen Eigenschaften als Tags.
	 * @throws ParseException (Datum muss im ISO-Format yyyy-MM-dd übergeben werden).
	 **/
	private void initByNode(Node node) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		this.m_lMafoID=Long.parseLong(shjNodeValue(node, "MafoID"));
		this.m_sMafoName=(shjNodeValue(node, "MafoName"));
		this.m_sMafoKommentar=(shjNodeValue(node, "MafoKommentar"));
		this.m_dMafoStart=(Date) (sdf.parse(shjNodeValue(node, "MafoStart")));
		this.m_dMafoStop=(Date) (sdf.parse(shjNodeValue(node, "MafoStop")));
		this.m_iMafoSemesterMin=Integer.parseInt(shjNodeValue(node, "MafoSemesterMin"));
		this.m_iMafoSemesterMax=Integer.parseInt(shjNodeValue(node, "MafoSemesterMax"));
		this.m_sMafoVeranstalter=(shjNodeValue(node, "MafoVeranstalter"));
		this.m_lMafoRestrictUni=Long.parseLong(shjNodeValue(node, "MafoRestrictUni"));
		this.m_lMafoRestrictFaculty=Long.parseLong(shjNodeValue(node, "MafoRestrictFaculty"));
		this.m_lMafoRestrictSeminar=Long.parseLong(shjNodeValue(node, "MafoRestrictSeminar"));
		this.m_iMafoRestrictionIndex=Integer.parseInt(shjNodeValue(node, "MafoRestrictionIndex"));
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
		pokeWhere(13,pstm);
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
	public MafoDefinition(){}	
	
	/**
	 * Konstruktor mit Indexwerten (eindeutig) der assoziierten Datenbanktabelle
	 * @throws NamingException 
	 * @throws SQLException 
	 **/
	public MafoDefinition(long lngMafoID) throws SQLException, NamingException{
		this.init(lngMafoID);
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
	public MafoDefinition(ResultSet rst) throws SQLException{
		this.initByRst(rst);
		this.m_bIsDirty = false;
	}

	/**
	 * Konstruktor per XML-Darstellung des Objekts.
	 * @throws ParseException, if a date can't be read.
	 **/
	public MafoDefinition(Node node) throws ParseException{
		this.initByNode(node);
		this.m_bIsDirty = false;
	}

  }//Klassenende
