<%@ page contentType="text/json" pageEncoding="UTF-8" import="java.util.Enumeration,java.text.SimpleDateFormat, de.shj.UP.data.Dozent,de.shj.UP.data.shjCore" session="true" isThreadSafe="false" errorPage="../error.jsp"%>
<%@page import="java.util.Locale"%>
<%@page import="de.shj.UP.data.StudentXLeistung"%><jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean id="seminar" scope="session" class="de.shj.UP.HTML.HtmlSeminar" />
<jsp:useBean id="student" scope="session" class="de.shj.UP.beans.config.student.StudentBean" />
<jsp:useBean id="credits" scope="page" class="de.shj.UP.beans.config.student.StudentLeistungBean" /><jsp:include page="../checkAccess.jsp" flush="true"></jsp:include>
<%!
String string2JSON(String s){
	return (s==null) ? "" : s.replace('\n', ' ').replace('\r', ' ').replace('\t', ' ').replaceAll("\"", "\\\\\""); 
}
String nn(Object s){
	return (s==null)?"":(((String)s).equals("null") ? "" : (String)s);
}
/**
 * Scans the request for all parameters whose 
 * name begins with "txt" or "cbo" or "chk".
 * 
 * If their name matches a setter-method of the 
 * object o, this method is called with the parameter-
 * value as argument.
 * 
 * The argument type is set to the type of 
 * the corresponding getter-method of the object.
 * 
 * A clear-text string with notes on the proceedings 
 * is returned for debugging purposes.
 * @param o
 * @param r
 * @return
 */
