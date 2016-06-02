/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



    // TODO
    // ============================================================
    // 1. ANMELDUNGEN TESTEN UND GERADEBIEGEN
    // seems OK
//	showKursAsLeistungToAdd
//	Muss ggf. nach dem Modul fragen
//	
    //
    // 4. Leistungen
    // -- Server wirft Fehlermeldung bei Leistungsanzeige durch Studierende
    // -- Rück-Navigation auf "Daten" schlägt noch fehl. Warum?
$(document).ready(function() {
//alert('I am actually doing this');
        // Das stellt die Ajax Wartemitteilung an.
        $("body").on({
            ajaxStart: function() { 
                $(this).addClass("loading"); 
            },
            ajaxStop: function() { 
                $(this).removeClass("loading"); 
            }    
        });
        $('#cmdAntragProblem').on('click', function(){
           $('#modAntragProblem').modal('show');
        });
        $('#cmdModAntragProblemClose').on('click', function(){
            $('#modAntragProblem').modal('hide');
        });
        $.signUpGlobal.matrikelnummer=$('#logged-in-user-matrikelnummer').val();
        $.signUpGlobal.seminar_id=$('#logged-in-user-seminar').val();
        student={};
        $.signUpGlobal.info={};
        $.signUpGlobal.info.seminar = {};

        $('#chkDatenKorrekt').on('click', function(){
            if(!$('#chkDatenKorrekt').prop('checked')){
                $('#divSprachwahl').fadeOut();
                $('#chkTranskript_de').prop('checked', false);
                $('#chkTranskript_en').prop('checked', false);
                $('#cmdApplyForTranscript').addClass('disabled');
            }else{
                $('#divSprachwahl').fadeIn();
            }
        });
       
        window.initSignUp=function(){
                /**
                * Lade die Studiengänge des Seminars mit 
                * den Modularisierungen vom Server
                * @see json/seminar/fach/get.jsp
                * @see shj.signUp.seminar.Faecher
                */ 
               $.signUpGlobal.info.seminar.faecher = new shj.signUp.seminar.Faecher($.signUpGlobal.seminar_id, function(){showData(true);});
              /**
              * Lade Noten dieses Seminars vom Server herunter,
              * um in sFAIL eine Liste der nicht-bestandenen Leistungen 
              * zu definieren
              * @see isNoteBestanden (this file)
              * @see shj.signUp.seminar.Noten
              * @see json/seminar/note/get.jsp
              */
             sFAIL='';
             new shj.signUp.seminar.Noten(
                     $.signUpGlobal.seminar_id, function(data) {
                 for (var ii = 0; ii < data.noten.length; ii++) {
                     if(data.noten[ii].bestanden !=='true'){
                         sFAIL += ';' + data.noten[ii].wert + ';';
                     }
                 }
             });
        };
    
    
    $('#navLeistungen').on("click", showLeistungen);
    $('#navAnmeldungen').on("click", showAnmeldungen);
    $('#navAntrag').on("click", function(){
        showAntraege();
        setView($.signUpGlobal.iVIEW_STUDENT_ANTRAG);
    });
    $('#cmdNeuesTranskript').on("click", function(){
        showTranscript('simple');
        setView($.signUpGlobal.iVIEW_STUDENT_ANTRAG_TRANSKRIPT_1);
    });

    // @GymPOMissing to do
    $('#cmdNeuesGymPOMissingCredits').on("click", function(){
//        if(confirm()){
addAntrag($.signUpGlobal.iTYP_ANTRAG_GYMPO_MISSING_CREDITS, 'Antrag auf Ausstellen der Bescheinigung zu fehlenden Leistungen fürs Prüfungamt zur Anmeldung zum Staatsexamen (GymPO).');

//            putApplicationGymPOMissing();
//        }
    });
    
    $('#cmdApplyForTranscript').on('click',function(){
        if($('#cmdApplyForTranscript').hasClass('disabled')) return;
        if($('#chkTranskript_de').prop('checked')) addAntrag($.signUpGlobal.iTYP_ANTRAG_TRANSKRIPT_DE, 'Antrag auf Ausstellen eines (deutschen) Transkripts.');
        if($('#chkTranskript_en').prop('checked')) addAntrag($.signUpGlobal.iTYP_ANTRAG_TRANSKRIPT_EN, 'Antrag auf Ausstellen eines (englischen) Transkripts.');
        if($('#chkTranskript_unmodular').prop('checked')) addAntrag($.signUpGlobal.iTYP_ANTRAG_TRANSKRIPT_UNMODULAR, 'Antrag auf Ausstellen eines Transkripts.');
        if(!($('#chkTranskript_de').prop('checked') || $('#chkTranskript_en').prop('checked') || $('#chkTranskript_unmodular').prop('checked'))) alert('Bitte wählen Sie aus, ob Sie ein deutsches oder englisches Transkript beantragen möchten.');
    });
    $('#cmdDeletePic').on("click", function(){ dropImage();});
    $('#cmdAnmeldungAdd').on("click", showAnmeldungPicker);
    $('#navPruefungen').on("click", function(){
            showPruefungen();
            setView($.signUpGlobal.iVIEW_STUDENT_PRUEFUNGEN);
    });
    $('#navStudienbilanz').on("click", function(){
            //showStudienbilanz();
            showTranscript();
            setView($.signUpGlobal.iVIEW_STUDENT_STUDIENBILANZ);
    });
    $('#navTranskriptdruck').on("click", function(){
            //showStudienbilanz();
            showDokumente();
            setView($.signUpGlobal.iVIEW_STUDENT_TRANSKRIPTDRUCK);
    });
    $('#cmdUpdatePic').on("click", function(){
            //$('#modFotoUpload').modal('show');
            $('#modIMGUpload').modal('show');
    });
    $('form').bind('submit', function(event) {  // Allg. Formulare abschalten
        event.preventDefault();                 
    });
    // Initialisiere Logout-Dropdown  
    // in der Navi oben rechts
    $('.dropdown-toggle').dropdown();
//        $('.collapse').collapse();
    $('#navDaten').on("click", function(){showData(false);});
    $('#cmdConfirmDeleteAnmeldung').on("click",function(){deleteAnmeldung(true);});
    $('#cmdCancelDeleteAnmeldung').on("click",function(){deleteAnmeldung(false);});
    $('#fKurssuche').submit(function(event){event.preventDefault();showKurssucheErgebnis();});
    $('#cmdPrintTranskript').on('click', addNewTranscript);
    $('#cmdPrintTranskript_en').on('click', function(){addNewTranscript(true);});
}); //end of document ready

