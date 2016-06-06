
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
 *  Module: Kvv
Link-list and/or list of downloads for this course.
 *  
 *  ALLGEMEINE INFORMATION ZU DEN OBJEKTEN DIESES PACKAGES:
 *  Diese Klasse kapselt die Tabelle 'KursXLink' in 
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
public class KursXLink extends shjCore{

	private static final long serialVersionUID = 7L;

////////////////////////////////////////////////////////////////
// 1.   P R I V A T E   D E K L A R A T I O N E N
////////////////////////////////////////////////////////////////
	
	private boolean m_bIsDirty = false;

	private long m_lSdSeminarID;
	private long m_lKursID;
	private long m_lLinkID;
	private String m_sKursLinkURL;
	private String m_sKursLinkBezeichnung;
	private String m_sKursLinkBeschreibung;
	private boolean m_bKursLinkDownload;
	private boolean m_bKursLinkVisible;
	private String m_sKursLinkCustom1;
	private String m_sKursLinkCustom2;
	private String m_sKursLinkCustom3;

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
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKursXLink.lngSdSeminarID</Code>
	 **/
	public long getSdSeminarID(){
		return this.m_lSdSeminarID;
	}

	/**
	 * Seminar
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKursXLink.lngSdSeminarID</Code>
	 **/	
	public void setSdSeminarID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lSdSeminarID));
		this.m_lSdSeminarID=value;
	}
	

	/**
	 * Id of course.
	 * @return Id of course.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKursXLink.lngKursID</Code>
	 **/
	public long getKursID(){
		return this.m_lKursID;
	}

	/**
	 * Id of course.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKursXLink.lngKursID</Code>
	 **/	
	public void setKursID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lKursID));
		this.m_lKursID=value;
	}
	

	/**
	 * Id of link.
	 * @return Id of link.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKursXLink.lngLinkID</Code>
	 **/
	public long getLinkID(){
		return this.m_lLinkID;
	}

	/**
	 * Id of link.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKursXLink.lngLinkID</Code>
	 **/	
	public void setLinkID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lLinkID));
		this.m_lLinkID=value;
	}
	

	/**
	 * URL of this link. (Can be anything).
	 * @return URL of this link. (Can be anything).
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKursXLink.strKursLinkURL</Code>
	 **/
	public String getKursLinkURL(){
		return this.m_sKursLinkURL;
	}

	/**
	 * URL of this link. (Can be anything).
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKursXLink.strKursLinkURL</Code>
	 **/	
	public void setKursLinkURL(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKursLinkURL)));
		this.m_sKursLinkURL=value;
	}
	

	/**
	 * Name of this link (displayed in a-tag).
	 * @return Name of this link (displayed in a-tag).
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKursXLink.strKursLinkBezeichnung</Code>
	 **/
	public String getKursLinkBezeichnung(){
		return this.m_sKursLinkBezeichnung;
	}

	/**
	 * Name of this link (displayed in a-tag).
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKursXLink.strKursLinkBezeichnung</Code>
	 **/	
	public void setKursLinkBezeichnung(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKursLinkBezeichnung)));
		this.m_sKursLinkBezeichnung=value;
	}
	

	/**
	 * Description of this link.
	 * @return Description of this link.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKursXLink.strKursLinkBeschreibung</Code>
	 **/
	public String getKursLinkBeschreibung(){
		return this.m_sKursLinkBeschreibung;
	}

	/**
	 * Description of this link.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKursXLink.strKursLinkBeschreibung</Code>
	 **/	
	public void setKursLinkBeschreibung(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKursLinkBeschreibung)));
		this.m_sKursLinkBeschreibung=value;
	}
	

	/**
	 * Is this a download-link?
	 * @return Is this a download-link?
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKursXLink.blnKursLinkDownload</Code>
	 **/
	public boolean getKursLinkDownload(){
		return this.m_bKursLinkDownload;
	}

	/**
	 * Is this a download-link?
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKursXLink.blnKursLinkDownload</Code>
	 **/	
	public void setKursLinkDownload(boolean value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_bKursLinkDownload));
		this.m_bKursLinkDownload=value;
	}
	

	/**
	 * Is this link visible for students?
	 * @return Is this link visible for students?
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKursXLink.blnKursLinkVisible</Code>
	 **/
	public boolean getKursLinkVisible(){
		return this.m_bKursLinkVisible;
	}

	/**
	 * Is this link visible for students?
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKursXLink.blnKursLinkVisible</Code>
	 **/	
	public void setKursLinkVisible(boolean value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_bKursLinkVisible));
		this.m_bKursLinkVisible=value;
	}
	

	/**
	 * Custom 1
	 * @return Custom 1
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKursXLink.strKursLinkCustom1</Code>
	 **/
	public String getKursLinkCustom1(){
		return this.m_sKursLinkCustom1;
	}

	/**
	 * Custom 1
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKursXLink.strKursLinkCustom1</Code>
	 **/	
	public void setKursLinkCustom1(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKursLinkCustom1)));
		this.m_sKursLinkCustom1=value;
	}
	

	/**
	 * Custom 2
	 * @return Custom 2
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKursXLink.strKursLinkCustom2</Code>
	 **/
	public String getKursLinkCustom2(){
		return this.m_sKursLinkCustom2;
	}

	/**
	 * Custom 2
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKursXLink.strKursLinkCustom2</Code>
	 **/	
	public void setKursLinkCustom2(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKursLinkCustom2)));
		this.m_sKursLinkCustom2=value;
	}
	

	/**
	 * Custom 3
	 * @return Custom 3
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKursXLink.strKursLinkCustom3</Code>
	 **/
	public String getKursLinkCustom3(){
		return this.m_sKursLinkCustom3;
	}

	/**
	 * Custom 3
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdKursXLink.strKursLinkCustom3</Code>
	 **/	
	public void setKursLinkCustom3(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sKursLinkCustom3)));
		this.m_sKursLinkCustom3=value;
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
			"<KursID>" + m_lKursID + "</KursID>"  + 
			"<LinkID>" + m_lLinkID + "</LinkID>"  + 
			"<KursLinkURL>" + m_sKursLinkURL + "</KursLinkURL>"  + 
			"<KursLinkBezeichnung>" + m_sKursLinkBezeichnung + "</KursLinkBezeichnung>"  + 
			"<KursLinkBeschreibung>" + m_sKursLinkBeschreibung + "</KursLinkBeschreibung>"  + 
			"<KursLinkDownload>" + m_bKursLinkDownload + "</KursLinkDownload>"  + 
			"<KursLinkVisible>" + m_bKursLinkVisible + "</KursLinkVisible>"  + 
			"<KursLinkCustom1>" + m_sKursLinkCustom1 + "</KursLinkCustom1>"  + 
			"<KursLinkCustom2>" + m_sKursLinkCustom2 + "</KursLinkCustom2>"  + 
			"<KursLinkCustom3>" + m_sKursLinkCustom3 + "</KursLinkCustom3>" ;
	}

