<%--
    JSP-Object "seminar/fach/add"
    @Revision 

    @TODO
    ===========================

    SYNOPSIS (German)
    ===========================
    2014, Mai 11, shj
    Erzeugt. 
    
    UN-üblicher Lifecycle: ADD
    
    Fügt die Rohfassung eines neuen Studiengangs hinzu 
    und bildet dafür ggf. auch neue Module
    
    Expected SESSION PROPERTIES
    ===========================
    user        muss initialisiert sein (AccessLevel >= 500),

    Expected PARAMETER(S):
    ===========================
    studiengang_name
    studiengang_version
    studiengang_beschreibung
    studentgang_cp
    studiengang_aufbau

    Returns
    =======


--%>
<%@page import="de.shj.UP.data.Pruefung"%>
<%@page import="de.shj.UP.data.ModulXLeistung"%>
<%@page import="de.shj.UP.data.PruefungXFach"%>
<%@page import="de.shj.UP.data.PruefungXModul"%>
<%@page import="de.shj.UP.data.Modul"%>
<%@page import="de.shj.UP.data.SeminarXFach"%>
<%@page import="de.shj.UP.data.Fach"%>
<%@page contentType="text/json" pageEncoding="UTF-8" import="java.sql.ResultSet,de.shj.UP.data.Dozent,de.shj.UP.data.shjCore" session="true" isThreadSafe="false"  errorPage="../../error.jsp" %>
<jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean id="seminar" scope="session" class="de.shj.UP.HTML.HtmlSeminar" /><jsp:useBean id="student" scope="session" class="de.shj.UP.beans.config.student.StudentBean" />
<%@include file="../../../fragments/checkVersion.jsp" %>
<%@include file="../../../fragments/conf_general.jsp" %>
<%@include file="../../../fragments/checkLogin.jsp" %>
<%@include file="../../../fragments/checkAccess500.jsp" %>
<%
    Fach fach=new Fach();
    fach.setFachID((int)user.getNextID("intFachID", "tblSdFach", ""));
    fach.setFachBezeichnung(request.getParameter("studiengang_name"));
    fach.setFachBeschreibung(request.getParameter("studiengang_beschreibung"));
    fach.setFachCreditPtsRequired(Float.parseFloat(request.getParameter("studiengang_cp")));
    fach.add();
    
    SeminarXFach sxf=new SeminarXFach();
    sxf.setFachID(fach.getFachID());
    sxf.setSeminarXFachBemerkung(request.getParameter("studiengang_version"));
    sxf.setSeminarID(seminar.getSeminarID());
    sxf.add();
    
    Pruefung p=new Pruefung();
    p.setSdSeminarID(seminar.getSeminarID());
    p.setPruefungBezeichnung("Abschlussprüfung " + fach.getFachBezeichnung());
    p.setPruefungZUVAmt("1125002");
    p.setPruefungZUVFach("Auto");
    p.setPruefungZUVTyp("001");
    p.setPruefungTriggered(false);
    p.setPruefungID(seminar.getNextID("lngPruefungID", "tblSdPruefung", "\"lngSdSeminarID\"=" + seminar.getSeminarID()));
    p.add();
    
    PruefungXFach pxf=new PruefungXFach();
    pxf.setSdSeminarID(seminar.getSeminarID());
    pxf.setBemerkung("Automatische Benennung der Prüfung aus 'Degree-Kit' am " + seminar.g_TODAY);
    pxf.setFachID(fach.getFachID());
    pxf.setPruefungFachAbschluss(true);
    pxf.setPruefungID(p.getPruefungID());
    pxf.add();
    
    Modul mod=null;
    System.out.println("Fach angelegt, ID: " + fach.getFachID());
    System.out.println("\nModularisierung:\n\n" + request.getParameter("studiengang_aufbau"));
    String lines[] = request.getParameter("studiengang_aufbau").split("\\r?\\n");
    for(int ii=0;ii<lines.length;ii++){
        if(lines[ii].trim().length()>=3){
            if(lines[ii].startsWith("- ")){
                System.out.println("...füge dem Modul Leistung " + lines[ii].substring("- ID=".length(), lines[ii].indexOf(",LP=")) + " hinzu\n");
                ModulXLeistung mxl = new ModulXLeistung();
                mxl.setSdSeminarID(seminar.getSeminarID());
                mxl.setLeistungID(Long.parseLong(lines[ii].substring("- ID=".length(), lines[ii].indexOf(",LP="))));
                mxl.setModulID(mod.getModulID());
                mxl.setMinCreditPts(Float.parseFloat(lines[ii].substring(lines[ii].indexOf(",LP=") + ",LP=".length())));
                mxl.add();
            }else{
                long lModulID=-1;
                if(lines[ii].startsWith("Name=")){
                    System.out.println("Lege Neues Modul mit Name " + lines[ii].substring("Name=".length(), lines[ii].indexOf(",LP=")) + " an...\n");
                    mod=new Modul();
                    mod.setSdSeminarID(seminar.getSeminarID());
                    mod.setModulID((int)user.getNextID("lngModulID", "tblSdModul", "\"lngSdSeminarID\"=" + seminar.getSeminarID()));
                    mod.setModulBezeichnung(lines[ii].substring("Name=".length(), lines[ii].indexOf(",LP=")));
                    mod.add();

                    lModulID=mod.getModulID();
                    System.out.println("... das neue Modul hat die ID: " + mod.getModulID());
                }else if(lines[ii].startsWith("ID=")){
                    System.out.println("Füge das vorhandene Modul mit ID " + lines[ii].substring("ID=".length()) + " hinzu\n");
                    lModulID=Long.parseLong(lines[ii].substring("ID=".length()));
                }

                PruefungXModul pxm = new PruefungXModul();
                System.out.println(lines[ii]);
                pxm.setSdSeminarID(seminar.getSeminarID());
                pxm.setPruefungID(p.getPruefungID());
                pxm.setPruefungLeistungGewichtung(1);
                pxm.setMinCreditPts(0);
                pxm.setModulID(lModulID);
                pxm.add();
            }
        }
    }
%>{"success":"true"}
