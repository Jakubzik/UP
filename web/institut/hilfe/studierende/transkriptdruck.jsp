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
          <h2>Hilfe zum Druck von Leistungsübersichten</h2> 
        </div>
      </div>
      <div class="row"> 
        <div class="span12"> 
          <h2>Leistungsübersicht selbst ausdrucken</h2> 
           <p>Sie können ein englisches oder deutsches Transkript selbst abrufen und als 
               PDF ausgeben (um es elektronisch zu versenden oder auszudrucken).</p>
           <p>Anstatt eines Siegels enthält dieses Transkript eine Verifikationsnummer, 
               mit der die Daten auf dem Transkript von Dritten (Stipendiengebern, anderen 
               Universitäten oder Ämtern) online überprüft werden können.</p>
        </div>
      </div>
       <br>
       <div class="row"> 
        <div class="span12"> 
          <h2>Erstellen deutscher und englischer Transkripte</h2> 
        </div>
      </div>
       <div class="row">
           <div class="span4">
               <img src="transkriptdruck-info.png" style="background-color:#ccc;padding:1px;border:thin solid gray;" alt="Erstellen der Leistungsübersicht" width="100%">
        </div> 
        <div class="span8"> 
            <p>Nach der Auswahl von &bdquo;Transkriptdruck&ldquo; (links) und 
                dem Klick auf &bdquo;Neues Transkript [Englisch]&ldquo; taucht 
                in der Tabelle &bdquo;Meine Transkripte&ldquo; ein entsprechender 
                Eintrag mit dem Ausstellungsdatum auf.</p>
            <p>Die Tabelle &bdquo;Meine Transkripte&ldquo; enthält in jeder Zeile Schaltflächen, 
                über die Sie Transkripte ansehen, als PDF ausgeben oder löschen können.</p>
            <p>Sie können höchstens sechs Transkripte gleichzeitig vorhalten.</p>
       </div> 
      </div>
       <br /><hr /><br />
      <div class="row"> 
        <div class="span12"> 
          <h2>Leistungsübersicht kontrollieren und drucken</h2> 
        </div>
      </div>
       <div class="row">
           <div class="span4">
               <img src="transkriptdruck-ansicht-pdf-delete.png" style="background-color:#ccc;padding:1px;border:thin solid gray;" alt="Liste Leistungsübersichten" width="100%">
        </div> 
        <div class="span8"> 
            <p>Wählen Sie zuerst die &bdquo;Ansicht&ldquo; und überprüfen Sie,</p>
            <ul><li>ob alle Leistungen aufgeführt sind,
                <li>ob alle Leistungspunkte stimmen,
                <li>ob alle Zuordnungen zu Modulen stimmen.
            </ul>
            <p>Wenn Sie einen Fehler finden, <b>lassen Sie diese zunächst von Ihrem Fachstudienberater korrigieren</b>.</p>
       </div> 
      </div>
       <br /><br />
 <div class="row"> 
        <div class="span12"> 
          <h2>Verifikation</h2> 
        </div>
      </div>
       <div class="row">
           <div class="span4">
               <img src="transkriptdruck-verifikation.png" style="background-color:#ccc;padding:1px;border:thin solid gray;" alt="Liste Leistungsübersichten" width="100%">
        </div> 
        <div class="span8"> 
                        <p>
                Sind alle Daten korrekt, geben Sie über die Schaltfläche &bdquo;PDF&ldquo; die Druckversion 
                des Transkripts aus. Dieses Dokument können Sie für den Postversand ausdrucken oder 
                es elektronisch versenden.
            </p>
            <p>Die von Ihnen selbst erstellte Leistungsübersicht enthält weder Siegel noch Unterschrift, 
                so dass die Richtigkeit der Angaben für Dritte auf andere Art überprüfbar gemacht 
                werden muss.
            </p>
            <p>Hierzu enthält Ihr Transkript eine Verifikationsnummer, mit deren Hilfe die Daten ohne 
                Login in SignUp angezeigt werden können (z.B. auf der Seite <a href="http://verifikation.uni-hd.de" 
                target="_blank">http://verifikation.uni-hd.de)</a>.
            </p>
            <p>Bei der Verifikation werden <b>exakt die Daten des von Ihnen erstellten Transkripts</b> 
                angezeigt, selbst wenn inzwischen neue Leistungen hinzugekommen sind oder Fehler 
                korrigiert wurden. (Deshalb ist es auch so wichtig, dass Sie das Transkript <b>aufmerksam 
                    kontrollieren</b>, bevor Sie es versenden).
            </p>
            <p>
                Die Daten können <b>bis zu sechs Monate</b> nach dem Erstellen verifiziert werden. 
                Nach sechs Monaten werden die Transkripte auch automatisch aus Ihrem SignUp 
                Konto entfernt.
            </p>
            <p>
                Wenn Sie ein Transkript selbst &bdquo;Löschen&ldquo; (um z.B. neue Transkripte 
                erzeugen zu können, oder weil es fehlerhaft war), ist es auch nicht mehr 
                verifizierbar. 
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