
/**
 * Title:        SignUp 2.5i (Postgres-Specific Version)
 * Version:		 Singled for publix
 *
 * Description:  Manages Uploads in 'multipart/form-data' forms;
 *				 Developed and tested only with jakarta.tomcat 3.2
 *
 *				 YOU MAY COPY OR USE THIS BEAN OR PARTS OF IT FOR PRIVATE
 *				 USE AS LONG AS YOU KEEP MY COPYRIGHT NOTE.
 *
 *				 PLEASE CONTACT ME VIA EMAIL IF YOU
 *				 - HAVE QUESTIONS OR A BUG REPORT
 *				 - WISH TO OBTAIN A COPY FOR PROFESSIONAL USE.
 *
 * Copyright:    Copyright (c) 2002 h. jakubzik
 * Company:      SHJ - software development h. jakubzik
 *
 * @author Heiko Jakubzik
 * @http://www.heiko-jakubzik.de
 * @mailto:heiko.jakubzik@t-online.de
 */

  //-------------------------------------------------------------
  //-------------------------------------------------------------
  //				S T R U C T U R E
  //-------------------------------------------------------------
  //-------------------------------------------------------------
  /**
   * 1.		Declarations + Error-Description retrieval
   * 2.		get/set methods (properties)
   * 3.		main methods
   */
  //-------------------------------------------------------------
  //-------------------------------------------------------------

package de.shj.UP.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.servlet.http.*;

/** Bean to handle uploads -
 */
public class shjUpload extends shjCore implements Serializable
{
  //-------------------------------------------------------------
  //Declarations
  //-------------------------------------------------------------

  //-------------------------------------------------------------

   /**
   * class name used in throw-clauses.
   */
  private String strClassName="signUp.shjUpload";

  //-------------------------------------------------------------

  /**
   * info about error:
   * m_intErrNumber = <br>
   *  1 <=> size of file to upload exceeds limit.<br>
   *  2 <=> content-type of file non-conformant with filter.<br>
   *  3 <=> destination-path couldn't be initialized.<br>
   *  4 <=> error reading from servletInputStream.<br>
   *  5 <=> error writing to destination-path.<br>
   *  6 <=> can't close file-output-stream.<br>
   *  7 <=> can't close servletInputStream.<br>
   * 10 <=> can't find 'name=' in servletInputStream.<br>
   * 11 <=> can't find value to given name (eof).<br>
   * 12 <=> file already exists and m_blnDenyOverwrite is true.<br>
   * 13 <=> can't create directory where file is to be stored.<br>
   * 14 <=> delete was called, but file doesn't exist.<br>
   * 15 <=> delete was called, file exists but can't be deleted.<br>
   * 16 <=> can't get hold of request.InputStream.<br>
   * 17 <=> can't initialize.<br>
   * 18 <=> checkInterface throws.<br>
   * 19 <=> Can't get Content-Type of Upload.<br>
   * @see getErrDescription.
   */
  public int m_intErrNumber=0;

  //-------------------------------------------------------------
  //properties with get/set methods:

  /**
   * is it forbidden to overwrite existing file in upload?
   * (Default is: no, it is allowed.)
   */
  private boolean m_blnDenyOverwrite=false;

  /**
   * size of upload-request in kb. Set in method 'initialize'.
   */
  private long m_lngSize;

  /**
   * size limit of upload in kb; default is 40,000.
   */
  private long m_lngSizeLimit = 40000;

  /**
   * destination path for upload on server-machine (absolute).
   */
  private String m_strDestinationPath;

  /**
   * content-disposition-string of upload.
   */
  private String m_strContentDisposition;

  /**
   * delimiter of upload-stream.
   */
  private String m_strDelimiter;

  /**
   * content-type of upload as seen by browser.
   */
  private String m_strContentType;

  /**
   * internal debugging purposes.
   */
  public String m_strDebug;

  /**
   * File to upload must confirm to this filter.<br>
   * This 'white-list' is only checked if set.<br>
   * By default it is meaningless.
   */
  private Hashtable m_ContentTypeWhiteList=new Hashtable();

  /**
   * Flag indicating whether white-list has entries.
   */
  private boolean m_blnCTWhiteListSet=false;

