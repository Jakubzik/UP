/*
 * Created on 30.01.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.shj.UP.util;

import java.io.File;
import java.io.IOException;

/**
 * This Program is used to execute an external application. developed to execute a C util for linux. 
 * The output stream from the executed program is returned to System.out (stdout)
 * This program uses two threads, one thread kicks off another thread which executes the proccess. if the thread has not terminated within 120 seconds the main thread destroys the proccess and stops the executing thread, the return code is 10000, This handles process lockups. 
 * @see java.lang.Process
 * @see java.lang.Runtime
 */
public class CommandRunner extends Thread{  
	
	/**
	 * this stores the command passed by the user
	 * @see #executeProccessBackGround(String)
	 * @see #executeProgram(String)
	 */
	private String m_strCommand;  
	private String[] m_envPath;       
	private File m_workerFile;
	private String m_strErrMsg="";
	private String m_strMsg = "";
	boolean bRead = false;				// read output of thread?
	
	public String getMsg(){
		return m_strMsg;
	}
	
	public String getError(){
		return m_strErrMsg;
	}
	
	public void setReadMsg(boolean value){
		bRead = value;
	}
	
	/**
	 * Temp return value from thread
	 */
	private static int intRetVal;
	
	/*
	 * The executed program runs using this process identifier
	 */
	protected static Process p;
	
	
	/**
	 * Thread that spawns process and monitors its status. once the proccess finishes execution the output of the program is outputted to console from this method.  
	 * this thread talks to main program using static variables. 
	 * @see #sb
	 * @see #p
	 * @see #intRetVal
	 */
	public void run(){      //thread method
		try{
			Runtime t = Runtime.getRuntime();       		
			p = t.exec(m_strCommand, m_envPath, m_workerFile);  //execute the passed command
			
			if(bRead) m_strMsg = readMessage();
			
			if(p.waitFor() != 0){  //wait for proccess to terminate
				m_strErrMsg += "Error running " + m_strCommand + ": " + readErrorStream() + "\n";
			}
			
		}
		//catch any errors that may of occured and display error to console (not web app)
		catch(IOException ioe){
			m_strErrMsg += "IOException occurred when executing " + m_strCommand + ": " + ioe.getMessage(); 
		}
		catch(InterruptedException ie){
			m_strErrMsg += "InterruptedException occurred when executing " + m_strCommand + ": " + ie.getMessage(); 
		}
	}
	
	/**
	 */
	protected String readErrorStream() {
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
		return strReturn + "k";
	}        
	
	protected String readMessage() {
		byte 	b[] 		= new byte[1024];
		boolean blnGoOn		= true;
		int 	intLength 	= 0;
		String 	strReturn 	= "";
		
		while(blnGoOn){
			try {
				intLength=p.getInputStream().read(b, 0, 1024);
			} catch (IOException e) {
				blnGoOn=false;
			}
			blnGoOn = (intLength>0);
			try {
				// #hack 
				// das war im Juli 2006 zur Diagnose gs.
				// Den String braucht man nicht wirklich.
				// strReturn += new String(b,0,intLength);
			} catch (RuntimeException e1) {
				blnGoOn=false;
			}
		}
		return strReturn + "k";
	}       	
	
	
	/**
	 * Gets the return value from an executed proccess running in the background.
	 * @see #executeProccessBackGround(String)
	 * @return process exit value or -1 on error. 
	 */
	
	public int getExitValue(){
		try{
			int i = p.exitValue();
			return i;
		}
		catch(IllegalThreadStateException itse){
			return -1;
		}
		catch(Exception e){
			return -1;
		}
	}
	
	/**
	 * Kills the process currently being executed
	 */
	protected void destroyProcess(){
		p.destroy();
	}
	
	/**
	 * Same as  executeProgram(String) but allows the caller of the method to pass a timeout in seconds. 
	 * @see #executeProgram(String)
	 * @return int value representing the return value from the executed proccess. 16 is returned if the method times-out when waiting for the
	 * proccess to finish
	 * @param stCommandin String the command to executed passed as a string e.g. "ls /tmp/"
	 */
	
	public int executeProgram(String stCommandin, String[]env, File workerFile, int intTimeoutSec){
		
		CommandRunner crThrd = new CommandRunner();
		
		crThrd.m_strCommand = stCommandin;         //pass the command to run to the thread
		crThrd.m_envPath    = env;
		crThrd.m_workerFile = workerFile;
		
		crThrd.setReadMsg(true);
		
		crThrd.start();  //start a new tread to execute the external program
		
		//crThrd.run();
		try{
			
			int intWaitFor = intTimeoutSec * 2;    
			
			while(crThrd.isAlive() && (intWaitFor > 0)){        
				intWaitFor--;
				sleep(500);  //sleep this thread for 500ms
			}		
			
			if (crThrd.isAlive()){  //if thread is still alive kill it, with the proccess
				p.destroy();
				intRetVal = 16;  //set a return value not zero               
			}
			else{
				intRetVal = p.exitValue();
			}
			
		}
		catch (InterruptedException ie){
			m_strErrMsg += "InterruptedException Occured : " + ie.getMessage();
		}
		m_strErrMsg += crThrd.m_strErrMsg;
		//m_strMsg    += crThrd.getMsg();
		return intRetVal;
	}
	
}