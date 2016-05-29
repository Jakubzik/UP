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

import de.shj.UP.HTML.HtmlDate;

/**
 * This class saves Exam-Reports ('Pr?fungsmeldungen') to disk in Xml-format.
 **/
public class SeminarExtractor extends shjCore{

	private 	Connection 	conDBLocal;
	private		boolean		isConnectedLocal=false;

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
//	private void run() throws Exception{
//
//
//	   // ---------------------------------------------------------------------------------------
//	   // 1. Make sure output can be written into file and there's no overwrite
//	   // ---------------------------------------------------------------------------------------
//
//	   File 					fileDestination		= new 	File( this.strOutputFile );
//
//	   if(fileDestination.exists()){throw new Exception("File '" + this.strOutputFile +"' already exists. You must specify an output-file that does not yet exist.");}
//
//	   fileDestination								=		null;
//	   fileDestination								= new 	File( this.strOutputFile.substring(0, this.strOutputFile.lastIndexOf("/")) );
//
//	   if(!fileDestination.exists()){
//	     if(!fileDestination.mkdirs()){throw new Exception("Directory '" + this.strOutputFile.substring(0, this.strOutputFile.lastIndexOf("/")) + "' cannot be created. Please change your settings concerning the output.");}
//       }
//
//	   fileDestination								=		null;
//
//	   // ---------------------------------------------------------------------------------------
//	   // 2. Get Xml-Report and write to disk
//	   // ---------------------------------------------------------------------------------------
//
//	   DataOutputStream 		out					= new 	DataOutputStream( new FileOutputStream( this.strOutputFile ) );
//	   ResultSet				rst					= null;
//
// 	   long						lngSeminarID		= 2;
//
// 	   rst = this.sqlQuery("select * from \"tblBdStudent\" where " +
// 	   		"(\"intStudentFach1\"=1601 or \"intStudentFach1\"=1602) or " +
// 	   		"(\"intStudentFach2\"=1601 or \"intStudentFach2\"=1602) or " +
// 	   		"(\"intStudentFach3\"=1601 or \"intStudentFach3\"=1602) or " +
// 	   		"(\"intStudentFach4\"=1601 or \"intStudentFach4\"=1602) or " +
// 	   		"(\"intStudentFach5\"=1601 or \"intStudentFach5\"=1602) or " +
// 	   		"(\"intStudentFach6\"=1601 or \"intStudentFach6\"=1602)");
// 	   
// 	   while(rst.next()){
// 		   Student s = new Student();
// 		   s.initByRst(rst);
// 		   out.writeBytes(s.toDBAddString() + "\n\n");
// 	   }
// 	   rst.close();
// 	   
// 	   rst = this.sqlQuery("select * from \"tblBdStudentXLeistung\" where \"lngSdSeminarID\"=2;");
// 	   while(rst.next()){
// 		   StudentXLeistung x = new StudentXLeistung();
// 		   x.initByRst(rst);
// 		   out.writeBytes(x.toDBAddString() + "\n\n");
// 	   }
// 	   rst.close();
//	  
// 	   rst = this.sqlQuery("select * from \"tblBdStudentXLeistung\" where \"lngSdSeminarID\"=2;");
// 	   while(rst.next()){
// 		   StudentXLeistung x = new StudentXLeistung();
// 		   x.initByRst(rst);
// 		   out.writeBytes(x.toDBAddString() + "\n\n");
// 	   }
// 	   rst.close();
//
//	  if(!(blnTestrunOnly)){
//		out.close();
//	    out					= new 	DataOutputStream( new FileOutputStream( this.strOutputFile + "-undo.sql") );
//	   // out.writeBytes("update \"tblBdStudentXPruefung\" set \"blnStudentPruefungZUVInformiert\"='f'::bool where (\"strStudentPruefungCustom1\"='" + strRowMarker + "');");
//      }
//
//	  System.err.println("...Done. Info in '" + this.strOutputFile + "'");
//	  System.err.println("Rows in 'tblBdStudentXPruefung' are marked with: ");
//	  //System.err.println("   strStudentPruefungCustom1 = '" + strRowMarker + "'");
//
//	  if(!(blnTestrunOnly)){
//		  System.err.println("Type 'psql SignUpXP < " + this.strOutputFile + "-undo.sql");
//		  System.err.println("To undo changes in the database. ");
//  	  }else{
//		  System.err.println("Testrun, no changes made to database.");
//      }
//
//	  out.close();
//
//    }


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

		SeminarExtractor this_=new SeminarExtractor();

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
			  System.err.println("#ERROR: the method run of Seminar extractor does not exist anymore. I'm doing nothing now.");
              //this_.run();
	        }catch(Exception eNoRun){System.err.println("\ncom.shj.signUp.ZuvPMeldg reports a failure: \n" + eNoRun.toString()); System.exit(1);}

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
    public SeminarExtractor(){}


  }//end class

