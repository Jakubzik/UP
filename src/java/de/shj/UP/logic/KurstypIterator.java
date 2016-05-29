package de.shj.UP.logic;

import de.shj.UP.data.Seminar;
import java.sql.*;
import java.util.Vector;

/**
 * The idea of KurstypIterator and KursIterator is to 
 * be able to write code as in the following example:<br />
 * <Code>
 *KurstypIterator kurstypen = new <br />
 *&nbsp;&nbsp;KurstypIterator(lngSeminarID);<br /><br /> 
 *while (kurstypen.nextKurstyp()){<br /><br />
 *&nbsp;&nbsp;kurstypen.getKurstyp().getKurstypBeschreibung()<br /><br />
 *&nbsp;&nbsp; ...<br />
 *&nbsp;&nbsp; (gather kurstypen-info)<br /><br />
 *&nbsp;&nbsp; ...<br /><br />
 *&nbsp;&nbsp;while (kurstypen.getKurstyp().nextKurs(){<br /><br />
 *&nbsp;&nbsp;&nbsp;&nbsp;kurstypen.getKurstyp().getKursXKurstyp().getKursCreditPts();<br />
 *&nbsp;&nbsp;&nbsp;&nbsp;kurstypen.getKurstyp().getKurs().getKursBeginn();<br /><br />
 *&nbsp;&nbsp;}//KursIterator.next<br />
 *}// Kurstypen.next<br />
 * </Code>
 * @author heiko
 * 
 * date			change user			change
 * 2011-1-7		hj					Codeschau
 *
 */
public class KurstypIterator extends Seminar {

	private static final long serialVersionUID = 3119237398168059605L;
	private Vector m_vKurstypen				= null;
	private int m_iKurstypenCount				= 0;
	private int m_iKurstypCurrent			=-1;
	
	private boolean   m_blnInitialized		= false;
	private String	  m_strErrMsg			= "";
	
	private long	  m_lngKvvInhaltNr		= g_ID_UNINITIALIZED;

	private KursIterator m_KursIterator;
	private boolean   m_blnPlanungssemester	= false;
	
	/**
	 * Call next, then access KursIterator.
	 * @return Kurstyp-like object that can iterate through its mapped courses.
	 * @throws Exception
	 */
	public KursIterator getKurstyp() throws Exception{
		if(m_KursIterator==null)
			m_KursIterator=new KursIterator(this.getSeminarID(), Long.parseLong((String) m_vKurstypen.get(m_iKurstypCurrent)), this.m_blnPlanungssemester);
		return m_KursIterator;
	}
	
	/**
	 * @return is there a next Kurstyp (a next KursIterator?).
	 * @throws Exception 
	 */
	public boolean nextKurstyp() throws Exception{
		if(!m_blnInitialized) initialize();
		m_KursIterator=null;
		return (m_iKurstypCurrent++<(m_iKurstypenCount-1));
	}
	
	private void initialize() throws Exception{
		ResultSet rst=null;
		if(isUninitialized(m_lngKvvInhaltNr)){
			rst = sqlQuery("select * from \"tblSdKurstyp\" where \"lngSdSeminarID\"=" + this.getSeminarID() + " order by \"strKurstypBezeichnung\";");
		}else{
			rst = sqlQuery("select k.* from \"tblSdKurstyp\" k, \"tblSdKvvInhaltXKurstyp\" i where k.\"lngSdSeminarID\"=" + this.getSeminarID() + " and i.\"lngSdSeminarID\"=" + this.getSeminarID() + " and i.\"intKvvInhaltNr\"=" + m_lngKvvInhaltNr + " and k.\"lngKurstypID\"=i.\"lngKurstypID\" order by \"lngKurstypID\";");
		}
		
		m_vKurstypen=new Vector();
		int ii=0;
		while(rst.next()){
			m_vKurstypen.add(ii++, rst.getString("lngKurstypID"));
		}
		rst.close();
		m_iKurstypenCount=ii;
		m_blnInitialized=true;
	}
	
	/**
	 * Initialize with SeminarID, order by KurstypID.
	 * @param lngSeminarID
	 * @throws Exception
	 */
	public KurstypIterator(long lngSeminarID, boolean blnPlanung) throws Exception{
		initSeminarAndPlanung(lngSeminarID, blnPlanung);
	}
	
	/**
	 * Empty, only for use in external webservices
	 */
	public KurstypIterator(){}
	
	/**
	 * Initialize with SeminarID, order by KurstypID, 
	 * restrict to one KvvInhaltNr.
	 * @param lngSeminarID
	 * @param lngKvvInhaltNr (what chapter of kvv to open?)
	 * @throws Exception
	 */
	public KurstypIterator(long lngSeminarID, long lngKvvInhaltNr, boolean blnPlanung) throws Exception{
		initSeminarAndPlanung(lngSeminarID, blnPlanung);
		m_lngKvvInhaltNr = lngKvvInhaltNr;
	}	
	
	/**
	 * (public only for external use in webservice)
	 * @param lngSeminarID
	 * @param blnPlanung
	 * @throws Exception
	 * @throws SQLException
	 */
	public void initSeminarAndPlanung(long lngSeminarID, boolean blnPlanung) throws Exception, SQLException {
		ResultSet rst = sqlQuery("select * from \"tblSdSeminar\" where \"lngSeminarID\"=" + lngSeminarID + ";");
		rst.next();
		this.initByRst(rst);
		this.m_blnPlanungssemester=blnPlanung;
		rst.close();
		rst=null;
	}
}
