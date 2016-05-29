package de.shj.UP.beans.student;

import java.sql.ResultSet;
import java.sql.SQLException;
import de.shj.UP.data.StudentBemerkung;
import de.shj.UP.logic.DozentData;

public class BemerkungIterator extends StudentBemerkung {

	private static final long serialVersionUID = -8025975780926130685L;
	private ResultSet m_rst = null;

	private String m_strDozentNachname;
	private String m_strDozentVorname;
	private String m_strDozentTitel;
	private long   m_lngDozentID;
	private DozentData m_Dozent;				
	private boolean m_bIsNewDozent=false;		

	/**
	 * <pre>
	 * In case the (more performant) shortcuts 
	 * to the author's name, title and id don't help.
	 * </pre>
	 * @return Current teacher
	 * @throws Exception
	 */
	public DozentData getDozent() throws Exception{
		if(m_bIsNewDozent || (m_Dozent==null)) m_Dozent = new DozentData(getSdSeminarID(), getDozentID());
		return m_Dozent;
	}
	/**
	 * @return last name of author of this remark
	 */
	public String getDozentNachname(){
		return m_strDozentNachname;
	}
	
	/**
	 * @return first name of author of this remark
	 */
	public String getDozentVorname(){
		return m_strDozentVorname;
	}
	
	/**
	 * @return academic title of author of this remark
	 */
	public String getDozentTitel(){
		return m_strDozentTitel;
	}
	
	/**
	 * @return last name of author of this remark
	 */
	public long getDozentID(){
		return m_lngDozentID;
	}
	
	/**
	 * @return true, if there is a next record.
	 * @throws Exception
	 */
	public boolean next() throws Exception{
		if(m_rst==null) return false;
		boolean blnReturn = m_rst.next();
		
		if(blnReturn){
			initLocalDozent();
			initByRst(m_rst);
		}else{
			// new March 6, 2010
			m_rst.close();
			
			disconnect();
		}
		
		return blnReturn;
	}

	/**
	 * @throws SQLException
	 */
	private void initLocalDozent() throws SQLException {
		
		// keep local object m_Dozent if possible:
		long lIdTmp = m_rst.getLong("lngDozentID");
		m_bIsNewDozent= (lIdTmp != getDozentID());
		
		m_strDozentNachname = m_rst.getString("strDozentNachname");
		m_strDozentVorname 	= m_rst.getString("strDozentVorname");
		m_strDozentTitel 	= m_rst.getString("strDozentTitel");
		m_lngDozentID	 	= lIdTmp;
	}
	
	/**
	 * @param lngSeminarID
	 * @param strMatrikelnummer
	 * @throws Exception
	 */
	public BemerkungIterator(long lngSeminarID, String strMatrikelnummer) throws Exception {
		m_rst = sqlQuery("select b.*, d.\"strDozentVorname\", " + 
				"d.\"strDozentNachname\", d.\"strDozentTitel\", " + 
				"d.\"lngDozentID\" " + 
				"from \"tblBdStudentBemerkung\" b, " +
				"\"tblSdDozent\" d where " +
				"d.\"lngSdSeminarID\"=" + lngSeminarID + " and " +
				"b.\"lngSdSeminarID\"=" + lngSeminarID + " and " +
				"d.\"lngDozentID\"=b.\"lngDozentID\" and " +
				"b.\"strMatrikelnummer\"='" + strMatrikelnummer + "' " + 
				"order by " +
				"\"dtmStudentBemerkungDatum\" asc;");
	}
}

