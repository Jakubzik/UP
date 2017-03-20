/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.shj.UP.util;

import de.shj.UP.data.shjCore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author heiko
 */
public class RqDescription {

    private String m_sRequestParamName = "";
    private int m_iRequestParamExpectedType = -1;
    private String m_sRequestParamDescription = "";
    private int m_iRequestParamMinLength = -1;
    private boolean m_bRequestParamOptional = false;
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    
    public final static int RQ_MATRIKELNUMMER = 1;
    public final static int RQ_STRING = 2;
    public final static int RQ_LONG = 3;
    public final static int RQ_DATE_ISO = 4;
    public final static int RQ_DATE_DE = 5;
    public final static int RQ_EMAIL = 6;
    public final static int RQ_BOOLEAN = 7;     // Nicht vergessen, Setter erweitern!
    
    /**
     * @return the m_iRequestParamMinLength
     */
    public int getRequestParamMinLength() {
        return m_iRequestParamMinLength;
    }

    /**
     * @param m_iRequestParamMinLength the m_iRequestParamMinLength to set
     */
    public void setRequestParamMinLength(int m_iRequestParamMinLength) {
        this.m_iRequestParamMinLength = m_iRequestParamMinLength;
    }
    
    /**
     * @return the m_sRequestParamExpectedType
     */
    public int getRequestParamExpectedType() {
        return m_iRequestParamExpectedType;
    }

    /**
     * @param m_sRequestParamExpectedType the m_sRequestParamExpectedType to set
     */
    public void setRequestParamExpectedType(int iRequestParamExpectedType) throws Exception {
        if(iRequestParamExpectedType == RQ_DATE_DE ||
                iRequestParamExpectedType == RQ_DATE_ISO ||
                iRequestParamExpectedType == RQ_MATRIKELNUMMER ||
                iRequestParamExpectedType == RQ_STRING ||
                iRequestParamExpectedType == RQ_LONG ||
                iRequestParamExpectedType == RQ_EMAIL ||
                iRequestParamExpectedType == RQ_BOOLEAN)
            m_iRequestParamExpectedType = iRequestParamExpectedType;
        else
            throw new Exception("Fehlkonfiguration: der Typ >" + iRequestParamExpectedType + 
                    "< ist nicht bekannt. Siehe vorhandene Typen unter .RQ_*");
            
    }

    /**
     * @return the m_sRequestParamDescription
     */
    public String getRequestParamDescription() {
        return m_sRequestParamDescription;
    }

    /**
     * @param m_sRequestParamDescription the m_sRequestParamDescription to set
     */
    public void setRequestParamDescription(String m_sRequestParamDescription) {
        this.m_sRequestParamDescription = m_sRequestParamDescription;
    }

    /**
     * @return the m_sRequestParamName
     */
    public String getRequestParamName() {
        return m_sRequestParamName;
    }

    /**
     * @param m_sRequestParamName the m_sRequestParamName to set
     */
    public void setRequestParamName(String m_sRequestParamName) {
        this.m_sRequestParamName = m_sRequestParamName;
    }
//    oD.add(new RqDescription("matrikelnummer", "String", "number", "Matrikelnummer"));

    public boolean meetsDescription(HttpServletRequest r){
        boolean bReturn = false;
        
        /* Wenn der Parameter als optional gekennzeichnet ist und 
           null, gilt der Test als bestanden. */
        if(r.getParameter(m_sRequestParamName) == null) return m_bRequestParamOptional;
        
        switch(m_iRequestParamExpectedType){
            case RQ_MATRIKELNUMMER:
                if( r.getParameter(m_sRequestParamName).length() != 7 ) bReturn = false;
                else try{
                    Long.parseLong(r.getParameter(m_sRequestParamName));
                    bReturn = true;
                }catch(Exception eNumber){ bReturn = false; }
                break;
                
            case RQ_STRING:
                if(m_iRequestParamMinLength > 0) bReturn = r.getParameter(m_sRequestParamName).length() >= m_iRequestParamMinLength;
                break;
                
            case RQ_EMAIL:
                Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(r.getParameter(m_sRequestParamName));
                bReturn = matcher.find();
                if(bReturn && m_iRequestParamMinLength > 0) 
                    bReturn = r.getParameter(m_sRequestParamName).length() >= m_iRequestParamMinLength;
                break;
                
            case RQ_LONG:
                try{
                    Long.parseLong(r.getParameter(m_sRequestParamName));
                    bReturn = true;
                }catch(Exception e){ bReturn = false; }
                break;
                
            case RQ_BOOLEAN:
                try{
                    Boolean.parseBoolean(r.getParameter(m_sRequestParamName));
                    bReturn = true;
                }catch(Exception e){ bReturn = false; }
                break;                

            case RQ_DATE_ISO:
                try{
                    shjCore.g_ISO_DATE_FORMAT.parse(r.getParameter(m_sRequestParamName));
                    bReturn = true;
                }catch(Exception e){ bReturn = false; }
                break;   
                
            case RQ_DATE_DE:
                try{
                    shjCore.g_GERMAN_DATE_FORMAT.parse(r.getParameter(m_sRequestParamName));
                    bReturn = true;
                }catch(Exception e){ bReturn = false; }
                break;   
                
            default:
                return false;
        }
        return bReturn;
    }
    
    public RqDescription(){};
    
    private void construct(String sParamName, int iParamTyp, int iParamLength, String sParamDescription, boolean bParamOptional) throws Exception{
        m_sRequestParamName = sParamName;
        setRequestParamExpectedType(iParamTyp);
        m_sRequestParamDescription = sParamDescription;
        m_iRequestParamMinLength = iParamLength;
        m_bRequestParamOptional = bParamOptional;
    }
    
    /**
     * Beispiel:
     * new RqDescription("Strasse", "String", 4, "Matrikelnummer")
     * 
     * Prüft dann nach Parameter "matrikelnummer" vom Typ String (not null), Mindestlänge 4, 
     * mit der Bezeichnung "Matrikelnummer" (letzteres für die Fehlermeldung!).
     * 
     * @param sParamName Name, d.h. Zugriff per Request.getParameter(<Name>);
     * @param sParamTyp Aus der Liste: Matrikelnummer, String, Date, Boolean, Long, Email "Date" meint ISO-Format.
     * @param sParamLength Mindestlänge für String
     * @param sParamDescription 
     * @param bParamOptional Default: false
     */
    public RqDescription(String sParamName, int iParamTyp, int iParamLength, String sParamDescription, boolean bParamOptional) throws Exception{
        construct(sParamName, iParamTyp, iParamLength, sParamDescription, bParamOptional);
    }
    
    public RqDescription(String sParamName, int iParamTyp, int iParamLength, String sParamDescription) throws Exception{
        construct(sParamName, iParamTyp, iParamLength, sParamDescription, false);
    }
    
    public RqDescription(String sParamName, int iParamTyp, String sParamDescription) throws Exception{
        construct(sParamName, iParamTyp, -1, sParamDescription, false);
    }
    
    public RqDescription(String sParamName, int iParamTyp, String sParamDescription, boolean bParamOptional) throws Exception{
        construct(sParamName, iParamTyp, -1, sParamDescription, bParamOptional);
    }
    


}
