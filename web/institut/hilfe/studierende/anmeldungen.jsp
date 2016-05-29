<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en"> 
  <head> 
    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
    <meta charset="utf-8"> 
    <title>SignUp Hilfe -- Anglistisches Seminar</title> 
    <meta name="description" content=""> 
    <meta name="author" content="H. Jakubzik (shj-online)"> 
 
    <!-- Le HTML5 shim, for IE6-8 support of HTML elements --> 
    <!--[if lt IE 9]>
      <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]--> 
 
    <!-- Le styles --> 
    <!--<link href="css/bootstrap.min.css" rel="stylesheet"> -->

    <jsp:include page="../../fragments/css.jsp"></jsp:include>
 	
    <!-- Le fav and touch icons --> 
    <link rel="shortcut icon" href="../../img/signup.ico"> 
    <link rel="apple-touch-icon" href="images/apple-touch-icon.png"> 
    <link rel="apple-touch-icon" sizes="72x72" href="images/apple-touch-icon-72x72.png"> 
    <link rel="apple-touch-icon" sizes="114x114" href="images/apple-touch-icon-114x114.png"> 

  </head> 
 
  <body id="todo"> 
 
<jsp:include page="./fragments/student-navbar-top.html" />
    <div class="container"> 
       <br /><br />
      <div class="row"> 
        <div class="span12 well"> 
          <h2>Hilfe zu: SignUp An- und Abmeldung zum Leistungserwerb</h2> 
        </div>
      </div>
      <div class="row"> 
        <div class="span4"> 
          <h2>Anmeldeverfahren</h2> 
        </div> 
        <div class="span8"> 
            <p>Es gibt in SignUp verschiedene Anmeldeverfahren (daher auch der Name "SignUp"): es 
                gibt die Kurswahl vor Semesterbeginn und die Anmeldung zum Leistungserwerb im 
                Semester
            </p>
            <p>
                Die Kurswahl dient der gleichmäßigen Auslastung von Parallelkursen: hier 
                müssen Sie Alternativen angeben und bekommen dann einen der Kurse 
                zugewiesen.
            </p>
            <p>
                Die Anmeldung zum Leistungserwerb ist Ihre Erklärung, den Leistungsnachweis 
                in einem bestimmten Kurs erbringen zu wollen: Sie erklären verbindlich 
                Ihre Teilnahme an einer Klausur oder erklären verbindlich, dass Sie 
                in einem Seminar eine Hausarbeit einreichen wollen etc.
            </p>
        </div>
      </div>
       <br /><hr><br />
       <div class="row">
           <div class="span4">
               <h2>Anmeldefrist</h2>
           </div>
           <div class="span8">
            <p>
                Bis relativ kurz vor Ende der Vorlesungszeit können Sie diese Anmeldungen 
                noch hinzufügen oder auch &mdash; folgenlos &mdash; wieder zurücknehmen. Bei 
                der Anmeldung spezifizieren Sie das Modul, in dem Sie die Leistung verbuchen 
                möchten (falls es für diese Leistung mehrere Optionen gibt).
            </p>
            <p>
                Nur wenn Sie für die Leistung angemeldet sind, kann Ihr Dozent/Ihre Dozentin
                Ihre Note in SignUp eintragen.
            </p>
        </div>
      </div>
       <br /><hr><br />
       <div class="row">
           <div class="span4">
               <h2>Anleitung</h2>
           </div>
           <div class="span8">
            <p>
                Um sich anzumelden, gehen Sie folgendermaßen vor:
            <ul>
                <li>Klicken Sie auf das "+",
                <li>Geben Sie Suchkriterien für den Kurs an, zu dem Sie sich anmelden wollen,
                <li>Klicken Sie auf den Kurs und
                <li>wählen Sie ggf. das relevante Modul aus. Fertig.
            </ul>
                Innerhalb der Anmeldefrist können Sie die Anmeldung jederzeit 
                folgenlos wieder löschen.
            </p>
            <p>
                Die Zuordnung zu einem Modul können Sie später vom 
                Fachstudienberater/Fachstudienberaterin ändern lassen. Wichtig 
                ist vor allem, dass Sie sich überhaupt anmelden.
            </p>
            <p>Den An- und Abmeldevorgang können Sie sich <a href="#" id="show_demo">hier demonstrieren lassen</a></p>
       </div> 
      </div>

                 <%--
                    -----------------------------------------------
                    Modalfenster Allg.: Wirklich löschen?
                    ----------------------------------------------- 
                --%>	            
                <div class="modal hide fade" id="modShj_SignUp_Lightbox">
                        <div class="modal-header">
                            <a class="close" data-dismiss="modal">×</a>
                            <h3 class="text-warning shj_SignUp_ConfirmDelete_Header"></h3>
                        </div>
                        <!-- <div class="modal-body shj_SignUp_ConfirmDelete_Body"> -->
                            <video width="100%" poster="" controls autoplay autobuffer>
                            <source src="./anmeldungen.ogv" type="video/ogg" type='video/ogg; codecs="theora, vorbis"'/>

                          </video>
                        <!-- </div> -->
                </div> 
      <footer> 
        <p>&copy; shj-online 2013</p> 
      </footer> 
 
    </div> <!-- /container --> 
 
    <!-- Le javascript
    ================================================== --> 
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
    <script src="../../js/2.1.1/bootstrap.min.js"></script>

    <script>
	$(document).ready(function() {
            $('#modShj_SignUp_Lightbox').modal({
                        backdrop: true,
                        keyboard: true,
                        show: false
                    }).css({
                        // make width 90% of screen
                       'width': function () { 
                           return ($(document).width() * .6) + 'px';  
                       },
                        // center model
                       'margin-left': function () { 
                           return -($(this).width() / 2); 
                       }
                });
		// Automatische Validierung
		// fürs Formular einschalten:
                $('#show_demo').on('click', function(){
                    $('#modShj_SignUp_Lightbox').modal('show');
                })

	});
</script>
  </body> 
</html> 