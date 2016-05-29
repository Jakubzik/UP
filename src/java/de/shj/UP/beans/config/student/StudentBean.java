/*
 * Created on 23.01.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 * 
 * Mar-22-2006 hj corrected reset (m_Fach=null, m_ExamIterator=null) on new student.
 */
package de.shj.UP.beans.config.student;

import de.shj.UP.beans.student.ExamIterator;
import de.shj.UP.data.Fach;
import de.shj.UP.logic.StudentData;
import javax.servlet.http.HttpServletRequest;

import java.sql.ResultSet;
import java.util.Locale;
import de.shj.UP.HTML.HtmlSeminar;

/**
 * Bean to use in config frontend. Should be able to 
 * deal with all student-related data, extends StudentData.
 * You must call init(request) to initialize the Bean. 
 */
public class StudentBean extends StudentData {

	private static final long serialVersionUID = -8780145803970367015L;
	private boolean 		m_blnInitialized=false;
	private	HtmlSeminar 	m_seminar		=null;
	private Locale 			m_locale		=null;
	private Fach			m_Fach			=null;
	private long			m_lngFachsemester= g_ID_UNINITIALIZED;
	private ExamIterator	m_ExamIterator	=null;
	
	/**
	 * Seit JDBC2 Probleme mit der Persistenz.
	 * Aufruf von "resetExamIterator" stellt 
	 * Abkopplung vom DBPool sicher.
	 * 
	 * Erneuter Aufruf von ExamIterator() erzeugt 
	 * dann ein neues Objekt
	 */
	public void resetExamIterator(){
		if(m_ExamIterator==null) return;
		try{
			m_ExamIterator.disconnect();
			m_ExamIterator=null;
		}catch(Exception eProblem){}
	}
	/**
	 * ExamIterator of current student.
	 * @return
	 * @throws Exception
	 */
	public ExamIterator ExamIterator() throws Exception{
		if(m_ExamIterator==null) m_ExamIterator=new ExamIterator(getSeminarID(), getMatrikelnummer());
		return m_ExamIterator;
	}
	
	/**
	 * Return the subject
	 * @return
	 */
	public Fach Fach() throws Exception{
		if(m_Fach==null) initFach();
		return m_Fach;
	}
	
	/**
	 * Semester in this subject.
	 * @return
	 */
	public long getFachsemester() throws Exception{
		if(m_Fach==null) initFach();
		return m_lngFachsemester;
	}
	
	/**
	 * This method can initialize a seminar-object that has a different 
	 * SeminarID from this object. It does not change the StudentBean's SeminarID.
	 * @return Cbo with Exams. SeminarID is handed over independant from current object's seminarID.
	 * @throws Exception
	 */
	public String getSeminarPruefungCbo(long lngSeminarID) throws Exception{
		if(m_seminar==null) m_seminar=new HtmlSeminar(lngSeminarID, m_locale);
		if(m_seminar.getSeminarID()!=this.getSeminarID()) m_seminar=new HtmlSeminar(lngSeminarID, m_locale);
		return m_seminar.getPruefungCbo();
	}

	/**
	 * This method can initialize a seminar-object that has a different 
	 * SeminarID from this object. It does not change the StudentBean's SeminarID.
	 * @param lngPruefungID: Id of exam that is to be preselected.
	 * @return Cbo with Exams. SeminarID is handed over independant from current object's seminarID.
	 * @throws Exception
	 */
	public String getSeminarPruefungCbo(long lngSeminarID, long lngPruefungID) throws Exception{
		if(m_seminar==null) m_seminar=new HtmlSeminar(lngSeminarID, m_locale);
		if(m_seminar.getSeminarID()!=this.getSeminarID()) m_seminar=new HtmlSeminar(lngSeminarID, m_locale);
		return m_seminar.getPruefungCbo(lngPruefungID);
	}
	
	/**
	 * @return Cbo with Exams. Mind that this is seminar-specific.
	 * @throws Exception
	 */
	public String getPruefungCbo() throws Exception{
		if(m_seminar==null) m_seminar=new HtmlSeminar(getSeminarID(), m_locale);
		return m_seminar.getPruefungCbo();
	}
	
	/**
	 * @param lngPruefungID: Id of Pruefung that is to be preselected.
	 * @return Cbo with Exams. Mind that this is seminar-specific.
	 * @throws Exception
	 */
	public String getPruefungCbo(long lngPruefungID) throws Exception{
		if(m_seminar==null) m_seminar=new HtmlSeminar(getSeminarID(), m_locale);
		return m_seminar.getPruefungCbo(lngPruefungID);
	}	
	
