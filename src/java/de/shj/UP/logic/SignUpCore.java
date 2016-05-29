/*
 * Software-Developement H. Jakubzik, Licence Version 1.0
 *
 *
 * Copyright (c) 2003 Heiko Jakubzik.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by
 *        H. Jakubzik (http:////www.heiko-jakubzik.de//)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "shj" and "SignUp" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact heiko.jakubzik@t-online.de.
 *
 * 5. Products derived from this software may not be called "e~vv,"
 *    nor may "e~vv" appear in their name, without prior written
 *    permission of H. Jakubzik.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL H. JAKUBZIK SOFTWARE-DEVELOPEMENT
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * code-structure
 *
 * 1.	declarations										line 90
 *
 * 2.	public properties									line 110
 *
 * 3. 	private properties									line 183
 *
 * 4.  	public methods (utils)								line 225
 *
 * 5.	constructor											line 350
 *
 *
 *
 *
 * @version 0-00-01 (auto-coded)
 * change date              change author           change description
 * initial creation           h. jakubzik             autoclass
 *
 */

package de.shj.UP.logic;


import de.shj.UP.data.shjCore;
import java.io.Serializable;
import java.util.Enumeration;

/**
 * This class contains <br />(1) methods and properties that concern all of SignUp (such as 'isValidEmail,' but
 * also 'isValidMatrikel'). Partly, these methods stem from this classes original (pre version 7) function as
 * the logical unit to deal with student-data).<br />(2) properties of a seminar such as 'GvpAdmin' and the like.
 *
 **/
public class SignUpCore extends shjCore implements Serializable{


	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 1.   D E C L A R A T I O N S
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------

	// ------------------------------
	// private
	// ------------------------------

	/**
	 * 
	 */
	private static final long serialVersionUID = -121225856536855747L;
	private de.shj.UP.data.StatusView m_objStatusView;
	private boolean m_blnObjStatusViewInitialized=false;

	private long m_lngSeminarID;
	private String m_strCoursePreselectID="";			//?
	private String m_strTeacherPreselectID="";			//?

	// ------------------------------
	// public
	// ------------------------------
	public String m_strErrMsg="";			//?
	public String m_strPID="";			//?
	public String m_strExam="";			//?


	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 2.   P U B L I C  P R O P E R T I E S
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------

	/**
	 * Are we currently before SignUp-date (as specified in 'tblBdData')?
	 * @return boolean value indicating whether 'now' (server-time) is before start of SignUp as specified in 'tblBdData.'
	 **/
	public boolean IsBeforeSignUp(){
	  return this.StatusView().getBeforeSignUp();
  	}

	/**
	 * Are we currently during SignUp-date (as specified in 'tblBdData')?
	 * @return boolean value indicating whether 'now' (server-time) is during SignUp-phase as specified in 'tblBdData.'
	 **/
  	public boolean IsDuringSignUp(){
	  return this.StatusView().getDuringSignUp();
  	}

  	/**
  	 * Are results of course-distribution accessible to students?
  	 * they are accessible, if and only if it is NOT during SignUp AND NOT before SignUp AND the server is 'active' according to 'tblBdData' AND the flag HttpShowResults is set.
  	 * @return boolean value indicating whether results of course-distributions are accessible to students or not.
  	 **/
  	public boolean ShowResults(){
	  return ((!this.IsDuringSignUp())&&(!this.IsBeforeSignUp())&&(this.StatusView().getHttpserverAktiv())&&(this.StatusView().getHttpShowResults()));
  	}


	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 3.   P R I V A T E   P R O P E R T I E S (Rolle, StatusView)
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------

	/**
	 * Bean StatusView containing information on SignUp-relevant stati (permission of changes, options for students etc.).<br />
	 * If the object cannot be initialized, the error message is added to 'm_strErrMsg.'
	 * @return the current or a newly instantiated 'StatusView'-object. In case the object
	 * is instantiated, the current SeminarID of this object here is used.
	 **/
	private de.shj.UP.data.StatusView StatusView(){

	  if(this.m_objStatusView==null){
		  try{
			this.m_objStatusView=new de.shj.UP.data.StatusView(this.m_lngSeminarID);
		  }catch(Exception eStatusView){m_strErrMsg+="failed initializing StatusView:" + eStatusView.toString() + "\n";}
	  }

	  return this.m_objStatusView;
	}


	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 4.   P U B L I C   ( U T I L I T Y )  M E T H O D S
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------

