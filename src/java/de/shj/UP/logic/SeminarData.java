/*
 * Created on 06.05.2005
 *
 * 
 */
package de.shj.UP.logic;
import java.sql.ResultSet;
import de.shj.UP.data.Seminar;
import static de.shj.UP.data.shjCore.g_ISO_DATE_FORMAT;
import java.sql.Date;

/**
 * @author h. jakubzik
 *
 * Needed to implement the method 'setAllCommitments' halfway properly. 
 * 
 */
public class SeminarData extends Seminar {

	private static final long serialVersionUID = -3209478629400704006L;
	private static long m_lngKURSTYP_DEFAULT	= 0;
	private String m_sLastZUVUpdate = null;
        private Date m_dLastZUVUpdate = null;
        
	/**
         * Betrifft Schnittstelle zu anderen universitären 
         * Datensystemen: wenn die Studierendendaten zentral 
         * in einer anderen Datenbank verwaltet werden, muss es 
         * einen Daten-Import zu U:P geben.
         * 
         * Die Importierten Daten werden dann mit dem Datum 
         * des Imports markiert.
         * 
         * So kann man veraltete Daten erkennen (deren Import-
         * Datum ist älter als das anderer Studierender).
         * 
         * Diese Methode liefert das letzte Importdatum, in dem 
         * es das aktuellste Importdatum aller Datensätze 
         * heraussucht.
	 * @return Datum, zu dem Studierendendatensätze generell zuletzt 
         * von der Zentralen Universitätsverwaltung ("ZUV") als 
         * aktiv bestätigt wurden.
	 * @throws Exception
	 */
	public Date getLastZUVUpdate() throws Exception{
		if(m_sLastZUVUpdate == null) {
                    ResultSet rTmp = sqlQuery("select max(\"dtmStudentZUVUpdate\") as \"datum\" from \"tblBdStudent\";");
                    rTmp.next();
		    m_sLastZUVUpdate = rTmp.getString("datum");
		}
		if(m_dLastZUVUpdate==null) m_dLastZUVUpdate = new java.sql.Date(g_ISO_DATE_FORMAT.parse(m_sLastZUVUpdate).getTime());
		return m_dLastZUVUpdate;
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
