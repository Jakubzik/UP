package de.shj.UP.transcript;

// Informationen zum Package
// ==================================
// Hier der Versuch, die 
// Trankripte und Prüfungsverwaltung
// neu aufzubauen.
//
// Das mittelfristige Ziel ist es, nach und nach 
// die Legacy abzubauen (insbes. 
// die Schleifen in den Studienbilanzen 
// und den Transkripten).
//
// Das kurzfristige Ziel des Packages
// ist es, die Fachnote berechenbar 
// zu machen (und möglichst die Kontrolle
// einfach zu halten)
//
// Informationen zur Klasse
// ==================================
// Wird initialisiert mit
// * Student
// * mündl. Note
// * schriftl. Note
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.ResultSet;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import de.shj.UP.data.shjCore;
import de.shj.UP.util.ResultSetSHJ;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Fachnote extends shjCore {

	private ResultSetSHJ m_Modules;
	private boolean m_bIsInitialized=false;
	private String m_sExplanation="Modul;LP lt. PO;erreichte LP;Modulnote;Modulnotengewicht\n";
	private String m_sErr="";
	private String m_sWarning=""; // Overbooking, wenn erlaubt.
	private String m_sMatrikelnummer;
        private String m_sGPA_CSV="";
        private String m_sGPA_CSV_HEADINGS="";
        private int g_DEBUG_LEVEL=12;
        
	// Falls Module nicht in die 
	// Notenberechnung einbezogen werden,
	// kann das Überbuchen (=zu viele Leistungspunkte)
	// erlaubt werden.
	// Anstatt als Fehler taucht die Information
	// dann als Warnung auf.
	private boolean m_bAllowOverbookingInZeroWeightModules=true;
	private long m_lSeminarID;
	private int m_iFachID;
	private long m_lPruefungID;
	private BigDecimal m_fFachnote=new BigDecimal("-1");
	private BigDecimal m_fPreliminaryFachnote=new BigDecimal("-1");
	private Modul m_ModulInfo[] = new Modul[50];
	private int m_iModulInfoCount=0;
	private byte m_bModulesServed=0;
	private BigDecimal m_fFachSumDemanded=new BigDecimal("0");
	private BigDecimal m_fFachSumAcquired=new BigDecimal("0");
	private static final byte m_bMODULES_SERVED_UNCHECKED=0;
	private static final byte m_bMODULES_SERVED_TRUE=1;
	private static final byte m_bMODULES_SERVED_FALSE=2;
	
	// Berechnungsmethode: soll die Modulnote
	// genau berechnet werden? Oder soll nach 
	// der ersten Kommastelle abgeschnitten werden?
	private boolean m_bMODULNOTE_GENAU = false;

	
/////////////////////////////////////////////////////////////
// Private Methoden
/////////////////////////////////////////////////////////////

	// Berechnungsmethode: soll die Modulnote
	// genau berechnet werden? Oder soll nach 
	// der ersten Kommastelle abgeschnitten werden?
	// DEFAULT: false
	public boolean isModulnoteGenau(){
		return m_bMODULNOTE_GENAU;
	}

	// Berechnungsmethode: soll die Modulnote
	// genau berechnet werden? Oder soll nach 
	// der ersten Kommastelle abgeschnitten werden?
	// DEFAULT: false	
	public void setModulnoteGenau(boolean value){
		m_bMODULNOTE_GENAU=value;
	}
	
        /**
         * Array mit Modulobjekten mit den Eigenschaften:
         * id
         * lp_gesamt
         * lp_erreicht
         * name
         * name_en
         * note
         * gewicht
         * 
         * Es fehlen allerdings all diejenigen Module, in denen 
         * keine Leistungen erbracht worden sind!
         * @return 
         */
        public String getModuleJSON(){
            if(!m_bIsInitialized) calculateFachnote();
            String sReturn = "";
            for(int ii=0;ii<m_iModulInfoCount;ii++){
                if(ii>0) sReturn +=",";
                sReturn += "{\"id\":\"" + m_ModulInfo[ii].getModulID() + "\"," +
                        "\"lp_gesamt\":\"" + m_ModulInfo[ii].getModulCreditPtSumDemanded() + "\"," + 
                        "\"lp_erreicht\":\"" + m_ModulInfo[ii].getModulCreditPtSumAcquired()+ "\"," + 
                        "\"name\":\"" + m_ModulInfo[ii].getModulBezeichnung().trim() + "\"," + 
                        "\"name_en\":\"" + m_ModulInfo[ii].getModulBezeichnung_en().trim() + "\"," + 
                        "\"note\":\"" + m_ModulInfo[ii].getModulnote().trim() + "\"," + 
                        "\"gewicht\":\"" + m_ModulInfo[ii].getWeight() + "\"}";
            }
            return "[" + sReturn.replace("\n", "") + "]";
        }
        
	public Modul getModul(long lModulID)throws Exception{
		if(!m_bIsInitialized) calculateFachnote();
		for(int ii=0;ii<m_iModulInfoCount;ii++){
//			System.out.println("Parsing Modul " + ii + " of " + m_iModulInfoCount + "; ID: " + m_ModulInfo[ii].getModulID());
			if(m_ModulInfo[ii].getModulID()==lModulID) return m_ModulInfo[ii];
		}
		return null;
	}
	
	/**
	 * Besonderheiten:
	 * PrüfungXModul.sngPruefungLeistungGewichtung ist entweder
	 * * 0 	(die Modulnote geht nicht in die Fachnote ein),
	 * * >0 (die Modulnote geht mit genau diesem Faktor in die Fachnote ein), oder
	 * * <0 (das Modul wird ignoriert).
	 * (Ignoriert wird z.B. das Modul der BA-Arbeit, da diese nicht zur Fachnote zählt).
	 */
	private void calculateFachnote(){
		
		m_bIsInitialized=true;
		
		// Welche Prüfung ist für das Fach als "Abschlussprüfung"
		// registriert?
		try{
                    ResultSet rPrfg = sqlQuery("select \"lngPruefungID\" from \"tblSdPruefungXFach\" where " +
                        "\"lngSdSeminarID\"=" + m_lSeminarID + 
			" and \"intFachID\"=" + m_iFachID + " and \"blnPruefungFachAbschluss\"=true");
                    rPrfg.next();
                    m_lPruefungID = rPrfg.getLong("lngPruefungID");
		}catch(Exception e){
			m_sErr += "Fehler beim Berechnen der Fachnote: zu Fach '" + m_iFachID + "' (Seminar '" + m_lSeminarID + "') kann keine Prüfung gefunden werden, die als Abschlussprüfung " +
						"gekennzeichnet wäre (für die also tblSdPruefungXFach.blnPruefungFachAbschluss==true gälte).";
			return;
		}
		
		// Sind alle Module (mit einem Gewicht>=0) absolviert?
		// Sonst wird gleich abgebrochen.
		// @TODO nicht gleich abbrechen...
		try {
			if(!allModulesServed());	// return;
		} catch (Exception e) {
			m_sErr += "Fehler beim Aufruf von calculateFachnote (allModulesServed): " + getStackTrace(e);
			return;
		}

		if(m_Modules==null)
			try {
				init();
			} catch (Exception e) {
				m_sErr += "Fehler beim Aufruf von calculateFachnote (init): " + getStackTrace(e);
				return;
			}

		BigDecimal fModulnote;
		BigDecimal fScalarProduct=new BigDecimal("0");
		BigDecimal fActualDivisor=new BigDecimal("0");
		
		// Iteriere Module
		while(m_Modules.next()){

			// Initialisiere Mini-Modul Objekt,
			// das auch eine Trendnote speichern
			// kann.

			m_ModulInfo[m_iModulInfoCount++]=new Modul(
					m_Modules.getLong("lngModulID"),
					m_Modules.getString("strModulBezeichnung"),
                                        m_Modules.getString("strModulBezeichnung_en"),
					m_Modules.getFloat("sum_demanded"),
					m_Modules.getFloat("sum_acquired"),
					(m_Modules.getFloat("actual_divisor")!=0) ? 
							(m_bMODULNOTE_GENAU ? 
									(new BigDecimal(String.valueOf(m_Modules.getFloat("scalar_product"))).divide(new BigDecimal(String.valueOf(m_Modules.getFloat("actual_divisor"))), 10, RoundingMode.HALF_UP).toString()) : 
									(new BigDecimal(String.valueOf(m_Modules.getFloat("scalar_product"))).divide(new BigDecimal(String.valueOf(m_Modules.getFloat("actual_divisor"))), 1, RoundingMode.FLOOR).toString())) 
							: "0",
					m_Modules.getFloat("weight")
			);
			
			// Gewichtungen unter 0 sind Escape-Sequenzen:
			// solche Module (z.B. "Zusatzmodul", BA-Arbeit) werden für 
			// die Fachnote komplett ignoriert.
			if(m_Modules.getFloat("weight")>=0){

				// Die geforderten und erreichten LP werden
				// notiert -- zwar nicht für die Berechnung 
				// der Note, aber für die Methoden .getSummeLPGefordert
				// und .getSummeLPERreicht.
				m_fFachSumDemanded=m_fFachSumDemanded.add(new BigDecimal(m_Modules.getFloat("sum_demanded")));
				m_fFachSumAcquired=m_fFachSumAcquired.add(new BigDecimal(m_Modules.getFloat("sum_acquired")));
				
				if(m_Modules.getFloat("sum_demanded") != m_Modules.getFloat("sum_acquired")){
					// Die Summe der erreichten Leistungspunkte entspricht
					// nicht der Summe der erwarteten Leistungspunkte.
					// Damit ist die Fachnote nicht berechenbar. Eine 
					// Fehlermeldung wird zusammengestellt
					//
					// Wenn das Modul aber für die Note nicht zählt,
					// wird möglicherweise eine _Überbuchung_ zugelassen.
					
					if(m_Modules.getFloat("weight")==0 && m_Modules.getFloat("sum_demanded") < m_Modules.getFloat("sum_acquired") && m_bAllowOverbookingInZeroWeightModules){
						m_sWarning += "Im Modul '" + m_Modules.getString("strModulBezeichnung") + "' sind " + m_Modules.getFloat("sum_acquired") + " anstatt der erwarteten " + m_Modules.getFloat("sum_demanded") + " Leistungspunkte erbracht.\n";
					}else{
						m_sErr += "Im Modul '" + m_Modules.getString("strModulBezeichnung") + "' sind " + m_Modules.getFloat("sum_acquired") + " anstatt der erwarteten " + m_Modules.getFloat("sum_demanded") + " Leistungspunkte erbracht.\n";
					}
					
				}else{
					// Das Modul ist absolviert, jetzt kann die Modulnote 
					// berechnet werden.
					// Sie ergibt sich aus
					// ~ Skalarprodukt LP*Note durch 'actual_divisor'. ~~
					// 
					// actual_divisor ist dabei die Summe aller
					// LP, deren Note einen kalkulierbaren
					// Wert hat (also nicht null ist, wie bei "anerkannten" Scheinen).
					//
					// Ist der 'actual_divisor' 0, hat diese 
					// Modul die Note "0" (unbenotet) und geht
					// nicht in die Berechnung der Fachnote ein.
					if(m_Modules.getFloat("actual_divisor")==0) fModulnote=new BigDecimal("0");
					else{
                                            // neu November 2013: 
                                            // Möglichkeit, die schlechteste Note auszulassen:
                                            // Erweiterung Februar 2015, auch für Modul 10100010 im Lehramt
                                            // eingeführt.
                                            // @TODO: braucht ein Flag in PruefungXModul.
                                            BigDecimal bScalarProd=new BigDecimal(String.valueOf(m_Modules.getFloat("scalar_product")));
                                            BigDecimal bDivisor=new BigDecimal(String.valueOf(m_Modules.getFloat("actual_divisor")));
                                            if((m_Modules.getLong("lngModulID")==1100 || m_Modules.getLong("lngModulID")==10100010) && m_lSeminarID==5){
                                                BigDecimal bWorstGrade=new BigDecimal(String.valueOf(m_Modules.getFloat("worst_grade")));
                                                try{
                                                    BigDecimal bWorstGradeLP = getWorstGradeLP(m_Modules.getLong("lngModulID"), bWorstGrade);
                                                
                                                    bScalarProd=bScalarProd.subtract(bWorstGradeLP.multiply(bWorstGrade));
                                                    bDivisor=bDivisor.subtract(bWorstGradeLP);
                                                    m_sWarning += "Im Modul '" + m_Modules.getString("strModulBezeichnung") + "' wurde die Note " + bWorstGrade.toPlainString() + " (" + bWorstGradeLP.toPlainString() + " LP) bei der Berechnung der Modulnote ausgelassen.\n";
                                                }catch(Exception eNotPossible){System.err.println("Kann die schlechteste Note nicht weglassen, sorry.");}
                                            }

						if(m_bMODULNOTE_GENAU)	fModulnote = bScalarProd.divide(bDivisor, 10, RoundingMode.HALF_UP);
						else {
                                                    fModulnote = bScalarProd.divide(bDivisor, 1, RoundingMode.FLOOR);
                                                }

                                                m_ModulInfo[m_iModulInfoCount-1].setModulnote(String.valueOf(fModulnote));
					}
					
					// Für die Fachnote wird die Modulnote
					// mit den geforderten Leistungspunkten gewichtet.
					// Ist die Modulnote 0, geht das Modul
					// nicht in die Berechnung der Fachnote ein.
					// 
					// Escape-Gewichtungen (<0) sind hier schon aus-
					// geschlossen, die positiven Gewichtungen gehen
					// direkt ein.
					//fScalarProduct += m_Modules.getFloat("sum_demanded")*fModulnote*m_Modules.getFloat("weight");
					//fActualDivisor += (fModulnote==0) ? 0 : m_Modules.getFloat("sum_demanded")*m_Modules.getFloat("weight");
					BigDecimal summand=(new BigDecimal(m_Modules.getFloat("sum_demanded"))).
									multiply(fModulnote).
									multiply(new BigDecimal(m_Modules.getFloat("weight")));
					
					// System.out.println("vorher: " + fScalarProduct.toString());
					fScalarProduct=new BigDecimal(fScalarProduct.add(summand).toString());
					// System.out.println("nach Addieren von " + summand.toString() + ": " + fScalarProduct.toString()+ "\n\n");
					
					fActualDivisor=fActualDivisor.add((fModulnote.floatValue()==0) ? new BigDecimal("0") : new BigDecimal(m_Modules.getString("sum_demanded")).multiply(new BigDecimal(m_Modules.getString("weight"))));
					
					m_sExplanation += m_Modules.getString("strModulBezeichnung") + ";" + 
									m_Modules.getFloat("sum_demanded") + ";" + 
									m_Modules.getFloat("sum_acquired") + ";" + 
									String.valueOf(fModulnote) + ";" + 
									m_Modules.getFloat("weight") + ";\n";
				}
			}
		}
		
		// Fachnote berechnen, falls 
		// im Prinzip alle Leistungen
		// absolviert sind:
		if(isComplete()){	
                        m_sExplanation += "\n\n <=> " + fScalarProduct + " / " + fActualDivisor;
			m_fFachnote = fScalarProduct.divide(fActualDivisor, 3, RoundingMode.HALF_UP);
		}else{
                        m_sExplanation += "\n\n <=> " + fScalarProduct + " / " + fActualDivisor;
			m_fPreliminaryFachnote = (fActualDivisor.floatValue()!=0) ? fScalarProduct.divide(fActualDivisor, 3, RoundingMode.HALF_UP) : new BigDecimal("0");
		}
	}
	
        /**
         * (Neu Nov 2013)
         * Falls ein Modul so konfiguriert ist, dass die 
         * schlechteste Leistungs nicht zur Modulnote zählt,
         * liefert die Master-Abfrage zwar die schlechteste Note,
         * nicht aber deren LP Zahl.
         * 
         * Hier kann die LP Zahl der schlechtesten Note abgefragt werden.
         * @param lModulID
         * @param bWorstGrade
         * @return
         * @throws Exception 
         */
        private BigDecimal getWorstGradeLP(long lModulID, BigDecimal bWorstGrade)throws Exception{
            ResultSet r=sqlQuery("select \"sngStudentLeistungCreditPts\" from \"tblBdStudentXLeistung\" x, \"tblSdNote\" n where " +
                    "x.\"lngSdSeminarID\"=" + m_lSeminarID + " and " +
                    "x.\"lngModulID\"=" + lModulID + " and " + 
                    "x.\"strMatrikelnummer\"='" + m_sMatrikelnummer + "' and " + 
                    "n.\"lngSdSeminarID\"=x.\"lngSdSeminarID\" and " + 
                    "n.\"intNoteID\"=x.\"intNoteID\" and " + 
                    "n.\"sngNoteECTSCalc\"=" + bWorstGrade.toPlainString() + 
                    "order by \"sngStudentLeistungCreditPts\" desc");
            if(r.next()) return new BigDecimal(r.getString("sngStudentLeistungCreditPts"));
            else throw new Exception("Kann die schlechteste Note nicht weglassen, sie ist nicht mehr aufzufinden.");
        }
        
	/**
	 * Schaut nach, ob zu allen Modulen in dieser
	 * Prüfungsordnung (mit Gewichtung >=0) zumindest 
	 * ein Schein vorhanden ist.
	 * 
	 * Falls nicht, wird der Fehlermeldungs-String
	 * mit den Namen der fehlenden Module initialisiert.
	 * 
	 * Der weitere Vorgang wird dann abgebrochen.
	 * @return
	 * @throws Exception
	 */
	private boolean allModulesServed() throws Exception{
		if(m_bModulesServed==m_bMODULES_SERVED_UNCHECKED){
			ResultSet rs = sqlQuery("SELECT " +
					  "pxf.\"lngSdSeminarID\"," + 
					  "pxf.\"intFachID\", " +
					  "pxm.\"sngMinCreditPts\", " +
					  "m.\"lngModulID\", " +
					  "m.\"strModulBezeichnung\" " +
					  "FROM \"tblSdModul\" m, \"tblSdPruefungXModul\" pxm, \"tblSdPruefungXFach\" pxf " +
					  "where pxm.\"lngSdSeminarID\" = m.\"lngSdSeminarID\" AND " +  
					  "pxm.\"lngSdSeminarID\" = m.\"lngSdSeminarID\" AND " +
					  "pxm.\"lngModulID\" = m.\"lngModulID\" AND " +
					  "pxm.\"lngSdSeminarID\" = pxf.\"lngSdSeminarID\" AND " +  
					  "pxm.\"lngPruefungID\" = pxf.\"lngPruefungID\" AND " +
					  "pxm.\"sngPruefungLeistungGewichtung\">=0 and " +
					  "pxf.\"lngSdSeminarID\" = " + m_lSeminarID + " AND pxf.\"intFachID\" = " + m_iFachID + " and pxf.\"lngPruefungID\"= " + m_lPruefungID +
					  "and not exists(" +
					  "  select \"lngModulID\" from \"tblBdStudentXLeistung\" x, \"tblSdNote\" n where " +
					  	"x.\"lngSdSeminarID\"=" +  m_lSeminarID + " and " +
					  	"x.\"strMatrikelnummer\"='" + m_sMatrikelnummer + "' and " +
					  	"x.\"lngModulID\"=pxm.\"lngModulID\" and " +
					  	"n.\"lngSdSeminarID\"=x.\"lngSdSeminarID\" and " +
					  	"n.\"intNoteID\"=x.\"intNoteID\" and " + 
					  	"n.\"blnNoteBestanden\"" +
					  ");");
				while(rs.next()){
					m_bModulesServed=m_bMODULES_SERVED_FALSE;
					m_sErr += "Im Modul '" + rs.getString("strModulBezeichnung") + "' wurden noch keine Leistungen erbracht.\n";
					m_fFachSumDemanded.add(new BigDecimal(rs.getFloat("sngMinCreditPts")));
				}
				rs.close();
				if(m_bModulesServed==m_bMODULES_SERVED_UNCHECKED) m_bModulesServed = m_bMODULES_SERVED_TRUE;
		}
		return (m_bModulesServed==m_bMODULES_SERVED_TRUE);
	}
	
	/**
	 * Initialisiert die Haupt-Abfrage:
	 * anhand der Stammdatentabellen (insbes. PrüfungXModul) 
	 * werden alle Module durchgegangen, und zu 
	 * jedem Modul die geforderte LP-Zahl ('sum_demanded') 
	 * mit der aggregierten Zahl der erworbenen LP ('sum_acquired')
	 * ausgegeben.
	 * @throws Exception
	 */
	private void init() throws Exception{
		m_Modules = sqlQuerySHJ("" +
				"SELECT " +
				  "pxf.\"lngSdSeminarID\"," + 
				  "pxf.\"intFachID\", " +
				  "m.\"lngModulID\", " +
				  "m.\"strModulBezeichnung\", " +
                                  "m.\"strModulBezeichnung_en\", " +
				  "\"tblSdPruefungXModul\".\"sngMinCreditPts\" AS sum_demanded," + 
				  "\"tblSdPruefungXModul\".\"sngPruefungLeistungGewichtung\" AS weight," + 
                                  "max(n.\"sngNoteECTSCalc\") as worst_grade," + 
				  "sum(\"tblBdStudentXLeistung\".\"sngStudentLeistungCreditPts\") as sum_acquired," + 
				  "sum(\"tblBdStudentXLeistung\".\"sngStudentLeistungCreditPts\"*n.\"sngNoteECTSCalc\") as scalar_product," +
				  "sum(CASE WHEN n.\"sngNoteECTSCalc\"=0 THEN 0 ELSE \"tblBdStudentXLeistung\".\"sngStudentLeistungCreditPts\" END) as actual_divisor " +
				  "FROM \"tblSdModul\" m, \"tblSdPruefungXModul\", \"tblSdPruefungXFach\" pxf, \"tblBdStudentXLeistung\", \"tblSdNote\" n  " +
				  "WHERE " +
				  "\"tblSdPruefungXModul\".\"lngSdSeminarID\" = m.\"lngSdSeminarID\" AND " +
				  "\"tblSdPruefungXModul\".\"lngModulID\" = m.\"lngModulID\" AND " +
				  "\"tblSdPruefungXModul\".\"lngSdSeminarID\" = pxf.\"lngSdSeminarID\" AND " +  
				  "\"tblSdPruefungXModul\".\"lngPruefungID\" = pxf.\"lngPruefungID\" AND " +
				  "pxf.\"lngSdSeminarID\" = " + m_lSeminarID + " AND pxf.\"intFachID\" = " + m_iFachID + " and pxf.\"lngPruefungID\"= " + m_lPruefungID + " AND " + 
				  "\"tblBdStudentXLeistung\".\"lngSdSeminarID\"=pxf.\"lngSdSeminarID\" AND " +
				  "\"tblBdStudentXLeistung\".\"lngModulID\"=\"tblSdPruefungXModul\".\"lngModulID\" AND " + 
				  "n.\"lngSdSeminarID\"=pxf.\"lngSdSeminarID\" AND " +
				  "n.\"blnNoteBestanden\"=true and " +
				  "n.\"intNoteID\"=\"tblBdStudentXLeistung\".\"intNoteID\" " + 
				  "GROUP BY pxf.\"lngSdSeminarID\", pxf.\"intFachID\", m.\"lngModulID\", m.\"strModulBezeichnung\", m.\"strModulBezeichnung_en\",  \"tblSdPruefungXModul\".\"sngMinCreditPts\", " + 
				  "\"tblSdPruefungXModul\".\"sngPruefungLeistungGewichtung\", " +
				  "\"strMatrikelnummer\" " +
				 "HAVING  " +
				  "\"tblBdStudentXLeistung\".\"strMatrikelnummer\"='" + m_sMatrikelnummer + "' " + 
				  "ORDER BY \"strModulBezeichnung\" ASC;");
	}
	
	 public static String getStackTrace(Throwable aThrowable) {
		    final Writer result = new StringWriter();
		    final PrintWriter printWriter = new PrintWriter(result);
		    aThrowable.printStackTrace(printWriter);
		    return result.toString();
	  }
	
/////////////////////////////////////////////////////////////
// Öffentliche Methoden
/////////////////////////////////////////////////////////////	

	 // Funktioniert nicht (aus Gründen, die ich 
	 // nicht verstanden habe; ein anderes Problem
	 // sind aber ohnehin Wahlpflichtmodule, 
	 // die bei der hier programmierten Systematik
	 // immer doppelt gezählt würden).
//	 public float getSummeLPGefordert(){
//		if(!m_bIsInitialized) calculateFachnote();
//		return (m_fFachSumDemanded);
//}
	
	public BigDecimal getSummeLPErreicht(){
			if(!m_bIsInitialized) calculateFachnote();
			return (m_fFachSumAcquired);
	}
	 
	/**
	 * Sind alle Leistungen erbracht, 
	 * so dass eine Fachnote berechnet
	 * werden kann?
	 * @return
	 */
	public boolean isComplete(){
		if(!m_bIsInitialized) calculateFachnote();
		return (m_sErr.equals(""));
	}
	
	public String getErr(){
		if(!m_bIsInitialized) calculateFachnote();
		return m_sErr;
	}
	
	public String getWarning(){
		if(!m_bIsInitialized) calculateFachnote();
		return m_sWarning;
	}
	
	
	/**
	 * Voreinstellung: true
	 * In Modulen, die nicht in die Fachnote zählen,
	 * wird dann eine Überbuchung (zu viele LP) nicht
	 * als Fehler gewertet.
	 * (Die Information über die Überbuchung ist statt-
	 * dessen als Warnung verfügbar).
	 * @param bAllow
	 */
	public void setAllowOverbookingInZeroWeightModules(boolean bAllow){
		m_bAllowOverbookingInZeroWeightModules=bAllow;
	}
	
	/**
	 * Fachnote (gerundet auf eine Stelle 
	 * hinterm Komma), falls alle Noten 
	 * vorhanden sind.
	 * 
	 * Falls noch Noten/Leistungspunkte 
	 * fehlen, leerer String.
	 * @return
	 */
	public String getGrade(){
		if(!m_bIsInitialized) calculateFachnote();
		if(!isComplete()) return "";
		return String.valueOf(m_fFachnote).substring(0, String.valueOf(m_fFachnote).indexOf('.')+2);
	}
	
	public BigDecimal getGradeUnrounded(){
		if(!m_bIsInitialized) calculateFachnote();
		if(!isComplete()) return new BigDecimal(0);
		return m_fFachnote;
	}
	
	public String getPreliminaryGrade(){
		if(!m_bIsInitialized) calculateFachnote();
		if(isComplete()) return "";
		return String.valueOf(m_fPreliminaryFachnote.floatValue()).substring(0, String.valueOf(m_fPreliminaryFachnote).indexOf('.')+2);
	}
	
        public BigDecimal getPreliminaryGradeUnrounded(){
		if(!m_bIsInitialized) calculateFachnote();
		if(isComplete()) return new BigDecimal(0);
		return m_fPreliminaryFachnote;
	}
        
	/**
	 * Deutsche Bezeichnung ("sehr gut, gut" etc.)
	 * @return
	 */
	public String getGradeName(){
		if(!m_bIsInitialized) calculateFachnote();
		if(!isComplete()) return "";
		if(m_fFachnote.floatValue()<1.6) return "sehr gut";
		if(m_fFachnote.floatValue()<2.6) return "gut";
		if(m_fFachnote.floatValue()<3.6) return "befriedigend";
		if(m_fFachnote.floatValue()<4.1) return "ausreichend";
		return "ungenügend";
	}
	
        /**
         * Februar 2015: liefert fürs GPA:
         * Matrikelnummer | Fach | Modulbez | Modulbez_en | LP | Gewichtung | Note
         * @return 
         */
        public String getGPA_CSV(boolean bWithHeadings){
            if(!m_bIsInitialized) calculateFachnote();
            
            if(m_sGPA_CSV.length()<=0){
                m_sGPA_CSV_HEADINGS="Matrikelnummer,Fach,Fachnote"; //reset
                
                // Achtung, manuell, #hack, @todo:
                String f_sFach="\"Fach unbekannt: " + m_iFachID + "\"";
                String iAnglistik = ",92402,92302,92202,83502,83504,83505,83907,200216,200317,200418,200519,200620,200721,200822,200923,210810,210811,210812,210813,210814,210915,211016,211117,211218,211319,211420,";
                String iGermanistik=",6702,6704,6705,-10,-20,200115,300000,300020,300010,300100,300120,300110.";
                
                if(iAnglistik.contains("," +  m_iFachID + ",")) f_sFach="\"Anglistik\"";
                if(iGermanistik.contains("," +  m_iFachID + ",")) f_sFach="\"Germanistik\"";
                
                m_sGPA_CSV=m_sGPA_CSV += m_sMatrikelnummer + "," + f_sFach + "," + getGrade();
                for(int ii=0;ii<m_iModulInfoCount;ii++){   
                    if(bWithHeadings) m_sGPA_CSV_HEADINGS+=",Modulbezeichnung,Modulbezeichnung_en,LP,Modulnote,Gewichtung";
                    m_sGPA_CSV += ",\"" + m_ModulInfo[ii].getModulBezeichnung() + "\",\"" +
                            string2JSON(m_ModulInfo[ii].getModulBezeichnung_en()).replace("\n", "").replace("\r","") + 
                            "\"," +((m_ModulInfo[ii].getModulCreditPtSumAcquired() != m_ModulInfo[ii].getModulCreditPtSumDemanded()) ? 0 : m_ModulInfo[ii].getModulCreditPtSumDemanded()) + "," +
                            m_ModulInfo[ii].getModulnote() + "," +
                            m_ModulInfo[ii].getWeight();
                }
                try {
                    ResultSet rs = sqlQuery("SELECT " +
                            "pxf.\"lngSdSeminarID\"," +
                            "pxf.\"intFachID\", " +
                            "pxm.\"sngMinCreditPts\", " +
                            "pxm.\"sngPruefungLeistungGewichtung\"," +
                            "m.\"lngModulID\", " +
                            "m.\"strModulBezeichnung_en\", " +
                            "m.\"strModulBezeichnung\" " +
                            "FROM \"tblSdModul\" m, \"tblSdPruefungXModul\" pxm, \"tblSdPruefungXFach\" pxf " +
                            "where pxm.\"lngSdSeminarID\" = m.\"lngSdSeminarID\" AND " +
                            "pxm.\"lngSdSeminarID\" = m.\"lngSdSeminarID\" AND " +
                            "pxm.\"lngModulID\" = m.\"lngModulID\" AND " +
                            "pxm.\"lngSdSeminarID\" = pxf.\"lngSdSeminarID\" AND " +
                            "pxm.\"lngPruefungID\" = pxf.\"lngPruefungID\" AND " +
                            // "pxm.\"sngPruefungLeistungGewichtung\">=0 and " +
                            "pxf.\"lngSdSeminarID\" = " + m_lSeminarID + " AND pxf.\"intFachID\" = " + m_iFachID + " and pxf.\"lngPruefungID\"= " + m_lPruefungID +
                            "and not exists(" +
                            "  select \"lngModulID\" from \"tblBdStudentXLeistung\" x, \"tblSdNote\" n where " +
                            "x.\"lngSdSeminarID\"=" +  m_lSeminarID + " and " +
                            "x.\"strMatrikelnummer\"='" + m_sMatrikelnummer + "' and " +
                            "x.\"lngModulID\"=pxm.\"lngModulID\" and " +
                            "n.\"lngSdSeminarID\"=x.\"lngSdSeminarID\" and " +
                            "n.\"intNoteID\"=x.\"intNoteID\" and " +
                            "n.\"blnNoteBestanden\"" +
                            ");");
                    while(rs.next()){
                            if(bWithHeadings) m_sGPA_CSV_HEADINGS+=",Modulbezeichnung,Modulbezeichnung_en,LP,Modulnote,Gewichtung";
                            m_sGPA_CSV += ",\"" + rs.getString("strModulBezeichnung") + "\",\"" +
                                        rs.getString("strModulBezeichnung_en").replace("\n", "").replace("\r","")+ "\"," +
                                        "0," +
                                        "0," +
                                        rs.getString("sngPruefungLeistungGewichtung");
                    }
                    rs.close();
                } catch (Exception ex) {
                    m_sGPA_CSV += m_sMatrikelnummer + "," + f_sFach + ",\",FEHLER!";
                    Logger.getLogger(Fachnote.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            if(bWithHeadings) return m_sGPA_CSV_HEADINGS + "\n" + m_sGPA_CSV;
            else return m_sGPA_CSV;
        }
	/**
	 * Berechnung der Fachnote.
	 * @return
	 */
	public String explain(){
		if(!m_bIsInitialized) calculateFachnote();
		if(!getErr().equals("")) return "Fehler bei der Berechnung:\n" + getErr() + (getWarning().equals("") ? "" : "\nWarnungen:" + getWarning());
		return m_sExplanation;
	}
	
	public String peek(){
		return m_sExplanation;
	}
	
	/**
	 * Beispielsweise Benutzung:
	 * 
	 * Ausgabe der Fachnote:
	 * Fachnote note = new Fachnote('123', 1, 8351, 1);
	 * 
	 * if(note.isComplete()){
	 *    System.out.println(note.getGrade());
	 *    
	 *    // Ggf. Details der Berechnung:
	 *    // System.out.println(note.explain()); 
	 * else{
	 * 	  // Informationen zum Problem
	 * 	  System.err.println(note.explain());
	 * }
	 * 
	 * @param sMatrikelnummer
	 * @param lSeminarID
	 * @param iFachID
	 * @param lPruefungID
	 */
	public Fachnote(String sMatrikelnummer, long lSeminarID, int iFachID){
		m_sMatrikelnummer=sMatrikelnummer;m_lSeminarID=lSeminarID; m_iFachID=iFachID; 
	}
}
