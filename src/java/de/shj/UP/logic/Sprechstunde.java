/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.shj.UP.logic;
import de.shj.UP.data.shjCore;
import static de.shj.UP.data.shjCore.normalize;
//import com.shj.signUp.util.SendMail;
import de.shj.UP.util.SignUpLocalConnector;
import java.sql.ResultSet;

/**
 *
 * @author heiko
 */
public class Sprechstunde extends shjCore {
    private String m_sEmailErr="";
    private static final String g_SERVER = "extmail.urz.uni-heidelberg.de";
    private int iCount=0;
    private int iCountErr=0;
    
    /**
     * String with errors that occurred while
     * .sendEmailReminders was called.
     * @return String with error information
     */
    public String getEmailErr(){return m_sEmailErr;}
    
    /**
     * 
     * @return Anzahl der E-Mails, die zu verwenden waren.
     */
    public int getCount(){return iCount;}
    
    /**
     * Anzahl der E-Mails, die nicht versendet werden konnten.
     * @return 
     */
    public int getCountErr(){return iCountErr;}
    public void sendEmailReminders(SignUpLocalConnector db) throws Exception{
        throw new Exception("Sorry, abgeschaltet in OpenSource Version -- Überarbeitung notwendig.");
//        ResultSet rReminder=db.sqlQueryLocal(
//                  "select n.\"strNutzerTitel\", n.\"strNutzerNachname\", n.\"strNutzerEmail\", s.\"strSprechstundeOrt\", s.\"strSprechstundeBemerkung\", " 
//                + "  d.\"strDozentTitel\", d.\"strDozentNachname\", " 
//                + "  d.\"strDozentEmail\", s.\"strSprechstundeStornolink\", s.\"dtmSprechstundeDatum\", \"tmeSprechstundeStart\", \"tmeSprechstundeStop\" " 
//                + "FROM " 
//                + "  \"tblSdNutzer\" n," 
//                + "  \"tblBdSprechstunde\" s," 
//                + "  \"tblSdDozent\" d " 
//                + "WHERE " 
//                + "  n.\"strNutzerUniID\" = s.\"strNutzerUniID\" AND " 
//                + "  s.\"blnSprechstundeEmailReminder\" = true AND " 
//                + "  s.\"dtmSprechstundeDatum\" = (select date 'tomorrow') AND " 
//                + "  d.\"lngSdSeminarID\"=s.\"lngSdSeminarID\" and " 
//                + "  d.\"lngDozentID\"=s.\"lngDozentID\";");
//        SendMail mail = new SendMail(g_SERVER);
//        while(rReminder.next()){
//            boolean bOK = mail.sendMail(
//                    "Info", rReminder.getString("strNutzerEmail"),"", rReminder.getString("strDozentEmail"), 
//                    "Terminerinnerung", getEmailReminderBody(rReminder), null);
//            iCount++;
//            if(!bOK){String sTmp ="ERROR beim Senden einer Sprechstundenerinnerung:" + mail.getError() + "\n\n" + "Mail should have gone\n" +
//						"to: " + rReminder.getString("strNutzerEmail") + "\n" + 
//						"return: " + rReminder.getString("strDozentEmail") + " -- containing:\n" + 
//						getEmailReminderBody(rReminder);
//                    System.err.println(sTmp);
//                    m_sEmailErr += "\n\n" + sTmp;
//                    iCountErr++;
//            }
//        }
    }

    
    private String getEmailReminderBody(ResultSet r)throws Exception{
        return "Dear " + normalize(r.getString("strNutzerTitel") + " ") + r.getString("strNutzerNachname") 
                + "\n\n" + 
                "Sie haben am " + g_GERMAN_DATE_FORMAT.format(r.getDate("dtmSprechstundeDatum")) + " von " + r.getTime("tmeSprechstundeStart").toString().substring(0,5) + " bis " + r.getTime("tmeSprechstundeStop").toString().substring(0,5) + " einen\nTermin bei " 
                + normalize(r.getString("strDozentTitel") + " ") + r.getString ("strDozentNachname") + " " 
                + "(" + r.getString ("strSprechstundeOrt") + ")\n"
                + "gebucht.\n\n"
                + "(Ihre Bemerkung zu dem Termin lautete:\n\"" + r.getString("strSprechstundeBemerkung") + "\").\n\n" 
                + "Dies ist eine automatische Erinnerung.\n\n"
                + "Sollten Sie den Termin nicht wahrnehmen können, geben\n"
                + "Sie ihn bitte online hier frei: \n" + r.getString("strSprechstundeStornolink") + ".";
    }
    
    /**
     * Empty constructor
     */
    public Sprechstunde(){}
}
