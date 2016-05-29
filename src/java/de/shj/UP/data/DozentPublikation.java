
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
 *  Module: Bibliography
 *  
 *  ALLGEMEINE INFORMATION ZU DEN OBJEKTEN DIESES PACKAGES:
 *  Diese Klasse kapselt die Tabelle 'DozentPublikation' in 
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
public class DozentPublikation extends shjCore{

	private static final long serialVersionUID = 7L;

////////////////////////////////////////////////////////////////
// 1.   P R I V A T E   D E K L A R A T I O N E N
////////////////////////////////////////////////////////////////
	
	private boolean m_bIsDirty = false;

	private long m_lSdSeminarID;
	private long m_lDozentID;
	private long m_lDozentPublikationID;
	private String m_sDPAutorName;
	private String m_sDPAutorVorname;
	private String m_sDPVerfassertyp;
	private String m_sDPTitel;
	private String m_sDPOrt;
	private String m_sDPVerlag;
	private int m_iDPJahr;
	private String m_sDPBuchtitel;
	private String m_sDPZeitschrift;
	private String m_sDPHeft;
	private String m_sDPBand;
	private String m_sDPSeitenangaben;
	private String m_sDPReihe;
	private String m_sDPReiheHgName;
	private String m_sDPReiheHgVorname;
	private String m_sDPHgName;
	private String m_sDPHgVorname;
	private String m_sDPAuflage;
	private String m_sDPArt;
	private String m_sDPIndex;
	private String m_sDPAlternativeAusgabe;
	private String m_sDPBemerkung;
	private String m_sDPURL;
	private String m_sDPISBN;
	private String m_sDPISSN;
	private String m_sDPWeiterePersonen;

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
	 * Id of Seminar that this teacher belongs to (in this context).
	 * @return Id of Seminar that this teacher belongs to (in this context).
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdDozentPublikation.lngSdSeminarID</Code>
	 **/
	public long getSdSeminarID(){
		return this.m_lSdSeminarID;
	}

