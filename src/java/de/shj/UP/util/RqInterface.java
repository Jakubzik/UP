/**
 * RqInterface und RqDescription erleichtern die 
 * Schnittstellentests für REST-Seiten.
 * 
 * Bsp:
 * RqInterface oD = new RqInterface();
 * 
 * oD.setErrBase ( "1" );
 * oD.setActionFailedDescription( "Die Studierendaten konnten nicht gespeichert werden." );
 * oD.setRequest(request);
 *   
 * oD.add(new RqDescription("matrikelnummer", "Matrikelnummer", "Matrikelnummer"));
 * oD.add(new RqDescription("studentNachname", "String", 2, "Nachname"));
 * 
 * // Löst ggf. eine Ausnahme im JSON-SignUp Format aus.
 * oD.checkRequestAgainstDescription();  
 *
 **/
package de.shj.UP.util;

import de.shj.UP.util.RqDescription;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author heiko
 */
public class RqInterface {
    
    private HttpServletRequest m_r = null;
    private boolean m_bThrowException = true;
    ArrayList<RqDescription> aDescription = new ArrayList<RqDescription>();
    private String m_sActionFailedDescription = "Aktion unbekannt";
    private long m_lERR_BASE = -1;
    private String m_sErrMsg = "{\"error\":\"<ACTION_FAIL_DESCR>. <ACTION_FAIL_DETAIL>.\"" +
            ",\"errorDebug\":\"<ACTION_FAIL_DETAIL_DEBUG>.\",\"errorcode\":\"<ERR_CODE>\",\"severity\":50}";
    
    /**
     * Der Aufruf der Hauptmethode .checkRequestAgainstDescriptions()
     * löst im Default eine Ausnahme aus, wenn der Request nicht 
     * den Anforderungen entspricht (im JSON-Format für SignUp).
     * 
     * Sie kann stattdessen aber auch nur true oder false 
     * zurückgeben -- je nach dem, ob der Request der 
     * Anforderung entspricht oder nicht. Dazu RqDescription
     * muss zuvor setDontThrowException aufgerufen werden.
     * 
     * An den Fehlertext kommt man über .getErrMsg
     */
    public void setDontThrowException(){
        m_bThrowException = false;
    }
    
    /**
     * Enthält im JSON-Format die Fehlermeldung über 
     * eine nicht erfüllte Anforderung an den Request,
     * @return 
     */
    public String getErrMsg(){
        return m_sErrMsg;
    }
    
    public void setErrBase(long l_IN){
        m_lERR_BASE = l_IN;
    }
    
    /**
     * Beschreibung des Kontexts für die Ausgabe in der 
     * Fehlermeldung, z.B. "Das Hinzufügen der Bemerkung ist 
     * fehlgeschlagen".
     * @param s_IN 
     */
    public void setActionFailedDescription(String s_IN){
        m_sActionFailedDescription = s_IN;
    }
    
    /**
     * Der Request, der überprüft werden soll.
     * @param r 
     */
    public void setRequest(HttpServletRequest r){
        m_r = r;
    }

    /**
     * Kriterium für die Konformität der Anfrage. Bsp.:
     * 
     *  oD.add(new RqDescription("matrikelnummer", "Matrikelnummer", "Matrikelnummer"));
     *  oD.add(new RqDescription("studentNachname", "String", 2, "Nachname des oder der Studierenden"));
     *  oD.add(new RqDescription("studentVorname", "String", 2, "Vorname des oder der Studierenden"));
     *  oD.add(new RqDescription("studentEmail", "Email", "1@stud.uni-heidelberg.de".length(), "E-Mail Adresse des oder der Studierenden"));
     *  oD.add(new RqDescription("studentGeburtsort", "String", 20, "Geburtsort des oder der Studierenden"));
     *  oD.add(new RqDescription("studentGeburtstag", "Date", "Geburtsdatum des oder der Studierenden"));
     *  oD.add(new RqDescription("studentFemale", "Boolean", "Anrede bzw. Geschlecht des oder der Studierenden"));
     * @param desc 
     */
    public void add(RqDescription desc){
        aDescription.add(desc);
    }
    
    /**
     * Prüft den übergebenen HttpServletRequest auf die übergebenen 
     * Descriptions.
     * 
     * Wenn die Descriptions alle erfüllt sind, gibt die Methode true 
     * zurück.
     * 
     * Ansonsten wird ein Fehler im SignUp-JSON-Format mit Beschreibung 
     * des Fehlers ausgelöst.
     * 
     * Das Auslösen eines Fehlers kann durch vorherigen Aufruf
     * von .setDontThrowException() unterdrückt werden. In diesem 
     * Fall ist die Fehlermeldung über .getErrMsg() auslesbar.
     * @return
     * @throws Exception 
     */
    public boolean checkRequestAgainstDescriptions() throws Exception{
        boolean bReturn = true;
        for(RqDescription desc : aDescription){
            if(!desc.meetsDescription(m_r)){
                String sTmp = (desc.getRequestParamMinLength() <= 0 ? 
                        "" : 
                        " mit der Mindestlänge >" +  desc.getRequestParamMinLength() + "< ");
                m_sErrMsg = m_sErrMsg.replace("<ERR_CODE>", String.valueOf(m_lERR_BASE));
                m_sErrMsg = m_sErrMsg.replace("<ACTION_FAIL_DESCR>", m_sActionFailedDescription);
                m_sErrMsg = m_sErrMsg.replace("<ACTION_FAIL_DETAIL>", desc.getRequestParamDescription() + " wurde nicht richtig verstanden");
                m_sErrMsg = m_sErrMsg.replace("<ACTION_FAIL_DETAIL_DEBUG>", 
                        desc.getRequestParamDescription() + " hat den Wert >" + 
                                m_r.getParameter(desc.getRequestParamName()) + 
                                "<, der für den Typ >" + desc.getRequestParamExpectedType() + 
                                "< " + sTmp + "nicht akzeptiert wird");
                bReturn = false;
                if(m_bThrowException) throw new Exception(m_sErrMsg);
                break;
            }
        }
        
        return bReturn;
    }
    
    public RqInterface(){}
}