 	/**
 	 * Registration: is this Matrikel new?
 	 * @return boolean flag indicating whether matrikel handed in is new to db.
 	 * @param strMatrikelnummer_IN String representing matrikel to be checked.
 	 * @throws Exception No Connection to Database
 	 **/
  	public boolean isNewMatrikel(String strMatrikelnummer_IN) throws Exception{
	  return (lookUp("lngStudentPID","tblBdStudent","\"strMatrikelnummer\"='" + getDBCleanString(strMatrikelnummer_IN) + "'").equals("#NO_RESULT"));
  	}

  	/**
  	 * Returns boolean value indicating whether the session
  	 * is valid or not. That is it returns 'true' if
  	 * 'tblBdStudent' contains a record with the SessionID
  	 * that was handed in, 'false' otherwise.<br>
  	 * <b>Note:</b> PID is NOT set ANYMORE in this method as of XP.
  	 * @param strSessionID: SessionID.
  	 * @return boolean value thumbs up or down.
  	 * @exception throws exception if connection to db
  	 * @deprecated session management is now the server's business.
  	 * erroneous - no check for 'blnAlive' or 'blnAllowChanges'.
  	 **/
	public boolean isValidSession(String strSessionID_IN) throws Exception{
		return(!(lookUp("lngStudentPID","tblBdStudent","lngStudentRandom=" + getDBCleanString(strSessionID_IN)).startsWith("#")));
	}//end of isValidSession()


   /**
    * General check for 'witty' requests.
   	* request ranks as 'okay', if all of its parameter-values are
   	* <> "--" AND
   	* begin with a letter or a digit (no wildcards or other fancy stuff) AND
   	* are shorter than 255 signs.
   	* @deprecated
   	* @return boolean representing the opinion of request (true==okay, false==forgetit)
   	* @param r Request handed in.
   	**/
   public boolean isClean(javax.servlet.http.HttpServletRequest r){

	boolean blnReturn=true;
	String strName="";
	String strValue="";
	Enumeration pn=r.getParameterNames();

	for(;pn.hasMoreElements();){

		strName = (String)pn.nextElement();
		strValue=r.getParameter(strName);

		if((strValue.length()>0)&&(!strValue.equals("--"))){
			Character ch=new Character(strValue.charAt(0));
			if(!(ch.isLetterOrDigit(strValue.charAt(0)))){
				blnReturn=false;
			}

			if(strValue.length()>255){
				blnReturn=false;
			}
		}
	  }
	  return blnReturn;
  	}//end of isClean

  	/**
  	 * Check for probable validity of matrikel -<br>
  	 * here, specifically the following two properties are checked:<br>
  	 * firstly, the matrikel must be numeric and<br>
  	 * secondly, the matrikel must have seven digits.
  	 * @param strMatrikel_IN String containing matrikel
  	 * @return boolean value inidicating whether matrikel conforms to<br>
  	 * the two above-mentioned criteria
  	 * @version >1.7i
  	 **/
  	public boolean isMatrikel(String strMatrikel_IN){
	  boolean blnReturn=false;
	  int intNo=0;

	  try{
		intNo=Integer.parseInt(strMatrikel_IN);
		blnReturn=true;
	  }catch(Exception e){
		blnReturn=false;
	  }

	  if(blnReturn){
		//Heidelberg-specific: 7 digits
		if(intNo<1000000||intNo>9999999){
			blnReturn=false;
	   }
	 }
	 return blnReturn;
	}

   /**
    * Superficial check if e-dress contains obvious errors:<br>
    * specific check is for existence of '@'-character and <br>
    * existence of dot after '@'-character
    * @param strEmail_IN String containing e-dress to be checked
    * @return boolean value indicating whether edress conforms<br>
    * to the two above-mentioned criteria or not.
    * @version >1.7i
    **/
  	public boolean seemsValidEmail(String strEmail_IN){

	  boolean blnReturn=false;
	  int intPosAt=0;
	  int intPosDot=0;

	  intPosAt = strEmail_IN.indexOf('@');
	  intPosDot= strEmail_IN.indexOf('.', intPosAt);

	  blnReturn = ((intPosAt>0)&&(intPosDot>0));

	  return blnReturn;
 	}


	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 5.   C O N S T R U C T O R
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------

	/**
	 * Constructor which initialized this object and sets its 'SeminarID.'
	 * @param SEMINAR_ID: id of seminar to which to initialize this object to.
	 **/
   public SignUpCore(long SEMINAR_ID){
	  this.m_lngSeminarID=SEMINAR_ID;
   }

}