   /**
    * Contains Content-Types that are forbidden.<br>
    * (E.g. application/octet or whatever).<br>
    * By default there is no Black-List.
    *
   */
  private Hashtable m_ContentTypeBlackList=new Hashtable();

  /**
   * Flag indicating whether black-list has entries.
   */
  private boolean m_blnCTBlackListSet=false;

  /**
   * In case there are additional param-name values
   * in the request, this hashtable holds them.
   */
  private Hashtable m_requestParameters=new Hashtable();

  /**
   * Here is the switch for behavior in case the upload-file
   * is non-conform with white-list or black-list or max size;
   * with a rebuff (default), the browser will feel 'reset by
   * peer' - so you can't control the error messages.
   * Otherwise, if you turn the rebuff off, the server is
   * forced to read the whole upload (even if it won't be saved).
   */
  private boolean m_blnRebuffOnContentError=true;

  /**
   * Original full-path of upload
   */
  private String m_strUploadOrigin;

  /**
   * Suffix (rtf, htm, html, whatever) of upload
   */
  private String m_strUploadSuffix;

  /**
   * Contains String of uploaded data. (SignUpXP-Specific!)
   */
  private String m_strUploadContent="";

  /**
   * @returns String of uploaded data. (SignUpXP-Specific!)
   */
  public String getContent(){
	  return m_strUploadContent;
  }

  /**
   * in case m_intErrNumber!=0, there's the description:
   * @return String ErrDescription: description of error.
   */
  public String getErrDescription(){
	  String strReturn="Kein Fehler aufgetreten.";
	  switch(m_intErrNumber){
	  case 0:
		  break;
	  case 1:
		  strReturn = "Die Datei ist zu gross. ";
		  strReturn+= "Es d�rfen h�chstens: " + this.getSizeLimit() + " bytes geladen werden; ";
		  strReturn+= "Ihre Datei ist " + m_lngSize + " bytes gross.";
		  break;
	  case 2:
		  strReturn = "Die Datei ist vom falschen Typ und darf nicht hochgeladen werden. ";
		  strReturn+= "(Ihre Datei hat den Typ '" + m_strContentType + "').";
		  break;
	  case 3:
		  strReturn = "Die Datei konnte nicht auf dem Server gespeichert werden. ";
		  //strReturn+= "This might well be a problem of the specific application. ";
		  //strReturn+= "Upon recurrence you might want to contact your developer. ";
		  //strReturn+= "The path I was trying is: '" + this.getDestinationPath() + "'.";
		  break;
	  case 4:
		  strReturn = "Fehler beim Lesen/�bermitteln der Daten. ";
		  strReturn+= "Vielleicht hat sich das Internet verschluckt. ";
		  //strReturn+= "If this error recurs, contact your developer.";
		  break;
	  case 5:
		  strReturn = "Die Datei konnte nicht auf dem Server gespeichert werden.";
		  //strReturn = "Error writing to destination path ('" + this.getDestinationPath() + "'). ";
		  //strReturn+= "Are the rights on server-machine thought-through?";
		  break;
	  case 6:
		  strReturn = "Can't close file-output-stream on file '" + this.getDestinationPath() + "'). ";
		  strReturn+= "Although it remains unclear why this is so, it's serious. ";
		  strReturn+= "You might want to 'sigterm' or 'kill' relevant processes on server.";
		  break;
	  case 7:
		  strReturn = "Really freaky: can't close input. ";
		  strReturn+= "Probably an internet-hickup, for security you might want ";
		  strReturn+= "to check the upload, though!";
		  break;
	  case 10:
		  strReturn = "Can't read the parameters from InputStream.";
		  strReturn+= "Maybe there's an encoding problem?";
		  break;
	  case 11:
		  strReturn = "Can't parse the parameters from InputStream.";
		  strReturn+= "There was a parameter with no value.";
	  case 12:
		  strReturn = "The file ('" + this.getDestinationPath() + "') ";
		  strReturn+= "already exists on this host. If you want to ";
		  strReturn+= "overwrite this file, you must use setDenyOverwrite()";
		  break;
	  case 13:
		  strReturn = "Can't make directory-structure for '" + this.getDestinationPath() + "'. ";
		  strReturn+= "You should check the rights for directory-creation.";
		  break;
	  case 14:
		  strReturn = "File '" + this.getDestinationPath() + "' doesn't exist on server.";
		  break;
	  case 15:
		  strReturn = "Can't delete the file you wanted to delete.";
		  strReturn+= "Do you have sufficient rights on server?";
		  break;
	  case 16:
		  strReturn = "Can't get hold of InputStream ... Upload impossible.";
		  strReturn+= "Try again.";
		  break;
	  case 17:
		  strReturn = "Can't initialize. What content-type did you want to upload?";
		  break;
	  case 18:
		  strReturn = "Interface-check throws. This shouldn't happen.";
		  strReturn+= "Basically it means that you can't upload THIS file.";
		  break;
	  case 19:
		  strReturn = "Unable to read content-type of your upload.";
		  break;
	  default:
		  strReturn = "Whoa, unknown error. Can't tell what happened. ";
		  strReturn+= "But somethin' went wrong ...";
		  break;
	  }
	  return strReturn;
  }

