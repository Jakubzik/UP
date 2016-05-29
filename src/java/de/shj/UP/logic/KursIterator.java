/*
 * Created on 17.01.2005
 *
 * date		change user			change
 * 2011-1-7	hj					Codeschau
 */
package de.shj.UP.logic;

import de.shj.UP.data.Kurstyp;
import de.shj.UP.logic.Kurs;
import de.shj.UP.util.ResultSetSHJ;
import de.shj.UP.data.KursXKurstyp;

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
 */
public class KursIterator extends Kurstyp {

	private static final long serialVersionUID = 134651062122942266L;
	private Vector m_vKursXKurstyp;
	private int m_iKursCount	= 0;
	private int m_iKursCurrent	= -1;
	private boolean m_blnInitialized;
	private boolean   m_blnPlanungssemester=false;

	private Kurs m_Kurs;
	private KursXKurstyp  m_KursXKurstyp;	
	
	/**
	 * Call next, then access getKursXKurstyp.
	 * @return Kurstype-like object that can iterate through its mapped courses.
	 * @throws Exception
	 */
	public KursXKurstyp getKursXKurstyp() throws Exception{
		if(m_KursXKurstyp==null) m_KursXKurstyp=new KursXKurstyp(this.getSdSeminarID(), this.getKurstypID(), Long.parseLong((String)m_vKursXKurstyp.get(m_iKursCurrent))); 
		return this.m_KursXKurstyp;
	}	
	
	/**
	 * Call next, then access getKurs.
	 * @return Kurstype-like object that can iterate through its mapped courses.
	 * @throws Exception
	 */
	public Kurs getKurs() throws Exception{
		try {
			if(m_Kurs==null) m_Kurs=new Kurs(this.getSdSeminarID(), Long.parseLong((String)m_vKursXKurstyp.get(m_iKursCurrent)));
		} catch (Exception e) {} 
		return this.m_Kurs;
	}
	
	/**
	 * @return is there a next Kurs?
	 */
	public boolean nextKurs(){
		m_Kurs=null;
		m_KursXKurstyp=null;
		return (m_iKursCurrent++<(m_iKursCount-1));
	}
	
	private void initialize() throws Exception{
		ResultSetSHJ rst = sqlQuerySHJ("select x.*, k.* from " +
				"\"tblBdKursXKurstyp\" x, \"tblBdKurs\" k " +
				"where (x.\"lngSeminarID\"=" + this.getSdSeminarID() + " and " +
						(this.getKurstypID()!=-4711 ? "x.\"lngKurstypID\"=" + this.getKurstypID() + " and " : "") +
						"k.\"lngSdSeminarID\"=" + this.getSdSeminarID() + " and " +
						"k.\"lngKursID\"=x.\"lngKursID\" and " +
						"k.\"blnKursPlanungssemester\"=" + getDBBoolRepresentation(m_blnPlanungssemester) + ") " +
						"order by \"intKursSequence\";");
		try {
			int ii=0;
			m_vKursXKurstyp=new Vector();
			while(rst.next()){
				m_vKursXKurstyp.add(ii++, rst.getString("lngKursID"));
			}
			m_iKursCount=ii;
			//rst.close();
			this.close();
			disconnect();
		} catch (Exception e) {
			//rst.close();
			disconnect();
		}
		m_blnInitialized=true;
	}	
	
	
	/**
	 * New Mar 13, 2007 for Kvv
	 * @param lSeminarID
	 * @param lKurstypID
	 * @param bPlanung
	 * @throws Exception
	 */
	public KursIterator(long lSeminarID, long lKurstypID, boolean bPlanung) throws Exception{
		super(lSeminarID, lKurstypID);
		m_blnPlanungssemester=bPlanung;
		initialize();
	}
	
	public KursIterator(ResultSet rst, boolean blnPlanung) throws Exception{
		super(rst);
		m_blnPlanungssemester=blnPlanung;
		initialize();
	}
}
