/*
 * Software-Developement H. Jakubzik, Licence Version 1.0
 *
 *
 * Copyright (c) 2004 Heiko Jakubzik.  All rights
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
 * 1. private declarations										line   85
 *
 * 2. public: run												line  100
 *
 * 3. public: main												line  225
 *
 * 4. usage	& constructor										line
 *
 *
 * @version 0-00-01 (hand-coded)
 * change date              change author           change description
 * Jul 13, 2004	            h. jakubzik             --
 * May  8, 2005				h. jakubzik				F?cher with Ids of above 100.000 are ignored (same as 
 * 													F?cher with an Id of 0 have been ignored before).
 */

package de.shj.UP.data;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import de.shj.UP.HTML.HtmlDate;

/**
 * This class saves Exam-Reports ('Pr?fungsmeldungen') to disk in Xml-format.
 **/
public class ZuvPMeldg extends shjCore{

	private 	Connection 	conDBLocal;
	private		boolean		isConnectedLocal=false;
	private		Vector m_vFachPruefungMap=null;

	/**
	 * The http-server uses a broker-mechanism for its database access;
	 * since this class is used with 'DataInput' as a standalone thing, 
	 * it creates a seperate pipeline to the database.
	 * @return true, if connection established
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public boolean connectLocal() throws SQLException, ClassNotFoundException{
    	if(conDBLocal!=null) return false;
		Class.forName("org.postgresql.Driver");
    	conDBLocal=java.sql.DriverManager.getConnection("jdbc:postgresql:SignUpXP", "postgres", "galavastik");
    	isConnectedLocal=(conDBLocal!=null);
    	return isConnectedLocal;
  	}//end of "connect"	

	public void finalize(){
		try {
			conDBLocal.close();
			conDBLocal=null;
		} catch (SQLException e) {}
		try {
			super.finalize();
		} catch (Throwable e1) {}
	}
	
  	/**
  	 * Construct and return ResultSet from query String that was handed over.
  	 * @returns a ResultSet that contains the results of the query. The ResultSet's method 'next()' has to
  	 * be called before the ResultSet can be used.
  	 * Checks if connection is okay and query-text not null.
  	 * @param strQuery: String representing a valid SQL-Select Query. Use "sqlExe" for executable SQL-Statements.
  	 * @throws Exception No Connection to Database OR
  	 * Empty Query String
  	 **/
  	public ResultSet sqlQueryLocal(String strQuery) throws Exception{
	  if(!isConnectedLocal) connectLocal();

  	  //check if connexion's okay:
  	  if((!isConnectedLocal)||(conDBLocal==null)) throw new Exception(String.valueOf(ERR_SIGNUP_NO_CONNECTION_TO_DB));

  	  //check if query not empty:
  	  if((strQuery==null)||(strQuery.equals(""))) throw new Exception(String.valueOf(ERR_SIGNUP_EMPTY_SQL));

  	  return conDBLocal.createStatement().executeQuery( strQuery );
  	}//end of "sqlQuery"	
	
	/**
	 * Executes SQL Command that does not return any values (insert, update).
	 * @param strQuery SQL-Query-String.
	 * @throws Exception No Connection to Database OR
	 * Empty SQL-String handed over
	 * @return true if database understood the command,
	 * false if an error occurred
	 **/
	public boolean sqlExeLocal(String strQuery) throws Exception{

		boolean blnReturn=false;
		if(!isConnectedLocal) connectLocal();

		try{
			Statement stmSqlExe = conDBLocal.createStatement();
			stmSqlExe.execute(strQuery);
			blnReturn=true;
		}catch(Exception e3){
			blnReturn=false;
		}

		return blnReturn;
	}//end of "sqlExe"
	
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 1.   P R I V A T E  D E C L A R A T I O N S
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------

	// where to save the Xml-file
	private String strOutputFile;


	// dont update database ... this is just a test-run.
	private boolean blnTestrunOnly;

