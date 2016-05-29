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
 * Nov 9, 2003	            h. jakubzik             --
 *
 */

package de.shj.UP.data;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import de.shj.UP.logic.StudentZUV;
import de.shj.UP.util.SignUpLocalConnector;

/**
 * This class serves as an interface to load XML-Data into SignUp. Currently, it supports only
 * one specific format, 'Student.xsd.' In order to load the data (into SignUp's database),
 * it is recommended to use the script 'ImportXML.sh' that automatically calls this class's main-method.
 * Various other scripts (also
 * for the conversion of ZUV data from Excel to XML) are shipped with the application.
 **/
public class DataInput extends shjCore{


	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 1.   P R I V A T E  D E C L A R A T I O N S
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------

	private String strOutputFile;
	private String strInputFile;


	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 2.   P U B L I C   M E T H O D :  R U N
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------

	/**
	 *  Method does this:<br/>
	 *  1. Open file and create XML-Document-object (on failure throws file-not-found or sax.parser exception),<br/>
	 *  2. Ensure that output can be written (Default: './Result.xml'). Ensure there's no overwrite.<br/>
	 *  3. Get NodeList of Students to update and initialize vars for transaction reporting.<br/>
	 *  4. Go through NodeList, construct 'com.shj.signUp.logic.StudentZUV - objects, and update/add them in database. Write report on transactions.<br/>
	 *  @throws Exceptions on error, e.g.: Output-file already exists, file/folder-structure to output-path cannot
	 *  be created,
	 **/
	private void run() throws Exception{

	   // ---------------------------------------------------------------------------------------
	   // 1. Open file and load content into an org.w3c.Document
	   // ---------------------------------------------------------------------------------------
		SignUpLocalConnector db = new SignUpLocalConnector();
		
	   System.err.println("Reading file " + strInputFile);
       InputSource 				in					= new 	InputSource(new FileInputStream(this.strInputFile));
       DocumentBuilderFactory 	dfactory 			= 		DocumentBuilderFactory.newInstance();

       dfactory.setNamespaceAware(true);

       System.err.println("Parsing " + strInputFile + " as Xml Document.");
       Document 				doc					= 		dfactory.newDocumentBuilder().parse(in);
       NodeList 				nlist				=		null;
	   String 					strTmp				=		"";

	   // ---------------------------------------------------------------------------------------
	   // 2. Make sure output can be written into file and there's no overwrite
	   // ---------------------------------------------------------------------------------------
	   System.err.println("Creating " + strOutputFile);
	   File 					fileDestination		= new 	File( this.strOutputFile );

	   if(fileDestination.exists()){throw new Exception("File '" + this.strOutputFile +"' already exists. You must specify an output-file that does not yet exist.");}

	   fileDestination								=		null;
	   fileDestination								= new 	File( this.strOutputFile.substring(0, this.strOutputFile.lastIndexOf("/")) );

	   if(!fileDestination.exists()){
	     if(!fileDestination.mkdirs()){throw new Exception("Directory '" + this.strOutputFile.substring(0, this.strOutputFile.lastIndexOf("/")) + "' cannot be created. Please change your settings concerning the output.");}
       }

	   fileDestination								=		null;

	   // ---------------------------------------------------------------------------------------
	   // 3. Get NodeList of Students to update and initialize vars for transaction reporting.
	   // ---------------------------------------------------------------------------------------
	   System.err.println("Reading Childnodes ... ");
	   try{
 	     nlist = doc.getDocumentElement().getChildNodes();
       }catch(Exception eInvalidXML){System.err.println("Invalid XML? ... " + eInvalidXML.toString());}

       System.err.println("Creating the output stream ... ");
	   java.util.Date 			dtmStart			= new 	java.util.Date();
	   DataOutputStream 		out					= new 	DataOutputStream( new FileOutputStream( this.strOutputFile ) );
	   
	   out.writeBytes("<SignUp method=\"Update\">");
	   
	   String 					strErr				=		"";
	   StudentZUV 				s					=		null;
	   int 						ij;
	   int 						intUpdates			=		0;		// count of updates
	   int 						intAdditions		=		0;		// count of additions
	   int 						intUpdateErrs		=		0;		// count of update-errors
	   int 						intAddErrs			=		0;		// count of addition-errors
	   int 						intInternalErrs		=		0;		// count of internal errors
	   int 						intRead				=		0;		// count of numbers read
	   int						intShortcuts		= 		0;		//
	   int						intShortcutErrs		= 		0;
	   float 					dblNodeNumber		=		0;		// nList.Length() / 2 = number of Students

	   final int 				INT_ERR				=		StudentZUV.INTERNAL_ERROR;

	   // ---------------------------------------------------------------------------------------
	   // 4. Go through NodeList, construct 'com.shj.signUp.logic.StudentZUV - objects,
	   //	 and update/add them in database. Write report on transactions.
	   // ---------------------------------------------------------------------------------------
	   System.err.println("Counting Childnodes ... ");
	   if(nlist==null) System.err.println("Nodelist is null, something went wrong...failure anticipated.");
	   dblNodeNumber=nlist.getLength()/2;
	   System.err.println("Anzahl gefundener Studenten: " + dblNodeNumber);
	   ij=-1;
	   
	   boolean bShortcut=false;
	   boolean bShortcut_OK=false;
	   
	   // iterate entire nodelist
       for(int ii=0;ii<nlist.getLength();ii++){

    	   //reset:
    	   bShortcut=false;
    	   bShortcut_OK=false;
		   
    	   if(nlist.item(ii).getNodeName().equals("Student")){
		     try{
		    	//new on Aug 2008: see if this is a shortcut, 
		    	// that simply updates the timestamp that 
		    	// indicates when the student was last
		    	// reported as immatriculated
		    	 
		    	if((nlist.item(ii).getAttributes().item(0).getNodeValue()).equals("Exec-SQL")){
		    		bShortcut=true;
		    		bShortcut_OK=db.sqlExeLocal(shjNodeValue(nlist.item(ii), "SQL"));
		    	}else{
			    	
					// if this node describes a student, construct StudentZUV and call s.updateZUV() (add or update student)
					s=new StudentZUV(nlist.item(ii));
					s.extConnect(db);
					//System.err.println("Init: " + s.getStudentNachname() + ", " + s.getMatrikelnummer());
					//System.err.println("New?: " + s.isNewStudent());
					ij=s.updateZUV();
		    	}
				intRead++;
				if ( (intRead % 10 ) == 0) System.err.println(intRead + " Nodes read, ~ " + (int)((float)(intRead/dblNodeNumber)*100) + " %\b\b");
		     }catch(Exception eXMLNotOkay){strErr="Please check XML->" + eXMLNotOkay.toString();eXMLNotOkay.printStackTrace(System.err);ij=StudentZUV.INTERNAL_ERROR;}

			// write log in XML-format:
		    if(bShortcut){
		    	out.writeBytes("<Student><Matrikelnummer>" + shjNodeValue(nlist.item(ii), "Matrikelnummer") + "</Matrikelnummer>" +
		    			"<Action>SHORTCUT (Just Update Timestamp)</Action>" +
		    			"<Result>" + ((bShortcut_OK) ? "OK" : "FAILURE..." + shjNodeValue(nlist.item(ii), "SQL")) + "</Result>");
		    	
		    }else{
		    	out.writeBytes("<Student><Matrikelnummer>" + shjNodeValue(nlist.item(ii), "Matrikelnummer") + "</Matrikelnummer>" +
		    			"<Action>" + (((ij==StudentZUV.UPDATE_OKAY)||(ij==StudentZUV.UPDATE_FAILED)) ? "Update" : "Add") + "</Action>" +
		    			"<Result>" + (((ij==StudentZUV.UPDATE_OKAY)||(ij==StudentZUV.ADDED_OKAY)) ? "OK" : "FAILURE..." + s.m_strDebug) + "</Result>");
		    }

			// count result:
		    if(bShortcut){
		    													intShortcuts++;
		    	if	    ( !bShortcut_OK )						intShortcutErrs++;
		    }else{
				if		( ij==StudentZUV.INTERNAL_ERROR ) 		intInternalErrs++;
				else if ( ij==StudentZUV.UPDATE_OKAY    ) 		intUpdates++;
				else if ( ij==StudentZUV.UPDATE_FAILED  ) 		intUpdateErrs++;
				else if ( ij==StudentZUV.ADDED_OKAY	   ) 		intAdditions++;
				else if ( ij==StudentZUV.ADDED_FAILED   ) 		intAddErrs++;

				// report error in XML-format:
			    if((ij==StudentZUV.INTERNAL_ERROR)||(!(strErr.equals("")))) out.writeBytes("<Error>Internal Error..." + strErr + ", SignUp says:" + s.m_strDebug + ", Student:" + s.toXMLString() + "</Error>");
		    }
		    
		    strErr="";

		    // close XML report-node:
		    out.writeBytes("</Student>\n");
          }
	  }

	  // iteration is complete: Write a final report-sheet with counted numbers:
	  out.writeBytes("<Transaktion><Start>" + dtmStart + "</Start><Stop>" + new java.util.Date() + "</Stop>" +
	  				"<InputFile>" + this.strInputFile + "</InputFile>" +
	  				"<OutputFile>" + this.strOutputFile + "</OutputFile>" +
	  				"<NodesRead>" + intRead + "</NodesRead>" +
	  				"<InternalErrs>" + intInternalErrs + "</InternalErrs>" +
	  				"<Updates><Total>" + (intUpdates + intUpdateErrs + intShortcuts + intShortcutErrs) + "</Total><OK>" + intUpdates + intShortcuts + "</OK><Failed>" + intUpdateErrs + intShortcutErrs + "</Failed><Shortcuts><Total>" + intShortcuts + "</Total><Failed>" + intShortcutErrs + "</Failed></Shortcuts></Updates>" +
	  				"<Additions><Total>" + (intAdditions +  intAddErrs) + "</Total><OK>" + intAdditions + "</OK><Failed>" + intAddErrs + "</Failed></Additions>" +
	  				"</Transaktion></SignUp>");

	  System.err.println("...Done. Info in '" + this.strOutputFile + "'");
	  out.close();

    }


	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 2.   P U B L I C   M E T H O D :  M A I N
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------