  //-------------------------------------------------------------
  //get/set methods (properties)
  //-------------------------------------------------------------

  //-------------------------------------------------------------

  /**
   * Full original path of upload.<br>
   * (Initialized in method 'initialize').
   * @return String containing path.
   */
  public String getUploadOrigin(){
	  return m_strUploadOrigin;
  }

  /**
   * Suffix (rtf, htm, txt, etc.) of upload.<br>
   * (Initialized in method 'initialize').
   */
  public String getUploadSuffix(){
	  return m_strUploadSuffix;
  }

  /**
   * Here is the switch for behavior in case the upload-file
   * is non-conform with white-list or black-list or max size;
   * with a rebuff (default), the browser will feel 'reset by
   * peer' - so you can't control the error messages.
   * Otherwise, if you turn the rebuff off, the server is
   * forced to read the whole upload (even if it won't be saved).
   * @param bln_IN: 'false' if you don't want a rebuff, 'true'<br>
   * otherwise ('true' is default).
   */
  public void setRebuffOnContentError(boolean bln_IN){
	  m_blnRebuffOnContentError=bln_IN;
  }

  /**
   * Deny to overwrite an existing file with upload?
   */
  public boolean getDenyOverwrite(){
	  return m_blnDenyOverwrite;
  }

  /**
   * Deny to overwrite an existing file with upload?
   */
  public void setDenyOverwrite(boolean blnDeny_IN){
	  m_blnDenyOverwrite=blnDeny_IN;
  }

  /**
   * get info on where the upload is to be stored
   * @return absolute path.
   */
  public String getDestinationPath(){
	  return m_strDestinationPath;
  }

  /**
   * set where the upload is to be stored
   * @param String strPath_IN ... absolute path.
   */
  public void setDestinationPath(String strPath_IN){
	  m_strDestinationPath = strPath_IN;
  }

  /**
   * Overloaded 'request.getParameter' to retrieve
   * parameters from query-string that browser sent.
   * @param strParamName_IN: name of parameter to retrieve
   * @return value of this parameter as String, or<br>
   * String 'null' if there is no such parameter.
   */
  public String getParameter(String strParamName_IN){
	  String strReturn="";
	  strReturn=(String)m_requestParameters.get(strParamName_IN);
	  return ((strReturn==null) ? "null" : strReturn);
  }

  /**
   * overrides 'setParameter' of HttpServletRequest.
   * Used internally, so why not make it public?
   * @param strParamName_IN: name of parameter to add,<br>
   * strParamValue_IN: value of this parameter.
   */
  public void setParameter(Object strParamName_IN, Object strParamValue_IN)  {
	  m_requestParameters.put(strParamName_IN, strParamValue_IN);
  }

  /**
   * overrides 'getParameterNames()' of HttpServletRequest.
   * @return Enumeration with parameter (or key-) names.
   */
  public Enumeration getParameterNames(){
	  return m_requestParameters.keys();
  }

  /**
   * Information about 'white-list', i.e. content-types<br>
   * the upload of which you explicitly allow.
   * @return keys of content-type white-list.
   */
  public Enumeration getContentTypeWhiteList(){
	  return m_ContentTypeWhiteList.keys();
  }

   /**
   * Information about 'black-list', i.e. content-types<br>
   * the upload of which you explicitly forbid.
   * @return keys of content-type white-list.
   */
  public Enumeration getContentTypeBlackList(){
	  return m_ContentTypeBlackList.keys();
  }

