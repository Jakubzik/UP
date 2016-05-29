package de.shj.UP.logic;

import de.shj.UP.data.Aktuelles;
import de.shj.UP.util.ResultSetSHJ;

/**
 * @author hj, shj-online
 * 
 * Anzeige aktueller Meldungen auf der Homepage,
 * Wrapper um data.Aktuelles.
 * 
 * Ideen: automatisches Posten auf Facebook/Twitter?
 *
 * change date              change author           change description
 * ===================================================================
 * 2011-1-4					hj				Umgestellt von ResultSet auf 
 * 											ResultSetSHJ (Anlass war Routine-
 * 											Codeschau)	            
 */
public class AktuellesIterator extends Aktuelles {

	private static final long serialVersionUID = 610271604261327104L;
	private ResultSetSHJ m_rst = null;
	private long m_lAktuellesCount=0;
	private long m_ll=0;
	
	/**
	 * Iterate through news-items
	 * @return true, if there is a next record.
	 * @throws Exception
	 */
	public boolean next() throws Exception{
		if(m_rst==null) return false;
		boolean blnReturn = m_rst.next();
		
		if(blnReturn){
			initByRst(m_rst);
			m_ll++;
		}else{
			//m_rst.close();
			disconnect();
		}
		
		return blnReturn;
	}
	
	public boolean last(){
		return(m_ll>=m_lAktuellesCount);
	}
	/**
	 * Selects all <b>currently active</b> news items for iteration.<br />
	 * (Where "currently active means: "now" is between display-start and -stop.
	 * @param lSeminarID
	 * @throws Exception 
	 */
	public AktuellesIterator(long lSeminarID) throws Exception{
		
		m_lAktuellesCount=dbCount("lngAktuellesSequence", "tblBdAktuelles", "\"lngSdSeminarID\"=" + lSeminarID + " and " + 
				"\"dtmAktuellesStart\"<=CURRENT_DATE and \"dtmAktuellesStop\">=CURRENT_DATE");
			
		m_rst=sqlQuerySHJ("select * from \"tblBdAktuelles\" " +
				"where \"lngSdSeminarID\"=" + lSeminarID + " and " + 
				"\"dtmAktuellesStart\"<=CURRENT_DATE and \"dtmAktuellesStop\">=CURRENT_DATE " +
				"order by \"lngAktuellesSequence\" desc;");
	}
}
