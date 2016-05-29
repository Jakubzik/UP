/*
 * (c) 2015 shj, heiko jakubzik
 *
 * Programm setzt die "Kommentierten Ankündigungen"
 * mit Hilfe von docxtemplater in ein Word Dokument.
 *
 * Die Templates liegen in ./print
 *
 * Das JSON wird aus
 * json/seminar/kvv/get_1.jsp erzeugt.
 *
 * Das Template kann z.B. so aussehen:
 * {#kategorien}
* {name}
* {#kurstypen}
* {name}
* {#kurse}
* {titel_en}
*
* {lehrende}
* {termin}
* {@beschreibungXML_R}
* {/kurse}{/kurstypen}{/kategorien}
*
* Die Tags sind mit einer Ausnahme hoffentlich selbsterklärend.
* Die Ausnahme ist @beschreibungXML_R:
*
* (1) @beschreibungXML
*     enthält die docx-kodierte Beschreibung,
*
* (2) @beschreibungXML_R
* 	  ebenfalls, allerdings beim zweiten Vorkommen derselben
* 	  Beschreibung nur einen Verweis.
*
* Für Teil-Kvvs ("Filter") eignet sich daher @beschreibungXML
* besser.
*
 */
$('#fLoad').submit(function (e) {
      e.preventDefault();
});

// #############################################################################
// Konfiguration
// #############################################################################
var g_bReplace_EM=true;       // Kursivschrift setzen
var g_bReplace_STRONG=true;   // Fettschrift setzen
var g_oKvv = {};
var g_aFilter = [];

var g_DEBUG_LEVEL = 3;  // 0=silent, 1=err, 2=info, 3 = debug

// Fundamentals (20010), PS II Sprachwiss, Fachdidaktik II, Pronunc (22, 4), Tense & Asp (Repeat), Writing I (6)
var g_aKurstypenMitBeschreibung = [20010, 10030, 20230, 22, 4, 5, 34, 6, 7];

// Fundamentals (20010), Pronunciation (22,4), Tense & Aspect (Repeat)
var g_aKurstypenOnlyGrouped = [20010, 22, 4, 5, 34, 6, 7];

// Die Filter sind nach den values der CBO "cboSparte" benannt.
g_aFilter['all'] = {
  "kurstypen":[1,2,5,6,7,9,14,15,16,19,20,21,24,25,28,30,34,39,10020,10030,10040,10050,10100,10110,10120,30000,30010,30060,30070],
  "template":'template_KVV.docx'
};
g_aFilter['lw'] = {
  "kurstypen":[2,14,15,19,21,24,25,30,10100,30060,30070],
  "template":'template_KVV_lw.docx'
};
g_aFilter['sw'] = {
  "kurstypen":[1,16,20,21,24,27,10010,10020,10030,10040,10050,30000,30010],
  "template":'template_KVV_sw.docx'
};
g_aFilter['kw'] = {
  "kurstypen":[10070,10080,10090,10150],
  "template":'template_KVV_kw.docx'
};
g_aFilter['sx'] = {
  "kurstypen":[5,6,7,9,28,34,39,10110,10120,10130,10140,20000,20005,20100],
  "template":'template_KVV_sx.docx'
};
g_aFilter['ma-fak'] = {
  "kurstypen":[25, 15, 19],
  "template":''     // empty for default
};
g_aFilter['hca'] = {
  "kurstypen":[30200],
  "template":''
};
g_aFilter['vv'] = {
  "kurstypen":[],
  "template":''
};

$('#cmdLoad').on('click', function(){
  loadKvv();
})

/**
 * Hauptfunktion:
 * - lädt Kvv-JSON von "kvv_1.jsp"
 * - setzt g_oKvv
 * - Erzeugt Word-Dokument ggf. mit Filter
 * @return {[type]} [description]
 */
