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
 * Jan 27, 2004	            h. jakubzik             creation
 *													init(): added method to initialize object (instead of a constructor)
 *
 * Dec 13, 2004				h. jakubzik				added .getModules() [Refactored from StudentExamAddPickCredits]
 */
package de.shj.UP.logic;

import java.sql.ResultSet;


/**
 * This class is the first layer above the raw data.
 **/
public class PruefungData extends de.shj.UP.data.Pruefung{


	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 1.   D E C L A R A T I O N S
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------

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
	 * @since 5-29
	 * @param blnZulassungsvoraussetzung if set to 'true,' only modules configured as Zulassungsvoraussetzung are selected.
	 * @return List of Modules in this Exam, ordered by ModulNummer ascending.
	 * @throws Exception
	 */
	public ResultSet getModules(boolean blnZulassungsvoraussetzung) throws Exception{	
		return sqlQuery("SELECT x.\"lngSdSeminarID\", x.\"lngPruefungID\", x.\"lngModulID\", " + 
			"x.\"sngMinCreditPts\", x.\"sngMinCreditPtsPLeistung\", " +
			"m.* " +
		  "FROM \"tblSdPruefungXModul\" x, \"tblSdModul\" m " +
		  "WHERE (" + 
		   "(x.\"lngSdSeminarID\" = m.\"lngSdSeminarID\") AND " +
		   "(x.\"lngModulID\" = m.\"lngModulID\") AND " +
		   "(x.\"blnZulassungsvoraussetzung\" = " + (blnZulassungsvoraussetzung ? "true" : "false") + ") AND " +
		   "(x.\"lngSdSeminarID\" = " + this.getSdSeminarID() + ") AND " + 
		   "(x.\"lngPruefungID\" = " + this.getPruefungID() + ")" +
		  ") order by \"lngModulNummer\" asc;");
	}
	
	/**
	 * @returns boolean value with a founded guess on whether this
	 * exam is modular or not.
	 * An exam is modular, when it consists of modules with a minimum credit-point
	 * value. If this value is null or <=0, it means that the module is a credit.
	 * @throws Exception if connection to db erroneous, or if there is no record of this exam.
	 **/
	public boolean isModular() throws Exception{
		boolean 	blnReturn = false;
		ResultSet 	rst  = sqlQuery("select * from \"tblSdPruefungXModul\" where " +
                     "\"lngSdSeminarID\" = " + this.getSdSeminarID() + " AND "
                    + "\"lngPruefungID\" = " + this.getPruefungID() + " AND "
                    + "\"sngMinCreditPts\" > 0");

            blnReturn = rst.next();

            rst.close();
            return blnReturn;
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
	public PruefungData(long lngSeminarID, long lngPruefungID){
		this.setSdSeminarID  ( lngSeminarID );
		this.setPruefungID ( lngPruefungID);
	}
}