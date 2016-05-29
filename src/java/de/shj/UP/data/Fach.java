
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
 *  Modules: SignUp, Exam
Subject.
 *  
 *  ALLGEMEINE INFORMATION ZU DEN OBJEKTEN DIESES PACKAGES:
 *  Diese Klasse kapselt die Tabelle 'Fach' in 
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
public class Fach extends shjCore{

	private static final long serialVersionUID = 7L;

////////////////////////////////////////////////////////////////
// 1.   P R I V A T E   D E K L A R A T I O N E N
////////////////////////////////////////////////////////////////
	
	private boolean m_bIsDirty = false;

	private int m_iFachID;
	private String m_sFachBezeichnung;
	private float m_sFachCreditPtsRequired;
	private String m_sFachTranskriptName;
	private String m_sFachTranskriptName_en;
	private String m_sFachBeschreibung;
	private String m_sHN;
	private String m_sFachHISStg;

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
	 * Id of this subject.
	 * @return Id of this subject.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdFach.intFachID</Code>
	 **/
	public int getFachID(){
		return this.m_iFachID;
	}

	/**
	 * Id of this subject.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdFach.intFachID</Code>
	 **/	
	public void setFachID(int value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_iFachID));
		this.m_iFachID=value;
	}
	

	/**
	 * Name of this subject.
	 * @return Name of this subject.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdFach.strFachBezeichnung</Code>
	 **/
	public String getFachBezeichnung(){
		return this.m_sFachBezeichnung;
	}

	/**
	 * Name of this subject.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdFach.strFachBezeichnung</Code>
	 **/	
	public void setFachBezeichnung(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_sFachBezeichnung));
		this.m_sFachBezeichnung=value;
	}
	

	/**
	 * How many credit pts. do you get need in order to graduate?
	 * @return How many credit pts. do you get need in order to graduate?
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdFach.sngFachCreditPtsRequired</Code>
	 **/
	public float getFachCreditPtsRequired(){
		return this.m_sFachCreditPtsRequired;
	}

	/**
	 * How many credit pts. do you get need in order to graduate?
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdFach.sngFachCreditPtsRequired</Code>
	 **/	
	public void setFachCreditPtsRequired(float value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_sFachCreditPtsRequired));
		this.m_sFachCreditPtsRequired=value;
	}
	

	/**
	 * Bennung auf dem Druck-Transkript.
	 * @return Bennung auf dem Druck-Transkript.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdFach.strFachTranskriptName</Code>
	 **/
	public String getFachTranskriptName(){
		return this.m_sFachTranskriptName;
	}

	/**
	 * Bennung auf dem Druck-Transkript.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdFach.strFachTranskriptName</Code>
	 **/	
	public void setFachTranskriptName(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_sFachTranskriptName));
		this.m_sFachTranskriptName=value;
	}
	

	/**
	 * Name of this subject in English.
	 * @return Name of this subject in English.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdFach.strFachTranskriptName_en</Code>
	 **/
	public String getFachTranskriptName_en(){
		return this.m_sFachTranskriptName_en;
	}

	/**
	 * Name of this subject in English.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdFach.strFachTranskriptName_en</Code>
	 **/	
	public void setFachTranskriptName_en(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_sFachTranskriptName_en));
		this.m_sFachTranskriptName_en=value;
	}
	

	/**
	 * Description of this subject.
	 * @return Description of this subject.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdFach.strFachBeschreibung</Code>
	 **/
	public String getFachBeschreibung(){
		return this.m_sFachBeschreibung;
	}

	/**
	 * Description of this subject.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdFach.strFachBeschreibung</Code>
	 **/	
	public void setFachBeschreibung(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_sFachBeschreibung));
		this.m_sFachBeschreibung=value;
	}
	

	/**
	 * HF or NF?
	 * @return HF or NF?
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdFach.strHN</Code>
	 **/
	public String getHN(){
		return this.m_sHN;
	}

	/**
	 * HF or NF?
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdFach.strHN</Code>
	 **/	
	public void setHN(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_sHN));
		this.m_sHN=value;
	}
	

	/**
	 * Studiengang in HIS-Notation zu diesem Fach; nur für BA-Fächer relevant. Neu in Verision 6-23, Dez. 2008.
	 * @return Studiengang in HIS-Notation zu diesem Fach; nur für BA-Fächer relevant. Neu in Verision 6-23, Dez. 2008.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdFach.strFachHISStg</Code>
	 **/
	public String getFachHISStg(){
		return this.m_sFachHISStg;
	}

	/**
	 * Studiengang in HIS-Notation zu diesem Fach; nur für BA-Fächer relevant. Neu in Verision 6-23, Dez. 2008.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdFach.strFachHISStg</Code>
	 **/	
	public void setFachHISStg(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_sFachHISStg));
		this.m_sFachHISStg=value;
	}
	


