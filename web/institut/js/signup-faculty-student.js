// TODO:
//
// Kontextsensitive 'Info'
// note when it changes
//
// Fehlanzeige, wenn nichts 
// gefunden wird
//
// Anzeige über Sitzungsende.
var search = "nothing";

/**
 * aktueller Studierender mit Fach, Modulen, Leistungen, Prüfungen
 * @type studi|Array
 */
var student = new Array();

$(document).ready(function() {

    // Ansichten der Register
    $.signUpGlobal = {
        'iVIEW_SEARCH': 0,
        'iVIEW_SEARCH_RESULT': 1,
        'iVIEW_STUDENT_DETAIL': 2,
        'iVIEW_STUDENT_LEISTUNGEN': 3,
        'iVIEW_STUDENT_ANMELDUNGEN': 4,
        'iVIEW_STUDENT_TRANSKRIPT': 5,
        'iVIEW_STUDENT_PRUEFUNGEN': 6,
        'iVIEW_STUDENT_BEMERKUNGEN': 7
    };

    // Das stellt die Ajax Wartemitteilung an.
    $('body').on({
        ajaxStart: function() {
            if(window.g_signUp_deactivate_ajax_loader){if (window.g_signUp_deactivate_ajax_loader===true) return;}
            $(this).addClass('loading').delay(1500).queue(function(next){
                if($(this).hasClass('loading')){
                    //alert('Ein Fehler ist aufgetreten -- sorry.\n\nWenn das Programm nicht normal weiter funktioniert, sollten Sie ' +
    //                            'auf "neu laden" klicken.');
                    $(this).removeClass('loading');
                    //window.location.reload(false);
                }
                next();
            });
        },
        ajaxStop: function() {
            $(this).removeClass('loading');
        }
    });

    $.signUpGlobal.user_name = $('#logged-in-user-name').val();
    $.signUpGlobal.user_vorname = $('#logged-in-user-vorname').val();
    $.signUpGlobal.seminar_id = $('#logged-in-user-seminar').val();
    $.signUpGlobal.access_level = parseInt($('#logged-in-user-accesslevel').val());
    $.signUpGlobal.info = {};
    $.signUpGlobal.printDocx = function(bIN){printDocx(bIN);};
    $.signUpGlobal.info.seminar = {};
    
    $('#modLeistungDetails').on('shown', function(){$('#leistung_name').focus();});
    $('#cmdShowFinalTranscripts').on('click', function(){$('#modFinalTranscripts').modal('show');});
    $('#modFinalTranscripts').on('show', function(){showFinalTranscripts();});
    
    $('#cmdPrintDocxGen').on('click', function(){
        $.signUpGlobal.info.TEMPLATE = $('#cmdPrintDocxGen').attr('data-template');
        printDocx(true);
    });
    $('#cmdPrintDocxGen_en').on('click', function(){
        $.signUpGlobal.info.TEMPLATE = $('#cmdPrintDocxGen_en').attr('data-template');
        printDocx(true);
    });
    
    /**
     * Per docxgen ausgegebene Word-Dokumente aus Daten
     * Testweise derzeit das Transkript
     * 
     * ACHTUNG!! magische Info in $.signUpGlobal.info.TEMPLATE.
     * 
     * @todo
     * - Anträge drucken
     * - Anträge im Loop drucken/Seriendruck
     * - Weitere Dokumente einbinden
     * 
     * - Funktionalität von 'bFirst' UND magische Info "TEMPLATE"
     *   gerade biegen
     *   (bFirst: die Fkt. ruft sich selbst per callback auf, 
     *            falls beim ersten Aufruf noch Informationen vom 
     *            Server geladen werden müssen.
     *            
     *            Um dabei nicht in eine ewige Schleife zu geraten,
     *            wird bFirst verwendet --- muss aber DRINGEND
     *            überarbeitet werden!
     * 
     * - Dokumentation, insbes. des Arrays für die 
     *   Übergabe
     * @param {type} bFirst
     * @returns {undefined}
     */
    function printDocx(bFirst){
        
        // Sind wir initialisiert?
        // (1) TRANSKRIPT
        if(!student.transkript){
            if(!bFirst){
                console.log('[ERR] signup-faculty-student.js.printDocx: nach Initialisierung immer noch kein Transkript.');
                alert('Fehler -- sorry. Kann das Transkript nicht ausgeben. Vielleicht hilft, erst "bearbeiten" zu klicken?');
                return;
            }
            
            console.log('[DEBUG]: ... muss student.transkript einräumen, rufe showTranscript mit printDocx als Callback.');
            showTranscript({"fSuccess":printDocx,"iCallbackLoopCount":0});
            console.log('[DEBUG]: ... exit nach Delegieren.');
            return;
        }

        // (2) Pruefungen 
        if(typeof student.pruefungen === 'undefined'){
            console.warn('[DEBUG]: ... muss erst prüfungen laden.');
            student.pruefungen = new shj.signUp.student.Pruefungen(student.matrikelnummer,
                printDocx);
            return;
         }
        
        // Achtung: das wird nicht als Parameter übergeben, weil 
        //          der Parameter schon die eternal loops zählt.
        var sTemplateURL = $.signUpGlobal.info.TEMPLATE;

        var loadFile = function (url,callback) {
          JSZipUtils.getBinaryContent(url,callback);
        };
        
        var getModulnote = function(id){
            if(!student.fachnote.fachnoten) return '#problem1';
            console.log('[DEBUG]: Getting Modulnote >' + id + '<, habe ' + student.fachnote.fachnoten[0].module.length + ' Module');
            for(var ii = 0,ij = student.fachnote.fachnoten[0].module.length; ii < ij; ii++){
                if(student.fachnote.fachnoten[0].module[ii].id == id)
                    return student.fachnote.fachnoten[0].module[ii].note.replace(".", ",");
            }
            return '#problem2';
        };
        
        /**
         * Das Array fürs Template zusammenstellen
         */
        var arrDOCXData = {};
        arrDOCXData.module = [];
        var fSummeGeforderteLP = 0;
        var fSummeErreichteLP = 0;
        
        console.log('[DEBUG]: ... Rekodiere die >' + student.transkript.modul.length + '< Module in Array für Template.');
        
        for (var ii = 0; ii < student.transkript.modul.length; ii++) {
            console.log('[DEBUG]: ... --- Modul ' + student.transkript.modul[ii].modul.name);
            var oMod = {};
            oMod.min_lp = student.transkript.modul[ii].modul.min_ects;   // Geforderte Leistungspunkte dieses Moduls 
            if(!isNaN(oMod.min_lp)) fSummeGeforderteLP += parseFloat(oMod.min_lp);
            oMod.name   = student.transkript.modul[ii].modul.name;
            oMod.id   = student.transkript.modul[ii].modul.id;
            oMod.name_en= student.transkript.modul[ii].modul.name_en;
            oMod.leer = !(student.transkript.modul[ii].modul.hatLeistung);
            oMod.leistungen = [];
            
            var dLPErreicht = 0;
            for (var jj = 0; jj < student.transkript.modul[ii].modul.leistungen.length; jj++) {
                console.log('[DEBUG]: ... --- ::: Leistung ' + student.transkript.modul[ii].modul.leistungen[jj].name);
                var oL = {};
                oL.lp = student.transkript.modul[ii].modul.leistungen[jj].ects;
                oL.name = student.transkript.modul[ii].modul.leistungen[jj].name;
                oL.name_en = student.transkript.modul[ii].modul.leistungen[jj].name_en;
                if(student.transkript.modul[ii].modul.leistungen[jj].kurstitel && 
                        student.transkript.modul[ii].modul.leistungen[jj].kurstitel.length>2) 
                    oL.kurstitel = '»' + student.transkript.modul[ii].modul.leistungen[jj].kurstitel + '«';
                else
                    oL.kurstitel = '';
                if(student.transkript.modul[ii].modul.leistungen[jj].kurstitel && 
                        student.transkript.modul[ii].modul.leistungen[jj].kurstitel_en.length>2) 
                    oL.kurstitel_en = '»' + student.transkript.modul[ii].modul.leistungen[jj].kurstitel_en + '«';
                else
                    oL.kurstitel_en = '';
                oL.datum = student.transkript.modul[ii].modul.leistungen[jj].datum;
                oL.datum_en = shj.signUp.toolbox.getInternationalDate( student.transkript.modul[ii].modul.leistungen[jj].datum );
                if(typeof student.transkript.modul[ii].modul.leistungen[jj].note!=='undefined'){
                    oL.note_en = student.transkript.modul[ii].modul.leistungen[jj].note;
                    oL.note = oL.note_en.replace(".", ",");
                }
                
                if(oL.note && oL.note.indexOf('Unbenotet (be')>=0){
                    if(oL.note.indexOf('*')>0){
                        oL.note = 'ub*';
                        oL.note_en = 'pass*';
                    }
                    else{
                        oL.note = 'ub';
                        oL.note_en = 'pass';
                    }
                }
                if(oL.note && oL.note.indexOf('Anerkannt (be')>=0){
                    oL.note = 'ub*';
                    oL.note_en = 'pass*';
                }
                
                // Nur wenn die Leistung auch absolviert 
                // wurde, der Auflistung für den Druck 
                // hinzufügen.
                if(!isNaN(oL.lp)){
                    oMod.leistungen.push(oL);
                    dLPErreicht += parseFloat(oL.lp);
                }
            }
            // Fehlen noch LP? Ist das Modul erfüllt? Gibt es
            // zu viele LP?
            oMod.lp = dLPErreicht;
            
            if (parseFloat(oMod.min_lp)==0 || parseFloat(oMod.min_lp) > parseFloat(dLPErreicht)) {
                if(parseFloat(oMod.min_lp)==0) oMod.modulnote = 'n/a';
                else{
                    oMod.modulnote_info = 'Vorläufige Modulnote (' + 
                            Math.round(parseFloat(dLPErreicht) / parseInt(oMod.min_lp) * 100) + 
                            '% abgeschlossen, ' + (oMod.min_lp - dLPErreicht) + 
                            ' LP fehlen noch)';
                    oMod.modulnote_info_en = 'Preliminary Module Grade (' + 
                            Math.round(parseFloat(dLPErreicht) / parseInt(oMod.min_lp) * 100) + 
                            '% completed, ' + (oMod.min_lp - dLPErreicht) + 
                            ' credit points short)';
                    oMod.modulnote = getModulnote(student.transkript.modul[ii].modul.id);
                    oMod.modulnote_en = oMod.modulnote.replace(',','.');
                }
            } else if (parseFloat(oMod.min_lp) < parseFloat(dLPErreicht)) {
                oMod.modulnote_info = 'Problem: ' + (oMod.min_lp - dLPErreicht) * (-1) + ' LP zu viel verbucht, bitte Studienberatung kontaktieren.';
                oMod.modulnote_info_en = 'Problem: ' + (oMod.min_lp - dLPErreicht) * (-1) + ' credit points too many, please contact advisor.';
                oMod.modulnote =  '!';
                oMod.modulnote_en =  '!';
            } else {
                oMod.modulnote_info = 'Modulnote';
                oMod.modulnote_info_en = 'Module Grade';
                console.log('[DEBUG]: ... --- ::: Hole Modulnote zu >' + student.transkript.modul[ii].modul.id + '<');
                console.log('[DEBUG]: ... --- ::: Hole Modulnote zu -->' + getModulnote(student.transkript.modul[ii].modul.id) + '<');
                oMod.modulnote = getModulnote(student.transkript.modul[ii].modul.id);
                oMod.modulnote_en = oMod.modulnote.replace(',','.');
            }
            
            // Latinum/Fremdsprachenkenntnisse
            if(typeof oMod.modulnote_info === 'undefined'){
                oMod.modulnote_info = 'Modul unbenotet';
                oMod.modulnote_info_en = 'not graded';
                oMod.modulnote = '--';
                oMod.modulnote_en = '--';
            }
            fSummeErreichteLP += oMod.lp;
            // Ist das Modul leer?
            if( !oMod.leer )
                arrDOCXData.module.push(oMod);
            
        }
        
        arrDOCXData.nachname = student.nachname;
        arrDOCXData.vorname = student.vorname;
        arrDOCXData.geburtsort = student.geburtsort;
        arrDOCXData.geburtstag = student.geburtstag;
        arrDOCXData.geburtstag_en = shj.signUp.toolbox.getInternationalDate(student.geburtstag);
        arrDOCXData.fachname = student.fachname;
        arrDOCXData.fachname_en = 'PROGRAM ME: ' + student.fachname;
        if(!student.fachnote.fachnoten){
            arrDOCXData.fachnote = '#problem';
            arrDOCXData.fachnote_lang = 'keine Angabe möglich';
            arrDOCXData.fachnote_lang_en = 'calculation not possible';
            arrDOCXData.fachnote_info = 'Vorläufige Fachnote';
            arrDOCXData.fachnote_info_en = 'Preliminary Overall Grade';
        }
        else{
            arrDOCXData.fachnote = student.fachnote.fachnoten[0].notenwert;
            if(student.fachnote.fachnoten[0].komplett==true){
                arrDOCXData.fachnote_lang = student.fachnote.fachnoten[0].bezeichnung + ' (' + student.fachnote.fachnoten[0].notenwert + ')';
                arrDOCXData.fachnote_info = 'Fachnote';
                
                arrDOCXData.fachnote_lang_en = arrDOCXData.fachnote_lang.replace(',','.');
                arrDOCXData.fachnote_info_en = 'Overall Grade';
            }else{
                arrDOCXData.fachnote_lang = student.fachnote.fachnoten[0].notenwert;
                arrDOCXData.fachnote_lang_en = arrDOCXData.fachnote_lang.replace(',','.');
                arrDOCXData.fachnote_info = 'Vorläufige Fachnote';
                arrDOCXData.fachnote_info_en = 'Preliminary Overall Grade';
            }
        }
        arrDOCXData.anrede = student.anrede;
        arrDOCXData.anrede_en = (student.anrede == 'Herr' ? 'Mr.' : 'Ms.');
        arrDOCXData.matrikelnummer = student.matrikelnummer;
        arrDOCXData.fachsemester = student.fachsemester.trim();
        
        // Bilanz der LP
        arrDOCXData.fach_lp_gesamt = fSummeGeforderteLP;
        arrDOCXData.fach_lp_erreicht = fSummeErreichteLP;
        arrDOCXData.datum = 'ww';//new Date();
        
        // Sortiere nach ModulID
        arrDOCXData.module.sort(function(a,b) {return (a.id > b.id) ? 1 : -1;} ); 
 
        // Prüfungen:
        arrDOCXData.pruefungen = student.pruefungen.pruefungen;
        for( var ii=0, ij=arrDOCXData.pruefungen.length; ii<ij; ii++){
            arrDOCXData.pruefungen[ii].datum = shj.signUp.toolbox.getGermanDate(arrDOCXData.pruefungen[ii].datum);
            if(arrDOCXData.pruefungen[ii].note.trim() == '0') arrDOCXData.pruefungen[ii].note = 'ub';
        }
 
        loadFile("./templates/" + sTemplateURL + "?v=" + Date.now(),function(err,content){    // v=time soll Cache löschen
          if (err) { console.log('[ERROR] .loadFile Template!'); console.log(err);};
          doc = new Docxgen(content);
          doc.setData(arrDOCXData);
          doc.render();
          out = doc.getZip().generate({type:"blob"});
          saveAs(out,"Transkript" + student.matrikelnummer + ".docx");
        });
    };
    
    /**
     * Lade die Studiengänge des Seminars mit 
     * den Modularisierungen vom Server
     * 
     * Die Select-Box #cboFach wird mit den 
     * Fächern des Seminars gefüllt.
     * @see json/seminar/fach/get.jsp
     * @see shj.signUp.seminar.Faecher
     */ 
    $.signUpGlobal.info.seminar.faecher = new shj.signUp.seminar.Faecher(
            $.signUpGlobal.seminar_id,
            function(data) {
                shj.signUp.toolbox.populateCbo(data.faecher,
                        {'identifyer': 'id', 'text': 'name', 'cboIdentifyer': '#cboFach'});
                $('#cboFach option[value="' + student.fach_id + '"]').attr('selected', true);

    });

    /**
     * Lade Noten dieses Seminars vom Server herunter 
     * und befülle die Select-Box #leistung_noten_id 
     * (in der Detailansicht der Leistung bzw. Anmeldung).
     * @see shj.signUp.seminar.Noten
     * @see json/seminar/note/get.jsp
     */
    $.signUpGlobal.info.seminar.noten = new shj.signUp.seminar.Noten(
            $.signUpGlobal.seminar_id, function(data) {
        // Damit Notenwert und Name 
        // angezeigt werden, Array umkopieren:
        var tmp = [];
        for (var ii = 0; ii < data.noten.length; ii++) {
            tmp[ii] = {};
            tmp[ii].id = data.noten[ii].id;
            tmp[ii].name = data.noten[ii].wert + ' -- ' + data.noten[ii].name;
        }
        shj.signUp.toolbox.populateCbo(tmp,
                {'identifyer': 'id', 'text': 'name', 'cboIdentifyer': '#leistung_noten_id'});
    });

    /**
     * Lade die Lehrenden herunter und 
     * initialisiere das (bootstrap) Typeahead
     * (in der Ansicht zum Edieren der Leistung).
     * @see shj.signUp.seminar.Dozenten
     * @see json/seminar/dozent/get.jsp
     */
    $.signUpGlobal.info.seminar.dozenten = new shj.signUp.seminar.Dozenten(
            $.signUpGlobal.seminar_id,
            function(data) {
                var ausstellerArray = new Array();
                for (var ii = 0; ii < data.dozenten.length; ii++) {
                    ausstellerArray.push(data.dozenten[ii].vorname + ' ' + data.dozenten[ii].name);
                }
                $('#leistung_aussteller').typeahead({'source': ausstellerArray, 'items': 6});
            }
    );

    // Initialisiere Logout-Dropdown  
    // in der Navi oben rechts
    $('.dropdown-toggle').dropdown();
    $('#cmdPruefungAdd').on('click', addPruefung);

    // Register-Klicks auf 'Leistungen', Anmeldungen
    $('#studentLeistungListe').on('click', showLeistungen);
    $('#studentAnmeldungListe').on('click', showAnmeldungen);
    $('#cmdShowPruefungen').on('click', showPruefungen);

    // Lifecycle von Bemerkungen
    $('#modBemerkungErfassen').on('shown', function() {
        $('#bemerkung_bemerkung').focus();
    });

    // Cursor ins Suchfeld:
    $('#txtSuche').focus();

    // Validiere Leistungsformular
    $('#fLeistungDetails input').on('blur', function() {
        setLeistungAktionen(leistung);
    });
    $('#fLeistungDetails select').on('blur', function() {
        setLeistungAktionen(leistung);
    });

    // Detailansicht Leistung: falls eine 
    // Hausarbeit angefertigt wurde, kann 
    // man auch ein Thema eingeben
    $('#leistung_leistungstyp').on('change', function() {
        controlVisibilityHA();
    });

    // Formulare werden grundsätzlich gesperrt
    $('form').submit(function(event) {
        event.preventDefault();
    });
    $('#fKurssuche').submit(function(event) {
        event.preventDefault();
        showKurssucheErgebnis();
    });
    $('#fFindStudents').submit(function(event) {
        event.preventDefault();
        findStudents();
    });
    $('#cmdWechsel').on('click', function(event) {
        event.preventDefault();
        updateFach();
    });
    $('#fPruefungAdd').submit(function(event) {
        event.preventDefault();
    });
    $('#fRenewLogin').submit(function(event) {
        event.preventDefault();
        renewLoginCreateServerSession();
    });

    /**
     * Eine TimeOut Funktion für das Suchfeld:
     * erfolgt kein neuer Tasten-
     * anschlag, wird die Suche 
     * ausgelöst. 
     */
    idleTimer = null;
    idleState = false;
    idleWait = 1500;
    $('#txtSuche').on('keydown', function() {
        clearTimeout(idleTimer);
        idleState = false;
        idleTimer = setTimeout(function() {
            // Idle Event
            if ($('#txtSuche').val().length >= 3) {
                if (search != $('#txtSuche').val()) {
                    search = $('#txtSuche').val();
                    //findStudents();
                    $('#fFindStudents').submit();
                }
            }
            idleState = true;
        }, idleWait);
    });

    // Die Register sind mit Links- und Rechtstasten
    // navigierbar
    $('body').keydown(function(event) {
        if (event.target.type !== 'text') {
            // Rechtspfeil
            if (event.which === 39) {
                // Falls Details sichtbar, 
                // Leistungsliste anzeigen
                if ($('#studentDetails').hasClass('active'))
                    showLeistungen();
                else if ($('#studentLeistungen').hasClass('active'))
                    showAnmeldungen();
                else if ($('#studentAnmeldungen').hasClass('active'))
                    showTranscript();
                else if ($('#studentTranskript').hasClass('active'))
                    showPruefungen();
                // Linkspfeil
            } else if (event.which === 37) {
                if ($('#studentLeistungen').hasClass('active'))
                    showDetails('same');
                else if ($('#studentAnmeldungen').hasClass('active'))
                    showLeistungen();
                else if ($('#studentTranskript').hasClass('active'))
                    showAnmeldungen();
                else if ($('#studentPruefungen').hasClass('active'))
                    showTranscript();
            }
        }
    });
});

