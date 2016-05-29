package de.shj.UP.transcript;


public class Modul {
	private long m_lModulID=-1;
	private String m_sModulBezeichnung="";
        private String m_sModulBezeichnung_en="";
	private float m_fCreditPtSumDemanded=-1;
	private float m_fCreditPtSumAcquired=-1;
	private String m_sModulnote="-1";
	private float m_fWeight=-1;
	
	public long getModulID(){
		return m_lModulID;
	}
	
	public String getModulBezeichnung(){
		return m_sModulBezeichnung;
	}
	
        public String getModulBezeichnung_en(){
		return m_sModulBezeichnung_en;
	}
	public float getModulCreditPtSumDemanded(){
		return m_fCreditPtSumDemanded;
	}
	
	public float getModulCreditPtSumAcquired(){
		return m_fCreditPtSumAcquired;
	}
	
	public String getModulnote(){
		return m_sModulnote;
	}
	
	public float getWeight(){
		return m_fWeight;
	}
        
        // November 2013: falls die schlechteste
        // Modulnote nicht zählt, wird sei so 
        // überschrieben.
        public void setModulnote(String sModulnote){
            this.m_sModulnote=sModulnote;
        }
	
	public String toJSON(){
		return "{\"ModulID\":\"" + m_lModulID + "\", " +
				"\"ModulBezeichnung\":\"" + m_sModulBezeichnung + "\", " +
				"\"ModulCreditPtDemanded\":\"" + m_fCreditPtSumDemanded + "\", " +
				"\"ModulCreditPtAcquired\":\"" + m_fCreditPtSumAcquired + "\", " +
				"\"Modulnote\":\"" + m_sModulnote + "\", " +
				"\"Modulgewicht\":\"" + m_fWeight + "\"}";
	}
	
	public Modul(long lModulID, 
				String sModulBezeichnung, 
                                String sModulBezeichnung_en, 
				float fCreditPtSumDemanded, 
				float fCreditPtSumAcquired, 
				String sModulnote, 
				float fWeight){

		m_lModulID=lModulID;
		m_sModulBezeichnung=sModulBezeichnung;
                m_sModulBezeichnung_en=sModulBezeichnung_en;
		m_fCreditPtSumAcquired=fCreditPtSumAcquired;
		m_fCreditPtSumDemanded=fCreditPtSumDemanded;
		m_sModulnote=sModulnote;
		m_fWeight=fWeight;
	}
}
