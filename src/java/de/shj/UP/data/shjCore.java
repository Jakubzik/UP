/**
 * Software-Entwicklung H. Jakubzik, Lizenz Version 1.0 (Uni Heidelberg)
 *
 *
 * Copyright (c) 2004 Heiko Jakubzik.  All rights reserved.
 *
 * http://www.heiko-jakubzik.de/
 * http://www.heiko-jakubzik.de/SignUp/
 * http://www.heiko-jakubzik.de/SignUp/hd/
 * mailto:heiko.jakubzik@t-online.de
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is not permitted.
 *
 * Lizenznehmer der Universitaet Heidelberg erwerben mit der Lizenz
 * das Recht, den Quellcode der Internetanwendung "SignUp" einzusehen,
 * zu veraendern und fuer den Eigenbedarf neu zu kompilieren.
 *
 * Die Weitergabe von Softwarekomponenten des Pakets durch die
 * Lizenznehmerin ist weder als Quelltext noch in kompilierter Form
 * ganz oder teilweise gestattet.
 *
 * Diese Lizenz erstreckt sich auf die Internetanwendung "SignUp",
 * also
 *
 * 1. das Datenschema,
 * 2. die Bibliothek "SignUp.jar"
 * 3. das Webarchiv  "SignUpXP.war"
 * 4. die Resource Bundles und Interfaces,
 * 5. das Access Frontend "SignUpXP",
 * 6. Konvertierungs-Skripte, XML und XSL Dateien,
 *    sowie alle vbs und hta Anwendungen zu Konvertierung
 *    von Excel und XML Dateien in SignUp-kompatibles Format,
 * 7. C# .NET Frontend Komponenten,
 * 8. die Webservice Spezifikation,
 * 9. die Dokumentation inkl. Bilder
 *
 * Das Rechtemanagement zur Neukompilierung unterliegt der
 * Kontrolle der Universtitaet Heidelberg. Fuer Kompilationen,
 * die von der Lizenznehmerin durchgefuehrt wurden, besteht
 * kein Anspruch auf Nachbesserung.
 *
 * Der Lizenzgeber behaelt es sich vor, die Lizenz auf
 * ein "Open Source"-Modell umzustellen.
 *
 * Bei Aenderungen am Datenmodell oder an Objekten _dieses_ Pakets
 * (com.shj.signUp.data) wird _dringend_ Ruecksprache mit dem
 * Autor der Software empfohlen.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF FITNESS FOR A PARTICULAR PURPOSE ARE
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
 * --------------|
 *
 * 1.  constants: error codes									line  170
 *
 * 2.  public declarations										line  230
 *
 * 3.  private/protected declarations							line  260
 *
 * [ 4.  properties												line  272 ]
 *
 * 5.  public methods: db connectivity							line  303
 *
 * 6.  public methods: db utils									line  403
 *
 * 7.  public date/time utils									line  677
 *
 * 8.  String utils												line  829
 *
 * 9.  HTTP utils												line  894
 *
 * 10  XML utils												line 1035
 *
 * 11  Teacher log-in methods									line 1081
 *
 * 12. Uninteresting code (one-liners)							line 1222
 *
 * 13. Constructor												line 1282
 *
 *
 * @todo:
 *  the properties-file is compiled into the class. But the
 *  methods ("properties") for a dynamically loaded class are
 *  not deleted.
 *
 * @version XP 1-00-02 - 5-24-1
 * change date				change author			change description
 * 08-Dec-2003				h. jakubzik				setup
 * 30-Dec-2003				h. jakubzik				login-methods added (level and level + ip; more to come...).
 * 11-Feb-2004			 	h. jakubzik				added coursetype-mapping error codes
 * 11-Mar-2004				h. jakubzik				added constants and methods for ports (client-auth vs. no client auth)
 * 13-Mar-2004				h. jakubzik				version 1 of .hasAccess()
 * 16-Mar-2004				h. jakubzik				version 2 of .hasAccess() (corrected call to .lookUp())
 * 18-Mar-2004				h. jakubzik				added getNextID() [although it feels wrong. It's still probably better than an Autowert. In critical circumstances, a millisec-value should be used instead).
 * 28-Apr-2004				h. jakubzik				added error ERR_CREDIT_MAPPING_TO_COURSE_FAILURE.
 * 04-May-2004				h. jakubzik				added error ERR_EXAM_DELETION_FAILED.
 *																ERR_EXAM_NOT_SAVED.
 * 11-May-2004				h. jakubzik				getHTTPBase(request, PORT). this (deprecated) method did not use
 *													the port that was handed over in the request (but the globally
 *													configured, insecure port). I fear that I did this for a reason. I
 *													forgot the reason, and it now disturbs the studium.iwr.uni-heidelberg
 *													installation (on port 9443 and, for http, 9449). So I changed this.
 *
 * 13-May-2004				h. jakubzik				added error
 *
 * 17-May-2004				h. jakubzik				made xSelect() public (for upload of KvvArchiv).
 * 03-Oct-2004			    h. jakubzik				getShjQueryString (new)
 * 06-Jan-2005				h. jakubzik				getSeminarID, getSessionID.
 * 08-Jan-2005				h. jakubzik				.getShjQueryString() ersetzt jetzt " durch \"
 * 22-Jan-2005				h. jakubzik				ERR_CONFIG_CREDIT_GRADE_MISSING added
 * 14-May-2005				h. jakubzik				ERR_KURS_BLOCKED_BY_OPEN_COMMITMENT added
 * 03-Jun-2005				h. jakubzik				ERR_OVERCOMMITTED added
 * 25-Jul-2005				h. jakubzik
 * 04-Aug-2005				h. jakubzik				added registration errors up to no. 53; 
 * 													adapted getFormData to XHTML (" /%gt;" at the end).
 * 													getNextID can now deal with an empty Crit-String.
 * 16-Aug-2005				h. jakubzik				sqlExeC (.execute changed to .executeUpdate)
 * 14-Sep-2005				h. jakubzik				g_STRING_UNITITIALIZED and g_ID_UNINITIALIZED added. .isUninitialized().
 * 28-Feb-2006				h. jakubzik				added errors 57 through 59.
 * 13-Mar-2006				h. jakubzik				added error ERR_LOGIN_FAILS_ON_URZ
 * 11-Jun-2006				h. jakubzik				added error ERR_PRINTING_CREDITS_EMPTY_SET
 * 8-April-2016                         h. jakubzik                             string2JSON verbietet jetzt Backslashes (stattdessen werden normale Slashes ausgegeben)
 */

package de.shj.UP.data;

import java.io.*;
import java.util.*;
import java.sql.*;
import java.sql.Date;

import javax.sql.DataSource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.servlet.http.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
//import com.sun.org.apache.xpath.internal.XPathAPI; // JAVA 5
import org.w3c.dom.*;
import javax.xml.parsers.*;		// needed for DocumentBuilderFactory
import org.xml.sax.*;			// needed for 'input-source'
import org.apache.commons.beanutils.RowSetDynaClass;

import de.shj.UP.util.ResultSetSHJ;
import de.shj.UP.util.SignUpLocalConnector;

import java.security.cert.*;	// needed for client-auth.

/**
 * shj Standard Klasse f�r PostgreSQL-Datenbanken
 * Spezielle SignUp Anpassungen
 *
 * =========================================================================================================================================================================================================================================
 */
public class shjCore implements Serializable, HttpSessionBindingListener{

	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 1.   C O N S T A N T E N:  F E H L E R C O D E S
	//
	// Die Fehler werden mit den Konstanten ausgel�st und 
	// durch die Ressource-Bundles i18n beschrieben
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------

	private static final long serialVersionUID = 3153961357140549366L;
	public static final byte ERR_SIGNUP_NO_CONNECTION_TO_DB											= -120;
	public static final byte ERR_SIGNUP_EMPTY_SQL													= -119;

	public static final byte ERR_UNKNOWN															=   0;
	public static final byte ERR_REGISTRATION_CURRENTLY_NOT_ALLOWED									=   1;
	public static final byte ERR_BROWSER_PROBLEM													=   2;
	public static final byte ERR_LOGIN_STUDENT_FAILED												=   3;
	public static final byte ERR_LOGIN_STUDENT_FAILED_UMLAUT										=   64;
	public static final byte ERR_INVALID_SESSION													=   4;
	public static final byte ERR_LOGIN_CONFIRMATION_FAILED											=   5;
	public static final byte ERR_STUDENT_REGISTRATION_FAILED_WRONG_MATRIKEL							=   6;
	public static final byte ERR_STUDENT_REGISTRATION_FAILED_FACH_MISSING							=   7;
	public static final byte ERR_STUDENT_REGISTRATION_FAILED_FACHSEMESTER_MISSING					=   8;
	public static final byte ERR_STUDENT_REGISTRATION_FAILED										=  10;
	public static final byte ERR_STUDENT_REGISTRATION_FAILED_ALREADY_REGISTERED						=  11;
	public static final byte ERR_STUDENT_TEST_REGISTRATION_FAILED_DOUBLETTE							=  12;
	public static final byte ERR_STUDENT_TEST_REGISTRATION_FAILED_DOUBLETTE_COMMITMENT				=  58;
	public static final byte ERR_STUDENT_TEST_REGISTRATION_FAILED_DOUBLETTE_CREDIT					=  59;
	public static final byte ERR_STUDENT_REGISTRATION_FAILED_ANREDE_MISSING							=  50;
	public static final byte ERR_STUDENT_REGISTRATION_FAILED_EMAIL_MISSING							=  51;
	public static final byte ERR_STUDENT_REGISTRATION_FAILED_PASSWORD_TOO_SIMPLE					=  52;
	public static final byte ERR_STUDENT_REGISTRATION_FAILED_BIRTHDAY_WRONG							=  53;
	
	public static final byte ERR_LOGIN_STAFF_FAILED													=  13;

	public static final byte ERR_KURS_DATE_ADD_FAILED												=  14;
	public static final byte ERR_KURS_DATE_COLLIDES_WITH_OTHER_KURS									=  15;
	public static final byte ERR_KURS_MISCONFIGURATION_FREETEXT_PLUS_ROOM							=  16;
	public static final byte ERR_KURS_MISCONFIGURATION_FREETEXT										=  17;
	public static final byte ERR_KURS_MISCONFIGURATION_EXTERNAL_ROOM_WITH_INTERNAL_NAME				=  18;
	public static final byte ERR_KURS_MISCONFIGURATION_EXTERNAL_ROOM								=  19;
	public static final byte ERR_KURS_MISCONFIGURATION_ROOM_MISSING									=  20;
	public static final byte ERR_KURS_MISCONFIGURATION_ROOM_AND_TIME_MISFIT							=  21;
	public static final byte ERR_KURS_MISCONFIGURATION_1											=  22;
	public static final byte ERR_KURS_MISCONFIGURATION_2											=  23;
	public static final byte ERR_KURS_ROOM_NOW_OCCUPIED_1											=  24;
	public static final byte ERR_KURS_ROOM_NOW_OCCUPIED_2											=  27;
	public static final byte ERR_KURS_ROOM_MISSING													=  25;
	public static final byte ERR_KURS_ROOM_MISSING_2												=  26;
	public static final byte ERR_KURS_ROOM_INTERNAL_ERROR											=  28;
	public static final byte ERR_KURS_KURSTYP_MISSING												=  29;
	public static final byte ERR_KURS_BLOCKED_BY_OPEN_COMMITMENT									=  48;
	
