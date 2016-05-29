/*
 * Created on 19.02.2005
 */
package de.shj.UP.util;
import java.util.ResourceBundle;
import java.util.Locale;

/**
 * @author hj
 * Structure:
 * (1) BaseURL
 * (2) SessionState
 * (3) Css
 * (4) Language & Locale
 */
public class SessionData {
	
	private String 			m_strCss;
        private String                  m_sSessionType="";
	private ResourceBundle 	m_rsbLanguage;
	private Locale 			m_locale;
	private byte			m_bytSessionState = 0;
	private String			m_strBaseLink 	  = "#";
	
	private String			m_strClipboard	  = "";
	private String 			m_sErrorDetails	  = "";
	
	private static final String m_strUninitialized 			= "#";
	private static final byte m_bytSESSION_STATE_INDEFINITE = 0;
	private static final byte m_bytSESSION_STATE_SESSION_OFF = 1;
	private static final byte m_bytSESSION_STATE_SESSION_ON = 2;

////////////////////////////
//	 Clipboard
////////////////////////////
	
	/**
	 * reset clipboard string 
	 */
	public void cleanClipboard(){
		m_strClipboard = "";
	}
	
	/**
	 * @param sWhat
	 */
	public void setClipboard(String sWhat){
		m_strClipboard=sWhat;
	}
	
	/**
	 * @return String in "Clipboard"
	 */
	public String getClipboard(){
		return m_strClipboard;
	}
	
	public void setErrorDetails(String sWhat){
		 m_sErrorDetails = sWhat;
	}
	
	public String getErrorDetails(){
		return m_sErrorDetails;
	}
        
        // For example: Student-Login
        public void setSessionType(String s){
            m_sSessionType=s;
        }
        
        public String getSessionType(){return m_sSessionType;}
	
////////////////////////////
// BaseURL (Cross Site)
////////////////////////////
	
	/**
	 * @return 'true' if the BaseLink has not been initialized
	 */
	public boolean isBaseLinkUninitialized(){
		return (m_strBaseLink.equals(m_strUninitialized));
	}
	
	/**
	 * @see #setBaseURL(String)
	 * @return Base URL
	 */
	public String getBaseURL(){
		return m_strBaseLink;
	}
	
	/**<pre>
	 * Throws an Exception if the URL seems out of this session; sets 
	 * the BaseURL (without throwing an exception), if it is yet unset.
	 * 
	 * Example code [1] of the jsp-page that constructs this bean:
	 * <Code>
	 * SessionData d = new SessionData();
	 * d.setBaseURL(request.getRequestURL().toString());
	 * </Code>
	 * Example code [2] of the Access-checking functionality of each further page:
	 * <Code>
	 * d.isProperDomain(request.getRequestURL().toString());
	 * </Code>
	 * 
	 * If the URL in [2] is not the same (up to .../SignUp/Seminar/), 
	 * an Exception is thrown. Please note that this method does <b>not</b> 
	 * differentiate Config/Staff/Student access.
	 * 
	 * The example is more complicated than need be. Since this method 
	 * also sets the BaseURL if it was not set before, Code example [2] suffices.
	 * </pre>
	 * @see #setBaseURL(String)
	 * @param strURL current URL
	 * @throws Exception if strURL does not belong to the same "realm" as the BaseURL.
	 * @return true if the domain was set to strURL because it had not been set before.
	 */
	public boolean isProperDomain(String strURL) throws Exception{
		if(isBaseLinkUninitialized()){
			setBaseURL(strURL);
			return true;
		}
		
		if(!strURL.startsWith(getBaseURL())) 
			throw new Exception("Session-break, seemingly cross-site. " + 
					"Cannot use \n'" + getBaseURL() + "'\n" + 
					"to gain access to \n'" + strURL + "'");
		
		return false;
	}
	
	/**
	 * <pre>
	 * This method sets the base-link (regardles if it has been set before),
	 * after calculating it from the entire url.
	 * 
	 * A proper call would be 
	 * <Code>.setBaseURL(request.getRequestURL().toString);</Code>
	 * 
	 * The BaseLink is the URL up to the short name of the institute, 
	 * e.g. https://192.168.10.2:8443/SignUp/cl/
	 * (including the trailing slash).
	 * 
	 * The "base url" is a utility to avoid cross-site scripting, or 
	 * using sessions from one institut (cl) to gain access to 
	 * another (inf).
	 * </pre>
	 * @see #isProperDomain(String)
	 * @param strURL Full URL, e.g. https://192.168.10.2:8443/SignUp/cl/Config/index.jsp
	 */
	public void setBaseURL(String strURL){
		int intFirstAfter = strURL.indexOf('/',strURL.indexOf("SignUp"));
		int intSecondAfter= strURL.indexOf('/', intFirstAfter+1);
		m_strBaseLink = strURL.substring(0, intSecondAfter+1 );
	}
	
//////////////////////////////
// Session States
////////////////////////////
	
	/**<pre>
	 * This method is empty, just for doc.:
	 * Session-state control: this is a helper for 
	 * error pages. Error pages can ask the application
	 * whether to explicitly act inside a session, 
	 * or explicitly outside a session.
	 * (And show the left navigation accordingly, for example.)
	 * The default value is 'indefinite'</pre>
	 **/
	public void explainSessionState(){}
	
	/**
	 * Is the session turned off by force?
	 * @see #explainSessionState()
	 * @return true, if the session is to be regarded as 'no session at all'.
	 */
	public boolean isSessionStateOff(){
		return (m_bytSessionState==m_bytSESSION_STATE_SESSION_OFF);
	}
	
	/**
	 * Set the state of this session as 'on'
	 * @see #explainSessionState()
	 */
	public void setSessionStateOn(){
		m_bytSessionState = m_bytSESSION_STATE_SESSION_ON;
	}

	/**
	 * Set the state of this session as 'off'
	 * @see #explainSessionState()
	 */
	public void setSessionStateOff(){
		m_bytSessionState = m_bytSESSION_STATE_SESSION_OFF;
	}
	
	/**
	 * Set the state of this session to 'indefinite' (default)
	 * @see #explainSessionState()
	 */
	public void resetSessionState(){
		m_bytSessionState = m_bytSESSION_STATE_INDEFINITE;
	}
	
////////////////////////////
// Css
////////////////////////////
	
	/**
	 * @param css The full relative path to the css.
	 */
	public void setCss(String css) {
		m_strCss = css;
	}
	
	/**
	 * @return Returns the full relative path to the css.
	 */
	public String getCss() {
		return m_strCss;
	}
	
////////////////////////////
// Language & Locale
////////////////////////////
	
	/**
	 * @param language The language to set.
	 */
	public void setLanguage(ResourceBundle language) {
		this.m_rsbLanguage = language;
	}
	
	/**
	 * @return Returns the language.
	 */
	public ResourceBundle getLanguage() {
		return m_rsbLanguage;
	}
	
	/**
	 * @param m_locale The m_locale to set.
	 */
	public void setLocale(Locale m_locale) {
		this.m_locale = m_locale;
	}
	
	/**
	 * @return Returns the m_locale.
	 */
	
	public Locale getLocale() {
		return m_locale;
	}
	
	public SessionData(){}
	
}
