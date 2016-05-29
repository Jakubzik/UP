/**
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
 *  Table to store med-hd-specific data of the staatsexamen (diploma) for the student's transcript.
 *  
 *  ALLGEMEINE INFORMATION ZU DEN OBJEKTEN DIESES PACKAGES:
 *  Diese Klasse kapselt die Tabelle 'MedStudentStaatsexamen' in 
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
public class MedStudentStaatsexamen extends shjCore{

	private static final long serialVersionUID = 7L;

////////////////////////////////////////////////////////////////
// 1.   P R I V A T E   D E K L A R A T I O N E N
////////////////////////////////////////////////////////////////
	
	private boolean m_bIsDirty = false;

	private String m_sMatrikelnummer;
	private Date m_dMedStudentGraduationDate;
	private Date m_dMedStudentVorklinikVon;
	private Date m_dMedStudentVorklinikBis;
	private Date m_dMedStudentKlinikVon;
	private Date m_dMedStudentKlinikBis;
	private String m_sMedStudentE1WP;
	private String m_sMedStudentE1NA;
	private String m_sMedStudentE1OP;
	private String m_sMedStudentE2WP;
	private String m_sMedStudentE2NA;
	private String m_sMedStudentE2OP;
	private String m_sMedStudentECS;
	private Date m_dMedStudentApprobation;
	private boolean m_bEmpty   = true;

////////////////////////////////////////////////////////////////
// 2.   Ö F F E N T L I C H E  E I G E N S C H A F T E N
////////////////////////////////////////////////////////////////

 	/**
 	 * Flag to indicate if there is a record in the database:
 	 * Call MedStudentStaatsexamen m = new MedStudentStaatsexamen('1234567');
 	 * if(m.isEmpty()) println "There is no student with matric. no. 1234567";
 	 * @return
 	 */
 	public boolean isEmpty(){
 		return m_bEmpty;
 	}
 	
	/**
 	 * Wahr, wenn sich das Objekt von der Datenbanktabellenzeile unterscheidet.
	 * @return: Urteil, ob das Objekt noch mit der Datenbanktabellenzeile identisch ist.
	 **/
	public boolean isDirty(){
	  return this.m_bIsDirty;
	}


	/**
	 * Registration number of this student. Must have seven digits, currently.
	 * @return Registration number of this student. Must have seven digits, currently.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdMedStudentStaatsexamen.strMatrikelnummer</Code>
	 **/
	public String getMatrikelnummer(){
		return this.m_sMatrikelnummer;
	}

	/**
	 * Registration number of this student. Must have seven digits, currently.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdMedStudentStaatsexamen.strMatrikelnummer</Code>
	 **/	
	public void setMatrikelnummer(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sMatrikelnummer)));
		this.m_sMatrikelnummer=value;
	}
	

	/**
	 * Datum des Abschlusses.
	 * @return Datum des Abschlusses.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdMedStudentStaatsexamen.dtmMedStudentGraduationDate</Code>
	 **/
	public Date getMedStudentGraduationDate(){
		return this.m_dMedStudentGraduationDate;
	}

	/**
	 * Datum des Abschlusses.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdMedStudentStaatsexamen.dtmMedStudentGraduationDate</Code>
	 **/	
	public void setMedStudentGraduationDate(Date value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_dMedStudentGraduationDate));
		this.m_dMedStudentGraduationDate=value;
	}
	

	/**
	 * Date when preclinical studies started.
	 * @return Date when preclinical studies started.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdMedStudentStaatsexamen.dtmMedStudentVorklinikVon</Code>
	 **/
	public Date getMedStudentVorklinikVon(){
		return this.m_dMedStudentVorklinikVon;
	}

	/**
	 * Date when preclinical studies started.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdMedStudentStaatsexamen.dtmMedStudentVorklinikVon</Code>
	 **/	
	public void setMedStudentVorklinikVon(Date value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_dMedStudentVorklinikVon));
		this.m_dMedStudentVorklinikVon=value;
	}
	

	/**
	 * Date when preclinical studies ended.
	 * @return Date when preclinical studies ended.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdMedStudentStaatsexamen.dtmMedStudentVorklinikBis</Code>
	 **/
	public Date getMedStudentVorklinikBis(){
		return this.m_dMedStudentVorklinikBis;
	}

	/**
	 * Date when preclinical studies ended.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdMedStudentStaatsexamen.dtmMedStudentVorklinikBis</Code>
	 **/	
	public void setMedStudentVorklinikBis(Date value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_dMedStudentVorklinikBis));
		this.m_dMedStudentVorklinikBis=value;
	}
	

	/**
	 * Date when clinical studies started.
	 * @return Date when clinical studies started.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdMedStudentStaatsexamen.dtmMedStudentKlinikVon</Code>
	 **/
	public Date getMedStudentKlinikVon(){
		return this.m_dMedStudentKlinikVon;
	}

	/**
	 * Date when clinical studies started.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdMedStudentStaatsexamen.dtmMedStudentKlinikVon</Code>
	 **/	
	public void setMedStudentKlinikVon(Date value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_dMedStudentKlinikVon));
		this.m_dMedStudentKlinikVon=value;
	}
	

	/**
	 * Date when clinical studies ended.
	 * @return Date when clinical studies ended.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdMedStudentStaatsexamen.dtmMedStudentKlinikBis</Code>
	 **/
	public Date getMedStudentKlinikBis(){
		return this.m_dMedStudentKlinikBis;
	}

	/**
	 * Date when clinical studies ended.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdMedStudentStaatsexamen.dtmMedStudentKlinikBis</Code>
	 **/	
	public void setMedStudentKlinikBis(Date value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_dMedStudentKlinikBis));
		this.m_dMedStudentKlinikBis=value;
	}
	

	/**
	 * (E)xam (1), (W)ritten part, (P)ersonal result.
	 * @return (E)xam (1), (W)ritten part, (P)ersonal result.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdMedStudentStaatsexamen.strMedStudentE1WP</Code>
	 **/
	public String getMedStudentE1WP(){
		return this.m_sMedStudentE1WP;
	}

	/**
	 * (E)xam (1), (W)ritten part, (P)ersonal result.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdMedStudentStaatsexamen.strMedStudentE1WP</Code>
	 **/	
	public void setMedStudentE1WP(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sMedStudentE1WP)));
		this.m_sMedStudentE1WP=value;
	}
	

	/**
	 * (E)xam (1), (W)ritten part, (N)ational average.
	 * @return (E)xam (1), (W)ritten part, (N)ational average.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdMedStudentStaatsexamen.strMedStudentE1NA</Code>
	 **/
	public String getMedStudentE1NA(){
		return this.m_sMedStudentE1NA;
	}

	/**
	 * (E)xam (1), (W)ritten part, (N)ational average.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdMedStudentStaatsexamen.strMedStudentE1NA</Code>
	 **/	
	public void setMedStudentE1NA(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sMedStudentE1NA)));
		this.m_sMedStudentE1NA=value;
	}
	

	/**
	 * (E)xam (1), (O)ral part, (P)ersonal result.
	 * @return (E)xam (1), (O)ral part, (P)ersonal result.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdMedStudentStaatsexamen.strMedStudentE1OP</Code>
	 **/
	public String getMedStudentE1OP(){
		return this.m_sMedStudentE1OP;
	}

	/**
	 * (E)xam (1), (O)ral part, (P)ersonal result.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdMedStudentStaatsexamen.strMedStudentE1OP</Code>
	 **/	
	public void setMedStudentE1OP(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sMedStudentE1OP)));
		this.m_sMedStudentE1OP=value;
	}
	

	/**
	 * (E)xam (2), (W)ritten part, (P)ersonal result.
	 * @return (E)xam (2), (W)ritten part, (P)ersonal result.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdMedStudentStaatsexamen.strMedStudentE2WP</Code>
	 **/
	public String getMedStudentE2WP(){
		return this.m_sMedStudentE2WP;
	}

	/**
	 * (E)xam (2), (W)ritten part, (P)ersonal result.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdMedStudentStaatsexamen.strMedStudentE2WP</Code>
	 **/	
	public void setMedStudentE2WP(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sMedStudentE2WP)));
		this.m_sMedStudentE2WP=value;
	}
	

	/**
	 * (E)xam (2), (W)ritten part, (N)ational average.
	 * @return (E)xam (2), (W)ritten part, (N)ational average.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdMedStudentStaatsexamen.strMedStudentE2NA</Code>
	 **/
	public String getMedStudentE2NA(){
		return this.m_sMedStudentE2NA;
	}

	/**
	 * (E)xam (2), (W)ritten part, (N)ational average.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdMedStudentStaatsexamen.strMedStudentE2NA</Code>
	 **/	
	public void setMedStudentE2NA(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sMedStudentE2NA)));
		this.m_sMedStudentE2NA=value;
	}
	

	/**
	 * (E)xam (2), (O)ral part, (P)ersonal result.
	 * @return (E)xam (2), (O)ral part, (P)ersonal result.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdMedStudentStaatsexamen.strMedStudentE2OP</Code>
	 **/
	public String getMedStudentE2OP(){
		return this.m_sMedStudentE2OP;
	}

	/**
	 * (E)xam (2), (O)ral part, (P)ersonal result.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdMedStudentStaatsexamen.strMedStudentE2OP</Code>
	 **/	
	public void setMedStudentE2OP(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sMedStudentE2OP)));
		this.m_sMedStudentE2OP=value;
	}
	

	/**
	 * (E)xams, (C)umulative (S)core.
	 * @return (E)xams, (C)umulative (S)core.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdMedStudentStaatsexamen.strMedStudentECS</Code>
	 **/
	public String getMedStudentECS(){
		return this.m_sMedStudentECS;
	}

	/**
	 * (E)xams, (C)umulative (S)core.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdMedStudentStaatsexamen.strMedStudentECS</Code>
	 **/	
	public void setMedStudentECS(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sMedStudentECS)));
		this.m_sMedStudentECS=value;
	}
	

	/**
	 * Date when student recieved approbation.
	 * @return Date when student recieved approbation.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdMedStudentStaatsexamen.dtmMedStudentApprobation</Code>
	 **/
	public Date getMedStudentApprobation(){
		return this.m_dMedStudentApprobation;
	}

	/**
	 * Date when student recieved approbation.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdMedStudentStaatsexamen.dtmMedStudentApprobation</Code>
	 **/	
	public void setMedStudentApprobation(Date value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_dMedStudentApprobation));
		this.m_dMedStudentApprobation=value;
	}
	