function loadKvv(){
  if(g_DEBUG_LEVEL>=3) console.log('[DEBUG]: .loadKvv()');
  $.ajax({
    type: 'POST',
    url: './json/kvv/kvv_1.jsp',
    data: {},
    contentType: "application/x-www-form-urlencoded;charset=utf8",
    dataType: 'json',
    success: function(data) {
      if(g_DEBUG_LEVEL>=3) console.log('[DEBUG]: .loadKvv() successfully completed.');
      g_oKvv = recodeDataToDocx(data);
      pushWord(
        getFilter(g_oKvv), getTemplate()
      );
      return true;
    },
    error: function(err){
      if(g_DEBUG_LEVEL>=1) console.log('[ERR]: .loadKvv(), Fehler beim Laden: ' + err.message);
      console.log('Fehler beim Laden d. Kvvs: ' + err.message);
    }
  });
}

/**
 * Filtert das Kvv-Objekt gemäß der Wahl von 'cboSparte'
 * @param  {[type]} oKvvKomplett [description]
 * @return {[type]}              [description]
 */
function getFilter (oKvvKomplett) {
  if(g_DEBUG_LEVEL>=3) console.log('[DEBUG]: .getFilter() aufgerufen, arg:');
  if(g_DEBUG_LEVEL>=3) console.log(oKvvKomplett);

  var oReturn = {};
  oReturn.kategorien = [];

  // Falls kein Filter gesetzt ist,
  // bleibt das Dokument unverändert
  if( $('#cboSparte').val() == '' ){
    if(g_DEBUG_LEVEL>=3) console.log('[DEBUG]: .getFilter liefert Argument ZURÜCK (kein Filter).');
    return oKvvKomplett;
  }else{

    // Array der Kurstypen im Filter
    var aFilter = g_aFilter[ $('#cboSparte').val() ].kurstypen;
    if(g_DEBUG_LEVEL>=3) console.log('[DEBUG]: .getFilter filtert folgende Kurstypen:');
    if(g_DEBUG_LEVEL>=3) console.log(aFilter);

    // Durchlaufe alle Kategorien
    for (var ii = 0, jj = oKvvKomplett.kategorien.length; ii < jj; ii++) {

      // Gibt es in dieser Kategorie einen Kurstyp des Filters?
      var bIsKategorieInFilter = (_intersect(oKvvKomplett.kategorien[ii].kurstypen_ids, aFilter).length > 0);
      if(g_DEBUG_LEVEL>=3) console.log('[DEBUG]: ... ist Kategorie >' +
          oKvvKomplett.kategorien[ii].name + '< im Filter? -> ' + bIsKategorieInFilter);
      if(bIsKategorieInFilter){
        // JA: suche nach den Kurstypen im Filter
        var tmpKurstypen = [];
        if(g_DEBUG_LEVEL>=3) console.log('[DEBUG]: ... ... ja.');
        for (var ij = 0, ij2 = oKvvKomplett.kategorien[ii].kurstypen.length; ij < ij2; ij++) {
          if(aFilter.indexOf(parseInt(oKvvKomplett.kategorien[ii].kurstypen[ij].id)) >= 0){
            if(g_DEBUG_LEVEL>=3) console.log('[DEBUG]: ... ... ... pushe Kurstyp >' +
                oKvvKomplett.kategorien[ii].kurstypen[ij].name + '<.');
            tmpKurstypen.push(oKvvKomplett.kategorien[ii].kurstypen[ij]);
          }
        }

        oReturn.kategorien.push({
          "name":oKvvKomplett.kategorien[ii].name,
          "kurstypen":tmpKurstypen})
      }
    }// End Look Kategorien
  }
  return oReturn;
}

/**
 * Gibt das Template des Filters zurück (siehe Konfiguration)
 * @return {[String]} [Name des docx-Templates]
 */