/**
 * Das Kontextmenü der Transkriptansicht 
 * zum Verschieben von Leistungen in andere 
 * Menüs.
 */
$.contextMenu.defaults({
    // Beim Anzeigen des Moduls werden dann
    // alle Module gelöscht, denen die 
    // gewählte Leistung nicht zugeordnet ist:
    onShowMenu: function(e, obj) {

        // Id der Leistung, auf die (rechts) geklickt wird
        var sRecordID = $(e.target).parents('tr').data("shj_item").id;

        // Welchem Modul ist die Leistung derzeit
        // zugeordnet (dahin kann die Leistung 
        // ja nicht verschoben werden)
        var sModulCurrentID = parseInt($(e.target).parents('tr').data("shj_item").modul).toString();

        // Durchlaufe alle Module inkl Zusatz:
        $('#transkriptContextMenu li').each(function(index) {

            // Id des Schleifen-Moduls
            var sModulID = $(this).attr('id').substring(8);

            // Alle 'unpassenden' Module werden
            // aussortiert -- per .remove()
            if (sModulID.length > 0 && sModulID != 'usatz') {
                if ((sModulCurrentID != sModulID) && (modulContainsLeistung(sModulID, sRecordID))) {
                } else {
                    $('#cctxmod-' + sModulID, obj).remove();
                }
            }

            // Ist die Leistung derzeit im 'Zusatzmodul',
            // kann sie dahin nicht mehr verschoben werden:
            if (sModulCurrentID.indexOf('999999') > 0)
                $('#zusatz', obj).remove();

        });
        return obj;
    }
});


// ==========================================================
// ==========================================================
// Thema Renew Login
// ==========================================================
// ==========================================================
/**
 * @todo
 * @returns {undefined}
 */
function renewLoginAskPassword() {
    $('.modal').modal('hide');
    $('#modReLogin').modal('show');
}

/**
 * @todo
 * @returns {undefined}
 */
function renewLoginCreateServerSession() {
    var sending = new Object();
    sending.user_name = $.signUpGlobal.user_name;
    sending.user_pwd = $('#txtPwd').val();
    sending.seminar_id = $.signUpGlobal.seminar_id;
    sending.matrikelnummer = student.matrikelnummer;
    $.ajax({
        data: sending,
        url: 'json/renewLogin.jsp',
        success: function(data) {
            if (data.success == 'true')
                $('#modReLogin').modal('hide');
            else
                window.location.href = 'logout.jsp';
        },
        error: function(data) {
            window.location.href = 'logout.jsp';
        }
    });
}


// ==========================================================
// ==========================================================
// Thema Prüfungen
// ==========================================================
// ==========================================================	  
/**
 * Erklärung der Berechnung der Prüfungsnote
 * @param {String} info Html-Tabelle mit Anzeige der Notenberechnung
 * @returns {undefined} nichts
 */
// Zeigt die Notenberechnung der Prüfung
function showPruefungCalculations(info) {
    $('#modPruefungNotenberechnung .modal-body').html(info);
    $('#modPruefungNotenberechnung').modal('show');
}

/**
 * Zeigt den Auswahldialog für eine neue 
 * Prüfung (lädt ggf. die Prüfungen herunter). 
 * @returns {undefined}
 */
function showPruefungOptions() {
    // reset view:
    $('#missingCreditList').hide();
    $('#cmdPruefungAdd').text('Ausstellen');
    $('#lblPruefungAddWarning').hide();
    $('#cmdPruefungAdd')
            .addClass('btn-success')
            .removeClass('btn-danger')
            .unbind()
            .on('click', addPruefung);
    $('#cgPruefungAdd').removeClass('error');

    $('#flagIgnoreMissingCredits').val(false);

    if (student.konfigurierte_pruefungen == null || typeof student.konfigurierte_pruefungen == 'undefined') {
        // Lade Prüfungen dieses Studiengangs vom Server
        student.konfigurierte_pruefungen = new shj.signUp.student.KonfiguriertePruefungen(student.matrikelnummer,
                function(item) {
                    // Stelle die Prüfungen dar
                    _renderKonfiguriertePruefungen(item.pruefungen);
                    $('#modPruefungAddOptions').modal('show');
                });
    } else {
        _renderKonfiguriertePruefungen(student.konfigurierte_pruefungen.pruefungen);
        $('#modPruefungAddOptions').modal('show');
    }
}

/**
 * Füll die Select-Box #cboPruefungAdd 
 * mit den konfigurierten Prüfungen
 * @param {Array} array
 * @returns {undefined}
 */
function _renderKonfiguriertePruefungen(array) {
    // reset
    $('#cboPruefungAdd').html('');

    // Iteriere Prüfungen
    $.each(array, function(key, val) {
        $('<option/>').val(val.id).text(val.bezeichnung).appendTo('#cboPruefungAdd');
    });
}

/**
 * Zeigt fehlende Leistungen an, 
 * wenn es welche gibt.
 * Oder speichert die Prüfung:
 * - trotz fehldender Leistungen, falls
 * 'bSaveAsIs==true',
 * - oder falls alle Leistungen vorhanden 
 * sind.
 * @param {Boolean} bSaveAsIs
 * @returns {Boolean} immer true
 */
