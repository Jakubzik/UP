<%@ page contentType="text/json" pageEncoding="UTF-8" import="de.shj.UP.data.KvvArchiv,java.util.Enumeration,java.text.SimpleDateFormat,de.shj.UP.data.Dozent,de.shj.UP.data.shjCore" session="true" isThreadSafe="false" errorPage="../error.jsp"%>
<%@page import="java.util.Locale"%>
<%@page import="de.shj.UP.data.StudentXLeistung"%><jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean id="seminar" scope="session" class="de.shj.UP.HTML.HtmlSeminar" />
<jsp:useBean id="student" scope="session" class="de.shj.UP.beans.config.student.StudentBean" />
<jsp:useBean id="credits" scope="page" class="de.shj.UP.beans.config.student.StudentLeistungBean" />
<%!String string2JSON(String s) {
		return (s == null) ? "" : s.replace('\n', ' ').replace('\r', ' ')
				.replace('\t', ' ').replaceAll("\"", "\\\\\"");
	}

	String nn(Object s) {
		return (s == null) ? "" : (((String) s).equals("null") ? ""
				: (String) s);
	}

	/**
	 * hj März 2012
	 *
	 * Setzt in Objekt o die Werte aus dem Request r. 
	 * 
	 * In Betracht gezogen werden nur solche Parameter,
	 * deren Namen mit "txt" oder "cbo" beginnen.
	 * 
	 * Bsp: Enthält "r" den Parameter "txtName" und 
	 *      enthält das Objekt "o" die Methoden 
	 *		o.getName() und o.setName(), dann wird
	 * 		o.setName(r.getParameter("txtName"))
	 *	    ausgeführt.
	 *
	 *		Enthält "r" den Parameter "cmdName", geschieht
	 *		nichts.
	 * @param o
	 * @param r
	 * @return Zur Fehlersuche wird Information über die aufgerufenen
	 *  Methoden ausgegeben.
	 */
	public String mapRequestToObject(Object o, HttpServletRequest r) {

		// ------------------------------------------
		// Initialisierung
		String sReturn = ""; // Info zur Fehlersuche
		int ii = o.getClass().getMethods().length; // Anzahl der Methoden in "o" 
		String sMethodName = ""; // tmp var 
		String sMethodNameCore = ""; // tmp var 
		Class c_Type = null; // Hilfsobjekte für den Methodenaufruf 
		String sType = ""; // -- "" "" -- -- "" "" -- -- "" "" --
		String sHtmlPrefix = ""; // txt or cbo

		// ------------------------------------------
		// Alle Mehoden von "o" werden durchlaufen:
		for (int ij = 0; ij < ii; ij++) {
			sMethodName = o.getClass().getMethods()[ij].getName();

			// Es handelt sich um einen "setter" 
			if (sMethodName.startsWith("set")) {

				// Welche Eigenschaft wird gesetzt?
				sMethodNameCore = sMethodName.substring(3);

				// Gibt es im Request "r" eine Entsprechung?
				if ((r.getParameter("txt" + sMethodNameCore) != null)
						|| ((r.getParameter("cbo" + sMethodNameCore) != null))) {

					// Präfix: cbo? txt?
					sHtmlPrefix = (r.getParameter("txt" + sMethodNameCore) == null ? "cbo"
							: "txt");

					// Welchen Typ erwartet der setter?
					try {
						c_Type = o.getClass().getMethod(
								"get" + sMethodNameCore, new Class[] {})
								.getReturnType();
					} catch (Exception e1) {
						sReturn += "# ERR: method 'get" + sMethodNameCore
								+ "()' not found";
					}

					sType = c_Type.getName();

					// Fehlersucheninfo
					sReturn += "* setting '" + sMethodNameCore + "' to '"
							+ r.getParameter(sHtmlPrefix + sMethodNameCore)
							+ "' (Type: " + sType + ")\n\n";

					// ------------------------------------------------
					// Setter wird aufgerufen, Parameter ggf. ge-castet
					// * Long
					if (sType.equals("long")) {
						try {
							o.getClass().getMethod("set" + sMethodNameCore,
									new Class[] { Long.TYPE }).invoke(
									o,
									new Object[] { new Long(Long.parseLong(r
											.getParameter(sHtmlPrefix
													+ sMethodNameCore))) });
						} catch (Exception e) {
							sReturn += " ... failed:" + e.toString()
									+ "\n\n\n\n";
						}
					}

					// * Double
					if (sType.equals("double")) {
						try {
							o
									.getClass()
									.getMethod("set" + sMethodNameCore,
											new Class[] { Double.TYPE })
									.invoke(
											o,
											new Object[] { new Double(
													Double
															.parseDouble(r
																	.getParameter(sHtmlPrefix
																			+ sMethodNameCore))) });
						} catch (Exception e) {
							sReturn += " ... failed:" + e.toString()
									+ "\n\n\n\n";
						}
					}

					// * Int
					if (sType.equals("int")) {
						try {
							o
									.getClass()
									.getMethod("set" + sMethodNameCore,
											new Class[] { Integer.TYPE })
									.invoke(
											o,
											new Object[] { new Integer(
													Integer
															.parseInt(r
																	.getParameter(sHtmlPrefix
																			+ sMethodNameCore))) });
						} catch (Exception e) {
							sReturn += " ... failed:" + e.toString()
									+ "\n\n\n\n";
						}
					}

					// * Float
					if (sType.equals("float")) {
						try {
							o.getClass().getMethod("set" + sMethodNameCore,
									new Class[] { Float.TYPE }).invoke(
									o,
									new Object[] { new Float(Float.parseFloat(r
											.getParameter(sHtmlPrefix
													+ sMethodNameCore))) });
						} catch (Exception e) {
							sReturn += " ... failed:" + e.toString()
									+ "\n\n\n\n";
						}
					}

					// * Date (wir versuchen's erst ISO, dann deutsch)
					if (sType.equals("java.sql.Date")) {
						try {
							SimpleDateFormat sdf = new SimpleDateFormat(
									"yyyy-MM-dd");
							java.util.Date d = sdf
									.parse(r.getParameter(sHtmlPrefix
											+ sMethodNameCore));
							o.getClass().getMethod(
									"set" + sMethodNameCore,
									new Class[] { Class
											.forName("java.sql.Date") })
									.invoke(
											o,
											new Object[] { new java.sql.Date(d
													.getTime()) });
						} catch (Exception e) {
							try {
								SimpleDateFormat sdf = new SimpleDateFormat(
										"dd.MM.yyyy");
								java.util.Date d = sdf.parse(r
										.getParameter(sHtmlPrefix
												+ sMethodNameCore));
								o
										.getClass()
										.getMethod(
												"set" + sMethodNameCore,
												new Class[] { Class
														.forName("java.sql.Date") })
										.invoke(
												o,
												new Object[] { new java.sql.Date(
														d.getTime()) });
							} catch (Exception e2) {
								sReturn += " ... failed:" + e.toString()
										+ "\nGerman format also failed:"
										+ e2.toString() + "\n\n\n";
							}
						}
					}

					// * String
					if (sType.equals("java.lang.String")) {
						try {
							o
									.getClass()
									.getMethod(
											"set" + sMethodNameCore,
											new Class[] { Class
													.forName("java.lang.String") })
									.invoke(
											o,
											new Object[] { r
													.getParameter(sHtmlPrefix
															+ sMethodNameCore) });
						} catch (Exception e) {
							sReturn += " ... failed:" + e.toString()
									+ "\n\n\n\n";
						}
					}
				}
			}
		}
		return sReturn;
	}

	/**
	 * Schreibt informationen aus dem Kurs (Titel, Uhrzeit, Beschreibung, Dozent etc.)
	 * in das Objekt "sl", das einen Schein repräsentiert.
	 *
	 * Der Kurs kann aus dem aktuellen Vorlesungsverzeichnis
	 * oder aus dem Archiv stammen.
	 *
	 * Die übergebene ID "sID" hat den Aufbau:
	 * 
	 * "Archive-2533" bezeichnet den Kurs mit der ID 2533 im Archiv, und 
	 * "Current-182" bezeichnet den Kurs mit der ID 182 im aktuellen Verzeichnis.
	 *
         * 'bStrict' lässt keine Kurse aus dem Archiv zu und beschränkt die Wahl
         * außerdem auf Kurse, deren Anmeldefrist noch läuft.
	 **/
	boolean loadCreditWithKursValues(StudentXLeistung sl, String sID, boolean bStrict)
			throws Exception {
		boolean bArchiv = sID.startsWith("Archive");
                if(bArchiv && bStrict) throw new Exception("Sorry, die Anmeldung zu Kursen aus dem Archiv ist Ihnen nicht erlaubt.");
		long lKursID = Long.parseLong(sID.substring(7).trim());
		if (!bArchiv) {
			de.shj.UP.data.Kurs k = new de.shj.UP.data.Kurs(sl
					.getSdSeminarID(), lKursID);
			Dozent dozent = new de.shj.UP.data.Dozent(sl.getSdSeminarID(),
					k.getDozentID());
                        if(bStrict){
                            if(!k.getKursScheinanmeldungErlaubt()){
                                throw new Exception("Sorry, die Anmeldung zu diesem Kurs ist Ihnen nicht erlaubt.");
                            }
                            java.util.Date now=new java.util.Date();
                            if(k.getKursScheinanmeldungVon().after(now) || k.getKursScheinanmeldungBis().before(now)){
                                throw new Exception("Sorry, die Anmeldung zu diesem Kurs ist Ihnen außerhalb der Anmeldefrist nicht erlaubt");
                            }
                        }
			sl.setKlausuranmeldungKursID(lKursID);
			sl.setDozentID(k.getDozentID());
			sl.setStudentLeistungAusstellerVorname(dozent.getDozentVorname());
			sl.setStudentLeistungAussteller(dozent.getDozentNachname());
			sl.setStudentLeistungAusstellerTitel(dozent.getDozentTitel());
			sl.setSLKursTag(k.getKursTag());
			sl.setSLKursBeginn(k.getKursBeginn());
			sl.setSLKursEnde(k.getKursEnde());
			sl.setSLKursRaum(k.getKursRaum());
			sl.setSLKursTag2(k.getKursTag2());
			sl.setSLKursBeginn2(k.getKursBeginn2());
			sl.setSLKursEnde2(k.getKursEnde2());
			sl.setSLKursRaum2(k.getKursRaum2());
			sl.setSLKursTitel(k.getKursTitel());
			sl.setSLKursTitel_en(k.getKursTitel_en());
			sl.setSLKursBeschreibung(k.getKursBeschreibung());
			sl.setSLKursBeschreibung_en(k.getKursBeschreibung_en());
			sl.setSLKursLiteratur(k.getKursLiteratur());
			sl.setSLKursZusatz(k.getKursZusatz());
			sl.setSLKursAnmeldung(k.getKursAnmeldung());
			sl.setSLKursVoraussetzung(k.getKursVoraussetzung());
			sl.setSLKursScheinanmeldungBis(k.getKursScheinanmeldungBis());
			sl.setSLKursScheinanmeldungVon(k.getKursScheinanmeldungVon());
			sl.setSLKursScheinanmeldungErlaubt(k.getKursScheinanmeldungErlaubt());
			sl.setSLKursTerminFreitext(k.getKursTerminFreitext());
			sl.setSLKursTeilnehmer(k.getKursTeilnehmer());
			sl.setSLKursRaumExtern1(k.getKursRaumExtern1());
			sl.setSLKursRaumExtern2(k.getKursRaumExtern2());
		}else{
			KvvArchiv k = new KvvArchiv(lKursID);
			sl.setDozentID(Long.parseLong(sl.lookUp("lngDozentID",
					"tblSdDozent", "\"lngSdSeminarID\"=" + sl.getSdSeminarID()
							+ " and \"strDozentNachname\"='"
							+ k.getKvvArchivDozentname()
							+ "' and \"strDozentVorname\"='"
							+ k.getKvvArchivDozentvorname() + "'")));
			sl.setLeistungsID(k.getKvvArchivLeistungID());
			sl.setKlausuranmeldungKursID(lKursID);
			sl.setStudentLeistungAusstellerVorname(k
					.getKvvArchivDozentvorname());
			sl.setStudentLeistungAussteller(k.getKvvArchivDozentname());
			sl.setSLKursTitel(k.getKvvArchivKurstitel());
			sl.setSLKursTitel_en(k.getKvvArchivKurstitel_en());
			sl.setStudentLeistungAusstellerTitel(k.getKvvArchivDozenttitel());
			sl.setSLKursTag(k.getKvvArchivKursTag());
			sl.setSLKursBeginn(k.getKvvArchivKursBeginn() == null ? null
					: new java.sql.Time(shjCore.g_TIME_FORMAT.parse(
							k.getKvvArchivKursBeginn()).getTime()));
			sl.setSLKursEnde(k.getKvvArchivKursEnde() == null ? null
					: new java.sql.Time(shjCore.g_TIME_FORMAT.parse(
							k.getKvvArchivKursEnde()).getTime()));
			sl.setSLKursRaum(k.getKvvArchivKursRaum());
			sl.setSLKursTag2(k.getKvvArchivKursTag2());
			sl.setSLKursBeginn2(k.getKvvArchivKursBeginn2() == null ? null
					: new java.sql.Time(shjCore.g_TIME_FORMAT.parse(
							k.getKvvArchivKursBeginn2()).getTime()));
			sl.setSLKursEnde2(k.getKvvArchivKursEnde2() == null ? null
					: new java.sql.Time(shjCore.g_TIME_FORMAT.parse(
							k.getKvvArchivKursEnde2()).getTime()));
			sl.setSLKursRaum2(k.getKvvArchivKursKursRaum2());
			sl.setSLKursBeschreibung(k.getKvvArchivKursBeschreibung());
			sl.setSLKursBeschreibung_en(k.getKvvArchivKursBeschreibung_en());
			sl.setSLKursLiteratur(k.getKvvArchivKursLiteratur());
			sl.setSLKursZusatz(k.getKvvArchivKursZusatz());
			sl.setSLKursVoraussetzung(k.getKvvArchivKursVoraussetzung());
			sl.setSLKursSchein(k.getKvvArchivKursSchein());
			sl.setSLKursScheinanmeldungBis(null);
			sl.setSLKursScheinanmeldungVon(null);
			sl.setSLKursTerminFreitext(k.getKvvArchivKursTerminFreitext());
			sl.setSLKursTeilnehmer(k.getKvvArchivKursTeilnehmer());
		}
		return true;
	}%>