function getTemplate () {
  if(g_DEBUG_LEVEL>=3) console.log('[DEBUG]: .getTemplate()');
  if($('#cboSparte').val()!='' && g_aFilter[$('#cboSparte').val()].template && g_aFilter[$('#cboSparte').val()].template != ''){
    if(g_DEBUG_LEVEL>=3) console.log('[DEBUG]: ... Sparte >' +
        $('#cboSparte').val() + '< benutzt Template >' +
        g_aFilter[$('#cboSparte').val()].template + '<');
    return g_aFilter[$('#cboSparte').val()].template;
  }else{
    if(g_DEBUG_LEVEL>=3) console.log('[DEBUG]: ... Spartenwahl leer, benutze >template_Kvv.docx<.');
    return 'template_KVV.docx';
  }
}

/**
 * Überarbeitet das JSON von kvv_1.jsp, so dass
 * es für docx-Templates verdaulicher ist.
 *
 * Insbesonder wird der Termin ins Englische
 * übersetzt, und die Formatierung (kursiv etc.)
 * für die Beschreibungen erzeugt.
 *
 * @param  {[type]} oKvvOld [description]
 * @return {[object]}         [description]
 */
function recodeDataToDocx (oKvvOld) {
  if(g_DEBUG_LEVEL>=3) console.log('[DEBUG]: .recodeDataToDocx aufgerufen. Arg:');
  if(g_DEBUG_LEVEL>=3) console.log(oKvvOld);

  var oKvv = {};
  oKvv.kategorien=[];

  var a_kurs_ids = [];

  // Dreifache Schleife:
  //
  // Kategorien
  //     |
  //    Kurstypen
  //         |
  //       Kurse
  //
  for (var ii = 0, jj = oKvvOld.kategorie.length; ii < jj; ii++) {
    var kurstypen_ids=[];

    for (var ij = 0, ij2=oKvvOld.kategorie[ii].kurstypen.length; ij < ij2; ij++) {

      var iKurstypID = parseInt(oKvvOld.kategorie[ii].kurstypen[ij].id);

      // Die KurstypenIDs werden gesammelt, weil sich so die
      // Filter später bequemer einsetzen lassen.
      kurstypen_ids.push(iKurstypID);

      // Nur von bestimmten Kurstypen werden Kurstypbeschreibungen
      // angezeigt (Def. in g_aKurstypenMitBeschreibung)
      oKvvOld.kategorie[ii].kurstypen[ij].zeigeBeschreibung = (g_aKurstypenMitBeschreibung.indexOf(iKurstypID)>=0)
      oKvvOld.kategorie[ii].kurstypen[ij].beschreibung_XML = getDocxMarkup(oKvvOld.kategorie[ii].kurstypen[ij].beschreibung);

      // Die Kurse mancher Kurstypen (z.B. Grammar1) sollen
      // nur aufgelistet werden: nicht jeder Kurs verlangt eine
      // eigene Beschreibung. Siehe g_aKurstypenOnlyGrouped
      //
      // Die Eigenschaft "gruppiert" wird den Kursen angeheftet,
      // weil im Template kein Zugriff auf den Kurstyp besteht.
      var bKurstypMitKursbeschreibung = (g_aKurstypenOnlyGrouped.indexOf(iKurstypID)<0);
      oKvvOld.kategorie[ii].kurstypen[ij].zeigeKursBeschreibungen = bKurstypMitKursbeschreibung;
      oKvvOld.kategorie[ii].kurstypen[ij].ohneKursBeschreibungen = (!bKurstypMitKursbeschreibung);

      // Wenn es in diesem Kurstyp Kurse gibt, durchlaufen:
      if(oKvvOld.kategorie[ii].kurstypen[ij].kurse){
        for(var kk = 0, kk2 = oKvvOld.kategorie[ii].kurstypen[ij].kurse.length; kk < kk2; kk++) {

          oKvvOld.kategorie[ii].kurstypen[ij].kurse[kk].zeigeBeschreibung = bKurstypMitKursbeschreibung;
          oKvvOld.kategorie[ii].kurstypen[ij].kurse[kk].ohneBeschreibung = (!bKurstypMitKursbeschreibung);

          // Wichtig ist, dass der englische Titel des Kurses
          // existiert.
          // Ist er leer, wird stattdessen der deutsche Titel gesetzt.
          // Ist er ...
          if(oKvvOld.kategorie[ii].kurstypen[ij].kurse[kk].titel_en == '')
                oKvvOld.kategorie[ii].kurstypen[ij].kurse[kk].titel_en = oKvvOld.kategorie[ii].kurstypen[ij].kurse[kk].titel;

          // ... dann immer noch leer, wird 'tba = to be announced' gesetzt.
          if(oKvvOld.kategorie[ii].kurstypen[ij].kurse[kk].titel_en == '')
                oKvvOld.kategorie[ii].kurstypen[ij].kurse[kk].titel_en = 'tba';

          // Die Beschreibung wird wg. der Formatierungen von .getDocxMarkup gesetzt
          oKvvOld.kategorie[ii].kurstypen[ij].kurse[kk].beschreibungXML = getDocxMarkup(oKvvOld.kategorie[ii].kurstypen[ij].kurse[kk].beschreibung);

          var tmp = oKvvOld.kategorie[ii].kurstypen[ij].kurse[kk].literatur;
          if(tmp && tmp.length>4){
              if(tmp.startsWith('<p>')){
                  tmp=tmp.substring(3);
                  oKvvOld.kategorie[ii].kurstypen[ij].kurse[kk].literatur = '<p><strong>Texts:</strong> ' + tmp;
              }else{
                  oKvvOld.kategorie[ii].kurstypen[ij].kurse[kk].literatur = '<strong>Texts:</strong> ' + tmp;
              }
          }
          oKvvOld.kategorie[ii].kurstypen[ij].kurse[kk].literaturXML = getDocxMarkup(oKvvOld.kategorie[ii].kurstypen[ij].kurse[kk].literatur);

          // Der Termin liegt im JSON nur auf Deutsch vor. Versuche die Übersetzung.
          if(oKvvOld.kategorie[ii].kurstypen[ij].kurse[kk].termin)
            oKvvOld.kategorie[ii].kurstypen[ij].kurse[kk].termin = getTerminEN(oKvvOld.kategorie[ii].kurstypen[ij].kurse[kk].termin);

          // Wurde dieser Kurs schon einmal durchlaufen (und die Arrays liegen
          // hier in der von SignUp sortierten Reihenfolge vor), wird anstatt
          // der Beschreibung in beschreibungXML_R nur die Referenz zur
          // ersten Erwähnung des Kurses hinterlegt.
          if (a_kurs_ids.indexOf(oKvvOld.kategorie[ii].kurstypen[ij].kurse[kk].id) >= 0) {
            oKvvOld.kategorie[ii].kurstypen[ij].kurse[kk].beschreibungXML_R = '<w:p><w:pPr>'+
                  '<w:pStyle w:val="Textkrper"/><w:rPr/></w:pPr><w:r><w:rPr>' +
                  '</w:rPr><w:t xml:space="preserve">Description see page </w:t></w:r><w:r><w:rPr>' +
                  '</w:rPr><w:fldChar w:fldCharType="begin"></w:fldChar></w:r><w:r>' +
                  '<w:instrText> PAGEREF Ref_kurs_id_' + oKvvOld.kategorie[ii].kurstypen[ij].kurse[kk].id +
                  ' \h </w:instrText></w:r><w:r>' +
                  '<w:fldChar w:fldCharType="separate"/></w:r><w:r><w:t>1</w:t>' +
                  '</w:r><w:r><w:fldChar w:fldCharType="end"/></w:r><w:r><w:rPr></w:rPr><w:t>.</w:t></w:r></w:p>';
          }else{
            // Kurs wurde zum ersten Mal durchlaufen.
            a_kurs_ids.push(oKvvOld.kategorie[ii].kurstypen[ij].kurse[kk].id);

            oKvvOld.kategorie[ii].kurstypen[ij].kurse[kk].beschreibungXML_R = '<w:bookmarkStart ' +
              'w:id="0" w:name="Ref_kurs_id_' + oKvvOld.kategorie[ii].kurstypen[ij].kurse[kk].id +
              '"/><w:bookmarkEnd w:id="0"/>' + oKvvOld.kategorie[ii].kurstypen[ij].kurse[kk].beschreibungXML;
          }
        }
      }
    }
    var tmp={"name":oKvvOld.kategorie[ii].name, "kurstypen":oKvvOld.kategorie[ii].kurstypen, "kurstypen_ids": kurstypen_ids};
    oKvv.kategorien.push(tmp);
  }
  return oKvv;
}

