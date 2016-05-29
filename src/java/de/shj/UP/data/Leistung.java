
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

import de.shj.UP.util.ResultSetSHJ;

/**
 *  Module: Exam
Credits as defined in this seminar.
 *  
 *  ALLGEMEINE INFORMATION ZU DEN OBJEKTEN DIESES PACKAGES:
 *  Diese Klasse kapselt die Tabelle 'Leistung' in 
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
public class Leistung extends shjCore{

	private static final long serialVersionUID = 7L;

////////////////////////////////////////////////////////////////
// 1.   P R I V A T E   D E K L A R A T I O N E N
////////////////////////////////////////////////////////////////
	
	private boolean m_bIsDirty = false;

	private long m_lSdSeminarID;
	private long m_lLeistungID;
	private String m_sLeistungBezeichnung;
	private String m_sLeistungBezeichnung_en;
	private String m_sLeistungBeschreibung;
	private String m_sLeistungBeschreibung_en;
	private String m_sLeistungZuordnung;
	private float m_sLeistungCreditPts;
	private int m_iLeistungCommitmentMode;
	private String m_sLeistungCustom1;
	private String m_sLeistungCustom2;
	private String m_sLeistungCustom3;
 	public final int m_intCOMMITMENT_MODE_NORMAL				= 0;
 	public final int m_intCOMMITMENT_MODE_STUDIENLEISTUNG_ONLY	= 1;
 	public final int m_intCOMMITMENT_MODE_PRUEFUNGSLEISTUNG_ONLY= 2;
 	public final int m_intCOMMITMENT_MODE_NORMAL_GENERIC				= 100;
 	public final int m_intCOMMITMENT_MODE_STUDIENLEISTUNG_ONLY_GENERIC	= 101;
 	public final int m_intCOMMITMENT_MODE_PRUEFUNGSLEISTUNG_ONLY_GENERIC= 102;

////////////////////////////////////////////////////////////////
// 2.   Ö F F E N T L I C H E  E I G E N S C H A F T E N
////////////////////////////////////////////////////////////////

 	
 	/**
 	 * Generic credits can be issued many times. A generic credit is, 
 	 * for example, a credit in a student's minor subject.<br /
 	 * The standard case is, however, that a credit can be issued only 
 	 * once.<br /<br /
 	 * The property 'being generic' is controlled by <CodeLeistungCommitmentMode()</Code.
 	 * @version 6-04-02 (May 30, 2005).
 	 * @see #m_intCOMMITMENT_MODE_NORMAL
 	 * @see #m_intCOMMITMENT_MODE_NORMAL_GENERIC
 	 * @see #m_intCOMMITMENT_MODE_PRUEFUNGSLEISTUNG_ONLY
 	 * @see #m_intCOMMITMENT_MODE_PRUEFUNGSLEISTUNG_ONLY_GENERIC
 	 * @see #m_intCOMMITMENT_MODE_STUDIENLEISTUNG_ONLY
 	 * @see #m_intCOMMITMENT_MODE_STUDIENLEISTUNG_ONLY_GENERIC
 	 * @return 'true,' if this credit is a 'generic' one.
 	 */
 	public boolean isGeneric(){
 		return (this.m_iLeistungCommitmentMode >= 100);
 	}
 	
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
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdLeistung.lngSdSeminarID</Code>
	 **/
	public long getSdSeminarID(){
		return this.m_lSdSeminarID;
	}

	/**
	 * Seminar
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdLeistung.lngSdSeminarID</Code>
	 **/	
	public void setSdSeminarID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lSdSeminarID));
		this.m_lSdSeminarID=value;
	}
	

	/**
	 * Id of credit.
	 * @return Id of credit.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdLeistung.lngLeistungID</Code>
	 **/
	public long getLeistungID(){
		return this.m_lLeistungID;
	}

	/**
	 * Id of credit.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdLeistung.lngLeistungID</Code>
	 **/	
	public void setLeistungID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lLeistungID));
		this.m_lLeistungID=value;
	}
	

	/**
	 * Credit name.
	 * @return Credit name.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdLeistung.strLeistungBezeichnung</Code>
	 **/
	public String getLeistungBezeichnung(){
		return this.m_sLeistungBezeichnung;
	}

	/**
	 * Credit name.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdLeistung.strLeistungBezeichnung</Code>
	 **/	
	public void setLeistungBezeichnung(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sLeistungBezeichnung)));
		this.m_sLeistungBezeichnung=value;
	}
	

	/**
	 * English name of credit.
	 * @return English name of credit.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdLeistung.strLeistungBezeichnung_en</Code>
	 **/
	public String getLeistungBezeichnung_en(){
		return this.m_sLeistungBezeichnung_en;
	}

	/**
	 * English name of credit.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdLeistung.strLeistungBezeichnung_en</Code>
	 **/	
	public void setLeistungBezeichnung_en(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sLeistungBezeichnung_en)));
		this.m_sLeistungBezeichnung_en=value;
	}
	

	/**
	 * Description of credit
	 * @return Description of credit
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdLeistung.strLeistungBeschreibung</Code>
	 **/
	public String getLeistungBeschreibung(){
		return this.m_sLeistungBeschreibung;
	}

	/**
	 * Description of credit
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdLeistung.strLeistungBeschreibung</Code>
	 **/	
	public void setLeistungBeschreibung(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sLeistungBeschreibung)));
		this.m_sLeistungBeschreibung=value;
	}
	

	/**
	 * English Description of credit
	 * @return English Description of credit
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdLeistung.strLeistungBeschreibung_en</Code>
	 **/
	public String getLeistungBeschreibung_en(){
		return this.m_sLeistungBeschreibung_en;
	}

	/**
	 * English Description of credit
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdLeistung.strLeistungBeschreibung_en</Code>
	 **/	
	public void setLeistungBeschreibung_en(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sLeistungBeschreibung_en)));
		this.m_sLeistungBeschreibung_en=value;
	}
	

	/**
	 * Mapping of this credit.
	 * @return Mapping of this credit.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdLeistung.strLeistungZuordnung</Code>
	 **/
	public String getLeistungZuordnung(){
		return this.m_sLeistungZuordnung;
	}

	/**
	 * Mapping of this credit.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdLeistung.strLeistungZuordnung</Code>
	 **/	
	public void setLeistungZuordnung(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sLeistungZuordnung)));
		this.m_sLeistungZuordnung=value;
	}
	

	/**
	 * Credit points attached to this credit.
	 * @return Credit points attached to this credit.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdLeistung.sngLeistungCreditPts</Code>
	 **/
	public float getLeistungCreditPts(){
		return this.m_sLeistungCreditPts;
	}

	/**
	 * Credit points attached to this credit.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdLeistung.sngLeistungCreditPts</Code>
	 **/	
	public void setLeistungCreditPts(float value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != m_sLeistungCreditPts));
		this.m_sLeistungCreditPts=value;
	}
	

	/**
	 * The default CommitmentMode to suggest for this Leistung. Commitment modes are: A) Values below 100 indicate that SignUp accepts only 1 passed credit; after the credit is passed, commitments are altogether forbidden. No two commitments to this credit are possible simultaneously (standard) 0=student can commit for both Pruefungsleistung or Studienleistung. 1=student can commit for STUDIENLEISTUNG ONLY. 2=student can commit for PRUEFUNGSLEISTUNG ONLY.B) Those values + 100 indicate, that multiple commitments and passed credits are allowed. (The so-called 'generic' credit, i.e. a generic term for 'minor subject'. This differentiation "B)" is new on May 30, 2005.
	 * @return The default CommitmentMode to suggest for this Leistung. Commitment modes are: A) Values below 100 indicate that SignUp accepts only 1 passed credit; after the credit is passed, commitments are altogether forbidden. No two commitments to this credit are possible simultaneously (standard) 0=student can commit for both Pruefungsleistung or Studienleistung. 1=student can commit for STUDIENLEISTUNG ONLY. 2=student can commit for PRUEFUNGSLEISTUNG ONLY.B) Those values + 100 indicate, that multiple commitments and passed credits are allowed. (The so-called 'generic' credit, i.e. a generic term for 'minor subject'. This differentiation "B)" is new on May 30, 2005.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdLeistung.intLeistungCommitmentMode</Code>
	 **/
	public int getLeistungCommitmentMode(){
		return this.m_iLeistungCommitmentMode;
	}

	/**
	 * The default CommitmentMode to suggest for this Leistung. Commitment modes are: A) Values below 100 indicate that SignUp accepts only 1 passed credit; after the credit is passed, commitments are altogether forbidden. No two commitments to this credit are possible simultaneously (standard) 0=student can commit for both Pruefungsleistung or Studienleistung. 1=student can commit for STUDIENLEISTUNG ONLY. 2=student can commit for PRUEFUNGSLEISTUNG ONLY.B) Those values + 100 indicate, that multiple commitments and passed credits are allowed. (The so-called 'generic' credit, i.e. a generic term for 'minor subject'. This differentiation "B)" is new on May 30, 2005.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdLeistung.intLeistungCommitmentMode</Code>
	 **/	
	public void setLeistungCommitmentMode(int value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_iLeistungCommitmentMode));
		this.m_iLeistungCommitmentMode=value;
	}
	

	/**
	 * Custom
	 * @return Custom
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdLeistung.strLeistungCustom1</Code>
	 **/
	public String getLeistungCustom1(){
		return this.m_sLeistungCustom1;
	}

	/**
	 * Custom
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdLeistung.strLeistungCustom1</Code>
	 **/	
	public void setLeistungCustom1(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sLeistungCustom1)));
		this.m_sLeistungCustom1=value;
	}
	

	/**
	 * Custom
	 * @return Custom
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdLeistung.strLeistungCustom2</Code>
	 **/
	public String getLeistungCustom2(){
		return this.m_sLeistungCustom2;
	}

	/**
	 * Custom
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdLeistung.strLeistungCustom2</Code>
	 **/	
	public void setLeistungCustom2(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sLeistungCustom2)));
		this.m_sLeistungCustom2=value;
	}
	

	/**
	 * Custom
	 * @return Custom
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdLeistung.strLeistungCustom3</Code>
	 **/
	public String getLeistungCustom3(){
		return this.m_sLeistungCustom3;
	}

	/**
	 * Custom
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdLeistung.strLeistungCustom3</Code>
	 **/	
	public void setLeistungCustom3(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sLeistungCustom3)));
		this.m_sLeistungCustom3=value;
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
			"<LeistungID>" + m_lLeistungID + "</LeistungID>"  + 
			"<LeistungBezeichnung>" + m_sLeistungBezeichnung + "</LeistungBezeichnung>"  + 
			"<LeistungBezeichnung_en>" + m_sLeistungBezeichnung_en + "</LeistungBezeichnung_en>"  + 
			"<LeistungBeschreibung>" + m_sLeistungBeschreibung + "</LeistungBeschreibung>"  + 
			"<LeistungBeschreibung_en>" + m_sLeistungBeschreibung_en + "</LeistungBeschreibung_en>"  + 
			"<LeistungZuordnung>" + m_sLeistungZuordnung + "</LeistungZuordnung>"  + 
			"<LeistungCreditPts>" + m_sLeistungCreditPts + "</LeistungCreditPts>"  + 
			"<LeistungCommitmentMode>" + m_iLeistungCommitmentMode + "</LeistungCommitmentMode>"  + 
			"<LeistungCustom1>" + m_sLeistungCustom1 + "</LeistungCustom1>"  + 
			"<LeistungCustom2>" + m_sLeistungCustom2 + "</LeistungCustom2>"  + 
			"<LeistungCustom3>" + m_sLeistungCustom3 + "</LeistungCustom3>" ;
	}

