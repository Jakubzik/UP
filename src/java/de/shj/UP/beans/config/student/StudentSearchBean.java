/*
 * Created on 06.01.2005
 * changes:
 * 07-Aug-2005	hj		implemented group-studentlist-method
 */
package de.shj.UP.beans.config.student;

import javax.servlet.http.*;
import de.shj.UP.logic.StudentList;

/**
 * This worker bean helps display students found by name or matriulation number.<br /> 
 * It is constructed with a HttpServletRequest that must contain 
 * param s (specifying the seminar) and may contain lastname and matriculationnumber 
 * fragments that are used as search criteria. <br />
 * Then, the method 'nextStudent()' iterates through the student-objects that are found.<br />
 * Example-usage:
 * <Code>
 * </Code>
 * 
 * @author hj
 */
public class StudentSearchBean extends StudentList {

	private static final long serialVersionUID = -8655224442026775516L;
	private long 		m_lngSeminarID 	= -1;
	private boolean		m_blnShowAllOnEmptyRequest;
	
	private String m_strRequestedName;
	private String m_strRequestedMatrikel;
	
	/**
	 * Utility: name and matrikelnummer as requested.
	 * @return "cmdSearch=1&txtStudentNachname=" + m_strRequestedName + "&txtMatrikelnummer=" + m_strRequestedMatrikel;
	 */
	public String getLink(){
		return "cmdSearch=1&txtStudentNachname=" + m_strRequestedName + "&txtMatrikelnummer=" + m_strRequestedMatrikel;
	}
	
	/**
	 * Show list of *all* students if there are no search-criteria?
	 * @param value
	 */
	public void setShowAllOnEmptySearch(boolean value){
		m_blnShowAllOnEmptyRequest=value;
	}
	
	/**
	 * Compatibility compromise when using cookies in webapp: in 
	 * this case, the SeminarID must be set explicitly (without 
	 * using cookies, it was handed over as parameter "s" in 
	 * the Request).
	 * @param value SeminarID
	 */
	public void setSeminarID(long value){
		this.m_lngSeminarID=value;
	}
	
	/**
	 * Empty constructor for Bean-usage: call init, then.
	 * @see #init(HttpServletRequest)
	 */
	public StudentSearchBean(){
		m_blnShowAllOnEmptyRequest = false;
	}
	
	/**
	 * Build the worker bean with search-criteria (last name and matrikelnummer with auto-wildcards), 
	 * sorted by Matrikelnummer. Student must be immatriculated for a subject that is mapped 
	 * to the current seminar.<br />
	 * Use the property 
	 * @param r: expects the folowing parameters: 
	 * #requestparam s (or idxs): (long), SeminarID;<br />
	 * #requestparam txtStudentNachname (String, optional),<br />
	 * #requestparam strMatrikelnummer (String, optional).
	 */
	public StudentSearchBean(HttpServletRequest r) throws Exception{
		init(r);
	}

	/**
	 * @param r
	 * #requestparam s (or idxs): (long), SeminarID; alternatively, the 
	 * seminarID can be set using setSeminarID.<br />
	 * #requestparam txtStudentNachname (String, optional),<br />
	 * #requestparam strMatrikelnummer (String, optional).	 * 
	 * #requestparam cmdSearch must not be null (or no records will be shown).
	 * 
	 * Or, new on Jan 2011, you have cmdSearch is null and 
	 * txtSuchen is not null.
	 * @throws Exception
	 */
	public void init(HttpServletRequest r) throws Exception {
		// Build SQL
		String  strSQL	 = "";
		
		// throws a NullPointerExeption
		if(m_lngSeminarID<0) m_lngSeminarID	= getSeminarID(r);
		
		// Anglistik hat nur ein Suchfeld:
		if(r.getParameter("txtStudentNachname")==null && r.getParameter("txtSuchen") != null){
			try{
				Long.parseLong(r.getParameter("txtSuchen"));
				m_strRequestedMatrikel = r.getParameter("txtSuchen");
				m_strRequestedName="";
			}catch(Exception eIsNoMatrikelnummer){
				m_strRequestedName=r.getParameter("txtSuchen");
				m_strRequestedMatrikel="";
			}
		}else{
			// Alle anderen haben Name und Matrikeln noch getrennt
			m_strRequestedName 		= normalize(r.getParameter("txtStudentNachname"));
			m_strRequestedMatrikel	= normalize(r.getParameter("txtMatrikelnummer") );
		}
			
		String	strCrit  = ( m_strRequestedName.equals("") ? 
									"" : 
									"\"strStudentNachname\" ~* '.*" + getDBCleanString(m_strRequestedName) + ".*' AND ") +
						   ( m_strRequestedMatrikel.equals("") ? 
						   			"" : 
						   			"\"strMatrikelnummer\" = '" + getDBCleanString(m_strRequestedMatrikel.trim()) + "' AND " );
		
		if (strCrit.length()>3) strCrit = strCrit.substring(0, strCrit.length()-4);
		
		if( (strCrit.length()<=10) ){
			strSQL = ""; // b" + m_blnShowAllOnEmptyRequest + r.getParameter("cmdSearch");
			
			if(m_blnShowAllOnEmptyRequest && (r.getParameter("cmdSearch")!=null)){
				strSQL = "SELECT " +
				  "x.\"lngSeminarID\",x.\"intFachID\",f.\"strFachBeschreibung\", s.* " +
				"FROM \"tblSdSeminarXFach\" x, \"tblBdStudent\" s, \"tblSdFach\" f " +
				"WHERE " +
				 "(" +
				  "(x.\"lngSeminarID\"=" + m_lngSeminarID + ") AND " +
                                    "x.\"intFachID\"=f.\"intFachID\" and " +
				    "(" +
				      "(s.\"intStudentFach1\"=x.\"intFachID\") OR " +
				      "(s.\"intStudentFach2\"=x.\"intFachID\") OR " +
				      "(s.\"intStudentFach3\"=x.\"intFachID\") OR " +
				      "(s.\"intStudentFach4\"=x.\"intFachID\") OR " +
				      "(s.\"intStudentFach5\"=x.\"intFachID\") OR " +
				      "(s.\"intStudentFach6\"=x.\"intFachID\")" +
				    ")" +
				 ") "; 		
                                
			}
			
		}else{
			strSQL = "SELECT " +
			  "x.\"lngSeminarID\",x.\"intFachID\",f.\"strFachBeschreibung\", s.* " +
			"FROM \"tblSdSeminarXFach\" x, \"tblBdStudent\" s, \"tblSdFach\" f " +
			"WHERE " +
			 "(" +
			  "(x.\"lngSeminarID\"=" + m_lngSeminarID + ") AND " + strCrit + " AND " +
                                "x.\"intFachID\"=f.\"intFachID\" and " +
			    "(" +
			      "(s.\"intStudentFach1\"=x.\"intFachID\") OR " +
			      "(s.\"intStudentFach2\"=x.\"intFachID\") OR " +
			      "(s.\"intStudentFach3\"=x.\"intFachID\") OR " +
			      "(s.\"intStudentFach4\"=x.\"intFachID\") OR " +
			      "(s.\"intStudentFach5\"=x.\"intFachID\") OR " +
			      "(s.\"intStudentFach6\"=x.\"intFachID\")" +
			    ")" +
			 ") ";
                        //System.out.println(strSQL);
		}
		
		// if(true==true) throw new Exception(strSQL);
		if(!strSQL.equals("")) this.init(strSQL, r);
	}

}
