/*
 * changed
 * 07-Aug-2005	hj	creation
 * 21-Aug-2006	hj	put an (optional) limit on group numbers?
 */
package de.shj.UP.logic;

import de.shj.UP.data.shjCore;
import java.util.GregorianCalendar;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Locale;
import de.shj.UP.HTML.HtmlDate;

/**
 * @author hj
 * Utility class to deal with Student-grouping: retrieve 
 * student-lists, add and remove students, move a student 
 * from one group to another one.
 */
public class StudGruppeData extends shjCore{

	private String m_strSemester		= "#";
	private long   m_lngStudienSemester = -999;
	private long   m_lngSeminarID		= -999;
	private long   m_lngStudGruppeID	= -999;
	
///////////////////////////
// HIGH LEVEL PUBLIC
///////////////////////////
	
	/**<pre>
	 * Moves the student (Matrikelnr.) to the emptiest group. 
	 * Hands back the Id of this group.
	 * 
	 * All other group mappings of this student (in this 
	 * Studiensemester and semester) are deleted.
	 * 
	 * SeminarID and Studiensemester must be set prior to 
	 * calling this method (or an error will occur); 
	 * if no semester was set, the 
	 * current semester is used.
	 * 
	 * Please note that this method will <i>NOT</i> map the student  
	 * to a group where flag blnStudGruppeOpen='f'.
	 * </pre>
	 * @param strMatrikelnummer of student
	 * @return Id of group that the student was mapped to.
	 * @throws Exception
	 */
	public long mapStudent(String strMatrikelnummer) throws Exception{
		long lngGroupID = -1;
		//checkConfig(); is called in 'moveStudent'
		lngGroupID = getEmptiestGroupID(m_strSemester, m_lngStudienSemester);
		moveStudent(strMatrikelnummer, lngGroupID);
		return lngGroupID;
	}
	
	/**
	 * <pre>
	 * Retrieve a list of students 
	 * in group <Code>lngStudGruppeID</Code> in 
	 * the current term (in the specified Seminar).
	 * 
	 * The resulting student-list is ordered by last name.
	 * </pre>
	 * @param lngSeminarID
	 * @param lngStudGruppeID
	 * @return
	 * @throws Exception
	 */
	public StudentList getStudentList() throws Exception {
		return new StudentList( sqlQuery(getSQL()));
	}

	/**
	 * <pre>
	 * Retrieve a list of students 
	 * in group <Code>lngStudGruppeID</Code> in 
	 * term <Code>strSemester</Code> (in the specified Seminar).
	 * 
	 * The resulting student-list is ordered by last name.
	 * </pre>
	 * @param lngSeminarID
	 * @param lngStudGruppeID
	 * @param strSemester
	 * @param lngStudiensemester
	 * @throws Exception
	 */
	public StudentList getStudentList(long lngSeminarID,
			long lngStudGruppeID, String strSemester, long lngStudiensemester)
			throws Exception {
		m_lngStudGruppeID=lngStudGruppeID;
		m_strSemester	 =strSemester;
		m_lngStudienSemester=lngStudiensemester;
		m_lngSeminarID=lngSeminarID;
		return new StudentList( sqlQuery(getSQL()) );
	}
	
///////////////////////////
// MAPPING STUDENTS
///////////////////////////
	/**<pre>
	 * moves the stududent to 
	 * group 'lngGruppeNeu'.
	 * 
	 *
	 * If no Semester was specified at construction time, 
	 * the current semester is used.
	 * 
	 * SeminarID ans StudienSemester must be set at 
	 * construction time, otherwise an error will 
	 * occur.
	 * 
	 * This method also sets 'dtmStudentStudGruppeDatum' to 
	 * today.
	 *   
	 * All mappings to groups other than the one specified 
	 * through 'lngGruppeNeu' are deleted. That's the main 
	 * difference to the method that explicitly asks for 
	 * a parameter that specifies the 'old group'
	 * 
	 * Please note that this method will map the student as 
	 * requested, even if the group's flag blnStudGruppeOpen='f'.
	 * </pre>
	 * @param strMatrikelnummer
	 * @param lngGruppeALT (old group)
	 * @param lngGruppeNeu (new group)
	 * @return
	 */
	public boolean moveStudent(String strMatrikelnummer, long lngGruppeNeu) throws Exception{
		boolean blnReturn = false;
		blnReturn = dropStudent(strMatrikelnummer);
		if(blnReturn){
			blnReturn = mapStudent(strMatrikelnummer,lngGruppeNeu);			
		}
		return blnReturn;
	}
	