/**
 * Zeigt an, welche Transkripte schon gespeichert sind, 
 * und bietet die Möglichkeit, 
 * - Dokumente anzusehen,
 * - ... als PDF zu laden,
 * - ... zu löschen
 * @returns {undefined}
 */
function showDokumente(){
    if(typeof student.dokumente==='undefined' || student.dokumente===null){
        student.dokumente = new shj.signUp.student.Dokumente(student.matrikelnummer,
                function(item) {
                    // Stelle die Dokumente in der Tabelle dar:
                    _renderDokumenteTbl();
                });
         return;
    }
}

function _renderDokumenteTbl(){
            // zwischenspeichern des Templates aus Performancegründen
    student.dokumente.transkripte.shj_rendering = {'item': student.dokumente.transkripte,
        'emptyFirst': true,
        'HTML_template': '#template_transkriptdruck',
        'HTML_table_container': '#divTranskriptdruck',
        'HTML_template_class_prefix': 'dokument_'};

    shj.signUp.toolbox.render(student.dokumente.transkripte);
    
}

function showDokument(oDok){
    $('#formShowDokument').remove();
    var form=$('<form target="_blank" id="formShowDokument" method="post" action="./dokument.jsp">' +
            '<input type="hidden" name="matrikelnummer" value="' + student.matrikelnummer + '"/>' + 
            '<input type="hidden" name="datum" value="' + shj.signUp.toolbox.getGermanDate(oDok.datum) + '" />' +
            '<input type="hidden" name="verifikation" value="' + oDok.verifikationscode + '"/>' + 
            '</form>');
    $("body").append(form);
    form.submit();
    $('#formShowDokument').remove();
    
}
function printDokument(oDok){
    $('#formShowDokumentPDF').remove();
    var form=$('<form target="_blank" method="post" action="./print/transcript-selfprint_relay.jsp">' +
            '<input type="hidden" name="matrikelnummer" value="' + student.matrikelnummer + '"/>' + 
            '<input type="hidden" name="datum" value="' + shj.signUp.toolbox.getGermanDate(oDok.datum) + '" />' +
            '<input type="hidden" name="verifikation" value="' + oDok.verifikationscode + '"/>' + 
            '</form>');
    $("body").append(form);
    form.submit();
    $('#formShowDokumentPDF').remove();
}
function deleteDokument(oDok){
    shj.signUp.toolbox.confirmDelete(
            '#modShj_SignUp_ConfirmDelete',
            'Transkript löschen?',
            'Soll dieses Transkript<br /><br /><b>' +
            oDok.dok_name + '</b> (vom ' + shj.signUp.toolbox.getGermanDate(oDok.datum) + ')' +
            '<br /><br />wirklich unwiderruflich gelöscht werden?<br />' +
            '(<b>Achtung, es kann dann auch nicht mehr verifiziert werden</b>)',
            function() {
                student.dokumente.drop(
                        oDok,
                        function() {
                            _renderDokumenteTbl();
                        },
                        function() {
                            alert('Fehler -- sorry. Das Transkript konnte nicht gelöscht werden!');
                        });
            }
    );
}

function addNewTranscript(bEnglisch){
    if(student.dokumente.transkripte.length >= 6){
        alert("Sie können nur höchstens sechs Transkripte gleichzeitig vorhalten.\n\n" + 
                "Löschen Sie bitte zunächst ein älteres Transkript");
        return;
    }
    var isEnglish= ((bEnglisch && bEnglisch===true) ? 'true' : 'false');
    (new shj.signUp.student.Dokument(student.matrikelnummer, {"english":isEnglish})).add(
        function(newDok){
            student.dokumente.transkripte.unshift(new shj.signUp.student.Dokument(student.matrikelnummer,newDok.transkript));
            _renderDokumenteTbl();},
        function(){alert('Konnte das neue Transkript nicht anzeigen -- Reload?');}
        );

}
//
// ==========================================================
// ==========================================================
// Antrag
// ==========================================================
// ==========================================================	  

/**
 * Gibt es zur Zeit einen offenen Antrag dieses Typs?
 * (Dient dann zur Msg.: "Sorry, bitte warten Sie, 
 *  bis der offene Antrag bearbeitet wurde").
 * @param {type} iTypID
 * @returns {true|false}
 */