<%
/**      Sorry for this chaos :-(
//       (1) Es fehlt bei der Benutzung vom Admin-Konto
//           das Übermitteln eines Moduls
//       (2) Es fehlt im Quelltext eine saubere Trennung
//           der Benutzung durch Admin/Staff oder Student
//       (3) Fehlermanagment: Über throw Exception? Über "{success:false}"?
//           Mehrsprachigkeit!
**/
	String sErrorReport = "";
        boolean bIsCalledFromStudent=(user.getDozentNachname()==null && student.getMatrikelnummer().length()==7);
        System.out.println(user.getDozentNachname() + " ... " + student.getMatrikelnummer());
        // 
        // if(bIsCalledFromStudent) System.out.println("Studierendenanmeldung");
        
	if (!request.getParameter("idxLeistungIDOrig").startsWith("AddNew")) {
		System.out.println("Misunderstanding in json/addNewCredit.jsp");
		return;
	}
        
        // Ob es eine Anmeldung oder Leistung ist hängt
        // entweder am entsprechenden Parameter der 
        // Anfrage. Oder die Anfrage kommt von den Studierenden, 
        // dann ist sie automatisch eine Anmeldung
	boolean bIsCommitment = (request.getParameter("commitment") != null) || bIsCalledFromStudent;

        // Falls das eine Anmeldung aus Studierendensicht ist,
        // handelt es sich _immer_ um ein Commitment:
        
	StudentXLeistung sl = new StudentXLeistung();
        if(!bIsCalledFromStudent){

            sl.setSdSeminarID(user.getSdSeminarID());
        }else {
            // Wenn eine Anmeldung VON STUDIERENDEN gespeichert wird, 
            // muss sie aus dem laufenden Semester sein,
            // d.h. idxLeistungIDOrig muss mit AddNew-Current beginnen.
            if(!(request.getParameter("idxLeistungIDOrig").startsWith("AddNew-Current")|| request.getParameter("idxLeistungIDOrig").startsWith("AddNewCurrent"))) throw new Exception("Studierende müssen einen Kurs auswählen, um die Leistung zu speichern");
            sl.setSdSeminarID(student.getSeminarID());
        }

	// Falls ein Kurs gewählt wurde, hängt die ID aus 
	// Archiv oder aktueller Tabelle hier dran:
	if (request.getParameter("idxLeistungIDOrig").length() > "AddNew"
			.length()) {
		loadCreditWithKursValues(sl, request.getParameter(
				"idxLeistungIDOrig").substring(8),bIsCalledFromStudent);
	}

	if (!bIsCommitment) {
		Dozent dozent = new Dozent(user.getSdSeminarID(), Long
				.parseLong(request.getParameter("txtDozentID")));
		sl.setStudentLeistungAussteller(dozent.getDozentNachname());
		sl.setStudentLeistungAusstellerVorname(dozent
				.getDozentVorname());
		sl.setStudentLeistungAusstellerTitel(dozent.getDozentTitel());
	}

	mapRequestToObject(sl, request);

	if (sl.getNoteID() <= 0 && (!bIsCommitment)) {
		out
				.write("{\"success\":\"false\", \"details\":\"Bitte Note auswählen!\"}");
		return;
	}

	if (!bIsCommitment)
		sl.setLeistungsID(Long.parseLong(request
				.getParameter("idxLeistungIDNew")));
	else
		sl.setLeistungsID(Long.parseLong(request.getParameter("idxLeistungID")));

	String sMaxID = user
			.dbMax("lngStudentLeistungCount", "tblBdStudentXLeistung",
					"\"lngSdSeminarID\"=" + student.getSeminarID()
							+ " and \"strMatrikelnummer\"='"
							+ sl.getMatrikelnummer()
							+ "' and \"lngLeistungsID\"="
							+ sl.getLeistungsID());

	sl.setStudentLeistungCount((sMaxID == null ? 0 : Long
			.parseLong(sMaxID) + 1));

	if (!bIsCommitment) {
                if(bIsCalledFromStudent) throw new Exception("Sorry -- Studierende dürfen keine Leistungen speichern");
		sl.setStudentLeistungGesiegelt(true);
		sl.setStudentLeistungValidiert(true);
	} else {
		sl.setStudentLeistungKlausuranmeldung(true);
                sl.setStudentLeistungGesiegelt(false);
		System.out.println("EIN COMMITMENT!!");
	}
        
        // Studierende geben auch eine ModulID an.
        // außerdem muss hier der Anmeldezeitraum
        // überprüft werden.
        // Damit auch vom Config-Konto aus im Prinzip 
        // die modul_id mitgeliefert werden kann (um 
        // die zukünftige Programmierung zu vereinfachen) 
        // wird sie in jedem Fall notiert...
        if(bIsCalledFromStudent || request.getParameter("modul_id")!=null){
            sl.setModulID(Long.parseLong(request.getParameter("modul_id")));
        }
        
	sl.add();
        
        // #HACK
        if(bIsCommitment){
            sl.sqlExe("update \"tblBdStudentXLeistung\" set \"intNoteID\"=null where " + 
                    "\"strMatrikelnummer\"='" + sl.getMatrikelnummer() + "' and " +
                    "\"lngLeistungsID\"=" + sl.getLeistungsID() + " and " +
                    "\"lngSdSeminarID\"=" + sl.getSdSeminarID() + " and " +
                    "\"lngStudentLeistungCount\"=" + sl.getStudentLeistungCount() + ";");
        }

	//System.out.println("added\n" + sl.toXMLString());

	// Verschiebung ins Zusatzmodul
	// if(request.getParameter("cboModulID")==null || request.getParameter("cboModulID").equals("null")) sl.setModulToNull();
%>{"success":"<%=(sErrorReport.equals("") ? "true" : "false")%>", "details":"<%=sErrorReport%>", "leistung":"<%=sl.getLeistungsID() %>", "leistung_count":"<%=sl.getStudentLeistungCount() %>"}