	// for date-format in output.
	final static private java.util.Locale locale = new java.util.Locale ( "de", "DE" );

	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 2.   P R I V A T E  M E T H O D S
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------

	/**
	 * @return 'SS 2001' or 'WS 2003/2004' and the like from
	 * SignUp typical format like 'ss2001' or 'ws2003/2004'
	 * @param strSignUpSem: semester in format 'ss2001' or 'ws2004/2005'.
	 **/
	private String getSemesterZUV(String strSignUpSem){
		
		if(strSignUpSem.substring(0,1).equals("s")){
		  long lngYear = Long.parseLong(strSignUpSem.substring(2));
		  return "SS " + lngYear;
		}
		
		long lngYear = Long.parseLong(strSignUpSem.substring(2,6));
		return "WS " + lngYear + "/" + (lngYear+1);

	}

	/**
	 * @return first day of the semester handed in. For 'ws2003/2004' it returns '01.10.2003';
	 * for 'ss2001' it returns '01.04.2001'.
	 * @param strSignUpSem: semester in format 'ss2001' or 'ws2004/2005'.
	 **/
	private String getSemesterZUVDate(String strSignUpSem){
		if(strSignUpSem.substring(0,1).equals("s")){
			long lngYear = Long.parseLong(strSignUpSem.substring(2));
			return "01.04." + lngYear;
		}
		long lngYear = Long.parseLong(strSignUpSem.substring(2,6));
		return "01.10." + lngYear;
	}