function addPruefung(bSaveAsIs) {
    if (student.pruefungen == null || typeof student.pruefungen == 'undefined') {
        // Die Prüfungen sind noch nicht geladen, erledige das also jetzt
        showPruefungen(function() {
            addPruefung();
        });
        return true;
    }

    var pruefung_tmp = new Object();

    pruefung_tmp.pruefung_id = $('#cboPruefungAdd').val();
    pruefung_tmp.bezeichnung = $('#cboPruefungAdd :selected').text();
    pruefung_tmp.matrikelnummer = student.matrikelnummer;

    if (bSaveAsIs == true) {
        // Es fehlen noch Leistungen, die Prüfung soll
        // aber trotzdem gespeichert werden.
        // wurde eine Note angegeben?
        if ($('#txtNote').val() == '') {
            $('#txtNote').focus();
            alert('Da noch Leistungen fehlen, kann die Note nicht automatisch' +
                    ' berechnet werden.\n\nBitte geben Sie eine Note ein!');
        }
        pruefung_tmp.saveAsIs = 'true';
        pruefung_tmp.note_manuell = $('#txtNote').val();
    } else {
        pruefung_tmp.saveAsIs = 'false';
    }

    $('#cmdPruefungAdd').button('loading');

    // ------------------------------------------------------
    // Lade Leistung auf Server und 
    // aktualisiere Darstellung
    student.pruefungen.add(student.matrikelnummer,
            pruefung_tmp,
            function(data) {
                _renderPruefungenTabelle(student.pruefungen);
                $('#cmdPruefungAdd').button('reset');
                $('#modPruefungAddOptions').modal('hide');
            },
            function(err_info) {
                $('#cmdPruefungAdd').button('reset');
                if (typeof err_info == 'undefined' || typeof err_info.bereinigungs_info.fehlende_leistungen == 'undefined') {
                    // Unvorhergesehener Fehler, darum kümmert
                    // sich eine allgemeinere Funktion
                    return false;
                }
                // Es gibt fehlende Leistungen:
                $('#missingCreditList').show();
                $('#cmdPruefungAdd').text('Trotzdem speichern');
                $('#lblPruefungAddWarning').show();
                $('#cmdPruefungAdd')
                        .removeClass('btn-success')
                        .addClass('btn-danger')
                        .unbind()
                        .on('click', function() {
                    addPruefung(true);
                });
                $('#cgPruefungAdd').addClass('error');
                $('#txtNote').val('anerkannt')

                shj.signUp.toolbox.render(err_info.bereinigungs_info.fehlende_leistungen,
                        {'item': err_info.bereinigungs_info.fehlende_leistungen,
                            'emptyFirst': true,
                            'HTML_template': '#template_fehlendeLeistungen',
                            'HTML_table_container': '#missingCreditList',
                            'HTML_template_class_prefix': 'fehlendeLeistung_'});

                return true;
            }
    );
}

/**
 * Navigiert zu RTF-Version der Prüfung
 * @param {object} oPruefung
 * @returns {undefined} nichts
 */
function printPruefung(oPruefung) {
    window.location = 'print/PruefungGeneric.rtf?lPruefungID=' + oPruefung.id + '&lPruefungCount=' + oPruefung.count + '&matrikelnummer=' + student.matrikelnummer;
}

/**
 * Prüfung wird gelöscht, und es wird eine 
 * Notiz über die gelöschte Prüfung angelegt.
 * @param {type} oPruefung
 * @returns {undefined} nichts
 * @todo Fehlerfall Serverkontakt durchprogrammieren
 */
function deletePruefung(oPruefung) {
    shj.signUp.toolbox.confirmDelete(
            '#modShj_SignUp_ConfirmDelete',
            'Prüfung löschen?',
            'Soll diese Prüfung<br /><br /><b>' +
            oPruefung.bezeichnung + '</b> (' + oPruefung.datum + ')' +
            '<br /><br />wirklich unwiderruflich gelöscht werden?',
            function() {
                student.pruefungen.drop(
                        oPruefung,
                        function() {
                            _renderPruefungenTabelle(student.pruefungen);
                            student.bemerkungen.add(student.matrikelnummer, {"name":"bemerkung","bemerkung":'Prüfung gelöscht (' +
                                    oPruefung.bezeichnung + ' (' + oPruefung.datum + '), ' +
                                    oPruefung.note, "matrikelnummer":student.matrikelnummer},  function() {
                            shj.signUp.toolbox.render(student.bemerkungen.bemerkungen);
                                    });
                        });
            },
            function() {
                //@todo
            }
    );
}

// ==========================================================
// ==========================================================
// Thema Fachwechsel
// ==========================================================
// ==========================================================
/**
 * Zeigt Auswahl der Studiengänge/Fächer
 * @returns {undefined} nichts
 */
function showStudiengangwechsel() {
    $('#modStudiengangswechsel').modal('show');
    $('#cboFach').val(student.fach_id);
};

/**
 * Ändert das Fach/den Studiengang des aktuellen
 * Studierenden auf den Wert der Auswahlbox #cboFach.
 * 
 * Hinterlässt eine entsprechende Bemerkung
 * @todo programmiere automatische Neuzuordnung der Leistungen zu Modulen
 * @returns {unresolved}
 */
function updateFach() {
    var dummy = {};
    dummy.fach_id = $('#cboFach').val();
    dummy.fach = $('#cboFach :selected').text();
    var fach_alt = $('.template_studentDetails_fachname').text();
    shj.signUp.student.update(
            student.matrikelnummer,
            dummy,
            function() {
                // Bei erfolgreichem Fachwechsel:
                // Bemerkung über den Vorgang speichern
                student.bemerkungen.add(student.matrikelnummer,
                {"name":"bemerkung","bemerkung":'Fach geändert von >' + fach_alt + '< zu >' + dummy.fach + '<',"matrikelnummer":student.matrikelnummer},
                        function(data) {
                            shj.signUp.toolbox.render(student.bemerkungen.bemerkungen);
                        }
                );
                // Fach Upgrade:
                student.fach_id = dummy.fach_id;
                //student.fach = getFach(dummy.fach_id);  //dummy.fach;
                student.fach=$.signUpGlobal.info.seminar.faecher.get(dummy.fach_id);
                student.fachname = dummy.fach;  //dummy.fach;
                $('.template_studentDetails_fachname').text(student.fachname);
                $('.tpl_studentWell_fachname').text(student.fachname);
                student.leistungen=null;
                student.anmeldungen=null;
                student.transkript=null;
                student.transkriptbindings=null;
                $('#cmdWechsel').button('reset');
                $('#modStudiengangswechsel').modal('hide');
            },
            function() {
                alert('Sorry -- Fachwechsel ist fehlgeschlagen');
                $('#cmdWechsel').button('reset');
                $('#modStudiengangswechsel').modal('hide');
            });
    return;
}

// ==========================================================
// ==========================================================
// Thema FINAL Transkripts (endgültige)
// ==========================================================
// ==========================================================
function addFinalTranscript(bEnglisch, bGPA_JSON){
    if(!bGPA_JSON){
        if(!bEnglisch)
            student.finalTranscripts.add($.signUpGlobal.seminar_id, {"permanent":"true", "matrikelnummer":student.matrikelnummer}, function(){_renderFinalTranscripts();});
        else
            student.finalTranscripts.add($.signUpGlobal.seminar_id, {"englisch":"true","permanent":"true", "matrikelnummer":student.matrikelnummer},function(){_renderFinalTranscripts();}); 
    }else{
        student.finalTranscripts.add($.signUpGlobal.seminar_id, 
        {"gpa_json":"true","permanent":"true", "matrikelnummer":student.matrikelnummer},
        function(){
            // Da das Dokument nicht "transkript" heißen soll,
            // müssen die permanenten Transkripte neu geladen werden:
            student.finalTranscripts=null;
            showFinalTranscripts();
        }); 
    }
}

function dropFinalTranscript(index){
    shj.signUp.toolbox.confirmDelete(
        '#modShj_SignUp_ConfirmDelete',
        'Abschlusstranskript löschen?',
        'Soll dieses Transkript<br /><br /><b>' +
        student.finalTranscripts.transkripte[index].dok_name + ' (' + student.finalTranscripts.transkripte[index].datum + ')' +
        '<br /><br />wirklich unwiderruflich gelöscht werden?',
        function() {
            student.finalTranscripts.drop(student.finalTranscripts.transkripte[index], function(){_renderFinalTranscripts();});
        });
}

function showFinalTranscripts(){
        if(typeof student.finalTranscripts==='undefined' || student.finalTranscripts===null){
        student.finalTranscripts = new shj.signUp.student.Dokumente(student.matrikelnummer,
                function(item) {
                    // Stelle die Dokumente in der Tabelle dar:
                    _renderFinalTranscripts();
                }, true);
         return;
        }
}

function _renderFinalTranscripts(){
    if(student.finalTranscripts){
        $('#listePermanenteTranskripte').empty();
        var pTranskr=[];
        for(var ii=0;ii<student.finalTranscripts.transkripte.length;ii++){
            if(student.finalTranscripts.transkripte[ii].permanent=='true'){
                with({transkr:student.finalTranscripts.transkripte[ii]}){
                    pTranskr.push('<tr><td>\n\
                    <a href="./dokument.rtf?matrikelnummer=' + student.matrikelnummer + 
                        '&datum=' + transkr.datum + 
                        '&verifikation=' + transkr.verifikationscode + '">' + 
                        transkr.dok_name + 
                        '</a></td><td>' + transkr.datum + '</td><td><i class="icon-trash" onclick="dropFinalTranscript(' + ii + ')"></i>&nbsp;</td></tr>');
                }

            }
        }
        $('#listePermanenteTranskripte').html('<table class="table">' + pTranskr.join('') + '</table>');
    }
}

// ==========================================================
// ==========================================================
// Thema Transkript
// ==========================================================
// ==========================================================

// @shj hj 2013-3-30
// 

// 
/**
 * Ändert die Modulzuordnung einer Leistung
 * (wird von der Transkriptansicht aus auf-
 * gerufen).
 * 
 * Die Änderung wird auf dem Server gespeichert
 * und die Ansichten des Transkripts und 
 * der Leistungen werden erneuert
 * 
 * @param {shj.signUp.student.Leistung} oLeistung die umzubuchende Leistung
 * @param {long} lNewModule die Id des neuen Moduls
 * @see json/student/leistung/update.jsp
 * @see shj.signUp.student.Leistung
 * @returns {undefined}
 */
function changeModule(oLeistung, lNewModule) {

    // Leistung.update() braucht
    // die Eigenschaft modul_id
    if (isNaN(lNewModule))
        oLeistung.modul_id = 'null';
    else
        oLeistung.modul_id = lNewModule;

    // Das alte Modul (Eigenschaft 'modul') muss 
    // zusätzlich überschrieben werden
    oLeistung.modul = lNewModule;

    // Update der Leistung auf den Server ...
    oLeistung.update(function() {

        // ... und im Erfolgsfall lokal
        // in Auflistung "leistungen" ersetzen
        for (var aa = 0; aa < student.leistungen.leistungen.length; aa++) {
            if (student.leistungen.leistungen[aa].id === oLeistung.id &&
                    student.leistungen.leistungen[aa].count === oLeistung.count) {
                //alert("Ja, ersetze auch die Leistung...");
                student.leistungen.leistungen[aa] = oLeistung;
                break;
            }
        }

        // Sowohl die Ansicht der Transkripte als 
        // auch der Leistungen aktualisieren
        // (ohne Serverkontakt)
        showTranscript();
        $('#divLeistungen').find('tr').remove();
    });
}

/**
 * Ändert die Leistungspunkte einer Leistung (wird 
 * vom Transkript aus aufgerufen).
 * 
 * Die Änderung wird auf den Server geladen und 
 * auch in der Anzeige der Leistungen umgesetzt.
 * 
 * @param {shj.signUp.student.Leistung} oLeistung
 * @param {float} fNewLP
 * @see json/student/leistung/update.jsp
 * @returns {undefined}
 */
function transcriptEditLP(oLeistung, fNewLP) {
    oLeistung.ects = parseFloat(fNewLP);
    oLeistung.update(function() {

        // ... und im Erfolgsfall lokal
        // in Auflistung "leistungen" ersetzen
        for (var aa = 0; aa < student.leistungen.leistungen.length; aa++) {
            if (student.leistungen.leistungen[aa].id === oLeistung.id &&
                    student.leistungen.leistungen[aa].count === oLeistung.count) {
                //alert("Ja, ersetze auch die Leistung...");
                student.leistungen.leistungen[aa] = oLeistung;
                break;
            }
        }

        // Sowohl die Ansicht der Transkripte als 
        // auch der Leistungen aktualisieren
        // (ohne Serverkontakt)
        showTranscript();
        // Erzwinge neue Darstellung der Leistungen
        // (Wenn die per .setView wieder aufgerufen wird).
        // [Achtung, wegen der Tabellen-Sortierfunktion
        //  muss das hier über Löschen und neuen Aufbau laufen]
        $('#divLeistungen').find('tr').remove();
    });
}

/**
 * Ändert den Kurstitel des ggf. zur Leistung gespeicherten 
 * Kurses (wird vom Transkript aus aufgerufen).
 * 
 * Achtung: es werden *alle* Leistungen diesen Typs 
 * in diesem Seminar entsprechend geändert.
 * 
 * Gibt es z.B. den Tippfehler "Darstellenden Texten" (anstatt
 * "Darstellende Texte"), können mit dieser Funktion alle 
 * Leistungen mit einem Schlag korrigiert werden.
 * 
 * @param {shj.signUp.student.Leistung} oLeistung
 * @param {String} sNewText der neue Kurstitel
 * @param {Boolean} bEnglish [false] ist es der englische Kurstitel? (Oder der deutsche?)
 * @see json/student/leistung/update.jsp inbsesondere die Option g_bSpreadKurstitelChanges
 * @returns {undefined}
 */
function transcriptEdit(oLeistung, sNewText, bEnglish) {
    if (bEnglish !== true)
        oLeistung.kurstitel = sNewText;
    else
        oLeistung.kurstitel_en = sNewText;
    oLeistung.update(function() {

        // ... und im Erfolgsfall lokal
        // in Auflistung "leistungen" ersetzen
        for (var aa = 0; aa < student.leistungen.leistungen.length; aa++) {
            if (student.leistungen.leistungen[aa].id === oLeistung.id &&
                    student.leistungen.leistungen[aa].count === oLeistung.count) {
                //alert("Ja, ersetze auch die Leistung...");
                student.leistungen.leistungen[aa] = oLeistung;
                break;
            }
        }

        // Sowohl die Ansicht der Transkripte als 
        // auch der Leistungen aktualisieren
        // (ohne Serverkontakt)
        showTranscript();
        
        // Erzwinge neue Darstellung der Leistungen
        // (Wenn die per .setView wieder aufgerufen wird).
        // [Achtung, wegen der Tabellen-Sortierfunktion
        //  muss das hier über Löschen und neuen Aufbau laufen]
        $('#divLeistungen').find('tr').remove();
    });
}