	/**<pre>
	 * moves the stududent from group 'lngGruppeALT' to 
	 * group 'lngGruppeNeu'.
	 * 
	 *
	 * If no Semester was specified at construction time, 
	 * the current semester is used.
	 * 
	 * SeminarID ans StudienSemester must be set at 
	 * construction time, otherwise an error will 
	 * occur.
	 * 
	 * This method also sets 'dtmStudentStudGruppeDatum' to 
	 * today.
	 * 
	 * 
	 * Please note that this method will map the student as 
	 * requested, even if the group's flag blnStudGruppeOpen='f'.
	 * </pre>
	 * @param strMatrikelnummer
	 * @param lngGruppeALT (old group)
	 * @param lngGruppeNeu (new group)
	 * @return
	 */
	public boolean moveStudent(String strMatrikelnummer, long lngGruppeALT, long lngGruppeNeu) throws Exception{
		boolean blnReturn = false;
		blnReturn = mapStudent(strMatrikelnummer,lngGruppeNeu);
		if(blnReturn){
			blnReturn = dropStudent(strMatrikelnummer, lngGruppeALT);
		}
		return blnReturn;
	}

	/**<pre>
	 * Maps the student to this group
	 *
	 * If no Semester was specified at construction time, 
	 * the current semester is used.
	 * 
	 * SeminarID ans StudienSemester must be set at 
	 * construction time, otherwise an error will 
	 * occur.
	 * 
	 * This method also sets 'dtmStudentStudGruppeDatum' to 
	 * today (without changing the object's date).
	 * 
	 * Please note that this method will map the student as 
	 * requested, even if the group's flag blnStudGruppeOpen='f'.
	 * </pre>
	 * @param strMatrikelnummer
	 * @param lngGroup
	 * @return success or failure of transaction
	 * @throws Exception
	 */
	public boolean mapStudent(String strMatrikelnummer, long lngGroup) throws Exception{
		checkConfig();
		m_lngStudGruppeID=lngGroup;
		return sqlExe("insert into \"tblBdStudentXStudGruppe\" ( " +
				"\"strMatrikelnummer\", \"lngSeminarID\", \"lngStudGruppeID\", \"strStudentStudGruppeSemester\", \"lngStudentStudGruppeStudiensem\", \"dtmStudentStudGruppeDatum\" ) VALUES ( " + 
				"" + dbNormal(strMatrikelnummer) + ", " +
				"" + m_lngSeminarID + ", " +
				"" + m_lngStudGruppeID + ", " +
				"" + dbNormal(m_strSemester) + ", " +
				"" + m_lngStudienSemester + ", " +
				"'" + new SimpleDateFormat ("yyyy-MM-dd").format( new GregorianCalendar().getTime() ) + "' " +
				" );");
	}
	
	/**<pre>
	 * Drops the mapping of this student to all groups.
	 * Depending on the construction of this object, 
	 * the default semester (or the semester used in 
	 * construction) is used.
	 * 
	 * Whatever the construction, the SeminarID and Studiensemester 
	 * must be set beforehand, there are no defaults!
	 * 
	 * An exception is thrown if SeminarID or Studiensemester are 
	 * not properly set.
	 * </pre>
	 * @param strMatrikelnummer
	 * @param lngGroup
	 * @return 'true' for success
	 * @throws Exception
	 */
	public boolean dropStudent(String strMatrikelnummer) throws Exception{
		checkConfig();
		
		return sqlExe("delete from \"tblBdStudentXStudGruppe\" where (" +
				"\"lngSeminarID\"=" + m_lngSeminarID + " and " +
				"\"lngStudentStudGruppeStudiensem\"=" + m_lngStudienSemester + " and " +
				"\"strStudentStudGruppeSemester\"='" + m_strSemester + "' and " +
				"\"strMatrikelnummer\"='" + strMatrikelnummer + "');");
	}	
	
