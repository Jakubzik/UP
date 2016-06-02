/* 
 * Request-Parameter:
 * shj_seminar=[as,gs,med] etc.
 * 
 * @TODO: 
 *  - seminar.dozent.termin
 *  - Sicherheitstests: geringe Berechtigungen führen zu Err
 *  
 */

/**
 * ====================================================================
 * T o o l b o x
 * ====================================================================
 * 
 * @type @exp;chai@pro;expect
 */
var expect = chai.expect;
var g_bTestInformationen = false;       // lokal fehlen Postgres die Tabellen...
var g_signup_expected_backend_version='1-0-0-2';
var g_SEMINAR_ID=1;
var g_lDOZENT_BSP=26;                   // an welchem Dozent darf ich herumspielen?
var g_lLEISTUNG_BSP=11;                 // welche Leistung soll man finden?
var g_lLeistung_ID_Anmeldungen=1;
var g_lModularerStudiengang=200822;     // Info zu Studiengängen exemplarisch testen an?
var g_iKursID=15013;                    // #HACK, das muss natürlich vorher abgerufen
                                        //        werden und dient nur temporär dem 
                                        //        Testen der Terminverwaltung

describe("shj.signUp.toolbox", function() {
    describe(".isValidEmail(string)", function() {
        it("prüft Valididtät von E-Mail Adressen", function() {
          expect(shj.signUp.toolbox.isValidEmail('heiko.jakubzik@as.uni-heidelberg.de')).to.equal(true);
          expect(shj.signUp.toolbox.isValidEmail('heiko.jakubzik@as.uni-heidelberg@de')).to.equal(false);
          expect(shj.signUp.toolbox.isValidEmail('heiko.jakubzik.as.uni-heidelberg@de')).to.equal(false);
          expect(shj.signUp.toolbox.isValidEmail('a@b.d')).to.equal(false);
        });
      });
      
    describe(".getGermanDate(iso)", function() {
        it("wandelt iso-Format in dt. Datum um", function() {
          expect(shj.signUp.toolbox.getGermanDate('2014-1-1')).to.equal('1.1.2014');
          expect(shj.signUp.toolbox.getGermanDate('2014')).to.equal('undefined.undefined.2014');
        });
      });
});

/**
 * ====================================================================
 * S e m i n a r
 * ====================================================================
 */
/**
 * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
 * Aktuelles
 * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
 * 
 * @type @exp;chai@pro;expect
 */
describe("shj.signUp.seminar.Aktuelles", function() {
    it('Aktuelles kann abgerufen werden', function(done) {
        var sending={};
        sending.signup_expected_backend_version=g_signup_expected_backend_version;                                             
        sending.seminar_id=1;
        $.ajax({
            data: sending,
            url: '../institut/json/seminar/aktuelles/get.jsp',              // #Hack, @TODO: "as" darf da nicht stehen.
            success: function(data){
                // Reload
                console.log('OK');
                console.log(data);
                done();
            }
        });
  });
});
/**
 * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
 * Noten
 * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
 * 
 * @type @exp;chai@pro;expect
 */
describe("shj.seminar.signUp.Noten [Collection]", function() {
    var noten; var note;
    it("werden vom Server geladen (mindestens 3).", function(done) {
      noten = new shj.signUp.seminar.Noten(g_SEMINAR_ID, function(data){
          console.log('...es gibt ' + data.noten.length + ' Noten');
          expect(data.noten).to.have.length.above(2);
          done();
      });
    });
    
    it("haben die Eigenschaften 'bestanden', 'name', 'wert', 'id'.", function() {
      note=noten.get(3);  
      console.log(note);
        expect(note).to.have.property('bestanden');
        expect(note).to.have.property('name');
        expect(note).to.have.property('wert');
        expect(note).to.have.property('id');
      });
      
    it("initialisiert Route zum Backend richtig", function() {
      expect(note.lifecycle_base_url).to.equal("note");
      expect(note.namespace).to.equal("seminar");
    });
    
    it("vergleicht Objekte korrekt per .equals", function() {
      var note_a = new shj.signUp.seminar.Note(g_SEMINAR_ID, {"id":1});
      var note_b = new shj.signUp.seminar.Note(g_SEMINAR_ID, {"id":2});
      var note_c = new shj.signUp.seminar.Note(g_SEMINAR_ID);
      note_c.id=note.id;
      expect(note_a.equals(note_b)).to.equal(false);
      expect(note_a.equals(note_a)).to.equal(true);
      expect(note.equals(note_c)).to.equal(true);
      expect(note_a.equals(note_c)).to.equal(false);
    });
});