/**
 * Aus dem Transkript heraus soll
 * eine neue Leistung eingetragen 
 * werden (Klick auf Button 'Neu'). 
 * Per Voreinstellung wird hier
 * als Aussteller der aktive Nutzer,
 * und als Leistung und Modul die 
 * Werte aus der Transkript-Anzeige
 * @param {shj.signUp.student.Leistung} oLeistung Name der neuen Leistung (.name)
 * @param {long} lModulID Id des neuen Moduls
 * @returns {undefined}
 */
function transcriptAddLeistung(oLeistung, lModulID) {
    // Zeite Modal für die Eingabe
    showEmptyCreditForm();

    // Setze Werte für Leistung, Modul und Aussteller
    $('#leistung_name').val(oLeistung.name);
    $('#leistung_aussteller').val($.signUpGlobal.user_vorname + ' ' + $.signUpGlobal.user_name);

    // Stoße Prüfungen an
    $('#cboLeistung').blur();
    $('#cboAussteller').blur();

    // Stoße Logik-Check an
    setLeistungAktionen();
    $('#cboModul').val(lModulID);
}



/**
 * Stellt das Array für die Darstellung des Transkripts
 * zusammen. Die Darstellung erfolgt dann in renderTranskript.
 * 
 * Zu Beginn werden ggf. erst noch die Leistungen vom 
 * Server geladen.
 * 
 * Das Array ist
 * student -
 *         |
 *         - transkript -
 *                      |
 *                      - modul[] -
 *                                |
 *                                leistungen[].leistung
 *                                
 * Ob eine Leistung absolviert ist oder nicht wird
 * daran gemessen, 
 * ob typeof student.transkript.modul[x].leistungen[y].leistung.note === 'undefined'
 * 
 * Achtung, falls fSuccess übergeben wird, 
 * wird nicht gerendert!
 * 
 * @returns {unresolved}
 */
function showTranscript(oConfig, fSuccess, iCallbackLoopCount) {
    
    if(!oConfig) oConfig = {'fSuccess':null, "iCallbackLoopCount":0};
    if(oConfig.iCallbackLoopCount>=4){
        console.log('[ERR]: showTranscript: Callback Loop.');
        return;
    }
    console.log('showTranskript, Aufruf Nr. ' + oConfig.iCallbackLoopCount);
//    console.log('[DEBUG]: .showTranscript(' + typeof fSuccess + ')');
    // Schnittstelle:
    // 
    // Ggf. noch die Leistungen vom Server laden
    if (typeof student.leistungen === 'undefined' || student.leistungen == null) {
        console.log('[DEBUG]: ... Leistungen müssen noch geladen werden ...');
        student.leistungen = new shj.signUp.student.Leistungen(
                student.matrikelnummer, 
                function(){
                    showTranscript({'fSuccess':oConfig.fSuccess, 'iCallbackLoopCount':(oConfig.iCallbackLoopCount+1)});
                });
        
        console.log('[DEBUG]: ... exit nach Delegieren.\n\n');
        return;
    }

    console.log('[DEBUG]: ... Leistungen liegen vor.');

    // Reset
    student.transkript = [];
    student.transkript.modul = [];
    
    // Experimentell: Info Fachnote
    if(!student.fachnote){
        console.log('[DEBUG]: ... initialisiere Fachnote.\n\n');
        student.fachnote=new shj.signUp.student.Fachnoten(student.matrikelnummer,
            function(){
                // student.fachnote=data;
                renderFachnote();
                showTranscript({'fSuccess':oConfig.fSuccess, 'iCallbackLoopCount':(oConfig.iCallbackLoopCount+1)});
         });
         return;
    }
    var absolv = [];

    console.log('[DEBUG]: ... Loop durch Module des Fachs:');
    
    // Durchlaufe die Module des 
    // relevanten Studiengangs
    for (var ii = 0; ii < student.fach.module.length; ii++) {
        
        console.log('[DEBUG]: ... --- Modul ' + student.fach.module[ii].modul.name);
        // Notiere relevantes Modul beim Studierenden
        student.transkript.modul.push({"modul":
                {'name': student.fach.module[ii].modul.name,
                    'name_en': student.fach.module[ii].modul.name_en,
                    'id': student.fach.module[ii].modul.id,
                    'min_ects': student.fach.module[ii].modul.min_ects,
                    'leistungen': []}});

        console.log('[DEBUG]: ... --- ::: Loop durch Leistungen des Moduls:');
        // Durchlaufe die Leistungen, die in diesem 
        // Modul erbracht werden können
        for (var jj = 0; jj < student.fach.module[ii].modul.leistungen.length; jj++) {

            console.log('[DEBUG]: ... --- ::: --- Leistung Nr. ' + jj);
            // Ist die Leistung in diesem Modul absolviert?
            absolv = getAbsolvierte(student.fach.module[ii].modul, student.fach.module[ii].modul.leistungen[jj].leistung,ii,jj);

            // Falls es Leistung(en) gibt, die als absolviert
            // gelten: einzeln anhängen
            for (var kk = 0; kk < absolv.length; kk++) {
                // Marker, dass mindestens eine Leistung absolviert ist.
                // (Nicht identisch mit LP > 0 wg. Fremdsprachenkenntnisse/Latinum)
                student.transkript.modul[ii].modul.hatLeistung = true;
                student.transkript.modul[ii].modul.leistungen.push(absolv[kk]);
            }

            // Wenn nicht absolviert, generischer Eintrag der Leistung im Array
            if (absolv.length === 0) {
                student.transkript.modul[ii].modul.leistungen.push(
                        student.fach.module[ii].modul.leistungen[jj].leistung);
            }
        }// END for Leistungen
    }// end for MODULE

    // Hier sollte jetzt ein Array hängen, mit
    if(typeof oConfig.fSuccess === 'function') {
        console.log('[DEBUG]: ... student.transkript ist eingeräumt, rufe callback auf');
        oConfig.fSuccess();
    }
    else {
        console.log('[DEBUG]: ... student.transkript ist eingeräumt, rendere Transkript online');
        renderTranscript();
    }
}

function renderFachnote(){
    if(student.fachnote.fachnoten[0].komplett==='true'){
        $('#studentFachnote').html('Fachnote: ' + student.fachnote.fachnoten[0].notenwert + ' (' + student.fachnote.fachnoten[0].bezeichnung + ')<br />' + 
              ((student.fachnote.warnung) ? student.fachnote.warnung : ''));
    }else{
        //alert('testing this...');
        // $('#studentFachnote').text('Was ist denn da kaputt?');
        $('#studentFachnote').html('<p>Vorläufige Fachnote: ' + student.fachnote.fachnoten[0].notenwert +'<br /><a href="#" title="">[mehr...]</a></p>');
        $('#studentFachnote a').popover({
            'delay': {
                'show': 200,
                'hide': 100
            },
            'title':function(){return 'Informationen zur Fachnote';},
            'content':function(){return student.fachnote.fachnoten[0].info;},
            'placement': 'bottom',
            'trigger': 'hover'});
    }
    //$('#studentFachnote').show();
}

/**
 * Darstellung des Transkripts (ohne Serverkontakt)
 */
function renderTranscript() {
    var rows = [];
    var dLPErreicht = 0;
    var dLPMin = 0;
    var sBindings = '';

    // Reset || Schnittstelle
    $('#transkriptContextMenu').html('<ul></ul>');
    $('#tStudienbilanz tbody').find('tr').remove(); // vermutlich funktionslos
    $('#transkript').find('*').remove();            // Reset der alten Anzeige

    // Durchlaufe die relevanten Module und konstruiere
    // die HTML-Tabellen mit Leistungen, ggf. Noten, LP etc.
    for (var ii = 0; ii < student.transkript.modul.length; ii++) {
        var tbl = $('#tStudienbilanz').clone();
        dLPErreicht = 0;                                  // Summierung erreichter Leistungspunkte
        dLPMin = student.transkript.modul[ii].modul.min_ects;   // Geforderte Leistungspunkte dieses Moduls 

        // Eintrag für diese Modul im Kontextmenü
        // (zum Verschieben von Leistungen)
        sBindings += ",'cctxmod-" + student.transkript.modul[ii].modul.id + "': function(t){changeModule($(t).data('shj_item'), " + student.transkript.modul[ii].modul.id + ");}";
        $('#transkriptContextMenu ul').append('<li id="cctxmod-' + student.transkript.modul[ii].modul.id + '">&rarr; ' + student.transkript.modul[ii].modul.name);

        // Durchlaufe jetzt die Leistungen des Moduls und 
        // notiere die absolvierten Leistungen mit Note
        // und Leistungspunkten (etc.), und die noch 
        // nicht absolvierten (z.B. mit Button zum 
        // Erzeugen neuer Leistungen).
        for (var jj = 0; jj < student.transkript.modul[ii].modul.leistungen.length; jj++) {

            // Leistung ist absolviert
            if (typeof student.transkript.modul[ii].modul.leistungen[jj].note != 'undefined') {
                var row = $('#template_transkript_absolvierte').clone();
                dLPErreicht += parseFloat(student.transkript.modul[ii].modul.leistungen[jj].ects);
                shj.signUp.toolbox.mergeToRow(row, student.transkript.modul[ii].modul.leistungen[jj], 'template_transkript_absolvierte_');
                // Leistung ist NICHT absolviert (sondern "offen")
            } else {
                var row = $('#template_transkript_offene').clone();
                shj.signUp.toolbox.mergeToRow(row, student.transkript.modul[ii].modul.leistungen[jj], 'template_transkript_offene_');
            }
            rows.push(row);
        }

        tbl.attr('id', 'mod_' + student.transkript.modul[ii].modul.id);
        tbl.find('.template_ModulSummary_modulname').text(student.transkript.modul[ii].modul.name);

        // Fehlen noch LP? Ist das Modul erfüllt? Gibt es
        // zu viele LP?
        if (parseFloat(dLPMin)==0 || parseFloat(dLPMin) > parseFloat(dLPErreicht)) {
            sClass = 'badge badge-warning';
            if(parseFloat(dLPMin)==0) sSummary="0 LP verlangt, SignUp kann nicht sehen, zu wie viel % das Modul abgeschlossen ist.";
            else sSummary = Math.round(parseFloat(dLPErreicht) / parseInt(dLPMin) * 100) + '% abgeschl. (' + (dLPMin - dLPErreicht) + ' LP fehlen noch).';
        } else if (parseFloat(dLPMin) < parseFloat(dLPErreicht)) {
            sClass = 'badge badge-important';
            sSummary = (dLPMin - dLPErreicht) * (-1) + ' LP zu viel.';
        } else {
            sSummary = 'OK';
            sClass = 'badge badge-success';
        }

        tbl.find('.template_ModulSummary_computedInfo').text(sSummary).parents('table').find('th').addClass(sClass);
        tbl.append(rows);
        $('#transkript').append(tbl);

        // Trennstrich vor der nächsten Tabelle
        $('#transkript').append('<hr>');
        rows = [];
    }

    // Vermerke auch Zusatzmodul
    // im Kontextmenü
    $('#transkriptContextMenu ul').append('<li id="zusatz">&rarr; Zusatzleistung');
    sBindings = '{' + sBindings.substring(1) + ',zusatz: function(t){changeModule($(t).data("shj_item"), "zusatz");}' + '}';

    // Die 'Bindings' enthalten die Trigger
    // des Kontextmenüs. Sie müssen bei Ver-
    // schiebungen der Leistungen neu 
    // initialisiert werden, werden hier
    // also global im Objekt 'Student' ge-
    // speichert.
    student.transkriptbindings = eval('(' + sBindings + ')');
    $('#transkript tr.with_contextmenu').contextMenu('transkriptContextMenu', {
        itemStyle: {fontSize: '0.7em'},
        bindings: eval('(' + sBindings + ')')
    });

    // Aktiviere das Register.
    setView($.signUpGlobal.iVIEW_STUDENT_TRANSKRIPT);
}

// ==========================================================
// ==========================================================
// Thema Kurse suchen und anzeigen
// ==========================================================
// ==========================================================

// ==========================================================
// Auswahl Semester, Lehrende, Titel ... um
// Kurs aus dem Archiv wählen zu können
/**
 * Zeigt die Auswahl Semester, Lehrende, Titel ... um
 * ggf. aus dem Archiv einen Kurs wählen zu können.
 * @param {Boolean} bCommitment ist es eine Anmeldung? (Oder eine Leistung?)
 * @returns {undefined} nichts
 */
function showKurssuche(bCommitment) {
    fillSemesterCboForKurssuche();
    showKurssucheFormular(true);
    $('#modKurssuche').modal('show');
    $('#bCommitment').val(bCommitment);
}

/**
 * Ändert die Ansicht der Kurssuche vom 
 * Suchformular zur Ergebnistabelle
 * @param {Boolean} bOn falls true: zeige die Ergebnistabelle
 */
function showKurssucheFormular(bOn) {
    if (bOn) {
        $('#Kurssuchformular').show();
        $('#modKurssuche .modal-footer').show();
        $('#Kurssuchergebnis').hide();
    } else {
        $('#Kurssuchformular').hide();
        $('#modKurssuche .modal-footer').hide();
        $('#Kurssuchergebnis').show();
    }
}

/**
 * Lädt und zeigt tabellarische Kursliste, die den Kriterien
 * der Kurssuche (u.U. aus dem Archiv)
 * erfüllt
 * @returns {Boolean} false (um Formularsendung zu unterbinden)
 */
