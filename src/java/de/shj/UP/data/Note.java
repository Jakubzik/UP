
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
 *  Module: Exam.
Grade. This object represents a grade (credits and exams are graded).
 *  
 *  ALLGEMEINE INFORMATION ZU DEN OBJEKTEN DIESES PACKAGES:
 *  Diese Klasse kapselt die Tabelle 'Note' in 
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
public class Note extends shjCore{

	private static final long serialVersionUID = 7L;

////////////////////////////////////////////////////////////////
// 1.   P R I V A T E   D E K L A R A T I O N E N
////////////////////////////////////////////////////////////////
	
	private boolean m_bIsDirty = false;

	private long m_lSdSeminarID;
	private int m_iNoteID;
	private String m_sNoteNameDE;
	private float m_sNoteWertDE;
	private String m_sNoteECTSGrade;
	private String m_sNoteECTSDefinition;
	private float m_sNoteECTSCalc;
	private boolean m_bNoteBestanden;
	private String m_sNoteCustom1;
	private String m_sNoteCustom2;
	private String m_sNoteCustom3;

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
	 * Seminar.
	 * @return Seminar.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdNote.lngSdSeminarID</Code>
	 **/
	public long getSdSeminarID(){
		return this.m_lSdSeminarID;
	}

	/**
	 * Seminar.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdNote.lngSdSeminarID</Code>
	 **/	
	public void setSdSeminarID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lSdSeminarID));
		this.m_lSdSeminarID=value;
	}
	

	/**
	 * Id of grade.
	 * @return Id of grade.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdNote.intNoteID</Code>
	 **/
	public int getNoteID(){
		return this.m_iNoteID;
	}

	/**
	 * Id of grade.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdNote.intNoteID</Code>
	 **/	
	public void setNoteID(int value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_iNoteID));
		this.m_iNoteID=value;
	}
	

	/**
	 * Local name of this grade.
	 * @return Local name of this grade.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdNote.strNoteNameDE</Code>
	 **/
	public String getNoteNameDE(){
		return this.m_sNoteNameDE;
	}

	/**
	 * Local name of this grade.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdNote.strNoteNameDE</Code>
	 **/	
	public void setNoteNameDE(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sNoteNameDE)));
		this.m_sNoteNameDE=value;
	}
	

	/**
	 * Numerical, local value of this grade.
	 * @return Numerical, local value of this grade.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdNote.sngNoteWertDE</Code>
	 **/
	public float getNoteWertDE(){
		return this.m_sNoteWertDE;
	}

	/**
	 * Numerical, local value of this grade.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdNote.sngNoteWertDE</Code>
	 **/	
	public void setNoteWertDE(float value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_sNoteWertDE));
		this.m_sNoteWertDE=value;
	}
	

	/**
	 * ECTS-grade.
	 * @return ECTS-grade.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdNote.strNoteECTSGrade</Code>
	 **/
	public String getNoteECTSGrade(){
		return this.m_sNoteECTSGrade;
	}

	/**
	 * ECTS-grade.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdNote.strNoteECTSGrade</Code>
	 **/	
	public void setNoteECTSGrade(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sNoteECTSGrade)));
		this.m_sNoteECTSGrade=value;
	}
	

	/**
	 * ECTS-Grade definition.
	 * @return ECTS-Grade definition.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdNote.strNoteECTSDefinition</Code>
	 **/
	public String getNoteECTSDefinition(){
		return this.m_sNoteECTSDefinition;
	}

	/**
	 * ECTS-Grade definition.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdNote.strNoteECTSDefinition</Code>
	 **/	
	public void setNoteECTSDefinition(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sNoteECTSDefinition)));
		this.m_sNoteECTSDefinition=value;
	}
	

	/**
	 * Numerical value for calculation attached to that grade (ECTS).
	 * @return Numerical value for calculation attached to that grade (ECTS).
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdNote.sngNoteECTSCalc</Code>
	 **/
	public float getNoteECTSCalc(){
		return this.m_sNoteECTSCalc;
	}

	/**
	 * Numerical value for calculation attached to that grade (ECTS).
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdNote.sngNoteECTSCalc</Code>
	 **/	
	public void setNoteECTSCalc(float value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_sNoteECTSCalc));
		this.m_sNoteECTSCalc=value;
	}
	

	/**
	 * Passed?
	 * @return Passed?
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdNote.blnNoteBestanden</Code>
	 **/
	public boolean getNoteBestanden(){
		return this.m_bNoteBestanden;
	}

	/**
	 * Passed?
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdNote.blnNoteBestanden</Code>
	 **/	
	public void setNoteBestanden(boolean value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_bNoteBestanden));
		this.m_bNoteBestanden=value;
	}
	

	/**
	 * Custom information.
	 * @return Custom information.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdNote.strNoteCustom1</Code>
	 **/
	public String getNoteCustom1(){
		return this.m_sNoteCustom1;
	}

	/**
	 * Custom information.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdNote.strNoteCustom1</Code>
	 **/	
	public void setNoteCustom1(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sNoteCustom1)));
		this.m_sNoteCustom1=value;
	}
	

	/**
	 * Custom information.
	 * @return Custom information.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdNote.strNoteCustom2</Code>
	 **/
	public String getNoteCustom2(){
		return this.m_sNoteCustom2;
	}

	/**
	 * Custom information.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdNote.strNoteCustom2</Code>
	 **/	
	public void setNoteCustom2(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sNoteCustom2)));
		this.m_sNoteCustom2=value;
	}
	

	/**
	 * Custom information.
	 * @return Custom information.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdNote.strNoteCustom3</Code>
	 **/
	public String getNoteCustom3(){
		return this.m_sNoteCustom3;
	}

	/**
	 * Custom information.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdNote.strNoteCustom3</Code>
	 **/	
	public void setNoteCustom3(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sNoteCustom3)));
		this.m_sNoteCustom3=value;
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
			"<NoteID>" + m_iNoteID + "</NoteID>"  + 
			"<NoteNameDE>" + m_sNoteNameDE + "</NoteNameDE>"  + 
			"<NoteWertDE>" + m_sNoteWertDE + "</NoteWertDE>"  + 
			"<NoteECTSGrade>" + m_sNoteECTSGrade + "</NoteECTSGrade>"  + 
			"<NoteECTSDefinition>" + m_sNoteECTSDefinition + "</NoteECTSDefinition>"  + 
			"<NoteECTSCalc>" + m_sNoteECTSCalc + "</NoteECTSCalc>"  + 
			"<NoteBestanden>" + m_bNoteBestanden + "</NoteBestanden>"  + 
			"<NoteCustom1>" + m_sNoteCustom1 + "</NoteCustom1>"  + 
			"<NoteCustom2>" + m_sNoteCustom2 + "</NoteCustom2>"  + 
			"<NoteCustom3>" + m_sNoteCustom3 + "</NoteCustom3>" ;
	}

