package de.shj.UP.beans.config.student;

import de.shj.UP.HTML.HtmlSeminar;
import de.shj.UP.HTML.HtmlDate;
import java.sql.SQLException;

import de.shj.UP.data.Kurs;
import de.shj.UP.data.KvvArchiv;
import de.shj.UP.logic.StudentData;
import de.shj.UP.logic.StudentLeistung;
import javax.servlet.http.*;
import de.shj.UP.data.Dozent;
import de.shj.UP.data.Note;
import de.shj.UP.data.KursXKurstyp;
;

/**
 * @author heiko jakubzik
 * @version 5-29-5
 * This class handles the detailed view on one specific credit of 
 * one specific student. Credits can be updated and added, also 
 * commitments can be turned into credits.<br />
 * changes<br />
 * 15.5.2005	hj		.setRequestDozent() now handles teacher's first name and academic title correctly.
 * 
 * 3.2.2006		hj		m_blnCheckDoublette added
 * 
 * 6.3.2006		hj		modified .loadCourse, so that it now sets
 * 						KlausuranmeldungKurstypID := request.getParameter("txtKurstypID").
 * 
 * 14.4.2006	hj		.addWithRequestVals() now sets lngStudentLeistungCount to 1 (if it is 0)
 * 
 * 30.5.2006	hj		.setRequestValues(), intricate change on dtmStudentLeistungAntragdatum:
 * 						Up to now, the Antragdatum was erased on editing by Config. This 
 * 						is now changed.
 * 
 * (15.8.2006	hj		changed StudentLeistung.loadCourse)
 * 
 * 28.2.2011	hj		cboLeistungIDAlt zum Wechel des Leistungstyps eingeführt
 * 
 * 30.1.2012	hj		updatemöglichkeit auch für SDKursBeschreibung
 */
public class StudentLeistungDetailBean extends StudentLeistung {	

	private static final long serialVersionUID = -9004393584474816332L;

	//	private static final byte MODE_UNINITIALIZED	=-1;
	private static final byte MODE_ADD_NEW_CREDIT	= 0;
	private static final byte MODE_ADD_PICKED_CREDIT= 1;	
	private static final byte MODE_DISPLAY_CREDIT	= 2;
	private static final byte MODE_PERFORM_UPDATE	= 3;
	private static final byte MODE_PERFORM_ADDITION	= 4;
	
	private boolean m_blnBeanMode					= false;	//is used in version >=5-30-2
	
	private boolean m_blnBestanden				=false;
	private boolean m_blnPruefungsleistung		=false;
	private boolean m_blnDetail					=false;
	private byte    m_bytMode					=-1;
	private String  m_strLeistungBezeichnung	= "";
//	private long	m_lngKursID					= -1;	// ID of picked course, if one was picked ...
	
//	private int	    m_intNoteID					= -1;	
	private boolean m_blnCheckDoublette			=false;
	private Note	m_Note						= null;
	private HttpServletRequest m_request		= null;
	
	private HtmlSeminar seminar					= null;
	private boolean m_blnHasPerformed			= false;
	
	private String m_dbg="";

	public String getDebug(){
		return m_dbg;
	}
	
	/**
	 * <pre>
	 * If set to 'true' (not the default), 
	 * @link #addWithRequestVals() throws an 
	 * Exception if you try to add a credit of 
	 * a type that is already added, or to which 
	 * an exam-application already exists.
	 * 
	 * TODO: Since this methodology is new in 6-11-05, 
	 * the default is set to 'false'. The default 
	 * should probably be 'true,' in the long run, but 
	 * only after thorough tests.
	 * 
	 * Doublette-checking is done using:
	 * @link com.shj.signUp.logic.StudentData#checkCommitmentDoublette(long, String, long), 
	 * which has been made public for that purpose.
	 * </pre>
	 * @param value
	 */
	public void setCheckDoublette(boolean value){
		m_blnCheckDoublette=value;
	}
	
	/**
	 * Former MODE_ADD_NEW (offer an empty form)
	 * @return Does the request look as if it wants to be offered an empty for to 
	 * enter data of a new credit?
	 * @see #isModeDisplayCredit()
	 */
	public boolean isModeEmptyForm(){
		return (this.m_bytMode==MODE_ADD_NEW_CREDIT);
	}
	
	/**
	 * Is this request picked from the course-catalogue?
	 * @return Does this request contain a "txtKursID" of a course?
	 */
	public boolean isModeAddPickedCourse(){
		return (this.m_bytMode==MODE_ADD_PICKED_CREDIT);
	}
	
	/**
	 * @return true, if the request points to a credit that can be displayed
	 * @see #isModeEmptyForm()
	 */
	public boolean isModeDisplayCredit(){
		return (this.m_bytMode==MODE_DISPLAY_CREDIT);
	}
	
	/**
	 * strLeistungBezeichnung of the request, or 'ADD' if it was empty in the request.
	 * @return strLeistungBezeichnung of the request, or 'ADD' if it was empty in the request.
	 */
	public String getLeistungBezeichnung(){
		return this.m_strLeistungBezeichnung;
	}
	
	/**
	 * @return true if either .update or .add had been (successfully) called. False otherwise.
	 */
	public boolean hasPerformed(){
		return m_blnHasPerformed;
	}
	
