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
				<h3><a href="index-studierende.jsp">Studierende</a></h3>
				<p>Studierende können <ul><li>ihre Stammdaten aktuell halten (E-Mail Adressen, 
				Telefonnummern, Foto, Adresse, Nachname etc.), <li>sich zu Lehrveranstaltungen 
				anmelden, <li>ihre Scheine einsehen, <li>ihre Studienbilanz einsehen (und so 
				kontrollieren, welche Leistungen noch zum Abschluss fehlen) und <li>Transkripte beantragen oder selbst ausstellen</ul>.
				</p>
                                <p>Studierende können sich <a href="./index-studierende.jsp">hier anmelden</a></p>
			    </div>
			    <div class="span4">
			    <h3><a href="index.jsp">Lehrende</a></h3>
			    <p>Lehrende <ul><li>beschreiben hier ihre Kurse, <li>benoten und bescheinigen die Leistungen der 
			    Studierenden, <li>kündigen Sprechstunden an und halten ihre Stammdaten 
			    aktuell<li>können auf Wunsch eine kleine Homepage erstellen.</ul>
			    </p>
                            <p>Lehrende können sich <a href="./index.jsp">hier anmelden</a></p>
			    </div>
			    <div class="span4">
			    <h3><a href="index.jsp">Administratoren</a></h3>
			    <p>Administratoren haben Zugriff auf alle Daten und können Studiengänge 
			    konzipieren. Außerdem können sie als einzige Transkripte ausstellen und 
			    haben generell Zugriff auf alle Daten.</p>
                            <p>Administratoren können sich <a href="./index.jsp">hier anmelden</a></p>
			    </div>
			  </div><!-- /row -->  
		</div>
	  
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