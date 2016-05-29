/*
 * Created on 06.01.2005
 * changes:
 * 07-Aug-2005	hj		implemented group-studentlist-method
 */
package de.shj.UP.logic;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.*;
import de.shj.UP.logic.StudentData;

/**
 * This is a utility class to display students in 
 * a chosen order. It iterates StudentData objects.
 * <Code>
 * </Code>
 * 
 * @author hj
 */
public class StudentList extends StudentData {

	private ResultSet 	m_rst			= null;
		
	private byte	    m_bytOrder			 = 0;
	
	private final byte	m_bytNAME_ASC		 = 1;
	private final byte	m_bytMATRIKEL_ASC	 = 2;
	private final byte	m_bytSTUDYCOURSE_ASC = 3;
	private final byte	m_bytVORNAME_ASC	 = 10;
	
	private final byte	m_bytNAME_DESC		 = 4;
	private final byte	m_bytMATRIKEL_DESC	 = 5;
	private final byte	m_bytSTUDYCOURSE_DESC= 6;
	private final byte	m_bytVORNAME_DESC	 = 11;
	
	private String m_strOrderSql							= "";
	
	/**
	 * Delete any order settings.
	 */
	public void setOrderNone(){
		m_strOrderSql = "";
		m_bytOrder	  = 0;
	}
	
	/**<pre>
	 * Depending on the ResultSet or SQLQuery you initialize 
	 * this object with, you may want to access columns that 
	 * are not explicit properties of a student.
	 * 
	 * For this purpose, you can use the 'getString' method. 
	 * If the column does not exist, or if another SQLException
	 * occurs, '#' is returned.
	 * </pre>
	 * @param strRSTColumnName_IN name of the column you want results from
	 * @return content of this column (String value).
	 */
	public String getString(String strRSTColumnName_IN){
		String strReturn	= "#";
		try {
			strReturn = m_rst.getString(strRSTColumnName_IN);
		} catch (SQLException e) {}
		return strReturn;
	}
	
	/**
	 * @return parameter list like <Code>para1=x&para2=y</Code> to initialize this bean with an ordering by name
	 */
	public String getLinkOrderByName(){
		return  "order=" + ((m_bytOrder==m_bytNAME_ASC) ? m_bytNAME_DESC : m_bytNAME_ASC);
	}

	/**
	 * @return parameter list like <Code>para1=x&para2=y</Code> to initialize this bean with an ordering by fistname
	 */
	public String getLinkOrderByFirstName(){
		return  "order=" + ((m_bytOrder==m_bytVORNAME_ASC) ? m_bytVORNAME_DESC : m_bytVORNAME_ASC);
	}	
	
	/**
	 * @return parameter list like <Code>para1=x&para2=y</Code> to initialize this bean with an ordering by Matrikelnummer
	 */
	public String getLinkOrderByMatrikel(){
		return  "order=" + ((m_bytOrder==m_bytMATRIKEL_ASC) ? m_bytMATRIKEL_DESC : m_bytMATRIKEL_ASC);
	}


	/**
	 * @return parameter list like <Code>para1=x&para2=y</Code> to initialize this bean with an ordering by study-course
	 */	
	public String getLinkOrderByStudyCourse(){
		return  "order=" + ((m_bytOrder==m_bytSTUDYCOURSE_ASC) ? m_bytSTUDYCOURSE_DESC : m_bytSTUDYCOURSE_ASC);
	}
	
	/**
	 * Sets the order of the internal list to LastName and FirstName of students ascending or descending, according to param.
	 * @param blnAscending true for ascending, false for descending.
	 */
	public void setOrderByName(boolean blnAscending){
		m_strOrderSql = "Order By \"strStudentNachname\" " + (blnAscending ? "ASC" : "DESC") + " , \"strStudentVorname\" " + (blnAscending ? "ASC" : "DESC");
		m_bytOrder	  = (blnAscending ? m_bytNAME_ASC : m_bytNAME_DESC);
	}

	/**
	 * Sets the order of the internal list to FirstName and LastName of students ascending or descending, according to param.
	 * @param blnAscending true for ascending, false for descending.
	 */
	public void setOrderByFirstName(boolean blnAscending){
		m_strOrderSql = "Order By \"strStudentVorname\" " + (blnAscending ? "ASC" : "DESC") + " , \"strStudentNachname\" " + (blnAscending ? "ASC" : "DESC");
		m_bytOrder	  = (blnAscending ? m_bytVORNAME_ASC : m_bytVORNAME_DESC);
	}
	
	/**
	 * Sets the order of the internal list to Matrikulation-number.
	 * @param blnAscending
	 */
	public void setOrderByMatrikel(boolean blnAscending){
		m_strOrderSql = "Order By \"strMatrikelnummer\" " + (blnAscending ? "ASC" : "DESC");
		m_bytOrder	  = (blnAscending ? m_bytMATRIKEL_ASC : m_bytMATRIKEL_DESC);
	}
	
