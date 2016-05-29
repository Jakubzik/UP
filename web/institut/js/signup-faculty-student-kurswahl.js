$(function() {

    // TO DO:
    // FUNKTIONALITÄT:
    // ==================
    // 
    // CONFIG
    // - Möglichkeit, Profile einzuspielen (unter "Config")
    //    
    // - Einstellmöglichkeiten Config
    // - Ergebnisberechnung Config
    //
    // OBERFLÄCHE:
    // ??????????????
    // 
    // CODEQUALITÄT
    // - Anmeldungen zu Objekt in faculty.common.student?
    // 
    // DOKUMENTATION:
    // - Dokumentieren der Funktionalität
    // 
    // DATEN(BANK)
    // Da automatisch nur noch Kurstypen angezeigt werden, 
    // die im Studiengang vorkommen, gibt es mit HCA und 
    // nichtmodularen Studiengängen Probleme:
    // 
    // Check HCA-Login "Trotter" -- führt zu Fehler
    // 
    // Lehramt/Magister alt: Modul mit wählbaren 
    // Leistungen erfinden!
    // 
    //

    /**
     * KURSWAHL (STUDIERENDENSICHT)
     * 
     * Registeransicht mit Registern für
     * (A) Kurs Auswahl (Drag'n'Drop)
     * (B1) Übersichtsanzeige (alle gewählten Kurse)
     * (B2) Ergebnisanzeige (alle zugeteilten Kurse)
     *      dann ist $('#signup-is-result-mode').val()=='true',
     *      außerdem wird der Titel "Übersicht" im zweiten 
     *      Register zu "Ergebnis" korrigiert.
     *      Die Kurswahl wird abgeschaltet.
     * (C) Tauschmöglichkeiten und Vormerkungen
     * 
     * (A) Auswahl:
     * Nur aktiv, falls #signup-is-result-mode!='true.
     * Eine Select-Box bietet Kurstypen zur Auswahl. Pro 
     * Kurstyp können per Drag'n'Drop Priorisierungen 
     * vorgenommen werden.
     * 
     * Es werden nur diejenigen Kurstypen angezeigt, die 
     * (a) für die Kurswahl zugelassen sind, und 
     * (b) im Studiengang vorkommen.
     * 
     * Es werden nur die Kurse angezeigt, die in 
     * tblBdKurs.blnKursScheinanmeldungErlaubt==true
     * haben (das ist neu und wird auch in update.jsp 
     * überprüft).
     * 
     * Konfiguration und beteiligte Dateien
     * ====================================
     * - Die Anzahl der maximal erlaubten Kurstypen 
     *   lässt sich in fragments/conf_kurswahl.jsp
     *   begrenzen.
     *   
     * - Voraussetzungen für die Wahl einzelner 
     *   Kurstypen (z.B. Voraussetzung für PS ist 
     *   Einführung etc.) lassen sich noch 
     *   in json/student/signup/update.jsp programmieren.
     *   (#hack)
     */
/**
* =====================================================================
* (C) Register Tausch
* =====================================================================
*/

/**
* Tauschfunktionalität:
*
*  initTausch(kurs, iStage, kurs_neu)
*  // Management der Stadien
*
*  _renderKurswahlListeTausch()
*  // Listet alle Kurse, die man löschen oder tauschen kann
*  // (siehe auch _renderKurseTausch(lKurstypID)
*
*  _renderKurseTausch(lKurstypID)
*  // listet Kurse mit Tauschoption dieses Kurstyps
*
*  dropVormerkung(vormerkung)
*  // lösche Vormerkung dieses Wunsches vom Server
*
*  dropKursAnmeldung(kurs)
*  // gebe Platz in diesem Kurs für andere frei.
*
*  confirmTausch(kurs, kurs_neu)
*  // Rückfrage: wirklich?
*/

   /**
    * Blendet die Rückfrage ein, ob Kurs "kurs" wirklich
    * für Kurs "kurs_neu" eingetauscht werden soll.
    * 
    * Bei Bestätigung wird .initTausch in Stufe 3 aufgerufen.
    * @param {type} kurs
    * @param {type} kurs_neu
    * @returns {undefined}
    */
   function confirmTausch(kurs, kurs_neu){
       $('#modConfirmTausch .titel').html(kurs.titel);
       $('#modConfirmTausch .dozent').html(kurs.dozent_name);
       $('#modConfirmTausch .titel_neu').html(kurs_neu.titel);
       $('#modConfirmTausch .dozent_neu').html(kurs_neu.dozent_name);
       $('#cmdKursTauschConfirm').off().on('click', function(){initTausch(kurs, 3, kurs_neu);});
       $('#cmdKursTauschCancel').on('click', function(){$('#modConfirmTausch').modal('hide');});
       $('#modConfirmTausch').modal('show');
   }

   /**
    * Löscht die Wahl zum übergebenen Kurs "kurs"
    * nach Rückfrage.
    * (Im Unterschied zum Löschen während der Anmeldefrist
    * wird serverseitig ein Skript ausgeführt, das ggf. 
    * Wünsche erfüllt, die durch den Rücktritt erfüllbar 
    * geworden sind).
    * @param {type} kurs
    * @returns {undefined}
    */
   function dropKursAnmeldung(kurs){
       if(confirm('Wollen Sie ' + kurs.titel + ' wirklich löschen?')){
           var sending={};
           sending.kurstyp_id=kurs.kurstyp_id;
           sending.kurs_id=kurs.id;
           $.ajax({
               data:sending,
               url: 'json/student/signup/tausch/delete.jsp', //server script to process data
               type: 'POST',
               success: function(answer) {
                  $('#modTauschenOderLoeschen').modal('hide');
                  alert('Danke, dass Sie den Kursplatz freigegeben haben!');
                  initAnmeldungen(function(){_renderKurswahlListeTausch();}); // lazy ... 
               },
               error: function(a, b, c) {
                   alert('Fehler beim Versuch, einen Kursplatz zurückzugeben');
               },
               cache: false,
               timeout: 20000,
               contentType: "application/x-www-form-urlencoded;charset=utf8",
               dataType: 'json'
           });
       }
   }

   /**
    * Zentrales Management der Tauschfunktion:
    * Stadium 0:
    *   Rückfrage, ob Kursplatz gelöscht werden soll (->dropKursAnmeldung), 
    *   oder ob getauscht werden soll (-> Stadium 1)
    *   
    * Stadium 1:
    *   Oberfläche zur Auswahl des Wunsch-Kurses/Kurstyps wird eingeblendet
    *   
    * Stadium 2:
    *   Nicht besetzt.
    *   
    * Stadium 3:
    *   Tausch wird an json/student/signup/tausch/add.jsp gereicht.
    *   Insbesondere wird bei Unerfüllbarkeit (wegen Teilnehmerzahl-
    *   begrenzungen) eine Vormerkung gespeichert und auch gleich
    *   angezeigt.
    * @param {type} kurs
    * @param {type} iStage 1 für Rückfrage "Tauschen oder Löschen", 2 für Auswahl Kurse zum Tauschen, 3 für Tausch
    * @param {type} kurs_neu [nur bei iStage=3}
    * @returns {unresolved}         */
   function initTausch(kurs, iStage, kurs_neu){

       // 1. Tauschen oder löschen?
       if(iStage<1){
           $('#cmdKursAnmeldungSwapShowOptions').off().on('click', function(){initTausch(kurs, 1);});
           $('#cmdKursAnmeldungDelete').off().on('click', function(){dropKursAnmeldung(kurs);});

           $('#modTauschenOderLoeschen .kurs_titel').html(kurs.titel);
           $('#modTauschenOderLoeschen').modal('show');
           return;
       }

       // 2. Tauschen: Kurs zum Tauschen zur Auswahl stellen
       if(iStage===1){
           $('#modTauschenOderLoeschen').modal('hide');
           $('#divKurseTausch').empty();
           $('#divKurseTausch').removeData();
           $('#modTauschkursWahl .kurs_titel').html(kurs.titel);

           // Ggf. die Auswahlbox mit den vorhandenen Kurstypen füllen
           if($('#cboKurstypenKurswahlTausch option').length<=1)
               shj.signUp.toolbox.populateCbo($.signUpGlobal.kurstypen_kurswahl_tausch, {"identifyer":"id","text":"name","cboIdentifyer":"#cboKurstypenKurswahlTausch"});

           // Bei Auswahl eines Kurstyps eine Liste der 
           // eintauschbaren Kurse anzeigen
           $('#cboKurstypenKurswahlTausch').on('click',function(){
                   var sKursListe='';
                   var lKurstypID=$(this).val();
                   var tmpKurstyp=$.signUpGlobal.kurstypen.get(lKurstypID);
                   var sKursIDs=',' + tmpKurstyp.kurse.join(',') + ',';
                   var sKursIDSignedUp = getSignedUpKursIDs(lKurstypID);   // bereits gewählte Kurse
                   $('#divKurseTausch').empty();
                   $('#divKurseTausch').removeData();

                   //// (1) Fülle das Template mit zugehörigen Kursen
                   // Durchlaufe alle Kurse und füge diejenigen
                   // zur Liste hinzu, die dem gewählten Kurstyp
                   // zugeordnet sind
                   var template_kurs_general=$('#template_kursListe').clone();
                   var kurse=new Array();
                   template_kurs_general.removeAttr('id');

                   for(var ii=0;ii<$.signUpGlobal.kurse.kurse.length;ii++){
                       if(sKursIDs.indexOf(',' + $.signUpGlobal.kurse.kurse[ii].id + ',')>=0 && $.signUpGlobal.kurse.kurse[ii].erlaubt_kurswahl=='true'){         // Ist dem Kurstyp zugeordnet
                         if($.signUpGlobal.kurse.kurse[ii].zuschlag != 'true'){ // und nicht schon belegt
                           // Rendering:
                           var template_kurs=template_kurs_general.clone();
                           template_kurs.find('.template_kurs_titel').html($.signUpGlobal.kurse.kurse[ii].titel);
                           template_kurs.find('.template_kurs_termin').html($.signUpGlobal.kurse.kurse[ii].termin);
                           template_kurs.find('.template_kurs_dozent_name').html($.signUpGlobal.kurse.kurse[ii].dozent_name);
                           template_kurs.data('shj_item',$.signUpGlobal.kurse.kurse[ii]);

                           $('#divKurseTausch').append(template_kurs);//
                         }
                       }
                   }

                   $('#divKurseTausch').find('dl').on('click', function(){
                       $('#modTauschkursWahl').modal('hide');
                       var tmp = $(this).data("shj_item");
                       tmp.kurstyp_id=$('#cboKurstypenKurswahlTausch').val();
                       confirmTausch(kurs, tmp);
                   });
           });

           // Vorauswahl ist der Kurstyp des zu tauschenden Kurses
           $('#cboKurstypenKurswahlTausch option[value=' + kurs.kurstyp_id + ']').attr('selected', 'selected');
           $('#cboKurstypenKurswahlTausch').trigger('click');

           $('#modTauschkursWahl').modal('show');
       }

       // Drittes Statdium:
       // Der Server wird beauftragt, kurs mit kurs_neu 
       // zu tauschen.
       // Eine kontrollierte Fehlermeldung informiert 
       // z.B. darüber, dass ein Tausch momentan nicht 
       // möglich war, aber eine Vormeldung angelegt wurde.
       if(iStage==3){
           var sending={};
           sending.kurstyp_id=kurs.kurstyp_id;
           sending.kurs_id=kurs.id;
           sending.kurstyp_id_neu=kurs_neu.kurstyp_id;
           sending.kurs_id_neu=kurs_neu.id;
           $.ajax({
               data:sending,
               url: 'json/student/signup/tausch/add.jsp', //server script to process data
               type: 'POST',
               success: function(answer) {
                  if(answer.success=="true"){
                    $('#modTauschkursWahl').modal('hide');
                    $('#modTauschenOderLoeschen').modal('hide');
                    $('#modConfirmTausch').modal('hide');
                    initAnmeldungen(function(){                       
                           // Blende kurs "OK" ein und 
                           $('#modShj_SignUp_OK').modal('show').delay(1500).queue(function(next){
                             $('#modShj_SignUp_OK').modal('hide');
                             _renderKurswahlListeTausch(); //lazy.
                             next();
                           });
                     });
                  }else{
                    $('#modTauschkursWahl').modal('hide');
                    $('#modTauschenOderLoeschen').modal('hide');
                    $('#modConfirmTausch').modal('hide');
                    $('#modSpeichernERR').modal('show').find('.fehler_msg').text(answer.message);
                    initAnmeldungen(_renderKurswahlListeTausch);
                  }
               },
               error: function(a, b, c) {
                   alert('Fehler beim Versuch, einen Kursplatz zu tauschen');
               },
               cache: false,
               timeout: 20000,
               contentType: "application/x-www-form-urlencoded;charset=utf8",
               dataType: 'json'
           });
       }
   }

    /**
     * Übersicht der gewählten Kurse (zu diesem Kurstyp),
     * in denen der Studierende einen Platz bekommen hat.
     * 
     * Wird von _renderKurswahlListe aufgerufen und 
     * erstellt eine <ol> mit den gewählten Kursen
     * in der Reihenfolge der Priorisierung.
     * 
     * Zur Anzeige im Register "Tausch"
     * 
     * @param {type} lKurstypID
     * @returns nothing
     */
    function _renderKurseTausch(lKurstypID){
        var sKurstypenMitTauschoption = $('#kurstypen_mit_tauschoption').val();
        var sKurseMitTauschverbot = $('#kurse_mit_tauschverbot').val();

        // Array mit nach Priorität sortierten Wünschen
        var oAnmeldungenSortiert=getAnmeldungenWithNormalizedPriorities(lKurstypID);

        // Überschrift: welcher Kurstyp
        $('#Kurswahl_Tausch').append('<h4>' + $.signUpGlobal.kurstypen.get(lKurstypID).name + '</h4>');

        var list=$("<ul>");
        var iCount=0;

        // Durchlaufe die Anmeldungen und bilde
        // list-items mit Kurstitel, Kurszeit und 
        // Lehrender/m
        if(oAnmeldungenSortiert!==null){
           for(var ii=0;ii<oAnmeldungenSortiert.length;ii++){
                   var oListItem=$('#template_kurswahl_liste li').clone();
                       if(oAnmeldungenSortiert[ii].zuschlag!='true'){
                       }else{
                           oListItem.addClass('alert alert-success');
                           iCount++;
                       }
                   // Erweitere das Kurs-Objekt um die Eigenschaft 'kurstyp_id':
                   // das wird später für .initTausch() benötigt!
                   var tmp=oAnmeldungenSortiert[ii];
                   tmp.kurstyp_id=lKurstypID;
                   oListItem.find('.shj_titel').html('<a href="#">' + oAnmeldungenSortiert[ii].titel + '</a>');
                   oListItem.find('.shj_kurs_info').text(' (' + oAnmeldungenSortiert[ii].dozent_name + ', ' + oAnmeldungenSortiert[ii].termin + ')');
                   oListItem.data('shj_item', tmp);
                   oListItem.find('a').on('click', function(){initTausch($(this).parents('li').data('shj_item'), 0);}); //showKurs($(this).parents('li').data('shj_item'))
                   if(oAnmeldungenSortiert[ii].zuschlag=='true')
                       list.append(oListItem);
           }

           // Füge die Liste an:
           if(iCount==0) $('#Kurswahl_Tausch').append('<ul><li>Kein Kurs</li></ul>');
           else $('#Kurswahl_Tausch').append(list);
       }else{throw Exception('Fehler beim Sortieren der Anmeldungen');}
    }

    /**
    * löscht die Vormerkung vom Server 
    * (ohne erneute Rückfrage)
    * Die Vormerkung wird nur anhand des Wunschkurses und 
    * Wunschkurstyps identifiziert. (Das sollte auch reichen -- 
    * die Datenbank würde aber eine weitere Differenzierung nach 
    * dem aktuellen Kurs u. Kurstyp gestatten).
    * @param {type} vormerkung (enthält kurstyp_id, kurs_id, kurstyp_wunsch_id und kurs_wunsch_id).
    * @returns {undefined}          */
    function dropVormerkung(vormerkung){
       var sending={};
       sending.cmdDeleteVormerkung='yes';
       sending.kurstyp_id=vormerkung.kurstyp_wunsch_id;
       sending.kurs_id=vormerkung.kurs_wunsch_id;
           $.ajax({
               data:sending,
               url: 'json/student/signup/tausch/delete.jsp', //server script to process data
               type: 'POST',
               success: function(answer) {
                   initAnmeldungen(_renderKurswahlListeTausch); //lazy.a
               },
               cache: false,
               timeout: 20000,
               contentType: "application/x-www-form-urlencoded;charset=utf8",
               dataType: 'json'
           });
    }

   /**
    * Stellt die  Kurse im Register
    * "Tausch" dar.
    * 
    * Listet nur die Kurstypen mit 
    * #kurstypen_mit_tauschoption 
    * 
    * z.B.
    * Proseminar Literatur
    * ====================
    * 1. Lyrik (Di, Jakubzik)
    * 2. Drama (Do, Schloss)
    * 3. Prosa (Fr, Schnierer)
    */
    function _renderKurswahlListeTausch(){
       var sKurstypID=''; // sammelt bereits abgehandelte KurstypIDs.
       var sKurstypenMitTauschoption = $('#kurstypen_mit_tauschoption').val();
       var sKurseMitTauschverbot = $('#kurse_mit_tauschverbot').val();

        // Lösche die bisherige Anzeige
        $('#Kurswahl_Tausch').empty();

        // Durchlaufe die Anmeldungen und filtere DISTINCT
        // die KurstypIDs
        // Pro Kurstyp wird dann _renderKurse aufgerufen.
        for(var ii=0;ii<$.signUpGlobal.anmeldungen.length;ii++){
            if(sKurstypenMitTauschoption.indexOf(',' + $.signUpGlobal.anmeldungen[ii].kurstyp_id + ',')>=0 && sKurstypID.indexOf(';' + $.signUpGlobal.anmeldungen[ii].kurstyp_id + ';')<0){
                sKurstypID+=';' + $.signUpGlobal.anmeldungen[ii].kurstyp_id + ';';
                _renderKurseTausch($.signUpGlobal.anmeldungen[ii].kurstyp_id);
            }
        }

        // Vormerkungen auflisten
        var tmp=$('#template_kurswahl_vormerkungen').clone();
        var oKurs={};
        $('#divVormerkungen').empty();
        for(var ii=0;ii<$.signUpGlobal.vormerkungen.length;ii++){
            var tmp=$('#template_kurswahl_vormerkungen').clone();
            oKurs=$.signUpGlobal.kurse.get($.signUpGlobal.vormerkungen[ii].kurs_wunsch_id);
            tmp.find('.shj_titel_wunsch').html(oKurs.titel).end()
               .find('.shj_titel').html($.signUpGlobal.kurse.get($.signUpGlobal.vormerkungen[ii].kurs_id).titel).end()
               .find('.shj_dozent_name').text(oKurs.dozent_name).end()
               .find('a').data('shj_item', $.signUpGlobal.vormerkungen[ii])
               .on('click', function(){
                   dropVormerkung($(this).data("shj_item"));
               });
            $('#divVormerkungen').append(tmp);
        }
    }

    /**
     * Trigger: Klick aufs Register "Ergebnis/Übersicht"
     * löst die Anzeige der gewählten Kurse aus.
     */
    $('#cmdShowTausch').on('click', function(){_renderKurswahlListeTausch();});

/**
* =====================================================================
* (A) Register Kurs Auswahl
* =====================================================================
*/
    // Wird ein Kurstyp ausgewählt,
    // (1) füllt sich ... mit den zugehörigen Kursen,
    // (2) wird die Anzahl der zu wählenden Kurse angezeigt
   $('#cboKurstypenKurswahl').on('click', function(){

       // -----------------------------------------------
       // Zurücksetzen
       $('#divKurse').empty();
       $('#divKurseDropped').empty();
       $('.shj_showOnKurswahl').fadeIn();
       $('.shj_hideOnKurswahl').hide();

       // initialisieren
       var sKursListe='';
       var lKurstypID=$('#cboKurstypenKurswahl').val();
       var tmpKurstyp=$.signUpGlobal.kurstypen.get(lKurstypID);
       var sKursIDs=',' + tmpKurstyp.kurse.join(',') + ',';
       var sKursIDSignedUp = getSignedUpKursIDs(lKurstypID);

       //// (1) Fülle mit zugehörigen Kursen
       // Durchlaufe alle Kurse und füge diejenigen
       // zur Liste hinzu, die dem gewählten Kurstyp
       // zugeordnet sind
       var template_kurs_general=$('#template_kursListe').clone();
       var kurse=new Array();
       template_kurs_general.removeAttr('id');

       for(var ii=0;ii<$.signUpGlobal.kurse.kurse.length;ii++){
           if(sKursIDs.indexOf(',' + $.signUpGlobal.kurse.kurse[ii].id + ',')>=0 && $.signUpGlobal.kurse.kurse[ii].erlaubt_kurswahl=='true'){         // Ist dem Kurstyp zugeordnet
             if(sKursIDSignedUp.indexOf(',' + $.signUpGlobal.kurse.kurse[ii].id + ',')<0){ // und noch nicht gewählt
               // Rendering:
               var template_kurs=template_kurs_general.clone();
               template_kurs.find('.template_kurs_titel').html($.signUpGlobal.kurse.kurse[ii].titel);
               template_kurs.find('.template_kurs_termin').html($.signUpGlobal.kurse.kurse[ii].termin);
               template_kurs.find('.template_kurs_dozent_name').html($.signUpGlobal.kurse.kurse[ii].dozent_name);
               template_kurs.data('shj_item',$.signUpGlobal.kurse.kurse[ii]);

               // Einklinken in DOM und Initialisierung Drag'n'Drop
               $('#divKurse').append(template_kurs).find( ".draggable" ).draggable({ revert: "invalid" });
             }
           }
       }

       // Falls Kurse gewählt wurden,
       // Priorisierung fortlaufend machen
       var oAnmeldungenSortiert=null;
       if(sKursIDSignedUp.length >2 ){
           oAnmeldungenSortiert=getAnmeldungenWithNormalizedPriorities(lKurstypID);
       }

       // Als nächstes müssen unter (2), den droppables, 
       // die gewählten Kurse hinzugefügt werden.
       // Geladen sind sie schon.

       // (2) Zeige korrekte Anzahl der "droppables" Platzhalter an
       // -----------------------------------------------
       var sDroppables='';
       for(var jj=0;jj<tmpKurstyp.kurswahl_anzahl;jj++){
           sDroppables += '<div class="well muted droppable" id="wahlkurs-' + (jj+1) + '">' + (jj+1) + '. Wahl</div>';
       }

       // Klinke droppables Platzhalter in DOM ein,
       // und initialisiere 'on drop' event.
       $('#divKurseDropped').append(sDroppables).find(".droppable").droppable({
           accept:function(){
               return (typeof $(this).data('shj_item')==='undefined');
           },
           activeClass: "ui-state-hover",
           hoverClass: "ui-state-active",
           drop: function( event, ui ) {
             $( this )
               .addClass( "ui-state-highlight" )
               .data('shj_item', ui.draggable.data('shj_item'))
               .text( ui.draggable.data('shj_item').titel )
               .prepend('<a class="pull-right label label-important" title="Kurs aus Auswahl löschen"><i class="icon-remove"></i> </a>');
               ui.draggable.hide();
             var id=$(this).attr('id');
             $( this ).find('a').on('click', function(){revertKurs(id);});
             updateAnmeldung();
           }
         });

       // Belege die Platzhalter ggf. mit den
       // Kursen, die der Server als gewählt
       // gemeldet hat.
       if(oAnmeldungenSortiert!==null){
           // Durchlaufe die Kurse, zu denen Anmeldungen vorliegen...
           for(var ii=0;ii<oAnmeldungenSortiert.length;ii++){
                   var sID='wahlkurs-' + (oAnmeldungenSortiert[ii].prio_normal);
                   $('#' + sID)
                           .data('shj_item', oAnmeldungenSortiert[ii])
                           .text(oAnmeldungenSortiert[ii].titel)
                           .prepend('<a class="pull-right label label-important"><i class="icon-remove"></i> </a>')
                           .removeClass('muted')
                           .addClass('shj_SignUp_clean');
                   $('#' + sID).find('a').on('click', function(){revertKurs($(this).parents('div').attr('id'));});
           }
       }
       updatePopovers();
   });

/**
* =====================================================================
* (B) Register Übersichtsanzeige
* =====================================================================
*/
    /**
     * Übersicht der gewählten Kurse (zu diesem Kurstyp)
     * 
     * Wird von _renderKurswahlListe aufgerufen und 
     * erstellt eine <ol> mit den gewählten Kursen
     * in der Reihenfolge der Priorisierung.
     * 
     * Zur Anzeige im Register "Übersicht/Ergebnis"
     * 
     * @param {type} lKurstypID
     * @returns nothing
     */
    function _renderKurse(lKurstypID){

        // Array mit nach Priorität sortierten Wünschen
        var oAnmeldungenSortiert=getAnmeldungenWithNormalizedPriorities(lKurstypID);

        // Überschrift: welcher Kurstyp
        $('#Kurswahl_Liste').append('<h4>' + $.signUpGlobal.kurstypen.get(lKurstypID).name + '</h4>');


        var is_RESULT_MODE=$('#signup-is-result-mode').val();
        if(is_RESULT_MODE=='true')
            var list=$("<ul>");
        else
            var list=$("<ol>");

        // Für den Fall, dass es z.B. bei Proseminaren 
        // schon Ergebnisse gibt in diesem Kurstyp,
        // wird die Anzeige entsprechend modifiziert
        var has_ZUSCHLAG=false;
        for(var ii=0;ii<oAnmeldungenSortiert.length;ii++){
            if(oAnmeldungenSortiert[ii].zuschlag=='true'){
                has_ZUSCHLAG=true;
                break;
            }
        }
        
        // Durchlaufe die Anmeldungen und bilde
        // list-items mit Kurstitel, Kurszeit und 
        // Lehrender/m
        if(oAnmeldungenSortiert!==null){
           for(var ii=0;ii<oAnmeldungenSortiert.length;ii++){
                   var oListItem=$('#template_kurswahl_liste li').clone();
                   var bGreyOut=false;
                   if(is_RESULT_MODE=='true' || has_ZUSCHLAG){
                       if(oAnmeldungenSortiert[ii].zuschlag!='true'){
                           oListItem.addClass('muted');
                           bGreyOut=true;
                       }else{
                           oListItem.addClass('alert alert-success');
                       }
                   }
                   if(bGreyOut){
                       oListItem.find('.shj_titel').html(oAnmeldungenSortiert[ii].titel);
                   }else{
                       oListItem.find('.shj_titel').html('<a href="#">' + oAnmeldungenSortiert[ii].titel + '</a>');
                   }
                   oListItem.find('.shj_kurs_info').text(' (' + oAnmeldungenSortiert[ii].dozent_name + ', ' + oAnmeldungenSortiert[ii].termin + ')');
                   oListItem.data('shj_item', oAnmeldungenSortiert[ii]);
                   oListItem.find('a').on('click', function(){showKurs($(this).parents('li').data('shj_item'));});

                   list.append(oListItem);
           }

           // Füge die Liste an:
           $('#Kurswahl_Liste').append(list);
           $('#Kurswahl_Liste .muted').hide();
       }else{throw Exception('Fehler beim Sortieren der Anmeldungen');}
    }

   /**
    * Stellt die ausgewählten Kurse im Register
    * "Erebnis/Übersicht" dar.
    * 
    * z.B.
    * Proseminar Literatur
    * ====================
    * 1. Lyrik (Di, Jakubzik)
    * 2. Drama (Do, Schloss)
    * 3. Prosa (Fr, Schnierer)
    */
    function _renderKurswahlListe(){
        var sKurstypID=''; // sammelt bereits abgehandelte KurstypIDs.

        // Lösche die bisherige Anzeige
        $('#Kurswahl_Liste').empty();

        // Durchlaufe die Anmeldungen und filtere DISTINCT
        // die KurstypIDs
        // Pro Kurstyp wird dann _renderKurse aufgerufen.
        for(var ii=0;ii<$.signUpGlobal.anmeldungen.length;ii++){
            if(sKurstypID.indexOf(';' + $.signUpGlobal.anmeldungen[ii].kurstyp_id + ';')<0){
                sKurstypID+=';' + $.signUpGlobal.anmeldungen[ii].kurstyp_id + ';';
                _renderKurse($.signUpGlobal.anmeldungen[ii].kurstyp_id);
            }
        }
    }

    /**
     * Trigger: Klick aufs Register "Ergebnis/Übersicht"
     * löst die Anzeige der gewählten Kurse aus.
     */
    $('#cmdShowErgebnis').on('click', function(){_renderKurswahlListe();});

/**
* =====================================================================
* Registerübergreifende, allgemeine Fkt. mit Serverkontakt
* =====================================================================
*/
    /**
     * Lädt die Anmeldungen vom 
     * Server (student/signup/get) und 
     * legt sie auf 
     * $.signUpGlobal.anmeldungen
     * @returns {undefined}
     */
    function initAnmeldungen(fSuccess){
           $.ajax({
               url: 'json/student/signup/get.jsp', //server script to process data
               type: 'POST',
               success: function(answer) {
                  $.signUpGlobal.anmeldungen=answer.anmeldungen;
                  $.signUpGlobal.vormerkungen=answer.vormerkungen;
                  if(typeof fSuccess === 'function') fSuccess();
               },
               error: function(a, b, c) {
                   alert('Fehler beim Laden der Anmeldungen: ' + b + ' ' + c);
               },
               cache: false,
               timeout: 20000,
               contentType: "application/x-www-form-urlencoded;charset=utf8",
               dataType: 'json'
           });
    }

    /**
     * Es wurde ein Kurs vom Angebot ausgewählt: 
     * ist die Auswahl komplett (bzw. leer)? Falls ja, wird 
     * die Wahl auf den Server geladen und die 
     * lokale Anzeige eingestellt.
     * @returns {undefined}
     */
    function updateAnmeldung(){

        // Sind alle Platzhalter mit einem Kurs belegt?
        if($('.droppable').length !== $('.droppable a').length) return;

        // Erstelle die Parameter für den Server:
        // .kurstyp_id,
        // .kurs_id_WAHL = kurs_id [wobei WAHL=1 <=> Priorität 9; WAHL=2 <=> Priorität 8 ...
        var kurswahl={};
        kurswahl.kurstyp_id=$('#cboKurstypenKurswahl').val();
        for(var ii=1;ii<11;ii++){
            if($('#wahlkurs-' + ii).length){
                kurswahl["kurs_id_" + ii] = $('#wahlkurs-' + ii).data('shj_item').id;
            }else{
                break;
            }
        }

        // Rufe student/signup/update auf
        // und stelle im Erfolgsfall die 
        // Optik um
        $.ajax({
               url: 'json/student/signup/update.jsp', //server script to process data
               type: 'POST',
               success: function(answer) {
                 if(answer.success=="true"){
                   // Blende kurs "OK" ein und 
                   // markiere die Wahl als "clean"
                   $('#modShj_SignUp_OK').modal('show').delay(1500).queue(function(next){
                     $('#modShj_SignUp_OK').modal('hide');
                     $('#divKurseDropped div').removeClass().addClass('well shj_SignUp_clean');
                     next();
                   });
                   // Lade die Anmeldungen neu (that's lazy...)
                   initAnmeldungen();
                 }else{
                   // Es trat ein Fehler auf, z.B. war dieser Kurstyp
                   // bereits belegt, oder Voraussetzungen fehlten.
                   // Zeige Fehlermeldung an.
                   $('#modSpeichernERR').modal('show').find('.fehler_msg').text(answer.message);

                   // Setze die getroffene -- fehlerhafte -- 
                   // Auswahl zurück:
                   $('#cboKurstypenKurswahl').trigger('click');
                 }

               },
               error: function(a, b, c) {
                   alert('Fehler beim Speichern der Anmeldung: ' + b + ' ' + c);
               },
               // Form data
               data: kurswahl,
               cache: false,
               timeout: 20000,
               contentType: "application/x-www-form-urlencoded;charset=utf8",
               dataType: 'json'
           });
    }

/**
* =====================================================================
* Registerübergreifende, allgemeine Fkt. ohne Serverkontakt
* =====================================================================
*/

    /**
     * Zeigt den übergebenen Kurs (Titel, Beschreibung
     * und Name der/s Lehrenden) im Modalfenster an.
     * @param {type} kurs
     * @returns {undefined}
     */
    function showKurs(kurs){
        if(typeof kurs !== 'undefined'){
           $('#modKursDetails').find('.kurs_titel').html(kurs.titel);
           $('#modKursDetails').find('.kurs_beschreibung').html(kurs.beschreibung);
           $('#modKursDetails').find('.kurs_dozent_name').text(kurs.dozent_name);
           $('#modKursDetails').modal('show');
        }
    }

    /**
     * Im alten System konnten z.B. die Prioritäten 9,5,1 
     * vergeben werden. Hier müssen die Prioritäten für 
     * die Anzeige (1. Wahl, 2. Wahl, ..., n. Wahl) 
     * fortlaufend sein.
     * 
     * Diese Fkt. gibt Kurs-Objekte der im Kurstyp 
     * gewählten Kurse zurück, die die zusätzliche 
     * Eigenschaft .prio_normal haben, die von 1 fortlaufend 
     * aufwärts zählt.
     * 
     * Bsp: 
     * kurs[1].prioritaet === 9 --> .prio_normal === 1
     * kurs[2].prioritaet === 1 --> .prio_normal === 3
     * kurs[3].prioritaet === 4 --> .prio_normal === 2
     * 
     * @param {type} lKurstypID
     * @returns {Array} mit gewünschter Eigenschaft prio_normal
     */
    function getAnmeldungenWithNormalizedPriorities(lKurstypID){
        var tmp=new Array();

        // Extrahiere die Anmeldungen zu diesem Kurstyp
        for(var ii=0;ii<$.signUpGlobal.anmeldungen.length;ii++){
            if($.signUpGlobal.anmeldungen[ii].kurstyp_id == lKurstypID){
                var kurs_tmp=$.signUpGlobal.kurse.get($.signUpGlobal.anmeldungen[ii].kurs_id);
                kurs_tmp.zuschlag=$.signUpGlobal.anmeldungen[ii].zuschlag;
                tmp.push(kurs_tmp);
            }
        }

        // sortiere diese absteigend nach Priorität
        tmp.sort(function(a,b){return b.prioritaet - a.prioritaet;});

        // zähle fortlaufend
        for(var ii=0;ii<tmp.length;ii++) tmp[ii].prio_normal = (ii+1);
        return tmp;
    }

    /**
     * Gibt eine Komma-Liste der IDs von Kursen, 
     * die in diesem Kurstyp gewählt wurden
     * (zum Test 'ist Element von')
     * 
     * Voraussetzung: initAnmeldungen() ist schon aufgerufen.
     * @param {type} lKurstypID
     * @returns {String}
     */
    function getSignedUpKursIDs(lKurstypID){
        var tmp = new Array();
        for(var ii=0;ii<$.signUpGlobal.anmeldungen.length;ii++){
            if($.signUpGlobal.anmeldungen[ii].kurstyp_id == lKurstypID) tmp.push($.signUpGlobal.anmeldungen[ii].kurs_id);
        }
        return ',' + tmp.join(',') + ',';
    }

/**
* =====================================================================
* Langweiliges und Kleinkram
* =====================================================================
*/
    /**
     * Erneuere die Verknüpfung zum Anzeigen 
     * der Kursdetails beim Klicken
     * in den beiden Spalten "Auswahl" 
     * und "Angebot"
     * @returns {undefined}
     */
    function updatePopovers(){
        $('#divKurse').find('dl').on('click', function(){
           showKurs($(this).data('shj_item'));
        });

        $('#divKurseDropped').find('div').on('click', function(){
           showKurs($(this).data('shj_item'));
        });
    }

/**
* =====================================================================
* Drag'n'Drop, ACHTUNG: MIT SERVERKONTAKT
**/
    /**
     * Es wird 
     * (1) Die Information zum Kurs wieder links 
     *     zum "Angebot" hinzugefügt, und 
     * (2) Der Kurs aus der Auswahl gelöscht
     * (3) Falls es der letzte Kurs war, die 
     *     Anmeldung vom Server gelöscht.
     * @param {type} oKurs_id (id of DIV, e.g. 'wahlkurs-3')
     * @returns {Boolean}
     */
    function revertKurs(oKurs_id){
        // Erwecke Eindruck, als würde Kursliste neu geladen:
        // (durch Blinken)
        $('#divKurse').addClass('muted').delay(100).queue(function(next){
            $('#divKurse').removeClass('muted');
            next();
        });

        // (1) Füge den abgewählten Kurs links zu den 
        // wählbaren Optionen wieder hinzu
        var kurs=$('#' + oKurs_id).data('shj_item');
        var template_kurs=$('#template_kursListe').clone();
        template_kurs.removeAttr('id');
        template_kurs.find('.template_kurs_titel').html(kurs.titel);
        template_kurs.find('.template_kurs_termin').html(kurs.termin);
        template_kurs.find('.template_kurs_dozent_name').html(kurs.dozent_name);
        template_kurs.data('shj_item',kurs);
        $('#divKurse').prepend(template_kurs).find( ".draggable" ).draggable({ revert: "invalid" });

        // (2) Lösche abgewählten Kurs aus der Auswahl
        $('#' + oKurs_id)
           .text(oKurs_id.substring(oKurs_id.length-1) + '. Wahl')
           .removeClass()
           .removeData()
           .addClass('well muted droppable')
           .droppable({
               accept:function(){
                   return (typeof $(this).data('shj_item')==='undefined');
               },
               activeClass: "ui-state-hover",
               hoverClass: "ui-state-active",
               drop: function( event, ui ) {
                 $( this )
                   .addClass( "ui-state-highlight" )
                   .data('shj_item', ui.draggable.data('shj_item'))
                   .text( ui.draggable.data('shj_item').titel )
                   .prepend('<a class="pull-right label label-important" title="Kurs aus Auswahl löschen"><i class="icon-remove"></i> </a>');
                   ui.draggable.hide();
                 var id=$(this).attr('id');
                 $( this ).find('a').on('click', function(){revertKurs(id);});
               }
           });

         // Wenn es der letzte der ausgewählten Kurse
         // dieses Typs war, muss dieser Kurstyp 
         // serverseitig als "nix gewählt" markiert
         // werden
        if($('#divKurseDropped a').length===0){
           var kurswahl={};
           kurswahl.kurstyp_id=$('#cboKurstypenKurswahl').val();
           $.ajax({
               url: 'json/student/signup/delete.jsp', //server script to process data
               type: 'POST',
               //Ajax events
               // beforeSend: beforeSendHandler,
               success: function(answer) {
                 $('#modShj_SignUp_OK').modal('show').delay(1500).queue(function(next){
                   $('#modShj_SignUp_OK').modal('hide');
                   next();
               });
               // Lade die Anmeldungen neu (which is lazy).
               initAnmeldungen();
               },
               error: function(a, b, c) {
                   alert('Fehler beim Löschen der Anmeldung: ' + b + ' ' + c);
               },
               // Form data
               data: kurswahl,
               //Options to tell JQuery not to process data or worry about content-type
               cache: false,
               timeout: 20000,
               contentType: "application/x-www-form-urlencoded;charset=utf8",
               dataType: 'json'
           });
        }
        else{
           // wenn es nicht der letzte Kurs war, wird
           // aber der Zusatnd als 'dirty' markiert.
           $('#divKurseDropped .shj_SignUp_clean').removeClass('shj_SignUp_clean').addClass('shj_SignUp_dirty');
           updatePopovers();
        }

        return true;
    }

/**
* !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
* =====================================================================
* Initialisierung nach Laden der Seite
* =====================================================================
* !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
*/
   /**
    * Die SelectBox mit Kurstypen kann erst gefüllt
    * werden, wenn das Fach des eingeloggten Studierenden 
    * geladen ist. 
    * 
    * Diese Methode wird also nur beim ersten Laden 
    * aufgerufen und ist nur deshalb als Funktion 
    * gekapselt, um als Callback verfügbar zu sein.
    * 
    * @returns {undefined}         */
   function fillCboKurstypen(){
           // Fülle Kombinationsfeld mit den Kurstypen
           // die derzeit eine Kurswahl erlauben
           $.signUpGlobal.kurstypen = new shj.signUp.seminar.Kurstypen($.signUpGlobal.seminar_id, function(result){
               $.signUpGlobal.kurstypen_kurswahl=new Array();
               for(var ii=0; ii<result.kurstypen.length;ii++){
                   if(result.kurstypen[ii].kurswahl==='true' && isKurstypImStudiengang(result.kurstypen[ii].leistung_id)) $.signUpGlobal.kurstypen_kurswahl.push(result.kurstypen[ii]);
               }
               $.signUpGlobal.kurstypen_kurswahl_tausch=new Array();
               for(var ii=0; ii<result.kurstypen.length;ii++){
                   if(result.kurstypen[ii].tauschoption==='true' && isKurstypImStudiengang(result.kurstypen[ii].leistung_id)){
                       $.signUpGlobal.kurstypen_kurswahl_tausch.push(result.kurstypen[ii]);
                   }
               }
               shj.signUp.toolbox.populateCbo($.signUpGlobal.kurstypen_kurswahl, {"identifyer":"id","text":"name","cboIdentifyer":"#cboKurstypenKurswahl"});
           });
   }

   $( ".draggable" ).draggable({ revert: "invalid" });
   $( ".droppable" ).droppable({
     activeClass: "ui-state-hover",
     hoverClass: "ui-state-active",
     drop: function( event, ui ) {
       $( this )
         .addClass( "ui-state-highlight" )
         .text( ui.draggable.text() );
         ui.draggable.hide();
     }
   });

   /**
    * Ist die übergebene Leistung Teil des 
    * Studiengangs?
    * @param {type} lKurstypID
    * @returns {undefined}
    */
   function isKurstypImStudiengang(lLeistungID){
       // Ggf. initialisieren
       if($.signUpGlobal.info.seminar.mein_fach==null){
           $.signUpGlobal.info.seminar.mein_fach=$.signUpGlobal.info.seminar.faecher.get($.signUpGlobal.student_fach_id);
           $.signUpGlobal.info.seminar.meine_leistungen='';
           for(var ii=0;ii<$.signUpGlobal.info.seminar.mein_fach.module.length;ii++){
               for(var jj=0;jj<$.signUpGlobal.info.seminar.mein_fach.module[ii].modul.leistungen.length; jj++){
                   $.signUpGlobal.info.seminar.meine_leistungen += ';' + $.signUpGlobal.info.seminar.mein_fach.module[ii].modul.leistungen[jj].leistung.id + ';';
               }
           }
       }
       //if(lLeistungID==22) alert('EPG: 22 ist in: ' + $.signUpGlobal.info.seminar.meine_leistungen);
       return ($.signUpGlobal.info.seminar.meine_leistungen.indexOf(';' + lLeistungID + ';')>=0);
   }



   // Lade alle Kurse herunter
   $.signUpGlobal.kurse = new shj.signUp.seminar.Kurse($.signUpGlobal.seminar_id, function(result){
   });

   // Lade die Anmeldungen herunter:
   initAnmeldungen();

   $.signUpGlobal.matrikelnummer=$('#logged-in-user-matrikelnummer').val();
   $.signUpGlobal.seminar_id=$('#logged-in-user-seminar').val();
   $.signUpGlobal.student_fach_id=$('#logged-in-user-fach_id').val();
   $.signUpGlobal.info={};
   $.signUpGlobal.info.seminar = {};

   /**
    * Lade die Studiengänge des Seminars mit 
    * den Modularisierungen vom Server
    * @see json/seminar/fach/get.jsp
    * @see shj.signUp.seminar.Faecher
    */ 
   $.signUpGlobal.info.seminar.mein_fach=null;
   $.signUpGlobal.info.seminar.faecher = new shj.signUp.seminar.Faecher($.signUpGlobal.seminar_id, function(){
       fillCboKurstypen();
   });

   // Wenn die Kurswahl schon vorbei ist, 
   // entsprechende Nachricht einblenden
   if($('#signup-is-result-mode').val()=='true') $('#tab1').html('<h2>Kurswahl zur Zeit nicht möglich</h2>');
});

