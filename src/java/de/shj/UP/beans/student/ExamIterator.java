/*
 * Created on 27.02.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.shj.UP.beans.student;

import java.sql.ResultSet;
import de.shj.UP.data.StudentXPruefung;

/**
 * Iterator for Exams of a student.
 */
public class ExamIterator extends StudentXPruefung{

	private String m_strPruefungBezeichnung;
	private ResultSet m_rstStudentPruefung = null;
	private StudentXPruefung m_sxp	= null;
	

	/**
	 * @return Bezeichnung
	 */
	public String getPruefungBezeichnung(){
		return m_strPruefungBezeichnung;
	}
	
	/**
	 * Object of the current exam.
	 * @return
	 * @throws Exception
	 */
	public StudentXPruefung StudentPruefung() throws Exception{
		if(m_sxp==null) m_sxp = new StudentXPruefung(m_rstStudentPruefung);
		return m_sxp;
	}
	
	public boolean next() throws Exception{
		if(m_rstStudentPruefung==null) return false;
		boolean blnReturn = m_rstStudentPruefung.next();
		
		if(blnReturn){
			m_strPruefungBezeichnung = m_rstStudentPruefung.getString("strPruefungBezeichnung");
			m_sxp=null;
			this.initByRst(m_rstStudentPruefung);
		}else{
			m_rstStudentPruefung.close();
			disconnect();
		}
		
		return blnReturn;
	}
	
	/**
	 * Init iterator and use .next() as usual.
	 * @param lngSeminarID
	 * @param strMatrikelnummer
	 * @throws Exception
	 */
	public ExamIterator(long lngSeminarID, String strMatrikelnummer) throws Exception{
		this.setMatrikelnummer(strMatrikelnummer);
		this.setSdSeminarID(lngSeminarID);
		m_rstStudentPruefung = sqlQuery("select x.*, p.* from \"tblBdStudentXPruefung\" x, \"tblSdPruefung\" p " +
				"where x.\"lngSdSeminarID\"=" + this.getSdSeminarID() + " and " +
				"p.\"lngPruefungID\"=x.\"lngSdPruefungsID\" and " +
				"p.\"lngSdSeminarID\"=x.\"lngSdSeminarID\" and " +
				"x.\"strMatrikelnummer\"='" + this.getMatrikelnummer() + "' order by \"lngSdPruefungsID\" asc;");
	}	
}
