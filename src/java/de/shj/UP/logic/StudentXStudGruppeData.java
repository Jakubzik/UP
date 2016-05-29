package de.shj.UP.logic;

import java.sql.ResultSet;

import org.w3c.dom.Node;

import de.shj.UP.HTML.HtmlDate;
import de.shj.UP.data.StudentXStudGruppe;

/**
 * Simpler creation through pre-supposing constructors.
 * Changed April 8, 2006: closed ResultSets, added serialUID
 */
public class StudentXStudGruppeData extends StudentXStudGruppe {

	private static final long serialVersionUID = 1138909356625862712L;
	private String m_strCurrSemester = g_STRING_UNINITIALIZED;
	
	/**
	 * <pre>
	 * Method initializes a student's group-mapping,
	 * * in the current semester,
	 * * in the StudienSemester that is HIS .StudentSemester().
	 * </pre>
	 * @param lngSeminarID
	 * @param strMatrikelnummer
	 * @param lngStudiensemester (of the record in StudentXStudGruppe, @link com.shj.signUp.data.StudentXStudGruppe#getStudentStudGruppeStudiensem())
	 * @throws Exception
	 */
	public StudentXStudGruppeData(long lngSeminarID, String strMatrikelnummer, long lngStudiensemester) throws Exception{
		setSeminarID(lngSeminarID);
		ResultSet rst = sqlQuery("select * from \"tblBdStudentXStudGruppe\" where " +
				"\"lngSeminarID\"=" + getSeminarID() + " and " +
				"\"lngStudentStudGruppeStudiensem\"=" + lngStudiensemester + " and " +
				"\"strMatrikelnummer\"='" + strMatrikelnummer + "' and " +
				"\"strStudentStudGruppeSemester\"='" + getCurrentSemester() + "'");
		rst.next();
		initByRst(rst);
		rst.close();
		rst=null;
	}	
	
	/**
	 * <pre>
	 * Method initializes a student's group-mapping,
	 * * in the current semester,
	 * * in the StudienSemester that is HIS .StudentSemester().
	 * 
	 * <b>CAUTION !!! USE THIS ONLY FOR MED-HD</b>
	 * (Because the Studiensemester is supposed to be relevant, which 
	 * it might not be with other faculties!)
	 * Please use @link #StudentXStudGruppeData(long, String, long) instead.
	 * </pre>
	 * @param lngSeminarID
	 * @param strMatrikelnummer
	 * @throws Exception
	 */
	public StudentXStudGruppeData(long lngSeminarID, String strMatrikelnummer) throws Exception{
		setSeminarID(lngSeminarID);
		ResultSet rst = sqlQuery("select * from \"tblBdStudentXStudGruppe\" where " +
				"\"lngSeminarID\"=" + getSeminarID() + " and " +
				"\"lngStudentStudGruppeStudiensem\"=" + lookUp("intStudentSemester", "tblBdStudent", "\"strMatrikelnummer\"='" + strMatrikelnummer + "'") + " and " +
				"\"strMatrikelnummer\"='" + strMatrikelnummer + "' and " +
				"\"strStudentStudGruppeSemester\"='" + getCurrentSemester() + "'");
		rst.next();
		initByRst(rst);
		rst.close();
		rst=null;
	}
	
	/**
	 * @link com.shj.signUp.HTML.HtmlDate#getSemesterName()
	 * @return current semester name
	 */
	private String getCurrentSemester(){
		try {
			if(isUninitialized(m_strCurrSemester)) m_strCurrSemester=new HtmlDate(java.util.Locale.GERMANY).getSemesterName();
		} catch (Exception e) {}
		return m_strCurrSemester;
	}	
	
	/**
	 * Empty
	 */
	public StudentXStudGruppeData() {
		super();
	}

	/**
	 * @param lngSeminarID
	 * @param lngStudGruppeID
	 * @param strMatrikelnummer
	 * @param strStudentStudGruppeSemester
	 * @param lngStudentStudGruppeStudiensem
	 * @throws Exception
	 */
	public StudentXStudGruppeData(long lngSeminarID, long lngStudGruppeID,
			String strMatrikelnummer, String strStudentStudGruppeSemester,
			long lngStudentStudGruppeStudiensem) throws Exception {
		super(lngSeminarID, lngStudGruppeID, strMatrikelnummer,
				strStudentStudGruppeSemester, lngStudentStudGruppeStudiensem);
	}

	/**
	 * @param rst
	 * @throws Exception
	 */
	public StudentXStudGruppeData(ResultSet rst) throws Exception {
		super(rst);
	}

	/**
	 * @param node
	 * @throws Exception
	 */
	public StudentXStudGruppeData(Node node) throws Exception {
		super(node);
	}

}
