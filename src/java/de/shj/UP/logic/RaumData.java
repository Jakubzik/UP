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
 *
 * code-structure:
 *
 * 1. Html-Helpers
 *
 * 2. Boolean configuration-check methods
 *
 * 3. Query
 *
 * 4. Constructor
 *
 *
 * @version 0-00-01 (auto-coded)
 * change date              change author           change description
 * 18 Nov 2003              h. jakubzik             ported from version F75
 *
 */
package de.shj.UP.logic;

import java.sql.ResultSet;
import java.sql.Time;

/**
 *  Utility class for rooms in a institute. Rooms are generally regarded as ressources: they can be booked for a certain
 *  time, or they can be free at that time. There are a couple of differences here from general ressource planning: <ul>
 *<li>
 *  Since the rooms in university-institutes are usually booked on a weekly basis, and for a period of time, all
 *  appointments (bookings on ressources) are always interpreted as weekly bookings.
 *<li>
 *  Rooms are always booked for periods of time. "Free rooms" are rooms that are free in an entire given period.
 *<li>
 *  The original aim of this class is to help booking courses to rooms. When a teacher changes the day or time of a
 *  course (postpones it for example for half an hour), this specific course must be <b>disregarded</b> for ressource
 *  planning. Otherwise it would block its own room. This just as an explanation to 'course is disregarded' that can
 *  be found in several method descriptions below. Currently, <b>courses with IDs <= 0 are generally disregarded</b>, or,
 *  if a course ID is explicitly handed over as a String, <b>the escape-sequence is KursID = "ADD"</b>. This is awkward,
 *  but it has developed this way. Sorry.
 * </ul>
 **/
public class RaumData extends de.shj.UP.data.Raum{


	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 1.   H T M L - H e l p e r s
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------

   /**
    * @returns an option combo-box in Html (a String, to be precise, looking like this:<br /> '<option value="333">333</option>...' for
    * room 333)<br />. It contains all the rooms that seem to be free during the specified time.
    * @param k: A course. <b>Note:</b> if k's SeminarID is 0 or below, the course is disregarded in the room-combo.
    * k does not have to be stored in database, but must have these properties initialized: SdSeminarID, KursID, KursTag/2, KursBeginn/2, KursEnde/2, KursPlanungssemester/2.
    * @param blnSecond: do you want to get a room combo for day1 of this course or for day2?
    * @throws Exceptions if db-connection goes wrong.
    */
    public String getFreeRoomComboHtml(Kurs k, boolean blnSecond) throws Exception{

	    this.setSdSeminarID(k.getSdSeminarID());

	    String strKursID = ( (k.getKursID()<=0) ? "ADD" : String.valueOf(k.getKursID()) );
		String strRaum="";
		String strReturn="";
		ResultSet rst;

		if(!blnSecond){
	      rst=getFreeRooms(k.getSdSeminarID(), k.getKursTag(), k.getKursBeginn(), k.getKursEnde(), k.getKursPlanungssemester(), strKursID, "");
	    }else{
		  rst=getFreeRooms(k.getSdSeminarID(), k.getKursTag2(), k.getKursBeginn2(), k.getKursEnde2(), k.getKursPlanungssemester(), strKursID, "");
	    }

		// go through records and construct list of free rooms
		while(rst.next()){
			strRaum = rst.getString("strRaumBezeichnung");
			strReturn+="<option value=\"" + strRaum + "\">" + strRaum + "</option>\n";
		}
		rst.close();
		return strReturn;
   }

   /**
    * @returns an option combo-box in Html (a String, to be precise, looking like this:<br /> '<option value="333">333</option>...' for
    * room 333)<br />. It contains all the rooms that seem to be free during the specified time.
    * @param lngSeminarID: Id of the Seminar (only those rooms are regarded),
    * @param strTag: Day which to look up (#hack currently this is thought of as German days; if done consistently,
    * this method is unaffected by -- say Spanish -- dates, though).
    * @param strStart: Beginning of time-period to check (expected format is 'hh:mm,' this method makes no format-checks whatsoever).
    * @param strTop: End of time-period to check (hh:mm),
    * @param blnPlanung: Looking for rooms in the 'Planungssemester' (future semester) or in the current semester?
    * @param strKursID: Either a numeric KursID (in which case that course is disregarded from looking up), or "ADD," which means that no course is disregarded from lookup.
    * @throws Exceptions if db-connection goes wrong.
    */
   public String getFreeRoomComboHtml(long lngSeminarID, String strTag, Time strStart, Time strStop, boolean blnPlanung, String strKursID) throws Exception{

	    this.setSdSeminarID(lngSeminarID);

		String strRaum="";
		String strReturn="";

	    ResultSet rst=getFreeRooms(lngSeminarID, strTag, strStart, strStop, blnPlanung, strKursID, "");

		while(rst.next()){
			strRaum = rst.getString("strRaumBezeichnung");
			strReturn+="<option value=\"" + strRaum + "\">" + strRaum + "</option>\n";
		}
		rst.close();
		return strReturn;
	 }


	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 2.   B o o l e a n  c o n f i g u r a t i o n - c h e c k
	//		m e t h o d s (isLocalRoom, isFree etc.)
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------