////////////////////////////////////////////////////////////////
// 3.   X M L  U T I L I T I E S
////////////////////////////////////////////////////////////////
	
	/**
	 * @return Objekt als XML-String
	 **/
	public String toXMLString(){
		return 	

			"<FachID>" + m_iFachID + "</FachID>"  + 
			"<FachBezeichnung>" + m_sFachBezeichnung + "</FachBezeichnung>"  + 
			"<FachCreditPtsRequired>" + m_sFachCreditPtsRequired + "</FachCreditPtsRequired>"  + 
			"<FachTranskriptName>" + m_sFachTranskriptName + "</FachTranskriptName>"  + 
			"<FachTranskriptName_en>" + m_sFachTranskriptName_en + "</FachTranskriptName_en>"  + 
			"<FachBeschreibung>" + m_sFachBeschreibung + "</FachBeschreibung>"  + 
			"<HN>" + m_sHN + "</HN>"  + 
			"<FachHISStg>" + m_sFachHISStg + "</FachHISStg>" ;
	}

////////////////////////////////////////////////////////////////
// 4.   S Q L  U T I L I T I E S
////////////////////////////////////////////////////////////////


	/**
	 * Where-SQL für eindeutigen Index dieses Objekte in der Datenbank (in Tabelle 'tblSdFach.').
	 * @return SQL-String wie z.B. "Id1=? and Id2=?"
	 **/
	private String getSQLWhereClause(){
		return 
			"\"intFachID\"=?";
	}

	/**
	 * SQL-Befehl zum Löschen des Objekts in der Datenbank (aus Tabelle 'tblSdFach.')
	 * @return SQL-String zum Löschen des Objekts, z.B. "delete from T1 where (Id1=?);"
	 **/
	private String toDBDeleteString(){
		return "delete from \"tblSdFach\" where ( " + this.getSQLWhereClause() + ");";
	}

	/**
 	 * Setzt die Werte für alle Platzhalter in {@link #getSQLWhereClause()}.
	 * @param ii Offset für die Parameter (inklusive).
	 * @throws SQLException 
	 **/
	private void pokeWhere(int ii, PreparedStatement ps) throws SQLException{
		
		ps.setInt(ii++, m_iFachID);
	}

	/**
  	 * Füllt das SQL-Statement mit den aktuellen Werten des Objekts
	 * @throws SQLException
	 **/
	private void pokeStatement(PreparedStatement ps) throws SQLException{
		
		ps.setInt(1, m_iFachID);
		ps.setString(2, m_sFachBezeichnung);
		ps.setFloat(3, m_sFachCreditPtsRequired);
		ps.setString(4, m_sFachTranskriptName);
		ps.setString(5, m_sFachTranskriptName_en);
		ps.setString(6, m_sFachBeschreibung);
		ps.setString(7, m_sHN);
		ps.setString(8, m_sFachHISStg);

	}

	/**
	 * SQL-Befehl zum Speichern der Objekteigenschaften in die Datenbank (Tabelle 'tblSdFach.')
	 * @return PreparedStatemtent, das nach Aufruf von {@link #pokeStatement(PreparedStatement)} und {@link #execute()} das Update ausführt.
	 **/
	private String toDBUpdateString(){
		return "update \"tblSdFach\" set " +
			"\"intFachID\"=?, " +
			"\"strFachBezeichnung\"=?, " +
			"\"sngFachCreditPtsRequired\"=?, " +
			"\"strFachTranskriptName\"=?, " +
			"\"strFachTranskriptName_en\"=?, " +
			"\"strFachBeschreibung\"=?, " +
			"\"strHN\"=?, " +
			"\"strFachHISStg\"=?" +
			" where (" + this.getSQLWhereClause() + ");";
	}

	/**
	 * SQL-Befehl zum Hinzufügen des Objekts in die Datenbank (Tabelle 'tblSdFach.')
	 * @return SQL-String "insert into...".
	 **/
	private String toDBAddString(){
		return "insert into \"tblSdFach\" ( " +
			"\"intFachID\", \"strFachBezeichnung\", \"sngFachCreditPtsRequired\", \"strFachTranskriptName\", \"strFachTranskriptName_en\", \"strFachBeschreibung\", \"strHN\", \"strFachHISStg\" ) VALUES ( ?,?,?,?,?,?,?,?);";
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
	private void init(int intFachID) throws SQLException, NamingException{

		this.m_iFachID=intFachID;

		
		PreparedStatement pstm = prepareStatement("select * from \"tblSdFach\" where (" + this.getSQLWhereClause() + ");");
		pokeWhere(1,pstm);

		ResultSet rst=pstm.executeQuery();
		if(rst.next()) this.initByRst(rst);
		rst.close();
		this.disconnect();
		rst=null;
	}	
	
	/**
	 * Lade die Objekteigenschaften aus einem ResultSet.
	 * param rst ResultSet mit allen Spalten der Tabelle 'tblSdFach'
	 * @throws SQLException 
	 **/
	protected void initByRst(ResultSet rst) throws SQLException{
		this.m_iFachID=rst.getInt("intFachID");
		this.m_sFachBezeichnung=rst.getString("strFachBezeichnung");
		this.m_sFachCreditPtsRequired=rst.getFloat("sngFachCreditPtsRequired");
		this.m_sFachTranskriptName=rst.getString("strFachTranskriptName");
		this.m_sFachTranskriptName_en=rst.getString("strFachTranskriptName_en");
		this.m_sFachBeschreibung=rst.getString("strFachBeschreibung");
		this.m_sHN=rst.getString("strHN");
		this.m_sFachHISStg=rst.getString("strFachHISStg");	
	}	
	
	/**
	 * Lade die Objekteigenschaften aus einer XML-Node.
	 * param node XML-Node mit allen Eigenschaften als Tags.
	 * @throws ParseException (Datum muss im ISO-Format yyyy-MM-dd übergeben werden).
	 **/
	private void initByNode(Node node) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		this.m_iFachID=Integer.parseInt(shjNodeValue(node, "FachID"));
		this.m_sFachBezeichnung=(shjNodeValue(node, "FachBezeichnung"));
		this.m_sFachCreditPtsRequired=Float.valueOf(shjNodeValue(node, "FachCreditPtsRequired")).floatValue();
		this.m_sFachTranskriptName=(shjNodeValue(node, "FachTranskriptName"));
		this.m_sFachTranskriptName_en=(shjNodeValue(node, "FachTranskriptName_en"));
		this.m_sFachBeschreibung=(shjNodeValue(node, "FachBeschreibung"));
		this.m_sHN=(shjNodeValue(node, "HN"));
		this.m_sFachHISStg=(shjNodeValue(node, "FachHISStg"));
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
		pokeWhere(9,pstm);
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
	public Fach(){}	
	
	/**
	 * Konstruktor mit Indexwerten (eindeutig) der assoziierten Datenbanktabelle
	 * @throws NamingException 
	 * @throws SQLException 
	 **/
	public Fach(int intFachID) throws SQLException, NamingException{
		this.init(intFachID);
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
	public Fach(ResultSet rst) throws SQLException{
		this.initByRst(rst);
		this.m_bIsDirty = false;
	}

	/**
	 * Konstruktor per XML-Darstellung des Objekts.
	 * @throws ParseException, if a date can't be read.
	 **/
	public Fach(Node node) throws ParseException{
		this.initByNode(node);
		this.m_bIsDirty = false;
	}

  }//Klassenende