	/**
	 * Id of Seminar that this teacher belongs to (in this context).
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdDozentPublikation.lngSdSeminarID</Code>
	 **/	
	public void setSdSeminarID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lSdSeminarID));
		this.m_lSdSeminarID=value;
	}
	

	/**
	 * Id of teacher.
	 * @return Id of teacher.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdDozentPublikation.lngDozentID</Code>
	 **/
	public long getDozentID(){
		return this.m_lDozentID;
	}

	/**
	 * Id of teacher.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdDozentPublikation.lngDozentID</Code>
	 **/	
	public void setDozentID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lDozentID));
		this.m_lDozentID=value;
	}
	

	/**
	 * Counter or Id of publication.
	 * @return Counter or Id of publication.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdDozentPublikation.lngDozentPublikationID</Code>
	 **/
	public long getDozentPublikationID(){
		return this.m_lDozentPublikationID;
	}

	/**
	 * Counter or Id of publication.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdDozentPublikation.lngDozentPublikationID</Code>
	 **/	
	public void setDozentPublikationID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lDozentPublikationID));
		this.m_lDozentPublikationID=value;
	}
	

	/**
	 * Name of author or editor of the publication
	 * @return Name of author or editor of the publication
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdDozentPublikation.strDPAutorName</Code>
	 **/
	public String getDPAutorName(){
		return this.m_sDPAutorName;
	}

	/**
	 * Name of author or editor of the publication
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdDozentPublikation.strDPAutorName</Code>
	 **/	
	public void setDPAutorName(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDPAutorName)));
		this.m_sDPAutorName=value;
	}
	

	/**
	 * First name of author or editor of this publication.
	 * @return First name of author or editor of this publication.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdDozentPublikation.strDPAutorVorname</Code>
	 **/
	public String getDPAutorVorname(){
		return this.m_sDPAutorVorname;
	}

	/**
	 * First name of author or editor of this publication.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdDozentPublikation.strDPAutorVorname</Code>
	 **/	
	public void setDPAutorVorname(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDPAutorVorname)));
		this.m_sDPAutorVorname=value;
	}
	

	/**
	 * Type: Author or editor?
	 * @return Type: Author or editor?
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdDozentPublikation.strDPVerfassertyp</Code>
	 **/
	public String getDPVerfassertyp(){
		return this.m_sDPVerfassertyp;
	}

	/**
	 * Type: Author or editor?
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdDozentPublikation.strDPVerfassertyp</Code>
	 **/	
	public void setDPVerfassertyp(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDPVerfassertyp)));
		this.m_sDPVerfassertyp=value;
	}
	

	/**
	 * Title of the publication.
	 * @return Title of the publication.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdDozentPublikation.strDPTitel</Code>
	 **/
	public String getDPTitel(){
		return this.m_sDPTitel;
	}

	/**
	 * Title of the publication.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdDozentPublikation.strDPTitel</Code>
	 **/	
	public void setDPTitel(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDPTitel)));
		this.m_sDPTitel=value;
	}
	

	/**
	 * Place of publisher.
	 * @return Place of publisher.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdDozentPublikation.strDPOrt</Code>
	 **/
	public String getDPOrt(){
		return this.m_sDPOrt;
	}

	/**
	 * Place of publisher.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdDozentPublikation.strDPOrt</Code>
	 **/	
	public void setDPOrt(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDPOrt)));
		this.m_sDPOrt=value;
	}
	

	/**
	 * Publishing company (name).
	 * @return Publishing company (name).
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdDozentPublikation.strDPVerlag</Code>
	 **/
	public String getDPVerlag(){
		return this.m_sDPVerlag;
	}

	/**
	 * Publishing company (name).
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdDozentPublikation.strDPVerlag</Code>
	 **/	
	public void setDPVerlag(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDPVerlag)));
		this.m_sDPVerlag=value;
	}
	

	/**
	 * Year of publication.
	 * @return Year of publication.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdDozentPublikation.intDPJahr</Code>
	 **/
	public int getDPJahr(){
		return this.m_iDPJahr;
	}

	/**
	 * Year of publication.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdDozentPublikation.intDPJahr</Code>
	 **/	
	public void setDPJahr(int value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_iDPJahr));
		this.m_iDPJahr=value;
	}
	

	/**
	 * Title of book (if it is a book).
	 * @return Title of book (if it is a book).
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdDozentPublikation.strDPBuchtitel</Code>
	 **/
	public String getDPBuchtitel(){
		return this.m_sDPBuchtitel;
	}

	/**
	 * Title of book (if it is a book).
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdDozentPublikation.strDPBuchtitel</Code>
	 **/	
	public void setDPBuchtitel(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDPBuchtitel)));
		this.m_sDPBuchtitel=value;
	}
	

	/**
	 * Title of journal (if it is a journal)
	 * @return Title of journal (if it is a journal)
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdDozentPublikation.strDPZeitschrift</Code>
	 **/
	public String getDPZeitschrift(){
		return this.m_sDPZeitschrift;
	}

	/**
	 * Title of journal (if it is a journal)
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdDozentPublikation.strDPZeitschrift</Code>
	 **/	
	public void setDPZeitschrift(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDPZeitschrift)));
		this.m_sDPZeitschrift=value;
	}
	

	/**
	 * Specification of journal number.
	 * @return Specification of journal number.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdDozentPublikation.strDPHeft</Code>
	 **/
	public String getDPHeft(){
		return this.m_sDPHeft;
	}

	/**
	 * Specification of journal number.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdDozentPublikation.strDPHeft</Code>
	 **/	
	public void setDPHeft(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDPHeft)));
		this.m_sDPHeft=value;
	}
	

	/**
	 * volume
	 * @return volume
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdDozentPublikation.strDPBand</Code>
	 **/
	public String getDPBand(){
		return this.m_sDPBand;
	}

	/**
	 * volume
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdDozentPublikation.strDPBand</Code>
	 **/	
	public void setDPBand(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDPBand)));
		this.m_sDPBand=value;
	}
	

	/**
	 * Page indications (in case of article in book or journal, e.g. 123-129.)
	 * @return Page indications (in case of article in book or journal, e.g. 123-129.)
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdDozentPublikation.strDPSeitenangaben</Code>
	 **/
	public String getDPSeitenangaben(){
		return this.m_sDPSeitenangaben;
	}

	/**
	 * Page indications (in case of article in book or journal, e.g. 123-129.)
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdDozentPublikation.strDPSeitenangaben</Code>
	 **/	
	public void setDPSeitenangaben(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDPSeitenangaben)));
		this.m_sDPSeitenangaben=value;
	}
	

	/**
	 * Name of the series (of books or publications, if any).
	 * @return Name of the series (of books or publications, if any).
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdDozentPublikation.strDPReihe</Code>
	 **/
	public String getDPReihe(){
		return this.m_sDPReihe;
	}

	/**
	 * Name of the series (of books or publications, if any).
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdDozentPublikation.strDPReihe</Code>
	 **/	
	public void setDPReihe(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDPReihe)));
		this.m_sDPReihe=value;
	}
	

	/**
	 * Name of editor of this series.
	 * @return Name of editor of this series.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdDozentPublikation.strDPReiheHgName</Code>
	 **/
	public String getDPReiheHgName(){
		return this.m_sDPReiheHgName;
	}

	/**
	 * Name of editor of this series.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdDozentPublikation.strDPReiheHgName</Code>
	 **/	
	public void setDPReiheHgName(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDPReiheHgName)));
		this.m_sDPReiheHgName=value;
	}
	

	/**
	 * First name of editor of this series.
	 * @return First name of editor of this series.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdDozentPublikation.strDPReiheHgVorname</Code>
	 **/
	public String getDPReiheHgVorname(){
		return this.m_sDPReiheHgVorname;
	}

	/**
	 * First name of editor of this series.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdDozentPublikation.strDPReiheHgVorname</Code>
	 **/	
	public void setDPReiheHgVorname(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDPReiheHgVorname)));
		this.m_sDPReiheHgVorname=value;
	}
	

	/**
	 * Name of editor.
	 * @return Name of editor.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdDozentPublikation.strDPHgName</Code>
	 **/
	public String getDPHgName(){
		return this.m_sDPHgName;
	}

	/**
	 * Name of editor.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdDozentPublikation.strDPHgName</Code>
	 **/	
	public void setDPHgName(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDPHgName)));
		this.m_sDPHgName=value;
	}
	

	/**
	 * First name of editor.
	 * @return First name of editor.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdDozentPublikation.strDPHgVorname</Code>
	 **/
	public String getDPHgVorname(){
		return this.m_sDPHgVorname;
	}

	/**
	 * First name of editor.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdDozentPublikation.strDPHgVorname</Code>
	 **/	
	public void setDPHgVorname(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDPHgVorname)));
		this.m_sDPHgVorname=value;
	}
	

	/**
	 * Print
	 * @return Print
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdDozentPublikation.strDPAuflage</Code>
	 **/
	public String getDPAuflage(){
		return this.m_sDPAuflage;
	}

	/**
	 * Print
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdDozentPublikation.strDPAuflage</Code>
	 **/	
	public void setDPAuflage(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDPAuflage)));
		this.m_sDPAuflage=value;
	}
	

	/**
	 * Kind of publication (monograph, article etc. Probably specific to seminar.)
	 * @return Kind of publication (monograph, article etc. Probably specific to seminar.)
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdDozentPublikation.strDPArt</Code>
	 **/
	public String getDPArt(){
		return this.m_sDPArt;
	}

	/**
	 * Kind of publication (monograph, article etc. Probably specific to seminar.)
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdDozentPublikation.strDPArt</Code>
	 **/	
	public void setDPArt(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDPArt)));
		this.m_sDPArt=value;
	}
	

	/**
	 * Loose list of keywords that should index this publication.
	 * @return Loose list of keywords that should index this publication.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdDozentPublikation.strDPIndex</Code>
	 **/
	public String getDPIndex(){
		return this.m_sDPIndex;
	}

	/**
	 * Loose list of keywords that should index this publication.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdDozentPublikation.strDPIndex</Code>
	 **/	
	public void setDPIndex(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDPIndex)));
		this.m_sDPIndex=value;
	}
	

	/**
	 * Alternative editor or publisher.
	 * @return Alternative editor or publisher.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdDozentPublikation.strDPAlternativeAusgabe</Code>
	 **/
	public String getDPAlternativeAusgabe(){
		return this.m_sDPAlternativeAusgabe;
	}

	/**
	 * Alternative editor or publisher.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdDozentPublikation.strDPAlternativeAusgabe</Code>
	 **/	
	public void setDPAlternativeAusgabe(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDPAlternativeAusgabe)));
		this.m_sDPAlternativeAusgabe=value;
	}
	

	/**
	 * Remark
	 * @return Remark
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdDozentPublikation.strDPBemerkung</Code>
	 **/
	public String getDPBemerkung(){
		return this.m_sDPBemerkung;
	}

	/**
	 * Remark
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdDozentPublikation.strDPBemerkung</Code>
	 **/	
	public void setDPBemerkung(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDPBemerkung)));
		this.m_sDPBemerkung=value;
	}
	

	/**
	 * Url for this publication.
	 * @return Url for this publication.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdDozentPublikation.strDPURL</Code>
	 **/
	public String getDPURL(){
		return this.m_sDPURL;
	}

	/**
	 * Url for this publication.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdDozentPublikation.strDPURL</Code>
	 **/	
	public void setDPURL(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDPURL)));
		this.m_sDPURL=value;
	}
	

	/**
	 * ISBN of this publication.
	 * @return ISBN of this publication.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdDozentPublikation.strDPISBN</Code>
	 **/
	public String getDPISBN(){
		return this.m_sDPISBN;
	}

	/**
	 * ISBN of this publication.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdDozentPublikation.strDPISBN</Code>
	 **/	
	public void setDPISBN(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDPISBN)));
		this.m_sDPISBN=value;
	}
	

	/**
	 * Issn of this publication.
	 * @return Issn of this publication.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdDozentPublikation.strDPISSN</Code>
	 **/
	public String getDPISSN(){
		return this.m_sDPISSN;
	}

	/**
	 * Issn of this publication.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdDozentPublikation.strDPISSN</Code>
	 **/	
	public void setDPISSN(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDPISSN)));
		this.m_sDPISSN=value;
	}
	

	/**
	 * More persons involved (co-authors, co-editors etc.)
	 * @return More persons involved (co-authors, co-editors etc.)
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblBdDozentPublikation.strDPWeiterePersonen</Code>
	 **/
	public String getDPWeiterePersonen(){
		return this.m_sDPWeiterePersonen;
	}

	/**
	 * More persons involved (co-authors, co-editors etc.)
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblBdDozentPublikation.strDPWeiterePersonen</Code>
	 **/	
	public void setDPWeiterePersonen(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDPWeiterePersonen)));
		this.m_sDPWeiterePersonen=value;
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
			"<DozentID>" + m_lDozentID + "</DozentID>"  + 
			"<DozentPublikationID>" + m_lDozentPublikationID + "</DozentPublikationID>"  + 
			"<DPAutorName>" + m_sDPAutorName + "</DPAutorName>"  + 
			"<DPAutorVorname>" + m_sDPAutorVorname + "</DPAutorVorname>"  + 
			"<DPVerfassertyp>" + m_sDPVerfassertyp + "</DPVerfassertyp>"  + 
			"<DPTitel>" + m_sDPTitel + "</DPTitel>"  + 
			"<DPOrt>" + m_sDPOrt + "</DPOrt>"  + 
			"<DPVerlag>" + m_sDPVerlag + "</DPVerlag>"  + 
			"<DPJahr>" + m_iDPJahr + "</DPJahr>"  + 
			"<DPBuchtitel>" + m_sDPBuchtitel + "</DPBuchtitel>"  + 
			"<DPZeitschrift>" + m_sDPZeitschrift + "</DPZeitschrift>"  + 
			"<DPHeft>" + m_sDPHeft + "</DPHeft>"  + 
			"<DPBand>" + m_sDPBand + "</DPBand>"  + 
			"<DPSeitenangaben>" + m_sDPSeitenangaben + "</DPSeitenangaben>"  + 
			"<DPReihe>" + m_sDPReihe + "</DPReihe>"  + 
			"<DPReiheHgName>" + m_sDPReiheHgName + "</DPReiheHgName>"  + 
			"<DPReiheHgVorname>" + m_sDPReiheHgVorname + "</DPReiheHgVorname>"  + 
			"<DPHgName>" + m_sDPHgName + "</DPHgName>"  + 
			"<DPHgVorname>" + m_sDPHgVorname + "</DPHgVorname>"  + 
			"<DPAuflage>" + m_sDPAuflage + "</DPAuflage>"  + 
			"<DPArt>" + m_sDPArt + "</DPArt>"  + 
			"<DPIndex>" + m_sDPIndex + "</DPIndex>"  + 
			"<DPAlternativeAusgabe>" + m_sDPAlternativeAusgabe + "</DPAlternativeAusgabe>"  + 
			"<DPBemerkung>" + m_sDPBemerkung + "</DPBemerkung>"  + 
			"<DPURL>" + m_sDPURL + "</DPURL>"  + 
			"<DPISBN>" + m_sDPISBN + "</DPISBN>"  + 
			"<DPISSN>" + m_sDPISSN + "</DPISSN>"  + 
			"<DPWeiterePersonen>" + m_sDPWeiterePersonen + "</DPWeiterePersonen>" ;
	}