 	/**
 	 * @return ResultSet selects information on student and exams, making sure that
	 * - pruefung.strPruefungZUVTyp <> "-1" (so that this is an exam that is reported to the ZUV,
	 * - ZUVINformiert		is either false or null (ZUV has NOT been informed yet),
	 * - Validiert = true	(default),
	 * - Bestanden = true  (default)
	 * - dtmStudentZUVUpdate = max (currently immatriculated students only),
	 * - Pruefung is mapped to the subject the student studies
	 * Sorted by seminar, Exam, and last name.
	 * 
	 * Revised on Mon, Oct 13, 2008. (hj)
	 **/
	private ResultSet getPMeldg() throws Exception{
//		return this.sqlQueryLocal("SELECT " +
//			  "s.*, " +
//			  "sxp.*, " +
//			  "p.\"lngPruefungID\", " +
//			  "p.\"strPruefungBezeichnung\", " +
//  "p.\"strPruefungFach\", " +
//  "p.\"strPruefungAbschluss\", " +
//  "p.\"strPruefungZUVTyp\", " +
//  "p.\"strPruefungZUVFach\", " +
//  "p.\"strPruefungZUVAmt\", " +
//  "f0.\"intFachID\" as fach1id, f0.\"strFachBeschreibung\" as fach1name," +
//  "f1.\"intFachID\" as fach2id, f1.\"strFachBeschreibung\" as fach2name," +
// "f2.\"intFachID\" as fach3id, f2.\"strFachBeschreibung\" as fach3name," +
//  "f3.\"intFachID\" as fach4id, f3.\"strFachBeschreibung\" as fach4name," +
//  "f4.\"intFachID\" as fach5id, f4.\"strFachBeschreibung\" as fach5name," +
//  "f5.\"intFachID\" as fach6id, f5.\"strFachBeschreibung\" as fach6name " +
//"FROM 	\"tblSdFach\" AS f5, \"tblSdFach\" AS f4, " +
//	"\"tblSdFach\" AS f3, \"tblSdFach\" AS f2, " +
//	"\"tblSdFach\" AS f1, \"tblSdFach\" AS f0, " +
//	"\"tblBdStudentXPruefung\" sxp, " +
//	"\"tblSdPruefung\" p, " +
//	"\"tblBdStudent\" s " +
//"WHERE (" +
//  "(s.\"strMatrikelnummer\" = sxp.\"strMatrikelnummer\") AND " +
//  "(p.\"lngPruefungID\" 	 = sxp.\"lngSdPruefungsID\") AND " +
//  "(p.\"lngSdSeminarID\" 	 = sxp.\"lngSdSeminarID\") AND " +
//  "(f0.\"intFachID\" 	 = s.\"intStudentFach1\") AND " +
//  "(f1.\"intFachID\" 	 = s.\"intStudentFach2\") AND " +
//  "(f2.\"intFachID\" 	 = s.\"intStudentFach3\") AND " +
//  "(f3.\"intFachID\" 	 = s.\"intStudentFach4\") AND " +
//  "(f4.\"intFachID\" 	 = s.\"intStudentFach5\") AND " +
//  "(f5.\"intFachID\" 	 = s.\"intStudentFach6\") AND " +
//  "(trim(p.\"strPruefungZUVTyp\") <>  '-1')  AND " +
//  "((sxp.\"blnStudentPruefungZUVInformiert\"='f'::bool) or (sxp.\"blnStudentPruefungZUVInformiert\" isnull)) AND " +
//  "(sxp.\"blnStudentPruefungValidiert\"='t'::bool) AND " +
//  "(sxp.\"blnStudentPruefungBestanden\"='t'::bool)" +
//") order by p.\"lngSdSeminarID\" asc;");

		ResultSet r = this.sqlQueryLocal("select max(\"dtmStudentZUVUpdate\") as mm from \"tblBdStudent\";");
		r.next();
		String sMax = r.getString("mm");
		r.close();
		return this.sqlQueryLocal("SELECT " +
			      "s.*, " +
			      "sxp.*, " +
			      "p.\"lngPruefungID\", " +
			      "p.\"strPruefungBezeichnung\", " +
			      "p.\"strPruefungFach\", " +
			      "p.\"strPruefungAbschluss\", " +
			      "p.\"strPruefungZUVTyp\", " +
			      "p.\"strPruefungZUVFach\", " +
			      "p.\"strPruefungZUVAmt\"," +
			      "f0.\"intFachID\" as fach1id, f0.\"strFachBeschreibung\" as fach1name," +
			      "f1.\"intFachID\" as fach2id, f1.\"strFachBeschreibung\" as fach2name," +
			      "f2.\"intFachID\" as fach3id, f2.\"strFachBeschreibung\" as fach3name," +
			      "f3.\"intFachID\" as fach4id, f3.\"strFachBeschreibung\" as fach4name," +
			      "f4.\"intFachID\" as fach5id, f4.\"strFachBeschreibung\" as fach5name," +
			      "f5.\"intFachID\" as fach6id, f5.\"strFachBeschreibung\" as fach6name " +
			    "FROM    \"tblSdFach\" AS f5, \"tblSdFach\" AS f4," +
				   "\"tblSdFach\" AS f3, \"tblSdFach\" AS f2," +
				    "\"tblSdFach\" AS f1, \"tblSdFach\" AS f0," +
				    "\"tblBdStudentXPruefung\" sxp," +
				    "\"tblSdPruefung\" p," +
				    "\"tblBdStudent\" s, " +
				    "\"tblSdPruefungXFach\" pxf " +
			    "WHERE (" +
			      "(s.\"strMatrikelnummer\" = sxp.\"strMatrikelnummer\") AND " +
			      "(s.\"dtmStudentZUVUpdate\">='" + sMax + "') AND " +
			      "(p.\"lngPruefungID\" 	 = sxp.\"lngSdPruefungsID\") AND " +
			      "(p.\"lngSdSeminarID\" 	 = sxp.\"lngSdSeminarID\") AND " +
			      "(f0.\"intFachID\" 	 = s.\"intStudentFach1\") AND " +
			      "(f1.\"intFachID\" 	 = s.\"intStudentFach2\") AND " +
			      "(f2.\"intFachID\" 	 = s.\"intStudentFach3\") AND " +
			      "(f3.\"intFachID\" 	 = s.\"intStudentFach4\") AND " +
			      "(f4.\"intFachID\" 	 = s.\"intStudentFach5\") AND " +
			      "(f5.\"intFachID\" 	 = s.\"intStudentFach6\") AND " +
			      "(trim(p.\"strPruefungZUVTyp\") <>  '-1')  AND " +
			      "(sxp.\"blnStudentPruefungZUVInformiert\"!='t'::bool) AND " +
			      "(sxp.\"blnStudentPruefungValidiert\"='t'::bool) AND " +
			      "(sxp.\"blnStudentPruefungBestanden\"='t'::bool)) AND " +
			      "pxf.\"lngSdSeminarID\"=p.\"lngSdSeminarID\" AND " +
			      "pxf.\"lngPruefungID\"=p.\"lngPruefungID\" AND " +
			      "(" +
						"pxf.\"intFachID\"=s.\"intStudentFach1\" or " + 
						"pxf.\"intFachID\"=s.\"intStudentFach2\" or " +
						"pxf.\"intFachID\"=s.\"intStudentFach3\" or " +
						"pxf.\"intFachID\"=s.\"intStudentFach4\" or " +
						"pxf.\"intFachID\"=s.\"intStudentFach5\" or " +
						"pxf.\"intFachID\"=s.\"intStudentFach6\" " +
			    "  )" +
			    " order by p.\"lngSdSeminarID\", p.\"lngPruefungID\" asc, s.\"strStudentNachname\" asc;");

	}