function existiertOffenerAntrag(iTypID){
    // ===========================================
    // Neu laden, falls noch nicht initialisiert
    if(typeof student.antraege==='undefined' || student.antraege===null){
        // Lade Leistungen vom Server und
        student.antraege = new shj.signUp.student.Antraege(student.matrikelnummer,
                function(item) {
                    // Stelle die Leistungen in der Tabelle dar:
                    return existiertOffenerAntrag(iTypID);
                });
         return;
    }

    // ===========================================
    // Schaue nach nicht-abgeschlossenen
    // Anträgen dieses Typs
    for(var ii=0;ii<student.antraege.antraege.length;ii++){
        if(parseInt(student.antraege.antraege[ii].typ_id)===parseInt(iTypID) && student.antraege.antraege[ii].abgeschlossen!='true'){
            return true;
            break;
        }
    }
    return false;
}

/**
 * Prüft 
 * (1) ob ein nicht-abgeschlossener Antrag gleichen Typs
 *     bereits vorliegt,
 * (2) ob ein entsprechendes Transkript schon ausgesgellt wurde.
 * @param {type} iTypID
 * @returns {undefined}
 */
function checkAntragPlausibility(iTypID){
    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Testen, ob es schon einen solchen Antrag gibt
    if(existiertOffenerAntrag(iTypID)){
        alert('Sorry -- ein entsprechender Antrag ist bereits gestellt. Bitte ' + 
            'warten Sie, bis er bearbeitet ist, bevor Sie ihn erneut stellen.');
        return false;
    }
    
    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Testen, ob ein identisches Transkript schon
    // ausgestellt wurde:
    // (a) suche Datum d. letzten Leistung,
    // (b) suche Datum d. letzten Transkripts diesen Typs,
    // (c) melde Fehler, falls b>=a
    var dLetzteLeistung=getLeistungLast();
    var dLetztesTranskript=getAntragLast(iTypID);
    
    if(dLetztesTranskript >= dLetzteLeistung){
        alert('Sorry -- das Transkript wurde Ihnen bereits ausgestellt, bzw. es sind ' +
                'seit dem letzten Ausstellen des Transkripts keine weiteren Leistungen ' +
                'hinzugekommen.\n\nBitte lassen Sie ggf. eine Kopie des vorliegenden ' + 
                'Transkripts beglaubigen.\n\nSobald eine neue Leistung hinterlegt ist, können ' + 
                'Sie auch wieder ein Transkript beantragen.');
        return false;
    }
    return true;
}

/**
 * @returns {Date} das Datum des zuletzt ausgestellten Scheins
 */
function getLeistungLast(){
    if((typeof student.leistungen==='undefined')||(student.leistungen===null)){
        student.leistungen = new shj.signUp.student.Leistungen(student.matrikelnummer, getLeistungLast);
    }
    var dTmpMax=new Date(2001,1,1);
    for(var ii=0;ii<student.leistungen.leistungen.length;ii++){
        var dTmpCurr=shj.signUp.toolbox.getDateFromGerman(student.leistungen.leistungen[ii].datum);
        if(dTmpMax<dTmpCurr) dTmpMax=dTmpCurr;
    }
    return dTmpMax;
}

/**
 * @returns {Date} das Datum des zuletzt _abgeschlossenen_ Antrags
 */
function getAntragLast(iTyp_IN){
    // ===========================================
    // Neu laden, falls noch nicht initialisiert
    if(typeof student.antraege==='undefined' || student.antraege===null){
        // Lade Anträge vom Server und
        student.antraege = new shj.signUp.student.Antraege(student.matrikelnummer,
                function(item) {
                    getAntragLast(iTyp_IN);
                });
    }

    // ===========================================
    // Suche abgeschlossenen Antrag dieses Typs
    // mit max Datum.
    var dTmpMax=new Date(2001,1,1);
    for(var ii=0;ii<student.antraege.antraege.length;ii++){
        if(student.antraege.antraege[ii].typ_id==iTyp_IN && student.antraege.antraege[ii].abgeschlossen=='true'){
            var dTmpCur=shj.signUp.toolbox.getDateFromGerman(student.antraege.antraege[ii].stati[student.antraege.antraege[ii].stati.length-1].datum);
//            alert('Habe abgeschl. Antrag gleichen Typs gefunden mit letztem Status vom: ' + dTmpCur);
            if(dTmpMax<dTmpCur) dTmpMax=dTmpCur;
        }
    }
    return dTmpMax;
}

// 
function addAntrag(iTypID, sAntragstext){
    if(!checkAntragPlausibility(iTypID)) return;
       
    shj.signUp.toolbox.confirmDelete(
            '#modShj_SignUp_ConfirmDelete',
            'Antrag stellen?',
            'Soll der Antrag<br /><br /><b>' +
            sAntragstext + '</b>' +
            '<br /><br />wirklich gestellt werden?',
            function() {
                var tmp=new shj.signUp.student.Antrag(-1, {"matrikelnummer":$.signUpGlobal.matrikelnummer, 
                    "id":"-1", 
                    "typ_id":iTypID, 
                    "antrag":sAntragstext, "stati":[]}).add(function(){
                        student.antraege=null;
                        showAntraege();
                        setView($.signUpGlobal.iVIEW_STUDENT_ANTRAG);
                    }, function(){alert('Fehler beim Hinzufügen des Antrags');});
            }
    );
    

}

        
/**
 * Stellt die Tabelle mit (alten und aktuellen) 
 * Anträgen dar.
 * @param {type} item
 * @returns {unresolved}
 */
