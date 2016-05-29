<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en"> 
  <head> 
    <meta charset="utf-8"> 
    <title>UP -- Universitäre Prüfungsverwaltung</title> 
    <meta name="description" content=""> 
    <meta name="author" content="H. Jakubzik (shj-online)"> 
 
    <!-- Le HTML5 shim, for IE6-8 support of HTML elements --> 
    <!--[if lt IE 9]>
      <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]--> 
 
    <!-- Le styles --> 
    <style type="text/css"> 
      body {
        padding-top: 60px;
        padding-bottom: 40px;
      }
    </style> 
    <jsp:include page="fragments/css.jsp" />
 	
    <!-- Le fav and touch icons --> 
    <link rel="shortcut icon" href="img/signup.ico"> 
    <link rel="apple-touch-icon" href="images/apple-touch-icon.png"> 
    <link rel="apple-touch-icon" sizes="72x72" href="images/apple-touch-icon-72x72.png"> 
    <link rel="apple-touch-icon" sizes="114x114" href="images/apple-touch-icon-114x114.png"> 
  </head> 
 
  <body id="todo"> 
 
    <div class="navbar navbar-fixed-top"> 
      <div class="navbar-inner"> 
        <div class="container"> 
          <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse"> 
            <span class="icon-bar"></span> 
            <span class="icon-bar"></span> 
            <span class="icon-bar"></span> 
          </a> 
          <a class="brand" href="index.jsp">U<span style="color: red">:</span>P</a> 
          <div class="nav-collapse"> 
            <ul class="nav"> 
              <li><a href="kontakt.jsp">Kontakt</a></li> 
            </ul> 
          </div><!--/.nav-collapse --> 
        </div> 
      </div> 
    </div> 
  
    <div class="container"> 
 
      <!-- Main hero unit for a primary marketing message or call to action --> 
      <div class="hero-unit"> 
        <div class="row">
	        <div class="span4">
	        <h1>U<span style="color: red">:</span>P</h1>
	        <h2>Open Source Software</h2>
	        </div>
	        <div class="span3"> 
	        <img src="img/SignUp.gif" width="20%" />
	        </div>
	    </div>
      </div>
			  <div class="row"> 
			    <div class="span4">
				<h3>Leistungsdaten</h3>
				<p>Die Leistungen der Studierenden stehen im Mittelpunkt: nicht nur 
				die Noten und Leistpungspunkte spielen beim Speichern dieser 
				Leistungen eine Rolle. Sondern auch die Anmeldung zum Leistungserwerb 
				sowie möglichst viele weitere Details, die auf Transkripten 
				bei Bewerbungen Einblick in die tatsächlich erlängten Kenntnisse 
				gewähren. 
				</p>
			    </div>
			    <div class="span4">
			    <h3>Lehrveranstaltungen</h3>
			    <p>Vorlesungen, Seminar, Übungen, Praktika &mdash; jeder individuelle Kurs 
			    wird in SignUp mit Beschreibung (auf englisch und deutsch), Sitzungsterminen, 
			    Voraussetzungen, Leistungspunkten etc. angelegt. Studierende melden sich 
			    zum Scheinerwerb an, und bei der Benotung werden alle Detailinformationen 
			    als Leistung gesichert.
			    </p>
			    </div>
			    <div class="span4">
			    <h3>Studiengänge</h3>
			    <p>Besonders einfach gestaltet U<span style="color: red">:</span>P das Anlegen von Studiengängen: zum 
			    Beispiel definiert<br />
                            <code>
			    Einführungsmodul, LP=10<br />
			    - Einführung Literaturwissenschaft, LP=5<br />
                            - Einführung Sprachwissenschaft, LP=5<br /></code>
			    in SignUp ein Modul eines Studiengangs, das aus den beiden Leistungen 
			    "Einführung Literatur-" und "Einführung Sprachwissenschaft" besteht.<br />
			    Studiengänge bestehen aus einer Menge von Modulen.</p>
			    </div>
			  </div><!-- /row -->  
			  <hr>
 			  <div class="row"> 
			    <div class="span4">
				<h3>Technisches</h3>
				<p>U<span style="color: red">:</span>P arbeitet seit über zehn Jahren als Webanwendung, ist also 
				allein per Browser bedienbar. Die aktuelle Version setzt auf JSON 
				für den Datentransfer (was wenig Bandbreite braucht) und eine gute 
				Portion Javascript im Browser (was dafür sorgt, dass die Bedienung 
				sich flüssig anfühlt).<br />
				Ob mit Mac, Linux, Windows, Android oder ChromeOS &mdash; die 
				Benutzung ist immer intuitiv und schnell. 
				</p>
			    </div>
			    <div class="span4">
			    <h3>Zusatzmodule</h3>
			    <p>Über die Kernfunktionalität (die als Open Source kostenlos nutzbar ist) 
			    hinaus bietet U<span style="color: red">:</span>P <a href="mailto:info@shj-online.de">auf Anfrage</a> eine ganze Reihe von Zusatzmodulen, darunter
			    <ul>
			    	<li>Personaldatenverwaltung</li>
			    	<li>Evaluierungen und Umfragen</li>
			    	<li>Statistische und Grafische Auswertungen (z.B. Korrelationen von Leistungsnoten)</li>
			    	<li>Soziale Netzwerke (Studierende/Lehrende)</li>
			    	<li>Terminverwaltung</li>
			    </ul>
			    </p>
			    </div>
				<div class="span4"><img src="img/studierendenakte.gif" />
			    </div>
			  </div><!-- /row --><hr>  
 			  <div class="row">
			    <div class="span4">
				<h3>Z.B.: die Studierendenakte</h3>
				<p>Meist steht bei der Bedienung die "Studierendenakte" im Mittelpunkt (siehe Bild).
				Hier sind Leistungsdaten, Anmeldungen, Prüfungen, Stammdaten der Studierenden schnell 
				im Blick &mdash; und schnell erfasst oder korrigiert. 
				</p>
			    </div>
			    <div class="span4">
			    <h3>Bologna oder nicht</h3>
			    <p>U<span style="color: red">:</span>P administriert modulare ebenso wie nicht-modulare Studiengänge. 
			    Im Gegensatz zu Konkurrenzprodukten geht U<span style="color: red">:</span>P den umgekehrten Weg: die 
			    Modularisierung wird nicht "nachgerüstet". Sondern der herkömmliche 
			    Studiengang ist ein Sonderfall der Modularisierung: alle Leistungen 
			    entstammen einem einzigen Modul, das mit dem Studiengang zusammen fällt.
			    </p>
			    </div>
			  </div><!-- /row -->  			  
      <hr> 
 
      <footer> 
        <p>&copy; shj-online 2016</p> 
      </footer> 
 
    </div> <!-- /container --> 
 
    <!-- Le javascript
    ================================================== --> 
    <!-- Placed at the end of the document so the pages load faster -->
  </body> 
</html> 