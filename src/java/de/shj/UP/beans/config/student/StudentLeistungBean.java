/*
 * Created on 06.01.2005
 * April 19, 2006	hj	m_blnKursScheinanmeldungErlaubt
 */
package de.shj.UP.beans.config.student;

import de.shj.UP.data.Leistung;
import de.shj.UP.data.StudentXLeistung;
import de.shj.UP.data.Note;
import de.shj.UP.logic.StudentData;
import java.sql.*;
import javax.servlet.http.*;

/**
 * Use this worker-bean (from config) to iterate through a student's credits and display their properties.
 * @author hj
 */
public class StudentLeistungBean extends StudentXLeistung {
	

	private static final long serialVersionUID = -2045209208120453512L;
	private ResultSet m_rstLeistungen = null;
	private Note	  m_Note		  = null;
	private Leistung  m_Leistung	  = null;
	private String    m_strLeistungBez= "";
	private boolean	  m_blnInitialized= false;
	private boolean   m_blnCommitmentMode=false;
	private boolean   m_blnAllowChanges = false;
	private boolean   m_blnKursScheinanmeldungErlaubt = false;
	private String	  m_strKursScheinanmeldungVon = "";
	private String	  m_strKursScheinanmeldungBis = "";
	
	/**
	 * @return Note (Grade) of this credit.
	 */
	public Note Note(){
		return m_Note;
	}
	
	/**
	 * @return Details on the credit
	 */
	public Leistung Leistung(){
		return m_Leistung;
	}
	
	/**
	 * @return 'true' if tblBdKurs.blnKursScheinanmeldungErlaubt==true.
	 * @version 6-14-07
	 */
	public boolean isKursScheinanmeldungErlaubt(){
		return m_blnKursScheinanmeldungErlaubt;
	}
	
	/**
	 * @return ISO-Date (as stored in DB, unconverted); makes sense only in commitment mode (otherwise: empty String).
	 */
	public String getKursScheinanmeldungVon(){
		return m_strKursScheinanmeldungVon;
	}

	/**
	 * @return ISO-Date (as stored in DB, unconverted); makes sense only in commitment mode (otherwise: empty String).
	 */
	public String getKursScheinanmeldungBis(){
		return m_strKursScheinanmeldungBis;
	}
	
	
	public String getLeistungBezeichnung(){
		return m_strLeistungBez;
	}
	
	/**
	 * In commitment-mode, returns 'true' if the current commitment 
	 * is still changeable.
	 * @return
	 */
	public boolean getAllowChanges(){
		return m_blnAllowChanges;
	}
	
	/**
	 * @return true if there is another credit, false otherwise
	 * @throws Exception
	 */
	public boolean nextLeistung() throws Exception{
		if(m_rstLeistungen==null)  return false;
		boolean bReturn=false;
		if(m_rstLeistungen.next()){
			this.initByRst(m_rstLeistungen);
			m_Note 		= new Note(this.getSdSeminarID(), this.getNoteID() );
			m_Leistung 	= new Leistung(m_rstLeistungen); 
			m_strLeistungBez = m_Leistung.getLeistungBezeichnung();
			if(isCommitmentMode()){
				m_blnAllowChanges			= m_rstLeistungen.getBoolean("blnAllowChanges");			// day inside commitment period?
				m_strKursScheinanmeldungVon = m_rstLeistungen.getString("dtmKursScheinanmeldungVon");
				m_strKursScheinanmeldungBis = m_rstLeistungen.getString("dtmKursScheinanmeldungBis");
				m_blnKursScheinanmeldungErlaubt=m_rstLeistungen.getBoolean("blnKursScheinanmeldungErlaubt"); // Course allows commitments?
			}
			bReturn= true;
		}else{
			m_rstLeistungen.close();
			disconnect();
			m_Note = null;
			m_strLeistungBez = "";
		}
		return bReturn;
	}
	
	/**
	 * @param r Contains these params:<br />
	 * #requestparam 's' or 'idxs' specifying seminar,<br />
	 * #requestparam 'txtStudentPID' and <br />
	 * #requestparam 'strMatrikelnummer' to identify student,
	 * #requestparam 'cmdCommitment' (optional, not null or null).<br />
	 * #requestparam 'order' (optional, order by name or date; ='Bezeichnung' means order by name).<br />
	 * @throws Exception (parameters missing, or database-problems.
	 * @see com.shj.signUp.logic.StudentData#StudentExamApplications()
	 * @see com.shj.signUp.logic.StudentData#StudentCredits(boolean, byte)
	 * @deprecated please use empty constructor and init(lngSeminarID, reqR) instead.
	 */
	public StudentLeistungBean(HttpServletRequest r) throws Exception{
		init(r);
	}