	public static final byte ERR_LOGIN_SECURITY														=  30;

	public static final byte ERR_DATE_FORMAT_UNREADABLE												=  31;
	public static final byte ERR_DATE_TOO_FAR_IN_FUTURE												=  32;
	public static final byte ERR_DATE_TOO_FAR_IN_PAST												=  33;
	public static final byte ERR_ECTS_CREDIT_FORMAT													=  34;

	public static final byte ERR_CREDIT_ADDITION_FAILED_ON_DB										=  35;
	public static final byte ERR_CREDIT_UPDATE_FAILED_ON_DB											=  36;
	public static final byte ERR_CREDIT_MAPPING_TO_COURSE_FAILURE									=  41;

	public static final byte ERR_MISSING_CREDITS_ON_MOD_EXAM										=  37;
	public static final byte ERR_TRYING_TO_ERASE_LAST_COURSEMAPPING									=  38;

	public static final byte ERR_CONFIG_CREDIT_TEACHER_MISSING										=  39;

	public static final byte ERR_STAFF_ACCESS_DENIED_CL_AUTH										=  40;
	public static final byte ERR_EXAM_DELETION_FAILED												=  42;
	public static final byte ERR_EXAM_NOT_SAVED														=  43;

	public static final byte ERR_STAFF_PASSWORD_MISMATCH											=  44;
	public static final byte ERR_CONFIG_CREDIT_GRADE_MISSING										=  45;
	public static final byte ERR_PASSWORD_TOO_SIMPLE												=  46;
	public static final byte ERR_CREDIT_DELETION_FAILED												=  47;
	
	public static final byte ERR_OVERCOMMITTED														=  49;
	public static final byte ERR_COMMITMENT_FAILED_ON_DB											=  54;
	
	public static final byte ERR_UPLOAD_FILE_TOO_BIG												=  55;
	public static final byte ERR_UPLOAD_FILE_WRONG_TYPE												=  56;
	
	public static final byte ERR_KVVINHALT_DELETE_FAILED_NOT_EMPTY									=  57;
	public static final byte ERR_LOGIN_FAILS_ON_URZ													=  60;
	public static final byte ERR_PRINTING_CREDITS_EMPTY_SET											=  61;
	
	public static final byte ERR_SIGNUP_PERIOD_ILLOGICAL											=  62;
	public static final byte ERR_PROHIBITED_DURING_SIGNUP_PERIOD									=  63;
	public static final byte ERR_CREDIT_MODULE_MAPPING_MISSING										=  65;
	public static final byte ERR_CREDIT_BA_NOT_MAPPED_TO_ANY_MODULE									=  66;
	public static final byte ERR_GS_BA_LATINUM_OR_FREMDSPR_MISSING_FOR_COMMITMENT					=  67;
	public static final byte ERR_AS_SIGNUP_PREREQUISITES_MISSING									=  68;
	public static final byte ERR_LOGIN_FAILS_ON_TOO_MANY_CONNECTIONS								=  69;
	public static final byte ERR_GS_SIGNUP_PREREQUISITES_MISSING									=  70;
	public static final byte ERR_AS_SIGNUP_DOUBLE_PROSEMINAR										=  71;
	// 68 gibts schon
	
	
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 2.   P U B L I C   C O N S T A N T S
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	public static final String PORT_SSL_NO_CLIENT_AUTH	= "9443";
	public static final String PORT_SSL_CLIENT_AUTH		= "8443";
	public static final String PORT_HTTP_INSECURE		= "8080";

	public static final byte   POLICY_NO_RESTRAINT		= 0;
	public static final byte   POLICY_HTTPS				= 1;
	public static final byte   POLICY_HTTPS_IP_IDENT	= 2;
	public static final byte   POLICY_HTTPS_DIGITAL_ID	= 3;
	public static final byte   POLICY_HTTPS_IP_AND_DIGID= 4;
	
	public static final String 	g_STRING_UNINITIALIZED	= "#";
	public static final long	g_ID_UNINITIALIZED		= -4711;
	
	public static final byte g_LOG_LEVEL_SILENT=0;
	public static final byte g_LOG_LEVEL_WARNING=1;
	public static final byte g_LOG_LEVEL_INFO=2;
	public static final byte g_LOG_LEVEL_DEBUG=3;
	
	public byte LOG_LEVEL=1;
	
	public Date g_TODAY; // = new Date(new java.util.Date().getTime());
	public static final SimpleDateFormat g_ISO_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat g_GERMAN_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
	public static final SimpleDateFormat g_BRITISH_DATE_FORMAT = new SimpleDateFormat("d MMM, yyyy", Locale.UK);
	public static final SimpleDateFormat g_TIME_FORMAT = new SimpleDateFormat("HH:mm");
	public static final SimpleDateFormat g_TIMESTAMT_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm");
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 2.   P U B L I C   D E C L A R A T I O N S
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------

	public 		boolean 		isConnected				=		false;
	//public		int				iNoOfOpenConnections

    /**
     * Objektvariable f�r Debugging
     **/
  	public 		String 			strErrMsg				=		"";
	public 		String 			m_strDebug;


	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 3.   PRIVATE / PROTECTED   D E C L A R A T I O N S
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------

  	private 	Connection 	conDB	= null;
  	private		static Context		m_ctx	= null;
  	private		DataSource	m_DB	= null;
  	private		boolean				m_bKeepConnectionOpen=false;

	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 5.   P U B L I C  M E T H O D S: D B-C O N N E C T I V I T Y
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
  	
  	public void log(String sMsg, byte level){
  		if(level<=LOG_LEVEL) System.err.println(sMsg);
  	}
  	
  	/**
  	 * Default is false
  	 * @param b
  	 */
  	public void setKeepConnectionOpen(boolean b){
  		m_bKeepConnectionOpen=b;
  	}
  	
  	/**
  	 * Seit 6-14-03 veraltet (und leer). 
  	 * @deprecated
  	 * @return
  	 */
  	public void connect(){}
	
  	/**
  	 * <pre>
  	 * Der Servlet Container h�lt f�r jsp-Zugriffe
  	 * die Datenbankverbindung vor.
  	 * 
 	 * F�r Zugriffe, die nicht vom Servlet Container
 	 * ausgehen (cron-Skripte und �hnliches), k�nnen 
 	 * Objekte hier explizit mit der Datenquelle 
 	 * verbunden werden.
 	 * 
	 * Zur Verbindung nutzt man am besten den
	 * #com.shj.signUp.util.SignUpLocalConnector.
	 * 
	 * So k�nnen dann Datenbankverbindungen auch 
	 * ohne Servlet-Container genutzt werden:
 	 * private SignUpLocalConnector db = new SignUpLocalConnector();
 	 * StudentXLeistung sx = new StudentXLeistung();
	 * sx.extConnect(db);
	 * sx.init(lSeminarID, sMatrikelnummer, lLeistungID, lLeistungCount);
	 * ...
  	 * </pre>
  	 * @param ds
  	 * @throws Exception 
  	 */
  	public void extConnect(DataSource ds) throws Exception{
  		m_DB=ds;
  		conDB=null;
  	}
  	  	
  	/**
	 * <pre>
	 * Initialisiert die Datenbankverbindung
	 * In SignUp also java:comp/env/jdbc/SignUpXP
	 * </pre>
	 * @version 6-14-03
	 * @throws SQLException 
	 * @return true, wenn die Verbindung erfolgreich neu
	 * hergestellt wurde; falsch, wenn etwas schiefging oder 
	 * die Verbindung bereits bestanden hat.
	 **/
	protected boolean reConnect() throws SQLException, javax.naming.NamingException{
    	if(conDB==null)  {log("reConnect: connection is null.", g_LOG_LEVEL_DEBUG);conDB=m_DB.getConnection();}
    	if(conDB.isClosed()){log("reConnect: connection is closed.", g_LOG_LEVEL_DEBUG);conDB=m_DB.getConnection();}
    	isConnected=(conDB!=null);
    	return isConnected;
  	}//end of "connect"
	
  	/**
  	 * Schlie�t die Verbindung zur Datenbank
  	 * @throws SQLException 
  	 **/
  	public void close() throws SQLException{
  	  if((conDB!=null) && (!conDB.isClosed())) conDB.close();
  	  conDB=null;
  	  isConnected=false;
  	}

  	/**
  	 * Zugriff auf die Datenbankverbindung; ruft
  	 * immer {@link #reConnect()} auf.
  	 * @version 6-14-2 (April 2006)
  	 * @return die Datenbankverbindung
  	 * @throws NamingException 
  	 * @throws SQLException 
  	 * @throws Exception
  	 */
  	private Connection getConnection() throws SQLException, NamingException {
  		reConnect();
  		return conDB;
  	}
  	
  	/**
  	 * <pre>
  	 * Gibt ein ResultSet Objekt mit dem Ergebnis der 
  	 * Abfrage aus, die in sqlQuery sepezifiziert wurde.
  	 * Beispiel
  	 * 
  	 * ResultSet rStudierende = sqlQuery("select * from \"tblBdStudent\");
  	 * while(rStudierende.next()){
  	 *     out.write(rStudierende.getString("strStudentNachname");
  	 *  }
  	 * </pre>
  	 * @returns ResultSet mit dem Ergebnis der spezifizierten Abfrage
  	 * @param strQuery: Select-Abfrage in PostgreSQL-g�ltigem Syntax.
  	 * @throws Exception Verbindung zum Datenbankserver fehlgeschlagen
  	 **/
  	public ResultSet sqlQuery(String strQuery) throws Exception{
  		getConnection().clearWarnings();
  		return getConnection().createStatement().executeQuery( strQuery );
  	}//end of "sqlQuery"

  	public Iterator sqlQuerySafe(String sQuery) throws Exception{
  		Statement stmt=getConnection().createStatement();
  		ResultSet rTmp=stmt.executeQuery(sQuery);
  		RowSetDynaClass rSwap_d = new RowSetDynaClass(rTmp);
  		try{
  			rTmp.close();
  			stmt.close();
  		}catch(Exception eWhateverIgnore){}
  		return rSwap_d.getRows().iterator();
  	}
  	
