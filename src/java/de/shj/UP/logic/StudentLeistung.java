/*
 * Software-Developement H. Jakubzik, Licence Version 1.0
 *
 *
 * Copyright (c) 2003 Heiko Jakubzik.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by
 *        H. Jakubzik (http:////www.heiko-jakubzik.de//)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "shj" and "SignUp" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact heiko.jakubzik@t-online.de.
 *
 * 5. Products derived from this software may not be called "e~vv,"
 *    nor may "e~vv" appear in their name, without prior written
 *    permission of H. Jakubzik.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
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
 * code structure:
 *
 * 1. declarations											line	86
 *
 * 2. protected properties									line	96
 *
 * 2a.public properties										line	110
 *
 * 3. public methods										line 	132
 *
 * 4. constructors											line	226
 *
 * @version 5-19-01 (hand-coded)
 * change date              change author           change description
 * Apr 27, 2004				h. jakubzik				Creation.
 * Apr 28, 2004				h. jakubzik				Debug.
 * Jun 15, 2004				h. jakubzik				wasCommitment
 * Aug 28, 2005				h. jakubzik				getLeistung, getStudent, getNote
 * Apr 18, 2006				h. jakubzik				swapStudentLeistungPruefung()
 * Aug 15, 2006				h. jakubzik				loadCourse: now checks if the loaded course really
 * 													exists (and is not full of null-values).
 */
package de.shj.UP.logic;

import java.sql.ResultSet;
import java.sql.Time;

import de.shj.UP.data.KvvArchiv;
import de.shj.UP.data.Leistung;
import de.shj.UP.data.Note;
import de.shj.UP.data.Student;

/**
 * This class is just a container to init the StudentXLeistung with
 * course-data from a course in current table or archive.
 **/
public class StudentLeistung extends de.shj.UP.data.StudentXLeistung{


	private static final long serialVersionUID = 4165762863494689626L;
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 1.   D E C L A R A T I O N S
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------

	public String m_strDebug;
	private long m_lngSeminarID2;		// Aug 29,2005. Why there is an extra SeminarID in this class, I do not know.

	private boolean m_blnArchive	= false;
	private boolean m_blnCurrent	= false;
	private Object  m_objKurs		= null;
	private Leistung m_objLeistung	= null;
	private Student m_objStudent	= null;
	private Note	m_objNote		= null;

	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 2.   P R O T E C T E D  P R O P E R T I E S
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------

	/**
	 * Was this object initialized with a course from the archive?
	 * @return true, if .loadCourse was called and the current object 
	 * initialized with a course from the archive.
	 */
	public boolean isArchivedCourse(){
		return this.m_blnArchive;
	}
	
	/**
	 * Was this object initialized with a course from the current catalog?
	 * @return true, if .loadCourse was called and this object 
	 * initialized with a current course.
	 */
	public boolean isCurrentCourse(){
		return this.m_blnCurrent;
	}
	
	/**
	 * Returns a KvvArchiv object if loadCourse was called with blnArchive=true, 
	 * a Kvv-object if .loadCourse was called with blnArchive=false.
	 * @return Kurs or KvvArchiv-object, or null. Call .loadCourse first.
	 * @see de.shj.UP.data.Kurs
	 * @see de.shj.UP.data.KvvArchiv
	 * @see #loadCourse(long, boolean)
	 */
	public Object getKursobjekt(){
		if(this.isCurrentCourse() || this.isArchivedCourse()) 	return this.m_objKurs;
		return null;
	}
	
	/**
	 * @return Leistung
	 * @throws Exception
	 */
	public Leistung getLeistung() throws Exception{
		if(m_objLeistung==null) m_objLeistung = new Leistung(getSeminarID(), getLeistungsID());
		return m_objLeistung;
	}
	
	/**
	 * @return Student
	 * @throws Exception
	 */
	public Student getStudent() throws Exception{
		if(m_objStudent==null){
			m_objStudent = new Student();
			ResultSet rst = sqlQuery("select * from \"tblBdStudent\" where \"strMatrikelnummer\"='" + getMatrikelnummer() + "';");
			rst.next();
			m_objStudent.initByRst(rst);
			rst.close();
			rst=null;
		}
		return m_objStudent;
	}
	
