/*
 * Created on 19.02.2005
 */
package de.shj.UP.util;
import javax.servlet.http.HttpServletRequest;
/**
 * @author heiko
 */
public class BrowserCss {

	private String m_strUserAgent=null;
	private String m_strCss = "";
	private final String KONQUEROR="Konqueror";
	private final String MOZILLA  ="Mozilla";
	private final String OPERA	  ="Opera";
	private final String IE	  	  ="MSIE";
	
	/**
	 * @return '_kq' if user agent is Konqueror, <br />
	 * '' if user agent is Mozilla (incl. Firefox) and not Konqueror, or if it's Opera<br />
	 * '_ie' if user agent is Internet-Explorer
	 */
	public String getCssExtension(){
		return m_strCss;
	}
	
	/**
	 * @return User-Agent-String as stored in request.
	 */
	public String getUserAgent(){
		return m_strUserAgent;
	}
	
	private void interpret(){
		if(m_strUserAgent.indexOf(KONQUEROR)>=0){
			m_strCss = "_kq";
			return;
		}
		if(m_strUserAgent.toLowerCase().indexOf(IE.toLowerCase())>=0){
			m_strCss = "_ie";
			return;
		}
		if(m_strUserAgent.toLowerCase().indexOf(MOZILLA.toLowerCase())>=0)	return;
		if(m_strUserAgent.toLowerCase().indexOf(OPERA.toLowerCase())>=0)    return;
	}
	
	/**
	 * @param r is searched for its header 'User-Agent'.
	 */
	public BrowserCss(HttpServletRequest r){
		m_strUserAgent=r.getHeader("User-Agent");
		interpret();
	}
}
