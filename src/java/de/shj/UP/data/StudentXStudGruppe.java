
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
 *  Maps students to StudGroups. A mapping takes a semester (e.g. ws2006/2007) and a Studiensemester (e.g. 2). This then means, that the group was/is current in ws2006/2007 and grouped all students of cohort 2 (e.g. those who were in the 2nd semester at the time).
 *  
 *  ALLGEMEINE INFORMATION ZU DEN OBJEKTEN DIESES PACKAGES:
 *  Diese Klasse kapselt die Tabelle 'StudentXStudGruppe' in 
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
public class StudentXStudGruppe extends shjCore{

	private static final long serialVersionUID = 7L;

////////////////////////////////////////////////////////////////
// 1.   P R I V A T E   D E K L A R A T I O N E N
////////////////////////////////////////////////////////////////
	
	private boolean m_bIsDirty = false;

	private String m_sMatrikelnummer;
	private long m_lSeminarID;
	private long m_lStudGruppeID;
	private String m_sStudentStudGruppeSemester;
	private long m_lStudentStudGruppeStudiensem;
	private Date m_dStudentStudGruppeDatum;
	private String m_sStudentStudGruppeCustom1;
	private String m_sStudentStudGruppeCustom2;
	private String m_sStudentStudGruppeCustom3;

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
	 * Registration number of student.
	 * @return Registration number of student.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXStudGruppe.strMatrikelnummer</Code>
	 **/
	public String getMatrikelnummer(){
		return this.m_sMatrikelnummer;
	}

	/**
	 * Registration number of student.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXStudGruppe.strMatrikelnummer</Code>
	 **/	
	public void setMatrikelnummer(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sMatrikelnummer)));
		this.m_sMatrikelnummer=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXStudGruppe.lngSeminarID</Code>
	 **/
	public long getSeminarID(){
		return this.m_lSeminarID;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXStudGruppe.lngSeminarID</Code>
	 **/	
	public void setSeminarID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lSeminarID));
		this.m_lSeminarID=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXStudGruppe.lngStudGruppeID</Code>
	 **/
	public long getStudGruppeID(){
		return this.m_lStudGruppeID;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXStudGruppe.lngStudGruppeID</Code>
	 **/	
	public void setStudGruppeID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lStudGruppeID));
		this.m_lStudGruppeID=value;
	}
	

	/**
	 * Part of key: in what semester was this group current? (Field max length is 10, for ws2006/2007
	 * @return Part of key: in what semester was this group current? (Field max length is 10, for ws2006/2007
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXStudGruppe.strStudentStudGruppeSemester</Code>
	 **/
	public String getStudentStudGruppeSemester(){
		return this.m_sStudentStudGruppeSemester;
	}

	/**
	 * Part of key: in what semester was this group current? (Field max length is 10, for ws2006/2007
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXStudGruppe.strStudentStudGruppeSemester</Code>
	 **/	
	public void setStudentStudGruppeSemester(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sStudentStudGruppeSemester)));
		this.m_sStudentStudGruppeSemester=value;
	}
	

	/**
	 * What studiensemester is this group relevant for? (Part of key).
	 * @return What studiensemester is this group relevant for? (Part of key).
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXStudGruppe.lngStudentStudGruppeStudiensem</Code>
	 **/
	public long getStudentStudGruppeStudiensem(){
		return this.m_lStudentStudGruppeStudiensem;
	}

	/**
	 * What studiensemester is this group relevant for? (Part of key).
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXStudGruppe.lngStudentStudGruppeStudiensem</Code>
	 **/	
	public void setStudentStudGruppeStudiensem(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lStudentStudGruppeStudiensem));
		this.m_lStudentStudGruppeStudiensem=value;
	}
	

	/**
	 * Date when this particular mapping of the student to this group was created.
	 * @return Date when this particular mapping of the student to this group was created.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXStudGruppe.dtmStudentStudGruppeDatum</Code>
	 **/
	public Date getStudentStudGruppeDatum(){
		return this.m_dStudentStudGruppeDatum;
	}

	/**
	 * Date when this particular mapping of the student to this group was created.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXStudGruppe.dtmStudentStudGruppeDatum</Code>
	 **/	
	public void setStudentStudGruppeDatum(Date value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_dStudentStudGruppeDatum)));
		this.m_dStudentStudGruppeDatum=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXStudGruppe.strStudentStudGruppeCustom1</Code>
	 **/
	public String getStudentStudGruppeCustom1(){
		return this.m_sStudentStudGruppeCustom1;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXStudGruppe.strStudentStudGruppeCustom1</Code>
	 **/	
	public void setStudentStudGruppeCustom1(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sStudentStudGruppeCustom1)));
		this.m_sStudentStudGruppeCustom1=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXStudGruppe.strStudentStudGruppeCustom2</Code>
	 **/
	public String getStudentStudGruppeCustom2(){
		return this.m_sStudentStudGruppeCustom2;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXStudGruppe.strStudentStudGruppeCustom2</Code>
	 **/	
	public void setStudentStudGruppeCustom2(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sStudentStudGruppeCustom2)));
		this.m_sStudentStudGruppeCustom2=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXStudGruppe.strStudentStudGruppeCustom3</Code>
	 **/
	public String getStudentStudGruppeCustom3(){
		return this.m_sStudentStudGruppeCustom3;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXStudGruppe.strStudentStudGruppeCustom3</Code>
	 **/	
	public void setStudentStudGruppeCustom3(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sStudentStudGruppeCustom3)));
		this.m_sStudentStudGruppeCustom3=value;
	}
	