  /**
   * Returns description of item in ContentTypeWhiteList.
   * @param strParamName_IN: Key in White-List.
   * @return description of this key.
   */
  public String getContentTypeWhiteListDescription(String strParamName_IN){
	  return (String)m_ContentTypeWhiteList.get(strParamName_IN);
  }

   /**
   * Returns description of item in ContentTypeBlackList.
   * @param strParamName_IN: Key in Black-List.
   * @return description of this key.
   */
  public String getContentTypeBlackListDescription(String strParamName_IN){
	  return (String)m_ContentTypeBlackList.get(strParamName_IN);
  }

  /**
   * Adds an entry to the white-list.<br>
   * Example: setContentTypeWhiteList("rtf", "Rich Text Format");<br>
   * This explicitly allows the upload of 'rtf'. If the upload-file<br>
   * if not of content-type 'rtf', an error occurs.
   * @param strKey_IN: the key of content-type to set; content-type of upload is checked for OCCURRENCE of this substring!
   * @param strDescription_IN: description of this content-type
   */
  public void setContentTypeWhiteList(Object strKey_IN, Object strDescription_IN){
	  m_blnCTWhiteListSet=true;
	  m_ContentTypeWhiteList.put((String)strKey_IN, (String)strDescription_IN);
  }

  /**
   * Adds an entry to the black-list.<br>
   * Example: setContentTypeBlackList("rtf", "Rich Text Format");<br>
   * This explicitly forbids the upload of 'rtf'. If the upload-file<br>
   * is an 'rtf'-file, an error occurs.
   * @param strKey_IN: the key of content-type to set; content-type of upload is checked for OCCURRENCE of this substring!
   * @param strDescription_IN: description of this content-type
   */
  public void setContentTypeBlackList(Object strKey_IN, Object strDescription_IN){
	  m_blnCTBlackListSet=true;
	  m_ContentTypeBlackList.put((String)strKey_IN, (String)strDescription_IN);
  }


  /**
   * get info on current size limit;
   * note: default is 40,000.
   * @return current size limie (in Bytes).
   */
  public long getSizeLimit(){
	  return m_lngSizeLimit;
  }

  /**
   * sets size limit to specified value.
   * @param lngSizeLimit_IN: max. Size for upload in bytes.
   */
  public void setSizeLimit(long lngSizeLimit_IN){
	  m_lngSizeLimit = lngSizeLimit_IN;
  }

  /**
   * must override when HttpSessionBindingListener is implemented.
   */
  public void valueBound(HttpSessionBindingEvent e)
  {}

  /**
   * must override when HttpSessionBindingListener is implemented.
   */
  public void valueUnbound(HttpSessionBindingEvent e)
  {}

  /**
   * function to rename uploaded file from current-name
   * to the name handed in. If it is necessary to create
   * a new directory-structure, this is done.
   * @param String strPath_IN: new name of this file.
   * @return boolean indicating renaming-success.
   */
  public boolean rename(String strPath_IN)
  {
	  boolean blnReturn;
	  File file=new File(this.getDestinationPath());
	  File fileTo=new File(strPath_IN);

	  blnReturn=(!fileTo.exists());

	  if(blnReturn){
		  File dir=new File(strPath_IN.substring(0,strPath_IN.lastIndexOf("/")));
		  if(!dir.exists()){
			  blnReturn = dir.mkdirs();
		  }
	  }

	  if(blnReturn){file.renameTo(fileTo);}
	  return blnReturn;
  }

  //-------------------------------------------------------------
  //main methods
  //-------------------------------------------------------------

  //-------------------------------------------------------------

