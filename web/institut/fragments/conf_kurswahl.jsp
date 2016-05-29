<%@ page language="java" pageEncoding="UTF8"%><%
    
    // Zu wie vielen Kurstypen
    // darf man sich gleichzeitig
    // anmelden? (Default: 4)
    long lMAX_AUSWAHL_KURSTYPEN=4;
    
    // Welche Kurstypen sind bei der Zählung 
    // der maximal wählbaren Kurstypen aus-
    // geschlossen? (Z.b. Vorlesungen, Tutorien und Fundamentals). CSV-Format.
    String sMAX_AUSWAHL_KURSTYPEN_AUSNAHMEN = "10150,10040,10050,10060,20010,25,32,38";
    
    // Sollen die Kurse bereits einer bereits 
    // abgehandelten Kurswahl ignoriert werden?
    // (So kann man z.B. bei einer vorgelagerten
    // Proseminarwahl 4 Kurstypen zulassen, und 
    // bei der späteren Kurswahl 4 weitere Kurse)
    boolean bIGNORE_ALREADY_ASSIGNED=false;
    %>
