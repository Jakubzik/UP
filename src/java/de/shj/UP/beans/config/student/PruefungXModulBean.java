package de.shj.UP.beans.config.student;
import de.shj.UP.logic.StudentData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import javax.servlet.http.*;

import de.shj.UP.data.Note;

/**
 * This bean can iterate through a student's credits (.nextLeistung()).<br />
 * Credits are limited to those of a certain exam (specified through the Modul which 
 * is in turn handed down in the constructor). On iteration, credit-points are summed
 * up. <br />
 * Next to .nextLeistung() there are two types of methods: suggestXYZ and chosenXYZ.<br />
 * The methods 'suggestXYZ' say what SignUp thinks would be  
 * a good property of this credit, and the methods 'chosenXYZ' reflect  
 * the combo-box user-choice about what to do with the credit. <b>The 
 * calculation of credit-point and grade is based on the user's choice, not on 
 * SignUp's suggestions</b>. <br />
 * Two tricky things: one is that SignUp's suggestions will be influenced by the user's choice. 
 * In other words: with every suggestion that is overruled by the user, SignUp re-calculates 
 * what would be best <i>given this user-choice</i>.<br />The other one is that no matter 
 * what the user wants, SignUp stops adding credit points when there are enough to 
 * satisfy this exam.<br />
 * In the currently (Jan 2005) existing user frontend, there is no mix of suggestions and 
 * user-choices: it's either or. So the mix is fairly untested.<br /><br />
 * Pending worker-beans, so to speak, 
 * that share the following diagram:<br /><br />
 * 
 * StudentPruefungBean<br />
 * |<br />
 * |----- <b>PruefungXModulBean</b><br />
 * |<br />
 * |<br />
 * |----- ModulXLeistung<br />
 * <br />
 * @see com.shj.signUp.logic.StudentData#getStudentCredits(long, long, boolean, boolean)
 */
public class PruefungXModulBean extends de.shj.UP.data.Modul {
	
	// ---------------------------------------------------------
	// 1. Declarations
	// ---------------------------------------------------------
	

	private static final long serialVersionUID = -3336729768884540213L;
	// a) CONSTANTS
	public final int	OPT_NO_CHOICE_IN_REQUEST= -1;
	public final int	OPT_NULL				=  0;
	public final int	OPT_STUDIENLEISTUNG		=  1;
	public final int	OPT_PRUEFUNGSLEISTUNG	=  2;	
	
	// b) OBJECT VARIABLES
	protected boolean	m_blnIsCompletelyRead	= false;	// was .nextLeistung() called until it returned false?
	private String		m_strReduktionsvermerk	= "";		// auto-reduction of Pruefungleistungen
	private HttpServletRequest m_request;					// request from jsp-page
	private float 		m_fltMinCreditPts;					
	private float 		m_fltMinCreditPtsPLeistung;
	private long 		m_lngPruefungID;
	private boolean 	m_blnCommitmentsOnly;
	private boolean 	m_blnNotAlreadyCounted	= true;
	private float 		m_fltLPPruefungVermindert= 0;
	private long 		m_lngLeistungID;
	private long 		m_lngLeistungCount;
	private String 		m_strLeistungBezeichnung= "";
	private String		m_strKursTitel			= "";
	private float  		m_fltCreditPoints   	= 0;
	private float  		m_fltCreditPointsPL 	= 0;	
	private float  		m_fltCreditPointsSum   	= 0;
	private float  		m_fltCreditPointsPLSum 	= 0;
	private boolean m_blnStudentLeistungPruefung= false;
	private ResultSet 	m_rstLeistungen			= null;
	private int 		m_intLeistungUseOption	= 0;
	private float		m_fltGradeSum			= 0;
	private long		m_lngDateMaxMillisec	= 0;
	
	private String		m_strSQL				= "";
	private boolean 	m_blnManualOverride		= false;
	private boolean		m_blnIgnoreManualChoice	= false;
	private StudentData m_StudentData;
	public  de.shj.UP.data.Note		LeistungNote;

