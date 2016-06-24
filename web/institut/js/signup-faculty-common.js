/**
 * (c) shj 2013-15
 * 
 * Bibliothek mit allg. Utility-Methoden und 
 * den wesentlichen Objekten (also: 
 * Dozenten, Leistungen etc.), in die 
 * die Routen und Methoden für den 
 * Lebenszyklus (add, update, delete)
 * eingebaut sind.
 */
$(document).ready(function() {

    shj={};
    
    // Konstenten für die Ansichten der Register im Browser
    $.signUpGlobal = {
        "iVIEW_COURSE_PICKER":1,
        "iVIEW_STUDENT_DETAIL":2,
        "iVIEW_STUDENT_LEISTUNGEN":3,
        "iVIEW_STUDENT_ANMELDUNGEN":4,
        "iVIEW_STUDENT_STUDIENBILANZ":5,
        "iVIEW_STUDENT_PRUEFUNGEN":6,
        "iVIEW_STUDENT_ANTRAG":7,
        "iVIEW_STUDENT_ANTRAG_TRANSKRIPT_1":8,
        "iVIEW_STUDENT_TRANSKRIPTDRUCK":9,
        "iTYP_ANTRAG_TRANSKRIPT_DE":1,
        "iTYP_ANTRAG_TRANSKRIPT_EN":2,
        "iTYP_ANTRAG_TRANSKRIPT_UNMODULAR":3,
        "iTYP_ANTRAG_GYMPO_MISSING_CREDITS":4,
        "sLIFECYCLE_REROUTE_TESTING_PREFIX":''
    };

    var debugMode=false;
    
    window.log = function(sev, msg){
        var img=new Image();
        img.src="log.jsp?sev=" + encodeURIComponent(sev) + "&msg=" + encodeURIComponent(msg);
    };
    
    window.onerror = function(msg,url,line){
        log(1, "Fehler in Zeile " + line + " (" + url + "): " + msg);
        if(debugMode) return false;
        else return true;
    };
    
    // @TODO: es gibt eine Kopie von 
    // diesem Befehl innerhalb des Namespaces. 
    // eine von beiden muss wech.
    $.ajaxSetup({
        type: "POST",
        data:{"signup_expected_backend_version":'1-0-0-2'},
        timeout: 20000,
        contentType: "application/x-www-form-urlencoded;charset=utf8",
        dataType: 'json',
        cache: false,
        error: function(xhr,status,dings){
            // Achtung
            // 1. sollte die fehlerauslösende Seite (!)
            //    den contentType "text/json" haben
            //    (oder jedenfalls nicht HTML).
            // 2. sollte die fehlerauslösende Seite (!)
            //    keine Leerzeilen ausliefern (also 
            //    irrelevante Zeilenumbrüche für die 
            //    Lesbarkeit o.ä.
                //if(true==true) alert("Fehler: " + xhr.responseText);
                var msg=eval("(" + xhr.responseText.substring(xhr.responseText.indexOf('{')) + ")");
                if(msg.errorcode==1){
                        // alert("Hey, ein Fehler mit dem Code 1 ... seltsam");
                }else if (msg.errorcode==11){
                    // Falls das Login Studierender abgelaufen ist,
                    // wird auf die Login-Seite umgelenkt
                    document.location="./index-studierende.jsp";
                }else{
                        alert("Sorry, ein unerwarteter Fehler ist aufgetreten. Der Server meldet: \n\n" + msg.error);
                        //if(true==true) alert("Debug Info: " + msg.errorDebug);
                }
        }    
    });
    
    // ======================================================================
    // Global Utilities
    // ======================================================================
    //@deprecated
    window.explainMe=function(obj){
        alert("Es ist ein: " + obj);
        for(var propt in obj){
            alert(propt + ': ' + obj[propt]);
        }
    },
    
    shj.signUp=(function(){
        
        /**
         * Grundlegendes Objekt mit .get, .add, .update und .delete
         * Funktionen zum Erweitern durch spezifischere Objekte.
         * Durch die Konstruktion "=new ShjCoreObject(...)" entsteht 
         * kein Server-Kontakt.
         * 
         * Das Objekt muss bei der Konstruktion die Funktion 
         * .equals() -> true|false enthalten.
         *
         * Das Objekt wird primär als Grundklasse benutzt, von der 
         * die eigentlich interessanten Objekte (Dozent, Leistung 
         * etc.) abgeleitet sind.
         *
         * @param {type} name gemäß JSON/.../get.jsp Array, z.B. "bemerkung" in {bemerkungen:[{bemerkung:{...}...]}
         * @param {type} seminar_id
         * @param {type} oData enthält die Daten zur Initialisierung,  insbesondere:
         * - namespace (student|seminar, je nach lifecycle_url),
         * @param {type} url
         * @returns {undefined}
         */
        var ShjCoreObject=function(name, seminar_id, oData, url){
            if(arguments.length===0) return;
            this._name=name;
            this.namespace=oData.namespace;
            this.seminar_id=seminar_id;
            this.seminar_id_orig=seminar_id; // <-- CHECK, is this really needed?
            this.lifecycle_base_url=url;
            if(typeof oData !== 'undefined'){
                $.extend(this,oData);
            }
        };
        ShjCoreObject.prototype.add=function(fSuccess,fErr){
            lifecycle_act(this, 'add', fSuccess, fErr);
            return true;
        };
        ShjCoreObject.prototype.update=function(fSuccess,fErr){
            lifecycle_act(this, 'update', fSuccess, fErr);
            return true;
        };
        ShjCoreObject.prototype.drop=function(fSuccess,fErr){
            lifecycle_act(this, 'delete', fSuccess, fErr);
            return true;
        };
        
        /**
         * @oType Info zum Objekttyp der Sammlung, insbesondere:
         * .lifecycle_base_url,
         * ._name,
         * .identifier (für .get(?Val))
         * .constructor_name
         * .namespace relative to shj.signUp.
         * namespace (damit die Collection den Pfad für den Lifecycle findet),
         * @name: Name des Arrays, z.B. noten.
         */
        var ShjCoreCollection=function(seminar_id, name, oType, fSuccess){
            // ermögliche leeren Konstruktor:
            if(arguments.length===0) return;
            
            this.seminar_id=seminar_id;
            this._name=name;
            this.lifecycle_base_url=oType.lifecycle_base_url;
            this.namespace=oType.namespace;
            this.item_identifier=oType.identifier;
            this.item_constructor=oType.constructor_name;
            this.item_name=oType._name;
            var self=this;
            
            var collection=[];
            
            // Sammlungen können auch als _offline markiert 
            // werden; daraufhin wird beim Konstruieren kein 
            // Serverkontakt hergestellt.
            // (Bsp.: Module; hier wird das Array der Module 
            // mit dem Objekt Fach vom Server abgerufen).
            if(this.lifecycle_base_url==="_offline"){
                if (typeof fSuccess === "function") {fSuccess(self); return self;}
            }else{
                // Lade die Anmeldungen vom Server
                // und konstruiere das Array
                lifecycle_act(self, 'get', function(data){
                    for(var ii=0; ii<data[self._name].length; ii++){
                        collection[ii] = new shj.signUp[self.namespace][self.item_constructor](self.seminar_id,data[self._name][ii][self.item_name]);
                        collection[ii].namespace=self.namespace;
                    }
                    self[self._name]=collection;
                    if (typeof fSuccess === "function") {fSuccess(self); return self;}
                    return self;
                    }, function(){;});
            }
            return self;
         };
         
         /**
          * Löscht das Objekt "oType" (a) zuerst per Server aus der Datenbank, und 
          * (b) im Erfolgsfall aus der Collection.
          * 
          * Zur Identifikation des Objekts in der Collection wird die Methode 
          * .equals() der Objekte der Auflistung verwendet.
          * 
          * Bsp.:                 
          *  student.pruefungen.drop(
          *   		 oPruefung,
          *          function() {alert('OK: Prüfung gelöscht');},
          *          function() {alert('Sorry: Fehler');}
          *   );
          * @param oType
          * @param fSuccess
          * @param fErr
          * @returns {ShjCoreCollection}
          */
         ShjCoreCollection.prototype.drop=function(oType, fSuccess, fErr){

            var self=this;

            // Lösche die Anmeldung vom Server
            oType.drop(function(){
                // Nehme nach erfolgreicher Löschung
                // die Anmeldung aus dem Array
                for(var ii=0; ii<self[self._name].length; ii++){
                    //if(self[self._name][ii][self.item_identifier]==oType[self.item_identifier]){
                    if(self[self._name][ii].equals(oType)){
                        self[self._name].splice(ii, 1);
                        break;
                    }
                }
                // Führe ggf. die durchgereichte Fkt.
                // für den Erfolgsfall aus (z.B. 'render')
                if(typeof fSuccess === 'function') fSuccess();
                }, fErr);
            return this;        
        };
        
        ShjCoreCollection.prototype.add=function(seminar_id, oType, fSuccess, fErr){
            var self=this;

            var tmp=new shj.signUp[self.namespace][self.item_constructor](seminar_id, oType).add(
                function(data){
                    $.extend(oType,data[self.item_name]);
                    self[self._name].push(new shj.signUp[self.namespace][self.item_constructor](seminar_id,oType));
                    if(typeof fSuccess==='function') fSuccess(data);
                }
            ,fErr);
            return self;
        }; 
        
        ShjCoreCollection.prototype.get=function(sValue){
            if(!this.item_identifier) return null;
            var self=this;
            var oReturn={};
            for(var ii=0; ii<self[self._name].length; ii++){
//                window.console.log(this[self._name][ii][self.item_identifier]);
                if(this[self._name][ii][self.item_identifier]==sValue){                  
                //if(this[this._name][ii].equals(self)){
                    oReturn=self[self._name][ii];
                    break;
                }
            }
            return oReturn;
        };
        ShjCoreCollection.prototype.getByName=function(sValue){
            var self=this;
            var oReturn={};
            for(var ii=0; ii<self[self._name].length; ii++){
//                window.console.log(this[self._name][ii][self.item_identifier]);
                if(this[self._name][ii].name==sValue){                  
                //if(this[this._name][ii].equals(self)){
                    oReturn=self[self._name][ii];
                    break;
                }
            }
            return oReturn;
        };
        getRequestValue=function(name){
            if(name=(new RegExp('[?&]'+encodeURIComponent(name)+'=([^&]*)')).exec(location.search))
               return decodeURIComponent(name[1]);
        };
        
        // unbenutzt und ungetestet (Dec 23, 2013)
        getConstructorName = function(shj_object) { 
           var funcNameRegex = /function (.{1,})\(/;
           var results = (funcNameRegex).exec((shj_object).constructor.toString());
           return (results && results.length > 1) ? results[1] : "";
        };
        
    /**
     * Verallgemeinerung der Lifecycle-Methoden
     * @param {object} oSignUpItem
     * @param {String} sAction ist [add|update|delete|get]
     * @param {function} fSuccess Funktion, die nach erfolgreichem Serverkontakt ausgeführt wird
     * @param {function} fErr Funktion, die nach fehlgeschlagenem Serverkontakt ausgeführt wird
     */
    var lifecycle_act=function(oSignUpItem,sAction, fSuccess, fErr){
            // Entferne die Methoden aus 
            // dem übergebenen Objekt
            var tmp = {};
            for(var attr in oSignUpItem){
                if(attr!=='add' 
                        && attr !=='update' 
                        && attr !=='drop' 
                        && attr !=='get' 
                        && attr !=='namespace' 
                        && (typeof oSignUpItem[attr]!=='function') 
                        && (typeof oSignUpItem[attr]!=='object')){
                    tmp[attr]=oSignUpItem[attr];
                }
            }
            
            // Füge Pflichtparameter bei --
            // das ist ein @TODO: leider wird hier
            // $.ajaxSetup ignoriert. Warum?
            //oSignUpItem.signup_expected_backend_version='1-0-0-2';
            tmp.signup_expected_backend_version='1-0-0-2';
            
            // Die Basis URL für diesen Namespace
            if(oSignUpItem.lifecycle_base_url=='undefined'){
                throw Exception('Fehler im Lifecycle: kein URL definiert.');
            }
            
            // Damit asynchrones Testen vom 
            // Pfad server/test/ aus auch den
            // korrekten json-Pfad findet:
            // (Das Seminar wird über den 
            // Parameter shj_seminar überegeben).
            var sPath=window.location.protocol + '//' + window.location.host +  window.location.pathname;
            if(sPath.indexOf('institut')>=0)
                sPath=sPath.substring(0, sPath.indexOf('institut/') + 'institut'.length);
            else
                sPath=sPath.substring(0, sPath.indexOf('test')) + '/institut';

            var sURL = sPath + '/json/' + oSignUpItem.namespace + "/" + oSignUpItem.lifecycle_base_url+ '/' + sAction + ".jsp";
            $.ajax({
                 data: tmp,
                 url: sURL,
                 type: "POST",
                 success: function(data){
                     if (typeof fSuccess === "function") fSuccess(data);
                },
                timeout: 20000,
                contentType: "application/x-www-form-urlencoded;charset=utf8",
                dataType: 'json',
                cache: false,
                error: function(xhr,status,dings){
                    // Achtung
                    // 1. sollte die fehlerauslösende Seite (!)
                    //    den contentType "text/json" haben
                    //    (oder jedenfalls nicht HTML).
                    // 2. sollte die fehlerauslösende Seite (!)
                    //    keine Leerzeilen ausliefern (also 
                    //    irrelevante Zeilenumbrüche für die 
                    //    Lesbarkeit o.ä.
                        //if(true===true) alert("Fehler: " + xhr.responseText);
                        if (typeof fErr === "function"){
                            if(fErr($.parseJSON(xhr.responseText))===true)
                                return;
                        }
                        var msg=eval("(" + xhr.responseText.substring(xhr.responseText.indexOf('{')) + ")");
                        if(msg.errorcode==1){
                                // alert("Hey, ein Fehler mit dem Code 1 IM NAMESPACE (ACT) ... seltsam");
                                alert("Sorry -- ein fehler ist aufgetreten. Falls das Programm nicht weiter normal funktioniert, melden Sie sich bitte neu an.");
                        }else{
                            alert("Sorry -- ein fehler ist aufgetreten. Der Server meldet: \n" + msg.error + "\n\nFalls das Programm nicht weiter normal funktioniert, melden Sie sich bitte neu an.");
                        }
                } 
            });      
            return true;

        };
        return {ShjCoreObject:ShjCoreObject, 
            ShjCoreCollection:ShjCoreCollection,
            lifecycle_act:lifecycle_act};
    })();
    
    
    shj.signUp.search=(function(){
       // Siehe URL für Details
       // der anzuegebenden Parameter.
       /**
        * fOnEmpty: Funktion, die ausgeführt wird, wenn die 
        *           Suche kein Ergebnis bringt (optional),
        */
       var findKurse=function(oKurssuche, oRender, fOnEmpty){
           $.ajax({
                    data: oKurssuche,
                    url: 'json/kurs/get.jsp',
                    success: function(data){
                        // Mapping in Array für die Anzeige
                        var kursliste=[];
                        for(var key in data.kurse){
                            kursliste.push(data.kurse[key].kurs);
                         }
                         
                         if(kursliste.length<=0){
                            if(typeof fOnEmpty === 'function'){
                                fOnEmpty();
                            }
                         }else{
                            shj.signUp.toolbox.render(kursliste, oRender);
                         }
                    }
           });
           return this;
       };
       
       // Siehe URL für Details
       // der anzuegebenden Parameter.
       var findStudierende=function(oStudierende, oRender, fRender){
           $.ajax({
                    data: oStudierende,
                    url: 'json/student/get.jsp',
                    success: function(data){
                        // Mapping in Array für die Anzeige
                        var studierende=[];
                        for(var key in data){
                            studierende.push(data[key].student);
                         }
                        if(!$.isEmptyObject(oRender)) shj.signUp.toolbox.render(studierende, oRender);
                        if(typeof fRender==='function') fRender(data);
                    }
           });
           
           return this;
       };
       
          
       return{findKurse:findKurse, findStudierende:findStudierende};
    })();
    
    shj.signUp.toolbox= (function () {
        
        var isValidEmail=function(email) { 
            var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
            return re.test(email);
        };
        
        /**
         * #Hack für die Übergangszeit von unmodularen zu 
         * modularen Studiengängen in Heidelberg.
         * @param {type} iFachID_IN
         * @returns {Boolean}
         */
        var isUnmodularInHeidelberg=function (iFachID_IN){
            iFachID_IN=parseInt(iFachID_IN);
            switch(iFachID_IN){
                case 8351: 
                case 8361:
                case 8371:
                case 8352:
                case 8362:
                case 671:
                case 671:
                    return true;
                default:
                    return false;
            }
        };
        
       /**
         * sDate_IN muss im Format 2013-10-29
         * eingereicht werden; es gibt keinerlei 
         * Kontrolle des Formats.
         */
        var getGermanDate= function(sDate_IN){
            var dateParts = sDate_IN.split("-");
            return dateParts[2] + "." + (dateParts[1]) + "." + dateParts[0];
        };
        
         /**
         * sDate_IN muss im Format 10.3.1999
         * eingereicht werden; es gibt keinerlei 
         * Kontrolle des Formats.
         */
        var getInternationalDate= function(sDate_IN){
            if(typeof sDate_IN === 'undefined') return '';
            var dateParts = sDate_IN.split(".");
            var aMonths = ['Jan', 'Feb', 'March', 'Apr', 'May', 'June', 'July', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
            return aMonths[parseInt(dateParts[1])-1] + ' ' + parseInt(dateParts[0]) + ', ' + dateParts[2];
        };
        
        /**
         * sDate_IN muss im Format DD.MM.JJJJ
         * eingereicht werden; es gibt keinerlei 
         * Kontrolle des Formats.
         */
        var getDateFromGerman= function(sDate_IN){
            var dateParts = sDate_IN.split(".");
            return new Date(
                parseInt(dateParts[2]),
                parseInt(dateParts[1])-1,
                dateParts[0]
            );
        };
        
        // sSemesterName ist 'current' fürs aktuelle Semester, 
        // oder ssXXXX oder wsXXXA/XXXB
        //
        // Rückgabewert ist 28.2.ZZZZ oder 31.8.AAAA als String
        var getLastDayOfSemester= function(sSemesterName){
            var today = new Date();
            var sReturn = '#Err';
            if(sSemesterName === 'current'){
                var iMonth=parseInt(today.getMonth())+1;
                if(iMonth >2 && iMonth<9) sReturn='31.8.' + today.getFullYear();
                else{
                    if (iMonth==1) sReturn='28.2.' + today.getFullYear();
                    else sReturn='28.2.' + today.getFullYear()+1;
                }
            }else{
                // Sommersemester
                if(sSemesterName.indexOf('ss')==0){
                    sReturn = '31.8.' + sSemesterName.substr(2,4);
                }else{  
                // Wintersemester
                    sReturn = '28.2.' + sSemesterName.substr(7,4);
                }
            }
            return sReturn;
        };
        
        // Ziel: Zusammenfassung von shj_tabelizer, merge2Div etc.
        var render= function(object, options){
            try{
            if(typeof object.shj_rendering != 'undefined') options=object.shj_rendering;
            }catch(eIgnoreUndefinde){}
            if($(options.HTML_template)[0].tagName==='TR') return this.shj_tabelizer(object, options);
            if($(options.HTML_template)[0].tagName==='DL') return this.shj_tabelizer(object, options, true);
            if($(options.HTML_template)[0].tagName==='FORM'){return this.mergeToForm(object, options)};
            if($(options.HTML_template)[0].tagName==='DIV'){return this.mergeToDiv(object, options)};
        };
        
        // oOptions:{
        //  identifyer: Wert, der als ID der Optionen in der Cbo gesetzt werden soll.
        //  text: Wert, der als Text der Optionen in der Cbo gesetzt werden soll
        //  cboIdentifyer: Id der Cbo
        // }
        var populateCbo= function(oArray, oOptions){
            for(var ii=0; ii<oArray.length; ii++){
                $("<option/>").val(oArray[ii][oOptions.identifyer]).text(oArray[ii][oOptions.text]).appendTo(oOptions.cboIdentifyer);
            };       
        };
                
        // Hilfsfunktion für die Rückfrage: wirkilch löschen?
        // 
        // Das .div muss die Fkt .modal('show') und .modal('hide') beherrschen.
        // 
        // Setzt im div die Werte:
        // shj_SignUp_Confirm_Header (z.B. "Bemerkung löschen?")
        // shj_SignUp_Confirm_Body (z.B. "Die Bemerkung >ist ein Doofie< wirklich löschen?"
        // shj_SignUp_ConfirmAct_Cancel (fCancel),
        // shj_SignUp_ConfirmAct (fAct)
        var confirm= function(htmlDiv, sHeader, sQuestion, fAct, fCancel){
            $(htmlDiv).find(".shj_SignUp_Confirm_Header").text(sHeader);
            $(htmlDiv).find(".shj_SignUp_Confirm_Body").html(sQuestion);
            
            $(htmlDiv).find('.cmdShj_SignUp_ConfirmAct_Cancel').off();
            $(htmlDiv).find('.cmdShj_SignUp_ConfirmAct_Cancel').on('click', function(){
                fCancel();
                $(htmlDiv).modal('hide');
            });

            $(htmlDiv).find('.cmdShj_SignUp_Confirm_Act').off();
            $(htmlDiv).find('.cmdShj_SignUp_Confirm_Act').on('click', function(){
                fAct();
                $(htmlDiv).modal('hide');
            });
            
            // Blende alle vorhandenen Modals aus:
            $('.modal').modal('hide');
            
            // Blende spezifiziertes Modal ein:
            $(htmlDiv).modal('show');
        };
        
        // Hilfsfunktion für die Rückfrage: wirkilch löschen?
        // 
        // Das .div muss die Fkt .modal('show') und .modal('hide') beherrschen.
        // 
        // Setzt im div die Werte:
        // shj_SignUp_ConfirmDelete_Header (z.B. "Bemerkung löschen?")
        // shj_SignUp_ConfirmDelete_Body (z.B. "Die Bemerkung >ist ein Doofie< wirklich löschen?"
        // shj_SignUp_ConfirmDelete_Cancel (fCancel),
        // shj_SignUp_ConfirmDelete (fDelete)
        // @Deprecated: better use .confirm.
        var confirmDelete= function(htmlDiv, sHeader, sQuestion, fDelete, fCancel){
            $(htmlDiv).find(".shj_SignUp_ConfirmDelete_Header").text(sHeader);
            $(htmlDiv).find(".shj_SignUp_ConfirmDelete_Body").html(sQuestion);
            
            // Deaktiviere mögliche Reste früherer
            // Aufrufe.
            $('#cmdShj_SignUp_ConfirmDelete').off('click');
            $('#cmdShj_SignUp_ConfirmDelete_Cancel').off('click');
            
            // Blende alle vorhandenen Modals aus:
            $('.modal').modal('hide');
            
            // Blende spezifiziertes Modal ein:
            $(htmlDiv).modal('show');
            
            $('#cmdShj_SignUp_ConfirmDelete').on('click', function(){
                fDelete();
                $(htmlDiv).modal('hide');
            });
            
            $('#cmdShj_SignUp_ConfirmDelete_Cancel').on('click', function(){
                if (typeof fCancel === "function") fCancel();
                $(htmlDiv).modal('hide');
            });
        };
        
    // versucht, die Eigenschaften des Objekts
    // in einer HTML-Tabellenzeile zu setzen
    //
    // Dabei wird stets in HTML_row nach einem
    // DOM Objekt mit der Klasse gesucht, die 
    // der Eigenschaft des Objekts entspricht.
    // Optional kann ein Präfix für die HTML-Klasse
    // angegeben werden.
    //
    // Bsp: Objekt item={foo:bar, number:seven, letter:c}
    // HTML_row: <tr>
    //      <td class="test_foo"></td>
    //      <td class="test_number"></td>
    //      <td class="llletter">blabla</td></tr>
    //
    // Der Aufruf von 
    // .mergeToRow(HTML_row, item, 'test_')
    // verändert HTML_row zu
    //      <td class="test_foo">bar</td>
    //      <td class="test_number">seven</td>
    //      <td class="llletter">blabla</td></tr>
    //
    // Insbesondere bleibt 'llletter' unverändert.'
    // 
    // 
    //
    var mergeToRow=function(HTML_row, item, prefix){
        $(HTML_row).data('shj_item', item);
        $.each(item, function(key, value){
            if(typeof value !== 'function')
            $(HTML_row).find('.' + prefix + key).text(value);
        });
        return HTML_row;
    };
        
    // Bildet eine Objektsammlung als Tabelle(nzeilen) ab.
    // Es muss ein HTML_template vorhanden sein, 
    // und die Objekteigenschaften müssen zu Klassen
    // der Tabellenspalten korrelieren (s.a. mergeToRow).
    // Als "items" wird die Iterationsgrundlage 
    // mitgeliefert.
    // Die Optionen enthalten z.B.:
    //    {"item":"pruefung",
    //     "emptyFirst":true,
    //     "actOnDom":true,
    //     "stripPrefixOffId":false, [INEFFECTIVE IN TABELIZER]
    //     "HTML_template":"#template_pruefung",
    //     "HTML_table_container":"#divPruefungen",
    //     "HTML_template_class_prefix":'pruefung_'}); 
    //
    // Es wird dann
    // (1)  wg. 'replace_rows':true die vorhandene Tabelle 
    //      im HTML_table_container geleert.
    // (2)  über die items iteriert,
    // (2a) das HTML Template #template_pruefung geladen, 
    // (2b) alle Eigenschaften von items.pruefung (vgl item)
    //      gemäß .mergeToRow ersetzt
    // (3)  Falls actOnDom===true wird die die neue Zeile an die Tabelle angehängt.
    // 
    // In jedem Fall wird die Sammlung der Tabellenzeilen als Array zurückgegeben.
    // 
    var shj_tabelizer= function(items, options, bUseDLInsteadOfTable){
        if(options.emptyFirst===true){
            if(bUseDLInsteadOfTable!=true) $(options.HTML_table_container + ' tbody').find('tr').remove();
            else{
                $(options.HTML_table_container + ' dl').remove();
            }
        }
        var rows=new Array();
        var self=this;

        $.each(items, function(key, val) {
            var row = $(options.HTML_template).clone();

            self.mergeToRow(row, val, options.HTML_template_class_prefix);
            rows.push(row);
        });
        if(options.actOnDom!==false) {
            if(bUseDLInsteadOfTable!=true) $(options.HTML_table_container + ' tbody').append(rows);
            else {
               $(options.HTML_table_container).append('<dl/>');
               $(options.HTML_table_container + ' dl').append(rows);
           }
        }
        return rows;
    };
    
    /**
     * Bildet die Eigenschaften des Objekts "item" 
     * auf Elemente im Formular mit passenden Klassennamen ab.
     * 
     * Es werden input, textarea und select-Elemente 
     * berücksichtigt.
     * @param {object} item
     * @param {object} options {"item":"pruefung",
     *     "emptyFirst":true,
     *      "actOnDom":true,
     *      "stripPrefixOffId":false,
     *     "HTML_template":"#template_pruefung",
     *     "HTML_table_container":"#divPruefungen",
     *     "HTML_template_class_prefix":'pruefung_'}); 
     */
    var mergeToForm=function(item, options){
 
        var prefix=options.HTML_template_class_prefix;
        
        // Clean this form
        $(options.HTML_template).removeData();
        
        // Setze Inputs zurück,
        // entferne 'data',
        // setze Combo-Boxen zurück, falls gewünscht'
        if(options.emptyFirst==true){
            $(options.HTML_template + " input").val('').removeData();
            $(options.HTML_template + " select").attr('selected','');
        }
        
        $(options.HTML_template).data('shj_item', item);
        
        // Setze in allen "input"-Feldern die 
        // Eigenschaften des "item" mit 
        // entsprechendem Namen
        $.each($(options.HTML_template + " input"), function(key, value){
            var tmp=item[$(value).attr('id').substring(prefix.length)];
            if (typeof tmp !== "undefined") $(value).val(tmp);
        });
        
        // Setze in allen "textarea"-Feldern die 
        // Eigenschaften des "item" mit 
        // entsprechendem Namen
        $.each($(options.HTML_template + " textarea"), function(key, value){
            var tmp=item[$(value).attr('id').substring(prefix.length)];
            if (typeof tmp !== "undefined") $(value).val(tmp);
        });
        
        // Wähle in allen "select"-Feldern die 
        // Opion des "item" mit 
        // entsprechendem Namen
        $.each($(options.HTML_template + " select"), function(key, value){
            var tmp=item[$(value).attr('id').substring(prefix.length)];
            if (typeof tmp !== "undefined") $(value).val(tmp)
                $('#' + $(value).attr('id') + " [value='" + tmp + "']").attr('selected', 'selected');
        });        
        
        // Falls gewünscht, wird das Präfix aus den IDs
        // aller Kindelemente gelöscht.
        // So wird vermieden, dass Template und realisiertes
        // Formular gleiche IDs vergeben.
        if(options.stripPrefixOffId){
            // Iterate children recursively
            $(options.HTML_template).find('*').each(function(key){
               if($(this).is('input') || $(this).is('select')){
                try{$(this).attr('id', $(this).attr('id').substring(prefix.length));
                }catch(eWhatIgnoreIt){//alert('no -> ' + e + ", this is a " + $(this));
                }
               }
            });
        }
        return $(options.HTML_template);
    };
    
    /**
     * Bildet die Eigenschaften des Objekts "item" 
     * auf Elemente im Div mit passenden Klassennamen ab.
     * @param {object} item
     * @param {object} options {"item":"pruefung",
     *     "emptyFirst":true,
     *      "actOnDom":true,
     *      "stripPrefixOffId":false,
     *     "HTML_template":"#template_pruefung",
     *     "HTML_table_container":"#divPruefungen",
     *     "HTML_template_class_prefix":'pruefung_'}); 
     */
    var mergeToDiv=function(item, options){
 
        var prefix=options.HTML_template_class_prefix;
        
        // Clean this DIV
        $(options.HTML_template).removeData();
        $(options.HTML_table_container)
            .contents()
            .filter(function() {
              return this.nodeType == 3; //Node.TEXT_NODE
        }).remove();
        
        $(options.HTML_template).data('shj_item', item);
        
        // Setze in allen "input"-Feldern die 
        // Eigenschaften des "item" mit 
        // entsprechendem Namen
        $.each(item, function(key, value){
            if(typeof value !== 'function'){
                alert('Merging ' + prefix + key + ' with ' + value );
            $(options.HTML_template).find('.' + prefix + key).text(value);}
        });

        return $(options.HTML_template);
    };
    
    //
    /**
     * Stellt ein Objekt in HTML-DIV dar. Das DIV soll 
     * Elemente enthalten, die Klassen angehören, deren 
     * Namen dem Schema Präfix + Eigenschaft gehorchen.
     * 
     * Außerdem wird dem Div das item als "data" angehängt.
     * 
     * Beispiel
     * HTML_div="<div><span class="template_tuer"></span><span class="template_dach"></span>"
     * item={"auto":{"tuer":"rot","dach":"gelb"}}
     * 
     * renderDiv(HTML_div, item, "template_") 
     * 
     * -> <div><span class="template_tuer">rot</span><span class="template_dach">gelb</span>
     * 
     * @private
     * @param {DOM} HTML_div
     * @param {object} item Objekt, dessen Eigenschaften dargestellt werden sollen
     * @param {String} prefix Präfix der CSS Klassen
     * @returns {unresolved}
     */
    var renderDiv=function(HTML_div, item, prefix){
        $(HTML_div).data(item);
        $.each(item, function(key, value){
           $(HTML_div).find('.' + prefix +  key).html(value);
        });
        return HTML_div;
    };
    return {isValidEmail:isValidEmail,isUnmodularInHeidelberg:isUnmodularInHeidelberg,getGermanDate:getGermanDate,getInternationalDate:getInternationalDate,getDateFromGerman:getDateFromGerman,getLastDayOfSemester:getLastDayOfSemester,render:render,populateCbo:populateCbo,confirm:confirm,confirmDelete:confirmDelete,mergeToRow:mergeToRow,shj_tabelizer:shj_tabelizer,mergeToForm:mergeToForm,mergeToDiv:mergeToDiv,renderDiv:renderDiv};
    })();
    
    shj.signUp.kurstermine = (function () {

        /**
         * Analysiert eine Serie von Terminen (über ein Semester) und 
         * kategorisiert diese entweder als:
         * a) .periode="block",
         * b) .periode="woche", oder
         * c) .periode="halbwoche" (==zweimal pro Woche)
         * 
         * Das Rückgabe-Objekt enthält neben der Periode 
         * noch die Eigenschaften 
         * 
         * .abbrev (Kurzform d. Temrine, z.B. "Mo 11:15-12:45") sowie
         * .hint  (Debugging Informationen).
         * 
         * .periode aus {'woche'|'halbwoche'|'block'},
         * .abbrev (Kurzform wie z.B. "Mo 11.15-12.45"
         * .hint  (Klartext Info zum 'Guesswork')
         * 
         * Bsp.:
         * var oKurs={};
         * var oSemesterProp={"semesterStart": "2014-10-15", "semesterStop": "2015-2-15", "feiertage":[]};
         *
         * oKurs.sitzungen = getTermineKomplett(
         *         {"day": "mo", "start": "11:15", "stop": "12:45"}, 
         *         oSemesterProp);
         *         
         * console.log(guessPattern(oFeier.sitzungen).periode);
         * // -> 'woche'
         * 
         * console.log(guessPattern(oFeier.sitzungen).abbrev);
         * // -> 'Mo 11:15-12:45'
         * 
         * @param {type} aSitzungen
         * @returns {object}
         */
        var guessPattern = function (aSitzungen) {
            var oReturn = {};
            var aWocheShort = ['So', 'Mo', 'Di', 'Mi', 'Do', 'Fr', 'Sa'];
            oReturn.periode = 'block';
            oReturn.abbrev = 'unklar';
            oReturn.hint = '--';
            if (aSitzungen.length > 11)
                oReturn.periode = 'woche';
            if (aSitzungen.length > 18)
                oReturn.periode = 'halbwoche';
            if (aSitzungen.length > 38)
                oReturn.periode = 'block';

            // Die Anzahl der Termine deutet auf wöchentliches Angebot.
            // Überprüfe, ob die Start- und Stoppzeiten immer gleich 
            // sind, und ob der Termin immer am gleichen Wochentag 
            // stattfindet.
            // (Damit sind Ausnahmen hier irrelevant!).
            if (oReturn.periode == 'woche') {
                // (1) sind alle Termine am gleichen Wochentag,
                //     zur gleichen Start- und Stoppzeit?
                var sStart = getNormalTime(aSitzungen[0].start);
                var sStop = getNormalTime(aSitzungen[0].stop);

                var iDay=getDateFromISO(aSitzungen[0].datum).getDay();

                for (var ii = 0; ii < aSitzungen.length; ii++) {
                    if (getNormalTime(aSitzungen[ii].start) !== sStart || getNormalTime(aSitzungen[ii].stop) !== sStop) {
                        oReturn.periode = 'block';
                        oReturn.hint = 'Nicht wöchentlich, weil die ' + ii + '.te ' +
                                'Sitzung von ' + aSitzungen[ii].start + '-' + aSitzungen[ii].stop +
                                ' läuft, und nicht wie die erste von ' + sStart + '-' + sStop;
                    }

                    if (getDateFromISO(aSitzungen[ii].datum).getDay() != iDay) {
                        console.log('INFO: Tag abweichend: ' + aSitzungen[ii].datum + ' (Serienstart: ' + aSitzungen[0].datum + ')');
                        oReturn.periode = 'block';
                        oReturn.hint = 'Nicht wöchentlich, weil keine Woche zwischen dem ' + (ii - 1) + '.ten ' +
                                'und dem ' + (ii) + '.ten Termin liegt (' + aSitzungen[ii].datum + ' fällt aus der Rolle).';
                    }
                }
                if (oReturn.periode == 'woche')
                    oReturn.abbrev = aWocheShort[iDay] + ' ' + sStart.substring(0, 5) + '-' + sStop.substring(0, 5);
            }

            // Zweimal pro Woche
            if (oReturn.periode == 'halbwoche') {
                // Unterteile das Array in zwei
                // und prüfe jeweils auf Woche
                var aFirst = [];
                var aSecond = [];
                var iDay2;
                // Achtung, falls der zweite Termin in der ersten Woche ausfällt
                if (getDatumNaechsteWoche(aSitzungen[0].datum) == aSitzungen[1].datum) {
                    iDay2 =getDateFromISO(aSitzungen[2].datum).getDay();
                } else {
                    iDay2 = getDateFromISO(aSitzungen[1].datum).getDay();
                }

                var iDay = getDateFromISO(aSitzungen[0].datum).getDay();

                if (iDay === iDay2) {
                    oReturn.periode = 'block';
                    oReturn.hint = 'Sah zunächst aus wie halbwöchentlich, es gibt aber nur einen Tag anstatt zwei pro Woche';
                }

                for (var ii = 0, jj=aSitzungen.length; ii < jj; ii++) {
                    // Splitte die Terminserie auf in die (wöchtenlichen) 
                    // Termine der ersten Serie aFirst, und der zweiten aSecond
                    if (getDateFromISO(aSitzungen[ii].datum).getDay() === iDay)
                        aFirst.push(aSitzungen[ii]);
                    else if (getDateFromISO(aSitzungen[ii].datum).getDay() === iDay2)
                        aSecond.push(aSitzungen[ii]);
                    else {
                        oReturn.periode = 'block';
                        oReturn.hint = 'Sah zunächst aus wie halbwöchentlich, aber Termin Nr. ' + ii + ' am ' + aSitzungen[ii].datum + ' lässt sich nicht einordnen.';
                        break;
                    }
                }

                // Ist noch alles gut, oder haben sich 
                // inzwischen Indizien ergeben, die doch 
                // auf einen Blocktermin hinweisen?
                if (oReturn.periode == 'halbwoche') {
                    var oFirst = guessPattern(aFirst);
                    var oSecond = {};
                    if (oFirst.periode != 'woche') {
                        oReturn.periode = 'block';
                        oReturn.hint = 'Sah zunächst aus wie halbwöchentlich, aber der erste Termin findet nicht wöchentlich ' +
                                'statt, denn: >>' + oFirst.hint + '<<.';
                    } else {
                        oSecond = guessPattern(aSecond);
                        if (oSecond.periode != 'woche') {
                            oReturn.periode = 'block';
                            oReturn.hint = 'Sah zunächst aus wie halbwöchentlich, aber der zweite Termin findet nicht wöchentlich ' +
                                    'statt, denn: >>' + oSecond.hint + '<<.';
                        } else {
                            oReturn.abbrev = oFirst.abbrev + ', ' + oSecond.abbrev;
                        }
                    }
                }
            }

            return oReturn;
        };
         
        /**
         * Berechnet aus der Terminangabe oTermin
         * bzw. oTermin2 die einzelnen Termine 
         * im Semester mit Datum. 
         * 
         * Findet die Lehrveranstaltung einmal pro 
         * Woche statt, wird oTermin2 nicht übergeben; 
         * findet der Termin "halbwöchentlich" statt, 
         * also z.B. Mo 11:15-12:00 und Mi 14:15-15:00 Uhr,
         * ist oTermin1={"day":"Mo","start":"11:15","stop":"12:00"} 
         * und oTermin2={"day":"Mi","start":"14:15","stop":"15:00"}
         * 
         * Die Semestergrenzen sind dabei in 
         * oProperties festgelegt, ebenso die Feiertage.
         * 
         * oProperties muss die folgende Form haben:
         * .semesterStart=<Datum im ISO-Format>
         * .semesterStop=<Datum im ISO-Format>
         * .feiertage=['2014-1-5', '2015-2-5' ... ] (optional)
         * (ACHTUNG: keine führenden Nullen, also NICHT: 2015-02-05!)
         * 
         * Bsp.:
         * var oProperties={
         *          "semesterStart":"2014-10-15",
         *          "semesterStop":"2015-2-15",
         *          "feiertage":[]};
         *
         * oProperties.feiertage.push('2014-12-29');
         *
         * oTermine={};
         * oTermine.sitzungen = shj.signUp.kurstermine.getTermineKomplett(
         *      {"day": "mo", "start": "11:15", "stop": "12:45"}, 
         *      oProperties);
         * 
         * @param {type} oTermin
         * @param {object} oProperties
         * @returns {Array}
         */
        var getTermineKomplett=function(oTermin, oProperties, oTermin2){
            var aTermine=[];                            // Ergebnis
            var iWochentag=getDayCode(oTermin.day)+1;  
            oProperties.feiertage=oProperties.feiertage || '1969-1-1';
            var g_dSemesterStart=getDateFromISO(oProperties.semesterStart);
            var g_dSemesterStop=getDateFromISO(oProperties.semesterStop);
            var iDaysToAdd=0;

            // ---------------------------------------------------------
            // Finde den ersten Termin im Semester
            iDaysToAdd= iWochentag-g_dSemesterStart.getUTCDay();
            if(iDaysToAdd<0) iDaysToAdd+=7;
            var dFirstDateOfSemester=new Date(g_dSemesterStart.getTime()+ parseInt(iDaysToAdd * 24 * 60 * 60 * 1000));

            // ---------------------------------------------------------
            // Erstelle erstes "Terminobjekt" ...
            var dTermin1={};
            dTermin1.datum=dFirstDateOfSemester.getFullYear() + '-' + (dFirstDateOfSemester.getMonth()+1) + '-' + dFirstDateOfSemester.getDate();
            dTermin1.start=getNormalTime(oTermin.start);
            dTermin1.stop=getNormalTime(oTermin.stop);

            // ... und füge es dem Rückgabe-Array hinzu.
            aTermine.push(dTermin1);

            // Füge nun so lange jeweils eine Woche später 
            // einen neuen Termin hinzu, wie das Semester
            // läuft (höchstens aber 21 mal):
            var tmpDtm;
            var dLastDate=dFirstDateOfSemester;
            for(var ii=0;ii<20;ii++){
                var tmpoT={};
                tmpDtm=new Date(dLastDate.getTime()+ parseInt(7 * 24 * 60 * 60 * 1000));
                if(tmpDtm.getTime()>g_dSemesterStop.getTime())break;
                dLastDate=tmpDtm;
                tmpoT.datum=tmpDtm.getFullYear() + '-' + (tmpDtm.getMonth()+1) + '-' + tmpDtm.getDate();
                tmpoT.start=getNormalTime(oTermin.start);
                tmpoT.stop=getNormalTime(oTermin.stop);
                
                if(oProperties.feiertage.indexOf(tmpoT.datum)<0) aTermine.push(tmpoT);
            }
            
            // Bei zwei Terminen pro Woche wird oTermin2 
            // übergeben. Die Termine werden rekursiv hinzu-
            // gefügt, anschließend wird alles chronologisch
            // sortiert.
            if(arguments.length>2){
                var bTermine=getTermineKomplett(oTermin2, oProperties);
                aTermine=aTermine.concat(bTermine);
                aTermine.sort(function(a,b){return (getDateFromISO(a.datum).getTime() - getDateFromISO(b.datum).getTime());});
            }

            return aTermine;
        };
        var getDayCode=function(day){
            var lReturn=-1;
            day=day.toLowerCase();
            if(['mo', 'mon', 'montag', 'monday'].indexOf(day) >=0) lReturn=0;
            if(['di', 'tue', 'dienstag', 'tuesday'].indexOf(day) >=0) lReturn=1;
            if(['mi', 'wed', 'mittwoch', 'wednesday'].indexOf(day) >=0) lReturn=2;
            if(['do', 'thu', 'donnerstag', 'thursday'].indexOf(day) >=0) lReturn=3;
            if(['fr', 'fri', 'freitag', 'friday'].indexOf(day) >=0) lReturn=4;
            if(['sa', 'sam', 'samstag', 'saturday'].indexOf(day) >=0) lReturn=5;
            if(['so', 'sun', 'sonntag', 'sunday'].indexOf(day) >=0) lReturn=6;
            return lReturn;
        };
         var getDatumNaechsteWoche=function(sDatum){
             var dLastDate=getDateFromISO(sDatum);
             tmpDtm=new Date(dLastDate.getTime()+ parseInt(7 * 24 * 60 * 60 * 1000));
             return tmpDtm.getFullYear() + '-' + (withLeadingZero(tmpDtm.getMonth()+1)) + '-' + withLeadingZero(tmpDtm.getDate());
         };
         withLeadingZero=function (sIn){
             if(parseInt(sIn)<10) return '0' + parseInt(sIn);
             else return sIn;
         };
         getDateFromISO=function(sIn){
             var tmp=sIn.split('-');
             return new Date(Date.UTC(tmp[0],parseInt(tmp[1])-1, parseInt(tmp[2])));
         };
         getNormalTime=function(sIn){
             // 10:00:00 -> 10:00
             if(sIn.length>5) return sIn.substring(0,5);
             return sIn;
         };
         return {guessPattern:guessPattern, getTermineKomplett:getTermineKomplett, getDayCode:getDayCode};
    })();
    
    /**
     * @namespace seminar
     */
    shj.signUp.seminar=(function(){
        /**
         * Erstelle Objekt Information, z.B.:
         * 
         * var info=new shj.signUp.seminar.Information(1, {
         *    "inhalt": "Nicht vergessen: Examensfeier am 18.7. um 17 Uhr.",
         *    "autor": "Heiko Jakubzik",
         *    "id": "12",
         *    "timestamp": "2014-6-1 19.37.00.123123123"
         * }
         * 
         * oder:
         * 
         * var info=new shj.signUp.seminar.Information(1);
         * 
         * Hat die Methoden
         * .update
         * .delete
         * .add
         * .equals  [liefert wahr, falls info_a.id===info_b.id
         * 
         * Hat optional das Array "kommentare":[]
         * 
         * @constructor der Note
         * @param {long} seminar_id Id des Seminars
         * @param {object} note Eigenschaften der Note
         */
        var Information = function(seminar_id, info){
            if(arguments.length===0) return;
            this.seminar_id_orig=seminar_id;
            this.lifecycle_base_url="information";
            if (typeof info !== 'undefined'){
                $.extend(this,info);
            }
            this.equals=function(obj){return this.id===obj.id;};
            shj.signUp.ShjCoreObject.call(this, "Information", seminar_id, {"namespace":"seminar"}, "information");
        };
        Information.prototype = new shj.signUp.ShjCoreObject();
        Information.prototype.kommentiere=function(
                sKommentar,fSuccess,fErr){
            this.sonderfkt='Kommentar';
            this.kommentar=sKommentar;
            this.add(fSuccess, fErr);
        };
        
        /**
         * Lädt die Noten des Seminars vom Server, z.B. :
         * var noten = new shj.signUp.seminar.Noten(g_SEMINAR_ID, function(data){
         *      alert('Anzahl der Noten: ' + data.noten.length);
         *      alert(data.noten[1].name + ' Bestanden? -> ' + data.noten[1].bestanden;
         * });
         * 
         * @constructor der Noten
         * @param {long} seminar_id Id des Seminars
         * @param {function} fSuccess wird nur nach Erfolgsmeldung vom Server ausgeführt
         * @param {long} info_after: nur Informationen bzw. Kommentare nach 'info_after' Millisek. abrufen.
         */
        var Informationen = function(seminar_id, fSuccess, info_after){
            this.seminar_id=seminar_id;
            this.lifecycle_base_url="informationen";
            if(typeof info_after !== 'undefined') this.info_after=info_after;
            shj.signUp.ShjCoreCollection.call(this, seminar_id, "informationen", 
                    {"lifecycle_base_url":"information",
                        "_name":"information",
                        "item_identifier":"id",
                        "identifier":"id",
                        "constructor_name":'Information',
                        "namespace":'seminar'
                    }, fSuccess);
        };
        Informationen.prototype = new shj.signUp.ShjCoreCollection();

        
        /**
         * Erstelle Objekt Note, z.B.:
         * 
         * var note=new shj.signUp.seminar.Note(1, {
         *    "name": "Unbenotet (bestanden)",
         *    "bestanden": "true",
         *    "wert": "0.0",
         *    "id": "13"
         * }
         * 
         * oder:
         * 
         * var note=new shj.signUp.seminar.Note(1);
         * 
         * Hat die Methoden
         * .update
         * .delete
         * .add
         * .equals  [liefert wahr, falls note_a.id===note_b.id
         * @constructor der Note
         * @param {long} seminar_id Id des Seminars
         * @param {object} note Eigenschaften der Note
         */
        var Note = function(seminar_id, note){
            if(arguments.length===0) return;
            this.seminar_id_orig=seminar_id;
            this.lifecycle_base_url="note";
            if (typeof note !== 'undefined'){
                $.extend(this,note);
            }
            this.equals=function(obj){return this.id===obj.id;};
            shj.signUp.ShjCoreObject.call(this, "Note", seminar_id, {"namespace":"seminar"}, "note");
        };
        Note.prototype = new shj.signUp.ShjCoreObject();
        
        /**
         * Lädt die Noten des Seminars vom Server, z.B. :
         * var noten = new shj.signUp.seminar.Noten(g_SEMINAR_ID, function(data){
         *      alert('Anzahl der Noten: ' + data.noten.length);
         *      alert(data.noten[1].name + ' Bestanden? -> ' + data.noten[1].bestanden;
         * });
         * 
         * @constructor der Noten
         * @param {long} seminar_id Id des Seminars
         * @param {function} fSuccess wird nur nach Erfolgsmeldung vom Server ausgeführt
         */
        var Noten = function(seminar_id, fSuccess){
            this.seminar_id=seminar_id;
            this.lifecycle_base_url="note";
            shj.signUp.ShjCoreCollection.call(this, seminar_id, "noten", 
                    {"lifecycle_base_url":"note",
                        "_name":"note",
                        "item_identifier":"id",
                        "identifier":"id",
                        "constructor_name":'Note',
                        "namespace":'seminar'
                    }, fSuccess);
        };
        Noten.prototype = new shj.signUp.ShjCoreCollection();
        
        // ##############################################################
        // Dozenten
        // (add ist serverseitig 
        // noch nicht implementiert)
        // ##############################################################
        
        /**
         * @constructor ohne Serverkontakt
         * var dozent=new shj.signUp.seminar.Dozent(1);
         * 
         * oder
         * 
         * var dozent=new shj.signUp.seminar.Dozent(1, {"dozent":{"name":"Jakubzik","vorname":"Heiko","email":"heiko.jakubzik@shj-offline.de","id":"380"}});
         * dozent.email='heiko.jakubzik@shj-online.de';
         * dozent.update(function(data){
         *       alert('SUCCESS!');
         * });
         * @param {long} seminar_id Id des Seminars
         * @param {object} dozent Objekt mit Eigenschaften des Dozenten,
         * z.B.: {"dozent":{"name":"Jakubzik","vorname":"Heiko","email":"heiko.jakubzik@shj-online.de","id":"380"}}
         */
        var Dozent = function(seminar_id, dozent){
            if(arguments.length===0) return;
            this.seminar_id_orig=seminar_id;
            this.lifecycle_base_url="dozent";
            if (typeof dozent !== 'undefined'){
                $.extend(this,dozent);
            }
            this.equals=function(obj){return this.id===obj.id;};
            shj.signUp.ShjCoreObject.call(this, "Dozent", seminar_id, {"namespace":"seminar"}, "dozent");
        };
        Dozent.prototype = new shj.signUp.ShjCoreObject();
        
        /**
         * Lädt die DozentInnen des Seminars vom Server in ein Array.
         * Bsp.:
         * dozenten = new shj.signUp.seminar.Dozenten(g_SEMINAR_ID, function(data){
         *    alert('Es gibt am Seminar ' + data.dozenten.length + ' Lehrende.');
         *    alert('Der Dozent mit ID 26 heißt: ' + data.get(26).name);
         *    
         *    var d26=data.get(26);
         *    d26.email='newemail@home.de';
         *    d26.update(function(){alert('OK');});
         * });
         * @constructor mit Serverkontakt
         * @param {long} seminar_id Id des Seminars
         * @param {function} fSuccess wird nur nach Erfolgsmeldung vom Server ausgeführt
         */
        var Dozenten = function(seminar_id, fSuccess){
            this.seminar_id=seminar_id;
            this.lifecycle_base_url="dozent";

            shj.signUp.ShjCoreCollection.call(this, seminar_id, "dozenten", 
                    {"lifecycle_base_url":"dozent",
                        "_name":"dozent",
                        "identifier":"id",
                        "constructor_name":'Dozent',
                        "namespace":'seminar'
                    }, fSuccess);
        };
        Dozenten.prototype=new shj.signUp.ShjCoreCollection();

        var DozentTermin = function(seminar_id, termin){
            if(arguments.length===0) return;
            console.warn(termin);
            this.seminar_id_orig=seminar_id;
            this.lifecycle_base_url="dozent/termin";
            if (typeof termin !== 'undefined'){
                $.extend(this,termin);
            }
            this.equals=function(obj){return this.id===obj.id;};
            shj.signUp.ShjCoreObject.call(this, "DozentTermin", seminar_id, {"namespace":"seminar"}, "dozent/termin");
        };
        DozentTermin.prototype = new shj.signUp.ShjCoreObject();

        var DozentTermine = function(seminar_id, fSuccess){
            this.seminar_id=seminar_id;
            this.lifecycle_base_url="dozent/termin";

            shj.signUp.ShjCoreCollection.call(this, seminar_id, "dozentTermine", 
                    {"lifecycle_base_url":"dozent/termin",
                        "_name":"dozentTermin",
                        "identifier":"id",
                        "constructor_name":'DozentTermin',
                        "namespace":'seminar'
                    }, fSuccess);
        };
        DozentTermine.prototype=new shj.signUp.ShjCoreCollection();
        

        // ##############################################################
        // Fach und Fächer
        // (add, update, drop ist serverseitig 
        // noch nicht implementiert)
        // FACH enthält MODULE
        // @todo: prototype.dropModule, .addModule, .updateModule
        // ##############################################################
        
        /**
         * Fach bzw. Studiengang eines 
         * Instituts. Zusammen mit dem Namen und der Id 
         * eines Fachs werden auch die Modularisierungen 
         * mitgeliefert.
         * 
         * Die Konstruktion sollte über Faecher erfolgen. 
         * Dieser Konstruktor beherrscht keinen Serverkontakt 
         * zum Holen von Informationen des Fachs.
         * 
         * Man kann das Fach-Objekt also beispielsweise
         * folgendermaßen iterieren:
         * 
         * // Nested:
         * // Fach -
         * //      |
         * //      - Module -
         * //               |
         * //               - Leistungen
         * oFach=new de.shj.UP.Fach(1, fachobjekt);
         * 
         * for (var jj = 0; jj < oFach.module.length; jj++) {
         *     for (var kk = 0; kk < oFach.module[jj].leistungen.length; kk++) {
         *        Console.log(oFach.module[jj].leistungen[kk].leistung.name);
         *     }
         * }
         * 
         * @see json/seminar/fach/get.jsp
         * @see shj.signUp.seminar.Faecher
         * @constructor ohne [1] Serverkontakt.
         * @param {long} seminar_id
         * @param {function} fSuccess wird nur nach Erfolgsmeldung vom Server ausgeführt
         */
        var Fach = function(seminar_id, fach){
            if(arguments.length===0) return;
            this.seminar_id_orig=seminar_id;
            this.lifecycle_base_url="fach";
            this.hasModule=false;
            
            if(typeof fach.module != 'undefined'){
                this.hasModule=true;
                var self=this;
                self.module=new Module(seminar_id, fach.module);
            }
            if (typeof fach !== 'undefined'){
                $.extend(this,fach);
            }
            this.equals=function(obj){return this.id===obj.id;};
            shj.signUp.ShjCoreObject.call(this, "Fach", seminar_id, {"namespace":"seminar"}, "fach");
        };
        Fach.prototype=new shj.signUp.ShjCoreObject();
                
        /**
         * Sammlung der Fächer bzw. Studiengänge eines 
         * Instituts. Zusammen mit dem Namen und der Id 
         * eines Fachs werden auch die Modularisierungen 
         * im JSON mitgeliefert. 
         * 
         * Man kann das Faecher-Objekt also beispielsweise
         * folgendermaßen iterieren:
         * 
         * // Nested:
         * // Fach -
         * //      |
         * //      - Module -
         * //               |
         * //               - Leistungen
         * oFacher=new de.shj.UP.Faecher(1);
         * 
         * for (var ii = 0; ii < oFacher.fach.length; ii++) {
         *   for (var jj = 0; jj < oFacher.fach[ii].module.length; jj++) {
         *     for (var kk = 0; kk < oFacher.fach[ii].module[jj].leistungen.length; kk++) {
         *        Console.log(oFaecher.fach[ii].module[jj].leistungen[kk].leistung.name);
         *     }
         *   }
         * }
         * 
         * @see json/seminar/fach/get.jsp
         * @see shj.signUp.seminar.Fach
         * @constructor mit Serverkontakt.
         * @param {long} seminar_id
         * @param {function} fSuccess wird nur nach Erfolgsmeldung vom Server ausgeführt
         */
        var Faecher = function(seminar_id, fSuccess){
            this.seminar_id=seminar_id;
            this.lifecycle_base_url="fach";
            var fach=[];

            shj.signUp.ShjCoreCollection.call(this, seminar_id, "faecher", 
                    {"lifecycle_base_url":"fach",
                        "_name":"fach",
                        "identifier":"id",
                        "constructor_name":'Fach',
                        "namespace":'seminar'
             }, fSuccess);
        };
        Faecher.prototype=new shj.signUp.ShjCoreCollection();

        /**
         * (Distinct) Liste der Leistungen in diesem Seminar als 
         * Array in alphabetischer Reihenfolge
         * @returns {Array}
         */
        Faecher.prototype.getLeistungen=function(){
            if(typeof this.leistungenArr !== 'undefined') return this.leistungenArr;
            var oReturn=[];
            var tmp = ';';
            var lst = [];
            for (var ii = 0; ii < this.faecher.length; ii++) {
                for (var jj = 0; jj < this.faecher[ii].module.length; jj++) {
                    for (var kk = 0; kk < this.faecher[ii].module[jj].modul.leistungen.length; kk++) {
                        if (tmp.indexOf(';' + this.faecher[ii].module[jj].modul.leistungen[kk].leistung.id + ';') < 0) {
                            tmp += this.faecher[ii].module[jj].modul.leistungen[kk].leistung.id + ';';
                            lst.push(this.faecher[ii].module[jj].modul.leistungen[kk].leistung.name);
                            oReturn.push(this.faecher[ii].module[jj].modul.leistungen[kk].leistung);
                        }
                    }
                }
            }

            // Sortiere
            oReturn.sort(function(a, b){
                  var nameA=a.name.toLowerCase(), nameB=b.name.toLowerCase();
                  if (nameA < nameB) 
                   return -1;
                  if (nameA > nameB)
                   return 1;
                  return 0;
            });
            this.leistungenArr=oReturn;
            return oReturn;
        };
        
        // ##############################################################
        // Modul
        // (add, update, drop ist serverseitig 
        // noch nicht implementiert)
        // ##############################################################
        
        /**
         * Modul des Fachs (das wiederum jeweils eine Sammlung von 
         * Leistungen enthält).
         * @todo serverseitig ist noch kein Lifecycle implementiert.
         */
        var Modul = function(seminar_id, modul){
            if(arguments.length===0) return;
            this.seminar_id_orig=seminar_id;
            this.lifecycle_base_url="seminar/fach/modul";
            var self=this;
            var leistungen=[];
            for(var ii=0; ii<modul.leistungen.length; ii++){
                leistungen[ii] = new Leistung(self.seminar_id,modul.leistungen[ii].leistung);
            }
            self.leistungen=leistungen;

            if (typeof modul !== 'undefined'){
                $.extend(this,modul);
            }
            this.equals=function(obj){return this.id===obj.id;};
            shj.signUp.ShjCoreObject.call(this, "Modul", seminar_id, {"namespace":"seminar"}, "modul");
        };
        Modul.prototype = new shj.signUp.ShjCoreObject();

        /**
         * Initialisiert die Auflistung der Module (mit den Leistungen) 
         * des Fachs (bzw. Studiengangs).
         * 
         * Die "module" werden derzeit als JSON vom Fach mit 
         * geliefert. Diese JSON Modul-Objekte enthalten aber 
         * keine Funktionalität zum Lifecycle. Die wird hier 
         * bei der Konstruktion nachgeliefert.
         * 
         * @see json/seminar/fach/get.jsp
         * @see shj.signUp.seminar.Fach
         * @constructor kein [!] Serverkontakt.
         * @param {type} seminar_id
         * @param {Array} module 
         * @param {function} fSuccess wird nur nach Erfolgsmeldung vom Server ausgeführt
         * @returns {_L5._L641.Module.prototype}
         */
        var Module = function(seminar_id, module, fSuccess){
            this.seminar_id=seminar_id;
            this.lifecycle_base_url="_offline";
            shj.signUp.ShjCoreCollection.call(this, seminar_id, "module", 
                    {"lifecycle_base_url":"_offline",
                        "_name":"modul",
                        "identifier":"id",
                        "constructor_name":'Modul',
                        "namespace":'seminar'
                    }, fSuccess);
            if (typeof fSuccess === "function") {fSuccess(this); return this;}        
        };
        Module.prototype = new shj.signUp.ShjCoreCollection();
        
        // ##############################################################
        // Leistung
        // ##############################################################
        /**
         * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
         *  Leistungen laden
         *  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
         *  leistungen = new shj.signUp.seminar.Leistungen(g_SEMINAR_ID, function(data){
         *      console.log('...es gibt ' + data.leistungen.length + ' Leistungen');
         *  });
         *
         *  leistung1=leistungen.get(200);
         *  
         *  console.log(leistung1.name);
         *  console.log(leistung1.name_en);
         *  
         *  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
         *  Leistung ändern
         *  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
         *  leistung1.code='shj-1';
         *  leistung1.update(function(){alert('Updated!');});
         *  
         *  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
         *  Leistung hinzufügen
         *  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
         *  var leistung_neu={};
         *  
         *  leistung_neu.name='Neue Leistung';
         *  leistung_neu.name_en='New Credit';
         *  leistung_neu.cp=3.5;
         *  leistung_neu.code='M1001';
         *  
         *  leistung_neu.add(function(data){
         *      leistung_neu.id=data.leistung.id;
         *  });
         *  
         *  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
         *  Leistung löschen
         *  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
         *  leistung_neu.drop(function(){alert('gelöscht');});
         */
        
        /**
         * Leistung (bzw. Schein) des Studiengangs (oder "Fachs"). Studiengänge 
         * bestehen aus Leistungen, z.B. "Einführung in die Literaturwissenschaft", 
         * "Physik III", "Praktikum zur Prothetik". Die Leistung 
         * im namespace "seminar" stellt eine solche allgemeine 
         * Leistung dar.
         * 
         * Achtung, nicht verwechseln mit der "Leistung" aus dem 
         * namespace "Student": diese bedeutet eine Leistung, die 
         * ein Studierender erbracht hat, die also absolviert und 
         * benotet ist.
         * @param {long} seminar_id Id des Seminars, dem diese Leistung zugeordnet ist
         * @param {object} leistung mit den gefragten Eigenschaften
         * @see shj.signUp.student.Leistung
         * @constructor ohne Serverkontakt
         * 
         */
        var Leistung = function(seminar_id, leistung){
            if(arguments.length===0) return;
            this.seminar_id_orig=seminar_id;
            this.modul_id=this.modul;
            this.lifecycle_base_url="leistung";
            if (typeof leistung !== 'undefined'){
                $.extend(this,leistung);
            }
            this.equals=function(obj){return this.id===obj.id;};
            shj.signUp.ShjCoreObject.call(this, "Leistung", seminar_id, {"namespace":"seminar"}, "leistung");
        };
        Leistung.prototype=new shj.signUp.ShjCoreObject();
        
        /**
         * Lädt die Leistungen des Seminars vom Server
         * @constructor der Noten
         * @param {long} seminar_id Id des Seminars
         * @param {function} fSuccess wird nur nach Erfolgsmeldung vom Server ausgeführt
         */
        var Leistungen = function(seminar_id, fSuccess){
            this.seminar_id=seminar_id;
            this.lifecycle_base_url="leistung";
            shj.signUp.ShjCoreCollection.call(this, this.seminar_id, "leistungen", 
                    {"lifecycle_base_url":"leistung",
                        "_name":"leistung",
                        "identifier":"id",
                        "constructor_name":'Leistung',
                        "namespace":'seminar'
            }, fSuccess);
        };
        Leistungen.prototype = new shj.signUp.ShjCoreCollection();
        
        // ##############################################################
        // Kurstypen
        // ##############################################################
        /**
         * Kurstypen sind oft mit Leistungen identisch. Allerdings 
         * sind Kurstypen zur Gruppierung der Lehrveranstaltungen 
         * gedacht (und somit von Prüfungsordnungen unabhängig).
         * 
         * Ein Kurstyp kann bzw. sollte einer Leistung zugeordnet sein.
         * 
         * Kurstypen heißen typischerweise z.B. "Einführung in die Literaturwissenschaft", 
         * "Physik III", "Praktikum zur Prothetik". 
         * 
         * Die Kurstypen enthalten Informationen für die Kurswahl zu 
         * Semesterbeginn: ist eine Online-Kurswahl erlaubt? In welchem 
         * Zeitraum? Wie viele Kurse müssen gewählt werden?
         * 
         * @param {long} seminar_id Id des Seminars, dem dieser Kurstyp zugeordnet ist
         * @param {object} kurstyp mit den gefragten Eigenschaften
         * @see shj.signUp.student.Leistung
         * @constructor ohne Serverkontakt
         * 
         */
        var Kurstyp = function(seminar_id, kurstyp){
            if(arguments.length===0) return;
            this.seminar_id_orig=seminar_id;
            this.lifecycle_base_url="kurstyp";
            if (typeof kurstyp !== 'undefined'){
                $.extend(this,kurstyp);
            }
            this.equals=function(obj){return this.id===obj.id;};
            shj.signUp.ShjCoreObject.call(this, "Kurstyp", this.seminar_id, {"namespace":"seminar"}, "kurstyp");
        };
        Kurstyp.prototype = new shj.signUp.ShjCoreObject();
                
        /**
         * Lädt die Noten des Seminars vom Server
         * @constructor der Noten
         * @param {long} seminar_id Id des Seminars
         * @param {function} fSuccess wird nur nach Erfolgsmeldung vom Server ausgeführt
         */
        var Kurstypen = function(seminar_id, fSuccess){
            this.seminar_id=seminar_id;
            this.lifecycle_base_url="kurstyp";
            shj.signUp.ShjCoreCollection.call(this, this.seminar_id, "kurstypen", 
                    {"lifecycle_base_url":"kurstyp",
                        "_name":"kurstyp",
                        "identifier":"id",
                        "constructor_name":'Kurstyp',
                        "namespace":'seminar'
            }, fSuccess);
        };
        Kurstypen.prototype = new shj.signUp.ShjCoreCollection();

        
        // ##############################################################
        // Kurse
        // ##############################################################
        /**
         * Kurse -- momentan mit ziemlich minimaler Info
         * und nur für die Kursanmeldung der Studierenden 
         * verwendet.
         * 
         * @param {long} seminar_id Id des Seminars, dem dieser Kurstyp zugeordnet ist
         * @param {object} kurs mit den gefragten Eigenschaften
         * @see shj.signUp.student.Leistung
         * @constructor ohne Serverkontakt
         * 
         */
        var Kurs = function(seminar_id, kurs){
            if(arguments.length===0) return;
            this.seminar_id_orig=seminar_id;
            this.lifecycle_base_url="kurs";
            if (typeof kurs !== 'undefined'){
                $.extend(this,kurs);
            }
            this.equals=function(obj){return this.id===obj.id;};
            shj.signUp.ShjCoreObject.call(this, "Kurs", -1, {"namespace":"seminar"}, "kurs");
        };
        Kurs.prototype=new shj.signUp.ShjCoreObject();
        /**
         * Lädt die Kurse des Seminars vom Server
         * @constructor der Kurse
         * @param {long} seminar_id Id des Seminars
         * @param {function} fSuccess wird nur nach Erfolgsmeldung vom Server ausgeführt
         */
        var Kurse = function(seminar_id, fSuccess){
            this.seminar_id=seminar_id;
            this.lifecycle_base_url="kurs";
            shj.signUp.ShjCoreCollection.call(this, -1, "kurse", 
                    {"lifecycle_base_url":"kurs",
                        "_name":"kurs",
                        "identifier":"id",
                        "constructor_name":'Kurs',
                        "namespace":'seminar'
                    }, fSuccess);
        };
        Kurse.prototype=new shj.signUp.ShjCoreCollection();
        return {
            Modul:Modul,
            Module:Module,
            Dozent:Dozent, 
            Dozenten:Dozenten,
            DozentTermin:DozentTermin,
            DozentTermine:DozentTermine,
            Fach:Fach, 
            Faecher:Faecher, 
            Information:Information,
            Informationen:Informationen,
            Leistung:Leistung,
            Leistungen:Leistungen,
            Note:Note, 
            Noten:Noten,
            Kurstyp:Kurstyp,
            Kurstypen:Kurstypen,
            Kurs:Kurs,
            Kurse:Kurse};
    })(); 

    
    /**
     * Kümmert sich um Leistungen, Prüfungen und 
     * Anmeldungen sowie die persönlichen Daten 
     * von Studierenden.
     * @namespace student
     */
    shj.signUp.student=(function(){
    
    /**
     * Kann derzeit ausschließlich zum Update des 
     * Fachs verwendet werden
     * @param {String} matrikelnummer
     * @param {object} oStudent
     * @param {function} fSuccess wird nur nach Erfolgsmeldung vom Server ausgeführt
     * @param {function} fErr wird nur nach Fehlermeldung vom Server ausgeführt
     */
    var update = function(matrikelnummer, oStudent, fSuccess, fErr){
        oStudent.lifecycle_base_url="";
        oStudent.namespace="student";
        oStudent.matrikelnummer=matrikelnummer;
        shj.signUp.lifecycle_act(oStudent, 'update', fSuccess, fErr);
    };
    
    // ##############################################################
    // ANMELDUNGEN
    // ##############################################################

    /**
     * Erstellt eine Anmeldung eines Studierenden zu einer Leistung.
     * Anmeldung ist eine Leistung ohne Note und Ausstellungsdatum;
     * DB: tblBdStudentXLeistung mit .blnStudentLeistungKlausuranmeldung=true
     * @constructor ohne Serverkontakt
     * @param {String} seminar_id
     * @param {object} anmeldung Informationen zur Anmeldung
     */
    var Anmeldung = function(seminar_id, anmeldung){
        if(arguments.length===0) return;
        this.matrikelnummer=anmeldung.matrikelnummer;
        this.lifecycle_base_url="anmeldung";
        if (typeof anmeldung !== 'undefined'){
            $.extend(this,anmeldung);
            this.id_orig=anmeldung.id;            
            this.count_orig=anmeldung.count;     
            this.is_anmeldung=true;            
            this.aussteller=anmeldung.aussteller_vorname.trim();
            if(typeof anmeldung.aussteller_nachname!='undefined') this.aussteller+=' ' + anmeldung.aussteller_nachname.trim(); 
        }
        this.equals=function(obj){return this.id===obj.id && this.count==obj.count;};
        shj.signUp.ShjCoreObject.call(this, "Anmeldung", -1, {"namespace":"student", "matrikelnummer":this.matrikelnummer}, "anmeldung");
    };
    Anmeldung.prototype=new shj.signUp.ShjCoreObject();
    
    var Anmeldungen = function(matrikelnummer, fSuccess){
        this.matrikelnummer=matrikelnummer;
        this.lifecycle_base_url="anmeldung";
        shj.signUp.ShjCoreCollection.call(this, -1, "anmeldungen", 
                {"lifecycle_base_url":"anmeldung",
                    "_name":"anmeldung",
                    "constructor_name":'Anmeldung',
                    "namespace":'student'
                }, fSuccess);
    };
    Anmeldungen.prototype = new shj.signUp.ShjCoreCollection();
    
    // ##############################################################
    // LEISTUNGEN
    // ##############################################################
    /**
     * Leistung eines Studierenden
     * DB: tblBdStudentXLeistung
     * @constructor kein Serverkontakt
     * @param {String} seminar_id
     * @param {shj.signUp.student.Leistung} leistung
     */
    var Leistung = function(seminar_id,leistung){
        if(arguments.length===0) return;
        this.lifecycle_base_url="leistung"; 
        if (typeof leistung !== 'undefined'){
            $.extend(this,leistung);
            if(typeof this.modul_id==='undefined')
                this.modul_id=this.modul;
            this.id_orig=leistung.id;         
            this.count_orig=leistung.count;     
            this.is_anmeldung=false;            
            this.aussteller=leistung.aussteller_vorname + ' ' + leistung.aussteller_nachname;            
        }
        this.equals=function(obj){return this.id===obj.id && this.count==obj.count;};
        shj.signUp.ShjCoreObject.call(this, "Leistung", -1, {"namespace":"student", "matrikelnummer":this.matrikelnummer}, "leistung");
    };
    Leistung.prototype=new shj.signUp.ShjCoreObject();
    
    var Leistungen = function(matrikelnummer, fSuccess){
        this.matrikelnummer=matrikelnummer;
        this.lifecycle_base_url="leistung";
        shj.signUp.ShjCoreCollection.call(this, -1, "leistungen", 
                {"lifecycle_base_url":"leistung",
                    "_name":"leistung",
                    "constructor_name":'Leistung',
                    "namespace":'student'
                }, fSuccess);

    };
    Leistungen.prototype=new shj.signUp.ShjCoreCollection();

    
    // ##############################################################
    // Prüfungen
    // ##############################################################
    /**
     * Kein Serverkontakt
     * @constructor kein Serverkontakt, Felder: id, count, bezeichnung, datum, note, semester, notenberechnung (Custom2)
     * @param {String} matrikelnummer
     * @param {object} pruefung
     */
    var Pruefung = function(seminar_id, pruefung){
        if(arguments.length===0) return;
        this.matrikelnummer=pruefung.matrikelnummer;
        this.equals=function(obj){return this.id===obj.id;};
        this.lifecycle_base_url="pruefung";
        if (typeof pruefung !== 'undefined'){
            $.extend(this,pruefung);            
            this.id_orig=pruefung.id;            
            this.count_orig=pruefung.count;                 
        }
        shj.signUp.ShjCoreObject.call(this, "Pruefung", -1, {"namespace":"student", "matrikelnummer":this.matrikelnummer}, "pruefung");
    };
    Pruefung.prototype = new shj.signUp.ShjCoreObject();

    var Pruefungen = function(matrikelnummer, fSuccess){
        this.matrikelnummer=matrikelnummer;
        this.lifecycle_base_url="pruefung";
        shj.signUp.ShjCoreCollection.call(this, -1, "pruefungen", 
                {"lifecycle_base_url":"pruefung",
                    "_name":"pruefung",
                    "item_identifier":"id",
                    "constructor_name":"Pruefung",
                    "namespace":'student'
                }, fSuccess);
    };
    Pruefungen.prototype = new shj.signUp.ShjCoreCollection();
    
    /**
     * @TODO
     * Lädt eine Sammlung der konfigurierten Prüfungen (im Studiengang 
     * des aktuellen Studierenden) vom Server.
     * @param {String} matrikelnummer
     * @param {function} fSuccess wird nach erfolgreichen Laden der konfigurierten Prüfungen ausgeführt
     */
    var KonfiguriertePruefungen = function(matrikelnummer, fSuccess){
        this.matrikelnummer=matrikelnummer;
        this.lifecycle_base_url="pruefung";
        this.konfigurierte_only=true;
        Pruefungen.call(this, matrikelnummer, fSuccess);
    };
    
    KonfiguriertePruefungen.prototype.add=function(){alert('Sorry, nicht implementiert');};
    KonfiguriertePruefungen.prototype.update=function(){alert('Sorry, nicht implementiert');};
    KonfiguriertePruefungen.prototype.drop=function(){alert('Sorry, nicht implementiert');};
 
    // ##############################################################
    // Fachnote (ohne update, add oder drop: nur get)
    // ##############################################################
    //@TODO go through all this and
    //  deprecate add, update etc. where applicable.
    //  
    /**
     * Berechnung der Fachnote
     * @param {type} seminar_id irrelevant
     * @param {type} oBemerkung Bemerkung mit .matrikelnummer,.bemerkung
     * @returns {undefined}
     */
    var Fachnote= function(seminar_id, oFachnote){
        if(arguments.length===0) return;
        $.extend(this,oFachnote);
        this.equals=function(obj){return this.id===obj.id;};
        shj.signUp.ShjCoreObject.call(this, "Fachnote", seminar_id, {"namespace":"student","matrikelnummer":oFachnote.matrikelnummer}, "Fachnote");
    };
    Fachnote.prototype=new shj.signUp.ShjCoreObject();
    /**
     * Berechnung der Fachnote
     * @param {type} seminar_id irrelevant
     * @param {type} oBemerkung Bemerkung mit .matrikelnummer,.bemerkung
     * @returns {undefined}
     */
    var Fachnoten= function(matrikelnummer, fSuccess){
        this.matrikelnummer=matrikelnummer;
        shj.signUp.ShjCoreCollection.call(this, -1, "fachnoten", 
                {"lifecycle_base_url":"fachnote",
                    "_name":"fachnote", 
                    "constructor_name":"Fachnote",
                    "namespace":'student'
                }, fSuccess);
    };
    Fachnoten.prototype=new shj.signUp.ShjCoreCollection();
    
    // ##############################################################
    // BEMERKUNGEN
    // ##############################################################
    //
    //  
    /**
     * Eine Bemerkung (Aktennotiz) zu einem Studierenden.
     * @param {type} seminar_id irrelevant
     * @param {type} oBemerkung Bemerkung mit .matrikelnummer,.bemerkung
     * @returns {undefined}
     */
    var Bemerkung= function(seminar_id, oBemerkung){
        if(arguments.length===0) return;
        this.equals=function(obj){return this.id===obj.id;};
        if(!this.bemerkung_id) this.bemerkung_id=oBemerkung.id || oBemerkung.bemerkung_id;
        shj.signUp.ShjCoreObject.call(this, "Bemerkung", -1, {"namespace":"student","matrikelnummer":oBemerkung.matrikelnummer,"id":oBemerkung.id,"bemerkung":oBemerkung.bemerkung, "datum":oBemerkung.datum,"autor":oBemerkung.autor}, "bemerkung");
    };
    Bemerkung.prototype=new shj.signUp.ShjCoreObject();
    
    //var ShjCoreCollection=function(seminar_id, name, oType, fSuccess){
    var Bemerkungen= function(matrikelnummer, fSuccess){
        this.matrikelnummer=matrikelnummer;
        shj.signUp.ShjCoreCollection.call(this, -1, "bemerkungen", 
                {"lifecycle_base_url":"bemerkung",
                    "_name":"bemerkung", 
                    "constructor_name":"Bemerkung",
                    "namespace":'student'
                }, fSuccess);
    };
    Bemerkungen.prototype=new shj.signUp.ShjCoreCollection();

    // *#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#
    // Dokumente:
    // (Selbstdruck, derzeit nur Transkripte)
    // 
    // Die Datenbank kennt die Tabellen 
    // 
    // "tblBdStudentDokument"
    // 
    // *#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#

     var Dokument =  function (seminar_id, json_dok){
        if(arguments.length===0) return;
        this.equals=function(obj){return this.verifikationscode===obj.verifikationscode && this.matrikelnummer===obj.matrikelnummer;};
        this.matrikelnummer=json_dok.matrikelnummer;
        if(json_dok.english){ this.english=json_dok.english;} else {this.english='false';}
        $.extend(this,json_dok);
        shj.signUp.ShjCoreObject.call(this, "Dokument", -1, {"namespace":"student", "matrikelnummer":this.matrikelnummer}, "transkript");
    };
    Dokument.prototype=new shj.signUp.ShjCoreObject();
    
    /**
     * Lade die Dokumente des Studierenden vom Server
     * Falls bPermanente gesetzt ist, werden permanent 
     * gespeicherte Transkripte geliefert, die nur 
     * für Prüfungsämter etc. zugänglich sind.
     * 
     * (Absolvieren Studierende z.B. den BA und bleiben 
     * für den MA immatrikuliert, kann das Abschlusstranskript 
     * hinterlegt werden; es ändert sich nicht mehr).
     * 
     * @param {String} matrikelnummer
     * @param {function} fSuccess Funktion wird nach erfolgreichem Laden vom Server ausgeführt
     */
    var Dokumente = function(matrikelnummer, fSuccess, bPermanente){
        this.matrikelnummer=matrikelnummer;
//            shj.signUp.ShjCoreCollection.call(this, -1, "transkripte", 
//                {"lifecycle_base_url":"transkript",
//                    "_name":"transkript",
//                    "permanent":"true",
//                    "constructor_name":"Dokument",
//                    "namespace":'student'
//                }, fSuccess);            
//        }else{
            shj.signUp.ShjCoreCollection.call(this, -1, "transkripte", 
                {"lifecycle_base_url":"transkript",
                    "_name":"transkript",
                    "constructor_name":"Dokument",
                    "namespace":'student'
                }, fSuccess);
//        }
    };
    Dokumente.prototype=new shj.signUp.ShjCoreCollection();

    // *#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#
    // Anträge:
    // (Transkripte, Formblatt 5, Prüfungsbescheinigungen etc.)
    // 
    // Die Datenbank kennt die Tabellen 
    // 
    // "tblBdStudentAntrag" ¹ --- n "tblBdStudentAntragStatus"
    // 
    // (Jeder Antrag kann n Stati haben -- beantragt, aufgeschoben, erledigt
    // etc.)
    // 
    // *#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#
    var Antrag =  function (seminar_id, json_antrag){
        if(arguments.length===0) return;
        this.matrikelnummer=json_antrag.matrikelnummer;
        this.lifecycle_base_url="antrag";
        this.equals=function(obj){return this.id===obj.id;};
        $.extend(this,json_antrag);
        if(!this.antrag_id && this.id) this.antrag_id=this.id;
        shj.signUp.ShjCoreObject.call(this, "Antrag", -1, {"namespace":"student", "matrikelnummer":this.matrikelnummer}, "antrag");
    };
    Antrag.prototype=new shj.signUp.ShjCoreObject();
    

    /**
     * Lade die Antraege des Studierenden vom Server
     * @param {String} matrikelnummer
     * @param {function} fSuccess Funktion wird nach erfolgreichem Laden vom Server ausgeführt
     */
    var Antraege = function(matrikelnummer, fSuccess){
         this.matrikelnummer=matrikelnummer;
         shj.signUp.ShjCoreCollection.call(this, -1, "antraege", 
                {"lifecycle_base_url":"antrag",
                    "_name":"antrag",
                    "constructor_name":"Antrag",
                    "namespace":'student'
                }, fSuccess);
    };
    Antraege.prototype=new shj.signUp.ShjCoreCollection();

    return {Bemerkung:Bemerkung, 
            Bemerkungen:Bemerkungen,
            Leistung:Leistung, 
            Leistungen:Leistungen, 
            Anmeldung:Anmeldung, 
            Anmeldungen:Anmeldungen,
            Pruefung:Pruefung,
            Pruefungen:Pruefungen,
            KonfiguriertePruefungen:KonfiguriertePruefungen, 
            update:update,
            Antrag:Antrag,
            Antraege:Antraege,
            Dokument:Dokument,
            Dokumente:Dokumente, 
            Fachnoten:Fachnoten,
            Fachnote:Fachnote};
   })();
});