function showKurssucheErgebnis() {
    showKurssucheFormular(false);
    $('#cmdSearch').button('loading');
        shj.signUp.search.findKurse($('#fKurssuche').serialize(),
            {'item': 'kurs',
                'emptyFirst': true,
                'actOnDom': true,
                'stripPrefixOffId': false,
                'HTML_template': '#template_kurs',
                'HTML_table_container': '#Kurssuchergebnis',
                'HTML_template_class_prefix': 'kurssuche_'},
                function(){
                    alert("Sorry -- unter diesen Kriterien wurde kein Kurs gefunden");
                    showKurssucheFormular(true);
                }
    );


    $('#cmdSearch').button('reset');
    // Formular anhalten:
    return false;
}

/**
 * Initialisiert das Formular zum Ausstellen einer 
 * neuen Leistung auf Basis eines Kurses.
 * 
 * Bei einer Anmeldung werden ggf. noch Module
 * zur Wahl gestellt.
 * 
 * @param {object} kurs
 * @returns {undefined}
 */
function coursePicked(kurs) {
    if ($('#bCommitment').val() == 'true') {
        var lModulID=-1;
        var lModulIDSingle=-1;
        var sModuleTable = new Array();
        $.each(student.fach.module, function(key, val) {
//            var sTmp = '' + val.leistungen;
            lModulID = val.modul.id.toString();
            
            if(modulContainsLeistung(lModulID, kurs.leistung_id.toString())){
                lModulIDSingle=val.modul.id;
//            if (sTmp.indexOf(';' + kurs.leistung_id + ';') >= 0) {
                
                sModuleTable.push('<tr class="linked-tablerow" class="anmeldungModulID" modul_id="' + lModulID + '"><td>' + val.modul.name + '</td></tr>');
            }
        });

        // Falls mehr als ein Modul existiert, 
        // das diese Leistung enthält, wird 
        // die Modulauswahl angezeigt
        if (sModuleTable.length > 1) {
            $('#tKurssuchergebnistabelle').html('');
            $('#lblKurssuche_Modulwahl').text("Bitte wählen Sie ein Modul...");
            $('#tKurssuchergebnistabelle').append(sModuleTable.join());

            // die Tabellenzeilen sind Links zu Details:
            $('#tKurssuchergebnistabelle tr').bind('click', function() {
                addAnmeldung(kurs, $(this).attr('modul_id'));
            });
        } else {
            // ------------------------------------------------------
            // Lade Leistung auf Server und 
            // aktualisiere Darstellung
            if (sModuleTable.length === 1) {
                addAnmeldung(kurs, lModulIDSingle);
            }else{
                addAnmeldung(kurs, -1);
            }
        }
    } else {
        showKursAsLeistungToAdd(kurs);
    }
}

/**
 * Zeigt das Formular zum Erfassen einer 
 * neuen Leistung (mit Vorauswahl-Werten
 * des Kurses).
 * @param {object} kurs
 * @returns {undefined}
 */
function showKursAsLeistungToAdd(kurs) {
    initSeminarLeistungen();

    // Setze die Eingabemaske zurück:
    $('#fLeistungDetails input').val('');
    $('#fLeistungDetails').removeData();
    $('#fLeistungDetails').data('kurs', kurs);
    $('#txtLeistungID').focus();

    // Setze Datum auf Min(letzten 
    // Tag des gewählten Semesters, heute)
    var today = new Date();
    if (kurs.semester === 'current')
        $('#leistung_datum').val(today.getDate() + '.' + (parseInt(today.getMonth()) + 1) + '.' + today.getFullYear());
    else
        $('#leistung_datum').val(shj.signUp.toolbox.getLastDayOfSemester(kurs.semester));


    // Lade aus der angeklickten Tabellenzeile
    // Informationen ins Formular
    $('#modLeistungDetails h3').html('Neue Leistung (' + kurs.semester_indicator + ')');
    $('#leistung_name').val(kurs.leistung);
    $('#leistung_aussteller').val(kurs.lehrende);
    $('#leistung_kurstitel').val(kurs.titel);
    $('#leistung_kurstitel_en').val(kurs.titel_en);
    $('#leistung_ects').val(kurs.ects);

    // Noten cbo zurücksetzen
    $("#leistung_noten_id option[value='null']").remove();
    $("<option/>").val('null').text('--wählen--').appendTo("#leistung_noten_id");
    $("#leistung_noten_id option[value='null']").attr('selected', 'true');

    // Löschen geht nicht, es ist ja 
    // noch gar nicht gespeichert:
    $('#divConfirmDelete').hide();
    $('#divCreditDetailOptions').show();

    // öffne Modalansicht:
    $('#modKurssuche').modal('hide');
    $('#modLeistungDetails').modal('show');
}

/**
 * Schnelles Ausblenden des Popovers bei Tabellen
 * @returns {undefined}
 */
function hideLeistungPopover() {
    $('#leistungen').find('td:nth-child(1)').popover('hide');
    $('#anmeldungen').find('td:nth-child(1)').popover('hide');
}

// ==========================================================
// ==========================================================
// Lebenszyklus der Objekte bzw. Sammlungen im Frontend:
// .add<Name>(obj)          Hinzufügen im Backend (Objekt und Sammlung)
// .update<Name>(obj)       Aktualisieren im Backend (Objekt)
// .delete<Name>(obj)       Löschen im Backend (Objekt und Sammlung)
// .show<Namen>             -- Anzeige in Listenform o.ä. (Sammlung)
// .show<Name>              -- Detailanzeige mit Möglichkeit (Objeit
//                              zum Bearbeiten, Löschen, oder neu hinzufügen
// 
// 
// Update/Add/Delete-Formulare blenden Steuerelemente ein/aus per
// .shj_lifecycle_delete/_add/_update
// ==========================================================
// ==========================================================

// ==========================================================
// B E M E R K U N G E N
// .showBemerkungen
// .showBemerkung
// .addBemerkung
// .updateBemerkung
// .deleteBemerkung
// 
// Anzeige der Liste von Bemerkungen
// (hier realisiert durch eine definition-list 'dl')

/**
 * Lädt ggf. die Bemerkungen zum Studierenden 
 * vom Server und zeigt sie an.
 * @returns {Boolean|nothing}
 */
function showBemerkungen() {
    if($.signUpGlobal.access_level<500) return;
    if (student.bemerkungen != null && typeof student.bemerkungen != 'undefined') {
        if (student.matrikelnummer == student.bemerkungen.matrikelnummer) {
            setView($.signUpGlobal.iVIEW_STUDENT_BEMERKUNGEN);
            return true;
        }
    }

    student.bemerkungen = new shj.signUp.student.Bemerkungen(student.matrikelnummer,
            function(item) {
                item.bemerkungen.shj_rendering = {'item': item.bemerkungen,
                    'emptyFirst': true,
                    'HTML_template': '#template_studentDetails_BemerkungListe',
                    'HTML_table_container': '#studentDetails_BemerkungListeContainer',
                    'HTML_template_class_prefix': 'template_studentDetails_Bemerkung'};
                shj.signUp.toolbox.render(item.bemerkungen);
            });
}

/**
 * Falls oBemerkung_in definiert ist:
 * Anzeige der einzelnen 
 * Bemerkung mit Möglichkeit
 * zum Edieren und Löschen
 * 
 * Falls oBemerkung_in undefined ist:
 * Möglichkeit zum Eingeben
 * einer neuen Bemerkung (durch
 * Aufruf von .showBemerkung()).
 * @param {shj.signUp.student.Bemerkung} oBemerkung_in
 */
function showBemerkung(oBemerkung_in) {
    if($.signUpGlobal.access_level<500) return;
    // Es wurde keine Bemerkung übergeben,
    // also Modalfenster zur Neueingabe vorbereiten:
    if (typeof oBemerkung_in == 'undefined') {
        $('#fBemerkungEdit').removeData();
        $('#fBemerkungEdit textarea').val('');
        $('#fBemerkungEdit').find('.shj_lifecycle_delete').hide();
        $('#fBemerkungEdit').find('.shj_lifecycle_update').hide();
        $('#fBemerkungEdit').find('.shj_lifecycle_add').show();
    } else {

        // Es wurde eine Bemerkung übergeben, also 
        // wird Sie zum Ändern oder Löschen angezeigt:
        shj.signUp.toolbox.render(oBemerkung_in, {
            'item': 'whatever',
            'emptyFirst': true,
            'actOnDom': true,
            'stripPrefixOffId': false,
            'HTML_template': '#fBemerkungEdit',
            'HTML_template_class_prefix': 'bemerkung_'}
        );
        $('#fBemerkungEdit').find('.shj_lifecycle_delete').show();
        $('#fBemerkungEdit').find('.shj_lifecycle_update').show();
        $('#fBemerkungEdit').find('.shj_lifecycle_add').hide();
    }

    // Blende Modalfenster ein
    $('#modBemerkungErfassen').modal('show');
}

/**
 * Lädt die Änderung der Bemerkung auf den Server und 
 * aktualisiert die Anzeige.
 * @param {shj.signUp.student.Bemerkung} bemerkung_in
 */
function updateBemerkung(bemerkung_in) {
    $('#cmdUpdateBemerkung').button('loading');

    // Erneuere den Text der Bemerkung:
    bemerkung_in.bemerkung = $('#bemerkung_bemerkung').val()

    bemerkung_in.update(
            function(data) {
                // Stelle die Leistungen in der DL dar:
                shj.signUp.toolbox.render(student.bemerkungen.bemerkungen)
            }
    );

    // Anzeige zurücksetzen
    $('#cmdUpdateBemerkung').button('reset');

    // Dialog ausblenden    
    $('#modBemerkungErfassen').modal('hide');
}

/**
 * Lade Bemerkungstext einer neuen Bemerkung auf den 
 * Server und erneuere die aktuelle Anzeige
 */
function addBemerkung() {
    if($.signUpGlobal.access_level<500) return;
    $('#cmdAddBemerkung').button('loading');

    // Server und Darstellung
    student.bemerkungen.add(student.matrikelnummer,
            {"name":"bemerkung","bemerkung":$('#bemerkung_bemerkung').val(),"matrikelnummer":student.matrikelnummer},
            function(data) {
                shj.signUp.toolbox.render(student.bemerkungen.bemerkungen);
                $('#modBemerkungErfassen').modal('hide');
            }
    );

    // Anzeige zurücksetzen.
    $('#cmdAddBemerkung').button('reset');
}

/**
 * Löscht die übergebene Anzeige aus Anzeige
 * und Datenbank. 
 * 
 * Fragt vorher: wirklich?
 * 
 * @param {shj.signUp.student.Bemerkung} bemerkung_in
 * @returns {undefined}
 */
function deleteBemerkung(bemerkung_in) {
    if (bemerkung_in instanceof shj.signUp.student.Bemerkung) {
        // Nachfragen: wirklich löschen?
        shj.signUp.toolbox.confirmDelete(
                '#modShj_SignUp_ConfirmDelete',
                'Bemerkung löschen?',
                'Soll diese Bemerkung<br /><br /><b>' +
                bemerkung_in.bemerkung +
                '<br /><br />wirklich unwiderruflich gelöscht werden?',
                function() {
                    student.bemerkungen.drop(
                            bemerkung_in,
                            function() {
                                shj.signUp.toolbox.render(student.bemerkungen.bemerkungen);
                            });
                });
    } else {
        throw Exception('Interner Fehler: das zum Löschen übergebene Objekt war keine Bemerkung');
    }
}

// ENDE -- B E M E R K U N G E N
// ==========================================================

// ==========================================================
// Leistungen
// .showLeistungen      // entscheidet zunächst, ob Serverkontakt notwendig, wechselt sonst nur die 'Tabe'
// ._renderLeistungenTabelle   // rendert. Extrahierte Fkt, weil mehrfach verwendet.
// .showLeistung
// .addLeistung
// .udpateLeistung
// .deleteLeistung
// 
// Hinzufügen von Leistungen fkt. so:
// Klick auf 'neu -- mit Kurs': 
//   .'showKurssuche()'
//   
// Kurs ausgewählt ->
//   .showKursAsLeistungToAdd()
//   
// (Implizit: setLeistungAktionenNewCredit)
//


/**
 * Zeige übergebene Leistung im Formular mit 
 * Änderungs- und Löschoption.
 * @param {shj.signUp.student.Leistung} leistung_in
 */
function showLeistung(leistung_in) {

    // ====================================================
    // Schnittstelle
    initSeminarLeistungen();                            // Sicherstellen, dass Typeahead initialisiert
    $("#cboNote option[value='null']").remove();        // Man kann nicht _keine Note_ wählen
    hideLeistungPopover();                              // Kosmetik
    $('#fLeistungDetails input').unbind('change');
    $('#fLeistungDetails select').unbind('change');
    $('#fLeistungDetails').find('[id^=cg]').removeClass('warning').removeClass('error');

    // Trage Eigenschaften der Leistung
    // ins Formular ein
    shj.signUp.toolbox.render(leistung_in, {
        "item": "leistung",
        "emptyFirst": true,
        "actOnDom": true,
        "stripPrefixOffId": false, // Das Formular wird direkt im HTML ersetzt
        "HTML_template": "#fLeistungDetails",
        "HTML_template_class_prefix": 'leistung_'}
    );

    // Korrektürchen
    if (leistung_in.leistungstyp == '')
        leistung_in.leistungstyp = '--';

    // Soll das Textfeld zur Hausarbeit angezeigt werden?
    controlVisibilityHA();

    // Welche Module sind zu dieser Leistung wählbar?
    initModule(leistung_in.id, leistung_in.modul);
    
    $('#leistung_anerkannt').prop('checked', (leistung_in.anerkannt=='t'));

    // Blende Formular ein.
    $('#cmdDeleteCredit').show();
    $('#modLeistungDetails').modal('show');

    // Kontrolle über Änderungen
    $('#fLeistungDetails input').bind('change', function() {
        setLeistungAktionen(leistung_in);
    });
    $('#fLeistungDetails select').bind('change', function() {
        setLeistungAktionen(leistung_in);
    });
    $('#leistung_leistungstyp').bind('change', function() {
        controlVisibilityHA();
    });
    $('#cmdUpdateCredit').addClass('disabled');
}

