
package de.shj.UP.beans.config.student;

import de.shj.UP.logic.StudentData;
import de.shj.UP.logic.PruefungData;
import javax.servlet.http.*;
import de.shj.UP.HTML.HtmlDate;
import de.shj.UP.data.Modul;
import de.shj.UP.data.Leistung;
import de.shj.UP.data.shjCore;
import java.sql.*;
import java.text.SimpleDateFormat;

/**
 * This bean manages the issuing of exams (worker-bean for cl/Config/Student/AddPick)
 * In order to do so, there are a number of pending worker-beans, so to speak, all 
 * of which share the following diagram:<br /><br />
 * 
 * <b>StudentPruefungBean</b><br />
 * |<br />
 * |----- PruefungXModulBean<br />
 * |<br />
 * |<br />
 * |----- ModulXLeistung<br />
 * <br />
 * This bean (<b>StudentPruefungBean</b>) is the major access-point for the frontend:
 * it can <br /><ol type="i"><li>Retrieve ModulXLeistung-objects (which simply consist of a Module and a Leistung) 
 * with missing credits (Zulassungsvoraussetzungen).<br />
 * There is a limit as to how many missing credits are returned (as ModulXLeistung-objects): 
 * m_intMaxRecords_MissingZulassungsvoraussetzung (default is 10, but there are get-set-methods 
 * to change that. Reason for the limit is -- besides velocity and memory-management precaution --
 * that there's an array to be initialized, and some number must be given)).<br />
 * It can also </li><li>Iterate through the Modules in this exam, using a PruefungXModulBean-array 
 * that can count and add credit points asf. (see there).</li></ol>
 * A Code Example to check Zulassungsvoraussetzungen could look like this:<br /><hr />
 * <Code>
 * 	String strReturn	= "";<br />
 *	for(int ii=0;ii&lt;StudentPruefungBean.getMissingZulassungsvoraussetzungenCount();ii++){<br /><br />
 *	&nbsp;&nbsp;&nbsp;strReturn += StudentPruefungBean.getMissing_<br />
 *  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Zulassungsvoraussetzung(ii).Leistung().getLeistungBezeichnung() + "(Modul " +<br /> 
 *	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;StudentPruefungBean.getMissing_<br />
 *  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Zulassungsvoraussetzung(ii).Modul().getModulBezeichnung() + ")";<br /><br />
 *	}<br />
 *  if(strReturn.length()&gt;0) strReturn = "Folgende Zulassungsvoraussetzungen " +<br />
 *  &nbsp;&nbsp;&nbsp;"m?ssen erst noch erbracht werden: " + strReturn;<br />
 * </Code><hr />
 * A Code Example of how to itereate through the credits that a student has gathered for a given exam could look like this one here:
 * <br /><hr />
 * <Code>
 * for(int ii=0;ii&lt;StudentPruefungBean.getModulCount();ii++){<br />
 * &nbsp;&nbsp;&nbsp;while(server.Modul(ii).nextLeistung()){<br />
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;...<br />
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;...<br />
 * &nbsp;&nbsp;&nbsp;}<br />
 * }
 * </Code>
 * <hr />
 * A little awkward, but maybe useful (?) is the 'BelowTenKilo'-methodology: for grade-suggestion, 
 * there is an extra method that disregards all grades in modules with a module-number of 10.000 
 * or greater. By convention, these Modules contain oral exams and BA-thesis. Often, a grade 
 * of just the 'Studienleistungen' is preferable.  
 * <br /> 
 * 
 * Changed
 * Jan 30th, 2006: added m_request.getParameter("txtStudentPruefungCustom1") = m_strStudentPruefungCustom1.
 */
public class StudentPruefungBean extends shjCore  {
	
	private static final long serialVersionUID = -8797694637189142074L;
	// -----------------------------------------------------------------
	// 1. Declarations
	// -----------------------------------------------------------------	
    private int		  m_intMaxRecords_MissingZulassungsvoraussetzung = 10;
	private int		  m_intMissingZulassungsvoraussetzungenCount	 =-1;
	private int		  m_intModulCount								 = -3;
	private boolean   m_blnModulInitialized							 = false;
	private boolean	  m_blnNotAlreadyCounted						 = true;
	private long 	  m_lngPID;
//	private long 	  m_lngSessionID;
	private long 	  m_lngSeminarID;
	private long 	  m_lngPruefungID;
	private String	  m_strMatrikelnummer;
	private long	  m_lngStudentPID;
	protected String  m_strSQL										= "";
	private int		  m_intDigitsAfterPoint							= 1;
	private long	  m_lngMaxDateMillisec							= 0;

