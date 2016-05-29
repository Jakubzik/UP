package de.shj.UP.logic;

import java.sql.ResultSet;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.w3c.dom.Node;
import de.shj.UP.HTML.HtmlDate;

/**
 * @author hj, shj-online
 * Helferklasse für die Kurswahlperiode: läuft die gerade?
 * Liegt sie in der Zukunft? In der Vergangenheit?
 * 
 * change date              change author           change description
 * ===================================================================
 * 2011-1-4					hj						CodeSchau, OK 
 */
public class Data extends de.shj.UP.data.Data {

	private static final long serialVersionUID = -106412320103494693L;
	
	private HtmlDate m_dtmVon = null;
	private HtmlDate m_dtmBis = null;
	private HtmlDate m_today  = null;
	
	private byte m_bytIsSignUpPeriod=0;
	
	private static final byte m_bytSIGNUP_UNINITIALIZED=0;
	private static final byte m_bytSIGNUP_CURRENT=1;
	private static final byte m_bytSIGNUP_IS_IN_THE_FUTURE=2;
	private static final byte m_bytSIGNUP_IS_IN_THE_PAST=3;

/////////////////////////////////////////////////
//	UTILITIES
/////////////////////////////////////////////////	
	
	/**
	 * @return true, if signup-period is currently 'on'.
	 * @throws Exception
	 */
	public boolean isSignUpPeriod() throws Exception{
		return (getPeriodStatus() == m_bytSIGNUP_CURRENT);
	}
	
	/**
	 * @return true, if signup period is in the future.
	 * @throws Exception
	 */
	public boolean isSignUpFuture() throws Exception{
		return (getPeriodStatus() == m_bytSIGNUP_IS_IN_THE_FUTURE);
	}

	/**
	 * @return true, if signup period is in the past.
	 * @throws Exception
	 */
	public boolean isSignUpPast() throws Exception{
		return (getPeriodStatus() == m_bytSIGNUP_IS_IN_THE_PAST);
	}
	
	/**
	 * <pre>
	 * True, if period for signing up is over and 
	 * the (manual) flag tblBdData.blnShowHttpResults is 'true'.
	 * </pre>
	 * @return true, if the students are allowed to see the results
	 * @throws Exception
	 */
	public boolean isSignUpShowResults() throws Exception{
		return (isSignUpPast() && getHttpShowResults());
	}
	
	/**
	 * <pre>
	 * True, if period for signing up is over and the 
	 * flag tblBdData.blnShowHttpResults is 'false'.
	 * 
	 * Enables the web-application to display a 
	 * 'please be patient' message.
	 * </pre>
	 * @return true, if application period is over, but students are not yet allowed to see results
	 * @throws Exception
	 */
	public boolean isSignUpPleaseWait() throws Exception{
		return (isSignUpPast() && (!getHttpShowResults()));
	}
	
	/**
	 * @return internal byte indicating whether the application period is in the 
	 * past, or ahead, or current.
	 * @throws Exception
	 */
	private byte getPeriodStatus() throws Exception{
		if(m_bytIsSignUpPeriod != m_bytSIGNUP_UNINITIALIZED) return m_bytIsSignUpPeriod;
		initHtmlDates();
		if(m_today==null) m_today=new HtmlDate(Locale.GERMANY);
		if((m_dtmVon.compareTo(m_today) <= 0) && (m_today.compareTo(m_dtmBis) <=0)) return m_bytSIGNUP_CURRENT;
		else if (m_dtmBis.compareTo(m_today) <= 0) return m_bytSIGNUP_IS_IN_THE_PAST;
		else return m_bytSIGNUP_IS_IN_THE_FUTURE;
	}
	
/////////////////////////////////////////////////
//	 PUBLIC 
	
	/**
	 * <pre>
	 * Store the start and stop dates for signup
	 * </pre>
	 * @param r containing params as produced by .getSitnUpStart[Stop]Date().
	 * @throws Exception ERR_SIGNUP_PERIOD_ILLOGICAL
	 */
	public void setSignUpPeriod(HttpServletRequest r) throws Exception{
		if(m_dtmVon==null){
			m_dtmVon = new HtmlDate(Locale.GERMANY);
			setDtmStartCboName();
		}
		if(m_dtmBis==null){
			m_dtmBis = new HtmlDate(Locale.GERMANY);
			setDtmStopCboName();
		}
			
		m_dtmVon.readRequest(r);
		m_dtmBis.readRequest(r);
		
		if(m_dtmBis.isBefore(m_dtmVon)) throw new Exception (String.valueOf(ERR_SIGNUP_PERIOD_ILLOGICAL));
		
		setSignUpStartDate(m_dtmVon);
		setSignUpStopDate(m_dtmBis);
		update();
	}
	
	public void setSignUpStartDate(HtmlDate d){
		m_dtmVon = d;
		setSignUpStart(d.getDate());
	}
	
	public void setSignUpStopDate(HtmlDate d){
		m_dtmBis = d;
		setSignUpStop(d.getDate());
	}
	
	/**
	 * @return Datum des Beginns der Kurswahl
	 * @throws Exception when signing up begins
	 */
	public HtmlDate getSignUpStartDate() throws Exception{
		if(m_dtmVon==null){
			try {
				m_dtmVon = new HtmlDate(g_ISO_DATE_FORMAT.format(getSignUpStart()), Locale.GERMANY);
			} catch (Exception e) {
				m_dtmVon = new HtmlDate("1970-1-1", Locale.GERMANY);
			}
			setDtmStartCboName();
		}
		return m_dtmVon;
	}

	/**
	 * @return
	 * @throws Exception when signing up begins
	 */
	public HtmlDate getSignUpStopDate() throws Exception{
		if(m_dtmBis==null){
			try {
				m_dtmBis = new HtmlDate(g_ISO_DATE_FORMAT.format(getSignUpStop()), Locale.GERMANY);
			} catch (Exception e) {
				m_dtmBis = new HtmlDate("1970-1-1", Locale.GERMANY);
			}
			setDtmStopCboName();
		}
		return m_dtmBis;
	}

/////////////////////////////////////////////////
//	 PRIVATE	
	
	private void initHtmlDates() throws Exception{
		if(m_dtmBis==null || m_dtmVon==null){
			getSignUpStartDate();
			getSignUpStopDate();
		}
	}
	
	/**
	 * Set the names
	 */
	private void setDtmStartCboName() {
		m_dtmVon.setCboDayName("dtmVonDay");
		m_dtmVon.setCboMonthName("dtmVonMonth");
		m_dtmVon.setCboYearName("dtmVonYear");
	}
	
	/**
	 * Set the names 
	 */
	private void setDtmStopCboName() {
		m_dtmBis.setCboDayName("dtmBisDay");
		m_dtmBis.setCboMonthName("dtmBisMonth");
		m_dtmBis.setCboYearName("dtmBisYear");
	}
	
/////////////////////////////////////////////////
// Constructors [all from superclass]
/////////////////////////////////////////////////	
	public Data() {
		super();
	}

	public Data(long lngSdSeminarID) throws Exception {
		super(lngSdSeminarID);
	}

	public Data(ResultSet rst) throws Exception {
		super(rst);
	}

	public Data(Node node) throws Exception {
		super(node);
	}
}
