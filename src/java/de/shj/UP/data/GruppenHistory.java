
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
 *  Module: Groups
This is the n:m map between people and groups. It can show a history if you administer more than just one semester.
 *  
 *  ALLGEMEINE INFORMATION ZU DEN OBJEKTEN DIESES PACKAGES:
 *  Diese Klasse kapselt die Tabelle 'GruppenHistory' in 
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
public class GruppenHistory extends shjCore{

	private static final long serialVersionUID = 7L;

////////////////////////////////////////////////////////////////
// 1.   P R I V A T E   D E K L A R A T I O N E N
////////////////////////////////////////////////////////////////
	
	private boolean m_bIsDirty = false;

	private long m_lSdSeminarID;
	private String m_sBdSemestername;
	private long m_lSdDozentID;
	private long m_lSdGruppenID;
	private String m_sDozentBereich;
	private String m_sDozentBemerkung;
	private String m_sDozentDeputat;
	private String m_sDozentMittelherkunft;
	private String m_sGruppenTyp;
	private int m_iLfdNr;
	private String m_sDozentOD;
	private String m_sBdZusatzInfo;
	private long m_lCount;

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
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdGruppenHistory.lngSdSeminarID</Code>
	 **/
	public long getSdSeminarID(){
		return this.m_lSdSeminarID;
	}

