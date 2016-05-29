package de.shj.UP.util;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import de.shj.UP.data.shjCore;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * @author hj, July 2006
 * 
 * Use this class if you want to access SignUp-DB 
 * without Tomcat's connection pool.
 *
 */
public class SignUpLocalConnector implements DataSource  {

	private static final long serialVersionUID = -5145456632198901921L;
	private 	Connection 	conDBLocal;
	private		boolean		isConnectedLocal=false;
	private     boolean		bFirstRun = true;
		
	public String lookUp(String strField_IN, String strTable_IN, String strCrit_IN){

		String 	strReturn	= "";
		String 	strSQL		= "";
		ResultSet rst;
		
		strSQL = "SELECT \"" + strField_IN + "\" FROM \"" + strTable_IN +
		"\" WHERE (" + strCrit_IN + ");";
		try{
			
			rst=sqlQueryLocal(strSQL);
			if(rst.next()){
				strReturn = rst.getString(strField_IN);
			}else{
				strReturn = "#NO_RESULT";
			}
			
			rst.close();
		}catch(Exception eLookUp){
			strReturn = "#Error: Couldn't execute query: " + eLookUp.toString();
		}
		
		return strReturn;
	}
	
	/**
	 * @see com.shj.signUp.data.shjCore#extConnect(DataSource)
	 * @return
	 * @throws Exception
	 */
	public DataSource getDataSource() throws Exception{
		//if(!isConnectedLocal) connectLocal();
		connectLocal();
	  	  //check if connexion's okay:
	    if((!isConnectedLocal)||(conDBLocal==null)) throw new Exception(String.valueOf(shjCore.ERR_SIGNUP_NO_CONNECTION_TO_DB));
	  	  
		return this;
	}
	
	/**
	 * The http-server uses a broker-mechanism for its database access;
	 * since this class is used with 'DataInput' as a standalone thing, 
	 * it creates a seperate pipeline to the database.
	 * @return true, if connection established
	 * @throws Exception 
	 */
	public boolean connectLocal() throws Exception{
    	if(conDBLocal!=null){
    		if(!conDBLocal.isClosed()) return false;
    	}
		// conDBLocal=null;
		Class.forName("org.postgresql.Driver");
		
    	conDBLocal=java.sql.DriverManager.getConnection("jdbc:postgresql:SignUpXP", "postgres", "galavastik");
    	isConnectedLocal=(conDBLocal!=null);
    	
    	if(!isConnectedLocal) throw new Exception("Hey, not locally connected for some reason.");
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
	  connectLocal();

  	  //check if connexion's okay:
  	  if((!isConnectedLocal)||(conDBLocal==null)) throw new Exception(String.valueOf(shjCore.ERR_SIGNUP_NO_CONNECTION_TO_DB));

  	  //check if query not empty:
  	  if((strQuery==null)||(strQuery.equals(""))) throw new Exception(String.valueOf(shjCore.ERR_SIGNUP_EMPTY_SQL));

  	  if(conDBLocal.isClosed()) throw new Exception("....here we go, conDBLocal is closed...");
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
		connectLocal();

		try{
			Statement stmSqlExe = conDBLocal.createStatement();
			stmSqlExe.execute(strQuery);
			blnReturn=true;
		}catch(Exception e3){
			blnReturn=false;
		}

		return blnReturn;
	}//end of "sqlExe"

	
///////////////////////////////////////////
// Satisfy interface DataSource	
///////////////////////////////////////////	
	
	/* (non-Javadoc)
	 * @see javax.sql.DataSource#getConnection()
	 */
	public Connection getConnection(){
		try {
			connectLocal();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Error 1");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Error 2");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Error 3");
		}
		return conDBLocal;
	}
	
	
	public Connection getConnection(String username,
            String password) throws SQLException{
		return null;
	}
	
	public int getLoginTimeout() throws SQLException{
		return 0;
	}
	
	public PrintWriter getLogWriter() throws SQLException{
		return null;
	}
	
	public void setLoginTimeout(int seconds) throws SQLException{
		//void
	}
	
	public void setLogWriter(PrintWriter out) throws SQLException{
		// void
	}
	
	
	/**
	 * Empty constructor
	 */
	public SignUpLocalConnector(){}

	public boolean isWrapperFor(Class arg0) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public Object unwrap(Class arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
