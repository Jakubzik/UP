/**
 * Software-Entwicklung H. Jakubzik, Lizenz Version 1.0 (Uni Heidelberg)
 *
 *
 * Copyright (c) 2004-2016 Heiko Jakubzik.
 *
 * http://www.shj-online.de
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
 * @version UP Open Source, Mai 2016
 * change date				change author			change description
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
import org.w3c.dom.*;
import org.apache.commons.beanutils.RowSetDynaClass;

import de.shj.UP.util.ResultSetSHJ;

import java.security.cert.*;	// needed for client-auth.

/**
 * shj Standard Klasse f�r PostgreSQL-Datenbanken
 * Spezielle SignUp Anpassungen
 *
 * =========================================================================================================================================================================================================================================
 */
public class shjCoreReduced implements Serializable, HttpSessionBindingListener{

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
	public static final byte   POLICY_NO_RESTRAINT		= 0;
	public static final byte   POLICY_HTTPS			= 1;
	public static final byte   POLICY_HTTPS_IP_IDENT	= 2;
	public static final byte   POLICY_HTTPS_DIGITAL_ID	= 3;
	public static final byte   POLICY_HTTPS_IP_AND_DIGID    = 4;
	
	public static final String 	g_STRING_UNINITIALIZED	= "#";
	public static final long	g_ID_UNINITIALIZED	= -4711;
	
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
  	public 		String 			strErrMsg				=		"";
	public 		String 			m_strDebug;


	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 3.   PRIVATE / PROTECTED   D E C L A R A T I O N S
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------