	/**
	 * A more or less internal method that puts Fach-information into an
	 * Xml-specific format.<br />
	 * @version 6.01.06: if the FachID is greater than 100000, an empty String is returned.
	 * @version 6.18, May 07: mark error if there is no PFach.
	 **/
	private String getFachXmlOld(ResultSet rst, int intPFach) throws Exception{

	   String	strReturn = "";
	   boolean  bFoundPFach=false;
	   
	   for(int intFachCounter=1; intFachCounter<7; intFachCounter++){
		   int 		intFach   = rst.getInt("fach" + intFachCounter + "id");
		   
		   if((intFach	 != 0) && (intFach < 100000)){
			  strReturn += "    <Fach>\n" +
							"		<Pr�fungsfach>" + ( (intFach==intPFach) ? "ja" : "nein" ) + "</Pr�fungsfach>\n" +
							"		<ID>" + intFach + "</ID>\n" +
							"		<Name>" + rst.getString("fach" + intFachCounter + "name") + "</Name>\n" +
							"		<Fachsemester>" + rst.getString("intStudentFachsemester" + intFachCounter) + "</Fachsemester>\n" +
			 				"    </Fach>\n\n";
			  if(intFach==intPFach) bFoundPFach=true;
		   }
	   }
	   if(!bFoundPFach) strReturn += "<FachError>Kein Pruefungsfach gefunden.</FachError>\n\n";
	   return strReturn;
	}
	
	private Vector getFachPruefungMap(){
		if(m_vFachPruefungMap == null){
			m_vFachPruefungMap=new Vector();
			try {
				ResultSet rst = sqlQueryLocal("select * from \"tblSdPruefungXFach\";");
				while(rst.next()){
					m_vFachPruefungMap.add(new String[]{rst.getString("intFachID"), rst.getString("lngPruefungID"), rst.getString("lngSdSeminarID")});
				}
			} catch (SQLException e) {
				System.err.println("Kann PruefungXFach nicht �ffnen :-(");e.printStackTrace();
			} catch (Exception e) {
				System.err.println("Kann den Fach Pruefung-Vektor nicht f�llen :-(");e.printStackTrace();
			}
		}
		return m_vFachPruefungMap;
	}
	
