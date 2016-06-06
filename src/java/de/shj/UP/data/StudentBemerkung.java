
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
 *  Remarks about students, new in version 6-14.
 *  
 *  ALLGEMEINE INFORMATION ZU DEN OBJEKTEN DIESES PACKAGES:
 *  Diese Klasse kapselt die Tabelle 'StudentBemerkung' in 
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
public class StudentBemerkung extends shjCore{

	private static final long serialVersionUID = 7L;

////////////////////////////////////////////////////////////////
// 1.   P R I V A T E   D E K L A R A T I O N E N
////////////////////////////////////////////////////////////////
	
	private boolean m_bIsDirty = false;

	private long m_lStudentBemerkungID;
	private long m_lSdSeminarID;
	private String m_sMatrikelnummer;
	private long m_lDozentID;
	private String m_sStudentBemerkungTag;
	private String m_sStudentBemerkungText;
	private Date m_dStudentBemerkungDatum;

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
	 * Primary key, SERIAL.
	 * @return Primary key, SERIAL.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentBemerkung.lngStudentBemerkungID</Code>
	 **/
	public long getStudentBemerkungID(){
		return this.m_lStudentBemerkungID;
	}

	/**
	 * Primary key, SERIAL.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentBemerkung.lngStudentBemerkungID</Code>
	 **/	
	public void setStudentBemerkungID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lStudentBemerkungID));
		this.m_lStudentBemerkungID=value;
	}
	

	/**
	 * Seminar
	 * @return Seminar
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentBemerkung.lngSdSeminarID</Code>
	 **/
	public long getSdSeminarID(){
		return this.m_lSdSeminarID;
	}

	/**
	 * Seminar
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentBemerkung.lngSdSeminarID</Code>
	 **/	
	public void setSdSeminarID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lSdSeminarID));
		this.m_lSdSeminarID=value;
	}
	

	/**
	 * Registration number of this student. Must have seven digits, currently.
	 * @return Registration number of this student. Must have seven digits, currently.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentBemerkung.strMatrikelnummer</Code>
	 **/
	public String getMatrikelnummer(){
		return this.m_sMatrikelnummer;
	}

	/**
	 * Registration number of this student. Must have seven digits, currently.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentBemerkung.strMatrikelnummer</Code>
	 **/	
	public void setMatrikelnummer(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sMatrikelnummer)));
		this.m_sMatrikelnummer=value;
	}
	

	/**
	 * Id of teacher who entered the remark.
	 * @return Id of teacher who entered the remark.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentBemerkung.lngDozentID</Code>
	 **/
	public long getDozentID(){
		return this.m_lDozentID;
	}

	/**
	 * Id of teacher who entered the remark.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentBemerkung.lngDozentID</Code>
	 **/	
	public void setDozentID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lDozentID));
		this.m_lDozentID=value;
	}
	

	/**
	 * A short tag to classify the remark, e.g. 'auto' for automatic remarks. There is no master table, the tags are managed by the frontend..
	 * @return A short tag to classify the remark, e.g. 'auto' for automatic remarks. There is no master table, the tags are managed by the frontend..
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentBemerkung.strStudentBemerkungTag</Code>
	 **/
	public String getStudentBemerkungTag(){
		return this.m_sStudentBemerkungTag;
	}

	/**
	 * A short tag to classify the remark, e.g. 'auto' for automatic remarks. There is no master table, the tags are managed by the frontend..
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentBemerkung.strStudentBemerkungTag</Code>
	 **/	
	public void setStudentBemerkungTag(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sStudentBemerkungTag)));
		this.m_sStudentBemerkungTag=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentBemerkung.strStudentBemerkungText</Code>
	 **/
	public String getStudentBemerkungText(){
		return this.m_sStudentBemerkungText;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentBemerkung.strStudentBemerkungText</Code>
	 **/	
	public void setStudentBemerkungText(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sStudentBemerkungText)));
		this.m_sStudentBemerkungText=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentBemerkung.dtmStudentBemerkungDatum</Code>
	 **/
	public Date getStudentBemerkungDatum(){
		return this.m_dStudentBemerkungDatum;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentBemerkung.dtmStudentBemerkungDatum</Code>
	 **/	
	public void setStudentBemerkungDatum(Date value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_dStudentBemerkungDatum));
		this.m_dStudentBemerkungDatum=value;
	}
	


////////////////////////////////////////////////////////////////
// 3.   X M L  U T I L I T I E S
////////////////////////////////////////////////////////////////
	
	/**
	 * @return Objekt als XML-String
	 **/
	public String toXMLString(){
		return 	

			"<StudentBemerkungID>" + m_lStudentBemerkungID + "</StudentBemerkungID>"  + 
			"<SdSeminarID>" + m_lSdSeminarID + "</SdSeminarID>"  + 
			"<Matrikelnummer>" + m_sMatrikelnummer + "</Matrikelnummer>"  + 
			"<DozentID>" + m_lDozentID + "</DozentID>"  + 
			"<StudentBemerkungTag>" + m_sStudentBemerkungTag + "</StudentBemerkungTag>"  + 
			"<StudentBemerkungText>" + m_sStudentBemerkungText + "</StudentBemerkungText>"  + 
			"<StudentBemerkungDatum>" + m_dStudentBemerkungDatum + "</StudentBemerkungDatum>" ;
	}