	// ---------------------------------------------------------
	// 2. Important methods:
	// ---------------------------------------------------------
	/**
	 * This method is responsible for the calculation of credit-point-sums that are 
	 * relevant for grade-calculation as well.<br />
	 * This method is intended for for-loops only (while(.nextLeistung()) asf. It can <b>not</b> 
	 * be used to show if there <b>is</b> a next Leistung or not, since it sets the master ResultSet to 
	 * zero when it has no more rows; the next call re-initializes the ResultSet automatically.
	 * @param student Object representing the student whose exam is to be calculated.
	 * @return true, if there is a next Leistung (credit) in the current module, false otherwise.
	 * @throws Exception
	 * @see com.shj.signUp.logic.StudentData#getStudentCredits(long, long, boolean, boolean)
	 */
	public boolean nextLeistung() throws Exception{
		
		boolean blnReturn	= false;

		// if this is the fist call, initialize the resultset
   		if ( this.m_rstLeistungen == null ) 
   			this.m_rstLeistungen = this.m_StudentData.getStudentCredits(this.m_lngPruefungID, this.getModulID(), this.m_blnCommitmentsOnly, this.m_blnNotAlreadyCounted);
		
		blnReturn			= this.m_rstLeistungen.next();
		
		if ( !blnReturn ){
			// this was the last 'Leistung' (=credit) -> set everything to zero.
			this.m_rstLeistungen = null;
			this.LeistungNote 	 = null;
			m_blnIsCompletelyRead=true;
		}else{
			// there is another Leistung: set object variables:		
			resetLeistung();
			updateLeistung();
			m_blnIsCompletelyRead=false;
		}

		return blnReturn;
	}	
	
	/**
	 * update Leistung (=Credit) after call of nextLeistung().
	 * @see #nextLeistung()
	 * @throws Exception
	 * @throws SQLException
	 */
	private void updateLeistung() throws Exception, SQLException {
		
		updateLeistungTrivia();		// set unproblematic object vars
		
		if(this.m_fltCreditPointsSum < this.m_fltMinCreditPts){

			m_intLeistungUseOption			= this.OPT_STUDIENLEISTUNG;
			
			// Leistungspunkte z?hlen nur dann, wenn nicht von Hand abgew?hlt
			if(! (this.containsManualChoice() && this.chosenAsDontUse())){
			  m_fltCreditPointsSum 			+= m_fltCreditPoints;
			}else{
				if(this.containsManualChoice()) this.m_blnManualOverride=true;
			}
		}
		
		if((m_blnStudentLeistungPruefung)&&(this.m_fltCreditPointsPLSum < this.m_fltMinCreditPtsPLeistung)){
 
			this.m_intLeistungUseOption		= this.OPT_PRUEFUNGSLEISTUNG;
			
			// Pr?fungsleistungspunkte z?hlen nur dann, wenn nicht von Hand abgew?hlt
			if (! (this.containsManualChoice() && this.getChosenUseOption()!=this.OPT_PRUEFUNGSLEISTUNG)){ 		  
				updatePruefungsleistungen();
			}else{
				if(this.containsManualChoice()) this.m_blnManualOverride=true;
			}
		}
	
		// Als Leistungspunkte (Leistungspunkte - Reduktion) eingetragen; hoffe, das stimmt immer
		// Pr?fungsleistung? --> chosenAsPruefungsleistung
		this.m_strSQL += "insert into \"tblBdStudentPruefungDetail\" (\"lngSdSeminarID\", \"lngSdPruefungsID\", \"strMatrikelnummer\", \"lngLeistungsID\", \"lngStudentLeistungCount\", \"lngModulID\", \"strCustom1\", \"blnStudentLeistungPruefung\", \"sngStudentLeistungCreditPts\") VALUES " +
			" ( " + this.getSdSeminarID() + ", " + this.m_lngPruefungID + ", '" + 
                        this.m_StudentData.getMatrikelnummer() + "'," + this.getLeistungID() + 
                        ", " + this.getLeistungCount() + ", " + this.getModulID() + 
                        ", 'Auto " + this.m_strReduktionsvermerk + "', " + 
                        (this.chosenAsPruefungsleistung() ? "true" : "false") + ", " + 
                        (this.getLeistungCreditPoints()-this.getLeistungPLReduktion()) + " );\n\n";		
	}
	
