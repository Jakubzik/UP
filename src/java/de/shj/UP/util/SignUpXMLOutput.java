/*
 * Created on 14.01.2005
 * Changes
 * date			who		what
 * 2006-2-27	hj		transformXml made public (for gs and rtf-printing)
 */
package de.shj.UP.util;

import java.io.*;

import javax.xml.transform.*;
import javax.xml.transform.stream.*;

/**
 * @author heiko jakubzik
 * Utility to convert an XML document using an XSLT stylesheet into 
 * latex, which is in turn turned into pdf by the system. The PDF-file 
 * is stored locally.
 */
public class SignUpXMLOutput {

	private String m_strLatexCommand = "/usr/bin/latex";
    private String m_strDvipsCommand = "/usr/bin/dvips";
    private String m_strPs2gifCommand = "/usr/X11R6/bin/convert -antialias -crop 0x0";   
    private String m_strPs2pdfCommand = "/usr/bin/ps2pdf -sPAPERSIZE=a4 ";
    private String m_strDvipPDFCommand= "/usr/bin/dvipdf";
    private String strWorkingDirectory	="";
    private String m_strOutputFilename	= "";
    private String m_strXsltPath	= "";
    private String m_strXMLSource	   = "";
    private String m_strErrMsg		  = "";
    private String g_sXSL_OUTPUT_FILE_SUFFIX = ".tex";
    private byte   m_bytLatexRuns	  = 3;
    private byte   m_bytTimeout		  = 10;
    public String ENCODING="ISO-8859-1";
    
    private boolean bReadMsg = false;		// Read the msg of the CommandRunner
    private String  m_sMsg   = "";
    
    public void setReadMsg(boolean value){
    	bReadMsg = value;
    }
    
    public String getMsg(){
    	return m_sMsg;
    }
    
    /**
     * <pre>
     * The method @link #tranformXml() 
     * produces an output file whose name ends with ".tex" 
     * by default.
     * 
     * You can chance the suffix calling this method here 
     * first, e.g. setTransformationOutputSuffix(".rtf")
     * </pre> 
     * @param sSuffix
     */
    public void setTransformationOutputSuffix(String sSuffix){
    	g_sXSL_OUTPUT_FILE_SUFFIX=sSuffix;
    }
    
	/**
	 * @param strXML URI to Xml-Document
	 * @param strXSL path to XSL-document
	 * @param strOutputFilename Where you want the .pdf to be stored.
	 */
	public SignUpXMLOutput(String strXML, String strXSL, String strWorkingDir, String strOutputFilename){
		this.setXMLSource(strXML);
		this.setXsltPath(strXSL);
		this.setWorkingDirectory(strWorkingDir);
		this.setOutputFilename(strOutputFilename);
	}

	/**
	 * set the timeout for a pslatex or dvips command. (If the 
	 * process does not exit after this time, it is terminated).<br />
	 * The default is 10 secs. (So it might take a minute after the 
	 * entire conversion from .tex through dvi to ps to pdf 
	 * if terminated in case of error).
	 * @param timeout
	 */
	public void setTimeout(byte timeout){
		m_bytTimeout = timeout;
	}
	
	/**
	 * @return errors that occurred during .toPDF if any.
	 */
	public String getErrorMsg(){
		if(m_strErrMsg.equals("")) return "";
		
		return "Using:\n" +
			"Xml :" + this.getXMLSource() + "\n" + 
			"Xsl :" + this.getXsltPath() + "\n" + 
			"WorkingDir:" + this.getWorkingDirectory() + "\n" + 
			"Outpath:" + this.getOutputPath() + "\n\n" +
			m_strErrMsg;
	}