	/**
	 * Initializes the underlying logic.Student object.<br>
	 * Requested parameters are:<br>
	 * #requestparam txtMatrikelnummer.<br>
	 * @param r
	 * @throws Exception.
	 */
	public void init(long lngSeminarID, HttpServletRequest r) throws Exception{
		if(!m_blnInitialized || (isDifferentStudent(r))){
			setSeminarID(lngSeminarID);
			this.m_locale=r.getLocale();
			this.m_Fach = null;
			this.m_ExamIterator = null;
			super.init(
					this.getSeminarID(), 
					r.getParameter("txtMatrikelnummer")
			);
			m_blnInitialized=true;
		}
	}
	
	/**
	 * Initializes the underlying logic.Student object.<br>
	 * Requested parameters are:<br>
	 * #requestparam txtMatrikelnummer.<br>
	 * @param r
	 * @throws Exception.
	 */
	public void init(long lngSeminarID, String sMatrikelnummer) throws Exception{
		if(!m_blnInitialized || (isDifferentStudent(sMatrikelnummer))){
			setSeminarID(lngSeminarID);
			this.m_locale=Locale.GERMANY;
			this.m_Fach = null;
			this.m_ExamIterator = null;
			super.init(
					this.getSeminarID(), 
					sMatrikelnummer
			);
			m_blnInitialized=true;
		}
	}	
	
	/**
	 * Called by init: should the Bean be re-initialized?
	 * Returns true, if current Matrikelnummer differs from the txtMatrikelnummer in 
	 * the request, or if there is no current Matrikelnummer,<br>
	 * Returns false if current and request-Matrikelnummer are the same, or if there 
	 * is no Matrikelnummer in the request.
	 * @see #init(long, HttpServletRequest)
	 * @param r the Request from init.
	 * @return true if the request seems to contain a new student, false otherwise.
	 */
	private boolean isDifferentStudent(HttpServletRequest r){
		return isDifferentStudent(r.getParameter("txtMatrikelnummer"));
	}
	
	/**
	 * Called by init: should the Bean be re-initialized?
	 * Returns true, if current Matrikelnummer differs from the txtMatrikelnummer in 
	 * the request, or if there is no current Matrikelnummer,<br>
	 * Returns false if current and request-Matrikelnummer are the same, or if there 
	 * is no Matrikelnummer in the request.
	 * @see #init(long, String)
	 * @param r the Request from init.
	 * @return true if the request seems to contain a new student, false otherwise.
	 */
	private boolean isDifferentStudent(String sMatrikelnummer){
		if(this.getMatrikelnummer()==null) return true;
		if(sMatrikelnummer==null) return false;
		return (!(this.getMatrikelnummer().equals(sMatrikelnummer)));
	}	
	
	/**
	 * @throws Exception
	 */
	private void initFach() throws Exception{
		ResultSet rstSeminarXFach = sqlQuery("select * from \"tblSdSeminarXFach\" where \"lngSeminarID\"=" + getSeminarID() + ";");
		boolean   blnGoOn=true;
		
		while(rstSeminarXFach.next() && blnGoOn){
			for(int ii=0;ii<7;ii++){
				if(rstSeminarXFach.getInt("intFachID")==getStudentFach(ii)){
					blnGoOn=false;
					m_lngFachsemester = getStudentFachsemester(ii);
					m_Fach = new Fach(getStudentFach(ii));
				}
			}
		}
		rstSeminarXFach.close();
	}

	private long getStudentFachsemester(int ii){
		switch(ii){
			case 1:
				return getStudentFachsemester1();
			case 2:
				return getStudentFachsemester2();
			case 3:
				return getStudentFachsemester3();
			case 4:
				return getStudentFachsemester4();
			case 5:
				return getStudentFachsemester5();
			case 6:
				return getStudentFachsemester6();
			default:
				return -1;
		}
	}
	
	private int getStudentFach(int ii){
		switch(ii){
			case 1:
				return getStudentFach1();
			case 2:
				return getStudentFach2();
			case 3:
				return getStudentFach3();
			case 4:
				return getStudentFach4();
			case 5:
				return getStudentFach5();
			case 6:
				return getStudentFach6();
			default:
				return -1;
		}
	}
	
	/**
	 * Bean constructor
	 */
	public StudentBean(){
	}
}
