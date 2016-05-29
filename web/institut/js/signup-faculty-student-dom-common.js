/* 
 * Funktionen, die zur DOM-Manipulation
 * sowohl von Studierenden als auch 
 * Lehrenden-Komponenden genutzt werden.
 */
$(document).ready(function() {

/**
 * Rendert die Tabelle der Leistungen und initialisiert 
 * ein zeilenweises Popover mit Details.
 * @param {shj.signUp.student.Leistungen} item
 */
window._renderLeistungenTabelle=function(item) {
    // Falls der TableSorter aktiv ist,
    // muss die Überschriftenzeile ersetzt werden:
    if($('.tableSorter').length){
        // Falls noch nicht initialisiert, ist es der 
        // erste Aufruf: Zwischenspeichern der 
        // (von tableSort noch unberührten) 
        // Überschriftenzeile
        if(typeof $.signUpGlobal.tableSorterHeadLeistung === 'undefined' || $.signUpGlobal.tableSorterHeadLeistung===''){
            $.signUpGlobal.tableSorterHeadLeistung=$('#divLeistungen').html();
        }
        
        // Ersetze Überschriftenzeile
        $('#divLeistungen').find('*').remove();
        $('#divLeistungen').html($.signUpGlobal.tableSorterHeadLeistung);
        
    }
    item.leistungen.shj_rendering = {'item': item.leistungen,
        'emptyFirst': true,
        'HTML_template': '#template_leistung',
        'HTML_table_container': '#divLeistungen',
        'HTML_template_class_prefix': 'leistung_'};

    
    shj.signUp.toolbox.render(item.leistungen);
    
    // Anerkannte Leistungen werden
    // mit einem Asterisk markiert
    $('#divLeistungen').find('.leistung_note').each(function(){
        if($(this).parents('tr').data('shj_item').anerkannt=='t')
            $(this).append(' <span title="* = anerkannt" style="color:red; font-size:1.5em">*</span>');
    }
    );
    
    // Jede Tabellenzeile erhält ein Pop-Up-Fenster
    // mit Zusatzinformationen zum Aussteller, 
    // Kurstitel und Modul
    $('#divLeistungen').find('.leistung_name').popover({
        'delay': {
            'show': 800,
            'hide': 100
        },
        'placement': 'bottom',
        'trigger': 'hover',
        'title': function() {
            return  '<strong>' + $(this).parents('tr').data('shj_item').name;
        },
        'content': function() {
            return  '<strong>' + $(this).parents('tr').data('shj_item').kurstitel +
                    '</strong><br />Aussteller: ' + $(this).parents('tr').data('shj_item').aussteller_vorname + ' ' + $(this).parents('tr').data('shj_item').aussteller_nachname +
                    '<br />Modul: ' + getModulBezeichnung($(this).parents('tr').data('shj_item').modul);
        }
    });
    
    // Das Table-Sorter Plugin: hübsch, 
    // aber degradiert es gracefully?
    if($('.tableSorter :visible').length){
        // Datumsspalte
        var ii=0;
        $('table.tableSorter tr').each(function(){
            $(this).find('.shjDataCol').attr('data-sortAs', ii++);
            //alert('setting ' + $(this).find('.shjDataCol').text + ' attrib to ' + ii);
        });
        $('table.tableSorter').tableSort();
    }
    
};
/**
 * Anzeige der Leistungen im HTML, ggf. vorheriges Laden
 * @param {function} fSuccess wird nur ausgeführt, falls zuvor die Leistungen erfolgreich vom Server geladen wurden
 * @returns {Boolean}
 */
window.showLeistungen = function(fSuccess) {
    if (student.leistungen != null && typeof student.leistungen != 'undefined') {

        // Die Leistunden sind bereits geladen, 
        // zeige das entsprechende Register
        if (student.matrikelnummer == student.leistungen.matrikelnummer) {
            
            // testen, ob Anzeige leer?
            // alert('Leistungen gelten schon als geladen ...');
            if(document.getElementById('tLeistungen').getElementsByTagName('tbody')[0].getElementsByTagName('tr').length===0){
                setView($.signUpGlobal.iVIEW_STUDENT_LEISTUNGEN);
                _renderLeistungenTabelle(student.leistungen, _renderLeistungenTabelle(student.leistungen));
            }
            setView($.signUpGlobal.iVIEW_STUDENT_LEISTUNGEN);
            return true;
        }
    }

    // Lade Leistungen vom Server und
    // forme sie zu HTML
    student.leistungen = new shj.signUp.student.Leistungen(student.matrikelnummer,
            function(item) {
                // Stelle die Leistungen in der Tabelle dar:
                _renderLeistungenTabelle(item);
                if (typeof fSuccess == 'function')
                    fSuccess();
                
            });

    setView($.signUpGlobal.iVIEW_STUDENT_LEISTUNGEN);
}
;  

/**
 * Bezeichnung des Moduls mit Id lModulID
 * @param {long} lModulID
 * @returns {String} Bezeichnung des Moduls
 */
window.getModulBezeichnung=function(lModulID) {
    var sReturn = 'unbekannt';
    for (var ii = 0; ii < student.fach.module.length; ii++) {
        if (student.fach.module[ii].modul.id == lModulID) {
            sReturn = student.fach.module[ii].modul.name;
            break;
        }
    }
    return sReturn;
}


/**
 * Stellt die Anmeldungen tabellarisch dar und 
 * initialisiert ein Popover mit Detailinformationen
 * @param {shj.signUp.student.Anmeldungen} item
 */
window._renderAnmeldungenTabelle=function(item) {
    // Stelle die Leistungen in der Tabelle dar:
    shj.signUp.toolbox.render(item.anmeldungen,
            {'item': item.anmeldungen,
                'emptyFirst': true,
                'HTML_template': '#template_anmeldung',
                'HTML_table_container': '#divAnmeldungen',
                'HTML_template_class_prefix': 'anmeldung_'});

    // Leistungen außerhalb des relevanten
    // Zeitraums können nicht gelöscht werden:
    // Disable also die Buttons und gebe einen 
    // Hinweis per 'title'-Attribut.
    var dd = new Date();
    $('#divAnmeldungen').find('.anmeldungDelete a').each(function(){
        var fristende=shj.signUp.toolbox.getDateFromGerman($(this).parents('tr').data('shj_item').fristende);
        if(fristende<dd) $(this).addClass("disabled").attr('onclick','').attr('title','An- und Abmeldefrist abgelaufen');
    }
    );
        
    // Jede Tabellenzeile erhält ein Pop-Up-Fenster
    // mit Zusatzinformationen zum Aussteller, 
    // Kurstitel und Modul
    $('#divAnmeldungen').find('.anmeldung_name').popover({
        'delay': {
            'show': 800,
            'hide': 100
        },
        'placement': 'bottom',
        'trigger': 'hover',
        'title': function() {
            return  '<strong>' + $(this).parents('tr').data('shj_item').name;
        },
        'content': function() {
            return  '<strong>' + $(this).parents('tr').data('shj_item').kurstitel +
                    '</strong><br />Aussteller: ' + $(this).parents('tr').data('shj_item').aussteller_vorname + ' ' + $(this).parents('tr').data('shj_item').aussteller_nachname +
                    '<br />Modul: ' + getModulBezeichnung($(this).parents('tr').data('shj_item').modul);
        }
    });
}

/**
 * Zeigt die Prüfungen in einer Tabelle an.
 * @param {shj.signUp.student.Pruefungen} item
 */
window._renderPruefungenTabelle=function(item) {
    item.pruefungen.shj_rendering = {'item': item.pruefungen,
        'emptyFirst': true,
        'HTML_template': '#template_pruefung',
        'HTML_table_container': '#divPruefungen',
        'HTML_template_class_prefix': 'pruefung_'};

    shj.signUp.toolbox.render(item.pruefungen);
}

/**
 * Anzeigen der Prüfungen (mit ggf. vorherigen Laden).
 * @param {type} fSuccess wird nur ausgeführt, falls die Prüfungen erfolgreich vom Server geladen wurden
 * @returns {Boolean}
 */
window.showPruefungen=function(fSuccess) {
    if (student.pruefungen != null && typeof student.pruefungen != 'undefined') {

        // Die Leistunden sind bereits geladen, 
        // zeige das entsprechende Register
        if (student.matrikelnummer == student.pruefungen.matrikelnummer) {
            setView($.signUpGlobal.iVIEW_STUDENT_PRUEFUNGEN);
            return true;
        }
    }

    // Lade Leistungen vom Server und
    // forme sie zu HTML
    student.pruefungen = new shj.signUp.student.Pruefungen(student.matrikelnummer,
            function(item) {
                // Stelle die Leistungen in der Tabelle dar:
                _renderPruefungenTabelle(item);
                if (typeof fSuccess == 'function')
                    fSuccess();
            });

    setView($.signUpGlobal.iVIEW_STUDENT_PRUEFUNGEN);
}
;

/**
 * Zeigt die Anmeldungen an
 * @param {function} fSuccess wird nur ausgeführt, wenn die Anmeldungen neu vom Server heruntergeladen werden.
 * @returns {Boolean}
 */
window.showAnmeldungen=function(fSuccess) {
    if (student.anmeldungen != null && typeof student.anmeldungen != 'undefined') {
        if (student.matrikelnummer == student.anmeldungen.matrikelnummer) {
            setView($.signUpGlobal.iVIEW_STUDENT_ANMELDUNGEN);
            return true;
        }
    }

    // Lade Leistungen vom Server und
    // forme sie zu HTML
    student.anmeldungen = new shj.signUp.student.Anmeldungen(student.matrikelnummer,
            function(item) {
                _renderAnmeldungenTabelle(item);
                if (typeof fSuccess == 'function')
                    fSuccess();
            });

    setView($.signUpGlobal.iVIEW_STUDENT_ANMELDUNGEN);
}
;

/**
 * Löscht die Anmeldung (von Server und Anzeige).
 * Mit vorheriger Rückfrage.
 * 
 * @param {shj.signUp.student.Anmeldung} anmeldung_in
 * @returns {undefined}
 */
window.deleteAnmeldung=function(anmeldung_in) {
    if (!anmeldung_in instanceof shj.signUp.student.Anmeldung) {
        throw ('Fehler im Programm. Es wurde keine Anmeldung zum Löschen übergeben.');
    }

    shj.signUp.toolbox.confirmDelete(
            '#modShj_SignUp_ConfirmDelete',
            'Anmeldung löschen?',
            'Soll diese Anmeldung<br /><br /><b>' +
            anmeldung_in.name + '</b> (' + anmeldung_in.aussteller_nachname + ')' +
            '<br /><br />wirklich unwiderruflich gelöscht werden?',
            function() {
                student.anmeldungen.drop(
                        anmeldung_in,
                        function() {
                            _renderAnmeldungenTabelle(student.anmeldungen);
                        },
                        function() {
                            alert('Fehler -- sorry. Die Anmeldung konnte nicht gelöscht werden!')
                        });
            }
    );
};
/**
 * Ist Leistung lLeistungID Teil des Moduls lModulID?
 * @param {long} lModulID die Id des Moduls
 * @param {long} lLeistungID die Id der Leistung
 * @returns {Boolean}
 */
window.modulContainsLeistung=function(lModulID, lLeistungID) {
    var bReturn = false;
    // Iteriere die Modules und Leistungen des Studierenden
    for (var ii = 0; ii < student.fach.module.length; ii++) {
        for (var jj = 0; jj < student.fach.module[ii].modul.leistungen.length; jj++) {
            if (student.fach.module[ii].modul.id === lModulID) {
                if (lLeistungID === student.fach.module[ii].modul.leistungen[jj].leistung.id) {
                    bReturn = true;
                    break;
                }
            }
        }
    }
    return bReturn;
}

/**
 * Fügt eine Anmeldung zu einem Kurs hinzu.
 * 
 * @param {object} kurs
 * @param {long} lModulID
 * @returns {Boolean}
 */
window.addAnmeldung=function(kurs, lModulID) {

    // ------------------------------------------------------
    // Schnittstelle
    // Falls die Anmeldungen noch nicht initialisiert sind,
    // wird das zuerst erledigt:
    if (student.anmeldungen == null || typeof student.anmeldungen == 'undefined') {
        showAnmeldungen(function() {
            addAnmeldung(kurs, lModulID);
        });
        return true;
    }

    var anmeldung_tmp = {};
    anmeldung_tmp.kurs_id = kurs.id;
    anmeldung_tmp.kurstyp_id = kurs.kurstyp_id;
    anmeldung_tmp.matrikelnummer = student.matrikelnummer;
    $.extend(anmeldung_tmp, kurs);
    anmeldung_tmp.kurstitel = kurs.titel;
    anmeldung_tmp.kurstitel_en = kurs.titel_en;
    anmeldung_tmp.id = kurs.leistung_id;
    anmeldung_tmp.aussteller_vorname = kurs.lehrende.substr(0, kurs.lehrende.indexOf(' '));
    anmeldung_tmp.aussteller_nachname = kurs.lehrende.substring(kurs.lehrende.indexOf(' ') + 1);
    anmeldung_tmp.name = kurs.leistung;
    anmeldung_tmp.ects = kurs.ects;
    anmeldung_tmp.fristende = kurs.fristende;
    var heute = new Date();
    anmeldung_tmp.datum = heute.getDate() + '.' + (heute.getMonth() + 1) + '.' + heute.getFullYear();
    if (lModulID > 0) {
        anmeldung_tmp.modul_id = lModulID;
        anmeldung_tmp.modul = lModulID;
    }

    student.anmeldungen.add(student.matrikelnummer,
            anmeldung_tmp,
            function(data) {
                _renderAnmeldungenTabelle(student.anmeldungen);
                $('#modKurssuche').modal('hide');
            }
    );
}

/**
 * Liefert ein Array der absolvierten 
 * Leistungen in diesem Modul (mit dieser ID)
 * (I.d.R. wird das keine oder eine Leistung
 * sein. Bei generischen Leistungen (ÜK und ähnliche) 
 * können es aber auch n Leistungen sein).
 * @param {shj.signUp.seminar.Modul} modul_in liefert die Id des Moduls
 * @param {shj.signUp.seminar.Leistung} leistung_in liefert die Id der Leistung
 * @returns {Array} Liste der absolvierten Leistungen mit der gefragten Id im gefragten Modul
 */
window.getAbsolvierte=function(modul_in, leistung_in,debugA,debugB) {
    var array = [];

    // Durchlaufe alle absolvierten Leistungen des Studierenden
    for (var ii = 0; ii < student.leistungen.leistungen.length; ii++) {
        // Wenn es sich um eine Leistung des 
        // angefragten Typs im angefragten Modul
        // handelt, wird sie ins Array aufgenommen.
        if (student.leistungen.leistungen[ii].id == leistung_in.id && student.leistungen.leistungen[ii].modul == modul_in.id) {
            array.push(student.leistungen.leistungen[ii]);
        }
    }
    return array;
}
});