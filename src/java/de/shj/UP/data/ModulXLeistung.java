
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
 *  Module: Exam
Credits that must be passed to complete a module.
 *  
 *  ALLGEMEINE INFORMATION ZU DEN OBJEKTEN DIESES PACKAGES:
 *  Diese Klasse kapselt die Tabelle 'ModulXLeistung' in 
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
public class ModulXLeistung extends shjCore{

	private static final long serialVersionUID = 7L;

////////////////////////////////////////////////////////////////
// 1.   P R I V A T E   D E K L A R A T I O N E N
////////////////////////////////////////////////////////////////
	
	private boolean m_bIsDirty = false;

	private long m_lSdSeminarID;
	private long m_lModulID;
	private long m_lLeistungID;
	private boolean m_bModulLeistungTranskript;
	private float m_sMinCreditPts;
	private String m_sCustom1;
	private String m_sCustom2;
	private String m_sCustom3;
	private String m_sCustom1_en;
	private String m_sCustom2_en;
	private String m_sCustom3_en;

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
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdModulXLeistung.lngSdSeminarID</Code>
	 **/
	public long getSdSeminarID(){
		return this.m_lSdSeminarID;
	}

	/**
	 * Seminar
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdModulXLeistung.lngSdSeminarID</Code>
	 **/	
	public void setSdSeminarID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lSdSeminarID));
		this.m_lSdSeminarID=value;
	}
	

	/**
	 * Id of module
	 * @return Id of module
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdModulXLeistung.lngModulID</Code>
	 **/
	public long getModulID(){
		return this.m_lModulID;
	}

	/**
	 * Id of module
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdModulXLeistung.lngModulID</Code>
	 **/	
	public void setModulID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lModulID));
		this.m_lModulID=value;
	}
	

	/**
	 * Id of credit.
	 * @return Id of credit.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdModulXLeistung.lngLeistungID</Code>
	 **/
	public long getLeistungID(){
		return this.m_lLeistungID;
	}

	/**
	 * Id of credit.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdModulXLeistung.lngLeistungID</Code>
	 **/	
	public void setLeistungID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lLeistungID));
		this.m_lLeistungID=value;
	}
	

	/**
	 * Show this on a transcript? Or is it just for configuration purposes, such as 'Hauptstudiumsmodule' or 'Basismodule'? (This is new in version 6-03 of May 23, 2005)
	 * @return Show this on a transcript? Or is it just for configuration purposes, such as 'Hauptstudiumsmodule' or 'Basismodule'? (This is new in version 6-03 of May 23, 2005)
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdModulXLeistung.blnModulLeistungTranskript</Code>
	 **/
	public boolean getModulLeistungTranskript(){
		return this.m_bModulLeistungTranskript;
	}

	/**
	 * Show this on a transcript? Or is it just for configuration purposes, such as 'Hauptstudiumsmodule' or 'Basismodule'? (This is new in version 6-03 of May 23, 2005)
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdModulXLeistung.blnModulLeistungTranskript</Code>
	 **/	
	public void setModulLeistungTranskript(boolean value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_bModulLeistungTranskript));
		this.m_bModulLeistungTranskript=value;
	}
	

	/**
	 * The minimum credit points a student needs in this credit in order to count for this module.
	 * @return The minimum credit points a student needs in this credit in order to count for this module.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdModulXLeistung.sngMinCreditPts</Code>
	 **/
	public float getMinCreditPts(){
		return this.m_sMinCreditPts;
	}

	/**
	 * The minimum credit points a student needs in this credit in order to count for this module.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdModulXLeistung.sngMinCreditPts</Code>
	 **/	
	public void setMinCreditPts(float value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_sMinCreditPts));
		this.m_sMinCreditPts=value;
	}
	

	/**
	 * Custom
	 * @return Custom
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdModulXLeistung.strCustom1</Code>
	 **/
	public String getCustom1(){
		return this.m_sCustom1;
	}

	/**
	 * Custom
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdModulXLeistung.strCustom1</Code>
	 **/	
	public void setCustom1(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sCustom1)));
		this.m_sCustom1=value;
	}
	

	/**
	 * Custom
	 * @return Custom
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdModulXLeistung.strCustom2</Code>
	 **/
	public String getCustom2(){
		return this.m_sCustom2;
	}

	/**
	 * Custom
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdModulXLeistung.strCustom2</Code>
	 **/	
	public void setCustom2(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sCustom2)));
		this.m_sCustom2=value;
	}
	

	/**
	 * Custom
	 * @return Custom
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdModulXLeistung.strCustom3</Code>
	 **/
	public String getCustom3(){
		return this.m_sCustom3;
	}

	/**
	 * Custom
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdModulXLeistung.strCustom3</Code>
	 **/	
	public void setCustom3(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sCustom3)));
		this.m_sCustom3=value;
	}
	

	/**
	 * Custom English.
	 * @return Custom English.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdModulXLeistung.strCustom1_en</Code>
	 **/
	public String getCustom1_en(){
		return this.m_sCustom1_en;
	}

	/**
	 * Custom English.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdModulXLeistung.strCustom1_en</Code>
	 **/	
	public void setCustom1_en(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sCustom1_en)));
		this.m_sCustom1_en=value;
	}
	

	/**
	 * Custom English.
	 * @return Custom English.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdModulXLeistung.strCustom2_en</Code>
	 **/
	public String getCustom2_en(){
		return this.m_sCustom2_en;
	}

	/**
	 * Custom English.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdModulXLeistung.strCustom2_en</Code>
	 **/	
	public void setCustom2_en(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sCustom2_en)));
		this.m_sCustom2_en=value;
	}
	

	/**
	 * Custom English.
	 * @return Custom English.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdModulXLeistung.strCustom3_en</Code>
	 **/
	public String getCustom3_en(){
		return this.m_sCustom3_en;
	}

	/**
	 * Custom English.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdModulXLeistung.strCustom3_en</Code>
	 **/	
	public void setCustom3_en(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sCustom3_en)));
		this.m_sCustom3_en=value;
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
			"<ModulID>" + m_lModulID + "</ModulID>"  + 
			"<LeistungID>" + m_lLeistungID + "</LeistungID>"  + 
			"<ModulLeistungTranskript>" + m_bModulLeistungTranskript + "</ModulLeistungTranskript>"  + 
			"<MinCreditPts>" + m_sMinCreditPts + "</MinCreditPts>"  + 
			"<Custom1>" + m_sCustom1 + "</Custom1>"  + 
			"<Custom2>" + m_sCustom2 + "</Custom2>"  + 
			"<Custom3>" + m_sCustom3 + "</Custom3>"  + 
			"<Custom1_en>" + m_sCustom1_en + "</Custom1_en>"  + 
			"<Custom2_en>" + m_sCustom2_en + "</Custom2_en>"  + 
			"<Custom3_en>" + m_sCustom3_en + "</Custom3_en>" ;
	}

