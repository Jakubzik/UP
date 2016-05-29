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
    <!--<link href="css/bootstrap.min.css" rel="stylesheet"> -->
    <jsp:include page="fragments/css.jsp" />
    <style type="text/css"> 
      body {
        padding-top: 60px;
        padding-bottom: 40px;
      }
    </style> 
 	
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
          <a class="brand active" href="#">U<span style="color: red">:</span>P</a> 
          <div class="nav-collapse"> 
            <ul class="nav"> 
              <li><a href="http://www.shj-online.de">U<span style="color: red">:</span>P-Home</a></li> 
              <li><a href="about-up.jsp">About</a></li> 
              <li><a href="kontakt.jsp">Kontakt</a></li> 
            </ul> 
          </div><!--/.nav-collapse --> 
        </div> 
      </div> 
    </div> 
 
    <div class="container"> 
 
      <!-- Main hero unit for a primary marketing message or call to action --> 
    <div class="hero-unit"> 
        <h2>Zugang für Studierende</h2> 

        <div class="row"> 
            <div class="span9">
                <form class="well form-horizontal" name="fLogin" id="fLogin" action="json/login.jsp">
                    <fieldset>
                        <legend>Anmeldung</legend>
                        <div class="control-group"> 
                            <label class="control-label" for="txtStudentName">Nachname</label> 
                            <div class="controls"> 
                                <input class="input-xlarge required" id="txtStudentName" name="txtStudentName" type="text" placeholder="Nachname"></input> 
                            </div> 
                        </div>
                        <div class="control-group"> 
                            <label class="control-label" for="txtPwd">Matrikelnummer</label> 
                            <div class="controls"> 
                                <input class="input-xlarge required" id="txtMatrikelnummer" name="txtMatrikelnummer" type="password" placeholder="Matrikelnummer"> 
                            </div> 
                        </div>          		     
                        <div class="control-group"> 
                            <label class="control-label" for="txtPwd">Passwort d. UniID</label> 
                            <div class="controls"> 
                                <input class="input-xlarge required" id="txtPwd" name="txtPwd" type="password" placeholder="Passwort zur UniID"> 
                            </div> 
                            <div id="help-link" class='controls' style="display:none">Login klappt nicht? <a href='./hilfe/studierende/login.jsp'>Hier gibt's Hilfestellungen</a>.</div>
                        </div>
                        <div class="control-group"> 
                            <label class="control-label"></label> 
                            <div class="controls"> 
                                <button class="btn btn-primary btn-large" id="cmdSubmit" type="submit" data-loading-text="Prüfe...">Anmelden</button> 
                            </div> 
                        </div>
                    </fieldset>
                </form> 
            </div> 
        </div><!-- /row -->  
    </div> 
 
      <!-- Example row of columns --> 
      <div class="row"> 
        <div class="span4"> 
            <h2>U<span style="color:red">:</span>P</h2> 
           <p>Das ist die quelloffene, im Aufbau befindliche Version der Prüfungsverwaltungs-Software (früher "SignUp") an der Ruprecht-Karls-Universität Heidelberg.</p> 
          <p><a class="btn" href="about-up.jsp">Mehr Information &raquo;</a></p> 
        </div> 
        <div class="span4"> 
          <h2>Funktionsweise</h2> 
           <p>SignUp speichert Leistungsdaten (Scheine) von Studierenden, verwaltet Informationen zu Lehrenden und Lehrveranstaltungen und Prüfungsordnungen.</p> 
          <p><a class="btn" href="funktionsweise-up.jsp">Mehr Information &raquo;</a></p> 
       </div> 
        <div class="span4"> 
          <h2>Zugänge</h2> 
          <p>Es gibt drei Zugänge: für Studierende, für Lehrende und für (Prüfungs-)Sekretariate. Jeder Zugang soll dem Nutzerkreis eine möglichst vollständige und klare Sicht auf die für ihn interessanten Daten geben.</p> 
          <p><a class="btn" href="zugaenge-up.jsp">Mehr Information &raquo;</a></p> 
        </div> 
      </div> 
 
      <hr> 
 
      <footer> 
        <p>&copy; shj-online 2016</p> 
      </footer> 
 
    </div> <!-- /container --> 
 
    <!-- Le javascript
    ================================================== --> 
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
    <script src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.23/jquery-ui.min.js"></script>
	<script src="js/jquery.validate.min.js"></script>
	<script src="js/jquery.metadata.js"></script>
	<script src="js/jquery.form.js"></script>

    <script>
	$(document).ready(function() {
            var iLoginFails=0;
		// Automatische Validierung
		// fürs Formular einschalten:
		$("#fLogin").validate();

		$('#txtDozentName').focus();

		// Formular per Ajax absenden
		$('#fLogin').ajaxForm({
		    type:"POST",
                    cache: false,
		    contentType: "application/x-www-form-urlencoded;charset=utf8",
		    dataType: 'json', 
		    success:    function(data) {
		    	if(data.success=='true'){
					document.location="ls-index.jsp";
		    	}else{
			    	$('#fLogin').effect('shake', {}, 'fast');
                                iLoginFails++;
                                if(iLoginFails>=3) $('#help-link').show();
		    	} 
			} 
        }); 
	});
</script>
  </body> 
</html> 