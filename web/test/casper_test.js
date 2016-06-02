/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//var casper = require('casper').create();
var g_server = 'http://localhost:8084/SignUpXP/as/Faculty/index-studierende.jsp';
var g_server_ls = 'http://localhost:8084/SignUpXP/as/Faculty/ls-index.jsp';
var g_login={}; g_login.name='Jakubzik'; g_login.matrikelnummer='1488258'; g_login.pwd='1488258';

casper.test.begin('SignUp Faculty Edition -- Login für Studierende', 12, function suite(test) {

// Öffne Login-Formular
casper.start(g_server, function() {
    test.assertExists('form#fLogin', "Login-Formular geladen");
    this.fill('form#fLogin', {
        'txtStudentName':    g_login.name,
        'txtMatrikelnummer': g_login.matrikelnummer,
        'txtPwd':   g_login.pwd
    }, false);
});

// Melde mich als Studierender an
casper.then(function() {
    test.assertExists('button', "Login-Button gefunden");
    this.click('button');
});

// Die Anmeldung wird per Skript
// und Ajax ausgeführt und dauert
// einen Moment.
casper.wait(1000, function() {
   //
});

//casper.thenOpen(g_server_ls,function() {
//    this.capture('logged_in.jpg', {
//        top: 0,
//        left: 0,
//        width: 500,
//        height: 400
//    }, {format:'jpg', quality:75});
//});

// Stelle sicher, dass der 
// Navigationslink "Leistungen" vorhanden ist.

casper.then(function() {
    test.assertExists('a#navLeistungen', 'Link "Leistungen" gefunden');
    this.echo('     ...klicke Leistungen');
    this.click('#navLeistungen');
});

// Zeit lassen, um die Leistungen zu laden
// und anzuzeigen.
casper.wait(500, function() {
   //
});

// Leistungen anklicken
casper.then(function(){
    test.assertExists('#tLeistungen', 'Tabelle "Leistungen" gefunden');
    var iLeistungen=this.evaluate(function(){return document.querySelectorAll('#tLeistungen tbody>tr').length;});
    // this.capture('Leistungen.jpg');
    this.echo('     ...es sind ' + iLeistungen + ' Leistungen aufgelistet');
    test.assertResourceExists('bootstrap.min.css', 'Bootstrap CSS geladen.');
    test.assertResourceExists('tsort/style.css', 'Tsort CSS zum Sortieren der Tabellen geladen');
    test.assertResourceExists('jquery.min.js', 'jquery.min.js geladen');
    test.assertResourceExists('bootstrap.min.js', 'bootstrap.min.js geladen');
    test.assertResourceExists('tsort.min.js', 'tsort.min.js geladen');
});

// Antragsfunktion testen
casper.then(function(){
    test.assertExists('a#navAntrag', 'Link "Antrag" gefunden');
    this.echo('     ...klicke Antrag');
    this.click('#navAntrag');
});

casper.then(function(){
    test.assertExists('a#navTranskriptdruck', 'Link "Transkriptdruck" gefunden');
    this.click('#navTranskriptdruck');
 
});

// Zeit lassen, um die Leistungen zu laden
// und anzuzeigen.
casper.wait(500, function() {
   //
});

casper.then(function(){     
    test.assertExists('a#cmdPrintTranskript', 'Link "Neues Transkript" gefunden');
    this.click('#cmdPrintTranskript');
});

    casper.run(function() {
        test.done();
    });
});