// Utility
function _escapeRegExp(str) {
    return str.replace(/([.*+?^=!:${}()|\[\]\/\\])/g, "\\$1");
}

/**
 * Haben die Arrays a und b gemeinsame Elemente?
 * @param  {[Array]} a
 * @param  {[Array]} b
 * @return {[boolean]} Schnittmenge nicht leer?
 */
function _intersect(a, b) {
    var t;
    if (b.length > a.length) t = b, b = a, a = t; // indexOf to loop over shorter
    return a.filter(function (e) {
        if (b.indexOf(e) !== -1) return true;
    });
}

// Utility
function _replaceAll (str, find, replace) {
  return str.replace(new RegExp(_escapeRegExp(find), 'g'), replace);
}

// Utility: Umlaute für docx
function _replaceAllUmlaute (s_in) {
  var sReturn = '';
  sReturn = _replaceAll (s_in, '&euml;', 'ë');
  sReturn = _replaceAll (sReturn, '&uuml;', 'ü');
  sReturn = _replaceAll (sReturn, '&auml;', 'ä');
  sReturn = _replaceAll (sReturn, '&ouml;', 'ö');
  sReturn = _replaceAll (sReturn, '&Uuml;', 'Ü');
  sReturn = _replaceAll (sReturn, '&Auml;', 'Ä');
  sReturn = _replaceAll (sReturn, '&Ouml;', 'Ö');
  sReturn = _replaceAll (sReturn, '&szlig;', 'ß');
  sReturn = _replaceAll (sReturn, '&aacute;', 'á');
  sReturn = _replaceAll (sReturn, '&eacute;', 'é');
  return sReturn;
}