	/**
	 * Sets the order of the internal list to study-course, last name and first name (this priority, ordering 
	 * refers to study-course only, names are always ascending).
	 * @param blnAscending
	 */
	public void setOrderByStudyCourse(boolean blnAscending){
		m_strOrderSql = "Order By \"lngStudentZUVZiel\" " + (blnAscending ? "ASC" : "DESC") + ", \"strStudentNachname\" ASC, \"strStudentVorname\" ASC";
		m_bytOrder	  = (blnAscending ? m_bytSTUDYCOURSE_ASC : m_bytSTUDYCOURSE_DESC);
	}

	
	/**
	 * Initializes next student. 
	 * @see #setOrder(byte, byte) for order in which students are iterated.
	 * @return true, if a next student was found, false otherwise.
	 * @throws Exception (connection to database-problem).
	 */
	public boolean nextStudent() throws Exception{
		
		if(m_rst==null)  return false;
		
		if(m_rst.next()){
			this.initByRst(m_rst);
			return true;
		}
		
		m_rst.close();
		m_rst=null;
		disconnect();
		return false;

	}
	
        // Neu März 2013: StudentSearchBean entsprechend erweitert
        public int getFachID() throws SQLException{
            return m_rst.getInt("intFachID");
        }
        
        // Neu März 2013: StudentSearchBean entsprechend erweitert
        public String getFach() throws SQLException{
            return m_rst.getString("strFachBeschreibung");
        }
        
	/* (non-Javadoc)
	 * Try to be nice to database-ressources.
	 * @see java.lang.Object#finalize()
	 */
	public void finalize(){
		try{
			m_rst.close();
			m_rst=null;
		}catch(Exception eAlreadyClosed){}
		try{
		  super.finalize();
		}catch(Exception eWhatever){}
	}
	
	/**<pre>
	 * Empty constructor for bean use (beans may then extend 
	 * this class).
	 * 
	 * Call init before using .next().
	 * </pre>
	 * @see #init(ResultSet)
	 * @see #init(String, HttpServletRequest)
	 */
	public StudentList(){}
	
	/**
	 * Initializes an iteration list with a ready-made ResultSet.
	 * Mostly it's better to initialize by SQL, since this leaves 
	 * options for ordering open.
	 * @param rst ResultSet containing "tblBdStudent.*" select
	 * @throws Exception
	 */
	public StudentList(ResultSet rst) throws Exception{
		init(rst);
	}

	/**
	 * @see #StudentList(ResultSet)
	 */
	protected void init(ResultSet rst) throws Exception{
		this.m_rst = rst;
	}

	/**<pre>
	 * Initializes the bean with an SQLQuery and a request that may 
	 * contain ordering information (parameters 'order=X').
	 * 
	 * If SQLQuery ends with a ";" character, the ordering information is 
	 * disregarded.
	 * 
	 * If SQLQuery ends otherwise, the ordering SQL is appended.
	 * 
	 * In both cases, 'next()' can be called, and student-objects 
	 * can then be accessed.
	 * </pre> 
	 * @param strSQLQuery SQL-Query that must contain "tblBdStudent.*"-select; otherwise an error occurs. If the query ends with a ";", no ordering information is appended.
	 * @param rOrderInfo may contain parameters 'order=x'.
	 * @throws Exception
	 */
	public StudentList(String strSQLQuery, HttpServletRequest rOrderingInformation) throws Exception{
		init(strSQLQuery, rOrderingInformation);
	}
	
	/**
	 * @see #StudentList(String, HttpServletRequest)
	 */
	protected void init(String strSQLQuery, HttpServletRequest rOrderInfo) throws Exception{
		setOrder(rOrderInfo);
		if(!strSQLQuery.endsWith(";")){
			if(m_strOrderSql.equals("")) strSQLQuery += " order by \"strStudentNachname\", \"strStudentVorname\";"; 
			strSQLQuery += " " + m_strOrderSql + ";";
		}
		init (sqlQuery(strSQLQuery));
	}
	
	/**
	 * @param r
	 */
	private void setOrder(HttpServletRequest r) {
		String strOrder		= normalize(r.getParameter("order") );
		
		if(strOrder.equals(String.valueOf(m_bytMATRIKEL_ASC))) 			setOrderByMatrikel	 ( true );
		else if(strOrder.equals(String.valueOf(m_bytMATRIKEL_DESC))) 	setOrderByMatrikel	 ( false );
		else if(strOrder.equals(String.valueOf(m_bytNAME_ASC))) 		setOrderByName 	  	 ( true );
		else if(strOrder.equals(String.valueOf(m_bytNAME_DESC))) 		setOrderByName 	  	 ( false );
		else if(strOrder.equals(String.valueOf(m_bytVORNAME_ASC))) 		setOrderByFirstName  ( true );
		else if(strOrder.equals(String.valueOf(m_bytVORNAME_DESC))) 	setOrderByFirstName  ( false );	
		else if(strOrder.equals(String.valueOf(m_bytSTUDYCOURSE_ASC))) 	setOrderByStudyCourse( true );
		else if(strOrder.equals(String.valueOf(m_bytSTUDYCOURSE_DESC))) setOrderByStudyCourse( false );
		
		else setOrderNone();
	}

}