	private float	  m_fltGradeSum									= -1;
	private float	  m_fltCreditPointsPLSum						= -1;
	private float	  m_fltGradeSumBelowTenKilo						= -1;	// A little awkward: extra grade-
	private float	  m_fltCreditPointsPLSumBelowTenKilo			= -1;	// calc for modules with a no. < 10000
	private String 	  m_strStudentPruefungCustom1					= "";
	
	private boolean	  m_blnZVModus									= false;
	private ModulXLeistung[]		missingZulassungsvoraussetzung;
	private PruefungXModulBean[]	module;
	private HttpServletRequest 		m_request;
	private StudentData   			m_StudentData					= new StudentData();
	private PruefungData  			m_PruefungData;
	
	// --------------------------------------------------------------------
	// 2. simple 'get'-properties
	// --------------------------------------------------------------------
	
	/**<pre>
	 * See @link com.shj.signUp.beans.config.student.PruefungXModulBean#setNotAlreadyCounted(boolean)
	 * 
	 * The default is 'true,' so credits that have already 
	 * been counted as a Pruefungsleistung in another exam 
	 * are generally ignored.
	 * 
	 * The value is set in the pending 'Module' objects on 
	 * initialization, see @link #Modul(int).
	 * 
	 * @version 6-09-04
	 * </pre>
	 * @param value
	 */
	public void setNotAlreadyCounted(boolean value){
		m_blnNotAlreadyCounted=value;
	}
	
	/**
	 * @return PID (simple get)
	 * @deprecated since version 5-30-2. There seems no need for an administrator's ID, here.
	 */
	public long getPID(){
		return this.m_lngPID;
	}

	/**
	 * @return latest date of a credit in this exam.
	 */
	private long getMaxDateMillisec(){
		if(this.m_lngMaxDateMillisec==0) this.sumUpModules();
		return this.m_lngMaxDateMillisec;
	}
	
	
	/**
	 * @return Date when last credit was issued. Of course all credits must have been checked with nextLeistung().
	 * @throws Exception
	 */
	public HtmlDate suggestExamDate() throws Exception{
		SimpleDateFormat sdfIso=new SimpleDateFormat ("yyyy-MM-dd");
		return new HtmlDate(sdfIso.format(new java.util.Date(this.getMaxDateMillisec())), this.m_request.getLocale());
	}	
	
	/**
	 * @return Semester when exam is issued according to user input (request-param "cboSemester").
	 */
	public String getSemesterChosen(){
		return this.m_request.getParameter("cboSemester");
	}
	
	/**
	 * @return SeminarID (simple get)
	 */
	public long getSeminarID(){
		return this.m_lngSeminarID;
	}

	/**
	 * @return SessionID (simple get)
	 */
	public long getSessionID(){
		return this.m_lngSeminarID;
	}
	
	/**
	 * @return PruefungID (simple get)
	 */	
	public long getPruefungID(){
		return this.m_lngPruefungID;
	}
	
	/**
	 * @return Number of missing Zulassungsvoraussetzungen that were found, or 0.
	 * Used for iteration, a code example:<br />
	 * <Code>
	 * for(int ii=0;ii&lt;bean.getMissingZulassungsvoraussetzungenCount();ii++){<br />
     * &nbsp;&nbsp;&nbsp;strReturn += bean.getMissing_<br />
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Zulassungsvoraussetzung(ii).Leistung().getLeistungBezeichnung() + _<br />
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"( Modul " +<br /> 
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;bean.getMissingZulassungsvoraussetzung(ii).Modul().getModulBezeichnung() + ")";<br />
     * }</Code><br />
	 * @throws Exception
	 */
	public int getMissingZulassungsvoraussetzungenCount() throws Exception{
		if(this.m_intMissingZulassungsvoraussetzungenCount<0) this.initMissingZulassungsvoraussetzung();
		return this.m_intMissingZulassungsvoraussetzungenCount;
	}

