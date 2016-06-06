
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
import java.text.SimpleDateFormat;
import java.text.ParseException;
import org.w3c.dom.Node; 

/**
 *  Modules: SignUp, Kvv, Exam
Maps course to coursetype, n:m, all within one seminar. This mapping is a student"s view on the course: it"s the course as a given coursetype.
 *  
 *  ALLGEMEINE INFORMATION ZU DEN OBJEKTEN DIESES PACKAGES:
 *  Diese Klasse kapselt die Tabelle 'KursXKurstyp' in 
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
public class KursXKurstyp extends shjCore{

	private static final long serialVersionUID = 7L;

////////////////////////////////////////////////////////////////
// 1.   P R I V A T E   D E K L A R A T I O N E N
////////////////////////////////////////////////////////////////
	
	private boolean m_bIsDirty = false;

	private long m_lSeminarID;
	private long m_lKurstypID;
	private long m_lKursID;
	private String m_sBemerkung;
	private String m_sBemerkung_en;
	private float m_sKursCreditPts;
	private int m_iKursSemesterMin;
	private int m_iKursSemesterMax;
	private int m_iKursCommitmentMode;
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
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKursXKurstyp.lngSeminarID</Code>
	 **/
	public long getSeminarID(){
		return this.m_lSeminarID;
	}

	/**
	 * Seminar
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKursXKurstyp.lngSeminarID</Code>
	 **/	
	public void setSeminarID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lSeminarID));
		this.m_lSeminarID=value;
	}
	

	/**
	 * Id of coursetype
	 * @return Id of coursetype
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKursXKurstyp.lngKurstypID</Code>
	 **/
	public long getKurstypID(){
		return this.m_lKurstypID;
	}

	/**
	 * Id of coursetype
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKursXKurstyp.lngKurstypID</Code>
	 **/	
	public void setKurstypID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lKurstypID));
		this.m_lKurstypID=value;
	}
	

	/**
	 * Id of course
	 * @return Id of course
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKursXKurstyp.lngKursID</Code>
	 **/
	public long getKursID(){
		return this.m_lKursID;
	}

	/**
	 * Id of course
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKursXKurstyp.lngKursID</Code>
	 **/	
	public void setKursID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lKursID));
		this.m_lKursID=value;
	}
	

	/**
	 * Remark
	 * @return Remark
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKursXKurstyp.strBemerkung</Code>
	 **/
	public String getBemerkung(){
		return this.m_sBemerkung;
	}

	/**
	 * Remark
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKursXKurstyp.strBemerkung</Code>
	 **/	
	public void setBemerkung(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sBemerkung)));
		this.m_sBemerkung=value;
	}
	

	/**
	 * English version of this remark
	 * @return English version of this remark
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKursXKurstyp.strBemerkung_en</Code>
	 **/
	public String getBemerkung_en(){
		return this.m_sBemerkung_en;
	}

	/**
	 * English version of this remark
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKursXKurstyp.strBemerkung_en</Code>
	 **/	
	public void setBemerkung_en(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sBemerkung_en)));
		this.m_sBemerkung_en=value;
	}
	

	/**
	 * Credit points that are handed out for this course as this coursetype.
	 * @return Credit points that are handed out for this course as this coursetype.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKursXKurstyp.sngKursCreditPts</Code>
	 **/
	public float getKursCreditPts(){
		return this.m_sKursCreditPts;
	}

	/**
	 * Credit points that are handed out for this course as this coursetype.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKursXKurstyp.sngKursCreditPts</Code>
	 **/	
	public void setKursCreditPts(float value){
		this.m_bIsDirty = (this.m_bIsDirty || value != this.m_sKursCreditPts);
		this.m_sKursCreditPts=value;
	}
	

	/**
	 * Minimum semester the student must have in order to attend this course. Unused.
	 * @return Minimum semester the student must have in order to attend this course. Unused.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKursXKurstyp.intKursSemesterMin</Code>
	 **/
	public int getKursSemesterMin(){
		return this.m_iKursSemesterMin;
	}

	/**
	 * Minimum semester the student must have in order to attend this course. Unused.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKursXKurstyp.intKursSemesterMin</Code>
	 **/	
	public void setKursSemesterMin(int value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_iKursSemesterMin));
		this.m_iKursSemesterMin=value;
	}
	

	/**
	 * Maximum semester a student can have in order to attend that course. Unused.
	 * @return Maximum semester a student can have in order to attend that course. Unused.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKursXKurstyp.intKursSemesterMax</Code>
	 **/
	public int getKursSemesterMax(){
		return this.m_iKursSemesterMax;
	}

	/**
	 * Maximum semester a student can have in order to attend that course. Unused.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKursXKurstyp.intKursSemesterMax</Code>
	 **/	
	public void setKursSemesterMax(int value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_iKursSemesterMax));
		this.m_iKursSemesterMax=value;
	}
	

	/**
	 * The CommitmentMode for this course. Commitment modes are: 0=student can commit for both Pruefungsleistung or Studienleistung. 1=student can commit for STUDIENLEISTUNG ONLY. 2=student can commit for PRUEFUNGSLEISTUNG ONLY.
	 * @return The CommitmentMode for this course. Commitment modes are: 0=student can commit for both Pruefungsleistung or Studienleistung. 1=student can commit for STUDIENLEISTUNG ONLY. 2=student can commit for PRUEFUNGSLEISTUNG ONLY.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKursXKurstyp.intKursCommitmentMode</Code>
	 **/
	public int getKursCommitmentMode(){
		return this.m_iKursCommitmentMode;
	}

	/**
	 * The CommitmentMode for this course. Commitment modes are: 0=student can commit for both Pruefungsleistung or Studienleistung. 1=student can commit for STUDIENLEISTUNG ONLY. 2=student can commit for PRUEFUNGSLEISTUNG ONLY.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKursXKurstyp.intKursCommitmentMode</Code>
	 **/	
	public void setKursCommitmentMode(int value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_iKursCommitmentMode));
		this.m_iKursCommitmentMode=value;
	}
	

	/**
	 * Custom information
	 * @return Custom information
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKursXKurstyp.strCustom1</Code>
	 **/
	public String getCustom1(){
		return this.m_sCustom1;
	}

	/**
	 * Custom information
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKursXKurstyp.strCustom1</Code>
	 **/	
	public void setCustom1(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sCustom1)));
		this.m_sCustom1=value;
	}
	

	/**
	 * Custom information
	 * @return Custom information
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKursXKurstyp.strCustom2</Code>
	 **/
	public String getCustom2(){
		return this.m_sCustom2;
	}

	/**
	 * Custom information
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKursXKurstyp.strCustom2</Code>
	 **/	
	public void setCustom2(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sCustom2)));
		this.m_sCustom2=value;
	}
	

	/**
	 * Custom information
	 * @return Custom information
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKursXKurstyp.strCustom3</Code>
	 **/
	public String getCustom3(){
		return this.m_sCustom3;
	}

	/**
	 * Custom information
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKursXKurstyp.strCustom3</Code>
	 **/	
	public void setCustom3(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sCustom3)));
		this.m_sCustom3=value;
	}
	

	/**
	 * English version of custom information
	 * @return English version of custom information
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKursXKurstyp.strCustom1_en</Code>
	 **/
	public String getCustom1_en(){
		return this.m_sCustom1_en;
	}

	/**
	 * English version of custom information
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKursXKurstyp.strCustom1_en</Code>
	 **/	
	public void setCustom1_en(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sCustom1_en)));
		this.m_sCustom1_en=value;
	}
	

	/**
	 * English version of custom information
	 * @return English version of custom information
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKursXKurstyp.strCustom2_en</Code>
	 **/
	public String getCustom2_en(){
		return this.m_sCustom2_en;
	}

	/**
	 * English version of custom information
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKursXKurstyp.strCustom2_en</Code>
	 **/	
	public void setCustom2_en(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sCustom2_en)));
		this.m_sCustom2_en=value;
	}
	

	/**
	 * English version of custom information
	 * @return English version of custom information
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKursXKurstyp.strCustom3_en</Code>
	 **/
	public String getCustom3_en(){
		return this.m_sCustom3_en;
	}

	/**
	 * English version of custom information
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKursXKurstyp.strCustom3_en</Code>
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

			"<SeminarID>" + m_lSeminarID + "</SeminarID>"  + 
			"<KurstypID>" + m_lKurstypID + "</KurstypID>"  + 
			"<KursID>" + m_lKursID + "</KursID>"  + 
			"<Bemerkung>" + m_sBemerkung + "</Bemerkung>"  + 
			"<Bemerkung_en>" + m_sBemerkung_en + "</Bemerkung_en>"  + 
			"<KursCreditPts>" + m_sKursCreditPts + "</KursCreditPts>"  + 
			"<KursSemesterMin>" + m_iKursSemesterMin + "</KursSemesterMin>"  + 
			"<KursSemesterMax>" + m_iKursSemesterMax + "</KursSemesterMax>"  + 
			"<KursCommitmentMode>" + m_iKursCommitmentMode + "</KursCommitmentMode>"  + 
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
	 * Where-SQL für eindeutigen Index dieses Objekte in der Datenbank (in Tabelle 'tblBdKursXKurstyp.').
	 * @return SQL-String wie z.B. "Id1=? and Id2=?"
	 **/
	private String getSQLWhereClause(){
		return 
			"\"lngSeminarID\"=? AND " + 
			"\"lngKurstypID\"=? AND " + 
			"\"lngKursID\"=?";
	}

	/**
	 * SQL-Befehl zum Löschen des Objekts in der Datenbank (aus Tabelle 'tblBdKursXKurstyp.')
	 * @return SQL-String zum Löschen des Objekts, z.B. "delete from T1 where (Id1=?);"
	 **/
	private String toDBDeleteString(){
		return "delete from \"tblBdKursXKurstyp\" where ( " + this.getSQLWhereClause() + ");";
	}

	/**
 	 * Setzt die Werte für alle Platzhalter in {@link #getSQLWhereClause()}.
	 * @param ii Offset für die Parameter (inklusive).
	 * @throws SQLException 
	 **/
	private void pokeWhere(int ii, PreparedStatement ps) throws SQLException{
		
		ps.setLong(ii++, m_lSeminarID);
		ps.setLong(ii++, m_lKurstypID);
		ps.setLong(ii++, m_lKursID);
	}

	/**
  	 * Füllt das SQL-Statement mit den aktuellen Werten des Objekts
	 * @throws SQLException
	 **/
	private void pokeStatement(PreparedStatement ps) throws SQLException{
		
		ps.setLong(1, m_lSeminarID);
		ps.setLong(2, m_lKurstypID);
		ps.setLong(3, m_lKursID);
		ps.setString(4, m_sBemerkung);
		ps.setString(5, m_sBemerkung_en);
		ps.setFloat(6, m_sKursCreditPts);
		ps.setInt(7, m_iKursSemesterMin);
		ps.setInt(8, m_iKursSemesterMax);
		ps.setInt(9, m_iKursCommitmentMode);
		ps.setString(10, m_sCustom1);
		ps.setString(11, m_sCustom2);
		ps.setString(12, m_sCustom3);
		ps.setString(13, m_sCustom1_en);
		ps.setString(14, m_sCustom2_en);
		ps.setString(15, m_sCustom3_en);

	}

	/**
	 * SQL-Befehl zum Speichern der Objekteigenschaften in die Datenbank (Tabelle 'tblBdKursXKurstyp.')
	 * @return PreparedStatemtent, das nach Aufruf von {@link #pokeStatement(PreparedStatement)} und {@link #execute()} das Update ausführt.
	 **/
	private String toDBUpdateString(){
		return "update \"tblBdKursXKurstyp\" set " +
			"\"lngSeminarID\"=?, " +
			"\"lngKurstypID\"=?, " +
			"\"lngKursID\"=?, " +
			"\"strBemerkung\"=?, " +
			"\"strBemerkung_en\"=?, " +
			"\"sngKursCreditPts\"=?, " +
			"\"intKursSemesterMin\"=?, " +
			"\"intKursSemesterMax\"=?, " +
			"\"intKursCommitmentMode\"=?, " +
			"\"strCustom1\"=?, " +
			"\"strCustom2\"=?, " +
			"\"strCustom3\"=?, " +
			"\"strCustom1_en\"=?, " +
			"\"strCustom2_en\"=?, " +
			"\"strCustom3_en\"=?" +
			" where (" + this.getSQLWhereClause() + ");";
	}

	/**
	 * SQL-Befehl zum Hinzufügen des Objekts in die Datenbank (Tabelle 'tblBdKursXKurstyp.')
	 * @return SQL-String "insert into...".
	 **/
	private String toDBAddString(){
		return "insert into \"tblBdKursXKurstyp\" ( " +
			"\"lngSeminarID\", \"lngKurstypID\", \"lngKursID\", \"strBemerkung\", \"strBemerkung_en\", \"sngKursCreditPts\", \"intKursSemesterMin\", \"intKursSemesterMax\", \"intKursCommitmentMode\", \"strCustom1\", \"strCustom2\", \"strCustom3\", \"strCustom1_en\", \"strCustom2_en\", \"strCustom3_en\" ) VALUES ( ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
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
	private void init(long lngSeminarID, long lngKurstypID, long lngKursID) throws SQLException, NamingException{

		this.m_lSeminarID=lngSeminarID;

		this.m_lKurstypID=lngKurstypID;

		this.m_lKursID=lngKursID;

		
		PreparedStatement pstm = prepareStatement("select * from \"tblBdKursXKurstyp\" where (" + this.getSQLWhereClause() + ");");
		pokeWhere(1,pstm);

		ResultSet rst=pstm.executeQuery();
		if(rst.next()) this.initByRst(rst);
		rst.close();
		this.disconnect();
		rst=null;
	}	
	
	/**
	 * Lade die Objekteigenschaften aus einem ResultSet.
	 * param rst ResultSet mit allen Spalten der Tabelle 'tblBdKursXKurstyp'
	 * @throws SQLException 
	 **/
	public void initByRst(ResultSet rst) throws SQLException{
		this.m_lSeminarID=rst.getLong("lngSeminarID");
		this.m_lKurstypID=rst.getLong("lngKurstypID");
		this.m_lKursID=rst.getLong("lngKursID");
		this.m_sBemerkung=rst.getString("strBemerkung");
		this.m_sBemerkung_en=rst.getString("strBemerkung_en");
		this.m_sKursCreditPts=rst.getFloat("sngKursCreditPts");
		this.m_iKursSemesterMin=rst.getInt("intKursSemesterMin");
		this.m_iKursSemesterMax=rst.getInt("intKursSemesterMax");
		this.m_iKursCommitmentMode=rst.getInt("intKursCommitmentMode");
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
		pokeWhere(16,pstm);
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
	public KursXKurstyp(){}	
	
	/**
	 * Konstruktor mit Indexwerten (eindeutig) der assoziierten Datenbanktabelle
	 * @throws NamingException 
	 * @throws SQLException 
	 **/
	public KursXKurstyp(long lngSeminarID, long lngKurstypID, long lngKursID) throws SQLException, NamingException{
		this.init(lngSeminarID, lngKurstypID, lngKursID);
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
	public KursXKurstyp(ResultSet rst) throws SQLException{
		this.initByRst(rst);
		this.m_bIsDirty = false;
	}

  }//Klassenende
