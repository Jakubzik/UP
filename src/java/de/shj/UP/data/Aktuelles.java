
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
 * version 7-01	            
 *
 * Oct-2009		    h. jakubzik             autoclass
 * Jan-4-2011		h. jakubzik				initByRST auch für ResultSetSHJ
 * 											implementiert (wg. AktuellesIterator)
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

import de.shj.UP.util.ResultSetSHJ;

/**
 *  Module: Newsticker
Here, news-items are stored. News items notably have a start- and a stop date (both inclusive, indicating in what time-period the news item should be displayed on the homepage (or wherever else), a heading and a body.
News-items are always linked to a seminar (Univeristy Institute), and they can be linked to teachers (using the property DozentID. It seems advisable, however, just to sign the news-item with a name.
If an audit-trail is installed, it might be useful to use it on the frontend, too.
 *  
 *  ALLGEMEINE INFORMATION ZU DEN OBJEKTEN DIESES PACKAGES:
 *  Diese Klasse kapselt die Tabelle 'Aktuelles' in 
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
public class Aktuelles extends shjCore{

	private static final long serialVersionUID = 7L;

////////////////////////////////////////////////////////////////
// 1.   P R I V A T E   D E K L A R A T I O N E N
////////////////////////////////////////////////////////////////
	
	private boolean m_bIsDirty = false;

	private long m_lAktuellesSequence;
	private long m_lSdSeminarID;
	private String m_sAktuellesHeading;
	private String m_sAktuellesDetails;
	private Date m_dAktuellesStart;
	private Date m_dAktuellesStop;
	private String m_sAktuellesDetailLink;
	private String m_sAktuellesAutorName;
	private long m_lAktuellesAutorDozentID;
	private String m_sAktuellesCustom1;
	private String m_sAktuellesCustom2;
	private String m_sAktuellesCustom3;

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
	 * Sequence where to show this news-item
	 * @return Sequence where to show this news-item
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdAktuelles.lngAktuellesSequence</Code>
	 **/
	public long getAktuellesSequence(){
		return this.m_lAktuellesSequence;
	}

	/**
	 * Sequence where to show this news-item
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdAktuelles.lngAktuellesSequence</Code>
	 **/	
	public void setAktuellesSequence(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lAktuellesSequence));
		this.m_lAktuellesSequence=value;
	}
	

	/**
	 * Seminar
	 * @return Seminar
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdAktuelles.lngSdSeminarID</Code>
	 **/
	public long getSdSeminarID(){
		return this.m_lSdSeminarID;
	}

	/**
	 * Seminar
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdAktuelles.lngSdSeminarID</Code>
	 **/	
	public void setSdSeminarID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lSdSeminarID));
		this.m_lSdSeminarID=value;
	}
	

	/**
	 * Heading of this news-item
	 * @return Heading of this news-item
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdAktuelles.strAktuellesHeading</Code>
	 **/
	public String getAktuellesHeading(){
		return this.m_sAktuellesHeading;
	}

	/**
	 * Heading of this news-item
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdAktuelles.strAktuellesHeading</Code>
	 **/	
	public void setAktuellesHeading(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sAktuellesHeading)));
		this.m_sAktuellesHeading=value;
	}
	

	/**
	 * The detailed news-item.
	 * @return The detailed news-item.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdAktuelles.strAktuellesDetails</Code>
	 **/
	public String getAktuellesDetails(){
		return this.m_sAktuellesDetails;
	}

	/**
	 * The detailed news-item.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdAktuelles.strAktuellesDetails</Code>
	 **/	
	public void setAktuellesDetails(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sAktuellesDetails)));
		this.m_sAktuellesDetails=value;
	}
	

	/**
	 * Start-date (inclusive) from when to show this news-item on the homepage (or wherever else).
	 * @return Start-date (inclusive) from when to show this news-item on the homepage (or wherever else).
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdAktuelles.dtmAktuellesStart</Code>
	 **/
	public Date getAktuellesStart(){
		return this.m_dAktuellesStart;
	}

	/**
	 * Start-date (inclusive) from when to show this news-item on the homepage (or wherever else).
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdAktuelles.dtmAktuellesStart</Code>
	 **/	
	public void setAktuellesStart(Date value){
		this.m_bIsDirty = (this.m_bIsDirty || (value.equals(this.m_dAktuellesStart)));
		this.m_dAktuellesStart=value;
	}
	

	/**
	 * Stop-date (inclusive) until when to show this news-item on the homepage (or wherever else).
	 * @return Stop-date (inclusive) until when to show this news-item on the homepage (or wherever else).
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdAktuelles.dtmAktuellesStop</Code>
	 **/
	public Date getAktuellesStop(){
		return this.m_dAktuellesStop;
	}

	/**
	 * Stop-date (inclusive) until when to show this news-item on the homepage (or wherever else).
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdAktuelles.dtmAktuellesStop</Code>
	 **/	
	public void setAktuellesStop(Date value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_dAktuellesStop)));
		this.m_dAktuellesStop=value;
	}
	

	/**
	 * Optional: a link leading to further details of this news-item. Of course (depending on the implementation), the link can also be part of the heading or the details.
	 * @return Optional: a link leading to further details of this news-item. Of course (depending on the implementation), the link can also be part of the heading or the details.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdAktuelles.strAktuellesDetailLink</Code>
	 **/
	public String getAktuellesDetailLink(){
		return this.m_sAktuellesDetailLink;
	}

	/**
	 * Optional: a link leading to further details of this news-item. Of course (depending on the implementation), the link can also be part of the heading or the details.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdAktuelles.strAktuellesDetailLink</Code>
	 **/	
	public void setAktuellesDetailLink(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sAktuellesDetailLink)));
		this.m_sAktuellesDetailLink=value;
	}
	

	/**
	 * Name of person responsible for this news-item (_Autor_ is German for _author_).
	 * @return Name of person responsible for this news-item (_Autor_ is German for _author_).
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdAktuelles.strAktuellesAutorName</Code>
	 **/
	public String getAktuellesAutorName(){
		return this.m_sAktuellesAutorName;
	}

	/**
	 * Name of person responsible for this news-item (_Autor_ is German for _author_).
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdAktuelles.strAktuellesAutorName</Code>
	 **/	
	public void setAktuellesAutorName(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sAktuellesAutorName)));
		this.m_sAktuellesAutorName=value;
	}
	

	/**
	 * Id of teacher responsible for this news-item. It is probably better to use the (unrelated) column Autor instead.
	 * @return Id of teacher responsible for this news-item. It is probably better to use the (unrelated) column Autor instead.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdAktuelles.lngAktuellesAutorDozentID</Code>
	 **/
	public long getAktuellesAutorDozentID(){
		return this.m_lAktuellesAutorDozentID;
	}

	/**
	 * Id of teacher responsible for this news-item. It is probably better to use the (unrelated) column Autor instead.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdAktuelles.lngAktuellesAutorDozentID</Code>
	 **/	
	public void setAktuellesAutorDozentID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lAktuellesAutorDozentID));
		this.m_lAktuellesAutorDozentID=value;
	}
	

	/**
	 * Custom additional information
	 * @return Custom additional information
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdAktuelles.strAktuellesCustom1</Code>
	 **/
	public String getAktuellesCustom1(){
		return this.m_sAktuellesCustom1;
	}

	/**
	 * Custom additional information
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdAktuelles.strAktuellesCustom1</Code>
	 **/	
	public void setAktuellesCustom1(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sAktuellesCustom1)));
		this.m_sAktuellesCustom1=value;
	}
	

	/**
	 * Custom additional information
	 * @return Custom additional information
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdAktuelles.strAktuellesCustom2</Code>
	 **/
	public String getAktuellesCustom2(){
		return this.m_sAktuellesCustom2;
	}

	/**
	 * Custom additional information
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdAktuelles.strAktuellesCustom2</Code>
	 **/	
	public void setAktuellesCustom2(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sAktuellesCustom2)));
		this.m_sAktuellesCustom2=value;
	}
	

	/**
	 * Custom additional information
	 * @return Custom additional information
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdAktuelles.strAktuellesCustom3</Code>
	 **/
	public String getAktuellesCustom3(){
		return this.m_sAktuellesCustom3;
	}

	/**
	 * Custom additional information
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdAktuelles.strAktuellesCustom3</Code>
	 **/	
	public void setAktuellesCustom3(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sAktuellesCustom3)));
		this.m_sAktuellesCustom3=value;
	}
	