  	public ResultSetSHJ sqlQuerySHJ(String sQuery) throws Exception{
  		Statement stmt=getConnection().createStatement();
  		ResultSet rTmp=stmt.executeQuery(sQuery);
  		ResultSetSHJ oTmp = new ResultSetSHJ(rTmp);
  		try{
  			rTmp.close();
  			stmt.close();
  		}catch(Exception eWhateverIgnore){}
  		return oTmp;
  	}
  	
	/**
	 * <pre>
	 * Executes a stored procedure that returns no results.
	 * 
	 * The syntax for calling this method is:
	 * <Code>.execProc("[StoredProcName]", "ParamType1", "ParamValue1", "ParamType2", "ParamValue2", ..., "ParamTypeN", "ParamValueN")</Code>, where 
	 * 
	 * - "name" is the name of the stored procedure,
	 * - "ParamType[i]" is either "int", "long", "String", or "boolean" and 
	 * - "ParamValue[i]" the value of this parameter as a String.
	 * 
	 * For example you can call the stored procedure
	 *    <Code>FUNCTION setStatus(lFeeUsageUniqueID bigint, iStatus integer, sInfo varchar, sUserID varchar)</Code>
	 *    
	 * in the following way:
	 * 
	 * <Code>.execProc("setStatus", "long", "1045", "int", "23", "String", "Neuer Status gesetzt", "String", "g96");</Code>
	 * 
	 * Fehlgeschlagene Typenkonvertierungen (<Code>"long", "23pp"</Code>) oder unbekannte Typen (<Code>"Time", "10:22"</Code>) führen zu Fehlern.
	 * </pre>
	 * @param sCommandInfo parameter list
	 * @throws SQLException
	 * @throws NamingException
	 * @throws ParseException 
	 */
	public void execProc(String...sCommandInfo) throws SQLException, NamingException, ParseException{
		int ij=1;
		
		CallableStatement cstm = getConnection().prepareCall("{call " + sCommandInfo[0] + "(" + getArgList((sCommandInfo.length-1)/2) + ")}");
		for(int ii=1;ii<sCommandInfo.length;ii++){
			if(sCommandInfo[ii].equals("int"))
				cstm.setInt(ij++, Integer.parseInt(sCommandInfo[++ii]));
			else if(sCommandInfo[ii].equals("long"))
				cstm.setLong(ij++, Long.parseLong(sCommandInfo[++ii]));
			else if(sCommandInfo[ii].equals("String"))
				cstm.setString(ij++, sCommandInfo[++ii]);
			else if(sCommandInfo[ii].equals("boolean"))
				cstm.setBoolean(ij++, Boolean.parseBoolean(sCommandInfo[++ii]));
			else if(sCommandInfo[ii].equals("Date")){
				cstm.setDate(ij++, new java.sql.Date(g_ISO_DATE_FORMAT.parse(sCommandInfo[++ii]).getTime()));
				System.err.println("Datum gesetzt auf: " + sCommandInfo[ii]);}
			else
				throw new SQLException("Der Typ '" + sCommandInfo[ii] + "' wird von der Methode 'execProc' nicht unterstützt.");
		}
		cstm.execute();
	}
	
	/**
	 * Example:
	 * <Code>getArgList(4)</Code> will return: "?,?,?,?"
	 * @param length
	 * @return String consisting of "?," exactly <Code>length</Code> times.
	 */
	private String getArgList(int length) {
		String sReturn = "";
		for(int ii=0;ii<length;ii++)
			sReturn += "?,";
		return sReturn.substring(0, sReturn.length()-1);
	}
  	
  	/**
  	 * <pre>
  	 * Bei komplexen, mehrstufigen Abfragen schl�gt {@link #sqlExe(String)} 
  	 * m�glicherweise fehl (die Abfrage ist zu lang).
  	 * In diesem Fall hilft {@link #sqlExeSingle(String)} weiter: ansonsten 
  	 * mit {@link #sqlExe(String)} funktionsgleich, werden die 
  	 * Abfrageabschnitte zwischen Semikolons nacheinander (aber 
  	 * innerhalb einer Transaktion) ausgef�hrt. 
  	 * </pre>
  	 * @param strQuery
  	 * @return true bei Erfolg
  	 * @throws Exception wenn {@link #setAutoCommit(boolean)} nicht auf wahr gesetzt 
  	 * werden kann.
  	 */
  	public boolean sqlExeSingle(String strQuery) throws Exception{
  		
  		boolean blnReturn = true;
  		int intPos=0;

  		try {
  			// start transaction:
  			//log("sqlExeSingle: Turning auto-commit off.", g_LOG_LEVEL_DEBUG);
  			log("sqlExeSingle: Transaktionen sind generell abgeschaltet! Hier: " + strQuery + ".", g_LOG_LEVEL_WARNING);
			//this.getConnection().setAutoCommit(false);
			
			String strBatch = strQuery;
			String strCmd   = "";
			intPos = strBatch.indexOf(';');
			
			// Loop through strBatch:
			while ( (intPos>=0) && blnReturn){
				strCmd = strBatch.substring(0, intPos);
				blnReturn = (blnReturn && sqlExe(strCmd + ";"));
				strBatch = strBatch.substring(intPos+1);
				intPos = strBatch.indexOf(';');
			}
			
			//if(blnReturn) {log("sqlExeSingle: committing transaction.", g_LOG_LEVEL_DEBUG);getConnection().commit();}
			//else		  {log("sqlExeSingle: ROLLING BACK (" + strQuery + ")", g_LOG_LEVEL_WARNING);getConnection().rollback();}
		} catch (SQLException e) {
			blnReturn = false;
		} catch (Exception e) {
			blnReturn = false;
		}
		//log("sqlExeSingle: switching auto-commit back on.", g_LOG_LEVEL_DEBUG);
		//this.getConnection().setAutoCommit(true);
		
		// Würgt möglicherweise laufende Queries ab
		// (seit dem Umstieg auf Tomcat 5.5...)
		try{
			  if(!m_bKeepConnectionOpen) conDB.close();
		}catch(Exception eClo){}
		return blnReturn;
  	}
  	
	/**
	 * F�hrt SQL-Befehl aus. Beispiel:
	 * <pre>
	 * if true=sqlExe("update \"tblBdStudent\" set \"blnStudentFemale\"='t';"){
	 * 	out.write("Alle Studierenden sind jetzt weiblich");
	 * }
	 * </pre>
	 * F�r komplexere oder l�ngere Befehle siehe: {@link #sqlExeSingle(String)}
	 * F�r Informationen �ber die Anzahl der betroffenen Datens�tze 
	 * siehe {@link #sqlExeCount(String)}
	 * @param strQuery SQL Abfrage ohne Ergebnisse (auch DDL).
	 * @throws Exception 
	 * @return wahr, wenn der Befehl ausgef�hrt werden konnte.
	 * false if an error occurred
	 **/
	public boolean sqlExe(String strQuery) throws Exception{
		return (sqlExeCount(strQuery) != -1);
	}//end of "sqlExe"

	/**
	 * F�hrt SQL-Befehl aus und liefert Anzahl der betroffenen 
	 * Datens�tze zur�ck. Beispiel:
	 * <pre>
	 * out.write("Anzahl Studierende: " + sqlExe("delete from \"tblBdStudent\";"));
	 * </pre>
	 * Siehe auch {@link #sqlExe(String)} und {@link #sqlExeSingle(String)}
	 * @param strQuery SQL-Abfrage ohne Ergebnisse.
	 * @since 6-08-00
	 * @throws Exception 
	 * @return Anzahl betroffener Datens�tze, 0 for DDL Abfragen, or -1 bei SQL Fehler.
	 **/
	public int sqlExeCount(String strQuery) throws Exception{

		int intReturn=-1;

		//check if query not empty:
		if((strQuery==null)||(strQuery.equals(""))) throw new Exception(String.valueOf(ERR_SIGNUP_EMPTY_SQL));

		try{
			Statement stm = getConnection().createStatement();
			intReturn = stm.executeUpdate(strQuery);
			log(intReturn + " Datensätze betroffen von " + strQuery, g_LOG_LEVEL_DEBUG);
			stm.close();
		}catch(Exception e3){}
		
		// April 11 2006
		try{
			if(!m_bKeepConnectionOpen) conDB.close(); 
		}catch(Exception eClo){}
		return intReturn;
	}//end of "sqlExe"	
	
	/**
	 * Transaktion ausf�hren.
	 * @throws SQLException DB error
	 **/
	public void commit() throws SQLException{
		if (conDB!=null) conDB.commit();
  	}//end of "commit"

  	/**
  	 * Transaktion nicht ausf�hren und aus Cache l�schen.
  	 * @throws SQLException
  	 **/
  	public void rollback() throws SQLException{
  	  if (conDB!=null) conDB.rollback();
  	}//end of "rollback"


	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 6.   P U B L I C  M E T H O D S: D B-U T I L S
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------

	/**
	 * Veraltet, noch f�r Versionen ohne Parameterabfragen
	 * gedacht zur Vermeidung von SQL-Injection.
	 * 
	 * Die Methode ersetzt einfache Anf�hrungszeichen (') 
	 * durch zwei einfache Anf�hrungszeichen ('' -- nicht ").
	 * 
	 * Bei jedem Aufruf einer aus Nutzereingaben basierten
	 * Abfrage sollte jeder String oder Date Wert 
	 * durch diese Methode gefiltert werden:
	 * <pre>
	 * s.sqlExe("update tblBdStudent set Name='" + getDBCleanString("M�ller")) + "';");
	 * </pre>
	 * s.sqlExe("update tblBdStudent set Name=" + getDBCleanString("M�ller'; delete from tblBdStudent;")) + ";");
	 * @return String ohne einfache Anf�hrungszeichen
	 * @param str_IN 
	 **/
	public String getDBCleanString(String str_IN){
	  return shjSubstitute(str_IN, "\'", "\'\'");
  	}//end of getDBCleanString

  	/**
  	 * 't' f�r true, 'f' f�r false; anpassbar f�r andere DBMS.
  	 * @return String 
  	 * @param bln_IN 
  	 **/
  	public String getDBBoolRepresentation(boolean bln_IN){
	  return ((bln_IN) ? "'t'" : "'f'");
  	}