   /**
    * Main method, reads an XML-file (conform to format 'Student.xsd,' and updates the database
    * accordingly (new students are added, the data of existing ones is updated). An XML-output
    * file describing the transactions is generated.<br />
    * usage:<br />
    *
    * DataInput.main [-options] InputXMLFilePath
    *
    * -o 		path where output file should be stored<br />
    *
    * -h		help on usage.
    **/
   public static void main(String[] argv) throws Exception{

		DataInput this_=new DataInput();

		this_.strOutputFile="./Result.xml";
		this_.strInputFile="";

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

                if (arg.equals("-h")) {
                    printUsage();
                    System.exit(1);
                }
            }

            // print uri
            this_.strInputFile=arg;

            System.err.println("\n\nUsing input-file " + arg + "\n");

            try{
              this_.run();
	        }catch(Exception eNoRun){System.err.println("\ncom.shj.signUp.DataInput reports a failure: \n" + eNoRun.toString() + "\n"); eNoRun.printStackTrace(System.err); System.exit(1);}

	   		System.exit(0);
       }
    } // main(String[])


	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 3.   U S A G E / C O N S T R U C T O R
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------

    /** Prints the usage. */
    private static void printUsage() {

        System.err.println( "usage: com.shj.signUp.DataInput (options) xml-file ..."	);
        System.err.println( 															);
        System.err.println( "options:"													);
        System.err.println( "  -o            Ouput log-file file [xml-format]"			);
        System.err.println( "                Default is './Result.xml'"					);
        System.err.println( "  -h            This help screen."							);

    }

	/** Constructor (empty) */
    public DataInput(){}

  }//end class