////////////////////////////////////////////////////////////////
// 3.   X M L  U T I L I T I E S
////////////////////////////////////////////////////////////////
	
	/**
	 * @return Objekt als XML-String
	 **/
	public String toXMLString(){
		return 	

			"<AktuellesSequence>" + m_lAktuellesSequence + "</AktuellesSequence>"  + 
			"<SdSeminarID>" + m_lSdSeminarID + "</SdSeminarID>"  + 
			"<AktuellesHeading>" + m_sAktuellesHeading + "</AktuellesHeading>"  + 
			"<AktuellesDetails>" + m_sAktuellesDetails + "</AktuellesDetails>"  + 
			"<AktuellesStart>" + m_dAktuellesStart + "</AktuellesStart>"  + 
			"<AktuellesStop>" + m_dAktuellesStop + "</AktuellesStop>"  + 
			"<AktuellesDetailLink>" + m_sAktuellesDetailLink + "</AktuellesDetailLink>"  + 
			"<AktuellesAutorName>" + m_sAktuellesAutorName + "</AktuellesAutorName>"  + 
			"<AktuellesAutorDozentID>" + m_lAktuellesAutorDozentID + "</AktuellesAutorDozentID>"  + 
			"<AktuellesCustom1>" + m_sAktuellesCustom1 + "</AktuellesCustom1>"  + 
			"<AktuellesCustom2>" + m_sAktuellesCustom2 + "</AktuellesCustom2>"  + 
			"<AktuellesCustom3>" + m_sAktuellesCustom3 + "</AktuellesCustom3>" ;
	}