	/**
	 * Schl�ge eine Spalte in der Datenbank nach.
	 * Beispie:<pre>  
  	 * String sName = lookUp("strName", "tblStudent", "\"strMatrikelnummer\"='1234567'")
  	 * </pre>
  	 * Achtung: in dieser PostgreSQL-spezifischen Variante werden 
  	 * Spalte und Tabelle jeweils in Anf�hrungszeichen verpackt.
  	 * 
	 * Im Fehlerfall beginnt das Ergebnis der Abfrage mit einer Raute "#"; 
	 * folgende Fehlermeldungen sind vorgegeben: "#NO_RESULT" (unter den 
	 * gegebenen Bedingungen gibt es kein Ergebnis), und "#Error: Couldn't execute query:" plus
	 * der Fehlermeldung der DBMS. 
	 * @return String mit dem Wert der Spalte.
	 *  @param strField_IN: Name der Spalte, die nachgeschlagen werden soll.
	 *  @param strTable_IN: Name der Tabelle
	 *  @param strCrit_IN: Kriterium.
	 **/
	public String lookUp(String strField_IN, String strTable_IN, String strCrit_IN){

		String 	strReturn	= "";
		String 	strSQL		= "";
		ResultSet rst;
		
		strSQL = "SELECT \"" + strField_IN + "\" FROM \"" + strTable_IN +
		"\" WHERE (" + strCrit_IN + ");";
		try{
			
			rst=sqlQuery(strSQL);
			if(rst.next()){
				strReturn = rst.getString(strField_IN);
			}else{
				strReturn = "#NO_RESULT";
			}
			
			rst.close();
		}catch(Exception eLookUp){
			strReturn = "#Error: Couldn't execute query: " + eLookUp.toString();
		}
		
		try {
			if(!m_bKeepConnectionOpen) conDB.close();
		} catch (Exception e) {}
		return strReturn;
	}
	
	/**
	 * Schl�ge eine Spalte in der Datenbank nach.
	 * Beispie:<pre>  
  	 * String sName = lookUp("strName", "tblStudent", "\"strMatrikelnummer\"='1234567'")
  	 * </pre>
  	 * Achtung: in dieser PostgreSQL-spezifischen Variante werden 
  	 * Spalte und Tabelle jeweils in Anf�hrungszeichen verpackt.
  	 * 
	 * Im Fehlerfall beginnt das Ergebnis der Abfrage mit einer Raute "#"; 
	 * folgende Fehlermeldungen sind vorgegeben: "#NO_RESULT" (unter den 
	 * gegebenen Bedingungen gibt es kein Ergebnis), und "#Error: Couldn't execute query:" plus
	 * der Fehlermeldung der DBMS. 
	 * @return String mit dem Wert der Spalte.
	 *  @param strField_IN: Name der Spalte, die nachgeschlagen werden soll.
	 *  @param strTable_IN: Name der Tabelle
	 *  @param strCrit_IN: Kriterium.
	 **/
	public String lookUpSHJ(String strField_IN, String strTable_IN, String strCrit_IN){

		String 	strReturn	= "";
		String 	strSQL		= "";
		ResultSetSHJ rst;
		
		strSQL = "SELECT \"" + strField_IN + "\" FROM \"" + strTable_IN +
		"\" WHERE (" + strCrit_IN + ");";
		try{
			
			rst=sqlQuerySHJ(strSQL);
			if(rst.next()){
				strReturn = rst.getString(strField_IN);
			}else{
				strReturn = "#NO_RESULT";
			}
		}catch(Exception eLookUp){
			strReturn = "#Error: Couldn't execute query: " + eLookUp.toString();
		}
		return strReturn;
	}

	/**
	 * <pre>
	 *  Look up maximum value.
	 *  Currently, for Postgres, the constructed SQL looks like this:
	 *  
	 *  <Code>SELECT max("strField_IN") FROM "strTable_IN" [WHERE strCrit_IN];</Code>.
	 *  
	 *  So the field and table names are wrapped up in quotes, 
	 *  while fields in the 'strCrit_IN'-clause 
	 *  have to be wrapped in quotes before calling this method.
	 *  
	 *  In case of error, "" is returned
	 *  An example call would look like this:
	 *  
	 *  <Code>String sMaxMatrikel = shjCore.dbMax("strMatrikelnummer", "tblBdStudent", "\"strStudentNachname\"='M?ller'")</Code>
	 *  </pre>
	 *  @return Maximum value.
	 *  @param strField_IN: column-name whose content to return.
	 *  @param strTable_IN: name of table that holds the column.
	 *  @param strCrit_IN: Optional. Where-clause to specify row(s) in table. Here, with postgres, field-names must be put in quotes.
	 **/ 	
	public String dbMax(String strField_IN, String strTable_IN, String strCrit_IN){
		String 	strSQL		= "";
		ResultSet rst;
		String	strReturn	= "";
				
		strSQL = "SELECT max(\"" + strField_IN + "\") as maximum FROM \"" + strTable_IN + "\"";
		if(!strCrit_IN.equals("")) strSQL += " WHERE (" + strCrit_IN + ")";
		strSQL+=";";

		try{
			rst=sqlQuery(strSQL);
			if(rst.next()){
				strReturn = rst.getString("maximum");
			}else{
				strReturn = "";
			}
			
			rst.close();
		}catch(Exception eLookUp){
			return strReturn;
		}
		try {
			if(!m_bKeepConnectionOpen) conDB.close();
		} catch (Exception e) {}
		return strReturn;
		
	}
	
	/**
	 * <pre>
	 *  Count records.
	 *  
	 *  Currently, for Postgres, the constructed SQL looks like this:
	 *  
	 *  <Code>SELECT count("strField_IN") FROM "strTable_IN" WHERE strCrit_IN.</Code>
	 *  
	 *  So the field and table names are wrapped up in quotes, 
	 *  while fields in the 'strCrit_IN'-clause
	 *  have to be wrapped in quotes before calling this method.
	 *  
	 *  In case of error, -3 is returned
	 *  
	 *  An example call would look like this:
	 *  
	 *  <Code>long lngCount = shjCore.dbCount("strMatrikelnummer", "tblBdStudent", "\"strStudentNachname\"='M?ller'");</Code>
	 *  
	 *  @return Count.
	 *  @param strField_IN: column-name whose content to return.
	 *  @param strTable_IN: name of table that holds the column.
	 *  @param strCrit_IN: where-clajdbc2 pool open two ResultSets simultaneouslyuse to specify row(s) in table. Here, with postgres, field-names must be put in quotes.
	 **/
	public long dbCount(String strField_IN, String strTable_IN, String strCrit_IN){

	  long 		lngReturn = -3;
	  String 	strSQL		= "";
	  ResultSet rst;

	  strSQL = "SELECT count(\"" + strField_IN + "\") as count FROM \"" + strTable_IN +
	  "\" WHERE (" + strCrit_IN + ");";
	  try{
		  rst=sqlQuery(strSQL);
		  if(rst.next()){
			  lngReturn = rst.getLong("count");
		  }else{
			  lngReturn = 0;
		  }
		  
		  rst.close();
	  }catch(Exception eLookUp){
		  return lngReturn;
	  }
	  try {
		  if(!m_bKeepConnectionOpen) conDB.close();
	  } catch (Exception e) {}
	  return lngReturn;
	}
	
	protected void finalize(){
		try {
			this.close();
		} catch (SQLException e) {}
		try {
			super.finalize();
		} catch (Throwable e1) {}
	}
	
	/**
	 * <pre>
	 * Generic ID-Broker for Database.
	 * 
	 * Currently, for Postgres, the constructed SQL looks like this:
	 * 
	 * <Code>SELECT max("strField_IN")+1 FROM "strTable_IN" WHERE strCrit_IN.</Code>
	 * 
	 * So the field and table names are wrapped up in quotes, 
	 * while fields in the 'strCrit_IN'-clause
	 * have to be wrapped in quotes before calling this method.
	 * 
	 * In case of error, Exceptions are thrown (in contrast to 'lookUp(),' 
	 * which works similarly, but uses return-type String to hand back 
	 * error information.
	 * 
	 * An example call would look like this:
	 * <Code>long lngNext = shjCore.getNextID("lngDozentID", "tblSdDozent", "\"lngSdSeminarID\"=2")</Code>
	 *  @return long value next id given the sql-criterion.
	 *  If the db-connection cannot be established, an Exception is thrown. If query-execution
	 *  @param strField_IN: column-name whose content to return.
	 *  @param strTable_IN: name of table that holds the column.
	 *  @param strCrit_IN: where-clause to specify row(s) in table. Here, with postgres, 
	 * field-names must be put in quotes. If left empty, no criteria are regarded.
	 **/
	public long getNextID(String strField_IN, String strTable_IN, String strCrit_IN) throws Exception{

	  long 		lngReturn	= 0;
	  String 	strSQL		= "";
	  ResultSet rst;
	  
	  if(normalize(strCrit_IN).equals(""))
		strSQL = "SELECT max(\"" + strField_IN + "\")+1 FROM \"" + strTable_IN + "\";";
	  else
	  	strSQL = "SELECT max(\"" + strField_IN + "\")+1 FROM \"" + strTable_IN +
			   "\" WHERE (" + strCrit_IN + ");";

		rst=sqlQuery(strSQL);

		if(rst.next()){
			lngReturn = rst.getLong(1);
		}else{
			throw new Exception ("#NO_RESULT");
		}

		
		try {
			rst.close();
			if(!m_bKeepConnectionOpen) conDB.close();
		} catch (Exception e) {}
	  return lngReturn;
	}

	/**
	 * <pre>
	 * Builds a where-clause from form-data handed over.
	 * 
	 * This is handy for Html-based search-forms: 
	 * a number of <input name=[colname] /> fields
	 * are posted to a jsp-page that calls <Code>shjCore.getWhereClause( request )</Code>
	 * and so gets the db-records that the user requested in his search-form.
	 * 
	 * If the parameter-values contain a '%'-sign, the = operator 
	 * is replaced with a 'LIKE.'
	 * 
	 * The String is built so that it transfers the parameter 
	 * name-value pairs to SQL.
	 * 
	 * For postgres, the parameter (or, column-) names are put into quotes.
	 * 
	 * All parameter names that start with 'tbl' or 'idx' are disregarded.
	 * 
	 * If there are no parameters, or no parameters whose value-length is greater 0,
	 * -- in short, if the where-clause makes no sense --, the String "#NoWhere" is returned.
	 * 
	 * Quotation marks are inserted according to shj-column-naming standards.
	 * 
	 * <b>Note</b> that in some contexts it is a disadvantage 
	 * to have to put column names of the 
	 * real database into a publicly accessible Html-page.
	 * 
	 * <b>Note</b> this method is currently unused in SignUp 6-14
	 * </pre>
	 * @return SQL-String, constructed as ("parname"=?value? AND ... AND "parname"=?value?), where
	 * parname is the name of a request-parameter that does not start with 'idx' or 'tbl.' Value
	 * is this parameter's value (SQL injection is avoided). "?" stands for a semi-quote for character-based
	 * column-types, an empty String otherwise.
	 **/
	public String getWhereClause(HttpServletRequest r){

	  Enumeration 	pn			=			r.getParameterNames();
	  String 		strWhere	=			"";
	  String 		strCompare	=			"";

	  for(;pn.hasMoreElements();){

		  String 	parName		= (String) 	pn.nextElement();
		  String 	parValue	=			r.getParameter(parName);

		  if( parValue.indexOf("%") > 0 ){
		  	strCompare=" LIKE ";
		  }else{
		  	strCompare="=";
		  }

		  if( parValue.length() > 0 )
		    if((! (parName.startsWith("tbl")) ) && (! (parName.startsWith("idx")) ))
          		strWhere += "(\"" + parName + "\"" + strCompare + getQuoteOpt(parName) + getDBCleanString(parValue) + getQuoteOpt(parName) + ") AND ";
	  }

	  if(strWhere.length() > 5){
		  strWhere=strWhere.substring(0,strWhere.length() - 5);
	  }else{
		  strWhere="#NoWhere";
  	  }

  	  return strWhere;
  	}