function _renderAntraegeTabelle(item){
        // Sind die Dozenten schon initialisiert?
        
        // zwischenspeichern des Templates aus Performancegründen
        var domAntragG=$('#template_divAntrag');
        
        // clear
        $('#divAntraege').empty();
        
        // Heidelberger Besonderheit, #Hack
        // Unmodulare Studiengänge haben keine
        // Studienbilanz.
        if(shj.signUp.toolbox.isUnmodularInHeidelberg(student.fach_id)){
            $('#AntragTranskriptStep1 .shjModularOnly').hide();
            $('#AntragTranskriptStep1 .shjUnmodularOnly').show();
        }
        
        // Durchlaufe die Anträge ...
        for(var ii=0;ii<item.antraege.length;ii++){
            var domAntrag=domAntragG.clone();
            domAntrag=shj.signUp.toolbox.renderDiv(domAntrag, item.antraege[ii], 'template_Antrag_');
            domAntrag.find('#collapseOne').attr('id','Stati-' + item.antraege[ii].id);
            domAntrag.attr('id', 'Antrag' + item.antraege[ii].id)
                    .find('a').attr('href','#Stati-' + item.antraege[ii].id);
            if(item.antraege[ii].abgeschlossen=='true') domAntrag.find('a').addClass("text-success");
            var sStati=new Array();
            for(var jj=0; jj<item.antraege[ii].stati.length;jj++){
                sStati.push('<li>' + item.antraege[ii].stati[jj].datum + ': ' + item.antraege[ii].stati[jj].bezeichnung + 
                    (item.antraege[ii].stati[jj].bearbeiter_name.indexOf('null') === 0 ? '' : ' (' +
                    item.antraege[ii].stati[jj].bearbeiter_name + ')</li>'));
            }
            domAntrag.find('.shj_antrag_stati').append('<ul>' + sStati.join('') + '</ul>');
         
            //alert(item.antraege[ii].id + " -> " + item.antraege[ii].stati);
            $('#divAntraege').append(domAntrag);
        };
        if(ii==0) $('#AntragTranskript_meineAntraege').hide();
        else $('#AntragTranskript_meineAntraege').show();
    }
    
    /**
     * liefert den Nachnamen zur DozentID.
     * Voraussetzung: $.signUpGlobal.info.seminar.dozenten ist initialisiert.
     * @param {type} lDozentID
     * @returns {String}
     */
    function getDozentName(lDozentID){
        for (var ii = 0; ii < $.signUpGlobal.info.seminar.dozenten.dozenten.length; ii++) {
            if ($.signUpGlobal.info.seminar.dozenten.dozenten[ii].id === lDozentID) {
                return $.signUpGlobal.info.seminar.dozenten.dozenten[ii].name;
                break;
            }
        }
        return '';
    }
    
    // ==========================================================
    // Zeige tabellarische Kursliste, die den Kriterien
    // der Kurssuche (u.U. aus dem Archiv)
    // entspricht
    function showAntraege() {
        if(typeof student.antraege==='undefined' || student.antraege===null){
            // Lade Leistungen vom Server und
            // forme sie zu HTML
            student.antraege = new shj.signUp.student.Antraege(student.matrikelnummer,
                    function(item) {
                        // Stelle die Leistungen in der Tabelle dar:
                        _renderAntraegeTabelle(item);
                    });
        }else{
            _renderAntraegeTabelle(student.antraege);
        }
        // Formular anhalten:
        return false;
    };    
        

// 
// ==========================================================
// ==========================================================
// Upload Bilder
// ==========================================================
// ==========================================================	  
            // Setze IMG Upload Ansicht zurück
            function imgUploadResetView(){
                $('#cmdResetIMG').click();
                $('#img_progress_bar').hide();
                $('#img_progress_bar').find('.bar').attr("style", "width:0%");
                $('#cmdIMGUpload').show();
                $('#cmdIMGCancel').show();
                $('#img_wait').hide();
            }
            
            
            // Fortschrittsbalken des Uploads
            function progressHandlingFunctionImg(e) {
                if (e.lengthComputable) {
                    $('.bar').attr("style", "width:" + e.loaded * 100 / e.total + "%");
                    if (e.loaded / e.total >= 1) {
                        $('#img_progress_bar').fadeOut('slow', function() {
                            $('#cgIMGSuchen').fadeOut();  
                            $('#cmdIMGUpload').fadeOut();
                            $('#cmdCancelIMG').fadeOut();
                            $('#img_wait').fadeIn();
                        });
                    }
                }
            }
            
            /**
             * Teste das Vorhandensein eines Fotos
             * auf dem Server (HEAD)
             * @returns {undefined}             */
            function initPhoto(){
                $.ajax({
                    url: '../../studentPhoto/' + student.pid + '.jpg',
                    type: 'HEAD',
                    success: function(){
                                // ... es GIBT EIN Foto, zeige es an, und setze
                                // Link zum Vergrößern bei Klick:
                                $('#studentPhoto').attr("src",function(){
                                    return "../../studentPhoto/" + student.pid + ".jpg?" + new Date().getTime();
                                });
                                $('#studentPhoto_thumbnail').attr("src",function(){
                                    return "../../studentPhoto/" + student.pid + ".jpg?" + new Date().getTime();
                                });
                                $('#picplace').attr("href",function(){
                                    return "../../studentPhoto/" + student.pid + ".jpg?" + new Date().getTime();
                                });
                                $('#studentInfo').show();
                                $('#cmdDeletePic').show();
                            },
                            error:function(){
                                // ... es GIBT KEIN Foto, zeige stattdessen 
                                // ein gendered-generic default
                                $('#studentInfo').show();
                                $('#cmdDeletePic').hide();
                                if(student.anrede=='Herr') $('#studentPhoto').attr("src","img/acspike_male_user_icon.png");
                            }
                });
            }