/**
 * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
 * Anmeldungen
 * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
 * 
 * @type @exp;chai@pro;expect
 */
var anmeldungen={};
describe("shj.signUp.seminar.Anmeldungen", function() {
    it("werden vom Server geladen", function(done) {
        var sending={};
        sending.signup_expected_backend_version=g_signup_expected_backend_version;                                             
        sending.seminar_id=1;
        sending.leistung_id=g_lLeistung_ID_Anmeldungen;
        sending.dozent_id='*';
        $.ajax({
            data: sending,
            url: '../institut/json/seminar/anmeldung/get.jsp',              // #Hack, @TODO: "as" darf da nicht stehen.
            success: function(data){
                // Reload
                console.log('OK');
                console.log(data);
                anmeldungen = data.anmeldungen;
                done();
            }
        });
    });
    it("Eigenschaften OK", function() {
        console.log('[DEBUG]: ANMELDUNGEN...');
        console.log(anmeldungen);
        expect(anmeldungen[0].anmeldung).to.have.property('name');
        expect(anmeldungen[0].anmeldung).to.have.property('name_en');
        expect(anmeldungen[0].anmeldung).to.have.property('matrikelnummer');
        expect(anmeldungen[0].anmeldung).to.have.property('id');
        expect(anmeldungen[0].anmeldung).to.have.property('ects');
    });
});

/**
 * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
 * Informationen
 * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
 * 
 * @type @exp;chai@pro;expect
 */
if(g_bTestInformationen){
var informationen;var info;var sTmp;var info_neu_id;
describe("shj.signUp.seminar.Informationen", function() {
    it("werden vom Server geladen", function(done) {
      informationen = new shj.signUp.seminar.Informationen(g_SEMINAR_ID, function(data){
          console.log('...es gibt ' + data.informationen.length + ' Informationen');
          console.log(informationen);
          expect(data.informationen).to.have.length.above(1);
          info=informationen.informationen[0];
          done();
      });
    });
    it("sind edierbar (fail ist unkritisch, liegt möglicherweise an Berechtigungen)", function(done) {
        sTmp=info.inhalt;
        info.inhalt='Testverfahren läuft ...';
        info.update(function(data){
            expect(data.information.inhalt).to.equal('Testverfahren läuft ...');
            done();
        });
    });
    it("sind edierbar (rückgängig; fail ist unkritisch, liegt möglicherweise an Berechtigungen)", function(done) {
        sTmp=info.inhalt;
        info.inhalt=sTmp;
        info.update(function(data){
            expect(data.information.inhalt).to.equal(sTmp);
            done();
        });
    });
    it("können ge-postet werden (add)", function(done) {
        info=new shj.signUp.seminar.Information(g_SEMINAR_ID);
        info.inhalt='Hallo, das ist eine neue Nachricht!';
        info.add(function(data){
            expect(data.information.inhalt).to.equal('Hallo, das ist eine neue Nachricht!');
            console.log('Neue Nachricht hat die Id: ' + data.information.id);
            info_neu_id=data.information.id;
            done();
        });
    });
    it("können mit Kommentar versehen werden", function(done) {
        info.id=info_neu_id;
        info.kommentiere('Hier ist ein Kommentar!',function(data){
            expect(data.success).to.equal('true');
            done();
        });
    });
    it("können gelöscht werden (drop)", function(done) {
        info.id=info_neu_id;
        info.force='all'; // Lösche mit Kommentaren!
        info.drop(function(data){
            expect(data.success).to.equal('true');
            done();
        });
    });
});
}
/**
 * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
 * Leistungen
 * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
 * 
 * @type @exp;chai@pro;expect
 */
