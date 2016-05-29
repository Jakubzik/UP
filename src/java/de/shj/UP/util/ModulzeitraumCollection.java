package de.shj.UP.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This will process Modulzeitraum classes, but 
 * it will not store them (as a usual "Collection" would) 
 * for performance reasons.
 * 
 * It will only construct a resulting Modulzeitraum that 
 * begins at the earliest date of all added Modulzeitraum 
 * objects, and ends at the latest date.
 * 
 * If you only want to count Modulzeitraum objects that 
 * could successfully be parsed, use the success indicator.
 * 
 * Example:
 * ========
 *
 * ModulzeitraumCollection mod = new ModulzeitraumCollection();
 * 
 * mod.setAllowSwapping(false);
 * mod.add("01.01.2003-12.12.2006"); // will work fine
 * mod.add("SS2004");				 // will count as an error
 * mod.add("o1.o1.o1-2.4.2008");	 // will work fine
 * mod.add("1.2.-3.1.05");			 // will count as an error
 * 									 //	(because swapping is disallowed,
 * 									 // and the sequence of dates wrong)
 * 
 * System.out.println("Resultat:" + mod.getMaxSpan());
 * System.out.println("Erfolg:" + mod.isSuccessfullyParsed());
 * System.out.println("# Zeitr.:" + mod.getModulzeitraumCount() );
 * System.out.println("# Fehler:" + mod.getErrCount());
 * 
 * @author shj
 *
 * changes
 * date			author	comment
 * Apr 7, 07	shj		creation
 */
public class ModulzeitraumCollection {

///////////////////////////////////
// Object vars
///////////////////////////////////	
	private Date m_dMin;
	private Date m_dMax;
	private long m_lErrCount = 0;
	private long m_lDatesCount=0;
	
	private boolean m_bAllowSwapping = false;		//default
	
///////////////////////////////////
// MAIN METHOD: .add
///////////////////////////////////	
	
	/**
	 * Add this Modulzeitraum to the collection.
	 * 
	 * If the modulzeitraum is unparseable, the 
	 * error count is increased by one.
	 * 
	 * Otherwise the peaks (first and last dates) 
	 * are checked against this new Modulzeitraum 
	 * -- and adapted, gegebenenfalls.
	 * @param sModulzeitraum
	 */
	public void add(String sModulzeitraum){
		add(new Modulzeitraum(sModulzeitraum));
	}

	/**
	 * Add this Modulzeitraum to the collection.
	 * 
	 * If the modulzeitraum is unparseable, the 
	 * error count is increased by one.
	 * 
	 * Otherwise the peaks (first and last dates) 
	 * are checked against this new Modulzeitraum 
	 * -- and adapted, gegebenenfalls.
	 * @param modulzeitraum
	 */
	public void add(Modulzeitraum modulzeitraum){
		m_lDatesCount++;
		
		// don't try anything if it could
		// not be parsed
		if(!modulzeitraum.isParsedSuccessfully()){
			m_lErrCount++;
			return;
		}
		
		// if the modulzeitraum had the wrong sequence,
		// it was swapped. If this is disallowed, count 
		// this modulzeitraum as erroneous.
		if(modulzeitraum.isSequenceSwapped() && !m_bAllowSwapping){
			m_lErrCount++;
			return;
		}
		
		// see if there are new peaks:
		if(m_dMax.before(modulzeitraum.getLastDate())) m_dMax = modulzeitraum.getLastDate();
		if(m_dMin.after(modulzeitraum.getFirstDate())) m_dMin = modulzeitraum.getFirstDate();
	}
	
///////////////////////////////////
//	 Properties 
///////////////////////////////////	
	
	/**
	 * @return resulting Modulzeitraum
	 */
	public String getMaxSpan(){
		return getMaxSpanObject().getModulzeitraum();
	}
	
	/**
	 * @return longest Modulzeitraum 
	 */
	public Modulzeitraum getMaxSpanObject(){
		return new Modulzeitraum(m_dMin, m_dMax);
	}
	
	/**
	 * @return number of Modulzeitraeume that were added.
	 */
	public long getModulzeitraumCount(){
		return m_lDatesCount;
	}
	
	/**
	 * @return true, if no parsing errors occurred.
	 */
	public boolean isSuccessfullyParsed(){
		return (getErrCount()==0);
	}
	
	/**
	 * @return number of parsing errors encountered
	 */
	public long getErrCount(){
		return m_lErrCount;
	}
	
///////////////////////////////////
//	 Little tweaks
///////////////////////////////////	
	
	/**
	 * If you allow swapping, a Modulzeitraum 
	 * such as "1.12.2007-1.11.2007" is silently 
	 * turned into the right sequence.
	 * 
	 * If you disallow swapping (which is the 
	 * default), a Modulzeitraum with such a 
	 * wrong sequence counts as not parsed.
	 * @param bAllow
	 */
	public void setAllowSwapping(boolean bAllow){
		m_bAllowSwapping = bAllow;
	}
	
	/**
	 * Reset object vars, but not the setting for 
	 * "AllowSwapping".
	 * @throws Exception
	 */
	public void reset() throws Exception{
		SimpleDateFormat sdf=new SimpleDateFormat ("dd.MM.yyyy");
		m_dMax = sdf.parse("01.01.1960");
		m_dMin = sdf.parse("31.12.2070");
		m_lDatesCount=0;
		m_lErrCount=0;
	}
///////////////////////////////////
//	 Constructor
//////////////////////////////////	/	
	
	/**
	 * Empty constructor
	 */
	public ModulzeitraumCollection(){
		try {
			reset();
		} catch (Exception e) {}
		
	}
}