	/**
	 * An internal, extracted method: after reading the next credit in this 
	 * module and identifying it as a Pruefungsleistung, the object vars are set accordingly. 
	 * @see #nextLeistung()
	 * @see #updateLeistung()
	 */
	private void updatePruefungsleistungen() {

	  m_fltCreditPointsPLSum 	 += m_fltCreditPointsPL;
	  this.m_intLeistungUseOption = this.OPT_PRUEFUNGSLEISTUNG;
	  
	  // if the sum exceeds what is needed, this credit's credit-points are reduced (vermindert).
	  if ( this.m_fltCreditPointsPLSum > this.m_fltMinCreditPtsPLeistung ){
		this.m_fltLPPruefungVermindert = this.m_fltCreditPointsPLSum-this.m_fltMinCreditPtsPLeistung;
		this.m_fltCreditPointsPLSum    = this.m_fltMinCreditPtsPLeistung;
		m_strReduktionsvermerk		   = "Reduktion um " + m_fltLPPruefungVermindert + " (von " + m_fltCreditPointsPL + ")";		
		this.m_fltCreditPointsPL 	  -= this.m_fltLPPruefungVermindert;
	  }
	  
	  // recalculate the grade
      updateGrade();
	}
	
	// ---------------------------------------------------------
	// 2. Boring code
	// ---------------------------------------------------------	
	/**
	 * @return date when last credit was issued (relative to calls of @see #nextLeistung()).
	 */
	private java.util.Date getMaxDate(){
		return new java.util.Date(this.m_lngDateMaxMillisec);
	}
	
	/**
	 * @return for summing up: which was the latest date?
	 */
	protected long getMaxDateMillisec(){
		return this.m_lngDateMaxMillisec;
	}
		
	/**
	 * If you want this bean to ignore the 'manual' input in the 
	 * request (i.e. the parameters 'cboWertung-XYZ', then set this
	 * property to true. The default is false, however.
	 * @param value default is false.
	 */
	public void setIgnoreManualChoice(boolean value){
		this.m_blnIgnoreManualChoice=value;
	}
	
	/**
	 * Are there cboWertung-xyz parameters in the request that specify how to use
	 * the credits for this exam?
	 * @return true if there seem to be manually set combo-values, false if not. 
	 * <b>Note</b> that this method delivers sensible values only after @see #nextLeistung() has
	 * been called.<br />
	 * <b>Note</b> that this method returns 'false' if @see #setIgnoreManualChoice(boolean) has been 
	 * set tu 'true.'
	 */
	public boolean containsManualChoice(){
		return ((this.getChosenUseOption()!=this.OPT_NO_CHOICE_IN_REQUEST) && (!this.m_blnIgnoreManualChoice));
	}
	
	/**
	 * @return is the currently loaded Leistung a Pruefungsleistung?
	 */
	public boolean isPruefungsleistung(){
		return this.m_blnStudentLeistungPruefung;
	}
	
	/**
	 * @return credit points of the currently loaded Leistung.
	 */
	public float getLeistungCreditPoints(){
		return this.m_fltCreditPoints;
	}
	
	/**
	 * @return current sum of credit points. NOTE THAT this depends on how often 'nextLeistung()' has
	 * been called! It is not a *final sum*.
	 */
	public float getCreditPointCurrentSum(){
		return this.m_fltCreditPointsSum;
	}