var leistungen;var leistung;var leistung2;
describe("shj.signUp.seminar.Leistungen", function() {
    it("werden vom Server geladen", function(done) {
      leistungen = new shj.signUp.seminar.Leistungen(g_SEMINAR_ID, function(data){
          console.log('...es gibt ' + data.leistungen.length + ' Leistungen');
          expect(data.leistungen).to.have.length.above(2);
          done();
      });
    });
    it("haben die Eigenschaften name, name_en, cp, id", function() {
      leistung=leistungen.get(g_lLEISTUNG_BSP);
        console.log(leistung);
        expect(leistung).to.have.property('name');
        expect(leistung).to.have.property('name_en');
        expect(leistung).to.have.property('cp');
        expect(leistung).to.have.property('id');
      });
    it("sind edierbar", function(done) {
        sTmp=leistung.code;
        leistung.code='SHJ-01';
        leistung.update(function(data){
            expect(data.leistung.code).to.equal('SHJ-01');
            done();
        });
      });
    it("sind edierbar (rückgängig)", function(done) {
        leistung.code=sTmp;
        leistung.update(function(data){
            expect(data.leistung.code).to.equal(sTmp);
            done();
        });
      });
    it("kann man hinzufügen", function(done) {
        leistung2=new shj.signUp.seminar.Leistung(g_SEMINAR_ID);
        leistung2.name='Neue Leistung';
        leistung2.name_en='New Credit';
        leistung2.cp=3.5;
        leistung2.code='M1001';
        
        // Todo: test 'add'; implement drop
        // Frage: macht man das über die Collection?
        //  
        leistung2.add(function(data){
            expect(data.leistung.id).to.be.above(3);
            console.log(leistung);
            leistung2.id=data.leistung.id;
            done();
        });
      });
    it("kann man löschen", function(done) {
        leistung2.drop(function(data){
            expect(data.success).to.equal("true");
            done();
        });
      });
});

/**
 * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
 * Dozenten
 * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
 * 
 * @type @exp;chai@pro;expect
 */
var dozenten;var dozent;
describe("shj.signUp.seminar.Dozenten", function() {
    it("werden vom Server geladen", function(done) {
      dozenten = new shj.signUp.seminar.Dozenten(g_SEMINAR_ID, function(data){
          console.log('...es gibt ' + data.dozenten.length + ' Dozenten');
          expect(data.dozenten).to.have.length.above(2);
          expect(data.dozenten[1]).to.have.property('name');
          expect(data.dozenten[1]).to.have.property('vorname');
          expect(data.dozenten[1]).to.have.property('id');
          done();
      });
    });
    it("haben die Eigenschaften name, vorname, email, id", function() {
      dozent=dozenten.get(g_lDOZENT_BSP);
        console.log(dozent);
        expect(dozent).to.have.property('name');
        expect(dozent).to.have.property('vorname');
        expect(dozent).to.have.property('id');
        expect(dozent).to.have.property('email');
      });
  
   it("sind edierbar (Dozent " + g_lDOZENT_BSP + " ändert E-Mail)", function(done) {
      dozent.email='heiko.jakubzik@shj-online.de';
      dozent.update(function(data){
         expect(data.dozent.email).to.equal('heiko.jakubzik@shj-online.de');
         done(); 
      });
  });
   it("mache Edieren rückgängig", function(done) {
        expect(dozent.email).to.equal('heiko.jakubzik@shj-online.de');
        dozent.email='heiko.jakubzik@as.uni-heidelberg.de';
        dozent.update(function(data){
            expect(data.dozent.email).to.equal('heiko.jakubzik@as.uni-heidelberg.de');
            done();
        });
    });
});

/**
 * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
 * Dozenten: Sprechstunden
 * - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - 
 * 
 * @type @exp;chai@pro;expect
 */