   /**
    * The context of this little method is that rooms can be booked if, and only if they are (1) inside an institute
    * that has a room-table in "SignUp" and (2) they are booked for a SignUp-understandable period of time. (SignUp understands
    * only weekly bookings, or bookings twice a week. It does not understand single dates (Sept. 14, 8-10 pm) oder weekends etc.)
    * So if a room <i>outside</i> the Seminar is booked (or if a date/time is handed in that SignUp does not interpret), it needs
    * to be understood that this room outside does not have the same name of a room inside.
    * @returns 'true,' if the room specified in 'strRaumBezeichnung' is listed in the Seminar's room-table. 'False' otherwise.
    * @param lngSeminarID: which Seminar's room-table should SignUp search?
    * @param strRaumBezeichnung: name of the room the user wants to book as an 'external' room.
    **/
   public boolean isLocalRoom(long lngSeminarID, String strRaumBezeichnung){
	   if(strRaumBezeichnung==null) return false;
	   if(strRaumBezeichnung.equals("")) return false;
	   return (!(lookUp("lngSdSeminarID","tblSdRaum","\"lngSdSeminarID\"=" + lngSeminarID + " AND \"strRaumBezeichnung\"='" + getDBCleanString(strRaumBezeichnung) + "'")).equals("#NO_RESULT"));
   }

  /**
   *  @returns boolean value indicating whether the current room (this.getRaumBezeichnung()) of the specified seminar (lngSeminarID)
   *  is free at the specified time (strTag, strStart, strStop) in the specified semester (blnPlanung), possibly disregarding a certain course (strKursID).
   *  <b>Please note</b> that the name of the room should be set in the constructor of this class. This is admittedly a little inconsistent:
   *  the SeminarID does not have to be set. This should be #hacked.
   *  @param lngSeminarID: Id of seminar,
   *  @param strTag: Day to look up (thought of as a German day),
   *  @param strStart: Beginning of period to check, format is supposed to be 'hh:mm' (no checks made),
   *  @param blnPlanung: Look up current semester (blnPlanung=false), or look up future semester (blnPlanung=true)?
   *  @param strKursID: disregarded if set to "ADD". If numeric, bookings of this course are disregarded here.
   *  @throws Exceptions when db connection goes bad (or funky values for query are handed over).
   **/
  public boolean isFree(long lngSeminarID, String strTag, Time strStart, Time strStop, boolean blnPlanung, String strKursID) throws Exception{
	  ResultSet rst=this.getFreeRooms(lngSeminarID, strTag, strStart, strStop, blnPlanung, strKursID, this.getRaumBezeichnung());
	  boolean blnReturn = (rst.next());
	  rst.close();
	  rst=null;
	  return blnReturn;
  }

  /**
   *  @returns boolean value indicating whether the current room (this.getRaumBezeichnung()) of the specified seminar (lngSeminarID)
   *  is free at the specified time (strTag, strStart, strStop) in the specified semester (blnPlanung) -- all specified in Kurs k --, possibly disregarding a certain course (k.KursID).
   *  <b>Please note</b> that the name of the room should be set in the constructor of this class. This is admittedly a little inconsistent:
   *  the SeminarID does not have to be set. This should be #hacked.
   *  @param k: fully initialized (not necessarily databased) course with start-and stop-time,
   *  @param blnSecond: is the first or second date/time to be checked?
   *  @throws Exceptions when db connection goes bad (or funky values for query are handed over).
   **/
  public boolean isFree(Kurs k, boolean blnSecond) throws Exception{
	  ResultSet rst;

	  if(blnSecond){
		rst=this.getFreeRooms(k.getSdSeminarID(), k.getKursTag2(), k.getKursBeginn2(), k.getKursEnde2(), k.getKursPlanungssemester(), ( (k.getKursID()<=0) ? "ADD" : String.valueOf(k.getKursID()) ), this.getRaumBezeichnung());
	  }else{
	  	rst=this.getFreeRooms(k.getSdSeminarID(), k.getKursTag(), k.getKursBeginn(), k.getKursEnde(), k.getKursPlanungssemester(), ( (k.getKursID()<=0) ? "ADD" : String.valueOf(k.getKursID()) ), this.getRaumBezeichnung());
	  }

	  boolean blnReturn = (rst.next());
	  rst.close();
	  rst=null;
	  return blnReturn;
  }

	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 3.   Q U E R Y
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------

