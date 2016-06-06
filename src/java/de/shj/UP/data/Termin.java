
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
 *  Module: Appointments
Simple timer-functionality.
 *  
 *  ALLGEMEINE INFORMATION ZU DEN OBJEKTEN DIESES PACKAGES:
 *  Diese Klasse kapselt die Tabelle 'Termin' in 
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
public class Termin extends shjCore{

	private static final long serialVersionUID = 7L;

////////////////////////////////////////////////////////////////
// 1.   P R I V A T E   D E K L A R A T I O N E N
////////////////////////////////////////////////////////////////
	
	private boolean m_bIsDirty = false;

	private long m_lSeminarID;
	private long m_lTerminID;
	private Date m_dTerminDatum;
	private String m_tTerminZeit;
	private String m_sTerminOrt;
	private String m_sTerminBeschreibung;
	private String m_sTerminAP;
	private String m_sTerminAPEmail;
	private String m_sTerminVerteiler;

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
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdTermin.lngSeminarID</Code>
	 **/
	public long getSeminarID(){
		return this.m_lSeminarID;
	}

	/**
	 * Seminar
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdTermin.lngSeminarID</Code>
	 **/	
	public void setSeminarID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lSeminarID));
		this.m_lSeminarID=value;
	}
	

	/**
	 * Id of appointment.
	 * @return Id of appointment.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdTermin.lngTerminID</Code>
	 **/
	public long getTerminID(){
		return this.m_lTerminID;
	}

	/**
	 * Id of appointment.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdTermin.lngTerminID</Code>
	 **/	
	public void setTerminID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lTerminID));
		this.m_lTerminID=value;
	}
	

	/**
	 * Date for appointment/meeting.
	 * @return Date for appointment/meeting.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdTermin.dtmTerminDatum</Code>
	 **/
	public Date getTerminDatum(){
		return this.m_dTerminDatum;
	}

	/**
	 * Date for appointment/meeting.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdTermin.dtmTerminDatum</Code>
	 **/	
	public void setTerminDatum(Date value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_dTerminDatum));
		this.m_dTerminDatum=value;
	}
	

	/**
	 * Time for appointment/meeting
	 * @return Time for appointment/meeting
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdTermin.dtmTerminZeit</Code>
	 **/
	public String getTerminZeit(){
		return this.m_tTerminZeit;
	}

	/**
	 * Time for appointment/meeting
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdTermin.dtmTerminZeit</Code>
	 **/	
	public void setTerminZeit(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_tTerminZeit));
		this.m_tTerminZeit=value;
	}
	

	/**
	 * Place for appointment/meeting
	 * @return Place for appointment/meeting
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdTermin.strTerminOrt</Code>
	 **/
	public String getTerminOrt(){
		return this.m_sTerminOrt;
	}

	/**
	 * Place for appointment/meeting
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdTermin.strTerminOrt</Code>
	 **/	
	public void setTerminOrt(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sTerminOrt)));
		this.m_sTerminOrt=value;
	}
	

	/**
	 * Description of  appointment/meeting
	 * @return Description of  appointment/meeting
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdTermin.strTerminBeschreibung</Code>
	 **/
	public String getTerminBeschreibung(){
		return this.m_sTerminBeschreibung;
	}

	/**
	 * Description of  appointment/meeting
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdTermin.strTerminBeschreibung</Code>
	 **/	
	public void setTerminBeschreibung(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sTerminBeschreibung)));
		this.m_sTerminBeschreibung=value;
	}
	

	/**
	 * Person to contact concerning this  appointment/meeting
	 * @return Person to contact concerning this  appointment/meeting
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdTermin.strTerminAP</Code>
	 **/
	public String getTerminAP(){
		return this.m_sTerminAP;
	}

	/**
	 * Person to contact concerning this  appointment/meeting
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdTermin.strTerminAP</Code>
	 **/	
	public void setTerminAP(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sTerminAP)));
		this.m_sTerminAP=value;
	}
	

	/**
	 * Email of person responsible for appointment/meeting
	 * @return Email of person responsible for appointment/meeting
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdTermin.strTerminAPEmail</Code>
	 **/
	public String getTerminAPEmail(){
		return this.m_sTerminAPEmail;
	}

	/**
	 * Email of person responsible for appointment/meeting
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdTermin.strTerminAPEmail</Code>
	 **/	
	public void setTerminAPEmail(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sTerminAPEmail)));
		this.m_sTerminAPEmail=value;
	}
	

	/**
	 * Who to inform about this appointment/meeting
	 * @return Who to inform about this appointment/meeting
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdTermin.strTerminVerteiler</Code>
	 **/
	public String getTerminVerteiler(){
		return this.m_sTerminVerteiler;
	}

	/**
	 * Who to inform about this appointment/meeting
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdTermin.strTerminVerteiler</Code>
	 **/	
	public void setTerminVerteiler(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sTerminVerteiler)));
		this.m_sTerminVerteiler=value;
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
			"<TerminID>" + m_lTerminID + "</TerminID>"  + 
			"<TerminDatum>" + m_dTerminDatum + "</TerminDatum>"  + 
			"<TerminZeit>" + m_tTerminZeit + "</TerminZeit>"  + 
			"<TerminOrt>" + m_sTerminOrt + "</TerminOrt>"  + 
			"<TerminBeschreibung>" + m_sTerminBeschreibung + "</TerminBeschreibung>"  + 
			"<TerminAP>" + m_sTerminAP + "</TerminAP>"  + 
			"<TerminAPEmail>" + m_sTerminAPEmail + "</TerminAPEmail>"  + 
			"<TerminVerteiler>" + m_sTerminVerteiler + "</TerminVerteiler>" ;
	}

