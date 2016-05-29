package de.shj.UP.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Medizin hd hat einen "Modulzeitraum" 
 * als Kurseigenschaft (strCustom3) und 
 * als Eigenschaft von StudentXLeistung
 * (strCustom2).
 * 
 * Es sollte ein Zeitraum der Form:
 * 1.8.2006-12.9.2007 sein, wird aber 
 * als String gespeichert (legacy).
 * 
 * Die Klasse "Modulzeitraum" soll zwischen 
 * den Datums- und String Formaten vermitteln
 * helfen.
 * 
 * Beim Zusammenführen von Leistungen (autom.
 * Erzeugen, vgl. Scheduler) benutzt man 
 * die ModulzeitraumCollection, die auf dieser
 * Klasse aufbaut.
 * 
 * Example:
 * ========
 * Modulzeitraum mod = new Modulzeitraum("1.2.-2.4.07");
 * System.out.println(mod.getModulzeitraum());
 * 
 * 
 * @author shj
 * 
 * changes:
 * date			author		comment
 * Apr 7, 07	hj			creation of class
 *
 */
public class Modulzeitraum {

//////////////////////////////////
// Object vars
//////////////////////////////////
	
	private final SimpleDateFormat m_sdfIn=new SimpleDateFormat ("dd.MM.yyyy");
	private String m_sModulzeitraum = "";
	
	private Date m_dDatum1=null;
	private Date m_dDatum2=null;

	private boolean m_bSwappedSequence=false;
	private boolean m_bParsedSuccessfully=false;

//////////////////////////////////
//	 Main method: parse
//////////////////////////////////

	/**
	 * Tries to parse the Modulzeitraum (that is handed 
	 * in on construction time) into two dates (which 
	 * are then available as object-vars through getters). 
	 * 
	 * The two dates are also forced into a sequence, 
	 * so that the getters "firstDate" and "lastDate" 
	 * deserve their names.
	 * @return true, if parsing seemed successful
	 */
	private boolean parse(){
				
		// replace o for 0 and eliminate space char's
		m_sModulzeitraum=m_sModulzeitraum.replaceAll(" ", "");
		m_sModulzeitraum=m_sModulzeitraum.replaceAll("o", "0");
		
		int iPosDash = m_sModulzeitraum.indexOf('-');		
		if(iPosDash < 0) return false;

		// local Strings for split dates
		String sDatum1="";
		String sDatum2="";

		// Split two dates into left-of-dash and
		// right-of-dash
		sDatum1=m_sModulzeitraum.substring(0, iPosDash).trim();
		sDatum2=m_sModulzeitraum.substring(iPosDash+1).trim();
		
		// sometimes, datum1 will end with a ".", if 
		// the date is e.g. 1.3.-20.7.2008.
		// in this case, the year of date 2 is appended
		//
		// it may also end with a "." in this situation:
		// 1. - 10.3.2007
		// in this case, month AND year of date 2 are 
		// appended.
		if(sDatum1.endsWith(".")){
			
			// if there is only 1 ".", add month AND year
			if(sDatum1.indexOf('.')==sDatum1.lastIndexOf('.')){
				String sMonthNYear = sDatum2.substring(sDatum2.indexOf('.') + 1);
				sDatum1 += sMonthNYear;
			}else{
				// Just add year of date2:
				int iPosPeriod = sDatum2.lastIndexOf('.');
				String sYear   = sDatum2.substring(iPosPeriod+1);
				sDatum1 += sYear;
			}
		}
		
		try{
			sDatum1 = normalizeDate(sDatum1);
			m_dDatum1 = m_sdfIn.parse(sDatum1);
		}catch(Exception e1){
			return false;
		}
		
		try{
			sDatum2 = normalizeDate(sDatum2);
			m_dDatum2 = m_sdfIn.parse(sDatum2);
		}catch(Exception e1){
			return false;
		}
		
		// turn the sequence of first and last 
		// around if necessary
		checkSwap();
		
		// if the method runs through, that's a success
		return true;
	}

//////////////////////////////////
//	 Getters -- Properties
//////////////////////////////////

