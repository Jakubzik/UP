/*
 * Created on 06.05.2005
 *
 * 
 */
package de.shj.UP.logic;

import java.sql.ResultSet;

import org.w3c.dom.Node;

import de.shj.UP.data.Seminar;
import de.shj.UP.HTML.HtmlDate;

/**
 * @author h. jakubzik
 *
 * Needed to implement the method 'setAllCommitments' halfway properly. 
 * 
 */
public class SeminarData extends Seminar {

	private static final long serialVersionUID = -3209478629400704006L;
	private static long m_lngKURSTYP_DEFAULT	= 0;
	private Data m_data							= null;
	
	/**
	 * <pre>
	 * Dates and settings for Online-Applications.
	 * </pre>
	 * @return the data of current term
	 * @throws Exception
	 */
	public Data Data() throws Exception{
		if(m_data==null){
			m_data = new Data(getSeminarID());
		}
		return m_data;
	}
	
	/**
	 * This will set the period for commitments of all courses <br />
	 * (except those with the DefaultKurstyp 0 and those whose commitment start- 
	 * date is not null)<br />
	 * to the given dates.
	 * @param dtmStart When does commitment-period begin (inclusive)?
	 * @param dtmStop When does commitment-period stop (inclusive)?
	 * @param blnKeepExistingRecords Should existing commitment-periods be kept or overridden by the new values?
	 * @return 'true' for a success, 'false' for failure
	 * @throws Exception handling the query
	 */
	public boolean setAllCommitments(HtmlDate dtmStart, HtmlDate dtmStop, boolean blnKeepExistingRecords) throws Exception{
		return sqlExe("Update \"tblBdKurs\" set " +
				"\"dtmKursScheinanmeldungVon\"='" + dtmStart.getIsoDate() + "', " +
				"\"dtmKursScheinanmeldungBis\"='" + dtmStop.getIsoDate() + "', " +
				"\"blnKursScheinanmeldungErlaubt\"=" + getDBBoolRepresentation(true) + " " +
				"where (\"lngSdSeminarID\"=" + getSeminarID() + " " +
				( (blnKeepExistingRecords) ? " and \"dtmKursScheinanmeldungVon\" is null " : "") + 
				"and not exists(" +
				"select * from \"tblBdKursXKurstyp\" where " +
				"(\"lngSeminarID\"=\"tblBdKurs\".\"lngSdSeminarID\" and" +
				"	\"lngKursID\"=\"tblBdKurs\".\"lngKursID\" and " +
				"\"lngKurstypID\"=" + getKurstypDefault() + ")));");
	}
	
	/**
	 * This will set the period for commitments of all courses <br />
	 * of the given coursetype 
	 * (except those whose commitment start- 
	 * date is not null)<br />
	 * to the given dates.
	 * @param lngKurstypID commitment-period of which coursetype is to be set?
	 * @param dtmStart When does commitment-period begin (inclusive)?
	 * @param dtmStop When does commitment-period stop (inclusive)?
	 * @param blnKeepExisting Should existing commitment-periods be kept or overridden by the new values?
	 * @return 'true' for a success, 'false' for failure
	 * @throws Exception handling the query
	 */
	public boolean setAllCommitments(long lngKurstypID, HtmlDate dtmStart, HtmlDate dtmStop, boolean blnKeepExistingRecords) throws Exception{
		return sqlExe("Update \"tblBdKurs\" set " +
				"\"dtmKursScheinanmeldungVon\"='" + dtmStart.getIsoDate() + "', " +
				"\"dtmKursScheinanmeldungBis\"='" + dtmStop.getIsoDate() + "', " +
				"\"blnKursScheinanmeldungErlaubt\"=" + getDBBoolRepresentation(true) + " " +
				"where (\"lngSdSeminarID\"=" + getSeminarID() + " " +
				( (blnKeepExistingRecords) ? " and \"dtmKursScheinanmeldungVon\" is null " : "") + 
				"and exists(" +
				"select * from \"tblBdKursXKurstyp\" where " +
				"(\"lngSeminarID\"=\"tblBdKurs\".\"lngSdSeminarID\" and" +
				"	\"lngKursID\"=\"tblBdKurs\".\"lngKursID\" and " +
				"\"lngKurstypID\"=" + lngKurstypID + ")));");
	}
	