	/**
	 * @return Note
	 * @throws Exception
	 */
	public Note getNote() throws Exception{
		if(m_objNote==null) m_objNote=new Note(getSeminarID(), getNoteID());
		return m_objNote;
	}
	
	/**
	 * Set SeminarID in StudentLeistung. This does <b>not set the 
	 * SeminarID in the underlying StudentXLeistung-object</b>!
	 * @see com.shj.signUp.data.StudentXLeistung#setSdSeminarID(long)
	 * @param lngSeminarID
	 */
	public void setSeminarID(long lngSeminarID){
		this.m_lngSeminarID2=lngSeminarID;
	}

	protected long getSeminarID(){
		return this.m_lngSeminarID2;
	}

	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 2a.   P U B L I C  P R O P E R T I E S
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------


	/**
	 *	In many bachelor and master studies, students _must_ file
	 *  a commitment (Klausuranmeldung) to get a credit that counts.
	 *  In SignUp, the criterion to decide whether a specific credit
	 *  (as stored in StudentXLeistung) was generated by a commitment,
	 *  is the existence of a commitment-date, a "Antragdatum".
	 *  This property reflects if there is a commitment date or not.
	 *  Please note that the flag 'Klausuranmeldung' is something
	 *  entirely different: a Klausuranmeldung is not yet a credit.
	 *  @returns true, if this credit was generated by a commitment;
	 *  false, if it was not.
	 **/
	 public boolean wasCommitment(){
		 return (!( this.getStudentLeistungAntragdatum() == null ));
	 }

	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 	3.   P U B L I C  M E T H O D S
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	 /**
	 * @param lngKursID
	 * @param blnArchiv
	 * @return true, if there is one course with this Id in this seminar.
	 */
	public boolean kursExists(long lngKursID, boolean blnArchiv){
            boolean bReturn = false;
            try{
                if(blnArchiv){                 
                    ResultSet rTmp = sqlQuery("select \"lngID\" from \"tblBdKvvArchiv\" where " +
                       "\"lngSdSeminarID\"=" + getSdSeminarID() + " and \"lngID\"=" + lngKursID);
                    bReturn = rTmp.next();
                    rTmp.close();
                }else{
                    ResultSet rTmp = sqlQuery("select \"lngKursID\" from \"tblBdKurs\" where " +
                       "\"lngSdSeminarID\"=" + getSdSeminarID() + " and \"lngKursID\"=" + lngKursID);
                    bReturn = rTmp.next();
                    rTmp.close();
                }
            }catch(Exception e){}
            return bReturn;
	 }

