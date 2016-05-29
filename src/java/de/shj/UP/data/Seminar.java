
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
 *  
 *  
 *  ALLGEMEINE INFORMATION ZU DEN OBJEKTEN DIESES PACKAGES:
 *  Diese Klasse kapselt die Tabelle 'Seminar' in 
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
public class Seminar extends shjCore{

	private static final long serialVersionUID = 7L;

////////////////////////////////////////////////////////////////
// 1.   P R I V A T E   D E K L A R A T I O N E N
////////////////////////////////////////////////////////////////
	
	private boolean m_bIsDirty = false;

	private long m_lUniID;
	private long m_lFakultaetID;
	private long m_lSeminarID;
	private String m_sSeminarShort;
	private String m_sSeminarUnivISID;
	private String m_sSeminarName;
	private String m_sSeminarLink;
	private boolean m_bSeminarNoteCboLocal;
	private String m_sSeminarCustom1;
	private String m_sSeminarCustom2;
	private String m_sSeminarCustom3;

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
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdSeminar.lngUniID</Code>
	 **/
	public long getUniID(){
		return this.m_lUniID;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdSeminar.lngUniID</Code>
	 **/	
	public void setUniID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lUniID));
		this.m_lUniID=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdSeminar.lngFakultaetID</Code>
	 **/
	public long getFakultaetID(){
		return this.m_lFakultaetID;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdSeminar.lngFakultaetID</Code>
	 **/	
	public void setFakultaetID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lFakultaetID));
		this.m_lFakultaetID=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdSeminar.lngSeminarID</Code>
	 **/
	public long getSeminarID(){
		return this.m_lSeminarID;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdSeminar.lngSeminarID</Code>
	 **/	
	public void setSeminarID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lSeminarID));
		this.m_lSeminarID=value;
	}
	

	/**
	 * The Short name of this seminar (cl for Computerlinguistik, as for Anglistik etc. This is the path under which the webapplication SignUp reaches this seminars frontend. The idea is to base authentication on this short name. Then it might become easier to allow changes in subfolders of the webapplication. Since this field is designated to become an important security feature, it has a unique index.
	 * @return The Short name of this seminar (cl for Computerlinguistik, as for Anglistik etc. This is the path under which the webapplication SignUp reaches this seminars frontend. The idea is to base authentication on this short name. Then it might become easier to allow changes in subfolders of the webapplication. Since this field is designated to become an important security feature, it has a unique index.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdSeminar.strSeminarShort</Code>
	 **/
	public String getSeminarShort(){
		return this.m_sSeminarShort;
	}

	/**
	 * The Short name of this seminar (cl for Computerlinguistik, as for Anglistik etc. This is the path under which the webapplication SignUp reaches this seminars frontend. The idea is to base authentication on this short name. Then it might become easier to allow changes in subfolders of the webapplication. Since this field is designated to become an important security feature, it has a unique index.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdSeminar.strSeminarShort</Code>
	 **/	
	public void setSeminarShort(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sSeminarShort)));
		this.m_sSeminarShort=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdSeminar.strSeminarUnivISID</Code>
	 **/
	public String getSeminarUnivISID(){
		return this.m_sSeminarUnivISID;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdSeminar.strSeminarUnivISID</Code>
	 **/	
	public void setSeminarUnivISID(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sSeminarUnivISID)));
		this.m_sSeminarUnivISID=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdSeminar.strSeminarName</Code>
	 **/
	public String getSeminarName(){
		return this.m_sSeminarName;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdSeminar.strSeminarName</Code>
	 **/	
	public void setSeminarName(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sSeminarName)));
		this.m_sSeminarName=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdSeminar.strSeminarLink</Code>
	 **/
	public String getSeminarLink(){
		return this.m_sSeminarLink;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdSeminar.strSeminarLink</Code>
	 **/	
	public void setSeminarLink(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sSeminarLink)));
		this.m_sSeminarLink=value;
	}
	

	/**
	 * Should the frontend display local gradenames rather than ECTS international names?
	 * @return Should the frontend display local gradenames rather than ECTS international names?
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdSeminar.blnSeminarNoteCboLocal</Code>
	 **/
	public boolean getSeminarNoteCboLocal(){
		return this.m_bSeminarNoteCboLocal;
	}

	/**
	 * Should the frontend display local gradenames rather than ECTS international names?
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdSeminar.blnSeminarNoteCboLocal</Code>
	 **/	
	public void setSeminarNoteCboLocal(boolean value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_bSeminarNoteCboLocal));
		this.m_bSeminarNoteCboLocal=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdSeminar.strSeminarCustom1</Code>
	 **/
	public String getSeminarCustom1(){
		return this.m_sSeminarCustom1;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdSeminar.strSeminarCustom1</Code>
	 **/	
	public void setSeminarCustom1(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sSeminarCustom1)));
		this.m_sSeminarCustom1=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdSeminar.strSeminarCustom2</Code>
	 **/
	public String getSeminarCustom2(){
		return this.m_sSeminarCustom2;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdSeminar.strSeminarCustom2</Code>
	 **/	
	public void setSeminarCustom2(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sSeminarCustom2)));
		this.m_sSeminarCustom2=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdSeminar.strSeminarCustom3</Code>
	 **/
	public String getSeminarCustom3(){
		return this.m_sSeminarCustom3;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdSeminar.strSeminarCustom3</Code>
	 **/	
	public void setSeminarCustom3(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sSeminarCustom3)));
		this.m_sSeminarCustom3=value;
	}
	


