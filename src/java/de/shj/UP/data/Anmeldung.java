
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
 *  Module: SignUp
Application of a student to a course (or, more precisely: to a KursXKurstyp).
The application is weighed with points of field intAnmeldungPrioritaet.
 *  
 *  ALLGEMEINE INFORMATION ZU DEN OBJEKTEN DIESES PACKAGES:
 *  Diese Klasse kapselt die Tabelle 'Anmeldung' in 
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
public class Anmeldung extends shjCore{

	private static final long serialVersionUID = 7L;

////////////////////////////////////////////////////////////////
// 1.   P R I V A T E   D E K L A R A T I O N E N
////////////////////////////////////////////////////////////////
	
	private boolean m_bIsDirty = false;

	private long m_lSdSeminarID;
	private String m_sMatrikelnummer;
	private long m_lKursID;
	private long m_lKurstypID;
	private int m_iAnmeldungPrioritaet;
	private long m_lAnmeldungRandom;
	private boolean m_bAnmeldungZuschlag;
	private boolean m_bAnmeldungFixiert;
	private Date m_dAnmeldungDatum;

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
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdAnmeldung.lngSdSeminarID</Code>
	 **/
	public long getSdSeminarID(){
		return this.m_lSdSeminarID;
	}

	/**
	 * Seminar
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdAnmeldung.lngSdSeminarID</Code>
	 **/	
	public void setSdSeminarID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lSdSeminarID));
		this.m_lSdSeminarID=value;
	}
	

	/**
	 * Registration number of student.
	 * @return Registration number of student.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdAnmeldung.strMatrikelnummer</Code>
	 **/
	public String getMatrikelnummer(){
		return this.m_sMatrikelnummer;
	}

	/**
	 * Registration number of student.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdAnmeldung.strMatrikelnummer</Code>
	 **/	
	public void setMatrikelnummer(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sMatrikelnummer)));
		this.m_sMatrikelnummer=value;
	}
	

	/**
	 * Course that this student applied to.
	 * @return Course that this student applied to.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdAnmeldung.lngKursID</Code>
	 **/
	public long getKursID(){
		return this.m_lKursID;
	}

	/**
	 * Course that this student applied to.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdAnmeldung.lngKursID</Code>
	 **/	
	public void setKursID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lKursID));
		this.m_lKursID=value;
	}
	

	/**
	 * Id of coursetype.
	 * @return Id of coursetype.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdAnmeldung.lngKurstypID</Code>
	 **/
	public long getKurstypID(){
		return this.m_lKurstypID;
	}

	/**
	 * Id of coursetype.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdAnmeldung.lngKurstypID</Code>
	 **/	
	public void setKurstypID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lKurstypID));
		this.m_lKurstypID=value;
	}
	

	/**
	 * Number of points to indicate how much the student wants or needs a place in this course(type).
	 * @return Number of points to indicate how much the student wants or needs a place in this course(type).
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdAnmeldung.intAnmeldungPrioritaet</Code>
	 **/
	public int getAnmeldungPrioritaet(){
		return this.m_iAnmeldungPrioritaet;
	}

	/**
	 * Number of points to indicate how much the student wants or needs a place in this course(type).
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdAnmeldung.intAnmeldungPrioritaet</Code>
	 **/	
	public void setAnmeldungPrioritaet(int value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_iAnmeldungPrioritaet));
		this.m_iAnmeldungPrioritaet=value;
	}
	

	/**
	 * Random number to create random sequence of students (so that the places are distributed equally).
	 * @return Random number to create random sequence of students (so that the places are distributed equally).
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdAnmeldung.lngAnmeldungRandom</Code>
	 **/
	public long getAnmeldungRandom(){
		return this.m_lAnmeldungRandom;
	}

	/**
	 * Random number to create random sequence of students (so that the places are distributed equally).
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdAnmeldung.lngAnmeldungRandom</Code>
	 **/	
	public void setAnmeldungRandom(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lAnmeldungRandom));
		this.m_lAnmeldungRandom=value;
	}
	

	/**
	 * if true, this student was given a place in this cours(xcoursetype).
	 * @return if true, this student was given a place in this cours(xcoursetype).
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdAnmeldung.blnAnmeldungZuschlag</Code>
	 **/
	public boolean getAnmeldungZuschlag(){
		return this.m_bAnmeldungZuschlag;
	}

	/**
	 * if true, this student was given a place in this cours(xcoursetype).
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdAnmeldung.blnAnmeldungZuschlag</Code>
	 **/	
	public void setAnmeldungZuschlag(boolean value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_bAnmeldungZuschlag));
		this.m_bAnmeldungZuschlag=value;
	}
	

	/**
	 * if true, this anmeldung is fixed (e.g. belongs to an earlier period, and will not be reconsidered).
	 * @return if true, this anmeldung is fixed (e.g. belongs to an earlier period, and will not be reconsidered).
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdAnmeldung.blnAnmeldungFixiert</Code>
	 **/
	public boolean getAnmeldungFixiert(){
		return this.m_bAnmeldungFixiert;
	}

	/**
	 * if true, this anmeldung is fixed (e.g. belongs to an earlier period, and will not be reconsidered).
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdAnmeldung.blnAnmeldungFixiert</Code>
	 **/	
	public void setAnmeldungFixiert(boolean value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_bAnmeldungFixiert));
		this.m_bAnmeldungFixiert=value;
	}
	

	/**
	 * Date: when did this student apply for this course?
	 * @return Date: when did this student apply for this course?
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdAnmeldung.dtmAnmeldungDatum</Code>
	 **/
	public Date getAnmeldungDatum(){
		return this.m_dAnmeldungDatum;
	}

	/**
	 * Date: when did this student apply for this course?
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdAnmeldung.dtmAnmeldungDatum</Code>
	 **/	
	public void setAnmeldungDatum(Date value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_dAnmeldungDatum)));
		this.m_dAnmeldungDatum=value;
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
			"<Matrikelnummer>" + m_sMatrikelnummer + "</Matrikelnummer>"  + 
			"<KursID>" + m_lKursID + "</KursID>"  + 
			"<KurstypID>" + m_lKurstypID + "</KurstypID>"  + 
			"<AnmeldungPrioritaet>" + m_iAnmeldungPrioritaet + "</AnmeldungPrioritaet>"  + 
			"<AnmeldungRandom>" + m_lAnmeldungRandom + "</AnmeldungRandom>"  + 
			"<AnmeldungZuschlag>" + m_bAnmeldungZuschlag + "</AnmeldungZuschlag>"  + 
			"<AnmeldungFixiert>" + m_bAnmeldungFixiert + "</AnmeldungFixiert>"  + 
			"<AnmeldungDatum>" + m_dAnmeldungDatum + "</AnmeldungDatum>" ;
	}

