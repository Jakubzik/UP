
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
 *  Module: Exam 1:n map of exams that this student took. Details about this specific Exam (which credits were used to fulfill its requirements) can be found in "tblBdStudentPruefungDetails." Please note that this table/object should potentially be able to handle _applications_ for exams, too. (The frontent does not handle this, yet). That's the reason why plenty of null-values are allowed on crucial columns. Another thing to remember is that, usually, an exam here will not have a grade with an 'intNoteID.' Calculations of final grades don't fit the ECTS-scala. There is definitely need for further thought on this.
 *  
 *  ALLGEMEINE INFORMATION ZU DEN OBJEKTEN DIESES PACKAGES:
 *  Diese Klasse kapselt die Tabelle 'StudentXPruefung' in 
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
public class StudentXPruefung extends shjCore{

	private static final long serialVersionUID = 7L;

////////////////////////////////////////////////////////////////
// 1.   P R I V A T E   D E K L A R A T I O N E N
////////////////////////////////////////////////////////////////
	
	private boolean m_bIsDirty = false;

	private long m_lSdSeminarID;
	private long m_lSdPruefungsID;
	private String m_sMatrikelnummer;
	private int m_iStudentPruefungCount;
	private int m_iNoteID;
	private String m_sStudentPruefungNote;
	private String m_sStudentPruefungSemester;
	private boolean m_bStudentPruefungZUVInformiert;
	private boolean m_bStudentPruefungValidiert;
	private boolean m_bStudentPruefungAnmeldung;
	private boolean m_bStudentPruefungGesiegelt;
	private boolean m_bStudentPruefungBestanden;
	private Date m_dStudentPruefungAusstellungsd;
	private String m_sStudentPruefungAussteller;
	private String m_sStudentPruefungRowIP;
	private String m_sStudentPruefungCustom1;
	private String m_sStudentPruefungCustom2;
	private String m_sStudentPruefungCustom3;

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
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXPruefung.lngSdSeminarID</Code>
	 **/
	public long getSdSeminarID(){
		return this.m_lSdSeminarID;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXPruefung.lngSdSeminarID</Code>
	 **/	
	public void setSdSeminarID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lSdSeminarID));
		this.m_lSdSeminarID=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXPruefung.lngSdPruefungsID</Code>
	 **/
	public long getSdPruefungsID(){
		return this.m_lSdPruefungsID;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXPruefung.lngSdPruefungsID</Code>
	 **/	
	public void setSdPruefungsID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lSdPruefungsID));
		this.m_lSdPruefungsID=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXPruefung.strMatrikelnummer</Code>
	 **/
	public String getMatrikelnummer(){
		return this.m_sMatrikelnummer;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXPruefung.strMatrikelnummer</Code>
	 **/	
	public void setMatrikelnummer(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sMatrikelnummer)));
		this.m_sMatrikelnummer=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXPruefung.intStudentPruefungCount</Code>
	 **/
	public int getStudentPruefungCount(){
		return this.m_iStudentPruefungCount;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXPruefung.intStudentPruefungCount</Code>
	 **/	
	public void setStudentPruefungCount(int value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_iStudentPruefungCount));
		this.m_iStudentPruefungCount=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXPruefung.intNoteID</Code>
	 **/
	public int getNoteID(){
		return this.m_iNoteID;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXPruefung.intNoteID</Code>
	 **/	
	public void setNoteID(int value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_iNoteID));
		this.m_iNoteID=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXPruefung.strStudentPruefungNote</Code>
	 **/
	public String getStudentPruefungNote(){
		return this.m_sStudentPruefungNote;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXPruefung.strStudentPruefungNote</Code>
	 **/	
	public void setStudentPruefungNote(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sStudentPruefungNote)));
		this.m_sStudentPruefungNote=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXPruefung.strStudentPruefungSemester</Code>
	 **/
	public String getStudentPruefungSemester(){
		return this.m_sStudentPruefungSemester;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXPruefung.strStudentPruefungSemester</Code>
	 **/	
	public void setStudentPruefungSemester(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sStudentPruefungSemester)));
		this.m_sStudentPruefungSemester=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXPruefung.blnStudentPruefungZUVInformiert</Code>
	 **/
	public boolean getStudentPruefungZUVInformiert(){
		return this.m_bStudentPruefungZUVInformiert;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXPruefung.blnStudentPruefungZUVInformiert</Code>
	 **/	
	public void setStudentPruefungZUVInformiert(boolean value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_bStudentPruefungZUVInformiert));
		this.m_bStudentPruefungZUVInformiert=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXPruefung.blnStudentPruefungValidiert</Code>
	 **/
	public boolean getStudentPruefungValidiert(){
		return this.m_bStudentPruefungValidiert;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXPruefung.blnStudentPruefungValidiert</Code>
	 **/	
	public void setStudentPruefungValidiert(boolean value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_bStudentPruefungValidiert));
		this.m_bStudentPruefungValidiert=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXPruefung.blnStudentPruefungAnmeldung</Code>
	 **/
	public boolean getStudentPruefungAnmeldung(){
		return this.m_bStudentPruefungAnmeldung;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXPruefung.blnStudentPruefungAnmeldung</Code>
	 **/	
	public void setStudentPruefungAnmeldung(boolean value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_bStudentPruefungAnmeldung));
		this.m_bStudentPruefungAnmeldung=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXPruefung.blnStudentPruefungGesiegelt</Code>
	 **/
	public boolean getStudentPruefungGesiegelt(){
		return this.m_bStudentPruefungGesiegelt;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXPruefung.blnStudentPruefungGesiegelt</Code>
	 **/	
	public void setStudentPruefungGesiegelt(boolean value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_bStudentPruefungGesiegelt));
		this.m_bStudentPruefungGesiegelt=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXPruefung.blnStudentPruefungBestanden</Code>
	 **/
	public boolean getStudentPruefungBestanden(){
		return this.m_bStudentPruefungBestanden;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXPruefung.blnStudentPruefungBestanden</Code>
	 **/	
	public void setStudentPruefungBestanden(boolean value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_bStudentPruefungBestanden));
		this.m_bStudentPruefungBestanden=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXPruefung.dtmStudentPruefungAusstellungsd</Code>
	 **/
	public Date getStudentPruefungAusstellungsd(){
		return this.m_dStudentPruefungAusstellungsd;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXPruefung.dtmStudentPruefungAusstellungsd</Code>
	 **/	
	public void setStudentPruefungAusstellungsd(Date value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_dStudentPruefungAusstellungsd)));
		this.m_dStudentPruefungAusstellungsd=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXPruefung.strStudentPruefungAussteller</Code>
	 **/
	public String getStudentPruefungAussteller(){
		return this.m_sStudentPruefungAussteller;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXPruefung.strStudentPruefungAussteller</Code>
	 **/	
	public void setStudentPruefungAussteller(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sStudentPruefungAussteller)));
		this.m_sStudentPruefungAussteller=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXPruefung.strStudentPruefungRowIP</Code>
	 **/
	public String getStudentPruefungRowIP(){
		return this.m_sStudentPruefungRowIP;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXPruefung.strStudentPruefungRowIP</Code>
	 **/	
	public void setStudentPruefungRowIP(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sStudentPruefungRowIP)));
		this.m_sStudentPruefungRowIP=value;
	}
	

	/**
	 * This is used currently to store the date when this row has been extracted to Xml in order to report it to ZUV.
	 * @return This is used currently to store the date when this row has been extracted to Xml in order to report it to ZUV.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXPruefung.strStudentPruefungCustom1</Code>
	 **/
	public String getStudentPruefungCustom1(){
		return this.m_sStudentPruefungCustom1;
	}

	/**
	 * This is used currently to store the date when this row has been extracted to Xml in order to report it to ZUV.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXPruefung.strStudentPruefungCustom1</Code>
	 **/	
	public void setStudentPruefungCustom1(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sStudentPruefungCustom1)));
		this.m_sStudentPruefungCustom1=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXPruefung.strStudentPruefungCustom2</Code>
	 **/
	public String getStudentPruefungCustom2(){
		return this.m_sStudentPruefungCustom2;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXPruefung.strStudentPruefungCustom2</Code>
	 **/	
	public void setStudentPruefungCustom2(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sStudentPruefungCustom2)));
		this.m_sStudentPruefungCustom2=value;
	}
	

	/**
	 * 
	 * @return 
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXPruefung.strStudentPruefungCustom3</Code>
	 **/
	public String getStudentPruefungCustom3(){
		return this.m_sStudentPruefungCustom3;
	}

	/**
	 * 
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdStudentXPruefung.strStudentPruefungCustom3</Code>
	 **/	
	public void setStudentPruefungCustom3(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sStudentPruefungCustom3)));
		this.m_sStudentPruefungCustom3=value;
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
			"<SdPruefungsID>" + m_lSdPruefungsID + "</SdPruefungsID>"  + 
			"<Matrikelnummer>" + m_sMatrikelnummer + "</Matrikelnummer>"  + 
			"<StudentPruefungCount>" + m_iStudentPruefungCount + "</StudentPruefungCount>"  + 
			"<NoteID>" + m_iNoteID + "</NoteID>"  + 
			"<StudentPruefungNote>" + m_sStudentPruefungNote + "</StudentPruefungNote>"  + 
			"<StudentPruefungSemester>" + m_sStudentPruefungSemester + "</StudentPruefungSemester>"  + 
			"<StudentPruefungZUVInformiert>" + m_bStudentPruefungZUVInformiert + "</StudentPruefungZUVInformiert>"  + 
			"<StudentPruefungValidiert>" + m_bStudentPruefungValidiert + "</StudentPruefungValidiert>"  + 
			"<StudentPruefungAnmeldung>" + m_bStudentPruefungAnmeldung + "</StudentPruefungAnmeldung>"  + 
			"<StudentPruefungGesiegelt>" + m_bStudentPruefungGesiegelt + "</StudentPruefungGesiegelt>"  + 
			"<StudentPruefungBestanden>" + m_bStudentPruefungBestanden + "</StudentPruefungBestanden>"  + 
			"<StudentPruefungAusstellungsd>" + m_dStudentPruefungAusstellungsd + "</StudentPruefungAusstellungsd>"  + 
			"<StudentPruefungAussteller>" + m_sStudentPruefungAussteller + "</StudentPruefungAussteller>"  + 
			"<StudentPruefungRowIP>" + m_sStudentPruefungRowIP + "</StudentPruefungRowIP>"  + 
			"<StudentPruefungCustom1>" + m_sStudentPruefungCustom1 + "</StudentPruefungCustom1>"  + 
			"<StudentPruefungCustom2>" + m_sStudentPruefungCustom2 + "</StudentPruefungCustom2>"  + 
			"<StudentPruefungCustom3>" + m_sStudentPruefungCustom3 + "</StudentPruefungCustom3>" ;
	}

