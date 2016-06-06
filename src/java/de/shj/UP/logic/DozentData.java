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
 * code structure
 *
 * 1. declarations,				line  92
 *
 * 2. properties,				line 102
 *
 * 3. methods general,			line 123
 *
 * 4. methods certificate		line 239
 *
 * 5. constructors				line 339
 *
 * @version 0-00-00
 * change date              change author           change description
 * Juni 2016                hj                      überarbeitet für OS
 */
package de.shj.UP.logic;
import java.sql.PreparedStatement;

/**
 * <pre>
 * Legacy class. Zur Löschung vormerken?
 * 
 * Ideen: vCard einbinden
 * </pre>
 **/
public class DozentData extends de.shj.UP.data.Dozent{

/////////////////////////////////////
// 1.   D E C L A R A T I O N S
/////////////////////////////////////
	
	private static final long serialVersionUID = -7102294401572715866L;
	public 	String m_strDebug;
//	private long m_lngSeminarID;	// W H Y ?	(Auskommentiert am 5.1.2011)
//	private KursteilnehmerIterator m_KI = null;


/////////////////////////////////////
// 2. P R O P E R T I E S
/////////////////////////////////////
	
//	/**
//	 * @return
//         * @deprecated Sorry, not in OS Variante (yet)
//	 */
//	public KursteilnehmerIterator getKursteilnehmerIterator() throws Exception{
//		if(m_KI==null) m_KI = new KursteilnehmerIterator(getSdSeminarID(), getDozentID());
//		return m_KI;
//	}
//	
//	/**
//	 * @return
//	 * @throws Exception
//         * @deprecated Sorry, not in OS Variante (yet)
//	 */
//	public KursteilnehmerIterator getNewKursteilnehmerIterator() throws Exception{
//		m_KI=null;
//		return getKursteilnehmerIterator();
//	}
	
	/**<pre>
	 * vCard in version 2.1, like this:
	 * BEFIN:VCARD
	 * VERSION:2.1
	 * N:lastname;firstname
	 * FN:firstname;lastname
	 * EMAIL:email
	 * TEL;TYPE=WORK:work-telephone
	 * TEL;TYPE=HOME:home-telephone
	 * BDAY:birthday
	 * END:VCARD
	 * </pre>
	 * @return vCard String
	 */
	public String vCard(){
		return "BEGIN:VCARD\n" +
		 		"VERSION:2.1\n" +
				"N:" + getDozentNachname() + ";" + getDozentVorname() + "\n" +
				"FN:" + getDozentVorname() + " " + getDozentNachname() +";\n" +
				"EMAIL:" + getDozentEmail() + "\n" +
				"TEL;TYPE=WORK:" + getDozentTelefon() + "\n" + 
				"TEL;TYPE=HOME:" + getDozentTelefonPrivat() + "\n" +
				"BDAY:" + getDozentGebdatum() + "\n" +
				"END:VCARD";
	}
	
	public String LDIF(){
		return "dn: cn=" + getDozentVorname() + " " + getDozentNachname() + ",mail=" + getDozentEmail() + "\n" +
			"objectclass: top\n" +
			"objectclass: person\n" +
			"objectclass: organizationalPerson\n" +
			"objectclass: inetOrgPerson\n" +
			"objectclass: mozillaAbPersonAlpha\n" +
			"givenName: " + getDozentVorname() + "\n" +
			"sn: " + getDozentNachname() + "\n" +
			"cn: " + getDozentVorname() + " " + getDozentNachname() + "\n" +
			"mail: " + normalize(getDozentEmail()) + "\n" +
			"modifytimestamp: 0Z\n" + 
			"telephoneNumber: " + normalize(getDozentTelefon()) + "\n" +
			"homePhone: " + normalize(getDozentTelefonPrivat()) + "\n" +
			"mobile: \n" +
			"homeStreet: " + normalize(getDozentStrasse()) + "\n" +
			"mozillaHomeLocalityName: " + normalize(getDozentOrt()) + "\n" +
			"mozillaHomePostalCode: " + normalize(getDozentPLZ()) + "\n" +
			"mozillaHomeCountryName: Deutschland\n" + 
			"street: Kettengasse 12\n" +
			"l: Heidelberg\n" +
			"postalCode: 69117\n" +
			"c: Deutschland\n" +
			"title: " + normalize(getDozentTitel()) + "\n" +
			"mozillaWorkUrl: http://\n" +
			"mozillaHomeUrl: " + normalize(getDozentHomepage()) + "\n";
	}

/////////////////////////////////////
//	 3. D B - U T I L I T I E S
/////////////////////////////////////
	
