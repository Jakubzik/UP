<%--
    JSP-Object "seminar/lehrauftrag/delete"
    @Erzeugt: Jul 6, 2014 -- hj

    @TODO
    ===========================
    Momentan stures Löschen eines einzelnen LAs  aus der Datenbank. Das 
    braucht aber ein Änderungsmanagement:
    
    Das Löschen eines Lehrauftrags muss ja nachvollziehbar 
    bleiben und in Änderungsdokumente einfließen können.

    Man könnte entweder beim Löschen anbieten, ein neues 
    Set anzulegen.

    Oder man müsste jeden Datensatz als "gelöscht" kennzeichnen 
    können.

    Auch wäre es u.U. wünschenswert, ganze Sets oder Semester löschen 
    zu können.

    SYNOPSIS (German)
    ===========================  
    Löscht den Lehrauftrag

    Expected SESSION PROPERTIES
    ===========================
    seminar     muss initialisiert sein
    user        muss initialisiert sein & Access Level >=500 haben

    Expected PARAMETER(S):
    ===========================
    semester    'Wintersemester 2015/2016', 'Sommersemester 2015' etc.      }
    dozent_id   <long>                                                      }   Identifizieren den zu ändernden
    set_nr      <long>      Set ist ein Satz bzw. eine Version der LA des   }   Datensatz
                            Semesters                                       }
    lfd_nr      <long>      Laufende Nr. in diesem Set                      }
    Returns
    =======
    {
        "success": "true",
        "information": {
            "id": "26",
        }
    }

    Sample Usage
    ============
--%><%@page import="java.sql.ResultSet"%>
<%@page import="de.shj.UP.data.shjCore"%><%@page import="java.util.Date"%><%@page import="java.sql.PreparedStatement"%><%@page contentType="text/json" pageEncoding="UTF-8" session="true" isThreadSafe="false"  errorPage="../../error.jsp" %>
<jsp:useBean id="user" scope="session" class="de.shj.UP.data.Dozent" /><jsp:useBean id="seminar" scope="session" class="de.shj.UP.HTML.HtmlSeminar" />
<%@include file="../../../fragments/checkVersion.jsp" %>
<%@include file="../../../fragments/checkAccess500.jsp" %>
<%long lERR_BASE=218000 + 300;    // Lehrauftrag + Delete
    // Init
    long lDozentID  = -1;
    long lLfdNr     = -1;
    int iLA_SetNr   = -1;
    
    // =========================================================================
    // Schnittstelle
    // =========================================================================
    if(request.getParameter("semester")==null || request.getParameter("semester").length()<19){
        throw new Exception("{\"error\":\"Der Lehrauftrag kann nicht gelöscht werden, "
                + "ohne dass ein Semester angegeben wird.\"" + 
                ",\"errorDebug\":\"Der Parameter >semester< muss z.B. lauten "
                + ">Wintersemester 2018/2019<, lautet aber >" + 
                request.getParameter("semester") + "<\",\"errorcode\":" + 
                lERR_BASE + 1 + ",\"severity\":10}"); 
    }
    
    try{
        lDozentID = Long.parseLong(request.getParameter("dozent_id"));
    }catch(Exception eNoParam){
        throw new Exception("{\"error\":\"Der Lehrauftrag kann nicht gelöscht "
                + "werden, ohne dass ein(e) Lehrende(r) angegeben ist.\"" + 
                    ",\"errorDebug\":\"Der Parameter >dozent_id< muss numerisch sein, "
                + "lautet aber >" + request.getParameter("dozent_id") + 
                "<\",\"errorcode\":" + lERR_BASE + 1 + ",\"severity\":10}"); 
    }
    
    try{
        lLfdNr = Integer.parseInt(request.getParameter("lfd_nr"));
    }catch(Exception eNoParam){
        throw new Exception("{\"error\":\"Der Lehrauftrag kann nicht gelöscht "
                + "werden, ohne dass seine lfd. Nr. angegeben wird.\"" + 
                    ",\"errorDebug\":\"Der Parameter >lfd_nr< muss numerisch sein, "
                + "lautet aber >" + request.getParameter("lfd_nr") + 
                "<\",\"errorcode\":" + lERR_BASE + 1 + ",\"severity\":10}"); 
    }
    
    try{
        iLA_SetNr = Integer.parseInt(request.getParameter("set_nr"));
    }catch(Exception eNoParam){
        throw new Exception("{\"error\":\"Der Lehrauftrag kann nicht gelöscht "
                + "werden, ohne dass seine Set Nr. angegeben wird.\"" + 
                    ",\"errorDebug\":\"Der Parameter >set_nr< muss numerisch sein, "
                + "lautet aber >" + request.getParameter("set_nr") + 
                "<\",\"errorcode\":" + lERR_BASE + 1 + ",\"severity\":10}"); 
    }
    
    
    PreparedStatement pstm= seminar.prepareStatement("delete from \"tblBdLehrauftrag\" " +
        "WHERE \"lngSdSeminarID\"=? and \"strLehrauftragSemester\"=? and " + 
        "\"lngDozentID\"=? and \"intLehrauftragSetNummer\"=? and \"intLehrauftragLfdNr\"=?;");
    
    int ii=1;

    
    pstm.setLong(ii++, seminar.getSeminarID());
    pstm.setString(ii++, request.getParameter("semester").trim());
    pstm.setLong(ii++, lDozentID);
    pstm.setInt(ii++, iLA_SetNr);
    pstm.setInt(ii++, (int)lLfdNr);
    
    int iRowsAffected=pstm.executeUpdate();

    if(iRowsAffected==0)
        throw new Exception("{\"error\":\"Der Lehrauftrag konnte nicht gelöscht werden, sorry..\"" + 
                    ",\"errorDebug\":\"Update hat keine Zeile in der Datenbank verändert\",\"errorcode\":" + lERR_BASE + 1 + ",\"severity\":10}"); 

    if(iRowsAffected>1)
        throw new Exception("{\"error\":\"Ein schwerwiegendes Problem ist aufgetreten -- bitte informieren Sie den Systemadministrator.\"" + 
                    ",\"errorDebug\":\"Das Löschen des Lehrauftrags hat mehr als eine Zeile verändert\",\"errorcode\":" + lERR_BASE + 1 + ",\"severity\":10}"); 

%>{"success":"true"}