  	private 	Connection 	conDB	= null;
  	private		static Context	m_ctx	= null;
  	private		DataSource	m_DB	= null;
  	protected 	XPathAPI 	g_xPath;
  	private		boolean		m_bKeepConnectionOpen=false;

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
	 * In U:P also java:comp/env/jdbc/db_UP
	 * </pre>
	 * @version os-1.0
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
  	}
	
  	/**
  	 * Schließt die Verbindung zur Datenbank
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
  	 * @version os-1.0
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
         * 
         * ACHTUNG: auf SQL Injection muss dann bei der 
         * Konstruktion von sqlString geachtet werden.
  	 * </pre>
  	 * @returns ResultSet mit dem Ergebnis der spezifizierten Abfrage
  	 * @param strQuery: Select-Abfrage in PostgreSQL-g�ltigem Syntax.
  	 * @throws Exception Verbindung zum Datenbankserver fehlgeschlagen
  	 **/
  	public ResultSet sqlQuery(String strQuery) throws Exception{
            getConnection().clearWarnings();
            return getConnection().createStatement().executeQuery( strQuery );
  	}

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
    public boolean sqlExeSingle(String strQuery) throws Exception {

        boolean blnReturn = true;
        int intPos = 0;

        try {
            // start transaction:
            //log("sqlExeSingle: Turning auto-commit off.", g_LOG_LEVEL_DEBUG);
            log("sqlExeSingle: Transaktionen sind generell abgeschaltet! Hier: " + strQuery + ".", g_LOG_LEVEL_WARNING);
            //this.getConnection().setAutoCommit(false);

            String strBatch = strQuery;
            String strCmd = "";
            intPos = strBatch.indexOf(';');

            // Loop through strBatch:
            while ((intPos >= 0) && blnReturn) {
                strCmd = strBatch.substring(0, intPos);
                blnReturn = (blnReturn && sqlExe(strCmd + ";"));
                strBatch = strBatch.substring(intPos + 1);
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
        try {
            if (!m_bKeepConnectionOpen) {
                conDB.close();
            }
        } catch (Exception eClo) {
        }
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
    public boolean sqlExe(String strQuery) throws Exception {
        return (sqlExeCount(strQuery) != -1);
    }//end of "sqlExe"

    /**
     * F�hrt SQL-Befehl aus und liefert Anzahl der betroffenen Datens�tze
     * zur�ck. Beispiel:
     * <pre>
     * out.write("Anzahl Studierende: " + sqlExe("delete from \"tblBdStudent\";"));
     * </pre> Siehe auch {@link #sqlExe(String)} und
     * {@link #sqlExeSingle(String)}
     *
     * @param strQuery SQL-Abfrage ohne Ergebnisse.
     * @since 6-08-00
     * @throws Exception
     * @return Anzahl betroffener Datens�tze, 0 for DDL Abfragen, or -1 bei SQL
     * Fehler.
	 *
     */
    public int sqlExeCount(String strQuery) throws Exception {

        int intReturn = -1;

        //check if query not empty:
        if ((strQuery == null) || (strQuery.equals(""))) {
            throw new Exception(String.valueOf(ERR_SIGNUP_EMPTY_SQL));
        }

        try {
            Statement stm = getConnection().createStatement();
            intReturn = stm.executeUpdate(strQuery);
            log(intReturn + " Datensätze betroffen von " + strQuery, g_LOG_LEVEL_DEBUG);
            stm.close();
        } catch (Exception e3) {
        }

        // April 11 2006
        try {
            if (!m_bKeepConnectionOpen) {
                conDB.close();
            }
        } catch (Exception eClo) {
        }
        return intReturn;
    }
	
    /**
     * Transaktion ausf�hren.
     * @throws SQLException DB error
     **/
    public void commit() throws SQLException{
       if (conDB!=null) conDB.commit();
    }//end of "commit"

    /**
     * Transaktion nicht ausf�hren und aus Cache l�schen.
     *
     * @throws SQLException
  	 *
     */
    public void rollback() throws SQLException {
        if (conDB != null) {
            conDB.rollback();
        }
    }//end of "rollback"


    // ------------------------------ ------------------------------
    // ------------------------------ ------------------------------
    // 6.   P U B L I C  M E T H O D S: D B-U T I L S
    // ------------------------------ ------------------------------
    // ------------------------------ ------------------------------

    protected void finalize() {
        try {
            this.close();
        } catch (SQLException e) {
        }
        try {
            super.finalize();
        } catch (Throwable e1) {
        }
    }
	
    /**
     * Close connection to database
     *
     * @throws SQLException Something goes wrong ... :-)
  	 *
     */
    public void disconnect() throws SQLException {
        if ((conDB != null) && (!conDB.isClosed())) {
            conDB.close();
        }
        conDB = null;
        isConnected = false;
    }//end of "close"
  	
    // ------------------------------ ------------------------------
    // ------------------------------ ------------------------------
    // 7.   P U B L I C   D A T E / T I M E  U T I L S
    // ------------------------------ ------------------------------
    // ------------------------------ ------------------------------

    /**
     * @param d
     * @return Deutsch formatiertes Datum
     */
    public static String shjGetGermanDate(java.sql.Date d) {
        return g_GERMAN_DATE_FORMAT.format(d);
    }

    // ------------------------------ ------------------------------
    // ------------------------------ ------------------------------
    // 8.   S T R I N G  U T I L S
    // ------------------------------ ------------------------------
    // ------------------------------ ------------------------------
	
    /**
     * <pre>
     * Returns the String that is handed
     * in, or an empty String, if <Code>null</Code>
     * is handed in.
     *
     * Utility to avoid a <Code>NullPointerException</Code>
     * </pre>
     *
     * @return Empty String if str_IN==null, or return the String itself
     * otherwise.
     * @param str_IN: String to be normalized.
	 *
     */
    public static String normalize(String str_IN) {
        return (str_IN == null) ? "" : str_IN;
    }
	
    /**
     * Überarbeitung April 2016: Backslashes werden aussortiert.
     *
     * @param s
     * @return
     */
    public static String string2JSON(String s) {
        return (s == null) ? "" : s.replace('\n', ' ').replace('\r', ' ').replace('\t', ' ').replace('\\', '/').replaceAll("\"", "\\\\\"");
    }
		


	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 9.   H T T P - U T I L S
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------

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
 	public shjCoreReduced(){
 		g_xPath=new XPathAPI();
 		g_TODAY = new Date(new java.util.Date().getTime());
 			try {
				m_ctx = new InitialContext();
				m_DB  = (DataSource)m_ctx.lookup("java:comp/env/jdbc/SignUpXP");
				//System.out.println("New Datasource created...");
			} catch (NamingException e) {
				m_strDebug += e.toString();
			}

  	}
}
