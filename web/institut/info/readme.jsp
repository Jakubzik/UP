<%-- 
    Document   : readme
    Created on : 18.11.2012, 19:45:15
    Author     : jakubzik

    Fehlerbehandlung
    =================
    Fehlerobjekt ist JSON,
    {
        error:      [String] Beschreibung des Fehlers zur Anzeige
        errorDebug: [String] Beschreibung auf Debug Level
        errorcode:  [int]    Nummer (sollte eindeutig sein für i18n)
        severity:   [int]
                        100: client app liefert Mist (Parameter nicht wie erwartet)
                         90: Sitzungsdaten uneindeutig: entweder client app Mist, oder seltsamer Zustand;
                             Retry and Inform Vendor if Persists
                         80: Berechtigung reicht nicht aus, Aktion nicht möglich.
    }

    Fehlercodes
    ===========
    (1) generell
    errorcode 1:    Sitzung ist abgelaufen [User]
    errorcode 11:   Sitzung ist abgelaufen [Student]
    errorcode 2:    Versionsparameter fehlt
    errorcode 3:    Frontend und Backend sind inkompatibel
    errorcode 4:    Berechtigung reicht nicht aus (ist unter 1)
    errorcode 5:    Berechtigung reicht nicht aus (ist unter 500)
    errorcode 6:    Parameter Matrikelnummer erwartet, aber nicht übergeben
    errorcode 7:    Parameter Matrikelnummer ist nicht numerisch
    errorcode 8:    Parameter Matrikelnummer entspricht nicht dem Sitzungsobjekt
    errorcode 9:    Parameter Mist
    errorcode 10:   Prüfung soll ausgestellt werden, aber es fehlen noch die Leistungen aus fehlende_leistungen

    (2) allgemein
    Logik: ItemID (see below) + actionID + subinfo

    Example:
    100.204 = Bemerkung (100.000) konnte nicht erneuert (200) werden, weil die Berechtigungn (2) nicht ausreichen.
    
    ItemID
    100.000     Bemerkung
    101.000     Leistung
    102.000     Student.Anmeldung 
    103.000     Kurs
    104.000     Prüfung
    105.000     Student [derzeit nur update]
    106.000     Antrag
    107.000     Student signUp (tblBdAnmeldung)
    108.000     Student signUp.tausch
    109.000     Transkriptdruck

    200.000     Seminar
    201.000     Dozent
    201.100     Dozent.Bemerkungen
    202.000     Fach
    203.000     Modul
    204.000     Note
    205.000     Kurstyp
    206.000     Kurs
    207.000     Seminar.Anmeldung
    208.000     Seminar.Dozent.Termin
    209.000     Fachnote
    210.000     Termin
    211.000     Seminar.signUp
    212.000     Seminar.signUp.Fehler
    213.000     Seminar.Leistung
    214.000     Seminar.Information
    215.000     Seminar.Kurs.Termin
    216.000     Seminar.Raum
    217.000     Seminar.Aktuelles
    218.000     Seminar.Lehrauftrag

    300.000     Visualisierung.Seminar

    actionID
    100         get
    200         update
    300         delete
    400         add

    subinfo
    1           fails on database
    2           access restrictions 
    3           request (parameters) mismatch
    4           unknown
    5           user input problematic
    6           configuration problem
    7           nicht implementiert

    severity
    100         progam problem
    ..
    ..
    50          program problem
    40          user input problem
    ..
    ..

    ===============================================
    Bsp. zum Lifecycle mit der js-lib und HTML
    1) Leistungen

    ===============================================
    Konfigurationshinweise:
    ===============================================
    PDF-Druck (Selbstdruck von Dokumenten).
    siehe auch: print/transcript-selfprint_relay.jsp
    Fkt. mit 
    * wkhtmltopdf-i386 (muss auf den Server kopiert werden, 
      siehe auch: 
    * und einem kleinen Shell-Script zum Aufruf, 
      namens shj-signup-printtranscript.sh
      (param1: URL, param2: Output pdf).
      Damit keine Pfadprobleme bei Updates 
      auftreten, wird das Skript unter /user/local/bin kopiert.

--%>