	/**
	 * @return current sum of credit points pruefungsleistungen. NOTE THAT this depends on how often 'nextLeistung()' has
	 * been called! It is not a *final sum*.
	 */
	public float getCreditPointCurrentSumPL(){
		return this.m_fltCreditPointsPLSum;
	}
	
	/**
	 * @return id of currently loaded Leistung. Call 'nextLeistung()' first, to load a new Leistung.
	 */
	public long getLeistungID(){
		return this.m_lngLeistungID;
	}

	/**
	 * @return count of currently loaded Leistung. Call 'nextLeistung()' first, to load a new Leistung.
	 */
	public long getLeistungCount(){
		return this.m_lngLeistungCount;
	}
	
	/**
	 * @return name of currently loaded Leistung. Call 'nextLeistung()' first, to load a new Leistung.
	 */
	public String getLeistungBezeichnung(){
		return this.m_strLeistungBezeichnung;
	}
	
	/**
	 * @return course-title of currently loaded Leistung. Call 'nextLeistung()' first, to load a new Leistung.
	 */
	public String getLeistungKurstitel(){
		return this.m_strKursTitel;
	}	
	
	/**
	 * @return Are this credit's Pruefungsleistungs-LP diminished? (If the sum of Pruefungsleistungen 
	 * exceeds the requirement, credit points may be deduced from single credits. An example:
	 * If in a module 10 LP Pruefungsleistungen are needed, and the student has the following credits:<br />
	 *  <ul><li>5 LP Credit 1, grade: 1</li>
	 * <li>3 LP Credit 2, grade: 2</li>
	 * <li>7 LP Credit 3, grade: 4</li></ul>
	 * SignUp will suggest 5 + 3 + 2-of-7 in order to calculate the best possible final grade.
	 * In this example, the last grade (7 LP) will have LeistungPLVermindert>0.
	 */
	public float getLeistungPLReduktion(){
		return (this.m_fltLPPruefungVermindert);
	}
	
	/**
	 * According to the current count of Leistungspunkte etc.: Suggest a Pruefungsleistung?
	 * @return Automatic suggestion
	 */
	public boolean suggestPruefungsleistung(){
		return (this.m_intLeistungUseOption==this.OPT_PRUEFUNGSLEISTUNG);
	}
	
	/**
	 * Boolean value indicating whether the Http request looks as if the Leistung was 
	 * chosen as a Pruefungsleistung. (That is the request's parameter 'cboWertung-' + getCheckboxName 
	 * has the value OPT_PRUEFUNGSLEISTUNG).
	 * <b>Note</b> that the method returns false if the current Leistung IS NO PRUEFUNGSLEISTUNG, 
	 * no matter what was chosen.
	 * @return Manual choice according to HttpServletRequest relative to current Leistung
	 */
	public boolean chosenAsPruefungsleistung(){
		if(!this.m_blnStudentLeistungPruefung) return false;
		return (this.getChosenUseOption()==this.OPT_PRUEFUNGSLEISTUNG);
	}

	/**
	 * According to the current count of Leistungspunkte etc.: Suggest a Studienleistung?
	 * @return Automatic suggestion
	 */
	public boolean suggestStudienleistung(){
		return (this.m_intLeistungUseOption==this.OPT_STUDIENLEISTUNG);
	}

	/**
	 * Boolean value indicating whether the Http request looks as if the Leistung was 
	 * chosen as a Studienleistung. (That is the request's parameter 'cboWertung-' + getCheckboxName 
	 * has the value OPT_STUDIENLEISTUNG).
	 * @return Manual choice according to HttpServletRequest relative to current Leistung
	 */
	public boolean chosenAsStudienleistung(){
		return (this.getChosenUseOption()==this.OPT_STUDIENLEISTUNG);
	}
	
	/**
	 * @return Sum of weighed grades in this module; hand-on method for managing class. Pends on 'nextLeistung'.
	 */
	protected float getGradeSum(){
		return this.m_fltGradeSum;
	}
	