	/**
	 * Html Data object, initialized with sensible preselection.
	 * Or (new in Jan 2008) with a hidden textfield by the name of "txtStudentLeistungAusstellungsdatum".
	 * @return Date-object that makes sense:<br /> 
	 * <ul><li>date of today in add-mode or add-picked-course-mode (non-performant), or if this is a commitment-originated  
	 * credit; </li><li>date reconstructed from request-combos if mode is 'performUpdate' or 'performAddition'</li>
	 * <li>otherwise the preselect is getStudentLeistungAusstellungsdatum.</li><li>null in case of error.</li></ul>
	 * @see #getDateCbo()
	 */	
	public HtmlDate getDate(){
		 try {
			if(this.m_bytMode==MODE_PERFORM_UPDATE || this.m_bytMode==MODE_PERFORM_ADDITION){

				if(m_request.getParameter("txtStudentLeistungAusstellungsdatum")!=null)
					return new HtmlDate(m_request.getParameter("txtStudentLeistungAusstellungsdatum"), m_request.getLocale());
				else
					return new HtmlDate(m_request, m_request.getLocale());
			}
			
			if(this.isCommitmentOriginated() || this.m_bytMode==MODE_ADD_NEW_CREDIT || this.isModeAddPickedCourse()){
				return new HtmlDate(m_request.getLocale());
			}

			return new HtmlDate((g_ISO_DATE_FORMAT.format(this.getStudentLeistungAusstellungsdatum())), m_request.getLocale() );
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Html Data selector with sensible preselection.
	 * @return Date-object that makes sense:<br /> 
	 * <ul><li>date of today in add-mode (non-performant), or if this is a commitment-generated 
	 * credit; </li><li>date reconstructed from request-combos if mode is 'performUpdate' or 'performAddition'</li>
	 * <li>otherwise the preselect is getStudentLeistungAusstellungsdatum.</li><li>null in case of error.</li></ul>
	 * @see #getDate()
	 */
	public String getDateCbo(){
		return this.getDate().getDateCombo();
	}
	
	/**
	 * param 'cboNote' of request turned into the Note-objext. Null on error (or if no parameter 'cboNote' exists).
	 * @return Note-object representing 'cboNote' of the request.
	 * 
	 */
	public Note getRequestNote(){
		if(m_Note==null){
			try{
				  m_Note = new Note( this.getSeminarID(), Integer.parseInt(m_request.getParameter("cboNote")) );
			}catch(Exception eNoGradeHandedOver){m_Note=null;}
		}
		return m_Note;
	}
	
	/**
	 * Does the request-object say that this credit is a Pruefungsleistung? (As 
	 * opposed to .getStudentLeistungPruefung, which returns the stored value.
	 * @return true, if this request contains chkPruefung, false otherwise.
	 * @see com.shj.signUp.data.StudentXLeistung#getStudentLeistungPruefung()
	 */
	public boolean isRequestPruefungsleistung(){
		return this.m_blnPruefungsleistung;
	}
	
	/**
	 * value of 'cboDozentID'
	 * @return Id of teacher according to the request.
	 * @throws Exception if 'cboDozentID' is NaN, or if its value is <=0 (for then 
	 * SignUp thinks this is a dummy teacher).
	 */
	public long getRequestDozentID() throws Exception{
		long lngDozentID = Long.parseLong(m_request.getParameter("cboDozentID"));
		if(lngDozentID<=0) throw new Exception("Dummy-teacher-id cannot be used in credits/commitments");
		return lngDozentID;
	}
	
	/**
	 * Is this a commitment that is in the process of being turned 
	 * into a credit?
	 * @return 'true' if the request contains 'cmdCommitment'
	 * @see #isRequestCommitment()
	 */
	public boolean isCommitmentOriginated(){
		return (m_request.getParameter("cmdCommitment")!=null);
	}
	
	/**
	 * Does this request have a checked 'Commitment' checkbox?
	 * @return true if request contains 'chkCommitment'.
	 * @see #isCommitmentOriginated()
	 */
	public boolean isRequestCommitment(){
		return (m_request.getParameter("chkCommitment")!=null);
	}
	
	/**
	 * Has this request been sent from a detail-view? If so, on 
	 * update or add, the KursDetail-properties are set from form.
	 * @TODO: Does this make sense? shouldn't there be a default updating 
	 * mechanism?
	 * @return true, if the request contains param Mode="Detail"
	 */
	public boolean isRequestDetails(){
		return this.m_blnDetail;
	}		

	
	/**
	 * Name of form-submit-button
	 * @return Either 'cmdAdd' (in add-mode), or 'cmdUpdate' (Display-Mode). Returns '#error' in case of error. 
	 */
	public String getCommandName(){
		switch(this.m_bytMode){
			case MODE_ADD_NEW_CREDIT:
				return "cmdAdd";
			
			case MODE_ADD_PICKED_CREDIT:
				return "cmdAdd";
			
			case MODE_DISPLAY_CREDIT:
				return "cmdUpdate";
		}
		
		return "#error";
	}
	
	/**
	 * Html-Combo-box with teachers.
	 * @return HTML-select of seminar's teachers, either with a default (if there is 
	 * a credit to be displayed), or without one (if this is add-mode). Returns '#error' in case 
	 * of error. (Default is to return a combo with preselect, if the DozentID() is &gt; 0 and 
	 * a combo without preselect if DozentID is &lt;= 0).
	 * @see com.shj.signUp.HTML.HtmlSeminar#getDozentCbo()
	 * @see com.shj.signUp.HTML.HtmlSeminar#getDozentCbo(long) 
	 */		
	public String getDozentCbo(){
		try {
			switch(m_bytMode){
				case MODE_ADD_PICKED_CREDIT:
					if(this.getDozentID() <= 0) this.guessDozentId();
					return this.seminar.getDozentCbo(this.getDozentID());
			
				case MODE_ADD_NEW_CREDIT:
					return this.seminar.getDozentCbo();
									
				case MODE_DISPLAY_CREDIT:
					return this.seminar.getDozentCbo(this.getDozentID());
				
				default:
					if(this.getDozentID() <= 0)
						return this.seminar.getDozentCbo();
					
					return this.seminar.getDozentCbo(this.getDozentID());
			}
				
		} catch (Exception e) {}
		return "#error";
	}

	/**
	 * Guesses LeistungId from requestparam "txtLeistungBezeichnung".
	 * Internal: call only in MODE_ADD_PICKED_CREDIT. <br /><br />
	 * For records generated before version 6-05.xx (July 2005), If 
	 * the course handed over from the archive, this involves a bit of guessing. 
	 * The guesswork, here is done through LeistungBezeichnung equality. 
	 */
	private void guessLeistungId(){
		long lngLeistungID = 0;
		
		if(isArchivedCourse()){
			// First try: the course in the archive has (as of version 6-05 or July 2005)
			// a LeistungID (and LeistungName) stored along with it.
			// This is looked up first:
			try {
				lngLeistungID = Long.parseLong(lookUp("lngKvvArchivLeistungID", "tblBdKvvArchiv", 
						"\"lngID\"=" + m_request.getParameter("txtKursID") + ""));
			}catch(Exception eNotFound){
				lngLeistungID = 0;
			}
		}
		
		// Fallback: looking up the LeistungID didn't work or produced a null:
		if(lngLeistungID==0){
			try {
				lngLeistungID = Long.parseLong(lookUp("lngLeistungID", "tblSdLeistung", 
						"\"lngSdSeminarID\"=" + this.getSdSeminarID() + " AND " +
						"\"strLeistungBezeichnung\"='" + m_request.getParameter("txtLeistungBezeichnung") + "'"));
				this.setLeistungsID( lngLeistungID );
			} catch (NumberFormatException e) {
			} catch (Exception e) {}
		}
	}	
	
	/**
	 * Sets this.DozentID to a good value (or leaves it unchanged).
	 * Internal: called only in MODE_ADD_PICKED_CREDIT. If 
	 * the course handed over from the archive, this involves a bit of guessing. 
	 * The guesswork, here is done through equality of last name, first name 
	 * and SeminarID. 
	 * This might need hacking and is on the todo list. Then again ...
	 * TODO Take DozentID into tblBdKvvArchiv
	 */
	private void guessDozentId(){
		
		if(getDozentID() <= 0){
			if(this.isArchivedCourse()){
				try{
					long lngDozentID = Long.parseLong(lookUp("lngDozentID", "tblSdDozent", 
							"\"lngSdSeminarID\"=" + this.getSdSeminarID() + 
							" AND \"strDozentNachname\"='" + 
							((KvvArchiv)getKursobjekt()).getKvvArchivDozentname() + "' And " +
							 "\"strDozentVorname\"='" + ((KvvArchiv)getKursobjekt()).getKvvArchivDozentvorname() + "'"));

					this.setDozentID( lngDozentID );
				}catch(Exception eTeacherNotFound){}
			}
			
			if(this.isCurrentCourse()){
				this.setDozentID( ((Kurs) this.getKursobjekt()).getDozentID() );
			}
		}
	}

	/**
	 * Internal, for MODE_ADD_PICK only. Tries to set LeistungCreditPoints to 
	 * requestparam txtCreditPoints (which is where it comes from in addpick-mode. 
	 */
	private void guessCreditPoints(){
		try {
			this.setStudentLeistungCreditPts(
					new Float(shjSubstitute(m_request.getParameter("txtCreditPoints"), ",",".")).floatValue()
			);
		} catch (NumberFormatException e) {}
	}
	
	/**
	 * Html-Combo-box with credits (generic types, Leistung).
	 * @return HTML-select of seminar's credits, either with a default (if there is 
	 * a credit to be displayed), or without one (if this is add-mode). Returns '#error' in case 
	 * of error. (Default is to return combo without preselect).
	 * @see com.shj.signUp.HTML.HtmlSeminar#getLeistungCbo()
	 * @see com.shj.signUp.HTML.HtmlSeminar#getLeistungCbo(long) 
	 */
	public String getLeistungCbo(){
		try {
			switch(m_bytMode){
				case MODE_ADD_NEW_CREDIT:
					return this.seminar.getLeistungCbo();
					
				case MODE_DISPLAY_CREDIT:
					return this.seminar.getLeistungCbo(this.getLeistungsID());
					
				default:
					if(getLeistungsID() <= 0)
					  return this.seminar.getLeistungCbo();
					return this.seminar.getLeistungCbo(getLeistungsID());
			}
				
		} catch (Exception e) {}
		return "#error";
	}
	

	/**
	 * Html-Combo-box with grades.
	 * @return HTML-select of seminar's grade, either with a default (if there is 
	 * a credit to be displayed), or without one (if this is add-mode). Returns '#error' in case 
	 * of error. (Default is to return the combo without preselect).
	 * @see com.shj.signUp.HTML.HtmlSeminar#getNoteCbo()
	 * @see com.shj.signUp.HTML.HtmlSeminar#getNoteCbo(int) 
	 */
	public String getNoteCbo(){
		try {
			switch(m_bytMode){
				case MODE_ADD_NEW_CREDIT:
					return this.seminar.getNoteCbo();

				case MODE_DISPLAY_CREDIT:
					//return this.seminar.getNoteCbo(m_intNoteID);
					return this.seminar.getNoteCbo(this.getNoteID());
					
				default:
					if(getNoteID() <= 0)
						return this.seminar.getNoteCbo();
					
					return this.seminar.getNoteCbo(getNoteID());
			}
				
		} catch (Exception e) {}
		return "#error";
	}

	/**
	 * Reads and casts CreditPoints (with . or , as decimal point) to float value.
	 * @return Float value of param 'txtStudentLeistungCreditPts' or error is caused. Decimal can 
	 * be either . or ,
	 * @throws Exception (ERR_ECTS_CREDIT_FORMAT)
	 */
	public float getRequestCreditPoints() throws Exception{
		try{
			return new Float(shjSubstitute(m_request.getParameter("txtStudentLeistungCreditPts"), ",",".")).floatValue();
		}catch(Exception e){System.out.println(e.getMessage() + ", " + m_request.getParameter("txtStudentLeistungCreditPts"));throw new Exception(String.valueOf(ERR_ECTS_CREDIT_FORMAT));}
	}
	
	/**
	 * @param r must contain the following requestparams [not sure if complete]:<br />
	 * #requestparam intNoteID (optional): grade,<br />
	 * #requestparam txtStudentPID (long), Student identifier,<br />
	 * #requestparam txtLeistungID (long), (Optional) Credit identifier 1,<br />
	 * #requestparam txtStudentLeistungCount (long), Credit identifier 2,<br />
	 * #requestparam txtDozentID (long), issuer/teacher identifier,<br />
	 * #requestparam txtStudentPID (long), Student identifier,<br />,
	 * #requestparam strLeistungBezeichnung (String), credit name<br />,
	 * 
	 * #requestparam chkBestanden (exists or not), indicative of passing of credit (not used anymore)<br />,
	 * #requestparam chkPruefung (exists or not), Pruefungsleistung?<br />,
	 * #requestparam Mode (usertype), ="Detail" means that details are to be displayed.<br />,
	 * 
	 * #requestparam txtStudentLeistungCreditPts (optional, needed if you want to call 'add' or 'update'
	 * 
	 * #requestparam cmdAdd (optional, means that the request-info is to be converted to object and added to db.
	 * #requestparam cmdUpdate (optional, means that the request-info is to be converted to object and updated in db.
	 * @see #getCommandName()
	 * @throws Exception
	 * @throws SQLException
	 */
	public void init(long lngSeminarID, String strMatrikelnummer, HttpServletRequest r) throws Exception, SQLException {
		m_blnBeanMode=true;
		this.setSeminarID(lngSeminarID);
		this.setMatrikelnummer(strMatrikelnummer);
		initialize(r);
	}	
	
	/**
	 * @param r must contain the following requestparams [not sure if complete]:<br />
	 * #requestparam s (or idxs), SeminarID,<br />
	 * #requestparam txtMatrikelnummer (student),<br />
	 * #requestparam intNoteID (optional): grade,<br />
	 * #requestparam txtStudentPID (long), Student identifier,<br />
	 * #requestparam txtLeistungID (long), (Optional) Credit identifier 1,<br />
	 * #requestparam txtStudentLeistungCount (long), Credit identifier 2,<br />
	 * #requestparam txtDozentID (long), issuer/teacher identifier,<br />
	 * #requestparam txtStudentPID (long), Student identifier,<br />,
	 * #requestparam strLeistungBezeichnung (String), credit name<br />,
	 * 
	 * #requestparam chkBestanden (exists or not), indicative of passing of credit (not used anymore)<br />,
	 * #requestparam chkPruefung (exists or not), Pruefungsleistung?<br />,
	 * #requestparam Mode (usertype), ="Detail" means that details are to be displayed.<br />,
	 * 
	 * #requestparam txtStudentLeistungCreditPts (optional, needed if you want to call 'add' or 'update'
	 * 
	 * #requestparam txtStudentLeistungCustom2 (optional, med-faculty uses this as Modulzeitraum as of version 6-06)
	 * 
	 * #requestparam cmdAdd (optional, means that the request-info is to be converted to object and added to db.
	 * #requestparam cmdUpdate (optional, means that the request-info is to be converted to object and updated in db.
	 * @see #getCommandName()
	 * @throws Exception
	 * @throws SQLException
	 * @deprecated as of version 5-32; please use @see #init(long, String, HttpServletRequest) instead.
	 */
	public void init(HttpServletRequest r) throws Exception, SQLException {
		initialize(r);
	}

	/**
	 * @param r
	 * @throws Exception
	 * @throws SQLException
	 */
	private void initialize(HttpServletRequest r) throws Exception, SQLException {
		m_request = r;
		initObjectVarsFromRequest();	
		
		long 	  lngLeistungID			 = -1;
		long	  lngStudentLeistungCount= -1;
		long	  lngLeistungIDAlt		 = -1;
		
		try {
			lngLeistungID		 	 = Long.parseLong(r.getParameter("txtLeistungID"));
			lngStudentLeistungCount	 = Long.parseLong(r.getParameter("txtStudentLeistungCount"));
			lngLeistungIDAlt		 = Long.parseLong(r.getParameter("cboLeistungAlt"));
		} catch (NumberFormatException e) {}
		
		m_strLeistungBezeichnung 	 = r.getParameter("strLeistungBezeichnung");
		if(m_strLeistungBezeichnung == null) m_strLeistungBezeichnung="ADD";
			
		// Set add or update mode and init if this refers to an existing credit
		setMode( lngLeistungID, lngStudentLeistungCount, m_strLeistungBezeichnung);
				
		// If there's really a commitment to be displayed, load the details:
		if(isCommitmentOriginated() || isModeAddPickedCourse()) this.loadCourseDetails();
				
		// in case it's a commitment, set the credit pts. as default from 
		// table tblKursXKurstyp.
		if(isCommitmentOriginated()){
			this.setStudentLeistungCreditPts(new KursXKurstyp(this.getSdSeminarID(), 
							this.getKlausuranmeldungKurstypID(), 
							this.getKlausuranmeldungKursID()).getKursCreditPts());
		}
		
		// ---------------------------------------------------------------------
		// 1. P e r f o r m  U p d a t e  i f   c a l l e d
		// ---------------------------------------------------------------------
		if(m_request.getParameter("cmdUpdate") != null)	  updateWithRequestVals();

		// ---------------------------------------------------------------------
		// 2. P e r f o r m  A d d i t i o n  i f   c a l l e d
		// ---------------------------------------------------------------------
		if(m_request.getParameter("cmdAdd") != null){
		  addWithRequestVals();
		}
	}

	/**
	 * <pre>
	 * Perform addition of the credit that is inherent in the request
	 * 
	 * Also, if checkDoublett is set to 'true' (not the default), 
	 * @link com.shj.signUp.logic.StudentData#checkCommitmentDoublette(long, String, long) 
	 * is called to avoid issuing double credits.
	 * 
	 * New in version 6-14-04 (Easter 2006): lngLeistungCount is at least 1 (not 0).
	 * </pre>
	 * @throws Exception
	 */
	private void addWithRequestVals() throws Exception {
		  this.m_bytMode=MODE_PERFORM_ADDITION; 
		  this.setRequestValues();
		  
		  // Kursdetails aus Kvv bzw. KvvArchiv laden, falls angegeben (?ber "Suchen", Kursauswahl etc.)
		  // #hack: x.loadCourse (...) gibt bool'schen Wert zur?ck, ob's geklappt hat.
		  // Eine Ausnahme wird gar nicht erzeugt. (Try ist nur wg. txtArchiv.equals("true").
		  if(m_request.getParameter("txtKursID")!=null){
		    loadCourseDetails();
		  }	  
 
		  setLeistungsID			( Long.parseLong(m_request.getParameter("cboLeistung")) );
		  setStudentLeistungCount( 
		  		getNextLeistungCount() 
				);
	
		  // A count of 0 is unwanted, esp. because of n strikes.
		  if(getStudentLeistungCount()==0) setStudentLeistungCount(1);

		  if(m_blnCheckDoublette) {
			  new StudentData().checkCommitmentDoublette(getSdSeminarID(), getMatrikelnummer(), getLeistungsID());
		  }
		  
		  if(!this.add()) throw new Exception(String.valueOf(ERR_CREDIT_ADDITION_FAILED_ON_DB));
		  this.m_blnHasPerformed=true;
	}

	/**
	 * Perform update of the credit with values inherent in the request.
	 * @throws Exception
	 * @throws SQLException
	 */
	private void updateWithRequestVals() throws Exception, SQLException {
		this.m_bytMode=MODE_PERFORM_UPDATE;
		  setRequestValues();	  
	  
		  // see if there is a change of Leistung/Credit that needs to be done:
		  try{
			  //hier
		    if(Long.parseLong(m_request.getParameter("cboLeistung"))!=this.getLeistungsID()){
		      if(this.getModulID()!= 0 || m_request.getParameter("txtModulID")!=null){
		    	  this.updateLeistungIdTo( Long.parseLong(m_request.getParameter("cboLeistung")),
		    			  Long.parseLong(m_request.getParameter("txtModulID")));
		      }else{
		    	  this.updateLeistungIdTo( Long.parseLong(m_request.getParameter("cboLeistung")) );
		      }
		  	  this.setLeistungsID(Long.parseLong(m_request.getParameter("cboLeistung")));
		    }
		  }catch(Exception eNoParameterCboLeistungHandedOver){}

		  if(!this.update()) throw new Exception(String.valueOf(ERR_CREDIT_UPDATE_FAILED_ON_DB));
		  try{
			  if(m_request.getParameter("txtModulID").equals("zusatz")){
				  // set ModulID to zero
				  this.sqlExe("update \"tblBdStudentXLeistung\" set \"lngModulID\"=null where " + 
						  "\"lngSdSeminarID\"=" + this.getSdSeminarID() + " and " +
						  "\"strMatrikelnummer\"='" + this.getMatrikelnummer() + "' and " +
						  "\"lngLeistungsID\"=" + this.getLeistungsID() + " and " +
						  "\"lngStudentLeistungCount\"=" + this.getStudentLeistungCount() + ";");
			  }
		  }catch(Exception nullingFailedIgnore){}
		  close();
		  this.m_blnHasPerformed=true;
	}

	/**
	 * Set HtmlSeminar, Matrikelnummer, blnBestanden, blnPr?fungsleistung, blnDetails from Request. 
	 */
	private void initObjectVarsFromRequest() throws Exception{
		
		// careful: both are needed, if they are not set extermally
		if(!m_blnBeanMode){
			this.setSeminarID(getSeminarID(m_request));
			this.setMatrikelnummer( m_request.getParameter("txtMatrikelnummer") );
		}
		
		this.setSdSeminarID( this.getSeminarID() );
		
		
		this.seminar 	 			 =  new HtmlSeminar( this.getSeminarID(), m_request.getLocale() );
			
		m_blnBestanden		 	 	 = (m_request.getParameter("chkBestanden")!=null);
		m_blnPruefungsleistung	 	 = (m_request.getParameter("chkPruefung")!=null);
		m_blnDetail 				 = normalize(m_request.getParameter("Mode")).equals("Detail");
	}

	/**<pre>
	 * Sets StudentXLeistung values 'SLKursXYZ' by loading the course 
	 * from SignUp's Kvv (either current or archive).<br />
	 * Course is specified by parameter <Code>txtKursID</Code>, while 
	 * parameter <Code>txtArchiv</Code> points SignUp to the archive if 
	 * set to <Code>true</Code>.
	 * 
	 * Since version 6-12 (March 2006), this method also looks for 
	 * a parameter "txtKurstypID" in the current request. If there is 
	 * a course-Id, and if there is a parameter "txtKurstypID", the 
	 * value of txtKlausuranmeldungKurstypID is set to 'txtKurstypID.'
	 * </pre>
	 * @see com.shj.signUp.logic.StudentLeistung#loadCourse(long, boolean)
	 * @throws Exception ERR_CREDIT_MAPPING_TO_COURSE_FAILURE
	 */
	private void loadCourseDetails() throws Exception {
		
		boolean blnArchive;
		
		try{
		  blnArchive = m_request.getParameter("txtArchiv").equals("true");
		}catch(Exception eWeird){blnArchive=false;}
		
		if (!(loadCourse(Long.parseLong(m_request.getParameter("txtKursID")), blnArchive)))
			throw new Exception(String.valueOf(ERR_CREDIT_MAPPING_TO_COURSE_FAILURE));
				
		// try to add the courstypeid: (new in March 2006).
		if(this.getKlausuranmeldungKursID() > 0){
			try{
				this.setKlausuranmeldungKurstypID(Long.parseLong(m_request.getParameter("txtKurstypID")));
			}catch(Exception eIgnore){}
		}
	}

	/**
	 * Request values that are set for updates and additions: Ausstellungsdatum, CreditPts, Bemerkung, 
	 * Details, Note, blnPruefungsleistung, but also details (if param txtSLKursTitel is not null 
	 * and <Code>this.isRequestDetails</Code>).<br /><br />
	 * Antragdatum is indicative of the credit being generated from a Commitment (if it is null or empty, 
	 * SignUp assumes that this <i>was no Commitment</i>. So if this request seems to be from 
	 * a commitment (param <Code>"cmdCommitment" not null</Code>, but the Antragdatum is null or empty, 
	 * SignUp will set it to the present date. If "chkCommitment" is null,  
	 * SignUp will delete the Antragdatum in this record.<br /><br />
	 * The teacher is either set, as is the grade.
	 * @see #setRequestDozent()
	 * @see #isRequestCommitment()
	 * @see com.shj.signUp.logic.StudentLeistung#wasCommitment()
	 * @see #isRequestDetails()
	 * @version 6-06 adds setting txtStudentLeistungCustom2, too (faculty of medicine needs that).
	 * @throws Exception ERR_CONFIG_CREDIT_TEACHER_MISSING, ERR_DATE_TOO_FAR_IN_FUTURE, ERR_CONFIG_CREDIT_GRADE_MISSING
	 */
	private void setRequestValues() throws Exception {

		if ( getDate().isFutureDate() ) throw new Exception (String.valueOf(ERR_DATE_TOO_FAR_IN_FUTURE)); 
		
		this.setStudentLeistungAusstellungsdatum( getDate().getDate()	 								);
		this.setStudentLeistungCreditPts		( getRequestCreditPoints() 								);
		this.setStudentLeistungBemerkung		( m_request.getParameter("txtStudentLeistungBemerkung")	);
		if(m_request.getParameter("txtSLKursBeschreibung")!=null) this.setSLKursBeschreibung				( m_request.getParameter("txtSLKursBeschreibung")	);
		this.setStudentLeistungDetails			( m_request.getParameter("txtStudentLeistungDetails")	);
		
		// new in version 6-19: lngModulID
		// modified on Dec 1, 2011, exception "zusatz"
		if(m_request.getParameter("txtModulID")!=null && !m_request.getParameter("txtModulID").equals("zusatz"))
			this.setModulID(Long.parseLong(m_request.getParameter("txtModulID")));
			
		
		// new in version 6-06: Custom2, in medicine-faculty, is a "Modulzeitraum"
		if(m_request.getParameter("txtStudentLeistungCustom2")!=null){
			this.setStudentLeistungCustom2(		m_request.getParameter("txtStudentLeistungCustom2")		);	
		}
		
		// new in version 6-11: Custom3, in medicine-faculty, is a "Halbjahr"
		if(m_request.getParameter("txtStudentLeistungCustom3")!=null){
			this.setStudentLeistungCustom3(		m_request.getParameter("txtStudentLeistungCustom3")		);	
		}
		
		try{
		  this.setNoteID							( getRequestNote().getNoteID()						);
		}catch(Exception eNoGradeFound){throw new Exception (String.valueOf(ERR_CONFIG_CREDIT_GRADE_MISSING));}
		
		this.setStudentLeistungPruefung			( this.isRequestPruefungsleistung() 					);	  

		// Durch Setzen eines Antragdatums als Commitment markieren, wenn der
		// Schein nicht aus einem Commitment generiert wurde.
		if (this.isRequestCommitment()){
			if(!(this.wasCommitment())) this.setStudentLeistungAntragdatum ( 
					new HtmlDate(m_request.getLocale()).getDate() );
		}else{
			// New May 30, 2006: only normalize, don't set to empty.
			// Otherwise, editing a credit erases the Antragdatum.
			// this.setStudentLeistungAntragdatum( "" );
			this.setStudentLeistungAntragdatum	( this.getStudentLeistungAntragdatum() );
		}
		// Kursdetails setzen falls per HTML-Form angegeben:
		if((m_request.getParameter("txtSLKursTitel")!=null) && (this.isRequestDetails())){
			setRequestSLKursValues();
		}	
		 // Wir brauchen hier den "Raum" (seit 12.3.2012), 
		 // weil die Mediziner das Feld für das 
		 // Ranking missbrauchen.
		if(m_request.getParameter("txtSLKursRaum")!=null) this.setSLKursRaum				( m_request.getParameter("txtSLKursRaum"));
		if(m_request.getParameter("txtSLKursRaum2")!=null) this.setSLKursRaum2				( m_request.getParameter("txtSLKursRaum2"));

		setRequestDozent();
		setStudentLeistungBestanden			( this.getRequestNote().getNoteBestanden()	);
		setStudentLeistungValidiert			( true );
		setStudentLeistungKlausuranmeldung	( false);		  
	}

	/**
	 * The teacher is set to <Code>requestparam cboDozentID</Code> or, if that fails, to 
	 * <Code>requestparams txtStudentLeistungAussteller, txtStudentLeistungAusstellerVorname and txtStudentLeistungAusstellerTitel</Code>. If 
	 * nothing works, ERR_CONFIG_CREDIT_TEACHER_MISSING is 
	 * thrown.	 <br />
	 * @version 6.02.01 (May 15, 2005): Where possible, first-names and academic titles 
	 * are stored separately.
	 * @throws Exception ERR_CONFIG_CREDIT_TEACHER_MISSING
	 */
	private void setRequestDozent() throws Exception {
		try{
		     Dozent dozent				= new Dozent ( this.getSeminarID(), getRequestDozentID() );
		     this.setDozentID						 ( dozent.getDozentID() 					 );
		     
		     setStudentLeistungAussteller	( dozent.getDozentNachname() );
		     setStudentLeistungAusstellerVorname( dozent.getDozentVorname() );
		     setStudentLeistungAusstellerTitel( dozent.getDozentTitel() );
		     
		     dozent.close();
		     dozent = null;
		  }catch(Exception eNoDozentID){
		     if (m_request.getParameter("txtStudentLeistungAussteller") == null)
				throw new Exception(String.valueOf(ERR_CONFIG_CREDIT_TEACHER_MISSING));
			if (m_request.getParameter("txtStudentLeistungAussteller").length() <= 2)
				throw new Exception(String.valueOf(ERR_CONFIG_CREDIT_TEACHER_MISSING));
			setDozentID(0);
			setStudentLeistungAussteller(m_request.getParameter("txtStudentLeistungAussteller"));
			setStudentLeistungAusstellerVorname(normalize(m_request.getParameter("txtStudentLeistungAusstellerVorname")));
			setStudentLeistungAusstellerTitel(normalize(m_request.getParameter("txtStudentLeistungAusstellerTitel")));
		  }
	}

	/**
	 * Set internal mode to either MODE_ADD_NEW_CREDIT or MODE_DISPLAY_CREDIT, and call method .init 
	 * in the latter case.<br />
	 * MODE_ADD_NEW_CREDIT is assumed if <br />
	 * <Code>(lngLeistungID==-1)&&<br />(lngStudentLeistungCount==-1)&&<br />(strLeistungBezeichnung.equals("ADD")
	 * </Code>
	 * @see com.shj.signUp.data.StudentXLeistung#init(long, String, long, long)
	 * @param lngLeistungID
	 * @param lngStudentLeistungCount
	 * @param strLeistungBezeichnung
	 * @throws Exception
	 */
	private void setMode(long lngLeistungID, long lngStudentLeistungCount, String strLeistungBezeichnung) throws Exception {
		if((lngLeistungID==-1)&&(lngStudentLeistungCount==-1)&&(strLeistungBezeichnung.equals("ADD"))){
		  // Show - ADD - mode: empty combos, no initial credit to display
		  try{
		  	Long.parseLong(m_request.getParameter("txtKursID"));
		  	
		  	if(m_request.getParameter("txtArchiv")!=null){
		  		this.m_bytMode	= MODE_ADD_PICKED_CREDIT;
		  		guessDetailsFromPickedCourse();
		  	}
		  	
		  }catch(Exception eNoCreditPicked){
		    this.m_bytMode 	= MODE_ADD_NEW_CREDIT;
		  }
		}else{
		  // NOT show-add mode: there's already a credit to display for update
		  this.m_bytMode = MODE_DISPLAY_CREDIT;
		  this.init(this.getSeminarID(), getMatrikelnummer(), lngLeistungID, lngStudentLeistungCount);
		}
	}

	/**
	 * Try to set DozentID, LeistungID and CreditPoints that fit the picked 
	 * course. If this is a course from the archives, this might take 
	 * guesswork (since teachers might no longer be there, credits 
	 * may have changed etc.).
	 * Also, guessing the CreditPoints is really guessing, since they 
	 * are a property of a KursXKurstyp object rather than of a Kurs-
	 * object alone.
	 */
	private void guessDetailsFromPickedCourse() {
		guessDozentId();
		guessLeistungId();
		guessCreditPoints();
	}

	/**
	 * Sets SLKursTitel, SLKursTitel_en, SLKursBeschreibung and SLKursBeschreibung_en from request.
	 */
	private void setRequestSLKursValues() {
		this.setSLKursTitel				( m_request.getParameter("txtSLKursTitel")		 	);
		 this.setSLKursTitel_en			( m_request.getParameter("txtSLKursTitel_en")	 	);
		 this.setSLKursBeschreibung		( m_request.getParameter("txtSLKursBeschreibung")	);
		 this.setSLKursBeschreibung_en	( m_request.getParameter("txtSLKursBeschreibung_en"));
	}
	
	/**
	 * @return true, if request contains "chkBestanden"
	 * @deprecated, do something like this instead:<br />
	 * <Code>
	 *  slBean = StudentLeistungDetailBean...<br />
	 *  boolean bestanden = <br />
	 *  &nbsp;&nbsp;&nbsp;new Note(slBean.getSeminarID, <br />
	 *  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;slBean.getNoteID())<br />
	 *  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.getNoteBestanden();
	 * </Code>
	 * @see com.shj.signUp.data.StudentXLeistung#getNoteID()
	 * @see com.shj.signUp.data.Note#getNoteBestanden()
	 */
	public boolean isBestanden(){
		return this.m_blnBestanden;
	}

	/** 
	 * Normalized
	 * @see com.shj.signUp.data.StudentXLeistung#getStudentLeistungAussteller()
	 **/
	public String getStudentLeistungAusstellerNormal(){
		return normalize(getStudentLeistungAussteller());
	}
	
	/** 
	 * Normalized.
	 * @see com.shj.signUp.data.StudentXLeistung#getStudentLeistungBemerkung()
	 **/
	public String getStudentLeistungBemerkungNormal(){
		return normalize(getStudentLeistungBemerkung());
	}
	
	/**
	 * Normalized
	 * @see com.shj.signUp.data.StudentXLeistung#getSLKursTitel()
	 **/
	public String getSLKursTitelNormal(){
		return normalize(getSLKursTitel());
	}
	
	/**
	 * Normalized
	 * @see com.shj.signUp.data.StudentXLeistung#getSLKursTitel_en()
	 **/
	public String getSLKursTitel_enNormal(){
		return normalize(getSLKursTitel_en());
	}
	
	/**
	 * Normalized
	 * @see com.shj.signUp.data.StudentXLeistung#getSLKursBeschreibung()
	 **/
	public String getSLKursBeschreibungNormal(){
		return normalize(getSLKursBeschreibung());
	}
	
	/**
	 * Normalized
	 * @see com.shj.signUp.data.StudentXLeistung#getSLKursBeschreibung_en()
	 **/
	public String getSLKursBeschreibung_enNormal(){
		return normalize(getSLKursBeschreibung_en());
	}
	
	/**
	 * Normalized
	 * @see com.shj.signUp.data.StudentXLeistung#getStudentLeistungDetails()
	 **/
	public String getStudentLeistungDetailsNormal(){
		return normalize(getStudentLeistungDetails());
	}
	
	/**
	 * @return Query String of this request that can deal with quotes.
	 * @see com.shj.signUp.data.shjCore#getShjQueryString(HttpServletRequest)
	 */
	public String getShjQueryString(){
		return getShjQueryString(m_request);
	}
	
	/**
	 * Constructor Adds or Update the given credit without 'asking again,' if 
	 * the request contains "cmdAdd" or "cmdUpdate".
	 * @param r must contain the following requestparams [not sure if complete]:<br />
	 * #requestparam s (or idxs), SeminarID,<br />
	 * #requestparam txtMatrikelnummer (student),<br />
	 * #requestparam intNoteID (optional): grade,<br />
	 * #requestparam txtStudentPID (long), Student identifier,<br />
	 * #requestparam txtLeistungID (long), (Optional) Credit identifier 1,<br />
	 * #requestparam txtStudentLeistungCount (long), Credit identifier 2,<br />
	 * #requestparam txtDozentID (long), issuer/teacher identifier,<br />
	 * #requestparam txtStudentPID (long), Student identifier,<br />,
	 * #requestparam strLeistungBezeichnung (String), credit name<br />,
	 * 
	 * #requestparam chkBestanden (exists or not), indicative of passing of credit (not used anymore)<br />,
	 * #requestparam chkPruefung (exists or not), Pruefungsleistung?<br />,
	 * #requestparam Mode (usertype), ="Detail" means that details are to be displayed.<br />,
	 * 
	 * #requestparam txtStudentLeistungCustom2 (Optional), can be a Modulzeitraum (med-faculty)<br />
	 * 
	 * #requestparam txtStudentLeistungCreditPts (optional, needed if you want to call 'add' or 'update'
	 * 
	 * #requestparam cmdAdd (optional, means that the request-info is to be converted to object and added to db.
	 * #requestparam cmdUpdate (optional, means that the request-info is to be converted to object and updated in db.
	 * @see #getCommandName()
	 * @deprecated as of version 5-32, please use empty constructor and .init instead.
	 * @throws Exception 
	 */
	public StudentLeistungDetailBean(HttpServletRequest r) throws Exception{
		init(r);
	}

	public StudentLeistungDetailBean(){
	}
	

}