/**
 * Wandelt HTML Input um in docx-Kodierung, wobei
 * Formatierungen möglichst erhalten bleiben.
 * @param  {[type]} sInput [description]
 * @return {[type]}        [description]
 */
function getDocxMarkup (sInput) {
  if(!sInput) return '';

  var sReturn = _replaceAll (sInput, '&nbsp;', ' ').trim();

  // Die HTML-Klammern machen im XML Probleme
  sReturn = _replaceAll (sReturn, '<', '[');
  sReturn = _replaceAll (sReturn, '>', ']');

  // Anführungszeichen:
  sReturn = _replaceAll (sReturn, ' &quot;', ' “');
  sReturn = _replaceAll (sReturn, ']&quot;', ']“');
  sReturn = _replaceAll (sReturn, '(&quot;', '(“');
  sReturn = _replaceAll (sReturn, '&quot; ', '” ');
  sReturn = _replaceAll (sReturn, '&quot;,', '”,');
  sReturn = _replaceAll (sReturn, '&quot;[', '”[');
  sReturn = _replaceAll (sReturn, '&quot;)', '”)');
  sReturn = _replaceAll (sReturn, '&quot;. ', '.” ');

  // Einfache Anführungszeichen und Genetiv-s-Apostroph
  sReturn = _replaceAll (sReturn, ' \'', ' ‘');
  sReturn = _replaceAll (sReturn, ']\'', ']‘');
  sReturn = _replaceAll (sReturn, '(\'', '(‘');
  sReturn = _replaceAll (sReturn, '\' ', '’ ');
  sReturn = _replaceAll (sReturn, '\',', '’,');
  sReturn = _replaceAll (sReturn, '\'[', '’[');
  sReturn = _replaceAll (sReturn, '\')', '’)');
  sReturn = _replaceAll (sReturn, '\'. ', '.’ ');
  sReturn = _replaceAll (sReturn, '?s', '’s');

  sReturn = _replaceAllUmlaute(sReturn);

  // <ul><li> Konstruktionen werden paraphrasiert
  sReturn = _replaceAll (sReturn, '[ul]', '');
  sReturn = _replaceAll (sReturn, '[/ul]', '');
  sReturn = _replaceAll (sReturn, '[li]', '[p]- ');
  sReturn = _replaceAll (sReturn, '[/li]', '[/p]');

  // Stelle sicher, dass alles in <p> Absätze eingeklammert ist.
  if(sReturn.indexOf('[p]') != 0) sReturn = '[p]' + sReturn + '[/p]';

  // Es kommt vor, dass <p>blabla</p>Und dann der Rest.
  // Das muss umgewandelt werden zu: <p>blabla</p><p>Und dann der Rest</p>
  if(!sReturn.endsWith('[/p]')){
    var iStart = sReturn.lastIndexOf('[/p]');
    sReturn = sReturn.substr(0, iStart) + '[/p][p]' + sReturn.substr(iStart + 4) + '[/p]';
  }

  // kursiv
  if(g_bReplace_EM) {
    sReturn = _replaceAll (sReturn, '[em]', '</w:t></w:r><w:r><w:rPr><w:i/><w:iCs/></w:rPr><w:t>');
    sReturn = _replaceAll (sReturn, '[/em]', '</w:t></w:r><w:r><w:rPr></w:rPr><w:t xml:space = "preserve">');
    sReturn = _replaceAll (sReturn, '[i]', '</w:t></w:r><w:r><w:rPr><w:i/><w:iCs/></w:rPr><w:t>');
    sReturn = _replaceAll (sReturn, '[/i]', '</w:t></w:r><w:r><w:rPr></w:rPr><w:t xml:space = "preserve">');
  }

  // fett
  if(g_bReplace_STRONG){
    sReturn = _replaceAll (sReturn, '[strong][p]', '[p][strong]');
    sReturn = _replaceAll (sReturn, '[/p][/strong]', '[/strong][/p]');
    sReturn = _replaceAll (sReturn, '[strong]', '</w:t></w:r><w:r><w:rPr><w:b/><w:bCs/></w:rPr><w:t>');
    sReturn = _replaceAll (sReturn, '[/strong]', '</w:t></w:r><w:r><w:rPr></w:rPr><w:t xml:space="preserve">');
  }

  // hochgestellt
  sReturn = _replaceAll (sReturn, '[sup]', '</w:t></w:r><w:r><w:rPr><w:vertAlign w:val="superscript"/></w:rPr><w:t>');
  sReturn = _replaceAll (sReturn, '[/sup]', '</w:t></w:r><w:r><w:rPr></w:rPr><w:t xml:space="preserve">');

  // unterstrichen
  sReturn = _replaceAll (sReturn, '[u]', '</w:t></w:r><w:r><w:rPr><w:u w:val="single"/></w:rPr><w:t>');
  sReturn = _replaceAll (sReturn, '[/u]', '</w:t></w:r><w:r><w:rPr></w:rPr><w:t xml:space="preserve">');

  // bedingte Silbentrennung: einfach löschen (momentan)
  sReturn = _replaceAll (sReturn, '&shy;', '');

  // Absätze:
  sReturn = _replaceAll (sReturn, '[br /]', '[p][/p]');

  sReturn = _replaceAll (sReturn, '[p]', '<w:p><w:pPr><w:pStyle w:val="Textkrper"/><w:rPr/></w:pPr><w:r><w:rPr/><w:t xml:space="preserve">');
  sReturn = _replaceAll (sReturn, '[/p]', '</w:t></w:r></w:p>');

  // Übriggebliebene Ampersands stören das XML
  sReturn = _replaceAll (sReturn, '&amp;', '</w:t></w:r><w:r><w:rPr></w:rPr><w:t xml:space="preserve">SHJ_AMPERSAND</w:t></w:r><w:r><w:rPr></w:rPr><w:t>');

  // Links sind zu kompliziert, wird aufgeschoben.
  sReturn = _replaceAll (sReturn, '&', 'AMPERSAND');
  sReturn = _replaceAll (sReturn, 'SHJ_AMPERSAND', '&amp;');
  return sReturn;
}