	/**
	 * @return SQL statements to add credits to StudentPruefungDetail, pending on @see #nextLeistung().
	 */
	protected String getSQL(){
		return this.m_strSQL;
	}
	
	/**
	 * According to the current count of Leistungspunkte: Suggest not to use this Credit at all?
	 * @return Automatic suggestion
	 */
	public boolean suggestDontUse(){
		return (this.m_intLeistungUseOption==this.OPT_NULL);
	}

	/**
	 * Boolean value indicating whether the Http request looks as if the Leistung was unselected. 
	 * (That is the request's parameter 'cboWertung-' + getCheckboxName 
	 * has the value OPT_NULL OR IS NULL).
	 * @return Manual choice according to HttpServletRequest relative to current Leistung
	 */
	public boolean chosenAsDontUse(){
		return (this.getChosenUseOption()==this.OPT_NULL);
	}	

	/**
	 * Was the decision to use this credit as a Pruefungsleistung or Studienleistung
	 * contrary to SignUp's original suggestion?
	 * @return
	 */
	public boolean isManualOverride(){
		return this.m_blnManualOverride;
	}
	
	/**
	 * Alternative to the 'suggestXYZ'-methods. The suggestion can be either
	 * <br />0 = DontUse,<br />
	 * 1 = Use as Studienleistung<br />
	 * 2 = Use as Pruefungsleistung.
	 * @return
	 */
	public int getUseOption(){
		return this.m_intLeistungUseOption;
	}
	
	/**
	 * Was the current Leistung (current=relative to @see #nextLeistung() chosen as Studienleistung oder Pruefungsleistung?
	 * Returns OPT_NO_CHOICE_IN_REQUEST if it seems that there was no choice at all.
	 * @return
	 */
	private int getChosenUseOption(){
		return (this.m_request.getParameter("cboWertung-" + getCheckboxName())==null) ? this.OPT_NO_CHOICE_IN_REQUEST : Integer.parseInt(this.m_request.getParameter("cboWertung-" + getCheckboxName())); 
	}	
	
	/**
	 * @return Minimum CreditPts needed in this module for the current exam.
	 */
	public float getMinCreditPts(){
		return this.m_fltMinCreditPts;
	}
	
	
	/**
	 * @return Minimum Pruefungsleistungen CreditPoints needed in this module for the current exam.
	 */
	public float getMinCreditPtsPLeistung(){
		return this.m_fltMinCreditPtsPLeistung;
	}
	
	/**
	 * @return A String constructed as ModulID[xl]LeistungID[xc]LeistungCount (without the brackets), 
	 * e.g. 3xl27xc2 is the checkbox for Leistung 27 of module 3, leistung-count number 2.
	 * Note that this method is relative to calls of getNextLeistung().
	 */
	public String getCheckboxName(){
		return this.getModulID() + "xl" + this.getLeistungID() + "xc" + this.getLeistungCount();
	}
	
	/**
	 * @return how many credit-points are 'currently' (pending on 'LeistungNext()') missing?
	 */
	public float getCurrentlyMissingCreditPoints(){
		return (this.getMinCreditPts()-this.getCreditPointCurrentSum());
	}

	/**
	 * @return how many credit-points Pruefungsleistungen are 'currently' (pending on 'LeistungNext()') missing?
	 */
	public float getCurrentlyMissingCreditPointsPL(){
		return (this.getMinCreditPtsPLeistung()-this.getCreditPointCurrentSumPL());
	}

	
	/**
	 * "NotAlreadyCounted==true" (default) means that only those credits 
	 * are allowed in the selection that are not already counted as 
	 * a Pruefungsleistung in another exam.<br />
	 * If you want to allow credits to be counted as Pruefungsleistung 
	 * in more than one exam, then set this property to false.
	 * @param value true or false, default is true.
	 */
	public void setNotAlreadyCounted(boolean value){
		this.m_blnNotAlreadyCounted=value;
	}

	
	/**
	 * "NotAlreadyCounted==true" (default) means that only those credits 
	 * are allowed in the selection that are not already counted as 
	 * a Pruefungsleistung in another exam.<br />
	 * @return current value: is it forbidden to count credits as Pruefungsleistung 
	 * for two exams?
	 */
	public boolean getNotAlreadyCounted(){
		return this.m_blnNotAlreadyCounted;
	}