	/** 
	 * Recreates the full URL that originally got the web client to the given 
	 * request.  This takes into account changes to the request due to request 
	 * dispatching.
	 *
	 * <p>Note that if the protocol is HTTP and the port number is 80 or if the
	 * protocol is HTTPS and the port number is 443, then the port number is not 
	 * added to the return string as a convenience.</p>
	 */
	public final static String getReturnURL(HttpServletRequest request)
	{
	    if (request == null)   {
	        throw new IllegalArgumentException("Cannot take null parameters.");
	    }
	    
	    String scheme = request.getScheme();
	    String serverName = request.getServerName();
	    int serverPort = request.getServerPort();
	    
	    //try to get the forwarder value first, only if it's empty fall back to the
	    // current value
	    String requestUri =
	    	(String)request.getAttribute("javax.servlet.forward.request_uri");
	    	requestUri = (requestUri == null) ? request.getRequestURI() : requestUri;
	 
	    //try to get the forwarder value first, only if it's empty fall back to the
	    // current value 
	    String queryString =
	    	(String)request.getAttribute("javax.servlet.forward.query_string");
	    	queryString = (queryString == null) ? request.getQueryString() : queryString;

	    StringBuffer buffer = new StringBuffer();
	    buffer.append(scheme);
	    buffer.append("://");
	    buffer.append(serverName);
	    
	    //if not http:80 or https:443, then add the port number
	    if(
	        !(scheme.equalsIgnoreCase("http") && serverPort == 80) &&
	        !(scheme.equalsIgnoreCase("https") && serverPort == 443)
	    )
	    {
	        buffer.append(":"+String.valueOf(serverPort));
	    }
	    
	    buffer.append(requestUri);
	    
	    if (queryString != null)
	    {
	        buffer.append("?");
	        buffer.append(queryString);
	    }
	    
	    return buffer.toString();
	}
        
        public String getParameterDebug(HttpServletRequest r){
            Enumeration lstParameters = r.getParameterNames();
            String sReturn=""; String sName="";
            //build insert-into SQL-String: iterate all parameters
            for (; lstParameters.hasMoreElements();) {
                sName = (String) lstParameters.nextElement();
                sReturn += sName + ": " + r.getParameter(sName) + "\n";
            }
            return sReturn;
        }
	
	/**
	 * <pre>
	 * Executes 'insert into table X ( [COLS] ) VALUES ( [VALS] );' from request.
	 * If a parameter starts with 'tbl,' then it's interpreted as the table-name.
	 * If a parameter starts with 'idx,' the method probably fails.
	 * </pre>
	 * @deprecated
	 * @param r: request containing name/value pairs.
	 * @return 'true' if SQL was executed successfully, false otherwise.
	 **/
	public boolean addRow(HttpServletRequest r) throws Exception{

		boolean 		blnReturn			=	false;
		String 			strSQLValues		=	"";
		String 			strSQLTemp			=	"";
		String 			strName				=	"";
		String 			strTableName		=	"";
		Enumeration 	lstParameters		=	r.getParameterNames();

		//build insert-into SQL-String: iterate all parameters
		for(;lstParameters.hasMoreElements();){

		  strName = ( String ) lstParameters.nextElement();

		  if(strName.startsWith("tbl")){
			  strTableName="\"" + r.getParameter(strName) + "\"";
		  }else{
			if((!r.getParameter(strName).equals(""))&&(!r.getParameter(strName).equals("null"))){
				  strSQLTemp+= "\"" + strName + "\", ";
				  strSQLValues += getQuoteOpt(strName) + getDBCleanString(r.getParameter(strName)) + getQuoteOpt(strName) + ", ";
			}
		  }
		}//end of for-loop

		//get rid of last commas:
		strSQLValues = 	strSQLValues.substring(0, strSQLValues.length()-2) + " ";
		strSQLTemp = 	strSQLTemp.substring(0, strSQLTemp.length()-2);

		try{
		  blnReturn	=	sqlExe("INSERT INTO " + strTableName + " ( " + strSQLTemp + " ) VALUES ( " + strSQLValues  + " );");
		}catch(Exception e){
		  blnReturn	=	false;
		}

		return blnReturn;
	}//end of boolean processUpdate()

  	/**
  	 * Close connection to database
  	 * @throws SQLException Something goes wrong ... :-)
  	 **/
  	public void disconnect() throws SQLException{
  	  if((conDB!=null) && (!conDB.isClosed())) conDB.close();
  	  conDB=null;
  	  isConnected=false;
  	}//end of "close"
  	
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 7.   P U B L I C   D A T E / T I M E  U T I L S
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------

	/**
	 * <pre>
	 * Legacy.
	 * 
	 * Checks if GermanDate_IN IS a valid date and 
	 * if it is in the future.
	 * </pre>
	 * @param String strGermanDate_IN: Format 'dd.MM.yyyy'.
	 * @return 'true', if date is future date.
	 * @throws Exception when something bad goes wrong :-)
	 */
	public boolean isFutureDate(String strGermanDate_IN) throws Exception{

		boolean 		blnExit	=		false;
		java.util.Date 	dtmNow	=		null;
		java.util.Date 	dtmIn	=		null;
		dtmNow					= new 	java.util.Date();

		if(strGermanDate_IN!=null){

			SimpleDateFormat sdfGerman = new SimpleDateFormat ("dd.MM.yyyy");
			try{
				dtmIn = sdfGerman.parse( strGermanDate_IN );
			}catch(Exception e){
				blnExit=true;
			}
		}

		if(!blnExit) return dtmNow.before(dtmIn);
		// otherwise:
		return false;
	}

	/**
	 * <pre>
	 * Legacy
	 * 
	 * Date-conversion from German to Postgres-understandable (=ISO).
	 * If the date handed over cannot be converted, an empty String is returned.
	 * </pre>
	 * @param String strGermanDate_IN: Format 'dd.MM.yyyy'.
	 * @return iso-Date as String ('yyyy-MM-dd').
	 **/
	public String shjGetPgDate(String strGermanDate_IN){
		String 	strReturn	= "null";
		try{
			if(strGermanDate_IN!=null){

				SimpleDateFormat sdfGerman=new SimpleDateFormat ("dd.MM.yyyy");
				SimpleDateFormat sdfIso=new SimpleDateFormat ("yyyy-MM-dd");
				strReturn = (String)sdfIso.format(sdfGerman.parse(strGermanDate_IN));
			}
		}catch(Exception eshjGetPgDate){strReturn="";}

		return strReturn;
	}

	/**
	 * <pre>
	 * Legacy.
	 * 
	 * Date-conversion from ISO to German.
	 * If the date handed over cannot be converted, an empty String is returned.
	 * </pre>
	 * @deprecated
	 * @param String strIsoDate_IN ('yyyy-MM-dd')
	 * @return German Date ('dd.MM.yyyy') as String.
	 * @throws Exception when String castings impossible.
	 **/
	public String shjGetGermanDateOld(String strIsoDate_IN){
		String strReturn="";
		try{
			if(strIsoDate_IN!=null){

				SimpleDateFormat sdfGerman=new SimpleDateFormat ("dd.MM.yyyy");
				SimpleDateFormat sdfIso=new SimpleDateFormat ("yyyy-MM-dd");
				strReturn = (String)sdfGerman.format(sdfIso.parse(strIsoDate_IN));
			}
		}catch(Exception eshjGetGermanDate){strReturn="";}

		return strReturn;
	}
	
	/**
	 * @param d
	 * @return Deutsch formatiertes Datum
	 */
	public static String shjGetGermanDate(java.sql.Date d){
		return g_GERMAN_DATE_FORMAT.format(d);
	}


	/**
	 * Date-conversion from ISO to Localized.
	 * @deprecated
	 * @param strIsoDate_IN ('yyyy-MM-dd')
	 * @param loc Locale to which to turn the Date-format
	 * @return Localized Date as String (java.text.DateFormat.MEDIUM).
	 * @throws Exception when String castings impossible.
	 **/
	public String shjGetLocalDateOld(String strIsoDate_IN, Locale loc) throws Exception{
		String strReturn="unknown";
		if(strIsoDate_IN!=null){

			java.text.DateFormat sdfGerman=java.text.DateFormat.getDateInstance(java.text.DateFormat.MEDIUM, loc);
			SimpleDateFormat sdfIso=new SimpleDateFormat ("yyyy-MM-dd");
			strReturn = (String)sdfGerman.format(sdfIso.parse(strIsoDate_IN));
		}

		return strReturn;
	}
	
	public String shjGetLocalDate(Date dDate, Locale loc) throws Exception{
		String strReturn="unknown";
		if(dDate!=null){
			java.text.DateFormat sdfLocal=java.text.DateFormat.getDateInstance(java.text.DateFormat.MEDIUM, loc);
			strReturn = (String)sdfLocal.format(dDate);
		}

		return strReturn;
	}

	/**
	 * <pre>
	 * Checks if 
	 * 
	 * (1) <Code>strTime1</Code> and <Code>strTime2</Code> are time representations ('hh:mm')
	 * (2) <Code>strTime1</Code> is before <Code>strTime2</Code>.
	 * </pre>
	 * @param String strTime1_IN (in hh:mm format)
	 * @param String strTime2_IN (in hh:mm format)
	 * @return 'true', if conditions (1) and (2) are met.
	 * @throws Exception if one of the two time-Strings cannot be parsed.
	 **/
	public boolean isTimeSequence(String strTime1_IN, String strTime2_IN) throws Exception{

		boolean 			blnReturn	= 		false;
		java.util.Date 		dtm1		= 		null;
		java.util.Date 		dtm2		= 		null;
		SimpleDateFormat 	sdf			=new 	SimpleDateFormat ("HH:mm");

		try{
			dtm1=sdf.parse(strTime1_IN);
			dtm2=sdf.parse(strTime2_IN);
			blnReturn = dtm1.before(dtm2);
		}catch(Exception eDate){blnReturn=false;}

		return blnReturn;
	}