	/**
	 * First Date "-" Last Date, if the 
	 * Modulzeitraum was successfully parsed.
	 * 
	 * If it was NOT successfully parsed, this 
	 * method returns "#" + whatever you handed 
	 * in on construction time.
	 * @return Modulzeitraum
	 */
	public String getModulzeitraum(){
		if(!m_bParsedSuccessfully) return "#" + m_sModulzeitraum;
		return getFirstDateString()+"-"+getLastDateString();
	}
	
	/**
	 * This is an error indicator:
	 * after parsing the dates, the method "checkSwap()"
	 * (called at every parsing) 
	 * looks if the first date is really before the 
	 * last one.
	 * If this is not the case, it swaps the two 
	 * dates, so that the sequence is right. It also 
	 * sets this flag.
	 * Usually, it indicates an error which should 
	 * then be raised.
	 * @see #checkSwap()
	 * @return true, if the first date was later than the last.
	 */
	public boolean isSequenceSwapped(){
		return m_bSwappedSequence;
	}
	
	/**
	 * @return true, if everything went well.
	 */
	public boolean isParsedSuccessfully(){
		return m_bParsedSuccessfully;
	}

////////////////////////////////////
//	Get the dates
	
	/**
	 * @return Datum1 in dd.MM.yyyy, or empty String, if parsing failed
	 */
	public String getFirstDateString(){
		if(!m_bParsedSuccessfully) return "";
		return m_sdfIn.format(m_dDatum1);
	}
	
	/**
	 * @return Datum2 in dd.MM.yyyy, or empty String, if parsing failed
	 */
	public String getLastDateString(){
		if(!m_bParsedSuccessfully) return "";
		return m_sdfIn.format(m_dDatum2);
	}
	
	/**
	 * @return first of the dates, or null, if not parseable
	 */
	public Date getFirstDate(){
		if(!m_bParsedSuccessfully) return null;
		return m_dDatum1;
	}
	
	/**
	 * @return last of the dates, or null, if not parseable
	 */
	public Date getLastDate(){
		if(!m_bParsedSuccessfully) return null;
		return m_dDatum2;
	}
	
//////////////////////////////////
//	 Private Code
//////////////////////////////////
	
	/**
	 * Checks, if firstDate is really before
	 * last date, and swaps the two, if 
	 * necessary.
	 * If this method performs the swap, 
	 * it also sets the flag
	 * @see #isSequenceSwapped()
	 */
	private void checkSwap(){
		if(! m_dDatum1.before(m_dDatum2)){
			Date dSwap = m_dDatum2;
			m_dDatum2 = m_dDatum1;
			m_dDatum1 = dSwap;
			m_bSwappedSequence=true;
		}
	}
	
	/**
	 * Tries to parse the date
	 * @param sDate
	 * @return
	 * @throws Exception
	 */
	private String normalizeDate(String sDate) throws Exception{
		SimpleDateFormat sdfNormalized=new SimpleDateFormat ("dd.MM.yy");
		String sReturn = "";
		try {
			sReturn = (String)m_sdfIn.format(sdfNormalized.parse(sDate));
			return sReturn;
		} catch (RuntimeException e) {}
		
		return "#no";
	}
	
//////////////////////////////////
//	 CONSTRUCTORS
//////////////////////////////////

	/**
	 * Modulzeitraum in one of these formats:
	 * 1.2.2006-10.12.2007 or
	 * 01.02.2007-10.12.2007 or
	 * 1. 1. -10.12.2007 or
	 * 3.-15.2.2007 or
	 * 1.2.-1o.12.2oo7 (and mixes).
	 * @param sModulzeitraum
	 */
	public Modulzeitraum(String sModulzeitraum){
		m_sModulzeitraum=sModulzeitraum;
		m_bParsedSuccessfully=parse();
	}
	
	/**
	 * Modulzeitraum constructed with the dates.
	 * Those are automatically sequenced (but 
	 * if they have to be swapped, the set a 
	 * flat -- so you'd better construct in the 
	 * right sequence: first date first, second 
	 * date second.
	 * @param dFirst
	 * @param dLast
	 */
	public Modulzeitraum(Date dFirst, Date dLast){
		m_dDatum1=dFirst;
		m_dDatum2=dLast;
		m_bParsedSuccessfully=true;
		checkSwap();
	}
}
