
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
 *  Module: Gvp
This table/class is about tasks that need to be distributed to run a seminar.
 *  
 *  ALLGEMEINE INFORMATION ZU DEN OBJEKTEN DIESES PACKAGES:
 *  Diese Klasse kapselt die Tabelle 'Aufgabe' in 
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
public class Aufgabe extends shjCore{

	private static final long serialVersionUID = 7L;

////////////////////////////////////////////////////////////////
// 1.   P R I V A T E   D E K L A R A T I O N E N
////////////////////////////////////////////////////////////////
	
	private boolean m_bIsDirty = false;

	private long m_lSdSeminarID;
	private long m_lAufgabenID;
	private String m_sAufgabenBezeichnung;
	private String m_sAufgabenBeschreibung;
	private long m_lAufgabeDeputatsreduktion;
	private String m_sAufgabeStichworte;
	private boolean m_bAufgabeShowInList;
	private String m_sAufgabeCustom1;
	private String m_sAufgabeCustom2;
	private String m_sAufgabeCustom3;

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
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdAufgabe.lngSdSeminarID</Code>
	 **/
	public long getSdSeminarID(){
		return this.m_lSdSeminarID;
	}

	/**
	 * Seminar
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdAufgabe.lngSdSeminarID</Code>
	 **/	
	public void setSdSeminarID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lSdSeminarID));
		this.m_lSdSeminarID=value;
	}
	

	/**
	 * Id of task.
	 * @return Id of task.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdAufgabe.lngAufgabenID</Code>
	 **/
	public long getAufgabenID(){
		return this.m_lAufgabenID;
	}

	/**
	 * Id of task.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdAufgabe.lngAufgabenID</Code>
	 **/	
	public void setAufgabenID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lAufgabenID));
		this.m_lAufgabenID=value;
	}
	

	/**
	 * Name of this task.
	 * @return Name of this task.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdAufgabe.strAufgabenBezeichnung</Code>
	 **/
	public String getAufgabenBezeichnung(){
		return this.m_sAufgabenBezeichnung;
	}

	/**
	 * Name of this task.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdAufgabe.strAufgabenBezeichnung</Code>
	 **/	
	public void setAufgabenBezeichnung(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sAufgabenBezeichnung)));
		this.m_sAufgabenBezeichnung=value;
	}
	

	/**
	 * Description of this task.
	 * @return Description of this task.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdAufgabe.strAufgabenBeschreibung</Code>
	 **/
	public String getAufgabenBeschreibung(){
		return this.m_sAufgabenBeschreibung;
	}

	/**
	 * Description of this task.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdAufgabe.strAufgabenBeschreibung</Code>
	 **/	
	public void setAufgabenBeschreibung(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sAufgabenBeschreibung)));
		this.m_sAufgabenBeschreibung=value;
	}
	

	/**
	 * Number of hours a member of faculty has to teach less for doing this task.
	 * @return Number of hours a member of faculty has to teach less for doing this task.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdAufgabe.intAufgabeDeputatsreduktion</Code>
	 **/
	public long getAufgabeDeputatsreduktion(){
		return this.m_lAufgabeDeputatsreduktion;
	}

	/**
	 * Number of hours a member of faculty has to teach less for doing this task.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdAufgabe.intAufgabeDeputatsreduktion</Code>
	 **/	
	public void setAufgabeDeputatsreduktion(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lAufgabeDeputatsreduktion));
		this.m_lAufgabeDeputatsreduktion=value;
	}
	

	/**
	 * Key words associated with this task that can be search-found from the internet.
	 * @return Key words associated with this task that can be search-found from the internet.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdAufgabe.strAufgabeStichworte</Code>
	 **/
	public String getAufgabeStichworte(){
		return this.m_sAufgabeStichworte;
	}

	/**
	 * Key words associated with this task that can be search-found from the internet.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdAufgabe.strAufgabeStichworte</Code>
	 **/	
	public void setAufgabeStichworte(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sAufgabeStichworte)));
		this.m_sAufgabeStichworte=value;
	}
	

	/**
	 * Should this task be displayed in a published list of tasks?
	 * @return Should this task be displayed in a published list of tasks?
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdAufgabe.blnAufgabeShowInList</Code>
	 **/
	public boolean getAufgabeShowInList(){
		return this.m_bAufgabeShowInList;
	}

	/**
	 * Should this task be displayed in a published list of tasks?
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdAufgabe.blnAufgabeShowInList</Code>
	 **/	
	public void setAufgabeShowInList(boolean value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_bAufgabeShowInList));
		this.m_bAufgabeShowInList=value;
	}
	

	/**
	 * Custom
	 * @return Custom
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdAufgabe.strAufgabeCustom1</Code>
	 **/
	public String getAufgabeCustom1(){
		return this.m_sAufgabeCustom1;
	}

	/**
	 * Custom
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdAufgabe.strAufgabeCustom1</Code>
	 **/	
	public void setAufgabeCustom1(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sAufgabeCustom1)));
		this.m_sAufgabeCustom1=value;
	}
	

	/**
	 * Custom
	 * @return Custom
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdAufgabe.strAufgabeCustom2</Code>
	 **/
	public String getAufgabeCustom2(){
		return this.m_sAufgabeCustom2;
	}

	/**
	 * Custom
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdAufgabe.strAufgabeCustom2</Code>
	 **/	
	public void setAufgabeCustom2(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sAufgabeCustom2)));
		this.m_sAufgabeCustom2=value;
	}
	

	/**
	 * Custom
	 * @return Custom
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdAufgabe.strAufgabeCustom3</Code>
	 **/
	public String getAufgabeCustom3(){
		return this.m_sAufgabeCustom3;
	}

	/**
	 * Custom
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdAufgabe.strAufgabeCustom3</Code>
	 **/	
	public void setAufgabeCustom3(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sAufgabeCustom3)));
		this.m_sAufgabeCustom3=value;
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
			"<AufgabenID>" + m_lAufgabenID + "</AufgabenID>"  + 
			"<AufgabenBezeichnung>" + m_sAufgabenBezeichnung + "</AufgabenBezeichnung>"  + 
			"<AufgabenBeschreibung>" + m_sAufgabenBeschreibung + "</AufgabenBeschreibung>"  + 
			"<AufgabeDeputatsreduktion>" + m_lAufgabeDeputatsreduktion + "</AufgabeDeputatsreduktion>"  + 
			"<AufgabeStichworte>" + m_sAufgabeStichworte + "</AufgabeStichworte>"  + 
			"<AufgabeShowInList>" + m_bAufgabeShowInList + "</AufgabeShowInList>"  + 
			"<AufgabeCustom1>" + m_sAufgabeCustom1 + "</AufgabeCustom1>"  + 
			"<AufgabeCustom2>" + m_sAufgabeCustom2 + "</AufgabeCustom2>"  + 
			"<AufgabeCustom3>" + m_sAufgabeCustom3 + "</AufgabeCustom3>" ;
	}

