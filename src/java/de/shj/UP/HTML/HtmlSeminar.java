/*
 * Software-Developement H. Jakubzik, Licence Version 1.0
 *
 *
 * Copyright (c) 2003 Heiko Jakubzik.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by
 *        H. Jakubzik (http:////www.heiko-jakubzik.de//)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "shj" and "SignUp" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact heiko.jakubzik@t-online.de.
 *
 * 5. Products derived from this software may not be called "e~vv,"
 *    nor may "e~vv" appear in their name, without prior written
 *    permission of H. Jakubzik.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
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
 * @todo
 * line		description
 * 102,274	Variable m_PLEASE_CHOOSE_LANG must be included in RessourceBundle for multi-language support
 *
 * code-structure:
 *
 * 1. declarations
 *
 * 2. properties	(option-selected-settings, SeminarID)
 *
 * 3. public cbo-strings (Pruefung, Coursetype)
 *
 * 4. private cbo-inits
 *
 * 5. public html-strings (tables),
 *
 * 6. private resultsets (helpers for cbos),
 *
 * 7. constructor
 *
 * @version 6-02-01 (auto-coded)
 * change date              change author           change description
 * Dec 2003					hj						creation
 * Jan 06, 2003				h. jakubzik				added getLeistungCbo and dependant methods.
 *
 * March 1st, 2004			h. jakubzik				added getDozentCbo and dependant methods.
 * April 10,  2004			h. jakubzik				added getModuleCbo and dependant methods.
 *													adapted getPruefungCbo to new schema (old method did not work anyway).
 *													added getFachCbo and dependant methods.
 * May   13,  2004			h. jakubzik				changed DozentCbo from firstname-lastname to lastname, firstname.
 *
 * Oct	 11,  2004		    h. jakubzik				getCoursesExamRegRST made public (for cl implementation)
 *
 * Nov   07,  2004			h. jakubzik				getCoursesExamRegRST liefert jetzt k.strKursTitel,
 *													getCoursesExamRegHtml liefert jetzt k.strKursTitel
 *
 * Nov   08,  2004			h. jakubzik				.getCourseTypesExamRegRST sortiert nach KurstypBezeichnung
 * 
 * Jan	 22,  2005			h. jakubzik				.getNoteCbo: forbid preselect values of a grade with 
 * 													a NoteID of 0. (Otherwise, defaults may be set by accident).
 * 
 * May    6, 2005			h. jakubzik				made the class extend the (newly created class) logic.SeminarData
 * 
 * May 	  7, 2005			h. jakubzik				.getBAMAFachCbo()
 * 
 * May 	 24, 2005			h. jakubzik				eliminated overridden lngSeminarID. Major Bugfix.
 * 
 * Aug	 08, 2005			h. jakubzik				.getGruppeCbo()
 * 
 * Sept. 14, 2005			h. jakubzik				.getPruefungCboShorter()
 * 
 * March  1, 2006			h. jakubzik				.m_blnNoteShowLocalName
 * 
 * April 14, 2006			j. jakubzik				.getToday()
 * 
 * May 23, 2008				h. jakubzik				.getLastZUVUpdate()
 * 
 * TODO: code and comments need refactoring; especially the cbo-methods 
 * use database-connections over and over again, which stems from pre-
 * cookie programming. 
 **/

package de.shj.UP.HTML;

import java.sql.Date;
import java.sql.ResultSet;
import java.util.Locale;

/**
 * <pre>
 * Small helper-class around Html-display of 
 * seminar-related items in jsp frontend.
 * 
 * Mainly, this class is supposed to help with 
 * combo-boxes ('Cbos') and their
 * possible preselected options.
 * 
 * As with all classes in package 'signUp.HTML,' 
 * it is required that you hand
 * the Locale over, so that language-specific 
 * settings can be made. Currently, however,
 * only the dates are localized. 
 * 
 * There's also 'm_PLEASE_CHOOSE_LANG' to be taken care of,
 * and some of the tables have to be #hack ed. (see source).
 * 
 * As in all classes of this package 
 * (signUp.HTML), table-headers are formatted with
 * css 'class="theaderstyle," and regulare fields 
 * with 'class="tcellstyle".'
 **/