////////////////////////////////////////////////////////////////
// 3.   X M L  U T I L I T I E S
////////////////////////////////////////////////////////////////
	
	/**
	 * @return Objekt als XML-String
	 **/
	public String toXMLString(){
		return 	

			"<Matrikelnummer>" + m_sMatrikelnummer + "</Matrikelnummer>"  + 
			"<SeminarID>" + m_lSeminarID + "</SeminarID>"  + 
			"<StudGruppeID>" + m_lStudGruppeID + "</StudGruppeID>"  + 
			"<StudentStudGruppeSemester>" + m_sStudentStudGruppeSemester + "</StudentStudGruppeSemester>"  + 
			"<StudentStudGruppeStudiensem>" + m_lStudentStudGruppeStudiensem + "</StudentStudGruppeStudiensem>"  + 
			"<StudentStudGruppeDatum>" + m_dStudentStudGruppeDatum + "</StudentStudGruppeDatum>"  + 
			"<StudentStudGruppeCustom1>" + m_sStudentStudGruppeCustom1 + "</StudentStudGruppeCustom1>"  + 
			"<StudentStudGruppeCustom2>" + m_sStudentStudGruppeCustom2 + "</StudentStudGruppeCustom2>"  + 
			"<StudentStudGruppeCustom3>" + m_sStudentStudGruppeCustom3 + "</StudentStudGruppeCustom3>" ;
	}