	/**
	 * Seminar
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdGruppenHistory.lngSdSeminarID</Code>
	 **/	
	public void setSdSeminarID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lSdSeminarID));
		this.m_lSdSeminarID=value;
	}
	

	/**
	 * Name of semester where this mapping of Dozent X to Group Y was valid.
	 * @return Name of semester where this mapping of Dozent X to Group Y was valid.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdGruppenHistory.strBdSemestername</Code>
	 **/
	public String getBdSemestername(){
		return this.m_sBdSemestername;
	}

	/**
	 * Name of semester where this mapping of Dozent X to Group Y was valid.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdGruppenHistory.strBdSemestername</Code>
	 **/	
	public void setBdSemestername(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sBdSemestername)));
		this.m_sBdSemestername=value;
	}
	

	/**
	 * Id of teacher who was a member of group GroupID at the given semester.
	 * @return Id of teacher who was a member of group GroupID at the given semester.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdGruppenHistory.lngSdDozentID</Code>
	 **/
	public long getSdDozentID(){
		return this.m_lSdDozentID;
	}

	/**
	 * Id of teacher who was a member of group GroupID at the given semester.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdGruppenHistory.lngSdDozentID</Code>
	 **/	
	public void setSdDozentID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lSdDozentID));
		this.m_lSdDozentID=value;
	}
	

	/**
	 * Id of group that teacher lngDozentID was a member of in the given semester.
	 * @return Id of group that teacher lngDozentID was a member of in the given semester.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdGruppenHistory.lngSdGruppenID</Code>
	 **/
	public long getSdGruppenID(){
		return this.m_lSdGruppenID;
	}

	/**
	 * Id of group that teacher lngDozentID was a member of in the given semester.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdGruppenHistory.lngSdGruppenID</Code>
	 **/	
	public void setSdGruppenID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lSdGruppenID));
		this.m_lSdGruppenID=value;
	}
	

	/**
	 * Utility, cusomizable. "Bereich" is German for "Field".
	 * @return Utility, cusomizable. "Bereich" is German for "Field".
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdGruppenHistory.strDozentBereich</Code>
	 **/
	public String getDozentBereich(){
		return this.m_sDozentBereich;
	}

	/**
	 * Utility, cusomizable. "Bereich" is German for "Field".
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdGruppenHistory.strDozentBereich</Code>
	 **/	
	public void setDozentBereich(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDozentBereich)));
		this.m_sDozentBereich=value;
	}
	

	/**
	 * Remark
	 * @return Remark
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdGruppenHistory.strDozentBemerkung</Code>
	 **/
	public String getDozentBemerkung(){
		return this.m_sDozentBemerkung;
	}

	/**
	 * Remark
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdGruppenHistory.strDozentBemerkung</Code>
	 **/	
	public void setDozentBemerkung(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDozentBemerkung)));
		this.m_sDozentBemerkung=value;
	}
	

	/**
	 * How many hours per week does the teacher teach?
	 * @return How many hours per week does the teacher teach?
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdGruppenHistory.strDozentDeputat</Code>
	 **/
	public String getDozentDeputat(){
		return this.m_sDozentDeputat;
	}

	/**
	 * How many hours per week does the teacher teach?
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdGruppenHistory.strDozentDeputat</Code>
	 **/	
	public void setDozentDeputat(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDozentDeputat)));
		this.m_sDozentDeputat=value;
	}
	

	/**
	 * From what money is this teacher payed?
	 * @return From what money is this teacher payed?
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdGruppenHistory.strDozentMittelherkunft</Code>
	 **/
	public String getDozentMittelherkunft(){
		return this.m_sDozentMittelherkunft;
	}

	/**
	 * From what money is this teacher payed?
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdGruppenHistory.strDozentMittelherkunft</Code>
	 **/	
	public void setDozentMittelherkunft(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDozentMittelherkunft)));
		this.m_sDozentMittelherkunft=value;
	}
	

	/**
	 * What type of group is this?
	 * @return What type of group is this?
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdGruppenHistory.strGruppenTyp</Code>
	 **/
	public String getGruppenTyp(){
		return this.m_sGruppenTyp;
	}

	/**
	 * What type of group is this?
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdGruppenHistory.strGruppenTyp</Code>
	 **/	
	public void setGruppenTyp(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sGruppenTyp)));
		this.m_sGruppenTyp=value;
	}
	

	/**
	 * What sequential number does this map-item have?
	 * @return What sequential number does this map-item have?
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdGruppenHistory.intLfdNr</Code>
	 **/
	public int getLfdNr(){
		return this.m_iLfdNr;
	}

	/**
	 * What sequential number does this map-item have?
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdGruppenHistory.intLfdNr</Code>
	 **/	
	public void setLfdNr(int value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_iLfdNr));
		this.m_iLfdNr=value;
	}
	

	/**
	 * Is this teacher a civil servant?
	 * @return Is this teacher a civil servant?
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdGruppenHistory.txtDozentOD</Code>
	 **/
	public String getDozentOD(){
		return this.m_sDozentOD;
	}

	/**
	 * Is this teacher a civil servant?
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdGruppenHistory.txtDozentOD</Code>
	 **/	
	public void setDozentOD(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDozentOD)));
		this.m_sDozentOD=value;
	}
	

	/**
	 * Additional information.
	 * @return Additional information.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdGruppenHistory.strBdZusatzInfo</Code>
	 **/
	public String getBdZusatzInfo(){
		return this.m_sBdZusatzInfo;
	}

	/**
	 * Additional information.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdGruppenHistory.strBdZusatzInfo</Code>
	 **/	
	public void setBdZusatzInfo(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sBdZusatzInfo)));
		this.m_sBdZusatzInfo=value;
	}
	

	/**
	 * Count
	 * @return Count
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdGruppenHistory.lngCount</Code>
	 **/
	public long getCount(){
		return this.m_lCount;
	}

	/**
	 * Count
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdGruppenHistory.lngCount</Code>
	 **/	
	public void setCount(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lCount));
		this.m_lCount=value;
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
			"<BdSemestername>" + m_sBdSemestername + "</BdSemestername>"  + 
			"<SdDozentID>" + m_lSdDozentID + "</SdDozentID>"  + 
			"<SdGruppenID>" + m_lSdGruppenID + "</SdGruppenID>"  + 
			"<DozentBereich>" + m_sDozentBereich + "</DozentBereich>"  + 
			"<DozentBemerkung>" + m_sDozentBemerkung + "</DozentBemerkung>"  + 
			"<DozentDeputat>" + m_sDozentDeputat + "</DozentDeputat>"  + 
			"<DozentMittelherkunft>" + m_sDozentMittelherkunft + "</DozentMittelherkunft>"  + 
			"<GruppenTyp>" + m_sGruppenTyp + "</GruppenTyp>"  + 
			"<LfdNr>" + m_iLfdNr + "</LfdNr>"  + 
			"<DozentOD>" + m_sDozentOD + "</DozentOD>"  + 
			"<BdZusatzInfo>" + m_sBdZusatzInfo + "</BdZusatzInfo>"  + 
			"<Count>" + m_lCount + "</Count>" ;
	}