////////////////////////////////////////////////////////////////
// 3.   X M L  U T I L I T I E S
////////////////////////////////////////////////////////////////
	
	/**
	 * @return Objekt als XML-String
	 **/
	public String toXMLString(){
		return 	

			"<UniID>" + m_lUniID + "</UniID>"  + 
			"<FakultaetID>" + m_lFakultaetID + "</FakultaetID>"  + 
			"<SeminarID>" + m_lSeminarID + "</SeminarID>"  + 
			"<SeminarShort>" + m_sSeminarShort + "</SeminarShort>"  + 
			"<SeminarUnivISID>" + m_sSeminarUnivISID + "</SeminarUnivISID>"  + 
			"<SeminarName>" + m_sSeminarName + "</SeminarName>"  + 
			"<SeminarLink>" + m_sSeminarLink + "</SeminarLink>"  + 
			"<SeminarNoteCboLocal>" + m_bSeminarNoteCboLocal + "</SeminarNoteCboLocal>"  + 
			"<SeminarCustom1>" + m_sSeminarCustom1 + "</SeminarCustom1>"  + 
			"<SeminarCustom2>" + m_sSeminarCustom2 + "</SeminarCustom2>"  + 
			"<SeminarCustom3>" + m_sSeminarCustom3 + "</SeminarCustom3>" ;
	}