////////////////////////////////////////////////////////////////
// 4.   S Q L  U T I L I T I E S
////////////////////////////////////////////////////////////////


	/**
	 * Where-SQL für eindeutigen Index dieses Objekte in der Datenbank (in Tabelle 'tblSdNote.').
	 * @return SQL-String wie z.B. "Id1=? and Id2=?"
	 **/
	private String getSQLWhereClause(){
		return 
			"\"lngSdSeminarID\"=? AND " + 
			"\"intNoteID\"=?";
	}

	/**
	 * SQL-Befehl zum Löschen des Objekts in der Datenbank (aus Tabelle 'tblSdNote.')
	 * @return SQL-String zum Löschen des Objekts, z.B. "delete from T1 where (Id1=?);"
	 **/
	private String toDBDeleteString(){
		return "delete from \"tblSdNote\" where ( " + this.getSQLWhereClause() + ");";
	}

	/**
 	 * Setzt die Werte für alle Platzhalter in {@link #getSQLWhereClause()}.
	 * @param ii Offset für die Parameter (inklusive).
	 * @throws SQLException 
	 **/
	private void pokeWhere(int ii, PreparedStatement ps) throws SQLException{
		
		ps.setLong(ii++, m_lSdSeminarID);
		ps.setInt(ii++, m_iNoteID);
	}

	/**
  	 * Füllt das SQL-Statement mit den aktuellen Werten des Objekts
	 * @throws SQLException
	 **/
	private void pokeStatement(PreparedStatement ps) throws SQLException{
		
		ps.setLong(1, m_lSdSeminarID);
		ps.setInt(2, m_iNoteID);
		ps.setString(3, m_sNoteNameDE);
		ps.setFloat(4, m_sNoteWertDE);
		ps.setString(5, m_sNoteECTSGrade);
		ps.setString(6, m_sNoteECTSDefinition);
		ps.setFloat(7, m_sNoteECTSCalc);
		ps.setBoolean(8, m_bNoteBestanden);
		ps.setString(9, m_sNoteCustom1);
		ps.setString(10, m_sNoteCustom2);
		ps.setString(11, m_sNoteCustom3);

	}

	/**
	 * SQL-Befehl zum Speichern der Objekteigenschaften in die Datenbank (Tabelle 'tblSdNote.')
	 * @return PreparedStatemtent, das nach Aufruf von {@link #pokeStatement(PreparedStatement)} und {@link #execute()} das Update ausführt.
	 **/
	private String toDBUpdateString(){
		return "update \"tblSdNote\" set " +
			"\"lngSdSeminarID\"=?, " +
			"\"intNoteID\"=?, " +
			"\"strNoteNameDE\"=?, " +
			"\"sngNoteWertDE\"=?, " +
			"\"strNoteECTSGrade\"=?, " +
			"\"strNoteECTSDefinition\"=?, " +
			"\"sngNoteECTSCalc\"=?, " +
			"\"blnNoteBestanden\"=?, " +
			"\"strNoteCustom1\"=?, " +
			"\"strNoteCustom2\"=?, " +
			"\"strNoteCustom3\"=?" +
			" where (" + this.getSQLWhereClause() + ");";
	}

	/**
	 * SQL-Befehl zum Hinzufügen des Objekts in die Datenbank (Tabelle 'tblSdNote.')
	 * @return SQL-String "insert into...".
	 **/
	private String toDBAddString(){
		return "insert into \"tblSdNote\" ( " +
			"\"lngSdSeminarID\", \"intNoteID\", \"strNoteNameDE\", \"sngNoteWertDE\", \"strNoteECTSGrade\", \"strNoteECTSDefinition\", \"sngNoteECTSCalc\", \"blnNoteBestanden\", \"strNoteCustom1\", \"strNoteCustom2\", \"strNoteCustom3\" ) VALUES ( ?,?,?,?,?,?,?,?,?,?,?);";
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
	private void init(long lngSdSeminarID, int intNoteID) throws SQLException, NamingException{

		this.m_lSdSeminarID=lngSdSeminarID;

		this.m_iNoteID=intNoteID;

		
		PreparedStatement pstm = prepareStatement("select * from \"tblSdNote\" where (" + this.getSQLWhereClause() + ");");
		pokeWhere(1,pstm);

		ResultSet rst=pstm.executeQuery();
		if(rst.next()) this.initByRst(rst);
		rst.close();
		this.disconnect();
		rst=null;
	}	
	
	/**
	 * Lade die Objekteigenschaften aus einem ResultSet.
	 * param rst ResultSet mit allen Spalten der Tabelle 'tblSdNote'
	 * muss public sein für Serienbrief Med.
	 * @throws SQLException 
	 **/
	public void initByRst(ResultSet rst) throws SQLException{
		this.m_lSdSeminarID=rst.getLong("lngSdSeminarID");
		this.m_iNoteID=rst.getInt("intNoteID");
		this.m_sNoteNameDE=rst.getString("strNoteNameDE");
		this.m_sNoteWertDE=rst.getFloat("sngNoteWertDE");
		this.m_sNoteECTSGrade=rst.getString("strNoteECTSGrade");
		this.m_sNoteECTSDefinition=rst.getString("strNoteECTSDefinition");
		this.m_sNoteECTSCalc=rst.getFloat("sngNoteECTSCalc");
		this.m_bNoteBestanden=rst.getBoolean("blnNoteBestanden");
		this.m_sNoteCustom1=rst.getString("strNoteCustom1");
		this.m_sNoteCustom2=rst.getString("strNoteCustom2");
		this.m_sNoteCustom3=rst.getString("strNoteCustom3");	
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
	public Note(){}	
	
	/**
	 * Konstruktor mit Indexwerten (eindeutig) der assoziierten Datenbanktabelle
	 * @throws NamingException 
	 * @throws SQLException 
	 **/
	public Note(long lngSdSeminarID, int intNoteID) throws SQLException, NamingException{
		this.init(lngSdSeminarID, intNoteID);
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
	public Note(ResultSet rst) throws SQLException{
		this.initByRst(rst);
		this.m_bIsDirty = false;
	}
  }//Klassenende