/**
 * Leistung hinzufügen (auf dem Server sowie in der Optik)
 * Die Eigenschaften der Leistung werden über 
 * ein HTML Formular definiert (siehe Code).
 * @param {Boolean} bPrint gleich nach dem Speichern RTF ausgeben?
 * @returns {Boolean}
 */
function addLeistung(bPrint) {
    
    // Schnittstelle: ggf. zur Initialisierung Leistungen
    // laden und diese Fkt. neu aufrufen
    if (student.leistungen == null || typeof student.leistungen == 'undefined') {
        // Erst Leistungen laden
        showLeistungen(function() {
            addLeistung();
        });
        return true;
    }

    $('#cmdUpdateCredit').button('loading');

    // ------------------------------------------------------
    // Stelle Eigenschaften d. Leistung zusammen            
    // (#HACK: bei besseren Feldnamen wäre das Mapping
    //  überflüssig).
    var leistung_tmp = {};
    leistung_tmp.id = getLeistungID('#leistung_name');
    leistung_tmp.name = $('#leistung_name').val();
    leistung_tmp.kurstitel = $('#leistung_kurstitel').val();
    leistung_tmp.kurstitel_en = $('#leistung_kurstitel_en').val();
    leistung_tmp.noten_id = $('#leistung_noten_id').val();
    leistung_tmp.note = $('#leistung_noten_id option:selected').text();
    leistung_tmp.datum = $('#leistung_datum').val();
    leistung_tmp.ects = $('#leistung_ects').val();
    leistung_tmp.leistungstyp = $('#leistung_leistungstyp').val();
    leistung_tmp.details = $('#leistung_details').val();
    leistung_tmp.bemerkung = $('#leistung_bemerkung').val();
    leistung_tmp.modul_id = $('#cboModul').val();
    leistung_tmp.modul = leistung_tmp.modul_id;
    leistung_tmp.aussteller_id = getDozentID($('#leistung_aussteller').val());
    leistung_tmp.aussteller_vorname = $('#leistung_aussteller').val();
    leistung_tmp.aussteller_nachname = '';
    leistung_tmp.matrikelnummer=student.matrikelnummer;
    if($('#leistung_anerkannt').prop('checked')===true) leistung_tmp.anerkannt='t';
    else leistung_tmp.anerkannt=null;

    if (typeof $('#fLeistungDetails').data('kurs') != 'undefined') {
        // Es ist keine generische Leistung, sondern ein Kurs
        // wurde zuvor ausgewählt:
        leistung_tmp.kurs_id = $('#fLeistungDetails').data('kurs').id;
        leistung_tmp.kurstyp_id = $('#fLeistungDetails').data('kurs').kurstyp_id;
        if ($('#fLeistungDetails').data('kurs').semester_indicator != 'Current')
            leistung_tmp.archiv = true;
    }

    // ------------------------------------------------------
    // Lade Leistung auf Server und 
    // aktualisiere Darstellung
    student.leistungen.add(student.matrikelnummer,
            leistung_tmp,
            function(data) {
                _renderLeistungenTabelle(student.leistungen);
                if (bPrint)
                    printLeistung(data.leistung);
                $('#modLeistungDetails').modal('hide');

                // Falls die Transkriptansicht aktiv ist,
                // muss sie neu ge-rendert werden:
                if ($('#studentTranskript').hasClass('active'))
                    showTranscript();
            }
    );

    // Anzeige zurücksetzen.
    $('#cmdUpdateCredit').button('reset');
}

/**
 * Löscht die übergebene Leistung von Server und Anzeige.
 * (Fragt zunächst "Wirklich?")
 * 
 * Über den Löschvorgang (und einige Details der Leistung) 
 * wird eine Bemerkung angelegt und gespeichert.
 * @param {shj.signUp.student.Leistung} leistung_in
 * @returns {Boolean}
 */
function deleteLeistung(leistung_in) {
    if (leistung_in instanceof shj.signUp.student.Anmeldung) {
        deleteAnmeldung(leistung_in);
        return true;
    }

    shj.signUp.toolbox.confirmDelete(
            '#modShj_SignUp_ConfirmDelete',
            'Leistung löschen?',
            'Soll diese Leistung<br /><br /><b>' +
            leistung_in.name + '</b> (' + leistung_in.aussteller_nachname + ')' +
            '<br /><br />wirklich unwiderruflich gelöscht werden?',
            function() {
                student.leistungen.drop(
                        leistung_in,
                        function() {
                            _renderLeistungenTabelle(student.leistungen);
                            student.bemerkungen.add(student.matrikelnummer, {"name":"bemerkung","bemerkung":'Leistung gelöscht (' +
                                    leistung_in.name + ' (' + leistung_in.id + '), ' +
                                    leistung_in.aussteller + ', ' +
                                    leistung_in.kurstitel + ', Note: ' + leistung_in.note + ' vom ' +
                                    leistung_in.datum,"matrikelnummer":student.matrikelnummer});

                            if ($('#studentTranskript').hasClass('active'))
                                showTranscript();
                        });
            },
            function() {
            }
    );
}

/**
 * Speichere die Leistung aus dem Formular mit den 
 * Werten der aktuellen Formularfelder.
 * 
 * @param {shj.signUp.student.Leistung} leistung_in
 * @param {Boolean} bPrint Soll gleich ein RTF ausgegeben werden?
 * @returns {Boolean}
 */
function updateLeistung(leistung_in, bPrint) {

    if (typeof leistung_in == 'undefined') {
        addLeistung(bPrint);
        return true;
    }

    // Korrigiere die veränderlichen Eigenschaften der Leistung
    leistung_in.id = getLeistungID('#leistung_name');
    leistung_in.name = $('#leistung_name').val();
    leistung_in.kurstitel = $('#leistung_kurstitel').val();
    leistung_in.kurstitel_en = $('#leistung_kurstitel_en').val();
    leistung_in.noten_id = $('#leistung_noten_id').val();
    leistung_in.notenbezeichnung = $('#leistung_noten_id').text();
    leistung_in.note = $('#leistung_noten_id :selected').text();
    leistung_in.datum = $('#leistung_datum').val();
    leistung_in.ects = $('#leistung_ects').val();
    leistung_in.leistungstyp = $('#leistung_leistungstyp').val();
    leistung_in.details = $('#leistung_details').val();
    leistung_in.bemerkung = $('#leistung_bemerkung').val();
    leistung_in.modul_id = $('#cboModul').val();
    leistung_in.modul = $('#cboModul').val();     // Sorry, muss aber sein, siehe getModulBezeichnung bzw. Popover-Aktivierung weiter unten in dieser Methode
    leistung_in.aussteller_id = getDozentID($('#leistung_aussteller').val());
    leistung_in.count_orig = leistung_in.count;
    if($('#leistung_anerkannt').prop('checked')===true) leistung_in.anerkannt='t';
    else leistung_in.anerkannt=null;

    leistung_in.update(
            // Falls erfolgreiche, Anzeige erneuern:
                    function(data) {
                        //student.leistungen=null;
                        $('#modLeistungDetails').modal('hide');

                        // Tabelle neu laden und ggf. Schein drucken
                        // #HACK: AB HIER: ES KÖNNTE AUCH EINE 
                        //        LEISTUNG SEIN; DIE IN EINEN SCHEIN
                        //        UMGEWANDELT WURDE!
                        if (leistung_in instanceof shj.signUp.student.Anmeldung) {
                            if (student.leistungen != null && typeof student.leistungen != 'undefined') {
                                student.leistungen.leistungen.push(leistung_in);
                            } else {
                                showLeistungen();
                            }
                            student.anmeldungen.drop(
                                    leistung_in,
                                    function() {
                                        _renderAnmeldungenTabelle(student.anmeldungen);
                                    });
                        }
                        // Transkript zurücksetzen
                        student.transkript = null;
                        setView($.signUpGlobal.iVIEW_STUDENT_LEISTUNGEN);
                        if (student.leistungen == null)
                            showLeistungen();
                        else
                            _renderLeistungenTabelle(student.leistungen);

                        if (bPrint)
                            printLeistung(leistung_in);
                    },
                    function() {
                        alert('Update der Leistung fehlgeschlagen -- keine weiteren Informationen verfügbar');
                    }
            );
        }





/**
 * Zeigt die Anmeldung im Formular zum Ändern/Löschen/
 * Überführen in eine Leistung
 * 
 * @param {shj.signUp.student.Anmeldung} anmeldung_in
 * @returns {undefined}
 */
function showAnmeldung(anmeldung_in) {

    // ====================================================
    // Schnittstelle
    $("#cboNote option[value='null']").remove();
    hideLeistungPopover();
    $('#fLeistungDetails input').unbind('change');
    $('#fLeistungDetails select').unbind('change');
    $('#fLeistungDetails').find('[id^=cg]').removeClass('warning').removeClass('error');

    // Trage Eigenschaften der Leistung
    // ins Formular ein
//            mergeToForm('#fLeistungDetails', leistung_in, 'leistung_');
    shj.signUp.toolbox.render(anmeldung_in, {
        "item": "anmeldung",
        "emptyFirst": true,
        "actOnDom": true,
        "stripPrefixOffId": false, // Das Formular wird direkt im HTML ersetzt
        "HTML_template": "#fLeistungDetails",
        "HTML_template_class_prefix": 'leistung_'}
    );

    // Welche Module sind zu dieser Leistung wählbar?
    initModule(anmeldung_in.id, anmeldung_in.modul);

    // Falls es sich um eine Anmeldung handeln sollte: -------------------
    $("<option/>").val('null').text('--wählen--').appendTo("#leistung_noten_id");
    $("#leistung_noten_id option[value='null']").attr('selected', 'true');
    // _____________________________________________________Ende Anmeldung

    // Blende Formular ein.
    $('#modLeistungDetails').modal('show');

    // Kontrolle über Änderungen
    $('#fLeistungDetails input').bind('change', function() {
        setLeistungAktionen(anmeldung_in);
    });
    $('#fLeistungDetails select').bind('change', function() {
        setLeistungAktionen(anmeldung_in);
    });
    $('#leistung_leistungstyp').bind('change', function() {
        controlVisibilityHA();
    });
    $('#cmdUpdateCredit').addClass("disabled");
}

/**
 * Zeigt leeres Formular zum Eingeben
 * einer neuen Leistung
 * @returns {undefined}
 */
function showEmptyCreditForm() {

    initSeminarLeistungen();

    // Setze Formular zurück
    $('#fLeistungDetails').removeData();
    $('#fLeistungDetails input').val('');

    // Setze Datum auf heute
    var today = new Date();
    $('#leistung_datum').val(today.getDate() + '.' + (parseInt(today.getMonth()) + 1) + '.' + today.getFullYear());
    $('#modLeistungDetails h3').html('Neue Leistung');

    // Notencombo zurücksetzen
    $("#cboNote option[value='null']").remove();
    $("<option/>").val('null').text('--wählen--').appendTo("#cboNote");
    $("#cboNote option[value='null']").attr('selected', 'true');

    // Löschen geht nicht
    $('#cmdDeleteCredit').hide();
    $('#divCreditDetailOptions').show();

    // Setze angemeldetn Benutzer als Vorauswahl
    $('#leistung_aussteller').val($('#logged-in-user-vorname').val() + ' ' + $('#logged-in-user-name').val());
    $('#leistung_aussteller').blur();
    
    // öffne Modalansicht:
    $('#modLeistungDetails').modal('show');
    

}

// ==========================================================
// ==========================================================
// Lebenszyklus der Objekte im Frontend:
// .add<Name>(obj)          Hinzufügen im Backend
// .update<Name>(obj)       Aktualisieren im Backend
// .delete<Name>(obj)       Löschen im Backend
// .show<Namen>             -- Anzeige in Listenform o.ä.
// .show<Name>              -- Detailanzeige mit Möglichkeit
//                              zum Bearbeiten, Löschen, oder neu hinzufügen
// 
// 
// Update/Add/Delete-Formulare blenden Steuerelemente ein/aus per
// .shj_lifecycle_delete/_add/_update
// ==========================================================
// ==========================================================

/**
 * Navigiert zum RTF-Dokument zur Anzeige der Leistung
 * @param {shj.signUp.student.Leistung} leistung_in
 * @returns {undefined}
 */
function printLeistung(leistung_in) {
    window.location = 'print/Credit2Rtf.rtf?leistung=' + leistung_in.id + '&leistung_count=' + leistung_in.count;
}


/**
 * Gibt die Id der Leistung zurück, deren 
 * Name in #leistung_name steht.
 * 
 * Außerdem wird die Modulauswahl sinnvoll eingeschränkt
 * und die Leistungspunkte vorgeschlagen.
 * 
 * @returns {Number} Id der Leistung oder -1 falls nicht erkannt.
 */
function getLeistungID() {
    initSeminarLeistungen();

    // LeistungsID bestimmen:
    var lLeistungID = -1;
    var sLeistungName = $('#leistung_name').val();
    for (var ii = 0; ii < $.signUpGlobal.info.seminar.leistungen.length; ii++) {
        if ($.signUpGlobal.info.seminar.leistungen[ii].name === sLeistungName) {
            lLeistungID = $.signUpGlobal.info.seminar.leistungen[ii].id;
            break;
        }
    }

    // Modulauswahl neu einstellen
    initModule(lLeistungID, $('#cboModul').val());

    // @todo: check: wenn das hier steht, ist es 
    // dann noch möglich, von der Vorgabe
    // abzuweichen?
    initCreditPts(lLeistungID);
    return lLeistungID;
}