	/**
	 * Sets simple object variables.
	 * @see #nextLeistung()
	 * @see #updateLeistung()
	 * @throws Exception
	 * @throws SQLException
	 */
	private void updateLeistungTrivia() throws Exception, SQLException {
		this.LeistungNote 					= new Note(this.m_rstLeistungen);
		updateLeistungMaxDate();
		
		this.m_blnStudentLeistungPruefung 	= this.m_rstLeistungen.getBoolean("blnStudentLeistungPruefung");
		this.m_fltCreditPoints	 		 	= this.m_rstLeistungen.getFloat("sngStudentLeistungCreditPts");
		
		if (m_blnStudentLeistungPruefung) m_fltCreditPointsPL=m_fltCreditPoints;
		
		this.m_lngLeistungID				= this.m_rstLeistungen.getLong("lngLeistungID");
		this.m_lngLeistungCount				= this.m_rstLeistungen.getLong("lngStudentLeistungCount");
		this.m_strLeistungBezeichnung		= this.m_rstLeistungen.getString("strLeistungBezeichnung");
		this.m_strKursTitel 				= this.m_rstLeistungen.getString("strSLKursTitel");
	}

	/**
	 * Sets max date (latest date is date of exam).
	 * @see #nextLeistung()
	 * @see #updateLeistung()
	 * @see #updateLeistungTrivia()
	 * @throws SQLException
	 */
	private void updateLeistungMaxDate() throws SQLException {
		long lngTmp = this.m_rstLeistungen.getDate("dtmStudentLeistungAusstellungsd").getTime();
		if(lngTmp > this.m_lngDateMaxMillisec) this.m_lngDateMaxMillisec = lngTmp;
	}



	/**
	 * After reading next credit, identifying it as Pruefungsleistung and 
	 * setting the object-vars accordingly, the grade-calculation is updated.
	 * @see #nextLeistung()
	 * @see #updateLeistung()
	 * @see #updatePruefungsleistungen()
	 */
	private void updateGrade() {
		m_fltGradeSum += this.LeistungNote.getNoteECTSCalc() * this.m_fltCreditPointsPL;
	}

	/**
	 * reset properties 'UseOption', 'LPVermindert' and m_fltCreditPointsPL after calling .nextLeistung
	 * and before calling .updateLeistung.
	 * @see #nextLeistung()
	 * @see #updateLeistung()
	 */
	private void resetLeistung() {
		// reset:
		m_intLeistungUseOption				= 0;
		this.m_fltLPPruefungVermindert		= 0;
		m_fltCreditPointsPL 				= 0;
		m_blnManualOverride					= false;
		m_strReduktionsvermerk				= "";
	}

	/**
	 * Initiate
	 * @param rst should be from PruefungData.getModules().
	 * @param r can contain the combo-boxes cboWertung-[code].
	 * @throws Exception
	 */
	public PruefungXModulBean(ResultSet rst, StudentData student,HttpServletRequest r) throws Exception {
		super(rst);
		this.m_request			= r;
		this.m_fltMinCreditPts 	= rst.getFloat("sngMinCreditPts");
		this.m_fltMinCreditPtsPLeistung = rst.getFloat("sngMinCreditPtsPLeistung");
		this.m_lngPruefungID 	= rst.getLong("lngPruefungID");
		this.m_StudentData 		= student;
	}
	
	public void finalize(){
		try{this.m_rstLeistungen.close();}catch(Exception e){}
	}


}
