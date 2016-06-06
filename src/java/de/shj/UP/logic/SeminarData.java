/*
 * Created on 06.05.2005
 *
 * 
 */
package de.shj.UP.logic;
import java.sql.ResultSet;
import de.shj.UP.data.Seminar;

/**
 * @author h. jakubzik
 *
 * Needed to implement the method 'setAllCommitments' halfway properly. 
 * 
 */
public class SeminarData extends Seminar {

	private static final long serialVersionUID = -3209478629400704006L;
	private static long m_lngKURSTYP_DEFAULT	= 0;
	
	
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