/**
 * Gibt die Id des Dozenten zurück, 
 * dessen (Vor- und Nach)Name in #leistung_aussteller steht
 * 
 * @returns {Number} Id des Dozenten oder -1, falls unbekannt
 */
function getDozentID() {
    var lDozentID = -1;
    var sDozentCbo = $('#leistung_aussteller').val();
    for (var ii = 0; ii < $.signUpGlobal.info.seminar.dozenten.dozenten.length; ii++) {
        if ($.signUpGlobal.info.seminar.dozenten.dozenten[ii].vorname + ' ' + $.signUpGlobal.info.seminar.dozenten.dozenten[ii].name == sDozentCbo) {
            lDozentID = $.signUpGlobal.info.seminar.dozenten.dozenten[ii].id;
            break;
        }
    }
    return lDozentID;
}

/**
 * Sind die Werte für eine neue Leistung
 * sinnvoll gesetzt?
 */
function setLeistungAktionenNewCredit() {
    if ($('#cboNote').val() <= 0 || $('#cboNote').val() == 'null')
        $('#cgNote').addClass('error');
    else
        $('#cgNote').removeClass('error');

    // Leistung eingegeben und erkannt?
    var lLeistungID = getLeistungID();
    if (lLeistungID < 0)
        $('#cgLeistung').addClass('error');
    else {
        $('#cgLeistung').removeClass('error');
        initModule(lLeistungID, $('#cboModul').val());
    }

    // Aussteller eingegeben und erkannt?
    if (getDozentID() < 0)
        $('#cgAussteller').addClass('error');
    else
        $('#cgAussteller').removeClass('error');

    // --------------------------------------------------------
    // Gibt es keine 
    // Fehler? Dann "Speichern" aktivieren:
    if ($('#fLeistungDetails .error').length == 0)
        $('#cmdUpdateCredit').removeClass("disabled");
    else
        $('#cmdUpdateCredit').addClass("disabled");
}

/**
 * (De)aktivieren von Schaltflächen
 * @param {shj.signUp.student.Leistung} leistung oder 'undefined'
 * @returns {unresolved}
 */
function setLeistungAktionen(leistung) {

    if (leistung === null || typeof leistung === 'undefined') {
        setLeistungAktionenNewCredit();
        return;
    }

    // --------------------------------------------------------
    // Note, Datum, LP geändert?
    if ($('#cboNote').val() != leistung.noten_id ||
            $('#leistung_datum').val() != leistung.datum ||
            $('#leistung_ects').val() != leistung.ects) {
        $('#cgNote').addClass('warning');
    }
    else
        $('#cgNote').removeClass('warning');

    if ($('#cboNote').val() <= 0 || $('#cboNote').val() == 'null')
        $('#cgNote').addClass('error');
    else
        $('#cgNote').removeClass('error');

    // --------------------------------------------------------
    // Leistung geändert?		
    if ($('#leistung_name').val() != leistung.name) {
        if (getLeistungID() < 0)
            $('#cgLeistung').addClass('error');
        else {
            $('#cgLeistung').removeClass('error');
            $('#cgLeistung').addClass('warning');
        }
    } else {
        $('#cgLeistung').removeClass('warning');
        $('#cgLeistung').removeClass('error');
    }

    // --------------------------------------------------------
    // Kurstitel geändert?		
    if ($('#leistung_kurstitel').val() != leistung.kurstitel)
        $('#cgKurs').addClass('warning');
    else
        $('#cgKurs').removeClass('warning');

    // --------------------------------------------------------
    // Modul geändert?
    if ($('#cboModul').val() != leistung.modul)
        $('#cgModul').addClass('warning');
    else
        $('#cgModul').removeClass('warning');

    // --------------------------------------------------------
    // Aussteller geändert?
    if ($('#leistung_aussteller').val() != leistung.aussteller) {
        if (getDozentID() < 0)
            $('#cgAussteller').addClass('error');
        else
            $('#cgAussteller').addClass('warning').removeClass('error');
    }
    else
        $('#cgAussteller').removeClass('warning').removeClass('error');

    // --------------------------------------------------------
    // HA/Klausur geändert? Oder HA Titel?
    if ($('#cboLeistungstyp').val() != leistung.leistungstyp ||
            $('#leistung_details').val() != leistung.details)
        $('#cgLeistungstyp').addClass('warning');
    else
        $('#cgLeistungstyp').removeClass('warning');

    // --------------------------------------------------------
    // Bemerkung geändert?
    if ($('#leistung_bemerkung').val() != leistung.bemerkung)
        $('#cgBemerkung').addClass('warning');
    else
        $('#cgBemerkung').removeClass('warning');

    // --------------------------------------------------------
    // Gibt es Warnungen? Aber keine 
    // Fehler? Dann "Speichern" aktivieren:
    if ($('#fLeistungDetails .warning').length > 0 && $('#fLeistungDetails .error').length == 0)
        $('#cmdUpdateCredit').removeClass("disabled");
    else
        $('#cmdUpdateCredit').addClass("disabled");
}

// =============================================================================================
// Register 'Details
// =============================================================================================
/**
 * sollen auch 
 */
//function toggleInactive() {
//    if ($('#chkShowInactive').attr('checked')) {
//        $('#results').find('.tdinactive').removeClass('tdinactive').addClass('tdinactivevisible');
//    } else {
//        $('#results').find('.tdinactivevisible').removeClass('tdinactivevisible').addClass('tdinactive');
//    }
//}
//;

/**
 * Anzeige gesuchter/gefundener Studierender
 * fSuccess wird ausgeführt, falls _ein/e_
 * Studierende(r) gefunden wurde.
 */
function findStudents(fSuccess) {
    // Blende ggf. alte Tabelle und 
    // Detailseite 'Studierende' aus:
    student = null;
    clearSingleStudentDisplay();
    
    // Blende Info Newsticker aus
    $('.shj-information').fadeOut();

    if(typeof(g_signUp_Informationen_Polling_Interval) !== "undefined") clearInterval(g_signUp_Informationen_Polling_Interval);
    
    // Globale 'search' Variable ist
    // in Verarbeitung -- Zeitmechanismus
    // abschalten
    search = $('#txtSuche').val();

    var searchTmp = {};
    searchTmp.txtSuchen = $('#txtSuche').val();
    searchTmp.chkEhemalige = $('#chkEhemalige').prop('checked');

    shj.signUp.search.findStudierende(searchTmp,
            {"item": "student",
                "emptyFirst": true,
                "replace_rows": true,
                "HTML_template": "#template_studierende",
                "HTML_table_container": "#liste",
                "HTML_template_class_prefix": 'student_'},
    function(gefundene) {
        // Niemand gefunden, schaue ggf. auch 
        // unter den 'Ehemaligen'
        if (gefundene.length === 0) {
            if ($('#chkEhemalige').prop('checked') !== true) {
                $('#chkEhemalige').prop('checked', "true").change();
                return null;
            } else {
                alert("Leider ist unter '" + search + "' nichts zu finden.");
            }

            // Falls genau 1 Person gefunden wurde,
            // gleich Details anzeigen
        } else if (gefundene.length === 1) {
            showDetails(gefundene[0].student.matrikelnummer, gefundene[0].student,fSuccess);
        }
    }
    );

    // Zeige die Resultate an und lade Popup mit Details
    $('#results').fadeIn(function() {
        $('#results').find('.student_nachname, .student_vorname').popover({
            'delay': {
                'show': 800,
                'hide': 100
            },
            'placement': 'bottom',
            'trigger': 'hover',
            'title': function() {
                return  $(this).parents('tr').data('shj_item').anrede + ' ' + $(this).parents('tr').data('shj_item').vorname + ' <strong>' + $(this).parents('tr').data('shj_item').nachname + '</strong>';
            },
            'content': function() {
                return $(this).parents('tr').data('shj_item').strasse + '<br />' +
                        $(this).parents('tr').data('shj_item').plz + ' ' + $(this).parents('tr').data('shj_item').ort + '<br />' +
                        '<i class="icon-calendar"></i> ' + $(this).parents('tr').data('shj_item').telefon + '<br />' +
                        '<i class="icon-envelope"></i> ' + $(this).parents('tr').data('shj_item').email + '<br />--<br />' +
                        $(this).parents('tr').data('shj_item').fach + '<br />' +
                        $(this).parents('tr').data('shj_item').fachsemester + '. Semester';
            }
        });
    });

    // Blende Ergebnisliste ein.
    $('#liste').show();
}

/**
 * Zeige Detailinformationen zu diesem Studierenden 
 * und prüfe, ob es ein Foto online gibt.
 * 
 * @param {String} sMatrikelnummer wird 'same' übergeben, wird nur das "Detail"-Register eingeblendet
 * @param {object} studi
 * @returns {Boolean} true
 */
function showDetails(sMatrikelnummer, studi, fSuccess) {
    // Wenn bloß das Register
    // angeklickt wurde, muss 
    // nichts neu geladen werden.
    if (sMatrikelnummer == 'same') {
        setView($.signUpGlobal.iVIEW_STUDENT_DETAIL);
        return;
    }

    leistung = null;

    // Nützlich für Tastennavigation
    $('#txtSuche').blur();

    student = studi;
    student.leistungen = null;
    student.anmeldungen = null;
    student.bemerkungen = null;
    try {
        student.transkript.modul = null;
        student.transkript = null;
    } catch (eIgnore) {
    }
    student.fach = null;

    // Kopiere Modularisierungen etc.
    // von info.seminar.faecher
    student.fach=$.signUpGlobal.info.seminar.faecher.get(student.fach_id);
    if(student.fach !==null)
        student.fachname=student.fach.name;         // Für .renderDiv und die Anzeige

    // Zur Initialisierung alles ausblenden
    $('#studentInfo').hide();

    // =========================================================
    // Lade Überschriftenzeile
    // Lade die Vorlage und trage dort die
    // aktuellen Werte des/r Studierenden ein
    var headline = $('#template_subStudentHeadlines').clone();
    shj.signUp.toolbox.renderDiv(headline, student, 'tpl_studentWell_');

    // Fürs Foto wird mit der ID hantiert -- die 
    // darf nicht doppelt vorkommen, muss also von 
    // der des Templates korrigiert werden
    headline.find('#template_studentPicture').attr("id", "studentPhoto");
    headline.find('#template_picplace').attr("id", "picplace");
    
    headline.find('#tpl_studentFachnote').attr("id", "studentFachnote");

    // Ersetze das DOM-Teil
    $('#studentInfo').empty();
    $('#studentInfo').append(headline);

    // Blende eine Warnung ein, falls es 
    // sich um einen 'Altbestand' handelt
    if (studi.info == 'Altbestand')
        $('#studentInfo').find('.altbestand').show().addClass('badge badge-important');

    // Teste auf das Vorhandensein eines Fotos ...
    $.ajax({
        url: '../../studentPhoto/' + student.pid + '.jpg',
        type: 'HEAD',
        success: function() {
            // ... es GIBT EIN Foto, zeige es an, und setze
            // Link zum Vergrößern bei Klick:
            $('#studentPhoto').attr("src", function() {
                return "../../studentPhoto/" + student.pid + ".jpg";
            });
            $('#picplace').attr("href", function() {
                return "../../studentPhoto/" + student.pid + ".jpg";
            });
            $('#studentInfo').show();
        },
        error: function() {
            // ... es GIBT KEIN Foto, zeige stattdessen 
            // ein gendered-generic default
            $('#studentInfo').show();
            if (student.anrede == 'Herr')
                $('#studentPhoto').attr("src", "img/acspike_male_user_icon.png");
        }
    });

    // =========================================================
    // Lade 'Details' Tabe mit den 
    // Werten des Objekts 'student'
    var threeColBody = $('#template_studentDetails').clone();
    threeColBody.find('#template_studentDetails_BemerkungListeContainer').attr('id', 'studentDetails_BemerkungListeContainer');
    
    // GymPO ab 2022 löschen:
    threeColBody.find('#tpl_chkStudentStx').attr('id', 'chkStudentStx');
    threeColBody.find('#tpl_showAnmeldungStxDelete').attr('id', 'showAnmeldungStxDelete');
    threeColBody.find('#tpl_cboDelGymPOAnm_Year').attr('id', 'cboDelGymPOAnm_Year');
    threeColBody.find('#tpl_cboDelGymPOAnm_Month').attr('id', 'cboDelGymPOAnm_Month');
    
    shj.signUp.toolbox.renderDiv(threeColBody, student, 'template_studentDetails_');
    
    // Ersetze ggf. die alte Detailsansicht
    // durch die neuen Inhalte
    $('#details').empty();
    $('#details').append(threeColBody);

    // Mailto-Link bei der E-Mail und 
    // Link der Homepage beifeilen
    $('#details').find('.template_studentDetails_email').attr('href', function() {
        return 'mailto:' + $('#details').find(".template_studentDetails_email").text();
    });
    $('#details').find('.template_studentDetails_homepage').attr('href', $('#details').find(".template_studentDetails_homepage").text());

    // Jahreszahlen setzen
    var iiA = (new Date()).getFullYear();
    $('#cboDelGymPOAnm_Year')
            .find('option')
            .remove()
            .end()
            .append('<option value="' + iiA + '">' + iiA + '</option>' +
            '<option value="' + (iiA+1) + '">' + (iiA+1) + '</option>' + 
            '<option value="' + (iiA+2) + '">' + (iiA+2) + '</option>');
    
    // Checkbox GymPO (Löschen in 2022)
    if(student.anmeldung_gympo.length>3){
        $('#details').find('#chkStudentStx').attr('checked', true);
        $('#showAnmeldungStxDelete').show();
        var aDtm = student.anmeldung_gympo.split('-');
        $('#cboDelGymPOAnm_Year').val(aDtm[0]);
        $('#cboDelGymPOAnm_Month').val(aDtm[1]);
    }else{
        $('#showAnmeldungStxDelete').fadeOut();
        $('#details').find('#chkStudentStx').attr('checked', false);
    }
    
    $('#details').find('#chkStudentStx').off();
    $('#details').find('#chkStudentStx').on('click', function(){updateGymPOAnmeldung(true);});
    $('#cboDelGymPOAnm_Month').on('change', updateGymPOAnmeldung);
    $('#cboDelGymPOAnm_Year').on('change', updateGymPOAnmeldung);
    
    function updateGymPOAnmeldung(bSetInitialDate){
        var sending ={};
        sending.setStxGymPO = true;
        if($('#details').find('#chkStudentStx').attr('checked')){
            $('#showAnmeldungStxDelete').show();
            if(bSetInitialDate){
                var d = (new Date()).getMonth();
                // September für alle Monate vor Juni
                if(d < 6) $('#cboDelGymPOAnm_Month').val(9);
                else{
                    $('#cboDelGymPOAnm_Month').val(3);
                    $('#cboDelGymPOAnm_Year').val((new Date()).getFullYear()+1);
                }
            }
            sending.chkStudentStx = true;
            sending.txtStxDelete = $('#cboDelGymPOAnm_Year').val() + '-' + $('#cboDelGymPOAnm_Month option:selected').val() + '-30';
        }else{
            $('#showAnmeldungStxDelete').hide();
        }
        $.ajax({
            data: sending,
            url: 'json/student/update.jsp',
            success: function(data) {
                if (data.success == 'true'){
                    if(data.state=='checked'){
                        $('#details').find('#chkStudentStx').attr('checked', true);
                    }else{
                        $('#details').find('#chkStudentStx').attr('checked', false);
                    }
                    $('.template_studentDetails_stx').addClass('label-success').delay(500).queue(function(next){
                        $('.template_studentDetails_stx').removeClass('label-success');
                        next();
                    });
                }else
                    alert('Fehler :-(');
            },
            error: function(data) {
                alert('Fehler :-(');
            }
        });
    }

    $.signUpGlobal.matrikelnummer = sMatrikelnummer;
    if($.signUpGlobal.access_level>=500){
        showBemerkungen();
        $('a.zuv_heidelberg_ext').attr('href','http://adb.zuv.uni-heidelberg.de:8888/sach/INFO_FDB$.startup?MODUL=STUD&M1=2&M2=10&M3=0&PRO=&MODUS=SUCHERGEBNISS&TEXT=' + sMatrikelnummer);
    }
    
    setView($.signUpGlobal.iVIEW_STUDENT_DETAIL);
    
    if(typeof fSuccess==='function') fSuccess();
    
    return true;
}