	/**
	 * Empty constructor for Bean usage; must call init(lngSeminarID, reqR) then to initialize
	 */
	public StudentLeistungBean(){
	}
	
	/**
	 * @param r
	 * #requestparam 'txtStudentPID' and <br />
	 * #requestparam 'strMatrikelnummer' to identify student,
	 * #requestparam 'cmdCommitment' (optional, not null or null).<br />
	 * #requestparam 'order' (optional, order by name or date; ='Bezeichnung' means order by name).<br />
	 * @throws Exception
	 */
	public void init(StudentData s, HttpServletRequest r, byte bOrder) throws Exception {
		if (! (m_blnInitialized && (this.isNewStudent(s,r)))){
			
			if(r.getParameter("cmdCommitment")==null){
				m_rstLeistungen	= s.StudentCredits(false,bOrder);
				this.setCommitmentMode(false);
			}else{
				m_rstLeistungen	= s.StudentExamApplications();
				this.setCommitmentMode(true);
			}
		}
	}
	
	/**
	 * @param r
	 * #requestparam 'txtStudentPID' and <br />
	 * #requestparam 'strMatrikelnummer' to identify student,
	 * #requestparam 'cmdCommitment' (optional, not null or null).<br />
	 * #requestparam 'order' (optional, order by name or date; ='Bezeichnung' means order by name).<br />
	 * @throws Exception
	 */
	public void init(StudentData s, HttpServletRequest r) throws Exception {
		if (! (m_blnInitialized && (this.isNewStudent(s,r)))){

			byte bytOrderBy	= (byte) ( (normalize(r.getParameter("order")).equals("Bezeichnung")) ? 1 : 0 );
			
			if(r.getParameter("cmdCommitment")==null){
				m_rstLeistungen	= s.StudentCredits(false,bytOrderBy);
				this.setCommitmentMode(false);
			}else{
				m_rstLeistungen	= s.StudentExamApplications();
				this.setCommitmentMode(true);
			}
			
			// New on March 1 2010
			this.disconnect();
		}
	}	
	
	/**
	 * true, if current Matrikelnummer is null or equal to the one in r and the commitment mode 
	 * is also equal to the one in r.
	 * @param r (txtMatrikelnummer, cmdCommitment)
	 * @return true, if it's a request that requires re-initialization of bean.
	 */
	private boolean isNewStudent(StudentData s, HttpServletRequest r){
		if(this.getMatrikelnummer()==null) return true;
		if(s.getMatrikelnummer()==null) return false;
		
		if(!(s.getMatrikelnummer().equals(this.getMatrikelnummer()))) return false;
		
		if(this.isCommitmentMode())  {
			return (r.getParameter("cmdCommitment")==null);
		}
		return (r.getParameter("cmdCommitment")!=null);
	}
		
	/**
	 * @param r
	 * @throws Exception
	 */
	private void init(HttpServletRequest r) throws Exception {
		StudentData student	= new StudentData();
		long lngSeminarID	= getSeminarID(r);
		
		byte bytOrderBy	= (byte) ( (normalize(r.getParameter("order")).equals("Bezeichnung")) ? 1 : 0 );
		student.init 				(lngSeminarID, 
									 Long.parseLong(r.getParameter("txtStudentPID")), 
									 r.getParameter("txtMatrikelnummer"));
		
		if(r.getParameter("cmdCommitment")==null){
			m_rstLeistungen	= student.StudentCredits(false,bytOrderBy);
		}else{
			m_rstLeistungen	= student.StudentExamApplications();
		}
	}

	/**
	 * @param m_blnCommitmentMode The m_blnCommitmentMode to set.
	 */
	public void setCommitmentMode(boolean m_blnCommitmentMode) {
		this.m_blnCommitmentMode = m_blnCommitmentMode;
	}

	/**
	 * @return Returns the m_blnCommitmentMode.
	 */
	public boolean isCommitmentMode() {
		return m_blnCommitmentMode;
	}
}
