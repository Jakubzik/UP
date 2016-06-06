
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

import de.shj.UP.util.PasswordHash;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import javax.naming.NamingException;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import org.w3c.dom.Node; 

/**
 *  Modules: SignUp, Kvv, Exam, Gvp, Newsticker, Appointments
Teacher, or, more generally, member of staff.
Please _note_ that if a teacher teaches at two institutes, there are two records of her/him in the datamodel.
 *  
 *  ALLGEMEINE INFORMATION ZU DEN OBJEKTEN DIESES PACKAGES:
 *  Diese Klasse kapselt die Tabelle 'Dozent' in 
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
public class Dozent extends shjCore{

	private static final long serialVersionUID = 7L;

////////////////////////////////////////////////////////////////
// 1.   P R I V A T E   D E K L A R A T I O N E N
////////////////////////////////////////////////////////////////
	
	private boolean m_bIsDirty = false;

	private long m_lSdSeminarID;
	private long m_lDozentID;
	private String m_sDozentUnivISID;
	private String m_sDozentPasswort;
	private String m_sDozentTitel;
	private String m_sDozentVorname;
	private String m_sDozentNachname;
	private Date m_dDozentGebdatum;
	private String m_sDozentStellennr;
	private String m_sDozentTelefonPrivat;
	private String m_sDozentStrasse;
	private String m_sDozentPLZ;
	private String m_sDozentOrt;
	private String m_sDozentDienstendeMonat;
	private String m_sDozentDienstendeJahr;
	private String m_sDozentOD;
	private String m_sDozentVertragsart;
	private String m_sDozentBank;
	private String m_sDozentKto;
	private String m_sDozentBLZ;
	private String m_sDozentSprechstundeTag;
	private Date m_dDozentSprechstundeDatum;
	private String m_tDozentSprechstundeZeitVon;
	private String m_tDozentSprechstundeZeitBis;
	private String m_sDozentSprechstunde;
	private String m_sDozentBau;
	private String m_sDozentZimmer;
	private String m_sDozentBereich;
	private String m_sDozentTelefon;
	private String m_sDozentEmail;
	private String m_sDozentHomepage;
	private boolean m_bDozentLehrend;
	private String m_sDozentBemerkung;
	private String m_sDozentAnrede;
	private String m_sDozentInteressen;
	private String m_tSprechstundeLastChange;
	private String m_sDozentPostfach;
	private int m_iDozentAccessLevel;
	private String m_sDozentIP;
	private long m_lDozentSessionID;
	private String m_tDozentLastAction;
	private String m_sDozentCertSubjectDN;
	private String m_sDozentCertIssuerDN;
	private String m_sDozentCertSerialID;
	private boolean m_bDozentCertValidated;
	private boolean m_bDozentCertRevoked;
	private boolean m_bDozentExtern;
        private String m_sDozentHomepageOptions;

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
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.lngSdSeminarID</Code>
	 **/
	public long getSdSeminarID(){
		return this.m_lSdSeminarID;
	}

	/**
	 * Seminar
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.lngSdSeminarID</Code>
	 **/	
	public void setSdSeminarID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lSdSeminarID));
		this.m_lSdSeminarID=value;
	}
	

	/**
	 * Id of teacher (at this institute. Teachers must be registeres separately at each institute where they teach. Thus they have differend Ids there).
	 * @return Id of teacher (at this institute. Teachers must be registeres separately at each institute where they teach. Thus they have differend Ids there).
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.lngDozentID</Code>
	 **/
	public long getDozentID(){
		return this.m_lDozentID;
	}

	/**
	 * Id of teacher (at this institute. Teachers must be registeres separately at each institute where they teach. Thus they have differend Ids there).
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.lngDozentID</Code>
	 **/	
	public void setDozentID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lDozentID));
		this.m_lDozentID=value;
	}
	

	/**
	 * UnivIS Id of this teacher for this seminar. (Set on import).
	 * @return UnivIS Id of this teacher for this seminar. (Set on import).
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentUnivISID</Code>
	 **/
	public String getDozentUnivISID(){
		return this.m_sDozentUnivISID;
	}

	/**
	 * UnivIS Id of this teacher for this seminar. (Set on import).
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentUnivISID</Code>
	 **/	
	public void setDozentUnivISID(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDozentUnivISID)));
		this.m_sDozentUnivISID=value;
	}
        
	/**
	 * JSON Value of Homepage options (new 2013)
	 * @return JSON Value of Homepage options (new 2013)
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentHomepageOptions</Code>
	 **/
	public String getDozentHomepageOptions(){
		return this.m_sDozentHomepageOptions;
	}

	/**
	 * JSON Value of Homepage options (new 2013)
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentHomepageOptions</Code>
	 **/	
	public void setDozentHomepageOptions(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDozentHomepageOptions)));
		this.m_sDozentHomepageOptions=value;
	}
	

	/**
	 * This teacher"s password to access this seminar"s SignUp installation.
	 * @return This teacher"s password to access this seminar"s SignUp installation.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentPasswort</Code>
	 **/
	public String getDozentPasswort(){
		return this.m_sDozentPasswort;
	}

	/**
	 * This teacher"s password to access this seminar"s SignUp installation.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentPasswort</Code>
	 **/	
	public void setDozentPasswort(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDozentPasswort)));
		this.m_sDozentPasswort=value;
	}
	

	/**
	 * This teacher"s title.
	 * @return This teacher"s title.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentTitel</Code>
	 **/
	public String getDozentTitel(){
		return this.m_sDozentTitel;
	}

	/**
	 * This teacher"s title.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentTitel</Code>
	 **/	
	public void setDozentTitel(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDozentTitel)));
		this.m_sDozentTitel=value;
	}
	

	/**
	 * First name.
	 * @return First name.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentVorname</Code>
	 **/
	public String getDozentVorname(){
		return this.m_sDozentVorname;
	}

	/**
	 * First name.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentVorname</Code>
	 **/	
	public void setDozentVorname(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDozentVorname)));
		this.m_sDozentVorname=value;
	}
	

	/**
	 * Last name.
	 * @return Last name.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentNachname</Code>
	 **/
	public String getDozentNachname(){
		return this.m_sDozentNachname;
	}

	/**
	 * Last name.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentNachname</Code>
	 **/	
	public void setDozentNachname(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDozentNachname)));
		this.m_sDozentNachname=value;
	}
	

	/**
	 * Date of birth.
	 * @return Date of birth.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.dtmDozentGebdatum</Code>
	 **/
	public Date getDozentGebdatum(){
		return this.m_dDozentGebdatum;
	}

	/**
	 * Date of birth.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.dtmDozentGebdatum</Code>
	 **/	
	public void setDozentGebdatum(Date value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_dDozentGebdatum)));
		this.m_dDozentGebdatum=value;
	}
	

	/**
	 * Job-number (concerning job in this seminar).
	 * @return Job-number (concerning job in this seminar).
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentStellennr</Code>
	 **/
	public String getDozentStellennr(){
		return this.m_sDozentStellennr;
	}

	/**
	 * Job-number (concerning job in this seminar).
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentStellennr</Code>
	 **/	
	public void setDozentStellennr(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDozentStellennr)));
		this.m_sDozentStellennr=value;
	}
	

	/**
	 * Private phone number.
	 * @return Private phone number.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentTelefonPrivat</Code>
	 **/
	public String getDozentTelefonPrivat(){
		return this.m_sDozentTelefonPrivat;
	}

	/**
	 * Private phone number.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentTelefonPrivat</Code>
	 **/	
	public void setDozentTelefonPrivat(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDozentTelefonPrivat)));
		this.m_sDozentTelefonPrivat=value;
	}
	

	/**
	 * Address: street.
	 * @return Address: street.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentStrasse</Code>
	 **/
	public String getDozentStrasse(){
		return this.m_sDozentStrasse;
	}

	/**
	 * Address: street.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentStrasse</Code>
	 **/	
	public void setDozentStrasse(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDozentStrasse)));
		this.m_sDozentStrasse=value;
	}
	

	/**
	 * Address: zip-code.
	 * @return Address: zip-code.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentPLZ</Code>
	 **/
	public String getDozentPLZ(){
		return this.m_sDozentPLZ;
	}

	/**
	 * Address: zip-code.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentPLZ</Code>
	 **/	
	public void setDozentPLZ(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDozentPLZ)));
		this.m_sDozentPLZ=value;
	}
	

	/**
	 * Address: place of residence.
	 * @return Address: place of residence.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentOrt</Code>
	 **/
	public String getDozentOrt(){
		return this.m_sDozentOrt;
	}

	/**
	 * Address: place of residence.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentOrt</Code>
	 **/	
	public void setDozentOrt(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDozentOrt)));
		this.m_sDozentOrt=value;
	}
	

	/**
	 * Job-end month.
	 * @return Job-end month.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentDienstendeMonat</Code>
	 **/
	public String getDozentDienstendeMonat(){
		return this.m_sDozentDienstendeMonat;
	}

	/**
	 * Job-end month.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentDienstendeMonat</Code>
	 **/	
	public void setDozentDienstendeMonat(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDozentDienstendeMonat)));
		this.m_sDozentDienstendeMonat=value;
	}
	

	/**
	 * Job-end year.
	 * @return Job-end year.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentDienstendeJahr</Code>
	 **/
	public String getDozentDienstendeJahr(){
		return this.m_sDozentDienstendeJahr;
	}

	/**
	 * Job-end year.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentDienstendeJahr</Code>
	 **/	
	public void setDozentDienstendeJahr(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDozentDienstendeJahr)));
		this.m_sDozentDienstendeJahr=value;
	}
	

	/**
	 * Is teacher civil servant?
	 * @return Is teacher civil servant?
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentOD</Code>
	 **/
	public String getDozentOD(){
		return this.m_sDozentOD;
	}

	/**
	 * Is teacher civil servant?
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentOD</Code>
	 **/	
	public void setDozentOD(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDozentOD)));
		this.m_sDozentOD=value;
	}
	

	/**
	 * Kind of contract (permanent, transient etc.)
	 * @return Kind of contract (permanent, transient etc.)
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentVertragsart</Code>
	 **/
	public String getDozentVertragsart(){
		return this.m_sDozentVertragsart;
	}

	/**
	 * Kind of contract (permanent, transient etc.)
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentVertragsart</Code>
	 **/	
	public void setDozentVertragsart(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDozentVertragsart)));
		this.m_sDozentVertragsart=value;
	}
	

	/**
	 * Bank
	 * @return Bank
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentBank</Code>
	 **/
	public String getDozentBank(){
		return this.m_sDozentBank;
	}

	/**
	 * Bank
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentBank</Code>
	 **/	
	public void setDozentBank(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDozentBank)));
		this.m_sDozentBank=value;
	}
	

	/**
	 * Bank account number.
	 * @return Bank account number.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentKto</Code>
	 **/
	public String getDozentKto(){
		return this.m_sDozentKto;
	}

	/**
	 * Bank account number.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentKto</Code>
	 **/	
	public void setDozentKto(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDozentKto)));
		this.m_sDozentKto=value;
	}
	

	/**
	 * Bank number.
	 * @return Bank number.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentBLZ</Code>
	 **/
	public String getDozentBLZ(){
		return this.m_sDozentBLZ;
	}

	/**
	 * Bank number.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentBLZ</Code>
	 **/	
	public void setDozentBLZ(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDozentBLZ)));
		this.m_sDozentBLZ=value;
	}
	

	/**
	 * Day of office hour.
	 * @return Day of office hour.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentSprechstundeTag</Code>
	 **/
	public String getDozentSprechstundeTag(){
		return this.m_sDozentSprechstundeTag;
	}

	/**
	 * Day of office hour.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentSprechstundeTag</Code>
	 **/	
	public void setDozentSprechstundeTag(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDozentSprechstundeTag)));
		this.m_sDozentSprechstundeTag=value;
	}
	

	/**
	 * Date of office hour.
	 * @return Date of office hour.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.dtmDozentSprechstundeDatum</Code>
	 **/
	public Date getDozentSprechstundeDatum(){
		return this.m_dDozentSprechstundeDatum;
	}

	/**
	 * Date of office hour.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.dtmDozentSprechstundeDatum</Code>
	 **/	
	public void setDozentSprechstundeDatum(Date value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_dDozentSprechstundeDatum)));
		this.m_dDozentSprechstundeDatum=value;
	}
	

	/**
	 * Time of office hour.
	 * @return Time of office hour.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.dtmDozentSprechstundeZeitVon</Code>
	 **/
	public String getDozentSprechstundeZeitVon(){
		return this.m_tDozentSprechstundeZeitVon;
	}

	/**
	 * Time of office hour.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.dtmDozentSprechstundeZeitVon</Code>
	 **/	
	public void setDozentSprechstundeZeitVon(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_tDozentSprechstundeZeitVon)));
		this.m_tDozentSprechstundeZeitVon=value;
	}
	

	/**
	 * Time when office hour ends.
	 * @return Time when office hour ends.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.dtmDozentSprechstundeZeitBis</Code>
	 **/
	public String getDozentSprechstundeZeitBis(){
		return this.m_tDozentSprechstundeZeitBis;
	}

	/**
	 * Time when office hour ends.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.dtmDozentSprechstundeZeitBis</Code>
	 **/	
	public void setDozentSprechstundeZeitBis(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_tDozentSprechstundeZeitBis)));
		this.m_tDozentSprechstundeZeitBis=value;
	}
	

	/**
	 * Office hour.
	 * @return Office hour.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentSprechstunde</Code>
	 **/
	public String getDozentSprechstunde(){
		return this.m_sDozentSprechstunde;
	}

	/**
	 * Office hour.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentSprechstunde</Code>
	 **/	
	public void setDozentSprechstunde(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDozentSprechstunde)));
		this.m_sDozentSprechstunde=value;
	}
	

	/**
	 * Building where this teacher resides (in this seminar).
	 * @return Building where this teacher resides (in this seminar).
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentBau</Code>
	 **/
	public String getDozentBau(){
		return this.m_sDozentBau;
	}

	/**
	 * Building where this teacher resides (in this seminar).
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentBau</Code>
	 **/	
	public void setDozentBau(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDozentBau)));
		this.m_sDozentBau=value;
	}
	

	/**
	 * Teacher"s room (in this seminar).
	 * @return Teacher"s room (in this seminar).
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentZimmer</Code>
	 **/
	public String getDozentZimmer(){
		return this.m_sDozentZimmer;
	}

	/**
	 * Teacher"s room (in this seminar).
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentZimmer</Code>
	 **/	
	public void setDozentZimmer(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDozentZimmer)));
		this.m_sDozentZimmer=value;
	}
	

	/**
	 * Field of research/teaching.
	 * @return Field of research/teaching.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentBereich</Code>
	 **/
	public String getDozentBereich(){
		return this.m_sDozentBereich;
	}

	/**
	 * Field of research/teaching.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentBereich</Code>
	 **/	
	public void setDozentBereich(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDozentBereich)));
		this.m_sDozentBereich=value;
	}
	

	/**
	 * Phone number in seminar.
	 * @return Phone number in seminar.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentTelefon</Code>
	 **/
	public String getDozentTelefon(){
		return this.m_sDozentTelefon;
	}

	/**
	 * Phone number in seminar.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentTelefon</Code>
	 **/	
	public void setDozentTelefon(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDozentTelefon)));
		this.m_sDozentTelefon=value;
	}
	

	/**
	 * Email address in seminar.
	 * @return Email address in seminar.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentEmail</Code>
	 **/
	public String getDozentEmail(){
		return this.m_sDozentEmail;
	}

	/**
	 * Email address in seminar.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentEmail</Code>
	 **/	
	public void setDozentEmail(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDozentEmail)));
		this.m_sDozentEmail=value;
	}
	

	/**
	 * Homepage.
	 * @return Homepage.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentHomepage</Code>
	 **/
	public String getDozentHomepage(){
		return this.m_sDozentHomepage;
	}

	/**
	 * Homepage.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentHomepage</Code>
	 **/	
	public void setDozentHomepage(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDozentHomepage)));
		this.m_sDozentHomepage=value;
	}
	

	/**
	 * Teaching? (Or other member of staff?)
	 * @return Teaching? (Or other member of staff?)
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.blnDozentLehrend</Code>
	 **/
	public boolean getDozentLehrend(){
		return this.m_bDozentLehrend;
	}

	/**
	 * Teaching? (Or other member of staff?)
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.blnDozentLehrend</Code>
	 **/	
	public void setDozentLehrend(boolean value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_bDozentLehrend));
		this.m_bDozentLehrend=value;
	}
	

	/**
	 * Remark
	 * @return Remark
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentBemerkung</Code>
	 **/
	public String getDozentBemerkung(){
		return this.m_sDozentBemerkung;
	}

	/**
	 * Remark
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentBemerkung</Code>
	 **/	
	public void setDozentBemerkung(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDozentBemerkung)));
		this.m_sDozentBemerkung=value;
	}
	

	/**
	 * How to address this person ("Mr., Mrs., Herr, Frau).
	 * @return How to address this person ("Mr., Mrs., Herr, Frau).
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentAnrede</Code>
	 **/
	public String getDozentAnrede(){
		return this.m_sDozentAnrede;
	}

	/**
	 * How to address this person ("Mr., Mrs., Herr, Frau).
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentAnrede</Code>
	 **/	
	public void setDozentAnrede(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDozentAnrede)));
		this.m_sDozentAnrede=value;
	}
	

	/**
	 * Comma-separated interest-list.
	 * @return Comma-separated interest-list.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentInteressen</Code>
	 **/
	public String getDozentInteressen(){
		return this.m_sDozentInteressen;
	}

	/**
	 * Comma-separated interest-list.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentInteressen</Code>
	 **/	
	public void setDozentInteressen(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDozentInteressen)));
		this.m_sDozentInteressen=value;
	}
	

	/**
	 * Last change of office hour.
	 * @return Last change of office hour.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.dtmSprechstundeLastChange</Code>
	 **/
	public String getSprechstundeLastChange(){
		return this.m_tSprechstundeLastChange;
	}

	/**
	 * Last change of office hour.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.dtmSprechstundeLastChange</Code>
	 **/	
	public void setSprechstundeLastChange(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_tSprechstundeLastChange)));
		this.m_tSprechstundeLastChange=value;
	}
	

	/**
	 * Pigeon hole.
	 * @return Pigeon hole.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentPostfach</Code>
	 **/
	public String getDozentPostfach(){
		return this.m_sDozentPostfach;
	}

	/**
	 * Pigeon hole.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentPostfach</Code>
	 **/	
	public void setDozentPostfach(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDozentPostfach)));
		this.m_sDozentPostfach=value;
	}
	

	/**
	 * Access Level: how much is this teacher allowed to do? (The values here are specific to implementations).
	 * @return Access Level: how much is this teacher allowed to do? (The values here are specific to implementations).
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.intDozentAccessLevel</Code>
	 **/
	public int getDozentAccessLevel(){
		return this.m_iDozentAccessLevel;
	}

	/**
	 * Access Level: how much is this teacher allowed to do? (The values here are specific to implementations).
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.intDozentAccessLevel</Code>
	 **/	
	public void setDozentAccessLevel(int value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_iDozentAccessLevel));
		this.m_iDozentAccessLevel=value;
	}
	

	/**
	 * IP-address of teacher"s computer. May be used to control access to SignUp-functionality.
	 * @return IP-address of teacher"s computer. May be used to control access to SignUp-functionality.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentIP</Code>
	 **/
	public String getDozentIP(){
		return this.m_sDozentIP;
	}

	/**
	 * IP-address of teacher"s computer. May be used to control access to SignUp-functionality.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentIP</Code>
	 **/	
	public void setDozentIP(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDozentIP)));
		this.m_sDozentIP=value;
	}
	

	/**
	 * SessionID.
	 * @return SessionID.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.lngDozentSessionID</Code>
	 **/
	public long getDozentSessionID(){
		return this.m_lDozentSessionID;
	}

	/**
	 * SessionID.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.lngDozentSessionID</Code>
	 **/	
	public void setDozentSessionID(long value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_lDozentSessionID));
		this.m_lDozentSessionID=value;
	}
	

	/**
	 * Timestamp of teacher"s last action in SignUp. Unused.
	 * @return Timestamp of teacher"s last action in SignUp. Unused.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.dtmDozentLastAction</Code>
	 **/
	public String getDozentLastAction(){
		return this.m_tDozentLastAction;
	}

	/**
	 * Timestamp of teacher"s last action in SignUp. Unused.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.dtmDozentLastAction</Code>
	 **/	
	public void setDozentLastAction(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_tDozentLastAction)));
		this.m_tDozentLastAction=value;
	}
	

	/**
	 * Teacher"s certificate subject DN. Certificates can be used to control access to SignUp-functionality.
	 * @return Teacher"s certificate subject DN. Certificates can be used to control access to SignUp-functionality.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentCertSubjectDN</Code>
	 **/
	public String getDozentCertSubjectDN(){
		return this.m_sDozentCertSubjectDN;
	}

	/**
	 * Teacher"s certificate subject DN. Certificates can be used to control access to SignUp-functionality.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentCertSubjectDN</Code>
	 **/	
	public void setDozentCertSubjectDN(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDozentCertSubjectDN)));
		this.m_sDozentCertSubjectDN=value;
	}
	

	/**
	 * Teacher"s certificate issuer DN. Certificates can be used to control access to SignUp-functionality.
	 * @return Teacher"s certificate issuer DN. Certificates can be used to control access to SignUp-functionality.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentCertIssuerDN</Code>
	 **/
	public String getDozentCertIssuerDN(){
		return this.m_sDozentCertIssuerDN;
	}

	/**
	 * Teacher"s certificate issuer DN. Certificates can be used to control access to SignUp-functionality.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentCertIssuerDN</Code>
	 **/	
	public void setDozentCertIssuerDN(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDozentCertIssuerDN)));
		this.m_sDozentCertIssuerDN=value;
	}
	

	/**
	 * Teacher"s certificate serial id. Certificates can be used to control access to SignUp-functionality.
	 * @return Teacher"s certificate serial id. Certificates can be used to control access to SignUp-functionality.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentCertSerialID</Code>
	 **/
	public String getDozentCertSerialID(){
		return this.m_sDozentCertSerialID;
	}

	/**
	 * Teacher"s certificate serial id. Certificates can be used to control access to SignUp-functionality.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.strDozentCertSerialID</Code>
	 **/	
	public void setDozentCertSerialID(String value){
		this.m_bIsDirty = (this.m_bIsDirty || (!value.equals(this.m_sDozentCertSerialID)));
		this.m_sDozentCertSerialID=value;
	}
	

	/**
	 * Is the teacher"s certificate validated (handed out by a member of staff?)
	 * @return Is the teacher"s certificate validated (handed out by a member of staff?)
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.blnDozentCertValidated</Code>
	 **/
	public boolean getDozentCertValidated(){
		return this.m_bDozentCertValidated;
	}

	/**
	 * Is the teacher"s certificate validated (handed out by a member of staff?)
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.blnDozentCertValidated</Code>
	 **/	
	public void setDozentCertValidated(boolean value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_bDozentCertValidated));
		this.m_bDozentCertValidated=value;
	}
	

	/**
	 * Use this flag to revoke a certificate"s validity. Certificates can be used to control access to SignUp-functionality. If this flag is set to "true," it is as if the teacher had no certificate. Please note that as of yet there is no connection to a revocation list of certificates from the issuer.
	 * @return Use this flag to revoke a certificate"s validity. Certificates can be used to control access to SignUp-functionality. If this flag is set to "true," it is as if the teacher had no certificate. Please note that as of yet there is no connection to a revocation list of certificates from the issuer.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.blnDozentCertRevoked</Code>
	 **/
	public boolean getDozentCertRevoked(){
		return this.m_bDozentCertRevoked;
	}

	/**
	 * Use this flag to revoke a certificate"s validity. Certificates can be used to control access to SignUp-functionality. If this flag is set to "true," it is as if the teacher had no certificate. Please note that as of yet there is no connection to a revocation list of certificates from the issuer.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.blnDozentCertRevoked</Code>
	 **/	
	public void setDozentCertRevoked(boolean value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_bDozentCertRevoked));
		this.m_bDozentCertRevoked=value;
	}
	

	/**
	 * Is this a teacher of the seminar, or just an import? Effect is that imported, external teachers can be excluded from combo-boxes.
	 * @return Is this a teacher of the seminar, or just an import? Effect is that imported, external teachers can be excluded from combo-boxes.
	 * Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.blnDozentExtern</Code>
	 **/
	public boolean getDozentExtern(){
		return this.m_bDozentExtern;
	}

	/**
	 * Is this a teacher of the seminar, or just an import? Effect is that imported, external teachers can be excluded from combo-boxes.
	 * @param value: Assoziiert mit der Datenbanktabellenspalte <Code>tblSdDozent.blnDozentExtern</Code>
	 **/	
	public void setDozentExtern(boolean value){
		this.m_bIsDirty = (this.m_bIsDirty || (value != this.m_bDozentExtern));
		this.m_bDozentExtern=value;
	}
        
	/**
         * Prüft Login
	 * @param strName last name of teacher to log in
	 * @param strPwd this teacher's password
	 * @param lngSeminarID id of institute or seminar.
	 * @return true if login okay and complete. False otherwise, i.e. if password or name wrong, or if an error occurred.
	 */
	public boolean loginSec(String strName, String strPwd, long lngSeminarID){

		boolean blnReturn=false;
		ResultSet rst=null;

		try{
                    PreparedStatement pstm=prepareStatement( "SELECT * " +
                                                    "FROM \"tblSdDozent\" " +
                                    "WHERE ((\"lngSdSeminarID\"=?) AND " +
                                                    "(\"intDozentAccessLevel\">0) AND " +
                                                    "(\"strDozentNachname\"=?));");
                    pstm.setLong(1, lngSeminarID);
                    pstm.setString(2, strName);
                    String sPwd;
		    rst=pstm.executeQuery();
			while(rst.next()){

                           sPwd=rst.getString("strDozentPasswort");
                           if(PasswordHash.validatePassword(strPwd, sPwd)){
				initByRst(rst);
				rst.close();
				blnReturn=true;
                                break;
                            }
                        }
                    }catch(NoSuchAlgorithmException eLogin){
				blnReturn=false;
                    } catch (InvalidKeySpecException eLogin) {
                            blnReturn=false;
                    } catch (SQLException eLogin) {
                        blnReturn=false;
                    } catch (NamingException eLogin) {
                        blnReturn=false;
                    }
		// new March 1 2010:
		try{this.disconnect();}catch(Exception e){}
		return blnReturn;
	}
	
	/** method checks if a Dozent (teacher) is registered under this name, with this password,
	 * and in this institute/seminar. Returns boolean true if this is so, false if not or an error occurred.
	 * In case the method works fine, the Dozent object is initialized. The teacher's SessionID is
	 * set in this method, as it assumes to be a _new_ login.
	 * @param strName last name of teacher to log in
	 * @param strPwd this teacher's password
	 * @param lngSeminarID id of institute or seminar.
         * @deprecated 
	 * @return true if login okay and complete. False otherwise, i.e. if password or name wrong, or if an error occurred.
	 */
    public boolean login(String strName, String strPwd, long lngSeminarID) {

        boolean blnReturn = false;
        ResultSet rst = null;

        String strSQL = "SELECT * FROM \"tblSdDozent\" "
                + "WHERE ((\"lngSdSeminarID\"=?) AND "
                + "(\"strDozentPasswort\"=?) AND "
                + "(\"intDozentAccessLevel\">0) AND "
                + "(\"strDozentNachname\"=?));";

        try {
            PreparedStatement pstm = prepareStatement(strSQL);
            pstm.setLong(1, lngSeminarID);
            pstm.setString(2, strPwd);
            pstm.setString(3, strName);
            rst = pstm.executeQuery();
            blnReturn = rst.next();
            if(blnReturn){
                this.initByRst(rst);
                rst.close();
            }
        } catch (Exception eLogin) {
            blnReturn = false;
        }
        // new March 1 2010:
        try {
            this.disconnect();
        } catch (Exception e) {
        }
        return blnReturn;
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
			"<DozentUnivISID>" + m_sDozentUnivISID + "</DozentUnivISID>"  + 
			"<DozentPasswort>" + m_sDozentPasswort + "</DozentPasswort>"  + 
			"<DozentTitel>" + m_sDozentTitel + "</DozentTitel>"  + 
			"<DozentVorname>" + m_sDozentVorname + "</DozentVorname>"  + 
			"<DozentNachname>" + m_sDozentNachname + "</DozentNachname>"  + 
			"<DozentGebdatum>" + m_dDozentGebdatum + "</DozentGebdatum>"  + 
			"<DozentStellennr>" + m_sDozentStellennr + "</DozentStellennr>"  + 
			"<DozentTelefonPrivat>" + m_sDozentTelefonPrivat + "</DozentTelefonPrivat>"  + 
			"<DozentStrasse>" + m_sDozentStrasse + "</DozentStrasse>"  + 
			"<DozentPLZ>" + m_sDozentPLZ + "</DozentPLZ>"  + 
			"<DozentOrt>" + m_sDozentOrt + "</DozentOrt>"  + 
			"<DozentDienstendeMonat>" + m_sDozentDienstendeMonat + "</DozentDienstendeMonat>"  + 
			"<DozentDienstendeJahr>" + m_sDozentDienstendeJahr + "</DozentDienstendeJahr>"  + 
			"<DozentOD>" + m_sDozentOD + "</DozentOD>"  + 
			"<DozentVertragsart>" + m_sDozentVertragsart + "</DozentVertragsart>"  + 
			"<DozentBank>" + m_sDozentBank + "</DozentBank>"  + 
			"<DozentKto>" + m_sDozentKto + "</DozentKto>"  + 
			"<DozentBLZ>" + m_sDozentBLZ + "</DozentBLZ>"  + 
			"<DozentSprechstundeTag>" + m_sDozentSprechstundeTag + "</DozentSprechstundeTag>"  + 
			"<DozentSprechstundeDatum>" + m_dDozentSprechstundeDatum + "</DozentSprechstundeDatum>"  + 
			"<DozentSprechstundeZeitVon>" + m_tDozentSprechstundeZeitVon + "</DozentSprechstundeZeitVon>"  + 
			"<DozentSprechstundeZeitBis>" + m_tDozentSprechstundeZeitBis + "</DozentSprechstundeZeitBis>"  + 
			"<DozentSprechstunde>" + m_sDozentSprechstunde + "</DozentSprechstunde>"  + 
			"<DozentBau>" + m_sDozentBau + "</DozentBau>"  + 
			"<DozentZimmer>" + m_sDozentZimmer + "</DozentZimmer>"  + 
			"<DozentBereich>" + m_sDozentBereich + "</DozentBereich>"  + 
			"<DozentTelefon>" + m_sDozentTelefon + "</DozentTelefon>"  + 
			"<DozentEmail>" + m_sDozentEmail + "</DozentEmail>"  + 
			"<DozentHomepage>" + m_sDozentHomepage + "</DozentHomepage>"  + 
			"<DozentLehrend>" + m_bDozentLehrend + "</DozentLehrend>"  + 
			"<DozentBemerkung>" + m_sDozentBemerkung + "</DozentBemerkung>"  + 
			"<DozentAnrede>" + m_sDozentAnrede + "</DozentAnrede>"  + 
			"<DozentInteressen>" + m_sDozentInteressen + "</DozentInteressen>"  + 
			"<SprechstundeLastChange>" + m_tSprechstundeLastChange + "</SprechstundeLastChange>"  + 
			"<DozentPostfach>" + m_sDozentPostfach + "</DozentPostfach>"  + 
			"<DozentAccessLevel>" + m_iDozentAccessLevel + "</DozentAccessLevel>"  + 
			"<DozentIP>" + m_sDozentIP + "</DozentIP>"  + 
			"<DozentSessionID>" + m_lDozentSessionID + "</DozentSessionID>"  + 
			"<DozentLastAction>" + m_tDozentLastAction + "</DozentLastAction>"  + 
			"<DozentCertSubjectDN>" + m_sDozentCertSubjectDN + "</DozentCertSubjectDN>"  + 
			"<DozentCertIssuerDN>" + m_sDozentCertIssuerDN + "</DozentCertIssuerDN>"  + 
			"<DozentCertSerialID>" + m_sDozentCertSerialID + "</DozentCertSerialID>"  + 
			"<DozentCertValidated>" + m_bDozentCertValidated + "</DozentCertValidated>"  + 
			"<DozentCertRevoked>" + m_bDozentCertRevoked + "</DozentCertRevoked>"  + 
			"<DozentExtern>" + m_bDozentExtern + "</DozentExtern>" ;
	}