////////////////////////////////////////////////////////////////
// 4.   S Q L  U T I L I T I E S
////////////////////////////////////////////////////////////////


	/**
	 * Where-SQL für eindeutigen Index dieses Objekte in der Datenbank (in Tabelle 'tblBdStudentBemerkung.').
	 * @return SQL-String wie z.B. "Id1=? and Id2=?"
	 **/
	private String getSQLWhereClause(){
		return 
			"\"lngStudentBemerkungID\"=?";
	}

	/**
	 * SQL-Befehl zum Löschen des Objekts in der Datenbank (aus Tabelle 'tblBdStudentBemerkung.')
	 * @return SQL-String zum Löschen des Objekts, z.B. "delete from T1 where (Id1=?);"
	 **/
	private String toDBDeleteString(){
		return "delete from \"tblBdStudentBemerkung\" where ( " + this.getSQLWhereClause() + ");";
	}

	/**
 	 * Setzt die Werte für alle Platzhalter in {@link #getSQLWhereClause()}.
	 * @param ii Offset für die Parameter (inklusive).
	 * @throws SQLException 
	 **/
	private void pokeWhere(int ii, PreparedStatement ps) throws SQLException{
		
		ps.setLong(ii++, m_lStudentBemerkungID);
	}

	/**
  	 * Füllt das SQL-Statement mit den aktuellen Werten des Objekts
	 * @throws SQLException
	 **/
	private void pokeStatement(PreparedStatement ps) throws SQLException{
		
		ps.setLong(1, m_lStudentBemerkungID);
		ps.setLong(2, m_lSdSeminarID);
		ps.setString(3, m_sMatrikelnummer);
		ps.setLong(4, m_lDozentID);
		ps.setString(5, m_sStudentBemerkungTag);
		ps.setString(6, m_sStudentBemerkungText);
		ps.setDate(7, m_dStudentBemerkungDatum);

	}

	/**
	 * SQL-Befehl zum Speichern der Objekteigenschaften in die Datenbank (Tabelle 'tblBdStudentBemerkung.')
	 * @return PreparedStatemtent, das nach Aufruf von {@link #pokeStatement(PreparedStatement)} und {@link #execute()} das Update ausführt.
	 **/
	private String toDBUpdateString(){
		return "update \"tblBdStudentBemerkung\" set " +
			"\"lngStudentBemerkungID\"=?, " +
			"\"lngSdSeminarID\"=?, " +
			"\"strMatrikelnummer\"=?, " +
			"\"lngDozentID\"=?, " +
			"\"strStudentBemerkungTag\"=?, " +
			"\"strStudentBemerkungText\"=?, " +
			"\"dtmStudentBemerkungDatum\"=?" +
			" where (" + this.getSQLWhereClause() + ");";
	}

	/**
	 * SQL-Befehl zum Hinzufügen des Objekts in die Datenbank (Tabelle 'tblBdStudentBemerkung.')
	 * @return SQL-String "insert into...".
	 **/
	private String toDBAddString(){
		return "insert into \"tblBdStudentBemerkung\" ( " +
			"\"lngStudentBemerkungID\", \"lngSdSeminarID\", \"strMatrikelnummer\", \"lngDozentID\", \"strStudentBemerkungTag\", \"strStudentBemerkungText\", \"dtmStudentBemerkungDatum\" ) VALUES ( ?,?,?,?,?,?,?);";
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
	private void init(long lngStudentBemerkungID) throws SQLException, NamingException{

		this.m_lStudentBemerkungID=lngStudentBemerkungID;

		
		PreparedStatement pstm = prepareStatement("select * from \"tblBdStudentBemerkung\" where (" + this.getSQLWhereClause() + ");");
		pokeWhere(1,pstm);

		ResultSet rst=pstm.executeQuery();
		if(rst.next()) this.initByRst(rst);
		rst.close();
		this.disconnect();
		rst=null;
	}	
	
	/**
	 * Lade die Objekteigenschaften aus einem ResultSet.
	 * param rst ResultSet mit allen Spalten der Tabelle 'tblBdStudentBemerkung'
	 * @throws SQLException 
	 **/
	protected void initByRst(ResultSet rst) throws SQLException{
		this.m_lStudentBemerkungID=rst.getLong("lngStudentBemerkungID");
		this.m_lSdSeminarID=rst.getLong("lngSdSeminarID");
		this.m_sMatrikelnummer=rst.getString("strMatrikelnummer");
		this.m_lDozentID=rst.getLong("lngDozentID");
		this.m_sStudentBemerkungTag=rst.getString("strStudentBemerkungTag");
		this.m_sStudentBemerkungText=rst.getString("strStudentBemerkungText");
		this.m_dStudentBemerkungDatum=rst.getDate("dtmStudentBemerkungDatum");	
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
		pokeWhere(8,pstm);
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
	public StudentBemerkung(){}	
	
	/**
	 * Konstruktor mit Indexwerten (eindeutig) der assoziierten Datenbanktabelle
	 * @throws NamingException 
	 * @throws SQLException 
	 **/
	public StudentBemerkung(long lngStudentBemerkungID) throws SQLException, NamingException{
		this.init(lngStudentBemerkungID);
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
	public StudentBemerkung(ResultSet rst) throws SQLException{
		this.initByRst(rst);
		this.m_bIsDirty = false;
	}
  }//Klassenende