////////////////////////////////////////////////////////////////
// 4.   S Q L  U T I L I T I E S
////////////////////////////////////////////////////////////////


	/**
	 * Where-SQL für eindeutigen Index dieses Objekte in der Datenbank (in Tabelle 'tblBdDozentPublikation.').
	 * @return SQL-String wie z.B. "Id1=? and Id2=?"
	 **/
	private String getSQLWhereClause(){
		return 
			"\"lngSdSeminarID\"=? AND " + 
			"\"lngDozentID\"=? AND " + 
			"\"lngDozentPublikationID\"=?";
	}

	/**
	 * SQL-Befehl zum Löschen des Objekts in der Datenbank (aus Tabelle 'tblBdDozentPublikation.')
	 * @return SQL-String zum Löschen des Objekts, z.B. "delete from T1 where (Id1=?);"
	 **/
	private String toDBDeleteString(){
		return "delete from \"tblBdDozentPublikation\" where ( " + this.getSQLWhereClause() + ");";
	}

	/**
 	 * Setzt die Werte für alle Platzhalter in {@link #getSQLWhereClause()}.
	 * @param ii Offset für die Parameter (inklusive).
	 * @throws SQLException 
	 **/
	private void pokeWhere(int ii, PreparedStatement ps) throws SQLException{
		
		ps.setLong(ii++, m_lSdSeminarID);
		ps.setLong(ii++, m_lDozentID);
		ps.setLong(ii++, m_lDozentPublikationID);
	}

	/**
  	 * Füllt das SQL-Statement mit den aktuellen Werten des Objekts
	 * @throws SQLException
	 **/
	private void pokeStatement(PreparedStatement ps) throws SQLException{
		
		ps.setLong(1, m_lSdSeminarID);
		ps.setLong(2, m_lDozentID);
		ps.setLong(3, m_lDozentPublikationID);
		ps.setString(4, m_sDPAutorName);
		ps.setString(5, m_sDPAutorVorname);
		ps.setString(6, m_sDPVerfassertyp);
		ps.setString(7, m_sDPTitel);
		ps.setString(8, m_sDPOrt);
		ps.setString(9, m_sDPVerlag);
		ps.setInt(10, m_iDPJahr);
		ps.setString(11, m_sDPBuchtitel);
		ps.setString(12, m_sDPZeitschrift);
		ps.setString(13, m_sDPHeft);
		ps.setString(14, m_sDPBand);
		ps.setString(15, m_sDPSeitenangaben);
		ps.setString(16, m_sDPReihe);
		ps.setString(17, m_sDPReiheHgName);
		ps.setString(18, m_sDPReiheHgVorname);
		ps.setString(19, m_sDPHgName);
		ps.setString(20, m_sDPHgVorname);
		ps.setString(21, m_sDPAuflage);
		ps.setString(22, m_sDPArt);
		ps.setString(23, m_sDPIndex);
		ps.setString(24, m_sDPAlternativeAusgabe);
		ps.setString(25, m_sDPBemerkung);
		ps.setString(26, m_sDPURL);
		ps.setString(27, m_sDPISBN);
		ps.setString(28, m_sDPISSN);
		ps.setString(29, m_sDPWeiterePersonen);

	}

	/**
	 * SQL-Befehl zum Speichern der Objekteigenschaften in die Datenbank (Tabelle 'tblBdDozentPublikation.')
	 * @return PreparedStatemtent, das nach Aufruf von {@link #pokeStatement(PreparedStatement)} und {@link #execute()} das Update ausführt.
	 **/
	private String toDBUpdateString(){
		return "update \"tblBdDozentPublikation\" set " +
			"\"lngSdSeminarID\"=?, " +
			"\"lngDozentID\"=?, " +
			"\"lngDozentPublikationID\"=?, " +
			"\"strDPAutorName\"=?, " +
			"\"strDPAutorVorname\"=?, " +
			"\"strDPVerfassertyp\"=?, " +
			"\"strDPTitel\"=?, " +
			"\"strDPOrt\"=?, " +
			"\"strDPVerlag\"=?, " +
			"\"intDPJahr\"=?, " +
			"\"strDPBuchtitel\"=?, " +
			"\"strDPZeitschrift\"=?, " +
			"\"strDPHeft\"=?, " +
			"\"strDPBand\"=?, " +
			"\"strDPSeitenangaben\"=?, " +
			"\"strDPReihe\"=?, " +
			"\"strDPReiheHgName\"=?, " +
			"\"strDPReiheHgVorname\"=?, " +
			"\"strDPHgName\"=?, " +
			"\"strDPHgVorname\"=?, " +
			"\"strDPAuflage\"=?, " +
			"\"strDPArt\"=?, " +
			"\"strDPIndex\"=?, " +
			"\"strDPAlternativeAusgabe\"=?, " +
			"\"strDPBemerkung\"=?, " +
			"\"strDPURL\"=?, " +
			"\"strDPISBN\"=?, " +
			"\"strDPISSN\"=?, " +
			"\"strDPWeiterePersonen\"=?" +
			" where (" + this.getSQLWhereClause() + ");";
	}

	/**
	 * SQL-Befehl zum Hinzufügen des Objekts in die Datenbank (Tabelle 'tblBdDozentPublikation.')
	 * @return SQL-String "insert into...".
	 **/
	private String toDBAddString(){
		return "insert into \"tblBdDozentPublikation\" ( " +
			"\"lngSdSeminarID\", \"lngDozentID\", \"lngDozentPublikationID\", \"strDPAutorName\", \"strDPAutorVorname\", \"strDPVerfassertyp\", \"strDPTitel\", \"strDPOrt\", \"strDPVerlag\", \"intDPJahr\", \"strDPBuchtitel\", \"strDPZeitschrift\", \"strDPHeft\", \"strDPBand\", \"strDPSeitenangaben\", \"strDPReihe\", \"strDPReiheHgName\", \"strDPReiheHgVorname\", \"strDPHgName\", \"strDPHgVorname\", \"strDPAuflage\", \"strDPArt\", \"strDPIndex\", \"strDPAlternativeAusgabe\", \"strDPBemerkung\", \"strDPURL\", \"strDPISBN\", \"strDPISSN\", \"strDPWeiterePersonen\" ) VALUES ( ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
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
	private void init(long lngSdSeminarID, long lngDozentID, long lngDozentPublikationID) throws SQLException, NamingException{

		this.m_lSdSeminarID=lngSdSeminarID;

		this.m_lDozentID=lngDozentID;

		this.m_lDozentPublikationID=lngDozentPublikationID;

		
		PreparedStatement pstm = prepareStatement("select * from \"tblBdDozentPublikation\" where (" + this.getSQLWhereClause() + ");");
		pokeWhere(1,pstm);

		ResultSet rst=pstm.executeQuery();
		if(rst.next()) this.initByRst(rst);
		rst.close();
		this.disconnect();
		rst=null;
	}	
	
	/**
	 * Lade die Objekteigenschaften aus einem ResultSet.
	 * param rst ResultSet mit allen Spalten der Tabelle 'tblBdDozentPublikation'
	 * @throws SQLException 
	 **/
	protected void initByRst(ResultSet rst) throws SQLException{
		this.m_lSdSeminarID=rst.getLong("lngSdSeminarID");
		this.m_lDozentID=rst.getLong("lngDozentID");
		this.m_lDozentPublikationID=rst.getLong("lngDozentPublikationID");
		this.m_sDPAutorName=rst.getString("strDPAutorName");
		this.m_sDPAutorVorname=rst.getString("strDPAutorVorname");
		this.m_sDPVerfassertyp=rst.getString("strDPVerfassertyp");
		this.m_sDPTitel=rst.getString("strDPTitel");
		this.m_sDPOrt=rst.getString("strDPOrt");
		this.m_sDPVerlag=rst.getString("strDPVerlag");
		this.m_iDPJahr=rst.getInt("intDPJahr");
		this.m_sDPBuchtitel=rst.getString("strDPBuchtitel");
		this.m_sDPZeitschrift=rst.getString("strDPZeitschrift");
		this.m_sDPHeft=rst.getString("strDPHeft");
		this.m_sDPBand=rst.getString("strDPBand");
		this.m_sDPSeitenangaben=rst.getString("strDPSeitenangaben");
		this.m_sDPReihe=rst.getString("strDPReihe");
		this.m_sDPReiheHgName=rst.getString("strDPReiheHgName");
		this.m_sDPReiheHgVorname=rst.getString("strDPReiheHgVorname");
		this.m_sDPHgName=rst.getString("strDPHgName");
		this.m_sDPHgVorname=rst.getString("strDPHgVorname");
		this.m_sDPAuflage=rst.getString("strDPAuflage");
		this.m_sDPArt=rst.getString("strDPArt");
		this.m_sDPIndex=rst.getString("strDPIndex");
		this.m_sDPAlternativeAusgabe=rst.getString("strDPAlternativeAusgabe");
		this.m_sDPBemerkung=rst.getString("strDPBemerkung");
		this.m_sDPURL=rst.getString("strDPURL");
		this.m_sDPISBN=rst.getString("strDPISBN");
		this.m_sDPISSN=rst.getString("strDPISSN");
		this.m_sDPWeiterePersonen=rst.getString("strDPWeiterePersonen");	
	}	
	
	/**
	 * Lade die Objekteigenschaften aus einer XML-Node.
	 * param node XML-Node mit allen Eigenschaften als Tags.
	 * @throws ParseException (Datum muss im ISO-Format yyyy-MM-dd übergeben werden).
	 **/
	private void initByNode(Node node) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		this.m_lSdSeminarID=Long.parseLong(shjNodeValue(node, "SdSeminarID"));
		this.m_lDozentID=Long.parseLong(shjNodeValue(node, "DozentID"));
		this.m_lDozentPublikationID=Long.parseLong(shjNodeValue(node, "DozentPublikationID"));
		this.m_sDPAutorName=(shjNodeValue(node, "DPAutorName"));
		this.m_sDPAutorVorname=(shjNodeValue(node, "DPAutorVorname"));
		this.m_sDPVerfassertyp=(shjNodeValue(node, "DPVerfassertyp"));
		this.m_sDPTitel=(shjNodeValue(node, "DPTitel"));
		this.m_sDPOrt=(shjNodeValue(node, "DPOrt"));
		this.m_sDPVerlag=(shjNodeValue(node, "DPVerlag"));
		this.m_iDPJahr=Integer.parseInt(shjNodeValue(node, "DPJahr"));
		this.m_sDPBuchtitel=(shjNodeValue(node, "DPBuchtitel"));
		this.m_sDPZeitschrift=(shjNodeValue(node, "DPZeitschrift"));
		this.m_sDPHeft=(shjNodeValue(node, "DPHeft"));
		this.m_sDPBand=(shjNodeValue(node, "DPBand"));
		this.m_sDPSeitenangaben=(shjNodeValue(node, "DPSeitenangaben"));
		this.m_sDPReihe=(shjNodeValue(node, "DPReihe"));
		this.m_sDPReiheHgName=(shjNodeValue(node, "DPReiheHgName"));
		this.m_sDPReiheHgVorname=(shjNodeValue(node, "DPReiheHgVorname"));
		this.m_sDPHgName=(shjNodeValue(node, "DPHgName"));
		this.m_sDPHgVorname=(shjNodeValue(node, "DPHgVorname"));
		this.m_sDPAuflage=(shjNodeValue(node, "DPAuflage"));
		this.m_sDPArt=(shjNodeValue(node, "DPArt"));
		this.m_sDPIndex=(shjNodeValue(node, "DPIndex"));
		this.m_sDPAlternativeAusgabe=(shjNodeValue(node, "DPAlternativeAusgabe"));
		this.m_sDPBemerkung=(shjNodeValue(node, "DPBemerkung"));
		this.m_sDPURL=(shjNodeValue(node, "DPURL"));
		this.m_sDPISBN=(shjNodeValue(node, "DPISBN"));
		this.m_sDPISSN=(shjNodeValue(node, "DPISSN"));
		this.m_sDPWeiterePersonen=(shjNodeValue(node, "DPWeiterePersonen"));
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
		pokeWhere(30,pstm);
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
	public DozentPublikation(){}	
	
	/**
	 * Konstruktor mit Indexwerten (eindeutig) der assoziierten Datenbanktabelle
	 * @throws NamingException 
	 * @throws SQLException 
	 **/
	public DozentPublikation(long lngSdSeminarID, long lngDozentID, long lngDozentPublikationID) throws SQLException, NamingException{
		this.init(lngSdSeminarID, lngDozentID, lngDozentPublikationID);
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
	public DozentPublikation(ResultSet rst) throws SQLException{
		this.initByRst(rst);
		this.m_bIsDirty = false;
	}

	/**
	 * Konstruktor per XML-Darstellung des Objekts.
	 * @throws ParseException, if a date can't be read.
	 **/
	public DozentPublikation(Node node) throws ParseException{
		this.initByNode(node);
		this.m_bIsDirty = false;
	}

  }//Klassenende