	// ==========================================
	// Transcript-Utility
	// ==========================================
	/**
	 * Liefert ein ResultSet mit alle interessanten Studierenden, die im betreffenden Zeitraum 
	 * eine Leistung (im richtigen Seminar) erbracht haben.<br />
	 * Eigentlich m??te hier noch der Filter <Code>tblSdModulXLeistung.blnModulLeistungTranskript=true</Code><br />
	 * eingebaut werden. Aus Performancegr?nden wurde das weggelassen.<br />
	 * Effekt: m?glicherweise werden leere Transkripte ausgedruckt.
	 * @param lngSeminarID: richtiges Seminar (=Institut)
	 * @param sem: betreffender Zeitraum
	 * @param blnNurEinSemester: Spezifizierung betreffender Zeitraum. 
	 *		  Bei 'Ja' wird nur das Semester <Code>sem</Code> gewertet, bei 
	 *		  'Nein' beginnt der "betreffende Zeitraum" bei 1970 und endet bei <Code>sem.Ende</Code>.
	 * @param strMatrikelnummer: ist nur *ein* Studierender "interessant", hier die Matrikelnr. angeben.
	 *		  Sollen Transkripte *aller* Studierender ausgegeben werden, einen leeren String ?bergeben.
	 **/
	public ResultSet getStudentsWithLeistung(SemesterUtil sem, String strMatrikelnummer, boolean blnNurEinSemester) throws Exception{
		boolean blnAllStudentsMode = (strMatrikelnummer.equals(""));
		String strSQLStudentsWithLeistung = "SELECT " +
		  "s.* " +
		"FROM \"tblBdStudent\" s  " +
		"WHERE " +
		  "(" +
	 	     "Exists( Select * from \"tblBdStudentXLeistung\" x Where " +
	  	   "(" +
		     "(x.\"lngSdSeminarID\"			= " + this.getSeminarID() + ")		AND " +
		     ( (blnNurEinSemester)  ? ("(x.\"dtmStudentLeistungAusstellungsd\" 	>= '" + sem.getSemesterStart() + "') AND ") : "") + 
		     "(x.\"dtmStudentLeistungAusstellungsd\" 	<= '" + sem.getSemesterEnd() + "') AND " +
		     ( (blnAllStudentsMode) ? "" : ("(s.\"strMatrikelnummer\" = '" + strMatrikelnummer + "')	AND ")) +
		     "(x.\"blnStudentLeistungValidiert\"		= 't'::bool)				AND " +
		     "(x.\"blnStudentLeistungKlausuranmeldung\"	= 'f'::bool) 			AND " +
		     "(s.\"strMatrikelnummer\"			= x.\"strMatrikelnummer\") 	 " +
		    ")" + 
		  ")) order by s.\"strStudentNachname\", s.\"strStudentVorname\", s.\"strMatrikelnummer\";";
		
		return sqlQuery(strSQLStudentsWithLeistung);
	}
	
	
	/**
	 * Empty (Bean usage). 
	 */
	public SeminarData() {
		super();
	}

	/**
	 * @param lngUniID
	 * @param lngFakultaetID
	 * @param lngSeminarID
	 * @throws Exception
	 */
	public SeminarData(long lngUniID, long lngFakultaetID, long lngSeminarID)
			throws Exception {
		super(lngUniID, lngFakultaetID, lngSeminarID);
	}

	/**
	 * @param rst
	 * @throws Exception
	 */
	public SeminarData(ResultSet rst) throws Exception {
		super(rst);
	}

	/**
	 * @param node
	 * @throws Exception
	 */
	public SeminarData(Node node) throws Exception {
		super(node);
	}


	/**
	 * The 'Kurstyp-Default' here means the coursetype 
	 * under which all courses are grouped that have 
	 * no coursetype yet. (E.g. on UnivIS-import). 
	 * Usually they have the ID 0.<br />
	 * .setAllCommitments() ignores all courses in this coursetype.
	 * @return Returns the Kurstyp-Default.
	 */
	public static long getKurstypDefault() {
		return m_lngKURSTYP_DEFAULT;
	}
	
	/**
	 * The 'Kurstyp-Default' here means the coursetype 
	 * under which all courses are grouped that have 
	 * no coursetype yet. (E.g. on UnivIS-import). 
	 * Usually they have the ID 0.<br />
	 * .setAllCommitments() ignores all courses in this coursetype.  
	 * @param kurstypDefault The Kurstyp-Default to set.
	 */
	public static void setKurstypDefault(long kurstypDefault) {
		m_lngKURSTYP_DEFAULT = kurstypDefault;
	}
}