////////////////////////////////////////////////////////////////
// 4.   S Q L  U T I L I T I E S
////////////////////////////////////////////////////////////////


	/**
	 * Where-SQL für eindeutigen Index dieses Objekte in der Datenbank (in Tabelle 'tblBdTermin.').
	 * @return SQL-String wie z.B. "Id1=? and Id2=?"
	 **/
	private String getSQLWhereClause(){
		return 
			"\"lngSeminarID\"=? AND " + 
			"\"lngTerminID\"=?";
	}

	/**
	 * SQL-Befehl zum Löschen des Objekts in der Datenbank (aus Tabelle 'tblBdTermin.')
	 * @return SQL-String zum Löschen des Objekts, z.B. "delete from T1 where (Id1=?);"
	 **/
	private String toDBDeleteString(){
		return "delete from \"tblBdTermin\" where ( " + this.getSQLWhereClause() + ");";
	}

	/**
 	 * Setzt die Werte für alle Platzhalter in {@link #getSQLWhereClause()}.
	 * @param ii Offset für die Parameter (inklusive).
	 * @throws SQLException 
	 **/
	private void pokeWhere(int ii, PreparedStatement ps) throws SQLException{
		
		ps.setLong(ii++, m_lSeminarID);
		ps.setLong(ii++, m_lTerminID);
	}

	/**
  	 * Füllt das SQL-Statement mit den aktuellen Werten des Objekts
	 * @throws SQLException
	 **/
	private void pokeStatement(PreparedStatement ps) throws SQLException{
		
		ps.setLong(1, m_lSeminarID);
		ps.setLong(2, m_lTerminID);
		ps.setDate(3, m_dTerminDatum);
		ps.setString(4, m_tTerminZeit);
		ps.setString(5, m_sTerminOrt);
		ps.setString(6, m_sTerminBeschreibung);
		ps.setString(7, m_sTerminAP);
		ps.setString(8, m_sTerminAPEmail);
		ps.setString(9, m_sTerminVerteiler);

	}

	/**
	 * SQL-Befehl zum Speichern der Objekteigenschaften in die Datenbank (Tabelle 'tblBdTermin.')
	 * @return PreparedStatemtent, das nach Aufruf von {@link #pokeStatement(PreparedStatement)} und {@link #execute()} das Update ausführt.
	 **/
	private String toDBUpdateString(){
		return "update \"tblBdTermin\" set " +
			"\"lngSeminarID\"=?, " +
			"\"lngTerminID\"=?, " +
			"\"dtmTerminDatum\"=?, " +
			"\"dtmTerminZeit\"=?, " +
			"\"strTerminOrt\"=?, " +
			"\"strTerminBeschreibung\"=?, " +
			"\"strTerminAP\"=?, " +
			"\"strTerminAPEmail\"=?, " +
			"\"strTerminVerteiler\"=?" +
			" where (" + this.getSQLWhereClause() + ");";
	}

	/**
	 * SQL-Befehl zum Hinzufügen des Objekts in die Datenbank (Tabelle 'tblBdTermin.')
	 * @return SQL-String "insert into...".
	 **/
	private String toDBAddString(){
		return "insert into \"tblBdTermin\" ( " +
			"\"lngSeminarID\", \"lngTerminID\", \"dtmTerminDatum\", \"dtmTerminZeit\", \"strTerminOrt\", \"strTerminBeschreibung\", \"strTerminAP\", \"strTerminAPEmail\", \"strTerminVerteiler\" ) VALUES ( ?,?,?,?,?,?,?,?,?);";
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
	private void init(long lngSeminarID, long lngTerminID) throws SQLException, NamingException{

		this.m_lSeminarID=lngSeminarID;

		this.m_lTerminID=lngTerminID;

		
		PreparedStatement pstm = prepareStatement("select * from \"tblBdTermin\" where (" + this.getSQLWhereClause() + ");");
		pokeWhere(1,pstm);

		ResultSet rst=pstm.executeQuery();
		if(rst.next()) this.initByRst(rst);
		rst.close();
		this.disconnect();
		rst=null;
	}	
	
	/**
	 * Lade die Objekteigenschaften aus einem ResultSet.
	 * param rst ResultSet mit allen Spalten der Tabelle 'tblBdTermin'
	 * @throws SQLException 
	 **/
	protected void initByRst(ResultSet rst) throws SQLException{
		this.m_lSeminarID=rst.getLong("lngSeminarID");
		this.m_lTerminID=rst.getLong("lngTerminID");
		this.m_dTerminDatum=rst.getDate("dtmTerminDatum");
		this.m_tTerminZeit=rst.getString("dtmTerminZeit");
		this.m_sTerminOrt=rst.getString("strTerminOrt");
		this.m_sTerminBeschreibung=rst.getString("strTerminBeschreibung");
		this.m_sTerminAP=rst.getString("strTerminAP");
		this.m_sTerminAPEmail=rst.getString("strTerminAPEmail");
		this.m_sTerminVerteiler=rst.getString("strTerminVerteiler");	
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
	public Termin(){}	
	
	/**
	 * Konstruktor mit Indexwerten (eindeutig) der assoziierten Datenbanktabelle
	 * @throws NamingException 
	 * @throws SQLException 
	 **/
	public Termin(long lngSeminarID, long lngTerminID) throws SQLException, NamingException{
		this.init(lngSeminarID, lngTerminID);
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
	public Termin(ResultSet rst) throws SQLException{
		this.initByRst(rst);
		this.m_bIsDirty = false;
	}
  }//Klassenende