////////////////////////////////////////////////////////////////
// 4.   S Q L  U T I L I T I E S
////////////////////////////////////////////////////////////////


	/**
	 * Where-SQL für eindeutigen Index dieses Objekte in der Datenbank (in Tabelle 'tblSdSeminar.').
	 * @return SQL-String wie z.B. "Id1=? and Id2=?"
	 **/
	private String getSQLWhereClause(){
		return 
			"\"lngSeminarID\"=? AND " + 
			"\"lngSeminarID\"=? AND " + 
			"\"lngUniID\"=? AND " + 
			"\"lngFakultaetID\"=? AND " + 
			"\"lngSeminarID\"=?";
	}

	/**
	 * SQL-Befehl zum Löschen des Objekts in der Datenbank (aus Tabelle 'tblSdSeminar.')
	 * @return SQL-String zum Löschen des Objekts, z.B. "delete from T1 where (Id1=?);"
	 **/
	private String toDBDeleteString(){
		return "delete from \"tblSdSeminar\" where ( " + this.getSQLWhereClause() + ");";
	}

	/**
 	 * Setzt die Werte für alle Platzhalter in {@link #getSQLWhereClause()}.
	 * @param ii Offset für die Parameter (inklusive).
	 * @throws SQLException 
	 **/
	private void pokeWhere(int ii, PreparedStatement ps) throws SQLException{
		ps.setLong(ii++, m_lUniID);
		ps.setLong(ii++, m_lFakultaetID);
		ps.setLong(ii++, m_lSeminarID);
	}

	/**
  	 * Füllt das SQL-Statement mit den aktuellen Werten des Objekts
	 * @throws SQLException
	 **/
	private void pokeStatement(PreparedStatement ps) throws SQLException{
		
		ps.setLong(1, m_lUniID);
		ps.setLong(2, m_lFakultaetID);
		ps.setLong(3, m_lSeminarID);
		ps.setString(4, m_sSeminarShort);
		ps.setString(5, m_sSeminarUnivISID);
		ps.setString(6, m_sSeminarName);
		ps.setString(7, m_sSeminarLink);
		ps.setBoolean(8, m_bSeminarNoteCboLocal);
		ps.setString(9, m_sSeminarCustom1);
		ps.setString(10, m_sSeminarCustom2);
		ps.setString(11, m_sSeminarCustom3);

	}

	/**
	 * SQL-Befehl zum Speichern der Objekteigenschaften in die Datenbank (Tabelle 'tblSdSeminar.')
	 * @return PreparedStatemtent, das nach Aufruf von {@link #pokeStatement(PreparedStatement)} und {@link #execute()} das Update ausführt.
	 **/
	private String toDBUpdateString(){
		return "update \"tblSdSeminar\" set " +
			"\"lngUniID\"=?, " +
			"\"lngFakultaetID\"=?, " +
			"\"lngSeminarID\"=?, " +
			"\"strSeminarShort\"=?, " +
			"\"strSeminarUnivISID\"=?, " +
			"\"strSeminarName\"=?, " +
			"\"strSeminarLink\"=?, " +
			"\"blnSeminarNoteCboLocal\"=?, " +
			"\"strSeminarCustom1\"=?, " +
			"\"strSeminarCustom2\"=?, " +
			"\"strSeminarCustom3\"=?" +
			" where (" + this.getSQLWhereClause() + ");";
	}

	/**
	 * SQL-Befehl zum Hinzufügen des Objekts in die Datenbank (Tabelle 'tblSdSeminar.')
	 * @return SQL-String "insert into...".
	 **/
	private String toDBAddString(){
		return "insert into \"tblSdSeminar\" ( " +
			"\"lngUniID\", \"lngFakultaetID\", \"strSeminarShort\", \"strSeminarUnivISID\", \"strSeminarName\", \"strSeminarLink\", \"blnSeminarNoteCboLocal\", \"strSeminarCustom1\", \"strSeminarCustom2\", \"strSeminarCustom3\" ) VALUES ( ?,?,?,?,?,?,?,?,?,?,?);";
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
	private void init(long lngUniID, long lngFakultaetID, long lngSeminarID) throws SQLException, NamingException{

		this.m_lUniID=lngUniID;

		this.m_lFakultaetID=lngFakultaetID;

		this.m_lSeminarID=lngSeminarID;

		
		PreparedStatement pstm = prepareStatement("select * from \"tblSdSeminar\" where (" + this.getSQLWhereClause() + ");");
		pokeWhere(1,pstm);

		ResultSet rst=pstm.executeQuery();
		if(rst.next()) this.initByRst(rst);
		rst.close();
		this.disconnect();
		rst=null;
	}	
	
	/**
	 * Lade die Objekteigenschaften aus einem ResultSet.
	 * param rst ResultSet mit allen Spalten der Tabelle 'tblSdSeminar'
	 * @throws SQLException 
	 **/
	protected void initByRst(ResultSet rst) throws SQLException{
		this.m_lUniID=rst.getLong("lngUniID");
		this.m_lFakultaetID=rst.getLong("lngFakultaetID");
		this.m_lSeminarID=rst.getLong("lngSeminarID");
		this.m_sSeminarShort=rst.getString("strSeminarShort");
		this.m_sSeminarUnivISID=rst.getString("strSeminarUnivISID");
		this.m_sSeminarName=rst.getString("strSeminarName");
		this.m_sSeminarLink=rst.getString("strSeminarLink");
		this.m_bSeminarNoteCboLocal=rst.getBoolean("blnSeminarNoteCboLocal");
		this.m_sSeminarCustom1=rst.getString("strSeminarCustom1");
		this.m_sSeminarCustom2=rst.getString("strSeminarCustom2");
		this.m_sSeminarCustom3=rst.getString("strSeminarCustom3");	
	}	
	
	/**
	 * Lade die Objekteigenschaften aus einer XML-Node.
	 * param node XML-Node mit allen Eigenschaften als Tags.
	 * @throws ParseException (Datum muss im ISO-Format yyyy-MM-dd übergeben werden).
	 **/
	private void initByNode(Node node) throws ParseException{
		this.m_lUniID=Long.parseLong(shjNodeValue(node, "UniID"));
		this.m_lFakultaetID=Long.parseLong(shjNodeValue(node, "FakultaetID"));
		this.m_lSeminarID=Long.parseLong(shjNodeValue(node, "SeminarID"));
		this.m_sSeminarShort=(shjNodeValue(node, "SeminarShort"));
		this.m_sSeminarUnivISID=(shjNodeValue(node, "SeminarUnivISID"));
		this.m_sSeminarName=(shjNodeValue(node, "SeminarName"));
		this.m_sSeminarLink=(shjNodeValue(node, "SeminarLink"));
		this.m_bSeminarNoteCboLocal=Boolean.valueOf(shjNodeValue(node, "SeminarNoteCboLocal")).booleanValue();
		this.m_sSeminarCustom1=(shjNodeValue(node, "SeminarCustom1"));
		this.m_sSeminarCustom2=(shjNodeValue(node, "SeminarCustom2"));
		this.m_sSeminarCustom3=(shjNodeValue(node, "SeminarCustom3"));
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
	public Seminar(){}	
	
	/**
	 * Konstruktor mit Indexwerten (eindeutig) der assoziierten Datenbanktabelle
	 * @throws NamingException 
	 * @throws SQLException 
	 **/
	public Seminar(long lngUniID, long lngFakultaetID, long lngSeminarID) throws SQLException, NamingException{
		this.init(lngUniID, lngFakultaetID, lngSeminarID);
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
	public Seminar(ResultSet rst) throws SQLException{
		this.initByRst(rst);
		this.m_bIsDirty = false;
	}

	/**
	 * Konstruktor per XML-Darstellung des Objekts.
	 * @throws ParseException, if a date can't be read.
	 **/
	public Seminar(Node node) throws ParseException{
		this.initByNode(node);
		this.m_bIsDirty = false;
	}

  }//Klassenende
