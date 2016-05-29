/**
 * Software-Entwicklung H. Jakubzik, Lizenz Version 1.0 (Uni Heidelberg)
 *
 *
 * Copyright (c) 2004 Heiko Jakubzik.  All rights reserved.
 *
 * http://www.heiko-jakubzik.de/
 * http://www.heiko-jakubzik.de/SignUp/
 * http://www.heiko-jakubzik.de/SignUp/hd/
 * mailto:heiko.jakubzik@t-online.de
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is not permitted.
 *
 * Lizenznehmer der Universitaet Heidelberg erwerben mit der Lizenz
 * das Recht, den Quellcode der Internetanwendung "SignUp" einzusehen,
 * zu veraendern und fuer den Eigenbedarf neu zu kompilieren.
 *
 * Die Weitergabe von Softwarekomponenten des Pakets durch die
 * Lizenznehmerin ist weder als Quelltext noch in kompilierter Form
 * ganz oder teilweise gestattet.
 *
 * Diese Lizenz erstreckt sich auf die Internetanwendung "SignUp",
 * also
 *
 * 1. das Datenschema,
 * 2. die Bibliothek "SignUp.jar"
 * 3. das Webarchiv  "SignUpXP.war"
 * 4. die Resource Bundles und Interfaces,
 * 5. das Access Frontend "SignUpXP",
 * 6. Konvertierungs-Skripte, XML und XSL Dateien,
 *    sowie alle vbs und hta Anwendungen zu Konvertierung
 *    von Excel und XML Dateien in SignUp-kompatibles Format,
 * 7. C# .NET Frontend Komponenten,
 * 8. die Webservice Spezifikation,
 * 9. die Dokumentation inkl. Bilder
 *
 * Das Rechtemanagement zur Neukompilierung unterliegt der
 * Kontrolle der Universtitaet Heidelberg. Fuer Kompilationen,
 * die von der Lizenznehmerin durchgefuehrt wurden, besteht
 * kein Anspruch auf Nachbesserung.
 *
 * Der Lizenzgeber behaelt es sich vor, die Lizenz auf
 * ein "Open Source"-Modell umzustellen.
 *
 * Bei Aenderungen am Datenmodell oder an Objekten _dieses_ Pakets
 * (com.shj.signUp.data) wird _dringend_ Ruecksprache mit dem
 * Autor der Software empfohlen.
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
 *
 * @version 0-00-01 (hand-coded)
 * change date              change author           change description
 * Jan 2004		            h. jakubzik             creation
 * April 9, 2004			h. jakubzik				added .getSemesterName()
 *													dates are now plausible 50 years back and future.
 *
 * April 25, 2004			h. jakubzik				getSemesterNameCbo()
 * May   04, 2004			h. jakubzik				getSemesterNameUnivIS()
 * May   16, 2004			h. jakubzik				getSemesterNameTranscript(LANGUAGE) [#hack, all German right now]
 *													getSemesterStart	}  SQL
 *													getSemesterStart	}  Utilities for Transcript
 * Oct	 03, 2004			h. jakubzik				getSemesterNameTranscript(LANGUAGE) added English
 * 
 * Aug   04, 2005			h. jakubzik				getYear, getMonth, getDay (simple getters), toString
 * 
 * Mar	 03, 2006			h. jakubzik				setXCboName methods to make Html-interaction variable (setDayCboName, setMonthCboName etc.).
 * 													m_PreselectCbos to turn off prelection 
 */

package de.shj.UP.HTML;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import javax.servlet.http.*;