////////////////////////////////////////////////////////////////
// 4.   S Q L  U T I L I T I E S
////////////////////////////////////////////////////////////////


	/**
	 * Where-SQL für eindeutigen Index dieses Objekte in der Datenbank (in Tabelle 'tblBdKursXLink.').
	 * @return SQL-String wie z.B. "Id1=? and Id2=?"
	 **/
	private String getSQLWhereClause(){
		return 
			"\"lngSdSeminarID\"=? AND " + 
			"\"lngKursID\"=? AND " + 
			"\"lngLinkID\"=?";
	}

	/**
	 * SQL-Befehl zum Löschen des Objekts in der Datenbank (aus Tabelle 'tblBdKursXLink.')
	 * @return SQL-String zum Löschen des Objekts, z.B. "delete from T1 where (Id1=?);"
	 **/
	private String toDBDeleteString(){
		return "delete from \"tblBdKursXLink\" where ( " + this.getSQLWhereClause() + ");";
	}

	/**
 	 * Setzt die Werte für alle Platzhalter in {@link #getSQLWhereClause()}.
	 * @param ii Offset für die Parameter (inklusive).
	 * @throws SQLException 
	 **/
	private void pokeWhere(int ii, PreparedStatement ps) throws SQLException{
		
		ps.setLong(ii++, m_lSdSeminarID);
		ps.setLong(ii++, m_lKursID);
		ps.setLong(ii++, m_lLinkID);
	}

	/**
  	 * Füllt das SQL-Statement mit den aktuellen Werten des Objekts
	 * @throws SQLException
	 **/
	private void pokeStatement(PreparedStatement ps) throws SQLException{
		
		ps.setLong(1, m_lSdSeminarID);
		ps.setLong(2, m_lKursID);
		ps.setLong(3, m_lLinkID);
		ps.setString(4, m_sKursLinkURL);
		ps.setString(5, m_sKursLinkBezeichnung);
		ps.setString(6, m_sKursLinkBeschreibung);
		ps.setBoolean(7, m_bKursLinkDownload);
		ps.setBoolean(8, m_bKursLinkVisible);
		ps.setString(9, m_sKursLinkCustom1);
		ps.setString(10, m_sKursLinkCustom2);
		ps.setString(11, m_sKursLinkCustom3);

	}

	/**
	 * SQL-Befehl zum Speichern der Objekteigenschaften in die Datenbank (Tabelle 'tblBdKursXLink.')
	 * @return PreparedStatemtent, das nach Aufruf von {@link #pokeStatement(PreparedStatement)} und {@link #execute()} das Update ausführt.
	 **/
	private String toDBUpdateString(){
		return "update \"tblBdKursXLink\" set " +
			"\"lngSdSeminarID\"=?, " +
			"\"lngKursID\"=?, " +
			"\"lngLinkID\"=?, " +
			"\"strKursLinkURL\"=?, " +
			"\"strKursLinkBezeichnung\"=?, " +
			"\"strKursLinkBeschreibung\"=?, " +
			"\"blnKursLinkDownload\"=?, " +
			"\"blnKursLinkVisible\"=?, " +
			"\"strKursLinkCustom1\"=?, " +
			"\"strKursLinkCustom2\"=?, " +
			"\"strKursLinkCustom3\"=?" +
			" where (" + this.getSQLWhereClause() + ");";
	}

	/**
	 * SQL-Befehl zum Hinzufügen des Objekts in die Datenbank (Tabelle 'tblBdKursXLink.')
	 * @return SQL-String "insert into...".
	 **/
	private String toDBAddString(){
		return "insert into \"tblBdKursXLink\" ( " +
			"\"lngSdSeminarID\", \"lngKursID\", \"lngLinkID\", \"strKursLinkURL\", \"strKursLinkBezeichnung\", \"strKursLinkBeschreibung\", \"blnKursLinkDownload\", \"blnKursLinkVisible\", \"strKursLinkCustom1\", \"strKursLinkCustom2\", \"strKursLinkCustom3\" ) VALUES ( ?,?,?,?,?,?,?,?,?,?,?);";
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
	private void init(long lngSdSeminarID, long lngKursID, long lngLinkID) throws SQLException, NamingException{

		this.m_lSdSeminarID=lngSdSeminarID;

		this.m_lKursID=lngKursID;

		this.m_lLinkID=lngLinkID;

		
		PreparedStatement pstm = prepareStatement("select * from \"tblBdKursXLink\" where (" + this.getSQLWhereClause() + ");");
		pokeWhere(1,pstm);

		ResultSet rst=pstm.executeQuery();
		if(rst.next()) this.initByRst(rst);
		rst.close();
		this.disconnect();
		rst=null;
	}	
	
	/**
	 * Lade die Objekteigenschaften aus einem ResultSet.
	 * param rst ResultSet mit allen Spalten der Tabelle 'tblBdKursXLink'
	 * @throws SQLException 
	 **/
	protected void initByRst(ResultSet rst) throws SQLException{
		this.m_lSdSeminarID=rst.getLong("lngSdSeminarID");
		this.m_lKursID=rst.getLong("lngKursID");
		this.m_lLinkID=rst.getLong("lngLinkID");
		this.m_sKursLinkURL=rst.getString("strKursLinkURL");
		this.m_sKursLinkBezeichnung=rst.getString("strKursLinkBezeichnung");
		this.m_sKursLinkBeschreibung=rst.getString("strKursLinkBeschreibung");
		this.m_bKursLinkDownload=rst.getBoolean("blnKursLinkDownload");
		this.m_bKursLinkVisible=rst.getBoolean("blnKursLinkVisible");
		this.m_sKursLinkCustom1=rst.getString("strKursLinkCustom1");
		this.m_sKursLinkCustom2=rst.getString("strKursLinkCustom2");
		this.m_sKursLinkCustom3=rst.getString("strKursLinkCustom3");	
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
	public KursXLink(){}	
	
	/**
	 * Konstruktor mit Indexwerten (eindeutig) der assoziierten Datenbanktabelle
	 * @throws NamingException 
	 * @throws SQLException 
	 **/
	public KursXLink(long lngSdSeminarID, long lngKursID, long lngLinkID) throws SQLException, NamingException{
		this.init(lngSdSeminarID, lngKursID, lngLinkID);
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
	public KursXLink(ResultSet rst) throws SQLException{
		this.initByRst(rst);
		this.m_bIsDirty = false;
	}

  }//Klassenende