	/**<pre>
	 * Drops the mapping of this student to this group.
	 * Depending on the construction of this object, 
	 * the default semester (or the semester used in 
	 * construction) is used.
	 * 
	 * Whatever the construction, the SeminarID and Studiensemester 
	 * must be set beforehand, there are no defaults!
	 * 
	 * An exception is thrown if SeminarID or Studiensemester are 
	 * not properly set.
	 * </pre>
	 * @param strMatrikelnummer
	 * @param lngGroup
	 * @return 'true' for success
	 * @throws Exception
	 */
	public boolean dropStudent(String strMatrikelnummer, long lngGroup) throws Exception{
		checkConfig();
		m_lngStudGruppeID=lngGroup;
		
		return sqlExe("delete from \"tblBdStudentXStudGruppe\" where (" +
				"\"lngSeminarID\"=" + m_lngSeminarID + " and " +
				"\"lngStudGruppeID\"=" + m_lngStudGruppeID + " and " +
				"\"lngStudentStudGruppeStudiensem\"=" + m_lngStudienSemester + " and " +
				"\"strStudentStudGruppeSemester\"='" + m_strSemester + "' and " +
				"\"strMatrikelnummer\"='" + strMatrikelnummer + "');");
	}

////////////////////////////
// UTILITY
////////////////////////////
	/**
	 * <pre>
	 * Sets semester to current semester if it was not 
	 * set at construction time.
	 * 
	 * If SeminarID or StudienSemester were not set 
	 * at construction time, an Exception is thrown.
	 * </pre>
	 * @throws Exception
	 */
	private void checkConfig() throws Exception {
		if(m_strSemester.equals("#")) setCurrentSemesterAsDefault();
		if( (m_lngSeminarID==-999) || (m_lngStudienSemester==-999) ) throw new Exception("Studiensemester or Seminar missing.");
	}

	/**
	 * Sets the current semester (server-time) as 
	 * object variable.
	 * @throws Exception
	 */
	private void setCurrentSemesterAsDefault() throws Exception {
		m_strSemester = new HtmlDate(Locale.GERMAN).getSemesterName();
	}
	
//////////////////
//	 Constructors
//////////////////		

	/**
	 * Uses current semester and sets Studiensemester 
	 * @param lngSeminarID
	 * @param lngStudiensemester
	 * @throws Exception
	 */
	public StudGruppeData(long lngSeminarID, long lngGruppeID, long lngStudiensemester) throws Exception{
		m_lngStudGruppeID			 = lngGruppeID;
		m_lngSeminarID				 = lngSeminarID;
		m_lngStudienSemester 		 = lngStudiensemester;
		setCurrentSemesterAsDefault();
	}	
	
	/**
	 * Uses current semester and sets Studiensemester 
	 * @param lngSeminarID
	 * @param lngStudiensemester
	 * @throws Exception
	 */
	public StudGruppeData(long lngSeminarID, long lngStudiensemester) throws Exception{
		m_lngSeminarID				 = lngSeminarID;
		m_lngStudienSemester 		 = lngStudiensemester;
		setCurrentSemesterAsDefault();
	}	
	
	/**
	 * Sets semester ('ws2005/2006', 'ss2007') and regards the first Studiensemester as 
	 * the relevant one.
	 * @param strSemester Format ws2005/2006 or ss2007 etc.
	 * @throws Exception
	 */
	public StudGruppeData(long lngSeminarID, String strSemester) throws Exception{
		m_lngSeminarID				 = lngSeminarID;
		m_strSemester 		 = strSemester;
		m_lngStudienSemester = 1;
	}	
	
	/**<pre>
	 * Empty constructor: sets current semester 
	 * as the relevant one, and the first as 
	 * the relevant "Studiensemester".
	 * 
	 * DO NOT USE WITHOUT SETTING SEMINARID
	 * </pre>
	 */
	public StudGruppeData() throws Exception{
		setCurrentSemesterAsDefault();
		m_lngStudienSemester = 1;
	}

	/**<pre>
	 * Utility method for the equal distribution of students into groups.
	 * 
	 * This method hands back the Id of the group, in which the 
	 * least students (in this seminar, in this Studiensemester, in 
	 * this semester) are grouped.
	 * 
	 * The method requeries the database each time it is called,
	 * so it may be used for batch mapping.
	 * 
	 * Since version 6-17 (Sept 5, 2006) this disregards groups with flag blnStudGruppeOpen='f'
	 * </pre>
	 * @param strSemester In SignUp-format (i.e. 'ss2005', 'ws2005/2006'
	 * @param lngStudienSemester the student's studiensemester as defines in tblBdStudentXStudGruppe
	 * @return GroupID of the group with the least students in it.
	 * @throws Exception
	 */
	private long getEmptiestGroupID(String strSemester, long lngStudienSemester) throws Exception{
		ResultSet rst=sqlQuery(getGroupMemberIndicatorSQL(strSemester, lngStudienSemester));
		rst.next();
		long lngReturn = rst.getLong("lngStudGruppeID");
		return lngReturn;
	}
	
