
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
import java.util.Locale;

import org.w3c.dom.Node; 

import de.shj.UP.HTML.HtmlDate;

/**
 *  Module: SignUp
Contains information on when to start and stop application period for SignUp. Also contains flags to disable server functionality.
 *  
 *  ALLGEMEINE INFORMATION ZU DEN OBJEKTEN DIESES PACKAGES:
 *  Diese Klasse kapselt die Tabelle 'Data' in 
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
public class Data extends shjCore{

	private static final long serialVersionUID = 7L;

////////////////////////////////////////////////////////////////
// 1.   P R I V A T E   D E K L A R A T I O N E N
////////////////////////////////////////////////////////////////
	
	private boolean m_bIsDirty = false;

	private long m_lSdSeminarID;
	private String m_sSemesterbezeichnung;
	private Date m_dSignUpStart;
	private Date m_dSignUpStop;
	private boolean m_bHttpserverAktiv;
	private boolean m_bHttpShowResults;
	private int m_iAllowRegistration;

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
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdData.lngSdSeminarID</Code>
	 **/
	public long getSdSeminarID(){
		return this.m_lSdSeminarID;
	}

	/**
	 * Seminar
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdData.lngSdSeminarID</Code>
	 **/	
	public void setSdSeminarID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lSdSeminarID));
		this.m_lSdSeminarID=value;
	}
	

	/**
	 * Name of semester.
@Deprecated, use utility in shjCore instead.
	 * @return Name of semester.
@Deprecated, use utility in shjCore instead.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdData.strSemesterbezeichnung</Code>
	 **/
	public String getSemesterbezeichnung(){
		return this.m_sSemesterbezeichnung;
	}

	/**
	 * Name of semester.
@Deprecated, use utility in shjCore instead.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdData.strSemesterbezeichnung</Code>
	 **/	
	public void setSemesterbezeichnung(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sSemesterbezeichnung)));
		this.m_sSemesterbezeichnung=value;
	}
	

	/**
	 * Date (inclusive) from when to enable application for courses.
	 * @return Date (inclusive) from when to enable application for courses.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdData.dtmSignUpStart</Code>
	 **/
	public Date getSignUpStart(){
		return this.m_dSignUpStart;
	}

	/**
	 * Date (inclusive) from when to enable application for courses.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdData.dtmSignUpStart</Code>
	 **/	
	public void setSignUpStart(Date value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_dSignUpStart)));
		this.m_dSignUpStart=value;
	}
	

	/**
	 * Date (inclusive) when to stop  application for courses.
	 * @return Date (inclusive) when to stop  application for courses.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdData.dtmSignUpStop</Code>
	 **/
	public Date getSignUpStop(){
		return this.m_dSignUpStop;
	}

	/**
	 * Date (inclusive) when to stop  application for courses.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdData.dtmSignUpStop</Code>
	 **/	
	public void setSignUpStop(Date value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_dSignUpStop)));
		this.m_dSignUpStop=value;
	}
	

	/**
	 * @Deprecated. Used to disable all methods except for newsticker etc. while calculations for SignUp were made (reason: when things go wrong, no wrong results will be displayed).
	 * @return @Deprecated. Used to disable all methods except for newsticker etc. while calculations for SignUp were made (reason: when things go wrong, no wrong results will be displayed).
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdData.blnHttpserverAktiv</Code>
	 **/
	public boolean getHttpserverAktiv(){
		return this.m_bHttpserverAktiv;
	}

	/**
	 * @Deprecated. Used to disable all methods except for newsticker etc. while calculations for SignUp were made (reason: when things go wrong, no wrong results will be displayed).
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdData.blnHttpserverAktiv</Code>
	 **/	
	public void setHttpserverAktiv(boolean value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_bHttpserverAktiv));
		this.m_bHttpserverAktiv=value;
	}
	

	/**
	 * Show results of student/course distribution online.
	 * @return Show results of student/course distribution online.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdData.blnHttpShowResults</Code>
	 **/
	public boolean getHttpShowResults(){
		return this.m_bHttpShowResults;
	}

	/**
	 * Show results of student/course distribution online.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdData.blnHttpShowResults</Code>
	 **/	
	public void setHttpShowResults(boolean value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_bHttpShowResults));
		this.m_bHttpShowResults=value;
	}
	

	/**
	 * Is it allowed for students to register themselves online? (This used to be SignUp).
	 * @return Is it allowed for students to register themselves online? (This used to be SignUp).
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdData.blnAllowRegistration</Code>
	 **/
	public int getAllowRegistration(){
		return this.m_iAllowRegistration;
	}

	/**
	 * Is it allowed for students to register themselves online? (This used to be SignUp).
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdData.blnAllowRegistration</Code>
	 **/	
	public void setAllowRegistration(int value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_iAllowRegistration));
		this.m_iAllowRegistration=value;
	}
	
 	/**
 	 * Sets current semester
 	 * @param lngSdSeminarID
 	 * @throws Exception
 	 */
 	public Data(long lngSdSeminarID) throws Exception{
 		this.m_sSemesterbezeichnung = new HtmlDate(Locale.GERMANY).getSemesterName(); 
 		this.init(lngSdSeminarID,m_sSemesterbezeichnung);
 		this.m_bIsDirty = false;
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
			"<Semesterbezeichnung>" + m_sSemesterbezeichnung + "</Semesterbezeichnung>"  + 
			"<SignUpStart>" + m_dSignUpStart + "</SignUpStart>"  + 
			"<SignUpStop>" + m_dSignUpStop + "</SignUpStop>"  + 
			"<HttpserverAktiv>" + m_bHttpserverAktiv + "</HttpserverAktiv>"  + 
			"<HttpShowResults>" + m_bHttpShowResults + "</HttpShowResults>"  + 
			"<AllowRegistration>" + m_iAllowRegistration + "</AllowRegistration>" ;
	}

