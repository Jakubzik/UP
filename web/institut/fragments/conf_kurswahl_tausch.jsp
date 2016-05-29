<%@ page language="java" pageEncoding="UTF8"%><%
    
    // Gibt an, ob der Tausch für Proseminare
    // aktiv ist oder nicht
    // Ist der Tausch aktiv, wird 
    // im Studierenden-Login unter 
    // Kurswahl das Register "Tausch" 
    // eingeblendet:
    boolean bTausch=false;
    
    // Welche Kurstypen dürfen getauscht werden
    // (KurstypIDs in CSV)
    
    // Proseminare Anglistik:
    String sKurstypenMitTauschoption = (bTausch ? ",14,15,16,10010,10020,10030,10070,10075,10080,10090,31,20300,20010," : "");
    
    // Tutorien, Phonetik, Grammar [Repeat]:
    // String sKurstypenMitTauschoption = (bTausch ? ",4,22,32,38,34,5,20010," : "");
    
    // Welche Kurse dürfen ggf. NICHT getauscht werden
    // (KursID in CSV) (Eller, Hertel)
    // Smith (wird abgesagt)
    String sKurseMitTauschverbot = (bTausch ? ",15609,15464," : "");
    %>
