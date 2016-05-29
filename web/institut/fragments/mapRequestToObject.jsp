<%@ page contentType="text/json" pageEncoding="UTF-8" import="java.text.SimpleDateFormat" %>
<%-- 
    Document   : mapRequestToObject
    Created on : 23.11.2012, 20:12:44
    Author     : heiko
--%><%!
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
	}%>