	/**
	 * Example-usage:<br />
	 * for(int ii=0;ii<bean.getMissingZulassungsvoraussetzungenCount();ii++){<br />
	 *		strReturn += bean.getMissingZulassungsvoraussetzung(ii).Leistung().getLeistungBezeichnung() + "( Modul " +<br /> 
	 *					 bean.getMissingZulassungsvoraussetzung(ii).Modul().getModulBezeichnung() + ")";<br />
	 * }<br />
	 * @param index which one of the missing zulassungsvoraussetzungen?
	 * @return ModulXLeistung-object containing the Module and Leistung of a missing Zulassungsvoraussetzung
	 * @throws Exception (wrong index etc.)
	 */
	public ModulXLeistung getMissingZulassungsvoraussetzung(int index) throws Exception{
		if (this.missingZulassungsvoraussetzung==null) this.initMissingZulassungsvoraussetzung();
		return this.missingZulassungsvoraussetzung[index];
	}
	
	
	/**
	 * @return Number of Modules in the current Pruefung, counting only those that are NOT ZULASSUNGSVORAUSSETZUNG.
	 * @throws Exception
	 */
	public int getModulCount() throws Exception{
		if(!m_blnModulInitialized) this.initModule();
		return this.m_intModulCount;
	}
	
	/**
	 * @return Module number index.
	 * @param index Module to return. 
	 * @throws Exception
	 */
	public PruefungXModulBean Modul(int index) throws Exception{
		if(!m_blnModulInitialized) this.initModule();
		this.module[index].setNotAlreadyCounted(m_blnNotAlreadyCounted);
		return this.module[index];
	}
	
	// --------------------------------------------------------------------
	// 3. simple 'get/set'-pairs
	// --------------------------------------------------------------------
	
	/**
	 * @return There is an implicit limit on the number of missing Zulassungsvoraussetzungen that are stored. The 
	 * default is 10. You can change the default using 'set'.
	 */	
	public int getMaxRecordsMissingZulassungsvoraussetzung(){
		return m_intMaxRecords_MissingZulassungsvoraussetzung;
	}

	/**
	 * @return How many missing Zulassungsvoraussetzungen are stored? The 
	 * default is 10. 
	 */	
	public void setMaxRecordsMissingZulassungsvoraussetzung(int value){
		this.m_intMaxRecords_MissingZulassungsvoraussetzung=value;
	}
	
	/**
	 * Grade calculation. 3.75 = 3.7 in current setting (1 digit=7). 
	 * @see #getGrade()
	 * @param value: number of digits after point to consider for grade (default: 1)
	 */
	public void setGradeDigitsAfterPt(int value){
		this.m_intDigitsAfterPoint = value;
	}

	/**
	 * This method disregards Credits in Modules with a module-number of 10.000 or greater.
	 * Default is to round the final grade by cutting everything beyond the first decimal point. 
	 * As a reminder of this un-mathematical rounding, the value is handed back as String rather than float.
	 * @return final grade, calculated as GradeSum/CreditPointPLSum, disregarding credits in modules with a 
	 * module-number of 10.000 or greater.
	 * @see #getGradeSuggestion()
	 * @see #setGradeDigitsAfterPt(int) to change rounding-settings
	 * @see #getGradeChosen()
	 */
	public String getGradeSuggestionBelowTenKilo(){
		String strReturn="";
		strReturn = String.valueOf(this.getGradeSumBelowTenKilo()/this.getCreditPointsPLSumBelowTenKilo());
		return (strReturn.substring(0, strReturn.indexOf(".") + m_intDigitsAfterPoint + 1));
	}	
	
	/**
	 * Default is to round the final grade by cutting everything beyond the first decimal point. 
	 * As a reminder of this un-mathematical rounding, the value is handed back as String rather than float.
	 * @return final grade, calculated as GradeSum/CreditPointPLSum.
	 * @see #setGradeDigitsAfterPt(int) to change rounding-settings
	 * @see #getGradeChosen()
	 */
	public String getGradeSuggestion(){
		String strReturn="";
		strReturn = String.valueOf(this.getGradeSum()/this.getCreditPointsPLSum());
		return (strReturn.substring(0, strReturn.indexOf(".") + m_intDigitsAfterPoint + 1));
	}
	