// ==========================================================
// ==========================================================
// Thema Prüfungen
// ==========================================================
// ==========================================================	  

        // ===============================
        // ===============================
        // T H E M A: Anmeldungen
        // ===============================
        // ===============================

        // SHARED-LIB START
        // ==========================================================
        // Auswahl Semester, Lehrende, Titel ... um
        // Kurs aus dem Archiv wählen zu können
        function showKurssuche(bCommitment){
                // fillSemesterCboForKurssuche(); //CHANGE FROM LIB: UNCOMMENTED IN OTHER LIB
                showKurssucheFormular(true);
                $('#modKurssuche').modal('show');
                $('#bCommitment').val(bCommitment);
        }

        // ==========================================================
        // Swap der Ansichten Formular|Tabelle
        function showKurssucheFormular(bOn){
                if(bOn){
                        $('#Kurssuchformular').show();
                        $('#modKurssuche .modal-footer').show();
                        $('#Kurssuchergebnis').hide();
                }else{
                        $('#Kurssuchformular').hide();
                        $('#modKurssuche .modal-footer').hide();
                        $('#Kurssuchergebnis').show();
                }
        }

    // ==========================================================
    // Zeige tabellarische Kursliste, die den Kriterien
    // der Kurssuche (u.U. aus dem Archiv)
    // entspricht
    function showKurssucheErgebnis() {
        showKurssucheFormular(false);
        
        //$('#template_kurs').on("click", function(){alert('Hello, coursePicked()');});
        //$('#template_kurs').on("click", function(){coursePicked($(this).data("shj_item"));});
        
        //$('#tKurssuchergebnistabelle').find('tr').remove();
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
        // Formular anhalten:
        return false;
    };


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
        var lModulID=-1;
        var lModulIDSingle=-1;
        var sModuleTable = new Array();
        $.each(student.fach.module, function(key, val) {
            lModulID = val.modul.id.toString();
            if(modulContainsLeistung(lModulID, kurs.leistung_id.toString())){
                lModulIDSingle=val.modul.id;
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
            if(sModuleTable.length===1){
                addAnmeldung(kurs, lModulIDSingle);
            }else{
                addAnmeldung(kurs, -1);
            }
        }
}

        //// SHARED-LIB STOP
        // ===============================
        // Modal einblenden
        // ===============================
        function showAnmeldungPicker(){
                showKurssuche(true);
        }


        // ===============================
        // ===============================
        // T H E M A: Daten
        // ===============================
        // ===============================
        $('input[type=file]').change(function(e){
                var sPath=$('input[type=file]').val();
                if(sPath.length<4) return;

                if( (sPath).substr(sPath.length - 4).toLowerCase()!=".jpg"){
                        alert("Das Foto muss im JPG-Format hochgeladen werden.");
                        $('#Reset1').click();
                }else{
                        $('#cmdFotoUpload').removeClass('disabled');
                }
        });

        // Speichert E-Mail, Mobiltel., Info-E-Mail 
        function saveDetails(){
            student.email=$('#email').val();
            student.mobil=$('#mobil').val();
            student.info_email=$('#infoEmail').attr('checked');
            var tmp=student;
            tmp.fach=null;
            tmp.pruefungen=null;
            tmp.leistungen=null;
            tmp.anmeldungen=null;
            shj.signUp.student.update(student.matrikelnummer, tmp,function(){
                showData(true);
            });
        }
        
        // NÄCHSTER SCHRITT: 
        // (a) studentDetails.jsp kann gelöscht werden, denke ich.
        // (b) .saveDetails über $.signUpGlobal.student.update() implementieren!
        /**
         * Lädt Details vom Server und 
         * zeigt sie an.
         * @param {type} bLoad true, wenn neues Laden vom Server erzwungen werden soll
         * @returns {Boolean|@exp;$@pro;signUpGlobal@pro;student}
         */
        function showData(bLoad){
            // Wenn bloß das Register
            // angeklickt wurde, muss 
            // nichts neu geladen werden.
            if(bLoad!=true){
                    setView($.signUpGlobal.iVIEW_STUDENT_DETAIL);
                    return student;
            }

            // Initialisiere Datensatz
            shj.signUp.search.findStudierende({},{},
                    function(data) {
                        student=data[0].student;
                        student.leistungen=null;
                        student.anmeldungen=null;
                        if(student.email=='') student.email='---';
                        if(student.telefon=='') student.telefon='--';

                        var headline=$('#template_subStudentHeadlines').clone();

                        student.fach = $.signUpGlobal.info.seminar.faecher.get(student.fach_id);
                        
                        // Heidelberger Besonderheit, #Hack
                        // Unmodulare Studiengänge haben keine
                        // Studienbilanz.
                        if(shj.signUp.toolbox.isUnmodularInHeidelberg(student.fach_id)){
                            $('#navStudienbilanz').hide();
                        }
                        
                        student.fachname=student.fach.name;         // Für .renderDiv und die Anzeige
                        shj.signUp.toolbox.renderDiv(headline, student, 'tpl_studentWell_');

                        headline.find('#template_studentDetails_email').val(student.email).attr("id","email");
                        headline.find('#template_studentDetails_mobil').val(student.mobil).attr("id","mobil");
                        headline.find('#template_studentDetails_infoEmail').attr("id","infoEmail");

                        if(student.info_email=='true') headline.find('#infoEmail').attr('checked', 'true');

                        // Fürs Foto wird mit der ID hantiert -- die 
                        // darf nicht doppelt vorkommen, muss also von 
                        // der des Templates korrigiert werden
                        headline.find('#template_studentPicture').attr("id","studentPhoto");
                        headline.find('#template_picplace').attr("id","picplace");
                        headline.find('#tpl_cmdDeletePic').attr("id","cmdDeletePic");

                        // Bei Änderung der Daten, die Schaltfläche
                        // zum Speichern aktivieren
                        headline.find('.template_studentDetails_change').on('change', function(){headline.find('.btn').removeClass('disabled');});

                        // Ersetze das DOM-Teil
                        $('#divDaten').empty();
                        $('#divDaten').append(headline);
                        $('#divDaten form').on('submit', function(event){event.preventDefault();saveDetails();});

                        // Teste auf das Vorhandensein eines Fotos ...
                        // (Das hier gibt Probleme in Firefox)
                        $.ajax({
                            url:'../../studentPhoto/' + student.pid + '.jpg',
                            type:'HEAD',
                            success: function(){
                                // ... es GIBT EIN Foto, zeige es an, und setze
                                // Link zum Vergrößern bei Klick:
                                $('#studentPhoto').attr("src",function(){
                                    return "../../studentPhoto/" + student.pid + ".jpg";
                                });
                                $('#picplace').attr("href",function(){
                                    return "../../studentPhoto/" + student.pid + ".jpg";
                                });
                                $('#studentInfo').show();
                            },
                            error:function(){
                                // ... es GIBT KEIN Foto, zeige stattdessen 
                                // ein gendered-generic default
                                $('#studentInfo').show();
                                if(student.anrede=='Herr') $('#studentPhoto').attr("src","img/acspike_male_user_icon.png");
                            }
                        });
                        initPhoto();
                        setView($.signUpGlobal.iVIEW_STUDENT_DETAIL);

                    });
            return true;
        }
        
        // Löschen eines Downloads
        function dropImage() {
            shj.signUp.toolbox.confirmDelete(
                    '#modShj_SignUp_ConfirmDelete',
                    'Bild löschen?',
                    'Soll Ihr Bild ' +
                    '<br />wirklich unwiderruflich gelöscht werden?',
                    function() {
                            var deleteMe = {};
                            deleteMe.matrikelnummer = student.matrikelnummer;
                            deleteMe.command = 'Delete Student Image';          // Wichtig für deleteFile.jsp
                            $.ajax({
                                url: 'util/deleteFile.jsp',
                                type: 'POST',
                                //Ajax events
                                // beforeSend: beforeSendHandler,
                                success: function(answer) {
                                    $('#studentPhoto').attr("src", "img/openclipart_dagobert83_user_icon.png");
                                    $('#cmdDeletePic').fadeOut();
                                    $('#studentPhoto_thumbnail').attr("src", "http://www.placehold.it/200x150/EFEFEF/AAAAAA&text=gelöscht");
                                },
                                error: function(a, b, c) {
                                    alert('Fehler beim Löschen des Fotos' + a + ',' + b + ',' + c);
                                },
                                // Form data
                                data: deleteMe,
                                //Options to tell JQuery not to process data or worry about content-type
                                cache: false,
                                timeout: 20000,
                                contentType: "application/x-www-form-urlencoded;charset=utf8",
                                dataType: 'json'
                            });
                    },
                    function() {
                        //@todo
                    }
            );
        }

        // Die Datei wird hochgeladen...
        $('#cmdIMGUpload').click(function() {
            var formData = new FormData();
            formData.append('file', $('#fileImg')[0].files[0]);
            if($('#fileImg')[0].files[0].size<=0) return false;
            if(formData.size > 500999){
                alert("Die Datei ist zu groß, sorry. Das Limit ist 500 kB");
                return false;
            }
            $.ajax({
                url: './util/fotoUpload.jsp', //server script to process data
                type: 'POST',
                xhr: function() {  // custom xhr
                    myXhr = $.ajaxSettings.xhr();
                    if (myXhr.upload) { // check if upload property exists
                        myXhr.upload.addEventListener('progress', progressHandlingFunctionImg, false); // for handling the progress of the upload
                    }
                    return myXhr;
                },
                //Ajax events
                beforeSend: function() {
                    $('#img_progress_bar').show();
                    $('#cmdIMGUpload').fadeOut();
                    $('#cmdIMGCancel').fadeOut();
                }, 
                success: function(answer) {
                    $('#img_progress_bar').hide();
                    $('#modIMGUpload').modal('hide');
                    initPhoto();
                },
                error: function(e,f,g){alert('Fehler beim Ajax Foto-Upload: ' + explainMe(e) + '\n' + f + '\n' +g);},
                data: formData,
                cache: false,
                contentType: false,
                processData: false
            });
        });
        // =============================================================================================
        // Allgemeine Funktionen
        // =============================================================================================

        // "iVIEW_COURSE_PICKER":1,
        // 	"iVIEW_STUDENT_DETAIL":2,
        // 	"iVIEW_STUDENT_LEISTUNGEN":3,
        // 	"iVIEW_STUDENT_ANMELDUNGEN":4,
        // 	"iVIEW_STUDENT_STUDIENBILANZ":5,
        // 	"iVIEW_STUDENT_PRUEFUNGEN":6
        // 	"iVIEW_STUDENT_ANTRAG":7
        // WHILE THE NUMBERS _ARE_ 
        // IDENTICAL, the FUNCTION IS NOT IDENTICAL
        // (OR VERY SIMILAR) TO THE MAIN THING IN signup-faculty-student.js
        window.setView=function (iVIEW){
                $('#lblKurssuche_Modulwahl').text("Kurssuche");
                $('.signUp-student-nav').removeClass("active");
                $('.signUp_viewToggle').hide();

                switch(iVIEW){
                        // ====================================================================
                        // Detailansicht (Kontakt, Studiengang etc.)
                        case $.signUpGlobal.iVIEW_STUDENT_DETAIL:
                                $('#navDaten').parent().addClass("active");
                                $('#divDaten').fadeIn();
                                $('#help_url').attr('href', 'hilfe/studierende/help_data.jsp');
                                leistung=null;
                                break;

                        case $.signUpGlobal.iVIEW_STUDENT_LEISTUNGEN:
                                $('#navLeistungen').parent().addClass("active");
                                $('#help_url').attr('href', 'hilfe/studierende/leistungen.jsp');
                                $('#divLeistungen').fadeIn();
                                break;

                        case $.signUpGlobal.iVIEW_STUDENT_ANMELDUNGEN:
                                $('#navAnmeldungen').parent().addClass("active");
                                $('#help_url').attr('href', 'hilfe/studierende/anmeldungen.jsp');
                                $('#divAnmeldungen').fadeIn();
                                break; 		

                        case $.signUpGlobal.iVIEW_STUDENT_PRUEFUNGEN:
                                $('#navPruefungen').parent().addClass("active");
                                $('#help_url').attr('href', 'hilfe/studierende/pruefungen.jsp');
                                $('#divPruefungen').fadeIn();
                                break;  
                                
                       case $.signUpGlobal.iVIEW_STUDENT_STUDIENBILANZ:
                                $('#navStudienbilanz').parent().addClass("active");
                                $('#help_url').attr('href', 'hilfe/studierende/studienbilanz.jsp');
                                $('#transkript').fadeIn();
                                break;
                                
                       case $.signUpGlobal.iVIEW_STUDENT_ANTRAG:
                                $('#navAntrag').parent().addClass("active");
                                $('#help_url').attr('href', 'hilfe/studierende/antrag.jsp');
                                $('#AntragTranskript').fadeIn();
                                break;  
                                
                       case $.signUpGlobal.iVIEW_STUDENT_ANTRAG_TRANSKRIPT_1:
                                $('#navAntrag').parent().addClass("active");
                                $('#help_url').attr('href', 'hilfe/studierende/antrag.jsp');
                                $('#AntragTranskriptStep1').fadeIn();
                                break;  

                        case $.signUpGlobal.iVIEW_STUDENT_TRANSKRIPTDRUCK:
                            if(shj.signUp.toolbox.isUnmodularInHeidelberg($('#logged-in-user-fach_id').val())){
                                alert('Sorry -- für unmodulare Studiengänge (Magister, Staatsexamen nach WPO) ' + 
                                        'stehen momentan keine Transkripte zum Selbstdruck zur Verfügung.');
                                break;
                            }else{
                                $('#navTranskriptdruck').parent().addClass("active");
                                $('#help_url').attr('href', 'hilfe/studierende/transkriptdruck.jsp');
                                $('#Transkriptdruck').fadeIn();
                                break;  
                            }

                }
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
 * Hat im Ggstz. zu Config kein Kontextmenü
 * @returns {unresolved}
 */
function showTranscript(silent) {
    // Schnittstelle:
    // 
    // Ggf. noch die Leistungen vom Server laden
    if (typeof student.leistungen === 'undefined' || student.leistungen === null) {
        student.leistungen = new shj.signUp.student.Leistungen(student.matrikelnummer, function(){showTranscript(silent);});
        return;
    }

    // Reset
    student.transkript = [];
    student.transkript.modul = [];
    var absolv = [];
    
    // Durchlaufe die Module des 
    // relevanten Studiengangs
    for (var ii = 0; ii < student.fach.module.length; ii++) {
        // Notiere relevantes Modul beim Studierenden
        student.transkript.modul.push(
                {'name': student.fach.module[ii].modul.name,
                    'name_en': student.fach.module[ii].modul.name_en,
                    'id': student.fach.module[ii].modul.id,
                    'min_ects': student.fach.module[ii].modul.min_ects,
                    'leistungen': []});

        // Durchlaufe die Leistungen, die in diesem 
        // Modul erbracht werden können
        for (var jj = 0; jj < student.fach.module[ii].modul.leistungen.length; jj++) {

            // Ist die Leistung in diesem Modul absolviert?
            absolv = getAbsolvierte(student.fach.module[ii].modul, student.fach.module[ii].modul.leistungen[jj].leistung);

            // Falls es Leistung(en) gibt, die als absolviert
            // gelten: einzeln anhängen
            for (var kk = 0; kk < absolv.length; kk++) {
                student.transkript.modul[ii].leistungen.push(absolv[kk]);
            }

            // Wenn nicht absolviert, generischer Eintrag der Leistung im Array
            if (absolv.length === 0) {
                student.transkript.modul[ii].leistungen.push(
                        student.fach.module[ii].modul.leistungen[jj].leistung);
            }
        }// END for Leistungen
    }// end for MODULE

    // Hier sollte jetzt ein Array hängen, mit
    if(typeof silent==='undefined') renderTranscript();
    else renderTranscriptSimple();
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
    $('#tStudienbilanz tbody').find('tr').remove(); // vermutlich funktionslos
    $('#transkript *').not('h2').remove();            // Reset der alten Anzeige

    // Durchlaufe die relevanten Module und konstruiere
    // die HTML-Tabellen mit Leistungen, ggf. Noten, LP etc.
    for (var ii = 0; ii < student.transkript.modul.length; ii++) {
        var tbl = $('#tStudienbilanz').clone();
        dLPErreicht = 0;                                  // Summierung erreichter Leistungspunkte
        dLPMin = student.transkript.modul[ii].min_ects;   // Geforderte Leistungspunkte dieses Moduls 

        // Durchlaufe jetzt die Leistungen des Moduls und 
        // notiere die absolvierten Leistungen mit Note
        // und Leistungspunkten (etc.), und die noch 
        // nicht absolvierten (z.B. mit Button zum 
        // Erzeugen neuer Leistungen).
        for (var jj = 0; jj < student.transkript.modul[ii].leistungen.length; jj++) {

            // Leistung ist absolviert
            if (typeof student.transkript.modul[ii].leistungen[jj].note != 'undefined') {
                var row = $('#template_transkript_absolvierte').clone();
                dLPErreicht += parseFloat(student.transkript.modul[ii].leistungen[jj].ects);
                shj.signUp.toolbox.mergeToRow(row, student.transkript.modul[ii].leistungen[jj], 'template_transkript_absolvierte_');
                // Leistung ist NICHT absolviert (sondern "offen")
            } else {
                var row = $('#template_transkript_offene').clone();
                shj.signUp.toolbox.mergeToRow(row, student.transkript.modul[ii].leistungen[jj], 'template_transkript_offene_');

            }
            rows.push(row);
        }

        tbl.attr('id', 'mod_' + student.transkript.modul[ii].id);
        tbl.find('.template_ModulSummary_modulname').text(student.transkript.modul[ii].name);

        // Fehlen noch LP? Ist das Modul erfüllt? Gibt es
        // zu viele LP?
        if (parseFloat(dLPMin)==0 || parseFloat(dLPMin) > parseFloat(dLPErreicht)) {
            sClass = 'badge badge-warning';
            if(parseFloat(dLPMin)==0) sSummary = 'Es werden 0 LP verlangt. SignUp kann nicht beurteilen, zu wie viel % das Modul absolviert ist';
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

    // Aktiviere das Register.
    setView($.signUpGlobal.iVIEW_STUDENT_STUDIENBILANZ);
}

/**
 * Darstellung des Transkripts (ohne Serverkontakt)
 */
function renderTranscriptSimple() {
    var rows = [];
    var dLPErreicht = 0;
    var dLPSumTotal=0;
    var dLPMin = 0;
    var sBindings = '';
    // Reset || Schnittstelle
    var header=$('#templatekontrollvorlage_sd').clone();
    header.attr('id', 'kontrollvorlage_sd');
    header=shj.signUp.toolbox.renderDiv(header, student, "template_kontrollvorlage_student_");
    $('#AntragTranskript_header').empty().append(header);

    // ###########
    // Durchlaufe die relevanten Module 
    var bIsFirstCredit=true;
    var modul_leistungen=new Array();
    for (var ii = 0; ii < student.transkript.modul.length; ii++) {
        dLPErreicht = 0;                                  // Summierung erreichter Leistungspunkte
        dLPMin = student.transkript.modul[ii].min_ects;   // Geforderte Leistungspunkte dieses Moduls 
        bIsFirstCredit=true;

        // Durchlaufe jetzt die Leistungen des Moduls und 
        // notiere die absolvierten Leistungen mit Note
        // und Leistungspunkten (etc.), und die noch 
        // nicht absolvierten (z.B. mit Button zum 
        // Erzeugen neuer Leistungen).
        for (var jj = 0; jj < student.transkript.modul[ii].leistungen.length; jj++) {

            // Leistung ist absolviert
            if (typeof student.transkript.modul[ii].leistungen[jj].note !== 'undefined') {
                if (isNoteBestanden(student.transkript.modul[ii].leistungen[jj].note)){
                    if(bIsFirstCredit){
                        modul_leistungen.push("<b>" + student.transkript.modul[ii].name + "</b>");
                        bIsFirstCredit=false;
                    }
                    var row = $('#template_kontrollvorlage_leistung').clone();
                    row.removeAttr("id");
                    dLPErreicht += parseFloat(student.transkript.modul[ii].leistungen[jj].ects);
                    modul_leistungen.push($(shj.signUp.toolbox.renderDiv(row, student.transkript.modul[ii].leistungen[jj], 'template_kontrollvorlage_leistung_')).html());
                }
            } 
        }


        // Fehlen noch LP? Ist das Modul erfüllt? Gibt es
        // zu viele LP?
        if (parseFloat(dLPMin) === parseFloat(dLPErreicht)) {
            if(!bIsFirstCredit) modul_leistungen.push('<div><em>Modul komplett absolviert</em></div><br />');            
        }else{
            if(!bIsFirstCredit) modul_leistungen.push('<br />');            
        }
        dLPSumTotal+=dLPErreicht;
    }
    modul_leistungen.push('<br /><b>Insgesamt ' + dLPSumTotal + ' Leistungspunkte erreicht<br />');
    $('#AntragTranskript_header').append(modul_leistungen.join('<br>'));
    // Aktiviere das Register.
    //setView($.signUpGlobal.iVIEW_STUDENT_STUDIENBILANZ);
}

function isNoteBestanden(sNotenBezeichnung){
    return !(sFAIL.indexOf(';' + sNotenBezeichnung + ';')>=0);
}