////////////////////////////////////////////////////////////////
// 3.   X M L  U T I L I T I E S
////////////////////////////////////////////////////////////////
	
	/**
	 * @return Objekt als XML-String
	 **/
	public String toXMLString(){
		return 	

			"<Matrikelnummer>" + m_sMatrikelnummer + "</Matrikelnummer>"  + 
			"<MedStudentGraduationDate>" + m_dMedStudentGraduationDate + "</MedStudentGraduationDate>"  + 
			"<MedStudentVorklinikVon>" + m_dMedStudentVorklinikVon + "</MedStudentVorklinikVon>"  + 
			"<MedStudentVorklinikBis>" + m_dMedStudentVorklinikBis + "</MedStudentVorklinikBis>"  + 
			"<MedStudentKlinikVon>" + m_dMedStudentKlinikVon + "</MedStudentKlinikVon>"  + 
			"<MedStudentKlinikBis>" + m_dMedStudentKlinikBis + "</MedStudentKlinikBis>"  + 
			"<MedStudentE1WP>" + m_sMedStudentE1WP + "</MedStudentE1WP>"  + 
			"<MedStudentE1NA>" + m_sMedStudentE1NA + "</MedStudentE1NA>"  + 
			"<MedStudentE1OP>" + m_sMedStudentE1OP + "</MedStudentE1OP>"  + 
			"<MedStudentE2WP>" + m_sMedStudentE2WP + "</MedStudentE2WP>"  + 
			"<MedStudentE2NA>" + m_sMedStudentE2NA + "</MedStudentE2NA>"  + 
			"<MedStudentE2OP>" + m_sMedStudentE2OP + "</MedStudentE2OP>"  + 
			"<MedStudentECS>" + m_sMedStudentECS + "</MedStudentECS>"  + 
			"<MedStudentApprobation>" + m_dMedStudentApprobation + "</MedStudentApprobation>" ;
	}