////////////////////////////////////////////////////////////////
// 4.   S Q L  U T I L I T I E S
////////////////////////////////////////////////////////////////


	/**
	 * Where-SQL für eindeutigen Index dieses Objekte in der Datenbank (in Tabelle 'tblBdAktuelles.').
	 * @return SQL-String wie z.B. "Id1=? and Id2=?"
	 **/
	private String getSQLWhereClause(){
		return 
			"\"lngAktuellesSequence\"=? AND " + 
			"\"lngSdSeminarID\"=?";
	}

	/**
	 * SQL-Befehl zum Löschen des Objekts in der Datenbank (aus Tabelle 'tblBdAktuelles.')
	 * @return SQL-String zum Löschen des Objekts, z.B. "delete from T1 where (Id1=?);"
	 **/
	private String toDBDeleteString(){
		return "delete from \"tblBdAktuelles\" where ( " + this.getSQLWhereClause() + ");";
	}

	/**
 	 * Setzt die Werte für alle Platzhalter in {@link #getSQLWhereClause()}.
	 * @param ii Offset für die Parameter (inklusive).
	 * @throws SQLException 
	 **/
	private void pokeWhere(int ii, PreparedStatement ps) throws SQLException{
		
		ps.setLong(ii++, m_lAktuellesSequence);
		ps.setLong(ii++, m_lSdSeminarID);
	}

	/**
  	 * Füllt das SQL-Statement mit den aktuellen Werten des Objekts
	 * @throws SQLException
	 **/
	private void pokeStatement(PreparedStatement ps) throws SQLException{
		
		ps.setLong(1, m_lAktuellesSequence);
		ps.setLong(2, m_lSdSeminarID);
		ps.setString(3, m_sAktuellesHeading);
		ps.setString(4, m_sAktuellesDetails);
		ps.setDate(5, m_dAktuellesStart);
		ps.setDate(6, m_dAktuellesStop);
		ps.setString(7, m_sAktuellesDetailLink);
		ps.setString(8, m_sAktuellesAutorName);
		ps.setLong(9, m_lAktuellesAutorDozentID);
		ps.setString(10, m_sAktuellesCustom1);
		ps.setString(11, m_sAktuellesCustom2);
		ps.setString(12, m_sAktuellesCustom3);

	}

	/**
	 * SQL-Befehl zum Speichern der Objekteigenschaften in die Datenbank (Tabelle 'tblBdAktuelles.')
	 * @return PreparedStatemtent, das nach Aufruf von {@link #pokeStatement(PreparedStatement)} und {@link #execute()} das Update ausführt.
	 **/
	private String toDBUpdateString(){
		return "update \"tblBdAktuelles\" set " +
			"\"lngAktuellesSequence\"=?, " +
			"\"lngSdSeminarID\"=?, " +
			"\"strAktuellesHeading\"=?, " +
			"\"strAktuellesDetails\"=?, " +
			"\"dtmAktuellesStart\"=?, " +
			"\"dtmAktuellesStop\"=?, " +
			"\"strAktuellesDetailLink\"=?, " +
			"\"strAktuellesAutorName\"=?, " +
			"\"lngAktuellesAutorDozentID\"=?, " +
			"\"strAktuellesCustom1\"=?, " +
			"\"strAktuellesCustom2\"=?, " +
			"\"strAktuellesCustom3\"=?" +
			" where (" + this.getSQLWhereClause() + ");";
	}

	/**
	 * SQL-Befehl zum Hinzufügen des Objekts in die Datenbank (Tabelle 'tblBdAktuelles.')
	 * @return SQL-String "insert into...".
	 **/
	private String toDBAddString(){
		return "insert into \"tblBdAktuelles\" ( " +
			"\"lngAktuellesSequence\", \"lngSdSeminarID\", \"strAktuellesHeading\", \"strAktuellesDetails\", \"dtmAktuellesStart\", \"dtmAktuellesStop\", \"strAktuellesDetailLink\", \"strAktuellesAutorName\", \"lngAktuellesAutorDozentID\", \"strAktuellesCustom1\", \"strAktuellesCustom2\", \"strAktuellesCustom3\" ) VALUES ( ?,?,?,?,?,?,?,?,?,?,?,?);";
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
	private void init(long lngAktuellesSequence, long lngSdSeminarID) throws SQLException, NamingException{

		this.m_lAktuellesSequence=lngAktuellesSequence;

		this.m_lSdSeminarID=lngSdSeminarID;

		
		PreparedStatement pstm = prepareStatement("select * from \"tblBdAktuelles\" where (" + this.getSQLWhereClause() + ");");
		pokeWhere(1,pstm);

		ResultSet rst=pstm.executeQuery();
		if(rst.next()) this.initByRst(rst);
		rst.close();
		this.disconnect();
		rst=null;
	}	
	
	/**
	 * Lade die Objekteigenschaften aus einem ResultSet.
	 * param rst ResultSet mit allen Spalten der Tabelle 'tblBdAktuelles'
	 * @throws SQLException 
	 **/
	protected void initByRst(ResultSet rst) throws SQLException{
		this.m_lAktuellesSequence=rst.getLong("lngAktuellesSequence");
		this.m_lSdSeminarID=rst.getLong("lngSdSeminarID");
		this.m_sAktuellesHeading=rst.getString("strAktuellesHeading");
		this.m_sAktuellesDetails=rst.getString("strAktuellesDetails");
		this.m_dAktuellesStart=rst.getDate("dtmAktuellesStart");
		this.m_dAktuellesStop=rst.getDate("dtmAktuellesStop");
		this.m_sAktuellesDetailLink=rst.getString("strAktuellesDetailLink");
		this.m_sAktuellesAutorName=rst.getString("strAktuellesAutorName");
		this.m_lAktuellesAutorDozentID=rst.getLong("lngAktuellesAutorDozentID");
		this.m_sAktuellesCustom1=rst.getString("strAktuellesCustom1");
		this.m_sAktuellesCustom2=rst.getString("strAktuellesCustom2");
		this.m_sAktuellesCustom3=rst.getString("strAktuellesCustom3");	
	}	
	
	/**
	 * Lade die Objekteigenschaften aus einem ResultSet.
	 * param rst ResultSet mit allen Spalten der Tabelle 'tblBdAktuelles'
	 * @throws SQLException 
	 **/
	protected void initByRst(ResultSetSHJ rst) throws SQLException{
		this.m_lAktuellesSequence=rst.getLong("lngAktuellesSequence");
		this.m_lSdSeminarID=rst.getLong("lngSdSeminarID");
		this.m_sAktuellesHeading=rst.getString("strAktuellesHeading");
		this.m_sAktuellesDetails=rst.getString("strAktuellesDetails");
		try{
			this.m_dAktuellesStart=rst.getDate("dtmAktuellesStart");
			this.m_dAktuellesStop=rst.getDate("dtmAktuellesStop");
		}catch(Exception e){log("Aktuelles initByRst: Datumswerte Start oder Stopp können nicht initialisiert werden.", g_LOG_LEVEL_WARNING);}
		this.m_sAktuellesDetailLink=rst.getString("strAktuellesDetailLink");
		this.m_sAktuellesAutorName=rst.getString("strAktuellesAutorName");
		this.m_lAktuellesAutorDozentID=rst.getLong("lngAktuellesAutorDozentID");
		this.m_sAktuellesCustom1=rst.getString("strAktuellesCustom1");
		this.m_sAktuellesCustom2=rst.getString("strAktuellesCustom2");
		this.m_sAktuellesCustom3=rst.getString("strAktuellesCustom3");	
	}	
	
	/**
	 * Lade die Objekteigenschaften aus einer XML-Node.
	 * param node XML-Node mit allen Eigenschaften als Tags.
	 * @throws ParseException (Datum muss im ISO-Format yyyy-MM-dd übergeben werden).
	 **/
	private void initByNode(Node node) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		this.m_lAktuellesSequence=Long.parseLong(shjNodeValue(node, "AktuellesSequence"));
		this.m_lSdSeminarID=Long.parseLong(shjNodeValue(node, "SdSeminarID"));
		this.m_sAktuellesHeading=(shjNodeValue(node, "AktuellesHeading"));
		this.m_sAktuellesDetails=(shjNodeValue(node, "AktuellesDetails"));
		this.m_dAktuellesStart=(Date) (sdf.parse(shjNodeValue(node, "AktuellesStart")));
		this.m_dAktuellesStop=(Date) (sdf.parse(shjNodeValue(node, "AktuellesStop")));
		this.m_sAktuellesDetailLink=(shjNodeValue(node, "AktuellesDetailLink"));
		this.m_sAktuellesAutorName=(shjNodeValue(node, "AktuellesAutorName"));
		this.m_lAktuellesAutorDozentID=Long.parseLong(shjNodeValue(node, "AktuellesAutorDozentID"));
		this.m_sAktuellesCustom1=(shjNodeValue(node, "AktuellesCustom1"));
		this.m_sAktuellesCustom2=(shjNodeValue(node, "AktuellesCustom2"));
		this.m_sAktuellesCustom3=(shjNodeValue(node, "AktuellesCustom3"));
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
	public Aktuelles(){}	
	
	/**
	 * Konstruktor mit Indexwerten (eindeutig) der assoziierten Datenbanktabelle
	 * @throws NamingException 
	 * @throws SQLException 
	 **/
	public Aktuelles(long lngAktuellesSequence, long lngSdSeminarID) throws SQLException, NamingException{
		this.init(lngAktuellesSequence, lngSdSeminarID);
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
	public Aktuelles(ResultSet rst) throws SQLException{
		this.initByRst(rst);
		this.m_bIsDirty = false;
	}

	/**
	 * Konstruktor per XML-Darstellung des Objekts.
	 * @throws ParseException, if a date can't be read.
	 **/
	public Aktuelles(Node node) throws ParseException{
		this.initByNode(node);
		this.m_bIsDirty = false;
	}

  }//Klassenende