	/**
	 * Since version 6-17 (Sept 5, 2006) this disregards groups with flag blnStudGruppeOpen='f'
	 * @param strSemester
	 * @param lngStudienSemester
	 * @return
	 */
	private String getGroupMemberIndicatorSQL(String strSemester, long lngStudienSemester) {
		String strSQL = "select g.\"lngStudGruppeID\"," +
				"count(x.\"strMatrikelnummer\") as \"lngStudentCount\" " +
				"from \"tblSdStudGruppe\" g, \"tblBdStudentXStudGruppe\" x " +
				"where (" +
					"(g.\"lngSeminarID\"=" + m_lngSeminarID + ") and " +
					"(x.\"lngStudentStudGruppeStudiensem\"=" + lngStudienSemester + ") and " +
					"(x.\"strStudentStudGruppeSemester\"='" + strSemester + "') and " +
					"(g.\"lngSeminarID\" = x.\"lngSeminarID\") and " +
					"(g.\"blnStudGruppeOpen\"='t') and " + 
					"(g.\"lngStudGruppeID\" = x.\"lngStudGruppeID\")" +
				") " +
				"group by g.\"lngSeminarID\", g.\"lngStudGruppeID\"" +
			" union " +
				"select g.\"lngStudGruppeID\", " +
				"0 " +
				"from \"tblSdStudGruppe\" g " +
				"where (" +
				"g.\"blnStudGruppeOpen\"='t' and " +
				"not exists " +
					"(select * from \"tblBdStudentXStudGruppe\" x where" +
					"(g.\"lngSeminarID\"=" + m_lngSeminarID + ") and" +
					"(x.\"lngStudentStudGruppeStudiensem\"=" + lngStudienSemester + ") and " +
					"(x.\"strStudentStudGruppeSemester\"='" + strSemester + "') and " +
					"(g.\"lngSeminarID\" = x.\"lngSeminarID\") and " +
					"(g.\"lngStudGruppeID\" = x.\"lngStudGruppeID\")" +
					")" +
				") " +
				"order by \"lngStudentCount\" asc;";
		return strSQL;
	}

	/**
	 * A studemt.* X gruppe.* SQL representation 
	 * @param lngSeminarID
	 * @param lngStudGruppeID
	 * @param lngStudiensemester
	 * @param strSemester
	 * @return
	 */
	private String getSQL() {
		String strSQL = "SELECT s.*, g.* , x.* " +
				"FROM \"tblBdStudent\" s, " +
				"\"tblSdStudGruppe\" g, " +
				"\"tblBdStudentXStudGruppe\" x  " +
				"WHERE (" +
				"	(x.\"strStudentStudGruppeSemester\"='" + m_strSemester + "') AND" +
				"	(x.\"lngStudentStudGruppeStudiensem\"=" + m_lngStudienSemester + ") AND" +
				"   (x.\"lngSeminarID\"=" + m_lngSeminarID + ") AND" +
				"	(x.\"lngSeminarID\"=g.\"lngSeminarID\") AND" +
				"   (g.\"lngStudGruppeID\"=" + m_lngStudGruppeID + ") AND" +
				"	(x.\"lngStudGruppeID\"=g.\"lngStudGruppeID\") AND" +
				"	(s.\"strMatrikelnummer\"=x.\"strMatrikelnummer\")" +
				") order by s.\"strStudentNachname\" asc;";
		return strSQL;
	}

	/**
	 * @return Returns the m_lngStudienSemester.
	 */
	public long getStudienSemester() {
		return m_lngStudienSemester;
	}
	/**
	 * @param studienSemester The m_lngStudienSemester to set.
	 */
	public void setStudienSemester(long studienSemester) {
		m_lngStudienSemester = studienSemester;
	}
	/**
	 * @return Returns the m_strSemester.
	 */
	public String getSemester() {
		return m_strSemester;
	}
	/**
	 * @param semester The m_strSemester to set.
	 */
	public void setSemester(String semester) {
		m_strSemester = semester;
	}
	
	public long getSeminarID(){
		return m_lngSeminarID;
	}
	
	public void setSeminarID(long value){
		m_lngSeminarID=value;
	}
}