        	/**
	 * Converts the Xml-File to PDF using the current XSL stylsheet and latex. 
	 * Information on possible errors can be retrieved through getErrorMsg().
	 * @throws Exception
	 */
	public void htmlToPDF() throws Exception{
   	            
		CommandRunner thread = new CommandRunner();
		File workerFile = new File( getWorkingDirectory() );
		thread.setReadMsg(bReadMsg);
		
		// Run wkhtml2pdf getLatexRuns() times
//                thread.executeProgram(m_strHtml2PDFCommand + " " + getOutputPath() + ".pdf", null, workerFile, m_bytTimeout);
                if ( thread.getExitValue() != 0 ){
                        m_strErrMsg += "Error converting to .pdf:\n" + thread.getError() + "\n";
                        thread.executeProgram("killall wkhtml2pdf", null, workerFile, 1);
                }
                //if(bReadMsg) m_sMsg += "Latex-Run " + ii + ": " + thread.getMsg() + "\n\n";

		
		// Run dvips
		thread.executeProgram(m_strDvipsCommand + " -o " + getOutputPath() + ".ps " +
	                 getOutputPath() + ".dvi", null, workerFile, m_bytTimeout);

		if ( thread.getExitValue() != 0 ){
			m_strErrMsg += "Error converting to .ps:\n" + thread.getError() + "\n";
		}
		
		if(bReadMsg) m_sMsg += "Dvips: " + thread.getMsg() + "\n\n";
		
		thread.executeProgram(m_strPs2pdfCommand + " " + getOutputPath() + ".ps " +
				getOutputPath() + ".pdf", null, workerFile, m_bytTimeout);
		
		if ( thread.getExitValue() != 0 ){
			m_strErrMsg += "Error converting to PDF:\n" + thread.getError() + "\n";
		}
		
		if(bReadMsg) m_sMsg += "PS2PDF: " + thread.getMsg() + "\n\n";

		if(m_strErrMsg.equals("")) deleteTmpFiles();
		
	}
	/**
	 * Converts the Xml-File to PDF using the current XSL stylsheet and latex. 
	 * Information on possible errors can be retrieved through getErrorMsg().
	 * @throws Exception
	 */
	public void toPDF() throws Exception{
   	      
		try {  
			tranformXml();
		} catch (TransformerConfigurationException e) {
			m_strErrMsg+=e.toString();
		} catch (TransformerFactoryConfigurationError e) {
			m_strErrMsg+=e.toString();
		} catch (TransformerException e) {
			m_strErrMsg+=e.toString() + "\n\nLocation:" + e.getLocationAsString() + "\n";
		}          
		
		CommandRunner thread = new CommandRunner();
		File workerFile = new File( getWorkingDirectory() );
		thread.setReadMsg(bReadMsg);
		
		// Run latex getLatexRuns() times
		for(int ii=0; ii<getLatexRuns();ii++){
			thread.executeProgram(m_strLatexCommand + " " + getOutputPath() + ".tex", null, workerFile, m_bytTimeout);
			if ( thread.getExitValue() != 0 ){
				m_strErrMsg += "Error converting to .tex:\n" + thread.getError() + "\n";
				thread.executeProgram("killall latex", null, workerFile, 1);
			}
			if(bReadMsg) m_sMsg += "Latex-Run " + ii + ": " + thread.getMsg() + "\n\n";
		}
		
		// Run dvips
		thread.executeProgram(m_strDvipsCommand + " -o " + getOutputPath() + ".ps " +
	                 getOutputPath() + ".dvi", null, workerFile, m_bytTimeout);

		if ( thread.getExitValue() != 0 ){
			m_strErrMsg += "Error converting to .ps:\n" + thread.getError() + "\n";
		}
		
		if(bReadMsg) m_sMsg += "Dvips: " + thread.getMsg() + "\n\n";
		
		thread.executeProgram(m_strPs2pdfCommand + " " + getOutputPath() + ".ps " +
				getOutputPath() + ".pdf", null, workerFile, m_bytTimeout);
		
		if ( thread.getExitValue() != 0 ){
			m_strErrMsg += "Error converting to PDF:\n" + thread.getError() + "\n";
		}
		
		if(bReadMsg) m_sMsg += "PS2PDF: " + thread.getMsg() + "\n\n";

		if(m_strErrMsg.equals("")) deleteTmpFiles();
		
	}
	