////////////////////////////////////////////////////////////////
// 4.   S Q L  U T I L I T I E S
////////////////////////////////////////////////////////////////


	/**
	 * Where-SQL für eindeutigen Index dieses Objekte in der Datenbank (in Tabelle 'tblBdStudentXStudGruppe.').
	 * @return SQL-String wie z.B. "Id1=? and Id2=?"
	 **/
	private String getSQLWhereClause(){
		return 
			"\"lngSeminarID\"=? AND " + 
			"\"lngStudGruppeID\"=? AND " + 
			"\"strMatrikelnummer\"=? AND " + 
			"\"strStudentStudGruppeSemester\"=? AND " + 
			"\"lngStudentStudGruppeStudiensem\"=?";
	}

	/**
	 * SQL-Befehl zum Löschen des Objekts in der Datenbank (aus Tabelle 'tblBdStudentXStudGruppe.')
	 * @return SQL-String zum Löschen des Objekts, z.B. "delete from T1 where (Id1=?);"
	 **/
	private String toDBDeleteString(){
		return "delete from \"tblBdStudentXStudGruppe\" where ( " + this.getSQLWhereClause() + ");";
	}

	/**
 	 * Setzt die Werte für alle Platzhalter in {@link #getSQLWhereClause()}.
	 * @param ii Offset für die Parameter (inklusive).
	 * @throws SQLException 
	 **/
	private void pokeWhere(int ii, PreparedStatement ps) throws SQLException{
		
		ps.setLong(ii++, m_lSeminarID);
		ps.setLong(ii++, m_lStudGruppeID);
		ps.setString(ii++, m_sMatrikelnummer);
		ps.setString(ii++, m_sStudentStudGruppeSemester);
		ps.setLong(ii++, m_lStudentStudGruppeStudiensem);
	}

	/**
  	 * Füllt das SQL-Statement mit den aktuellen Werten des Objekts
	 * @throws SQLException
	 **/
	private void pokeStatement(PreparedStatement ps) throws SQLException{
		
		ps.setString(1, m_sMatrikelnummer);
		ps.setLong(2, m_lSeminarID);
		ps.setLong(3, m_lStudGruppeID);
		ps.setString(4, m_sStudentStudGruppeSemester);
		ps.setLong(5, m_lStudentStudGruppeStudiensem);
		ps.setDate(6, m_dStudentStudGruppeDatum);
		ps.setString(7, m_sStudentStudGruppeCustom1);
		ps.setString(8, m_sStudentStudGruppeCustom2);
		ps.setString(9, m_sStudentStudGruppeCustom3);

	}

	/**
	 * SQL-Befehl zum Speichern der Objekteigenschaften in die Datenbank (Tabelle 'tblBdStudentXStudGruppe.')
	 * @return PreparedStatemtent, das nach Aufruf von {@link #pokeStatement(PreparedStatement)} und {@link #execute()} das Update ausführt.
	 **/
	private String toDBUpdateString(){
		return "update \"tblBdStudentXStudGruppe\" set " +
			"\"strMatrikelnummer\"=?, " +
			"\"lngSeminarID\"=?, " +
			"\"lngStudGruppeID\"=?, " +
			"\"strStudentStudGruppeSemester\"=?, " +
			"\"lngStudentStudGruppeStudiensem\"=?, " +
			"\"dtmStudentStudGruppeDatum\"=?, " +
			"\"strStudentStudGruppeCustom1\"=?, " +
			"\"strStudentStudGruppeCustom2\"=?, " +
			"\"strStudentStudGruppeCustom3\"=?" +
			" where (" + this.getSQLWhereClause() + ");";
	}

	/**
	 * SQL-Befehl zum Hinzufügen des Objekts in die Datenbank (Tabelle 'tblBdStudentXStudGruppe.')
	 * @return SQL-String "insert into...".
	 **/
	private String toDBAddString(){
		return "insert into \"tblBdStudentXStudGruppe\" ( " +
			"\"strMatrikelnummer\", \"lngSeminarID\", \"lngStudGruppeID\", \"strStudentStudGruppeSemester\", \"lngStudentStudGruppeStudiensem\", \"dtmStudentStudGruppeDatum\", \"strStudentStudGruppeCustom1\", \"strStudentStudGruppeCustom2\", \"strStudentStudGruppeCustom3\" ) VALUES ( ?,?,?,?,?,?,?,?,?);";
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
	private void init(long lngSeminarID, long lngStudGruppeID, String strMatrikelnummer, String strStudentStudGruppeSemester, long lngStudentStudGruppeStudiensem) throws SQLException, NamingException{

		this.m_lSeminarID=lngSeminarID;

		this.m_lStudGruppeID=lngStudGruppeID;

		this.m_sMatrikelnummer=strMatrikelnummer;

		this.m_sStudentStudGruppeSemester=strStudentStudGruppeSemester;

		this.m_lStudentStudGruppeStudiensem=lngStudentStudGruppeStudiensem;

		
		PreparedStatement pstm = prepareStatement("select * from \"tblBdStudentXStudGruppe\" where (" + this.getSQLWhereClause() + ");");
		pokeWhere(1,pstm);

		ResultSet rst=pstm.executeQuery();
		if(rst.next()) this.initByRst(rst);
		rst.close();
		this.disconnect();
		rst=null;
	}	
	
	/**
	 * Lade die Objekteigenschaften aus einem ResultSet.
	 * param rst ResultSet mit allen Spalten der Tabelle 'tblBdStudentXStudGruppe'
	 * @throws SQLException 
	 **/
	public void initByRst(ResultSet rst) throws SQLException{
		this.m_sMatrikelnummer=rst.getString("strMatrikelnummer");
		this.m_lSeminarID=rst.getLong("lngSeminarID");
		this.m_lStudGruppeID=rst.getLong("lngStudGruppeID");
		this.m_sStudentStudGruppeSemester=rst.getString("strStudentStudGruppeSemester");
		this.m_lStudentStudGruppeStudiensem=rst.getLong("lngStudentStudGruppeStudiensem");
		this.m_dStudentStudGruppeDatum=rst.getDate("dtmStudentStudGruppeDatum");
		this.m_sStudentStudGruppeCustom1=rst.getString("strStudentStudGruppeCustom1");
		this.m_sStudentStudGruppeCustom2=rst.getString("strStudentStudGruppeCustom2");
		this.m_sStudentStudGruppeCustom3=rst.getString("strStudentStudGruppeCustom3");	
	}	
	
	/**
	 * Lade die Objekteigenschaften aus einer XML-Node.
	 * param node XML-Node mit allen Eigenschaften als Tags.
	 * @throws ParseException (Datum muss im ISO-Format yyyy-MM-dd übergeben werden).
	 **/
	private void initByNode(Node node) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		this.m_sMatrikelnummer=(shjNodeValue(node, "Matrikelnummer"));
		this.m_lSeminarID=Long.parseLong(shjNodeValue(node, "SeminarID"));
		this.m_lStudGruppeID=Long.parseLong(shjNodeValue(node, "StudGruppeID"));
		this.m_sStudentStudGruppeSemester=(shjNodeValue(node, "StudentStudGruppeSemester"));
		this.m_lStudentStudGruppeStudiensem=Long.parseLong(shjNodeValue(node, "StudentStudGruppeStudiensem"));
		this.m_dStudentStudGruppeDatum=(Date) (sdf.parse(shjNodeValue(node, "StudentStudGruppeDatum")));
		this.m_sStudentStudGruppeCustom1=(shjNodeValue(node, "StudentStudGruppeCustom1"));
		this.m_sStudentStudGruppeCustom2=(shjNodeValue(node, "StudentStudGruppeCustom2"));
		this.m_sStudentStudGruppeCustom3=(shjNodeValue(node, "StudentStudGruppeCustom3"));
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
	public StudentXStudGruppe(){}	
	
	/**
	 * Konstruktor mit Indexwerten (eindeutig) der assoziierten Datenbanktabelle
	 * @throws NamingException 
	 * @throws SQLException 
	 **/
	public StudentXStudGruppe(long lngSeminarID, long lngStudGruppeID, String strMatrikelnummer, String strStudentStudGruppeSemester, long lngStudentStudGruppeStudiensem) throws SQLException, NamingException{
		this.init(lngSeminarID, lngStudGruppeID, strMatrikelnummer, strStudentStudGruppeSemester, lngStudentStudGruppeStudiensem);
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
	public StudentXStudGruppe(ResultSet rst) throws SQLException{
		this.initByRst(rst);
		this.m_bIsDirty = false;
	}

	/**
	 * Konstruktor per XML-Darstellung des Objekts.
	 * @throws ParseException, if a date can't be read.
	 **/
	public StudentXStudGruppe(Node node) throws ParseException{
		this.initByNode(node);
		this.m_bIsDirty = false;
	}

  }//Klassenende