	/**
         * Update einer Untermenge von Eigenschaften d. Dozenten, nämlich:
	 * 
	 * - last- and first name, 
	 * - email, 
	 * - homepage, 
	 * - phone, 
	 * - title, 
	 * - password, 
	 * - office-hours, 
	 * - postfach (pigeon hole),
	 * - interests, 
	 * - Access-Level and IP. 
	 * - address
	 * - private telephone
	 * - Bereich
	 * 
	 * Ursprünglich eingeführt wegen der Client-Zertifikate; 
         * Überarbeitet für UP (OS)
         * @todo: testen, ob obsolet und delegierbar an super.update().
	 * 
	 * @return 'true' for a successful update, 'false' for an sql-error.
	 * @throws Exception 
	 * @throws Exception 
	 * @throws Exception when connection to database is erroneous.
	 **/
	public boolean update() throws Exception{
            PreparedStatement pstm = prepareStatement("update \"tblSdDozent\" set " +
                    "\"strDozentNachname\"=?, " +	
                    "\"strDozentVorname\"=?, " +
                    "\"strDozentTitel\"=?, " +
                    "\"strDozentPasswort\"=?, " +
                    "\"strDozentSprechstunde\"=?, " +
                    "\"strDozentZimmer\"=?," +
                    "\"strDozentEmail\"=?, " +
                    "\"strDozentTelefon\"=?, " +
                    "\"strDozentHomepage\"=?, " +
                    "\"strDozentStrasse\"=?, " +
                    "\"strDozentPLZ\"=?, " +
                    "\"strDozentOrt\"=?, " +
                    "\"strDozentTelefonPrivat\"=?, " +
                    "\"strDozentInteressen\"=?, " +
                    "\"intDozentAccessLevel\"=?, " +
                    "\"strDozentIP\"=?, " +
                    "\"blnDozentLehrend\"=?, " +
                    "\"strDozentBereich\"=?, " +
                    "\"strDozentPostfach\"=? " +
                    "where (\"lngSdSeminarID\"=? AND "  + 
			"\"lngDozentID\"=?);");
            
            int ii=1;
            pstm.setString(ii++, this.getDozentNachname());
            pstm.setString(ii++, this.getDozentVorname());
            pstm.setString(ii++, this.getDozentTitel());
            pstm.setString(ii++, this.getDozentPasswort());
            pstm.setString(ii++, this.getDozentSprechstunde());
            pstm.setString(ii++, this.getDozentZimmer());
            pstm.setString(ii++, this.getDozentEmail());
            pstm.setString(ii++, this.getDozentTelefon());
            pstm.setString(ii++, this.getDozentHomepage());
            pstm.setString(ii++, this.getDozentStrasse());
            pstm.setString(ii++, this.getDozentPLZ());
            pstm.setString(ii++, this.getDozentOrt());
            pstm.setString(ii++, this.getDozentTelefonPrivat());
            pstm.setLong(ii++, this.getDozentAccessLevel());
            pstm.setString(ii++, normalize(this.getDozentIP()));
            pstm.setBoolean(ii++, this.getDozentLehrend());
            pstm.setString(ii++, this.getDozentBereich());
            pstm.setString(ii++, this.getDozentPostfach());
            pstm.setLong(ii++, this.getSdSeminarID());
            pstm.setLong(ii++, this.getDozentID());
            return (pstm.executeUpdate() == 1);
	}

//////////////////////////////////////////////////////////////////////////
//  5. Construction
/////////////////////////////////////////////////////////////////////////


	/**
	 * Empty constructor, for backwards compatibility in jsp
	 **/
	public DozentData(){}

	/**
	 * @see com.shj.data.Dozent
	 **/
	public DozentData(long lngSdSeminarID, long lngDozentID) throws Exception{
		this.init(lngSdSeminarID, lngDozentID);
	}

}