	/**
	 * like toPDF, only it uses dvipdf instead of dvips
	 * @throws Exception
	 */
	public void toPDFDirect() throws Exception{
   	      
		try {  
			tranformXml();
		} catch (TransformerConfigurationException e) {
			m_strErrMsg+=e.toString();
		} catch (TransformerFactoryConfigurationError e) {
			m_strErrMsg+=e.toString();
		} catch (TransformerException e) {
			m_strErrMsg+=e.toString() + "\n\nLocation:" + e.getLocationAsString() + "\n";
		}          
		
		CommandRunner thread = new CommandRunner();
		File workerFile = new File( getWorkingDirectory() );
		thread.setReadMsg(bReadMsg);
		
		// Run latex getLatexRuns() times
		for(int ii=0; ii<getLatexRuns();ii++){
			thread.executeProgram(m_strLatexCommand + " " + getOutputPath() + ".tex", null, workerFile, m_bytTimeout);
			if ( thread.getExitValue() != 0 ){
				m_strErrMsg += "Error converting to .tex:\n" + thread.getError() + "\n";
				thread.executeProgram("killall latex", null, workerFile, 1);
			}
			if(bReadMsg) m_sMsg += "Latex-Run " + ii + ": " + thread.getMsg() + "\n\n";
		}
		
		// Run dvipdf
		thread.executeProgram(m_strDvipPDFCommand + " " + getOutputPath() + ".dvi " +
	                 getOutputPath() + ".pdf", null, workerFile, m_bytTimeout);

		if ( thread.getExitValue() != 0 ){
			m_strErrMsg += "Error converting to .pdf [dvipdf]:\n" + thread.getError() + "\n";
		}
		
		if(bReadMsg) m_sMsg += "Dvips: " + thread.getMsg() + "\n\n";
		
		if(m_strErrMsg.equals("")) deleteTmpFiles();
		
	}
	
	
	/**
	 * Converts the Xml-File to PDF using the current XSL stylsheet and latex. 
	 * Information on possible errors can be retrieved through getErrorMsg().
	 * @throws Exception
	 */
	public void toPDFOld() throws Exception{
   	      
		try {
			//Setup Transformer   
			tranformXml();
		} catch (TransformerConfigurationException e) {
			m_strErrMsg+=e.toString();
		} catch (TransformerFactoryConfigurationError e) {
			m_strErrMsg+=e.toString();
		} catch (TransformerException e) {
			m_strErrMsg+=e.toString();
		}          
		
		// Get runtime.
		Runtime r = Runtime.getRuntime();
		Process p;
		
		File workerFile = new File( getWorkingDirectory() );

		// Run latex getLatexRuns() times
		for(int ii=0; ii<getLatexRuns();ii++){
			
			p = r.exec(m_strLatexCommand + " " + getOutputPath() + ".tex", null, workerFile);
			if ( p.waitFor() != 0 ){
				m_strErrMsg += "Error converting to .tex:\n" + readErrorStream(p) + "\n";
			}   
		}
		
		// Run dvips
	    p = r.exec(m_strDvipsCommand + " -o " + getOutputPath() + ".ps " +
	                 getOutputPath() + ".dvi", null, workerFile);
		if ( p.waitFor() != 0 ){
			m_strErrMsg += "Error converting to .ps:\n" + readErrorStream(p) + "\n";
		}
		
		// Run convert in directory "imagePath".
		//	    p = r.exec(convertCommand + " " + filename + ".ps " +
		//	               filename + ".gif", null, imagePath);
		//	    if ( p.waitFor() != 0 ) out.write("<p>Error in convert</p>\n");      
		
		// Run convert in directory "imagePath".
		p = r.exec(m_strPs2pdfCommand + " " + getOutputPath() + ".ps " +
				getOutputPath() + ".pdf", null, workerFile);
		
		if ( p.waitFor() != 0 ){
			m_strErrMsg += "Error converting to PDF:\n" + readErrorStream(p) + "\n";
		}

		if(m_strErrMsg.equals("")) deleteTmpFiles();
		
	}
	
	/**
	 * @param p
	 */
	private String readErrorStream(Process p) {
		byte 	b[] 		= new byte[1024];
		boolean blnGoOn		= true;
		int 	intLength 	= 0;
		String 	strReturn 	= "";
		
		while(blnGoOn){
			try {
				intLength=p.getErrorStream().read(b, 0, 1024);
			} catch (IOException e) {
				blnGoOn=false;
			}
			blnGoOn = (intLength>0);
			try {
				strReturn += new String(b,0,intLength);
			} catch (RuntimeException e1) {
				blnGoOn=false;
			}
		}
		return strReturn;
	}

	/**
	 * @return Full path except for extension (e.g. /home/SignUp/Exam, hinting 
	 * at /home/SignUp/Exam.pdf, /homeSignUp/Exam.tex etc.)
	 */
	public String getOutputPath(){
		return getWorkingDirectory() + getOutputFilename();
	}
	
	/**
	 * The .tex, .log, .aux and .ps-files are deleted, only pdf remains. 
	 */
	private void deleteTmpFiles() {
		File f;
		f = new File(getOutputPath() + ".tex");
	     f.delete();
	     f = new File(getOutputPath() + ".log");
	     f.delete();
	     f = new File(getOutputPath() + ".aux");
	     f.delete();
	     f = new File(getOutputPath() + ".dvi");
	     f.delete();
	     f = new File(getOutputPath() + ".ps");
	     f.delete();
	}