////////////////////////////////////////////////////////////////
// 4.   S Q L  U T I L I T I E S
////////////////////////////////////////////////////////////////


	/**
	 * Where-SQL für eindeutigen Index dieses Objekte in der Datenbank (in Tabelle 'tblSdModulXLeistung.').
	 * @return SQL-String wie z.B. "Id1=? and Id2=?"
	 **/
	private String getSQLWhereClause(){
		return 
			"\"lngSdSeminarID\"=? AND " + 
			"\"lngModulID\"=? AND " + 
			"\"lngLeistungID\"=?";
	}

	/**
	 * SQL-Befehl zum Löschen des Objekts in der Datenbank (aus Tabelle 'tblSdModulXLeistung.')
	 * @return SQL-String zum Löschen des Objekts, z.B. "delete from T1 where (Id1=?);"
	 **/
	private String toDBDeleteString(){
		return "delete from \"tblSdModulXLeistung\" where ( " + this.getSQLWhereClause() + ");";
	}

	/**
 	 * Setzt die Werte für alle Platzhalter in {@link #getSQLWhereClause()}.
	 * @param ii Offset für die Parameter (inklusive).
	 * @throws SQLException 
	 **/
	private void pokeWhere(int ii, PreparedStatement ps) throws SQLException{
		
		ps.setLong(ii++, m_lSdSeminarID);
		ps.setLong(ii++, m_lModulID);
		ps.setLong(ii++, m_lLeistungID);
	}

	/**
  	 * Füllt das SQL-Statement mit den aktuellen Werten des Objekts
	 * @throws SQLException
	 **/
	private void pokeStatement(PreparedStatement ps) throws SQLException{
		
		ps.setLong(1, m_lSdSeminarID);
		ps.setLong(2, m_lModulID);
		ps.setLong(3, m_lLeistungID);
		ps.setBoolean(4, m_bModulLeistungTranskript);
		ps.setFloat(5, m_sMinCreditPts);
		ps.setString(6, m_sCustom1);
		ps.setString(7, m_sCustom2);
		ps.setString(8, m_sCustom3);
		ps.setString(9, m_sCustom1_en);
		ps.setString(10, m_sCustom2_en);
		ps.setString(11, m_sCustom3_en);

	}

	/**
	 * SQL-Befehl zum Speichern der Objekteigenschaften in die Datenbank (Tabelle 'tblSdModulXLeistung.')
	 * @return PreparedStatemtent, das nach Aufruf von {@link #pokeStatement(PreparedStatement)} und {@link #execute()} das Update ausführt.
	 **/
	private String toDBUpdateString(){
		return "update \"tblSdModulXLeistung\" set " +
			"\"lngSdSeminarID\"=?, " +
			"\"lngModulID\"=?, " +
			"\"lngLeistungID\"=?, " +
			"\"blnModulLeistungTranskript\"=?, " +
			"\"sngMinCreditPts\"=?, " +
			"\"strCustom1\"=?, " +
			"\"strCustom2\"=?, " +
			"\"strCustom3\"=?, " +
			"\"strCustom1_en\"=?, " +
			"\"strCustom2_en\"=?, " +
			"\"strCustom3_en\"=?" +
			" where (" + this.getSQLWhereClause() + ");";
	}

	/**
	 * SQL-Befehl zum Hinzufügen des Objekts in die Datenbank (Tabelle 'tblSdModulXLeistung.')
	 * @return SQL-String "insert into...".
	 **/
	private String toDBAddString(){
		return "insert into \"tblSdModulXLeistung\" ( " +
			"\"lngSdSeminarID\", \"lngModulID\", \"lngLeistungID\", \"blnModulLeistungTranskript\", \"sngMinCreditPts\", \"strCustom1\", \"strCustom2\", \"strCustom3\", \"strCustom1_en\", \"strCustom2_en\", \"strCustom3_en\" ) VALUES ( ?,?,?,?,?,?,?,?,?,?,?);";
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
	private void init(long lngSdSeminarID, long lngModulID, long lngLeistungID) throws SQLException, NamingException{

		this.m_lSdSeminarID=lngSdSeminarID;

		this.m_lModulID=lngModulID;

		this.m_lLeistungID=lngLeistungID;

		
		PreparedStatement pstm = prepareStatement("select * from \"tblSdModulXLeistung\" where (" + this.getSQLWhereClause() + ");");
		pokeWhere(1,pstm);

		ResultSet rst=pstm.executeQuery();
		if(rst.next()) this.initByRst(rst);
		rst.close();
		this.disconnect();
		rst=null;
	}	
	
	/**
	 * Lade die Objekteigenschaften aus einem ResultSet.
	 * param rst ResultSet mit allen Spalten der Tabelle 'tblSdModulXLeistung'
	 * @throws SQLException 
	 **/
	protected void initByRst(ResultSet rst) throws SQLException{
		this.m_lSdSeminarID=rst.getLong("lngSdSeminarID");
		this.m_lModulID=rst.getLong("lngModulID");
		this.m_lLeistungID=rst.getLong("lngLeistungID");
		this.m_bModulLeistungTranskript=rst.getBoolean("blnModulLeistungTranskript");
		this.m_sMinCreditPts=rst.getFloat("sngMinCreditPts");
		this.m_sCustom1=rst.getString("strCustom1");
		this.m_sCustom2=rst.getString("strCustom2");
		this.m_sCustom3=rst.getString("strCustom3");
		this.m_sCustom1_en=rst.getString("strCustom1_en");
		this.m_sCustom2_en=rst.getString("strCustom2_en");
		this.m_sCustom3_en=rst.getString("strCustom3_en");	
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
	public ModulXLeistung(){}	
	
	/**
	 * Konstruktor mit Indexwerten (eindeutig) der assoziierten Datenbanktabelle
	 * @throws NamingException 
	 * @throws SQLException 
	 **/
	public ModulXLeistung(long lngSdSeminarID, long lngModulID, long lngLeistungID) throws SQLException, NamingException{
		this.init(lngSdSeminarID, lngModulID, lngLeistungID);
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
	public ModulXLeistung(ResultSet rst) throws SQLException{
		this.initByRst(rst);
		this.m_bIsDirty = false;
	}
  }//Klassenende