describe("Seminar.Dozenten.Termine", function() {
  describe("Termine vom Server laden ...", function() {

    it("Termine/Sprechstunden des Dozenten kann hinzugefügt (=angeboten) werden.", function(done) {
      var t1 = {};
      t1.dozent_id=26;
      t1.datum = '2026-6-1';
      t1.start_0 = '22:00';
      t1.stop_0 = '23:00';
      t1.stornolink = 'http://www.storno.de';
      t1 = new shj.signUp.seminar.DozentTermin (g_SEMINAR_ID, t1);
      
      t1.add(function fSuccess(){done();});
     
    });
    
    it("Termine/Sprechstunden des Dozenten sind abrufbarund die haben die gefragten Eigenschaften (name, vorname, id).", function(done) {
      termine = new shj.signUp.seminar.DozentTermine(g_SEMINAR_ID, function(data){
          expect(data.dozentTermine).to.have.length.above(0);
          expect(data.dozentTermine[0].intervalle[0].sprechstunde).to.have.property("start");
          expect(data.dozentTermine[0].intervalle[0].sprechstunde).to.have.property("ort");
          expect(data.dozentTermine[0].intervalle[0].sprechstunde).to.have.property("stop");
          expect(data.dozentTermine[0].intervalle[0].sprechstunde).to.have.property("stornolink");
          expect(data.dozentTermine[0]).to.have.property("datum");
          var iCount = 0; var bDatumstreffer = false;
          for(var ii = 0, ij = data.dozentTermine.length; ii < ij; ii++){
              bDatumstreffer = (data.dozentTermine[ii].datum == '01.06.2026');
              
              if(bDatumstreffer){
                for(var aa = 0, ab = data.dozentTermine[ii].intervalle.length; aa < ab; aa++){
                    if(data.dozentTermine[ii].intervalle[aa].sprechstunde.start == '22:00') iCount++;
                }
              }
          }
          
          expect(iCount).to.equal(1);
          done();
      });
    });
    
    it("Termine/Sprechstunden des Dozenten kann gelöscht werden.", function(done) {
      var t1 = {};
      t1.dozent_id=26;
      t1.datum = '1.6.2026';
      t1 = new shj.signUp.seminar.DozentTermin (g_SEMINAR_ID, t1);
      
      t1.drop(function fSuccess(){done();});
     
    });
  });
  });

/**
 * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
 * Fach
 * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
 * 
 * @type @exp;chai@pro;expect
 */
var studiengaenge;
describe("shj.signUp.seminar.Fach", function() {
    it('Studiengänge des Seminare sind abrufbar (mindestens 3)', function(done) {
      studiengaenge = new shj.signUp.seminar.Faecher(g_SEMINAR_ID, function(data){
          console.log('...es gibt ' + data.faecher.length + ' konfigurierte Studiengänge');
          expect(data.faecher).to.have.length.above(2);
          done();
      });
  });
  var modul;
    it('Studiengang ' + g_lModularerStudiengang + ' hat mehr als 2 Module', function() {
        modul=studiengaenge.get(g_lModularerStudiengang).module[0].modul;
        console.log('...es gibt ' + studiengaenge.get(g_lModularerStudiengang).module.length + ' Module im Studiengang ' + g_lModularerStudiengang);
        expect(studiengaenge.get(g_lModularerStudiengang).module).to.have.length.above(2);
    });
    it('Modul 0 hat mehr als 2 Leistungen', function() {
        console.log(modul);
        console.log('...es gibt ' + modul.leistungen.length + ' Leistungen im Modul ' + modul.name);
        expect(modul.leistungen).to.have.length.above(2);
        console.log(modul.leistungen[0]);
    });  
  });


describe("shj.signUp.seminar.Kurs.Termin", function() {
    it('Kurstermine sind abrufbar', function(done) {
        var sending={};
        sending.signup_expected_backend_version=g_signup_expected_backend_version;
        sending.kurs_id=g_iKursID;                                                  

        $.ajax({
            data: sending,
            url: '../institut/json/seminar/kurs/termin/get.jsp',              // #Hack, @TODO: "as" darf da nicht stehen.
            success: function(data){
                // Reload
                console.log('OK');
                done();
            }
        });
  });
});