/**
 * Login als gewählte(r) Studierende
 */
function login() {
    var login_url = 'util/delegate-login.jsp?matrikelnummer=' + $.signUpGlobal.matrikelnummer;
    window.open(login_url, '_blank');
}

// =============================================================================================
// Register 'Leistungen' (/Anmeldungen): Tabellarisch
// =============================================================================================
// 
// ##################################################

/**
 * Regelt die Anzeige je nach aktivem Register
 * 
 * $.signUpGlobal.iVIEW_STUDENT_DETAIL
 * (Detailansicht mit Kontaktdaten)
 * 
 * $.signUpGlobal.iVIEW_STUDENT_LEISTUNGEN
 * (Liste der absolvierten Leistungen)
 * 
 * $.signUpGlobal.iVIEW_STUDENT_ANMELDUNGEN
 * (Liste der aktuellen Anmeldungen)
 * 
 * $.signUpGlobal.iVIEW_STUDENT_TRANSKRIPT
 * (Liste der Module und Leistungen mit Änderungsmöglichkeiten)
 * 
 * $.signUpGlobal.iVIEW_STUDENT_PRUEFUNGEN
 * (Liste der Prüfungen)
 * 
 * @param {type} iVIEW
 */
function setView(iVIEW) {
    $('.signUp_Student_Register').removeClass('active');

    // Aus unklaren Gründen (Reihenfolge und Geschwindigkeit
    // der Ausführung?) bleiben die Register aktiviert, 
    // also hier noch manuell:
    $('#studentPruefungen').removeClass('active');
    $('#studentTranskript').removeClass('active');
    $('#studentLeistungen').removeClass('active');
    $('#studentAnmeldungen').removeClass('active');

    $('.signUp_viewToggle').hide();

    switch (iVIEW) {
        // ====================================================================
        // Detailansicht (Kontakt, Studiengang etc.)
        case $.signUpGlobal.iVIEW_STUDENT_DETAIL:
            leistung = null;
            $('#singleStudent').fadeIn();
            $('.shj-information').fadeOut();
            if(typeof(g_signUp_Informationen_Polling_Interval) !== "undefined") clearInterval(g_signUp_Informationen_Polling_Interval);
            $('#studentDetails').addClass('active');	   // Register
            $('#details').show();
            break;

            // ====================================================================
            // Tabelle mit Leistungen
        case $.signUpGlobal.iVIEW_STUDENT_LEISTUNGEN:
            leistung = null;
            $('#singleStudent').fadeIn();				   	// Blende Register ein.
            $('.shj-information').fadeOut();
            if(typeof(g_signUp_Informationen_Polling_Interval) !== "undefined") clearInterval(g_signUp_Informationen_Polling_Interval);
            $('#divLeistungen').fadeIn();
            $('#studentLeistungen').addClass('active'); 	// Setze ...
            if(!$('#divLeistungen tr').length){
                // Falls die Tabelle gelöscht wurde, neu aufbauen:
                _renderLeistungenTabelle(student.leistungen);
            }
            break;

            // ====================================================================
            // Tabelle mit Anmeldungen
        case $.signUpGlobal.iVIEW_STUDENT_ANMELDUNGEN:
            leistung = null;
            $('#singleStudent').fadeIn();	
            $('.shj-information').fadeOut();// Blende Register ein.
            if(typeof(g_signUp_Informationen_Polling_Interval) !== "undefined") clearInterval(g_signUp_Informationen_Polling_Interval);
            $('#studentAnmeldungen').addClass('active');
            $('#divAnmeldungen').fadeIn();
            break;

        case $.signUpGlobal.iVIEW_STUDENT_TRANSKRIPT:
            $('.shj-information').fadeOut();
            if(typeof(g_signUp_Informationen_Polling_Interval) !== "undefined") clearInterval(g_signUp_Informationen_Polling_Interval);
            $('#singleStudent').fadeIn();				   	// Blende Register ein.
            $('#studentTranskript').addClass('active');
            $('#transkript').fadeIn();
            break;

        case $.signUpGlobal.iVIEW_STUDENT_PRUEFUNGEN:
            $('.shj-information').fadeOut();
            if(typeof(g_signUp_Informationen_Polling_Interval) !== "undefined") clearInterval(g_signUp_Informationen_Polling_Interval);
            $('#singleStudent').fadeIn();			   	// Blende Register ein.
            $('#divPruefungen').fadeIn();
            $('#studentPruefungen').addClass('active');
            break;
    }
}

// ========================================================================================
// Langweiliger Kram
// ========================================================================================



/**
 * Füllt #cboKurssucheSemester mit sinnvollen Werten, 
 * also Semestern ab Sommer 2007 bis zum aktuellen Semester.
 */
function fillSemesterCboForKurssuche() {
    if ($('#cboKurssucheSemester').length <= 1) {
        var dtm = new Date();
        var iJahr = dtm.getFullYear();
        var iMon = dtm.getMonth() + 1;
        for (var ii = 2007; ii <= iJahr; ii++) {
            $('<option/>').val('ss' + ii).text('Sommersemester ' + ii).appendTo('#cboKurssucheSemester');
            $('<option/>').val('ws' + ii + '/' + (ii + 1)).text('Wintersemester ' + ii + '/' + (ii + 1)).appendTo('#cboKurssucheSemester');
        }
    }
}

//
/**
 * Umsortierung:
 * 
 * Lade Liste mit Leistungen des Seminars
 * in $.signUpGlobal.info.seminar.leistungen, 
 * falls noch nicht vorhanden.
 * 
 * Das Leistungs-Typeahead wird auch gleich initialisiert.
 * 
 * Die Leistungen wurden mit dem 
 * Fach geladen und sind nach 
 * Modulen sortiert:
 * Fach ¹ -- n Module ¹ -- n Leistungen
 * 
 * Das ist in späteren Loops hier und da ungünstig,
 * deshalb die Umsortierung
 */
function initSeminarLeistungen() {
    if (typeof $.signUpGlobal.info.seminar.leistungen === 'undefined') {
        $.signUpGlobal.info.seminar.leistungen = $.signUpGlobal.info.seminar.faecher.getLeistungen();
        var lst=[];
        for(var ii=0; ii<$.signUpGlobal.info.seminar.leistungen.length; ii++){
            lst.push($.signUpGlobal.info.seminar.leistungen[ii].name);
        }
        $('#leistung_name').typeahead({'source': lst, 'items': 14});
    }
}

//
/**
 * Füllt die Combo-Box für Module mit den
 * zur Leistung lLeistungID wählbaren Modulen.
 * lModulID gibt ggf. das derzeit ausgewählte
 * Modul an.
 * @param {long} lLeistungID Id der gewählten Leistung
 * @param {long} lModulID
 */
function initModule(lLeistungID, lModulID) {
    // Setze Combo zurück
    $('#cboModul').empty();

    // Durchlaufe alle Module des 
    // einschlägigen Studiengangs und 
    // wähle diejenigen Module aus, die die 
    // Leistung lLeistungID enthalten.
    for (var ii = 0; ii < student.fach.module.length; ii++) {
        for (var jj = 0; jj < student.fach.module[ii].modul.leistungen.length; jj++) {
            if (lLeistungID === student.fach.module[ii].modul.leistungen[jj].leistung.id) {
                $('<option/>').val(student.fach.module[ii].modul.id).text(student.fach.module[ii].modul.name).appendTo('#cboModul')
            }
        }
    }
    $("<option/>").val('null').text('Zusatzmodul').appendTo("#cboModul");
    if (lModulID == 0 || lModulID == 'null')
        $('#cboModul option[value="null"]').attr('selected', 'true');
    else
        $('#cboModul option[value="' + lModulID + '"]').attr('selected', 'true');
}

/**
 * Gibt die default Leistungspunkte der angezeigten Leistung aus.
 * @param {long} lLeistungID
 * @returns {undefined}
 * @todo es sollte die Default-Leistungspunkte pro Modul/Leistung ausgeben.
 */
function initCreditPts(lLeistungID) {
    initSeminarLeistungen();
    //@TODO
    // 1. nicht sicher, ob das funktioniert,
    // 2. die CP sollten pro Fach/Modul/Leistung vorgeschlagen werden.
    for (var ii = 0; ii < $.signUpGlobal.info.seminar.leistungen.length; ii++) {
        if ($.signUpGlobal.info.seminar.leistungen[ii].id === lLeistungID) {
            if ($('#leistung_ects').val() == '' || $('#leistung_ects').val() <= 0)
                $('#leistung_ects').val($.signUpGlobal.info.seminar.leistungen[ii].cp);
            break;
        }
    }
}

/**
 * Ein- und Ausblenden des Inputs zur Eingabe eines Hausarbeitstitels
 */
function controlVisibilityHA() {
    if ($('#leistung_leistungstyp').val() == 'HA')
        $('#leistung_details').show();
    else
        $('#leistung_details').hide();
}

/**
 * Löscht die Anzeige einer Studierendenakte
 */
function clearSingleStudentDisplay() {
    $('divLeistungen tbody').find('tr').remove();
    $('divAnmeldungen tbody').find('tr').remove();
    $('divPruefungen tbody').find('tr').remove();
    $('#results').hide();
    $('#singleStudent').hide();
    $('#cgShowInactive').hide();
    $('#studentInfo').html('');
    $('#details').html('');
    $('#studentInfo').hide();
    leistung = null;
}

/**
 * Wandelt die Anzeige des Kurstitels um 
 * in ein Textfeld mit dem onChange 
 * Event transcriptEdit
 * @see transcriptEdit
 * @param {DOMobject} oHTMLSpan
 */
function showEditMode(oHTMLSpan) {
    if(oHTMLSpan.find('input').length > 0) return;
    var tmp = oHTMLSpan.text();
    oHTMLSpan.html('<input class="span5" type="text" value="" onchange="transcriptEdit($(this).parents(&quot;tr&quot;).data(&quot;shj_item&quot;), $(this).val())"></input>').removeClass('editable');
    oHTMLSpan.find('input').val(tmp).focus();
}

/**
 * Wandelt die Anzeige des englischen Kurstitels um 
 * in ein Textfeld mit dem onChange 
 * Event transcriptEdit
 * @see transcriptEdit
 * @param {DOMobject} oHTMLSpan
 */
function showEditMode_en(oHTMLSpan) {
    if(oHTMLSpan.find('input').length > 0) return;
    var tmp = oHTMLSpan.text();
    oHTMLSpan.html('<input class="span5" type="text" value="" onchange="transcriptEdit($(this).parents(&quot;tr&quot;).data(&quot;shj_item&quot;), $(this).val(), true)"></input>').removeClass('editable');
    oHTMLSpan.find('input').val(tmp).focus();
}

/**
 * Wandelt die Anzeige der Leistungspunkte
 * in ein Textfeld mit dem onChange 
 * Event transcriptEditLP
 * @see transcriptEditLP
 * @param {DOMobject} oHTMLSpan
 */
function showEditModeLP(oHTMLSpan) {
    if(oHTMLSpan.find('input').length > 0) return;
    var tmp = oHTMLSpan.text();
    oHTMLSpan.html('<input class="span1" type="text" value="" onchange="transcriptEditLP($(this).parents(&quot;tr&quot;).data(&quot;shj_item&quot;), $(this).val())"></input>').removeClass('editable');
    oHTMLSpan.find('input').val(tmp).focus();

}
        