import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * Very small helper class for Html-Dates 'n Combos.
 * This may grow in the future (compare #hack-sections in other classes).
 * Localization is as yet rudimentary.
 * Extends shjCore to make use of 'shjGetLocalDate.'
 * The logic is: to construct, always use ISO-format (yyyy-MM-dd)
 * Output is always dd-mm-yyyy.
 **/
public class HtmlDate extends de.shj.UP.data.shjCore{

	private static final long serialVersionUID = 322004670074144924L;
	private 		String	m_strSummerTerm[]	= new String[3];
	private 		String	m_strWinterTerm[]	= new String[3];
	private			boolean m_bUnparsable			= false;

	private 		String 	m_strDate			= "";	//the date as used in constructor (i.e. ISO, 'yyyy-MM-dd')
	private			String  m_strGermanDate		= "";	//the date as German format		  (i.e. 	 'dd.MM.yyyy')
	private			String	m_strToday			= "";	//today's date, in German format  (i.e.		 'dd.MM.yyyy')
	private			Locale  m_loc;						//Locale used in constructor
	private 		int 	m_intDay			= 0;	//Day 	of m_strDate
	private			int		m_intMonth			= 0;	//Month of m_strDate
	private			int 	m_intYear			= 0; 	//Year  of m_strDate

	private			String  m_strSemesterName	= "";	//Name of semester
	private			String  m_strSemesterNameUnivIS = ""; //Name of semester in UnivIS format

	private			String  m_strCboDay			= "cboDay";		// Default names for combo-boxes
	private			String  m_strCboMonth		= "cboMonth";	//	...
	private			String  m_strCboYear		= "cboYear";	//	...

	private			String  m_strSemesterCbo	= "cboSemester";// Default ...

	private final 	int 	m_intYearRangePlus	= 50;	//How far into the future is this date regarded plausible?
	private final 	int 	m_intYearRangeMinus	= 60;	//How far into the past is this date regarded plausible?

	private			int		m_intMinYear		= 0;	//Calculated on construction: year of object - YearRangeMinus
	private			int		m_intMaxYear		= 0;	//Calculated on construction: year of object + YearRangePlus
	
	private			boolean m_PreselectCbos		= true;	// default for preselect combos is true

	private			String	m_strMonths[]		= {"Jan", "Feb", "Mar", "Apr", "Mai", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dez"};
	private Date m_dDate;

	/**
	 * @returns date in ISO format ('yyyy-MM-dd').
	 **/
	public String getIsoDate(){
		return this.m_strDate;
	}
	
	public Date getDate(){
		return m_dDate;
	}
	
	/**
	 * For combo-construction: which years to show?
	 * @param iDistPast
	 * @param iDistFuture
	 */
	public void setYearRange(int iDistPast, int iDistFuture){

		int intCurrentYear = Integer.parseInt ( m_strToday.substring(6)			);

		m_intMinYear   = intCurrentYear - iDistPast;
		m_intMaxYear   = intCurrentYear + iDistFuture;
	}
	
	/**
	 * <pre>
	 * Methods that return option-tags for select Html 
	 * constructs usually set the current date as default, 
	 * marking it as an 'option selected' in the Html.
	 * 
	 * Sometimes it might be useful, however, to retrieve 
	 * un-preselected combos, if only to make the JSP source 
	 * smaller.
	 * 
	 * Calling <Code>.setPreselectCbo (false)</Code> will turn preselection off.
	 * </pre>
	 * @version 6-12-3 (Mar, 2006)
	 * @param value
	 */
	public void setPreselectCbo(boolean value){
		m_PreselectCbos=value;
	}

	/**
	 * @return String with combo-box like this:
	 * &lt;select name="cboSemesterName"&gt;&lt;option value=[name] [selected]&gt;[name-f]&lt;/option&gt;, where
	 * [name] is -- beginning from m_inYearRangeMinus to current year -- ss19xy | ws19xy/19zz,
	 * [name-f] is SS 19xy | WS 19xy/19zz,
	 * [selected] occurs where the semester equals the currentsemester.
	 **/
	public String getSemesterNameCbo(){

	   String strReturn = "<select name=\"" + m_strSemesterCbo + "\">\n";
	   String strSummer = "";
	   String strWinter = "";

	   for(int ii=(m_intYear-m_intYearRangeMinus); ii<= m_intYear; ii++){

		   strSummer	 = "ss" + ii;
		   strWinter	 = "ws" + ii + "/" + (ii+1);

		   strReturn	+= "<option " + ((strSummer.equals(this.getSemesterName())) ? "selected" : "") + " value=\"" + strSummer + "\">SS " + ii + "</option>\n" +
		   				   "<option " + ((strWinter.equals(this.getSemesterName())) ? "selected" : "") + " value=\"" + strWinter + "\">WS " + ii + "/" + (ii+1) + "</option>\n";
	   }

	   return strReturn + "</select>";
	}

	/**
	 * Utility to get semester name of date.
	 * (Changed Sept 2008, due to changes in university's semester scheduling)
	 * @return String representing the semester, like 'ss2004, ws2003/2004.'
	 **/
	public String getSemesterName(){

		if(m_strSemesterName.equals("")){

			// summer semester (month between 2 and 9):
			if( (this.m_intMonth > 2) && (this.m_intMonth < 9) ) m_strSemesterName = "ss" + this.m_intYear;

			// winter semester: which years?
			if(this.m_intMonth <= 2 ) m_strSemesterName = "ws" + (this.m_intYear - 1) + "/" + this.m_intYear;
			if(this.m_intMonth >= 9) m_strSemesterName = "ws" + this.m_intYear       + "/" + (this.m_intYear + 1);
		}

		return m_strSemesterName;
	}

	/**
	 * @return 20081 f�r Sommersemester 2008, 20082 f�r WS 2008/09
	 */
	public int getSemesterNameHIS(){
		int iIndicator=-1;
		if( (this.m_intMonth > 2) && (this.m_intMonth < 9) )
			iIndicator=1;
		else
			iIndicator=2;
		return (m_intYear*10) + iIndicator;
	}
	
	/**
	 * Utility to get semester name of date in UnivIS-format.
	 * @return String representing the semester, like '2004s' (summer term 2004), 2004w (winter termin 2004/2005) etc.
	 **/
	public String getSemesterNameUnivIS(){

		if(m_strSemesterNameUnivIS.equals("")){

			// summer semester (month between 3 and 10):
			if( (this.m_intMonth > 2) && (this.m_intMonth < 9) ) m_strSemesterNameUnivIS = this.m_intYear + "s";

			// winter semester: which years?
			if(this.m_intMonth <= 2 ) m_strSemesterNameUnivIS = (this.m_intYear - 1) + "w";
			if(this.m_intMonth >= 9) m_strSemesterNameUnivIS = this.m_intYear       + "w";
		}

		return m_strSemesterNameUnivIS;
	}

	/**
	 * @return String with a representation of the semester name in one of the following
	 * languages: German and English. (Only options right now)
	 * @param strLanguage: "de" for German, "en" for English. Default is English.
	 **/
	public String getSemesterNameTranscript(String strLanguage){
		String strReturn = "";

		int intLanguage = 1;
		if(strLanguage.equals("de")) intLanguage=2;

		// summer semester (month between 3 and 10):
		if( (this.m_intMonth > 2) && (this.m_intMonth < 9) ) strReturn = m_strSummerTerm[intLanguage] + " " + this.m_intYear;

		// winter semester: which years?
		if(this.m_intMonth <= 2 ) strReturn  = m_strWinterTerm[intLanguage] + " " +  (this.m_intYear - 1) + "/" + this.m_intYear;
		if(this.m_intMonth >= 9) strReturn += m_strWinterTerm[intLanguage] + " " +  this.m_intYear        + "/" + (this.m_intYear + 1);

		return strReturn;
	}

	/**
	 * @return semester start (for use in SQL query), in ISO format.
	 **/
    public String getSemesterStart(){
		String strReturn = "";

		// summer semester (month between 3 and 10):
		if( (this.m_intMonth > 2) && (this.m_intMonth < 9) ) strReturn = this.m_intYear + "-03-01";

		// winter semester: which years?
		if(this.m_intMonth <= 2 ) strReturn  = (this.m_intYear - 1) + "-9-01";
		if(this.m_intMonth >= 9) strReturn += this.m_intYear       + "-9-01";

		return strReturn;
	}

	/**
	 * @return semester end (for use in SQL query), in ISO format.
	 **/
    public String getSemesterEnd(){
		String strReturn = "";

		// summer semester (month between 3 and 10):
		if( (this.m_intMonth > 2) && (this.m_intMonth < 9) ) strReturn = this.m_intYear + "-08-31";

		// winter semester: which years?
		if(this.m_intMonth <= 2 ) strReturn  = (this.m_intYear) + "-02-28";
		if(this.m_intMonth >= 9) strReturn += (this.m_intYear + 1) + "-02-28";

		return strReturn;
	}

    /**
     * <pre>
     * Like java.util.Date.compareTo:
     * 
     * the value 
     * 	0 if the argument Date is equal to this Date; 
     *  a value less than 0 if this Date is before the Date argument; 
     *  and a value greater than 0 if this Date is after the Date argument.
     * </pre>
     * @version 6-15-01 (June 2006)
     * @param dtm
     * @return
     */
    public int compareTo(HtmlDate dtm){
		java.util.Date 		dtm1			=			null;
		java.util.Date 		dtm2			=			null;
		SimpleDateFormat 	sdf				=new 		SimpleDateFormat ( "dd.MM.yyyy" );

		try{
			dtm1 = sdf.parse( getDay() + "." + getMonth() + "." + getYear() );
			dtm2 = sdf.parse( dtm.getDay() + "." + dtm.getMonth() + "." + dtm.getYear());
			
		}catch(Exception eDate){return -4711;}
		return dtm1.compareTo(dtm2);
    }
    
	/**
	 * Is the currently loaded Date before the one handed in as parameter?
	 * @throws ERR_DATE_FORMAT_UNREADABLE, if unreadable.
     * @param dtm date to check current date against.
     * @return
     * @throws Exception
     */
    public boolean isBefore(HtmlDate dtm) throws Exception{
		try{
		  	  return isDateSequence(getGermanDate(), dtm.getGermanDate());
		}catch(Exception eIsFutureDate){throw new Exception(String.valueOf(ERR_DATE_FORMAT_UNREADABLE));}
    }
    
	/**
	 * Is the currently loaded Date in the future?
	 * @throws ERR_DATE_FORMAT_UNREADABLE, if unreadable.
	 **/
	public boolean isFutureDate() throws Exception{
		try{
	  	  return this.isFutureDate(this.m_strGermanDate);
	    }catch(Exception eIsFutureDate){throw new Exception(String.valueOf(ERR_DATE_FORMAT_UNREADABLE));}
	}

	/**
	 * @return dd.MM.yyyy
	 */
	public String getGermanDate(){
		return m_strGermanDate;
	}
	
	/**
	 * Date-representation according to locale
	 * @param strIsoDate_IN ('yyyy-MM-dd')
	 * @return Localized Date as String (java.text.DateFormat.MEDIUM).
	 * @throws ERR_DATE_FORMAT_UNREADABLE, if unreadable.
	 **/
	public String shjGetLocalDate() throws Exception{
		try{
		  return shjGetLocalDate(m_dDate, m_loc);
		}catch(Exception eGetLocalDate){throw new Exception(String.valueOf(ERR_DATE_FORMAT_UNREADABLE));}
	}

	/**
	 * Returns a String like: select name="cboDay"&gt;--options --&lt;/select name="txtDay"&gt;.
	 * &lt;select name="cboMonth"&gt;--options --&lt;/select name="txtDay"&gt;.
	 * &lt;select name="cboYear"&gt;--options --&lt;/select name="txtDay"&gt;
	 * The names of the input boxes are only the default-values of course. The "--options --" are added, of course.
	 * All options have a preselected '----' as default, with value 0.
	 **/
	public String getDatesCombo(){
		return
		  "<select name=\"" + m_strCboDay + "\"><option selected value=\"0\">---</option>" + this.getDaysCombo() + "</select>." +
		  "<select name=\"" + m_strCboMonth + "\"><option selected value=\"0\">---</option>" + this.getMonthsCombo() + "</select>." +
		  "<select name=\"" + m_strCboYear + "\"><option selected value=\"0\">---</option>" + this.getYearsCombo() + "</select>.";
	}

	/**
	 * Returns a String like: &lt;td class=tcellstyle"&gt;&lt;select name="cboDay"&gt;--options --&lt;/select name="txtDay"&gt;&lt;/td&gt;
	 * &lt;td class=tcellstyle"&gt;&lt;select name="cboMonth"&gt;--s-options --&lt;/select name="txtDay"&gt;&lt;/td&gt;
	 * &lt;td class=tcellstyle"&gt;&lt;select name="cboYear"&gt;--s-options --&lt;/select name="txtDay"&gt;&lt;/td&gt;
	 * The names of the input boxes are only the default-values of course. The "--s- options --" are added, of course.
	 * All options have a preselected value according to this.Date.
	 **/
	public String getDateCombo(){
		if(m_bUnparsable) return getDatesCombo();
		return
		  "<select name=\"" + m_strCboDay + "\">" + this.getDayCombo() + "</select>" +
		  "<select name=\"" + m_strCboMonth + "\">" + this.getMonthCombo() + "</select>" +
		  "<select name=\"" + m_strCboYear + "\">" + this.getYearCombo() + "</select>";
	}

	/**
	 * Returns a String like: &lt;td class=tcellstyle"&gt;&lt;select name="cboDay"&gt;--options --&lt;/select name="txtDay"&gt;&lt;/td&gt;
	 * &lt;td class=tcellstyle"&gt;&lt;select name="cboMonth"&gt;--options --&lt;/select name="txtDay"&gt;&lt;/td&gt;
	 * &lt;td class=tcellstyle"&gt;&lt;select name="cboYear"&gt;--options --&lt;/select name="txtDay"&gt;&lt;/td&gt;
	 * The names of the input boxes are only the default-values of course. The "--options --" are added, of course.
	 * All options have a preselected '----' as default, with value 0.
	 **/
	public String getDatesComboT(){
		return
		  "<td class=\"tcellstyle\"><select name=\"" + m_strCboDay + "\"><option selected value=\"0\">---</option>" + this.getDaysCombo() + "</select></td>" +
		  "<td class=\"tcellstyle\"><select name=\"" + m_strCboMonth + "\"><option selected value=\"0\">---</option>" + this.getMonthsCombo() + "</select></td>" +
		  "<td class=\"tcellstyle\"><select name=\"" + m_strCboYear + "\"><option selected value=\"0\">---</option>" + this.getYearsCombo() + "</select></td>";
	}

	/**
	 * Returns a String like: &lt;td class=tcellstyle"&gt;&lt;select name="cboDay"&gt;--options --&lt;/select name="txtDay"&gt;&lt;/td&gt;
	 * &lt;td class=tcellstyle"&gt;&lt;select name="cboMonth"&gt;--s-options --&lt;/select name="txtDay"&gt;&lt;/td&gt;
	 * &lt;td class=tcellstyle"&gt;&lt;select name="cboYear"&gt;--s-options --&lt;/select name="txtDay"&gt;&lt;/td&gt;
	 * The names of the input boxes are only the default-values of course. The "--s- options --" are added, of course.
	 * All options have a preselected value according to this.Date.
	 **/
	public String getDateComboT(){
		return
		  "<td class=\"tcellstyle\"><select name=\"" + m_strCboDay + "\">" + this.getDayCombo() + "</select></td>" +
		  "<td class=\"tcellstyle\"><select name=\"" + m_strCboMonth + "\">" + this.getMonthCombo() + "</select></td>" +
		  "<td class=\"tcellstyle\"><select name=\"" + m_strCboYear + "\">" + this.getYearCombo() + "</select></td>";
	}

	/**
	 * Returns a String like: &lt;option value = [val]&gt;month&lt;/option&gt;<br />
	 * Where: <br />
	 * [val] is from 1 to 31.
	 * Note: there is no preselected entry, and no null-entry. This must be added in HTML/JSP.
	 **/
	public String getDaysCombo(){
		return this.getDayCombo( false );
	}

	/**
	 * Returns a String like: &lt;option [selected] value = [val]&gt;month&lt;/option&gt;<br />
	 * Where: <br />
	 * [val] is from 1 to 31.<br />
	 * [selected] is set if this.Day is equal to this option.
	 **/
	public String getDayCombo(){
		return this.getDayCombo( m_PreselectCbos );
	}

	/**
	 * Returns a String like: &lt;option value = [val]&gt;month&lt;/option&gt;<br />
	 * Where: <br />
	 * [val] is 1 for January, 2 for February asf., until 12 for December.
	 * The name of the month is currently NOT localized (sorry, too busy).
	 * Note: there is no preselected entry, and no null-entry. This must be added in HTML/JSP.
	 * Note: don't confuse the two methods 'getMonthsCombo' (with months in plural, which retrieves no preselects) and 'getMonthCombo' which returns a combo-String that has the Month of this object preselected.
	 **/
	public String getMonthsCombo(){
		return this.getMonthCombo( false );
	}

	/**
	 * Returns a String like: &lt;option [selected] value = [val]&gt;month&lt;/option&gt;<br />
	 * Where: <br />
	 * [selected] is set if the month equal to the month of this object.
	 * [val] is 1 for January, 2 for February asf., until 12 for December.
	 * The name of the month is currently NOT localized (sorry, too busy).
	 * Note: there is no preselected entry, and no null-entry. This must be added in HTML/JSP.
	 **/
	public String getMonthCombo(){
		return this.getMonthCombo( m_PreselectCbos );
	}

	/**
	 * @returns a String like: &lt;option value = [val]&gt;[val]&lt;/option&gt;<br />
	 * Where: <br />
	 * [val] is just the year, it runs from min to max (see class vars for details).
	 * @param blnPreselect: switch 'selected' on (true) of off (false).
	 **/
	public String getYearsCombo(){
		return this.getYearCombo( false );
	}

	/**
	 * @returns a String like: &lt;option [selected] value = [val]&gt;[val]&lt;/option&gt;<br />
	 * Where: <br />
	 * [selected] is used for the option that matches the year of _this_ object's year,<br />
	 * [val] is just the year, it runs from min to max (see class vars for details).
	 **/
	public String getYearCombo(){
		return this.getYearCombo( m_PreselectCbos );
	}

	/**
	 * @returns a String like: &lt;option [selected] value = [val]&gt;[val]&lt;/option&gt;<br />
	 * Where: <br />
	 * [selected] is used for the option that matches the year of _this_ object's year,<br />
	 * [val] is just the year, it runs from min to max (see class vars for details).
     * @param intMinus: how many years back from this object do you want the combo to start?
     * @param intPlus: how many years into the future do you want this object to reach?
	 **/
	public String getYearCombo(int intMinus, int intPlus){

		String	strReturn = "";

		m_intMinYear   = m_intYear - intMinus;
		m_intMaxYear   = m_intYear + intPlus;

		strReturn	   = this.getYearCombo( m_PreselectCbos );

		m_intMinYear   = m_intYear - m_intYearRangeMinus;
		m_intMaxYear   = m_intYear + m_intYearRangePlus;

		return strReturn;

	}

	/**
	 * @returns a String like: &lt;option [selected] value = [val]&gt;month&lt;/option&gt;<br />
	 * Where: <br />
	 * [selected] is set if blnPreselect is true, and the month equal to the month of this object.
	 * [val] is 1 for January, 2 for February asf., until 12 for December.
	 * The name of the month is currently NOT localized (sorry, too busy).
	 * @param blnPreselect: switch 'selected' on (true) of off (false).
	 **/
	private String getMonthCombo(boolean blnPreselect){
		String strReturn	= "";

		for( int ii=0; ii<12; ii++ ){
			strReturn += "<option " + (((blnPreselect)&&(ii == (m_intMonth-1))) ? "selected" : "" ) + " value=\"" + ( ii + 1 ) + "\">" + m_strMonths[ii] + "</option>\n";
		}

		return strReturn;
	}

	/**
	 * @returns a String like: &lt;option [selected] value = [val]&gt;[val]&lt;/option&gt;<br />
	 * Where: <br />
	 * [selected] is set if blnPreselect is true, and the year equal to the year of this object.
	 * [val] is just the year.
	 * @param blnPreselect: switch 'selected' on (true) of off (false).
	 **/
	private String getYearCombo(boolean blnPreselect){
		String strReturn	= "";

		for( int ii = m_intMaxYear; ii>=m_intMinYear; ii--) {
			strReturn += "<option " + ( ((blnPreselect)&&(ii==m_intYear)) ? "selected" : "" ) + " value=\"" + ii + "\">" + ii + "</option>\n";
		}

		return strReturn;
	}

	/**
	 * @returns a String like: &lt;option [selected] value = [val]&gt;[val]&lt;/option&gt;<br />
	 * Where: <br />
	 * [selected] is set if blnPreselect is true, and the day equal to the day of this object.
	 * [val] is from 1 to 31.
	 * @param blnPreselect: switch 'selected' on (true) of off (false).
	 **/
	private String getDayCombo(boolean blnPreselect){
		String strReturn	= "";

		for( int ii = 1; ii<=31 ; ii++) {
			strReturn += "<option " + ( ((blnPreselect)&&(ii==m_intDay)) ? "selected" : "" ) + " value=\"" + ii + "\">" + ii + "</option>\n";
		}

		return strReturn;

	}

	/**
	 * Year of this HtmlDate.
	 * @return Year
	 */
	public int getYear(){
		return m_intYear;
	}
	
	/**
	 * Month of this HtmlDate.
	 * @return Month
	 */
	public int getMonth(){
		return m_intMonth;
	}
	
	/**
	 * Day of this HtmlDate.
	 * @return Day
	 */
	public int getDay(){
		return m_intDay;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return m_intDay + "." + m_strMonths[m_intMonth-1] + "." + m_intYear;
	}
	
	/**
	 * Method checks m_strDate for plausibility. It is plausible, if <br />(1) it can be transformed into
	 * a German date-format which is then used to initialize intDay, intMonth, intYear, and <br />(2) it
	 * is within a certain range to 'now.' This range is specified through 'YearRangePlus' and 'YearRangeMinus'.
	 * Default is 50 for both.
	 * @throws Exceptions if the date is 'too far in the future, or too far in the past,' that is to say
	 * if the year is outside the boundaries. .
	 **/
	private void parseDate() throws Exception{
		//Declarations
		int				intCurrentYear 	= 		0;

		//java.util.Date 	dtmNow			=		null;
		//dtmNow							= new 	java.util.Date();
		SimpleDateFormat sdfGerman 		= new SimpleDateFormat ("dd.MM.yyyy");
		m_strGermanDate	 				= shjGetGermanDate( m_dDate );

		if ( m_strGermanDate.equals("") ) throw new Exception(String.valueOf(ERR_DATE_FORMAT_UNREADABLE));
		
		//Init
		m_strToday 		 				= sdfGerman.format( new java.util.Date() );

		//Retrieve numeric values, check plausibility.
		m_intDay 	   = Integer.parseInt ( m_strGermanDate.substring(0, 2) );
		m_intMonth 	   = Integer.parseInt ( m_strGermanDate.substring(3, 5) );
		m_intYear 	   = Integer.parseInt ( m_strGermanDate.substring(6) 	);

		m_dDate = new Date(g_ISO_DATE_FORMAT.parse(m_intYear + "-" + m_intMonth + "-" + m_intDay).getTime());
		intCurrentYear = Integer.parseInt ( m_strToday.substring(6)			);
		
		m_intMinYear   = intCurrentYear - m_intYearRangeMinus;
		m_intMaxYear   = intCurrentYear + m_intYearRangePlus;

		if ( m_intYear > m_intMaxYear ) throw new Exception(String.valueOf(ERR_DATE_TOO_FAR_IN_FUTURE));
		if ( m_intYear < m_intMinYear ) throw new Exception(String.valueOf(ERR_DATE_TOO_FAR_IN_PAST));

		m_strSummerTerm[1]	= "Summer Term";
		m_strSummerTerm[2]	= "Sommersemester";

		m_strWinterTerm[1]	= "Winter Term";
		m_strWinterTerm[2]	= "Wintersemester";
		
		m_bUnparsable=false;
	}

	/**
	 * <pre>
	 * Using Html forms, which name does the select box have 
	 * that contains the day? [Default is: cboDay]
	 * </pre>
	 * @see #readRequest(HttpServletRequest)
	 * @param value
	 */
	public void setCboDayName(String value){
		m_strCboDay = value;
	}

	/**
	 * <pre>
	 * Using Html forms, which name does the select box have 
	 * that contains the month? [Default is: cboMonth]
	 * </pre>
	 * @see #readRequest(HttpServletRequest)
	 * @param value
	 */
	public void setCboMonthName(String value){
		m_strCboMonth = value;
	}	
	
	/**
	 * <pre>
	 * Using Html forms, which name does the select box have 
	 * that contains the year? [Default is: cboYear]
	 * </pre>
	 * @see #readRequest(HttpServletRequest)
	 * @param value
	 */
	public void setCboYearName(String value){
		m_strCboYear = value;
	}	
	
	/**
	 * <pre>
	 * If Html forms use combo-names that differ from 
	 * the norm (cboDay, cboMonth, cboYear), you can 
	 * do this:
	 * 
	 * <Code>
	 * HtmlDate dtm = new HtmlDate(Locale.GERMANY);
	 * dtm.setCboMonthName("Monat");
	 * dtm.readRequest(request);
	 * </Code>
	 * 
	 * </pre>
	 * @version 6-12 (Mar 2006)
	 * @param r
	 * @throws Exception
	 */
	public void readRequest(HttpServletRequest r) throws Exception{
		m_strDate = r.getParameter(this.m_strCboYear) + "-" + r.getParameter(this.m_strCboMonth) + "-" + r.getParameter(this.m_strCboDay);
		m_dDate   = new Date(g_ISO_DATE_FORMAT.parse(m_strDate).getTime());
		this.parseDate();
	}
	
	/**
	 * Changes the current date.
	 * If you want the current date set to yesterday, call
	 * .addDays( -1 );
	 * If you want tomorrow, add +1.
	 * @param iDays
	 * @throws Exception
	 */
	public void addDays(int iDays) throws Exception{
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.DATE, iDays);
		
		SimpleDateFormat sdfISO 		= new SimpleDateFormat ("yyyy-MM-dd");
		m_strDate 		 				= sdfISO.format( cal.getTime() );
		
		this.parseDate();
	}
	
	/**
	 * Constructs a HtmlDate of today.
	 * The date is checked for plausibility; if implausible, an Exception is thrown.
	 * @param loc: Location
	 **/
	public HtmlDate(Locale loc) throws Exception{
		m_dDate							= g_TODAY;
		m_strDate 		 				= g_ISO_DATE_FORMAT.format( m_dDate );
		m_loc							= loc;

		this.parseDate();
	}

	/**
	 * Constructs a HtmlDate with the given date (format 'yyyy-MM-dd').
	 * The format is checked for plausibility; if implausible, an Exception is thrown.
	 * @param strISODate: String in format 'yyyy-MM-dd.'
	 * @param loc: Location
	 **/
	public HtmlDate(String strISODate, Locale loc) throws Exception{
		m_strDate = strISODate;
		m_dDate   = new Date(g_ISO_DATE_FORMAT.parse(strISODate).getTime());
		m_loc	  = loc;

		this.parseDate();
	}
	
	/**
	 * Constructs a HtmlDate with the given java.sql.Date
	 * The format is checked for plausibility; if implausible, an Exception is thrown.
	 * @param strISODate: String in format 'yyyy-MM-dd.'
	 * @param loc: Location
	 **/
	public HtmlDate(java.sql.Date dtm, Locale loc) throws Exception{
		m_strDate = g_ISO_DATE_FORMAT.format(dtm); //strISODate;
		m_dDate   = new Date(dtm.getTime());
		m_loc	  = loc;

		this.parseDate();
	}

	/**
	 * Constructs a HtmlDate with the given date in the request.
	 * The format is checked for plausibility; if implausible, an Exception is thrown.
	 * @param r: the request must contain the default-params "cboDay," "cboMonth," and "cboYear."
	 * @param loc: Location
	 **/
	public HtmlDate(HttpServletRequest r, Locale loc) throws Exception{
		m_strDate = r.getParameter(this.m_strCboYear) + "-" + r.getParameter(this.m_strCboMonth) + "-" + r.getParameter(this.m_strCboDay);
		m_dDate   = new Date(g_ISO_DATE_FORMAT.parse(m_strDate).getTime());
		m_loc	  = loc;

		this.parseDate();
	}

	/**
	 * Same as other constructor, only never throws Exception. 
	 * If the date is unparseable, the flag 'm_bUnparsable' is 
	 * set, and the method getDateCbo() returns the value from 
	 * getDatesCbo() (which is to say: no preselected date).
	 * @param r
	 * @param strDayCbo
	 * @param strMonthCbo
	 * @param strYearCbo
	 * @param loc
	 * @param bIgnoreException
	 */
	public HtmlDate(HttpServletRequest r, String strDayCbo, String strMonthCbo, String strYearCbo, Locale loc, boolean bIgnoreException){
		try{
			m_strDate = r.getParameter(strYearCbo) + "-" + r.getParameter(strMonthCbo) + "-" + r.getParameter(strDayCbo);
			m_dDate   = new Date(g_ISO_DATE_FORMAT.parse(m_strDate).getTime());
			m_loc	  = loc;
			this.parseDate();
		}catch(Exception eUnparsable){m_bUnparsable=true;}
	}
	
	/**
	 * Constructs a HtmlDate with the given date in the request.
	 * The format is checked for plausibility; if implausible, an Exception is thrown.
	 * @param r: the request must contain the default-params for day, month and year separately.
	 * @param strDayCbo: name of the parameter that contains the value of the day,
	 * @param strMonthCbo: name of the parameter that contains the value of the month,
	 * @param strYearCbo: name of the parameter that contains the value of the year,
	 * @param loc: Location
	 **/
	public HtmlDate(HttpServletRequest r, String strDayCbo, String strMonthCbo, String strYearCbo, Locale loc) throws Exception{
		m_strDate = r.getParameter(strYearCbo) + "-" + r.getParameter(strMonthCbo) + "-" + r.getParameter(strDayCbo);
		m_dDate   = new Date(g_ISO_DATE_FORMAT.parse(m_strDate).getTime());
		m_loc	  = loc;

		this.parseDate();
	}

}