	/**
	 * #hack: this method produces an error (hands back -1) if not all modules have 
	 * been read completely. This is for debug, change please.
	 * @return Sum of all CreditPoints Pruefungsleistungen in all modules, or -1 in case of error.
	 */	
	private float getCreditPointsPLSum(){
		if(this.m_fltCreditPointsPLSum < 0){
			sumUpModules();
		}
		
		return this.m_fltCreditPointsPLSum;
	}

	/**
	 * Credit-point sum of credits in modules with a module-number below 10.000.
	 * @return Credit-point sum of credits in modules with a module-number below 10.000., or -1 in case of error.
	 */	
	private float getCreditPointsPLSumBelowTenKilo(){
		if(this.m_fltCreditPointsPLSumBelowTenKilo < 0){
			sumUpModules();
		}
		
		return this.m_fltCreditPointsPLSumBelowTenKilo;
	}	
	
	/**
	 * Sum of all grades, needed for grade calculation
	 * @return Sum of all weighed grades in all modules, or -1 in case of error.
	 * @see #sumUpModules()
	 */
	private float getGradeSum(){
		if(this.m_fltGradeSum < 0){
			sumUpModules();
		}

		return this.m_fltGradeSum;
	}	

	/**
	 * Sum of all grades of credits in modules with a module number below 10.000.<br />
	 * @return Sum of all weighed grades in all modules, or -1 in case of error.
	 * @see #sumUpModules()
	 */
	private float getGradeSumBelowTenKilo(){
		if(this.m_fltGradeSumBelowTenKilo < 0){
			sumUpModules();
		}

		return this.m_fltGradeSumBelowTenKilo;
	}	
	
	
	/**
	 * @return SQL-String to save this exam to database.
	 */
	private String getSQLAdd(){
		if(this.m_strSQL.equals("")) this.sumUpModules();
		return this.m_strSQL;
	}
	
	/**
	 * Sums up Pruefungsleistungs-Creditpoints and weighed grades of all modules.
	 */
	private void sumUpModules() {
		try {
			
			// reset grade-calculation parameters:
			this.m_fltGradeSum 						= 0;
			this.m_fltCreditPointsPLSum 			= 0;
			this.m_fltGradeSumBelowTenKilo			= 0;
			this.m_fltCreditPointsPLSumBelowTenKilo = 0;
			
			this.m_strSQL				= "";
			
			for(int ii=0;ii<this.getModulCount();ii++){
				
				// make sure the Modul has been completely read.
				if(!this.Modul(ii).m_blnIsCompletelyRead){
					while(this.Modul(ii).nextLeistung()){}
				}
			
				this.m_strSQL				+= this.Modul(ii).getSQL();
				this.m_fltGradeSum 			+= this.Modul(ii).getGradeSum(); 
				this.m_fltCreditPointsPLSum += this.Modul(ii).getCreditPointCurrentSumPL(); 
				
				// enable extra grade calculation for grades in modules below 10000
				if(this.Modul(ii).getModulNummer() < 10000){
					this.m_fltGradeSumBelowTenKilo 			+= this.Modul(ii).getGradeSum(); 
					this.m_fltCreditPointsPLSumBelowTenKilo += this.Modul(ii).getCreditPointCurrentSumPL(); 					
				}
				
				// ggf. sp?testes Datum hochsetzen.
				if(this.m_lngMaxDateMillisec < this.Modul(ii).getMaxDateMillisec()) 
					this.m_lngMaxDateMillisec = this.Modul(ii).getMaxDateMillisec();
			}
		} catch (Exception e) {
			this.m_fltCreditPointsPLSum = -1;
			this.m_fltGradeSum			= -1;
		}
	}


	/**
	 * @return Grade that was issued by user (request-parameter "txtStudentPruefungNote")
	 * @see #getGradeSuggestion()
	 */
	public String getGradeChosen(){
		return (this.m_request.getParameter("txtStudentPruefungNote"));
	}	
	