/**
 * Öffnet das Word-Dokument und übergibt
 * an docxtemplater das Kvv-Objekt
 * @param  {[object]} oKvv Mit Kategorien, Kurstypen und Kursen
 * @return {[String]} Name des Word-Templates
 */
function pushWord (oKvv, sTemplate) {
    var loadFile = function (url,callback) {
      JSZipUtils.getBinaryContent(url,callback);
    };

    if (g_DEBUG_LEVEL >= 3) console.log('[DEBUG] .pushWord, template=>' + sTemplate + '<, oKvv:');
    if (g_DEBUG_LEVEL >= 3) console.log(oKvv);

    loadFile ("./print/" + sTemplate + "?v=" + Date.now(),function(err,content){    // v=time soll Cache löschen
      if (err) {
        if (g_DEBUG_LEVEL >= 1) console.log('[ERR] .pushWord:');
        if (g_DEBUG_LEVEL >= 1) console.log(err);
          throw err;};
      if (g_DEBUG_LEVEL >= 3) console.log('[DEBUG] ... pushWord.loadFile content is:');
      if (g_DEBUG_LEVEL >= 3) console.log(content);
      doc = new Docxgen(content);
      doc.setData(oKvv);

      if (g_DEBUG_LEVEL >= 3) console.log('[DEBUG] ... pushWord.loadFile doc:');
      if (g_DEBUG_LEVEL >= 3) console.log(doc);

      doc.render();
      out = doc.getZip().generate({type:"blob"});
      saveAs(out,"Kvv.docx");
    });
}

