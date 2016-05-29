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
          <h2>Hilfe zu: SignUp Daten</h2> 
        </div>
      </div>
      <div class="row"> 
        <div class="span4"> 
          <h2>Bild hochladen</h2> 
          <img src="bild_laden.png" alt="screenshot" width="40%" />
        </div> 
        <div class="span8"> 
           <p>Sie können in SignUp ein Foto hochladen, 
           das den Lehrenden und Studienberatern am Institut 
           angezeigt wird, damit diese sich besser erinnern, 
           mit wem sie es zu tun haben.</p> 
           <p>Klicken Sie dazu auf "Bild ändern" und laden 
               Sie ein Foto von Ihrer Festplatte auf 
               den Server.</p> 
           <p>Das Foto muss im JPG-Format vorliegen und 
               darf nicht mehr als 600 kB umfassen. 
           (Tipp: um die Dateigröße zu reduzieren, können 
           Sie gut die Bildgröße skalieren; eine Bildbreite 
           von 150px reicht für diesen Zweck aus)</p> 
           <p>Das Foto wird außerhalb von SignUp nicht 
               verwendet und nicht an andere Universitätseinrichtungen 
               oder an Dritte weitergegeben.</p>
           <p>Selbstverständlich können Sie Ihr Bild jederzeit 
               wieder löschen oder ändern.</p>
       </div> 
      </div>
             <hr> 
       <div class="row">
        <div class="span4"> 
          <h2>E-Mail Adresse etc.</h2> 
          <img src="daten.png" alt="screenshot" width="90%" />
        </div> 
        <div class="span8"> 
           <p>Sie können Ihre Kontaktdaten &mdash; also E-Mail Adresse und 
               Mobilfunknummer &mdash; selbst in SignUp verwalten, um 
               Lehrende, Studienberater und Seminarbibliothek auf dem Laufenden 
               zu halten. Wenn wir Sie kontaktieren möchten (weil Sie z.B. 
           Ihren Ausweis in der Bibliothek vergessen haben), wissen wir 
           so, wohin wir uns wenden müssen.</p> 
           <p>Wenn Sie die Option "E-Mail bei neuem Schein" aktivieren, 
               versucht SignUp, an die von Ihnen angegebene E-Mail Adresse 
               eine Nachricht zu senden, sobald eine neue Note 
               in SignUp für Sie gespeichert wurde.</p> 
           <p>Vergessen Sie bei Änderungen nicht, abschließend auf 
               "Speichern" zu klicken.</p>
       </div> 
      </div> 
            <hr> 
       <div class="row">
        <div class="span4"> 
          <h2>Allgemeine Informationen und Datenschutz</h2> 
        </div> 
        <div class="span8"> 
           <p>Ihre Stammdaten (Name, Fachsemester, Anschrift etc.) werden aus der Datenbank 
               des Studentensekretariats übernommen. (Wenn sich z.B. Ihr Name ändert, reicht 
               es aus, das dem Studentensekretariat mitzuteilen; die Namensänderung dort wirkt 
               sich mit einer ca. 10-tägigen Verzögerung automatisch auf SignUp aus).
            </p> 
           <p>Umgekehrt werden absolvierte Prüfungen (Orientierungs-, Zwischen- und Abschlussprüfungen) 
           ohne Angabe der Note an das Studentensekretariat elektronisch übermittelt.</p> 
           <p>Ihre persönlichen Daten und die Leistungsnoten (und Daten) werden ausschließlich in SignUp gespeichert 
               und grundsätzlich nicht übermittelt. Die IP-Adressen, MAC-Adressen, Browser-Footprints etc. 
           Ihrer Transaktionen in SignUp (insbesondere bei Anmeldungen zu Leistungen, bei der Kurswahl, 
           bei dem Beantragen von Transkripten) werden nicht gespeichert. Logdateien des Servers 
           die bei Fehlfunktionen Informationen zu IP-Adressen enthalten können werden nicht 
           weitergegeben.</p>
           <p>Zugang zu Ihren Leistungsdaten (Noten, Anmeldungen etc.) in SignUp haben nur 
               Beschäftigte des Landes Baden-Württemberg und Lehrbeauftragte; in der Regel 
               haben Lehrende nur Zugriff auf diejenigen Noten, die diese Ihnen gegeben haben.
           <p>Ihre Passworte oder UniID werden in SignUp nicht gespeichert; SignUp &quot;fragt&quot; den 
               Server des Rechenzentrums beim Login (über einen verschlüsselten Kanal), ob Ihre 
               Zugangsdaten korrekt sind oder nicht</p>
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
      <hr> 
 
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
                });

	});
</script>
  </body> 
</html> 