	private boolean isEqual(String[] sArray, int iFachID, long lPruefungID, long lSeminarID){
		return sArray[0].equals(String.valueOf(iFachID)) &&
				sArray[1].equals(String.valueOf(lPruefungID)) &&
				sArray[2].equals(String.valueOf(lSeminarID));
	}
	
	
	private boolean isFachOfPruefung(int iFachID, long lPruefungID, long lSeminarID){
		Vector vFachPruefungMap = getFachPruefungMap();
		for(int ii=0; ii<vFachPruefungMap.size(); ii++){
			//if(((String[])(vFachPruefungMap.get(ii))).equals(new String[]{String.valueOf(iFachID), String.valueOf(lPruefungID), String.valueOf(lSeminarID)})){
			if(isEqual((String[])vFachPruefungMap.get(ii), iFachID, lPruefungID, lSeminarID)){
				//if(bFound==true) throw new Exception("Triple (iFachID, lPruefungID, lSeminarID)=(" + iFachID + ", " + lPruefungID + ", " + lSeminarID + ") not unique.");
				return true;
			}
		}
		return false;
	}
	
	/**
	 * A more or less internal method that puts Fach-information into an
	 * Xml-specific format.<br />
	 * @version 6.01.06: if the FachID is greater than 100000, an empty String is returned.
	 * @version 6.18, May 07: mark error if there is no PFach.
	 **/
	private String getFachXml(ResultSet rst, long lPruefungID, long lSeminarID) throws Exception{

	   String	strReturn = "";
	   boolean  bFoundPFach=false;
	   boolean  bPFach     =false;
	   for(int intFachCounter=1; intFachCounter<7; intFachCounter++){
		   int 		intFach   = rst.getInt("fach" + intFachCounter + "id");
		   
		   if((intFach	 != 0) && (intFach < 100000)){
			  bPFach	= isFachOfPruefung(intFach, lPruefungID, lSeminarID);
			  
			  strReturn += "    <Fach>\n" +
							"		<Pr�fungsfach>" + ( (bPFach) ? "ja" : "nein" ) + "</Pr�fungsfach>\n" +
							"		<ID>" + intFach + "</ID>\n" +
							"		<Name>" + rst.getString("fach" + intFachCounter + "name") + "</Name>\n" +
							"		<Fachsemester>" + rst.getString("intStudentFachsemester" + intFachCounter) + "</Fachsemester>\n" +
			 				"    </Fach>\n\n";
			  if(bPFach) bFoundPFach=true;
		   }
	   }
	   if(!bFoundPFach) strReturn += "<FachError>Kein Pruefungsfach gefunden.</FachError>\n\n";
	   return strReturn;
	}

	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 2.   P U B L I C   M E T H O D :  R U N
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------