/**
 * Montag, 14.15-15.45, Hörsaal ->
 * Mon 2:15-3.45
 * @param  {[type]} s_IN [description]
 * @return {[type]}      [description]
 */
function getTerminEN(s_IN){
   var sReturn = _replaceAll (s_IN, 'Montag', 'Mon');
   var sReturn = _replaceAll (sReturn, 'Dienstag', 'Tue');
   var sReturn = _replaceAll (sReturn, 'Mittwoch', 'Wed');
   var sReturn = _replaceAll (sReturn, 'Donnerstag', 'Thu');
   var sReturn = _replaceAll (sReturn, 'Freitag', 'Fri');
   var sReturn = _replaceAll (sReturn, 'Samstag', 'Sat');

   var sReturn = _replaceAll (sReturn, '13.', '1:');
   var sReturn = _replaceAll (sReturn, '14.', '2:');
   var sReturn = _replaceAll (sReturn, '15.', '3:');
   var sReturn = _replaceAll (sReturn, '16.', '4:');
   var sReturn = _replaceAll (sReturn, '17.', '5:');
   var sReturn = _replaceAll (sReturn, '18.', '6:');
   var sReturn = _replaceAll (sReturn, '19.', '7:');
   var sReturn = _replaceAll (sReturn, '.', ':');

   var sReturn = _replaceAll (sReturn, ':0 ', ':00 ');
   var sReturn = _replaceAll (sReturn, ':0, ', ':00, ');
   if(sReturn.endsWith(':0')) sReturn += '0';

   return sReturn;
}