	/**
	 * This method initializes the underlying 'StudentXLeistung'-data object and
	 * sets all values to the course identified by the parameters handed in.<br />
	 * Please note that some values cannot be set from the archive (because they are not
	 * stored there). These values are: the 'UnivISID' ("not in archive"), 'KursAnmeldung' ("not in archive"),
	 * 'KursStunden', 'KursLastChange', 'KursScheinanmeldungBis', 'KursScheinanmeldungVon', 'KursScheinanmeldungErlaubt',
	 * 'KursRaumExtern1', 'KursRaumExtern2' (all default: null, 0 or empty String).<br /><br />
	 * Interestlingly, a course from the archive is initialized with just its ID -- not the SeminarID.
	 * So this method can also be used to issue a credit based on a course of another seminar.
	 * Only the LeistungID will have to be set separately, of course.<br />
	 * By default, the course's "Custom1"-Tag is copied to the StudentLeistung's Custom2-Tag.<br />
	 * This is a little odd, and can be switched off using @link #setCopyCustomEnabled(boolean)
	 * @see #isCopyCustomEnabled()
	 * @return 'true' if the StudentXLeistung-object could be initialized, 'false' if not.
	 * @param lngKursID: Id of the course in 'tblBdKurs' (current semester), or Id in 'tblKvvArchiv'.
	 * @param blnArchiv: 'true' if the course is to be loaded from the archive.
	 **/
	public boolean loadCourse(long lngKursID, boolean blnArchiv){

		try{
		  if(!blnArchiv){
    		de.shj.UP.data.Kurs k	= new de.shj.UP.data.Kurs(this.getSdSeminarID(), lngKursID);
			
    		// only load the details if you really found the/a course.
    		// It can happen that after a semester-change, when old applications 
    		// are loaded, there is no flag "archive".
    		// In that case, we use the values that are already in StudentXLeistung.
    		if(kursExists(lngKursID, blnArchiv)){
    			this.setKlausuranmeldungKursID      ( lngKursID                           );
    			this.setSLKursUnivISID		        ( k.getKursUnivISID()                 );
    			this.setSLKursTag			        ( k.getKursTag()	                  );
    			this.setSLKursBeginn		        ( k.getKursBeginn()                   );
    			this.setSLKursEnde			        ( k.getKursEnde()	                  );
    			this.setSLKursRaum			        ( k.getKursRaum()	                  );
    			this.setSLKursTag2			        ( k.getKursTag2()	                  );
    			this.setSLKursBeginn2		        ( k.getKursBeginn2()                  );
    			this.setSLKursEnde2			        ( k.getKursEnde2()                    );
    			this.setSLKursRaum2			        ( k.getKursRaum2()	                  );
    			this.setSLKursTitel			        ( k.getKursTitel()	                  );
    			this.setSLKursTitel_en		        ( k.getKursTitel_en()                 );
    			this.setSLKursBeschreibung	        ( k.getKursBeschreibung()             );
    			this.setSLKursBeschreibung_en       ( k.getKursBeschreibung_en()          );
    			this.setSLKursLiteratur		        ( k.getKursLiteratur()	              );
    			this.setSLKursZusatz		        ( k.getKursZusatz()		              );
    			this.setSLKursAnmeldung		        ( k.getKursAnmeldung()	              );
    			this.setSLKursVoraussetzung         ( k.getKursVoraussetzung()            );
    			this.setSLKursSchein		        ( k.getKursSchein()		              );
    			this.setSLKursEinordnung	        ( k.getKursEinordnung()               );
    			this.setSLKursStunden		        ( k.getKursStunden()		          );
    			if(k.getKursLastChange()!=null)	this.setSLKursLastChange	        ( g_TIMESTAMT_FORMAT.format(k.getKursLastChange()	              ));
    			this.setSLKursScheinanmeldungBis    ( k.getKursScheinanmeldungBis()       );
    			this.setSLKursScheinanmeldungVon    ( k.getKursScheinanmeldungVon()       );
    			this.setSLKursScheinanmeldungErlaubt( k.getKursScheinanmeldungErlaubt()   );
    			this.setSLKursTerminFreitext	    ( k.getKursTerminFreitext()			  );
    			this.setSLKursTeilnehmer		    ( k.getKursTeilnehmer()				  );
    			this.setSLKursRaumExtern1		    ( k.getKursRaumExtern1() 			  );
    			this.setSLKursRaumExtern2		    ( k.getKursRaumExtern2()			  );
    		}
    		this.m_objKurs = k;
    		k.close();
    		this.m_blnCurrent=true;
	    }else{
	    	KvvArchiv  k = new KvvArchiv( lngKursID );
	    	if(kursExists(lngKursID, blnArchiv)){
	    		this.setKlausuranmeldungKursID      ( lngKursID                           );
	    		this.setSLKursUnivISID		        ( "Not in archive"                    );
	    		this.setSLKursTag			        ( k.getKvvArchivKursTag()	          );
	    		this.setSLKursBeginn		        ( k.getKvvArchivKursBeginn()==null ? null : new java.sql.Time( g_TIME_FORMAT.parse(k.getKvvArchivKursBeginn()).getTime()));
	    		this.setSLKursEnde			        ( k.getKvvArchivKursEnde()==null ? null : new java.sql.Time( g_TIME_FORMAT.parse(k.getKvvArchivKursEnde()	          ).getTime()));
	    		this.setSLKursRaum			        ( k.getKvvArchivKursRaum()	          );
	    		this.setSLKursTag2			        ( k.getKvvArchivKursTag2()	          );
	    		this.setSLKursBeginn2		        ( k.getKvvArchivKursBeginn2()==null ? null : new java.sql.Time( g_TIME_FORMAT.parse(k.getKvvArchivKursBeginn2()         ).getTime()));
	    		this.setSLKursEnde2			        ( k.getKvvArchivKursEnde2()==null ? null : new java.sql.Time( g_TIME_FORMAT.parse(k.getKvvArchivKursEnde2()           ).getTime()));
	    		this.setSLKursRaum2			        ( k.getKvvArchivKursKursRaum2()       );
	    		this.setSLKursTitel			        ( k.getKvvArchivKurstitel()	          );
	    		this.setSLKursTitel_en		        ( k.getKvvArchivKurstitel_en()        );
	    		this.setSLKursBeschreibung	        ( k.getKvvArchivKursBeschreibung()    );
	    		this.setSLKursBeschreibung_en       ( k.getKvvArchivKursBeschreibung_en() );
	    		this.setSLKursLiteratur		        ( k.getKvvArchivKursLiteratur()	      );
	    		this.setSLKursZusatz		        ( k.getKvvArchivKursZusatz()		  );
	    		this.setSLKursAnmeldung		        ( "Not in archive"	                  );
	    		this.setSLKursVoraussetzung         ( k.getKvvArchivKursVoraussetzung()   );
	    		this.setSLKursSchein		        ( k.getKvvArchivKursSchein()		  );
	    		this.setSLKursEinordnung	        ( k.getKvvArchivKursEinordnung()      );
//	    		this.setSLKursStunden		        ( 0	          );
	    		this.setSLKursLastChange	        ( null	              )	             ;
	    		this.setSLKursScheinanmeldungBis    ( null       );
	    		this.setSLKursScheinanmeldungVon    ( null       );
//	    		this.setSLKursScheinanmeldungErlaubt(k.getKursScheinanmeldungErlaubt()    );
	    		this.setSLKursTerminFreitext	    ( k.getKvvArchivKursTerminFreitext()  );
	    		this.setSLKursTeilnehmer		    ( k.getKvvArchivKursTeilnehmer()	  );
//	    		this.setSLKursRaumExtern1		    ( k.getKursRaumExtern1() 			  );
//	    		this.setSLKursRaumExtern2		    ( k.getSLKursRaumExtern2()			  );
	    	}
    		this.m_objKurs 		= k;
    		this.m_blnArchive 	= true;
    		k.close();
	    }
	  }catch(Exception eInitWrong){m_strDebug = eInitWrong.toString();System.out.println(m_strDebug);return false;}

	  return true;
	}

	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 	4.   C O N S T R U C T O R
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------

	/**
	 * @return
	 * @throws Exception
	 */
	public long getNextLeistungCount() throws Exception {
            long lReturn = 0;
            ResultSet rTmp = sqlQuery("select max(\"lngStudentLeistungCount\")+1 as \"nextid\" from \"tblBdStudentXLeistung\" where " +
                    "\"lngLeistungsID\"=" + this.getLeistungsID() + " AND " +
                    "\"strMatrikelnummer\"='" + Long.parseLong(this.getMatrikelnummer()) + "' AND " + 
                    "\"lngSdSeminarID\"=" + this.getSeminarID());
            rTmp.next();
            try{
                lReturn = rTmp.getLong("nextid");
                rTmp.close();
            }catch(Exception eNull){}
            return lReturn;
	}

	// empty constructor.
	public StudentLeistung(){
	}

	/**
	 * This constructor just passes construction over to the underlying data.StudentXLeistung-object.
	 **/
	public StudentLeistung(long lngSdSeminarID, String strMatrikelnummer, long lngLeistungsID, long lngStudentLeistungCount) throws Exception{
		this.init(lngSdSeminarID, strMatrikelnummer, lngLeistungsID, lngStudentLeistungCount);
		m_lngSeminarID2 = lngSdSeminarID;
	}
}