  /**
   *  @returns ResultSet (*-select) with all rooms that are not booked during the given period in the given semester,
   *  possibly disregarding a given course. The method also checks for bookings in table 'tblBdRaumplanExtern.'<br />
   *  And here the difficult part: since this method is also used for 'isFree,' a room can be specified for the query.
   *  If no room is specified (strRoom=""), the method just hands back all free rooms.
   *  @param lngSeminarID: Id of seminar,
   *  @param strTag: Day to look up (thought of as a German day),
   *  @param strStart: Beginning of period to check, format is supposed to be 'hh:mm' (no checks made),
   *  @param blnPlanung: Look up current semester (blnPlanung=false), or look up future semester (blnPlanung=true)?
   *  @param strKursID: disregarded if set to "ADD". If numeric, bookings of this course are disregarded here.
   *  @param strRoom: if strRoom is not empty, only this specific room is searched (and only this room's data will be handed back).
   *  @throws Exceptions when db connection goes bad (or funky values for query are handed over).
   **/
  public ResultSet getFreeRooms(long lngSeminarID, String strTag, Time tStart, Time tStop, boolean blnPlanung, String strKursID, String strRoom) throws Exception{
	String strKursChange = ((strKursID.equals("ADD")) ? "" : ("b.\"lngKursID\"<>" + getDBCleanString(strKursID) + " AND "));
	String strPlanung    = getDBBoolRepresentation(blnPlanung);

	// no SQL-injection:
	strTag				 = getDBCleanString(strTag);
	String strStart			 = g_TIME_FORMAT.format(tStart);
	String strStop				 = g_TIME_FORMAT.format(tStop);

	if(!(this.normalize(strRoom).equals(""))) strRoom = "a.\"strRaumBezeichnung\"='" + strRoom + "' AND ";

	return this.sqlQuery("SELECT a.\"strRaumBezeichnung\", a.\"lngSdSeminarID\", a.* FROM \"tblSdRaum\" a " +
			"WHERE " +
			"a.\"lngSdSeminarID\" = " + lngSeminarID + " AND " + strRoom +
			"NOT EXISTS (" +
			"SELECT b.\"strKursRaum\" FROM \"tblBdKurs\" b WHERE " +
			"(a.\"strRaumBezeichnung\"=b.\"strKursRaum\" AND " +		//1. Tag
			"b.\"lngSdSeminarID\"=" + lngSeminarID + " AND " +
			"b.\"strKursTag\"='" + strTag + "' AND " +
			 strKursChange +
			"b.\"blnKursPlanungssemester\"=" + strPlanung + " AND " +
			"((b.\"dtmKursBeginn\">='" + strStart + "' AND " +
			"b.\"dtmKursBeginn\"<='" + strStop + "') OR " +				//Beginn zw. Start & Stop
			"(b.\"dtmKursEnde\">='" + strStart + "' AND " +
			"b.\"dtmKursEnde\"<='" + strStop + "') OR " +				//Ende zw. Start & Stop
			"(b.\"dtmKursBeginn\"<'" + strStart + "' AND " +
			"b.\"dtmKursEnde\">'" + strStop +  "'))) OR " +				//Beginn<Start und Ende>Stop

			 "(a.\"strRaumBezeichnung\"=b.\"strKursRaum2\" AND " +		//2. Tag
			"b.\"lngSdSeminarID\"=" + lngSeminarID + " AND " +
			"b.\"strKursTag2\"='" + strTag + "' AND " +
			 strKursChange +
			"b.\"blnKursPlanungssemester\"=" + strPlanung + " AND " +
			"((b.\"dtmKursBeginn2\">='" + strStart + "' AND " +
			"b.\"dtmKursBeginn2\"<='" + strStop + "') OR " +			//Beginn zw. Start & Stop
			"(b.\"dtmKursEnde2\">='" + strStart + "' AND " +
			"b.\"dtmKursEnde2\"<='" + strStop + "') OR " +				//Ende zw. Start & Stop
			"(b.\"dtmKursBeginn2\"<'" + strStart + "' AND " +
			"b.\"dtmKursEnde2\">'" + strStop +  "')))" +				//Beginn<Start und Ende>Stop
			") AND " +
			"NOT EXISTS (" +
			"SELECT * FROM \"tblBdRaumplanExtern\" x WHERE (" +
			"a.\"strRaumBezeichnung\"=x.\"strRaum\" AND " +
			"x.\"lngSdSeminarID\"=" + lngSeminarID + " AND " +
			"x.\"blnPlanung\"=" + strPlanung + " AND " +
			"x.\"strTag\"='" + strTag + "' AND " +
			"((x.\"dtmBeginn\">='" + strStart + "' AND " +
			"x.\"dtmBeginn\"<='" + strStop + "') OR " +				//Beginn zw. Start & Stop
			"(x.\"dtmEnde\">='" + strStart + "' AND " +
			"x.\"dtmEnde\"<='" + strStop + "') OR " +				//Ende zw. Start & Stop
			"(x.\"dtmBeginn\"<'" + strStart + "' AND " +
			"x.\"dtmEnde\">'" + strStop +  "'))));");
  }


	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 4.   C O N S T R U C T O R
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------

  	/**
  	 * For the methods 'getFreeRooms' and the like, there is of course RaumBezeichnung: set it empty.
  	 */
  	public RaumData(String strRaumBezeichnung){
		  this.setRaumBezeichnung(strRaumBezeichnung);
  	}
}