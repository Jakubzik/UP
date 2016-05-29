<%@page import="de.shj.UP.beans.config.student.StudentBean"%>
<%@page import="java.sql.PreparedStatement"%>
<%@ page contentType="text/json" errorPage="../../error.jsp" session="true" import="java.util.Date, java.util.List,java.io.File,de.shj.UP.util.CommandRunner,java.util.Random,de.shj.UP.beans.student.ExamIterator,de.shj.UP.transcript.*,de.shj.UP.data.Pruefung,de.shj.UP.transcript.Fachnote,de.shj.UP.data.shjCore,de.shj.UP.HTML.HtmlDate,java.sql.ResultSet" %>
<jsp:useBean id="student" scope="session" class="de.shj.UP.beans.config.student.StudentBean" />
<jsp:useBean id="seminar" scope="session" class="de.shj.UP.HTML.HtmlSeminar" />
<%
    // Anglistik: Stichproben 5 GymPO, 3 BA 75, 3 BA 50 1.HF, 3 2.HF, 3*3 25%
    String[] sMatrikelnummer_as={"3239549","2888612","3230868","2893449","3077260","2897043","2605299","3131002","2895690","3068700","2773238","2857703","2911010","2907611","3040892","3049339","3208470","3068551","3039334","3149472","3133486","3033090","2909561","3040161","3080668"};
    
    // Germanistik
    String[] sMatrikelnummer_gs={"2810661","2909769","2921586","2886540","2886065","2854264","2711967","2455677","2910756","2891597","2891179","2850289","2889774","2898863","2910720","2857764","2551239"};
    
    String sSeminar_AS="Anglistik";
    long lSeminar_AS=1;
    for(int ii=0;ii<sMatrikelnummer_as.length; ii++){
        StudentBean studi=new StudentBean();
        studi.setMatrikelnummer(sMatrikelnummer_as[ii]);
        studi.setSeminarID(lSeminar_AS);
        Fachnote note = new Fachnote(studi.getMatrikelnummer(), studi.getSeminarID(), studi.getFachID(lSeminar_AS));
        out.write(sMatrikelnummer_as[ii] + "," + sSeminar_AS + ", Note: " + note.getGrade() + ", Trend:" + note.getPreliminaryGrade()  + "\n" + note.explain() + "\n\n\n======\n\n\n");
    }
    
    String sSeminar_GS="Germanistik";
    long lSeminar_GS=5;
    for(int ii=0;ii<sMatrikelnummer_gs.length; ii++){
        StudentBean studi=new StudentBean();
        studi.setMatrikelnummer(sMatrikelnummer_gs[ii]);
        studi.setSeminarID(lSeminar_GS);
        Fachnote note = new Fachnote(studi.getMatrikelnummer(), studi.getSeminarID(), studi.getFachID(lSeminar_GS));
        out.write(sMatrikelnummer_gs[ii] + "," + sSeminar_GS + ", Note: " + note.getGrade() + ", Trend:" + note.getPreliminaryGrade()  + "\n" + note.explain() + "\n\n\n======\n\n\n");
    }
    
%>