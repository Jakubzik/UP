package de.shj.UP.logic;

import java.sql.ResultSet;
import java.sql.SQLException;

import de.shj.UP.data.KvvInhalt;

/**
 * The idea of the iterator is to  
 * be able to write code as in the following example:<br />
 * <Code>
 *KvvIterator kvv = new <br />
 *&nbsp;&nbsp;KvvIterator(lngSeminarID);<br /><br /> 
 *while (kvv.next()){<br /><br />
 *&nbsp;&nbsp;kvv.getKvvInhaltHeading()<br /><br />
 *&nbsp;&nbsp; ...<br />
 *&nbsp;&nbsp; (gather Kvv-Inhalt-info)<br /><br />
 *&nbsp;&nbsp;}//KvvInhaltIterator.next<br />
 * </Code>
 */
public class KvvInhaltIterator extends KvvInhalt {

	private static final long serialVersionUID = 7459402744942518343L;
	private ResultSet m_rstKvvInhalt=null;
	private String	  m_strErrMsg="";

	private KurstypIterator m_KurstypIterator;
	private boolean   m_blnPlanungssemester=false;
	
	/**
	 * Do you want the KurstypIterator to deliver information 
	 * about the 'Planungssemester' (catalogue of next term), 
	 * or the current semester (which is the default)?
	 * @param value
	 */
	public void setPlanungssemester(boolean value){
		m_blnPlanungssemester=value;
	}
	
	/**
	 * Call next, then access KurstypIterator.
	 * @return Kurstype-like object that can iterate through its mapped courses.
	 * @throws Exception
	 */
	public KurstypIterator KurstypIterator() throws Exception{
		if(m_KurstypIterator==null) m_KurstypIterator=new KurstypIterator(this.getSdSeminarID(), this.getKvvInhaltNr(), this.m_blnPlanungssemester); 
		return this.m_KurstypIterator;
	}
	
	/**
	 * @return is there a next Kvv-Inhalt-item?
	 */
	public boolean next(){
		try {
			boolean isNext = m_rstKvvInhalt.next();
			if(!isNext){
				m_rstKvvInhalt.close();
				disconnect();
			}else{
				m_KurstypIterator=null;
				this.initByRst(m_rstKvvInhalt);
			}
			return isNext;
		} catch (Exception e) {
			m_strErrMsg += e.toString();
			return false;
		}
	}
	
	public void finalize(){
		try {
			m_rstKvvInhalt.close();
		} catch (SQLException e) {}
		super.finalize();
	}
	/**
	 * Initialize with SeminarID, order by KurstypID.
	 * @param lngSeminarID
	 * @throws Exception
	 */
	public KvvInhaltIterator(long lngSeminarID) throws Exception{
		m_rstKvvInhalt = sqlQuery("select * from \"tblSdKvvInhalt\" where \"lngSdSeminarID\"=" + lngSeminarID + " order by \"intKvvInhaltHeadingSequence\" asc;");
	}

}