public String mapRequestToObject(Object o, HttpServletRequest r){

	// ------------------------------------------
	// Initialize some local vars.
	String sReturn = "";						// hand back a itinerary'n'error report
	int ii= o.getClass().getMethods().length;	// no. of methods for iteration
	String sMethodName = "";					// tmp var (e.g. setName)
	String sMethodNameCore = "";				// tmp var (e.g. Name)
	Class c_Type = null;						// casting helper for invocation of method 
	String sType = "";							// -- "" "" -- -- "" "" -- -- "" "" --
	String sHtmlPrefix="";						// txt or cbo
	
	// ------------------------------------------
	// Loop through all of o's methods:
	for (int ij=0; ij<ii; ij++){
		sMethodName = o.getClass().getMethods()[ij].getName();
		
		// we have a "set"-Method, e.g. setAddress 
		if(sMethodName.startsWith("set")){
			
			// extract relevant name, e.g. "Address"
			sMethodNameCore = sMethodName.substring(3);
			
			// see if we have a matching value in the request
			if((r.getParameter("txt" + sMethodNameCore) != null) || ((r.getParameter("cbo" + sMethodNameCore) != null))){

				// name: cbo? txt?
				sHtmlPrefix = (r.getParameter("txt" + sMethodNameCore)==null ? "cbo" : "txt");

				// fetch return type
				try{
					c_Type = o.getClass().getMethod("get" + sMethodNameCore, new Class[]{}).getReturnType();
				}catch(Exception e1){sReturn += "# ERR: method 'get" + sMethodNameCore + "()' not found";}
				
				sType = c_Type.getName();
				
				// make a note
				sReturn += "* setting '" + sMethodNameCore + "' to '" + r.getParameter(sHtmlPrefix + sMethodNameCore) + "' (Type: " + sType + ")\n\n";				
				
				// ------------------------------------------
				// invoke method with appropriate type
				if(sType.equals("long")){
					try{
						o.getClass().getMethod("set" + sMethodNameCore, new Class[] {Long.TYPE}).invoke(o, new Object[]{new Long(Long.parseLong(r.getParameter(sHtmlPrefix + sMethodNameCore)))});
					}catch(Exception e){sReturn +=" ... failed:" + e.toString() + "\n\n\n\n";}
				}

				if(sType.equals("double")){
					try{
						o.getClass().getMethod("set" + sMethodNameCore, new Class[] {Double.TYPE}).invoke(o, new Object[]{new Double(Double.parseDouble(r.getParameter(sHtmlPrefix + sMethodNameCore)))});
					}catch(Exception e){sReturn +=" ... failed:" + e.toString() + "\n\n\n\n";}
				}
				
				if(sType.equals("int")){
					try{
						o.getClass().getMethod("set" + sMethodNameCore, new Class[] {Integer.TYPE}).invoke(o, new Object[]{new Integer(Integer.parseInt(r.getParameter(sHtmlPrefix + sMethodNameCore)))});
					}catch(Exception e){sReturn +=" ... failed:" + e.toString() + "\n\n\n\n";}
				}
				
				if(sType.equals("float")){
					try{
						o.getClass().getMethod("set" + sMethodNameCore, new Class[] {Float.TYPE}).invoke(o, new Object[]{new Float(Float.parseFloat(r.getParameter(sHtmlPrefix + sMethodNameCore)))});
					}catch(Exception e){sReturn +=" ... failed:" + e.toString() + "\n\n\n\n";}
				}
				
				if(sType.equals("java.sql.Date")){
					try{
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						java.util.Date d = sdf.parse(r.getParameter(sHtmlPrefix + sMethodNameCore));
						o.getClass().getMethod("set" + sMethodNameCore, new Class[] {Class.forName("java.sql.Date")}).invoke(o, new Object[]{new java.sql.Date(d.getTime())});
					}catch(Exception e){
						try{
							SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
							java.util.Date d = sdf.parse(r.getParameter(sHtmlPrefix + sMethodNameCore));
							o.getClass().getMethod("set" + sMethodNameCore, new Class[] {Class.forName("java.sql.Date")}).invoke(o, new Object[]{new java.sql.Date(d.getTime())});
						}catch(Exception e2){
							sReturn +=" ... failed:" + e.toString() + "\nGerman format also failed:" + e2.toString() + "\n\n\n";
						}
					}
				}
				
				if(sType.equals("java.lang.String")){
					try{
						o.getClass().getMethod("set" + sMethodNameCore, new Class[] {Class.forName("java.lang.String")}).invoke(o, new Object[]{r.getParameter(sHtmlPrefix + sMethodNameCore)});
					}catch(Exception e){sReturn +=" ... failed:" + e.toString() + "\n\n\n\n";}
				}				
			}		
		}
	}
	return sReturn;
}
%>
<%
	StudentXLeistung sl = new StudentXLeistung(
			user.getSdSeminarID(), 
			student.getMatrikelnummer(),
			Long.parseLong(request.getParameter("idxLeistungIDOrig")),
			Long.parseLong(request.getParameter("idxLeistungCountOrig")));
	
	try{
		if(sl.getDozentID() != Long.parseLong(request.getParameter("txtDozentID")) && (Long.parseLong(request.getParameter("txtDozentID"))!=-1)){
			Dozent dozent = new Dozent(user.getSdSeminarID(), Long.parseLong(request.getParameter("txtDozentID")));
			sl.setStudentLeistungAussteller(dozent.getDozentNachname());
			sl.setStudentLeistungAusstellerVorname(dozent.getDozentVorname());
			sl.setStudentLeistungAusstellerTitel(dozent.getDozentTitel());
		}
	}catch(NullPointerException eNoDozentIDIgnore){
		// Kommt in folgender Konstellation vor:
		// Wird der Schein eines früheren Lehrenden auf-
		// gerufen, der nicht aktuell in der Kombo-Box 
		// aufgelistet ist, wird beim Update des Scheins 
		// keine ID übergeben (weil sie nämlich schlicht
		// nicht vorliegt). Das kann unschädlich
		// ignoriert werden.
	}
	
	// Es soll vermieden werden, dass der Dozent auf -1 gesetzt wird:
	if(Long.parseLong(request.getParameter("txtDozentID"))==-1){
		long lDozentTmp = sl.getDozentID();
		mapRequestToObject(sl, request);
		sl.setDozentID(lDozentTmp);
	}else{
		mapRequestToObject(sl, request);
	}

	// Verschiebung ins Zusatzmodul
	if(request.getParameter("cboModulID")==null || request.getParameter("cboModulID").equals("null")) sl.setModulToNull();
	
	// Ggf. die LeistungsID ändern
	if(Long.parseLong(request.getParameter("idxLeistungIDNew")) != sl.getLeistungsID()){
		if(request.getParameter("cboModulID")==null || request.getParameter("cboModulID").equals("null")) sl.updateLeistungIdTo(Long.parseLong(request.getParameter("idxLeistungIDNew")));
		else sl.updateLeistungIdTo(Long.parseLong(request.getParameter("idxLeistungIDNew")), Long.parseLong(request.getParameter("cboModulID")));
	}
	
	// Fehler abfangen:
	String sErrorReport="";
	if(sl.getNoteID()<=0){
		sErrorReport += "Es wurde keine Note ausgewählt.";
	}else{
	
		// Ist das eine Umwandlung einer Anmeldung?
		if(sl.getStudentLeistungKlausuranmeldung()){
			sl.setStudentLeistungKlausuranmeldung(false);
			sl.setStudentLeistungGesiegelt(true);
			sl.setStudentLeistungValidiert(true);
			sl.tellStudentAboutNewCredit();
		}
		if(!sl.update()) sErrorReport +="Speichern fehlgeschlagen (Grund unklar)";
	}	
		
	%>{"success":"<%=(sErrorReport.equals("") ? "true" : "false")%>", "details":"<%=sErrorReport %>", "leistung":"<%=sl.getLeistungsID() %>", "leistung_count":"<%=sl.getStudentLeistungCount() %>"}