////////////////////////////////////////////////////////////////
// 4.   S Q L  U T I L I T I E S
////////////////////////////////////////////////////////////////


	/**
	 * Where-SQL für eindeutigen Index dieses Objekte in der Datenbank (in Tabelle 'tblSdDozent.').
	 * @return SQL-String wie z.B. "Id1=? and Id2=?"
	 **/
	private String getSQLWhereClause(){
		return 
			"\"lngSdSeminarID\"=? AND " + 
			"\"lngDozentID\"=?"; 
	}
	
	/**
	 * Unique where-clause for table 'tblSdDozent.'
	 * @return String (SQL-code, postgres-specific)
	 * @deprecated
	 **/
	protected String getSQLWhereClauseOld(){
		return 
			"\"lngSdSeminarID\"=" + this.m_lSdSeminarID + " AND "  + 
			"\"lngDozentID\"=" + this.m_lDozentID;
	}

	/**
	 * SQL-Befehl zum Löschen des Objekts in der Datenbank (aus Tabelle 'tblSdDozent.')
	 * @return SQL-String zum Löschen des Objekts, z.B. "delete from T1 where (Id1=?);"
	 **/
	private String toDBDeleteString(){
		return "delete from \"tblSdDozent\" where ( " + this.getSQLWhereClause() + ");";
	}

	/**
 	 * Setzt die Werte für alle Platzhalter in {@link #getSQLWhereClause()}.
	 * @param ii Offset für die Parameter (inklusive).
	 * @throws SQLException 
	 **/
	private void pokeWhere(int ii, PreparedStatement ps) throws SQLException{
		ps.setLong(ii++, m_lSdSeminarID);
		ps.setLong(ii++, m_lDozentID);
	}

	/**
  	 * Füllt das SQL-Statement mit den aktuellen Werten des Objekts
	 * @throws SQLException
	 **/
	private void pokeStatement(PreparedStatement ps) throws SQLException{
		
		ps.setLong(1, m_lSdSeminarID);
		ps.setLong(2, m_lDozentID);
		ps.setString(3, m_sDozentUnivISID);
		ps.setString(4, m_sDozentPasswort);
		ps.setString(5, m_sDozentTitel);
		ps.setString(6, m_sDozentVorname);
		ps.setString(7, m_sDozentNachname);
		ps.setDate(8, m_dDozentGebdatum);
		ps.setString(9, m_sDozentStellennr);
		ps.setString(10, m_sDozentTelefonPrivat);
		ps.setString(11, m_sDozentStrasse);
		ps.setString(12, m_sDozentPLZ);
		ps.setString(13, m_sDozentOrt);
		ps.setString(14, m_sDozentDienstendeMonat);
		ps.setString(15, m_sDozentDienstendeJahr);
		ps.setString(16, m_sDozentOD);
		ps.setString(17, m_sDozentVertragsart);
		ps.setString(18, m_sDozentBank);
		ps.setString(19, m_sDozentKto);
		ps.setString(20, m_sDozentBLZ);
		ps.setString(21, m_sDozentSprechstundeTag);
		ps.setDate(22, m_dDozentSprechstundeDatum);
		try{
			ps.setTime(23, new java.sql.Time(g_TIME_FORMAT.parse(m_tDozentSprechstundeZeitVon).getTime()));
			ps.setTime(24, new java.sql.Time(g_TIME_FORMAT.parse(m_tDozentSprechstundeZeitBis).getTime()));
		}catch(Exception eNull){
			ps.setTime(23, new java.sql.Time(0));
			ps.setTime(24, new java.sql.Time(0));		
		}
		ps.setString(25, m_sDozentSprechstunde);
		ps.setString(26, m_sDozentBau);
		ps.setString(27, m_sDozentZimmer);
		ps.setString(28, m_sDozentBereich);
		ps.setString(29, m_sDozentTelefon);
		ps.setString(30, m_sDozentEmail);
		ps.setString(31, m_sDozentHomepage);
		ps.setBoolean(32, m_bDozentLehrend);
		ps.setString(33, m_sDozentBemerkung);
		ps.setString(34, m_sDozentAnrede);
		ps.setString(35, m_sDozentInteressen);
		try{
			ps.setTimestamp(36,  new java.sql.Timestamp(g_TIME_FORMAT.parse(m_tSprechstundeLastChange).getTime()));
		}catch(Exception eBz){
			ps.setTimestamp(36,  new java.sql.Timestamp(0));
		}
		ps.setString(37, m_sDozentPostfach);
		ps.setInt(38, m_iDozentAccessLevel);
		ps.setString(39, m_sDozentIP);
		ps.setLong(40, m_lDozentSessionID);
		try{
			ps.setTimestamp(41,  new java.sql.Timestamp(g_TIME_FORMAT.parse(m_tDozentLastAction).getTime()));
		}catch(Exception asdf){
			ps.setTimestamp(41,  new java.sql.Timestamp(0));
		}
		ps.setString(42, m_sDozentCertSubjectDN);
		ps.setString(43, m_sDozentCertIssuerDN);
		ps.setString(44, m_sDozentCertSerialID);
		ps.setBoolean(45, m_bDozentCertValidated);
		ps.setBoolean(46, m_bDozentCertRevoked);
		ps.setBoolean(47, m_bDozentExtern);
                ps.setString(48, m_sDozentHomepageOptions);

	}

	/**
	 * SQL-Befehl zum Speichern der Objekteigenschaften in die Datenbank (Tabelle 'tblSdDozent.')
	 * @return PreparedStatemtent, das nach Aufruf von {@link #pokeStatement(PreparedStatement)} und {@link #execute()} das Update ausführt.
	 **/
	private String toDBUpdateString(){
		return "update \"tblSdDozent\" set " +
			"\"lngSdSeminarID\"=?, " +
			"\"lngDozentID\"=?, " +
			"\"strDozentUnivISID\"=?, " +
			"\"strDozentPasswort\"=?, " +
			"\"strDozentTitel\"=?, " +
			"\"strDozentVorname\"=?, " +
			"\"strDozentNachname\"=?, " +
			"\"dtmDozentGebdatum\"=?, " +
			"\"strDozentStellennr\"=?, " +
			"\"strDozentTelefonPrivat\"=?, " +
			"\"strDozentStrasse\"=?, " +
			"\"strDozentPLZ\"=?, " +
			"\"strDozentOrt\"=?, " +
			"\"strDozentDienstendeMonat\"=?, " +
			"\"strDozentDienstendeJahr\"=?, " +
			"\"strDozentOD\"=?, " +
			"\"strDozentVertragsart\"=?, " +
			"\"strDozentBank\"=?, " +
			"\"strDozentKto\"=?, " +
			"\"strDozentBLZ\"=?, " +
			"\"strDozentSprechstundeTag\"=?, " +
			"\"dtmDozentSprechstundeDatum\"=?, " +
			"\"dtmDozentSprechstundeZeitVon\"=?, " +
			"\"dtmDozentSprechstundeZeitBis\"=?, " +
			"\"strDozentSprechstunde\"=?, " +
			"\"strDozentBau\"=?, " +
			"\"strDozentZimmer\"=?, " +
			"\"strDozentBereich\"=?, " +
			"\"strDozentTelefon\"=?, " +
			"\"strDozentEmail\"=?, " +
			"\"strDozentHomepage\"=?, " +
			"\"blnDozentLehrend\"=?, " +
			"\"strDozentBemerkung\"=?, " +
			"\"strDozentAnrede\"=?, " +
			"\"strDozentInteressen\"=?, " +
			"\"dtmSprechstundeLastChange\"=?, " +
			"\"strDozentPostfach\"=?, " +
			"\"intDozentAccessLevel\"=?, " +
			"\"strDozentIP\"=?, " +
			"\"lngDozentSessionID\"=?, " +
			"\"dtmDozentLastAction\"=?, " +
			"\"strDozentCertSubjectDN\"=?, " +
			"\"strDozentCertIssuerDN\"=?, " +
			"\"strDozentCertSerialID\"=?, " +
			"\"blnDozentCertValidated\"=?, " +
			"\"blnDozentCertRevoked\"=?, " +
			"\"blnDozentExtern\"=?," +
                        "\"strDozentHomepageOptions\"=?" +
			" where (" + this.getSQLWhereClause() + ");";
	}

	/**
	 * SQL-Befehl zum Hinzufügen des Objekts in die Datenbank (Tabelle 'tblSdDozent.')
	 * @return SQL-String "insert into...".
	 **/
	private String toDBAddString(){
		return "insert into \"tblSdDozent\" ( " +
			"\"lngSdSeminarID\", \"lngDozentID\", \"strDozentUnivISID\", \"strDozentPasswort\", \"strDozentTitel\", \"strDozentVorname\", \"strDozentNachname\", \"dtmDozentGebdatum\", \"strDozentStellennr\", \"strDozentTelefonPrivat\", \"strDozentStrasse\", \"strDozentPLZ\", \"strDozentOrt\", \"strDozentDienstendeMonat\", \"strDozentDienstendeJahr\", \"strDozentOD\", \"strDozentVertragsart\", \"strDozentBank\", \"strDozentKto\", \"strDozentBLZ\", \"strDozentSprechstundeTag\", \"dtmDozentSprechstundeDatum\", \"dtmDozentSprechstundeZeitVon\", \"dtmDozentSprechstundeZeitBis\", \"strDozentSprechstunde\", \"strDozentBau\", \"strDozentZimmer\", \"strDozentBereich\", \"strDozentTelefon\", \"strDozentEmail\", \"strDozentHomepage\", \"blnDozentLehrend\", \"strDozentBemerkung\", \"strDozentAnrede\", \"strDozentInteressen\", \"dtmSprechstundeLastChange\", \"strDozentPostfach\", \"intDozentAccessLevel\", \"strDozentIP\", \"lngDozentSessionID\", \"dtmDozentLastAction\", \"strDozentCertSubjectDN\", \"strDozentCertIssuerDN\", \"strDozentCertSerialID\", \"blnDozentCertValidated\", \"blnDozentCertRevoked\", \"blnDozentExtern\", \"strDozentHomepageOptions\" ) VALUES ( ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
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
	protected void init(long lngSdSeminarID, long lngDozentID) throws SQLException, NamingException{

		this.m_lSdSeminarID=lngSdSeminarID;
		this.m_lDozentID=lngDozentID;
		
		PreparedStatement pstm = prepareStatement("select * from \"tblSdDozent\" where (" + this.getSQLWhereClause() + ");");
		pokeWhere(1,pstm);

		ResultSet rst=pstm.executeQuery();
		if(rst.next()) this.initByRst(rst);
		rst.close();
		this.disconnect();
		rst=null;
	}	
	
	/**
	 * Lade die Objekteigenschaften aus einem ResultSet.
	 * param rst ResultSet mit allen Spalten der Tabelle 'tblSdDozent'
	 * @throws SQLException 
	 **/
	public void initByRst(ResultSet rst) throws SQLException{
		this.m_lSdSeminarID=rst.getLong("lngSdSeminarID");
		this.m_lDozentID=rst.getLong("lngDozentID");
		this.m_sDozentUnivISID=rst.getString("strDozentUnivISID");
		this.m_sDozentPasswort=rst.getString("strDozentPasswort");
		this.m_sDozentTitel=rst.getString("strDozentTitel");
		this.m_sDozentVorname=rst.getString("strDozentVorname");
		this.m_sDozentNachname=rst.getString("strDozentNachname");
		this.m_dDozentGebdatum=rst.getDate("dtmDozentGebdatum");
		this.m_sDozentStellennr=rst.getString("strDozentStellennr");
		this.m_sDozentTelefonPrivat=rst.getString("strDozentTelefonPrivat");
		this.m_sDozentStrasse=rst.getString("strDozentStrasse");
		this.m_sDozentPLZ=rst.getString("strDozentPLZ");
		this.m_sDozentOrt=rst.getString("strDozentOrt");
		this.m_sDozentDienstendeMonat=rst.getString("strDozentDienstendeMonat");
		this.m_sDozentDienstendeJahr=rst.getString("strDozentDienstendeJahr");
		this.m_sDozentOD=rst.getString("strDozentOD");
		this.m_sDozentVertragsart=rst.getString("strDozentVertragsart");
		this.m_sDozentBank=rst.getString("strDozentBank");
		this.m_sDozentKto=rst.getString("strDozentKto");
		this.m_sDozentBLZ=rst.getString("strDozentBLZ");
		this.m_sDozentSprechstundeTag=rst.getString("strDozentSprechstundeTag");
		this.m_dDozentSprechstundeDatum=rst.getDate("dtmDozentSprechstundeDatum");
		this.m_tDozentSprechstundeZeitVon=rst.getString("dtmDozentSprechstundeZeitVon");
		this.m_tDozentSprechstundeZeitBis=rst.getString("dtmDozentSprechstundeZeitBis");
		this.m_sDozentSprechstunde=rst.getString("strDozentSprechstunde");
		this.m_sDozentBau=rst.getString("strDozentBau");
		this.m_sDozentZimmer=rst.getString("strDozentZimmer");
		this.m_sDozentBereich=rst.getString("strDozentBereich");
		this.m_sDozentTelefon=rst.getString("strDozentTelefon");
		this.m_sDozentEmail=rst.getString("strDozentEmail");
		this.m_sDozentHomepage=rst.getString("strDozentHomepage");
		this.m_bDozentLehrend=rst.getBoolean("blnDozentLehrend");
		this.m_sDozentBemerkung=rst.getString("strDozentBemerkung");
		this.m_sDozentAnrede=rst.getString("strDozentAnrede");
		this.m_sDozentInteressen=rst.getString("strDozentInteressen");
		this.m_tSprechstundeLastChange=rst.getString("dtmSprechstundeLastChange");
		this.m_sDozentPostfach=rst.getString("strDozentPostfach");
		this.m_iDozentAccessLevel=rst.getInt("intDozentAccessLevel");
		this.m_sDozentIP=rst.getString("strDozentIP");
		this.m_lDozentSessionID=rst.getLong("lngDozentSessionID");
		this.m_tDozentLastAction=rst.getString("dtmDozentLastAction");
		this.m_sDozentCertSubjectDN=rst.getString("strDozentCertSubjectDN");
		this.m_sDozentCertIssuerDN=rst.getString("strDozentCertIssuerDN");
		this.m_sDozentCertSerialID=rst.getString("strDozentCertSerialID");
		this.m_bDozentCertValidated=rst.getBoolean("blnDozentCertValidated");
		this.m_bDozentCertRevoked=rst.getBoolean("blnDozentCertRevoked");
		this.m_bDozentExtern=rst.getBoolean("blnDozentExtern");	
                this.m_sDozentHomepageOptions=rst.getString("strDozentHomepageOptions");
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
	 * @throws Exception 
	 * @throw 
         **/
	public boolean update() throws SQLException, NamingException, Exception{
		if( !(isDirty()) ) return true;
		boolean bReturn = false;

		// Lade Statement mit SQL
		PreparedStatement pstm = prepareStatement(toDBUpdateString());

		// Lade Objekteigenschaften in Statement
		pokeStatement(pstm);

		// Identifiziere das Objekt (bzw. die Tabellenzeile) per Where-Clause
		// und übermittle die neuen Werte an die Datenbank
		pokeWhere(49,pstm);
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
	public Dozent(){}	
	
	/**
	 * Konstruktor mit Indexwerten (eindeutig) der assoziierten Datenbanktabelle
	 * @throws NamingException 
	 * @throws SQLException 
	 **/
	public Dozent(long lngSdSeminarID, long lngDozentID) throws SQLException, NamingException{
		this.init(lngSdSeminarID, lngDozentID);
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
	public Dozent(ResultSet rst) throws SQLException{
		this.initByRst(rst);
		this.m_bIsDirty = false;
	}


  }//Klassenende