////////////////////////////////////////////////////////////////
// 4.   S Q L  U T I L I T I E S
////////////////////////////////////////////////////////////////


	/**
	 * Where-SQL für eindeutigen Index dieses Objekte in der Datenbank (in Tabelle 'tblBdStudentXPruefung.').
	 * @return SQL-String wie z.B. "Id1=? and Id2=?"
	 **/
	private String getSQLWhereClause(){
		return 
			"\"lngSdSeminarID\"=? AND " + 
			"\"lngSdPruefungsID\"=? AND " + 
			"\"strMatrikelnummer\"=? AND " + 
			"\"intStudentPruefungCount\"=?";
	}
	
	/**
	 * @deprecated
	 * Where-SQL für eindeutigen Index dieses Objekte in der Datenbank (in Tabelle 'tblBdStudentXPruefung.').
	 * @return SQL-String wie z.B. "Id1=? and Id2=?"
	 **/
	protected String getSQLWhereClauseOld(){
		return 
			"\"lngSdSeminarID\"=" + m_lSdSeminarID + " AND " + 
			"\"lngSdPruefungsID\"=" + m_lSdPruefungsID + " AND " + 
			"\"strMatrikelnummer\"='" + m_sMatrikelnummer + "' AND " + 
			"\"intStudentPruefungCount\"=" + m_iStudentPruefungCount;
	}

	/**
	 * SQL-Befehl zum Löschen des Objekts in der Datenbank (aus Tabelle 'tblBdStudentXPruefung.')
	 * @return SQL-String zum Löschen des Objekts, z.B. "delete from T1 where (Id1=?);"
	 **/
	private String toDBDeleteString(){
		return "delete from \"tblBdStudentXPruefung\" where ( " + this.getSQLWhereClause() + ");";
	}

	/**
 	 * Setzt die Werte für alle Platzhalter in {@link #getSQLWhereClause()}.
	 * @param ii Offset für die Parameter (inklusive).
	 * @throws SQLException 
	 **/
	private void pokeWhere(int ii, PreparedStatement ps) throws SQLException{
		
		ps.setLong(ii++, m_lSdSeminarID);
		ps.setLong(ii++, m_lSdPruefungsID);
		ps.setString(ii++, m_sMatrikelnummer);
		ps.setInt(ii++, m_iStudentPruefungCount);
	}

	/**
  	 * Füllt das SQL-Statement mit den aktuellen Werten des Objekts
	 * @throws SQLException
	 **/
	private void pokeStatement(PreparedStatement ps) throws SQLException{
		
		ps.setLong(1, m_lSdSeminarID);
		ps.setLong(2, m_lSdPruefungsID);
		ps.setString(3, m_sMatrikelnummer);
		ps.setInt(4, m_iStudentPruefungCount);
		ps.setInt(5, m_iNoteID);
		ps.setString(6, m_sStudentPruefungNote);
		ps.setString(7, m_sStudentPruefungSemester);
		ps.setBoolean(8, m_bStudentPruefungZUVInformiert);
		ps.setBoolean(9, m_bStudentPruefungValidiert);
		ps.setBoolean(10, m_bStudentPruefungAnmeldung);
		ps.setBoolean(11, m_bStudentPruefungGesiegelt);
		ps.setBoolean(12, m_bStudentPruefungBestanden);
		ps.setDate(13, m_dStudentPruefungAusstellungsd);
		ps.setString(14, m_sStudentPruefungAussteller);
		ps.setString(15, m_sStudentPruefungRowIP);
		ps.setString(16, m_sStudentPruefungCustom1);
		ps.setString(17, m_sStudentPruefungCustom2);
		ps.setString(18, m_sStudentPruefungCustom3);

	}

	/**
	 * SQL-Befehl zum Speichern der Objekteigenschaften in die Datenbank (Tabelle 'tblBdStudentXPruefung.')
	 * @return PreparedStatemtent, das nach Aufruf von {@link #pokeStatement(PreparedStatement)} und {@link #execute()} das Update ausführt.
	 **/
	private String toDBUpdateString(){
		return "update \"tblBdStudentXPruefung\" set " +
			"\"lngSdSeminarID\"=?, " +
			"\"lngSdPruefungsID\"=?, " +
			"\"strMatrikelnummer\"=?, " +
			"\"intStudentPruefungCount\"=?, " +
			"\"intNoteID\"=?, " +
			"\"strStudentPruefungNote\"=?, " +
			"\"strStudentPruefungSemester\"=?, " +
			"\"blnStudentPruefungZUVInformiert\"=?, " +
			"\"blnStudentPruefungValidiert\"=?, " +
			"\"blnStudentPruefungAnmeldung\"=?, " +
			"\"blnStudentPruefungGesiegelt\"=?, " +
			"\"blnStudentPruefungBestanden\"=?, " +
			"\"dtmStudentPruefungAusstellungsd\"=?, " +
			"\"strStudentPruefungAussteller\"=?, " +
			"\"strStudentPruefungRowIP\"=?, " +
			"\"strStudentPruefungCustom1\"=?, " +
			"\"strStudentPruefungCustom2\"=?, " +
			"\"strStudentPruefungCustom3\"=?" +
			" where (" + this.getSQLWhereClause() + ");";
	}

	/**
	 * SQL-Befehl zum Hinzufügen des Objekts in die Datenbank (Tabelle 'tblBdStudentXPruefung.')
	 * @return SQL-String "insert into...".
	 **/
	private String toDBAddString(){
		return "insert into \"tblBdStudentXPruefung\" ( " +
			"\"lngSdSeminarID\", \"lngSdPruefungsID\", \"strMatrikelnummer\", \"intStudentPruefungCount\", \"intNoteID\", \"strStudentPruefungNote\", \"strStudentPruefungSemester\", \"blnStudentPruefungZUVInformiert\", \"blnStudentPruefungValidiert\", \"blnStudentPruefungAnmeldung\", \"blnStudentPruefungGesiegelt\", \"blnStudentPruefungBestanden\", \"dtmStudentPruefungAusstellungsd\", \"strStudentPruefungAussteller\", \"strStudentPruefungRowIP\", \"strStudentPruefungCustom1\", \"strStudentPruefungCustom2\", \"strStudentPruefungCustom3\" ) VALUES ( ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
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
	private void init(long lngSdSeminarID, long lngSdPruefungsID, String strMatrikelnummer, int intStudentPruefungCount) throws SQLException, NamingException{

		this.m_lSdSeminarID=lngSdSeminarID;

		this.m_lSdPruefungsID=lngSdPruefungsID;

		this.m_sMatrikelnummer=strMatrikelnummer;

		this.m_iStudentPruefungCount=intStudentPruefungCount;

		
		PreparedStatement pstm = prepareStatement("select * from \"tblBdStudentXPruefung\" where (" + this.getSQLWhereClause() + ");");
		pokeWhere(1,pstm);

		ResultSet rst=pstm.executeQuery();
		if(rst.next()) this.initByRst(rst);
		rst.close();
		this.disconnect();
		rst=null;
	}	
	
	/**
	 * Lade die Objekteigenschaften aus einem ResultSet.
	 * param rst ResultSet mit allen Spalten der Tabelle 'tblBdStudentXPruefung'
	 * @throws SQLException 
	 **/
	protected void initByRst(ResultSet rst) throws SQLException{
		this.m_lSdSeminarID=rst.getLong("lngSdSeminarID");
		this.m_lSdPruefungsID=rst.getLong("lngSdPruefungsID");
		this.m_sMatrikelnummer=rst.getString("strMatrikelnummer");
		this.m_iStudentPruefungCount=rst.getInt("intStudentPruefungCount");
		this.m_iNoteID=rst.getInt("intNoteID");
		this.m_sStudentPruefungNote=rst.getString("strStudentPruefungNote");
		this.m_sStudentPruefungSemester=rst.getString("strStudentPruefungSemester");
		this.m_bStudentPruefungZUVInformiert=rst.getBoolean("blnStudentPruefungZUVInformiert");
		this.m_bStudentPruefungValidiert=rst.getBoolean("blnStudentPruefungValidiert");
		this.m_bStudentPruefungAnmeldung=rst.getBoolean("blnStudentPruefungAnmeldung");
		this.m_bStudentPruefungGesiegelt=rst.getBoolean("blnStudentPruefungGesiegelt");
		this.m_bStudentPruefungBestanden=rst.getBoolean("blnStudentPruefungBestanden");
		this.m_dStudentPruefungAusstellungsd=rst.getDate("dtmStudentPruefungAusstellungsd");
		this.m_sStudentPruefungAussteller=rst.getString("strStudentPruefungAussteller");
		this.m_sStudentPruefungRowIP=rst.getString("strStudentPruefungRowIP");
		this.m_sStudentPruefungCustom1=rst.getString("strStudentPruefungCustom1");
		this.m_sStudentPruefungCustom2=rst.getString("strStudentPruefungCustom2");
		this.m_sStudentPruefungCustom3=rst.getString("strStudentPruefungCustom3");	
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
		pokeWhere(19,pstm);
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
	public StudentXPruefung(){}	
	
	/**
	 * Konstruktor mit Indexwerten (eindeutig) der assoziierten Datenbanktabelle
	 * @throws NamingException 
	 * @throws SQLException 
	 **/
	public StudentXPruefung(long lngSdSeminarID, long lngSdPruefungsID, String strMatrikelnummer, int intStudentPruefungCount) throws SQLException, NamingException{
		this.init(lngSdSeminarID, lngSdPruefungsID, strMatrikelnummer, intStudentPruefungCount);
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
	public StudentXPruefung(ResultSet rst) throws SQLException{
		this.initByRst(rst);
		this.m_bIsDirty = false;
	}

  }//Klassenende