	/**
	 * Transforms the Xml document using the Xslt stylesheet (encoding sensitive), 
	 * and stores the result in a file. The file is: WorkingDirectory + OutputFilename + ".tex".
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerConfigurationException
	 * @throws TransformerException
	 */
	public void tranformXml() throws TransformerFactoryConfigurationError, TransformerConfigurationException, TransformerException {
		TransformerFactory transformerFactory	= TransformerFactory.newInstance();     
	      Source xsltSrc = new StreamSource(new File(getXsltPath()));      
	      Transformer transformer = transformerFactory.newTransformer(xsltSrc);
	     
	      Source src = new StreamSource(getXMLSource());
	      Result res = new javax.xml.transform.stream.StreamResult(new File( getOutputPath() + g_sXSL_OUTPUT_FILE_SUFFIX));
	      transformer.setOutputProperty(OutputKeys.ENCODING, this.ENCODING);
	     

	      //Start the transformation and rendering process
	      transformer.transform(src, res);
	}

	/**
	 * @param latexCommand The latex command to set (default is /usr/bin/latex).
	 */
	public void setLatexCommand(String latexCommand) {
		this.m_strLatexCommand = latexCommand;
	}

	/**
	 * @return Returns the latex command.
	 */
	public String getLatexCommand() {
		return m_strLatexCommand;
	}

	/**
	 * @param dvipsCommand The dvips command to set (default is /usr/bin/dvips).
	 */
	public void setDvipsCommand(String dvipsCommand) {
		this.m_strDvipsCommand = dvipsCommand;
	}

	/**
	 * @return Returns the dvips command.
	 */
	public String getDvipsCommand() {
		return m_strDvipsCommand;
	}

	/**
	 * @param convert command The convert command to set (default is /usr/X11R6/bin/convert -antialias -crop 0x0).
	 */
	public void setPs2gifCommand(String convertCommand) {
		this.m_strPs2gifCommand = convertCommand;
	}

	/**
	 * @return Returns the convert command.
	 */
	public String getPs2gifCommand() {
		return m_strPs2gifCommand;
	}

	/**
	 * @param ps2pdfCommand The ps2pdf command to set.
	 */
	public void setPs2pdfCommand(String ps2pdfCommand) {
		this.m_strPs2pdfCommand = ps2pdfCommand;
	}

	/**
	 * @return Returns the ps2pdf command.
	 */
	public String getPs2pdfCommand() {
		return m_strPs2pdfCommand;
	}

	/**
	 * @param strWorkingDirectory The Working Directory to set.
	 */
	public void setWorkingDirectory(String strWorkingDirectory) {
		this.strWorkingDirectory = strWorkingDirectory;
	}

	/**
	 * @return Returns the Working Directory.
	 */
	public String getWorkingDirectory() {
		return strWorkingDirectory;
	}

	/**
	 * @param strOutputFilename The Output Filename to set.
	 */
	public void setOutputFilename(String strOutputFilename) {
		this.m_strOutputFilename = strOutputFilename;
	}

	/**
	 * @return Returns the Output Filename.
	 */
	public String getOutputFilename() {
		return m_strOutputFilename;
	}

	/**
	 * @param m_strXsltPath The Xslt Path to set.
	 */
	public void setXsltPath(String m_strXsltPath) {
		this.m_strXsltPath = m_strXsltPath;
	}

	/**
	 * @return Returns the Xslt Path.
	 */
	public String getXsltPath() {
		return m_strXsltPath;
	}

	/**
	 * @param strXMLSource The XMLSource URI to set.
	 */
	public void setXMLSource(String strXMLSource) {
		this.m_strXMLSource = strXMLSource;
	}

	/**
	 * @return Returns the XMLSource URI.
	 */
	public String getXMLSource() {
		return m_strXMLSource;
	}

	/**
	 * @param m_bytLatexRuns The number of latex runs (default is three), to gather information for TOC etc. to set.
	 */
	public void setLatexRuns(byte bytLatexRuns) {
		this.m_bytLatexRuns = bytLatexRuns;
	}

	/**
	 * @return Returns how often latex is run (default is three), to gather information for TOC etc.
	 */
	public byte getLatexRuns() {
		return m_bytLatexRuns;
	}
}