	/**
	 * Generate Module-Array with Modules that are needed for this Pruefung (Exam).
	 * The Module-Array Contains only Modules that are NOT A ZULASSUNGSVORAUSSETZUNG.
	 * @throws Exception
	 */
	private void initModule() throws Exception{
		m_blnModulInitialized=true;
		int ii=0;
		this.m_intModulCount		= (int)dbCount("lngModulID", "tblSdPruefungXModul", 
												"\"lngPruefungID\"=" + this.m_PruefungData.getPruefungID() + " and " +
												"\"lngSdSeminarID\"= " +this.m_PruefungData.getSdSeminarID() + " and " +
												"\"blnZulassungsvoraussetzung\"=" + getDBBoolRepresentation(this.m_blnZVModus));
		
		// This selects Modules that are NOT ZULASSUNGSVORAUSSETZUNGEN (!). (These are checked elsewhere).
		if(this.m_intModulCount>0){
			module	= new PruefungXModulBean[this.m_intModulCount];
			ResultSet	rstModule 		= this.m_PruefungData.getModules(this.m_blnZVModus);
			while(rstModule.next()){
				module[ii++]=new PruefungXModulBean(rstModule, this.Student(), this.m_request);
			}
			rstModule.close();
		}
		
	}
	
	/**
	 * Refactored in 5-29-3c (Dec 19, 2004).
	 * Initializes Modul-Leistung pairs that represent missing Zulassungsvoraussetzungen.
	 * These are accessible through the corresponding get-methods (...Count and Object).
	 * @throws Exception
	 */
	private void initMissingZulassungsvoraussetzung() throws Exception{
		this.m_intMissingZulassungsvoraussetzungenCount=0;
		this.missingZulassungsvoraussetzung 			  = new ModulXLeistung[m_intMaxRecords_MissingZulassungsvoraussetzung];
		boolean blnZulassungsvoraussetzung= true;
		ResultSet rstModule 			  = this.m_PruefungData.getModules(blnZulassungsvoraussetzung);
		ResultSet rstMissingCredits		  = null;
		int iCount=0;
		while(rstModule.next()){
			rstMissingCredits 		= this.Student().getMissingCredits(rstModule.getLong("lngModulID"));
			while(rstMissingCredits.next() && iCount<m_intMaxRecords_MissingZulassungsvoraussetzung){
			   missingZulassungsvoraussetzung[iCount++]	= new ModulXLeistung(new Modul(rstModule),new Leistung(rstMissingCredits));		   
			}
			rstMissingCredits.close();
		}
		rstModule.close();
		this.m_intMissingZulassungsvoraussetzungenCount=iCount;
	}
	
	/**
	 * @return Student related to this request.
	 */
	public StudentData Student(){
		return m_StudentData;
	}
	
	/**
	 * Saves the current exam, specified in the request. In order for this method to 
	 * work, the request must contain 'cboSemester', 'txtStudentPruefungNote' and 
	 * a data (HtmlDate) in its default construction.<br />
	 * Now (version 6-11-02), this method also saves 'txtStudentPruefungCustom1' to 
	 * the database.
	 * All relevant data must have been collected, that is to say that 
	 * @see com.shj.SignUp.HTML.HtmlDate
	 * @return true for success in saving this exam, false for failure.
	 */
	public boolean save(){
		String strSQL = "";
		 try {
			strSQL = "insert into \"tblBdStudentXPruefung\" (\"lngSdSeminarID\", \"lngSdPruefungsID\", \"strMatrikelnummer\", \"strStudentPruefungNote\", \"blnStudentPruefungValidiert\", \"blnStudentPruefungAnmeldung\", \"blnStudentPruefungBestanden\", \"strStudentPruefungSemester\", \"dtmStudentPruefungAusstellungsd\", \"strStudentPruefungCustom1\") VALUES ( " +
				this.getSeminarID() + ", " + this.m_lngPruefungID + ", '" + 
				this.m_StudentData.getMatrikelnummer() + "', '" + this.getGradeChosen() + 
				"', 't', 'f', 't', '" + this.getSemesterChosen() + "', '" + 
				new HtmlDate(this.m_request, this.m_request.getLocale()).getIsoDate() + "', '" + this.m_strStudentPruefungCustom1 + "' );\n\n" + 
				this.getSQLAdd();
			 m_strDebug += "\n" + strSQL;
			 return this.sqlExeSingle(strSQL);
		} catch (Exception e) {
			// Construction of HtmlDate goes wrong, or sql-execution goes wrong.
			m_strDebug += "\n" + strSQL + "\n<br />Error: "+ e.getMessage();
			return false;
		}
	}	
	
