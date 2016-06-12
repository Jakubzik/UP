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
import org.apache.commons.beanutils.RowSetDynaClass;

import de.shj.UP.util.ResultSetSHJ;


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
    
    public static final byte ERR_SIGNUP_NO_CONNECTION_TO_DB = -120;
    public static final byte ERR_SIGNUP_EMPTY_SQL = -119;
    public static final byte ERR_UNKNOWN = 0;
    public static final byte ERR_REGISTRATION_CURRENTLY_NOT_ALLOWED = 1;
    public static final byte ERR_BROWSER_PROBLEM = 2;
    public static final byte ERR_LOGIN_STUDENT_FAILED = 3;
    public static final byte ERR_LOGIN_STUDENT_FAILED_UMLAUT = 64;
    public static final byte ERR_INVALID_SESSION = 4;
    public static final byte ERR_LOGIN_CONFIRMATION_FAILED = 5;
    public static final byte ERR_STUDENT_REGISTRATION_FAILED_WRONG_MATRIKEL = 6;
    public static final byte ERR_STUDENT_REGISTRATION_FAILED_FACH_MISSING = 7;
    public static final byte ERR_STUDENT_REGISTRATION_FAILED_FACHSEMESTER_MISSING = 8;
    public static final byte ERR_STUDENT_REGISTRATION_FAILED = 10;
    public static final byte ERR_STUDENT_REGISTRATION_FAILED_ALREADY_REGISTERED = 11;
    public static final byte ERR_STUDENT_TEST_REGISTRATION_FAILED_DOUBLETTE = 12;
    public static final byte ERR_STUDENT_TEST_REGISTRATION_FAILED_DOUBLETTE_COMMITMENT = 58;
    public static final byte ERR_STUDENT_TEST_REGISTRATION_FAILED_DOUBLETTE_CREDIT = 59;
    public static final byte ERR_STUDENT_REGISTRATION_FAILED_ANREDE_MISSING = 50;
    public static final byte ERR_STUDENT_REGISTRATION_FAILED_EMAIL_MISSING = 51;
    public static final byte ERR_STUDENT_REGISTRATION_FAILED_PASSWORD_TOO_SIMPLE = 52;
    public static final byte ERR_STUDENT_REGISTRATION_FAILED_BIRTHDAY_WRONG = 53;
    public static final byte ERR_LOGIN_STAFF_FAILED = 13;
    public static final byte ERR_KURS_DATE_ADD_FAILED = 14;
    public static final byte ERR_KURS_DATE_COLLIDES_WITH_OTHER_KURS = 15;
    public static final byte ERR_KURS_MISCONFIGURATION_FREETEXT_PLUS_ROOM = 16;
    public static final byte ERR_KURS_MISCONFIGURATION_FREETEXT = 17;
    public static final byte ERR_KURS_MISCONFIGURATION_EXTERNAL_ROOM_WITH_INTERNAL_NAME = 18;
    public static final byte ERR_KURS_MISCONFIGURATION_EXTERNAL_ROOM = 19;
    public static final byte ERR_KURS_MISCONFIGURATION_ROOM_MISSING = 20;
    public static final byte ERR_KURS_MISCONFIGURATION_ROOM_AND_TIME_MISFIT = 21;
    public static final byte ERR_KURS_MISCONFIGURATION_1 = 22;
    public static final byte ERR_KURS_MISCONFIGURATION_2 = 23;
    public static final byte ERR_KURS_ROOM_NOW_OCCUPIED_1 = 24;
    public static final byte ERR_KURS_ROOM_NOW_OCCUPIED_2 = 27;
    public static final byte ERR_KURS_ROOM_MISSING = 25;
    public static final byte ERR_KURS_ROOM_MISSING_2 = 26;
    public static final byte ERR_KURS_ROOM_INTERNAL_ERROR = 28;
    public static final byte ERR_KURS_KURSTYP_MISSING = 29;
    public static final byte ERR_KURS_BLOCKED_BY_OPEN_COMMITMENT = 48;
    public static final byte ERR_LOGIN_SECURITY = 30;
    public static final byte ERR_DATE_FORMAT_UNREADABLE = 31;
    public static final byte ERR_DATE_TOO_FAR_IN_FUTURE = 32;
    public static final byte ERR_DATE_TOO_FAR_IN_PAST = 33;
    public static final byte ERR_ECTS_CREDIT_FORMAT = 34;
    public static final byte ERR_CREDIT_ADDITION_FAILED_ON_DB = 35;
    public static final byte ERR_CREDIT_UPDATE_FAILED_ON_DB = 36;
    public static final byte ERR_CREDIT_MAPPING_TO_COURSE_FAILURE = 41;
    public static final byte ERR_MISSING_CREDITS_ON_MOD_EXAM = 37;
    public static final byte ERR_TRYING_TO_ERASE_LAST_COURSEMAPPING = 38;
    public static final byte ERR_CONFIG_CREDIT_TEACHER_MISSING = 39;
    public static final byte ERR_STAFF_ACCESS_DENIED_CL_AUTH = 40;
    public static final byte ERR_EXAM_DELETION_FAILED = 42;
    public static final byte ERR_EXAM_NOT_SAVED = 43;
    public static final byte ERR_STAFF_PASSWORD_MISMATCH = 44;
    public static final byte ERR_CONFIG_CREDIT_GRADE_MISSING = 45;
    public static final byte ERR_PASSWORD_TOO_SIMPLE = 46;
    public static final byte ERR_CREDIT_DELETION_FAILED = 47;
    public static final byte ERR_OVERCOMMITTED = 49;
    public static final byte ERR_COMMITMENT_FAILED_ON_DB = 54;
    public static final byte ERR_UPLOAD_FILE_TOO_BIG = 55;
    public static final byte ERR_UPLOAD_FILE_WRONG_TYPE = 56;
    public static final byte ERR_KVVINHALT_DELETE_FAILED_NOT_EMPTY = 57;
    public static final byte ERR_LOGIN_FAILS_ON_URZ = 60;
    public static final byte ERR_PRINTING_CREDITS_EMPTY_SET = 61;
    public static final byte ERR_SIGNUP_PERIOD_ILLOGICAL = 62;
    public static final byte ERR_PROHIBITED_DURING_SIGNUP_PERIOD = 63;
    public static final byte ERR_CREDIT_MODULE_MAPPING_MISSING = 65;
    public static final byte ERR_CREDIT_BA_NOT_MAPPED_TO_ANY_MODULE = 66;
    public static final byte ERR_GS_BA_LATINUM_OR_FREMDSPR_MISSING_FOR_COMMITMENT = 67;
    public static final byte ERR_AS_SIGNUP_PREREQUISITES_MISSING = 68;
    public static final byte ERR_LOGIN_FAILS_ON_TOO_MANY_CONNECTIONS = 69;
    public static final byte ERR_GS_SIGNUP_PREREQUISITES_MISSING = 70;
    public static final byte ERR_AS_SIGNUP_DOUBLE_PROSEMINAR = 71;

    // ------------------------------ ------------------------------
    // ------------------------------ ------------------------------
    // 2.   Ö F F E N T L I C H E   K O N S T A N T E N
    // ------------------------------ ------------------------------
    // ------------------------------ ------------------------------
    public static final byte POLICY_NO_RESTRAINT = 0;
    public static final byte POLICY_HTTPS = 1;
    public static final byte POLICY_HTTPS_IP_IDENT = 2;
    public static final byte POLICY_HTTPS_DIGITAL_ID = 3;
    public static final byte POLICY_HTTPS_IP_AND_DIGID = 4;

    public static final byte g_LOG_LEVEL_SILENT = 0;
    public static final byte g_LOG_LEVEL_WARNING = 1;
    public static final byte g_LOG_LEVEL_INFO = 2;
    public static final byte g_LOG_LEVEL_DEBUG = 3;
    public static final byte g_LOG_LEVEL_ERROR = 4;

    public byte LOG_LEVEL = 1;

    public Date g_TODAY; // Wird bei Konstruktion initialisiert.
    
    public static final SimpleDateFormat g_ISO_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat g_GERMAN_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
    public static final SimpleDateFormat g_BRITISH_DATE_FORMAT = new SimpleDateFormat("d MMM, yyyy", Locale.UK);
    public static final SimpleDateFormat g_TIME_FORMAT = new SimpleDateFormat("HH:mm");
    public static final SimpleDateFormat g_TIMESTAMT_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        
    // ------------------------------ ------------------------------
    // ------------------------------ ------------------------------
    // 2.   Ö F F E N T L I C H E   E I G E N S C H A F T E N
    // ------------------------------ ------------------------------
    // ------------------------------ ------------------------------
    public boolean isConnected = false;
    public String strErrMsg = "";
    public String m_strDebug;

    // ------------------------------ ------------------------------
    // ------------------------------ ------------------------------
    // 3.   P R I V A T E   E I G E N S C H A F T E N
    // ------------------------------ ------------------------------
    // ------------------------------ ------------------------------
    private Connection conDB = null;
    private static Context m_ctx = null;
    private DataSource m_DB = null;
    private boolean m_bKeepConnectionOpen = false;

    // ------------------------------ ------------------------------
    // ------------------------------ ------------------------------
    // 5.   P U B L I C  M E T H O D S: D B-C O N N E C T I V I T Y
    // ------------------------------ ------------------------------
    // ------------------------------ ------------------------------
    public void log(String sMsg, byte level) {
        if (level <= LOG_LEVEL) {
            System.err.println(getLogPrefix(level) + sMsg);
        }
    }
    
    private String getLogPrefix(byte level) {
        switch(level){
                case g_LOG_LEVEL_WARNING:
                    return "[WARN]: ";
                    
                case g_LOG_LEVEL_INFO:
                    return "[INFO]: ";
                    
                case g_LOG_LEVEL_DEBUG:
                    return "[DEBUG]: ";
                    
                case g_LOG_LEVEL_ERROR:
                    return "[ERROR]: ";
                    
                default:
                    return "[?]:";
        }
    }

    /**
     * Voreinstellung ist 'falsch'; je nach jdbc-Treiber 
     * und Postgres-Version ist es besser, die DB-Verbindung 
     * nicht zu schließen.
     * @param b
     */
    public void setKeepConnectionOpen(boolean b) {
        m_bKeepConnectionOpen = b;
    }
  	
    /**
     * Der Servlet Container h�lt f�r jsp-Zugriffe
     * die Datenbankverbindung vor.
     *
     * Für Zugriffe, die nicht vom Servlet Container
     * ausgehen (cron-Skripte undÄ�hnliches), können
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
     *
     * @param ds
     * @throws Exception
     */
    public void extConnect(DataSource ds) throws Exception {
        m_DB = ds;
        conDB = null;
    }
  	  	
    /**
     * Initialisiert die Datenbankverbindung
     * In U:P also java:comp/env/jdbc/db_UP
     *
     * @version os-1.0
     * @throws SQLException
     * @return true, wenn die Verbindung erfolgreich neu hergestellt wurde;
     * falsch, wenn etwas schiefging oder die Verbindung bereits bestanden hat.
	 *
     */
    protected boolean reConnect() throws SQLException, javax.naming.NamingException {
        if (conDB == null) {
            log("reConnect: connection is null.", g_LOG_LEVEL_DEBUG);
            conDB = m_DB.getConnection();
        }
        if (conDB.isClosed()) {
            log("reConnect: connection is closed.", g_LOG_LEVEL_DEBUG);
            conDB = m_DB.getConnection();
        }
        isConnected = (conDB != null);
        return isConnected;	
    }
	
    /**
     * Schließt die Verbindung zur Datenbank
     *
     * @throws SQLException 
  	 *
     */
    public void close() throws SQLException {
        if ((conDB != null) && (!conDB.isClosed())) {
            conDB.close();
        }
        conDB = null;
        isConnected = false;
    }

    /**
     * Zugriff auf die Datenbankverbindung; ruft immer {@link #reConnect()} auf.
     *
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
     * Gibt ein ResultSet Objekt mit dem Ergebnis der Abfrage aus, die in
     * sqlQuery sepezifiziert wurde. Beispiel
     *
     * ResultSet rStudierende = sqlQuery("select * from \"tblBdStudent\";");
     * while(rStudierende.next()){
     * out.write(rStudierende.getString("strStudentNachname"); }
     *
     * ACHTUNG: auf SQL Injection muss dann bei der Konstruktion von sqlString
     * geachtet werden.
     *
     * @returns ResultSet mit dem Ergebnis der spezifizierten Abfrage
     * @param strQuery: Select-Abfrage in PostgreSQL-gültigem Syntax.
     * @throws Exception Verbindung zum Datenbankserver fehlgeschlagen
     *
     */
    public ResultSet sqlQuery(String strQuery) throws Exception {
        getConnection().clearWarnings();
        return getConnection().createStatement().executeQuery(strQuery);
    }

    public Iterator sqlQuerySafe(String sQuery) throws Exception {
        Statement stmt = getConnection().createStatement();
        ResultSet rTmp = stmt.executeQuery(sQuery);
        RowSetDynaClass rSwap_d = new RowSetDynaClass(rTmp);
        try {
            rTmp.close();
            stmt.close();
        } catch (Exception eWhateverIgnore) {
        }
        return rSwap_d.getRows().iterator();
    }
  	
    public ResultSetSHJ sqlQuerySHJ(String sQuery) throws Exception {
        Statement stmt = getConnection().createStatement();
        ResultSet rTmp = stmt.executeQuery(sQuery);
        ResultSetSHJ oTmp = new ResultSetSHJ(rTmp);
        try {
            rTmp.close();
            stmt.close();
        } catch (Exception eWhateverIgnore) {
        }
        return oTmp;
    }
  	
    /**
     * Kapselung einer StoredProc in der aktiven Datenbank (UP_db)
     * Beispiel:
     * <Code>.execProc("[StoredProcName]", "ParamType1", "ParamValue1", 
     * "ParamType2", "ParamValue2", ..., "ParamTypeN", "ParamValueN")</Code>, where
     *
     * - "name" ist der Name der Stored Procedure,
     * - "ParamType[i]" ist "int", "long", "String", oder "boolean" and
     * - "ParamValue[i]" ist der Wert des Parameters
     *
     * Zum Bsp. kann man folgende StoredProc:
     *    <Code>FUNCTION setStatus(lFeeUsageUniqueID bigint, iStatus integer, 
     * sInfo varchar, sUserID varchar)</Code>
     *
     * so aufrufen:
     *
     * <Code>.execProc("setStatus", "long", "1045", "int", "23", "String", 
     * "Neuer Status gesetzt", "String", "g96");</Code>
     *
     * Fehlgeschlagene Typenkonvertierungen (<Code>"long", "23pp"</Code>) 
     * oder unbekannte Typen (<Code>"Time", "10:22"</Code>) führen zu Fehlern.
     *
     * @param sCommandInfo parameter list
     * @throws SQLException
     * @throws NamingException
     * @throws ParseException
     */
    public void execProc(String... sCommandInfo) throws SQLException, NamingException, ParseException {
        int ij = 1;

        CallableStatement cstm = getConnection().prepareCall("{call " + sCommandInfo[0] + "(" + getArgList((sCommandInfo.length - 1) / 2) + ")}");
        for (int ii = 1; ii < sCommandInfo.length; ii++) {
            if (sCommandInfo[ii].equals("int")) {
                cstm.setInt(ij++, Integer.parseInt(sCommandInfo[++ii]));
            } else if (sCommandInfo[ii].equals("long")) {
                cstm.setLong(ij++, Long.parseLong(sCommandInfo[++ii]));
            } else if (sCommandInfo[ii].equals("String")) {
                cstm.setString(ij++, sCommandInfo[++ii]);
            } else if (sCommandInfo[ii].equals("boolean")) {
                cstm.setBoolean(ij++, Boolean.parseBoolean(sCommandInfo[++ii]));
            } else if (sCommandInfo[ii].equals("Date")) {
                cstm.setDate(ij++, new java.sql.Date(g_ISO_DATE_FORMAT.parse(sCommandInfo[++ii]).getTime()));
            } else {
                throw new SQLException("Der Typ '" + sCommandInfo[ii] + "' wird von der Methode 'execProc' nicht unterstützt.");
            }
        }
        cstm.execute();
    }
	
    /**
     * Example: <Code>getArgList(4)</Code> will return: "?,?,?,?"
     *
     * @param length
     * @return String consisting of "?," exactly <Code>length</Code> times.
     */
    private String getArgList(int length) {
        String sReturn = "";
        for (int ii = 0; ii < length; ii++) {
            sReturn += "?,";
        }
        return sReturn.substring(0, sReturn.length() - 1);
    }
  	
    /**
     * @param strQuery
     * @return true bei Erfolg
     * @throws Exception wenn {@link #setAutoCommit(boolean)} nicht auf wahr
     * @deprecated 
     * gesetzt werden kann.
     */
    public boolean sqlExeSingle(String strQuery) throws Exception {

        boolean blnReturn = true;
        int intPos = 0;

        try {
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

        } catch (SQLException e) {
            blnReturn = false;
        } catch (Exception e) {
            blnReturn = false;
        }

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
     * Führt SQL-Befehl aus. Beispiel:
     * 
     * ACHTUNG: Keine Kontrolle über SQL-Injection.
     * <Code>
     * if true=sqlExe("update \"tblBdStudent\" set \"blnStudentFemale\"='t';"){
     * 	out.write("Alle Studierenden sind jetzt weiblich");
     * }
     * </Code>
     * @param strQuery SQL Abfrage ohne Ergebnisse (auch DDL).
     * @throws Exception 
     * @return wahr, wenn der Befehl ausgef�hrt werden konnte.
     * false if an error occurred
     **/
    public boolean sqlExe(String strQuery) throws Exception {
        return (sqlExeCount(strQuery) != -1);
    }//end of "sqlExe"

    /**
     * Führt SQL-Befehl aus und liefert Anzahl der betroffenen Datensätze
     * zurück. Beispiel:
     * <Code>
     * out.write("Anzahl Studierende: " + sqlExe("delete from \"tblBdStudent\";"));
     * </Code> Siehe auch {@link #sqlExe(String)} und
     * {@link #sqlExeSingle(String)}
     *
     * ACHTUNG: Keine Kontrolle über SQL-Injection.
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
        } catch (Exception e3) {}

        // April 11 2006
        try {
            if (!m_bKeepConnectionOpen) {
                conDB.close();
            }
        } catch (Exception eClo) {}
        return intReturn;
    }
	
    /**
     * Transaktion ausf�hren.
     * @throws SQLException DB error
     **/
    public void commit() throws SQLException{
       if (conDB!=null) conDB.commit();
    }

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
    }


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
     * Schließe DB-Verbindung um Resourcen 
     * freizugeben; die Verwaltung der Resourcen 
     * ist in Tomcat nicht immer ganz 
     * klar.
     *
     * @throws SQLException
  	 *
     */
    public void disconnect() throws SQLException {
        if ((conDB != null) && (!conDB.isClosed())) {
            conDB.close();
        }
        conDB = null;
        isConnected = false;
    }
  	
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
     * Vermeiden von NullPointerException: ist der 
     * String <Code>str_IN</Code> null, wird eine 
     * leere Zeichenfolge zurückgegeben.
     * 
     * Sonst wird der übergebene String unverändert 
     * wieder ausgegeben.
     * @param str_IN 
	 *
     */
    public static String normalize(String str_IN) {
        return (str_IN == null) ? "" : str_IN;
    }
	
    /**
     * Liefert einen JSON-tauglichen String, also 
     * - mit Leerzeichen anstatt 'newline' oder 'carriage return' oder tab, 
     * - mit Slash (/) anstatt Backslash (\)
     * - mit \" anstatt "
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
     * Gibt es den Parameter <Code>strParam_IN</Code> im Request <Code>r</Code>?
     *
     * @version > 1.7i
	 *
     */
    public boolean requestContains(HttpServletRequest r, String strParam_IN) {

        boolean blnReturn = false;
        Enumeration pn = r.getParameterNames();

        //check if parameter strParam_IN is part of r
        for (; pn.hasMoreElements();) {
            if (((String) pn.nextElement()).equals(strParam_IN)) {
                blnReturn = true;
                break;
            }
        }
        return blnReturn;
    }

	
    // ------------------------------ ------------------------------
    // ------------------------------ ------------------------------
    // 12.	"U N I N T E R E S T I N G"    C O D E
    // ------------------------------ ------------------------------
    // ------------------------------ ------------------------------

    /**
     * Per Voreinstellung 'wahr'
     */
    public void setAutoCommit(boolean blnAutoCommit) throws Exception {
        getConnection().setAutoCommit(blnAutoCommit);
    }//end of "setAutoCommit"

    /**
     * Einfacher getter.
     */
    public PreparedStatement prepareStatement(String strSQL) throws SQLException, NamingException {
        return getConnection().prepareStatement(strSQL);
    }


    // ------------------------------ ------------------------------
    // ------------------------------ ------------------------------
    // 13.	C O N S T R U C T O R
    // ------------------------------ ------------------------------
    // ------------------------------ ------------------------------
	
    /**
     * Setzt das heutige Datum und die Datenbank-Konfiguration.
 	 *
     */
    public shjCore() {
        g_TODAY = new Date(new java.util.Date().getTime());
        try {
            m_ctx = new InitialContext();
            m_DB = (DataSource) m_ctx.lookup("java:comp/env/jdbc/UP_db");
        } catch (NamingException e) {
            m_strDebug += e.toString();
        }

    }

    @Override
    public void valueBound(HttpSessionBindingEvent event) {
    }

    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
    }
}