	/**
	 *  Method does this:<br/>
	 *  1. Make sure output can be written into file and there's no overwrite
	 *  2. Get Xml-Report and write to disk
	 *  3. Write info to database
	 *  @throws Exceptions on error, e.g.: Output-file already exists, file/folder-structure to output-path cannot
	 *  be created,
	 **/
	private void run() throws Exception{


	   // ---------------------------------------------------------------------------------------
	   // 1. Make sure output can be written into file and there's no overwrite
	   // ---------------------------------------------------------------------------------------

	   File 					fileDestination		= new 	File( this.strOutputFile );

	   if(fileDestination.exists()){throw new Exception("File '" + this.strOutputFile +"' already exists. You must specify an output-file that does not yet exist.");}

	   fileDestination								=		null;
	   fileDestination								= new 	File( this.strOutputFile.substring(0, this.strOutputFile.lastIndexOf("/")) );

	   if(!fileDestination.exists()){
	     if(!fileDestination.mkdirs()){throw new Exception("Directory '" + this.strOutputFile.substring(0, this.strOutputFile.lastIndexOf("/")) + "' cannot be created. Please change your settings concerning the output.");}
       }

	   fileDestination								=		null;

	   // ---------------------------------------------------------------------------------------
	   // 2. Get Xml-Report and write to disk
	   // ---------------------------------------------------------------------------------------

	   DataOutputStream 		out					= new 	DataOutputStream( new FileOutputStream( this.strOutputFile ) );
	   ResultSet				rst					= null;
	   int 						ii					= 0;
 	   //int						intFach				= 0;
 	   //int						intPFach			= 0;
	   
 	   //int      				intFachCounter 		= 0;

 	   long						lngSeminarID		= 0;
 	   long						lngPruefungID		= 0;
 	   int						intPruefungCount	= 0;
 	   String					strMatrikelnummer	= "";

 	   java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
 	   String					strRowMarker		= formatter.format(new java.util.Date());
 	   
 	   
	   System.err.println("Fetching ResultSet...");
 	   rst	= this.getPMeldg();

 	   out.writeBytes("<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>\n<!DOCTYPE Pr�fungsmeldungen SYSTEM \"PMeldung.dtd\">\n<!-- generated with 'SignUp', (c) 2001-2004, h. jakubzik, info: http://www.heiko-jakubzik.de/SignUp/ -->" +
					   "\n<Pr�fungsmeldungen>\n<Datum>" + strRowMarker + "</Datum>\n");

 	   while(rst.next()){
		   System.err.println("Writing entry number " + ii++);
		   //intPFach				= Integer.parseInt(rst.getString("strPruefungZUVFach"));
		   strMatrikelnummer	= rst.getString("strMatrikelnummer");
		   lngPruefungID		= rst.getLong("lngPruefungID");
		   lngSeminarID			= rst.getLong("lngSdSeminarID");
		   intPruefungCount		= rst.getInt("intStudentPruefungCount");

 		out.writeBytes("<Pr�fungsmeldung>\n" +
 					   	"  <Student>\n" +
 						"     <Matrikelnummer>" + strMatrikelnummer + "</Matrikelnummer>\n" +
 						"     <Name>" + rst.getString("strStudentNachname") + "</Name>\n" +
 						"     <Vorname>" + rst.getString("strStudentVorname") + "</Vorname>\n" +
						"  </Student>\n" +
						"    <Pr�fung>\n" +
  		  				"     <F�cher>\n" +
  		  				getFachXml(rst,lngPruefungID,lngSeminarID)  +
  		  				"   </F�cher>\n" +
						"   <Semester>\n" +
		  				"     <Name>" + getSemesterZUV(rst.getString("strStudentPruefungSemester")) + "</Name>\n" +
		  				"	  <Beginn>" + getSemesterZUVDate(rst.getString("strStudentPruefungSemester")) + "</Beginn>\n" +
						"   </Semester>\n" +
						"   <Typ>\n" +
						"     <ID>" + rst.getString("strPruefungZUVTyp").trim() + "</ID>\n" +
						"     <Name>" + rst.getString("strPruefungBezeichnung") + "</Name>\n" +
						"   </Typ>\n" +
						"   <Datum>" + new HtmlDate(rst.getString("dtmStudentPruefungAusstellungsd"), locale).shjGetLocalDate() + "</Datum>\n" +					
						"   <Amt>" + rst.getString("strPruefungZUVAmt") + "</Amt>\n" +
						"	<Schl�sselwert>1250</Schl�sselwert>\n" +
						"   <Note>\n" +
						"     <ID>800</ID>\n" +
						" 	  <Name>bestanden</Name>\n" +
						"   </Note>\n" +						
						" </Pr�fung>\n" +
						"</Pr�fungsmeldung>\n\n");

	   // ---------------------------------------------------------------------------------------
	   // 3. Write info to database
	   // ---------------------------------------------------------------------------------------

	   if(!(blnTestrunOnly)){
	   	StudentXPruefung sxp = new StudentXPruefung();
		
	   	sxp.setSdSeminarID(lngSeminarID);
		sxp.setSdPruefungsID( lngPruefungID );
		sxp.setMatrikelnummer( strMatrikelnummer );
		sxp.setStudentPruefungCount(intPruefungCount);

		ResultSet rstSXP=sqlQueryLocal("select * from \"tblBdStudentXPruefung\" where (" + sxp.getSQLWhereClauseOld() + ");");
		if(rstSXP.next()) sxp.initByRst(rst);
	     
	    sxp.setStudentPruefungZUVInformiert( true );
	    sxp.setStudentPruefungCustom1( strRowMarker );
	    out.writeBytes("<!-- update successful: " + sqlExeLocal(sxp.toDBUpdateStringOld()) + " -->\n");
   	   }else{
		 out.writeBytes("<!--  testrun, no update -->\n");
	   }

     }

 	  rst.close();

	  out.writeBytes("</Pr�fungsmeldungen>");

	  if(!(blnTestrunOnly)){
		out.close();
	    out					= new 	DataOutputStream( new FileOutputStream( this.strOutputFile + "-undo.sql") );
	    out.writeBytes("update \"tblBdStudentXPruefung\" set \"blnStudentPruefungZUVInformiert\"='f'::bool where (\"strStudentPruefungCustom1\"='" + strRowMarker + "');");
      }

	  System.err.println("...Done. Info in '" + this.strOutputFile + "'");
	  System.err.println("Rows in 'tblBdStudentXPruefung' are marked with: ");
	  System.err.println("   strStudentPruefungCustom1 = '" + strRowMarker + "'");

	  if(!(blnTestrunOnly)){
		  System.err.println("Type 'psql SignUpXP < " + this.strOutputFile + "-undo.sql");
		  System.err.println("To undo changes in the database. ");
  	  }else{
		  System.err.println("Testrun, no changes made to database.");
      }

	  out.close();

    }


	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 2.   P U B L I C   M E T H O D :  M A I N
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------