	/**
	 * <pre>
	 * Checks if 
	 * 
	 * (1) <Code>strDate1</Code> and <Code>strDate2</Code> are 
	 *     date representations ('dd.MM.yyyy'),
	 * (2) <Code>strDate1</Code> is before strDate2.
	 * </pre>
	 * @param String strDate1 (interpreted as 'dd.MM.yyyy')
	 * @param String strDate2 (interpreted as 'dd.MM.yyyy')
	 * @return 'true', if conditions (1) and (2) are met.
	 * @throws Exception if one of the dates cannot be parsed.
	 **/
	public boolean isDateSequence(String strDate1_IN, String strDate2_IN) throws Exception{

		boolean 			blnReturn		=			false;
		java.util.Date 		dtm1			=			null;
		java.util.Date 		dtm2			=			null;
		SimpleDateFormat 	sdf				=new 		SimpleDateFormat ( "dd.MM.yyyy" );

		try{
			dtm1 = sdf.parse(strDate1_IN);
			dtm2 = sdf.parse(strDate2_IN);
			blnReturn = dtm1.before(dtm2);
		}catch(Exception eDate){blnReturn=false;}

		return blnReturn;
	}


	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 8.   S T R I N G  U T I L S
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	
	/**
	 * <pre>
	 * Utility for SQL queries (NOT updates): 
	 * turn all characters other than 
	 * <Code>abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ 123456789</Code>
	 * into SQL wildcards.
	 * 
	 * Example usage:
	 * <Code>String sWhereCondition = shjCore.getEncodingNeutralSQL("M?ller");</Code>
	 * Where then <Code>sWhereCondition = "M.*.ller"</Code>
	 * 
	 * This is currently used for UnivIS communication here and there.
	 * </pre>
	 * @param str_IN
	 * @return all encoding-critical characters (&aacute;&uuml; etc.) are replaced with *.*
	 */
	public String getEncodingNeutralSQL(String str_IN){
		 String strReturn = str_IN;
		 String strAllowed = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ 123456789";
		 for(int ii=0;ii<strReturn.length();ii++){
		   if( (strAllowed.indexOf(strReturn.charAt(ii) ) < 0) ){
		     strReturn = strReturn.substring(0, ii) + ".*." + strReturn.substring(ii+1);
		     ii += 2;
		   }
		 }
		 return strReturn;
	}
	
	/**
	 * <pre>
	 * Returns the String that is handed 
	 * in, or an empty String, if <Code>null</Code> 
	 * is handed in.
	 * 
	 * Utility to avoid a <Code>NullPointerException</Code>
	 * </pre>
	 * @return Empty String if str_IN==null, or return the String itself otherwise.
	 * @param str_IN: String to be normalized.
	 **/
	public static String normalize(String str_IN){
	  return (str_IN==null) ? "" : str_IN;
	}
	
        /**
         * Überarbeitung April 2016: Backslashes werden aussortiert.
         * @param s
         * @return 
         */
	public static String string2JSON(String s){
		return (s==null) ? "" : s.replace('\n', ' ').replace('\r', ' ').replace('\t', ' ').replace('\\','/').replaceAll("\"", "\\\\\""); 
	}

	/**
	 * <pre>
	 * Legacy
	 * 
	 * Substitute or Replace this for that in String str_IN.
	 * </pre>
	 * @return String with substitutions in effect. Returns empty String ('') for a null-input.
	 * @param str_IN String that may contain character-sequences to be substituted
	 * @param strThis_IN String to look for.
	 * @param strForThat_IN String to substitute for 'strThis_IN' wherever it occurs.
	 **/
	public String shjSubstitute(String str_IN, String strThis_IN, String strForThat_IN){

		// interface: do nothing if there's nothing to be done.
		if ( str_IN == null ) return "";
		if ( str_IN.indexOf( strThis_IN, 0 ) < 0 ) return str_IN;

		// declarations
		String 		strReturn		=		str_IN;

		int 		intPos			=		0;
		int 		intLength 		= 		Math.max(strThis_IN.length(), strForThat_IN.length());
		boolean 	blnStart		=		true;

		// loop until last character was substituted.
		while(intPos>=0){
			if(blnStart){
				// initializations
				intPos 				= 		strReturn.indexOf(strThis_IN, 0);
				blnStart			=		false;
			}else{
				intPos 				= 		strReturn.indexOf(strThis_IN, intPos + intLength);
			}

			if(intPos>=0){
				strReturn 			= 		strReturn.substring(0,intPos) +
											strForThat_IN +
											strReturn.substring(intPos+strThis_IN.length());
			}
		}//end while

		return strReturn;
	}


	/**
	 * <pre>
	 * Is this String null or empty? 
	 * 
	 * Utility for HttpServletRequest-parameters where 
	 * both checks are needed for form-validation).
	 * </pre>
	 * @return true if [what] is either null or an empty String ("").
	 **/
	public boolean isEmpty(String what){
		if(what==null) return true;
		if(what.trim().equals("")) return true;
		return false;
	}
	
	/**<pre>
	 * Utility for readability of code.
	 * 
	 * Returns true, if the String equals @link #g_STRING_UNINITIALIZED; 
	 * false if it doesn't.
	 * 
	 * This implies also that false is returned if the String is null 
	 * or empty.
	 * </pre>
	 * @param what
	 * @return true, if what is equal to @link #g_STRING_UNINITIALIZED
	 */
	public boolean isUninitialized(String what){
		if(what==null) return false;
		return what.endsWith(g_STRING_UNINITIALIZED);
	}
	
	/**<pre>
	 * Utility for readability of code.
	 * @param what
	 * @return true, if what==@link #g_ID_UNINITIALIZED; false otherwise.
	 */
	public boolean isUninitialized(long what){
		return (what==g_ID_UNINITIALIZED);
	}
	


	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 9.   H T T P - U T I L S
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------

	/**
	 * @deprecated 6
	 * @version 5.29-5
	 * @param r request containing either parameter s or idxs
	 * @return the SeminarID if it can be parsed.
	 * @throws NumberFormatException
	 */
	public static long getSessionID(HttpServletRequest r) throws NullPointerException{
		try{
			return Long.parseLong(r.getParameter("SessionID"));
		}catch(Exception eOtherParadigm){
			return Long.parseLong(r.getParameter("idxSessionID"));
		}
	}	
	
	/**
	 * <pre>
	 * Legacy.
	 * 
	 * Returns value of parameter "s," or, 
	 * if there is no numeric value for 
	 * parameter "s," it returns the 
	 * value of parameter "idxs".
	 * </pre>
	 * @version 5.29-5
	 * @param r request containing either parameter s or idxs
	 * @return the SeminarID if it can be parsed.
	 * @throws NumberFormatException
	 */
	public static long getSeminarID(HttpServletRequest r) throws NullPointerException, NumberFormatException{
		try{
			return Long.parseLong(r.getParameter("s"));
		}catch(Exception eOtherParadigm){
			return Long.parseLong(r.getParameter("idxs"));
		}
	}

	/**
	 * <pre>
	 * Utility to switch to secure SSL-connection.
	 * 
	 * Based on the ServletRequest, a https address 
	 * is constructed the following way: 
	 * 
	 * <Code>https://ServerName[:PORT]/requestURI</Code>
	 * 
	 * (Where <Code>[:PORT]</Code> is a local const.)
	 * 
	 * This method replaces the deprecated 'getHTTPSBase'
	 * 
	 * If blnClientAuth is set to 'true,' the server will 
	 * ask the client for an SSL certificate (digital id).
	 * </pre>
	 * @see #getHTTPSBase(HttpServletRequest, String)
	 * @param r: request to determine from where to form the SSL-address.
	 * @param blnClientAuth: boolean value indicating whether ssl should ask for client-identification or not.
	 * @return https-address to current folder of request.
	 **/
	public String getSSLBase(HttpServletRequest r, boolean blnClientAuth){
		String 		strGetSSLBase			 =			"";
		int 		intPosLastSlash			 =			0;
		String		strPort 				 = 	( blnClientAuth ) ? (":" + PORT_SSL_CLIENT_AUTH ) : (":" + PORT_SSL_NO_CLIENT_AUTH);
		strGetSSLBase						 = "https://" + r.getServerName() + strPort;
		strGetSSLBase						+=	r.getRequestURI();
		intPosLastSlash						 =	strGetSSLBase.lastIndexOf((int)'/');
		strGetSSLBase						 =  strGetSSLBase.substring(0,intPosLastSlash);

		return strGetSSLBase;
	}

	/**
	 * <pre>
	 * Utility to switch to secure SSL-connection.
	 * Based on the ServletRequest and the Port handed over, 
	 * a https address is constructed the following
	 * way: <Code>https://ServerName[:PORT]/requestURI</Code>.
	 * 
	 * Use getSSLBase(HttpServletRequest, boolean) instead!
	 * </pre>
	 * @see #getSSLBase(HttpServletRequest, boolean)
	 * @param r: request to determine from where to form the SSL-address.
	 * @param strPort: the port used for https-connections, if any. (default 8443 is _not_ presupposed).
	 * @return https-address to current folder of request.
	 * @deprecated since version 5-15-02. Please use getSSLBase() instead.
	 **/
	public String getHTTPSBase(HttpServletRequest r, String strPort){
		String 		strGetSSLBase			 =			"";
		int 		intPosLastSlash			 =			0;
		strPort 							 = 	( strPort.equals("") ) ? "" : ":" + strPort;
		strGetSSLBase						 = "https://" + r.getServerName() + strPort;
		strGetSSLBase						+=	r.getRequestURI();
		intPosLastSlash						 =	strGetSSLBase.lastIndexOf((int)'/');
		strGetSSLBase						 =  strGetSSLBase.substring(0,intPosLastSlash);

		return strGetSSLBase;
	}

	/**
	 * <pre>
	 * Utility to switch from secure SSL-connection.
	 * 
	 * Based on the ServletRequest and the Port handed over, 
	 * a http address is constructed the following way: 
	 * <Code>http://ServerName[:PORT]/requestURI</Code>
	 * </pre>
	 * @deprecated
	 * @param r: request to determine from where to form the Http-address.
	 * @param strPort: the port used for http-connections, if any. <b>Note: the port is disregarded since version 5-15-02 (March 2004).</b>
	 * @return http-address to current folder of request.
	 * Please use getHTTPBase(HttpServletRequest r) where possible.
	 **/
	public String getHTTPBase(HttpServletRequest r, String strPort){
		String 		strGetBase				 =			"";
		int 		intPosLastSlash			 =			0;
		strGetBase							 =	"http://" + r.getServerName() + ":" + strPort;
		strGetBase							+=	r.getRequestURI();
		intPosLastSlash						 =	strGetBase.lastIndexOf((int)'/');
		strGetBase							 =	strGetBase.substring(0,intPosLastSlash);

		return strGetBase;
	}