  /**
   * Is called by method 'upload' and reads the first four lines of InputStream.<br>
   * These lines contain:<br>
   * 1st the delimiter (stored in m_strDelimiter),<br>
   * 2nd the content-disposition (stored in m_strContenDisposition),<br>
   * 3rd the content-type (stored in m_strContentType), and<br>
   * an empty line.
   * Besides setting the above mentioned class variables, this method
   * also sets 'm_lngSize', viz. request.getContentLength().
   * @param HttpServletRequest r, the upload-request, needed for content-length,
   * @param ServletInputStream in, the stream that must already be opened prior to calling
   * this method.
   * @return true or false, according to whether an error occurred or not.
   */
  private boolean initialize(HttpServletRequest r, javax.servlet.ServletInputStream in){
	  byte b[] = new byte[1024];
	  boolean blnReturn=true;
	  m_lngSize = r.getContentLength();
	  String strContentType="";
	  int intPosStart;

	  try
	  {
		//read and interpret 1st three lines:
		m_strDelimiter = new String(b,0,in.readLine(b,0,1024)).trim();
		m_strContentDisposition  = new String(b,0,in.readLine(b,0,1024)).trim();
		m_strContentType  = new String(b,0,in.readLine(b,0,1024)).trim();

		//parse:
		intPosStart = m_strContentDisposition.indexOf("filename=");
		if(intPosStart>0)
		{
			m_strUploadOrigin=m_strContentDisposition.substring(intPosStart + 10);
			m_strUploadOrigin=m_strUploadOrigin.substring(0, m_strUploadOrigin.length()-1);

			m_strUploadSuffix=m_strUploadOrigin.substring(m_strUploadOrigin.lastIndexOf(".") + 1);
		}
		//empty line:
	    in.readLine(b,0,1024);

	  }catch(Exception e){
		  blnReturn=false;
	  }

	  return blnReturn;
  }//end of initialize

  /**
   * Main method of this Bean. Handed a 'multipart/form-data' - encrypted
   * html-form with upload-data, the method does this:<br>
   * 1st open 'upload-stream' for input,<br>
   * 2nd initialize class variables,<br>
   * 3rd check if upload conforms to settings (size etc.),<br>
   * 4th write upload to file,<br>
   * 5th initialize additional parameters of form, which can be accessed as in a HttpServletRequest.
   * @param HttpServletRequest of upload.
   * @return boolean 'true' if no error occurred, 'false' otherwise.
   */
  public boolean upload(HttpServletRequest r){
	byte b[] = new byte[1024];
	boolean blnGoOn=true;
	boolean blnExists=false;
	int intLength=-1;
	String str="";
	OutputStream f;
	File fileDestination;

	try{
		m_intErrNumber=1;

		m_intErrNumber=16;
		javax.servlet.ServletInputStream in = r.getInputStream();

		m_intErrNumber=17;
		if(!initialize(r, in)){
			in.close();
			throw new Exception("Can't initialize for some reason");
		}

		m_intErrNumber=0;
		try{
			checkInterface();
		}catch(Exception e2)
		{
			if(!m_blnRebuffOnContentError){
				//wrong content-type or file-size too big.
				//must read input anyway, otherwise the
				//connection is 'reset by peer' and the application
				//can't deal with the error properly.
				while(blnGoOn){
					intLength=in.readLine(b, 0, 1024);
					blnGoOn = (intLength>0);
				}
			}

			in.close();
			throw new Exception("Wrong content");
		}

		m_intErrNumber=3;

		str=this.getDestinationPath();
		fileDestination=new File(str.substring(0, str.lastIndexOf("/")));

		if(fileDestination.exists()&&m_blnDenyOverwrite){
			m_intErrNumber=12;
			throw new Exception(strClassName + ".upload(): File already exists.");
		}else{
			m_intErrNumber=13;
			if(!fileDestination.exists()){
				if(!fileDestination.mkdirs()){
					throw new Exception(strClassName + ".upload(): Direcory can't be created.");
				}
			}
		}

		m_intErrNumber=3;
		f=new FileOutputStream(this.getDestinationPath());
		intLength=-1;
		String strTmp="";
		while(blnGoOn){
			m_intErrNumber=4;
			intLength=in.readLine(b, 0, 1024);
			blnGoOn = (intLength>0);

			if(blnGoOn){

				if((new String(b,0,intLength)).startsWith(m_strDelimiter)){blnGoOn=false;}
				if(blnGoOn){
					strTmp=new String(b,0,intLength, "iso-8859-1");
					strTmp=((strTmp==null) ? "" : strTmp).trim();
					m_strUploadContent+=((strTmp.equals("null")) ? "" : strTmp);
					m_intErrNumber=5;
					f.write(b,0,intLength);
				}
			}
		}

		m_intErrNumber=6;
		f.close();

		blnExists = fileDestination.exists();

		//look for additional request-parameters
		loadParameters(in);

		m_intErrNumber=7;
		in.close();

		m_intErrNumber=0;

	}catch(Exception e)	{}

	return ((m_intErrNumber==0)&&(blnExists));

  }//end of upload

