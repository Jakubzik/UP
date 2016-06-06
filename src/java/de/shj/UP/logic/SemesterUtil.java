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
 * code structure:
 *
 * 1. declarations											line	80
 *
 * 2. protected properties									line	90
 *
 * 3. public methods										line 	100
 *
 * 4. private methods										line	325
 *
 * 6. protected queries										line	355
 *
 * 7. utility												line 	523
 *
 * @version 5-00-02 (hand-coded)
 * change date              change author           change description
 * Oct 3, 2004	            h. jakubzik             creation
 *
 */
package de.shj.UP.logic;


import de.shj.UP.data.shjCore;
import java.util.Calendar;
import java.util.Locale;

/**
 * Handling semester names;
 * Creates a HTML.HtmlDate-object (with
 * a date within the given period), and
 * uses its methods.
 **/
public class SemesterUtil{


	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 1.   D E C L A R A T I O N S
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------

	public String m_strDebug;

	private java.util.Date	m_Date;


	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 2.   P R O T E C T E D  P R O P E R T I E S
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------

	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 	3.   P U B L I C  M E T H O D S
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	/**
         * UNGETESTET!
	 * @return semester start (for use in SQL query), in ISO format.
	 **/
	public String getSemesterStart(){
            Calendar cal = Calendar.getInstance();
            cal.setTime(m_Date);
            int iMonth = cal.get(Calendar.MONTH);
            String sReturn = "";
            
            // April(=3) bis Oktober(=9) ist Sommer
            if(iMonth > 2 && iMonth < 10)
                sReturn = cal.get(Calendar.YEAR) + "-4-1";
            else{
                if (iMonth <=2)
                    sReturn = (cal.get(Calendar.YEAR)-1) + "-10-1";
                else
                    sReturn = (cal.get(Calendar.YEAR)) + "-10-1";
            }
            return sReturn;
	}

	/**
         * UNGETESTET!
	 * @return semester end (for use in SQL query), in ISO format.
	 **/
	public String getSemesterEnd(){
            Calendar cal = Calendar.getInstance();
            cal.setTime(m_Date);
            int iMonth = cal.get(Calendar.MONTH);
            String sReturn = "";
            
            // April(=3) bis Oktober(=9) ist Sommer
            if(iMonth > 2 && iMonth < 10)
                sReturn = cal.get(Calendar.YEAR) + "-9-30";
            else{
                if (iMonth <=2)
                    sReturn = (cal.get(Calendar.YEAR)) + "-3-31";
                else
                    sReturn = (cal.get(Calendar.YEAR)+1) + "-3-31";
            }
            return sReturn;
	}


	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 	4.   P R I V A T E   M E T H O D S
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------

	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 	5.   P R O T E C T E D    Q U E R I E S
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------

	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 	6.   UTILITY
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------

	/**
	 * Constructs object (needs HTML.HtmlDate).
	 * @param strSemesterName is "ss2004" or "ws2004/2005" etc.
	 **/
	public SemesterUtil(String strSemesterName, Locale loc) throws Exception{

            String strDate		= "";

            if(strSemesterName.startsWith("ss")){
                    strDate = strSemesterName.substring(2) + "-4-2";
            }else{
                if(strSemesterName.startsWith("ws")){
                            strDate = strSemesterName.substring(2, 6) + "-10-2";
                    }
            }
            m_Date = shjCore.g_ISO_DATE_FORMAT.parse(strDate);
	}
}