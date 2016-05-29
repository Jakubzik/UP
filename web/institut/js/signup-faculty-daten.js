    /**
     * "Daten" dient dem Setzen der Sprechstunde, des 
     * Dienstzimmers, der Telefonnummer und E-Mail Adresse.
     * 
     * Diese werden je in Spalten der Tabelle "tblSdDozent" 
     * abgelegt; diese Eigenschaften werden über 
     * das Objekt "Dozent" aus der JS-Bibliothek common
     * gemanagt und entsprechen i.d.R. wie üblich
     * Tabellenspalten.
     * 
     * Was die Rubriken der Homepage, die Anzeige von 
     * Lehrveranstaltungen sowie die Downloads betrifft,
     * wird hier aber testweise semi-strukturierte 
     * Speicherweise verwendet: JSON-Objekte landen 
     * in Tabellenspalten des Typs text in Postgres.
     * 
     * @requires signup-faculty-common.js, bootstrap.min.js,
     * bootstrap-fileupload.min.js, wysihtml/simple.js, 
     * wysihtml5-0.3.min.js, 
     */
    $(document).ready(function() {
        // User init
        var me = {};
        
        // Zwischenspeichern der HTML-Vorlage
        $.signUpGlobal.templateRubriken = $('#template_rubrik');
        $.signUpGlobal.user_id=$('#d_id').val().trim();
        
        // Standard: Ajax Wartemitteilung.
        $('body').on({
            ajaxStart: function() {
                $(this).addClass('loading');
            },
            ajaxStop: function() {
                $(this).removeClass('loading');
            }
        });
        
        /**
         * ##########################################################
         * Initialisieren der Seite beim Laden
         * ##########################################################
         */
        if($('#d_homepage').val().length<5){        // Ist eine externe ('ex'), automatische ('hd') 
            $('#cboAddress').val('');               // oder keine Homepage ('') 
        }else{                                      // gespeichert?
            var sTmp = $('#d_homepage').val();
            if(sTmp.indexOf('?id=' + $.signUpGlobal.user_id) !== -1) $('#cboAddress').val('hd');
            else $('#cboAddress').val('ex');
        }

        if($('#d_interessen').val().length<4){      // Dozent.Interessen: enthält JSON-Array
            me.rubriken = new Array();              // mit Escape " -> |
            createRubrik();                         // Rubriken finden sich 
        }else{                                      // im Array
            var sTmp=JSON.stringify($('#d_interessen').val());
            sTmp = sTmp.replace(/\|/g, "\"").substr(1, sTmp.length-2);
            me.rubriken = JSON.parse(sTmp);
            displayHpRubriken(me.rubriken);
        }
        
        if($('#d_hpOptionen').val().length<4){
            me.hp_options={};                       // Dozent.HomepageOptionen enthält JSON-Objekt
        }else{                                      // mit Kvv-Anzeige-Optionen sowie Downloads
            var sTmp=JSON.stringify($('#d_hpOptionen').val());              // (Escape ist wieder " -> |)
            sTmp = sTmp.replace(/\|/g, "\"").substr(1, sTmp.length-2);
            me.hp_options=JSON.parse(sTmp);
            $('#cboKvv').val(me.hp_options.show_kvv);
        }
        
        setViewHP();    // Logik der Optionen für die Homepage 
        initPhoto();    // Ggf. Foto anzeigen

        /**
         * ##########################################################
         * Benutzer-Interaktion
         * ##########################################################
         */
        $('#cmdDeletePic').on('click',dropImage);   // Löschen des Fotos

        $('form').bind('submit', function(event) {  // Allg. Formulare abschalten
            event.preventDefault();                 // und stattdessen .changeData()
            changeData();
        });
        
        $('#cmdSave').on('click', changeData);      // Speichere Einstellungen
        $('#b3').on('click',changeData);            // ""   ""    ""       ""
        $('#b1').on('click',createRubrik);          // Anlegen neuer Rubrik

        $('#cboAddress').on('change', function() {  // Reaktion auf Auswahl
            setViewHP();                            // des Homepage-Typs
            changeData();                           // (extern, automatisch, keine)
        });

        $('#cboKvv').on('change', changeData);      // Auswahl Cbo löst Speichern aus
        $('#address').on('change', changeData);     // "" ""         ""          ""

        // Setze Modalfenster zum Hochladen
        // der PDFs zurück
        $('#modPDFUpload').on('hidden', pdfUploadResetView);
        $('#modIMGUpload').on('hidden', imgUploadResetView);
        $('#modIMGUpload').on('show', imgUploadResetView);
            
        // Wenn es bereits Downloads gibt: anzeigen
        if (typeof me.hp_options.downloads !== 'undefined') _renderDownloads();
            
        $('#cmdUpdatePic').on("click", function() {     // Auswahl eines (neuen) Fotos
            $('#modFotoUpload').modal('show');          // ermöglichen.
        });

        $('#cmdAddDownload').on('click', function() {   // Auswahl eines (neuen)
            $('#modPDFUpload').modal('show');           // Downloads ermöglichen
        });
            
        $('input[type=file]').change(function(e) {      // Validiere neu ausgewählte
            var sPath = $('input[type=file]').val();    // Datei (Foto oder PDF)
            if (sPath.length < 4)
                return;
            if ((sPath).substr(sPath.length - 4).toLowerCase() != "." + $(this).attr('shj_allow')) {
                alert("Die Datei muss die Endung ." + $(this).attr('shj_allow') + " haben.");
                if($(this).attr('shj_allow')==='pdf') $('#pdfReset').click();
                else $('#cmdResetIMG').click();
            } else {
            }
        });
            
        // ==============================================
        // Uploads (PDF und Bild)
        $('#pdf').on('keydown', function(){             // Erzwinge Bezeichnung für Download
            if(pdfFileOK() && $('#pdf').val().length>3) $('#cmdPDFUpload').removeClass('disabled');
            else $('#cmdPDFUpload').addClass('disabled');
        }).on('shown', function(){$(this).focus();});
            
        $('#filePDF').change(function(e) {              // Plausibilität des Uploads
            if(!pdfFileOK()) return;
            else {
                if ($('#pdf').val().length < 3) {
                    $('#pdf').fadeIn().focus();
                } else {
                    $('#cmdPDFUpload').removeClass('disabled');
                }
            }
        });
        
        $('#cmdPDFUpload').click(function() {
            if($('#cmdPDFUpload').hasClass('disabled')) return;
            var formData = new FormData();
            formData.append('file', $('#filePDF')[0].files[0]);
            $.ajax({
                url: 'util/pdfUpload.jsp', //server script to process data
                type: 'POST',
                xhr: function() {  // custom xhr
                    myXhr = $.ajaxSettings.xhr();
                    if (myXhr.upload) { // check if upload property exists
                        myXhr.upload.addEventListener('progress', progressHandlingFunction, false); // for handling the progress of the upload
                    }
                    return myXhr;
                },
                beforeSend: function() {
                    $('#pdf_progress_bar').show();
                    $('#cmdPDFUpload').fadeOut();
                    $('#cmdCancelPDF').fadeOut();
                },
                success: function(answer) {
                    answer.name = $('#pdf').val();
                    addDownload(answer);
                    $('#pdf_progress_bar').hide();
                    $('#modPDFUpload').modal('hide');
                },
                // error: ,
                data: formData,
                cache: false,
                contentType: false,
                processData: false
            });
        });

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
                url: 'util/fotoUpload.jsp', //server script to process data
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
                // error: function(e,f,g){alert('Fehler beim Ajax Foto-Upload: ' + e + '\n' + f + '\n' +g);},
                data: formData,
                cache: false,
                contentType: false,
                processData: false
            });
        });
 
            /**
             * Ist die Dateigröße in #filePDF < 1 MB 
             * und vom Typ .pdf (attr shj_allow)?
             * @returns {Boolean}
             */
            function pdfFileOK(){
                // Funktion wird durch change getriggert
                // aufgerufen, während filePDF unsichtbar ist.
                if(typeof $('#filePDF')[0].files[0]==='undefined') return false;
                
                if ($('#filePDF')[0].files[0].size > 999999) {
                    alert("Die Datei ist zu groß, sorry. Das Limit ist 1 MB.");
                    $('#pdfReset').click();
                    return false;
                }
                var sPath = $('#filePDF').val();
                if ((sPath).substr(sPath.length - 4).toLowerCase() != "." + $('#filePDF').attr('shj_allow')) {
                    alert("Die Datei muss die Endung ." + $('#filePDF').attr('shj_allow') + " haben.");
                    $('#pdfReset').click();
                    return false;
                }
                return true;
            }
            
            
            /**
             * "Externe" Homepage: nur Textfeld für Eingabe anzeigen,
             * "Auto"-Homeage: alle Optionen (Rubriken, Link, Downloads)
             * "Keine" Homepage: alles ausblenden.
             * @returns {undefined}             */
            function setViewHP(){
                var sHPOption=$('#cboAddress').val();
                if (sHPOption == 'ex') {        // externe Homepage
                    $('#address').show();       // Adresse für ext. HP
                    $('#showRubriken').hide();  // Rubriken
                    $('#hp_link').hide();       // Link 
                    $('#cboKvv').hide();        // Optionen für die Lehveranstaltungsanzeige
                    $('#tabDownloads').hide();
                } else {
                    if (sHPOption == 'hd') {    // automatische Homepage
                        $('#address').hide();   // Textfeld für URL ausblenden
                        $('#showRubriken').fadeIn();    //Rubriken anzeigen
                        $('#hp_link').show();   // Link für die Homepage anzeigen
                        $('#cboKvv').show();    // Optionen für die Lehrveranstaltungsanzeige
                        $('#tabDownloads').show();
                    } else {
                        $('#address').hide();   // gar keine Homepage
                        $('#showRubriken').hide();  // Rubriken weg
                        $('#hp_link').hide();   // Link ausblenden
                        $('#cboKvv').hide();    // Optionen für Lehrveranstaltungsanzeige weg
                        $('#tabDownloads').hide();
                    }
                }
            }
            
            // Setze PDFUploadAnsicht zurück
            function pdfUploadResetView(){
                $('#pdfReset').click();         // FileUpload Formularfeld zurücksetzen
                $('#pdf_progress_bar').hide();  
                $('#pdf_progress_bar').find('.bar').attr("style", "width:0%");
                $('#pdf').val('').hide();       // Name des Downloads
                $('#cgPDFSuchen').show();       
                $('#cmdPDFUpload').addClass('disabled').show();
                $('#cmdCancelPDF').show();
                $('#pdf_wait').hide();
            }
            
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
            function progressHandlingFunction(e) {
                if (e.lengthComputable) {
                    $('.bar').attr("style", "width:" + e.loaded * 100 / e.total + "%");
                    if (e.loaded / e.total >= 1) {
                        $('#pdf_progress_bar').fadeOut('slow', function() {
                            $('#pdf').fadeOut();
                            $('#cgPDFSuchen').fadeOut();  
                            $('#cmdPDFUpload').fadeOut();
                            $('#cmdCancelPDF').fadeOut();
                            $('#pdf_wait').fadeIn();
                        });
                    }
                }
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
                    url: '../../studentPhoto/dozent' + $('#d_id').val().trim() + '.jpg',
                    type: 'HEAD',
                    success: function() {
                        // ... es GIBT EIN Foto, zeige es an, und setze
                        // Link zum Vergrößern bei Klick:
                        $('#dozent_photo').attr("src", function() {
                            return "../../studentPhoto/dozent" + $('#d_id').val().trim() + ".jpg?" + new Date().getTime();
                        });
                        $('#dozent_photo_thumbnail').attr("src", function() {
                            return "../../studentPhoto/dozent" + $('#d_id').val().trim() + ".jpg?" + new Date().getTime();
                        });

                        $('#picplace').attr("href", function() {
                            return "../../studentPhoto/dozent" + $('#d_id').val().trim() + ".jpg";
                        });
                        $('#cmdDeletePic').show();
                        $('#dozent_photo').show();
                    },
                    error: function() {
                        // ... es GIBT KEIN Foto, zeige stattdessen 
                        // ein gendered-generic default
                        $('#studentInfo').show();
                        $('#cmdDeletePic').hide();
                        //if(student.anrede=='Herr') $('#studentPhoto').attr("src","img/acspike_male_user_icon.png");
                    }
                });
            }
            
            /**
             * Anzeige der Downloads als Liste mit Link
             * @returns {undefined}             */
            function _renderDownloads() {
                var sDown = new Array();
                $('#Downloads').empty();
                for (var ii = 0; ii < me.hp_options.downloads.length; ii++) {
                    sDown.push('<div class="alert alert-block">' +
                            '<h5><a target="_blank" href="../../studentPhoto/' + me.hp_options.downloads[ii].src +
                            '">' + me.hp_options.downloads[ii].name + '</a><a shj_download="' + ii + '" href="#" class="shj-delete btn btn-danger pull-right"><i class="icon-minus-sign icon-white"></i> Löschen</a>' +
                            '</h5></div>');
                }
                $('#Downloads').append(sDown.join(''));
                $('#Downloads a.shj-delete').on("click", function() {
                    dropDownload($(this).attr('shj_download'));
                })
            };

            // Löschen eines Downloads
            function dropImage() {
                shj.signUp.toolbox.confirmDelete(
                        '#modShj_SignUp_ConfirmDelete',
                        'Bild löschen?',
                        'Soll Ihr Bild ' +
                        '<br />wirklich unwiderruflich gelöscht werden?',
                        function() {
                                var deleteMe = {};
                                deleteMe.user = $.signUpGlobal.user_id;
                                deleteMe.command = 'Delete Image';
                                $.ajax({
                                    url: 'util/deleteFile.jsp', //server script to process data
                                    type: 'POST',
                                    //Ajax events
                                    // beforeSend: beforeSendHandler,
                                    success: function(answer) {
                                        $('#dozent_photo').attr("src", "img/openclipart_dagobert83_user_icon.png");
                                        $('#cmdDeletePic').fadeOut();
                                        $('#dozent_photo_thumbnail').attr("src", "http://www.placehold.it/200x150/EFEFEF/AAAAAA&text=<%=user.getDozentNachname()%>");
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
                                    dataType: 'json',
                                });
                        },
                        function() {
                            //@todo
                        }
                );
            }

            // Löschen eines Downloads
            function dropDownload(iNumber) {
                shj.signUp.toolbox.confirmDelete(
                        '#modShj_SignUp_ConfirmDelete',
                        'Download löschen?',
                        'Soll dieser Download<br /><br /><b>' +
                        me.hp_options.downloads[iNumber].name + '</b>' +
                        '<br /><br />wirklich unwiderruflich gelöscht werden?',
                        function() {
                                var deleteMe = {};
                                deleteMe.src = me.hp_options.downloads[iNumber].src;
                                deleteMe.command = 'Delete Upload';
                                $.ajax({
                                    url: 'util/deleteFile.jsp', //server script to process data
                                    type: 'POST',
                                    //Ajax events
                                    // beforeSend: beforeSendHandler,
                                    success: function(answer) {
                                        me.hp_options.downloads.splice(iNumber, 1);
                                        changeData();
                                        _renderDownloads();
                                    },
                                    error: function(a, b, c) {
                                        alert('Fehler beim Löschen Download: ' + b + ' ' + c);
                                    },
                                    // Form data
                                    data: deleteMe,
                                    //Options to tell JQuery not to process data or worry about content-type
                                    cache: false,
                                    timeout: 20000,
                                    contentType: "application/x-www-form-urlencoded;charset=utf8",
                                    dataType: 'json',
                                });
                        },
                        function() {
                            //@todo
                        }
                );
            }
            function dropRubrik(iNumber) {
                // Es gibt einen #BUG, @TODO
                // die erste Rubrik löscht alle 
                // anderen mit -- unklar, warum.
                // if(iNumber==0) return;
                $('#rubrik_' + iNumber).empty();
                changeData();
            }
            function createRubrik(oRubrik) {
                // Wie viele gibt's schon?
                var iNumber = $('#fRubriken textarea').length;

                // Berechne die neuen Ids
                var iTextareaID = 'hp-text' + iNumber;
                var iToolbarID = 'wysihtml5-toolbar' + iNumber;
//                var iCreate = 'cmdNext_' + iNumber;
                var iDelete = 'cmdDelete_' + iNumber;
                var iSave = 'cmdSave_' + iNumber;

                // Hole Kopie der Vorlage und passe 
                // deren IDs an
                var tmp = $.signUpGlobal.templateRubriken.clone();
                tmp.attr('id', 'rubrik_' + iNumber);
                tmp.find('#template_hp-text').attr('id', iTextareaID);
                tmp.find('#template_wysihtml5-toolbar').attr('id', iToolbarID);
//                tmp.find('#b1').attr('id', iCreate);
                tmp.find('#b2').attr('id', iDelete);
                tmp.find('#b3').attr('id', iSave);

                // Hänge angepasste Kopie ins DOM
                //if(iNumber===0) $('#fRubriken').append(tmp);
                // else $('#rubrik_' + (iNumber-1)).after(tmp);
                $('#rubrik_' + (iNumber - 1)).after(tmp);
                // Initialisiere neue Schaltfläche
//                $('#' + iCreate).on('click', createRubrik);
                $('#' + iDelete).on('click', function() {
                    dropRubrik(iNumber)
                });
                $('#' + iSave).on('click',changeData);
                if (typeof oRubrik !== 'undefined') {
                    $('#' + iTextareaID).val(oRubrik.inhalt);
                    $('#rubrik_' + iNumber + ' input').val(oRubrik.rubrik);
                }
                // Initialisiere neue Rich-Text TextArea
                var editor = new wysihtml5.Editor(iTextareaID, {// id of textarea element
                    toolbar: iToolbarID, // id of toolbar element
                    parserRules: wysihtml5ParserRules // defined in parser rules set 
                });
            }

            /**
             * JSON-Array mit den Rubriken
             * [{"rubrik":"X","inhalt","Y"}, .. , {"rubrik":"A","inhalt","B"}]
             * @returns {Array}
             */
            function gatherRubriken() {
                var tmp = new Array();
                for (var ii = 0; ii < $('#fRubriken textarea').length; ii++) {
                    if (typeof $('#rubrik_' + ii + ' input').val() !== 'undefined')
                        tmp.push('{"rubrik":"' + $('#rubrik_' + ii + ' input').val().replace(/"/g, "&quot;").replace(/\t/g, '') + '","inhalt":"' + $('#rubrik_' + ii + ' textarea').val().replace(/"/g, "&quot;").replace(/\t/g, '') + '"}')
                }
                return tmp;
            }

            /**
             * Stellt die Rubriken dar
             * @param {type} oRubriken
             * @returns {undefined}
             */
            function displayHpRubriken(oRubriken) {
                $('#fRubriken').find('*').remove();
                $('#showRubriken').fadeIn();
                $('#fRubriken').html('<div id="rubrik_-1"></div>');
                for (var ii = 0; ii < oRubriken.length; ii++) {
                    createRubrik(oRubriken[ii]);
                }
            }

            function addDownload(oNewDownload) {
                if (typeof me.hp_options.downloads === 'undefined')
                    me.hp_options.downloads = new Array();
                me.hp_options.downloads.push(oNewDownload);
                changeData();
                _renderDownloads();
            }

            function stringifyDownloads() {
                var tmp = new Array();
                if (typeof me.hp_options.downloads !== 'undefined'){
                    for (var ii = 0; ii < me.hp_options.downloads.length; ii++) {
                        tmp.push("{\"src\":\"" + me.hp_options.downloads[ii].src + "\",\"name\":\"" + me.hp_options.downloads[ii].name + "\"}");
                    }
                }
                return tmp.join(',');
            }
            /**
             * Speichert die aktuell angezeigten
             * Optionen auf dem Server
             * @returns {unresolved}
             */
            function changeData() {
                var dozent = new shj.signUp.seminar.Dozent($('#logged-in-user-seminar').val());
                dozent.id = $('#d_id').val();
                dozent.sprechstunde = $('#sprechstunde').val();

                //dozent.rubriken=JSON.stringify( gatherRubriken() );
                dozent.rubriken = gatherRubriken().join(',');

                dozent.hp_optionen = "\"show_kvv\":\"" + $('#cboKvv').val() + "\"";
                dozent.hp_optionen += ",\"downloads\":[" + stringifyDownloads() + "]";

                //dozent.hp_optionen.show_kvv = $('#cboKvv').val();
                //alert(stringifyDownloads());
                dozent.passwort = $('#txtPasswort').val();
                if ($('#txtPasswort').val() !== '*****') {
                    // Passwort geändert:
                    if ($('#txtPasswort').val() !== $('#txtPasswortWdh').val()) {
                        alert("Password und Wiederholung sind nicht identisch -- bitte geben\n\
                                Sie zu Ihrer eigenen Sicherheit gleiche Werte an.");
                        return;
                    }
                    //dozent.passwort_wdh=$('#txtPasswortWdh').val();
                } else {
                    dozent.passwort = '*****';
                }
                dozent.email = $('#email').val();
                dozent.zimmer = $('#zimmer').val();
                dozent.telefon = $('#telefon').val();
                var cboVal = $('#cboAddress').val();
                if (cboVal === '') {
                    dozent.homepage = '';
                } else {
                    if (cboVal === 'ex')
                        dozent.homepage = $('#address').val();
                    else
                        dozent.homepage = 'http://www.as.uni-heidelberg.de/personen/index-generic.php?id=' + $.signUpGlobal.user_id;
                }
                dozent.titel = $('#titel').val();
                dozent.update(function(data) {
                    shj.signUp.toolbox.render(data.dozent, {
                        "item": "dozent",
                        "emptyFirst": true,
                        "actOnDom": true,
                        "stripPrefixOffId": false, // Das Formular wird direkt im HTML ersetzt
                        "HTML_template": "#fEditData",
                        "HTML_template_class_prefix": ''}
                    );
                    $('#txtPasswort').val('*****');
                    $('#txtPasswortWdh').val('*****');

                    $('#modShj_SignUp_OK').modal('show').delay(500).queue(function(next){
                        $('#modShj_SignUp_OK').modal('hide');
                        next();
                    });
                    displayHpRubriken(data.dozent.homepage_rubriken);
                    setViewHP();
                }, function() {
                    alert('Versuch, den Dozenten zu updaten fehlgeschlagen');
                });
            }
});