////////////////////////////////////////////////////////////////
// 4.   S Q L  U T I L I T I E S
////////////////////////////////////////////////////////////////


	/**
	 * Where-SQL für eindeutigen Index dieses Objekte in der Datenbank (in Tabelle 'tblBdAnmeldung.').
	 * @return SQL-String wie z.B. "Id1=? and Id2=?"
	 **/
	private String getSQLWhereClause(){
		return 
			"\"lngSdSeminarID\"=? AND " + 
			"\"strMatrikelnummer\"=? AND " + 
			"\"lngKursID\"=? AND " + 
			"\"lngKurstypID\"=?";
	}

	/**
	 * SQL-Befehl zum Löschen des Objekts in der Datenbank (aus Tabelle 'tblBdAnmeldung.')
	 * @return SQL-String zum Löschen des Objekts, z.B. "delete from T1 where (Id1=?);"
	 **/
	private String toDBDeleteString(){
		return "delete from \"tblBdAnmeldung\" where ( " + this.getSQLWhereClause() + ");";
	}

	/**
 	 * Setzt die Werte für alle Platzhalter in {@link #getSQLWhereClause()}.
	 * @param ii Offset für die Parameter (inklusive).
	 * @throws SQLException 
	 **/
	private void pokeWhere(int ii, PreparedStatement ps) throws SQLException{
		
		ps.setLong(ii++, m_lSdSeminarID);
		ps.setString(ii++, m_sMatrikelnummer);
		ps.setLong(ii++, m_lKursID);
		ps.setLong(ii++, m_lKurstypID);
	}

	/**
  	 * Füllt das SQL-Statement mit den aktuellen Werten des Objekts
	 * @throws SQLException
	 **/
	private void pokeStatement(PreparedStatement ps) throws SQLException{
		
		ps.setLong(1, m_lSdSeminarID);
		ps.setString(2, m_sMatrikelnummer);
		ps.setLong(3, m_lKursID);
		ps.setLong(4, m_lKurstypID);
		ps.setInt(5, m_iAnmeldungPrioritaet);
		ps.setLong(6, m_lAnmeldungRandom);
		ps.setBoolean(7, m_bAnmeldungZuschlag);
		ps.setBoolean(8, m_bAnmeldungFixiert);
		ps.setDate(9, m_dAnmeldungDatum);

	}

	/**
	 * SQL-Befehl zum Speichern der Objekteigenschaften in die Datenbank (Tabelle 'tblBdAnmeldung.')
	 * @return PreparedStatemtent, das nach Aufruf von {@link #pokeStatement(PreparedStatement)} und {@link #execute()} das Update ausführt.
	 **/
	private String toDBUpdateString(){
		return "update \"tblBdAnmeldung\" set " +
			"\"lngSdSeminarID\"=?, " +
			"\"strMatrikelnummer\"=?, " +
			"\"lngKursID\"=?, " +
			"\"lngKurstypID\"=?, " +
			"\"intAnmeldungPrioritaet\"=?, " +
			"\"lngAnmeldungRandom\"=?, " +
			"\"blnAnmeldungZuschlag\"=?, " +
			"\"blnAnmeldungFixiert\"=?, " +
			"\"dtmAnmeldungDatum\"=?" +
			" where (" + this.getSQLWhereClause() + ");";
	}

	/**
	 * SQL-Befehl zum Hinzufügen des Objekts in die Datenbank (Tabelle 'tblBdAnmeldung.')
	 * @return SQL-String "insert into...".
	 **/
	private String toDBAddString(){
		return "insert into \"tblBdAnmeldung\" ( " +
			"\"lngSdSeminarID\", \"strMatrikelnummer\", \"lngKursID\", \"lngKurstypID\", \"intAnmeldungPrioritaet\", \"lngAnmeldungRandom\", \"blnAnmeldungZuschlag\", \"blnAnmeldungFixiert\", \"dtmAnmeldungDatum\" ) VALUES ( ?,?,?,?,?,?,?,?,?);";
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
	private void init(long lngSdSeminarID, String strMatrikelnummer, long lngKursID, long lngKurstypID) throws SQLException, NamingException{

		this.m_lSdSeminarID=lngSdSeminarID;

		this.m_sMatrikelnummer=strMatrikelnummer;

		this.m_lKursID=lngKursID;

		this.m_lKurstypID=lngKurstypID;

		
		PreparedStatement pstm = prepareStatement("select * from \"tblBdAnmeldung\" where (" + this.getSQLWhereClause() + ");");
		pokeWhere(1,pstm);

		ResultSet rst=pstm.executeQuery();
		if(rst.next()) this.initByRst(rst);
		rst.close();
		this.disconnect();
		rst=null;
	}	
	
	/**
	 * Lade die Objekteigenschaften aus einem ResultSet.
	 * param rst ResultSet mit allen Spalten der Tabelle 'tblBdAnmeldung'
	 * @throws SQLException 
	 **/
	protected void initByRst(ResultSet rst) throws SQLException{
		this.m_lSdSeminarID=rst.getLong("lngSdSeminarID");
		this.m_sMatrikelnummer=rst.getString("strMatrikelnummer");
		this.m_lKursID=rst.getLong("lngKursID");
		this.m_lKurstypID=rst.getLong("lngKurstypID");
		this.m_iAnmeldungPrioritaet=rst.getInt("intAnmeldungPrioritaet");
		this.m_lAnmeldungRandom=rst.getLong("lngAnmeldungRandom");
		this.m_bAnmeldungZuschlag=rst.getBoolean("blnAnmeldungZuschlag");
		this.m_bAnmeldungFixiert=rst.getBoolean("blnAnmeldungFixiert");
		this.m_dAnmeldungDatum=rst.getDate("dtmAnmeldungDatum");	
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
	public Anmeldung(){}	
	
	/**
	 * Konstruktor mit Indexwerten (eindeutig) der assoziierten Datenbanktabelle
	 * @throws NamingException 
	 * @throws SQLException 
	 **/
	public Anmeldung(long lngSdSeminarID, String strMatrikelnummer, long lngKursID, long lngKurstypID) throws SQLException, NamingException{
		this.init(lngSdSeminarID, strMatrikelnummer, lngKursID, lngKurstypID);
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
	public Anmeldung(ResultSet rst) throws SQLException{
		this.initByRst(rst);
		this.m_bIsDirty = false;
	}

  }//Klassenende