////////////////////////////////////////////////////////////////
// 4.   S Q L  U T I L I T I E S
////////////////////////////////////////////////////////////////


	/**
	 * Where-SQL für eindeutigen Index dieses Objekte in der Datenbank (in Tabelle 'tblSdAufgabe.').
	 * @return SQL-String wie z.B. "Id1=? and Id2=?"
	 **/
	private String getSQLWhereClause(){
		return 
			"\"lngSdSeminarID\"=? AND " + 
			"\"lngAufgabenID\"=?";
	}

	/**
	 * SQL-Befehl zum Löschen des Objekts in der Datenbank (aus Tabelle 'tblSdAufgabe.')
	 * @return SQL-String zum Löschen des Objekts, z.B. "delete from T1 where (Id1=?);"
	 **/
	private String toDBDeleteString(){
		return "delete from \"tblSdAufgabe\" where ( " + this.getSQLWhereClause() + ");";
	}

	/**
 	 * Setzt die Werte für alle Platzhalter in {@link #getSQLWhereClause()}.
	 * @param ii Offset für die Parameter (inklusive).
	 * @throws SQLException 
	 **/
	private void pokeWhere(int ii, PreparedStatement ps) throws SQLException{
		
		ps.setLong(ii++, m_lSdSeminarID);
		ps.setLong(ii++, m_lAufgabenID);
	}

	/**
  	 * Füllt das SQL-Statement mit den aktuellen Werten des Objekts
	 * @throws SQLException
	 **/
	private void pokeStatement(PreparedStatement ps) throws SQLException{
		
		ps.setLong(1, m_lSdSeminarID);
		ps.setLong(2, m_lAufgabenID);
		ps.setString(3, m_sAufgabenBezeichnung);
		ps.setString(4, m_sAufgabenBeschreibung);
		ps.setLong(5, m_lAufgabeDeputatsreduktion);
		ps.setString(6, m_sAufgabeStichworte);
		ps.setBoolean(7, m_bAufgabeShowInList);
		ps.setString(8, m_sAufgabeCustom1);
		ps.setString(9, m_sAufgabeCustom2);
		ps.setString(10, m_sAufgabeCustom3);

	}

	/**
	 * SQL-Befehl zum Speichern der Objekteigenschaften in die Datenbank (Tabelle 'tblSdAufgabe.')
	 * @return PreparedStatemtent, das nach Aufruf von {@link #pokeStatement(PreparedStatement)} und {@link #execute()} das Update ausführt.
	 **/
	private String toDBUpdateString(){
		return "update \"tblSdAufgabe\" set " +
			"\"lngSdSeminarID\"=?, " +
			"\"lngAufgabenID\"=?, " +
			"\"strAufgabenBezeichnung\"=?, " +
			"\"strAufgabenBeschreibung\"=?, " +
			"\"intAufgabeDeputatsreduktion\"=?, " +
			"\"strAufgabeStichworte\"=?, " +
			"\"blnAufgabeShowInList\"=?, " +
			"\"strAufgabeCustom1\"=?, " +
			"\"strAufgabeCustom2\"=?, " +
			"\"strAufgabeCustom3\"=?" +
			" where (" + this.getSQLWhereClause() + ");";
	}

	/**
	 * SQL-Befehl zum Hinzufügen des Objekts in die Datenbank (Tabelle 'tblSdAufgabe.')
	 * @return SQL-String "insert into...".
	 **/
	private String toDBAddString(){
		return "insert into \"tblSdAufgabe\" ( " +
			"\"lngSdSeminarID\", \"lngAufgabenID\", \"strAufgabenBezeichnung\", \"strAufgabenBeschreibung\", \"intAufgabeDeputatsreduktion\", \"strAufgabeStichworte\", \"blnAufgabeShowInList\", \"strAufgabeCustom1\", \"strAufgabeCustom2\", \"strAufgabeCustom3\" ) VALUES ( ?,?,?,?,?,?,?,?,?,?);";
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
	private void init(long lngSdSeminarID, long lngAufgabenID) throws SQLException, NamingException{

		this.m_lSdSeminarID=lngSdSeminarID;

		this.m_lAufgabenID=lngAufgabenID;

		
		PreparedStatement pstm = prepareStatement("select * from \"tblSdAufgabe\" where (" + this.getSQLWhereClause() + ");");
		pokeWhere(1,pstm);

		ResultSet rst=pstm.executeQuery();
		if(rst.next()) this.initByRst(rst);
		rst.close();
		this.disconnect();
		rst=null;
	}	
	
	/**
	 * Lade die Objekteigenschaften aus einem ResultSet.
	 * param rst ResultSet mit allen Spalten der Tabelle 'tblSdAufgabe'
	 * @throws SQLException 
	 **/
	protected void initByRst(ResultSet rst) throws SQLException{
		this.m_lSdSeminarID=rst.getLong("lngSdSeminarID");
		this.m_lAufgabenID=rst.getLong("lngAufgabenID");
		this.m_sAufgabenBezeichnung=rst.getString("strAufgabenBezeichnung");
		this.m_sAufgabenBeschreibung=rst.getString("strAufgabenBeschreibung");
		this.m_lAufgabeDeputatsreduktion=rst.getLong("intAufgabeDeputatsreduktion");
		this.m_sAufgabeStichworte=rst.getString("strAufgabeStichworte");
		this.m_bAufgabeShowInList=rst.getBoolean("blnAufgabeShowInList");
		this.m_sAufgabeCustom1=rst.getString("strAufgabeCustom1");
		this.m_sAufgabeCustom2=rst.getString("strAufgabeCustom2");
		this.m_sAufgabeCustom3=rst.getString("strAufgabeCustom3");	
	}	
	
	/**
	 * Lade die Objekteigenschaften aus einer XML-Node.
	 * param node XML-Node mit allen Eigenschaften als Tags.
	 * @throws ParseException (Datum muss im ISO-Format yyyy-MM-dd übergeben werden).
	 **/
	private void initByNode(Node node) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		this.m_lSdSeminarID=Long.parseLong(shjNodeValue(node, "SdSeminarID"));
		this.m_lAufgabenID=Long.parseLong(shjNodeValue(node, "AufgabenID"));
		this.m_sAufgabenBezeichnung=(shjNodeValue(node, "AufgabenBezeichnung"));
		this.m_sAufgabenBeschreibung=(shjNodeValue(node, "AufgabenBeschreibung"));
		this.m_lAufgabeDeputatsreduktion=Long.parseLong(shjNodeValue(node, "AufgabeDeputatsreduktion"));
		this.m_sAufgabeStichworte=(shjNodeValue(node, "AufgabeStichworte"));
		this.m_bAufgabeShowInList=Boolean.valueOf(shjNodeValue(node, "AufgabeShowInList")).booleanValue();
		this.m_sAufgabeCustom1=(shjNodeValue(node, "AufgabeCustom1"));
		this.m_sAufgabeCustom2=(shjNodeValue(node, "AufgabeCustom2"));
		this.m_sAufgabeCustom3=(shjNodeValue(node, "AufgabeCustom3"));
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
	public Aufgabe(){}	
	
	/**
	 * Konstruktor mit Indexwerten (eindeutig) der assoziierten Datenbanktabelle
	 * @throws NamingException 
	 * @throws SQLException 
	 **/
	public Aufgabe(long lngSdSeminarID, long lngAufgabenID) throws SQLException, NamingException{
		this.init(lngSdSeminarID, lngAufgabenID);
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
	public Aufgabe(ResultSet rst) throws SQLException{
		this.initByRst(rst);
		this.m_bIsDirty = false;
	}

	/**
	 * Konstruktor per XML-Darstellung des Objekts.
	 * @throws ParseException, if a date can't be read.
	 **/
	public Aufgabe(Node node) throws ParseException{
		this.initByNode(node);
		this.m_bIsDirty = false;
	}

  }//Klassenende