////////////////////////////////////////////////////////////////
// 4.   S Q L  U T I L I T I E S
////////////////////////////////////////////////////////////////


	/**
	 * Where-SQL für eindeutigen Index dieses Objekte in der Datenbank (in Tabelle 'tblBdMedStudentStaatsexamen.').
	 * @return SQL-String wie z.B. "Id1=? and Id2=?"
	 **/
	private String getSQLWhereClause(){
		return 
			"\"strMatrikelnummer\"=?";
	}

	/**
	 * SQL-Befehl zum Löschen des Objekts in der Datenbank (aus Tabelle 'tblBdMedStudentStaatsexamen.')
	 * @return SQL-String zum Löschen des Objekts, z.B. "delete from T1 where (Id1=?);"
	 **/
	private String toDBDeleteString(){
		return "delete from \"tblBdMedStudentStaatsexamen\" where ( " + this.getSQLWhereClause() + ");";
	}

	/**
 	 * Setzt die Werte für alle Platzhalter in {@link #getSQLWhereClause()}.
	 * @param ii Offset für die Parameter (inklusive).
	 * @throws SQLException 
	 **/
	private void pokeWhere(int ii, PreparedStatement ps) throws SQLException{
		
		ps.setString(ii++, m_sMatrikelnummer);
	}

	/**
  	 * Füllt das SQL-Statement mit den aktuellen Werten des Objekts
	 * @throws SQLException
	 **/
	private void pokeStatement(PreparedStatement ps) throws SQLException{
		
		ps.setString(1, m_sMatrikelnummer);
		ps.setDate(2, m_dMedStudentGraduationDate);
		ps.setDate(3, m_dMedStudentVorklinikVon);
		ps.setDate(4, m_dMedStudentVorklinikBis);
		ps.setDate(5, m_dMedStudentKlinikVon);
		ps.setDate(6, m_dMedStudentKlinikBis);
		ps.setString(7, m_sMedStudentE1WP);
		ps.setString(8, m_sMedStudentE1NA);
		ps.setString(9, m_sMedStudentE1OP);
		ps.setString(10, m_sMedStudentE2WP);
		ps.setString(11, m_sMedStudentE2NA);
		ps.setString(12, m_sMedStudentE2OP);
		ps.setString(13, m_sMedStudentECS);
		ps.setDate(14, m_dMedStudentApprobation);

	}

	/**
	 * SQL-Befehl zum Speichern der Objekteigenschaften in die Datenbank (Tabelle 'tblBdMedStudentStaatsexamen.')
	 * @return PreparedStatemtent, das nach Aufruf von {@link #pokeStatement(PreparedStatement)} und {@link #execute()} das Update ausführt.
	 **/
	private String toDBUpdateString(){
		return "update \"tblBdMedStudentStaatsexamen\" set " +
			"\"strMatrikelnummer\"=?, " +
			"\"dtmMedStudentGraduationDate\"=?, " +
			"\"dtmMedStudentVorklinikVon\"=?, " +
			"\"dtmMedStudentVorklinikBis\"=?, " +
			"\"dtmMedStudentKlinikVon\"=?, " +
			"\"dtmMedStudentKlinikBis\"=?, " +
			"\"strMedStudentE1WP\"=?, " +
			"\"strMedStudentE1NA\"=?, " +
			"\"strMedStudentE1OP\"=?, " +
			"\"strMedStudentE2WP\"=?, " +
			"\"strMedStudentE2NA\"=?, " +
			"\"strMedStudentE2OP\"=?, " +
			"\"strMedStudentECS\"=?, " +
			"\"dtmMedStudentApprobation\"=?" +
			" where (" + this.getSQLWhereClause() + ");";
	}

	/**
	 * SQL-Befehl zum Hinzufügen des Objekts in die Datenbank (Tabelle 'tblBdMedStudentStaatsexamen.')
	 * @return SQL-String "insert into...".
	 **/
	private String toDBAddString(){
		return "insert into \"tblBdMedStudentStaatsexamen\" ( " +
			"\"strMatrikelnummer\", \"dtmMedStudentGraduationDate\", \"dtmMedStudentVorklinikVon\", \"dtmMedStudentVorklinikBis\", \"dtmMedStudentKlinikVon\", \"dtmMedStudentKlinikBis\", \"strMedStudentE1WP\", \"strMedStudentE1NA\", \"strMedStudentE1OP\", \"strMedStudentE2WP\", \"strMedStudentE2NA\", \"strMedStudentE2OP\", \"strMedStudentECS\", \"dtmMedStudentApprobation\" ) VALUES ( ?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
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
	private void init(String strMatrikelnummer) throws SQLException, NamingException{

		this.m_sMatrikelnummer=strMatrikelnummer;

		
		PreparedStatement pstm = prepareStatement("select * from \"tblBdMedStudentStaatsexamen\" where (" + this.getSQLWhereClause() + ");");
		pokeWhere(1,pstm);

		ResultSet rst=pstm.executeQuery();
		if(rst.next()){ this.initByRst(rst);m_bEmpty=false;}
		rst.close();
		this.disconnect();
		rst=null;
	}	
	
	/**
	 * Lade die Objekteigenschaften aus einem ResultSet.
	 * param rst ResultSet mit allen Spalten der Tabelle 'tblBdMedStudentStaatsexamen'
	 * @throws SQLException 
	 **/
	protected void initByRst(ResultSet rst) throws SQLException{
		this.m_sMatrikelnummer=rst.getString("strMatrikelnummer");
		this.m_dMedStudentGraduationDate=rst.getDate("dtmMedStudentGraduationDate");
		this.m_dMedStudentVorklinikVon=rst.getDate("dtmMedStudentVorklinikVon");
		this.m_dMedStudentVorklinikBis=rst.getDate("dtmMedStudentVorklinikBis");
		this.m_dMedStudentKlinikVon=rst.getDate("dtmMedStudentKlinikVon");
		this.m_dMedStudentKlinikBis=rst.getDate("dtmMedStudentKlinikBis");
		this.m_sMedStudentE1WP=rst.getString("strMedStudentE1WP");
		this.m_sMedStudentE1NA=rst.getString("strMedStudentE1NA");
		this.m_sMedStudentE1OP=rst.getString("strMedStudentE1OP");
		this.m_sMedStudentE2WP=rst.getString("strMedStudentE2WP");
		this.m_sMedStudentE2NA=rst.getString("strMedStudentE2NA");
		this.m_sMedStudentE2OP=rst.getString("strMedStudentE2OP");
		this.m_sMedStudentECS=rst.getString("strMedStudentECS");
		this.m_dMedStudentApprobation=rst.getDate("dtmMedStudentApprobation");	
	}	
	
	/**
	 * Lade die Objekteigenschaften aus einer XML-Node.
	 * param node XML-Node mit allen Eigenschaften als Tags.
	 * @throws ParseException (Datum muss im ISO-Format yyyy-MM-dd übergeben werden).
	 **/
	private void initByNode(Node node) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		this.m_sMatrikelnummer=(shjNodeValue(node, "Matrikelnummer"));
		this.m_dMedStudentGraduationDate=(Date) (sdf.parse(shjNodeValue(node, "MedStudentGraduationDate")));
		this.m_dMedStudentVorklinikVon=(Date) (sdf.parse(shjNodeValue(node, "MedStudentVorklinikVon")));
		this.m_dMedStudentVorklinikBis=(Date) (sdf.parse(shjNodeValue(node, "MedStudentVorklinikBis")));
		this.m_dMedStudentKlinikVon=(Date) (sdf.parse(shjNodeValue(node, "MedStudentKlinikVon")));
		this.m_dMedStudentKlinikBis=(Date) (sdf.parse(shjNodeValue(node, "MedStudentKlinikBis")));
		this.m_sMedStudentE1WP=(shjNodeValue(node, "MedStudentE1WP"));
		this.m_sMedStudentE1NA=(shjNodeValue(node, "MedStudentE1NA"));
		this.m_sMedStudentE1OP=(shjNodeValue(node, "MedStudentE1OP"));
		this.m_sMedStudentE2WP=(shjNodeValue(node, "MedStudentE2WP"));
		this.m_sMedStudentE2NA=(shjNodeValue(node, "MedStudentE2NA"));
		this.m_sMedStudentE2OP=(shjNodeValue(node, "MedStudentE2OP"));
		this.m_sMedStudentECS=(shjNodeValue(node, "MedStudentECS"));
		this.m_dMedStudentApprobation=(Date) (sdf.parse(shjNodeValue(node, "MedStudentApprobation")));
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
		pokeWhere(15,pstm);
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
	public MedStudentStaatsexamen(){}	
	
	/**
	 * Konstruktor mit Indexwerten (eindeutig) der assoziierten Datenbanktabelle
	 * @throws NamingException 
	 * @throws SQLException 
	 **/
	public MedStudentStaatsexamen(String strMatrikelnummer) throws SQLException, NamingException{
		this.init(strMatrikelnummer);
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
	public MedStudentStaatsexamen(ResultSet rst) throws SQLException{
		this.initByRst(rst);
		this.m_bIsDirty = false;
	}

	/**
	 * Konstruktor per XML-Darstellung des Objekts.
	 * @throws ParseException, if a date can't be read.
	 **/
	public MedStudentStaatsexamen(Node node) throws ParseException{
		this.initByNode(node);
		this.m_bIsDirty = false;
	}

  }//Klassenende