   /**
    * Main method, creates an XML-file and updates the database
    * accordingly.
    * usage:<br />
    *
    * DataInput.main [-options] InputXMLFilePath
    *
    * -o 		path where output file should be stored<br />
    *
    * -h		help on usage.
    **/
   public static void main(String[] argv) throws Exception{

		System.err.println("Setting SignUp up...");

		ZuvPMeldg this_=new ZuvPMeldg();

		this_.strOutputFile="./PMeldg.xml";

        // check parameters
        for (int i = 0; i < argv.length; i++) {

        	String arg = argv[i];

            // options
            if (arg.startsWith("-")) {
                if (arg.equals("-o")) {
                    if (i == argv.length - 1) {
                        System.err.println("error: missing output-file");
                        System.exit(1);
                    }
					this_.strOutputFile=argv[++i];
                    continue;
                }

                if (arg.equals("-testrun")) this_.blnTestrunOnly = true;

                if (arg.equals("-h")) {
                    printUsage();
                    System.exit(1);
                }
            }
      }


            try{
			  System.err.println("Trying to run the export now ...");
			  System.err.println("output to " + this_.strOutputFile);
              this_.run();
	        }catch(Exception eNoRun){System.err.println("\ncom.shj.signUp.ZuvPMeldg reports a failure: \n" + eNoRun.toString() + "\n" + eNoRun.getMessage()); eNoRun.printStackTrace(); System.exit(1);}

	   		System.exit(0);

    } // main(String[])


	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 3.   U S A G E / C O N S T R U C T O R
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------

    /** Prints the usage. */
    private static void printUsage() {

        System.err.println( "usage: com.shj.signUp.ZuvPMeldg (options)  ..."	);
        System.err.println( 															);
        System.err.println( "options:"													);
        System.err.println( "  -o            Ouput log-file file [xml-format]"			);
        System.err.println( "                Default is './PMeldg.xml'"					);
        System.err.println( "  -testrun      do a testrun only, dont mark the"			);
        System.err.println( "                rows as reported to Zuv."					);
        System.err.println( "  -h            This help screen."							);

    }

	/** Constructor (empty) */
    public ZuvPMeldg(){}


  }//end class