	/**
	 * Empty constructor for Bean-usage. Use init to initialize.
	 */
	public StudentPruefungBean(){
	}
	
	/**
	 * @param s must be initialized prior to constructing this worker-bean.
	 * @param r must contain "txtPruefungID" and may contain 'cboWertung-' coded-combos (see 
	 * the jsp for details), and in case you want to call 'save',
	 * it must also contain 'cboSemester' and 'txtStudentPruefungNote' as well as 
	 * a default-constructed HtmlDate combo. 
	 * @see com.shj.signUp.HTML.HtmlDate#getDateCombo()
	 * @throws Exception (no parameter txtPruefungID found or database-connection problems).
	 */
	public void init(StudentData s,HttpServletRequest r) throws Exception{
		m_StudentData 	= s;
		m_request	  	= r;
		m_lngSeminarID	= s.getSeminarID();
		m_lngPruefungID	= Long.parseLong (	m_request.getParameter("txtPruefungID") );
		if(m_request.getParameter("txtStudentPruefungCustom1") != null) m_strStudentPruefungCustom1 =  m_request.getParameter("txtStudentPruefungCustom1");
		m_PruefungData	= new PruefungData(m_lngSeminarID, m_lngPruefungID);
	}
	
	/**
	 * @param s must be initialized prior to constructing this worker-bean.
	 * @param r must contain "txtPruefungID" and may contain 'cboWertung-' coded-combos (see 
	 * the jsp for details), and in case you want to call 'save',
	 * it must also contain 'cboSemester' and 'txtStudentPruefungNote' as well as 
	 * a default-constructed HtmlDate combo. 
	 * @see com.shj.signUp.HTML.HtmlDate#getDateCombo()
	 * @throws Exception (no parameter txtPruefungID found or database-connection problems).
	 */
	public void init(StudentData s, long lPruefungID) throws Exception{
		m_StudentData 	= s;
		// m_request	  	= r;
		m_lngSeminarID	= s.getSeminarID();
		m_lngPruefungID	= lPruefungID;
		m_PruefungData	= new PruefungData(m_lngSeminarID, m_lngPruefungID);
	}	
	
	/**
	 * @param request must contain these expected parameters:
	 * #requestparam idxPID			(long)   ID of administrator<br />
	 * (#requestparam idxSessionID		(long)   SessionID (of config- or admin-session)<br />)
	 * #requestparam idxs			(long)   SeminarID<br />
	 * #requestparam txtMatrikelnummer	(String) Matrikelnummer of student to look up.<br />
	 * #requestparam txtStudentPID		(long)   P-ID of student (in order to init StudentData-object).<br />
	 * #requestparam txtPruefungID		(long)	 Id of the exam to save.<br /><hr /><br />
	 * If you want to _save_ the exam, the following additional parameters are required:<br />
	 * #requestparam cboSemester		(String) Semester when exam was issued (user-choice)<br />
	 * #requestparam txtStudentPruefungNote (String) Grade (user-input)<br />
	 * #requestparams as needed for the construction of a @see com.shj.signUp.HTML.HtmlDate with a request.
	 * @deprecated in version 5-30-2, use empty constructor and init instead.
	 **/
	public StudentPruefungBean(HttpServletRequest request)throws Exception{
		m_StudentData		= new StudentData();
		m_lngPID			=     Long.parseLong (	request.getParameter("idxPID")	  	  );
//		m_lngSessionID 		=     Long.parseLong (	request.getParameter("idxSessionID")  );
		m_lngSeminarID 		=     Long.parseLong (	request.getParameter("idxs")		  );
		m_lngPruefungID		=     Long.parseLong (	request.getParameter("txtPruefungID") );
		m_strMatrikelnummer =     					request.getParameter("txtMatrikelnummer");
		m_lngStudentPID 	=     Long.parseLong (	request.getParameter("txtStudentPID")  );
		m_StudentData.init 				(m_lngSeminarID, m_lngStudentPID, m_strMatrikelnummer);
		m_PruefungData		= new PruefungData(m_lngSeminarID, m_lngPruefungID);
		m_request		 	= request;
	}
	
	
}
