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


import de.shj.UP.util.ResultSetSHJ;

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
	public Date getLastZUVUpdate() throws Exception{
		if(m_sLastZUVUpdate == null) {
                    ResultSet rTmp = sqlQuery("select max(\"dtmStudentZUVUpdate\") as \"datum\" from \"tblBdStudent\";");
                    rTmp.next();
		    m_sLastZUVUpdate = rTmp.getString("datum");
		}
		if(m_dLastZUVUpdate==null) m_dLastZUVUpdate = new java.sql.Date(g_ISO_DATE_FORMAT.parse(m_sLastZUVUpdate).getTime());
		return m_dLastZUVUpdate;
	}


	/**
	 * <pre>
	 * If CourseTypeCboPreselect is 
	 * set to a CourseTypeID,
	 * then this CourseType is the preselected 
	 * item in all related Html-combos.
	 * 
	 * ResetCourseTypeCbo makes sure that no 
	 * Course Type is preselected.
	 * Both methods influence "getCourseTypeCbo" 
	 * in this class.
	 * </pre>
	 **/
	private void resetCourseTypeCboPreselect(){
		this.m_lngCourseTypePreselect=-1;
	}

	/**
	 * <pre>
	 * If CourseTypeCboPreselect is set to a CourseTypeID,
	 * then this CourseType is the preselected item in all 
	 * Html-combos.
	 * 
	 * ResetCourseTypeCbo makes sure that no Course Type is preselected.
	 * Both methods influence "getCourseTypeCbo" in this class.
	 * </pre>
	 * @param lngCourseTypeID_IN: ID of course-type that is to be the presection.
	 **/
	private void setCourseTypeCboPreselect(long lngCourseTypeID_IN){
		this.m_lngCourseTypePreselect=lngCourseTypeID_IN;
	}


	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 3.   P U B L I C   C B O - S T R I N G S
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------

	/**
	 * @returns a String like &lt;option value=[val]&gt;[name]&lt;/option&gt;..., where <br />
	 * [val] is the lngStudGruppeID of the subject,<br />
	 * [name] is the group's name: strStudGruppeBezeichnung.<br />
	 * The combo is ordered by the GruppeBezeichnung, ascending.<br />
	 * @throws if connection to database is erroneous.
	 **/
	public String getStudGruppeCbo() throws Exception{
		if(m_strStudGruppeCboCache.equals("#")) m_strStudGruppeCboCache=this.getStudGruppeCbo(-1, false);
		return m_strStudGruppeCboCache;
	}	
	
	/**
	 * @returns a String like &lt;option value=[val]&gt;[name]&lt;/option&gt;..., where <br />
	 * [val] is the lngStudGruppeID of the subject,<br />
	 * [name] is the group's name: strStudGruppeBezeichnung.<br />
	 * The combo is ordered by the GruppeNummer, ascending.<br />
	 * lngGruppeID is preselected.
	 * @throws if connection to database is erroneous.
	 **/
	public String getStudGruppeCbo( long lngGruppeID ) throws Exception{
		return this.getStudGruppeCbo(lngGruppeID, true);
	}
	
	/**
	 * @returns a String like &lt;option value=[val]&gt;[name]&lt;/option&gt;..., where <br />
	 * [val] is the lngFachID of the subject,<br />
	 * [name] is the subject's intFachID: strFachBeschreibung.<br />
	 * The combo is ordered by the Fach description, ascending.<br />
	 * This Combo is restricted to FachIDs of values x with SeminarID*100.000 <= x < (SeminarId+1)*100.000
	 * @throws if connection to database is erroneous.
	 **/
	public String getBAMAFachCbo( long lngFachID ) throws Exception{
		return this.getBAMAFachCbo(lngFachID,true);
	}	
	
	/**
	 * @returns a String like &lt;option value=[val]&gt;[name]&lt;/option&gt;..., where <br />
	 * [val] is the lngFachID of the subject,<br />
	 * [name] is the subject's intFachID: strFachBeschreibung.<br />
	 * The combo is ordered by the FachID, ascending.
	 * @throws if connection to database is erroneous.
	 **/
	public String getFachCbo() throws Exception{
		return this.getFachCbo(-1, false);
	}

	/**
	 * @returns a String like &lt;option [selected] value=[val]&gt;[name]&lt;/option&gt;..., where <br />
	 * [selected] is set in the row where the FachID matches the param lngFachID,<br />
	 * [val] is the module's lngFachID,<br />
	 * [name] is the subject's intFachID: strFachBeschreibung.<br />
	 * The combo is ordered by the FachID ascending.
	 * @param lngFachID: id of the subject to mark as preselected,
	 * @throws if connection to database is erroneous.
	 **/
	public String getFachCbo(long lngFachID) throws Exception{
		return this.getFachCbo(lngFachID, true);
	}

	/**
	 * @returns a String like &lt;option value=[val]&gt;[name]&lt;/option&gt;..., where <br />
	 * [val] is the ModulID of the module,<br />
	 * [name] is the module's 'Number: Bezeichnung'.<br />
	 * The combo is ordered by the module number, ascending.
	 * @throws if connection to database is erroneous.
	 **/
	public String getModulCbo() throws Exception{
		return this.getModulCbo(-1, false);
	}

	/**
	 * @returns a String like &lt;option [selected] value=[val]&gt;[name]&lt;/option&gt;..., where <br />
	 * [selected] is set in the row where the ModulID matches the param lngModulID,<br />
	 * [val] is the module's ModulID,<br />
	 * [name] is the module's 'Number: Bezeichnung'.<br />
	 * The combo is ordered by the module number, ascending.
	 * @param lngModulID: id of the module to mark as preselected,
	 * @throws if connection to database is erroneous.
	 **/
	public String getModulCbo(long lngModulID) throws Exception{
		return this.getModulCbo(lngModulID, true);
	}

	/**
	 * @returns a String like &lt;option value=[val]&gt;[name]&lt;/option&gt;..., where <br />
	 * [val] is the DozentID of the teacher,<br />
	 * [name] is the teacher's first- and lastname.<br />
	 * The combo is ordered by the teacher's last name, ascending.
	 * @version 6-03-01 (May 15-2005): contains only teachers with a positive blnLehrend-Flag.
	 * @throws if connection to database is erroneous.
	 **/
	public String getDozentCbo() throws Exception{
		return this.getDozentCbo(-1, false);
	}

	/**
	 * @returns a String like &lt;option [selected] value=[val]&gt;[name]&lt;/option&gt;..., where <br />
	 * [selected] is set in the row where the DozentID matches the param lngDozentID,<br />
	 * [val] is the DozentID of the teacher,<br />
	 * [name] is the teacher's first- and lastname.<br />
	 * The combo is ordered by the teacher's last name, ascending.
	 * @param lngDozentID: id of the teacher to mark as preselected,
	 * @throws if connection to database is erroneous.
	 **/
	public String getDozentCbo(long lngDozentID) throws Exception{
		return this.getDozentCbo(lngDozentID, true);
	}

	/**
	 * @returns a String like &lt;option value=[val]&gt;[name]&lt;/option&gt;..., where <br />
	 * [val] is the LeistungID of the credit,<br />
	 * [name] is the credit's name.<br />
	 * The combo is ordered by [name], ascending.
	 **/
	 public String getLeistungCbo() throws Exception{
		 return this.getLeistungCbo(-1, false);
	 }

	/**
	 * @returns a String like &lt;option [selected] value=[val]&gt;[name]&lt;/option&gt;..., where <br />
	 * [selected] is set in the row where the LeistungsID matches the param lngLeistungID and blnPreselect=true,<br />
	 * [val] is the LeistungID of the credit,<br />
	 * [name] is the credit's name.<br />
	 * The combo is ordered by [name], ascending.
	 * @param lngLeistungID_IN: id of the Credit to mark as preselected (if any),
	 **/
	 public String getLeistungCbo(long lngLeistungID) throws Exception{
		 return this.getLeistungCbo(lngLeistungID, true);
	 }

	/**
	 * @returns a String like &lt;option value=[val]&gt;[name]&lt;/option&gt;..., where <br />
	 * [val] is the NoteID of the credit,<br />
	 * [name] is the grade's name (ECTS Def and Value).<br />
	 * The combo is ordered by the ECTS value, ascending.
	 * @throws Exception if connection to database erroneous.
	 **/
	 public String getNoteCbo() throws Exception{
		 return this.getNoteCbo(-1, false);
	 }

	/**
	 * @returns a String like &lt;option [selected] value=[val]&gt;[name]&lt;/option&gt;..., where <br />
	 * [selected] is set in the row where the NoteID(=GradeID) matches the param lngNoteID and blnPreselect=true,<br />
	 * [val] is the NoteID of the credit,<br />
	 * [name] is the grade's name (ECTS Def and Value).<br />
	 * The combo is ordered by the ECTS value, ascending.
	 * @param lngNoteID_IN: id of the Note (=grade) to mark as preselected (if any),
	 * @throws Exception if connection to database erroneous.
	 **/
	 public String getNoteCbo(int intNoteID) throws Exception{
		 return this.getNoteCbo(intNoteID, true);
	 }

	/**
	 * @returns a String like &lt;option value=[val]&gt;[name]&lt;/option&gt;..., where <br />
	 * [val] is the PruefungID of the exam,<br />
	 * [name] is the exam's name.<br />
	 * The combo is ordered by the [name] value, ascending.<br />
	 * Differs from @link #getPruefungCbo() in that there is no description in [name].
	 * @throws Exception if connection to database erroneous.
	 **/	 
	 public String getPruefungCboShorter() throws Exception{
	 	return getPruefungCbo(-1, false, false);
	 }
	 
	/**
	 * @returns a String like &lt;option value=[val]&gt;[name]&lt;/option&gt;..., where <br />
	 * [val] is the PruefungID of the exam,<br />
	 * [name] is the exam's name and, in brackets, the (description) PruefungBeschreibung..<br />
	 * The combo is ordered by the [name] value, ascending.
	 * @throws Exception if connection to database erroneous.
	 **/
	 public String getPruefungCbo() throws Exception{
		 return this.getPruefungCbo(-1, false, true);
	 }

	/**
	 * @returns a String like &lt;option [selected] value=[val]&gt;[name]&lt;/option&gt;..., where <br />
	 * [selected] is set in the row where the PruefungID(=ExamID) matches the param handed in, and blnPreselect=true,<br />
	 * [val] is the PruefungID,<br />
	 * [name] is the exam's name.and, in brackets, the (description) PruefungBeschreibung..<br />
	 * The combo is ordered by [name], ascending.
	 * @param lngPruefungID: id of the Exam (=Pruefung) to mark as preselected (if any),
	 * @throws Exception if connection to database erroneous.
	 **/
	 public String getPruefungCbo(long lngPruefungID) throws Exception{
		 return this.getPruefungCbo(lngPruefungID, true, true);
	 }

	/**
	 * Get HTML-representation of all CourseTypes ordered alphabetically by name of this seminar.
	 * @return HTML-representation of all coursetype in this seminar, ordered alphabetically.
	 * In case of error, a String that starts with '#' is handed back. The error is stored in 'm_strErr.'
	 * Please note that <b>this method calls 'resetCourseTypeCboPreselect.'</b>
	 **/
    public String getCourseTypeCbo(){

		this.resetCourseTypeCboPreselect();

		// initialize only if 'cache' (class-var) is empty.
		if(this.m_strCourseTypeCbo.equals("#")){
		  try{this.initCourseTypeCbo();}catch(Exception eCCbo){
			  this.m_strErr+="Can't build CourseType Combo Box. Please check your settings in database." + eCCbo.toString();
		  }
		}

		return this.m_strCourseTypeCbo;
    }//end of getCourseTypeCombo


	/**
	 * Get HTML-representation of all CourseTypes ordered alphabetically by name of this seminar. In this variant,
	 * the Html-Combo shows a preselected item (the coursetype with the ID of lngPreselectID). The preselection-id
	 * is set in this method, so you may refer to it later. In case of error, a String beginning with '#' is handed back,
	 * and the error is stored in m_strErr.
	 * @return HTML-representation of all CourseTypes ordered alphabetically by name of this seminar.
	 * @param lngPreselectID_IN: id of coursetype that you want with the attribute 'option selected' in the HTML-cbo String.
	 */
    public String getCourseTypeCbo(long lngPreselectedID_IN){

    	// Reset the CBO-thing if it is different from the 'cache':
    	if(m_lngCourseTypePreselect != lngPreselectedID_IN) m_strCourseTypeCbo = "#";
    	
		this.setCourseTypeCboPreselect(lngPreselectedID_IN);

		// initialize only if 'cache' (class-var) is empty.
		if(this.m_strCourseTypeCbo.equals("#")){
		  try{this.initCourseTypeCbo();}catch(Exception eCCbo){
			  this.m_strErr+="Can't build CourseType Combo Box. Please check your settings in database." + eCCbo.toString();
		  }
		}

		return this.m_strCourseTypeCbo;
    }//end of getCourseTypeCombo


	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 4.   P R I V A T E  C B O - I N I T S
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------

	/**
	 * @returns a String like &lt;option [selected] value=[val]&gt;[name]&lt;/option&gt;..., where <br />
	 * [selected] is set in the row where the FachID matches the param lngFachID_IN and blnPreselect=true,<br />
	 * [val] is the subject's FachID,<br />
	 * [name] is the FachID: FachBeschreibung (id and description).<br />
	 * The combo is ordered by id, ascending.
	 * @param lngFachID_IN: id of the subject to mark as preselected (if any),
	 * @param blnPreselect: if false, lngFachID_IN is disregarded altogether.
	 **/
	private String getFachCbo(long lngFachID_IN, boolean blnPreselect) throws Exception{

		ResultSetSHJ 	rst 			=     sqlQuerySHJ("SELECT " +
												  "x.\"lngSeminarID\", " +
												  "f.* " +
												"FROM \"tblSdFach\" f, \"tblSdSeminarXFach\" x " +
												"WHERE (" +
												 "(f.\"intFachID\" = x.\"intFachID\") AND " +
												 "(x.\"lngSeminarID\" = " + getSeminarID()  + " )" +
												") ORDER BY f.\"intFachID\" ASC;");
		String		strReturn		=	  "";
		long		lngFachID		= 	  -1;

		while(rst.next()){
			lngFachID			=	  rst.getInt("intFachID");
			strReturn 			+= "<option " + ( ((blnPreselect) && (lngFachID==lngFachID_IN)) ? "selected" : "" ) + " value=\"" + lngFachID + "\">" +
										lngFachID + ": " + rst.getString("strFachBeschreibung") + "</option>\n";
		}

		rst							=     null;

		return strReturn;
	}

	/**
	 * @returns a String like &lt;option [selected] value=[val]&gt;[name]&lt;/option&gt;..., where <br />
	 * [selected] is set in the row where the lngStudGruppeID matches the param lngStudGruppeID_IN and blnPreselect=true,<br />
	 * [val] is the lngStudGruppeID,<br />
	 * [name] is the StudGruppeBezeichnung.<br />
	 * The combo is ordered by StudGruppeNummer, ascending.
	 * @param lngStudGruppeID_IN: id of the group to mark as preselected (if any),
	 * @param blnPreselect: if false, lngStudGruppeID_IN is disregarded altogether.
	 **/
	private String getStudGruppeCbo(long lngStudGruppeID_IN, boolean blnPreselect) throws Exception{

		ResultSet 	rst 			=     sqlQuery("SELECT " +
												  "* " +
												"FROM \"tblSdStudGruppe\" " +
												"WHERE (" +
												 "(\"lngSeminarID\" >= " +  getSeminarID()  + ") " +
												") ORDER BY \"lngStudGruppeNummer\" ASC;");
		String		strReturn		=	  "";
		long		lngStudGruppeID = 	  -1;

		while(rst.next()){
			lngStudGruppeID		=	  rst.getLong("lngStudGruppeID");
			strReturn 			+= "<option " + ( ((blnPreselect) && (lngStudGruppeID==lngStudGruppeID_IN)) ? "selected" : "" ) + " value=\"" + lngStudGruppeID + "\">" +
										rst.getString("strStudGruppeBezeichnung") + "</option>\n";
		}

		rst.close();
		rst							=     null;

		return strReturn;
	}		
	
	/**
	 * @returns a String like &lt;option [selected] value=[val]&gt;[name]&lt;/option&gt;..., where <br />
	 * [selected] is set in the row where the FachID matches the param lngFachID_IN and blnPreselect=true,<br />
	 * [val] is the subject's FachID,<br />
	 * [name] is the FachID: FachBeschreibung (id and description).<br />
	 * The combo is ordered by id, ascending.
	 * @param lngFachID_IN: id of the subject to mark as preselected (if any),
	 * @param blnPreselect: if false, lngFachID_IN is disregarded altogether.
	 **/
	private String getBAMAFachCbo(long lngFachID_IN, boolean blnPreselect) throws Exception{

		ResultSetSHJ 	rst 			=     sqlQuerySHJ("SELECT " +
												  "* " +
												"FROM \"tblSdFach\" " +
												"WHERE (" +
												 "(\"intFachID\" >= " +  (getSeminarID() * 100000) + ") and " +
												 "(\"intFachID\" < " +  ((getSeminarID()+1) * 100000) + ") " +
												") ORDER BY \"strFachBeschreibung\" ASC;");
		String		strReturn		=	  "";
		long		lngFachID		= 	  -1;

		while(rst.next()){
			lngFachID			=	  rst.getInt("intFachID");
			strReturn 			+= "<option " + ( ((blnPreselect) && (lngFachID==lngFachID_IN)) ? "selected" : "" ) + " value=\"" + lngFachID + "\">" +
										lngFachID + ": " + rst.getString("strFachBeschreibung") + "</option>\n";
		}

		rst							=     null;

		return strReturn;
	}	
	
	/**
	 * @returns a String like &lt;option [selected] value=[val]&gt;[name]&lt;/option&gt;..., where <br />
	 * [selected] is set in the row where the ModulID matches the param lngModulID_IN and blnPreselect=true,<br />
	 * [val] is the module's ModulID,<br />
	 * [name] is the Modulenumber:ModulBezeichnung.<br />
	 * The combo is ordered by module-number, ascending.
	 * @param lngModulID_IN: id of the module to mark as preselected (if any),
	 * @param blnPreselect: if false, lngModulID_IN is disregarded altogether.
	 **/
	private String getModulCbo(long lngModulID_IN, boolean blnPreselect) throws Exception{

		ResultSetSHJ 	rst 			=     sqlQuerySHJ("SELECT * FROM \"tblSdModul\" Where \"lngSdSeminarID\" = " + getSeminarID() + " ORDER BY \"lngModulNummer\" ASC;");
		String		strReturn		=	  "";
		long		lngModulID		= 	  -1;

		while(rst.next()){
			lngModulID			=	  rst.getLong("lngModulID");
			strReturn 			+= "<option " + ( ((blnPreselect) && (lngModulID==lngModulID_IN)) ? "selected" : "" ) + " value=\"" + lngModulID + "\">" +
										rst.getString("lngModulNummer") + ": " + rst.getString("strModulBezeichnung") + "</option>\n";
		}

		rst							=     null;

		return strReturn;
	}

	/**
	 * @returns a String like &lt;option [selected] value=[val]&gt;[name]&lt;/option&gt;..., where <br />
	 * [selected] is set in the row where the PruefungID (ExamID) matches the param lngPruefungID_IN and blnPreselect=true,<br />
	 * [val] is the exam's PruefungID,<br />
	 * [name] is the PruefungBezeichnung (name of the exam), PruefungBeschreibung.<br />
	 * The combo is ordered by [name] asc.
	 * @param lngPruefungID_IN: id of the Pruefung to mark as preselected (if any),
	 * @param blnPreselect: if false, lngPruefungID_IN is disregarded altogether.
	 * @param blnWithDescription: do you want the @link com.shj.signUp.data.Pruefung#getPruefungBeschreibung() in the option or not?
	 **/
	private String getPruefungCbo(long lngPruefungID_IN, boolean blnPreselect, boolean blnWithDescription) throws Exception{

		ResultSetSHJ 	rst 			=     sqlQuerySHJ("SELECT * FROM \"tblSdPruefung\" Where \"lngSdSeminarID\" = " + getSeminarID() + " ORDER BY \"strPruefungBezeichnung\" ASC;");
		String		strReturn		=	  "";
		long		lngPruefungID	= 	  -1;

		while(rst.next()){
			lngPruefungID		=	  rst.getLong("lngPruefungID");
			strReturn 			+= "<option " + ( ((blnPreselect) && (lngPruefungID==lngPruefungID_IN)) ? "selected" : "" ) + " value=\"" + lngPruefungID + "\">" +
										rst.getString("strPruefungBezeichnung") + ((blnWithDescription) ? " (" + rst.getString("strPruefungBeschreibung") + ")" : "") +  "</option>\n";
		}

		rst							=     null;

		return strReturn;
	}

	/**
	 * @returns a String like &lt;option [selected] value=[val]&gt;[name]&lt;/option&gt;..., where <br />
	 * [selected] is set in the row where the DozentID matches the param lngDozentID and blnPreselect=true,<br />
	 * [val] is the DozentID of the teacher,<br />
	 * [name] is the teacher's first- and lastname.<br />
	 * The combo is ordered by [name], ascending.<br />
	 * Only those teachers are listet whose flag 'blnLehrend' is active.<br />
	 * Exception: If the teacher 'lngDozentID_IN' is the one you want, he 
	 * is shown regarless of the flag-setting.
	 * @version 6-03-01
	 * @param lngDozentID_IN: id of the teacher to mark as preselected (if any),
	 * @param blnPreselect: if false, lngLeistungID is disregarded altogether.
	 **/
	private String getDozentCbo(long lngDozentID_IN, boolean blnPreselect) throws Exception{

		ResultSetSHJ 	rst 			=     sqlQuerySHJ("SELECT * FROM \"tblSdDozent\" Where \"lngSdSeminarID\" = " + getSeminarID() + " ORDER BY \"strDozentNachname\" ASC;");
		String		strReturn		=	  "";
		long		lngDozentID		= 	  -1;
		long		ll				= 	   0;
		
		m_sAutoSuggestDozentArray = 	   "";
		
		while(rst.next()){
			lngDozentID			=	  rst.getLong("lngDozentID");
			
			// if the teacher is marked as 'teaching' -- that is current, or to be shown in list --
			// or if he is the one who was preselected.
			if((rst.getBoolean("blnDozentLehrend") || (blnPreselect && (lngDozentID==lngDozentID_IN)))){
					m_sAutoSuggestDozentArray += "oDozent[0][" + ll + "]" + " = \"" + rst.getString("strDozentNachname") + ", " + rst.getString("strDozentVorname") + "\";\n";
					m_sAutoSuggestDozentArray += "oDozent[1][" + ll++ + "]" + " = " + lngDozentID + ";\n\n";
				
					strReturn 			+= "<option " + ( ((blnPreselect) && (lngDozentID==lngDozentID_IN)) ? "selected" : "" ) + " value=\"" + lngDozentID + "\">" +
										rst.getString("strDozentNachname") + ", " + rst.getString("strDozentVorname") + "</option>\n";
			}
		}

		rst							=     null;

		return strReturn;
	}

	/**
	 * @returns a String like &lt;option [selected] value=[val]&gt;[name]&lt;/option&gt;..., where <br />
	 * [selected] is set in the row where the LeistungsID matches the param lngLeistungID and blnPreselect=true,<br />
	 * [val] is the LeistungID of the credit,<br />
	 * [name] is the credit's name.<br />
	 * The combo is ordered by [name], ascending.
	 * @param lngLeistungID_IN: id of the Credit to mark as preselected (if any),
	 * @param blnPreselect: if false, lngLeistungID is disregarded altogether.
	 **/
	private String getLeistungCbo(long lngLeistungID_IN, boolean blnPreselect) throws Exception{

		ResultSetSHJ 	rst 			=     sqlQuerySHJ("SELECT * FROM \"tblSdLeistung\" Where \"lngSdSeminarID\" = " + getSeminarID() + " ORDER BY \"strLeistungBezeichnung\" ASC;");
		String		strReturn		=	  "";
		long		lngLeistungID	= 	  -1;
		m_sAutoSuggestLeistungArray =     "";
		long 		ll				=      0;
		while(rst.next()){
			lngLeistungID			=	  rst.getLong("lngLeistungID");
			m_sAutoSuggestLeistungArray += "oLeistung[0][" + ll + "]" + " = '" + rst.getString("strLeistungBezeichnung") + "';\n";
			m_sAutoSuggestLeistungArray += "oLeistung[1][" + ll++ + "]" + " = " + lngLeistungID + ";\n\n";
			strReturn 			   += "<option " + ( ((blnPreselect) && (lngLeistungID==lngLeistungID_IN)) ? "selected" : "" ) + " value=\"" + lngLeistungID + "\">" +
										rst.getString("strLeistungBezeichnung") + "</option>\n";
		}

		rst							=     null;

		return strReturn;
	}

	/**
	 * @returns a String like &lt;option [selected] value=[val]&gt;[name]&lt;/option&gt;..., where <br />
	 * [selected] is set in the row where the NoteID(=GradeID) matches the param lngNoteID 
	 * and blnPreselect=true, and if the NoteID is not 0.<br />
	 * [val] is the NoteID of the credit,<br />
	 * [name] is the grade's name (ECTS Def and Value).<br />
	 * The combo is ordered by the ECTS value, ascending.
	 * @param lngNoteID_IN: id of the Note (=grade) to mark as preselected (if any),
	 * @param blnPreselect: if false, lngNoteID is disregarded altogether.
	 **/
	private String getNoteCbo(int intNoteID_IN, boolean blnPreselect) throws Exception{

		ResultSetSHJ 	rst 			=     sqlQuerySHJ("SELECT * FROM \"tblSdNote\" Where \"lngSdSeminarID\" = " + getSeminarID() + " ORDER BY \"sngNoteECTSCalc\" ASC;");
		String		strReturn		=	  "";
		int			intNoteID		= 	  -1;
		
		if(intNoteID_IN==0)	blnPreselect=false;
		
		while(rst.next()){
			intNoteID				=	  rst.getInt("intNoteID");
			
			if(!getSeminarNoteCboLocal()){
				strReturn 			   += "<option " + ( ((blnPreselect) && (intNoteID==intNoteID_IN)) ? "selected" : "" ) + " value=\"" + intNoteID + "\">" +
										rst.getString("strNoteECTSGrade") + " - " + rst.getString("strNoteECTSDefinition") + " - " + rst.getFloat("sngNoteECTSCalc") + "</option>\n";
			}else{
				strReturn 			   += "<option " + ( ((blnPreselect) && (intNoteID==intNoteID_IN)) ? "selected" : "" ) + " value=\"" + intNoteID + "\">" +
										 rst.getString("strNoteNameDE") + " (" + rst.getFloat("sngNoteWertDE") + ")</option>\n";				
			}
		}

		rst							=     null;

		return strReturn;
	}



	private void initCourseTypeCbo() throws Exception{

		//not yet initialized
		String strReturn="";
		long lngKurstypID;
		boolean blnPreselect=false;
		ResultSet rst;

		rst = sqlQuery("SELECT \"lngSdSeminarID\", \"lngKurstypID\", \"strKurstypBezeichnung\" " +
						"FROM \"tblSdKurstyp\" " +
						"WHERE \"lngSdSeminarID\"=" + getSeminarID() + " ORDER BY \"strKurstypBezeichnung\" ASC;");

		while(rst.next()){

			lngKurstypID = rst.getLong("lngKurstypID");

			if(lngKurstypID==this.m_lngCourseTypePreselect){
				strReturn+="<option selected value=\"" + lngKurstypID + "\">" + rst.getString("strKurstypBezeichnung") + "</option>\n";
				blnPreselect=true;
			}else{
				strReturn+="<option value=\"" + lngKurstypID + "\">" + rst.getString("strKurstypBezeichnung") + "</option>\n";
			}

		}

		rst.close();

		if(!blnPreselect) strReturn="<option selected value=\"null\">" + m_PLEASE_CHOOSE_LANG + "</option>" + strReturn;
		this.m_strCourseTypeCbo= strReturn;
	}

	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 5.   P U B L I C   H T M L-S T R I N G S   (TABLES)
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------


	/**
	 * Utility to get a table of coursetypes that (will) allow for online-exam-registration.
	 * @return An Html-Table (just <tr><td>LINK</td></tr> asf.) with coursetypes that allow online exam-registration now or in the future (that
	 * is to say that the records have blnKursScheinanmeldungErlaubt=true and a registration deadline in the future).
	 * 'LINK' here signifies the name of the coursetype, and href is same page with parameter ?txtKurstypID=IDx (x depends on the row in the table) plus everything that is handed over in 'strRequestQuery_IN.'
	 * @param strRequestQuery_IN: a Http query addition in the usual form of 'param=value&param2=value2' etc. It is appended to the link.
	 * @return String containing rows of a table.
	 **/
	public String getCourseTypesExamRegHtml(String strRequestQuery_IN) throws Exception{

		ResultSet rst=this.getCourseTypesExamRegRST();
		String strReturn="";
		String strKurstypBezeichnung;

		while(rst.next()){
			strKurstypBezeichnung=rst.getString("strKurstypBezeichnung");
			strReturn+="<tr><td align=\"center\" class=\"tcellstyle\"><a href=\"?txtKurstypID=" + rst.getString("lngKurstypID") + "&txtKurstypBezeichnung=" + strKurstypBezeichnung + "&" + strRequestQuery_IN + "\">" + strKurstypBezeichnung + "</a></td></tr>\n";
		}

		rst.close();
		return strReturn;
	}


	/**
	 *	Utility to get a table with exams (relative only to seminar).
	 *  @return An Html-String describing a three-column table: col 1 contains a description of the "Pr?fungsordnung".
	 *	Col 2 contains the descriptions of this exam ('Pr?fung'). Cold 3 contains the exam type (MA/Staatsexamen/BA/Magister),
	 *  And Col 4 contains a link to more details. The link is called 'Details' and leads to ?PrufungID=x&s=y&PruefungBezeichnung=z"
	 *  @throws when db-connection is erroneous.
	 **/
	public String getExamsHtml() throws Exception{

		ResultSet rst=this.getExamsRST();
		String strReturn="";
		String strPruefungBezeichnung="";

		while(rst.next()){

			strPruefungBezeichnung=rst.getString("strPruefungBezeichnung");
			strReturn+="<tr><td class=\"tcellstyle\"><font size=\"1\">" + rst.getString("strPruefungsordnung") + "</font></td>" +
						"<td class=\"tcellstyle\">" + strPruefungBezeichnung + "<br/>" + rst.getString("strPruefungBeschreibung") + "</td>" +
						"<td class=\"tcellstyle\">" + rst.getString("strPruefungAbschluss") + "</td>" +
						"<td class=\"tcellstyle\"><a href=\"?txtPruefungID=" + rst.getString("lngPruefungID") + "&s=" + this.getSeminarID() + "&txtPruefungBezeichnung=" + strPruefungBezeichnung + "\">Details</a></td></tr>\n";

		}
		rst.close();
		return strReturn;
	}

	/**
	 * Table with credits that are needed for an exam.
	 * @return An Html-table with three columns, [mapping|credit|credit-weight], where 'mapping' is Hauptstudium or Grundstudium.
	 * @throws Exception when connection to db is erroneous.
	 * @param lngPruefungsID: id of exam ('Pruefung') whose credits are to be listed.
	 **/
	public String getExamCreditsHtml(long lngPruefungsID) throws Exception{

		ResultSet rst=this.getExamCreditsRST(lngPruefungsID);
		String strReturn="";

		while(rst.next()){
			strReturn+="<tr><td class=\"tcellstyle\">" + rst.getString("strLeistungZuordnung") + "</td>" +
						"<td class=\"tcellstyle\">" + rst.getString("strLeistungBezeichnung") + "</td>" +
						"<td class=\"tcellstyle\">" + rst.getString("sngPruefungLeistungGewichtung") + "</td></tr>\n";
		}

		rst.close();
		return strReturn;
	}

	/**
	 * Table (one col) with a link to deeper details.
	 * @return One-column Html-table that looks like this: |LINK|,
	 * where LINK's name is the Exam-Name ('Pruefungsbezeichnung') and the href is a combination of:
	 * strBaseLink?txtPruefungBezeichnung=xyz&" + strQuery.
	 * @param strBaseLink the target link for these categories of exams,
	 * @param strQuery the param/value http-query to be added to the Base-Link.
	 * @throws Exception when connection to db is erroneous.
	 **/
	public String getExamCategoriesHtml(String strBaseLink, String strQuery) throws Exception{

		ResultSet rst=this.getExamCategoriesRST();
		String strReturn="";
		String strExamName="";

		while(rst.next()){
			strExamName=rst.getString("strPruefungBezeichnung");
			strReturn+="<tr><td class=\"tcellstyle\"><a href=\"" + strBaseLink + "?txtPruefungBezeichnung=" + strExamName + "&" + strQuery + "\">" + strExamName + "</a></td></tr>\n";
		}

		rst.close();
		return strReturn;
	}

	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 6.   p r i v a t e   r e s u l t s e t s (SQL)
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------


	/**
	 * ResultSet with coursetypes that (have courses which) allow exam-registration (whose deadline is in the future).
	 * @return ResultSet with all coursetypes which have courses in this seminar whose exam registration date is current or in the future.
	 * Postgres-specific through booleans and 'CURRENT_DATE,' vesion compatib. 7.0 (no inner joins).
	 * Criteria are: no 'Planungssemester,' 'ScheinanmeldungErlaubt,' 'dtmKursScheinanmeldungBis>=NOW.'
	 * @version 6-14-09 (May 2006): added t.strKurstypCustom1 (for cl)
	 * @version 6-19-02 (April 2008): added t.strKurstypCustom2 (for gs, as)
	 **/
	public ResultSet getCourseTypesExamRegRST() throws Exception{
		return sqlQuery("SELECT " +
			  "k.\"blnKursPlanungssemester\", " +
			  "k.\"blnKursScheinanmeldungErlaubt\", " +
			  "k.\"dtmKursScheinanmeldungBis\", " +
			  "t.\"strKurstypBezeichnung\", " +
			  "t.\"strKurstypCustom1\", " +
			  "t.\"strKurstypCustom2\", " +
			  "t.\"lngKurstypID\" " +
			"FROM \"tblBdKurs\" k, \"tblSdKurstyp\" t, \"tblBdKursXKurstyp\" x  " +
			"WHERE " +
			  "(" +
				"t.\"lngKurstypID\" = x.\"lngKurstypID\" AND " +
				"t.\"lngSdSeminarID\" = " + this.getSeminarID() + " AND " +
				"k.\"lngSdSeminarID\" = " + this.getSeminarID() + " AND " +
				"t.\"lngSdSeminarID\" = x.\"lngSeminarID\" AND " +
				"k.\"lngSdSeminarID\" = x.\"lngSeminarID\" AND " +
				"k.\"lngKursID\" = x.\"lngKursID\"" +
			  ")" +
			"GROUP BY k.\"blnKursPlanungssemester\", k.\"blnKursScheinanmeldungErlaubt\", k.\"dtmKursScheinanmeldungBis\", t.\"strKurstypBezeichnung\", t.\"lngKurstypID\", t.\"strKurstypCustom1\", t.\"strKurstypCustom2\" " +
			"HAVING " +
			  "(" +
				"(k.\"blnKursPlanungssemester\"='f'::bool) AND " +
				"(k.\"blnKursScheinanmeldungErlaubt\"='t'::bool) AND " +
				"(k.\"dtmKursScheinanmeldungBis\">=CURRENT_DATE)" +
			  ") ORDER BY \"strKurstypBezeichnung\";");
	}

	/**
	 * ResultSet with all courses that allow exam-registration and whose deadline is not expired, ordered by 'intKursSequence.'
	 * @return A ResultSet with all courses of a given coursetype whose exam registration date is current or in the future, ordered by 'intKursSequence.'.
	 * @param lngKurstypID: courses of what course-type are to be returned?
	 * Postgres-specific through booleans and 'CURRENT_DATE,' vesion compatib. 7.0 (no inner joins).
	 * Criteria are: no 'Planungssemester,' 'ScheinanmeldungErlaubt,' 'dtmKursScheinanmeldungBis>=NOW.'
	 * @throws Exception if connection to database is erroneous.
	 **/
	public ResultSet getCoursesExamRegRST(long lngKurstypID) throws Exception{
		return sqlQuery("SELECT " +
			  "k.\"blnKursPlanungssemester\", " +
			  "k.\"blnKursScheinanmeldungErlaubt\", " +
			  "k.\"dtmKursScheinanmeldungBis\", " +
			  "k.\"dtmKursScheinanmeldungVon\", " +
			  "CASE WHEN ((k.\"dtmKursScheinanmeldungVon\"<=CURRENT_DATE AND k.\"dtmKursScheinanmeldungBis\">=CURRENT_DATE)) THEN 't'::bool ELSE 'f'::bool END AS \"blnAllowChanges\", " +
			  "x.\"lngKurstypID\", " +
			  "k.\"intKursSequence\", " +
			  "k.\"strKursTag\", " +
			  "k.\"dtmKursBeginn\", " +
			  "k.\"dtmKursEnde\", " +
			  "k.\"lngKursID\", " +
			  "k.\"strKursTitel\", " +
			  "d.\"strDozentTitel\", " +
			  "d.\"strDozentVorname\", " +
			  "d.\"strDozentNachname\", " +
			  "d.\"lngSdSeminarID\", " +
			  "k.\"lngSdSeminarID\", " +
			  "k.\"strKursTerminFreitext\", " +
			  "x.\"lngSeminarID\" " +
			"FROM \"tblSdDozent\" d, \"tblBdKurs\" k, \"tblBdKursXKurstyp\" x " +
			"WHERE " +
			 "(" +
			   "(k.\"lngKursID\"= x.\"lngKursID\") AND " +
			   "(k.\"lngSdSeminarID\"= x.\"lngSeminarID\") AND " +
			   "(d.\"lngDozentID\"= k.\"lngDozentID\") AND " +
			   "(d.\"lngSdSeminarID\"= k.\"lngSdSeminarID\") AND " +
			   "(k.\"blnKursPlanungssemester\"='f'::bool) AND " +
			   "(k.\"blnKursScheinanmeldungErlaubt\"='t'::bool) AND " +
			   "(k.\"dtmKursScheinanmeldungBis\">=CURRENT_DATE) AND " +
			   "(x.\"lngKurstypID\"=" + lngKurstypID + ") AND " +
			   "(x.\"lngSeminarID\"=" + this.getSeminarID() + ")" +
			  ") " +
			"ORDER BY k.\"intKursSequence\" ASC;");
	}

	/**
	 *  ResultSet containing exams of current seminar.
	 *  @return "*"-select of all exams in this seminar ordered by pruefungsid.
	 *  @throws Exception if connection to db is faulty.
	 **/
	private ResultSet getExamsRST() throws Exception{
		return sqlQuery("select * from \"tblSdPruefung\" where \"lngSdSeminarID\"=" + this.getSeminarID() + " ORDER BY \"lngPruefungID\" ASC;");
	}

	/**
	 * Credits needed for the exam specified through 'lngPruefungsID.'
	 * #hack: this does not work (tblSdPruefungXLeistung does not exist anymore)
	 * @return All credits that are needed as prerequisite for this exam (identified through lngPruefungsID).
	 * @param lngPruefungsID: id of exam to analyze.
	 * @throws Exception if connection to db is erroneous.
	 **/
	private ResultSet getExamCreditsRST(long lngPruefungsID) throws Exception{
		return sqlQuery("SELECT " +
		  "p.\"lngSdSeminarID\", " +
		  "p.\"lngPruefungID\", " +
		  "l.\"strLeistungBezeichnung\", " +
		  "l.\"strLeistungZuordnung\", " +
		  "x.\"sngPruefungLeistungGewichtung\" " +
		"FROM \"tblSdLeistung\" l, \"tblSdPruefung\" p, \"tblSdPruefungXLeistung\" x " +
		"WHERE " +
		  "(" +
		   "(p.\"lngPruefungID\" = x.\"lngPruefungID\") AND " +
		   "(p.\"lngSdSeminarID\" = x.\"lngSdSeminarID\") AND " +
		   "(l.\"lngLeistungID\" = x.\"lngLeistungID\") AND " +
		   "(l.\"lngSdSeminarID\" = x.\"lngSdSeminarID\") AND " +
		   "(p.\"lngSdSeminarID\"=" + this.getSeminarID() + ") AND " +
		   "(p.\"lngPruefungID\"=" + lngPruefungsID + ")" +
		  ")" +
		"ORDER BY l.\"strLeistungZuordnung\" ASC;");
	}

	/**
	 *  ResultSet with exams' names.
	 *  @return ResultSet with names of exams ('strPruefungBezeichnung') of this seminar, grouped (so that e.g. 'Orientierungepr?fung' and 'Zwischenpr?fung' are the outcome).
	 *	@throws Exception if connection to db is erroneous.
	 **/
	private ResultSet getExamCategoriesRST() throws Exception{
		return sqlQuery("select \"lngSdSeminarID\", \"strPruefungBezeichnung\" from \"tblSdPruefung\" where (\"lngSdSeminarID\"=" + this.getSeminarID() + ") Group By \"lngSdSeminarID\", \"strPruefungBezeichnung\";");
	}

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