	/**
	 * <pre>
	 * Utility to switch from secure SSL-connection.
	 * 
	 * Based on the ServletRequest and the Port handed over, 
	 * a http address is constructed the following way: 
	 * 
	 * <Code>http://ServerName[:PORT]/requestURI</Code>
	 * 
	 * </pre>
	 * @param r: request to determine from where to form the Http-address.
	 * @return http-address to current folder of request.
	 **/
	public String getHTTPBase(HttpServletRequest r){
		String 		strGetBase				 =			"";
		int 		intPosLastSlash			 =			0;
		strGetBase							 =	"http://" + r.getServerName() + ":" + PORT_HTTP_INSECURE;
		strGetBase							+=	r.getRequestURI();
		intPosLastSlash						 =	strGetBase.lastIndexOf((int)'/');
		strGetBase							 =	strGetBase.substring(0,intPosLastSlash);

		return strGetBase;
	}

	/**
	 * <pre>
	 * Little helper for checkboxes:
	 * 
	 * To find out whether the checkbox 'chkWhat' was checked
	 * in the request 'req', call <Code>requestContains(req, "chkWhat")</Code>
	 * 
	 * </pre>
	 * @param HttpServletRequest r, the request to be skimmed
	 * String strParam_IN, the string that r is skimmed for.
	 * @return 'true', if strParam_IN is part of r,<br>
	 * 'false' otherwise.
	 * @version > 1.7i
	 **/
	public boolean requestContains(HttpServletRequest r, String strParam_IN){

		boolean 	blnReturn	= false;
		Enumeration pn			= r.getParameterNames();

		//check if parameter strParam_IN is part of r
		for(;pn.hasMoreElements();){
			if( ( (String) pn.nextElement() ).equals(strParam_IN) ){
				blnReturn		= true;
				break;
			}
		}
		return blnReturn;
  	}

	/**
	 * <pre>
	 * This method is used when dynamically created links 
	 * with many params end up in jsp-forms.
	 * Provides html-String ("&lt;input type="hidden" name="[name]" value="[value]" /&gt;") 
	 * with all params handed
	 * over in the request.
	 * </pre>
	 * @return Html-String to put into a form, containing "&lt;input type="hidden" ... /&gt;" elements.
	 * @param r: the request with form-data.
	 **/
	public String getFormData(HttpServletRequest r){

		String 		strReturn		 =				"";
		String 		strTmp			 =				"";
		Enumeration en				 =				r.getParameterNames();

		for(;en.hasMoreElements();){
			strTmp					 = ( String ) 	en.nextElement();
			strReturn				+= "<input type=\"hidden\" name=\"" +
										strTmp + "\" value=\"" +
										shjSubstitute(r.getParameter(strTmp), "\"", "&quot;") +
										"\" />\n";
		}

		return strReturn;
	}

	/**
	 * <pre>
	 * Method returns a query String (just like 
	 * HttpServletRequest.getQueryString()),
	 * but it also includes POST-sent form-data. 
	 * 
	 * " characters are substituted with %22 characters.
	 * </pre>
	 * @return query String such as mode=1&session=34
	 * @param r: the request with form-data.
	 **/
	public String getShjQueryString(HttpServletRequest r){

		String 		strReturn		 =				"";
		String 		strTmp			 =				"";
		Enumeration en				 =				r.getParameterNames();

		for(;en.hasMoreElements();){
			strTmp					 = ( String ) 	en.nextElement();
			strReturn				+= strTmp + "=" + shjSubstitute(r.getParameter(strTmp), "\"", "%22") + "&";
		}

		return strReturn.substring(0, strReturn.length()-1);
	}


	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 10.   X M L - U T I L S
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------

	/**
	 *  Main util to convert a String into an XML node.
	 *  @return Node with the document's DocumentElement.
	 *  @throws Exceptions on malformed XML String.
	 *  @param strXML: String containing a valid, well-formed XML document.
	 **/
	public Node shjDocumentElement(String strXML) throws Exception{
		InputSource 			in		 = new InputSource(new java.io.StringReader(strXML));
		DocumentBuilderFactory 	dfactory =     DocumentBuilderFactory.newInstance();
		dfactory.setNamespaceAware		( true );

		return (dfactory.newDocumentBuilder().parse(in)).getDocumentElement();
  	}

	/**
	 *  Main util to convert a String into an XML node.
	 *  @return Node with the document's DocumentElement.
	 *  @throws Exceptions on malformed XML String.
	 *  @param strXML: String containing a valid, well-formed XML document.
	 *  @param strEncoding: valid character encoding
	 **/
	public Node shjDocumentElement(String strXML, String strEncoding) throws Exception{
		InputSource 			in		 = new InputSource(new java.io.StringReader(strXML));
		in.setEncoding					  ( strEncoding );
		DocumentBuilderFactory 	dfactory =     DocumentBuilderFactory.newInstance();
		dfactory.setNamespaceAware		( true );

		return (dfactory.newDocumentBuilder().parse(in)).getDocumentElement();
  	}

	/**
	 * <pre>
	 * Get Node value of the node <Code>NodeName</Code> in the node <Code>n</Code>. 
	 * If an error occurrs -- the node does not exist --, 
	 * an empty String is returned.
	 * </pre>
	 * @return value of the subnode 'NodeName' of node n. Empty String on error.
	 * @param n: Node to scan
	 * @param NodeName: name of the node whose value is to be returned.
	 **/
	public String shjNodeValue(Node n, String NodeName){
		try{
		  return ((Element)n).getElementsByTagName(NodeName).item(0).getChildNodes().item(0).getNodeValue();
		}catch(Exception eShjNodeValue){return "";}
	}

	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 11.   T e a c h e r/A d m i n   l o g i n - m e t h o d s
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------

	/**
	 * initialize object by SessionID, DozentID and SeminarID (the latter two being the primary key of the table 'tblSdDozent').
	 * The reason why this method does not rely on the SessionID alone as a unique index on table tblSdDozent alone is this:
	 * if a teacher logs in and gets SessionID 1. (s)he log out but keeps pages in his browser's history. Later, another teacher logs in and
	 * gets SessionID 1. If the first teacher then clicks his browser's history, (s)he might get access to the data of another teacher.
	 * This is a theoretical threat to security. But now it's not even theoretical anymore.<br />
	 * <b>Note:</b> Currently, this method is (almost) identical to 'initBySession' in com.shj.data.Dozent.
	 * See methods 'initBySession' with more parameters (security level and ip) for more info.
	 * @deprecated version 6
	 * @param lngSeminarID ID of seminar. Unique identifier together with DozentID (see below)
	 * @param lngDozentID ID of this teacher. Unique identifier together with SeminarID (see above).
	 * @param lngSessionID current SessionID of this teacher.
	 * @return true if this object could be initialized, false if session seems invalid.
	 * @exception sql exceptions are thrown if the connection to database is erroneous.
	 */
	public boolean initBySession(long lngSeminarID, long lngDozentID, long lngSessionID) throws Exception{
		ResultSet rst		= sqlQuery("select * from \"tblSdDozent\" where \"lngDozentID\"=" + lngDozentID + "AND \"lngDozentSessionID\"=" + lngSessionID + " AND \"lngSdSeminarID\"=" + lngSeminarID + ";");
		boolean blnReturn	= rst.next();
		rst.close();
		return blnReturn;
	}

	/**
	 * Initialize object by SessionID, DozentID and SeminarID (the latter two being the primary key of the table 'tblSdDozent'), checking that the teacher has a high-enough 'security level.'
	 * The reason why this method does not rely on the SessionID alone as a unique index on table tblSdDozent alone is this:
	 * if a teacher logs in and gets SessionID 1. (s)he log out but keeps pages in his browser's history. Later, another teacher logs in and
	 * gets SessionID 1. If the first teacher then clicks his browser's history, (s)he might get access to the data of another teacher.
	 * This method returns true if and only if <br />(1) the teacher can be identified by id and session, and <br />(2) the
	 * teacher's security-level in the database is equal or greater than 'intSecLevelMin.'
	 * @deprecated
	 * @param lngSeminarID ID of seminar. Unique identifier together with DozentID (see below)
	 * @param lngDozentID ID of this teacher. Unique identifier together with SeminarID (see above).
	 * @param lngSessionID current SessionID of this teacher.
	 * @param intSecLevelMin the level of security that is required to show a given page.
	 * @return true if this object could be initialized, false if session seems invalid.
	 * @exception sql exceptions are thrown if the connection to database is erroneous.
	 */
	public boolean initBySession(long lngSeminarID, long lngDozentID, long lngSessionID, int intSecLevelMin) throws Exception{
		ResultSet 	rst		  = sqlQuery("select * from \"tblSdDozent\" where \"lngDozentID\"=" + lngDozentID + "AND \"lngDozentSessionID\"=" + lngSessionID + " AND \"lngSdSeminarID\"=" + lngSeminarID + " AND \"intDozentAccessLevel\">=" + intSecLevelMin + ";");
		boolean 	blnReturn = rst.next();
		rst.close();
		return blnReturn;
	}

	/**
	 * Initialize object by SessionID, DozentID and SeminarID (the latter two being the primary key of the table 'tblSdDozent'), checking that the teacher has a high-enough 'security level' and that the request comes from the teacher's stored IP address.
	 * The reason why this method does not rely on the SessionID alone as a unique index on table tblSdDozent alone is this:
	 * if a teacher logs in and gets SessionID 1. (s)he log out but keeps pages in his browser's history. Later, another teacher logs in and
	 * gets SessionID 1. If the first teacher then clicks his browser's history, (s)he might get access to the data of another teacher.
	 * This method returns true if and only if <br />(1) the teacher can be identified by id and session, and <br />(2) the
	 * teacher's security-level in the database is equal or greater than 'intSecLevelMin,' and finally <br />(3) the IP handed over is identical with the
	 * IP that is stored of this teacher.
	 * @deprecated
	 * @param lngSeminarID ID of seminar. Unique identifier together with DozentID (see below)
	 * @param lngDozentID ID of this teacher. Unique identifier together with SeminarID (see above).
	 * @param lngSessionID current SessionID of this teacher.
	 * @param intSecLevelMin the level of security that is required to show a given page.
	 * @param strIP the IP of the request. It is compared with the teacher's IP stored in the database.
	 * @return true if this object could be initialized, false if session seems invalid.
	 * @exception sql exceptions are thrown if the connection to database is erroneous.
	 */
	public boolean initBySession(long lngSeminarID, long lngDozentID, long lngSessionID, int intSecLevelMin, String strIP) throws Exception{
		ResultSet 	rst			= sqlQuery("select * from \"tblSdDozent\" where \"lngDozentID\"=" + lngDozentID + "AND \"lngDozentSessionID\"=" + lngSessionID + " AND \"lngSdSeminarID\"=" + lngSeminarID + " AND \"intDozentAccessLevel\">=" + intSecLevelMin + " AND \"strDozentIP\"='" + strIP + "';");
		boolean 	blnReturn	= rst.next();
		rst.close();
		return blnReturn;
	}