////////////////////////////////////////////////////////////////
// 4.   S Q L  U T I L I T I E S
////////////////////////////////////////////////////////////////


	/**
	 * Where-SQL für eindeutigen Index dieses Objekte in der Datenbank (in Tabelle 'tblSdLeistung.').
	 * @return SQL-String wie z.B. "Id1=? and Id2=?"
	 **/
	private String getSQLWhereClause(){
		return 
			"\"lngSdSeminarID\"=? AND " + 
			"\"lngLeistungID\"=?";
	}

	/**
	 * SQL-Befehl zum Löschen des Objekts in der Datenbank (aus Tabelle 'tblSdLeistung.')
	 * @return SQL-String zum Löschen des Objekts, z.B. "delete from T1 where (Id1=?);"
	 **/
	private String toDBDeleteString(){
		return "delete from \"tblSdLeistung\" where ( " + this.getSQLWhereClause() + ");";
	}

	/**
 	 * Setzt die Werte für alle Platzhalter in {@link #getSQLWhereClause()}.
	 * @param ii Offset für die Parameter (inklusive).
	 * @throws SQLException 
	 **/
	private void pokeWhere(int ii, PreparedStatement ps) throws SQLException{
		
		ps.setLong(ii++, m_lSdSeminarID);
		ps.setLong(ii++, m_lLeistungID);
	}

	/**
  	 * Füllt das SQL-Statement mit den aktuellen Werten des Objekts
	 * @throws SQLException
	 **/
	private void pokeStatement(PreparedStatement ps) throws SQLException{
		
		ps.setLong(1, m_lSdSeminarID);
		ps.setLong(2, m_lLeistungID);
		ps.setString(3, m_sLeistungBezeichnung);
		ps.setString(4, m_sLeistungBezeichnung_en);
		ps.setString(5, m_sLeistungBeschreibung);
		ps.setString(6, m_sLeistungBeschreibung_en);
		ps.setString(7, m_sLeistungZuordnung);
		ps.setFloat(8, m_sLeistungCreditPts);
		ps.setInt(9, m_iLeistungCommitmentMode);
		ps.setString(10, m_sLeistungCustom1);
		ps.setString(11, m_sLeistungCustom2);
		ps.setString(12, m_sLeistungCustom3);

	}

	/**
	 * SQL-Befehl zum Speichern der Objekteigenschaften in die Datenbank (Tabelle 'tblSdLeistung.')
	 * @return PreparedStatemtent, das nach Aufruf von {@link #pokeStatement(PreparedStatement)} und {@link #execute()} das Update ausführt.
	 **/
	private String toDBUpdateString(){
		return "update \"tblSdLeistung\" set " +
			"\"lngSdSeminarID\"=?, " +
			"\"lngLeistungID\"=?, " +
			"\"strLeistungBezeichnung\"=?, " +
			"\"strLeistungBezeichnung_en\"=?, " +
			"\"strLeistungBeschreibung\"=?, " +
			"\"strLeistungBeschreibung_en\"=?, " +
			"\"strLeistungZuordnung\"=?, " +
			"\"sngLeistungCreditPts\"=?, " +
			"\"intLeistungCommitmentMode\"=?, " +
			"\"strLeistungCustom1\"=?, " +
			"\"strLeistungCustom2\"=?, " +
			"\"strLeistungCustom3\"=?" +
			" where (" + this.getSQLWhereClause() + ");";
	}

	/**
	 * SQL-Befehl zum Hinzufügen des Objekts in die Datenbank (Tabelle 'tblSdLeistung.')
	 * @return SQL-String "insert into...".
	 **/
	private String toDBAddString(){
		return "insert into \"tblSdLeistung\" ( " +
			"\"lngSdSeminarID\", \"lngLeistungID\", \"strLeistungBezeichnung\", \"strLeistungBezeichnung_en\", \"strLeistungBeschreibung\", \"strLeistungBeschreibung_en\", \"strLeistungZuordnung\", \"sngLeistungCreditPts\", \"intLeistungCommitmentMode\", \"strLeistungCustom1\", \"strLeistungCustom2\", \"strLeistungCustom3\" ) VALUES ( ?,?,?,?,?,?,?,?,?,?,?,?);";
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
	private void init(long lngSdSeminarID, long lngLeistungID) throws SQLException, NamingException{

		this.m_lSdSeminarID=lngSdSeminarID;

		this.m_lLeistungID=lngLeistungID;

		
		PreparedStatement pstm = prepareStatement("select * from \"tblSdLeistung\" where (" + this.getSQLWhereClause() + ");");
		pokeWhere(1,pstm);

		ResultSet rst=pstm.executeQuery();
		if(rst.next()) this.initByRst(rst);
		rst.close();
		this.disconnect();
		rst=null;
	}	
	
	/**
	 * Lade die Objekteigenschaften aus einem ResultSet.
	 * param rst ResultSet mit allen Spalten der Tabelle 'tblSdLeistung'
	 * @throws SQLException 
	 **/
	protected void initByRst(ResultSet rst) throws SQLException{
		this.m_lSdSeminarID=rst.getLong("lngSdSeminarID");
		this.m_lLeistungID=rst.getLong("lngLeistungID");
		this.m_sLeistungBezeichnung=rst.getString("strLeistungBezeichnung");
		this.m_sLeistungBezeichnung_en=rst.getString("strLeistungBezeichnung_en");
		this.m_sLeistungBeschreibung=rst.getString("strLeistungBeschreibung");
		this.m_sLeistungBeschreibung_en=rst.getString("strLeistungBeschreibung_en");
		this.m_sLeistungZuordnung=rst.getString("strLeistungZuordnung");
		this.m_sLeistungCreditPts=rst.getFloat("sngLeistungCreditPts");
		this.m_iLeistungCommitmentMode=rst.getInt("intLeistungCommitmentMode");
		this.m_sLeistungCustom1=rst.getString("strLeistungCustom1");
		this.m_sLeistungCustom2=rst.getString("strLeistungCustom2");
		this.m_sLeistungCustom3=rst.getString("strLeistungCustom3");	
	}	
	
	/**
	 * Lade die Objekteigenschaften aus einem ResultSet.
	 * param rst ResultSet mit allen Spalten der Tabelle 'tblSdLeistung'
	 * @throws SQLException 
	 **/
	protected void initByRstSHJ(ResultSetSHJ rst) throws SQLException{
		this.m_lSdSeminarID=rst.getLong("lngSdSeminarID");
		this.m_lLeistungID=rst.getLong("lngLeistungID");
		this.m_sLeistungBezeichnung=rst.getString("strLeistungBezeichnung");
		this.m_sLeistungBezeichnung_en=rst.getString("strLeistungBezeichnung_en");
		this.m_sLeistungBeschreibung=rst.getString("strLeistungBeschreibung");
		this.m_sLeistungBeschreibung_en=rst.getString("strLeistungBeschreibung_en");
		this.m_sLeistungZuordnung=rst.getString("strLeistungZuordnung");
		this.m_sLeistungCreditPts=rst.getFloat("sngLeistungCreditPts");
		this.m_iLeistungCommitmentMode=rst.getInt("intLeistungCommitmentMode");
		this.m_sLeistungCustom1=rst.getString("strLeistungCustom1");
		this.m_sLeistungCustom2=rst.getString("strLeistungCustom2");
		this.m_sLeistungCustom3=rst.getString("strLeistungCustom3");	
	}	
	
	/**
	 * Lade die Objekteigenschaften aus einer XML-Node.
	 * param node XML-Node mit allen Eigenschaften als Tags.
	 * @throws ParseException (Datum muss im ISO-Format yyyy-MM-dd übergeben werden).
	 **/
	private void initByNode(Node node) throws ParseException{
		this.m_lSdSeminarID=Long.parseLong(shjNodeValue(node, "SdSeminarID"));
		this.m_lLeistungID=Long.parseLong(shjNodeValue(node, "LeistungID"));
		this.m_sLeistungBezeichnung=(shjNodeValue(node, "LeistungBezeichnung"));
		this.m_sLeistungBezeichnung_en=(shjNodeValue(node, "LeistungBezeichnung_en"));
		this.m_sLeistungBeschreibung=(shjNodeValue(node, "LeistungBeschreibung"));
		this.m_sLeistungBeschreibung_en=(shjNodeValue(node, "LeistungBeschreibung_en"));
		this.m_sLeistungZuordnung=(shjNodeValue(node, "LeistungZuordnung"));
		this.m_sLeistungCreditPts=Float.valueOf(shjNodeValue(node, "LeistungCreditPts")).floatValue();
		this.m_iLeistungCommitmentMode=Integer.parseInt(shjNodeValue(node, "LeistungCommitmentMode"));
		this.m_sLeistungCustom1=(shjNodeValue(node, "LeistungCustom1"));
		this.m_sLeistungCustom2=(shjNodeValue(node, "LeistungCustom2"));
		this.m_sLeistungCustom3=(shjNodeValue(node, "LeistungCustom3"));
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
	public Leistung(){}	
	
	/**
	 * Konstruktor mit Indexwerten (eindeutig) der assoziierten Datenbanktabelle
	 * @throws NamingException 
	 * @throws SQLException 
	 **/
	public Leistung(long lngSdSeminarID, long lngLeistungID) throws SQLException, NamingException{
		this.init(lngSdSeminarID, lngLeistungID);
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
	public Leistung(ResultSet rst) throws SQLException{
		this.initByRst(rst);
		this.m_bIsDirty = false;
	}

	/**
	 * Konstruktor per XML-Darstellung des Objekts.
	 * @throws ParseException, if a date can't be read.
	 **/
	public Leistung(Node node) throws ParseException{
		this.initByNode(node);
		this.m_bIsDirty = false;
	}

  }//Klassenende
