<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en"> 
  <head> 
    <meta charset="utf-8"> 
    <title>UP &mdash; Universitäre Prüfungsverwaltung</title> 
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
				<h3>Was ist das?</h3>
				<p>U<span style="color: red">:</span>P ist eine schlanke Software zur Administrierung der 
				Lehrdaten eines Universitätsinstitutes. Im Gegensatz zu anderen 
                                Verwaltungsprogrammen steht bei SignUp die Bedienbarkeit und Intuitivität 
                                des Programms an erster Stelle. Es soll die Verwaltungsabläufe 
                                (Leistungen, Transkripte, Kurse, Raumpläne etc.) für 
                                die FachstudienberaterInnen und MitarbeiterInnen von Prüfungsämtern 
                                vereinfachen.
				</p>
			    </div>
			    <div class="span4">
			    <h3>Wie geht das?</h3>
			    <p>Seit 2001 wird U<span style="color: red">:</span>P (früher: "SignUp") in Heidelberg in unmittelbarer Zusammenarbeit 
			    mit Lehrenden und Institutsverwaltungen entwickelt. Regelmäßig wird 
			    das Bedienkonzept überarbeitet. Es kommen nur die besten Zutaten  
			    quelloffener Software zum Einsatz: Debian, Postgres, Tomcat, jquery, Bootstrap.
			    </p>
			    </div>
			    <div class="span4">
			    <h3>Wie bekomme ich das?</h3>
			    <p>Schauen Sie auf GitHub ;-)</p>
			    </div>
			  </div><!-- /row -->  
			  <hr>
 			  <div class="row"> 
			    <div class="span4">
				<h3>Funktionsweise</h3>
				<p>Im Kern besteht U<span style="color: red">:</span>P aus einer relationalen Datenbank mit ca. 54 Tabellen. Es wird 
				je nach Nutzer eine optimierte Ansicht der Daten berechnet: Studierende sehen 
				ihre Scheine, Tranksripte, Noten und Kurse. Lehrende können benoten und Kurse ankündigen. 
				Verwaltende können Studiengänge konfigurieren, Raumpläne erstellen und Anmeldefristen 
				setzen. 
				</p>
			    </div>
			    <div class="span4">
			    <h3>Alleinstellungsmerkmal</h3>
			    <p>Das Bedienen von U<span style="color: red">:</span>P soll intuitiv sein und Spaß machen! Dazu 
                                werden aktuelle Paradigmen der Bedienbarkeit und Bedienästhetik 
                                in den Vordergrund gestellt. 
			    </p>
			    </div>
			    <div class="span4"  style="text-align:center"><img src="img/simplicity.gif" width="45%"/>
			    </div>
			  </div><!-- /row --><hr>  
 			  <div class="row">
 			  	<div class="span4" style="text-align:center"><img src="img/penguin.gif" width="45%"/>
			    </div> 
			    <div class="span4">
				<h3>Geschäftsmodell: umsonst?</h3>
				<p>Gute Produkte gibt es nicht umsonst. Andererseits ist es schwierig, 
				kostenpflichtige Software auf dem Markt zu etablieren, die nicht bereits 
				Marktführer ist. U<span style="color: red">:</span>P geht den üblichen Weg: die Open Source Version ist 
				kostenlos und bietet den vollen Umfang aller Grundfunktionen. Kostenpflichtig 
				sind allein Zusatzmodule (Nachrichtendienste, soziale Netzwerkfunktionen, Terminkalender) 
                                und Dienstleistungen (Sonderanpassungen, Konfiguration von Studiengängen etc.)
				</p>
			    </div>
			    <div class="span4">
			    <h3>Get Involved!</h3>
			    <p>Um die Qualität und die Zukunfstfähigkeit zu erhalten braucht U<span style="color: red">:</span>P 
			    ständig neue Mitstreiter. <a href="mailto:info@shj-online.de">Registrieren Sie sich hier</a>, und arbeiten Sie 
			    als Tester, Übersetzer, Programmierer (Frontend, Datenbank, JSP/Java, Javascript) oder 
			    Dokumentierer mit!
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