 	/**
 	 * From @version 5-15-03 on, this should be the major access-check-method.
 	 * @return true if teacher/administrator has access under the defined circumstances. The circumstances are:
 	 * <ul>
 	 *  <li>the seminar, teacher-id and sessionid must match.
 	 *  <li>if intSecLevelMin>0, then the teacher's access level must be equal to or greater than intSecLevelMin,
 	 *  <li>if policy asks for "IP_IDENT", the teacher's id in sign up is compared to the ip of the request.
 	 *  <li>if policy asks for "DIGITAL_ID", the request's digital id is compared to the one stored in signup.
 	 * </ul>
 	 * In case the digital id is checked, it is also checked for non-revoke and validity (according to signup's flags).
 	 * Valid policy-values are:
 	 * <ul>
 	 *   <li>POLICY_NO_RESTRAINT (0),
 	 *   <li>POLICY_HTTPS (1),
 	 *   <li>POLICY_HTTPS_IP_IDENT (2),
 	 *   <li>POLICY_HTTPS_DIGITAL_ID (3),
 	 *   <li>POLICY_HTTPS_IP_AND_DIGID (4)
 	 * </ul>
 	 * @param lngSeminarID: ID of seminar that the teacher belongs to
 	 * @param lngDozentID: ID of teacher
 	 * @param lngSessionID: ID of session
 	 * @param r: request asking for access (only needed for 'isSecure()' and digital id)
 	 * @param policy: the access-policy to use. Possible values are:
 	 * <ul>
 	 *   <li>POLICY_NO_RESTRAINT (0),
 	 *   <li>POLICY_HTTPS (1),
 	 *   <li>POLICY_HTTPS_IP_IDENT (2),
 	 *   <li>POLICY_HTTPS_DIGITAL_ID (3),
 	 *   <li>POLICY_HTTPS_IP_AND_DIGID (4)
 	 * </ul>
 	 * The reason why this method does not throw the 'appropriate' Exceptions (error-codes for 'access_denied') is
 	 * that I am still hoping to be able to redirect access (from no client-auth-port to client-auth-port etc.) if
 	 * 'hasAccess' fails. So, currently, the jsp-page has to throw the corresponding error.
 	 * @deprecated
 	 * @param intSecLevelMin: minimum security level that this teacher must have in order to be granted access.
 	 * @throws Exception only when db-connection is erroneous.
 	 **/
	public boolean hasAccess(long lngSeminarID, long lngDozentID, long lngSessionID, HttpServletRequest r, byte policy, int intSecLevelMin) throws Exception{
		//#hack
		//if((policy>0)&&(!r.isSecure())) return false;

		String		strWhere	= "\"lngDozentID\"=" + lngDozentID +
								  " AND \"lngDozentSessionID\"=" + lngSessionID +
								  " AND \"lngSdSeminarID\"=" + lngSeminarID;

		if(intSecLevelMin>0) strWhere	+= " AND \"intDozentAccessLevel\">=" + intSecLevelMin;
		if((policy==POLICY_HTTPS_IP_IDENT)||(policy==POLICY_HTTPS_IP_AND_DIGID)) strWhere+=" AND \"strDozentIP\"='" + r.getRemoteAddr() + "'";
		if((policy==POLICY_HTTPS_DIGITAL_ID)||(policy==POLICY_HTTPS_IP_AND_DIGID)){
        	X509Certificate cert = null;
			X509Certificate certArray[] = (X509Certificate []) r.getAttribute("javax.servlet.request.X509Certificate");
        	if (certArray != null) {
          		cert = certArray[0];
			  strWhere			+= "AND \"blnDozentCertRevoked\"=" + getDBBoolRepresentation(false) +  ", " +
			  "AND \"blnDozentCertValidated\"=" + getDBBoolRepresentation(true) +  ", " +
			  "AND \"strDozentCertSerialID\"='" + cert.getSerialNumber().toString() +  "', " +
			  "AND \"strDozentCertIssuerDN\"='" + cert.getIssuerDN().getName() +  "', " +
			  "AND \"strDozentCertSubjectDN\"='" + cert.getSubjectDN().getName() +  "'";
			}else{return false;}
		}
		return !((lookUp("lngDozentID", "tblSdDozent", strWhere).equals("#NO_RESULT")));
	}

	
	/**
	 * This is now the main method for Staff/Config login-checks.
	 * Method to check if access to a page should be granted (@version 5-27-11). Returns
	 * true if access is granted. Throws an Exception otherwise.
	 * The method looks up the teacher specified in the request (through his/her Id and 
	 * the SessionID. Then a check according to the policy handed over is made.
	 * Lastly, an exception is also thrown if the level handed over is greater 
	 * than the teacher's access-level.
	 * @deprecated
	 * @param request the request put to the server. Must contain "txtPID", "SessionID" and "s" (=SeminarID), or, 
	 * alternatively, "idxPID", "idxSessionID" and "idxs" (=SeminarID).
	 * @param policy one of the policies defined in this class  above (HTTPS, or CLIENT_AUTH, or IP), or whatever.
	 * @param level a security level.  
	 * @return true, if everything goes well. Otherwise falls or Exception.
	 * @throws Exception (ERR_LOGIN_SECURITY, ERR_STAFF_ACCESS_DENIED_CL_AUTH, or NullPointerException (java.lang.NumberFormat), if request does not contain the needed parameters.
	 */
	public boolean checkAccess(HttpServletRequest request, byte policy, int level) throws Exception{
		
		// This page _has_ a security restraint of at least https-level:
		if( policy > POLICY_NO_RESTRAINT ){
		  // insecure request, although policy wants secure one
		  if(!request.isSecure()) throw new Exception(String.valueOf(ERR_LOGIN_SECURITY));
		  
		  if (String.valueOf(request.getServerPort()).equals(PORT_SSL_NO_CLIENT_AUTH) && policy >= POLICY_HTTPS_DIGITAL_ID){
		    throw new Exception(String.valueOf(ERR_STAFF_ACCESS_DENIED_CL_AUTH));
		  }
		}
		
		// Extract Ids
		long lngSeminarID		= -99;
		long lngPID				= -99;
		long lngSessionID		= 0;
		
		try{
			lngSeminarID		= Long.parseLong(request.getParameter("s"));
			lngPID				= Long.parseLong(request.getParameter("txtPID"));
			lngSessionID		= Long.parseLong(request.getParameter("SessionID"));
		}catch(Exception eOtherParadigm){
			lngSeminarID		= Long.parseLong(request.getParameter("idxs"));
			lngPID				= Long.parseLong(request.getParameter("idxPID"));
			lngSessionID		= Long.parseLong(request.getParameter("idxSessionID"));
			}
		if(!(hasAccess(lngSeminarID, lngPID, lngSessionID, request, policy, level)))  throw new Exception(String.valueOf(ERR_LOGIN_SECURITY));
		
		return true;
	}	
	
	/**
	 * @returns a String that postgres can deal with: if str is empty (null or empty string), then [null] is returned.
	 * Otherwise, dbNormal returns the String itself, wrapped in half quotation-marks: ['content'].
	 * @param str: String to be normalized for database.
	 **/
	protected String dbNormal(String str){
		return ( (this.normalize(str).equals("")) ? "null" : "'" + this.getDBCleanString(str) + "'");
	}
	
	public String dbNormalTime(Time time){
		return (time==null ? "null" :  ("'"  + g_TIME_FORMAT.format(time) + "'"));
	}
	
	public String dbNormalDate(Date date){
		return (date==null ? "null" :  ("'"  + g_ISO_DATE_FORMAT.format(date) + "'"));
	}

	public String dbNormalTimestamp(Timestamp timestamp){
		return (timestamp==null ? "null" :  ("'"  + g_TIMESTAMT_FORMAT.format(timestamp) + "'"));
	}

	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 12.	"U N I N T E R E S T I N G"    C O D E
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------

	/**
	 * (Session-management via Cookies: unused)
	 * @param event Session started
	 **/
	 public void valueBound(HttpSessionBindingEvent event){
	 }

	/**
	 * (Session-management via Cookies: unused, tries a 'close connection' nevertheless.)
	 * @param event Session stopped
	 **/
	public void valueUnbound(HttpSessionBindingEvent event){
      try{close();}
	  catch (SQLException e){}
    }//end of "valueUnbound"

	/**
	 * @param blnAutoCommit Flag indicating whether db should be in auto-commit-
	 * mode or whether an explicit commit is needed.
	 * @throws SQLException DB-error
	 **/
	public void setAutoCommit(boolean blnAutoCommit) throws Exception{
   	 getConnection().setAutoCommit(blnAutoCommit);
 	}//end of "setAutoCommit"

  	/** Unused in this context
  	 * @param intLevel Transaction Level to be set.
  	 * @throws SQLException db error
  	 */
  	public void setTransactionIsolation(int intLevel) throws Exception{
  		getConnection().setTransactionIsolation(intLevel);
  	}//end of "setTransactionIsolation"

	/**
	 * Unused in this context
	 * @param strSQL Statement to prepare
	 * @throws SQLException db error
	 * @return Flag indicating whether preparation was successful.
	 */
	public PreparedStatement prepareStatement(String strSQL) throws SQLException, NamingException{
		return getConnection().prepareStatement(strSQL);
  	}//end of "prepareStatement"

	/**
	 * Either a single quote, if strName_IN starts with 'str', or an empty String.
	 * @return Either a single quote, if strName_IN starts with 'str', or an empty String.
	 * @param strName_IN: name of a db column.
	 **/
	private String getQuoteOpt(String strName_IN){
	  return (strName_IN.startsWith("str")) ? "'" : "";
	}


	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 13.	C O N S T R U C T O R
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	
	/**
	 * Empty constructor.
 	 **/
    public shjCore() {
        g_TODAY = new Date(new java.util.Date().getTime());
        try {
            m_ctx = new InitialContext();
            m_DB = (DataSource) m_ctx.lookup("java:comp/env/jdbc/UP_db");
            //System.out.println("New Datasource created...");
        } catch (NamingException e) {
            m_strDebug += e.toString();
        }

    }
}