////////////////////////////////////////////////////////////////
// 4.   S Q L  U T I L I T I E S
////////////////////////////////////////////////////////////////


	/**
	 * Where-SQL für eindeutigen Index dieses Objekte in der Datenbank (in Tabelle 'tblBdGruppenHistory.').
	 * @return SQL-String wie z.B. "Id1=? and Id2=?"
	 **/
	private String getSQLWhereClause(){
		return 
			"\"lngCount\"=?";
	}

	/**
	 * SQL-Befehl zum Löschen des Objekts in der Datenbank (aus Tabelle 'tblBdGruppenHistory.')
	 * @return SQL-String zum Löschen des Objekts, z.B. "delete from T1 where (Id1=?);"
	 **/
	private String toDBDeleteString(){
		return "delete from \"tblBdGruppenHistory\" where ( " + this.getSQLWhereClause() + ");";
	}

	/**
 	 * Setzt die Werte für alle Platzhalter in {@link #getSQLWhereClause()}.
	 * @param ii Offset für die Parameter (inklusive).
	 * @throws SQLException 
	 **/
	private void pokeWhere(int ii, PreparedStatement ps) throws SQLException{
		
		ps.setLong(ii++, m_lCount);
	}

	/**
  	 * Füllt das SQL-Statement mit den aktuellen Werten des Objekts
	 * @throws SQLException
	 **/
	private void pokeStatement(PreparedStatement ps) throws SQLException{
		
		ps.setLong(1, m_lSdSeminarID);
		ps.setString(2, m_sBdSemestername);
		ps.setLong(3, m_lSdDozentID);
		ps.setLong(4, m_lSdGruppenID);
		ps.setString(5, m_sDozentBereich);
		ps.setString(6, m_sDozentBemerkung);
		ps.setString(7, m_sDozentDeputat);
		ps.setString(8, m_sDozentMittelherkunft);
		ps.setString(9, m_sGruppenTyp);
		ps.setInt(10, m_iLfdNr);
		ps.setString(11, m_sDozentOD);
		ps.setString(12, m_sBdZusatzInfo);
		ps.setLong(13, m_lCount);

	}

	/**
	 * SQL-Befehl zum Speichern der Objekteigenschaften in die Datenbank (Tabelle 'tblBdGruppenHistory.')
	 * @return PreparedStatemtent, das nach Aufruf von {@link #pokeStatement(PreparedStatement)} und {@link #execute()} das Update ausführt.
	 **/
	private String toDBUpdateString(){
		return "update \"tblBdGruppenHistory\" set " +
			"\"lngSdSeminarID\"=?, " +
			"\"strBdSemestername\"=?, " +
			"\"lngSdDozentID\"=?, " +
			"\"lngSdGruppenID\"=?, " +
			"\"strDozentBereich\"=?, " +
			"\"strDozentBemerkung\"=?, " +
			"\"strDozentDeputat\"=?, " +
			"\"strDozentMittelherkunft\"=?, " +
			"\"strGruppenTyp\"=?, " +
			"\"intLfdNr\"=?, " +
			"\"txtDozentOD\"=?, " +
			"\"strBdZusatzInfo\"=?, " +
			"\"lngCount\"=?" +
			" where (" + this.getSQLWhereClause() + ");";
	}

	/**
	 * SQL-Befehl zum Hinzufügen des Objekts in die Datenbank (Tabelle 'tblBdGruppenHistory.')
	 * @return SQL-String "insert into...".
	 **/
	private String toDBAddString(){
		return "insert into \"tblBdGruppenHistory\" ( " +
			"\"lngSdSeminarID\", \"strBdSemestername\", \"lngSdDozentID\", \"lngSdGruppenID\", \"strDozentBereich\", \"strDozentBemerkung\", \"strDozentDeputat\", \"strDozentMittelherkunft\", \"strGruppenTyp\", \"intLfdNr\", \"txtDozentOD\", \"strBdZusatzInfo\", \"lngCount\" ) VALUES ( ?,?,?,?,?,?,?,?,?,?,?,?,?);";
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
	private void init(long lngCount) throws SQLException, NamingException{

		this.m_lCount=lngCount;

		
		PreparedStatement pstm = prepareStatement("select * from \"tblBdGruppenHistory\" where (" + this.getSQLWhereClause() + ");");
		pokeWhere(1,pstm);

		ResultSet rst=pstm.executeQuery();
		if(rst.next()) this.initByRst(rst);
		rst.close();
		this.disconnect();
		rst=null;
	}	
	
	/**
	 * Lade die Objekteigenschaften aus einem ResultSet.
	 * param rst ResultSet mit allen Spalten der Tabelle 'tblBdGruppenHistory'
	 * @throws SQLException 
	 **/
	protected void initByRst(ResultSet rst) throws SQLException{
		this.m_lSdSeminarID=rst.getLong("lngSdSeminarID");
		this.m_sBdSemestername=rst.getString("strBdSemestername");
		this.m_lSdDozentID=rst.getLong("lngSdDozentID");
		this.m_lSdGruppenID=rst.getLong("lngSdGruppenID");
		this.m_sDozentBereich=rst.getString("strDozentBereich");
		this.m_sDozentBemerkung=rst.getString("strDozentBemerkung");
		this.m_sDozentDeputat=rst.getString("strDozentDeputat");
		this.m_sDozentMittelherkunft=rst.getString("strDozentMittelherkunft");
		this.m_sGruppenTyp=rst.getString("strGruppenTyp");
		this.m_iLfdNr=rst.getInt("intLfdNr");
		this.m_sDozentOD=rst.getString("txtDozentOD");
		this.m_sBdZusatzInfo=rst.getString("strBdZusatzInfo");
		this.m_lCount=rst.getLong("lngCount");	
	}	
	
	/**
	 * Lade die Objekteigenschaften aus einer XML-Node.
	 * param node XML-Node mit allen Eigenschaften als Tags.
	 * @throws ParseException (Datum muss im ISO-Format yyyy-MM-dd übergeben werden).
	 **/
	private void initByNode(Node node) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		this.m_lSdSeminarID=Long.parseLong(shjNodeValue(node, "SdSeminarID"));
		this.m_sBdSemestername=(shjNodeValue(node, "BdSemestername"));
		this.m_lSdDozentID=Long.parseLong(shjNodeValue(node, "SdDozentID"));
		this.m_lSdGruppenID=Long.parseLong(shjNodeValue(node, "SdGruppenID"));
		this.m_sDozentBereich=(shjNodeValue(node, "DozentBereich"));
		this.m_sDozentBemerkung=(shjNodeValue(node, "DozentBemerkung"));
		this.m_sDozentDeputat=(shjNodeValue(node, "DozentDeputat"));
		this.m_sDozentMittelherkunft=(shjNodeValue(node, "DozentMittelherkunft"));
		this.m_sGruppenTyp=(shjNodeValue(node, "GruppenTyp"));
		this.m_iLfdNr=Integer.parseInt(shjNodeValue(node, "LfdNr"));
		this.m_sDozentOD=(shjNodeValue(node, "DozentOD"));
		this.m_sBdZusatzInfo=(shjNodeValue(node, "BdZusatzInfo"));
		this.m_lCount=Long.parseLong(shjNodeValue(node, "Count"));
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
		pokeWhere(14,pstm);
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
	public GruppenHistory(){}	
	
	/**
	 * Konstruktor mit Indexwerten (eindeutig) der assoziierten Datenbanktabelle
	 * @throws NamingException 
	 * @throws SQLException 
	 **/
	public GruppenHistory(long lngCount) throws SQLException, NamingException{
		this.init(lngCount);
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
	public GruppenHistory(ResultSet rst) throws SQLException{
		this.initByRst(rst);
		this.m_bIsDirty = false;
	}

	/**
	 * Konstruktor per XML-Darstellung des Objekts.
	 * @throws ParseException, if a date can't be read.
	 **/
	public GruppenHistory(Node node) throws ParseException{
		this.initByNode(node);
		this.m_bIsDirty = false;
	}

  }//Klassenende
