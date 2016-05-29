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
 	<jsp:include page="../../fragments/libs.jsp"></jsp:include>
 	
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
          <h2>Hilfe zu: SignUp Antrag</h2> 
        </div>
      </div>
      <div class="row"> 
        <div class="span12"> 
          <h2>Antrag auf Ausstellen eines Transkripts</h2> 
           <p>Hier können Sie beantragen, dass Ihnen eine (deutsche oder englische) Leistungsübersicht 
               (&bdquo;Transcript of Records&ldquo;) ausgestellt und im Glaskasten hinterlegt wird.</p>
           <p>Dazu müssen Sie zunächst selbst sicherstellen, dass Ihre Studienbilanz 
           keine Fehler enthält und alle Daten korrekt gespeichert sind.</p>
        </div>
      </div>
       <br><hr><br>
       <div class="row"> 
        <div class="span12"> 
          <h2>Kontrolle der Daten</h2> 
        </div>
      </div>
       <div class="row">
           <div class="span4">
               <img src="antrag_kontrolle.png" style="background-color:#ccc;padding:1px;border:thin solid gray;" alt="Antrag: Kontrolle der Daten" width="100%">
        </div> 
        <div class="span8"> 
            <p>Nach dem Klick auf &bdquo;Transkript ausstellen&ldquo; müssen Sie 
                in einem ersten Schritt sorgfältig die in Ihrer Leistungsübersicht 
                gespeicherten Daten kontrollieren.
            <ul><li>Sind alle Leistungen aufgeführt?
                <li>Stimmen die Leistungspunkte?
                <li>Stimmen die Zurodnungen zu Modulen?
            </ul>
            </p>
            <p>Wenn Sie einen Fehler finden, klicken Sie auf <a href="#" id="cmdAntragProblem" class="btn btn-danger btn-mini">Problem melden</a> 
                und folgen Sie den Anweisungen dort.</p>
       </div> 
      </div>
       <br><hr><br>
      <div class="row"> 
        <div class="span12"> 
          <h2>Antrag absenden</h2> 
        </div>
      </div>
       <div class="row">
           <div class="span4">
               <img src="antrag_absenden.png" style="background-color:#ccc;padding:1px;border:thin solid gray;" alt="Antrag absenden" width="100%">
        </div> 
        <div class="span8"> 
            <p>Sind Ihre Leistungdaten fehlerfrei, <ul>
                <li>aktivieren Sie &bdquo;alle Daten sind korrekt&ldquo;,
                <li>wählen Sie die gewünschte Sprache und
                <li>senden Sie den Antrag ab.
            </p>
            <p>Alle Anträge werden i.d.R. einmal wöchentlich in 
                einem Sekretariat bearbeitet.
            </p>
       </div> 
      </div>
       <br><hr><br>
      <div class="row"> 
        <div class="span12"> 
          <h2>Status der Bearbeitung des Antrags</h2> 
        </div>
      </div>
       <div class="row">
           <div class="span4">
               <img src="antrag_status.png" style="background-color:#ccc;padding:1px;border:thin solid gray;" alt="Abgeschlossener Antrag" width="100%">
        </div> 
        <div class="span8"> 
            <p>Unter &bdquo;Meine Anträge&ldquo; lässt sich (per Klick auf die Überschrift) jederzeit 
                der Status des Antrags ablesen: sobald das Transkript gedruckt und hinterlegt ist, 
                erscheint die Überschrift grün und in in den Details eine Meldung über das 
                Datum der Fertigstellung des Transkripts. Außerdem ist (für Rückfragen) der 
                Name des Ausstellers angegeben.
            </p>
            <p>Sobald der Antrag als &bdquo;abgeschlossen&ldquo; markiert ist, kann 
                das Transkript an der Bibliotheksaufsicht abgeholt werden.
            </p>
       </div> 
      </div>

        <%--
           -----------------------------------------------
           Modalfenster für Video und Bilder
           ----------------------------------------------- 
       --%>	            
       <div class="modal hide fade" tabindex='-1' id="modShj_SignUp_Lightbox">
               <div class="modal-header">
                   <a class="close" data-keyboard="true" data-dismiss="modal">×</a>
                   <h3 class="text-warning shj_SignUp_ConfirmDelete_Header"></h3>
               </div>
               <!-- <div class="modal-body shj_SignUp_ConfirmDelete_Body"> -->
                   <video width="100%" poster="" controls autoplay autobuffer>
                   <source src="#" type="video/ogg" type='video/ogg; codecs="theora, vorbis"'/>

                 </video>
               <!-- </div> -->
       </div> 
       <div class="modal hide fade" tabindex='-1' id="modShj_SignUp_LightboxPic">
               <div class="modal-header">
                   <a class="close" data-keyboard="true" data-dismiss="modal">×</a>
                   <h3 class="text-warning shj_SignUp_ConfirmDelete_Header"></h3>
               </div>
                   <img src="#" width="100%" />
       </div>
      <footer> 
        <p>&copy; shj-online 2013</p> 
      </footer> 
 
    </div> <!-- /container --> 
 
    <!-- Le javascript
    ================================================== --> 
    <!-- Placed at the end of the document so the pages load faster -->
	<script src="../../js/jquery.validate.min.js"></script>
	<script src="../../js/jquery.metadata.js"></script>
	<script src="../../js/jquery.form.js"></script>
    <script src="../../js/bootstrap.min.js"></script>

    <script>
	$(document).ready(function() {
                $('#modShj_SignUp_LightboxPic').modal({
                        backdrop: true,
                        keyboard: true,
                        show: false
                    }).css({
                        // make width 90% of screen
                       'width': function () { 
                           return ($(document).width() * .9) + 'px';  
                       },
                        // center model
                       'margin-left': function () { 
                           return -($(this).width() / 2); 
                       }
                });
		// Automatische Validierung
		// fürs Formular einschalten:
                $('img').on('click', function(){
                    $('#modShj_SignUp_LightboxPic img').attr('src', $(this).attr('src'));
                    $('#modShj_SignUp_LightboxPic').modal('show');
                })

	});
</script>
  </body> 
</html> 