////////////////////////////////////////////////////////////////
// 4.   S Q L  U T I L I T I E S
////////////////////////////////////////////////////////////////


	/**
	 * Where-SQL für eindeutigen Index dieses Objekte in der Datenbank (in Tabelle 'tblBdData.').
	 * @return SQL-String wie z.B. "Id1=? and Id2=?"
	 **/
	private String getSQLWhereClause(){
		return 
			"\"lngSdSeminarID\"=? AND " + 
			"\"strSemesterbezeichnung\"=?";
	}

	/**
	 * SQL-Befehl zum Löschen des Objekts in der Datenbank (aus Tabelle 'tblBdData.')
	 * @return SQL-String zum Löschen des Objekts, z.B. "delete from T1 where (Id1=?);"
	 **/
	private String toDBDeleteString(){
		return "delete from \"tblBdData\" where ( " + this.getSQLWhereClause() + ");";
	}

	/**
 	 * Setzt die Werte für alle Platzhalter in {@link #getSQLWhereClause()}.
	 * @param ii Offset für die Parameter (inklusive).
	 * @throws SQLException 
	 **/
	private void pokeWhere(int ii, PreparedStatement ps) throws SQLException{
		
		ps.setLong(ii++, m_lSdSeminarID);
		ps.setString(ii++, m_sSemesterbezeichnung);
	}

	/**
  	 * Füllt das SQL-Statement mit den aktuellen Werten des Objekts
	 * @throws SQLException
	 **/
	private void pokeStatement(PreparedStatement ps) throws SQLException{
		
		ps.setLong(1, m_lSdSeminarID);
		ps.setString(2, m_sSemesterbezeichnung);
		ps.setDate(3, m_dSignUpStart);
		ps.setDate(4, m_dSignUpStop);
		ps.setBoolean(5, m_bHttpserverAktiv);
		ps.setBoolean(6, m_bHttpShowResults);
		ps.setInt(7, m_iAllowRegistration);

	}

	/**
	 * SQL-Befehl zum Speichern der Objekteigenschaften in die Datenbank (Tabelle 'tblBdData.')
	 * @return PreparedStatemtent, das nach Aufruf von {@link #pokeStatement(PreparedStatement)} und {@link #execute()} das Update ausführt.
	 **/
	private String toDBUpdateString(){
		return "update \"tblBdData\" set " +
			"\"lngSdSeminarID\"=?, " +
			"\"strSemesterbezeichnung\"=?, " +
			"\"dtmSignUpStart\"=?, " +
			"\"dtmSignUpStop\"=?, " +
			"\"blnHttpserverAktiv\"=?, " +
			"\"blnHttpShowResults\"=?, " +
			"\"blnAllowRegistration\"=?" +
			" where (" + this.getSQLWhereClause() + ");";
	}

	/**
	 * SQL-Befehl zum Hinzufügen des Objekts in die Datenbank (Tabelle 'tblBdData.')
	 * @return SQL-String "insert into...".
	 **/
	private String toDBAddString(){
		return "insert into \"tblBdData\" ( " +
			"\"lngSdSeminarID\", \"strSemesterbezeichnung\", \"dtmSignUpStart\", \"dtmSignUpStop\", \"blnHttpserverAktiv\", \"blnHttpShowResults\", \"blnAllowRegistration\" ) VALUES ( ?,?,?,?,?,?,?);";
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
	private void init(long lngSdSeminarID, String strSemesterbezeichnung) throws SQLException, NamingException{

		this.m_lSdSeminarID=lngSdSeminarID;

		this.m_sSemesterbezeichnung=strSemesterbezeichnung;

		
		PreparedStatement pstm = prepareStatement("select * from \"tblBdData\" where (" + this.getSQLWhereClause() + ");");
		pokeWhere(1,pstm);

		ResultSet rst=pstm.executeQuery();
		if(rst.next()) this.initByRst(rst);
		rst.close();
		this.disconnect();
		rst=null;
	}	
	
	/**
	 * Lade die Objekteigenschaften aus einem ResultSet.
	 * param rst ResultSet mit allen Spalten der Tabelle 'tblBdData'
	 * @throws SQLException 
	 **/
	protected void initByRst(ResultSet rst) throws SQLException{
		this.m_lSdSeminarID=rst.getLong("lngSdSeminarID");
		this.m_sSemesterbezeichnung=rst.getString("strSemesterbezeichnung");
		this.m_dSignUpStart=rst.getDate("dtmSignUpStart");
		this.m_dSignUpStop=rst.getDate("dtmSignUpStop");
		this.m_bHttpserverAktiv=rst.getBoolean("blnHttpserverAktiv");
		this.m_bHttpShowResults=rst.getBoolean("blnHttpShowResults");
		this.m_iAllowRegistration=rst.getInt("blnAllowRegistration");	
	}	
	
	/**
	 * Lade die Objekteigenschaften aus einer XML-Node.
	 * param node XML-Node mit allen Eigenschaften als Tags.
	 * @throws ParseException (Datum muss im ISO-Format yyyy-MM-dd übergeben werden).
	 **/
	private void initByNode(Node node) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		this.m_lSdSeminarID=Long.parseLong(shjNodeValue(node, "SdSeminarID"));
		this.m_sSemesterbezeichnung=(shjNodeValue(node, "Semesterbezeichnung"));
		this.m_dSignUpStart=(Date) (sdf.parse(shjNodeValue(node, "SignUpStart")));
		this.m_dSignUpStop=(Date) (sdf.parse(shjNodeValue(node, "SignUpStop")));
		this.m_bHttpserverAktiv=Boolean.valueOf(shjNodeValue(node, "HttpserverAktiv")).booleanValue();
		this.m_bHttpShowResults=Boolean.valueOf(shjNodeValue(node, "HttpShowResults")).booleanValue();
		this.m_iAllowRegistration=Integer.parseInt(shjNodeValue(node, "AllowRegistration"));
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
	public Data(){}	
	
	/**
	 * Konstruktor mit Indexwerten (eindeutig) der assoziierten Datenbanktabelle
	 * @throws NamingException 
	 * @throws SQLException 
	 **/
	public Data(long lngSdSeminarID, String strSemesterbezeichnung) throws SQLException, NamingException{
		this.init(lngSdSeminarID, strSemesterbezeichnung);
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
	public Data(ResultSet rst) throws SQLException{
		this.initByRst(rst);
		this.m_bIsDirty = false;
	}

	/**
	 * Konstruktor per XML-Darstellung des Objekts.
	 * @throws ParseException, if a date can't be read.
	 **/
	public Data(Node node) throws ParseException{
		this.initByNode(node);
		this.m_bIsDirty = false;
	}

  }//Klassenende