  /**
   * At the end of 'request.ServletInputStream' come the parameter<br>
   * name/value-pairs.<br>
   * From the looks they work like this:<br>
   * 1st line: delimiter,<br>
   * 2nd line: String containing parameter name,<br>
   * 3rd line: empty, and<br>
   * 4th line: String with parameter value.<br>
   * Function stores name/value-pairs in class-variable Hashtable.
   * @param ServletInputStream in: must be at the position where<br>
   * upload has been read and first delimiter after that has also<br>
   * been read.
   * @throws Exception: sets m_intErrNumber before throwing, so
   * @see m_intErrNumber.
   */
  private void loadParameters(javax.servlet.ServletInputStream in) throws Exception{
	String strTmp="";
	String strParamName="";
	String strParamValue="";
	byte b[] = new byte[1024];
	boolean blnGoOn=true;
	int intLength=-1;
	int intPosStart=-1;
	m_strDebug="";

	while(blnGoOn){
		//1st line: either empty or contains name of parameter:
		intLength=in.readLine(b, 0, 1024);
		blnGoOn = (intLength>0);

		if(blnGoOn){
			strTmp = new String(b,0,intLength).trim();
			intPosStart = strTmp.indexOf("name=");
			if(intPosStart<0){
				m_intErrNumber=10;
				m_strDebug+="Name not found;";
				throw new Exception("Can't find 'name='");
			}else{
				strParamName = strTmp.substring(intPosStart+6);
				//cut last character (a quotation mark).
				strParamName = strParamName.substring(0,strParamName.length()-1);
			}

			//next line contains nothing:
			intLength=in.readLine(b, 0, 1024);

			//next line contains value of parameter:
			intLength=in.readLine(b, 0, 1024);
			blnGoOn = (intLength>0);
			if(blnGoOn){
				strParamValue = new String(b,0,intLength).trim();
			}else{
				m_intErrNumber=11;
				throw new Exception("Parameter without a value");
			}

			//next line contains delimiter:
			intLength=in.readLine(b, 0, 1024);

			//put name-value-pair
			this.setParameter(strParamName, strParamValue);
		}
	}
  }//end of loadParameters

  /**
   * checks consistency of configuration:<br>
   * is filesize in limit (errNo 1)?<br>
   * is filter okay (errNo2)?<br>
   * @exception see m_intErrNumber.
   */
  private void checkInterface() throws Exception{
	  boolean blnWhiteListOK=true;
	  boolean blnBlackListOK=true;

	  if (m_lngSize > this.getSizeLimit()){
		  m_intErrNumber=1;
	  }

	  //check for content-types:
	  //is there a white-list?
	  if(m_blnCTWhiteListSet){
		  //check through white-list
		  blnWhiteListOK=false;
		  Enumeration en=this.getContentTypeWhiteList();
		  for(;en.hasMoreElements();){
			  if(m_strContentType.indexOf((String) en.nextElement())>0){
				blnWhiteListOK=true;
			  }
		  }
	  }

	  if(!blnWhiteListOK) m_intErrNumber= 2;

	  //check for content-types:
	  //is there a black-list?
	  if(m_blnCTBlackListSet){
		  //check through black-list
		  blnBlackListOK=true;
		  Enumeration en=this.getContentTypeBlackList();

		  for(;en.hasMoreElements();){
			  if(m_strContentType.indexOf((String) en.nextElement())>0){
				blnBlackListOK=false;
			  }
		  }
	  }

	  if(!blnBlackListOK) m_intErrNumber=2;

	  if(m_intErrNumber!=0){
		  throw new Exception(strClassName + ": ErrNo. " + m_intErrNumber);
	  }
  }

  /**
   * careful: deletes file on server. File must be specified before call.<br>
   * (Using setDestinationPath()).
   * @return 'true' if everything went as expected, 'false' otherwise.<br>
   * In case of false, m_intErrNumber is set, so that details can
   * be obtained through getErrDescription.
   */
  public boolean delete(){
	boolean blnReturn=false;
	File file=new File(this.getDestinationPath());

	if(!file.exists()){
		m_intErrNumber=14;
	}else{
		m_intErrNumber=15;
		blnReturn=file.delete();
	}
	return blnReturn;
  }
 /** empty constructor
 */
  public shjUpload()
  {
  }
}