public class HtmlSeminar extends de.shj.UP.logic.SeminarData{

	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 1.   D E C L A R A T I O N S
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------

	private static final long serialVersionUID = 1L;

	// #hack
	private static final String m_PLEASE_CHOOSE_LANG="--bitte auswählen--";

	private String m_strStudGruppeCboCache="";
	private String m_strCourseTypeCbo="#";
	private long m_lngCourseTypePreselect=-1;
	private String m_strErr;
	private Locale m_Locale;
	private String m_sLastZUVUpdate=null;
	
	private String m_sAutoSuggestLeistungArray = "#";
	private String m_sAutoSuggestDozentArray   = "#";

	private Date m_dLastZUVUpdate;

	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 2.   P R O P E R T I E S
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------

	/**
	 * @return Date of last ZUV Update, String, ISO-Format (yyyy-mm-dd)
	 * @throws Exception
	 */
	public Date getLastZUVUpdate2() throws Exception{
		if(m_sLastZUVUpdate == null) {
                    ResultSet rTmp = sqlQuery("select max(\"dtmStudentZUVUpdate\") as \"datum\" from \"tblBdStudent\";");
                    rTmp.next();
		    m_sLastZUVUpdate = rTmp.getString("datum");
		}
		if(m_dLastZUVUpdate==null) m_dLastZUVUpdate = new java.sql.Date(g_ISO_DATE_FORMAT.parse(m_sLastZUVUpdate).getTime());
		return m_dLastZUVUpdate;
	}


	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 3.   P U B L I C   C B O - S T R I N G S
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 4.   P R I V A T E  C B O - I N I T S
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------


	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 5.   P U B L I C   H T M L-S T R I N G S   (TABLES)
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------



	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 6.   p r i v a t e   r e s u l t s e t s (SQL)
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------



	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 7.   C O N S T R U C T O R
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------

	/**
	 * Instantiate object with ID of seminar and the current Locale. Locale is needed for formatting of dates only.
	 * @param lngSeminarID_IN Id of Seminar to instantiate; must be unique (otherwise: Exception)
	 * @param locale Locale 
	 * @throws Exception DB-connection erroneous, Seminar with this Id not found, or more than 
	 * one Seminar with this Id found.
	 */
	public HtmlSeminar(long lngSeminarID_IN, Locale locale) throws Exception{
		init(lngSeminarID_IN, locale);
	}

	/**
	 * Constructor for use as Bean. Please call init next.
	 * @see #init(long, Locale)
	 */
	public HtmlSeminar(){
	}
	
	/**
	 * Calls super.initByRst with a ResultSet that contains the row from tblSdSeminar where the SeminarID 
	 * is equal to this.getSeminarID.<br />
	 * Since in the table tblSdSeminar the SeminarID does not have to be unique, an Exception is 
	 * generated when more that one seminar with a given Id is found.
	 * @throws Exception Either db-connection is erroneous, or there's more or less than one 
	 * Seminar with the current SeminarID.
	 */
	private void initSuper() throws Exception{
		ResultSet rst = sqlQuery("select * from \"tblSdSeminar\" where \"lngSeminarID\"=" + getSeminarID() + ";");
		if(rst.next()){
			super.initByRst(rst);
		}
		
		try {
			if(rst.next()){
				rst.close();
				throw new Exception("Cannot construct Seminar-object, SeminarID '" + getSeminarID() + "' is not unique.");
			}
		} catch (Exception e) {	}
		rst.close();
	}
	
	/**
	 * Initializer for Bean-Construction.
	 * Since version 6.01.06 (May 7, 2005), this initializes 
	 * the entire Seminar-object calling .initSuper().
	 * @param lngSeminarID_IN
	 * @param locale
	 */
	public void init(long lngSeminarID_IN, Locale locale) throws Exception {
		this.setSeminarID(lngSeminarID_IN);
		initSuper();
		this.m_Locale=locale;
		this.disconnect();
	}

  }//end class