describe("shj.signUp.seminar.Kurs.Termin", function () {
    var oWeekly     = {};
    var oBiWeekly   = {};
    var oProps      = {"semesterStart": "2014-10-15", "semesterStop": "2015-2-15"};
    var iFreieRaume = -1;
    var sRaum       = '';
    
    it("Ein Einzeltermin lässt sich als wöchentlich darstellen", function () {
        oWeekly.sitzungen = shj.signUp.kurstermine.getTermineKomplett({"day": "mo", "start": "11:15", "stop": "12:45"}, oProps);
        expect(oWeekly.sitzungen.length).to.equal(17);
    });
    
    it("Ein Einzeltermin mit Feiertagen lässt sich als wöchentlich darstellen, wobei die Feiertage ausgenommen werden", function () {
        var oFeier={};
        var oPropsFeier={"semesterStart": "2014-10-15", "semesterStop": "2015-2-15", "feiertage":[]};
        var aFeier=[];
        aFeier.push("2014-12-29");
        aFeier.push("2015-1-5");
        oPropsFeier.feiertage=aFeier;
        oFeier.sitzungen = shj.signUp.kurstermine.getTermineKomplett(
                {"day": "mo", "start": "11:15", "stop": "12:45"}, 
                oPropsFeier);
        expect(oFeier.sitzungen.length).to.equal(15);
        expect(shj.signUp.kurstermine.guessPattern(oFeier.sitzungen).periode).to.equal('woche');
        expect(shj.signUp.kurstermine.guessPattern(oFeier.sitzungen).short).to.equal('Mo 11:15-12:45');
    });
    
    it("Wöchentlich mit Abweichung einer Uhrzeit gilt als 'Block'", function () {
        oWeekly.sitzungen[6].start = "10:15:00";
        expect(shj.signUp.kurstermine.guessPattern(oWeekly.sitzungen).periode).to.equal('block');
        oWeekly.sitzungen[6].start = "11:15:00";
    });
    
    it("Wöchentlich mit Abweichung eines Tages gilt als 'Block'", function () {
        var swap = oWeekly.sitzungen[6].datum;
        oWeekly.sitzungen[6].datum = "2014-12-02";
        expect(shj.signUp.kurstermine.guessPattern(oWeekly.sitzungen).periode).to.equal('block');
        oWeekly.sitzungen[6].datum = swap;
    });
    
    it("Wöchentlich wird richtig erkannt, Kurzform des Termins korrekt", function () {
        expect(shj.signUp.kurstermine.guessPattern(oWeekly.sitzungen).periode).to.equal('woche');
        expect(shj.signUp.kurstermine.guessPattern(oWeekly.sitzungen).short).to.equal('Mo 11:15-12:45');
    });

    it("Halbwöchentlich mit Abweichung einer Uhrzeit gilt als 'Block'", function () {
        oBiWeekly.sitzungen = shj.signUp.kurstermine.getTermineKomplett(
                {"day": "mo", "start": "11:15", "stop": "12:45"},
        oProps,
                {"day": "di", "start": "14:15", "stop": "15:45"});
        oBiWeekly.sitzungen[6].start = "10:15:00";
        expect(shj.signUp.kurstermine.guessPattern(oBiWeekly.sitzungen).periode).to.equal('block');
        oBiWeekly.sitzungen[6].start = "11:15:00";
    });
    
    it("Halbwöchentlich mit Abweichung eines Tages gilt als 'Block'", function () {
        oBiWeekly.sitzungen[6].datum = "2014-11-04";
        expect(shj.signUp.kurstermine.guessPattern(oBiWeekly.sitzungen).periode).to.equal('block');
        oBiWeekly.sitzungen[6].datum = "2014-11-03"
    });
    
    it("Halbwöchentlich wird richtig erkannt", function () {
        expect(shj.signUp.kurstermine.guessPattern(oBiWeekly.sitzungen).periode).to.equal('halbwoche');
    });
    
    it("Halbwöchentlich mit 1 unregistrierten Feiertag wird richtig halbwöchentlich erkannt", function () {
        var oDatum = oBiWeekly.sitzungen[6];
        oBiWeekly.sitzungen.splice(6, 1);
        expect(shj.signUp.kurstermine.guessPattern(oBiWeekly.sitzungen).periode).to.equal('halbwoche');
        oBiWeekly.sitzungen.splice(6, 0, oDatum);
    });

    it("Anfrage nach Räumen liefert mindestens drei freie Räume (sonntags...)", function (done) {
        oWeekly.sitzungen = shj.signUp.kurstermine.getTermineKomplett({"day": "so", "start": "11:15", "stop": "12:45"}, oProps);
        var sending={};
        sending.signup_expected_backend_version=g_signup_expected_backend_version;
        sending.termin_prio=2;

        for(var ij=0;ij<oWeekly.sitzungen.length; ij++){
            sending['datum_' + ij] = oWeekly.sitzungen[ij].datum;
            sending['start_' + ij] = '' + oWeekly.sitzungen[ij].start;
            sending['stop_' + ij] = '' + oWeekly.sitzungen[ij].stop;
        }

        $.ajax({
            data: sending,
            url: '../institut/json/seminar/raum/get.jsp',
            success: function(data){
                expect(data.raeume).to.have.length.above(2);
                sRaum=data.raeume[0];
                iFreieRaume=data.raeume.length;
                done();
            }
        });
    });

    it("Kann einen der Termine (sonntags) buchen.", function (done) {
        var sending={};
        sending.signup_expected_backend_version=g_signup_expected_backend_version;
        sending.termin_prio=1;
        sending.book_directly=true;
        sending.kurs_id=g_iKursID;
        
        for(var ij=0;ij<2; ij++){
            sending['datum_' + ij] = oWeekly.sitzungen[ij].datum;
            sending['start_' + ij] = '' + oWeekly.sitzungen[ij].start;
            sending['stop_' + ij] = '' + oWeekly.sitzungen[ij].stop;
            sending['raum_' + ij] = sRaum;
        }

        $.ajax({
            data: sending,
            url: '../institut/json/seminar/kurs/termin/add.jsp',
            success: function(data){
                done();
            }
        });
    });
    it("Anfrage nach Räumen liefert nun einen Raum weniger als zuvor...", function (done) {
        var sending={};
        sending.signup_expected_backend_version=g_signup_expected_backend_version;

        for(var ij=0;ij<oWeekly.sitzungen.length; ij++){
            sending['datum_' + ij] = oWeekly.sitzungen[ij].datum;
            sending['start_' + ij] = '' + oWeekly.sitzungen[ij].start;
            sending['stop_' + ij] = '' + oWeekly.sitzungen[ij].stop;
        }

        $.ajax({
            data: sending,
            url: '../institut/json/seminar/raum/get.jsp',
            success: function(data){
                console.log(data.raeume.length + ' < ' + iFreieRaume);
                expect(data.raeume.length).to.equal(parseInt(iFreieRaume-1));
                done();
            }
        });
    });
    it("Kann Raumbuchung löschen (1)...", function (done) {
        var sending={};
        sending.signup_expected_backend_version=g_signup_expected_backend_version;
        sending.termin_prio=1;
        sending.kurs_id=g_iKursID;

        for(var ij=0;ij<2; ij++){
            sending['datum_' + ij] = oWeekly.sitzungen[ij].datum;
            sending['start_' + ij] = '' + oWeekly.sitzungen[ij].start;
            sending['stop_' + ij] = '' + oWeekly.sitzungen[ij].stop;
            sending['raum_' + ij] = sRaum;
        }

        $.ajax({
            data: sending,
            url: '../institut/json/seminar/kurs/termin/delete.jsp',
            success: function(data){
                expect(data.success).to.equal('true');
                done();
            }
        });
    });
    it("Anfrage nach Räumen liefert nun wieder die alte Anzahl an Räumen...", function (done) {
        var sending={};
        sending.signup_expected_backend_version=g_signup_expected_backend_version;

        for(var ij=0;ij<oWeekly.sitzungen.length; ij++){
            sending['datum_' + ij] = oWeekly.sitzungen[ij].datum;
            sending['start_' + ij] = '' + oWeekly.sitzungen[ij].start;
            sending['stop_' + ij] = '' + oWeekly.sitzungen[ij].stop;
        }

        $.ajax({
            data: sending,
            url: '../institut/json/seminar/raum/get.jsp',
            success: function(data){
                expect(data.raeume.length).to.equal(parseInt(iFreieRaume));
                done();
            }
        });
    });
});